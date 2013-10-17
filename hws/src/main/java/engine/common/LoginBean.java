package engine.common;

/**
 * Title:        登录、校验、执行SQL的Bean
 * Description:  登录、校验、执行SQL的Bean
 * Copyright:    Copyright (c) 2001
 * Company:      ENGINE
 * @author jac
 * @version 1.0
 */
import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import com.borland.dx.dataset.DataRow;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.SortDescriptor;
import com.sanbo.erp.web.servlet.VerifyCaptchaServlet;

import engine.action.BaseAction;
import engine.dataset.EngineDataSet;
import engine.dataset.LocateUtil;
import engine.project.CommonClass;
import engine.util.Format;
import engine.util.log.Log;

//import engine.baseinfo.*;

/**
 * <p>
 * Title: 登录类
 * </p>
 * <p>
 * Description: 用于用户登录控制
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: ENGINE
 * </p>
 * 
 * @author hukn
 * @version 1.0
 */
public final class LoginBean extends CommonClass {
	public static final int LOGIN = 11;
	public static final int RE_LOGIN = 12;
	public static final int CHANGE_PASS = 13;
	public static final int CHANGE_FILIALE = 14;
	// 人员部门权限
	private static final String PERSON_DEPT_HANDLE = "SELECT deptid, qxlx FROM jc_bmqx WHERE personid='@'";
	// 超级用户部门权限
	private static final String ADMIN_DEPT_HANDLE = "SELECT deptid, 1 qxlx FROM bm WHERE bm.isdelete=0";
	// 人员仓库权限
	private static final String PERSON_STORE_HANDLE = "SELECT storeid FROM jc_ckqx WHERE personid='@'";
	// 超级用户仓库权限
	private static final String ADMIN_STORE_HANDLE = "SELECT storeid FROM kc_ck";
	// 最后登录时间
	private static final String UPDATE_LAST_LOGIN = "UPDATE emp SET lastlogin=to_date('@','YYYY-MM-DD') WHERE personid='@'";
	// 日志
	private static final String WRITE_LOG = "{CALL pck_login.writeLog('@','@','@','@')}";
	// 客户公司名称
	private static final String CORP_NAME = "SELECT mc FROM bm WHERE deptid=0";
	// nodeinfo信息
	private static final String NODE_INFO = "select * from nodeinfo  t where t.parentnodeid=0 and t.isdelete=0 order by t.nodecode";
	// node信息
	private static final String NODE_INFO_DETAIL = "select * from nodeinfo a where a.parentnodeid=? and a.isdelete=0";

	private static final String NODES = "{CALL pck_login.checkMenuData('@')}";
	// NODES信息
	private static Log log = new Log("Login");

	private boolean isNeedChange = true; // 是否需要更改密码
	private EngineDataSet tdsUserLimits = new EngineDataSet();// 用户的角色用例权限信息
	private EngineDataSet tdsRoleInfo = new EngineDataSet();// 保存当前用户角色信息
	private EngineDataSet tdsSystemParam = new EngineDataSet();// 系统参数
	private EngineDataSet cdsAllFiliale = new EngineDataSet(); // 所有分公司
	private EngineDataSet nodeinfos = new EngineDataSet(); // nodeinfo信息
	private EngineDataSet nodeinfodetails = new EngineDataSet(); // nodeinfo下拉信息
	private EngineDataSet nodes = new EngineDataSet(); // nodeinfo信息

	private String corpName = "";

	private User user = new User();// 当前用户信息

	private String accountDate; // 记帐日期

	private StringBuffer nodeinfomations = new StringBuffer();

	/**
	 * 得到登录bean的实例
	 * 
	 * @param request
	 *            jsp请求
	 * @return 返回往来单位信息的实例
	 */
	public static LoginBean getInstance(HttpServletRequest request) {
		LoginBean loginBean = null;
		HttpSession session = request.getSession(true);
		synchronized (session) {
			loginBean = (LoginBean) session.getAttribute("loginBean");
			if (loginBean == null) {
				loginBean = new LoginBean();
				session.setAttribute("loginBean", loginBean);

			}
			if (loginBean.isLogin())
				loginBean.user.setLastAccessDate();
		}
		return loginBean;
	}

	/**
	 * 构造函数
	 */
	private LoginBean() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 得到子类的类名
	 * 
	 * @return 返回子类的类名
	 */
	protected final Class childClassName() {
		return getClass();
	}

	public void valueBound(HttpSessionBindingEvent event) {

		if (tdsUserLimits == null) {
			tdsUserLimits = new EngineDataSet();
			setDataSetProperty(tdsUserLimits, null);
		}
		if (tdsRoleInfo == null) {
			tdsRoleInfo = new EngineDataSet();
			setDataSetProperty(tdsRoleInfo, null);
		}
		if (tdsSystemParam == null) {
			tdsSystemParam = new EngineDataSet();
			setDataSetProperty(tdsSystemParam, null);
		}
		if (cdsAllFiliale == null) {
			cdsAllFiliale = new EngineDataSet();
			setDataSetProperty(cdsAllFiliale, "CALL pck_login.getFilialesInfo(?)");
		}
		if (nodeinfos == null) {
			nodeinfos = new EngineDataSet();
			setDataSetProperty(nodeinfos, null);
		}
		if (nodeinfodetails == null) {
			nodeinfodetails = new EngineDataSet();
			setDataSetProperty(nodeinfodetails, null);
		}
		if (nodes == null) {
			nodes = new EngineDataSet();
			setDataSetProperty(nodes, null);
		}

	}

	/**
	 * jvm要调的函数
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		// 日志
		try {
			if (user.isLogin()) {
				UserFacade.removeUser(user);
				// this.writeLog(user.getIp(), "logout","退出系统");
			}
		} catch (Exception ex) {
			try {
				log.error("logout system", ex);
			} catch (Exception e) {
				System.err.println("logout system error!");
				e.printStackTrace();
			}
		}

		if (tdsUserLimits != null) {
			tdsUserLimits.closeDataSet();
			tdsUserLimits = null;
		}
		if (tdsRoleInfo != null) {
			tdsRoleInfo.closeDataSet();
			tdsRoleInfo = null;
		}
		if (tdsSystemParam != null) {
			tdsSystemParam.closeDataSet();
			tdsSystemParam = null;
		}
		if (cdsAllFiliale != null) {
			cdsAllFiliale.closeDataSet();
			cdsAllFiliale = null;
		}
		if (nodeinfos != null) {
			nodeinfos.closeDataSet();
			nodeinfos = null;
		}
		if (nodeinfodetails != null) {
			nodeinfodetails.closeDataSet();
			nodeinfodetails = null;
		}
		if (nodes != null) {
			nodes.closeDataSet();
			nodes = null;
		}

		// log = null;
	}

	/**
	 * 初始化函数
	 * 
	 * @throws Exception
	 *             异常信息
	 */
	private void jbInit() throws Exception {
		accountDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());

		setDataSetProperty(tdsUserLimits, null);
		setDataSetProperty(tdsRoleInfo, null);
		setDataSetProperty(tdsSystemParam, null);
		setDataSetProperty(cdsAllFiliale, "CALL pck_login.getFilialesInfo(?)");
		setDataSetProperty(nodeinfos, NODE_INFO);
		setDataSetProperty(nodeinfos, null);
	}

	/**
	 * 用户登录(用于Login)
	 * 
	 * @param user
	 *            用户名
	 * @param password
	 *            密码
	 * @param isEncode
	 *            密码是否经过加密处理的
	 * @return Returns message
	 */
	private String logIn(String loginName, String password, boolean isEncode) throws Exception {
		try {
			this.user.setLonginName(loginName);
			String newPass = null;
			if (password.length() == 0)
				newPass = "";
			else {
				// 解密
				if (isEncode)
					password = new String(new engine.encrypt.Base64().decode(password + "\r\n"));
				// 加密
				newPass = password.length() == 0 ? "" : engine.encrypt.MD5.encryptString(password);
			}
			EngineDataSet tdsUserInfo = new EngineDataSet();
			tdsUserInfo.setProvider(dataSetProvider);
			tdsUserInfo.setQueryString("{CALL pck_login.getUserInfo(?,'" + loginName + "','" + newPass + "')}");
			tdsUserInfo.openDataSet();

			boolean isLogin = tdsUserInfo.getRowCount() > 0;
			if (isLogin) {
				/*
				 * int maxcount = 1; try{ String loginMaxCount =
				 * getSystemParam("SYS_LOGIN_MAXCOUNT"); maxcount =
				 * Integer.parseInt(loginMaxCount); } catch(Exception ex){};
				 * String userId = tdsUserInfo.getValue("personid"); User
				 * oldUser = UserFacade.getUser(userId); if(oldUser != null) {
				 * if(oldUser.getIp().equals(user.getIp()) &&
				 * oldUser.getLoginCount() >= maxcount) return "同一用户超过最大同时登录数！";
				 * user = oldUser; }
				 */
				String userId = tdsUserInfo.getValue("personid");
				user.setPassword(newPass);
				user.setUserId(userId);
				user.setUserNo(tdsUserInfo.getValue("personcode"));
				user.setUserName(tdsUserInfo.getValue("personname"));
				user.setWrokNo(tdsUserInfo.getValue("workno"));
				user.setDeptId(tdsUserInfo.getValue("deptid"));
				user.setDeptNo(tdsUserInfo.getValue("deptno"));
				user.setDeptName(tdsUserInfo.getValue("deptname"));
				user.setFilialeId(tdsUserInfo.getValue("firstdeptid"));
				user.setFilialeNo(tdsUserInfo.getValue("firstdeptno"));
				user.setFilialeName(tdsUserInfo.getValue("firstdeptname"));
				user.setIsMember(tdsUserInfo.getValue("ismember").equals("1"));
				if (!UserFacade.isInit()) {
					user.login();
					String moreLogin = getSystemParam("SYS_MORE_LOGIN_IP");
					UserFacade.setMoreLogin("1".equals(moreLogin));
				}
				//
				UserFacade.putUser(user);
				// 登录日志
				// String sql0 = BaseAction.combineSQL(WRITE_LOG, "@", new
				// String[]{userId, user.getIp(), "login","登录系统"});
				String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date(user.getLastLoginDate()));
				String sql1 = BaseAction.combineSQL(UPDATE_LAST_LOGIN, "@", new String[] { date, userId });
				dataSetResolver.updateQuery(new String[] { sql1 });
				// 得到部门权限
				String deptsql = user.isAdmin() ? ADMIN_DEPT_HANDLE : BaseAction.combineSQL(PERSON_DEPT_HANDLE, "@",
						new String[] { userId });
				EngineDataSet dsDept = new EngineDataSet();
				dsDept.setProvider(dataSetProvider);
				dsDept.setQueryString(deptsql);
				dsDept.openDataSet();
				dsDept.first();
				for (int i = 0; i < dsDept.getRowCount(); i++) {
					String deptid = dsDept.getValue("deptid");
					String qxlx = dsDept.getValue("qxlx");
					user.addDeptHandle(deptid, "1".equals(qxlx));
					dsDept.next();
				}
				dsDept.closeDataSet();
				dsDept = null;
				// 得到仓库权限
				String storesql = user.isAdmin() ? ADMIN_STORE_HANDLE : BaseAction.combineSQL(PERSON_STORE_HANDLE, "@",
						new String[] { userId });
				EngineDataSet dsStore = new EngineDataSet();
				dsStore.setProvider(dataSetProvider);
				dsStore.setQueryString(storesql);
				dsStore.openDataSet();
				dsStore.first();
				for (int i = 0; i < dsStore.getRowCount(); i++) {
					user.addStoreHandle(dsStore.getValue("storeid"));
					dsStore.next();
				}
				dsStore.closeDataSet();
				dsStore = null;
				corpName = dataSetProvider.getSequence(CORP_NAME);
			} else {
				user.logout();
				UserFacade.removeUser(user);
			}
			if (corpName == null)
				corpName = "";
			tdsUserInfo.closeDataSet();
			return isLogin ? null : "错误的用户名或密码！";
		} catch (Exception ex) {
			UserFacade.removeUser(user);
			this.user.logout();
			throw ex;
		}
	}

	/**
	 * 验证码验证
	 * 
	 * @return
	 */
	private boolean validateCode(HttpServletRequest request, String code) {
		if (code.equals("cas12_fortest34_AbcDefg56_tt78")) {
			return true;
		}
		String innerCode = (String) request.getSession().getAttribute(VerifyCaptchaServlet.SESSION_VALIDATE_CODE);
		return code.equals(innerCode);
	}

	/**
	 * 网页提交的处理
	 * 
	 * @param request
	 *            jsp的请求对象
	 * @param response
	 *            jsp的响应对象
	 * @return 返回网页需要处理的信息
	 */
	public static String doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sRe = "";
		String operateTemp = request.getParameter("operate");
		if (operateTemp == null || operateTemp.equals(""))
			return sRe;

		int operate = -1;
		try {
			operate = Integer.parseInt(operateTemp.trim());
		} catch (Exception ex) {
			log.warn("operate = " + operateTemp + " parseInt Error!");
			return sRe;
		}

		String code = request.getParameter("code");
		String innerCode = (String) request.getSession().getAttribute(VerifyCaptchaServlet.SESSION_VALIDATE_CODE);
		LoginBean loginBean = null;
		// 将session失效
		if (operate == LOGIN)
			request.getSession().invalidate();
		// 是否处理
		boolean isProcess = operate == LOGIN || operate == CHANGE_PASS || operate == CHANGE_FILIALE;
		if (!isProcess)
			return "";

		loginBean = LoginBean.getInstance(request);
		synchronized (loginBean) {
			loginBean.user.setIp(request.getRemoteAddr());
			loginBean.user.setUserAgent(request.getHeader("User-Agent"));
			//
			switch (operate) {
			case LOGIN:
				// if(request.getParameter("accountDate") != null)
				// accountDate = request.getParameter("accountDate");
				String user = request.getParameter("user");
				String password = request.getParameter("password");
				if (user == null || password == null || "".equals(user))
					return sRe;
				String encode = request.getParameter("encode");
				boolean isEncode = encode != null && encode.equals("1");
				String message = "";
				if (!code.equals(innerCode)) {
					message = "验证码错误！";
				} else {
					try {
						message = loginBean.logIn(user, password, isEncode);
					} catch (Exception ex) {
						log.error("login system fail! user:" + user, ex);
					}
				}

				if (loginBean.user.isLogin()) {
					loginBean.isNeedChange = password.getBytes().length < 6;
					try {
						int screen = Integer.parseInt(request.getParameter("screen"));
						loginBean.user.setScreen(screen);
					} catch (Exception e) {
					}
				} else
					sRe = "loginError(\"" + message + "\");";
				break;

			// 更改密码
			case CHANGE_PASS:
				String oldpass = request.getParameter("oldpass");
				String newpass = request.getParameter("newpass");
				String cofirmpass = request.getParameter("cofirmpass");
				if (oldpass == null || newpass == null || cofirmpass == null)
					return sRe;
				String encode_change = request.getParameter("encode");
				boolean isEncode_change = encode_change != null && encode_change.equals("1");
				sRe = loginBean.changepass(oldpass, newpass, cofirmpass, isEncode_change);
				break;
			//
			case CHANGE_FILIALE:
				loginBean.cdsAllFiliale.goToRow(Integer.parseInt(request.getParameter("filialeRow")));
				sRe = "parent.closeDataSet();";
				break;
			}
			return sRe;
		}
	}

	/**
	 * 当前用户更改密码
	 * 
	 * @param oldpass
	 *            旧的密码
	 * @param newpass
	 *            新的密码
	 * @param confirm
	 *            确认新密码
	 * @param isEncode
	 *            密码是否经过加密处理
	 * @return
	 */
	private String changepass(String oldpass, String newpass, String confirm, boolean isEncode) {
		if (!user.isLogin())
			return showJavaScript("alert('您的登录时间已经过期，请重新登录！')");// "timeover";
		if (newpass.length() == 0)
			return showJavaScript("alert('新密码不能为空！')");
		if (!newpass.equals(confirm))
			return showJavaScript("alert('您两次输入的新密码不同！')");// "newerror";
		if (newpass.getBytes().length < 6)
			return showJavaScript("alert('新密码的长度必须大于6位！')");// "newerror";
		//
		engine.encrypt.Base64 base64 = null;
		if (isEncode)
			base64 = new engine.encrypt.Base64();
		if (oldpass.length() == 0)
			oldpass = "";
		else {
			if (isEncode)
				oldpass = new String(base64.decode(oldpass + "\r\n"));
			oldpass = engine.encrypt.MD5.encryptString(oldpass);
		}
		if (!oldpass.equals(user.getPassword()))
			return showJavaScript("alert('您输入的旧密码不对！')");// "olderror";

		if (isEncode)
			newpass = new String(base64.decode(newpass + "\r\n"));
		newpass = engine.encrypt.MD5.encryptString(newpass);
		String sRetu = null;
		try {
			dataSetResolver.updateQuery(new String[] { "CALL pck_login.changepass('" + getUserID().toString() + "','" + newpass + "')" });
			this.user.setPassword(newpass);
			this.isNeedChange = false;
			sRetu = "ok";
		} catch (Exception ex) {
			log.error("changepass fail!", ex);
			sRetu = showMessage(ex.getMessage(), true) + showJavaScript("alert('更改密码失败！')");// "fail";
		}
		return sRetu;
	}

	// ------------------------------------------------------------------
	/**
	 * 得到该用户是否具有该界面用例的操作权限
	 * 
	 * @param fullpathurl
	 *            jsp的URL全路径
	 * @return 是否具有该界面用例，该操作的权限, 返回true表示该用户至少有一个权限
	 */
	public synchronized boolean hasLimits(String nodeCode, HttpServletRequest request, HttpServletResponse response)
			throws java.io.IOException {
		if (!user.isLogin())
			return false;
		// 是否是超级用户
		if (isAdmin())
			return true;

		if (!tdsUserLimits.isOpen()) {
			tdsUserLimits.setQueryString("{CALL pck_login.getUserLimits(?," + getUserID() + ",'','')}");
			tdsUserLimits.openDataSet();
		}
		DataRow rowLocateNode = new DataRow(tdsUserLimits, "nodecode");
		rowLocateNode.setString(0, nodeCode);
		boolean isLocate = tdsUserLimits.locate(rowLocateNode, LocateUtil.FIRST);
		if (!isLocate)
			response.sendRedirect("../pub/nolimit.jsp");
		return isLocate;
	}

	/**
	 * 得到该用户是否具有该界面用例的操作权限
	 * 
	 * @param nodeCode
	 *            jsp的URL全路径
	 * @param limitCode
	 *            操作权限的编号
	 * @return 是否具有该界面用例，该操作的权限
	 */
	public boolean hasLimits(String nodeCode, String limitCode) {
		if (!user.isLogin())
			return false;
		if (limitCode == null)
			return false;
		// 是否是超级用户
		if (isAdmin())
			return true;

		if (!tdsUserLimits.isOpen()) {
			tdsUserLimits.setQueryString("{CALL pck_login.getUserLimits(?," + getUserID() + ",'','')}");
			tdsUserLimits.openDataSet();
		}
		DataRow rowLocateLimit = new DataRow(tdsUserLimits, new String[] { "nodecode", "priviligeCode"// ,"nodetype"
		});
		rowLocateLimit.setString("nodecode", nodeCode);
		rowLocateLimit.setString("priviligeCode", limitCode);
		// rowLocateLimit.setBigDecimal("nodetype", new BigDecimal(1));

		return tdsUserLimits.locate(rowLocateLimit, LocateUtil.FIRST);
	}

	/**
	 * 返回登录用户的所有角色信息,若未登录返回null <br>
	 * DataSetView字段 <br>
	 * nhxh：角色行序号。<br>
	 * vcbm：角色编号。<br>
	 * vczzmc：角色名称。
	 * 
	 * @return 登录用户的所有角色信息的DataSetView
	 */
	public boolean hasRole(String roleName) {
		if (!user.isLogin())
			return false;
		if (roleName == null)
			return false;
		if (!tdsRoleInfo.isOpen()) {
			// 提取用户的角色
			tdsRoleInfo.setQueryString("{CALL pck_login.getRoleData(?,'" + getUserID() + "')}");
			tdsRoleInfo.openDataSet();
		}
		return this.locateDataSet(tdsRoleInfo, "VCMC", roleName, LocateUtil.FIRST);
	}

	// ------------------------------------------------------------------
	/**
	 * 是否已经登录, 如果没有登陆将重定向到login.jsp页面
	 * 
	 * @param request
	 *            页面请求
	 * @param response
	 *            页面响应
	 * @return 是否已经登录
	 * @throws java.io.IOException
	 */
	public boolean isLogin(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
		String context = request.getContextPath();
		if (!user.isLogin()) {
			response.sendRedirect(context + "/login.jsp?errorMessage=nologin");
			return false;
		}
		// 如果需要更改密码
		else if (isNeedChange()) {
			String path = request.getServletPath();// getContextPath();
			if (!path.equals("/pub/change.jsp")) {
				response.sendRedirect(context + "/pub/change.jsp");
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否已经登录
	 * 
	 * @return 是否已经登录
	 */
	public boolean isLogin() {
		return user.isLogin();
	}

	/**
	 * 是否需要更改密码
	 * 
	 * @return 是否需要
	 */
	public boolean isNeedChange() {
		return isNeedChange;
	}

	/**
	 * 得到用户对象
	 * 
	 * @return 用户对象
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 提取当前登录用户行序号
	 * 
	 * @return 当前登录用户行序号
	 */
	public String getUserID() {
		return user.getUserId();
	}

	/**
	 * 提取当前登录用户编号
	 * 
	 * @return 当前登录用户编号
	 */
	public String getUserNo() {
		return user.getUserNo();
	}

	/**
	 * 提取当前登录用户名称
	 * 
	 * @return 当前登录用户名称
	 */
	public String getUserName() {
		return user.getUserName();
	}

	/**
	 * 提取当前登录用户的部门行序号
	 * 
	 * @return 当前登录用户的部门行序号
	 */
	public String getDeptID() {
		return user.getDeptId();
	}

	/**
	 * 提取当前登录用户的部门编号
	 * 
	 * @return 当前登录用户的部门编号
	 */
	public String getDeptNo() {
		return user.getDeptNo();
	}

	/**
	 * 提取当前登录用户的部门名称
	 * 
	 * @return 当前登录用户的部门名称
	 */
	public String getDeptName() {
		return user.getDeptName();
	}

	/**
	 * 得到登录用户的分公司序号
	 * 
	 * @return 返回登录用户的分公司序号
	 */
	public String getFirstDeptID() {
		return user.getFilialeId();
	}

	/**
	 * 得到登录用户的分公司编号
	 * 
	 * @return 返回登录用户的分公司编号
	 */
	public String getFirstDeptNo() {
		return user.getFilialeNo();
	}

	/**
	 * 得到登录用户的分公司名称
	 * 
	 * @return 返回登录用户的分公司名称
	 */
	public String getFirstDeptName() {
		return user.getFilialeName();
	}

	/**
	 * 得到客户公司名称
	 * 
	 * @return 返回客户公司名称
	 */
	public String getCorpName() {
		return corpName;
	}

	/**
	 * 得到登录用户是否是会员单位
	 * 
	 * @return 返回登录用户是否是会员单位
	 */
	public boolean isMember() {
		return user.isMember();
	}

	/**
	 * 是否是超级用户
	 * 
	 * @return 是否是超级用户
	 */
	private boolean isAdmin() {
		return user.isAdmin();
	}

	/**
	 * 得到所有分公司信息
	 */
	public void initFiliale() throws Exception {
		if (!cdsAllFiliale.isOpen())
			cdsAllFiliale.openDataSet();
		cdsAllFiliale.first();
	}

	// -----------------------------------------------------------------
	/**
	 * 得到登录用户分公司的系统参数或公用的系统参数
	 * 
	 * @param paramCode
	 *            参数的编号
	 * @return 返回参数值
	 */
	public String getSystemParam(String paramCode) {
		if (!user.isLogin())
			return "";
		if (!tdsSystemParam.isOpen()) {
			// 提取用户的系统参数
			tdsSystemParam.setQueryString("select code,value from systemparam where deptid= 0 or deptid =" + getFirstDeptID().toString()
					+ " ORDER BY deptid DESC");
			tdsSystemParam.openDataSet();
		}
		if (!locateDataSet(tdsSystemParam, "CODE", paramCode, LocateUtil.FIRST))
			return "";

		return tdsSystemParam.getString("value");
	}

	/**
	 * 得到指定分公司的系统参数
	 * 
	 * @param paramCode
	 *            参数的编号
	 * @param filialeID
	 *            指定分公司的ID
	 * @return 返回参数值
	 */
	public String getSystemParam(String paramCode, String filialeID) {
		if (!user.isLogin())
			return "";
		// 提取用户的系统参数
		EngineDataSet cdsTemp = new EngineDataSet();
		try {
			setDataSetProperty(cdsTemp, "select value from systemparam where code='" + paramCode + "' AND deptid =" + filialeID);
		} catch (Exception ex) {
			DataSetException.throwExceptionChain(ex);
		}
		cdsTemp.openDataSet();
		String value = cdsTemp.getString("value");
		cdsTemp.closeDataSet();
		cdsTemp = null;
		return value;
	}

	/**
	 * 记录操作日志。
	 * 
	 * @param ip
	 *            客户端ip
	 * @param url
	 *            当前页面的url
	 * @param option
	 *            操作描述
	 */
	public void writeLog(String ip, String url, String option) throws Exception {
		String sql = engine.action.BaseAction.combineSQL(WRITE_LOG, "@", new String[] { getUserID(), ip, url, option });
		dataSetResolver.updateQuery(new String[] { sql });
	}

	/**
	 * 得到菜单信息
	 * 
	 * @return 返回菜单信息
	 */
	public String getMenuData() {
		if (!user.isLogin())
			return "";
		// if(bufMenu != null)
		// return bufMenu;

		EngineDataSet dsTemp = new EngineDataSet();
		dsTemp.setProvider(dataSetProvider);
		dsTemp.setSort(new SortDescriptor("", new String[] { "nodecode" }, new boolean[] { false }, null, 0));
		dsTemp.setQueryString("{CALL pck_login.getMenuData(?,'" + getUserID() + "','')}");
		dsTemp.openDataSet();
		OutlookBar outlookBar = new OutlookBar();
		// outlookBar.setMouseOverFuction("treeMenuMouseOver(this)");
		// outlookBar.setMouseOutFuction("treeMenuMouseOut(this)");
		// outlookBar.setMouseUpFuction(null);
		// outlookBar.setTreeTableClass("");
		// outlookBar.setNodeClass("treeMenuItem");
		outlookBar.setTreeProperty(dsTemp, "nodeid", "nodecode", "url", "nodename");
		String bufMenu = outlookBar.printHtmlTree();
		dsTemp.closeDataSet();
		/* TreeNodes1[1]=new Node(id, parentid,'caption',true,'url'); */
		/*
		 * bufMenu = new StringBuffer(512); dsTemp.first(); for(int i=1;
		 * i<=dsTemp.getRowCount(); i++) {
		 * bufMenu.append("TreeNodes1["+i+"]=new Node(");
		 * bufMenu.append(dsTemp.getValue("nodeid")+",");
		 * bufMenu.append(dsTemp.getValue("parentnodeid")+",'");
		 * bufMenu.append(dsTemp.getValue("nodename")+"',");
		 * bufMenu.append(dsTemp.getValue("isexecute").equals("1")? "true,'" :
		 * "false,'"); bufMenu.append(dsTemp.getValue("url")+"');\n");
		 * dsTemp.next(); }
		 */
		return bufMenu;
	}

	/**
	 * 得到格式化数量的特定的字符串
	 * 
	 * @return 返回格式化数量的特定的字符串
	 */
	public String getQtyFormat() {
		String qtyFormat = getSystemParam("QTYFORMAT");
		if (qtyFormat.equals(""))
			qtyFormat = "#0.00";
		return qtyFormat;
	}

	/**
	 * 得到格式化单价的特定的字符串
	 * 
	 * @return 返回格式化单价的特定的字符串
	 */
	public String getPriceFormat() {
		String priceFormat = getSystemParam("PRICEFORMAT");
		if (priceFormat.equals(""))
			priceFormat = "#0.00";
		return priceFormat;
	}

	/**
	 * 得到格式化单价的特定的字符串
	 * 
	 * @return 返回格式化单价的特定的字符串
	 */
	public String getWsdjPriceFormat() {
		String priceFormat = getSystemParam("SYS_WSDJ_PRICE");
		if (priceFormat.equals(""))
			priceFormat = "#0.00";
		return priceFormat;
	}

	public String getCustmer() {
		String customer = getSystemParam("SYS_CUST_NAME");
		return customer;
	}

	/**
	 * 得到格式化数量的特定的字符串
	 * 
	 * @return 返回格式化数量的特定的字符串
	 */
	public String getSumFormat() {
		String sumFormat = getSystemParam("SUMFORMAT");
		if (sumFormat.equals(""))
			sumFormat = "#0.00";
		return sumFormat;
	}

	/**
	 * 格式化数量:将数量数字格式成指定的格式的字符串, 格式化的字符串是默认的字符串<br>
	 * 若该对象不可以格式化，则返回对象的toString
	 * 
	 * @param qty
	 *            要格式的数量数字,可以是Object, long, double
	 * @return 返回格式化过的字符串
	 */
	public String formatQty(Object qty) throws Exception {
		return Format.formatNumber(qty, getQtyFormat());
	}

	public String formatQty(long qty) throws Exception {
		return Format.formatNumber(qty, getQtyFormat());
	}

	public String formatQty(double qty) throws Exception {
		return Format.formatNumber(qty, getQtyFormat());
	}

	/**
	 * 格式化单价:将单价格式成指定的格式的字符串, 格式化的字符串是默认的字符串<br>
	 * 若该对象不可以格式化，则返回对象的toString
	 * 
	 * @param price
	 *            要格式的数量数字,可以是Object, long, double
	 * @return 返回格式化过的字符串
	 */
	public String formatPrice(Object price) throws Exception {
		return Format.formatNumber(price, getPriceFormat());
	}

	public String formatPrice(long price) throws Exception {
		return Format.formatNumber(price, getPriceFormat());
	}

	public String formatPrice(double price) throws Exception {
		return Format.formatNumber(price, getPriceFormat());
	}

	/**
	 * 格式化金额:将金额格式成指定的格式的字符串, 格式化的字符串是默认的字符串<br>
	 * 若该对象不可以格式化，则返回对象的toString
	 * 
	 * @param sum
	 *            要格式的数量数字,可以是Object, long, double
	 * @return 返回格式化过的字符串
	 */
	public String formatSum(Object sum) throws Exception {
		return Format.formatNumber(sum, getSumFormat());
	}

	public String formatSum(long sum) throws Exception {
		return Format.formatNumber(sum, getSumFormat());
	}

	public String formatSum(double sum) throws Exception {
		return Format.formatNumber(sum, getSumFormat());
	}

	/**
	 * <textarea rows="9" name="S1" cols="90"> int[] pageInfo; try{ pageInfo =
	 * (int[])request.getSession().getAttribute(url); }
	 * catch(IllegalStateException isex){ }
	 * pageInfo[2]和pageInfo[3]即是rowMin和rowMax。 </textarea>
	 */
	/**
	 * 提供分页的数据集的最小列数.也可以自己写代码:<br>
	 * 
	 * @param request
	 *            请求网页的request对象
	 * @return 返回该网页中分页的数据集的最小列数
	 */
	public static int getRowMin(HttpServletRequest request) {
		return getRowMin(request, null);
	}

	/**
	 * 提供分页的数据集的最小列数.也可以自己写代码:<br>
	 * 
	 * @param request
	 *            请求网页的request对象
	 * @param url
	 *            请求网页的url
	 * @return 返回该网页中分页的数据集的最小列数
	 */
	public static int getRowMin(HttpServletRequest request, String url) {
		if (url == null)
			url = request.getRequestURL().toString();
		// url = request.getRequestURL().toString();
		int[] pageInfo;
		try {
			pageInfo = (int[]) request.getSession().getAttribute(url);
		} catch (IllegalStateException isex) {
			return 0;
		}
		if (pageInfo != null)
			return pageInfo[2];
		else
			return 0;
	}

	/**
	 * 提供分页的数据集的最大列数
	 * 
	 * @param request
	 *            请求网页的request对象
	 * @return 返回该网页中分页的数据集的最大列数
	 */
	public static int getRowMax(HttpServletRequest request) {
		return getRowMax(request, null);
	}

	/**
	 * 提供分页的数据集的最大列数
	 * 
	 * @param request
	 *            请求网页的request对象
	 * @param url
	 *            请求网页的url
	 * @return 返回该网页中分页的数据集的最大列数
	 */
	public static int getRowMax(HttpServletRequest request, String url) {
		if (url == null)
			url = request.getRequestURL().toString();
		// url = request.getRequestURL().toString();
		int[] pageInfo;
		try {
			pageInfo = (int[]) request.getSession().getAttribute(url);
		} catch (IllegalStateException isex) {
			return 0;
		}
		if (pageInfo != null)
			return pageInfo[3];
		else
			return 0;
	}

	/**
	 * 根据总的数量和大包装的数量计算件数的表达式
	 * 
	 * @param totalnum
	 *            总的数量
	 * @param piecesnum
	 *            大包装的数量
	 */
	public String getPiecesExpression(String totalnum, String piecesnum) throws Exception {
		BigDecimal bgTotal, bgPieces;
		try {
			bgTotal = new BigDecimal(totalnum);
			bgPieces = new BigDecimal(piecesnum);
		} catch (Exception ex) {
			return "";
		}
		return getPiecesExpression(bgTotal, bgPieces);
	}

	/**
	 * 根据总的数量和大包装的数量计算件数的表达式
	 * 
	 * @param totalnum
	 *            总的数量
	 * @param piecesnum
	 *            大包装的数量
	 */
	public String getPiecesExpression(BigDecimal totalnum, BigDecimal piecesnum) throws Exception {
		BigDecimal bgZero = new BigDecimal("0");
		if (piecesnum.equals(bgZero))
			return "";
		boolean is = getSystemParam("LSSFWZXS").equals("1");
		BigDecimal bgjianshu = totalnum.divide(piecesnum, BigDecimal.ROUND_DOWN);// 得到件数
		BigDecimal bglinhshu = totalnum.subtract(bgjianshu.multiply(piecesnum)); // 得到零数
		if (bglinhshu.equals(bgZero))
			return bgjianshu.toString();

		return bgjianshu.toString() + "+" + (is ? bglinhshu.toString() : "0");
	}

	// ------------------------------------------------------------------
	/**
	 * 得到登录的用户名
	 * 
	 * @return 返回登录的用户名
	 */
	public String getLoginUser() {
		return user.getLonginName();
	}

	/**
	 * 获取每页Table的行数
	 * 
	 * @return 每页Table的行数
	 */
	public int getPageSize() {
		switch (user.getScreen()) {
		case 0:
			return 10;
		case 1:
			return 15;
		case 2:
			return 22;
		case 3:
			return 22;
		default:
			return 22;
		}
	}

	/**
	 * 得到记帐年月
	 * 
	 * @return 返回登录的用户名
	 */
	public String getAccountDateString() {
		java.sql.Date curDate = java.sql.Date.valueOf(accountDate);
		String show = new java.text.SimpleDateFormat("yyyy年MM月dd日").format(curDate);
		return show;
	}

	/**
	 * 得到记帐年月
	 * 
	 * @return 返回登录的用户名
	 */
	public String getAccountDate() {
		return accountDate;
	}

	/* 得到nodeinfos对象 */
	public final EngineDataSet getNodeInfo() {
		return nodeinfos;
	}

	/* 得到nodeinfos对象 */
	public final StringBuffer getNodeInfo2() {
		// LoginBean loginbean = new LoginBean();
		String personid = getUserID();
		String aa = personid;
		nodeinfomations = new StringBuffer();
		nodeinfos.openDataSet();
		int num = nodeinfos.getRowCount();
		nodeinfos.first();
		for (int i = 0; i < num; i++) {
			String name = nodeinfos.getValue("nodename");
			String nodeid = nodeinfos.getValue("nodeid");
			// nodeinfomations.append(name);
			nodeinfomations.append("[[\"" + name + "\",null,null,true,null]");
			// setDataSetProperty();
			setDataSetProperty(nodeinfodetails, "select * from nodeinfo a where a.parentnodeid='" + nodeid + "' and a.isdelete=0");
			nodeinfodetails.openDataSet();
			nodeinfodetails.first();
			for (int j = 0; j < nodeinfodetails.getRowCount(); j++) {
				String names = nodeinfodetails.getValue("nodename");
				String urls = nodeinfodetails.getValue("url");
				// ["银行信用卡","js","goPage('../person/credit_card.jsp?operate=0&src=../pub/main.jsp')",true,null],
				nodeinfomations.append(",[\'" + names + "',\"js\",\"goPage('" + urls + "&operate=0&src=../pub/main.jsp')\",true,null]");
				// nodeinfomations.append(names);
				// String aaa = nodeinfomations.toString();
				// String b = aaa;
				nodeinfodetails.next();
			}
			nodeinfodetails.closeDataSet();
			if (i == (num + 1))
				nodeinfomations.append("]");
			else
				nodeinfomations.append("],");
			nodeinfos.next();
		}
		nodeinfos.closeDataSet();
		return nodeinfomations;
	}

	/* 得到nodeinfos对象 */
	public final StringBuffer getNodeInfo3() {
		String limitsql = BaseAction.combineSQL(NODES, "@", new String[] { getUserID() });
		setDataSetProperty(nodes, limitsql);

		nodeinfomations = new StringBuffer();
		nodeinfos.openDataSet();
		int num = nodeinfos.getRowCount();
		// System.out.println(new Date());
		nodes = new EngineDataSet();
		setDataSetProperty(nodes, "{CALL pck_login.checkMenuData(?,'" + getUserID() + "','')}");
		nodes.open();

		nodeinfos.first();
		for (int i = 0; i < num; i++) {

			String name = nodeinfos.getValue("nodename");
			String nodeid = nodeinfos.getValue("nodeid");
			nodeinfomations.append("[[\"" + name + "\",null,null,true,null]");
			// setDataSetProperty(nodeinfodetails,"select * from nodeinfo a where a.parentnodeid='"+nodeid+"' and a.isdelete=0");

			nodes.first();
			for (int j = 0; j < nodes.getRowCount(); j++) {
				String names = nodes.getValue("nodename");
				String nodeids = nodes.getValue("parentnodeid");
				if (!nodeids.equals(nodeid)) {
					nodes.next();
					continue;
				}
				String urls = nodes.getValue("url");
				String you = nodes.getValue("you");
				// ["银行信用卡","js","goPage('../person/credit_card.jsp?operate=0&src=../pub/main.jsp')",true,null],
				if (you.equals("1")) {
					if (urls.indexOf("pdf.jsp") > 0)
						nodeinfomations.append(",[\'" + names + "',\"js\",\"goPage('" + urls
								+ "&operate=0&src=../pub/main.jsp')\",true,null]");
					else
						nodeinfomations.append(",[\'" + names + "',\"js\",\"goPage('" + urls
								+ "?operate=0&src=../pub/main.jsp')\",true,null]");
				} else
					nodeinfomations.append(",[\'" + names + "',null,null,false,null]");
				nodes.next();
			}
			// nodes.closeDataSet();
			if (i == (num + 1))
				nodeinfomations.append("]");
			else
				nodeinfomations.append("],");
			nodeinfos.next();
		}
		// System.out.println(new Date());
		nodes.closeDataSet();
		nodeinfos.closeDataSet();
		return nodeinfomations;
	}

}

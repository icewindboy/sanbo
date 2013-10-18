package engine.erp.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.common.OtherLoginBean;
import engine.dataset.EngineDataSet;
import engine.html.HtmlTableProducer;
import engine.util.log.Log;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;

/**
 * <p>
 * Title: 基础信息维护--产品选择列表
 * </p>
 * <p>
 * Description: 基础信息维护--产品选择列表<br>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 江海岛
 * @version 1.0
 */

public final class OtherProductSelect extends BaseAction implements Operate {
	public static final String PRODUCT_KIND_CHANGE = "10001";

	private static final String PRODUCT_NORMAL = "SELECT * FROM VW_KC_DM_EXIST a WHERE @ ORDER BY cpbm";// WHERE
																										// isdelete=0
																										// ";

	private static final String PRODUCT_RUIJIAO = "SELECT a.* FROM VW_KC_DM_EXIST a, kc_dmlb b " + "WHERE a.wzlbid=b.wzlbid AND @ ORDER BY b.bm, a.pm, a.gg";

	private EngineDataSet dsOneTable = new EngineDataSet();// 保存类别树状信息的数据集
	public HtmlTableProducer table = new HtmlTableProducer(dsOneTable, "kc_dm_select", "a");

	public String[] inputName = null; //
	public String[] fieldName = null; // 字段名称
	public String srcFrm = null; // 传递的原form的名称
	public boolean isMultiSelect = false;// 是否是多选的
	public String multiIdInput = null; // 多选的ID组合串

	private String personid = null;
	private String methodName = null; // 调用window.opener中的方法

	private String prodSQL = null;
	private String custName = null;

	/**
	 * 得到往来单位信息的实例
	 * 
	 * @param request
	 *            jsp请求
	 * @return 返回往来单位信息的实例
	 */
	public static OtherProductSelect getInstance(HttpServletRequest request) {
		OtherProductSelect productSelectBean = null;
		HttpSession session = request.getSession(true);
		synchronized (session) {
			productSelectBean = (OtherProductSelect) session.getAttribute("otherProductSelect");
			if (productSelectBean == null) {
				productSelectBean = new OtherProductSelect(request);
				session.setAttribute("otherProductSelect", productSelectBean);
			}
		}
		return productSelectBean;
	}

	/**
	 * 构造函数
	 */
	private OtherProductSelect(HttpServletRequest request) {
		custName = OtherLoginBean.getInstance(request).getSystemParam("SYS_CUST_NAME");
		prodSQL = "ruijiao".equals(custName) ? PRODUCT_RUIJIAO : PRODUCT_NORMAL;
		try {
			jbInit();
		} catch (Exception ex) {
			log.error("jbInit", ex);
		}
	}

	/**
	 * 初始化函数
	 * 
	 * @throws Exception
	 *             异常信息
	 */
	protected final void jbInit() throws java.lang.Exception {

		String SQL = BaseAction.combineSQL(prodSQL, "@", new String[] { "1=1" });
		setDataSetProperty(dsOneTable, SQL);
		// dsOneTable.setSort(new SortDescriptor("", new String[]{"cpbm"}, new
		// boolean[]{false}, null, 0));

		addObactioner(String.valueOf(INIT), new Init());
		addObactioner(String.valueOf(FIXED_SEARCH), new Search());
		addObactioner(String.valueOf(PROD_CHANGE), new CodeSearch());
		addObactioner(String.valueOf(PROD_NAME_CHANGE), new NameSearch());
		addObactioner(PRODUCT_KIND_CHANGE, new KindChange());
	}

	// ----Implementation of the BaseAction abstract class
	/**
	 * JSP调用的函数
	 * 
	 * @param request
	 *            网页的请求对象
	 * @param response
	 *            网页的响应对象
	 * @return 返回HTML或javascipt的语句
	 * @throws Exception
	 *             异常
	 */
	public String doService(HttpServletRequest request, HttpServletResponse response) {
		try {
			String opearate = request.getParameter(OPERATE_KEY);
			if (opearate != null && opearate.trim().length() > 0) {
				RunData data = notifyObactioners(opearate, request, response, null);
				if (data.hasMessage())
					return data.getMessage();
			}
			return "";
		} catch (Exception ex) {
			log.error("doService", ex);
			return showMessage(ex.getMessage(), true);
		}
	}

	/**
	 * Implement this engine.project.OperateCommon abstract method
	 * jvm要调的函数,类似于析构函数
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		if (dsOneTable != null) {
			dsOneTable.closeDataSet();
			dsOneTable = null;
		}
		super.deleteObservers();
		log = null;
	}

	/**
	 * 得到子类的类名
	 * 
	 * @return 返回子类的类名
	 */
	protected final Class childClassName() {
		return getClass();
	}

	/**
	 * 初始化表单数据及其SQL语句
	 * 
	 * @return 返回子类的类名
	 */
	private void init(RunData data) {
		personid = OtherLoginBean.getInstance(data.getRequest()).getUserID();
		table.getWhereInfo().clearWhereValues();
		// 得到关闭窗体前要调用的方法
		methodName = data.getParameter("method");
		// 剔除不需要出现的产品记录
		String method = data.getParameter("notin", "");
		String deptid = data.getParameter("deptid", "");
		String storeid = data.getParameter("storeid", "");
		String chxz = data.getParameter("chxz", "");
		prodSQL = "ruijiao".equals(custName) ? PRODUCT_RUIJIAO : PRODUCT_NORMAL;
		prodSQL = BaseAction.combineSQL(prodSQL, "@", new String[] { " 1=1 AND @ " });
		if (method.length() > 0) {
			HttpSession session = data.getRequest().getSession();
			String notin = (String) session.getAttribute(method);
			if (notin != null) {
				session.removeAttribute(method);
				prodSQL = BaseAction.combineSQL(prodSQL, "@", new String[] { " a.cpid NOT IN (" + notin + ") AND @ " });
			}
		}
		if (deptid.length() > 0)
			prodSQL = BaseAction.combineSQL(prodSQL, "@", new String[] { " @ AND (a.deptid is null OR a.deptid=" + deptid + ")" });
		if (storeid.length() > 0)
			prodSQL = BaseAction.combineSQL(prodSQL, "@", new String[] { " @ AND (a.storeid is null OR a.storeid=" + storeid + ")" });
		if (chxz.length() > 0)
			prodSQL = BaseAction.combineSQL(prodSQL, "@", new String[] { " @ AND a.chxz='" + chxz + "'" });

		srcFrm = data.getParameter("srcFrm");
		// 是否是多选
		String multi = data.getParameter("multi");
		isMultiSelect = multi != null && multi.equals("1");
		if (isMultiSelect)
			multiIdInput = data.getParameter("srcVar");
		else {
			inputName = data.getParameterValues("srcVar");
			fieldName = data.getParameterValues("fieldVar");
			/*
			 * String curID = request.getParameter("curID"); if(curID!= null) {
			 * curID = curID.trim(); try{ Integer.parseInt(curID); SQL =
			 * SQL==null ? (PRODUCT_SQL +" WHERE cpid="+ curID) :
			 * (" WHERE cpid="+ curID); }catch(Exception ex){} }
			 */
		}
	}

	// ------------------------------------------
	// 操作实现的类
	// ------------------------------------------
	/**
	 * 初始化操作的触发类
	 */
	final class Init implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			init(data);
			String SQL = BaseAction.combineSQL(prodSQL, "@", new String[] { "1=1" });
			getOneTable().setQueryString(SQL);
			getOneTable().setRowMax(null);
		}
	}

	/**
	 * 通过产品编码得到产品信息的触发类
	 */
	final class CodeSearch implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			init(data);
			String cpbm = data.getParameter("code", "");
			table.getWhereInfo().putWhereValue("cpbm", cpbm);
			String SQL = BaseAction.combineSQL(prodSQL, "@", new String[] { "a.cpbm LIKE '" + cpbm + "%'" });
			EngineDataSet ds = getOneTable();
			ds.setQueryString(SQL);
			if (ds.isOpen()) {
				ds.readyRefresh();
				ds.refresh();
			} else
				ds.openDataSet();
		}
	}

	/**
	 * 通过产品品名规格得到产品信息的触发类
	 */
	final class NameSearch implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			init(data);
			String name = data.getParameter("name", "");
			table.getWhereInfo().putWhereValue("product", name);
			String SQL = BaseAction.combineSQL(prodSQL, "@", new String[] { "a.product LIKE '%" + name + "%'" });
			EngineDataSet ds = getOneTable();
			ds.setQueryString(SQL);
			if (ds.isOpen()) {
				ds.readyRefresh();
				ds.refresh();
			} else
				ds.openDataSet();
		}
	}

	/**
	 * 产品类别变更的触发类
	 */
	final class KindChange implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			String wzlbid = data.getParameter("wzlbid", "");
			table.getWhereInfo().clearWhereValues();
			table.getWhereInfo().putWhereValue("a$wzlbid", wzlbid);
			String SQL = wzlbid.length() == 0 ? "1=1" : "a.wzlbid = " + wzlbid;
			SQL = BaseAction.combineSQL(prodSQL, "@", new String[] { SQL });
			getOneTable().setQueryString(SQL);
			getOneTable().setRowMax(null);
		}
	}

	/**
	 * 查询操作的触发类
	 */
	final class Search implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			table.getWhereInfo().setWhereValues(data.getRequest());
			String SQL = table.getWhereInfo().getWhereQuery();
			SQL = BaseAction.combineSQL(prodSQL, "@", new String[] { SQL.length() == 0 ? "1=1" : SQL });
			// SQL = PRODUCT_SQL +(SQL.equals("")? "" : "WHERE (" +SQL +")")
			// +" ORDER BY cpbm";
			getOneTable().setQueryString(SQL);
			getOneTable().setRowMax(null);
		}
	}

	/* 得到表对象 */
	public final EngineDataSet getOneTable() {
		return dsOneTable;
	}

	/**
	 * 得到需要调用window.opener中的方法的名称
	 * 
	 * @return 方法的名称
	 */
	public String getMethodName() {
		if (methodName != null && methodName.length() == 0)
			return null;
		return methodName;
	}

	/**
	 * 得到写日志的对象
	 * 
	 * @return 写日志的对象
	 */
	public Log getLog() {
		return log;
	}
}
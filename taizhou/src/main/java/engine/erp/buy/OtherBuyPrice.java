package engine.erp.buy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.common.OtherLoginBean;
import engine.common.OtherUser;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.RowMap;
import engine.dataset.SequenceDescriptor;
import engine.html.HtmlTableProducer;
import engine.project.LookUp;
import engine.project.LookupBeanFacade;
import engine.project.SysConstant;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;

public final class OtherBuyPrice extends BaseAction implements Operate {
	public static final String FIXED_SEARCH = "1009";
	public static final String WB_ONCHANGE = "1008";
	/**
	 * 提取报价资料信息的SQL语句
	 */
	private static final String BUYPRICE_SQL = // "SELECT * FROM cg_bj where fgsid=? ? ORDER BY cgbjID DESC";//
	"SELECT * FROM cg_bj, kc_dm WHERE cg_bj.cpid = kc_dm.cpid ? ORDER BY kc_dm.cpbm ";
	private static final String MASTER_STRUT_SQL = "SELECT * FROM cg_bj WHERE 1=1 AND DWTXID=? ";

	/**
	 * 保存报价资料信息的数据集
	 */
	private EngineDataSet dsBuyPrice = new EngineDataSet();

	private LookUp foreignBean = null;// 外币信息bean

	public HtmlTableProducer table = new HtmlTableProducer(dsBuyPrice, "cg_bj", "cg_bj");
	/**
	 * 用于定位数据集
	 */
	private EngineRow locateRow = null;

	/**
	 * 保存用户输入的信息
	 */
	private RowMap rowInfo = new RowMap();

	/**
	 * 是否在添加状态
	 */
	public boolean isAdd = true;

	/**
	 * 保存修改操作的行记录指针
	 */
	private long editrow = 0;

	/**
	 * 点击返回按钮的URL
	 */
	public String retuUrl = null;
	public String loginId = ""; // 登录员工的ID
	public String loginName = ""; // 登录员工的姓名
	private String fgsid = null; // 分公司ID
	/**
	 * 定义固定查询类
	 */
	// private QueryBasic fixedQuery = new QueryFixedItem();
	private boolean isInitQuery = false; // 是否已经初始化查询条件
	private String qtyFormat = null, priceFormat = null, sumFormat = null;

	private OtherUser user = null;
	private String cpid = null;

	/**
	 * 得到报价资料信息的实例
	 * 
	 * @param request
	 *            jsp请求
	 * @param isApproveStat
	 *            是否在审批状态
	 * @return 返回报价资料信息的实例
	 */
	public static OtherBuyPrice getInstance(HttpServletRequest request) {
		OtherBuyPrice buyPriceBean = null;
		HttpSession session = request.getSession(true);
		synchronized (session) {
			String beanName = "otherBuyPrice";
			buyPriceBean = (OtherBuyPrice) session.getAttribute(beanName);
			// 判断该session是否有该bean的实例
			if (buyPriceBean == null) {
				OtherLoginBean loginBean = OtherLoginBean.getInstance(request);
				buyPriceBean = new OtherBuyPrice();
				buyPriceBean.fgsid = loginBean.getFirstDeptID();
				buyPriceBean.loginId = loginBean.getUserID();
				buyPriceBean.loginName = loginBean.getUserName();
				buyPriceBean.priceFormat = loginBean.getPriceFormat();
				// 设置格式化的字段
				buyPriceBean.dsBuyPrice.setColumnFormat("bj", loginBean.getPriceFormat());
				buyPriceBean.dsBuyPrice.setColumnFormat("wbbj", loginBean.getPriceFormat());

				buyPriceBean.user = loginBean.getUser();

				session.setAttribute(beanName, buyPriceBean);
			}
		}
		return buyPriceBean;
	}

	/**
	 * 构造函数
	 */
	private OtherBuyPrice() {
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
	protected void jbInit() throws Exception {
		setDataSetProperty(dsBuyPrice, null);
		dsBuyPrice.setSequence(new SequenceDescriptor(new String[] { "cgbjid" }, new String[] { "s_cg_bj" }));
		dsBuyPrice.setTableName("cg_bj");
		// 添加操作的触发对象
		BuyPrice_Add_Edit add_edit = new BuyPrice_Add_Edit();
		addObactioner(String.valueOf(INIT), new BuyPrice_Init());
		addObactioner(String.valueOf(ADD), add_edit);
		addObactioner(String.valueOf(EDIT), add_edit);
		addObactioner(String.valueOf(POST), new BuyPrice_Post());
		addObactioner(String.valueOf(DEL), new BuyPrice_Delete());
		addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
		addObactioner(String.valueOf(WB_ONCHANGE), new Wb_Onchange());
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
			String operate = request.getParameter(OPERATE_KEY);
			if (operate != null && operate.trim().length() > 0) {
				RunData data = notifyObactioners(operate, request, response, null);
				if (data.hasMessage())
					return data.getMessage();
			}
			return "";
		} catch (Exception ex) {
			if (dsBuyPrice.isOpen() && dsBuyPrice.changesPending())
				dsBuyPrice.reset();
			log.error("doService", ex);
			return showMessage(ex.getMessage(), true);
		}
	}

	// ----Implementation of the BaseAction abstract class
	/**
	 * jvm要调的函数,类似于析构函数
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		if (dsBuyPrice != null) {
			dsBuyPrice.close();
			dsBuyPrice = null;
		}
		log = null;
		rowInfo = null;
		locateRow = null;
	}

	// ----Implementation of the BaseAction abstract class
	/**
	 * 得到子类的类名
	 * 
	 * @return 返回子类的类名
	 */
	protected Class childClassName() {
		return getClass();
	}

	/**
	 * 初始化行信息
	 * 
	 * @param isAdd
	 *            是否时添加
	 * @param isInit
	 *            是否从新初始化
	 * @throws java.lang.Exception
	 *             异常
	 */
	private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception {
		// 是否时添加操作
		if (isInit && rowInfo.size() > 0)
			rowInfo.clear();
		if (isAdd) {
			Calendar day = new GregorianCalendar();
			int year = day.get(Calendar.YEAR);
			int month = day.get(Calendar.MONTH);
			day.clear();
			day.set(year, month + 1, 0);
			Date endDate = day.getTime();
			Date startDate = new Date();
			String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
			String endday = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
			rowInfo.put("ksrq", today);
			rowInfo.put("jsrq", endday);
			if (cpid != null)
				rowInfo.put("cpid", cpid);
			rowInfo.put("dwtxid",user.getDwtxId());
			
		}
		if (!isAdd)
			rowInfo.put(getOneTable());
	}

	/* 得到表对象 */
	public final EngineDataSet getOneTable() {
		if (!dsBuyPrice.isOpen())
			dsBuyPrice.open();
		return dsBuyPrice;
	}

	/* 得到一列的信息 */
	public final RowMap getRowinfo() {
		return rowInfo;
	}

	/**
	 * 得到固定查询的用户输入的值
	 * 
	 * @param col
	 *            查询项名称
	 * @return 用户输入的值 public final String getFixedQueryValue(String col) {
	 *         return fixedQuery.getSearchRow().get(col); }
	 */

	// ------------------------------------------
	// 操作实现的类
	// ------------------------------------------
	/**
	 * 初始化操作的触发类
	 */
	class BuyPrice_Init implements Obactioner {
		// ----Implementation of the Obactioner interface
		/**
		 * 触发初始化操作
		 * 
		 * @parma action 触发执行的参数（键值）
		 * @param o
		 *            触发者对象
		 * @param data
		 *            传递的信息的类
		 * @param arg
		 *            触发者对象调用<code>notifyObactioners</code>方法传递的参数
		 */
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			cpid = data.getParameter("cpid");
			retuUrl = data.getParameter("src");
			retuUrl = retuUrl != null ? retuUrl.trim() : retuUrl;
			// 初始化时清空数据集
			// RowMap row = fixedQuery.getSearchRow();
			// row.clear();
			table.getWhereInfo().clearWhereValues();
			String SQL = "   ";

			String MSQL = combineSQL(MASTER_STRUT_SQL, "?", new String[] { user.getDwtxId(), SQL });
			dsBuyPrice.setQueryString(MSQL);
			if (dsBuyPrice.isOpen() && dsBuyPrice.getRowCount() > 0)
				dsBuyPrice.empty();
			dsBuyPrice.setRowMax(null);
			if(cpid!=null)
				data.setMessage(showJavaScript("showInterFrame(11,-1);"));
			// data.setMessage(showJavaScript("showFixedQuery()"));// 初始化弹出查询界面
		}
	}

	/**
	 * 添加查询操作的触发类
	 */
	class FIXED_SEARCH implements Obactioner {
		// ----Implementation of the Obactioner interface
		/**
		 * 添加或修改的触发操作
		 * 
		 * @parma action 触发执行的参数（键值）
		 * @param o
		 *            触发者对象
		 * @param data
		 *            传递的信息的类
		 * @param arg
		 *            触发者对象调用<code>notifyObactioners</code>方法传递的参数
		 */
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			table.getWhereInfo().setWhereValues(data.getRequest());
			// fixedQuery.setSearchValue(data.getRequest());
			String SQL = table.getWhereInfo().getWhereQuery();
			if (SQL.length() > 0)
				SQL = " AND " + SQL;
			SQL = combineSQL(BUYPRICE_SQL, "?", new String[] { SQL });
			dsBuyPrice.setQueryString(SQL);
			dsBuyPrice.setRowMax(null);
		}

	}

	/**
	 * 添加或修改操作的触发类
	 */
	class BuyPrice_Add_Edit implements Obactioner {
		// ----Implementation of the Obactioner interface
		/**
		 * 添加或修改的触发操作
		 * 
		 * @parma action 触发执行的参数（键值）
		 * @param o
		 *            触发者对象
		 * @param data
		 *            传递的信息的类
		 * @param arg
		 *            触发者对象调用<code>notifyObactioners</code>方法传递的参数
		 */
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			isAdd = action.equals(String.valueOf(ADD));
			if (!isAdd) {
				dsBuyPrice.goToRow(Integer.parseInt(data.getParameter("rownum")));
				editrow = dsBuyPrice.getInternalRow();
			}
			initRowInfo(isAdd, true);
			
		}
	}

	/**
	 * 选择外币触发事件
	 */
	class Wb_Onchange implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			HttpServletRequest req = data.getRequest();
			rowInfo.put(req);

			String wbid = rowInfo.get("wbid");
			String bj = rowInfo.get("bj");
			RowMap foreignRow = getForeignBean(req).getLookupRow(wbid);
			String hl = foreignRow.get("hl");
			double curhl = hl.length() > 0 ? Double.parseDouble(hl) : 0;
			double curbj = bj.length() > 0 ? Double.parseDouble(bj) : 0;
			double wbbj = curhl == 0 ? 0 : curbj / curhl;
			rowInfo.put("wbid", wbid);
			rowInfo.put("hl", hl);
			rowInfo.put("wbbj", formatNumber(String.valueOf(wbbj), priceFormat));
		}
	}

	/**
	 * 保存操作的触发类
	 */
	class BuyPrice_Post implements Obactioner {
		// ----Implementation of the Obactioner interface
		/**
		 * 触发保存操作
		 * 
		 * @parma action 触发执行的参数（键值）
		 * @param o
		 *            触发者对象
		 * @param data
		 *            传递的信息的类
		 * @param arg
		 *            触发者对象调用<code>notifyObactioners</code>方法传递的参数
		 */
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			EngineDataSet ds = getOneTable();
			// 校验数据
			rowInfo.put(data.getRequest());
			String dwtxid = rowInfo.get("dwtxid");
			String cpid = rowInfo.get("cpid");
			String bj = rowInfo.get("bj");
			// String sfhssl = rowInfo.get("sfhssl");
			String yhtj = rowInfo.get("yhtj");
			String ksrq = rowInfo.get("ksrq");
			String jsrq = rowInfo.get("jsrq");
			String gyslh = rowInfo.get("gyslh");
			String bz = rowInfo.get("bz");
			String hl = rowInfo.get("hl");
			String wbbj = rowInfo.get("wbbj");
			String sflsbj = rowInfo.get("sflsbj");
			if (!ksrq.equals("") && !jsrq.equals("")) {
				java.sql.Date ksrqDtae = java.sql.Date.valueOf(ksrq);
				java.sql.Date jsrqDtae = java.sql.Date.valueOf(jsrq);
				if (jsrqDtae.before(ksrqDtae)) {
					data.setMessage(showJavaScript("alert('结束日期不能小于开始日期！');"));
					return;
				}
				if (sflsbj.equals("1")) {
					ds.setValue("jsrq", ksrq);
					ds.setValue("sflsbj", "1");
					ds.post();
					ds.insertRow(false);
					ds.setValue("cgbjID", "-1");
				}
			}
			if (dwtxid.equals("")) {
				data.setMessage(showJavaScript("alert('供应商ID不能为空！');"));
				return;
			}
			if (cpid.equals("")) {
				data.setMessage(showJavaScript("alert('产品ID不能为空！');"));
				return;
			}
			String temp = null;
			if (bj.equals("")) {
				data.setMessage(showJavaScript("alert('报价不能为空！');"));
				return;
			}
			if (bj.length() > 0 && (temp = checkNumber(bj, "原币报价", false)) != null) {
				data.setMessage(temp);
				return;
			}
			if (hl.length() > 0 && (temp = checkNumber(hl, "汇率", false)) != null) {
				data.setMessage(temp);
				return;
			}
			if (wbbj.length() > 0 && (temp = checkNumber(wbbj, "外币报价")) != null) {
				data.setMessage(temp);
				return;
			}
			double d_bj = bj.length() > 0 ? Double.parseDouble(bj) : 0;
			double d_hl = hl.length() > 0 ? Double.parseDouble(hl) : 0;
			if (!isAdd)
				ds.goToInternalRow(editrow);

			if (isAdd) {
				ds.insertRow(false);
				ds.setValue("cgbjID", "-1");
			}
			ds.setValue("dwtxid", dwtxid);
			ds.setValue("cpid", cpid);
			// ds.setValue("sfhssl",sfhssl);
			ds.setValue("bj", bj);
			ds.setValue("wbid", rowInfo.get("wbid"));
			ds.setValue("hl", hl);
			ds.setValue("wbbj", d_hl == 0 ? "" : formatNumber(String.valueOf(d_bj / d_hl), priceFormat));
			ds.setValue("yhtj", yhtj);
			ds.setValue("ksrq", ksrq);
			ds.setValue("jsrq", jsrq);
			ds.setValue("gyslh", gyslh);
			ds.setValue("dmsxid", rowInfo.get("dmsxid"));
			ds.setValue("bz", bz);
			ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));// 制单日期
			ds.setValue("czyid", loginId);
			ds.setValue("fgsid", fgsid);
			ds.setValue("czy", loginName); // 操作员
			ds.post();
			ds.saveChanges();
			data.setMessage(showJavaScript("parent.hideInterFrame();"));
		}
	}

	/**
	 * 删除操作的触发类
	 */
	class BuyPrice_Delete implements Obactioner {
		// ----Implementation of the Obactioner interface
		/**
		 * 触发删除操作
		 * 
		 * @parma action 触发执行的参数（键值）
		 * @param o
		 *            触发者对象
		 * @param data
		 *            传递的信息的类
		 * @param arg
		 *            触发者对象调用<code>notifyObactioners</code>方法传递的参数
		 */
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			EngineDataSet ds = getOneTable();
			ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
			ds.deleteRow();
			ds.saveChanges();
		}
	}

	/**
	 * 得到外币信息的bean
	 * 
	 * @param req
	 *            WEB的请求
	 * @return 返回外币信息bean
	 */
	public LookUp getForeignBean(HttpServletRequest req) {
		if (foreignBean == null)
			foreignBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_FOREIGN_CURRENCY);
		return foreignBean;
	}
}

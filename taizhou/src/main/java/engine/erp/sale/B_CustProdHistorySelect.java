package engine.erp.sale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import com.borland.dx.dataset.Locate;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.RowMap;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;

/**
 * <p>
 * Title: 销售子系统_销售货物选择--客户历史产品
 * </p>
 * <p>
 * Description: 销售子系统_销售货物选择--客户历史产品
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @version 1.0
 */

public final class B_CustProdHistorySelect extends BaseAction implements Operate {
	// private static final String CUST_GOODS_STRUCT_SQL =
	// "SELECT * FROM VW_XS_WZDJ WHERE 1<>1";
	// 客户历史产品
	private static final String CUST_GOODS_SQL = "SELECT * FROM VW_XS_HITORY_PRODUCT WHERE 1=1 AND dwtxid=?  ORDER BY cpbm";
	private static final String CUST_STORE_GOODS_SQL = "SELECT * FROM VW_XS_HITORY_PRODUCT WHERE (storeid IS NULL OR storeid=? )  AND dwtxid=?   ORDER BY cpbm";
	// 特殊客户,引入客户产品折扣中定价的产品
	private static final String CUST_GOODS_HELP_SQL = "SELECT * FROM VW_XS_KHCPZK WHERE 1=1 AND dwtxid=? ORDER BY cpbm";
	private static final String CUST_STORE_GOODS_HELP_SQL = "SELECT * FROM VW_XS_KHCPZK WHERE (storeid IS NULL OR storeid=? )   AND dwtxid=?  ORDER BY cpbm";

	private EngineDataSet dsCustGoods = new EngineDataSet();// 主表
	private EngineRow locateRow = null;

	// private QueryBasic fixedQuery = new QueryFixedItem();
	// private boolean isInitQuery = false;
	public String retuUrl = null;
	private String fgsid = null; // 分公司ID
	private String khlx = "";

	/**
	 * 得到往来单位信息的实例
	 * 
	 * @param request
	 *            jsp请求
	 * @param isApproveStat
	 *            是否在审批状态
	 * @return 返回往来单位信息的实例
	 */
	public static B_CustProdHistorySelect getInstance(HttpServletRequest request) {
		B_CustProdHistorySelect custProdHistoryBean = null;
		HttpSession session = request.getSession(true);
		synchronized (session) {
			String beanName = "custProdHistoryBean";
			custProdHistoryBean = (B_CustProdHistorySelect) session.getAttribute(beanName);
			if (custProdHistoryBean == null) {
//				LoginBean loginBean = LoginBean.getInstance(request);
//				String fgsid = loginBean.getFirstDeptID();
//				String qtyFormat = loginBean.getQtyFormat();
//				String priceFormat = loginBean.getPriceFormat();
				custProdHistoryBean = new B_CustProdHistorySelect("0");
//				custProdHistoryBean.dsCustGoods.setColumnFormat("dj", priceFormat);

				session.setAttribute(beanName, custProdHistoryBean);
			}
		}
		return custProdHistoryBean;
	}

	/**
	 * 构造函数
	 */
	private B_CustProdHistorySelect(String fgsid) {
		this.fgsid = fgsid;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Implement this engine.project.OperateCommon abstract method 初始化函数
	 * 
	 * @throws Exception
	 *             异常信息
	 */
	protected final void jbInit() throws java.lang.Exception {
		setDataSetProperty(dsCustGoods, null);

		addObactioner(String.valueOf(INIT), new Init());
		// addObactioner(String.valueOf(FIXED_SEARCH), new Search());
	}

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
			log.error("doService", ex);
			return showMessage(ex.getMessage(), true);
		}
	}

	/**
	 * Session失效时，调用的函数
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		if (dsCustGoods != null) {
			dsCustGoods.close();
			dsCustGoods = null;
		}
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
	 * 初始化操作的触发类
	 */
	class Init implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			retuUrl = data.getParameter("src", "");
			String storeid = data.getParameter("storeid", "");// 仓库id
			String dwtxid = data.getParameter("dwtxid", "");
			khlx = data.getParameter("khlx", "");
			String lb = data.getParameter("lb", "");
			String SQL = "";
			// lb==1历史记录,lb==2客户产品折扣中定义的
			// 替换可变字符串，组装SQL
			if (lb.equals("1"))
				SQL = storeid.length() == 0 ? combineSQL(CUST_GOODS_SQL, "?", new String[] { dwtxid }) : combineSQL(CUST_STORE_GOODS_SQL,
						"?", new String[] { storeid, dwtxid });
			else
				SQL = storeid.length() == 0 ? combineSQL(CUST_GOODS_HELP_SQL, "?", new String[] { dwtxid }) : combineSQL(
						CUST_STORE_GOODS_HELP_SQL, "?", new String[] { storeid, dwtxid });

			dsCustGoods.setQueryString(SQL);
			dsCustGoods.setRowMax(null);
		}
	}

	/**
	 * 查询操作
	 * 
	 * class Search implements Obactioner { public void execute(String action,
	 * Obationable o, RunData data, Object arg) throws Exception { //初始化查询项目和内容
	 * //initQueryItem(data.getRequest()); QueryBasic queryBasic = fixedQuery;
	 * queryBasic.setSearchValue(data.getRequest()); String SQL =
	 * queryBasic.getWhereQuery(); if(SQL.length() > 0) SQL = " AND " + SQL;
	 * //替换可变字符串，组装SQL SQL = combineSQL(SALE_GOODS_SQL, "?", new String[]{fgsid,
	 * SQL}); if(!dsCustGoods.getQueryString().equals(SQL)) {
	 * dsCustGoods.setQueryString(SQL); dsCustGoods.setRowMax(null); } }
	 * 
	 * /** 初始化查询的各个列
	 * 
	 * @param request
	 *            web请求对象
	 * 
	 *            private void initQueryItem(HttpServletRequest request) {
	 *            if(isInitQuery) return; EngineDataSet ds = dsCustGoods;
	 *            if(!ds.isOpen()) ds.open();
	 * 
	 *            //初始化固定的查询项目 fixedQuery = new QueryFixedItem();
	 *            fixedQuery.addShowColumn("", new QueryColumn[]{//"",表示默认的表名
	 *            new QueryColumn(ds.getColumn("cpbm"), null, null, null), new
	 *            QueryColumn(ds.getColumn("th"), null, null, null), new
	 *            QueryColumn(ds.getColumn("product"), null, null, null), new
	 *            QueryColumn(ds.getColumn("zjm"), null, null, null) });
	 *            isInitQuery = true; }
	 * 
	 *            }
	 * 
	 *            得到固定查询的用户输入的值
	 * @param col
	 *            查询项名称
	 * @return 用户输入的值
	 * 
	 *         public final String getFixedQueryValue(String col) { return
	 *         fixedQuery.getSearchRow().get(col); }
	 **/
	/**
	 * 得到数据集对象
	 * 
	 * @return 返回数据集对象
	 */
	public final EngineDataSet getOneTable() {
		return dsCustGoods;
	}

	/**
	 * 根据根据产品ID得到相应的客户的产品折扣信息
	 * 
	 * @param cpid
	 *            产品ID
	 * @return 得到客户的产品折扣，具体字段见视图VW_XS_KHCPZK
	 */
	public final RowMap getSelectRow(String wzdjid) throws Exception {
		RowMap row = new RowMap();
		if (wzdjid == null || wzdjid.equals(""))
			return row;
		if (locateRow == null)
			locateRow = new EngineRow(dsCustGoods, "wzdjid");
		locateRow.setValue(0, wzdjid);
		// if(dsSearchGoods.locate(locateRow, Locate.FIRST))
		if (dsCustGoods.locate(locateRow, Locate.FIRST))
			row.put(dsCustGoods);
		return row;
	}
}

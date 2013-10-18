package engine.erp.store;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.project.*;
import engine.html.*;
import engine.common.*;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

/**
 * <p>Title: 报损单列表</p>
 * <p>Description: 报损单列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

/**
 * 2004-06-14 09:21 修改原来的batchRow.get("wzmxid")为只赋值为1 2004-4-20 18:08
 * 将原来的djxz=1改成现在的djxz=7 03.16 16:03 加入部门不能为空的判断. yjg 03.16 15:25 修改
 * 在Master_Search中去掉判断旧的SQL和新产生的SQL是否相同的if语句. yjg 03.16 15:04 修改
 * 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
 * 以实现:根据下达的部门，进行提交审批；
 */

public final class OtherDestroy extends BaseAction implements Operate {
	public static final String SINGLE_PRODUCT_ADD = "10801";
	public static final String CANCEL_APPROVE = "10201";
	public static final String STORE_ONCHANGE = "10031";// 选择仓库提交
	public static final String HSBL_ONCHANGE = "10041";// 提交换算比例
	public static final String SHOW_DETAIL = "12500";// 响应 调用从表明细资料 事件
	public static final String REPORT = "2000";// 报表追踪调用 事件 2.21
	public static final String NEXT = "9999";// 新增 为上一笔,下一笔打印而加的
	public static final String PRIOR = "9998";// 新增 为上一笔,下一笔打印而加的
	public static final String RECODE_ACCOUNT = "9996";// 2004-4-17 17:48 新增
														// 记帐功能的sql yjg
	public static final String TURNPAGE = "9995";// 新增 为明细表格番页而加的事件
	public static final String SELECTBATCH = "12401";

	private static final String MASTER_STRUT_SQL = "SELECT * FROM KC_OTHER_SFDJ WHERE 1<>1";
	private static String MASTER_SQL = "SELECT * FROM KC_OTHER_SFDJ WHERE dwtxid=? AND djxz=? and fgsid=? ? ORDER BY sfrq DESC";
	private static final String TOTALZSL_SQL = "SELECT SUM(nvl(zsl,0)) tzsl FROM KC_OTHER_SFDJ a WHERE dwtxid=? AND djxz=? AND fgsid=? ? ORDER BY sfdjdh DESC";
	private static final String DETAIL_STRUT_SQL = "SELECT * FROM KC_OTHER_SFDJMX WHERE 1<>1";
	// 02.28 23:15 修改 将下面此句的sql,用了'?'来代替原来的sfdjid=.
	// 解决了当sdfdjid是空的时候会页面上(主要是contract_instore_bottom.jsp上)会出现sql错误的问题.yjg
	private static final String DETAIL_SQL = "SELECT * FROM KC_OTHER_SFDJMX WHERE sfdjid='?'";//
	// 用于审批时候提取一条记录
	private static final String MASTER_APPROVE_SQL = "SELECT * FROM KC_OTHER_SFDJ WHERE djxz='?' and sfdjid='?'";
	// 查询数据库是否有记账的单据
	private static final String RECODE_DATASQL = " SELECT COUNT(*) FROM KC_OTHER_SFDJ a WHERE a.zdrid='?' AND  a.djxz ='?' AND a.zt=1";
	// 把符合记帐功能的数据全部记帐
	private static final String RECODE = "UPDATE KC_OTHER_SFDJ a SET a.zt=2 WHERE a.zdrid='?' AND  a.djxz ='?' AND a.zt=1";
	// 当删除一笔损益单的时候,如果这笔损益是由盘点单生成的,那么把它的这笔盘点单的状态置为0未审.
	private static final String UPDATE_KC_PD_SQL = "UPDATE kc_pd SET zt = 0 where pdid = '?'";
	private static final String UPDATE_KC_PD_STRUT_SQL = "SELECT * from kc_pd WHERE 1<>1";
	//
	private EngineDataSet dsMasterTable = new EngineDataSet();// 主表
	private EngineDataSet dsDetailTable = new EngineDataSet();// 从表
	private EngineDataSet dsTotalZsl = new EngineDataSet();// 统计总数量和
	private EngineDataSet dsUpdatePDTable = new EngineDataSet();// 当生成损
	public HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "kc_other_sfdj.5");
	public HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "kc_other_sfdjmx.5");

	private boolean isMasterAdd = true; // 是否在添加状态
	public boolean isApprove = false; // 是否在审批状态
	public boolean isDetailAdd = false; // 从表是否在审批状态
	public boolean isReport = false;//
	private long masterRow = -1; // 保存主表修改操作的行记录指针
	private RowMap m_RowInfo = new RowMap(); // 主表添加行或修改行的引用
	private ArrayList d_RowInfos = null; // 从表多行记录的引用

	private LookUp buyOrderBean = null; // 采购单价的bean的引用, 用于提取采购单价
	private Select_Batch selectBatchBean = null; // 选择批号的bean的引用, 用于从表产品选择批号

	private boolean isInitQuery = false; // 是否已经初始化查询条件
	private QueryBasic fixedQuery = new QueryFixedItem();
	public String retuUrl = null;
	// 2004-4-20 18:08 将原来的djxz=1改成现在的djxz=7
	public int djxz = 7;// 单据性质
	public String totalzsl = "0";
	public static String SYS_CUST_NAME = "";// 新增 2004-06-19 客户名称
											// 用于给不同的客户定制程序.现在第一次新增是因为essen有关排序的要求
											// yjg
	// 被分页后的数据集中某一个页面中从第几笔记录开始到第几笔数据结束.如第二页的资料范围是从第51-101笔
	public int min = 0;
	public int max = 0;

	public String loginId = ""; // 登录员工的ID
	public String loginCode = ""; // 登陆员工的编码
	public String loginName = ""; // 登录员工的姓名
	public String loginDept = ""; // 登录员工的部门
	// public String bjfs = ""; //系统的报价方式
	public String dwtxid = ""; // 登录员工dwtx的ID

	private String qtyFormat = null, priceFormat = null, sumFormat = null;
	private String fgsid = null; // 分公司ID
	private OtherUser user = null;

	/**
	 * 入库单列表的实例
	 * 
	 * @param request
	 *            jsp请求
	 * @param isApproveStat
	 *            是否在审批状态
	 * @return 返回入库单列表的实例
	 */
	public static OtherDestroy getInstance(HttpServletRequest request) {
		OtherDestroy destroyBean = null;
		HttpSession session = request.getSession(true);
		synchronized (session) {
			String beanName = "otherDestroy";
			destroyBean = (OtherDestroy) session.getAttribute(beanName);
			if (destroyBean == null) {
				// 引用LoginBean
				OtherLoginBean loginBean = OtherLoginBean.getInstance(request);

				destroyBean = new OtherDestroy();
				destroyBean.qtyFormat = loginBean.getQtyFormat();

				destroyBean.fgsid = loginBean.getFirstDeptID();
				destroyBean.loginId = loginBean.getUserID();
				destroyBean.dwtxid = loginBean.getUser().getDwtxId();
				
				destroyBean.loginName = loginBean.getUserName();
				destroyBean.loginDept = loginBean.getUser().getDeptId();
				destroyBean.user = loginBean.getUser();
				// destroyBean.bjfs =
				// loginBean.getSystemParam("BUY_PRICLE_METHOD");
				// 设置格式化的字段
				destroyBean.dsDetailTable.setColumnFormat("sl", destroyBean.qtyFormat);
				destroyBean.dsDetailTable.setColumnFormat("hssl", destroyBean.qtyFormat);
				destroyBean.dsMasterTable.setColumnFormat("zsl", destroyBean.qtyFormat);
				if (SYS_CUST_NAME.equals("essen"))
					MASTER_SQL = "SELECT * FROM KC_OTHER_SFDJ WHERE ? AND djxz=? and fgsid=? ? ORDER BY sfdjdh DESC, sfrq DESC";
				session.setAttribute(beanName, destroyBean);
			}
		}
		return destroyBean;
	}

	/**
	 * 构造函数
	 */
	private OtherDestroy() {
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
	private final void jbInit() throws java.lang.Exception {
		setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
		setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
		setDataSetProperty(dsTotalZsl, null);
		setDataSetProperty(dsUpdatePDTable, UPDATE_KC_PD_STRUT_SQL);

		dsMasterTable.setSequence(new SequenceDescriptor(new String[] { "sfdjdh" }, new String[] { "SELECT pck_base.billNextCode('KC_OTHER_SFDJ','sfdjdh','h') from dual" }));
		// dsMasterTable.setSort(new SortDescriptor("", new String[]{"sfdjdh"},
		// new boolean[]{true}, null, 0));

		dsDetailTable.setSequence(new SequenceDescriptor(new String[] { "rkdmxid" }, new String[] { "s_kc_sfdjmx" }));
		// dsDetailTable.setSort(new SortDescriptor("", new String[]{"hthwid"},
		// new boolean[]{false}, null, 0));

		Master_Add_Edit masterAddEdit = new Master_Add_Edit();
		Master_Post masterPost = new Master_Post();
		addObactioner(String.valueOf(INIT), new Init());
		addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
		addObactioner(String.valueOf(ADD), masterAddEdit);
		addObactioner(String.valueOf(EDIT), masterAddEdit);
		addObactioner(String.valueOf(DEL), new Master_Delete());
		addObactioner(String.valueOf(POST), masterPost);
		addObactioner(String.valueOf(POST_CONTINUE), masterPost);
		addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
		addObactioner(String.valueOf(APPROVE), new Approve());
		addObactioner(String.valueOf(REPORT), new Approve());// 2.14
		addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
		addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());// 取消审批操作
		addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
		addObactioner(String.valueOf(STORE_ONCHANGE), new Onchange());
		addObactioner(String.valueOf(HSBL_ONCHANGE), new Onchange());
		addObactioner(String.valueOf(SINGLE_PRODUCT_ADD), new Single_Product_Add());
		addObactioner(SHOW_DETAIL, new Show_Detail());// 02.17 21:54 新增
														// 新增查看从表明细资料事件发生时的触发操作类.
														// yjg
		addObactioner(NEXT, new Move_Cursor_ForPrint());
		addObactioner(PRIOR, new Move_Cursor_ForPrint());
		addObactioner(String.valueOf(RECODE_ACCOUNT), new Recode_Account());
		addObactioner(TURNPAGE, new Turn_Page());// 翻页事件
		addObactioner(String.valueOf(SELECTBATCH), new Detail_Select_Batch());
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
				if (data == null)
					return showMessage("无效操作", false);
				if (data.hasMessage())
					return data.getMessage();
			}
			return "";
		} catch (Exception ex) {
			if (dsMasterTable.isOpen() && dsMasterTable.changesPending())
				dsMasterTable.reset();
			log.error("doService", ex);
			return showMessage(ex.getMessage(), true);
		}
	}

	/**
	 * Session失效时，调用的函数
	 */
	public final void valueUnbound(HttpSessionBindingEvent event) {
		if (dsMasterTable != null) {
			dsMasterTable.close();
			dsMasterTable = null;
		}
		if (dsDetailTable != null) {
			dsDetailTable.close();
			dsDetailTable = null;
		}
		if (dsTotalZsl != null) {
			dsTotalZsl.close();
			dsTotalZsl = null;
		}
		log = null;
		m_RowInfo = null;
		d_RowInfos = null;
		if (masterProducer != null) {
			masterProducer.release();
			masterProducer = null;
		}
		if (detailProducer != null) {
			detailProducer.release();
			detailProducer = null;
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

	/**
	 * 初始化列信息
	 * 
	 * @param isAdd
	 *            是否时添加
	 * @param isInit
	 *            是否从新初始化
	 * @throws java.lang.Exception
	 *             异常
	 */
	private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception {
		// 是否是主表
		if (isMaster) {
			if (isInit && m_RowInfo.size() > 0)
				m_RowInfo.clear();

			if (!isAdd)
				m_RowInfo.put(getMaterTable());
			else {
				String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				m_RowInfo.put("zdrq", today);// 制单日期
				m_RowInfo.put("zdr", loginName);// 操作员
				m_RowInfo.put("sfrq", today);// 收发日期
				m_RowInfo.put("zdrid", loginId);
				m_RowInfo.put("deptid", loginDept);
				m_RowInfo.put("jsr", loginName);
				m_RowInfo.put("dwtxid", dwtxid);
			}
		} else {
			EngineDataSet dsDetail = dsDetailTable;
			if (d_RowInfos == null)
				d_RowInfos = new ArrayList(dsDetail.getRowCount());
			else if (isInit)
				d_RowInfos.clear();

			dsDetail.first();
			for (int i = 0; i < dsDetail.getRowCount(); i++) {
				RowMap row = new RowMap(dsDetail);
				d_RowInfos.add(row);
				dsDetail.next();
			}
		}
	}

	/**
	 * 从表保存操作
	 * 
	 * @param request
	 *            网页的请求对象
	 * @param response
	 *            网页的响应对象
	 * @return 返回HTML或javascipt的语句
	 * @throws Exception
	 *             异常
	 */
	private final void putDetailInfo(HttpServletRequest request) {
		RowMap rowInfo = getMasterRowinfo();
		// 保存网页的所有信息
		rowInfo.put(request);

		int rownum = d_RowInfos.size();
		RowMap detailRow = null;
		for (int i = min; i <= max; i++) {
			detailRow = (RowMap) d_RowInfos.get(i);
			detailRow.put("cpid", rowInfo.get("cpid_" + i));
			detailRow.put("kwid", rowInfo.get("kwid_" + i));// 库位
			detailRow.put("sl", formatNumber(rowInfo.get("sl_" + i), qtyFormat));//
			detailRow.put("hssl", formatNumber(rowInfo.get("hssl_" + i), qtyFormat));//
			detailRow.put("dmsxid", rowInfo.get("dmsxid_" + i));//
			detailRow.put("ph", rowInfo.get("ph_" + i));//
			detailRow.put("bz", rowInfo.get("bz_" + i));// 备注
		}
	}

	/* 得到表对象 */
	public final EngineDataSet getMaterTable() {
		return dsMasterTable;
	}

	/* 得到从表表对象 */
	public final EngineDataSet getDetailTable() {
		if (!dsDetailTable.isOpen())
			dsDetailTable.open();
		return dsDetailTable;
	}

	/* 打开从表 */
	public final void openDetailTable(boolean isMasterAdd) {
		String sfdjid = dsMasterTable.getValue("sfdjid");
		// 02.28 23:15 修改
		// 将下面此句setQueryString中的sql由原来的手动用+号组成sql`改成现在用combineSQL来组成.
		// 解决了当sdfdjid是空的时候会页面上(主要是contract_instore_bottom.jsp上)会出现sql错误的问题.yjg
		dsDetailTable.setQueryString(combineSQL(DETAIL_SQL, "?", new String[] { isMasterAdd ? "-1" : sfdjid }));

		if (dsDetailTable.isOpen())
			dsDetailTable.refresh();
		else
			dsDetailTable.open();
	}

	/* 得到主表一行的信息 */
	public final RowMap getMasterRowinfo() {
		return m_RowInfo;
	}

	/* 得到从表多列的信息 */
	public final RowMap[] getDetailRowinfos() {
		RowMap[] rows = new RowMap[d_RowInfos.size()];
		d_RowInfos.toArray(rows);
		return rows;
	}

	/**
	 * 主表是否在添加状态
	 * 
	 * @return 是否在添加状态
	 */
	public final boolean masterIsAdd() {
		return isMasterAdd;
	}

	/**
	 * 得到固定查询的用户输入的值
	 * 
	 * @param col
	 *            查询项名称
	 * @return 用户输入的值
	 */
	public final String getFixedQueryValue(String col) {
		return fixedQuery.getSearchRow().get(col);
	}

	/**
	 * 初始化操作的触发类
	 */
	class Init implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			isApprove = false;
			isDetailAdd = false;
			isReport = false;//
			retuUrl = data.getParameter("src");
			retuUrl = retuUrl != null ? retuUrl.trim() : retuUrl;
			//
			HttpServletRequest request = data.getRequest();
			masterProducer.init(request, loginId);
			detailProducer.init(request, loginId);
			// 初始化查询项目和内容
			RowMap row = fixedQuery.getSearchRow();
			row.clear();
			String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
			row.put("zt", "0");
			row.put("sfrq$a", startDay);
			row.put("sfrq$b", today);
			isMasterAdd = true;
			//
			String SQL = " AND zt<>2";
			SQL = combineSQL(MASTER_SQL, "?", new String[] { user.getDwtxId(), String.valueOf(djxz), fgsid, SQL });
			dsMasterTable.setQueryString(SQL);
			dsMasterTable.setRowMax(null);
			if (dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
				dsDetailTable.empty();

			String tempTzslSql = " AND zt<>2 ";
			tempTzslSql = combineSQL(TOTALZSL_SQL, "?", new String[] { user.getDwtxId(), String.valueOf(djxz), fgsid, tempTzslSql });
			dsTotalZsl.setQueryString(tempTzslSql);
			if (dsTotalZsl.isOpen())
				dsTotalZsl.refresh();
			else
				dsTotalZsl.openDataSet();
			if (dsTotalZsl.getRowCount() < 1)
				totalzsl = "0";
			else
				totalzsl = dsTotalZsl.getValue("tzsl");
		}
	}

	/**
	 * 主表添加或修改操作的触发类
	 */
	class Master_Add_Edit implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			isApprove = false;
			isDetailAdd = false;
			isReport = false;//
			isMasterAdd = String.valueOf(ADD).equals(action);
			if (!isMasterAdd) {
				isMasterAdd = false;
				dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
				masterRow = dsMasterTable.getInternalRow();
				openDetailTable(false);
			} else
				// 打开从表
				// 02.18 16:45 新增 同步子表 yjg
				synchronized (dsDetailTable) {
					openDetailTable(true);
				}
			initRowInfo(true, isMasterAdd, true);
			initRowInfo(false, isMasterAdd, true);

			data.setMessage(showJavaScript("toDetail();"));
		}
	}

	/**
	 * 审批操作的触发类
	 */
	class Approve implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			HttpServletRequest request = data.getRequest();
			masterProducer.init(request, loginId);
			detailProducer.init(request, loginId);
			/**
			 * 报表调从表页面,传递operate='2000'操作
			 */
			isReport = String.valueOf(REPORT).equals(action);
			String id = null;
			if (isReport) {
				isApprove = false;
				id = data.getParameter("id");// 得到报表传递的参数既收发单据主表ID
			} else {
				isApprove = true;// 审批操作
				id = data.getParameter("id", "");
			}

			// 得到request的参数,值若为null, 则用""代替
			String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[] { String.valueOf(djxz), id });
			dsMasterTable.setQueryString(sql);
			if (dsMasterTable.isOpen()) {
				dsMasterTable.readyRefresh();
				dsMasterTable.refresh();
			} else
				dsMasterTable.open();
			// 打开从表
			openDetailTable(false);

			initRowInfo(true, false, true);
			initRowInfo(false, false, true);
		}
	}

	/**
	 * 添加到审核列表的操作类
	 */
	class Add_Approve implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
			ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
			String content = dsMasterTable.getValue("sfdjdh");
			dsMasterTable.setValue("zt", "1");
			dsMasterTable.post();
			// 03.16 15:04 修改
			// 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
			// 以实现:根据下达的部门，进行提交审批；
//			approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "report_destroy_list", content, dsMasterTable.getValue("deptid"));
		}
	}

	/**
	 * 取消审批触发操作
	 */
	class Cancel_Approve implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
			ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
			approve.cancelAprove(dsMasterTable, dsMasterTable.getRow(), "report_destroy_list");
		}
	}

	/**
	 * 提交仓库
	 */
	class Onchange implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			HttpServletRequest request = data.getRequest();
			boolean isStore = String.valueOf(STORE_ONCHANGE).equals(action);
			if (isStore)
				m_RowInfo.put(request);
			else
				putDetailInfo(request);
		}
	}

	/**
	 * 从表增加操作（单选产品）
	 */
	class Single_Product_Add implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			HttpServletRequest req = data.getRequest();
			// 保存输入的明细信息
			putDetailInfo(data.getRequest());
			int row = Integer.parseInt(data.getParameter("rownum"));
			String singleIdInput = m_RowInfo.get("singleIdInput_" + row);
			if (singleIdInput.equals(""))
				return;

			// 实例化查找数据集的类
			String cpid = singleIdInput;
			if (!isMasterAdd)
				dsMasterTable.goToInternalRow(masterRow);
			String sfdjid = dsMasterTable.getValue("sfdjid");
			dsDetailTable.goToRow(row);
			RowMap detailrow = null;
			detailrow = (RowMap) d_RowInfos.get(row);
			detailrow.put("rkdmxid", "-1");
			detailrow.put("cpid", cpid);
			detailrow.put("sfdjid", isMasterAdd ? "-1" : sfdjid);
		}
	}

	/**
	 * 主表保存操作的触发类
	 */
	class Master_Post implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			putDetailInfo(data.getRequest());

			EngineDataSet ds = getMaterTable();
			RowMap rowInfo = getMasterRowinfo();
			// 校验表单数据
			String temp = checkMasterInfo();
			if (temp != null) {
				data.setMessage(temp);
				return;
			}
			temp = checkDetailInfo();
			if (temp != null) {
				data.setMessage(temp);
				return;
			}
			if (!isMasterAdd)
				ds.goToInternalRow(masterRow);

			// 得到主表主键值
			String sfdjid = null;
			if (isMasterAdd) {
				ds.insertRow(false);
				sfdjid = dataSetProvider.getSequence("s_kc_sfdj");
				ds.setValue("sfdjid", sfdjid);
				ds.setValue("fgsid", fgsid);
				ds.setValue("zt", "0");
				ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));// 制单日期
				ds.setValue("zdrid", loginId);
				ds.setValue("zdr", loginName);// 操作员
			}
			// 保存从表的数据
			RowMap detailrow = null;
			BigDecimal totalNum = new BigDecimal(0);
			EngineDataSet detail = getDetailTable();
			detail.first();
			for (int i = 0; i < detail.getRowCount(); i++) {
				detailrow = (RowMap) d_RowInfos.get(i);
				// 新添的记录
				if (isMasterAdd)
					detail.setValue("sfdjid", sfdjid);

				// double hsbl = detailrow.get("hsbl").length() > 0 ?
				// Double.parseDouble(detailrow.get("hsbl")) : 0;//换算比例
				double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;
				double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;// 换算数量
				detail.setValue("sl", detailrow.get("sl"));// 保存数量
				detail.setValue("hssl", String.valueOf(hssl));// 保存数量String.valueOf(hsbl
																// == 0 ? 0 :
																// sl/hsbl)
				detail.setValue("cpid", detailrow.get("cpid"));
				detail.setValue("kwid", detailrow.get("kwid"));
				detail.setValue("dmsxid", detailrow.get("dmsxid"));// 物资规格属性
				detail.setValue("djxz", String.valueOf(djxz));
				detail.setValue("ph", detailrow.get("ph"));
				detail.setValue("bz", detailrow.get("bz"));// 备注
				detail.setValue("fgsid", fgsid);
				// 保存用户自定义字段
				FieldInfo[] fields = detailProducer.getBakFieldCodes();
				for (int j = 0; j < fields.length; j++) {
					String fieldCode = fields[j].getFieldcode();
					detail.setValue(fieldCode, detailrow.get(fieldCode));
				}
				detail.post();
				totalNum = totalNum.add(detail.getBigDecimal("sl"));
				detail.next();
			}

			// 保存主表数据
			ds.setValue("storeid", rowInfo.get("storeid"));// 仓库id
			ds.setValue("jsr", rowInfo.get("jsr"));// 经手人
			ds.setValue("deptid", rowInfo.get("deptid"));// 部门id
			ds.setValue("sfdjlbid", rowInfo.get("sfdjlbid"));// 收发单据类别ID
			ds.setValue("djxz", String.valueOf(djxz));// 单据性质
			ds.setValue("jfkm", rowInfo.get("jfkm"));// 用途ID
			ds.setValue("dwtxid", rowInfo.get("dwtxid"));// 购货单位ID
			ds.setValue("sfrq", rowInfo.get("sfrq"));// 收发日期
			ds.setValue("zsl", totalNum.toString());// 总数量
			ds.setValue("bz", rowInfo.get("bz"));// 备注
			// 保存用户自定义的字段
			FieldInfo[] fields = masterProducer.getBakFieldCodes();
			for (int j = 0; j < fields.length; j++) {
				String fieldCode = fields[j].getFieldcode();
				detail.setValue(fieldCode, rowInfo.get(fieldCode));
			}
			ds.post();
			ds.saveDataSets(new EngineDataSet[] { ds, detail }, null);
			LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_LOSS);

			if (String.valueOf(POST_CONTINUE).equals(action)) {
				isMasterAdd = true;
				initRowInfo(true, true, true);
				detail.empty();
				initRowInfo(false, true, true);// 重新初始化从表的各行信息
			} else if (String.valueOf(POST).equals(action)) {
				// data.setMessage(showJavaScript("backList();"));
				isMasterAdd = false;
				masterRow = ds.getInternalRow();// 2004-3-30 14:53 新增
												// 如果是保存按钮行为完成后定位数据集 yjg
				initRowInfo(true, isMasterAdd, true);
				initRowInfo(false, isMasterAdd, true);
			}
		}

		/**
		 * 校验从表表单信息从表输入的信息的正确性
		 * 
		 * @return null 表示没有信息
		 */
		private String checkDetailInfo() {
			String temp = null;
			RowMap detailrow = null;
			if (d_RowInfos.size() < 1)
				return showJavaScript("alert('不能保存空的数据')");
			// ArrayList list = new ArrayList(d_RowInfos.size());
			for (int i = 0; i < d_RowInfos.size(); i++) {
				int row = i + 1;
				detailrow = (RowMap) d_RowInfos.get(i);
				String cpid = detailrow.get("cpid");
				if (cpid.equals(""))
					return showJavaScript("alert('第" + row + "行产品不能为空');");
				/**
				 * if(list.contains(cpid)) return
				 * showJavaScript("alert('第"+row+"行产品重复');"); else
				 * list.add(cpid);
				 */
				String sl = detailrow.get("sl");
				if ((temp = checkNumber(sl, "第" + row + "行数量")) != null)
					return temp;
				String hssl = detailrow.get("hssl");
				if ((temp = checkNumber(hssl, "第" + row + "行数量")) != null)
					return temp;
				if (sl.length() > 0 && sl.equals("0") && hssl.length() > 0 && hssl.equals("0"))
					return showJavaScript("alert('第" + row + "行数量和换算数量不能同时为零！');");
			}
			return null;
		}

		/**
		 * 校验主表表表单信息从表输入的信息的正确性
		 * 
		 * @return null 表示没有信息,校验通过
		 */
		private String checkMasterInfo() {
			RowMap rowInfo = getMasterRowinfo();
			String temp = rowInfo.get("sfrq");
			if (temp.equals(""))
				return showJavaScript("alert('收发日期不能为空！');");
			else if (!isDate(temp))
				return showJavaScript("alert('非法收发日期！');");
//			temp = rowInfo.get("storeid");
//			if (temp.equals(""))
//				return showJavaScript("alert('请选择仓库！');");
//			// 03.16 16:03 加入部门不能为空的判断. yjg
//			temp = rowInfo.get("deptid");
//			if (temp.equals(""))
//				return showJavaScript("alert('请选择部门！');");
			return null;
		}
	}

	/**
	 * 主表删除操作
	 */
	class Master_Delete implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			if (isMasterAdd) {
				data.setMessage(showJavaScript("backList();"));
				return;
			}
			EngineDataSet ds = getMaterTable();
			ds.goToInternalRow(masterRow);
			String pdid = ds.getValue("pdid");
			dsDetailTable.deleteAllRows();
			ds.deleteRow();
			dsUpdatePDTable.updateQuery(new String[] { combineSQL(UPDATE_KC_PD_SQL, "?", new String[] { pdid }) });
			if (dsUpdatePDTable.isOpen()) {
				dsUpdatePDTable.readyRefresh();
				dsUpdatePDTable.refresh();
			} else
				dsUpdatePDTable.open();
			ds.saveDataSets(new EngineDataSet[] { ds, dsDetailTable, dsUpdatePDTable }, null);

			dsUpdatePDTable.post();
			dsUpdatePDTable.saveChanges();
			LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE);
			//
			d_RowInfos.clear();
			data.setMessage(showJavaScript("backList();"));
		}
	}

	/**
	 * 查询操作
	 */
	class Master_Search implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			initQueryItem(data.getRequest());
			fixedQuery.setSearchValue(data.getRequest());
			String SQL = fixedQuery.getWhereQuery();
			if (SQL.length() > 0)
				SQL = " AND " + SQL;
			SQL = combineSQL(MASTER_SQL, "?", new String[] { user.getDwtxId(), String.valueOf(djxz), fgsid, SQL });
			// 03.16 15:25 修改 在Master_Search中去掉判断旧的SQL和新产生的SQL是否相同的if语句. yjg
			// if(!dsMasterTable.getQueryString().equals(SQL))
			// {
			dsMasterTable.setQueryString(SQL);
			dsMasterTable.setRowMax(null);
			// }
			String tempTzslSql = fixedQuery.getWhereQuery();
			if (tempTzslSql.length() > 0)
				tempTzslSql = " AND " + tempTzslSql;
			tempTzslSql = combineSQL(TOTALZSL_SQL, "?", new String[] { user.getDwtxId(), String.valueOf(djxz), fgsid, tempTzslSql });
			dsTotalZsl.setQueryString(tempTzslSql);
			if (dsTotalZsl.isOpen())
				dsTotalZsl.refresh();
			else
				dsTotalZsl.openDataSet();
			if (dsTotalZsl.getRowCount() < 1)
				totalzsl = "0";
			else
				totalzsl = dsTotalZsl.getValue("tzsl");
		}

		/**
		 * 初始化查询的各个列
		 * 
		 * @param request
		 *            web请求对象
		 */
		private void initQueryItem(HttpServletRequest request) {
			if (isInitQuery)
				return;
			EngineDataSet master = dsMasterTable;
			// EngineDataSet detail = dsDetailTable;
			if (!master.isOpen())
				master.open();
			// 初始化固定的查询项目
			fixedQuery = new QueryFixedItem();
			fixedQuery.addShowColumn("", new QueryColumn[] { new QueryColumn(master.getColumn("sfdjdh"), null, null, null),
					new QueryColumn(master.getColumn("sfrq"), null, null, null, "a", ">="), new QueryColumn(master.getColumn("sfrq"), null, null, null, "b", "<="),
					new QueryColumn(master.getColumn("sfdjlbid"), null, null, null, null, "="),// 单据类别
					new QueryColumn(master.getColumn("storeid"), null, null, null, null, "="),// 仓库
					new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),// 部门
					new QueryColumn(master.getColumn("sfdjid"), "KC_OTHER_SFDJMX", "sfdjid", "cpid", null, "="),// 从表规格
					new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "cpbm", "cpbm", "like"),// 从表产品编码
					new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "product", "product", "like"),// 从表产品名称
					new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "pm", "pm", "like"),// 从表产品名称
					new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "gg", "gg", "like"),// 从表产品名称
					new QueryColumn(master.getColumn("zt"), null, null, null, null, "="), new QueryColumn(master.getColumn("jsr"), null, null, null) });
			isInitQuery = true;
		}
	}

	/**
	 * 从表增加操作(增加一空白行)
	 */
	class Detail_Add implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			HttpServletRequest req = data.getRequest();
			// 保存输入的明细信息
			putDetailInfo(data.getRequest());
			EngineDataSet detail = getDetailTable();
			EngineDataSet ds = getMaterTable();
			isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
			if (!isMasterAdd)
				ds.goToInternalRow(masterRow);
			String sfdjid = ds.getValue("sfdjid");
			detail.insertRow(false);
			detail.setValue("sfdjid", isMasterAdd ? "-1" : sfdjid);
			detail.post();
			d_RowInfos.add(new RowMap());
		}
	}

	/**
	 * 从表删除操作
	 */
	class Detail_Delete implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			putDetailInfo(data.getRequest());
			EngineDataSet ds = getDetailTable();
			int rownum = Integer.parseInt(data.getParameter("rownum"));
			// 删除临时数组的一列数据
			d_RowInfos.remove(rownum);
			ds.goToRow(rownum);
			ds.deleteRow();
		}
	}

	// 02.17 21:54 新增 新增一个当页面上单击事件发生时想要查看明细资料 这个单击事件的Show_Detail类 . yjg
	/**
	 * 显示从表的列表信息
	 */
	class Show_Detail implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
			masterRow = dsMasterTable.getInternalRow();
			// 打开从表
			openDetailTable(false);
		}

	}

	/**
	 * 新增 实现翻页为方便打印的类.
	 */
	class Move_Cursor_ForPrint implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			boolean isNext = String.valueOf(NEXT).equals(action);
			dsMasterTable.goToInternalRow(masterRow);
			if (isNext)
				dsMasterTable.next();
			else
				dsMasterTable.prior();
			masterRow = dsMasterTable.getInternalRow();

			// dsMasterTable.goToInternalRow(masterRow+1);
			// masterRow = dsMasterTable.getInternalRow();
			// int i = dsMasterTable.getRow();
			synchronized (dsDetailTable) {
				openDetailTable(false);
			}
			initRowInfo(true, false, true);
			initRowInfo(false, false, true);
		}
	}

	/**
	 * 2004-4-17 17:48 新增 记帐功能 yjg
	 */
	class Recode_Account implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			// 是否有符合记帐的数据，有几条
			String SQL = combineSQL(RECODE_DATASQL, "?", new String[] { loginId, String.valueOf(djxz) });
			String UPDATE_SQL = combineSQL(RECODE, "?", new String[] { loginId, String.valueOf(djxz) });
			String count = dataSetProvider.getSequence(SQL);
			if (count.equals("0")) {
				data.setMessage(showJavaScript("alert('没有可以记帐的单据')"));
				return;
			} else {
				dsMasterTable.updateQuery(new String[] { UPDATE_SQL });
				dsMasterTable.readyRefresh();
				dsMasterTable.refresh();
			}
		}
	}

	public long getMasterRow() {
		return masterRow;
	}

	/**
	 * 得到选中的行的行数
	 * 
	 * @return 若返回-1，表示没有选中的行
	 */
	public final int getSelectedRow() {
		if (masterRow < 0)
			return -1;

		dsMasterTable.goToInternalRow(masterRow);
		return dsMasterTable.getRow();
	}

	/**
	 * 2004-5-2 19:00 明细资料数据集页面翻页功能.
	 */
	class Turn_Page implements Obactioner {
		/**
		 * 按页翻动明细数据集的数据
		 */
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			// 保存输入的明细信息
			putDetailInfo(data.getRequest());
		}
	}

	/**
	 * 2004-05-15 新增从表选择批号操作
	 */
	class Detail_Select_Batch implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			HttpServletRequest req = data.getRequest();
			// 保存输入的明细信息
			putDetailInfo(data.getRequest());
			int row = Integer.parseInt(req.getParameter("rownum"));
			String mutibatch = m_RowInfo.get("mutibatch_" + row);
			if (mutibatch.length() == 0)
				return;
			dsDetailTable.goToRow(row);
			String sfdjid = dsDetailTable.getValue("sfdjid");
			String wjid = dsDetailTable.getValue("wjid");
			String cpid = dsDetailTable.getValue("cpid");
			// 实例化查找数据集的类
			// 02.14 21:12 修改: 此处参数多加了 cpid, dmsxid两个.目地是:在 批号选择清单 选物资明细时只有cpid,
			// ph, dmsxid组合不同的才允许带回给提单界面 yjg
			EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[] { "cpid", "ph", "dmsxid" });
			String[] wzmxIDs = parseString(mutibatch, ",");
			RowMap detail = null;
			for (int i = 0; i < wzmxIDs.length; i++) {
				if (wzmxIDs[i].equals("-1"))
					continue;
				RowMap batchRow = getBatchBean(req).getLookupRow(wzmxIDs[i]);
				// 配合 上面 02.14 21:12 的改动.此处也作了相应改动 yjg
				String ph = batchRow.get("ph");
				String newcpid = batchRow.get("cpid");
				String dmsxid = batchRow.get("dmsxid");
				locateGoodsRow.setValue(0, newcpid);
				locateGoodsRow.setValue(1, ph); // 03.10 22:40 修改
												// 修改原来的setValue(0,
												// value)为现在的(数组索引, value) 与
												// 02.14 21:12处的数组对应. yjg
				locateGoodsRow.setValue(2, dmsxid);
				if (!dsDetailTable.locate(locateGoodsRow, Locate.FIRST)) {
					if (i == 0) {
						detail = (RowMap) d_RowInfos.get(row);
					} else {
						dsDetailTable.insertRow(false);
						dsDetailTable.setValue("sfdjid", sfdjid);
						dsDetailTable.setValue("wjid", wjid);
						dsDetailTable.setValue("cpid", cpid);
						dsDetailTable.post();
						detail = new RowMap(dsDetailTable);
						detail.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
						d_RowInfos.add(++row, detail);
					}
					detail.put("wzmxid", "1");// 2004-06-14 09:21
												// 修改原来的batchRow.get("wzmxid")为只赋值为1
					detail.put("ph", batchRow.get("ph"));
					detail.put("dmsxid", batchRow.get("dmsxid"));
					detail.put("kwid", batchRow.get("kwid"));
					// if (isKC_OUT_STORE_STYLE) //03:26 21:15 新增
					// 当配批号的时候是否也要把配批号页面上的库存量引过来 yjg
					detail.put("sl", batchRow.get("zl"));
					/*
					 * if (i==0){ detail = (RowMap)d_RowInfos.get(row);
					 * detail.put("wzmxid",batchRow.get("wzmxid"));
					 * detail.put("ph",batchRow.get("ph"));
					 * detail.put("dmsxid",batchRow.get("dmsxid"));
					 * detail.put("kwid",batchRow.get("kwid")); if
					 * (isKC_OUT_STORE_STYLE)
					 * detail.put("sl",batchRow.get("zl")); }
					 */
				} else {
					detail = (RowMap) d_RowInfos.get(row);
					detail.put("wzmxid", "1");// 2004-06-14 09:21
												// 修改原来的batchRow.get("wzmxid")为只赋值为1
					detail.put("ph", batchRow.get("ph"));
					detail.put("dmsxid", batchRow.get("dmsxid"));
					detail.put("kwid", batchRow.get("kwid"));
					// if (isKC_OUT_STORE_STYLE) //03:26 21:15 新增
					// 当配批号的时候是否也也要把配批号页面上的库存量引过来 yjg
					detail.put("sl", batchRow.get("zl"));
				}
				// RowMap detailrow = new RowMap(dsDetailTable);
				// detailrow.put("InternalRow",
				// String.valueOf(dsDetailTable.getInternalRow()));
				// d_RowInfos.add(detailrow);
			}
			data.setMessage(showJavaScript("totalCalSl();"));
		}
	}

	/**
	 * 得到用于选择批号信息的bean
	 * 
	 * @param req
	 *            WEB的请求
	 * @return 返回用于选择批号信息的bean
	 */
	public Select_Batch getBatchBean(HttpServletRequest req) {
		if (selectBatchBean == null)
			selectBatchBean = Select_Batch.getInstance(req);
		return selectBatchBean;
	}
}

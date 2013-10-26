package engine.erp.baseinfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


import engine.action.BaseAction;
import engine.action.Operate;
import engine.common.LoginBean;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.RowMap;
import engine.dataset.SequenceDescriptor;
import engine.project.LookUp;
import engine.project.LookupBeanFacade;
import engine.project.QueryBasic;
import engine.project.QueryColumn;
import engine.project.QueryFixedItem;
import engine.project.SysConstant;
import engine.util.StringUtils;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;

/**
 * <p>
 * Title: 基础维护－物资代码维护
 * </p>
 * <p>
 * Description: 基础维护－物资代码维护<br>
 * 在列表中需要得到往来单位的信息的方法：先调getPersonsData(String[] personIDs)方法得到数据,
 * 再调getPersonName(String personId)得到往来单位名称并定位数据集。或用调用getPersonRow(String
 * personId)得到一行信息
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 李建华
 * @version 1.0
 */

public class Product extends BaseAction implements Operate {

	// 在物资树下添加员工信息
	public static final int SEARCH = 10002;
	public static final int ONCHANGE = 10003;

	private static final String PRODUCT_STURCT_SQL = "SELECT * FROM kc_dm a WHERE 1<>1";

	private static final String PRODUCT_NORMAL_SQL = "SELECT a.* FROM kc_dm a WHERE (a.storeid IS NULL OR a.storeid IN (SELECT c.storeid FROM jc_ckqx c WHERE c.personid='{personid}')) "
			+ "AND a.isdelete=0 ? ORDER BY a.cpbm";

	private static final String PRODUCT_RUIJIAO_SQL = "SELECT a.* FROM kc_dm a, kc_dmlb b WHERE a.wzlbid=b.wzlbid AND a.isdelete=0 ? ORDER BY b.bm, a.pm, a.gg";

	private static final String PRODUCT_LEN_SQL = "SELECT autolen FROM jc_coderule_cont WHERE coderule='product'";
	private static final String UPDATE_SQL = "{CALL PCK_PRODUCE.updateBomProduceTime(@)}";
	private static final String PRODUCT_LIKE_1 = "(a.pm LIKE '%{prod}%' AND a.gg LIKE'%{prod2}%' ) OR (a.pm LIKE '%{prod}%' AND a.gg is null)";
	private static final String PRODUCT_LIKE_2 = "a.hh LIKE '%{prod}%' OR a.ks LIKE'%{prod}%'";

	// 一新客户参数值
	public static final String CUST_ESSEN = "essen";
	// 盛宇客户参数值
	public static final String CUST_SHENGYU = "shengyu";
	// 盛宇客户参数值
	public static final String CUST_RUIJIAO = "ruijiao";

	private EngineRow locateRow = null;

	public EngineDataSet dsProduct = new EngineDataSet();
	public RowMap rowInfo = new RowMap(); // 添加行或修改行的引用

	public boolean isAdd = true;// 是否在添加状态
	public boolean isWzAddProduct = false;// 是否是在部门树下添加员工
	public String wzlbid = null;// 保存在物资类别树下添加物资传递的物资类别id
	private long editrow = 0;// 保存修改记录的记录行

	private boolean isInitQuery = false; // 是否已经初始化查询条件
	private QueryBasic fixedQuery = new QueryFixedItem();
	public String retuUrl = null;
	public String loginName = ""; // 登录员工的姓名

	public int product_len = 0;// 顺序号位数
	private String qtyFormat = null, priceFormat = null, sumFormat = null;
	private String parentCode = null;
	private String prefix = null;// 前缀编码
	public String systemCustName = null;// 系统的客户名称
	private String PRODUCT_SQL = null;

	private File photo = null;

	/**
	 * 得到物资代码信息的实例
	 * 
	 * @param request
	 *            jsp请求
	 * @return 返回物资代码信息的实例
	 */
	public static Product getInstance(HttpServletRequest request) {
		Product productBean = null;
		HttpSession session = request.getSession(true);
		synchronized (session) {
			productBean = (Product) session.getAttribute("productBean");
			if (productBean == null) {
				LoginBean loginBean = LoginBean.getInstance(request);
				productBean = new Product();
				productBean.loginName = loginBean.getUserName();
				// 设置格式化的字段
				productBean.dsProduct.setColumnFormat("jhdj", loginBean.getPriceFormat());
				productBean.dsProduct.setColumnFormat("minsl", loginBean.getQtyFormat());
				productBean.dsProduct.setColumnFormat("maxsl", loginBean.getQtyFormat());
				productBean.systemCustName = loginBean.getSystemParam("SYS_CUST_NAME");
				productBean.PRODUCT_SQL = CUST_RUIJIAO.equals(productBean.systemCustName) ? PRODUCT_RUIJIAO_SQL
						: StringUtils.replace(PRODUCT_NORMAL_SQL, "{personid}", loginBean.getUserID());
				session.setAttribute("productBean", productBean);
			}
		}
		return productBean;
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
	 * 构造函数
	 */
	private Product() {
		try {
			jbInit();
		} catch (Exception ex) {
			log.error("jbInit", ex);
		}
	}

	/**
	 * jvm要调的函数,类似于析构函数
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		if (dsProduct != null) {
			dsProduct.close();
			dsProduct = null;
		}
		// vLimitTreeInfo = null;
		rowInfo = null;
	}

	/**
	 * 初始化函数
	 * 
	 * @throws Exception
	 *             异常信息
	 */
	private void jbInit() throws Exception {
		setDataSetProperty(dsProduct, PRODUCT_STURCT_SQL);
		dsProduct.setSequence(new SequenceDescriptor(new String[] { "cpid" }, new String[] { "s_kc_dm" }));
		dsProduct.setTableName("kc_dm");
		Add_Edit addedit = new Add_Edit();
		addObactioner(String.valueOf(INIT), new Init());
		addObactioner(String.valueOf(SEARCH), new Search());
		addObactioner(String.valueOf(ADD), addedit);
		addObactioner(String.valueOf(EDIT), addedit);
		addObactioner(String.valueOf(DEL), new Delete());
		addObactioner(String.valueOf(POST), new Post());
		addObactioner(String.valueOf(PROD_CHANGE), new WzAddProduct());
		addObactioner(String.valueOf(ONCHANGE), new OnChange());
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
			String opearate = request.getParameter(OPERATE_KEY);
			if (opearate != null && opearate.trim().length() > 0) {
				RunData data = notifyObactioners(opearate, request, response, null);
				if (data == null)
					return showMessage("无效操作", false);
				if (data.hasMessage())
					return data.getMessage();
			}
			return "";
		} catch (Exception ex) {
			if (dsProduct.isOpen() && dsProduct.changesPending())
				dsProduct.reset();
			log.error("doService", ex);
			return showMessage(ex.getMessage(), true);
		}
	}

	/* 得到表对象 */
	public final EngineDataSet getOneTable() {
		if (!dsProduct.isOpen())
			dsProduct.open();
		return dsProduct;
	}

	/* 得到一列的信息 */
	public final RowMap getRowinfo() {
		return rowInfo;
	}

	/**
	 * 在物资类别树下添加物资
	 */
	class WzAddProduct implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			String length = dataSetProvider.getSequence(PRODUCT_LEN_SQL);
			product_len = Integer.parseInt(length);

			retuUrl = data.getParameter("src");
			retuUrl = retuUrl != null ? retuUrl.trim() : retuUrl;
			wzlbid = data.getParameter("wzlbid");
			if (wzlbid == null)
				wzlbid = "null";
			isWzAddProduct = true;
			RowMap row = fixedQuery.getSearchRow();
			row.clear();
			row.put("a$wzlbid", wzlbid);
			rowInfo.put("hsbl", "1");
			String SQL = combineSQL(PRODUCT_SQL, "?", new String[] { "AND a.wzlbid='" + wzlbid + "'" });
			dsProduct.setQueryString(SQL);
			dsProduct.setRowMax(null);
		}
	}

	/**
	 * 初始化操作的触发类
	 */
	class Init implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			String length = dataSetProvider.getSequence(PRODUCT_LEN_SQL);
			product_len = Integer.parseInt(length);
			retuUrl = data.getParameter("src");
			retuUrl = retuUrl != null ? retuUrl.trim() : retuUrl;
			isWzAddProduct = false;
			wzlbid = null;
			// 初始化查询项目和内容
			RowMap row = fixedQuery.getSearchRow();
			row.clear();
			//
			dsProduct.setQueryString(combineSQL(PRODUCT_SQL, "?", new String[] { "" }));
			dsProduct.setRowMax(null);
		}
	}

	/**
	 * 初始化操作的查询类
	 */
	class Search implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			initQueryItem(data.getRequest());
			fixedQuery.setSearchValue(data.getRequest());
			String pm = fixedQuery.getSearchRow().get("a$pm");
			String gg = fixedQuery.getSearchRow().get("a$gg");
			// 生成SQL前不组装pm
			fixedQuery.put("a$pm", "");
			String product = "";
			if (pm.length() > 0 || gg.length() > 0) {
				product = CUST_SHENGYU.equals(systemCustName) ? PRODUCT_LIKE_1 + " OR " + PRODUCT_LIKE_2
						: PRODUCT_LIKE_1;
				product = StringUtils.replace(product, "{prod}", pm);
				product = StringUtils.replace(product, "{prod2}", gg);
			}
			String SQL = fixedQuery.getWhereQuery();
			// 重新将pm保存回去
			fixedQuery.put("a$pm", pm);
			if (SQL.length() > 0)
				SQL = " AND " + SQL;
			if (product.length() > 0)
				SQL = " AND (" + product + ")" + SQL;
			SQL = combineSQL(PRODUCT_SQL, "?", new String[] { SQL });
			dsProduct.setQueryString(SQL);
			dsProduct.readyRefresh();
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
			EngineDataSet master = dsProduct;
			if (!master.isOpen())
				master.open();
			// 初始化固定的查询项目
			fixedQuery.addShowColumn("a", new QueryColumn[] {
					new QueryColumn(master.getColumn("cpbm"), null, null, null),
					new QueryColumn(master.getColumn("pm"), null, null, null, null, "like"),// 品名模糊查询
					new QueryColumn(master.getColumn("gg"), null, null, null, null, "like"),// 规格模糊查询
					// new QueryColumn(master.getColumn("pm"), null, null,
					// null,"b","="),//品名等于
					new QueryColumn(master.getColumn("zjm"), null, null, null, null, "like"),// 助计码
					new QueryColumn(master.getColumn("issale"), null, null, null, null, "="),// 是否销售
					new QueryColumn(master.getColumn("isprops"), null, null, null, null, "="),// 有无规格属性
					new QueryColumn(master.getColumn("abc"), null, null, null, null, "="),// ABC类别
					new QueryColumn(master.getColumn("chxz"), null, null, null, null, "="),// 存货性质
					new QueryColumn(master.getColumn("jjff"), null, null, null, null, "="),// 计价方法
					new QueryColumn(master.getColumn("wzlbid"), null, null, null, null, "="),// 物资类别
					new QueryColumn(master.getColumn("chlbid"), null, null, null, null, "="),// 存货类别
					new QueryColumn(master.getColumn("storeid"), null, null, null, null, "="),// 存放仓库
					new QueryColumn(master.getColumn("deptid"), null, null, null, null, "=") // 生产车间
					});
			isInitQuery = true;
		}
	}

	/**
	 * 添加或修改操作的触发类
	 */
	class Add_Edit implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			isAdd = action.equals(String.valueOf(ADD));
			if (!isAdd) {
				dsProduct.goToRow(Integer.parseInt(data.getParameter("rownum")));
				editrow = dsProduct.getInternalRow();
			}

			initRowInfo(isAdd, true);
			if (isAdd && isWzAddProduct) {
				rowInfo.put("wzlbid", wzlbid);
				LookUp lookupBean = LookupBeanFacade.getInstance(data.getRequest(), SysConstant.BEAN_PRODUCT_KIND);
				String prefix = lookupBean.getLookupRow(wzlbid).get("bm");
				String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('kc_dm','cpbm','" + prefix
						+ "','isdelete=0'," + product_len + ",0) from dual");
				rowInfo.put("parentCode", prefix);
				rowInfo.put("self_code", code);
			}
		}
	}

	public static byte[] getBytes(File file) {
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 保存操作的触发类
	 */
	class Post implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			EngineDataSet ds = getOneTable();
			// 校验数据
			HttpServletRequest request = data.getRequest();


			rowInfo.put(request);
			String parentCode = rowInfo.get("parentCode");
			String self_Code = rowInfo.get("self_code");
			String pm = rowInfo.get("pm");// 品名
			String gg = rowInfo.get("gg");// 规格
			String cpids = rowInfo.get("cpid");// cpid

			String hsbl = rowInfo.get("hsbl");
			String minsl = rowInfo.get("minsl");
			String maxsl = rowInfo.get("maxsl");
			String chlbid = rowInfo.get("chlbid");// 存货类别id
			String storeid = rowInfo.get("storeid");// 存放仓库id
			String tqq = rowInfo.get("tqq");
			String scdwgs = rowInfo.get("scdwgs");// 生产单位公式
			String scydw = rowInfo.get("scydw");// 生产用单位
			String isbatchno = rowInfo.get("isbatchno");
			String txm = rowInfo.get("txm");
			String temp = null;
			String length = dataSetProvider.getSequence(PRODUCT_LEN_SQL);
			product_len = Integer.parseInt(length);
			if ((temp = checkInt(self_Code, "产品编码")) != null) {
				data.setMessage(temp);
				return;
			}
			if (self_Code.length() < product_len) {
				data.setMessage(showJavaScript("alert('物资编码位数错误！');"));
				return;
			}
			if (pm.equals("")) {
				data.setMessage(showJavaScript("alert('物资名称不能为空！');"));
				return;
			}

			if (rowInfo.get("wzlbid").equals("")) {
				data.setMessage(showJavaScript("alert('物资类别不能为空！');"));
				return;
			}
			if (rowInfo.get("jjff").equals("")) {
				data.setMessage(showJavaScript("alert('计价方法不能为空！');"));
				return;
			}
			/**
			 * if((temp = checkNumber(hsbl,"换算比例")) !=null) {
			 * data.setMessage(temp); return; }
			 */
			if (chlbid.equals("")) {
				data.setMessage(showJavaScript("alert('存货类别不能为空！');"));
				return;
			}
			/**
			 * double hsblVal = hsbl.length()>0 ? Double.parseDouble(hsbl) : 0 ;
			 * if(hsblVal<=0) {
			 * data.setMessage(showJavaScript("alert('换算比例必须大于零！');")); return;
			 * }
			 * 
			 * if(scdwgs.length()>0 && (temp = checkNumber(scdwgs,
			 * "生产公式"))!=null) { data.setMessage(temp); return; }
			 */
			if (minsl.length() > 0 && (temp = checkNumber(minsl, "库存下限")) != null) {
				data.setMessage(temp);
				return;
			}
			if (maxsl.length() > 0 && (temp = checkNumber(maxsl, "库存上限")) != null) {
				data.setMessage(temp);
				return;
			}
			String cpbm = parentCode + self_Code;
			boolean isCheckCode = !cpbm.equals(dsProduct.getValue("cpbm"));
			boolean isCheckBarCode = (txm.length() > 0 && !txm.equals(dsProduct.getValue("txm")));
			if (isAdd || isCheckCode || isCheckBarCode) {
				String count = !isCheckCode ? "0" : dataSetProvider
						.getSequence("SELECT COUNT(*) FROM kc_dm WHERE cpbm='" + cpbm + "' AND isdelete=0");
				if (!count.equals("0")) {
					data.setMessage(showJavaScript("alert('产品编码(" + cpbm + ")已经存在!');"));
					return;
				}
				//
				count = !isCheckBarCode ? "0" : dataSetProvider.getSequence("SELECT COUNT(*) FROM kc_dm WHERE txm='"
						+ txm + "' AND isdelete=0");
				if (!count.equals("0")) {
					data.setMessage(showJavaScript("alert('条形码(" + txm + ")已经存在!');"));
					return;
				}
				count = dataSetProvider.getSequence("SELECT COUNT(*) FROM kc_dm where pm='" + pm + "' and gg='" + gg
						+ "' and storeid='" + storeid + "' and isdelete=0");
				if (!count.equals("0")) {
					data.setMessage(showJavaScript("alert('已有该品名,规格的物资存放在该仓库')"));
					return;
				}
			} else {
				String counter = dataSetProvider.getSequence("SELECT COUNT(*) FROM kc_dm where pm='" + pm
						+ "' and gg='" + gg + "' and storeid='" + storeid + "' and cpid<>'" + cpids
						+ "' and isdelete=0");
				if (!counter.equals("0")) {
					data.setMessage(showJavaScript("alert('已有该品名,规格的物资存放在该仓库')"));
					return;
				}
			}
			boolean isChange = false;
			String cpid = null;
			String SQL = null;
			// 保存员工数据
			if (isAdd) {
				ds.insertRow(false);
				ds.setValue("cpid", "-1");
				ds.setValue("isdelete", "0");
			} else {
				ds.goToInternalRow(editrow);
				cpid = ds.getValue("cpid");
				SQL = combineSQL(UPDATE_SQL, "@", new String[] { cpid });
				if (!tqq.equals(ds.getValue("tqq")))
					isChange = true;
			}
			ds.setValue("cpbm", cpbm);
			ds.setValue("pm", pm);
			ds.setValue("zjm", rowInfo.get("zjm"));
			ds.setValue("gg", rowInfo.get("gg"));
			ds.setValue("chlbid", rowInfo.get("chlbid"));
			ds.setValue("wzlbid", rowInfo.get("wzlbid"));
			ds.setValue("th", rowInfo.get("th"));
			ds.setValue("abc", rowInfo.get("abc"));
			ds.setValue("txm", rowInfo.get("txm"));
			ds.setValue("jldw", rowInfo.get("jldw"));
			ds.setValue("hsdw", rowInfo.get("hsdw"));
			ds.setValue("hsbl", rowInfo.get("hsbl"));
			ds.setValue("jhdj", rowInfo.get("jhdj"));
			ds.setValue("chxz", rowInfo.get("chxz"));
			ds.setValue("tqq", rowInfo.get("tqq"));
			ds.setValue("deptid", rowInfo.get("deptid"));
			ds.setValue("bz", rowInfo.get("bz"));
			ds.setValue("isprops", rowInfo.get("isprops"));
			ds.setValue("issale", rowInfo.get("issale"));
			ds.setValue("jjff", rowInfo.get("jjff"));
			ds.setValue("maxsl", maxsl);
			ds.setValue("minsl", minsl);
			ds.setValue("xgr", loginName);
			ds.setValue("isbatchno", isbatchno.equals("1") ? isbatchno : "0");
			ds.setValue("storeid", rowInfo.get("storeid"));
			// --2004.6.19 修改生产用单位和生产单位公式可为空
			ds.setValue("scydw", scydw);
			ds.setValue("scdwgs", scdwgs);
			if (photo != null) {
				// ds.setInputStream("photo",null);
				InputStream oin = ds.getInputStream("photo");
				int avl = oin.available();
				byte[] fbs = getBytes(photo);
				if (avl > 0) {
//					oin = new ByteArrayInputStream(fbs, 0, fbs.length);
					ds.setInputStream("photo", null);
				} else {
					ds.setInputStream("photo", new ByteArrayInputStream(fbs, 0, fbs.length));
				}
			}
			// ds.setValue("scydw", scydw.equals("") ? rowInfo.get("jldw") :
			// scydw);//生产用单位
			// ds.setValue("scdwgs", scdwgs.equals("") ? "1" : scdwgs);//生产单位公式
			ds.setValue("productProp", rowInfo.get("productProp"));
			if (CUST_ESSEN.equals(systemCustName)) {
				ds.setValue("sjbcps", rowInfo.get("sjbcps"));
				ds.setValue("sjlbhl", rowInfo.get("sjlbhl"));
				ds.setValue("sjlbsf", rowInfo.get("sjlbsf"));
			} else if (CUST_SHENGYU.equals(systemCustName)) {
				ds.setValue("hh", rowInfo.get("hh"));// 花号
				ds.setValue("ks", rowInfo.get("ks"));// 款式
				ds.setValue("zxbz", rowInfo.get("zxbz"));
			}
			String CZRQ = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
			// ds.setValue("CZRQ", CZRQ);

			ds.post();
			ds.saveChanges(new String[] { SQL });
			data.setMessage(showJavaScript("parent.hideInterFrame();"));
		}
	}

	/**
	 * 删除操作的触发类
	 */
	class Delete implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			dsProduct.goToRow(Integer.parseInt(data.getParameter("rownum")));
			dsProduct.setValue("isdelete", "1");
			dsProduct.setValue("xgr", loginName);
			String CZRQ = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
			// dsProduct.setValue("CZRQ", CZRQ);
			dsProduct.post();
			dsProduct.saveChanges();
			dsProduct.deleteRow();
			dsProduct.resetPendingStatus(true);
		}
	}

	/**
	 * 初始化列信息
	 * 
	 * @param isAdd
	 *            是否是添加
	 * @param isInit
	 *            是否重新初始化行信息
	 * @throws Exception
	 *             异常
	 */
	private void initRowInfo(boolean isAdd, boolean isInit) throws Exception {
		if (!dsProduct.isOpen())
			dsProduct.open();
		if (isInit || rowInfo.size() > 0) {
			rowInfo.clear();
			rowInfo.put("hsbl", "1");
			rowInfo.put("scdwgs", "1");
		}
		if (!isAdd) {
			rowInfo.put(dsProduct);
			String parentCode = dsProduct.getValue("cpbm");
			int leng = parentCode.length() - product_len;
			rowInfo.put("parentCode", parentCode.substring(0, leng));
			rowInfo.put("self_code", parentCode.substring(leng));
		}
		/*
		 * else { String code = dataSetProvider.getSequence(
		 * "SELECT pck_base.fieldNextCode('kc_dm','cpbm','','isdelete=0',"
		 * +product_len+") from dual"); rowInfo.put("cpbm", code); }
		 */
	}

	class OnChange implements Obactioner {
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			rowInfo.put(data.getRequest());
			String wzlbid = rowInfo.get("wzlbid");
			LookUp lookupBean = LookupBeanFacade.getInstance(data.getRequest(), SysConstant.BEAN_PRODUCT_KIND);
			String prefix = lookupBean.getLookupRow(wzlbid).get("bm");
			String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('kc_dm','cpbm','" + prefix
					+ "','isdelete=0'," + product_len + ",0) from dual");
			rowInfo.put("parentCode", prefix);
			rowInfo.put("self_code", code);
		}
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
}

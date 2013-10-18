package engine.project;

import java.util.Map;
import java.util.Hashtable;

/**
 * 2004.11.1 jac 增加客户应收款, 相关视图VW_CUST_ACCOUNT_RECEIVABLE
 * 2004.11.4 jac 1.工艺名称按编号升序顺序
 *               2.添加根据分公司ID, 仓库ID, 产品ID得到库存数量。
 */
/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: ENGINE
 * </p>
 * 
 * @author 江海岛
 * @version 1.0
 */
public final class LookupHelperPoolImpl extends LookupHelperPool implements SysConstant, java.io.Serializable {
	private Map hashSql = null;

	public LookupHelperPoolImpl() {
		hashSql = new Hashtable(100, 1);
		// -----------------------------------------------------------------------
		// --基础
		// -----------------------------------------------------------------------
		// 仓库
		hashSql.put(BEAN_STORE, new LookupHelper("SELECT * FROM VW_BEAN_STORE", null, "storeid", "ORDER BY ckbm", new String[] { "ckmc" }, true, new String[] { "storeid" }));
		// 仓库库位JAVABEAN
		hashSql.put(BEAN_STORE_AREA, new LookupHelper("SELECT * FROM VW_BEAN_STORE_AREA", null, "kwid", "ORDER BY ckmc, dm", new String[] { "mc" }, true));
		// 存货信息JAVABEAN
		hashSql.put(BEAN_PRODUCT, new LookupHelper("SELECT * FROM VW_BEAN_PRODUCT", null, "cpid", "", new String[] { "product" }));
		// 存货信息（通过产品编码得到产品id）: kc_dm.code
		hashSql.put(BEAN_PRODUCT_CODE, new LookupHelper("SELECT cpid, cpbm, storeid FROM VW_BEAN_PRODUCT WHERE isdelete=0", null, "cpbm", "", new String[] { "cpid" }));
		// 国家JAVABEAN
		hashSql.put(BEAN_COUNTRY, new LookupHelper("SELECT * FROM country", null, "cdm", "ORDER BY countrycode", new String[] { "mc" }, true));
		// 地区JAVABEAN
		hashSql.put(BEAN_AREA, new LookupHelper("SELECT * FROM dwdq", null, "dqh", "ORDER BY areacode", new String[] { "dqmc" }, true));
		// 地区JAVABEAN
		hashSql.put(BEAN_AREA_CODE, new LookupHelper("SELECT dqh, areacode FROM dwdq", null, "dqh", "ORDER BY areacode", new String[] { "areacode" }, true));
		// 外来单位JAVABEAN
		hashSql.put(BEAN_CORP, new LookupHelper("SELECT * FROM VW_BEAN_CORP", null, "dwtxid", null, new String[] { "dwmc" },true));
		// 外来单位信誉度JAVABEAN
		hashSql.put(BEAN_CORP_CREDIT, new LookupHelper("SELECT * FROM VW_BEAN_CORP_CREDIT", new String[] { "fgsid" }, "dwtxid", null, new String[] { "dwmc" }));
		// 车间信息JAVABEAN
		hashSql.put(BEAN_WORKSHOP, new LookupHelper("SELECT deptid, dm, parentdeptid, mc, isdelete FROM bm WHERE iswork=1 AND deptid>0", null, "deptid", "ORDER BY dm",
				new String[] { "mc" }, true, new String[] { "isdelete", "deptid" }));
		// 所有部门列表信息JAVABEAN
		hashSql.put(BEAN_DEPT_LIST, new LookupHelper("SELECT deptid, dm, parentdeptid, mc FROM bm WHERE deptid>0 AND isdelete=0", null, "deptid", "ORDER BY dm",
				new String[] { "mc" }, true));
		// 部门信息JAVABEAN
		hashSql.put(BEAN_DEPT, new LookupHelper("SELECT deptid, dm, parentdeptid, mc, isdelete FROM bm WHERE deptid>0", null, "deptid", "ORDER BY dm", new String[] { "mc" }, true,
				new String[] { "isdelete", "deptid" }));// ,
														// true,
														// "dm"));
		// 人员信息JAVABEAN
		hashSql.put(BEAN_PERSON, new LookupHelper("SELECT * FROM VW_BEAN_PERSON", null, "personid", "ORDER BY dm, bm", new String[] { "xm" }, true, new String[] { "isshow",
				"deptid" }));
		// 人员信息-职务JAVABEAN
		hashSql.put(BEAN_PERSON_DUTY, new LookupHelper("SELECT * FROM rl_ygfzxx WHERE lx=1", null, "mc", "ORDER BY dm", new String[] { "mc" }, true));// ygfzxxid
		/**
		 * 人员信息-人员类别 JAVABEAN
		 */
		hashSql.put(BEAN_PERSON_CLASS, new LookupHelper("SELECT * FROM rl_ygfzxx WHERE lx=2", null, "mc", "ORDER BY dm", new String[] { "mc" }, true));
		/**
		 * 人员信息-学历JAVABEAN
		 */
		hashSql.put(BEAN_PERSON_EDUCATION, new LookupHelper("SELECT * FROM rl_ygfzxx WHERE lx=3", null, "mc", "ORDER BY dm", new String[] { "mc" }, true));
		/**
		 * 人员信息-民族JAVABEAN
		 */
		hashSql.put(BEAN_PERSON_NATION, new LookupHelper("SELECT * FROM rl_ygfzxx WHERE lx=4", null, "mc", "ORDER BY dm", new String[] { "mc" }, true));
		/**
		 * 人员信息-籍贯JAVABEAN
		 */
		hashSql.put(BEAN_PERSON_NATIVE, new LookupHelper("SELECT * FROM rl_ygfzxx WHERE lx=5", null, "mc", "ORDER BY dm", new String[] { "mc" }, true));
		/**
		 * 人员信息-职称JAVABEAN
		 */
		hashSql.put(BEAN_PERSON_TECH, new LookupHelper("SELECT * FROM rl_ygfzxx WHERE lx=6", null, "mc", "ORDER BY dm", new String[] { "mc" }, true));
		/**
		 * 人员信息-政治面貌JAVABEAN
		 */
		hashSql.put(BEAN_PERSON_POLITY, new LookupHelper("SELECT * FROM rl_ygfzxx WHERE lx=7", null, "mc", "ORDER BY dm", new String[] { "mc" }, true));
		/**
		 * 产品大类JAVABEAN
		 */
		hashSql.put(BEAN_PRODUCT_FIRST_KIND, new LookupHelper("SELECT wzlbid, bm, mc FROM kc_dmlb WHERE parentid=0 AND isdelete=0", null, "wzlbid", "ORDER BY bm",
				new String[] { "mc" }, true));

		/**
		 * 产品大类用编码表示keyJAVABEAN
		 */
		hashSql.put(BEAN_PRODUCT_FIRST_KIND_CODE, new LookupHelper("SELECT bm, mc FROM kc_dmlb WHERE parentid=0 AND isdelete=0", null, "bm", "ORDER BY bm", new String[] { "mc" },
				true));

		/**
		 * 产品类别JAVABEAN
		 */
		hashSql.put(BEAN_PRODUCT_KIND, new LookupHelper("SELECT wzlbid, bm, parentid, mc FROM kc_dmlb WHERE isdelete=0", null, "wzlbid", "ORDER BY bm", new String[] { "mc" },
				true, true, "bm"));
		/**
		 * 存货类别JAVABEAN
		 */
		hashSql.put(BEAN_STOCKS_KIND, new LookupHelper("SELECT chlbid, chmc, dygs, ckdgs FROM kc_chlb", null, "chlbid", "ORDER BY pxh", new String[] { "chmc" }, true));
		/**
		 * 结算方式JAVABEAN
		 */
		hashSql.put(BEAN_BALANCE_MODE, new LookupHelper("SELECT jsfsid, jsfs, kmdm FROM jsfs", null, "jsfsid", "ORDER BY dm", new String[] { "jsfs" }, true));
		/**
		 * 发货方式JAVABEAN
		 */
		hashSql.put(BEAN_SEND_MODE, new LookupHelper("SELECT * FROM jc_sendmode", null, "sendmodeid", "ORDER BY sendmodecode", new String[] { "sendmode" }, true));
		/**
		 * 合同类型JAVABEAN
		 */
		hashSql.put(BEAN_ORDER_TYPE, new LookupHelper("SELECT * FROM jc_ordertype", null, "ordertypeid", "ORDER BY ordertypecode", new String[] { "ordertype" }, true));
		/**
		 * 单据类型设置JAVABEAN
		 */
		hashSql.put(BEAN_FUND_TYPE, new LookupHelper("SELECT * FROM xs_fund_item", null, "funditemid", "ORDER BY funditemcode", new String[] { "funditemname" }, true));

		// -----------------------------------------------------------------------
		// --财务
		// -----------------------------------------------------------------------
		// 外币信息JAVABEAN
		hashSql.put(BEAN_FOREIGN_CURRENCY, new LookupHelper("SELECT * FROM wb", null, "wbid", "ORDER BY dm", new String[] { "mc" }, true));
		// 发票类别JAVABEAN
		hashSql.put(BEAN_BUY_INVOICE_TYPE, new LookupHelper("SELECT * FROM jc_fplb WHERE sylx=1", null, "fplbid", "ORDER BY pxh", new String[] { "mc" }, true));
		// 发票类别JAVABEAN
		hashSql.put(BEAN_SALE_INVOICE_TYPE, new LookupHelper("SELECT * FROM jc_fplb WHERE sylx=2", null, "fplbid", "ORDER BY pxh", new String[] { "mc" }, true));
		// 银行JAVABEAN
		hashSql.put(BEAN_BANK, new LookupHelper("SELECT * FROM yh", null, "yhid", "ORDER BY dm", new String[] { "yhmc" }, true));
		// 银行帐号JAVABEAN
		hashSql.put(BEAN_BANK_ACCOUNT, new LookupHelper("SELECT * FROM VW_BEAN_BANK_ACCOUNT", new String[] { "fgsid" }, "yhzhid", "ORDER BY dm", new String[] { "zh" }, true));
		//
		// ------------------------------------
		// 固定资产
		// ------------------------------------
		/**
		 * 固定资产类别: cw_capital_type
		 */
		hashSql.put(BEAN_CAPITAL_TYPE, new LookupHelper("SELECT * FROM cw_capital_type", null, "capi_type_id", "ORDER BY capi_type_no", new String[] { "capi_type_name" }, true));
		// public static final String BEAN_CAPITAL_TYPE = "cw_capital_type";

		/**
		 * 固定资产类别编码: cw_capital_type.code
		 */
		hashSql.put(BEAN_CAPITAL_TYPE_CODE, new LookupHelper("SELECT * FROM cw_capital_type", null, "capi_type_id", "ORDER BY capi_type_no", new String[] { "capi_type_no" }, true));

		// -----------------------------------------------------------------------
		// --销售
		// -----------------------------------------------------------------------
		// 销售物资单价JAVABEAN
		hashSql.put(BEAN_SALE_PRICE, new LookupHelper("SELECT * FROM VW_BEAN_SALE_PRICE", null, "wzdjid", null, new String[] { "cpid" },true));

		// 外贸物资单价JAVABEAN
		hashSql.put(BEAN_XPORT_PRICE, new LookupHelper("SELECT * FROM VW_BEAN_XPORT_PRICE", null, "xpwzdjid", null, new String[] { "cpid" }));

		// 销售物资单价（根据cpid得到）JAVABEAN
		hashSql.put(BEAN_PROD_PRICE, new LookupHelper("SELECT * FROM VW_XS_WZDJ", null, "cpid", null, new String[] { "product" }));
		/**
		 * 销售合同JAVABEAN
		 */
		hashSql.put(BEAN_SALE_ORDER, new LookupHelper("SELECT htid, htbh FROM xs_ht", null, "htid", null, new String[] { "htbh" }));
		/**
		 * 销售纸箱单JAVABEAN
		 */
		hashSql.put(BEAN_BOX_MX, new LookupHelper("SELECT * FROM xs_paperbox_mx", null, "hthwid", null, new String[] { "pcs" }));
		/**
		 * 外贸订单JAVABEAN
		 */
		hashSql.put(BEAN_XPORT_ORDER, new LookupHelper("SELECT htid, htbh FROM xp_ht", null, "htid", null, new String[] { "htbh" }));
		/**
		 * 销售合同货物
		 */
		hashSql.put(BEAN_SALE_ORDER_GOODS, new LookupHelper("SELECT * FROM VW_BEAN_SALE_ORDER_GOODS", null, "hthwid", null, new String[] { "htbh" }));
		/**
		 * 外贸订单货物
		 */
		hashSql.put(BEAN_XPORT_ORDER_GOODS, new LookupHelper("SELECT * FROM VW_BEAN_XPORT_ORDER_GOODS", null, "hthwid", null, new String[] { "htbh" }));
		/**
		 * 销售提单货物
		 */
		hashSql.put(BEAN_SALE_LADING_BILL, new LookupHelper("SELECT * FROM VW_BEAN_SALE_LADING_BILL", null, "tdhwid", null, new String[] { "tdbh" }));
		/**
		 * 外贸提单货物
		 */
		hashSql.put(BEAN_XPORT_LADING_BILL, new LookupHelper("SELECT * FROM VW_BEAN_XPORT_LADING_BILL", null, "tdhwid", null, new String[] { "tdbh" }));
		/**
		 * 销售提单
		 */
		hashSql.put(BEAN_SALE_LADING, new LookupHelper("SELECT * FROM VW_BEAN_SALE_LADING", null, "tdid", null, new String[] { "tdbh" }));
		/**
		 * 外贸提单
		 */
		hashSql.put(BEAN_XPORT_LADING_BILL, new LookupHelper("SELECT * FROM VW_BEAN_XPORT_LADING", null, "tdid", null, new String[] { "tdbh" }));
		/**
		 * 销售分栏帐
		 */
		hashSql.put(BEAN_SALE_COLUMN_BREAK, new LookupHelper("SELECT flzid, mc FROM xs_flz", null, "flzid", null, new String[] { "mc" }, true));
		/**
		 * 销售提单承运单位明细
		 */
		hashSql.put(BEAN_SALE_CARRY_THING, new LookupHelper("SELECT * FROM VW_BEAN_SALE_CARRY", null, "tdcyqkID", null, new String[] { "fy" }, true));

		/**
		 * 销售地区价格JAVABEAN
		 */
		hashSql.put(BEAN_AREA_PRICE, new LookupHelper("SELECT * FROM XS_AREA_PRICE", null, "area_price_id", "ORDER BY area_code", new String[] { "area_name" }, true));

		/**
		 * 销售驾驶员：xs_driver
		 */
		hashSql.put(BEAN_SALE_DRIVER, new LookupHelper("SELECT * FROM xs_driver", null, "driver_id", "ORDER BY driver_code", new String[] { "driver_name" }, true));
		/**
		 * 地区车号: xs_car
		 */
		hashSql.put(BEAN_SALE_CAR, new LookupHelper("SELECT car_id, car_no, (car_no||' '||car_stand) car FROM xs_car", null, "car_id", "ORDER BY car_no", new String[] { "car" },
				true));

		/**
		 * 地区车型: xs_area_car
		 */
		hashSql.put(BEAN_AREA_CAR, new LookupHelper("SELECT * FROM (SELECT (a.area_price_id||'_'||a.car_id) id, a.cal_stand, b.area_code "
				+ "FROM xs_area_car a, xs_area_price b WHERE a.area_price_id=b.area_price_id) WHERE 1=1", null, "id", "ORDER BY area_code", new String[] { "area_name" }, false));

		/**
		 * 客户信誉等级cust_grade
		 */
		hashSql.put(BEAN_CUST_GRADE, new LookupHelper("SELECT DISTINCT t.xydj FROM xs_khxyed t WHERE t.xydj IS NOT NULL", null, "xydj", "", new String[] { "xydj" }, true));

		/**
		 * 客户应收款
		 */
		hashSql.put(BEAN_CUST_ACCOUNT_RECEIVABLE, new LookupHelper("SELECT * FROM VW_CUST_ACCOUNT_RECEIVABLE", null, new String[] { "fgsid", "dwtxid" }, "",
				new String[] { "account" }, false, false, null, null));

		/**
		 * 客户历史记录 hashSql.put(BEAN_SALE_CUST_PROD, new
		 * LookupHelper("SELECT * FROM VW_BEAN_SALE_CUST_PROD", null, "wzdjid",
		 * null, new String[]{"wzdjid"}));
		 */
		// -----------------------------------------------------------------------
		// --生产
		// -----------------------------------------------------------------------
		/**
		 * 物资属性提前期
		 */
		hashSql.put(BEAN_PROD_AHAED_TIME, new LookupHelper("SELECT (cpid||'_'||dmsxid) propid, ahead_time, tot_time FROM sc_prod_time", null, "propid", "",
				new String[] { "ahead_time" }));
		// public static final String BEAN_PROD_AHAED_TIME = "sc_prod_time";
		/**
		 * 工艺路线类型JAVABEAN
		 */
		hashSql.put(BEAN_TECHNICS_ROUTE_TYPE, new LookupHelper("SELECT gylxlxid, gylxlxbh, gylxlxmc FROM sc_gylxlx", null, "gylxlxid", "ORDER BY gylxlxbh",
				new String[] { "gylxlxmc" }, true));
		/**
		 * 工艺名称JAVABEAN
		 */
		hashSql.put(BEAN_TECHNICS_NAME, new LookupHelper("SELECT gymcid, gybh, gymc, gxfdid FROM sc_gymc", null, "gymcid", "ORDER BY gybh", new String[] { "gymc" }, true));
		/**
		 * 工序分段JAVABEAN
		 */
		hashSql.put(BEAN_WORK_PROCEDURE, new LookupHelper("SELECT gxfdid, gdbh, gdmc, deptid FROM sc_gxfd", null, "gxfdid", "ORDER BY gdbh", new String[] { "gdmc" }, true));
		/**
		 * 工作中心JAVABEAN
		 */
		hashSql.put(BEAN_WORK_CENTER, new LookupHelper("SELECT gzzxid, gzzxbh, gzzxmc FROM sc_gzzx", null, "gzzxid", "ORDER BY gzzxbh", new String[] { "gzzxmc" }, true));

		/**
		 * 工作组JAVABEAN
		 */
		hashSql.put(BEAN_WORK_GROUP, new LookupHelper("SELECT gzzid, gzzbh, gzzmc, deptid FROM sc_gzz", null, "gzzid", "ORDER BY gzzbh", new String[] { "gzzmc" }, true));
		/**
		 * 工艺路线JAVABEAN
		 */
		hashSql.put(BEAN_TECHNICS_ROUTE, new LookupHelper("SELECT * FROM VW_BEAN_TECHNICS_ROUTE", null, "gylxid", null, new String[] { "gylxlxmc" }, false));

		/**
		 * 工序（工艺路线明细）JAVABEAN
		 */
		hashSql.put(
				BEAN_TECHNICS_PROCEDURE,
				new LookupHelper(
						"SELECT * FROM (SELECT a.gylxmxid, a.gylxid, a.gxfdid, b.gymc, a.desl, a.deje, a.sfwx, a.wxjg, a.jjff FROM sc_gylxmx a, sc_gymc b WHERE a.gymcID=b.gymcID) WHERE 1=1",
						null, "gylxmxid", null, new String[] { "gymc" }, false));

		/**
		 * 生产计划
		 */
		hashSql.put(BEAN_PRODUCE_PLAN, new LookupHelper("SELECT scjhid, jhh, zsl FROM sc_jh", null, "scjhid", null, new String[] { "jhh" }, false));

		/**
		 * 物料需求明细
		 */
		hashSql.put(BEAN_MRP_GOODS, new LookupHelper("SELECT * FROM VW_BEAN_MRP_GOODS", null, "wlxqjhmxid", null, new String[] { "wlxqh" }, false));

		/**
		 * 任务单明细
		 */
		hashSql.put(BEAN_PRODUCE_TASK_GOODS, new LookupHelper("SELECT * FROM VW_BEAN_PRODUCE_TASK_GOODS", null, "rwdmxid", null, new String[] { "rwdh" }, false));

		/**
		 * 生产加工单
		 */
		hashSql.put(BEAN_PRODUCE_PROCESS, new LookupHelper("SELECT jgdid, jgdh, deptid, zsl FROM sc_jgd", null, "jgdid", null, new String[] { "jgdh" }, false));

		/**
		 * 生产加工单明细
		 */
		hashSql.put(BEAN_PRODUCE_PROCESS_GOODS, new LookupHelper("SELECT * FROM VW_BEAN_PRODUCE_PROCESS_GOODS", null, "jgdmxid", null, new String[] { "jgdh" }, false));

		/**
		 * 计划可供量: sc_jhkgl
		 */
		hashSql.put(BEAN_PALN_USABLE_NUMBER, new LookupHelper("SELECT * FROM sc_jhkgl", null, new String[] { "cpid", "dmsxid" }, null, new String[] { "jhkgl" }, false, false,
				null, null));

		/**
		 * 生产跟踪单类型: sc_track_type
		 */
		hashSql.put(BEAN_PRODUCE_TRACK_TYPE, new LookupHelper("SELECT track_type_id, type_name, type_prop FROM sc_track_type", null, "track_type_id", "ORDER BY type_code",
				new String[] { "type_name" }, true));

		/**
		 * 车间流转单明细: sc_cjlzdmx
		 */
		hashSql.put(BEAN_SHOP_FLOW_DETALL, new LookupHelper("SELECT * FROM (SELECT b.cjlzdmxid, a.cjlzdid, a.cjlzdh "
				+ "FROM sc_cjlzd a, sc_cjlzdmx b WHERE a.cjlzdid=b.cjlzdid) WHERE 1=1", null, "cjlzdmxid", "", new String[] { "cjlzdh" }));
		// -----------------------------------------------------------------------
		// --人力
		// -----------------------------------------------------------------------
		/**
		 * 银行信用卡JAVABEAN
		 */
		hashSql.put(BEAN_BANK_CREDIT_CARD, new LookupHelper("SELECT xykid, xykbh, xykmc, xykhcd FROM rl_yhxyk", null, "xykid", "ORDER BY xykbh", new String[] { "xykmc" }, true));

		// -----------------------------------------------------------------------
		// --采购
		// -----------------------------------------------------------------------
		/**
		 * 采购报价
		 */
		hashSql.put(BEAN_BUY_PRICE, new LookupHelper("SELECT * FROM VW_BEAN_BUY_PRICE", null, "cgbjid", null, new String[] { "bj" }, false));
		/**
		 * 采购申请单货物
		 */
		hashSql.put(BEAN_BUY_APPLY_GOODS, new LookupHelper("SELECT * FROM VW_BEAN_BUY_APPLY_GOODS", null, "cgsqdhwid", null, new String[] { "sqbh" }, false));

		/**
		 * 采购合同货物
		 */
		hashSql.put(BEAN_BUY_ORDER_GOODS, new LookupHelper("SELECT * FROM VW_BEAN_BUY_ORDER_GOODS", null, "hthwid", null, new String[] { "htbh" }, false));
		/**
		 * 进货单货物 JAVABEAN
		 */
		hashSql.put(BEAN_BUY_ORDER_STOCK, new LookupHelper("SELECT * FROM VW_BEAN_BUY_ORDER_STOCK", null, "jhdhwid", null, new String[] { "jhdbm" }, false));
		// -----------------------------------------------------------------------
		// --库存
		// -----------------------------------------------------------------------
		// 1=合同入库单,2=销售出库单,3=自制入库单,4=生产领料单,5=外加工入库单,6=外加工发料单,7=报损单,8=移库单
		/**
		 * 收发单据类别(全部) 用于列表显示
		 */
		hashSql.put(BEAN_IN_OUT_BILL, new LookupHelper("SELECT sfdjlbid, lbmc, srlx FROM kc_sfdjlb", null, "sfdjlbid", null, new String[] { "lbmc" }, true));

		/**
		 * 收发单据类别-合同入库单 JAVABEAN
		 */

		hashSql.put(BEAN_STORE_SALE_IN, new LookupHelper("SELECT sfdjlbid, lbmc, srlx FROM kc_sfdjlb WHERE djxz=1", null, "sfdjlbid", "ORDER BY lbbm", new String[] { "lbmc" },
				true));

		/**
		 * 收发单据类别-合同出库单 JAVABEAN
		 */
		hashSql.put(BEAN_STORE_SALE_OUT, new LookupHelper("SELECT sfdjlbid, lbmc, srlx FROM kc_sfdjlb WHERE djxz=2", null, "sfdjlbid", "ORDER BY lbbm", new String[] { "lbmc" },
				true));

		/**
		 * 收发单据类别-自制入库单 JAVABEAN
		 */
		hashSql.put(BEAN_STORE_PRODUCE_IN, new LookupHelper("SELECT sfdjlbid, lbmc, srlx FROM kc_sfdjlb WHERE djxz=3", null, "sfdjlbid", "ORDER BY lbbm", new String[] { "lbmc" },
				true));

		/**
		 * 收发单据类别-生产领料单 JAVABEAN
		 */
		hashSql.put(BEAN_STORE_PRODUCE_OUT, new LookupHelper("SELECT sfdjlbid, lbmc, srlx FROM kc_sfdjlb WHERE djxz=4", null, "sfdjlbid", "ORDER BY lbbm", new String[] { "lbmc" },
				true));

		/**
		 * 收发单据类别-外加工入库单 JAVABEAN
		 */
		hashSql.put(BEAN_STORE_PROCESS_IN, new LookupHelper("SELECT sfdjlbid, lbmc, srlx FROM kc_sfdjlb WHERE djxz=5", null, "sfdjlbid", "ORDER BY lbbm", new String[] { "lbmc" },
				true));

		/**
		 * 收发单据类别-外加工发料单 JAVABEAN
		 */
		hashSql.put(BEAN_STORE_PROCESS_OUT, new LookupHelper("SELECT sfdjlbid, lbmc, srlx FROM kc_sfdjlb WHERE djxz=6", null, "sfdjlbid", "ORDER BY lbbm", new String[] { "lbmc" },
				true));

		/**
		 * 收发单据类别-报损单 JAVABEAN
		 */
		hashSql.put(BEAN_STORE_LOSS, new LookupHelper("SELECT sfdjlbid, lbmc, srlx FROM kc_sfdjlb WHERE djxz=7", null, "sfdjlbid", "ORDER BY lbbm", new String[] { "lbmc" }, true));

		/**
		 * 收发单据类别-移库单 JAVABEAN
		 */
		hashSql.put(BEAN_STORE_MOVE, new LookupHelper("SELECT sfdjlbid, lbmc, srlx FROM kc_sfdjlb WHERE djxz=8", null, "sfdjlbid", "ORDER BY lbbm", new String[] { "lbmc" }, true));

		/**
		 * 收发单据类别-其他入库单 JAVABEAN
		 */
		hashSql.put(BEAN_STORE_OTHER_IN, new LookupHelper("SELECT sfdjlbid, lbmc, srlx FROM kc_sfdjlb WHERE djxz=9", null, "sfdjlbid", "ORDER BY lbbm", new String[] { "lbmc" },
				true));

		/*
		 * 收发单据类别-其他出库单: kc_sfdjlb.10
		 */
		hashSql.put(BEAN_STORE_OTHER_OUT, new LookupHelper("SELECT sfdjlbid, lbmc, srlx FROM kc_sfdjlb WHERE djxz=10", null, "sfdjlbid", "ORDER BY lbbm", new String[] { "lbmc" },
				true));

		/*
		 * 收发单据类别-其他出库单: kc_sfdjlb.10
		 */
		hashSql.put(BEAN_STORE_SALEMOVE, new LookupHelper("SELECT sfdjlbid, lbmc, srlx FROM kc_sfdjlb WHERE djxz=11", null, "sfdjlbid", "ORDER BY lbbm", new String[] { "lbmc" },
				true));

		/**
		 * 收发单据明细: kc_sfdjmx
		 */
		hashSql.put(BEAN_STORE_BILL_DETAIL, new LookupHelper("SELECT a.sfdjid, a.storeid, a.sfdjdh, b.rkdmxid FROM kc_sfdj a, kc_sfdjmx b WHERE a.sfdjid=b.sfdjid", null,
				"rkdmxid", "", new String[] { "sfdjdh" }));

		/**
		 * 生产单据用途 JAVABEAN
		 */
		hashSql.put(BEAN_PRODUCE_USE, new LookupHelper("SELECT ytid, ytbh, ytmc FROM kc_csdjyt", null, "ytid", "ORDER BY ytbh", new String[] { "ytmc" }, true));

		/**
		 * 物资规格属性 JAVABEAN
		 */
		hashSql.put(BEAN_SPEC_PROPERTY, new LookupHelper("SELECT dmsxid, cpid, sxz FROM kc_dmsx", null, "dmsxid", null, new String[] { "sxz" }));

		/**
		 * 库存汇总信息JAVABEAN
		 */
		hashSql.put(BEAN_PRODUCT_STOCK, new LookupHelper("SELECT * FROM VW_BEAN_PRODUCT_STOCK", null, "cpid", "", new String[] { "product" }));

		/**
		 * 产品库存批号JAVABEAN
		 */
		hashSql.put(BEAN_PRODUCT_BATCH, new LookupHelper("SELECT wzmxid, cpid, storeid, kwid, dmsxid, ph, zl FROM kc_wzmx", null, "wzmxid", "", new String[] { "ph" }));

		/**
		 * 盘点单: kc_pd
		 */
		hashSql.put(BEAN_STORE_CHECK, new LookupHelper("SELECT pdid, pdhm FROM kc_pd", null, "pdid", "", new String[] { "pdhm" }));

		/**
		 * 生产领料单明细: sc_drawmaterialdetail
		 */
		hashSql.put(BEAN_DRAW_MATERIAL_DETALL, new LookupHelper("SELECT * FROM (SELECT b.drawdetailid, a.drawid, a.drawcode "
				+ "FROM sc_drawmaterial a, sc_drawmaterialdetail b WHERE a.drawid=b.drawid) WHERE 1=1", null, "drawdetailid", "", new String[] { "drawcode" }));

		/**
		 * 自制收获单明细: sc_receiveproddetail
		 */
		hashSql.put(BEAN_RECEIVE_MATERIAL_DETALL, new LookupHelper("SELECT * FROM (SELECT b.receivedetailid, a.receiveid, a.receivecode "
				+ "FROM sc_receiveprod a, sc_receiveproddetail b WHERE a.receiveid=b.receiveid) WHERE 1=1", null, "receivedetailid", "", new String[] { "receivecode" }));

		/**
		 * 根据分公司ID, 仓库ID, 产品ID得到库存数量: kc_dm.currstock
		 */
		hashSql.put(BEAN_PRODUCT_STORE_STOCK, new LookupHelper("SELECT * FROM kc_kchz ", null, new String[] { "fgsid", "storeid", "cpid" }, "", new String[] { "kcsl" }, false,
				false, null, null));
		// -----------------------------------------------------------------------
		// --审批
		// -----------------------------------------------------------------------
		/**
		 * 审批项目特殊明细
		 */
		hashSql.put(BEAN_APPROVE_SPECIAL, new LookupHelper("SELECT * FROM sp_xmtsmx", null, "spxmtsmxid", null, new String[] { "tsxmmc" }));

		// -----------------------------------------------------------------------
		// --成本
		// -----------------------------------------------------------------------
		/**
		 * 费用类型
		 */
		hashSql.put(BEAN_FEE_TYPE, new LookupHelper("SELECT * FROM cb_fee_type", null, "fee_type_id", null, new String[] { "fee_type_name" }, true));
		/**
		 * 费用名称
		 */
		hashSql.put(BEAN_FEE_NAME, new LookupHelper("SELECT * FROM cb_fee_name", null, "fee_name_id", null, new String[] { "fee_name" }, true));

		// -----------------------------------------------------------------------
		// --质量
		// -----------------------------------------------------------------------
		/**
		 * 检验器具分类
		 */
		hashSql.put(BEAN_QUALITY_TOOLTYPE, new LookupHelper("SELECT * FROM zl_tooltype", null, "toolTypeID", "ORDER BY toolTypeCode", new String[] { "toolTypeName" }, true));

		/**
		 * 检验方法
		 */
		hashSql.put(BEAN_QUALITY_CHECKMETHOD, new LookupHelper("SELECT * FROM zl_checkMethod", null, "id", "ORDER BY code", new String[] { "checkMethod" }, true));

		/**
		 * 检验项目(外观检验): zl_checkItem.7
		 */
		hashSql.put(BEAN_QUALITY_CHECKITEM_FACE, new LookupHelper("SELECT * FROM zl_checkItem where checktype=7", null, "checkitemid", "ORDER BY code",
				new String[] { "checkItem" }, true));

		/**
		 * 检验项目(镀铝纸生产过程检验): zl_checkItem.8
		 */
		hashSql.put(BEAN_QUALITY_CHECKITEM_PROC, new LookupHelper("SELECT * FROM zl_checkItem where checktype=8", null, "checkitemid", "ORDER BY code",
				new String[] { "checkItem" }, true));

		/**
		 * 缺陷等级: zl_bugGrade
		 */
		hashSql.put(BEAN_QUALITY_BUGGRADE, new LookupHelper("SELECT * FROM zl_bugGrade", null, "bugid", "ORDER BY bugcode", new String[] { "bugname" }, true));

		/**
		 * 质量原因: zl_qualityReason
		 */
		hashSql.put(BEAN_QUALITY_REASON, new LookupHelper("SELECT * FROM zl_qualityReason", null, "reasonid", "ORDER BY reasoncode", new String[] { "reasonname" }, true));

		/**
		 * 检验标准: zl_checkStandard
		 */
		hashSql.put(BEAN_QUALITY_CHECKSTANDARD, new LookupHelper("SELECT * FROM zl_checkStandard", null, "standardid", "ORDER BY standardcode", new String[] { "standardname" },
				true));

		/**
		 * 检查水平: zl_checkLevel
		 */
		hashSql.put(BEAN_QUALITY_CHECKLEVEL, new LookupHelper("SELECT * FROM zl_checkLevel ", null, "levelid", "ORDER BY levelcode", new String[] { "levelname" }, true));
		// -----------------------------------------------------------------------
		// --设备
		// -----------------------------------------------------------------------
		/**
		 * 设备信息:sb_recordCard
		 */
		hashSql.put(BEAN_EQUIPMENT, new LookupHelper("SELECT * FROM sb_recordCard ", null, "equipmentID", "ORDER BY equipment_code", new String[] { "equipment_name" }, true));

		/**
		 * 故障原因:sb_exceptionReason
		 */
		hashSql.put(BEAN_EQUIPMENT_EXCEPTIONREASON, new LookupHelper("SELECT * FROM sb_exceptionReason ", null, "excepReasonID", "ORDER BY excepReasonCode",
				new String[] { "excepReasonName" }, true));

		/**
		 * 大发包装方式
		 * 
		 */

		hashSql.put(BEAN_PACKAGE, new LookupHelper(
				"select * from(select (package_code||' '||package_name) packagename,funditemid,package_id,package_name,package_code from kc_package  where isdelete=0) where 1=1",
				null, "package_id", "ORDER BY packagename", new String[] { "packagename" }, true));
		/**
		 * 大发包装类别
		 * 
		 */
		hashSql.put(BEAN_PACKAGETYPE, new LookupHelper("SELECT * FROM xs_fund_item where 1=1", null, "funditemid", "ORDER BY funditemcode", new String[] { "funditemname" }, true));
	}

	/**
	 * 在缓冲池中得到LookupLookupHelperer对象
	 * 
	 * @return
	 */
	protected LookupHelper getLookupConfig(String name) {
		return (LookupHelper) hashSql.get(name);
	}
}

package engine.project;

/**
 * <p>
 * Title: 系统常量
 * </p>
 * <p>
 * Description: 系统常量列表
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

public interface SysConstant {
	// ---------相关需要引用的JAVABEAN
	/**
	 * 材质:jc_metal
	 */
	public static final String BEAN_METAL = "jc_metal";
	// -----------------------------------------------------------------------
	// --基础
	// -----------------------------------------------------------------------
	/**
	 * 所有仓库信息:kc_ck.both
	 */
	public static final String BEAN_STORE_BOTH = "kc_ck.both";

	/**
	 * 仓库信息:kc_ck
	 */
	public static final String BEAN_STORE = "kc_ck";

	/**
	 * 2004.12.14 add branch仓库信息:kc_ck_branch yjg
	 */
	public static final String BEAN_STORE_BRANCH = "kc_ck_branch";

	/**
	 * 仓库库位: kc_kw
	 */
	public static final String BEAN_STORE_AREA = "kc_kw";

	/**
	 * 存货信息: kc_dm
	 */
	public static final String BEAN_PRODUCT = "kc_dm";
	/**
	 * 存货信息: kc_dm
	 */
	public static final String BEAN_PRODUCT_ALL = "kc_dm_all";

	/**
	 * 存货信息（通过产品编码得到产品id）: kc_dm.code
	 */
	public static final String BEAN_PRODUCT_CODE = "kc_dm.code";

	/**
	 * 国家: country
	 */
	public static final String BEAN_COUNTRY = "country";

	/**
	 * 地区: dwdq
	 */
	public static final String BEAN_AREA = "dwdq";

	/**
	 * 地区号列表: dwdq.areacode
	 */
	public static final String BEAN_AREA_CODE = "dwdq.areacode";

	/**
	 * 外来单位: dwtx
	 */
	public static final String BEAN_CORP = "dwtx";

	/**
	 * 外来单位信誉额度: dwtx.credit
	 */
	public static final String BEAN_CORP_CREDIT = "dwtx.credit";

	/**
	 * 部门信息(包括总部和分部):bm.both
	 */
	public static final String BEAN_DEPT_BOTH = "bm.both";

	/**
	 * 部门信息(下拉列表显示总部): bm
	 */
	public static final String BEAN_DEPT = "bm";

	/**
	 * 所有部门列表信息: bm.list
	 */
	public static final String BEAN_DEPT_LIST = "bm.list";

	/**
	 * 车间信息key: bm.iswork
	 */
	public static final String BEAN_WORKSHOP = "bm.iswork";

	/**
	 * 分部信息key: bm.branch
	 */
	public static final String BEAN_DEPT_BRANCH = "bm.branch";

	/**
	 * 分公司信息key: bm.filiale
	 */
	public static final String BEAN_DEPT_FILIALE = "bm.filiale";
	/**
	 * 分公司信息key: bm.filiale
	 */
	public static final String BEAN_DEPT_ALL = "bm.all";

	/**
	 * 人员信息JAVABEAN
	 */
	public static final String BEAN_PERSON = "emp";
	/**
	 * 人员信息JAVABEAN,增加按姓名排序
	 */
	public static final String BEAN_PERSON_XM = "emp.xm";

	/**
	 * 1职务 2人员类别 3学历 4民族 5籍贯 6职称 7政治面貌 人员信息-职务: emp.1
	 */
	public static final String BEAN_PERSON_DUTY = "emp.1";

	/**
	 * 人员信息-人员类别: emp.2
	 */
	public static final String BEAN_PERSON_CLASS = "emp.2";

	/**
	 * 人员信息-学历: emp.3
	 */
	public static final String BEAN_PERSON_EDUCATION = "emp.3";

	/**
	 * 人员信息-民族: emp.4
	 */
	public static final String BEAN_PERSON_NATION = "emp.4";

	/**
	 * 人员信息-籍贯: emp.5
	 */
	public static final String BEAN_PERSON_NATIVE = "emp.5";

	/**
	 * 人员信息-职称: emp.6
	 */
	public static final String BEAN_PERSON_TECH = "emp.6";

	/**
	 * 人员信息-政治面貌: emp.7
	 */
	public static final String BEAN_PERSON_POLITY = "emp.7";

	/**
	 * 人员信息-工作组人员: gzzperson
	 */
	public static final String BEAN_GZZ_PERSON = "sc_gzz_person";

	/**
	 * 产品大类: kc_dmlb.first
	 */
	public static final String BEAN_PRODUCT_FIRST_KIND = "kc_dmlb.first";

	/**
	 * 产品大类(以编码作为key): kc_dmlb.firstcode
	 */
	public static final String BEAN_PRODUCT_FIRST_KIND_CODE = "kc_dmlb.firstcode";

	/**
	 * 产品类别: kc_dmlb
	 */
	public static final String BEAN_PRODUCT_KIND = "kc_dmlb";

	/**
	 * 存货类别: kc_chlb
	 */
	public static final String BEAN_STOCKS_KIND = "kc_chlb";

	/**
	 * 结算方式: jsfs
	 */
	public static final String BEAN_BALANCE_MODE = "jsfs";

	/**
	 * 发货方式: jc_sendmode
	 */
	public static final String BEAN_SEND_MODE = "jc_sendmode";

	/**
	 * 合同类型: jc_ordertype
	 */
	public static final String BEAN_ORDER_TYPE = "jc_ordertype";

	/**
	 * 常用单位: jc_common_unit
	 */
	public static final String BEAN_COMMON_UNIT = "jc_common_unit";

	/**
	 * 单据类型设置: jc_ordertype
	 */
	public static final String BEAN_FUND_TYPE = "xs_fund_item";
	/**
	 * 商标设置: brand
	 */
	public static final String BEAN_BRAND_ITEM = "brand";
	/**
	 * 采购单据类型设置: jc_ordertype
	 */
	public static final String BEAN_BUY_FUND_TYPE = "cg_fund_item";
	// -----------------------------------------------------------------------
	// --财务
	// -----------------------------------------------------------------------
	/**
	 * 外币信息: foreigncurrency
	 */
	public static final String BEAN_FOREIGN_CURRENCY = "foreigncurrency";

	/**
	 * 采购发票类别: jc_fplb.1
	 */
	public static final String BEAN_BUY_INVOICE_TYPE = "jc_fplb";

	/**
	 * 销售发票类别: jc_fplb.2
	 */
	public static final String BEAN_SALE_INVOICE_TYPE = "jc_fplb";

	/**
	 * 银行: bank
	 */
	public static final String BEAN_BANK = "bank";

	/**
	 * 银行帐号: bankaccount
	 */
	public static final String BEAN_BANK_ACCOUNT = "bankaccount";

	// ------------------------------------
	// 固定资产
	// ------------------------------------
	/**
	 * 固定资产类别: cw_capital_type
	 */
	public static final String BEAN_CAPITAL_TYPE = "cw_capital_type";

	/**
	 * 贷款信息: cw_loan
	 */
	public static final String BEAN_LOAN = "cw_loan";
	/**
	 * 固定资产类别: cw_capital_type
	 */
	public static final String BEAN_CAPITAL_TYPE_CODE = "cw_capital_type.code";
	// -----------------------------------------------------------------------
	// --销售
	// -----------------------------------------------------------------------
	/**
	 * 销售物资单价设置: xs_wzdj
	 */
	public static final String BEAN_SALE_PRICE = "xs_wzdj";
	/**
	 * 外贸物资单价设置: xp_wzdj
	 */
	public static final String BEAN_XPORT_PRICE = "xp_wzdj";
	/**
	 * 销售物资单价（根据cpid得到）: xs_wzdj_prod
	 */
	public static final String BEAN_PROD_PRICE = "xs_wzdj_prod";

	/**
	 * 销售合同: xs_ht
	 */
	public static final String BEAN_SALE_ORDER = "xs_ht";
	/**
	 * 纸箱单: xs_paperbox_mx
	 */
	public static final String BEAN_BOX_MX = "xs_paperbox_mx";
	/**
	 * 纸箱单: xs_paperbox_mx
	 */
	public static final String BEAN_BOX_MX_CHD = "xs_paperbox_mx.1";
	/**
	 * 外贸订单: xp_ht
	 */
	public static final String BEAN_XPORT_ORDER = "xp_ht";
	/**
	 * 销售合同货物: xs_hthw
	 */
	public static final String BEAN_SALE_ORDER_GOODS = "xs_hthw";
	/**
	 * 销售合同货物得到已开提单数量: xs_hthw.ykdsl
	 */
	public static final String BEAN_TDHW_YKTDSL = "xs_hthw.ykdsl";
	/**
	 * 外贸订单货物: xp_hthw
	 */
	public static final String BEAN_XPORT_ORDER_GOODS = "xp_hthw";
	/**
	 * 销售提单: xs_td
	 */
	public static final String BEAN_SALE_LADING = "xs_td";
	/**
	 * 外贸提单: xp_td
	 */
	public static final String BEAN_XPORT_LADING = "xp_td";
	/**
	 * 销售提单货物: xs_tdhw
	 */
	public static final String BEAN_SALE_LADING_BILL = "xs_tdhw";
	/**
	 * 外贸提单
	 */
	public static final String BEAN_XPORT_LADING_BILL = "VW_BEAN_XPORT_LADING";
	/**
	 * 外贸提单货物: xp_tdhw
	 */
	public static final String VW_BEAN_XPORT_LADING_DETAIL = "VW_BEAN_XPORT_LADING_DETAIL";
	/**
	 * 销售分栏帐设置: xs_flz
	 */
	public static final String BEAN_SALE_COLUMN_BREAK = "xs_flz";
	/**
	 * 销售提单承运单位明细: xs_tdcyqk
	 */
	public static final String BEAN_SALE_CARRY_THING = "xs_tdcyqk";
	/**
	 * 销售地区价格: xs_area_price
	 */
	public static final String BEAN_AREA_PRICE = "xs_area_price";

	/**
	 * 销售驾驶员：xs_driver
	 */
	public static final String BEAN_SALE_DRIVER = "xs_driver";

	/**
	 * 地区车号: xs_car
	 */
	public static final String BEAN_SALE_CAR = "xs_car";

	/**
	 * 地区车号明细: xs_area_car
	 */
	public static final String BEAN_AREA_CAR = "xs_area_car";

	/**
	 * 客户信誉等级
	 */
	public static final String BEAN_CUST_GRADE = "cust_grade";

	/**
	 * 客户应收款
	 */
	public static final String BEAN_CUST_ACCOUNT_RECEIVABLE = "VW_CUST_ACCOUNT_RECEIVABLE";

	/**
	 * 促销产品的记录数
	 */
	public static final String BEAN_PROMOTION_COUNT = "xs_promotion.count";

	// -----------------------------------------------------------------------
	// --生产
	// -----------------------------------------------------------------------
	/**
	 * 物资属性提前期
	 */
	public static final String BEAN_PROD_AHAED_TIME = "sc_prod_time";
	/**
	 * 工艺路线类型: sc_gylxlx
	 */
	public static final String BEAN_TECHNICS_ROUTE_TYPE = "sc_gylxlx";
	/**
	 * 生产流程卡货物
	 */
	public static final String BEAN_PROCESS_CARD_DETAIL = "sc_processmx";

	/**
	 * 工艺路线组: sc_gylxlxz
	 */
	public static final String BEAN_TECHNICS_ROUTE_GROUP = "sc_gylxlxz";
	/**
	 * 工艺路线类型: process_factory
	 */
	public static final String BEAN_PROCESS_FACTORY_TYPE = "process_factory";

	/**
	 * 工艺名称: sc_gymc
	 */
	public static final String BEAN_TECHNICS_NAME = "sc_gymc";

	/**
	 * 工序分段: sc_gxfd
	 */
	public static final String BEAN_WORK_PROCEDURE = "sc_gxfd";

	/**
	 * 工作中心: sc_gzzx
	 */
	public static final String BEAN_WORK_CENTER = "sc_gzzx";

	/**
	 * 工作组: sc_gzz
	 */
	public static final String BEAN_WORK_GROUP = "sc_gzz";

	/**
	 * 工艺路线: sc_gylx
	 */
	public static final String BEAN_TECHNICS_ROUTE = "sc_gylx";

	/**
	 * 工序（工艺路线明细）: sc_gylxmx
	 */
	public static final String BEAN_TECHNICS_PROCEDURE = "sc_gylxmx";

	/**
	 * 工序（工艺路线明细）: sc_gylxmx
	 */
	public static final String BEAN_TECHNICS_OPTION_PROCEDURE = "sc_gylxmx_options";
	/**
	 * 工序（工艺路线明细）: sc_gylxmx_expand
	 */
	public static final String BEAN_TECHNICS_OPTION_PROCEDURE_EXPAND = "sc_gylxmx_options_expand";

	/**
	 * 生产计划: sc_jh
	 */
	public static final String BEAN_PRODUCE_PLAN = "sc_jh";

	/**
	 * 生产计划detail: sc_jhmx get htbh via scjhmxid
	 */
	public static final String BEAN_PRODUCE_PLAN_DETAIL = "sc_jhmxid_htbh";

	/**
	 * 物料需求明细: sc_wlxqjhmx
	 */
	public static final String BEAN_MRP_GOODS = "sc_wlxqjhmx";

	/**
	 * 任务单明细: sc_rwdmx
	 */
	public static final String BEAN_PRODUCE_TASK_GOODS = "sc_rwdmx";
	/**
	 * 任务单主表: sc_rwd
	 */
	public static final String BEAN_PRODUCE_TASK = "sc_rwd";
	/**
	 * 生产加工单: sc_jgd
	 */
	public static final String BEAN_PRODUCE_PROCESS = "sc_jgd";

	/**
	 * 生产加工单明细: sc_jgdmx
	 */
	public static final String BEAN_PRODUCE_PROCESS_GOODS = "sc_jgdmx";

	/**
	 * 计划可供量: sc_jhkgl 按cpid dmsxid
	 */
	public static final String BEAN_PALN_USABLE_NUMBER = "sc_jhkgl";
	/**
	 * 计划可供量: sc_jhkgl 按cpid
	 */
	public static final String BEAN_PALN_USABLE_NUMBER_TWO = "sc_jhkgl.2";
	/**
	 * 生产跟踪卡类型: sc_track_type
	 */
	public static final String BEAN_PRODUCE_TRACK_TYPE = "sc_track_type";

	/**
	 * 车间流转单: sc_cjlzdmx
	 */
	public static final String BEAN_SHOP_FLOW_DETALL = "sc_cjlzdmx";
	/**
	 * 华正生产设备班次维护主表
	 */
	public static final String BEAN_SCSBBC = "VW_BEAN_SCSBBC";
	/**
	 * 华正生产设备班次维护明细
	 */
	public static final String BEAN_SCSBBCMX = "VW_BEAN_SCSBBCMX";
	/**
	 * 锡星生产工序单位 2005.1.21
	 */
	public static final String BEAN_SCGXDW = "sc_procedure_dw";
	/**
	 * 计件工资工序: sc_wage_procedure
	 * 
	 * public static final String BEAN_WAGE_PROCEDURE = "sc_wage_procedure";
	 * 
	 * //-----------------------------------------------------------------------
	 * //--人力
	 * //-----------------------------------------------------------------------
	 * /** 银行信用卡: rl_yhxyk
	 */
	public static final String BEAN_BANK_CREDIT_CARD = "rl_yhxyk";

	/**
	 * 招聘申请单: rl_invite_apply
	 */
	public static final String BEAN_PERSON_APPLY = "rl_invite_apply";

	// -----------------------------------------------------------------------
	// --采购
	// -----------------------------------------------------------------------
	/**
	 * 采购报价: cg_bj
	 */
	public static final String BEAN_BUY_PRICE = "cg_bj";

	/**
	 * 采购审请单货物: cg_sqdhw
	 */
	public static final String BEAN_BUY_APPLY_GOODS = "cg_sqdhw";

	/**
	 * 采购合同货物: cg_hthw
	 */
	public static final String BEAN_BUY_ORDER_GOODS = "cg_hthw";

	/**
	 * 进货单货物: cg_htjhdhw
	 */
	public static final String BEAN_BUY_ORDER_STOCK = "cg_htjhdhw";

	/**
	 * 采购应付款余额查询: VW_YFKYE
	 */
	public static final String VW_BEAN_BUY_YFKYE = "VW_BUY_YFKYE";

	// -----------------------------------------------------------------------
	// --库存
	// -----------------------------------------------------------------------
	// 1=合同入库单,2=销售出库单,3=自制入库单,4=生产领料单,5=外加工入库单,6=外加工发料单,7=报损单,8=移库单
	/**
	 * 收发单据类别(全部) 用于列表显示
	 */
	public static final String BEAN_IN_OUT_BILL = "kc_sfdjlb";

	/**
	 * 收发单据类别-合同入库单: kc_sfdjlb.1
	 */
	public static final String BEAN_STORE_SALE_IN = "kc_sfdjlb.1";

	/**
	 * 收发单据类别-合同出库单: kc_sfdjlb.2
	 */
	public static final String BEAN_STORE_SALE_OUT = "kc_sfdjlb.2";

	/**
	 * 收发单据类别-自制入库单: kc_sfdjlb.3
	 */
	public static final String BEAN_STORE_PRODUCE_IN = "kc_sfdjlb.3";

	/**
	 * 收发单据类别-生产领料单: kc_sfdjlb.4
	 */
	public static final String BEAN_STORE_PRODUCE_OUT = "kc_sfdjlb.4";

	/**
	 * 收发单据类别-外加工入库单: kc_sfdjlb.5
	 */
	public static final String BEAN_STORE_PROCESS_IN = "kc_sfdjlb.5";

	/**
	 * 收发单据类别-外加工发料单: kc_sfdjlb.6
	 */
	public static final String BEAN_STORE_PROCESS_OUT = "kc_sfdjlb.6";

	/**
	 * 收发单据类别-报损单: kc_sfdjlb.7
	 */
	public static final String BEAN_STORE_LOSS = "kc_sfdjlb.7";

	/**
	 * 收发单据类别-移库单: kc_sfdjlb.8
	 */
	public static final String BEAN_STORE_MOVE = "kc_sfdjlb.8";

	/*
	 * 收发单据类别-其他入库单: kc_sfdjlb.9
	 */
	public static final String BEAN_STORE_OTHER_IN = "kc_sfdjlb.9";

	/*
	 * 收发单据类别-其他出库单: kc_sfdjlb.10
	 */
	public static final String BEAN_STORE_OTHER_OUT = "kc_sfdjlb.10";

	/*
	 * 收发单据类别-同价调拨单: kc_sfdjlb.11
	 */
	public static final String BEAN_STORE_SALEMOVE = "kc_sfdjlb.11";
	/**
	 * 收发单据类别-外贸出库单: kc_sfdjlb.13
	 */
	public static final String BEAN_STORE_OUT_SALE = "kc_sfdjlb.13";

	/**
	 * 收发单据类别-外贸入库单: kc_sfdjlb.14
	 */
	public static final String BEAN_STORE_IN_SALE = "kc_sfdjlb.14";

	/**
	 * 收发单据明细: kc_sfdjmx
	 */
	public static final String BEAN_STORE_BILL_DETAIL = "kc_sfdjmx";

	/**
	 * 生产单据用途: kc_csdjyt
	 */
	public static final String BEAN_PRODUCE_USE = "kc_csdjyt";

	/**
	 * 物资规格属性: kc_dmsx
	 */
	public static final String BEAN_SPEC_PROPERTY = "kc_dmsx";

	/**
	 * 产品信息及其库存汇总信息: kc_dm_hz
	 */
	public static final String BEAN_PRODUCT_STOCK = "kc_dm_hz";

	/**
	 * 产品库存批号: kc_wzmx
	 */
	public static final String BEAN_PRODUCT_BATCH = "kc_wzmx";

	/**
	 * 盘点单: kc_pd
	 */
	public static final String BEAN_STORE_CHECK = "kc_pd";

	/**
	 * 自制收获单明细: sc_receiveproddetail
	 */
	public static final String BEAN_RECEIVE_MATERIAL_DETALL = "sc_receiveproddetail";

	/**
	 * 生产领料单明细: sc_drawmaterialdetail
	 */
	public static final String BEAN_DRAW_MATERIAL_DETALL = "sc_drawmaterialdetail";

	/**
	 * 根据分公司ID, 仓库ID, 产品ID得到库存数量: kc_dm.currstock
	 */
	public static final String BEAN_PRODUCT_STORE_STOCK = "kc_dm.currstock";
	/**
	 * 根据规格属性取得库存量
	 */
	public static final String BEAN_PRODUCT_STORE_STOCK2 = "kc_dm2.currstock";
	// -----------------------------------------------------------------------
	// --审批
	// -----------------------------------------------------------------------
	/**
	 * 审批项目特殊明细: sp_xmtsmx
	 */
	public static final String BEAN_APPROVE_SPECIAL = "sp_xmtsmx";

	// -----------------------------------------------------------------------
	// --成本
	// -----------------------------------------------------------------------
	/**
	 * 费用类型
	 */
	public static final String BEAN_FEE_TYPE = "cb_fee_type";

	/**
	 * 费用名称
	 */
	public static final String BEAN_FEE_NAME = "cb_fee_name";

	// -----------------------------------------------------------------------
	// --质量
	// -----------------------------------------------------------------------
	/**
	 * 检验器具分类: zl_tooltype
	 */
	public static final String BEAN_QUALITY_TOOLTYPE = "zl_tooltype";

	/**
	 * 检验方法: zl_checkMethod
	 */
	public static final String BEAN_QUALITY_CHECKMETHOD = "zl_checkMethod";

	/**
	 * 检验项目(外观检验): zl_checkItem.7
	 */
	public static final String BEAN_QUALITY_CHECKITEM_FACE = "zl_checkItem.7";

	/**
	 * 检验项目(镀铝纸生产过程检验): zl_checkItem.8
	 */
	public static final String BEAN_QUALITY_CHECKITEM_PROC = "zl_checkItem.8";

	/**
	 * 缺陷等级: zl_bugGrade
	 */
	public static final String BEAN_QUALITY_BUGGRADE = "zl_bugGrade";

	/**
	 * 质量原因: zl_qualityReason
	 */
	public static final String BEAN_QUALITY_REASON = "zl_qualityReason";

	/**
	 * 检验标准: zl_checkStandard
	 */
	public static final String BEAN_QUALITY_CHECKSTANDARD = "zl_checkStandard";

	/**
	 * 检查水平: zl_checkLevel
	 */
	public static final String BEAN_QUALITY_CHECKLEVEL = "zl_checkLevel";

	// -----------------------------------------------------------------------
	// --设备
	// -----------------------------------------------------------------------
	/**
	 * 设备信息:sb_recordCard
	 */
	public static final String BEAN_EQUIPMENT = "sb_recordCard";

	/**
	 * 故障原因:sb_exceptionReason
	 */
	public static final String BEAN_EQUIPMENT_EXCEPTIONREASON = "sb_exceptionReason";
	/**
	 * 华正设备机台号 通过设备状态ID得到设备机台号
	 */
	public static final String BEAN_EQUIPMENT_JTH = "sc_sbzt";
	/**
	 * 锡鑫设备 通过设备id得到设备
	 */
	public static final String BEAN_FIXING = "sc_fixing";
	/**
	 * 锡鑫工序单位 通过工序名称id得到工序名称
	 */
	public static final String BEAN_GXDW = "sc_procedure_dw";
	// -----------------------------------------------------------------------
	//
	public static final String TABLE_SALE_ORDER = "xs_ht";

	public static final String TABLE_SALE_ORDER_GOODS = "xs_hthw";
	// -----------------------------------------------------------------------
	// --展览广告公司javabean
	// -----------------------------------------------------------------------
	/**
	 * 展览广告公司javabean
	 */
	/**
	 * 三鑫展览广告公司 得到展馆号
	 */
	public static final String BEAN_BOOK_ZH = "fgs_show_setting.zh";
	public static final String BEAN_BOOK_ZG = "fgs_show_setting";

	public static final String BEAN_BOOK_ZW = "fgs_zwsetting";
	public static final String BEAN_PACKAGE = "kc_package";
	public static final String BEAN_PACKAGETYPE = "xs_fund_item";
	/**
	 * 车间产能明细: kc_cjcnmx
	 */
	public static final String BEAN_PRODUCE_SHOP_GOODS = "kc_cjcnmx";
	/**
	 * 人员信息-职工级别: emp.9
	 */
	public static final String BEAN_PRODUCE_RANK = "emp.9";
}
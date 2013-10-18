package engine.erp.jit;

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
import engine.erp.jit.PlanSelectStore;
import engine.erp.jit.PlanSelectSaleMaster;
import engine.erp.jit.PlanSelectSale;
import engine.erp.jit.PlanMakeProcess;
import engine.erp.baseinfo.BasePublicClass;
//import engine.erp.produce.SubPlan_Sel_Materail;
//import engine.erp.produce.BestCombination;

import java.util.List;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 生产--生产计划维护列表</p>
 * <p>Description: 生产--生产计划维护列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨建国
 * @version 1.0
 */

public final class ProducePlan extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";//打开从表事件
  public  static final String DETAIL_SELECT_STORE = "10111";//按库存量生产事件
  public  static final String DETAIL_PLAN_SELECT = "10021";//根据销售合同从表信息增加触发事件
  public  static final String CANCEL_APPROVE = "15631";//取消审批触发事件
  public  static final String SINGLE_PRODUCT_ADD = "10631";//从表单选产品事件
  public  static final String MUTI_ORDER_ADD = "10931";//生产计划引入合同主表，从表信息全部引入
  public  static final String SINGLE_ORDER_ADD = "10932";//生产计划引入合同主表，从表信息全部引入
  public  static final String ONCHANGE = "10002";//在从表中输入产品编码触发事件
  //public  static final String PROPERTYCHANGE = "10003";//在从表中选择规格属性触发事件
  public  static final String COMPLETE = "11231";//完成操作事件
  public  static final String REPORT = "2000";//报表追踪触发事件
  public  static final String SUBPLAN = "2001";//在生产计划主表里面增加分切计划触发事件
  //public  static final String SUBPLAN_SEL_SALE = "2002";//增加分切计划明细时选择销售合同货物触发事件
  public  static final String SUBPLAN_DEL = "2003";//删除分切计划明细一行纪录触发事件
  public  static final String SUBPLAN_DET_ADD = "2004";//分切计划明细增加一行纪录触发事件
  public  static final String SUBPLAN_TAXIS = "2005";//分切计划明细从表排序触发事件
  public  static final String SET_SUBSECTION = "2006";//分切计划明细设置分段设置触发事件
  public  static final String SUBDETAIL_COPY = "2007";//分切计划明细复制触发事件
  public  static final String SUBPLAN_POST_CONTINUE = "2008";//分切计划明细和生产分切物料保存添加触发事件
  public  static final String SELECT_MATERAIL = "2009";//生产分切物料从库存物资明细中选择物料触发事件
  public  static final String MATERAIL_DEL = "2010";//生产分切物料明细中删除物料触发事件
  public  static final String SUBMASTER_DEL = "2011";//生产分切计划主表删除触发事件
  public  static final String FINE_DISPART = "2012";//生产分切计划最佳搭配分切原料触发事件
  public  static final String INSERT_MATERAIL = "2013";//生产分切计划物料选择最佳搭配后将最佳物料插入分切物料表中触发事件
  public  static final String BUILD_PROCESS_BILL = "2014";//生成生产加工单
  public  static final String ADD_WM = "2015";//生产计划引用外贸合同
  /**
   * 定义一些SQL语句
   */
  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_jh WHERE 1<>1";//生产计划主表表结构
  private static final String MASTER_SQL    = "SELECT * FROM sc_jh WHERE ? AND fgsid=? ? ORDER BY jhh DESC";//生产计划主表SQL
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_jhmx WHERE 1<>1";//生产通用计划从表结构
  private static final String DETAIL_SQL    = "SELECT * FROM sc_jhmx WHERE scjhid='?' ORDER BY fdxh, fdxs, hthwid, cpid";//
  //private static final String SUB_DETAIl_STRUT_SQL = "SELECT * FROM sc_fqjhmx WHERE 1<>1";//生产通用计划从表结构
  private static final String SUB_MRP_STRUT_SQL = "SELECT * FROM sc_fqjhwl WHERE 1<>1";//生产分切计划物料结构
  //分切计划要用到的SQL语句
  //private static final String SUB_DETAIL_SQL    = "SELECT * FROM sc_fqjhmx WHERE scjhid='?' ORDER BY fdxs";//生产分切计划明同细
  private static final String SUB_MRP_SQL    = "SELECT * FROM sc_fqjhwl WHERE scjhid='?' ";//生产分切计划物料需求数据

  //保存前把生产计划明细里的是否生成实际BOM设为零，以便下次从新生成
  private static final String UPDATE_SQL  = "UPDATE sc_jhmx SET sfsc='1' WHERE scjhmxid=";//生成实际BOM后UPDATE生产计划明细
  //库存物资明细数据，用于分切。供选择
  private static final String Materail_SQL = "SELECT a.wzmxid, a.cpid, a.zl, c.cpbm, b.sxz, (c.pm|| '' || c.gg) product, c.jldw, c.scdwgs, a.dmsxid FROM kc_wzmx a, kc_dmsx b, kc_dm c "
      + " WHERE a.cpid=c.cpid AND a.dmsxid=b.dmsxid(+) AND a.zl>0 AND b.sxz  LIKE '%@%' @ ORDER BY c.cpbm ";
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM sc_jh WHERE scjhid='?'";

  //得到该生产计划ID当该计划生成物料需求后物料需求的状态为审核SQL语句，确定是否显示下达任务的按钮
  private static final String TASK_SHOW
      = "SELECT scjhid FROM (SELECT scjhid, zt FROM sc_wlxqjh WHERE scjhid IN (?) ) t  ";

  //通过合同ID得到合同货物信息，生产计划引入销售合同主表时，保合同从表中已计划数量小于数量的信息全部加入。
  private static final String ORDER_DETAIL_SQL
      = "SELECT * FROM VW_PLAN_SALEDETAIL WHERE htid='?' ";//销售订单填入
  private static final String ORDER_DETAIL_SQL_WM
      = "SELECT * FROM VW_PLAN_SALEDETAIL2 WHERE htid='?' ";//外贸订单填入

  /*********/
  //得到生产计划生成的物料需求.只取1=自制件
  private static final String MRP_DETAIL_SQL =
      "SELECT * FROM VW_PRODUCETASK_SEL_MRP WHERE fgsid=?  and scjhid=? "; //AND (deptid IS NULL OR deptid=?)
  private static final String PROCESS_MASTER_STRUT_SQL = "SELECT * FROM sc_jgd WHERE 1<>1";
  private static final String PROCESS_MASTER_SQL    =
      "SELECT * FROM sc_jgd WHERE ? AND fgsid='?' AND jgdid IN(SELECT DISTINCT a.jgdid FROM sc_jgdmx a, sc_wlxqjh b, sc_wlxqjhmx c WHERE a.wlxqjhmxid=c.wlxqjhmxid AND b.wlxqjhid= c.wlxqjhid AND b.scjhid='?') ORDER BY jgdh DESC";
  private static final String PROCESS_DETAIL_STRUT_SQL = "SELECT * FROM sc_jgdmx WHERE 1<>1";
  private static final String PROCESS_DETAIL_SQL    = "SELECT * FROM sc_jgdmx WHERE jgdid='?' ";//
  private static final String SC_JH_UPDATE_SQL = "UPDATE sc_jh SET zt='3' WHERE scjhid= ";//下达车间任务后改变生产计划状态
  //用于审批时候提取一条记录
  //private static final String MASTER_APPROVE_SQL = "SELECT * FROM sc_jgd WHERE rwdid='?'";
  //
  //生产加工单的表结构SQL语句
  private static final String PROCESSMASTER_STRUT_SQL = "SELECT * FROM sc_jgd WHERE 1<>1";
  private static final String PROCESSDETAIL_STRUT_SQL = "SELECT * FROM sc_jgdmx WHERE 1<>1";
  //生成加工单后直接生成领料清单
  private static final String DRAW_MATETAIL_SQL = "SELECT * FROM sc_jgdwl where 1<>1";
  //通过加工单主表生产计划ID和从表的任务单明细ID得到合同ID再根据cpid等于实际BOM表的上级cpid得到物料
  private static final String MATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.sjsjbomid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, a.htid, "
      + " a.wgcl, a.chxz "
      + " FROM sc_sjbom a, sc_bomsection b WHERE a.sjsjbomid = '?' "
      + " and a.bomid = b.bomid and b.gxfdid = '?' "
      + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, a.htid, a.wgcl,a.sjsjbomid, a.chxz ORDER BY a.cpid";
  /**
   private static final String MATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, a.htid, b.scjhid "
   + " FROM sc_sjbom a, sc_jh b, sc_jhmx c WHERE a.scjhmxid=c.scjhmxid AND b.scjhid=c.scjhid AND a.htid IN( "
   + " SELECT d.htid FROM sc_wlxqjhmx d, sc_rwdmx e WHERE d.wlxqjhmxid=e.wlxqjhmxid AND  e.rwdmxid='?') AND b.scjhid='?' AND a.sjcpid='?' "
   + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, a.htid, b.scjhid ORDER BY a.cpid ";
   */
  //分切计划通过加工单主表生产计划ID和从表的任务单明细ID得到合同ID再根据cpid等于实际BOM表的上级cpid得到物料
  private static final String SUBMATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, b.scjhid "
      + " FROM sc_sjbom a, sc_jh b, sc_jhmx c WHERE a.scjhmxid=c.scjhmxid AND b.scjhid=c.scjhid AND nvl(a.htid,-1) IN( "
      + " SELECT nvl(d.htid,-1) FROM sc_wlxqjhmx d WHERE d.wlxqjhmxid='?' ) AND b.scjhid='?' AND a.sjcpid IN(?) "
      + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, b.scjhid ORDER BY a.cpid ";
  //BOM表数据
  private static final String BOM_MATERAIL = "SELECT a.* FROM sc_bom a WHERE a.sjcpid='?' ";
  //对sc_wlxqjhmx按cpid,dmsxid,gylxid分组得出来的记录数就是要生成的加工单的张数
  //2004-10-10 修改 因为物料须求计划中去掉了gylxid,
  //               所以gylxid的分组是根据wlxqjhmx中的cpid, dmsxid组合在sc_gylx中找到的 yjg
  private  String GROUPBYGYLXID_MRPDETAIL
      = " SELECT h.gylxid, m.cpid, nvl(m.dmsxid,0) dmsxid FROM sc_gylx h, sc_wlxqjh l, "
      + " sc_wlxqjhmx m "
      + " WHERE l.wlxqjhid = m.wlxqjhid "
      + " AND m.cpid = h.cpid AND nvl(m.dmsxid,0) = h.dmsxid and m.chxz = 1 and l.scjhid = ? "
      + " GROUP BY h.gylxid, m.cpid, m.dmsxid ";
    /*" SELECT s.gylxid FROM sc_wlxqjhmx s, "
      + " sc_wlxqjh s2 "
      + " WHERE s.chxz = 1 and s.wlxqjhID = s2.wlxqjhID "
      + " and s2.scjhID = ? "
      + " GROUP BY gylxid ";
      */
  private String LOCATEGYLXID = "SELECT gylxid, cpid, dmsxid from sc_gylx where cpid = ? and dmsxid = ? ";
  //统计
  private static final String COUNT_SCJGD = "SELECT COUNT(*) FROM sc_jgd a WHERE a.scjhid =  ";
  private static final String SELECT_GXFDID = "SELECT s.gxfdid FROM sc_gylxmx s, sc_gylx sc "
      + " WHERE s.gylxid = sc.gylxid "
      + " AND sc.gylxid = '?' group by s.gxfdid ";
  private static final String DELETESJ_BOM_SQL = "delete sc_sjbom where scjhmxid in ( ? ) ";

  //从sc_sjbom,gxfdid中取出gxfdid.查出来的笔数就是要生成加工单的个数.
  private static final String SC_JGD_MASTER_SJBOM =
      " SELECT h.gxfdid " //, m.cc, m.wgcl
      + " FROM sc_gylxmx h, sc_jhmx j, sc_sjbom m , sc_gylx e "
      + " WHERE j.scjhmxid = m.scjhmxid AND h.gylxid = e.gylxid "
      + "       AND e.cpid = m.cpid AND nvl(m.dmsxid,0) = e.dmsxid "
      + "       AND m.chxz = 1 AND j.scjhid = '?' "
      + " GROUP BY h.gxfdid "//, m.cc, m.wgcl
      + " ORDER BY max(m.cc) DESC, MAX(h.list) DESC ";
  //生产加工单明细数据.从sc_sjbom中取出
  private static final String SC_JGD_DETAIL_SJBOM =
      " SELECT h.gxfdid, m.cc, m.wgcl, m.cpid, m.dmsxid, m.xql sl, m.scxql scsl, m.sjbomid, m.scjhmxid, j.ksrq "
      + " FROM sc_gylxmx h, sc_jhmx j, sc_sjbom m , sc_gylx e "
      + " WHERE j.scjhmxid = m.scjhmxid AND h.gylxid = e.gylxid "
      + "       AND e.cpid = m.cpid AND nvl(m.dmsxid,0) = e.dmsxid "
      + "       AND m.chxz = 1 AND j.scjhid = '?' "
      + " GROUP BY h.gxfdid, m.cc, m.wgcl, m.cpid, m.dmsxid,m.xql, m.scxql, m.sjbomid, m.scjhmxid, j.ksrq "
      + " ORDER BY m.cc DESC, MAX(h.list) DESC ";
  private static final String GET_PROD_TIME = " select to_date('?', 'yyyy-mm-dd')+ trunc(nvl(tot_time, 0)/24) jgdksrq , to_date('?', 'yyyy-mm-dd') + trunc(nvl(tot_time, 0)/24) + trunc(nvl(ahead_time, 0)/24) jgdwcrq  from sc_prod_time where cpid = '?' and dmsxid = '?' ";
  private static final String SC_JH_GET_PROD_TIME = " select to_date('?', 'yyyy-mm-dd') + trunc(nvl(tot_time, 0)/24) scjhwcrq  from sc_prod_time where cpid = '?' and dmsxid = '?' ";
  private static final String GET_SJCPID_WGCL = " SELECT wgcl FROM sc_sjbom sbm "
     + " WHERE sbm.sjbomid = (SELECT sb.sjsjbomid FROM sc_sjbom sb WHERE sb.sjbomid = '?' ) ";
  private EngineDataSet dsMrpMaster  = new EngineDataSet();//物料需求计划主表
  private EngineDataSet dsMrpDetail  = new EngineDataSet();//物料需求计划从表
  private EngineDataSet dsGroupByMrpDetail  = new EngineDataSet();//按gylxid对物料需求计划明细表分组

  private EngineDataSet dsFactBom = new EngineDataSet();;//实际BOM数据集
  private EngineDataSet dsDeleteFactBom = new EngineDataSet();;//实际BOM数据集
  private EngineDataSet dsDrawMaterail = new EngineDataSet();//领料请单数据集
  private EngineDataSet dsBom = new EngineDataSet();;//BOM数据集
  //private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  //private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsProcessMaster = new EngineDataSet();//加工单主表数据集
  private EngineDataSet dsProcessDetail = new EngineDataSet();//加工单从表
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//生产通用计划从表
  //private EngineDataSet dsSubDetailTable  = new EngineDataSet();//生产分切计划从表
  private EngineDataSet dsSubMrpTable  = new EngineDataSet();//生产分切计划物料表
  private EngineDataSet dsThrowOut = new EngineDataSet();//用于是否显示下达任务单
  private EngineDataSet dsGxfdid = new EngineDataSet();//用于取得一个工艺路线下的所有工序分段
  private EngineDataSet ds_Sc_jh_Get_Prod_Time = new EngineDataSet();

  private ArrayList throwOut = new ArrayList();

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_jh");//打印生产计划表头的对象
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_jhmx");//打印生产计划明细表头的对象
  public  HtmlTableProducer searchTable = new HtmlTableProducer(dsMasterTable, "sc_jh", "sc_jh");//查询得到数据库中配置的字段

  //public  HtmlTableProducer subdetailProducer = new HtmlTableProducer(dsSubDetailTable, "sc_fqjhmx");//打印生产分切计划表头的对象
  public  HtmlTableProducer submrpProducer = new HtmlTableProducer(dsSubMrpTable, "sc_fqjhwl");//打印生产分切计划物料表头

  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态

  public  boolean isReport = false;      //是否是在报表追踪状态

  public  boolean isDetailAdd = false; //是否从表在增加状态

  public boolean isSubPlan = false; //是否是增加分切计划

  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private long    subDetailRow = -1;      //保存生产分切计划明细操作的行纪录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  //private ArrayList d_SubRowInfos = null; //生产分切计划从表多行记录的引用
  private ArrayList d_SubMrpRowInfos = null; //生产分切计划物料从表多行记录的引用
  public Hashtable table = null; //用于存放经过分段序数排序后，各个相等序数段的宽度和
  public Hashtable minwidthTable = null; //用于存放分切计划明细中最小宽度
  private Hashtable remainTable = null; //用于存放模拟配料后的剩余原料
  private ArrayList remainKey = null;//存放余料信息remainTable的Key
  //private CombinationInfo fineInfo = null;//用于保存返回最佳搭配物料方法，和各种方法所需物料的List
  private boolean isBest = false;//是否得到最佳配料，如果是false就是有移料
  private PlanSelectSaleDetail planSelectSaleDetailBean = null; //销售合同的bean的引用, 用于提取销售合同
  private PlanSelectStore planSelectStoreBean = null; //当前库存量的bean的引用, 用于提取当前库存量
  private PlanMakeProcess planMakeProcess = null; //生成生产加工单
  //private SubPlan_Sel_Materail materailBean = null; //用于分切当前库存量的bean的引用, 生产分切计划用
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  private LookUp productBean = null; //产品信息的bean的引用, 用于提取产品信息
  private LookUp propertyBean = null; //产品信息的bean的引用, 用于提取产品信息

  private PlanSelectSale planSelectSaleBean = null;//销售合同主表bean的引用

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String scjhid = null;
  public  String jhlx = null;//计划类型
  private String deptid = null;//计划类型
  private String jgdid = null;//计划类型
  BigDecimal totalNum = new BigDecimal(0);
  boolean isAll =  false;//明细表中是否物料需求引入
  private User user = null;
  private String minWidth = null; //分切计划明细中经过分段排序后小计宽度最小值
  public String SC_PLAN_ADD_STYLE = null;//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
  public String SC_PRODUCE_UNIT_STYLE =null;//1=强制换算,0=仅空值时换算
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……

  /**
   * 生产计划列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回生产计划列表的实例
   */
  public static ProducePlan getInstance(HttpServletRequest request)
  {
    ProducePlan producePlanBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "producePlanBean";
      producePlanBean = (ProducePlan)session.getAttribute(beanName);
      if(producePlanBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        producePlanBean = new ProducePlan();
        producePlanBean.qtyFormat = loginBean.getQtyFormat();
        producePlanBean.sumFormat = loginBean.getSumFormat();

        producePlanBean.fgsid = loginBean.getFirstDeptID();
        producePlanBean.loginId = loginBean.getUserID();
        producePlanBean.loginName = loginBean.getUserName();
        producePlanBean.loginDept = loginBean.getDeptID();
        producePlanBean.user = loginBean.getUser();
        producePlanBean.SC_PLAN_ADD_STYLE = loginBean.getSystemParam("SC_PLAN_ADD_STYLE");
        producePlanBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//计量单位和生产单位是否强制换算
        producePlanBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        producePlanBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        //设置格式化的字段
        producePlanBean.dsDetailTable.setColumnFormat("sl", producePlanBean.qtyFormat);
        producePlanBean.dsMasterTable.setColumnFormat("zsl", producePlanBean.sumFormat);
        producePlanBean.dsDetailTable.setColumnFormat("scsl", producePlanBean.qtyFormat);
        //producePlanBean.dsSubDetailTable.setColumnFormat("sl", producePlanBean.qtyFormat);
        producePlanBean.dsSubMrpTable.setColumnFormat("sl", producePlanBean.qtyFormat);
        session.setAttribute(beanName, producePlanBean);//把类的对象存入session
      }
    }
    return producePlanBean;
  }

  /**
   * 构造函数
   */
  private ProducePlan()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }

  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private final void jbInit() throws java.lang.Exception
  {
    //设置不提换双引号和单引号
    dsMasterTable.setReplaceQuotate(false);
    setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    //setDataSetProperty(dsSubDetailTable, SUB_MRP_STRUT_SQL);
    setDataSetProperty(dsSubMrpTable, SUB_MRP_STRUT_SQL);
    setDataSetProperty(dsThrowOut, null);
    setDataSetProperty(dsMrpMaster, null);
    setDataSetProperty(dsGroupByMrpDetail, null);
    setDataSetProperty(dsFactBom, null);
    setDataSetProperty(dsBom, null);
    setDataSetProperty(dsMrpDetail, null);
    setDataSetProperty(dsGxfdid, null);
    setDataSetProperty(dsDeleteFactBom, null);
    setDataSetProperty(ds_Sc_jh_Get_Prod_Time, null);
    setDataSetProperty(dsProcessMaster, PROCESSMASTER_STRUT_SQL);
    setDataSetProperty(dsProcessDetail, PROCESSDETAIL_STRUT_SQL);
    setDataSetProperty(dsDrawMaterail, DRAW_MATETAIL_SQL);
    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jhh"}, new String[]{"SELECT pck_base.billNextCode('sc_jh','jhh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"jhh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"scjhmxid"}, new String[]{"s_sc_jhmx"}));
    dsDetailTable.setSort(new SortDescriptor("", new String[]{"fdxh","fdxs","hthwid","cpid"}, new boolean[]{false, false}, null, 0));

    dsSubMrpTable.setSequence(new SequenceDescriptor(new String[]{"scfqjhwlid"}, new String[]{"s_sc_fqjhwl"}));
    dsMasterTable.addLoadListener(new com.borland.dx.dataset.LoadListener(){
      public void dataLoaded(com.borland.dx.dataset.LoadEvent event)
      {
        throwOut.clear();
        if(dsMasterTable.getRowCount() == 0)
          return;
        String sql = getWhereIn(dsMasterTable, "scjhid", null);
        sql = combineSQL(TASK_SHOW, "?", new String[]{sql});
        dsThrowOut.setQueryString(sql);
        if(dsThrowOut.isOpen())
          dsThrowOut.refresh();
        else
          dsThrowOut.openDataSet();
        for(int i=0; i<dsThrowOut.getRowCount(); i++)
        {
          throwOut.add(dsThrowOut.getValue("scjhid"));
          dsThrowOut.next();
        }
        dsThrowOut.closeDataSet();
      }
    });

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"scjhmxid"}, new String[]{"s_sc_jhmx"}));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);//增加通用计划触发事件-销售合同
    addObactioner(String.valueOf(ADD_WM), masterAddEdit);//增加通用计划触发事件-外贸合同
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(SUBPLAN), masterAddEdit);//增加分切计划触发事件
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(SUBPLAN_POST_CONTINUE), masterPost);//分切计划明细和分切计划物料保存添加触发事件
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());//增加一空白行
    addObactioner(String.valueOf(SINGLE_PRODUCT_ADD), new Single_Product_Add());//从表选择单个产品增加操作
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(APPROVE), new Approve());//审批事件
    addObactioner(String.valueOf(REPORT), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(DETAIL_PLAN_SELECT), new Detail_Select_Sale());
    addObactioner(String.valueOf(DETAIL_SELECT_STORE), new Detail_Select_Store());
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());
    addObactioner(String.valueOf(MUTI_ORDER_ADD), new Muti_Order_Add());
    addObactioner(String.valueOf(SINGLE_ORDER_ADD), new Single_Order_Add());
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    //addObactioner(String.valueOf(PROPERTYCHANGE), new Onchange());//从表选择规格属性触发事件
    addObactioner(String.valueOf(COMPLETE), new Complete());//手工完成操作
    //addObactioner(String.valueOf(SUBPLAN_SEL_SALE), new SubPlan_Select_Sale());//注册分切计划明细中选择销售合同货物操作
    addObactioner(String.valueOf(SUBPLAN_DEL), new Detail_Delete());//删除分切计划明细一行纪录触发事件
    //addObactioner(String.valueOf(SUBPLAN_DET_ADD), new SubDetail_Add());//分切计划明细增加一行纪录触发事件
    addObactioner(String.valueOf(SUBPLAN_TAXIS), new SubPlan_Taxis());//分切计划明细输入分段序数后排序触发事件
    //addObactioner(String.valueOf(SET_SUBSECTION), new Set_Subsection());//分切计划明细选择货物添加以设置分段设置触发事件
    //addObactioner(String.valueOf(SUBDETAIL_COPY), new SubDetail_Copy());//分切计划明细复制一行触发事件
    addObactioner(String.valueOf(MATERAIL_DEL), new Materail_Del());//分切计划物料删除物料触发事件
    addObactioner(String.valueOf(SUBMASTER_DEL), new Master_Delete());//分切计划主表删除触发事件
    addObactioner(String.valueOf(BUILD_PROCESS_BILL), new Build_Process_Bill());//生成生产加工单
  }

  //----Implementation of the BaseAction abstract class
  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String operate = request.getParameter(OPERATE_KEY);
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
        if(data == null)
          return showMessage("无效操作", false);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsMasterTable.isOpen() && dsMasterTable.changesPending())
        dsMasterTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   *
   * @param scjhid
   * 是否显示下达任务单按钮
   * @return
   */
  public boolean isShowTask(String scjhid)
  {
    return throwOut.contains(scjhid);
  }
  /**
   * @param scjhid
   * 得到分切计划产品中最小规格属性宽度值
   * @return
   */
  public String getMinWidth()
  {
    return minWidth;
  }


  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMasterTable != null){
      dsMasterTable.close();
      dsMasterTable = null;
    }
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
    }
    if(dsSubMrpTable != null){
      dsSubMrpTable.close();
      dsSubMrpTable = null;
    }
    if(dsProcessMaster != null){
     dsProcessMaster.close();
     dsProcessMaster = null;
    }
    if(dsProcessDetail != null){
     dsProcessDetail.close();
     dsProcessDetail = null;
    }
    if(dsDrawMaterail != null){
     dsDrawMaterail.close();
     dsDrawMaterail = null;
    }
    if(ds_Sc_jh_Get_Prod_Time != null){
      ds_Sc_jh_Get_Prod_Time.close();
      ds_Sc_jh_Get_Prod_Time = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    d_SubMrpRowInfos=null;
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
    }
    if(detailProducer != null)
    {
      detailProducer.release();
      detailProducer = null;
    }
    if(submrpProducer != null)
    {
      submrpProducer.release();
      submrpProducer = null;
    }
  }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否是主表
    if(isMaster){
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();

      if(!isAdd)
        m_RowInfo.put(getMaterTable());
      else
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        m_RowInfo.put("zdrq", today);//制单日期
        m_RowInfo.put("zdr", loginName);//操作员
        m_RowInfo.put("jhrq", today);//计划日期
        m_RowInfo.put("zdrid", loginId);
        m_RowInfo.put("deptid", loginDept);
        String jhh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('sc_jh','jhh') from dual");
        m_RowInfo.put("jhh", jhh);
        //m_RowInfo.put("djh", jhh);
        m_RowInfo.put("jhlx", jhlx);
        m_RowInfo.put("jhsm", "印花规格:           成品规格:        坯布配比:");


      }
    }
    else
    {
      EngineDataSet dsDetail = getDetailTable();
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if(isInit)
        d_RowInfos.clear();

      dsDetail.first();//循环数据集
      for(int i=0; i<dsDetail.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDetail);
        row.put("InternalRow", String.valueOf(dsDetail.getInternalRow()));
        d_RowInfos.add(row);
        dsDetail.next();
      }
      //如果是通用计划不用计算小计和最小宽度值
      if(!jhlx.equals("1"))
        return;
      //得到相同分段的宽度小计值
      String fdxs=null, sxz=null, width=null, dmsxid=null, fdxh=null;
      BigDecimal wid_1 = new BigDecimal(0),
      wid_2 = new BigDecimal(0), total = new BigDecimal(0);
      if(table == null)
        table = new Hashtable(dsDetail.getRowCount()+1);//存放宽度小计
      else
        table.clear();
      if(minwidthTable==null)
        minwidthTable = new Hashtable(dsDetail.getRowCount()+1);//存放最小宽度
      else
        minwidthTable.clear();

      for(int i=0; i<d_RowInfos.size(); i++)
      {
        RowMap row = (RowMap)d_RowInfos.get(i);
        dmsxid = row.get("dmsxid");
        fdxh = row.get("fdxh");
        fdxs= row.get("fdxs");
        if(fdxs.equals(""))
          continue;
        //通过规格属性ID得到属性值
        //dataSetProvider.getSequence("select sxz from kc_dmsx where dmsxid='"+dmsxid+"'");
        sxz = propertyBean.getLookupName(dmsxid);
        width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP,"()");//得到规格属性的宽度SYS_PRODUCT_SPEC_PROP为“宽度“
        minwidthTable.put(dmsxid, new BigDecimal(width));
        String key = fdxh+","+fdxs;
        BigDecimal bgWidth = (BigDecimal)table.get(key);
        if(bgWidth == null)
          bgWidth = new BigDecimal(width);
        else
          bgWidth = bgWidth.add(new BigDecimal(width));

        table.put(key, bgWidth);
      }
      minWidth = BasePublicClass.getMinString(minwidthTable);//得到小计后最小宽度
    }
  }
  /**
   * 制定分切生产计划时初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initSubRowInfo(boolean isInit) throws java.lang.Exception
  {
    EngineDataSet dsSubMrp = getSubMrpTable();//把生产分切计划物料表的数据推入到ArrayList数组里面
    if(d_SubMrpRowInfos == null)//如果数组为空，重新new
      d_SubMrpRowInfos = new ArrayList(dsSubMrp.getRowCount());
    else if(isInit)//如果是初始化先清空数据集
      d_SubMrpRowInfos.clear();

    dsSubMrp.first();//循环数据集
    for(int i=0; i<dsSubMrp.getRowCount(); i++)
    {
      RowMap row = new RowMap(dsSubMrp);
      d_SubMrpRowInfos.add(row);
      dsSubMrp.next();
    }
  }

  /**
   * 生产通用计划从表保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = getMasterRowinfo();
    //保存网页的所有信息
    rowInfo.put(request);

    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      String sl = rowInfo.get("sl_"+i);
      detailRow.put("sl", rowInfo.get("sl_"+i));//
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性
      detailRow.put("gylxid", rowInfo.get("gylxid_"+i));//工艺路线
      detailRow.put("csl", rowInfo.get("csl_"+i));//超产率
      detailRow.put("ksrq", rowInfo.get("ksrq_"+i));//开始日期
      detailRow.put("wcrq", rowInfo.get("wcrq_"+i));//完成日期
      detailRow.put("jgyq", rowInfo.get("jgyq_"+i));//加工要求
      detailRow.put("scsl", rowInfo.get("scsl_"+i));//生产数量
      detailRow.put("fdxs", rowInfo.get("fdxs_"+i));//分段序数
      detailRow.put("fdxh", rowInfo.get("fdxh_"+i));//分段序号
      detailRow.put("cpl", rowInfo.get("cpl_"+i));
      //detailRow.put("scdwgs", rowInfo.get("scdwgs_"+i));//分段序号
      if(jhlx.equals("1"))
        detailRow.put("ischeck", rowInfo.get("ischeck_"+i));//是否参与模拟配料
      //保存用户自定义的字段
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
  }

  /**
   * 生产分切计划从表保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常

  private final void putSubDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = getMasterRowinfo();
    //保存网页的所有信息
    rowInfo.put(request);

    int rownum = d_SubRowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_SubRowInfos.get(i);
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//数量
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性
      detailRow.put("gylxid", rowInfo.get("gylxid_"+i));//工艺路线
      detailRow.put("scsl", rowInfo.get("scsl_"+i));//转换为生产单位的生产数量
      detailRow.put("fdxs", rowInfo.get("fdxs_"+i));//分段序数
      detailRow.put("jgyq", rowInfo.get("jgyq_"+i));//加工要求
      //保存用户自定义的字段
      FieldInfo[] fields = subdetailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
  }
  */
 /**
  * 生产分切计划物料保存操作
  * 把页面分切物料信息推入到ArrayList中
  * @param request 网页的请求对象
  * @param response 网页的响应对象
  * @return 返回HTML或javascipt的语句
  * @throws Exception 异常
  */
  private final void putSubMrpInfo(HttpServletRequest request)
  {
    RowMap rowInfo = getMasterRowinfo();
    //保存网页的所有信息
    rowInfo.put(request);

    int rownum = d_SubMrpRowInfos.size();
    RowMap subMrpRow = null;
    for(int i=0; i<rownum; i++)
    {
      subMrpRow = (RowMap)d_SubMrpRowInfos.get(i);
      subMrpRow.put("sl", formatNumber(rowInfo.get("wlsl_"+i), qtyFormat));//数量
      subMrpRow.put("scsl", formatNumber(rowInfo.get("wlscsl_"+i), qtyFormat));//生产数量

      //subMrpRow.put("cpid", rowInfo.get("wlcpid_"+i));//产品
      //subMrpRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性
      //保存用户自定义的字段
      FieldInfo[] fields = submrpProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        subMrpRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
  }
   /*得到模拟配料后的剩余原料对象*/
  public final Hashtable getRemainTable()
  {
    if(remainTable==null)
      remainTable = new Hashtable();
    return remainTable;
  }
  /*得到存放模拟配料后的剩余原料remainTable的Key*/
  public final ArrayList getRemainKey()
  {
    if(remainKey==null)
      remainKey = new ArrayList();
    return remainKey;
  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    /*if ( !dsMasterTable.isOpen() )
      dsMasterTable.openDataSet();
    else
    {
      dsMasterTable.readyRefresh();
      dsMasterTable.refresh();
    }*/
    return dsMasterTable;
  }

  /*得到生产通用计划从表表对象*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.openDataSet();
    return dsDetailTable;
  }
  /*得到生产分切计划从表表对象
  public final EngineDataSet getSubDetailTable(){
    if(!dsSubDetailTable.isOpen())
      dsSubDetailTable.open();
    return dsSubDetailTable;
  }
  */
 /*得到生产分切计划物料表对象*/
  public final EngineDataSet getSubMrpTable(){
    if(!dsSubMrpTable.isOpen())
      dsSubMrpTable.open();
    return dsSubMrpTable;
  }
  /*
  * 打开从表
  * 打开通用计划明细的数据集
  */
  public final void openDetailTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : scjhid;
    SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});

    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    else
      dsDetailTable.refresh();
  }
  /*
  * 打开从表
  * 打开分切计划明细的数据集，以及打开分切计划物料数据集
  */
  public final void openSubDetailTable(boolean isMasterAdd)
  {
    String id = isMasterAdd ? "-1" : scjhid;
    //String SQL = combineSQL(SUB_DETAIL_SQL, "?", new String[]{id});//打开生产分切计划表语句
    String sql = combineSQL(SUB_MRP_SQL,"?",new String[]{id});//打开生产分切计划物料语句
    /**
    dsSubDetailTable.setQueryString(SQL);//打开生产分切计划表
    if(!dsSubDetailTable.isOpen())
      dsSubDetailTable.open();
    else
      dsSubDetailTable.refresh();
      */
    dsSubMrpTable.setQueryString(sql);//打开生产分切计划物料表
    if(!dsSubMrpTable.isOpen())
      dsSubMrpTable.open();
    else
      dsSubMrpTable.refresh();
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }



  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
 /*得到生产分切计划从表多列的信息
  public final RowMap[] getSubRowinfos() {
    RowMap[] rows = new RowMap[d_SubRowInfos.size()];
    d_SubRowInfos.toArray(rows);
    return rows;
  }
  */
   /*得到从表多列的信息*/
  public final RowMap[] getSubMrpRowinfos() {
    RowMap[] rows = new RowMap[d_SubMrpRowInfos.size()];
    d_SubMrpRowInfos.toArray(rows);
    return rows;
  }
  /**
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isMasterAdd; }

  /**
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }

  /**
   * 得到选中的行的行数
   * @return 若返回-1，表示没有选中的行
   */
  public final int getSelectedRow()
  {
    if(masterRow < 0)
      return -1;

    dsMasterTable.goToInternalRow(masterRow);
    return dsMasterTable.getRow();
  }
  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      isReport = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      submrpProducer.init(request, loginId);
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("zt", "0");
      row.put("jhrq$a", startDay);
      row.put("jhrq$b", today);
      searchTable.getWhereInfo().clearWhereValues();
      isMasterAdd = true;
      isDetailAdd= false;
      //
      String SQL = " AND zt<>8";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      if(dsSubMrpTable.isOpen() && dsSubMrpTable.getRowCount() > 0)
        dsSubMrpTable.empty();
    }
  }

  /**
   * 显示从表的列表信息
   */
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterTable.getInternalRow();
      scjhid = dsMasterTable.getValue("scjhid");
      //打开从表
      openDetailTable(false);
    }
  }
  /**
   *  从表输入产品编码触发操作
   */
  class Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      //boolean isproperty = String.valueOf(PROPERTYCHANGE).equals(action);
      //if(!isproperty){
        int row = Integer.parseInt(data.getParameter("rownum"));
        dsDetailTable.goToRow(row);
        RowMap detail=(RowMap)d_RowInfos.get(row);
        String cpid = detail.get("cpid");
        String dmsxid = detail.get("dmsxid");
        RowMap productRow = getProductBean(req).getLookupRow(cpid);
        long ztqq = productRow.get("ztqq").length()>0 ? Long.parseLong(productRow.get("ztqq")) : 0;//总提前期
        Date startdate = new Date();
        Date enddate = new Date(startdate.getTime() + ztqq*60*60*1000);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(startdate);
        String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
        String ksrq = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //String today = new SimpleDateFormat("yyyy-MM-dd").format(startdate);//生产计划开始日期为当前日期
        //String endDate = new SimpleDateFormat("yyyy-MM-dd").format(end);//当前日期加上总提前期
        //java.sql.Date jsrqDtae = java.sql.Date.valueOf(jsrq);
        //java.sql.Date rq = java.sql.Date.valueOf(endDate);
        String wcrq = getScjhWcrq(cpid, dmsxid);
        wcrq = wcrq.equals("")?ksrq:wcrq;
        detail.put("ksrq", ksrq);
        detail.put("wcrq", wcrq);
        //}
    }
  }
  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      String operate = request.getParameter(OPERATE_KEY);
      if(operate.equals(String.valueOf(ADD)))
        jhlx ="0";
        else
          jhlx="1";
      if(remainTable!=null)
        remainTable.clear();
      if(remainKey !=null)
        remainKey.clear();
      isApprove = false;
      isReport = false;
      isDetailAdd = false;
      isMasterAdd = String.valueOf(ADD_WM).equals(action)||String.valueOf(ADD).equals(action) || String.valueOf(SUBPLAN).equals(action);//新增通用计划或者新增分切计划
      isSubPlan = String.valueOf(SUBPLAN).equals(action);//新增分切计划
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        jhlx = dsMasterTable.getValue("jhlx");
        scjhid = dsMasterTable.getValue("scjhid");
      }
      //if(isSubPlan)
      //  jhlx="1";//如果是制定分切计划，设置计划类型为1
      //else if(!isSubPlan && isMasterAdd)
      //  jhlx="0";//如果是制定通用计划，设置计划类型为0
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      //reg
      LookUp prodPropBean = getPropertyBean(data.getRequest());
      prodPropBean.regData(dsDetailTable, "dmsxid");
      //
      initRowInfo(false, isMasterAdd, true);
      if(jhlx.equals("1")){
        openSubDetailTable(isMasterAdd);
        initSubRowInfo(true);
      }
     // if(!jhlx.equals("1"))
       data.setMessage(showJavaScript("toDetail();"));
     // else
     //   data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
   * 审批操作的触发类
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      boolean isRep = String.valueOf(REPORT).equals(action);

      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      submrpProducer.init(request, loginId);
      //得到request的参数,值若为null, 则用""代替
      String id =null;
      if(!isRep){
        isApprove = true;
        id = data.getParameter("id", "");
      }
      else{
        isApprove = false;
        isReport = true;
        id = data.getParameter("scjhid");
      }
      //得到request的参数,值若为null, 则用""代替
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();
      scjhid = dsMasterTable.getValue("scjhid");
      jhlx = dsMasterTable.getValue("jhlx");
      //打开从表
      openDetailTable(false);
      LookUp prodPropBean = getPropertyBean(data.getRequest());
      prodPropBean.regData(dsDetailTable, "dmsxid");//注册
      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
      if(jhlx.equals("1")){
        openSubDetailTable(false);
        initSubRowInfo(true);
      }
    }
  }
  /**
   * 强制完成操作触发的类
   * 根据已完成量和计划量比较手工完成操作
   */
  class Complete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsMasterTable.goToRow(row);
      dsMasterTable.setValue("zt", "8");
      dsMasterTable.post();
      dsMasterTable.saveChanges();
    }
  }
  /**
   插入明细的时候.把
  1.把DETAILDATASET的数据cpid+dmsxid的放入到arraylist中.arraylist[0]的cpid就是最后一个工段,此时要判断wgcl
    决定当前sc_jgdmx表中的wgcl是1 or 2.如原来wgcl是1则最终wgcl=2.如是2则2.其它情况wgcl不变
  2.循环DETAILDATASET. 取主表的gxfdid,cc,wgcl locate明细表.如locate到则将当前定位到的
    DEATILDATA插入到sc_jgdmx表中
  **/
  class Build_Process_Bill implements Obactioner
  {
    //从sc_sjbom中取出来的原始数据.将会被插入到加工单主表和明细表中去.
    EngineDataSet dsSc_JgdMaster = null;
    EngineDataSet dsSc_JgdDetail = null;
    EngineDataSet dsGet_Prod_Time = null;
    ArrayList sc_sjbomCpidList = null;
    ArrayList sc_sjbomGxfdList = null;
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      isMasterAdd = true;
      int row = Integer.parseInt(data.getParameter("rownum"));
      //从sc_sjbom中取出来的原始数据.将会被插入到加工单主表和明细表中去.
      dsSc_JgdMaster = null;
      dsSc_JgdDetail = null;
      dsMasterTable.goToRow(row);
      //request到scjhid, jhlx, deptid从MRP_DETAIL_SQL中取出物料插入到明细表中
      HttpServletRequest req = data.getRequest();
      deptid = dsMasterTable.getValue("deptid");
      scjhid = dsMasterTable.getValue("scjhid");
      scjhid = dsMasterTable.getValue("scjhid");
      jhlx = dsMasterTable.getValue("jhlx");
      if(deptid.equals(""))
        return;
      String count  = dataSetProvider.getSequence(COUNT_SCJGD + scjhid);
      //如count=0说明没有此生产计划生成的加工单,那么就继续生成加单的操作.否则,返回.什么都不做
      if (!count.equals("0"))
      {
        data.setMessage(showJavaScript("toProduce_Process("+scjhid+");"));
        return;
      }
      if (dsSc_JgdMaster==null)
      {
        dsSc_JgdMaster = new EngineDataSet();
        setDataSetProperty(dsSc_JgdMaster, null);
      }
      if (dsSc_JgdDetail==null)
      {
        dsSc_JgdDetail = new EngineDataSet();
        setDataSetProperty(dsSc_JgdDetail, null);
      }
      if (dsGet_Prod_Time==null)
      {
        dsGet_Prod_Time = new EngineDataSet();
        setDataSetProperty(dsGet_Prod_Time, null);
      }
      String tmp = combineSQL(SC_JGD_MASTER_SJBOM, "?", new String[]{scjhid});
      dsSc_JgdMaster.setQueryString(tmp);
      tmp = combineSQL(SC_JGD_DETAIL_SJBOM, "?", new String[]{scjhid});
      dsSc_JgdDetail.setQueryString(tmp);
      if (!dsSc_JgdMaster.isOpen())
        dsSc_JgdMaster.openDataSet();
      else
        dsSc_JgdMaster.refresh();

      if (!dsSc_JgdDetail.isOpen())
        dsSc_JgdDetail.openDataSet();
      else
        dsSc_JgdDetail.refresh();

      if(dsProcessMaster.isOpen())
        dsProcessMaster.refresh();
      else
        dsProcessMaster.openDataSet();

      if(dsProcessDetail.isOpen())
        dsProcessDetail.refresh();
      else
        dsProcessDetail.openDataSet();
      //把dsSc_JgdDetail中的cpid取出来放到arraylist中
      int numOfSc_jgdDetail = dsSc_JgdDetail.getRowCount();
      sc_sjbomCpidList = new ArrayList(numOfSc_jgdDetail);
      sc_sjbomGxfdList = new ArrayList(numOfSc_jgdDetail);
      dsSc_JgdDetail.first();
      for (int i = 0;i<numOfSc_jgdDetail;i++)
      {
        //cpid+dmsxid组合相同的只保存一次
        String tmpCpidDmsxidLink =
                   dsSc_JgdDetail.getValue("cpid")+"_"+dsSc_JgdDetail.getValue("dmsxid")
                   + "_" + dsSc_JgdDetail.getValue("cc")+"_"+dsSc_JgdDetail.getValue("wgcl");
        if (!sc_sjbomCpidList.contains(tmpCpidDmsxidLink))
        {
          sc_sjbomCpidList.add(tmpCpidDmsxidLink);
          String gxfdidtmp = dsSc_JgdDetail.getValue("gxfdid");
          sc_sjbomGxfdList.add(gxfdidtmp);
        }
        dsSc_JgdDetail.next();
      }
      int numOfSc_jgdMaster = dsSc_JgdMaster.getRowCount();
      dsSc_JgdMaster.first();
      //loop生产加工单主表原始数据集.dsSc_JgdMaster数据集有几笔,就生成几张加工单
      for (int j = 0; j<numOfSc_jgdMaster;j++)
      {
        /*
        插入生产加工单明细表数据 逻辑是:
        取主表的gxfdid,cc,wgcl locate明细表.如locate到则将当前定位到的
        DEATILDATA插入到sc_jgdmx表中
        要考虑到的两个判断是:
        1.当前的此笔将要插入到sc_jgdmx表中的数据.是不是属于这张加工单主表的.用明细表中的gxfdid, cc, wgcl在主表中locate来判断
        2.当前插入的此笔数据是不是属于最后一个工段的.
        */
        String gxfdid = dsSc_JgdMaster.getValue("gxfdid");
        //String cc = dsSc_JgdMaster.getValue("cc");
        //String wgcl = dsSc_JgdMaster.getValue("wgcl");
        //插入一张加工单主表
        totalNum = new BigDecimal(0);
        master_post(gxfdid);
        dsSc_JgdDetail.first();
        //loop dsSc_JgdDetail 从sc_sjbom中取出的这个数据集.
        //筛选gxfd, cc, wgcl与主表相同的数据插入到sc_jgdmx表中去.
        ArrayList sjbomidList = new ArrayList();
        for (int k=0; k<numOfSc_jgdDetail;k++)
        {
          //判断是否可以插入到sc_jgdmx表中.如果定位到,则说明工段与主表对应.可以当成明细表数据插入
          String detailGxfdid = dsSc_JgdDetail.getValue("gxfdid");
          String detailCC = dsSc_JgdDetail.getValue("cc");
          String detailWgcl = dsSc_JgdDetail.getValue("wgcl");
          String sjbomid = dsSc_JgdDetail.getValue("sjbomid");
          if (detailGxfdid.equals(gxfdid)){
            //&&detailCC.equals(cc)&&detailWgcl.equals(wgcl)
            detail_post(gxfdid, sjbomid);
            /*把sjbomid连接成sjbomid1, sjbomid2, .....的字串.目地是给取这笔加工单的物料的时候用
            if (sjbomidBuf.length()>0) sjbomidBuf.append(",");
            sjbomidBuf.append(sjbomid);
            */
            sjbomidList.add(sjbomid);
          }
          dsSc_JgdDetail.next();
        }//end 生成加工单明细表的loop
        //如果生产加工单明细表中有资料产生,那么则保存主表,及产生物料.
        String zsl = totalNum.toString();
        dsProcessMaster.setValue("zsl", zsl);
        dsProcessMaster.post();
        if ( dsProcessDetail.getRowCount()>0 )
        {
          dsProcessMaster.saveDataSets(new EngineDataSet[]{dsProcessMaster, dsProcessDetail});
          //String sjbomidStr = sjbomidBuf.toString();
          wlDetail_post(gxfdid, sjbomidList);
          dsDrawMaterail.saveChanges();
        }
        dsProcessMaster.emptyRow();
        dsProcessDetail.emptyAllRows();
        dsDrawMaterail.emptyAllRows();
        dsSc_JgdMaster.next();
      }//end 生成加工单主表的loop
      dsSc_JgdMaster.closeDataSet();
      dsSc_JgdDetail.closeDataSet();
      sc_sjbomCpidList = null;
      sc_sjbomGxfdList = null;
      ProducePlan producePlanBean = ProducePlan.getInstance(data.getRequest());
      EngineDataSet dsPlanMaster = producePlanBean.getMaterTable();
      dsPlanMaster.goToRow(row);
      String zt = dsPlanMaster.getValue("zt");
      if(zt.equals("2")&&numOfSc_jgdDetail>0)
      {
        dsPlanMaster.setValue("zt", "3");
        dsPlanMaster.post();
      }
      dsPlanMaster.saveChanges();
      data.setMessage(showJavaScript("toProduce_Process("+scjhid+");"));
    }
    public void master_post(String gxfdid) throws Exception
    {
      //得到主表主键值
      //if(isMasterAdd){
      dsProcessMaster.insertRow(false);
      jgdid = dataSetProvider.getSequence("s_sc_jgd");
      //保存最基本的数据
      dsProcessMaster.setValue("jgdid", jgdid);
      String jgdh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('sc_jgd','jgdh') from dual");
      dsProcessMaster.setValue("jgdh", jgdh);
      dsProcessMaster.setValue("fgsid", fgsid);
      //dsProcessMaster.setValue("wlxqjhid", wlxqjhid);
      dsProcessMaster.setValue("scjhid",scjhid);
      isAll =  isAllMaterail();//从表中是否有一条纪录引入物料需求
      //dsProcessMaster.setValue("wlxqjhid", wlxqjhid);//物料需求计划ID
      //dsProcessMaster.setValue("wlxqjhid", isAll ? wlxqjhid : "");//物料需求计划ID
      //dsProcessMaster.setValue("scjhid", isAll ? scjhid : "");//生产计划ID
      //dsProcessMaster.setValue("zsl", totalNum.toString());
      //dsProcessMaster.setValue("rwlx", jhlx);
      //String gylxid = dsMrpDetail.getValue("gylxid");
      //04.10.12 修改 物料须求生成加工单主表的时候,加工单主表上的部门是这样产生的:通过到目前为止可以得到的gxfdid得到的部门作为它的部门.  yjg
      String workshopdeptid = dataSetProvider.getSequence(" select deptid from sc_gxfd where gxfdid = " + gxfdid );
      dsProcessMaster.setValue("deptid", workshopdeptid);//下达车间id
      dsProcessMaster.setValue("bm_deptid", deptid);//下达车间id
      //dsProcessMaster.setValue("gylxid", gylxid); 04.10.11去掉gylxid的赋值.因为, 此时SC_JGD去掉了GYLXID字段了
      dsProcessMaster.setValue("gxfdid", gxfdid);//??工段如何取得??
      dsProcessMaster.setValue("sfwjg","0");//自动生成加工单时，默认为自加工不是外加工
      dsProcessMaster.setValue("zt","0");
      dsProcessMaster.setValue("kdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//日期
      dsProcessMaster.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
      dsProcessMaster.setValue("zdrid", loginId);
      dsProcessMaster.setValue("zdr", loginName);//操作员
      //保存主表数据
      dsProcessMaster.post();
    }
    public void detail_post(String gxfdid, String sjbomid) throws Exception
    {
      /*
      1. 如果这笔cpid的wgcl是领料,那么最后一个工段的完工处理是入库(领料).如果是流转,则这笔插入到sc_jgdmx表中的cpid所有的工段都是流转.
      2. 如何判断工段是最后一个工段
      3.完工处理的判断时考虑的条件:
        1.是否最后一个工段设为self.lastgxfd:是最后一个工段:true,否则false
        2.他的上一级产品的完工处理什么parent.wgcl.领料:1.流转:2。
          自己的完工处理设为self.wgcl
          判断条件是:
          if self.lastgxfd and parent.wgcl=1 then self.wgcl=1
          else self.wgcl=2
      */
      //保存从表的数据
      String cpid = dsSc_JgdDetail.getValue("cpid");
      String dmsxid = dsSc_JgdDetail.getValue("dmsxid");
      String wgcl =  dsSc_JgdDetail.getValue("wgcl");
      String cc =  dsSc_JgdDetail.getValue("cc");
      String tmpContains = cpid+"_"+dmsxid+"_"+cc+"_"+wgcl;
      String getSjCpidTmpSql = combineSQL(GET_SJCPID_WGCL, "?", new String[]{sjbomid});
      String sjcpidwgcl = dataSetProvider.getSequence(getSjCpidTmpSql);
      sjcpidwgcl = sjcpidwgcl==null?"1":sjcpidwgcl;
      /*
        判断是不是最后一个工段.如果包含到了,那则说明这是最后一个工段.
        如果bom中定义的原来的wgcl是入库(1),并且,当前工段是最后一个工段,那么赋值给sc_jgdmx中的wgcl就是入库(1)
        if cpid原始bom wgcl=1(入库) and gxfdid=最后一个工段 then wgcl=1
        else if cpid原始bom wgcl = 1 and gxfdid <> 最后一个工段  then wgcl = 2(流转)
        else if wgcl = 2 and (gxfdid = 最后一个工段 or <> 最后一个工段) wgcl = 2
       */
      int i = sc_sjbomCpidList.indexOf(tmpContains);
      if ( i>=0 )
      {
        String gxfdidtmp =  (String)sc_sjbomGxfdList.get(i);
        // if detail table current gxfdid equals master table gxfdid then it's the last gxfd
        if (gxfdidtmp.equals(gxfdid)&&wgcl.equals("1"))//&&sjcpidwgcl.equals("1")
        {
          //如果是最后一个工段.则remove
          sc_sjbomCpidList.remove(i);
          sc_sjbomGxfdList.remove(i);
          wgcl = "1";
        }
        else
          wgcl = "2";
      }
      else
        wgcl = "2";
      dmsxid = dmsxid.equals("")?"0":dmsxid;
      dsProcessDetail.insertRow(false);
      String jgdmxid = dataSetProvider.getSequence("s_sc_jgdmx");
      dsProcessDetail.setValue("jgdmxid", jgdmxid);
      dsProcessDetail.setValue("jgdid", jgdid);
      String scsl = dsSc_JgdDetail.getValue("scsl");
      String sl = dsSc_JgdDetail.getValue("sl");
      String scjhmxid = dsSc_JgdDetail.getValue("scjhmxid");
      String ksrq = dsSc_JgdDetail.getValue("ksrq");//生产日期开始日期
      dsProcessDetail.setValue("cpid", cpid);
      dsProcessDetail.setValue("dmsxid", dmsxid);
      String tmpdmsxid = dmsxid.equals("")?"0":dmsxid;
      String prod_time_sql = combineSQL(GET_PROD_TIME, "?", new String[]{ksrq, ksrq, cpid, tmpdmsxid});
      dsGet_Prod_Time.setQueryString(prod_time_sql);
      if (dsGet_Prod_Time.isOpen())
        dsGet_Prod_Time.refresh();
      else
        dsGet_Prod_Time.openDataSet();
      //加工单的物资的开始日期 = 生产计划开始日期 + 该物资的总提前日期。
      //结束日期 = 开始日期 + 提前期。(总提前期和提前期在产品提前期表中sc_prod_time)
      String jgdksrq = dsGet_Prod_Time.getValue("jgdksrq");//提前其
      String jgdwcrq = dsGet_Prod_Time.getValue("jgdwcrq");//总提前其
      dsProcessDetail.setValue("scsl", scsl);
      dsProcessDetail.setValue("wgcl", wgcl);
      dsProcessDetail.setValue("scjhmxid", scjhmxid);
      //俩个都返回已排计量数量， 已排生产单位单位数量
      dsProcessDetail.setValue("sl", sl);
      //dsProcessDetail.setValue("gylxid", dsMrpDetail.getValue("gylxid"));
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      //String xqrq = dsSc_JgdDetail.getValue("xqrq");
      //String ksrq = dsSc_JgdDetail.getValue("ksrq");
      totalNum = totalNum.add(dsSc_JgdDetail.getBigDecimal("sl"));
      dsProcessDetail.setValue("ksrq",jgdksrq);
      dsProcessDetail.setValue("wcrq", jgdwcrq);
      //detail.setValue("rwdid", isMasterAdd ? "" : rwdid);
      dsProcessDetail.post();
    }
    public void wlDetail_post(String gxfdid, ArrayList sjbomidList) throws Exception
    {
      /*
      * 生成加工单后直接生成领料单
      * 根据加工单明细ID得到任务单明细ID，再得到物料需求计划明细ID，在得到合同ID
      * 生产加工单主表的生产计划ID和明细的产品ID等于实际bom的上级产品ID
      * 关联到实际BOM得到加工单所需要的原料 SC_AUTO_PROCESS_BILL.equals("1") &&
      *
      * 通过加工单明细ID得到销售合同ID和生产计划ID。
      * 并根据销售合同ID和生产计划ID和物资ID(对应实际BOM的上级物资ID)得到下级物料列表，
      * 相同的下级物料数量需要合并。
      */
      dsDrawMaterail.setQueryString(DRAW_MATETAIL_SQL);
      if(!jhlx.equals("1")){
        if(dsDrawMaterail.isOpen())
          dsDrawMaterail.refresh();
        else
          dsDrawMaterail.openDataSet();
        EngineDataSet detail = getDetailTable();
        detail.first();
        int tmpLoop2 = dsProcessDetail.getRowCount();
        for(int p=0; p<tmpLoop2; p++){
          /*
          //String wlxqjhmxid = dsProcessDetail.getValue("wlxqjhmxid");
          String dmsxid = dsProcessDetail.getValue("dmsxid");
          //String gxfdid = dsGxfdid.getValue("gxfdid");
          String T_dmsxid = null;
          if(dmsxid.equals(""))
            T_dmsxid =" AND b.dmsxid IS NULL";
          else
            T_dmsxid = " AND b.dmsxid ="+ dmsxid;
          */
          String jgdmxid = dsProcessDetail.getValue("jgdmxid");
          String cpid = dsProcessDetail.getValue("cpid");
          String sjbomid = (String)sjbomidList.get(p);
          double d_sl = dsProcessDetail.getValue("sl").length()>0 ? Double.parseDouble(dsProcessDetail.getValue("sl")) : 0;
          if(true){//如果任务单明细ID不为空，说明是通用计划下达下来的。就是实际BOM抽取物料
            String BOM_SQL = combineSQL(MATERAIL_LIST_SQL, "?", new String[]{sjbomid, gxfdid});
            dsFactBom.setQueryString(BOM_SQL);
            if(dsFactBom.isOpen())
              dsFactBom.refresh();
            else
              dsFactBom.openDataSet();
            dsFactBom.first();
            int tmpLoop3 = dsFactBom.getRowCount();
            String getWgclSQL = " SELECT nvl(t.wgcl,1) wgcl FROM sc_sjbom t, "
              + " ( select distinct k.sjbomid from sc_sjbom k "
              + "    where k.sjsjbomid = ? and k.cpid = ? "
              + "        and k.dmsxid = ? "
              + " ) l WHERE t.sjsjbomid = l.sjbomid ";
            for(int t=0; t<tmpLoop3; t++){
              String tmpdmsxid = dsFactBom.getValue("dmsxid");
              String tmpcpid = dsFactBom.getValue("cpid");
              String sjsjbomid = dsFactBom.getValue("sjsjbomid");
              String wgcl = dsFactBom.getValue("wgcl");
              String chxz = dsFactBom.getValue("chxz");
              String xql = dsFactBom.getValue("xql");
              String scxql = dsFactBom.getValue("scxql");
              String htid = dsFactBom.getValue("htid");
              String tmpSQL = combineSQL(getWgclSQL, "?", new String[]{sjsjbomid, tmpcpid, tmpdmsxid});
              if (!htid.equals("")) tmpSQL += " k.htid = " + htid;
              String tmpwgcl = dataSetProvider.getSequence(tmpSQL);
              tmpwgcl = tmpwgcl==null?"1":wgcl;
              //插入物料的时候判断它的工段与当前加工单主表的工段一样不一样.一样则插入
              //boolean isMaterialVariable = isMaterial(sjsjbomid, gxfdid);//判断是不是可以插入到物料清单中的物料
              //if ( isMaterialVariable ) {
              dsDrawMaterail.insertRow(false);
              dsDrawMaterail.setValue("jgdid", jgdid);
              String jgdwlid = dataSetProvider.getSequence("s_sc_jgdwl");
              dsDrawMaterail.setValue("jgdwlid", jgdwlid);
              dsDrawMaterail.setValue("dmsxid", tmpdmsxid);
              dsDrawMaterail.setValue("cpid", tmpcpid);
              dsDrawMaterail.setValue("sl", xql);
              dsDrawMaterail.setValue("scsl", scxql);
              dsDrawMaterail.setValue("jgdmxid", jgdmxid);
              dsDrawMaterail.setValue("wgcl", tmpwgcl);
              dsDrawMaterail.post();
              //}
              dsFactBom.next();
            }
          }
          //如果为空即该条加工单明细是手工增加的，要去BOM表里面抽取数据
          else{
            String BOMsql = combineSQL(BOM_MATERAIL, "?" , new String[]{cpid});
            dsBom.setQueryString(BOMsql);
            if(dsBom.isOpen())
              dsBom.refresh();
            else
              dsBom.openDataSet();
            dsBom.first();
            int tmpLoop4 = dsBom.getRowCount();
            for(int t=0; t<tmpLoop4; t++){
              double zjsl = dsBom.getValue("sl").length()>0 ? Double.parseDouble(dsBom.getValue("sl")) : 0;//生产该产品需要下级原料的数量
              double shl = dsBom.getValue("shl").length()>0 ? Double.parseDouble(dsBom.getValue("shl")) : 0;//生产该产品需要下级原料的数量
              double m_sl = d_sl*zjsl*(1+shl);
              dsDrawMaterail.insertRow(false);
              dsDrawMaterail.setValue("jgdid", jgdid);
              String jgdwlid = dataSetProvider.getSequence("s_sc_jgdwl");
              dsDrawMaterail.setValue("jgdwlid", jgdwlid);
              //dsDrawMaterail.setValue("dmsxid", dsBom.getValue("dmsxid"));
              String tmpcpid = dsBom.getValue("cpid");
              dsDrawMaterail.setValue("cpid", tmpcpid);
              dsDrawMaterail.setValue("sl", formatNumber(String.valueOf(m_sl), qtyFormat));
              dsDrawMaterail.setValue("scsl", formatNumber(String.valueOf(m_sl), qtyFormat));
              dsDrawMaterail.setValue("jgdmxid", jgdmxid);
              dsDrawMaterail.post();
              dsBom.next();
            }
          }
          detail.next();
        }
        dsDrawMaterail.saveChanges();
      }
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      ArrayList list = new ArrayList(d_RowInfos.size());
      String cpid=null, dmsxid=null, gylxid=null, unit=null, wlxqjhmxid=null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");
        gylxid = detailrow.get("gylxid");
        wlxqjhmxid = detailrow.get("wlxqjhmxid");
        StringBuffer buf = new StringBuffer().append(wlxqjhmxid).append(",").append(cpid).append(",").append(dmsxid).append(",").append(gylxid);
        unit = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(unit))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(unit);
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
        String csl = detailrow.get("csl");
        if(csl.length()>0 && (temp = checkNumber(csl, detailProducer.getFieldInfo("csl").getFieldname())) != null)
          return temp;
        String ksrq = detailrow.get("ksrq");
        if(ksrq.length() > 0 && !isDate(ksrq))
          return showJavaScript("alert('第"+row+"行非法开始日期！');");
        String wcrq = detailrow.get("wcrq");
        if(wcrq.length() > 0 && !isDate(wcrq))
          return showJavaScript("alert('第"+row+"行非法完成日期！');");
        if(!ksrq.equals("") && !wcrq.equals("")){
          java.sql.Date ksrqDate = java.sql.Date.valueOf(ksrq);
          java.sql.Date wcrqDate = java.sql.Date.valueOf(wcrq);
          if(wcrqDate.before(ksrqDate))
            return showJavaScript("alert('第"+row+"行开始日期不能大于完成日期')");
        }
      }
      return null;
    }
    /**
     * 校验从表表单信息从表输入的信息中是否全部是引入物料需求计划
     * @return true 只要有一条是引物料需求计划过来的，忽略手工增加信息。同样吧物料需求计划ID和生产计划ID保存入任务单主表
     * 如果全部是手工增加这不保存物料需求计划ID和生产计划ID到任务单主表
     * 手工增加的信息将的不到物料
     */
    private boolean  isAllMaterail()
    {
      boolean isAllMaterail = false;
      RowMap detail = null;
      dsProcessDetail.first();
      for(int i=0; i<dsProcessDetail.getRowCount(); i++)
      {
        String wlxqjhmxid = dsProcessDetail.getValue("wlxqjhmxid");
        if(!wlxqjhmxid.equals(""))
          return isAllMaterail=true;
      }
      return isAllMaterail;
    }
    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("rq");
      if(temp.equals(""))
        return showJavaScript("alert('日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法日期！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择车间！');");
      return null;
    }
  }

  /**
   * 添加到审核列表的操作类
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      String jhlx= dsMasterTable.getValue("jhlx");
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterTable.getValue("jhh");
      String deptid = dsMasterTable.getValue("deptid");
      if(jhlx.equals("1"))
        approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "produce_subplan", content,deptid);
      else
        approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "produce_plan", content,deptid);
    }
  }
  /**
   * 取消审核操作类
   */
  class Cancel_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      String jhlx = dsMasterTable.getValue("jhlx");
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      if(jhlx.equals("0"))
        approve.cancelAprove(dsMasterTable, dsMasterTable.getRow(), "produce_plan");
      else
        approve.cancelAprove(dsMasterTable, dsMasterTable.getRow(), "produce_subplan");
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());

      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      //校验表单数据
      String temp = checkMasterInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      /*
      temp = checkSubMrpDetail();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      */
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);

      //得到主表主键值
      //String scjhid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        scjhid = dataSetProvider.getSequence("s_sc_jh");
        String djh = rowInfo.get("djh");
        String jhh = rowInfo.get("jhh");
        String count = dataSetProvider.getSequence("select count(*) from sc_jh t where t.djh='"+djh+"'");
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('此单号已存在!')"));
          return;
        }
        ds.setValue("scjhid", scjhid);
        ds.setValue("djh",djh);
        ds.setValue("jhh",jhh);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
      }
      else
      {
        scjhid = ds.getValue("scjhid");
        String djh = rowInfo.get("djh");
        String sscjhid = dataSetProvider.getSequence("select scjhid from sc_jh t where t.djh='"+djh+"'");
        //String count = dataSetProvider.getSequence("select count(*) from sc_jh t where t.jhh='"+jhh+"'");
        if(sscjhid!=null&&!scjhid.equals(sscjhid))
        {
          data.setMessage(showJavaScript("alert('该单号已存在!')"));
          return;
        }
        ds.setValue("djh",djh);
      }

      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        long internalRow = Long.parseLong(detailrow.get("InternalRow"));
        detail.goToInternalRow(internalRow);

        //新添的记录
        if(isMasterAdd)
          detail.setValue("scjhid", scjhid);

        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("sl", detailrow.get("sl"));
        detail.setValue("hthwid", detailrow.get("hthwid"));
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("gylxid", detailrow.get("gylxid"));//
        detail.setValue("csl", detailrow.get("csl"));//超产率
        detail.setValue("ksrq", detailrow.get("ksrq"));
        detail.setValue("wcrq", detailrow.get("wcrq"));
        detail.setValue("jgyq", detailrow.get("jgyq"));//加工要求
        detail.setValue("scsl", detailrow.get("scsl"));//生产单位换算过后的生产数量
        detail.setValue("fdxs", detailrow.get("fdxs"));//分段序数
        detail.setValue("cpl", detailrow.get("cpl"));
        detail.setValue("sfsc", "0");
        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
      }
      /*
      EngineDataSet submrpdetail = getSubMrpTable();//生产分切计划物料数据集
      if(jhlx.equals("1")){
        putSubMrpInfo(data.getRequest());
        //保存分切计划物料的数据
        RowMap subMrpRow = null;
        int submrpCount = submrpdetail.getRowCount();
        submrpdetail.first();
        for(int k=0; k<submrpCount; k++)
        {
          subMrpRow = (RowMap)d_SubMrpRowInfos.get(k);//分切计划物料明细信息ArrayList
          //新添的记录
          if(isMasterAdd)
            submrpdetail.setValue("scjhid", scjhid);//如果是主表添加，设置生产计划ID为sequence得到的值。

          submrpdetail.setValue("sl", subMrpRow.get("sl"));
          submrpdetail.setValue("scsl", subMrpRow.get("scsl"));
          if(!isMasterAdd)
            submrpdetail.setValue("sfjs", "0");
          //保存用户自定义的字段
          FieldInfo[] fields = submrpProducer.getBakFieldCodes();
          for(int l=0; l<fields.length; l++)
          {
            String fieldCode = fields[l].getFieldcode();
            submrpdetail.setValue(fieldCode, subMrpRow.get(fieldCode));
          }
          submrpdetail.post();
          submrpdetail.next();
        }
      }*/
      //保存主表数据
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("jhrq", rowInfo.get("jhrq"));//计划日期
      ds.setValue("jhsm", rowInfo.get("jhsm"));//计划说明
      //ds.setValue("fdl", rowInfo.get("fdl"));//分段长度
      //ds.setValue("yxwc", rowInfo.get("yxwc"));//允许误差
      ds.setValue("jhlx", jhlx);
      ds.setValue("zsl", totalNum.toString());//总数量

      ds.setValue("wash", rowInfo.get("wash"));
      ds.setValue("pkgcl", rowInfo.get("pkgcl"));
      ds.setValue("pkgdmd", rowInfo.get("pkgdmd"));
      ds.setValue("htid", rowInfo.get("htid"));
      ds.setValue("brand", rowInfo.get("brand"));
      ds.setValue("jhwcrq", rowInfo.get("jhwcrq"));
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));
      ds.setValue("xm", rowInfo.get("xm"));
      ds.setValue("hm", rowInfo.get("hm"));
      ds.setValue("wfb", rowInfo.get("wfb"));
      ds.setValue("shprt", rowInfo.get("shprt"));  //装运港口
      ds.setValue("cq", rowInfo.get("cq"));      //船期

      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      rowInfo.put(dsMasterTable);

      if(String.valueOf(POST_CONTINUE).equals(action)){
        jhlx ="0";
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(SUBPLAN_POST_CONTINUE).equals(action))
      {
        jhlx="1";
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();//清空生产分切计划明细数据集
        //submrpdetail.empty();//清空生产分切计划物料数据集
        initRowInfo(false, true, true);//重新初始化从表的各行信息
        initSubRowInfo(true);
      }
      else if(String.valueOf(POST).equals(action))
      {
        isMasterAdd = false;
        //data.setMessage(showJavaScript("backList();"));
      }
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      ArrayList list = new ArrayList(d_RowInfos.size());
      String cpid=null, dmsxid=null, gylxid=null, unit=null, hthwid=null, fdxs=null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");
        fdxs = detailrow.get("fdxs");
        hthwid = detailrow.get("hthwid");//有可能相同的cpid和hthwid和dmsxid有可能在同一段分切
        StringBuffer buf = new StringBuffer().append(fdxs).append(",").append(hthwid).append(",").append(cpid).append(",").append(dmsxid);//.append(",").append(gylxid);
        //unit = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        //if(list.contains(unit) && jhlx.equals("0"))
        //return showJavaScript("alert('第"+row+"行产品重复');");
        //else
        //list.add(unit);
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        //if(sl.length()>0 && sl.equals("0"))
        //  return showJavaScript("alert('第"+row+"行数量不能为零！');");
        double d_sl= sl.length()>0 ? Double.parseDouble(sl) : 0;
        if(d_sl<0)
          return showJavaScript("alert('第"+row+"行数量不能小于零！');");
        String scsl = detailrow.get("sl");
        if((temp = checkNumber(scsl, "第"+row+"行生产数量")) != null)
          return temp;
        if(scsl.length()>0 && scsl.equals("0"))
          return showJavaScript("alert('第"+row+"行生产数量不能为零！');");
        double d_scsl= scsl.length()>0 ? Double.parseDouble(scsl) : 0;
        if(d_scsl<0)
          return showJavaScript("alert('第"+row+"行生产数量不能小于零！');");
        String csl = detailrow.get("csl");
        if(csl.length()>0 && (temp = checkNumber(csl, "第"+row+"行超产率")) != null)
          return temp;
        String ksrq = detailrow.get("ksrq");
        if(ksrq.length() > 0 && !isDate(ksrq))
          return showJavaScript("alert('第"+row+"行非法开始日期！');");
        String wcrq = detailrow.get("wcrq");
        if(wcrq.length() > 0 && !isDate(wcrq))
          return showJavaScript("alert('第"+row+"行非法完成日期！');");
        if(!ksrq.equals("") && !wcrq.equals("")){
          java.sql.Date ksrqDate = java.sql.Date.valueOf(ksrq);
          java.sql.Date wcrqDate = java.sql.Date.valueOf(wcrq);
          if(wcrqDate.before(ksrqDate))
            return showJavaScript("alert('第"+row+"行开始日期不能大于完成日期')");
        }
      }
      return null;
    }
    /**
     * 校验生产分切物料从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息

    private String checkSubMrpDetail()
    {
      if(!jhlx.equals("1"))
        return null;
      String temp = null;
      RowMap mrprow = null;
      for(int i=0; i<d_SubMrpRowInfos.size(); i++)
      {
        //dsSubMrpTable.goToRow(i);
        int row = i+1;
        mrprow = (RowMap)d_SubMrpRowInfos.get(i);
        String sl = mrprow.get("sl");
        //if(sl.equals(dsSubMrpTable.getValue("sl")))
        // mrprow.put("sfjs", "1");
        if((temp = checkNumber(sl, "物料数量", false))!=null)
          return temp;
        String scsl = mrprow.get("scsl");
        if((temp = checkNumber(scsl, "物料生产数量", false))!=null)
          return temp;
      }
      return null;
    }
         */
  }
  /**
   * 校验主表表表单信息从表输入的信息的正确性
   * @return null 表示没有信息,校验通过
   */
  private String checkMasterInfo()
  {
    RowMap rowInfo = getMasterRowinfo();
    String temp = rowInfo.get("jhrq");
    if(temp.equals(""))
      return showJavaScript("alert('计划日期不能为空！');");
    else if(!isDate(temp))
      return showJavaScript("alert('非法计划日期！');");
    temp = rowInfo.get("deptid");
    if(temp.equals(""))
      return showJavaScript("alert('请选择制定部门！');");
    temp = rowInfo.get("djh");
    if(temp.equals(""))
      return showJavaScript("alert('单号不能为空！');");
    return null;
  }
  /**
   * 生产分切计划主表保存操作的触发类
   * 页面上分切计划的信息保存到数据库，分切物料信息保存到数据库。
   * 保存后将分切生产计划明细表的分类汇总插入到生产计划明细表中

  class SubPlan_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putSubMrpInfo(data.getRequest());

      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      //校验表单数据
      String temp = checkMasterInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);

   //得到主表主键值
      String jhid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        jhid = dataSetProvider.getSequence("s_sc_jh");
        ds.setValue("scjhid", jhid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
      }
      //保存分切计划从表的数据
      RowMap subdetailrow = null;
      BigDecimal totalNum = new BigDecimal(0);
      EngineDataSet detail = getSubDetailTable();//生产分切计划明细数据集
      int count = detail.getRowCount();
      EngineDataSet planDetail = getDetailTable();//生产通用计划数据集
      String cpid=null, dmsxid=null, fdxs=null, sl =null, hthwid=null;
      BigDecimal b_sl=new BigDecimal(0), total = new BigDecimal(0), scsltotal = new BigDecimal(0);//保存数量的值，如果相同的代码属性和分段序数，把分切计划中的数量叠加
      Hashtable table = new Hashtable(count);//存放叠加数量的哈希表，key为代码属性加分段序数
      Hashtable scsltable = new Hashtable(count);//存放叠加生产数量的哈希表。
      EngineRow locatRow = new EngineRow(planDetail, new String[]{"hthwid", "dmsxid"});
      detail.first();
      for(int i=0; i<count; i++)//循环生产分切计划明细，并把保存有页面数据的d_SubRowInfo存入数据集里面
      {
        subdetailrow = (RowMap)d_SubRowInfos.get(i);//分切计划明细信息ArrayList强制转换为RowMap型，即哈希
        hthwid = subdetailrow.get("hthwid");
        cpid = subdetailrow.get("cpid");
        dmsxid = subdetailrow.get("dmsxid");
        fdxs = subdetailrow.get("fdxs");
        sl = subdetailrow.get("sl");
        b_sl = new BigDecimal(sl);
        String sxz = getPropertyBean(data.getRequest()).getLookupName(dmsxid);//得到规格属性
        String width = BasePublicClass.parseEspecialString(sxz, "宽度", "()");
        if(width.equals("0"))
        {
          data.setMessage(showJavaScript("alert('不能存在规格属性没有宽度的物资参与分切')"));
          return;
        }
        double d_wid = width.equals("0") ? 1 :  Double.parseDouble(width);
        double d_sl = sl.length()>0 ? Double.parseDouble(sl) : 0;
        String scdwgs = subdetailrow.get("scdwgs");
        double d_scdwgs = scdwgs.length()>0 ? Double.parseDouble(scdwgs) : 1;
        String scsl = String.valueOf(d_wid==0 ? 0 : d_sl*d_scdwgs/d_wid);
        BigDecimal b_scsl = new BigDecimal(scsl);


        StringBuffer buf = new StringBuffer().append(dmsxid).append(",").append(fdxs);
        String s = buf.toString();
        total = (BigDecimal)table.get(s);
        if(total==null){//如果该规格属性和分段序数在哈希表的key中没有，就推入当前数量
          total = b_sl;
        }
        else{//如果存在相同的该规格属性和分段序数就推入当前数量和哈希表中的数量之和
          total = b_sl.add(total);
        }
        table.put(s, total.toString());
        scsltotal = (BigDecimal)scsltable.get(s);
        if(scsltotal==null)
          scsltotal = b_scsl;
        else
          scsltotal = scsltotal.add(b_scsl);
        scsltable.put(s, scsltotal.toString());
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        /**
         * 整合生产分切计划明细把相同规格属性和分段序数的行纪录合并，并且保存到生产通用计划明细中

        locatRow.setValue(0, hthwid);
        locatRow.setValue(1, dmsxid);
        if(planDetail.locate(locatRow, Locate.FIRST)){//在生产通用计划中定位到了，执行一下语句，不插入
          planDetail.setValue("cpid", cpid);
          planDetail.setValue("sl", detail.getValue("sl"));
          planDetail.setValue("hthwid", detail.getValue("hthwid"));
          planDetail.setValue("dmsxid", dmsxid);
          planDetail.setValue("gylxid", detail.getValue("gylxid"));//
          planDetail.setValue("jgyq", detail.getValue("jgyq"));//加工要求
          planDetail.setValue("sl", total.toString());
          planDetail.setValue("scsl", scsltotal.toString());
          planDetail.setValue("sfsc", "0");
        }
        else{//通用计划中没有定位到，插入一行纪录
          planDetail.insertRow(false);
          planDetail.setValue("scjhid", isMasterAdd ? jhid : scjhid);//如果是主表添加，设置生产计划ID为sequence得到的值。
          planDetail.setValue("scjhmxid", "-1");
          planDetail.setValue("cpid", cpid);
          planDetail.setValue("sl", detail.getValue("sl"));
          planDetail.setValue("scsl", String.valueOf(d_wid==0 ? 0 : 1000*d_sl*d_scdwgs/d_wid));//生产计划明细中也要保存生产数量
          planDetail.setValue("hthwid", detail.getValue("hthwid"));
          planDetail.setValue("dmsxid", dmsxid);
          planDetail.setValue("gylxid", detail.getValue("gylxid"));//
          //detail.setValue("ksrq", today);
          //detail.setValue("wcrq", today);
          planDetail.setValue("sfsc", "0");//生产计划明细中把是否生成实际BOM设置为零。主要是为了如果分切物料修改后实际BOM也要变
          planDetail.setValue("jgyq", detail.getValue("jgyq"));//加工要求
          //保存用户自定义的字段
          FieldInfo[] fields = subdetailProducer.getBakFieldCodes();
          for(int j=0; j<fields.length; j++)
          {
            String fieldCode = fields[j].getFieldcode();
            planDetail.setValue(fieldCode, subdetailrow.get(fieldCode));
          }
        }
        planDetail.post();
        //新添的记录,保存分切计划明细数据到数据集
        if(isMasterAdd)
          detail.setValue("scjhid", jhid);

        detail.setValue("cpid", cpid);
        detail.setValue("sl", subdetailrow.get("sl"));
        detail.setValue("hthwid", subdetailrow.get("hthwid"));
        detail.setValue("dmsxid", dmsxid);
        detail.setValue("gylxid", subdetailrow.get("gylxid"));//
        detail.setValue("scsl",  String.valueOf(d_wid==0 ? 0 : 1000*d_sl*d_scdwgs/d_wid) );//生产数量
        detail.setValue("fdxs", fdxs);//分段序数
        detail.setValue("jgyq", subdetailrow.get("jgyq"));//加工要求
        //保存用户自定义的字段
        FieldInfo[] fields = subdetailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, subdetailrow.get(fieldCode));
        }
        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        detail.next();
      }
      //保存分切计划物料的数据
      RowMap subMrpRow = null;
      EngineDataSet submrpdetail = getSubMrpTable();//生产分切计划物料数据集
      int submrpCount = submrpdetail.getRowCount();
      submrpdetail.first();
      for(int i=0; i<submrpCount; i++)
      {
        subMrpRow = (RowMap)d_SubMrpRowInfos.get(i);//分切计划物料明细信息ArrayList
        //新添的记录
        submrpdetail.setValue("scjhid", isMasterAdd ? jhid : scjhid);//如果是主表添加，设置生产计划ID为sequence得到的值。

        submrpdetail.setValue("sl", subMrpRow.get("sl"));
        submrpdetail.setValue("scsl", subMrpRow.get("scsl"));
        //保存用户自定义的字段
        FieldInfo[] fields = submrpProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          submrpdetail.setValue(fieldCode, subMrpRow.get(fieldCode));
        }
        submrpdetail.post();
        submrpdetail.next();
      }

      //保存主表数据
      ds.setValue("jhlx", "1");
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("jhrq", rowInfo.get("jhrq"));//计划日期
      ds.setValue("jhsm", rowInfo.get("jhsm"));//计划说明
      ds.setValue("ztms", rowInfo.get("ztms"));//状态描述
      ds.setValue("zsl", totalNum.toString());//总数量
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail, submrpdetail, planDetail}, null);
      rowInfo.put(dsMasterTable);

      if(String.valueOf(SUBPLAN_POST_CONTINUE).equals(action)){
        jhlx="1";
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();//清空生产分切计划明细数据集
        submrpdetail.empty();//清空生产分切计划物料数据集
        initRowInfo(false, true, true);//重新初始化从表的各行信息
        initSubRowInfo(false, true);
        initSubRowInfo(true, true);
      }
      else if(String.valueOf(SUBPLAN_POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息

    private String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('生产分切计划明细不能保存空的数据')");
      ArrayList list = new ArrayList(d_RowInfos.size());
      String cpid=null, dmsxid=null, gylxid=null, unit=null, hthwid=null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");
        hthwid = detailrow.get("hthwid");
        StringBuffer buf = new StringBuffer().append(hthwid).append(",").append(cpid).append(",").append(dmsxid);//.append(",").append(gylxid);
        unit = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(unit))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(unit);
        String sl = detailrow.get("sl");
        double d_sl= sl.length()>0 ? Double.parseDouble(sl) : 0;
        if((temp = checkNumber(sl, "第"+row+"行数量", false)) != null)
          return temp;
        if(d_sl<0)
          return showJavaScript("alert('第"+row+"行数量不能小于零！');");
      }
      return null;
    }
  }
  */

 /**
  * 主表删除操作
  */
  class Master_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(isMasterAdd){
        data.setMessage(showJavaScript("backList();"));
        return;
      }
      boolean isSubDel = String.valueOf(SUBMASTER_DEL).equals(action);
      EngineDataSet ds = getMaterTable();
      ds.goToInternalRow(masterRow);
      String sql = getWhereIn(dsDetailTable, "scjhmxid", null);
      sql = combineSQL(DELETESJ_BOM_SQL, "?", new String[]{sql});
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      if(isSubDel){
        dsSubMrpTable.deleteAllRows();
        dsDeleteFactBom.updateQuery(new String[]{sql});
        ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable,dsSubMrpTable}, null);
        d_SubMrpRowInfos.clear();
      }
      else
      {
        dsDeleteFactBom.updateQuery(new String[]{sql});
        ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      }
      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   *  查询操作
   */
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      searchTable.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = searchTable.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), fgsid, SQL});
      if(!dsMasterTable.getQueryString().equals(SQL))
      {
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);
      }
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsMasterTable;
      EngineDataSet detail = dsMasterTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("jhh"), null, null, null),
        new QueryColumn(master.getColumn("jhrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("jhrq"), null, null, null, "b", "<="),
        new QueryColumn(detail.getColumn("scjhid"), "sc_jhmx", "scjhid","ksrq", "a", ">="),
        new QueryColumn(detail.getColumn("scjhid"), "sc_jhmx", "scjhid", "ksrq", "b", "<="),
        new QueryColumn(detail.getColumn("scjhid"), "sc_jhmx", "scjhid", "wcrq", "c", ">="),
        new QueryColumn(detail.getColumn("scjhid"), "sc_jhmx", "scjhid","wcrq", "d", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        new QueryColumn(master.getColumn("scjhid"), "sc_jhmx", "scjhid", "cpid", null, "="),//从表品名
        new QueryColumn(master.getColumn("scjhid"), "VW_SCJH_QUERY", "scjhid", "cpbm", "cpbm", "like"),//从表产品编码
        new QueryColumn(master.getColumn("scjhid"), "VW_SCJH_QUERY", "scjhid", "product", "product", "like"),//从表品名
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }

  /**
   *  生产通用计划根据当前库存量从表增加操作
   */

  class Detail_Select_Store implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String storage = m_RowInfo.get("storage");
      if(storage.length() == 0)
        return;

      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[]{"cpid","dmsxid"});
      String[] cpIDs = parseString(storage,",");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String scjhid = dsMasterTable.getValue("scjhid");
      for(int i=0; i < cpIDs.length; i++)
      {
        if(cpIDs[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        //locateGoodsRow.setValue(0,cpIDs[i]);
        //if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        //{
        RowMap selectStoreRow = getSelectStoreGoodsBean(req).getLookupRow(cpIDs[i]);
        long ztqq = selectStoreRow.get("ztqq").length()>0 ? Long.parseLong(selectStoreRow.get("ztqq")) : 0;//总提前期
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("scjhmxid", "-1");
        dsDetailTable.setValue("cpid",cpIDs[i]);
        String cesl = selectStoreRow.get("cesl");//差额数量等于最大库存量减去库存可供量
        double scsl = cesl.length()>0 ? Double.parseDouble(cesl) : 0;
        dsDetailTable.setValue("sl", scsl<0 ? "0" : cesl);
        Date startdate = new Date();
        Date enddate = new Date(startdate.getTime() + ztqq*60*60*1000);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(startdate);
        String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
        String ksrq = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String cpid = cpIDs[i];
        String dmsxid = selectStoreRow.get("dmsxid");
        String wcrq = getScjhWcrq(cpid, dmsxid);
        dsDetailTable.setValue("ksrq", ksrq);
        dsDetailTable.setValue("wcrq", wcrq);
        dsDetailTable.setValue("dmsxid", selectStoreRow.get("dmsxid"));
        dsDetailTable.setValue("scjhid", isMasterAdd ? "-1" : scjhid);
        dsDetailTable.post();
        //创建一个与用户相对应的行
        detailrow = new RowMap(dsDetailTable);
        detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
        d_RowInfos.add(detailrow);
        //}
      }
    }
  }
  /**
   *  生产通用计划，根据销售合同从表增加操作
   */

  class Detail_Select_Sale implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(req);
      if(jhlx.equals("1"))
        putSubMrpInfo(req);
      RowMap rowInfo = getMasterRowinfo();

      String selmethod = m_RowInfo.get("selmethod");
      String importorder = m_RowInfo.get("importorder");
      if(importorder.length() == 0)
        return;

      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwid");
      String[] hthwID = parseString(importorder,",");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String scjhid = dsMasterTable.getValue("scjhid");
      if(jhlx.equals("1")){
        if(minwidthTable==null)
          minwidthTable = new Hashtable(hthwID.length+1);
        else
          minwidthTable.clear();
      }
      for(int i=0; i < hthwID.length; i++)
      {
        if(hthwID[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        locateGoodsRow.setValue(0,hthwID[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap selectSaleRow = getSelectSaleOrderBean(req).getLookupRow(hthwID[i]);
          String jsrq = selectSaleRow.get("jsrq");
          //long ztqq = selectSaleRow.get("ztqq").length()>0 ? Long.parseLong(selectSaleRow.get("ztqq")) : 0;//总提前期
          double wjhsl = selectSaleRow.get("wjhsl").length()>0 ? Double.parseDouble(selectSaleRow.get("wjhsl")): 0;//合同未开计划数量
          double jhkgl = selectSaleRow.get("jhkgl").length()>0 ? Double.parseDouble(selectSaleRow.get("jhkgl")): 0;//计划可供量
          if(jhkgl < 0)
            jhkgl = 0;//如果计划可供量下于零的时候设置为零
          double minsl = selectSaleRow.get("minsl").length()>0 ? Double.parseDouble(selectSaleRow.get("minsl")): 0;//最低库存量
          double total = 0;
          String cpid=selectSaleRow.get("cpid");
          String dmsxid = selectSaleRow.get("dmsxid");
          String sxz = selectSaleRow.get("sxz");
          String width = BasePublicClass.parseEspecialString(sxz,SYS_PRODUCT_SPEC_PROP,"()");//解析属性直字符串
          if(jhlx.equals("1")){
            RowMap row = null;
            for(int j=0; j < d_RowInfos.size(); j++)
            {
              row = (RowMap)d_RowInfos.get(j);
              String id=row.get("cpid");
              //把产品ID推入到ArrayList里面，如果有不同的CPID将return.判断是否有不同的产品参与分切
              if(!cpid.equals(id))
              {
                data.setMessage(showJavaScript("alert('有不同的产品参与分切，请重新选择')"));
                return;
              }
              else
                continue;
            }
            if(width.equals("0"))
            {
              data.setMessage(showJavaScript("alert('不能存在规格属性没有"+SYS_PRODUCT_SPEC_PROP+"的物资参与分切')"));
              return;
            }
          }
          for(int j=0;j<d_RowInfos.size();j++)
          {
            RowMap detail = (RowMap)d_RowInfos.get(j);
            if(cpid.equals(detail.get("cpid")) && dmsxid.equals(detail.get("dmsxid")))
            {
              String sl0 = detail.get("sl");
              total += isDouble(sl0) ? Double.parseDouble(sl0) : 0;
            }
          }//如果选择的产品数量和从表数据里的产品相同，把已经put到从表里的计划数量叠加不包括所选择产品的数量。
          //如果所选择产品已经安排数量大于计划可供量设置零时数量为零，否则等于叠加量减去计划可供量
          double testsl= (total-jhkgl)>0 ? 0 : total-jhkgl;
          double scjhl = 0;//临时数量
          if(selmethod.equals("0")){
            //如果未计划数量加上测试数量小于零说明还有可供量，所以不需生产。如果大于零两者相加
            scjhl= (testsl+wjhsl)<0 ? 0 : (testsl + wjhsl);
            if(scjhl==0)//如果计划可供量大于叠加销售合同量，跳出本次循环，继续循环下面的选择
              continue;
          }
          else if(selmethod.equals("1")){
            scjhl=(testsl+wjhsl+minsl)<0 ? 0 : (testsl+wjhsl+minsl);
            if(scjhl==0)//如果计划可供量大于叠加销售合同量，跳出本次循环，继续循环下面的选择
              continue;
          }
          else if(selmethod.equals("2")){
            scjhl = wjhsl;
          }
          String scdwgs = selectSaleRow.get("scdwgs");//从物资编码表中得到生产用公式

          double gs = scdwgs.length()>0 ? Double.parseDouble(scdwgs) : 1;
          double wid = width.equals("0") ? 1 : Double.parseDouble(width);
          double scysl = width.equals("0") ? scjhl : scjhl*gs/wid;
          dsDetailTable.insertRow(false);
          if(jhlx.equals("1"))
            minwidthTable.put(dmsxid, new BigDecimal(width));
          dsDetailTable.setValue("scjhmxid", "-1");
          dsDetailTable.setValue("hthwid",hthwID[i]);
          dsDetailTable.setValue("cpid",selectSaleRow.get("cpid"));
          dsDetailTable.setValue("sl", String.valueOf(scjhl));
          dsDetailTable.setValue("scsl", formatNumber(String.valueOf(scysl), qtyFormat));
          dsDetailTable.setValue("dmsxid", selectSaleRow.get("dmsxid"));
          /*Date startdate = new Date();
          Date end = new Date(startdate.getTime() + ztqq*60*60*1000);
          */
          String ksrq = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
          //String today = new SimpleDateFormat("yyyy-MM-dd").format(startdate);//生产计划开始日期为当前日期
          //String endDate = new SimpleDateFormat("yyyy-MM-dd").format(end);//当前日期加上总提前期
          //java.sql.Date jsrqDtae = java.sql.Date.valueOf(jsrq);
          //java.sql.Date rq = java.sql.Date.valueOf(endDate);
          dsDetailTable.setValue("ksrq", ksrq);
          String wcrq = getScjhWcrq(cpid, dmsxid);
          dsDetailTable.setValue("wcrq", wcrq);//selectSaleRow.get("jhrq") 开始日期加上总提前期小于结束日期，用开始日期加总提前期。否则用结束日期
          dsDetailTable.setValue("jgyq", selectSaleRow.get("bz"));
          dsDetailTable.setValue("scjhid", isMasterAdd ? "-1" : scjhid);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
          d_RowInfos.add(detailrow);
        }
      }
      if(jhlx.equals("1"))
        minWidth = BasePublicClass.getMinString(minwidthTable);
    }
  }
  /**
   *  生产分切计划，根据销售合同从表增加以后，手工输入分段序数。
   *  根据每行的分段序数从小到大进行排序
   */

  class SubPlan_Taxis implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(req);
      putSubMrpInfo(req);
      EngineDataSet dsDetail = getDetailTable();//得到分切计划从表数据集
      RowMap subrows =null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        subrows = (RowMap)d_RowInfos.get(i);
        subDetailRow = Long.parseLong(subrows.get("InternalRow"));
        dsDetail.goToInternalRow(subDetailRow);
        String fdxs = subrows.get("fdxs");
        String dmsxid = subrows.get("dmsxid");
        if(fdxs.equals("")){
          data.setMessage(showJavaScript("alert('不能有空的分段序数')"));
          return;
        }
        String temp = null;
        if(fdxs.length()>0 && (temp = checkInt(fdxs,"分段序数")) !=null)
        {
          data.setMessage(temp);
          return;
        }
        dsDetail.setValue("cpid", subrows.get("cpid"));
        dsDetail.setValue("sl", subrows.get("sl"));
        dsDetail.setValue("hthwid", subrows.get("hthwid"));
        dsDetail.setValue("dmsxid", dmsxid);
        dsDetail.setValue("gylxid", subrows.get("gylxid"));//
        dsDetail.setValue("fdxs", fdxs);//分段序数
        dsDetail.setValue("fdxh", subrows.get("fdxh"));
        dsDetail.setValue("scsl", subrows.get("scsl"));//生产数量
        dsDetail.setValue("jgyq", subrows.get("jgyq"));//加工要求
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          dsDetail.setValue(fieldCode, subrows.get(fieldCode));
        }
        dsDetail.post();
        dsDetail.next();
      }
      initRowInfo(false, false, true);
    }
  }


  /**
   *  生产通用计划选择合同主表从表信息全部引入触发操作的类
   */
  class Muti_Order_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowinfo = getMasterRowinfo();

      String selmethod = m_RowInfo.get("selmethod");
      String mutiSaleOrder = rowinfo.get("mutiSaleOrder");
      if(mutiSaleOrder.length()==0)
        return;
      String[] htID = parseString(mutiSaleOrder,",");
      for(int k=0; k < htID.length; k++)
      {
        if(htID[k].equals("-1"))
          continue;
        String htid = htID[k];
        String SQL = combineSQL(ORDER_DETAIL_SQL, "?", new String[]{htid});
        EngineDataSet saleOrderData = null;
        if(saleOrderData==null)
        {
          saleOrderData = new EngineDataSet();
          setDataSetProperty(saleOrderData,null);
        }
        saleOrderData.setQueryString(SQL);
        if(!saleOrderData.isOpen())
          saleOrderData.openDataSet();
        else
          saleOrderData.refresh();
        //实例化查找数据集的类
        EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwid");
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String scjhid = dsMasterTable.getValue("scjhid");
        for(int i=0; i < saleOrderData.getRowCount(); i++)
        {
          saleOrderData.goToRow(i);
          String hthwid = saleOrderData.getValue("hthwid");

          locateGoodsRow.setValue(0, hthwid);
          if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
          {
            double sl  = saleOrderData.getValue("sl").length() > 0 ? Double.parseDouble(saleOrderData.getValue("sl")) : 0;//数量
            double yjhsl = saleOrderData.getValue("yjhsl").length() > 0 ? Double.parseDouble(saleOrderData.getValue("yjhsl")) : 0;//已计划数量
            double wjhsl = sl-yjhsl;
            String cpid = saleOrderData.getValue("cpid");
            String dmsxid = saleOrderData.getValue("dmsxid");
            RowMap productRow = getProductBean(req).getLookupRow(cpid);
            String scdwgs = productRow.get("scdwgs");
            String sxz = getPropertyBean(data.getRequest()).getLookupName(dmsxid);
            String wid = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
            double jhkgl = saleOrderData.getValue("jhkgl").length()>0 ? Double.parseDouble(saleOrderData.getValue("jhkgl")): 0;//计划可供量
            if(jhkgl>0)
              jhkgl=0;
            double minsl = productRow.get("minsl").length()>0 ? Double.parseDouble(productRow.get("minsl")): 0;//最低库存量
            long  ztqq = productRow.get("ztqq").length()>0 ? Long.parseLong(productRow.get("ztqq")) : 0;//总提前期
            double total = 0;
            for(int j=0;j<d_RowInfos.size();j++)
            {
              RowMap detail = (RowMap)d_RowInfos.get(j);
              if(cpid.equals(detail.get("cpid")) && dmsxid.equals("dmsxid"))
              {
                String sl0 = detail.get("sl");
                total += isDouble(sl0) ? Double.parseDouble(sl0) : 0;
              }
            }//如果选择的产品数量和从表数据里的产品相同，把从表的计划数量叠加不包括所选择产品的数量。
            //如果所选择产品已经安排数量大于计划可供量时设置零时数量为零，否则等于叠加量减去计划可供量
            double testsl= (total-jhkgl)>0 ? 0 : total-jhkgl;
            double scjhl = 0;//临时数量
            if(selmethod.equals("0")){
              //如果未计划数量加上测试数量小于零说明还有可供量，所以不需生产。如果大于零两者相加
              scjhl= (testsl+wjhsl)<0 ? 0 : (testsl + wjhsl);
              if(scjhl==0)//如果计划可供量大于叠加销售合同量，跳出本次循环，继续循环下面的选择
                continue;
            }
            else if(selmethod.equals("1")){
              scjhl=(testsl+wjhsl+minsl)<0 ? 0 : (testsl+wjhsl+minsl);
              if(scjhl==0)//如果计划可供量大于叠加销售合同量，跳出本次循环，继续循环下面的选择
                continue;
            }
            else if(selmethod.equals("2")){
              scjhl = wjhsl;
            }
            double d_gs = scdwgs.length()>0 ? Double.parseDouble(scdwgs) : 1;//生产公式值
            double d_width = wid.equals("0") ? 1 : Double.parseDouble(wid);//规格属性宽度，如果没有宽度则为1
            double scdwsl = 0;
            if(wid.equals("0"))
              scdwsl = scjhl;
            else
              scdwsl = scjhl*d_gs/d_width;
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("scjhmxid", "-1");
            dsDetailTable.setValue("scjhid", isMasterAdd ? "" : scjhid);
            dsDetailTable.setValue("hthwid", hthwid);
            dsDetailTable.setValue("cpid", cpid);
            dsDetailTable.setValue("sl", String.valueOf(scjhl));
            dsDetailTable.setValue("scsl", formatNumber(String.valueOf(scdwsl), qtyFormat));
            Date startdate = new Date();
            Date end = new Date(startdate.getTime() + ztqq*60*60*1000);
            String today = new SimpleDateFormat("yyyy-MM-dd").format(startdate);
            String endDate = new SimpleDateFormat("yyyy-MM-dd").format(end);
            String ksrq = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String wcrq = getScjhWcrq(cpid, dmsxid);
            //String jsrq = saleOrderData.getValue("jsrq");
            //java.sql.Date jsrqDtae = java.sql.Date.valueOf(jsrq);
            java.sql.Date rq = java.sql.Date.valueOf(endDate);
            dsDetailTable.setValue("ksrq", ksrq);
            dsDetailTable.setValue("wcrq", wcrq);//saleOrderData.getValue("jhrq")
            dsDetailTable.setValue("dmsxid", saleOrderData.getValue("dmsxid"));
            dsDetailTable.setValue("jgyq", saleOrderData.getValue("bz"));
            dsDetailTable.post();
            //创建一个与用户相对应的行
            RowMap detailrow = new RowMap(dsDetailTable);
            detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
            d_RowInfos.add(detailrow);
          }
        }
      }
    }
  }



  /**
 *  生产计划单选择合同主表从表信息过来触发操作的类
 */
class Single_Order_Add implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest req = data.getRequest();
    //保存输入的明细信息
    putDetailInfo(data.getRequest());
    RowMap rowinfo = getMasterRowinfo();

    String singleOrder = rowinfo.get("singleOrder");
    if(singleOrder.equals(""))
      return;
    String SQL=null;
    if(jhlx.equals("0"))
    {
      SQL = combineSQL(ORDER_DETAIL_SQL, "?", new String[]{singleOrder});
    }
    else if(jhlx.equals("1"))
    {
      SQL = combineSQL(ORDER_DETAIL_SQL_WM, "?", new String[]{singleOrder});
    }
    EngineDataSet processData = null;
    if(processData==null)
    {
      processData = new EngineDataSet();
      setDataSetProperty(processData,null);
    }
    processData.setQueryString(SQL);
    if(!processData.isOpen())
      processData.openDataSet();
    else
      processData.refresh();
    RowMap singleRow = getOrderMasterBean(req).getLookupRow(singleOrder);//采购合同主表一行信息
    String ddd=singleRow.get("brandid");
    rowinfo.put("khlx", singleRow.get("khlx"));  //客户类型
    rowinfo.put("brand", singleRow.get("brandid"));//商标id
    rowinfo.put("xm", singleRow.get("xm"));      //商标id
    rowinfo.put("dwtxid", singleRow.get("dwtxid"));//商标id
    rowinfo.put("wash", singleRow.get("wash"));//水洗标
    rowinfo.put("pkgcl", singleRow.get("pkgcl"));//包编布颜色
    rowinfo.put("pkgdmd", singleRow.get("pkgdmd"));//包装要求
    rowinfo.put("htid", singleRow.get("htid"));//合同id
    rowinfo.put("shprt", singleRow.get("shprt")); //装运港口
    rowinfo.put("cq", singleRow.get("cq"));      //船期
    rowinfo.put("jhwcrq", singleRow.get("jhfhrq"));
    rowinfo.put("jhsm", singleRow.get("qtxx")+"     印花规格:           成品规格:        坯布配比:");
    rowinfo.put("hm", singleRow.get("hm"));
    rowinfo.put("wfb", singleRow.get("wfb"));
    //实例化查找数据集的类
    EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwid");
    if(!isMasterAdd)
      dsMasterTable.goToInternalRow(masterRow);
    String scjhid = dsMasterTable.getValue("scjhid");
    for(int i=0; i < processData.getRowCount(); i++)
    {
      processData.goToRow(i);
      String hthwid = processData.getValue("hthwid");
      locateGoodsRow.setValue(0, hthwid);
      if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
      {
        double sjjhl=0, sl=0, wjhl=0,dj=0;//实际入库量， 合同数量， 未进货量,金额
        //sjjhl = processData.getValue("sjjhl").length() > 0 ? Double.parseDouble(processData.getValue("sjjhl")) : 0;//实际进货数量
        sl  = processData.getValue("sl").length() > 0 ? Double.parseDouble(processData.getValue("sl")) : 0;//数量
        //dj  = processData.getValue("dj").length() > 0 ? Double.parseDouble(processData.getValue("dj")) : 0;//单价
        //wjhl = sjjhl-sl >0 ? 0 : sl-sjjhl;
        //if(wjhl==0)
        //  continue;
        dsDetailTable.insertRow(false);
        String cpid  = processData.getValue("cpid");
        RowMap productRow = getProductBean(req).getLookupRow(cpid);

        double hsbl = productRow.get("hsbl").length() > 0 ? Double.parseDouble(productRow.get("hsbl")) : 0;//数量
        dsDetailTable.setValue("scjhmxid", "-1");
        dsDetailTable.setValue("scjhid", isMasterAdd ? "" : scjhid);
        dsDetailTable.setValue("hthwid", hthwid);
        dsDetailTable.setValue("cpid", cpid);
        // if(bjfs.equals("1"))
        //   dsDetailTable.setValue("hssl", String.valueOf(wjhl));
        //  else
           dsDetailTable.setValue("sl", String.valueOf(sl));
        //dsDetailTable.setValue("jhrq", processData.getValue("jhrq"));//交货日期
        //dsDetailTable.setValue("dj", processData.getValue("dj"));
        String d=processData.getValue("dmsxid");
        dsDetailTable.setValue("dmsxid", processData.getValue("dmsxid"));

        dsDetailTable.post();
        //创建一个与用户相对应的行
        RowMap detailrow = new RowMap(dsDetailTable);
        detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
        d_RowInfos.add(detailrow);
      }
    }
  }
 }



  /**
   * 从表增加操作,单选产品
   */
  class Single_Product_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      int row = Integer.parseInt(data.getParameter("rownum"));
      String singleIdInput = m_RowInfo.get("singleIdInput_"+row);
      if(singleIdInput.equals(""))
        return;

      //实例化查找数据集的类
      String cpid = singleIdInput;
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cpid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String scjhid = dsMasterTable.getValue("scjhid");
      dsDetailTable.goToRow(row);
      RowMap detailrow = null;
      detailrow = (RowMap)d_RowInfos.get(row);
      locateGoodsRow.setValue(0, cpid);
      if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
      {
        RowMap productRow = getProductBean(req).getLookupRow(cpid);
        long ztqq = productRow.get("ztqq").length()>0 ? Long.parseLong(productRow.get("ztqq")) : 0;//总提前期
        detailrow.put("scjhmxid", "-1");
        detailrow.put("cpid", cpid);
        Date startdate = new Date();
        Date enddate = new Date(startdate.getTime() + ztqq*60*60*1000);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(startdate);
        String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
        detailrow.put("ksrq", today);
        detailrow.put("wcrq", endDate);
        detailrow.put("scjhid", isMasterAdd ? "" : scjhid);
      }
    }
  }
  /**
   *  从表增加操作(增加一个空白行)
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(req);     //保存输入的明细信息
      EngineDataSet ds = getMaterTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String scjhid = dsMasterTable.getValue("scjhid");

      EngineDataSet detail = getDetailTable();
      isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
      detail.insertRow(false);//插入一行数据
      detail.setValue("scjhid", isMasterAdd ? "-1" : scjhid);
      detail.post();
      RowMap detailrow = new RowMap(detail);
      detailrow.put("InternalRow", String.valueOf(detail.getInternalRow()));
      d_RowInfos.add(detailrow);//增加一个空白行
    }
  }
  /**
   *  生产分切计划从表增加操作(增加一个空白行), 如果有数据增加一行列表相同的产品。如果没有数据增加空白行

  class SubDetail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(req);     //保存输入的明细信息
      putSubMrpInfo(req);
      EngineDataSet ds = getMaterTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String scjhid = dsMasterTable.getValue("scjhid");

      EngineDataSet detail = getDetailTable();
      RowMap rowinfo = null;
      String cpid =null;
      detail.insertRow(false);//插入一行数据
      detail.setValue("scjhid", isMasterAdd ? "-1" : scjhid);
      if(d_RowInfos.size()>0){
        rowinfo = (RowMap)d_RowInfos.get(0);
        cpid = rowinfo.get("cpid");
        detail.setValue("cpid", cpid);
      }
      detail.post();
      RowMap detailrow = new RowMap(detail);
      detailrow.put("InternalRow", String.valueOf(detail.getInternalRow()));
      d_RowInfos.add(detailrow);
    }
  }
   */
  /**
   *分切计划复制一行操作
   *

  class SubDetail_Copy implements Obactioner
  {
    //----Implementation of the Obactioner interface
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);
      putSubMrpInfo(request);
      EngineDataSet ds = getMaterTable();
      EngineDataSet detail = getDetailTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String scjhid = dsMasterTable.getValue("scjhid");
      RowMap rowinfo = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        rowinfo = (RowMap)d_RowInfos.get(i);
        subDetailRow = Long.parseLong(rowinfo.get("InternalRow"));
        detail.goToInternalRow(subDetailRow);
        detail.setValue("cpid", rowinfo.get("cpid"));
        detail.setValue("sl", rowinfo.get("sl"));
        detail.setValue("dmsxid", rowinfo.get("dmsxid"));
        detail.setValue("jgyq", rowinfo.get("jgyq"));
        detail.setValue("scsl", rowinfo.get("scsl"));
        detail.setValue("fdxs", rowinfo.get("fdxs"));
        detail.setValue("fdxh", rowinfo.get("fdxh"));
        detail.post();
      }
      int num = Integer.parseInt(data.getParameter("rownum"));
      RowMap temprow  = (RowMap)d_RowInfos.get(num);
      detail.goToInternalRow(Long.parseLong(temprow.get("InternalRow")));
      String cpid = detail.getValue("cpid");
      String sl = detail.getValue("sl");
      String dmsxid = detail.getValue("dmsxid");
      String jgyq = detail.getValue("jgyq");
      String scsl = detail.getValue("scsl");
      String fdxs = detail.getValue("fdxs");
      String fdxh = detail.getValue("fdxh");
      String hthwid = detail.getValue("hthwid");
      RowMap masterrow = getMasterRowinfo();
      String tCopyNumber = request.getParameter("tCopyNumber");
      int copyNum= (tCopyNumber==null || tCopyNumber.equals("0")) ? 1 : Integer.parseInt(tCopyNumber);
      for(int j=0; j<copyNum; j++){
        detail.insertRow(false);
        detail.setValue("scjhmxid","-1");
        detail.setValue("scjhid", isMasterAdd ? "-1" : scjhid);
        detail.setValue("cpid", cpid);
        detail.setValue("sl", sl);
        detail.setValue("dmsxid",dmsxid);
        detail.setValue("scsl", scsl);
        detail.setValue("jgyq",jgyq);
        detail.setValue("fdxs", fdxs);
        detail.setValue("fdxh", fdxh);
        detail.setValue("hthwid", hthwid);
        detail.post();
      }
      initRowInfo(false, false, true);
    }
  }
   */
  /**
   *  从表删除操作
   */
  class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      boolean isSubDel = String.valueOf(SUBPLAN_DEL).equals(action);//是否是分切计划删除从表信息
      int rownum = Integer.parseInt(data.getParameter("rownum"));//参数，删除第几行
      putDetailInfo(data.getRequest());
      if(isSubDel)
        putSubMrpInfo(data.getRequest());
      EngineDataSet ds = getDetailTable();//通用计划
      //删除临时数组的一列数据
      RowMap detailrow = (RowMap)d_RowInfos.get(rownum);
      long l_row  = Long.parseLong(detailrow.get("InternalRow"));
      d_RowInfos.remove(rownum);
      ds.goToInternalRow(l_row);
      String scjhmxid = ds.getValue("scjhmxid");
      String sql = combineSQL(DELETESJ_BOM_SQL, "?", new String[]{scjhmxid});
      ds.deleteRow();
    }
  }
  /**
   *  生产分切计划物料删除操作
   */
  class Materail_Del implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int rownum = Integer.parseInt(data.getParameter("rownum"));//参数，删除第几行
      EngineDataSet dsSubMrp = getSubMrpTable();//分切计划物料从表数据集
      putDetailInfo(data.getRequest());
      putSubMrpInfo(data.getRequest());
      d_SubMrpRowInfos.remove(rownum);
      dsSubMrp.goToRow(rownum);
      dsSubMrp.deleteRow();
    }
  }
  /**
   * 得到用于查找合同编号的bean
   * @param req WEB的请求
   * 生产计划选择销售合同得到销售合同一行主表信息
   * @return 返回用于查找合同编号的bean
   */
  public PlanSelectSaleDetail getSelectSaleOrderBean(HttpServletRequest req)
  {
    if(planSelectSaleDetailBean == null)
      planSelectSaleDetailBean = PlanSelectSaleDetail.getInstance(req);
    return planSelectSaleDetailBean;
  }
  /**
   * 得到用于查找库存当前量差额的bean
   * @param req WEB的请求
   * 生产计划按库存量生产
   * @return 返回用于查找库存当前量差额的bean
   */
  public PlanSelectStore getSelectStoreGoodsBean(HttpServletRequest req)
  {
    if(planSelectStoreBean == null)
      planSelectStoreBean = PlanSelectStore.getInstance(req);
    return planSelectStoreBean;
  }
  /**
   * 得到用于查找产品单价的bean
   * @param req WEB的请求
   * @return 返回用于查找产品单价的bean
   */
  public LookUp getProductBean(HttpServletRequest req)
  {
    if(productBean == null)
      productBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PRODUCT_STOCK);
    return productBean;
  }
/**
 * 得到规格属性的bean
 * @param req WEB的请求
 * @return 返回规格属性的bean
 */
  public LookUp getPropertyBean(HttpServletRequest req)
  {
    if(propertyBean == null)
      propertyBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_SPEC_PROPERTY);
    return propertyBean;
  }
  public String getScjhWcrq(String cpid, String dmsxid)
  {
    String ksrq = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    String tmpdmsxid = dmsxid.equals("")?"0":dmsxid;
    String prod_time_sql = combineSQL(SC_JH_GET_PROD_TIME, "?", new String[]{ksrq, cpid, tmpdmsxid});
    ds_Sc_jh_Get_Prod_Time.setQueryString(prod_time_sql);
    if (ds_Sc_jh_Get_Prod_Time.isOpen())
      ds_Sc_jh_Get_Prod_Time.refresh();
    else
      ds_Sc_jh_Get_Prod_Time.openDataSet();
    //结束日期 = 开始日期 + 提前期。(总提前期和提前期在产品提前期表中sc_prod_time)
    String wcrq = ds_Sc_jh_Get_Prod_Time.getValue("scjhwcrq");//总提前其
    return wcrq;
  }
  public boolean isMaterial( String sjsjbomid, String gxfdid)
  {
    /*自制件:根据sjsjbomid取得sjbomid.然后
      1.select bomid from sc_sjbom where sjsjbomid = sjbomid
      2.取得bomid去BOM领料工段 (sc_bomsection)用sql得到gxfdid.
      3.判断gxfdid和加工单指定的gxfdid是不是相等.如相同则插入物料.如不同.则取下一笔记录
      */
    String getBomGxfdidSql = " SELECT gxfdid FROM sc_bomsection sfd "
                           + " WHERE sfd.bomid = "
                           + " ( SELECT DISTINCT bomid FROM sc_sjbom "
                           + "     WHERE sjbomid = '" + sjsjbomid + "'"
                           + " )";
    String tmpgxfdid = dataSetProvider.getSequence(getBomGxfdidSql);
    tmpgxfdid = tmpgxfdid == null?"":tmpgxfdid;
    if (tmpgxfdid.equals(gxfdid))
      return true; //是可以加入到物料清单中的物料
    else
      return false;
  }
  /**
 * 得到用于合同主表信息的bean
 * @param req WEB的请求
 * @return 返回用于合同主表信息的bean
 */
public PlanSelectSale getOrderMasterBean(HttpServletRequest req)
{
  if(planSelectSaleBean == null)
    planSelectSaleBean = PlanSelectSale.getInstance(req);
  return planSelectSaleBean;
  }
  }




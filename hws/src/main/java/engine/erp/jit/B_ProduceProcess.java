package engine.erp.jit;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineDataView;
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
//import engine.erp.produce.ImportTask;
//import engine.erp.produce.B_Process_SingleSelTask;

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
 * <p>Title: 生产--生产加工单列表</p>
 * <p>Description: 生产--生产加工单列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_ProduceProcess extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL            = "10001";
  public  static final String DETAIL_SELECT_TASK     = "10021";
  public  static final String ONCHANGE               = "10031";
  public  static final String SINGLE_SELECT_TASK     = "10531";
  public  static final String PRODUCT_ONCHANGE       = "10091";//输入产品触发事件
  public  static final String SINGLE_SELECT_PRODUCT  = "10891";//单选产品触发事件
  public  static final String COMPLETE               = "11001";//手工强制完成事件
  //public  static final String MATERAIL_ADD           = "11002";//生产加工单物料增加事件
  public  static final String SUBTASK_ADD            = "11003";//生产加工单新增分切计划任务单增加事件
  //public  static final String MATERAIL_DEL           = "11004";//生产加工单物料清单删除事件
  //public  static final String COMMONMATERAIL_EDIT    = "11005";//通用加工单。物料清单查看编辑事件
  //public  static final String COMMONMATERAIL_ADD     = "11006";//通用加工单。物料清单增加编辑事件
  //public  static final String COMMONMATERAIL_DEL     = "11007";//通用加工单。物料清单删除事件
  public  static final String COMMONDETAIL_DEL       = "11008";//通用加工单。物料清单删除事件
  public  static final String CONFIRM                = "11009";//通用加工单。对每一条加工单明细的物料清单保存到内存事件
  public  static final String COMMON_POST            = "2001";//通用加工单。保存事件
  public  static final String COMMONPOST_CONTINUE    = "2002";//通用加工单。保存事件
  public  static final String DETAIL_REFRESH         = "2003";//通用加工单。保存事件
  public  static final String MATERAIL_REFRESH       = "2004";//通用加工单。刷新该条加工单物料事件
  public  static final String WJGD_ADD               = "2005";//外加工单增加
  public  static final String CANCEL_APPROVE         = "11031";
  public  static final String MULTISELECT_SCBOM      = "99999";
  public  static final String REPORT                 = "2000";
  public  static final String DWTXONCHNAGE           = "99998";//单位改变
  public static final String PROCESS_CARD_ADD        = "55555";
  public  static final String GZZCHANGE              = "11732";
  public  static final String DETAIL_COPY             ="2007";
  public  static final String IMPORT_PROCESSCARD     = "2006";
  public  static final String DEL_SELECT             = "2008";

  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_jgd WHERE 1<>1";
  private static final String MASTER_SQL    = "select * from (SELECT a.*,b.djh FROM sc_jgd a,VW_BEAN_PROCESSCARD_DETAIL b  WHERE ? AND a.processmxid=b.processmxid(+) and a.sfwjg='?' AND a.fgsid=? ?) where 1=1 ? ORDER BY jgdh DESC";
  private static final String MASTER_EDIT_SQL       = "SELECT * FROM sc_jgd WHERE   jgdid='?' ";


  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_jgdmx WHERE 1<>1 ";
  private static final String DETAIL_SQL    = "SELECT * FROM sc_jgdmx WHERE jgdid='?'  ORDER BY personid,cpid, jgdmxid";//
  //引任务单明细
  private static final String TASK_DETAIL_SQL = "SELECT * FROM sc_wlxqjhmx WHERE  wlxqjhid= ";

  //生产加工单物料清单结构SQL语句
  //private static final String DRAWMATERAIL_STRUT_SQL = "SELECT * FROM sc_jgdwl WHERE 1<>1 ORDER BY cpid";
  //private static final String DRAWMATERAIL_SQL = "SELECT * FROM sc_jgdwl WHERE jgdid='?' ORDER BY cpid, jgdmxid ";
  //生产加工单物料清单编辑页面SQL
  //工作组人员
  private static final String GZZ_RY_SQL =
      "select a.gzzid,a.deptid,a.gzzbh,a.gzzmc,b.personid from sc_gzz a,sc_gzzry b where a.gzzid=b.gzzid ? order by b.ordernum";
  /*通过加工单主表生产计划ID和从表的任务单明细ID得到合同ID再根据cpid等于实际BOM表的上级cpid得到物料
  private static final String MATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.sjsjbomid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, a.htid, a.wgcl, a.chxz "
      + " FROM sc_sjbom a, sc_bomsection b  WHERE a.sjsjbomid IN (SELECT b.sjbomid "
      + " FROM sc_sjbom b, sc_jh c, sc_jhmx d WHERE b.scjhmxid=d.scjhmxid AND c.scjhid=d.scjhid  AND  nvl(b.htid,-1) IN( "
      + " SELECT nvl(e.htid,-1) FROM sc_wlxqjhmx e WHERE   e.wlxqjhmxid='?') "
      + " AND c.scjhid='?' AND b.cpid='?' ?) "
      + " and a.bomid = b.bomid and b.gxfdid = '?' "
      + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, a.htid, a.wgcl,a.sjsjbomid, a.chxz ORDER BY a.cpid";
  */
  /*
  private static final String MATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.sjsjbomid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, a.htid, a.wgcl, a.chxz "
      + " FROM sc_sjbom a, sc_bomsection b WHERE a.sjsjbomid = '?' "
      + " and a.bomid = b.bomid and b.gxfdid = '?' "
      + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, a.htid, a.wgcl,a.sjsjbomid, a.chxz ORDER BY a.cpid";
  //分切计划通过加工单主表生产计划ID和从表的任务单明细ID得到合同ID再根据cpid等于实际BOM表的上级cpid得到物料
  private static final String SUBMATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, b.scjhid "
      + " FROM sc_sjbom a, sc_jh b, sc_jhmx c WHERE a.scjhmxid=c.scjhmxid AND b.scjhid=c.scjhid AND nvl(a.htid,-1) IN( "
      + " SELECT nvl(d.htid,-1) FROM sc_wlxqjhmx d, sc_rwdmx e WHERE d.wlxqjhmxid=e.wlxqjhmxid  AND e.rwdmxid IN(?)) AND b.scjhid='?' AND a.sjcpid IN(?) "
      + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, b.scjhid ORDER BY a.cpid ";
  //如果通用加工单明细中手工输入的半成品的原料SQL，要从实际BOM中得到
  private static final String BOM_SQL = "SELECT a.* FROM sc_bom a, sc_bomsection b "
                                      + "  WHERE a.sjcpid='?' and a.bomid = b.bomid and b.gxfdid = '?' ";
  private static final String BOM_SQL_SIMPLE = " SELECT a.* FROM sc_bom a "
      + "  WHERE a.sjcpid ='?' ";
  */
  //取出物料须求的一笔明细数据A.这笔A数据会被插入到生产加工单明细SC_JGDMX表中去.
  //在插入A到SC_JGDMX的时候同时也须要把这此笔A的cpid所代表的这个产品是流转还是入库的标志得到赋值给sc_jgmx.wgcl字段.
  //取得wgcl的sql如下:
  /*
  private static final String GET_PER_SC_JGDMX_WGCL = " SELECT b.wgcl "
      + " FROM sc_sjbom b, sc_jh c, sc_jhmx d "
      + " WHERE b.scjhmxid=d.scjhmxid AND c.scjhid=d.scjhid "
      + "       AND  nvl(b.htid,-1) = "
      + "      ( SELECT nvl(e.htid,-1) FROM sc_wlxqjhmx e "
      + "        WHERE   e.wlxqjhmxid='?' "
      + "       )  AND c.scjhid='?' "
      + "       AND b.cpid='?' ? ";
  //用于审批时候提取一条记录
  */
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM sc_jgd WHERE jgdid = '?'";
  //private static final String GETMATERAIL_LIST_SJCPID = "SELECT * FROM sc_sjbom WHERE sjsjbomid ='?' ";

  //生产加工单明细数据.从sc_sjbom中取出
  /*
  private static final String SC_JGD_DETAIL_SJBOM =
      " SELECT h.gxfdid, m.cc, m.wgcl, m.cpid, j.dmsxid, j.sl, m.scxql scsl, m.sjbomid, m.scjhmxid, g.scjhid, "
      + " f.deptid, g.jhh, g.scjhid||'#'||h.gxfdid||m.cc||m.wgcl||m.cpid||j.dmsxid||m.sjbomid||m.scjhmxid||f.deptid||g.jhh tag "
      + " FROM sc_gylxmx h, sc_jhmx j, sc_sjbom m , sc_gylx e, sc_gxfd f, sc_jh g "
      + " WHERE j.scjhmxid = m.scjhmxid AND h.gylxid = e.gylxid "
      + "       AND e.cpid = m.cpid AND nvl(m.dmsxid,0) = e.dmsxid "
      + "       AND m.chxz = 1 AND f.gxfdid = h.gxfdid and j.scjhid = g.scjhid "
      + "       AND ( g.scjhid||'#'||h.gxfdid||m.cc||m.wgcl||m.cpid||j.dmsxid||m.sjbomid||m.scjhmxid||f.deptid||g.jhh  = '?' ) "
      + " GROUP BY h.gxfdid, m.cc, m.wgcl, m.cpid, j.dmsxid,j.sl, m.scxql, m.sjbomid, m.scjhmxid, f.deptid, g.scjhid, g.jhh "
      + " ORDER BY m.cc DESC, MAX(h.list) DESC ";
  */
  private String updatStructSql = " select * from sc_jh where 1<>1 ";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsMasterList  = new EngineDataSet();//主表

  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表

  //private EngineDataSet dsProcessMaterail = new EngineDataSet();//分切加工单： 加工单物料清单数据集如果是通用计划该数据集只用于显示，不能编辑
  //private EngineDataSet dsDrawMaterail = new EngineDataSet();//通用加工单时： 对应加工单中每一条纪录都存在物料，该数据集用于修改和保存每条明细的物料

  //private EngineDataSet dsBom = new EngineDataSet();//BOM零时数据集
  //private EngineDataSet dsFactBomMaterail = new EngineDataSet();//实际BOM零时数据集
  //private EngineDataSet dsGetMaterail_List_Cpid = new EngineDataSet();
  //private EngineDataSet dsUpdateScjhState = new EngineDataSet();
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_jgd");
  public  HtmlTableProducer masterListProducer = new HtmlTableProducer(dsMasterList, "sc_jgd");

  public  HtmlTableProducer wjgmasterProducer = new HtmlTableProducer(dsMasterTable, "sc_jgd.2");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_jgdmx");
  //public  HtmlTableProducer materailProducer = new HtmlTableProducer(dsProcessMaterail, "sc_jgdwl");
  //public  HtmlTableProducer commonProducer = new HtmlTableProducer(dsDrawMaterail, "sc_jgdwl");
  public  HtmlTableProducer searchTable = new HtmlTableProducer(dsMasterTable, "sc_jgd", "sc_jgd");//查询得到数据库中配置的字段

  private boolean isMasterAdd = true;  //是否在添加状态
  public  boolean isApprove = false;   //是否在审批状态
  public  boolean isDetailAdd = false; //从表是否在增加状态
  public  boolean isReport = false;
  private long    masterRow = -1;         //保存主表修改操作的行记录指针

  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  //private ArrayList d_MaterailBill = null;//物料清单多行纪录的引用,分切加工单用
  //private ArrayList d_CommonMaterail = null;//物料多行纪录，通用加工单


  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  //private ImportTask importTaskBean = null; //生产任务单的bean的引用, 用于提取生产任务单信息
  private B_Process_SingleSelTask singleSelTaskBean = null; //生产任务单主表信息BEAN的引用

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String jgdid = null;
  private User user = null;
  public String SC_PRODUCE_UNIT_STYLE =null;//1=强制换算,0=仅空值时换算
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  public static String SC_STORE_UNIT_STYLE    = "1";//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  private String scjhid = null;//生产计划ID
  private String rwdid = null;//生产任务单ID
  private String jglx = "0";//生产加工单加工类型 0=通用加工,1=分切加工
  private String jgdmxid = null;
  public String SC_PLAN_ADD_STYLE = null;//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
  //private int procDetaiLlSeq = -1;//在生成物料的时候可用,因为新增加工单明细的时候还没有得到加工单明细ＩＤ。而生成物料时又要用到
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public String tCopyNumber = "1";
  public String sfwjg ="0";//是否外加工0是自加工，1是外加工
  /**
   * 生产加工单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回生产加工单列表的实例
   */
  public static B_ProduceProcess getInstance(HttpServletRequest request)
  {
    B_ProduceProcess produceProcessBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "produceProcessBean";
      produceProcessBean = (B_ProduceProcess)session.getAttribute(beanName);
      if(produceProcessBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        produceProcessBean = new B_ProduceProcess();
        produceProcessBean.qtyFormat = loginBean.getQtyFormat();
        produceProcessBean.sumFormat = loginBean.getSumFormat();

        produceProcessBean.fgsid = loginBean.getFirstDeptID();
        produceProcessBean.loginId = loginBean.getUserID();
        produceProcessBean.loginName = loginBean.getUserName();
        produceProcessBean.loginDept = loginBean.getDeptID();
        produceProcessBean.user = loginBean.getUser();
        produceProcessBean.SC_PLAN_ADD_STYLE = loginBean.getSystemParam("SC_PLAN_ADD_STYLE");//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
        produceProcessBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        produceProcessBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//计量单位和生产单位是否强制换算
        produceProcessBean.SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
        produceProcessBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        //设置格式化的字段
        produceProcessBean.dsMasterTable.setColumnFormat("zsl", produceProcessBean.sumFormat);
        produceProcessBean.dsDetailTable.setColumnFormat("sl", produceProcessBean.qtyFormat);
        produceProcessBean.dsDetailTable.setColumnFormat("scsl", produceProcessBean.qtyFormat);
        produceProcessBean.dsDetailTable.setColumnFormat("jgdj", produceProcessBean.priceFormat);
        produceProcessBean.dsDetailTable.setColumnFormat("jgje", produceProcessBean.sumFormat);
        produceProcessBean.dsDetailTable.setColumnFormat("yrksl", produceProcessBean.qtyFormat);
        produceProcessBean.dsDetailTable.setColumnFormat("yrkscsl", produceProcessBean.qtyFormat);
        //produceProcessBean.dsProcessMaterail.setColumnFormat("sl",produceProcessBean.qtyFormat);
        //produceProcessBean.dsProcessMaterail.setColumnFormat("scsl",produceProcessBean.qtyFormat);
        //produceProcessBean.dsDrawMaterail.setColumnFormat("sl",produceProcessBean.qtyFormat);
        //produceProcessBean.dsDrawMaterail.setColumnFormat("scsl",produceProcessBean.qtyFormat);
        //produceProcessBean.dsDrawMaterail.setColumnFormat("wlje",produceProcessBean.sumFormat);
        //produceProcessBean.dsDrawMaterail.setColumnFormat("wldj",produceProcessBean.priceFormat);
        session.setAttribute(beanName, produceProcessBean);
      }
    }
    return produceProcessBean;
  }

  /**
   * 构造函数
   */
  private B_ProduceProcess()
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
    setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
    setDataSetProperty(dsMasterList, MASTER_STRUT_SQL);

    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    //setDataSetProperty(dsProcessMaterail, DRAWMATERAIL_STRUT_SQL);
    //setDataSetProperty(dsDrawMaterail, DRAWMATERAIL_STRUT_SQL);
    //setDataSetProperty(dsUpdateScjhState, updatStructSql);
    //dsDrawMaterail.setLoadDataUseSelf(false);
    dsDetailTable.setLoadDataUseSelf(false);
    dsMasterList.setTableName("sc_jgd");
    //setDataSetProperty(dsBom, null);
    //setDataSetProperty(dsFactBomMaterail, null);
    //setDataSetProperty(dsGetMaterail_List_Cpid, null);

    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"jgdh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"jgdmxid"}, new String[]{"s_sc_jgdmx"}));

    //dsProcessMaterail.setSequence(new SequenceDescriptor(new String[]{"jgdwlid"}, new String[]{"s_sc_jgdwl"}));//分切加工单
    //dsDrawMaterail.setSequence(new SequenceDescriptor(new String[]{"jgdwlid"}, new String[]{"s_sc_jgdwl"}));//通用加工单
    //
    //dsDrawMaterail.setMasterLink(    new MasterLinkDescriptor(dsDetailTable,  new String[]{"jgdmxid"}, new String[]{"jgdmxid"}, false, true, true));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(WJGD_ADD), masterAddEdit);

    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(SUBTASK_ADD), masterAddEdit);//新增分切计划任务触发事件
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    //addObactioner(String.valueOf(DETAIL_SELECT_TASK), new Detail_Select_Task());
    //addObactioner(String.valueOf(SINGLE_SELECT_TASK), new Single_Select_Task());//单选任务单
    addObactioner(String.valueOf(PRODUCT_ONCHANGE), new Product_Onchange());//输入产品编码触发事件
    //addObactioner(String.valueOf(MATERAIL_ADD), new Materail_Add());//加工单物料增加事件
    addObactioner(String.valueOf(COMPLETE), new Complete());//强制完成操作
    //addObactioner(String.valueOf(MATERAIL_DEL), new Materail_Delete());//物料删除操作
    //addObactioner(String.valueOf(COMMONMATERAIL_EDIT), new CommonMaterail_Edit());//通用加工单，物料查看编辑操作
    //addObactioner(String.valueOf(COMMONMATERAIL_ADD), new CommonMaterail_Add());//通用加工单，物料增加操作
    //addObactioner(String.valueOf(COMMONMATERAIL_DEL), new CommonMaterail_Delete());//通用加工单，物料删除操作
    addObactioner(String.valueOf(COMMONDETAIL_DEL), new CommonMaster_Delete());//通用加工单，删除从表操作
    //addObactioner(String.valueOf(CONFIRM), new Confirm());//通用加工单，删除从表操作
    addObactioner(String.valueOf(COMMON_POST), new Common_Post());//通用加工单，保存操作
    addObactioner(String.valueOf(COMMONPOST_CONTINUE), new Common_Post());//通用加工单，保存添加操作
    addObactioner(String.valueOf(DETAIL_REFRESH), new Detail_Refresh());//通用加工单，保存加工单明细页面信息操作
    //addObactioner(String.valueOf(MATERAIL_REFRESH), new CommonMaterail_Edit());//通用加工单，保存操作
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(REPORT), new Approve());
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());//取消审批
    //addObactioner(String.valueOf(MULTISELECT_SCBOM), new MultiSelect_SCBOM());//取消审批
    addObactioner(String.valueOf(DWTXONCHNAGE), new DWTX_Onchange());//取消审批

    addObactioner(PROCESS_CARD_ADD, new Master_Add());

    addObactioner(String.valueOf(GZZCHANGE), new GzzChange());//工作组改变事件

    addObactioner(String.valueOf(IMPORT_PROCESSCARD), new Import_ProcessCard());
    //addObactioner(String.valueOf(DETAIL_COPY), new Detail_Copy_Add());
    //addObactioner(String.valueOf(DEL_SELECT), new Detail_Delete_Select());

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
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMasterTable != null){
      dsMasterTable.close();
      dsMasterTable = null;
    }
    if(dsMasterList != null){
      dsMasterList.close();
      dsMasterList = null;
    }
    if(masterListProducer != null)
    {
      masterListProducer.release();
      masterListProducer = null;
    }
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
    }
    // if(dsProcessMaterail != null){
    //   dsProcessMaterail.close();
    //  dsProcessMaterail = null;
    //}
    //if(dsDrawMaterail != null){
    //  dsDrawMaterail.close();
    //   dsDrawMaterail = null;
    // }
    /*
    if(dsBom != null){
      dsBom.close();
      dsBom = null;
    }
    if(dsFactBomMaterail != null){
      dsFactBomMaterail.close();
      dsFactBomMaterail = null;
    }
    */
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    //d_MaterailBill = null;
    //d_CommonMaterail = null;
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
    }

    if(wjgmasterProducer != null)
    {
      wjgmasterProducer.release();
      wjgmasterProducer = null;
    }

    if(detailProducer != null)
    {
      detailProducer.release();
      detailProducer = null;
    }
    /*
    if(materailProducer != null)
    {
      materailProducer.release();
      materailProducer = null;
    }
    if(commonProducer != null)
    {
      commonProducer.release();
      commonProducer = null;
    }
    */
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
        m_RowInfo.put("zdrid", loginId);
        m_RowInfo.put("bm_deptid", loginDept);
        //m_RowInfo.put("deptid", loginDept);
        m_RowInfo.put("kdrq", today);
        m_RowInfo.put("zt", "0");
      }
    }
    else
    {
      EngineDataSet dsDetail = dsDetailTable;
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if(isInit)
        d_RowInfos.clear();

      dsDetail.first();//循环加工单明细把数据存入RowMap里面
      for(int i=0; i<dsDetail.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDetail);
        row.put("internalRowNum",String.valueOf(dsDetail.getInternalRow()));
        d_RowInfos.add(row);
        dsDetail.next();
      }
      /*
      if(jglx.equals("1")){
        EngineDataSet dsMaterail = getMaterailTable();
        if(d_MaterailBill == null)
          d_MaterailBill = new ArrayList(dsMaterail.getRowCount());
        else if(isInit)
          d_MaterailBill.clear();

        dsMaterail.first();//循环物料清单数据集
        for(int j=0; j<dsMaterail.getRowCount(); j++)
        {
          RowMap row = new RowMap(dsMaterail);

          d_MaterailBill.add(row);
          dsMaterail.next();
        }
      }
      else{
        EngineDataSet dsCommon = getCommonMaterail();
        if(d_CommonMaterail == null)
          d_CommonMaterail = new ArrayList(dsCommon.getRowCount());
        else if(isInit)
          d_CommonMaterail.clear();

        dsCommon.first();//循环物料清单数据集
        for(int j=0; j<dsCommon.getRowCount(); j++)
        {
          RowMap row = new RowMap(dsCommon);
          d_CommonMaterail.add(row);
          dsCommon.next();
        }
      }
      */
    }
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常

  private final void initCommonInfo(boolean isInit) throws java.lang.Exception
  {
    EngineDataSet dsCommon = getCommonMaterail();
    if(d_CommonMaterail == null)
      d_CommonMaterail = new ArrayList(dsCommon.getRowCount());
    else if(isInit)
      d_CommonMaterail.clear();

    dsCommon.first();//循环物料清单数据集
    int n = dsCommon.getRowCount();
    for(int j=0; j<dsCommon.getRowCount(); j++)
    {
      RowMap row = new RowMap(dsCommon);
      d_CommonMaterail.add(row);
      dsCommon.next();
    }
  }
  */
 /**
  * 从表保存操作
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
    String ss = rowInfo.get("bm_deptid");

    //生产加工单明细信息
    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      String cpid = rowInfo.get("cpid_"+i);
      String sl = rowInfo.get("sl_"+i);
      detailRow.put("internalRowNum", rowInfo.get("internalRowNum_"+i));
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//计量单位数量
      detailRow.put("cpl", rowInfo.get("cpl_"+i));//出品率
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性
      detailRow.put("ksrq", rowInfo.get("ksrq_"+i));//物资规格属性
      detailRow.put("wcrq", rowInfo.get("wcrq_"+i));//物资规格属性
      detailRow.put("jgyq", rowInfo.get("jgyq_"+i));
      String a= rowInfo.get("gzzid_"+i);
      detailRow.put("gzzid", rowInfo.get("gzzid_"+i));
      detailRow.put("wgcl", rowInfo.get("wgcl_"+i));
      detailRow.put("rtrn", rowInfo.get("rtrn_"+i));
      detailRow.put("hssl", rowInfo.get("hssl_"+i));
      detailRow.put("scsl", formatNumber(rowInfo.get("scsl_"+i), qtyFormat));//生产单位数量
    }
    /*
    if(jglx.equals("1")){
    //保存生产加工单物料信息
      int count = d_MaterailBill.size();
      RowMap materailRow = null;
      for(int m=0; m<count; m++)
      {
        materailRow = (RowMap)d_MaterailBill.get(m);
        materailRow.put("cpid", rowInfo.get("wlcpid_"+m));//产品
        materailRow.put("sl", formatNumber(rowInfo.get("wlsl_"+m), qtyFormat));//计量单位数量
        materailRow.put("scsl", formatNumber(rowInfo.get("wlscsl_"+m), qtyFormat));//生产单位数量
        materailRow.put("dmsxid", rowInfo.get("wldmsxid_"+m));//物资规格属性
        materailRow.put("wldj", formatNumber(rowInfo.get("wldj_"+m),sumFormat));//物资规格属性
    //保存用户自定义的字段
        FieldInfo[] fields = materailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          materailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + j));
        }
      }
    }
    */



  }
  /**
   * 通用加工单
   * 保存用户输入的信息

  private final void putCommonInfo(HttpServletRequest request)
  {
    RowMap rowInfo = new RowMap();
    //保存网页的所有信息
    rowInfo.put(request);
    //保存生产加工单物料信息
    int count = d_CommonMaterail.size();
    RowMap materailRow = null;
    for(int m=0; m<count; m++)
    {
      materailRow = (RowMap)d_CommonMaterail.get(m);
      materailRow.put("cpid", rowInfo.get("cpid"));//产品
      materailRow.put("personid", rowInfo.get("personid_"+m));
      materailRow.put("sl", formatNumber(rowInfo.get("sl_"+m), qtyFormat));//计量单位数量
      materailRow.put("scsl", formatNumber(rowInfo.get("scsl_"+m), qtyFormat));//生产单位数量
      materailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+m), qtyFormat));//生产单位数量
      materailRow.put("dmsxid", rowInfo.get("dmsxid_"+m));//物资规格属性
      materailRow.put("wgcl", rowInfo.get("wgcl_"+m));
      materailRow.put("wldj", formatNumber(rowInfo.get("wldj_"+m),priceFormat));//物资规格属性
      //materailRow.put("wlje", formatNumber(rowInfo.get("wlje_"+m),sumFormat));//物资规格属性
      //保存用户自定义的字段
      FieldInfo[] fields = commonProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        materailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + j));
      }
    }
  }
  */
  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }
  public final EngineDataSet getMaterListTable()
  {
    return dsMasterList;
  }
  /*通用加工单用于显示
  public final EngineDataView getDataView()
  {
    return dataview;
  }
  */

  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.openDataSet();
    //else
    //  dsDetailTable.refresh();
    return dsDetailTable;
  }
  /*得到分切加工单物料表对象
  public final EngineDataSet getMaterailTable()
  {
    if(!dsProcessMaterail.isOpen())
      dsProcessMaterail.openDataSet();
    return dsProcessMaterail;
  }
  */
  /*得到通用加工单物料表对象
  public final EngineDataSet getCommonMaterail()
  {
    if(!dsDrawMaterail.isOpen())
      dsDrawMaterail.openDataSet();
    return dsDrawMaterail;
  }*/
  /**
   * 得到任务是通过什么计划生成的。1.分切计划0通用计划
   */
  public final String getTaskType(){
    return jglx;
  }
  /**
   * 得到生产计划ＩＤ
   */
  public final String getPlanID(){
    return scjhid;
  }
  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    String id = isMasterAdd ? "-1" : jgdid;
    String SQL = combineSQL(DETAIL_SQL, "?", new String[]{id});

    dsDetailTable.setQueryString(SQL);//打开加工单明细数据集
    if(!dsDetailTable.isOpen())
      dsDetailTable.openDataSet();
    else
      dsDetailTable.refresh();
  }

 /*加工单时打开物料从表
  public final void openMaterailTable(boolean isMasterAdd)
  {
    String id = isMasterAdd ? "-1" : jgdid;
    String materailSQL = combineSQL(DRAWMATERAIL_SQL, "?", new String[]{id});//打开物料清单数据集]
    if(jglx.equals("1")){
      dsProcessMaterail.setQueryString(materailSQL);
      if(!dsProcessMaterail.isOpen())
        dsProcessMaterail.openDataSet();
      else
        dsProcessMaterail.refresh();
    }
    else{
      dsDrawMaterail.setQueryString(materailSQL);
      if(!dsDrawMaterail.isOpen())
        dsDrawMaterail.openDataSet();
      else
        dsDrawMaterail.refresh();
    }
  }
*/
  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /*得到物料表多列的信息：分切加工单用
  public final RowMap[] getMaterailRowinfos() {
    RowMap[] rows = new RowMap[d_MaterailBill.size()];
    d_MaterailBill.toArray(rows);
    return rows;
  }*/
  /*得到物料表多列的信息：通用加工单用
  public final RowMap[] getCommonRowinfos() {
    RowMap[] rows = new RowMap[d_CommonMaterail.size()];
    d_CommonMaterail.toArray(rows);
    return rows;
  }*/

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
    dsMasterList.goToInternalRow(masterRow);
    return dsMasterList.getRow();
  }
  /**
   * 初始化操作的触发类oiguguiuiui
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      tCopyNumber = "1";
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      masterListProducer.init(request, loginId);
      wjgmasterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //materailProducer.init(request, loginId);
      //commonProducer.init(request, loginId);

      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("zt", "0");
      //if (sfwjg.equals("1"))
      //{
      row.put("kdrq$a", startDay);
      row.put("kdrq$b", today);
      //}
      /*else
      {
        row.put("rq$a", startDay);
        row.put("rq$b", today);
      }
      */
      isMasterAdd = true;
      isDetailAdd =false;
      sfwjg = request.getParameter("sfwjg");
      //初始化时不显示已完成的单据
      searchTable.getWhereInfo().clearWhereValues();

      /*
      String SQL = " AND zt<>8 ";
      String scjhid = request.getParameter("scjhid");
      scjhid = scjhid == null? "":" AND scjhid = " + scjhid;
      SQL = SQL + scjhid;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),sfwjg,fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      */

      String SQL = " AND a.zt<>8  ";
      String MSQL =  combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("a.deptid", "a.zdrid"),sfwjg, fgsid, SQL,""});
      dsMasterList.setQueryString(MSQL);
      dsMasterList.setRowMax(null);

      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      //if(dsProcessMaterail.isOpen() && dsProcessMaterail.getRowCount() > 0)
      //    dsProcessMaterail.empty();
    }
  }

  /**
   * 显示从表的列表信息
   */
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterList.getInternalRow();
      jgdid = dsMasterList.getValue("jgdid");
      //打开从表
      openDetailTable(false);
    }
  }

  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      //procDetaiLlSeq = -1;
      isMasterAdd = !String.valueOf(EDIT).equals(action);
      boolean isSubTaskAdd = String.valueOf(SUBTASK_ADD).equals(action);
      isDetailAdd = false;
      if(!isMasterAdd)
      {
        isMasterAdd =false;
        dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterList.getInternalRow();
        scjhid = dsMasterList.getValue("scjhid");
        jgdid = dsMasterList.getValue("jgdid");
        openDetailTable(false);
      }
      else
      {
        synchronized(dsDetailTable){
          openDetailTable(isMasterAdd);
        }
      }
      if(isMasterAdd){
        //if(isSubTaskAdd)
        //  jglx = "1";
        //else
        jglx = "0";
        //dsMasterTable.empty();
      }

      dsMasterTable.setQueryString(isMasterAdd?MASTER_STRUT_SQL:combineSQL(MASTER_EDIT_SQL,"?",new String[]{jgdid}));
      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();


      //if(String.valueOf(WJGD_ADD).equals(action))
      //  sfwjg = "1";
      //openMaterailTable(isMasterAdd);
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      //if(jglx.equals("1"))
      //   data.setMessage(showJavaScript("toSubDetail();"));
      //else
      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();


      isReport = false;
      String deptid = req.getParameter("deptid");
      String cpid = req.getParameter("cpid");
      String processmxid = req.getParameter("processmxid");
      String gymcid = req.getParameter("gymcid");
      String gylxmxid = req.getParameter("gylxmxid");
      String gxfdid = req.getParameter("gxfdid");
      isMasterAdd = true;
      String scjhid = req.getParameter("scjhid");
      dsMasterTable.setQueryString(MASTER_STRUT_SQL);
      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();

      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
/*
      String SQL = " and 1<>1 ";//" and deptid='"+deptid+"' and cpid ='"+cpid+"' and processmxid='"+processmxid+"'";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), sfwjg, fgsid, SQL});
      if(dsMasterList.isOpen())
        dsMasterList.close();
      setDataSetProperty(dsMasterList,SQL);
      dsMasterList.open();
      dsMasterTable.first();

      synchronized(dsDetailTable){
        String jgdid = dsMasterTable.getValue("jgdid");
        isMasterAdd = jgdid.equals("")?true:false;
        String sql = jgdid.equals("") ? "-1" : jgdid;
        SQL = combineSQL(DETAIL_SQL, "?", new String[]{sql});
        dsDetailTable.setQueryString(SQL);
        if(!dsDetailTable.isOpen())
          dsDetailTable.open();
        else
          dsDetailTable.refresh();
      }
*/

      masterProducer.init(req, loginId);
      detailProducer.init(req, loginId);

      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      m_RowInfo.put("deptid",deptid);
      m_RowInfo.put("cpid",cpid);
      m_RowInfo.put("gymcid",gymcid);
      m_RowInfo.put("processmxid",processmxid);
      m_RowInfo.put("gylxmxid",gylxmxid);
      m_RowInfo.put("gxfdid",gxfdid);
      m_RowInfo.put("scjhid",scjhid);
    }
  }
  /**
   * 引入生产流程卡明细
   */
  class Import_ProcessCard implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();


      isReport = false;
      String deptid = req.getParameter("sdeptid");
      String cpid = req.getParameter("cpid");
      String processmxid = req.getParameter("processmxid");
      String gymcid = req.getParameter("gymcid");
      String gylxmxid = req.getParameter("gylxmxid");
      String gxfdid = req.getParameter("gxfdid");
      String scjhid = req.getParameter("scjhid");

/*
      String SQL = "  and processmxid='"+processmxid+"'";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), sfwjg, fgsid, SQL});
      if(dsMasterTable.isOpen())
        dsMasterTable.close();
      EngineDataSet tmp = new EngineDataSet();
      setDataSetProperty(tmp,SQL);
      dsMasterTable.open();
      if(dsMasterTable.getRowCount()!=0)
      {
        dsMasterTable.first();
        synchronized(dsDetailTable){
          String jgdid = dsMasterTable.getValue("jgdid");
          isMasterAdd = jgdid.equals("")?true:false;
          String sql = jgdid.equals("") ? "-1" : jgdid;
          SQL = combineSQL(DETAIL_SQL, "?", new String[]{sql});
          dsDetailTable.setQueryString(SQL);
          if(!dsDetailTable.isOpen())
            dsDetailTable.open();
          else
            dsDetailTable.refresh();
        }
        initRowInfo(true, isMasterAdd, true);
        initRowInfo(false, isMasterAdd, true);

      }else
      {
        initRowInfo(true, isMasterAdd, true);
        initRowInfo(false, isMasterAdd, true);

        m_RowInfo.put("deptid",deptid);
        m_RowInfo.put("cpid",cpid);
        m_RowInfo.put("gymcid",gymcid);
        m_RowInfo.put("processmxid",processmxid);
        m_RowInfo.put("gylxmxid",gylxmxid);
        m_RowInfo.put("gxfdid",gxfdid);
      }

      */

      masterProducer.init(req, loginId);
      detailProducer.init(req, loginId);

      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      m_RowInfo.put("deptid",deptid);
      m_RowInfo.put("cpid",cpid);
      m_RowInfo.put("gymcid",gymcid);
      m_RowInfo.put("processmxid",processmxid);
      m_RowInfo.put("gylxmxid",gylxmxid);
      m_RowInfo.put("gxfdid",gxfdid);
      m_RowInfo.put("scjhid",scjhid);




    }
  }
  /**
   * 重新生成物料
   * 通用加工单中，加工单明细物料生成及修改操作的触发类
   * 如果该条明细没有物料，则生成
   * 如果是手工增加就去BOM表里面抽取
   * 如果是引入任务单并且是从生产计划里面过来的，就要关联到实际BOM里面收取
   * 04.10.27 18:18 新增了从sc_sjbom中取出一笔cpid数据是否可以作为物料插入到生产加工单明细物料表中的判断
   *  1.一笔sc_sjbom中的数据是否能作为物料插入到sc_jgdmxwl在isMaterial()中判断出来.具体逻辑须看isMaterial()

  class CommonMaterail_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      HttpServletRequest req = data.getRequest();
      boolean isRefresh = String.valueOf(MATERAIL_REFRESH).equals(action);

      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String jgdid = dsMasterTable.getValue("jgdid");

      int rownum = Integer.parseInt(data.getParameter("rownum"));
      dsDetailTable.goToRow(rownum);
      int dm = dsDrawMaterail.getRowCount();
      if(dsDrawMaterail.getRowCount()>0){
        if(isRefresh)
          dsDrawMaterail.deleteAllRows();
        else{
          initCommonInfo(true);
          return;
        }
      }
      //RowMap detail = (RowMap)d_RowInfos.get(rownum);
      //对应从表信息
      jgdmxid = dsDetailTable.getValue("jgdmxid");
      String wlxqjhmxid = dsDetailTable.getValue("wlxqjhmxid");
      String sjcpid = data.getParameter("cpid");
      String sl = data.getParameter("sl");
      String scsl = data.getParameter("scsl");
      String dmsxid = data.getParameter("dmsxid");
      String gxfdid = data.getParameter("gxfdid");
      double d_sl = sl.length()>0 ? Double.parseDouble(sl) : 0;//加工单中产品要加工的数量
      double d_scsl = scsl.length()>0 ? Double.parseDouble(scsl) : 0;//

   //如果该条纪录是手工增加不是引入任务单，物料从bom中抽取.//实际ＢＯＭ中抽取
      if(sjcpid.equals("") || sjcpid==null)
        return;
      if(wlxqjhmxid.equals("") || scjhid.equals("")  || scjhid==null){
        String FACTBOM = combineSQL(sfwjg.equals("1")?BOM_SQL_SIMPLE:BOM_SQL,"?", new String[]{sjcpid, gxfdid});
        dsBom.setQueryString(FACTBOM);
        if(dsBom.isOpen())
          dsBom.refresh();
        else
          dsBom.openDataSet();
        dsBom.first();
        engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_PRODUCT);
        engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
        boolean isMaterialVariable = false;//判断是不是可以插入到物料清单中的物料
        for(int i=0;i<dsBom.getRowCount();i++){
          String cpidA = dsBom.getValue("cpid");
          String dmsxidA = dsBom.getValue("dmsxid");
          String bomid = dsBom.getValue("bomid");
          String wgcl = dsBom.getValue("wgcl");
          prodBean.regData(dsBom,"cpid");
          propertyBean.regData(dsBom, "dmsxid");
          RowMap  prodRow= prodBean.getLookupRow(cpidA);
          String jhdj = prodRow.get("jhdj");
          String hsbl = prodRow.get("hsbl");
          String sxz = propertyBean.getLookupName(dmsxid);
          //String chxz = dsBom.getValue("chxz");
          //当是手工新增,物料从sc_bom中取得.就直接取得bomid然后到sc_bomsection取得gxfdid
          //String bomgxfdid = dataSetProvider.getSequence("select gxfdid from sc_bomsection where bomid = " + bomid );
          //bomgxfdid = bomgxfdid == null?"":bomgxfdid;
          //判断是否是可以加入到物料清单中的物料
          //isMaterialVariable = gxfdid.equals(bomgxfdid);
          //if (isMaterialVariable){
          int mn = dsDrawMaterail.getRowCount();
          dsDrawMaterail.insertRow(false);
          dsDrawMaterail.setValue("jgdwlid", "-1");
          dsDrawMaterail.setValue("jgdmxid", jgdmxid);
          dsDrawMaterail.setValue("jgdid",isMasterAdd ? "-1" : jgdid);
          dsDrawMaterail.setValue("cpid", dsBom.getValue("cpid"));
          dsDrawMaterail.setValue("dmsxid", dmsxid);
          dsDrawMaterail.setValue("wgcl", wgcl);
          dsDrawMaterail.setValue("wldj", jhdj);
          double zjsl = dsBom.getValue("sl").length()>0 ? Double.parseDouble(dsBom.getValue("sl")) : 0;//生产该产品需要下级原料的数量
          double shl = dsBom.getValue("shl").length()>0 ? Double.parseDouble(dsBom.getValue("shl")) : 0;//生产该产品需要下级原料的数量
          BigDecimal scale = new BigDecimal(0);
          try
          {
            scale = calculateExpression(hsbl, sxz);
          }
          catch (Exception e)
          {
          //异常默认比例设为1
            scale = new BigDecimal(1);
          }
          scale = scale==null?new BigDecimal(1):scale;
          double m_sl = d_sl*zjsl*(1+shl);
          BigDecimal tmpsl = new BigDecimal(m_sl);
          BigDecimal tmphssl = tmpsl.divide(scale,6,BigDecimal.ROUND_HALF_UP);
          dsDrawMaterail.setValue("sl",formatNumber(String.valueOf(m_sl), qtyFormat));
          dsDrawMaterail.setValue("hssl",formatNumber(tmphssl.toString(), qtyFormat));
          dsDrawMaterail.setValue("scsl",formatNumber(String.valueOf(m_sl), qtyFormat));
          dsDrawMaterail.post();
          //}
          dsBom.next();
        }
        int mn = dsDrawMaterail.getRowCount();
      }
      else if(!wlxqjhmxid.equals("") && !scjhid.equals("")  && scjhid!=null){
        String temp = null;
        if(dmsxid.equals(""))
          temp =" AND b.dmsxid IS NULL";
        else
          temp = " AND b.dmsxid ="+ dmsxid;
        String  SQl = combineSQL(MATERAIL_LIST_SQL, "?", new String[]{wlxqjhmxid, scjhid,sjcpid, temp, gxfdid});
        boolean isMaterialVariable = false;//判断是不是可以插入到物料清单中的物料
        dsFactBomMaterail.setQueryString(SQl);
        if(dsFactBomMaterail.isOpen())
          dsFactBomMaterail.refresh();
        else
          dsFactBomMaterail.openDataSet();
        dsFactBomMaterail.first();
        engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_PRODUCT);
        for(int i=0;i<dsFactBomMaterail.getRowCount();i++){
          String cpidA = dsFactBomMaterail.getValue("cpid");
          String dmsxidA = dsFactBomMaterail.getValue("dmsxid");
          String sjsjbomid = dsFactBomMaterail.getValue("sjsjbomid");
          String wgcl = dsFactBomMaterail.getValue("wgcl");
          String chxz = dsFactBomMaterail.getValue("chxz");
          prodBean.regData(dsFactBomMaterail,"cpid");
          RowMap  prodRow= prodBean.getLookupRow(cpidA);
          String jhdj = prodRow.get("jhdj");
          //判断是否是可以加入到物料清单中的物料
          //isMaterialVariable = isMaterial(sjsjbomid,  gxfdid);
          //if (isMaterialVariable){
          dsDrawMaterail.insertRow(false);
          dsDrawMaterail.setValue("jgdwlid", "-1");
          dsDrawMaterail.setValue("jgdmxid", jgdmxid);
          dsDrawMaterail.setValue("jgdid",isMasterAdd ? "-1" : jgdid);
          dsDrawMaterail.setValue("cpid", cpidA);
          dsDrawMaterail.setValue("dmsxid", dmsxidA);
          dsDrawMaterail.setValue("sl",dsFactBomMaterail.getValue("xql"));
          dsDrawMaterail.setValue("scsl",dsFactBomMaterail.getValue("scxql"));
          dsDrawMaterail.setValue("wgcl",wgcl);
          dsDrawMaterail.setValue("wldj",jhdj);
          dsDrawMaterail.post();
          //}
          dsFactBomMaterail.next();
        }
      }
      initCommonInfo(true);
    }
  }
  */
 /**
  *  通用加工单才有的功能
  *  加工单物料清单增加操作(增加一个空白行)

  class CommonMaterail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      //putCommonInfo(data.getRequest());
      EngineDataSet dsMaterail = getCommonMaterail();
      EngineDataSet ds = getMaterTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String jgdid = dsMasterTable.getValue("jgdid");
      dsMaterail.insertRow(false);
      dsMaterail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
      dsMaterail.setValue("jgdmxid", jgdmxid);
      dsMaterail.post();
      d_CommonMaterail.add(new RowMap());
    }
  }
  */
 /**
  *  通用加工单才有的功能
  *  物料删除操作

  class CommonMaterail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet dsMaterail = getCommonMaterail();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      d_CommonMaterail.remove(rownum);
      dsMaterail.goToRow(rownum);
      dsMaterail.deleteRow();
    }
  }
  */
 /**
  * 通用加工单主表保存操作的触发类

  class Confirm implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
    //putCommonInfo(data.getRequest());
    //校验表单数据
      String temp = null;
      temp = checkInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getCommonMaterail();
      int m = detail.getRowCount();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_CommonMaterail.get(i);
        //新添的记录
        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("sl", detailrow.get("sl"));//生产单位需求量
        detail.setValue("scsl", detailrow.get("scsl"));//需购量
        detail.setValue("hssl", detailrow.get("hssl"));//需购量
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("wldj", detailrow.get("wldj"));
        //保存用户自定义的字段
        FieldInfo[] fields = commonProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        detail.next();
      }
      //保存用户自定义的字段
      data.setMessage(showJavaScript("refresh();"));
    }


    private String checkInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      ArrayList list = new ArrayList(d_CommonMaterail.size());
      String cpid=null, dmsxid=null,unit=null;
      for(int i=0; i<d_CommonMaterail.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_CommonMaterail.get(i);
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");
        StringBuffer buf = new StringBuffer().append(cpid).append(",").append(dmsxid).append(",");
        unit = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(unit))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(unit);
        String scsl = detailrow.get("scsl");//生产单位需求量
        if(scsl.length()>0 &&  (temp = checkNumber(scsl, "第"+row+"行生产数量")) != null)
          return temp;
        String sl = detailrow.get("sl");//计量单位需求量
        if(sl.equals(""))
          return showJavaScript("alert('第"+row+"行数量不能为空');");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
      }
      return null;
    }
  }
  */
 /**
  * 分切加工单保存操作
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
      temp = checkMaterail();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      */
      //if(!isMasterAdd)
     //   ds.goToInternalRow(masterRow);

      //得到主表主键值
      String jgdid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        jgdid = dataSetProvider.getSequence("s_sc_jgd");
        ds.setValue("jgdid", jgdid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
        ds.setValue("zt", "0");
      }
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0);
      BigDecimal totalSum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("jgdid", jgdid);

        detail.setValue("cpid", detailrow.get("cpid"));
        //detail.setValue("personid", detailrow.get("personid"));
        detail.setValue("sl", detailrow.get("sl"));//保存计量单位数量
        detail.setValue("scsl", detailrow.get("scsl"));//保存生产单位数量
        detail.setValue("gylxid", detailrow.get("gylxid"));
        detail.setValue("rwdmxid", detailrow.get("rwdmxid"));
        detail.setValue("hssl", detailrow.get("hssl"));//出品率
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("ksrq", detailrow.get("ksrq"));
        detail.setValue("wcrq", detailrow.get("wcrq"));
        detail.setValue("jgyq", detailrow.get("jgyq"));
        detail.setValue("gzzid", detailrow.get("gzzid"));
        detail.setValue("wgcl", detailrow.get("wgcl"));
        detail.setValue("rtrn", detailrow.get("rtrn"));
        String jgdj = detailrow.get("jgdj");
        String jgje = detailrow.get("jgje");
        detail.setValue("jgdj", detailrow.get("jgdj"));//加工单价
        detail.setValue("jgje", detailrow.get("jgje"));//加工金额
        detail.setValue("jgyq", detailrow.get("jgyq"));//加工要求
        //保存用户自定义的字段

        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        totalSum = totalSum.add(detail.getBigDecimal("jgje"));
        detail.next();
      }
      //保存加工单物料信息
      //EngineDataSet dsMaterail = getMaterailTable();
      //RowMap materailRow = null;


      //保存主表数据
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      String ss = rowInfo.get("bm_deptid");
      ds.setValue("bm_deptid", rowInfo.get("bm_deptid"));//部门id
      ds.setValue("rq", rowInfo.get("rq"));//日期
      ds.setValue("zsl", totalNum.toString());
      ds.setValue("zje", totalSum.toString());
      ds.setValue("describe", rowInfo.get("describe"));//加工说明
      //boolean isAll = isAllMaterail();
      //ds.setValue("rwdid", isAll ? rwdid : "");//任务单ID
      ds.setValue("scjhid",   rowInfo.get("scjhid"));
      ds.setValue("jglx", jglx);//加工类型
      ds.setValue("sfwjg", sfwjg);//是否外加工
      ds.setValue("dwtxid",rowInfo.get("dwtxid"));

      ds.setValue("cpid",rowInfo.get("cpid"));
      ds.setValue("gymcid",rowInfo.get("gymcid"));
      ds.setValue("processmxid",rowInfo.get("processmxid"));
      ds.setValue("gylxmxid",rowInfo.get("gylxmxid"));
      ds.setValue("gxfdid",rowInfo.get("gxfdid"));
      ds.setValue("dwtxid",rowInfo.get("dwtxid"));



      ds.post();
      if(sfwjg.equals("1"))
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh','a') from dual"}));
      else
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh') from dual"}));
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      //procDetaiLlSeq =-1;

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        jglx="0";
        initRowInfo(true, true, true);
        detail.empty();
        //dsMaterail.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
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
      String cpid=null, dmsxid=null, gylxid=null,rwdmxid=null, unit=null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");
        gylxid = detailrow.get("gylxid");
        rwdmxid = detailrow.get("rwdmxid");
        StringBuffer buf = new StringBuffer().append(rwdmxid).append(",").append(cpid).append(",").append(dmsxid).append(",").append(gylxid);
        unit = buf.toString();

        /*
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(unit))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(unit);
        */
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
        String hssl = detailrow.get("hssl");
        if((temp = checkNumber(hssl, "第"+row+"行换算数量")) != null)
          return temp;
        if(sfwjg.equals("1")){
          String jgdj = detailrow.get("jgdj");
          if((temp = checkNumber(jgdj, "第"+row+"行加工单价")) != null)
            return temp;
          String jgje = detailrow.get("jgje");
          if((temp = checkNumber(jgje, "第"+row+"行加工金额")) != null)
            return temp;
        }
        String cpl = detailrow.get("cpl");
        if(cpl.length()>0 && (temp = checkNumber(cpl, "第"+row+"行出品率")) != null)
          return temp;
      }
      return null;
    }
    /**
     * 校验物料信息从表输入的信息的正确性
     * @return null 表示没有信息

    private String checkMaterail()
    {
      String temp = null;
      RowMap materailrow = null;
      if(d_MaterailBill.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      String cpid=null, dmsxid=null, unit=null;
      for(int i=0; i<d_MaterailBill.size(); i++)
      {
        int row = i+1;
        materailrow = (RowMap)d_MaterailBill.get(i);
        cpid = materailrow.get("cpid");
        dmsxid = materailrow.get("dmsxid");
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        String sl = materailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
      }
      return null;
    }     */
   /**
    * 校验从表表单信息从表输入的信息中是否全部是引入任务单明细
    * @return true 只要有一条是引任务单明细过来的，忽略手工增加信息。同时任务单ID和生产计划ID保存入任务单主表
    * 如果全部是手工增加这不保存任务单计划ID和生产计划ID到任务单主表
    * 手工增加的信息将的不到物料

    private boolean  isAllMaterail()
    {
      boolean isAllMaterail = false;
      RowMap detail = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detail = (RowMap)d_RowInfos.get(i);
        String rwdmxid = detail.get("rwdmxid");
        if(!rwdmxid.equals(""))
          return isAllMaterail=true;
      }
      return isAllMaterail;
    }
    */
   /**
    * 校验主表表表单信息从表输入的信息的正确性
    * @return null 表示没有信息,校验通过
    */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("kdrq");
      if(temp.equals(""))
        return showJavaScript("alert('日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法日期！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择车间！');");
      temp = rowInfo.get("gxfdid");
      if(temp.equals("")&&sfwjg.equals("0"))//sfwjg=0表明是生产加工单,因此须要校验工序分段,而辅料外加工单则不须此检验.
        return showJavaScript("alert('请选择工序分段！');");
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
      String num = data.getParameter("rownum");
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterList.getValue("jgdh");
      String deptid = dsMasterList.getValue("deptid");
      //if (sfwjg.equals("0"))//生产加工单
      approve.putAproveList(dsMasterList, dsMasterList.getRow(), "produce_process", content,deptid);
      //else //辅料外加工单
      //  approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "produce_outprocess", content, deptid);
    }
  }
  /**
   * 取消审批的操作类
   */
  class Cancel_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterList, dsMasterList.getRow(), "produce_process");
    }
  }
  /**
   * 审批操作的触发类
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      wjgmasterProducer.init(request, loginId);
      //得到request的参数,值若为null, 则用""代替
      isReport = String.valueOf(REPORT).equals(action);
      String id = data.getParameter("id", "");
      if(isReport){
        isApprove = false;
        id = data.getParameter("id");//得到报表传递的参数既收发单据主表ID
      }
      else{
        isApprove = true;//审批操作
        id = data.getParameter("id", "");
      }
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.openDataSet();
      jgdid = dsMasterTable.getValue("jgdid");
      //打开从表
      openDetailTable(false);
      //openMaterailTable(false);
      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
      //initCommonInfo(true);
    }
  }


  /**
   * 主表保存操作的触发类
   * 通用加工单保存操作
   */
  class Common_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      //putCommonInfo(data.getRequest());

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
     // if(!isMasterAdd)
     //   ds.goToInternalRow(masterRow);
      //得到主表主键值
      if(isMasterAdd){
        ds.insertRow(false);
        jgdid = dataSetProvider.getSequence("s_sc_jgd");
        ds.setValue("jgdid", jgdid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
        ds.setValue("zt", "0");
      }
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
          detail.setValue("jgdid", jgdid);
        if(detail.getBigDecimal("jgdmxid").intValue()<0 ){
          String id = dataSetProvider.getSequence("s_sc_jgdmx");
          detail.setValue("jgdmxid", id);
        }
        //detail.setValue("personid", detailrow.get("personid"));
        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("sl", detailrow.get("sl"));//保存计量单位数量
        detail.setValue("scsl", detailrow.get("scsl"));//保存生产单位数量
        detail.setValue("cpl", detailrow.get("cpl"));//出品率
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("ksrq", detailrow.get("ksrq"));
        detail.setValue("wcrq", detailrow.get("wcrq"));
         detail.setValue("gzzid", detailrow.get("gzzid"));
        String jgyq = detailrow.get("jgyq");
        detail.setValue("jgyq", detailrow.get("jgyq"));
        detail.setValue("jgdj", detailrow.get("jgdj"));//加工单价
        detail.setValue("jgje", detailrow.get("jgje"));//加工金额
        detail.setValue("hssl", detailrow.get("hssl"));//换算数量
        detail.setValue("wgcl", detailrow.get("wgcl"));
        detail.setValue("rtrn", detailrow.get("rtrn"));
        detail.post();
        //设置加工单物料的加工单ID

        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        totalSum = totalSum.add(detail.getBigDecimal("jgje"));
        detail.next();
      }

      //保存主表数据
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("bm_deptid", rowInfo.get("bm_deptid"));//部门id
      ds.setValue("kdrq", rowInfo.get("kdrq"));//日期
      ds.setValue("zsl", totalNum.toString());
      //ds.setValue("zje", totalSum.toString());
      ds.setValue("describe", rowInfo.get("describe"));//加工说明
      //boolean isAll = isAllMaterail();
      //ds.setValue("rwdid", isAll ? rwdid : "");//任务单ID
      ds.setValue("scjhid",  rowInfo.get("scjhid"));
      ds.setValue("jglx", jglx);//加工类型
      ds.setValue("sfwjg", sfwjg);//是否外加工
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));
      if(sfwjg.equals("1"))
      {
        ds.setValue("jsr", rowInfo.get("jsr"));
        ds.setValue("jsfsid", rowInfo.get("jsfsid"));
        ds.setValue("khlx", rowInfo.get("khlx"));
      }

      ds.setValue("cpid",rowInfo.get("cpid"));
      ds.setValue("gymcid",rowInfo.get("gymcid"));
      ds.setValue("processmxid",rowInfo.get("processmxid"));
      ds.setValue("gylxmxid",rowInfo.get("gylxmxid"));
      ds.setValue("gxfdid",rowInfo.get("gxfdid"));
      ds.setValue("dwtxid",rowInfo.get("dwtxid"));
      //ds.setValue("gzzid",rowInfo.get("gzzid"));


      String gxfdid = rowInfo.get("gxfdid");
      if (sfwjg.equals("0"))
        ds.setValue("gxfdid", gxfdid);//工序分段
      ds.post();

      if(sfwjg.equals("1"))
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh','a') from dual"}));
      else
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh') from dual"}));

      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);

      //procDetaiLlSeq =-1;
      isMasterAdd = false;
      /*
      if(String.valueOf(COMMONPOST_CONTINUE).equals(action)){
        isMasterAdd = true;
        jglx="0";
        initRowInfo(true, true, true);
        detail.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      */
      //else if(String.valueOf(COMMON_POST).equals(action))
      //   data.setMessage(showJavaScript("backList();"));
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
      String cpid=null, dmsxid=null, gylxid=null,rwdmxid=null, unit=null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");
        gylxid = detailrow.get("gylxid");
        rwdmxid = detailrow.get("rwdmxid");
        StringBuffer buf = new StringBuffer().append(rwdmxid).append(",").append(cpid).append(",").append(dmsxid).append(",").append(gylxid);
        unit = buf.toString();
        /*
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(unit))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(unit);
        */
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
        String hssl = detailrow.get("sl");
        if((temp = checkNumber(hssl, "第"+row+"行换算数量")) != null&&!hssl.equals(""))
          return temp;
        if(sfwjg.equals("1")){
          String jgdj = detailrow.get("jgdj");
          if((temp = checkNumber(jgdj, "第"+row+"行加工单价")) != null)
            return temp;
          String jgje = detailrow.get("jgje");
          if((temp = checkNumber(jgje, "第"+row+"行加工金额")) != null)
            return temp;
        }
        String cpl = detailrow.get("cpl");
        if(cpl.length()>0 && (temp = checkNumber(cpl, "第"+row+"行出品率")) != null)
          return temp;
      }
      return null;
    }
    /**
     * 校验物料信息从表输入的信息的正确性
     * @return null 表示没有信息

    private String checkCommonInfo()
    {
      String temp = null;
      RowMap materailrow = null;
      if(d_CommonMaterail.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      String cpid=null, dmsxid=null, unit=null;
      for(int i=0; i<d_CommonMaterail.size(); i++)
      {
        int row = i+1;
        materailrow = (RowMap)d_CommonMaterail.get(i);
        cpid = materailrow.get("cpid");
        dmsxid = materailrow.get("dmsxid");
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        String sl = materailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
      }
      return null;
    }
    */
   /**
    * 校验从表表单信息从表输入的信息中是否全部是引入任务单明细
    * @return true 只要有一条是引任务单明细过来的，忽略手工增加信息。同时任务单ID和生产计划ID保存入任务单主表
    * 如果全部是手工增加这不保存任务单计划ID和生产计划ID到任务单主表
    * 手工增加的信息将的不到物料

    private boolean  isAllMaterail()
    {
      boolean isAllMaterail = false;
      RowMap detail = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detail = (RowMap)d_RowInfos.get(i);
        String rwdmxid = detail.get("rwdmxid");
        if(!rwdmxid.equals(""))
          return isAllMaterail=true;
      }
      return isAllMaterail;
    }
    */
   /**
    * 校验主表表表单信息从表输入的信息的正确性
    * @return null 表示没有信息,校验通过
    */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("kdrq");
      if(temp.equals(""))
        return showJavaScript("alert('日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法日期！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择车间！');");
      /*
      temp = rowInfo.get("gxfdid");
      if(temp.equals("")&&sfwjg.equals("0"))
        return showJavaScript("alert('请选择工序分段！');");

      temp = rowInfo.get("dwtxid");
      if(temp.equals("")&&sfwjg.equals("1"))
        return showJavaScript("alert('请选择加工厂！');");
            */
      /*
      temp = rowInfo.get("jsfsid");
      if(temp.equals("")&&sfwjg.equals("1"))
        return showJavaScript("alert('请选择结算方式！');");
      */
      return null;
    }
  }
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
      EngineDataSet ds = getMaterTable();
      ds.goToInternalRow(masterRow);


      String id = ds.getValue("jgdid");
      String count = dataSetProvider.getSequence("SELECT count(*) FROM sc_piecewage a WHERE a.jgdid='"+id+"'");
      if(!count.equals("0"))
      {
        data.setMessage(showJavaScript("alert('该加工单已被引用,不能删除!')"));
        return;
      }
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM sc_jgd a WHERE a.jgdid='"+id+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        data.setMessage(showJavaScript("alert('该加工单不能删除!')"));
        return;
      }





      //dsProcessMaterail.deleteAllRows();
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      //
      d_RowInfos.clear();
      //d_MaterailBill.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   * 主表删除操作
   */
  class CommonMaster_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(isMasterAdd){
        data.setMessage(showJavaScript("backList();"));
        return;
      }
      EngineDataSet ds = getMaterTable();
      ds.goToInternalRow(masterRow);
      String scjhid = ds.getValue("scjhid");
      dsDetailTable.first();
      for(int i=0;i<dsDetailTable.getRowCount();i++)
      {
        //dsDrawMaterail.deleteAllRows();
        dsDetailTable.next();
      }
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      /*
      String updateSQL = " update sc_jh  set zt = 2 where scjhid = '" + scjhid + "'"
                       + " and ( select count(*) from sc_jgd b where b.scjhid = '" + scjhid + "') = 1 ";
      dsUpdateScjhState.updateQuery(new String[]{updateSQL});
      if(dsUpdateScjhState.isOpen())
      {
        dsUpdateScjhState.readyRefresh();
        dsUpdateScjhState.refresh();
      }
      else
        dsUpdateScjhState.openDataSet();
      dsUpdateScjhState.saveChanges();
      */
      /*if(dsUpdateScjhState.isOpen())
      {
        dsUpdateScjhState.readyRefresh();
        dsUpdateScjhState.refresh();
      }
      else
        dsUpdateScjhState.openDataSet();
      */
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      //
      d_RowInfos.clear();
      //d_CommonMaterail.clear();
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
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      searchTable.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("a.deptid", "a.zdrid"), sfwjg, fgsid,"", SQL});
      if(!dsMasterList.getQueryString().equals(SQL))
      {
        dsMasterList.setQueryString(SQL);
        dsMasterList.setRowMax(null);
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
      EngineDataSet master = dsMasterList;
      //EngineDataSet detail = dsMasterTable;
      if(!master.isOpen())
        master.openDataSet();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("jgdh"), null, null, null),
        new QueryColumn(master.getColumn("kdrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("kdrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("djh"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("djh"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        new QueryColumn(master.getColumn("jgdid"), "sc_jgdmx", "jgdid", "cpid", null, "="),//从表品名
        new QueryColumn(master.getColumn("jgdid"), "VW_SCJGD_QUERY", "jgdid", "cpbm", "cpbm", "like"),//从表产品编码
        new QueryColumn(master.getColumn("jgdid"), "VW_SCJGD_QUERY", "jgdid", "product", "product", "like"),//从表品名
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
  //单位改变
  class DWTX_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);
    }
  }
  class GzzChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {


      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      RowMap rowInfo = getMasterRowinfo();
      detail.deleteAllRows();
      d_RowInfos.clear();
      /*
      String deptid = rowInfo.get("deptid");
      String gzzid =  rowInfo.get("gzzid");
      String cpid  = rowInfo.get("cpid");
      EngineDataSet tmp = new EngineDataSet();
      setDataSetProperty(tmp,combineSQL(GZZ_RY_SQL,"?",new String[]{" and a.deptid='"+deptid+"' and a.gzzid='"+gzzid+"'"}));
      //GZZ_RY_SQL+" and a.deptid='"+deptid+"' and a.gzzid='"+gzzid+"'");
      tmp.open();
      tmp.first();
      for(int i=0,n=tmp.getRowCount();i<n;i++)
      {
        detail.insertRow(false);
        detail.setValue("personid",tmp.getValue("personid"));
        detail.setValue("jgdmxid","-1");
        detail.setValue("cpid",cpid);


        detail.post();
        RowMap detailrow = new RowMap(detail);
        detailrow.put("internalRowNum",String.valueOf(detail.getInternalRow()));
        d_RowInfos.add(detailrow);
        tmp.next();
      }
      */
    }
  }
  /**
   *  根据生产任务单从表增加操作
 class Detail_Select_Task implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     //保存输入的明细信息
     putDetailInfo(data.getRequest());
     RowMap rowInfo = getMasterRowinfo();

     String mutitask = m_RowInfo.get("mutitask");
     if(mutitask.length() == 0)
       return;

   //实例化查找数据集的类
     EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "rwdmxid");
     String[] rwdmxID = parseString(mutitask,",");
     if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
       String jgdid = dsMasterTable.getValue("jgdid");
     for(int i=0; i < rwdmxID.length; i++)
     {
       if(rwdmxID[i].equals("-1"))
         continue;
       RowMap detailrow = null;
       locateGoodsRow.setValue(0, rwdmxID[i]);
       if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
       {
         RowMap importTaskRow = getTaskGoodsBean(req).getLookupRow(rwdmxID[i]);
         dsDetailTable.insertRow(false);
         dsDetailTable.setValue("jgdmxid", "-1");
         dsDetailTable.setValue("rwdmxid",rwdmxID[i]);
         dsDetailTable.setValue("cpid", importTaskRow.get("cpid"));
         dsDetailTable.setValue("gylxid", importTaskRow.get("gylxid"));
         dsDetailTable.setValue("sl", importTaskRow.get("wjgl"));
         dsDetailTable.setValue("scsl", importTaskRow.get("wjgscl"));
         dsDetailTable.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
         String dmsxid = importTaskRow.get("dmsxid");
         dsDetailTable.setValue("dmsxid", importTaskRow.get("dmsxid"));
         dsDetailTable.setValue("jgyq", importTaskRow.get("jgyq"));
         dsDetailTable.post();
         //创建一个与用户相对应的行
         detailrow = new RowMap(dsDetailTable);
         d_RowInfos.add(detailrow);
       }
     }
   }
  }
  */
 /**
  *  选择任务单主表，引入从表所有未加工信息
  *  jit中改成引物料需求.
  *  04.10.27 18:18 新增了从sc_sjbom中取出一笔cpid数据是否可以作为物料插入到生产加工单明细物料表中的判断
  *  1.取一笔sc_sjbom中的数据是作为物料插入到sc_jgdmxwl中的sql逻辑做了修改.加入到gxfdid的条件
  *  2.把sc_wlxqjhmx中的符合条件的一笔cpid数据插入到sc_jgdmx表中的时候,关联到cpid到sc_sjbom去得到wgcl也插进去

  class Single_Select_Task implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String singleImportTask = m_RowInfo.get("singleImportTask");
      if(singleImportTask.equals(""))
        return;
      String SQL = TASK_DETAIL_SQL+singleImportTask;
      EngineDataSet tempTaskData = null;
      if(tempTaskData==null)
      {
        tempTaskData = new EngineDataSet();
        setDataSetProperty(tempTaskData,null);
      }
      tempTaskData.setQueryString(SQL);
      if(!tempTaskData.isOpen())
        tempTaskData.openDataSet();
      else
        tempTaskData.refresh();

      EngineDataSet detail = getDetailTable();
      detail.deleteAllRows();
      d_RowInfos.clear();
      RowMap taskMasterRow = getTaskMasterBean(req).getLookupRow(singleImportTask);
      scjhid = taskMasterRow.get("scjhid");
      rowInfo.put("scjhid",scjhid);
      rwdid = singleImportTask;
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wlxqjhmxid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String jgdid = dsMasterTable.getValue("jgdid");
      tempTaskData.first();
      for(int i=0; i<tempTaskData.getRowCount(); i++)
      {
        tempTaskData.goToRow(i);
        String scjhmxid = tempTaskData.getValue("scjhmxid");
        String cpid = tempTaskData.getValue("cpid");
        String dmsxid = tempTaskData.getValue("dmsxid");
        locateGoodsRow.setValue(0, scjhmxid);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          double sl = tempTaskData.getValue("xgl").length()>0 ? Double.parseDouble(tempTaskData.getValue("xgl")) : 0;//需购量(计量单位)
          double yjgl = tempTaskData.getValue("yprwl").length()>0 ? Double.parseDouble(tempTaskData.getValue("yprwl")) : 0;//任务单回填数据
          double scsl = tempTaskData.getValue("xql").length()>0 ? Double.parseDouble(tempTaskData.getValue("xql")) : 0;//生产单位需求量
          double yjgscl = tempTaskData.getValue("yprwcsl").length()>0 ? Double.parseDouble(tempTaskData.getValue("yprwcsl")) : 0;//任务单生产数量回填数据
          double wjgl = sl-yjgl>0 ? sl-yjgl : 0;
          double wjgscl = scsl-yjgscl>0 ? scsl-yjgscl :0;
          if(wjgl==0)
            continue;
          detail.insertRow(false);
          //if(jglx.equals("1"))
          // detail.setValue("jgdmxid", "-1");
          //else
            detail.setValue("jgdmxid", String.valueOf(procDetaiLlSeq));
            //准备取得当前tempTaskData数据集物料须求明细中cpid,dmsxid对应的的wgcl值
          String wgcltemp = null;
          if(dmsxid.equals(""))
            wgcltemp =" AND b.dmsxid IS NULL";
          else
            wgcltemp = " AND b.dmsxid ="+ dmsxid;
            //String WGCL_SQL = combineSQL(GET_PER_SC_JGDMX_WGCL, "?", new String[]{wlxqjhmxid, scjhid, cpid, wgcltemp});
            //String WgclForSc_jgdmx = dataSetProvider.getSequence(WGCL_SQL);
            //detail.setValue("wgcl",WgclForSc_jgdmx);
            //detail.setValue("wlxqjhmxid",wlxqjhmxid);
          detail.setValue("cpid", cpid);
          detail.setValue("sl", String.valueOf(wjgl));
          detail.setValue("scsl", String.valueOf(wjgscl));
          //detail.setValue("gylxid", tempTaskData.getValue("gylxid"));
          detail.setValue("dmsxid", tempTaskData.getValue("dmsxid"));//规格属性
          //detail.setValue("jgyq", tempTaskData.getValue("jgyq"));//加工要求
          detail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);

          detail.post();
          //创建一个与用户相对应的行
          RowMap detailrow = new RowMap(detail);
          d_RowInfos.add(detailrow);
          if(jglx.equals("0")){
            String temp = null;
            if(dmsxid.equals(""))
              temp =" AND b.dmsxid IS NULL";
            else
              temp = " AND b.dmsxid ="+ dmsxid;

              //String M_SQL = combineSQL(MATERAIL_LIST_SQL, "?", new String[]{wlxqjhmxid, scjhid, cpid, temp});
              //dsFactBomMaterail.setQueryString(M_SQL);
            if(!dsFactBomMaterail.isOpen())
              dsFactBomMaterail.openDataSet();
            else
              dsFactBomMaterail.refresh();
            int row = dsFactBomMaterail.getRowCount();
            dsFactBomMaterail.first();

            boolean isMaterialVariable = false;//判断是不是可以插入到物料清单中的物料
            engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_PRODUCT);
            for(int t=0; t<dsFactBomMaterail.getRowCount(); t++){
              String cpidA = dsFactBomMaterail.getValue("cpid");
              String dmsxidA = dsFactBomMaterail.getValue("dmsxid");
              String sjsjbomid = dsFactBomMaterail.getValue("sjsjbomid");
              String wgcl = dsFactBomMaterail.getValue("wgcl");
              String chxz = dsFactBomMaterail.getValue("chxz");
              prodBean.regData(dsFactBomMaterail,"cpid");
              RowMap  prodRow= prodBean.getLookupRow(cpidA);
              String jhdj = prodRow.get("jhdj");
              //isMaterialVariable = false;

              //isMaterialVariable = isMaterial(sjsjbomid, gxfdid);
              //if (isMaterialVariable)
              //{
              dsDrawMaterail.insertRow(false);
              dsDrawMaterail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
              dsDrawMaterail.setValue("jgdwlid", "-1");
              dsDrawMaterail.setValue("dmsxid", dsFactBomMaterail.getValue("dmsxid"));
              dsDrawMaterail.setValue("cpid", dsFactBomMaterail.getValue("cpid"));
              dsDrawMaterail.setValue("sl", dsFactBomMaterail.getValue("xql"));
              dsDrawMaterail.setValue("scsl", dsFactBomMaterail.getValue("scxql"));
              dsDrawMaterail.setValue("jgdmxid", String.valueOf(procDetaiLlSeq));
              dsDrawMaterail.setValue("wgcl", dsFactBomMaterail.getValue("wgcl"));
              dsDrawMaterail.setValue("wldj", jhdj);
              dsDrawMaterail.post();
              RowMap Materailrow = new RowMap(dsProcessMaterail);
              d_CommonMaterail.add(Materailrow);
              //}
              dsFactBomMaterail.next();
            }
          }
          //if(!jglx.equals("1"))
          //  procDetaiLlSeq--;
          tempTaskData.next();
        }
      }
      if(jglx.equals("1")){
      //String BOM_SQL = combineSQL(SUBMATERAIL_LIST_SQL, "?", new String[]{rwdmxidBuf.toString(), scjhid, cpidBuf.toString()});
        dsFactBomMaterail.setQueryString(BOM_SQL);
        if(dsFactBomMaterail.isOpen())
          dsFactBomMaterail.refresh();
        else
          dsFactBomMaterail.openDataSet();
        dsFactBomMaterail.first();
        for(int t=0; t<dsFactBomMaterail.getRowCount(); t++){
          dsProcessMaterail.insertRow(false);
          dsProcessMaterail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
          dsProcessMaterail.setValue("jgdwlid", "-1");
          dsProcessMaterail.setValue("dmsxid", dsFactBomMaterail.getValue("dmsxid"));
          dsProcessMaterail.setValue("cpid", dsFactBomMaterail.getValue("cpid"));
          dsProcessMaterail.setValue("sl", dsFactBomMaterail.getValue("xql"));
          dsProcessMaterail.setValue("scsl", dsFactBomMaterail.getValue("scxql"));
          //dsProcessMaterail.setValue("jgdmxid", String.valueOf(procDetaiLlSeq));
          dsProcessMaterail.post();
          RowMap Materailrow = new RowMap(dsProcessMaterail);
          d_MaterailBill.add(Materailrow);
          dsFactBomMaterail.next();
        }
      }
    }
  }
  */
 /**
  *  选择任务单主表，引入从表所有未加工信息
  *  jit中改成引物料需求.
  *  04.10.27 18:18 新增了从sc_sjbom中取出一笔cpid数据是否可以作为物料插入到生产加工单明细物料表中的判断
  *  1.取一笔sc_sjbom中的数据是作为物料插入到sc_jgdmxwl中的sql逻辑做了修改.加入到gxfdid的条件
  *  2.把sc_wlxqjhmx中的符合条件的一笔cpid数据插入到sc_jgdmx表中的时候,关联到cpid到sc_sjbom去得到wgcl也插进去

  class MultiSelect_SCBOM implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();


      String singleImportTask = m_RowInfo.get("singleImportTask");
      if(singleImportTask.equals(""))
        return;
      String[] tags = parseString(singleImportTask,",");

      String[] tmpscjhidarray = parseString(tags[0],"#");
      scjhid = tmpscjhidarray[0];

      EngineDataSet detail = getDetailTable();
      detail.deleteAllRows();
      d_RowInfos.clear();
      //RowMap taskMasterRow = getTaskMasterBean(req).getLookupRow(singleImportTask);
      //scjhid = taskMasterRow.get("scjhid");
      //rwdid = singleImportTask;
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[]{"cpid", "dmsxid", "scjhmxid"});
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String jgdid = dsMasterTable.getValue("jgdid");
      for (int j = 0 ; j < tags.length; j++){
        String tmp = tags[j];
        if (tmp.equals("-1"))
          continue;
        String SQL = combineSQL(SC_JGD_DETAIL_SJBOM, "?", new String[]{tmp});
        EngineDataSet tempTaskData = null;
        tempTaskData = new EngineDataSet();
        setDataSetProperty(tempTaskData,null);
        tempTaskData.setQueryString(SQL);
        tempTaskData.openDataSet();
        tempTaskData.first();
        for(int i=0; i<tempTaskData.getRowCount(); i++)
        {
          tempTaskData.goToRow(i);
          String scjhmxid = tempTaskData.getValue("scjhmxid");
          String cpid = tempTaskData.getValue("cpid");
          String dmsxid = tempTaskData.getValue("dmsxid");
          String sjbomid = tempTaskData.getValue("sjbomid");
          String gxfdid = tempTaskData.getValue("gxfdid");
          scjhid = tempTaskData.getValue("scjhid");
          locateGoodsRow.setValue(0, cpid);
          locateGoodsRow.setValue(1, dmsxid);
          locateGoodsRow.setValue(2, scjhmxid);
          if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
          {
            double sl = Double.parseDouble(tempTaskData.getValue("sl").equals("")?"0":tempTaskData.getValue("sl"));//tempTaskData.getValue("xgl").length()>0 ? Double.parseDouble(tempTaskData.getValue("xgl")) : 0;//需购量(计量单位)
            //double yjgl = tempTaskData.getValue("yprwl").length()>0 ? Double.parseDouble(tempTaskData.getValue("yprwl")) : 0;//任务单回填数据
            double scsl =  Double.parseDouble(tempTaskData.getValue("scsl").equals("")?"0":tempTaskData.getValue("scsl"));//tempTaskData.getValue("xql").length()>0 ? : 0;//生产单位需求量
            //double yjgscl = tempTaskData.getValue("yprwcsl").length()>0 ? Double.parseDouble(tempTaskData.getValue("yprwcsl")) : 0;//任务单生产数量回填数据
            double wjgl = 0;//sl-yjgl>0 ? sl-yjgl : 0;

            detail.insertRow(false);
            if(jglx.equals("1"))
              detail.setValue("jgdmxid", "-1");
            else
              detail.setValue("jgdmxid", String.valueOf(procDetaiLlSeq));
              //准备取得当前tempTaskData数据集物料须求明细中cpid,dmsxid对应的的wgcl值
            String wgcltemp = null;
            if(dmsxid.equals(""))
              wgcltemp =" AND b.dmsxid IS NULL";
            else
              wgcltemp = " AND b.dmsxid ="+ dmsxid;
              //String WGCL_SQL = combineSQL(GET_PER_SC_JGDMX_WGCL, "?", new String[]{wlxqjhmxid, scjhid, cpid, wgcltemp});
              //String WgclForSc_jgdmx = dataSetProvider.getSequence(WGCL_SQL);
              //detail.setValue("wgcl",WgclForSc_jgdmx);
              //detail.setValue("wlxqjhmxid",wlxqjhmxid);
            detail.setValue("cpid", cpid);
            detail.setValue("sl", String.valueOf(sl));//wjgl
            detail.setValue("scsl", String.valueOf(scsl));//wjgscl
            detail.setValue("wgcl", tempTaskData.getValue("wgcl"));
            //detail.setValue("gylxid", tempTaskData.getValue("gylxid"));
            detail.setValue("dmsxid", tempTaskData.getValue("dmsxid"));//规格属性
            //detail.setValue("jgyq", tempTaskData.getValue("jgyq"));//加工要求
            detail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
            detail.setValue("scjhmxid", scjhmxid);
            detail.post();
            //创建一个与用户相对应的行
            RowMap detailrow = new RowMap(detail);
            d_RowInfos.add(detailrow);
            if(jglx.equals("0")){
              String temp = null;
              if(dmsxid.equals(""))
                temp =" AND b.dmsxid IS NULL";
              else
                temp = " AND b.dmsxid ="+ dmsxid;

              String M_SQL = combineSQL(MATERAIL_LIST_SQL, "?", new String[]{sjbomid,gxfdid});
              //dsFactBomMaterail.setQueryString(M_SQL);
              if(!dsFactBomMaterail.isOpen())
                dsFactBomMaterail.openDataSet();
              else
                dsFactBomMaterail.refresh();
              int row = dsFactBomMaterail.getRowCount();
              dsFactBomMaterail.first();

              boolean isMaterialVariable = false;//判断是不是可以插入到物料清单中的物料
              engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_PRODUCT);
              for(int t=0; t<dsFactBomMaterail.getRowCount(); t++){
                String cpidA = dsFactBomMaterail.getValue("cpid");
                String dmsxidA = dsFactBomMaterail.getValue("dmsxid");
                String sjsjbomid = dsFactBomMaterail.getValue("sjsjbomid");
                String wgcl = dsFactBomMaterail.getValue("wgcl");
                String chxz = dsFactBomMaterail.getValue("chxz");
                prodBean.regData(dsFactBomMaterail,"cpid");
                RowMap  prodRow= prodBean.getLookupRow(cpidA);
                String jhdj = prodRow.get("jhdj");
                //isMaterialVariable = false;

                //isMaterialVariable = isMaterial(sjsjbomid, gxfdid);
                //if (isMaterialVariable)
                //{
                dsDrawMaterail.insertRow(false);
                dsDrawMaterail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
                dsDrawMaterail.setValue("jgdwlid", "-1");
                dsDrawMaterail.setValue("dmsxid", dsFactBomMaterail.getValue("dmsxid"));
                dsDrawMaterail.setValue("cpid", dsFactBomMaterail.getValue("cpid"));
                dsDrawMaterail.setValue("sl", dsFactBomMaterail.getValue("xql"));
                dsDrawMaterail.setValue("scsl", dsFactBomMaterail.getValue("scxql"));
                dsDrawMaterail.setValue("jgdmxid", String.valueOf(procDetaiLlSeq));
                dsDrawMaterail.setValue("wgcl", dsFactBomMaterail.getValue("wgcl"));
                dsDrawMaterail.setValue("wldj", jhdj);
                dsDrawMaterail.post();
                RowMap Materailrow = new RowMap(dsProcessMaterail);
                d_CommonMaterail.add(Materailrow);
                //}
                dsFactBomMaterail.next();
              }
            }
            if(!jglx.equals("1"))
              procDetaiLlSeq--;
            tempTaskData.next();
          }
        }
      }
      if(jglx.equals("1")){
      //String BOM_SQL = combineSQL(SUBMATERAIL_LIST_SQL, "?", new String[]{rwdmxidBuf.toString(), scjhid, cpidBuf.toString()});
        dsFactBomMaterail.setQueryString(BOM_SQL);
        if(dsFactBomMaterail.isOpen())
          dsFactBomMaterail.refresh();
        else
          dsFactBomMaterail.openDataSet();
        dsFactBomMaterail.first();
        for(int t=0; t<dsFactBomMaterail.getRowCount(); t++){
          dsProcessMaterail.insertRow(false);
          dsProcessMaterail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
          dsProcessMaterail.setValue("jgdwlid", "-1");
          dsProcessMaterail.setValue("dmsxid", dsFactBomMaterail.getValue("dmsxid"));
          dsProcessMaterail.setValue("cpid", dsFactBomMaterail.getValue("cpid"));
          dsProcessMaterail.setValue("sl", dsFactBomMaterail.getValue("xql"));
          dsProcessMaterail.setValue("scsl", dsFactBomMaterail.getValue("scxql"));
          //dsProcessMaterail.setValue("jgdmxid", String.valueOf(procDetaiLlSeq));
          dsProcessMaterail.post();
          RowMap Materailrow = new RowMap(dsProcessMaterail);
          d_MaterailBill.add(Materailrow);
          dsFactBomMaterail.next();
        }
      }
      rowInfo.put("scjhid",scjhid);
    }
  }
  */
 /**
  *改变车间触发的事件
  */
  class Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      /*
      EngineDataSet detail = getDetailTable();
      detail.first();
      while(detail.inBounds())
      {
        String wlxqjhmxid = detail.getValue("wlxqjhmxid");
        if(!wlxqjhmxid.equals(""))
        {
          d_RowInfos.remove(detail.getRow());
          detail.deleteRow();
        }
        else
          detail.next();
      }
      */
    }
  }
  /**
   *改变车间触发的事件
   */
  class Detail_Refresh implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
    }
  }
  /**
   *输入产品编码触发的事件
   */
  class Product_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
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
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();
      //if(!jglx.equals("1"))
      //  putCommonInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
      //if(!isMasterAdd)
      //  ds.goToInternalRow(masterRow);
      //String jgdid = dsMasterTable.getValue("jgdid");
      detail.insertRow(false);
      //String jgdmxid = dataSetProvider.getSequence("s_sc_jgdmx");
      detail.setValue("jgdmxid", "-1");
      detail.setValue("cpid", rowInfo.get("cpid"));
      //detail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
      detail.post();
      d_RowInfos.add(new RowMap(detail));
    }
  }
  /**
   * 复制当前行
   *

   class Detail_Copy_Add implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       HttpServletRequest req = data.getRequest();
       putDetailInfo(data.getRequest());
       String rownum = req.getParameter("rownum");
       tCopyNumber = req.getParameter("tCopyNumber");
       int row = Integer.parseInt(rownum);
       int size = d_RowInfos.size();
       if(row>size)
         return;
       RowMap newadd = (RowMap)d_RowInfos.get(row);

       String cpid = newadd.get("cpid");
       String personid = newadd.get("personid");

       String ksrq = newadd.get("ksrq");
       String wcrq = newadd.get("wcrq");
       String jgyq = newadd.get("jgyq");
       String wgcl = newadd.get("wgcl");
       String jgdj = newadd.get("jgdj");
       String jgje = newadd.get("jgje");


       int copynumber = Integer.parseInt(tCopyNumber);

       for(int i=0;i<copynumber;i++)
       {
         dsDetailTable.insertRow(false);

         dsDetailTable.setValue("jgdmxid", "-1");
         dsDetailTable.setValue("jgdid", jgdid);
         dsDetailTable.setValue("cpid", cpid);
         dsDetailTable.setValue("personid", personid);

         dsDetailTable.setValue("ksrq", ksrq);
         dsDetailTable.setValue("wcrq", wcrq);
         dsDetailTable.setValue("jgyq", jgyq);
         dsDetailTable.setValue("wgcl", wgcl);
         dsDetailTable.setValue("jgdj", jgdj);
         dsDetailTable.setValue("jgje", jgje);


         dsDetailTable.post();
         RowMap detailrow = new RowMap(dsDetailTable);
         detailrow.put("internalRowNum",String.valueOf(dsDetailTable.getInternalRow()));
         d_RowInfos.add(detailrow);
       }
       tCopyNumber="1";
     }
  }
  * */
 /**
  *  增加分切加工单才有的功能
  *  加工单物料清单增加操作(增加一个空白行)

  class Materail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet dsMaterail = getMaterailTable();
      EngineDataSet ds = getMaterTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String jgdid = dsMasterTable.getValue("jgdid");
      dsMaterail.insertRow(false);
      dsMaterail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
      dsMaterail.post();
      d_MaterailBill.add(new RowMap());
    }
  }
  */
 /**
  *  分切加工单才有的功能
  *  物料删除操作

  class Materail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet dsMaterail = getMaterailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      d_MaterailBill.remove(rownum);
      dsMaterail.goToRow(rownum);
      dsMaterail.deleteRow();
    }
  }
  */
 /**
  *  强制完成触发事件.此操作就是入库确认的操作,由入库确认按钮触发.
  *  根据已排工作量和加工数量手工完成操作
  */
  class Complete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsMasterList.goToRow(row);

      String jgdid = dsMasterList.getValue("jgdid");
      /*
      String totDetailSum = dataSetProvider.getSequence("select nvl(sum(jgje), 0) from sc_jgdmx where jgdid = " + jgdid);
      String totMaterialSum = dataSetProvider.getSequence("select nvl(sum(wlje), 0) from sc_jgdwl where jgdid = " + jgdid);
      BigDecimal totMasterSum = new BigDecimal(totDetailSum).subtract(new BigDecimal(totMaterialSum));
      if (sfwjg.equals("1"))
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //汇总明细表金额给主表中的zje
        dsMasterTable.setValue("zje", totMasterSum.toString());
        dsMasterTable.setValue("rq", today);
      }
      */
      dsMasterList.setValue("zt", "8");
      dsMasterList.post();
      dsMasterList.saveChanges();
    }
  }
  /**
   *  从表增加操作
   */
  class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getDetailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
    }
  }
  /**
   *  从表增加操作

  class Detail_Delete_Select implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      String[]sels = data.getParameterValues("sel");
      for(int i=0;i<sels.length;i++)
      {
        long dtRow = Long.parseLong(sels[i]);
        detail.goToInternalRow(dtRow);
        detail.deleteRow();
      }
      initRowInfo(false,false,true);
    }
  }
  */
 /**
  * 得到用于查找生产任务单信息的bean
  * @param req WEB的请求
  * @return 返回用于查找生产任务单信息的bean

 public ImportTask getTaskGoodsBean(HttpServletRequest req)
 {
   if(importTaskBean == null)
     importTaskBean = ImportTask.getInstance(req);
   return importTaskBean;
 }
 */
/**
 * 得到用于查找生产任务单主表的一条信息的bean
 * @param req WEB的请求
 * @return 返回用于查找生产任务单主表的一条信息的bean
 */
  public B_Process_SingleSelTask getTaskMasterBean(HttpServletRequest req)
  {
    if(singleSelTaskBean == null)
      singleSelTaskBean = B_Process_SingleSelTask.getInstance(req);
    return singleSelTaskBean;
  }
  public boolean isMaterial( String sjsjbomid, String gxfdid)
  {
    //存货性质如是1则说明是自制件否则是外购件
    //if (!chxz.equals("1"))
    //{
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
    //BOM领料工段 (sc_bomsection) 中的gxfdid与加工单页面上取得的gxfdid如相等就可以当成物料插入
    if (tmpgxfdid.equals(gxfdid))
      return true; //是可以加入到物料清单中的物料
    else
      return false;
     /*}
    else if (chxz.equals("1"))//自制件
    {

      1.如是外购件,先判断wgcl是流转还是入库
      2.if wgcl = 入库则剩下的步骤与 if chxz = 1时的处理步骤相同.否则.wgcl<> 入库那么空操作.继续下一次loop
      if (wgcl.equals("1"))//入库
      {
        String getBomGxfdidSql = " SELECT gxfdid FROM sc_bomsection sfd "
                               + " WHERE sfd.bomid = "
                               + " ( SELECT DISTINCT bomid FROM sc_sjbom "
                               + "     WHERE sjbomid = '" + sjsjbomid + "'"
                               + " )";
        String tmpgxfdid = dataSetProvider.getSequence(getBomGxfdidSql);
        tmpgxfdid = tmpgxfdid == null?"":tmpgxfdid;
    //BOM领料工段 (sc_bomsection) 中的gxfdid与加工单页面上取得的gxfdid如相等就可以当成物料插入
        if (tmpgxfdid.equals(gxfdid))
          return true;
        else
          return false;
      }
    }*/
    //return false;
  }
}


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
//import engine.erp.produce.ImportMrp;
import engine.erp.jit.ProducePlan;

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
 * <p>Title: 生产计划中下达车间任务</p>
 * <p>Description: 生产计划中下达车间任务<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨建国
 * @version 1.0
 */

public final class PlanMakeProcess extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";
  public  static final String ONCHANGE = "10031";
  public  static final String CANCEL_APPROVE = "18531";//取消审批操作
  //public  static final String SINGLE_SELECT_PRODUCT = "10041";//单选产品操作
  //public  static final String PRODUCT_ONCHANGE = "10060";//从表输入产品编码触发事件
  public static final String BUILD_PROCESS_BILL = "10002";//生产加工单事件

  private static final String MRP_DETAIL_SQL =
      "SELECT * FROM VW_PRODUCETASK_SEL_MRP WHERE fgsid=? AND (deptid IS NULL OR deptid=?) and scjhid=? ";
  private static final String MRP_DETAIL_STRUCT_SQL =
      "SELECT * FROM VW_PRODUCETASK_SEL_MRP WHERE 1<>1 ";
  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_jgd WHERE 1<>1";
  private static final String MASTER_SQL    =
      "SELECT * FROM sc_jgd WHERE ? AND fgsid='?' AND jgdid IN(SELECT DISTINCT a.jgdid FROM sc_jgdmx a, sc_wlxqjh b, sc_wlxqjhmx c WHERE a.wlxqjhmxid=c.wlxqjhmxid AND b.wlxqjhid= c.wlxqjhid AND b.scjhid='?') ORDER BY jgdh DESC";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_jgdmx WHERE 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sc_jgdmx WHERE jgdid='?' ";//
  private static final String UPDATE_SQL = "UPDATE sc_jh SET zt='3' WHERE scjhid= ";//下达车间任务后改变生产计划状态
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM sc_jgd WHERE rwdid='?'";
  //
  //生产加工单的表结构SQL语句
  private static final String PROCESSMASTER_STRUT_SQL = "SELECT * FROM sc_jgd WHERE 1<>1";
  private static final String PROCESSDETAIL_STRUT_SQL = "SELECT * FROM sc_jgdmx WHERE 1<>1";
  //生成加工单后直接生成领料清单
  private static final String DRAW_MATETAIL_SQL = "SELECT * FROM sc_jgdwl where 1<>1";
  //通过加工单主表生产计划ID和从表的任务单明细ID得到合同ID再根据cpid等于实际BOM表的上级cpid得到物料
  private static final String MATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, a.htid "
      + " FROM sc_sjbom a WHERE a.sjsjbomid IN (SELECT b.sjbomid "
      + " FROM sc_sjbom b, sc_jh c, sc_jhmx d WHERE b.scjhmxid=d.scjhmxid AND c.scjhid=d.scjhid  AND  nvl(b.htid,-1) IN( "
      + " SELECT nvl(d.htid,-1) FROM sc_wlxqjhmx d WHERE d.wlxqjhmxid=e.wlxqjhmxid ) "
      + " AND c.scjhid='?' AND b.cpid='?' ?) "
      + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, a.htid ORDER BY a.cpid ";
  /**
  private static final String MATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, a.htid, b.scjhid "
         + " FROM sc_sjbom a, sc_jh b, sc_jhmx c WHERE a.scjhmxid=c.scjhmxid AND b.scjhid=c.scjhid AND a.htid IN( "
         + " SELECT d.htid FROM sc_wlxqjhmx d, sc_rwdmx e WHERE d.wlxqjhmxid=e.wlxqjhmxid AND  e.rwdmxid='?') AND b.scjhid='?' AND a.sjcpid='?' "
         + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, a.htid, b.scjhid ORDER BY a.cpid ";
         */
        //分切计划通过加工单主表生产计划ID和从表的任务单明细ID得到合同ID再根据cpid等于实际BOM表的上级cpid得到物料
  private static final String SUBMATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, b.scjhid "
      + " FROM sc_sjbom a, sc_jh b, sc_jhmx c WHERE a.scjhmxid=c.scjhmxid AND b.scjhid=c.scjhid AND nvl(a.htid,-1) IN( "
      + " SELECT nvl(d.htid,-1) FROM sc_wlxqjhmx d WHERE d.wlxqjhmxid=e.wlxqjhmxid ) AND b.scjhid='?' AND a.sjcpid IN(?) "
      + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, b.scjhid ORDER BY a.cpid ";
  //BOM表数据
  private static final String BOM_MATERAIL = "SELECT a.* FROM sc_bom a WHERE a.sjcpid='?' ";
  //对sc_wlxqjhmx按cpid,dmsxid,gylxid分组得出来的记录数就是要生成的加工单的张数
  private static final String GROUPBYGYLXID_MRPDETAIL = " SELECT ylxid FROM sc_wlxqjhmx s "
      + " WHERE s.chxz = 1 GROUP BY gylxid ";

  private EngineDataSet dsMrpMaster  = new EngineDataSet();//物料需求计划主表
  private EngineDataSet dsMrpDetail  = new EngineDataSet();//物料需求计划从表
  private EngineDataSet dsGroupByMrpDetail  = new EngineDataSet();//按gylxid对物料需求计划明细表分组

  private EngineDataSet dsFactBom = new EngineDataSet();;//实际BOM数据集
  private EngineDataSet dsDrawMaterail = new EngineDataSet();//领料请单数据集
  private EngineDataSet dsBom = new EngineDataSet();;//BOM数据集

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsProcessMaster = new EngineDataSet();//加工单主表数据集
  private EngineDataSet dsProcessDetail = new EngineDataSet();//加工单从表

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_rwd");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_rwdmx");
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  private LookUp productBean = null; //产品信息的bean的引用, 用于提取产品信息
  //private ImportMrp importMrpBean = null; //物料需求计划的bean的引用, 用于提取物料需求计划信息

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String rwdid = null;//任务单id
  private String scjhid = null;//生产计划id
  private String jhlx = null;//计划类型
  private String deptid = null;//计划类型
  private String jgdid = null;//计划类型
  boolean isAll =  false;//明细表中是否物料需求引入
  private int row = 0;//传入的生产计划的指针所在行
  private User user = null;
  private String SC_AUTO_PROCESS_BILL = null;//系统参数0=手动,1=自动生成加工单
  public String SC_PRODUCE_UNIT_STYLE =null;//1=强制换算,0=仅空值时换算
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  private String wlxqjhid = null;//生产任务单只能对应一张物料需求计划，即也只能对应一张生产计划
  public String SC_PLAN_ADD_STYLE = null;//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
  //private User user = null;
  /**
   * 生产计划中下达车间任务的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回生产计划中下达车间任务的实例
   */
  public static PlanMakeProcess getInstance(HttpServletRequest request)
  {
    PlanMakeProcess planMakeProcessBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "planMakeProcessBean";
      planMakeProcessBean = (PlanMakeProcess)session.getAttribute(beanName);
      if(planMakeProcessBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        planMakeProcessBean = new PlanMakeProcess();
        planMakeProcessBean.qtyFormat = loginBean.getQtyFormat();
        planMakeProcessBean.sumFormat = loginBean.getSumFormat();

        planMakeProcessBean.fgsid = loginBean.getFirstDeptID();
        planMakeProcessBean.loginId = loginBean.getUserID();
        planMakeProcessBean.loginName = loginBean.getUserName();
        planMakeProcessBean.loginDept = loginBean.getDeptID();
        planMakeProcessBean.user = loginBean.getUser();
        planMakeProcessBean.SC_PLAN_ADD_STYLE = loginBean.getSystemParam("SC_PLAN_ADD_STYLE");//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
        planMakeProcessBean.SC_AUTO_PROCESS_BILL = loginBean.getSystemParam("SC_AUTO_PROCESS_BILL");
        planMakeProcessBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//计量单位和生产单位是否强制换算
        planMakeProcessBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        planMakeProcessBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        //设置格式化的字段
        planMakeProcessBean.dsDetailTable.setColumnFormat("sl", planMakeProcessBean.qtyFormat);
        planMakeProcessBean.dsDetailTable.setColumnFormat("scsl", planMakeProcessBean.sumFormat);
        planMakeProcessBean.dsMasterTable.setColumnFormat("zsl", planMakeProcessBean.sumFormat);
        session.setAttribute(beanName, planMakeProcessBean);
      }
    }
    return planMakeProcessBean;
  }

  /**
   * 构造函数
   */
  private PlanMakeProcess()
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
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    setDataSetProperty(dsMrpDetail, null);
    setDataSetProperty(dsMrpMaster, null);
    setDataSetProperty(dsProcessMaster, PROCESSMASTER_STRUT_SQL);
    setDataSetProperty(dsProcessDetail, PROCESSDETAIL_STRUT_SQL);
    setDataSetProperty(dsDrawMaterail, DRAW_MATETAIL_SQL);
    setDataSetProperty(dsGroupByMrpDetail, null);//统计gylxid分组的个数
    setDataSetProperty(dsFactBom, null);
    setDataSetProperty(dsBom,null);

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"jgdh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"jgdmxid"}, new String[]{"s_sc_jgdmx"}));
    //如果系统参数自动生成加工单，得到主键
    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh') from dual"}));
    dsProcessDetail.setSequence(new SequenceDescriptor(new String[]{"jgdmxid"}, new String[]{"s_sc_jgdmx"}));
    //加工单物料即领料清单
    dsDrawMaterail.setSequence(new SequenceDescriptor(new String[]{"jgdwlid"}, new String[]{"s_sc_jgdwl"}));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    //addObactioner(String.valueOf(SINGLE_SELECT_PRODUCT), new Single_Product_Add());//单选产品
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());
    addObactioner(String.valueOf(BUILD_PROCESS_BILL), new Build_Process_Bill());//生产加工单事件
    //addObactioner(String.valueOf(PRODUCT_ONCHANGE), new Product_Onchange());
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
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
    }
    if(dsMrpMaster != null){
      dsMrpMaster.close();
      dsMrpMaster = null;
    }
    if(dsMrpDetail != null){
      dsMrpDetail.close();
      dsMrpDetail = null;
    }
    if(dsProcessDetail != null){
      dsProcessDetail.close();
      dsProcessDetail = null;
    }
    if(dsProcessMaster != null){
      dsProcessMaster.close();
      dsProcessMaster = null;
    }
    if(dsDrawMaterail != null){
      dsDrawMaterail.close();
      dsDrawMaterail = null;
    }
    if(dsFactBom != null){
      dsFactBom.close();
      dsFactBom = null;
    }
    if(dsBom != null){
      dsBom.close();
      dsBom = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
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
        m_RowInfo.put("rq", today);
        m_RowInfo.put("bm_deptid", loginDept);
        m_RowInfo.put("deptid", loginDept);
      }
    }
    else
    {
      EngineDataSet dsDetail = dsDetailTable;
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if(isInit)
        d_RowInfos.clear();

      dsDetail.first();
      for(int i=0; i<dsDetail.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDetail);
        d_RowInfos.add(row);
        dsDetail.next();
      }
    }
  }

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

    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品ID
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//计量单位数量
      detailRow.put("scsl", formatNumber(rowInfo.get("scsl_"+i), qtyFormat));//生产用单位数量
      detailRow.put("csl", rowInfo.get("csl_"+i));//超产率
      detailRow.put("gylxid", rowInfo.get("gylxid_"+i));//工艺路线
      detailRow.put("ksrq", rowInfo.get("ksrq_"+i));//开始日期
      detailRow.put("wcrq", rowInfo.get("wcrq_"+i));//完成日期
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性
      detailRow.put("jgyq", rowInfo.get("jgyq_"+i));//加工要求
      //保存用户自定义的字段
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    return dsDetailTable;
  }

  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : rwdid;
    SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});

    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    else
      dsDetailTable.refresh();
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
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
   * 初始化操作的触发类.
   *
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      scjhid= data.getParameter("scjhid");
      jhlx = data.getParameter("jhlx");
      String deptid = data.getParameter("deptid");
      row = Integer.parseInt(data.getParameter("row"));
      dsMasterTable.setQueryString(combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),fgsid, scjhid}));
      if(dsMasterTable.isOpen())
        dsMasterTable.refresh();
      else
        dsMasterTable.open();
      if(dsMasterTable.getRowCount()<1){
        isMasterAdd = true;
        dsMasterTable.setQueryString(MASTER_STRUT_SQL);
        dsMasterTable.setRowMax(null);
        if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
          dsDetailTable.empty();
      }
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
      rwdid = dsMasterTable.getValue("rwdid");
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
      isMasterAdd = String.valueOf(ADD).equals(action);
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        rwdid = dsMasterTable.getValue("rwdid");
      }
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);

      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
   * 生产计划top页面单击生成加工单事件
   * 响应从生产计划top页面单击生生成生产加工单按钮的事件.
   * 在当生成生产加工单页面弹出来的时候,就应该根据传过来的参数scjhdi,jhlx,deptid把物料须求取出来.
   * 给生产加工单主表和明细表.因此,就可以看到明细表里有已经有数据被填充进去了.
   * 流程:
   * 1.在生产加工单里查看是否有此笔生产计划生成的加工单(count).如没有则继续下面的动作,如有则不做操作
   * 2.在这笔生产计划生成的物料需求里按如下条件查找出来符合的物料需求数据,用来生成加工单
   *   2.1 取物料需求明细表中属于自制件的产品
   *   2.2 将物料需求明细表中工艺路线相等的明细数据分组.这样会将物料需求明细表数据集分成若干组
   *       每一组这样工艺路线相同的物料需求生成一张生产加工单.这样会生成n张加工单.工艺路线被同时带过去.
   *   2.3 加工单物料清单中的物料根据物料需求明细数据到bom中找到填入
   * 3. 在生成n个加工单并且保存后.把页面转到生产加工单的界面去,传递此笔scjhID过去,
   *    以使生产加工单找出刚刚用此笔scjhID生产的所有生产加工单
   *    并且传入供backlist返回的url为我们现在正在执行生成加工单的生产计划列表页面.供返回的时候回到当前的页面
   */
  class Build_Process_Bill implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      isMasterAdd = true;
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      //request到scjhid, jhlx, deptid从MRP_DETAIL_SQL中取出物料插入到明细表中
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      RowMap rowinfo = getMasterRowinfo();
      deptid = rowinfo.get("deptid");
      scjhid = data.getParameter("scjhid");
      jhlx = data.getParameter("jhlx");
      if(deptid.equals(""))
        return;
      //对物料需求明细表进行gylxid分组
      dsGroupByMrpDetail.setQueryString(GROUPBYGYLXID_MRPDETAIL);
      if (dsGroupByMrpDetail.isOpen())
        dsGroupByMrpDetail.refresh();
      else
        dsGroupByMrpDetail.open();
      ArrayList gylxidKinds = new ArrayList(dsGroupByMrpDetail.getRowCount());
      dsGroupByMrpDetail.first();
      for (int i=0; i<dsGroupByMrpDetail.getRowCount();i++)
      {
        gylxidKinds.add(dsGroupByMrpDetail.getValue("gylxid"));
        dsGroupByMrpDetail.next();
      }
      String sql = combineSQL(MRP_DETAIL_SQL, "?", new String[]{fgsid, deptid, scjhid});
      dsMrpDetail.setQueryString(sql);
      if(dsMrpDetail.isOpen())
        dsMrpDetail.refresh();
      else
        dsMrpDetail.open();
      int rowcount = dsMrpDetail.getRowCount();
      EngineRow locatrow = new EngineRow(dsDetailTable, "wlxqjhmxid");
      /*
      * 循环gylxidKinds次.每一种gylxid生成一张生产加工单.(即同时生成 加工单主表,明细表,加工单物料)
      * 1.从gylxidKinds gylxid ArrayList数组中取出一个gylxid.循环dsMrpDetail数据集,
      *   取出与gylxidKinds得到的gylxid相等的所有数据,用这些数据去生成一张生产加工单
      * 2. 循环下一个gylxidKinds.重复1中的操作
       */
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      for (int j=0;j<gylxidKinds.size();j++)
      {
        dsMrpDetail.first();
        //将物料需求明细数据插入到生产加工单明细表中生成一张生产加工单
        for(int i=0; i<rowcount; i++)
        {
          dsMrpDetail.goToRow(i);
          if(i==0)
            wlxqjhid = dsMrpDetail.getValue("wlxqjhid");
          //如果i=0则说明是一个新的gylxid进来了.那么就须要新增一张加工单主表.
          //否则说明还是在同一张gylxid的主表下新增数据的.因此只对加工单子表及物料清单进行操作
          if (i>0)
          {
            //下面的一组操作所做工作为:1.生成生产加工单明细表数据.2.生加工单物料清单
            detail_post((String)gylxidKinds.get(i));
            continue;
          }
          else//i==0则新增一张生产加工单主表
          {
            master_post();
          }
        }
        //每生成一张加工单主表,明细表,物料清单并且savechage后就把这三个数据集清空.等待下一个gylxid重新生成加工单.
        //一组gylxid的主表,明细表已经生成了.然后就根据明细表数据生成物料清单.
        wlDetail_post();
        //然后将这三张表一同savechange
        //ds.setAfterResolvedSQL(new String[]{UPDATE_SQL +scjhid});
        ds.saveDataSets(new EngineDataSet[]{ds, detail,  dsDrawMaterail});
        ds.deleteRow();
        detail.deleteAllRows();
        dsDrawMaterail.deleteAllRows();
      }
      dsMrpDetail.empty();
      //改变生产计划主表数据集，使当前行生产计划状态变为3
      ProducePlan producePlanBean = ProducePlan.getInstance(data.getRequest());
      EngineDataSet dsPlanMaster = producePlanBean.getMaterTable();
      dsPlanMaster.goToRow(row);
      String zt = dsPlanMaster.getValue("zt");
      if(zt.equals("2"))
      {
        dsPlanMaster.setValue("zt", "3");
        dsPlanMaster.post();
      }
      dsPlanMaster.saveChanges();
      data.setMessage(showJavaScript("toProduce_Process();"));
    }
    public void master_post() throws Exception
    {
      EngineDataSet ds = getMaterTable();
      //得到主表主键值
      String jgdid = null;
      //if(isMasterAdd){
      ds.insertRow(false);
      jgdid = dataSetProvider.getSequence("s_sc_jgd");
      //保存最基本的数据
      ds.setValue("jgdid", jgdid);
      ds.setValue("fgsid", fgsid);
      ds.setValue("wlxqjhid", wlxqjhid);
      ds.setValue("scjhid",scjhid);
      ds.setValue("deptid", deptid);//下达车间id
      isAll =  isAllMaterail();//从表中是否有一条纪录引入物料需求
      ds.setValue("wlxqjhid", isAll ? wlxqjhid : "");//物料需求计划ID
      ds.setValue("scjhid", isAll ? scjhid : "");//生产计划ID
      //ds.setValue("zsl", totalNum.toString());
      ds.setValue("rwlx", jhlx);
      ds.setValue("zt","0");
      ds.setValue("rq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//日期
      ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
      ds.setValue("zdrid", loginId);
      ds.setValue("zdr", loginName);//操作员
      //保存主表数据
      ds.post();
    }
    public void detail_post(String tempgylxid)
    {
      //保存从表的数据
      EngineDataSet detail = getDetailTable();
      EngineRow locatrow = new EngineRow(detail, "wlxqjhmxid");
      String wlxqjhmxid = dsMrpDetail.getValue("wlxqjhmxid");
      String gylxid = dsMrpDetail.getValue("gylxid");
      locatrow.setValue(0, wlxqjhmxid);
      if(!detail.locate(locatrow, Locate.FIRST)&&gylxid.equals(tempgylxid)){
        detail.insertRow(false);
        detail.setValue("jgdmxid","-1");
        detail.setValue("wlxqjhmxid", wlxqjhmxid);
        detail.setValue("jgdid", jgdid);
        detail.setValue("cpid", dsMrpDetail.getValue("cpid"));
        detail.setValue("scsl", dsMrpDetail.getValue("wprwscl"));
        //俩个都返回已排计量数量， 已排生产单位单位数量
        detail.setValue("sl", dsMrpDetail.getValue("wprwl"));
        detail.setValue("gylxid", dsMrpDetail.getValue("gylxid"));
        detail.setValue("dmsxid", dsMrpDetail.getValue("dmsxid"));
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String xqrq=dsMrpDetail.getValue("xqrq");
        String ksrq = dsMrpDetail.getValue("ksrq") ;
        detail.setValue("ksrq", ksrq.equals("") ? today : ksrq);
        detail.setValue("wcrq", xqrq.equals("") ? today : xqrq);
        detail.setValue("jgyq", dsMrpDetail.getValue("jgyq"));
        detail.setValue("rwdid", isMasterAdd ? "" : rwdid);
      }
      detail.post();
    }
    public void wlDetail_post()
    {
      /*
      * 生成加工单后直接生成领料单
      * 根据加工单明细ID得到任务单明细ID，再得到物料需求计划明细ID，在得到合同ID
      * 生产加工单主表的生产计划ID和明细的产品ID等于实际bom的上级产品ID
      * 关联到实际BOM得到加工单所需要的原料 SC_AUTO_PROCESS_BILL.equals("1") &&
      */
      if(!jhlx.equals("1")){
        if(dsDrawMaterail.isOpen())
          dsDrawMaterail.refresh();
        else
          dsDrawMaterail.openDataSet();
        EngineDataSet detail = getDetailTable();
        detail.first();
        for(int p=0; p<detail.getRowCount(); p++){
          String cpid = detail.getValue("cpid");
          String rwdmxid = detail.getValue("rwdmxid");
          String jgdmxid = detail.getValue("jgdmxid");
          String dmsxid = detail.getValue("dmsxid");
          String T_dmsxid = null;
          if(dmsxid.equals(""))
            T_dmsxid =" AND b.dmsxid IS NULL";
          else
            T_dmsxid = " AND b.dmsxid ="+ dmsxid;
          double d_sl = dsProcessDetail.getValue("sl").length()>0 ? Double.parseDouble(dsProcessDetail.getValue("sl")) : 0;
          if(!rwdmxid.equals("")){//如果任务单明细ID不为空，说明是通用计划下达下来的。就是实际BOM抽取物料
            String BOM_SQL = combineSQL(MATERAIL_LIST_SQL, "?", new String[]{rwdmxid, scjhid, cpid, T_dmsxid});
            dsFactBom.setQueryString(BOM_SQL);
            if(dsFactBom.isOpen())
              dsFactBom.refresh();
            else
              dsFactBom.openDataSet();
            dsFactBom.first();
            for(int t=0; t<dsFactBom.getRowCount(); t++){
              dsDrawMaterail.insertRow(false);
              dsDrawMaterail.setValue("jgdid", jgdid);
              dsDrawMaterail.setValue("jgdwlid", "-1");
              dsDrawMaterail.setValue("dmsxid", dsFactBom.getValue("dmsxid"));
              dsDrawMaterail.setValue("cpid", dsFactBom.getValue("cpid"));
              dsDrawMaterail.setValue("sl", dsFactBom.getValue("xql"));
              dsDrawMaterail.setValue("scsl", dsFactBom.getValue("scxql"));
              dsDrawMaterail.setValue("jgdmxid", jgdmxid);
              dsDrawMaterail.post();
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
            for(int t=0; t<dsBom.getRowCount(); t++){
              double zjsl = dsBom.getValue("sl").length()>0 ? Double.parseDouble(dsBom.getValue("sl")) : 0;//生产该产品需要下级原料的数量
              double shl = dsBom.getValue("shl").length()>0 ? Double.parseDouble(dsBom.getValue("shl")) : 0;//生产该产品需要下级原料的数量
              double m_sl = d_sl*zjsl*(1+shl);
              dsDrawMaterail.insertRow(false);
              dsDrawMaterail.setValue("jgdid", jgdid);
              dsDrawMaterail.setValue("jgdwlid", "-1");
              //dsDrawMaterail.setValue("dmsxid", dsBom.getValue("dmsxid"));
              dsDrawMaterail.setValue("cpid", dsBom.getValue("cpid"));
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
      dsDetailTable.first();
      for(int i=0; i<dsDetailTable.getRowCount(); i++)
      {
        String wlxqjhmxid = dsDetailTable.getValue("wlxqjhmxid");
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
   * 审批操作的触发类
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = true;

      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //得到request的参数,值若为null, 则用""代替
      String id = data.getParameter("id", "");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();
      rwdid = dsMasterTable.getValue("rwdid");
      //打开从表
      openDetailTable(false);

      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
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
      String deptid = dsMasterTable.getValue("deptid");//下达车间
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterTable.getValue("rwdh");
      //审批列表中只登陆员工有哪些下达车间权限，只显示生产任务单中下达车间为属于它权限范围的单据。而不是制定部门
      if(!deptid.equals(""))
        approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "produce_task", content, deptid);
      else
        approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "produce_task", content, deptid);
    }
  }
  /**
   * 取消审核的操作类
   */
  class Cancel_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable, dsMasterTable.getRow(), "produce_task");
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
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);

      //得到主表主键值
      String jgdid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        jgdid = dataSetProvider.getSequence("s_sc_jgd");
        ds.setValue("jgdid", jgdid);
        ds.setValue("fgsid", fgsid);
        //ds.setValue("wlxqjhid", wlxqjhid);
        ds.setValue("zt","0");
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      BigDecimal totalNum = new BigDecimal(0) ;
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("jgdid", jgdid);

        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("sl", detailrow.get("sl"));//计量单位数量
        detail.setValue("scsl", detailrow.get("scsl"));//生产单位数量
        detail.setValue("gylxid", detailrow.get("gylxid"));
        detail.setValue("wlxqjhmxid", detailrow.get("wlxqjhmxid"));
        detail.setValue("csl", detailrow.get("csl"));//超产率
        detail.setValue("ksrq", detailrow.get("ksrq"));
        detail.setValue("wcrq", detailrow.get("wcrq"));
        detail.setValue("dmsxid", detailrow.get("dmsxid"));//已加工量
        detail.setValue("jgyq", detailrow.get("jgyq"));//加工要求
        String jgdmxid = detailrow.get("jgdmxid");
        jgdmxid = (jgdmxid.equals("-1") || jgdmxid.equals("")) ? dataSetProvider.getSequence("s_sc_jgdmxid") : jgdmxid;
        detail.setValue("jgdmxid", jgdmxid);
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        detail.next();
      }

      //保存主表数据
      ds.setValue("deptid", rowInfo.get("deptid"));//下达车间id
      ds.setValue("rq", rowInfo.get("rq"));//日期
      ds.setValue("sm", rowInfo.get("sm"));//计划说明
      ds.setValue("ztms", rowInfo.get("ztms"));//状态描述
      ds.setValue("bm_deptid", rowInfo.get("bm_deptid"));//制定部门
      boolean isAll =  isAllMaterail();//从表中是否有一条纪录引入物料需求
      ds.setValue("wlxqjhid", isAll ? wlxqjhid : "");//物料需求计划ID
      ds.setValue("scjhid", isAll ? scjhid : "");//生产计划ID
      ds.setValue("zsl", totalNum.toString());
      ds.setValue("rwlx", jhlx);
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      //系统参数为一时根据这张任务单自动生产加工单
      /*String jgdid = null;
      if(SC_AUTO_PROCESS_BILL.equals("1") && isMasterAdd)
      {
        if(dsProcessMaster.isOpen())
          dsProcessMaster.refresh();
        else
          dsProcessMaster.open();
        if(dsProcessDetail.isOpen())
         dsProcessDetail.refresh();
       else
          dsProcessDetail.open();
        dsProcessMaster.insertRow(false);
        jgdid = dataSetProvider.getSequence("s_sc_jgd");
        dsProcessMaster.setValue("jgdid", jgdid);
        dsProcessMaster.setValue("fgsid", fgsid);
        dsProcessMaster.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        dsProcessMaster.setValue("zdrid", loginId);
        dsProcessMaster.setValue("zdr", loginName);//操作员
        dsProcessMaster.setValue("zt", "0");
        dsProcessMaster.setValue("deptid", rowInfo.get("deptid"));//部门车间
        dsProcessMaster.setValue("rq", rowInfo.get("rq"));//日期
        dsProcessMaster.setValue("zsl", totalNum.toString());
        String jgdh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('sc_jgd','jgdh') from dual");
        dsProcessMaster.setValue("jgdh", jgdh);
        dsProcessMaster.setValue("zsl", totalNum.toString());
        dsProcessMaster.setValue("scjhid", isAll ? scjhid : "");//生产计划ID
        dsProcessMaster.setValue("rwdid", rwdid);//任务单ID
        dsProcessMaster.setValue("jglx", jhlx);
        dsProcessMaster.setValue("sfwjg","0");//自动生成加工单时，默认为自加工不是外加工
        int count = detail.getRowCount();
        detail.first();
        for(int j=0;j<count; j++)
        {
      //新添的记录
          dsProcessDetail.insertRow(false);
          dsProcessDetail.setValue("jgdid", jgdid);
          String jgdmxid = dataSetProvider.getSequence("s_sc_jgdmx");
          dsProcessDetail.setValue("jgdmxid", jgdmxid);

          dsProcessDetail.setValue("cpid", detail.getValue("cpid"));
          dsProcessDetail.setValue("sl", detail.getValue("sl"));//计量单位
          dsProcessDetail.setValue("scsl", detail.getValue("scsl"));//生产数量
          dsProcessDetail.setValue("gylxid", detail.getValue("gylxid"));
          dsProcessDetail.setValue("rwdmxid", detail.getValue("rwdmxid"));
          dsProcessDetail.setValue("dmsxid", detail.getValue("dmsxid"));
          dsProcessDetail.setValue("jgyq", detail.getValue("jgyq"));
          dsProcessDetail.post();
          detail.next();
        }
        dsProcessMaster.post();
      }*/
      //改变生产计划主表数据集，使当前行生产计划状态变为3
      ProducePlan producePlanBean = ProducePlan.getInstance(data.getRequest());
      EngineDataSet dsPlanMaster = producePlanBean.getMaterTable();
      dsPlanMaster.goToRow(row);
      String zt = dsPlanMaster.getValue("zt");
      if(zt.equals("2"))
      {
        dsPlanMaster.setValue("zt", "3");
        dsPlanMaster.post();
      }
      //ds.setAfterResolvedSQL(new String[]{UPDATE_SQL +scjhid});
      ds.saveDataSets(new EngineDataSet[]{dsPlanMaster, ds, detail,  dsDrawMaterail});
        /*
      * 生成加工单后直接生成领料单
      * 根据加工单明细ID得到任务单明细ID，再得到物料需求计划明细ID，在得到合同ID
      * 生产加工单主表的生产计划ID和明细的产品ID等于实际bom的上级产品ID
      * 关联到实际BOM得到加工单所需要的原料
         */
      if(isAll && SC_AUTO_PROCESS_BILL.equals("1") && isMasterAdd && jhlx.equals("1")){
        if(dsDrawMaterail.isOpen())
          dsDrawMaterail.refresh();
        else
          dsDrawMaterail.openDataSet();
        StringBuffer rwdmxidBuf = new StringBuffer();
        StringBuffer cpidBuf = new StringBuffer();
        dsProcessDetail.first();
        for(int p=0; p<dsProcessDetail.getRowCount(); p++){
          String cpid = dsProcessDetail.getValue("cpid");
          String rwdmxid = dsProcessDetail.getValue("rwdmxid");
          String jgdmxid = dsProcessDetail.getValue("jgdmxid");
          if(p==dsProcessDetail.getRowCount()-1){
            rwdmxidBuf = rwdmxidBuf.append("'").append(rwdmxid).append("'");
            cpidBuf = cpidBuf.append("'").append(cpid).append("'");
          }
          else{
            rwdmxidBuf = rwdmxidBuf.append("'").append(rwdmxid).append("',");
            cpidBuf = cpidBuf.append("'").append(cpid).append("',");
          }
          dsProcessDetail.next();
        }
        String BOM_SQL = combineSQL(SUBMATERAIL_LIST_SQL, "?", new String[]{rwdmxidBuf.toString(), scjhid, cpidBuf.toString()});
        dsFactBom.setQueryString(BOM_SQL);
        if(dsFactBom.isOpen())
          dsFactBom.refresh();
        else
          dsFactBom.openDataSet();
        dsFactBom.first();
        for(int t=0; t<dsFactBom.getRowCount(); t++){
          dsDrawMaterail.insertRow(false);
          dsDrawMaterail.setValue("jgdid", jgdid);
          dsDrawMaterail.setValue("jgdwlid", "-1");
          dsDrawMaterail.setValue("dmsxid", dsFactBom.getValue("dmsxid"));
          dsDrawMaterail.setValue("cpid", dsFactBom.getValue("cpid"));
          dsDrawMaterail.setValue("sl", dsFactBom.getValue("xql"));
          dsDrawMaterail.setValue("scsl", dsFactBom.getValue("scxql"));
          //dsDrawMaterail.setValue("jgdmxid", jgdmxid);
          dsDrawMaterail.post();
          dsFactBom.next();
        }
        dsDrawMaterail.saveChanges();
      }
      //通用加工单，即不是通过分切计划下达的。需要保存加工单明细ID
      else if(SC_AUTO_PROCESS_BILL.equals("1") && isMasterAdd && !jhlx.equals("1")){
        if(dsDrawMaterail.isOpen())
          dsDrawMaterail.refresh();
        else
          dsDrawMaterail.openDataSet();
        dsProcessDetail.first();
        for(int p=0; p<dsProcessDetail.getRowCount(); p++){
          String cpid = dsProcessDetail.getValue("cpid");
          String rwdmxid = dsProcessDetail.getValue("rwdmxid");
          String jgdmxid = dsProcessDetail.getValue("jgdmxid");
          String dmsxid = dsProcessDetail.getValue("dmsxid");
          String T_dmsxid = null;
          if(dmsxid.equals(""))
            T_dmsxid =" AND b.dmsxid IS NULL";
          else
            T_dmsxid = " AND b.dmsxid ="+ dmsxid;
          double d_sl = dsProcessDetail.getValue("sl").length()>0 ? Double.parseDouble(dsProcessDetail.getValue("sl")) : 0;
          if(!rwdmxid.equals("")){//如果任务单明细ID不为空，说明是通用计划下达下来的。就是实际BOM抽取物料
            String BOM_SQL = combineSQL(MATERAIL_LIST_SQL, "?", new String[]{rwdmxid, scjhid, cpid, T_dmsxid});
            dsFactBom.setQueryString(BOM_SQL);
            if(dsFactBom.isOpen())
              dsFactBom.refresh();
            else
              dsFactBom.openDataSet();
            dsFactBom.first();
            for(int t=0; t<dsFactBom.getRowCount(); t++){
              dsDrawMaterail.insertRow(false);
              dsDrawMaterail.setValue("jgdid", jgdid);
              dsDrawMaterail.setValue("jgdwlid", "-1");
              dsDrawMaterail.setValue("dmsxid", dsFactBom.getValue("dmsxid"));
              dsDrawMaterail.setValue("cpid", dsFactBom.getValue("cpid"));
              dsDrawMaterail.setValue("sl", dsFactBom.getValue("xql"));
              dsDrawMaterail.setValue("scsl", dsFactBom.getValue("scxql"));
              dsDrawMaterail.setValue("jgdmxid", jgdmxid);
              dsDrawMaterail.post();
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
            for(int t=0; t<dsBom.getRowCount(); t++){
              double zjsl = dsBom.getValue("sl").length()>0 ? Double.parseDouble(dsBom.getValue("sl")) : 0;//生产该产品需要下级原料的数量
              double shl = dsBom.getValue("shl").length()>0 ? Double.parseDouble(dsBom.getValue("shl")) : 0;//生产该产品需要下级原料的数量
              double m_sl = d_sl*zjsl*(1+shl);
              dsDrawMaterail.insertRow(false);
              dsDrawMaterail.setValue("jgdid", jgdid);
              dsDrawMaterail.setValue("jgdwlid", "-1");
              //dsDrawMaterail.setValue("dmsxid", dsBom.getValue("dmsxid"));
              dsDrawMaterail.setValue("cpid", dsBom.getValue("cpid"));
              dsDrawMaterail.setValue("sl", formatNumber(String.valueOf(m_sl), qtyFormat));
              dsDrawMaterail.setValue("scsl", formatNumber(String.valueOf(m_sl), qtyFormat));
              dsDrawMaterail.setValue("jgdmxid", jgdmxid);
              dsDrawMaterail.post();
              dsBom.next();
            }
          }
          dsProcessDetail.next();
        }
        dsDrawMaterail.saveChanges();
      }

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
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
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detail = (RowMap)d_RowInfos.get(i);
        String wlxqjhmxid = detail.get("wlxqjhmxid");
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
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      //
      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   * 改变车间触发的事件
   * 改变车间后找到本条生产计划生成的物料需求计划明细
   * 物料需求明细根据所选择的车间进行筛选物料需求
   * 将筛选出来的物料插入任务单明细中
   * 备注如果物料中的物料没有生产车间也会抽过来，不会被筛除
   */
  class Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      RowMap rowinfo = getMasterRowinfo();
      String deptid = rowinfo.get("deptid");
      if(deptid.equals(""))
        return;
      String sql = combineSQL(MRP_DETAIL_SQL, "?", new String[]{fgsid, deptid, scjhid});
      dsMrpDetail.setQueryString(sql);
      if(dsMrpDetail.isOpen())
        dsMrpDetail.refresh();
      else
        dsMrpDetail.open();

      EngineDataSet detail = getDetailTable();
      detail.deleteAllRows();
      d_RowInfos.clear();
      if(!isMasterAdd)
        detail.goToInternalRow(masterRow);
      rwdid = detail.getValue("rwdid");
      int rowcount = dsMrpDetail.getRowCount();
      EngineRow locatrow = new EngineRow(dsDetailTable, "wlxqjhmxid");
      dsMrpDetail.first();
      for(int i=0; i<rowcount; i++)
      {
        dsMrpDetail.goToRow(i);
        if(i==0)
          wlxqjhid = dsMrpDetail.getValue("wlxqjhid");
        String wlxqjhmxid = dsMrpDetail.getValue("wlxqjhmxid");
        locatrow.setValue(0, wlxqjhmxid);
        if(!detail.locate(locatrow, Locate.FIRST)){
          detail.insertRow(false);
          detail.setValue("jgdmxid","-1");
          detail.setValue("wlxqjhmxid", wlxqjhmxid);
          detail.setValue("cpid", dsMrpDetail.getValue("cpid"));
          detail.setValue("scsl", dsMrpDetail.getValue("wprwscl"));
          //俩个都返回已排计量数量， 已排生产单位单位数量
          detail.setValue("sl", dsMrpDetail.getValue("wprwl"));
          detail.setValue("gylxid", dsMrpDetail.getValue("gylxid"));

          detail.setValue("dmsxid", dsMrpDetail.getValue("dmsxid"));
          String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
          String xqrq=dsMrpDetail.getValue("xqrq");
          String ksrq = dsMrpDetail.getValue("ksrq") ;
          detail.setValue("ksrq", ksrq.equals("") ? today : ksrq);
          detail.setValue("wcrq", xqrq.equals("") ? today : xqrq);
          detail.setValue("jgyq", dsMrpDetail.getValue("jgyq"));
          detail.setValue("rwdid", isMasterAdd ? "" : rwdid);
        }
        dsMrpDetail.next();
      }
      dsMrpDetail.empty();
      initRowInfo(false, true, true);
    }
  }
  /**
   *  从表输入产品编码触发操作

  class Product_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsDetailTable.goToRow(row);
      RowMap detail=(RowMap)d_RowInfos.get(row);
      String cpid = detail.get("cpid");
      RowMap productRow = getProductBean(req).getLookupRow(cpid);
      long ztqq = productRow.get("ztqq").length()>0 ? Long.parseLong(productRow.get("ztqq")) : 0;//总提前期
      Date startdate = new Date();
      Date enddate = new Date(startdate.getTime() + ztqq*60*60*24*1000);
      String today = new SimpleDateFormat("yyyy-MM-dd").format(startdate);
      String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
      detail.put("ksrq", today);
      detail.put("wcrq", endDate);
    }
  }
  */

 /**
  * 从表增加操作
  */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String rwdid = dsMasterTable.getValue("rwdid");
      detail.insertRow(false);
      detail.setValue("rwdid", isMasterAdd ? "-1" : rwdid);
      detail.post();
      d_RowInfos.add(new RowMap());
    }
  }
  /**
   * 从表增加操作（单选产品）

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
      String rwdid = dsMasterTable.getValue("rwdid");
      dsDetailTable.goToRow(row);
      RowMap detailrow = null;
      detailrow = (RowMap)d_RowInfos.get(row);
      locateGoodsRow.setValue(0, cpid);
      if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
      {
        RowMap productRow = getProductBean(req).getLookupRow(cpid);
        long ztqq = productRow.get("ztqq").length()>0 ? Long.parseLong(productRow.get("ztqq")) : 0;//总提前期
        detailrow.put("rwdmxid", "-1");
        detailrow.put("cpid", cpid);
        Date startdate = new Date();
        Date enddate = new Date(startdate.getTime() + ztqq*60*60*24*1000);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(startdate);
        String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
        detailrow.put("ksrq", today);
        detailrow.put("wcrq", endDate);
        detailrow.put("rwdid", isMasterAdd ? "-1" : rwdid);
      }
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
   * 得到用于查找物料需求计划信息的bean
   * @param req WEB的请求
   * @return 返回用于查找物料需求计划信息的bean

    public ImportMrp getMrpGoodsBean(HttpServletRequest req)
    {
      if(importMrpBean == null)
        importMrpBean = ImportMrp.getInstance(req);
      return importMrpBean;
    }
    */
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
}
package engine.erp.produce;

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
import engine.erp.produce.ImportMrp;
import engine.erp.produce.B_Task_SingleSelMrp;

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
 * <p>Title: 生产--生产任务维护列表</p>
 * <p>Description: 生产--生产任务维护列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_ProduceTask extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";
  public  static final String DETAIL_SELECT_MRP = "10021";
  public  static final String ONCHANGE = "10031";
  public  static final String CANCEL_APPROVE = "18531";//取消审批操作
  public  static final String SINGLE_SELECT_PRODUCT = "10041";//单选产品操作
  public  static final String SINGLE_SELECT_MRP = "10051";//单选物料需求计划操作
  public  static final String PRODUCT_ONCHANGE = "10060";//从表输入产品编码触发事件
  public  static final String REPORT = "2000";//报表追踪触发事件
  public  static final String SUBPLAN_MRP_ADD = "2002";//在生产加工单主表里面增加分切计划任务单触发事件，即选择jhlx为一的物料需求


  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_rwd WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM sc_rwd WHERE ? AND fgsid=? ? ORDER BY rwdh DESC";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_rwdmx WHERE 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sc_rwdmx WHERE rwdid='?'";//
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM sc_rwd WHERE rwdid='?'";
  //提取物料需求计划明细的SQL语句
  //private static final String MRP_DETAIL_SQL = "SELECT * FROM VW_TASK_SEL_MRPDETAIL WHERE wlxqjhid= ";
  //生成加工单后直接生成领料清单
  private static final String DRAW_MATETAIL_SQL = "SELECT * FROM sc_jgdwl where 1<>1";
  //通过加工单主表生产计划ID和从表的任务单明细ID得到合同ID再根据cpid等于实际BOM表的上级cpid得到物料
  private static final String MATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, a.htid "
             + " FROM sc_sjbom a WHERE a.sjsjbomid IN (SELECT b.sjbomid "
             + " FROM sc_sjbom b, sc_jh c, sc_jhmx d WHERE b.scjhmxid=d.scjhmxid AND c.scjhid=d.scjhid  AND  nvl(b.htid,-1) IN( "
             + " SELECT nvl(d.htid,-1) FROM sc_wlxqjhmx d, sc_rwdmx e WHERE d.wlxqjhmxid=e.wlxqjhmxid AND  e.rwdmxid='?') "
             + " AND c.scjhid='?' AND b.cpid='?' ?) "
             + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, a.htid ORDER BY a.cpid";
  /**
  private static final String MATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, a.htid, b.scjhid "
         + " FROM sc_sjbom a, sc_jh b, sc_jhmx c WHERE a.scjhmxid=c.scjhmxid AND b.scjhid=c.scjhid AND a.htid IN( "
         + " SELECT d.htid FROM sc_wlxqjhmx d, sc_rwdmx e WHERE d.wlxqjhmxid=e.wlxqjhmxid AND  e.rwdmxid='?') AND b.scjhid='?' AND a.sjcpid='?' "
         + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, a.htid, b.scjhid ORDER BY a.cpid ";
         */
  //分切计划通过加工单主表生产计划ID和从表的任务单明细ID得到合同ID再根据cpid等于实际BOM表的上级cpid得到物料
  private static final String SUBMATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, b.scjhid "
       + " FROM sc_sjbom a, sc_jh b, sc_jhmx c WHERE a.scjhmxid=c.scjhmxid AND b.scjhid=c.scjhid AND nvl(a.htid,-1) IN( "
       + " SELECT nvl(d.htid,-1) FROM sc_wlxqjhmx d, sc_rwdmx e WHERE d.wlxqjhmxid=e.wlxqjhmxid  AND e.rwdmxid IN(?)) AND b.scjhid='?' AND a.sjcpid IN(?) "
         + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, b.scjhid ORDER BY a.cpid ";
  //BOM表数据
  private static final String BOM_MATERAIL = "SELECT a.* FROM sc_bom a WHERE a.sjcpid='?' ";
  //引物料需求主表时，根据主表物料需求计划ID得到物料计划明细
  private static final String SINGLEMRP_DETAIL_SQL = "SELECT * FROM VW_TASK_SINGLESEL_MRPDETAIL WHERE wlxqjhid='?' AND (deptid IS NULL OR deptid='?')";
  //生产加工单的表结构SQL语句
  private static final String PROCESSMASTER_STRUT_SQL = "SELECT * FROM sc_jgd WHERE 1<>1";
  private static final String PROCESSDETAIL_STRUT_SQL = "SELECT * FROM sc_jgdmx WHERE 1<>1";


  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表

  private EngineDataSet dsProcessMaster = new EngineDataSet();//加工单主表数据集
  private EngineDataSet dsProcessDetail = new EngineDataSet();//加工单从表

  private EngineDataSet dsFactBom = new EngineDataSet();;//实际BOM数据集
  private EngineDataSet dsDrawMaterail = new EngineDataSet();//领料请单数据集
  private EngineDataSet dsBom = new EngineDataSet();;//BOM数据集

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_rwd");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_rwdmx");
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态

  public  boolean isReport = false;      //是否是在报表追踪状态
  public boolean isDetailAdd = false;   //是否从表增加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private PlanSelectSale planSelectSaleBean = null; //销售合同的bean的引用, 用于提取销售合同
  private PlanSelectStore planSelectStoreBean = null; //当前库存量的bean的引用, 用于提取当前库存量
  private B_Task_SingleSelMrp taskSingleSelMrpBean = null; //物料需求计划主表一行信息

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  private LookUp productBean = null; //产品信息的bean的引用, 用于提取产品信息
  private ImportMrp importMrpBean = null; //物料需求计划的bean的引用, 用于提取物料需求计划信息

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String rwdid = null;
  private User user = null;
  private String SC_AUTO_PROCESS_BILL = null;//系统参数0=手动,1=自动生成加工单
  public String SC_PRODUCE_UNIT_STYLE =null;//1=强制换算,0=仅空值时换算
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  private String wlxqjhid = null;//物料需求计划ID
  private String scjhid = null;//一张任务单只能引用一张物料需求计划，而一张物料需求和生产计划又是一对一，所以要保存scjhid
  private String rwlx = null;//任务单是否是通过分切计划物料生成的，是 rwlx为1
  public String SC_PLAN_ADD_STYLE = null;//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
  /**
   * 生产任务列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回生产任务列表的实例
   */
  public static B_ProduceTask getInstance(HttpServletRequest request)
  {
    B_ProduceTask produceTaskBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "produceTaskBean";
      produceTaskBean = (B_ProduceTask)session.getAttribute(beanName);
      if(produceTaskBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        produceTaskBean = new B_ProduceTask();
        produceTaskBean.qtyFormat = loginBean.getQtyFormat();
        produceTaskBean.sumFormat = loginBean.getSumFormat();

        produceTaskBean.fgsid = loginBean.getFirstDeptID();
        produceTaskBean.loginId = loginBean.getUserID();
        produceTaskBean.loginName = loginBean.getUserName();
        produceTaskBean.loginDept = loginBean.getDeptID();
        produceTaskBean.user = loginBean.getUser();
        produceTaskBean.SC_PLAN_ADD_STYLE = loginBean.getSystemParam("SC_PLAN_ADD_STYLE");//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
        produceTaskBean.SC_AUTO_PROCESS_BILL = loginBean.getSystemParam("SC_AUTO_PROCESS_BILL");
        produceTaskBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//计量单位和生产单位是否强制换算
        produceTaskBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        produceTaskBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        //设置格式化的字段
        produceTaskBean.dsDetailTable.setColumnFormat("sl", produceTaskBean.qtyFormat);
        produceTaskBean.dsDetailTable.setColumnFormat("scsl", produceTaskBean.sumFormat);
        produceTaskBean.dsMasterTable.setColumnFormat("zsl", produceTaskBean.sumFormat);
        session.setAttribute(beanName, produceTaskBean);
      }
    }
    return produceTaskBean;
  }

  /**
   * 构造函数
   */
  private B_ProduceTask()
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
    setDataSetProperty(dsProcessMaster, PROCESSMASTER_STRUT_SQL);
    setDataSetProperty(dsProcessDetail, PROCESSDETAIL_STRUT_SQL);
    setDataSetProperty(dsDrawMaterail, DRAW_MATETAIL_SQL);
    setDataSetProperty(dsFactBom, null);
    setDataSetProperty(dsBom,null);


    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"rwdh"}, new String[]{"SELECT pck_base.billNextCode('sc_rwd','rwdh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"rwdh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"rwdmxid"}, new String[]{"s_sc_rwdmx"}));

    dsProcessDetail.setSequence(new SequenceDescriptor(new String[]{"jgdmxid"}, new String[]{"s_sc_jgdmx"}));

    dsDrawMaterail.setSequence(new SequenceDescriptor(new String[]{"jgdwlid"}, new String[]{"s_sc_jgdwl"}));
    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
        addObactioner(String.valueOf(SUBPLAN_MRP_ADD), masterAddEdit);//增加加工单（选择分切计划的物料需求）的触发事件
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(REPORT), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(DETAIL_SELECT_MRP), new Detail_Select_Mrp());
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());
    addObactioner(String.valueOf(SINGLE_SELECT_PRODUCT), new Single_Product_Add());//单选产品
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());
    addObactioner(String.valueOf(SINGLE_SELECT_MRP), new Single_Select_Mrp());
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
   * 得到物料是通过什么计划生成的。1.分切计划0通用计划
   */
  public final String getMrpType(){
    return rwlx;
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
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//超产率
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//计量单位数量
      detailRow.put("scsl", formatNumber(rowInfo.get("scsl_"+i), qtyFormat));//生产单位数量
      detailRow.put("csl", rowInfo.get("csl_"+i));//超产率
      detailRow.put("gylxid", rowInfo.get("gylxid_"+i));//工艺路线
      detailRow.put("ksrq", rowInfo.get("ksrq_"+i));//开始日期
      detailRow.put("wcrq", rowInfo.get("wcrq_"+i));//完成日期
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//规格属性
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
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("zt", "0");
      row.put("rq$a", startDay);
      row.put("rq$b", today);
      isMasterAdd = true;
      isDetailAdd = false;
      //
      String SQL = " AND zt<>8";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
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
      isDetailAdd= false;
      isReport = false;
      isMasterAdd = !String.valueOf(EDIT).equals(action);
      boolean isSubPlanAdd  = String.valueOf(SUBPLAN_MRP_ADD).equals(action);
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        rwlx = dsMasterTable.getValue("rwlx");
        rwdid = dsMasterTable.getValue("rwdid");
      }
      if(isMasterAdd){
        if(isSubPlanAdd)
          rwlx = "1";
        else
          rwlx = "0";
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
     //得到request的参数,值若为null, 则用""代替
     String id =null;
     if(!isRep){
       isApprove = true;
       id = data.getParameter("id", "");
     }
     else{
       isReport = true;
       id = data.getParameter("rwdid");
     }
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
      String rwdid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        rwdid = dataSetProvider.getSequence("s_sc_rwd");
        ds.setValue("rwdid", rwdid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      detail.first();
      BigDecimal totalNum = new BigDecimal(0);
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("rwdid", rwdid);

        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("sl", detailrow.get("sl"));//保存计量单位数量
        detail.setValue("scsl", detailrow.get("scsl"));//保存生产单位数量
        detail.setValue("gylxid", detailrow.get("gylxid"));
        detail.setValue("wlxqjhmxid", detailrow.get("wlxqjhmxid"));
        detail.setValue("csl", detailrow.get("csl"));//超产率
        detail.setValue("ksrq", detailrow.get("ksrq"));
        detail.setValue("wcrq", detailrow.get("wcrq"));
        detail.setValue("dmsxid", detailrow.get("dmsxid"));//规格属性
        detail.setValue("jgyq", detailrow.get("jgyq"));//加工要求
        String rwdmxid = detailrow.get("rwdmxid");
        rwdmxid = rwdmxid.equals("-1") ? dataSetProvider.getSequence("s_sc_rwdmx") : rwdmxid;
        detail.setValue("rwdmxid", rwdmxid);
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
      ds.setValue("zsl", totalNum.toString());
      boolean isAll =  isAllMaterail();//从表中是否有一条纪录引入物料需求
      ds.setValue("wlxqjhid", isAll ? wlxqjhid : "");//物料需求计划ID
      ds.setValue("scjhid", isAll ? scjhid : "");//生产计划ID
      ds.setValue("rwlx",  rwlx);//该生产任务单是通过什么物料需求计划生成的，1.分切计划物料需求0通用计划物料需求
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      //系统参数为一时根据这张任务单自动生产加工单
      String jgdid = null;
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
        dsProcessMaster.setValue("rwdid", rwdid);//任务单ID
        dsProcessMaster.setValue("scjhid", isAll ? scjhid : "");//生产计划ID
        dsProcessMaster.setValue("jglx", rwlx);//加工类型
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
      }
      ds.saveDataSets(new EngineDataSet[]{ds, detail, dsProcessMaster, dsProcessDetail}, null);

      /*
      * 生成加工单后直接生成加工物料单
      * 根据加工单明细ID得到任务单明细ID，再得到物料需求计划明细ID，在得到合同ID
      * 生产加工单主表的生产计划ID和明细的产品ID等于实际bom的上级产品ID
      * 关联到实际BOM得到加工单所需要的原料
      */
      if(isAll && SC_AUTO_PROCESS_BILL.equals("1") && isMasterAdd && rwlx.equals("1")){
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
      else if(SC_AUTO_PROCESS_BILL.equals("1") && isMasterAdd && !rwlx.equals("1")){
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
            String BOM_SQL = combineSQL(MATERAIL_LIST_SQL, "?", new String[]{rwdmxid, scjhid, cpid,T_dmsxid});
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
            return showJavaScript("alert('第"+row+"行开始日期不能大于结束日期')");
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
      temp = rowInfo.get("bm_deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择制定部门！');");
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
   *  查询操作
   */
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
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
        new QueryColumn(master.getColumn("rwdh"), null, null, null),
        new QueryColumn(master.getColumn("rq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("rq"), null, null, null, "b", "<="),
        new QueryColumn(detail.getColumn("rwdid"), "sc_jhmx", "rwdid","ksrq", "a", ">="),
        new QueryColumn(detail.getColumn("rwdid"), "sc_jhmx", "rwdid", "ksrq", "b", "<="),
        new QueryColumn(detail.getColumn("rwdid"), "sc_jhmx", "rwdid", "wcrq", "c", ">="),
        new QueryColumn(detail.getColumn("rwdid"), "sc_jhmx", "rwdid","wcrq", "d", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        new QueryColumn(master.getColumn("rwdid"), "sc_rwdmx", "rwdid", "cpid", null, "="),//从表品名
        new QueryColumn(master.getColumn("rwdid"), "VW_SCRWD_QUERY", "rwdid", "cpbm", "cpbm", "like"),//从表产品编码
        new QueryColumn(master.getColumn("rwdid"), "VW_SCRWD_QUERY", "rwdid", "product", "product", "like"),//从表品名
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
  /**
   *  根据物料需求计划增加操作
   *  首先得到选择的物料需求计划，得到物料需求计划明细
   *  然后根据生产车间筛选得到该车间生产的物料，如果物料生产车间为空也引用。并插入到任务单明细中
  */

  class Single_Select_Mrp implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String singleImportMrp = m_RowInfo.get("singleImportMrp");
      String deptid = rowInfo.get("deptid");
      if(singleImportMrp.equals(""))
        return;
      String SQL = combineSQL(SINGLEMRP_DETAIL_SQL,"?", new String[]{singleImportMrp, deptid});
      EngineDataSet mrpDetailData = null;
      if(mrpDetailData==null)
      {
        mrpDetailData = new EngineDataSet();
        setDataSetProperty(mrpDetailData,null);
      }
      mrpDetailData.setQueryString(SQL);
      if(!mrpDetailData.isOpen())
        mrpDetailData.openDataSet();
      else
        mrpDetailData.refresh();
      if(mrpDetailData.getRowCount()<1)
      {
        data.setMessage(showJavaScript("alert('该条物料需求计划没有在该车间生产的物料')"));
        return;
      }

      EngineDataSet dsDetailTable = getDetailTable();
      if(dsDetailTable != null)
        dsDetailTable.deleteAllRows();
      d_RowInfos.clear();
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wlxqjhmxid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String rwdid = dsMasterTable.getValue("rwdid");
      mrpDetailData.first();
      for(int i=0; i < mrpDetailData.getRowCount(); i++)
      {
        if(i==0){
          wlxqjhid = mrpDetailData.getValue("wlxqjhid");
          scjhid = mrpDetailData.getValue("scjhid");
        }
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("rwdmxid", "-1");
        dsDetailTable.setValue("wlxqjhmxid", mrpDetailData.getValue("wlxqjhmxid"));
        dsDetailTable.setValue("cpid", mrpDetailData.getValue("cpid"));
        dsDetailTable.setValue("sl", mrpDetailData.getValue("wprwl"));
        dsDetailTable.setValue("scsl", mrpDetailData.getValue("wprwscl"));
        dsDetailTable.setValue("gylxid", mrpDetailData.getValue("gylxid"));
        dsDetailTable.setValue("dmsxid", mrpDetailData.getValue("dmsxid"));//规格属性
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String xqrq=mrpDetailData.getValue("xqrq");
        String ksrq = mrpDetailData.getValue("ksrq");
        dsDetailTable.setValue("ksrq", ksrq.equals("") ? today : ksrq);
        dsDetailTable.setValue("wcrq", xqrq.equals("") ? today : xqrq);
        dsDetailTable.setValue("jgyq", mrpDetailData.getValue("jgyq"));
        dsDetailTable.setValue("rwdid", isMasterAdd ? "" : rwdid);
        dsDetailTable.post();
        //创建一个与用户相对应的行
        RowMap detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
        mrpDetailData.next();
      }
    }
  }

  /**
   *  根据物料需求计划从表增加操作
   */

  class Detail_Select_Mrp implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String mutimrp = m_RowInfo.get("mutimrp");
      if(mutimrp.length() == 0)
        return;

      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wlxqjhmxid");
      String[] wlxqjhmxID = parseString(mutimrp,",");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String rwdid = dsMasterTable.getValue("rwdid");
      ArrayList list = new ArrayList(wlxqjhmxID.length);
      /*
      * 判断是否选择了不同的物料需求计划
      */
      for(int i=0; i < wlxqjhmxID.length; i++)
      {
        if(wlxqjhmxID[i].equals("-1"))
          continue;
        RowMap importMrpRow = getMrpGoodsBean(req).getLookupRow(wlxqjhmxID[i]);
        if(list.contains(wlxqjhmxID[i]))
          data.setMessage(showJavaScript("只能选择同一个物料需求计划"));
        else
          list.add(wlxqjhmxID[i]);
      }
      for(int i=0; i < wlxqjhmxID.length; i++)
      {
        if(wlxqjhmxID[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, wlxqjhmxID[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          /**
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
          */
          RowMap importMrpRow = getMrpGoodsBean(req).getLookupRow(wlxqjhmxID[i]);
          if(wlxqjhid.equals("") || wlxqjhid==null){
            wlxqjhid = importMrpRow.get("wlxqjhid");
            scjhid = importMrpRow.get("scjhid");
          }
          else if(!wlxqjhid.equals(importMrpRow.get("wlxqjhid"))){
            data.setMessage(showJavaScript("alert('一张任务单只能对应一张物料需求计划')"));
            return;
          }
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("rwdmxid", "-1");
          dsDetailTable.setValue("wlxqjhmxid",wlxqjhmxID[i]);
          dsDetailTable.setValue("cpid", importMrpRow.get("cpid"));
          dsDetailTable.setValue("sl", importMrpRow.get("wprwl"));
          dsDetailTable.setValue("scsl", importMrpRow.get("wprwscl"));
          dsDetailTable.setValue("gylxid", importMrpRow.get("gylxid"));
          dsDetailTable.setValue("dmsxid", importMrpRow.get("dmsxid"));//规格属性
          String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
          String xqrq=importMrpRow.get("xqrq");
          String ksrq = importMrpRow.get("ksrq");
          dsDetailTable.setValue("ksrq", ksrq.equals("") ? today : ksrq);
          dsDetailTable.setValue("wcrq", xqrq.equals("") ? today : xqrq);
          dsDetailTable.setValue("jgyq", importMrpRow.get("jgyq"));
          dsDetailTable.setValue("rwdid", isMasterAdd ? "" : rwdid);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
    }
  }
  /**
   *改变车间触发的事件
   */
  class Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
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
    }
  }
  /**
   * 从表增加操作(从表增加一行空白行)
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
      isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
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
   * 从表增加操作（单选产品）
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
     */
    public ImportMrp getMrpGoodsBean(HttpServletRequest req)
    {
      if(importMrpBean == null)
        importMrpBean = ImportMrp.getInstance(req);
      return importMrpBean;
    }
    /**
     * 得到物料需求计划主表一行信息的bean
     * @param req WEB的请求
     * @return 返回用于物料需求计划主表一行信息的bean
     */
    public B_Task_SingleSelMrp getMrpMasterBean(HttpServletRequest req)
    {
      if(taskSingleSelMrpBean == null)
        taskSingleSelMrpBean = B_Task_SingleSelMrp.getInstance(req);
      return taskSingleSelMrpBean;
    }
    /**
   * 得到用于查找产品单价的bean
   * @param req WEB的请求
   * @return 返回用于查找产品单价的bean
   */
  public LookUp getProductBean(HttpServletRequest req)
  {
    if(productBean == null)
      productBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PRODUCT);
    return productBean;
  }
}
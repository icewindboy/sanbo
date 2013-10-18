package engine.erp.equipment;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.RowMap;
import engine.dataset.SequenceDescriptor;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.html.*;
import engine.common.*;
import engine.project.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 设备管理--设备维修、保养记录</p>
 * <p>Description: 设备管理--设备维修、保养记录</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */
public final class B_EquipmentResult  extends BaseAction implements Operate
{
  /**
   * 提取所有信息的SQL语句
   */
  private static final String Master_Quality_SQL = "SELECT * FROM sb_maintainResult where ? ?";
//and billType='?'
  private static final String Master_STRUT_SQL = "SELECT * FROM sb_maintainResult where 1<>1";
  private static final String Detail_STRUT_SQL="SELECT * FROM sb_mainResultDetail where 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sb_mainResultDetail WHERE maintainResultID='?' ";
  //SELECT * FROM sb_mainResultDetail WHERE maintainResultID='?' and maintainapplyNO='?' and maintainPlanNO='?'
  //private static final String DETAIL_ADD_SQL    = "SELECT * FROM sb_mainResultDetail WHERE maintainResultID='?'";
  private static final String mainapplydetail_SQL="select b.* from sb_maintainapply a,sb_applydetail b where a.maintainapplyid=b.maintainapplyid and b.maintainapplyid='?'";
  private static final String mainapplydetail_sql="select b.* from sb_maintainapply a,sb_applydetail b where 1<>1";
  private static final String mainPlandetail_SQL="select b.*,a.maintainplanno from sb_maintainplan a,sb_mainplandetail b where a.maintainplanid=b.maintainplanid and b.maintainplanid ='?'";
  private static final String mainPlandetail_sql="select b.*,a.maintainplanno from sb_maintainplan a,sb_mainplandetail b where 1<>1";
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM sb_maintainResult WHERE maintainResultID='?'";
  /**
   * 建立数据集
   */
  private EngineDataSet dsMasterTable = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable = new EngineDataSet();//从表
  private EngineDataSet dsselectTable = new EngineDataSet();
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sb_maintainResult");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sb_mainResultDetail");
  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;

  public  boolean isMasterAdd = true;
  public  boolean isDetailAdd = false;          //从表是否在添加状态
  public  boolean isApprove = false;            //是否在审批状态
  private RowMap     m_RowInfo  = new RowMap(); //主表添加行或修改行的引用
  private ArrayList  d_RowInfos = null;         //从表多行记录的引用
  private long  mastereditRow = -1;             //保存主表修改操作的行记录指针
  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;
  //---------------------
  public  String loginID = null;    //登录员工的ID
  //public String loginName = null;   //登录员工的姓名
  private User user = null;        //登陆用户（设置用户部门权限）
  public  String loginDept = null;   //登录员工的部门
  private String creatorID =null;  //操作员ID
  private String creator =null;    //操作员==登录员工的姓名
  private String filialeID = null;  //分公司ID
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public  static final String CANCLE_APPROVE = null;//取消审批
  public String maintainResultID = null;
  public String maintainresultid = null;
  public String billType = null;
 // public static final String SHOW_DETAIL = "10001";
  public static final String EQUIPMENT_CHANGE = "10002";
  public static final String PUT_EQUIPMENTID = "100010";
  public static final String SHOW_SELECT = "1500";



  /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_EquipmentResult getInstance(HttpServletRequest request)
  {
    B_EquipmentResult b_EquipmentResult = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_EquipmentResult";
      b_EquipmentResult =(B_EquipmentResult)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_EquipmentResult == null)
      {
        b_EquipmentResult = new B_EquipmentResult();
        LoginBean loginBean = LoginBean.getInstance(request);
        b_EquipmentResult.filialeID = loginBean.getFirstDeptID();//分公司id
        b_EquipmentResult.creator=loginBean.getUserName();//制单人
        b_EquipmentResult.user=loginBean.getUser();//登陆用户
        b_EquipmentResult.loginID=loginBean.getUserID();//操作员ID--》登陆用户id
        b_EquipmentResult.loginDept = loginBean.getDeptID();//登陆用户部门
        b_EquipmentResult.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        session.setAttribute(beanName, b_EquipmentResult);
      }
    }
    return b_EquipmentResult;
  }
  /**
   * 构造函数
   */
  public B_EquipmentResult()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  public  HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "sb_maintainresult", "sb_maintainresult");//查询得到数据库中配置的字段
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsMasterTable, Master_STRUT_SQL);
    setDataSetProperty(dsDetailTable, Detail_STRUT_SQL);
    setDataSetProperty(dsselectTable, mainapplydetail_sql);
    setDataSetProperty(dsselectTable, mainPlandetail_sql);
    //按保养日期或维修日期排序
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"maintain_date"}, new boolean[]{true}, null, 0));
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"maintainResultID"}, new String[]{"s_sb_maintainResult"}));
    //单据号自动编号
    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"maintainresultno"}, new String[]{"SELECT pck_base.billNextCode('sb_maintainresult','maintainresultno','') from dual"}));

    dsDetailTable.setSort(new SortDescriptor("", new String[]{"mainResultDetailID"}, new boolean[]{false}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"mainResultDetailID"}, new String[]{"s_sb_mainResultDetail"}));

    //添加操作的触发对象
    Master_Add_Edit masteradd_edit = new Master_Add_Edit();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(ADD),  masteradd_edit);
    addObactioner(String.valueOf(EDIT), masteradd_edit);
    addObactioner(String.valueOf(POST), new Master_Post());
    addObactioner(String.valueOf(POST_CONTINUE),new Master_Post());
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCLE_APPROVE), new Cancle_Approve());
    //addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(EQUIPMENT_CHANGE, new EquipmentChange());
    addObactioner(PUT_EQUIPMENTID, new putEquipmentid());
    addObactioner(SHOW_SELECT, new showSelect());
  }
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
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isMasterAdd; }
  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
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
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }
  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster,boolean isMasterAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    //是否是主表
    if(isMaster){
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();
      if(!isMasterAdd)
        m_RowInfo.put(getMasterTable());
      else
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        m_RowInfo.put("createDate", today);//制单日期
        m_RowInfo.put("creator", creator);//制单人
        m_RowInfo.put("creatorID", loginID);//制单人id
        m_RowInfo.put("filialeid",filialeID);//分公司ID
        m_RowInfo.put("deptid", loginDept);
      }
    }
    else{
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

      detailRow.put("personid",rowInfo.get("personid_"+i));              //人员ID==保养人/排除故障人
      detailRow.put("cpId", rowInfo.get("cpId_"+i));                     //产品ID==设备
      detailRow.put("cpbm", rowInfo.get("cpbm_"+i));
      detailRow.put("pm", rowInfo.get("pm_"+i));
      detailRow.put("excepReasonID", rowInfo.get("excepReasonID_"+i));   //故障原因ID
      detailRow.put("content", rowInfo.get("content_"+i));               //内容
      /*实际开始时间*/
      String fact_startdate=null;
      String fact_startdate_hour=rowInfo.get("fact_startdate_hour_"+i);
      if(fact_startdate_hour.length()<2)fact_startdate_hour="0"+fact_startdate_hour;
      String fact_startdate_minute=rowInfo.get("fact_startdate_minute_"+i);
      if(fact_startdate_minute.length()<2)fact_startdate_minute="0"+fact_startdate_minute;
      detailRow.put("fact_startdate",rowInfo.get("fact_startdate_"+i));
      if(!rowInfo.get("fact_startdate_hour_"+i).trim().equals("")&&!rowInfo.get("fact_startdate_minute_"+i).trim().equals(""))
        fact_startdate=rowInfo.get("fact_startdate_"+i)+" "+fact_startdate_hour+":"+fact_startdate_minute+":"+"00";
      else fact_startdate=rowInfo.get("fact_startdate_"+i);
      detailRow.put("fact_startdate", fact_startdate);
      /*实际完成时间*/
      String fact_finishdate=null;
      String fact_finishdate_hour=rowInfo.get("fact_finishdate_hour_"+i);
      if(fact_finishdate_hour.length()<2)fact_finishdate_hour="0"+fact_finishdate_hour;
      String fact_finishdate_minute=rowInfo.get("fact_finishdate_minute_"+i);
      if(fact_finishdate_minute.length()<2)fact_finishdate_minute="0"+fact_finishdate_minute;
      detailRow.put("fact_finishdate",rowInfo.get("fact_finishdate_"+i));
      if(!rowInfo.get("fact_finishdate_hour_"+i).trim().equals("")&&!rowInfo.get("fact_finishdate_minute_"+i).trim().equals(""))
        fact_finishdate=rowInfo.get("fact_finishdate_"+i)+" "+fact_finishdate_hour+":"+fact_finishdate_minute+":"+"00";
      else fact_finishdate=rowInfo.get("fact_finishdate_"+i);
      detailRow.put("fact_finishdate", fact_finishdate);
      detailRow.put("finish_circs",rowInfo.get("finish_circs_"+i)); //完成情况
      detailRow.put("memo", rowInfo.get("memo_"+i));                     //备注
      detailRow.put("fault_depict",rowInfo.get("fault_depict_"+i));      //故障描述
      /*故障发生时间*/
      String fault_time=null;
      String fault_time_hour=rowInfo.get("fault_time_hour_"+i);
      if(fault_time_hour.length()<2)fault_time_hour="0"+fault_time_hour;
      String fault_time_minute=rowInfo.get("fault_time_minute_"+i);
      if(fault_time_minute.length()<2)fault_time_minute="0"+fault_time_minute;
      detailRow.put("fault_time",rowInfo.get("fault_time_"+i));
      if(!rowInfo.get("fault_time_hour_"+i).trim().equals("")&&!rowInfo.get("fault_time_minute_"+i).trim().equals(""))
       fault_time=rowInfo.get("fault_time_"+i)+" "+fault_time_hour+":"+fault_time_minute+":"+"00";
      else fault_time=rowInfo.get("fault_time_"+i);
      detailRow.put("fault_time", fault_time);
      /*计划排故障时间*/
      String plan_debar_time=null;
      String plan_debar_time_hour = rowInfo.get("plan_debar_time_hour_"+i);
      if(plan_debar_time_hour.length()<2)plan_debar_time_hour="0"+plan_debar_time_hour;
      String plan_debar_time_minute=rowInfo.get("plan_debar_time_minute_"+i);
      if(plan_debar_time_minute.length()<2)plan_debar_time_minute="0"+plan_debar_time_minute;
      detailRow.put("plan_debar_time",rowInfo.get("plan_debar_time_"+i));
      if(!rowInfo.get("plan_debar_time_hour_"+i).trim().equals("")&&!rowInfo.get("plan_debar_time_minute_"+i).trim().equals(""))
      plan_debar_time=rowInfo.get("plan_debar_time_"+i)+" "+plan_debar_time_hour+":"+plan_debar_time_minute+":"+"00";
      else plan_debar_time=rowInfo.get("plan_debar_time_"+i);
      detailRow.put("plan_debar_time",plan_debar_time);

      detailRow.put("debar_method",rowInfo.get("debar_method_"+i));       //故障排除方法
      /*故障排除时间*/
      String debar_time=null;
      String debar_time_hour = rowInfo.get("debar_time_hour_"+i);
      if(debar_time_hour.length()<2)debar_time_hour="0"+debar_time_hour;
      String debar_time_minute=rowInfo.get("debar_time_minute_"+i);
      if(debar_time_minute.length()<2)debar_time_minute="0"+debar_time_minute;
      detailRow.put("debar_time",rowInfo.get("debar_time_"+i));
      if(!rowInfo.get("debar_time_hour_"+i).trim().equals("")&&!rowInfo.get("debar_time_minute_"+i).trim().equals(""))
      debar_time=rowInfo.get("debar_time_"+i)+" "+debar_time_hour+":"+debar_time_minute+":"+"00";
      else debar_time=rowInfo.get("debar_time_"+i);
      detailRow.put("debar_time",debar_time);

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
  public final EngineDataSet getMasterTable()
  {
    if(!dsMasterTable.isOpen())
      dsMasterTable.open();
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
    String SQL = isMasterAdd ? "-1" : maintainResultID;
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
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      String SQL=combineSQL(Master_Quality_SQL,"?",new String[]{user.getHandleDeptValue("deptid")});
      dsMasterTable.setQueryString(SQL);
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginID);
      detailProducer.init(request, loginID);
    }
  }
  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isMasterAdd = action.equals(String.valueOf(ADD));
      if(!isMasterAdd)
      {
        isDetailAdd = false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        mastereditRow = dsMasterTable.getInternalRow();
        maintainResultID = dsMasterTable.getValue("maintainResultID");
      }
      else{//打开从表
        isDetailAdd = true;
      }
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      String bill_type= data.getParameter("billType");
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      data.setMessage(showJavaScript("toDetail("+bill_type+");"));
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发保存操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      m_RowInfo.put(data.getRequest());
      putDetailInfo(data.getRequest());
      String deptid = m_RowInfo.get("deptid");                             //部门id
      String mainPlanDetailID = m_RowInfo.get("mainPlanDetailID");         //设备保养计划明细ID
      String maiApplyDetailID = m_RowInfo.get("maiApplyDetailID");         //设备维修、保养申请明细ID
      String equipmentid = m_RowInfo.get("equipmentid");                      //设备ID
      //String maintainPlanNO = m_RowInfo.get("maintainPlanNO");                //设备保养计划单号
      String maintainapplyNO = m_RowInfo.get("maintainapplyNO");              //设备保养申请单号
      String billType = m_RowInfo.get("billType");                            //单据类型
      String maintain_date = m_RowInfo.get("maintain_date");                   //日期
      /*启用日期*/
      String start_date = null;
      String start_date_hour = m_RowInfo.get("start_date_hour");
      if(start_date_hour.length()<2)start_date_hour="0"+start_date_hour;
      String start_date_minute = m_RowInfo.get("start_date_minute");
      if(start_date_minute.length()<2)start_date_minute="0"+start_date_minute;
      if(!m_RowInfo.get("start_date_hour").trim().equals("")&&!m_RowInfo.get("start_date_minute").trim().equals(""))
         start_date=m_RowInfo.get("start_date")+" "+start_date_hour+":"+start_date_minute+":"+"00";
      else start_date=m_RowInfo.get("start_date");

      String maintain_type = m_RowInfo.get("maintain_type");                  //维修类别
      String by_type = m_RowInfo.get("by_type");                              //保养类别
      /*停用日期*/
      String stop_date = null;
      String stop_date_hour = m_RowInfo.get("stop_date_hour");
      if(stop_date_hour.length()<2)stop_date_hour="0"+stop_date_hour;
      String stop_date_minute = m_RowInfo.get("stop_date_minute");
      if(stop_date_minute.length()<2)stop_date_minute="0"+stop_date_minute;
      if(!m_RowInfo.get("stop_date_hour").trim().equals("")&&!m_RowInfo.get("stop_date_minute").trim().equals(""))
        stop_date=m_RowInfo.get("stop_date")+" "+stop_date_hour+":"+stop_date_minute+":"+"00";
      else stop_date=m_RowInfo.get("stop_date");

      String stop_causation = m_RowInfo.get("stop_causation");                //停产原因
      String verdict = m_RowInfo.get("verdict");                              //结论
      String approverID = m_RowInfo.get("approverID");            //审批人
      String state_desc = m_RowInfo.get("state_desc");            //状态描述
      String createDate = m_RowInfo.get("createDate");            //制单日期
      String creatorID = m_RowInfo.get("creatorID");              //制单人ID
      String creator = m_RowInfo.get("creator");                  //制单人
      if(d_RowInfos.size()<1)
      {
        data.setMessage(showJavaScript("alert('不能保存空的数据')"));
        return;
      }
      if(equipmentid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择设备！');"));
        return;
      }
      EngineDataSet ds = getMasterTable();
      if(!isMasterAdd)
        ds.goToInternalRow(mastereditRow);
      String maintainResultID = null;
      String drawcode = null;
      if(isMasterAdd){
        ds.insertRow(false);
        maintainResultID = dataSetProvider.getSequence("s_sb_maintainResult");
        ds.setValue("maintainResultID", maintainResultID);
        if(billType.equals("0"))//维修记录单据号
        {
           drawcode = dataSetProvider.getSequence("SELECT pck_base.billNextCode('sb_maintainresult','maintainresultno','a') from dual");
        }
        else//保养记录单据号
        {
           drawcode = dataSetProvider.getSequence("SELECT pck_base.billNextCode('sb_maintainresult','maintainresultno','b') from dual");
        }
      }
      ds.setValue("maintainresultno",drawcode);
      ds.setValue("deptid",deptid);
      //ds.setValue("mainPlanDetailID",mainPlanDetailID);
      ds.setValue("maiApplyDetailID",maiApplyDetailID);
      ds.setValue("equipmentid", equipmentid);
      //ds.setValue("maintainPlanNO",maintainPlanNO);
      ds.setValue("maintainapplyNO",maintainapplyNO);
      ds.setValue("billType",billType);
      ds.setValue("maintain_date",maintain_date);
      ds.setValue("start_date",start_date);
      ds.setValue("maintain_type",maintain_type);
      ds.setValue("by_type",by_type);
      ds.setValue("stop_date",stop_date);
      ds.setValue("stop_causation",stop_causation);
      ds.setValue("verdict",verdict);
      ds.setValue("state", "0");                 //状态
      ds.setValue("approverID", approverID);
      ds.setValue("state_desc", state_desc);
      ds.setValue("createDate", createDate);
      ds.setValue("creatorID", creatorID);
      ds.setValue("creator", creator);
      ds.setValue("filialeID", filialeID);
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        detail.setValue("maintainResultID",maintainResultID);              //设备维修、保养记录ID
        detail.setValue("personid",detailrow.get("personid"));             //人员ID==保养人/排除故障人
        detail.setValue("cpId", detailrow.get("cpId"));                    //产品ID==设备
        if(billType.equals("0")){
          detail.setValue("excepReasonID", detailrow.get("excepReasonID"));
        }  //故障原因ID
        detail.setValue("mainplandetailid", detailrow.get("mainplandetailid"));
        detail.setValue("maintainPlanNO", detailrow.get("maintainPlanNO"));
        detail.setValue("content", detailrow.get("content"));              //内容
        detail.setValue("fact_startdate",detailrow.get("fact_startdate"));     //实际开始时间
        detail.setValue("fact_finishdate",detailrow.get("fact_finishdate"));  //实际完成时间
        detail.setValue("finish_circs",detailrow.get("finish_circs"));        //完成情况
        detail.setValue("memo", detailrow.get("memo"));                      //备注
        detail.setValue("fault_depict",detailrow.get("fault_depict"));        //故障描述
        detail.setValue("fault_time",detailrow.get("fault_time"));           //故障发生时间
        detail.setValue("plan_debar_time",detailrow.get("plan_debar_time")); //计划排故障时间
        detail.setValue("debar_method",detailrow.get("debar_method"));      //故障排除方法
        detail.setValue("debar_time",detailrow.get("debar_time"));         //故障排除时间
        detail.post();
        detail.next();
      }
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);//重新初始化从表的各行信息
        detail.empty();
        initRowInfo(false, false, true);//重新初始化从表的各行信息
      }
      else
        data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   * 主表删除操作的触发类
   */
  class Master_Delete implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发保存操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(isMasterAdd){
        data.setMessage(showJavaScript("backList();"));
        return;
      }
      EngineDataSet ds = getMasterTable();
      ds.goToInternalRow(mastereditRow);
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   * 从表添加操作的触发类
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMasterTable();
      isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
      if(!isMasterAdd)
        ds.goToInternalRow(mastereditRow);
      String maintainResultID = dsMasterTable.getValue("maintainResultID");
      detail.insertRow(false);
      detail.setValue("maintainResultID", isMasterAdd ? "-1" : maintainResultID);
      detail.post();
      d_RowInfos.add(new RowMap());
      //equipmentChange.execute(action,o,data,arg);
      String equipmentid=data.getParameter("equipmentid");
      m_RowInfo.put("equipmentid", equipmentid);
      m_RowInfo.put("maintainapplyNO", data.getParameter("maintainapplyNO"));
      m_RowInfo.put("maintainPlanNO", data.getParameter("maintainPlanNO"));
      String stop_date= data.getParameter("stop_date");
      String stop_date_hour= data.getParameter("stop_date_hour");
      String stop_date_minute= data.getParameter("stop_date_minute");
      String new_stop_date=null;
      if(!stop_date.equals("")&&!stop_date_hour.equals("")&&!stop_date_minute.equals(""))
      {
        new_stop_date=stop_date+" "+stop_date_hour+":"+stop_date_minute+":"+"00";
      }
      m_RowInfo.put("stop_date", new_stop_date);//停用时间

      String start_date= data.getParameter("start_date");
      String start_date_hour= data.getParameter("start_date_hour");
      String start_date_minute= data.getParameter("start_date_minute");
      String new_start_date = null;
      if(!start_date.equals("")&&!start_date_hour.equals("")&&!start_date_minute.equals(""))
      {
        new_start_date=start_date+" "+start_date_hour+":"+start_date_minute+":"+"00";
      }
      m_RowInfo.put("start_date",new_start_date);//启用时间
    }
  }
  public final EngineDataSet getselectdetailTable(){
    if(!dsselectTable.isOpen())
      dsselectTable.open();
    return dsselectTable;
  }
//打开引维修申请单从表
  public final void openselectdetailTable(boolean isMasterAdd,String maintainapplyID)
  {
    String  SQL = isMasterAdd ? "-1" : maintainapplyID;
    SQL = combineSQL(mainapplydetail_SQL, "?", new String[]{SQL});
    dsselectTable.setQueryString(SQL);
    if(!dsselectTable.isOpen())
      dsselectTable.open();
    else
      dsselectTable.refresh();
  }
  /*打开引保养计划从表*/
  public final void openplandetailTable(boolean isMasterAdd,String maintainplanID)
  {
    String  SQL = isMasterAdd ? "-1" : maintainplanID;
    SQL = combineSQL(mainPlandetail_SQL, "?", new String[]{SQL});
    dsselectTable.setQueryString(SQL);
    if(!dsselectTable.isOpen())
      dsselectTable.open();
    else
      dsselectTable.refresh();
  }
    /*设备ID改变触发的事件*/
  class EquipmentChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String equipmentid=data.getParameter("equipmentid");
      m_RowInfo.put("equipmentid", equipmentid);
      m_RowInfo.put("maintainapplyNO", data.getParameter("maintainapplyNO"));

      String stop_date= data.getParameter("stop_date");
      String stop_date_hour= data.getParameter("stop_date_hour");
      String stop_date_minute= data.getParameter("stop_date_minute");
      String new_stop_date=null;
      if(!stop_date.equals("")&&!stop_date_hour.equals("")&&!stop_date_minute.equals(""))
      {
        new_stop_date=stop_date+" "+stop_date_hour+":"+stop_date_minute+":"+"00";
      }
      m_RowInfo.put("stop_date", new_stop_date);//停用时间

      String start_date= data.getParameter("start_date");
      String start_date_hour= data.getParameter("start_date_hour");
      String start_date_minute= data.getParameter("start_date_minute");
      String new_start_date = null;
      if(!start_date.equals("")&&!start_date_hour.equals("")&&!start_date_minute.equals(""))
      {
        new_start_date=start_date+" "+start_date_hour+":"+start_date_minute+":"+"00";
      }
      m_RowInfo.put("start_date",new_start_date);//启用时间
      /*保存输入的明细信息*/
      HttpServletRequest req = data.getRequest();
      engine.erp.equipment.B_EquipmentResult b_EquipmentResultBean = engine.erp.equipment.B_EquipmentResult.getInstance(req);
      dsDetailTable.deleteAllRows();
      d_RowInfos.clear();
      String  maintainapplyID = data.getParameter("maintainapplyID");
      String  maintainPlanID =data.getParameter("maintainPlanID");
      m_RowInfo.put("maintainPlanID",maintainPlanID);
      String  billType = data.getParameter("billType");                       //单据类型
      if(billType.equals("0")){/*维修记录*/
        b_EquipmentResultBean.openselectdetailTable(false,maintainapplyID);
      }
      else{
        b_EquipmentResultBean.openplandetailTable(false,maintainPlanID);
      }
      dsselectTable.first();
      if(billType.equals("0")){
        //dsMasterTable.setValue("maiapplydetailid",dsselectTable.getValue("maiapplydetailid"));
        m_RowInfo.put("maiapplydetailid",dsselectTable.getValue("maiapplydetailid"));
      }
      int i=0;
      for(;i<dsselectTable.getRowCount();i++)
      {
        RowMap detailrow = null;
        dsDetailTable.insertRow(false);
        if(billType.equals("0")){
          dsDetailTable.setValue("maintainResultID",maintainResultID);
          dsDetailTable.setValue("cpid",dsselectTable.getValue("cpid"));
          dsDetailTable.setValue("memo",dsselectTable.getValue("memo"));
          dsDetailTable.setValue("content",dsselectTable.getValue("content"));
          dsDetailTable.setValue("excepReasonID",dsselectTable.getValue("excepReasonID"));
        }
        else{
          dsDetailTable.setValue("maintainResultID",maintainResultID);
          dsDetailTable.setValue("mainplandetailid",dsselectTable.getValue("mainplandetailid"));
          dsDetailTable.setValue("maintainPlanNO",dsselectTable.getValue("maintainPlanNO"));
          dsDetailTable.setValue("cpid",dsselectTable.getValue("cpid"));
          dsDetailTable.setValue("content",dsselectTable.getValue("maintain_content"));
        }
        dsDetailTable.post();
        //创建一个与用户相对应的行
        detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
        dsselectTable.next();
        initRowInfo(false,false,true);
      }
    }
  }
  /**
   * 从表删除操作的触发类
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
      //equipmentChange.execute(action,o,data,arg);
      String equipmentid=data.getParameter("equipmentid");
      m_RowInfo.put("equipmentid", equipmentid);
      m_RowInfo.put("maintainapplyNO", data.getParameter("maintainapplyNO"));
      m_RowInfo.put("maintainPlanNO", data.getParameter("maintainPlanNO"));
      String stop_date= data.getParameter("stop_date");
      String stop_date_hour= data.getParameter("stop_date_hour");
      String stop_date_minute= data.getParameter("stop_date_minute");
      String new_stop_date=null;
      if(!stop_date.equals("")&&!stop_date_hour.equals("")&&!stop_date_minute.equals(""))
      {
        new_stop_date=stop_date+" "+stop_date_hour+":"+stop_date_minute+":"+"00";
      }
      m_RowInfo.put("stop_date", new_stop_date);//停用时间

      String start_date= data.getParameter("start_date");
      String start_date_hour= data.getParameter("start_date_hour");
      String start_date_minute= data.getParameter("start_date_minute");
      String new_start_date = null;
      if(!start_date.equals("")&&!start_date_hour.equals("")&&!start_date_minute.equals(""))
      {
        new_start_date=start_date+" "+start_date_hour+":"+start_date_minute+":"+"00";
      }
      m_RowInfo.put("start_date",new_start_date);//启用时间
    }
  }
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      mastereditRow = dsMasterTable.getInternalRow();
      maintainResultID = dsMasterTable.getValue("maintainResultID");
      //打开从表
      openDetailTable(false);
    }
  }

  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String tempSQL=null;
      String SQL=combineSQL(Master_Quality_SQL,"?",new String[]{user.getHandleDeptValue("deptid")});
      table.getWhereInfo().setWhereValues(data.getRequest());
      tempSQL = table.getWhereInfo().getWhereQuery();
      if(tempSQL.length() > 0) SQL=SQL+" and "+tempSQL;
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
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
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterTable.getValue("maintainResultNO");
      String billType=data.getParameter("billType");
      String billName=billType.equals("0")?"servicing_result":"maintain_result";
      String deptid = dsMasterTable.getValue("deptid");//下达车间
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), billName, content, deptid);
    }
  }
    /*审批操作*/
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginID);
      detailProducer.init(request, loginID);
      String id = data.getParameter("id");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();
      isApprove = true;
      maintainResultID = dsMasterTable.getValue("maintainResultID");
      //打开从表
      openDetailTable(false);
      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
    }
  }
  /**
   * 取消审批触发操作
   */
  class Cancle_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String billName=billType.equals("0")?"servicing_result":"maintain_result";
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), billName);
    }
  }

  ///////////
  class putEquipmentid implements Obactioner{
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception{
      m_RowInfo.put("equipmentid", data.getParameter("equipmentid"));
    }
  }
  class showSelect implements Obactioner{
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception{
      dsMasterTable.close();
      dsDetailTable.close();
      String SQL="SELECT * FROM sb_maintainResult where maintainresultid="+maintainresultid;
      String Detail_SQL="SELECT * FROM sb_mainResultDetail WHERE maintainresultid="+maintainresultid;
      dsMasterTable.setQueryString(SQL);
      dsDetailTable.setQueryString(Detail_SQL);
      dsMasterTable.open();
      dsDetailTable.open();
      dsMasterTable.refresh();
      dsDetailTable.refresh();
      m_RowInfo.put("state","1");
      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
      isMasterAdd=false;
    }
  }
  //SHOW_SELECT
}

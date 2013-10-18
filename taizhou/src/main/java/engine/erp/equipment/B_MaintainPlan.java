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
 * <p>Title: 设备管理--设备保养计划</p>
 * <p>Description: 设备管理--设备保养计划</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */
public final class B_MaintainPlan  extends BaseAction implements Operate
{
  /**
   * 提取所有信息的SQL语句
   */
  private static final String Master_Quality_SQL = "SELECT * FROM sb_maintainPlan where ? ?";

  private static final String Master_STRUT_SQL = "SELECT * FROM sb_maintainPlan where 1<>1";
  private static final String Detail_STRUT_SQL="SELECT * FROM sb_mainPlanDetail where 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sb_mainPlanDetail WHERE maintainPlanID='?' ";
  //private static final String Detail_STRUT_SQL="select a.maintainresultid,a.finish_circs,a.fact_startdate,a.fact_finishdate,a.personid,"+
                                               //" c.cpid,c.maintain_content,c.plan_startdate,c.plan_finishdate,c.mainplandetailid"+
                                              // " from sb_mainresultdetail a,sb_maintainresult b,sb_mainplandetail c,sb_maintainplan d where 1<>1";

  //private static final String DETAIL_SQL="select a.maintainresultid,a.finish_circs,a.fact_startdate,a.fact_finishdate,a.personid,"+
                                        // " c.cpid,c.maintain_content,c.plan_startdate,c.plan_finishdate,c.mainplandetailid"+
                                        // " from sb_mainresultdetail a,sb_maintainresult b,sb_mainplandetail c,sb_maintainplan d"+
                                         //" where a.maintainresultid=b.maintainresultid"+
                                        // " and b.mainplandetailid=c.mainplandetailid"+
                                         //" and c.maintainplanid=d.maintainplanid and c.maintainPlanID='?'";

  private static final String mainapplydetail_SQL="select b.* from sb_maintainapply a,sb_applydetail b where a.maintainapplyid=b.maintainapplyid and b.maintainapplyid='?'";

  private static final String mainapplydetail_sql="select b.* from sb_maintainapply a,sb_applydetail b where 1<>1";

  private static final String resultdetail_SQL="select a.*,b.*,c.maintainplanid from sb_mainresultdetail a,sb_maintainresult b,sb_mainplandetail c"+
                                                " where a.maintainresultid=b.maintainresultid and b.mainplandetailid=c.mainplandetailid and c.maintainPlanID='?'";
  private static final String resultdetail_sql="select a.*,b.*,c.maintainplanid from sb_mainresultdetail a,sb_maintainresult b,sb_mainplandetail c where 1<>1";
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM sb_maintainPlan WHERE maintainPlanID='?'";
  /**
   * 建立数据集
   */
  private EngineDataSet dsMasterTable = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable = new EngineDataSet();//从表
  private EngineDataSet dsmaintainapplyTable = new EngineDataSet();//引申请单明细
  private EngineDataSet dsresultdetailTable=new EngineDataSet();//保养记录从表
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sb_maintainPlan");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sb_mainPlanDetail");
  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;

  public  boolean isMasterAdd = true;
  public  boolean isDetailAdd = false;          // 从表是否在添加状态
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
  private String maintainPlanID = null;
  public static final String SHOW_DETAIL = "10001";
  public static final String EQUIPMENT_CHANGE = "10002";
  public static final String PUT_EQUIPMENTID = "100010";

  /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_MaintainPlan getInstance(HttpServletRequest request)
  {
    B_MaintainPlan b_MaintainPlan = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_MaintainPlan";
      b_MaintainPlan =(B_MaintainPlan)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_MaintainPlan == null)
      {
        b_MaintainPlan = new B_MaintainPlan();
        LoginBean loginBean = LoginBean.getInstance(request);
        b_MaintainPlan.filialeID = loginBean.getFirstDeptID();//分公司id
        b_MaintainPlan.creator=loginBean.getUserName();//制单人
        b_MaintainPlan.user=loginBean.getUser();//登陆用户
        b_MaintainPlan.loginID=loginBean.getUserID();//操作员ID--》登陆用户id
        b_MaintainPlan.loginDept = loginBean.getDeptID();//登陆用户部门
        b_MaintainPlan.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        session.setAttribute(beanName, b_MaintainPlan);
      }
   }
    return b_MaintainPlan;
  }
  /**
   * 构造函数
   */
  public B_MaintainPlan()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  public  HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "sb_maintainplan", "sb_maintainplan");//查询得到数据库中配置的字段
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsMasterTable, Master_STRUT_SQL);
    setDataSetProperty(dsDetailTable, Detail_STRUT_SQL);
    setDataSetProperty(dsmaintainapplyTable, mainapplydetail_sql);
    setDataSetProperty(dsresultdetailTable, resultdetail_sql);
    //按计划日期排序
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"plan_date"}, new boolean[]{true}, null, 0));
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"maintainPlanID"}, new String[]{"s_sb_maintainPlan"}));
    //单据号自动编号
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"maintainplanno"}, new String[]{"SELECT pck_base.billNextCode('sb_maintainplan','maintainplanno','') from dual"}));

    dsDetailTable.setSort(new SortDescriptor("", new String[]{"mainPlanDetailID"}, new boolean[]{false}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"mainPlanDetailID"}, new String[]{"s_sb_mainPlanDetail"}));

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
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(EQUIPMENT_CHANGE, new EquipmentChange());
    addObactioner(PUT_EQUIPMENTID, new putEquipmentid());
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
      detailRow.put("cpid", rowInfo.get("cpid_"+i));                                      //产品ID
      detailRow.put("maintain_content", rowInfo.get("maintain_content_"+i));              //保养内容
      /*计划开始时间*/
      String plan_startdate=null;
      String plan_startdate_hour=rowInfo.get("plan_startdate_hour_"+i);
      if(plan_startdate_hour.length()<2)plan_startdate_hour="0"+plan_startdate_hour;
      String plan_startdate_minute=rowInfo.get("plan_startdate_minute_"+i);
      if(plan_startdate_minute.length()<2)plan_startdate_minute="0"+plan_startdate_minute;
      detailRow.put("plan_startdate",rowInfo.get("plan_startdate_"+i));
      if(!rowInfo.get("plan_startdate_hour_"+i).trim().equals("")&&!rowInfo.get("plan_startdate_minute_"+i).trim().equals(""))
        plan_startdate=rowInfo.get("plan_startdate_"+i)+" "+plan_startdate_hour+":"+plan_startdate_minute+":"+"00";
      else plan_startdate=rowInfo.get("plan_startdate_"+i);
      detailRow.put("plan_startdate", plan_startdate);
      /*计划完成时间*/
      String plan_finishdate=null;
      String plan_finishdate_hour=rowInfo.get("plan_finishdate_hour_"+i);
      if(plan_finishdate_hour.length()<2)plan_finishdate_hour="0"+plan_finishdate_hour;
      String plan_finishdate_minute=rowInfo.get("plan_finishdate_minute_"+i);
      if(plan_finishdate_minute.length()<2)plan_finishdate_minute="0"+plan_finishdate_minute;
      detailRow.put("plan_finishdate",rowInfo.get("plan_finishdate_"+i));
      if(!rowInfo.get("plan_finishdate_hour_"+i).trim().equals("")&&!rowInfo.get("plan_finishdate_minute_"+i).trim().equals(""))
        plan_finishdate=rowInfo.get("plan_finishdate_"+i)+" "+plan_finishdate_hour+":"+plan_finishdate_minute+":"+"00";
      else plan_finishdate=rowInfo.get("plan_finishdate_"+i);
      detailRow.put("plan_finishdate", plan_finishdate);
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
    String SQL = isMasterAdd ? "-1" : maintainPlanID;
    SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});

    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    else
      dsDetailTable.refresh();
  }

  /*得到保养记录从表表对象*/
  public final EngineDataSet getresultDetailTable(){
    if(!dsresultdetailTable.isOpen())
      dsresultdetailTable.open();
    return dsresultdetailTable;
  }
    /*打开保养记录从表*/
  public final void openresultDetailTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : maintainPlanID;
    SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});

    dsresultdetailTable.setQueryString(SQL);
    if(!dsresultdetailTable.isOpen())
      dsresultdetailTable.open();
    else
      dsresultdetailTable.refresh();
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
        maintainPlanID = dsMasterTable.getValue("maintainPlanID");
      }
      else{//打开从表
        isDetailAdd = true;
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
      EngineDataSet ds = getMasterTable();
      EngineDataSet detail = getDetailTable();
      String deptid = m_RowInfo.get("deptid");                       //部门id==计划部门
      String equipmentid=m_RowInfo.get("equipmentid");               //设备ID
      String personid=m_RowInfo.get("personid");                     //人员ID==计划人
      String maintainapplyNO=m_RowInfo.get("maintainapplyNO");       //设备保养申请单号
      String apply_deptid=m_RowInfo.get("apply_deptid");             //申请部门ID
      String proposerID=m_RowInfo.get("proposerID");                 //申请人ID
      String plan_date=m_RowInfo.get("plan_date");                   //计划日期
      String approverID = m_RowInfo.get("approverID");            //审批人
      String state_desc = m_RowInfo.get("state_desc");            //状态描述
      String createDate = m_RowInfo.get("createDate");            //制单日期
      String creatorID = m_RowInfo.get("creatorID");              //制单人ID
      String creator = m_RowInfo.get("creator");                  //制单人
      String memo = m_RowInfo.get("memo");               //备注
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
      if(deptid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择申请部门！');"));
        return;
      }
      if(personid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择计划人！');"));
        return;
      }
      if(detail.getColumnCount()<0){
        data.setMessage(showJavaScript("alert('从表不能为空！');"));
        return;
      }
      if(!isMasterAdd)
        ds.goToInternalRow(mastereditRow);
      String maintainPlanID = null;
      if(isMasterAdd){
        ds.insertRow(false);
        maintainPlanID = dataSetProvider.getSequence("s_sb_maintainPlan");
        ds.setValue("maintainPlanID", maintainPlanID);
      }
      ds.setValue("deptid",deptid);
      ds.setValue("equipmentid", equipmentid);
      ds.setValue("personid", personid);
      ds.setValue("maintainapplyNO", maintainapplyNO);
      ds.setValue("apply_deptid",apply_deptid);
      ds.setValue("proposerID",proposerID);
      ds.setValue("plan_date", plan_date);
      ds.setValue("state", "0");                 //状态
      ds.setValue("approverID", approverID);
      ds.setValue("state_desc", state_desc);
      ds.setValue("createDate", createDate);
      ds.setValue("creatorID", creatorID);
      ds.setValue("creator", creator);
      ds.setValue("filialeID", filialeID);
      ds.setValue("memo",memo);
     //保存从表的数据
      RowMap detailrow = null;

      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        detail.setValue("maintainPlanID", maintainPlanID);
        detail.setValue("cpid", detailrow.get("cpid"));                                       //产品ID
        detail.setValue("maintain_content", detailrow.get("maintain_content"));               //保养内容
        detail.setValue("plan_startdate", detailrow.get("plan_startdate"));                    //计划开始时间
        detail.setValue("plan_finishdate", detailrow.get("plan_finishdate"));                 //计划完成时间
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
      String maintainPlanID = dsMasterTable.getValue("maintainPlanID");
      detail.insertRow(false);
      detail.setValue("maintainPlanID", isMasterAdd ? "-1" : maintainPlanID);
      detail.post();
      d_RowInfos.add(new RowMap());
    }
  }
  public final EngineDataSet getselectdetailTable(){
    if(!dsmaintainapplyTable.isOpen())
      dsmaintainapplyTable.open();
    return dsmaintainapplyTable;
  }
  //打开引保养申请单从表
  public final void openselectdetailTable(boolean isMasterAdd,String maintainapplyID)
  {
    String SQL = isMasterAdd ? "-1" : maintainapplyID;
    SQL = combineSQL(mainapplydetail_SQL, "?", new String[]{SQL});
    dsmaintainapplyTable.setQueryString(SQL);
    if(!dsmaintainapplyTable.isOpen())
      dsmaintainapplyTable.open();
    else
      dsmaintainapplyTable.refresh();
  }
  /*设备ID改变触发的事件*/
  class EquipmentChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String equipmentid=data.getParameter("equipmentid");
      m_RowInfo.put("equipmentid", equipmentid);
      m_RowInfo.put("maintainapplyNO", data.getParameter("maintainapplyNO"));
      m_RowInfo.put("apply_deptid", data.getParameter("apply_dept"));
      m_RowInfo.put("proposerid", data.getParameter("proposer"));
      m_RowInfo.put("deptid", data.getParameter("deptid"));
      m_RowInfo.put("personid", data.getParameter("personid"));
      /*保存输入的明细信息*/
      HttpServletRequest req = data.getRequest();
      engine.erp.equipment.B_MaintainPlan  b_maintainplanBean = engine.erp.equipment.B_MaintainPlan.getInstance(req);
      dsDetailTable.deleteAllRows();
      d_RowInfos.clear();
      //String  maintainapplyID = m_RowInfo.get("maintainapplyID");
      String  maintainapplyID = data.getParameter("maintainapplyID");
      b_maintainplanBean.openselectdetailTable(false,maintainapplyID);
      dsmaintainapplyTable.first();
      int i=0;
      for(;i<dsmaintainapplyTable.getRowCount();i++)
      {
        RowMap detailrow = null;
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("maintainPlanID",maintainPlanID);
        dsDetailTable.setValue("cpid",dsmaintainapplyTable.getValue("cpid"));
        dsDetailTable.setValue("maintain_content",dsmaintainapplyTable.getValue("content"));
        dsDetailTable.post();
        //创建一个与用户相对应的行
        detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
        dsmaintainapplyTable.next();
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
    }
  }
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      mastereditRow = dsMasterTable.getInternalRow();
      maintainPlanID = dsMasterTable.getValue("maintainPlanID");
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
      String content = dsMasterTable.getValue("maintainPlanNO");
      String deptid = dsMasterTable.getValue("deptid");//下达车间
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "maintainPlan", content, deptid);
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
      maintainPlanID = dsMasterTable.getValue("maintainPlanID");
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
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "maintainPlan");
    }
  }
  ///////////
  class putEquipmentid implements Obactioner{
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception{
      m_RowInfo.put("equipmentid", data.getParameter("equipmentid"));
    }
  }
}

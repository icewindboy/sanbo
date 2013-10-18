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
 * <p>Title: 设备管理--设备维修、保养申请</p>
 * <p>Description: 设备管理--设备维修、保养申请</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */
public final class B_MaintainRequisition extends BaseAction implements Operate
{
  /**
   * 提取所有信息的SQL语句
   */
  private static final String Master_Quality_SQL = "SELECT * FROM sb_maintainApply where ? ?";

  private static final String Master_STRUT_SQL = "SELECT * FROM sb_maintainApply where 1<>1";
  private static final String Detail_STRUT_SQL="SELECT * FROM sb_applyDetail where 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sb_applyDetail WHERE maintainapplyID='?' ";
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM sb_maintainApply WHERE maintainapplyID='?'";
  /**
   * 建立数据集
   */
  private EngineDataSet dsMasterTable = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable = new EngineDataSet();//从表
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sb_maintainApply");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sb_applyDetail");
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
  private String maintainapplyID = null;
  public static final String SHOW_DETAIL = "10001";
  public static final String PUT_EQUIPMENTID = "100010";
  /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_MaintainRequisition getInstance(HttpServletRequest request)
  {
    B_MaintainRequisition b_MaintainRequisition = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_MaintainRequisition";
      b_MaintainRequisition =(B_MaintainRequisition)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_MaintainRequisition == null)
      {
        b_MaintainRequisition = new B_MaintainRequisition();
        LoginBean loginBean = LoginBean.getInstance(request);
        b_MaintainRequisition.filialeID = loginBean.getFirstDeptID();//分公司id
        b_MaintainRequisition.creator=loginBean.getUserName();//制单人
        b_MaintainRequisition.user=loginBean.getUser();//登陆用户
        b_MaintainRequisition.loginID=loginBean.getUserID();//操作员ID--》登陆用户id
        b_MaintainRequisition.loginDept = loginBean.getDeptID();//登陆用户部门
        b_MaintainRequisition.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        session.setAttribute(beanName, b_MaintainRequisition);
      }
    }
    return b_MaintainRequisition;
  }
  /**
   * 构造函数
   */
  public B_MaintainRequisition()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  public  HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "sb_maintainapply", "sb_maintainapply");//查询得到数据库中配置的字段
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsMasterTable, Master_STRUT_SQL);
    setDataSetProperty(dsDetailTable, Detail_STRUT_SQL);
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"applydate"}, new boolean[]{true}, null, 0));
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"maintainapplyID"}, new String[]{"s_sb_maintainApply"}));
    //单据号自动编号
   // dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"maintainapplyno"}, new String[]{"SELECT pck_base.billNextCode('sb_maintainapply','maintainapplyno','') from dual"}));

    dsDetailTable.setSort(new SortDescriptor("", new String[]{"maiApplyDetailID"}, new boolean[]{false}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"maiApplyDetailID"}, new String[]{"s_sb_applyDetail"}));

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
      detailRow.put("cpid", rowInfo.get("cpid_"+i));                    //产品ID
      detailRow.put("content", rowInfo.get("content_"+i));              //内容
      detailRow.put("memo", rowInfo.get("memo_"+i));                    //备注
      detailRow.put("excepReasonID", rowInfo.get("excepReasonID_"+i));  //故障原因ID
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
    String SQL = isMasterAdd ? "-1" : maintainapplyID;
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
        maintainapplyID = dsMasterTable.getValue("maintainapplyID");
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
      String deptid = m_RowInfo.get("deptid");                     //部门id
      String equipmentid=m_RowInfo.get("equipmentid");              //设备ID
      String personid=m_RowInfo.get("personid");                     //人员ID
      String applydate = m_RowInfo.get("applydate");                 //申请日期
      String bill_sort = m_RowInfo.get("bill_sort");               //单据类别
      String approverID = m_RowInfo.get("approverID");            //审批人
      String state_desc = m_RowInfo.get("state_desc");            //状态描述
      String createDate = m_RowInfo.get("createDate");            //制单日期
      String creatorID = m_RowInfo.get("creatorID");              //制单人ID
      String creator = m_RowInfo.get("creator");                  //制单人
      String buy_cause = m_RowInfo.get("buy_cause");               //原因
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
        data.setMessage(showJavaScript("alert('请选择申请人！');"));
        return;
      }
      if(bill_sort.equals(""))
     {
      data.setMessage(showJavaScript("alert('请选择单据类型！');"));
      return;
      }
      EngineDataSet ds = getMasterTable();
      if(!isMasterAdd)
        ds.goToInternalRow(mastereditRow);
      String maintainapplyID = null;
      String drawcode = null;
      if(isMasterAdd){
        ds.insertRow(false);
        maintainapplyID = dataSetProvider.getSequence("s_sb_maintainapply");
        ds.setValue("maintainapplyID", maintainapplyID);
        if(bill_sort.equals("0"))//维修申请
        {
          drawcode = dataSetProvider.getSequence("SELECT pck_base.billNextCode('sb_maintainapply','maintainapplyno','a') from dual");
        }
        else//保养申请
        {
          drawcode = dataSetProvider.getSequence("SELECT pck_base.billNextCode('sb_maintainapply','maintainapplyno','b') from dual");
        }
      }
      ds.setValue("maintainapplyno",drawcode);
      ds.setValue("deptid",deptid);
      ds.setValue("equipmentid", equipmentid);
      ds.setValue("personid", personid);
      ds.setValue("applydate", applydate);
      ds.setValue("bill_sort", bill_sort);
      ds.setValue("state", "0");                 //状态
      ds.setValue("approverID", approverID);
      ds.setValue("state_desc", state_desc);
      ds.setValue("createDate", createDate);
      ds.setValue("creatorID", creatorID);
      ds.setValue("creator", creator);
      ds.setValue("filialeID", filialeID);
      ds.setValue("buy_cause",buy_cause);
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        detail.setValue("maintainapplyID", maintainapplyID);
        detail.setValue("cpid", detailrow.get("cpid"));                    //产品ID
        detail.setValue("content", detailrow.get("content"));              //内容
        detail.setValue("memo", detailrow.get("memo"));                    //备注
        detail.setValue("excepReasonID", detailrow.get("excepReasonID"));  //故障原因ID
        detail.post();
        detail.next();
      }
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_EQUIPMENT_EXCEPTIONREASON);//同步刷新数据
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
      String maintainapplyID = dsMasterTable.getValue("maintainapplyID");
      detail.insertRow(false);
      detail.setValue("maintainapplyID", isMasterAdd ? "-1" : maintainapplyID);
      detail.post();
      d_RowInfos.add(new RowMap());
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
      maintainapplyID = dsMasterTable.getValue("maintainapplyID");
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
      String content = dsMasterTable.getValue("maintainapplyNO");
      String deptid = dsMasterTable.getValue("deptid");//下达车间
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "maintainApply", content, deptid);
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
      maintainapplyID = dsMasterTable.getValue("maintainapplyID");
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
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "maintainApply");
    }
  }
  ///////////
  class putEquipmentid implements Obactioner{
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception{
      m_RowInfo.put("equipmentid", data.getParameter("equipmentid"));
    }
  }
}

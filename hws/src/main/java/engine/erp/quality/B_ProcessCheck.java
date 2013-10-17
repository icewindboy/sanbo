package engine.erp.quality;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.html.*;
import engine.common.*;
import engine.project.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.*;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import engine.html.HtmlTableProducer;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 质量管理--镀铝纸生产过程检验</p>
 * <p>Description: 质量管理--镀铝纸生产过程检验</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */
public final class B_ProcessCheck extends BaseAction implements Operate
{
  /**
   * 提取所有信息的SQL语句
   */
  private static final String Master_Quality_SQL = "SELECT * FROM zl_processcheck where ? ?";

  private static final String Master_STRUT_SQL = "SELECT * FROM zl_processcheck where 1<>1";
  private static final String Detail_STRUT_SQL="SELECT * FROM zl_processdetail where 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM zl_processdetail WHERE processcheckID='?' ";
 //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM zl_processcheck WHERE processcheckID='?'";
 /**
  * 建立数据集
  */
 private EngineDataSet dsMasterTable = new EngineDataSet();//主表
 private EngineDataSet dsDetailTable = new EngineDataSet();//从表

 public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "ZL_CHEMICAL");
 public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "ZL_CHEMICALDETAIL");
 /**
  * 用于定位数据集
  */
 private EngineRow locateRow = null;
 /**
  * 是否在添加状态
  */
 public  boolean isMasterAdd = true;
 public  boolean isDetailAdd = false;   // 从表是否在添加状态
 public  boolean isApprove = false;     //是否在审批状态

 private RowMap     m_RowInfo  = new RowMap(); //主表添加行或修改行的引用
 private ArrayList  d_RowInfos = null;         //从表多行记录的引用
 private long  mastereditRow = -1;               //保存主表修改操作的行记录指针
 /**
  * 点击返回按钮的URL
  */
 public  String retuUrl = null;
 //---------------------
 public  String loginID = null;     //登录员工的ID
 public  String loginName = "";     //登录员工的姓名-->制单人
 private User user = null;          //登陆用户（设置用户部门权限）
 public  String loginDept = null;   //登录员工的部门
 private String creatorID =null;    //操作员ID
 private String creator =null;      //操作员
 private String filialeid = null;   //分公司ID
 private String processcheckID=null;
 public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
 public  static final String CANCLE_APPROVE = null;//取消审批
 public  static final String SHOW_DETAIL = "10001";
 /**
  * 得到收发单据信息的实例
  * @param request jsp请求
  * @param isApproveStat 是否在审批状态
  * @return 返回收发单据信息的实例
  */
 public static B_ProcessCheck getInstance(HttpServletRequest request)
 {
   B_ProcessCheck b_ProcessCheckBean = null;
   HttpSession session = request.getSession(true);
   synchronized (session)
   {
     String beanName = "ProcessCheckBean";
     b_ProcessCheckBean =(B_ProcessCheck)session.getAttribute(beanName);
     //判断该session是否有该bean的实例
     if(b_ProcessCheckBean == null)
     {
       //引用LoginBean
       b_ProcessCheckBean = new B_ProcessCheck();
       LoginBean loginBean = LoginBean.getInstance(request);
       b_ProcessCheckBean.loginID=loginBean.getUserID();
       b_ProcessCheckBean.loginName=loginBean.getUserName();
       b_ProcessCheckBean.filialeid=loginBean.getFirstDeptID();
       b_ProcessCheckBean.loginDept = loginBean.getDeptID();
       b_ProcessCheckBean.user = loginBean.getUser();
       b_ProcessCheckBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
       session.setAttribute(beanName, b_ProcessCheckBean);
     }
   }
   return b_ProcessCheckBean;
  }
  /**
   * 构造函数
   */
 public B_ProcessCheck()
  {
    try {
     jbInit();
   }
   catch (Exception ex) {
     log.error("jbInit", ex);
    }
  }
 public  HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "zl_processcheck", "zl_processcheck");//查询得到数据库中配置的字段
 /**
  * 初始化函数
  * @throws Exception 异常信息
  */
 protected void jbInit() throws Exception
 {
   setDataSetProperty(dsMasterTable, Master_STRUT_SQL);
   setDataSetProperty(dsDetailTable, Detail_STRUT_SQL);
   dsMasterTable.setSort(new SortDescriptor("", new String[]{"processcheckID"}, new boolean[]{false}, null, 0));
   dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"processcheckID"}, new String[]{"S_ZL_PROCESSCHECK"}));
   dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"processcheckno"}, new String[]{"SELECT pck_base.billNextCode('zl_processcheck','processcheckno','') from dual"}));

   dsDetailTable.setSort(new SortDescriptor("", new String[]{"processdetailid"}, new boolean[]{false}, null, 0));
   dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"processdetailid"}, new String[]{"S_ZL_PROCESSDETAIL"}));

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
  //----Implementation of the BaseAction abstract class
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
  private final void initRowInfo(boolean isMaster,boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    //是否是主表
    if(isMaster){
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();
      if(!isAdd)
        m_RowInfo.put(getMasterTable());
      else
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        m_RowInfo.put("createDate", today);//制单日期
        m_RowInfo.put("creator", loginName);//制单人
        m_RowInfo.put("creatorID", loginID);//制单人id
        m_RowInfo.put("filialeid",filialeid);//分公司ID
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
      detailRow.put("personid", rowInfo.get("personid_"+i));              //人员ID
      detailRow.put("checkItemID", rowInfo.get("checkItemID_"+i));        //检验项目ID
      detailRow.put("procedureName", rowInfo.get("procedureName_"+i));    //工序名称
      detailRow.put("serial_num",rowInfo.get("serial_num_"+i));           //生产编号
      detailRow.put("produce_date", rowInfo.get("produce_date_"+i));      //生产日期
      detailRow.put("check_date", rowInfo.get("check_date_"+i));          //抽检日期
      detailRow.put("checkResult", rowInfo.get("checkResult_"+i));        //检测结果
      detailRow.put("check_verdict", rowInfo.get("check_verdict_"+i));    //检验结论
      detailRow.put("remark", rowInfo.get("remark_"+i));                  //备注

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
  public final void openDetailTable(boolean isAdd)
  {
    String SQL = isAdd ? "-1" : processcheckID;
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
    * 触发初始化操作
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
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isMasterAdd; }
  /**
   * 得到选中的行的行数
   * @return 若返回-1，表示没有选中的行
   */
  public final int getSelectedRow()
  {
    if(mastereditRow < 0)
      return -1;

    dsMasterTable.goToInternalRow(mastereditRow);
    return dsMasterTable.getRow();
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
      processcheckID = dsMasterTable.getValue("processcheckID");
    }
    else{//打开从表
      isDetailAdd = true;
    }
    synchronized(dsDetailTable){
      openDetailTable(isMasterAdd);
    }
    initRowInfo(true, isMasterAdd, true);
    initRowInfo(false, isMasterAdd, true);
    //data.setMessage(showJavaScript("alert('aaaaaaaa');"));
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
    String cpId = m_RowInfo.get("cpId");                         //产品ID
    String dmsxid=m_RowInfo.get("dmsxid");                       //规格属性id
    String plantid=m_RowInfo.get("plantid");                     //车间ID
    String checknum = m_RowInfo.get("checknum");                 //检验数量
    String rejectnum = m_RowInfo.get("rejectnum");               //不合格数
    String check_verdict = m_RowInfo.get("check_verdict");      //检验结论
    String approverID = m_RowInfo.get("approverID");            //审批人
    String state_desc = m_RowInfo.get("state_desc");            //状态描述
    String createDate = m_RowInfo.get("createDate");            //制单日期
    String creatorID = m_RowInfo.get("creatorID");              //制单人ID
    String creator = m_RowInfo.get("creator");                  //制单人
    String remark = m_RowInfo.get("remark");                    //备注
    String deptid = m_RowInfo.get("deptid");                    //部门id
    if(cpId.equals(""))
    {
      data.setMessage(showJavaScript("alert('请选择产品！');"));
      return;
    }
    if(checknum.equals("")) checknum="0";
    if(rejectnum.equals(""))rejectnum="0";
    if(Integer.parseInt(rejectnum)>Integer.parseInt(checknum))
    {
      data.setMessage(showJavaScript("alert('不合格数不能大于检验数量！');"));
      return;
    }
    EngineDataSet ds = getMasterTable();
    if(!isMasterAdd)
     ds.goToInternalRow(mastereditRow);
     String processcheckID = null;
    if(isMasterAdd){
      ds.insertRow(false);
      processcheckID = dataSetProvider.getSequence("S_ZL_PROCESSCHECK");
      ds.setValue("processcheckID", processcheckID);
    }
    ds.setValue("cpId", cpId);
    ds.setValue("dmsxid", dmsxid);
    ds.setValue("plantid", plantid);
    ds.setValue("checknum", checknum);
    ds.setValue("rejectnum", rejectnum);
    ds.setValue("state", "0");
    ds.setValue("approverID", approverID);
    ds.setValue("state_desc", state_desc);
    ds.setValue("createDate", createDate);
    ds.setValue("creatorID", creatorID);
    ds.setValue("creator", creator);
    ds.setValue("filialeID", filialeid);
    ds.setValue("check_verdict", check_verdict);
    ds.setValue("remark", remark);
    ds.setValue("deptid",deptid);
    //保存从表的数据
    RowMap detailrow = null;
    //BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);
    EngineDataSet detail = getDetailTable();
    detail.first();
    for(int i=0; i<detail.getRowCount(); i++)
    {
      detailrow = (RowMap)d_RowInfos.get(i);
      //新添的记录
      detail.setValue("processcheckID", processcheckID);                 //镀铝纸生产过程检验ID
      detail.setValue("personid", detailrow.get("personid"));            //人员ID
      detail.setValue("checkItemID", detailrow.get("checkItemID"));      //检验项目ID
      detail.setValue("procedureName", detailrow.get("procedureName"));  //工序名称
      detail.setValue("serial_num", detailrow.get("serial_num"));        //生产编号
      detail.setValue("produce_date", detailrow.get("produce_date"));    //生产日期
      detail.setValue("check_date", detailrow.get("check_date"));        //抽检日期
      detail.setValue("checkResult", detailrow.get("checkResult"));      //检测结果
      detail.setValue("check_verdict", detailrow.get("check_verdict"));  //检验结论
      detail.setValue("remark", detailrow.get("remark"));                //备注
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
   * 触发删除操作
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
     String processcheckID = dsMasterTable.getValue("processcheckID");
     detail.insertRow(false);
     detail.setValue("processcheckID", isMasterAdd ? "-1" : processcheckID);
     detail.post();
     d_RowInfos.add(new RowMap());
   }
  }
   /*
  *从表删除操作
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
    * 显示从表的列表信息
    */
 class ShowDetail implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
       mastereditRow = dsMasterTable.getInternalRow();
       processcheckID = dsMasterTable.getValue("processcheckID");
       //打开从表
       openDetailTable(false);
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
     processcheckID = dsMasterTable.getValue("processcheckID");
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
     ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
     String content = dsMasterTable.getValue("processcheckNo");
     String deptid = dsMasterTable.getValue("deptid");//下达车间
     approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "processcheck", content, deptid);
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
    approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "processcheck");
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
}

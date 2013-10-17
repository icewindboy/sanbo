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
import engine.common.*;
import engine.project.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import engine.html.HtmlTableProducer;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 质量管理--辅料验收报告单</p>
 * <p>Description: 质量管理--辅料验收报告单</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */

public final class B_AdminicleCheck extends BaseAction implements Operate
{
  /**
   * 提取收发单据列表所有信息的SQL语句
   */
  private static final String Quality_SQL = "SELECT * FROM ZL_ADMINICLECHECK where ? ?";
  private static final String Quality = "SELECT * FROM ZL_ADMINICLECHECK where 1<>1";
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM ZL_ADMINICLECHECK WHERE adminiclecheckID='?'";
  /**
   * 建立收发单据列表信息的数据集
   */
  private EngineDataSet dsAdminicleCheck = new EngineDataSet();
  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;
  /**
   * 保存用户输入的信息
   */
  private RowMap rowInfo = new RowMap();
  /**
   * 是否在添加状态
   */
  public  boolean isAdd = true;
  /**
   * 保存修改操作的行记录指针
   */
  private long  editrow = 0;
  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;
  //---------------------
  public  String loginID = null;   //登录员工的ID
  private User user = null;//登陆用户（设置用户部门权限）
  public  String loginDept = ""; //登录员工的部门
  private String creatorID =null;//操作员ID
  private String creator =null;//操作员

  private String filialeID = null;  //分公司ID

  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public  static final String CANCLE_APPROVE = "10081";//取消审批
  public  boolean isApprove = false;    //是否在审批状态
  /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_AdminicleCheck getInstance(HttpServletRequest request)
  {
    B_AdminicleCheck b_AdminicleCheckBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "AdminicleCheckBean";
      b_AdminicleCheckBean =(B_AdminicleCheck)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_AdminicleCheckBean == null)
      {
        b_AdminicleCheckBean = new B_AdminicleCheck();
        LoginBean loginBean = LoginBean.getInstance(request);
        b_AdminicleCheckBean.filialeID = loginBean.getFirstDeptID();
        b_AdminicleCheckBean.creator=loginBean.getUserName();//制单人
        b_AdminicleCheckBean.user=loginBean.getUser();//登陆用户
        b_AdminicleCheckBean.loginDept = loginBean.getDeptID();
        b_AdminicleCheckBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        b_AdminicleCheckBean.loginID=loginBean.getUserID();//操作员ID

        session.setAttribute(beanName, b_AdminicleCheckBean);

      }
    }
    return b_AdminicleCheckBean;
  }
  /**
   * 构造函数
   */
  private B_AdminicleCheck()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  public  HtmlTableProducer table = new HtmlTableProducer(dsAdminicleCheck, "zl_adminiclecheck", "zl_adminiclecheck");//查询得到数据库中配置的字段
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsAdminicleCheck, Quality);
    dsAdminicleCheck.setSort(new SortDescriptor("", new String[]{"adminiclecheckID"}, new boolean[]{false}, null, 0));
    dsAdminicleCheck.setSequence(new SequenceDescriptor(new String[]{"adminiclecheckID"}, new String[]{"S_ZL_ADMINICLECHECK"}));
    //单据号
    dsAdminicleCheck.setSequence(new SequenceDescriptor(new String[]{"adminiclecheckno"}, new String[]{"SELECT pck_base.billNextCode('zl_adminiclecheck','adminiclecheckno','') from dual"}));

    B_WorkProcedure_Add_Edit add_edit = new B_WorkProcedure_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(ADD),  add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_WorkProcedure_Post());
    addObactioner(String.valueOf(POST_CONTINUE),new B_WorkProcedure_Post());
    addObactioner(String.valueOf(DEL), new B_WorkProcedure_Delete());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCLE_APPROVE), new Cancle_Approve());
    addObactioner(String.valueOf(APPROVE), new Approve());
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
      if(dsAdminicleCheck.isOpen() && dsAdminicleCheck.changesPending())
        dsAdminicleCheck.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean IsAdd() { return isAdd; }
  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsAdminicleCheck != null){
      dsAdminicleCheck.close();
      dsAdminicleCheck = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
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
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    if(!isAdd)
      rowInfo.put(getOneTable());
    else
    {
      //是否时添加操作
      if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      rowInfo.put("createDate", today);//制单日期
      rowInfo.put("creator", creator);//操作员
      rowInfo.put("filialeID",filialeID);//filialeID分公司ID
      rowInfo.put("creatorID", loginID);
      rowInfo.put("deptid", loginDept);
    }
  }
  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsAdminicleCheck.isOpen())
      dsAdminicleCheck.open();
    return dsAdminicleCheck;
  }
  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_WorkProcedure_Init implements Obactioner
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
      String SQL=combineSQL(Quality_SQL,"?",new String[]{user.getHandleDeptValue("deptid")});
      dsAdminicleCheck.setQueryString(SQL);
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class B_WorkProcedure_Add_Edit implements Obactioner
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
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsAdminicleCheck.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsAdminicleCheck.getInternalRow();
      }
      initRowInfo(isAdd, true);
      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
   * 保存操作的触发类
   */
  class B_WorkProcedure_Post implements Obactioner
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
      EngineDataSet ds = getOneTable();
      //校验数据
      rowInfo.put(data.getRequest());
      String cpId = rowInfo.get("cpId");                        //产品ID
      String deptid = rowInfo.get("deptid");                    //部门id
      String dwtxId=rowInfo.get("dwtxId");                      //往来单位ID
      String goods_date = rowInfo.get("goods_date");            //到货日期
      String tot_num = rowInfo.get("tot_num");                  //总数量
      String facies = rowInfo.get("facies");                    //外观
      String sample_number = rowInfo.get("sample_number");      //抽样数
      String tryout_date = rowInfo.get("tryout_date");          //试用日期
      //String state = rowInfo.get("state");                      //状态
      String approverID = rowInfo.get("approverID");            //审批人
      String state_desc = rowInfo.get("state_desc");            //状态描述
      String remark = rowInfo.get("remark");                    //备注
      String createDate = rowInfo.get("createDate");            //制单日期
      String creatorID = rowInfo.get("creatorID");              //制单人ID
      String creator = rowInfo.get("creator");                  //制单人
     // String filialeID = rowInfo.get("filialeID");              //分公司ID
      String use_circs = rowInfo.get("use_circs");              //试用情况
      String dmsxID=rowInfo.get("dmsxID");                      //物资规格属性ID
      String check_verdict=rowInfo.get("check_verdict");              //试用结论
      String sxz=rowInfo.get("sxz");                               //规格属性

      if(dwtxId.equals("")){
        data.setMessage(showJavaScript("alert('请选择供应商！');"));
        return;
      }
      if(cpId.equals("")){
        data.setMessage(showJavaScript("alert('请选择产品！');"));
        return;
      }
      if(goods_date.equals("")){
        data.setMessage(showJavaScript("alert('请选择来货日期！');"));
        return;
      }
      if(sample_number.equals(""))sample_number="0";
      if(tot_num.equals(""))tot_num="0";
      if(Integer.parseInt(sample_number)>Integer.parseInt(tot_num)){
        data.setMessage(showJavaScript("alert('抽样数量不能大于总数量！');"));
        return;
      }
      if(deptid.equals("")){
        data.setMessage(showJavaScript("alert('请选择试用部门！');"));
        return;
      }
      if(check_verdict.equals("")){
        data.setMessage(showJavaScript("alert('请选择试用结论！');"));
        return;
      }
      if(!isAdd)
        ds.goToInternalRow(editrow);
      String adminiclecheckID = null;
      if(isAdd)
      {
        ds.insertRow(false);
        adminiclecheckID = dataSetProvider.getSequence("S_ZL_ADMINICLECHECK");
        ds.setValue("adminiclecheckID", adminiclecheckID);
      }
      ds.setValue("cpId", cpId);
      ds.setValue("dmsxID",dmsxID);
      ds.setValue("deptid", deptid);
      ds.setValue("dwtxId", dwtxId);
      ds.setValue("goods_date", goods_date);
      ds.setValue("tot_num", tot_num);
      ds.setValue("facies", facies);
      ds.setValue("sample_number", sample_number);
      ds.setValue("tryout_date", tryout_date);
      ds.setValue("state", "0");
      ds.setValue("approverID", approverID);
      ds.setValue("state_desc", state_desc);
      ds.setValue("use_circs", use_circs);
      ds.setValue("remark", remark);
      ds.setValue("createDate", createDate);
      ds.setValue("creatorID", creatorID);
      ds.setValue("creator", creator);
      ds.setValue("filialeID", filialeID);
      ds.setValue("check_verdict",check_verdict);////试用结论
      ds.setValue("sxz",sxz);//规格属性
      ds.post();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_QUALITY_TOOLTYPE);//同步刷新数据
      if(String.valueOf(POST_CONTINUE).equals(action)){
        isAdd = true;
        //ds.empty();
        initRowInfo(true, true);//重新初始化从表的各行信息
      }
      else
      data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   * 删除操作的触发类
   */
  class B_WorkProcedure_Delete implements Obactioner
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
      EngineDataSet ds = getOneTable();
      ds.goToInternalRow(editrow);
      ds.deleteRow();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_QUALITY_TOOLTYPE);//同步刷新数据
      data.setMessage(showJavaScript("backList();"));
    }
  }
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String tempSQL=null;
      table.getWhereInfo().setWhereValues(data.getRequest());
      tempSQL = table.getWhereInfo().getWhereQuery();
      String SQL=combineSQL(Quality_SQL,"?",new String[]{user.getHandleDeptValue("deptid")});
      if(tempSQL.length() > 0) SQL=SQL+" and "+tempSQL;
      //else SQL=Quality_SQL;
      dsAdminicleCheck.setQueryString(SQL);
      dsAdminicleCheck.setRowMax(null);
    }
  }
  /**
   * 添加到审核列表的操作类
   */
  class Add_Approve implements Obactioner
 {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsAdminicleCheck.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsAdminicleCheck.getValue("adminiclecheckNo");
      approve.putAproveList(dsAdminicleCheck, dsAdminicleCheck.getRow(), "adminicle_check", content,dsAdminicleCheck.getValue("deptid"));
    }
  }
  /**
   * 取消审批触发操作
   */
  class Cancle_Approve implements Obactioner
 {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsAdminicleCheck.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsAdminicleCheck,dsAdminicleCheck.getRow(), "adminicle_check");
    }
  }
  /*审批操作*/
  class Approve implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
    HttpServletRequest request = data.getRequest();
    String id = data.getParameter("id");
    String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
    dsAdminicleCheck.setQueryString(sql);
    if(dsAdminicleCheck.isOpen()){
      dsAdminicleCheck.readyRefresh();
      dsAdminicleCheck.refresh();
    }
    else
      dsAdminicleCheck.open();
      isApprove = true;
      initRowInfo(false, true);
    }
  }
}

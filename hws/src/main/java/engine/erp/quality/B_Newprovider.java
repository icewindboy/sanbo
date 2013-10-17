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
 * <p>Title: 质量管理--新供方产品质量评定表</p>
 * <p>Description: 质量管理--新供方产品质量评定表</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */
public final class B_Newprovider extends BaseAction implements Operate
{
  /**
   * 提取所有信息的SQL语句
   */
  private static final String Quality_SQL = "SELECT * FROM zl_newprovider where ? ?";
  private static final String Quality = "SELECT * FROM zl_newprovider where 1<>1";
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM zl_newprovider WHERE newproviderID='?'";
  /**
   * 建立数据集
   */
  private EngineDataSet dsnewprovider = new EngineDataSet();
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
  private User user = null;        //登陆用户（设置用户部门权限）
  public  String loginDept = null;   //登录员工的部门
  private String creatorID =null;  //操作员ID
  private String creator =null;    //操作员

  private String filialeID = null;  //分公司ID

  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public  static final String CANCLE_APPROVE = null;//取消审批
  public  boolean isApprove = false;    //是否在审批状态
  /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_Newprovider getInstance(HttpServletRequest request)
  {
    B_Newprovider b_NewproviderBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "NewproviderBean";
      b_NewproviderBean =(B_Newprovider)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_NewproviderBean == null)
      {
        b_NewproviderBean = new B_Newprovider();
        LoginBean loginBean = LoginBean.getInstance(request);
        b_NewproviderBean.filialeID = loginBean.getFirstDeptID();//分公司id
        b_NewproviderBean.creator=loginBean.getUserName();//制单人
        b_NewproviderBean.user=loginBean.getUser();//登陆用户
        b_NewproviderBean.loginDept = loginBean.getDeptID();//登陆用户部门
        b_NewproviderBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        b_NewproviderBean.loginID=loginBean.getUserID();//操作员ID--》登陆用户id
        session.setAttribute(beanName, b_NewproviderBean);
      }
    }
    return b_NewproviderBean;
  }
  /**
   * 构造函数
   */
  public B_Newprovider()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  public  HtmlTableProducer table = new HtmlTableProducer(dsnewprovider, "zl_newprovider", "zl_newprovider");//查询得到数据库中配置的字段
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsnewprovider, Quality);
    dsnewprovider.setSort(new SortDescriptor("", new String[]{"newproviderID"}, new boolean[]{false}, null, 0));
    dsnewprovider.setSequence(new SequenceDescriptor(new String[]{"newproviderID"}, new String[]{"S_ZL_NEWPROVIDER"}));
    //添加操作的触发对象
    dsnewprovider.setSequence(new SequenceDescriptor(new String[]{"newproviderNo"}, new String[]{"SELECT pck_base.billNextCode('zl_newprovider','newproviderNo','') from dual"}));

    B_WorkProcedure_Add_Edit add_edit = new B_WorkProcedure_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(ADD),  add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_WorkProcedure_Post());
    addObactioner(String.valueOf(POST_CONTINUE), new B_WorkProcedure_Post());
    addObactioner(String.valueOf(DEL), new B_WorkProcedure_Delete());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCLE_APPROVE), new Cancle_Approve());
    addObactioner(String.valueOf(APPROVE), new Approve());
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
     if(dsnewprovider.isOpen() && dsnewprovider.changesPending())
       dsnewprovider.reset();
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
  if(dsnewprovider != null){
    dsnewprovider.close();
    dsnewprovider = null;
  }
  log = null;
  rowInfo = null;
  locateRow = null;
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
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd)
      rowInfo.put(getOneTable());
    else
    {
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
    if(!dsnewprovider.isOpen())
      dsnewprovider.open();
    return dsnewprovider;
  }
  /*得到一列的信息*/
  public final RowMap getRowinfo() {   return rowInfo;  }
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
      dsnewprovider.setQueryString(SQL);
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
        dsnewprovider.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsnewprovider.getInternalRow();
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

       String dwtxId=rowInfo.get("dwtxId");                       //往来单位ID
       String standardID=rowInfo.get("standardID");               //检验标准ID
       String deptid = rowInfo.get("deptid");                     //部门id
       String cpId = rowInfo.get("cpId");                         //产品ID
       String dmsxid=rowInfo.get("dmsxid");                        //规格属性
        String get_num = rowInfo.get("get_num");                  //来货数量
       String get_date = rowInfo.get("get_date");                 //到货日期
       String check_num = rowInfo.get("check_num");               //抽检数量
       String check_date = rowInfo.get("check_date");             //抽检日期
       String check_circs = rowInfo.get("check_circs");           //检验情况
       String quality_result = rowInfo.get("quality_result");     //质管部评定结果
       String check_verdict=rowInfo.get("check_verdict");          //检验结论
       String qualityID = rowInfo.get("qualityID");                //质管部签名
       String produce_tryout = rowInfo.get("produce_tryout");      //生产部试用情况
       String produce_result = rowInfo.get("produce_result");      //生产部评定结果
       String produceID = rowInfo.get("produceID");              //生产部签名
       String approverID = rowInfo.get("approverID");            //审批人
       String state_desc = rowInfo.get("state_desc");            //状态描述
       String createDate = rowInfo.get("createDate");            //制单日期
       String creatorID = rowInfo.get("creatorID");              //制单人ID
       String creator = rowInfo.get("creator");                  //制单人
       if(dwtxId.equals("")){
         data.setMessage(showJavaScript("alert('请选择供应商！');"));
         return;
       }
       if(cpId.equals("")){
         data.setMessage(showJavaScript("alert('请选择产品！');"));
         return;
       }
       if(get_num.equals(""))get_num="0";
      if(check_num.equals(""))check_num="0";
      if(Integer.parseInt(check_num)>Integer.parseInt(get_num)){
        data.setMessage(showJavaScript("alert('抽样数量不能大于来货数量！');"));
        return;
      }
       if(!isAdd)
           ds.goToInternalRow(editrow);
       String newproviderID = null;
       if(isAdd)
      {
        ds.insertRow(false);
        newproviderID = dataSetProvider.getSequence("S_ZL_NEWPROVIDER");
        ds.setValue("newproviderID", newproviderID);
      }
       ds.setValue("dwtxId", dwtxId);
       ds.setValue("standardID", standardID);
       ds.setValue("deptid", deptid);
       ds.setValue("cpId", cpId);
       //ds.setValue("adminiclecheckNo", adminiclecheckNo);
       ds.setValue("dmsxid",dmsxid);
       ds.setValue("get_num", get_num);
       ds.setValue("get_date", get_date);
       ds.setValue("check_num", check_num);
       ds.setValue("check_date", check_date);
       ds.setValue("check_circs", check_circs);
       ds.setValue("quality_result",quality_result);
       ds.setValue("check_verdict",check_verdict);//检验结论
       ds.setValue("qualityID", qualityID);
       ds.setValue("produce_result", produce_result);
       ds.setValue("produce_tryout", produce_tryout);
       ds.setValue("produceID", produceID);
       ds.setValue("state", "0");//状态
       ds.setValue("approverID", approverID);
       ds.setValue("approverID", approverID);
       ds.setValue("state_desc", state_desc);
       ds.setValue("createDate", createDate);
       ds.setValue("creatorID", creatorID);
       ds.setValue("creator", creator);
       ds.setValue("filialeID", filialeID);
       ds.post();
       ds.saveChanges();
       LookupBeanFacade.refreshLookup(SysConstant.BEAN_QUALITY_TOOLTYPE);//同步刷新数据
       if(String.valueOf(POST_CONTINUE).equals(action)){
         isAdd = true;
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
       dsnewprovider.setQueryString(SQL);
       dsnewprovider.setRowMax(null);
     }
   }
   /**
    * 添加到审核列表的操作类
    */
   class Add_Approve implements Obactioner
  {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       dsnewprovider.goToRow(Integer.parseInt(data.getParameter("rownum")));
       ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
       String content = dsnewprovider.getValue("newproviderNo");
       approve.putAproveList(dsnewprovider, dsnewprovider.getRow(), "newprovidercheck", content,dsnewprovider.getValue("deptid"));
     }
   }
   /**
    * 取消审批触发操作
    */
   class Cancle_Approve implements Obactioner
  {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       dsnewprovider.goToRow(Integer.parseInt(data.getParameter("rownum")));
       ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
       approve.cancelAprove(dsnewprovider,dsnewprovider.getRow(), "newprovidercheck");
     }
   }
   /*审批操作*/
   class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
     String id = data.getParameter("id");
     String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
     dsnewprovider.setQueryString(sql);
     if(dsnewprovider.isOpen()){
       dsnewprovider.readyRefresh();
       dsnewprovider.refresh();
     }
     else
       dsnewprovider.open();
       isApprove = true;
       initRowInfo(false, true);
     }
  }
}

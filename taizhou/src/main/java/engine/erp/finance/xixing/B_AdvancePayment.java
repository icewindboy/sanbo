package engine.erp.finance.xixing;

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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.borland.dx.dataset.*;
import engine.html.HtmlTableProducer;
/**
 * <p>Title: 采购预付款</p>
 * <p>Description: 采购预付款<</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */

public final class B_AdvancePayment extends BaseAction implements Operate
{
  public static final String WB_ONCHANGE = "1008";
  private LookUp foreignBean =null;
  public String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;  //分公司ID
  private String czy=null;//操作员
  private User user = null;//登陆用户（设置用户部门权限）
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public  String loginId = "";   //登录员工的ID
  public  static final String CANCLE_APPROVE = "10081";//取消审批
  public  static final String COMPLETE       = "11231";//强制完成
  public  static final String REPEAL         = "11581";//审批作废
  public  static final String REPORT         = "2000";//报表追踪触发事件
  public  boolean isApprove = false;     //是否在审批状态
  public boolean isReport = false;
  private String cgyfkid = null;
  private long Row = -1;         //保存主表修改操作的行记录指针
  /**
   * 提取收发单据列表所有信息的SQL语句
   */
  private static final String buyPaymentStruct = "SELECT * FROM cw_cgyfk WHERE 1<>1";
  private static final String Quality_SQL = "SELECT * FROM cw_cgyfk WHERE ? ? ORDER BY yfkbh DESC";//
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM cw_cgyfk WHERE cgyfkid='?'";
  /**
   * 建立收发单据列表信息的数据集
   */
  private EngineDataSet dsWorkCheck = new EngineDataSet();
  /**
   * 用于定位数据集
   *
   */
  public  HtmlTableProducer table = new HtmlTableProducer(dsWorkCheck, "cw_cgyfk", "cw_cgyfk");//查询得到数据库中配置的字段
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

  /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_AdvancePayment getInstance(HttpServletRequest request)
  {
    B_AdvancePayment advancePayment = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "AdvancePaymentBean";
      advancePayment = (B_AdvancePayment)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(advancePayment == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        advancePayment = new B_AdvancePayment();
        advancePayment.fgsid = loginBean.getFirstDeptID();
        advancePayment.czy=loginBean.getUserName();
        advancePayment.user = loginBean.getUser();
        advancePayment.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        advancePayment.loginId = loginBean.getUserID();
        session.setAttribute(beanName, advancePayment);
        advancePayment.priceFormat  = loginBean.getPriceFormat();
      }
    }
    return advancePayment;
  }
  /**
   * 构造函数
   */
  private B_AdvancePayment()
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
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsWorkCheck, buyPaymentStruct);
    dsWorkCheck.setSequence(new SequenceDescriptor(new String[]{"yfkbh"}, new String[]{"SELECT pck_base.billNextCode('cw_cgyfk','yfkbh') from dual"}));
    dsWorkCheck.setSort(new SortDescriptor("", new String[]{"yfkbh"}, new boolean[]{true}, null, 0));
    dsWorkCheck.setSequence(new SequenceDescriptor(new String[]{"cgyfkID"}, new String[]{"S_cw_cgyfk"}));
    //添加操作的触发对象
    B_WorkProcedure_Add_Edit add_edit = new B_WorkProcedure_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_WorkProcedure_Post());
    addObactioner(String.valueOf(DEL), new B_WorkProcedure_Delete());
    addObactioner(String.valueOf(WB_ONCHANGE), new Wb_Onchange());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCLE_APPROVE), new Cancle_Approve());
    addObactioner(String.valueOf(COMPLETE), new Complete());
    addObactioner(String.valueOf(REPEAL), new Repeal());
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
    addObactioner(String.valueOf(REPORT), new Approve());
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
      String opearate = request.getParameter(OPERATE_KEY);
      //System.out.println(opearate);
      if(opearate != null && opearate.trim().length() > 0)
      {
        RunData data = notifyObactioners(opearate, request, response, null);
        if(data == null)
          return showMessage("无效操作", false);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsWorkCheck.isOpen() && dsWorkCheck.changesPending())
        dsWorkCheck.reset();
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
    if(dsWorkCheck != null){
      dsWorkCheck.close();
      dsWorkCheck = null;
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
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd){
      rowInfo.put(getOneTable());
    }
    else{
      Date startDate = new Date();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
      rowInfo.put("czrq",today);
      rowInfo.put("fgsid",fgsid);//fgsid分公司ID
      rowInfo.put("czy",czy);//操作员
      rowInfo.put("czyid",loginId);//操作员id
    }
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsWorkCheck.isOpen())
      dsWorkCheck.open();
    return dsWorkCheck;
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
      isReport = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      String SQL=combineSQL(Quality_SQL, "?", new String[]{user.getHandleDeptValue("deptid")});
      //SQL =SQL+ " AND zt<>8 AND zt<>4";
      dsWorkCheck.setQueryString(SQL);//部门权限
      //dsWorkCheck.setRowMax(null);
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
        dsWorkCheck.goToRow(Integer.parseInt(data.getParameter("rownum")));
        Row = dsWorkCheck.getInternalRow();
        editrow = dsWorkCheck.getInternalRow();
      }
      initRowInfo(isAdd, true);
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
      String wbid = rowInfo.get("wbid");//外币
      String dwtxid = rowInfo.get("dwtxid");//往来单位
      String deptid=rowInfo.get("deptid");//部门
      String jsfsid=rowInfo.get("jsfsid");//结算方式
      String personid=rowInfo.get("personid");//人员
      String yfkbh=rowInfo.get("yfkbh");//预付款编号
      String yflx=rowInfo.get("yflx");//预付类型
      String yfrq=rowInfo.get("yfrq");//预付日期
      String hl=rowInfo.get("hl");//汇率
      String je=rowInfo.get("je");//金额
      String ybje=rowInfo.get("ybje");//外币金额
      String zh=rowInfo.get("zh");//帐号
      String yh=rowInfo.get("yh");//银行
      String yfms=rowInfo.get("yfms");//预付描述
      String ztms=rowInfo.get("ztms");//状态描述
      String fgsid=rowInfo.get("fgsid");//分公司
      String czrq=rowInfo.get("czrq");//操作日期
      String czy=rowInfo.get("czy");//操作员
      String czyid=rowInfo.get("czyid");//操作员id
      if(deptid.equals("")){
        data.setMessage(showJavaScript("alert('部门不能为空！');"));
        return;
      }
      //System.out.print("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
      if(!isAdd)
        ds.goToInternalRow(editrow);
      //ds.setValue("zt", wbid);
      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("cgyfkID", "-1");
        ds.setValue("zt", "0");
      }
      ds.setValue("wbid", wbid);//外币
      ds.setValue("dwtxid", dwtxid);//往来单位
      ds.setValue("deptid", deptid);//部门
      ds.setValue("jsfsid", jsfsid);//结算方式
      ds.setValue("personid", personid);//人员
      ds.setValue("yfkbh", yfkbh);//预付款编号
      ds.setValue("yflx", yflx);//预付类型
      ds.setValue("yfrq", yfrq);//预付日期
      ds.setValue("hl", hl);//汇率
      ds.setValue("je", je);//金额
      ds.setValue("ybje", ybje);//外币金额
      ds.setValue("zh", zh);//帐号
      ds.setValue("yh", yh);//银行
      ds.setValue("yfms", yfms);//预付描述
      ds.setValue("ztms", ztms);//状态描述
      ds.setValue("fgsid", fgsid);//分公司
      ds.setValue("czrq", czrq);//操作日期
      ds.setValue("czy", czy);//操作员
      ds.setValue("czyid", czyid);//操作员
      ds.post();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_QUALITY_TOOLTYPE);//同步刷新数据
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
     * @param  arg    触发者对象调用<wbId>notifyObactioners</wbId>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.goToInternalRow(Row);
      ds.deleteRow();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_QUALITY_TOOLTYPE);//同步刷新数据
      data.setMessage(showJavaScript("backList();"));
    }
  }
  class Wb_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      rowInfo.put(req);

      String wbid = rowInfo.get("wbid");
      String je = rowInfo.get("je");
      RowMap foreignRow = getForeignBean(req).getLookupRow(wbid);
      String hl = foreignRow.get("hl");
      double curhl = hl.length()>0 ? Double.parseDouble(hl) : 0 ;
      double curbj = je.length()>0 ? Double.parseDouble(je) : 0;
      double ybje = curhl==0 ? 0 : curbj/curhl;
      rowInfo.put("wbid",wbid);
      rowInfo.put("hl",hl);
      rowInfo.put("ybje", formatNumber(String.valueOf(ybje), priceFormat) );
    }
  }
  public LookUp getForeignBean(HttpServletRequest req)
  {
    if(foreignBean == null)
      foreignBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_FOREIGN_CURRENCY);
    return foreignBean;
  }
  /**
   * 添加到审核列表的操作类
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsWorkCheck.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsWorkCheck.getValue("yfkbh");
      approve.putAproveList(dsWorkCheck, dsWorkCheck.getRow(), "advance_payment", content,dsWorkCheck.getValue("deptid"));
    }
  }
  /**
   * 取消审批触发操作
   */
  class Cancle_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsWorkCheck.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsWorkCheck,dsWorkCheck.getRow(), "advance_payment");
    }
  }
  /**
   * 强制完成操作触发的类（合同被引用就不显示按钮即不能被完成）
   */
  class Complete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsWorkCheck.goToRow(row);
      dsWorkCheck.setValue("zt", "8");
      dsWorkCheck.post();
      dsWorkCheck.saveChanges();
    }
  }
  class Repeal implements Obactioner//作废
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsWorkCheck.goToRow(row);
      dsWorkCheck.setValue("zt", "4");
      dsWorkCheck.post();
      dsWorkCheck.saveChanges();
    }
  }
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String id=null;
      HttpServletRequest request = data.getRequest();
      if(String.valueOf(REPORT).equals(action))
      {
        isReport=true;
        isApprove = false;
        id=request.getParameter("id");
      }else
      {
        isApprove = true;
        id = data.getParameter("id", "");
       }
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsWorkCheck.setQueryString(sql);
      if(dsWorkCheck.isOpen()){
        dsWorkCheck.readyRefresh();
        dsWorkCheck.refresh();
      }
      else
        dsWorkCheck.open();
      initRowInfo(false, true);
    }
  }
  class Search implements Obactioner
  {
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<cardID >notifyObactioners</cardID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      table.getWhereInfo().setWhereValues(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
      SQL =" and "+SQL;
      SQL=combineSQL(Quality_SQL, "?", new String[]{user.getHandleDeptValue("deptid"),SQL});
      dsWorkCheck.setQueryString(SQL);
      dsWorkCheck.setRowMax(null);
    }
  }
}
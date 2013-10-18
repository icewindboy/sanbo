package engine.erp.person.shengyu;
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
import engine.erp.person.shengyu.ImportZpApply;//引招聘申请


import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;


public final class B_InviteJob extends BaseAction implements Operate
{
  public  static final String CANCER_APPROVE = "10235131";
  public  static final String BDLX_CHANGE = "55555556";
  public  static final String PERSON_ONCHANGE = "55555554";
  public  static final String DEPT_CHANGE = "55555555";//申请部门
  public  static final String DETAIL_APPLY_ADD = "55555557";//引招聘申请
  private static final String MASTER_STRUT_SQL = "SELECT * FROM rl_invite_plan  ";//返回表结构
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM rl_invite_plan_detail where 1<>1 ";//返回表结构
  private static final String MASTER_SQL    = "SELECT * FROM rl_invite_plan WHERE  1=1 ?  ORDER BY plan_ID DESC";
  private static final String DETAIL_SQL    = "SELECT * FROM rl_invite_plan_detail WHERE plan_ID='?' ";//
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM rl_invite_plan WHERE plan_ID='?'";
  private static final String SEARCH_SQL = "SELECT * FROM VW_PERSON_ZGXXBD WHERE 1=1 ? ";

  //private static final String ZPSQ_SQL = "SELECT * FROM rl_invite_apply WHERE 1=1 ? ";//招聘申请数据集




  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsSearchTable = new EngineDataSet();//查询数据集

  //private EngineDataSet ZPSQTable  = new EngineDataSet();//招聘申请数据集




  public  boolean isDetailAdd = false; // 从表是否在添加状态

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "rl_invite_plan");//引入主表页面
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "rl_invite_plan_detail");

  //public  HtmlTableProducer ZPSQProducer = new HtmlTableProducer(ZPSQTable, "rl_invite_apply");//招聘申请数据集
  //public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, SysConstant.TABLE_SALE_ORDER);//"xs_ht"
  //public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, SysConstant.TABLE_SALE_ORDER_GOODS);
  public boolean isMasterAdd = true;    //是否在添加状态
  private long    masterRow = 0;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private LookUp personBean = null;

  //&#$
  public  boolean isApprove = false;     //是否在审批状态

private ImportZpApply buyApplyBean = null; //招聘申请单的bean的引用, 用于提取采购单价

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String filialeID = null;   //分公司ID
  private String plan_ID="";
  public String []state;
  public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
    public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_InviteJob getInstance(HttpServletRequest request)
  {
    B_InviteJob b_InviteJobBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_InviteJobBean";
      b_InviteJobBean = (B_InviteJob)session.getAttribute(beanName);
      if(b_InviteJobBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        b_InviteJobBean = new B_InviteJob();
        b_InviteJobBean.qtyFormat = loginBean.getQtyFormat();
        b_InviteJobBean.priceFormat = loginBean.getPriceFormat();
        b_InviteJobBean.sumFormat = loginBean.getSumFormat();

        b_InviteJobBean.filialeID = loginBean.getFirstDeptID();
        b_InviteJobBean.loginId = loginBean.getUserID();
        b_InviteJobBean.loginName = loginBean.getUserName();
        b_InviteJobBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交

        session.setAttribute(beanName, b_InviteJobBean);
      }
    }
    return b_InviteJobBean;
  }

  /**
   * 构造函数
   */
  private B_InviteJob()
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

    //setDataSetProperty(ZPSQTable, ZPSQ_SQL);// 招聘申请数据集



    setDataSetProperty(dsSearchTable, combineSQL(SEARCH_SQL,"?",new String[]{""}));

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"plan_code"}, new String[]{"SELECT pck_base.billNextCode('rl_invite_plan','plan_code') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"plan_code"}, new boolean[]{true}, null, 0));

    String pref = "ZPJH";
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"plan_ID"}, new String[]{"s_rl_invite_plan"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"plan_ID"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"plan_detail_ID"}, new String[]{"s_rl_invite_plan_detail"}));
    //dsDetailTable.setSort(new SortDescriptor("", new String[]{"hthwid"}, new boolean[]{false}, null, 0));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(BDLX_CHANGE), new Bdlx_Onchange());
    addObactioner(String.valueOf(DEPT_CHANGE), new DeptChange());
    addObactioner(String.valueOf(DETAIL_APPLY_ADD), new Detail_Apply_Add());//引招聘申请
    //&#$//审核部分
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());//取消审批
    addObactioner(String.valueOf(PERSON_ONCHANGE), new Person_Onchage());
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
      String opearate = request.getParameter(OPERATE_KEY);
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
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }

  /**
   * 初始化行信息
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
      if(!isAdd){
        m_RowInfo.put(getMaterTable());
      }
      else
      {
        //主表新增
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //String lsh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('rl_invite_plan','lsh') from dual");
       // m_RowInfo.put("lsh", lsh);
         m_RowInfo.put("createDate", today);//制单日期
         m_RowInfo.put("creator", loginName);
         m_RowInfo.put("creatorID", loginId);
         //m_RowInfo.put("chg_date", today);
         m_RowInfo.put("state", "0");
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
      detailRow.put("plan_ID", dsMasterTable.getValue("plan_ID"));//

      String cc = rowInfo.get("bdeptid_"+i);
      String dd = rowInfo.get("isMarried_"+i);

      detailRow.put("deptid", rowInfo.get("bdeptid_"+i));//申请部门
      detailRow.put("personid", rowInfo.get("personid_"+i));//申请人
      detailRow.put("job_kind", rowInfo.get("job_kind_"+i));//工作性质
      detailRow.put("age", rowInfo.get("age_"+i));//年龄
      detailRow.put("emp_title", rowInfo.get("emp_title_"+i));//职称
      detailRow.put("foreign_lang", rowInfo.get("foreign_lang_"+i));//外语水平
      detailRow.put("edu_level", rowInfo.get("edu_level_"+i));//学历
      detailRow.put("invite_kind", rowInfo.get("invite_kind_"+i));//聘用形式
      detailRow.put("recruit_num", rowInfo.get("recruit_num_"+i));//人数
      detailRow.put("memo", rowInfo.get("memo_"+i));//备注
      detailRow.put("emp_sex", rowInfo.get("emp_sex_"+i));//性别
      detailRow.put("isMarried", rowInfo.get("isMarried_"+i));//婚否
      d_RowInfos.set(i,detailRow);
    }
  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){return dsDetailTable;}

  /*打开从表*/
  private final void openDetailTable(boolean isMasterAdd)
  {
    plan_ID = dsMasterTable.getValue("plan_ID");
    String SQL = isMasterAdd ? "-1" : plan_ID;
     SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});
     //if (!isMasterAdd){
       dsDetailTable.setQueryString(SQL);
       //plan_ID = dsMasterTable.getValue("plan_ID");
       // dsDetailTable.setQueryString(DETAIL_SQL + (isMasterAdd ? "-1" : plan_ID));

       if(dsDetailTable.isOpen())
         dsDetailTable.refresh();
       else
         dsDetailTable.open();
     //}
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    int s = d_RowInfos.size();
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
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
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
       RowMap row = fixedQuery.getSearchRow();
       row.clear();//
       state = new String[]{""};
      if(dsMasterTable.isOpen() && dsMasterTable.getRowCount() > 0)
        dsMasterTable.empty();
      dsMasterTable.setQueryString(MASTER_STRUT_SQL);
      dsMasterTable.setRowMax(null) ;
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      //data.setMessage(showJavaScript("showFixedQuery();"));
     // String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      //if(code.equals("1"))
      //  submitType=true;
     // else
      //  submitType=false;
    }
  }


  /**
 *改变车间触发的事件
 */
class DeptChange implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    putDetailInfo(data.getRequest());
    HttpServletRequest req = data.getRequest();
    m_RowInfo.put(req);
    boolean isDept = String.valueOf(DEPT_CHANGE).equals(action);
  }
  }



  /**
 * 选择姓名后提交页面的类
 */
  class Person_Onchage implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
    }
  }
  //&#$
  /**
   * 审批操作的触发类
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = true;
      isMasterAdd=false;

      HttpServletRequest request = data.getRequest();
      //得到request的参数,值若为null, 则用""代替
      String id = data.getParameter("id", "");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen())
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      //打开从表
      isMasterAdd=false;
      openDetailTable(false);
      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
    }
  }
  //&#$
  /**
   * 添加到审核列表的操作类
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterTable.getValue("plan_code");//?
      String deptid = dsMasterTable.getValue("deptid");
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "invite_job", content,deptid);
    }
  }
  /**
   * 取消
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = true;

      HttpServletRequest request = data.getRequest();
      //得到request的参数,值若为null, 则用""代替
      String id = data.getParameter("id", "");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(),"invite_job");
    }
  }

  /**
   * 2004-2-18添加
   * 改变变动类型
   */
  class Bdlx_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      //String oldbdlx=m_RowInfo.get("chg_type");
      putDetailInfo(data.getRequest());//保存输入的明细信息
      //String chg_type=m_RowInfo.get("chg_type");
      //if(oldbdlx.equals(chg_type))
      //{
     //   return;
     // }
      //else
     // {
      //  dsDetailTable.empty();
     //   d_RowInfos.clear();
    //  }
    }
  }
  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //&#$
      isApprove = false;
      isMasterAdd = String.valueOf(ADD).equals(action);
      //打开从表
      if(!isMasterAdd)
      {
        isMasterAdd=false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        plan_ID = dsMasterTable.getValue("plan_ID");
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
      String plan_ID = null;
      if(isMasterAdd){
        ds.insertRow(false);
        plan_ID = dataSetProvider.getSequence("s_rl_invite_plan");
         //ds.setValue("lsh", rowInfo.get("lsh"));
         ds.setValue("plan_ID", plan_ID);
         ds.setValue("filialeID", filialeID);
         ds.setValue("creatorID", loginId);
         ds.setValue("creator", loginName);//操作员
         ds.setValue("state", "0");//操作员
         ds.setValue("createDate", rowInfo.get("createDate"));
      }
      //保存从表的数据
      //FieldInfo[] fields = detailProducer.getBakFieldCodes();
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();

      if(detail.getRowCount()<1)
      {
        ds.refresh();
        data.setMessage(showJavaScript("alert('明细情况需要有数据!');"));
        return;
      }
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //long internalRow = Long.parseLong(detailrow.get("InternalRow"));
        //detail.goToInternalRow(internalRow);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("plan_ID", plan_ID);

          String k=detailrow.get("t_recruit_num");

          detail.setValue("deptid", detailrow.get("deptid"));//申请部门
          detail.setValue("personid", detailrow.get("personid"));//申请人
          detail.setValue("job_kind", detailrow.get("job_kind"));//工作性质
          detail.setValue("age", detailrow.get("age"));//年龄
          detail.setValue("emp_title", detailrow.get("emp_title"));//职称
          detail.setValue("foreign_lang", detailrow.get("foreign_lang"));//外语水平
          detail.setValue("edu_level", detailrow.get("edu_level"));//学历
          detail.setValue("invite_kind", detailrow.get("invite_kind"));//聘用形式
          detail.setValue("recruit_num", detailrow.get("recruit_num"));//人数
          detail.setValue("memo", detailrow.get("memo"));//备注
          detail.setValue("emp_sex", detailrow.get("emp_sex"));//性别
          detail.setValue("isMarried", detailrow.get("isMarried"));//婚否


          detail.post();
          detail.next();
      }
      //保存主表数据
      //ds.setValue("chg_date", rowInfo.get("chg_date"));//变动日期
      //制单日期

      String aa = rowInfo.get("t_recruit_num");
      ds.setValue("personid", rowInfo.get("personid"));
      String bb=rowInfo.get("plan_mode");
      ds.setValue("plan_mode", rowInfo.get("plan_mode"));//招聘形式
      ds.setValue("plan_date", rowInfo.get("plan_date"));//招聘日期
      ds.setValue("tot_num", rowInfo.get("t_recruit_num"));//人数

      //ds.setValue("memo", rowInfo.get("memo"));//主表备注
      ds.setValue("deptid", rowInfo.get("deptid"));
      //ds.setValue("state", rowInfo.get("state"));//变动原因
      ds.post();
      dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"plan_code"}, new String[]{"SELECT pck_base.billNextCode('rl_invite_plan','plan_code') from dual"}));
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      if(String.valueOf(POST_CONTINUE).equals(action))
      {
        isMasterAdd = true;
        detail.empty();
        initRowInfo(true, true, true);
        initRowInfo(false, true, true);//重新初始化从表的各行信息
        //initRowInfo(false, false, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
    }


    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      //String temp = rowInfo.get("chg_date");
      //if(temp.equals(""))
        //return showJavaScript("alert('变动日期不能为空！');");
     String temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('提交部门不能为空！');");
     // temp = rowInfo.get("chg_type");
   // if(temp.equals(""))
      //  return showJavaScript("alert('请选择变动类型!');");
    //temp = rowInfo.get("personid");
      //  if(temp.equals(""))
       // return showJavaScript("alert('请选择姓名!');");
      return null;
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
    for(int i=0; i<d_RowInfos.size(); i++)
    {
      int row = i+1;
      detailrow = (RowMap)d_RowInfos.get(i);
      temp = detailrow.get("deptid");
     if(temp.equals(""))
       return showJavaScript("alert('申请部门不能为空！');");
     temp = detailrow.get("recruit_num");
    if(temp.equals("")||temp.equals("0"))
       return showJavaScript("alert('招聘人数要大于0！');");
    }
    return null;
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
    state = data.getRequest().getParameterValues("state");
    if(!(state==null))
    {
      StringBuffer sbzt = null;
      String m = state[state.length-1];
      if ( !m.equals("") )
      {
        for(int i=0;i<state.length;i++)
        {
          if(sbzt==null)
            sbzt= new StringBuffer(" AND state IN(");
          sbzt.append(state[i]+",");
        }
      }
      if(sbzt == null)
        sbzt =new StringBuffer();
      else
        sbzt.append("-99)");
      SQL = SQL+sbzt.toString();
    }
    else
      state = new String[]{""};
    if(state==null)
      SQL = "  AND zt<>0 AND zt<>9  ";


    SQL = combineSQL(MASTER_SQL, "?", new String[]{ SQL});
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
    if(!master.isOpen())
      master.open();
    //初始化固定的查询项目
    fixedQuery = new QueryFixedItem();
    fixedQuery.addShowColumn("", new QueryColumn[]{
      //new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
      new QueryColumn(master.getColumn("plan_date"), null, null, null, "a", ">="),
      new QueryColumn(master.getColumn("plan_date"), null, null, null, "b", "<="),
      new QueryColumn(master.getColumn("createDate"), null, null, null, "a", ">="),
      new QueryColumn(master.getColumn("createDate"), null, null, null, "b", "<="),
      new QueryColumn(master.getColumn("plan_code"), null, null, null, "a", ">="),
      new QueryColumn(master.getColumn("plan_code"), null, null, null, "b", "<="),
      //new QueryColumn(master.getColumn("try_job"), null, null, null, null, "try_job"),
      //new QueryColumn(master.getColumn("apply_ID"), "VW_PERSON_PXFK", "apply_ID", "xm", "xm", "like"),
      new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门
      new QueryColumn(master.getColumn("creator"), null, null, null, null, "="),
      new QueryColumn(master.getColumn("state"), null, null, null, null, "=")
      //new QueryColumn(master.getColumn("emp_sex"), null, null, null, null, "="),
    });
    isInitQuery = true;
  }
  }
/**
  * 得到招聘申请单货物的bean
  * @param req WEB的请求
  * @return 返回申请单货物的bean
  */
  public ImportZpApply getBuyApplyBean(HttpServletRequest req)
 {
   if(buyApplyBean == null)
     buyApplyBean = ImportZpApply.getInstance(req);
   return buyApplyBean;
  }



  //  根据招聘申请单从表增加操作

  class Detail_Apply_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();
      String ImportZpApply = m_RowInfo.get("mutiimportmrp");
      if(ImportZpApply.length() == 0)
        return;

      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "apply_ID");
      String[] apply_ID = parseString(ImportZpApply,",");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String plan_ID = dsMasterTable.getValue("plan_ID");
      //String personid = ZPSQTable.getValue("personid");//招聘申请数据集
      for(int i=0; i < apply_ID.length; i++)
      {
        if(apply_ID[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, apply_ID[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          //LookUp look = getBuyPriceBean(req);
          //look.regData(new String[]{apply_ID[i]});
          RowMap buyapplyRow = getBuyApplyBean(req).getLookupRow(apply_ID[i]);
          dsDetailTable.insertRow(false);
          double recruit_num = buyapplyRow.get("recruit_num").length() > 0 ? Double.parseDouble(buyapplyRow.get("recruit_num")) : 0;//人数
          //double sl = buyapplyRow.get("sl").length() >0 ? Double.parseDouble(buyapplyRow.get("sl")) : 0;
          //String skhtlStr = buyapplyRow.get("skhtl");
          //double skhtl = buyapplyRow.get("skhtl").length() >0 ? Double.parseDouble(buyapplyRow.get("skhtl")) : 0;
          //double wkhtl = sl-skhtl;
          //double wkhtl = buyapplyRow.get("wkhtl").length() > 0 ? Double.parseDouble(buyapplyRow.get("wkhtl")) : 0;
          //double hl = rowInfo.get("hl").length() > 0 ? Double.parseDouble(rowInfo.get("hl")) : 0;// 汇率
          dsDetailTable.setValue("plan_detail_ID", "-1");
          dsDetailTable.setValue("apply_ID",apply_ID[i]);
          //dsDetailTable.setValue("cpid",buyapplyRow.get("cpid"));
          //dsDetailTable.setValue("sl", String.valueOf(wkhtl));
          //dsDetailTable.setValue("dj", buyapplyRow.get("dj"));
          //dsDetailTable.setValue("je", formatNumber(String.valueOf(wkhtl*dj), sumFormat));
          //dsDetailTable.setValue("dmsxid", buyapplyRow.get("dmsxid"));//规格属性
          String aa=buyapplyRow.get("deptid");
          String bb=buyapplyRow.get("edu_level");
          String cc=buyapplyRow.get("sex");
          dsDetailTable.setValue("recruit_num", buyapplyRow.get("recruit_num"));//招收人数
          dsDetailTable.setValue("memo", buyapplyRow.get("memo"));//备注
          dsDetailTable.setValue("job_kind", buyapplyRow.get("job_kind"));//工作性质
          dsDetailTable.setValue("personid", buyapplyRow.get("personid"));//申请人
          dsDetailTable.setValue("deptid", buyapplyRow.get("deptid"));//申请部门
          dsDetailTable.setValue("emp_sex", buyapplyRow.get("sex"));//性别
          dsDetailTable.setValue("age", buyapplyRow.get("age"));//年龄
          dsDetailTable.setValue("emp_title", buyapplyRow.get("emp_title"));//职称
          dsDetailTable.setValue("foreign_lang", buyapplyRow.get("foreign_lang"));//外语
          dsDetailTable.setValue("edu_level", buyapplyRow.get("edu_level"));//学历
          dsDetailTable.setValue("isMarried", buyapplyRow.get("isMarried"));//婚否
          dsDetailTable.setValue("invite_kind", buyapplyRow.get("invite_kind"));//聘用性质

          dsDetailTable.setValue("plan_ID", isMasterAdd ? "" : plan_ID);


          dsDetailTable.post();

          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);

        }
      }
    }
  }
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
     //detail.setTableName("CG_HTHW");
     EngineDataSet ds = getMaterTable();
     isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
     if(!isMasterAdd)
       ds.goToInternalRow(masterRow);
     String plan_ID = dsMasterTable.getValue("plan_ID");
     detail.insertRow(false);
     //hthwid = dataSetProvider.getSequence("s_cg_hthw");
     //detail.setValue("hthwid", isMasterAdd ? "-1" : hthwid);
     detail.setValue("plan_ID", isMasterAdd ? "-1" : plan_ID);
     detail.post();
     int d = detail.getRowCount();
     RowMap detailrow = new RowMap(detail);
     detailrow.put("InternalRow", String.valueOf(detail.getInternalRow()));
     d_RowInfos.add(detailrow);
     initRowInfo(false, isMasterAdd, true);//重新初始化从表的各行信息

   }
  }


  /**
   *   从表删除操作
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
    *人员信息
   */
  public LookUp getpersonNameBean(HttpServletRequest req)
  {
    if(personBean == null)
      personBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PERSON);
    return personBean;
  }
}
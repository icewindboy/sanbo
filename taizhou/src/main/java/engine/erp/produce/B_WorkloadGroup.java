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
import engine.erp.produce.ImportProcess;
import engine.erp.produce.B_WageFormula;
import engine.erp.baseinfo.BasePublicClass;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 生产--工人工作量列表（按工作组输入）</p>
 * <p>Description: 生产--工人工作量列表（按工作组输入）<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_WorkloadGroup extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";
  public  static final String MASTER_SEL_PROCESS = "10021";
  public  static final String ONCHANGE = "10031";
  public  static final String GROUP_DETAIL_ADD = "10921";
  public  static final String DELETE_PROCESS = "11231";//删除加工单触发事件
  public  static final String DELETE_PRODUCT = "12231";
  public  static final String GXMC_ONCHANGE = "10731";
  public  static final String DEPTCHANGE = "11731";
  public  static final String COMPLETE = "10011";//强制完成触发事件
  public  static final String REPORT = "2000";//报表追踪操作
  public  static final String DETAIL_PERSON_ADD = "2001";//工人工作量人员增加触发事件
  public  static final String DETAIL_PERSON_DELETE = "2002";//工人工作量人员删除触发事件
  public  static final String SINGLE_SEL_PROCESS = "2003";//工人工作量明细单选加工单触发事件
  public  static final String MATERAIL_ADD = "2004";//工人工作量物料新增一行触发事件
  public  static final String MATERAIL_DEL = "2005";//工人工作量物料删除一行触发事件
  public  static final String MATERAIL_CONFIRM = "2006";//工人工作量物料保存页面信息触发事件


  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_gzzgzl WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM sc_gzzgzl WHERE ? AND fgsid=? ? ORDER BY djh DESC";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_gzzgzlmx WHERE 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sc_gzzgzlmx WHERE gzzgzlid='?'";//
  //工作组输入工人工作量人员SQL
  private static final String DETAIL_PERSONSTRUCT_SQL = "SELECT * FROM sc_gzzgzlry WHERE 1<>1";
  private static final String DETAIL_PERSON_SQL    = "SELECT * FROM sc_gzzgzlry WHERE gzzgzlid='?' ";
  //工作组输入工人工作量物料SQL
  private static final String GROUP_MATERAILSTRUCT_SQL = "SELECT * FROM sc_gzzgzlwl WHERE 1<>1";//工作组工作量物料结构SQL
  private static final String GROUP_MATERAIL_SQL    = "SELECT * FROM sc_gzzgzlwl WHERE gzzgzlid='?' ";//工作组工作量物料

  //工作组工作量引入加工单，得到加工单物料的主配料。
  private static final String PROCESSMATERAIL_SQL = "SELECT a.* FROM sc_jgdwl a,sc_bom b WHERE a.cpid=b.cpid AND b.zjlx=5 AND a.jgdid='?'";

  //
  //private static final String GROUP_DETAIL_SQL  =
      //"SELECT a.personid, (a.ryjs) bl, (SELECT b.deje FROM sc_gylxmx b WHERE b.gymcid = a.gymcid AND b.gylxid = ?) jjdj, (SELECT c.gymc FROM sc_gylxmx b, sc_gymc c WHERE b.gymcid= c.gymcid AND c.gymcid = a.gymcid AND b.gylxid = ?) gx FROM sc_gzzry a WHERE a.gzzid = ? ";//
  //
  private static final String GROUP_DETAIL_SQL  =
      "SELECT a.personid, (a.ryjs) bl  FROM sc_gzzry a WHERE a.gzzid = ? ";//
  //加工单主表信息SQL
  private static final String PROCESS_DETAIL_SQL = "SELECT a.*, b.hsbl FROM sc_jgdmx a, kc_dm b WHERE a.cpid=b.cpid AND nvl(a.sl,0)>nvl(a.ypgzl,0) AND a.jgdid= ";
  //报表调用工人工作量的SQL
  private static final String MASTER_REPORT_SQL    = "SELECT * FROM sc_gzzgzl WHERE gzzgzlid=";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//工作组工作量主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//工作组工作量明细表

  //private EngineDataSet dsGroupMaterail  = new EngineDataSet();//工作组工作量物料表
  private EngineDataSet dsDetailPerson  = new EngineDataSet();//工作组工作量人员表


  private LookUp technicsBean = null; //工艺路线信息的bean的引用, 用于提取工艺路线信息
  public B_WorkLoad_Sel_Process  workloadSelProcessBean = null;//加工单主表信息BEAN的引用

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_gzzgzl");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_gzzgzlmx");
  public  HtmlTableProducer persondetailProducer = new HtmlTableProducer(dsDetailPerson, "sc_gzzgzlry");
  private boolean isMasterAdd = true;    //是否在添加状态
  public boolean isReport = false;
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private ArrayList d_PersonRowInfos = null; //工作组工作量人员信息
  //private ArrayList d_GroupMaterail = null;//工作组工作量物料

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public ImportProcess  importprocessBean = null;//引入加工单的bean的引用, 用于提取引入加工单信息
  private LookUp propertyBean = null; //产品信息的bean的引用, 用于提取产品信息

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String gzzgzlid = null;
  private User user = null;
  public String SC_STORE_UNIT_STYLE = null;//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  public String SC_PRODUCE_UNIT_STYLE = null;//计量单位和生产单位换算方式1=强制换算,0=仅空值时换算
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  private int count = 0;//引加工单，加工单明细的行数
  private String MaterailTotal = null;//引加工单时，加工单物料主配料的数量
  private String jgdid = null;
  /**
   * 工作量列表（按工人输入）的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回工作量列表（按工人输入）的实例
   */
  public static B_WorkloadGroup getInstance(HttpServletRequest request)
  {
    B_WorkloadGroup workloadGroupBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "workloadGroupBean";
      workloadGroupBean = (B_WorkloadGroup)session.getAttribute(beanName);
      if(workloadGroupBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        workloadGroupBean = new B_WorkloadGroup();
        workloadGroupBean.qtyFormat = loginBean.getQtyFormat();
        workloadGroupBean.sumFormat = loginBean.getSumFormat();
        workloadGroupBean.priceFormat = loginBean.getPriceFormat();

        workloadGroupBean.fgsid = loginBean.getFirstDeptID();
        workloadGroupBean.loginDept = loginBean.getDeptID();
        workloadGroupBean.loginId = loginBean.getUserID();
        workloadGroupBean.loginName = loginBean.getUserName();
        workloadGroupBean.user = loginBean.getUser();
        workloadGroupBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//计量单位和生产单位是否强制换算
        workloadGroupBean.SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和换算单位是否强制换算
        workloadGroupBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        //设置格式化的字段
        workloadGroupBean.dsMasterTable.setColumnFormat("sl", workloadGroupBean.qtyFormat);
        workloadGroupBean.dsMasterTable.setColumnFormat("zgf", workloadGroupBean.priceFormat);
        workloadGroupBean.dsDetailTable.setColumnFormat("sl", workloadGroupBean.qtyFormat);
        workloadGroupBean.dsDetailTable.setColumnFormat("hssl", workloadGroupBean.qtyFormat);
        workloadGroupBean.dsDetailTable.setColumnFormat("scsl", workloadGroupBean.qtyFormat);
        workloadGroupBean.dsDetailTable.setColumnFormat("jjdj", workloadGroupBean.qtyFormat);
        workloadGroupBean.dsDetailTable.setColumnFormat("jjgz", workloadGroupBean.priceFormat);
        workloadGroupBean.dsDetailPerson.setColumnFormat("jjgz", workloadGroupBean.priceFormat);
        session.setAttribute(beanName, workloadGroupBean);
      }
    }
    return workloadGroupBean;
  }

  /**
   * 构造函数
   */
  private B_WorkloadGroup()
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
    setDataSetProperty(dsDetailPerson, DETAIL_PERSONSTRUCT_SQL);
    //setDataSetProperty(dsGroupMaterail, GROUP_MATERAILSTRUCT_SQL);

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"djh"}, new String[]{"SELECT pck_base.billNextCode('sc_gzzgzl','djh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"djh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"gzzgzlmxid"}, new String[]{"s_sc_gzzgzlmx"}));
    dsDetailPerson.setSequence(new SequenceDescriptor(new String[]{"gzzgzlryid"}, new String[]{"s_sc_gzzgzlry"}));
    //dsGroupMaterail.setSequence(new SequenceDescriptor(new String[]{"gzzgzlwlid"}, new String[]{"s_sc_gzzgzlwl"}));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());//工作量明细增加事件
    addObactioner(String.valueOf(DETAIL_PERSON_ADD), new Detail_Add());//工作量人员增加事件
    //addObactioner(String.valueOf(MATERAIL_ADD), new Materail_Add());//工作量物料增加事件
    ///addObactioner(String.valueOf(MATERAIL_DEL), new Materail_Delete());//工作量物料增加事件
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());//工作量明细删除事件
    addObactioner(String.valueOf(DETAIL_PERSON_DELETE), new Detail_Delete());//工作量人员删除事件
    //addObactioner(String.valueOf(MATERAIL_CONFIRM), new Materail_Confirm());//工作量物料确定
    addObactioner(String.valueOf(DEPTCHANGE), new DeptChange());
    addObactioner(String.valueOf(DELETE_PROCESS), new Delete_Process());
    //addObactioner(String.valueOf(DELETE_PRODUCT), new Delete_Product());
    addObactioner(String.valueOf(GROUP_DETAIL_ADD), new Group_Detail_Add());
    addObactioner(String.valueOf(GXMC_ONCHANGE), new Gxmc_Onchange());
    //addObactioner(String.valueOf(MASTER_SEL_PROCESS), new Master_Sel_Process());
    addObactioner(String.valueOf(COMPLETE), new Complete());//强制完成事件
    addObactioner(String.valueOf(REPORT), new Report());//报表引用事件
    addObactioner(String.valueOf(SINGLE_SEL_PROCESS), new Single_Select_Process());//单选加工单触发事件
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
    if(dsDetailPerson != null){
      dsDetailPerson.close();
      dsDetailPerson = null;
    }
    /**
    if(dsGroupMaterail != null){
      dsGroupMaterail.close();
      dsGroupMaterail = null;
    }
    */
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    d_PersonRowInfos = null;
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
    if(persondetailProducer != null)
    {
     persondetailProducer.release();
     persondetailProducer = null;
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
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), priceFormat));//数量
      detailRow.put("scsl", formatNumber(rowInfo.get("scsl_"+i), qtyFormat));//生产数量
      detailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+i), priceFormat));//换算数量
      detailRow.put("jjdj", formatNumber(rowInfo.get("jjdj_"+i), priceFormat));//计件单价
      detailRow.put("cpid", formatNumber(rowInfo.get("cpid_"+i), qtyFormat));//产品
      detailRow.put("jjgz", formatNumber(rowInfo.get("jjgz_"+i), priceFormat));//计件工资
      detailRow.put("gx", rowInfo.get("gx_"+i));//工序
      detailRow.put("gylxid", rowInfo.get("gylxid_"+i));//工艺路线ID
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//规格属性
      detailRow.put("jjff", rowInfo.get("jjff_"+i));//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
      //保存用户自定义的字段
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
    int personrownum = d_PersonRowInfos.size();
    RowMap personDetailRow = null;
    for(int i=0; i<personrownum; i++)
    {
      personDetailRow = (RowMap)d_PersonRowInfos.get(i);
      personDetailRow.put("bl", formatNumber(rowInfo.get("bl_"+i), qtyFormat));//比例
      personDetailRow.put("jjgz", formatNumber(rowInfo.get("grjjgz_"+i), priceFormat));//计件工资
      personDetailRow.put("personid", rowInfo.get("personid_"+i));//人员
      //保存用户自定义的字段
      FieldInfo[] fields = persondetailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        personDetailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
  }
  /**
   * 通用加工单
   * 保存用户输入的信息

  private final void putCommonInfo(HttpServletRequest request)
  {
    RowMap rowInfo = new RowMap();
    //保存网页的所有信息
    rowInfo.put(request);
    //保存生产加工单物料信息
    int count = d_GroupMaterail.size();
    RowMap materailRow = null;
    for(int m=0; m<count; m++)
    {
      materailRow = (RowMap)d_GroupMaterail.get(m);
      materailRow.put("cpid", rowInfo.get("cpid_"+m));//产品
      materailRow.put("sl", formatNumber(rowInfo.get("sl_"+m), qtyFormat));//计量单位数量
      materailRow.put("scsl", formatNumber(rowInfo.get("scsl_"+m), qtyFormat));//生产单位数量
      materailRow.put("dmsxid", rowInfo.get("dmsxid_"+m));//物资规格属性
    }
  }
     */

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
  /*得到人员从表表对象*/
  public final EngineDataSet getPersonDetailTable(){
    if(!dsDetailPerson.isOpen())
      dsDetailPerson.open();
    return dsDetailPerson;
  }
    /*得到物料从表表对象
  public final EngineDataSet getMaterailTable(){
    if(!dsGroupMaterail.isOpen())
      dsGroupMaterail.open();
    return dsGroupMaterail;
  }
  */


  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    String id = isMasterAdd ? "-1" : gzzgzlid;
    String SQL = combineSQL(DETAIL_SQL, "?", new String[]{id});

    dsDetailTable.setQueryString(SQL);//打开工作组工作量明细表
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    else
      dsDetailTable.refresh();
    /**
    String sql = combineSQL(GROUP_MATERAIL_SQL,"?", new String[]{id});
    if(!dsGroupMaterail.isOpen())
      dsGroupMaterail.open();
    else
      dsGroupMaterail.refresh();
    */
  }
  /*打开从表*/
  public final void openPersonTable(boolean isMasterAdd)
  {
    String id = isMasterAdd ? "-1" : gzzgzlid;
    String sql = combineSQL(DETAIL_PERSON_SQL, "?", new String[]{id});
    dsDetailPerson.setQueryString(sql);//打开工作组工作量人员表
    if(!dsDetailPerson.isOpen())
      dsDetailPerson.open();
    else
      dsDetailPerson.refresh();
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /*得到工作组人员从表多列的信息*/
  public final RowMap[] getPersonDetailRowinfos() {
    RowMap[] rows = new RowMap[d_PersonRowInfos.size()];
    d_PersonRowInfos.toArray(rows);
    return rows;
  }
  /*得到工作组物料从表多列的信息
  public final RowMap[] getMaterailRowinfos() {
    RowMap[] rows = new RowMap[d_GroupMaterail.size()];
    d_GroupMaterail.toArray(rows);
    return rows;
  }
  */

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
      isReport =false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      persondetailProducer.init(request, loginId);
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("rq$a", startDay);
      row.put("rq$b", today);
      isMasterAdd = true;
      //
      String SQL = "AND zt<>8";
      dsMasterTable.setQueryString(combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), fgsid, SQL}));
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      if(dsDetailPerson.isOpen() && dsDetailPerson.getRowCount() > 0)
        dsDetailPerson.empty();
      /**
      if(dsGroupMaterail.isOpen() && dsGroupMaterail.getRowCount() > 0)
        dsGroupMaterail.empty();
        */
    }
  }
  /**
   * 报表调用工人工作量操作的触发类
   */
  class Report implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isReport = true;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      persondetailProducer.init(request, loginId);
      String id = request.getParameter("gzzgzlid");
      String SQL = MASTER_REPORT_SQL+id;
      dsMasterTable.setQueryString(SQL);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();

      gzzgzlid = dsMasterTable.getValue("gzzgzlid");
      B_WageFormula.getInstance(request).readyExpressions();
      //打开从表
      openDetailTable(false);

      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
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
      gzzgzlid = dsMasterTable.getValue("gzzgzlid");
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
      isReport =false;
      isMasterAdd = String.valueOf(ADD).equals(action);
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        gzzgzlid = dsMasterTable.getValue("gzzgzlid");
      }
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      openPersonTable(isMasterAdd);
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);

      data.setMessage(showJavaScript("toDetail();"));
    }
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
        m_RowInfo.put("zdrq", today);
        m_RowInfo.put("zdr", loginName);
        m_RowInfo.put("zdrid", loginId);
        m_RowInfo.put("deptid", loginDept);
        m_RowInfo.put("rq", today);
        m_RowInfo.put("zt","0");
      }
    }
    else
    {
      //工作组工作量产品
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
      //工作组工作量人员
      EngineDataSet dsPersonDetail = dsDetailPerson;
      if(d_PersonRowInfos == null)
        d_PersonRowInfos = new ArrayList(dsPersonDetail.getRowCount());
      else if(isInit)
        d_PersonRowInfos.clear();

      dsPersonDetail.first();
      for(int j=0; j<dsPersonDetail.getRowCount(); j++)
      {
        RowMap row = new RowMap(dsPersonDetail);
        d_PersonRowInfos.add(row);
        dsPersonDetail.next();
      }
      /**工作组工作量物料
      EngineDataSet dsMaterail = dsGroupMaterail;
      if(d_GroupMaterail == null)
        d_GroupMaterail = new ArrayList(dsMaterail.getRowCount());
      else if(isInit)
        d_GroupMaterail.clear();

      dsMaterail.first();
      for(int j=0; j<dsMaterail.getRowCount(); j++)
      {
        RowMap row = new RowMap(dsMaterail);
        d_GroupMaterail.add(row);
        dsMaterail.next();
      }
      */
    }
  }
  /**
   *改变车间触发的事件提交deptid
   */
  class DeptChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(req);
      dsDetailPerson.deleteAllRows();
      d_PersonRowInfos.clear();
    }
  }
  /**
   *  选择加工单主表，引入从表所有未加工信息
   */
  class Single_Select_Process implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      RowMap masterrow = getMasterRowinfo();
      String oldjgdid = masterrow.get("jgdid");
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      jgdid = m_RowInfo.get("jgdid");
      if(jgdid.equals("") || oldjgdid.equals(jgdid))
        return;
      EngineDataSet detail = getDetailTable();
      detail.first();
      while(detail.inBounds())
      {
        String jgdmxid = detail.getValue("jgdmxid");
        if(!jgdmxid.equals(""))
        {
          d_RowInfos.remove(detail.getRow());
          detail.deleteRow();
       }
       else
         detail.next();
      }
      //把加工单物料引入并存入工作组工作量物料
      String SQL = PROCESS_DETAIL_SQL+jgdid;
      EngineDataSet tempProcessData = null;//零时加工单从表信息数据集
      if(tempProcessData==null)
      {
        tempProcessData = new EngineDataSet();
        setDataSetProperty(tempProcessData,null);
      }
      tempProcessData.setQueryString(SQL);
      if(!tempProcessData.isOpen())
        tempProcessData.openDataSet();
      else
        tempProcessData.refresh();

      /**
      String sql = combineSQL(PROCESSMATERAIL_SQL,"?", new String[]{jgdid});
      EngineDataSet dsSumNumber = null;//得到加工单物料中主配料的数量；
      if(dsSumNumber==null)
      {
        dsSumNumber = new EngineDataSet();
        setDataSetProperty(dsSumNumber,null);
      }
      dsSumNumber.setQueryString(sql);
      if(!dsSumNumber.isOpen())
        dsSumNumber.openDataSet();
      else
        dsSumNumber.refresh();
      dsSumNumber.first();
      for(int t=0; t<dsSumNumber.getRowCount(); t++){
        dsGroupMaterail.insertRow(false);
        dsGroupMaterail.setValue("cpid", dsSumNumber.getValue("cpid"));
        dsGroupMaterail.setValue("dmsxid", dsSumNumber.getValue("dmsxid"));
        dsGroupMaterail.setValue("sl", dsSumNumber.getValue("sl"));
        dsGroupMaterail.setValue("scsl", dsSumNumber.getValue("scsl"));
        dsGroupMaterail.post();
        RowMap temprow = new RowMap(dsGroupMaterail);
        d_GroupMaterail.add(temprow);
        dsSumNumber.next();
      }
      */


      RowMap processMasterRow = getProcessMasterBean(req).getLookupRow(jgdid);
      rowInfo.put("deptid", processMasterRow.get("deptid"));
      rowInfo.put("jgdid", jgdid);
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String gzzgzlid = dsMasterTable.getValue("gzzgzlid");
      count = tempProcessData.getRowCount();//加工单明细的行数。
      for(int i=0; i<tempProcessData.getRowCount(); i++)
      {
        tempProcessData.goToRow(i);
        //double hsbl = tempProcessData.getValue("hsbl").length()>0 ? Double.parseDouble(tempProcessData.getValue("hsbl")) : 0;
        String jgdmxid = tempProcessData.getValue("jgdmxid");
        String cpid = tempProcessData.getValue("cpid");
        String dmsxid = tempProcessData.getValue("dmsxid");//规格属性
        //String sxz = getPropertyBean(data.getRequest()).getLookupName(dmsxid);

        locateGoodsRow.setValue(0, jgdmxid);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          double sl = tempProcessData.getValue("sl").length()>0 ? Double.parseDouble(tempProcessData.getValue("sl")) : 0;//加工单需要的加工数量
          double ypgzl = tempProcessData.getValue("ypgzl").length()>0 ? Double.parseDouble(tempProcessData.getValue("ypgzl")) : 0;//加工单中已加工量
          double wpgzl = sl-ypgzl>0 ? sl-ypgzl : 0;
          if(wpgzl==0)
            continue;
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("gzzgzlmxid", "-1");
          dsDetailTable.setValue("jgdmxid",jgdmxid);
          dsDetailTable.setValue("cpid", cpid);
          dsDetailTable.setValue("sl", String.valueOf(wpgzl));
          //dsDetailTable.setValue("hssl", formatNumber(String.valueOf(hsbl==0 ? 0 : wpgzl/hsbl), qtyFormat));
          dsDetailTable.setValue("gylxid", tempProcessData.getValue("gylxid"));
          dsDetailTable.setValue("gzzgzlid", isMasterAdd ? "-1" : gzzgzlid);
          dsDetailTable.setValue("dmsxid", tempProcessData.getValue("dmsxid"));
          dsDetailTable.post();
          //创建一个与用户相对应的行
          RowMap detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
      data.setMessage(showJavaScript("big_change()"));
    }
  }
  /**
   *删除产品触发的事件

  class Delete_Product implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
      HttpServletRequest req = data.getRequest();
      m_RowInfo.put(req);
      m_RowInfo.put("cpid","");
   }
  }
     */
  /**
   *删除加工单触发的事件
   */
  class Delete_Process implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(req);
      RowMap rowinfo = getMasterRowinfo();
      rowinfo.put("jgdid", "");
      EngineDataSet detail = getDetailTable();
      detail.first();
      while(detail.inBounds())
      {
        String jgdmxid = detail.getValue("jgdmxid");
        if(!jgdmxid.equals(""))
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
  *选择工序类型触发的事件
  */
  class Gxmc_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      RowMap rowinfo = getMasterRowinfo();
      double sl = rowinfo.get("sl").length()>0 ? Double.parseDouble(rowinfo.get("sl")) : 0;
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      String gxmcid = req.getParameter("v_gx_"+rownum);
      RowMap technicsRow = getTechnicsBean(req).getLookupRow(gxmcid);//根据工序id得到工艺路线明细的一行信息
      RowMap detailRow = (RowMap)d_RowInfos.get(rownum);
      double deje = technicsRow.get("deje").length()>0 ? Double.parseDouble(technicsRow.get("deje")) : 0;//得到定额
      double bl = detailRow.get("bl").length()>0 ? Double.parseDouble(detailRow.get("bl")) : 0;
      detailRow.put("jjdj", technicsRow.get("deje"));
      if(sl!=0 && deje!=0)
        detailRow.put("jjgz", formatNumber(String.valueOf(sl*deje*bl),priceFormat) );
      double total = 0;
      for(int k=0; k<d_RowInfos.size(); k++)
      {
        RowMap detail = (RowMap)d_RowInfos.get(k);
        String jjgz = detail.get("jjgz");
        total += isDouble(jjgz) ? Double.parseDouble(jjgz) : 0;
      }
      rowinfo.put("zgf",formatNumber(String.valueOf(total),priceFormat));
    }
  }
  /**
   * 从表增加操作（通过选择工作组从表增加该组员工）
   */
  class Group_Detail_Add implements Obactioner
  {
    private EngineDataSet groupData = null;
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();
      EngineDataSet dsDetailPerson = getPersonDetailTable();
      /**
      String gylxid = rowInfo.get("gylxid");
      if(gylxid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择工艺路线')"));
        return;
      }
      */
      String gzzid = rowInfo.get("gzzid");
      if(gzzid.equals(""))
      {
        dsDetailPerson.deleteAllRows();
        d_PersonRowInfos.clear();
        //rowInfo.put("zgf","");
        return;
      }
      String sql = combineSQL(GROUP_DETAIL_SQL, "?", new String[]{gzzid});
      if(groupData == null)
      {
        groupData = new EngineDataSet();
        setDataSetProperty(groupData, null);
      }
      groupData.setQueryString(sql);
      if(!groupData.isOpen())
        groupData.openDataSet();
      else
        groupData.refresh();

      dsDetailPerson.deleteAllRows();
      d_PersonRowInfos.clear();
      String zgf = rowInfo.get("zgf");
      double d_zgf = zgf.length()>0 ? Double.parseDouble(zgf) : 0;
      int count = groupData.getRowCount();
      groupData.first();
      for(int j=0; j<count; j++)
      {
        dsDetailPerson.insertRow(false);
        dsDetailPerson.setValue("gzzgzlryid", "-1");
        dsDetailPerson.setValue("gzzgzlid", isMasterAdd ? "" : gzzgzlid);
        String bl = groupData.getValue("bl");
        String personid = groupData.getValue("personid");
        dsDetailPerson.setValue("personid", personid);
        dsDetailPerson.setValue("bl", bl);
        dsDetailPerson.post();
        //创建一个与用户相对应的行
        RowMap detailrow = new RowMap(dsDetailPerson);
        d_PersonRowInfos.add(detailrow);
        groupData.next();
      }
      data.setMessage(showJavaScript("big_blchange();"));
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
      String gz = B_WageFormula.getInstance(data.getRequest()).getWage();
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
      temp = checkPerosnDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      /**
      temp = checkMaterail();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      */
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);

      //得到主表主键值
      String gzzgzlid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        gzzgzlid = dataSetProvider.getSequence("s_sc_gzzgzl");
        ds.setValue("gzzgzlid", gzzgzlid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
        ds.setValue("zt","0");
      }
      //保存工作组工作量明细从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("gzzgzlid", gzzgzlid);

        detail.setValue("gx", detailrow.get("gx"));//工序
        detail.setValue("gylxid", detailrow.get("gylxid"));//工艺路线ID
        detail.setValue("sl", detailrow.get("sl"));//数量
        detail.setValue("hssl", detailrow.get("hssl"));//换算数量
        detail.setValue("scsl", detailrow.get("scsl"));//生产数量
        detail.setValue("cpid", detailrow.get("cpid"));//产品
        detail.setValue("dmsxid", detailrow.get("dmsxid"));//规格属性ID
        detail.setValue("jjdj", detailrow.get("jjdj"));//计件单价
        detail.setValue("jjgz", detailrow.get("jjgz"));//计件工资
        detail.setValue("jjff", detailrow.get("jjff"));
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        totalSum = totalSum.add(detail.getBigDecimal("jjgz"));
        totalNum = totalSum.add(detail.getBigDecimal("sl"));
        detail.next();
      }
      //保存工作组工作量明细从表的数据
      RowMap persondetailrow = null;
      EngineDataSet persondetail = getPersonDetailTable();
      //BigDecimal totalSum = new BigDecimal(0);
      persondetail.first();
      for(int i=0; i<persondetail.getRowCount(); i++)
      {
        persondetailrow = (RowMap)d_PersonRowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          persondetail.setValue("gzzgzlid", gzzgzlid);

        persondetail.setValue("personid", persondetailrow.get("personid"));//人员
        persondetail.setValue("bl", persondetailrow.get("bl"));//比例
        persondetail.setValue("jjgz", persondetailrow.get("jjgz"));//计件工资
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        persondetail.post();
        persondetail.next();
      }
      /**保存工作组工作量物料从表的数据
      RowMap materailRow = null;
      EngineDataSet dsMaterail = getMaterailTable();
      //BigDecimal totalSum = new BigDecimal(0);
      dsMaterail.first();
      for(int i=0; i<dsMaterail.getRowCount(); i++)
      {
        materailRow = (RowMap)d_GroupMaterail.get(i);
        //新添的记录
        if(isMasterAdd)
          dsMaterail.setValue("gzzgzlid", gzzgzlid);

        dsMaterail.setValue("cpid", materailRow.get("cpid"));
        dsMaterail.setValue("dmsxid", materailRow.get("dmsxid"));
        dsMaterail.setValue("sl", materailRow.get("sl"));
        dsMaterail.setValue("scsl", materailRow.get("scsl"));
        dsMaterail.post();
        dsMaterail.next();
      }
      */

      //保存主表数据
      double sl = rowInfo.get("sl").length()>0 ? Double.parseDouble(rowInfo.get("sl")) : 0;
      double hsbl = rowInfo.get("hsbl").length()>0 ? Double.parseDouble(rowInfo.get("hsbl")) : 0;
      ds.setValue("jgdid", rowInfo.get("jgdid"));//加工单明细id
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("gzzid", rowInfo.get("gzzid"));//工作组id
      //ds.setValue("gylxid", rowInfo.get("gylxid"));//工艺路线id
      ds.setValue("bc", rowInfo.get("bc"));//班次
      ds.setValue("sl", totalNum.toString());//数量
      ds.setValue("jdr", rowInfo.get("jdr"));//经手人
      ds.setValue("zgf", totalSum.toString());//总工费
      ds.setValue("rq", rowInfo.get("rq"));//日期
      ds.setValue("zlyz", rowInfo.get("zlyz"));//质量验证
      ds.setValue("ydzs", rowInfo.get("ydzs"));//原断纸数
      ds.setValue("dzcs", rowInfo.get("dzcs"));//断纸次数
      ds.setValue("jhsh", rowInfo.get("jhsh"));//计划损耗
      ds.setValue("sjsh", rowInfo.get("sjsh"));//实际损耗
      ds.setValue("jf", rowInfo.get("jf"));//奖罚
      ds.setValue("shyy", rowInfo.get("shyy"));//损耗原因
      ds.setValue("jt", rowInfo.get("jt"));//机台
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail,persondetail}, null);

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
        persondetail.empty();
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
      //ArrayList table = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, "产品明细中第"+row+"行数量")) != null)
          return temp;
        /**
        if(table.contains(personid))
          return showJavaScript("alert('员工重复！');");
        else
          table.add(personid);
          */
        temp = detailrow.get("cpid");
        if(temp.equals(""))
          return showJavaScript("alert('产品不能为空！');");
        temp = detailrow.get("gx");
        if(temp.equals(""))
          return showJavaScript("alert('产品明细中工序不能为空！');");
      }
      return null;
    }
    /**
     * 校验物料信息从表输入的信息的正确性
     * @return null 表示没有信息

    private String checkMaterail()
    {
      String temp = null;
      RowMap materailrow = null;
      if(d_GroupMaterail.size()<1)
        return null;
      String cpid=null, dmsxid=null, unit=null;
      for(int i=0; i<d_GroupMaterail.size(); i++)
      {
        int row = i+1;
        materailrow = (RowMap)d_GroupMaterail.get(i);
        cpid = materailrow.get("cpid");
        dmsxid = materailrow.get("dmsxid");
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        String sl = materailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
      }
      return null;
    }
    */
    /**
     * 校验工作组工作量人员从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkPerosnDetailInfo()
    {
      String temp = null;
      RowMap persondetailrow = null;
      if(d_PersonRowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      //ArrayList table = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_PersonRowInfos.size(); i++)
      {
        persondetailrow = (RowMap)d_PersonRowInfos.get(i);
        String personid = persondetailrow.get("personid");
        if(personid.equals(""))
          return showJavaScript("alert('员工不能为空！');");
        /**
         if(table.contains(personid))
         return showJavaScript("alert('员工重复！');");
         else
         table.add(personid);
         */
        String bl = persondetailrow.get("bl");
        if(bl.equals(""))
          return showJavaScript("alert('比例不能为空！');");
        if(bl.length() > 0 &&(temp = checkNumber(bl, "比例")) != null)
          return temp;
      }
      return null;
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
      temp = rowInfo.get("gzzid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择工作组！');");
      //temp = rowInfo.get("gylxid");
      //if(temp.equals(""))
        //return showJavaScript("alert('请选择工艺路线！');");
      String sl = rowInfo.get("sl");
      if(sl.length()>0 &&(temp = checkNumber(sl, "数量", false)) != null)
        return temp;
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
      dsDetailPerson.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable,dsDetailPerson}, null);
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),fgsid, SQL});
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
        new QueryColumn(master.getColumn("djh"), null, null,null),
        new QueryColumn(master.getColumn("bc"), null, null,null),
        new QueryColumn(master.getColumn("rq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("rq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "="),//状态
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        new QueryColumn(master.getColumn("gzzid"), null, null, null, null, "="),//工作组ID
        new QueryColumn(master.getColumn("jdr"), null, null,null),
        new QueryColumn(master.getColumn("jt"), null, null,null),
        new QueryColumn(master.getColumn("gzzgzlid"), "sc_gzzgzlmx", "gzzgzlid","cpid",  null,"="),//从表产品编码
        new QueryColumn(master.getColumn("gzzgzlid"), "sc_gzzgzlry", "gzzgzlid","personid", null, "="),//从表品名
      });
      isInitQuery = true;
    }
  }
  /**
  *  根据加工单主表增加操作

 class Master_Sel_Process implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     m_RowInfo.put(req);

     String singleIdInput = m_RowInfo.get("singleIdInput");
     if(singleIdInput.equals(""))
       return;
     RowMap importProcessRow = getProcessGoodsBean(req).getLookupRow(singleIdInput);
     double sl = importProcessRow.get("sl").length()>0 ? Double.parseDouble(importProcessRow.get("sl")) : 0;
     double hsbl = importProcessRow.get("hsbl").length()>0 ? Double.parseDouble(importProcessRow.get("hsbl")) : 0;
     m_RowInfo.put("jgdmxid",singleIdInput);
     m_RowInfo.put("cpid", importProcessRow.get("cpid"));
     m_RowInfo.put("sl", formatNumber(importProcessRow.get("sl"),qtyFormat));
     m_RowInfo.put("hssl", formatNumber(String.valueOf(hsbl==0 ? 0 : sl/hsbl), qtyFormat));
     m_RowInfo.put("gylxid", importProcessRow.get("gylxid"));
     m_RowInfo.put("dmsxid", importProcessRow.get("dmsxid"));
   }
 }
   */
   /**
   *  从表增加操作
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
      EngineDataSet dsPerson = getPersonDetailTable();
      boolean isPersonAdd = String.valueOf(DETAIL_PERSON_ADD).equals(action);
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String gzzgzlid = dsMasterTable.getValue("gzzgzlid");
      if(!isPersonAdd){
        detail.insertRow(false);
        detail.setValue("gzzgzlid", isMasterAdd ? "" : gzzgzlid);
        detail.post();
        d_RowInfos.add(new RowMap());
      }
      else{
        dsPerson.insertRow(false);
        dsPerson.setValue("gzzgzlid", isMasterAdd ? "" : gzzgzlid);
        dsPerson.post();
        d_PersonRowInfos.add(new RowMap());
      }
    }
  }
  /**
   * 工作组工作量物料保存操作的触发类

  class Materail_Confirm implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putCommonInfo(data.getRequest());
      //校验表单数据
      String temp = null;
      temp = checkInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getMaterailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_GroupMaterail.get(i);
        //新添的记录
        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("sl", detailrow.get("sl"));//生产单位需求量
        detail.setValue("scsl", detailrow.get("scsl"));//需购量
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.post();
        detail.next();
      }
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息

    private String checkInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      ArrayList list = new ArrayList(d_GroupMaterail.size());
      String cpid=null, dmsxid=null,unit=null;
      for(int i=0; i<d_GroupMaterail.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_GroupMaterail.get(i);
        cpid = detailrow.get("cpid");
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        /**
        dmsxid = detailrow.get("dmsxid");
        StringBuffer buf = new StringBuffer().append(cpid).append(",").append(dmsxid).append(",");
        unit = buf.toString();
        if(list.contains(unit))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(unit);

        String scsl = detailrow.get("scsl");//生产单位需求量
        if(scsl.length()>0 &&  (temp = checkNumber(scsl, "第"+row+"行生产数量")) != null)
          return temp;
        String sl = detailrow.get("sl");//计量单位需求量
        if(sl.equals(""))
          return showJavaScript("alert('第"+row+"行数量不能为空');");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
      }
      return null;
    }
  }
  /**
   *  物料增加操作

  class Materail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();
      EngineDataSet dsMaterail = getMaterailTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String gzzgzlid = dsMasterTable.getValue("gzzgzlid");
      dsMaterail.insertRow(false);
      dsMaterail.setValue("gzzgzlid", isMasterAdd ? "" : gzzgzlid);
      dsMaterail.post();
      d_GroupMaterail.add(new RowMap());
    }
  }
  /**
   *  物料删除操作

  class Materail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      boolean isdsPersonDel= String.valueOf(DETAIL_PERSON_DELETE).equals(action);
      EngineDataSet ds = getDetailTable();
      EngineDataSet dsMaterail = getMaterailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      d_GroupMaterail.remove(rownum);
      dsMaterail.goToRow(rownum);
      dsMaterail.deleteRow();
    }
  }
     */
  /**
   *  强制完成触发事件
   */
  class Complete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsMasterTable.goToRow(row);
      dsMasterTable.setValue("zt", "8");
      dsMasterTable.post();
      dsMasterTable.saveChanges();
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
      boolean isdsPersonDel= String.valueOf(DETAIL_PERSON_DELETE).equals(action);
      EngineDataSet ds = getDetailTable();
      EngineDataSet dsPerson = getPersonDetailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      if(!isdsPersonDel){
        d_RowInfos.remove(rownum);
        ds.goToRow(rownum);
        ds.deleteRow();
      }
      else{
        d_PersonRowInfos.remove(rownum);
        dsPerson.goToRow(rownum);
        dsPerson.deleteRow();
        data.setMessage(showJavaScript("big_change()"));
      }
    }
  }
  /**
   * 得到用于查找生产加工单信息的bean
   * @param req WEB的请求
   * @return 返回用于查找生产加工单信息的bean
   */
  public ImportProcess getProcessGoodsBean(HttpServletRequest req)
    {
    if(importprocessBean == null)
      importprocessBean = ImportProcess.getInstance(req);
    return importprocessBean;
  }
  /**
   * 得到生产加工单主表一行信息的bean
   * @param req WEB的请求
   * @return 返回生产加工单主表一行信息的bean
   */
  public B_WorkLoad_Sel_Process getProcessMasterBean(HttpServletRequest req)
  {
    if(workloadSelProcessBean == null)
      workloadSelProcessBean = B_WorkLoad_Sel_Process.getInstance(req);
    return workloadSelProcessBean;
  }
  /**
   * 得到用于查找产品单价的bean
   * @param req WEB的请求
   * @return 返回用于查找产品单价的bean
   */
  public LookUp getTechnicsBean(HttpServletRequest req)
  {
    if(technicsBean == null){
      technicsBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_TECHNICS_PROCEDURE);
      technicsBean.regData(new String[]{});
    }
    return technicsBean;
  }
  /**
   * 得到规格属性的bean
   * @param req WEB的请求
   * @return 返回规格属性的bean
   */
  public LookUp getPropertyBean(HttpServletRequest req)
  {
    if(propertyBean == null)
      propertyBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_SPEC_PROPERTY);
    return propertyBean;
  }
}

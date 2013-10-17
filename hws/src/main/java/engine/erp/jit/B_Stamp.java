package engine.erp.jit;

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
import engine.erp.produce.B_WorkLoad_Sel_Process;

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
 * <p>Title: 印花记录</p>
 * <p>Description: 印花记录<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @version 1.0
 */

public final class B_Stamp extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";
  public  static final String DETAIL_SELECT_PROCESS = "10021";
  public  static final String ONCHANGE = "10031";
  public  static final String GXMC_ONCHANGE = "10731";
  public  static final String DEPTCHANGE = "11731";
  public  static final String GZZCHANGE = "11732";
  public  static final String SINGLE_SEL_PROCESS = "10041";//单选生产加工单主表操作
  public  static final String SINGLE_SELECT_PRODUCT = "10091";//单选产品操作
  public  static final String PRODUCT_ONCHANGE = "14591";//输入产品触发事件
  public  static final String COMPLETE = "10011";//强制完成触发事件
  public  static final String REPORT = "2000";//报表追踪操作
  public  static final String CANCEL_APPROVE = "12345";//取消审批
  public  static final String DRAW_SINGLE_PROCESSTASK = "12346";//流转单引入任务单事件
  public  static final String PERSONCHANGE = "12347";//人员更改触发的事件

  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_stamp WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM sc_stamp where ?  ORDER BY stampid DESC";
  private static final String MASTER_EDIT_SQL    = "SELECT * FROM sc_stamp where stampid=? ";
  //报表调用工人工作量的SQL
  private static final String MASTER_REPORT_SQL    = "SELECT * FROM sc_stamp WHERE stampid =";

  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_stampmx WHERE 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sc_stampmx WHERE stampid ='?' ";


  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsHourWageInfoTable  = null;//计时工资设置表数据集

  private LookUp technicsBean = null; //工艺路线信息的bean的引用, 用于提取工艺路线信息

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_stamp");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_stampmx");
  public HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "sc_stamp", "sc_stamp");

  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isDetailAdd = false;    //从表是否在增加状态
  public  boolean isApprove = false;     //是否在审批状态

  public boolean isReport = false; // 从表是否在报表引用状态

  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public ImportProcess  importprocessBean = null;//引入加工单的bean的引用, 用于提取引入加工单信息
  public B_WorkLoad_Sel_Process  workloadSelProcessBean = null;//加工单主表信息BEAN的引用

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String gzlid = null;
  private String lx = null;
  public String SC_STORE_UNIT_STYLE = null;//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  public String SC_PRODUCE_UNIT_STYLE =null;//1=强制换算,0=仅空值时换算
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  private User user = null;
  private String stampid = "";
  //private String personid = null;
  //private String rq = null;
  /**
   * 工作量列表（按工人输入）的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回工作量列表（按工人输入）的实例
   */
  public static B_Stamp getInstance(HttpServletRequest request)
  {
    B_Stamp B_StampBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_StampBean";
      B_StampBean = (B_Stamp)session.getAttribute(beanName);
      if(B_StampBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        B_StampBean = new B_Stamp();
        B_StampBean.qtyFormat = loginBean.getQtyFormat();
        B_StampBean.sumFormat = loginBean.getSumFormat();
        B_StampBean.priceFormat = loginBean.getPriceFormat();

        B_StampBean.fgsid = loginBean.getFirstDeptID();
        B_StampBean.loginDept = loginBean.getDeptID();
        B_StampBean.loginId = loginBean.getUserID();
        B_StampBean.loginName = loginBean.getUserName();
        B_StampBean.user = loginBean.getUser();
        B_StampBean.SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和换算单位是否强制换算
        B_StampBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//计量单位和生产单位是否强制换算
        B_StampBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        B_StampBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        //B_StampBean.lx = "1";
        //设置格式化的字段
        B_StampBean.dsDetailTable.setColumnFormat("work_hour", B_StampBean.qtyFormat);
        B_StampBean.dsDetailTable.setColumnFormat("work_price", B_StampBean.priceFormat);
        B_StampBean.dsDetailTable.setColumnFormat("work_wage", B_StampBean.sumFormat);
        B_StampBean.dsDetailTable.setColumnFormat("over_hour", B_StampBean.qtyFormat);
        B_StampBean.dsDetailTable.setColumnFormat("over_price", B_StampBean.priceFormat);
        B_StampBean.dsDetailTable.setColumnFormat("over_wage", B_StampBean.sumFormat);
        B_StampBean.dsDetailTable.setColumnFormat("night_hour", B_StampBean.qtyFormat);
        B_StampBean.dsDetailTable.setColumnFormat("night_price", B_StampBean.priceFormat);
        B_StampBean.dsDetailTable.setColumnFormat("night_wage", B_StampBean.sumFormat);
        B_StampBean.dsDetailTable.setColumnFormat("bounty", B_StampBean.sumFormat);
        B_StampBean.dsDetailTable.setColumnFormat("amerce", B_StampBean.sumFormat);
        B_StampBean.dsMasterTable.setColumnFormat("sc_stampmx", B_StampBean.sumFormat);
        //B_StampBean.dsMasterTable.setColumnFormat("zjjgz", B_StampBean.priceFormat);
        session.setAttribute(beanName, B_StampBean);
      }
    }
    return B_StampBean;
  }

  /**
   * 构造函数
   */
  private B_Stamp()
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

    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"djh"}, new String[]{"SELECT pck_base.billNextCode('sc_stamp','djh') from dual"}));
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"stampid"}, new String[]{"s_sc_stamp"}));
    //dsMasterTable.setSort(new SortDescriptor("", new String[]{"djh"}, new boolean[]{true}, null, 0));sc_stamp
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"stampmxid"}, new String[]{"s_sc_stampmx"}));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());

    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(REPORT), new Report());//报表引用事件
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
    if(dsHourWageInfoTable != null){
      dsHourWageInfoTable.close();
      dsHourWageInfoTable = null;
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
        m_RowInfo.put("wage_date", today);
        m_RowInfo.put("zdrq", today);//制单日期
        m_RowInfo.put("zdr", loginName);//操作员
        m_RowInfo.put("zdrid", loginId);
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
      detailRow.put("scjhid", rowInfo.get("scjhid_"+i));//产品
      detailRow.put("yzsl", formatNumber(rowInfo.get("yzsl_"+i), qtyFormat));
      detailRow.put("sysl", formatNumber(rowInfo.get("sysl_"+i), priceFormat));
      detailRow.put("jhrq", formatNumber(rowInfo.get("jhrq_"+i), priceFormat));
      detailRow.put("bz", formatNumber(rowInfo.get("bz_"+i), qtyFormat));
      detailRow.put("djh", formatNumber(rowInfo.get("djh_"+i), priceFormat));
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
    String stampid = dsMasterTable.getValue("stampid");
    String SQL = isMasterAdd ? "-1" : stampid;
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
      isReport = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      table.getWhereInfo().clearWhereValues();
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      isDetailAdd = false;
      //
      //初始化时不显示已完成的单据
      String SQL = "";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{" 1=1 "});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      B_WageFormula.getInstance(request).readyExpressions();
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
      //gzlid = dsMasterTable.getValue("gzlid");
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
      isMasterAdd = String.valueOf(ADD).equals(action);
      isDetailAdd = false;
      isReport = false;
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();

        stampid = dsMasterTable.getValue("stampid");
        //personid = dsMasterTable.getValue("personid");
        //rq = dsMasterTable.getValue("rq");
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
      String id = request.getParameter("gzlid");
      String SQL = MASTER_REPORT_SQL+id;
      dsMasterTable.setQueryString(SQL);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();

      gzlid = dsMasterTable.getValue("gzlid");
      B_WageFormula.getInstance(request).readyExpressions();
      //打开从表
      openDetailTable(false);

      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
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


      if(isMasterAdd){
        ds.insertRow(false);
        stampid = dataSetProvider.getSequence("s_sc_stamp");
        //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh','a') from dual"}));
        ds.setValue("stampid", stampid);
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("stampid", stampid);

        detail.setValue("scjhid" ,detailrow.get("scjhid"));
        detail.setValue("yzsl",detailrow.get("yzsl"));
        detail.setValue("sysl",detailrow.get("sysl"));
        detail.setValue("jhrq", detailrow.get("jhrq"));
        detail.setValue("bz", detailrow.get("bz"));
        detail.setValue("djh", detailrow.get("djh"));

        detail.post();
        detail.next();
      }
      //保存主表数据
      ds.setValue("yhzsl", rowInfo.get("yhzsl"));
      ds.setValue("xczsl", rowInfo.get("xczsl"));
      ds.setValue("lczsl", rowInfo.get("lczsl"));
      ds.setValue("dtan", rowInfo.get("dtan"));
      ds.setValue("bgad", rowInfo.get("bgad"));
      ds.setValue("putn", rowInfo.get("putn"));
      ds.setValue("lsr", rowInfo.get("lsr"));
      ds.setValue("memo", rowInfo.get("memo"));


      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);


      isMasterAdd = false;
      masterRow = ds.getInternalRow();//2004-3-30 14:53 新增 如果是保存按钮行为完成后定位数据集 yjg
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);

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
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
/*
        String personid = detailrow.get("personid");
        if((temp = checkNumber(personid, "第"+row+"行人员")) != null)
          return temp;
        String work_hour = detailrow.get("work_hour");
        if((temp = checkNumber(work_hour, "第"+row+"行计时工时")) != null)
          return temp;
        //if(work_hour.length()>0 && work_hour.equals("0"))
        //  return showJavaScript("alert('第"+row+"行计时工时不能为零！');");

        String work_price = detailrow.get("work_price");
        if((temp = checkNumber(work_price, "第"+row+"行计时单价")) != null)
          return temp;

*/
      }
      return null;
    }

    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo() throws Exception
    {
      RowMap rowInfo = getMasterRowinfo();

      /*
      String temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择车间！');");

      String wage_date = rowInfo.get("wage_date");
      if(wage_date.equals(""))
        return showJavaScript("alert('日期不能为空！');");
      else if(!isDate(wage_date))
        return showJavaScript("alert('非法日期！');");

      String tot_hour_wage = rowInfo.get("tot_hour_wage");
      if((temp = checkNumber(tot_hour_wage, "总工资")) != null)
        return temp;
      if(tot_hour_wage.length()>0 && tot_hour_wage.equals("0"))
        return showJavaScript("alert('总工资不能为零！');");
*/
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
   *  从表增加操作(增加一个空白行)
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
      String stampid = dsMasterTable.getValue("stampid");
      detail.insertRow(false);
      detail.setValue("stampid", isMasterAdd ? "-1" : stampid);
      detail.post();
      d_RowInfos.add(new RowMap());
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
}
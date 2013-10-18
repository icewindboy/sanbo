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
 * <p>Title: 计时工资</p>
 * <p>Description: 计时工资<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨建国
 * @version 1.0
 */

public final class HourlyWages extends BaseAction implements Operate
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

  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_hourwage WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM sc_hourwage where ? ? ORDER BY hourwage_ID DESC";
  private static final String MASTER_EDIT_SQL    = "SELECT * FROM sc_hourwage where hourwage_ID=? ";
  //报表调用工人工作量的SQL
  private static final String MASTER_REPORT_SQL    = "SELECT * FROM sc_hourwage WHERE hourwage_ID =";

  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_hourwage_emp WHERE 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sc_hourwage_emp WHERE hourwage_ID ='?' ";


  //计时工资设置 (sc_hourwage_inf)
  private static final String SC_HOURWAGE_INF_SQL = "SELECT * FROM sc_hourwage_inf where 1=1 ?  ";
  //取出属于指定车间,工作组的人员的工资数据.
  private static final String SC_PERSON_HOURWAGE_INF_SQL =
        "SELECT m.*, n.hour_unit, nvl(n.hour_wage,0) hour_wage, nvl(n.night_wage,0) night_wage, "
      + " nvl(n.over_wage,0) over_wage  FROM "
      + " ( "
      + "   SELECT c.deptid, d.* "
      + "   FROM sc_gzz c, sc_gzzry d "
      + "   WHERE  c.gzzid = d.gzzid "
      + "          and c.deptid = '?' "
      + "  ) m, sc_hourwage_inf n  "
      + " WHERE m.deptid = n.deptid(+) "
      + "      AND m.personid = n.personid(+) ? ";

  private static final String PROCESSTASK_DETAIL_SQL = "SELECT * FROM VW_DRAW_PROCESSTASKDETAIL WHERE rwdid = '?' ";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsHourWageInfoTable  = null;//计时工资设置表数据集

  private LookUp technicsBean = null; //工艺路线信息的bean的引用, 用于提取工艺路线信息

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_hourwage");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_hourwage_emp");
  public HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "sc_hourwage", "sc_hourwage");

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
  private String hourwage_ID = "";
  //private String personid = null;
  //private String rq = null;
  /**
   * 工作量列表（按工人输入）的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回工作量列表（按工人输入）的实例
   */
  public static HourlyWages getInstance(HttpServletRequest request)
  {
    HourlyWages hourlyWagesBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "hourlyWagesBean";
      hourlyWagesBean = (HourlyWages)session.getAttribute(beanName);
      if(hourlyWagesBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        hourlyWagesBean = new HourlyWages();
        hourlyWagesBean.qtyFormat = loginBean.getQtyFormat();
        hourlyWagesBean.sumFormat = loginBean.getSumFormat();
        hourlyWagesBean.priceFormat = loginBean.getPriceFormat();

        hourlyWagesBean.fgsid = loginBean.getFirstDeptID();
        hourlyWagesBean.loginDept = loginBean.getDeptID();
        hourlyWagesBean.loginId = loginBean.getUserID();
        hourlyWagesBean.loginName = loginBean.getUserName();
        hourlyWagesBean.user = loginBean.getUser();
        hourlyWagesBean.SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和换算单位是否强制换算
        hourlyWagesBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//计量单位和生产单位是否强制换算
        hourlyWagesBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        hourlyWagesBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        //hourlyWagesBean.lx = "1";
        //设置格式化的字段
        hourlyWagesBean.dsDetailTable.setColumnFormat("work_hour", hourlyWagesBean.qtyFormat);
        hourlyWagesBean.dsDetailTable.setColumnFormat("work_price", hourlyWagesBean.priceFormat);
        hourlyWagesBean.dsDetailTable.setColumnFormat("work_wage", hourlyWagesBean.sumFormat);
        hourlyWagesBean.dsDetailTable.setColumnFormat("over_hour", hourlyWagesBean.qtyFormat);
        hourlyWagesBean.dsDetailTable.setColumnFormat("over_price", hourlyWagesBean.priceFormat);
        hourlyWagesBean.dsDetailTable.setColumnFormat("over_wage", hourlyWagesBean.sumFormat);
        hourlyWagesBean.dsDetailTable.setColumnFormat("night_hour", hourlyWagesBean.qtyFormat);
        hourlyWagesBean.dsDetailTable.setColumnFormat("night_price", hourlyWagesBean.priceFormat);
        hourlyWagesBean.dsDetailTable.setColumnFormat("night_wage", hourlyWagesBean.sumFormat);
        hourlyWagesBean.dsDetailTable.setColumnFormat("bounty", hourlyWagesBean.sumFormat);
        hourlyWagesBean.dsDetailTable.setColumnFormat("amerce", hourlyWagesBean.sumFormat);
        hourlyWagesBean.dsMasterTable.setColumnFormat("sc_hourwage_emp", hourlyWagesBean.sumFormat);
        //hourlyWagesBean.dsMasterTable.setColumnFormat("zjjgz", hourlyWagesBean.priceFormat);
        session.setAttribute(beanName, hourlyWagesBean);
      }
    }
    return hourlyWagesBean;
  }

  /**
   * 构造函数
   */
  private HourlyWages()
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

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"djh"}, new String[]{"SELECT pck_base.billNextCode('sc_hourwage','djh') from dual"}));
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"hourwage_ID"}, new String[]{"s_sc_hourwage"}));
    //dsMasterTable.setSort(new SortDescriptor("", new String[]{"djh"}, new boolean[]{true}, null, 0));sc_hourwage
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"houremp_ID"}, new String[]{"s_sc_hourwage_emp"}));

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
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(DETAIL_SELECT_PROCESS), new Detail_Select_Process());
    //addObactioner(String.valueOf(SINGLE_SEL_PROCESS), new Single_Select_Process());//单选加工单主表
    addObactioner(String.valueOf(SINGLE_SELECT_PRODUCT), new Single_Product_Add());//单选产品
    addObactioner(String.valueOf(COMPLETE), new Complete());//强制完成事件
    addObactioner(String.valueOf(REPORT), new Report());//报表引用事件
    addObactioner(String.valueOf(GZZCHANGE), new GzzChange());//工作组改变事件
    addObactioner(String.valueOf(DEPTCHANGE), new DeptChange());//工作组改变事件
    addObactioner(String.valueOf(PERSONCHANGE), new PersonChage());//工作组改变事件


    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(REPORT), new Approve());
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());//取消审批
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
   *改变工作组的触发事件:
   * 1. 须要把所有的此工作组中的人员取出来.当成明细资料插入到明细数据集中
   *  1.1 取出在工资设置功能中设置好的此车间此人员工资数据.
   */
 class GzzChange implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     putDetailInfo(data.getRequest());
     EngineDataSet detail = getDetailTable();
     RowMap rowInfo = getMasterRowinfo();
     if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
     String hourwage_ID = dsMasterTable.getValue("hourwage_ID");
     String deptid = rowInfo.get("deptid");
     String gzzid =  rowInfo.get("gzzid");
     String SQL = deptid.equals("")?"":" and m.deptid = " + deptid + (gzzid.equals("")?"":" and gzzid = " + gzzid +" order by m.ordernum ");
     SQL = combineSQL(SC_PERSON_HOURWAGE_INF_SQL, "?", new String[]{deptid, SQL});
     //EngineDataSet dsHourWageInfoTable = null;//零时任务单从表信息数据集
     if(dsHourWageInfoTable==null)
     {
       dsHourWageInfoTable = new EngineDataSet();
       setDataSetProperty(dsHourWageInfoTable,null);
     }
     dsHourWageInfoTable.setQueryString(SQL);
     if(!dsHourWageInfoTable.isOpen())
       dsHourWageInfoTable.openDataSet();
     else
       dsHourWageInfoTable.refresh();
     detail.deleteAllRows();
     d_RowInfos.clear();
     dsHourWageInfoTable.first();
     for (int i=0;i<dsHourWageInfoTable.getRowCount();i++)
     {
       String personid = dsHourWageInfoTable.getValue("personid");
       String work_price = dsHourWageInfoTable.getValue("hour_wage");
       String over_price = dsHourWageInfoTable.getValue("over_wage");
       String night_price = dsHourWageInfoTable.getValue("night_wage");
       detail.insertRow(false);
       detail.setValue("hourwage_ID", isMasterAdd?"-1":hourwage_ID);
       detail.setValue("houremp_ID","-1");
       detail.setValue("personid",personid);
       detail.setValue("work_price",work_price);
       detail.setValue("over_price",over_price);
       detail.setValue("night_price",night_price);
       detail.post();
       RowMap detailrow = new RowMap(detail);
       d_RowInfos.add(detailrow);
       dsHourWageInfoTable.next();
     }
   }
 }
  /*
   *  选择个人触发的事件须要取得页面上部门和工作组的值.依此条件找出person的数据.
   *
   */
  class PersonChage implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      RowMap detailrowUI = null;
      int row = Integer.parseInt(data.getParameter("rownum"));
      detailrowUI = (RowMap)d_RowInfos.get(row);
      EngineDataSet detail = getDetailTable();
      EngineDataSet dspersonHourWageInfoTable = null;//零时任务单从表信息数据集
      RowMap rowInfo = getMasterRowinfo();
      String deptid = rowInfo.get("deptid");
      String tmpdeptid = rowInfo.get("deptid");
      String gzzid =  rowInfo.get("gzzid");
      String personid =  detailrowUI.get("personid");
      String personidCondition = personid.equals("")?"":" and m.personid = " + personid;
      deptid = deptid.equals("")?"":" and m.deptid = " + deptid;
      gzzid =  gzzid.equals("")?"":" and gzzid = " + gzzid;
      String SQL = deptid + gzzid + personidCondition;
      SQL = combineSQL(SC_PERSON_HOURWAGE_INF_SQL, "?", new String[]{tmpdeptid,SQL});
      if(dspersonHourWageInfoTable==null)
      {
        dspersonHourWageInfoTable = new EngineDataSet();
        setDataSetProperty(dspersonHourWageInfoTable,null);
      }
      dspersonHourWageInfoTable.setQueryString(SQL);
      if(!dspersonHourWageInfoTable.isOpen())
        dspersonHourWageInfoTable.openDataSet();
      else
        dspersonHourWageInfoTable.refresh();
      //detail.deleteAllRows();
      //d_RowInfos.clear();
      dspersonHourWageInfoTable.first();
      for (int i=0;i<dspersonHourWageInfoTable.getRowCount();i++)
      {
        //String personid = dspersonHourWageInfoTable.getValue("personid");
        String work_price = dspersonHourWageInfoTable.getValue("hour_wage");
        String over_price = dspersonHourWageInfoTable.getValue("over_wage");
        String night_price = dspersonHourWageInfoTable.getValue("night_wage");
        //detail.insertRow(false);
        detailrowUI.put("houremp_ID","-1");
        detailrowUI.put("personid",personid);
        detailrowUI.put("work_price",work_price);
        detailrowUI.put("over_price",over_price);
        detailrowUI.put("night_price",night_price);
        dspersonHourWageInfoTable.next();
      }
    }
  }

  /*
   * 部门更改触发事件.
   * 1.首先判断是否此部门下面有工作组.
   * 2.如有工作组则不做任何动作.等待使用者选择工作组
   * 3.如没有则把此车间下的所有人员的信息全取过来.
   */
  class DeptChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet dspersonHourWageInfoTable = null;//零时任务单从表信息数据集
      RowMap rowInfo = getMasterRowinfo();
      String deptid = rowInfo.get("deptid");
      deptid = deptid.equals("")?"":" and deptid = " + deptid;
      String gzzid = "";//rowInfo.get("gzzid");
      String SQL = "select count(*) from sc_gzz where  1=1 ? ";
      SQL = combineSQL(SQL, "?", new String[]{deptid});
      String count = dataSetProvider.getSequence(SQL);
      //如果车间下面没有工作组的话.那么就取整个车间的人员作为明细数据插入到明细数据集中
      if (count.equals("0"))
      {
        SQL = deptid;
        SQL = combineSQL(SC_PERSON_HOURWAGE_INF_SQL, "?", new String[]{ rowInfo.get("deptid")});
        if(dsHourWageInfoTable==null)
        {
          dsHourWageInfoTable = new EngineDataSet();
          setDataSetProperty(dsHourWageInfoTable,null);
        }
        dsHourWageInfoTable.setQueryString(SQL);
        if(!dsHourWageInfoTable.isOpen())
          dsHourWageInfoTable.openDataSet();
        else
          dsHourWageInfoTable.refresh();
        detail.deleteAllRows();
        d_RowInfos.clear();
        dsHourWageInfoTable.first();
        for (int i=0;i<dsHourWageInfoTable.getRowCount();i++)
        {
          String personid = dsHourWageInfoTable.getValue("personid");
          String work_price = dsHourWageInfoTable.getValue("hour_wage");
          String over_price = dsHourWageInfoTable.getValue("over_wage");
          String night_price = dsHourWageInfoTable.getValue("night_wage");
          detail.insertRow(false);
          detail.setValue("houremp_ID","-1");
          detail.setValue("personid",personid);
          detail.setValue("work_price",work_price);
          detail.setValue("over_price",over_price);
          detail.setValue("night_price",night_price);
          detail.post();
          RowMap detailrow = new RowMap(detail);
          d_RowInfos.add(detailrow);
          dsHourWageInfoTable.next();
        }
      }
      data.setMessage(showJavaScript("deptChange()"));
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
      String personid = rowInfo.get("personid_"+i);
      detailRow.put("personid", rowInfo.get("personid_"+i));//产品
      detailRow.put("work_hour", formatNumber(rowInfo.get("work_hour_"+i), qtyFormat));//计时工时
      detailRow.put("work_price", formatNumber(rowInfo.get("work_price_"+i), priceFormat));//计时单价
      detailRow.put("work_wage", formatNumber(rowInfo.get("work_wage_"+i), priceFormat));//计时金额
      detailRow.put("over_hour", formatNumber(rowInfo.get("over_hour_"+i), qtyFormat));//加班工时
      detailRow.put("over_price", formatNumber(rowInfo.get("over_price_"+i), priceFormat));//加班单价
      detailRow.put("over_wage", formatNumber(rowInfo.get("over_wage_"+i), priceFormat));//加班金额
      detailRow.put("night_hour", formatNumber(rowInfo.get("night_hour_"+i), qtyFormat));//夜班工时
      detailRow.put("night_price", formatNumber(rowInfo.get("night_price_"+i), priceFormat));//夜班单价
      detailRow.put("night_wage", formatNumber(rowInfo.get("night_wage_"+i), priceFormat));//夜班金额
      detailRow.put("bounty", formatNumber(rowInfo.get("bounty_"+i), priceFormat));//夜班金额
      detailRow.put("amerce", formatNumber(rowInfo.get("amerce_"+i), priceFormat));//夜班金额
      detailRow.put("hour_wage", formatNumber(rowInfo.get("hour_wage_"+i), priceFormat));//夜班金额
      detailRow.put("memo", rowInfo.get("memo_"+i));//计件工时
      personid = detailRow.get("personid");
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
    String hourwage_id = dsMasterTable.getValue("hourwage_ID");
    String SQL = isMasterAdd ? "-1" : hourwage_id;
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid")});
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
      isApprove= false;
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();

        hourwage_ID = dsMasterTable.getValue("hourwage_ID");
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
        hourwage_ID = dataSetProvider.getSequence("s_sc_hourwage");
        //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh','a') from dual"}));
        ds.setValue("hourwage_ID", hourwage_ID);

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
          detail.setValue("hourwage_ID", hourwage_ID);
        double sl = detailrow.get("sl").length()>0 ? Double.parseDouble(detailrow.get("sl")) : 0;
        String personid = detailrow.get("personid");
        String work_hour = detailrow.get("work_hour");
        String work_price = detailrow.get("work_price");
        String work_wage  = detailrow.get("work_wage");
        String over_hour = detailrow.get("over_hour");
        String over_wage = detailrow.get("over_wage");
        String over_price = detailrow.get("over_price");
        String night_hour  = detailrow.get("night_hour");
        String night_wage = detailrow.get("night_wage");
        String night_price = detailrow.get("night_price");
        String bounty = detailrow.get("bounty");
        String amerce  = detailrow.get("amerce");
        String hour_wage = detailrow.get("hour_wage");
        String memo  = detailrow.get("memo");

        detail.setValue("personid" , personid);
        detail.setValue("work_hour",work_hour);
        detail.setValue("work_price",work_price);
        detail.setValue("work_wage", work_wage);
        detail.setValue("over_hour", over_hour);
        detail.setValue("over_wage", over_wage);
        detail.setValue("over_price", over_price);
        detail.setValue("night_hour", night_hour);
        detail.setValue("night_wage", night_wage);
        detail.setValue("night_price", night_price);
        detail.setValue("bounty" , bounty);
        detail.setValue("amerce" , amerce);
        detail.setValue("hour_wage", hour_wage);
        detail.setValue("memo" , memo);

        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        detail.next();
      }
      //保存主表数据
      ds.setValue("djh", rowInfo.get("djh"));
      ds.setValue("deptid", rowInfo.get("deptid"));//完工车间id
      ds.setValue("wage_date", rowInfo.get("wage_date"));//接收日期
      ds.setValue("gzzid", rowInfo.get("gzzid"));//完工车间班组
      ds.setValue("tot_hour_wage", rowInfo.get("tot_hour_wage"));//完工车间班组
      ds.setValue("zt", "0");

      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action)){
        //data.setMessage(showJavaScript("backList();"));
        isMasterAdd = false;
        masterRow = ds.getInternalRow();//2004-3-30 14:53 新增 如果是保存按钮行为完成后定位数据集 yjg
        initRowInfo(true, isMasterAdd, true);
        initRowInfo(false, isMasterAdd, true);
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
      ArrayList list = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);

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
        //if(work_price.length()>0 && work_price.equals("0"))
        //  return showJavaScript("alert('第"+row+"行计时单价不能为零！');");

        String work_wage = detailrow.get("work_wage");
        if((temp = checkNumber(work_wage, "第"+row+"行计时金额")) != null)
          return temp;
        //if(work_wage.length()>0 && work_wage.equals("0"))
        //  return showJavaScript("alert('第"+row+"行计时金额不能为零！');");

       String over_hour = detailrow.get("over_hour");
       //if((temp = checkNumber(over_hour, "第"+row+"行加班工时")) != null)
       //  return temp;
       if(over_hour.length()>0 && (temp = checkNumber(over_hour, "第"+row+"行加班工时")) != null)
          return temp;

       String over_wage = detailrow.get("over_wage");
       //if((temp = checkNumber(over_wage, "第"+row+"行加班金额")) != null)
       //  return temp;
       if(over_wage.length()>0 && (temp = checkNumber(over_wage, "第"+row+"行加班金额")) != null)
          return temp;

       String night_hour = detailrow.get("night_hour");
       //if((temp = checkNumber(night_hour, "第"+row+"行夜班工时")) != null)
       //  return temp;
       if(night_hour.length()>0 && (temp = checkNumber(night_hour, "第"+row+"行夜班工时")) != null)
          return temp;

       String night_wage = detailrow.get("night_wage");
       //if((temp = checkNumber(night_wage, "第"+row+"行夜班金额")) != null)
       // return temp;
       if(night_wage.length()>0 && (temp = checkNumber(night_wage, "第"+row+"行夜班金额")) != null)
          return temp;

       String bounty = detailrow.get("bounty");
       if(bounty.length()>0&&(temp = checkNumber(bounty, "第"+row+"行奖励")) != null)
         return temp;
       //if(bounty.length()>0 && bounty.equals("0"))
       //   return showJavaScript("alert('第"+row+"行奖励不能为零！');");

       String amerce = detailrow.get("amerce");
       if(amerce.length()>0&&(temp = checkNumber(amerce, "第"+row+"行罚款")) != null)
         return temp;
       //if(amerce.length()>0 && amerce.equals("0"))
       //   return showJavaScript("alert('第"+row+"行罚款不能为零！');");

       String hour_wage = detailrow.get("hour_wage");
       if((temp = checkNumber(hour_wage, "第"+row+"行总工资")) != null)
         return temp;
       //if(hour_wage.length()>0 && hour_wage.equals("0"))
       //   return showJavaScript("alert('第"+row+"行总工资不能为零！');");
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
      String temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择车间！');");
      /*String tempgzz = rowInfo.get("gzzid");
      if(tempgzz.equals(""))
        return showJavaScript("alert('请选择工作班组！');");
      */
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
   *  查询操作
   */
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      table.getWhereInfo().setWhereValues(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
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
        new QueryColumn(master.getColumn("cjlzdh"), null, null, null),
        new QueryColumn(master.getColumn("wgrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("wgrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("jsrq"), null, null, null, "c", ">="),
        new QueryColumn(master.getColumn("jsrq"), null, null, null, "d", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "="),//状态
        new QueryColumn(master.getColumn("cjlzdid"), "VW_CJLZD_QUERY", "cjlzdid", "gg", "gg", "like"),//从表品名
        new QueryColumn(master.getColumn("cjlzdid"), "VW_CJLZD_QUERY", "cjlzdid", "cpbm", "cpbm", "="),//从表产品编码
        new QueryColumn(master.getColumn("cjlzdid"), "VW_CJLZD_QUERY", "cjlzdid", "product", "product", "like"),//从表品名
      });
      isInitQuery = true;
    }
  }
  /**
  *  根据加工单从表增加操作
  */
 class Detail_Select_Process implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     //保存输入的明细信息
     putDetailInfo(data.getRequest());
     RowMap rowInfo = getMasterRowinfo();

     String mutiprocess = m_RowInfo.get("mutiprocess");
     if(mutiprocess.length() == 0)
       return;

     //实例化查找数据集的类
     EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
     String[] jgdmxID = parseString(mutiprocess,",");
     if(!isMasterAdd)
       dsMasterTable.goToInternalRow(masterRow);
     String gzlid = dsMasterTable.getValue("gzlid");
     for(int i=0; i < jgdmxID.length; i++)
     {
       if(jgdmxID[i].equals("-1"))
         continue;
       RowMap detailrow = null;
       locateGoodsRow.setValue(0, jgdmxID[i]);
       if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
       {
         RowMap importProcessRow = getProcessGoodsBean(req).getLookupRow(jgdmxID[i]);
         double sl = importProcessRow.get("sl").length()>0 ? Double.parseDouble(importProcessRow.get("sl")) : 0;
         //double hsbl = importProcessRow.get("hsbl").length()>0 ? Double.parseDouble(importProcessRow.get("hsbl")) : 0;
         dsDetailTable.insertRow(false);
         dsDetailTable.setValue("gzlmxid", "-1");
         dsDetailTable.setValue("jgdmxid",jgdmxID[i]);
         dsDetailTable.setValue("cpid", importProcessRow.get("cpid"));
         dsDetailTable.setValue("sl", importProcessRow.get("sl"));
         //dsDetailTable.setValue("hssl", formatNumber(String.valueOf(hsbl==0 ? 0 : sl/hsbl), qtyFormat));
         dsDetailTable.setValue("gzlid", isMasterAdd ? "-1" : gzlid);
         dsDetailTable.setValue("dmsxid", importProcessRow.get("dmsxid"));
         dsDetailTable.post();
         //创建一个与用户相对应的行
         detailrow = new RowMap(dsDetailTable);
         d_RowInfos.add(detailrow);
       }
       data.setMessage(showJavaScript("big_change()"));
     }
   }
  }
  /**
   * 从表增加操作（单选产品）
   */
  class Single_Product_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      int row = Integer.parseInt(data.getParameter("rownum"));
      String singleIdInput = m_RowInfo.get("singleIdInput_"+row);
      if(singleIdInput.equals(""))
        return;

      //实例化查找数据集的类
      String cpid = singleIdInput;
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String gzlid = dsMasterTable.getValue("gzlid");
      dsDetailTable.goToRow(row);
      RowMap detailrow = null;
      detailrow = (RowMap)d_RowInfos.get(row);
      detailrow.put("gzlmxid", "-1");
      detailrow.put("cpid", cpid);
      detailrow.put("gzlid", isMasterAdd ? "-1" : gzlid);
    }
 }
  /**
   *  选择任务单主表，引入从表所有未加工信息

  class Single_Select_Process implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String singleImportProcess = m_RowInfo.get("drawSingleProcessTask");
      if(singleImportProcess.equals(""))
        return;
      String SQL = PROCESS_DETAIL_SQL+singleImportProcess;
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

      RowMap processMasterRow = getProcessMasterBean(req).getLookupRow(singleImportProcess);
      rowInfo.put("deptid", processMasterRow.get("deptid"));
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String gzlid = dsMasterTable.getValue("gzlid");
      for(int i=0; i<tempProcessData.getRowCount(); i++)
      {
        tempProcessData.goToRow(i);
        //double hsbl = tempProcessData.getValue("hsbl").length()>0 ? Double.parseDouble(tempProcessData.getValue("hsbl")) : 0;
        String jgdmxid = tempProcessData.getValue("jgdmxid");
        String cpid = tempProcessData.getValue("cpid");
        locateGoodsRow.setValue(0, jgdmxid);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          double sl = tempProcessData.getValue("sl").length()>0 ? Double.parseDouble(tempProcessData.getValue("sl")) : 0;//加工单需要的加工数量
          double ypgzl = tempProcessData.getValue("ypgzl").length()>0 ? Double.parseDouble(tempProcessData.getValue("ypgzl")) : 0;//加工单中已加工量
          double wpgzl = sl-ypgzl>0 ? sl-ypgzl : 0;
          if(wpgzl==0)
            continue;
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("gzlmxid", "-1");
          dsDetailTable.setValue("jgdmxid",jgdmxid);
          dsDetailTable.setValue("cpid", cpid);
          dsDetailTable.setValue("sl", String.valueOf(wpgzl));
          //dsDetailTable.setValue("hssl", formatNumber(String.valueOf(hsbl==0 ? 0 : wpgzl/hsbl), qtyFormat));
          dsDetailTable.setValue("gylxid", tempProcessData.getValue("gylxid"));
          dsDetailTable.setValue("gzlid", isMasterAdd ? "-1" : gzlid);
          dsDetailTable.setValue("dmsxid", tempProcessData.getValue("dmsxid"));
          dsDetailTable.post();
          //创建一个与用户相对应的行
          RowMap detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
        data.setMessage(showJavaScript("big_change()"));
      }
    }
  }
  */
  /**
   *  选择任务单主表.得到的任务单id定位出任务单明细资料,然后把任务单明细资料数据引入到此张流转单中来.
   */
  class Draw_Single_ProcessTask implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String mulitProcessTask = m_RowInfo.get("drawSingleProcessTask");
      if(mulitProcessTask.equals(""))
        return;
      String[] singleImportProcessTask = parseString(mulitProcessTask,",");
      for(int k=0; k < singleImportProcessTask.length; k++)
      {
        String processTask = singleImportProcessTask[k];
        String SQL = combineSQL(PROCESSTASK_DETAIL_SQL, "?", new String[]{processTask});
        EngineDataSet tempProcessData = null;//零时任务单从表信息数据集
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

        //实例化查找数据集的类
        EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String cjlzdid = dsMasterTable.getValue("cjlzdid");
        for(int i=0; i<tempProcessData.getRowCount(); i++)
        {
          tempProcessData.goToRow(i);
          String jgdmxid = tempProcessData.getValue("rwdmxid");
          locateGoodsRow.setValue(0, jgdmxid);
          if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
          {
            double sl = tempProcessData.getValue("sl").length()>0 ? Double.parseDouble(tempProcessData.getValue("sl")) : 0;//加工单需要的加工数量
            double yjgl = tempProcessData.getValue("yjgl").length()>0 ? Double.parseDouble(tempProcessData.getValue("yjgl")) : 0;//加工单需要的加工数量
            double wjgl = sl-yjgl>0 ? sl-yjgl : 0;
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("cjlzdmxid", "-1");
            dsDetailTable.setValue("jgdmxid",jgdmxid);
            dsDetailTable.setValue("cpid", tempProcessData.getValue("cpid"));
            dsDetailTable.setValue("sl", String.valueOf(wjgl));
            dsDetailTable.setValue("cjlzdid", isMasterAdd ? "-1" : cjlzdid);
            dsDetailTable.setValue("dmsxid", tempProcessData.getValue("dmsxid"));
            dsDetailTable.post();
            //创建一个与用户相对应的行
            RowMap detailrow = new RowMap(dsDetailTable);
            d_RowInfos.add(detailrow);
          }
          //data.setMessage(showJavaScript("big_change()"));
        }
      }
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
        String hourwage_ID = dsMasterTable.getValue("hourwage_ID");
        detail.insertRow(false);
        detail.setValue("hourwage_ID", isMasterAdd ? "-1" : hourwage_ID);
        detail.post();
        d_RowInfos.add(new RowMap());
      }
    }
    /**
     * 添加到审核列表的操作类
     */
    class Add_Approve implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        String num = data.getParameter("rownum");
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
        String content = dsMasterTable.getValue("djh");
        String deptid = dsMasterTable.getValue("deptid");
        approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "sc_hourwage", content,deptid);
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
        masterProducer.init(request, loginId);
        detailProducer.init(request, loginId);
        //得到request的参数,值若为null, 则用""代替
        isReport = String.valueOf(REPORT).equals(action);
        String id = data.getParameter("id", "");
        if(isReport){
          isApprove = false;
          id = data.getParameter("id");//得到报表传递的参数既收发单据主表ID
        }
        else{
          isApprove = true;//审批操作
          id = data.getParameter("id", "");
        }
        String sql = combineSQL(MASTER_EDIT_SQL, "?", new String[]{id});

        dsMasterTable.setQueryString(sql);
        if(dsMasterTable.isOpen()){
          dsMasterTable.readyRefresh();
          dsMasterTable.refresh();
        }
        else
          dsMasterTable.openDataSet();
        hourwage_ID = dsMasterTable.getValue("hourwage_ID");

        openDetailTable(false);
        initRowInfo(true, false, true);
        initRowInfo(false, false, true);

      }
    }
    /**
     * 取消审批的操作类
     */
    class Cancel_Approve implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
        approve.cancelAprove(dsMasterTable, dsMasterTable.getRow(), "sc_hourwage");
      }
    }
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
        EngineDataSet ds = getDetailTable();
        int rownum = Integer.parseInt(data.getParameter("rownum"));
        //删除临时数组的一列数据
        d_RowInfos.remove(rownum);
        ds.goToRow(rownum);
        ds.deleteRow();
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
  }

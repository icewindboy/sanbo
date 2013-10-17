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
import engine.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;


/**
 * <p>Title: 生产-工人工资明细</p>
 * <p>Description: 生产-工人工资明细<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */
public final class B_PersonalWage  extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  public static final String MASTER_DEL = "1010";
  public static final String REPORT = "2000";//工人工资统计报表追踪触发事件
  public static final String SYNC   = "2001";//同步工作组人员
  public static final String OVER   = "2002";//完成

  //抽取在自制收货单列表
  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_grgz WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM sc_grgz  where ? ? ORDER BY djrq DESC";

  //自制收货单人员SQL
  private static final String PERSONSTRUCT_SQL = "SELECT * FROM sc_grgzry WHERE 1<>1";
  private static final String PERSON_SQL    = "SELECT * FROM sc_grgzry WHERE grgzid='?' ";
  //
  private static final String WAGE_MAST_SYNC = "SELECT * FROM sc_grgz WHERE ? ? AND (ztbj IS NULL OR ztbj<>8)";
  private static final String WAGE_DETAIL_DEL
      = "DELETE FROM sc_grgzry WHERE grgzid IN(SELECT grgzid FROM sc_grgz WHERE ? ? AND (ztbj IS NULL OR ztbj<>8))";
  private static final String WORK_TEAM_PERSON = "SELECT * FROM sc_gzzry";

  private static final String REPORT_PURSUANT_SQL    = "SELECT * FROM sc_grgz WHERE grgzid='?' ";
  //工资未结算日期
  private static final String WAGE_UNBALANCE_DATE
     = "SELECT MAX(t.nf)||'-'||(MAX(t.yf)+1)||'-01' FROM rl_gzkxzb t WHERE t.sfjz=1";

  private EngineDataSet dsSelfMaster = new EngineDataSet();
  private EngineDataSet dsPersonWage = new EngineDataSet();

  //数据库维护查询条件
  public  HtmlTableProducer table = new HtmlTableProducer(dsSelfMaster, "sc_grgz", "sc_grgz");

  private RowMap rowInfo = new RowMap();//保存页面提交时信息
  private ArrayList d_RowInfos = null; //BOM替换件表里多行记录的引用

  private long    masterRow = -1;         //保存主表修改操作的行记录指针

  public  String retuUrl = null;
  private boolean isInitQuery = false;
  public  String loginName = ""; //登录员工的姓名
  private String fgsID = null;   //分公司ID
  private User user = null;
  private String grgzid = "";   //
  public boolean isReport = false;
  private String qtyFormat = null,priceFormat=null;
  /**
   * 定义固定查询类
   */
  private QueryFixedItem fixedQuery = new QueryFixedItem();

  public static B_PersonalWage getInstance(HttpServletRequest request)
  {
    B_PersonalWage personalWageBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "personalWageBean";
      personalWageBean = (B_PersonalWage)session.getAttribute(beanName);
      if(personalWageBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        personalWageBean = new B_PersonalWage();
        personalWageBean.fgsID = loginBean.getFirstDeptID();
        personalWageBean.loginName = loginBean.getUserName();
        personalWageBean.qtyFormat = loginBean.getQtyFormat();
        personalWageBean.priceFormat=loginBean.getPriceFormat();
        personalWageBean.user = loginBean.getUser();
        personalWageBean.dsPersonWage.setColumnFormat("jjgz", personalWageBean.priceFormat);
        session.setAttribute(beanName, personalWageBean);
      }
    }
    return personalWageBean;
  }
  /**
   * 构造函数
   */
  private B_PersonalWage()
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
    setDataSetProperty(dsPersonWage, PERSONSTRUCT_SQL);
    setDataSetProperty(dsSelfMaster, MASTER_STRUT_SQL);
    dsPersonWage.setSequence(new SequenceDescriptor(new String[]{"grgzryid"}, new String[]{"S_SC_GRGZRY"}));

    addObactioner(String.valueOf(INIT), new B_PersonalWage_Init());
    addObactioner(String.valueOf(POST), new B_PersonalWage_Post());//
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
    addObactioner(String.valueOf(ADD), new B_PersonalWage_Add());
    addObactioner(String.valueOf(DEL), new B_PersonalWage_Del());
    addObactioner(String.valueOf(EDIT), new Master_Edit());
    addObactioner(String.valueOf(MASTER_DEL), new Master_Delete());
    addObactioner(String.valueOf(REPORT), new Report_Pursuant());//报表追踪
    addObactioner(SYNC, new SyncPerson());
    addObactioner(OVER, new Over());
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
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
/**
 *
 * 得到网页所填信息
 * */
  public  RowMap getRowinfo()
  {
    return rowInfo;
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsPersonWage != null){
      dsPersonWage.close();
      dsPersonWage = null;
    }
    if(dsSelfMaster != null){
      dsSelfMaster.close();
      dsSelfMaster = null;
    }
    rowInfo = null;
    d_RowInfos = null;
    log = null;
  }
  private final void putDetailInfo(HttpServletRequest request)
  {
    rowInfo.put(request);
    //保存网页的所有信息
    RowMap drowInfo=new RowMap();
    int rownum = dsPersonWage.getRowCount();
    dsPersonWage.first();
    for(int i=0; i<rownum; i++)
    {
      drowInfo = (RowMap)d_RowInfos.get(i);
      drowInfo.put("personid",rowInfo.get("personid_"+i));
      drowInfo.put("bl",rowInfo.get("bl_"+i));
      drowInfo.put("jjgz", rowInfo.get("jjgz_"+i));
      dsPersonWage.next();
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
  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /*得到替换件表对象*/
  public final EngineDataSet getDetailTable()
  {
    //if(!dsPersonWage.isOpen())
      //dsPersonWage.open();
    return dsPersonWage;
  }
  /*得到表对象*/
  public final EngineDataSet getMasterTable()
  {
    //if(!dsSelfMaster.isOpen())
      //dsSelfMaster.open();
    return dsSelfMaster;
  }
  /**
   * 初始化操作的触发类
   */
  class B_PersonalWage_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isReport = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      table.getWhereInfo().clearWhereValues();
      table.initWhereInfo(data.getRequest());
      String snf = dataSetProvider.getSequence("SELECT MAX(t.nf) FROM rl_gzkxzb t WHERE t.sfjz=1");
      String syf = dataSetProvider.getSequence("SELECT (MAX(t.yf)+1) FROM rl_gzkxzb t WHERE t.sfjz=1");

      String startDate = snf+"-"+syf+"-01";//dataSetProvider.getSequence(WAGE_UNBALANCE_DATE);
      if(syf.equals("13"))
      {
        snf = dataSetProvider.getSequence("SELECT (MAX(t.nf)+1) FROM rl_gzkxzb t WHERE t.sfjz=1");
        syf="01";
        startDate = snf+"-"+syf+"-01";
      }
      String endDate = null;
      if(startDate == null)
      {
        Date date = new Date();
        SimpleDateFormat dateForamt = new SimpleDateFormat("yyyy-MM");
        startDate = dateForamt.format(date)+"-01";
        dateForamt.applyPattern("yyyy-MM-dd");
        endDate = dateForamt.format(date);
      }
      else
      {
        SimpleDateFormat dateForamt = new SimpleDateFormat("yyyy-M-dd");
        Date date = dateForamt.parse(startDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        endDate = dateForamt.format(calendar.getTime());
        //60*60*24 =86400
      }
      table.getWhereInfo().putWhereValue("sc_grgz$djrq$a", startDate);
      table.getWhereInfo().putWhereValue("sc_grgz$djrq$b", endDate);
      table.getWhereInfo().putWhereValue("sc_grgz$ztbj", "0");
      String SQL = table.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptValue("deptid"), SQL});
      //打开在BOM表中所有可选件物料
      dsSelfMaster.setQueryString(SQL);
      dsSelfMaster.readyRefresh();
      if(dsPersonWage.isOpen() && dsPersonWage.getRowCount() > 0)
        dsPersonWage.empty();
    }
  }

  /**
   * 同步工作组人员
   */
  class SyncPerson implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String where = table.getWhereInfo().getWhereQuery();
      if(where.length() > 0)
        where = " AND "+where;

      String wageMastSql = combineSQL(WAGE_MAST_SYNC, "?", new String[]{user.getHandleDeptValue("deptid"), where});
      String wageDetailDel = combineSQL(WAGE_DETAIL_DEL, "?", new String[]{user.getHandleDeptValue("deptid"), where});
      EngineDataSet dsWageMast = new EngineDataSet();
      EngineDataSet dsWageDetail = new EngineDataSet();
      EngineDataSet dsTeamPerson = new EngineDataSet();
      setDataSetProperty(dsWageMast, wageMastSql);
      setDataSetProperty(dsWageDetail, PERSONSTRUCT_SQL);
      setDataSetProperty(dsTeamPerson, WORK_TEAM_PERSON);
      dsTeamPerson.setMasterLink(new MasterLinkDescriptor(dsWageMast,
          new String[]{"gzzid"}, new String[]{"gzzid"}, false, false, false));
      dsWageMast.openDataSet();
      dsWageDetail.openDataSet();
      dsTeamPerson.openDataSet();
      //WORK_TEAM_PERSON
      dsWageMast.first();
      int count = dsWageMast.getRowCount();
      for(int i=0; i<count; i++)
      {
        BigDecimal zgf = dsWageMast.getBigDecimal("zgf");
        dsTeamPerson.first();
        for(int j=0; j<dsTeamPerson.getRowCount(); j++)
        {
          BigDecimal baseNum = dsTeamPerson.getBigDecimal("ryjs");
          BigDecimal wage = baseNum.multiply(zgf);
          String personid = dsTeamPerson.getValue("personid");
          dsWageDetail.insertRow(false);
          dsWageDetail.setValue("personid", personid);
          dsWageDetail.setValue("personid", personid);
          dsWageDetail.setValue("grgzid", dsWageMast.getValue("grgzid"));
          dsWageDetail.setBigDecimal("jjgz", wage);
          dsWageDetail.setBigDecimal("bl", baseNum);
          dsWageDetail.setValue("grgzryid", dataSetProvider.getSequence("s_sc_grgzry"));
          dsWageDetail.post();
          dsTeamPerson.next();
        }
        dsWageMast.next();
      }
      dsTeamPerson.closeDataSet();
      dsWageMast.closeDataSet();
      dsWageDetail.setBeforeResolvedSQL(new String[]{wageDetailDel});
      dsWageDetail.saveChanges();
      dsWageDetail.closeDataSet();
    }
  }
  /**
  * 报表追踪的触发类
  */
 class Report_Pursuant implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest request = data.getRequest();
     //得到request的参数,值若为null, 则用""代替
     isReport = true;
     grgzid = data.getParameter("grgzid");
     String sql = combineSQL(REPORT_PURSUANT_SQL, "?", new String[]{grgzid});
     dsSelfMaster.setQueryString(sql);
     if(dsSelfMaster.isOpen()){
       dsSelfMaster.readyRefresh();
       dsSelfMaster.refresh();
     }
     else
       dsSelfMaster.open();

     //打开从表
     openDetailTable();

     initRowInfo(false, true);
   }
 }
  /**
  * 主表添加或修改操作的触发类
  * 进入从表页面操作
  * 打开从表传递两个参数父件ID和子件ID
  */
  class Master_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isReport = false;
      dsSelfMaster.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsSelfMaster.getInternalRow();
      grgzid = dsSelfMaster.getValue("grgzid");
      openDetailTable();
      initRowInfo(false, true);
      data.setMessage(showJavaScript("toDetail();"));
    }
  }

  /**
   * 完成工人工资,使之不能修改
   */
  class Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isReport = false;
      dsSelfMaster.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsSelfMaster.getInternalRow();
      grgzid = dsSelfMaster.getValue("grgzid");
      dsSelfMaster.setValue("ztbj", "8");
      dsSelfMaster.post();
      dsSelfMaster.saveChanges();
    }
  }

  /*打开从表*/
  public final void openDetailTable()
  {
    String SQL = combineSQL(PERSON_SQL, "?", new String[]{grgzid});

    dsPersonWage.setQueryString(SQL);
    if(!dsPersonWage.isOpen())
      dsPersonWage.open();
    else
      dsPersonWage.refresh();
  }
  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster,boolean isInit) throws java.lang.Exception
  {
    rowInfo.clear();
    rowInfo.put(getMasterTable());//把BOM中可选件的信息推入到RowMap中
    EngineDataSet dsDetail = getDetailTable();//把替换件信息存在ArrayList中
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
  /**
   * 保存操作的触发类
   */
  class B_PersonalWage_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest rq=data.getRequest();
      putDetailInfo(rq);
      String temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      RowMap detail = null;
      String count = null, cpid=null;
      EngineDataSet ds = getDetailTable();
      ds.first();
      for(int i=0; i<ds.getRowCount(); i++)
      {
        RowMap row = (RowMap)d_RowInfos.get(i);
        ds.setValue("bl", row.get("bl"));
        ds.setValue("personid", row.get("personid"));
        ds.setValue("jjgz", row.get("jjgz"));
        ds.post();
        ds.next();
      }
      ds.saveChanges();
      data.setMessage(showJavaScript("backList();"));
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo() throws Exception
    {
      String temp;
      String cpid = null;
      RowMap detailrow = null;
      for(int i=0;i<d_RowInfos.size();i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        String  personid=detailrow.get("personid");
        if(personid.equals(""))
          return showJavaScript("alert('请选择物料！');");
        String bl = detailrow.get("bl");
        if((temp = checkNumber(bl, "比例")) != null)
          return temp;
        String jjgz = detailrow.get("jjgz");
        if((temp = checkNumber(jjgz, "计件工资")) != null)
          return temp;
      }
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
      dsSelfMaster.goToInternalRow(masterRow);
      dsPersonWage.deleteAllRows();
      dsSelfMaster.deleteRow();
      dsSelfMaster.saveDataSets(new EngineDataSet[]{dsSelfMaster, dsPersonWage}, null);
      //
      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
  * <p>从表增加一行操作 </p>
  */
  class B_PersonalWage_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest rq=data.getRequest();
      putDetailInfo(rq);
      dsPersonWage.insertRow(false);
      dsPersonWage.setValue("grgzid",grgzid);
      dsPersonWage.setValue("grgzryid","-1");
      dsPersonWage.post();
      RowMap detailrow = new RowMap(dsPersonWage);
      d_RowInfos.add(detailrow);
    }
  }
  /**
   *  查询操作
   */
  class FIXED_SEARCH implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      table.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptValue("deptid"), SQL});
      dsSelfMaster.setQueryString(SQL);
      dsSelfMaster.setRowMax(null);
    }
  }
  /**
  * <p>从表删除操作 </p>
  */
  class B_PersonalWage_Del implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest rq=data.getRequest();
      putDetailInfo(rq);
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      d_RowInfos.remove(rownum);
      dsPersonWage.goToRow(rownum);
      dsPersonWage.deleteRow();
    }
  }
}

package engine.erp.store;


import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;
import engine.util.StringUtils;
import java.text.SimpleDateFormat;
import engine.html.HtmlTableProducer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title:库存管理——月末结账</p>
 * <p>Description: 库存管理——月末结账</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_ChalkOut extends BaseAction implements Operate
{

  public static final String FIXED_SEARCH = "1009";
  private static final String B_ChalkOut_SQL =   "SELECT * FROM kc_yj ORDER BY nf,yf ";
  private static final String MASTER_STRUT_SQL = "SELECT * FROM kc_yj WHERE 1<>1";
  private static final String COUNT_KC_YJ = "SELECT COUNT(*) FROM kc_yj WHERE fgsID = ? ";
  private static final String SELECT_KC_SFDJ_MIN_SFRQ = "SELECT to_char(MIN(d.sfrq), 'yyyy-mm-dd') FROM kc_sfdj d WHERE fgsid = ? ";
  private static final String SELECT_KC_YJ_WITH_NF= "SELECT * FROM kc_yj y WHERE (nf= ? or sfyj = 0) AND fgsid = ? ";
  private static final String PROCEDURESQL = "call PCK_STORE.doMonthBalance( ?, ?, ? )";
  private static final String SQLSELECTYJRQ = "select rq from jc_yjrq where yf = ? and rq >= ? ";
  private static final String CURRENTMONTHCLOSEDATE = "SELECT * FROM kc_yj WHERE nf = (SELECT MIN(nf) FROM kc_yj WHERE sfyj = 0) AND yf = (SELECT MIN(yf) FROM kc_yj WHERE sfyj = 0)";
  private EngineDataSet dsMonthClose = new EngineDataSet();
  public EngineDataSet dsCurrentMonthCloseDate = new EngineDataSet();
  private EngineRow locateRow = null;
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = true;
  private long    editrow = 0;
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginName = ""; //登录员工的姓名
  private String fgsid = null;   //分公司ID
  /**
   * 得到报价资料信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回报价资料信息的实例
   */
  public static B_ChalkOut getInstance(HttpServletRequest request)
  {
    B_ChalkOut B_ChalkOutBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_ChalkOutBean";
      B_ChalkOutBean = (B_ChalkOut)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(B_ChalkOutBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        B_ChalkOutBean = new B_ChalkOut();
        B_ChalkOutBean.fgsid = loginBean.getFirstDeptID();
        B_ChalkOutBean.loginId = loginBean.getUserID();
        B_ChalkOutBean.loginName = loginBean.getUserName();
        session.setAttribute(beanName, B_ChalkOutBean);
      }
    }
    return B_ChalkOutBean;
  }
  /**
   * 构造函数
   */
  private B_ChalkOut()
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
    setDataSetProperty(dsMonthClose, MASTER_STRUT_SQL);
    dsMonthClose.setSort(new SortDescriptor("", new String[]{"nf", "yf"}, new boolean[]{false, false}, null, 0));
    setDataSetProperty(dsCurrentMonthCloseDate, CURRENTMONTHCLOSEDATE);
    //添加操作的触发对象
    addObactioner(String.valueOf(INIT), new B_ChalkOut_Init());
    addObactioner(String.valueOf(POST), new B_ChalkOut_Post());
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
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsMonthClose.isOpen() && dsMonthClose.changesPending())
        dsMonthClose.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMonthClose != null){
      dsMonthClose.close();
      dsMonthClose = null;
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
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(isAdd){
      Date startDate = new Date();
      /*
      Date endDate = new Date(startDate.getYear(), startDate.getMonth()+1, 0);
      String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
      String endday = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
      rowInfo.put("ksrq", today);
      rowInfo.put("jsrq", endday);
      */
    }
    if(!isAdd)
      rowInfo.put(getOneTable());
  }

  private final void locateCurrentMonthCloseDate(){
    if ( !dsCurrentMonthCloseDate.isOpen())
      dsCurrentMonthCloseDate.open();
    else
      dsCurrentMonthCloseDate.refresh();
  }
  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsMonthClose.isOpen())
      dsMonthClose.open();
    return dsMonthClose;
  }
  /*得到一列的信息*/
  public final RowMap getRowinfo()
  {
    return rowInfo;
  }
  /**
   * 初始化操作的触发类
   */
  class B_ChalkOut_Init implements Obactioner
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
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //调用月结初始化过程.
      data.setMessage(monthCloseInitOperate());
      dsMonthClose.setRowMax(null);
      locateCurrentMonthCloseDate();
    }

    public String monthCloseInitOperate() throws Exception {
      String s = combineSQL(COUNT_KC_YJ, "?", new String[]{fgsid});
      String count = dataSetProvider.getSequence(combineSQL(COUNT_KC_YJ, "?", new String[]{fgsid}));
      Calendar calendar = new GregorianCalendar();//为了取得年份
      String currentYear = String.valueOf(calendar.get(Calendar.YEAR));
      int CurrentMonth = calendar.get(Calendar.MONTH);
      int CurrentDay = calendar.get(Calendar.DAY_OF_MONTH);
      String[] MonthAndDay = null;//为下面得到分割出来的年月做准备.
      if ( !count.equals("0") ) {
        dsMonthClose.setQueryString(combineSQL(SELECT_KC_YJ_WITH_NF, "?", new String[]{currentYear,fgsid}));
        if(dsMonthClose.isOpen())
          dsMonthClose.refresh();
        else
          dsMonthClose.openDataSet();
        //若记录数＝0，则插入今年的所有未月结记录.连续12个月的记录.
        if(dsMonthClose.getRowCount() == 0){
          for(int i = 1; i<=12; i++){
            dsMonthClose.insertRow(false);
            dsMonthClose.setValue("nf",currentYear);
            dsMonthClose.setValue("yf", Integer.toString(i));
            dsMonthClose.setValue("fgsID", fgsid);
            dsMonthClose.setValue("sfyj", "0");//是否月结.全部为0.0=初始化,1=月结
            dsMonthClose.post();
          }
          dsMonthClose.saveChanges();
        }
      }
      else
      {
        String minSfrqRecord = dataSetProvider.getSequence(combineSQL(SELECT_KC_SFDJ_MIN_SFRQ, "?", new String[]{fgsid}));
        if ( minSfrqRecord == null ){
          return showJavaScript("alert('不能月结！')");
        }
        else
        {
          int whichMonth = 0;
          MonthAndDay = StringUtils.parseString(minSfrqRecord, "-");
          String yjrq = dataSetProvider.getSequence(combineSQL(SQLSELECTYJRQ, "?", new String[]{MonthAndDay[1], MonthAndDay[2]}));
          if ( yjrq == null )
          {
            //年份变为下一年度
            if ( MonthAndDay[1].equals("12") )
            {  MonthAndDay[0] = String.valueOf(Integer.parseInt(MonthAndDay[0]) + 1);
               whichMonth = 1;
            }
            whichMonth = Integer.parseInt(MonthAndDay[1]) + 1;
          }
          else
          {
            whichMonth = Integer.parseInt(MonthAndDay[1]);
          }
          dsMonthClose.open();
          for(int i = whichMonth; i<=12; i++){
            dsMonthClose.insertRow(false);
            dsMonthClose.setValue("nf",MonthAndDay[0]);
            dsMonthClose.setValue("yf", Integer.toString(i));
            dsMonthClose.setValue("fgsID", fgsid);
            dsMonthClose.setValue("sfyj", "0");//是否月结.全部为0.0=初始化,1=月结
            dsMonthClose.post();
          }
          dsMonthClose.saveChanges();
        }
      }
      return "";//操作成功返回空串.
    }
  }
  /**
   * 保存操作的触发类.此类实现的是 月结过程 操作.
   */
  class B_ChalkOut_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      data.setMessage(monthClosePost());
      locateCurrentMonthCloseDate();
    }
    public String monthClosePost() throws Exception{
        dsMonthClose.first();
        for(int i = 1; i <= dsMonthClose.getRowCount(); i++)
        {
          if ( dsMonthClose.getValue("sfyj").equals("0") )
            break;
        dsMonthClose.next();
        }
        String s = dsMonthClose.getValue("nf");
        String s1 = dsMonthClose.getValue("yf");
        dsMonthClose.updateQuery(new String[]{combineSQL(PROCEDURESQL, "?", new String[]{dsMonthClose.getValue("nf"), dsMonthClose.getValue("yf"), fgsid})});
        dsMonthClose.setValue("sfyj", "1");
        dsMonthClose.post();
        dsMonthClose.saveChanges();
        return showJavaScript("alert('月结成功！')");
    }
  }






  }

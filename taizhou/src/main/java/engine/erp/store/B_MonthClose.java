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

public final class B_MonthClose extends BaseAction implements Operate
{
  /**
   * 处理流程:
   * 1. 初始过程.
   *   1.1 判断是否是第一次月结SELECT COUNT(*) FROM kc_yj y
   *       若记录数＝0，则提取SELECT MIN(d.sfrq) FROM kc_sfdj d,
   *      (如果收发单据没有记录，即最小值为null，提示不能月结)
   *      根据月结日期表中的信息计算该收发日期所属的月结月份，并插入今年的所有未月结记录。
   *   1.2 1.1 条件中 count(*)>0 满足的话，
   *       提取当年的SELECT * FROM kc_yj y WHERE nf=? AND yf=? AND fgsid = ?.
   *       若记录数＝0，则插入今年的所有未月结记录
   * 2. 月结过程：
   *   2.1 以下处理过程调用存储过程PCK_STORE.doMonthBalance
   *       (1).得到月结的日期区间（与月结日期jc_yjrq有关）
   *       (2).检查是否有未审核的单据
   *       (3).计算平均价并插入数据到库存年结存表 (kc_kcnjc)中
   *       (4).更新单据的单价和金额
   * 3. 结束月结
   *    调用成功（存储过程没有抛出异常），将是否月结字段的打上月结标志
   *
   * /
  public static final String FIXED_SEARCH = "1009";
  /**
   * 提取月末结账信息的SQL语句
   */
  private static final String B_MonthClose_SQL = //"SELECT * FROM cg_bj where fgsid=? ? ORDER BY cgbjID DESC";//
  "SELECT * FROM kc_yj ORDER BY nf,yf ";
  private static final String MASTER_STRUT_SQL = "SELECT * FROM kc_yj WHERE 1<>1";
  //判断是否是第一次月结sql.以库存月结 (kc_yj)  表中count(*)是否为空来判断
  private static final String COUNT_KC_YJ = "SELECT COUNT(*) FROM kc_yj WHERE fgsID = ? ";
  //提取收发单据 (kc_sfdj) 中收发日期的最小值为.
  //如果提取的收发日期的最小值为null的sql. 即收发单据没有记录，提示不能月结
  private static final String SELECT_KC_SFDJ_MIN_SFRQ = "SELECT to_char(MIN(d.sfrq), 'yyyy-mm-dd') FROM kc_sfdj d WHERE fgsid = ? ";
  //如果 库存月结 (kc_yj)  表中count(*)>0
  //提取当年的SELECT * FROM kc_yj y WHERE nf=? AND fgsid = ?.
  //若记录数＝0，则插入今年的所有未月结记录
  private static final String SELECT_KC_YJ_WITH_NF= "SELECT * FROM kc_yj y WHERE (nf= ? or sfyj = 0) AND fgsid = ? ";
  //
  private static final String PROCEDURESQL = "call PCK_STORE.doMonthBalance( ?, ?, ? )";
  private static final String SQLSELECTYJRQ = "select rq from jc_yjrq where yf = ? and rq >= ? ";
  //02.25 16:10 新增  新增取得应该是当前月结月份的记录 yjg
  private static final String CURRENTMONTHCLOSEDATE = "SELECT * FROM kc_yj WHERE nf = (SELECT MIN(nf) FROM kc_yj WHERE sfyj = 0) AND yf = (SELECT MIN(yf) FROM kc_yj WHERE sfyj = 0)";
  /**
   * 保存月末结账信息的数据集
   */
  private EngineDataSet dsMonthClose = new EngineDataSet();
  //02.25 16:10 新增  新增保存应该是当前月结月份的记录的记录集 yjg
  public EngineDataSet dsCurrentMonthCloseDate = new EngineDataSet();
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
  private long    editrow = 0;

  /**
   * 点击返回按钮的URL
   */
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
  public static B_MonthClose getInstance(HttpServletRequest request)
  {
    B_MonthClose B_MonthCloseBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_MonthCloseBean";
      B_MonthCloseBean = (B_MonthClose)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(B_MonthCloseBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        B_MonthCloseBean = new B_MonthClose();
        B_MonthCloseBean.fgsid = loginBean.getFirstDeptID();
        B_MonthCloseBean.loginId = loginBean.getUserID();
        B_MonthCloseBean.loginName = loginBean.getUserName();
        session.setAttribute(beanName, B_MonthCloseBean);
      }
    }
    return B_MonthCloseBean;
  }
  /**
   * 构造函数
   */
  private B_MonthClose()
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
    addObactioner(String.valueOf(INIT), new B_MonthClose_Init());
    addObactioner(String.valueOf(POST), new B_MonthClose_Post());
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

  //----Implementation of the BaseAction abstract class
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
      Date endDate = new Date(startDate.getYear(), startDate.getMonth()+1, 0);
      String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
      String endday = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
      rowInfo.put("ksrq", today);
      rowInfo.put("jsrq", endday);
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

  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class B_MonthClose_Init implements Obactioner
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
      //02.25 16:26 新增 在初始化时执行定位应该是当前月结月份的操作. yjg
      locateCurrentMonthCloseDate();
      //String nf = dsCurrentMonthCloseDate.getValue("nf");
      //02.18 15:57 去掉这句返回语句     要不然页面上就会出现一个javasrcipt错误. yjg
      // data.setMessage(showJavaScript("showFixedQuery()"));//初始化弹出查询界面
    }

    /**
     * 1.初始化过程：
     *   (1).判断是否是第一次月结SELECT COUNT(*) FROM kc_yj y WHERE fgsid = 登录的分公司id。
     *       若记录数＝0，则提取SELECT MIN(d.sfrq) FROM kc_sfdj d WHERE fgsid = 登录的分公司id
     *       (如果提取的收发日期的最小值为null，即收发单据没有记录，提示不能月结)，
     *       根据月结日期表中的信息计算该收发日期所属的月结月份，并插入今年的所有未月结记录。
     *   (2).若(1)条件满足的话，提取当年的SELECT * FROM kc_yj y WHERE nf=? AND fgsid = ?.
     *       若记录数＝0，则插入今年的所有未月结记录。
     * @throws Exception
     */
    public String monthCloseInitOperate() throws Exception {
      String s = combineSQL(COUNT_KC_YJ, "?", new String[]{fgsid});
      String count = dataSetProvider.getSequence(combineSQL(COUNT_KC_YJ, "?", new String[]{fgsid}));
      Calendar calendar = new GregorianCalendar();//为了取得年份
      String currentYear = String.valueOf(calendar.get(Calendar.YEAR));
      int CurrentMonth = calendar.get(Calendar.MONTH);
      int CurrentDay = calendar.get(Calendar.DAY_OF_MONTH);
      String[] MonthAndDay = null;//为下面得到分割出来的年月做准备.
      //如果 kc_yj 表中取到的记录数>0.说明已初始化过
      if ( !count.equals("0") ) {
        //提取当年的SELECT * FROM kc_yj y WHERE nf=?.
        //若记录数＝0，则插入今年的所有未月结记录
        //组EngineDataSet对象设置所要执行的sql
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
        //如果Table 库存月结 (kc_yj) 表中的记录数不为零
        //那么 则提取SELECT MIN(d.sfrq) FROM kc_sfdj d WHERE fgsid = 登录的分公司id
      }
      else
      {
        //提取的收发日期的最小值
        String minSfrqRecord = dataSetProvider.getSequence(combineSQL(SELECT_KC_SFDJ_MIN_SFRQ, "?", new String[]{fgsid}));
        //如果提取的收发日期的最小值为null，即收发单据没有记录，提示不能月结
        if ( minSfrqRecord == null ){
          //提示不能月结
          return showJavaScript("alert('不能月结！')");
        }
        else
        {
          //取得应该是当前月还是下一月.
          int whichMonth = 0;
          //根据月结日期表中的信息计算该收发日期所属的月结月份，并插入今年的所有未月结记录
          //select yf , rq from 月结日期 (jc_yjrq)
          //"/" 号分割得到kc_sfdj.MIN(d.sfrq)日期字符串. 用- 是因为sql用to_char(Date, "yyyy-mm-dd")格式.
          MonthAndDay = StringUtils.parseString(minSfrqRecord, "-");
          //select 月结日期 (jc_yjrq).rq from 月结日期 (jc_yjrq)
          //where yf 月份=  上面此数组中的 月份 and rq > 上面此数组中的 日期.(即:收发日期最小的那笔单据的日期)
          //用此最小的收发日期和月结日期比较.看此收发单据位于本月还是下月.
          String yjrq = dataSetProvider.getSequence(combineSQL(SQLSELECTYJRQ, "?", new String[]{MonthAndDay[1], MonthAndDay[2]}));
          //如果yjrq有值则说明此收发单据日期属于下一个月的了.要将yf+1值变为下一月的插入 库存月结 (kc_yj)表中
          //如果恰好是12月份,并且当前日期又大于此12月份的月结日期则要转到下一年了.
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
  class B_MonthClose_Post implements Obactioner
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
      //调用处理 月结过程 的方法.
      data.setMessage(monthClosePost());
      //02.25 16:26 新增 在资料更新及时执行定位应该是当前月结月份的操作. yjg
      locateCurrentMonthCloseDate();
    }
    /**
     *此方法做 做月结操作 的工作.
     *2.月结过程：
     *以下处理过程调用存储过程PCK_STORE.doMonthBalance
     *(1).得到月结的日期区间（与月结日期jc_yjrq有关）
     *(2).检查是否有未审核的单据
     *(3).计算平均价并插入数据到库存年结存表 (kc_kcnjc)中
     *(4).更新单据的单价和金额
     *3.结束月结
     *调用成功（存储过程没有抛出异常），将是否月结字段的打上月结标志
     *@para data execute方法传进来的参数,应该主要是取页面上的值信息的
     *@throws Exception
     */
    public String monthClosePost() throws Exception{
      /**
       * 本方法执行过程为:
       * 1. 取得用户在界面上要月结的年份月份.
       * 2.调用存储过程,传递年份月份过去.
       * 3.如果没有异常的话,那么找用户指定的月结月份,将是否月结字段的打上月结标志
       */
      //得到年份,及月份

        dsMonthClose.first();
        for(int i = 1; i <= dsMonthClose.getRowCount(); i++)
        {
          if ( dsMonthClose.getValue("sfyj").equals("0") )
            break;
        dsMonthClose.next();
        }
        String s = dsMonthClose.getValue("nf");
        String s1 = dsMonthClose.getValue("yf");
        //调用存储过程,把年月这两个值传进去.
        dsMonthClose.updateQuery(new String[]{combineSQL(PROCEDURESQL, "?", new String[]{dsMonthClose.getValue("nf"), dsMonthClose.getValue("yf"), fgsid})});
        //更新库存月结 (kc_yj)表.将此年份此月份记录的 是否月结字段 打上月结标志.
        dsMonthClose.setValue("sfyj", "1");
        dsMonthClose.post();
        dsMonthClose.saveChanges();
        return showJavaScript("alert('月结成功！')");

    }
  }
  }

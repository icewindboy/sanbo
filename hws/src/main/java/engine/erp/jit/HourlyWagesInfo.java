package engine.erp.jit;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;
import engine.html.*;
import engine.common.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 生产管理--车间计时工资设置</p>
 * <p>Description: 生产管理--车间计时工资设置</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨建国
 * @version 1.0
 */

public final class HourlyWagesInfo extends BaseAction implements Operate
{
  /**
   * 提取用途设置信息的SQL语句
   */
  private static final String STRUCT_SQL = "SELECT * FROM sc_hourwage_inf where 1<>1 ";
  private static final String PRODUCEUSE_SQL = "SELECT * FROM kc_csdjyt WHERE ytid=";//
  private static final String COUNTPERSON = "SELECT COUNT(*) FROM sc_hourwage_inf WHERE personid = ? and deptid = ? ";//
   private static final String SEARCH_SQL = "SELECT * FROM sc_hourwage_inf where 1=1 ? order by deptid ";//

  /**
   * 保存用途设置信息的数据集
   */
  private EngineDataSet dsHourWageInfo = new EngineDataSet();
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsHourWageInfo, "sc_hourwage_inf");

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

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;
  public  String loginId   = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  //public  String bjfs = ""; //系统的报价方式

  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private User user = null;
  /**
   * 得到用途设置信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回用途设置信息的实例
   */
  public static HourlyWagesInfo getInstance(HttpServletRequest request)
  {
   HourlyWagesInfo hourWageInfoBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "hourWageInfoBean";
      hourWageInfoBean = (HourlyWagesInfo)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(hourWageInfoBean == null)
      {
        //引用LoginBean
        hourWageInfoBean = new HourlyWagesInfo();
        LoginBean loginBean = LoginBean.getInstance(request);
        hourWageInfoBean.fgsid = loginBean.getFirstDeptID();
        hourWageInfoBean.loginId = loginBean.getUserID();
        hourWageInfoBean.loginName = loginBean.getUserName();
        hourWageInfoBean.loginDept = loginBean.getDeptID();
        hourWageInfoBean.user = loginBean.getUser();
        session.setAttribute(beanName, hourWageInfoBean);
      }
    }
    return hourWageInfoBean;
  }
  /**
   * 构造函数
   */
  private HourlyWagesInfo()
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
    //02.24 20:54 新增 给下面句sql加上order by ytbh语句. yjg
    setDataSetProperty(dsHourWageInfo,STRUCT_SQL);
    dsHourWageInfo.setSort(new SortDescriptor("", new String[]{"deptid"}, new boolean[]{false}, null, 0));

    //添加操作的触发对象
    HourlyWagesInfo_Add_Edit add_edit = new HourlyWagesInfo_Add_Edit();
    addObactioner(String.valueOf(INIT), new HourlyWagesInfo_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new HourlyWagesInfo_Post());
    addObactioner(String.valueOf(DEL), new HourlyWagesInfo_Delete());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
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
      if(dsHourWageInfo.isOpen() && dsHourWageInfo.changesPending())
        dsHourWageInfo.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
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

  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsHourWageInfo != null){
      dsHourWageInfo.close();
      dsHourWageInfo = null;
    }
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
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
    if(!isAdd)
      rowInfo.put(getOneTable());
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsHourWageInfo.isOpen())
      dsHourWageInfo.open();
    return dsHourWageInfo;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class HourlyWagesInfo_Init implements Obactioner
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
      HttpServletRequest request = data.getRequest();
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      masterProducer.init(request, loginId);

      String deptvalue = user.getHandleDeptValue("deptid");
      if(deptvalue.indexOf(">")>0)
        deptvalue = "and 1<>1 ";
      else
        deptvalue = " and "+user.getHandleDeptValue("deptid");
      dsHourWageInfo.setQueryString(combineSQL(SEARCH_SQL, "?", new String[]{ deptvalue,""}));
      dsHourWageInfo.setRowMax(null);
    }
  }

  /**
   * 添加或修改操作的触发类
   */
  class HourlyWagesInfo_Add_Edit implements Obactioner
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
        dsHourWageInfo.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsHourWageInfo.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class HourlyWagesInfo_Post implements Obactioner
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
      String personid = rowInfo.get("personid");
      String deptid = rowInfo.get("deptid");
      String hour_unit  = rowInfo.get("hour_unit");
      String hour_wage  = rowInfo.get("hour_wage");
      String over_wage  = rowInfo.get("over_wage");
      String night_wage = rowInfo.get("night_wage");
      if(personid.equals("")){
        data.setMessage(showJavaScript("alert('人员不能为空！');"));
        return;
      }
      if(deptid.equals(""))
      {
        data.setMessage(showJavaScript("alert('车间不能为空！');"));
        return;
      }
      if(!isAdd)
        ds.goToInternalRow(editrow);

      if(!isAdd && (!personid.equals(ds.getValue("personid")) && !deptid.equals(ds.getValue("deptid"))))
      {
        String count = dataSetProvider.getSequence(combineSQL(COUNTPERSON, "?", new String[]{personid, deptid}));
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('人员("+ personid +" 车间 "+ deptid +")已经存在!');"));
          return;
        }
      }
      if(isAdd)
      {
        String count = dataSetProvider.getSequence(combineSQL(COUNTPERSON, "?", new String[]{personid, deptid}));
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('人员("+ personid +" 车间 "+ deptid +")已经存在!');"));
          return;
        }
        ds.insertRow(false);
      }
      ds.setValue("personid", personid);
      ds.setValue("deptid", deptid);
      ds.setValue("hour_unit", hour_unit);
      ds.setValue("hour_wage", hour_wage);
      ds.setValue("over_wage", over_wage);
      ds.setValue("night_wage", night_wage);
      ds.post();
      ds.saveChanges();
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCE_USE);
    }
  }

  /**
   * 删除操作的触发类
   */
  class HourlyWagesInfo_Delete implements Obactioner
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
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCE_USE);
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
      SQL = " and "+SQL;
    SQL = combineSQL(SEARCH_SQL, "?", new String[]{SQL});
    //03.16 15:25 修改 在Master_Search中去掉判断旧的SQL和新产生的SQL是否相同的if语句. yjg
    //if(!dsMasterTable.getQueryString().equals(SQL))
    //{
      dsHourWageInfo.setQueryString(SQL);
      dsHourWageInfo.setRowMax(null);
    //}
  }
  /**
   * 初始化查询的各个列
   * @param request web请求对象
   */
  private void initQueryItem(HttpServletRequest request)
  {
    if(isInitQuery)
      return;
    EngineDataSet dsHourWageInfo = getOneTable();
    //初始化固定的查询项目
    fixedQuery = new QueryFixedItem();
    fixedQuery.addShowColumn("", new QueryColumn[]{
      new QueryColumn(dsHourWageInfo.getColumn("deptid"), null, null, null),
      new QueryColumn(dsHourWageInfo.getColumn("personid"), null, null, null),
    });
    isInitQuery = true;
  }

  }
}

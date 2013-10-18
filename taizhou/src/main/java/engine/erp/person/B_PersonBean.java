package engine.erp.person;

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
import com.borland.dx.dataset.*;
/**
 * <p>Title: 设备管理--设备履历卡（引设备名称）</p>
 * <p>Description: 设备管理--设备履历卡（引设备名称）</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author why
 * @version 1.0
 */
public final class B_PersonBean extends BaseAction implements Operate
{
  private  static   final   String mastersql="select * from emp where 1<>1";

  private static final String Quality_SQL="select * from emp ";
  //private static final String Select_SQL="select * from emp";

  /**
   * 建立检验类型列表信息的数据集
   */
  private EngineDataSet dsmaster=new  EngineDataSet();
  private EngineDataSet dsSelect=new  EngineDataSet();
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
  public String curID=null;
  private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private User user = null;
  public  boolean isWorkNo = false;
  /**
   * 得到检验类型信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_PersonBean getInstance(HttpServletRequest request)
  {
    B_PersonBean B_PersonBeanBean = null;
    HttpSession session = request.getSession(true);
    synchronized(session)
    {
      String beanName = "B_PersonBeanBean";
      B_PersonBeanBean = (B_PersonBean)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(B_PersonBeanBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        B_PersonBeanBean = new B_PersonBean();
        B_PersonBeanBean.user = loginBean.getUser();
        B_PersonBeanBean.isWorkNo = loginBean.getSystemParam("SYS_CUST_NAME").equals("ruijiao");
        session.setAttribute(beanName, B_PersonBeanBean);
      }
    }
    return B_PersonBeanBean;
  }
  /**
   * 构造函数
   */
  public B_PersonBean()
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
    setDataSetProperty(dsmaster, mastersql);
    //添加操作的触发对象
    setDataSetProperty(dsSelect, mastersql);
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
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
      if(dsmaster.isOpen() && dsmaster.changesPending())
        dsmaster.reset();
      if(dsSelect.isOpen() && dsSelect.changesPending())
        dsSelect.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  public String code_name_Change(String code,String name,String operate,String isdelete){
    String SelectSQL=null;
    String Selectfield="personid";
    String SelectColumn="";
    String SelectColumnName="";
    if(operate.equals("53")){
      SelectColumn=code;
      SelectColumnName="bm";
    }
    if(operate.equals("54")){
      SelectColumn=name;
      SelectColumnName="xm";
    }
    String dp = user.getHandleDeptValue();
    SelectSQL=Quality_SQL +" where "+SelectColumnName+" like '%"+SelectColumn+"%' and "+dp+"";
    if(isdelete.equals("0"))
      SelectSQL= SelectSQL + " and isdelete=0 ";
    SelectSQL = SelectSQL+ " order by bm ";
    dsSelect.setQueryString(SelectSQL);
    openSelectTable();
    dsSelect.setRowMax(null);
    if(dsSelect.getRowCount()>1){
      return "more";
    }
    if(dsSelect.getRowCount()<1) return "none";
    return dsSelect.getValue("personid");
  }
  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsmaster != null){
      dsmaster.close();
      dsmaster = null;
    }
    if(dsSelect != null){
      dsSelect.close();
      dsSelect = null;
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
    if(!dsmaster.isOpen())
      dsmaster.open();
    return dsmaster;
  }
        /*得到选择表对象*/
  public final EngineDataSet getSelectTable()
  {
    if(!dsSelect.isOpen())
      dsSelect.open();
    return dsSelect;
  }
          /*打开清理或报废记录表*/
  public final void openSelectTable()
  {
    //dsSelect.setQueryString(SQL);
    if(!dsSelect.isOpen())
      dsSelect.open();
    else
      dsSelect.refresh();
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
      /*得到一列的信息*/
  public final RowMap getRowinfo() {return rowInfo;  }
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
            fixedQuery.getSearchRow().clear();
      String dp = user.getHandleDeptValue();
      String ddp = user.getHandleDeptWhereValue();
      try{
        String SQL=null;
        String code=data.getParameter("bm");
        String name=data.getParameter("xm");
        String isdelete=data.getParameter("isdelete");
        if(code!=null&&!code.equals("")&& !code.equals("null"))
          SQL=Quality_SQL+" where bm like '%"+code+"%' and "+dp+"";
        else if(name!=null&&!name.equals(""))
          SQL=Quality_SQL+" where xm like '%"+name+"%' and "+dp+"";
        else SQL=Quality_SQL+" where 1=1 and "+dp;
        if(isdelete!=null&&isdelete.equals("0"))
          SQL = SQL+" and isdelete=0 ";
        SQL = SQL+ " order by bm ";
        dsmaster.setQueryString(SQL);
        dsmaster.setRowMax(null);
        retuUrl = data.getParameter("src");
        retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      }
      catch(Exception e1){
      }
    }
  }


  /**
   *  查询操作
   */
  class Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND " + SQL;
      //替换可变字符串，组装SQL
      String deptids = user.getHandleDeptValue("deptid");
      SQL = Quality_SQL+" where 1=1 "+SQL;//combineSQL(PERSON_SQL, "?", new String[]{stat, deptids, SQL});
      if(!dsmaster.getQueryString().equals(SQL))
      {
        dsmaster.setQueryString(SQL);
        dsmaster.setRowMax(null);
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
      EngineDataSet ds = dsmaster;
      if(!ds.isOpen())
        ds.open();
      //初始化固定的查询项目
      fixedQuery.addShowColumn("", new QueryColumn[]{//"",表示默认的表名
        new QueryColumn(ds.getColumn(isWorkNo ? "gh": "bm"), null, null, null),
        new QueryColumn(ds.getColumn("xm"), null, null, null),
        new QueryColumn(ds.getColumn("deptid"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }

}

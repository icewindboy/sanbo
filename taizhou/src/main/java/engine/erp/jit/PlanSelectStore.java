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
import engine.common.LoginBean;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 生产子系统_生产计划维护根据库存量生产</p>
 * <p>Description: 生产子系统_生产计划维护根据库存量生产</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨建国
 * @version 1.0
 */

public final class PlanSelectStore extends BaseAction implements Operate
{
  private static final String IMPORT_ORDER_SQL = "SELECT * FROM VW_PRODUCEPLAN_SEL_STORE ? order by cesl desc";
  private EngineDataSet dsPlanSelectStore  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false; //是否已经初始化查询条件
  public  HtmlTableProducer searchTable = new HtmlTableProducer(dsPlanSelectStore, "kc_dm_select", "");//查询得到数据库中配置的字段
  /**
   * 得到当前库存量信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回当前库存量信息的实例
   */
  public static PlanSelectStore getInstance(HttpServletRequest request)
  {
    PlanSelectStore planSelectStoreBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "planSelectStoreBean";
      planSelectStoreBean = (PlanSelectStore)session.getAttribute(beanName);
      if(planSelectStoreBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        planSelectStoreBean = new PlanSelectStore();
        planSelectStoreBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, planSelectStoreBean);
      }
    }
    return planSelectStoreBean;
  }

  /**
   * 构造函数
   */
  private PlanSelectStore()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /**
   * Implement this engine.project.OperateCommon abstract method
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsPlanSelectStore, null);

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
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
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsPlanSelectStore != null){
      dsPlanSelectStore.close();
      dsPlanSelectStore = null;
    }
    log = null;
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
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      String SQL = " where 1=1 ";
      String chxz = request.getParameter("chxz");
      if (chxz!=null) SQL += " and chxz = " + chxz;//存货性质.自制件=1
      SQL = combineSQL(IMPORT_ORDER_SQL, "?", new String[]{SQL});
      dsPlanSelectStore.setQueryString(SQL);
        dsPlanSelectStore.setRowMax(null);
    }
  }
  /*
  *得到一行信息
    */
  public final RowMap getLookupRow(String cpid)
  {
    RowMap row = new RowMap();
    if(cpid == null || cpid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsPlanSelectStore, "cpid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "cpid");
    locateRow.setValue(0, cpid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
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
   *  查询操作
   */
  class Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      //searchTable.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " WHERE "+ SQL;
      else
        SQL = " WHERE 1=1 ";
      SQL = combineSQL(IMPORT_ORDER_SQL, "?", new String[]{SQL});
      if(!dsPlanSelectStore.getQueryString().equals(SQL))
      {
        dsPlanSelectStore.setQueryString(SQL);
        dsPlanSelectStore.setRowMax(null);
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
      EngineDataSet master = dsPlanSelectStore;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("cpbm"), null, null, null),
        new QueryColumn(master.getColumn("product"), null, null, null),
        new QueryColumn(master.getColumn("chxz"), null, null, null),
        new QueryColumn(master.getColumn("wzlbid"), null, null, null),
      });
      isInitQuery = true;
    }
  }
   /**
     * 得到表
    */
  public final EngineDataSet getOneTable()
  {
    return dsPlanSelectStore;
  }
}
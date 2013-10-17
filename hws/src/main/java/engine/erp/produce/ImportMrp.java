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
 * <p>Title: 生产子系统_生产任务单引入物料需求计划</p>
 * <p>Description: 生产子系统_生产任务单引入物料需求计划</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class ImportMrp extends BaseAction implements Operate
{
  private static final String IMPORT_MRP_SQL
      = "SELECT * FROM VW_PRODUCETASK_SEL_MRP WHERE (deptid IS NULL OR deptid=?) AND fgsid=? ? ";

  private EngineDataSet dsmrp  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null;
  private String deptid = null;
  /**
   * 定义固定查询类
    */
  private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false; //是否已经初始化查询条件
  /**
   * 得到物料需求计划信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回物料需求计划信息的实例
   */
  public static ImportMrp getInstance(HttpServletRequest request)
  {
    ImportMrp importMrpBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "importMrpBean";
      importMrpBean = (ImportMrp)session.getAttribute(beanName);
      if(importMrpBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        importMrpBean = new ImportMrp();
        importMrpBean.qtyFormat = loginBean.getQtyFormat();
        importMrpBean.dsmrp.setColumnFormat("xql", importMrpBean.qtyFormat);
        importMrpBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, importMrpBean);
      }
    }
    return importMrpBean;
  }

  /**
   * 构造函数
   */
  private ImportMrp()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
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
  /**
   * Implement this engine.project.OperateCommon abstract method
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsmrp, null);

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
    if(dsmrp != null){
      dsmrp.close();
      dsmrp = null;
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
      //初始化查询项目和内容
      //initQueryItem(request);
      //fixedQuery.getSearchRow().clear();
      //替换可变字符串，组装SQL
      deptid = request.getParameter("deptid");
      String SQL = combineSQL(IMPORT_MRP_SQL, "?", new String[]{deptid,fgsid});
      dsmrp.setQueryString(SQL);
      dsmrp.setRowMax(null);
    }
  }
  /*
  *得到一行信息
    */
  public final RowMap getLookupRow(String wlxqjhmxid)
  {
    RowMap row = new RowMap();
    if(wlxqjhmxid == null || wlxqjhmxid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsmrp, "wlxqjhmxid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "wlxqjhmxid");
    locateRow.setValue(0, wlxqjhmxid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
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
        SQL = " AND "+SQL;
      SQL = combineSQL(IMPORT_MRP_SQL, "?", new String[]{deptid,fgsid, SQL});
      if(!dsmrp.getQueryString().equals(SQL))
      {
        dsmrp.setQueryString(SQL);
        dsmrp.setRowMax(null);
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
      EngineDataSet master = dsmrp;
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("wlxqh"), null, null, null),
        new QueryColumn(master.getColumn("rq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("rq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),//产品
      });
      isInitQuery = true;
    }
  }
  public final EngineDataSet getOneTable()
  {
    return dsmrp;
  }
}
package engine.erp.sale.xixing;

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
 * <p>Title: 库存管理_移库单引入库存物资明细</p>
 * <p>Description: 库存管理_移库单引入库存物资明细</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class ImportMaterail extends BaseAction implements Operate
{
  private static final String IMPORT_MATERAIL_SQL = "SELECT * FROM VW_XS_KC_WZMX WHERE zl<>0 and fgsid=? and storeid=? ?  order by cpbm";

  private EngineDataSet dsMaterail  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null;
  private String storeid = null;
  /**
  * 定义固定查询类
   */
  private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false; //是否已经初始化查询条件
  /**
   * 得到库存物资信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回库存物资信息的实例
   */
  public static ImportMaterail getInstance(HttpServletRequest request)
  {
    ImportMaterail importMaterailBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "importMaterailBean";
      importMaterailBean = (ImportMaterail)session.getAttribute(beanName);
      if(importMaterailBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        importMaterailBean = new ImportMaterail();
        importMaterailBean.qtyFormat = loginBean.getQtyFormat();

        importMaterailBean.fgsid = loginBean.getFirstDeptID();
        importMaterailBean.dsMaterail.setColumnFormat("zl", importMaterailBean.qtyFormat);
        session.setAttribute(beanName, importMaterailBean);
      }
    }
    return importMaterailBean;
  }

  /**
   * 构造函数
   */
  private ImportMaterail()
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
    setDataSetProperty(dsMaterail, null);

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
   * Session失效时，调用的函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMaterail != null){
      dsMaterail.close();
      dsMaterail = null;
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
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      //替换可变字符串，组装SQL
      storeid = request.getParameter("storeid");
      String SQL = combineSQL(IMPORT_MATERAIL_SQL, "?", new String[]{fgsid,storeid});
      dsMaterail.setQueryString(SQL);
        dsMaterail.setRowMax(null);
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
      SQL = combineSQL(IMPORT_MATERAIL_SQL, "?", new String[]{fgsid, storeid, SQL});
      if(!dsMaterail.getQueryString().equals(SQL))
      {
        dsMaterail.setQueryString(SQL);
        dsMaterail.setRowMax(null);
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
      EngineDataSet master = dsMaterail;
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("cpbm"), null, null, null),
        new QueryColumn(master.getColumn("product"), null, null, null),
      });
      isInitQuery = true;
    }
  }
  /*
  *得到一行信息
    */
  public final RowMap getLookupRow(String wzmxid)
  {
    RowMap row = new RowMap();
    if(wzmxid == null || wzmxid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsMaterail, "wzmxid");
    locateRow.setValue(0, wzmxid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }

  public final EngineDataSet getOneTable()
  {
    return dsMaterail;
  }
}
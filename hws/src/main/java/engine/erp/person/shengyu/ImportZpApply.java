package engine.erp.person.shengyu;

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
 * <p>Title: 采购子系统_引入申请单</p>
 * <p>Description: 采购子系统_引入申请单</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class ImportZpApply extends BaseAction implements Operate
{
 // private static final String IMPORT_APPLY_SQL = "SELECT * FROM VW_CG_SQD WHERE fgsid='?' and (dwtxid IS NULL or dwtxid='?') ? ORDER BY dwdm, cpbm";

  private static final String IMPORT_APPLY_SQL = "SELECT * FROM rl_invite_apply WHERE filialeID='?'  ? ";
  private EngineDataSet dsBuyApplyProduct  = new EngineDataSet();
  private EngineRow locateResult = null;

  /**
   * 定义固定查询类
    */
  private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false; //是否已经初始化查询条件
  public  String retuUrl = null;
  private String filialeID = null;   //分公司ID
  private String dwtxid = null;// 往来单位
  /**
   * 得到申请单信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回申请单信息的实例
   */
  public static ImportZpApply getInstance(HttpServletRequest request)
  {
    ImportZpApply importZpApplyBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "importZpApplyBean";
      importZpApplyBean = (ImportZpApply)session.getAttribute(beanName);
      if(importZpApplyBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        importZpApplyBean = new ImportZpApply();
        importZpApplyBean.filialeID = loginBean.getFirstDeptID();
        session.setAttribute(beanName, importZpApplyBean);
      }
    }
    return importZpApplyBean;
  }

  /**
   * 构造函数
   */
  private ImportZpApply()
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
    setDataSetProperty(dsBuyApplyProduct, null);

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
    if(dsBuyApplyProduct != null){
      dsBuyApplyProduct.close();
      dsBuyApplyProduct = null;
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
      dwtxid = request.getParameter("dwtxid");
      String SQL = combineSQL(IMPORT_APPLY_SQL, "?", new String[]{filialeID});
      //if(!dwtxid.equals(""))
        //SQL=SQL+" AND dwtxid="+dwtxid;
      dsBuyApplyProduct.setQueryString(SQL);
      dsBuyApplyProduct.setRowMax(null);
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
      SQL = combineSQL(IMPORT_APPLY_SQL, "?", new String[]{filialeID, SQL});
      if(!dsBuyApplyProduct.getQueryString().equals(SQL))
      {
        dsBuyApplyProduct.setQueryString(SQL);
        dsBuyApplyProduct.setRowMax(null);
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
      EngineDataSet master = dsBuyApplyProduct;
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("sqbh"), null, null, null),
        new QueryColumn(master.getColumn("sqrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("sqrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),//产品
      });
      isInitQuery = true;
    }
  }
  /*
  *得到一行信息
    */
  public final RowMap getLookupRow(String apply_ID)
  {
    RowMap row = new RowMap();
    if(apply_ID == null || apply_ID.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsBuyApplyProduct, "apply_ID");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "apply_ID");
    locateRow.setValue(0, apply_ID);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsBuyApplyProduct;
  }
}
package engine.erp.buy;

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
 * <p>Title: 采购子系统_采购申请单引入物料需求计划</p>
 * <p>Description: 采购子系统_采购申请单引入物料需求计划</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class Import_MrpGoods extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1999";
  private static final String IMPORT_MRPGOODS_SQL = "SELECT * FROM VW_APPLY_IMPORT_MRP WHERE fgsid=? ? ";

  private EngineDataSet dsmrpGoods  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null;
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
  public static Import_MrpGoods getInstance(HttpServletRequest request)
  {
    Import_MrpGoods importMrpGoodsBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "importMrpGoodsBean";
      importMrpGoodsBean = (Import_MrpGoods)session.getAttribute(beanName);
      if(importMrpGoodsBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        importMrpGoodsBean = new Import_MrpGoods();
        importMrpGoodsBean.qtyFormat = loginBean.getQtyFormat();
        importMrpGoodsBean.dsmrpGoods.setColumnFormat("hssl", importMrpGoodsBean.qtyFormat);
        importMrpGoodsBean.dsmrpGoods.setColumnFormat("sl", importMrpGoodsBean.qtyFormat);
        importMrpGoodsBean.dsmrpGoods.setColumnFormat("wgl", importMrpGoodsBean.qtyFormat);
        importMrpGoodsBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, importMrpGoodsBean);
      }
    }
    return importMrpGoodsBean;
  }

  /**
   * 构造函数
   */
  private Import_MrpGoods()
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
    setDataSetProperty(dsmrpGoods, null);

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
    if(dsmrpGoods != null){
      dsmrpGoods.close();
      dsmrpGoods = null;
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
      String SQL = combineSQL(IMPORT_MRPGOODS_SQL, "?", new String[]{fgsid});
      dsmrpGoods.setQueryString(SQL);
      dsmrpGoods.setRowMax(null);
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
    EngineRow locateRow = new EngineRow(dsmrpGoods, "wlxqjhmxid");
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
      SQL = combineSQL(IMPORT_MRPGOODS_SQL, "?", new String[]{fgsid, SQL});
      if(!dsmrpGoods.getQueryString().equals(SQL))
      {
        dsmrpGoods.setQueryString(SQL);
        dsmrpGoods.setRowMax(null);
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
      EngineDataSet master = dsmrpGoods;
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
    //用换算比例计算换算数量
    if (dsmrpGoods.isOpen()) dsmrpGoods.refresh(); else dsmrpGoods.openDataSet();
    dsmrpGoods.first();
    for (int i=0; i<dsmrpGoods.getRowCount();i++){
      BigDecimal hssl = new BigDecimal(0);
      String sxz = dsmrpGoods.getValue("sxz");
      String hsbl = dsmrpGoods.getValue("hsbl");
      BigDecimal xgl = new BigDecimal(0);
      try{
        hssl = calculateExpression(hsbl, sxz).multiply(xgl);
      }
      catch (Exception e){
        hssl = new BigDecimal(0);
      }
      dsmrpGoods.setValue("hssl", hssl.toString());
      dsmrpGoods.next();
    }
    return dsmrpGoods;
  }
}

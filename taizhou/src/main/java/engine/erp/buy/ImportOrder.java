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
 * <p>Title: 采购子系统_采购进货单引入合同</p>
 * <p>Description: 采购子系统_采购进货单引入合同</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class ImportOrder extends BaseAction implements Operate
{
  private static final String IMPORT_ORDER_SQL = "SELECT * FROM VW_BUY_ORDER WHERE fgsid=? and dwtxid=? and (storeid IS NULL OR storeid=? )";

  private EngineDataSet dsBuyOrderProduct  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  /**
   * 得到采购合同信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回采购合同信息的实例
   */
  public static ImportOrder getInstance(HttpServletRequest request)
  {
    ImportOrder importOrderBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "importOrderBean";
      importOrderBean = (ImportOrder)session.getAttribute(beanName);
      if(importOrderBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        importOrderBean = new ImportOrder();
        importOrderBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, importOrderBean);
      }
    }
    return importOrderBean;
  }

  /**
   * 构造函数
   */
  private ImportOrder()
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
    setDataSetProperty(dsBuyOrderProduct, null);

    addObactioner(String.valueOf(INIT), new Init());
    //addObactioner(String.valueOf(FIXED_SEARCH), new Search());
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
    if(dsBuyOrderProduct != null){
      dsBuyOrderProduct.close();
      dsBuyOrderProduct = null;
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
      String dwtxid = request.getParameter("dwtxid");
      String storeid = request.getParameter("storeid");
      String SQL = combineSQL(IMPORT_ORDER_SQL, "?", new String[]{fgsid,dwtxid,storeid});
      dsBuyOrderProduct.setQueryString(SQL);
        dsBuyOrderProduct.setRowMax(null);
    }
  }
  /*
  *得到一行信息
    */
  public final RowMap getLookupRow(String hthwid)
  {
    RowMap row = new RowMap();
    if(hthwid == null || hthwid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsBuyOrderProduct, "hthwid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "hthwid");
    locateRow.setValue(0, hthwid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }

  public final EngineDataSet getOneTable()
  {
    return dsBuyOrderProduct;
  }
}

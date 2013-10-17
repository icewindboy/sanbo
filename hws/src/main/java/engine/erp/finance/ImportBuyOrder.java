package engine.erp.finance;

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
 * <p>Title: 销售子系统_提货单管理引入合同</p>
 * <p>Description: 销售子系统_提货单管理引入合同</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */

public final class ImportBuyOrder extends BaseAction implements Operate
{
  private static final String IMPORT_SALE_ORDER_SQL = "SELECT * FROM vw_sale_order WHERE fgsid=? and dwtxid=? ";//分公司; 往来单位(根据购货单位提取数据)
  private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();//数据集
  private EngineRow locateResult = null;//用于定位数据集数据
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  /**
   *从会话中取出本类实例
   *
   * */
  public static ImportBuyOrder getInstance(HttpServletRequest request)
  {
    ImportBuyOrder importBuyOrderBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "importBuyOrderBean";
      importBuyOrderBean = (ImportBuyOrder)session.getAttribute(beanName);
      if(importBuyOrderBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        importBuyOrderBean = new ImportBuyOrder();
        importBuyOrderBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, importBuyOrderBean);
      }
    }
    return importBuyOrderBean;
  }
  /**
   * 构造函数
   */
  private ImportBuyOrder()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsSaleOrderProduct, null);//空数据集
    addObactioner(String.valueOf(INIT), new Init());//注册初始化操作
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
      String operate = request.getParameter("operate");
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
    if(dsSaleOrderProduct != null){
      dsSaleOrderProduct.close();
      dsSaleOrderProduct = null;
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
  //得到一行信息
  public final RowMap getLookupRow(String hthwid)
  {
    RowMap row = new RowMap();
    if(hthwid == null || hthwid.equals(""))
      return row;//返回
    EngineRow locateRow = new EngineRow(dsSaleOrderProduct, "hthwid");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "hthwid");
    locateRow.setValue(0, hthwid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsSaleOrderProduct;
  }
  /**
   * 初始化操作的的执行者
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");//where is it coming?
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      String dwtxid = request.getParameter("dwtxid");
      String SQL = combineSQL(IMPORT_SALE_ORDER_SQL, "?", new String[]{fgsid,dwtxid});
      dsSaleOrderProduct.setQueryString(SQL);
      dsSaleOrderProduct.setRowMax(null);
    }
  }

}
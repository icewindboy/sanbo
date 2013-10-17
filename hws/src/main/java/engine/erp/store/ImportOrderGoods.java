package engine.erp.store;

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
 * <p>Title: 库存管理_采购入库单引入进货单</p>
 * <p>Description: 库存管理_采购入库单引入进货单</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class ImportOrderGoods extends BaseAction implements Operate
{
  private static final String IMPORT_ORDERGOODS_SQL = "SELECT * FROM VW_BUY_ORDERGOODS WHERE fgsid=? and dwtxid=? and storeid=? ";

  private EngineDataSet dsBuyOrderGoods  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null;
  public String bjfs = "";   //报价方式
  /**
   * 得到采购合同信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回采购合同信息的实例
   */
  public static ImportOrderGoods getInstance(HttpServletRequest request)
  {
    ImportOrderGoods importOrderGoodsBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "importOrderGoodsBean";
      importOrderGoodsBean = (ImportOrderGoods)session.getAttribute(beanName);
      if(importOrderGoodsBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        importOrderGoodsBean = new ImportOrderGoods();
        importOrderGoodsBean.qtyFormat = loginBean.getQtyFormat();
        importOrderGoodsBean.bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");

        importOrderGoodsBean.fgsid = loginBean.getFirstDeptID();
        importOrderGoodsBean.dsBuyOrderGoods.setColumnFormat("sjrkl", importOrderGoodsBean.qtyFormat);
        session.setAttribute(beanName, importOrderGoodsBean);
      }
    }
    return importOrderGoodsBean;
  }

  /**
   * 构造函数
   */
  private ImportOrderGoods()
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
    setDataSetProperty(dsBuyOrderGoods, null);

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
    if(dsBuyOrderGoods != null){
      dsBuyOrderGoods.close();
      dsBuyOrderGoods = null;
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
      String SQL = combineSQL(IMPORT_ORDERGOODS_SQL, "?", new String[]{fgsid,dwtxid,storeid});
      if(bjfs.equals("1"))
        SQL = SQL + " AND nvl(hssl,0)>nvl(sjrkhsl,0)";
      else
        SQL = SQL + " AND nvl(sl,0)>nvl(sjrkl,0)";
      dsBuyOrderGoods.setQueryString(SQL);
        dsBuyOrderGoods.setRowMax(null);
    }
  }
  /*
  *得到一行信息
    */
  public final RowMap getLookupRow(String jhdhwid)
  {
    RowMap row = new RowMap();
    if(jhdhwid == null || jhdhwid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsBuyOrderGoods, "jhdhwid");
    locateRow.setValue(0, jhdhwid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }

  public final EngineDataSet getOneTable()
  {
    return dsBuyOrderGoods;
  }
}

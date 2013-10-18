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
 * <p>Title: 生产管理—生产计划引用销售合同或者外贸合同</p>
 * <p>Description: 生产管理—生产计划引用销售合同或者外贸合同</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zjb
 * @version 1.0
 */

public final class PlanSelectSale extends BaseAction implements Operate
{
 // private static final String SINGLE_ORDER_SQL
 //     = "SELECT DISTINCT htid, deptid, dwtxid, personid, khlx, htbh, htrq, fgsid, zt, zsl, zje, wbid,hl "
 //     + " FROM vw_ordergoods_single_order WHERE djxz='0' AND (storeid IS NULL OR storeid= ?) AND fgsid=? ";
  private static final String SINGLE_ORDER_SQL = "SELECT * FROM VW_PLAN_SINGLE_SALEORDER2 WHERE fgsid= ";
  private static final String SINGLE_ORDER_SQL_WM = "SELECT * FROM VW_PLAN_SINGLE_SALEORDER3 WHERE fgsid= ";

  private EngineDataSet dsOrder  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null;
  /**
   * 得到生产计划选择销售订单信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回进货单选择采购合同信息的实例
   */
  public static PlanSelectSale getInstance(HttpServletRequest request)
  {
    PlanSelectSale planSelectSaleBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "planSelectSaleBean";
      planSelectSaleBean = (PlanSelectSale)session.getAttribute(beanName);
      if(planSelectSaleBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        planSelectSaleBean = new PlanSelectSale();
        planSelectSaleBean.qtyFormat = loginBean.getQtyFormat();

        planSelectSaleBean.fgsid = loginBean.getFirstDeptID();
        planSelectSaleBean.dsOrder.setColumnFormat("zsl", planSelectSaleBean.qtyFormat);
        planSelectSaleBean.dsOrder.setColumnFormat("zje", planSelectSaleBean.qtyFormat);
        session.setAttribute(beanName, planSelectSaleBean);
      }
    }
    return planSelectSaleBean;
  }

  /**
   * 构造函数
   */
  private PlanSelectSale()
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
    setDataSetProperty(dsOrder, null);

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
    if(dsOrder != null){
      dsOrder.close();
      dsOrder = null;
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
      String SQL=null;
      String jhlx = request.getParameter("jhlx");
      if(jhlx.equals("0"))
      {
      SQL = SINGLE_ORDER_SQL + fgsid;
      }
      else if(jhlx.equals("1"))
      {
      SQL = SINGLE_ORDER_SQL_WM + fgsid;
      }
      String htid = request.getParameter("htid");
      if(htid!=null&&!htid.equals(""))
        SQL = SQL+" and htid="+htid;

      //String storeid = request.getParameter("storeid");
      //String SQL = combineSQL(SINGLE_ORDER_SQL, "?", new String[]{storeid, fgsid});
      //String dwtxid = request.getParameter("dwtxid");
      //if(!dwtxid.equals(""))
        //SQL = SQL + "AND dwtxid= " + dwtxid;
      dsOrder.setQueryString(SQL);
      dsOrder.setRowMax(null);
    }
  }
  /*
  *得到一行信息
  */
  public final RowMap getLookupRow(String htid)
  {
    RowMap row = new RowMap();
    if(htid == null || htid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsOrder, "htid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "htid");
    locateRow.setValue(0, htid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsOrder;
  }
}



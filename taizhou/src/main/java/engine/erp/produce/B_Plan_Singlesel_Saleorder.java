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
 * <p>Title: 生产管理—生产计划选择销售合同</p>
 * <p>Description: 生产管理—生产计划选择销售合同</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_Plan_Singlesel_Saleorder extends BaseAction implements Operate
{
  private static final String SINGLE_SALEORDER_SQL = "SELECT * FROM VW_PLAN_SINGLE_SALEORDER WHERE fgsid= ";

  private EngineDataSet dsSaleOrder  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null;
  /**
   * 得到销售合同信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同信息的实例
   */
  public static B_Plan_Singlesel_Saleorder getInstance(HttpServletRequest request)
  {
    B_Plan_Singlesel_Saleorder singleSelectSaleOrderBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "singleSelectSaleOrderBean";
      singleSelectSaleOrderBean = (B_Plan_Singlesel_Saleorder)session.getAttribute(beanName);
      if(singleSelectSaleOrderBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        singleSelectSaleOrderBean = new B_Plan_Singlesel_Saleorder();
        singleSelectSaleOrderBean.qtyFormat = loginBean.getQtyFormat();

        singleSelectSaleOrderBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, singleSelectSaleOrderBean);
      }
    }
    return singleSelectSaleOrderBean;
  }

  /**
   * 构造函数
   */
  private B_Plan_Singlesel_Saleorder()
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
    setDataSetProperty(dsSaleOrder, null);

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
    if(dsSaleOrder != null){
      dsSaleOrder.close();
      dsSaleOrder = null;
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
      String SQL = SINGLE_SALEORDER_SQL + fgsid;
      dsSaleOrder.setQueryString(SQL);
      dsSaleOrder.setRowMax(null);
    }
  }
  public final EngineDataSet getOneTable()
  {
    return dsSaleOrder;
  }
}



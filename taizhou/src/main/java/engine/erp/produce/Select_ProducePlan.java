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
 * <p>Title: 生产子系统_下达物料需求选择生产计划</p>
 * <p>Description: 生产子系统_下达物料需求选择生产计划</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class Select_ProducePlan extends BaseAction implements Operate
{
  private static final String IMPORT_PRODUCEPLAN_SQL = "SELECT * FROM sc_jh WHERE (isrefer=1 AND zt<>2 AND zt<>3 AND zt<>8 AND zt<>4) AND fgsid='?' AND ? ORDER BY jhh DESC";
  private static final String IMPORT_PLAN_SQL = "SELECT * FROM sc_jh WHERE (isrefer=1 AND zt<>2 AND zt<>3 AND zt<>8 AND zt<>4) AND fgsid='?' and deptid ='?' AND ? ORDER BY jhh DESC";

  private EngineDataSet dsProduce  = new EngineDataSet();
  private EngineRow locateResult = null;

  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null;
  /**
   * 得到生产计划信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回生产计划信息的实例
   */
  public static Select_ProducePlan getInstance(HttpServletRequest request)
  {
    Select_ProducePlan select_ProducePlanBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "select_ProducePlanBean";
      select_ProducePlanBean = (Select_ProducePlan)session.getAttribute(beanName);
      if(select_ProducePlanBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        select_ProducePlanBean = new Select_ProducePlan();
        select_ProducePlanBean.qtyFormat =loginBean.getQtyFormat();
        select_ProducePlanBean.fgsid = loginBean.getFirstDeptID();

        select_ProducePlanBean.dsProduce.setColumnFormat("zsl", select_ProducePlanBean.qtyFormat);
        session.setAttribute(beanName, select_ProducePlanBean);
      }
    }
    return select_ProducePlanBean;
  }

  /**
   * 构造函数
   */
  private Select_ProducePlan()
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
    setDataSetProperty(dsProduce, null);

    addObactioner(String.valueOf(INIT), new Init());
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
    if(dsProduce != null){
      dsProduce.close();
      dsProduce = null;
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
      String deptid = request.getParameter("deptid");
      String jhlx = request.getParameter("jhlx");
      if(jhlx.equals("1"))
        jhlx= "jhlx="+jhlx;
      else
        jhlx = "jhlx IS NULL OR jhlx="+jhlx;
      String SQL = null;
      if(deptid.equals(""))
        SQL = combineSQL(IMPORT_PRODUCEPLAN_SQL,"?",new String[]{fgsid, jhlx});
      else
        SQL = combineSQL(IMPORT_PLAN_SQL,"?",new String[]{fgsid, deptid, jhlx});
      dsProduce.setQueryString(SQL);
        dsProduce.setRowMax(null);
    }
  }
  public final EngineDataSet getOneTable()
  {
    return dsProduce;
  }
}
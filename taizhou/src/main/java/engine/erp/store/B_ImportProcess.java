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
 * <p>Title: 库存管理_自制收货单引入生产加工单</p>
 * <p>Description: 库存管理_自制收货单引入生产加工单</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_ImportProcess extends BaseAction implements Operate
{
  private static final String IMPORT_PROCESS_SQL = "SELECT DISTINCT d.jgdid,d.deptid, d.jgdh,d.describe,d.jglx,d.sfwjg,d.dwtxid,d.rq,d.zdr,d.zdrq,d.zt,d.zsl,d.fgsid,d.scjhid,d.rwdid FROM( "
  +" SELECT a.jgdid, a.cpid, a.sl,a.yrksl,a.scsl,a.yrkscsl, b.storeid FROM sc_jgdmx a, kc_dm b WHERE  a.cpid=b.cpid AND (b.storeid IS NULL OR b.storeid='?') "
  +" ) c, sc_jgd d WHERE nvl(c.yrksl,0)<nvl(c.sl,0) AND c.jgdid=d.jgdid AND d.zt<>8 AND d.fgsid='?' AND (d.sfwjg IS NULL OR d.sfwjg='?') ";//自制收货单引用
  //private static final String SINGLE_PROCESSMATERAIL_SQL = "SELECT * FROM VW_DRAW_SELECTMATERAIL  WHERE fgsid= ";//生产领料单引用

  private EngineDataSet dsProcess  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null;
  /**
   * 得到生产加工单信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回生产加工单信息的实例
   */
  public static B_ImportProcess getInstance(HttpServletRequest request)
  {
    B_ImportProcess b_importProcessBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_importProcessBean";
      b_importProcessBean = (B_ImportProcess)session.getAttribute(beanName);
      if(b_importProcessBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_importProcessBean = new B_ImportProcess();
        b_importProcessBean.qtyFormat = loginBean.getQtyFormat();

        b_importProcessBean.fgsid = loginBean.getFirstDeptID();
        b_importProcessBean.dsProcess.setColumnFormat("sl", b_importProcessBean.qtyFormat);
        session.setAttribute(beanName, b_importProcessBean);
      }
    }
    return b_importProcessBean;
  }

  /**
   * 构造函数
   */
  private B_ImportProcess()
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
    setDataSetProperty(dsProcess, null);

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
    if(dsProcess != null){
      dsProcess.close();
      dsProcess = null;
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
      String deptid = request.getParameter("deptid");
      String storeid = request.getParameter("storeid");
      String isout = request.getParameter("isout");
      if(storeid==null)
        storeid = "";
      String SQL = combineSQL(IMPORT_PROCESS_SQL, "?", new String[]{storeid,fgsid, isout});
      if(!deptid.equals(""))
        SQL = SQL+" AND d.deptid= " +deptid;
      dsProcess.setQueryString(SQL);
        dsProcess.setRowMax(null);
    }
  }
  public final EngineDataSet getOneTable()
  {
    return dsProcess;
  }
}

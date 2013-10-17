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
 * <p>Title: 库存管理_生产退料单引入生产领料单</p>
 * <p>Description: 库存管理_生产退料单引入生产领料单</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_ImportReceive extends BaseAction implements Operate
{
  private static final String IMPORT_RECEIVE_SQL = "SELECT a.drawcode, a.drawdate,a.creator, a.filialeid ,a.drawtype, a.storeid, b.*,c.cpbm,c.pm,c.gg, c.jldw,c.hsdw,c.scydw,b.drawDetailID "
         + " FROM sc_drawmaterial a, sc_drawmaterialdetail b, kc_dm c "
         + " WHERE a.drawid=b.drawid AND a.drawtype='1' AND isout='?' AND b.cpid=c.cpid AND a.filialeid='?' AND (a.storeid IS NULL OR a.storeid='?') ";
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
  public static B_ImportReceive getInstance(HttpServletRequest request)
  {
    B_ImportReceive B_ImportReceiveBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_ImportReceiveBean";
      B_ImportReceiveBean = (B_ImportReceive)session.getAttribute(beanName);
      if(B_ImportReceiveBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        B_ImportReceiveBean = new B_ImportReceive();
        B_ImportReceiveBean.qtyFormat = loginBean.getQtyFormat();

        B_ImportReceiveBean.fgsid = loginBean.getFirstDeptID();
        B_ImportReceiveBean.dsProcess.setColumnFormat("drawNum", B_ImportReceiveBean.qtyFormat);
        B_ImportReceiveBean.dsProcess.setColumnFormat("drawBigNum", B_ImportReceiveBean.qtyFormat);
        B_ImportReceiveBean.dsProcess.setColumnFormat("produceNum", B_ImportReceiveBean.qtyFormat);
        session.setAttribute(beanName, B_ImportReceiveBean);
      }
    }
    return B_ImportReceiveBean;
  }

  /**
   * 构造函数
   */
  private B_ImportReceive()
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
      String storeid = request.getParameter("storeid");
      String isout = request.getParameter("isout");
      String SQL = combineSQL(IMPORT_RECEIVE_SQL, "?", new String[]{fgsid, storeid});
      dsProcess.setQueryString(SQL);
        dsProcess.setRowMax(null);
    }
  }
  /*
  *得到一行信息
    */
  public final RowMap getLookupRow(String drawDetailID)
  {
    RowMap row = new RowMap();
    if(drawDetailID == null || drawDetailID.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsProcess, "drawdetailid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "drawdetailid");
    locateRow.setValue(0, drawDetailID);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsProcess;
  }
}

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
 * <p>Title: 生产管理-车间流转单引入任务单</p>
 * <p>Description: 生产管理-车间流转单引入任务单</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨建国
 * @version 1.0
 */

public final class PieceSelectBill extends BaseAction implements Operate
{
  //private static final String IMPORT_PROCESS_SQL = "SELECT * FROM sc_jgd WHERE fgsid= ";//自制收货单引用
  private static final String PIECE_SELECT_BILL = " SELECT * from VW_SC_PIECEWAGE_OTHER_BILLCPID a "
                              + " where 1=1 ? ? ";//引车间流转单和自制收货单

  private EngineDataSet dsProcessTaskMaster  = new EngineDataSet();
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
  public static PieceSelectBill getInstance(HttpServletRequest request)
  {
    PieceSelectBill pieceSelectBillBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "pieceSelectBillBean";
      pieceSelectBillBean = (PieceSelectBill)session.getAttribute(beanName);
      if(pieceSelectBillBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        pieceSelectBillBean = new PieceSelectBill();
        pieceSelectBillBean.qtyFormat = loginBean.getQtyFormat();

        pieceSelectBillBean.fgsid = loginBean.getFirstDeptID();
        pieceSelectBillBean.dsProcessTaskMaster.setColumnFormat("zsl", pieceSelectBillBean.qtyFormat);
        session.setAttribute(beanName, pieceSelectBillBean);
      }
    }
    return pieceSelectBillBean;
  }

  /**
   * 构造函数
   */
  private PieceSelectBill()
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
    setDataSetProperty(dsProcessTaskMaster, null);

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
    if(dsProcessTaskMaster != null){
      dsProcessTaskMaster.close();
      dsProcessTaskMaster = null;
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
      deptid = deptid == null?"":deptid;
      String gzzid = request.getParameter("gzzid");
      String djh = request.getParameter("djh");
      djh = djh == null?"":djh;
      String SQL = "";
      gzzid = gzzid == null?"":gzzid;
      if(!deptid.equals(""))
        SQL = SQL+" AND deptid= " + deptid;
      if(!gzzid.equals(""))
        SQL = SQL+" AND ( gzzid is null or gzzid= " + gzzid + ")";
      if(!djh.equals(""))
        SQL = SQL+" AND (  djh like '" + djh + "%')";
      SQL = combineSQL(PIECE_SELECT_BILL, "?", new String[]{SQL});
      dsProcessTaskMaster.setQueryString(SQL);
        dsProcessTaskMaster.setRowMax(null);
    }
  }
  public final EngineDataSet getOneTable()
  {
    return dsProcessTaskMaster;
  }
}
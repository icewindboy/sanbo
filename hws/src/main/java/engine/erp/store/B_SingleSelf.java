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
 * <p>Title: 库存管理_复制自制收货单</p>
 * <p>Description: 库存管理_复制自制收货单</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_SingleSelf extends BaseAction implements Operate
{
  //private static final String IMPORT_PROCESS_SQL = "SELECT * FROM sc_jgd WHERE fgsid= ";//自制收货单引用
  private static final String SINGLE_SELF_SQL = "SELECT a.* FROM sc_receiveprod a WHERE a.createdate<=to_date('?','YYYY-MM-DD') AND a.createdate>=to_date('?','YYYY-MM-DD') AND a.filialeID='?'";

  private EngineDataSet dsSelfMaster  = new EngineDataSet();
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
  public static B_SingleSelf getInstance(HttpServletRequest request)
  {
    B_SingleSelf singleselfBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "singleselfBean";
      singleselfBean = (B_SingleSelf)session.getAttribute(beanName);
      if(singleselfBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        singleselfBean = new B_SingleSelf();
        singleselfBean.qtyFormat = loginBean.getQtyFormat();

        singleselfBean.fgsid = loginBean.getFirstDeptID();
        singleselfBean.dsSelfMaster.setColumnFormat("totalNum", singleselfBean.qtyFormat);
        session.setAttribute(beanName, singleselfBean);
      }
    }
    return singleselfBean;
  }

  /**
   * 构造函数
   */
  private B_SingleSelf()
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
    setDataSetProperty(dsSelfMaster, null);

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
    if(dsSelfMaster != null){
      dsSelfMaster.close();
      dsSelfMaster = null;
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
      Date startdate = new Date();
      Date enddate = new Date(startdate.getTime() - 30*60*60*24*1000);
      String today = new SimpleDateFormat("yyyy-MM-dd").format(startdate);
      String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
      String SQL = combineSQL(SINGLE_SELF_SQL, "?", new String[]{today, endDate, fgsid});
      dsSelfMaster.setQueryString(SQL);
        dsSelfMaster.setRowMax(null);
    }
  }
    /*
  *得到一行信息
    */
  public final RowMap getLookupRow(String receiveid)
  {
    RowMap row = new RowMap();
    if(receiveid == null || receiveid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsSelfMaster, "receiveid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "receiveid");
    locateRow.setValue(0, receiveid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsSelfMaster;
  }
}

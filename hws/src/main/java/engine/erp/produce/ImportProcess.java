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
 * <p>Title: 生产子系统_工人工作量引入生产加工单</p>
 * <p>Description: 生产子系统_工人工作量引入生产加工单</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class ImportProcess extends BaseAction implements Operate
{
  private static final String IMPORT_PROCESS_SQL = "SELECT * FROM VW_WORKLOAD_SEL_PROCESS WHERE  (deptid IS NULL OR deptid=?) ";

  private EngineDataSet dsprocess  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String qtyFormat = null;
  private QueryBasic fixedQuery = new QueryFixedItem();
  /**
   * 得到生产加工单信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回生产加工单信息的实例
   */
  public static ImportProcess getInstance(HttpServletRequest request)
  {
    ImportProcess importProcessBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "importProcessBean";
      importProcessBean = (ImportProcess)session.getAttribute(beanName);
      if(importProcessBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        importProcessBean = new ImportProcess();
        importProcessBean.qtyFormat = loginBean.getQtyFormat();
        importProcessBean.dsprocess.setColumnFormat("sl", importProcessBean.qtyFormat);
        session.setAttribute(beanName, importProcessBean);
      }
    }
    return importProcessBean;
  }

  /**
   * 构造函数
   */
  private ImportProcess()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /**
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }
  /**
   * Implement this engine.project.OperateCommon abstract method
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsprocess, null);

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
    if(dsprocess != null){
      dsprocess.close();
      dsprocess = null;
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
      String SQL = combineSQL(IMPORT_PROCESS_SQL, "?", new String[]{deptid,deptid});
      dsprocess.setQueryString(SQL);
      dsprocess.setRowMax(null);
    }
  }
  /*
  *得到一行信息
    */
  public final RowMap getLookupRow(String jgdmxid)
  {
    RowMap row = new RowMap();
    if(jgdmxid == null || jgdmxid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsprocess, "jgdmxid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "jgdmxid");
    locateRow.setValue(0, jgdmxid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsprocess;
  }
}

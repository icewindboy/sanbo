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
 * <p>Title: 生产子系统_生产加工单引入生产任务</p>
 * <p>Description: 生产子系统_生产加工单引入生产任务</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class ImportTask extends BaseAction implements Operate
{
  private static final String IMPORT_TASK_SQL = "SELECT * FROM VW_PRODUCEPROCESS_SEL_TASK WHERE fgsid=? and (deptid IS NULL OR deptid=?) ORDER BY rwdh DESC ";

  private EngineDataSet dstask  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null;
  private QueryBasic fixedQuery = new QueryFixedItem();
  /**
   * 得到物料需求计划信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回物料需求计划信息的实例
   */
  public static ImportTask getInstance(HttpServletRequest request)
  {
    ImportTask importTaskBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "importTaskBean";
      importTaskBean = (ImportTask)session.getAttribute(beanName);
      if(importTaskBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        importTaskBean = new ImportTask();
        importTaskBean.qtyFormat = loginBean.getQtyFormat();
        importTaskBean.dstask.setColumnFormat("sl", importTaskBean.qtyFormat);
        importTaskBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, importTaskBean);
      }
    }
    return importTaskBean;
  }

  /**
   * 构造函数
   */
  private ImportTask()
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
    setDataSetProperty(dstask, null);

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
    if(dstask != null){
      dstask.close();
      dstask = null;
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
      String SQL = combineSQL(IMPORT_TASK_SQL, "?", new String[]{fgsid,deptid,deptid});
      dstask.setQueryString(SQL);
      dstask.setRowMax(null);
    }
  }
  /*
  *得到一行信息
    */
  public final RowMap getLookupRow(String rwdmxid)
  {
    RowMap row = new RowMap();
    if(rwdmxid == null || rwdmxid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dstask, "rwdmxid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "rwdmxid");
    locateRow.setValue(0, rwdmxid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dstask;
  }
}

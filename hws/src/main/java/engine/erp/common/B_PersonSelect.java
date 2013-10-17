package engine.erp.common;

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
import engine.common.*;

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
 * <p>Title: 销售子系统_销售货物选择</p>
 * <p>Description: 销售子系统_销售货物选择</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public final class B_PersonSelect extends BaseAction implements Operate
{
  private static final String PERSON_BM_SQL
      = "SELECT * FROM emp WHERE (isdelete=?) AND deptid IN (SELECT deptid FROM bm WHERE ? AND ismember=0 AND isdelete=0) ? ORDER BY bm";
  private static final String PERSON_GH_SQL
      = "SELECT * FROM emp WHERE (isdelete=?) AND deptid IN (SELECT deptid FROM bm WHERE ? AND ismember=0 AND isdelete=0) ? ORDER BY gh";

  private EngineDataSet dsPerson  = new EngineDataSet();//主表

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  private String fgsid = null;  //分公司ID
  private String stat = "0";
  private User user = null;
  private String PERSON_SQL = null;
  public  boolean isWorkNo = false;
  /**
   * 得到往来单位信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回往来单位信息的实例
   */
  public static B_PersonSelect getInstance(HttpServletRequest request)
  {
    B_PersonSelect personSelectBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "personSelectBean";
      personSelectBean = (B_PersonSelect)session.getAttribute(beanName);
      if(personSelectBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        //String fgsid = loginBean.getFirstDeptID();
        String qtyFormat  = loginBean.getQtyFormat();
        String priceFormat  = loginBean.getPriceFormat();
        personSelectBean = new B_PersonSelect(loginBean.getUser());
        session.setAttribute(beanName, personSelectBean);
        personSelectBean.isWorkNo = loginBean.getSystemParam("SYS_CUST_NAME").equals("ruijiao");
        personSelectBean.PERSON_SQL = personSelectBean.isWorkNo ?
                                      personSelectBean.PERSON_GH_SQL : personSelectBean.PERSON_BM_SQL;
      }
    }
    return personSelectBean;
  }

  /**
   * 构造函数
   */
  private B_PersonSelect(User user)
  {
    this.user = user;
    this.fgsid = user.getFilialeId();
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
    setDataSetProperty(dsPerson, null);

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
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
    if(dsPerson != null){
      dsPerson.close();
      dsPerson = null;
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
      String isOff = data.getParameter("isoff","");
      //boolean isOff = data.getParameter("isoff", "").equals("1");
      stat = isOff.equals("1") ? "2" :
             isOff.equals("0$1") ? "2 OR isdelete=0" : "0";//0+1改为0$1
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //初始化查询项目和内容
      fixedQuery.getSearchRow().clear();
      //替换可变字符串，组装SQL
      String deptids = user.getHandleDeptValue("deptid");
      String SQL = combineSQL(PERSON_SQL, "?", new String[]{stat, deptids, ""});
      dsPerson.setQueryString(SQL);
      dsPerson.setRowMax(null);
    }
  }

  /**
   *  查询操作
   */
  class Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND " + SQL;
      //替换可变字符串，组装SQL
      String deptids = user.getHandleDeptValue("deptid");
      SQL = combineSQL(PERSON_SQL, "?", new String[]{stat, deptids, SQL});
      if(!dsPerson.getQueryString().equals(SQL))
      {
        dsPerson.setQueryString(SQL);
        dsPerson.setRowMax(null);
      }
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet ds = dsPerson;
      if(!ds.isOpen())
        ds.open();
      //初始化固定的查询项目
      fixedQuery.addShowColumn("", new QueryColumn[]{//"",表示默认的表名
        new QueryColumn(ds.getColumn(isWorkNo ? "gh": "bm"), null, null, null),
        new QueryColumn(ds.getColumn("xm"), null, null, null),
        new QueryColumn(ds.getColumn("deptid"), null, null, null)
      });
      isInitQuery = true;
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
   * 得到数据集对象
   * @return 返回数据集对象
   */
  public final EngineDataSet getOneTable()
  {
    return dsPerson;
  }
}

package engine.erp.finance.xixing;

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
 * <p>Title: 外加工结算_货物选择</p>
 * <p>Description: 外加工结算_货物选择</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @version 1.0
 */

public final class B_wjgdhwList extends BaseAction implements Operate
{
  private static final String IMPORT_BUY_JHD_SQL = "SELECT * FROM VW_SC_WJG_BALANCE WHERE fgsid=? ? ";//分公司; 往来单位(根据购货单位提取数据)
  private EngineDataSet dsJHDProduct  = new EngineDataSet();//数据集
  private EngineRow locateResult = null;//用于定位数据集数据
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  /**
   *从会话中取出本类实例
   *
   * */
  public static B_wjgdhwList getInstance(HttpServletRequest request)
  {
    B_wjgdhwList b_wjgdhwListBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_wjgdhwListBean";
      b_wjgdhwListBean = (B_wjgdhwList)session.getAttribute(beanName);
      if(b_wjgdhwListBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_wjgdhwListBean = new B_wjgdhwList();
        b_wjgdhwListBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, b_wjgdhwListBean);
      }
    }
    return b_wjgdhwListBean;
  }
  /**
   * 构造函数
   */
  private B_wjgdhwList()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsJHDProduct, null);//空数据集
    addObactioner(String.valueOf(INIT), new Init());//注册初始化操作
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
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
      String operate = request.getParameter("operate");
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
    if(dsJHDProduct != null){
      dsJHDProduct.close();
      dsJHDProduct = null;
    }
    log = null;
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
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }
  //得到一行信息
  public final RowMap getLookupRow(String jgdmxid)
  {
    RowMap row = new RowMap();
    if(jgdmxid == null || jgdmxid.equals(""))
      return row;//返回
    EngineRow locateRow = new EngineRow(dsJHDProduct, "jgdmxid");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "jgdmxid");
    locateRow.setValue(0, jgdmxid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsJHDProduct;
  }
  /**
   * 初始化操作的的执行者
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");//where is it coming?
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      String dwtxid = request.getParameter("dwtxid");
      String SQL = combineSQL(IMPORT_BUY_JHD_SQL, "?", new String[]{fgsid," AND dwtxid="+dwtxid});
      dsJHDProduct.setQueryString(SQL);
      dsJHDProduct.setRowMax(null);
    }
  }
  /**
   *  查询操作
   */
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(IMPORT_BUY_JHD_SQL, "?", new String[]{fgsid, SQL});
      if(!dsJHDProduct.getQueryString().equals(SQL))
      {
        dsJHDProduct.setQueryString(SQL);
        dsJHDProduct.setRowMax(null);
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
     EngineDataSet master = dsJHDProduct;
     //EngineDataSet detail = dsDetailTable;
     if(!master.isOpen())
       master.open();
     //初始化固定的查询项目
     fixedQuery = new QueryFixedItem();
     fixedQuery.addShowColumn("", new QueryColumn[]{
       new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//供货单位
       new QueryColumn(master.getColumn("jgdh"), null, null, null, null, "="), //加工单号
       new QueryColumn(master.getColumn("rq"), null, null, null, "a", ">="),//交货日期
       new QueryColumn(master.getColumn("rq"), null, null, null, "b", "<="),//交货日期
       new QueryColumn(master.getColumn("deptid"), null, null, null, null, "=")//部门id
     });
     isInitQuery = true;
  }
}
}
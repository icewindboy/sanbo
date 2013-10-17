package engine.erp.store.xixing;

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
 * <p>Title: 销售出库单查询已审提单
 * </p>
 * <p>Description: 销售出库单查询已审提单</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zjb
 * @version 1.0
 */

public final class Td_Check extends BaseAction implements Operate
{
  private static final String SINGLE_TD_SQL
      = "SELECT DISTINCT tdid, deptid,storeid, dwtxid, personid, khlx, tdrq, jhfhrq,tdbh, fgsid, zt, zsl, zje"
      + " FROM VW_SALEOUTSTORE_SINGLE_LADDING where djlx=1 and zt=1 and fgsid=? ? ?";

  private EngineDataSet dsTd  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null;
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public String dwtxid = "";
  public String personid = "";
  /**
   * 销售出库单查询已审提单实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 销售出库单查询已审提单
   */
  public static Td_Check getInstance(HttpServletRequest request)
  {
    Td_Check tdckBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "tdckBean";
      tdckBean = (Td_Check)session.getAttribute(beanName);
      if(tdckBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        tdckBean = new Td_Check();
        tdckBean.qtyFormat = loginBean.getQtyFormat();

        tdckBean.fgsid = loginBean.getFirstDeptID();
        tdckBean.dsTd.setColumnFormat("zsl", tdckBean.qtyFormat);
        tdckBean.dsTd.setColumnFormat("zje", tdckBean.qtyFormat);
        session.setAttribute(beanName, tdckBean);
      }
    }
    return tdckBean;
  }

  /**
   * 构造函数
   */
  private Td_Check()
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
    setDataSetProperty(dsTd, null);

    addObactioner(String.valueOf(INIT), new Init());
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
      String opearate = request.getParameter(OPERATE_KEY);
      if(opearate != null && opearate.trim().length() > 0)
      {
        RunData data = notifyObactioners(opearate, request, response, null);
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
    if(dsTd != null){
      dsTd.close();
      dsTd = null;
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
      String SQL = combineSQL(SINGLE_TD_SQL, "?", new String[]{fgsid});
      dsTd.setQueryString(SQL);
      dsTd.setRowMax(null);
    }
  }
  /*
  *得到一行信息
  */
  public final RowMap getLookupRow(String htid)
  {
    RowMap row = new RowMap();
    if(htid == null || htid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsTd, "htid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "htid");
    locateRow.setValue(0, htid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsTd;
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
   /*  查询操作
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
       SQL = combineSQL(SINGLE_TD_SQL, "?", new String[]{fgsid,SQL});
         dsTd.setQueryString(SQL);
         dsTd.setRowMax(null);
    }
    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsTd;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("tdbh"), null, null, null, null, "="),//发货单编号
        new QueryColumn(master.getColumn("jhfhrq"), null, null, null, "a", ">="),//计划发货日期
        new QueryColumn(master.getColumn("jhfhrq"), null, null, null, "b", "<="),//计划发货日期
        new QueryColumn(master.getColumn("storeid"), null, null, null, null, "=")//仓库id
      });
      isInitQuery = true;
   }
}
}



package engine.erp.subsection;

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
 * <p>Title: 销售子系统_提货单管理引入合同货物--</p>
 * <p>Description: 销售子系统_提货单管理引入合同货物</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */

public final class ImportOrderProduct extends BaseAction implements Operate
{
  private static final String IMPORT_SALE_ORDER_SQL = "SELECT * FROM VW_SALE_IMPORT_TD_ORDER_DETAIL WHERE 1=1 ? ";//分公司; 往来单位(根据购货单位提取数据)
  private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();//数据集
  private EngineRow locateResult = null;//用于定位数据集数据
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String dwtxid="";
  private String storeid ="";
  private String personid ="";
  private String khlx ="";
  private String jsfsid ="";

  public String djlx="";
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  /**
   *从会话中取出本类实例
   *
   * */
  public static ImportOrderProduct getInstance(HttpServletRequest request)
  {
    ImportOrderProduct ImportOrderProductBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "ImportOrderProductBean";
      ImportOrderProductBean = (ImportOrderProduct)session.getAttribute(beanName);
      if(ImportOrderProductBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        ImportOrderProductBean = new ImportOrderProduct();
        ImportOrderProductBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, ImportOrderProductBean);
      }
    }
    return ImportOrderProductBean;
  }
  /**
   * 构造函数
   */
  private ImportOrderProduct()
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
    setDataSetProperty(dsSaleOrderProduct, null);//空数据集
    addObactioner(String.valueOf(INIT), new Init());//注册初始化操作
    addObactioner(String.valueOf(FIXED_SEARCH),new Master_Search());
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
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
      }
  /**
   * Session失效时，调用的函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsSaleOrderProduct != null){
      dsSaleOrderProduct.close();
      dsSaleOrderProduct = null;
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
  //得到一行信息
  public final RowMap getLookupRow(String hthwid)
  {
    RowMap row = new RowMap();
    if(hthwid == null || hthwid.equals(""))
      return row;//返回
    EngineRow locateRow = new EngineRow(dsSaleOrderProduct, "hthwid");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "hthwid");
    locateRow.setValue(0, hthwid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsSaleOrderProduct;
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
      dwtxid = request.getParameter("dwtxid").trim();
      //dwtxid=dwtxid.equals("")?"":" AND dwtxid="+dwtxid;
      storeid = request.getParameter("storeid").trim();//仓库
      personid = request.getParameter("personid").trim();
      //khlx = request.getParameter("khlx").trim();
      jsfsid = request.getParameter("jsfsid").trim();
      djlx =  request.getParameter("djlx").trim();//单据类型
      if(dwtxid.equals("")||storeid.equals("")||djlx.equals("")||personid.equals(""))
        return;
      String SQL = "";//combineSQL(IMPORT_SALE_ORDER_SQL, "?", new String[]{" AND fgsid="+fgsid+" AND dwtxid="+dwtxid+" AND (storeid is null OR storeid="+storeid+") "});
      if(djlx.equals("-1"))
        SQL=combineSQL(IMPORT_SALE_ORDER_SQL, "?", new String[]{" AND jsfsid='"+jsfsid+"' AND personid='"+personid+"' AND fgsid='"+fgsid+"' AND dwtxid='"+dwtxid+"' AND (storeid is null OR storeid='"+storeid+"') AND isrefer=1 AND zt<>4 AND zt<>8  AND lb=2  "});
      else
        SQL=combineSQL(IMPORT_SALE_ORDER_SQL, "?", new String[]{" AND jsfsid='"+jsfsid+"' AND personid='"+personid+"' AND fgsid='"+fgsid+"' AND dwtxid='"+dwtxid+"' AND (storeid is null OR storeid='"+storeid+"') AND isrefer=1 AND zt<>4 AND zt<>8 AND lb=1 and sl>0 "});
      dsSaleOrderProduct.setQueryString(SQL);
      dsSaleOrderProduct.setRowMax(null);
      RowMap row = fixedQuery.getSearchRow();
      row.put("zt","1");
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
      String zt=data.getParameter("zt");
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;//+" AND jsfsid='"+jsfsid+"' AND personid='"+personid+"' AND khlx='"+khlx+"' AND dwtxid='"+dwtxid+"'  AND fgsid='"+fgsid+"' AND (storeid is null or storeid='"+storeid+"')";
      SQL=SQL+(djlx.equals("-1")?" AND lb=1 ":" AND lb=2 ")+" AND jsfsid='"+jsfsid+"' AND personid='"+personid+"' AND khlx='"+khlx+"' AND dwtxid='"+dwtxid+"'  AND fgsid='"+fgsid+"' AND (storeid is null or storeid='"+storeid+"')";
      SQL = combineSQL(IMPORT_SALE_ORDER_SQL, "?", new String[]{SQL});
      if(!dsSaleOrderProduct.getQueryString().equals(SQL))
      {
        dsSaleOrderProduct.setQueryString(SQL);
        dsSaleOrderProduct.setRowMax(null);
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
      EngineDataSet master = dsSaleOrderProduct;
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("htbh"), null, null, null, null, "="),//合同编号
        new QueryColumn(master.getColumn("htrq"), null, null, null, "a", ">="),//合同日期
        new QueryColumn(master.getColumn("htrq"), null, null, null, "b", "<="),//合同日期
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")//状态
      });
      isInitQuery = true;
    }
  }
}

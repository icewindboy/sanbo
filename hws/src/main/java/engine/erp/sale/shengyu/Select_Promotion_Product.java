package engine.erp.sale.shengyu;

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
import java.util.Hashtable;
import engine.util.MessageFormat;
import engine.util.log.Log;
/**
 * <p>Title: 销售子系统_</p>
 * <p>Description: 销售子系统_</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class Select_Promotion_Product extends BaseAction implements Operate
{
  private static final String IMPORT_SALE_SQL = "select t.dwtxid,t.cpid,t.startdate,t.enddate,t.memo,t.prom_price,b.wzdjid,c.storeid, c.jldw from xs_promotion t ,xs_wzdj b,VW_KC_DM c WHERE t.cpid=b.cpid(+) AND t.cpid=c.cpid AND t.dwtxid='?' AND t.startdate<=to_date('?','yyyy-mm-dd') AND t.enddate>=to_date('?','yyyy-mm-dd') and (c.storeid is null or c.storeid='?' ) ? ";//分公司; 往来单位
  private static final String BACK_SALE_SQL = "select t.dwtxid,t.cpid,t.startdate,t.enddate,t.memo,t.prom_price,b.wzdjid,c.storeid,c.jldw from xs_promotion t ,xs_wzdj b,VW_KC_DM c WHERE t.cpid=b.cpid(+) AND t.cpid=c.cpid AND t.dwtxid='?'  and (c.storeid is null or c.storeid='?' ) ? ";//分公司; 往来单位

  private static final String SALE_SQL = "select t.dwtxid,t.cpid,t.startdate,t.enddate,t.memo,t.prom_price,b.wzdjid,c.storeid,c.jldw from xs_promotion t ,xs_wzdj b,VW_KC_DM c WHERE t.cpid=b.cpid(+) AND t.cpid=c.cpid AND t.dwtxid={dwtxid}  and (c.storeid is null or c.storeid={storeid} ) {other} AND fgsid={fgsid} ";//分公司; 往来单位


  //private static final String MASTER_SQL    = "SELECT * FROM VW_SALE_INVOICE_IMPORT_TDDETAL WHERE  fgsid=? ?  order by tdbh,dwtxid ";
  private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  private String dwtxid = "";
  private String storeid = "";
  private String personid = "";
  private boolean isback = false;
  private String methodName = null;   //调用window.opener中的方法
  public String[] inputName = null;    //
  public String[] fieldName = null;    //字段名称
  public String srcFrm=null;           //传递的原form的名称
  /**
   * 析构函数
   * */
  public static Select_Promotion_Product getInstance(HttpServletRequest request)
  {
    Select_Promotion_Product select_Promotion_ProductBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "select_Promotion_ProductBean";
      select_Promotion_ProductBean = (Select_Promotion_Product)session.getAttribute(beanName);
      if(select_Promotion_ProductBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        select_Promotion_ProductBean = new Select_Promotion_Product();
        select_Promotion_ProductBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, select_Promotion_ProductBean);
      }
    }
    return select_Promotion_ProductBean;
  }
  /**
   * 构造函数
   */
  private Select_Promotion_Product()
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
    setDataSetProperty(dsSaleOrderProduct, null);//无数据
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(PROD_CHANGE), new CodeSearch());
    addObactioner(String.valueOf(PROD_NAME_CHANGE), new NameSearch());
  }
  /**
   *网页调用
   * */
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
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }

  /**
   * session失效时调用
   * */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsSaleOrderProduct != null){
      dsSaleOrderProduct.close();
      dsSaleOrderProduct = null;
    }
    log = null;
  }
  protected final Class childClassName()
  {
    return getClass();
  }
  //得到查询的行
  public final RowMap getLookupRow(String cpid)
  {
    RowMap row = new RowMap();
    if(cpid == null || cpid.equals(""))
      return row;//返回
    EngineRow locateRow = new EngineRow(dsSaleOrderProduct, "cpid");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "cpid");
    locateRow.setValue(0, cpid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsSaleOrderProduct;
  }



  /**
    * 初始化表单数据及其SQL语句
    * @return 返回子类的类名
    */
   private void init(RunData data) throws Exception
   {
     HttpServletRequest request = data.getRequest();
     retuUrl = request.getParameter("src");
     retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
     dwtxid = request.getParameter("dwtxid");
     storeid = request.getParameter("storeid");
     LoginBean loginBean = LoginBean.getInstance(request);
     fgsid=loginBean.getFirstDeptID();
     if(request.getParameter("isback")!=null&&request.getParameter("isback").equals("1"))
        isback = true;
   //得到关闭窗体前要调用的方法
     methodName = data.getParameter("method");
     srcFrm = data.getParameter("srcFrm");

     inputName = data.getParameterValues("srcVar");
     fieldName = data.getParameterValues("fieldVar");
  }

/**
 * 初始化
 *
 * */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      init(data);
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String SQL = "";
      if(isback)
        SQL=combineSQL(BACK_SALE_SQL,"?",new String[]{dwtxid,storeid,SQL});
      else
        SQL=combineSQL(IMPORT_SALE_SQL,"?",new String[]{dwtxid,today,today,storeid,SQL});

      dsSaleOrderProduct.setQueryString(SQL);
      dsSaleOrderProduct.setRowMax(null);
    }
  }
/**
 *
 *
 * */
/**
 *  查询操作
 */
class Master_Search implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest request = data.getRequest();
    initQueryItem(data.getRequest());
    fixedQuery.setSearchValue(data.getRequest());
    String cpid = request.getParameter("cpid");
    String pm = request.getParameter("pm");
    String prom_price = request.getParameter("prom_price");
    String SQL="";
    if(!cpid.equals(""))
      SQL = SQL+" AND t.cpid='"+cpid+"'";
    if(!prom_price.equals(""))
      SQL = SQL+" AND t.prom_price='"+prom_price+"'";
    if(!pm.equals(""))
      SQL = SQL+" AND c.product like '%"+pm+"%'";

    String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    if(isback)
      SQL=combineSQL(BACK_SALE_SQL,"?",new String[]{dwtxid,storeid,SQL});
    else
        SQL=combineSQL(IMPORT_SALE_SQL,"?",new String[]{dwtxid,today,today,storeid,SQL});

    if(!dsSaleOrderProduct.getQueryString().equals(SQL))
    {
      dsSaleOrderProduct.setQueryString(SQL);
      dsSaleOrderProduct.setRowMax(null);
    }
  }

  /**
     * 得到需要调用window.opener中的方法的名称
     * @return 方法的名称
     */
    public String getMethodName()
    {
      if(methodName != null && methodName.length() == 0)
        return null;
      return methodName;
  }
  /**
    * 得到写日志的对象
    * @return 写日志的对象
    */
   public Log getLog()
   {
     return log;
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
    //初始化固定的查询项目
    fixedQuery = new QueryFixedItem();
    fixedQuery.addShowColumn("", new QueryColumn[]{
      new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
      new QueryColumn(master.getColumn("prom_price"), null, null, null, null, "=")
    });
    isInitQuery = true;
  }
  }
  /**
  * 通过产品编码得到产品信息的触发类
  */
 final class CodeSearch implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     //初始化查询项目和内容
     init(data);
     //table.getWhereInfo().clearWhereValues();
     String cpbm = data.getParameter("code", "");
     String storeid = data.getParameter("storeid", "");
     String dwtxid = data.getParameter("dwtxid", "");
     Hashtable table = new Hashtable();
     table.put("fgsid", fgsid);
     table.put("storeid", storeid);
     table.put("other", "AND cpbm LIKE '"+cpbm+"%'");
     table.put("dwtxid", dwtxid);
     //SQL = combineSQL(SALE_GOODS_SQL, "?", new String[]{"AND cpbm LIKE '"+cpbm+"%'", fgsid});
     String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
     String SQL = null;
      SQL=combineSQL(SALE_SQL,"?",new String[]{dwtxid,today,today,storeid,SQL});
      SQL = MessageFormat.format(SQL, table);
      System.out.print(SQL);
     EngineDataSet ds = getOneTable();
     ds.setQueryString(SQL);
     if(ds.isOpen())
     {
       ds.readyRefresh();
       ds.refresh();
     }
     else
       ds.openDataSet();
   }
 }

 /**
  * 通过产品品名规格得到产品信息的触发类
  */
 final class NameSearch implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     init(data);
     String name = data.getParameter("name", "");
     String storeid = data.getParameter("storeid", "");
     String dwtxid = data.getParameter("dwtxid", "");
     Hashtable table = new Hashtable();
     table.put("fgsid", fgsid);
     table.put("storeid", storeid);
     table.put("other", "AND product LIKE '%"+name+"%'");
     table.put("dwtxid", dwtxid);

     //SQL = combineSQL(SALE_GOODS_SQL, "?", new String[]{"AND product LIKE '%"+name+"%'", fgsid});

     String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
     String SQL = null;
     SQL=combineSQL(SALE_SQL,"?",new String[]{dwtxid,today,today,storeid,SQL});
     SQL = MessageFormat.format(SQL, table);

     System.out.print(SQL);
     EngineDataSet ds = getOneTable();
     ds.setQueryString(SQL);
     if(ds.isOpen())
     {
       ds.readyRefresh();
       ds.refresh();
     }
     else
       ds.openDataSet();
   }
 }

}
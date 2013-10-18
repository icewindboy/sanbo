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
 * <p>Title: 销售子系统_销售结算-引入提单货物</p>
 * <p>Description: 销售子系统_销售结算-引入提单货物</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class ImportLadingProduct extends BaseAction implements Operate
 {
   private static final String IMPORT_SALE_SQL = "SELECT * FROM vw_sale_autobalance WHERE fgsid=? ? order by tdrq ";//分公司; 往来单位
   private static final String MASTER_SQL    = "SELECT * FROM vw_sale_autobalance WHERE  fgsid=? ?  order by tdrq ";
   private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();
   private EngineRow locateResult = null;
   public  String retuUrl = null;
   private String fgsid = null;   //分公司ID
   private boolean isInitQuery = false; //是否已经初始化查询条件
   private QueryBasic fixedQuery = new QueryFixedItem();
   public String dwtxid = "";
   public String personid = "";
   /**
    * 析构函数
    * */
   public static ImportLadingProduct getInstance(HttpServletRequest request)
   {
     ImportLadingProduct ImportLadingProductBean = null;
     HttpSession session = request.getSession(true);
     synchronized (session)
     {
       String beanName = "ImportLadingProductBean";
       ImportLadingProductBean = (ImportLadingProduct)session.getAttribute(beanName);
       if(ImportLadingProductBean == null)
       {
         LoginBean loginBean = LoginBean.getInstance(request);
         ImportLadingProductBean = new ImportLadingProduct();
         ImportLadingProductBean.fgsid = loginBean.getFirstDeptID();
         session.setAttribute(beanName, ImportLadingProductBean);
       }
     }
     return ImportLadingProductBean;
   }
   /**
    * 构造函数
    */
   private ImportLadingProduct()
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
   }
   /**
    *网页调用
    * */
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
    * 得到固定查询的用户输入的值
    * @param col 查询项名称
    * @return 用户输入的值
    */
   public final String getFixedQueryValue(String col)
   {
     return fixedQuery.getSearchRow().get(col);
   }
/**
*session失效时调用
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
   public final RowMap getLookupRow(String tdhwid)
   {
     RowMap row = new RowMap();
     if(tdhwid == null || tdhwid.equals(""))
       return row;//返回
     EngineRow locateRow = new EngineRow(dsSaleOrderProduct, "tdhwid");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
     if(locateRow == null)
       locateRow = new EngineRow(getOneTable(), "tdhwid");
     locateRow.setValue(0, tdhwid);
     if(getOneTable().locate(locateRow, Locate.FIRST))
       row.put(getOneTable());
     return row;
   }
   public final EngineDataSet getOneTable()
   {
     return dsSaleOrderProduct;
   }
   class Init implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       HttpServletRequest request = data.getRequest();
       retuUrl = request.getParameter("src");
       retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
       String dwxid = request.getParameter("dwtxid");
       personid = request.getParameter("personid");
       String khlx = request.getParameter("khlx");
       String SQL = "";
       if(!dwxid.equals(""))
       {
         dwtxid = dwxid;
         SQL=" AND dwtxid='"+dwtxid+"' AND personid='"+personid+"' AND khlx='"+khlx+"'";
       }
         SQL= combineSQL(IMPORT_SALE_SQL, "?", new String[]{fgsid,SQL});
       dsSaleOrderProduct.setQueryString(SQL);
       dsSaleOrderProduct.setRowMax(null);
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
       SQL = combineSQL(MASTER_SQL, "?", new String[]{fgsid, SQL});
       if(!dsSaleOrderProduct.getQueryString().equals(SQL))
       {
         dsSaleOrderProduct.setQueryString(SQL);
         dsSaleOrderProduct.setRowMax(null);
       }
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
     new QueryColumn(master.getColumn("tdbh"), null, null, null, null, "="),//提单编号
     new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//购货单位
     new QueryColumn(master.getColumn("tdrq"), null, null, null, "a", ">="),//提单日期
     new QueryColumn(master.getColumn("tdrq"), null, null, null, "b", "<="),//提单日期
     new QueryColumn(master.getColumn("deptid"), null, null, null, null, "=")//部门id
   });
   isInitQuery = true;
   }
}
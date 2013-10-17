package engine.erp.sale.xixing;

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
 * <p>Title: 销售子系统_</p>
 * <p>Description: 销售子系统_</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class Select_Promotion_Product extends BaseAction implements Operate
 {
   private static final String IMPORT_SALE_SQL = "select t.dwtxid,t.cpid,t.startdate,t.enddate,t.memo,t.prom_price,t.zk,t.djlx,b.wzdjid,c.storeid from xs_promotion t ,xs_wzdj b,kc_dm c WHERE t.cpid=b.cpid(+) AND t.cpid=c.cpid AND t.dwtxid='?' AND t.startdate<=to_date('?','yyyy-mm-dd') AND t.enddate>=to_date('?','yyyy-mm-dd') and (c.storeid is null or c.storeid='?' ) ";//分公司; 往来单位
   //private static final String MASTER_SQL    = "SELECT * FROM VW_SALE_INVOICE_IMPORT_TDDETAL WHERE  fgsid=? ?  order by tdbh,dwtxid ";
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
   class Init implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       HttpServletRequest request = data.getRequest();
       retuUrl = request.getParameter("src");
       retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
       String dwxid = request.getParameter("dwtxid");
       String storeid = request.getParameter("storeid");
       String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
       String SQL = "";
       dwtxid = dwxid;
       SQL=combineSQL(IMPORT_SALE_SQL,"?",new String[]{dwtxid,today,today,storeid});
       dsSaleOrderProduct.setQueryString(SQL);
       dsSaleOrderProduct.setRowMax(null);
     }
   }
}
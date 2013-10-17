package engine.erp.jit;

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
 * <p>Title: 销售子系统_销售发票,销售结算.引入提单货物</p>
 * <p>Description: 销售子系统_销售发票引入提单货物</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class Select_Gzz_Person extends BaseAction implements Operate
 {
   private static final String IMPORT_PERSON_SQL = "SELECT a.personid,a.ordernum,a.ryjs,c.deptid FROM sc_gzzry a,emp b,sc_gzz c where a.personid=b.personid and a.gzzid=c.gzzid ? order by a.ordernum ";//分公司; 往来单位

   private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();
   private EngineRow locateResult = null;
   public  String retuUrl = null;
   private String fgsid = null;   //分公司ID
   private boolean isInitQuery = false; //是否已经初始化查询条件
   private QueryBasic fixedQuery = new QueryFixedItem();
   public String gzzid = "";
   /**
    * 析构函数
    * */
   public static Select_Gzz_Person getInstance(HttpServletRequest request)
   {
     Select_Gzz_Person Select_Gzz_PersonBean = null;
     HttpSession session = request.getSession(true);
     synchronized (session)
     {
       String beanName = "Select_Gzz_PersonBean";
       Select_Gzz_PersonBean = (Select_Gzz_Person)session.getAttribute(beanName);
       if(Select_Gzz_PersonBean == null)
       {
         LoginBean loginBean = LoginBean.getInstance(request);
         Select_Gzz_PersonBean = new Select_Gzz_Person();
         Select_Gzz_PersonBean.fgsid = loginBean.getFirstDeptID();
         session.setAttribute(beanName, Select_Gzz_PersonBean);
       }
     }
     return Select_Gzz_PersonBean;
   }
   /**
    * 构造函数
    */
   private Select_Gzz_Person()
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
   public final RowMap getLookupRow(String personid)
   {
     RowMap row = new RowMap();
     if(personid == null || personid.equals(""))
       return row;//返回
     EngineRow locateRow = new EngineRow(dsSaleOrderProduct, "personid");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
     if(locateRow == null)
       locateRow = new EngineRow(getOneTable(), "tdhwid");
     locateRow.setValue(0, personid);
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
       gzzid = request.getParameter("gzzid");
       String SQL = "";
       if(gzzid!=null)
         SQL=" AND a.gzzid='"+gzzid+"'";
       SQL= combineSQL(IMPORT_PERSON_SQL, "?", new String[]{SQL});
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
       if(gzzid!=null)
         SQL=SQL+" AND a.gzzid='"+gzzid+"'";
       SQL = combineSQL(IMPORT_PERSON_SQL, "?", new String[]{SQL});
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
        new QueryColumn(master.getColumn("xm"), null, null, null, null, "like")
      });
      isInitQuery = true;
   }
 }
}
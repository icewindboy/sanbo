    package engine.erp.buy;

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
     * <p>Title: 采购管理_采购发票-引入采购单</p>
     * <p>Description:  采购管理_采购发票-引入采购单</p>
     * <p>Copyright: Copyright (c) 2003</p>
     * <p>Company: </p>
     * @author engine
     * @version 1.0
     */
    public final class Select_Bill_Of_Stocking extends BaseAction implements Operate
    {
      private static final String IMPORT_SALE_SQL = "SELECT * FROM VW_BUY_INVOICE_SEL_JHD WHERE fgsid=?  ";//分公司; 往来单位
      private static final String MASTER_SQL    = "SELECT * FROM VW_BUY_INVOICE_SEL_JHD WHERE  fgsid=? ? ORDER BY jhdbm DESC ";
      private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();
      private EngineRow locateResult = null;
      public  String retuUrl = null;
      private String fgsid = null;   //分公司ID
      private boolean isInitQuery = false; //是否已经初始化查询条件
      private QueryBasic fixedQuery = new QueryFixedItem();
      /**
       *析构函数
       * */
      public static Select_Bill_Of_Stocking getInstance(HttpServletRequest request)
      {
        Select_Bill_Of_Stocking Select_Bill_Of_StockingBean = null;
        HttpSession session = request.getSession(true);
        synchronized (session)
        {
          String beanName = "Select_Bill_Of_StockingBean";
          Select_Bill_Of_StockingBean = (Select_Bill_Of_Stocking)session.getAttribute(beanName);
          if(Select_Bill_Of_StockingBean == null)
          {
            LoginBean loginBean = LoginBean.getInstance(request);
            Select_Bill_Of_StockingBean = new Select_Bill_Of_Stocking();
            Select_Bill_Of_StockingBean.fgsid = loginBean.getFirstDeptID();
            session.setAttribute(beanName, Select_Bill_Of_StockingBean);
          }
        }
        return Select_Bill_Of_StockingBean;
      }
      /**
       * 构造函数
       */
      private Select_Bill_Of_Stocking()
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
      public final RowMap getLookupRow(String jhdID)
      {
        RowMap row = new RowMap();
        if(jhdID == null || jhdID.equals(""))
          return row;//返回
        EngineRow locateRow = new EngineRow(dsSaleOrderProduct, "jhdID");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
        if(locateRow == null)
          locateRow = new EngineRow(getOneTable(), "jhdID");
        locateRow.setValue(0, jhdID);
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
          String dwtxid = request.getParameter("dwtxid");
          String SQL = combineSQL(IMPORT_SALE_SQL, "?", new String[]{fgsid});
          if(!dwtxid.equals(""))
            SQL=SQL+" AND dwtxid="+dwtxid;
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
           new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//供货单位
           new QueryColumn(master.getColumn("jhdbm"), null, null, null, null, "="),
           new QueryColumn(master.getColumn("jhrq"), null, null, null, "a", ">="),//交货日期
           new QueryColumn(master.getColumn("jhrq"), null, null, null, "b", "<="),//交货日期
           new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门id
           new QueryColumn(master.getColumn("djlx"), null, null, null, null, "=")
         });
         isInitQuery = true;
      }
    }
}

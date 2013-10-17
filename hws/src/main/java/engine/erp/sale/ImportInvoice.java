    package engine.erp.sale;

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
     * <p>Title: 销售子系统_提货单管理引入合同</p>
     * <p>Description: 销售子系统_提货单管理引入合同</p>
     * <p>Copyright: Copyright (c) 2003</p>
     * <p>Company: </p>
     * @author engine
     * @version 1.0
     */
    public final class ImportInvoice extends BaseAction implements Operate
    {
      private static final String IMPORT_SALE_SQL = "SELECT * FROM xs_td WHERE fgsid=? and dwtxid=? ";//分公司; 往来单位
      private static final String MASTER_SQL    = "SELECT * FROM xs_td WHERE isrefer=1 AND zt<>4 AND zt<>8 and fgsid=? ? ORDER BY tdbh DESC ";
      private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();
      private EngineRow locateResult = null;
      public  String retuUrl = null;
      private String fgsid = null;   //分公司ID
      private boolean isInitQuery = false; //是否已经初始化查询条件
      private QueryBasic fixedQuery = new QueryFixedItem();
      /**
       *析构函数
       * */
      public static ImportInvoice getInstance(HttpServletRequest request)
      {
        ImportInvoice ImportInvoiceBean = null;
        HttpSession session = request.getSession(true);
        synchronized (session)
        {
          String beanName = "ImportInvoiceBean";
          ImportInvoiceBean = (ImportInvoice)session.getAttribute(beanName);
          if(ImportInvoiceBean == null)
          {
            LoginBean loginBean = LoginBean.getInstance(request);
            ImportInvoiceBean = new ImportInvoice();
            ImportInvoiceBean.fgsid = loginBean.getFirstDeptID();
            session.setAttribute(beanName, ImportInvoiceBean);
          }
        }
        return ImportInvoiceBean;
      }
      /**
       * 构造函数
       */
      private ImportInvoice()
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
          new QueryColumn(master.getColumn("ckrq"), null, null, null, "a", ">="),//出库日期
          new QueryColumn(master.getColumn("ckrq"), null, null, null, "b", "<="),//出库日期
          new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门id
          new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
        });
        isInitQuery = true;
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
      public final RowMap getLookupRow(String tdId)
      {
        RowMap row = new RowMap();
        if(tdId == null || tdId.equals(""))
          return row;//返回
        EngineRow locateRow = new EngineRow(dsSaleOrderProduct, "tdId");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
        if(locateRow == null)
          locateRow = new EngineRow(getOneTable(), "tdId");
        locateRow.setValue(0, tdId);
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
          String SQL = combineSQL(IMPORT_SALE_SQL, "?", new String[]{fgsid,dwtxid});
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
}
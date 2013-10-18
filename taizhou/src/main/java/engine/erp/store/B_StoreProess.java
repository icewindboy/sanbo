    package engine.erp.store;

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
    import java.util.Hashtable;
    import java.util.Date;
    import java.text.SimpleDateFormat;
    import java.math.BigDecimal;
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    import javax.servlet.http.HttpSessionBindingEvent;
    import javax.servlet.http.HttpSession;

    import com.borland.dx.dataset.*;
    /**
     * <p>Title: 库存--自制收货单</p>
     * <p>Description: 库存--自制收货单</p>
     * <p>Copyright: Copyright (c) 2003</p>
     * <p>Company: </p>
     * @author 胡康宁
     * @version 1.0
     */

    public final class B_StoreProess extends BaseAction implements Operate
    {
      public  static final String SHOW_DETAIL           = "20001";
      public  static final String DETAIL_SELECT_PROCESS = "20002";
      public  static final String ONCHANGE              = "20003";
      public  static final String GXMC_ONCHANGE         = "20004";
      public  static final String DEPTCHANGE            = "20005";
      public  static final String SINGLE_SEL_PROCESS    = "20006";     //单选生产加工单主表操作
      public  static final String SINGLE_SELECT_PRODUCT = "20007";     //单选产品操作
      public  static final String PRODUCT_ONCHANGE      = "20008";     //输入产品触发事件
      public  static final String COMPLETE              = "20009";     //强制完成触发事件
      public  static final String REPORT                = "20010";     //报表追踪操作
      public  static final String STORE_ONCHANGE        = "20011";     //选择仓库提交
      private static final String MASTER_STRUT_SQL     = "SELECT * FROM sc_receiveprod WHERE 1<>1";
      private static final String MASTER_SQL           = "SELECT * FROM sc_receiveprod WHERE 1=1 AND ? AND filialeID=? ? ORDER BY receiveid DESC";
      private static final String MASTER_REPORT_SQL    = "SELECT * FROM sc_receiveprod WHERE gzlid=";
      private static final String DETAIL_STRUT_SQL     = "SELECT * FROM sc_receiveproddetail WHERE 1<>1";
      private static final String DETAIL_SQL           = "SELECT * FROM sc_receiveproddetail WHERE receiveid='?' ";
      private static final String DETAIL_ADD_SQL       = "SELECT * FROM vw_sc_rwd_detail WHERE rwdid='?' ";
      private static final String PROCESS_DETAIL_SQL   = "SELECT a.*, b.hsbl FROM sc_jgdmx a, kc_dm b WHERE a.cpid=b.cpid AND nvl(a.sl,0)>nvl(a.ypgzl,0) AND a.jgdid= ";
      private static final String MASTER_APPROVE_SQL = "SELECT * FROM sc_receiveprod WHERE receiveid='?'";
      private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
      private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
      private LookUp technicsBean = null; //工艺路线信息的bean的引用, 用于提取工艺路线信息
      public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_receiveprod");
      public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_receiveproddetail");
      private boolean isMasterAdd = true;      //是否在添加状态
      public boolean isReport = false;         // 从表是否在报表引用状态
      private long    masterRow = -1;          //保存主表修改操作的行记录指针
      private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
      private ArrayList d_RowInfos = null;         //从表多行记录的引用
      private boolean isInitQuery = false;         //是否已经初始化查询条件
      private QueryBasic fixedQuery = new QueryFixedItem();
      public  String retuUrl = null;
      public  String loginId = "";   //登录员工的ID
      public  String loginCode = ""; //登陆员工的编码
      public  String loginName = ""; //登录员工的姓名
      public  String loginDept = ""; //登录员工的部门
      public  boolean isApprove = false;     //是否在审批状态
      private String qtyFormat = null, priceFormat = null, sumFormat = null;
      private String filialeID = null;   //分公司ID
      private String receiveid = null;
      private String rwdid = null;
      public String SC_STORE_UNIT_STYLE = null;  //计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
      public String SC_PRODUCE_UNIT_STYLE =null; //1=强制换算,0=仅空值时换算
      public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
      private User user = null;
      public  static final String TURNPAGE = "9996";
      public int min = 0;
      public int max = 0;
      public String isRepeat = "0";              //重定向，如果本业检测数据不正确的话isrepeat为1。将不翻页
      /**
       * 自制收货单
       * @param request jsp请求
       * @param isApproveStat 是否在审批状态
       * @return 返回自制收货单的实例
       */
      public static B_StoreProess getInstance(HttpServletRequest request)
      {
        B_StoreProess b_StoreProessBean = null;
        HttpSession session = request.getSession(true);
        synchronized (session)
        {
          String beanName = "b_StoreProessBean";
          b_StoreProessBean = (B_StoreProess)session.getAttribute(beanName);
          if(b_StoreProessBean == null)
          {
            //引用LoginBean
            LoginBean loginBean = LoginBean.getInstance(request);

            b_StoreProessBean = new B_StoreProess();
            b_StoreProessBean.qtyFormat = loginBean.getQtyFormat();
            b_StoreProessBean.sumFormat = loginBean.getSumFormat();
            b_StoreProessBean.priceFormat = loginBean.getPriceFormat();

            b_StoreProessBean.filialeID = loginBean.getFirstDeptID();
            b_StoreProessBean.loginDept = loginBean.getDeptID();
            b_StoreProessBean.loginId = loginBean.getUserID();
            b_StoreProessBean.loginName = loginBean.getUserName();
            b_StoreProessBean.user = loginBean.getUser();
            b_StoreProessBean.SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和换算单位是否强制换算
            b_StoreProessBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//计量单位和生产单位是否强制换算
            b_StoreProessBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
            b_StoreProessBean.dsDetailTable.setColumnFormat("sl", b_StoreProessBean.qtyFormat);
            b_StoreProessBean.dsDetailTable.setColumnFormat("scsl", b_StoreProessBean.qtyFormat);
            b_StoreProessBean.dsDetailTable.setColumnFormat("hssl", b_StoreProessBean.qtyFormat);
            b_StoreProessBean.dsDetailTable.setColumnFormat("de", b_StoreProessBean.priceFormat);
            b_StoreProessBean.dsDetailTable.setColumnFormat("desl", b_StoreProessBean.qtyFormat);
            b_StoreProessBean.dsDetailTable.setColumnFormat("jjgz", b_StoreProessBean.priceFormat);
            b_StoreProessBean.dsMasterTable.setColumnFormat("je", b_StoreProessBean.priceFormat);
            b_StoreProessBean.dsDetailTable.setColumnFormat("jjgs", b_StoreProessBean.priceFormat);
            session.setAttribute(beanName, b_StoreProessBean);
          }
        }
        return b_StoreProessBean;
      }

      /**
       * 构造函数
       */
      private B_StoreProess()
      {
        try {
          jbInit();
        }
        catch (Exception ex) {
          log.error("jbInit", ex);
        }
      }

      /**
       * 初始化函数
       * @throws Exception 异常信息
       */
      private final void jbInit() throws java.lang.Exception
      {
        setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
        setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"receiveid"}, new String[]{"S_SC_RECEIVEPROD"}));
        dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"receiveDetailID"}, new String[]{"S_SC_RECEIVEPRODDETAIL"}));
        Master_Add_Edit masterAddEdit = new Master_Add_Edit();
        Master_Post masterPost = new Master_Post();
        addObactioner(String.valueOf(INIT), new Init());
        addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
        addObactioner(SHOW_DETAIL, new ShowDetail());
        addObactioner(String.valueOf(ADD), masterAddEdit);
        addObactioner(String.valueOf(EDIT), masterAddEdit);
        addObactioner(String.valueOf(DEL), new Master_Delete());
        addObactioner(String.valueOf(POST), masterPost);
        addObactioner(String.valueOf(POST_CONTINUE), masterPost);
        addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
        addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
        addObactioner(String.valueOf(STORE_ONCHANGE), new Onchange());
        //addObactioner(String.valueOf(DEPTCHANGE), new Deptchange());
        //addObactioner(String.valueOf(DETAIL_SELECT_PROCESS), new Detail_Select_Process());
        //addObactioner(String.valueOf(SINGLE_SEL_PROCESS), new Single_Select_Process());//单选加工单主表
        addObactioner(String.valueOf(PRODUCT_ONCHANGE), new Product_Onchange());//输入产品编码触发事件
        addObactioner(String.valueOf(SINGLE_SELECT_PRODUCT), new Single_Product_Add());//单选产品
        addObactioner(String.valueOf(COMPLETE), new Complete());//强制完成事件
        addObactioner(String.valueOf(REPORT), new Report());//报表引用事件
        addObactioner(TURNPAGE, new Turn_Page());//翻页事件
        //addObactioner(TURNPAGE, new Turn_Page());

      }

      //----Implementation of the BaseAction abstract class
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
            if(data == null)
              return showMessage("无效操作", false);
            if(data.hasMessage())
              return data.getMessage();
          }
          return "";
        }
        catch(Exception ex){
          if(dsMasterTable.isOpen() && dsMasterTable.changesPending())
            dsMasterTable.reset();
          log.error("doService", ex);
          return showMessage(ex.getMessage(), true);
        }
      }

      /**
       * Session失效时，调用的函数
       */
      public final void valueUnbound(HttpSessionBindingEvent event)
      {
        if(dsMasterTable != null){
          dsMasterTable.close();
          dsMasterTable = null;
        }
        if(dsDetailTable != null){
          dsDetailTable.close();
          dsDetailTable = null;
        }
        log = null;
        m_RowInfo = null;
        d_RowInfos = null;
        if(masterProducer != null)
        {
          masterProducer.release();
          masterProducer = null;
        }
        if(detailProducer != null)
        {
          detailProducer.release();
          detailProducer = null;
        }
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
       * 初始化列信息
       * @param isAdd 是否时添加
       * @param isInit 是否从新初始化
       * @throws java.lang.Exception 异常
       */
      private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception
      {
        //是否是主表
        if(isMaster){
          if(isInit && m_RowInfo.size() > 0)
            m_RowInfo.clear();

          if(!isAdd)
            m_RowInfo.put(getMaterTable());
          else
          {
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            m_RowInfo.put("zdrq", today);
            m_RowInfo.put("zdr", loginName);
            m_RowInfo.put("zdrid", loginId);
            m_RowInfo.put("deptid", loginDept);
            m_RowInfo.put("rq", today);
            m_RowInfo.put("state","0");
          }
        }
        else
        {
          EngineDataSet dsDetail = dsDetailTable;
          if(d_RowInfos == null)
            d_RowInfos = new ArrayList(dsDetail.getRowCount());
          else if(isInit)
            d_RowInfos.clear();

          dsDetail.first();
          for(int i=0; i<dsDetail.getRowCount(); i++)
          {
            RowMap row = new RowMap(dsDetail);
            d_RowInfos.add(row);
            dsDetail.next();
          }
        }
      }
      /**
       * 改变车间触发的事件

    class Deptchange implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest req = data.getRequest();
        putDetailInfo(data.getRequest());
        EngineDataSet detail = getDetailTable();
        detail.first();
        while(detail.inBounds())
        {
          String jgdmxid = detail.getValue("jgdmxid");
          if(!jgdmxid.equals(""))
          {
            d_RowInfos.remove(detail.getRow());
            detail.deleteRow();
          }
          else
            detail.next();
        }
      }
    }
    */
    /**
     * 提交仓库
     */
    class Store_Onchange implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest request = data.getRequest();
        putDetailInfo(request);
      }
    }
    /**
     *选择工艺路线类型触发的事件
     */
    class Onchange implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest req = data.getRequest();
        putDetailInfo(data.getRequest());
        RowMap rowinfo = getMasterRowinfo();
        int rownum = Integer.parseInt(data.getParameter("rownum"));
        RowMap detailRow = (RowMap)d_RowInfos.get(rownum);
        rowinfo.put("je","");
        detailRow.put("gx","");
        detailRow.put("desl","");
        detailRow.put("jjgs","");
        detailRow.put("jjgz","");
      }
    }
    /**
     * 输入产品编码触发的事件
     */
    class Product_Onchange implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest req = data.getRequest();
        putDetailInfo(data.getRequest());
      }
    }
    /**
     *选择工序类型触发的事件

     class Gxmc_Onchange implements Obactioner
     {
       public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
       {
         HttpServletRequest req = data.getRequest();
         putDetailInfo(data.getRequest());
         String jjgsgs = B_WageFormula.getInstance(req).getWorkTime();
         double jjgsgsz = jjgsgs.length()>0 ? Double.parseDouble(jjgsgs) : 0 ;
         int rownum = Integer.parseInt(data.getParameter("rownum"));
         String gxmcid = req.getParameter("v_gx_"+rownum);
         //technicsBean.regData(dsDetailTable,"gylxid");
         RowMap technicsRow = getTechnicsBean(req).getLookupRow(gxmcid);//根据工序id得到工艺路线明细的一行信息
         RowMap detailRow = (RowMap)d_RowInfos.get(rownum);
         double de = technicsRow.get("deje").length()>0 ? Double.parseDouble(technicsRow.get("deje")) : 0;//得到定额
         double desl = technicsRow.get("desl").length()>0 ? Double.parseDouble(technicsRow.get("desl")) : 0;//得到定额数量
         double sl = detailRow.get("sl").length()>0 ? Double.parseDouble(detailRow.get("sl")) : 0;
         detailRow.put("de", technicsRow.get("deje"));
         detailRow.put("desl",technicsRow.get("desl"));
         if(sl!=0 && desl !=0)
           detailRow.put("jjgs", formatNumber(String.valueOf(sl*jjgsgsz/desl),sumFormat));
         if(sl!=0 && de !=0)
           detailRow.put("jjgz", formatNumber(String.valueOf(sl*de),sumFormat) );
         double total = 0;
         double tot = 0;
         for(int k=0; k<d_RowInfos.size(); k++)
         {
           RowMap detail = (RowMap)d_RowInfos.get(k);
           String jjgs = detail.get("jjgs");
           String jjgz = detail.get("jjgz");
           total += jjgs.length()>0 ? Double.parseDouble(jjgs) : 0;
           tot += jjgz.length()>0 ? Double.parseDouble(jjgz) : 0;
         }
         String gz = B_WageFormula.getInstance(req).getWage();
         EngineDataSet ds = getMaterTable();
         RowMap rowinfo = getMasterRowinfo();
         if(gz.equals("1")){
           rowinfo.put("je",formatNumber(String.valueOf(tot),sumFormat));
           //rowinfo.put("zjjgz",formatNumber(String.valueOf(tot),sumFormat));
         }
         else{
           rowinfo.put("je",formatNumber(String.valueOf(total),sumFormat));
           //rowinfo.put("zjjgz",formatNumber(String.valueOf(total),sumFormat));
         }
       }
     }
    */

      /**
       * 从表保存操作
       * @param request 网页的请求对象
       * @param response 网页的响应对象
       * @return 返回HTML或javascipt的语句
       * @throws Exception 异常
       */
      private final void putDetailInfo(HttpServletRequest request)
      {
        RowMap rowInfo = getMasterRowinfo();
        //保存网页的所有信息
        rowInfo.put(request);

        int rownum = d_RowInfos.size();
        RowMap detailRow = null;
        for(int i=0; i<rownum; i++)
        {
          detailRow = (RowMap)d_RowInfos.get(i);
          //detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品
          //detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//计量单位数量
          //detailRow.put("scsl", formatNumber(rowInfo.get("scsl_"+i), qtyFormat));//生产单位数量
          //detailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+i), qtyFormat));
          //detailRow.put("de", formatNumber(rowInfo.get("de_"+i), priceFormat));//
          //detailRow.put("desl", formatNumber(rowInfo.get("desl_"+i), priceFormat));//
          //detailRow.put("gx", rowInfo.get("gx_"+i));//工序
          //detailRow.put("gylxid", rowInfo.get("gylxid_"+i));//工艺路线
          detailRow.put("jjgz", rowInfo.get("jjgz_"+i));//计件工时
          detailRow.put("cp", rowInfo.get("cp_"+i));//计件工资
          //detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性
          //保存用户自定义的字段
          FieldInfo[] fields = detailProducer.getBakFieldCodes();
          for(int j=0; j<fields.length; j++)
          {
            String fieldCode = fields[j].getFieldcode();
            detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
          }
        }
      }

      /*得到表对象*/
      public final EngineDataSet getMaterTable()
      {
        return dsMasterTable;
      }

      /*得到从表表对象*/
      public final EngineDataSet getDetailTable(){
        if(!dsDetailTable.isOpen())
          dsDetailTable.open();
        return dsDetailTable;
      }

      /*打开从表*/
      public final void openDetailTable(boolean isMasterAdd)
      {
        String SQL = isMasterAdd ? "-1" : receiveid;
        SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});
        dsDetailTable.setQueryString(SQL);
        if(!dsDetailTable.isOpen())
          dsDetailTable.open();
        else
          dsDetailTable.refresh();
      }

      /*得到主表一行的信息*/
      public final RowMap getMasterRowinfo() { return m_RowInfo; }

      /*得到从表多列的信息*/
      public final RowMap[] getDetailRowinfos() {
        RowMap[] rows = new RowMap[d_RowInfos.size()];
        d_RowInfos.toArray(rows);
        return rows;
      }

      /**
       * 主表是否在添加状态
       * @return 是否在添加状态
       */
      public final boolean masterIsAdd() {return isMasterAdd; }

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
       * 得到选中的行的行数
       * @return 若返回-1，表示没有选中的行
       */
      public final int getSelectedRow()
      {
        if(masterRow < 0)
          return -1;

        dsMasterTable.goToInternalRow(masterRow);
        return dsMasterTable.getRow();
      }
      /**
       * 初始化操作的触发类
       */
      class Init implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          isReport = false;
          isApprove = false;
          retuUrl = data.getParameter("src");
          retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
          //
          HttpServletRequest request = data.getRequest();
          masterProducer.init(request, loginId);
          detailProducer.init(request, loginId);
          //初始化查询项目和内容
          RowMap row = fixedQuery.getSearchRow();
          row.clear();
          String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
          String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
          row.put("state", "0");
          row.put("rq$a", startDay);
          row.put("rq$b", today);
          isMasterAdd= true;
          //isDetailAdd = false;
          //
          //初始化时不显示已完成的单据
          String SQL = " AND state<>8";
          SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),filialeID, SQL});
          dsMasterTable.setQueryString(SQL);
          dsMasterTable.setRowMax(null);
          if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
            dsDetailTable.empty();
          //B_WageFormula.getInstance(request).readyExpressions();
        }
      }

      /**
       * 显示从表的列表信息
       */
      class ShowDetail implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
          masterRow = dsMasterTable.getInternalRow();
          receiveid = dsMasterTable.getValue("receiveid");
          //打开从表
          openDetailTable(false);
        }
      }
      /**
       * 审批操作的触发类
       */
      class Approve implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          HttpServletRequest request = data.getRequest();
          masterProducer.init(request, loginId);
          detailProducer.init(request, loginId);
          /**
           *报表调从表页面,传递operate='2000'操作
           */
           isReport = String.valueOf(REPORT).equals(action);
           String id=null;
           if(isReport){
             isReport = true;
             isApprove = false;
             id = data.getParameter("id");//得到报表传递的参数既收发单据主表ID
           }
           else{
             isReport = false;
             isApprove = true;//审批操作
             id = data.getParameter("id", "");
          }
          String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
          dsMasterTable.setQueryString(sql);
          if(dsMasterTable.isOpen()){
            dsMasterTable.readyRefresh();
            dsMasterTable.refresh();
          }
          else
            dsMasterTable.open();
          //打开从表
          openDetailTable(false);
          initRowInfo(true, false, true);
          initRowInfo(false, false, true);
        }
      }
      /**
       * 添加到审核列表的操作类
       */
      class Add_Approve implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
          ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
          String content = dsMasterTable.getValue("receiveCode");
          String isout = dsMasterTable.getValue("isout");
          //03.16 15:04 修改 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
          //以实现:mantis上库存管理中0000158bug描述的:根据下达的部门，进行提交审批；
          //if(!isout.equals("1"))
            approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "self_gain_list", content, dsMasterTable.getValue("deptid"));
         // else
         //   approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "process_instore_list", content, dsMasterTable.getValue("deptid"));
        }
      }
      /**
       * 取消审批触发操作
       */
      class Cancel_Approve implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
          String isout = dsMasterTable.getValue("isout");
          ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
          if(!isout.equals("1"))
            approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "self_gain_list");
          else
            approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "process_instore_list");
        }
      }

      /**
       * 主表添加或修改操作的触发类
       */
      class Master_Add_Edit implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          isMasterAdd = String.valueOf(ADD).equals(action);
          isApprove = false;
          isReport = false;
          if(!isMasterAdd)
          {
            dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
            masterRow = dsMasterTable.getInternalRow();
            receiveid = dsMasterTable.getValue("receiveid");
          }
          else
            receiveid = dataSetProvider.getSequence("s_sc_grgzl");
          synchronized(dsDetailTable){
            openDetailTable(isMasterAdd);
          }
          initRowInfo(true, isMasterAdd, true);
          initRowInfo(false, isMasterAdd, true);
          data.setMessage(showJavaScript("toDetail();"));
        }
      }
      /**
       * 报表调用工人工作量操作的触发类
       */
      class Report implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          isReport = true;
          HttpServletRequest request = data.getRequest();
          masterProducer.init(request, loginId);
          detailProducer.init(request, loginId);
          String id = request.getParameter("receiveid");
          String SQL = MASTER_REPORT_SQL+id;
          dsMasterTable.setQueryString(SQL);
          if(dsMasterTable.isOpen()){
            dsMasterTable.readyRefresh();
            dsMasterTable.refresh();
          }
          else
            dsMasterTable.open();

          receiveid = dsMasterTable.getValue("receiveid");
         // B_WageFormula.getInstance(request).readyExpressions();
          //打开从表
          openDetailTable(false);

          initRowInfo(true, false, true);
          initRowInfo(false, false, true);
        }
      }

      /**
       * 主表保存操作的触发类
       */
      class Master_Post implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          putDetailInfo(data.getRequest());
          EngineDataSet ds = getMaterTable();
          RowMap rowInfo = getMasterRowinfo();
          //String gz = B_WageFormula.getInstance(data.getRequest()).getWage();//工人工资公式设置，gz为1以计件工时计算工资，为0时以计件工资计算
          //校验表单数据
          String temp = checkMasterInfo();
          if(temp != null)
          {
            data.setMessage(temp);
            return;
          }
          temp = checkDetailInfo();
          if(temp != null)
          {
            data.setMessage(temp);
            return;
          }
          if(rwdid==null||rwdid.equals(""))
            return;
          if(!isMasterAdd)
            ds.goToInternalRow(masterRow);

          //得到主表主键值
          String receiveid = null;
          if(isMasterAdd){
            ds.insertRow(false);
            //receiveid = dataSetProvider.getSequence("s_sc_grgzl");
            ds.setValue("receiveid", receiveid);
            ds.setValue("filialeID", filialeID);
            ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
            ds.setValue("zdrid", loginId);
            ds.setValue("zdr", loginName);//操作员
            ds.setValue("state","0");
            //ds.setValue("lx", "1");
          }
          //保存从表的数据
          //String jjgsgs = B_WageFormula.getInstance(data.getRequest()).getWorkTime();
          //double jjgsVal = isDouble(jjgsgs) ? Double.parseDouble(jjgsgs) : 0;
          RowMap detailrow = null;
          EngineDataSet detail = getDetailTable();
          BigDecimal totalNum = new BigDecimal(0),totalSum = new BigDecimal(0);
          detail.first();
          for(int i=0; i<detail.getRowCount(); i++)
          {
            detailrow = (RowMap)d_RowInfos.get(i);
            //新添的记录
            if(isMasterAdd)
              detail.setValue("receiveid", receiveid);

            detail.setValue("cp", detailrow.get("cp"));//计量单位数量
            detail.setValue("jjgz", detailrow.get("jjgz"));//生产单位数量

            //保存用户自定义的字段
            FieldInfo[] fields = detailProducer.getBakFieldCodes();
            for(int j=0; j<fields.length; j++)
            {
              String fieldCode = fields[j].getFieldcode();
              detail.setValue(fieldCode, detailrow.get(fieldCode));
            }
            detail.post();
            totalNum = totalNum.add(detail.getBigDecimal("jjgz"));   //
            detail.next();
          }

          //保存主表数据
          ds.setValue("rwdid", rwdid);              //id
          ds.setValue("rq", rowInfo.get("rq"));     //日期
          ds.setValue("zjjgz", totalNum.toString());//总计件工资
          ds.setValue("memo", rowInfo.get("memo"));
          //保存用户自定义的字段
          FieldInfo[] fields = masterProducer.getBakFieldCodes();
          for(int j=0; j<fields.length; j++)
          {
            String fieldCode = fields[j].getFieldcode();
            detail.setValue(fieldCode, rowInfo.get(fieldCode));
          }
          ds.post();
          ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);

          if(String.valueOf(POST_CONTINUE).equals(action)){
            isMasterAdd = true;
            initRowInfo(true, true, true);
            detail.empty();
            initRowInfo(false, true, true);//重新初始化从表的各行信息
          }
          else if(String.valueOf(POST).equals(action))
            data.setMessage(showJavaScript("backList();"));
        }
        /**
         * 校验从表表单信息从表输入的信息的正确性
         * @return null 表示没有信息
         */
        private String checkDetailInfo()
        {
          String temp = null;
          RowMap detailrow = null;
          if(d_RowInfos.size()<1)
            return showJavaScript("alert('不能保存空的数据')");
          return null;
        }

        /**
         * 校验主表表表单信息从表输入的信息的正确性
         * @return null 表示没有信息,校验通过
         */
        private String checkMasterInfo() throws Exception
        {
          RowMap rowInfo = getMasterRowinfo();
          String temp = rowInfo.get("deptid");
          if(temp.equals(""))
            return showJavaScript("alert('请选择车间！');");
          String rq = rowInfo.get("rq");
          if(rq.equals(""))
            return showJavaScript("alert('日期不能为空！');");
          else if(!isDate(rq))
            return showJavaScript("alert('非法日期！');");
          /*
          String personid = rowInfo.get("personid");
          if(personid.equals(""))
            return showJavaScript("alert('请选择员工！');");
          if(isMasterAdd)
          {
            String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_grgzl WHERE personid="+personid+" AND rq=to_date('"+rq+"','YYYY-MM-DD')");
            if(!count.equals("0"))
              return showJavaScript("alert('已存在该员工"+rq+"的工作量')");
          }
          temp = rowInfo.get("cq");
          if(temp.equals(""))
            return showJavaScript("alert('出勤不能为空！');");
          */
          return null;
        }
      }

      /**
       * 主表删除操作
       */
      class Master_Delete implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          if(isMasterAdd){
            data.setMessage(showJavaScript("backList();"));
            return;
          }
          EngineDataSet ds = getMaterTable();
          ds.goToInternalRow(masterRow);
          dsDetailTable.deleteAllRows();
          ds.deleteRow();
          ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
          //
          d_RowInfos.clear();
          data.setMessage(showJavaScript("backList();"));
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
          SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),filialeID, SQL});
          dsMasterTable.setQueryString(SQL);
          dsMasterTable.setRowMax(null);
        }

        /**
         * 初始化查询的各个列
         * @param request web请求对象
         */
        private void initQueryItem(HttpServletRequest request)
        {
          if(isInitQuery)
            return;
          EngineDataSet master = dsMasterTable;
          EngineDataSet detail = dsMasterTable;
          if(!master.isOpen())
            master.open();
          //初始化固定的查询项目
          fixedQuery = new QueryFixedItem();
          fixedQuery.addShowColumn("", new QueryColumn[]{
            new QueryColumn(master.getColumn("rq"), null, null, null, "a", ">="),
            new QueryColumn(master.getColumn("rq"), null, null, null, "b", "<="),
            new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
            new QueryColumn(master.getColumn("state"), null, null, null, null, "="),//状态
            new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),//员工ID
            new QueryColumn(master.getColumn("receiveid"), "sc_grgzlmx", "receiveid", "cpid", null, "="),//从表品名
            new QueryColumn(master.getColumn("receiveid"), "VW_SCGRGZL_QUERY", "receiveid", "cpbm", "cpbm", "like"),//从表产品编码
            new QueryColumn(master.getColumn("receiveid"), "VW_SCGRGZL_QUERY", "receiveid", "product", "product", "like"),//从表品名
          });
          isInitQuery = true;
        }
      }

      /**
       *  根据加工单从表增加操作

      class Detail_Select_Process implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          HttpServletRequest req = data.getRequest();
          //保存输入的明细信息
          putDetailInfo(data.getRequest());
          RowMap rowInfo = getMasterRowinfo();

          String mutiprocess = m_RowInfo.get("mutiprocess");
          if(mutiprocess.length() == 0)
            return;

          //实例化查找数据集的类
          EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
          String[] jgdmxID = parseString(mutiprocess,",");
          if(!isMasterAdd)
            dsMasterTable.goToInternalRow(masterRow);
          String receiveid = dsMasterTable.getValue("receiveid");
          for(int i=0; i < jgdmxID.length; i++)
          {
            if(jgdmxID[i].equals("-1"))
              continue;
            RowMap detailrow = null;
            locateGoodsRow.setValue(0, jgdmxID[i]);
            if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
            {

            }
            data.setMessage(showJavaScript("big_change()"));
          }
        }
      }
      */
      /**
       * 2004-5-2 19:00 明细资料数据集页面翻页功能.
       */
      class Turn_Page implements Obactioner
      {
        /**
         * 按页翻动明细数据集的数据
         */
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          //保存输入的明细信息
          isRepeat="0";
          putDetailInfo(data.getRequest());
          /*
          String temp = checkNumRigtt(min,max);
          if(temp!=null){
          isRepeat="1";//重定向，如果本页检测数据不正确的话isrepeat为1。将不翻页
          data.setMessage(temp);
          return;
          }
          */
        }
        //检验数据正确性方法
        private String checkNumRigtt(int tempmin, int tempmax) throws Exception{
          String temp =null;
          RowMap detailrow = null;
          ArrayList list = new ArrayList();
          for(int i=tempmin; i<=tempmax; i++){
            int row = i+1;
            detailrow = (RowMap)d_RowInfos.get(i);
            String cpid = detailrow.get("cpid");
            if(cpid.equals(""))
              return showJavaScript("alert('第"+row+"行产品不能为空');");
            String batchNo = detailrow.get("batchNo");
            String isbatchno = detailrow.get("isbatchno");
            //如果收货单明细中产品相同批号也相同，并且该产品设置为批号跟踪则不能保存。设置是否批号跟踪在物资信息中设置
            if(!batchNo.equals("") && isbatchno.equals("1")){
              if(list.contains(batchNo))
                return showJavaScript("alert('第"+row+"行批号重复');");
              else
                list.add(batchNo);
            }
            String sl = detailrow.get("drawNum");
            if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
              return temp;
            if(sl.length()>0 && sl.equals("0"))
              return showJavaScript("alert('第"+row+"行数量不能为零！')");
          }
          RowMap temprow = null;
          if(isMasterAdd){
            for(int j=tempmin; j<=tempmax; j++){
              temprow = (RowMap)d_RowInfos.get(j);
              int row = j+1;
              String cpid = detailrow.get("cpid");
              String batchNo = detailrow.get("batchNo");
              String isbatchno = detailrow.get("isbatchno");
              //如果收货单明细中产品相同批号也相同，并且该产品设置为批号跟踪则不能保存。设置是否批号跟踪在物资信息中设置
              if(!batchNo.equals("") && isbatchno.equals("1")){
                String count ="0";
                if(isMasterAdd)
                  count  = dataSetProvider.getSequence("SELECT COUNT(*) FROM vw_kc_storebill t WHERE t.djxz IN(1,3,5,7,9) AND t.ph='"+batchNo+"' AND t.cpid='"+cpid+"'");
                else{
                  String receiveid = dsMasterTable.getValue("receiveID");
                  count  = dataSetProvider.getSequence("SELECT COUNT(*) FROM vw_kc_storebill t WHERE t.djxz IN(1,3,5,7,9) AND t.ph='"+batchNo+"' AND t.cpid='"+cpid+"' AND t.sfdjid<>'"+receiveid+"'");
                }
                //String count  = dataSetProvider.getSequence("SELECT COUNT(*) FROM vw_kc_storebill t WHERE t.djxz IN(1,3,5,7,9) AND t.ph='"+batchNo+"'");
                if(!count.equals("0"))
                  return showJavaScript("alert('第"+row+"行批号在库存中已存在');");
              }
            }
          }
          return null;
        }
      }
      /**
       * 从表增加操作（单选产品）
       */
      class Single_Product_Add implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          HttpServletRequest req = data.getRequest();
          //保存输入的明细信息
          putDetailInfo(data.getRequest());
          int row = Integer.parseInt(data.getParameter("rownum"));
          String singleIdInput = m_RowInfo.get("singleIdInput_"+row);
          if(singleIdInput.equals(""))
            return;

          //实例化查找数据集的类
          String cpid = singleIdInput;
          if(!isMasterAdd)
            dsMasterTable.goToInternalRow(masterRow);
          String receiveid = dsMasterTable.getValue("receiveid");
          dsDetailTable.goToRow(row);
          RowMap detailrow = null;
          detailrow = (RowMap)d_RowInfos.get(row);
          detailrow.put("gzlmxid", "-1");
          detailrow.put("cpid", cpid);
          detailrow.put("receiveid", isMasterAdd ? "-1" : receiveid);
        }
     }
      /**
       *  选择任务单主表，引入从表所有未加工信息

      class Single_Select_Process implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          HttpServletRequest req = data.getRequest();
          //保存输入的明细信息
          putDetailInfo(data.getRequest());
          RowMap rowInfo = getMasterRowinfo();

          String singleImportProcess = m_RowInfo.get("singleImportProcess");
          if(singleImportProcess.equals(""))
            return;
          String SQL = PROCESS_DETAIL_SQL+singleImportProcess;
          EngineDataSet tempProcessData = null;//零时加工单从表信息数据集
          if(tempProcessData==null)
          {
            tempProcessData = new EngineDataSet();
            setDataSetProperty(tempProcessData,null);
          }
          tempProcessData.setQueryString(SQL);
          if(!tempProcessData.isOpen())
            tempProcessData.openDataSet();
          else
            tempProcessData.refresh();

          //RowMap processMasterRow = getProcessMasterBean(req).getLookupRow(singleImportProcess);
          //rowInfo.put("deptid", processMasterRow.get("deptid"));
          //实例化查找数据集的类
          EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
          if(!isMasterAdd)
            dsMasterTable.goToInternalRow(masterRow);
          String receiveid = dsMasterTable.getValue("receiveid");
          for(int i=0; i<tempProcessData.getRowCount(); i++)
          {
            tempProcessData.goToRow(i);
            //double hsbl = tempProcessData.getValue("hsbl").length()>0 ? Double.parseDouble(tempProcessData.getValue("hsbl")) : 0;
            String jgdmxid = tempProcessData.getValue("jgdmxid");
            String cpid = tempProcessData.getValue("cpid");
            locateGoodsRow.setValue(0, jgdmxid);
            if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
            {
              double sl = tempProcessData.getValue("sl").length()>0 ? Double.parseDouble(tempProcessData.getValue("sl")) : 0;//加工单需要的加工数量
              double ypgzl = tempProcessData.getValue("ypgzl").length()>0 ? Double.parseDouble(tempProcessData.getValue("ypgzl")) : 0;//加工单中已加工量
              double wpgzl = sl-ypgzl>0 ? sl-ypgzl : 0;
              if(wpgzl==0)
                continue;
              dsDetailTable.insertRow(false);
              dsDetailTable.setValue("gzlmxid", "-1");
              dsDetailTable.setValue("jgdmxid",jgdmxid);
              dsDetailTable.setValue("cpid", cpid);
              dsDetailTable.setValue("sl", String.valueOf(wpgzl));
              //dsDetailTable.setValue("hssl", formatNumber(String.valueOf(hsbl==0 ? 0 : wpgzl/hsbl), qtyFormat));
              dsDetailTable.setValue("gylxid", tempProcessData.getValue("gylxid"));
              dsDetailTable.setValue("receiveid", isMasterAdd ? "-1" : receiveid);
              dsDetailTable.setValue("dmsxid", tempProcessData.getValue("dmsxid"));
              dsDetailTable.post();
              //创建一个与用户相对应的行
              RowMap detailrow = new RowMap(dsDetailTable);
              d_RowInfos.add(detailrow);
            }
            data.setMessage(showJavaScript("big_change()"));
          }
        }
      }
      */
      /**
       *  从表增加操作
       * 引入任务单
       */
      class Detail_Add implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          HttpServletRequest req = data.getRequest();
          rwdid = req.getParameter("selectedrwdid");
          putDetailInfo(data.getRequest());
          EngineDataSet detail = getDetailTable();
          EngineDataSet ds = getMaterTable();
          EngineDataSet dsRwdDetail = new EngineDataSet();
          if(rwdid==null||rwdid.equals(""))
            return;
          setDataSetProperty(dsRwdDetail,combineSQL(DETAIL_ADD_SQL,"?",new String[]{rwdid}));
          dsRwdDetail.open();
          dsRwdDetail.first();
          for(int i=0;i<dsRwdDetail.getRowCount();i++)
          {
            detail.insertRow(false);
            detail.setValue("receiveid", receiveid);
            //rwdid= dsRwdDetail.getValue("rwdid");
            detail.setValue("cpid",  dsRwdDetail.getValue("cpid"));
            detail.setValue("dmsxid",  dsRwdDetail.getValue("dmsxid"));
            //detail.setValue("personid",  dsRwdDetail.getValue("personid"));
            detail.setValue("jth",  dsRwdDetail.getValue("jth"));
            detail.setValue("sjgg",  dsRwdDetail.getValue("sjgg"));
            //detail.setValue("sl",  dsRwdDetail.getValue("sl"));
            detail.setValue("scsl",  dsRwdDetail.getValue("scsl"));
            detail.setValue("hssl",  dsRwdDetail.getValue("hssl"));
            //detail.setValue("jjgz",  "");
            detail.setValue("cp",  "");
            detail.setValue("ccbj",  "1");
            detail.setValue("rwdmxid",  dsRwdDetail.getValue("rwdmxid"));
            detail.post();
            d_RowInfos.add(new RowMap(detail));
            dsRwdDetail.next();
          }
        }
      }
      /**
       *  强制完成触发事件
       */
      class Complete implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          int row = Integer.parseInt(data.getParameter("rownum"));
          dsMasterTable.goToRow(row);
          dsMasterTable.setValue("state", "8");
          dsMasterTable.post();
          dsMasterTable.saveChanges();
        }
      }

      /**
       *  从表删除操作
       */
      class Detail_Delete implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          putDetailInfo(data.getRequest());
          EngineDataSet ds = getDetailTable();
          int rownum = Integer.parseInt(data.getParameter("rownum"));
          //删除临时数组的一列数据
          d_RowInfos.remove(rownum);
          ds.goToRow(rownum);
          ds.deleteRow();
        }
      }
      /**
       * 得到用于查找生产加工单信息的bean
       * @param req WEB的请求
       * @return 返回用于查找生产加工单信息的bean

      public ImportProcess getProcessGoodsBean(HttpServletRequest req)
        {
        if(importprocessBean == null)
          importprocessBean = ImportProcess.getInstance(req);
        return importprocessBean;
      }
      */
      /**
       * 得到生产加工单主表一行信息的bean
       * @param req WEB的请求
       * @return 返回生产加工单主表一行信息的bean

      public B_StoreProess_Sel_Process getProcessMasterBean(HttpServletRequest req)
        {
        if(workloadSelProcessBean == null)
          workloadSelProcessBean = B_StoreProess_Sel_Process.getInstance(req);
        return workloadSelProcessBean;
      }
      */
      /**
       * 得到用于查找产品单价的bean
       * @param req WEB的请求
       * @return 返回用于查找产品单价的bean
       */
      public LookUp getTechnicsBean(HttpServletRequest req)
      {
        if(technicsBean == null){
          technicsBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_TECHNICS_PROCEDURE);
          technicsBean.regData(new String[]{});
        }
        return technicsBean;
      }
}

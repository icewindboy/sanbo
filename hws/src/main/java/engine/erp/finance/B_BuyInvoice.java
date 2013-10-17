package engine.erp.finance;

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
import engine.erp.finance.*;
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
 * <p>Title: 采购--采购发票管理</p>
 * <p>Description: 采购--采购发票管理<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
  public final class B_BuyInvoice extends BaseAction implements Operate
  {
    public  static final String SHOW_DETAIL        = "1005";
    public  static final String DETAIL_SALE_ADD    = "1006";
    public  static final String TD_RETURN_ADD      = "1007";//提单退货新增
    public  static final String DWTXID_CHANGE      = "1008";
    public  static final String CANCER_APPROVE     = "1009";
    public  static final String JHDHW              = "1010";//采购单购货
    public  static final String REPORT             = "2000";
    public  static final String INVOICE_OVER       = "1012";//完成
    public  static final String DETAIL_COPY        = "1013";//复制当前选中行
    public  static final String DEL_NULL           = "1014";//删除数量为空的行
    public  static final String DETAIL_ADD_NULL    = "1015";
    public  static final String FPLB_CHANGE        = "1016";

    private static final String MASTER_STRUT_SQL = "SELECT * FROM cw_cgfp WHERE 1<>1";
    private static final String MASTER_SQL    = "SELECT * FROM cw_cgfp WHERE 1=1 AND ? AND fgsid=? ? ORDER BY fphm DESC ";
    private static final String DETAIL_STRUT_SQL = "SELECT * FROM cw_cgfpmx WHERE 1<>1 ";
    private static final String DETAIL_SQL    = "SELECT * FROM cw_cgfpmx WHERE cgfpid= ";//
    private static final String TOTAL_JHD_SQL      ="SELECT * FROM VW_BUY_INVOICE_DETAIL WHERE jhdID= ";//
    private static final String MASTER_APPROVE_SQL = "SELECT * FROM cw_cgfp WHERE cgfpid='?'";//用于审批时候提取一条记录
    private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
    private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
    private EngineDataSet dsJHDTable      = new EngineDataSet();//交货单明细
    private BuyGoodsSelect buyGoodsSelectBean = null; //采购进货单引用
    public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "cw_cgfp");
    public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "cw_cgfpmx");
    public  boolean isApprove = false;     //是否在审批状态
    private boolean isMasterAdd = true;    //是否在添加状态
    private long    masterRow = -1;         //保存主表修改操作的行记录指针
    private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
    private ArrayList d_RowInfos = null; //从表多行记录的引用
    private LookUp buyPriceBean = null; //销售单价的bean的引用, 用于提取销售单价
    private boolean isInitQuery = false; //是否已经初始化查询条件
    private QueryBasic fixedQuery = new QueryFixedItem();
    public  String retuUrl = null;
    public  String loginId = "";   //登录员工的ID
    public  String loginCode = ""; //登陆员工的编码
    public  String loginName = ""; //登录员工的姓名
    private String qtyFormat = null, priceFormat = null, sumFormat = null;
    private String fgsid = null;   //分公司ID
    private String cgfpid="";      //采购发票ID
    private User user = null;
    public boolean isReport = false;
    public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
    public String []zt;
    public String dwdm="";
    public String dwmc="";
    private String price_method="0";

    public boolean canOperate=false;
    /**
     * 销售合同列表的实例
     * static 作用--只能得到一个这个类的实例,并且该实例存储在会话中
     * @param request jsp请求
     * @param isApproveStat 是否在审批状态
     * @return 返回销售合同列表的实例
     * The purpose of this method is to allow a single instance of this class to be shared by multiple frames
     * Declares this method as static. This means that you are able to call this method without a current instantiation of a B_BuyInvoice class object.
     * Returns an instance of the B_BuyInvoice class.
     * Checks to see if there is a current instantiation of a B_BuyInvoice.
     * Creates and returns a new B_BuyInvoice if one doesn't already exist.
     * Returns a B_BuyInvoice object if one has been instantiate
     */
    public static B_BuyInvoice getInstance(HttpServletRequest request)
    {
      B_BuyInvoice b_BuyInvoiceBean = null;
      HttpSession session = request.getSession(true);
      synchronized (session)
      {
        String beanName = "b_BuyInvoiceBean";
        b_BuyInvoiceBean = (B_BuyInvoice)session.getAttribute(beanName);
        if(b_BuyInvoiceBean == null)
        {
          //引用LoginBean
          LoginBean loginBean = LoginBean.getInstance(request);
          b_BuyInvoiceBean = new B_BuyInvoice();
          b_BuyInvoiceBean.qtyFormat = loginBean.getQtyFormat();
          b_BuyInvoiceBean.priceFormat = loginBean.getPriceFormat();
          b_BuyInvoiceBean.sumFormat = loginBean.getSumFormat();
          b_BuyInvoiceBean.fgsid = loginBean.getFirstDeptID();
          b_BuyInvoiceBean.loginId = loginBean.getUserID();
          b_BuyInvoiceBean.loginName = loginBean.getUserName();
          b_BuyInvoiceBean.user = loginBean.getUser();
          if(loginBean.getSystemParam("BUY_INVOICE_HANDWORK").equals("1"))
          b_BuyInvoiceBean.canOperate = true;//是否可以手工开
          if(loginBean.getSystemParam("BUY_PRICLE_METHOD").equals("1"))
          b_BuyInvoiceBean.price_method = "1";
          //采购供应商报价方式
          //设置格式化的字段
          b_BuyInvoiceBean.dsDetailTable.setColumnFormat("sl", b_BuyInvoiceBean.qtyFormat);
          b_BuyInvoiceBean.dsDetailTable.setColumnFormat("hsdj", b_BuyInvoiceBean.priceFormat);
          b_BuyInvoiceBean.dsDetailTable.setColumnFormat("wsdj", b_BuyInvoiceBean.priceFormat);
          b_BuyInvoiceBean.dsDetailTable.setColumnFormat("je", b_BuyInvoiceBean.sumFormat);
          b_BuyInvoiceBean.dsDetailTable.setColumnFormat("zzsl", b_BuyInvoiceBean.qtyFormat);
          b_BuyInvoiceBean.dsDetailTable.setColumnFormat("se", b_BuyInvoiceBean.sumFormat);
          b_BuyInvoiceBean.dsDetailTable.setColumnFormat("jshj", b_BuyInvoiceBean.sumFormat);
          session.setAttribute(beanName, b_BuyInvoiceBean);
        }
      }
      return b_BuyInvoiceBean;
    }
    /**
     * 构造函数
     * private使得该类的实例创建被定制
     */
    private B_BuyInvoice()
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
      String pref = "CG";
      dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"fphm"},new String[]{"SELECT pck_base.billNextCode('cw_cgfp','fphm') from dual"}));// new String[]{"SELECT pck_base.fieldNextCode('cw_cgfp','fphm','"+ pref +"','',6) from dual"}));
      dsMasterTable.setSort(new SortDescriptor("", new String[]{"fphm"}, new boolean[]{true}, null, 0));//new String[]{"SELECT pck_base.billNextCode('cw_cgjs','djh') from dual"}));
      dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"cgfpmxID"}, new String[]{"s_cw_cgfpmx"}));

      Master_Add_Edit masterAddEdit = new Master_Add_Edit();
      Master_Post masterPost = new Master_Post();
      addObactioner(String.valueOf(INIT), new Init());
      addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
      addObactioner(SHOW_DETAIL, new ShowDetail());
      addObactioner(String.valueOf(ADD), masterAddEdit);
      addObactioner(String.valueOf(TD_RETURN_ADD), masterAddEdit);//提单退货新增
      addObactioner(String.valueOf(EDIT), masterAddEdit);
      addObactioner(String.valueOf(DEL), new Master_Delete());
      addObactioner(String.valueOf(POST), masterPost);
      addObactioner(String.valueOf(POST_CONTINUE), masterPost);
      addObactioner(String.valueOf(JHDHW), new Detail_Add());//采购单货物
      addObactioner(String.valueOf(DETAIL_SALE_ADD), new Detail_Lading_ADD());//采购单
      addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
      //&#$//审核部分
      addObactioner(String.valueOf(APPROVE), new Approve());
      addObactioner(String.valueOf(REPORT), new Approve());
      addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
      addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());
      addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());
      addObactioner(String.valueOf(DEPT_CHANGE), new Dept_Change());
      addObactioner(String.valueOf(INVOICE_OVER), new Invoice_Over());//完成
      addObactioner(String.valueOf(DETAIL_COPY), new Detail_Copy_Add());//DETAIL_COPY
      addObactioner(String.valueOf(DEL_NULL), new Detail_Delete_Null());//Detail_Delete_Null
      addObactioner(String.valueOf(DETAIL_ADD_NULL), new Detail_Add_Null());
      addObactioner(String.valueOf(FPLB_CHANGE), new Invoice_Type_Change());


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
      if(dsJHDTable != null){
        dsJHDTable.close();
        dsJHDTable = null;
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
          m_RowInfo.put("czrq", today);//制单日期
          m_RowInfo.put("kprq", today);//制单日期
          m_RowInfo.put("czy", loginName);//操作员
          m_RowInfo.put("czyid", loginId);
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
        detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
        detailRow.put("cpid", rowInfo.get("cpid_"+i));
        detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));
        detailRow.put("hsdj", formatNumber(rowInfo.get("hsdj_"+i), priceFormat));//含税单价
        detailRow.put("wsdj", formatNumber(rowInfo.get("wsdj_"+i), priceFormat));//无税单价
        detailRow.put("je", rowInfo.get("je_"+i));//金额
        detailRow.put("zzsl", rowInfo.get("zzsl_"+i));//税率
        detailRow.put("se", formatNumber(rowInfo.get("se_"+i), sumFormat));//净金额
        detailRow.put("jshj", rowInfo.get("jshj_"+i));//价税合计
        detailRow.put("hssl", rowInfo.get("hssl_"+i));
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
      //cgfpid= dsMasterTable.getValue("cgfpid");//关链
      String SQL = DETAIL_SQL + (isMasterAdd ? "-1" : cgfpid);
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
     * 得到报价方式
     * 1=以换算单位报价,0=主单位报价
     **/
    public final String getPriceMethod()
    {
      return price_method;
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
    //&#$
    /**
     * 审批操作的触发类
     */
    class Approve implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        String id=null;
        isMasterAdd=false;
        HttpServletRequest request = data.getRequest();
        masterProducer.init(request, loginId);
        detailProducer.init(request, loginId);
        //得到request的参数,值若为null, 则用""代替
        if(String.valueOf(REPORT).equals(action))
        {
          isReport=true;
          isApprove = false;
          id=request.getParameter("id");
        }else
        {
          isApprove = true;
          id = data.getParameter("id", "");
        }
        id = data.getParameter("id", "");
        String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
        dsMasterTable.setQueryString(sql);
        if(dsMasterTable.isOpen())
          dsMasterTable.readyRefresh();
        dsMasterTable.refresh();

        dsMasterTable.readyRefresh();
        //打开从表
        cgfpid= dsMasterTable.getValue("cgfpid");//关链
        openDetailTable(false);

        initRowInfo(true, false, true);
        initRowInfo(false, false, true);
      }
    }

    //&#$
    /**
     * 添加到审核列表的操作类
     */
    class Add_Approve implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
        String content = dsMasterTable.getValue("fphm");
        String deptid = dsMasterTable.getValue("deptid");
        //content += "1合同编号2合同编号3合同编号4合同编号5合同编号6合同编号7合同编号8合同编号9合同编号10合同编号11合同编号12合同编号13合同编号14合同编号15合同编号";
        approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "buy_invoice", content,deptid);
      }
    }
    /**
     * 取消
     */
    class Cancer_Approve implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
        approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(),"buy_invoice");
      }
    }
    /**
     * 完成
     */
    class Invoice_Over implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        dsMasterTable.setValue("zt","8");
        dsMasterTable.saveChanges();
      }
    }
    /**
     * 初始化操作的触发类
     */
    class Init implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        //&#$
        isApprove = false;
        isReport = false;
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
        row.put("kprq$a", startDay);
        row.put("kprq$b", today);
        //row.put("zt","0");
        zt = new String[]{""};
        isMasterAdd = true;
        //
        String SQL=" ";
        SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);
        if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
          dsDetailTable.empty();
        //data.setMessage(showJavaScript("showFixedQuery();"));
        String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
        if(code!=null&&code.equals("1"))
          submitType=true;
        else
          submitType=false;          }
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
        cgfpid = dsMasterTable.getValue("cgfpid");
        //打开从表
        openDetailTable(false);
      }
    }
    /**
    * ---------------------------------------------------------
    * 主表添加或修改操作的触发类
    */
   class Master_Add_Edit implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       //&#$
       isApprove = false;
       if(String.valueOf(EDIT).equals(action))
       {
         isMasterAdd=false;
         dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
         masterRow = dsMasterTable.getInternalRow();
         cgfpid= dsMasterTable.getValue("cgfpid");//关链
       }
       else{
         isMasterAdd=true;
       }
       synchronized(dsDetailTable){
         openDetailTable(isMasterAdd);
       }
       initRowInfo(true, isMasterAdd, true);
       initRowInfo(false, isMasterAdd, true);
       data.setMessage(showJavaScript("toDetail();"));
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
        if(!isMasterAdd)
          ds.goToInternalRow(masterRow);
        //得到主表主键值
        String cgfpid = null;
        if(isMasterAdd){
          ds.insertRow(false);
          cgfpid = dataSetProvider.getSequence("s_cw_xsfp");
          String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
          ds.setValue("cgfpid", cgfpid);//主健
          ds.setValue("zt","0");
          ds.setValue("czrq", date);//制单日期
          ds.setValue("kprq", date);//开单初始化为系统日期
          ds.setValue("czyid", loginId);
          ds.setValue("czy", loginName);//操作员
          ds.setValue("fgsid", fgsid);//分公司
        }
        //保存从表的数据
        RowMap detailrow = null;
        BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);
        EngineDataSet detail = getDetailTable();
        detail.first();
        for(int i=0; i<detail.getRowCount(); i++)
        {
          detailrow = (RowMap)d_RowInfos.get(i);
          //新添的记录
          detail.setValue("cgfpid", cgfpid);
          detail.setValue("cpid", detailrow.get("cpid"));//?
          detail.setValue("jhdhwid", detailrow.get("jhdhwid"));//?
          detail.setValue("dmsxid", detailrow.get("dmsxid"));//

          double wsdj = detailrow.get("wsdj").length() > 0 ? Double.parseDouble(detailrow.get("wsdj")) : 0;//销售价
          double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;  //数量
          double zzsl = detailrow.get("zzsl").length() > 0 ? Double.parseDouble(detailrow.get("zzsl")) : 0;//税率
          detail.setValue("sl", detailrow.get("sl"));//数量
          detail.setValue("je", engine.util.Format.formatNumber( Double.parseDouble(detailrow.get("je")),"#.00"));//无税金额
          detail.setValue("hsdj", detailrow.get("hsdj"));//含税单价
          detail.setValue("wsdj", detailrow.get("wsdj"));//无税单价
          detail.setValue("zzsl", detailrow.get("zzsl"));//税率
          detail.setValue("hssl", detailrow.get("hssl"));
          detail.setValue("se",engine.util.Format.formatNumber( Double.parseDouble(detailrow.get("se")),"#.00"));//税额
          detail.setValue("jshj",engine.util.Format.formatNumber( Double.parseDouble(detailrow.get("jshj")),"#.00"));//价税合计
          //保存用户自定义的字段
          FieldInfo[] fields = detailProducer.getBakFieldCodes();
          for(int j=0; j<fields.length; j++)
          {
            String fieldCode = fields[j].getFieldcode();
            detail.setValue(fieldCode, detailrow.get(fieldCode));
          }
          detail.post();
          totalNum = totalNum.add(detail.getBigDecimal("sl"));
          totalSum = totalSum.add(detail.getBigDecimal("je"));
          detail.next();
        }
        //保存主表数据
        ds.setValue("kprq", rowInfo.get("kprq"));//开票日期
        ds.setValue("deptid", rowInfo.get("deptid"));//部门id
        ds.setValue("jsfsid", rowInfo.get("jsfsid"));//结算方式ID
        ds.setValue("dwtxid", rowInfo.get("dwtxid"));//购货单位ID
        ds.setValue("fphm", rowInfo.get("fphm"));
        ds.setValue("sjhm", rowInfo.get("sjhm"));  ////发票号
        ds.setValue("personid", rowInfo.get("personid"));//人员ID
        ds.setValue("fplbid", rowInfo.get("fplbid"));//发票种类
        ds.setValue("dz", rowInfo.get("dz"));//地址
        ds.setValue("sh", rowInfo.get("sh"));//税号
        ds.setValue("khh", rowInfo.get("khh"));//开户行
        ds.setValue("zh", rowInfo.get("zh"));//账号
        ds.setValue("bz", rowInfo.get("bz"));//摘要
        ds.setValue("fgsid", fgsid);//分公司
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
          initRowInfo(true, true, true);//重新初始化从表的各行信息
          detail.empty();
          initRowInfo(false, false, true);//重新初始化从表的各行信息
        }
        else if(String.valueOf(POST).equals(action))
          data.setMessage(showJavaScript("backList();"));
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
       * 校验从表表单信息从表输入的信息的正确性
       * @return null 表示没有信息
       */
      private String checkDetailInfo()
      {
        if(d_RowInfos.size()==0)
        {
          return showJavaScript("alert('从表不能空--没产品等相关信息！');");
        }
        String temp = null;
        RowMap detailrow = null;
        for(int i=0; i<d_RowInfos.size(); i++)
        {
          detailrow = (RowMap)d_RowInfos.get(i);
          String sl = detailrow.get("sl");
          if((temp = checkNumber(sl, detailProducer.getFieldInfo("sl").getFieldname())) != null)
            return temp;
          if(Double.parseDouble(sl)==0)
            return showJavaScript("alert('数量不能为零!');");
          String wsdj = detailrow.get("wsdj");
          if((temp = checkNumber(wsdj, detailProducer.getFieldInfo("wsdj").getFieldname())) != null)
            return temp;
          String zzsl = detailrow.get("zzsl");
          if((temp = checkNumber(zzsl, detailProducer.getFieldInfo("zzsl").getFieldname())) != null)
            return temp;
          String hsdj = detailrow.get("hsdj");
          if((temp = checkNumber(hsdj, detailProducer.getFieldInfo("hsdj").getFieldname())) != null)
            return temp;
        }
        return null;
      }
      /**
       * 校验主表表表单信息从表输入的信息的正确性
       * @return null 表示没有信息,校验通过
       */
      private String checkMasterInfo()
      {
        RowMap rowInfo = getMasterRowinfo();
        String temp = rowInfo.get("kprq");
        if(temp.equals(""))
          return showJavaScript("alert('开票日期不能为空！');");
        else if(!isDate(temp))
          return showJavaScript("alert('非法开票日期！');");
        temp = rowInfo.get("dwtxid");
        if(temp.equals(""))
          return showJavaScript("alert('请选择购货单位！');");
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
        cgfpid = ds.getValue("cgfpid");
        String zt = dataSetProvider.getSequence("SELECT a.zt FROM cw_cgfp a WHERE a.cgfpid='"+cgfpid+"'");
        if(zt!=null&&!zt.equals("0"))
        {
          data.setMessage(showJavaScript("alert('该单据不能删除!')"));
          return;
        }
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

        zt = data.getRequest().getParameterValues("zt");
        if(!(zt==null))
        {
          StringBuffer sbzt = null;
          for(int i=0;i<zt.length;i++)
          {
            if(sbzt==null)
              sbzt= new StringBuffer(" AND zt IN(");
            sbzt.append(zt[i]+",");
          }
          if(sbzt == null)
            sbzt =new StringBuffer();
          else
            sbzt.append("-99)");
          SQL = SQL+sbzt.toString();
        }
        else
          zt = new String[]{""};

        SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});//组装SQL语句

        if(!dsMasterTable.getQueryString().equals(SQL))
        {
          dsMasterTable.setQueryString(SQL);
          dsMasterTable.setRowMax(null);
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
        EngineDataSet master = dsMasterTable;
        //EngineDataSet detail = dsDetailTable;
        if(!master.isOpen())
          master.open();
        //初始化固定的查询项目
        fixedQuery = new QueryFixedItem();
        fixedQuery.addShowColumn("", new QueryColumn[]{
          new QueryColumn(master.getColumn("fphm"), null, null, null, null, "="),//
          new QueryColumn(master.getColumn("sjhm"), null, null, null, null, "="),//发票号码
          new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//购货单位
          new QueryColumn(master.getColumn("kprq"), null, null, null, "a", ">="),//开票日期
          new QueryColumn(master.getColumn("kprq"), null, null, null, "b", "<="),//开票日期
          new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门id
          new QueryColumn(master.getColumn("fplbid"), null, null, null, null, "="),
          //new QueryColumn(master.getColumn("zt"), null, null, null, null, "="),
          new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
          new QueryColumn(master.getColumn("personid"), "emp", "personid", "xm", "xm", "like"),
          new QueryColumn(master.getColumn("czy"), null, null, null, null, "like")
        });
        isInitQuery = true;
      }
    }
    /**
     *  从表新增
     */
    class Detail_Add_Null implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest req = data.getRequest();
        putDetailInfo(data.getRequest());
        engine.project.LookUp invoiceTypeBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_BUY_INVOICE_TYPE);//发票种类
        String fplbid=m_RowInfo.get("fplbid");
        RowMap fplbRow = invoiceTypeBean.getLookupRow(fplbid);
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("cgfpmxID", "-1");
        dsDetailTable.setValue("cgfpid", cgfpid);
        dsDetailTable.setValue("zzsl", fplbRow.get("sl"));
        dsDetailTable.post();
        RowMap detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);

      }
  }
    /**
     *  从表增加
     */
    class Detail_Add implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest req = data.getRequest();
        putDetailInfo(data.getRequest());

        engine.project.LookUp invoiceTypeBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_BUY_INVOICE_TYPE);//发票种类
        String fplbid=m_RowInfo.get("fplbid");
        RowMap fplbRow = invoiceTypeBean.getLookupRow(fplbid);
        String zzsl = fplbRow.get("sl");//税率
        double dzzsl = Double.parseDouble(zzsl.equals("")?"17":zzsl);

        String multiIdInput = m_RowInfo.get("multiIdInput");
        if(multiIdInput.length() == 0)
          return;
        //实例化查找数据集的类
        EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jhdhwid");
        String[] jhdhwids = parseString(multiIdInput,",");//解析出合同货物ID数组
        buyGoodsSelectBean =getselectBuyGoodsBean(req);
        for(int i=0; i < jhdhwids.length; i++)
        {
          if(jhdhwids[i].equals("-1"))
            continue;
          if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
          String cgfpid = dsMasterTable.getValue("cgfpid");
          locateGoodsRow.setValue(0, jhdhwids[i]);
          if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
          {
            RowMap saleRow = buyGoodsSelectBean.getLookupRow(jhdhwids[i]);
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("cgfpmxID", "-1");
            dsDetailTable.setValue("cgfpid", cgfpid);
            dsDetailTable.setValue("jhdhwid", jhdhwids[i]);
            dsDetailTable.setValue("cpid", saleRow.get("cpid"));
            dsDetailTable.setValue("hsdj", saleRow.get("dj"));
            dsDetailTable.setValue("dmsxid", saleRow.get("dmsxid"));

            double sl = Double.parseDouble(saleRow.get("sl").equals("")?"0":saleRow.get("sl"));
            double hsdj =Double.parseDouble(saleRow.get("dj").equals("")?"0":saleRow.get("dj"));
            double wsdj = hsdj/(1+dzzsl*0.01);
            double se = Double.parseDouble(saleRow.get("sl").equals("")?"0":saleRow.get("sl"))*wsdj*dzzsl*0.01;
            double hssl = Double.parseDouble(saleRow.get("hssl").equals("")?"0":saleRow.get("hssl"));
            double skphsl = Double.parseDouble(saleRow.get("skphsl").equals("")?"0":saleRow.get("skphsl"));
            //jshjObj.value=formatQty(parseFloat(jeObj.value)*(1+parseFloat(zzslObj.value)*0.01));
            double dje = sl*wsdj;
            double djshj= sl*hsdj;

            dsDetailTable.setValue("je", String.valueOf(dje));
            dsDetailTable.setValue("sl", saleRow.get("sl"));
            dsDetailTable.setValue("hssl", String.valueOf(hssl-skphsl));

            dsDetailTable.setValue("wsdj", String.valueOf(wsdj));
            dsDetailTable.setValue("zzsl", zzsl);
            dsDetailTable.setValue("se", String.valueOf(se));
            dsDetailTable.setValue("jshj", String.valueOf(djshj));
            dsDetailTable.post();
            //创建一个与用户相对应的行
            RowMap detailrow = new RowMap(dsDetailTable);
            d_RowInfos.add(detailrow);
          }
         }
      }
    }
    /**
     * 复制当前行
     *
     * */
    class Detail_Copy_Add implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest req = data.getRequest();
        putDetailInfo(data.getRequest());
        String rownum = req.getParameter("rownum");
        String tCopyNumber = req.getParameter("tCopyNumber");
        int row = Integer.parseInt(rownum);
        int size = d_RowInfos.size();
        if(row>size)
          return;
        RowMap newadd = (RowMap)d_RowInfos.get(row);
        String dmsxid = newadd.get("dmsxid");
        String cpid = newadd.get("cpid");
        String xsj = newadd.get("xsj");
        String zzsl = newadd.get("zzsl");
        String hthwid = newadd.get("hthwid");
        int copynumber = Integer.parseInt(tCopyNumber);

        for(int i=0;i<copynumber;i++)
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("cgfpmxID", "-1");
          dsDetailTable.setValue("cgfpid", cgfpid);
          dsDetailTable.setValue("dmsxid", dmsxid);
          dsDetailTable.setValue("cpid", cpid);
          dsDetailTable.setValue("zzsl", zzsl);
          dsDetailTable.post();
          RowMap detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
        tCopyNumber="1";
      }
    }
    /**
     * 引入提单主从
     *
     * */
    class Detail_Lading_ADD implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest req = data.getRequest();
        putDetailInfo(data.getRequest());
        String selectedjhdid = m_RowInfo.get("selectedtdid");
        if(selectedjhdid.equals(""))
          return;

        engine.project.LookUp invoiceTypeBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_BUY_INVOICE_TYPE);//发票种类
        String fplbid=m_RowInfo.get("fplbid");
        RowMap fplbRow = invoiceTypeBean.getLookupRow(fplbid);
        String zzsl = fplbRow.get("sl");//税率
        double dzzsl = Double.parseDouble(zzsl.equals("")?"17":zzsl);

        String dwtxid =m_RowInfo.get("dwtxid");
        String dz =m_RowInfo.get("dz");
        String sh =m_RowInfo.get("sh");
        String khh =m_RowInfo.get("khh");
        String zh =m_RowInfo.get("zh");
        String deptid =m_RowInfo.get("deptid");
        String personid =m_RowInfo.get("personid");
        if(dsJHDTable.isOpen())dsJHDTable.close();
        setDataSetProperty(dsJHDTable,TOTAL_JHD_SQL+selectedjhdid);
        dsJHDTable.open();
        if(!isMasterAdd)
        {
        dsMasterTable.goToInternalRow(masterRow);
        }
        dsJHDTable.first();

        m_RowInfo.put("dwtxid",dwtxid.equals("")?dsJHDTable.getValue("dwtxid"):dwtxid);
        m_RowInfo.put("dz",dz.equals("")?dsJHDTable.getValue("addr"):dz);
        m_RowInfo.put("sh",sh.equals("")?dsJHDTable.getValue("nsrdjh"):sh);
        m_RowInfo.put("khh",khh.equals("")?dsJHDTable.getValue("khh"):khh);
        m_RowInfo.put("zh",zh.equals("")?dsJHDTable.getValue("zh"):zh);
        m_RowInfo.put("deptid",deptid.equals("")?dsJHDTable.getValue("deptid"):deptid);
        m_RowInfo.put("personid",personid.equals("")?dsJHDTable.getValue("personid"):personid);
        EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jhdhwid");
        dsJHDTable.first();
        for(int i=0;i<dsJHDTable.getRowCount();i++)
        {
          if(!isMasterAdd)
          {
          dsMasterTable.goToInternalRow(masterRow);
          }
          String cgfpid = dsMasterTable.getValue("cgfpid");
          String jhdhwid=dsJHDTable.getValue("jhdhwid");
          locateGoodsRow.setValue(0, jhdhwid);
          if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
          {
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("cgfpmxID", "-1");
            dsDetailTable.setValue("cgfpid", cgfpid);
            dsDetailTable.setValue("cpid", dsJHDTable.getValue("cpid"));
            dsDetailTable.setValue("jhdhwid", dsJHDTable.getValue("jhdhwid"));
            dsDetailTable.setValue("sl", dsJHDTable.getValue("sl"));
            dsDetailTable.setValue("hsdj", dsJHDTable.getValue("dj"));
            dsDetailTable.setValue("dmsxid", dsJHDTable.getValue("dmsxid"));

            //dsDetailTable.setValue("je", dsJHDTable.getValue("je"));
            double sl = Double.parseDouble(dsJHDTable.getValue("sl").equals("")?"0":dsJHDTable.getValue("sl"));
            double hssl = Double.parseDouble(dsJHDTable.getValue("hssl").equals("")?"0":dsJHDTable.getValue("hssl"));
            double skphsl = Double.parseDouble(dsJHDTable.getValue("skphsl").equals("")?"0":dsJHDTable.getValue("skphsl"));
            double hsdj =Double.parseDouble(dsJHDTable.getValue("dj").equals("")?"0":dsJHDTable.getValue("dj"));
            double wsdj = hsdj/(1+dzzsl*0.01);
            double se = sl*wsdj*dzzsl*0.01;

            dsDetailTable.setValue("wsdj", String.valueOf(wsdj));
            dsDetailTable.setValue("hssl", String.valueOf(hssl-skphsl));
            dsDetailTable.setValue("je", String.valueOf(sl*wsdj));

            if(price_method.equals("1"))
            {
              dsDetailTable.setValue("je", String.valueOf((hssl-skphsl)*wsdj));
              se = (hssl-skphsl)*wsdj*dzzsl*0.01;
            }
            dsDetailTable.setValue("zzsl", zzsl);
            dsDetailTable.setValue("se", String.valueOf(se));
            dsDetailTable.setValue("jshj", String.valueOf(sl*hsdj));
            dsDetailTable.post();
            RowMap detailrow = new RowMap(dsDetailTable);
            d_RowInfos.add(detailrow);
          }
          dsJHDTable.next();
        }
      }
    }
    /**
     * 部门改变
     * */
    class Dept_Change implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest req = data.getRequest();
        m_RowInfo.put(req);
      }
    }
    /**
     * 发票类别
     * */
    class Invoice_Type_Change implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest req = data.getRequest();
        String oldfplbid=m_RowInfo.get("fplbid");
        putDetailInfo(data.getRequest());
        engine.project.LookUp invoiceTypeBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_BUY_INVOICE_TYPE);//发票种类
        String fplbid=m_RowInfo.get("fplbid");
         RowMap fplbRow = invoiceTypeBean.getLookupRow(fplbid);
        if(oldfplbid.equals(fplbid))
        {
          return;
        }
        else
        {
          int rownum = d_RowInfos.size();
          String sl = fplbRow.get("sl");//税率
          RowMap detailRow = null;
          for(int i=0; i<rownum; i++)
          {
            detailRow = (RowMap)d_RowInfos.get(i);

            double dzzsl = Double.parseDouble(sl.equals("")?"17":sl);
            double dsl = Double.parseDouble(detailRow.get("sl").equals("")?"0":detailRow.get("sl"));
            double dhsdj = Double.parseDouble(detailRow.get("hsdj").equals("")?"0":detailRow.get("hsdj"));
            //double dwsdj = Double.parseDouble(detailRow.get("wsdj").equals("")?"0":detailRow.get("wsdj"));

            double dje = dsl*dhsdj/(1+dzzsl*0.01);
            double djshj= dje*(1+(dzzsl*0.01));

            detailRow.put("je", String.valueOf(dje));
            detailRow.put("zzsl", String.valueOf(dzzsl));//税率
            detailRow.put("se", formatNumber(dsl*dzzsl*0.01*dhsdj/(1+dzzsl*0.01), sumFormat));//税额
            detailRow.put("jshj", formatNumber(dsl*dhsdj, sumFormat));
            detailRow.put("wsdj", formatNumber(dhsdj/(1+dzzsl*0.01), sumFormat));
          }
        }
      }
    }
    /**
     * 单位变化
     * */
    class Dwtxid_Change implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest req = data.getRequest();
        engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP);

        String olddwtxid=m_RowInfo.get("dwtxid");
        putDetailInfo(data.getRequest());//保存输入的明细信息
        String dwtxid=m_RowInfo.get("dwtxid");
        RowMap corRow = corpBean.getLookupRow(dwtxid);

        if(olddwtxid.equals(dwtxid))
        {
          return;
        }
        else
        {
          m_RowInfo.put("dwtxid",dwtxid);
          m_RowInfo.put("zh",req.getParameter("zh"));
          m_RowInfo.put("khh",req.getParameter("khh"));
          m_RowInfo.put("sh",req.getParameter("sh"));
          m_RowInfo.put("dz",req.getParameter("dz"));
          m_RowInfo.put("deptid",req.getParameter("deptid").equals("")?corRow.get("deptid"):req.getParameter("deptid"));
          m_RowInfo.put("personid",req.getParameter("personid").equals("")?corRow.get("personid"):req.getParameter("personid"));
          m_RowInfo.put("bz","");
          dsDetailTable.empty();
          d_RowInfos.clear();
        }
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
     *  从表数量为空的行删除操作
     */
    class Detail_Delete_Null implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        putDetailInfo(data.getRequest());
        delRows();
      }
      /**
       * 删除
       *
       * */
      public void delRows() throws Exception
      {
        for(int i=0;i<d_RowInfos.size();i++)
        {
          RowMap detailRow = (RowMap)d_RowInfos.get(i);
          String sl = detailRow.get("sl");
          if(sl.equals(""))
          {
            d_RowInfos.remove(i);
            delRows();
          }
        }
      }
    }
    /**
     * 通过jhdhwid得到采购货物相应的信息
     * @param req WEB的请求
     * @return 返回用于查找合同编号的bean
     */
    public BuyGoodsSelect getselectBuyGoodsBean(HttpServletRequest req)
    {
      if(buyGoodsSelectBean == null)
        buyGoodsSelectBean = BuyGoodsSelect.getInstance(req);
      return buyGoodsSelectBean;
    }
}
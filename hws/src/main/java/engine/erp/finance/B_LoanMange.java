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
import engine.erp.sale.*;
import engine.html.*;
import engine.erp.finance.*;
import engine.common.*;
import java.util.ArrayList;
import java.util.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 销售管理--销售发票--</p>
 * <p>Description: 销售管理--销售发票--<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class B_LoanMange extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL        = "2001";
  public  static final String DETAIL_SALE_ADD    = "2002";
  public  static final String PRODUCT_ADD        = "2003";
  public  static final String DWTXID_CHANGE      = "2004";
  public  static final String TD_RETURN_ADD      = "2005";    //提单退货新增
  public  static final String CANCER_APPROVE     = "2006"; //取消审批
  public  static final String INVOICE_OVER       = "2007"; //完成
  public  static final String REPORT             = "2008";  //
  public  static final String FPLB_CHANGE        = "2009";

  private static final String MASTER_STRUT_SQL   = "SELECT * FROM cw_loan WHERE 1<>1 ";
  private static final String MASTER_SQL         = "SELECT * FROM cw_loan WHERE 1=1 AND ? AND filialeid=? ?  ";

  private static final String DETAIL_STRUT_SQL   = "SELECT * FROM cw_loanmx WHERE 1<>1 ";
  private static final String DETAIL_SQL         = "SELECT * FROM cw_loanmx WHERE state in(1,8) and loanid=? order by loanmxid ";//

  private static final String TDMX_SQL           = "SELECT * FROM VW_SALE_INVOICE_IMPORT_TDDETAL WHERE tdid= ";//
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM cw_loan WHERE loanid='?'";//用于审批时候提取一条记录


  private static final String CALCULATE_LX     = "SELECT a.loanid,a.loanfund,a.loandate,b.loanmxid,b.retnfund,b.retndate FROM cw_loan a,cw_loanmx b WHERE b.state in (1,8) and  a.loanid=b.loanid(+) ? order by b.loanmxid";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet tdMxTable      = new EngineDataSet();//提单明细
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "cw_loan");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "cw_loanmx");
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private Select_Bill_Of_Lading select_Bill_Of_LadingBean = null; //销售提单引用
  private Select_LadingBill_Product ladingBill_ProductBean = null; //提单货物引用
  private LookUp salePriceBean = null; //销售单价的bean的引用, 用于提取销售单价
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String filialeid = null;   //分公司ID
  private String loanid="";      //销售发票ID

  private User user = null;
  public boolean isReport = false;
  public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
  public String []zt;
  public String dwdm="";
  public String dwmc="";
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_LoanMange getInstance(HttpServletRequest request)
  {
    B_LoanMange B_LoanMangeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_LoanMangeBean";
      B_LoanMangeBean = (B_LoanMange)session.getAttribute(beanName);
      if(B_LoanMangeBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);//引用LoginBean
        B_LoanMangeBean = new B_LoanMange();
        B_LoanMangeBean.qtyFormat = loginBean.getQtyFormat();
        B_LoanMangeBean.priceFormat = loginBean.getPriceFormat();
        B_LoanMangeBean.sumFormat = loginBean.getSumFormat();
        B_LoanMangeBean.filialeid = loginBean.getFirstDeptID();
        B_LoanMangeBean.loginId = loginBean.getUserID();
        B_LoanMangeBean.loginName = loginBean.getUserName();
        B_LoanMangeBean.user = loginBean.getUser();
        session.setAttribute(beanName, B_LoanMangeBean);
      }
    }
    return B_LoanMangeBean;
  }
  /**
   * 构造函数
   */
  private B_LoanMange()
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
    String pref = "FP";
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"djh"}, new String[]{"SELECT pck_base.billNextCode('cw_loan','djh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"djh"}, new boolean[]{true}, null, 0));
    //dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"xsfpmxID"}, new String[]{"s_cw_loanmx"}));
    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(TD_RETURN_ADD), masterAddEdit);//提单退货新增
    addObactioner(String.valueOf(APPROVE), new Approve());//审核部分
    addObactioner(String.valueOf(REPORT), new Approve());

    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_SALE_ADD), new Detail_Lading_ADD());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(PRODUCT_ADD), new Detail_Lading_Product_ADD());
    addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());
    addObactioner(String.valueOf(DEPT_CHANGE), new Dept_Change());//部门变化

    addObactioner(String.valueOf(FPLB_CHANGE), new Invoice_Type_Change());
    addObactioner(String.valueOf(INVOICE_OVER), new Invoice_Over());//完成
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
    if(tdMxTable != null){
      tdMxTable.close();
      tdMxTable = null;
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
    if(isMaster){
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();
      if(!isAdd)
        m_RowInfo.put(getMaterTable());
      else
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        m_RowInfo.put("czrq", today);//制单日期
        m_RowInfo.put("kprq", today);//开票日期
        m_RowInfo.put("czy", loginName);//操作员
        m_RowInfo.put("czyid", loginId);
        m_RowInfo.put("state", "0");
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
    rowInfo.put(request); //保存网页的所有信息
    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));
      detailRow.put("hsdj", formatNumber(rowInfo.get("hsdj_"+i), priceFormat));//含税单价
      detailRow.put("wsdj", formatNumber(rowInfo.get("wsdj_"+i), priceFormat));//无税单价
      detailRow.put("je", rowInfo.get("je_"+i));//金额
      detailRow.put("zzsl", rowInfo.get("zzsl_"+i));//税率
      detailRow.put("se", formatNumber(rowInfo.get("se_"+i), sumFormat));//净金额
      detailRow.put("jshj", rowInfo.get("jshj_"+i));//价税合计
      FieldInfo[] fields = detailProducer.getBakFieldCodes();//保存用户自定义的字段
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
  }
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    return dsDetailTable;
  }
  /**得到剩余本息**/
   public String getBX(String loanid,String yearate)
   {
     String id = loanid;
     EngineDataSet tmp = new EngineDataSet();
     setDataSetProperty(tmp,combineSQL(CALCULATE_LX,"?",new String[]{" and a.loanid='"+loanid+"'"}));;
     tmp.openDataSet();
     String loanfund="";
     double dayrate =Double.parseDouble(yearate.equals("")?"0":yearate)/(365*100);
     double bx = 0;
     String retndate = "";
     String loandate = "";
     String retnfund ="";
     tmp.first();
     if(tmp.getRowCount()>0)
       return "over";
     else
       return "";

     /*
     for(int i=0;i<tmp.getRowCount();i++)
     {
       if(i==0)
       {
         loanfund = tmp.getValue("loanfund");
         loandate = tmp.getValue("loandate");
       }
       retndate = tmp.getValue("retndate");
       retnfund = tmp.getValue("retnfund");
       if(retnfund==null||retnfund.equals(""))
         return "";
       try
       {
         Calendar  cd= new GregorianCalendar();
         Date startdate = new SimpleDateFormat("yyyy-MM-dd").parse(loandate);//提单的开单日期
         Date enddate = new SimpleDateFormat("yyyy-MM-dd").parse(retndate);
         cd.setTime(startdate);
         long s = cd.getTimeInMillis();
         cd.setTime(enddate);
         long e =cd.getTimeInMillis();
         long days = (e-s)/(60*60*24*1000);
         double dloanfund = Double.parseDouble(loanfund);
         double dretnfund = Double.parseDouble(retnfund);
         loanfund = String.valueOf(dloanfund+dloanfund*days*dayrate-dretnfund);
       }catch(Exception e){}
       loandate = retndate;
       tmp.next();
     }
     double dretnfund = Double.parseDouble(loanfund.equals("")?"0":loanfund);
     if(dretnfund<0.01)
       return "over";
     return "";
     */
 }
 /**得到剩余本息**/
  public String getSYBX(String loanid,String lonamxid,String yearate)
  {
    String id = loanid;
    String mxid = lonamxid;
    EngineDataSet tmp = new EngineDataSet();
    setDataSetProperty(tmp,combineSQL(CALCULATE_LX,"?",new String[]{" and a.loanid='"+loanid+"' and b.loanmxid<="+mxid}));;
    tmp.openDataSet();
    String loanfund="";
    double dayrate =Double.parseDouble(yearate.equals("")?"0":yearate)/(365*100);
    double bx = 0;
    String retndate = "";
    String loandate = "";
    String retnfund ="";
    tmp.first();
    for(int i=0;i<tmp.getRowCount();i++)
    {
      if(i==0)
      {
        loanfund = tmp.getValue("loanfund");
        loandate = tmp.getValue("loandate");
      }
      retndate = tmp.getValue("retndate");
      retnfund = tmp.getValue("retnfund");
      try
      {
        Calendar  cd= new GregorianCalendar();
        Date startdate = new SimpleDateFormat("yyyy-MM-dd").parse(loandate);//提单的开单日期
        Date enddate = new SimpleDateFormat("yyyy-MM-dd").parse(retndate);
        cd.setTime(startdate);
        long s = cd.getTimeInMillis();
        cd.setTime(enddate);
        long e =cd.getTimeInMillis();
        long days = (e-s)/(60*60*24*1000);
        double dloanfund = Double.parseDouble(loanfund);
        double dretnfund = Double.parseDouble(retnfund);
        loanfund = String.valueOf(dloanfund+dloanfund*days*dayrate-dretnfund);
      }catch(Exception e){}
      loandate = retndate;
      tmp.next();
    }
    return engine.util.Format.formatNumber(loanfund,"#0.00");
 }
  private final void openDetailTable(boolean isMasterAdd)
  {
    //loanid = dsMasterTable.getValue("loanid");//关链
    dsDetailTable.setQueryString(combineSQL(DETAIL_SQL,"?",new String[]{(isMasterAdd ? "-1" : loanid)}));
    if(dsDetailTable.isOpen())
    {
      dsDetailTable.refresh();
    }
    else
      dsDetailTable.open();
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
      isMasterAdd = false;
      dwdm ="";
      dwmc ="";
      isApprove = false;
      isReport = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      RowMap row = fixedQuery.getSearchRow();//初始化查询项目和内容
      row.clear();
      //row.put("zt","0");
      zt = new String[]{""};
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("kprq$a", startDay);
      row.put("kprq$b", today);
      String SQL = " AND  state<>8 ";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), filialeid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();

      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code!=null&&code.equals("1"))
        submitType=true;
      else
        submitType=false;
    }
  }
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
      if(String.valueOf(REPORT).equals(action))
      {
        isReport=true;
        isApprove = false;
        id=request.getParameter("id");
      }else
      {
        isApprove = true;
        id = data.getParameter("id", "");//得到request的参数,值若为null, 则用""代替
      }
      loanid = id;
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsDetailTable.isOpen())
        dsMasterTable.readyRefresh();
      dsMasterTable.refresh();
      loanid = dsMasterTable.getValue("loanid");//关链
      openDetailTable(false);//打开从表
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
      String content = dsMasterTable.getValue("djh");      //content += "1合同编号2合同编号3合同编号4合同编号5合同编号6合同编号7合同编号8合同编号9合同编号10合同编号11合同编号12合同编号13合同编号14合同编号15合同编号";

      String deptid = dsMasterTable.getValue("deptid");
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "loan_list", content,deptid);
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
      dsMasterTable.setValue("state","8");
      dsMasterTable.saveChanges();
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
      loanid = dsMasterTable.getValue("loanid");
      openDetailTable(false);
      if(dsDetailTable.getRowCount()>0)
      {
        data.setMessage(showJavaScript("alert('不能取消审批!')"));
        return;
      }
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(),"loan_list");
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
      loanid = dsMasterTable.getValue("loanid");
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
      isApprove = false;
      if(String.valueOf(EDIT).equals(action))
      {
        isMasterAdd=false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        loanid = dsMasterTable.getValue("loanid");//关链
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

      String ioflag = rowInfo.get("ioflag");
      String deptid = rowInfo.get("deptid");
      String loandate = rowInfo.get("loandate");
      String retndate = rowInfo.get("retndate");
      String alertdate = rowInfo.get("alertdate");
      String loanfund = rowInfo.get("loanfund");
      String yhid = rowInfo.get("yhid");
      String yhzhid = rowInfo.get("yhzhid");
      String personid = rowInfo.get("personid");
      String loancode = rowInfo.get("loancode");
      String creditor = rowInfo.get("creditor");
      String yearate = rowInfo.get("yearate");

      String retnfund = "";

      if(loandate.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入贷款日期!')"));
        return;
      }
      if(!isDate(loandate))
      {
        data.setMessage(showJavaScript("alert('非法贷款日期!')"));
        return;
      }
      if(retndate.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入还款日期!')"));
        return;
      }
      if(!isDate(retndate))
      {
        data.setMessage(showJavaScript("alert('非法还款日期!')"));
        return;
      }
      if(alertdate.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入报警日期!')"));
        return;
      }
      if(!isDate(alertdate))
      {
        data.setMessage(showJavaScript("alert('非法报警日期!')"));
        return;
      }
      if(personid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择经手人!')"));
        return;
      }
      if(deptid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择提交部门!')"));
        return;
      }
      if(loancode.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入贷款单号!')"));
        return;
      }
      if(yhid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择银行!')"));
        return;
      }
      if(yhzhid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择帐户!')"));
        return;
      }
      if(deptid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择提交部门!')"));
        return;
      }
      if(loanfund.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入贷款金额!')"));
        return;
      }
      if(yearate.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入年利率(%)!')"));
        return;
      }
      double dyearate = Double.parseDouble(yearate);
      if(dyearate<0)
      {
        data.setMessage(showJavaScript("alert('非法年利率!')"));
        return;
      }
      String temp = null;
      if((temp = checkNumber(loanfund, "贷款金额")) != null)
      {
        data.setMessage(temp);
        return;
      }
      double dloanfund = Double.parseDouble(loanfund);
      if(dloanfund<=0)
      {
        data.setMessage(showJavaScript("alert('非法贷款金额!')"));
        return;
      }
      if((temp = checkNumber(yearate, "年利率")) != null)
      {
        data.setMessage(temp);
        return;
      }
      try{
        Date ksdate = new SimpleDateFormat("yyyy-MM-dd").parse(loandate);
        Date jsdate = new SimpleDateFormat("yyyy-MM-dd").parse(alertdate);
        if(ksdate.compareTo(jsdate)>0)
        {
          data.setMessage(showJavaScript("alert('非法报警日期!')"));
          return;
        }
        jsdate = new SimpleDateFormat("yyyy-MM-dd").parse(retndate);
        if(ksdate.compareTo(jsdate)>0)
        {
          data.setMessage(showJavaScript("alert('非法还款日期!')"));
          return;
        }
        }catch(Exception e){}


        if(!isMasterAdd)
          ds.goToInternalRow(masterRow);
        String loanid = null;//得到主表主键值
        if(isMasterAdd){
          ds.insertRow(false);
          loanid = dataSetProvider.getSequence("s_cw_loan");
          ds.setValue("loanid", loanid);
          String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
          ds.setValue("createdate", today);//制单日期
          ds.setValue("creator", loginName);//操作员
          ds.setValue("creatorid", loginId);
          ds.setValue("state", "0");
          ds.setValue("filialeid", filialeid);
          isMasterAdd = false;
          String count = dataSetProvider.getSequence("select count(*) from cw_loan where loancode='"+loancode+"'");
          if(count!=null&&!count.equals("0"))
          {
            data.setMessage(showJavaScript("alert('贷款单号重复!')"));
            return;
          }
          ds.setValue("loancode", loancode);
        }
        ds.setValue("ioflag", ioflag);
        ds.setValue("loandate", loandate);
        ds.setValue("retndate", retndate);
        ds.setValue("alertdate", alertdate);
        ds.setValue("ioflag", "1");
        ds.setValue("loanfund", loanfund);
        //ds.setValue("retnfund", retnfund);
        ds.setValue("yhid", yhid);
        ds.setValue("yhzhid", yhzhid);
        ds.setValue("personid", personid);
        ds.setValue("deptid", deptid);
        ds.setValue("creditor", creditor);
        ds.setValue("summary", rowInfo.get("summary"));
        ds.setValue("yearate", yearate);

        ds.post();
        ds.saveChanges();
        rowInfo.put(ds);
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
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()==0)
      {
        return showJavaScript("alert('从表不能空--没产品等相关信息！');");
      }
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
        return;
      }
      EngineDataSet ds = getMaterTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterTable.getInternalRow();
      ds.goToInternalRow(masterRow);
      loanid = ds.getValue("loanid");
      openDetailTable(false);
      if(dsDetailTable.getRowCount()>0)
      {
        data.setMessage(showJavaScript("alert('不能删除!')"));
        return;
      }
      ds.deleteRow();
      ds.saveChanges();
    }
  }
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
            sbzt= new StringBuffer(" AND state IN(");
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

      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), filialeid, SQL});//组装SQL语句

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
      if(!master.isOpen())
        master.open();
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("djh"), null, null, null, "a", ">="),//开票日期
        new QueryColumn(master.getColumn("djh"), null, null, null, "b", "<="),//开票日期
        new QueryColumn(master.getColumn("loanfund"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("loanfund"), null, null, null, "b", "<="),
        //new QueryColumn(master.getColumn("retnfund"), null, null, null, "a", ">="),
       // new QueryColumn(master.getColumn("retnfund"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("loancode"), null, null, null,null, "like"),
        new QueryColumn(master.getColumn("loandate"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("loandate"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("state"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("stateDesc"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("creator"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("summary"), null, null, null, null, "like")
      });
      isInitQuery = true;
    }
  }
  /**
   *引入提单主从
   * */
  class Detail_Lading_ADD implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());//保存输入的明细信息
      String selectedtdid = m_RowInfo.get("selectedtdid");
      if(selectedtdid.equals(""))
        return;

      engine.project.LookUp invoiceTypeBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_SALE_INVOICE_TYPE);//发票种类
      String fplbid=m_RowInfo.get("fplbid");
      RowMap fplbRow = invoiceTypeBean.getLookupRow(fplbid);
      String zzsl = fplbRow.get("sl");//税率
      double dzzsl = Double.parseDouble(zzsl.equals("")?"17":zzsl);

      if(tdMxTable.isOpen())
        tdMxTable.close();
      setDataSetProperty(tdMxTable,TDMX_SQL+selectedtdid);
      tdMxTable.open();
      if(!isMasterAdd)
      {
        dsMasterTable.goToInternalRow(masterRow);
      }
      tdMxTable.first();
      RowMap tdMXRow=new RowMap(tdMxTable);
      m_RowInfo.put("jsfsid",tdMXRow.get("jsfsid"));
      m_RowInfo.put("dwtxId",tdMXRow.get("dwtxId"));
      m_RowInfo.put("dz",tdMXRow.get("addr"));//地址
      m_RowInfo.put("sh",tdMXRow.get("nsrdjh"));//税号
      m_RowInfo.put("khh",tdMXRow.get("khh"));//开户行
      m_RowInfo.put("zh",tdMXRow.get("zh"));//帐号
      m_RowInfo.put("deptid",tdMXRow.get("deptid"));
      m_RowInfo.put("personid",tdMXRow.get("personid"));
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "tdhwId");//实例化查找数据集的类
      tdMxTable.first();
      for(int i=0;i<tdMxTable.getRowCount();i++)
      {
        if(!isMasterAdd)
        {
        dsMasterTable.goToInternalRow(masterRow);
      }
      String loanid = dsMasterTable.getValue("loanid");
      String tdhwId = tdMxTable.getValue("tdhwId");
      RowMap detailrow = null;
      locateGoodsRow.setValue(0, tdhwId);
      if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("xsfpmxID", "-1");
        dsDetailTable.setValue("loanid", loanid);
        dsDetailTable.setValue("cpid", tdMxTable.getValue("cpid"));
        dsDetailTable.setValue("tdhwId", tdMxTable.getValue("tdhwId"));
        dsDetailTable.setValue("sl", tdMxTable.getValue("sl"));
        dsDetailTable.setValue("hsdj", tdMxTable.getValue("dj"));

        dsDetailTable.setValue("dmsxid", tdMxTable.getValue("dmsxid"));


        dsDetailTable.setValue("zzsl", zzsl);
        double hsdj = Double.parseDouble(tdMxTable.getValue("dj").equals("")?"0":tdMxTable.getValue("dj"));
        //double wsdj = Double.parseDouble(tdMxTable.getValue("dj").equals("")?"0":tdMxTable.getValue("dj"));
        double wsdj = hsdj/(1+dzzsl*0.01);
        double sl = Double.parseDouble(tdMxTable.getValue("sl").equals("")?"0":tdMxTable.getValue("sl"));

        //seObj.value=formatQty(parseFloat(slObj.value) * parseFloat(wsdjObj.value)*parseFloat(zzslObj.value)*0.01);
        double se = sl*wsdj*dzzsl*0.01;
        double jshj = sl*hsdj;

        dsDetailTable.setValue("wsdj", String.valueOf(wsdj));
        dsDetailTable.setValue("se", String.valueOf(se));
        dsDetailTable.setValue("jshj", String.valueOf(jshj));
        dsDetailTable.setValue("je", String.valueOf(sl*wsdj));

        dsDetailTable.post();
        detailrow = new RowMap(dsDetailTable);//创建一个与用户相对应的行
        d_RowInfos.add(detailrow);
      }
      tdMxTable.next();
      }
    }
  }
  /**
   *部门改变
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
   *改变购货单位时引发的操作
   * */
  class Dwtxid_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP);

      String olddwtxId=m_RowInfo.get("dwtxid");
      putDetailInfo(data.getRequest());//保存输入的明细信息
      String dwtxId=m_RowInfo.get("dwtxid");
      RowMap corRow = corpBean.getLookupRow(dwtxId);
      if(olddwtxId.equals(dwtxId))
      {
        return;
      }
      else
      {
        m_RowInfo.put("dwtxId",dwtxId);
        m_RowInfo.put("deptid",req.getParameter("deptid").equals("")?corRow.get("deptid"):req.getParameter("deptid"));
        m_RowInfo.put("personid",req.getParameter("personid").equals("")?corRow.get("personid"):req.getParameter("personid"));

        dsDetailTable.empty();
        d_RowInfos.clear();
      }
    }
  }
  /**
   *
   * 引入提单货物
   *
   * */
  class Detail_Lading_Product_ADD implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String tdhwids = m_RowInfo.get("tdhwids");
      if(tdhwids.length() == 0)
        return;

      engine.project.LookUp invoiceTypeBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_SALE_INVOICE_TYPE);//发票种类
      String fplbid=m_RowInfo.get("fplbid");
      RowMap fplbRow = invoiceTypeBean.getLookupRow(fplbid);
      String zzsl = fplbRow.get("sl");//税率
      double dzzsl = Double.parseDouble(zzsl.equals("")?"17":zzsl);

      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "tdhwid");
      String[] tdhwid = parseString(tdhwids,",");//解析出合同货物ID数组
      Select_LadingBill_Product ladingBill_ProductBean =getsaleOrderBean(req);
      for(int i=0; i < tdhwid.length; i++)
      {
        if(tdhwid[i].equals("-1"))
          continue;
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String loanid = dsMasterTable.getValue("loanid");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, tdhwid[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap saleRow = ladingBill_ProductBean.getLookupRow(tdhwid[i]);

          m_RowInfo.put("jsfsid",saleRow.get("jsfsid"));
          m_RowInfo.put("dwtxId",saleRow.get("dwtxId"));
          m_RowInfo.put("dz",saleRow.get("addr"));//地址
          m_RowInfo.put("sh",saleRow.get("nsrdjh"));//税号
          m_RowInfo.put("khh",saleRow.get("khh"));//开户行
          m_RowInfo.put("zh",saleRow.get("zh"));//帐号
          m_RowInfo.put("deptid",saleRow.get("deptid"));
          m_RowInfo.put("personid",saleRow.get("personid"));
          //String dmsxid = saleRow.get("dmsxid");
          //m_RowInfo.put("dmsxid",saleRow.get("dmsxid"));

          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("xsfpmxID", "-1");
          dsDetailTable.setValue("loanid", loanid);
          String cpid=saleRow.get("cpid");
          dsDetailTable.setValue("cpid", cpid);
          String tdhwId=saleRow.get("tdhwId");
          dsDetailTable.setValue("tdhwId", tdhwId);
          dsDetailTable.setValue("sl", saleRow.get("sl"));

          dsDetailTable.setValue("dmsxid", saleRow.get("dmsxid"));
          dsDetailTable.setValue("hsdj", saleRow.get("dj"));


          dsDetailTable.setValue("zzsl", zzsl);
          double hsdj = Double.parseDouble(saleRow.get("dj").equals("")?"0":saleRow.get("dj"));
          //wsdjObj.value = formatQty(parseFloat(hsdjObj.value))/(parseFloat(zzslObj.value)*0.01+1);
          //double wsdj = Double.parseDouble(saleRow.get("dj").equals("")?"0":saleRow.get("dj"));
          // seObj.value=formatQty(parseFloat(jshjObj.value) - parseFloat(slObj.value) *parseFloat(wsdjObj.value));
          double wsdj = hsdj/(1+Double.parseDouble(zzsl)*0.01);
          double sl = Double.parseDouble(saleRow.get("sl").equals("")?"0":saleRow.get("sl"));
          double se = sl*wsdj*dzzsl*0.01;
          double jshj = sl*hsdj;
          dsDetailTable.setValue("wsdj", String.valueOf(wsdj));
          dsDetailTable.setValue("se", String.valueOf(se));
          dsDetailTable.setValue("jshj", String.valueOf(jshj));
          dsDetailTable.setValue("je", String.valueOf(sl*wsdj));


          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
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
      engine.project.LookUp invoiceTypeBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_SALE_INVOICE_TYPE);//发票种类
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
   *  从表删除操作
   */
  class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getDetailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
    }
  }
  /**
   * 得到用于查找合同编号的bean
   * @param req WEB的请求
   * @return 返回用于查找合同编号的bean
   */
  public Select_Bill_Of_Lading getselect_Bill_Of_LadingBean(HttpServletRequest req)
  {
    if(select_Bill_Of_LadingBean == null)
      select_Bill_Of_LadingBean = Select_Bill_Of_Lading.getInstance(req);
    return select_Bill_Of_LadingBean;
  }
  /**
   * 得到用于查找产品单价的bean
   * @param req WEB的请求
   * @return 返回用于查找产品单价的bean
   */
  public LookUp getSalePriceBean(HttpServletRequest req)
  {
    if(salePriceBean == null)
      salePriceBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_SALE_PRICE);
    return salePriceBean;
  }
  /**
   * 得到用于查找合同编号的bean
   * @param req WEB的请求
   * @return 返回用于查找合同编号的bean
   */
  public Select_LadingBill_Product getsaleOrderBean(HttpServletRequest req)
  {
    if(ladingBill_ProductBean == null)
      ladingBill_ProductBean = Select_LadingBill_Product.getInstance(req);
    return ladingBill_ProductBean;
  }
}
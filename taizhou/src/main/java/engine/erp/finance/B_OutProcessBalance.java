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
 * <p>Title: 采购管理--外加工结算--</p>
 * <p>Description: 采购管理--外加工结算--</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 胡康宁
 * @version 1.0
 */
public final class B_OutProcessBalance extends BaseAction implements Operate
{

  public  static final String SHOW_DETAIL =        "80000001";
  public  static final String DETAIL_SALE_ADD =    "80000002";
  public  static final String TD_RETURN_ADD =      "80000003";//提单退货新增
  public  static final String CANCER_APPROVE =     "80000004";
  public  static final String BALANCE_OVER =       "80000005";//完成
  public  static final String IMPORT_TD =          "80000006";//引入提单主-从表
  public  static final String DWTXID_CHANGE =      "80000007";
  public  static final String MASTER_ADD =         "80000008";
  public  static final String MASTER_EDIT =        "80000009";
  //public  static final String MONTH_CHANGE =       "80000010";
  public  static final String AUTO_CANCER =        "80000011";
  public  static final String MASTER_DETAIL_POST = "80000012";
  public  static final String DEPT_CHANGE  =       "80000013";
  public  static final String REPORT    = "55555343";


  private static final String MASTER_STRUT_SQL = "SELECT * FROM cw_wjgjs WHERE 1<>1 ";
  private static final String MASTER_SQL    = "SELECT * FROM cw_wjgjs WHERE 1=1 AND ? AND fgsid =? ? ORDER BY djh DESC ";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM cw_wjgjshx WHERE 1<>1 ";
  private static final String DETAIL_SQL    = "SELECT * FROM cw_wjgjshx WHERE wjgjsid= ";//
  private static final String TDMX_SQL      ="SELECT * FROM VW_SC_WJG_BALANCE WHERE jgdid= ";//
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM cw_wjgjs WHERE wjgjsid='?'";
  private static final String AUTO_CANCER_SQL = "SELECT * FROM VW_SC_WJG_BALANCE where 1=1 and dwtxid=? and (personid is null or personid=? )";
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet tdMxTable      = new EngineDataSet();//提单明细

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "cw_wjgjs");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "cw_wjgjshx");
  public  boolean isApprove = false;     //是否在审批状态
  private boolean isMasterAdd = true;    //是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private SelectBuyGoods ImportLadingProductBean = null; //进货单货物引用
  private LookUp salePriceBean = null; //销售单价的bean的引用, 用于提取销售单价
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String deptid = null;   //单位
  //private String djxz="" ;       //提单类型
  private String wjgjsid = null;
  private User user = null;
  private int rownum=0;
  public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
  private BigDecimal zje=new BigDecimal(0) ;
  public String []zt;
  private BigDecimal zhxje = new BigDecimal(0);//从表总核销金额
  public boolean isReport = false;
  //public boolean canVerify=false;//外加工结算是否要核销进货产品
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_OutProcessBalance getInstance(HttpServletRequest request)
  {
    B_OutProcessBalance b_OutProcessBalanceBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_OutProcessBalanceBean";
      b_OutProcessBalanceBean = (B_OutProcessBalance)session.getAttribute(beanName);
      if(b_OutProcessBalanceBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        b_OutProcessBalanceBean = new B_OutProcessBalance();
        b_OutProcessBalanceBean.qtyFormat = loginBean.getQtyFormat();
        b_OutProcessBalanceBean.priceFormat = loginBean.getPriceFormat();
        b_OutProcessBalanceBean.sumFormat = loginBean.getSumFormat();
        b_OutProcessBalanceBean.fgsid = loginBean.getFirstDeptID();
        b_OutProcessBalanceBean.deptid = loginBean.getFirstDeptName();
        b_OutProcessBalanceBean.loginId = loginBean.getUserID();
        b_OutProcessBalanceBean.loginName = loginBean.getUserName();
        //设置格式化的字段
        b_OutProcessBalanceBean.user = loginBean.getUser();
        b_OutProcessBalanceBean.dsMasterTable.setColumnFormat("je", b_OutProcessBalanceBean.priceFormat);
        b_OutProcessBalanceBean.dsMasterTable.setColumnFormat("hxje", b_OutProcessBalanceBean.priceFormat);
        b_OutProcessBalanceBean.dsMasterTable.setColumnFormat("whxje", b_OutProcessBalanceBean.priceFormat);  //未核销金额
        b_OutProcessBalanceBean.dsDetailTable.setColumnFormat("jsje", b_OutProcessBalanceBean.sumFormat);
        session.setAttribute(beanName, b_OutProcessBalanceBean);
      }
    }
    return b_OutProcessBalanceBean;
  }
  /**
   * 构造函数
   */
  private B_OutProcessBalance()
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
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"djh"}, new String[]{"SELECT pck_base.billNextCode('cw_wjgjs','djh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"djh"}, new boolean[]{true}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"wjgjshxid"}, new String[]{"S_CW_WJGJSHX"}));
    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    Master_Detail_Post masterDetailPost = new Master_Detail_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());

    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(TD_RETURN_ADD), masterAddEdit);//
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(MASTER_ADD),  masterAddEdit);
    addObactioner(String.valueOf(MASTER_EDIT),  masterAddEdit);

    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(MASTER_DETAIL_POST), masterDetailPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_SALE_ADD), new Detail_SALE_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(IMPORT_TD), new Import_XSTD());
    //&#$//审核部分
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());


    addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());
    addObactioner(String.valueOf(BALANCE_OVER), new Balance_Over());//完成

    addObactioner(String.valueOf(DEPT_CHANGE),  new Dept_Change());
    addObactioner(String.valueOf(AUTO_CANCER),  new Detail_Auto_Cancer());
    addObactioner(String.valueOf(REPORT), new Approve());

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
        String djh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('cw_wjgjs','djh') from dual");
        m_RowInfo.put("djh", djh);
        m_RowInfo.put("czrq", today);//制单日期
        m_RowInfo.put("czy", loginName);//操作员
        m_RowInfo.put("rq", today);
        m_RowInfo.put("czyid", loginId);
        //m_RowInfo.put("djxz", djxz);
        m_RowInfo.put("fgsid", fgsid);
        m_RowInfo.put("zt", "0");
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
      //detailRow.put("tdhwId", rowInfo.get("tdhwId_"+i));//
      detailRow.put("jsje", formatNumber(rowInfo.get("jsje_"+i), sumFormat));//结算金额
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
    String SQL = DETAIL_SQL + (isMasterAdd ? "-1" : wjgjsid);
    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
    {
      dsDetailTable.open();
    }
    else
    {
      dsDetailTable.refresh();
    }
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
  //&#$
   /**
    * 审批操作的触发类
    */
   class Approve implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       String id=null;
       isApprove = true;
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
       String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
       dsMasterTable.setQueryString(sql);
       if(dsDetailTable.isOpen())
         dsMasterTable.readyRefresh();
       dsMasterTable.refresh();
       dsMasterTable.readyRefresh();
       openDetailTable(false);
       initRowInfo(true, false, true);
       initRowInfo(false, false, true);
     }
   }
  /**
   * 完成
   */
  class Balance_Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      wjgjsid = dsMasterTable.getValue("wjgjsid");
      openDetailTable(false);
      if(dsDetailTable.getRowCount()==0)
      {
        data.setMessage(showJavaScript("alert('还没有核销!')"));
        return;
      }
      dsMasterTable.setValue("zt","8");
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
       ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
       approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(),"out_process_balance");
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
       String content = dsMasterTable.getValue("djh");
       String deptid = dsMasterTable.getValue("deptid");
       approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "out_process_balance", content,deptid);
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
      isReport=false;
      zhxje = new BigDecimal(0);//从表总核销金额初始化
      zje=new BigDecimal(0) ;//主表的结算金额
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("rq$a", startDay);
      row.put("rq$b", today);
      //row.put("zt","0");
      zt = new String[]{""};
      isMasterAdd = true;
      String SQL = " AND zt<>8 AND zt<>4 ";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();

      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      ////用于判断submitType ==true 仅制定人可提交,submitType == false 有权限人可提交
      if(code!=null&&code.equals("1"))
        submitType=true;
      else
        submitType=false;
       /*
      code = dataSetProvider.getSequence("select value from systemparam where code='CG_SHOW_VERIFY' ");
      //用于判断是否要核销 canVerify == true要核销
      if(code.equals("1"))
        canVerify=true;
      else
        canVerify=false;
      */
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
      wjgjsid = dsMasterTable.getValue("wjgjsid");
      //打开从表
      openDetailTable(false);
    }
  }
  /**
  * ---------------------------------------------------------
  * 主表添加或修改操作的触发类
  */
 class Master_Add implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     //&#$
      isApprove = false;
      /*
     if(String.valueOf(EDIT).equals(action))
     {
       isMasterAdd=false;
       dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
       masterRow = dsMasterTable.getInternalRow();
       wjgjsid = dsMasterTable.getValue("wjgjsid");
     }
     else{
      */
       isMasterAdd=true;
       //djxz=String.valueOf(ADD).equals(action)?"1":"-1";//1=采购付款,-1=采购退款
       //打开从表
     //}
     synchronized(dsDetailTable){
       openDetailTable(isMasterAdd);
     }
     initRowInfo(true, isMasterAdd, true);
     initRowInfo(false, isMasterAdd, true);
     data.setMessage(showJavaScript("masterAdd();"));
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
     if(String.valueOf(EDIT).equals(action)||String.valueOf(MASTER_EDIT).equals(action))
     {
       isMasterAdd=false;
       rownum = Integer.parseInt(data.getParameter("rownum"));
       dsMasterTable.goToRow(rownum);
       masterRow = dsMasterTable.getInternalRow();
       wjgjsid = dsMasterTable.getValue("wjgjsid");
     }
     else{
       isMasterAdd=true;

       //djxz=String.valueOf(ADD).equals(action)?"-1":"1";//1=销售收款,-1=销售退款
       //打开从表
     }
     synchronized(dsDetailTable){
       openDetailTable(isMasterAdd);
     }
     initRowInfo(true, isMasterAdd, true);
     initRowInfo(false, isMasterAdd, true);
     if(String.valueOf(MASTER_ADD).equals(action)||String.valueOf(MASTER_EDIT).equals(action))
       data.setMessage(showJavaScript("masterAdd();"));
     else
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
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      //得到主表主键值
      String wjgjsid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        wjgjsid = dataSetProvider.getSequence("S_CW_WJGJSHX");
        String djh = rowInfo.get("djh");
        String count = dataSetProvider.getSequence("select count(*) from cw_wjgjs t where t.djh='"+djh+"'");
        if(!count.equals("0"))
          djh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('cw_wjgjs','djh') from dual");
        ds.setValue("djh", djh);//

        ds.setValue("wjgjsid", wjgjsid);//主健
        ds.setValue("zt","0");
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("czyid", loginId);
        ds.setValue("czy", loginName);//操作员
        ds.setValue("fgsid", fgsid);//分公司
      }
      //保存主表数据
      ds.setValue("personid", rowInfo.get("personid"));
      ds.setValue("jsfsid", rowInfo.get("jsfsid"));
      ds.setValue("dwtxId", rowInfo.get("dwtxId"));
      ds.setValue("personid", rowInfo.get("personid"));
      ds.setValue("deptid", rowInfo.get("deptid"));
      //ds.setValue("djxz", rowInfo.get("djxz"));
      //ds.setValue("djh", rowInfo.get("djh"));
      ds.setValue("rq", rowInfo.get("rq"));
      ds.setValue("jsdh", rowInfo.get("jsdh"));

      ds.setValue("zh", rowInfo.get("zh"));
      ds.setValue("yh", rowInfo.get("yh"));
      ds.setValue("czrq", rowInfo.get("czrq"));
      ds.setValue("ztms", rowInfo.get("ztms"));
      ds.setValue("khlx", rowInfo.get("khlx"));


      ds.setValue("cjyf", rowInfo.get("cjyf").equals("")?"0":rowInfo.get("cjyf"));
      ds.setValue("je", rowInfo.get("je"));
      ds.setValue("zfkje",new BigDecimal(rowInfo.get("je")).add(new BigDecimal(rowInfo.get("cjyf").equals("")?"0":rowInfo.get("cjyf"))).toString() );


      //ds.setValue("fgsid", fgsid);
      //ds.setValue("czyID", rowInfo.get("czyID"));
      //ds.setValue("czy", rowInfo.get("czy"));
      ds.setValue("bz", rowInfo.get("bz"));

      ds.post();
      ds.saveChanges();
      //ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);同步保存几个数据集
      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);//重新初始化从表的各行信息
        //detail.empty();
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
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("rq");
      if(temp.equals(""))
        return showJavaScript("alert('日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法日期！');");
      temp = rowInfo.get("dwtxid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择单位！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择部门！');");
      temp = rowInfo.get("jsfsid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择结算方式！');");
      temp = rowInfo.get("personid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择业务员！');");
      temp = rowInfo.get("je");
      if(temp.equals(""))
        return showJavaScript("alert('金额不能为空');");
      temp = checkNumber(temp,"金额");
      if(temp!=null)
        return temp;
      temp = rowInfo.get("je");
      double bd = Double.parseDouble(temp);
      if(bd==0)
        return showJavaScript("alert('金额不能为0');");
      temp = rowInfo.get("cjyf");
      temp = checkNumber(temp,"冲减预付");
      if(temp!=null)
        return temp;
      temp = rowInfo.get("cjyf");
      temp = temp.equals("")?"0":temp;
      double dcjyf = Double.parseDouble(temp);
      if(dcjyf>bd)
        return showJavaScript("alert('冲减预付错误');");
      return null;
    }
  }
  /**
   * 主从表保存
   */
  class Master_Detail_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();
      ds.goToRow(rownum);
      RowMap rowInfo = getMasterRowinfo();
      zje = new BigDecimal(rowInfo.get("je"));
      //校验表单数据
      String temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      double sumjsje=0;
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
        detail.setValue("wjgjsid", wjgjsid);
        double jsje = detailrow.get("jsje").length() > 0 ? Double.parseDouble(detailrow.get("jsje")) : 0;//结算金额
        //double tcl = detailrow.get("tcl").length() > 0 ? Double.parseDouble(detailrow.get("tcl")) : 0;  //提成率        double zk = detailrow.get("zk").length() > 0 ? Double.parseDouble(detailrow.get("zk")) : 0;//折扣
        detail.setValue("jsje", detailrow.get("jsje"));//结算金额
        //detail.setValue("tcl", detailrow.get("tcl"));//提成率
        //detail.setValue("tcj", String.valueOf(jsje * tcl*0.01));//提成奖
        //detail.setValue("yhrq", detailrow.get("yhrq"));//应回日期
        sumjsje +=jsje;
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        detail.next();
      }
      ds.setValue("hxje",formatNumber(String.valueOf(sumjsje),priceFormat));
      ds.setValue("whxje",formatNumber(String.valueOf(Double.parseDouble(rowInfo.get("je"))-sumjsje),priceFormat));
      ds.post();
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      //detail.saveChanges();
      /*

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);//重新初始化从表的各行信息
        detail.empty();
        initRowInfo(false, false, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action))
      */
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
      String temp = null;
      RowMap detailrow = null;
      zhxje = new BigDecimal(0);
      if(d_RowInfos.size()==0)
        return null;//从表可以为空!
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String jsje = detailrow.get("jsje");
        if((temp = checkNumber(jsje, detailProducer.getFieldInfo("jsje").getFieldname())) != null)
          return temp;
        zhxje = zhxje.add(new BigDecimal(jsje));
      }
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
        new QueryColumn(master.getColumn("djh"), null, null, null, null, "="),//提单编号
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//购货单位
        new QueryColumn(master.getColumn("rq"), null, null, null, "a", ">="),//提单日期
        new QueryColumn(master.getColumn("rq"), null, null, null, "b", "<="),//提单日期
        new QueryColumn(master.getColumn("je"), null, null, null, "a", ">="),//
        new QueryColumn(master.getColumn("je"), null, null, null, "b", "<="),//
        new QueryColumn(master.getColumn("djh"), null, null, null, "a", ">="),//
        new QueryColumn(master.getColumn("djh"), null, null, null, "b", "<="),//
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门id
        new QueryColumn(master.getColumn("khlx"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("czy"), null, null, null, null, "like"),
        //new QueryColumn(master.getColumn("djxz"), null, null, null, null, "=")
        //new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
  /**
   *  自动核销
   */
  class Detail_Auto_Cancer implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String dwtxid ="";
      String personid = "";
      String wjgjsid = "";
      double sjje=0;
      if(dsDetailTable.getRowCount()>0)
        dsDetailTable.deleteAllRows();
      dsMasterTable.goToRow(rownum);
      dwtxid = dsMasterTable.getValue("dwtxid");
      personid = dsMasterTable.getValue("personid");
      wjgjsid = dsMasterTable.getValue("wjgjsid");
      if(dwtxid.equals("")||personid.equals(""))
        return;
      sjje = Double.parseDouble(dsMasterTable.getValue("zfkje"));
      String SQL = combineSQL(AUTO_CANCER_SQL,"?",new String[]{dwtxid,personid});
      EngineDataSet dstmp = new EngineDataSet();
      setDataSetProperty(dstmp,SQL);
      dstmp.open();
      double zjsje=0;
      dstmp.first();
      for(int i=0;i<dstmp.getRowCount();i++)
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("wjgjshxid", "-1");
        dsDetailTable.setValue("wjgjsid", wjgjsid);
        dsDetailTable.setValue("cpid", dstmp.getValue("cpid"));
        dsDetailTable.setValue("jgdmxid", dstmp.getValue("jgdmxid"));  //加工单明细ID
        double jsje = dstmp.getValue("wjsje").length() > 0 ? Double.parseDouble(dstmp.getValue("wjsje")) : 0;//结算金额
        dsDetailTable.setValue("jsje", dstmp.getValue("wjsje"));
        zjsje +=jsje;
        if(zjsje>=sjje)
        {
          dsDetailTable.setValue("jsje", String.valueOf(jsje-zjsje+sjje));
          break;
        }
        dsDetailTable.post();
        dstmp.next();
      }
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetailTable.getRowCount());
      else
        d_RowInfos.clear();
      dsDetailTable.first();
      for(int i=0; i<dsDetailTable.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDetailTable);
        d_RowInfos.add(row);
        dsDetailTable.next();
      }
      initRowInfo(false,false,true);
    }
  }
  /**
   *  从表新增操作
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String multiIdInput = m_RowInfo.get("multiIdInput");
      if(multiIdInput.length() == 0)
        return;
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wzdjid");
      String[] wzdjids = parseString(multiIdInput,",");
      B_LadingSelProduct ladingBean = B_LadingSelProduct.getInstance(req);
      for(int i=0; i < wzdjids.length; i++)
      {
        if(wzdjids[i].equals("-1"))
          continue;
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String tdid = dsMasterTable.getValue("tdid");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, wzdjids[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap priceRow = ladingBean.getSelectedRow(wzdjids[i]);
          dsDetailTable.insertRow(false);
          //dsDetailTable.setValue("jgdmxid", "-1");
          //dsDetailTable.setValue("tdid", tdid);
          dsDetailTable.setValue("cpid", priceRow.get("cpid"));
          dsDetailTable.setValue("wzdjid", wzdjids[i]);
          dsDetailTable.setValue("xsj", priceRow.get("xsj"));
          dsDetailTable.setValue("sl", priceRow.get("kchgl"));
          double xsj = priceRow.get("xsj").length() > 0 ? Double.parseDouble(priceRow.get("xsj")) : 0;//销售价
          double kchgl = priceRow.get("kchgl").length() > 0 ? Double.parseDouble(priceRow.get("kchgl")) : 0;  //数量
          dsDetailTable.setValue("xsje", String.valueOf(xsj*kchgl));
          //p
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
    }
  }
  /**
   *传入TDID
   *引入提单主--从表
   * */
  class Import_XSTD implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());//保存输入的明细信息
      String selectedtdid = m_RowInfo.get("selectedtdid");
      if(selectedtdid.equals(""))
        return;
      if(tdMxTable.isOpen())tdMxTable.close();
      setDataSetProperty(tdMxTable,TDMX_SQL+selectedtdid);
      tdMxTable.open();
      if(!isMasterAdd)
      {
      dsMasterTable.goToInternalRow(masterRow);
      }
      tdMxTable.first();
      String dwtxId=tdMxTable.getValue("dwtxId");
      m_RowInfo.put("dwtxId",tdMxTable.getValue("dwtxId"));
      m_RowInfo.put("yh",tdMxTable.getValue("khh"));//开户行
      m_RowInfo.put("zh",tdMxTable.getValue("zh"));//帐号
      m_RowInfo.put("deptid",tdMxTable.getValue("deptid"));
      m_RowInfo.put("personid",tdMxTable.getValue("personid"));
      m_RowInfo.put("khlx",tdMxTable.getValue("khlx"));
      m_RowInfo.put("jsfsid",tdMxTable.getValue("jsfsid"));
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");//实例化查找数据集的类
      tdMxTable.first();
      for(int i=0;i<tdMxTable.getRowCount();i++)
      {
        if(!isMasterAdd)
        {
        dsMasterTable.goToInternalRow(masterRow);
        }
        String wjgjsid = dsMasterTable.getValue("wjgjsid");
        String jgdmxid = tdMxTable.getValue("jgdmxid");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, jgdmxid);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("wjgjshxid", "-1");
          dsDetailTable.setValue("wjgjsid", wjgjsid);
          dsDetailTable.setValue("cpid", tdMxTable.getValue("cpid"));
          dsDetailTable.setValue("jgdmxid", tdMxTable.getValue("jgdmxid"));
          dsDetailTable.setValue("jsje", tdMxTable.getValue("je"));//净金额与实收金额的差额
          dsDetailTable.setValue("tcl", tdMxTable.getValue("hktcl").length()>0?tdMxTable.getValue("hktcl"):"0");
          double jsje = tdMxTable.getValue("je").length() > 0 ? Double.parseDouble(tdMxTable.getValue("je")) : 0;//结算金额
          double tcl = tdMxTable.getValue("hktcl").length() > 0 ? Double.parseDouble(tdMxTable.getValue("hktcl")) : 0;  //提成率        double zk = detailrow.get("zk").length() > 0 ? Double.parseDouble(detailrow.get("zk")) : 0;//折扣
          dsDetailTable.setValue("tcj", String.valueOf(jsje * tcl*0.01));//提成奖
          String yhrq=tdMxTable.getValue("yhrq");
          dsDetailTable.setValue("yhrq", tdMxTable.getValue("yhrq"));
          dsDetailTable.post();
          detailrow = new RowMap(dsDetailTable);//创建一个与用户相对应的行
          d_RowInfos.add(detailrow);
        }
        tdMxTable.next();
      }
    }
    /**
     *根据所传递的tdid获取提单明细
     * */
    public void getDetailCP(String tdid) throws Exception
    {
      tdMxTable.setQueryString(TDMX_SQL+tdid);
      setDataSetProperty(tdMxTable,TDMX_SQL+tdid);
      tdMxTable.open();
    }
      }
  /**
   *
   * 引入提单货物
   * */
  class Detail_SALE_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String tdhwids = m_RowInfo.get("tdhwids");
      if(tdhwids.length() == 0)
        return;
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
      String[] jgdmxid = parseString(tdhwids,",");//解析出合同货物ID数组
      SelectBuyGoods ImportLadingProductBean =getsaleOrderBean(req);
      for(int i=0; i < jgdmxid.length; i++)
      {
        if(jgdmxid[i].equals("-1"))
          continue;
        if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
        String wjgjsid = dsMasterTable.getValue("wjgjsid");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, jgdmxid[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap saleRow = ImportLadingProductBean.getLookupRow(jgdmxid[i]);
          m_RowInfo.put("yh",saleRow.get("khh"));//开户行
          m_RowInfo.put("zh",saleRow.get("zh"));//帐号
          m_RowInfo.put("deptid",saleRow.get("deptid"));
          m_RowInfo.put("personid",saleRow.get("personid"));
          m_RowInfo.put("khlx",saleRow.get("khlx"));
          m_RowInfo.put("jsfsid",saleRow.get("jsfsid"));
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("wjgjshxid", "-1");
          dsDetailTable.setValue("wjgjsid", wjgjsid);
          dsDetailTable.setValue("jgdmxid", jgdmxid[i]);
          dsDetailTable.setValue("cpid", saleRow.get("cpid"));
          dsDetailTable.setValue("jsje", saleRow.get("je"));//净金额与实收金额的差额
          //dsDetailTable.setValue("tcl", saleRow.get("hktcl").length()>0?saleRow.get("hktcl"):"0");

          double jsje = saleRow.get("je").length() > 0 ? Double.parseDouble(saleRow.get("je")) : 0;//结算金额
          //double tcl = saleRow.get("hktcl").length() > 0 ? Double.parseDouble(saleRow.get("hktcl")) : 0;  //提成率        double zk = detailrow.get("zk").length() > 0 ? Double.parseDouble(detailrow.get("zk")) : 0;//折扣
          //dsDetailTable.setValue("tcj", String.valueOf(jsje * tcl*0.01));//提成奖

          //String yhrq=saleRow.get("yhrq");
          //dsDetailTable.setValue("yhrq", saleRow.get("yhrq"));
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
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
      //删除临时数组的一列数据
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
    }
  }

  /**
   *改变部门引发的操作
   * */
  class Dept_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());//保存输入的明细信息
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
      String yh = corRow.get("yh");
      String zh = corRow.get("zh");
        if(olddwtxId.equals(dwtxId))
        {
          return;
        }
        else
        {
          m_RowInfo.put("dwtxId",dwtxId);
          m_RowInfo.put("rq",req.getParameter("rq"));
          m_RowInfo.put("djh",req.getParameter("djh"));
          //m_RowInfo.put("yh",req.getParameter("yh"));
          //m_RowInfo.put("zh",req.getParameter("zh"));
          m_RowInfo.put("deptid",req.getParameter("deptid").equals("")?corRow.get("deptid"):req.getParameter("deptid"));
          m_RowInfo.put("personid",req.getParameter("personid").equals("")?corRow.get("personid"):req.getParameter("personid"));
        }
    }
  }
  /**
   * 得到用于查找合同编号的bean
   * @param req WEB的请求
   * @return 返回用于查找合同编号的bean
   */
  public SelectBuyGoods getsaleOrderBean(HttpServletRequest req)
  {
    if(ImportLadingProductBean == null)
      ImportLadingProductBean = SelectBuyGoods.getInstance(req);
    return ImportLadingProductBean;
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
}
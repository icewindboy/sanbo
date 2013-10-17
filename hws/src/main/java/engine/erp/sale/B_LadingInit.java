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
import engine.common.*;
import java.util.*;
import java.text.*;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 销售管理--销售货物管理--提单管理--</p>
 * <p>Description: 销售管理--销售货物管理--提单管理<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class B_LadingInit extends BaseAction implements Operate
{

  public  static final String SHOW_DETAIL     = "1001";
  public  static final String DETAIL_SALE_ADD = "1002";
  public  static final String TD_RETURN_ADD   = "1003";//提单退货新增
  public  static final String CANCER_APPROVE  = "1004";
  public  static final String LADING_OVER     = "1005";//完成
  public  static final String LADING_CANCER   = "1006";//作废
  public  static final String DWTXID_CHANGE   = "1007";
  public  static final String IMPORT_ORDER    = "1008";//引入合同(主表)
  public  static final String DETAIL_CHANGE   = "1009";//从表操作
  public  static final String DETAIL_PRODUCT_ADD = "1010";//引入客户历史产品
  public  static final String PRODUCT_ADD = "1011";  //引入合同货物
  public  static final String REPORT             ="645355666";
  public  static final String WRAPPER_PRINT             ="1012";
  public  static final String DETAIL_COPY             ="1013";//复制当前选中行
  public  static final String DEL_NULL = "1014";            //删除数量为空的行
  public  static final String LADING_OUT = "1015";//出库确认
  public  static final String LADING_INIT = "1016";//初始化
  public  static final String LADING_INIT_CONTINUE = "1017";//初始化保存新增
  public  static final String LADING_INIT_OVER = "1018";//初始化完成


  private static final String MASTER_STRUT_SQL = "SELECT * FROM xs_td WHERE 1<>1 ORDER BY tdbh DESC ";
  private static final String MASTER_SQL    = "SELECT * FROM xs_td WHERE isinit=1 AND ? AND fgsid=? ? ORDER BY tdbh DESC ";
  private static final String SALEABLE_PRODUCT_SQL = " SELECT * FROM vw_lading_sel_product WHERE cpid=? and (storeid is null or storeid= ? )";//可销产品
  private static final String MASTER_SUM_SQL    = "SELECT SUM(nvl(zsl,0))zsl FROM xs_td WHERE isinit=1 AND ? AND fgsid=? ? ORDER BY tdbh DESC ";
  private static final String MASTER_JE_SQL    = "SELECT SUM(nvl(zje,0))zje FROM xs_td WHERE isinit=1 AND ? AND fgsid=? ? ORDER BY tdbh DESC ";


  private static final String DETAIL_STRUT_SQL = "SELECT * FROM xs_tdhw WHERE 1<>1 ";
  private static final String DETAIL_SQL    = "SELECT * FROM xs_tdhw WHERE tdid= ";//
  public static final String ORDER_DETAIL_SQL ="SELECT * FROM VW_SALE_IMPORT_TD_ORDER_DETAIL WHERE nvl(sl,0)>0 and lb=2 and htid= ";//提货单根据HTID引入相应合同的明细
  public static final String TH_DETAIL_SQL = "SELECT * FROM VW_SALE_IMPORT_TH_ORDER_DETAIL WHERE lb=1 and  htid= ";//退货单引入合同货物
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM xs_td WHERE tdid='?'";
  private static final String CAN_OVER_SQL = "select count(*) from xs_tdhw a where nvl(a.sl,0)>nvl(a.stsl,0) and a.tdid=";//
  private static final String CAN_CANCER_SQL = "select count(*) from xs_tdhw a where nvl(a.stsl,0)>0 and a.tdid=";//

  private static final String REFERENCED_SQL = "select count(*) from xs_tdhw a where nvl(a.stsl,0)>0 and a.tdid=";//

  private static final String PRINT_MASTER_SQL = " SELECT * from VW_SALE_LADING_BILL ";//套打的主表
  private static final String PRINT_DETAIL_SQL = " SELECT * from VW_SALE_LADING_BILL_DETAIL ";//套打的从表

  private static final String SEARCH_SQL = "SELECT * FROM VW_SALE_LADING_PRODUCT WHERE 1=1 ? ";//提货单主从表查询
  private static final String INIT_OVER_FIRST_SQL = "update xs_td t set t.zt=1 where t.isinit=1";//初始化完成
  private static final String INIT_OVER_SECD_SQL = "update xs_td t set t.zt=3 where t.isinit=1";//初始化完成
  private static final String SYS_OVER_SQL = "UPDATE systemparam t SET t.value=1 WHERE t.code='XS_LADINGBILL_INIT_OVER'";//系统参数

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表

  private EngineDataSet dsMaster  = new EngineDataSet();//套打的主表
  private EngineDataSet dsDetail  = new EngineDataSet();//套打的从表

  private EngineDataSet dsSearchTable  = new EngineDataSet();//查询用到的数据集

  private EngineDataSet hthwmxTable      = new EngineDataSet();//合同货物明细


  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "xs_td");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "xs_tdhw");

  public  boolean isApprove = false;     //是否在审批状态
  private boolean isMasterAdd = true;    //是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private RowMap[] masterRows;
  private ArrayList drows=null;

  private B_ImportOrder b_ImportOrderBean = null; //提货单引用销售合同
  private B_ImportOrderToBackLading b_ImportThOrderBean = null; //退货单引用销售合同

  private LookUp salePriceBean = null; //销售单价的bean的引用, 用于提取销售单价
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String djlx="0" ;       //提单类型
  private String isinit="0" ;       //提单初始化数据
  private String tdid = null;
  private User user = null;
  public boolean isReport = false;
  public BigDecimal hkts = new BigDecimal(0);
  public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
  public boolean conversion=false;//销售合同的换算数量与数量是否需要强制转换取

  private static ArrayList keys = new ArrayList();
  private static Hashtable table = new Hashtable();
  public  String syskey="";
  public String []zt;
  public String tCopyNumber = "1";
  public String sys_init = "";
  private String zzsl="";//总数量
  private String zzje="";//总金额
  private String SLSQL="";
  private String JESQL="";
  public String dwdm="";
  public String dwmc="";
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_LadingInit getInstance(HttpServletRequest request)
  {
    B_LadingInit b_LadingInitBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_LadingInitBean";
      b_LadingInitBean = (B_LadingInit)session.getAttribute(beanName);
      if(b_LadingInitBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        b_LadingInitBean = new B_LadingInit();
        b_LadingInitBean.qtyFormat = loginBean.getQtyFormat();
        b_LadingInitBean.priceFormat = loginBean.getPriceFormat();
        b_LadingInitBean.sumFormat = loginBean.getSumFormat();
        b_LadingInitBean.fgsid = loginBean.getFirstDeptID();
        b_LadingInitBean.loginId = loginBean.getUserID();
        b_LadingInitBean.loginName = loginBean.getUserName();
        b_LadingInitBean.user = loginBean.getUser();
        b_LadingInitBean.syskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");
        b_LadingInitBean.sys_init = loginBean.getSystemParam("XS_LADINGBILL_INIT_OVER");

        if(loginBean.getSystemParam("SC_STORE_UNIT_STYLE").equals("1"))
        	b_LadingInitBean.conversion = true;
        //设置格式化的字段
        b_LadingInitBean.dsDetailTable.setColumnFormat("sl", b_LadingInitBean.qtyFormat);
        b_LadingInitBean.dsDetailTable.setColumnFormat("hssl", b_LadingInitBean.qtyFormat);
        b_LadingInitBean.dsDetailTable.setColumnFormat("xsj", b_LadingInitBean.priceFormat);
        b_LadingInitBean.dsDetailTable.setColumnFormat("dj", b_LadingInitBean.priceFormat);
        b_LadingInitBean.dsDetailTable.setColumnFormat("jje", b_LadingInitBean.sumFormat);
        b_LadingInitBean.dsDetailTable.setColumnFormat("zje", b_LadingInitBean.sumFormat);
        b_LadingInitBean.dsDetailTable.setColumnFormat("xsje", b_LadingInitBean.sumFormat);
        b_LadingInitBean.dsDetailTable.setColumnFormat("sthssl", b_LadingInitBean.qtyFormat);
        b_LadingInitBean.dsDetailTable.setColumnFormat("stsl", b_LadingInitBean.qtyFormat);
        session.setAttribute(beanName, b_LadingInitBean);
      }
    }
    return b_LadingInitBean;
  }
  /**
   * 构造函数
   */
  private B_LadingInit()
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
    setDataSetProperty(dsMaster, PRINT_MASTER_SQL+" WHERE 1<>1 ");
    setDataSetProperty(dsDetail, PRINT_DETAIL_SQL+" WHERE 1<>1 ");
    setDataSetProperty(dsSearchTable, combineSQL(SEARCH_SQL,"?",new String[]{""}));

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"tdbh"}, new String[]{"SELECT pck_base.billNextCode('xs_td','tdbh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"tdbh"}, new boolean[]{true}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"tdhwid"}, new String[]{"s_xs_tdhw"}));
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
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_SALE_ADD), new Detail_SALE_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(IMPORT_ORDER), new Import_Total_Order());//引入整个合同
    //&#$//审核部分
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(REPORT), new Approve());

    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());
    addObactioner(String.valueOf(DETAIL_CHANGE), new Detail_Change());
    addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());
    addObactioner(String.valueOf(LADING_INIT_OVER), new Lading_Init_Over());//初始化完成
    addObactioner(String.valueOf(DETAIL_PRODUCT_ADD), new Detail_History_Product_Add());
    addObactioner(String.valueOf(DEPT_CHANGE), new Dept_Change());
    addObactioner(String.valueOf(PRODUCT_ADD), new Multi_Product_Add());
    addObactioner(String.valueOf(WRAPPER_PRINT), new Wrapper_Print());//套打
    addObactioner(String.valueOf(DETAIL_COPY), new Detail_Copy_Add());//DETAIL_COPY
    addObactioner(String.valueOf(DEL_NULL), new Detail_Delete_Null());//Detail_Delete_Null
    addObactioner(String.valueOf(LADING_OUT), new Lading_Out());
    addObactioner(String.valueOf(LADING_INIT), masterAddEdit);//LADING_INIT初始化新增
    addObactioner(String.valueOf(LADING_INIT_CONTINUE), masterPost);//LADING_INIT_CONTINUE

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
    //Returns the runtime class of an object.
    //That Class object is the object that is locked by static synchronized methods of the represented class.
    return getClass();//Object类的方法
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
        String tdbh="";
        m_RowInfo.put("czrq", today);//制单日期
        m_RowInfo.put("czy", loginName);//操作员
        m_RowInfo.put("jhrq", today);
        m_RowInfo.put("tdrq", today);
        m_RowInfo.put("czyid", loginId);
        m_RowInfo.put("djlx", djlx);
        m_RowInfo.put("zt", "0");
        m_RowInfo.put("isinit", isinit);
        if(djlx.equals("1"))
          tdbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_td','tdbh') from dual");
        else
          tdbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_td','tdbh','t') from dual");
        m_RowInfo.put("tdbh", tdbh);
        //m_RowInfo.put("khlx", "A");
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
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));
      detailRow.put("wzdjid", rowInfo.get("wzdjid_"+i));//
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
      detailRow.put("hssl", rowInfo.get("hssl_"+i));//
      detailRow.put("xsje", formatNumber(rowInfo.get("xsje_"+i), priceFormat));//销售金额
      detailRow.put("jje", formatNumber(rowInfo.get("jje_"+i), sumFormat));//净金额
      detailRow.put("xsj", formatNumber(rowInfo.get("xsj_"+i), priceFormat));//销售价
      detailRow.put("zk", rowInfo.get("zk_"+i));//折扣
      detailRow.put("dj", rowInfo.get("dj_"+i));//单价
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
      detailRow.put("hthwid", rowInfo.get("hthwid_"+i));
      detailRow.put("hltcl", rowInfo.get("hltcl_"+i));
      //detailRow.put("sthssl", rowInfo.get("sthssl_"+i));//-实提换算数量
      //detailRow.put("stsl", rowInfo.get("stsl_"+i));//实提数量
      //保存用户自定义的字段
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
  }
      /*打印主表**/
  public final RowMap[] getMasterRows()
  {
    return masterRows;
  }
      /*打印从表**/
  public final  RowMap[] getDetailRows()
  {
    RowMap[] rows = new RowMap[drows.size()];
    drows.toArray(rows);
    return rows;
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
      /*得到总数量*/
  public final String getZsl()
  {
    return zzsl;
  }
      /*得到总金额*/
  public final String getZje()
  {
    return zzje;
  }
      /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    //tdid = dsMasterTable.getValue("tdid");//关链
    String SQL = DETAIL_SQL + (isMasterAdd ? "-1" : tdid);
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
  /**
   * 是否可以完成
   * 提单数不大于出库数量时,才可以完成
   * 只要有一笔提单货物没完全出库,就不能完成
   * @param htid
   * @return
   */
  public boolean isCanOver(String tdid)
  {
    if(tdid.equals(""))
      return false;
    String count="";
    try
    {
      count = dataSetProvider.getSequence(CAN_OVER_SQL+tdid);
    }
    catch(Exception e){}
    if(!count.equals("0"))
      return false;
    else
      return true;
  }
  /**
   *在实提数量大于0的情况下,提货单不能取消审批
   * */
  public boolean isCanCancer(String tdid)
  {
    if(tdid.equals(""))
      return false;
    String count="";
    try
    {
      count = dataSetProvider.getSequence(CAN_CANCER_SQL+tdid);
    }
    catch(Exception e){}
    if(!count.equals("0"))
      return false;
    else
      return true;
  }
  /**
   *
   * @param htid
   * @return
   */
  public boolean hasReferenced(String tdid)
  {
    if(tdid.equals(""))
      return false;
    String count="";
    try
    {
      count = dataSetProvider.getSequence(REFERENCED_SQL+tdid);
    }
    catch(Exception e){}
    if(!count.equals("0"))
      return true;
    else
      return false;
  }
  //&#$
  /**
   * 审批操作的触发类
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isMasterAdd=false;
      String id=null;
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
        id = data.getParameter("id", "");
      }
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsDetailTable.isOpen())
        dsMasterTable.readyRefresh();
      dsMasterTable.refresh();
      //打开从表
      tdid = dsMasterTable.getValue("tdid");
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
      String content = dsMasterTable.getValue("tdbh");
      String djlx = dsMasterTable.getValue("djlx");
      String deptid = dsMasterTable.getValue("deptid");
      if(djlx.equals("1"))
        approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "lading_bill", content,deptid);
      else
        approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "unlading_bill", content,deptid);
    }
  }
  /**
   * 取消审批
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String djlx = dsMasterTable.getValue("djlx");
      if(djlx.equals("1"))
        approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(),"lading_bill");
      else
        approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(),"unlading_bill");
    }
  }
  /**
   * 2004-4-5
   * 状态2为已全部出库
   * sl>stsl时不能确认已全部出库
   * */
  class Lading_Out implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterTable.getInternalRow();
      tdid = dsMasterTable.getValue("tdid");
      openDetailTable(false);
      dsDetailTable.first();
      //String lx = dsMasterTable.getValue("djlx");
      for(int i=0;i<dsDetailTable.getRowCount();i++)
      {
        String sl = dsDetailTable.getValue("sl");
        String stsl = dsDetailTable.getValue("stsl");//出库回填
        if((Math.abs(Double.parseDouble(sl.equals("")?"0":sl)))>(Math.abs(Double.parseDouble(stsl.equals("")?"0":stsl))))
        {
          ///data.setMessage(showJavaScript(lx.equals("1")?"alert('还有货物没出库!')":"alert('还有款项未结清,不能完成!')"));
          data.setMessage(showJavaScript("alert('还有货物没出库!')"));
          return;
        }
        dsDetailTable.next();
      }
      dsMasterTable.setValue("zt","2");
      dsMasterTable.saveChanges();
    }
  }
  /**
   * 完成初始化
   * 完成
   */
  class Lading_Init_Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.updateQuery(new String[]{INIT_OVER_FIRST_SQL,INIT_OVER_SECD_SQL,SYS_OVER_SQL});
      dsMasterTable.refresh();

      //LoginBean loginBean = LoginBean.getInstance(data.getRequest());
      //sys_init = loginBean.getSystemParam("XS_LADINGBILL_INIT_OVER");

      sys_init = dataSetProvider.getSequence("select t.VALUE from systemparam t WHERE t.code='XS_LADINGBILL_INIT_OVER'");

      initRowInfo(true,false,false);
    }
  }
  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dwdm ="";
      dwmc ="";
      //&#$
      isApprove = false;
      isReport = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      tCopyNumber = "1";
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("tdrq$a", startDay);
      row.put("tdrq$b", today);
      //row.put("zt","0");
      zt = new String[]{""};
      isMasterAdd = true;
      String SQL = " AND zt<>8  ";
      String MSQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsMasterTable.setQueryString(MSQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      isReport=false;

      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code!=null&&code.equals("1"))
        submitType=true;
      else
        submitType=false;

      sys_init = dataSetProvider.getSequence("select value from systemparam where code='XS_LADINGBILL_INIT_OVER' ");

      SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      JESQL = combineSQL(MASTER_JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;

      zzsl = formatNumber(zzsl, priceFormat);
      zzje = formatNumber(zzje, priceFormat);
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
      tdid = dsMasterTable.getValue("tdid");
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
        tdid = dsMasterTable.getValue("tdid");
        djlx= dsMasterTable.getValue("djlx");
        isinit= dsMasterTable.getValue("isinit");
      }
      else{
        isMasterAdd=true;
        djlx=String.valueOf(ADD).equals(action)?"1":"-1";//1:新增提货单,-1新增退货单
        djlx = String.valueOf(LADING_INIT).equals(action)?"1":djlx;//LADING_INIT提单初始化为提货单
        isinit = String.valueOf(LADING_INIT).equals(action)?"1":"0";
        //打开从表
        if(djlx.equals("-1"))
        {
          dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"tdbh"}, new String[]{"SELECT pck_base.billNextCode('xs_td','tdbh','t') from dual"}));
        }
        else
          dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"tdbh"}, new String[]{"SELECT pck_base.billNextCode('xs_td','tdbh') from dual"}));
      }
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      if(String.valueOf(LADING_INIT).equals(action)||isinit.equals("1"))
        data.setMessage(showJavaScript("toInitTD();"));
      else
      {
        if(djlx.equals("1"))
          data.setMessage(showJavaScript("toTihd();"));
        else
          data.setMessage(showJavaScript("toTweihd();"));
      }
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
      //String Id = null;
      if(isMasterAdd){
        String tdbh = rowInfo.get("tdbh");
        String count = dataSetProvider.getSequence("select count(*) from xs_td t where t.tdbh='"+tdbh+"'");
        if(!count.equals("0"))
        {
          if(djlx.equals("1"))
            tdbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_td','tdbh') from dual");
          else
            tdbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_td','tdbh','t') from dual");
        }
        ds.insertRow(false);
        tdid = dataSetProvider.getSequence("s_xs_td");
        //log.debug("hk:"+tdid);
        ds.setValue("tdid", tdid);//主健
        ds.setValue("zt","0");
        ds.setValue("tdbh",tdbh);
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("czyid", loginId);
        ds.setValue("czy", loginName);//操作员
        ds.setValue("fgsid", fgsid);//分公司
        ds.setValue("isinit", isinit);
      }
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0),totalZje=new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      double zsl=0.0;
      double zje = 0;
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        detail.setValue("tdid", tdid);
        detail.setValue("cpid", detailrow.get("cpid"));//?
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        double xsj = detailrow.get("xsj").length() > 0 ? Double.parseDouble(detailrow.get("xsj")) : 0;//销售价
        double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;  //数量
        double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(detailrow.get("dj")) : 0;//单价
        //double zk = detailrow.get("zk").length() > 0 ? Double.parseDouble(detailrow.get("zk")) : 0;//折扣
        double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;
        double xsje = detailrow.get("xsje").length() > 0 ? Double.parseDouble(detailrow.get("xsje")) : 0;
        double jje = detailrow.get("jje").length() > 0 ? Double.parseDouble(detailrow.get("jje")) : 0;
        zje =zje+jje;
        zsl=zsl+sl;
        detail.setValue("sl", detailrow.get("sl"));//数量
        detail.setValue("xsj", detailrow.get("xsj"));//销售价
        detail.setValue("xsje", String.valueOf(sl * xsj));//销售金额
        detail.setValue("zk", detailrow.get("zk"));//折扣
        detail.setValue("dj", detailrow.get("dj"));//单价
        detail.setValue("hssl", detailrow.get("hssl"));//
        detail.setValue("wzdjid", detailrow.get("wzdjid"));  //单价?
        detail.setValue("jje", detailrow.get("jje"));//净金额
        detail.setValue("bz", detailrow.get("bz"));//备注
        detail.setValue("hltcl", detailrow.get("hltcl"));
        if(rowInfo.get("djlx").equals("-1"))
        {
          detail.setValue("sl", String.valueOf(-sl));//数量
          detail.setValue("hssl", String.valueOf(-hssl));//
          detail.setValue("xsje", String.valueOf(-sl * xsj));//销售金额
          detail.setValue("jje", String.valueOf(-jje));//净金额
        }
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        totalSum = totalSum.add(detail.getBigDecimal("xsje"));
        totalZje =totalZje.add(detail.getBigDecimal("jje"));
        detail.next();
      }
      //保存主表数据
      ds.setValue("tdrq", rowInfo.get("tdrq"));//提单日期
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("jsfsid", rowInfo.get("jsfsid"));//结算方式ID
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));//购货单位ID
      ds.setValue("personid", rowInfo.get("personid"));//人员ID
      ds.setValue("zsl", String.valueOf(zsl));//总数量
      ds.setValue("dwt_dwtxId", rowInfo.get("dwt_dwtxId"));//承运单位ID
      //ds.setValue("czyID", rowInfo.get("czyID"));//操作员ID
      ds.setValue("ddfy", rowInfo.get("ddfy"));//代垫费用
      ds.setValue("yf", rowInfo.get("yf"));//运费
      ds.setValue("ztms", rowInfo.get("ztms"));//状态描述
      ds.setValue("storeid", rowInfo.get("storeid"));//仓库id
      ds.setValue("hkrq", rowInfo.get("hkrq"));//回款日期
      ds.setValue("hkts", rowInfo.get("hkts"));//回款天数
      ds.setValue("djlx", djlx);//单据类型
      ds.setValue("sendmodeid", rowInfo.get("sendmodeid"));//sendmodeid
      ds.setValue("dz", rowInfo.get("dz"));
      ds.setValue("yfdj", rowInfo.get("yfdj"));
      ds.setValue("lxr", rowInfo.get("lxr"));
      ds.setValue("dh", rowInfo.get("dh"));
      if(rowInfo.get("djlx").equals("-1"))
      {
        ds.setValue("zsl", String.valueOf(-zsl));
        ds.setValue("zje", String.valueOf(-zje));//总金额
      }
      else
      {
        ds.setValue("zje", String.valueOf(zje));//总金额
      }
      ds.setValue("khlx", rowInfo.get("khlx"));//客户类型
      ds.setValue("fgsid", fgsid);//分公司
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      //String id = ds.getValue("tdid");
      //log.debug("hhhhhhhhhhhhhhhhhhhhhhhhhhh:"+id);

      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);

      //合计相关
      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;

      zzsl = formatNumber(zzsl, priceFormat);
      zzje = formatNumber(zzje, priceFormat);

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        djlx=rowInfo.get("djlx");
        initRowInfo(true, true, true);//重新初始化从表的各行信息
        detail.empty();
        initRowInfo(false, false, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(LADING_INIT_CONTINUE).equals(action)){
        isMasterAdd = true;
        isinit = "1";
        djlx=rowInfo.get("djlx");
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
      HashSet htmp = new HashSet();
      htmp.clear();
      //HashSet wzdijds=new HashSet(d_RowInfos.size());
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()==0)
      {
        return showJavaScript("alert('从表不能空--没产品等相关信息！');");
      }
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String cpid="c"+detailrow.get("cpid");
        String dmsxid="d"+detailrow.get("dmsxid").trim();
        if(!htmp.add(cpid+dmsxid))
          return showJavaScript("alert('所选第"+(i+1)+"行货物重复!');");
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, detailProducer.getFieldInfo("sl").getFieldname())) != null)
          return temp;
        String hssl = detailrow.get("hssl");
        if(!hssl.equals(""))
        {
          if((temp = checkNumber(hssl, detailProducer.getFieldInfo("hssl").getFieldname())) != null)
            return temp;
        }
        String xsj = detailrow.get("xsj");
        if((temp = checkNumber(xsj, detailProducer.getFieldInfo("xsj").getFieldname())) != null)
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
      String temp = rowInfo.get("tdrq");
      if(temp.equals(""))
        return showJavaScript("alert('提单日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法提单日期！');");
      temp = rowInfo.get("dwtxid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择购货单位！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择部门！');");
      temp = rowInfo.get("personid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择业务员！');");
      temp = rowInfo.get("jsfsid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择结算方式！');");
      temp = rowInfo.get("storeid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择仓库！');");
      temp = rowInfo.get("khlx");
      if(temp.equals(""))
        return showJavaScript("alert('请选择客户类型！');");
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


      //合计相关
      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;

      zzsl = formatNumber(zzsl, priceFormat);
      zzje = formatNumber(zzje, priceFormat);

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
      HttpServletRequest request = data.getRequest();
      dwdm =request.getParameter("dwdm");
      dwmc =request.getParameter("dwmc");

      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;

          /*

          SQL=SQL+" AND "+user.getHandleDeptWhereValue("deptid", "czyid")+" AND isinit=1 ";
          SQL = combineSQL(SEARCH_SQL, "?", new String[]{SQL});
          if(!dsSearchTable.getQueryString().equals(SQL))
          {
            dsSearchTable.setQueryString(SQL);
            dsSearchTable.setRowMax(null);
          }
          dsSearchTable.refresh();
          StringBuffer sb = new StringBuffer();
          ArrayList al = new ArrayList();
          String tdids="";
          dsSearchTable.first();
          for(int i=0;i<dsSearchTable.getRowCount();i++)
          {
            String tdid = dsSearchTable.getValue("tdid");
            if(tdid!=null&&!tdid.equals(""))
              if(!al.contains(tdid))
                al.add(tdid);
            dsSearchTable.next();
          }
          if(al.size()==0)
            tdids="0";
          else
          {
            for(int j=0;j<al.size();j++)
              sb.append(al.get(j)+",");
            tdids=sb.append("0").toString();
          }
          String alltd = sb.toString();
          SQL = alltd.equals("")? " and tdid IN(-1)":" and tdid IN("+alltd+")";
          */
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

      String MSQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});//组装SQL语句
      if(!dsMasterTable.getQueryString().equals(MSQL))
      {
        dsMasterTable.setQueryString(MSQL);
        dsMasterTable.setRowMax(null);
      }
      SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      JESQL = combineSQL(MASTER_JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;

      zzsl = formatNumber(zzsl, priceFormat);
      zzje = formatNumber(zzje, priceFormat);
    }
    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsSearchTable;
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("tdbh"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("tdbh"), null, null, null, "b", "<="),//提单编号
        new QueryColumn(master.getColumn("tdrq"), null, null, null, "a", ">="),//提单日期
        new QueryColumn(master.getColumn("tdrq"), null, null, null, "b", "<="),//提单日期
        new QueryColumn(master.getColumn("hkrq"), null, null, null, "a", ">="),//
        new QueryColumn(master.getColumn("hkrq"), null, null, null, "b", "<="),//
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门id
        new QueryColumn(master.getColumn("djlx"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("khlx"), null, null, null, null, "="),
        //new QueryColumn(master.getColumn("dwdm"), null, null, null, null, "="),
        // new QueryColumn(master.getColumn("dwmc"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("czy"), null, null, null, null, "like")
      });
      isInitQuery = true;
    }
  }
  /**
   *  从表新增
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      dsDetailTable.insertRow(false);
      dsDetailTable.setValue("tdhwid", "-1");
      dsDetailTable.setValue("tdid", tdid);
      dsDetailTable.post();
      RowMap detailrow = new RowMap(dsDetailTable);
      d_RowInfos.add(detailrow);

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
      tCopyNumber = req.getParameter("tCopyNumber");
      int row = Integer.parseInt(rownum);
      int size = d_RowInfos.size();
      if(row>size)
        return;
      RowMap newadd = (RowMap)d_RowInfos.get(row);
      String cpid = newadd.get("cpid");
      String wzdjid = newadd.get("wzdjid");
      String xsj = newadd.get("xsj");
      String hthwid = newadd.get("hthwid");
      int copynumber = Integer.parseInt(tCopyNumber);

        /*
        newadd.put("dmsxid","");
        newadd.put("sl","");
        newadd.put("hssl","");
        newadd.put("xsje","");
        newadd.put("jje","");
        newadd.put("zk","");
        newadd.put("dj","");
      //d_RowInfos.add(newadd);
        */

      for(int i=0;i<copynumber;i++)
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("tdhwid", "-1");
        dsDetailTable.setValue("tdid", tdid);
        dsDetailTable.setValue("cpid", cpid);
        dsDetailTable.setValue("wzdjid", wzdjid);
        dsDetailTable.setValue("xsj", xsj);
        dsDetailTable.setValue("hthwid", hthwid);
        dsDetailTable.post();
        RowMap detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
      }
      tCopyNumber="1";
    }
  }
  /**
   *客户历史记录产品
   *
   * **/
  class Detail_History_Product_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      engine.project.LookUp salePriceBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_SALE_PRICE);
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String importwzdjids = m_RowInfo.get("multiIdInput");
      String djlx =  m_RowInfo.get("djlx");
      if(importwzdjids.length() == 0)
        return;
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wzdjid");
      String[] wzdjids = parseString(importwzdjids,",");//解析出合同货物ID数组
      engine.erp.sale.B_CustProdHistorySelect custProdHisBean = engine.erp.sale.B_CustProdHistorySelect.getInstance(req);
      for(int i=0; i < wzdjids.length; i++)
      {
        if(wzdjids[i].equals("-1"))
          continue;
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String tdid = dsMasterTable.getValue("tdid");
        RowMap detailrow = null;
        String wzdjid = wzdjids[i];
        locateGoodsRow.setValue(0, wzdjids[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap saleRow = custProdHisBean.getSelectRow(wzdjids[i]);
          RowMap wzRow=salePriceBean.getLookupRow(wzdjids[i]);
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("tdhwid", "-1");
          dsDetailTable.setValue("tdid", tdid);
          //dsDetailTable.setValue("tdid", tdid);
          dsDetailTable.setValue("wzdjid", wzdjids[i]);
          dsDetailTable.setValue("cpid", saleRow.get("cpid"));
          dsDetailTable.setValue("dmsxid", saleRow.get("dmsxid"));
          dsDetailTable.setValue("sl", "");//数量

          double dj=Double.parseDouble(saleRow.get("dj").equals("")?"0":saleRow.get("dj"));//历史单价
          String xsj=saleRow.get("xsj").equals("")?saleRow.get("xsjzj"):saleRow.get("xsj");
          double dxsj = Double.parseDouble(xsj.equals("")?"0":xsj);
          if(dxsj==0)
          {
            data.setMessage(showJavaScript("alert('引入销售价错误!')"));
            return;
          }
          double zk=dj/dxsj*100;

          dsDetailTable.setValue("xsj", xsj);//销售价
          dsDetailTable.setValue("xsje", "0");//销售金额
          dsDetailTable.setValue("zk", engine.util.Format.formatNumber(zk,"00"));//折扣
          dsDetailTable.setValue("dj", String.valueOf(dj));//单价
          dsDetailTable.setValue("hssl", "");//
          dsDetailTable.setValue("jje", "");//净金额
          dsDetailTable.setValue("bz", "");//备注
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
    }
  }

  /**
   *  从表更新
   */
  class Detail_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
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
   * 暂没用上
   *引入销售合同
   *引入销售合同主表及从表信息
   * */
  class Import_Total_Order implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());//保存输入的明细信息
      String selectedhtid = m_RowInfo.get("selectedhtid");
      String djlx = m_RowInfo.get("djlx");
      String storeid = m_RowInfo.get("storeid");
      if(selectedhtid.equals("")||storeid.equals(""))
        return;
      if(hthwmxTable.isOpen())hthwmxTable.close();
      String sql=ORDER_DETAIL_SQL+selectedhtid+" AND (storeid="+storeid+" OR storeid is null)";//提货单
      if(djlx.equals("-1"))
        sql=TH_DETAIL_SQL+selectedhtid+" AND (storeid="+storeid+" OR storeid is null)";//退货单
      setDataSetProperty(hthwmxTable,ORDER_DETAIL_SQL+selectedhtid+" AND (storeid="+storeid+" OR storeid is null)");
      hthwmxTable.open();
      if(!isMasterAdd)
      {
        dsMasterTable.goToInternalRow(masterRow);
      }
      hthwmxTable.first();
      engine.project.LookUp creditBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP_CREDIT);
      creditBean.regData(new String[]{hthwmxTable.getValue("dwtxId")});
      RowMap creditRow = creditBean.getLookupRow(hthwmxTable.getValue("dwtxId"));

      String dw = hthwmxTable.getValue("dwtxId");
      String ps = hthwmxTable.getValue("personid");
      String det = hthwmxTable.getValue("deptid");
      String hk = hthwmxTable.getValue("khlx");
      String hs = creditRow.get("hkts");

      m_RowInfo.put("dwtxId",hthwmxTable.getValue("dwtxId"));//单位ID
      m_RowInfo.put("personid",hthwmxTable.getValue("personid"));//业务员
      m_RowInfo.put("deptid",hthwmxTable.getValue("deptid"));//部门
      m_RowInfo.put("khlx",hthwmxTable.getValue("khlx"));//客户类型
      m_RowInfo.put("hkts",creditRow.get("hkts"));

      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwid");

      hthwmxTable.first();
      for(int i=0;i<hthwmxTable.getRowCount();i++)
      {
        if(!isMasterAdd)
        {
        dsMasterTable.goToInternalRow(masterRow);
      }
      String tdid = dsMasterTable.getValue("tdid");
      String hthwid=hthwmxTable.getValue("hthwid");//合同货物ID
      locateGoodsRow.setValue(0, hthwid);
      if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("tdhwid", "-1");
        dsDetailTable.setValue("tdid", tdid);
        dsDetailTable.setValue("dmsxid", hthwmxTable.getValue("dmsxid"));
        dsDetailTable.setValue("wzdjid", hthwmxTable.getValue("wzdjid"));
        dsDetailTable.setValue("hthwid", hthwmxTable.getValue("hthwid"));
        dsDetailTable.setValue("cpid", hthwmxTable.getValue("cpid"));

        double sl=Double.parseDouble(hthwmxTable.getValue("sl").equals("")?"0":hthwmxTable.getValue("sl"));
        double hssl = Double.parseDouble(hthwmxTable.getValue("hssl").equals("")?"0":hthwmxTable.getValue("hssl"));
        double xsj = Double.parseDouble(hthwmxTable.getValue("xsj").equals("")?"0":hthwmxTable.getValue("xsj"));
        double dj = Double.parseDouble(hthwmxTable.getValue("dj").equals("")?"0":hthwmxTable.getValue("dj"));
        double xsje = 0;
        double jje = 0;
        xsje=sl*xsj;
        jje=sl*dj;
        if(djlx.equals("-1"))
        {
          xsje=-xsje;
          jje=-jje;
          hssl=-hssl;
        }
        dsDetailTable.setValue("sl", djlx.equals("-1")?(String.valueOf(-sl)):hthwmxTable.getValue("sl"));

        dsDetailTable.setValue("hssl", String.valueOf(hssl));
        dsDetailTable.setValue("xsj", hthwmxTable.getValue("xsj"));
        dsDetailTable.setValue("xsje", String.valueOf(xsje));
        dsDetailTable.setValue("zk", hthwmxTable.getValue("zk"));
        dsDetailTable.setValue("dj", hthwmxTable.getValue("dj"));
        dsDetailTable.setValue("jje", String.valueOf(jje));
        dsDetailTable.setValue("bz", hthwmxTable.getValue("bz"));
        //dsDetailTable.setValue("sthssl", hthwmxTable.getValue("sthssl"));
        //dsDetailTable.setValue("stsl", hthwmxTable.getValue("stsl"));
        dsDetailTable.post();
        RowMap detailrow = new RowMap(dsDetailTable);//创建一个与用户相对应的行
        d_RowInfos.add(detailrow);
      }
      hthwmxTable.next();
      }
    }
  }
  /**
   * 引入合同
   * 对应页面引入合同操作
   * 一次只能引入一张合同的货物
   * 引入销售合同货物
   * */
  class Detail_SALE_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String importOrder = m_RowInfo.get("importOrder");
      String djlx =  m_RowInfo.get("djlx");

      String dwtxid =  m_RowInfo.get("dwtxid");
      String jsfsid =  m_RowInfo.get("jsfsid");
      String deptid =  m_RowInfo.get("deptid");
      String personid =  m_RowInfo.get("personid");
      String khlx =  m_RowInfo.get("khlx");
      String sendmodeid =  m_RowInfo.get("sendmodeid");

      if(importOrder.length() == 0)
        return;
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwID");
      String[] hthwIDs = parseString(importOrder,",");//解析出合同货物ID数组
      b_ImportOrderBean =getb_ImportOrderBean(req);     //提货
      b_ImportThOrderBean =getb_ImportThOrderBean(req); //退货
      BigDecimal bd = new BigDecimal(0);
      for(int i=0; i < hthwIDs.length; i++)
      {
        if(hthwIDs[i].equals("-1"))
          continue;
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String tdid = dsMasterTable.getValue("tdid");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, hthwIDs[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap orderRow =null;
          if(djlx.equals("1"))
            orderRow = b_ImportOrderBean.getLookupRow(hthwIDs[i]);//提货
          else
            orderRow = b_ImportThOrderBean.getLookupRow(hthwIDs[i]);//退货
          if(dwtxid.equals(""))
          {
            m_RowInfo.put("dwtxid",orderRow.get("dwtxid"));
          }
          if(jsfsid.equals(""))
          {
            m_RowInfo.put("jsfsid",orderRow.get("jsfsid"));
          }
          if(deptid.equals(""))
          {
            m_RowInfo.put("deptid",orderRow.get("deptid"));
          }
          if(sendmodeid.equals(""))
          {
            m_RowInfo.put("sendmodeid",orderRow.get("sendmodeid"));
          }
          if(personid.equals(""))
          {
            m_RowInfo.put("personid",orderRow.get("personid"));
          }
          if(khlx.equals(""))
          {
            m_RowInfo.put("khlx",orderRow.get("khlx"));
          }
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("tdhwid", "-1");
          dsDetailTable.setValue("tdid", tdid);
          dsDetailTable.setValue("cpid", orderRow.get("cpid"));
          dsDetailTable.setValue("hthwid", hthwIDs[i]);
          dsDetailTable.setValue("wzdjid", orderRow.get("wzdjid"));
          String dmsxid = orderRow.get("dmsxid");
          dsDetailTable.setValue("dmsxid", dmsxid);

          String hlts = orderRow.get("hlts").equals("")?"0":orderRow.get("hlts");
          bd=bd.add(new BigDecimal(hlts));

          double sl=Double.parseDouble(orderRow.get("sl").equals("")?"0":orderRow.get("sl"));
          //double stsl=Double.parseDouble(orderRow.get("stsl").equals("")?"0":orderRow.get("skdsl"));
          double stsl=Double.parseDouble(orderRow.get("skdsl").equals("")?"0":orderRow.get("skdsl"));
          double hssl = Double.parseDouble(orderRow.get("hssl").equals("")?"0":orderRow.get("hssl"));
          double xhssl = hssl/sl*(sl-stsl);
          double xsj = Double.parseDouble(orderRow.get("xsj").equals("")?"0":orderRow.get("xsj"));
          double dj = Double.parseDouble(orderRow.get("dj").equals("")?"0":orderRow.get("dj"));
          if(djlx.equals("-1"))
          {
            dsDetailTable.setValue("hssl", "-"+String.valueOf(xhssl));
            dsDetailTable.setValue("sl", "-"+String.valueOf(sl-stsl));
            dsDetailTable.setValue("xsje", "-"+String.valueOf(xsj*(sl-stsl)));
            dsDetailTable.setValue("jje", "-"+String.valueOf(dj*(sl-stsl)));
          }else
          {
            dsDetailTable.setValue("hssl", String.valueOf(xhssl));
            dsDetailTable.setValue("sl", String.valueOf(sl-stsl));
            dsDetailTable.setValue("xsje", String.valueOf(xsj*(sl-stsl)));
            dsDetailTable.setValue("jje", String.valueOf(dj*(sl-stsl)));
          }
          dsDetailTable.setValue("xsj", orderRow.get("xsj"));
          dsDetailTable.setValue("zk", orderRow.get("zk"));
          dsDetailTable.setValue("dj", orderRow.get("dj"));
          dsDetailTable.setValue("bz", orderRow.get("bz"));
          //dsDetailTable.setValue("sthssl", saleRow.get("sthssl"));
          //dsDetailTable.setValue("stsl", saleRow.get("stsl"));
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
      hkts = hkts.add(bd);
      String ts = m_RowInfo.get("hkts");
      String tdrq = m_RowInfo.get("tdrq");
      Date startdate = new SimpleDateFormat("yyyy-MM-dd").parse(tdrq);//提单的开单日期
      Date enddate = new Date(startdate.getTime() + hkts.longValue()*60*60*24*1000);//毫秒
      if(ts.equals(""))
      {
        m_RowInfo.put("hkts",hkts.toString());
        m_RowInfo.put("hkrq",new SimpleDateFormat("yyyy-MM-dd").format(enddate));
      }
    }
  }
  /**
   *对应页面引入合同货物操作
   * 一次可以引入多张合同的货物
   * */
  class Multi_Product_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String importOrder = m_RowInfo.get("importOrderproduct");
      String djlx =  m_RowInfo.get("djlx");
      if(importOrder.length() == 0)
        return;
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwID");
      String[] hthwIDs = parseString(importOrder,",");//解析出合同货物ID数组
      ImportOrderProduct b_ImportOrderBean =ImportOrderProduct.getInstance(req);
      b_ImportThOrderBean =getb_ImportThOrderBean(req); //退货
      for(int i=0; i < hthwIDs.length; i++)
      {
        if(hthwIDs[i].equals("-1"))
          continue;
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String tdid = dsMasterTable.getValue("tdid");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, hthwIDs[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap saleRow =null;
          saleRow = b_ImportOrderBean.getLookupRow(hthwIDs[i]);
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("tdhwid", "-1");
          dsDetailTable.setValue("tdid", tdid);
          dsDetailTable.setValue("cpid", saleRow.get("cpid"));
          dsDetailTable.setValue("hthwid", hthwIDs[i]);
          dsDetailTable.setValue("wzdjid", saleRow.get("wzdjid"));
          String dmsxid = saleRow.get("dmsxid");
          dsDetailTable.setValue("dmsxid", dmsxid);

          double sl=Double.parseDouble(saleRow.get("sl").equals("")?"0":saleRow.get("sl"));
          double hssl = Double.parseDouble(saleRow.get("hssl").equals("")?"0":saleRow.get("hssl"));
          if(djlx.equals("-1"))
          {
            dsDetailTable.setValue("hssl", String.valueOf(-hssl));
            dsDetailTable.setValue("sl", "-"+saleRow.get("sl"));
            dsDetailTable.setValue("xsje", "-"+saleRow.get("xsje"));
            dsDetailTable.setValue("jje", "-"+saleRow.get("jje"));
          }else
          {
            dsDetailTable.setValue("hssl", String.valueOf(hssl));
            dsDetailTable.setValue("sl", saleRow.get("sl"));
            dsDetailTable.setValue("xsje", saleRow.get("xsje"));
            dsDetailTable.setValue("jje", saleRow.get("jje"));
          }
          dsDetailTable.setValue("xsj", saleRow.get("xsj"));
          dsDetailTable.setValue("zk", saleRow.get("zk"));
          dsDetailTable.setValue("dj", saleRow.get("dj"));
          dsDetailTable.setValue("bz", saleRow.get("bz"));
          //dsDetailTable.setValue("sthssl", saleRow.get("sthssl"));
          //dsDetailTable.setValue("stsl", saleRow.get("stsl"));
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
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
      engine.project.LookUp creditBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP_CREDIT);
      String olddwtxId=m_RowInfo.get("dwtxid");
      putDetailInfo(data.getRequest());//保存输入的明细信息
      String dwtxId=m_RowInfo.get("dwtxid");
      RowMap corRow = corpBean.getLookupRow(dwtxId);
      creditBean.regData(new String[]{dwtxId});
      RowMap creditRow = creditBean.getLookupRow(dwtxId);
      if(olddwtxId.equals(dwtxId))
      {
        return;
      }
      else
      {
        m_RowInfo.put("dwtxId",dwtxId);
        m_RowInfo.put("tdrq",req.getParameter("tdrq"));
        m_RowInfo.put("tdbh",req.getParameter("tdbh"));
        m_RowInfo.put("personid",corRow.get("personid"));
        m_RowInfo.put("deptid",corRow.get("deptid"));
        m_RowInfo.put("hkts",creditRow.get("hkts"));
        //hkts
        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate = new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("tdrq"));
        long tqq = Long.parseLong(creditRow.get("hkts").equals("")?"0":creditRow.get("hkts"));
        Date enddate = new Date(startdate.getTime() + tqq*60*60*24*1000);
        //String today = new SimpleDateFormat("yyyy-MM-dd").format(startdate);
        String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
        //hkrq
        m_RowInfo.put("hkrq",endDate);
        m_RowInfo.put("khlx",req.getParameter("khlx"));
        dsDetailTable.empty();
        d_RowInfos.clear();
      }
    }
  }
  /**
   * 套打
   * 提供套打数据
   * 主从表的数据集分别提供,放入在RowMap中
   * */
  class Wrapper_Print implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String tdid = dsMasterTable.getValue("tdid");
      if(tdid==null)
        tdid="";
      //主表是直接产生RowMap数组
      if(dsMaster.isOpen())
        dsMaster.close();
      setDataSetProperty(dsMaster,PRINT_MASTER_SQL+" where tdid='"+tdid+"'");
      dsMaster.open();
      RowMap rm = null;
      masterRows =new RowMap[dsMaster.getRowCount()];//放主表的数据(数组)
      dsMaster.first();
      for(int i=0;i<dsMaster.getRowCount();i++)
      {
        rm = new RowMap(dsMaster);
        masterRows[i]=rm;
        dsMaster.next();
      }
      //从表放入ArrayList中
      if(dsDetail.isOpen())
        dsDetail.close();
      setDataSetProperty(dsDetail,PRINT_DETAIL_SQL+" where tdid='"+tdid+"'");
      dsDetail.open();
      //ArrayList al = new ArrayList();//放从表数据
      drows = new ArrayList();
      dsDetail.first();
      for(int i=0;i<dsDetail.getRowCount();i++)
      {
        rm = new RowMap();
        //String product = dsDetail.getValue("product");
        String sl = dsDetail.getValue("sl");
        String tdhwid = dsDetail.getValue("tdhwid");
        String price="";
        String pm = dsDetail.getValue("pm");
        String gg = dsDetail.getValue("gg");
        String jldw = dsDetail.getValue("jldw");
        String sxz = dsDetail.getValue("sxz");
        String dj = dsDetail.getValue("dj");
        String jje = dsDetail.getValue("jje");

        price = dj;
        String width = parseEspecialString(sxz,"()");
        //product=pm+gg+width+" 单价:"+dj+"元/"+jldw;

        rm.put("pm",pm);
        rm.put("gg",gg+width);
        rm.put("price",dj);
        //rm.put("product",product);
        rm.put("sl",sl);
        rm.put("tdhwid",tdhwid);
        rm.put("jje",jje);
        //al.add(rm);
        drows.add(rm);
        dsDetail.next();
      }
      /**
             drows = new ArrayList();
             ArrayList contains = new ArrayList();
             //
             * 对数据集进行循环,对货物按TDHWID进行分组,相同的货物每三个一行,
             * 不足三个的占一行.多余三个的分成几行
             *
             //
             dsDetail.first();
             for(int i=0;i<dsDetail.getRowCount();i++)
             {
             String tdhwid = dsDetail.getValue("tdhwid");
             if(contains.contains(tdhwid))
             {
             //已经分组的货物不再分组
             dsDetail.next();
             continue;
             }
             rm = new RowMap();
             int h=0;
             String count = dataSetProvider.getSequence("SELECT count(*) from VW_SALE_LADING_BILL_DETAIL where tdhwid='"+tdhwid+"'");
             int totalcount = Integer.parseInt(count);//对应同类货物(不同规格属性)总数,有totalcount/3+Integer.parseInt(((totalcount%3)>0)?"1":"0")行
             for(int j=0;j<al.size();j++)
             {
             //al里包含了数据集里的数据
             RowMap rt =(RowMap)al.get(j);
             String product = rt.get("product");
             String atdhwid = rt.get("tdhwid");
             if(rt.get("tdhwid").equals(tdhwid))
             {
             //提取出对应tdhwid
             String sl = rt.get("sl");
             String sn = "s"+(h%3);
             rm.put(sn,sl);
             h=h+1;
             if(totalcount<3&&h==totalcount)
             {
             //总数小于3且循环到结束.
             rm.put("product",product);
             drows.add(rm);//循环完
             contains.add(tdhwid);
             }
             else if((h%3)==0)
             {
             //
             rm.put("product",product);
             drows.add(rm);
             contains.add(tdhwid);
             }
             else if(totalcount>3&&h==totalcount)
             {
             //
             rm.put("product",product);
             drows.add(rm);//循环完
             contains.add(tdhwid);
             }
             }
             }
             dsDetail.next();
             }
             **/
    }
    /**
     * 分割字符串s,用sep分割成一个字符串数组.并保存到HashTable中
     * 返回字符串，是Hash表中key为field的值
     */
    public final String parseEspecialString(String s, String sep)
    {
      if(s==null || s.equals(""))
        return "0";
      String[] code = parseString(s, sep);
      String key=null, value = null;
      for(int i=0; i<code.length; i++)
      {
        if(i%2 > 0){
          value = code[i];
        }
        else{
          key = code[i].trim();
        }
        if(value==null)
          continue;
        if(key.equals(syskey))
          return value;
      }
      return "";
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
     * */
    public void delRows() throws Exception
    {
      for(int i=0;i<d_RowInfos.size();i++)
      {
        RowMap detailRow = (RowMap)d_RowInfos.get(i);
        String sl = detailRow.get("sl");
        if(sl.equals(""))
        {
          delRows();
        }
      }
    }
  }
  /**
   * 提货单引用
   * 对应引入合同
   * 得到用于查找合同编号的bean
   * @param req WEB的请求
   * @return 返回用于查找合同编号的bean
   */
  public B_ImportOrder getb_ImportOrderBean(HttpServletRequest req)
  {
    if(b_ImportOrderBean == null)
      b_ImportOrderBean = B_ImportOrder.getInstance(req);
    return b_ImportOrderBean;
  }
  /**
   * 退货单引用
   * 对应引入合同
   * 得到用于查找合同编号的bean
   * @param req WEB的请求
   * @return 返回用于查找合同编号的bean
   */
  public B_ImportOrderToBackLading getb_ImportThOrderBean(HttpServletRequest req)
  {
    if(b_ImportThOrderBean == null)
      b_ImportThOrderBean = B_ImportOrderToBackLading.getInstance(req);
    return b_ImportThOrderBean;
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
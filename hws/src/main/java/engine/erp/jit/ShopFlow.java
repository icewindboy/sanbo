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
import engine.common.*;
import engine.erp.produce.ImportProcess;
import engine.erp.produce.B_WageFormula;
import engine.erp.produce.B_WorkLoad_Sel_Process;

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
 * <p>Title: 车间流转单</p>
 * <p>Description: 车间流转单<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨建国
 * @version 1.0
 */

public final class ShopFlow extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";
  public  static final String DETAIL_SELECT_PROCESS = "10021";
  public  static final String ONCHANGE = "10031";
  public  static final String GXMC_ONCHANGE = "10731";
  public  static final String DEPTCHANGE = "11731";
  public  static final String SINGLE_SEL_PROCESS = "10041";//单选生产加工单主表操作
  public  static final String SINGLE_SELECT_PRODUCT = "10091";//单选产品操作
  public  static final String PRODUCT_ONCHANGE = "14591";//输入产品触发事件
  public  static final String COMPLETE = "10011";//强制完成触发事件
  public  static final String REPORT = "2000";//报表追踪操作
  public  static final String CANCEL_APPROVE = "12345";//取消审批
  public  static final String DRAW_SINGLE_PROCESSTASK = "12346";//流转单引入任务单事件
  public  static final String RECEIVE_GZZ_ONCHANGE = "12347";//流转单引入任务单事件

  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_cjlzd WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM sc_cjlzd WHERE ? AND fgsid=? ? ORDER BY cjlzdh DESC";
  //报表调用工人工作量的SQL
  private static final String MASTER_REPORT_SQL    = "SELECT * FROM sc_cjlzd WHERE cjlzdID =";

  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_cjlzdmx WHERE 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sc_cjlzdmx WHERE   cjlzdid ='?' ";
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM sc_cjlzd WHERE cjlzdid='?'";


  //加工单主表信息SQL
  private static final String PROCESS_DETAIL_SQL = "SELECT a.*, b.hsbl FROM sc_jgdmx a, kc_dm b WHERE a.cpid=b.cpid AND nvl(a.sl,0)>nvl(a.ypgzl,0) AND a.jgdid= ";

  private static final String PROCESSTASK_DETAIL_SQL = "SELECT * FROM VW_DRAW_PROCESSTASKDETAIL WHERE jgdid = '?' and ( wgcl is null or wgcl = 2 ) ";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表

  private LookUp technicsBean = null; //工艺路线信息的bean的引用, 用于提取工艺路线信息

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_cjlzd");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_cjlzdmx");

  private boolean isMasterAdd = true;    //是否在添加状态
  public boolean isDetailAdd = false;    //从表是否在增加状态
  public  boolean isApprove = false;     //是否在审批状态

  public boolean isReport = false; // 从表是否在报表引用状态

  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public ImportProcess  importprocessBean = null;//引入加工单的bean的引用, 用于提取引入加工单信息
  public B_WorkLoad_Sel_Process  workloadSelProcessBean = null;//加工单主表信息BEAN的引用

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String gzlid = null;
  private String lx = null;
  public String SC_STORE_UNIT_STYLE = null;//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  public String SC_PRODUCE_UNIT_STYLE =null;//1=强制换算,0=仅空值时换算
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  private User user = null;
  //private String personid = null;
  //private String rq = null;
  /**
   * 工作量列表（按工人输入）的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回工作量列表（按工人输入）的实例
   */
  public static ShopFlow getInstance(HttpServletRequest request)
  {
    ShopFlow shopFlowBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "workShopFlow";
      shopFlowBean = (ShopFlow)session.getAttribute(beanName);
      if(shopFlowBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        shopFlowBean = new ShopFlow();
        shopFlowBean.qtyFormat = loginBean.getQtyFormat();
        shopFlowBean.sumFormat = loginBean.getSumFormat();
        shopFlowBean.priceFormat = loginBean.getPriceFormat();

        shopFlowBean.fgsid = loginBean.getFirstDeptID();
        shopFlowBean.loginDept = loginBean.getDeptID();
        shopFlowBean.loginId = loginBean.getUserID();
        shopFlowBean.loginName = loginBean.getUserName();
        shopFlowBean.user = loginBean.getUser();
        shopFlowBean.SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和换算单位是否强制换算
        shopFlowBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//计量单位和生产单位是否强制换算
        shopFlowBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        shopFlowBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        //shopFlowBean.lx = "1";
        //设置格式化的字段
        shopFlowBean.dsDetailTable.setColumnFormat("sl", shopFlowBean.qtyFormat);
        shopFlowBean.dsDetailTable.setColumnFormat("scsl", shopFlowBean.qtyFormat);
        shopFlowBean.dsDetailTable.setColumnFormat("hssl", shopFlowBean.qtyFormat);
        shopFlowBean.dsDetailTable.setColumnFormat("de", shopFlowBean.priceFormat);
        shopFlowBean.dsDetailTable.setColumnFormat("desl", shopFlowBean.qtyFormat);
        shopFlowBean.dsDetailTable.setColumnFormat("jjgz", shopFlowBean.priceFormat);
        shopFlowBean.dsMasterTable.setColumnFormat("je", shopFlowBean.priceFormat);
        //shopFlowBean.dsMasterTable.setColumnFormat("zjjgz", shopFlowBean.priceFormat);
        shopFlowBean.dsDetailTable.setColumnFormat("jjgs", shopFlowBean.priceFormat);
        session.setAttribute(beanName, shopFlowBean);
      }
    }
    return shopFlowBean;
  }

  /**
   * 构造函数
   */
  private ShopFlow()
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

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"cjlzdh"}, new String[]{"SELECT pck_base.billNextCode('sc_cjlzd','cjlzdh') from dual"}));
    //dsMasterTable.setSort(new SortDescriptor("", new String[]{"jgdh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"cjlzdmxID"}, new String[]{"s_sc_cjlzdmx"}));

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
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(DEPTCHANGE), new Deptchange());
    addObactioner(String.valueOf(GXMC_ONCHANGE), new Gxmc_Onchange());
    addObactioner(String.valueOf(DETAIL_SELECT_PROCESS), new Detail_Select_Process());
    addObactioner(String.valueOf(SINGLE_SEL_PROCESS), new Single_Select_Process());//单选加工单主表
    addObactioner(String.valueOf(PRODUCT_ONCHANGE), new Product_Onchange());//输入产品编码触发事件
    addObactioner(String.valueOf(SINGLE_SELECT_PRODUCT), new Single_Product_Add());//单选产品
    addObactioner(String.valueOf(COMPLETE), new Complete());//强制完成事件
    addObactioner(String.valueOf(REPORT), new Report());//报表引用事件
    addObactioner(String.valueOf(DRAW_SINGLE_PROCESSTASK), new Draw_Single_ProcessTask());//引入任务单
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());
    addObactioner(String.valueOf(RECEIVE_GZZ_ONCHANGE), new Product_Onchange());

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
        m_RowInfo.put("jsrq", today);
        m_RowInfo.put("wgrq", today);
        m_RowInfo.put("zt","0");
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
   * 添加到审核列表的操作类
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterTable.getValue("cjlzdh");
      String deptid = dsMasterTable.getValue("deptid");
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "shop_flow", content, deptid);
    }
  }
  /**
   * 取消审批的操作类
   */
  class Cancel_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable, dsMasterTable.getRow(), "shop_flow");
    }
  }
  /**
   * 审批操作的触发类
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = true;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //得到request的参数,值若为null, 则用""代替
      String id = data.getParameter("id", "");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();
      String cjlzdid = dsMasterTable.getValue("cjlzdid");
      //打开从表
      openDetailTable(false);

      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
    }
  }

  /**
   *改变车间触发的事件
   */
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
   *输入产品编码触发的事件
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
   */
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
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//计量单位数量
      //detailRow.put("jgdmxid", rowInfo.get("jgdmxid_"+i));//工艺路线
      detailRow.put("bz", rowInfo.get("bz_"+i));//计件工时
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性
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
    String cjlzdid = dsMasterTable.getValue("cjlzdid");
    String SQL = isMasterAdd ? "-1" : cjlzdid;
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
      row.put("zt", "0");
      row.put("zdrq$a", startDay);
      row.put("zdrq$b", today);
      isMasterAdd= true;
      isDetailAdd = false;
      //
      //初始化时不显示已完成的单据
      String SQL = " AND zt<>8";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      B_WageFormula.getInstance(request).readyExpressions();
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
      //gzlid = dsMasterTable.getValue("gzlid");
      //打开从表
      openDetailTable(false);
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
      isDetailAdd = false;
      isReport = false;
      isApprove = false;
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        //cjlzdID = dsMasterTable.getValue("gzlid");
        //personid = dsMasterTable.getValue("personid");
        //rq = dsMasterTable.getValue("rq");
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
      String id = request.getParameter("gzlid");
      String SQL = MASTER_REPORT_SQL+id;
      dsMasterTable.setQueryString(SQL);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();

      gzlid = dsMasterTable.getValue("gzlid");
      B_WageFormula.getInstance(request).readyExpressions();
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
      String cjlzdid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        cjlzdid = dataSetProvider.getSequence("s_sc_cjlzd");
        //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh','a') from dual"}));
        ds.setValue("cjlzdid", cjlzdid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
        ds.setValue("zt","0");
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("cjlzdid", cjlzdid);
        double sl = detailrow.get("sl").length()>0 ? Double.parseDouble(detailrow.get("sl")) : 0;
        String cpid = detailrow.get("cpid");
        String jgdmxid = detailrow.get("jgdmxid");
        String dmsxid = detailrow.get("dmsxid");
        String bz = detailrow.get("bz");

        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("sl", String.valueOf(sl));//计量单位数量
        detail.setValue("jgdmxid", jgdmxid);
        detail.setValue("dmsxid",dmsxid);//工序
        detail.setValue("bz", bz);//定额
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
      //保存主表数据
      ds.setValue("deptid", rowInfo.get("deptid"));//完工车间id
      ds.setValue("bm_deptid", rowInfo.get("bm_deptid"));//接收车间id
      ds.setValue("wgrq", rowInfo.get("wgrq"));//完工日期
      ds.setValue("jsrq", rowInfo.get("jsrq"));//接收日期
      ds.setValue("jsr", rowInfo.get("jsr"));//接收人
      ds.setValue("gzzid", rowInfo.get("gzzid"));//完工车间班组
      ds.setValue("sc__gzzid", rowInfo.get("sc__gzzid"));//接收车间班组
      ds.setValue("bz", rowInfo.get("bz"));//memo
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
      else if(String.valueOf(POST).equals(action)){
        //data.setMessage(showJavaScript("backList();"));
        isMasterAdd = false;
        masterRow = ds.getInternalRow();//2004-3-30 14:53 新增 如果是保存按钮行为完成后定位数据集 yjg
        initRowInfo(true, isMasterAdd, true);
        initRowInfo(false, isMasterAdd, true);
      }
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
      ArrayList list = new ArrayList(d_RowInfos.size());
      String cpid=null, dmsxid=null, unit = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");

        StringBuffer buf = new StringBuffer().append(cpid).append(",").append(dmsxid);
        unit = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        /*if(list.contains(unit))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
         list.add(unit);
        */
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
      }
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
        return showJavaScript("alert('请选择完工车间！');");

      String tempgzz = rowInfo.get("gzzid");
      if(tempgzz.equals(""))
        return showJavaScript("alert('请选择完工车间班组！');");
      String wgrq = rowInfo.get("wgrq");
      if(wgrq.equals(""))
        return showJavaScript("alert('完工日期不能为空！');");
      else if(!isDate(wgrq))
        return showJavaScript("alert('非法日期！');");
      String bm_deptid = rowInfo.get("bm_deptid");
      if(bm_deptid.equals(""))
        return showJavaScript("alert('请选择接收车间！');");
      String tempgzz2 = rowInfo.get("sc__gzzid");
      if(tempgzz2.equals(""))
        return showJavaScript("alert('请选择接收车间班组！');");
      String jsr = rowInfo.get("jsr");
      if(jsr.equals(""))
        return showJavaScript("alert('请选择接收人！');");
      String jsrq = rowInfo.get("jsrq");
      if(jsrq.equals(""))
        return showJavaScript("alert('接收日期不能为空！');");
      else if(!isDate(jsrq))
        return showJavaScript("alert('非法日期！');");
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),fgsid, SQL});
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
        new QueryColumn(master.getColumn("cjlzdh"), null, null, null),
        new QueryColumn(master.getColumn("wgrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("wgrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("jsrq"), null, null, null, "c", ">="),
        new QueryColumn(master.getColumn("jsrq"), null, null, null, "d", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "="),//状态
        new QueryColumn(master.getColumn("cjlzdid"), "VW_CJLZD_QUERY", "cjlzdid", "gg", "gg", "like"),//从表品名
        new QueryColumn(master.getColumn("cjlzdid"), "VW_CJLZD_QUERY", "cjlzdid", "cpbm", "cpbm", "="),//从表产品编码
        new QueryColumn(master.getColumn("cjlzdid"), "VW_CJLZD_QUERY", "cjlzdid", "product", "product", "like"),//从表品名
      });
      isInitQuery = true;
    }
  }
  /**
   *  根据加工单从表增加操作
   */
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
      String gzlid = dsMasterTable.getValue("gzlid");
      for(int i=0; i < jgdmxID.length; i++)
      {
        if(jgdmxID[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, jgdmxID[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap importProcessRow = getProcessGoodsBean(req).getLookupRow(jgdmxID[i]);
          double sl = importProcessRow.get("sl").length()>0 ? Double.parseDouble(importProcessRow.get("sl")) : 0;
          //double hsbl = importProcessRow.get("hsbl").length()>0 ? Double.parseDouble(importProcessRow.get("hsbl")) : 0;
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("gzlmxid", "-1");
          dsDetailTable.setValue("jgdmxid",jgdmxID[i]);
          dsDetailTable.setValue("cpid", importProcessRow.get("cpid"));
          dsDetailTable.setValue("sl", importProcessRow.get("sl"));
          //dsDetailTable.setValue("hssl", formatNumber(String.valueOf(hsbl==0 ? 0 : sl/hsbl), qtyFormat));
          dsDetailTable.setValue("gzlid", isMasterAdd ? "-1" : gzlid);
          dsDetailTable.setValue("dmsxid", importProcessRow.get("dmsxid"));
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
        data.setMessage(showJavaScript("big_change()"));
      }
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
      String gzlid = dsMasterTable.getValue("gzlid");
      dsDetailTable.goToRow(row);
      RowMap detailrow = null;
      detailrow = (RowMap)d_RowInfos.get(row);
      detailrow.put("gzlmxid", "-1");
      detailrow.put("cpid", cpid);
      detailrow.put("gzlid", isMasterAdd ? "-1" : gzlid);
    }
  }
  /**
   *  选择任务单主表，引入从表所有未加工信息
   */
  class Single_Select_Process implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String singleImportProcess = m_RowInfo.get("drawSingleProcessTask");
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

      RowMap processMasterRow = getProcessMasterBean(req).getLookupRow(singleImportProcess);
      rowInfo.put("deptid", processMasterRow.get("deptid"));
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String gzlid = dsMasterTable.getValue("gzlid");
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
          dsDetailTable.setValue("gzlid", isMasterAdd ? "-1" : gzlid);
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
  /**
   *  选择任务单主表.得到的任务单id定位出任务单明细资料,然后把任务单明细资料数据引入到此张流转单中来.
   */
  class Draw_Single_ProcessTask implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String mulitProcessTask = m_RowInfo.get("drawSingleProcessTask");
      if(mulitProcessTask.equals(""))
        return;
      String[] singleImportProcessTask = parseString(mulitProcessTask,",");
      for(int k=0; k < singleImportProcessTask.length; k++)
      {
        String processTask = singleImportProcessTask[k];
        String SQL = combineSQL(PROCESSTASK_DETAIL_SQL, "?", new String[]{processTask});
        EngineDataSet tempProcessData = null;//零时任务单从表信息数据集
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

        //实例化查找数据集的类
        EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String cjlzdid = dsMasterTable.getValue("cjlzdid");
        for(int i=0; i<tempProcessData.getRowCount(); i++)
        {
          tempProcessData.goToRow(i);
          String jgdmxid = tempProcessData.getValue("jgdmxid");
          locateGoodsRow.setValue(0, jgdmxid);
          if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
          {
            double sl = tempProcessData.getValue("sl").length()>0 ? Double.parseDouble(tempProcessData.getValue("sl")) : 0;//加工单需要的加工数量
            double yrksl = tempProcessData.getValue("yrksl").length()>0 ? Double.parseDouble(tempProcessData.getValue("yrksl")) : 0;//加工单需要的加工数量
            double wrksl = sl-yrksl>0 ? sl-yrksl : 0;
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("cjlzdmxid", "-1");
            dsDetailTable.setValue("jgdmxid",jgdmxid);
            dsDetailTable.setValue("cpid", tempProcessData.getValue("cpid"));
            dsDetailTable.setValue("sl", String.valueOf(wrksl));
            dsDetailTable.setValue("cjlzdid", isMasterAdd ? "-1" : cjlzdid);
            dsDetailTable.setValue("dmsxid", tempProcessData.getValue("dmsxid"));
            dsDetailTable.post();
            //创建一个与用户相对应的行
            RowMap detailrow = new RowMap(dsDetailTable);
            d_RowInfos.add(detailrow);
          }
          //data.setMessage(showJavaScript("big_change()"));
        }
      }
    }
  }

  /**
   *  从表增加操作(增加一个空白行)
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String cjlzdid = dsMasterTable.getValue("cjlzdid");
      detail.insertRow(false);
      detail.setValue("cjlzdid", isMasterAdd ? "-1" : cjlzdid);
      detail.post();
      d_RowInfos.add(new RowMap());
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
      dsMasterTable.setValue("zt", "8");
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
   */
  public ImportProcess getProcessGoodsBean(HttpServletRequest req)
  {
    if(importprocessBean == null)
      importprocessBean = ImportProcess.getInstance(req);
    return importprocessBean;
  }
  /**
   * 得到生产加工单主表一行信息的bean
   * @param req WEB的请求
   * @return 返回生产加工单主表一行信息的bean
   */
  public B_WorkLoad_Sel_Process getProcessMasterBean(HttpServletRequest req)
  {
    if(workloadSelProcessBean == null)
      workloadSelProcessBean = B_WorkLoad_Sel_Process.getInstance(req);
    return workloadSelProcessBean;
  }
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
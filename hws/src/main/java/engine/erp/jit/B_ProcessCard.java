package engine.erp.jit;


import engine.dataset.EngineDataSet;
import engine.dataset.EngineDataView;
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
 * <p>Title: 生产--生产加工单列表</p>
 * <p>Description: 生产--生产加工单列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */
public final class B_ProcessCard extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL            = "10001";
  public  static final String DETAIL_SELECT_TASK     = "10021";
  public  static final String ONCHANGE               = "10031";
  public  static final String SINGLE_SELECT_TASK     = "10531";
  public  static final String PRODUCT_ONCHANGE       = "10091";//输入产品触发事件
  public  static final String SINGLE_SELECT_PRODUCT  = "10891";//单选产品触发事件
  public  static final String COMPLETE               = "11001";//手工强制完成事件
  public  static final String MATERAIL_ADD           = "11002";//生产加工单物料增加事件
  public  static final String SUBTASK_ADD            = "11003";//生产加工单新增分切计划任务单增加事件
  public  static final String MATERAIL_DEL           = "11004";//生产加工单物料清单删除事件
  public  static final String CONFIRM                = "11009";//通用加工单。对每一条加工单明细的物料清单保存到内存事件
  public  static final String DETAIL_REFRESH         = "20032";//通用加工单。保存事件
  public  static final String MATERAIL_REFRESH       = "20042";//通用加工单。刷新该条加工单物料事件
  public  static final String WJGD_ADD               = "20051";//外加工单增加
  public  static final String CANCEL_APPROVE         = "11031";
  public  static final String MULTISELECT_SCBOM      = "99999";
  public  static final String REPORT                 = "20007";
  public  static final String DWTXONCHNAGE           = "99998";//单位改变
  public  static final String DETAIL_COMPLETE        = "20010";

  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_process WHERE 1<>1";
  private static final String MASTER_SQL       = "SELECT a.*,b.djh,c.deptid FROM sc_process a,VW_SC_JH b,sc_jh c WHERE ?  AND a.scjhid=b.scjhid(+) AND b.xm=c.xm AND a.fgsid=? ? ORDER BY a.processdm DESC";
  private static final String MASTER_EDIT_SQL  = "SELECT * FROM sc_process WHERE   processID='?' ";

  private static final String DETAIL_STRUT_SQL = "SELECT a.*,(SELECT sum(nvl(c.sl,0))ywcsl FROM  sc_jgd b,sc_jgdmx c where b.zt in(1,8) and c.rtrn=0 and a.processmxid=b.processmxid(+) and b.jgdid=c.jgdid)ywcsl FROM  sc_processmx a where  1<>1  ORDER BY a.cpid, a.processmxID ";
  private static final String DETAIL_SQL       = " SELECT a.*,(SELECT sum(nvl(c.sl,0))ywcsl FROM  sc_jgd b,sc_jgdmx c where b.zt in(1,8) and c.rtrn=0  and  a.processmxid=b.processmxid(+) and b.jgdid=c.jgdid)ywcsl FROM  sc_processmx a where  a.processID='?'  ORDER BY a.processmxID ";

  //private static final String DETAIL_EDIT_SQL = "SELECT * FROM sc_processmx WHERE processID = '?'";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsMasterList   = new EngineDataSet();
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_process");
  public  HtmlTableProducer masterListProducer = new HtmlTableProducer(dsMasterList, "sc_process");

  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_processmx");
  public  HtmlTableProducer searchTable = new HtmlTableProducer(dsMasterTable, "sc_process", "sc_process");//查询得到数据库中配置的字段

  private boolean isMasterAdd = true;  //是否在添加状态
  public  boolean isApprove = false;   //是否在审批状态
  public  boolean isDetailAdd = false; //从表是否在增加状态
  public  boolean isReport = false;
  private long    masterRow = -1;         //保存主表修改操作的行记录指针

  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String processID = null;
  private User user = null;
  public String SC_PRODUCE_UNIT_STYLE =null;//1=强制换算,0=仅空值时换算
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  public static String SC_STORE_UNIT_STYLE    = "1";//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  //private String scjhid = null;//生产计划ID
  private String rwdid = null;//生产任务单ID

  private String processmxID = null;
  public String SC_PLAN_ADD_STYLE = null;//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
  private int procDetaiLlSeq = -1;//在生成物料的时候可用,因为新增加工单明细的时候还没有得到加工单明细ＩＤ。而生成物料时又要用到
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交


  /**
   * 生产加工单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回生产加工单列表的实例
   */
  public static B_ProcessCard getInstance(HttpServletRequest request)
  {
    B_ProcessCard B_ProcessCardBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_ProcessCardBean";
      B_ProcessCardBean = (B_ProcessCard)session.getAttribute(beanName);
      if(B_ProcessCardBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        B_ProcessCardBean = new B_ProcessCard();
        B_ProcessCardBean.qtyFormat = loginBean.getQtyFormat();
        B_ProcessCardBean.sumFormat = loginBean.getSumFormat();

        B_ProcessCardBean.fgsid = loginBean.getFirstDeptID();
        B_ProcessCardBean.loginId = loginBean.getUserID();
        B_ProcessCardBean.loginName = loginBean.getUserName();
        B_ProcessCardBean.loginDept = loginBean.getDeptID();
        B_ProcessCardBean.user = loginBean.getUser();
        B_ProcessCardBean.SC_PLAN_ADD_STYLE = loginBean.getSystemParam("SC_PLAN_ADD_STYLE");//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
        B_ProcessCardBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        B_ProcessCardBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//计量单位和生产单位是否强制换算
        B_ProcessCardBean.SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
        B_ProcessCardBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        //设置格式化的字段

        session.setAttribute(beanName, B_ProcessCardBean);
      }
    }
    return B_ProcessCardBean;
  }

  /**
   * 构造函数
   */
  private B_ProcessCard()
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
    setDataSetProperty(dsMasterList, MASTER_STRUT_SQL);
    dsMasterList.setTableName("sc_process");
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    dsDetailTable.setTableName("sc_processmx");
    dsDetailTable.setLoadDataUseSelf(false);
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"processdm"}, new String[]{"SELECT pck_base.billNextCode('sc_process','processdm','a') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"processdm"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"processmxID"}, new String[]{"s_sc_processmx"}));
    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(WJGD_ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(SUBTASK_ADD), masterAddEdit);//新增分切计划任务触发事件
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(PRODUCT_ONCHANGE), new Product_Onchange());//输入产品编码触发事件
    addObactioner(String.valueOf(COMPLETE), new Complete());//强制完成操作
    //addObactioner(String.valueOf(DETAIL_REFRESH), new Detail_Refresh());//通用加工单，保存加工单明细页面信息操作
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(REPORT), new Approve());
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());//取消审批
    addObactioner(String.valueOf(SINGLE_SELECT_TASK), new Import_Scjh());//取消审批
    addObactioner(String.valueOf(DETAIL_COMPLETE), new Detail_Complete());

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
    if(dsMasterList != null){
      dsMasterList.close();
      dsMasterList = null;
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
    if(masterListProducer != null)
    {
      masterListProducer.release();
      masterListProducer = null;
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
        m_RowInfo.put("zdrq", today);//制单日期
        m_RowInfo.put("zdr", loginName);//操作员
        m_RowInfo.put("zdrid", loginId);
        m_RowInfo.put("deptid", loginDept);
        m_RowInfo.put("kdrq", today);
        m_RowInfo.put("zt", "0");
        String processdm = dataSetProvider.getSequence("SELECT pck_base.billNextCode('sc_process','processdm') from dual");
        m_RowInfo.put("processdm", processdm);
      }
    }
    else
    {
      EngineDataSet dsDetail = dsDetailTable;
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if(isInit)
        d_RowInfos.clear();

      dsDetail.first();//循环加工单明细把数据存入RowMap里面
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
    String ss = rowInfo.get("deptid");

    //生产加工单明细信息
    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      String cpid = rowInfo.get("cpid_"+i);


      detailRow.put("cpid", rowInfo.get("cpid_"+i));
      detailRow.put("gymcid", rowInfo.get("gymcid_"+i));
      detailRow.put("gxfdid", rowInfo.get("gxfdid_"+i));
      detailRow.put("deptid", rowInfo.get("deptid_"+i));
      detailRow.put("rcvnmbr", rowInfo.get("rcvnmbr_"+i));
            detailRow.put("rcvbgnmbr", rowInfo.get("rcvbgnmbr_"+i));
      detailRow.put("jhrq", rowInfo.get("jhrq_"+i));
      detailRow.put("sjrq", rowInfo.get("sjrq_"+i));
      detailRow.put("bz", rowInfo.get("bz_"+i));
      detailRow.put("wgcl", rowInfo.get("wgcl_"+i));
    }
  }
  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }
  public final EngineDataSet getMaterListTable()
  {
    return dsMasterList;
  }
/*
  public ArrayList[] getOptionGylx(String gylxmxid)
  {
    String sql = "select * from VW_sc_option_gylx ";
    if(gylxmxid!=null&&!gylxmxid.equals(""))
      sql = sql+" where gylxmxid='"+gylxmxid+"'";
    else
    {
      ArrayList opkey = new ArrayList();
      ArrayList opval = new ArrayList();
      ArrayList[] lists  = new ArrayList[]{opkey, opval};
      return lists;
    }
    EngineDataSet tmp = new EngineDataSet();
    setDataSetProperty(tmp,sql);
    tmp.open();

    ArrayList opkey = new ArrayList();
    ArrayList opval = new ArrayList();
    tmp.first();
    for(int i=0;i<tmp.getRowCount();i++)
    {
      opkey.add(tmp.getValue("gymcid"));
      opval.add(tmp.getValue("gymc"));
      tmp.next();
    }
    ArrayList[] lists  = new ArrayList[]{opkey, opval};
    return lists;
  }
    */
  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.openDataSet();
    return dsDetailTable;
  }
  /**
   * 得到生产计划ＩＤ

  public final String getPlanID(){
    return scjhid;
  }
     */
  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    String id = isMasterAdd ? "-1" : processID;
    String SQL = combineSQL(DETAIL_SQL, "?", new String[]{id});

    dsDetailTable.setQueryString(SQL);//打开加工单明细数据集
    if(!dsDetailTable.isOpen())
      dsDetailTable.openDataSet();
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
    dsMasterList.goToInternalRow(masterRow);
    return dsMasterList.getRow();
  }
  /**
   * 初始化操作的触发类oiguguiuiui
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      masterListProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("zt", "0");
      row.put("kdrq$a", startDay);
      row.put("kdrq$b", today);
      isMasterAdd = true;
      isDetailAdd =false;
      //初始化时不显示已完成的单据
      //String SQL = " AND zt<>8 ";
      //String scjhid = request.getParameter("scjhid");
      //scjhid = scjhid == null? "":" AND scjhid = " + scjhid;
      //SQL = SQL + scjhid;
      searchTable.getWhereInfo().clearWhereValues();
      //SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),fgsid, SQL});


      String SQL = " AND a.zt<>8  ";
      String MSQL =  combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("a.deptid", "a.zdrid"), fgsid, SQL});
      dsMasterList.setQueryString(MSQL);
      dsMasterList.setRowMax(null);

      //dsMasterTable.setQueryString(SQL);
      //dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
    }
  }

  /**
   * 显示从表的列表信息
   */
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterList.getInternalRow();
      processID = dsMasterList.getValue("processID");
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
      isApprove = false;
      procDetaiLlSeq = -1;
      isMasterAdd = !String.valueOf(EDIT).equals(action);
      if(!isMasterAdd)
      {
        dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterList.getInternalRow();
        //scjhid = dsMasterList.getValue("scjhid");
        processID = dsMasterList.getValue("processID");
        openDetailTable(false);
      }
      else
      {
        synchronized(dsDetailTable){
          openDetailTable(isMasterAdd);
        }
      }
      dsMasterTable.setQueryString(isMasterAdd?MASTER_STRUT_SQL:combineSQL(MASTER_EDIT_SQL,"?",new String[]{processID}));
      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();

      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();


      isReport = false;
      String deptid = req.getParameter("deptid");
      String cpid = req.getParameter("cpid");
      String processmxid = req.getParameter("processmxid");
      String gymcid = req.getParameter("gymcid");
      String gylxmxid = req.getParameter("gylxmxid");


      String SQL = " and a.deptid='"+deptid+"' and a.cpid ='"+cpid+"' and a.processmxid='"+processmxid+"'";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("a.deptid", "a.zdrid"),SQL});
      if(dsMasterList.isOpen())
        dsMasterList.close();
      setDataSetProperty(dsMasterList,SQL);
      dsMasterList.open();
      dsMasterList.first();
      synchronized(dsDetailTable){
        String piecewage_ID = dsMasterList.getValue("piecewage_ID");
        isMasterAdd = piecewage_ID.equals("")?true:false;
        String sql = piecewage_ID.equals("") ? "-1" : piecewage_ID;
        SQL = combineSQL(DETAIL_SQL, "?", new String[]{sql});
        dsDetailTable.setQueryString(SQL);
        if(!dsDetailTable.isOpen())
          dsDetailTable.open();
        else
          dsDetailTable.refresh();
      }
      masterProducer.init(req, loginId);
      detailProducer.init(req, loginId);

      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      m_RowInfo.put("deptid",deptid);
      m_RowInfo.put("cpid",cpid);
      m_RowInfo.put("gymcid",gymcid);
      m_RowInfo.put("processmxid",processmxid);
      m_RowInfo.put("gylxmxid",gylxmxid);
    }
  }
  /**
   * 分切加工单保存操作
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
      String processID = null;
      if(isMasterAdd){
        ds.insertRow(false);
        processID = dataSetProvider.getSequence("s_sc_process");
        ds.setValue("processID", processID);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);

        ds.setValue("processdm", rowInfo.get("processdm"));
        ds.setValue("scjhid", rowInfo.get("scjhid"));
        ds.setValue("zdr", loginName);//操作员
        ds.setValue("zt", "0");


      }
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0);
      BigDecimal totalSum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("processID", processID);

        detail.setValue("gymcid",detailrow.get("gymcid"));
        detail.setValue("gxfdid",detailrow.get("gxfdid"));
        detail.setValue("cpid",detailrow.get("cpid"));
        detail.setValue("deptid",detailrow.get("deptid"));
        detail.setValue("rcvnmbr",detailrow.get("rcvnmbr"));
        detail.setValue("rcvbgnmbr",detailrow.get("rcvbgnmbr"));
        detail.setValue("scsl",detailrow.get("scsl"));
        detail.setValue("jhrq",detailrow.get("jhrq"));
        detail.setValue("sjrq",detailrow.get("sjrq"));
        detail.setValue("wgcl",detailrow.get("wgcl"));
        detail.setValue("bz",detailrow.get("bz"));
        detail.post();

        detail.next();
      }

      //保存主表数据
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("kdrq", rowInfo.get("kdrq"));

      ds.setValue("scjhid",   rowInfo.get("scjhid"));
      ds.setValue("jgyq", rowInfo.get("jgyq"));//加工说明
      //boolean isAll = isAllMaterail();
      //ds.setValue("rwdid", isAll ? rwdid : "");//任务单ID



      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      dsMasterList.readyRefresh();

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
      ArrayList list = new ArrayList(d_RowInfos.size());
      String cpid=null, dmsxid=null, gylxid=null,rwdmxid=null, unit=null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");
        gylxid = detailrow.get("gylxid");
        rwdmxid = detailrow.get("rwdmxid");
        StringBuffer buf = new StringBuffer().append(rwdmxid).append(",").append(cpid).append(",").append(dmsxid).append(",").append(gylxid);
        unit = buf.toString();
        String cpl = detailrow.get("cpl");
        if(cpl.length()>0 && (temp = checkNumber(cpl, "第"+row+"行出品率")) != null)
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
      String  temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择车间！');");
      return null;
    }
  }
  /**
   * 添加到审核列表的操作类
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String num = data.getParameter("rownum");
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterList.getValue("processdm");
      String deptid = dsMasterList.getValue("deptid");
      approve.putAproveList(dsMasterList, dsMasterList.getRow(), "process_card", content,deptid);
    }
  }
  /**
   * 取消审批的操作类
   */
  class Cancel_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterList, dsMasterList.getRow(), "process_card");
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

      //得到request的参数,值若为null, 则用""代替
      isReport = String.valueOf(REPORT).equals(action);
      String id = data.getParameter("id", "");
      if(isReport){
        isApprove = false;
        id = data.getParameter("id");//得到报表传递的参数既收发单据主表ID
      }
      else{
        isApprove = true;//审批操作
        id = data.getParameter("id", "");
      }
      String sql = combineSQL(MASTER_EDIT_SQL, "?", new String[]{id});

      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.openDataSet();

      processID = dsMasterTable.getValue("processID");
      openDetailTable(false);
      initRowInfo(true, false, true);
      initRowInfo(false, false, true);

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



      String count = dataSetProvider.getSequence("select count(*) from REP_SC_jgd_detail t where t.processid='"+processID+"'");
      if(!count.equals("0"))
      {
        data.setMessage(showJavaScript("alert('该单据已被引用,不能删除!')"));
        return;
      }
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM sc_process a WHERE a.processID='"+processID+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        data.setMessage(showJavaScript("alert('该单据不能删除!')"));
        return;
      }

      //dsProcessMaterail.deleteAllRows();
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
      searchTable.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("a.deptid", "a.zdrid"),  fgsid, SQL});
      if(!dsMasterList.getQueryString().equals(SQL))
      {
        dsMasterList.setQueryString(SQL);
        dsMasterList.setRowMax(null);
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
      EngineDataSet master = dsMasterList;
      //EngineDataSet detail = dsMasterTable;
      if(!master.isOpen())
        master.openDataSet();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("processdm"), null, null, null),
        new QueryColumn(master.getColumn("kdrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("kdrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("djh"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("djh"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        //new QueryColumn(master.getColumn("processID"), "sc_processmx", "processID", "cpid", null, "="),//从表品名
        //new QueryColumn(master.getColumn("processID"), "VW_SCJGD_QUERY", "processID", "cpbm", "cpbm", "like"),//从表产品编码
        //new QueryColumn(master.getColumn("processID"), "VW_SCJGD_QUERY", "processID", "product", "product", "like"),//从表品名
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
  /**
   *改变车间触发的事件
   */
  class Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      detail.first();
      while(detail.inBounds())
      {
        String wlxqjhmxid = detail.getValue("wlxqjhmxid");
        if(!wlxqjhmxid.equals(""))
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
   *改变车间触发的事件

  class Detail_Refresh implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
    }
  }
     */
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
      String processID = dsMasterTable.getValue("processID");
      detail.insertRow(false);
      detail.setValue("processmxID", "-1");
      detail.setValue("processID", isMasterAdd ? "-1" : processID);
      detail.post();
      d_RowInfos.add(new RowMap());
    }
  }

  class Import_Scjh implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();
      String scjhid = m_RowInfo.get("singleImportTask");
      if(scjhid.equals(""))
        return;
      rowInfo.put("scjhid",scjhid);
      EngineDataSet tmp = new EngineDataSet();
      setDataSetProperty(tmp,"select * from vw_sc_prouce_card where scjhid='"+scjhid+"'");
      tmp.open();
      if(tmp.getRowCount()==0)
        return;
      RowMap detailrow = null;
      tmp.first();
      for(int i=0,n=tmp.getRowCount();i<n;i++)
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("processmxid", "-1");

        dsDetailTable.setValue("gylxmxid",tmp.getValue("gylxmxid"));
        dsDetailTable.setValue("gymcid",tmp.getValue("gymcid"));
        dsDetailTable.setValue("gxfdid",tmp.getValue("gxfdid"));
        dsDetailTable.setValue("cpid",tmp.getValue("gxcpid"));
        dsDetailTable.setValue("deptid",tmp.getValue("deptid"));
        dsDetailTable.setValue("rcvnmbr",tmp.getValue("sl"));
        dsDetailTable.setValue("scsl",tmp.getValue("sl"));
        dsDetailTable.post();
        detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
        tmp.next();
      }
    }
  }

  /**
   *  强制完成触发事件.此操作就是入库确认的操作,由入库确认按钮触发.
   *  根据已排工作量和加工数量手工完成操作
   */
  class Complete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsMasterList.goToRow(row);
      String processID = dsMasterList.getValue("processID");
      String totDetailSum = dataSetProvider.getSequence("select nvl(sum(jgje), 0) from sc_processmx where processID = " + processID);
      String totMaterialSum = dataSetProvider.getSequence("select nvl(sum(wlje), 0) from sc_processwl where processID = " + processID);
      BigDecimal totMasterSum = new BigDecimal(totDetailSum).subtract(new BigDecimal(totMaterialSum));

      dsMasterList.setValue("zt", "8");
      dsMasterList.post();
      dsMasterList.saveChanges();
    }
  }
  /**
   *  从表增加操作
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
   *  从表增加操作
   */
  class Detail_Complete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getDetailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      ds.goToRow(rownum);
      ds.setValue("statte","1");
      ds.saveChanges();
      d_RowInfos.set(rownum,new RowMap(ds));
    }
  }
}

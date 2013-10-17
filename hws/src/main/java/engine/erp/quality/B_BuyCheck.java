package engine.erp.quality;

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
import engine.html.HtmlTableProducer;

import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import java.util.Calendar;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 采购--采购订单列表</p>
 * <p>Description: 采购--采购订单列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_BuyCheck extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";
  public  static final String DETAIL_PRICE_ADD = "10111";
  public  static final String DETAIL_APPLY_ADD = "10021";
  public  static final String WB_ONCHANGE = "10031";
  public  static final String ONCHANGE = "10041";
  public  static final String CANCLE_APPROVE = "10081";
  public  static final String COMPLETE = "11231";
  public  static final String REPEAL = "11581";
  public  static final String DEPTCHANGE = "11591";//改变车间触发事件
  public  static final String CORPCHANGE = "11571";//手工输入往来单位触发事件
  public  static final String REPORT = "2000";//报表追踪触发事件
  public  static final String OTHERADD = "2004";//选择添加方式


  private static final String MASTER_STRUT_SQL = "SELECT * FROM zl_buyCheck WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM zl_buyCheck WHERE billtype='?'";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM zl_buyCheckDetail WHERE 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM zl_buyCheckDetail WHERE buyCheckID='?' ";//
  //判断合同货物是否被引用的SQL语句
  //private static final String ORDER_RECIEVE_GOODS
  //= "SELECT * FROM zl_buyCheckDetail a,zl_buyCheck b"
  //+ " WHERE a.buyCheckID = b.buyCheckID";
  //用于审批时候提取一条记录
  //private static final String MASTER_APPROVE_SQL = "SELECT * FROM zl_buyCheck ";//WHERE buyCheckID='?'";
  //
  //private static final String BUY_GOODS_SQL = "SELECT * FROM cg_bj a, kc_dm b WHERE a.cpid = b.cpid AND fgsid=? AND dwtxid = ? ORDER BY b.cpbm ";
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表

  private EngineDataSet dsCancel = new EngineDataSet();//用于判断合同是否能被作废
  private ArrayList cancelOrder = new ArrayList();

  private EngineDataSet dsBuyGoods  = new EngineDataSet();//引入供应商报价

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "zl_buyCheck");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "zl_buyCheckDetail");
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态
  public boolean isReport = false;
  public  boolean isDetailAdd = false; // 从表是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  //private LookUp buyApplyBean = null; //采购申请单的bean的引用, 用于提取采购单价
  private LookUp foreignBean = null; //外币信息的bean的引用
  private LookUp corpBean = null; //得到往来单位的一行信息
  //private ImportApply buyApplyBean = null; //采购申请单的bean的引用, 用于提取采购单价

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String buyCheckID = null;
  private User user = null;
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public String billType= null;
  /**
   * 采购订单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回采购订单列表的实例
   */
  public static B_BuyCheck getInstance(HttpServletRequest request)
  {
    B_BuyCheck buyCheckBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "buyCheckBean";
      buyCheckBean = (B_BuyCheck)session.getAttribute(beanName);
      if(buyCheckBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        buyCheckBean = new B_BuyCheck();
        buyCheckBean.qtyFormat = loginBean.getQtyFormat();
        buyCheckBean.priceFormat = loginBean.getPriceFormat();
        buyCheckBean.sumFormat = loginBean.getSumFormat();

        buyCheckBean.fgsid = loginBean.getFirstDeptID();
        buyCheckBean.loginId = loginBean.getUserID();
        buyCheckBean.loginName = loginBean.getUserName();
        buyCheckBean.loginDept = loginBean.getDeptID();
        buyCheckBean.user = loginBean.getUser();
        buyCheckBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        session.setAttribute(beanName, buyCheckBean);
      }
    }
    return buyCheckBean;
  }

  /**
   * 构造函数
   */
  private B_BuyCheck()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  public  HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "zl_buycheck", "zl_buycheck");//查询得到数据库中配置的字段
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    setDataSetProperty(dsCancel, null);

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"buyCheckID"}, new String[]{"SELECT pck_base.billNextCode('zl_buyCheck','buyCheckID') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"buyCheckID"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"buyCheckDetailID"}, new String[]{"s_zl_buyCheckDetail"}));
    dsDetailTable.setSort(new SortDescriptor("", new String[]{"buyCheckDetailID"}, new boolean[]{false}, null, 0));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(OTHERADD), masterAddEdit);

    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    //addObactioner(String.valueOf(APPROVE), new Approve());
    //addObactioner(String.valueOf(REPORT), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    //addObactioner(String.valueOf(DETAIL_PRICE_ADD), new Detail_Price_Add());
    //addObactioner(String.valueOf(DETAIL_APPLY_ADD), new Detail_Apply_Add());
    addObactioner(String.valueOf(WB_ONCHANGE), new Wb_Onchange());
    //////addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(CANCLE_APPROVE), new Cancle_Approve());
    addObactioner(String.valueOf(COMPLETE), new Complete());
    addObactioner(String.valueOf(REPEAL), new Repeal());
    addObactioner(String.valueOf(DEPT_CHANGE), new DeptChange());//OTHERADD
    //addObactioner(String.valueOf(), new DeptChange());
    //addObactioner(String.valueOf(CORPCHANGE), new Corp_Change());
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
   *
   * @param buyCheckID
   * @return
   */
  public boolean isCanCancel(String buyCheckID)
  {
    return !cancelOrder.contains(buyCheckID);
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
        Calendar  cd= new GregorianCalendar();
        int year = cd.get(Calendar.YEAR);
        int month = cd.get(Calendar.MONTH);
        cd.clear();
        //cd.set(year,month+1,0);
        //Date ed = cd.getTime();
        //String endday =  new SimpleDateFormat("yyyy-MM-dd").format(ed);
        //Date startDate = new Date();
        //Date endDate = new Date(startDate.getYear(), startDate.getMonth()+1, 0);
        //String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        //String endday = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
        //m_RowInfo.put("czrq", today);//制单日期
        /////m_RowInfo.put("czy", loginName);//操作员
        //m_RowInfo.put("htrq", today);
        //m_RowInfo.put("ksrq", today);
        //m_RowInfo.put("jsrq", endday);
        //m_RowInfo.put("czyid", loginId);
        //m_RowInfo.put("deptid", loginDept);
        //m_RowInfo.put("khlx", "A");
        //m_RowInfo.put("billType","1");
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
      detailRow.put("jymxID", rowInfo.get("jymxID_"+i));//检验项目ID
      detailRow.put("techRequest", rowInfo.get("techRequest_"+i));//技术要求
      detailRow.put("checkResult", rowInfo.get("checkResult_"+i));//检验结果
      detailRow.put("checkNum", formatNumber(rowInfo.get("checkNum_"+i), sumFormat));//检验数量
      //detailRow.put("rejectNum", formatNumber(rowInfo.get("rejectNum_"+i), priceFormat));//不合格数
      detailRow.put("rejectNum", formatNumber(rowInfo.get("rejectNum_"+i), sumFormat));//不合格数
      detailRow.put("reasonID",rowInfo.get("reasonID_"+i));//原因ID
      detailRow.put("bugID", rowInfo.get("bugID_"+i));//缺陷ID
      detailRow.put("ID", rowInfo.get("ID_"+i));//检验方法ID
      detailRow.put("memo", rowInfo.get("memo_"+i));//备注
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
    String SQL = isMasterAdd ? "-1" : buyCheckID;
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
      isApprove = false;
      isReport = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      //table.getWhereInfo().clearWhereValues();
      //String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      //String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      isMasterAdd = true;
      isDetailAdd = false;
      //String retu = doService(request, response);
      //String SQL = MASTER_SQL;//" AND billType<>8 AND billType<>4";
      //SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      String SQL=combineSQL(MASTER_SQL,"?",new String[]{billType});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
    }
  }
  /**
   *改变车间触发的事件
   *业务员跟着改
   */
  class DeptChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      m_RowInfo.put(req);
    }
  }
  /**
   *改变往来单位触发的事件

  class Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      EngineDataSet ds = getMaterTable();
      RowMap rowinfo = getMasterRowinfo();
      String oldDwtxid = rowinfo.get("dwtxid");
      m_RowInfo.put(req);
      String dwtxid = rowinfo.get("dwtxid");
      if(!oldDwtxid.equals(dwtxid)){
        RowMap corpRow = getCorpBean(req).getLookupRow(dwtxid);
        String deptid = corpRow.get("deptid");
        String personid = corpRow.get("personid");
        rowinfo.put("deptid", corpRow.get("deptid"));
        rowinfo.put("personid", corpRow.get("personid"));
        EngineDataSet detail = getDetailTable();
        detail.first();
        while(detail.inBounds())
        {
          String cgsqdhwid = detail.getValue("cgsqdhwid");
          if(!cgsqdhwid.equals(""))
          {
            d_RowInfos.remove(detail.getRow());
            detail.deleteRow();
          }
          else
            detail.next();
        }
      }
      else return;
    }
  }
  */
 /**
  *改变往来单位触发的事件

 class Corp_Change implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     RowMap rowinfo = getMasterRowinfo();
     m_RowInfo.put(req);
     String dwtxid = m_RowInfo.get("dwtxid");
     RowMap corpRow = getCorpBean(req).getLookupRow(dwtxid);
     String deptid = corpRow.get("deptid");
     String personid = corpRow.get("personid");
     rowinfo.put("deptid", corpRow.get("deptid"));
     rowinfo.put("personid", corpRow.get("personid"));
     EngineDataSet detail = getDetailTable();
     detail.first();
     while(detail.inBounds())
     {
       String cgsqdhwid = detail.getValue("cgsqdhwid");
       if(!cgsqdhwid.equals(""))
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
 * 显示从表的列表信息
 */
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int i= Integer.parseInt(data.getParameter("rownum"));
      int j = dsMasterTable.getRowCount();
      dsMasterTable.goToRow(i);
      masterRow = dsMasterTable.getInternalRow();
      buyCheckID = dsMasterTable.getValue("buyCheckID");
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
      isReport = false;
      isMasterAdd = String.valueOf(ADD).equals(action);
      if(!isMasterAdd)
      {
        isDetailAdd = false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        buyCheckID = dsMasterTable.getValue("buyCheckID");
      }
      else{//打开从表
        isDetailAdd = true;
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
   * 审批操作的触发类

 class Approve implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     boolean isRep = String.valueOf(REPORT).equals(action);

     HttpServletRequest request = data.getRequest();
     masterProducer.init(request, loginId);
     detailProducer.init(request, loginId);
     //得到request的参数,值若为null, 则用""代替
     String id =null;
     if(!isRep){
       isApprove = true;
       id = data.getParameter("id", "");
     }
     else{
       isReport = true;
       id = data.getParameter("buyCheckID");
     }
     String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
     dsMasterTable.setQueryString(sql);
     if(dsMasterTable.isOpen()){
       dsMasterTable.readyRefresh();
       dsMasterTable.refresh();
     }
     else
       dsMasterTable.open();

     buyCheckID = dsMasterTable.getValue("buyCheckID");
     //打开从表
     openDetailTable(false);

     initRowInfo(true, false, true);
     initRowInfo(false, false, true);
   }
 }
 */
/**
 * 添加到审核列表的操作类
 */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterTable.getValue("buyCheckID");
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "buy_order", content, dsMasterTable.getValue("deptid"));
    }
  }
  /**
   * 强制完成操作触发的类（合同被引用就不显示按钮即不能被完成）
   */
  class Complete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsMasterTable.goToRow(row);
      dsMasterTable.setValue("billType", "1");
      dsMasterTable.post();
      dsMasterTable.saveChanges();
    }
  }
  /**
   * 合同作废操作触发的类
   */
  class Repeal implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsMasterTable.goToRow(row);
      /**
     String buyCheckID=dsMasterTable.getValue("buyCheckID");
     dsDetailTable.setQueryString(DETAIL_SQL + buyCheckID);
     dsDetailTable.first();
     for(int i=0; i<dsDetailTable.getRowCount(); i++)
     {
       dsDetailTable.goToRow(i);
       String buyCheckID = dsDetailTable.getValue("buyCheckID");
       String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM cg_htjhdhw WHERE buyCheckID="+buyCheckID);
       if(!count.equals("0"))
         data.setMessage(showJavaScript("alert('该合同已被引用不能作废');"));
       dsDetailTable.next();
     }*/
      dsMasterTable.setValue("billType", "1");
       dsMasterTable.post();
       dsMasterTable.saveChanges();
    }
  }
  /**
   * 取消审批触发操作
   */
  class Cancle_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "buy_order");
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
      String buyCheckID = null;
      if(isMasterAdd){
        ds.insertRow(false);
        buyCheckID = dataSetProvider.getSequence("s_zl_buyCheck");

        ds.setValue("buyCheckID", buyCheckID);
        //ds.setValue("buycheckno", buycheckno);
        //ds.setValue("billType","1");
        //ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        //ds.setValue("czyid", loginId);
        //ds.setValue("czy", loginName);//操作员
        //ds.setValue("isXn", "0");//是否虚拟合同
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
        if(isMasterAdd)
          detail.setValue("buyCheckID", buyCheckID);
        detail.setValue("jymxID", detailrow.get("jymxID"));//检验项目ID
        detail.setValue("techRequest", detailrow.get("techRequest"));//技术要求
        detail.setValue("checkResult", detailrow.get("checkResult"));//检验结果
        detail.setValue("checkNum", detailrow.get("checkNum"));//检验数量
        detail.setValue("rejectNum", detailrow.get("rejectNum"));//不合格数
        detail.setValue("reasonID", detailrow.get("reasonID"));//原因ID
        detail.setValue("bugID", detailrow.get("bugID"));//缺陷ID
        detail.setValue("ID", detailrow.get("ID"));//检验方法ID
        detail.setValue("memo", detailrow.get("memo"));//备注
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        //totalNum = totalNum.add(detail.getBigDecimal("sl"));
        //totalSum = totalSum.add(detail.getBigDecimal("je"));
        detail.next();
      }

      //保存主表数据
      ds.setValue("buycheckno", rowInfo.get("buycheckno"));//检验单号
      ds.setValue("buyCheckDate", rowInfo.get("buyCheckDate"));//检验日期
      ds.setValue("standardID", rowInfo.get("standardID"));//检验标准ID
      ds.setValue("levelID", rowInfo.get("levelID"));//检查水平ID
      ds.setValue("rkdmxID", rowInfo.get("rkdmxID"));//入库明细单ID
      ds.setValue("billType", rowInfo.get("billType"));//单据类型 1=合同入库单,9=其他入库单
      ds.setValue("checkNum", rowInfo.get("checkNum"));//检验数量
      ds.setValue("rejectNum", rowInfo.get("rejectNum"));//不合格数
      ds.setValue("AQL", rowInfo.get("AQL"));//AQL
      ds.setValue("checkResult", rowInfo.get("checkResult"));//检验结果
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
      /** String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
        //ArrayList list = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        String cpid = detailrow.get("cpid");
        String dmsxid = detailrow.get("dmsxid");
        //StringBuffer buf = new StringBuffer().append(cpid).append(",").append(dmsxid);
        //String cpiddmsxid = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
          //if(list.contains(cpiddmsxid))
          //return showJavaScript("alert('第"+row+"行产品重复');");
          //else
          //list.add(cpiddmsxid);
        String sl = detailrow.get("sl");
        if(sl.equals(""))
          return showJavaScript("alert('第"+row+"行数量不能为空！');");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
        String dj = detailrow.get("dj");
        if((temp = checkNumber(dj, "第"+row+"行单价")) != null)
          return temp;
        String je = detailrow.get("je");
        if((temp = checkNumber(je, "第"+row+"行金额")) != null)
          return temp;
        String ybje = detailrow.get("ybje");
        if(ybje.length()>0 && (temp = checkNumber(ybje, "第"+row+"行外币金额")) != null)
          return temp;
        temp = detailrow.get("jhrq");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('第"+row+"行非法交货日期！');");
      }*/
      return null;
    }

    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("buyCheckNo");
      if(temp.equals(""))
        return showJavaScript("alert('检验单号不能为空！');");
      /*//else if(!isDate(temp))
      //return showJavaScript("alert('非法合同日期！');");
      //temp = rowInfo.get("dwtxid");
      //if(temp.equals(""))
      //return showJavaScript("alert('请选择采购单位！');");
      temp = rowInfo.get("hl");
      if(temp.length()>0 && (temp = checkNumber(temp, "汇率")) != null)
        return showJavaScript("alert('非法汇率！');");
      temp = rowInfo.get("khlx");
      if(temp.equals(""))
        return showJavaScript("alert('客户类型不能为空！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('部门不能为空！');");
      String ksrq = rowInfo.get("ksrq");
      String jsrq = rowInfo.get("jsrq");
      if(!ksrq.equals("") && !jsrq.equals("")){
      java.sql.Date ksrqDate = java.sql.Date.valueOf(ksrq);
      java.sql.Date jsrqDate = java.sql.Date.valueOf(jsrq);
      if(jsrqDate.before(ksrqDate))
        return showJavaScript("alert('合同有效期始不能大于有效期终')");
      }*/
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
      String tempSQL=null;
      table.getWhereInfo().setWhereValues(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0){
      tempSQL=combineSQL(MASTER_SQL,"?",new String[]{billType});
      SQL=tempSQL+" AND "+SQL;
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      }
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象

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
        new QueryColumn(master.getColumn("buyCheckID"), null, null, null),
        //new QueryColumn(master.getColumn("htrq"), null, null, null, "a", ">="),
        //new QueryColumn(master.getColumn("htrq"), null, null, null, "b", "<="),
        //new QueryColumn(master.getColumn("ksrq"), null, null, null, "a", ">="),
        //new QueryColumn(master.getColumn("ksrq"), null, null, null, "b", "<="),
        ///new QueryColumn(master.getColumn("jsrq"), null, null, null, "a", ">="),
        //new QueryColumn(master.getColumn("jsrq"), null, null, null, "b", "<="),
        //new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        //new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//购货单位
        //new QueryColumn(master.getColumn("qddd"), null, null, null),
        //new QueryColumn(master.getColumn("buyCheckID"), "zl_buyCheckDetail", "buyCheckID", "cpid", null, "="),//从表产品
        //new QueryColumn(master.getColumn("buyCheckID"), "VW_zl_buyCheckDetail", "buyCheckID", "product", "product", "like"),//从表产品
        //new QueryColumn(master.getColumn("buyCheckID"), "VW_zl_buyCheckDetail", "buyCheckID", "cpbm", "cpbm", "left_like"),//从表编码
        //new QueryColumn(master.getColumn("billType"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
    */
  }

  //  根据申请单从表增加操作
  /**
  class Detail_Apply_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String importapply = m_RowInfo.get("importapply");
      if(importapply.length() == 0)
        return;

        //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cgsqdhwid");
      String[] cgsqdhwID = parseString(importapply,",");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String buyCheckID = dsMasterTable.getValue("buyCheckID");
      for(int i=0; i < cgsqdhwID.length; i++)
      {
        if(cgsqdhwID[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, cgsqdhwID[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
        //LookUp look = getBuyPriceBean(req);
        //look.regData(new String[]{cgsqdhwID[i]});
          RowMap buyapplyRow = getBuyApplyBean(req).getLookupRow(cgsqdhwID[i]);
          dsDetailTable.insertRow(false);
          double dj = buyapplyRow.get("dj").length() > 0 ? Double.parseDouble(buyapplyRow.get("dj")) : 0;//单价
          double sl = buyapplyRow.get("sl").length() >0 ? Double.parseDouble(buyapplyRow.get("sl")) : 0;
          String skhtlStr = buyapplyRow.get("skhtl");
          double skhtl = buyapplyRow.get("skhtl").length() >0 ? Double.parseDouble(buyapplyRow.get("skhtl")) : 0;
          double wkhtl = sl-skhtl;
          //double wkhtl = buyapplyRow.get("wkhtl").length() > 0 ? Double.parseDouble(buyapplyRow.get("wkhtl")) : 0;
          double hl = rowInfo.get("hl").length() > 0 ? Double.parseDouble(rowInfo.get("hl")) : 0;// 汇率
          dsDetailTable.setValue("buyCheckID", "-1");
          dsDetailTable.setValue("cgsqdhwid",cgsqdhwID[i]);
          dsDetailTable.setValue("cpid",buyapplyRow.get("cpid"));
          dsDetailTable.setValue("sl", String.valueOf(wkhtl));
          dsDetailTable.setValue("dj", buyapplyRow.get("dj"));
          dsDetailTable.setValue("je", formatNumber(String.valueOf(wkhtl*dj), sumFormat));
          dsDetailTable.setValue("ybje", formatNumber(rowInfo.get("hl").length() > 0 ? String.valueOf(wkhtl*dj/hl) : "", sumFormat));
          dsDetailTable.setValue("dmsxid", buyapplyRow.get("dmsxid"));
          dsDetailTable.setValue("buyCheckID", isMasterAdd ? "" : buyCheckID);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
    }
  }
  */
 /**
  * 从表增加操作
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
      String buyCheckID = dsMasterTable.getValue("buyCheckID");
      detail.insertRow(false);
      detail.setValue("buyCheckID", isMasterAdd ? "-1" : buyCheckID);
      detail.post();
      d_RowInfos.add(new RowMap());

      /**
      String multiIdInput = m_RowInfo.get("multiIdInput");
      if(multiIdInput.length() == 0)
        return;

        //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cpid");
      String[] cpID = parseString(multiIdInput,",");
      if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
      String buyCheckID = dsMasterTable.getValue("buyCheckID");
      for(int i=0; i < cpID.length; i++)
      {
        if(cpID[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, cpID[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("buyCheckID", "-1");
          dsDetailTable.setValue("cpid", cpID[i]);
          dsDetailTable.setValue("buyCheckID", isMasterAdd ? "" : buyCheckID);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
      */
    }
  }
  /*
  *根据供应商报价资料增加从表


  class Detail_Price_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
  //保存输入的明细信息
      putDetailInfo(data.getRequest());
      if(dsBuyGoods == null)
      {
        dsBuyGoods = new EngineDataSet();
        setDataSetProperty(dsBuyGoods, null);
      }

      String select = m_RowInfo.get("select");
      if(select.length() == 0)
        return;
    */
    /**
     *选择外币触发事件
     */
  class Wb_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());

      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      String wbid = rowInfo.get("wbid");
      RowMap foreignRow = getForeignBean(req).getLookupRow(wbid);
      String hl = foreignRow.get("hl");
      rowInfo.put("wbid",wbid);
      rowInfo.put("hl",hl);
      double curhl = hl.length()>0 ? Double.parseDouble(hl) : 0 ;
      for(int j=0; j<d_RowInfos.size(); j++)
      {
        RowMap detailrow = (RowMap)d_RowInfos.get(j);
        String je = detailrow.get("je");
        double curje = isDouble(je) ? Double.parseDouble(je) : 0 ;
        detailrow.put("ybje", formatNumber(curhl==0 ? "" : String.valueOf(curje/curhl),qtyFormat));
      }
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
   * 得到申请单货物的bean
   * @param req WEB的请求
   * @return 返回申请单货物的bean

  public ImportApply getBuyApplyBean(HttpServletRequest req)
 {
   if(buyApplyBean == null)
     buyApplyBean = ImportApply.getInstance(req);
   return buyApplyBean;
  }
  */
 /**
  * 得到外币信息的bean
  * @param req WEB的请求
  * @return 返回外币信息bean
  */
  public LookUp getForeignBean(HttpServletRequest req)
  {
    if(foreignBean == null)
      foreignBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_FOREIGN_CURRENCY);
    return foreignBean;
  }
  /**
   * 得到用于查找往来单位中所属业务员和所属部门信息的bean
   * @param req WEB的请求
   * @return 返回用于查找往来单位中所属业务员和所属部门信息的bean
   */
  public LookUp getCorpBean(HttpServletRequest req)
  {
    if(corpBean == null)
      corpBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_CORP);
    return corpBean;
  }
}

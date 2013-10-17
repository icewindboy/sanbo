package engine.erp.buy.xixing;

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
import engine.erp.buy.BuyGoodsSelect;
import engine.erp.buy.ImportApply;

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
 * @author 王林川
 * @version 1.0
 */

public final class B_BuyOrder extends BaseAction implements Operate
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
  public static final String AUTO_MIN_PRICE = "2001";//填入最低报价
  public static final String MATERIAL_ON_CHANGE = "2002";//引入物资时候单入采购报价中的价格

  private static final String REFERENCED_SQL        = "select count(*) from ?  where ? ? ?";//
  private static final String MASTER_STRUT_SQL = "SELECT * FROM cg_ht WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM cg_ht where ? AND fgsid=? ? ORDER BY htbh DESC";
  private static final String NEW_MASTER_SQL    = "SELECT * FROM cg_ht where 1=1 ? ";
  private static final String DETAIL_STRUT_SQL = "SELECT a.* FROM cg_hthw a  where 1<>1";
  private static final String DETAIL_SQL = "SELECT a.* FROM cg_hthw a ,kc_dm b where a.cpid=b.cpid and a.htid='?' order by b.cpbm  ";//

  private static final String HTTK_SQL    = "SELECT itemvalue FROM jc_orderdefitem  ";//

  //判断合同货物是否被引用的SQL语句
  private static final String ORDER_RECIEVE_GOODS
      = "SELECT htid FROM (SELECT a.htid, SUM(b.sl) sl FROM cg_hthw a, cg_htjhdhw b "
      + "WHERE a.hthwid = b.hthwid AND a.htid IN (?) GROUP BY a.htid) t WHERE t.sl > 0 ";
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM cg_ht WHERE htid='?'";
  private static final String MASTER_SEARCH_SQL = "SELECT * FROM VW_CG_HTHW WHERE  ";
  //
  private static final String BUY_GOODS_SQL = "SELECT * FROM cg_bj a, kc_dm b WHERE a.cpid = b.cpid AND fgsid=? AND dwtxid = ? ORDER BY b.cpbm ";

  /**
   * 提取采购报价最低价的数据
   */
  private static final String BUY_PRICE_SQL = "SELECT b.cpid, b.dwtxid, b.bj FROM cg_bj b WHERE b.cgbjid = pck_store.getMinBuyPriceID(?) ";
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsSearchTable  = new EngineDataSet();
  private EngineDataSet dsHttkTable = new EngineDataSet();//合同条款数据集
  private EngineDataSet dsBuyPrice = null;//填入最低报价数据集


  private EngineDataSet dsCancel = new EngineDataSet();//用于判断合同是否能被作废
  private ArrayList cancelOrder = new ArrayList();

  private EngineDataSet dsBuyGoods  = new EngineDataSet();//引入供应商报价

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "cg_ht");//引入主表页面
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "cg_hthw");
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
  private ImportApply buyApplyBean = null; //采购申请单的bean的引用, 用于提取采购单价

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public String dwdm="";
  public String dwmc="";
  private String bjfs=""; //得到登陆用户报价方式的系统参数
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String htid = null;
  private String itemvalue = null;
  private String hthwid = null;
  private User user = null;
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  /**
   * 采购订单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回采购订单列表的实例
   */
  public static B_BuyOrder getInstance(HttpServletRequest request)
  {
    B_BuyOrder buyOrderBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "buyOrderBean";
      buyOrderBean = (B_BuyOrder)session.getAttribute(beanName);
      if(buyOrderBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        buyOrderBean = new B_BuyOrder();
        buyOrderBean.qtyFormat = loginBean.getQtyFormat();
        buyOrderBean.priceFormat = loginBean.getPriceFormat();
        buyOrderBean.sumFormat = loginBean.getSumFormat();

        buyOrderBean.fgsid = loginBean.getFirstDeptID();
        buyOrderBean.loginId = loginBean.getUserID();
        buyOrderBean.loginName = loginBean.getUserName();
        buyOrderBean.loginDept = loginBean.getDeptID();
        buyOrderBean.user = loginBean.getUser();
        buyOrderBean.bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");
        buyOrderBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        //设置格式化的字段
        buyOrderBean.dsDetailTable.setColumnFormat("sl", buyOrderBean.qtyFormat);
        buyOrderBean.dsDetailTable.setColumnFormat("hssl", buyOrderBean.qtyFormat);
        buyOrderBean.dsDetailTable.setColumnFormat("sjjhl", buyOrderBean.qtyFormat);
        buyOrderBean.dsDetailTable.setColumnFormat("sjrkl", buyOrderBean.qtyFormat);
        buyOrderBean.dsDetailTable.setColumnFormat("dj", buyOrderBean.priceFormat);
        buyOrderBean.dsDetailTable.setColumnFormat("je", buyOrderBean.sumFormat);
        buyOrderBean.dsMasterTable.setColumnFormat("zsl", buyOrderBean.sumFormat);
        buyOrderBean.dsMasterTable.setColumnFormat("zje", buyOrderBean.sumFormat);
        session.setAttribute(beanName, buyOrderBean);
      }
    }
    return buyOrderBean;
  }

  /**
   * 构造函数
   */
  private B_BuyOrder()
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
    setDataSetProperty(dsCancel, null);
    setDataSetProperty(dsHttkTable, null);
    dsDetailTable.setTableName("cg_hthw");
    setDataSetProperty(dsSearchTable, MASTER_SEARCH_SQL+" 1<>1");



    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"htbh"}, new String[]{"SELECT pck_base.billNextCode('cg_ht','htbh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"htbh"}, new boolean[]{true}, null, 0));
    //装载数据时得到数据集的htid判断是否能被作废，并且把htid放在ArrayList里面,该合同从表中有任何一条被引用后不能修改
    dsMasterTable.addLoadListener(new com.borland.dx.dataset.LoadListener(){
      public void dataLoaded(com.borland.dx.dataset.LoadEvent event)
      {
        cancelOrder.clear();
        if(dsMasterTable.getRowCount() == 0)
          return;
        String sql = getWhereIn(dsMasterTable, "htid", null);
        sql = combineSQL(ORDER_RECIEVE_GOODS, "?", new String[]{sql});
        dsCancel.setQueryString(sql);
        if(dsCancel.isOpen())
          dsCancel.refresh();
        else
          dsCancel.openDataSet();
        for(int i=0; i<dsCancel.getRowCount(); i++)
        {
          cancelOrder.add(dsCancel.getValue("htid"));
          dsCancel.next();
        }
        dsCancel.closeDataSet();
      }
    });

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"hthwid"}, new String[]{"s_cg_hthw"}));
    //dsDetailTable.setSort(new SortDescriptor("", new String[]{"cpbm"}, new boolean[]{false}, null, 0));

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
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(REPORT), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(DETAIL_PRICE_ADD), new Detail_Price_Add());
    addObactioner(String.valueOf(DETAIL_APPLY_ADD), new Detail_Apply_Add());
    addObactioner(String.valueOf(WB_ONCHANGE), new Wb_Onchange());
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(CANCLE_APPROVE), new Cancle_Approve());
    addObactioner(String.valueOf(COMPLETE), new Complete());
    addObactioner(String.valueOf(REPEAL), new Repeal());
    addObactioner(String.valueOf(DEPT_CHANGE), new DeptChange());
    addObactioner(String.valueOf(AUTO_MIN_PRICE), new Import_Price());//填入最低报价
    addObactioner(String.valueOf(MATERIAL_ON_CHANGE), new Import_MATERIAL_Price());//引入物资时候带入采购报价中的价格
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
      String opearate = request.getParameter(OPERATE_KEY);
      if(opearate != null && opearate.trim().length() > 0)
      {
        RunData data = notifyObactioners(opearate, request, response, null);
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
   * @param htid
   * @return
   */
  public boolean isCanCancel(String htid)
  {
    return !cancelOrder.contains(htid);
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
        cd.set(year,month+1,0);
        Date ed = cd.getTime();
        String endday =  new SimpleDateFormat("yyyy-MM-dd").format(ed);
        Date startDate = new Date();
        //Date endDate = new Date(startDate.getYear(), startDate.getMonth()+1, 0);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        //String endday = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
        m_RowInfo.put("czrq", today);//制单日期(
        m_RowInfo.put("czy", loginName);//操作员
        m_RowInfo.put("htrq", today);
        m_RowInfo.put("ksrq", today);
        m_RowInfo.put("jsrq", endday);
        m_RowInfo.put("czyid", loginId);
        m_RowInfo.put("deptid", loginDept);
        //m_RowInfo.put("khlx", "A");
        m_RowInfo.put("zt","0");
        m_RowInfo.put("httk",itemvalue);

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
      detailRow.put("cpid", rowInfo.get("cpid_"+i));
      detailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+i), qtyFormat));//
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
      detailRow.put("ybje", formatNumber(rowInfo.get("ybje_"+i), sumFormat));//原币金额
      detailRow.put("dj", formatNumber(rowInfo.get("dj_"+i), priceFormat));//单价
      detailRow.put("je", formatNumber(rowInfo.get("je_"+i), sumFormat));//金额
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
      detailRow.put("bj", rowInfo.get("bj_"+i));//标记
      detailRow.put("gyszyh", rowInfo.get("gyszyh_"+i));//供应商资源号
      detailRow.put("jhrq", rowInfo.get("jhrq_"+i));//交货日期
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));
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
    htid = dsMasterTable.getValue("htid");
    String SQL = isMasterAdd ? "-1" : htid;
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
      dwdm ="";
      dwmc ="";
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
      row.put("zt", "0");
      row.put("htrq$a", startDay);
      row.put("htrq$b", today);
      isMasterAdd = true;
      isDetailAdd = false;
      //
      String SQL = " AND zt<>8 AND zt<>4";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
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
   */
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
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterTable.getInternalRow();
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
      }
      else{//打开从表
        isDetailAdd = true;
        dsHttkTable.setQueryString(HTTK_SQL);
        if (dsHttkTable.isOpen())
          dsHttkTable.refresh();
        else
          dsHttkTable.openDataSet();
        itemvalue=dsHttkTable.getValue("itemvalue");
        int aa=1;

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
   */
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
        id = data.getParameter("htid");
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
      String content = dsMasterTable.getValue("htbh");
      String deptid = dsMasterTable.getValue("deptid");//下达车间
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "buy_order", content, deptid);

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
      dsMasterTable.setValue("zt", "8");
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
      htid = dsMasterTable.getValue("htid");
    String count = dataSetProvider.getSequence("select count(*) from cg_hthw a,cg_htjhdhw b where b.hthwid=a.hthwid and a.htid="+htid);
    if(!count.equals("0"))
    {
      data.setMessage(showJavaScript("alert('该单据已被采购到货单或采购退货单引用，不能作废，您可以删除采购到货单或采购退货单中的相应货物后再作废!')"));
      return;
     }
      /**
     String htid=dsMasterTable.getValue("htid");
     dsDetailTable.setQueryString(DETAIL_SQL + htid);
     dsDetailTable.first();
     for(int i=0; i<dsDetailTable.getRowCount(); i++)
     {
       dsDetailTable.goToRow(i);
       String hthwid = dsDetailTable.getValue("hthwid");
       String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM cg_htjhdhw WHERE hthwid="+hthwid);
       if(!count.equals("0"))
         data.setMessage(showJavaScript("alert('该合同已被引用不能作废');"));
       dsDetailTable.next();
     }*/
      dsMasterTable.setValue("zt", "4");
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
      htid = dsMasterTable.getValue("htid");
    String count = dataSetProvider.getSequence("select count(*) from cg_hthw a,cg_htjhdhw b where b.hthwid=a.hthwid and a.htid="+htid);
    if(!count.equals("0"))
    {
      data.setMessage(showJavaScript("alert('该单据已被采购到货单或采购退货单引用，不能取消审批，您可以删除采购到货单或采购退货单中的相应货物后再取消审批!')"));
      return;
     }
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
      String htid = null;
      if(isMasterAdd)
      {
        ds.insertRow(false);
        htid = dataSetProvider.getSequence("s_cg_ht");
        ds.setValue("htid", htid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("czyid", loginId);
        ds.setValue("czy", loginName);//操作员

        //ds.setValue("isXn", "0");//是否虚拟合同
      }
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      //detail.setTableName("cg_hthw");
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("htid", htid);


        //detail.setValue("cpid", detailrow.get("cpid"));
        double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(detailrow.get("dj")) : 0;//单价
        double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;
        double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;//计算金额功能
        double je = detailrow.get("je").length() > 0 ? Double.parseDouble(detailrow.get("je")) : 0;
        double hl = rowInfo.get("hl").length() > 0 ? Double.parseDouble(rowInfo.get("hl")) : 0;// 汇率
        detail.setValue("dj", detailrow.get("dj"));//
        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("je", String.valueOf(sl * (je/sl)));
        detail.setValue("sl", detailrow.get("sl"));
        detail.setValue("hssl", detailrow.get("hssl"));//保存换算数量
        detail.setValue("gyszyh", detailrow.get("gyszyh"));//供应商资源号
        detail.setValue("bj", detailrow.get("bj"));//标记
        detail.setValue("bz", detailrow.get("bz"));//备注
        detail.setValue("jhrq", detailrow.get("jhrq"));
        detail.setValue("dmsxid", detailrow.get("dmsxid"));

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
      ds.setValue("personid", rowInfo.get("personid"));//人员ID
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("hl", rowInfo.get("hl"));//汇率
      ds.setValue("ztms", rowInfo.get("ztms"));//状态描述
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));//购货单位ID
      ds.setValue("htrq", rowInfo.get("htrq"));//合同日期
      ds.setValue("ksrq", rowInfo.get("ksrq"));//合同有效期始
      ds.setValue("jsrq", rowInfo.get("jsrq"));//合同有效期终
      ds.setValue("qddd", rowInfo.get("qddd"));//签订地点
      ds.setValue("zsl", totalNum.toString());//总数量
      ds.setValue("zje", totalSum.toString());//总金额
      ds.setValue("khlx", rowInfo.get("khlx"));//客户类型
      ds.setValue("djh", rowInfo.get("djh"));//单据号
      ds.setValue("memo", rowInfo.get("memo"));//总备注
      ds.setValue("httk", rowInfo.get("httk"));//合同条款
      ds.setValue("jsfsid", rowInfo.get("jsfsid"));//结算方式
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
      ArrayList list = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        String cpid = detailrow.get("cpid");
        String dmsxid = detailrow.get("dmsxid");
        StringBuffer buf = new StringBuffer().append(cpid).append(",").append(dmsxid);
        String cpiddmsxid = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(cpiddmsxid))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(cpiddmsxid);
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
        if(temp.equals(""))
          return showJavaScript("alert('交货日期不能为空！');");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('第"+row+"行非法交货日期！');");

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
      String temp = rowInfo.get("htrq");
      if(temp.equals(""))
        return showJavaScript("alert('合同日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法合同日期！');");
      temp = rowInfo.get("dwtxid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择采购单位！');");
      temp = rowInfo.get("hl");
      if(temp.length()>0 && (temp = checkNumber(temp, "汇率")) != null)
        return showJavaScript("alert('非法汇率！');");
      temp = rowInfo.get("khlx");
      if(temp.equals(""))
        return showJavaScript("alert('客户类型不能为空！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('部门不能为空！');");
      temp=rowInfo.get("jsfsid");
      if (temp.equals(""))
        return showJavaScript("alert('结算方式不能为空！');");

      String ksrq = rowInfo.get("ksrq");
      String jsrq = rowInfo.get("jsrq");
      if(!ksrq.equals("") && !jsrq.equals("")){
        java.sql.Date ksrqDate = java.sql.Date.valueOf(ksrq);
        java.sql.Date jsrqDate = java.sql.Date.valueOf(jsrq);
        if(jsrqDate.before(ksrqDate))
          return showJavaScript("alert('合同有效期始不能大于有效期终')");
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
      HttpServletRequest request = data.getRequest();
      dwdm =request.getParameter("dwdm");
      dwmc =request.getParameter("dwmc");

      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();//得到WHERE子句
      if(SQL.length() > 0)
        SQL = " AND "+SQL;

      if(dsSearchTable.isOpen())
        dsSearchTable.close();
      setDataSetProperty(dsSearchTable,MASTER_SEARCH_SQL+user.getHandleDeptWhereValue("deptid", "czyid")+" and fgsid='"+fgsid+"'"+SQL);
      dsSearchTable.open();
      StringBuffer htids = new StringBuffer();
      HashSet hset = new HashSet();
      dsSearchTable.first();
      for(int i=0;i<dsSearchTable.getRowCount();i++)
      {
        String htid = dsSearchTable.getValue("htid");
        if(hset.add(htid))
          htids.append(dsSearchTable.getValue("htid")+",");
        dsSearchTable.next();
      }
      String sql = htids.toString();
      sql = sql.equals("")?"-1":sql+"-1";
      sql = " and htid in("+sql+")";


      SQL = combineSQL(NEW_MASTER_SQL, "?", new String[]{sql});
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
        return;//已初始化查询条件

      EngineDataSet master = dsSearchTable;
      if(!master.isOpen())
        master.open();//打开主表数据集
      //初始化固定的查询项目
      String pm = request.getParameter("pm");
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("htbh"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("htrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("htrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("ksrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("ksrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("czrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("czrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("jsrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("jsrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//购货单位
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        new QueryColumn(master.getColumn("khlx"), null, null, null, null, "="),//
        new QueryColumn(master.getColumn("qddd"), null, null, null),
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),//
        new QueryColumn(master.getColumn("czy"), null, null, null, null, "="),//
        new QueryColumn(master.getColumn("sprid"), null, null, null, null, "="),//
        new QueryColumn(master.getColumn("cpbm"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("product"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("pm"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("gg"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("sxz"), null, null, null, null, "like"),
        //new QueryColumn(master.getColumn("htbh"), "VW_CG_HTHW", "htid", "cpbm", "cpbm", "="),//
        //new QueryColumn(master.getColumn("htbh"), "VW_CG_HTHW", "htid", "pm", "pm", "="),//从表品名
        //new QueryColumn(master.getColumn("htbh"), "VW_CG_HTHW", "htid", "gg", "gg", "="),//从表规格
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;//初始化完成
    }
  }

  //  根据申请单从表增加操作

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
      String htid = dsMasterTable.getValue("htid");
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
          double hssl = buyapplyRow.get("hssl").length() >0 ? Double.parseDouble(buyapplyRow.get("hssl")) : 0;
          String skhtlStr = buyapplyRow.get("skhtl");
          double skhtl = buyapplyRow.get("skhtl").length() >0 ? Double.parseDouble(buyapplyRow.get("skhtl")) : 0;
          double wkhtl = sl-skhtl;
          //double wkhtl = buyapplyRow.get("wkhtl").length() > 0 ? Double.parseDouble(buyapplyRow.get("wkhtl")) : 0;
          double hl = rowInfo.get("hl").length() > 0 ? Double.parseDouble(rowInfo.get("hl")) : 0;// 汇率
          String a1=buyapplyRow.get("sl");
          String bb=buyapplyRow.get("hssl");
          dsDetailTable.setValue("hthwid", "-1");
          dsDetailTable.setValue("cgsqdhwid",cgsqdhwID[i]);
          dsDetailTable.setValue("cpid",buyapplyRow.get("cpid"));
          //dsDetailTable.setValue("hssl", String.valueOf(wkhtl));
          dsDetailTable.setValue("hssl", buyapplyRow.get("hssl"));
          dsDetailTable.setValue("sl", buyapplyRow.get("sl"));
          String aa=buyapplyRow.get("dj");
          dsDetailTable.setValue("dj", buyapplyRow.get("dj"));
          if(bjfs.equals("1"))
          dsDetailTable.setValue("je", formatNumber(String.valueOf(hssl*dj), sumFormat));
          else
          dsDetailTable.setValue("je", formatNumber(String.valueOf(sl*dj), sumFormat));
          dsDetailTable.setValue("dmsxid", buyapplyRow.get("dmsxid"));//规格属性
          dsDetailTable.setValue("jhrq", buyapplyRow.get("xqrq"));//进货日期
          dsDetailTable.setValue("htid", isMasterAdd ? "" : htid);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
    }
  }
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
      //detail.setTableName("CG_HTHW");
      EngineDataSet ds = getMaterTable();
      isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String htid = dsMasterTable.getValue("htid");
      detail.insertRow(false);
      //hthwid = dataSetProvider.getSequence("s_cg_hthw");
      //detail.setValue("hthwid", isMasterAdd ? "-1" : hthwid);
      detail.setValue("htid", isMasterAdd ? "-1" : htid);
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
      String htid = dsMasterTable.getValue("htid");
      for(int i=0; i < cpID.length; i++)
      {
        if(cpID[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, cpID[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("hthwid", "-1");
          dsDetailTable.setValue("cpid", cpID[i]);
          dsDetailTable.setValue("htid", isMasterAdd ? "" : htid);
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
  */

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

      //实例化查找数据集的类
      BuyGoodsSelect buyGoodsSelectBean = BuyGoodsSelect.getInstance(req);
      String[] cgbjID = parseString(select,",");
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cpid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String htid = dsMasterTable.getValue("htid");
      for(int i=0; i < cgbjID.length; i++)
      {
        if(cgbjID[i].equals("-1"))
          continue;
        RowMap priceRow = buyGoodsSelectBean.getLookupRow(cgbjID[i]);
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, cgbjID[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("hthwid", "-1");
          dsDetailTable.setValue("htid", isMasterAdd ? "" : htid);
          dsDetailTable.setValue("cpid", priceRow.get("cpid"));
          dsDetailTable.setValue("dj", priceRow.get("bj"));
          dsDetailTable.setValue("dmsxid", priceRow.get("dmsxid"));
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
    }
  }
  /**
   *选择外币触发事件
   */
  /**
   *  自动填入最低供应及报价
   */
  class Master_Buy_Price implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      /*
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      RowMap detail = null;
      RowMap prceRow = null;
      String dwtxid = m_RowInfo.get("dwtxid");
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        if(i==rownum)
        {
          detail = (RowMap)d_RowInfos.get(i);
          String cpid = detail.get("cpid");
          String dmsxid = detail.get("dmsxid");
          String bj = dataSetProvider.getSequence("select bj from cg_bj where cpid='"+cpid+"' and dwtxid='"+dwtxid+"'");
          bj=bj==null?"":bj;
          detail.put("dj", bj);
          detail.put("dmsxid", "");
        }
      }*/
    }
  }
  class Import_Price implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      RowMap detail = null;
      RowMap prceRow = null;
      String dwtxid = m_RowInfo.get("dwtxid");
      if(dwtxid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请先选取往来单位!')"));
        return;
      }
      for(int i=0; i<d_RowInfos.size(); i++)
      {
          detail = (RowMap)d_RowInfos.get(i);
          String cpid = detail.get("cpid");
          String dmsxid = detail.get("dmsxid").trim();
          double sl = detail.get("sl").length() > 0 ? Double.parseDouble(detail.get("sl")) : 0;//计算金额功能
          double hssl = detail.get("hssl").length() > 0 ? Double.parseDouble(detail.get("hssl")) : 0;//计算金额功能
          if(cpid.equals(""))
          {
            data.setMessage(showJavaScript("alert('请先选取产品!')"));
            return;
          }

          String bj = "";
          if(dmsxid.equals(""))
            bj = dataSetProvider.getSequence("select bj from cg_bj where sflsbj=0 and cpid='"+cpid+"' and dwtxid='"+dwtxid+"' and dmsxid is null");
          else
            bj = dataSetProvider.getSequence("select bj from cg_bj where sflsbj=0 and cpid='"+cpid+"' and dwtxid='"+dwtxid+"' and dmsxid ='"+dmsxid+"'");
          bj=bj==null?"":bj;
          detail.put("dj", bj);
          double djk = detail.get("dj").length() > 0 ? Double.parseDouble(detail.get("dj")) : 0;//计算金额功能
          String je="";
          if(bjfs.equals("1")){
          je=String.valueOf(djk*hssl);//计算金额功能
          }if(bjfs.equals("0")){
            je=String.valueOf(djk*sl);//计算金额功能
          }
          detail.put("je", formatNumber(je, sumFormat));//金额formatSum
          //detail.put("je",aa);//计算金额功能
          detail.put("dmsxid", "");
      }
    }
  }
/*引入物资后带入报价*/
  class Import_MATERIAL_Price implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      RowMap detail = null;
      RowMap prceRow = null;
      String dwtxid = m_RowInfo.get("dwtxid");
      if(dwtxid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请先选取往来单位!')"));
        return;
      }
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detail = (RowMap)d_RowInfos.get(i);
        String cpid = detail.get("cpid");
        String dmsxid = detail.get("dmsxid").trim();
        String dj = detail.get("dj");
        if(dj.equals(""))
        {
          if(cpid.equals(""))
          {
            data.setMessage(showJavaScript("alert('请先选取产品!')"));
            return;
        }

        String bj = "";
        if(dmsxid.equals(""))
          bj = dataSetProvider.getSequence("select bj from cg_bj where sflsbj=0 and cpid='"+cpid+"' and dwtxid='"+dwtxid+"' and dmsxid is null");
        else
          bj = dataSetProvider.getSequence("select bj from cg_bj where sflsbj=0 and cpid='"+cpid+"' and dwtxid='"+dwtxid+"' and dmsxid ='"+dmsxid+"'");
        bj=bj==null?"":bj;
        detail.put("dj", bj);
        double djk = detail.get("dj").length() > 0 ? Double.parseDouble(detail.get("dj")) : 0;//计算金额功能
        detail.put("dmsxid", "");
        }
      }
    }
  }
  class Wb_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());

      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      //String wbid = rowInfo.get("wbid");
      //RowMap foreignRow = getForeignBean(req).getLookupRow(wbid);
      //String hl = foreignRow.get("hl");
      //rowInfo.put("wbid",wbid);
      //rowInfo.put("hl",hl);
      //double curhl = hl.length()>0 ? Double.parseDouble(hl) : 0 ;
      for(int j=0; j<d_RowInfos.size(); j++)
      {
        RowMap detailrow = (RowMap)d_RowInfos.get(j);
        String je = detailrow.get("je");
        double curje = isDouble(je) ? Double.parseDouble(je) : 0 ;
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
   */
  public ImportApply getBuyApplyBean(HttpServletRequest req)
  {
    if(buyApplyBean == null)
      buyApplyBean = ImportApply.getInstance(req);
    return buyApplyBean;
  }
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
  /**
* @param table 表名或视图名
* @param field 字段名
* @param value 字段值
* @return
*/
public  boolean hasReferenced(String table,String field,String value)
{
if(table==null||table.equals(""))
return false;
if(field==null||field.equals(""))
return false;
if(value==null||value.equals(""))
return false;
String count="";
String sql = combineSQL(REFERENCED_SQL,"?",new String []{table,field,"="+value});
try
{
count = dataSetProvider.getSequence(sql);
}
catch(Exception e)
{
System.out.print("error");
return false;
}
if(!count.equals("0"))
return true;
else
return false;
  }
}
package engine.erp.quality;

import engine.action.*;
import engine.action.Operate;
import engine.common.*;
import engine.dataset.*;
import engine.html.*;
import engine.project.*;
import engine.web.observer.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;


import com.borland.dx.dataset.*;
/**
 * <p>Title: 采购--采购订单列表</p>
 * <p>Description: 采购--采购订单列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_ProductCheck extends BaseAction implements Operate
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
  public  static final String PRODUCTCHANGE = "11571";//手工输入往来单位触发事件
  public  static final String REPORT = "2000";//报表追踪触发事件
  public  static final String OTHERADD = "2004";//选择添加方式
  public  static final String PRODUCT_ADD = "1011";  //引入库单货物

  private static final String MASTER_STRUT_SQL = "SELECT * FROM zl_prodcheck WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM zl_prodcheck WHERE ? and billtype='?'";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM zl_prodcheckdetail WHERE 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM zl_prodcheckdetail WHERE productcheckid='?' ";//
  private static final String facies_strut_sql="SELECT * FROM zl_facies WHERE 1<>1";
  private static final String facies_sql="SELECT * FROM zl_facies WHERE productcheckid='?' ";
  private static final String CHECKITEM_STRUT_SQL="select b.checkitemid,b.checkitem,b.unit,b.appeal from zl_checkitem b,zl_checktype a where 1<>1";
  private static final String CHECKITEM_SQL="select b.checkitemid,b.checkitem,b.unit,b.appeal,a.wzlbid from zl_checkitem b,zl_checktype a "+
                                            "WHERE b.checktype='?' and a.checktypeid=b.checktype and a.wzlbid='?'";
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM zl_prodcheck WHERE productcheckid='?'";
  //判断合同货物是否被引用的SQL语句
  //private static final String ORDER_RECIEVE_GOODS
  //= "SELECT * FROM zl_buyCheckDetail a,zl_buyCheck b"
  //+ " WHERE a.productcheckid = b.productcheckid";
  //用于审批时候提取一条记录
  //private static final String MASTER_APPROVE_SQL = "SELECT * FROM zl_buyCheck ";//WHERE productcheckid='?'";
  //
  //private static final String BUY_GOODS_SQL = "SELECT * FROM cg_bj a, kc_dm b WHERE a.cpid = b.cpid AND fgsid=? AND dwtxid = ? ORDER BY b.cpbm ";
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsFaciesTable  = new EngineDataSet();//外观
  private EngineDataSet appendTable  = new EngineDataSet();//检验项目

  private EngineDataSet dsCancel = new EngineDataSet();//用于判断合同是否能被作废
  private ArrayList cancelOrder = new ArrayList();

  private EngineDataSet dsBuyGoods  = new EngineDataSet();//引入供应商报价

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "zl_prodcheck");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "zl_prodcheckdetail");
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态
  public boolean isReport = false;
  public  boolean isDetailAdd = false; // 从表是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private ArrayList f_RowInfos = null; //外观多行记录的引用

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
  private String filialeid = null;   //分公司ID
  private String productcheckid = null;
  private User user = null;
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public String billType= null;
  public String checkType= null;
  /**
   * 采购订单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回采购订单列表的实例
   */
  public static B_ProductCheck getInstance(HttpServletRequest request)
  {
    B_ProductCheck productBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "productBean";
      productBean = (B_ProductCheck)session.getAttribute(beanName);
      if(productBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        productBean = new B_ProductCheck();
        productBean.qtyFormat = loginBean.getQtyFormat();
        productBean.priceFormat = loginBean.getPriceFormat();
        productBean.sumFormat = loginBean.getSumFormat();
        productBean.filialeid = loginBean.getFirstDeptID();
        productBean.loginId = loginBean.getUserID();
        productBean.loginName = loginBean.getUserName();
        //productBean.filialeid = loginBean.getDeptID();
        productBean.user = loginBean.getUser();
        productBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        session.setAttribute(beanName, productBean);
      }
    }
    return productBean;
  }

  /**
   * 构造函数
   */
  private B_ProductCheck()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  public  HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "zl_prodcheck", "zl_prodcheck");//查询得到数据库中配置的字段
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsMasterTable,MASTER_STRUT_SQL);
    setDataSetProperty(dsDetailTable,DETAIL_STRUT_SQL);
    setDataSetProperty(appendTable,CHECKITEM_STRUT_SQL);
    setDataSetProperty(dsFaciesTable,facies_strut_sql);
    setDataSetProperty(dsCancel, null);
    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"productcheckno"}, new String[]{"SELECT pck_base.billNextCode('zl_bucheck','productcheckno','a') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"productcheckno"}, new boolean[]{true}, null, 0));
    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"sfdjdh"}, new String[]{"SELECT pck_base.billNextCode('kc_sfdj','sfdjdh','a') from dual"}));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"productcheckdetailid"}, new String[]{"s_zl_buyCheckDetail"}));
    dsDetailTable.setSort(new SortDescriptor("", new String[]{"productcheckdetailid"}, new boolean[]{false}, null, 0));

    dsFaciesTable.setSequence(new SequenceDescriptor(new String[]{"faciescheckid"}, new String[]{"S_zl_facies"}));
    dsFaciesTable.setSort(new SortDescriptor("", new String[]{"checkitemid"}, new boolean[]{false}, null, 0));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Billtype_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(OTHERADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(PRODUCT_ADD), new Multi_Product_Add());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(WB_ONCHANGE), new Wb_Onchange());
    addObactioner(String.valueOf(CANCLE_APPROVE), new Cancle_Approve());
    addObactioner(String.valueOf(COMPLETE), new Complete());
    addObactioner(String.valueOf(REPEAL), new Repeal());
    addObactioner(String.valueOf(DEPT_CHANGE), new DeptChange());
    addObactioner(String.valueOf(PRODUCTCHANGE), new Product_Change());
    addObactioner(String.valueOf(APPROVE), new Approve());
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
    if(dsFaciesTable != null){
      dsFaciesTable.close();
      dsFaciesTable = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    f_RowInfos = null;
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
   * @param productcheckid
   * @return
   */
  public boolean isCanCancel(String productcheckid)
  {
    return !cancelOrder.contains(productcheckid);
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
      if(!isAdd){
        m_RowInfo.put(getMaterTable());
      }
      else
      {
        Calendar  cd= new GregorianCalendar();
        int year = cd.get(Calendar.YEAR);
        int month = cd.get(Calendar.MONTH);
        cd.clear();
        Date startDate = new Date();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        m_RowInfo.put("createdate", today);//制单日期
        m_RowInfo.put("creator", loginName);//操作员
        m_RowInfo.put("creatorid",loginId);//操作员ID
        m_RowInfo.put("filialeid",filialeid);//分公司ID
      }
    }
    else
    {
      EngineDataSet dsDetail = dsDetailTable;
      if(d_RowInfos == null){
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      }
      else if(isInit)
        d_RowInfos.clear();
      dsDetail.first();
      for(int i=0; i<dsDetail.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDetail);
        d_RowInfos.add(row);
        dsDetail.next();
      }
      ////////////////////////////新增外观数据集
      EngineDataSet dsFacies =dsFaciesTable;
      if(f_RowInfos == null){f_RowInfos = new ArrayList(dsFacies.getRowCount());}
      else if(isInit)
        f_RowInfos.clear();
      int a=f_RowInfos.size();
      dsFacies.first();
      for(int i=0; i<dsFacies.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsFacies);
        f_RowInfos.add(row);
        dsFacies.next();
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
    //if(d_RowInfos.size()>0)d_RowInfos.clear();
    //保存网页的所有信息
    rowInfo.put(request);
    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("checkResult", rowInfo.get("checkResult_"+i));//检验项目ID
      detailRow.put("techRequest", rowInfo.get("techRequest_"+i));//检验结果
      detailRow.put("check_verdict", rowInfo.get("fcheck_verdict_"+i));//检验结论
      //保存用户自定义的字段
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }//faciesProducer
    int frownum = f_RowInfos.size();
    RowMap faciesRow = null;
    for(int i=0; i<frownum; i++)
    {
      faciesRow = (RowMap)f_RowInfos.get(i);
      faciesRow.put("result", rowInfo.get("result_"+i));//检验结果
      //faciesrow
      // dsFaciesTable.p
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
    String SQL = isMasterAdd ? "-1" : productcheckid;
    SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});

    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    else
      dsDetailTable.refresh();
  }
    /*得到外观表对象*/
  public final EngineDataSet getFaciesTable(){
    if(!dsFaciesTable.isOpen())
      dsFaciesTable.open();
    return dsFaciesTable;
  }
  //打开外观表
  public final void openFaciesTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : productcheckid;
    SQL = combineSQL(facies_sql, "?", new String[]{SQL});
    //SQL = combineSQL(facies_sql, "?", new String[]{"72"});
    dsFaciesTable.setQueryString(SQL);
    if(!dsFaciesTable.isOpen())
      dsFaciesTable.open();
    else
      dsFaciesTable.refresh();
  }
  /*得到检验项目表对象*/
  public final EngineDataSet getAppendTable(){
    if(!appendTable.isOpen())
      appendTable.open();
    return appendTable;
  }
  //打开检验项目表
  public final void openAppendTable(boolean isMasterAdd,String wzlbid)
  {
    String SQL = isMasterAdd ? "-1" : wzlbid;
    String SEARCHSQL=null;
    SQL = combineSQL(CHECKITEM_SQL, "?", new String[]{checkType,SQL});
    //SQL = combineSQL(CHECKITEM_SQL, "?", new String[]{SQL});
    //appendTable.deleteAllRows();
    appendTable.setQueryString(SQL);
    if(!appendTable.isOpen())
      appendTable.open();
    else
      appendTable.refresh();
  }
  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
    /*得到从表多列的信息*/
  public final RowMap[] getFaciesRowinfos() {
    RowMap[] rows = new RowMap[f_RowInfos.size()];
    f_RowInfos.toArray(rows);
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
      table.getWhereInfo().clearWhereValues();
      //String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      //String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      isMasterAdd = true;
      isDetailAdd = false;
      //String retu = doService(request, response);
      //String SQL = MASTER_SQL;//" AND billType<>8 AND billType<>4";
      //SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      String SQL=combineSQL(MASTER_SQL,"?",new String[]{user.getHandleDeptValue("deptid"),billType});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      if(dsFaciesTable.isOpen() && dsFaciesTable.getRowCount() > 0)
        dsFaciesTable.empty();
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

   // *改变产品名称触发的事件

  class Product_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      RowMap rowinfo = getMasterRowinfo();
      m_RowInfo.put(req);
      m_RowInfo.put("dmsxid","");
      m_RowInfo.put("receivedetailid","");
      //String dwtxid = m_RowInfo.get("dwtxid");
      //RowMap corpRow = getCorpBean(req).getLookupRow(dwtxid);
      //保存输入的明细信息
      engine.erp.quality.B_ProductCheck productBean=engine.erp.quality.B_ProductCheck.getInstance(req);
      dsDetailTable.deleteAllRows();
      d_RowInfos.clear();
      String wzlbid=m_RowInfo.get("wzlbid");
      productBean.openAppendTable(false,wzlbid);
      appendTable.first();
      int i=0;
      for(;i<appendTable.getRowCount();i++){
        RowMap detailrow = null;
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("productcheckid", productcheckid);
        dsDetailTable.setValue("checkitemid", appendTable.getValue("checkitemid"));
        dsDetailTable.setValue("checkitem", appendTable.getValue("checkitem"));
        dsDetailTable.setValue("unit", appendTable.getValue("unit"));
        dsDetailTable.setValue("techRequest", appendTable.getValue("appeal"));
        dsDetailTable.post();
        //创建一个与用户相对应的行
        detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
        appendTable.next();
      }
      initRowInfo(false,false,true);
    }
  }
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
      productcheckid = dsMasterTable.getValue("productcheckid");
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
        productcheckid = dsMasterTable.getValue("productcheckid");

      }
      else{//打开从表
        isDetailAdd = true;
      }
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      synchronized(dsFaciesTable){
        // openDetailTable(isMasterAdd);
        openFaciesTable(isMasterAdd);
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
       id = data.getParameter("productcheckid");
     }
     String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
     dsMasterTable.setQueryString(sql);
     if(dsMasterTable.isOpen()){
       dsMasterTable.readyRefresh();
       dsMasterTable.refresh();
     }
     else
       dsMasterTable.open();

     productcheckid = dsMasterTable.getValue("productcheckid");
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
      String content = dsMasterTable.getValue("productcheckno");
      String billName=billType.equals("2")?"product_wrapper_check":"product_film_check";
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), billName, content, dsMasterTable.getValue("deptid"));
    }
  }
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //boolean isRep = String.valueOf(REPORT).equals(action);

      HttpServletRequest request = data.getRequest();
      //masterProducer.init(request, loginId);
      //detailProducer.init(request, loginId);
      //得到request的参数,值若为null, 则用""代替
      isReport = true;
      String id = data.getParameter("id");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();
      productcheckid = dsMasterTable.getValue("productcheckid");
      isApprove = true;
      //打开从表
      openDetailTable(false);
      openFaciesTable(false);
      //initRowInfo(false, true);
      //initRowInfo(boolean isMaster, boolean isAdd, boolean isInit)
      initRowInfo(false, false, true);
      initRowInfo(false, false, true);
      initRowInfo(true, false, true);
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
    String billName=billType.equals("2")?"product_wrapper_check":"product_film_check";
    approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), billName);
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
     String productcheckid=dsMasterTable.getValue("productcheckid");
     dsDetailTable.setQueryString(DETAIL_SQL + productcheckid);
     dsDetailTable.first();
     for(int i=0; i<dsDetailTable.getRowCount(); i++)
     {
       dsDetailTable.goToRow(i);
       String productcheckid = dsDetailTable.getValue("productcheckid");
       String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM cg_htjhdhw WHERE productcheckid="+productcheckid);
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
      String productcheckid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        productcheckid = dataSetProvider.getSequence("s_zl_buyCheck");
        ds.setValue("productcheckid", productcheckid);
      }
      //保存技术指标
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      detail.first();
      int index=detail.getRowCount();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        detail.setValue("productcheckid", productcheckid);
        String checkResult=detailrow.get("checkResult");
        detail.setValue("checkResult", detailrow.get("checkResult"));//
        detail.setValue("check_verdict", detailrow.get("check_verdict"));//
        detail.post();
        detail.next();
      }
      //保存外观检验
      RowMap faciesrow = null;
      EngineDataSet facies =getFaciesTable();
      facies.first();
      int a=f_RowInfos.size();
      int b=facies.getRowCount();
      for(int i=0; i<facies.getRowCount(); i++)
      {
        faciesrow = (RowMap)f_RowInfos.get(i);
        facies.setValue("productcheckid", productcheckid);
        //facies
        String c=faciesrow.get("result");
        facies.setValue("result",faciesrow.get("result"));
        facies.post();
        facies.next();
      }

      //保存主表数据
      ds.setValue("receivedetailid", rowInfo.get("receivedetailid"));//自制收货明细单号
      ds.setValue("cpid", rowInfo.get("cpid"));//产品名称
      ds.setValue("dmsxid", rowInfo.get("dmsxid"));//规格属性
      ds.setValue("productno", rowInfo.get("productno"));//生产编号
      ds.setValue("buyCheckDate", rowInfo.get("buyCheckDate"));//抽检日期
      ds.setValue("check_num", rowInfo.get("check_num"));//抽检数量
      ds.setValue("standardid", rowInfo.get("standardid"));//检验依据
      ds.setValue("deptid", rowInfo.get("deptid"));//检验部门
      ds.setValue("personid", rowInfo.get("personid"));//检验员
      ds.setValue("check_verdict", rowInfo.get("check_verdict"));//检验结论
      ds.setValue("state", "0");//状态
      ds.setValue("creator", rowInfo.get("creator"));//制单人
      ds.setValue("creatorid",rowInfo.get("creatorid"));//制单人ID
      ds.setValue("createdate",rowInfo.get("createdate"));//制单日期
      ds.setValue("filialeid",rowInfo.get("filialeid"));//分公司ID
     // ds.setValue("remark",rowInfo.get("remark"));//备注
      String drawcode = null;
      if(isMasterAdd){
        if(billType.equals("1")){//单据号
          ds.setValue("billtype", "1");//成品膜检验报告单
          //获得单据的自动编号
          drawcode=dataSetProvider.getSequence("SELECT pck_base.billNextCode('zl_prodcheck','productcheckno','a') from dual");
          //ds.setValue("tie_in", rowInfo.get("tie_in"));//接头
        }
        else{
          ds.setValue("billtype", "2");//成品纸检验报告单
          drawcode=dataSetProvider.getSequence("SELECT pck_base.billNextCode('zl_prodcheck','productcheckno','b') from dual");
        }
      }
      ds.setValue("productcheckno",drawcode);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_QUALITY_TOOLTYPE);//同步刷新数据

      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      ds.saveDataSets(new EngineDataSet[]{ds, facies}, null);

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;

        detail.empty();
        //initRowInfo(false, true, true);//重新初始化技术指标的各行信息
        facies.empty();
        dsFaciesTable.empty();
        //dsFacies.
        initRowInfo(true, true, true);
        initRowInfo(false, true, true);//重新初始化技术指标和外观各行信息
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
        return showJavaScript("alert('技术指标不能为空')");
      for(int i=0; i<f_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)f_RowInfos.get(i);
        String result = detailrow.get("result");
        if(result.equals(""))
          return showJavaScript("alert('外观检验第"+row+"行结论不能为空！');");
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
      String buyCheckDate = rowInfo.get("buyCheckDate");
      if(buyCheckDate.length()>0&&!isDate(buyCheckDate))
        return showJavaScript("alert('非法抽检日期！');");
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
      dsFaciesTable.deleteAllRows();
      ds.saveDataSets(new EngineDataSet[]{ds, dsFaciesTable}, null);
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      d_RowInfos.clear();
      f_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }

  /**
   *  查询
   * 1=原膜进货检验报告单；0=原纸进货检验报告单
   */
  class Billtype_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
     // initQueryItem(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      //String SQL = fixedQuery.getWhereQuery();//得到WHERE子句
      String tempSQL=null;
      table.getWhereInfo().setWhereValues(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      tempSQL=combineSQL(MASTER_SQL,"?",new String[]{user.getHandleDeptValue("deptid"),billType});
      if(SQL.length() > 0){
        tempSQL=tempSQL+" AND "+SQL;
        dsMasterTable.setQueryString(tempSQL);
        dsMasterTable.setRowMax(null);
      }
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象
    **/
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
        //new QueryColumn(master.getColumn("productcheckid"), null, null, null),
        new QueryColumn(master.getColumn("productCheckNo"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("productCheckNo"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("buyCheckDate"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("buyCheckDate"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("createDate"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("createDate"), null, null, null, "b", "<="),
        //new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        //new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//购货单位
        //new QueryColumn(master.getColumn("qddd"), null, null, null),
        //new QueryColumn(master.getColumn("productcheckid"), "zl_buyCheckDetail", "productcheckid", "cpid", null, "="),//从表产品
        //new QueryColumn(master.getColumn("productcheckid"), "VW_zl_buyCheckDetail", "productcheckid", "product", "product", "like"),//从表产品
        //new QueryColumn(master.getColumn("productcheckid"), "VW_zl_buyCheckDetail", "productcheckid", "cpbm", "cpbm", "left_like"),//从表编码
        //new QueryColumn(master.getColumn("billType"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
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
      String productcheckid = dsMasterTable.getValue("productcheckid");
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
          dsDetailTable.setValue("productcheckid", "-1");
          dsDetailTable.setValue("cgsqdhwid",cgsqdhwID[i]);
          dsDetailTable.setValue("cpid",buyapplyRow.get("cpid"));
          dsDetailTable.setValue("sl", String.valueOf(wkhtl));
          dsDetailTable.setValue("dj", buyapplyRow.get("dj"));
          dsDetailTable.setValue("je", formatNumber(String.valueOf(wkhtl*dj), sumFormat));
          dsDetailTable.setValue("ybje", formatNumber(rowInfo.get("hl").length() > 0 ? String.valueOf(wkhtl*dj/hl) : "", sumFormat));
          dsDetailTable.setValue("dmsxid", buyapplyRow.get("dmsxid"));
          dsDetailTable.setValue("productcheckid", isMasterAdd ? "" : productcheckid);
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
      EngineDataSet detail =getFaciesTable();
      EngineDataSet ds = getMaterTable();
      isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String productcheckid = dsMasterTable.getValue("productcheckid");
      //String productcheckid = dsMasterTable.getValue("productcheckid");
      detail.insertRow(false);
      detail.setValue("productcheckid", isMasterAdd ? "-1" : productcheckid);
      detail.post();
      //detailrow = new RowMap(dsDetailTable);
      f_RowInfos.add(new RowMap());
      data.setMessage(showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_1');"));
      /**
      String multiIdInput = m_RowInfo.get("multiIdInput");
      if(multiIdInput.length() == 0)
        return;

       //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cpid");
      String[] cpID = parseString(multiIdInput,",");
      if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
      String productcheckid = dsMasterTable.getValue("productcheckid");
      for(int i=0; i < cpID.length; i++)
      {
        if(cpID[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, cpID[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("productcheckid", "-1");
          dsDetailTable.setValue("cpid", cpID[i]);
          dsDetailTable.setValue("productcheckid", isMasterAdd ? "" : productcheckid);
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
   *  外观删除操作
   */
  class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      try{
        putDetailInfo(data.getRequest());
        EngineDataSet ds =getFaciesTable();
        int rownum = Integer.parseInt(data.getParameter("rownum"));
        //删除临时数组的一列数据
        f_RowInfos.remove(rownum);
        ds.goToRow(rownum);
        ds.deleteRow();
        data.setMessage(showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_1');"));
      }
      catch(Exception e1){
        data.setMessage(showJavaScript("alert('"+e1.getMessage()+"')"));
      }
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
  /**
   *对应页面引入库单货物操作
   * 一次可以引入一条货物
   * */
  class Multi_Product_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      m_RowInfo.put(data.getRequest());
      String selectedtdid = req.getParameter("selectedtdid");
      String importOrder = m_RowInfo.get("selectedtdid");
      B_ReceiveSelect b_ImportOrderBean =B_ReceiveSelect.getInstance(req);
      RowMap saleRow = b_ImportOrderBean.getLookupRow(selectedtdid);
      m_RowInfo.put("cpid",saleRow.get("cpid"));
      m_RowInfo.put("receivedetailid",saleRow.get("receivedetailid"));
      //m_RowInfo.put("dmsxid","");
      //m_RowInfo.put("get_date",saleRow.get("receivedate"));
      //m_RowInfo.put("get_num",saleRow.get("sl"));
      m_RowInfo.put("wzlbid",saleRow.get("wzlbid"));
      m_RowInfo.put("dmsxid",saleRow.get("dmsxid"));
      engine.erp.quality.B_ProductCheck productBean=engine.erp.quality.B_ProductCheck.getInstance(req);
      dsDetailTable.deleteAllRows();
      d_RowInfos.clear();
      String wzlbid=m_RowInfo.get("wzlbid");
      productBean.openAppendTable(false,saleRow.get("wzlbid"));
      appendTable.first();
      int i=0;
      for(;i<appendTable.getRowCount();i++){
        RowMap detailrow = null;
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("productcheckid",productcheckid);
        dsDetailTable.setValue("checkitemid", appendTable.getValue("checkitemid"));
        dsDetailTable.setValue("checkitem", appendTable.getValue("checkitem"));
        dsDetailTable.setValue("unit", appendTable.getValue("unit"));
        dsDetailTable.setValue("techRequest", appendTable.getValue("appeal"));
        dsDetailTable.post();
        //创建一个与用户相对应的行
        detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
        appendTable.next();
      }
    }
  }
}

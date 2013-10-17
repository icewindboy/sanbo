package engine.erp.store.xixing;

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
import engine.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;



public final class B_StoreCheck extends BaseAction implements Operate
{
  public  static final String CANCEL_APPROVE = "10081";
  public  static final String COMPLETE       = "11231";
  public  static final String DELETE_BLANK   = "11581";//删除从表中库存量为空的纪录触发事件
  public  static final String DETAIL_BIG_ADD = "10002";//批量增加操作
  public  static final String DETAIL_ADD_BLANK = "11591";//从表增加一个空白行
  public  static final String STORECHANGE      = "11571";//改变仓库触发事件
  public  static final String SINGLE_PRODUCT_ADD = "10001";//单选产品触发事件
  public  static final String PRODUCT_ADD = "10011";//输入产品编码和品名规格触发事件
  public  static final String SHOW_DETAIL = "12500";//响应 调用从表明细资料 事件
  public  static final String TRANSFERSCAN = "10061";//调用盘点单 02.18 11:39 新增因为此盘点功能要新增一个盘点机功能 yjg
  public  static final String NEXT = "9999";// 新增 为上一笔,下一笔打印而加的
  public  static final String PRIOR = "9998";// 新增 为上一笔,下一笔打印而加的
  public  static final String COPYROW = "9997";
  public  static final String NEW_TRANSFERSCAN = "10062";//读新盘点机触发事件
  public  static final String TURNPAGE = "9996";// 新增 为明细表格番页而加的事件

  public  static final String IMPORT_DATA = "20000";

  private static final String MASTER_STRUT_SQL = "SELECT * FROM kc_pd WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT a.*, b.sfdjid FROM kc_pd a, kc_sfdj b WHERE a.? AND a.? AND a.pdid=b.pdid(+) AND a.fgsid=? ? ORDER BY a.rq DESC";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM kc_pdmx WHERE 1<>1";
  //2004-4-14 18:21 为了按规格属性排序而新加入的kc_dmsx c 表及条件. yjg
  private static final String DETAIL_SQL    = "SELECT a.*, b.cpbm , c.sxz FROM kc_pdmx a,kc_dm b, kc_dmsx c WHERE a.cpid = b.cpid and a.dmsxid = c.dmsxid(+) and pdid='?' order by b.cpbm, c.sxz";//
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM kc_pd WHERE pdid='?'";
  //打开物资类别的SQL
  //2004-4-26 10:18 新增 where a.cs=1  ORDER BY bm 对应解决mantis上0000421 bug:.不取物资类别明细,并且要对结果排序. yjg
  private static final String PRODUCT_SORT_SQL = "SELECT a.wzlbid, a.bm, a.mc FROM kc_dmlb a where a.cs=1 and a.isdelete = 0 ORDER BY a.bm ";
  //用于添加从表信息的SQL，根据条件和汇总表得到信息
  private static final String STOCK_COLLECT_ADD
      = "select CPID,CPBM,DMSXID,PH,FGSID,SXZ,PARENTID ,SUM(nvl(KCSL,0))kcsl,SUM(nvl(HSZL,0))hszl from vw_store_collect  ";
  //批量增加视图
  private static final String STOCK_BIG_ADD
      = "SELECT cpbm, cpid, wzlbid, chlbid, storeid FROM kc_dm a "
      +" WHERE cpid NOT IN(SELECT cpid FROM vw_store_collect WHERE ? ) AND a.isdelete = 0 AND ?  order by cpbm";
  //只取库存物资明细表中的数据
  private static final String STOCK_MATERIAL_LIST
      = " SELECT * FROM "
      + " ( "
      + "   SELECT b.storeid, b.cpid, b.dmsxid, b.ph, SUM(nvl(b.zl,0)) kcsl "
      + "   FROM kc_wzmx b, vw_kc_dm_exist a "
      + "    WHERE  b.cpid = a.cpid "
      + "    GROUP BY b.storeid, b.cpid, b.dmsxid, b.ph "
      + "  ) b "
      + "  WHERE b.kcsl <> 0 and b.storeid = '@' ";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet productSortData = new EngineDataSet();//得到物资类别数据集
  private EngineDataSet stockCollectData = null;//库存汇总的数据集
  private EngineDataSet dmsxidExistData = new EngineDataSet();//代码属性是否存在的数据集
  private EngineDataSet dsStockMaterialDetail = new EngineDataSet();//物资明细表的数据集

  public HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "kc_pd", "a");


  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "kc_pd");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "kc_pdmx");
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态
  public  boolean isDetailAdd = false; // 从表是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  private LookUp storeBean = null; //仓库信息的bean的引用, 用于提取仓库信息

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String pdid = null;
  private double childcount = 0;   //判断仓库是否含有库位
  //被分页后的数据集中某一个页面中从第几笔记录开始到第几笔数据结束.如第二页的资料范围是从第51-101笔
  public int min = 0;
  public int max = 0;
  public  String widthSyskey = "";
  public  String lengthSyskey = "";
  private User user=null;
  public static String SC_STORE_UNIT_STYLE = "1";//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  /**
   * 库存盘点单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回库存盘点单列表的实例
   */
  public static B_StoreCheck getInstance(HttpServletRequest request)
  {
    B_StoreCheck storeCheckBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "storeCheckBean";
      storeCheckBean = (B_StoreCheck)session.getAttribute(beanName);
      if(storeCheckBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        storeCheckBean = new B_StoreCheck();
        storeCheckBean.qtyFormat = loginBean.getQtyFormat();
        storeCheckBean.priceFormat = loginBean.getPriceFormat();
        storeCheckBean.sumFormat = loginBean.getSumFormat();
        SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
        storeCheckBean.widthSyskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");
        storeCheckBean.lengthSyskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_LENGTH");

        storeCheckBean.fgsid = loginBean.getFirstDeptID();
        storeCheckBean.loginId = loginBean.getUserID();
        storeCheckBean.loginName = loginBean.getUserName();
        storeCheckBean.loginDept = loginBean.getDeptID();
        storeCheckBean.user = loginBean.getUser();
        //设置格式化的字段
        storeCheckBean.dsDetailTable.setColumnFormat("zcsl", storeCheckBean.qtyFormat);
        storeCheckBean.dsDetailTable.setColumnFormat("scsl", storeCheckBean.qtyFormat);
        session.setAttribute(beanName, storeCheckBean);
      }
    }
    return storeCheckBean;
  }

  /**
   * 构造函数
   */
  private B_StoreCheck()
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
    setDataSetProperty(productSortData, null);
    setDataSetProperty(dmsxidExistData, null);
    setDataSetProperty(dsStockMaterialDetail, null);

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"pdhm"}, new String[]{"SELECT pck_base.billNextCode('kc_pd','pdhm') from dual"}));
    //dsMasterTable.setSort(new SortDescriptor("", new String[]{"pdhm"}, new boolean[]{true}, null, 0));
    dsMasterTable.setTableName("kc_pd");

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"pdmxid"}, new String[]{"s_kc_pdmx"}));
    dsDetailTable.setSort(new SortDescriptor("", new String[]{"cpbm", "sxz"}, new boolean[]{false, false}, null, 0));
    dsDetailTable.setTableName("kc_pdmx");

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_BIG_ADD), new Detail_Big_Add());//批量增加事件
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancle_Approve());
    addObactioner(String.valueOf(STORECHANGE), new Store_Onchange());
    addObactioner(String.valueOf(DETAIL_ADD_BLANK), new Detail_Add_Blank());
    addObactioner(String.valueOf(DELETE_BLANK), new Delete_Blank());
    addObactioner(String.valueOf(SINGLE_PRODUCT_ADD), new Single_Product_Add());
    addObactioner(String.valueOf(PRODUCT_ADD), new Product_Add());
    addObactioner(SHOW_DETAIL, new Show_Detail());//02.17 21:54 新增 新增查看从表明细资料事件发生时的触发操作类. yjg
    //02.18 11:41 新增 注册一个处理盘点机按钮事件的操作.此操作通过 transferScan() 类来完成.这个类也是要新增的. yjg
    addObactioner(String.valueOf(TRANSFERSCAN), new transferScan());//旧盘点机
    addObactioner(String.valueOf(NEW_TRANSFERSCAN), new transferScan());//新盘点机
    addObactioner(NEXT, new Move_Cursor_ForPrint());
    addObactioner(PRIOR, new Move_Cursor_ForPrint());
    addObactioner(COPYROW, new Copy_CurrentRow());
    addObactioner(TURNPAGE, new Turn_Page());//翻页事件
    //
    addObactioner(IMPORT_DATA, new ImportData());
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
    if(stockCollectData != null){
      stockCollectData.close();
      stockCollectData = null;
    }
    if(productSortData != null){
      productSortData.close();
      productSortData = null;
    }
    if(dmsxidExistData != null){
     dmsxidExistData.close();
     dmsxidExistData = null;
    }
    if(dsStockMaterialDetail != null){
     dsStockMaterialDetail.close();
     dsStockMaterialDetail = null;
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
        Date startDate = new Date();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        m_RowInfo.put("zdrq", today);//制单日期
        m_RowInfo.put("zdr", loginName);//操作员
        m_RowInfo.put("pdr", loginName);
        m_RowInfo.put("rq", today);
        m_RowInfo.put("zdrid", loginId);
        m_RowInfo.put("deptid", loginDept);
      }
    }
    else
    {
      EngineDataSet dsDetail = dsDetailTable;
      int l2 = dsDetail.getRowCount();
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if(isInit)
        d_RowInfos.clear();

      dsDetail.first();
      for(int i=0; i<dsDetail.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDetail);
        row.put("InternalRow", String.valueOf(dsDetail.getInternalRow()));
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

    //int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=min; i<=max; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);

      String cpid = rowInfo.get("cpid_"+i);
      String cpbm = rowInfo.get("cpbm_"+i);
      String dmsxid = rowInfo.get("dmsxid_"+i);
      String ph = rowInfo.get("ph_"+i);
      String scsl = rowInfo.get("scsl_"+i);
      String hssl = rowInfo.get("hssl_"+i);
      String zchssl = rowInfo.get("zchssl_"+i);
      String kwid = rowInfo.get("kwid_"+i);

      detailRow.put("cpid", cpid);
      detailRow.put("cpbm", cpbm);
      detailRow.put("ph", ph);//批号
      detailRow.put("dmsxid", dmsxid);//规格属性 02.14新增为配合页面上新增此字段而新增长的.
      detailRow.put("scsl", formatNumber(scsl, qtyFormat));//实存数量
      detailRow.put("hssl", formatNumber(hssl, qtyFormat));//实存数量
      detailRow.put("zchssl", formatNumber(zchssl, qtyFormat));//实存数量
      detailRow.put("kwid", kwid);//
      //保存用户自定义的字段
      /*
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
      */
    }
  }
  /**
   * 从表增加一个空白行后单选产品增加操作
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
     String storeid = m_RowInfo.get("storeid");
     if(storeid.equals("")){
       data.setMessage(showJavaScript("alert('请先选择仓库')"));
       return;
     }
     String SQL = STOCK_COLLECT_ADD + " WHERE cpid='"+cpid+"' AND( storeid='" + storeid+"' or storeid is null) GROUP  BY CPID,CPBM,DMSXID,PH,FGSID,SXZ,PARENTID ";
     //根据所选择的产品iD和仓库id在库存汇总表中得到帐存数量
     if(stockCollectData == null)
      {
        stockCollectData = new EngineDataSet();
        setDataSetProperty(stockCollectData, null);
      }
      stockCollectData.setQueryString(SQL);
      if(!stockCollectData.isOpen())
        stockCollectData.openDataSet();
      else
        stockCollectData.refresh();

     String zcsl=null;//帐存数量
     String hszl=null;//帐存换算数量
     if(stockCollectData.getRowCount()<1)
       zcsl="0";
     else
     {
       zcsl = stockCollectData.getValue("kcsl");
       hszl = stockCollectData.getValue("hszl");
     }
     if(!isMasterAdd)
       dsMasterTable.goToInternalRow(masterRow);
     String pdid = dsMasterTable.getValue("pdid");
     dsDetailTable.goToRow(row);
     RowMap detailrow = null;
     detailrow = (RowMap)d_RowInfos.get(row);
     detailrow.put("pdmxid", "-1");
     detailrow.put("cpid", cpid);
     detailrow.put("zcsl", zcsl);//2004-3-30 10:05 修改 Single_Product_Add操作中修改zcsl为空 yjg
     detailrow.put("zchssl", hszl);
     detailrow.put("bz", "0");
     detailrow.put("pdid", isMasterAdd ? "" : pdid);
    }
  }
  /**
   * 从表输入产品编码和品名规格触发事件
   */
  class Product_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
     HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      int row = Integer.parseInt(data.getParameter("rownum"));
      RowMap detailrow = null;
      detailrow = (RowMap)d_RowInfos.get(row);
      String cpid = detailrow.get("cpid");
      String storeid = m_RowInfo.get("storeid");
      if(storeid.equals("")){
        data.setMessage(showJavaScript("alert('请先选择仓库')"));
        return;
      }
      String SQL = STOCK_COLLECT_ADD + " WHERE cpid='"+cpid+"' AND (storeid='" + storeid+"'  or storeid is null) GROUP  BY CPID,CPBM,DMSXID,PH,FGSID,SXZ,PARENTID";
      //根据所选择的产品iD和仓库id在库存汇总表中得到帐存数量
      if(stockCollectData == null)
      {
        stockCollectData = new EngineDataSet();
        setDataSetProperty(stockCollectData, null);
      }
      stockCollectData.setQueryString(SQL);
      if(!stockCollectData.isOpen())
        stockCollectData.openDataSet();
      else
        stockCollectData.refresh();

      String zcsl=null;//帐存数量
      if(stockCollectData.getRowCount()<1)
        zcsl="0";
      else
        zcsl = stockCollectData.getValue("kcsl");
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cpid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String pdid = dsMasterTable.getValue("pdid");
      dsDetailTable.goToRow(row);
      locateGoodsRow.setValue(0, cpid);
      if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
      {
        detailrow.put("pdmxid", "-1");
        detailrow.put("cpid", cpid);
        detailrow.put("zcsl", "0");
        detailrow.put("bz", "0");//标志为零表示可以修改
        detailrow.put("pdid", isMasterAdd ? "" : pdid);
      }
    }
  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }
  /*得到物资类别表对象*/
  public final EngineDataSet getProductSortTable()
  {
    return productSortData;
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
    //02.17 10:58 修改 修改原来的三元运算符最后的pdid值改为现在的从 dsMasterTable.getValue("pdid")
    //中取出来.即原来是这样的. ( isMasterAdd ? "-1" : pdid ),而现在改成下面这样. yjg
    //String SQL = DETAIL_SQL + (isMasterAdd ? "-1" : dsMasterTable.getValue("pdid"));
    String SQL = combineSQL(DETAIL_SQL, "?", new String[]{(isMasterAdd ? "-1" : dsMasterTable.getValue("pdid"))});

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
   * 得到用于查找仓库信息的bean
   * @param req WEB的请求
   * @return 返回用于仓库信息的bean
   */
  public LookUp getStoreBean(HttpServletRequest req)
  {
    if(storeBean == null)
      storeBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_STORE);
    return storeBean;
  }
  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      table.getWhereInfo().clearWhereValues();
      /**
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("zt", "0");
      row.put("rq$a", startDay);
      row.put("rq$b", today);
      */
      isMasterAdd = true;
      isDetailAdd = false;
      //
      String SQL = " AND a.zt<>9 ";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptValue(), user.getHandleStoreValue(),fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
    }
  }

  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      productSortData.setQueryString(PRODUCT_SORT_SQL);
      if(!productSortData.isOpen())
        productSortData.open();//打开物资类别数据集
      isApprove = false;
      isMasterAdd = String.valueOf(ADD).equals(action);
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        pdid = dsMasterTable.getValue("pdid");
      }
      //02.18 16:45 新增 同步子表 yjg
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

     pdid = dsMasterTable.getValue("pdid");
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
     String content = dsMasterTable.getValue("pdhm");
     //03.16 15:04 修改 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
     //以实现:根据下达的部门，进行提交审批；
     approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "store_check", content, dsMasterTable.getValue("deptid"));
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
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "store_check");
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
      String storeid = rowInfo.get("storeid");
      //2004-4-9 11:06 新加入了判断库位如果有设的话那么就必须指定库位. 其程序中其它地方的相关的新增 yjg
      RowMap storeRow = null;
      if(!storeid.equals("")){
        storeRow = getStoreBean(data.getRequest()).getLookupRow(storeid);
        childcount = storeRow.get("childcount").length()>0 ? Double.parseDouble(storeRow.get("childcount")) : 0;
      }
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
      String pdid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        pdid = dataSetProvider.getSequence("s_kc_pd");
        ds.setValue("pdid", pdid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      //detail.first();
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        long l_row = Long.parseLong(detailrow.get("InternalRow"));
        detail.goToInternalRow(l_row);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("pdid", pdid);

        //double hsbl = detailrow.get("hsbl").length() > 0 ? Double.parseDouble(detailrow.get("hsbl")) : 0;//换算比例
        double scsl = detailrow.get("scsl").length() > 0 ? Double.parseDouble(detailrow.get("scsl")) : 0;
        double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;//换算数量
        //计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
        //if (SC_STORE_UNIT_STYLE.equals("1"))
        //  detail.setValue("hssl", String.valueOf(hsbl==0 ? 0 : scsl/hsbl));//保存换算数量
        // else
        detail.setValue("hssl", detailrow.get("hssl"));//保存换算数量
        //detail.setValue("hssl", String.valueOf(hsbl==0 ? 0 : scsl/hsbl));//保存换算数量
        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("ph", detailrow.get("ph"));
        detail.setValue("zcsl", detailrow.get("zcsl").equals("")?"0":detailrow.get("zcsl"));
        detail.setValue("scsl", detailrow.get("scsl").equals("")?"0":detailrow.get("scsl"));
        detail.setValue("zchssl",detailrow.get("zchssl").equals("")?"0":detailrow.get("zchssl"));
        //detail.setValue("hssl", detailrow.get("hssl"));
        /*02-14 15:16 新增dmsxID物资规格属性*/
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("kwid", detailrow.get("kwid"));
        //保存用户自定义的字段
        /*
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        */
        detail.post();
        //detail.next();
      }
      initRowInfo(false, false, true);
      //保存主表数据
      ds.setValue("storeid", rowInfo.get("storeid"));//仓库ID
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("rq", rowInfo.get("rq"));//日期
      ds.setValue("pdr", rowInfo.get("pdr"));//盘点人
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      detail.getColumn("cpbm").setResolvable(false);
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      //int kk = detail.getRowCount();
      //int kc = detail.getRowCount();
      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action)){
        isMasterAdd = false;
        masterRow = ds.getInternalRow();//2004-3-30 14:53 新增 如果是保存按钮行为完成后定位数据集.目地是:实现保存不返回 yjg
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
      String cpid = new String();
      String ph = new String();
      String dmsxid  = new String();
      //为验证产品编码  批号 规格属性组合 是否有重复而设置的.02.14 yjg
      ArrayList list = new ArrayList(d_RowInfos.size());
      StringBuffer buf = new StringBuffer();
      String combinStr = new String();
      String hssl = new String();
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        ph = detailrow.get("ph");
        dmsxid = detailrow.get("dmsxid");
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");

        /*
        String zcsl = detailrow.get("zcsl");
        if((temp = checkNumber(zcsl, "第"+row+"行帐存数量")) != null)
          return temp;
        String scsl = detailrow.get("scsl");
        if((temp = checkNumber(scsl, "第"+row+"行实存数量")) != null)
          return temp;
        String zchssl = detailrow.get("zchssl");
        if((temp = checkNumber(zchssl, "第"+row+"行实存数量")) != null&&!zchssl.equals(""))
         return temp;

        hssl = detailrow.get("hssl");
        if((temp = checkNumber(hssl, "第"+row+"行换算数量")) != null&&!hssl.equals(""))
         return temp;

        */
        //2004-4-9 11:06 新加入了判断库位如果有设的话那么就必须指定库位. 其程序中其它地方的相关的新增 yjg
        String kwid = detailrow.get("kwid");
        if(childcount>0 && kwid.equals(""))
          return showJavaScript("alert('"+row+"行库位不能为空')");
        //将每一行从页面上读取出来的此三个值相连. 02.14 yjg
        buf.append(cpid).append(",");
        buf.append(ph).append(",");
        buf.append(dmsxid);
        combinStr = buf.toString();
        // 新增 :如果cpid, ph, dmsxid的组合有相同的则是不允许的.盘点点中不允许有这样的情况. 02.14 21:19 yjg
        if(list.contains(combinStr))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(combinStr);
        buf.delete(0, buf.length());
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
      String temp = rowInfo.get("rq");
      if(temp.equals(""))
        return showJavaScript("alert('盘点日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法盘点日期！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择部门！');");
      temp = rowInfo.get("storeid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择仓库！');");
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
   * 删除库存量为空白的操作
   */
  class Delete_Blank implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      //EngineDataSet detail = getDetailTable();
      //while(detail.inBounds())
      for(int i=0; i< d_RowInfos.size(); i++)
      {
        RowMap detailrow = (RowMap)d_RowInfos.get(i);
        String scsl = detailrow.get("scsl");
        String zcsl = detailrow.get("zcsl");
        //02.25 11:14 修改 修改删除一行库存量为空的一笔记录的条件改为 电脑库存数量, 实际库存数量全部都是零的才删除 yjg
        //03.23 11:36 修改 将上方的02.25 11:14 bug中所做的修改的基础上再修改:现在又改回了:
        //                删除一行库存量为空的一笔记录的条件改为:实际库存数量是空的. yjg
        if(scsl.equals(""))
        {
          dsDetailTable.goToRow(i);
          d_RowInfos.remove(i);
          dsDetailTable.deleteRow();
          i--;
        }
      }
    }
  }

  /**
   *  查询操作
   */
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //initQueryItem(data.getRequest());
      table.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptValue(), user.getHandleStoreValue(),fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
    }
  }
  /**
   *  改变仓库触发的事件
   */
  class Store_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(req);
      int rowcount = d_RowInfos.size();
      if(rowcount>0){
        dsDetailTable.deleteAllRows();
        d_RowInfos.clear();
      }
    }
  }
  /**
   * 从表增加一个空白行操作
   * */
  class Detail_Add_Blank implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      isDetailAdd = String.valueOf(DETAIL_ADD_BLANK).equals(action);
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String pdid = dsMasterTable.getValue("pdid");

      detail.insertRow(false);
      detail.setValue("pdid", isMasterAdd ? "-1" : pdid);
      detail.setValue("zcsl", "0");
      detail.setValue("zchssl", "0");
      detail.post();
      RowMap detailRow = new RowMap(detail);
      detailRow.put("InternalRow", String.valueOf(detail.getInternalRow()));
      d_RowInfos.add(detailRow);
    }
  }
  /**
   * 根据条件从表增加操作
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowinfo = getMasterRowinfo();
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      detail.deleteAllRows();
      d_RowInfos.clear();
      String upstoreid = rowinfo.get("storeid");
      String storeid = req.getParameter("add_storeid");
      if(storeid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择仓库')"));
          return;
      }
      if(!storeid.equals(upstoreid))
      {
        data.setMessage(showJavaScript("alert('请选择相同的仓库')"));
          return;
      }
      String scope  = req.getParameter("scope");//得到选择添加方式
      String chlbid = req.getParameter("chlbid");//得到存货类别ID
      String cpbm_a = req.getParameter("cpbm_a");
      String cpbm_b = req.getParameter("cpbm_b");
      //String sxz_a  = req.getParameter("sxz_a");
      //String sxz_b  = req.getParameter("sxz_b");
      String[] sel  = req.getParameterValues("sel");//得到所选择物资大类别ID数组
      String SQL    = null;
      if(scope.equals("1"))//选择查询条件为所有存货
        SQL = STOCK_COLLECT_ADD + " WHERE (storeid='" +storeid+"'  or storeid is null) GROUP  BY CPID,CPBM,DMSXID,PH,FGSID,SXZ,PARENTID";
      //查询条件为按存货类别筛选
      if(scope.equals("2")){
        if(chlbid.equals("")){
          data.setMessage(showJavaScript("alert('请选择存货类别')"));
          data.setMessage(showJavaScript("showFixedQuery()"));
          return;
        }
        SQL = STOCK_COLLECT_ADD + " WHERE (storeid='" +storeid+"'  or storeid is null) AND chlbid='" + chlbid+"' GROUP  BY CPID,CPBM,DMSXID,PH,FGSID,SXZ,PARENTID";
      }
      //按产品编码范围
      if(scope.equals("3")){
        if(cpbm_a.equals("") && cpbm_b.equals(""))
        {
          data.setMessage(showJavaScript("alert('产品编码范围不能为空')"));
          data.setMessage(showJavaScript("showFixedQuery()"));
          return;
        }
        //从页面上取得的cpbm中分解出来宽度值.因为在这里的页面上为了实现宽度查询,允许他像老编码一样有cpbm中有宽度后缀
        //sxz_a = cpbm_a.length()>7?cpbm_a.substring(7):"";
        //sxz_b = cpbm_b.length()>7?cpbm_b.substring(7):"";
        //分解出产品编码
        /*cpbm_a = cpbm_a.length()>6?cpbm_a.substring(0,7):cpbm_a;
        cpbm_b = cpbm_b.length()>6?cpbm_b.substring(0,7):cpbm_b;
        */
        if(!cpbm_a.equals("") && !cpbm_b.equals(""))
          SQL = STOCK_COLLECT_ADD + " WHERE (storeid='" +storeid+"'  or storeid is null) AND cpbm>='" + cpbm_a+"' AND cpbm<='"+cpbm_b+"' GROUP  BY CPID,CPBM,DMSXID,PH,FGSID,SXZ,PARENTID";
        if(cpbm_a.equals("") && !cpbm_b.equals(""))
          SQL = STOCK_COLLECT_ADD + " WHERE (storeid='" +storeid+"'  or storeid is null) AND cpbm<='"+cpbm_b+"' GROUP  BY CPID,CPBM,DMSXID,PH,FGSID,SXZ,PARENTID";
        if(!cpbm_a.equals("") && cpbm_a.equals(""))
          SQL = STOCK_COLLECT_ADD + " WHERE (storeid='" +storeid+"'  or storeid is null) AND cpbm>='"+cpbm_a+"' GROUP  BY CPID,CPBM,DMSXID,PH,FGSID,SXZ,PARENTID";
      }
      //选择物资大类
      if(scope.equals("4"))
      {
        if(sel.length<1)
        {
          data.setMessage(showJavaScript("alert('请选择物资大类')"));
          data.setMessage(showJavaScript("showFixedQuery()"));
          return;
        }
        //循环数组组装SQL
        String sql = "";
        for(int j=0; j<sel.length; j++)
        {
          if(j==sel.length)
            continue;
          String wzlbid = sel[j];
          if(j==sel.length-1)
            sql +=wzlbid + "";
          else
            sql += wzlbid + ",";
        }
        //2004-08-31  修改 加入了parentid in (sql) yjg
        SQL = STOCK_COLLECT_ADD + " WHERE (storeid='" +storeid+"'  or storeid is null) AND ( wzlbid IN("+sql+") or parentid in (" + sql +") ) GROUP  BY CPID,CPBM,DMSXID,PH,FGSID,SXZ,PARENTID";
      }
      if(stockCollectData == null)
      {
        stockCollectData = new EngineDataSet();
        setDataSetProperty(stockCollectData, null);
      }
      System.out.println(SQL);
      stockCollectData.setQueryString(SQL);
      if(!stockCollectData.isOpen())
        stockCollectData.openDataSet();
      else
        stockCollectData.refresh();

      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String pdid = dsMasterTable.getValue("pdid");
      m_RowInfo.put("storeid", storeid);
      stockCollectData.first();
      String tmpSQLSxz = "";
      String tmpInputSxz = "";
      //宽度上下限条件
      boolean lower_limit = true;
      boolean upper_limit = true;
      boolean SQLSxzCondition = true;
      for(int i=0; i<stockCollectData.getRowCount(); i++)
      {
        stockCollectData.goToRow(i);
        tmpSQLSxz = stockCollectData.getValue("sxz");
        //分解从sql中取出来的sxz.分解成只取出它的宽度数值来.
        tmpSQLSxz = parseEspecialString(tmpSQLSxz, "()", false);
        detail.insertRow(false);
        detail.setValue("pdid", isMasterAdd ? "-1" : pdid);
        detail.setValue("pdmxid", "-1");
        detail.setValue("bz", "0");//标志为0能修改
        detail.setValue("cpid", stockCollectData.getValue("cpid"));
        detail.setValue("ph", stockCollectData.getValue("ph"));
        detail.setValue("zcsl", stockCollectData.getValue("kcsl"));
        detail.setValue("zchssl", stockCollectData.getValue("hszl"));
        //2004-3-29 23:43 新增 dmsxid yjg
        detail.setValue("dmsxid", stockCollectData.getValue("dmsxid"));
        detail.post();
        //RowMap detailrow = new RowMap(detail);
        //d_RowInfos.add(detailrow);
      }
      initRowInfo(false,false,true);
    }
  }
  /**
   * 根据条件从表批量增加操作（增加在物资明细中没有库存的物资）
   */
  class Detail_Big_Add implements Obactioner
  {
    private EngineDataSet dsproduct = null;
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowinfo = getMasterRowinfo();
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      String upstoreid = rowinfo.get("storeid");
      String storeid = req.getParameter("add_storeid");
      if(storeid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择仓库')"));
          return;
      }
      if(!storeid.equals(upstoreid))
      {
        data.setMessage(showJavaScript("alert('请选择相同的仓库')"));
          return;
      }
      String scope = req.getParameter("scope");//得到选择添加方式
      String chlbid = req.getParameter("chlbid");//得到存货类别ID
      String cpbm_a = req.getParameter("cpbm_a");
      String cpbm_b = req.getParameter("cpbm_b");
      String[] sel = req.getParameterValues("sel");//得到所选择物资大类别ID数组
      String SQL = null;
      if(scope.equals("1"))//选择查询条件为所有存货
      {
        String s = "storeid='"+storeid + "'";
        SQL = combineSQL(STOCK_BIG_ADD, "?", new String[]{s,s});
      }
      //查询条件为按存货类别筛选
      if(scope.equals("2")){
        if(chlbid.equals("")){
          data.setMessage(showJavaScript("alert('请选择存货类别')"));
          data.setMessage(showJavaScript("showFixedQuery()"));
          return;
        }
        String t = "(storeid is null or storeid='" +storeid+"') AND chlbid='"+ chlbid+"'";
        SQL = combineSQL(STOCK_BIG_ADD, "?", new String[]{t,t});
      }
      //按产品编码范围
      if(scope.equals("3")){
        if(cpbm_a.equals("") && cpbm_b.equals(""))
        {
          data.setMessage(showJavaScript("alert('产品编码范围不能为空')"));
          data.setMessage(showJavaScript("showFixedQuery()"));
          return;
        }
        String f =null;
        if(!cpbm_a.equals("") && !cpbm_b.equals(""))
           f = "(storeid is null or storeid='" +storeid+"') AND cpbm>='"+cpbm_a+"'AND cpbm<='"+cpbm_b+"'";
        if(cpbm_a.equals("") && !cpbm_b.equals(""))
          f = "(storeid is null or storeid='" +storeid+"') AND cpbm<='"+cpbm_b+"'";
        if(!cpbm_a.equals("") && cpbm_a.equals(""))
          f = "(storeid is null or storeid='" +storeid+"') AND cpbm>='"+cpbm_a+"'";
         SQL = combineSQL(STOCK_BIG_ADD, "?", new String[]{f,f});
      }
      //选择物资大类
      if(scope.equals("4"))
      {
        if(sel.length<1)
        {
          data.setMessage(showJavaScript("alert('请选择物资大类')"));
          data.setMessage(showJavaScript("showFixedQuery()"));
          return;
        }
        //循环数组组装SQL
        String sql = "";
        for(int j=0; j<sel.length; j++)
        {
          if(j==sel.length)
            continue;
          String wzlbid = sel[j];
          if(j==sel.length-1)
            sql +=wzlbid + "";
          else
            sql += wzlbid + ",";
        }
        String g = "(storeid is null or storeid='" +storeid+"') AND wzlbid IN("+sql+")";
        SQL = combineSQL(STOCK_BIG_ADD, "?", new String[]{g,g});
      }
      if(dsproduct == null)
      {
        dsproduct = new EngineDataSet();
        setDataSetProperty(dsproduct, null);
      }
      dsproduct.setQueryString(SQL);
      if(!dsproduct.isOpen())
        dsproduct.openDataSet();
      else
        dsproduct.refresh();

      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String pdid = dsMasterTable.getValue("pdid");
      m_RowInfo.put("storeid", storeid);
      dsproduct.first();
      for(int i=0; i<dsproduct.getRowCount(); i++)
      {
        dsproduct.goToRow(i);
        detail.insertRow(false);
        detail.setValue("pdid", isMasterAdd ? "-1" : pdid);
        detail.setValue("pdmxid", "-1");
        detail.setValue("bz", "0");//标志为0能修改
        detail.setValue("zcsl", "0");
        detail.setValue("cpid", dsproduct.getValue("cpid"));
        detail.setValue("cpbm", dsproduct.getValue("cpbm"));
        detail.post();
        //RowMap detailrow = new RowMap(detail);
        //d_RowInfos.add(detailrow);
      }
      initRowInfo(false,false,true);
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
      RowMap detailrow = (RowMap)d_RowInfos.get(rownum);
      long l_row = Long.parseLong(detailrow.get("InternalRow"));
      //删除临时数组的一列数据
      d_RowInfos.remove(rownum);
      ds.goToInternalRow(l_row);
      ds.deleteRow();
    }
  }

  //02.17 22:40 新增 新增一个当页面上单击事件发生时想要查看明细资料 这个单击事件的Show_Detail类 . yjg
  /**
  * 显示从表的列表信息
  */
 class Show_Detail implements Obactioner
 {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterTable.getInternalRow();
      //打开从表
      openDetailTable(false);
    }

  }

  //02.18 11:45 新增 新增这个类来处理盘点机事件时要做的操作. yjg
  /**
   * 调用盘点单触发的事件
   * 盘点机做的操作为:
   * 1.盘点机操作首先把符合指定条件的库存物资明细表中的数据全部取过来作为盘点数据记录放到dsDetailTable中用为盘点数据
   * 2.接着比较:
   *       2.1 如果盘点机机器中读入的一条数据能在1.操作中取到的库存物资明细表中的数据定位到.那么,就把这笔设置为盘点数据的scsl设为物资明细的kcsl
   *        2.2 如果盘点机机器中读入的一条数据在库存物资明细表中的数据中定位不到那么还是把这笔盘点机器中的数据插入dsDetailTable中.但scsl设置为0
   *3.保存数据.
   */
   class transferScan implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       HttpServletRequest req = data.getRequest();
       //保存输入的明细信息
       putDetailInfo(data.getRequest());
       //清空明细表dataset和rowinfo中所有数据集
       dsDetailTable.deleteAllRows();
       d_RowInfos.clear();
       //2004-4-23 11:06 新增 为新编码的盘点机而加 yjg
       //boolean isNew = String.valueOf(NEW_TRANSFERSCAN).equals(action);
       RowMap rowinfo = getMasterRowinfo();
       String storeid = rowinfo.get("storeid");
       String scanValue= req.getParameter("scanValue");//得到包含产品编码和批号的字符串
       String[][] s=engine.util.StringUtils.getArrays(scanValue);
       String[] cpbms = s[0];//产品编码数组
       String[] phStr   = s[1];//批号数组
       ArrayList dmsxids = new  ArrayList(cpbms.length);
       ArrayList cpids = new  ArrayList(cpbms.length);
       ArrayList phs = new  ArrayList(cpbms.length);
       LookUp prodCodeBean = LookupBeanFacade.getInstance(req,SysConstant.BEAN_PRODUCT_CODE);
       prodCodeBean.regData(cpbms);
       //取总共执行几次sql
       //int total = cpbms.length/10 + (cpbms.length%10 > 0 ? 1 : 0);
       //int i = 0;//用来记录cpids数组size实际的大小.
       for(int i=0; i<cpbms.length; i++)
       {
         String cpbm = cpbms[i].length()>6?cpbms[i].substring(0, 7):cpbms[i];//2004-4-2 23:12 修改 暂时修改取产品编码前七位 yjg
         RowMap prodCodeRow = prodCodeBean.getLookupRow(cpbm);
         String cpid = prodCodeRow.get("cpid");
         String p_storeid = prodCodeRow.get("storeid");
         if(cpid.equals("") || (!p_storeid.equals(storeid) && !p_storeid.equals("")))
           continue;
         //2004-4-23 11:06 修改 为新编码的盘点机而修改新增 yjg
         String dmsxid = "";
         boolean isNew = cpbms[i].indexOf("-") >-1;
         if(isNew)
         {
           String[] dmsxidArray = parseString(cpbms[i],"-");
           if (dmsxidArray.length < 2 )
             continue;
           dmsxid = dmsxidArray[1];
         }
         else
           dmsxid = getDmsxId(cpid, cpbms[i]);//取得代码属性id
         cpids.add(cpid);
         dmsxids.add(dmsxid);
         phs.add(phStr[i]);
       }
     String sql = combineSQL(STOCK_MATERIAL_LIST, "@", new String[]{storeid});
     dsStockMaterialDetail.setQueryString(sql);
     if(dsStockMaterialDetail.isOpen())
       dsStockMaterialDetail.refresh();
     else
       dsStockMaterialDetail.openDataSet();

     for (int i=0;i<dsStockMaterialDetail.getRowCount();i++)
     {
       String kcsl = dsStockMaterialDetail.getValue("kcsl");
       dsDetailTable.insertRow(false);
       dsDetailTable.setValue("cpid", dsStockMaterialDetail.getValue("cpid"));
       dsDetailTable.setValue("ph", dsStockMaterialDetail.getValue("ph"));
       dsDetailTable.setValue("zcsl", kcsl);
       dsDetailTable.setValue("scsl", "0");
       dsDetailTable.setValue("dmsxid", dsStockMaterialDetail.getValue("dmsxid"));
       dsDetailTable.post();
       dsStockMaterialDetail.next();
     }
     dsStockMaterialDetail.closeDataSet();

     EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[]{"cpid", "ph", "dmsxid"});
     String zlCardScsl = "0";//用来保存质量合格证上的数量
     String zlCardSql = "";  //取质量合格证上的数量的sql
     for(int i=0; i<cpids.size(); i++)
     {
       String cpid = (String)cpids.get(i);
       String ph = (String)phs.get(i);
       String dmsxid = (String)dmsxids.get(i);
       String kcsl = "0";
       locateGoodsRow.setValue(0, cpid);
       locateGoodsRow.setValue(1, ph);
       locateGoodsRow.setValue(2, dmsxid);
       //如果盘点机中的数据在库存物资明细里定位到了
       //那么就把这笔库存物资明细中的对应记录的scsl设置为它的库存物资明细中的kcsl
       if(dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
       {
         kcsl = dsDetailTable.getValue("zcsl");
         dsDetailTable.setValue("scsl", kcsl);//如果盘点机中的数据在库存物资明细中的定位到了那么盘点实存数量scsl设为物资明细中的kcsl
         dsDetailTable.post();
       }
       else//如果盘点机中的数据没有定位到那么就插入一笔.但此时scsl去合格证上去取.
       {
         zlCardSql  = " SELECT saleNum FROM zl_certifiedCard WHERE cpid='@' and dmsxid= '@' and batNo = '@' ";
         zlCardSql  = combineSQL(zlCardSql, "@", new String[]{cpid, dmsxid, ph});
         zlCardScsl =  dataSetProvider.getSequence(zlCardSql);
         zlCardScsl = zlCardScsl==null?"0":zlCardScsl;
         dsDetailTable.insertRow(false);
         dsDetailTable.setValue("cpid", cpid);
         dsDetailTable.setValue("ph", ph);
         dsDetailTable.setValue("zcsl", "0");
         dsDetailTable.setValue("scsl", zlCardScsl);
         dsDetailTable.setValue("dmsxid", dmsxid);
         dsDetailTable.post();
       }
     }
     initRowInfo(false,false,true);
   }
}

  /**
   *
   * @param cpid
   * @param cpbm
   * @return
   * @throws Exception
   */
  private String getDmsxId(String cpid, String cpbm) throws Exception
  {
    String tempGgSx = cpbm.length()>7?cpbm.substring(7):cpbm;//2004-4-2 23:12 修改 暂时修改取产品编码前七位 yjg
    String ggsx = String.valueOf(Integer.parseInt(tempGgSx));
    String sql = "select * from kc_dmsx"
               + " where sxz like '%宽度(?)%'"
               + " and cpid = '?' and rownum<2 and isdelete = 0";
    sql = combineSQL(sql, "?", new String[]{ggsx, cpid});
    dmsxidExistData.setQueryString(sql);
    if(!dmsxidExistData.isOpen())
      dmsxidExistData.openDataSet();
    else
      dmsxidExistData.refresh();

    if (dmsxidExistData.getRowCount() == 0 )
    {
      dmsxidExistData.insertRow(false);
      String tempStr = dataSetProvider.getSequence("s_kc_dmsx");
      dmsxidExistData.setValue("dmsxid", tempStr);
      dmsxidExistData.setValue("cpid", cpid);
      dmsxidExistData.setValue("sxz", "宽度(" + ggsx + ")");
      dmsxidExistData.post();
      dmsxidExistData.saveChanges();
    }
    return dmsxidExistData.getValue("dmsxid");
  }

  /**
   * 03.19 20:34 新增 实现翻页为方便打印的类.
   */
  class Move_Cursor_ForPrint implements Obactioner
  {
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    boolean isNext = String.valueOf(NEXT).equals(action);
    dsMasterTable.goToInternalRow(masterRow);
    if(isNext)
      dsMasterTable.next();
    else
      dsMasterTable.prior();
    masterRow = dsMasterTable.getInternalRow();

    //dsMasterTable.goToInternalRow(masterRow+1);
    //masterRow = dsMasterTable.getInternalRow();
    //int i = dsMasterTable.getRow();
    synchronized(dsDetailTable){
      openDetailTable(false);
    }
    initRowInfo(true, false, true);
    initRowInfo(false, false, true);
  }
}
  /**
   * 03:24 10:39 拷贝当前行功能.
   */
  class Copy_CurrentRow implements Obactioner
  {
    /**
     * 取得网页上明细资料表格中当前行的资料,然后再复制指定数量的此行的资料.加入到网页数据集中,显示出来在网页上.
     *  1. 从request中得到页面上当前光标所停留在的行数是下方明细表格中的那一行.设为i行.
     *  2. 接着要从保存网页资料的RowMap集中取出此i行的数据.
     *  3. 将此i行的数据加入到保存网页资料的RowMap集中,连续加入指定数量次.
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //取得网页上光标所停留在的那一行的行数.
      int currentRow = Integer.parseInt(data.getParameter("rownum"));
      //取得使用复制功能的时候,默认一次复制几笔. yjg
      int tCopyNumber = Integer.parseInt(data.getParameter("tCopyNumber"));
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      RowMap detailRow = (RowMap)d_RowInfos.get(currentRow);
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String pdid = dsMasterTable.getValue("pdid");
      RowMap rowinfo = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        rowinfo = (RowMap)d_RowInfos.get(i);
        long swapdetailRow = Long.parseLong(rowinfo.get("InternalRow"));
        detail.goToInternalRow(swapdetailRow);
        detail.setValue("pdid", rowinfo.get("pdid"));
        detail.setValue("cpid", rowinfo.get("cpid"));
        detail.setValue("cpbm", rowinfo.get("cpbm"));
        detail.setValue("ph", rowinfo.get("ph"));
        detail.setValue("zcsl", rowinfo.get("zcsl"));
        detail.setValue("scsl", rowinfo.get("scsl"));
        detail.setValue("hssl", rowinfo.get("hssl"));
        detail.setValue("dmsxid", rowinfo.get("dmsxid"));
        detail.setValue("kwid", rowinfo.get("kwid"));
        detail.post();
      }
      for (int i =0; i<tCopyNumber; i++)
      {
        detail.insertRow(false);
        detail.setValue("pdid", isMasterAdd ? "-1" : pdid);
        detail.setValue("pdmxid", "-1");
        //detail.setValue("bz", "1");//标志为1表示不能修改
        detail.setValue("cpid", detailRow.get("cpid"));
        detail.setValue("cpbm", detailRow.get("cpbm"));
        detail.setValue("ph", "");
        detail.setValue("zcsl", "0");
        detail.setValue("dmsxid", detailRow.get("dmsxid"));
        detail.setValue("kwid", detailRow.get("kwid"));
        detail.post();
        //RowMap detailrow = new RowMap(detail);
        //d_RowInfos.add(detailrow);
      }
      initRowInfo(false, false, true);
    }
  }
  public long getMasterRow()
  {
    return masterRow;
  }
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
     putDetailInfo(data.getRequest());
    }
  }

  /**
   * 2004-5-5 .
   */
  class ImportData implements Obactioner
  {
    /**
     * 按页翻动明细数据集的数据
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //清空明细表dataset和rowinfo中所有数据集
      dsDetailTable.deleteAllRows();
      d_RowInfos.clear();
      RowMap rowinfo = getMasterRowinfo();
      String storeid = rowinfo.get("storeid");
      boolean isNew = false;
      EngineDataSet dsTemp = new EngineDataSet();
      setDataSetProperty(dsTemp, "SELECT TRIM(kcdm) cpbm, TRIM(ph) ph, jc FROM ss");
      dsTemp.openDataSet();

      LookUp prodCodeBean = LookupBeanFacade.getInstance(req,SysConstant.BEAN_PRODUCT_CODE);
      prodCodeBean.regData(new String[]{"1"});
      dsTemp.first();
      for(int i=0; i<dsTemp.getRowCount(); i++)
      {
        String cpbm0 = dsTemp.getValue("cpbm");
        String cpbm = cpbm0.length()>6?cpbm0.substring(0, 7):cpbm0;
        RowMap prodCodeRow = prodCodeBean.getLookupRow(cpbm);
        String cpid = prodCodeRow.get("cpid");
        String p_storeid = prodCodeRow.get("storeid");
        if(cpid.equals("") || (!p_storeid.equals(storeid) && !p_storeid.equals("")))
        {
          dsTemp.next();
          continue;
        }

        //2004-4-23 11:06 修改 为新编码的盘点机而修改新增 yjg
        String dmsxid = "";
        if(isNew)
        {
          String[] dmsxidArray = parseString(cpbm0,"-");
          if (dmsxidArray.length < 2 )
          {
            dsTemp.next();
            continue;
          }
          dmsxid = dmsxidArray[1];
        }
        else
          dmsxid = getDmsxId(cpid, cpbm0);//取得代码属性id

        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("cpid", cpid);
        dsDetailTable.setValue("ph", dsTemp.getValue("ph"));
        dsDetailTable.setValue("zcsl", "0");
        dsDetailTable.setValue("scsl", dsTemp.getValue("jc"));
        dsDetailTable.setValue("hssl", "0");
        dsDetailTable.setValue("dmsxid", dmsxid);
        dsDetailTable.post();
        dsTemp.next();
      }
      dsTemp.closeDataSet();
      initRowInfo(false,false,true);
    }
  }
  /**
     * 分割字符串s,用sep分割成一个字符串数组.并保存到HashTable中
     * @para s 源串
     * @para sep 分割符
     * @para isGetLength 解析出长度.true则解析长度,false则解析宽度
     * @return 返回字符串，是Hash表中key为field的值
     */
    public final String parseEspecialString(String s, String sep, boolean isGetLength)
    {
      //保存返回的串值.
      String returnS = "";
      if(s==null || s.equals(""))
        return "0";
      String[] code = parseString(s, sep);
      //宽度的键及值.
      String key=null, value = null;
      //长度的键及值.
      //String lengthKey=null, lengthValue = null;
      //取宽度
      int j = 0; //值在被分割出来的数组中的index位置.在这处始终key的index>value的index:
      for(int i=0; i<code.length; i++)
      {
        if(i%2 > 0){
          value = code[i];
          j = i;//保存住上一个key的value的index
        }
        else{
          key = code[i].trim();
        }
        //任何情况下key的index>value的index.如相反则说明现在还只有key而没有value,那么回去找紧接着的value.
        //如果j<i则说当前的value是上一个key的value
        if ( j<i ) continue;
        if(value==null)
          continue;
        if(key.equals(isGetLength?lengthSyskey:widthSyskey))
         {
          return value;
         }
      }
      return "";
    }
}





package engine.erp.store;

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
import engine.erp.store.ImportSaleOrderGoods;
import engine.erp.store.Select_Batch;
import engine.erp.store.B_SingleLadding;
import engine.util.StringUtils;

import java.lang.Math;
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
 * <p>Title: 销售出库单列表</p>
 * <p>Description: 销售出库单列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

/**
 * 2004-6-19 16:10 新增 客户名称 用于给不同的客户定制程序.现在第一次新增是因为essen有关排序的要求 yjg
 * 2004-4-30 12:52  新增 以改正:复制功能复制的时候,除页面上最后一笔以外的数据都不能保留在复制前输入的数量(还未保存)值. yjg
 * 2004-4-29 16:59  修改 如果dmsxidArray尺寸小于2那么就continue因为在size<2的情况下dmsxidArray[1]会出错.
 *                  size<2默认为编码不合格.同时cpids也要删除刚刚做此操作cpids.add(cpid);add进来的这笔cpid.
 * 2004-4-29 10:35 修改 判断是不是一个要分到一组中的条件是:主从表的cpbm,宽度*长度相等 yjg
 * 2004-4-23 21:36 修改 销售出库单经手人默认为空 yjg
 * 2004-4-21 17:45 修改 引提单的时候不把部门引过来. yjg
 * 2004-4-17 17:48 新增 记帐功能 yjg
 * 2004-4-13 16:54 新增 用于添加从表信息的SQL，根据条件和汇总表得到信息.提供给盘点机使用. yjg
 * 2004-3-31 14:48 修改 为防止排序出错而改成多加一句put("InternalRow")这样的.以及相关改动 yjg
 * 2004-3-31 12:28 修改 重新将原来单价,金额的注释去掉,起用单价及金额.同时加上根据系统参数决定是否将提单单价也引过来 yjg
 * 2004-3-31 10:47 修改 根据系统参数将提单单价也引过来 yjg
 * 2004-3-30 14:53 新增 如果是保存按钮行为完成后定位数据集 yjg
 * 2004-3-29 23:08 新增 判断当是保存按钮的时候改变isMasterAdd为false.
 * 因为现在要求的保存行为是:在最初进来新增的那笔资料的基础上做修改的操作,而不是再新增一笔.yjg
 * 2004-3-27 11:46 修改 注释掉下面这个返回bachList()js函数的功能.以实现保存返回功能改成保存功能的要求. yjg
 * 2004-3-27 10:24 新增 新增删除空白行功能 yjg
 * 2004-3-27 10:24 修改 去掉当复制当前行的时候连同当前行的sl,hssl也复制过来的行为. yjg
 * 03:26 21:38 新增 当配批号的时候是否引库存量过来.1=以库存数量为准,0=没有变化 yjg
 * 03:26 21:15 新增 当配批号的时候也要把配批号页面上的库存量引过来 yjg
 * 03:26 15:25 新增 当引入提单时同时也把结算方式带过来给销售出库单主表. VW_SALEOUTSTORE_SINGLE_LADDING也加入了jsfsid yjg
 * 03:23 17:07 修改 修改下面这条sql.将原来的nvl(b.sl,0))>nvl(b.stsl,0)条件加上取绝对值的函数. yjg
 * 03.16 16:03 加入部门不能为空的判断. yjg
 * 03.16 15:25 修改 在Master_Search中去掉判断旧的SQL和新产生的SQL是否相同的if语句. yjg
 * 03.16 15:04 修改 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
 * 以实现:mantis上库存管理中0000158bug描述的:根据下达的部门，进行提交审批；
 */

public final class B_OtherOutStore extends BaseAction implements Operate
{
 public  static final String  SINGLE_PRODUCT_ADD   = "10812";
  public  static final String DETAIL_LADINGGOODS_ADD = "10801";
  public  static final String ONCHANGE               = "10401";
   public  static final String HSBL_ONCHANGE         = "1005123456";//提交换算比例
  public  static final String SELECTBATCH            = "12401";
  public  static final String SINGLE_LADDING         = "12351";
  public  static final String CANCEL_APPROVE         = "15698";
   public  static final String  DETAIL_ADD = "811214";
  //public  static final String STORE_ONCHANGE       = "15001";
  public  static final String TRANSFERSCAN           = "10061";//调用盘点单
  public  static final String SHOW_DETAIL            = "12500";//调用从表明细资料
  public  static final String REPORT                 = "2000";//02.23 11:26 新增 为配合小李的报表追踪调用事件而加 yjg
  public  static final String NEXT                   = "9999";//03.19 新增 为上一笔,下一笔打印而加的
  public  static final String PRIOR                  = "9998";//03.19 新增 为上一笔,下一笔打印而加的
  public  static final String COPYROW                = "9997";//03.25 新增 为上一笔,下一笔打印而加的
  public  static final String DELETE_BLANK           = "11581";//删除从表中库存量为空的纪录触发事件
  public  static final String WRAPPER_PRINT          = "1012";
  public  static final String RECODE_ACCOUNT         = "9996";//2004-4-17 17:48 新增 记帐功能的sql yjg
  public  static final String NEW_TRANSFERSCAN       = "10062";//读新盘点机触发事件
  public  static final String TURNPAGE               = "9995";// 新增 为明细表格番页而加的事件

  private static final String MASTER_STRUT_SQL       = "SELECT * FROM kc_sfdj WHERE 1<>1";
  private static String MASTER_SQL                   = "SELECT * FROM kc_sfdj WHERE ? AND djxz=? and fgsid=? ? ORDER BY sfdjdh DESC";
  private static final String TOTALZSL_SQL           = "SELECT SUM(nvl(zsl,0)) tzsl FROM kc_sfdj a WHERE ? AND djxz=? AND fgsid=? ? ORDER BY sfdjdh DESC";
  private static final String DETAIL_STRUT_SQL       = "SELECT * FROM kc_sfdjmx WHERE 1<>1";
  //02.28 23:15  修改 将下面此句的sql,用了'?'来代替原来的sfdjid=.
  //解决了当sdfdjid是空的时候会页面上(主要是contract_instore_bottom.jsp上)会出现sql错误的问题.yjg
  private static final String DETAIL_SQL             = "SELECT * FROM kc_sfdjmx WHERE sfdjid='?'";//
  private static final String TOTALHSSL_DETAIL_SQL   = "SELECT SUM(hssl) sumHssl FROM kc_sfdjmx WHERE sfdjid='?'";//
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL     = "SELECT * FROM kc_sfdj WHERE djxz='?' and sfdjid='?'";
  //通过销售合同ID得到销售合同货物sql
  //03:23 17:07 修改 修改下面这条sql.将原来的nvl(b.sl,0))>nvl(b.stsl,0)条件加上取绝对值的函数.
  //            同时数据库中的VW_SALEOUTSTORE_SINGLE_LADDING也做了这样相同的修改. yjg
  private static final String LADDING_DETAIL_SQL     = " SELECT a.djlx,a.khlx,a.storeid,a.dwtxid,a.jsfsid,a.deptid,a.personid, b.* FROM　xs_td a, xs_tdhw b WHERE a.tdid=b.tdid and abs(nvl(b.sl,0))>abs(nvl(b.stsl,0)) AND b.tdid= '?' ";
  private static final String PRINT_MASTER_SQL       = " SELECT * from VW_OUTPUTLIST_MST_BILL_WRAPPER ";//主
  private static final String PRINT_DETAIL_SQL       = " SELECT * from VW_OUTPUTLIST_DTL_BILL_WRAPPER ";//从
  //查询数据库是否有记账的单据
  private static final String RECODE_DATASQL         = " SELECT COUNT(*) FROM kc_sfdj a WHERE a.zdrid='?' AND  a.djxz ='?' AND a.zt=1";
  //把符合记帐功能的数据全部记帐
  private static final String RECODE                 = "UPDATE kc_sfdj a SET a.zt=2 WHERE a.zdrid='?' AND  a.djxz ='?' AND a.zt=1";
  //2004-4-13 16:54 新增 用于添加从表信息的SQL，根据条件和汇总表得到信息.提供给盘点机使用. yjg
  private static final String STOCK_MATERIAL_LIST
      = "SELECT b.cpid, b.dmsxid, b.ph, SUM(nvl(b.zl,0)) kcsl, SUM(nvl(b.hszl,0)) hszl "
      + "FROM   kc_wzmx b, vw_kc_dm_exist a "
      + "WHERE  b.cpid = a.cpid @ AND b.storeid = @ "
      + "GROUP BY b.storeid, b.cpid, b.dmsxid, b.ph";

  private EngineDataSet dsMasterTable            = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable            = new EngineDataSet();//从表
  private EngineDataSet dsTotalZsl               = new EngineDataSet();//统计总数量和
  private EngineDataSet dsTotaDetaillHssl        = new EngineDataSet();//统计总数量和
  private EngineDataSet dmsxidExistData          = new EngineDataSet();//代码属性是否存在的数据集
  private EngineDataSet dsStockMaterialDetail    = new EngineDataSet();//物资明细表的数据集
  public  HtmlTableProducer masterProducer       = new HtmlTableProducer(dsMasterTable, "kc_sfdj.8");
  public  HtmlTableProducer detailProducer       = new HtmlTableProducer(dsDetailTable, "kc_sfdjmx.8");

  private EngineDataSet dsBillWrapperMaster      = new EngineDataSet();//2004-4-2 21:23 新增 专门为套打的主表数据集.
  private EngineDataSet dsBillWrapperDetail      = new EngineDataSet();//2004-4-2 21:23 新增 专门为套打的从表数据集.

  private RowMap[] masterRows;//套打主表
  private ArrayList drows=null;//套打从表
  //private ArrayList mrows=null;//套打主表

  private boolean isMasterAdd = true;      //是否在添加状态
  public  boolean isApprove   = false;     //是否在审批状态
  private long    masterRow   = -1;        //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo   = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null;        //从表多行记录的引用
  public  boolean isReport     = false;       //02.23 11:26 新增 为配合小李的报表追踪调用事件而加的这个标志 yjg

  private ImportSaleOrderGoods saleLadingBean = null; //销售提单货物的bean的引用, 用于提取销售提单货物信息
  private Select_Batch selectBatchBean        = null; //选择批号的bean的引用, 用于从表产品选择批号
  private B_SingleLadding singleLadingBean    = null; //销售提单的bean的引用, 用于提取销售提单信息

  private LookUp storeBean = null; //仓库信息的bean的引用, 用于提取仓库信息
  public  boolean isDetailAdd    = false;
  private boolean isInitQuery   = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  int djxz = 10;//单据性质
  public String totalzsl = "0";
  public String totalDeatilHssl = "0";
  public  String loginId   = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门

  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid     = null;   //分公司ID
  private double childcount = 0;   //判断仓库是否含有库位
  private User user = null; //定义一个存放用户信息的实例
  private static boolean isKC_OUT_STORE_STYLE = false;//03:26 21:38 新增 当配批号的时候是否引库存量过来.1=以库存数量为准,0=没有变化 yjg
  public static String KC_OUT_SHOW_PRICE      = "0";//销售出库是否显示单价金额.1=显示,0=不显示
  public static String SC_STORE_UNIT_STYLE    = "1";//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  public static String SYS_CUST_NAME = "";//新增 2004-06-19 客户名称 用于给不同的客户定制程序.现在第一次新增是因为essen有关排序的要求 yjg
  private ArrayList mprint;
  private ArrayList dprint;
  public  String widthSyskey = "";
  public  String lengthSyskey = "";
  //被分页后的数据集中某一个页面中从第几笔记录开始到第几笔数据结束.如第二页的资料范围是从第51-101笔
  public int min = 0;
  public int max = 0;
  public String isRepeat = "0";//重定向，如果本业检测数据不正确的话isrepeat为1。将不翻页

  /**
   * 入库单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回入库单列表的实例
   */
  public static B_OtherOutStore getInstance(HttpServletRequest request)
  {
    B_OtherOutStore B_OtherOutStore = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_OtherOutStore";
      B_OtherOutStore = (B_OtherOutStore)session.getAttribute(beanName);
      if(B_OtherOutStore == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        //1=以库存数量为准,0=没有变化
        isKC_OUT_STORE_STYLE = loginBean.getSystemParam("KC_OUT_STORE_STYLE").equals("1")?true:false;
        KC_OUT_SHOW_PRICE = loginBean.getSystemParam("KC_OUT_SHOW_PRICE");//销售出库单1=显示单价,0=不显示单价
        SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
        SYS_CUST_NAME = loginBean.getSystemParam("SYS_CUST_NAME");//客户名称,用于客制化程序
        B_OtherOutStore = new B_OtherOutStore();
        B_OtherOutStore.qtyFormat = loginBean.getQtyFormat();
        B_OtherOutStore.priceFormat = loginBean.getPriceFormat();
        B_OtherOutStore.sumFormat = loginBean.getSumFormat();

        B_OtherOutStore.fgsid = loginBean.getFirstDeptID();
        B_OtherOutStore.loginId = loginBean.getUserID();
        B_OtherOutStore.loginName = loginBean.getUserName();
        B_OtherOutStore.loginDept = loginBean.getDeptID();
        B_OtherOutStore.user = loginBean.getUser();
        B_OtherOutStore.widthSyskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");
        B_OtherOutStore.lengthSyskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_LENGTH");
        //设置格式化的字段
        B_OtherOutStore.dsDetailTable.setColumnFormat("sl", B_OtherOutStore.qtyFormat);
        B_OtherOutStore.dsDetailTable.setColumnFormat("hssl", B_OtherOutStore.qtyFormat);
        B_OtherOutStore.dsDetailTable.setColumnFormat("dj", B_OtherOutStore.priceFormat);
        B_OtherOutStore.dsDetailTable.setColumnFormat("je", B_OtherOutStore.sumFormat);
        B_OtherOutStore.dsMasterTable.setColumnFormat("zsl", B_OtherOutStore.qtyFormat);
        //B_OtherOutStore.dsMasterTable.setColumnFormat("zje", B_OtherOutStore.qtyFormat);
        if (SYS_CUST_NAME.equals("essen"))
          MASTER_SQL = "SELECT * FROM kc_sfdj WHERE ? AND djxz=? and fgsid=? ? ORDER BY sfdjdh DESC, sfrq DESC";
        session.setAttribute(beanName, B_OtherOutStore);
      }
    }
    return B_OtherOutStore;
  }

  /**
   * 构造函数
   */
  private B_OtherOutStore()
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
    setDataSetProperty(dmsxidExistData, null);
    setDataSetProperty(dsStockMaterialDetail, null);
    setDataSetProperty(dsTotalZsl, null);
    setDataSetProperty(dsTotaDetaillHssl, null);
    setDataSetProperty(dsBillWrapperMaster, null);
    setDataSetProperty(dsBillWrapperDetail, null);


    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"sfdjdh"}, new String[]{"SELECT pck_base.billNextCode('kc_sfdj','sfdjdh','c') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"sfdjdh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"rkdmxid"}, new String[]{"s_kc_sfdjmx"}));
    dsDetailTable.setSort(new SortDescriptor("", new String[]{"wjid"}, new boolean[]{false}, null, 0));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(SINGLE_PRODUCT_ADD), new Single_Product_Add());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(SELECTBATCH), new Detail_Select_Batch());
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(DETAIL_LADINGGOODS_ADD), new Detail_LadingGoods_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(SINGLE_LADDING), new Single_Ladding());
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());
    addObactioner(String.valueOf(TRANSFERSCAN), new transferScan());//旧盘点机
    addObactioner(String.valueOf(NEW_TRANSFERSCAN), new transferScan());//新盘点机
    addObactioner(SHOW_DETAIL, new Show_Detail());//02.17 新增 新增查看从表明细资料事件发生时的触发操作类. yjg
    addObactioner(String.valueOf(REPORT), new Approve());//2.14 新增报表追此事件 yjg
    addObactioner(NEXT, new Move_Cursor_ForPrint());
    addObactioner(PRIOR, new Move_Cursor_ForPrint());
    addObactioner(COPYROW, new Copy_CurrentRow());
    addObactioner(DELETE_BLANK, new Delete_Blank());
    addObactioner(String.valueOf(WRAPPER_PRINT), new Wrapper_Print());//套打
    addObactioner(String.valueOf(RECODE_ACCOUNT), new Recode_Account());//套打
    addObactioner(TURNPAGE, new Turn_Page());//翻页事件
    addObactioner(String.valueOf(HSBL_ONCHANGE), new Store_Onchange());
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
    if(dsTotalZsl != null){
      dsTotalZsl.close();
      dsTotalZsl = null;
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
    if(dmsxidExistData != null){
     dmsxidExistData.close();
     dmsxidExistData = null;
    }
    if(dsStockMaterialDetail != null){
     dsStockMaterialDetail.close();
     dsStockMaterialDetail = null;
    }
    if(dsBillWrapperMaster != null){
     dsBillWrapperMaster.close();
     dsBillWrapperMaster = null;
    }
    if(dsBillWrapperDetail != null){
      dsBillWrapperDetail.close();
    dsBillWrapperDetail = null;
    }

    if(dsTotaDetaillHssl != null){
      dsTotaDetaillHssl.close();
    dsTotaDetaillHssl = null;
    }
    if (singleLadingBean != null){
      singleLadingBean = null;
    }
    if (selectBatchBean != null){
      selectBatchBean = null;
    }
    if (saleLadingBean != null){
      saleLadingBean = null;
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
        m_RowInfo.put("sfrq", today);
        m_RowInfo.put("zdrid", loginId);
        m_RowInfo.put("deptid", loginDept);
        //2004-4-23 21:36 修改 销售出库单经手人默认为空 yjg
        //m_RowInfo.put("jsr", loginName);
        m_RowInfo.put("khlx", "A");
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
      //2004-3-31 14:48  修改 为防止排序出错而改成多加一句put("InternalRow")这样的 yjg
      for(int i=0; i<dsDetail.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDetail);
        row.put("InternalRow", String.valueOf(dsDetail.getInternalRow()));
        d_RowInfos.add(row);
        dsDetail.next();
      }
    }
    caluTotalHssl();
  }

  /**
   * 从表保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
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
     detailRow.put("kwid", rowInfo.get("kwid_"+i));//
     detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//规格属性
     detailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+i), qtyFormat));//
     detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
     detailRow.put("ph", rowInfo.get("ph_"+i));//
     detailRow.put("isbatchno", rowInfo.get("isbatchno_"+i));

     detailRow.put("dj", formatNumber(rowInfo.get("dj_"+i), priceFormat));//单价
     detailRow.put("je", formatNumber(rowInfo.get("je_"+i), sumFormat));//金额

     detailRow.put("bz", rowInfo.get("bz_"+i));//备注
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
    String sfdjid = dsMasterTable.getValue("sfdjid");
    //02.28 23:15  修改 将下面此句setQueryString中的sql由原来的手动用+号组成sql`改成现在用combineSQL来组成.
    //解决了当sdfdjid是空的时候会页面上(主要是contract_instore_bottom.jsp上)会出现sql错误的问题.yjg
    dsDetailTable.setQueryString(combineSQL(DETAIL_SQL, "?", new String[]{isMasterAdd ? "-1" : sfdjid}));

    if(dsDetailTable.isOpen())
      dsDetailTable.refresh();
    else
      dsDetailTable.open();
    //取得换算数量的和.
    caluTotalHssl();
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

  //02.17 11:03 新增 新增一个当页面上单击事件发生时想要查看明细资料 这个单击事件的Show_Detail类 . yjg
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
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("zt", "0");
      row.put("sfrq$a", startDay);
      row.put("sfrq$b", today);
      isMasterAdd = true;
      isDetailAdd = false;
      //
      String SQL = " AND zt<>2 and zt<>8 ";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid") ,String.valueOf(djxz),fgsid,SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();

      String tempTzslSql = " AND zt<>2 and zt<>8  ";
      tempTzslSql = combineSQL(TOTALZSL_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), String.valueOf(djxz),fgsid,tempTzslSql});
      dsTotalZsl.setQueryString(tempTzslSql);
      if (dsTotalZsl.isOpen())
        dsTotalZsl.refresh();
      else
        dsTotalZsl.openDataSet();
      if (dsTotalZsl.getRowCount()<1)
        totalzsl = "0";
      else
        totalzsl = dsTotalZsl.getValue("tzsl");
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
      isDetailAdd = false;
      isMasterAdd = String.valueOf(ADD).equals(action);
      isReport = false;//02.23 11:26 新增 为配合小李的报表追踪调用事件而加的这个标志 yjg
      if(!isMasterAdd)
      {
        isMasterAdd =false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        openDetailTable(false);
      }
      else//打开从表
      {
        //02.18 16:45 新增 同步子表 yjg
       synchronized(dsDetailTable){
         openDetailTable(true);
        }
      }

      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);

      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
    *调用盘点单触发的事件
    */
   class transferScan implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       HttpServletRequest req = data.getRequest();
       //保存输入的明细信息
       putDetailInfo(data.getRequest());
       RowMap rowinfo = getMasterRowinfo();
       String storeid = rowinfo.get("storeid");
       String scanValue= req.getParameter("scanValue");//得到包含产品编码和批号的字符串
       //2004-4-23 11:06 修改 为新编码的盘点机而修改新增 yjg
       //boolean isNew = String.valueOf(NEW_TRANSFERSCAN).equals(action);
       String[][] s=engine.util.StringUtils.getArrays(scanValue);
       String[] cpbms = s[0];//产品编码数组
       String[] phStr   = s[1];//批号数组
       ArrayList dmsxids = new ArrayList();
       ArrayList cpids = new ArrayList();
       ArrayList phs = new ArrayList();
       ArrayList cpbmsArry = new ArrayList();

       StringBuffer rejectScanDataMessage = new StringBuffer();//用来保存盘点机读进来的但是却没有在此单据定位到因此不能插入(即舍弃掉)那些数据信息.
       LookUp prodCodeBean = LookupBeanFacade.getInstance(req,SysConstant.BEAN_PRODUCT_CODE);
       prodCodeBean.regData(cpbms);
       //
       int total = cpbms.length/10 + (cpbms.length%10 > 0 ? 1 : 0);
       int i = 0;//用来记录cpids数组size实际的大小.
       for(int out=0; out<total; out++)
       {
         ArrayList tmp_dmsxids = new ArrayList();
         ArrayList tmp_cpids = new ArrayList();
         ArrayList tmp_phs = new ArrayList();
         ArrayList tmp_cpbmsArry = new ArrayList();
         for(int j=0; j<10; j++)
         {
           if(i>= cpbms.length)
             break;
           if(cpbms[i]==null){
             i++;
             continue;
           }
           String cpbm = cpbms[i].length()>6?cpbms[i].substring(0, 7):cpbms[i];//2004-4-2 23:12 修改 暂时修改取产品编码前七位 yjg
           RowMap prodCodeRow = prodCodeBean.getLookupRow(cpbm);
           String cpid = prodCodeRow.get("cpid");
           String p_storeid = prodCodeRow.get("storeid");
           if(cpid.equals("") || (!p_storeid.equals(storeid) && !p_storeid.equals(""))){
             i++;
             continue;
           }
           //2004-4-23 11:06 修改 为新编码的盘点机而修改新增 yjg
           String dmsxid = "";
           boolean isNew = cpbms[i].indexOf("-") >-1;
           if(isNew)
           {
             String[] dmsxidArray = parseString(cpbms[i],"-");
             //2004-4-29 16:59  修改 如果dmsxidArray尺寸小于2那么就continue因为在size<2的情况下dmsxidArray[1]会出错.
             //size<2默认为编码不合格
             if (dmsxidArray.length < 2){
               i++;
               continue;
             }
             dmsxid = dmsxidArray[1];
           }
           else
             dmsxid = getDmsxId(cpid, cpbms[i]);//取得代码属性id

           tmp_cpids.add(cpid);
           tmp_dmsxids.add(dmsxid);
           tmp_phs.add(phStr[i]);
           tmp_cpbmsArry.add(cpbm);
           i++;
         }
         cpids.add(tmp_cpids);
         dmsxids.add(tmp_dmsxids);
         phs.add(tmp_phs);
         cpbmsArry.add(tmp_cpbmsArry);
       }
       //
       EngineDataSet dsTemp = new EngineDataSet();
       setDataSetProperty(dsTemp, null);
       for(int out=0; out<total; out++)
       {
         ArrayList tmp_cpids = (ArrayList)cpids.get(out);
         if(tmp_cpids.size() == 0)
           continue;
         ArrayList tmp_dmsxids = (ArrayList)dmsxids.get(out);
         ArrayList tmp_phs = (ArrayList)phs.get(out);

         String temp = StringUtils.getArrayValue((String[])tmp_phs.toArray(new String[tmp_phs.size()]), "','");
         String sql = temp.length() > 0 ? " AND  b.ph IN ('" + temp + "')" : "";
         temp = StringUtils.getArrayValue((String[])tmp_cpids.toArray(new String[tmp_cpids.size()]), ",");
         sql += temp.length() > 0 ? (sql.length()>0 ? " AND" : "") + " b.cpid IN (" + temp + ")" : "";
         temp = StringUtils.getArrayValue((String[])tmp_dmsxids.toArray(new String[tmp_dmsxids.size()]), ",");
         sql += temp.length() > 0 ? (sql.length()>0 ? " AND" : "") + " b.dmsxid IN (" + temp + ")" : "";
         sql = combineSQL(STOCK_MATERIAL_LIST, "@", new String[]{sql, storeid});
         if(dsStockMaterialDetail.isOpen())
         {
           dsTemp.setQueryString(sql);
           if(dsTemp.isOpen())
             dsTemp.refresh();
           else
             dsTemp.openDataSet();

           DataSetData.extractDataSet(dsTemp).loadDataSet(dsStockMaterialDetail);
         }
         else
         {
           dsStockMaterialDetail.setQueryString(sql);
           dsStockMaterialDetail.openDataSet();
         }
       }
       //关闭数据集体
       dsTemp.closeDataSet();
       if (!dsStockMaterialDetail.isOpen()) {
          data.setMessage(showJavaScript("alert('盘点机中没有该指定库房的数据')"));
          return;
       }

       EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[]{"cpid", "dmsxid"});
       //只用cpid定位
       EngineRow locateCpidGoodsRow = new EngineRow(dsDetailTable, new String[]{"cpid"});
       EngineRow locateGoodsPhRow = new EngineRow(dsStockMaterialDetail, new String[]{"cpid", "dmsxid", "ph"});
       engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
       //复制一个明细数据集.其实就是引入的提单数据
       EngineDataView tempDsDetailViewTable = dsDetailTable.cloneEngineDataView();
       tempDsDetailViewTable.open();
       propertyBean.regData(new String[]{"1"});
       for(i=0; i<cpids.size(); i++)
       {
         ArrayList tmpcpids = (ArrayList)cpids.get(i);
         ArrayList tmpdmsxids = (ArrayList)dmsxids.get(i);
         ArrayList tmpphs = (ArrayList)phs.get(i);
         ArrayList tmpCpbmsArry = (ArrayList)cpbmsArry.get(i);
         for(int m=0;m<tmpcpids.size();m++)
         {
           String cpid = (String)tmpcpids.get(m);
           String ph = (String)tmpphs.get(m);
           String dmsxid = (String)tmpdmsxids.get(m);
           String cpbm = (String)tmpCpbmsArry.get(m);
           locateGoodsRow.setValue(0, cpid);
           locateGoodsRow.setValue(1, dmsxid);
           //locateGoodsRow.setValue(2, ph);
           //先用cpid, dmsxid 定位
           if(dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
           {
             if(!dsDetailTable.getValue("ph").equals(ph))
             {
               String kcsl = "0";//库存数量.主要是为当盘点扫描进来的时候.去物资明细中取库存数量
               String hszl = "0";//库存换算重量.主要是为当盘点扫描进来的时候.去物资明细中取换算重量
               //用cpid, dmsxid, ph去物资明细表中定位记录,目地是要取到:kcsl
               locateGoodsPhRow.setValue(0, cpid);
               locateGoodsPhRow.setValue(1, dmsxid);
               locateGoodsPhRow.setValue(2, ph);
               if (dsStockMaterialDetail.locate(locateGoodsPhRow, Locate.FIRST))
               {
                 kcsl = dsStockMaterialDetail.getValue("kcsl");
                 hszl = dsStockMaterialDetail.getValue("hszl");
               }
               String tdhwid = dsDetailTable.getValue("wjid");
               String sfdjid = dsDetailTable.getValue("sfdjid");
               dsDetailTable.insertRow(false);
               dsDetailTable.setValue("sfdjid", isMasterAdd ? "-1" : sfdjid);
               dsDetailTable.setValue("cpid", cpid);
               dsDetailTable.setValue("wjid", tdhwid);
               dsDetailTable.setValue("ph", ph);
               dsDetailTable.setValue("dmsxid", dmsxid);
               dsDetailTable.setValue("sl", kcsl);
               dsDetailTable.setValue("hssl", hszl);
               dsDetailTable.post();
               //创建一个与用户相对应的行
               RowMap detailrow = null;
               detailrow = new RowMap(dsDetailTable);
               detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
               d_RowInfos.add(detailrow);
             }
           }
           //用dsDetailTable中每笔记录的sxz中的宽度去由扫描机扫进来的dmsxid而得到的sxz宽度indexof
           else
           {
             /*if (tempDsDetailViewTable.isOpen())
               tempDsDetailViewTable.refresh();
             else
               tempDsDetailViewTable.open();
             */
             tempDsDetailViewTable.first();
             int qq = tempDsDetailViewTable.getRowCount();
             for (int f=0;f<tempDsDetailViewTable.getRowCount();f++)
             {
               String tmpDetailCpid = tempDsDetailViewTable.getValue("cpid");
               String tmpDetaildmsxid = tempDsDetailViewTable.getValue("dmsxid");
               //.cpid定位到就接着看sxz中的宽度
               //locateCpidGoodsRow.setValue(0, cpid);
               //此处不用定位locate来查询cpid符合的了，而是一笔一笔的比较cpid的
               if ( tmpDetailCpid.equals(cpid))
               {
                 //取dsDetailTable中的dmsxid来得到sxz
                 String detailSxz = propertyBean.getLookupName(tmpDetaildmsxid);
                 String scanSxz = propertyBean.getLookupName(dmsxid);
                 int gg = detailSxz.indexOf(scanSxz);
                 int bb = scanSxz.indexOf(detailSxz);
                 if ( scanSxz.indexOf(detailSxz)<0 )
                 {
                   if ( rejectScanDataMessage.length()<=0 ) rejectScanDataMessage.append("以下盘点机读进来的数据\n因其品名规格和规格属性\n在本单据没有找到与之对应的数据,未能取用\n");
                   rejectScanDataMessage.append(cpbm+dmsxid+ph+"\n");
                   tempDsDetailViewTable.next();
                   continue;
                 }
                 else//把盘点机读进来的插入一笔
                 {
                   //tempDsDetailTable.getValue()
                   if(!tempDsDetailViewTable.getValue("ph").equals(ph))
                   {
                     String kcsl = "0";//库存数量.主要是为当盘点扫描进来的时候.去物资明细中取库存数量
                     String hszl = "0";//库存换算重量.主要是为当盘点扫描进来的时候.去物资明细中取换算重量
                     //用cpid, dmsxid, ph去物资明细表中定位记录,目地是要取到:kcsl
                     locateGoodsPhRow.setValue(0, cpid);
                     locateGoodsPhRow.setValue(1, dmsxid);
                     locateGoodsPhRow.setValue(2, ph);
                     if (dsStockMaterialDetail.locate(locateGoodsPhRow, Locate.FIRST))
                     {
                       kcsl = dsStockMaterialDetail.getValue("kcsl");
                       hszl = dsStockMaterialDetail.getValue("hszl");
                     }
                     String tdhwid = tempDsDetailViewTable.getValue("wjid");
                     String sfdjid = tempDsDetailViewTable.getValue("sfdjid");
                     dsDetailTable.insertRow(false);
                     dsDetailTable.setValue("sfdjid", isMasterAdd ? "-1" : sfdjid);
                     dsDetailTable.setValue("cpid", cpid);
                     dsDetailTable.setValue("wjid", tdhwid);
                     dsDetailTable.setValue("ph", ph);
                     dsDetailTable.setValue("dmsxid", dmsxid);
                     dsDetailTable.setValue("sl", kcsl);
                     dsDetailTable.setValue("hssl", hszl);
                     dsDetailTable.post();
                     //创建一个与用户相对应的行
                     RowMap detailrow = null;
                     detailrow = new RowMap(dsDetailTable);
                     detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
                     d_RowInfos.add(detailrow);
                   }
                   break;
                 }
               }
               else
               {
                 tempDsDetailViewTable.next();
                 continue;
               }
                /*while (dsDetailTable.last())
               //如果cpid能定位到那么考虑尝试sxz中的宽度是否匹配.
               if (dsDetailTable.locate(locateCpidGoodsRow, Locate.FIRST))
               {
               //循环取出dsDetailTable中每笔的dmsxid然后得到sxz,用这个sxz去当前盘点机扫进来的这个dmsxid的sxz去indexof
               //一旦找到一笔就.不再找下面的了
               }*/
             }
           }/*
           else //如果没有定位到那么.记录下来此笔读进来资料的cpid, dmsxid
           {
           if ( rejectScanDataMessage.length()<=0 ) rejectScanDataMessage.append("以下盘点机读进来的数据\n因其品名规格和规格属性\n在本单据没有找到与之对应的数据,未能取用\n");
           rejectScanDataMessage.append(cpbm+dmsxid+ph+"\n");
           }*/
         }
       }
       String tmpMessage = "";
       if (rejectScanDataMessage.length()>0)
         tmpMessage += showMessage(rejectScanDataMessage.toString(), false);
       //关闭数据集体
       //tmpMessage += showJavaScript("totalCalSl();");
       dsStockMaterialDetail.closeDataSet();
       tempDsDetailViewTable.cloneDataSetView();
       data.setMessage(tmpMessage);
     }
     public String getDmsxId(String cpid, String cpbm) throws Exception{
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
      String oldstoreid = rowinfo.get("storeid");
      putDetailInfo(data.getRequest());
      String dwtxid = rowinfo.get("dwtxid");
      String storeid = rowinfo.get("storeid");
      if(!oldDwtxid.equals(dwtxid) || !oldstoreid.equals(storeid)){
        EngineDataSet detail = getDetailTable();
        detail.first();
        while(detail.inBounds())
        {
          String wjid = detail.getValue("wjid");
          if(!wjid.equals(""))
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
 * 提交仓库
 */
class Store_Onchange implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest request = data.getRequest();
    //boolean isStore = String.valueOf(STORE_ONCHANGE).endsWith(action);
    //if(isStore)
     // m_RowInfo.put(request);
    //else
      putDetailInfo(request);
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
      /**
       *报表调从表页面,传递operate='2000'操作
       */
      isReport = String.valueOf(REPORT).equals(action);
      String id=null;
      if(isReport){
        isApprove = false;
        id = data.getParameter("id");//得到报表传递的参数既收发单据主表ID
      }
      else{
        isApprove = true;//审批操作
        id = data.getParameter("id", "");
      }
      //得到request的参数,值若为null, 则用""代替
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{String.valueOf(djxz),id});
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
      String content = dsMasterTable.getValue("sfdjdh");
      //03.16 15:04 修改 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
      //以实现:mantis上库存管理中0000158bug描述的:根据下达的部门，进行提交审批；
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "outputlist", content, dsMasterTable.getValue("deptid"));
    }
  }
  /**
   * 取消审批触发操作
   */
  class Cancel_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "outputlist");
    }
  }
  /**
   * 主表保存操作的触发类
   */
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
     String sfdjid = null;
     if(isMasterAdd){
       ds.insertRow(false);
       sfdjid = dataSetProvider.getSequence("s_kc_sfdj");
       ds.setValue("sfdjid", sfdjid);
       ds.setValue("fgsid", fgsid);
       ds.setValue("zt","0");
       ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
       ds.setValue("zdrid", loginId);
       ds.setValue("zdr", loginName);//操作员
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
         detail.setValue("sfdjid", sfdjid);

       //detail.setValue("cpid", detailrow.get("cpid"));
       //double hsbl = detailrow.get("hsbl").length() > 0 ? Double.parseDouble(detailrow.get("hsbl")) : 0;//换算比例
       double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(detailrow.get("dj")) : 0;//单价
       double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;
       double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;//换算数量
       detail.setValue("sl", detailrow.get("sl"));//保存数量
       detail.setValue("hssl", detailrow.get("hssl"));//保存换算数量
       detail.setValue("dj", detailrow.get("dj"));//单价
       detail.setValue("je", String.valueOf(sl * dj));//金额
       detail.setValue("cpid", detailrow.get("cpid"));
       detail.setValue("kwid", detailrow.get("kwid"));
       detail.setValue("dmsxid", detailrow.get("dmsxid"));
       detail.setValue("wjid", detailrow.get("wjid"));
       detail.setValue("djxz", "10");
       detail.setValue("ph", detailrow.get("ph"));
       detail.setValue("bz", detailrow.get("bz"));//备注
       detail.setValue("fgsid", fgsid);
       //保存用户自定义字段
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
     ds.setValue("storeid", rowInfo.get("storeid"));//仓库id
     ds.setValue("jsr", rowInfo.get("jsr"));//经手人
     ds.setValue("deptid", rowInfo.get("deptid"));//部门id
     ds.setValue("jsfsid", rowInfo.get("jsfsid"));//结算方式id
     ds.setValue("sfdjlbid", rowInfo.get("sfdjlbid"));//汇率
     ds.setValue("djxz", "10");//单据性质
     ds.setValue("dwtxid", rowInfo.get("dwtxid"));//购货单位ID
     ds.setValue("sfrq", rowInfo.get("sfrq"));//收发日期
     ds.setValue("zsl", totalNum.toString());//总数量
     ds.setValue("zje", totalSum.toString());//总金额
     ds.setValue("bz", rowInfo.get("bz"));//备注
     ds.setValue("khlx", rowInfo.get("khlx"));//客户类型
     //保存用户自定义的字段
     FieldInfo[] fields = masterProducer.getBakFieldCodes();
     for(int j=0; j<fields.length; j++)
     {
       String fieldCode = fields[j].getFieldcode();
       detail.setValue(fieldCode, rowInfo.get(fieldCode));
     }
     ds.post();
     ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
     LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_SALE_IN);

     if(String.valueOf(POST_CONTINUE).equals(action)){
       isMasterAdd = true;
       initRowInfo(true, true, true);
       detail.empty();
       initRowInfo(false, true, true);//重新初始化从表的各行信息
     }
     else if(String.valueOf(POST).equals(action))
     {
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
      //2004-4-21 14:28 为验证产品编码  批号 规格属性组合 是否有重复而设置的.
      String cpid = new String();
      String ph = new String();
      String dmsxid  = new String();
      ArrayList list = new ArrayList(d_RowInfos.size());
      StringBuffer buf = new StringBuffer();
      String combinStr = new String();
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      //ArrayList list = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        ph = detailrow.get("ph");
        dmsxid = detailrow.get("dmsxid");
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        String wzmxid = detailrow.get("wzmxid");
        String kwid = detailrow.get("kwid");
        if(wzmxid.equals("") && childcount>0 && kwid.equals(""))
          return showJavaScript("alert('"+row+"行库位不能为空')");
        /**
         if(list.contains(cpid))
         return showJavaScript("alert('第"+row+"行产品重复');");
         else
         list.add(cpid);
        */
        String sl = detailrow.get("sl");
        String hssl = detailrow.get("hssl");
        String dj = detailrow.get("dj");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
        if((temp = checkNumber(hssl, "第"+row+"行换算数量")) != null)
          return temp;
        if(hssl.length()>0 && hssl.equals("0"))
          return showJavaScript("alert('第"+row+"行换算数量不能为零！');");
         if (KC_OUT_SHOW_PRICE.equals("1")) //1=显示,0=不显示
        {
           if((temp = checkNumber(dj, "第"+row+"行单价")) != null)
             return temp;
        }
         if (KC_OUT_SHOW_PRICE.equals("1")) //1=显示,0=不显示
         {
           if(dj.length()>0 && dj.equals("0"))
             return showJavaScript("alert('第"+row+"行单价不能为零！');");
         }
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
      String temp = rowInfo.get("sfrq");
      if(temp.equals(""))
        return showJavaScript("alert('收发日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法收发日期！');");
      temp = rowInfo.get("dwtxid");
      if(temp.equals(""))
      return showJavaScript("alert('请选择购货单位！');");
      temp = rowInfo.get("storeid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择仓库！');");
      //03.16 16:03 加入部门不能为空的判断. yjg
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择部门！');");
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
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_SALE_OUT);
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),String.valueOf(djxz), fgsid, SQL});
      //03.16 15:25 修改 在Master_Search中去掉判断旧的SQL和新产生的SQL是否相同的if语句. yjg
      //if(!dsMasterTable.getQueryString().equals(SQL))
      //{
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);
      //}

        String tempTzslSql = fixedQuery.getWhereQuery();
        if(tempTzslSql.length() > 0)
          tempTzslSql = " AND "+tempTzslSql;
        tempTzslSql = combineSQL(TOTALZSL_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), String.valueOf(djxz),fgsid,tempTzslSql});
        dsTotalZsl.setQueryString(tempTzslSql);
        if (dsTotalZsl.isOpen())
          dsTotalZsl.refresh();
        else
          dsTotalZsl.openDataSet();
        if (dsTotalZsl.getRowCount()<1)
          totalzsl = "0";
        else
          totalzsl = dsTotalZsl.getValue("tzsl");
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
        new QueryColumn(master.getColumn("sfdjdh"), null, null, null),
        new QueryColumn(master.getColumn("sfrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("sfrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//购货单位
        new QueryColumn(master.getColumn("storeid"), null, null, null, null, "="),//仓库
        new QueryColumn(master.getColumn("sfdjlbid"), null, null, null, null, "="),//收发单据类别
        new QueryColumn(master.getColumn("jsfsid"), null, null, null, null, "="),//结算方式
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门
        new QueryColumn(master.getColumn("sfdjid"), "kc_sfdjmx", "sfdjid", "cpid", null, "="),//从表产品
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "cpbm", "cpbm", "like"),//从表产品编码
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "pm", "pm", "like"),//从表产品名称
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "gg", "gg", "like"),//从表产品名称
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("jsr"), null, null, null, null, "like")
      });
      isInitQuery = true;
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
     String sfdjid = dsMasterTable.getValue("sfdjid");
     detail.insertRow(false);
     detail.setValue("sfdjid", isMasterAdd ? "-1" : sfdjid);
     detail.post();
     RowMap detailrow = new RowMap(detail);
     detailrow.put("InternalRow", String.valueOf(detail.getInternalRow()));
     d_RowInfos.add(detailrow);
   }
 }
 /**
  *  从表引入销售提单货物操作
 */
  class Detail_LadingGoods_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String importLadingGoods = m_RowInfo.get("importLadingGoods");
      if(importLadingGoods.length() == 0)
        return;

      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wjid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String sfdjid = dsMasterTable.getValue("sfdjid");
      String[] tddhwIDs = parseString(importLadingGoods,",");
      for(int i=0; i < tddhwIDs.length; i++)
      {
        if(tddhwIDs[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, tddhwIDs[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap saleLadingRow = getSaleOrderBean(req).getLookupRow(tddhwIDs[i]);
          double sjhssl  = saleLadingRow.get("sjhssl").length() > 0 ? Double.parseDouble(saleLadingRow.get("sjhssl")) : 0;//实际换算数量
          double  sjsl  = saleLadingRow.get("sjsl").length() > 0 ? Double.parseDouble(saleLadingRow.get("sjsl")) : 0;//实际数量
          //double djlx = saleLadingRow.get("djlx").length() > 0 ? Double.parseDouble(saleLadingRow.get("djlx")) : 0;
          double wckhssl = sjhssl;
          double wcksl = sjsl;
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("rkdmxid", "-1");
          dsDetailTable.setValue("sfdjid", isMasterAdd ? "-1" : sfdjid);
          dsDetailTable.setValue("wjid", tddhwIDs[i]);
          dsDetailTable.setValue("cpid", saleLadingRow.get("cpid"));
          dsDetailTable.setValue("sl", formatNumber(String.valueOf(wcksl), qtyFormat));
          dsDetailTable.setValue("hssl", formatNumber(String.valueOf(wckhssl), qtyFormat));
          dsDetailTable.setValue("bz", saleLadingRow.get("bz"));
          dsDetailTable.setValue("dmsxid", saleLadingRow.get("dmsxid"));
          //2004-3-31 10:47 新增 根据系统参数将提单单价也引过来 yjg
          if (KC_OUT_SHOW_PRICE.equals("1")) //1=显示,0=不显示
          {
            dsDetailTable.setValue("dj", saleLadingRow.get("dj"));
            dsDetailTable.setValue("je", saleLadingRow.get("jje"));
          }
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
          d_RowInfos.add(detailrow);
        }
      }
      //data.setMessage(showJavaScript("totalCalSl();"));
      //2004-3-31 14:48  修改 为防止排序出错而改成多加一句put("InternalRow")这样的.以及相关改动 yjg
      //initRowInfo(false, false, true);
    }
  }
  /**
   *  销售出库单选择提单主表信息触发操作的类
   */

  class Single_Ladding implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowinfo = getMasterRowinfo();

      String singleSelectLadding = rowinfo.get("singleSelectLadding");//单选得到采购进货单id
      if(singleSelectLadding.equals(""))
        return;
      String SQL = combineSQL(LADDING_DETAIL_SQL, "?", new String[]{singleSelectLadding}) ;
      EngineDataSet laddingGoodsData = new EngineDataSet();
      setDataSetProperty(laddingGoodsData,SQL);
      laddingGoodsData.open();
      if(laddingGoodsData.getRowCount()<1)
        return;
      laddingGoodsData.first();
      //RowMap singleLaddingRow = getSingleLaddingBean(req).getLookupRow(singleSelectLadding);//采购合同主表一行信息
      rowinfo.put("storeid", laddingGoodsData.getValue("storeid"));//仓库
      rowinfo.put("dwtxid", laddingGoodsData.getValue("dwtxid"));
      //2004-4-22 10:45  新增  当引入提单时同时也把客户类型带过来给销售出库单主表 yjg
      rowinfo.put("khlx", laddingGoodsData.getValue("khlx"));
      //2004-4-21 17:45 引提单的时候不把部门引过来. yjg
      //rowinfo.put("deptid", singleLaddingRow.get("deptid"));
      //03:26 15:25 新增 当引入提单时同时也把结算方式带过来给销售出库单主表 yjg
      rowinfo.put("jsfsid", laddingGoodsData.getValue("jsfsid"));
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wjid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String sfdjid = dsMasterTable.getValue("sfdjid");
      laddingGoodsData.first();
      for(int i=0; i < laddingGoodsData.getRowCount(); i++)
      {
        //laddingGoodsData.goToRow(i);
        String tdhwid = laddingGoodsData.getValue("tdhwid");
        locateGoodsRow.setValue(0, tdhwid);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          double hssl  = laddingGoodsData.getValue("hssl").length() > 0 ? Double.parseDouble(laddingGoodsData.getValue("hssl")) : 0;//换算数量
          double  sl  = laddingGoodsData.getValue("sl").length() > 0 ? Double.parseDouble(laddingGoodsData.getValue("sl")) : 0;//数量
          double sthssl  = laddingGoodsData.getValue("sthssl").length() > 0 ? Double.parseDouble(laddingGoodsData.getValue("sthssl")) : 0;//实提换算数量
          double  stsl  = laddingGoodsData.getValue("stsl").length() > 0 ? Double.parseDouble(laddingGoodsData.getValue("stsl")) : 0;//实提数量
          //double  djlx  = laddingGoodsData.getValue("djlx").length() > 0 ? Double.parseDouble(laddingGoodsData.getValue("djlx")) : 0;//实提数量
          double wthsl = hssl-sthssl;//未提换算数量
          double wtsl = sl-stsl;//未提数量
          if(wtsl==0)
            continue;
          dsDetailTable.insertRow(false);
          String cpid = laddingGoodsData.getValue("cpid");

          dsDetailTable.setValue("rkdmxid", "-1");
          dsDetailTable.setValue("sfdjid", isMasterAdd ? "-1" : sfdjid);
          dsDetailTable.setValue("wjid", tdhwid);
          dsDetailTable.setValue("cpid", cpid);
          dsDetailTable.setValue("sl", formatNumber(String.valueOf(wtsl), qtyFormat));
          dsDetailTable.setValue("hssl",formatNumber(String.valueOf(wthsl), qtyFormat));
          dsDetailTable.setValue("bz", laddingGoodsData.getValue("bz"));
          dsDetailTable.setValue("dmsxid", laddingGoodsData.getValue("dmsxid"));
          //2004-3-31 10:47 修改 根据系统参数将提单单价也引过来 yjg
          if (KC_OUT_SHOW_PRICE.equals("1")) //1=显示,0=不显示
          {
            dsDetailTable.setValue("dj", laddingGoodsData.getValue("dj"));
            dsDetailTable.setValue("je", laddingGoodsData.getValue("jje"));
          }
          dsDetailTable.post();
          //创建一个与用户相对应的行
          RowMap detailrow = new RowMap(dsDetailTable);
          long l = dsDetailTable.getInternalRow();
          detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
          d_RowInfos.add(detailrow);
        }
        laddingGoodsData.next();
      }
      //2004-3-31 14:48  修改 为防止排序出错而改成多加一句put("InternalRow")这样的.以及相关改动 yjg
      initRowInfo(false, false, true);
    }
  }
  /**
   *  从表选择批号操作
   */
  class Detail_Select_Batch implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      int row = Integer.parseInt(req.getParameter("rownum"));
      String mutibatch = m_RowInfo.get("mutibatch_"+row);
      if(mutibatch.length() == 0)
        return;
      dsDetailTable.goToRow(row);
      String sfdjid = dsDetailTable.getValue("sfdjid");
      String wjid = dsDetailTable.getValue("wjid");
      String cpid = dsDetailTable.getValue("cpid");
      //实例化查找数据集的类
      //02.14 21:12 修改: 此处参数多加了 cpid, dmsxid两个.目地是:在 批号选择清单 选物资明细时只有cpid, ph, dmsxid组合不同的才允许带回给提单界面 yjg
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[]{"cpid","ph","dmsxid"});
      String[] wzmxIDs = parseString(mutibatch,",");
      RowMap detail = null;
      for(int i=0; i < wzmxIDs.length; i++)
      {
        if(wzmxIDs[i].equals("-1"))
          continue;
        RowMap batchRow = getBatchBean(req).getLookupRow(wzmxIDs[i]);
        //配合 上面 02.14 21:12 的改动.此处也作了相应改动 yjg
        String ph = batchRow.get("ph");
        String newcpid = batchRow.get("cpid");
        String dmsxid = batchRow.get("dmsxid");
        locateGoodsRow.setValue(0, newcpid);
        locateGoodsRow.setValue(1, ph); //03.10 22:40 修改 修改原来的setValue(0, value)为现在的(数组索引, value) 与 02.14 21:12处的数组对应. yjg
        locateGoodsRow.setValue(2, dmsxid);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          if(i==0){
            detail = (RowMap)d_RowInfos.get(row);
          }
          else {
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("sfdjid", sfdjid);
            dsDetailTable.setValue("wjid", wjid);
            dsDetailTable.setValue("cpid", cpid);
            dsDetailTable.post();
            detail = new RowMap(dsDetailTable);
            detail.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
            d_RowInfos.add(++row, detail);
          }
          detail.put("wzmxid","1");//2004-06-14 09:21 修改原来的batchRow.get("wzmxid")为只赋值为1
          detail.put("ph",batchRow.get("ph"));
          detail.put("dmsxid",batchRow.get("dmsxid"));
          detail.put("kwid",batchRow.get("kwid"));
          if (isKC_OUT_STORE_STYLE) //03:26 21:15 新增 当配批号的时候是否也要把配批号页面上的库存量引过来 yjg
            detail.put("sl",batchRow.get("zl"));
          /*if (i==0){
            detail = (RowMap)d_RowInfos.get(row);
            detail.put("wzmxid",batchRow.get("wzmxid"));
            detail.put("ph",batchRow.get("ph"));
            detail.put("dmsxid",batchRow.get("dmsxid"));
            detail.put("kwid",batchRow.get("kwid"));
            if (isKC_OUT_STORE_STYLE)
              detail.put("sl",batchRow.get("zl"));
          }*/
        }
        else{
          detail = (RowMap)d_RowInfos.get(row);
          detail.put("wzmxid","1");//2004-06-14 09:21 修改原来的batchRow.get("wzmxid")为只赋值为1
          detail.put("ph",batchRow.get("ph"));
          detail.put("dmsxid",batchRow.get("dmsxid"));
          detail.put("kwid",batchRow.get("kwid"));
          if (isKC_OUT_STORE_STYLE) //03:26 21:15 新增 当配批号的时候是否也也要把配批号页面上的库存量引过来 yjg
            detail.put("sl",batchRow.get("zl"));
        }
        //RowMap detailrow = new RowMap(dsDetailTable);
        //detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
        //d_RowInfos.add(detailrow);
      }
      data.setMessage(showJavaScript("totalCalSl();"));
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
      RowMap detailrow = (RowMap)d_RowInfos.get(rownum);
      long l_row = Long.parseLong(detailrow.get("InternalRow"));
      ds.goToInternalRow(l_row);
      //删除临时数组的一列数据
      d_RowInfos.remove(rownum);
      ds.deleteRow();
    }
  }

  /**
   * 得到用于查找提单货物编号的bean
   * @param req WEB的请求
   * @return 返回用于查找提单货物编号的bean
  */
  public ImportSaleOrderGoods getSaleOrderBean(HttpServletRequest req)
  {
    if(saleLadingBean == null)
      saleLadingBean = ImportSaleOrderGoods.getInstance(req);
    return saleLadingBean;
  }
  /**
   * 得到用于选择批号信息的bean
   * @param req WEB的请求
   * @return 返回用于选择批号信息的bean
  */
  public Select_Batch getBatchBean(HttpServletRequest req)
  {
    if(selectBatchBean == null)
      selectBatchBean = Select_Batch.getInstance(req);
    return selectBatchBean;
  }
  /**
   * 得到用于销售提单信息的bean
   * @param req WEB的请求
   * @return 返回用于选择销售提单信息的bean
  */
  public B_SingleLadding getSingleLaddingBean(HttpServletRequest req)
  {
    if(singleLadingBean == null)
      singleLadingBean = B_SingleLadding.getInstance(req);
    return singleLadingBean;
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
      String tCopyNumber = data.getParameter("tCopyNumber");
      int copyNum= (tCopyNumber==null || tCopyNumber.equals("0")) ? 1 : Integer.parseInt(tCopyNumber);
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      RowMap detailRow = (RowMap)d_RowInfos.get(currentRow);
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String sfdjid = dsMasterTable.getValue("sfdjid");
      RowMap rowinfo = null;
      long swapdetailRow = -1;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        rowinfo = (RowMap)d_RowInfos.get(i);
        swapdetailRow = Long.parseLong(rowinfo.get("InternalRow"));
        //2004-4-30 12:52  新增 以改正:复制功能复制的时候,除页面上最后一笔以外的数据都不能保留在复制前输入的数量(还未保存)值. yjg
        detail.goToInternalRow(swapdetailRow);
        //double hsbl = rowinfo.get("hsbl").length() > 0 ? Double.parseDouble(rowinfo.get("hsbl")) : 0;//换算比例
        //2004-3-31 12:28  修改 重新将原来单价,金额的注释去掉,起用单价及金额.同时加上根据系统参数决定是否将提单单价也引过来 yjg
        double dj = 0.0;
        if (KC_OUT_SHOW_PRICE.equals("1")) //1=显示,0=不显示
          dj = rowinfo.get("dj").length() > 0 ? Double.parseDouble(rowinfo.get("dj")) : 0;//单价
        double sl = rowinfo.get("sl").length() > 0 ? Double.parseDouble(rowinfo.get("sl")) : 0;
        double hssl = rowinfo.get("hssl").length() > 0 ? Double.parseDouble(rowinfo.get("hssl")) : 0;//换算数量
        detail.setValue("sl", rowinfo.get("sl"));//保存数量
        //计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
        //if (SC_STORE_UNIT_STYLE.equals("1"))
          //detail.setValue("hssl", String.valueOf(hsbl==0 ? 0 : sl/hsbl));//保存换算数量
        //else
        //直接取页面上的hssl值
          detail.setValue("hssl", String.valueOf(hssl));//保存换算数量
        //2004-3-31 12:28  修改 紧接着的下面两行去掉注释.目地是为:重新将原来单价,金额的注释去掉,起用单价及金额.同时加上根据系统参数决定是否将提单单价也引过来 yjg
        if (KC_OUT_SHOW_PRICE.equals("1")) //1=显示,0=不显示
        {
          detail.setValue("dj", rowinfo.get("dj"));//单价
          detail.setValue("je", String.valueOf(sl * dj));//金额
        }
        detail.setValue("cpid", rowinfo.get("cpid"));
        detail.setValue("kwid", rowinfo.get("kwid"));
        detail.setValue("dmsxid", rowinfo.get("dmsxid"));
        detail.setValue("wjid", rowinfo.get("wjid"));
        detail.setValue("djxz", String.valueOf(djxz));
        detail.setValue("ph", rowinfo.get("ph"));
        detail.setValue("bz", rowinfo.get("bz"));//备注
        detail.setValue("fgsid", fgsid);
        detail.post();
      }
      for (int i =0; i<copyNum; i++)
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("rkdmxid", "-1");
        dsDetailTable.setValue("sfdjid", isMasterAdd ? "-1" : sfdjid);
        dsDetailTable.setValue("wjid", detailRow.get("wjid"));
        dsDetailTable.setValue("cpid", detailRow.get("cpid"));
        //2004-3-27 10:24 修改 去掉当复制当前行的时候连同当前行的sl,hssl也复制过来的行为. yjg
        //dsDetailTable.setValue("sl", detailRow.get("sl"));
        //dsDetailTable.setValue("hssl",detailRow.get("hssl"));
        dsDetailTable.setValue("bz", detailRow.get("bz"));
        dsDetailTable.setValue("djxz", detailRow.get("djxz"));
        //dsDetailTable.setValue("ph", detailRow.get("ph"));
        dsDetailTable.setValue("kwid", detailRow.get("kwid"));
        //03.07 14:50 新增 在界面上用引入进货单按钮引入进货单时同时也取出规格属性来. yjg
        dsDetailTable.setValue("dmsxid", detailRow.get("dmsxid"));
        dsDetailTable.post();
        //创建一个与用户相对应的行
        //RowMap detailrow = new RowMap(dsDetailTable);
        //d_RowInfos.add(detailrow);
      }
      //2004-3-31 14:48  修改 为防止排序出错而改成多加一句put("InternalRow")这样的.以及相关改动 yjg
      initRowInfo(false, false, true);
    }
  }
  /**
   * 2004-3-27 10:24 新增 新增删除空白行功能 yjg
   * 删除数量为空白的操作
   */
  class Delete_Blank implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      //while(detail.inBounds())
      for(int i=0; i< d_RowInfos.size(); i++)
      {
        RowMap detailrow = (RowMap)d_RowInfos.get(i);
        long internalRow = Long.parseLong(detailrow.get("InternalRow"));
        detail.goToInternalRow(internalRow);
        String sl = detailrow.get("sl");
        //02.25 11:14 修改 修改删除一行库存量为空的一笔记录的条件改为 电脑库存数量, 实际库存数量全部都是零的才删除 yjg
        //03.23 11:36 修改 将上方的02.25 11:14 bug中所做的修改的基础上再修改:现在又改回了:
        //                删除一行库存量为空的一笔记录的条件改为:实际库存数量是空的. yjg
        if(sl.equals(""))
        {
          d_RowInfos.remove(i);
          detail.deleteRow();
          i--;
        }
      }
    }
  }
  /**
   *2004-4-17 17:48 新增 记帐功能 yjg
   */
  class Recode_Account implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
     //是否有符合记帐的数据，有几条
     String SQL = combineSQL(RECODE_DATASQL, "?", new String[]{loginId, String.valueOf(djxz)});
     String UPDATE_SQL = combineSQL(RECODE, "?", new String[]{loginId, String.valueOf(djxz)});
     String count = dataSetProvider.getSequence(SQL);
     if(count.equals("0"))
     {
       data.setMessage(showJavaScript("alert('没有你本人可以记帐的单据')"));
       return;
     }
     else{
       dsMasterTable.updateQuery(new String[]{UPDATE_SQL});
       dsMasterTable.readyRefresh();
       dsMasterTable.refresh();
     }
    }
   }
  /**
   * 套打
   * 提供套打数据
   * 主从表的数据集分别提供,放入在RowMap中
   */
  class Wrapper_Print implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String sfdjid = dsMasterTable.getValue("sfdjid");
      if(sfdjid==null)
        sfdjid="";
      ArrayList mastListKey = new ArrayList();
      ArrayList mastListValue = new ArrayList();
      //累计总共打印了几页
      //int totalPageNos = 0;
      //当前打印的是第几页
      //int cureentPageNo = 0;
      /*
      1.mrows中保存的是从sql语句中查询出来原始的主要是由cpbm,dmsxid分组的记录.
      2.但是可能会有cpbm, 长度*宽度 也相同的记录.因为是用cpbm,dmsxid分组,而不是cpbm ,sxz分组
      3.而打印的本意是:cpbm,sxz=长度*宽度相同的统一归到一笔主表记录下面.所以,主表中记录要求:
        3.1 cpbm,sxz=长度*宽度组合的数据在主表数据集中不允许有相同的
      4.因此,下面的操作就是:除去主表mrows数据集中cpbm,sxz=长度*宽度组合有多笔相同的记录.
        保持mrows数据中cpbm,sxz=长度*宽度组合没有相同的
      */
      //主表是直接产生RowMap数组
      dsBillWrapperMaster.setQueryString(PRINT_MASTER_SQL+" where sfdjid='"+sfdjid+"'");
      if(dsBillWrapperMaster.isOpen())
        dsBillWrapperMaster.refresh();
      else
        dsBillWrapperMaster.openDataSet();
      dsBillWrapperMaster.first();
      for(int i=0;i<dsBillWrapperMaster.getRowCount();i++)
      {
        String cpbm = dsBillWrapperMaster.getValue("cpbm");
        String sxz = dsBillWrapperMaster.getValue("sxz");
        String ckdgs = dsBillWrapperMaster.getValue("ckdgs");
        String width = null;
        //如果是平张纸品那么它的打印出来的规格要从全从规格属性里取
        if (ckdgs.equals("3"))
        {
          width = parseEspecialString(sxz, "()", true);//取长度
          width = width + "*" + parseEspecialString(sxz, "()", false);//取宽度
          //如果是平张纸的话那么,规格的格式直接就是规格属性里取到的:长度*宽度
        }
        else
          width = dsBillWrapperMaster.getValue("gg") + parseEspecialString(sxz,"()", false);

        String allName = cpbm + "$" + width;
        //如果是平张纸品(存货类别 kc_chlb表中的ckdgs=3)并且它也是可销售的那么它的hssl就用数据库中取出来的.
        //除此之外的情况,那么主表中的hssl字段就不须要用到了.程序中在这里将其置为空串.
        String issale = dsBillWrapperMaster.getValue("issale");
        boolean isNoPaper = !(issale.equals("1")&&ckdgs.equals("3"));

        int index = mastListKey.indexOf(allName);
        if(index < 0)
        {
          RowMap rm = new RowMap(dsBillWrapperMaster);
          rm.put("gg", width);
          rm.put("isPaper", isNoPaper ? "false" : "true");
          //empty hssl's value
          if(isNoPaper)
            rm.put("hssl", "");
          mastListKey.add(allName);
          mastListValue.add(rm);
        }
        dsBillWrapperMaster.next();
      }
      ///从表放入ArrayList中
      Hashtable detalTable = new Hashtable();
      dsBillWrapperDetail.setQueryString(PRINT_DETAIL_SQL+" where sfdjid='"+sfdjid+"'");
      if(dsBillWrapperDetail.isOpen())
        dsBillWrapperDetail.refresh();
      else
        dsBillWrapperDetail.openDataSet();
      //ArrayList al = new ArrayList();//放从表数据
      dsBillWrapperDetail.first();
      for(int i=0;i<dsBillWrapperDetail.getRowCount();i++)
      {
        String sl = dsBillWrapperDetail.getValue("sl");
        String ph = dsBillWrapperDetail.getValue("ph");
        String cpbm = dsBillWrapperDetail.getValue("cpbm");

        RowMap rm = new RowMap();
        rm.put("sl", dsBillWrapperDetail.getValue("sl"));
        rm.put("hssl", dsBillWrapperDetail.getValue("hssl"));
        rm.put("ph", dsBillWrapperDetail.getValue("ph"));
        rm.put("cpbm", dsBillWrapperDetail.getValue("cpbm"));
        rm.put("dmsxid", dsBillWrapperDetail.getValue("dmsxid"));
        rm.put("gg", dsBillWrapperDetail.getValue("gg"));
        String sxz = dsBillWrapperDetail.getValue("sxz");
        String ckdgs = dsBillWrapperDetail.getValue("ckdgs");
        String width = null;
        //如果是平张纸品那么它的打印出来的规格要从全从规格属性里取
        if (ckdgs.equals("3"))
        {
          width = parseEspecialString(sxz, "()", true);//取长度
          width = width + "*" + parseEspecialString(sxz, "()", false);//取宽度
          //如果是平张纸的话那么,规格的格式直接就是规格属性里取到的:长度*宽度
          rm.put("gg", width);
        }
        else
        {
          width = rm.get("gg") + parseEspecialString(sxz,"()", false);
          rm.put("gg", width);
        }
        String allName = cpbm + "$" + width;
        ArrayList detailList = (ArrayList)detalTable.get(allName);
        if(detailList == null)
        {
          detailList = new ArrayList();
          detalTable.put(allName, detailList);
        }
        detailList.add(rm);
        dsBillWrapperDetail.next();
      }
      //process print info
      mprint = new ArrayList();//主,放的是rowmap
      dprint = new ArrayList();//从,放的是arraylist
      for(int page=0; page < mastListKey.size(); page++)
      {
        RowMap mastRow = (RowMap)mastListValue.get(page);
        String allName = (String)mastListKey.get(page);
        boolean isPaper = mastRow.get("isPaper").equals("true");
        ArrayList detailRows = (ArrayList)detalTable.get(allName);
        if(detailRows == null){
          page++;
          continue;
        }
        //detail print bill data
        int num = 0;
        while(num < detailRows.size())
        {
          //add mast print row
          if(num > 0)
            mastRow = (RowMap)mastRow.clone();
          mprint.add(mastRow);
          //process detail print row
          BigDecimal pageSlTotal = new BigDecimal(0);
          BigDecimal pageHsslTotal = new BigDecimal(0);
          ArrayList list = new ArrayList();
          boolean isEnd = false;
          for(int i=0; i<9; i++)
          {
            RowMap detailPrintRow = new RowMap();
            for(int j=0; j<3; j++)
            {
              RowMap detailRow = (RowMap)detailRows.get(num);
              String ph = detailRow.get("ph");
              String sl = detailRow.get("sl");
              String hssl = detailRow.get("hssl");
              detailPrintRow.put("sl"+j, sl.length()==0 ? "0" : sl);
              detailPrintRow.put("ph"+j, ph);
              //是平装纸则求它的每页面的换算数量页数
              pageSlTotal = pageSlTotal.add(new BigDecimal(sl.equals("")?"0":sl));
              if (isPaper)
                pageHsslTotal = pageHsslTotal.add(new BigDecimal(hssl.equals("")?"0":hssl));

              num++;
              if(num >= detailRows.size())
              {
                isEnd = true;
                break;
              }
            } /*end of j=3*/
            list.add(detailPrintRow);
            if(isEnd)
              break;
          }/*end of i=9*/
          dprint.add(list.toArray(new RowMap[list.size()]));
          mastRow.put("total", "合计:"+pageSlTotal.toString());
          if(isPaper)
            mastRow.put("hssl", pageHsslTotal.toString());
        }
      }
      /*
      RowMap[] mastRows = (RowMap[])mrows.values().toArray(new RowMap[mrows.size()]);
      for (int j=0; j<mastRows.length;j++)
      {
        //用来记录每一个与主表数据集的一笔数据相对应的明细资料分组中被包含进去的明细资料的记录个数.
        int perGroupNumber = 0;
        ArrayList dprintrow = new ArrayList();
        RowMap tempMrm = mastRows[j];
        String cpbm = tempMrm.get("cpbm");
        String dmsxid = tempMrm.get("dmsxid");
        String mgg = tempMrm.get("gg");
        //ArrayList dArray = new ArrayList();
        int m = 0;
        RowMap rtmp = new RowMap();
        BigDecimal alBtotal = new BigDecimal(0);
        for (int l=0;l<al.size();l++)
        {
          //对从表
          RowMap tempDrm = (RowMap)al.get(l);
          String dcpbm = tempDrm.get("cpbm");
          String ddmsxid = tempDrm.get("dmsxid");
          String dgg = tempDrm.get("gg");
          String ph = tempDrm.get("ph");
          String sl = tempDrm.get("sl");
          String hssl = tempDrm.get("hssl");
          //2004-4-29 10:35 修改 判断是不是一个要分到一组中的条件是:主从表的cpbm,宽度*长度相等 yjg
          if (dcpbm.equals(cpbm) && dgg.equals(mgg))
          {
            perGroupNumber++;
            rtmp.put("ph"+m,ph);
            rtmp.put("sl"+m,sl);
            rtmp.put("hssl"+m,hssl);
            //对cpbm, dmsxid相同的数据(即:打印出来的时候会打印在同一张纸上)合计sl
            alBtotal = alBtotal.add(new BigDecimal(sl.equals("")?"0":sl));
            m=m+1;
            if(m==3)
            {
              //从表的记录数多于3行
              m=0;
              dprintrow.add(rtmp);
              rtmp = new RowMap();
            }
            //dArray.add(tempDrm);//取出子表对应主表一行的数据.
          }
        }
        //形成 合计:xxx.xx供打印
        tempMrm.put("total", "合计:"+alBtotal.toString());
        if(m<3)
          dprintrow.add(rtmp);//
        //如果dprintrow的大小大于一页9行的数量.那么就要分页来打印了.
        //里面记录了.属于同一个明细资料长*宽分组的明细数据.
        //循环几次.即按每组9行.要分拆成几组
        if (dprintrow.size()>9)
        {
          int total = dprintrow.size()/9 + (dprintrow.size()%9 > 0 ? 1 : 0);
          //tempMrm.put("pageNo", "第" + String.valueOf(j+1)+ "页" + "/"+  "共" + String.valueOf(mrows.size()+total-1) + "页");
          for (int i=0;i<total;i++)
          {
            //确定这一页会有几条记录
            int tempSize = dprintrow.size()-(i*9)>9?9:dprintrow.size()-(i*9);
            RowMap[] drs= new RowMap[tempSize];
            //取从表打印数据数据集中的第几笔到第几笔组成一页.
            String sl0 = "0";
            String sl1 = "0";
            String sl2 = "0";
            String hssl0 = "0";
            String hssl1 = "0";
            String hssl2 = "0";
            boolean isPaper = tempMrm.get("isPaper").equals("true");
            tempMrm.put("total", "0");
            tempMrm.put("hssl", "");
            BigDecimal btotal = new BigDecimal(0);
            BigDecimal hsslBtotal = new BigDecimal(0);
            for (int v=i*9,w=0; v<(i+1)*9&&v<dprintrow.size();v++,w++)
            {
              RowMap rp = (RowMap)dprintrow.get(v);
              drs[w] = (RowMap)dprintrow.get(v);
              sl0 = rp.get("sl0").equals("")?"0":rp.get("sl0");
              sl1 = rp.get("sl1").equals("")?"0":rp.get("sl1");
              sl2 = rp.get("sl2").equals("")?"0":rp.get("sl2");
              //是平装纸则求它的每页面的换算数量页数
              if (isPaper)
              {
                hssl0 = rp.get("hssl0").equals("")?"0":rp.get("hssl0");
                hssl1 = rp.get("hssl1").equals("")?"0":rp.get("hssl1");
                hssl2 = rp.get("hssl2").equals("")?"0":rp.get("hssl2");
                //求每页换算数量和数量的和
                hsslBtotal=hsslBtotal.add(new BigDecimal(hssl0.equals("")?"0":hssl0).add(new BigDecimal(hssl1.equals("")?"0":hssl1).add(new BigDecimal(hssl2.equals("")?"0":hssl2))));
              }
              btotal=btotal.add(new BigDecimal(sl0.equals("")?"0":sl0).add(new BigDecimal(sl1.equals("")?"0":sl1).add(new BigDecimal(sl2.equals("")?"0":sl2))));

              //float tempTotal= tempMrm.get("total").equals("")?0:Float.parseFloat(tempMrm.get("total"));
              //float tempSl = Float.parseFloat(sl0) + Float.parseFloat(sl1) + Float.parseFloat(sl2);
              //tempMrm.put("total", Float.toString(tempTotal+tempSl));
            }
            tempMrm.put("total", "合计:"+btotal.toString());
            tempMrm.put("hssl", isPaper?hsslBtotal.toString():"");
            totalPageNos++;
            //同一组的明细资料分成几页,那么主表也要相应的有几个主表资料头
            RowMap tempNewMrm = (RowMap)tempMrm.clone();
            mprint.add(tempNewMrm);//放的是RowMap;
            dprint.add(drs);//放的是arrayList
          }
        }
        else
        {
          RowMap[] drs= new RowMap[dprintrow.size()];
          for(int q=0;q<dprintrow.size();q++)
          {
            RowMap rp =(RowMap)dprintrow.get(q);
            drs[q] = (RowMap)dprintrow.get(q);
            String sl = rp.get("sl0");
          }
          totalPageNos++;
          mprint.add(tempMrm);//放的是RowMap
          dprint.add(drs);//放的是arrayList
        }
        //如果明细资料中按长度*宽度分组后被分出来的数据条数和本来sql抽取到的明细资料表数据条数一样.那说明只存在一种分组.只须打印一张.
        //不须要再对主表数据集mrows进行循环了.
        if ( perGroupNumber == al.size() ) break;
      }*/
      /*
      * 对数据集进行循环,对货物按产品编码cpbm进行分组,相同的货物每三个一行,
      * 不足三个的占一行.多余三个的分成几行
      dsBillWrapperDetail.first();
      for(int i=0;i<dsBillWrapperDetail.getRowCount();i++)
      {
        String cpbm = dsBillWrapperDetail.getValue("cpbm");
        if(contains.contains(cpbm))
        {
          //已经分组的货物不再分组
          dsBillWrapperDetail.next();
          continue;
        }
        rm = new RowMap();
        int h=0;
        String count = dataSetProvider.getSequence("SELECT count(*) from VW_OUTPUTLIST_DTL_BILL_WRAPPER where sfdjid='"+sfdjid+"'");
        int totalcount = Integer.parseInt(count);//对应同类货物(不同规格属性)总数,有totalcount/3+Integer.parseInt(((totalcount%3)>0)?"1":"0")行
        for(int j=0;j<al.size();j++)
        {
          //al里包含了数据集里的数据
          RowMap rt =(RowMap)al.get(j);
          String ph = rt.get("ph");
          //String cpbm = rt.get("cpbm");
          if(rt.get("cpbm").equals(cpbm))
          {
            //提取出对应tdhwid
            String sl = rt.get("sl");
            String sn = "s"+(h%3);
            rm.put(sn,sl);
            h=h+1;
            if(totalcount<3&&h==totalcount)
            {
              //总数小于3且循环到结束.
              rm.put("ph",ph);
              drows.add(rm);//循环完
              contains.add(cpbm);
            }
            else if((h%3)==0)
            {
              //
              rm.put("ph",ph);
              drows.add(rm);
              contains.add(cpbm);
            }
            else if(totalcount>3&&h==totalcount)
            {
              //
              rm.put("ph",ph);
              drows.add(rm);//循环完
              contains.add(cpbm);
            }
          }
        }////////
        dsBillWrapperDetail.next();
      }*/
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
  /*打印主表**/
  public final ArrayList getMasterArray()
  {
    return mprint;
  }
  /*打印从表**/
  public final  ArrayList getDetailArray()
  {
   return dprint;
  }
  public long getMasterRow()
  {
    return masterRow;
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
     isRepeat="0";
     putDetailInfo(data.getRequest());
     String temp = checkNumRigtt(min,max);
     if(temp!=null){
       isRepeat="1";//重定向，如果本页检测数据不正确的话isrepeat为1。将不翻页
       data.setMessage(temp);
       return;
     }
   }
 }
 //检验数据正确性方法
 private String checkNumRigtt(int tempmin, int tempmax) throws Exception
 {
   String temp = null;
   RowMap detailrow = null;
   //2004-4-21 14:28 为验证产品编码  批号 规格属性组合 是否有重复而设置的.
   String cpid = new String();
   String ph = new String();
   String dmsxid  = new String();
   ArrayList list = new ArrayList(d_RowInfos.size());
   StringBuffer buf = new StringBuffer();
   String combinStr = new String();
   if(d_RowInfos.size()<1)
     return showJavaScript("alert('不能保存空的数据')");
   //ArrayList list = new ArrayList(d_RowInfos.size());
   for(int i=tempmin; i<=tempmax; i++)
   {
     int row = i+1;
     detailrow = (RowMap)d_RowInfos.get(i);
     cpid = detailrow.get("cpid");
     ph = detailrow.get("ph");
     dmsxid = detailrow.get("dmsxid");
     if(cpid.equals(""))
       return showJavaScript("alert('第"+row+"行产品不能为空');");
     String wzmxid = detailrow.get("wzmxid");
     String kwid = detailrow.get("kwid");
     if(wzmxid.equals("") && childcount>0 && kwid.equals(""))
       return showJavaScript("alert('"+row+"行库位不能为空')");
     /**
      if(list.contains(cpid))
      return showJavaScript("alert('第"+row+"行产品重复');");
      else
      list.add(cpid);
      */
     String sl = detailrow.get("sl");
     String hssl = detailrow.get("hssl");
     String dj = detailrow.get("dj");
     if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
       return temp;
     if(sl.length()>0 && sl.equals("0"))
       return showJavaScript("alert('第"+row+"行数量不能为零！');");
     if((temp = checkNumber(hssl, "第"+row+"行换算数量")) != null)
       return temp;
     if(hssl.length()>0 && hssl.equals("0"))
       return showJavaScript("alert('第"+row+"行换算数量不能为零！');");
     if (KC_OUT_SHOW_PRICE.equals("1")) //1=显示,0=不显示
     {
       if((temp = checkNumber(dj, "第"+row+"行单价")) != null)
         return temp;
     }
     if (KC_OUT_SHOW_PRICE.equals("1")) //1=显示,0=不显示
     {
       if(dj.length()>0 && dj.equals("0"))
         return showJavaScript("alert('第"+row+"行单价不能为零！');");
     }
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
    String sfdjid = dsMasterTable.getValue("sfdjid");
    dsDetailTable.goToRow(row);
    RowMap detailrow = null;
    detailrow = (RowMap)d_RowInfos.get(row);
    detailrow.put("rkdmxid", "-1");
    detailrow.put("cpid", cpid);
    detailrow.put("sfdjid", isMasterAdd ? "-1" : sfdjid);
  }
}

 private final void caluTotalHssl()
 {
   String sfdjid = dsMasterTable.getValue("sfdjid");
   dsTotaDetaillHssl.setQueryString(combineSQL(TOTALHSSL_DETAIL_SQL, "?", new String[]{sfdjid}));
   if (dsTotaDetaillHssl.isOpen())
     dsTotaDetaillHssl.refresh();
   else
     dsTotaDetaillHssl.openDataSet();
   if (dsTotaDetaillHssl.getRowCount()<1)
     totalDeatilHssl = "0";
   else
     totalDeatilHssl = dsTotaDetaillHssl.getValue("sumHssl");
   //dsTotaDetaillHssl.closeDataSet();
 }
 /**
 *  从表增加操作(增加一个空白行)
 */

}


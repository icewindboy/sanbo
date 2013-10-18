package engine.erp.store;

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
import engine.erp.store.Select_Batch;
import engine.util.StringUtils;
import engine.erp.store.B_SingleSelf;
import engine.erp.baseinfo.BasePublicClass;

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
 * <p>Title: 自制收货单列表-大发专用，在自制收货单模块中计算工资</p>
 * <p>Description: 自制收货单列表-大发专用，在自制收货单模块中计算工资<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */
/**
 * <p>Title:  自制收货单列表表结构改过，不在kc_sfdj,kc_sfdjmx中了，表为sc_receiveProd,sc_receiveProdDetail</p>
 */

public final class B_NewSelfGain
    extends BaseAction implements Operate {
  public static final String SELF_SEL_PROCESS = "10801";
  public static final String SELF_CANCEL_APPROVE = "19881";
  public static final String ONCHANGE = "10031"; //选择仓库提交
  public static final String DETAIL_COPY = "10041"; //从表复制多行触发事件
  public static final String TRANSFERSCAN = "10051"; //读盘点机触发事件
  public static final String NEW_TRANSFERSCAN = "10061"; //读新盘点机触发事件
  public static final String SHOW_DETAIL = "12500"; //调用从表明细资料
  public static final String REPORT = "2000"; //02.23 11:26 新增 为配合小李的报表追踪调用事件而加 yjg
  public static final String NEXT = "9999"; //新增 为上一笔,下一笔打印而加的
  public static final String PRIOR = "9998"; //新增 为上一笔,下一笔打印而加的
  public static final String MATCHING_BATCH = "2001"; //配批号触发事件
  public static final String DELETE_BLANK = "2002"; //删除数量为空行触发事件
  public static final String RECODE_ACCOUNT = "2005"; //记帐触发事件
  public static final String COPY_SELF = "2006"; //复制前几天的自制收货单触发事件
  public static final String MATERAIL_ADD = "2004"; //工人工作量物料新增一行触发事件
  public static final String MATERAIL_DEL = "2005"; //工人工作量物料删除一行触发事件
  public static final String ACCOUNT_SUBWAGE = "2006"; //计算分切工资触发事件
  public static final String BATCH_DEL = "2007"; //批量删除触发事件
  public static final String TURNPAGE = "9996"; // 新增 为明细表格番页而加的事件

  private static final String MASTER_STRUT_SQL =
      "SELECT * FROM sc_receiveprod a WHERE 1<>1";
  private static final String MASTER_SQL = "SELECT * FROM sc_receiveprod  WHERE ? AND filialeID='?' AND isout='0' ? ORDER BY receivedate DESC,receiveCode DESC";
  private static final String DETAIL_STRUT_SQL =
      "SELECT * FROM sc_receiveproddetail WHERE 1<>1";
  private static final String DETAIL_SQL =
      "SELECT * FROM sc_receiveproddetail WHERE receiveid='?' ORDER BY jgdmxid,cpid"; //

  //查询过后总数量
  private static final String MASTER_SUM_SQL = "SELECT SUM(nvl(totalnum,0)) zsl, SUM(nvl(zgf,0)) zgf, SUM(nvl(zgf2,0)) zgf2, SUM(nvl(zgf3,0)) zgf3, "
      + " SUM((SELECT SUM(b.drawbignum) FROM sc_receiveproddetail b WHERE b.receiveid  = sc_receiveprod.receiveid)) zhssl FROM sc_receiveprod  WHERE  ? AND filialeid='?' AND isout='0' ?  ";
  //工人工资人员SQL
  private static final String WAGE_MASTERSTRUCT_SQL =
      "SELECT * FROM sc_grgz WHERE 1<>1";
  private static final String WAGE_MASTER_SQL =
      "SELECT * FROM sc_grgz WHERE receiveid='?' ";

  //工人工资人员SQL
  private static final String WAGE_DETAILSTRUCT_SQL =
      "SELECT * FROM sc_grgzry WHERE 1<>1";
  private static final String WAGE_DETAIL_SQL =
      "SELECT * FROM sc_grgzry WHERE grgzID='?' ";
  //工资删除SQL
  private static final String WAGE_MASTERDELETE_SQL =
      "SELECT * FROM sc_grgz WHERE receiveid='?' and gzzid IN('?') ";
  //工资删除SQL
  private static final String WAGE_DELETEDTAIL_SQL =
      "SELECT * FROM sc_grgzry WHERE grgzID IN('?') ";

  //自制收货单引入加工单，得到加工单物料的主配料。
  private static final String PROCESSMATERAIL_SQL = "SELECT a.* FROM sc_jgdwl a,sc_bom b WHERE a.cpid=b.cpid AND b.zjlx=5 AND a.jgdid='?'";

  //private static final String NEWDETAIL_SQL = "SELECT * FROM sc_receiveproddetail WHERE receiveid='?' ORDER BY receiveDetailID";//复制收货单用，不按产品排序
  private static final String SELF_SINGLE_PROCESS_SQL =
      "SELECT * FROM VW_STOREPROCESS_SEL_PROCESS WHERE jgdid= "; //自制收货单引入加工单明细信息
  //根据工作组得到人员信息
  private static final String GROUP_DETAIL_SQL =
      "SELECT a.personid, a.ryjs  FROM sc_gzzry a WHERE a.gzzid = ? "; //
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL =
      "SELECT * FROM sc_receiveprod WHERE receiveid='?'";

  //查询数据库是否有记账的单据
  private static final String RECODE_DATASQL = " SELECT COUNT(*) FROM sc_receiveprod a WHERE a.creatorid='?' AND  a.isout ='0' AND a.state=1";
  //把符合记帐功能的数据全部记帐
  private static final String RECODE = "UPDATE sc_receiveprod a SET a.state=2 WHERE a.creatorid='?' AND  a.isout ='0' AND a.state=1";
  //
  //2004-4-13 16:54 新增 用于添加从表信息的SQL，根据条件和汇总表得到信息.提供给盘点机使用. yjg
  private static final String STOCK_MATERIAL_LIST
      = "SELECT b.cpid, b.dmsxid, b.ph, SUM(nvl(b.zl,0)) kcsl "
      + "FROM   kc_wzmx b, vw_kc_dm_exist a "
      + "WHERE  b.cpid = a.cpid @ AND b.storeid = @ "
      + "GROUP BY b.storeid, b.cpid, b.dmsxid, b.ph";
  //取合格证数据
  private static final String QUALITY_CARD_SQL =
      "SELECT * FROM zl_certifiedCard WHERE @ ";

  private EngineDataSet dsMasterTable = new EngineDataSet(); //主表
  private EngineDataSet dsDetailTable = new EngineDataSet(); //从表
  private EngineDataSet dmsxidExistData = new EngineDataSet(); //代码属性是否存在的数据集
  //private EngineDataSet dsStockMaterialDetail = new EngineDataSet();//物资明细表的数据集
  private EngineDataSet dsQuality = new EngineDataSet(); //合格证的数据集
  private EngineDataSet dssl = new EngineDataSet(); //计算查询总数量

  private EngineDataSet dsMasterWage = new EngineDataSet(); //工人工资主表
  private EngineDataSet dsDetailWage = new EngineDataSet(); //工人工资人员
  private EngineDataSet dsPersonTable = new EngineDataSet(); //工作组信息

  public HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable,
      "sc_newreceiveprod");
  public HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable,
      "sc_newreceiveproddetail");
  public HtmlTableProducer table = new HtmlTableProducer(dsMasterTable,
      "sc_receiveprod", "sc_receiveprod"); //查询得到数据库中配置的字段
  private boolean isMasterAdd = true; //是否在添加状态
  public boolean isApprove = false; //是否在审批状态
  public boolean isDetailAdd = false; //从表是否在添加状态
  private long masterRow = -1; //保存主表修改操作的行记录指针
  private long swapdetailRow = -1; //保存从表修改操作的行记录指针
  private RowMap m_RowInfo = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private ArrayList d_PersonRowInfos = null; //工作组工作量人员信息
  private ArrayList d_ProcessMaterail = null; //工作组工作量物料

  public boolean isReport = false; //02.23 11:26 新增 为配合小李的报表追踪调用事件而加的这个标志 yjg

  private LookUp buyOrderBean = null; //采购单价的bean的引用, 用于提取采购单价
  private LookUp propertyBean = null; //产品信息的bean的引用, 用于提取产品信息
  private B_SingleSelf singleselfBean = null; //采购单价的bean的引用, 用于提取采购单价
  private Select_Batch selectBatchBean = null; //选择批号的bean的引用, 用于从表产品选择批号

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public String retuUrl = null;
  //public  int djxz =1;//单据性质

  public String loginId = ""; //登录员工的ID
  public String loginCode = ""; //登陆员工的编码
  public String loginName = ""; //登录员工的姓名
  public String loginDept = ""; //登录员工的部门
  public String bjfs = ""; //系统的报价方式
  private String isout = "0"; //是否外加工入库
  private String zzsl = ""; //总数量
  private String zzgf = ""; //总数量
  private String zzgf2 = ""; //总数量
  private String zzgf3 = ""; //总数量
  private String zzhssl = ""; //总数量
  //被分页后的数据集中某一个页面中从第几笔记录开始到第几笔数据结束.如第二页的资料范围是从第51-101笔
  public int min = 0;
  public int max = 0;
  public String isRepeat = "0"; //重定向，如果本业检测数据不正确的话isrepeat为1。将不翻页

  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null; //分公司ID
  public String isHandwork = null; //是否允许手工录入自制入库单
  private User user = null;
  public String SYS_APPROVE_ONLY_SELF = null; //提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public String SC_DRAW_MATERIAL = null; //生产领料配批是否以库存数量为准,1=以库存数量为准,0=没有变化
  public String SC_STORE_UNIT_STYLE = null; //计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  public String KC_PRODUCE_UNIT_STYLE = null; //计量单位和生产单位换算方式1=强制换算,0=仅空值时换算
  public String SYS_PRODUCT_SPEC_PROP = null; //生产用位换算的相关规格属性名称 得到的值为“宽度”……
  //private String SC_INSTORE_SORT_FIELD = null;//自制收货单明细的排序字段
  private boolean isGroup1Empty = true; //工作组一是否能空
  private boolean isGroup1Empty2 = true; //工作组2是否能空
  private boolean isGroup1Empty3 = true; //工作组3是否能空
  /**
   * 入库单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回入库单列表的实例
   */
  public static B_NewSelfGain getInstance(HttpServletRequest request) {
    B_NewSelfGain newSelfGainBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session) {
      String beanName = "newSelfGainBean";
      newSelfGainBean = (B_NewSelfGain) session.getAttribute(beanName);
      if (newSelfGainBean == null) {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        newSelfGainBean = new B_NewSelfGain();
        newSelfGainBean.qtyFormat = loginBean.getQtyFormat();

        newSelfGainBean.fgsid = loginBean.getFirstDeptID();
        newSelfGainBean.loginId = loginBean.getUserID();
        newSelfGainBean.loginName = loginBean.getUserName();
        newSelfGainBean.loginDept = loginBean.getDeptID();
        newSelfGainBean.bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");
        newSelfGainBean.isHandwork = loginBean.getSystemParam(
            "KC_HANDIN_STOCK_BILL"); //是否可以手工添加系统参数1=允许手工输入,0=不允许手工输入
        newSelfGainBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam(
            "SYS_APPROVE_ONLY_SELF"); //提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        newSelfGainBean.SC_DRAW_MATERIAL = loginBean.getSystemParam(
            "SC_DRAW_MATERIAL"); //生产领料配批是否以库存数量为准,1=以库存数量为准,0=没有变化
        newSelfGainBean.SC_STORE_UNIT_STYLE = loginBean.getSystemParam(
            "SC_STORE_UNIT_STYLE"); //计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
        newSelfGainBean.KC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam(
            "KC_PRODUCE_UNIT_STYLE"); //计量单位和生产单位换算方式1=强制换算,0=仅空值时换算
        newSelfGainBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam(
            "SYS_PRODUCT_SPEC_PROP"); //生产用位换算的相关规格属性名称 得到的值为“宽度”……
        //newSelfGainBean.SC_INSTORE_SORT_FIELD = loginBean.getSystemParam("SC_INSTORE_SORT_FIELD");//自制收货单明细的排序字段
        newSelfGainBean.user = loginBean.getUser();
        //设置格式化的字段
        newSelfGainBean.dsMasterTable.setColumnFormat("totalnum",
            newSelfGainBean.qtyFormat);
        newSelfGainBean.dsMasterTable.setColumnFormat("totalsum",
            newSelfGainBean.qtyFormat);
        newSelfGainBean.dsMasterTable.setColumnFormat("zgf",
            newSelfGainBean.qtyFormat);
        newSelfGainBean.dsDetailTable.setColumnFormat("drawnum",
            newSelfGainBean.qtyFormat);
        newSelfGainBean.dsDetailTable.setColumnFormat("producenum",
            newSelfGainBean.qtyFormat);
        newSelfGainBean.dsDetailTable.setColumnFormat("drawprice",
            newSelfGainBean.qtyFormat);
        newSelfGainBean.dsDetailTable.setColumnFormat("drawsum",
            newSelfGainBean.qtyFormat);
        newSelfGainBean.dsDetailTable.setColumnFormat("drawBigNum",
            newSelfGainBean.qtyFormat);
        newSelfGainBean.dsDetailTable.setColumnFormat("drawnum",
            newSelfGainBean.qtyFormat);
        newSelfGainBean.dsDetailTable.setColumnFormat("jjdj",
            newSelfGainBean.priceFormat);
        newSelfGainBean.dsDetailTable.setColumnFormat("jjgz",
            newSelfGainBean.priceFormat);
        session.setAttribute(beanName, newSelfGainBean);
      }
    }
    return newSelfGainBean;
  }

  /**
   * 构造函数
   */
  private B_NewSelfGain() {
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
  private final void jbInit() throws java.lang.Exception {
    setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    setDataSetProperty(dmsxidExistData, null);
    setDataSetProperty(dsMasterWage, WAGE_MASTERSTRUCT_SQL);
    setDataSetProperty(dsDetailWage, WAGE_MASTERSTRUCT_SQL);
    setDataSetProperty(dsPersonTable, null);
    setDataSetProperty(dsQuality, null);
    setDataSetProperty(dssl, null);
    //SC_INSTORE_SORT_FIELD = dataSetProvider.getSequence("SELECT  a.VALUE FROM systemparam a WHERE a.code='SC_INSTORE_SORT_FIELD'");
    //setDataSetProperty(dsStockMaterialDetail, null);
    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"receiveCode"}, new String[]{"SELECT pck_base.billNextCode('sc_receiveprod','receiveCode') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[] {"receivedate",
                                             "receiveCode"}, new boolean[] {true, true}, null,
                                             0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[] {
        "receiveDetailID"}, new String[] {"s_sc_receiveproddetail"}));
    dsMasterWage.setSequence(new SequenceDescriptor(new String[] {"grgzid"},
        new String[] {"S_SC_GRGZ"}));
    dsDetailWage.setSequence(new SequenceDescriptor(new String[] {"grgzryid"},
        new String[] {"S_SC_GRGZRY"}));
    //if(SC_INSTORE_SORT_FIELD==null)
    dsDetailTable.setSort(new SortDescriptor("", new String[] {"jgdmxid",
                                             "cpid"}, new boolean[] {false, false}, null,
                                             0));
    //else
    //dsDetailTable.setSort(new SortDescriptor("", new String[]{"receiveDetailID"}, new boolean[]{false}, null, 0));

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
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(SELF_SEL_PROCESS), new Self_Sel_Process()); //自制收货单引入加工单从表增加操作
    addObactioner(String.valueOf(SELF_CANCEL_APPROVE), new Cancel_Approve()); //生产领料单取消审批操作
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(DETAIL_COPY), new Detail_Copy()); //从表复制多行触发事件
    addObactioner(String.valueOf(DELETE_BLANK), new Delete_Blank()); //删除数量为空白行触发事件
    addObactioner(String.valueOf(TRANSFERSCAN), new transferScan()); //旧盘点机
    addObactioner(String.valueOf(NEW_TRANSFERSCAN), new transferScan()); //新盘点机
    addObactioner(SHOW_DETAIL, new Show_Detail()); //02.17 15:16 新增 新增查看从表明细资料事件发生时的触发操作类. yjg
    addObactioner(String.valueOf(REPORT), new Approve()); //2.14 新增报表追此事件 yjg
    addObactioner(String.valueOf(MATCHING_BATCH), new Matching_Batch()); //3.24 新增配批号触发事件
    addObactioner(NEXT, new Move_Cursor_ForPrint()); //下一页
    addObactioner(PRIOR, new Move_Cursor_ForPrint()); //上一页
    addObactioner(RECODE_ACCOUNT, new Recode_Account()); //生产领料单记帐触发事件
    addObactioner(COPY_SELF, new Copy_Self());
    addObactioner(BATCH_DEL, new Batch_Delete());
    addObactioner(TURNPAGE, new Turn_Page()); //翻页事件
    //addObactioner(String.valueOf(MATERAIL_ADD), new Materail_Add());//工作量物料增加事件
    //addObactioner(String.valueOf(MATERAIL_DEL), new Materail_Delete());//工作量物料增加事件
    //addObactioner(String.valueOf(ACCOUNT_SUBWAGE), new Account_Subwage());//计算分切工资触发事件
  }

  //----Implementation of the BaseAction abstract class
  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request,
                          HttpServletResponse response) {
    try {
      String operate = request.getParameter(OPERATE_KEY);
      if (operate != null && operate.trim().length() > 0) {
        RunData data = notifyObactioners(operate, request, response, null);
        if (data == null)
          return showMessage("无效操作", false);
        if (data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch (Exception ex) {
      if (dsMasterTable.isOpen() && dsMasterTable.changesPending())
        dsMasterTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event) {
    if (dsMasterTable != null) {
      dsMasterTable.close();
      dsMasterTable = null;
    }
    if (dsDetailTable != null) {
      dsDetailTable.close();
      dsDetailTable = null;
    }
    if (dsDetailWage != null) {
      dsDetailWage.close();
      dsDetailWage = null;
    }
    if (dsMasterWage != null) {
      dsMasterWage.close();
      dsMasterWage = null;
    }
    if (dsPersonTable != null) {
      dsPersonTable.close();
      dsPersonTable = null;
    }
    if (dssl != null) {
      dssl.close();
      dssl = null;
    }
    /**
         if(dsStockMaterialDetail != null){
      dsStockMaterialDetail.close();
      dsStockMaterialDetail = null;
         }
     */
    if (dmsxidExistData != null) {
      dmsxidExistData.close();
      dmsxidExistData = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    if (masterProducer != null) {
      masterProducer.release();
      masterProducer = null;
    }
    if (detailProducer != null) {
      detailProducer.release();
      detailProducer = null;
    }
  }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName() {
    return getClass();
  }

  /*得到总数量*/
  public final String getZsl() {
    return zzsl;
  }

  /*得到总数量*/
  public final String getZgf() {
    return zzgf;
  }

  /*得到总数量*/
  public final String getZhssl() {
    return zzhssl;
  }

  /*得到总数量*/
  public final String getZgf2() {
    return zzgf2;
  }

  /*得到总数量*/
  public final String getZgf3() {
    return zzgf3;
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd,
                                 boolean isInit) throws java.lang.Exception {
    //是否是主表
    if (isMaster) {
      if (isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();

      if (!isAdd)
        m_RowInfo.put(getMaterTable());
      else {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        m_RowInfo.put("createdate", today); //制单日期
        m_RowInfo.put("creator", loginName); //操作员
        m_RowInfo.put("receiveDate", today); //收发日期
        m_RowInfo.put("creatorid", loginId);
        m_RowInfo.put("deptid", loginDept); //部门
        //m_RowInfo.put("handlePerson", loginName);//经手人
      }
    }
    else {
      EngineDataSet dsDetail = dsDetailTable;
      if (d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if (isInit)
        d_RowInfos.clear();

      dsDetail.first();
      for (int i = 0; i < dsDetail.getRowCount(); i++) {
        RowMap row = new RowMap(dsDetail);
        row.put("InternalRow", String.valueOf(dsDetail.getInternalRow()));
        d_RowInfos.add(row);
        dsDetail.next();
      }
      /**自制收货单物料
             EngineDataSet dsMaterail = dsProcessMaterail;
             if(d_ProcessMaterail == null)
        d_ProcessMaterail = new ArrayList(dsMaterail.getRowCount());
             else if(isInit)
        d_ProcessMaterail.clear();

             dsMaterail.first();
             for(int j=0; j<dsMaterail.getRowCount(); j++)
             {
        RowMap row = new RowMap(dsMaterail);
        d_ProcessMaterail.add(row);
        dsMaterail.next();
             }
       */
    }
  }

  /**
   * 从表保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request) {
    RowMap rowInfo = getMasterRowinfo();
    //保存网页的所有信息
    rowInfo.put(request);

    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for (int i = min; i <= max; i++) {
      detailRow = (RowMap) d_RowInfos.get(i);
      detailRow.put("cpid", rowInfo.get("cpid_" + i));
      //rowInfo.remove("cpid_"+i);
      detailRow.put("kwid", rowInfo.get("kwid_" + i)); //库位
      detailRow.put("package_id", rowInfo.get("package_id_" + i));
      detailRow.put("funditemid", rowInfo.get("funditemid_" + i));
      //rowInfo.remove("kwid_"+i);
      detailRow.put("dmsxid", rowInfo.get("dmsxid_" + i)); //物资规格属性
      //rowInfo.remove("dmsxid_"+i);
      detailRow.put("drawNum", formatNumber(rowInfo.get("drawNum_" + i), qtyFormat)); //计量数量
      //rowInfo.remove("drawNum_"+i);
      detailRow.put("drawBigNum",  formatNumber(rowInfo.get("drawBigNum_" + i), qtyFormat)); //换算数量
      //rowInfo.remove("drawBigNum_"+i);
      detailRow.put("produceNum", formatNumber(rowInfo.get("produceNum_" + i), qtyFormat)); //生产数量
      //rowInfo.remove("produceNum_"+i);
      detailRow.put("batchNo", rowInfo.get("batchNo_" + i)); //批号
      //rowInfo.remove("batchNo_"+i);
      detailRow.put("memo", rowInfo.get("memo_" + i)); //备注
      //rowInfo.remove("memo_"+i);
      detailRow.put("isbatchno", rowInfo.get("isbatchno_" + i)); //是否批号跟踪
      //rowInfo.remove("isbatchno_"+i);
      detailRow.put("jjdj", formatNumber(rowInfo.get("jjdj_" + i), priceFormat)); //计件单价
      //rowInfo.remove("jjdj_"+i);
      detailRow.put("jjgz", formatNumber(rowInfo.get("jjgz_" + i), priceFormat)); //计件工资
      //rowInfo.remove("jjgz_"+i);
      detailRow.put("gx", rowInfo.get("gx_" + i)); //工序
      //rowInfo.remove("gx_"+i);
      detailRow.put("gylxid", rowInfo.get("gylxid_" + i)); //工艺路线ID
      //rowInfo.remove("gylxid_"+i);
      detailRow.put("jjff", rowInfo.get("jjff_" + i)); //计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
      //rowInfo.remove("jjff_"+i);
      detailRow.put("jjdj2",
                    formatNumber(rowInfo.get("jjdj2_" + i), priceFormat)); //计件单价
      //rowInfo.remove("jjdj2_"+i);
      detailRow.put("jjgz2",
                    formatNumber(rowInfo.get("jjgz2_" + i), priceFormat)); //计件工资
      //rowInfo.remove("jjgz2_"+i);
      detailRow.put("gx2", rowInfo.get("gx2_" + i)); //工序
      //rowInfo.remove("gx2_"+i);
      detailRow.put("jjff2", rowInfo.get("jjff2_" + i)); //计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
      //rowInfo.remove("jjff2_"+i);
      detailRow.put("jjdj3",
                    formatNumber(rowInfo.get("jjdj3_" + i), priceFormat)); //计件单价
      //rowInfo.remove("jjdj3_"+i);
      detailRow.put("jjgz3",
                    formatNumber(rowInfo.get("jjgz3_" + i), priceFormat)); //计件工资
      //rowInfo.remove("jjgz3_"+i);
      detailRow.put("gx3", rowInfo.get("gx3_" + i)); //工序
      //rowInfo.remove("gx3_"+i);
      detailRow.put("jjff3", rowInfo.get("jjff3_" + i)); //计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
      //rowInfo.remove("jjff3_"+i);
      detailRow.put("isdel", rowInfo.get("isdel_" + i)); //计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
      Object o = rowInfo.remove("ISDEL_" + i);
    }
  }

  /**
   *  从表配批号操作，可以看到当前行产品的库存数量
   */
  class Matching_Batch
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      int row = Integer.parseInt(req.getParameter("rownum"));
      String mutibatch = m_RowInfo.get("mutibatch_" + row);
      if (mutibatch.length() == 0)
        return;
      RowMap rowinfo = (RowMap) d_RowInfos.get(row);
      dsDetailTable.goToRow(row);
      String receiveID = dsDetailTable.getValue("receiveID");
      String jgdmxid = dsDetailTable.getValue("jgdmxid");
      String cpid = rowinfo.get("cpid");
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable,
                                               new String[] {"dmsxid",
                                               "batchNo"});
      String[] wzmxIDs = parseString(mutibatch, ",");
      RowMap detail = null;
      for (int i = 0; i < wzmxIDs.length; i++) {
        if (wzmxIDs[i].equals("-1"))
          continue;
        RowMap batchRow = getBatchBean(req).getLookupRow(wzmxIDs[i]);
        String ph = batchRow.get("ph");
        String dmsxid = batchRow.get("dmsxid");
        locateGoodsRow.setValue(0, dmsxid);
        locateGoodsRow.setValue(1, ph);
        if (!dsDetailTable.locate(locateGoodsRow, Locate.FIRST)) {
          if (i == 0) {
            detail = (RowMap) d_RowInfos.get(row);
          }
          else {
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("receiveID", receiveID);
            dsDetailTable.setValue("jgdmxid", jgdmxid);
            dsDetailTable.setValue("cpid", cpid);
            dsDetailTable.post();
            detail = new RowMap(dsDetailTable);
            d_RowInfos.add(++row, detail);
          }
          detail.put("wzmxid", "1"); //2004-06-14 09:21 修改原来的batchRow.get("wzmxid")为只赋值为1
          detail.put("batchNo", batchRow.get("ph"));
          detail.put("dmsxid", batchRow.get("dmsxid"));
          detail.put("kwid", batchRow.get("kwid"));
          if (SC_DRAW_MATERIAL.equals("1"))
            detail.put("drawNum", batchRow.get("zl"));
          if (i == 0) {
            dsDetailTable.goToRow(row);
            dsDetailTable.setValue("wzmxid", "1"); //2004-06-14 09:21 修改原来的batchRow.get("wzmxid")为只赋值为1
            dsDetailTable.setValue("batchNo", batchRow.get("ph"));
            dsDetailTable.setValue("dmsxid", batchRow.get("dmsxid"));
            dsDetailTable.setValue("kwid", batchRow.get("kwid"));
            if (SC_DRAW_MATERIAL.equals("1"))
              dsDetailTable.setValue("drawNum", batchRow.get("zl"));
            dsDetailTable.post();
          }
        }
        else {
          detail = (RowMap) d_RowInfos.get(row);
          detail.put("wzmxid", "1"); //2004-06-14 09:21 修改原来的batchRow.get("wzmxid")为只赋值为1
          detail.put("batchNo", batchRow.get("ph"));
          detail.put("dmsxid", batchRow.get("dmsxid"));
          detail.put("kwid", batchRow.get("kwid"));
          if (SC_DRAW_MATERIAL.equals("1"))
            detail.put("drawNum", batchRow.get("zl"));
        }
      }
      data.setMessage(showJavaScript("big_change();"));
    }
  }

  /**
   * 得到用于选择批号信息的bean
   * @param req WEB的请求
   * @return 返回用于某批号的这一行库存物资明细信息的bean
   */
  public Select_Batch getBatchBean(HttpServletRequest req) {
    if (selectBatchBean == null)
      selectBatchBean = Select_Batch.getInstance(req);
    return selectBatchBean;
  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable() {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getDetailTable() {
    if (!dsDetailTable.isOpen())
      dsDetailTable.open();
    return dsDetailTable;
  }

  /*得到物料从表表对象
     public final EngineDataSet getMaterailTable(){
    if(!dsProcessMaterail.isOpen())
      dsProcessMaterail.open();
    return dsProcessMaterail;
     }
   */
  /**
   * 得到选中的行的行数
   * @return 若返回-1，表示没有选中的行
   */
  public final int getSelectedRow() {
    if (masterRow < 0)
      return -1;

    dsMasterTable.goToInternalRow(masterRow);
    return dsMasterTable.getRow();
  }

  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd) {
    String receiveID = dsMasterTable.getValue("receiveID");
    //02.28 23:15  修改 将下面此句setQueryString中的sql由原来的手动用+号组成sql`改成现在用combineSQL来组成.
    //解决了当sdfdjid是空的时候会页面上(主要是contract_instore_bottom.jsp上)会出现sql错误的问题.yjg
    //String SQL = ( SC_INSTORE_SORT_FIELD==null) ? DETAIL_SQL : NEWDETAIL_SQL;//是否需要排序
    dsDetailTable.setQueryString(combineSQL(DETAIL_SQL, "?",
                                            new String[] {isMasterAdd ? "-1" :
                                            receiveID}));

    if (dsDetailTable.isOpen())
      dsDetailTable.refresh();
    else
      dsDetailTable.open();
    /**打开物料数据集
         String sql = combineSQL(SELF_MATERAIL_SQL,"?", new String[]{isMasterAdd ? "-1" : receiveID});
         dsProcessMaterail.setQueryString(sql);
         if(!dsProcessMaterail.isOpen())
      dsProcessMaterail.open();
         else
      dsProcessMaterail.refresh();
         //打开人员数据集
         String p_sql = combineSQL(DETAIL_PERSON_SQL,"?", new String[]{isMasterAdd ? "-1" : receiveID});
         dsDetailPerson.setQueryString(p_sql);
         if(!dsDetailPerson.isOpen())
      dsDetailPerson.open();
         else
      dsDetailPerson.refresh();
     */
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() {
    return m_RowInfo;
  }

  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }

  /*得到物料多列的信息*/
  public final RowMap[] getMaterailRowinfos() {
    RowMap[] rows = new RowMap[d_ProcessMaterail.size()];
    d_ProcessMaterail.toArray(rows);
    return rows;
  }

  /**
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {
    return isMasterAdd;
  }

  /**
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col) {
    return fixedQuery.getSearchRow().get(col);
  }

  /**
   * 初始化操作的触发类
   */
  class Init
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      isApprove = false;
      isReport = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl != null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      table.getWhereInfo().clearWhereValues();
      isMasterAdd = true;
      isDetailAdd = false;
      table.getWhereInfo().clearWhereValues();
      //
      String temp = " AND state<>8 AND state<>2 ";
      String SQL = combineSQL(MASTER_SQL, "?",
                              new String[] {user.
                              getHandleDeptWhereValue("deptid", "creatorID"),
                              fgsid, temp});

      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if (dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();

      String SUM_SLSQL = combineSQL(MASTER_SUM_SQL, "?",
                                    new String[] {user.
                                    getHandleDeptWhereValue("deptid",
          "creatorID"), fgsid, temp});

      dssl.setQueryString(SUM_SLSQL);
      if (dssl.isOpen())
        dssl.refresh();
      else
        dssl.openDataSet();
      int cn = dssl.getRowCount();
      if (dssl.getRowCount() < 1) {
        zzsl = "0";
        zzgf = "0";
        zzgf2 = "0";
        zzgf3 = "0";
        zzhssl = "0";
      }
      else {
        zzsl = dssl.getValue("zsl");
        zzgf = dssl.getValue("zgf");
        zzgf2 = dssl.getValue("zgf2");
        zzgf3 = dssl.getValue("zgf3");
        zzhssl = dssl.getValue("zhssl");
      }

      zzsl = zzsl.equals("") ? "0" : zzsl;
      zzsl = formatNumber(zzsl, qtyFormat);

      zzgf = zzgf.equals("") ? "0" : zzgf;
      zzgf = formatNumber(zzgf, qtyFormat);

      zzgf2 = zzgf2.equals("") ? "0" : zzgf2;
      zzgf2 = formatNumber(zzgf2, qtyFormat);

      zzgf3 = zzgf3.equals("") ? "0" : zzgf3;
      zzgf3 = formatNumber(zzgf3, qtyFormat);

      zzhssl = zzhssl.equals("") ? "0" : zzhssl;
      zzhssl = formatNumber(zzhssl, qtyFormat);
    }
  }

  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      isApprove = false;
      isReport = false;
      isDetailAdd = false;
      if (String.valueOf(EDIT).equals(action)) {
        isMasterAdd = false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
      }
      else
        isMasterAdd = true;
      synchronized (dsDetailTable) {
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
  class Approve
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      /**
       *报表调从表页面,传递operate='2000'操作
       */
      isReport = String.valueOf(REPORT).equals(action);
      String id = null;
      if (isReport) {
        isReport = true;
        isApprove = false;
        id = data.getParameter("id"); //得到报表传递的参数既收发单据主表ID
      }
      else {
        isReport = false;
        isApprove = true; //审批操作
        id = data.getParameter("id", "");
      }
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[] {id});
      dsMasterTable.setQueryString(sql);
      if (dsMasterTable.isOpen()) {
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
  class Add_Approve
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterTable.getValue("receiveCode");
      //String isout = dsMasterTable.getValue("isout");
      //03.16 15:04 修改 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
      //以实现:mantis上库存管理中0000158bug描述的:根据下达的部门，进行提交审批；
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(),
                            "self_gain_list", content,
                            dsMasterTable.getValue("deptid"));
    }
  }

  /**
   * 取消审批触发操作
   */
  class Cancel_Approve
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable, dsMasterTable.getRow(),
                           "self_gain_list");
    }
  }

  /**
   * 自制收货单生成工资
   * 仅分切生成
   * 在提交审核时调用
   */
  private void buildWage(ArrayList gzzids, ArrayList zgfArray) throws Exception {
    //根据生产领料单得到工作组，根据工作组得到工作组人员和人员基数
    if (gzzids.size() < 1)
      return;

    String temp = StringUtils.getArrayValue( (String[]) gzzids.toArray(new
        String[gzzids.size()]), "','");
    //根据自制收货单ID打开工人工资表
    String receiveID = dsMasterTable.getValue("receiveID");
    dsMasterWage.setQueryString(combineSQL(WAGE_MASTER_SQL, "?",
                                           new String[] {receiveID}));
    if (dsMasterWage.isOpen())
      dsMasterWage.refresh();
    else
      dsMasterWage.openDataSet();
    ArrayList grgzids = new ArrayList();
    if (dsMasterWage.getRowCount() > 0) {
      dsMasterWage.first();
      for (int y = 0; y < dsMasterWage.getRowCount(); y++) {
        grgzids.add(dsMasterWage.getValue("grgzid"));
        dsMasterWage.next();
      }
      String grgztemp = StringUtils.getArrayValue( (String[]) grgzids.toArray(new
          String[grgzids.size()]), "','");
      dsDetailWage.setQueryString(combineSQL(WAGE_DELETEDTAIL_SQL, "?",
                                             new String[] {grgztemp}));
      if (dsDetailWage.isOpen())
        dsDetailWage.refresh();
      else
        dsDetailWage.openDataSet();
      if (dsDetailWage.getRowCount() > 0)
        dsDetailWage.deleteAllRows();
      dsMasterWage.deleteAllRows();
    }
    else { //如果是新增的自制收货单，没有工人工资数据。打开工人工资人员结构数据集
      dsDetailWage.setQueryString(WAGE_DETAILSTRUCT_SQL);
      if (dsDetailWage.isOpen())
        dsDetailWage.refresh();
      else
        dsDetailWage.openDataSet();
    }
    for (int q = 0; q < gzzids.size(); q++) {
      String gzzid = (String) gzzids.get(q);
      BigDecimal zgf = (BigDecimal) zgfArray.get(q);
      dsPersonTable.setQueryString(combineSQL(GROUP_DETAIL_SQL, "?",
                                              new String[] {gzzid}));
      if (dsPersonTable.isOpen())
        dsPersonTable.refresh();
      else
        dsPersonTable.openDataSet();
      dsMasterWage.insertRow(false);
      String grgzid = dataSetProvider.getSequence("S_SC_GRGZ");
      dsMasterWage.setValue("grgzid", grgzid);
      dsMasterWage.setValue("receiveID", receiveID);
      dsMasterWage.setValue("gzzid", gzzid);
      dsMasterWage.setValue("deptid", dsMasterTable.getValue("deptid"));
      dsMasterWage.setValue("zgf", formatNumber(zgf.toString(), priceFormat));
      dsMasterWage.setValue("djrq", dsMasterTable.getValue("receiveDate"));
      String djh = dsMasterTable.getValue("receiveCode");
      dsMasterWage.setValue("djh", dsMasterTable.getValue("receiveCode"));
      dsMasterWage.setValue("jsr", dsMasterTable.getValue("handlePerson"));
      dsMasterWage.setValue("bc", dsMasterTable.getValue("bc"));
      dsMasterWage.post();

      dsPersonTable.first();
      for (int m = 0; m < dsPersonTable.getRowCount(); m++) {
        BigDecimal ryjs = dsPersonTable.getBigDecimal("ryjs");
        BigDecimal jjgz = ryjs.multiply(zgf);
        dsDetailWage.insertRow(false);
        dsDetailWage.setValue("grgzryID", "-1");
        dsDetailWage.setValue("grgzID", grgzid);
        dsDetailWage.setValue("personid", dsPersonTable.getValue("personid"));
        dsDetailWage.setValue("bl", dsPersonTable.getValue("ryjs"));
        dsDetailWage.setValue("jjgz", formatNumber(jjgz.toString(), priceFormat));
        dsDetailWage.post();
        dsPersonTable.next();
      }
    }
  }

  /**
   * 主表保存操作的触发类
   */
  class Master_Post       implements Obactioner {
    EngineDataSet tempDataSet = null;
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      putDetailInfo(data.getRequest());

      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      //校验表单数据
      String temp = checkMasterInfo();
      if (temp != null) {
        data.setMessage(temp);
        return;
      }
      temp = checkDetailInfo();
      if (temp != null) {
        data.setMessage(temp);
        return;
      }
      //setSubWage(data,false);//计算分切工资
      if (!isMasterAdd)
        ds.goToInternalRow(masterRow);

      //得到主表主键值
      String receiveID = null;
      if (isMasterAdd) {
        ds.insertRow(false);
        receiveID = dataSetProvider.getSequence("s_sc_receiveproddetail");
        ds.setValue("receiveID", receiveID);
        ds.setValue("filialeID", fgsid); //分公司
        ds.setValue("state", "0");
        ds.setValue("createdate", new SimpleDateFormat("yyyy-MM-dd").format(new Date())); //制单日期
        ds.setValue("creatorid", loginId);
        ds.setValue("creator", loginName); //操作员
      }
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0),
          totalSum2 = new BigDecimal(0), totalSum3 = new BigDecimal(0);
      BigDecimal totalBigNum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      for (int i = 0; i < d_RowInfos.size(); i++) {
        detailrow = (RowMap) d_RowInfos.get(i);
        long internalRow = Long.parseLong(detailrow.get("InternalRow"));
        detail.goToInternalRow(internalRow);
        //新添的记录
        if (isMasterAdd)
          detail.setValue("receiveID", receiveID);

        String receiveDetailID = detail.getValue("receiveDetailID");
        String detailid = null;
        if (receiveDetailID.equals("-1") || receiveDetailID == null)
          detailid = dataSetProvider.getSequence("s_sc_receiveproddetail");
        detail.setValue("receiveDetailID", detailid);
        detail.setValue("drawNum", detailrow.get("drawNum")); //保存数量
        detail.setValue("drawBigNum", detailrow.get("drawBigNum")); //保存换算数量
        detail.setValue("produceNum", detailrow.get("produceNum")); //保存生产数量

        detail.setValue("package_id", detailrow.get("package_id"));
        detail.setValue("funditemid", detailrow.get("funditemid"));

        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("kwid", detailrow.get("kwid"));
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("batchNo", detailrow.get("batchNo"));
        detail.setValue("memo", detailrow.get("memo")); //备注
        detail.setValue("gx", detailrow.get("gx")); //工序
        detail.setValue("gylxid", detailrow.get("gylxid")); //工艺路线ID
        detail.setValue("jjdj", detailrow.get("jjdj")); //计件单价
        detail.setValue("jjgz", detailrow.get("jjgz")); //计件工资
        detail.setValue("jjff", detailrow.get("jjff"));
        detail.setValue("gx2", detailrow.get("gx2")); //工序
        detail.setValue("jjdj2", detailrow.get("jjdj2")); //计件单价
        detail.setValue("jjgz2", detailrow.get("jjgz2")); //计件工资
        detail.setValue("jjff2", detailrow.get("jjff2"));
        detail.setValue("gx3", detailrow.get("gx3")); //工序
        detail.setValue("jjdj3", detailrow.get("jjdj3")); //计件单价
        detail.setValue("jjgz3", detailrow.get("jjgz3")); //计件工资
        detail.setValue("jjff3", detailrow.get("jjff3"));


        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("drawNum"));

        totalBigNum = totalBigNum.add(detail.getBigDecimal("drawBigNum"));
        totalSum = totalSum.add(detail.getBigDecimal("jjgz"));
        totalSum2 = totalSum2.add(detail.getBigDecimal("jjgz2"));
        totalSum3 = totalSum3.add(detail.getBigDecimal("jjgz3"));
      }

      //保存主表数据
      ds.setValue("storeid", rowInfo.get("storeid")); //仓库id
      ds.setValue("handlePerson", rowInfo.get("handlePerson")); //经手人
      ds.setValue("deptid", rowInfo.get("deptid")); //部门id
      ds.setValue("ytid", rowInfo.get("ytid"));
      ds.setValue("sfdjlbid", rowInfo.get("sfdjlbid")); //收发单据类别ID
      ds.setValue("receiveDate", rowInfo.get("receiveDate")); //领料日期
      ds.setValue("totalnum", totalNum.toString()); //总数量
      ds.setValue("totalbignum", totalBigNum.toString()); //总数量
      ds.setValue("memo", rowInfo.get("memo")); //备注
      ds.setValue("isout", isout); //是否外加工
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));
      ds.setValue("zlyz", rowInfo.get("zlyz")); //质量验证
      ds.setValue("ydzs", rowInfo.get("ydzs")); //原断纸数
      ds.setValue("dzcs", rowInfo.get("dzcs")); //断纸次数
      ds.setValue("jhsh", rowInfo.get("jhsh")); //计划损耗
      ds.setValue("sjsh", rowInfo.get("sjsh")); //实际损耗
      ds.setValue("jf", rowInfo.get("jf")); //奖罚
      ds.setValue("shyy", rowInfo.get("shyy")); //损耗原因
      ds.setValue("jt", rowInfo.get("jt")); //机台
      ds.setValue("bc", rowInfo.get("bc")); //班次
      ds.setValue("gzzid", rowInfo.get("gzzid"));
      ds.setValue("zgf", totalSum.toString()); //总工费
      ds.setValue("sc__gzzID", rowInfo.get("sc__gzzID"));
      ds.setValue("sc__gzzID2", rowInfo.get("sc__gzzID2"));
      ds.setValue("zgf2", totalSum2.toString()); //总工费
      ds.setValue("zgf3", totalSum3.toString()); //总工费
      if (ds.getValue("receiveCode").equals("")) {
        String receiveCode = dataSetProvider.getSequence(
            "SELECT pck_base.billNextCode('sc_receiveprod','receiveCode') from dual");
        ds.setValue("receiveCode", receiveCode);
      }

      ds.post();

      String gzzid = ds.getValue("gzzid");
      String sc__gzzid = ds.getValue("sc__gzzid");
      String sc__gzzid2 = ds.getValue("sc__gzzid2");
      ArrayList gzzidArray = new ArrayList();
      ArrayList zgfArray = new ArrayList();
      BigDecimal zgf = ds.getBigDecimal("zgf");
      BigDecimal zgf2 = ds.getBigDecimal("zgf2");
      BigDecimal zgf3 = ds.getBigDecimal("zgf3");
      if (!gzzid.equals("") && !isGroup1Empty) {
        gzzidArray.add(gzzid);
        zgfArray.add(zgf);
      }
      if (!sc__gzzid.equals("") && !isGroup1Empty2) {
        gzzidArray.add(sc__gzzid);
        zgfArray.add(zgf2);
      }
      if (!sc__gzzid2.equals("") && !isGroup1Empty3) {
        gzzidArray.add(sc__gzzid2);
        zgfArray.add(zgf3);
      }
      if (gzzidArray.size() > 0) {
        buildWage(gzzidArray, zgfArray);
        ds.saveDataSets(new EngineDataSet[] {ds, detail, dsMasterWage,
                        dsDetailWage}, null);
      }
      else
        ds.saveDataSets(new EngineDataSet[] {ds, detail}, null);
      //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"receiveCode"}, new String[]{"SELECT pck_base.billNextCode('sc_receiveprod','receiveCode') from dual"}));
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_IN);
      //LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_OUT);

      if (String.valueOf(POST_CONTINUE).equals(action)) {
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
        initRowInfo(false, true, true); //重新初始化从表的各行信息
      }
      else if (String.valueOf(POST).equals(action)) {
        isMasterAdd = false;
        masterRow = ds.getInternalRow(); //2004-3-30 14:53 新增 如果是保存按钮行为完成后定位数据集 yjg
        initRowInfo(true, isMasterAdd, true);
        initRowInfo(false, isMasterAdd, true);
      }
    }

    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo() throws Exception {
      isGroup1Empty = true; //工作组一是否能空
      isGroup1Empty2 = true; //工作组2是否能空
      isGroup1Empty3 = true; //工作组3是否能空
      String temp = null;
      RowMap detailrow = null;
      if (d_RowInfos.size() < 1)
        return showJavaScript("alert('不能保存空的数据')");
      ArrayList list = new ArrayList(d_RowInfos.size()); //用于存放收货单明细的批号，判断批号是否相同
      for (int i = 0; i < d_RowInfos.size(); i++) {
        int row = i + 1;
        detailrow = (RowMap) d_RowInfos.get(i);
        String cpid = detailrow.get("cpid");
        if (cpid.equals(""))
          return showJavaScript("alert('第" + row + "行产品不能为空');");
        String batchNo = detailrow.get("batchNo");
        String isbatchno = detailrow.get("isbatchno");
        //如果收货单明细中产品相同批号也相同，并且该产品设置为批号跟踪则不能保存。设置是否批号跟踪在物资信息中设置
        if (!batchNo.equals("") && isbatchno.equals("1")) {
          if (list.contains(batchNo))
            return showJavaScript("alert('第" + row + "行批号重复');");
          else
            list.add(batchNo);
        }
        String sl = detailrow.get("drawNum");
        if ( (temp = checkNumber(sl, "第" + row + "行数量")) != null)
          return temp;
        if (sl.length() > 0 && sl.equals("0"))
          return showJavaScript("alert('第" + row + "行数量不能为零！');");
        String hssl = detailrow.get("drawbignum");
        if (hssl.length() > 0 && (temp = checkNumber(hssl, "第" + row + "行换算数量")) != null)
          return temp;
        String scsl = detailrow.get("produceNum");
        if (scsl.length() > 0 && (temp = checkNumber(scsl, "第" + row + "行生产数量")) != null)
          return temp;
        String jjff = detailrow.get("jjff");
        String jjgz = detailrow.get("jjgz");
        if (!jjff.equals("") && !jjff.equals("3") && !jjff.equals("4") &&
            !jjgz.equals(""))
          isGroup1Empty = false;

        String jjff2 = detailrow.get("jjff2");
        String jjgz2 = detailrow.get("jjgz2");
        if (!jjff2.equals("") && !jjff2.equals("3") && !jjff2.equals("4") &&
            !jjgz2.equals(""))
          isGroup1Empty2 = false;

        String jjff3 = detailrow.get("jjff3");
        String jjgz3 = detailrow.get("jjgz2");
        if (!jjff3.equals("") && !jjff3.equals("3") && !jjff3.equals("4") &&
            !jjgz3.equals(""))
          isGroup1Empty3 = false;
      }
      RowMap rowInfo = getMasterRowinfo();
      String gzzid = rowInfo.get("gzzid");
      String sc__gzzid = rowInfo.get("sc__gzzid");
      String sc__gzzid2 = rowInfo.get("sc__gzzid2");

      if (!isGroup1Empty && gzzid.equals(""))
        return showJavaScript("alert('请选择工作组1');");

      if (!isGroup1Empty2 && sc__gzzid.equals(""))
        return showJavaScript("alert('请选择工作组2');");

      if (!isGroup1Empty3 && sc__gzzid2.equals(""))
        return showJavaScript("alert('请选择工作组3');");

      RowMap temprow = null;
      for (int t = 0; t < d_RowInfos.size(); t++) {
        temprow = (RowMap) d_RowInfos.get(t);
        int row = t + 1;
        String cpid = temprow.get("cpid");
        String batchNo = temprow.get("batchNo");
        String isbatchno = temprow.get("isbatchno");
        //如果收货单明细中产品相同批号也相同，并且该产品设置为批号跟踪则不能保存。设置是否批号跟踪在物资信息中设置
        if (!batchNo.equals("") && isbatchno.equals("1")) {
          String count = "0";
          if (isMasterAdd)
            count = dataSetProvider.getSequence(
                "SELECT COUNT(*) FROM vw_kc_storebill t WHERE t.djxz IN(1,3,5,7,9) AND t.ph='" +
                batchNo + "' AND t.cpid='" + cpid + "'");
          else {
            String receiveid = dsMasterTable.getValue("receiveID");
            count = dataSetProvider.getSequence(
                "SELECT COUNT(*) FROM vw_kc_storebill t WHERE t.djxz IN(1,3,5,7,9) AND t.ph='" +
                batchNo + "' AND t.cpid='" + cpid + "' AND t.sfdjid<>'" +
                receiveid + "'");
          }
          if (!count.equals("0"))
            return showJavaScript("alert('第" + row + "行批号" + batchNo +
                                  "在库存中已存在');");
        }
      }
      return null;
    }

    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo() {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("receiveDate");
      if (temp.equals(""))
        return showJavaScript("alert('收货日期不能为空！');");
      else if (!isDate(temp))
        return showJavaScript("alert('非法收发日期！');");
      temp = rowInfo.get("storeid");
      if (temp.equals(""))
        return showJavaScript("alert('请选择仓库！');");
      temp = rowInfo.get("deptid");
      if (temp.equals(""))
        return showJavaScript("alert('请选择部门！');");
      return null;
    }
  }

  /**
   * 记帐操作的触发类
   */
  class Recode_Account
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      //是否有符合记帐的数据，有几条
      String SQL = combineSQL(RECODE_DATASQL, "?", new String[] {loginId});
      String UPDATE_SQL = combineSQL(RECODE, "?", new String[] {loginId});
      String count = dataSetProvider.getSequence(SQL);
      if (count.equals("0")) {
        data.setMessage(showJavaScript("alert('没有可以记帐的单据')"));
        return;
      }
      else {
        dsMasterTable.updateQuery(new String[] {UPDATE_SQL});
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
    }
  }

  /**
   * 主表删除操作
   */
  class Master_Delete
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      if (isMasterAdd) {
        data.setMessage(showJavaScript("backList();"));
        return;
      }
      EngineDataSet ds = getMaterTable();
      //删除工人工资
      String receiveid = dsMasterTable.getValue("receiveid");
      ArrayList gzzids = new ArrayList();
      String gzzid = dsMasterTable.getValue("gzzid");
      if (!gzzid.equals(""))
        gzzids.add(gzzid);
      String sc__gzzid = dsMasterTable.getValue("sc__gzzid");
      if (!sc__gzzid.equals(""))
        gzzids.add(sc__gzzid);
      String sc__gzzid2 = dsMasterTable.getValue("sc__gzzid2");
      if (!sc__gzzid2.equals(""))
        gzzids.add(sc__gzzid2);

      String temp = StringUtils.getArrayValue( (String[]) gzzids.toArray(new
          String[gzzids.size()]), "','");

      dsMasterWage.setQueryString(combineSQL(WAGE_MASTER_SQL, "?",
                                             new String[] {receiveid}));
      if (dsMasterWage.isOpen())
        dsMasterWage.refresh();
      else
        dsMasterWage.openDataSet();

      if (dsMasterWage.getRowCount() > 0) {
        ArrayList grgzids = new ArrayList(dsMasterWage.getRowCount());
        dsMasterWage.first();
        for (int j = 0; j < dsMasterWage.getRowCount(); j++) {
          String grgzid = dsMasterWage.getValue("grgzid");
          if (!grgzid.equals(""))
            grgzids.add(grgzid);
          dsMasterWage.next();
        }
        String temp2 = StringUtils.getArrayValue( (String[]) grgzids.toArray(new
            String[grgzids.size()]), "','");
        dsDetailWage.setQueryString(combineSQL(WAGE_DELETEDTAIL_SQL, "?",
                                               new String[] {temp2}));
        if (dsDetailWage.isOpen())
          dsDetailWage.refresh();
        else
          dsDetailWage.openDataSet();
        if (dsDetailWage.getRowCount() > 0)
          dsDetailWage.deleteAllRows();
      }
      dsMasterWage.deleteAllRows();

      ds.goToInternalRow(masterRow);
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[] {ds, dsDetailTable, dsMasterWage,
                      dsDetailWage}, null);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_IN);
      //LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_OUT);
      //
      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }

  /**
   *  查询操作
   */
  class Master_Search
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      table.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if (SQL.length() > 0)
        SQL = " AND " + SQL;
      String NEWSQL = combineSQL(MASTER_SQL, "?",
                                 new String[] {user.
                                 getHandleDeptWhereValue("deptid", "creatorID"),
                                 fgsid, SQL});
      dsMasterTable.setQueryString(NEWSQL);
      dsMasterTable.setRowMax(null);

      String SUM_SLSQL = combineSQL(MASTER_SUM_SQL, "?",
                                    new String[] {user.
                                    getHandleDeptWhereValue("deptid",
          "creatorID"), fgsid, SQL});

      dssl.setQueryString(SUM_SLSQL);
      if (dssl.isOpen())
        dssl.refresh();
      else
        dssl.openDataSet();
      int cn = dssl.getRowCount();
      if (dssl.getRowCount() < 1) {
        zzsl = "0";
        zzgf = "0";
        zzgf2 = "0";
        zzgf3 = "0";
        zzhssl = "0";
      }
      else {
        zzsl = dssl.getValue("zsl");
        zzgf = dssl.getValue("zgf");
        zzgf2 = dssl.getValue("zgf2");
        zzgf3 = dssl.getValue("zgf3");
        zzhssl = dssl.getValue("zhssl");
      }

      zzsl = zzsl.equals("") ? "0" : zzsl;
      zzsl = formatNumber(zzsl, qtyFormat);

      zzgf = zzgf.equals("") ? "0" : zzgf;
      zzgf = formatNumber(zzgf, qtyFormat);

      zzgf2 = zzgf2.equals("") ? "0" : zzgf2;
      zzgf2 = formatNumber(zzgf2, qtyFormat);

      zzgf3 = zzgf3.equals("") ? "0" : zzgf3;
      zzgf3 = formatNumber(zzgf3, qtyFormat);

      zzhssl = zzhssl.equals("") ? "0" : zzhssl;
      zzhssl = formatNumber(zzhssl, qtyFormat);
    }
  }

  /**
   *  从表增加操作(增加一个空白行)
   */
  class Detail_Add
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
      if (!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String receiveID = dsMasterTable.getValue("receiveID");
      detail.insertRow(false);
      detail.setValue("receiveID", isMasterAdd ? "-1" : receiveID);
      detail.setValue("receiveDetailID", "-1");
      detail.post();
      RowMap detailrow = new RowMap(detail);
      detailrow.put("InternalRow", String.valueOf(detail.getInternalRow()));
      d_RowInfos.add(detailrow);
    }
  }

  /**
   *  计算分切工资触发事件
     class Account_Subwage implements Obactioner
     {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(dsProcessMaterail.getRowCount()<1)
        return;
      putDetailInfo(data.getRequest());
      setSubWage(data, true);
    }
     }
     /**
    * 计算分切工资函数
    * 循环自制收货单从表数据集，如果工序为空返回
    * 如果从表中有不同的产品计价方法为3，返回
    * 物料为空返回
    * 计价为三的相同产品工序也要相等
    * 得到计件单价乘物料数量在除以物料的宽度，即计件工资
    * 产品的计件工资为加权平均值

      private void setSubWage(RunData data, boolean isCollect){
     BigDecimal jjdj = new BigDecimal(0);
     BigDecimal zsl = new BigDecimal(0);
     //判断是否有某一行明细不是通过领料来计算工资的，如果有将返回不计算
     ArrayList cpids= new ArrayList();
     ArrayList gxs = new ArrayList();
     dsDetailTable.first();
     for(int t=0; t<dsDetailTable.getRowCount(); t++){
       int k = t+1;
       RowMap detailRow = (RowMap)d_RowInfos.get(t);
       String gylxid = detailRow.get("gylxid");
       String cpid = detailRow.get("cpid");
       if(cpid.equals(""))
       {
         data.setMessage(showJavaScript("alert('第"+k+"行产品不能为空')"));
         return;
       }
       if(gylxid.equals(""))
       {
         data.setMessage(showJavaScript("alert('第"+k+"行工艺路线不能为空')"));
         return;
       }
       String gx = detailRow.get("gx");
       if(gx.equals(""))
       {
         data.setMessage(showJavaScript("alert('第"+k+"行工序路线不能为空')"));
         return;
       }
       String jjff = detailRow.get("jjff");//以领料数量计算工资
       if(jjff.equals("3")){

         if(cpids.size()!=0 && !cpids.contains(cpid))
         {
           data.setMessage(showJavaScript("alert('含有不同的产品参与计算分切工资')"));
           return;
         }
         else
           cpids.add(cpid);
         if(gxs.size()!=0 && !gxs.contains(gx))
         {
           data.setMessage(showJavaScript("alert('含有不同的工序参与计算分切工资')"));
           return;
         }
         else
           gxs.add(gx);
    if((jjdj.compareTo(new BigDecimal(0))==0) && isDouble(detailRow.get("jjdj")))
           jjdj= new BigDecimal(detailRow.get("jjdj"));
         BigDecimal sl = new BigDecimal(0);
         if(isDouble(detailRow.get("drawNum")))
           sl = new BigDecimal(detailRow.get("drawNum"));
         zsl = zsl.add(sl);
       }
       dsDetailTable.next();
     }
     BigDecimal totalWage = new BigDecimal(0);
     BigDecimal b_wid = new BigDecimal(0);
     BigDecimal sl = new BigDecimal(0);
     BigDecimal  gf = new BigDecimal(0);
     //通过加工物料计算工资
     dsProcessMaterail.first();
     for(int i=0; i<dsProcessMaterail.getRowCount(); i++){
       RowMap materailRow = (RowMap)d_ProcessMaterail.get(i);
       String dmsxid = materailRow.get("dmsxid");
       String sxz = getPropertyBean(data.getRequest()).getLookupName(dmsxid);
       String width  = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP,"()");
       double d_wid = width.equals("0") ? 1 : Double.parseDouble(width);
       b_wid = new BigDecimal(d_wid);
       if(isDouble(materailRow.get("sl")))
         sl=new BigDecimal(materailRow.get("sl"));
       gf = sl.multiply(jjdj).divide(b_wid,4);
       totalWage = totalWage.add(gf);
       dsProcessMaterail.next();
     }
     //m_RowInfo.put("zgf", String.valueOf(totalWage));
     //计算明细的计件工资，即把平均工资计算出来
     dsDetailTable.first();
     for(int j=0;j<dsDetailTable.getRowCount(); j++){
       RowMap tempRow = (RowMap)d_RowInfos.get(j);
       String jjff = tempRow.get("jjff");//以领料数量计算工资
       if(jjff.equals("3")){
         BigDecimal sl1 = new BigDecimal(0);
         if(isDouble(tempRow.get("drawNum")))
           sl1 = new BigDecimal(tempRow.get("drawNum"));
         BigDecimal bl = sl1.divide(zsl,6,BigDecimal.ROUND_HALF_UP);
         tempRow.put("jjgz", formatNumber(String.valueOf(totalWage.multiply(bl)), qtyFormat));
       }
       dsDetailTable.next();
     }
     if(isCollect)
     data.setMessage(showJavaScript("cal_tot('jjgz')"));
      }
    */
   /**
    *  自制收货单从表增加操作
    *  显示的加工单必须是该加工单至少有一条明细还未入库完，并且存放仓库和所选仓库要相同的数据才引过来，空的也过来
    *  引入的数据来源于加工单主表
    *  单选加工单主表，通过主表得到这张加工单明细的信息并插入生产领料单从表
    */

   class Self_Sel_Process
       implements Obactioner {
     public void execute(String action, Obationable o, RunData data, Object arg) throws
         Exception {
       HttpServletRequest req = data.getRequest();
       //保存输入的明细信息
       putDetailInfo(data.getRequest());
       RowMap rowinfo = getMasterRowinfo();

       String importProcess = rowinfo.get("importProcess");
       if (importProcess.equals(""))
         return;
       /**
        String sql = combineSQL(PROCESSMATERAIL_SQL,"?", new String[]{importProcess});
              EngineDataSet dsSumNumber = null;//得到加工单物料中主配料的数量；
              if(dsSumNumber==null)
              {
         dsSumNumber = new EngineDataSet();
         setDataSetProperty(dsSumNumber,null);
              }
              dsSumNumber.setQueryString(sql);
              if(!dsSumNumber.isOpen())
         dsSumNumber.openDataSet();
              else
         dsSumNumber.refresh();
              dsSumNumber.first();
              for(int t=0; t<dsSumNumber.getRowCount(); t++){
         dsProcessMaterail.insertRow(false);
         dsProcessMaterail.setValue("cpid", dsSumNumber.getValue("cpid"));
         dsProcessMaterail.setValue("dmsxid", dsSumNumber.getValue("dmsxid"));
         dsProcessMaterail.setValue("sl", dsSumNumber.getValue("sl"));
         dsProcessMaterail.setValue("scsl", dsSumNumber.getValue("scsl"));
         dsProcessMaterail.post();
         RowMap temprow = new RowMap(dsProcessMaterail);
         d_ProcessMaterail.add(temprow);
         dsSumNumber.next();
              }
        */

       String SQL = SELF_SINGLE_PROCESS_SQL + importProcess;
       EngineDataSet processData = null;
       if (processData == null) {
         processData = new EngineDataSet();
         setDataSetProperty(processData, null);
       }
       processData.setQueryString(SQL);
       if (!processData.isOpen())
         processData.openDataSet();
       else
         processData.refresh();
       rowinfo.put("deptid", processData.getValue("deptid"));
       //实例化查找数据集的类
       EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
       if (!isMasterAdd)
         dsMasterTable.goToInternalRow(masterRow);
       String receiveID = dsMasterTable.getValue("receiveID");
       processData.first();
       for (int i = 0; i < processData.getRowCount(); i++) {
         processData.goToRow(i);
         locateGoodsRow.setValue(0, processData.getValue("jgdmxid"));
         if (!dsDetailTable.locate(locateGoodsRow, Locate.FIRST)) {
           dsDetailTable.insertRow(false);
           dsDetailTable.setValue("receiveDetailID", "-1");
           dsDetailTable.setValue("jgdmxid", processData.getValue("wjid"));
           String cpid = processData.getValue("cpid");
           String wrksl = processData.getValue("wrksl");
           dsDetailTable.setValue("cpid", processData.getValue("cpid"));
           dsDetailTable.setValue("drawNum", wrksl);
           dsDetailTable.setValue("produceNum", processData.getValue("wrkscsl"));
           //03.10 09:53 新增 新增当 引入加工单 事件在这里做处理是也要把dmsxid(规格属性取得) yjg
           dsDetailTable.setValue("dmsxid", processData.getValue("dmsxid"));
           dsDetailTable.setValue("receiveID", isMasterAdd ? "" : receiveID);
           dsDetailTable.post();
           //创建一个与用户相对应的行
           RowMap detailrow = new RowMap(dsDetailTable);
           detailrow.put("InternalRow",
                         String.valueOf(dsDetailTable.getInternalRow()));
           d_RowInfos.add(detailrow);
         }
         processData.next();
       }
       data.setMessage(showJavaScript("big_change()"));
     }
   }

  class transferScan
      implements Obactioner {

    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();
      EngineDataSet detail = getDetailTable();
      if (!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String receiveID = dsMasterTable.getValue("receiveID");
      RowMap detailrowinfo = null;
      for (int i = 0; i < d_RowInfos.size(); i++) {
        detailrowinfo = (RowMap) d_RowInfos.get(i);
        swapdetailRow = Long.parseLong(detailrowinfo.get("InternalRow"));
        detail.goToInternalRow(swapdetailRow);
        detail.setValue("cpid", detailrowinfo.get("cpid"));
        detail.setValue("drawNum", detailrowinfo.get("drawNum"));
        detail.setValue("dmsxid", detailrowinfo.get("dmsxid"));
        detail.setValue("memo", detailrowinfo.get("memo"));
        detail.setValue("drawBigNum", detailrowinfo.get("drawBigNum"));
        detail.setValue("produceNum", detailrowinfo.get("produceNum"));
        detail.setValue("batchNo", detailrowinfo.get("batchNo"));
        detail.setValue("kwid", detailrowinfo.get("kwid"));
        detail.post();
      }

      RowMap rowinfo = getMasterRowinfo();
      String storeid = rowinfo.get("storeid");
      String scanValue = req.getParameter("scanValue");
      String s[][] = StringUtils.getArrays(scanValue);
      String cpbms[] = s[0];
      String phStr[] = s[1];
      ArrayList tempdmsxids = new ArrayList();
      ArrayList dmsxids = new ArrayList();
      ArrayList cpids = new ArrayList(cpbms.length);
      ArrayList phs = new ArrayList(cpbms.length);
      ArrayList cpbmsArry = new ArrayList();
      StringBuffer rejectScanDataMessage = new StringBuffer();
      LookUp prodCodeBean = LookupBeanFacade.getInstance(req, "kc_dm.code");
      LookUp prodBean = LookupBeanFacade.getInstance(req, "kc_dm");
      LookUp stockKindBean = LookupBeanFacade.getInstance(req, "kc_chlb");
      prodBean.regData(new String[] {
                       "1"
      });
      stockKindBean.regData(new String[] {
                            "1"
      });
      prodCodeBean.regData(cpbms);
      int total = cpbms.length / 10 + (cpbms.length % 10 <= 0 ? 0 : 1);
      int i = 0;
      for (int out = 0; out < total; out++) {
        ArrayList tmp_dmsxids = new ArrayList();
        ArrayList tmp_cpids = new ArrayList();
        ArrayList tmp_phs = new ArrayList();
        ArrayList tmp_cpbmsArry = new ArrayList();
        for (int j = 0; j < 10 && i < cpbms.length; j++) {
          if (cpbms[i] == null) {
            i++;
            continue;
          }
          String cpbm = cpbms[i].length() <= 6 ? cpbms[i] :
              cpbms[i].substring(0, 7);
          if (cpbm.equals("")) {
            i++;
            continue;
          }
          RowMap prodCodeRow = prodCodeBean.getLookupRow(cpbm);
          String cpid = prodCodeRow.get("cpid");
          String p_storeid = prodCodeRow.get("storeid");
          if (cpid.equals("") ||
              !p_storeid.equals(storeid) && !p_storeid.equals("")) {
            i++;
            continue;
          }
          String dmsxid = "";
          boolean isNew = cpbms[i].indexOf("-") > -1;
          if (isNew) {
            String dmsxidArray[] = BaseAction.parseString(cpbms[i], "-");
            if (dmsxidArray.length < 2) {
              i++;
              continue;
            }
            dmsxid = dmsxidArray[1];
          }
          else {
            dmsxid = getDmsxId(cpid, cpbms[i]);
          }
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

      EngineDataSet dsTemp = new EngineDataSet();
      setDataSetProperty(dsTemp, null);
      for (int out = 0; out < total; out++) {
        ArrayList tmp_cpids = (ArrayList) cpids.get(out);
        if (tmp_cpids.size() == 0)
          continue;
        ArrayList tmp_dmsxids = (ArrayList) dmsxids.get(out);
        ArrayList tmp_phs = (ArrayList) phs.get(out);
        String temp = StringUtils.getArrayValue( (String[]) tmp_phs.toArray(new
            String[tmp_phs.size()]), "','");
        String sql = temp.length() <= 0 ? "" :
            String.valueOf(String.valueOf( (new StringBuffer(" batNo IN ('")).
                                          append(temp).append("')")));
        temp = StringUtils.getArrayValue( (String[]) tmp_cpids.toArray(new
            String[tmp_cpids.size()]), ",");
        sql = String.valueOf(sql) +
            String.valueOf(temp.length() <= 0 ? "" :
                           ( (
                            Object) (String.valueOf(
                                     String.valueOf( (new
            StringBuffer(String.
                         valueOf(String.valueOf(sql.length() <= 0 ? "" : " AND")))).
                                             append(" cpid IN (").append(temp).
                                             append(")"))))));
        temp = StringUtils.getArrayValue( (String[]) tmp_dmsxids.toArray(new
            String[tmp_dmsxids.size()]), ",");
        sql = String.valueOf(sql) +
            String.valueOf(temp.length() <= 0 ? "" :
                           ( (
                            Object) (String.valueOf(
                                     String.valueOf( (new
            StringBuffer(String.
                         valueOf(String.valueOf(sql.length() <= 0 ? "" : " AND")))).
                                             append(" dmsxid IN (").append(temp).
                                             append(")"))))));
        sql = BaseAction.combineSQL("SELECT * FROM zl_certifiedCard WHERE @ ",
                                    "@", new String[] {
                                    sql
        });
        if (dsQuality.isOpen()) {
          dsTemp.setQueryString(sql);
          if (dsTemp.isOpen())
            dsTemp.refresh();
          else
            dsTemp.openDataSet();
          DataSetData.extractDataSet(dsTemp).loadDataSet(dsQuality);
        }
        else {
          dsQuality.setQueryString(sql);
          dsQuality.openDataSet();
        }
      }

      dsTemp.closeDataSet();
      if (!dsQuality.isOpen()) {
        data.setMessage(BaseAction.showJavaScript("alert('盘点机中没有该仓库的数据')"));
        return;
      }
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[] {
                                               "cpid", "dmsxid"
      });
      EngineRow locateGoodsPhRow = new EngineRow(dsQuality, new String[] {
                                                 "cpid", "dmsxid", "batNo"
      });
      for (i = 0; i < cpids.size(); i++) {
        ArrayList tmpcpids = (ArrayList) cpids.get(i);
        ArrayList tmpdmsxids = (ArrayList) dmsxids.get(i);
        ArrayList tmpphs = (ArrayList) phs.get(i);
        ArrayList tmpCpbmsArry = (ArrayList) cpbmsArry.get(i);
        for (int m = 0; m < tmpcpids.size(); m++) {
          String cpid = (String) tmpcpids.get(m);
          String ph = (String) tmpphs.get(m);
          String dmsxid = (String) tmpdmsxids.get(m);
          String sxzSql = "select nvl(sxz, '') from kc_dmsx where dmsxid = ".
              concat(String.valueOf(String.valueOf(dmsxid)));
          String hsblSql = "select nvl(hsbl, '') from kc_dm where cpid = ".
              concat(String.valueOf(String.valueOf(cpid)));
          String sxz = dataSetProvider.getSequence(sxzSql);
          BigDecimal scale = new BigDecimal(0.0D);
          String hsbl = dataSetProvider.getSequence(hsblSql);
          try {
            scale = BaseAction.calculateExpression(hsbl, sxz);
          }
          catch (Exception e) {
            scale = new BigDecimal(1.0D);
          }
          scale = scale != null ? scale : new BigDecimal(1.0D);
          String hgzsl = "";
          String hgzscsl = "";
          String pagenum = "";
          locateGoodsPhRow.setValue(0, cpid);
          locateGoodsPhRow.setValue(1, dmsxid);
          locateGoodsPhRow.setValue(2, ph);
          String count = dataSetProvider.getSequence(String.valueOf(String.valueOf( (new
              StringBuffer(
              "SELECT COUNT(*) FROM vw_kc_storebill t WHERE t.djxz IN(1,3,5,7,9) AND t.ph='")).
              append(ph).append("' AND t.cpid='").append(cpid).append("'"))));
          if (!count.equals("0")) {
            hgzsl = "";
            hgzscsl = "";
            pagenum = "";
          }
          else
          if (dsQuality.locate(locateGoodsPhRow, 32)) {
            hgzsl = dsQuality.getValue("saleNum");
            hgzscsl = dsQuality.getValue("produceNum");
            pagenum = dsQuality.getValue("pagenum");
          }
          if (!hgzsl.equals("") && (pagenum.equals("") || hgzscsl.equals(""))) {
            RowMap prodRow = prodBean.getLookupRow(cpid);
            String chlbid = prodRow.get("chlbid");
            BigDecimal tmpsl = new BigDecimal(hgzsl);
            RowMap ckdgsRow = stockKindBean.getLookupRow(chlbid);
            String ckdgs = ckdgsRow.get("ckdgs");
            if (!ckdgs.equals("3")) {
              BigDecimal tmppage = tmpsl.divide(scale, 6, 4);
              pagenum = tmppage.toString();
            }
            String scdwgs = prodRow.get("scdwgs");
            String width = BasePublicClass.parseEspecialString(sxz,
                SYS_PRODUCT_SPEC_PROP, "()");
            double d_wid = width.equals("0") ? 1.0D : Double.parseDouble(width);
            BigDecimal b_wid = new BigDecimal(0.0D);
            BigDecimal b_scdwgs = new BigDecimal(scdwgs);
            b_wid = new BigDecimal(d_wid);
            BigDecimal b_scsl = tmpsl.multiply(b_scdwgs).divide(b_wid, 6, 4);
            hgzscsl = b_scsl.toString();
          }
          locateGoodsRow.setValue(0, cpid);
          locateGoodsRow.setValue(1, dmsxid);
          if (dsDetailTable.locate(locateGoodsRow, 32)) {
            if (dsDetailTable.getValue("batchNo").equals("")) {
              dsDetailTable.setValue("batchNo", ph);
              dsDetailTable.setValue("drawNum", hgzsl);
              dsDetailTable.setValue("produceNum", hgzscsl);
              dsDetailTable.setValue("drawBigNum", pagenum);
              dsDetailTable.post();
              continue;
            }
            if (!dsDetailTable.getValue("batchNo").equals(ph) &&
                !dsDetailTable.getValue("batchNo").equals("")) {
              dsDetailTable.insertRow(false);
              dsDetailTable.setValue("receiveDetailID", "-1");
              dsDetailTable.setValue("receiveID",
                                     isMasterAdd ? "-1" : receiveID);
              dsDetailTable.setValue("cpid", cpid);
              dsDetailTable.setValue("batchNo", ph);
              dsDetailTable.setValue("dmsxid", dmsxid);
              dsDetailTable.setValue("drawNum", hgzsl);
              dsDetailTable.setValue("produceNum", hgzscsl);
              dsDetailTable.setValue("drawBigNum", pagenum);
              dsDetailTable.post();
              RowMap detailrow = null;
              detailrow = new RowMap(dsDetailTable);
              detailrow.put("InternalRow",
                            String.valueOf(dsDetailTable.getInternalRow()));
              d_RowInfos.add(detailrow);
            }
          }
          else {
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("receiveDetailID", "-1");
            dsDetailTable.setValue("cpid", cpid);
            dsDetailTable.setValue("dmsxid", dmsxid);
            dsDetailTable.setValue("batchNo", ph);
            dsDetailTable.setValue("receiveID", isMasterAdd ? "-1" : receiveID);
            dsDetailTable.setValue("drawNum", hgzsl);
            dsDetailTable.setValue("produceNum", hgzscsl);
            dsDetailTable.setValue("drawBigNum", pagenum);
            dsDetailTable.post();
          }
        }

      }

      dsQuality.closeDataSet();
      initRowInfo(false, false, true);
      data.setMessage(BaseAction.showJavaScript("big_change()"));
    }

    public String getDmsxId(String cpid, String cpbm) throws Exception {
      String tempGgSx = cpbm.length() <= 6 ? cpbm : cpbm.substring(7);
      if (tempGgSx.equals("") || tempGgSx.indexOf("-") > -1)
        return "";
      String ggsx = String.valueOf(Integer.parseInt(tempGgSx));
      String sql = "select * from kc_dmsx where sxz like '%宽度(?)%' and cpid = '?' and rownum<2 and isdelete = 0";
      sql = BaseAction.combineSQL(sql, "?", new String[] {
                                  ggsx, cpid
      });
      dmsxidExistData.setQueryString(sql);
      if (!dmsxidExistData.isOpen())
        dmsxidExistData.openDataSet();
      else
        dmsxidExistData.refresh();
      if (dmsxidExistData.getRowCount() == 0) {
        dmsxidExistData.insertRow(false);
        String tempStr = dataSetProvider.getSequence("s_kc_dmsx");
        dmsxidExistData.setValue("dmsxid", tempStr);
        dmsxidExistData.setValue("cpid", cpid);
        dmsxidExistData.setValue("sxz",String.valueOf(String.valueOf( (new  StringBuffer("宽度(")).append(ggsx).append(")"))));
        dmsxidExistData.post();
        dmsxidExistData.saveChanges();
      }
      return dmsxidExistData.getValue("dmsxid");
    }

    transferScan() {
    }
  }

  /**
   *调用盘点单触发的事件

    class transferScan implements Obactioner
    {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {




    }





      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();
      EngineDataSet detail = getDetailTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String receiveID = dsMasterTable.getValue("receiveID");
      RowMap detailrowinfo = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrowinfo = (RowMap)d_RowInfos.get(i);
        swapdetailRow = Long.parseLong(detailrowinfo.get("InternalRow"));
        detail.goToInternalRow(swapdetailRow);
        detail.setValue("cpid", detailrowinfo.get("cpid"));
        detail.setValue("drawNum", detailrowinfo.get("drawNum"));
        detail.setValue("dmsxid", detailrowinfo.get("dmsxid"));
        detail.setValue("memo", detailrowinfo.get("memo"));
        detail.setValue("drawBigNum", detailrowinfo.get("drawBigNum"));
        detail.setValue("produceNum", detailrowinfo.get("produceNum"));
        detail.setValue("batchNo", detailrowinfo.get("batchNo"));
        detail.setValue("kwid", detailrowinfo.get("kwid"));
        detail.post();
      }
      //boolean isNew = String.valueOf(NEW_TRANSFERSCAN).equals(action);
      RowMap rowinfo = getMasterRowinfo();
      String storeid = rowinfo.get("storeid");
      String scanValue= req.getParameter("scanValue");//得到包含产品编码和批号的字符串
      String[][] s=engine.util.StringUtils.getArrays(scanValue);
      String[] cpbms = s[0];//产品编码数组
      String[] phStr   = s[1];//批号数组
      ArrayList tempdmsxids = new ArrayList();//用于查询库存里是否存在
      ArrayList dmsxids = new ArrayList();//用于定位规格属性
      ArrayList cpids = new ArrayList(cpbms.length);
      ArrayList phs = new ArrayList(cpbms.length);
      ArrayList cpbmsArry = new ArrayList();

      StringBuffer rejectScanDataMessage = new StringBuffer();//用来保存盘点机读进来的但是却没有在此单据定位到因此不能插入(即舍弃掉)那些数据信息.
      LookUp prodCodeBean = LookupBeanFacade.getInstance(req,SysConstant.BEAN_PRODUCT_CODE);
      prodCodeBean.regData(cpbms);

      //
      int total = cpbms.length/10 + (cpbms.length%10 > 0 ? 1 : 0);
      int i = 0;//用来记录cpids数组size实际的大小.
      for(int out=0; out<total; out++)
      {
        //零时数据存放10条纪录的数据
        ArrayList tmp_dmsxids = new ArrayList();
        ArrayList tmp_cpids = new ArrayList();
        ArrayList tmp_phs = new ArrayList();
        ArrayList tmp_cpbmsArry = new ArrayList();
        for(int j=0; j<10; j++)
        {
          if(i>= cpbms.length)
              break;
          if(cpbms[i]== null)
          {
            i++;
            continue;
          }
          String cpbm =  cpbms[i].length()>6?cpbms[i].substring(0, 7):cpbms[i];//2004-4-2 23:12 修改 暂时修改取产品编码前七位 yjg
          if(cpbm.equals(""))
          {
            i++;
            continue;
          }
          RowMap prodCodeRow = prodCodeBean.getLookupRow(cpbm);
          String cpid = prodCodeRow.get("cpid");
          String p_storeid = prodCodeRow.get("storeid");
   if(cpid.equals("") || (!p_storeid.equals(storeid) && !p_storeid.equals(""))){
            i++;
            continue;
          }

          String dmsxid = "";
          boolean isNew = cpbms[i].indexOf("-") > -1;
          if(isNew)
          {
   String[] dmsxidArray = parseString(cpbms[i],"-");//新盘点机直接解析读进来的产品编码得到规格属性ID
            if(dmsxidArray.length<2){
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
        //把
        cpids.add(tmp_cpids);
        dmsxids.add(tmp_dmsxids);
        phs.add(tmp_phs);
        cpbmsArry.add(tmp_cpbmsArry);
      }
      //每次10条打开数据集，第1次以后的数据集加入数据集
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
        String sql = temp.length() > 0 ? " batNo IN ('" + temp + "')" : "";
        temp = StringUtils.getArrayValue((String[])tmp_cpids.toArray(new String[tmp_cpids.size()]), ",");
        sql += temp.length() > 0 ? (sql.length()>0 ? " AND" : "") + " cpid IN (" + temp + ")" : "";
        temp = StringUtils.getArrayValue((String[])tmp_dmsxids.toArray(new String[tmp_dmsxids.size()]), ",");
        sql += temp.length() > 0 ? (sql.length()>0 ? " AND" : "") + " dmsxid IN (" + temp + ")" : "";

        sql = combineSQL(QUALITY_CARD_SQL, "@", new String[]{sql});
        if(dsQuality.isOpen())//第二个10条数据集先用零时数据集打开
        {
          dsTemp.setQueryString(sql);
          if(dsTemp.isOpen())
            dsTemp.refresh();
          else
            dsTemp.openDataSet();

          DataSetData.extractDataSet(dsTemp).loadDataSet(dsQuality);
        }
        else
        {
          dsQuality.setQueryString(sql);
          dsQuality.openDataSet();
        }
      }
      //关闭数据集
      dsTemp.closeDataSet();
      //如果盘点机中没有一条是这个仓库的数据就返回
      if(!dsQuality.isOpen()){
        data.setMessage(showJavaScript("alert('盘点机中没有该仓库的数据')"));
        return;
      }


      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[]{"cpid", "dmsxid"});
      EngineRow locateGoodsPhRow = new EngineRow(dsQuality, new String[]{"cpid", "dmsxid", "batNo"});


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

          String hgzsl = "";//库存数量.主要是为当盘点扫描进来的时候.去物资明细中取库存数量
          String hgzscsl = "";//库存数量.主要是为当盘点扫描进来的时候.去物资明细中取库存数量
          String pagenum = "";//平张纸张

          //用cpid, dmsxid, ph去物资明细表中定位记录,目地是要取到:kcsl
          locateGoodsPhRow.setValue(0, cpid);
          locateGoodsPhRow.setValue(1, dmsxid);
          locateGoodsPhRow.setValue(2, ph);
          String count  = dataSetProvider.getSequence("SELECT COUNT(*) FROM vw_kc_storebill t WHERE t.djxz IN(1,3,5,7,9) AND t.ph='"+ph+"' AND t.cpid='"+cpid+"'");
          if(!count.equals("0"))
          {
            hgzsl="";
            hgzscsl="";
            pagenum="";
          }
          else if (dsQuality.locate(locateGoodsPhRow, Locate.FIRST)){
            hgzsl = dsQuality.getValue("saleNum");
            hgzscsl = dsQuality.getValue("produceNum");
            pagenum = dsQuality.getValue("pagenum");//平张纸张数
          }
          locateGoodsRow.setValue(0, cpid);
          locateGoodsRow.setValue(1, dmsxid);
          //locateGoodsRow.setValue(2, ph);
          if(dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
          {
            if(dsDetailTable.getValue("batchNo").equals("")){
              dsDetailTable.setValue("batchNo", ph);
              dsDetailTable.setValue("drawNum", hgzsl);
              dsDetailTable.setValue("produceNum", hgzscsl);
              dsDetailTable.setValue("drawBigNum", pagenum);
              dsDetailTable.post();
            }
            else if(!dsDetailTable.getValue("batchNo").equals(ph) && !dsDetailTable.getValue("batchNo").equals(""))
            {

              dsDetailTable.insertRow(false);
              dsDetailTable.setValue("receiveDetailID","-1");
   dsDetailTable.setValue("receiveID", isMasterAdd ? "-1" : receiveID);
              dsDetailTable.setValue("cpid", cpid);
              //dsDetailTable.setValue("jgdmxid", jgdwlid);
              dsDetailTable.setValue("batchNo", ph);
              dsDetailTable.setValue("dmsxid", dmsxid);
              dsDetailTable.setValue("drawNum", hgzsl);
              dsDetailTable.setValue("produceNum", hgzscsl);
              dsDetailTable.setValue("drawBigNum", pagenum);
              //dsDetailTable.setValue("drawNum", kcsl);
              dsDetailTable.post();
              //创建一个与用户相对应的行
              RowMap detailrow = null;
              detailrow = new RowMap(dsDetailTable);
   detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
              d_RowInfos.add(detailrow);
            }
          }
          else //如果没有定位到那么.记录下来此笔读进来资料的cpid, dmsxid
          {
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("receiveDetailID", "-1");
            dsDetailTable.setValue("cpid", cpid);
            dsDetailTable.setValue("dmsxid", dmsxid);
            dsDetailTable.setValue("batchNo", ph);
   dsDetailTable.setValue("receiveID", isMasterAdd ? "-1" : receiveID);
            dsDetailTable.setValue("drawNum", hgzsl);
            dsDetailTable.setValue("produceNum", hgzscsl);
            dsDetailTable.setValue("drawBigNum", pagenum);
            dsDetailTable.post();
            //if (i==0) rejectScanDataMessage.append("以下盘点机读进来的数据\n因其品名规格和规格属性\n在本单据没有找到与之对应的数据,故未能取用\n");
            //rejectScanDataMessage.append(cpbms[i]+phStr[i]+"\n");
          }
        }
      }
      dsQuality.closeDataSet();
      initRowInfo(false,false, true);
      data.setMessage(showJavaScript("big_change()"));
      //if (rejectScanDataMessage.length()>0)
      //data.setMessage(showMessage(rejectScanDataMessage.toString(), false));
    }
    public String getDmsxId(String cpid, String cpbm) throws Exception{
      String tempGgSx = cpbm.length()>6?cpbm.substring(7):cpbm;//2004-4-2 23:12 修改 暂时修改取产品编码前七位 yjg
      if(tempGgSx.equals("") || tempGgSx.indexOf("-") >-1)
         return "";
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
   **/
  /**
   *自制收货单复制一行操作
   */
  class Detail_Copy
      implements Obactioner {
    //----Implementation of the Obactioner interface
    /**
     * 触发复制操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);
      EngineDataSet ds = getMaterTable();
      EngineDataSet detail = getDetailTable();
      if (!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String receiveID = dsMasterTable.getValue("receiveID");
      RowMap rowinfo = null;
      for (int i = 0; i < d_RowInfos.size(); i++) {
        rowinfo = (RowMap) d_RowInfos.get(i);
        swapdetailRow = Long.parseLong(rowinfo.get("InternalRow"));
        detail.goToInternalRow(swapdetailRow);
        detail.setValue("cpid", rowinfo.get("cpid"));
        detail.setValue("drawNum", rowinfo.get("drawNum"));
        detail.setValue("dmsxid", rowinfo.get("dmsxid"));
        detail.setValue("memo", rowinfo.get("memo"));
        detail.setValue("drawBigNum", rowinfo.get("drawBigNum"));
        detail.setValue("produceNum", rowinfo.get("produceNum"));
        detail.setValue("batchNo", rowinfo.get("batchNo"));
        detail.setValue("kwid", rowinfo.get("kwid"));
        detail.setValue("jjdj", rowinfo.get("jjdj")); //计件单价
        detail.setValue("jjgz", rowinfo.get("jjgz")); //计件工资
        detail.setValue("gx", rowinfo.get("gx")); //工序
        detail.setValue("gylxid", rowinfo.get("gylxid")); //工艺路线ID
        detail.setValue("jjff", rowinfo.get("jjff")); //计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
        detail.setValue("jjdj2", formatNumber(rowinfo.get("jjdj2"), priceFormat)); //计件单价
        detail.setValue("jjgz2", formatNumber(rowinfo.get("jjgz2"), priceFormat)); //计件工资
        detail.setValue("gx2", rowinfo.get("gx2")); //工序
        detail.setValue("jjff2", rowinfo.get("jjff2")); //计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
        detail.setValue("jjdj3", formatNumber(rowinfo.get("jjdj3"), priceFormat)); //计件单价
        detail.setValue("jjgz3", formatNumber(rowinfo.get("jjgz3"), priceFormat)); //计件工资
        detail.setValue("gx3", rowinfo.get("gx3")); //工序
        detail.setValue("jjff3", rowinfo.get("jjff3")); //计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
        detail.post();
      }
      int num = Integer.parseInt(data.getParameter("rownum"));
      RowMap temprow = (RowMap) d_RowInfos.get(num);
      detail.goToInternalRow(Long.parseLong(temprow.get("InternalRow")));
      String cpid = detail.getValue("cpid");
      String drawNum = detail.getValue("drawNum");
      String dmsxid = detail.getValue("dmsxid");
      String memo = detail.getValue("memo");
      String kwid = detail.getValue("kwid");
      String drawBigNum = detail.getValue("drawBigNum");
      String produceNum = detail.getValue("produceNum");
      String batchNo = detail.getValue("batchNo");
      String jgdmxid = detail.getValue("jgdmxid");
      String gylxid = detail.getValue("gylxid");
      String gx = detail.getValue("gx");
      String jjdj = detail.getValue("jjdj");
      String jjff = detail.getValue("jjff");
      String gx2 = detail.getValue("gx2");
      String jjdj2 = detail.getValue("jjdj2");
      String jjff2 = detail.getValue("jjff2");
      String gx3 = detail.getValue("gx3");
      String jjdj3 = detail.getValue("jjdj3");
      String jjff3 = detail.getValue("jjff3");
      RowMap masterrow = getMasterRowinfo();
      String tCopyNumber = request.getParameter("tCopyNumber");
      int copyNum = (tCopyNumber == null || tCopyNumber.equals("0")) ? 1 :
          Integer.parseInt(tCopyNumber);
      for (int j = 0; j < copyNum; j++) {
        detail.insertRow(false);
        detail.setValue("receiveDetailID", "-1");
        detail.setValue("receiveID", isMasterAdd ? "-1" : receiveID);
        detail.setValue("jgdmxid", jgdmxid);
        detail.setValue("cpid", cpid);
        detail.setValue("dmsxid", dmsxid);
        detail.setValue("memo", memo);
        detail.setValue("kwid", kwid);
        detail.setValue("gylxid", gylxid);
        detail.setValue("gx", gx);
        detail.setValue("jjdj", jjdj);
        detail.setValue("jjff", jjff);
        detail.setValue("gx2", gx2);
        detail.setValue("jjdj2", jjdj2);
        detail.setValue("jjff2", jjff2);
        detail.setValue("gx3", gx3);
        detail.setValue("jjdj3", jjdj3);
        detail.setValue("jjff3", jjff3);
        detail.post();
        /**
                  if(SC_INSTORE_SORT_FIELD==null){
          RowMap detailrow = new RowMap(detail);
         detailrow.put("InternalRow", String.valueOf(detail.getInternalRow()));
          d_RowInfos.add(detailrow);
                  }
         */
      }
      //if(SC_INSTORE_SORT_FIELD!=null)
      initRowInfo(false, false, true);
    }
  }

  /**
   * 删除产品编码为空白行操作
   */
  class Delete_Blank
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      putDetailInfo(data.getRequest());
      String cpid = null;
      EngineDataSet detail = getDetailTable();
      for (int i = 0; i < d_RowInfos.size(); i++) {
        RowMap detailrow = (RowMap) d_RowInfos.get(i);
        long internalRow = Long.parseLong(detailrow.get("InternalRow"));
        detail.goToInternalRow(internalRow);
        String drawNum = detailrow.get("drawNum");
        if (drawNum.equals("")) {
          d_RowInfos.remove(i);
          detail.deleteRow();
          i--;
        }
      }
    }
  }

  /**
   * 删除选中的复选框操作
   */
  class Batch_Delete
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      for (int i = 0; i < d_RowInfos.size(); i++) {
        RowMap detailrow = (RowMap) d_RowInfos.get(i);
        long internalRow = Long.parseLong(detailrow.get("InternalRow"));
        detail.goToInternalRow(internalRow);
        String isdel = detailrow.get("isdel");
        if (isdel.equals("1")) {
          d_RowInfos.remove(i);
          detail.deleteRow();
          i--;
        }
        //detailrow.put("isdel","");
      }
      RowMap temprow = null;
      for (int j = 0; j < d_RowInfos.size(); j++) {
        temprow = (RowMap) d_RowInfos.get(j);
        temprow.put("isdel", "0");
      }
    }
  }

  /**
   *  物料增加操作

     class Materail_Add implements Obactioner
     {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();
      EngineDataSet dsMaterail = getMaterailTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String receiveID = dsMasterTable.getValue("receiveID");
      dsMaterail.insertRow(false);
      dsMaterail.setValue("receiveID", isMasterAdd ? "" : receiveID);
      dsMaterail.post();
      d_ProcessMaterail.add(new RowMap());
    }
     }
     /**
    *  物料删除操作

      class Materail_Delete implements Obactioner
      {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       putDetailInfo(data.getRequest());
       EngineDataSet ds = getDetailTable();
       EngineDataSet dsMaterail = getMaterailTable();
       int rownum = Integer.parseInt(data.getParameter("rownum"));
       //删除临时数组的一列数据
       d_ProcessMaterail.remove(rownum);
       dsMaterail.goToRow(rownum);
       dsMaterail.deleteRow();
     }
      }
    */
   /**
    * 首先得到当前自制收货单的ID
    * 复制前两天的自制收货单操作
    */
   class Copy_Self
       implements Obactioner {
     public void execute(String action, Obationable o, RunData data, Object arg) throws
         Exception {
       putDetailInfo(data.getRequest());
       String id = data.getParameter("masterid");

       RowMap selfRow = getselfBean(data.getRequest()).getLookupRow(id);
       m_RowInfo.put("storeid", selfRow.get("storeid"));
       m_RowInfo.put("sfdjlbid", selfRow.get("sfdjlbid"));
       m_RowInfo.put("deptid", selfRow.get("deptid"));
       m_RowInfo.put("ytid", selfRow.get("ytid"));
       EngineDataSet tempDetailData = null;
       String SQL = null;
       //if(SC_INSTORE_SORT_FIELD==null)
       SQL = combineSQL(DETAIL_SQL, "?", new String[] {id});
       //else
       //SQL = combineSQL(NEWDETAIL_SQL,"?", new String[]{id});
       if (tempDetailData == null) {
         tempDetailData = new EngineDataSet();
         setDataSetProperty(tempDetailData, null);
       }
       tempDetailData.setQueryString(SQL);
       if (!tempDetailData.isOpen())
         tempDetailData.openDataSet();
       else
         tempDetailData.refresh();

       tempDetailData.first();
       for (int i = 0; i < tempDetailData.getRowCount(); i++) {
         dsDetailTable.insertRow(false);
         dsDetailTable.setValue("receiveDetailID", "-1");
         dsDetailTable.setValue("receiveID", "-1");
         //dsDetailTable.setValue("jgdmxid", tempDetailData.getValue("jgdmxid"));
         dsDetailTable.setValue("cpid", tempDetailData.getValue("cpid"));
         dsDetailTable.setValue("dmsxid", tempDetailData.getValue("dmsxid"));
         dsDetailTable.setValue("memo", tempDetailData.getValue("memo"));
         dsDetailTable.setValue("kwid", tempDetailData.getValue("kwid"));
         dsDetailTable.setValue("drawNum", tempDetailData.getValue("drawNum"));
         dsDetailTable.setValue("produceNum",
                                tempDetailData.getValue("produceNum"));
         dsDetailTable.setValue("drawBigNum",
                                tempDetailData.getValue("drawBigNum"));
         dsDetailTable.post();
         /**
                  if(SC_INSTORE_SORT_FIELD==null){
           RowMap detailrow = new RowMap(dsDetailTable);
          detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
           d_RowInfos.add(detailrow);
                  }
          */
       }
       //if(SC_INSTORE_SORT_FIELD!=null)
       initRowInfo(false, false, true);
     }
   }

  /**
   *  从表删除操作
   */
  class Detail_Delete
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getDetailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      RowMap detailrow = (RowMap) d_RowInfos.get(rownum);
      long l_row = Long.parseLong(detailrow.get("InternalRow"));
      d_RowInfos.remove(rownum);
      ds.goToInternalRow(l_row);
      ds.deleteRow();
      data.setMessage(showJavaScript("big_change()"));
    }
  }

  /**
   * 提交仓库
   */
  class Onchange
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);
    }
  }

  /**
   * 2004-5-2 19:00 明细资料数据集页面翻页功能.
   */
  class Turn_Page
      implements Obactioner {
    /**
     * 按页翻动明细数据集的数据
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      //保存输入的明细信息
      isRepeat = "0";
      putDetailInfo(data.getRequest());
      String temp = checkNumRigtt(min, max);
      if (temp != null) {
        isRepeat = "1"; //重定向，如果本页检测数据不正确的话isrepeat为1。将不翻页
        data.setMessage(temp);
        return;
      }
    }
  }

  //检验数据正确性方法
  private String checkNumRigtt(int tempmin, int tempmax) throws Exception {
    String temp = null;
    RowMap detailrow = null;
    ArrayList list = new ArrayList();
    for (int i = tempmin; i <= tempmax; i++) {
      int row = i + 1;
      detailrow = (RowMap) d_RowInfos.get(i);
      String cpid = detailrow.get("cpid");
      if (cpid.equals(""))
        return showJavaScript("alert('第" + row + "行产品不能为空');");
      String batchNo = detailrow.get("batchNo");
      String isbatchno = detailrow.get("isbatchno");
      //如果收货单明细中产品相同批号也相同，并且该产品设置为批号跟踪则不能保存。设置是否批号跟踪在物资信息中设置
      if (!batchNo.equals("") && isbatchno.equals("1")) {
        if (list.contains(batchNo))
          return showJavaScript("alert('第" + row + "行批号重复');");
        else
          list.add(batchNo);
      }
      String sl = detailrow.get("drawNum");
      if ( (temp = checkNumber(sl, "第" + row + "行数量")) != null)
        return temp;
      if (sl.length() > 0 && sl.equals("0"))
        return showJavaScript("alert('第" + row + "行数量不能为零！')");
    }
    RowMap temprow = null;
    if (isMasterAdd) {
      for (int j = tempmin; j <= tempmax; j++) {
        temprow = (RowMap) d_RowInfos.get(j);
        int row = j + 1;
        String cpid = detailrow.get("cpid");
        String batchNo = detailrow.get("batchNo");
        String isbatchno = detailrow.get("isbatchno");
        //如果收货单明细中产品相同批号也相同，并且该产品设置为批号跟踪则不能保存。设置是否批号跟踪在物资信息中设置
        if (!batchNo.equals("") && isbatchno.equals("1")) {
          String count = "0";
          if (isMasterAdd)
            count = dataSetProvider.getSequence(
                "SELECT COUNT(*) FROM vw_kc_storebill t WHERE t.djxz IN(1,3,5,7,9) AND t.ph='" +
                batchNo + "' AND t.cpid='" + cpid + "'");
          else {
            String receiveid = dsMasterTable.getValue("receiveID");
            count = dataSetProvider.getSequence(
                "SELECT COUNT(*) FROM vw_kc_storebill t WHERE t.djxz IN(1,3,5,7,9) AND t.ph='" +
                batchNo + "' AND t.cpid='" + cpid + "' AND t.sfdjid<>'" +
                receiveid + "'");
          }
          //String count  = dataSetProvider.getSequence("SELECT COUNT(*) FROM vw_kc_storebill t WHERE t.djxz IN(1,3,5,7,9) AND t.ph='"+batchNo+"'");
          if (!count.equals("0"))
            return showJavaScript("alert('第" + row + "行批号在库存中已存在');");
        }
      }
    }
    return null;
  }

  //02.17 15:12 新增 新增一个当页面上单击事件发生时想要查看明细资料 这个单击事件的Show_Detail类 . yjg
  /**
   * 显示从表的列表信息
   */
  class Show_Detail
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterTable.getInternalRow();
      //打开从表
      openDetailTable(false);
    }
  }

  /**
   * 新增 实现翻页为方便打印的类.
   */
  class Move_Cursor_ForPrint
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      boolean isNext = String.valueOf(NEXT).equals(action);
      dsMasterTable.goToInternalRow(masterRow);
      if (isNext)
        dsMasterTable.next();
      else
        dsMasterTable.prior();
      masterRow = dsMasterTable.getInternalRow();

      //dsMasterTable.goToInternalRow(masterRow+1);
      //masterRow = dsMasterTable.getInternalRow();
      //int i = dsMasterTable.getRow();
      synchronized (dsDetailTable) {
        openDetailTable(false);
      }
      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
    }
  }

  public B_SingleSelf getselfBean(HttpServletRequest req) {
    if (singleselfBean == null)
      singleselfBean = B_SingleSelf.getInstance(req);
    return singleselfBean;
  }

  public long getMasterRow() {
    return masterRow;
  }

  /**
   * 得到规格属性的bean
   * @param req WEB的请求
   * @return 返回规格属性的bean
   */
  public LookUp getPropertyBean(HttpServletRequest req) {
    if (propertyBean == null)
      propertyBean = LookupBeanFacade.getInstance(req,
                                                  SysConstant.BEAN_SPEC_PROPERTY);
    return propertyBean;
  }
}

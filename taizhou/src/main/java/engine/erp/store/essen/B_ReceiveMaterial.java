package engine.erp.store.essen;

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
import engine.erp.store.B_ImportReceive;
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
/**
 * <p>Title: 生产领料单列表</p>
 * <p>Description: 生产领料单列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

/**
 * 2004-6-16 11:11 新增 当在明细表中定位不到的时候也去取库存里的数量. yjg
 * 03.16 15:04 修改 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
 *                 以实现:mantis上库存管理中0000158bug描述的:根据下达的部门，进行提交审批；
 * 03.16 15:25 修改 在Master_Search中去掉判断旧的SQL和新产生的SQL是否相同的if语句. yjg
 * 03.10 09:53 新增 新增当 引入加工单 事件在这里做处理是也要把dmsxid(规格属性取得) yjg
 */
/**
 * <p>Title:  生产领料单表结构改过，不在kc_sfdj,kc_sfdjmx中了.现有表为sc_drawmaterail,sc_drawmaterialdetail</p>
 */

public final class B_ReceiveMaterial extends BaseAction implements Operate
{
  public  static final String RECEIVE_SEL_PROCESS = "10881";
  public  static final String RECEIVE_CANCEL_APPROVE = "19881";
  public  static final String ONCHANGE = "10031";//选择仓库提交
  //public  static final String PROP_ONCHANGE = "10041";//输入产品编码或则品名触发事件
  public  static final String TRANSFERSCAN = "10051";//读盘点机触发事件
  public  static final String NEW_TRANSFERSCAN = "10061";//读新盘点机触发事件
  public  static final String SHOW_DETAIL = "12500";//调用从表明细资料
  public  static final String REPORT = "2000";//02.23 11:26 新增 为配合小李的报表追踪调用事件而加 yjg
  public  static final String NEXT = "9999";//新增 为上一笔,下一笔打印而加的
  public  static final String PRIOR = "9998";//新增 为上一笔,下一笔打印而加的
  public  static final String MATCHING_BATCH = "2001";//配批号触发事件
  public  static final String ADD_BACK_MATERAIL = "2002";//新增退料单
  public  static final String IMPORT_RECEIVE_ADD = "2003";//新增退料单引入生产领料单
  public  static final String SELECT_MATERAIL = "2004";//选择加工单物料
  public  static final String RECODE_ACCOUNT = "2005";//记帐触发事件
  public  static final String DETAIL_COPY = "10041";//从表复制多行触发事件
  public  static final String DELETE_BLANK = "2007";//删除数量为空行触发事件s
  public  static final String TURNPAGE = "9996";// 新增 为明细表格番页而加的事件



  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_drawmaterail WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM sc_drawmaterial WHERE ? AND filialeID='?' AND isout='?' ? ORDER BY drawType DESC, drawdate DESC, drawcode DESC";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_drawmaterialdetail WHERE 1<>1";
  private static final String DETAIL_SQL = "SELECT * FROM sc_drawmaterialdetail WHERE drawid='?'";//
  private static final String SINGLE_PROCESS_SQL = "SELECT * FROM VW_SELECT_PROCESSMATERAIL WHERE jgdid='?' and  fgsid='?' and  (storeid IS NULL OR storeid='?') ";//生产领料单引入加工单明细并根据BOM表从表增加
  //查询过后总数量
  private static final String MASTER_SUM_SQL    = "SELECT SUM(nvl(totalnum,0)) zsl, SUM(nvl(zgf,0)) zgf, SUM(nvl(zgf2,0)) zgf2 FROM sc_drawmaterial WHERE  ? AND filialeid='?' AND isout='?' ?  ";
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM sc_drawmaterial WHERE drawid='?'";

  //查询数据库是否有记账的单据
  private static final String RECODE_DATASQL = " SELECT COUNT(*) FROM sc_drawmaterial a WHERE a.creatorid='?' AND  a.isout ='?' AND a.state=1";
  //把符合记帐功能的数据全部记帐
  private static final String RECODE = "UPDATE sc_drawmaterial a SET a.state=2 WHERE a.creatorid='?' AND  a.isout ='?' AND a.state=1";

  //工人工资人员SQL
  private static final String WAGE_MASTERSTRUCT_SQL = "SELECT * FROM sc_grgz WHERE 1<>1";
  private static final String WAGE_MASTER_SQL    = "SELECT * FROM sc_grgz WHERE drawid='?' ";

  //工人工资人员SQL
  private static final String WAGE_DETAILSTRUCT_SQL = "SELECT * FROM sc_grgzry WHERE 1<>1";
  private static final String WAGE_DETAIL_SQL    = "SELECT * FROM sc_grgzry WHERE grgzID='?' ";
  //工资删除SQL
  private static final String WAGE_DELETEDTAIL_SQL    = "SELECT * FROM sc_grgzry WHERE grgzID IN('?') ";
  //根据工作组得到人员信息
  private static final String GROUP_DETAIL_SQL  =
      "SELECT a.personid, a.ryjs  FROM sc_gzzry a WHERE a.gzzid = '?' ";//
  //2004-4-13 16:54 新增 用于添加从表信息的SQL，根据条件和汇总表得到信息.提供给盘点机使用. yjg
  private static final String STOCK_MATERIAL_LIST
      = "SELECT b.cpid, b.dmsxid, b.ph, SUM(nvl(b.zl,0)) kcsl "
      + "FROM   kc_wzmx b, vw_kc_dm_exist a "
      + "WHERE  b.cpid = a.cpid @ AND b.storeid = @ "
      + "GROUP BY b.storeid, b.cpid, b.dmsxid, b.ph";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dmsxidExistData  = new EngineDataSet();//代码属性是否存在的数据集
  private EngineDataSet dsStockMaterialDetail = new EngineDataSet();//物资明细表的数据集

  //private EngineDataSet dsMasterWage  = new EngineDataSet();//工人工资主表
  //private EngineDataSet dsDetailWage  = new EngineDataSet();//工人工资人员
  //private EngineDataSet dsPersonTable  = new EngineDataSet();//工作组信息.生产领料单计算工资

  private EngineDataSet dssl = new EngineDataSet();//计算查询总数量



  public  HtmlTableProducer newmasterProducer = new HtmlTableProducer(dsMasterTable, "sc_newdrawmaterial");
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_drawmaterial");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_drawmaterialdetail");
  public  HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "sc_drawmaterial", "sc_drawmaterial");//查询得到数据库中配置的字段
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态
  public  boolean isDetailAdd = false; //从表是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  public  boolean isReport =false;//02.23 11:26 新增 为配合小李的报表追踪调用事件而加的这个标志 yjg

  private LookUp buyOrderBean = null; //采购单价的bean的引用, 用于提取采购单价
  private Select_Batch selectBatchBean = null; //选择批号的bean的引用, 用于从表产品选择批号
  private B_ImportReceive importReceiveBean = null;//退料单选择领料单信息
  private B_ImportProcessMaterail importProcessBean = null;//引入加工单物料信息
  private B_DrawSingleProcess drawSingleProcessBean = null;

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  //public  int djxz =1;//单据性质

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  public  String bjfs = ""; //系统的报价方式
  public  String  isout = "0";
  private String zzsl="";//总数量
  private String zzgf="";//总工费
  private String zzgf2="";//总工费2
  private long    swapdetailRow = -1;         //保存从表修改操作的行记录指针
  //被分页后的数据集中某一个页面中从第几笔记录开始到第几笔数据结束.如第二页的资料范围是从第51-101笔
  public int min = 0;
  public int max = 0;
  public String isRepeat = "0";//重定向，如果本业检测数据不正确的话isrepeat为1。将不翻页


  private boolean    isGroup1Empty = true;//工作组一是否能空
  private boolean    isGroup1Empty2 = true;//工作组2是否能空

  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  public String isHandwork = null; //是否允许手工录入自制入库单
  private User user = null;
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public String SC_DRAW_MATERIAL = null;//生产领料配批是否以库存数量为准,1=以库存数量为准,0=没有变化
  public String SC_STORE_UNIT_STYLE = null;//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  public String KC_PRODUCE_UNIT_STYLE = null;//计量单位和生产单位换算方式1=强制换算,0=仅空值时换算
  public String drawType = null;//单据类型1是领料-1是退料
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  public String SC_OUTSTORE_SHOW_ADD_FIELD = null;//是否显示生产领料单的附加字段
  /**
   * 入库单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回入库单列表的实例
   */
  public static B_ReceiveMaterial getInstance(HttpServletRequest request)
  {
    B_ReceiveMaterial receiveMaterialBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "receiveMaterialBean";
      receiveMaterialBean = (B_ReceiveMaterial)session.getAttribute(beanName);
      if(receiveMaterialBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        receiveMaterialBean = new B_ReceiveMaterial();
        receiveMaterialBean.qtyFormat = loginBean.getQtyFormat();

        receiveMaterialBean.fgsid = loginBean.getFirstDeptID();
        receiveMaterialBean.loginId = loginBean.getUserID();
        receiveMaterialBean.loginName = loginBean.getUserName();
        receiveMaterialBean.loginDept = loginBean.getDeptID();
        receiveMaterialBean.bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");
        receiveMaterialBean.isHandwork = loginBean.getSystemParam("KC_HANDIN_STOCK_BILL");//是否可以手工添加系统参数1=允许手工输入,0=不允许手工输入
        receiveMaterialBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        receiveMaterialBean.SC_DRAW_MATERIAL = loginBean.getSystemParam("SC_DRAW_MATERIAL");//生产领料配批是否以库存数量为准,1=以库存数量为准,0=没有变化
        receiveMaterialBean.SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
        receiveMaterialBean.KC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("KC_PRODUCE_UNIT_STYLE");//计量单位和生产单位换算方式1=强制换算,0=仅空值时换算
        receiveMaterialBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        receiveMaterialBean.SC_OUTSTORE_SHOW_ADD_FIELD = loginBean.getSystemParam("SC_OUTSTORE_SHOW_ADD_FIELD");//是否显示生产领料单的附加字段
        receiveMaterialBean.user = loginBean.getUser();
        //设置格式化的字段
        receiveMaterialBean.dsMasterTable.setColumnFormat("totalnum", receiveMaterialBean.qtyFormat);
        receiveMaterialBean.dsMasterTable.setColumnFormat("totalsum", receiveMaterialBean.qtyFormat);
        receiveMaterialBean.dsDetailTable.setColumnFormat("drawnum", receiveMaterialBean.qtyFormat);
        receiveMaterialBean.dsDetailTable.setColumnFormat("producenum", receiveMaterialBean.qtyFormat);
        receiveMaterialBean.dsDetailTable.setColumnFormat("drawprice", receiveMaterialBean.qtyFormat);
        receiveMaterialBean.dsDetailTable.setColumnFormat("drawsum", receiveMaterialBean.qtyFormat);
        receiveMaterialBean.dsDetailTable.setColumnFormat("drawBigNum", receiveMaterialBean.qtyFormat);
        receiveMaterialBean.dsMasterTable.setColumnFormat("netNum", receiveMaterialBean.qtyFormat);
        receiveMaterialBean.dsMasterTable.setColumnFormat("zgf", receiveMaterialBean.priceFormat);
        //receiveMaterialBean.dsMasterWage.setColumnFormat("zdf2", receiveMaterialBean.priceFormat);
        //receiveMaterialBean.dsDetailWage.setColumnFormat("jjgz", receiveMaterialBean.priceFormat);
        //receiveMaterialBean.dsDetailWage.setColumnFormat("jjgz2", receiveMaterialBean.priceFormat);
        session.setAttribute(beanName, receiveMaterialBean);
      }
    }
    return receiveMaterialBean;
  }

  /**
   * 构造函数
   */
  private B_ReceiveMaterial()
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
    //setDataSetProperty(dsMasterWage, WAGE_MASTERSTRUCT_SQL);
    //setDataSetProperty(dsDetailWage, WAGE_DETAILSTRUCT_SQL);
    //setDataSetProperty(dsPersonTable, null);
    setDataSetProperty(dssl,null);
    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"drawcode"}, new String[]{"SELECT pck_base.billNextCode('sc_drawmaterial','drawcode') from dual"}));

    dsMasterTable.setSort(new SortDescriptor("", new String[]{"drawType","drawdate","drawcode"}, new boolean[]{true,true,true}, null, 0));

    dsDetailTable.setSort(new SortDescriptor("", new String[]{"jgdwlID","cpid"}, new boolean[]{false,false}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"drawdetailid"}, new String[]{"s_sc_drawmaterialdetail"}));
    //dsMasterWage.setSequence(new SequenceDescriptor(new String[]{"grgzid"}, new String[]{"S_SC_GRGZ"}));
    //dsDetailWage.setSequence(new SequenceDescriptor(new String[]{"grgzryid"}, new String[]{"S_SC_GRGZRY"}));

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
    addObactioner(String.valueOf(RECEIVE_SEL_PROCESS), new Receive_Sel_Process());//生产领料单引入加工单从表增加操作
    addObactioner(String.valueOf(RECEIVE_CANCEL_APPROVE), new Cancel_Approve());//生产领料单取消审批操作
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    //addObactioner(String.valueOf(PROP_ONCHANGE), new Onchange());
    addObactioner(String.valueOf(TRANSFERSCAN), new transferScan());//旧盘点机
    addObactioner(String.valueOf(NEW_TRANSFERSCAN), new transferScan());//新盘点机
    addObactioner(SHOW_DETAIL, new Show_Detail());//02.17 15:16 新增 新增查看从表明细资料事件发生时的触发操作类. yjg
    addObactioner(String.valueOf(REPORT), new Approve());//2.14 新增报表追此事件 yjg
    addObactioner(String.valueOf(MATCHING_BATCH), new Matching_Batch());//3.24 新增配批号触发事件
    addObactioner(NEXT, new Move_Cursor_ForPrint());//下一页
    addObactioner(PRIOR, new Move_Cursor_ForPrint());//上一页
    addObactioner(ADD_BACK_MATERAIL, masterAddEdit);
    addObactioner(IMPORT_RECEIVE_ADD, new Import_Receive_Add());//生产退料单引入领料单触发事件
    addObactioner(SELECT_MATERAIL, new Select_Materail());//生产领料单引入加工单物料触发事件
    addObactioner(RECODE_ACCOUNT, new Recode_Account());//生产领料单记帐触发事件
    addObactioner(String.valueOf(DETAIL_COPY), new Detail_Copy());//从表复制多行触发事件
    addObactioner(String.valueOf(DELETE_BLANK), new Delete_Blank());//删除数量为空白行触发事件
    addObactioner(TURNPAGE, new Turn_Page());//翻页事件
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
    if(dsStockMaterialDetail != null){
      dsStockMaterialDetail.close();
      dsStockMaterialDetail = null;
    }
    /*
    if(dsDetailWage != null){
      dsDetailWage.close();
      dsDetailWage = null;
    }
    if(dsMasterWage != null){
      dsMasterWage.close();
      dsMasterWage = null;
    }

    if(dsPersonTable != null){
      dsPersonTable.close();
      dsPersonTable = null;
    }*/
    if(dmsxidExistData != null){
      dmsxidExistData.close();
      dmsxidExistData = null;
    }
    if(dssl != null){
      dssl.close();
      dssl = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    if(SC_OUTSTORE_SHOW_ADD_FIELD.equals("1")){
      if(masterProducer != null)
      {
        masterProducer.release();
        masterProducer = null;
      }
    }
    else{
      if(newmasterProducer != null)
     {
       newmasterProducer.release();
       newmasterProducer = null;
      }
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
  /*得到总数量*/
  public final String getZsl()
  {
    return zzsl;
  }
  /*得到总数量*/
  public final String getZgf()
  {
    return zzgf;
  }
  /*得到总数量*/
  public final String getZgf2()
  {
    return zzgf2;
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
        m_RowInfo.put("createdate", today);//制单日期
        m_RowInfo.put("creator", loginName);//操作员
        m_RowInfo.put("drawDate", today);//收发日期
        m_RowInfo.put("creatorid", loginId);
        m_RowInfo.put("deptid", loginDept);//部门
        //m_RowInfo.put("handlePerson", loginName);//经手人
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

    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=min; i<=max; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("cpid", rowInfo.get("cpid_"+i));
      detailRow.put("kwid", rowInfo.get("kwid_"+i));//库位
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性
      detailRow.put("drawNum", formatNumber(rowInfo.get("drawNum_"+i), qtyFormat));//计量数量
      //detailRow.put("drawBigNum", formatNumber(rowInfo.get("drawBigNum_"+i), qtyFormat));//换算数量
      detailRow.put("shouldNum", formatNumber(rowInfo.get("shouldNum_"+i), qtyFormat));//换算数量
      detailRow.put("produceNum", formatNumber(rowInfo.get("produceNum_"+i), qtyFormat));//生产数量
      detailRow.put("batchNo", rowInfo.get("batchNo_"+i));//批号
      detailRow.put("memo", rowInfo.get("memo_"+i));//备注
      //
      detailRow.put("jjdj", formatNumber(rowInfo.get("jjdj_"+i), priceFormat));//计件单价
      detailRow.put("jjgz", formatNumber(rowInfo.get("jjgz_"+i), priceFormat));//计件工资
      detailRow.put("gx", rowInfo.get("gx_"+i));//工序
      detailRow.put("gylxid", rowInfo.get("gylxid_"+i));//工艺路线ID
      detailRow.put("jjff", rowInfo.get("jjff_"+i));//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
      //
      detailRow.put("jjdj2", formatNumber(rowInfo.get("jjdj2_"+i), priceFormat));//计件单价
      detailRow.put("jjgz2", formatNumber(rowInfo.get("jjgz2_"+i), priceFormat));//计件工资
      detailRow.put("gx2", rowInfo.get("gx2_"+i));//工序
      detailRow.put("jjff2", rowInfo.get("jjff2_"+i));//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
      detailRow.put("drawPrice", rowInfo.get("drawPrice_"+i));
      detailRow.put("drawSum", rowInfo.get("drawSum_"+i));
    }
  }
  /**
   *  从表配批号操作，可以看到当前行产品的库存数量
   */
  class Matching_Batch implements Obactioner
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
      RowMap rowinfo = (RowMap)d_RowInfos.get(row);
      dsDetailTable.goToRow(row);
      String drawID = dsDetailTable.getValue("drawID");
      String jgdmxid = dsDetailTable.getValue("jgdmxid");
      String cpid = rowinfo.get("cpid");
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[]{"dmsxid","batchNo"});
      String[] wzmxIDs = parseString(mutibatch,",");
      RowMap detail = null;
      for(int i=0; i < wzmxIDs.length; i++)
      {
        if(wzmxIDs[i].equals("-1"))
          continue;
        RowMap batchRow = getBatchBean(req).getLookupRow(wzmxIDs[i]);
        String ph = batchRow.get("ph");
        String dmsxid = batchRow.get("dmsxid");
        locateGoodsRow.setValue(0, dmsxid);
        locateGoodsRow.setValue(1, ph);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          if(i==0){
            detail = (RowMap)d_RowInfos.get(row);
          }
          else {
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("drawID", drawID);
            dsDetailTable.setValue("batchNo", batchRow.get("ph"));
            dsDetailTable.setValue("jgdmxid", jgdmxid);
            dsDetailTable.setValue("cpid", cpid);
            dsDetailTable.post();
            detail = new RowMap(dsDetailTable);
            detail.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
            d_RowInfos.add(++row, detail);
          }
          detail.put("wzmxid",batchRow.get("wzmxid"));
          detail.put("batchNo",batchRow.get("ph"));
          detail.put("dmsxid",batchRow.get("dmsxid"));
          detail.put("kwid",batchRow.get("kwid"));
          if(SC_DRAW_MATERIAL.equals("1"))
            detail.put("drawNum", batchRow.get("zl"));
          if(i==0){
            long swapRow = Long.parseLong(detail.get("InternalRow"));
            dsDetailTable.goToInternalRow(swapRow);
            dsDetailTable.setValue("wzmxid", batchRow.get("wzmxid"));
            dsDetailTable.setValue("batchNo", batchRow.get("ph"));
            dsDetailTable.setValue("dmsxid", batchRow.get("dmsxid"));
            dsDetailTable.setValue("kwid", batchRow.get("kwid"));
            if(SC_DRAW_MATERIAL.equals("1"))
              dsDetailTable.setValue("drawNum", batchRow.get("zl"));
            dsDetailTable.post();
          }
        }
        else{
          dsDetailTable.setValue("wzmxid", batchRow.get("wzmxid"));
          //dsDetailTable.setValue("batchNo", batchRow.get("ph"));
          //dsDetailTable.setValue("dmsxid", batchRow.get("dmsxid"));
          dsDetailTable.setValue("kwid", batchRow.get("kwid"));
          if(SC_DRAW_MATERIAL.equals("1"))
            dsDetailTable.setValue("drawNum", batchRow.get("zl"));
          dsDetailTable.post();
           detail = (RowMap)d_RowInfos.get(row);
           detail.put("wzmxid",batchRow.get("wzmxid"));
           //detail.put("batchNo",batchRow.get("ph"));
           //detail.put("dmsxid",batchRow.get("dmsxid"));
           detail.put("kwid",batchRow.get("kwid"));
           if(SC_DRAW_MATERIAL.equals("1"))
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
  public Select_Batch getBatchBean(HttpServletRequest req)
  {
    if(selectBatchBean == null)
      selectBatchBean = Select_Batch.getInstance(req);
    return selectBatchBean;
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
    String drawID = dsMasterTable.getValue("drawID");
    //02.28 23:15  修改 将下面此句setQueryString中的sql由原来的手动用+号组成sql`改成现在用combineSQL来组成.
    //解决了当sdfdjid是空的时候会页面上(主要是contract_instore_bottom.jsp上)会出现sql错误的问题.yjg
    dsDetailTable.setQueryString(combineSQL(DETAIL_SQL, "?", new String[]{isMasterAdd ? "-1" : drawID}));

    if(dsDetailTable.isOpen())
      dsDetailTable.refresh();
    else
      dsDetailTable.open();
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
      if(SC_OUTSTORE_SHOW_ADD_FIELD.equals("1"))
        masterProducer.init(request, loginId);
      else
        newmasterProducer.init(request, loginId);
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
      table.getWhereInfo().clearWhereValues();
      //
      String  temp = " AND state<>8 AND state<>2 ";
      String SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid","creatorID"),fgsid,isout,temp});

      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();


     String SUM_SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "creatorID"), fgsid, isout, temp});


     dssl.setQueryString(SUM_SLSQL);
     if(dssl.isOpen())
       dssl.refresh();
     else
       dssl.openDataSet();
     int cn = dssl.getRowCount();
     if(dssl.getRowCount()<1)
       {
        zzsl="0";
        zzgf="0";
        zzgf2="0";
       }
      else
       {
        zzsl=dssl.getValue("zsl");
        zzgf=dssl.getValue("zgf");
        zzgf2=dssl.getValue("zgf2");
       }

      zzsl = zzsl.equals("")?"0":zzsl;
      zzgf = zzgf.equals("")?"0":zzgf;
      zzgf2 = zzgf2.equals("")?"0":zzgf2;
      zzgf = formatNumber(zzgf, qtyFormat);
      zzgf2 = formatNumber(zzgf2, qtyFormat);
    }
  }
  /**
  * 记帐操作的触发类
  */
 class Recode_Account implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
     //是否有符合记帐的数据，有几条
     String SQL = combineSQL(RECODE_DATASQL, "?", new String[]{loginId, isout});
     String UPDATE_SQL = combineSQL(RECODE, "?", new String[]{loginId,isout});
     String count = dataSetProvider.getSequence(SQL);
     if(count.equals("0"))
     {
       data.setMessage(showJavaScript("alert('没有可以记帐的单据')"));
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
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      isReport = false;
      if(String.valueOf(EDIT).equals(action))
      {
        isMasterAdd=false;
        isDetailAdd =false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        drawType = dsMasterTable.getValue("drawtype");
      }
      else{
        isMasterAdd=true;
        isDetailAdd=false;
        drawType =String.valueOf(ADD).equals(action) ? "1" : "-1";//1:新增领料单,-1新增退料单
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
      HttpServletRequest request = data.getRequest();
      if(SC_OUTSTORE_SHOW_ADD_FIELD.equals("1"))
        masterProducer.init(request, loginId);
      else
        newmasterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      /**
       *报表调从表页面,传递operate='2000'操作
       */
       isReport = String.valueOf(REPORT).equals(action);
       String id=null;
       if(isReport){
         isReport = true;
         isApprove = false;
         id = data.getParameter("id");//得到报表传递的参数既收发单据主表ID
       }
       else{
         isReport = false;
         isApprove = true;//审批操作
         id = data.getParameter("id", "");
      }
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();

      drawType = dsMasterTable.getValue("drawType");
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
      String content = dsMasterTable.getValue("drawCode");
      String isOut=dsMasterTable.getValue("isout");
      //03.16 15:04 修改 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
      //以实现:mantis上库存管理中0000158bug描述的:根据下达的部门，进行提交审批；
      if(!isOut.equals("1"))
        approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "receive_material_list", content, dsMasterTable.getValue("deptid"));
      else
        approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "process_issue_list", content, dsMasterTable.getValue("deptid"));
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
      //boolean isSelfGain = String.valueOf(SELF_CANCEL_APPROVE).equals(action);
      String isOut=dsMasterTable.getValue("isout");
      if(!isout.equals("1"))
        approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "receive_material_list");
      else
        approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "process_issue_list");
    }
  }
  /**
   *  选择加工单物料领料单增加操作
   *  生产领料单引入加工单物料
   *  领料单从表增加操作
   */

  class Select_Materail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String selectMaterail = m_RowInfo.get("selectMaterail");
      if(selectMaterail.equals(""))
        return;

      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdwlid");
      String[] jgdwlID = parseString(selectMaterail,",");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String drawID = dsMasterTable.getValue("drawID");
      for(int i=0; i < jgdwlID.length; i++)
      {
        if(jgdwlID[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, jgdwlID[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap importMaterailRow = getMaterailBean(req).getLookupRow(jgdwlID[i]);
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("drawdetailid", "-1");
          dsDetailTable.setValue("jgdwlid",jgdwlID[i]);
          dsDetailTable.setValue("jgdmxid", importMaterailRow.get("jgdmxid"));
          dsDetailTable.setValue("cpid", importMaterailRow.get("cpid"));
          dsDetailTable.setValue("dmsxid", importMaterailRow.get("dmsxid"));
          dsDetailTable.setValue("drawNum", importMaterailRow.get("wlsl"));
          dsDetailTable.setValue("produceNum", importMaterailRow.get("wlscsl"));
          dsDetailTable.setValue("drawID", isMasterAdd ? "-1" : drawID);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
          d_RowInfos.add(detailrow);
        }
      }
    }
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
  private String checkNumRigtt(int tempmin, int tempmax) throws Exception{
    String temp =null;
    RowMap detailrow = null;
    ArrayList list = new ArrayList();
    for(int i=tempmin; i<=tempmax; i++){
      int row = i+1;
      detailrow = (RowMap)d_RowInfos.get(i);
      String cpid = detailrow.get("cpid");
      String dmsxid = detailrow.get("dmsxid");
      String ph = detailrow.get("batchNo");
      StringBuffer buf = new StringBuffer().append(cpid).append(",").append(dmsxid).append(",").append(ph);
      String bufString = buf.toString();
      if(cpid.equals(""))
        return showJavaScript("alert('第"+row+"行产品不能为空');");
      if(list.contains(bufString))
        return showJavaScript("alert('第"+row+"行产品批号重复');");
      else
        list.add(bufString);
      String sl = detailrow.get("drawNum");
      if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
        return temp;
      if(sl.length()>0 && sl.equals("0"))
        return showJavaScript("alert('第"+row+"行数量不能为零！');");
    }
    return null;
  }
  /**
   * 生产领料单生成工资
   * 仅分切生成
   * 在提交审核时调用

  private void buildWage() throws Exception{
    //根据生产领料单得到工作组，根据工作组得到工作组人员和人员基数
     String gzzid = dsMasterTable.getValue("gzzid");
     if(gzzid.equals(""))
        return;
     BigDecimal zgf = dsMasterTable.getBigDecimal("zgf");
     dsPersonTable.setQueryString(combineSQL(GROUP_DETAIL_SQL,"?",new String[]{gzzid}));
     if(dsPersonTable.isOpen())
       dsPersonTable.refresh();
     else
       dsPersonTable.openDataSet();
     //根据生产领料单ID打开工人工资表
     String drawid = dsMasterTable.getValue("drawid");
     dsMasterWage.setQueryString(combineSQL(WAGE_MASTER_SQL, "?", new String[]{drawid}));
     if(dsMasterWage.isOpen())
       dsMasterWage.refresh();
     else
       dsMasterWage.openDataSet();

     String grgzid = null;
     if(dsMasterWage.getRowCount()<1){//如果生产领料单是在新增的情况就插入一行数据
       dsMasterWage.insertRow(false);
       grgzid = dataSetProvider.getSequence("S_SC_GRGZ");
       dsMasterWage.setValue("grgzid",grgzid);
     }
     dsMasterWage.setValue("drawid", drawid);
     dsMasterWage.setValue("gzzid", gzzid);
     dsMasterWage.setValue("deptid", dsMasterTable.getValue("deptid"));
     dsMasterWage.setValue("zgf", formatNumber(zgf.toString(), priceFormat));
     dsMasterWage.setValue("djrq", dsMasterTable.getValue("drawDate"));
     String djh = dsMasterTable.getValue("drawcode");
     dsMasterWage.setValue("djh", dsMasterTable.getValue("drawcode"));
     dsMasterWage.setValue("jsr", dsMasterTable.getValue("handlePerson"));
     dsMasterWage.setValue("bc", dsMasterTable.getValue("bc"));
     dsMasterWage.post();

     grgzid = dsMasterWage.getValue("grgzid");
     dsDetailWage.setQueryString(combineSQL(WAGE_DETAIL_SQL,"?", new String[]{grgzid}));
     if(dsDetailWage.isOpen())
       dsDetailWage.refresh();
     else
       dsDetailWage.openDataSet();
     if(dsDetailWage.getRowCount()>0)
       dsDetailWage.deleteAllRows();

     dsPersonTable.first();
     for(int m=0; m<dsPersonTable.getRowCount();m++){
       BigDecimal ryjs = dsPersonTable.getBigDecimal("ryjs");
       BigDecimal jjgz = ryjs.multiply(zgf);
       dsDetailWage.insertRow(false);
       dsDetailWage.setValue("grgzryID","-1");
       dsDetailWage.setValue("grgzID", grgzid);
       dsDetailWage.setValue("personid", dsPersonTable.getValue("personid"));
       dsDetailWage.setValue("bl", dsPersonTable.getValue("ryjs"));
       dsDetailWage.setValue("jjgz", formatNumber(jjgz.toString(), priceFormat));
       dsDetailWage.post();
       dsPersonTable.next();
     }
  }
  */
  /**
   * 自制收货单生成工资
   * 仅分切生成
   * 在提交审核时调用
   *
  private void buildWage(ArrayList gzzids, ArrayList zgfArray) throws Exception{
    //根据生产领料单得到工作组，根据工作组得到工作组人员和人员基数
    if(gzzids.size()<1)
      return;

    String temp = StringUtils.getArrayValue((String[])gzzids.toArray(new String[gzzids.size()]), "','");
    //根据自制收货单ID打开工人工资表
    String  drawID= dsMasterTable.getValue("drawID");
    dsMasterWage.setQueryString(combineSQL(WAGE_MASTER_SQL, "?", new String[]{drawID}));
    if(dsMasterWage.isOpen())
      dsMasterWage.refresh();
    else
      dsMasterWage.openDataSet();
    ArrayList grgzids = new ArrayList();
    if(dsMasterWage.getRowCount()>0)
    {
      dsMasterWage.first();
      for(int y=0; y<dsMasterWage.getRowCount();y++){
        grgzids.add(dsMasterWage.getValue("grgzid"));
        dsMasterWage.next();
      }
      String grgztemp = StringUtils.getArrayValue((String[])grgzids.toArray(new String[grgzids.size()]), "','");
      dsDetailWage.setQueryString(combineSQL(WAGE_DELETEDTAIL_SQL,"?", new String[]{grgztemp}));
      if(dsDetailWage.isOpen())
        dsDetailWage.refresh();
      else
        dsDetailWage.openDataSet();
      if(dsDetailWage.getRowCount()>0)
        dsDetailWage.deleteAllRows();
      dsMasterWage.deleteAllRows();
    }
    else{//如果是新增的自制收货单，没有工人工资数据。打开工人工资人员结构数据集
      dsDetailWage.setQueryString(WAGE_DETAILSTRUCT_SQL);
      if(dsDetailWage.isOpen())
        dsDetailWage.refresh();
      else
        dsDetailWage.openDataSet();
    }
    for(int q=0; q<gzzids.size();q++){
      String gzzid = (String)gzzids.get(q);
      BigDecimal zgf = (BigDecimal)zgfArray.get(q);
      dsPersonTable.setQueryString(combineSQL(GROUP_DETAIL_SQL,"?",new String[]{gzzid}));
      if(dsPersonTable.isOpen())
        dsPersonTable.refresh();
      else
        dsPersonTable.openDataSet();
      dsMasterWage.insertRow(false);
      String grgzid = dataSetProvider.getSequence("S_SC_GRGZ");
      dsMasterWage.setValue("grgzid",grgzid);
      dsMasterWage.setValue("drawID", drawID);
      dsMasterWage.setValue("gzzid", gzzid);
      dsMasterWage.setValue("deptid", dsMasterTable.getValue("deptid"));
      dsMasterWage.setValue("zgf", formatNumber(zgf.toString(), priceFormat));
      dsMasterWage.setValue("djrq", dsMasterTable.getValue("drawDate"));
      String djh = dsMasterTable.getValue("drawcode");
      dsMasterWage.setValue("djh", dsMasterTable.getValue("drawcode"));
      dsMasterWage.setValue("jsr", dsMasterTable.getValue("handlePerson"));
      dsMasterWage.setValue("bc", dsMasterTable.getValue("bc"));
      dsMasterWage.post();


      dsPersonTable.first();
      for(int m=0; m<dsPersonTable.getRowCount();m++){
        BigDecimal ryjs = dsPersonTable.getBigDecimal("ryjs");
        BigDecimal jjgz = ryjs.multiply(zgf);
        dsDetailWage.insertRow(false);
        dsDetailWage.setValue("grgzryID","-1");
        dsDetailWage.setValue("grgzID", grgzid);
        dsDetailWage.setValue("personid", dsPersonTable.getValue("personid"));
        dsDetailWage.setValue("bl", dsPersonTable.getValue("ryjs"));
        dsDetailWage.setValue("jjgz", formatNumber(jjgz.toString(), priceFormat));
        dsDetailWage.post();
        dsPersonTable.next();
      }
    }
  }*/

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
      //boolean isNeedAccountWage = false;
      //校验表单数据
      String temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
       temp = checkMasterInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }

      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);

      //得到主表主键值
      String drawID = null;
      if(isMasterAdd){
        ds.insertRow(false);
        drawID = dataSetProvider.getSequence("s_sc_drawmaterial");
        ds.setValue("drawID", drawID);
        ds.setValue("filialeID", fgsid);//分公司
        ds.setValue("state","0");
        ds.setValue("createdate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("creatorid", loginId);
        ds.setValue("creator", loginName);//操作员
      }
      //保存从表的数据

      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0),totalSum = new BigDecimal(0),totalSum2= new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        long internalRow = Long.parseLong(detailrow.get("InternalRow"));
        detail.goToInternalRow(internalRow);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("drawID", drawID);

        detail.setValue("drawNum", detailrow.get("drawNum"));//保存数量
        //detail.setValue("drawBigNum", detailrow.get("drawBigNum"));//保存换算数量
        detail.setValue("shouldNum", detailrow.get("shouldNum"));//应发数量
        detail.setValue("produceNum", detailrow.get("produceNum"));//保存生产数量
        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("kwid", detailrow.get("kwid"));
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("drawType", drawType);
        detail.setValue("batchNo", detailrow.get("batchNo"));
        detail.setValue("memo", detailrow.get("memo"));//备注
        detail.setValue("gx", detailrow.get("gx"));//工序
        detail.setValue("gylxid", detailrow.get("gylxid"));//工艺路线ID
        String jjdj = detailrow.get("jjdj");
        detail.setValue("jjdj", detailrow.get("jjdj"));//计件单价
        detail.setValue("jjgz", detailrow.get("jjgz"));//计件工资
        detail.setValue("jjff", detailrow.get("jjff"));//计价方法
        detail.setValue("gx2", detailrow.get("gx2"));//工序
        detail.setValue("jjdj2", detailrow.get("jjdj2"));//计件单价
        detail.setValue("jjgz2", detailrow.get("jjgz2"));//计件工资
        detail.setValue("jjff2", detailrow.get("jjff2"));
        //if((detailrow.get("jjff").equals("3") && !detailrow.get("jjgz").equals("")) || (detailrow.get("jjff").equals("4") && !detailrow.get("jjgz").equals("")))
          //isNeedAccountWage =true;
        detail.setValue("drawPrice", detailrow.get("drawPrice"));
        detail.setValue("drawSum", detailrow.get("drawSum"));

        //保存用户自定义字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
       for(int j=0; j<fields.length; j++)
       {
         String fieldCode = fields[j].getFieldcode();
         detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("drawNum"));
        totalSum = totalSum.add(detail.getBigDecimal("jjgz"));
        totalSum2 = totalSum2.add(detail.getBigDecimal("jjgz2"));
       // detail.next();
      }

      //保存主表数据
      ds.setValue("storeid", rowInfo.get("storeid"));//仓库id
      ds.setValue("handlePerson", rowInfo.get("handlePerson"));//经手人
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("sfdjlbid", rowInfo.get("sfdjlbid"));//收发单据类别ID
      ds.setValue("drawType", drawType);//单据性质
      ds.setValue("ytid", rowInfo.get("ytid"));//用途ID
      ds.setValue("drawdate", rowInfo.get("drawdate"));//领料日期
      ds.setValue("totalnum", totalNum.toString());//总数量
      ds.setValue("memo", rowInfo.get("memo"));//备注
      ds.setValue("netNum", rowInfo.get("netnum"));//净重
      ds.setValue("dyneSide", rowInfo.get("dyneSide"));//达因面
      ds.setValue("hotSide", rowInfo.get("hotSide"));//热封面
      ds.setValue("checkor", rowInfo.get("checkor"));//检验员
      ds.setValue("checkResult", rowInfo.get("checkResult"));// 检验结果
      ds.setValue("isout", isout);//1为外加工，0为自加工
      ds.setValue("dwtxid",rowInfo.get("dwtxid"));//加工厂
      ds.setValue("gzzid", rowInfo.get("gzzid"));//工作组
      ds.setValue("zgf",totalSum.toString());//总工费
      ds.setValue("sc__gzzID", rowInfo.get("sc__gzzID"));
      ds.setValue("zgf2", totalSum2.toString());//总工费
      ds.setValue("bc", rowInfo.get("bc"));//总工费
      String drawcode = null;
      if(ds.getValue("drawcode").equals("")){
        if(isout=="1")
          drawcode = dataSetProvider.getSequence("SELECT pck_base.billNextCode('sc_drawmaterial','drawcode','a') from dual");
        //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"sfdjdh"}, new String[]{"SELECT pck_base.billNextCode('kc_sfdj','sfdjdh','b') from dual"}));
        if(isout=="0")
          drawcode =dataSetProvider.getSequence("SELECT pck_base.billNextCode('sc_drawmaterial','drawcode') from dual");
        ds.setValue("drawcode",drawcode);
      }
      //保存用户自定义的字段
      FieldInfo[] fields = null;
      if(SC_OUTSTORE_SHOW_ADD_FIELD.equals("1"))
        fields = masterProducer.getBakFieldCodes();
      else
        fields = newmasterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      /*String gzzid = ds.getValue("gzzid");
      String sc__gzzid = ds.getValue("sc__gzzid");
      ArrayList gzzidArray = new ArrayList();
      ArrayList zgfArray  = new ArrayList();
      BigDecimal zgf = ds.getBigDecimal("zgf");
      BigDecimal zgf2 = ds.getBigDecimal("zgf2");
      if(!gzzid.equals("") && !isGroup1Empty){
        gzzidArray.add(gzzid);
        zgfArray.add(zgf);
      }
      if(!sc__gzzid.equals("") && !isGroup1Empty2){
        gzzidArray.add(sc__gzzid);
        zgfArray.add(zgf2);
      }
      if(gzzidArray.size()>0){
       buildWage(gzzidArray, zgfArray);
       ds.saveDataSets(new EngineDataSet[]{ds, detail,dsMasterWage,dsDetailWage}, null);
      }
      else*/
        ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      /**
      if(isout=="1")
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"drawcode"}, new String[]{"SELECT pck_base.billNextCode('sc_drawmaterial','drawcode','a') from dual"}));
        //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"sfdjdh"}, new String[]{"SELECT pck_base.billNextCode('kc_sfdj','sfdjdh','b') from dual"}));
      if(isout=="0")
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"drawcode"}, new String[]{"SELECT pck_base.billNextCode('sc_drawmaterial','drawcode') from dual"}));
        */
      //LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_IN);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_OUT);

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action)){
        isMasterAdd=false;
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
      isGroup1Empty = true;//工作组一是否能空
      isGroup1Empty2 = true;//工作组2是否能空
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
        String ph = detailrow.get("batchNo");
        StringBuffer buf = new StringBuffer().append(cpid).append(",").append(dmsxid).append(",").append(ph);
        String bufString = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(bufString))
          return showJavaScript("alert('第"+row+"行产品批号重复');");
        else
          list.add(bufString);
        String sl = detailrow.get("drawNum");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
        String jjff = detailrow.get("jjff");
        String jjgz = detailrow.get("jjgz");
        if((jjff.equals("3") || jjff.equals("4")) && !jjgz.equals(""))
          isGroup1Empty =false;
        if(jjff.equals("4") && !jjgz.equals(""))
          isGroup1Empty =false;
        String jjff2 = detailrow.get("jjff2");
        String jjgz2 = detailrow.get("jjgz2");
        if((jjff2.equals("3") || jjff2.equals("4")) && !jjgz2.equals(""))
          isGroup1Empty2 = false;
      }
      RowMap rowInfo = getMasterRowinfo();
      String gzzid = rowInfo.get("gzzid");
      String sc__gzzid = rowInfo.get("sc__gzzid");
      if(!isGroup1Empty && gzzid.equals(""))
        return showJavaScript("alert('请选择工作组');");
      if(!isGroup1Empty2 && sc__gzzid.equals(""))
        return showJavaScript("alert('请选择工作组2');");
      return null;
    }

    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("drawdate");
      if(temp.equals(""))
        return showJavaScript("alert('收发日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法收发日期！');");
      temp = rowInfo.get("storeid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择仓库！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择部门！');");
      if(isout.equals("1")){
        temp = rowInfo.get("dwtxid");
        if(temp.equals(""))
          return showJavaScript("alert('请选择加工厂！');");
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
      //根据生产领料单ID打开工人工资表
      //删除工人工资
      /*
      String drawid = dsMasterTable.getValue("drawid");
      dsMasterWage.setQueryString(combineSQL(WAGE_MASTER_SQL, "?", new String[]{drawid}));
      if(dsMasterWage.isOpen())
        dsMasterWage.refresh();
      else
        dsMasterWage.openDataSet();
      if(dsMasterWage.getRowCount()>0){
        ArrayList grgzids = new ArrayList(dsMasterWage.getRowCount());
        dsMasterWage.first();
        for(int j=0;j<dsMasterWage.getRowCount();j++){
          String grgzid = dsMasterWage.getValue("grgzid");
          if(!grgzid.equals(""))
            grgzids.add(grgzid);
          dsMasterWage.next();
        }

        String temp2 = StringUtils.getArrayValue((String[])grgzids.toArray(new String[grgzids.size()]), "','");
        dsDetailWage.setQueryString(combineSQL(WAGE_DELETEDTAIL_SQL,"?", new String[]{temp2}));
        if(dsDetailWage.isOpen())
          dsDetailWage.refresh();
        else
          dsDetailWage.openDataSet();
        if(dsDetailWage.getRowCount()>0)
          dsDetailWage.deleteAllRows();
      }
      dsMasterWage.deleteAllRows();
      */
      ds.goToInternalRow(masterRow);
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      //LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_IN);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_OUT);
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
      table.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      String NEWSQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid","creatorID"), fgsid, isout, SQL});
      dsMasterTable.setQueryString(NEWSQL);
      dsMasterTable.setRowMax(null);

      String SUM_SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "creatorID"), fgsid,isout, SQL});


      dssl.setQueryString(SUM_SLSQL);
      if(dssl.isOpen())
        dssl.refresh();
      else
        dssl.openDataSet();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
       {
        zzsl="0";
        zzgf="0";
        zzgf2="0";
       }
      else
       {
        zzsl=dssl.getValue("zsl");
        zzgf=dssl.getValue("zgf");
        zzgf2=dssl.getValue("zgf2");
       }

      zzsl = zzsl.equals("")?"0":zzsl;
      zzgf = zzgf.equals("")?"0":zzgf;
      zzgf2 = zzgf2.equals("")?"0":zzgf2;
      zzgf = formatNumber(zzgf, qtyFormat);
      zzgf2 = formatNumber(zzgf2, qtyFormat);
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
      String drawid = dsMasterTable.getValue("drawid");
      detail.insertRow(false);
      detail.setValue("drawid", isMasterAdd ? "-1" : drawid);
      detail.post();
      RowMap detailrow = new RowMap(detail);
      detailrow.put("InternalRow", String.valueOf(detail.getInternalRow()));
      d_RowInfos.add(detailrow);
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
      EngineDataSet ds = getMaterTable();
      EngineDataSet detail = getDetailTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String drawid = dsMasterTable.getValue("drawid");
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

      String kcsl = "";//库存数量.主要是为当盘点扫描进来的时候.去物资明细中取库存数量
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
        String sql = temp.length() > 0 ? " AND  b.ph IN ('" + temp + "')" : "";
        temp = StringUtils.getArrayValue((String[])tmp_cpids.toArray(new String[tmp_cpids.size()]), ",");
        sql += temp.length() > 0 ? (sql.length()>0 ? " AND" : "") + " b.cpid IN (" + temp + ")" : "";
        temp = StringUtils.getArrayValue((String[])tempdmsxids.toArray(new String[tempdmsxids.size()]), ",");
        sql += temp.length() > 0 ? (sql.length()>0 ? " AND" : "") + " b.dmsxid IN (" + temp + ")" : "";
        sql = combineSQL(STOCK_MATERIAL_LIST, "@", new String[]{sql, storeid});
        dsStockMaterialDetail.setQueryString(sql);
        if(dsStockMaterialDetail.isOpen())//第二个10条数据集先用零时数据集打开
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
      //关闭数据集
      dsTemp.closeDataSet();
      //如果盘点机中没有一条是这个仓库的数据就返回
      if(!dsStockMaterialDetail.isOpen()){
        data.setMessage(showJavaScript("alert('盘点机中没有该仓库的数据')"));
        return;
     }
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[]{"cpid", "dmsxid"});
      EngineRow locateGoodsPhRow = new EngineRow(dsStockMaterialDetail, new String[]{"cpid", "dmsxid", "ph"});
      for(i=0; i<cpids.size(); i++)
      {
        ArrayList tmpcpids = (ArrayList)cpids.get(i);
        ArrayList tmpdmsxids = (ArrayList)dmsxids.get(i);
        ArrayList tmpphs = (ArrayList)phs.get(i);
        ArrayList tmpCpbmsArry = (ArrayList)cpbmsArry.get(i);
        for(int m=0;m<tmpcpids.size();m++)
        {
          kcsl = "";
          String cpid = (String)tmpcpids.get(m);
          String ph = (String)tmpphs.get(m);
          String dmsxid = (String)tmpdmsxids.get(m);
          locateGoodsRow.setValue(0, cpid);
          locateGoodsRow.setValue(1, dmsxid);
          //locateGoodsRow.setValue(2, ph);
          if(dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
          {
            if(!dsDetailTable.getValue("batchNo").equals(ph))
            {
              //用cpid, dmsxid, ph去物资明细表中定位记录,目地是要取到:kcsl
              locateGoodsPhRow.setValue(0, cpid);
              locateGoodsPhRow.setValue(1, dmsxid);
              locateGoodsPhRow.setValue(2, ph);
              if (dsStockMaterialDetail.locate(locateGoodsPhRow, Locate.FIRST))
                kcsl = dsStockMaterialDetail.getValue("kcsl");
              String jgdwlid = dsDetailTable.getValue("jgdwlid");
              dsDetailTable.insertRow(false);
              dsDetailTable.setValue("drawDetailID","-1");
              dsDetailTable.setValue("drawid", isMasterAdd ? "-1" : drawid);
              dsDetailTable.setValue("cpid", cpid);
              dsDetailTable.setValue("jgdmxid", jgdwlid);
              dsDetailTable.setValue("batchNo", ph);
              dsDetailTable.setValue("dmsxid", dmsxid);
              dsDetailTable.setValue("drawNum", kcsl);
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
            //2004-6-16 11:11 新增 当在明细表中定位不到的时候也去取库存里的数量. yjg
            locateGoodsPhRow.setValue(0, cpid);
            locateGoodsPhRow.setValue(1, dmsxid);
            locateGoodsPhRow.setValue(2, ph);
            if (dsStockMaterialDetail.locate(locateGoodsPhRow, Locate.FIRST))
              kcsl = dsStockMaterialDetail.getValue("kcsl");
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("drawDetailID", "-1");
            dsDetailTable.setValue("cpid", cpid);
            dsDetailTable.setValue("dmsxid", dmsxid);
            dsDetailTable.setValue("batchNo", ph);
            dsDetailTable.setValue("drawNum", kcsl);///2004-6-16 11:11 新增 当在明细表中定位不到的时候也去取库存里的数量. yjg
            dsDetailTable.setValue("drawid", drawid);
            dsDetailTable.post();
            RowMap detailrow = null;
            detailrow = new RowMap(dsDetailTable);
            detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
            d_RowInfos.add(detailrow);
            //if (i==0) rejectScanDataMessage.append("以下盘点机读进来的数据\n因其品名规格和规格属性\n在本单据没有找到与之对应的数据,故未能取用\n");
            //rejectScanDataMessage.append(cpbms[i]+phStr[i]+"\n");
          }
        }
      }
      dsStockMaterialDetail.closeDataSet();
      initRowInfo(false,false, true);
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
 /**
   *调用盘点单触发的事件

  class transferScan implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();
      EngineDataSet detail = getDetailTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String drawid = dsMasterTable.getValue("drawid");
      RowMap detailrowinfo = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrowinfo = (RowMap)d_RowInfos.get(i);
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
      String scanValue= req.getParameter("scanValue");//得到包含产品编码和批号的字符串
      String[][] s=engine.util.StringUtils.getArrays(scanValue);
      String[] cpbmStr = s[0];//产品编码数组
      String[] phStr = s[1];//批号数组
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cpid");
      LookUp prodCodeBean = LookupBeanFacade.getInstance(req,SysConstant.BEAN_PRODUCT_CODE);
      prodCodeBean.regData(cpbmStr);
      for(int i=0; i<cpbmStr.length; i++)
      {
        String cpbm = cpbmStr[i];
        String ph = phStr[i];
        RowMap prodCodeRow = prodCodeBean.getLookupRow(cpbm);
        String cpid = prodCodeRow.get("cpid");
        String p_storeid = prodCodeRow.get("storeid");
        if(cpid.equals("") || (!p_storeid.equals(storeid) && !p_storeid.equals("")))//如果cpid为空或者存放仓库不等于所选仓库继续
          continue;
        locateGoodsRow.setValue(0, cpid);
        if(dsDetailTable.locate(locateGoodsRow, Locate.FIRST)){
          String o_ph = dsDetailTable.getValue("batchNo");
          if(isHandwork.equals("0")){
            if(o_ph.equals("") || !o_ph.equals(ph)){
              dsDetailTable.setValue("batchNo", ph);
              dsDetailTable.post();
            }
          }
          else{
             if(o_ph.equals("")){
               dsDetailTable.setValue("batchNo",ph);
               dsDetailTable.post();
             }
             else if(!o_ph.equals(ph))
             {
               dsDetailTable.insertRow(false);
               dsDetailTable.setValue("drawDetailID", "-1");
               dsDetailTable.setValue("cpid", cpid);
               dsDetailTable.setValue("batchNo", ph);
               dsDetailTable.setValue("drawid", drawid);
               dsDetailTable.post();
             }
          }
        }
        else if(isHandwork.equals("1"))
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("drawDetailID", "-1");
          dsDetailTable.setValue("cpid", cpid);
          dsDetailTable.setValue("batchNo", ph);
          dsDetailTable.setValue("drawid", drawid);
          dsDetailTable.post();
        }
      }
      initRowInfo(false,false, true);
    }
  }
   */
  /**
   *  生产领料单从表增加操作
   *  显示的加工单必须是该加工单至少有一条物料还未领完，并且存放仓库和所选仓库要相同的数据才引过来，空的也过来
   *  引入的数据来源于加工单主表
   *  单选加工单主表，通过主表得到这张加工单物料的信息并插入生产领料单从表
   */
  class Receive_Sel_Process implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowinfo = getMasterRowinfo();

      String storeid = rowinfo.get("storeid");
      if(storeid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择仓库')"));
        return;
      }
      String selectProcess = rowinfo.get("selectProcess");
      if(selectProcess.equals(""))
        return;
      EngineDataSet processBomData = null;
      String SQL = combineSQL(SINGLE_PROCESS_SQL,"?", new String[]{selectProcess,fgsid,storeid});
      if(processBomData==null)
      {
        processBomData = new EngineDataSet();
        setDataSetProperty(processBomData,null);
      }
      processBomData.setQueryString(SQL);
      if(!processBomData.isOpen())
        processBomData.openDataSet();
      else
        processBomData.refresh();

      //RowMap processMasterRow = getSingleProcessBean(req).getlook

      if(rowinfo.get("deptid").equals(""))
        rowinfo.put("deptid", processBomData.getValue("deptid"));
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdwlid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String drawid = dsMasterTable.getValue("drawid");

      processBomData.first();
      for(int i=0; i < processBomData.getRowCount(); i++)
      {
        locateGoodsRow.setValue(0, processBomData.getValue("jgdwlid"));
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("drawdetailid","-1");
          dsDetailTable.setValue("jgdwlid", processBomData.getValue("jgdwlid"));
          dsDetailTable.setValue("cpid", processBomData.getValue("cpid"));
          dsDetailTable.setValue("shouldNum", processBomData.getValue("wlsl"));
          //dsDetailTable.setValue("produceNum", processBomData.getValue("wlscsl"));
          dsDetailTable.setValue("jgdmxid", processBomData.getValue("jgdmxid"));
          dsDetailTable.setValue("dmsxid", processBomData.getValue("dmsxid"));
          //dsDetailTable.setValue("batchNo", processBomData.getValue("scph"));
          //dsDetailTable.setValue("kwid", processBomData.getValue("kwid"));
          dsDetailTable.setValue("drawid", isMasterAdd ? "" : drawid);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          RowMap detailrow = new RowMap(dsDetailTable);
          detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
          d_RowInfos.add(detailrow);
        }
        processBomData.next();
      }
      data.setMessage(showJavaScript("big_change()"));
    }
  }
  /**
   *  生产退料单引入领料单从表增加操作
   */
  class Import_Receive_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowinfo = getMasterRowinfo();

      String selectReceive = m_RowInfo.get("selectReceive");
      if(selectReceive.length() == 0)
        return;

      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[]{"jgdmxid","cpid"});
      String[] drawdetailID = parseString(selectReceive,",");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String drawID = dsMasterTable.getValue("drawID");
      for(int i=0; i < drawdetailID.length; i++)
      {
        if(drawdetailID[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        RowMap receiveProdRow = getReceiveProdBean(req).getLookupRow(drawdetailID[i]);
        locateGoodsRow.setValue(0, receiveProdRow.get("jgdmxid"));
        locateGoodsRow.setValue(1, receiveProdRow.get("cpid"));
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          //LookUp look = getBuyPriceBean(req);
          //look.regData(new String[]{cgsqdhwID[i]});
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("drawdetailid", "-1");
          dsDetailTable.setValue("jgdmxid",receiveProdRow.get("jgdmxid"));
          dsDetailTable.setValue("backDrawID", receiveProdRow.get("drawDetailID"));
          dsDetailTable.setValue("cpid",receiveProdRow.get("cpid"));
          dsDetailTable.setValue("drawNum", receiveProdRow.get("drawNum"));
          //dsDetailTable.setValue("produceNum", receiveProdRow.get("produceNum"));
          //dsDetailTable.setValue("drawBigNum", receiveProdRow.get("drawBigNum"));
          dsDetailTable.setValue("dmsxid", receiveProdRow.get("dmsxid"));//规格属性
          dsDetailTable.setValue("drawID", isMasterAdd ? "" : drawID);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
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
      //删除临时数组的一列数据
      RowMap detailrow = (RowMap)d_RowInfos.get(rownum);
      long l_row  = Long.parseLong(detailrow.get("InternalRow"));
      d_RowInfos.remove(rownum);
      ds.goToInternalRow(l_row);
      ds.deleteRow();
      data.setMessage(showJavaScript("big_change()"));
    }
  }
  /**
   *自制收货单复制一行操作
   */
  class Detail_Copy implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发复制操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);
      EngineDataSet ds = getMaterTable();
      EngineDataSet detail = getDetailTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String drawID = dsMasterTable.getValue("drawID");
      RowMap rowinfo = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        rowinfo = (RowMap)d_RowInfos.get(i);
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
        detail.setValue("jjdj", rowinfo.get("jjdj"));//计件单价
        detail.setValue("jjgz", rowinfo.get("jjgz"));//计件工资
        detail.setValue("gx", rowinfo.get("gx"));//工序
        detail.setValue("gylxid", rowinfo.get("gylxid"));//工艺路线ID
        detail.setValue("jjff", rowinfo.get("jjff"));//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
        detail.setValue("jjdj2", formatNumber(rowinfo.get("jjdj2"), priceFormat));//计件单价
        detail.setValue("jjgz2", formatNumber(rowinfo.get("jjgz2"), priceFormat));//计件工资
        detail.setValue("gx2", rowinfo.get("gx2"));//工序
        detail.setValue("jjff2", rowinfo.get("jjff2"));//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
        if(isout.equals("1")){
          detail.setValue("drawPrice", rowinfo.get("drawPrice"));
          detail.setValue("drawSum", rowinfo.get("drawSum"));
        }
        detail.post();
      }
      int num = Integer.parseInt(data.getParameter("rownum"));
      RowMap temprow  = (RowMap)d_RowInfos.get(num);
      detail.goToInternalRow(Long.parseLong(temprow.get("InternalRow")));
      String cpid = detail.getValue("cpid");
      String drawNum = detail.getValue("drawNum");
      String dmsxid = detail.getValue("dmsxid");
      String memo = detail.getValue("memo");
      String kwid = detail.getValue("kwid");
      String drawBigNum = detail.getValue("drawBigNum");
      String produceNum = detail.getValue("produceNum");
      String batchNo = detail.getValue("batchNo");
      String jgdwlid = detail.getValue("jgdwlid");
      String gylxid = detail.getValue("gylxid");
      String gx = detail.getValue("gx");
      String jjdj = detail.getValue("jjdj");
      String jjff = detail.getValue("jjff");
      String gx2 = detail.getValue("gx2");
      String jjdj2 = detail.getValue("jjdj2");
      String jjff2 = detail.getValue("jjff2");
      RowMap masterrow = getMasterRowinfo();
      String tCopyNumber = request.getParameter("tCopyNumber");
      int copyNum= (tCopyNumber==null || tCopyNumber.equals("0")) ? 1 : Integer.parseInt(tCopyNumber);
      for(int j=0; j<copyNum; j++){
        detail.insertRow(false);
        detail.setValue("drawDetailID","-1");
        detail.setValue("drawID", isMasterAdd ? "-1" : drawID);
        detail.setValue("jgdwlid", jgdwlid);
        detail.setValue("cpid", cpid);
        detail.setValue("dmsxid",dmsxid);
        detail.setValue("memo",memo);
        detail.setValue("kwid", kwid);
        detail.setValue("gylxid", gylxid);
        detail.setValue("gx", gx);
        detail.setValue("jjdj",jjdj);
        detail.setValue("jjff",jjff);
        detail.setValue("gx2", gx2);
        detail.setValue("jjdj2",jjdj2);
        detail.setValue("jjff2",jjff2);
        detail.post();
        RowMap detailrow = new RowMap(detail);
        detailrow.put("InternalRow", String.valueOf(detail.getInternalRow()));
        d_RowInfos.add(detailrow);
      }
    }
 }
 /**
  * 删除产品编码为空白行操作
  */
 class Delete_Blank implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     putDetailInfo(data.getRequest());
     String cpid = null;
     EngineDataSet detail = getDetailTable();
     for(int i=0; i< d_RowInfos.size(); i++)
     {
       RowMap detailrow = (RowMap)d_RowInfos.get(i);
       long internalRow = Long.parseLong(detailrow.get("InternalRow"));
       detail.goToInternalRow(internalRow);
       String drawNum = detailrow.get("drawNum");
       if(drawNum.equals(""))
       {
         d_RowInfos.remove(i);
         detail.deleteRow();
         i--;
       }
     }
   }
  }
  /**
  * 提交仓库
  */
 class Onchange implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest request = data.getRequest();
     putDetailInfo(request);
   }
 }

 //02.17 15:12 新增 新增一个当页面上单击事件发生时想要查看明细资料 这个单击事件的Show_Detail类 . yjg
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
   * 新增 实现翻页为方便打印的类.
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

  public long getMasterRow()
  {
    return masterRow;
  }
  /**
   * 得到加工单物料的bean
   * @param req WEB的请求
   * @return 返回加工单物料信息的bean
   */
  public B_ImportProcessMaterail getMaterailBean(HttpServletRequest req)
  {
    if(importProcessBean == null)
      importProcessBean = B_ImportProcessMaterail.getInstance(req);
    return importProcessBean;
  }

  /**
   * 得到领料单货物的bean
   * @param req WEB的请求
   * @return 返回领料单货物的bean
   */
  public B_ImportReceive getReceiveProdBean(HttpServletRequest req)
  {
    if(importReceiveBean == null)
      importReceiveBean = B_ImportReceive.getInstance(req);
    return importReceiveBean;
  }
  /**
   * 得到加工单货物的bean
   * @param req WEB的请求
   * @return 返回加工单货物的bean
   */
  public B_DrawSingleProcess getSingleProcessBean(HttpServletRequest req)
  {
    if(drawSingleProcessBean == null)
      drawSingleProcessBean = B_DrawSingleProcess.getInstance(req);
    return drawSingleProcessBean;
  }
};

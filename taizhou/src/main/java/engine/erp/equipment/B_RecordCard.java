package engine.erp.equipment;

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
import java.sql.SQLException;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 设备履历卡/p>
 * <p>Description: 设备--设备履历卡<br>
 * <p>Copyright: Copyright (c) 2004 10.13</p>
 * <p>Company: engine
 * @author 王惠义
 * @version 1.0(2004 10.13)
 */

public final class B_RecordCard extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";



  public  static final String ONCHANGE                 = "10041";//
  public  static final String APPERTAINCHANGEDEL_ADD   = "10200400";  //主体及附属设备变更添加操作
  public  static final String APPERTAIN_ADD            = "10200401";  //主体及附属设备添加操作
  public  static final String DISPLACERESULT_ADD       = "10200402";  //转移记录添加操作
  public  static final String STOPANNAL_ADD            = "10200403";  //转移记录添加操作
  public  static final String DEVOLVEANNAL_ADD         = "10200404";  //调拨记录添加操作
  public  static final String REJECTANNAL_ADD          = "10200405";  //清理或报废记录添加操作

  public  static final String APPERTAINCHANGEDEL_DEL   = "10200406";  //主体及附属设备变更删除操作
  public  static final String APPERTAIN_DEL            = "10200407";  //主体及附属设备变更删除操作
  public  static final String DISPLACERESULT_DEL       = "10200408";  //转移记录删除操作
  public  static final String STOPANNAL_DEL            = "10200409";  //停用记录删除操作
  public  static final String DEVOLVEANNAL_DEL         = "10200410";  //调拨记录删除操作
  public  static final String REJECTANNAL_DEL          = "10200411";  //清理或报废记录删除操作

  private static final String MASTER_STRUT_SQL = "SELECT * FROM sb_recordcard WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM sb_recordcard WHERE ? ? ";
  private static final String Appertain_STRUT_SQL = "SELECT * FROM sb_appertain WHERE 1<>1";
  private static final String Appertain_SQL    = "SELECT * FROM sb_appertain WHERE equipmentID='?' ";//
  private static final String appertainChange_strut_sql="SELECT * FROM sb_appertainChange WHERE 1<>1";
  private static final String appertainChange_sql="SELECT * FROM sb_appertainChange WHERE equipmentID='?'";
  private static final String displaceResult_strut_sql="SELECT * FROM sb_displaceResult WHERE 1<>1";
  private static final String displaceResult_SQL= "SELECT * FROM sb_displaceResult WHERE equipmentID='?' ";//
  private static final String stopAnnal_strut_sql="SELECT * FROM sb_stopAnnal WHERE 1<>1";
  private static final String stopAnnal_SQL= "SELECT * FROM sb_stopAnnal WHERE equipmentID='?' ";//
  private static final String devolveAnnal_STRUT_SQL= "SELECT * FROM sb_devolveAnnal WHERE 1<>1";//
  private static final String devolveAnnal_SQL= "SELECT * FROM sb_devolveAnnal WHERE equipmentID='?'";//
  private static final String rejectAnnal_STRUT_SQL= "SELECT * FROM sb_rejectAnnal WHERE 1<>1";//
  private static final String rejectAnnal_SQL= "SELECT * FROM sb_rejectAnnal WHERE equipmentID='?'";//
  private static final String heavyRepair_STRUT_SQL= "select a.mainresultdetailid,a.cpid,a.memo,b.maintainresultid,b.maintain_type,b.maintain_date,b.maintainResultNO"+
                                                     " from sb_mainResultDetail a,sb_maintainResult b WHERE 1<>1";//
  private static final String heavyRepair_SQL= "select a.mainresultdetailid,a.cpid,a.memo,b.maintainresultid,b.maintain_type,b.maintain_date,b.maintainResultNO "+
                                               "from sb_mainResultDetail a,sb_maintainResult b "+
                                               "where a.maintainresultid=b.maintainresultid "+
                                               "and b.maintain_type='1'and b.billType='0'and b.state =1 and equipmentID='?'";
  private static final String maintainResult_STRUT_SQL= "select a.mainresultdetailid,a.maintainresultid,a.cpid,a.content,a.fact_startdate,a.fact_finishdate,a.personid,a.finish_circs,a.memo "+
                                                         "from sb_mainResultDetail a,sb_maintainresult b  WHERE 1<>1";//
  private static final String maintainResult_SQL= "select a.mainresultdetailid,a.maintainresultid,a.cpid,a.content,a.fact_startdate,a.fact_finishdate,a.personid,a.finish_circs,a.memo "+
                                                  "from sb_mainResultDetail a,sb_maintainresult b "+
                                                  "where a.maintainresultid=b.maintainresultid "+
                                                  "and b.billtype='1' and b.state =1 and equipmentID='?'";//

  private EngineDataSet dsMasterTable = new EngineDataSet();//主表
  private EngineDataSet dsAppertainTable = new EngineDataSet();//主体及附属设备
  private EngineDataSet dsappertainChangeTable = new EngineDataSet();//主体及附属设备变更记录
  private EngineDataSet displaceResultTable = new EngineDataSet();//转移记录
  private EngineDataSet stopAnnalTable = new EngineDataSet();//停用记录
  private EngineDataSet devolveAnnalTable = new EngineDataSet();//调拨记录
  private EngineDataSet rejectAnnalTable = new EngineDataSet();//清理或报废记录
  private EngineDataSet heavyRepairTable = new EngineDataSet();//大修理记录
  private EngineDataSet maintainResultTable = new EngineDataSet();//保养记录

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sb_recordcard");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsAppertainTable, "sb_appertain");
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态
  public boolean isReport = false;
  public  boolean isAppertainAdd = false; // 主体及附属设备表是否在添加状态
  public  boolean isAppertainChangeAdd = false; // 主体及附属设备变更表是否在添加状态
  public  boolean isDisplaceResultAdd = false; // 主体及附属设备变更表是否在添加状态
  public  boolean isStopAnnalAdd = false; //停用记录表是否在添加状态
  public  boolean isDevolveAnnalAdd = false; //调拨记录表是否在添加状态
  public  boolean isRejectAnnalAdd = false; //清理或报废记录表是否在添加状态

  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList Appertain_RowInfos = null; //主体及附属设备多行记录的引用
  private ArrayList appertainChange_RowInfos = null; //主体及附属设备变更多行记录的引用
  private ArrayList displaceResult_RowInfos = null; //转移记录多行记录的引用
  private ArrayList stopAnnal_RowInfos = null; //停用记录多行记录的引用
  private ArrayList devolveAnnal_RowInfos = null; //调拨记录记录的引用
  private ArrayList rejectAnnal_RowInfos = null; //清理或报废记录的引用
  private ArrayList heavyRepair_RowInfos = null; //大修理记录的引用
  private ArrayList maintainResult_RowInfos = null; //保养记录的引用


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
  private String equipmentID = null;
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
  public static B_RecordCard getInstance(HttpServletRequest request)
  {
    B_RecordCard recordCardBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "recordCardBean";
      recordCardBean = (B_RecordCard)session.getAttribute(beanName);
      if(recordCardBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        recordCardBean = new B_RecordCard();
        recordCardBean.qtyFormat = loginBean.getQtyFormat();
        recordCardBean.priceFormat = loginBean.getPriceFormat();
        recordCardBean.sumFormat = loginBean.getSumFormat();
        recordCardBean.filialeid = loginBean.getFirstDeptID();
        recordCardBean.loginId = loginBean.getUserID();
        recordCardBean.loginName = loginBean.getUserName();
        //recordCardBean.filialeid = loginBean.getDeptID();
        recordCardBean.user = loginBean.getUser();
        recordCardBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        session.setAttribute(beanName, recordCardBean);
      }
    }
    return recordCardBean;
  }

  /**
   * 构造函数
   */
  private B_RecordCard()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  public  HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "sb_recordcard", "sb_recordcard");//查询得到数据库中配置的字段
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsMasterTable,MASTER_STRUT_SQL);
    setDataSetProperty(dsAppertainTable,Appertain_STRUT_SQL);
    setDataSetProperty(dsappertainChangeTable,appertainChange_strut_sql);
    setDataSetProperty(displaceResultTable,displaceResult_strut_sql);
    setDataSetProperty(stopAnnalTable,displaceResult_strut_sql);
    setDataSetProperty(devolveAnnalTable,devolveAnnal_STRUT_SQL);
    setDataSetProperty(rejectAnnalTable,rejectAnnal_STRUT_SQL);
    setDataSetProperty(heavyRepairTable,heavyRepair_STRUT_SQL);
    setDataSetProperty(maintainResultTable,maintainResult_STRUT_SQL);


    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"productCheckNo"}, new String[]{"SELECT pck_base.billNextCode('zl_bucheck','productCheckNo','a') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"equipment_code"}, new boolean[]{false}, null, 0));
    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"sfdjdh"}, new String[]{"SELECT pck_base.billNextCode('kc_sfdj','sfdjdh','a') from dual"}));
    dsAppertainTable.setSequence(new SequenceDescriptor(new String[]{"sb_appertainID"}, new String[]{"s_sb_appertain"}));
    dsAppertainTable.setSort(new SortDescriptor("", new String[]{"appertain_name"}, new boolean[]{false}, null, 0));

    dsappertainChangeTable.setSequence(new SequenceDescriptor(new String[]{"appertainChangeID"}, new String[]{"S_sb_appertainChange"}));
    dsappertainChangeTable.setSort(new SortDescriptor("", new String[]{"appertain_name"}, new boolean[]{false}, null, 0));

    displaceResultTable.setSequence(new SequenceDescriptor(new String[]{"displaceResultID"}, new String[]{"S_sb_displaceResult"}));
    displaceResultTable.setSort(new SortDescriptor("", new String[]{"displace_date"}, new boolean[]{false}, null, 0));

    stopAnnalTable.setSequence(new SequenceDescriptor(new String[]{"stopAnnalID"}, new String[]{"S_sb_stopAnnal"}));
    stopAnnalTable.setSort(new SortDescriptor("", new String[]{"start_date"}, new boolean[]{false}, null, 0));

    devolveAnnalTable.setSequence(new SequenceDescriptor(new String[]{"devolveAnnalID"}, new String[]{"S_sb_stopAnnal"}));
    devolveAnnalTable.setSort(new SortDescriptor("", new String[]{"devolve_date"}, new boolean[]{false}, null, 0));

    rejectAnnalTable.setSequence(new SequenceDescriptor(new String[]{"rejectAnnalID"}, new String[]{"S_sb_stopAnnal"}));
    rejectAnnalTable.setSort(new SortDescriptor("", new String[]{"virement_date"}, new boolean[]{false}, null, 0));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Bill_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(APPERTAINCHANGEDEL_ADD), new Detail_Add());
    addObactioner(String.valueOf(APPERTAIN_ADD), new Detail_Add());
    addObactioner(String.valueOf(DISPLACERESULT_ADD), new Detail_Add());
    addObactioner(String.valueOf(STOPANNAL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DEVOLVEANNAL_ADD), new Detail_Add());
    addObactioner(String.valueOf(REJECTANNAL_ADD), new Detail_Add());

    addObactioner(String.valueOf(APPERTAIN_DEL), new Detail_Delete());
    addObactioner(String.valueOf(APPERTAINCHANGEDEL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(DISPLACERESULT_DEL), new Detail_Delete());
    addObactioner(String.valueOf(STOPANNAL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(DEVOLVEANNAL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(REJECTANNAL_DEL), new Detail_Delete());
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
    if(dsAppertainTable != null){
      dsAppertainTable.close();
      dsAppertainTable = null;
    }
    if(dsappertainChangeTable != null){
      dsappertainChangeTable.close();
      dsappertainChangeTable = null;
    }
    if(displaceResultTable != null){
      displaceResultTable.close();
      displaceResultTable = null;
    }
    if(stopAnnalTable != null){
      stopAnnalTable.close();
      stopAnnalTable = null;
    }
    if(devolveAnnalTable != null){
      devolveAnnalTable.close();
      devolveAnnalTable = null;
    }
    if(rejectAnnalTable != null){
      rejectAnnalTable.close();
      rejectAnnalTable = null;
    }
    if(heavyRepairTable != null){
      heavyRepairTable.close();
      heavyRepairTable = null;
    }
    if(maintainResultTable != null){
      maintainResultTable.close();
      maintainResultTable = null;
    }
    log = null;
    m_RowInfo = null;
    Appertain_RowInfos = null;
    appertainChange_RowInfos = null;
    displaceResult_RowInfos=null;
    stopAnnal_RowInfos=null;
    devolveAnnal_RowInfos=null;
    rejectAnnal_RowInfos=null;
    heavyRepair_RowInfos=null;
    maintainResult_RowInfos=null;
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
      ////////////////////////////主体及附属设备
      EngineDataSet dsAppertain = dsAppertainTable;
      if(Appertain_RowInfos == null){
        Appertain_RowInfos = new ArrayList(dsAppertain.getRowCount());
      }
      else if(isInit)
        Appertain_RowInfos.clear();
      dsAppertain.first();
      int a=dsAppertain.getRowCount();
      for(int i=0; i<dsAppertain.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsAppertain);
        Appertain_RowInfos.add(row);
        dsAppertain.next();
      }
      ////////////////////////////主体及附属设备变更记录
      EngineDataSet dsappertainChange =dsappertainChangeTable;
      if(appertainChange_RowInfos == null){appertainChange_RowInfos = new ArrayList(dsappertainChange.getRowCount());}
      else if(isInit)
        appertainChange_RowInfos.clear();
      dsappertainChange.first();
      for(int i=0; i<dsappertainChange.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsappertainChange);
        appertainChange_RowInfos.add(row);
        dsappertainChange.next();
      }
      ////////////////////////////转移记录
      EngineDataSet displaceResult =displaceResultTable;
      if(displaceResult_RowInfos == null){displaceResult_RowInfos = new ArrayList(displaceResult.getRowCount());}
      else if(isInit)
        displaceResult_RowInfos.clear();
      displaceResult.first();
      for(int i=0; i<displaceResult.getRowCount(); i++)
      {
        RowMap row = new RowMap(displaceResult);
        displaceResult_RowInfos.add(row);
        displaceResult.next();
      }

      ////////////////////////////停用记录
      EngineDataSet stopAnnal =stopAnnalTable;
      if(stopAnnal_RowInfos == null){stopAnnal_RowInfos = new ArrayList(stopAnnal.getRowCount());}
      else if(isInit)
        stopAnnal_RowInfos.clear();
      stopAnnal.first();
      for(int i=0; i<stopAnnal.getRowCount(); i++)
      {
        RowMap row = new RowMap(stopAnnal);
        stopAnnal_RowInfos.add(row);
        stopAnnal.next();
      }
      ////////////////////////////调拨记录
      EngineDataSet dsDevolveAnnal =devolveAnnalTable;
      if(devolveAnnal_RowInfos == null){devolveAnnal_RowInfos = new ArrayList(dsDevolveAnnal.getRowCount());}
      else if(isInit)
        devolveAnnal_RowInfos.clear();
      dsDevolveAnnal.first();
      for(int i=0; i<dsDevolveAnnal.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDevolveAnnal);
        devolveAnnal_RowInfos.add(row);
        dsDevolveAnnal.next();
      }
      ////////////////////////////清理或报废记录
      EngineDataSet dsRejectAnnal =rejectAnnalTable;
      if(rejectAnnal_RowInfos== null){rejectAnnal_RowInfos = new ArrayList(dsRejectAnnal.getRowCount());}
      else if(isInit)
        rejectAnnal_RowInfos.clear();
      dsRejectAnnal.first();
      for(int i=0; i<dsRejectAnnal.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsRejectAnnal);
        rejectAnnal_RowInfos.add(row);
        dsRejectAnnal.next();
      }
    ////////////////////////////大修理记录
    EngineDataSet dsHeavyRepair =heavyRepairTable;
    if(heavyRepair_RowInfos== null){heavyRepair_RowInfos = new ArrayList(dsHeavyRepair.getRowCount());}
    else if(isInit)
      heavyRepair_RowInfos.clear();
    dsHeavyRepair.first();
    for(int i=0; i<dsHeavyRepair.getRowCount(); i++)
    {
      RowMap row = new RowMap(dsHeavyRepair);
      heavyRepair_RowInfos.add(row);
      dsHeavyRepair.next();
    }
    //maintainResultTable
    ////////////////////////////保养记录
    EngineDataSet dsMaintainResult =maintainResultTable;
    if(maintainResult_RowInfos== null){maintainResult_RowInfos = new ArrayList(dsMaintainResult.getRowCount());}
    else if(isInit)
      maintainResult_RowInfos.clear();
    dsMaintainResult.first();
    for(int i=0; i<dsMaintainResult.getRowCount(); i++)
    {
      RowMap row = new RowMap(dsMaintainResult);
      maintainResult_RowInfos.add(row);
      dsMaintainResult.next();
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
    //if(Appertain_RowInfos.size()>0)Appertain_RowInfos.clear();
    //保存网页的所有信息
    rowInfo.put(request);
    /*主体及附属设备*/
    int rownum = Appertain_RowInfos.size();
    RowMap appertainrow = null;
    for(int i=0; i<rownum; i++)
    {
      appertainrow = (RowMap)Appertain_RowInfos.get(i);
      appertainrow.put("appertain_name", rowInfo.get("appertain_name_01_"+i));//
      appertainrow.put("standard_gg", rowInfo.get("standard_gg_01_"+i));//
      appertainrow.put("buy_money", rowInfo.get("buy_money_"+i));//
      //保存用户自定义的字段
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        appertainrow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
     /*主体及附属设备变更记录*/
    int frownum = appertainChange_RowInfos.size();
    RowMap appertainChangeRow = null;
    for(int i=0; i<frownum; i++)
    {
      appertainChangeRow = (RowMap)appertainChange_RowInfos.get(i);
      //System.out.println("");
      appertainChangeRow.put("change_date", rowInfo.get("change_date_"+i));//日期
      appertainChangeRow.put("voucher", rowInfo.get("voucher_"+i));//凭证号数
      appertainChangeRow.put("appertain_name", rowInfo.get("appertain_name_"+i));//名称
      appertainChangeRow.put("standard_gg", rowInfo.get("standard_gg_02_"+i));//型号规格
      appertainChangeRow.put("unit", rowInfo.get("unit_"+i));//
      appertainChangeRow.put("num", rowInfo.get("num_"+i));//
      appertainChangeRow.put("add_money", rowInfo.get("add_money_"+i));//
      appertainChangeRow.put("reduce_money", rowInfo.get("reduce_money_"+i));//
    }
    /*转移记录*/
    int dirownum = displaceResult_RowInfos.size();
    RowMap displaceResultRow = null;
    for(int i=0; i<dirownum; i++)
    {
      displaceResultRow = (RowMap)displaceResult_RowInfos.get(i);
      displaceResultRow.put("displace_date", rowInfo.get("displace_date_"+i));//
      displaceResultRow.put("take_deptID", rowInfo.get("take_deptID_"+i));//
      displaceResultRow.put("depositary", rowInfo.get("depositary_"+i));//
      displaceResultRow.put("handoverPersonID", rowInfo.get("handoverPersonID_"+i));
      displaceResultRow.put("takePersonID", rowInfo.get("takePersonID_"+i));//
    }
        /*停用记录*/
    int sirownum = stopAnnal_RowInfos.size();
    RowMap stopAnnal = null;
    for(int i=0; i<sirownum; i++)
    {
      stopAnnal = (RowMap)stopAnnal_RowInfos.get(i);
      /*开始时间*/
      String startDate=null;
      String  end_date=null;
      String start_date_hour=rowInfo.get("start_date_hour_"+i);
      if(start_date_hour.length()<2)start_date_hour="0"+start_date_hour;
      String start_date_minute=rowInfo.get("start_date_minute_"+i);
      if(start_date_minute.length()<2)start_date_minute="0"+start_date_minute;
      if(!rowInfo.get("start_date_hour_"+i).trim().equals("")&&!rowInfo.get("start_date_minute_"+i).trim().equals(""))
        startDate=rowInfo.get("start_date_"+i)+" "+start_date_hour+":"+start_date_minute+":"+"00";
      else startDate=startDate=rowInfo.get("start_date_"+i);
      stopAnnal.put("start_date", startDate);
      /*结束时间*/
      String end_date_hour=rowInfo.get("end_date_hour_"+i);
      if(end_date_hour.length()<2)end_date_hour="0"+end_date_hour;
      String end_date_minute=rowInfo.get("end_date_minute_"+i);
      if(end_date_minute.length()<2)end_date_minute="0"+end_date_minute;
      if(!rowInfo.get("end_date_hour_"+i).trim().equals("")&&!rowInfo.get("end_date_minute_"+i).trim().equals(""))
        end_date=rowInfo.get("end_date_"+i)+" "+end_date_hour+":"+end_date_minute+":"+"00";
      else end_date=rowInfo.get("end_date_"+i);
      stopAnnal.put("end_date", end_date);
      stopAnnal.put("cause", rowInfo.get("cause_"+i));//
    }
    /*调拨记录*/
    int dbirownum = devolveAnnal_RowInfos.size();
    RowMap devolveAnnalRow = null;
    for(int i=0; i<dbirownum; i++)
    {
      devolveAnnalRow = (RowMap)devolveAnnal_RowInfos.get(i);
      devolveAnnalRow.put("devolve_date", rowInfo.get("devolve_date_"+i));//
      devolveAnnalRow.put("deptid", rowInfo.get("devolve_deptid_"+i));//
      devolveAnnalRow.put("devolveNO", rowInfo.get("devolveNO_"+i));//
      devolveAnnalRow.put("devolve_price", rowInfo.get("devolve_price_"+i));
      devolveAnnalRow.put("memo", rowInfo.get("memo_"+i));//
    }
    /*清理或报废记录*/
    int rejrownum = rejectAnnal_RowInfos.size();
    RowMap rejectAnnalRow = null;
    for(int i=0; i<rejrownum; i++)
    {
      rejectAnnalRow = (RowMap)rejectAnnal_RowInfos.get(i);
      rejectAnnalRow.put("liquidate_cause", rowInfo.get("liquidate_cause_"+i));//
      rejectAnnalRow.put("virement_date", rowInfo.get("virement_date_"+i));//
      rejectAnnalRow.put("liquidate_rate", rowInfo.get("liquidate_rate_"+i));//
      rejectAnnalRow.put("formerly_price", rowInfo.get("formerly_price_"+i));//
      rejectAnnalRow.put("earning", rowInfo.get("earning_"+i));
      rejectAnnalRow.put("total_depreciation", rowInfo.get("total_depreciation_"+i));//
      rejectAnnalRow.put("liquidate_margin", rowInfo.get("liquidate_margin_"+i));//
    }
  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getAppertainTable(){
    if(!dsAppertainTable.isOpen())
      dsAppertainTable.open();
    return dsAppertainTable;
  }
  /*打开从表*/
  public final void openAppertainTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : equipmentID;
    SQL = combineSQL(Appertain_SQL, "?", new String[]{SQL});

    dsAppertainTable.setQueryString(SQL);
    if(!dsAppertainTable.isOpen())
      dsAppertainTable.open();
    else
      dsAppertainTable.refresh();
  }
    /*得到主体及附属设备变更记录表对象*/
  public final EngineDataSet getAppertainChangeTable(){
    if(!dsappertainChangeTable.isOpen())
      dsappertainChangeTable.open();
    return dsappertainChangeTable;
  }
  //打开主体及附属设备变更记录表
  public final void openAppertainChangeTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : equipmentID;
    SQL = combineSQL(appertainChange_sql, "?", new String[]{SQL});
    dsappertainChangeTable.setQueryString(SQL);
    if(!dsappertainChangeTable.isOpen())
      dsappertainChangeTable.open();
    else
      dsappertainChangeTable.refresh();
  }
    /*得到转移记录表对象*/
  public final EngineDataSet getDisplaceResultTable(){
    if(!displaceResultTable.isOpen())
      displaceResultTable.open();
    return displaceResultTable;
  }
  /*打开转移记录表*/
  public final void openDisplaceResultTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : equipmentID;
    SQL = combineSQL(displaceResult_SQL, "?", new String[]{SQL});
    displaceResultTable.setQueryString(SQL);
    if(!displaceResultTable.isOpen())
      displaceResultTable.open();
    else
      displaceResultTable.refresh();
  }
      /*得到停用记录表对象*/
  public final EngineDataSet getStopAnnalTable(){
    if(!stopAnnalTable.isOpen())
      stopAnnalTable.open();
    return stopAnnalTable;
  }
  /*打开停用记录表*/
  public final void openStopAnnalTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : equipmentID;
    SQL = combineSQL(stopAnnal_SQL, "?", new String[]{SQL});
    stopAnnalTable.setQueryString(SQL);
    if(!stopAnnalTable.isOpen())
      stopAnnalTable.open();
    else
      stopAnnalTable.refresh();
  }
  /*得到调拨记录表对象*/
  public final EngineDataSet getDevolveAnnalTable(){
    if(!devolveAnnalTable.isOpen())
      devolveAnnalTable.open();
    return devolveAnnalTable;
  }
  /*打开调拨记录表*/
  public final void openDevolveAnnalTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : equipmentID;
    SQL = combineSQL(devolveAnnal_SQL, "?", new String[]{SQL});
    devolveAnnalTable.setQueryString(SQL);
    if(!devolveAnnalTable.isOpen())
      devolveAnnalTable.open();
    else
      devolveAnnalTable.refresh();
  }

  /*得到清理或报废记录表对象*/
  public final EngineDataSet getRejectAnnalTable(){
    if(!rejectAnnalTable.isOpen())
      rejectAnnalTable.open();
    return rejectAnnalTable;
  }
    /*打开清理或报废记录表*/
  public final void openRejectAnnalTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : equipmentID;
    SQL = combineSQL(rejectAnnal_SQL, "?", new String[]{SQL});
    rejectAnnalTable.setQueryString(SQL);
    if(!rejectAnnalTable.isOpen())
      rejectAnnalTable.open();
    else
      rejectAnnalTable.refresh();
  }
    /*得到大修理记录表对象*/
  public final EngineDataSet getHeavyRepairTable(){
    if(!heavyRepairTable.isOpen())
      heavyRepairTable.open();
    return heavyRepairTable;
  }
    /*打开大修理记录表*/
  public final void openHeavyRepairTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : equipmentID;
    SQL = combineSQL(heavyRepair_SQL, "?", new String[]{SQL});
    heavyRepairTable.setQueryString(SQL);
    if(!heavyRepairTable.isOpen())
      heavyRepairTable.open();
    else
      heavyRepairTable.refresh();
  }
      /*得到保养记录表对象*/
  public final EngineDataSet getMaintainResultTable(){
    if(!maintainResultTable.isOpen())
      maintainResultTable.open();
    return maintainResultTable;
  }
      /*打开保养记录表*/
  public final void openMaintainResultTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : equipmentID;
    SQL = combineSQL(maintainResult_SQL, "?", new String[]{SQL});
    maintainResultTable.setQueryString(SQL);
    if(!maintainResultTable.isOpen())
      maintainResultTable.open();
    else
      maintainResultTable.refresh();
  }
  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表多列的信息*/
  public final RowMap[] getAppertainRowinfos() {
    RowMap[] rows = new RowMap[Appertain_RowInfos.size()];
    Appertain_RowInfos.toArray(rows);
    return rows;
  }
    /*得到从表多列的信息*/
  public final RowMap[] getAppertainChangeRowinfos() {
    RowMap[] rows = new RowMap[appertainChange_RowInfos.size()];
    appertainChange_RowInfos.toArray(rows);
    return rows;
  }
    /*得到转移记录表多列的信息*/
  public final RowMap[] getDisplaceResult_RowInfos() {
    RowMap[] rows = new RowMap[displaceResult_RowInfos.size()];
    displaceResult_RowInfos.toArray(rows);
    return rows;
  }
/*得到转移记录表多列的信息*/
  public final RowMap[] getStopAnnal_RowInfos() {
    RowMap[] rows = new RowMap[stopAnnal_RowInfos.size()];
    stopAnnal_RowInfos.toArray(rows);
    return rows;
  }

/*得到调拨记录表多列的信息*/
  public final RowMap[] getDevolveAnnal_RowInfos() {
    RowMap[] rows = new RowMap[devolveAnnal_RowInfos.size()];
    devolveAnnal_RowInfos.toArray(rows);
    return rows;
  }
/*得到清理或报废记录表多列的信息*/
  public final RowMap[] getRejectAnnal_RowInfos() {
    RowMap[] rows = new RowMap[rejectAnnal_RowInfos.size()];
    rejectAnnal_RowInfos.toArray(rows);
    return rows;
  }
/*得到大修理记录表多列的信息*/
  public final RowMap[] getHeavyRepair_RowInfos() {
    RowMap[] rows = new RowMap[heavyRepair_RowInfos.size()];
    heavyRepair_RowInfos.toArray(rows);
    return rows;
  }
  /*得到保养记录表多列的信息*/
  public final RowMap[] getMaintainResult_RowInfos() {
    RowMap[] rows = new RowMap[maintainResult_RowInfos.size()];
    maintainResult_RowInfos.toArray(rows);
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
      isAppertainChangeAdd = false;
      isDisplaceResultAdd=false;
      String SQL=combineSQL(MASTER_SQL,"?",new String[]{user.getHandleDeptValue("deptid")});
      //combineSQL(Quality_SQL, "?", new String[]{user.getHandleDeptValue("deptid")});//部门权限
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsAppertainTable.isOpen() && dsAppertainTable.getRowCount() > 0)
        dsAppertainTable.empty();
      if(dsappertainChangeTable.isOpen() && dsappertainChangeTable.getRowCount() > 0)
        dsappertainChangeTable.empty();
      if(displaceResultTable.isOpen() && displaceResultTable.getRowCount() > 0)
        displaceResultTable.empty();
      if(stopAnnalTable.isOpen() && stopAnnalTable.getRowCount() > 0)
        stopAnnalTable.empty();
      if(devolveAnnalTable.isOpen() && devolveAnnalTable.getRowCount() > 0)
        devolveAnnalTable.empty();
      if(rejectAnnalTable.isOpen() && rejectAnnalTable.getRowCount() > 0)
        rejectAnnalTable.empty();
      if(heavyRepairTable.isOpen() && heavyRepairTable.getRowCount() > 0)
        heavyRepairTable.empty();
      if(maintainResultTable.isOpen() && maintainResultTable.getRowCount() > 0)
        maintainResultTable.empty();
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
      equipmentID = dsMasterTable.getValue("equipmentID");
      //打开从表
      openAppertainTable(false);
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
        isAppertainChangeAdd = false;
        isDisplaceResultAdd=false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        equipmentID = dsMasterTable.getValue("equipmentID");
      }
      else{//打开从表
        isAppertainChangeAdd = true;
        isAppertainAdd=true;
        isDisplaceResultAdd=true;
        isDevolveAnnalAdd=true;
        isRejectAnnalAdd=true;
      }
      synchronized(dsAppertainTable){
        openAppertainTable(isMasterAdd);
      }
      synchronized(dsappertainChangeTable){
        openAppertainChangeTable(isMasterAdd);
      }
      synchronized(displaceResultTable){
        openDisplaceResultTable(isMasterAdd);
      }
      synchronized(stopAnnalTable){
        openStopAnnalTable(isMasterAdd);
      }
      synchronized(devolveAnnalTable){
        openDevolveAnnalTable(isMasterAdd);
      }
      synchronized(rejectAnnalTable){
        openRejectAnnalTable(isMasterAdd);
      }
      synchronized(heavyRepairTable){
        openHeavyRepairTable(isMasterAdd);
      }
      synchronized(maintainResultTable){
        openMaintainResultTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      try{

      putDetailInfo(data.getRequest());

      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
       /*
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
       */
      /*判断设备代码是否重复*/
      if(isMasterAdd){
        String equipment_code= rowInfo.get("equipment_code");
        String sql="select count(equipment_code) from sb_recordcard where equipment_code='"+equipment_code+"'";
        String count=dataSetProvider.getSequence(sql);
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('该设备编码已经存在');"));
          return;
        }
      }
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);

      //得到主表主键值
      String equipmentID = null;
      if(isMasterAdd){
        ds.insertRow(false);
        equipmentID = dataSetProvider.getSequence("s_sb_recordcard");
        ds.setValue("equipmentID", equipmentID);
      }

      /*--------------------主体及附属设备----------------*/
      RowMap appertainrow = null;
      EngineDataSet detail = getAppertainTable();
      detail.first();
      int index=detail.getRowCount();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        appertainrow = (RowMap)Appertain_RowInfos.get(i);
        detail.setValue("equipmentID", equipmentID);
        detail.setValue("appertain_name", appertainrow.get("appertain_name"));//
        detail.setValue("standard_gg", appertainrow.get("standard_gg"));//
        detail.setValue("buy_money", appertainrow.get("buy_money"));//
        detail.post();
        detail.next();
      }

      /*-------------------主体及附属设备变更记录-------------*/
      RowMap appertainChangerow = null;
      EngineDataSet appertainChange =getAppertainChangeTable();
      appertainChange.first();
      int a=appertainChange_RowInfos.size();
      int b=appertainChange.getRowCount();
      for(int i=0; i<appertainChange.getRowCount(); i++)
      {
        appertainChangerow = (RowMap)appertainChange_RowInfos.get(i);
        appertainChange.setValue("equipmentID", equipmentID);
        appertainChange.setValue("change_date",appertainChangerow.get("change_date"));
        appertainChange.setValue("voucher",appertainChangerow.get("voucher"));
        appertainChange.setValue("appertain_name",appertainChangerow.get("appertain_name"));
        appertainChange.setValue("standard_gg",appertainChangerow.get("standard_gg"));
        appertainChange.setValue("unit",appertainChangerow.get("unit"));
        appertainChange.setValue("num",appertainChangerow.get("num"));
        appertainChange.setValue("add_money",appertainChangerow.get("add_money"));
        appertainChange.setValue("reduce_money",appertainChangerow.get("reduce_money"));
        appertainChange.post();
        appertainChange.next();
      }

      /*------------------------转移记录-----------------------*/
      RowMap displaceResultrow = null;
      EngineDataSet dsDisplaceResult =getDisplaceResultTable();
      appertainChange.first();
      for(int i=0; i<dsDisplaceResult.getRowCount(); i++)
      {
        displaceResultrow = (RowMap)displaceResult_RowInfos.get(i);
        dsDisplaceResult.setValue("equipmentID", equipmentID);
        dsDisplaceResult.setValue("displace_date",displaceResultrow.get("displace_date"));
        dsDisplaceResult.setValue("take_deptID",displaceResultrow.get("take_deptID"));
        dsDisplaceResult.setValue("depositary",displaceResultrow.get("depositary"));
        dsDisplaceResult.setValue("handoverPersonID",displaceResultrow.get("handoverPersonID"));
        dsDisplaceResult.setValue("takePersonID",displaceResultrow.get("takePersonID"));
        dsDisplaceResult.post();
        dsDisplaceResult.next();
      }

      /*------------------------停用记录-----------------------*/
      RowMap stopAnnalrow = null;
      EngineDataSet dsStopAnnal =getStopAnnalTable();
      appertainChange.first();
      for(int i=0; i<dsStopAnnal.getRowCount(); i++)
      {
        stopAnnalrow = (RowMap)stopAnnal_RowInfos.get(i);
        dsStopAnnal.setValue("equipmentID", equipmentID);
        dsStopAnnal.setValue("start_date",stopAnnalrow.get("start_date"));
        dsStopAnnal.setValue("end_date",stopAnnalrow.get("end_date"));
        dsStopAnnal.setValue("cause",stopAnnalrow.get("cause"));
        dsStopAnnal.post();
        dsStopAnnal.next();
      }
      /*------------------------调拨记录-----------------------*/
      RowMap devolveAnnalrow = null;
      EngineDataSet dsDevolveAnnal =getDevolveAnnalTable();
      appertainChange.first();
      for(int i=0; i<dsDevolveAnnal.getRowCount(); i++)
      {
        devolveAnnalrow = (RowMap)devolveAnnal_RowInfos.get(i);
        dsDevolveAnnal.setValue("equipmentID", equipmentID);
        dsDevolveAnnal.setValue("devolve_date",devolveAnnalrow.get("devolve_date"));
        dsDevolveAnnal.setValue("deptid",devolveAnnalrow.get("deptid"));
        dsDevolveAnnal.setValue("devolveNO",devolveAnnalrow.get("devolveNO"));
        dsDevolveAnnal.setValue("devolve_price",devolveAnnalrow.get("devolve_price"));
        dsDevolveAnnal.setValue("memo",devolveAnnalrow.get("memo"));
        dsDevolveAnnal.post();
        dsDevolveAnnal.next();
      }

    /*------------------------清理或报废记录-----------------------*/
      RowMap rejectAnnalrow = null;
      EngineDataSet dsRejectAnnal=getRejectAnnalTable();
      appertainChange.first();
      for(int i=0; i<dsRejectAnnal.getRowCount(); i++)
      {
        rejectAnnalrow = (RowMap)rejectAnnal_RowInfos.get(i);
        dsRejectAnnal.setValue("equipmentID", equipmentID);
        dsRejectAnnal.setValue("liquidate_cause",rejectAnnalrow.get("liquidate_cause"));
        dsRejectAnnal.setValue("virement_date",rejectAnnalrow.get("virement_date"));
        dsRejectAnnal.setValue("liquidate_rate",rejectAnnalrow.get("liquidate_rate"));
        dsRejectAnnal.setValue("formerly_price",rejectAnnalrow.get("formerly_price"));
        dsRejectAnnal.setValue("earning",rejectAnnalrow.get("earning"));
        dsRejectAnnal.setValue("total_depreciation",rejectAnnalrow.get("total_depreciation"));
        dsRejectAnnal.setValue("liquidate_margin",rejectAnnalrow.get("liquidate_margin"));
        dsRejectAnnal.post();
        dsRejectAnnal.next();
      }
      /*------------------------保存主表数据-----------------------*/
      ds.setValue("equipment_code", rowInfo.get("equipment_code"));//设备编码
      ds.setValue("equipment_name", rowInfo.get("equipment_name"));//设备名称
      ds.setValue("standard_gg", rowInfo.get("standard_gg"));//型号规格
      ds.setValue("unit", rowInfo.get("unit"));//单位
      ds.setValue("manufactureDate", rowInfo.get("manufactureDate"));//制造日期
      ds.setValue("manufacturer", rowInfo.get("manufacturer"));//制造厂
      ds.setValue("buy_date", rowInfo.get("buy_date"));//购买日期
      ds.setValue("buy_money", rowInfo.get("buy_money"));//购置金额
      ds.setValue("use_date", rowInfo.get("use_date"));//开始使用日期
      ds.setValue("use_deptID", rowInfo.get("use_deptID"));//使用部门
      ds.setValue("depositary", rowInfo.get("depositary"));//存放地点
      ds.setValue("deptid", rowInfo.get("deptid"));//购入部门
      ds.setValue("use_state","0");//引用状态
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail,appertainChange,dsStopAnnal,dsDisplaceResult,dsDevolveAnnal,dsRejectAnnal}, null);
      //ds.saveDataSets(new EngineDataSet[]{ds, appertainChange}, null);
      //ds.saveDataSets(new EngineDataSet[]{ds, dsStopAnnal}, null);
      //ds.saveDataSets(new EngineDataSet[]{ds, dsDisplaceResult}, null);
      //ds.saveDataSets(new EngineDataSet[]{ds, dsDevolveAnnal}, null);
      //ds.saveDataSets(new EngineDataSet[]{ds, dsRejectAnnal}, null);
      //ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_EQUIPMENT);

      /*保存新增*/
      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
        appertainChange.empty();
        dsDisplaceResult.empty();
        dsStopAnnal.empty();
        dsDevolveAnnal.empty();
        dsRejectAnnal.empty();
        heavyRepairTable.empty();
        maintainResultTable.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
      }
      catch(Exception e1){
        String message=e1.getMessage();
        data.setMessage(showJavaScript("alert('"+message+"')"));
      }
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      String temp = null;
      RowMap appertainrow = null;
      if(Appertain_RowInfos.size()<1)
        return showJavaScript("alert('技术指标不能为空')");
      for(int i=0; i<appertainChange_RowInfos.size(); i++)
      {
        int row = i+1;
        appertainrow = (RowMap)appertainChange_RowInfos.get(i);
        String result = appertainrow.get("result");
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
      String get_date = rowInfo.get("get_date");
      if(get_date.length()>0&&!isDate(get_date))
        return showJavaScript("alert('非法到货日期！');");
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
      try{
        if(isMasterAdd){
          data.setMessage(showJavaScript("backList();"));
          return;
        }
        EngineDataSet ds = getMaterTable();
        ds.goToInternalRow(masterRow);

        dsAppertainTable.deleteAllRows();
        dsappertainChangeTable.deleteAllRows();
        displaceResultTable.deleteAllRows();
        stopAnnalTable.deleteAllRows();
        devolveAnnalTable.deleteAllRows();
        rejectAnnalTable.deleteAllRows();

        ds.saveDataSets(new EngineDataSet[]{ds, dsAppertainTable}, null);
        ds.saveDataSets(new EngineDataSet[]{ds, dsappertainChangeTable}, null);
        ds.saveDataSets(new EngineDataSet[]{ds, displaceResultTable}, null);
        ds.saveDataSets(new EngineDataSet[]{ds, stopAnnalTable}, null);
        ds.saveDataSets(new EngineDataSet[]{ds, devolveAnnalTable}, null);
        ds.saveDataSets(new EngineDataSet[]{ds, rejectAnnalTable}, null);
        ds.deleteRow();
        ds.saveChanges();
        Appertain_RowInfos.clear();
        appertainChange_RowInfos.clear();
        displaceResult_RowInfos.clear();
        stopAnnal_RowInfos.clear();
        devolveAnnal_RowInfos.clear();
        rejectAnnal_RowInfos.clear();
        heavyRepair_RowInfos.clear();
        maintainResult_RowInfos.clear();
        data.setMessage(showJavaScript("backList();"));
      }
      catch(Exception e1){
        data.setMessage(showJavaScript("alert('该设备已经被别的单据引用,不能删除');"));
        return;
      }
      finally{
        String SQL=combineSQL(MASTER_SQL,"?",new String[]{user.getHandleDeptValue("deptid")});
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);
        putDetailInfo(data.getRequest());
        return;
      }
    }
  }
  /**
   *  查询
   */
  class Bill_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String tempSQL=null;
      table.getWhereInfo().setWhereValues(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      tempSQL=combineSQL(MASTER_SQL,"?",new String[]{user.getHandleDeptValue("deptid")});
      if(SQL.length() > 0){
        tempSQL=tempSQL+" AND "+SQL;
      }
      dsMasterTable.setQueryString(tempSQL);
      dsMasterTable.setRowMax(null);
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
        new QueryColumn(master.getColumn("equipmentID"), null, null, null),
        //new QueryColumn(master.getColumn("htrq"), null, null, null, "a", ">="),
        //new QueryColumn(master.getColumn("htrq"), null, null, null, "b", "<="),
        //new QueryColumn(master.getColumn("ksrq"), null, null, null, "a", ">="),
        //new QueryColumn(master.getColumn("ksrq"), null, null, null, "b", "<="),
        ///new QueryColumn(master.getColumn("jsrq"), null, null, null, "a", ">="),
        //new QueryColumn(master.getColumn("jsrq"), null, null, null, "b", "<="),
        //new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        //new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//购货单位
        //new QueryColumn(master.getColumn("qddd"), null, null, null),
        //new QueryColumn(master.getColumn("equipmentID"), "sb_appertain", "equipmentID", "cpid", null, "="),//从表产品
        //new QueryColumn(master.getColumn("equipmentID"), "VW_sb_appertain", "equipmentID", "product", "product", "like"),//从表产品
        //new QueryColumn(master.getColumn("equipmentID"), "VW_sb_appertain", "equipmentID", "cpbm", "cpbm", "left_like"),//从表编码
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
      String equipmentID = dsMasterTable.getValue("equipmentID");
      for(int i=0; i < cgsqdhwID.length; i++)
      {
        if(cgsqdhwID[i].equals("-1"))
          continue;
        RowMap appertainrow = null;
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
          dsDetailTable.setValue("equipmentID", "-1");
          dsDetailTable.setValue("cgsqdhwid",cgsqdhwID[i]);
          dsDetailTable.setValue("cpid",buyapplyRow.get("cpid"));
          dsDetailTable.setValue("sl", String.valueOf(wkhtl));
          dsDetailTable.setValue("dj", buyapplyRow.get("dj"));
          dsDetailTable.setValue("je", formatNumber(String.valueOf(wkhtl*dj), sumFormat));
          dsDetailTable.setValue("ybje", formatNumber(rowInfo.get("hl").length() > 0 ? String.valueOf(wkhtl*dj/hl) : "", sumFormat));
          dsDetailTable.setValue("dmsxid", buyapplyRow.get("dmsxid"));
          dsDetailTable.setValue("equipmentID", isMasterAdd ? "" : equipmentID);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          appertainrow = new RowMap(dsDetailTable);
          Appertain_RowInfos.add(appertainrow);
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
      EngineDataSet ds = getMaterTable();
      isAppertainAdd = String.valueOf(APPERTAIN_ADD).equals(action);//
      isAppertainChangeAdd = String.valueOf(APPERTAINCHANGEDEL_ADD).equals(action);//
      isDisplaceResultAdd=String.valueOf(DISPLACERESULT_ADD).equals(action);//
      isStopAnnalAdd=String.valueOf(STOPANNAL_ADD).equals(action);//
      isDevolveAnnalAdd=String.valueOf(DEVOLVEANNAL_ADD).equals(action);//
      isRejectAnnalAdd=String.valueOf(REJECTANNAL_ADD).equals(action);//
      EngineDataSet detail=null;
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      ////////主体及附属设备
      if(isAppertainAdd){
        detail =getAppertainTable();
        String equipmentID = dsMasterTable.getValue("equipmentID");
        detail.insertRow(false);
        detail.setValue("equipmentID", isMasterAdd ? "-1" : equipmentID);
        detail.post();
        Appertain_RowInfos.add(new RowMap());
        data.setMessage(showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_0');"));
      }
      /////////主体及附属设备变更
      if(isAppertainChangeAdd){
        detail =getAppertainChangeTable();
        String equipmentID = dsMasterTable.getValue("equipmentID");
        detail.insertRow(false);
        detail.setValue("equipmentID", isMasterAdd ? "-1" : equipmentID);
        detail.post();
        appertainChange_RowInfos.add(new RowMap());
        data.setMessage(showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_1');"));
      }
      /////////转移记录
      if(isDisplaceResultAdd){
        detail =getDisplaceResultTable();
        String equipmentID = dsMasterTable.getValue("equipmentID");
        detail.insertRow(false);
        detail.setValue("equipmentID", isMasterAdd ? "-1" : equipmentID);
        detail.post();
        displaceResult_RowInfos.add(new RowMap());
        data.setMessage(showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_4');"));
      }
      /////////停用记录
      if(isStopAnnalAdd){
        detail =getStopAnnalTable();
        String equipmentID = dsMasterTable.getValue("equipmentID");
        detail.insertRow(false);
        detail.setValue("equipmentID", isMasterAdd ? "-1" : equipmentID);
        detail.post();
        stopAnnal_RowInfos.add(new RowMap());
        data.setMessage(showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_5');"));
      }
      /////////调拨记录
      //devolveAnnalTable
      if(isDevolveAnnalAdd){
        detail =getDevolveAnnalTable();
        String equipmentID = dsMasterTable.getValue("equipmentID");
        detail.insertRow(false);
        detail.setValue("equipmentID", isMasterAdd ? "-1" : equipmentID);
        detail.post();
        devolveAnnal_RowInfos.add(new RowMap());
        data.setMessage(showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_6');"));
      }
      /////////清理或报废记录
      if(isRejectAnnalAdd){
        detail =getRejectAnnalTable();
        String equipmentID = dsMasterTable.getValue("equipmentID");
        detail.insertRow(false);
        detail.setValue("equipmentID", isMasterAdd ? "-1" : equipmentID);
        detail.post();
        rejectAnnal_RowInfos.add(new RowMap());
        data.setMessage(showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_7');"));
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
      String javascript=null;
      try{
        putDetailInfo(data.getRequest());
        boolean isAppertainDel=false;//是否主体及附属设备删除
        boolean isAppertainChangeDel=false;//是否主体及附属设备变更记录删除
        boolean isDisplaceResultDel=false;//是否转移记录删除
        boolean isStopAnnalDel=false;//是否停用记录删除
        boolean isDevolveAnnalDel=false;//是否调拨记录删除
        boolean isRejectAnnalDel=false;//是否清理或报废记录删除
        /*判断删除操作*/
        isAppertainDel=String.valueOf(APPERTAIN_DEL).equals(action);
        isAppertainChangeDel=String.valueOf(APPERTAINCHANGEDEL_DEL).equals(action);
        isDisplaceResultDel=String.valueOf(DISPLACERESULT_DEL).equals(action);
        isStopAnnalDel=String.valueOf(STOPANNAL_DEL).equals(action);
        isDevolveAnnalDel=String.valueOf(DEVOLVEANNAL_DEL).equals(action);
        isRejectAnnalDel=String.valueOf(REJECTANNAL_DEL).equals(action);
        /*定义全局变量*/
        ArrayList detail_RowInfos = null;

        int rownum = Integer.parseInt(data.getParameter("rownum"));
        EngineDataSet ds=null;
/*----------------------------------------------------------------------------------------
        @*主体及附属设备删除
----------------------------------------------------------------------------------------*/

        if(isAppertainDel){
          detail_RowInfos=Appertain_RowInfos;
          ds =getAppertainTable();
          javascript="SetActiveTab(INFO_EX,'INFO_EX_0');";
        }

/*----------------------------------------------------------------------------------------
        @*主体及附属设备变更记录删除
----------------------------------------------------------------------------------------*/
        if(isAppertainChangeDel){
          detail_RowInfos=appertainChange_RowInfos;
          ds =getAppertainChangeTable();
          javascript="SetActiveTab(INFO_EX,'INFO_EX_1');";
        }

/*----------------------------------------------------------------------------------------
        @*转移记录删除
----------------------------------------------------------------------------------------*/
        if(isDisplaceResultDel){
          detail_RowInfos=displaceResult_RowInfos;
          ds =getDisplaceResultTable();
          javascript="SetActiveTab(INFO_EX,'INFO_EX_4');";
        }

/*----------------------------------------------------------------------------------------
        @*停用记录删除
----------------------------------------------------------------------------------------*/
        if(isStopAnnalDel){
          detail_RowInfos=stopAnnal_RowInfos;
          ds =getStopAnnalTable();
          javascript="SetActiveTab(INFO_EX,'INFO_EX_5');";
        }

/*----------------------------------------------------------------------------------------
        @*调拨记录删除
----------------------------------------------------------------------------------------*/
        if(isDevolveAnnalDel){
          detail_RowInfos=devolveAnnal_RowInfos;
          ds =getDevolveAnnalTable();
          javascript="SetActiveTab(INFO_EX,'INFO_EX_6');";
        }

/*----------------------------------------------------------------------------------------
        @*清理或报废记录删除
----------------------------------------------------------------------------------------*/
        if(isRejectAnnalDel){
          detail_RowInfos=rejectAnnal_RowInfos;
          ds =getRejectAnnalTable();
          javascript="SetActiveTab(INFO_EX,'INFO_EX_7');";
        }

        //删除临时数组的一列数据
        detail_RowInfos.remove(rownum);
        ds.goToRow(rownum);
        ds.deleteRow();
        data.setMessage(showJavaScript(javascript));
      }
      catch(Exception e1){
        //data.setMessage(showJavaScript("alert('"+e1.getMessage()+"')"));
        data.setMessage(showJavaScript(javascript));
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
}

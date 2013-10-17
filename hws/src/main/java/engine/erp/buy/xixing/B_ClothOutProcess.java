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


import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/*************************************************
 * <p>Title: 坯布外加工单</p>
 * <p>Description: 采购坯布外加工单</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author xiaozhi
 *************************************************/

public final class B_ClothOutProcess extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL      = "10001";//单击显示框架下方从表信息事件
  public static final String COMPLETE          = "1002";//完成操作
    public  static final String ONCHANGE       = "1003";
     public  static final String BONCHANGE       = "1004";
  public static final String DETAIL_IMPORT_MRP = "1080";
  public static final String CANCLE_APPROVE    = "1090";//取消审批
  public static final String SINGLE_IMPORT_ADD = "1191";//选择物料需求计划主表，引入全部从表信息操作类
  public  static final String WB_ONCHANGE      = "10031";//选择外币类别触发事件

  public  static final String PBQKMX_ADD       = "1020";
  public  static final String WJGQK_ADD        = "1021";
  public  static final String WJGCP_ADD        = "1022";

  public  static final String PBQKMX_DEL       = "1023";
  public  static final String WJGQK_DEL        = "1024";
  public  static final String WJGCP_DEL        = "1025";

  public  static final String PBQKMX_SEL       = "1027";
  public  static final String WJGQK_SEL        = "1028";
  public  static final String WJGCP_SEL        = "1029";

  public  static final String RECODE_ACCOUNT   = "1026";

  public  static final String REPORT = "2000";//报表追踪操作



  private static final String MASTER_STRUT_SQL = "SELECT * FROM cg_pbwjgd WHERE 1<>1";//取主表结构
  private static final String MASTER_SQL    = "SELECT * FROM cg_pbwjgd WHERE ? AND fgsid=? ? ORDER BY djh DESC";

  private static final String Pbwjgwl_SQL    = "SELECT * FROM cg_pbwjgwl  where  pbwjgdid='?' ";//取坯布情况明细表数据----从表1
  private static final String pbwjgqk_SQL    = "SELECT * FROM cg_pbwjgqk  where  pbwjgdid='?' ";//取外加工单情况数据------从表2
  private static final String pbwjgcp_SQL    = "SELECT * FROM cg_pbwjgcp  where  pbwjgdid='?' ";//取坯布外加工成品数据----从表3
  private static final String Pbwjgwl_STRUCT_SQL    = "SELECT * FROM cg_pbwjgwl  where  1<>1 ";//取坯布情况明细表数据----从表1
  private static final String pbwjgqk_STRUCT_SQL    = "SELECT * FROM cg_pbwjgqk  where  1<>1 ";//取外加工单情况数据------从表2
  private static final String pbwjgcp_STRUCT_SQL    = "SELECT * FROM cg_pbwjgcp  where  1<>1 ";//取坯布外加工成品数据----从表3


    /*以下是对从表的条件查询,需提供ypxxID参数.*/
  private static final String YPGZJL_SQL    = "SELECT * FROM cg_pbwjgwl WHERE pbwjgdID= ";//应聘人员工作经历
  private static final String YPJYQK_SQL    = "SELECT * FROM cg_pbwjgqk WHERE pbwjgdID= ";//应聘人员教育情况
  private static final String MSXX_SQL      = "SELECT * FROM cg_pbwjgcp WHERE  pbwjgdID= ";//应聘人员面试信息



  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM cg_pbwjgd WHERE pbwjgdid='?'";
  //提取物料需求计划明细的SQL语句
  private static final String MRP_DETAIL_SQL = "SELECT * FROM sc_wlxqjhmx WHERE nvl(xgl,0)>nvl(ygl,0) AND chxz=2 AND wlxqjhid= ";

    private static final String RECODE_DATASQL = " SELECT COUNT(*)   FROM cg_pbwjgd a WHERE a.czyid='?' AND  a.zt=1";//计账
    private static final String RECODE = "UPDATE cg_pbwjgd a SET a.zt=2 WHERE a.czyid='?' AND  a.zt=1";//计账


  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表

  private EngineDataSet dsPbwjgwlTable  = new EngineDataSet();//从表1
  private EngineDataSet dspbwjgqkTable  = new EngineDataSet();//从表2
  private EngineDataSet dspbwjgcpTable  = new EngineDataSet();//从表3

  private EngineDataSet dsBuyPrice = null;
  private LookUp foreignBean = null; //外币信息的bean的引用
  private LookUp corpBean = null;//往来单位Bean

  //public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable,"cg_pbwjgd");//
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable,"cg_pbwjgd","cg_pbwjgd");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsPbwjgwlTable,"cg_pbwjgwl");
  public  HtmlTableProducer bdetailProducer = new HtmlTableProducer(dspbwjgqkTable,"cg_pbwjgqk");
  public  HtmlTableProducer cdetailProducer = new HtmlTableProducer(dspbwjgcpTable,"cg_pbwjgcp");

  public  boolean isRep = false;
  public boolean isReport = false;        //是否报表跟踪
  public  boolean isApprove = false;     //是否在审批状态
  private boolean isMasterAdd = true;    //是否在添加状态
  public boolean isDetailAdd = false;   // 从表是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList Pbqkmx_Rowinfos = null; //
  private ArrayList Wjgqk_Rowinfos = null;
  private ArrayList Wjgcp_Rowinfos = null;


  private LookUp B_ClothOutProcessBean = null; //采购报价的bean的引用, 用于提取采购报价

  private LookUp productBean = null; //产品信息的bean的引用, 用于提取产品信息

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  public String bjfs = "";   //报价方式
  private User user = null;//new一个登录员工的对象包含部门权限
  private String pbwjgdid= "";
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  /**
   * 坯布外加工单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_ClothOutProcess getInstance(HttpServletRequest request)
  {
    B_ClothOutProcess b_ClothOutProcessBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_ClothOutProcessBean";
      b_ClothOutProcessBean = (B_ClothOutProcess)session.getAttribute(beanName);
      if(b_ClothOutProcessBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        b_ClothOutProcessBean = new B_ClothOutProcess();
        b_ClothOutProcessBean.qtyFormat = loginBean.getQtyFormat();
        b_ClothOutProcessBean.priceFormat = loginBean.getPriceFormat();
        b_ClothOutProcessBean.sumFormat = loginBean.getSumFormat();

        b_ClothOutProcessBean.fgsid = loginBean.getFirstDeptID();
        b_ClothOutProcessBean.loginId = loginBean.getUserID();
        b_ClothOutProcessBean.loginName = loginBean.getUserName();
        b_ClothOutProcessBean.loginDept = loginBean.getDeptID();
        //b_ClothOutProcessBean.bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");
        b_ClothOutProcessBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        b_ClothOutProcessBean.user = loginBean.getUser();
        //设置格式化的字段
        b_ClothOutProcessBean.dsPbwjgwlTable.setColumnFormat("sl", b_ClothOutProcessBean.qtyFormat);
        b_ClothOutProcessBean.dsPbwjgwlTable.setColumnFormat("dj", b_ClothOutProcessBean.priceFormat);
        b_ClothOutProcessBean.dsPbwjgwlTable.setColumnFormat("je", b_ClothOutProcessBean.sumFormat);
        b_ClothOutProcessBean.dspbwjgqkTable.setColumnFormat("jgje", b_ClothOutProcessBean.sumFormat);
        b_ClothOutProcessBean.dspbwjgqkTable.setColumnFormat("jgdj", b_ClothOutProcessBean.priceFormat);
        b_ClothOutProcessBean.dspbwjgqkTable.setColumnFormat("ysl", b_ClothOutProcessBean.sumFormat);
        b_ClothOutProcessBean.dspbwjgcpTable.setColumnFormat("jgwdj", b_ClothOutProcessBean.priceFormat);
        b_ClothOutProcessBean.dspbwjgcpTable.setColumnFormat("je", b_ClothOutProcessBean.sumFormat);



        session.setAttribute(beanName, b_ClothOutProcessBean);
      }
    }
    return b_ClothOutProcessBean;
  }

  /**
   * 构造函数
   */
  private B_ClothOutProcess()
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

    setDataSetProperty(dsPbwjgwlTable, Pbwjgwl_STRUCT_SQL);
    setDataSetProperty(dspbwjgqkTable, pbwjgqk_STRUCT_SQL);
    setDataSetProperty(dspbwjgcpTable, pbwjgcp_STRUCT_SQL);

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"djh"}, new String[]{"SELECT pck_base.billNextCode('cg_pbwjgd','djh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"djh"}, new boolean[]{true}, null, 0));

    dsPbwjgwlTable.setSequence(new SequenceDescriptor(new String[]{"pbwjgwlID"}, new String[]{"S_CG_PBWJGWL"}));
    dspbwjgqkTable.setSequence(new SequenceDescriptor(new String[]{"pbwjgqkid"}, new String[]{"S_CG_PBWJGQK"}));
    dspbwjgcpTable.setSequence(new SequenceDescriptor(new String[]{"pbwjgcpid"}, new String[]{"S_CG_PBWJGCP"}));
    //dsPbwjgwlTable.setSort(new SortDescriptor("", new String[]{"cpbm"}, new boolean[]{false}, null, 0));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(RECODE_ACCOUNT), new Recode_Account());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);

    addObactioner(String.valueOf(REPORT), new Approve());//报表追踪操作
    addObactioner(String.valueOf(PBQKMX_ADD), new pbwjgwl_Add_DEL());
    addObactioner(String.valueOf(PBQKMX_DEL), new pbwjgwl_Add_DEL());

    addObactioner(String.valueOf(WJGCP_ADD), new pbwjgcp_Add_DEL());
    addObactioner(String.valueOf(WJGCP_DEL), new pbwjgcp_Add_DEL());

    addObactioner(String.valueOf(WJGQK_ADD), new pbwjgqk_Add_DEL());
    addObactioner(String.valueOf(WJGQK_DEL), new pbwjgqk_Add_DEL());



    addObactioner(String.valueOf(SINGLE_IMPORT_ADD), new Single_Import_Add());//引入物料需求计划主表，从表数据全部过来

    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCLE_APPROVE), new Cancle_Approve());
    //addObactioner(String.valueOf(AUTO_MIN_PRICE), new Master_Buy_Price());//填入最低报价
    addObactioner(String.valueOf(COMPLETE), new Complete());//完成操作
    addObactioner(String.valueOf(SHOW_DETAIL), new ShowDetail());
     addObactioner(String.valueOf(ONCHANGE), new Onchange());
     addObactioner(String.valueOf(BONCHANGE), new bOnchange());
    addObactioner(String.valueOf(WB_ONCHANGE), new Wb_Onchange());//选择外币触发事件
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

    if(dsPbwjgwlTable != null){
      dsPbwjgwlTable.close();
      dsPbwjgwlTable = null;
    }
    if(dsPbwjgwlTable != null){
      dsPbwjgwlTable.close();
      dsPbwjgwlTable = null;
    }
    if(dsPbwjgwlTable != null){
      dsPbwjgwlTable.close();
      dsPbwjgwlTable = null;
    }

    log = null;
    m_RowInfo = null;

    Pbqkmx_Rowinfos = null;
    Pbqkmx_Rowinfos = null;
    Pbqkmx_Rowinfos = null;

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
    if(bdetailProducer != null)
    {
      bdetailProducer.release();
  bdetailProducer = null;
    }
    if(cdetailProducer != null)
    {
      cdetailProducer.release();
  cdetailProducer = null;
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
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否是主表
    if(isMaster){
      if(isInit)
        m_RowInfo.clear();
      if(!isAdd)
        m_RowInfo.put(getMaterTable());
      else
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        m_RowInfo.put("czrq", today);//制单日期
        m_RowInfo.put("czy", loginName);//操作员
        m_RowInfo.put("sqrq", today);//申请日期
        m_RowInfo.put("czyid", loginId);//操作员ID
        m_RowInfo.put("deptid", loginDept);//部门

      }
    }
        /*初始化主表行**/
    else
    {
      if(Pbqkmx_Rowinfos == null)
        Pbqkmx_Rowinfos = new ArrayList(dsPbwjgwlTable.getRowCount());
      else if(isInit)
        Pbqkmx_Rowinfos.clear();
      dsPbwjgwlTable.first();
      int hh = dsPbwjgwlTable.getRowCount();
      for(int i=0; i<dsPbwjgwlTable.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsPbwjgwlTable);
        Pbqkmx_Rowinfos.add(row);
        dsPbwjgwlTable.next();
      }
      //**********//
      if(Wjgqk_Rowinfos == null)
        Wjgqk_Rowinfos = new ArrayList(dspbwjgqkTable.getRowCount());
      else if(isInit)
        Wjgqk_Rowinfos.clear();
      dspbwjgqkTable.first();
      for(int i=0; i<dspbwjgqkTable.getRowCount(); i++)
      {
        RowMap row = new RowMap(dspbwjgqkTable);
        Wjgqk_Rowinfos.add(row);
        dspbwjgqkTable.next();
      }
      /**********/
      if(Wjgcp_Rowinfos == null)
        Wjgcp_Rowinfos = new ArrayList(dspbwjgcpTable.getRowCount());
      else if(isInit)
        Wjgcp_Rowinfos.clear();
      dspbwjgcpTable.first();
      for(int i=0; i<dspbwjgcpTable.getRowCount(); i++)
      {
        RowMap row = new RowMap(dspbwjgcpTable);
        Wjgcp_Rowinfos.add(row);
        dspbwjgcpTable.next();
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
    /**坯布情况明细表**/
    int rownum = Pbqkmx_Rowinfos.size();
    RowMap detailRow = null;

    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)Pbqkmx_Rowinfos.get(i);
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品id
      detailRow.put("bpsl", formatNumber(rowInfo.get("bpsl_"+i), qtyFormat));//数量
      detailRow.put("dj", formatNumber(rowInfo.get("dj_"+i), priceFormat));//单价
      detailRow.put("je", formatNumber(rowInfo.get("je0_"+i), sumFormat));//金额
      //detailRow.put("ybje", formatNumber(rowInfo.get("ybje_"+i), sumFormat));//原币金额
      detailRow.put("dwtxid", rowInfo.get("dwtxid_"+i));//单位通讯
      detailRow.put("jsfsid", rowInfo.get("bjsfsid_"+i));//结算方式id
      detailRow.put("grrq", rowInfo.get("grrq_"+i));//需求日期
      //detailRow.put("slrq", rowInfo.get("slrq_"+i));//需求日期
      detailRow.put("grsl", rowInfo.get("grsl_"+i));//狗入数量
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
    }
    /**外加工情况明细表**/
    rownum = Wjgqk_Rowinfos.size();
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)Wjgqk_Rowinfos.get(i);
String aa = rowInfo.get("bdwtxid_"+i);
      //detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品id
      detailRow.put("trsl", formatNumber(rowInfo.get("trsl_"+i), qtyFormat));//投入数量
      detailRow.put("ccsl", formatNumber(rowInfo.get("ccsl_"+i), qtyFormat));//产出数量
      detailRow.put("jgdj", formatNumber(rowInfo.get("jgdj_"+i), priceFormat));//加工单价
      detailRow.put("jgje", formatNumber(rowInfo.get("jgje_"+i), sumFormat));//金额
      //detailRow.put("ybje", formatNumber(rowInfo.get("ybje_"+i), sumFormat));//原币金额
      detailRow.put("dwtxid", rowInfo.get("bdwtxid_"+i));//单位通讯
      detailRow.put("wjggx", rowInfo.get("wjggx_"+i));//外加工工序
      detailRow.put("jldw", rowInfo.get("jldw5_"+i));//计量单位
      detailRow.put("yssl", rowInfo.get("yssl_"+i));//益损数量
      detailRow.put("ysl", rowInfo.get("ysl_"+i));//益损率
      detailRow.put("cpsl", rowInfo.get("cpsl_"+i));//次品数量
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//代码属性ID
      detailRow.put("jsfsid", rowInfo.get("jsfsid_"+i));//结算方式id
    }
    /**加工成品布料明细表**/
    rownum = Wjgcp_Rowinfos.size();

    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)Wjgcp_Rowinfos.get(i);
      String cc = rowInfo.get("bcpid_"+i);

      String sl = rowInfo.get("sl_"+i);
      detailRow.put("cpid", rowInfo.get("bcpid_"+i));//产品id
      //detailRow.put("kwid", rowInfo.get("kwid_"+i));
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//数量
      detailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+i), qtyFormat));//换算数量
      detailRow.put("jgwdj", formatNumber(rowInfo.get("jgwdj_"+i), priceFormat));//加工完单价
      String je = formatNumber(rowInfo.get("je2_"+i),sumFormat);
      detailRow.put("je", formatNumber(rowInfo.get("je2_"+i), sumFormat));//金额
      //detailRow.put("ybje", formatNumber(rowInfo.get("ybje_"+i), sumFormat));//原币金额
      //detailRow.put("dwtxid", rowInfo.get("dwtxid_"+i));//单位通讯
      //detailRow.put("grrq", rowInfo.get("grrq_"+i));//需求日期
      //detailRow.put("bz", rowInfo.get("bz_"+i));//备注
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//代码属性ID
    }


  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

   /*得到从表表对象*/
  public final EngineDataSet getPbqkmxTable(){
    return dsPbwjgwlTable;
  }
   /*得到从表表对象*/
  public final EngineDataSet getWjgqkTable(){
    return dspbwjgqkTable;
  }
   /*得到从表表对象*/
  public final EngineDataSet getWjgcpTable(){
    return dspbwjgcpTable;
  }
  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    //pbwjgdid = dsMasterTable.getValue("pbwjgdid");
    String sql = isMasterAdd ? "-1" : pbwjgdid;
    String SQL = combineSQL(Pbwjgwl_SQL, "?", new String[]{sql});
    dsPbwjgwlTable.setQueryString(SQL);
    if(dsPbwjgwlTable.isOpen())
      dsPbwjgwlTable.refresh();
    else
      dsPbwjgwlTable.open();
    /***/
    SQL = combineSQL(pbwjgqk_SQL, "?", new String[]{sql});
    dspbwjgqkTable.setQueryString(SQL);
    if(dspbwjgqkTable.isOpen())
      dspbwjgqkTable.refresh();
    else
      dspbwjgqkTable.open();
    /***/
    SQL = combineSQL(pbwjgcp_SQL, "?", new String[]{sql});
    dspbwjgcpTable.setQueryString(SQL);
    if(dspbwjgcpTable.isOpen())
      dspbwjgcpTable.refresh();
    else
      dspbwjgcpTable.open();
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表多列的信息*/
  public final RowMap[] getPbqkmxRowinfos() {
    int gg  =  Pbqkmx_Rowinfos.size();
    RowMap[] rows = new RowMap[Pbqkmx_Rowinfos.size()];
    Pbqkmx_Rowinfos.toArray(rows);
    return rows;
  }
  /*得到从表多列的信息*/
  public final RowMap[] getWjgqkRowinfos() {
    RowMap[] rows = new RowMap[Wjgqk_Rowinfos.size()];
    Wjgqk_Rowinfos.toArray(rows);
    return rows;
  }
  /*得到从表多列的信息*/
  public final RowMap[] getWjgcpRowinfos() {
    RowMap[] rows = new RowMap[Wjgcp_Rowinfos.size()];
    Wjgcp_Rowinfos.toArray(rows);
    return rows;
  }
  //重载getDetailRowinfos. 为了在bottom页面上取得cpid.
  public final RowMap[] getDetailRowinfos(String cpid) {
  RowMap[] rows = new RowMap[Pbqkmx_Rowinfos.size()];
  Pbqkmx_Rowinfos.toArray(rows);
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
      isDetailAdd =false;
      isMasterAdd = true;
      isRep = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      bdetailProducer.init(request, loginId);
      cdetailProducer.init(request, loginId);
      //初始化查询项目和内容
       RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("zt", "0");
      row.put("sqrq$a", startDay);
      row.put("sqrq$b", today);
      //初始化时清空数据集
      String SQL = " AND zt<>2";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
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
      isRep = false;
      isDetailAdd = false;
      isMasterAdd = String.valueOf(ADD).equals(action);
      if(!isMasterAdd)
      {
        isMasterAdd=false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        pbwjgdid = dsMasterTable.getValue("pbwjgdid");
      }
      else
        pbwjgdid =  dataSetProvider.getSequence("s_cg_pbwjgd");
        openDetailTable(isMasterAdd);
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
      boolean isReport = String.valueOf(REPORT).equals(action);
     String id = null;
   /*
     * 如果是报表追踪操作
   */
   if(isReport)
   {
     isRep = true;
     id = data.getParameter("id");
   }
   else {
     isApprove = true;
     id = data.getParameter("id", "");
   }
   HttpServletRequest request = data.getRequest();
   masterProducer.init(request, loginId);
   detailProducer.init(request, loginId);
   bdetailProducer.init(request, loginId);
   cdetailProducer.init(request, loginId);

      //得到request的参数,值若为null, 则用""代替


      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();
        pbwjgdid  = id;

      //打开从表
      openDetailTable(false);

      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
    }
  }

  //&#$
  /**
   * 添加到审核列表的操作类
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      String deptid = dsMasterTable.getValue("deptid");
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterTable.getValue("djh");
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "cloth_outprocess", content,deptid);
    }
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
   * 完成操作出发类
   * 手工完成操作，没有约束
   */
  class Complete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getMaterTable();
      int row = Integer.parseInt(data.getParameter("rownum"));
      ds.goToRow(row);
      ds.setValue("zt", "2");
      ds.post();
      ds.saveChanges();

    }
  }


  /**
 * 得到往来单位信息的bean
 * @param req WEB的请求
 * @return 返回往来单位信息的bean
 */
public LookUp getCorpBean(HttpServletRequest req)
{
  if(corpBean == null)
    corpBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_CORP);
  return corpBean;
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
     putDetailInfo(data.getRequest());
     String dwtxid = rowinfo.get("dwtxid");
     if(!oldDwtxid.equals(dwtxid)){
       RowMap corpRow = getCorpBean(req).getLookupRow(dwtxid);
       rowinfo.put("deptid", corpRow.get("deptid"));
       rowinfo.put("personid", corpRow.get("personid"));
       EngineDataSet detail = getPbqkmxTable();
       detail.first();
       while(detail.inBounds())
       {
         String pbwjgqkID = detail.getValue("pbwjgqkID");
         if(!pbwjgqkID.equals(""))
         {
           Pbqkmx_Rowinfos.remove(detail.getRow());
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
*/
class bOnchange implements Obactioner
{
 public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
 {
   HttpServletRequest req = data.getRequest();
   EngineDataSet ds = getMaterTable();
   RowMap rowinfo = getMasterRowinfo();
   String oldDwtxid = rowinfo.get("dwtxid");
   putDetailInfo(data.getRequest());
   String dwtxid = rowinfo.get("dwtxid");
   if(!oldDwtxid.equals(dwtxid)){
     RowMap corpRow = getCorpBean(req).getLookupRow(dwtxid);
     rowinfo.put("deptid", corpRow.get("deptid"));
     rowinfo.put("personid", corpRow.get("personid"));
     EngineDataSet detail = getWjgqkTable();
     detail.first();
     while(detail.inBounds())
     {
       String pbwjgwlID = detail.getValue("pbwjgwlID");
       if(!pbwjgwlID.equals(""))
       {
         Wjgqk_Rowinfos.remove(detail.getRow());
         detail.deleteRow();
       }
       else
         detail.next();
     }
     data.setMessage(showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_1');"));
   }

   else
     {
     data.setMessage(showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_1');"));
     return;
     }

 }

  }
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
      for(int j=0; j<Pbqkmx_Rowinfos.size(); j++)
      {
        RowMap detailrow = (RowMap)Pbqkmx_Rowinfos.get(j);
        String je = detailrow.get("je");
        double curje = isDouble(je) ? Double.parseDouble(je) : 0 ;
        // detailrow.put("ybje", formatNumber(curhl==0 ? "" : String.valueOf(curje/curhl),qtyFormat));
      }
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
     approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "cloth_outprocess");
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
      if(isMasterAdd){
        ds.insertRow(false);
        pbwjgdid = dataSetProvider.getSequence("s_cg_pbwjgd");
        ds.setValue("pbwjgdid", pbwjgdid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("czyid", loginId);
        ds.setValue("czy", loginName);//操作员
        isMasterAdd=false;
      }
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);
      /**坯布物了情况明细表**/
      int rowcount = dsPbwjgwlTable.getRowCount();
      int rows = Pbqkmx_Rowinfos.size();
      dsPbwjgwlTable.first();
      for(int i=0; i<dsPbwjgwlTable.getRowCount(); i++)
      {
      dsPbwjgwlTable.setValue("pbwjgdid", pbwjgdid);
      detailrow = (RowMap)Pbqkmx_Rowinfos.get(i);
      double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(detailrow.get("dj")) : 0;//单价
      double bpsl = detailrow.get("bpsl").length() > 0 ? Double.parseDouble(detailrow.get("bpsl")) : 0;//产品数量
      double je = detailrow.get("je").length() > 0 ? Double.parseDouble(detailrow.get("je")) : 0;//金额
      dsPbwjgwlTable.setValue("dj", detailrow.get("dj"));//
      dsPbwjgwlTable.setValue("bpsl", detailrow.get("bpsl"));
      dsPbwjgwlTable.setValue("je", String.valueOf(bpsl*(je/bpsl)));
      dsPbwjgwlTable.setValue("grrq", detailrow.get("grrq"));//需求日期
      dsPbwjgwlTable.setValue("cpid", detailrow.get("cpid"));//
      dsPbwjgwlTable.setValue("grsl", detailrow.get("grsl"));
      dsPbwjgwlTable.setValue("dwtxid", detailrow.get("dwtxid"));
      dsPbwjgwlTable.setValue("jsfsid", detailrow.get("jsfsid"));//结算方式id
      dsPbwjgwlTable.post();
      //String s= dsPbwjgwlTable.getValue("je");
      //totalNum = totalNum.add(dsPbwjgwlTable.getBigDecimal("sl"));
      //totalSum = totalSum.add(dsPbwjgwlTable.getBigDecimal("je"));
      dsPbwjgwlTable.next();
      }
      /**外加工情况明细表**/
       int rowcount2 = dspbwjgqkTable.getRowCount();
       int rows2 = Wjgqk_Rowinfos.size();
       dspbwjgqkTable.first();
      for(int i=0; i<dspbwjgqkTable.getRowCount(); i++)
    {
      dspbwjgqkTable.setValue("pbwjgdid", pbwjgdid);
      detailrow = (RowMap)Wjgqk_Rowinfos.get(i);
      double jgdj = detailrow.get("jgdj").length() > 0 ? Double.parseDouble(detailrow.get("jgdj")) : 0;//单价
      double trsl = detailrow.get("trsl").length() > 0 ? Double.parseDouble(detailrow.get("trsl")) : 0;//产品数量
      double cpsl = detailrow.get("cpsl").length() > 0 ? Double.parseDouble(detailrow.get("cpsl")) : 0;//产品数量
      double ccsl = detailrow.get("ccsl").length() > 0 ? Double.parseDouble(detailrow.get("ccsl")) : 0;
      double yssl = detailrow.get("yssl").length() > 0 ? Double.parseDouble(detailrow.get("yssl")) : 0;
      double ysl = detailrow.get("ysl").length() > 0 ? Double.parseDouble(detailrow.get("ysl")) : 0;
      double jgje = detailrow.get("jgje").length() > 0 ? Double.parseDouble(detailrow.get("jgje")) : 0;//加工金额
      String aa=detailrow.get("jldw");
      dspbwjgqkTable.setValue("jgdj", detailrow.get("jgdj"));
      dspbwjgqkTable.setValue("trsl", detailrow.get("trsl"));
      dspbwjgqkTable.setValue("ccsl", detailrow.get("ccsl"));
      dspbwjgqkTable.setValue("yssl", detailrow.get("yssl"));
      dspbwjgqkTable.setValue("cpsl", detailrow.get("cpsl"));
      dspbwjgqkTable.setValue("jldw", detailrow.get("jldw"));
      dspbwjgqkTable.setValue("wjggx", detailrow.get("wjggx"));
      dspbwjgqkTable.setValue("ysl", detailrow.get("ysl"));
      dspbwjgqkTable.setValue("dwtxid", detailrow.get("dwtxid"));
      String dsa=detailrow.get("jsfsid");
      dspbwjgqkTable.setValue("jsfsid", detailrow.get("jsfsid"));//结算方式id
      dspbwjgqkTable.setValue("jgje", String.valueOf(ccsl*(jgje/ccsl)));
      //dsPbwjgwlTable.setValue("dmsxid", detailrow.get("dmsxid"));
      //dspbwjgqkTable.setValue("cpid", detailrow.get("cpid"));

      dspbwjgqkTable.post();
      dspbwjgqkTable.next();
      }
      /**加工成品布料明细表**/

      int rowcount3 = dspbwjgcpTable.getRowCount();
      int rows3 = Wjgcp_Rowinfos.size();
      dspbwjgcpTable.first();

      for(int i=0; i<dspbwjgcpTable.getRowCount(); i++)
      {



        dspbwjgcpTable.setValue("pbwjgdid",pbwjgdid);

        detailrow = (RowMap)Wjgcp_Rowinfos.get(i);

        double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;//换算数量
        double jgwdj = detailrow.get("jgwdj").length() > 0 ? Double.parseDouble(detailrow.get("jgwdj")) : 0;//加工完单价
        double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;//产品数量
        double je = detailrow.get("je").length() > 0 ? Double.parseDouble(detailrow.get("je")) : 0;//金额
        dspbwjgcpTable.setValue("jgwdj", detailrow.get("jgwdj"));//
        String aa=detailrow.get("cpid");
        dspbwjgcpTable.setValue("cpid", detailrow.get("cpid"));//
         //dspbwjgcpTable.setValue("kwid", detailrow.get("kwid"));
        dspbwjgcpTable.setValue("sl", detailrow.get("sl"));//
         dspbwjgcpTable.setValue("hssl", detailrow.get("hssl"));//保存换算数量
        dspbwjgcpTable.setValue("dmsxid", detailrow.get("dmsxid"));
        dspbwjgcpTable.setValue("je", String.valueOf(sl*(je/sl)));
        dspbwjgcpTable.post();
        dspbwjgcpTable.next();
      }
      //保存主表数据
      //ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      String sqrq = rowInfo.get("slrq");
      String ddrq = rowInfo.get("ddrq");


      ds.setValue("deptid", rowInfo.get("deptid"));//部门ID
      ds.setValue("storeid", rowInfo.get("storeid"));//仓库id
      ds.setValue("jbr", rowInfo.get("jbr"));//经手人
      ds.setValue("bz", rowInfo.get("bz"));//备注
      ds.setValue("slrq", rowInfo.get("slrq"));//收料日期
      ds.setValue("ddrq", rowInfo.get("ddrq"));//申请日期
      ds.setValue("khlx", rowInfo.get("khlx"));//客户类型
      //ds.setValue("cgyy", rowInfo.get("cgyy"));//采购原因
      //ds.setValue("zsl", totalNum.toString());//总数量
      //ds.setValue("zje", totalSum.toString());//总金额

      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, dsPbwjgwlTable,dspbwjgqkTable,dspbwjgcpTable}, null);
      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        dsPbwjgwlTable.empty();
        dspbwjgqkTable.empty();
        dspbwjgcpTable.empty();
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
      if(Pbqkmx_Rowinfos.size()<1&&Wjgqk_Rowinfos.size()<1&&Wjgcp_Rowinfos.size()<1)
        return showJavaScript("alert('不能保存空数据')");

      //java.sql.Date sqrq = java.sql.Date.valueOf(getMasterRowinfo().get("sqrq"));
      if(Pbqkmx_Rowinfos.size()>=1)
      {
        ArrayList list = new ArrayList(Pbqkmx_Rowinfos.size());
        for(int i=0; i<Pbqkmx_Rowinfos.size(); i++)
        {

          int row = i+1;
          detailrow = (RowMap)Pbqkmx_Rowinfos.get(i);
          String cpid = detailrow.get("cpid");
          String jsfsid = detailrow.get("jsfsid");
          String dmsxid = detailrow.get("dmsxid");
          String dwtxid = detailrow.get("dwtxid");
          String wlxqjhmxid = detailrow.get("wlxqjhmxid");
          StringBuffer buf = new StringBuffer().append(wlxqjhmxid).append(",").append(cpid).append(",").append(dmsxid).append(",").append(dwtxid);
          String cpiddmsxid = buf.toString();
          if(cpid.equals(""))
            return showJavaScript("alert('第"+row+"行产品不能为空');");
          if(list.contains(cpiddmsxid))
            return showJavaScript("alert('第"+row+"行产品重复');");
          else
            list.add(cpiddmsxid);
          String bpsl = detailrow.get("bpsl");
        if(bpsl.equals(""))
          return showJavaScript("alert('第"+row+"行数量不能为空！');");
        if((temp = checkNumber(bpsl, "第"+row+"行数量")) != null)
          return temp;
        if(bpsl.length()>0 && bpsl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");

        String dj = detailrow.get("dj");
        if(dj.equals(""))
        return showJavaScript("alert('第"+row+"行单价不能为空！');");
        if(dj.length()>0 && (temp = checkNumber(dj, "第"+row+"行单价")) != null)
          return temp;
        temp = detailrow.get("grrq");
        if(temp.equals(""))
          return showJavaScript("alert('第"+row+"行需求日期不能为空！');");
        if(!isDate(temp))
          return showJavaScript("alert('第"+row+"行非法需求日期！');");
        temp = detailrow.get("dwtxid");
        if(temp.equals(""))
          return showJavaScript("alert('第"+row+"行单位不能为空！');");
        if(jsfsid.equals(""))
          return showJavaScript("alert('第"+row+"行结算方式不能为空！');");
        //java.sql.Date ggrq = java.sql.Date.valueOf(temp);
        // if(grrq.before(sqrq))
        //  return showJavaScript("alert('第"+row+"行需求日期不能小于申请日期！');");
      }
      }

      if(Wjgqk_Rowinfos.size()>=1)
      {
      ArrayList list = new ArrayList(Wjgqk_Rowinfos.size());
      for(int i=0; i<Wjgqk_Rowinfos.size(); i++)
      {

        int row = i+1;
        detailrow = (RowMap)Wjgqk_Rowinfos.get(i);
        String dwtxid = detailrow.get("dwtxid");
        String bjsfsid = detailrow.get("jsfsid");
        temp=detailrow.get("jgdj");
        if(temp.equals(""))
        return showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_1');"+"alert('第"+row+"行单价不能为空！');");
        temp = detailrow.get("dwtxid");
        if(temp.equals(""))
         //return showJavaScript("alert('第"+row+"行单位不能为空！');");
        return showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_1');"+"alert('第"+row+"行单位不能为空！');");
        if(bjsfsid.equals(""))
        return showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_1');"+"alert('第"+row+"行结算方式不能为空！');");

      }
      }

         if(Wjgcp_Rowinfos.size()>=1)
        {
        ArrayList list = new ArrayList(Wjgcp_Rowinfos.size());
        for(int i=0; i<Wjgcp_Rowinfos.size(); i++)
        {


          int row = i+1;

          detailrow = (RowMap)Wjgcp_Rowinfos.get(i);
          String cpid = detailrow.get("cpid");
          String dmsxid = detailrow.get("dmsxid");
          String dwtxid = detailrow.get("dwtxid");
         String wlxqjhmxid = detailrow.get("wlxqjhmxid");
         temp = detailrow.get("jgwdj");
         if(temp.equals(""))
         return showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_2');"+"alert('第"+row+"行单价不能为空！');");
         StringBuffer buf = new StringBuffer().append(wlxqjhmxid).append(",").append(cpid).append(",").append(dmsxid).append(",").append(dwtxid);
         String cpiddmsxid = buf.toString();
         if(cpid.equals(""))
           return showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_2');"+"alert('第"+row+"行产品不能为空！');");
         if(list.contains(cpiddmsxid))
           return showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_2');"+"alert('第"+row+"行产品不能重复！');");
         else
           list.add(cpiddmsxid);






          if(cpid.equals(""))
          //  return showJavaScript("alert('第"+row+"行产品不能为空');");
          return showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_2');"+"alert('第"+row+"行产品不能为空！');");
      }
      }

     if(Pbqkmx_Rowinfos.size()<1||Wjgqk_Rowinfos.size()<1||Wjgcp_Rowinfos.size()<1)
        return showJavaScript("alert('加工情况不完整')");

      return null;

  }
    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("slrq");
      if(temp.equals(""))
        return showJavaScript("alert('申请日期不能为空！');");
      if(!isDate(temp))
        return showJavaScript("alert('非法申请日期！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('部门不能为空！');");
      temp = rowInfo.get("storeid");
       if(temp.equals(""))
         return showJavaScript("alert('仓库不能为空！');");
      temp = rowInfo.get("khlx");
      if(temp.equals(""))
         return showJavaScript("alert('客户类型不能为空！');");

      //temp = rowInfo.get("cgyy");
      //if(temp.getBytes().length > getMaterTable().getColumn("cgyy").getPrecision())
      //return showJavaScript("alert('您输入的采购原因的内容太长了！');");
      return null;
    }
  }

   class Master_Delete implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       EngineDataSet ds = getMaterTable();
     //在主从明细里执行删除操作.
       if(isMasterAdd){
         data.setMessage(showJavaScript("backList();"));//主从表新增,还未保存时,
         return;
       }
       dsMasterTable.goToInternalRow(masterRow);
       if(dsPbwjgwlTable.isOpen())
         dsPbwjgwlTable.close();
       String pbwjgdID = dsMasterTable.getValue("pbwjgdID");
       setDataSetProperty(dsPbwjgwlTable,YPGZJL_SQL + "'"+pbwjgdID+"'");
       dsPbwjgwlTable.open();
       while(dsPbwjgwlTable.getRowCount()>0)
       {
         dsPbwjgwlTable.first();
         dsPbwjgwlTable.deleteRow();
         dsPbwjgwlTable.post();
         dsPbwjgwlTable.saveChanges();
       }
       if(dspbwjgqkTable.isOpen())
         dspbwjgqkTable.close();
       //String ypxxID = dsMasterTable.getValue("ypxxID");
       setDataSetProperty(dspbwjgqkTable,YPJYQK_SQL + "'"+pbwjgdID+"'");
       dspbwjgqkTable.open();
       while(dspbwjgqkTable.getRowCount()>0)
       {
         dspbwjgqkTable.first();
         dspbwjgqkTable.deleteRow();
         dspbwjgqkTable.post();
         dspbwjgqkTable.saveChanges();
       }
       if(dspbwjgcpTable.isOpen())
         dspbwjgcpTable.close();
       //String ypxxID = dsMasterTable.getValue("ypxxID");
       setDataSetProperty(dspbwjgcpTable,MSXX_SQL + "'"+pbwjgdID+"'");
       dspbwjgcpTable.open();
       while(dspbwjgcpTable.getRowCount()>0)
       {
         dspbwjgcpTable.first();
         dspbwjgcpTable.deleteRow();
         dspbwjgcpTable.post();
         dspbwjgcpTable.saveChanges();
       }
       ds.deleteRow();
       ds.post();
       ds.saveChanges();
       ds.refresh();
data.setMessage(showJavaScript("backList();"));
     }
  }














  /**
   * 主表删除操作
   */
/*  class Master_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(isMasterAdd){
        data.setMessage(showJavaScript("backList();"));
        return;
      }
      EngineDataSet ds = getMaterTable();
      ds.goToInternalRow(masterRow);
      dsPbwjgwlTable.deleteAllRows();
      dspbwjgqkTable.deleteAllRows();
      dspbwjgcpTable.deleteAllRows();

      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsPbwjgwlTable}, null);
      ds.saveDataSets(new EngineDataSet[]{ds, dspbwjgqkTable}, null);
      ds.saveDataSets(new EngineDataSet[]{ds, dspbwjgcpTable}, null);
      //
      Pbqkmx_Rowinfos.clear();
      Wjgqk_Rowinfos.clear();
      Wjgcp_Rowinfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }
*/

  class Search implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<cardID >notifyObactioners</cardID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      masterProducer.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = masterProducer.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
        SQL = " and "+SQL;
      SQL=combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      System.out.print(SQL);
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
    }
  }
  /**
   *计账操作
   * */
  class Recode_Account implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
      //是否有符合记帐的数据，有几条
      String SQL = combineSQL(RECODE_DATASQL, "?", new String[]{loginId});
      String UPDATE_SQL = combineSQL(RECODE, "?", new String[]{loginId});
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
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
      //EngineDataSet detail = dsPbwjgwlTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("djh"), null, null, null),
        new QueryColumn(master.getColumn("ddrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("ddrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("jbr"), null, null, null,null,"like"),
        new QueryColumn(master.getColumn("pbwjgdid"), "cg_pbwjgcp", "pbwjgdid", "cpid", null, "="),//从表产品id
        new QueryColumn(master.getColumn("pbwjgdid"), "VW_cg_pbwjgdHW", "pbwjgdid", "cpbm", "cpbm", "left_like"),//从表品名规格
        new QueryColumn(master.getColumn("pbwjgdid"), "VW_cg_pbwjgdHW", "pbwjgdid", "product", "product", "like"),//从表产品编码
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("pbwjgdid"), "VW_cg_pbwjgdHW", "pbwjgdid", "sxz", "sxz", "like")
      });
      isInitQuery = true;
    }
  }

  /**
   *  选择物料需求计划主表从表数据全部引入操作
   */

  class Single_Import_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String singleImportMrp = m_RowInfo.get("singleImportMrp");
      if(singleImportMrp.equals(""))
        return;
      String SQL = MRP_DETAIL_SQL+singleImportMrp;
      EngineDataSet tempMrpData = null;
      if(tempMrpData==null)
      {
        tempMrpData = new EngineDataSet();
        setDataSetProperty(tempMrpData,null);
      }
      tempMrpData.setQueryString(SQL);
      if(!tempMrpData.isOpen())
        tempMrpData.openDataSet();
      else
        tempMrpData.refresh();
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsPbwjgwlTable, "wlxqjhmxid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String pbwjgdid = dsMasterTable.getValue("pbwjgdid");
      for(int i=0; i<tempMrpData.getRowCount(); i++)
      {
        tempMrpData.goToRow(i);
        if(!tempMrpData.getValue("chxz").equals("2"))
          continue;
        String wlxqjhmxid = tempMrpData.getValue("wlxqjhmxid");
        String cpid = tempMrpData.getValue("cpid");
        locateGoodsRow.setValue(0, wlxqjhmxid);
        if(!dsPbwjgwlTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap prodRow = getProductBean(req).getLookupRow(cpid);
          String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
          double xgl = tempMrpData.getValue("xgl").length()>0 ? Double.parseDouble(tempMrpData.getValue("xgl")) : 0;//需求计划中需要采购的数量
          double hsbl = prodRow.get("hsbl").length() > 0 ? Double.parseDouble(prodRow.get("hsbl")) : 0;//换算比例
          double ygl = tempMrpData.getValue("ygl").length()>0 ? Double.parseDouble(tempMrpData.getValue("ygl")) : 0;//需求计划中已经采购的数量
          double wgl = xgl-ygl>0 ? xgl-ygl : 0;
          if(wgl==0)
            continue;
          dsPbwjgwlTable.insertRow(false);
          dsPbwjgwlTable.setValue("cgsqdhwid", "-1");
          dsPbwjgwlTable.setValue("wlxqjhmxid",wlxqjhmxid);
          dsPbwjgwlTable.setValue("cpid", cpid);
          dsPbwjgwlTable.setValue("sl", bjfs.equals("1") ? String.valueOf(hsbl==0 ? 0 : wgl/hsbl) : String.valueOf(wgl));
          dsPbwjgwlTable.setValue("xqrq", tempMrpData.getValue("xqrq").equals("") ? today : tempMrpData.getValue("xqrq"));
          dsPbwjgwlTable.setValue("bz", tempMrpData.getValue("bz"));
          dsPbwjgwlTable.setValue("dmsxid", tempMrpData.getValue("dmsxid"));
          dsPbwjgwlTable.setValue("pbwjgdid", isMasterAdd ? "" : pbwjgdid);
          dsPbwjgwlTable.post();
          //创建一个与用户相对应的行
          RowMap detailrow = new RowMap(dsPbwjgwlTable);
          Pbqkmx_Rowinfos.add(detailrow);
        }
      }
    }
  }
  /**
   * 坯布情况明细表
   */
  class pbwjgwl_Add_DEL implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      if(String.valueOf(PBQKMX_ADD).equals(action))
      {
        dsPbwjgwlTable.insertRow(false);
        dsPbwjgwlTable.setValue("pbwjgdid", pbwjgdid);
        dsPbwjgwlTable.post();
        RowMap detailrow = new RowMap(dsPbwjgwlTable);
        Pbqkmx_Rowinfos.add(detailrow);
      }else
      {
        int rownum = Integer.parseInt(data.getParameter("rownum"));
        Pbqkmx_Rowinfos.remove(rownum);
        dsPbwjgwlTable.goToRow(rownum);
        dsPbwjgwlTable.deleteRow();
      }
    }
  }
  /**外加工情况明细表**/
  class pbwjgqk_Add_DEL implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     putDetailInfo(data.getRequest());
     if(String.valueOf(WJGQK_ADD).equals(action))
     {
       dspbwjgqkTable.insertRow(false);
       dspbwjgqkTable.setValue("pbwjgdid", pbwjgdid);
       dspbwjgqkTable.post();
       RowMap detailrow = new RowMap(dspbwjgqkTable);
       Wjgqk_Rowinfos.add(detailrow);
     }else
     {
       int rownum = Integer.parseInt(data.getParameter("rownum"));
       Wjgqk_Rowinfos.remove(rownum);
       dspbwjgqkTable.goToRow(rownum);
       dspbwjgqkTable.deleteRow();
     }
     data.setMessage(showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_1');"));
   }
  }
  /**加工成品布料明细表**/
  class pbwjgcp_Add_DEL implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     putDetailInfo(data.getRequest());
     if(String.valueOf(WJGCP_ADD).equals(action))
     {
       dspbwjgcpTable.insertRow(false);
       dspbwjgcpTable.setValue("pbwjgdid", pbwjgdid);
       dspbwjgcpTable.post();
       RowMap detailrow = new RowMap(dspbwjgcpTable);
       Wjgcp_Rowinfos.add(detailrow);
     }else
     {
       int rownum = Integer.parseInt(data.getParameter("rownum"));
       Wjgcp_Rowinfos.remove(rownum);
       dspbwjgcpTable.goToRow(rownum);
       dspbwjgcpTable.deleteRow();
     }
     data.setMessage(showJavaScript("SetActiveTab(INFO_EX,'INFO_EX_2');"));
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
       pbwjgdid = dsMasterTable.getValue("pbwjgdid");
       //打开从表
       openDetailTable(false);
     }
  }
  /**
   *  自动填入最低供应及报价

  class Master_Buy_Price implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      if(dsBuyPrice == null)
      {
        dsBuyPrice = new EngineDataSet();
        setDataSetProperty(dsBuyPrice, null);
      }

      //EngineRow priceRow = new EngineRow(dsBuyPrice, "cpid");
      RowMap detail = null;
      boolean isCanRework = true; //在取消审批实从表一行纪录是否被合同引用
      String zt = m_RowInfo.get("zt");
      String hl = m_RowInfo.get("hl");
      double d_hl = hl.length()>0 ? Double.parseDouble(hl) : 0;
      for(int i=0; i<Pbqkmx_Rowinfos.size(); i++)
      {
        detail = (RowMap)Pbqkmx_Rowinfos.get(i);
        if(zt.equals("0"))
          isCanRework = engine.erp.baseinfo.BasePublicClass.isRevamp(dsPbwjgwlTable, "skhtl", i);
        if(!isCanRework)
          continue;//如果该纪录被合同引用供应商将不能改
       // String SQL = combineSQL(BUY_PRICE_SQL, "?", new String[]{detail.get("cpid")});
       // dsBuyPrice.setQueryString(SQL);
       // if(dsBuyPrice.isOpen())
         // dsBuyPrice.refresh();
       // else
         // dsBuyPrice.open();

        String dwtxid = dsBuyPrice.getValue("dwtxid");
        String dj = dsBuyPrice.getValue("bj");
        detail.put("dwtxid", dwtxid);
        detail.put("dj", dj);
        double sl = detail.get("sl").length()>0 ? Double.parseDouble(detail.get("sl")) : 0;
        double djVal = dj.length()>0 ? Double.parseDouble(dj) : 0;
        //detail.put("je",formatNumber(String.valueOf(sl*djVal), sumFormat));
        //if(d_hl!=0)
        // detail.put("ybje", formatNumber(String.valueOf(sl*djVal/d_hl), sumFormat));
      }
      /**
       dsPbwjgwlTable.first();
       for(int i=0; i<dsPbwjgwlTable.getRowCount(); i++)
       {
       String SQL = combineSQL(BUY_PRICE_SQL, "?", new String[]{dsPbwjgwlTable.getValue("cpid")});
       dsBuyPrice.setQueryString(SQL);
       if(dsBuyPrice.isOpen())
          dsBuyPrice.refresh();
        else
          dsBuyPrice.open();

        String dwtxid = dsBuyPrice.getValue("dwtxid");
        String dj = dsBuyPrice.getValue("bj");
        dsPbwjgwlTable.setValue("dwtxid", dwtxid);
        dsPbwjgwlTable.setValue("dj", dj);
        detail = (RowMap)Pbqkmx_Rowinfos.get(dsPbwjgwlTable.getRow());
        double sl = detail.get("sl").length()>0 ? Double.parseDouble(detail.get("sl")) : 0;
        double djVal = dj.length()>0 ? Double.parseDouble(dj) : 0;
        dsPbwjgwlTable.setValue("je",String.valueOf(sl*djVal));
        dsPbwjgwlTable.post();
        detail.put("dwtxid", dwtxid);
        detail.put("dj", dj);
        detail.put("je", formatNumber(String.valueOf(sl*djVal), sumFormat));
        // }
        dsPbwjgwlTable.next();
      }
      dsPbwjgwlTable.post();

    }
  }
        */
    /**
     * 得到用于查找产品单价的bean
     * @param req WEB的请求
     * @return 返回用于查找产品单价的bean
     */
    public LookUp getProductBean(HttpServletRequest req)
    {
      if(productBean == null)
        productBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PRODUCT);
      return productBean;
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
}


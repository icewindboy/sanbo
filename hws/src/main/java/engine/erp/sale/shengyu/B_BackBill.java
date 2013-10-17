package engine.erp.sale.shengyu;

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
import java.util.*;
import java.text.*;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
import engine.report.util.ReportData;
/**
 * <p>Title: 销售管理--销售货物管理--退货单管理--</p>
 * <p>Description: 销售管理--销售货物管理--退货单管理<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class B_BackBill extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL     = "1001";
  public  static final String DETAIL_SALE_ADD = "1002";
  public  static final String TD_RETURN_ADD   = "1003";//提单退货新增
  public  static final String CANCER_APPROVE  = "1004";
  public  static final String LADING_OVER     = "1005";//完成
  public  static final String LADING_CANCER   = "1006";//作废
  public  static final String DWTXID_CHANGE   = "1007";
  public  static final String IMPORT_ORDER    = "1008";//引入合同(主表)
  public  static final String DETAIL_CHANGE   = "1009";//从表操作
  public  static final String PROMOTION_PRODUCT_ADD  = "1010";//
  public  static final String PRODUCT_ADD         = "1011";  //引入合同货物
  public  static final String REPORT              ="645355666";
  public  static final String WRAPPER_PRINT       ="1012";
  public  static final String DETAIL_COPY         ="1013";//复制当前选中行
  public  static final String DEL_NULL            = "1014";            //删除数量为空的行
  public  static final String LADING_OUT          = "1015";//出库确认
  public  static final String LADING_OUT_TWO          = "1111015";//出库确认
  public  static final String MASTER_ADD          = "1018";
  public  static final String APPROVED_MASTER_ADD = "1019";//审批后的保存
  public  static final String LADDING_CANCER      = "1020";     //提单作废
  public  static final String CYQK_ADD            = "1021";           //承运情况新增
  public  static final String QTFY_ADD            = "1022";           //承运情况新增
  public  static final String CYQK_DEL            = "1023";           //承运情况删除
  public  static final String QTFY_DEL            = "1024";           //承运情况删除
  public  static final String THLX_CHANGE         = "1025";
  public  static final String IMPORT_PROMOTION    = "1026";


  private static final String MASTER_STRUT_SQL = "SELECT * FROM xs_td WHERE 1<>1  ";
  private static final String MASTER_SQL    = "SELECT * FROM xs_td WHERE djlx=-1 AND ? AND fgsid=? ?  order by djlx desc,tdbh desc";
  private static final String MASTER_EDIT_SQL       = "SELECT * FROM xs_td WHERE tdid='?' ";
  private static final String MASTER_SUM_SQL    = "SELECT SUM(nvl(zsl,0))zsl FROM xs_td WHERE djlx=-1 AND ? AND fgsid=? ?  ";
  private static final String MASTER_JE_SQL    = "SELECT SUM(nvl(zje,0))zje FROM xs_td WHERE djlx=-1 AND ? AND fgsid=? ?  ";
  private static final String SALEABLE_PRODUCT_SQL = " SELECT * FROM vw_lading_sel_product WHERE cpid=? and (storeid is null or storeid= ? )";//可销产品

  private static final String DETAIL_STRUT_SQL      = "SELECT * FROM xs_tdhw WHERE 1<>1 ";
  private static final String DETAIL_SQL            = "SELECT a.* FROM xs_tdhw a,kc_dm b WHERE tdid='?' and a.cpid=b.cpid order by b.cpbm";//
  private static final String XS_TDCYDK_STRUT_SQL   = "SELECT * FROM xs_tdcyqk WHERE 1<>1 "; //承运情况
  private static final String XS_TDCYDK_SQL         = "SELECT * FROM xs_tdcyqk WHERE tdid= ";     //

  public  static final String DETAIL_MULTI_ADD    = "258369";
  private static final String CUST_GRADE_SQL
      = "SELECT b.xydj FROM xs_khxyed b WHERE b.fgsid='?' AND b.dwtxid='?'";
  private static final String CUST_SALE_GOODS_STORE_SQL =
  " SELECT t.*, "
  +"       decode(nvl(z.djlx, nvl(y.djlx, t.mrjg)), 'ccj', t.ccj, 'msj', t.msj, 'lsj', t.lsj, 'qtjg1', t.qtjg1, 'qtjg2', t.qtjg2, 'qtjg3', t.qtjg3, NULL) price,"
  +"            nvl(z.zk, nvl(y.zk, t.oldzk)) mrzk, h.kcsl, p.sdsl, (nvl(h.kcsl,0)-nvl(p.sdsl,0)) kckgl "
  +" FROM "
  +" ("
  +" SELECT t.*, nvl(t.storeid,?) newstoreid FROM vw_xs_wzdj t WHERE t.fgsid='?' AND (t.storeid IS NULL OR t.storeid='?')  "
  +" UNION  "
  +" SELECT t.*, ? FROM (SELECT * FROM vw_xs_wzdj t WHERE t.fgsid='?' AND (t.storeid IS NULL OR t.storeid='?') ) t,(SELECT DISTINCT k.cpid FROM kc_wzmx k WHERE (k.zl<>0 OR k.hszl<>0) AND k.storeid='?' )k   "
  +" WHERE t.cpid=k.cpid "
  +" ) t, ("
  +" SELECT z.cpid, z.djlx, z.zk FROM xs_khcpzk z WHERE z.fgsid='?' AND z.dwtxid='?'"
  +" ) z,  kc_kchz h, vw_product_lock p, ( SELECT a.cplx, a.zk, a.djlx FROM xs_khtyzk a WHERE a.xydj='?') y "
  +" WHERE t.abc=y.cplx(+) AND t.cpid=z.cpid(+) AND t.newstoreid=h.storeid(+) AND t.cpid=h.cpid(+) "
  +"       AND t.newstoreid=p.storeid(+) AND t.cpid=p.cpid(+)  "
  +" ORDER BY t.cpbm";


  //private static final String XS_TDQTFY_STRUT_SQL   = "SELECT * FROM xs_tdqtfy WHERE 1<>1 ";//其他费用
  //private static final String XS_TDQTFY_SQL         = "SELECT * FROM xs_tdqtfy WHERE tdid= ";//
  public static final String ORDER_DETAIL_SQL ="SELECT * FROM VW_SALE_IMPORT_TD_ORDER_DETAIL WHERE nvl(sl,0)>0 and lb=2 and htid= ";//提货单根据HTID引入相应合同的明细
  public static final String TH_DETAIL_SQL = "SELECT * FROM VW_SALE_IMPORT_TH_ORDER_DETAIL WHERE lb=1 and  htid= ";//退货单引入合同货物
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM xs_td WHERE tdid='?'";

  private static final String CAN_OVER_SQL = "select count(*) from xs_tdhw a where nvl(a.sl,0)>nvl(a.stsl,0) and a.tdid=";//
  private static final String CAN_CANCER_SQL = "select count(*) from xs_tdhw a where nvl(a.stsl,0)>0 and a.tdid=";//
  private static final String REFERENCED_SQL        = "select count(*) from xs_tdhw a where nvl(a.sl,0)>nvl(a.stsl,0) and a.tdid=";//

  private static final String SEARCH_SQL = "SELECT * FROM VW_SALE_LADING_PRODUCT WHERE 1=1 ? ";//提货单主从表查询
  private static final String KHCPZK_SQL           = "SELECT * FROM xs_khcpzk where dwtxid=? and cpid=? ";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsMasterList  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsTdcyqkTable  = new EngineDataSet();//承运情况
  //private EngineDataSet dsTdqtfyTable  = new EngineDataSet();//其他费用
  private EngineDataSet dsSearchTable  = new EngineDataSet();//查询用到的数据集
  private EngineDataSet hthwmxTable      = new EngineDataSet();//合同货物明细
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "xs_td.1");
  public  HtmlTableProducer masterListProducer = new HtmlTableProducer(dsMasterList, "xs_td.1");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "xs_tdhw");
  public  boolean isApprove = false;     //是否在审批状态
  private boolean isMasterAdd = true;    //是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用

  private ArrayList d_RowInfos = null;         //从表多行记录的引用
  private ArrayList d_TdcyqkRowInfos = null;   //承运情况
  //private ArrayList d_TdqtfyRowInfos = null;   //其他费用

  private ArrayList drows=null;
  private B_ImportOrderToBackLading b_ImportOrderToBackLadingBean = null; //提货单引用销售合同
  private B_ImportPromotionToBackLading promotionToBackLadingBean = null;
  private LookUp salePriceBean = null; //销售单价的bean的引用, 用于提取销售单价
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String djlx="-1" ;       //提单类型
  private String tdid = null;
  private User user = null;
  public boolean isReport = false;
  public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
  public boolean conversion=false;//销售合同的换算数量与数量是否需要强制转换取
  private static ArrayList keys = new ArrayList();
  private static Hashtable table = new Hashtable();
  public String []zt;
  public String tCopyNumber = "1";
  private String zzsl="";//总数量
  private String zzje="";//总金额
  private String SLSQL="";//统计数量的SQL
  private String JESQL="";//统计金额的SQL
  public String dwdm="";//单位查询
  public String dwmc="";//单位查询
  public boolean canOperate=false;//
  //public String activetab = "SetActiveTab(INFO_EX,'INFO_EX_0')";//从表当前的div
  public String jglx = "";//单价类型
  public String zkl = "";
  public String thlx="";
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_BackBill getInstance(HttpServletRequest request)
  {
    B_BackBill b_BackBillBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_BackBillBean";
      b_BackBillBean = (B_BackBill)session.getAttribute(beanName);
      if(b_BackBillBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        b_BackBillBean = new B_BackBill();
        b_BackBillBean.qtyFormat = loginBean.getQtyFormat();
        b_BackBillBean.priceFormat = loginBean.getPriceFormat();
        b_BackBillBean.sumFormat = loginBean.getSumFormat();
        b_BackBillBean.fgsid = loginBean.getFirstDeptID();
        b_BackBillBean.loginId = loginBean.getUserID();
        b_BackBillBean.loginName = loginBean.getUserName();
        b_BackBillBean.user = loginBean.getUser();
        if(loginBean.getSystemParam("SC_STORE_UNIT_STYLE").equals("1"))
          b_BackBillBean.conversion = true;
        if(loginBean.getSystemParam("XS_LADINGBILL_HANDWORK").equals("1"))
          b_BackBillBean.canOperate = true;//是否可以手工开提单
        //设置格式化的字段

        b_BackBillBean.dsMasterTable.setColumnFormat("zje", b_BackBillBean.sumFormat);
        b_BackBillBean.dsMasterTable.setColumnFormat("zsl", b_BackBillBean.qtyFormat);

        b_BackBillBean.dsDetailTable.setColumnFormat("sl", b_BackBillBean.qtyFormat);
        b_BackBillBean.dsDetailTable.setColumnFormat("hssl", b_BackBillBean.qtyFormat);
        b_BackBillBean.dsDetailTable.setColumnFormat("xsj", b_BackBillBean.priceFormat);
        b_BackBillBean.dsDetailTable.setColumnFormat("dj", b_BackBillBean.priceFormat);
        b_BackBillBean.dsDetailTable.setColumnFormat("jje", b_BackBillBean.sumFormat);
        b_BackBillBean.dsDetailTable.setColumnFormat("xsje", b_BackBillBean.sumFormat);

        session.setAttribute(beanName, b_BackBillBean);
      }
    }
    return b_BackBillBean;
  }
  /**
   * 构造函数
   */
  private B_BackBill()
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

    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    setDataSetProperty(dsTdcyqkTable, XS_TDCYDK_STRUT_SQL);
    //setDataSetProperty(dsTdqtfyTable, XS_TDQTFY_STRUT_SQL);
    dsDetailTable.setTableName("xs_tdhw");
    setDataSetProperty(dsSearchTable, combineSQL(SEARCH_SQL,"?",new String[]{""}));

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"tdbh"}, new String[]{"SELECT pck_base.billNextCode('xs_td.t','tdbh') from dual"}));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"tdhwid"}, new String[]{"s_xs_tdhw"}));
    dsTdcyqkTable.setSequence(new SequenceDescriptor(new String[]{"tdcyqkid"}, new String[]{"s_xs_tdcyqk"}));
    //dsTdqtfyTable.setSequence(new SequenceDescriptor(new String[]{"tdqtfyid"}, new String[]{"s_xs_tdqtfy"}));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    Detail_Delete detaildel = new Detail_Delete();

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(TD_RETURN_ADD), masterAddEdit);//提单退货新增
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(CYQK_ADD), new Detail_Add());
    addObactioner(String.valueOf(QTFY_ADD), new Detail_Add());

    addObactioner(String.valueOf(DETAIL_SALE_ADD), new Detail_SALE_Add());
    addObactioner(String.valueOf(IMPORT_PROMOTION), new Detail_SALE_Add());

    addObactioner(String.valueOf(DETAIL_DEL), detaildel);
    addObactioner(String.valueOf(CYQK_DEL), detaildel);
    addObactioner(String.valueOf(QTFY_DEL), detaildel);

    //&#$//审核部分
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(REPORT), new Approve());

    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());
    addObactioner(String.valueOf(DETAIL_CHANGE), new Detail_Change());
    addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());
    addObactioner(String.valueOf(DEPT_CHANGE), new Dept_Change());
    addObactioner(String.valueOf(PRODUCT_ADD), new Multi_Product_Add());
    addObactioner(String.valueOf(DETAIL_COPY), new Detail_Copy_Add());//DETAIL_COPY
    addObactioner(String.valueOf(DEL_NULL), new Detail_Delete_Null());//Detail_Delete_Null
    addObactioner(String.valueOf(LADING_OUT), new Lading_Out());
    addObactioner(String.valueOf(LADING_OUT_TWO), new Lading_Out());
    addObactioner(String.valueOf(MASTER_ADD), new Master_Add());
    addObactioner(String.valueOf(APPROVED_MASTER_ADD), new Approved_Master_Post());
    addObactioner(String.valueOf(LADDING_CANCER), new Cancer());//作废
    addObactioner(String.valueOf(THLX_CHANGE), new Thlx_Change());

    addObactioner(String.valueOf(PROMOTION_PRODUCT_ADD), new Promotion_Product_Add());
    addObactioner(String.valueOf(DETAIL_MULTI_ADD), new Detail_Multi_Add());
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
    if(dsTdcyqkTable != null){
      dsTdcyqkTable.close();
      dsTdcyqkTable = null;
    }
    /*
    if(dsTdqtfyTable != null){
      dsTdqtfyTable.close();
      dsTdqtfyTable = null;
    }
    */
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
    //Returns the runtime class of an object.
    //That Class object is the object that is locked by static synchronized methods of the represented class.
    return getClass();//Object类的方法
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
        String tdbh="";
        m_RowInfo.put("czrq", today);//制单日期
        m_RowInfo.put("czy", loginName);//操作员
        m_RowInfo.put("jhrq", today);
        m_RowInfo.put("kdrq", today);
        m_RowInfo.put("czyid", loginId);
        m_RowInfo.put("thlx", "1");
        m_RowInfo.put("djlx", djlx);
        m_RowInfo.put("zt", "0");
        tdbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_td.t','tdbh') from dual");
        m_RowInfo.put("tdbh", tdbh);
        m_RowInfo.put("jbr", loginName);
        m_RowInfo.put("jhfhrq", today);
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
      /**承运情况**/
      if(d_TdcyqkRowInfos == null)
        d_TdcyqkRowInfos = new ArrayList(dsTdcyqkTable.getRowCount());
      else if(isInit)
        d_TdcyqkRowInfos.clear();
      dsTdcyqkTable.first();
      for(int i=0; i<dsTdcyqkTable.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsTdcyqkTable);
        d_TdcyqkRowInfos.add(row);
        dsTdcyqkTable.next();
      }
      /**其他费用
      if(d_TdqtfyRowInfos == null)
        d_TdqtfyRowInfos = new ArrayList(dsTdqtfyTable.getRowCount());
      else if(isInit)
        d_TdqtfyRowInfos.clear();
      dsTdqtfyTable.first();
      for(int i=0; i<dsTdqtfyTable.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsTdqtfyTable);
        d_TdqtfyRowInfos.add(row);
        dsTdqtfyTable.next();
      }**/
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
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));
      detailRow.put("wzdjid", rowInfo.get("wzdjid_"+i));//
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
      detailRow.put("hssl", rowInfo.get("hssl_"+i));//
      //detailRow.put("xsje", formatNumber(rowInfo.get("xsje_"+i), priceFormat));//销售金额
      detailRow.put("jje", formatNumber(rowInfo.get("jje_"+i), sumFormat));//净金额
      detailRow.put("xsj", formatNumber(rowInfo.get("xsj_"+i), priceFormat));//销售价
      detailRow.put("zk", rowInfo.get("zk_"+i));//折扣
      detailRow.put("dj", rowInfo.get("dj_"+i));//单价
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
      detailRow.put("hthwid", rowInfo.get("hthwid_"+i));
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
    /**承运情况**/
    rownum = d_TdcyqkRowInfos.size();
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_TdcyqkRowInfos.get(i);
      detailRow.put("qsd", rowInfo.get("qsd_"+i));//
      detailRow.put("dwtxid", rowInfo.get("dwtxid_"+i));
      detailRow.put("mdd", rowInfo.get("mdd_"+i));
      detailRow.put("zy", rowInfo.get("zy_"+i));//
      detailRow.put("fy", rowInfo.get("fy_"+i));//
      detailRow.put("lx", rowInfo.get("lx_"+i));
    }
    /**承运情况
    rownum = d_TdqtfyRowInfos.size();
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_TdqtfyRowInfos.get(i);
      detailRow.put("fylx", rowInfo.get("fylx_"+i));//
      detailRow.put("fy", rowInfo.get("qtfy_"+i));
      detailRow.put("zy", rowInfo.get("qtzy_"+i));//
    }**/
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
  public final EngineDataSet getMaterListTable()
  {
    return dsMasterList;
  }
  /*得到从表承运情况表对象*/
  public final EngineDataSet getCyqkTable(){
    if(!dsTdcyqkTable.isOpen())
      dsTdcyqkTable.open();
    return dsTdcyqkTable;
  }
  /*得到从表其他费用表对象
  public final EngineDataSet getQtfyTable(){
    if(!dsTdqtfyTable.isOpen())
      dsTdqtfyTable.open();
    return dsTdqtfyTable;
  }*/
  /*得到总数量*/
  public final String getZsl()
  {
    return zzsl;
  }
  /*得到总金额*/
  public final String getZje()
  {
    return zzje;
  }
  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    String SQL = combineSQL(DETAIL_SQL,"?",new String[]{(isMasterAdd ? "-1" : tdid)});
    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
    {
      dsDetailTable.open();
    }
    else
    {
      dsDetailTable.refresh();
    }
    /**承运情况**/
    SQL = XS_TDCYDK_SQL + (isMasterAdd ? "-1" : tdid);
    dsTdcyqkTable.setQueryString(SQL);
    if(!dsTdcyqkTable.isOpen())
    {
      dsTdcyqkTable.open();
    }
    else
    {
      dsTdcyqkTable.refresh();
    }
    /**其他费用
    SQL = XS_TDQTFY_SQL + (isMasterAdd ? "-1" : tdid);
    dsTdqtfyTable.setQueryString(SQL);
    if(!dsTdqtfyTable.isOpen())
    {
      dsTdqtfyTable.open();
    }
    else
    {
      dsTdqtfyTable.refresh();
    }**/
  }
  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }
  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /*得到从表承运情况多列的信息*/
  public final RowMap[] getTdcyqkRowinfos() {
    RowMap[] rows = new RowMap[d_TdcyqkRowInfos.size()];
    d_TdcyqkRowInfos.toArray(rows);
    return rows;
  }
  /*得到从表其他费用多列的信息
  public final RowMap[] getTdqtfyRowinfos() {
    RowMap[] rows = new RowMap[d_TdqtfyRowInfos.size()];
    d_TdqtfyRowInfos.toArray(rows);
    return rows;
  }*/
  /**
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isMasterAdd; }
  /**
   *引入单位的客户管理信息
   * */
  public RowMap getkhcpzk(String dwtxid,String cpid)
  {
    RowMap xyedRow ;
    try{
      EngineDataSet dskhcpzk = new EngineDataSet();
      setDataSetProperty(dskhcpzk,combineSQL(KHCPZK_SQL,"?",new String[]{dwtxid,cpid}));
      dskhcpzk.open();
      dskhcpzk.first();
      xyedRow= new RowMap(dskhcpzk);
      }catch(Exception e)
      {
        xyedRow=new RowMap();
      }
      return xyedRow;
  }
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
   * 是否可以完成
   * 提单数不大于出库数量时,才可以完成
   * 只要有一笔提单货物没完全出库,就不能完成
   * @param htid
   * @return
   */
  public boolean isCanOver(String tdid)
  {
    if(tdid.equals(""))
      return false;
    String count="";
    try
    {
      count = dataSetProvider.getSequence(CAN_OVER_SQL+tdid);
    }
    catch(Exception e){}
    if(!count.equals("0"))
      return false;
    else
      return true;
  }
  /**
   *在实提数量大于0的情况下,提货单不能取消审批
   * */
  public boolean isCanCancer(String tdid)
  {
    if(tdid.equals(""))
      return false;
    String count="";
    try
    {
      count = dataSetProvider.getSequence(CAN_CANCER_SQL+tdid);
    }
    catch(Exception e){}
    if(!count.equals("0"))
      return false;
    else
      return true;
  }
  /**
   *
   * @param htid
   * @return
   */
  public boolean hasReferenced(String tdid)
  {
    if(tdid.equals(""))
      return false;
    String count="";
    try
    {
      count = dataSetProvider.getSequence(REFERENCED_SQL+tdid);
    }
    catch(Exception e){}
    if(!count.equals("0"))
      return true;
    else
      return false;
  }
  //&#$
  /**
   * 审批操作的触发类
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isMasterAdd=false;
      String id=null;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      if(String.valueOf(REPORT).equals(action))
      {
        isReport=true;
        isApprove = false;
        id=request.getParameter("id");
      }else
      {
        isApprove = true;
        id = data.getParameter("id", "");
      }
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen())
        dsMasterTable.readyRefresh();
      dsMasterTable.refresh();

      //打开从表
      tdid = id;
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
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterList.getValue("tdbh");
      String deptid = dsMasterList.getValue("deptid");
      String tdid = dsMasterList.getValue("tdid");

      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_td a WHERE a.tdid='"+tdid+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        dsMasterList.readyRefresh();
        return;
      }
      approve.putAproveList(dsMasterList, dsMasterList.getRow(),"backsend", content,deptid);
    }
  }
  /**
   * 取消审批
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));

      String tdid = dsMasterList.getValue("tdid");
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_td a WHERE a.tdid='"+tdid+"'");
      if(zt!=null&&!zt.equals("1"))
      {
        dsMasterList.readyRefresh();
        return;
      }
      //是否已开出库单
      String count = dataSetProvider.getSequence("SELECT count(a.wjid) FROM kc_sfdjmx a,xs_tdhw b,kc_sfdj c WHERE a.wjid=b.tdhwid AND a.sfdjid=c.sfdjid AND c.djxz=2 AND b.tdid='"+tdid+"'");
      if(count!=null&&!count.equals("0"))
      {
        data.setMessage(showJavaScript("alert('不能取消审批!')"));
        dsMasterList.readyRefresh();
        return;
      }
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterList,dsMasterList.getRow(),"backsend");
    }
  }
  /**
   * 作废
   */
  class Cancer implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String tdid = data.getParameter("rownum");

      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_td a WHERE a.tdid='"+tdid+"'");
      if(zt!=null&&(zt.equals("8")||zt.equals("9")||zt.equals("4")))
      {
        dsMasterList.readyRefresh();
        return;
      }

      dsMasterTable.setQueryString(combineSQL(MASTER_EDIT_SQL,"?",new String[]{tdid}));
      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();
      dsMasterTable.setValue("zt", "4");
      dsMasterTable.saveChanges();

      dsMasterList.readyRefresh();
    }
  }
  /**
   * 2004-4-5
   * 状态2为已全部出库
   * sl>stsl时不能确认已全部出库
   * */
  class Lading_Out implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //传入的rownum是tdid
      //dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      tdid = data.getParameter("rownum");


      dsMasterTable.setQueryString(combineSQL(MASTER_EDIT_SQL,"?",new String[]{tdid}));
      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();

      openDetailTable(false);
      dsDetailTable.first();
     /*
      for(int i=0;i<dsDetailTable.getRowCount();i++)
      {
        String sl = dsDetailTable.getValue("sl");
        String stsl = dsDetailTable.getValue("stsl");//出库回填
        if((Math.abs(Double.parseDouble(sl.equals("")?"0":sl)))!=(Math.abs(Double.parseDouble(stsl.equals("")?"0":stsl))))
        {
          data.setMessage(showJavaScript("alert('与出库数量不一致,还不能出库确认!')"));
          return;
        }
        dsDetailTable.next();
      }
     */
      int counta=0;
    for(int i=0;i<dsDetailTable.getRowCount();i++)
    {
      String sl = dsDetailTable.getValue("sl");
      String stsl = dsDetailTable.getValue("stsl");//出库回填
      if((Math.abs(Double.parseDouble(sl.equals("")?"0":sl)))!=(Math.abs(Double.parseDouble(stsl.equals("")?"0":stsl))))
      {
        counta++;
      }
      dsDetailTable.next();
     }
     if(counta>0&&!String.valueOf(LADING_OUT_TWO).equals(action))
     {
      data.setMessage(showJavaScript("checksl("+tdid+");"));
      return;
      }
      BigDecimal bzsl = new BigDecimal(0);
      BigDecimal bzje = new BigDecimal(0);
      double dzsl = 0;
      double dzje = 0;
      dsDetailTable.first();
      for(int i=0;i<dsDetailTable.getRowCount();i++)
      {
        String sl = dsDetailTable.getValue("sl");
        String dj = dsDetailTable.getValue("dj");
        String stsl = dsDetailTable.getValue("stsl");//出库回填
        //if((Math.abs(Double.parseDouble(sl.equals("")?"0":sl)))!=(Math.abs(Double.parseDouble(stsl.equals("")?"0":stsl))))
        //{
        //  data.setMessage(showJavaScript("alert('与出库数量不一致,还不能出库确认!')"));
        //  return;
       // }
        dzsl = dzsl+Double.parseDouble(stsl.equals("")?"0":stsl);
        dzje = dzje+Double.parseDouble(stsl.equals("")?"0":stsl)*Double.parseDouble(dj.equals("")?"0":dj);

        bzsl = bzsl.add(new BigDecimal(stsl.equals("")?"0":stsl));
        bzje = bzje.add(new BigDecimal(dj.equals("")?"0":dj).multiply(new BigDecimal(stsl.equals("")?"0":stsl)));
        dsDetailTable.setValue("sl",stsl);
        String sjje = new BigDecimal(stsl.equals("")?"0":stsl).multiply(new BigDecimal(dj.equals("")?"0":dj)).toString();
        dsDetailTable.setValue("jje",new BigDecimal(stsl.equals("")?"0":stsl).multiply(new BigDecimal(dj.equals("")?"0":dj)).toString());
        dsDetailTable.next();
      }
      dsDetailTable.post();
      String sbssl = bzsl.toString();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      dsMasterTable.setValue("zt","8");
      dsMasterTable.setValue("zsl",String.valueOf(dzsl));
      dsMasterTable.setValue("zje",formatNumber(String.valueOf(dzje),"#0.00"));
      //dsMasterTable.setValue("zsl",bzsl.toString());
      //dsMasterTable.setValue("zje",bzje.toString());
      dsMasterTable.setValue("tdrq",today);
      dsMasterTable.post();
      dsMasterTable.saveDataSets(new EngineDataSet[]{dsMasterTable, dsDetailTable}, null);
      dsMasterList.readyRefresh();
      data.setMessage(showJavaScript("prnt("+tdid+")"));
    }
  }
  /**
   *所有相关提货单明细的应收款小于等于该提货单的实收金额时才能设为完成。
   * 完成

      class Lading_Over implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          int rownum = Integer.parseInt(data.getParameter("rownum"));
          dsMasterTable.goToRow(rownum);
          tdid = dsMasterTable.getValue("tdid");
          openDetailTable(false);
          String lx = dsMasterTable.getValue("djlx");
          dsDetailTable.first();
          for(int i=0;i<dsDetailTable.getRowCount();i++)
          {
            String jje = dsDetailTable.getValue("jje");
            String ssje = dsDetailTable.getValue("ssje");
            if((Math.abs(Double.parseDouble(jje.equals("")?"0":jje)))>(Math.abs(Double.parseDouble(ssje.equals("")?"0":ssje))))
            {
              data.setMessage(showJavaScript(lx.equals("1")?"alert('还有款项未收回,不能完成!')":"alert('还有款项未结清,不能完成!')"));
              return;
            }
            dsDetailTable.next();
          }

          dsMasterTable.setValue("zt","8");
          dsMasterTable.saveChanges();
        }
      }
      */
     /**
      * 初始化操作的触发类
      */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dwdm ="";
      dwmc ="";
      jglx = "";
      zkl = "";
      //&#$
      isApprove = false;
      isReport = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      tCopyNumber = "1";
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      masterListProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("kdrq$a", startDay);
      row.put("kdrq$b", today);
      //row.put("zt","0");
      zt = new String[]{""};
      isMasterAdd = true;
      String SQL = " AND zt<>8   AND zt<>3  AND zt<>4 ";
      String MSQL =  combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsMasterList.setQueryString(MSQL);
      dsMasterList.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      isReport=false;

      String ss=dsMasterTable.getQueryString();

      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code.equals("1"))
        submitType=true;
      else
        submitType=false;

      SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      JESQL = combineSQL(MASTER_JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;

      //zzsl = formatNumber(zzsl, priceFormat);
      //zzje = formatNumber(zzje, priceFormat);
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
      tdid = dsMasterList.getValue("tdid");
      //打开从表
      openDetailTable(false);
    }
  }
  /**
   * ---------------------------------------------------------
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //&#$
      isApprove = false;
      if(String.valueOf(EDIT).equals(action))
      {
        isMasterAdd=false;
        dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterList.getInternalRow();
        tdid = dsMasterList.getValue("tdid");
        String dwtxid = dsMasterList.getValue("dwtxid");
      }
      else
        isMasterAdd=true;

      dsMasterTable.setQueryString(isMasterAdd?MASTER_STRUT_SQL:combineSQL(MASTER_EDIT_SQL,"?",new String[]{tdid}));
      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();

      openDetailTable(isMasterAdd);
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
   * ---------------------------------------------------------
   * 合同列表里直接生成提单
   */
  class Master_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //&#$
      isApprove = false;
      isReport = false;
      isMasterAdd = true;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      tCopyNumber = "1";
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("kdrq$a", startDay);
      row.put("kdrq$b", today);
      //row.put("zt","0");
      zt = new String[]{""};

      if(!dsMasterList.isOpen())
        dsMasterList.open();
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      isReport=false;

      String SQL = " AND zt<>8  AND zt<>2  AND zt<>3 ";
      SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      JESQL = combineSQL(MASTER_JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    private String zt="0";
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      zt = ds.getValue("zt");
      if(isMasterAdd)
        zt="0";
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
      if(isMasterAdd){
        String tdbh = rowInfo.get("tdbh");
        String count = dataSetProvider.getSequence("select count(*) from xs_td t where t.tdbh='"+tdbh+"'");
        if(!count.equals("0"))
        {
          tdbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_td.t','tdbh') from dual");
        }
        ds.insertRow(false);
        tdid = dataSetProvider.getSequence("s_xs_td");
        ds.setValue("tdid", tdid);//主健
        zt="0";
        ds.setValue("zt","0");
        ds.setValue("tdbh",tdbh);
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("czyid", loginId);
        ds.setValue("czy", loginName);//操作员
        ds.setValue("fgsid", fgsid);//分公司
        isMasterAdd=false;
      }else
        zt = ds.getValue("zt");
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0),totalZje=new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      double zsl=0.0;
      double zje = 0;
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        detail.setValue("tdid", tdid);
        detail.setValue("cpid", detailrow.get("cpid"));//?
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        double xsj = detailrow.get("xsj").length() > 0 ? Double.parseDouble(detailrow.get("xsj")) : 0;//销售价
        double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;  //数量
        double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(detailrow.get("dj")) : 0;//单价

        double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;
        double xsje =sl*xsj; //detailrow.get("xsje").length() > 0 ? Double.parseDouble(detailrow.get("xsje")) : 0;
        double jje = sl*dj;//detailrow.get("jje").length() > 0 ? Double.parseDouble(detailrow.get("jje")) : 0;
        zje =zje+jje;
        zsl=zsl+sl;
        if(zt.equals("0"))
          detail.setValue("kdsl", detailrow.get("sl"));
        detail.setValue("sl", detailrow.get("sl"));//数量
        detail.setValue("xsj", formatNumber(detailrow.get("xsj"),"#0.00"));
        detail.setValue("xsje", formatNumber(String.valueOf(sl * xsj),"#0.00"));
        detail.setValue("zk", detailrow.get("zk"));//折扣
        detail.setValue("dj", formatNumber(detailrow.get("dj"),"#0.00"));
        detail.setValue("hssl", detailrow.get("hssl"));//
        detail.setValue("wzdjid", detailrow.get("wzdjid"));  //单价?
        detail.setValue("jje",formatNumber(String.valueOf(sl * dj),"#0.00"));

        detail.setValue("bz", detailrow.get("bz"));//备注
        detail.setValue("cjtcl", detailrow.get("cjtcl"));

        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        totalSum = totalSum.add(detail.getBigDecimal("xsje"));
        totalZje =totalZje.add(new BigDecimal(String.valueOf(jje)));
        detail.next();
      }
      /**承运情况**/
      dsTdcyqkTable.first();
      for(int i=0; i<dsTdcyqkTable.getRowCount(); i++)
      {
        detailrow = (RowMap)d_TdcyqkRowInfos.get(i);
        //新添的记录
        dsTdcyqkTable.setValue("tdid", tdid);
        dsTdcyqkTable.setValue("dwtxid", detailrow.get("dwtxid"));
        dsTdcyqkTable.setValue("qsd", detailrow.get("qsd"));//?
        dsTdcyqkTable.setValue("mdd", detailrow.get("mdd"));
        dsTdcyqkTable.setValue("zy", detailrow.get("zy"));//
        dsTdcyqkTable.setValue("fy", detailrow.get("fy"));//
        dsTdcyqkTable.setValue("lx", detailrow.get("lx"));//
        dsTdcyqkTable.post();
        dsTdcyqkTable.next();
      }
      /**其他费用
      dsTdqtfyTable.first();
      for(int i=0; i<dsTdqtfyTable.getRowCount(); i++)
      {
        detailrow = (RowMap)d_TdqtfyRowInfos.get(i);
        //新添的记录
        dsTdqtfyTable.setValue("tdid", tdid);
        dsTdqtfyTable.setValue("fylx", detailrow.get("fylx"));//?
        dsTdqtfyTable.setValue("fy", detailrow.get("fy"));
        dsTdqtfyTable.setValue("zy", detailrow.get("zy"));//

        dsTdqtfyTable.post();
        dsTdqtfyTable.next();
      }**/
      String tdbh = rowInfo.get("tdbh");

      //保存主表数据
      ds.setValue("kdrq", rowInfo.get("kdrq"));//提单日期
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("jsfsid", rowInfo.get("jsfsid"));//结算方式ID
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));//购货单位ID
      ds.setValue("personid", rowInfo.get("personid"));//人员ID
      ds.setValue("zsl", String.valueOf(zsl));//总数量
      ds.setValue("ztms", rowInfo.get("ztms"));//状态描述
      ds.setValue("storeid", rowInfo.get("storeid"));//仓库id
      ds.setValue("hkrq", rowInfo.get("hkrq"));//回款日期
      ds.setValue("hkts", rowInfo.get("hkts"));//回款天数
      ds.setValue("djlx", djlx);//单据类型
      ds.setValue("sendmodeid", rowInfo.get("sendmodeid"));//sendmodeid
      ds.setValue("dz", rowInfo.get("dz"));
      ds.setValue("lxr", rowInfo.get("lxr"));
      ds.setValue("zje", String.valueOf(zje));//总金额
      ds.setValue("khlx", rowInfo.get("khlx"));//客户类型
      ds.setValue("thr", rowInfo.get("thr"));
      ds.setValue("jbr", rowInfo.get("jbr"));
      ds.setValue("thlx", rowInfo.get("thlx"));
      ds.setValue("fgsid", fgsid);//分公司
      ds.setValue("bz", rowInfo.get("bz"));
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail,dsTdcyqkTable}, null);
      dsMasterList.readyRefresh();
      //合计相关
      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;
      dsMasterList.readyRefresh();


      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);//重新初始化从表的各行信息
        detail.empty();
        dsTdcyqkTable.empty();
        //dsTdqtfyTable.empty();
        initRowInfo(false, false, true);//重新初始化从表的各行信息
      }
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      HashSet htmp = new HashSet();
      htmp.clear();
      //HashSet wzdijds=new HashSet(d_RowInfos.size());
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()==0)
      {
        return showJavaScript("alert('从表不能空--没产品等相关信息！');");
      }
      /**明细**/
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        temp = detailrow.get("cpid");
        if(temp.equals(""))
          return showJavaScript("alert('产品不能为空！');");
        String cpid="c"+detailrow.get("cpid");
        String dmsxid="d"+detailrow.get("dmsxid").trim();
        if(!htmp.add(cpid+dmsxid))
          return showJavaScript("alert('所选第"+(i+1)+"行货物重复!');");
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, detailProducer.getFieldInfo("sl").getFieldname())) != null)
          return temp;
        String hssl = detailrow.get("hssl");
        if(!hssl.equals(""))
        {
          if((temp = checkNumber(hssl, detailProducer.getFieldInfo("hssl").getFieldname())) != null)
            return temp;
        }
        double dsl= Double.parseDouble(sl);
        //if(dsl<0)
        //  return showJavaScript("alert('数量必须大于或等于0')");
        String dj = detailrow.get("dj");
        if((temp = checkNumber(dj, detailProducer.getFieldInfo("dj").getFieldname())) != null)
          return temp;
        double ddj= Double.parseDouble(dj);
        if(ddj<0)
          return showJavaScript("alert('单价必须大于或等于0')");
        String zk = detailrow.get("zk");
        if((temp = checkNumber(zk, detailProducer.getFieldInfo("zk").getFieldname())) != null)
          return temp;
        String jje = detailrow.get("jje");
        if((temp = checkNumber(jje, detailProducer.getFieldInfo("jje").getFieldname())) != null)
          return temp;
        double djje= Double.parseDouble(jje);
        //if(djje<0)
        //  return showJavaScript("alert('金额必须大于或等于0')");
        //去除数量小于等于零的判断 2004.5.16 modify by jac
        if(isMasterAdd||zt.equals("0"))
        {
          if(Double.parseDouble(sl) == 0)
            return showJavaScript("alert('数量不能等于零!');");
        }
        String stsl = detailrow.get("stsl");
        if(Math.abs(Double.parseDouble(sl)) < Math.abs(Double.parseDouble(stsl.equals("")?"0":stsl)))
          return showJavaScript("alert('数量不能小于已出库出量!');");
      }
      /**承运情况**/
      for(int i=0; i<d_TdcyqkRowInfos.size(); i++)
      {
        detailrow = (RowMap)d_TdcyqkRowInfos.get(i);
        String dwtxid = detailrow.get("dwtxid");
        if(dwtxid.equals(""))
          return showJavaScript("alert('承运商不能空!');");
        String lx = detailrow.get("lx");
        if(lx.equals(""))
          return showJavaScript("alert('承运情况的类型不能空!');");
        String fy = detailrow.get("fy");
        if((temp = checkNumber(fy, "承运情况的费用")) != null)
          return temp;
      }
      /**其他费用
      for(int i=0; i<d_TdqtfyRowInfos.size(); i++)
      {
        detailrow = (RowMap)d_TdqtfyRowInfos.get(i);
        String fylx = detailrow.get("fylx");
        if(fylx.equals(""))
          return showJavaScript("alert('其他费用的类型不能空!');");
        String fy = detailrow.get("fy");
        if((temp = checkNumber(fy, "其他费用的费用")) != null)
          return temp;
      }**/
      return null;
    }
    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("kdrq");
      if(temp.equals(""))
        return showJavaScript("alert('开单日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法开单日期！');");
      temp = rowInfo.get("dwtxid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择购货单位！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择部门！');");
      temp = rowInfo.get("personid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择业务员！');");
      temp = rowInfo.get("jsfsid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择结算方式！');");
      temp = rowInfo.get("storeid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择仓库！');");
      temp = rowInfo.get("khlx");
      if(temp.equals(""))
        return showJavaScript("alert('请选择客户类型！');");
      temp = rowInfo.get("bz");
      if(!temp.equals("")&&temp.length()>100)
        return showJavaScript("alert('备注长度不能超过100个字！');");
      return null;
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Approved_Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isMasterAdd=false;
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      ds.goToInternalRow(masterRow);
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
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0),totalZje=new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      double zsl=0.0;
      double zje = 0;
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);

        double xsj = detailrow.get("xsj").length() > 0 ? Double.parseDouble(detailrow.get("xsj")) : 0;//销售价
        double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;  //数量
        double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(detailrow.get("dj")) : 0;//单价
        double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;
        double xsje =sl*xsj;
        double jje = sl*dj;
        zje =zje+jje;
        zsl=zsl+sl;
        detail.setValue("sl", detailrow.get("sl"));//数量

        detail.setValue("xsj", formatNumber(detailrow.get("xsj"),"#0.00"));
        detail.setValue("zk", detailrow.get("zk"));//折扣
        detail.setValue("dj", formatNumber(detailrow.get("dj"),"#0.00"));
        detail.setValue("hssl", detailrow.get("hssl"));//

        detail.setValue("jje",formatNumber(String.valueOf(sl * dj),"#0.00"));

        detail.setValue("bz", detailrow.get("bz"));//备注

        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        totalSum = totalSum.add(detail.getBigDecimal("xsje"));
        totalZje =totalZje.add(new BigDecimal(String.valueOf(jje)));
        detail.next();
      }
      /**承运情况**/
      dsTdcyqkTable.first();
      for(int i=0; i<dsTdcyqkTable.getRowCount(); i++)
      {
        detailrow = (RowMap)d_TdcyqkRowInfos.get(i);
        //新添的记录
        dsTdcyqkTable.setValue("tdid", tdid);
        dsTdcyqkTable.setValue("dwtxid", detailrow.get("dwtxid"));
        dsTdcyqkTable.setValue("qsd", detailrow.get("qsd"));//?
        dsTdcyqkTable.setValue("mdd", detailrow.get("mdd"));
        dsTdcyqkTable.setValue("zy", detailrow.get("zy"));//
        dsTdcyqkTable.setValue("fy", detailrow.get("fy"));//
        dsTdcyqkTable.setValue("lx", detailrow.get("lx"));//
        dsTdcyqkTable.post();
        dsTdcyqkTable.next();
      }
      ds.setValue("bz", rowInfo.get("bz"));
      ds.setValue("zsl", String.valueOf(zsl));//总金额
      ds.setValue("zje", String.valueOf(zje));//总金额
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail,dsTdcyqkTable}, null);
      dsMasterList.readyRefresh();
      //合计相关
      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;

    }
    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String  temp = rowInfo.get("bz");
      if(!temp.equals("")&&temp.length()>100)
        return showJavaScript("alert('备注长度不能超过100个字！');");
      return null;
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      HashSet htmp = new HashSet();
      htmp.clear();
      //HashSet wzdijds=new HashSet(d_RowInfos.size());
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()==0)
      {
        return showJavaScript("alert('从表不能空--没产品等相关信息！');");
      }
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String cpid="c"+detailrow.get("cpid");
        String dmsxid="d"+detailrow.get("dmsxid").trim();
        if(!htmp.add(cpid+dmsxid))
          return showJavaScript("alert('所选第"+(i+1)+"行货物重复!');");
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, detailProducer.getFieldInfo("sl").getFieldname())) != null)
          return temp;
        String dj = detailrow.get("dj");
        if((temp = checkNumber(dj, detailProducer.getFieldInfo("dj").getFieldname())) != null)
          return temp;
        //String zk = detailrow.get("zk");
        //if((temp = checkNumber(zk, detailProducer.getFieldInfo("zk").getFieldname())) != null)
        // return temp;
        String jje = detailrow.get("jje");
        if((temp = checkNumber(jje, detailProducer.getFieldInfo("jje").getFieldname())) != null)
          return temp;

        if(isMasterAdd||zt.equals("0"))
        {
          if(Double.parseDouble(sl) == 0)
            return showJavaScript("alert('数量不能等于零!');");
        }
        String stsl = detailrow.get("stsl");
        if(Math.abs(Double.parseDouble(sl)) < Math.abs(Double.parseDouble(stsl.equals("")?"0":stsl)))
          return showJavaScript("alert('数量不能小于已出库出量!');");
        String hssl = detailrow.get("hssl");
        if((temp = checkNumber(sl, detailProducer.getFieldInfo("hssl").getFieldname())) != null)
          return temp;
      }
      /**承运情况**/
      for(int i=0; i<d_TdcyqkRowInfos.size(); i++)
      {
        detailrow = (RowMap)d_TdcyqkRowInfos.get(i);
        String dwtxid = detailrow.get("dwtxid");
        if(dwtxid.equals(""))
          return showJavaScript("alert('承运商不能空!');");
        String lx = detailrow.get("lx");
        if(lx.equals(""))
          return showJavaScript("alert('承运情况的类型不能空!');");
        String fy = detailrow.get("fy");
        if((temp = checkNumber(fy, "承运情况的费用")) != null)
          return temp;
      }
      /**其他费用
      for(int i=0; i<d_TdqtfyRowInfos.size(); i++)
      {
        detailrow = (RowMap)d_TdqtfyRowInfos.get(i);
        String fylx = detailrow.get("fylx");
        if(fylx.equals(""))
          return showJavaScript("alert('其他费用的类型不能空!');");
        String fy = detailrow.get("fy");
        if((temp = checkNumber(fy, "其他费用的费用")) != null)
          return temp;
      }**/
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
      String id = ds.getValue("tdid");
      String count = dataSetProvider.getSequence("SELECT SUM(nvl(a.stsl,0)) FROM xs_tdhw a WHERE a.tdid='"+id+"'");
      if(!count.equals("0"))
      {
        data.setMessage(showJavaScript("alert('该提单已被引用,不能删除!')"));
        return;
      }
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_td a WHERE a.tdid='"+id+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        data.setMessage(showJavaScript("alert('该单据不能删除!')"));
        return;
      }
      dsDetailTable.deleteAllRows();
      dsTdcyqkTable.deleteAllRows();
      //dsTdqtfyTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable,dsTdcyqkTable}, null);

      //合计相关
      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");
      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");
      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;
      zzsl = formatNumber(zzsl, priceFormat);
      zzje = formatNumber(zzje, priceFormat);

      d_RowInfos.clear();
      d_TdcyqkRowInfos.clear();
      //d_TdqtfyRowInfos.clear();
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
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
          /*
          SQL=SQL+" AND "+user.getHandleDeptWhereValue("deptid", "czyid");
          SQL = combineSQL(SEARCH_SQL, "?", new String[]{SQL});
          if(!dsSearchTable.getQueryString().equals(SQL))
          {
            dsSearchTable.setQueryString(SQL);
            dsSearchTable.setRowMax(null);
          }
          dsSearchTable.refresh();
          StringBuffer sb = new StringBuffer();
          ArrayList al = new ArrayList();
          String tdids="";
          dsSearchTable.first();
          for(int i=0;i<dsSearchTable.getRowCount();i++)
          {
            String tdid = dsSearchTable.getValue("tdid");
            if(tdid!=null&&!tdid.equals(""))
              if(!al.contains(tdid))
                al.add(tdid);
            dsSearchTable.next();
          }
          if(al.size()==0)
            tdids="0";
          else
          {
            for(int j=0;j<al.size();j++)
              sb.append(al.get(j)+",");
            tdids=sb.append("0").toString();
          }
          String alltd = sb.toString();
          SQL = alltd.equals("")? " and tdid IN(-1)":" and tdid IN("+alltd+")";
          */
      zt = data.getRequest().getParameterValues("zt");
      if(!(zt==null))
      {
        StringBuffer sbzt = null;
        for(int i=0;i<zt.length;i++)
        {
          if(sbzt==null)
            sbzt= new StringBuffer(" AND zt IN(");
          sbzt.append(zt[i]+",");
        }
        if(sbzt == null)
          sbzt =new StringBuffer();
        else
          sbzt.append("-99)");
        SQL = SQL+sbzt.toString();
      }
      else
        zt = new String[]{""};


      String MSQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});//组装SQL语句
      if(!dsMasterList.getQueryString().equals(MSQL))
      {
        dsMasterList.setQueryString(MSQL);
        dsMasterList.setRowMax(null);
      }
      SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      JESQL = combineSQL(MASTER_JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;
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
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("tdbh"), null, null, null, null, "="),

        new QueryColumn(master.getColumn("kdrq"), null, null, null, "a", ">="),//制单日期
        new QueryColumn(master.getColumn("kdrq"), null, null, null, "b", "<="),//制单日期
        new QueryColumn(master.getColumn("tdrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("tdrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门id
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("storeid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("thlx"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("tdid"), "VW_XS_TD_DETAIL", "tdid", "product", "product", "like"),//从表品名
        new QueryColumn(master.getColumn("tdid"), "VW_XS_TD_DETAIL", "tdid", "cpbm", "cpbm", "like"),
        new QueryColumn(master.getColumn("tdid"), "VW_XS_TD_DETAIL", "tdid", "cpid", "cpid", "="),
        new QueryColumn(master.getColumn("czy"), null, null, null, null, "like")
      });
      isInitQuery = true;
    }
  }
  /**
   *  从表新增
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      if(String.valueOf(DETAIL_ADD).equals(action))
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("tdhwid", "-1");
        dsDetailTable.setValue("tdid", tdid);
        dsDetailTable.post();
        RowMap detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
        }else if(String.valueOf(CYQK_ADD).equals(action))
        {
          dsTdcyqkTable.insertRow(false);
          dsTdcyqkTable.setValue("tdcyqkid", "-1");
          dsTdcyqkTable.setValue("tdid", tdid);
          dsTdcyqkTable.post();
          RowMap detailrow = new RowMap(dsTdcyqkTable);
          d_TdcyqkRowInfos.add(detailrow);
          //activetab="SetActiveTab(INFO_EX,'INFO_EX_0')";
          //data.setMessage(showJavaScript(activetab));
        }
          /*else if(String.valueOf(QTFY_ADD).equals(action))
          {
            dsTdqtfyTable.insertRow(false);
            dsTdqtfyTable.setValue("tdqtfyid", "-1");
            dsTdqtfyTable.setValue("tdid", tdid);
            dsTdqtfyTable.post();
            RowMap detailrow = new RowMap(dsTdqtfyTable);
            d_TdqtfyRowInfos.add(detailrow);
            activetab="SetActiveTab(INFO_EX,'INFO_EX_1')";
            data.setMessage(showJavaScript(activetab));
          }*/
    }
  }
  /**
   * 复制当前行
   *
   * */
  class Detail_Copy_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      String rownum = req.getParameter("rownum");
      tCopyNumber = req.getParameter("tCopyNumber");
      int row = Integer.parseInt(rownum);
      int size = d_RowInfos.size();
      if(row>size)
        return;
      RowMap newadd = (RowMap)d_RowInfos.get(row);

      String cpid = newadd.get("cpid");
      String wzdjid = newadd.get("wzdjid");
      String xsj = newadd.get("xsj");
      String hthwid = newadd.get("hthwid");
      int copynumber = Integer.parseInt(tCopyNumber);
      for(int i=0;i<copynumber;i++)
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("tdhwid", "-1");
        dsDetailTable.setValue("tdid", tdid);
        dsDetailTable.setValue("cpid", cpid);
        dsDetailTable.setValue("wzdjid", wzdjid);
        dsDetailTable.setValue("xsj", xsj);
        dsDetailTable.setValue("hthwid", hthwid);
        dsDetailTable.post();
        RowMap detailrow = new RowMap();
        detailrow.put("tdid", tdid);
        detailrow.put("cpid", cpid);
        detailrow.put("wzdjid", wzdjid);
        detailrow.put("xsj", xsj);
        detailrow.put("hthwid", hthwid);
        d_RowInfos.add(detailrow);
      }
      tCopyNumber="1";
    }
  }
  class Detail_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      putDetailInfo(data.getRequest());
      String dwtxid = data.getParameter("dwtxid");

      RowMap detailRow = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailRow = (RowMap)d_RowInfos.get(i);
        String wzdjid = detailRow.get("wzdjid");
        String cpid = detailRow.get("cpid");
        if(rownum==i)
        {
          RowMap khcpzkrow = getkhcpzk(dwtxid,cpid);
          String zk = khcpzkrow.get("zk");
          String djlx = khcpzkrow.get("djlx");
          String mrzk = dataSetProvider.getSequence("select t.mrzk from xs_wzdj t WHERE t.wzdjid='"+wzdjid+"'");
          String xsj= "";
          if(djlx.equals(""))
          {
            String mrjg = dataSetProvider.getSequence("select t.mrjg from xs_wzdj t WHERE t.wzdjid='"+wzdjid+"'");
            if(mrjg==null||mrjg.equals(""))
            {
              data.setMessage(showJavaScript("alert('该产品没定义默认价格!')"));
              return;
            }
            xsj = dataSetProvider.getSequence("select t."+mrjg+" from xs_wzdj t WHERE t.wzdjid='"+wzdjid+"'");
          }
          else
            xsj = dataSetProvider.getSequence("select t."+djlx+" from xs_wzdj t WHERE t.wzdjid='"+wzdjid+"'");

          if(xsj==null||xsj.equals(""))
          {
            data.setMessage(showJavaScript("alert('该产品没定义默认价格!')"));
            return;
          }
          if(zk==null||zk.equals(""))
            zk=mrzk;
          if(zk==null||zk.equals(""))
            zk="100";
          detailRow.put("xsj", xsj);
          detailRow.put("dj", new BigDecimal(xsj).multiply(new BigDecimal(zk).divide(new BigDecimal(100),4,4)).toString());
          detailRow.put("zk", zk);
        }
      }
    }
  }
  /**
   *部门改变
   * */
  class Dept_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      m_RowInfo.put(req);
    }
  }
  /**
   * 引入合同
   * 对应页面引入合同操作
   * 一次只能引入一张合同的货物
   * 引入销售合同货物
   * */
  class Detail_SALE_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP);
      //保存输入的明细信息
      putDetailInfo(data.getRequest());

      String importOrder = m_RowInfo.get("importOrder");
      String importPromotion = m_RowInfo.get("importPromotion");
      String dwtxid =  m_RowInfo.get("dwtxid");
      String jsfsid =  m_RowInfo.get("jsfsid");
      String deptid =  m_RowInfo.get("deptid");
      String personid =  m_RowInfo.get("personid");
      String khlx =  m_RowInfo.get("khlx");
      String sendmodeid =  m_RowInfo.get("sendmodeid");
      String jhfhrq =  m_RowInfo.get("jhfhrq");
      String dz =  m_RowInfo.get("dz");

      if(String.valueOf(DETAIL_SALE_ADD).equals(action))
      {


        if(importOrder.length() == 0)
          return;
        //实例化查找数据集的类
        EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwID");
        String[] hthwIDs = parseString(importOrder,",");//解析出合同货物ID数组
        b_ImportOrderToBackLadingBean =getb_ImportOrderBean(req);     //提货
        //b_ImportThOrderBean =getb_ImportThOrderBean(req); //退货
        BigDecimal bd = new BigDecimal(0);
        double max = 0;
        for(int i=0; i < hthwIDs.length; i++)
        {
          if(hthwIDs[i].equals("-1"))
            continue;
          if(!isMasterAdd)
            dsMasterTable.goToInternalRow(masterRow);
          String tdid = dsMasterTable.getValue("tdid");
          RowMap detailrow = null;
          locateGoodsRow.setValue(0, hthwIDs[i]);
          if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
          {
            RowMap orderRow =null;

            orderRow = b_ImportOrderToBackLadingBean.getLookupRow(hthwIDs[i]);//提货


            if(dwtxid.equals(""))
            {
              dwtxid = orderRow.get("dwtxid");
              m_RowInfo.put("dwtxid",orderRow.get("dwtxid"));
            }
            if(jsfsid.equals(""))
            {
              jsfsid = orderRow.get("jsfsid");
              m_RowInfo.put("jsfsid",orderRow.get("jsfsid"));
            }
            if(dz.equals(""))
            {
              dz = orderRow.get("dz");
              m_RowInfo.put("dz",orderRow.get("dz"));
            }
            if(deptid.equals(""))
            {
              deptid = orderRow.get("deptid");
              m_RowInfo.put("deptid",orderRow.get("deptid"));
            }
            if(jhfhrq.equals(""))
            {
              jhfhrq = orderRow.get("jhfhrq");
              m_RowInfo.put("jhfhrq",orderRow.get("jhfhrq"));
            }
            if(sendmodeid.equals(""))
            {
              sendmodeid = orderRow.get("sendmodeid");
              m_RowInfo.put("sendmodeid",orderRow.get("sendmodeid"));
            }
            if(personid.equals(""))
            {
              personid = orderRow.get("personid");
              m_RowInfo.put("personid",orderRow.get("personid"));
            }

            if(khlx.equals(""))
            {
              khlx = orderRow.get("khlx");
              m_RowInfo.put("khlx",orderRow.get("khlx"));
            }
              /*
              if(yfdj.equals(""))
              {
                yfdj = orderRow.get("yfdj");
                m_RowInfo.put("yfdj",orderRow.get("yfdj"));
              }
              */
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("tdhwid", "-1");
            dsDetailTable.setValue("tdid", tdid);
            dsDetailTable.setValue("cpid", orderRow.get("cpid"));
            dsDetailTable.setValue("hthwid", hthwIDs[i]);
            dsDetailTable.setValue("wzdjid", orderRow.get("wzdjid"));
            String dmsxid = orderRow.get("dmsxid");
            dsDetailTable.setValue("dmsxid", dmsxid);

            String cjtcl = orderRow.get("cjtcl");
            dsDetailTable.setValue("cjtcl", cjtcl);
            dsDetailTable.setValue("hlts", orderRow.get("hlts"));
            String jzj = orderRow.get("jzj");
            dsDetailTable.setValue("jzj", orderRow.get("jzj"));
            String jxts = orderRow.get("jxts");
            dsDetailTable.setValue("jxts", orderRow.get("jxts"));
            String hltcl = orderRow.get("hltcl");
            dsDetailTable.setValue("hltcl", orderRow.get("hltcl"));

            String hlts = orderRow.get("hlts").equals("")?"0":orderRow.get("hlts");
            double dhlts = Double.parseDouble(hlts);
            if(max<dhlts)
              max=dhlts;//2004-4-13取最大回笼天数
            //bd=bd.add(new BigDecimal(hlts));

            double sl=Double.parseDouble(orderRow.get("sl").equals("")?"0":orderRow.get("sl"));//合同数量
            double stsl=Double.parseDouble(orderRow.get("stsl").equals("")?"0":orderRow.get("stsl"));//实出库数量
            double skdsl=Double.parseDouble(orderRow.get("skdsl").equals("")?"0":orderRow.get("skdsl"));//实开单数量
            double hssl = Double.parseDouble(orderRow.get("hssl").equals("")?"0":orderRow.get("hssl"));
            double xhssl = hssl*(sl-skdsl)/sl;
            double xsj = Double.parseDouble(orderRow.get("xsj").equals("")?"0":orderRow.get("xsj"));
            double dj = Double.parseDouble(orderRow.get("dj").equals("")?"0":orderRow.get("dj"));
            double zk = Double.parseDouble(orderRow.get("zk").equals("")?"0":orderRow.get("zk"));

            dsDetailTable.setValue("hssl", String.valueOf(hssl));
            dsDetailTable.setValue("sl", String.valueOf(sl));
            dsDetailTable.setValue("xsje",formatNumber(String.valueOf(xsj*(sl)), sumFormat) );
            dsDetailTable.setValue("jje", formatNumber(String.valueOf(dj*(sl)), sumFormat) );


            dsDetailTable.setValue("xsj", orderRow.get("xsj"));
            dsDetailTable.setValue("zk", orderRow.get("zk"));
            dsDetailTable.setValue("dj", orderRow.get("dj"));
            dsDetailTable.setValue("bz", orderRow.get("bz"));
            //dsDetailTable.setValue("sthssl", saleRow.get("sthssl"));
            //dsDetailTable.setValue("stsl", saleRow.get("stsl"));
            dsDetailTable.post();
            //创建一个与用户相对应的行
            detailrow = new RowMap(dsDetailTable);
            d_RowInfos.add(detailrow);
          }
        }
        String ts = m_RowInfo.get("hkts");
        String kdrq = m_RowInfo.get("kdrq");
        Date startdate = new SimpleDateFormat("yyyy-MM-dd").parse(kdrq);//提单的开单日期
        Date enddate = new Date(startdate.getTime() + (long)max*(60*60*24*1000));//毫秒
        if(ts.equals(""))
        {
          m_RowInfo.put("hkts",String.valueOf((int)max));
          m_RowInfo.put("hkrq",new SimpleDateFormat("yyyy-MM-dd").format(enddate));
        }
      }
      if(String.valueOf(IMPORT_PROMOTION).equals(action))
      {
        if(importPromotion.length() == 0)
          return;
        //实例化查找数据集的类
        EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwID");
        String[] tdhwids = parseString(importPromotion,",");//解析出合同货物ID数组
        promotionToBackLadingBean =getb_ImportPromotionBean(req);     //提货
        //b_ImportThOrderBean =getb_ImportThOrderBean(req); //退货
        BigDecimal bd = new BigDecimal(0);
        double max = 0;
        for(int i=0; i < tdhwids.length; i++)
        {
          if(tdhwids[i].equals("-1"))
            continue;
          if(!isMasterAdd)
            dsMasterTable.goToInternalRow(masterRow);
          String tdid = dsMasterTable.getValue("tdid");
          RowMap detailrow = null;
          locateGoodsRow.setValue(0, tdhwids[i]);
          if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
          {
            RowMap orderRow =null;

            orderRow = promotionToBackLadingBean.getLookupRow(tdhwids[i]);//提货


            if(dwtxid.equals(""))
            {
              dwtxid = orderRow.get("dwtxid");
              m_RowInfo.put("dwtxid",orderRow.get("dwtxid"));
            }
            if(jsfsid.equals(""))
            {
              jsfsid = orderRow.get("jsfsid");
              m_RowInfo.put("jsfsid",orderRow.get("jsfsid"));
            }
            if(dz.equals(""))
            {
              dz = orderRow.get("dz");
              m_RowInfo.put("dz",orderRow.get("dz"));
            }
            if(deptid.equals(""))
            {
              deptid = orderRow.get("deptid");
              m_RowInfo.put("deptid",orderRow.get("deptid"));
            }
            if(jhfhrq.equals(""))
            {
              jhfhrq = orderRow.get("jhfhrq");
              m_RowInfo.put("jhfhrq",orderRow.get("jhfhrq"));
            }
            if(sendmodeid.equals(""))
            {
              sendmodeid = orderRow.get("sendmodeid");
              m_RowInfo.put("sendmodeid",orderRow.get("sendmodeid"));
            }
            if(personid.equals(""))
            {
              personid = orderRow.get("personid");
              m_RowInfo.put("personid",orderRow.get("personid"));
            }

            if(khlx.equals(""))
            {
              khlx = orderRow.get("khlx");
              m_RowInfo.put("khlx",orderRow.get("khlx"));
            }
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("tdhwid", "-1");
            dsDetailTable.setValue("tdid", tdid);
            dsDetailTable.setValue("cpid", orderRow.get("cpid"));
            ///dsDetailTable.setValue("hthwid", tdhwids[i]);
            dsDetailTable.setValue("wzdjid", orderRow.get("wzdjid"));
            String dmsxid = orderRow.get("dmsxid");
            dsDetailTable.setValue("dmsxid", dmsxid);

            String cjtcl = orderRow.get("cjtcl");
            dsDetailTable.setValue("cjtcl", cjtcl);
            dsDetailTable.setValue("hlts", orderRow.get("hlts"));
            String jzj = orderRow.get("jzj");
            dsDetailTable.setValue("jzj", orderRow.get("jzj"));
            String jxts = orderRow.get("jxts");
            dsDetailTable.setValue("jxts", orderRow.get("jxts"));
            String hltcl = orderRow.get("hltcl");
            dsDetailTable.setValue("hltcl", orderRow.get("hltcl"));

            String hlts = orderRow.get("hlts").equals("")?"0":orderRow.get("hlts");
            double dhlts = Double.parseDouble(hlts);
            if(max<dhlts)
              max=dhlts;//2004-4-13取最大回笼天数
            //bd=bd.add(new BigDecimal(hlts));

            double sl=Double.parseDouble(orderRow.get("sl").equals("")?"0":orderRow.get("sl"));//合同数量
            double stsl=Double.parseDouble(orderRow.get("stsl").equals("")?"0":orderRow.get("stsl"));//实出库数量
            double skdsl=Double.parseDouble(orderRow.get("skdsl").equals("")?"0":orderRow.get("skdsl"));//实开单数量
            double hssl = Double.parseDouble(orderRow.get("hssl").equals("")?"0":orderRow.get("hssl"));
            double xhssl = hssl*(sl-skdsl)/sl;
            double xsj = Double.parseDouble(orderRow.get("xsj").equals("")?"0":orderRow.get("xsj"));
            double dj = Double.parseDouble(orderRow.get("dj").equals("")?"0":orderRow.get("dj"));
            double zk = Double.parseDouble(orderRow.get("zk").equals("")?"0":orderRow.get("zk"));

            dsDetailTable.setValue("hssl", String.valueOf(hssl));
            dsDetailTable.setValue("sl", String.valueOf(sl));
            dsDetailTable.setValue("xsje",formatNumber(String.valueOf(xsj*(sl)), sumFormat) );
            dsDetailTable.setValue("jje", formatNumber(String.valueOf(dj*(sl)), sumFormat) );


            dsDetailTable.setValue("xsj", orderRow.get("xsj"));
            dsDetailTable.setValue("zk", orderRow.get("zk"));
            dsDetailTable.setValue("dj", orderRow.get("dj"));
            dsDetailTable.setValue("bz", orderRow.get("bz"));
            //dsDetailTable.setValue("sthssl", saleRow.get("sthssl"));
            //dsDetailTable.setValue("stsl", saleRow.get("stsl"));
            dsDetailTable.post();
            //创建一个与用户相对应的行
            detailrow = new RowMap(dsDetailTable);
            d_RowInfos.add(detailrow);
          }
        }
        String ts = m_RowInfo.get("hkts");
        String kdrq = m_RowInfo.get("kdrq");
        Date startdate = new SimpleDateFormat("yyyy-MM-dd").parse(kdrq);//提单的开单日期
        Date enddate = new Date(startdate.getTime() + (long)max*(60*60*24*1000));//毫秒
        if(ts.equals(""))
        {
          m_RowInfo.put("hkts",String.valueOf((int)max));
          m_RowInfo.put("hkrq",new SimpleDateFormat("yyyy-MM-dd").format(enddate));
        }
      }
    }
  }
  /**
   *对应页面引入合同货物操作
   * 一次可以引入多张合同的货物
   * */
  class Multi_Product_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String importOrder = m_RowInfo.get("importOrderproduct");
      if(importOrder.length() == 0)
        return;
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwID");
      String[] hthwIDs = parseString(importOrder,",");//解析出合同货物ID数组
      //ImportOrderProduct b_ImportOrderToBackLadingBean =ImportOrderProduct.getInstance(req);
      // b_ImportThOrderBean =getb_ImportThOrderBean(req); //退货
      for(int i=0; i < hthwIDs.length; i++)
      {
        if(hthwIDs[i].equals("-1"))
          continue;
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String tdid = dsMasterTable.getValue("tdid");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, hthwIDs[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap saleRow =null;

          saleRow = b_ImportOrderToBackLadingBean.getLookupRow(hthwIDs[i]);
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("tdhwid", "-1");
          dsDetailTable.setValue("tdid", tdid);
          dsDetailTable.setValue("cpid", saleRow.get("cpid"));
          dsDetailTable.setValue("hthwid", hthwIDs[i]);
          dsDetailTable.setValue("wzdjid", saleRow.get("wzdjid"));
          String dmsxid = saleRow.get("dmsxid");
          dsDetailTable.setValue("dmsxid", dmsxid);
          String cjtcl = saleRow.get("cjtcl");
          dsDetailTable.setValue("cjtcl", cjtcl);

          dsDetailTable.setValue("hlts", saleRow.get("hlts"));
          String jzj = saleRow.get("jzj");
          dsDetailTable.setValue("jzj", saleRow.get("jzj"));
          String jxts = saleRow.get("jxts");
          dsDetailTable.setValue("jxts", saleRow.get("jxts"));
          String hltcl = saleRow.get("hltcl");
          dsDetailTable.setValue("hltcl", saleRow.get("hltcl"));

          double sl=Double.parseDouble(saleRow.get("sl").equals("")?"0":saleRow.get("sl"));
          double hssl = Double.parseDouble(saleRow.get("hssl").equals("")?"0":saleRow.get("hssl"));
          if(djlx.equals("-1"))
          {
            dsDetailTable.setValue("hssl", String.valueOf(-hssl));
            dsDetailTable.setValue("sl", "-"+saleRow.get("sl"));
            dsDetailTable.setValue("xsje",formatNumber("-"+saleRow.get("xsje"), sumFormat)  );
            dsDetailTable.setValue("jje",formatNumber("-"+saleRow.get("jje"), sumFormat) );
          }else
          {
            dsDetailTable.setValue("hssl", String.valueOf(hssl));
            dsDetailTable.setValue("sl", saleRow.get("sl"));
            dsDetailTable.setValue("xsje",formatNumber(saleRow.get("xsje"), sumFormat) );
            dsDetailTable.setValue("jje",formatNumber(saleRow.get("jje"), sumFormat) );
          }
          dsDetailTable.setValue("xsj", saleRow.get("xsj"));
          dsDetailTable.setValue("zk", saleRow.get("zk"));
          dsDetailTable.setValue("dj", saleRow.get("dj"));
          dsDetailTable.setValue("bz", saleRow.get("bz"));
          //dsDetailTable.setValue("sthssl", saleRow.get("sthssl"));
          //dsDetailTable.setValue("stsl", saleRow.get("stsl"));
          dsDetailTable.post();

          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
    }
  }
  /**
   *
   *
   * */
  class Promotion_Product_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String importcpids = m_RowInfo.get("cpids");
      String dwtxid = m_RowInfo.get("dwtxid");
      String storeid = m_RowInfo.get("storeid");
      if(importcpids.length() == 0)
        return;
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cpid");
      String[] cpids = parseString(importcpids,",");//解析出合同货物ID数组
      Select_Promotion_Product select_Promotion_ProductBean =Select_Promotion_Product.getInstance(req);
      for(int i=0; i < cpids.length; i++)
      {
        if(cpids[i].equals("-1"))
          continue;
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String tdid = dsMasterTable.getValue("tdid");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, cpids[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap saleRow =null;
          saleRow = select_Promotion_ProductBean.getLookupRow(cpids[i]);
          String dwid = saleRow.get("dwtxid");
          if(!dwtxid.equals(dwid))
            return;//单位不一致
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("tdhwid", "-1");
          dsDetailTable.setValue("tdid", tdid);
          dsDetailTable.setValue("cpid", cpids[i]);
          dsDetailTable.setValue("xsj", saleRow.get("prom_price"));
          dsDetailTable.setValue("zk", "100");
          dsDetailTable.setValue("dj", saleRow.get("prom_price"));
          dsDetailTable.setValue("wzdjid", saleRow.get("wzdjid"));

          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
    }
  }
  /**
   *改变购货单位时引发的操作
   * */
  class Dwtxid_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP);
      engine.project.LookUp creditBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP_CREDIT);
      String olddwtxId=m_RowInfo.get("dwtxid");
      putDetailInfo(data.getRequest());//保存输入的明细信息
      String dwtxid=m_RowInfo.get("dwtxid");
      RowMap corRow = corpBean.getLookupRow(dwtxid);
      creditBean.regData(new String[]{dwtxid});
      RowMap creditRow = creditBean.getLookupRow(dwtxid);
      if(olddwtxId.equals(dwtxid))
        return;
      else
      {
        m_RowInfo.put("dwtxid",dwtxid);
        m_RowInfo.put("kdrq",req.getParameter("kdrq"));
        m_RowInfo.put("tdbh",req.getParameter("tdbh"));
        m_RowInfo.put("personid",corRow.get("personid"));
        m_RowInfo.put("deptid",corRow.get("deptid"));
        m_RowInfo.put("hkts",creditRow.get("hkts"));
        Date startdate = new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("kdrq"));
        long tqq = Long.parseLong(creditRow.get("hkts").equals("")?"0":creditRow.get("hkts"));
        Date enddate = new Date(startdate.getTime() + tqq*60*60*24*1000);
        String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
        m_RowInfo.put("hkrq",endDate);
        dsDetailTable.empty();
        d_RowInfos.clear();
      }
    }
  }
  /**
   * 退货类型改变
   *
   * */
  class Thlx_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      String oldthlx=m_RowInfo.get("thlx");
      putDetailInfo(data.getRequest());//保存输入的明细信息
      String thlx=m_RowInfo.get("thlx");
      if(oldthlx.equals(thlx))
        return;
      else if(dsDetailTable.getRowCount()>0)
      {
        dsDetailTable.deleteAllRows();
        dsDetailTable.post();
        d_RowInfos.clear();
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
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      if(String.valueOf(DETAIL_DEL).equals(action))
      {
        d_RowInfos.remove(rownum);
        dsDetailTable.goToRow(rownum);
        dsDetailTable.deleteRow();
      }
      else if(String.valueOf(CYQK_DEL).equals(action))
      {
        d_TdcyqkRowInfos.remove(rownum);
        dsTdcyqkTable.goToClosestRow(rownum);
        dsTdcyqkTable.deleteRow();
        //activetab="SetActiveTab(INFO_EX,'INFO_EX_0')";
        //data.setMessage(showJavaScript(activetab));
      }
      /*
      else if(String.valueOf(QTFY_DEL).equals(action))
      {
        d_TdqtfyRowInfos.remove(rownum);
        dsTdqtfyTable.goToClosestRow(rownum);
        dsTdqtfyTable.deleteRow();
        activetab="SetActiveTab(INFO_EX,'INFO_EX_1')";
        data.setMessage(showJavaScript(activetab));
      }*/
    }
  }
  /***物资多选**/
  class Detail_Multi_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      String importOrder = m_RowInfo.get("wzdjid");
      if (importOrder.equals("undefined-1"))
        return;
      String dwtxid = m_RowInfo.get("dwtxid");
      String storeid = m_RowInfo.get("storeid");
      if(importOrder.length() == 0)
        return;
      String alert="";
      RowMap detailrow = null;
      if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
      String tdid = dsMasterTable.getValue("tdid");

      String GRADE_SQL =  combineSQL(CUST_GRADE_SQL,"?",new String[]{fgsid,dwtxid});
      String xydj = dataSetProvider.getSequence(GRADE_SQL);
      String sql = combineSQL(CUST_SALE_GOODS_STORE_SQL,"?",new String[]{storeid,fgsid,storeid,storeid,fgsid,storeid,storeid,fgsid,dwtxid,xydj});
      String SQL = "select p.cpid,p.wzdjid,p.mrzk,p.price from ("+sql+")p where p.wzdjid IN("+importOrder+")";
      EngineDataSet tmp = new EngineDataSet();
      setDataSetProperty(tmp,SQL);
      tmp.open();

      String pricesql = "select p.price from ("+sql+")p where p.wzdjid =";
      String zksql = "select p.mrzk from ("+sql+")p where p.wzdjid=";

      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wzdjid");
      String[] wzdjids = parseString(importOrder,",");//解析出合同货物ID数组
      tmp.first();
      for(int i=0; i < tmp.getRowCount(); i++)
      {
        String wzdjid = tmp.getValue("wzdjid");//wzdjids[i];
        String xsj = tmp.getValue("price");
        String mrzk = tmp.getValue("mrzk");
        String cpid = tmp.getValue("cpid");
        locateGoodsRow.setValue(0, wzdjid);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          String cnn = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_promotion t,xs_wzdj b WHERE t.cpid=b.cpid and sysdate>t.startdate AND sysdate<=t.enddate AND t.dwtxid='"+dwtxid+"' AND b.wzdjid='"+wzdjid+"' ");
          if(cnn!=null&&!cnn.equals("0"))
          {
            alert="'所选产品在促销中'";
            continue;
          }
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("tdhwid", "-1");
          dsDetailTable.setValue("tdid", tdid);
          dsDetailTable.setValue("wzdjid", wzdjid);
          if(xsj==null||xsj.equals(""))
            continue;
          if(mrzk==null)
            mrzk="";
          dsDetailTable.setValue("xsj", xsj);
          dsDetailTable.setValue("zk", mrzk);
          dsDetailTable.setValue("cpid", cpid);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
        tmp.next();
      }
      if(alert.length()>0)
        data.setMessage(showJavaScript("alert('促销中产品没引入!')"));
    }
  }
  /**
   *  从表数量为空的行删除操作
   */
  class Detail_Delete_Null implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      delRows();
    }
    /**
     * 删除
     * */
    public void delRows() throws Exception
    {
      for(int i=0;i<d_RowInfos.size();i++)
      {
        RowMap detailRow = (RowMap)d_RowInfos.get(i);
        String sl = detailRow.get("sl");
        if(sl.equals(""))
        {
          d_RowInfos.remove(i);
          delRows();
        }
      }
    }
  }
  /**
   * 提货单引用
   * 对应引入合同
   * 得到用于查找合同编号的bean
   * @param req WEB的请求
   * @return 返回用于查找合同编号的bean
   */
  public B_ImportOrderToBackLading getb_ImportOrderBean(HttpServletRequest req)
  {
    if(b_ImportOrderToBackLadingBean == null)
      b_ImportOrderToBackLadingBean = B_ImportOrderToBackLading.getInstance(req);
    return b_ImportOrderToBackLadingBean;
  }
  public B_ImportPromotionToBackLading getb_ImportPromotionBean(HttpServletRequest req)
  {
    if(promotionToBackLadingBean == null)
      promotionToBackLadingBean = B_ImportPromotionToBackLading.getInstance(req);
    return promotionToBackLadingBean;
  }

  /**
   * 得到用于查找产品单价的bean
   * @param req WEB的请求
   * @return 返回用于查找产品单价的bean
   */
  public LookUp getSalePriceBean(HttpServletRequest req)
  {
    if(salePriceBean == null)
      salePriceBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_SALE_PRICE);
    return salePriceBean;
  }
}
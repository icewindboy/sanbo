package engine.erp.produce;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;
import engine.html.*;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import engine.erp.produce.Select_Trackbill_of_lading;
import engine.erp.produce.Select_Trackbilldetail_of_lading;
import com.borland.dx.dataset.*;
import javax.swing.*;
import engine.util.StringUtils;
public final class B_SC_TrackBill extends BaseAction implements Operate
{
  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_track_bill WHERE 1<>1";   //取主表的结构
  private static final String MASTER_SQL    = "SELECT * FROM sc_track_bill where 1=1 ? ";    //对主表的参数化查询
  /*以下是对从表的条件查询,需提供ypxxID参数.*/
  private static final String DE_SQL    = "SELECT * FROM sc_track_detail WHERE track_bill_ID = ";//从表信息
  private static final String DETAIL_SQL    = "SELECT * FROM sc_track_dispart WHERE track_bill_ID = ";//从表的从表
   private static final String DETAIL_DETAIL_SQL    = "SELECT * FROM sc_track_dispart WHERE ? ? ";
  private static final String TDMX_SQL      = "SELECT sc_drawMaterialDetail.*,sc_drawMaterial.*,emp.xm FROM emp,sc_drawMaterialDetail,sc_drawMaterial  WHERE  sc_drawMaterialDetail.drawID=sc_drawMaterial.drawID AND emp.personid=sc_drawMaterial.approveID AND drawDetailID= ";//

  private static final String Panduan_SQL   = "SELECT sc_track_type.type_prop,sc_track_proc.track_type_ID,sc_track_proc.track_type,sc_track_proc.gymcID  FROM sc_track_proc,sc_track_type WHERE sc_track_proc.track_type_id= sc_track_type.track_type_id AND sc_track_type.track_type_ID =  ";
  //操作
  private static final String GX_SQL = " SELECT t.*,g.xm FROM ( "
                                     +" SELECT d.receivedetailid, d.cpid, d.gx ,d.dmsxid,d.batchNo ,e.creator,e.handleperson,e.approveid "
                                     +" FROM sc_receiveproddetail d,sc_receiveprod e WHERE d.receiveid=e.receiveid AND d.gx IS NOT NULL "
                                     +"  UNION ALL "
                                     +"  SELECT d.receivedetailid, d.cpid, d.gx2 gx,d.dmsxid,d.batchNo,e.creator,e.handleperson,e.approveid "
                                     +" FROM sc_receiveproddetail d,sc_receiveprod e WHERE d.receiveid=e.receiveid  AND d.gx2  IS NOT NULL "
                                     +" UNION ALL "
                                     +" SELECT d.receivedetailid, d.cpid, d.gx3 gx,d.dmsxid,d.batchNo,e.creator,e.handleperson,e.approveid "
                                     +"  FROM sc_receiveproddetail d,sc_receiveprod e WHERE d.receiveid=e.receiveid  AND d.gx3  IS NOT NULL "
                                     +"  ) t,emp g where g.personid=t.approveid AND receivedetailid= ";//引入各个工序的信息
  private static final String get_jsgs_SQL="SELECT kc_dm.scdwgs,kc_dm.hsbl FROM kc_dm WHERE kc_dm.cpid= ";
  private static final String get_guige_SQL="SELECT  kc_dmsx.sxz FROM kc_dmsx WHERE kc_dmsx.dmsxid= ";
  //操作
  private String qtyFormat = null;
  public  static final String PANDUAN = "888784216987";
  public  static final String DETAIL_SALE_ADD = "10801";
  public static final String Disp_ADD    = "9000";    //分切新增
  public static final String Disp_DEL    = "9001";    //分切删除操作
  public static final String GETID    = "9002";
  public  static final String INVOICE_OVER = "3423513";//完成
  public  static final String OVER = "3423513";
  public  static final String GX_ADD="895623";
  //public String activetab = "SetActiveTab(INFO_EX,'INFO_EX_0')";//从表当前的div
  public static final String COUNT = "18888";
  public static final String VIEW_DETAIL = "1055";   //主从明细
  public static final String OPERATE_SEARCH = "1066";//主表查询操作
  public static final String DELETE_RETURN = "1067"; //主从删除操作
  public static final String POST_FEQIE= "1068"; //只把从表的从表的信息放在ROWINOF中，不保存
  public static final String All_Disp_DEL="65656";
  /*主从表的数据集*/
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表培训课程
  private EngineDataSet dsDetail_detail_Table  = new EngineDataSet();//从表的从表
  private  String syskey="";
  private  String widthSyskey = "";//解析时用到的参数
  private  String lengthSyskey = "";//解析时用到的参数
  private boolean isMasterAdd = true;                          //主表是否在添加状态
  private long    masterRow = 0;
  public int  detailRow = 0;
  //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap();                 //主表添加行或修改行的引用
  /*把从表数据放在ArrayList中,这样可以保存多行信息来对应主表*/
  public ArrayList arraylist_de  = null;//从表
  public ArrayList arraylist_detail  = null;//从表的从表
  private boolean isInitQuery = false;                         //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  private boolean isPutong= false;
  private boolean isDitu = false;
  private boolean isFenqie = false;
  private boolean isHengqie = true;
  public boolean hasHengqiepost = false;
  public boolean hasleixingchang = false;
  public boolean fenqieadd = false;
  private String track_bill_ID = null;
  private String track_type_ID = null;
  private String track_detail_ID = null;
  public String Fenqie_track_detail_ID= null;
  public String master_track_bill_ID= null;
  public String dispart_ID= null;
  public String storeid= null;
  public String cpid= null;
  //手动设置表主键
  int s_track_detail_ID=0;
  int s_track_bill_ID=0;
  int s_dispart_ID=0;
  public float all_disp_weight=0;
  private Select_Trackbill_of_lading ladingtrackBill_ProductBean = null; //提单货物引用
  /**
   * 生产跟踪单的实例
   * @param request jsp请求
   * @return 返回生产跟踪单列表的实例
   */
  public static B_SC_TrackBill getInstance(HttpServletRequest request)
  {
    B_SC_TrackBill b_TrackbillBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_TrackbillBean";
      b_TrackbillBean = (B_SC_TrackBill)session.getAttribute(beanName);
      if(b_TrackbillBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        b_TrackbillBean = new B_SC_TrackBill();
        session.setAttribute(beanName, b_TrackbillBean);
        b_TrackbillBean.qtyFormat = loginBean.getQtyFormat();
        b_TrackbillBean.syskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");
        b_TrackbillBean.widthSyskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");
        b_TrackbillBean.lengthSyskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_LENGTH");
        b_TrackbillBean.dsDetailTable.setColumnFormat("tot_ull", b_TrackbillBean.qtyFormat);
        b_TrackbillBean.dsDetailTable.setColumnFormat("tot_ratio", b_TrackbillBean.qtyFormat);
        b_TrackbillBean.dsDetailTable.setColumnFormat("good_ratio", b_TrackbillBean.qtyFormat);
        b_TrackbillBean.dsDetailTable.setColumnFormat("hypo_ratio", b_TrackbillBean.qtyFormat);
        b_TrackbillBean.dsDetailTable.setColumnFormat("waster_ratio", b_TrackbillBean.qtyFormat);

      }
    }
    return b_TrackbillBean;
  }

  /**
   * 构造函数
   */
  private B_SC_TrackBill()
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
    setDataSetProperty(dsMasterTable, combineSQL(MASTER_SQL,"?",new String[]{""}));
    setDataSetProperty(dsDetailTable, null);  //从表应聘人员工作经历
    setDataSetProperty(dsDetail_detail_Table, null);
    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"track_bill_ID"}, new String[]{"S_SC_TRACK_BILL"}));
   /* dsDetail_detail_Table.setMasterLink(new MasterLinkDescriptor(dsDetailTable,
        new String[]{"track_detail_ID", "track_bill_ID"},
        new String[]{"track_detail_ID", "track_bill_ID"}, false, true, true));//设置主从从关联*/
    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());//初始化
    addObactioner(String.valueOf(OPERATE_SEARCH), new Master_Search());//定制查询
    addObactioner(String.valueOf(Disp_ADD), new Disp_detail_add());//新增删除操作
    addObactioner(String.valueOf(Disp_DEL), new Disp_detail_add());//新增删除操作
    addObactioner(String.valueOf(All_Disp_DEL), new Disp_detail_add());//分切全删除操作
    addObactioner(String.valueOf(GETID), new get_detail_id());//新增删除操作
    addObactioner(String.valueOf(POST_FEQIE), new POST_Fenqie());
    addObactioner(String.valueOf(COUNT), new GetCount());
    addObactioner(String.valueOf(VIEW_DETAIL), masterAddEdit);//修改主表,及其对应的从表
    addObactioner(String.valueOf(DELETE_RETURN), new Master_Delete());//删除主表某一行,及其对应的从表
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(INVOICE_OVER), new Invoice_Over());//完成
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(DETAIL_SALE_ADD), new Detail_Lading_ADD());
    addObactioner(String.valueOf(GX_ADD), new Detail_GX_ADD());
    addObactioner(String.valueOf(PANDUAN), new Leixing_Change());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
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
      dsMasterTable.closeDataSet();
      dsMasterTable = null;
    }
    if(dsDetailTable != null){
      dsDetailTable.closeDataSet();
      dsDetailTable = null;
    }
    if(dsDetail_detail_Table != null){
      dsDetail_detail_Table.closeDataSet();
      dsDetail_detail_Table = null;
    }

      log = null;
      m_RowInfo = null;
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
   * 保存继续
   * 主表新增
   * 主表编辑
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否是主表
    if(isMaster)
    {
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();
      if(!isAdd)
        m_RowInfo.put(getMaterTable());

    }
    else{
      openDetailTable();
      arraylist_de=putDetailToArraylist(dsDetailTable,arraylist_de);

    }
  }

  /**
   * 把从表数据集数据推入到ArrayList中
   */
  private final ArrayList putDetailToArraylist(EngineDataSet dsDetail,ArrayList arrlist)
  {
    arrlist = new ArrayList(dsDetail.getRowCount());
    dsDetail.first();
    for(int i=0; i<dsDetail.getRowCount(); i++)
    {
      RowMap row = new RowMap(dsDetail);
      arrlist.add(row);
      dsDetail.next();
    }
    return arrlist;
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
    rowInfo.put(request);

    int rownum=arraylist_de.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)arraylist_de.get(i);

      detailRow.put("dmsxID", rowInfo.get("dmsxid_"+i));//
      detailRow.put("cpId", rowInfo.get("cpid_"+i));//
      detailRow.put("scdwgs",rowInfo.get("scdwgs_"+i));
      detailRow.put("prod_batno", rowInfo.get("prod_batno_"+i));//
      detailRow.put("prod_date", rowInfo.get("prod_date_"+i));//
      detailRow.put("prod_ratio", rowInfo.get("prod_ratio_"+i));//
      detailRow.put("qual_validate", rowInfo.get("qual_validate_"+i));//
      detailRow.put("dope_one", rowInfo.get("dope_one_"+i));//
      detailRow.put("dope_two", rowInfo.get("dope_two_"+i));//

      detailRow.put("weight", rowInfo.get("weight_"+i));//
      detailRow.put("length", rowInfo.get("length_"+i));//


      detailRow.put("weight2", rowInfo.get("weight2_"+i));//

      detailRow.put("length2", rowInfo.get("length2_"+i));//
      detailRow.put("return_num", rowInfo.get("return_num_"+i));//

      detailRow.put("old_break", rowInfo.get("old_break_"+i));//

      detailRow.put("new_break", rowInfo.get("new_break_"+i));//

      detailRow.put("plan_length", rowInfo.get("plan_length_"+i));//


      detailRow.put("plan_weigth", rowInfo.get("plan_weigth_"+i));//

      detailRow.put("fact_length", rowInfo.get("fact_length_"+i));//

      detailRow.put("fact_weight", rowInfo.get("fact_weight_"+i));//

      detailRow.put("award", rowInfo.get("ward_"+i));//


      detailRow.put("ull_reason", rowInfo.get("ull_reason_"+i));//

      detailRow.put("qual_desc", rowInfo.get("qual_desc_"+i));//

      detailRow.put("end_side", rowInfo.get("end_side_"+i));//

      detailRow.put("machine_no", rowInfo.get("machine_no_"+i));//

      detailRow.put("team_no", rowInfo.get("team_no_"+i));//

      detailRow.put("handler", rowInfo.get("handler_"+i));//


      detailRow.put("creator", rowInfo.get("creator_"+i));//

      detailRow.put("approver", rowInfo.get("approver_"+i));//

      detailRow.put("ok_sheet1", rowInfo.get("ok_sheet1_"+i));//
      detailRow.put("ok_sheet2", rowInfo.get("ok_sheet2_"+i));//

      detailRow.put("ok_sheet3 ", rowInfo.get("ok_sheet3_"+i));//
      detailRow.put("ok_weight1", rowInfo.get("ok_weight1_"+i));//
      detailRow.put("ok_weight2", rowInfo.get("ok_weight2_"+i));//
      detailRow.put("ok_weight3", rowInfo.get("ok_weight3_"+i));//
      detailRow.put("hypo_sheet1", rowInfo.get("hypo_sheet1_"+i));//

      detailRow.put("hypo_sheet2", rowInfo.get("hypo_sheet2_"+i));//
      detailRow.put("hypo_sheet3", rowInfo.get("hypo_sheet3_"+i));//
      detailRow.put("hypo_weight1", rowInfo.get("hypo_weight1_"+i));//
      detailRow.put("hypo_weight2", rowInfo.get("hypo_weight2_"+i));//
      detailRow.put("hypo_weight3", rowInfo.get("hypo_weight3_"+i));//

      detailRow.put("wast_sheet1", rowInfo.get("wast_sheet1_"+i));//
      detailRow.put("wast_sheet2", rowInfo.get("wast_sheet2_"+i));//
      detailRow.put("wast_sheet3", rowInfo.get("wast_sheet3_"+i));//
      detailRow.put("wast_weight1", rowInfo.get("wast_weight1_"+i));//

      detailRow.put("wast_weight2", rowInfo.get("wast_weight2_"+i));//
      detailRow.put("wast_weight3", rowInfo.get("wast_weight3_"+i));//
      detailRow.put("disp_width1", rowInfo.get("disp_width1_"+i));//
      detailRow.put("disp_width2", rowInfo.get("disp_width2_"+i));//
      detailRow.put("disp_width3", rowInfo.get("disp_width3_"+i));//
      detailRow.put("disp_width4", rowInfo.get("disp_width4_"+i));//
      detailRow.put("disp_width5", rowInfo.get("disp_width5_"+i));//
      detailRow.put("disp_width6", rowInfo.get("disp_width6_"+i));//
      detailRow.put("disp_width7", rowInfo.get("disp_width7_"+i));//
      detailRow.put("disp_width8", rowInfo.get("disp_width8_"+i));//
      detailRow.put("disp_width9", rowInfo.get("disp_width9_"+i));//

      arraylist_de.set(i,detailRow);
    }
  }

  private final void putDetail_DetailInfo(HttpServletRequest request)
  {
    RowMap detailrowInfo = getMasterRowinfo();
    detailrowInfo.put(request);
    int row_de_num=arraylist_detail.size();
    RowMap detail_de_Row = null;
    for(int j=0; j<row_de_num; j++)
    {
      detail_de_Row = (RowMap)arraylist_detail.get(j);
      detail_de_Row.put("disp_weight", detailrowInfo.get("disp_weight_"+j));//
      all_disp_weight=all_disp_weight+Float.parseFloat(detail_de_Row.get("disp_weight"));
      detail_de_Row.put("disp_weight1", detailrowInfo.get("disp_weight1_"+j));//
      String a=detail_de_Row.get("disp_weight1");
      detail_de_Row.put("disp_weight2", detailrowInfo.get("disp_weight2_"+j));//
      detail_de_Row.put("disp_weight3", detailrowInfo.get("disp_weight3_"+j));//
      detail_de_Row.put("disp_weight4", detailrowInfo.get("disp_weight4_"+j));//
      detail_de_Row.put("disp_weight5", detailrowInfo.get("disp_weight5_"+j));//
      detail_de_Row.put("disp_weight6", detailrowInfo.get("disp_weight6_"+j));//
      detail_de_Row.put("disp_weight7", detailrowInfo.get("disp_weight7_"+j));//
      detail_de_Row.put("disp_weight8", detailrowInfo.get("disp_weight8_"+j));//
      detail_de_Row.put("disp_weight9", detailrowInfo.get("disp_weight9_"+j));//
      detail_de_Row.put("disp_lenth1", detailrowInfo.get("disp_lenth1_"+j));//

      detail_de_Row.put("disp_lenth2", detailrowInfo.get("disp_lenth2_"+j));//
      detail_de_Row.put("disp_lenth3", detailrowInfo.get("disp_lenth3_"+j));//
      detail_de_Row.put("disp_lenth4", detailrowInfo.get("disp_lenth4_"+j));//
      detail_de_Row.put("disp_lenth5", detailrowInfo.get("disp_lenth5_"+j));//
      detail_de_Row.put("disp_lenth6", detailrowInfo.get("disp_lenth6_"+j));//
      detail_de_Row.put("disp_lenth7", detailrowInfo.get("disp_lenth7_"+j));//
      detail_de_Row.put("disp_lenth8", detailrowInfo.get("disp_lenth8_"+j));//
      detail_de_Row.put("disp_lenth9", detailrowInfo.get("disp_lenth9_"+j));//
      detail_de_Row.put("cust_code1", detailrowInfo.get("cust_code1_"+j));//

      detail_de_Row.put("cust_code2", detailrowInfo.get("cust_code2_"+j));//
      detail_de_Row.put("cust_code3", detailrowInfo.get("cust_code3_"+j));//
      detail_de_Row.put("cust_code4", detailrowInfo.get("cust_code4_"+j));//
      detail_de_Row.put("cust_code5", detailrowInfo.get("cust_code5_"+j));//
      detail_de_Row.put("cust_code6", detailrowInfo.get("cust_code6_"+j));//
      detail_de_Row.put("cust_code7", detailrowInfo.get("cust_code7_"+j));//
      detail_de_Row.put("cust_code8", detailrowInfo.get("cust_code8_"+j));//
      detail_de_Row.put("cust_code9", detailrowInfo.get("cust_code9_"+j));//
      detail_de_Row.put("batch_no1", detailrowInfo.get("batch_no1_"+j));//

      detail_de_Row.put("batch_no2", detailrowInfo.get("batch_no2_"+j));//
      detail_de_Row.put("batch_no3", detailrowInfo.get("batch_no3_"+j));//
      detail_de_Row.put("batch_no4", detailrowInfo.get("batch_no4_"+j));//
      detail_de_Row.put("batch_no5", detailrowInfo.get("batch_no5_"+j));//
      detail_de_Row.put("batch_no6", detailrowInfo.get("batch_no6_"+j));//
      detail_de_Row.put("batch_no7", detailrowInfo.get("batch_no7_"+j));//
      detail_de_Row.put("batch_no8", detailrowInfo.get("batch_no8_"+j));//
      detail_de_Row.put("batch_no9", detailrowInfo.get("batch_no9_"+j));//
      arraylist_detail.set(j,detail_de_Row);
    }

  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getdetailTable(){return dsDetailTable;}
  public final EngineDataSet getdetail_de_Table(){return dsDetail_detail_Table;}



/*打开从表*/
  public final void openDetailTable() throws Exception
  {

    dsDetailTable.setQueryString(DE_SQL +"'"+track_bill_ID+"'");
    if(dsDetailTable.isOpen())
      dsDetailTable.refresh();
    else
      dsDetailTable.open();


  }
   public final void openDetaildetailTable() throws Exception{

      dsDetail_detail_Table.setQueryString(combineSQL(DETAIL_DETAIL_SQL,"?",new String[]{"track_bill_ID="+"'"+track_bill_ID+"'","and track_detail_ID="+"'"+track_detail_ID+"'"}));
     if(dsDetail_detail_Table.isOpen())
       dsDetail_detail_Table.refresh();
     else
      dsDetail_detail_Table.open();
   }


  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到应聘人员工作经历从表的多行信息*/
  public final RowMap[] getDeRowinfos() {
    RowMap[] De_rows = new RowMap[arraylist_de.size()];
    arraylist_de.toArray(De_rows);
    return De_rows;
  }
  public final RowMap[] getDetailRowinfos() {
    RowMap[] Detail_rows = new RowMap[arraylist_detail.size()];
    arraylist_detail.toArray(Detail_rows);
    return Detail_rows;
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
   *
   *初始化从表信息
   */
  public void initArrayList()
  {
    if(arraylist_detail!=null){
      if(arraylist_detail.size()>0)
      {
        arraylist_detail.clear();
      }
    }
    if(arraylist_de!=null){
      if(arraylist_de.size()>0)
      {
        arraylist_de.clear();
      }
    }
  }
  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      HttpServletRequest request = data.getRequest();

      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      //初始化时清空数据集
      if(dsMasterTable.isOpen() && dsMasterTable.getRowCount() > 0)
      dsMasterTable.empty();
      dsMasterTable.setQueryString(combineSQL(MASTER_SQL,"?",new String[]{""}));
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
      isMasterAdd = String.valueOf(ADD).equals(action);
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        track_bill_ID = dsMasterTable.getValue("track_bill_ID");
        initRowInfo(true, false, true);
        initRowInfo(false, false, true);
      }
      if(isMasterAdd){
        track_bill_ID =String.valueOf(--s_track_detail_ID);
        initRowInfo(false, isMasterAdd, true);
        initRowInfo(true, isMasterAdd, true);
        m_RowInfo.clear();
        initArrayList();
      }
      //打开从表
      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
   * 完成
   */
  class Invoice_Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      dsMasterTable.setValue("state","8");

      dsMasterTable.saveChanges();
      initRowInfo(true,false,false);
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //把网页中的从表的信息推入ArrayList中
      putDetailInfo(data.getRequest());


      EngineDataSet ds = getMaterTable();

      //校验
      RowMap rowInfo = getMasterRowinfo();
      track_type_ID= rowInfo.get("track_type_ID");
      if (track_type_ID.equals(""))
      {
        data.setMessage(showJavaScript("alert('跟踪单类型必须选择!')"));
        return;
      }
      String dwtxId= rowInfo.get("dwtxId");
      if (dwtxId.equals(""))
      {
        data.setMessage(showJavaScript("alert('供应商必须选择!')"));
        return;
      }

      String storeid=rowInfo.get("storeid");
      if (storeid.equals(""))
      {
        data.setMessage(showJavaScript("alert('仓库名称必须选择!')"));
        return;
      }
      String cpId=rowInfo.get("cpId");
      String dmsxID=rowInfo.get("dmsxID");
      if(cpId.equals("")||dmsxID.equals(""))
      {
        data.setMessage(showJavaScript("alert('领料产品必须选择!')"));
        return;
      }
      //
      if(isMasterAdd)
      {
        if (!hasHengqiepost){

        track_bill_ID =dataSetProvider.getSequence("S_SC_TRACK_BILL");
        ds.insertRow(false);
        ds.setValue("track_bill_ID",track_bill_ID);
        }
      }
      else
      {
        ds.goToInternalRow(masterRow);
        track_bill_ID = ds.getValue("track_bill_ID");

      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail_de = getdetailTable();

      detail_de.first();
      int b=arraylist_de.size();
      for(int i=0; i<arraylist_de.size(); i++)
      {
        detailrow = (RowMap)arraylist_de.get(i);
        //判断是否有执行分切从表保存操作，有就用分切的id，没有就去取Sequence
        if(detail_de.isNew(i)){
         if (!hasHengqiepost)
          detail_de.setValue("track_bill_ID", track_bill_ID);
        //判断是否有执行改变工序名称保存操作，有就用从表的id，没有就去取Sequence
        if(hasleixingchang){
          if (!(detail_de.getValue("track_type").equals("2")&&hasHengqiepost))

          detail_de.setValue("track_detail_ID",dataSetProvider.getSequence("S_SC_TRACK_DETAIL") );}}
          track_detail_ID = detail_de.getValue("track_detail_ID");

          detail_de.setValue("dmsxID", detailrow.get("dmsxID"));//
          detail_de.setValue("cpId", detailrow.get("cpId"));//
          detail_de.setValue("prod_batno", detailrow.get("prod_batno"));//
          detail_de.setValue("gymcID", detailrow.get("gymcID"));//
          detail_de.setValue("track_type", detailrow.get("track_type"));//
          detail_de.setValue("prod_date", detailrow.get("prod_date"));//
          detail_de.setValue("prod_ratio", detailrow.get("prod_ratio"));//
          detail_de.setValue("qual_validate", detailrow.get("qual_validate"));//
          detail_de.setValue("dope_one", detailrow.get("dope_one"));//
          detail_de.setValue("dope_two", detailrow.get("dope_two"));//
          detail_de.setValue("weight", detailrow.get("weight"));//
          detail_de.setValue("length", detailrow.get("length"));//

          detail_de.setValue("weight2", detailrow.get("weight2"));//
          detail_de.setValue("length2", detailrow.get("length2"));//
          detail_de.setValue("return_num", detailrow.get("return_num"));//
          detail_de.setValue("old_break", detailrow.get("old_break"));//
          detail_de.setValue("new_break", detailrow.get("new_break"));//

          detail_de.setValue("plan_length", detailrow.get("plan_length"));//
          detail_de.setValue("plan_weigth", detailrow.get("plan_weigth"));//
          detail_de.setValue("fact_length", detailrow.get("fact_length"));//
          detail_de.setValue("fact_weight", detailrow.get("fact_weight"));//
          detail_de.setValue("award", detailrow.get("award"));//

          detail_de.setValue("ull_reason", detailrow.get("ull_reason"));//
          detail_de.setValue("qual_desc", detailrow.get("qual_desc"));//
          detail_de.setValue("end_side", detailrow.get("end_side"));//
          detail_de.setValue("machine_no", detailrow.get("machine_no"));//
          detail_de.setValue("team_no", detailrow.get("team_no"));//
          detail_de.setValue("handler", detailrow.get("handler"));//

          detail_de.setValue("creator", detailrow.get("creator"));//
          detail_de.setValue("approver", detailrow.get("approver"));//

          detail_de.setValue("ok_sheet1", detailrow.get("ok_sheet1"));//
          detail_de.setValue("ok_sheet2", detailrow.get("ok_sheet2"));//

          detail_de.setValue("ok_sheet3", detailrow.get("ok_sheet3"));//
          detail_de.setValue("ok_weight1", detailrow.get("ok_weight1"));//
          detail_de.setValue("ok_weight2", detailrow.get("ok_weight2"));//
          detail_de.setValue("ok_weight3", detailrow.get("ok_weight3"));//
          detail_de.setValue("hypo_sheet1", detailrow.get("hypo_sheet1"));//

          detail_de.setValue("hypo_sheet2", detailrow.get("hypo_sheet2"));//
          detail_de.setValue("hypo_sheet3", detailrow.get("hypo_sheet3"));//
          detail_de.setValue("hypo_weight1", detailrow.get("hypo_weight1"));//
          detail_de.setValue("hypo_weight2", detailrow.get("hypo_weight2"));//
          detail_de.setValue("hypo_weight3", detailrow.get("hypo_weight3"));//

          detail_de.setValue("wast_sheet1", detailrow.get("wast_sheet1"));//
          detail_de.setValue("wast_sheet2", detailrow.get("wast_sheet2"));//
          detail_de.setValue("wast_sheet3", detailrow.get("wast_sheet3"));//
          detail_de.setValue("wast_weight1", detailrow.get("wast_weight1"));//

          detail_de.setValue("wast_weight2", detailrow.get("wast_weight2"));//
          detail_de.setValue("wast_weight3", detailrow.get("wast_weight3"));//
          detail_de.setValue("disp_width1", detailrow.get("disp_width1"));//

          detail_de.setValue("disp_width2", detailrow.get("disp_width2"));//
          detail_de.setValue("disp_width3", detailrow.get("disp_width3"));//
          detail_de.setValue("disp_width4", detailrow.get("disp_width4"));//
          detail_de.setValue("disp_width5", detailrow.get("disp_width5"));//
          detail_de.setValue("disp_width6", detailrow.get("disp_width6"));//
          detail_de.setValue("disp_width7", detailrow.get("disp_width7"));//
          detail_de.setValue("disp_width8", detailrow.get("disp_width8"));//
          detail_de.setValue("disp_width9", detailrow.get("disp_width9"));//
          detail_de.post();
          detail_de.next();
      }

      //保存主表数据

      ds.setValue("dwtxId", rowInfo.get("dwtxId"));//
      ds.setValue("track_type_ID",rowInfo.get("track_type_ID"));//
      ds.setValue("dmsxID",rowInfo.get("dmsxID"));//

      ds.setValue("storeid", rowInfo.get("storeid"));//
      ds.setValue("cpId", rowInfo.get("cpId"));//
      ds.setValue("track_bill_no", rowInfo.get("track_bill_no"));//
      ds.setValue("material_no", rowInfo.get("material_no"));//
      ds.setValue("checker", rowInfo.get("checker"));//
      ds.setValue("gross_weight", rowInfo.get("gross_weight"));//
      ds.setValue("net_weight", rowInfo.get("net_weight"));//
      ds.setValue("check_result", rowInfo.get("check_result"));//

      ds.setValue("handler", rowInfo.get("handler"));//
      ds.setValue("creator", rowInfo.get("creator"));//
      ds.setValue("approver", rowInfo.get("approver"));//
      ds.setValue("dyne_side", rowInfo.get("dyne_side"));//

      ds.setValue("hot_side", rowInfo.get("hot_side"));//
      ds.setValue("grade", rowInfo.get("grade"));//
      ds.setValue("tot_ull", rowInfo.get("tot_ull"));//
      ds.setValue("good_ratio", rowInfo.get("good_ratio"));//
      ds.setValue("hypo_ratio", rowInfo.get("hypo_ratio"));//
      ds.setValue("waster_ratio", rowInfo.get("waster_ratio"));//

      ds.setValue("type_prop", rowInfo.get("type_prop"));//
      ds.setValue("tot_ratio", rowInfo.get("tot_ratio"));//


      ds.setValue("drawID", rowInfo.get("drawID"));//
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail_de}, null);
      hasleixingchang=false;
      hasHengqiepost=false;
      if(String.valueOf(POST_CONTINUE).equals(action))
      {
        isMasterAdd = false;
        m_RowInfo.clear();
        initRowInfo(true,false,false);;//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
    }

  }
  /**
   * 主表删除操作
   */
  class Master_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getMaterTable();
      if(!action.equals(String.valueOf(DEL)))
      {
        //在主从明细里执行删除操作.
        if(isMasterAdd){
          data.setMessage(showJavaScript("backList();"));//主从表新增,还未保存时,
          return;
        }
        dsMasterTable.goToInternalRow(masterRow);
      }
      else
      {
        //在主表的列表里执行删除操作
        String rownum=data.getRequest().getParameter("rownum");
        ds.goToRow(Integer.parseInt(rownum));

      }
      String track_bill_ID = dsMasterTable.getValue("track_bill_ID");
      dsDetailTable.setQueryString(DE_SQL + "'"+track_bill_ID+"'");
       dsDetail_detail_Table.setQueryString(combineSQL(DETAIL_DETAIL_SQL,"?",new String[]{"track_bill_ID="+"'"+track_bill_ID+"'",""}));
       if(dsDetail_detail_Table.isOpen())
       dsDetail_detail_Table.refresh();
     else
        dsDetail_detail_Table.openDataSet();
      if(dsDetailTable.isOpen())
        dsDetailTable.refresh();
      else
        dsDetailTable.openDataSet();
      dsDetail_detail_Table.deleteAllRows();

      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable,dsDetail_detail_Table}, null);
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCE_TRACK_TYPE);

      if(action.equals(String.valueOf(DELETE_RETURN)))
      {
        data.setMessage(showJavaScript("backList();"));
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{ SQL});


      if(!dsMasterTable.getQueryString().equals(SQL))
      {
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);
      }
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
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("track_bill_no"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("track_type_ID"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("cpid"),  null, null, null, null, "="),
        new QueryColumn(master.getColumn("material_no"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("checker"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("gross_weight"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("net_weight"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("check_result"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("handler"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("creator"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("approver"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("dyne_side"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("hot_side"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("grade"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("tot_ull"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("tot_ratio"), null, null, null, null, "=")

      });
      isInitQuery = true;
    }
  }
  /**
   * 0号
   *
   *
   * */
  /**
   *引入领料单
   * */
  class Detail_GX_ADD implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());//保存输入的明细信息
      String rownum=data.getRequest().getParameter("rownum");
      int n = Integer.parseInt(rownum);
      String selectedtdid = m_RowInfo.get("selectedtdid");
      if(selectedtdid.equals(""))
      return;
      EngineDataSet GXTable = new EngineDataSet();
      setDataSetProperty(GXTable,GX_SQL+selectedtdid);
      GXTable.openDataSet();
      if(!isMasterAdd)
      {
        dsMasterTable.goToInternalRow(masterRow);
      }
      GXTable.first();
      RowMap GXRow=new RowMap(GXTable);
      RowMap  derow = (RowMap)arraylist_de.get(n);
      derow.put("cpId",GXRow.get("cpId"));
      derow.put("dmsxID",GXRow.get("dmsxID"));
      derow.put("prod_batno",GXRow.get("batchNo"));
      derow.put("handler",GXRow.get("handlePerson"));
      derow.put("creator",GXRow.get("creator"));
      derow.put("approver",GXRow.get("xm"));
      EngineDataSet dsJSGS=new EngineDataSet();
      EngineDataSet dsSXZ=new EngineDataSet();
      String tempcpid=derow.get("cpId");
      String tempdmsxID=derow.get("dmsxID");
      setDataSetProperty(dsJSGS,get_jsgs_SQL+tempcpid);
      setDataSetProperty(dsSXZ,get_guige_SQL+tempdmsxID);
      dsJSGS.openDataSet();
      dsSXZ.openDataSet();
      String scdwgs=dsJSGS.getValue("scdwgs");
      String sxz=dsSXZ.getValue("sxz");
      String length=derow.get("length");
      double length_1=0;
      double length_2=0;
      if(length.equals(""))
        length_1=0;
      else
        length_1=Double.parseDouble(length);
      String length2=derow.get("length2");
      if(length2.equals(""))
        length_2=0;
      else
        length_2=Double.parseDouble(length2);
      if (!derow.get("track_type").equals("2"))
      {
        String width=parseEspecialString(sxz, "()", false);//取宽度
        double weight=length_1*Double.parseDouble(width)/Double.parseDouble(scdwgs);
        double weight2=length_2*Double.parseDouble(width)/Double.parseDouble(scdwgs);
        String weight_1=String.valueOf(weight);
        String weight_2=String.valueOf(weight2);
        derow.put("weight",GXRow.get("weight_1"));
        derow.put("weight2",GXRow.get("weight_2"));
      }
      GXTable.closeDataSet();
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
      String[] code = StringUtils.parseString(s, sep);
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
  /**
   *引入自制收货单
   * */
  class Detail_Lading_ADD implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());//保存输入的明细信息

      String selectedtdid = m_RowInfo.get("selectedtdid");
      if(selectedtdid.equals(""))
        return;
      EngineDataSet tdMxTable = new EngineDataSet();
      setDataSetProperty(tdMxTable,TDMX_SQL+selectedtdid);
      tdMxTable.openDataSet();
      if(!isMasterAdd)
      {
        dsMasterTable.goToInternalRow(masterRow);
      }
      tdMxTable.first();
      RowMap tdMXRow=new RowMap(tdMxTable);
      m_RowInfo.put("material_no",tdMXRow.get("batchNo"));

      m_RowInfo.put("cpId",tdMXRow.get("cpId"));

      m_RowInfo.put("dmsxID",tdMXRow.get("dmsxID"));//地址

      m_RowInfo.put("storeid",tdMXRow.get("storeid"));

      m_RowInfo.put("handler",tdMXRow.get("handlePerson"));

      m_RowInfo.put("creator",tdMXRow.get("creator"));
      m_RowInfo.put("approver",tdMXRow.get("xm"));
      m_RowInfo.put("checker",tdMXRow.get("checkor"));
      m_RowInfo.put("drawID",tdMXRow.get("drawID"));

      m_RowInfo.put("net_weight",tdMXRow.get("netNum"));
      m_RowInfo.put("check_result",tdMXRow.get("checkResult"));
      m_RowInfo.put("dyne_side",tdMXRow.get("dyneSide"));
      m_RowInfo.put("hot_side",tdMXRow.get("hotSide"));
      tdMxTable.closeDataSet();
    }
  }
  //工序选择改变执行动态打印
  class  Leixing_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      hasleixingchang=true;
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());

      EngineDataSet dsdetable=getdetailTable();
      EngineDataSet dsMaster = getMaterTable();
      EngineDataSet dsPanduan = new EngineDataSet();
      RowMap MasterRow = getMasterRowinfo();

      track_type_ID=MasterRow.get("track_type_ID");
      if(track_type_ID.equals(""))
        return;
      setDataSetProperty(dsPanduan,Panduan_SQL + track_type_ID);

      dsPanduan.open();
      dsdetable.open();
      int a=dsPanduan.getRowCount();
      dsPanduan.first();

      dsdetable.deleteAllRows();
      initArrayList();

      m_RowInfo.put("track_type_ID",track_type_ID);

      m_RowInfo.put("type_prop",dsPanduan.getValue("type_prop"));
      dsPanduan.first();
      for(int i=0;i<dsPanduan.getRowCount();i++){
        String c=dsPanduan.getValue("track_type_ID");
        dsdetable.insertRow(false);
        track_detail_ID=String.valueOf(--s_track_detail_ID);
        dsdetable.setValue("track_bill_ID", track_bill_ID);
        dsdetable.setValue("track_detail_ID", track_detail_ID);
        dsdetable.setValue("track_type",dsPanduan.getValue("track_type"));
        String y=dsdetable.getValue("track_type");
        String b=dsPanduan.getValue("gymcID");
        dsdetable.setValue("gymcID",dsPanduan.getValue("gymcID"));

        dsdetable.post();
        RowMap drow = new RowMap(dsdetable);
        arraylist_de.add(drow);
        dsPanduan.next();
      }
      ///initRowInfo(true, true, true);
    }
  }
  //计算各种率
  class  GetCount implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception

    {

      double zzgxcpl=1;double zsjsh=0;double zcpl=0;double jz=0;
      double ok_sl=0;
      double wast_sl=0;
      double hypo_sl=0;
      double zok_sl=0;
      double zwast_sl=0;
      double zhypo_sl=0;
      double ok_pl=0;
      double hypo_pl=0;
      double wast_pl=0;
      RowMap countrowInfo = getMasterRowinfo();
      countrowInfo.put(data.getRequest());
      String type=countrowInfo.get("type_prop");
      putDetailInfo(data.getRequest());
      RowMap De_count_row=null;
      RowMap[] De_count_rows = getDeRowinfos();;
      for(int i=0;i<De_count_rows.length;i++){
        De_count_row=De_count_rows[i];
        double sjsh = Double.parseDouble( De_count_row.get("fact_weight").equals("")?"0": De_count_row.get("fact_weight"));
        double gxcpl= Double.parseDouble( De_count_row.get("prod_ratio").equals("")?"0": De_count_row.get("prod_ratio"));
        ok_sl= Double.parseDouble( De_count_row.get("ok_sheet1").equals("")?"0": De_count_row.get("ok_sheet1"))
             + Double.parseDouble( De_count_row.get("ok_sheet2").equals("")?"0": De_count_row.get("ok_sheet2"))
             + Double.parseDouble( De_count_row.get("ok_sheet3").equals("")?"0": De_count_row.get("ok_sheet3"));
        hypo_sl= Double.parseDouble( De_count_row.get("hypo_sheet1").equals("")?"0": De_count_row.get("hypo_sheet1"))
               + Double.parseDouble( De_count_row.get("hypo_sheet2").equals("")?"0": De_count_row.get("hypo_sheet2"))
               + Double.parseDouble( De_count_row.get("hypo_sheet3").equals("")?"0": De_count_row.get("hypo_sheet3"));
        wast_sl= Double.parseDouble( De_count_row.get("wast_sheet1").equals("")?"0": De_count_row.get("wast_sheet1"))
               + Double.parseDouble( De_count_row.get("wast_sheet2").equals("")?"0": De_count_row.get("wast_sheet2"))
               + Double.parseDouble( De_count_row.get("wast_sheet3").equals("")?"0": De_count_row.get("wast_sheet3"));
        zok_sl=zok_sl+ok_sl; zhypo_sl=zhypo_sl+hypo_sl; zwast_sl=zwast_sl+wast_sl;
        zsjsh=zsjsh+sjsh;
        zzgxcpl=zzgxcpl*gxcpl;
      }
      if(type.equals("1")){
        countrowInfo.put("tot_ull",String.valueOf(zsjsh));
        jz= Double.parseDouble(countrowInfo.get("net_weight").equals("")? "0" : countrowInfo.get("net_weight"));
        if(jz==0)
        {
          zcpl=0;
        }
        else{
          zcpl=(jz-zsjsh)/jz*100;
        }
        countrowInfo.put("tot_ratio",String.valueOf(zcpl));
      }
      if(type.equals("2")){
        zcpl=zzgxcpl;
        if(zok_sl+zhypo_sl+zwast_sl==0){
          ok_pl=0;
          hypo_pl=0;
          wast_pl=0;
        }
        else{
          ok_pl=zok_sl/(zok_sl+zhypo_sl+zwast_sl)*zcpl;
          hypo_pl=zhypo_sl/(zok_sl+zhypo_sl+zwast_sl)*zcpl;
          wast_pl=zwast_sl/(zok_sl+zhypo_sl+zwast_sl)*zcpl;
        }
        countrowInfo.put("tot_ull",formatNumber(String.valueOf(zsjsh),qtyFormat));
        countrowInfo.put("tot_ratio",formatNumber(String.valueOf(zcpl),qtyFormat));
        countrowInfo.put("good_ratio",formatNumber(String.valueOf(ok_pl),qtyFormat));
        countrowInfo.put("hypo_ratio",formatNumber(String.valueOf(hypo_pl),qtyFormat));
        countrowInfo.put("waster_ratio",formatNumber(String.valueOf(wast_pl),qtyFormat));
      }
    }
  }
  //进入分切从表得到当前行id
  class  get_detail_id implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
       RowMap masterrowInfo = getMasterRowinfo();
      storeid=masterrowInfo.get("storeid");

       cpid=masterrowInfo.get("cpid");
      detailRow=Integer.parseInt(data.getRequest().getParameter("rownum"));
      dsDetailTable.goToRow(detailRow);
      track_detail_ID = dsDetailTable.getValue("track_detail_ID");
      track_bill_ID=dsDetailTable.getValue("track_bill_ID");
      openDetaildetailTable();
      arraylist_detail=putDetailToArraylist(dsDetail_detail_Table,arraylist_detail);

    }
  }
//保存从表，执行savechange ,与主从分开保存
  class POST_Fenqie implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      hasHengqiepost=true;
      putDetail_DetailInfo(data.getRequest());
      EngineDataSet detail_detail_disp = getdetail_de_Table();
      EngineDataSet dsTable = getMaterTable();
      EngineDataSet ds_detailtable=getdetailTable();

      if(isMasterAdd){
        master_track_bill_ID =dataSetProvider.getSequence("S_SC_TRACK_BILL");

        dsTable.insertRow(false);
        dsTable.setValue("track_bill_ID",master_track_bill_ID);
        dsTable.setValue("track_type_ID",track_type_ID);
        dsTable.setValue("storeid",storeid);
        dsTable.setValue("cpid",cpid);
        dsTable.post();
        dsTable.saveChanges();
      }
      else
        {
        dsTable.goToInternalRow(masterRow);
        master_track_bill_ID =dsTable.getValue("track_bill_ID");}
      if(hasleixingchang){
         ds_detailtable.first();
         for(int i=0;i<ds_detailtable.getRowCount();i++){

         track_detail_ID = dataSetProvider.getSequence("S_SC_TRACK_DETAIL");
         ds_detailtable.setValue("track_detail_ID",track_detail_ID);
         ds_detailtable.setValue("track_bill_ID",master_track_bill_ID);
         ds_detailtable.post();
         ds_detailtable.next();
         }
         ds_detailtable.saveChanges();
         ds_detailtable.goToRow(detailRow);
         Fenqie_track_detail_ID= ds_detailtable.getValue("track_detail_ID");
      }
      else
      {  ds_detailtable.goToRow(detailRow);
         Fenqie_track_detail_ID= ds_detailtable.getValue("track_detail_ID");
      }
         detail_detail_disp.first();
      for(int i=0; i<arraylist_detail.size(); i++)
      {
        RowMap detail_de_row = (RowMap)arraylist_detail.get(i);

        dispart_ID=dataSetProvider.getSequence("S_SC_TRACK_DISPART");
        detail_detail_disp.setValue("track_bill_ID", master_track_bill_ID);
        detail_detail_disp.setValue("track_detail_ID", Fenqie_track_detail_ID);
        detail_detail_disp.setValue("dispart_ID",dispart_ID);

        detail_detail_disp.setValue("disp_weight1", detail_de_row.get("disp_weight1"));//
        String e= detail_detail_disp.getValue("disp_weight1");
        detail_detail_disp.setValue("disp_weight2", detail_de_row.get("disp_weight2"));//
        detail_detail_disp.setValue("disp_weight3", detail_de_row.get("disp_weight3"));//
        detail_detail_disp.setValue("disp_weight4", detail_de_row.get("disp_weight4"));//
        detail_detail_disp.setValue("disp_weight5", detail_de_row.get("disp_weight5"));//
        detail_detail_disp.setValue("disp_weight6", detail_de_row.get("disp_weight6"));//
        detail_detail_disp.setValue("disp_weight7", detail_de_row.get("disp_weight7"));//
        detail_detail_disp.setValue("disp_weight8", detail_de_row.get("disp_weight8"));//
        detail_detail_disp.setValue("disp_weight9", detail_de_row.get("disp_weight9"));//
        detail_detail_disp.setValue("disp_weight9", detail_de_row.get("disp_weight9"));//

        detail_detail_disp.setValue("disp_lenth1", detail_de_row.get("disp_lenth1"));//

        detail_detail_disp.setValue("disp_lenth2", detail_de_row.get("disp_lenth2"));//
        detail_detail_disp.setValue("disp_lenth3", detail_de_row.get("disp_lenth3"));//
        detail_detail_disp.setValue("disp_lenth4", detail_de_row.get("disp_lenth4"));//
        detail_detail_disp.setValue("disp_lenth5", detail_de_row.get("disp_lenth5"));//
        detail_detail_disp.setValue("disp_lenth6", detail_de_row.get("disp_lenth6"));//
        detail_detail_disp.setValue("disp_lenth7", detail_de_row.get("disp_lenth7"));//
        detail_detail_disp.setValue("disp_lenth8", detail_de_row.get("disp_lenth8"));//
        detail_detail_disp.setValue("disp_lenth9", detail_de_row.get("disp_lenth9"));//

        detail_detail_disp.setValue("cust_code1", detail_de_row.get("cust_code1"));//

        detail_detail_disp.setValue("cust_code2", detail_de_row.get("cust_code2"));//
        detail_detail_disp.setValue("cust_code3", detail_de_row.get("cust_code3"));//
        detail_detail_disp.setValue("cust_code4", detail_de_row.get("cust_code4"));//
        detail_detail_disp.setValue("cust_code5", detail_de_row.get("cust_code5"));//
        detail_detail_disp.setValue("cust_code6", detail_de_row.get("cust_code6"));//
        detail_detail_disp.setValue("cust_code7", detail_de_row.get("cust_code7"));//
        detail_detail_disp.setValue("cust_code8", detail_de_row.get("cust_code8"));//
        detail_detail_disp.setValue("cust_code9", detail_de_row.get("cust_code9"));//
        detail_detail_disp.setValue("batch_no1", detail_de_row.get("batch_no1"));//
        detail_detail_disp.setValue("batch_no2", detail_de_row.get("batch_no2"));//
        detail_detail_disp.setValue("batch_no3", detail_de_row.get("batch_no3"));//
        detail_detail_disp.setValue("batch_no4", detail_de_row.get("batch_no4"));//
        detail_detail_disp.setValue("batch_no5", detail_de_row.get("batch_no5"));//
        detail_detail_disp.setValue("batch_no6", detail_de_row.get("batch_no6"));//
        detail_detail_disp.setValue("batch_no7", detail_de_row.get("batch_no7"));//
        detail_detail_disp.setValue("batch_no8", detail_de_row.get("batch_no8"));//
        detail_detail_disp.setValue("batch_no9", detail_de_row.get("batch_no9"));//

        detail_detail_disp.post();
        detail_detail_disp.next();

      }
     detail_detail_disp.saveChanges();
    }
  }
//分切新增删除
  class Disp_detail_add  implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(action.equals(Disp_ADD))
      {

        fenqieadd=true;
        dsDetail_detail_Table.insertRow(false);
        dsDetail_detail_Table.setValue("track_bill_ID", track_bill_ID);
        dsDetail_detail_Table.setValue("track_detail_ID",dsDetailTable.getValue("track_detail_ID"));
        dispart_ID=String.valueOf(--s_dispart_ID);


        dsDetail_detail_Table.post();
        putDetail_DetailInfo(data.getRequest());
        RowMap Disp_Row = new RowMap(dsDetail_detail_Table);
        arraylist_detail.add(Disp_Row);

      }
      if(action.equals(Disp_DEL))
      {
        int rownum=Integer.parseInt(data.getRequest().getParameter("rownum"));
        arraylist_detail.remove(rownum);
        dsDetail_detail_Table.goToRow(rownum);
        String a=dsDetail_detail_Table.getValue("dispart_ID");

        dsDetail_detail_Table.deleteRow();
        putDetail_DetailInfo(data.getRequest());

      }
      if(action.equals(All_Disp_DEL))
          {  detailRow=Integer.parseInt(data.getRequest().getParameter("rownum"));
             dsDetailTable.goToRow(detailRow);
             track_detail_ID = dsDetailTable.getValue("track_detail_ID");
             dsDetail_detail_Table.setQueryString(combineSQL(DETAIL_DETAIL_SQL,"?",new String[]{"track_bill_ID="+"'"+track_bill_ID+"'","and track_detail_ID="+"'"+track_detail_ID+"'"}));
             dsDetail_detail_Table.deleteAllRows();
             dsDetail_detail_Table.saveChanges();
          }

    }
  }
}

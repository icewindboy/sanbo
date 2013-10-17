package engine.erp.sale.dafa;

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
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import engine.erp.sale.B_CustProdHistorySelect;
import com.borland.dx.dataset.*;
/**
 * 销售管理-销售合同
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @version 1.0
 */
public final class B_SaleOrder extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL        = "9001";          //显示明细
  public  static final String CANCER_APPROVE     = "9002";          //取消审批
  public  static final String SALE_OVER          = "9003";          //完成
  public  static final String SALE_CANCER        = "9004";          //作废
  public  static final String DWTXID_CHANGE      = "9005";          //改变往来单位
  public  static final String DEPT_CHANGE        = "9006";          //部门改变
  public  static final String DETAIL_CHANGE      = "9007";          //从表明细改变
  public  static final String KHLX_CHANGE        = "9008";          //客户类型改变!
  public  static final String DETAIL_PRODUCT_ADD = "9009";          //引入客户历史产品
  public  static final String PRICE_POST         = "9010";          //优惠价审批保存
  public  static final String WBZL_ONCHANGE      = "190011";        //外币类别改变
  public  static final String REPORT             ="645355666";      //报表追踪
  public  static final String CONFIRM            = "700001";        //确认
  public  static final String CONFIRM_RETURN     = "700002";        //确认后返回
  public  static final String DETAIL_COPY        = "1013";           //复制当前选中行
  public  static final String DEL_NULL           = "1014";          //删除数量为空的行
  public  static final String FORCE_OVER         = "1015";          //强制完成
  public  static final String PRODUCT_INVOC      = "1016";          //生产调用合同
  public  static final String MATERIAL_ADD       = "1017";          //来料加工

  private static final String MASTER_STRUT_SQL   = "SELECT * FROM xs_ht WHERE 1<>1 ORDER BY htbh DESC ";
  private static final String MASTER_SQL         = "SELECT * FROM xs_ht WHERE  ?  AND fgsid=? ? ORDER BY htbh DESC";
  private static final String MASTER_EDIT_SQL         = "SELECT * FROM xs_ht WHERE htid='?' ";
  private static final String MASTER_SUM_SQL     = "SELECT SUM(nvl(zsl,0))zsl FROM xs_ht WHERE 1=1 AND ? AND fgsid=? ?  ";
  private static final String MASTER_JE_SQL      = "SELECT SUM(nvl(zje,0))zje FROM xs_ht WHERE 1=1 AND ? AND fgsid=? ?  ";
  private static final String DETAIL_STRUT_SQL   = "SELECT * FROM xs_hthw WHERE 1<>1";
  private static final String DETAIL_SQL         = "SELECT * FROM xs_hthw WHERE htid=";
  private static final String CAN_OVER_SQL       = "select count(*) from xs_hthw a where nvl(a.sl,0)>nvl(a.stsl,0) and a.htid=";
  private static final String CAN_GEN_SQL        = "SELECT COUNT(*) FROM xs_hthw a, xs_ht b WHERE a.htid=b.htid AND b.zt=1 AND abs(nvl(a.sl,0))>abs(nvl(a.skdsl,0)) AND a.htid=";//合同数>实开单数且合同没作废和完成
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM xs_ht WHERE htid='?'";//用于审批时候提取一条记录
  private static final String XYED_SQL           = "SELECT a.hlts,b.xyedid,b.dwtxid,b.xyed,b.xydj,b.fgsid,b.hkts,b.ysk, a.xysdl,a.kyxyed FROM vw_xs_new_xyed a,xs_khxyed b WHERE b.dwtxid=a.dwtxid AND a.fgsid=b.fgsid and a.dwtxid=";

  private static final String ORDER_RECEIVE_GOODS
                                                 = "SELECT htid FROM (SELECT a.htid, SUM(nvl(b.sl,0)) sl FROM xs_hthw a, xs_tdhw b "
                                                 + "WHERE a.hthwid = b.hthwid AND a.htid IN (?) GROUP BY a.htid) t WHERE t.sl <> 0 ";
  private static final String XYDE_SQL = "SELECT nvl(xyed,0)-nvl(xysdl,0) FROM vw_xs_new_xyed where dwtxid= ";//得到信誉度
  private EngineDataSet dsMasterList  = new EngineDataSet();  //主表
  private EngineDataSet dsMasterTable  = new EngineDataSet();  //主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();  //从表
  private EngineDataSet dsCancel = new EngineDataSet();        //用
  private ArrayList cancelOrder = new ArrayList();             //可作废
  public  HtmlTableProducer masterListProducer = new HtmlTableProducer(dsMasterList, "xs_ht");
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "xs_ht");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "xs_hthw");
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = new ArrayList(); //从表多行记录的引用
  private LookUp salePriceBean = null; //销售单价的bean的引用, 用于提取销售单价
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String htid = null;
  private User user = null;
  public boolean isReport = false;
  public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
  public boolean appear=false;//在销售合同保存时,是否要显示需要审批的信息
  public boolean conversion=false;//销售合同的换算数量与数量是否需要强制转换取
  public String []zt;
  public String  hkts="";//该客户的回款天数
  public String tCopyNumber = "1";
  public String sfxdw ="";//允许更小单位
  private String zzsl="";//总数量
  private String zzje="";//总金额
  private String SLSQL="";
  private String JESQL="";
  public String dwdm="";
  public String dwmc="";
  public boolean showable=false;//是否显示 差价提成率(%) 计息天数 回笼天数 回笼提成率(%)
  public boolean isProductInvoke = false;
  public String isnet = "0";
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_SaleOrder getInstance(HttpServletRequest request)
  {
    B_SaleOrder saleOrderBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "saleOrderBean";
      saleOrderBean = (B_SaleOrder)session.getAttribute(beanName);
      if(saleOrderBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        saleOrderBean = new B_SaleOrder();
        saleOrderBean.qtyFormat = loginBean.getQtyFormat();
        saleOrderBean.priceFormat = loginBean.getPriceFormat();
        saleOrderBean.sumFormat = loginBean.getSumFormat();

        saleOrderBean.fgsid = loginBean.getFirstDeptID();
        saleOrderBean.loginId = loginBean.getUserID();
        saleOrderBean.loginName = loginBean.getUserName();
        if(loginBean.getSystemParam("XS_ORDER_SHOW_CREDIT").equals("1"))
            saleOrderBean.appear=true;
        if(loginBean.getSystemParam("SC_STORE_UNIT_STYLE").equals("1"))
          saleOrderBean.conversion = true;
        if(loginBean.getSystemParam("XS_ORDER_SHOW_ADD_FIELD").equals("1"))
          saleOrderBean.showable = true;//显示 差价提成率(%) 计息天数 回笼天数 回笼提成率(%)
        //设置格式化的字段
        saleOrderBean.dsDetailTable.setColumnFormat("sl", saleOrderBean.qtyFormat);
        saleOrderBean.dsDetailTable.setColumnFormat("hssl", saleOrderBean.qtyFormat);
        saleOrderBean.dsDetailTable.setColumnFormat("xsj", saleOrderBean.priceFormat);
        saleOrderBean.dsDetailTable.setColumnFormat("dj", saleOrderBean.priceFormat);
        saleOrderBean.dsDetailTable.setColumnFormat("xsje", saleOrderBean.sumFormat);
        saleOrderBean.dsDetailTable.setColumnFormat("jje", saleOrderBean.sumFormat);

        saleOrderBean.dsMasterTable.setColumnFormat("zsl", saleOrderBean.qtyFormat);
        saleOrderBean.dsMasterTable.setColumnFormat("zje", saleOrderBean.sumFormat);
        saleOrderBean.dsMasterTable.setColumnFormat("wbje", saleOrderBean.sumFormat);

        saleOrderBean.user = loginBean.getUser();
        session.setAttribute(beanName, saleOrderBean);
      }
    }
    return saleOrderBean;
  }

  /**
   * 构造函数
   */
  private B_SaleOrder()
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
    setDataSetProperty(dsCancel, null);
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"hthwid"}, new String[]{"s_xs_hthw"}));
    dsMasterList.addLoadListener(new com.borland.dx.dataset.LoadListener(){
      public void dataLoaded(com.borland.dx.dataset.LoadEvent event)
      {
        cancelOrder.clear();
        if(dsMasterList.getRowCount() == 0)
          return;
        String sql = getWhereIn(dsMasterList, "htid", null);
        sql = combineSQL(ORDER_RECEIVE_GOODS, "?", new String[]{sql});
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

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(PRODUCT_INVOC), new Init());

    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(MATERIAL_ADD), masterAddEdit);

    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(PRICE_POST), masterPost);
    //PRICE_POST
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    //&#$//审核部分
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());//取消审批
    addObactioner(String.valueOf(SALE_CANCER), new Cancer());//作废
    addObactioner(String.valueOf(SALE_OVER), new Over());//完成
    addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());
    addObactioner(String.valueOf(DETAIL_CHANGE), new Detail_Change());
    addObactioner(String.valueOf(KHLX_CHANGE), new Khlx_Change());//DETAIL_PRODUCT_ADD
    addObactioner(String.valueOf(DETAIL_PRODUCT_ADD), new Detail_Product_Add());
    addObactioner(String.valueOf(DEPT_CHANGE), new Dept_Change());
    addObactioner(String.valueOf(WBZL_ONCHANGE), new Wb_Onchange());
    addObactioner(String.valueOf(REPORT), new Approve());
    addObactioner(String.valueOf(CONFIRM), masterPost);
    addObactioner(String.valueOf(CONFIRM_RETURN), masterPost);
    addObactioner(String.valueOf(DETAIL_COPY), new Detail_Copy_Add());//DETAIL_COPY
    addObactioner(String.valueOf(DEL_NULL), new Detail_Delete_Null());//Detail_Delete_Null
    addObactioner(String.valueOf(FORCE_OVER), new Over());

  }
  /**
   *引入已审核合同未发货金额
   * */
 public RowMap getHtzje()
 {
   EngineDataSet tmp=new EngineDataSet();
   String dwtxid=m_RowInfo.get("dwtxid");
   try{
   setDataSetProperty(tmp,"select xs_ht.dwtxid,xs_hthw.jje jje  from xs_ht,xs_hthw where xs_ht.htid=xs_hthw.htid AND xs_ht.zt='1' AND xs_ht.dwtxid="+dwtxid);
   }catch(Exception e){}
   tmp.open();
   RowMap jerow=new RowMap();
   BigDecimal zje=new BigDecimal(0);
   int j=tmp.getRowCount();
   tmp.first();
   for(int i=0;i<tmp.getRowCount();i++)
   {
     zje = tmp.getBigDecimal("jje").add(zje);//销售金额
     tmp.next();
   }
   jerow.put("zje",zje.toString());
   return jerow;
 }
 /**
  *引入业务员奖金计算公式
  * */
public RowMap getJjjsgs()
{
  EngineDataSet jsgs=new EngineDataSet();
  try{
  setDataSetProperty(jsgs,"select * FROM xs_jjjsgs");
  }catch(Exception e){}
  jsgs.open();
  jsgs.first();
  RowMap jsgsrow=new RowMap();
  jsgsrow.put(jsgs);
  String xsjzj=jsgs.getValue("xsjzj");
  String tclzj=jsgs.getValue("tclzj");
  String hltszj=jsgs.getValue("hltszj");
  String xsjjs=jsgs.getValue("xsjjs");
  return jsgsrow;
 }
 /**
  *引入单位的信誉额度信息
  * */
public RowMap getXYED(String dwtxid)
{
  RowMap xyedRow ;
  try{
    EngineDataSet dsxyed = new EngineDataSet();
    setDataSetProperty(dsxyed,XYED_SQL+dwtxid);
    dsxyed.open();
    dsxyed.first();
    xyedRow= new RowMap(dsxyed);
    }catch(Exception e)
    {
      xyedRow=new RowMap();
    }
    return xyedRow;
}
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
/**
  *得到与此合同相关的总销售结算金额
  * */
 public String getTotalJsje(String dwtxid)
 {
   String wsje="";
   //String dwtxid = m_RowInfo.get("dwtxid");
   if(dwtxid.equals(""))
     return "0";
   try{
     wsje =dataSetProvider.getSequence("SELECT sum(nvl(b.jje,0))-sum(nvl(b.ssje,0))wjsje FROM xs_td a,xs_tdhw b WHERE a.tdid=b.tdid AND  a.zt in(1,2,3,8) AND a.hkrq<=sysdate AND a.dwtxid='"+dwtxid+"'");
     if(wsje==null)
       wsje="";
   }catch(Exception e){}
   return wsje;
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
      dsMasterTable.closeDataSet();
      dsMasterTable = null;
    }
    if(dsMasterList != null){
      dsMasterList.closeDataSet();
      dsMasterList = null;
    }
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    if(masterListProducer != null)
    {
      masterListProducer.release();
      masterListProducer = null;
    }
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
    deleteObservers();
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
  /**是否可以生成提单**/
  public boolean iscanGen(String htid)
  {
    if(htid.equals(""))
      return false;
    String count="";
    try
    {
      count = dataSetProvider.getSequence(CAN_GEN_SQL+"'"+htid+"'");
    }
    catch(Exception e){}
    if(count.equals("0"))
      return false;
    else
      return true;
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
        m_RowInfo.clear();//清除旧数据
      if(!isAdd)
        m_RowInfo.put(getMaterTable());//不是新增时,推入主表当前行
      else
      {
        //主表新增
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
        String htbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_ht','htbh')htbh FROM dual");
        m_RowInfo.put("htbh", htbh);
        m_RowInfo.put("ksrq", today);
        m_RowInfo.put("jsrq", endday);
        m_RowInfo.put("czrq", today);//制单日期
        m_RowInfo.put("czy", loginName);//操作员
        m_RowInfo.put("htrq", today);
        m_RowInfo.put("jhrq", today);
        m_RowInfo.put("czyid", loginId);
        m_RowInfo.put("zt", "0");
        m_RowInfo.put("isnet", isnet);
      }
      m_RowInfo.put("isdock", "1");
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
      detailRow.put("wzdjid", rowInfo.get("wzdjid_"+i));
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));
      detailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+i), qtyFormat));//
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
      detailRow.put("zk", formatNumber(rowInfo.get("zk_"+i), priceFormat));//
      detailRow.put("dj", formatNumber(rowInfo.get("dj_"+i), priceFormat));//含税单价

      detailRow.put("xsje", formatNumber(rowInfo.get("xsje_"+i), sumFormat));
      detailRow.put("jje", formatNumber(rowInfo.get("jje_"+i), sumFormat));//无税单价
      detailRow.put("jzj", formatNumber(rowInfo.get("jzj_"+i), sumFormat));//基准价
      detailRow.put("cjtcl", formatNumber(rowInfo.get("cjtcl_"+i), sumFormat));//差价提成率
      detailRow.put("jxts", formatNumber(rowInfo.get("jxts_"+i), qtyFormat));//计息天数
      detailRow.put("hlts", formatNumber(rowInfo.get("hlts_"+i), qtyFormat));//回笼天数
      detailRow.put("hltcl", formatNumber(rowInfo.get("hltcl_"+i), sumFormat));//回笼提成率
      detailRow.put("wbje", formatNumber(rowInfo.get("wbje_"+i), sumFormat));
      String xs_jzj = rowInfo.get("xs_jzj_"+i);
      detailRow.put("xs_jzj", formatNumber(rowInfo.get("xs_jzj_"+i), qtyFormat));//引入的基准价
      //
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
      detailRow.put("jhrq", rowInfo.get("jhrq_"+i));//
      detailRow.put("xsj", rowInfo.get("xsj_"+i));//
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
  /*得到表对象*/
  public final EngineDataSet getMaterList()
  {
    return dsMasterList;
  }
  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){
    return dsDetailTable;
  }
  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    //htid = dsMasterTable.getValue("htid");//关链
    //isMasterAdd为真是返回空的从表数据集(主表新增时,从表要打开)
    dsDetailTable.setQueryString(DETAIL_SQL + (isMasterAdd ? "-1" : "'"+htid+"'"));
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
  public final boolean getState()
  {
    return isMasterAdd;
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
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dwdm ="";
      dwmc ="";
      //&#$
      isApprove = false;
      isReport = false;
      isProductInvoke = false;
      if(String.valueOf(PRODUCT_INVOC).equals(action))
          isProductInvoke=true;

      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      masterListProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("htrq$a", startDay);
      row.put("htrq$b", today);
      //row.put("zt", "0");
      isMasterAdd = true;
      zt = new String[]{""};
      String SQL = " AND zt<>4 AND zt<>8 ";
      if(isProductInvoke)
        SQL = " AND zt<>0 AND zt<>9 ";
      String MSQL =  combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsMasterList.setQueryString(MSQL);
      dsMasterList.setRowMax(null);

      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();

      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code!=null&&code.equals("1"))
        submitType=true;
      else
        submitType=false;

      sfxdw = dataSetProvider.getSequence("select t.sfxdw from xs_jjjsgs t");

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

      zzsl = formatNumber(zzsl, priceFormat);
      zzje = formatNumber(zzje, priceFormat);
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
      //打开从表
      htid = dsMasterList.getValue("htid");
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
      //&#$
      isApprove = false;
      if(String.valueOf(ADD).equals(action))
      {
        isMasterAdd = true;
        isnet = "0";
      }else if(String.valueOf(MATERIAL_ADD).equals(action))
      {
        isMasterAdd = true;
        isnet = "1";
      }
      else
        isMasterAdd = false;
      if(!isMasterAdd)
      {
        dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));//查看或修改
        masterRow = dsMasterList.getInternalRow();//返回当前行指针(long)
        htid = dsMasterList.getValue("htid");
        isnet = dsMasterList.getValue("isnet");
      }

      dsMasterTable.setQueryString(isMasterAdd?MASTER_STRUT_SQL:combineSQL(MASTER_EDIT_SQL,"?",new String[]{htid}));
      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();

      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      data.setMessage(showJavaScript("toDetail();"));
    }
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
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      masterListProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //得到request的参数,值若为null, 则用""代替

      String id = null;

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
      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();
      //打开从表
      htid = id;
      openDetailTable(false);

      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
    }
  }
  /**
   * 完成
   */
  class Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      htid = data.getParameter("rownum");
      String count = dataSetProvider.getSequence(CAN_OVER_SQL+htid);
      if(String.valueOf(SALE_OVER).equals(action))
      {
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("if(confirm('还未完全出库,要强制完成吗?')) sumitForm("+FORCE_OVER+","+htid+");"));//强制完成
          return;
        }
      }

      dsMasterTable.setQueryString(combineSQL(MASTER_EDIT_SQL,"?",new String[]{htid}));
      if(!dsMasterList.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();

      dsMasterTable.setValue("zt","8");
      dsMasterTable.saveChanges();
      //
      dsMasterList.readyRefresh();
    }
  }
  /**
   * 作废
   */
  class Cancer implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //int row = Integer.parseInt(data.getParameter("rownum"));
      //dsMasterList.goToRow(row);
      htid = data.getParameter("rownum");

      dsMasterTable.setQueryString(combineSQL(MASTER_EDIT_SQL,"?",new String[]{htid}));
      if(!dsMasterList.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();

      dsMasterTable.setValue("zt", "4");
      dsMasterTable.saveChanges();
      //
      dsMasterList.readyRefresh();
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
      htid = dsMasterList.getValue("htid");
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterList,dsMasterList.getRow(),"sale_order_list");
      dsMasterList.readyRefresh();
    }
  }
  //&#$
  /**
   * 添加到审核列表的操作类
   */
 public class Add_Approve implements Obactioner,ApproveListener
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      htid = dsMasterList.getValue("htid");
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_ht a WHERE a.htid='"+htid+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        dsMasterList.readyRefresh();
        return;
      }
      openDetailTable(false);
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterList.getValue("htbh");
      String deptid = dsMasterList.getValue("deptid");
      approve.putAproveList(dsMasterList, dsMasterList.getRow(),"sale_order_list", content,deptid, this);
    }
    public void processApprove(ApproveResponse[] reponses) throws Exception
    {
      if(reponses==null||reponses.length==0)
        return;
     String ordertypeid = dsMasterList.getValue("ordertypeid");
     String dwtxid = dsMasterList.getValue("dwtxid");

     String ordertype = dataSetProvider.getSequence("select ordertype from jc_ordertype where ordertypeid="+ordertypeid);
     for(int i=0;i<reponses.length;i++)
     {
       String tmp = reponses[i].getProjectFlowValue();

       if(reponses[i].getProjectFlowCode().equals("sale_price_approve"))
       {
         dsDetailTable.first();
         for(int j=0;j<dsDetailTable.getRowCount();j++)
         {
           double dj=Double.parseDouble(dsDetailTable.getValue("dj").equals("")?"0":dsDetailTable.getValue("dj"));
           double jzj =Double.parseDouble(dsDetailTable.getValue("jzj").equals("")?"0":dsDetailTable.getValue("jzj"));
           if(dj<jzj)
             {
             reponses[i].add();
             break;
             }
           else
             reponses[i].skip();
           dsDetailTable.next();
         }
       }
       else if(reponses[i].getProjectFlowCode().equals("sale_credit_approve"))
       {
         String syed= dataSetProvider.getSequence(XYDE_SQL+dsMasterList.getValue("dwtxid"));
         double xyed=(syed==null?0:Double.parseDouble(syed.length()==0?"0":syed));
         double zje=dsMasterList.getBigDecimal("zje").doubleValue();
         if(zje>xyed)
           reponses[i].add();
         else
           reponses[i].skip();
       }
       else if(reponses[i].getProjectFlowCode().equals("sale_extend_term")&&ordertype.equalsIgnoreCase(reponses[i].getProjectFlowValue()))
       {
         double dqje =Double.parseDouble(getTotalJsje(dwtxid).equals("")?"0":getTotalJsje(dwtxid));
         if(dqje>0)
           reponses[i].add();
       }
       else if(reponses[i].getProjectFlowCode().equals("sale_order_list")&&ordertype.equalsIgnoreCase(reponses[i].getProjectFlowValue()))
       {
         reponses[i].add();
       }
       else
         reponses[i].skip();
     }
    }
  }
  /**
   * 临时保存
   */
  class Master_Temp_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(String.valueOf(PRICE_POST).equals(action))
        isMasterAdd=false;//在进行优惠价审批时,修改从表数据后保存操作
      putDetailInfo(data.getRequest());
      String confirmString = getConfirmString();//获得是否有需要审批的信息
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
      if(appear)
      {
        if(!confirmString.equals("")&&(String.valueOf(POST_CONTINUE).equals(action)))
        {
          data.setMessage(showJavaScript("if(confirm('"+confirmString+"')) sumitForm("+CONFIRM+",-1);"));//保存确认
          return;
        }
        if(!confirmString.equals("")&&(String.valueOf(POST).equals(action)))
        {
          data.setMessage(showJavaScript("if(confirm('"+confirmString+"')) sumitForm("+CONFIRM_RETURN+",-1);"));//保存确认
          return;
        }
      }
      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      //if(!isMasterAdd)
      //  ds.goToInternalRow(masterRow);
      if(isMasterAdd){
        ds.insertRow(false);
        htid = dataSetProvider.getSequence("s_xs_ht");//得到主表主键值
        String htbh = rowInfo.get("htbh");
        String count = dataSetProvider.getSequence("select count(*) from xs_ht t where t.htbh='"+htbh+"'");
        if(!count.equals("0"))
          htbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_ht','htbh')htbh FROM dual");
        ds.setValue("htbh", htbh);//
        ds.setValue("htid", htid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("czyid", loginId);
        ds.setValue("czy", loginName);//操作员
        ds.setValue("isnet", isnet);//是否网络
      }
      //保存从表的数据
      RowMap detailrow = null;
      double hl = rowInfo.get("hl").length() > 0 ? Double.parseDouble(rowInfo.get("hl")) : 0;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        detail.setValue("wzdjid", detailrow.get("wzdjid"));
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("htid", htid);

        double xsj = detailrow.get("xsj").length() > 0 ? Double.parseDouble(detailrow.get("xsj")) : 0;//销售单价
        //double hsbl = detailrow.get("hsbl").length() > 0 ? Double.parseDouble(detailrow.get("hsbl")) : 0;//换算比例
        double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;
        double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(detailrow.get("dj")) : 0;

        if(dj==0)
        {
          data.setMessage(showJavaScript("alert('单价不能为0!')"));
          return;
        }
        detail.setValue("xsj", detailrow.get("xsj"));//
        detail.setValue("sl", detailrow.get("sl"));
        if(conversion)
        {
        detail.setValue("zk", String.valueOf(dj/xsj*100));
        detail.setValue("xsje", String.valueOf(sl * xsj));
        detail.setValue("dj", String.valueOf(dj));
        detail.setValue("jje", engine.util.Format.formatNumber(String.valueOf(sl * detail.getBigDecimal("dj").doubleValue()),sumFormat) );
        detail.setValue("hssl", detailrow.get("hssl"));
        }
        else
        {
          detail.setValue("zk", detailrow.get("zk"));
          detail.setValue("xsje", detailrow.get("xsje"));
          detail.setValue("dj", detailrow.get("dj"));
          detail.setValue("jje", engine.util.Format.formatNumber(detailrow.get("jje"),sumFormat) );
          detail.setValue("hssl", detailrow.get("hssl"));
        }
        detail.setValue("jzj", detailrow.get("jzj"));
        detail.setValue("cjtcl", detailrow.get("cjtcl"));
        detail.setValue("jxts", detailrow.get("jxts"));
        detail.setValue("hlts", detailrow.get("hlts"));
        detail.setValue("hltcl", detailrow.get("hltcl"));
        detail.setValue("bz", detailrow.get("bz"));//备注
        detail.setValue("jhrq", detailrow.get("jhrq"));
        if(hl==0)
          detail.setValue("wbje","0");
        else
           detail.setValue("wbje", formatNumber(String.valueOf(sl * detail.getBigDecimal("dj").doubleValue()/hl),sumFormat));

        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        totalSum = totalSum.add(detail.getBigDecimal("jje"));
        detail.next();
      }
      //保存主表数据
      ds.setValue("personid", rowInfo.get("personid"));//人员ID
      ds.setValue("deptid", rowInfo.get("deptid"));//
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));//购货单位ID
      ds.setValue("htrq", rowInfo.get("htrq"));//合同日期
      ds.setValue("ksrq", rowInfo.get("ksrq"));//合同有效期始
      ds.setValue("jsrq", rowInfo.get("jsrq"));//合同有效期终
      ds.setValue("qddd", rowInfo.get("qddd"));//签订地点
      ds.setValue("qtxx", rowInfo.get("qtxx"));//其他信息
      ds.setValue("khlx", rowInfo.get("khlx"));//客户类型
      ds.setValue("wbid", rowInfo.get("wbid"));
      ds.setValue("sendmodeid", rowInfo.get("sendmodeid"));
      ds.setValue("ordertypeid", rowInfo.get("ordertypeid"));
      ds.setValue("hl", rowInfo.get("hl"));
      ds.setValue("zsl", totalNum.toString());//总金额
      ds.setValue("zje", engine.util.Format.formatNumber(totalSum.toString(),sumFormat));//总金额
      ds.setValue("jsfsid", rowInfo.get("jsfsid"));
      ds.setValue("yfdj", rowInfo.get("yfdj"));
      ds.setValue("isproduce", rowInfo.get("isproduce"));

      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      dsMasterList.readyRefresh();
      //合计相关
      if(!(SLSQL.equals("")))
      {
      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");
      }

      if(!(JESQL.equals("")))
      {
      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");
      }

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;

      zzsl = formatNumber(zzsl, priceFormat);
      zzje = formatNumber(zzje, priceFormat);
      //
      dsMasterList.readyRefresh();

      if((String.valueOf(CONFIRM).equals(action))||(String.valueOf(POST_CONTINUE).equals(action)))
      {
        isMasterAdd = true;
        detail.empty();
        initRowInfo(true, true, true);
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      else if((String.valueOf(CONFIRM_RETURN).equals(action))||(String.valueOf(POST).equals(action)))
        data.setMessage(showJavaScript("backList();"));
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      //HashSet wzdijds=new HashSet(d_RowInfos.size());
      HashSet htmp = new HashSet();
      htmp.clear();
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
      {
        return showJavaScript("alert('从表为空,不能保存!');");
      }
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String wzdjid="w"+detailrow.get("wzdjid").trim();
        String dmsxid="d"+detailrow.get("dmsxid").trim();
        /*
        if(!wzdijds.add(wzdjid))
          return showJavaScript("alert('所选产品重复!');");
        */
        if(!htmp.add(wzdjid+dmsxid))
         return showJavaScript("alert('所选产品重复!');");
        String hssl = detailrow.get("hssl");
        if((temp = checkNumber(hssl, detailProducer.getFieldInfo("hssl").getFieldname())) != null)
          return temp;
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, detailProducer.getFieldInfo("sl").getFieldname())) != null)
          return temp;
        if(Double.parseDouble(sl)==0)
          return showJavaScript("alert('数量不能为零!');");
        String dj = detailrow.get("dj");
        if((temp = checkNumber(dj, detailProducer.getFieldInfo("dj").getFieldname())) != null)
          return temp;
        String jzj = detailrow.get("jzj");
        if((temp = checkNumber(jzj, "基准价")) != null)
          return temp;
        String jje = detailrow.get("jje");
        if((temp = checkNumber(jje, detailProducer.getFieldInfo("jje").getFieldname())) != null)
          return temp;
        String hlts = detailrow.get("hlts");
        if((temp = checkNumber(hlts, detailProducer.getFieldInfo("hlts").getFieldname())) != null)
          return temp;
        temp = detailrow.get("jhrq");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('非法交货日期！');");
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

      String kssj = rowInfo.get("ksrq");
      String jssj = rowInfo.get("jsrq");
      String zt = rowInfo.get("zt");

      String temp = rowInfo.get("htrq");
      if(temp.equals(""))
        return showJavaScript("alert('合同日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法合同日期！');");
      temp = rowInfo.get("ksrq");
      if(temp.equals(""))
        return showJavaScript("alert('合同有效期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法合同有效期！');");
      temp = rowInfo.get("jsrq");
      if(temp.equals(""))
        return showJavaScript("alert('合同有效期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法合同有效期！');");
      temp = rowInfo.get("dwtxid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择购货单位！');");
//      temp = rowInfo.get("personid");
//      if(temp.equals(""))
//        return showJavaScript("alert('请选择业务员！');");
//      temp = rowInfo.get("ordertypeid");
//      if(temp.equals(""))
//        return showJavaScript("alert('请选择合同类型！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择部门！');");
      temp = rowInfo.get("khlx");
      if(temp.equals(""))
        return showJavaScript("alert('请选择客户类型！');");
      temp = rowInfo.get("qtxx");
      if(temp.getBytes().length > getMaterTable().getColumn("qtxx").getPrecision())
        return showJavaScript("alert('您输入的其他信息的内容太长了！');");

      String yfdj = rowInfo.get("yfdj");
      if(!yfdj.equals(""))
      {
      if((temp = checkNumber(yfdj, "运费单价")) != null)
          return temp;
      }
      if((kssj.length()>0)&&(jssj.length()>0)&&zt.equals("0"))
      {
        try{
          Date ksdate = new SimpleDateFormat("yyyy-MM-dd").parse(kssj);
          Date jsdate = new SimpleDateFormat("yyyy-MM-dd").parse(jssj);
          Date today = new Date();
          if(ksdate.compareTo(jsdate)>0)
            return showJavaScript("alert('非法合同有效期！');");
          if(today.compareTo(jsdate)>0)
            return showJavaScript("alert('非法合同有效期！');");
          }catch(Exception e){}
      }
      return null;
    }
    /**
     *得到是不需在审批的信息
     * */
    private String getConfirmString()
    {
      RowMap rowInfo = getMasterRowinfo();
      String dwtxid = rowInfo.get("dwtxid");
      double dzje =0;
      String confirmString="";
      for(int i=0;i<d_RowInfos.size();i++)
      {
        RowMap detail = (RowMap)d_RowInfos.get(i);
        double sl=Double.parseDouble(detail.get("sl").equals("")?"0":detail.get("sl"));
        double dj=Double.parseDouble(detail.get("dj").equals("")?"0":detail.get("dj"));
        double jzj =Double.parseDouble(detail.get("jzj").equals("")?"0":detail.get("jzj"));
        dzje = dzje+sl*dj;
        if(dj<jzj)
          confirmString = "该合同需要进行优惠价审批!";
      }
      String syed="";
      try{
        syed= dataSetProvider.getSequence(XYDE_SQL+dwtxid);
        }catch(Exception e){}
        double xyed=(syed==null?0:Double.parseDouble(syed.length()==0?"0":syed));
        if(dzje>xyed)
        {
          if(confirmString.equals(""))
            confirmString = "该合同需要进行信誉度审批!\\\n已超过信誉度"+(engine.util.Format.formatNumber(dzje-xyed,"#0.00"));
          else
            confirmString = "该合同需要进行优惠价审批与信誉度审批!\\\n已超过信誉度"+(engine.util.Format.formatNumber(dzje-xyed,"#0.00"));
        }
        String wjsje = getTotalJsje(dwtxid);
        if(wjsje!=null&&!wjsje.equals(""))
        {
          if(Double.parseDouble(wjsje)>0)
          confirmString = confirmString+"  该客户已有到期未回笼款"+wjsje;
        }
        return confirmString;
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
      //ds.goToInternalRow(masterRow);

      String htid = ds.getValue("htid");
      String max = dataSetProvider.getSequence("SELECT MAX(htid) FROM xs_ht");
      if(!htid.equals(max))
      {
        data.setMessage(showJavaScript("alert('该合同不能删除!')"));
        return;
      }
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_ht a WHERE a.htid='"+htid+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        ds.readyRefresh();
        data.setMessage(showJavaScript("alert('该单据不能删除!')"));
        return;
      }
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
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
      //
      dsMasterList.readyRefresh();
      data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   *  查询操作
   *  QueryColumn
   *  QueryFixedItem
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
      if(zt==null&&isProductInvoke)
        SQL = "  AND zt<>0 AND zt<>9  ";

      String MSQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});//组装SQL语句
      if(!dsMasterList.getQueryString().equals(MSQL))
      {
        dsMasterList.setQueryString(MSQL);
        dsMasterList.setRowMax(null);//以便dbNavigator刷新数据集
      }
      String SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      String JESQL = combineSQL(MASTER_JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

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
    }
    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;//已初始化查询条件
      EngineDataSet master = dsMasterList;
      if(!master.isOpen())
        master.open();//打开主表数据集
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("htbh"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("htbh"), null, null, null, "b", "<="),
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
        new QueryColumn(master.getColumn("isnet"), null, null, null, null, "="),//
        new QueryColumn(master.getColumn("qddd"), null, null, null),
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),//
        new QueryColumn(master.getColumn("czy"), null, null, null, null, "like"),//
        new QueryColumn(master.getColumn("sprid"), null, null, null, null, "="),//
        new QueryColumn(master.getColumn("htid"), "vw_xs_hthw", "htid", "cpbm", "cpbm$a", ">="),//
        new QueryColumn(master.getColumn("htid"), "vw_xs_hthw", "htid", "cpbm", "cpbm$b", "<="),//
        new QueryColumn(master.getColumn("htid"), "vw_xs_hthw", "htid", "pm", "pm", "like"),//从表品名
        new QueryColumn(master.getColumn("htid"), "vw_xs_hthw", "htid", "gg", "gg", "=")//从表规格
        //new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;//初始化完成
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
   *  从表新增
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      dsDetailTable.insertRow(false);
      dsDetailTable.setValue("hthwid", "-1");
      dsDetailTable.post();
      RowMap detailrow = new RowMap(dsDetailTable);
      d_RowInfos.add(detailrow);
      int rownum = d_RowInfos.size()-1;
      data.setMessage(showJavaScript("document.form1.cpbm_"+rownum+".focus();"));
    }
  }
  /**
   *客户历史记录产品
   *
   * */
  class Detail_Product_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      engine.project.LookUp salePriceBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_SALE_PRICE);
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String importwzdjids = m_RowInfo.get("importwzdjids");
      String khlx = m_RowInfo.get("khlx");
      if(importwzdjids.length() == 0)
        return;
      String[] wzdjids = parseString(importwzdjids,",");//解析出合同货物ID数组
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[]{"wzdjid","dmsxid"});
      B_CustProdHistorySelect custProdHisBean = B_CustProdHistorySelect.getInstance(req);
      for(int i=0; i < wzdjids.length; i++)
      {
        if(wzdjids[i].equals("-1"))
          continue;
        //if(!isMasterAdd)
        //  dsMasterTable.goToInternalRow(masterRow);
        String htid = dsMasterTable.getValue("htid");
        RowMap detailrow = null;
        String wzdjiddwtxmid = wzdjids[i];
        int ln = wzdjiddwtxmid.length();
        int index = wzdjiddwtxmid.indexOf("%");
        String wzdjid = wzdjiddwtxmid.substring(0,index);
        String dmsxid =(index<ln)?(wzdjiddwtxmid.substring(index+1,ln)):"";

          locateGoodsRow.setValue(0,wzdjid);
          locateGoodsRow.setValue(1,dmsxid);
          if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap saleRow = custProdHisBean.getSelectRow(wzdjid);
          //RowMap wzRow=salePriceBean.getLookupRow(wzdjid);

          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("hthwid", "-1");
          dsDetailTable.setValue("htid", htid);
          dsDetailTable.setValue("wzdjid", wzdjid);

          String ldj=saleRow.get("dj");

          double dj=Double.parseDouble(saleRow.get("dj"));//历史单价
          double xsj=Double.parseDouble(saleRow.get("xsj").equals("")?saleRow.get("xsjzj"):saleRow.get("xsj"));

          double zk=dj/xsj*100;
          dsDetailTable.setValue("zk", String.valueOf(zk));
          dsDetailTable.setValue("xsj", String.valueOf(xsj));
          dsDetailTable.setValue("dj", String.valueOf(dj));
          dsDetailTable.setValue("jzj", saleRow.get("xsjzj"));
          dsDetailTable.setValue("cjtcl", saleRow.get("xstcl"));
          dsDetailTable.setValue("jxts", saleRow.get("hkts"));
          dsDetailTable.setValue("hlts", saleRow.get("hkts"));
          dsDetailTable.setValue("hltcl", saleRow.get("hktcl"));
          dsDetailTable.setValue("dmsxid", dmsxid);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
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
      String wzdjid = newadd.get("wzdjid");
      String htid = newadd.get("htid");
      String xsj = newadd.get("xsj");
      //String dj =  newadd.get("dj");
      String jzj = newadd.get("jzj");
      String hltcl = newadd.get("hltcl");
      int copynumber = Integer.parseInt(tCopyNumber);


      for(int i=0;i<copynumber;i++)
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("hthwid", "-1");
        dsDetailTable.setValue("htid", htid);
        dsDetailTable.setValue("hltcl", hltcl);
        dsDetailTable.setValue("wzdjid", wzdjid);
        dsDetailTable.setValue("xsj", xsj);
        dsDetailTable.setValue("jzj", jzj);
        dsDetailTable.post();
        RowMap detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
      }
      tCopyNumber="1";
    }
}
  /**
   *  从表更新
   */
  class Detail_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      putDetailInfo(data.getRequest());
      RowMap detailRow = null;
      String khlx=req.getParameter("khlx");
      String dwtxid=m_RowInfo.get("dwtxid");
      RowMap xyedRow ;
      EngineDataSet dsxyed = new EngineDataSet();
      setDataSetProperty(dsxyed,XYED_SQL+dwtxid);
      dsxyed.open();
      dsxyed.first();
      xyedRow= new RowMap(dsxyed);
      hkts = xyedRow.get("hkts");
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailRow = (RowMap)d_RowInfos.get(i);
        if(rownum==i)
        {
          detailRow.put("dmsxid", "");
          if(khlx.equals("C"))
          {
            String xs_jzj = detailRow.get("xs_jzj").equals("")?"0":detailRow.get("xs_jzj");
            double d = Double.parseDouble(xs_jzj)*0.98;
            String st = engine.util.Format.formatNumber(String.valueOf(d),"#,##0.00");
            detailRow.put("jzj", st);//基准价
          }
          if(!(hkts.equals("")&&!hkts.equals("45")))
          {
            detailRow.put("hlts", hkts);
            detailRow.put("jxts", hkts);
          }
        }
      }
      data.setMessage(showJavaScript("document.form1.sxz_"+rownum+".focus();"));
    }
  }
  /**
   * 没用
   * 当客户类型为A类时,基准价为引入的基准价,
   * 当客户类型为c类时,销售合同的基准价为引入的基准价的98%
   *
   *  从表更新
   */
  class Khlx_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      engine.project.LookUp salePriceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_PRICE);
      String oldkhlx=m_RowInfo.get("khlx");
      putDetailInfo(data.getRequest());//保存输入的明细信息
      String khlx=m_RowInfo.get("khlx");
      if(oldkhlx.equals(""))
        return;
      if(oldkhlx.equals(khlx))
        return;
      if(khlx.equals(""))
      {
        data.setMessage(showJavaScript("alert('客户类型不能为空!')"));
        m_RowInfo.put("khlx",oldkhlx);
        return;
      }
      if(!isMasterAdd)
      {
        dsMasterTable.goToInternalRow(masterRow);
      }
      if(khlx.equals("A"))
      {
        RowMap jsfs=getJjjsgs();
        for(int i=0;i<d_RowInfos.size();i++)
        {
          RowMap detailRow = (RowMap)d_RowInfos.get(i);
          String wzdjid=detailRow.get("wzdjid");
          RowMap wzRow=salePriceBean.getLookupRow(wzdjid);
          double dj=Double.parseDouble(detailRow.get("dj"));
          double jzj=Double.parseDouble(detailRow.get("jzj"))*100/98;
          double xstcl= Double.parseDouble(wzRow.get("xstcl"));
          double hkts=Double.parseDouble(wzRow.get("hkts"));
          detailRow.put("jzj",formatNumber(String.valueOf(jzj), sumFormat));
          if(dj>jzj)
          {
            double xsjzj=Double.parseDouble(jsfs.get("xsjzj"));
            double hltszj=Double.parseDouble(jsfs.get("hltszj"));
            double tclzj=Double.parseDouble(jsfs.get("tclzj"));

            double bs=(dj-jzj)/xsjzj-1;
            detailRow.put("hlts",formatNumber(String.valueOf(hkts+bs*hltszj), sumFormat));//
            detailRow.put("cjtcl",formatNumber(String.valueOf(xstcl+bs*tclzj), sumFormat));//
          }else if(dj<jzj)
          {
            double xsjjs=Double.parseDouble(jsfs.get("xsjjs"));
            double hltsjs=Double.parseDouble(jsfs.get("hltsjs"));
            double tcljs=Double.parseDouble(jsfs.get("tcljs"));

            double bs=(jzj-dj)/xsjjs-1;
            detailRow.put("hlts",formatNumber(String.valueOf(-(hkts+bs*hltsjs)), sumFormat));//
            detailRow.put("cjtcl",formatNumber(wzRow.get("hkts"), sumFormat));//
          }
          else
          {
            detailRow.put("hlts",formatNumber(wzRow.get("hkts"), sumFormat));//
            detailRow.put("cjtcl","0");
          }
        }
      }
      else if(khlx.equals("C"))
      {
        RowMap jsfs=getJjjsgs();
        for(int i=0;i<d_RowInfos.size();i++)
        {
          RowMap detailRow = (RowMap)d_RowInfos.get(i);
          String wzdjid=detailRow.get("wzdjid");
          RowMap wzRow=salePriceBean.getLookupRow(wzdjid);
          double dj=Double.parseDouble(detailRow.get("dj"));
          double jzj=Double.parseDouble(detailRow.get("jzj"))*0.98;
          double xstcl= Double.parseDouble(wzRow.get("xstcl"));
          double hkts=Double.parseDouble(wzRow.get("hkts"));
          detailRow.put("jzj",formatNumber(String.valueOf(jzj), sumFormat));
          if(dj>jzj)
          {
            double xsjzj=Double.parseDouble(jsfs.get("xsjzj"));
            double hltszj=Double.parseDouble(jsfs.get("hltszj"));
            double tclzj=Double.parseDouble(jsfs.get("tclzj"));

            double bs=(dj-jzj)/xsjzj;//formatNumber(String.valueOf(-(xstcl+bs*tcljs)), sumFormat)
            detailRow.put("hlts",formatNumber(String.valueOf(hkts+bs*hltszj), sumFormat));//
            detailRow.put("cjtcl",formatNumber(String.valueOf(xstcl+bs*tclzj), sumFormat));//
          }else if(dj<jzj)
          {
            double xsjjs=Double.parseDouble(jsfs.get("xsjjs"));
            double hltsjs=Double.parseDouble(jsfs.get("hltsjs"));
            double tcljs=Double.parseDouble(jsfs.get("tcljs"));

            double bs=(jzj-dj)/xsjjs;
            detailRow.put("hlts",formatNumber(String.valueOf(-(hkts+bs*hltsjs)), sumFormat));//
            detailRow.put("cjtcl",formatNumber(String.valueOf(-(xstcl+bs*tcljs)), sumFormat));//
          }
          else
          {
            detailRow.put("hlts",formatNumber(wzRow.get("hkts"), sumFormat));//
            detailRow.put("cjtcl","0");
          }
        }
      }
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
   *改变购货单位时引发的操作
   * */
  class Dwtxid_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP);

      String olddwtxid=m_RowInfo.get("dwtxid");
      putDetailInfo(data.getRequest());//保存输入的明细信息
      String dwtxid=m_RowInfo.get("dwtxid");

      RowMap corRow = corpBean.getLookupRow(dwtxid);
        if(olddwtxid.equals(dwtxid))
        {
          return;
        }
        else
        {
          m_RowInfo.put("dwtxid",dwtxid);
          m_RowInfo.put("htbh",req.getParameter("htbh"));
          m_RowInfo.put("htrq",req.getParameter("htrq"));
          m_RowInfo.put("ksrq",req.getParameter("ksrq"));
          m_RowInfo.put("jsrq",req.getParameter("jsrq"));
          m_RowInfo.put("personid",corRow.get("personid"));
          m_RowInfo.put("deptid",corRow.get("deptid"));
          m_RowInfo.put("qddd",req.getParameter("qddd"));
          m_RowInfo.put("khlx",req.getParameter("khlx"));
          dsDetailTable.deleteAllRows();
          d_RowInfos.clear();
        }
        data.setMessage(showJavaScript("form1.dwmc.focus();"));
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
      String wbid = rowInfo.get("wbid");
      RowMap foreignRow =  getForeignBean(req).getLookupRow(wbid);
      String hl = foreignRow.get("hl");
      rowInfo.put("wbid",wbid);
      rowInfo.put("hl",hl);
      double curhl = hl.length()>0 ? Double.parseDouble(hl) : 0 ;
      for(int j=0; j<d_RowInfos.size(); j++)
      {
        RowMap detailrow = (RowMap)d_RowInfos.get(j);
        String jje = detailrow.get("jje");
        double curje = isDouble(jje) ? Double.parseDouble(jje) : 0 ;
        detailrow.put("wbje", formatNumber(curhl==0 ? "" : String.valueOf(curje/curhl),qtyFormat));
      }
     }
    }
  /**
   * 得到用于查找产品单价的bean
   * @param req WEB的请求private B_SaleGoodsSelect saleGoodsBean=null;
   * @return 返回用于查找产品单价的bean
   */
  public LookUp getSalePriceBean(HttpServletRequest req)
  {
    if(salePriceBean == null)
      salePriceBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_SALE_PRICE);
    return salePriceBean;
  }
  public LookUp getForeignBean(HttpServletRequest req)
  {
    engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);
    return wbBean;
  }
}

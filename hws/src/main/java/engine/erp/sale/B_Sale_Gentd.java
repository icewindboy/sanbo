package engine.erp.sale;

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
/**
 * <p>Title: 销售管理--销售货物管理--提单管理--</p>
 * <p>Description: 销售管理--销售货物管理--提单管理<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class B_Sale_Gentd extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL     = "1001";
  public  static final String DETAIL_SALE_ADD = "1002";
  public  static final String DETAIL_CHANGE   = "1009";//从表操作
  public  static final String DETAIL_COPY             ="1013";//复制当前选中行
  public  static final String DEL_NULL = "1014";            //删除数量为空的行
  public  static final String LADING_OUT = "1015";//出库确认
  public  static final String SELECT_STORE = "1019";//选择仓库
  public  static final String DJLX_ONCHANGE = "1020";//
  private static final String MASTER_STRUT_SQL = "SELECT * FROM xs_td WHERE 1<>1 ORDER BY tdbh DESC ";
  private static final String MASTER_SQL    = "SELECT * FROM xs_td WHERE 1=1 AND ? AND fgsid=? ? ORDER BY tdbh DESC ";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM xs_tdhw WHERE 1<>1 ";
  private static final String DETAIL_SQL    = "SELECT * FROM xs_tdhw WHERE tdid= ";//
  public static final String ORDER_DETAIL_SQL ="SELECT * FROM VW_SALE_IMPORT_TD_ORDER_DETAIL WHERE abs(nvl(sl,0))>0 and lb=2 and htid= ";//提货单根据HTID引入相应合同的明细
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsMaster  = new EngineDataSet();//套打的主表
  private EngineDataSet dsDetail  = new EngineDataSet();//套打的从表
  private EngineDataSet dsSearchTable  = new EngineDataSet();//查询用到的数据集
  private EngineDataSet hthwmxTable      = new EngineDataSet();//合同货物明细
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "xs_td");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "xs_tdhw");
  public  boolean isApprove = false;     //是否在审批状态
  private boolean isMasterAdd = true;    //是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private RowMap[] masterRows;
  private ArrayList drows=null;
  private B_ImportOrder b_ImportOrderBean = null; //提货单引用销售合同
  private B_ImportOrderToBackLading b_ImportThOrderBean = null; //退货单引用销售合同
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String djlx="" ;       //提单类型
  private User user = null;
  public boolean isReport = false;
  public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
  public boolean conversion=false;//销售合同的换算数量与数量是否需要强制转换取
  private static ArrayList keys = new ArrayList();
  private static Hashtable table = new Hashtable();
  public  String syskey="";
  public String []zt;
  public String tCopyNumber = "1";
  private String zzsl="";//总数量
  private String zzje="";//总金额
  private String SLSQL="";
  private String JESQL="";
  private String dwtxid = "";
  private String jsfsid =  "";
  private String deptid =  "";
  private String personid =  "";
  private String khlx = "";
  private String sendmodeid = "";
  private String htid="";
  private String tdid ="";
  private String storeid ="";
  private String yfdj ="";
  private String dz ="";
  private String dh ="";
  private String lxr ="";
  private String isnet ="0";
  public boolean canOperate=false;
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_Sale_Gentd getInstance(HttpServletRequest request)
  {
    B_Sale_Gentd b_Sale_GentdBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_Sale_GentdBean";
      b_Sale_GentdBean = (B_Sale_Gentd)session.getAttribute(beanName);
      if(b_Sale_GentdBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        b_Sale_GentdBean = new B_Sale_Gentd();
        b_Sale_GentdBean.qtyFormat = loginBean.getQtyFormat();
        b_Sale_GentdBean.priceFormat = loginBean.getPriceFormat();
        b_Sale_GentdBean.sumFormat = loginBean.getSumFormat();
        b_Sale_GentdBean.fgsid = loginBean.getFirstDeptID();
        b_Sale_GentdBean.loginId = loginBean.getUserID();
        b_Sale_GentdBean.loginName = loginBean.getUserName();
        b_Sale_GentdBean.user = loginBean.getUser();
        b_Sale_GentdBean.syskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");
        if(loginBean.getSystemParam("SC_STORE_UNIT_STYLE").equals("1"))
          b_Sale_GentdBean.conversion = true;
        if(loginBean.getSystemParam("XS_LADINGBILL_HANDWORK").equals("1"))
          b_Sale_GentdBean.canOperate = true;//是否可以手工开提单
        //设置格式化的字段

        b_Sale_GentdBean.dsMasterTable.setColumnFormat("yfdj", b_Sale_GentdBean.priceFormat);
        b_Sale_GentdBean.dsMasterTable.setColumnFormat("yf", b_Sale_GentdBean.sumFormat);
        b_Sale_GentdBean.dsMasterTable.setColumnFormat("zje", b_Sale_GentdBean.sumFormat);
        b_Sale_GentdBean.dsMasterTable.setColumnFormat("zsl", b_Sale_GentdBean.qtyFormat);

        b_Sale_GentdBean.dsDetailTable.setColumnFormat("sl", b_Sale_GentdBean.qtyFormat);
        b_Sale_GentdBean.dsDetailTable.setColumnFormat("hssl", b_Sale_GentdBean.qtyFormat);
        b_Sale_GentdBean.dsDetailTable.setColumnFormat("xsj", b_Sale_GentdBean.priceFormat);
        b_Sale_GentdBean.dsDetailTable.setColumnFormat("dj", b_Sale_GentdBean.priceFormat);
        b_Sale_GentdBean.dsDetailTable.setColumnFormat("jje", b_Sale_GentdBean.sumFormat);
        b_Sale_GentdBean.dsDetailTable.setColumnFormat("xsje", b_Sale_GentdBean.sumFormat);
        b_Sale_GentdBean.dsDetailTable.setColumnFormat("sthssl", b_Sale_GentdBean.qtyFormat);
        b_Sale_GentdBean.dsDetailTable.setColumnFormat("stsl", b_Sale_GentdBean.qtyFormat);

        session.setAttribute(beanName, b_Sale_GentdBean);
      }
    }
    return b_Sale_GentdBean;
  }
  /**
   * 构造函数
   */
  private B_Sale_Gentd()
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
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"tdbh"}, new boolean[]{true}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"tdhwid"}, new String[]{"s_xs_tdhw"}));
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(DETAIL_CHANGE), new Detail_Change());
    addObactioner(String.valueOf(DETAIL_COPY), new Detail_Copy_Add());//DETAIL_COPY
    addObactioner(String.valueOf(DEL_NULL), new Detail_Delete_Null());//Detail_Delete_Null
    addObactioner(String.valueOf(SELECT_STORE), new Stroe_Onchange());
    addObactioner(String.valueOf(DJLX_ONCHANGE), new Djlx_Change());

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
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
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
        m_RowInfo.put("tdrq", today);
        m_RowInfo.put("czyid", loginId);
        m_RowInfo.put("djlx", djlx);
        m_RowInfo.put("zt", "0");
        m_RowInfo.put("dwtxid", dwtxid);
        m_RowInfo.put("jsfsid", jsfsid);
        m_RowInfo.put("deptid", deptid);
        m_RowInfo.put("personid", personid);
        m_RowInfo.put("khlx", khlx);
        m_RowInfo.put("sendmodeid", sendmodeid);
        m_RowInfo.put("storeid", storeid);
        m_RowInfo.put("dz", dz);
        m_RowInfo.put("dh", dh);
        m_RowInfo.put("lxr", lxr);
        m_RowInfo.put("isnet", isnet);
        if(djlx.equals("1"))
          tdbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_td','tdbh') from dual");
        else
          tdbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_td','tdbh','t') from dual");
        m_RowInfo.put("tdbh", tdbh);
        //m_RowInfo.put("khlx", "A");
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
      detailRow.put("sl", rowInfo.get("sl_"+i));//
      detailRow.put("hssl", rowInfo.get("hssl_"+i));//
      detailRow.put("xsje", rowInfo.get("xsje_"+i));//销售金额
      detailRow.put("jje", rowInfo.get("jje_"+i));//净金额
      detailRow.put("xsj", rowInfo.get("xsj_"+i));//销售价
      detailRow.put("zk", rowInfo.get("zk_"+i));//折扣
      detailRow.put("dj", rowInfo.get("dj_"+i));//单价
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
      detailRow.put("hthwid", rowInfo.get("hthwid_"+i));
      //保存用户自定义的字段
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
  }
      /*打印主表**/
  public final RowMap[] getMasterRows()
  {
    return masterRows;
  }
      /*打印从表**/
  public final  RowMap[] getDetailRows()
  {
    RowMap[] rows = new RowMap[drows.size()];
    drows.toArray(rows);
    return rows;
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
    //tdid = dsMasterTable.getValue("tdid");//关链
    String SQL = DETAIL_SQL + (isMasterAdd ? "-1" : tdid);
    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
    {
      dsDetailTable.open();
    }
    else
    {
      dsDetailTable.refresh();
    }
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
      storeid="";
      HttpServletRequest request = data.getRequest();
      engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);

      htid = request.getParameter("htid");
      hthwmxTable = new EngineDataSet();
      setDataSetProperty(hthwmxTable, ORDER_DETAIL_SQL+"'"+htid+"'");
      if(hthwmxTable.isOpen())
        hthwmxTable.refresh();
      else
        hthwmxTable.openDataSet();

      if(hthwmxTable.getRowCount()<1)
        return;
      else
      {
        hthwmxTable.first();
        tdid = dataSetProvider.getSequence("s_xs_td");
        dwtxid = hthwmxTable.getValue("dwtxid");
        jsfsid =   hthwmxTable.getValue("jsfsid");
        deptid =  hthwmxTable.getValue("deptid");
        personid =  hthwmxTable.getValue("personid");
        khlx =  hthwmxTable.getValue("khlx");
        sendmodeid =  hthwmxTable.getValue("sendmodeid");
        yfdj =  hthwmxTable.getValue("yfdj");
        isnet =  hthwmxTable.getValue("isnet");
        djlx="1";//1:新增提货单
        RowMap corRow = corpBean.getLookupRow(dwtxid);
        dz = corRow.get("dz");
        lxr = corRow.get("lxr");
        dh = corRow.get("tel");
      }
      isApprove = false;
      isReport = false;
      isMasterAdd = true;

      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);

      zt = new String[]{""};
      if(!dsMasterTable.isOpen())
        dsMasterTable.open();
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      isReport=false;
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
      //String Id = null;
      if(isMasterAdd){
        String tdbh = rowInfo.get("tdbh");
        String count = dataSetProvider.getSequence("select count(*) from xs_td t where t.tdbh='"+tdbh+"'");
        if(!count.equals("0"))
        {
          if(djlx.equals("1"))
            tdbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_td','tdbh') from dual");
          else
            tdbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_td','tdbh','t') from dual");
        }
        ds.insertRow(false);
        tdid = dataSetProvider.getSequence("s_xs_td");
        //log.debug("hk:"+tdid);
        ds.setValue("tdid", tdid);//主健
        ds.setValue("zt","0");
        ds.setValue("tdbh",tdbh);
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("czyid", loginId);
        ds.setValue("czy", loginName);//操作员
        ds.setValue("fgsid", fgsid);//分公司
        ds.setValue("isnet", isnet);
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
        //新添的记录
        detail.setValue("tdid", tdid);
        detail.setValue("cpid", detailrow.get("cpid"));//?
        detail.setValue("dmsxid", detailrow.get("dmsxid"));

       double xsj = detailrow.get("xsj").length() > 0 ? Double.parseDouble(formatNumber(detailrow.get("xsj"),priceFormat)) : 0;//销售价
       double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;  //数量
       double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(formatNumber(detailrow.get("dj"),priceFormat)) : 0;//单价

        //double zk = detailrow.get("zk").length() > 0 ? Double.parseDouble(detailrow.get("zk")) : 0;//折扣
        double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;
        double xsje = sl*xsj;
        double jje = sl*dj;

        zje =zje+jje;
        zsl=zsl+sl;
        detail.setValue("sl", detailrow.get("sl"));//数量
        detail.setValue("xsj", detailrow.get("xsj"));
        detail.setValue("xsje", formatNumber(String.valueOf(sl * xsj),sumFormat));
        detail.setValue("zk", detailrow.get("zk"));//折扣
        detail.setValue("dj", detailrow.get("dj"));//单价
        detail.setValue("hssl", detailrow.get("hssl"));//
        detail.setValue("wzdjid", detailrow.get("wzdjid"));  //单价?
        detail.setValue("jje",formatNumber(String.valueOf(sl * dj),sumFormat));
        detail.setValue("bz", detailrow.get("bz"));//备注
        if(rowInfo.get("djlx").equals("-1"))
        {
          detail.setValue("sl", String.valueOf(-sl));//数量
          detail.setValue("hssl", String.valueOf(-hssl));//
          detail.setValue("xsje", formatNumber(String.valueOf(-sl * xsj),sumFormat));
          detail.setValue("jje",formatNumber(String.valueOf(-sl * dj),sumFormat));
        }
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        totalSum = totalSum.add(detail.getBigDecimal("xsje"));
        totalZje =totalZje.add(detail.getBigDecimal("jje"));
        detail.next();
      }
      //保存主表数据
      ds.setValue("tdrq", rowInfo.get("tdrq"));//提单日期
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("jsfsid", rowInfo.get("jsfsid"));//结算方式ID
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));//购货单位ID
      ds.setValue("personid", rowInfo.get("personid"));//人员ID
      ds.setValue("zsl", String.valueOf(zsl));//总数量
      ds.setValue("dwt_dwtxId", rowInfo.get("dwt_dwtxId"));//承运单位ID
      ds.setValue("ddfy", rowInfo.get("ddfy"));//代垫费用
      ds.setValue("yf", rowInfo.get("yf"));//运费
      ds.setValue("ztms", rowInfo.get("ztms"));//状态描述
      ds.setValue("storeid", rowInfo.get("storeid"));//仓库id
      ds.setValue("hkrq", rowInfo.get("hkrq"));//回款日期
      ds.setValue("hkts", rowInfo.get("hkts"));//回款天数
      ds.setValue("djlx", rowInfo.get("djlx"));//单据类型
      ds.setValue("djh", "");
      ds.setValue("sendmodeid", rowInfo.get("sendmodeid"));//sendmodeid
      ds.setValue("dz", rowInfo.get("dz"));
      ds.setValue("yfdj", rowInfo.get("yfdj"));
      ds.setValue("lxr", rowInfo.get("lxr"));
      ds.setValue("dh", rowInfo.get("dh"));
      if(rowInfo.get("djlx").equals("-1"))
      {
        ds.setValue("zsl", String.valueOf(-zsl));
        ds.setValue("zje", formatNumber(String.valueOf(-zje),sumFormat));
      }
      else
      {
        ds.setValue("zsl", String.valueOf(zsl));//总金额
        ds.setValue("zje", formatNumber(String.valueOf(zje),sumFormat));
      }
      ds.setValue("khlx", rowInfo.get("khlx"));//客户类型
      ds.setValue("fgsid", fgsid);//分公司
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      isMasterAdd=false;
      data.setMessage(showJavaScript("window.close()"));
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
        //去除数量小于零的判断 2004.5.16 modify by jac
        if(Double.parseDouble(sl)==0)
          return showJavaScript("alert('数量不能等于零!');");
            /*if(Double.parseDouble(sl)<=0)
              return showJavaScript("alert('数量不能小零!');");*/
        String hssl = detailrow.get("hssl");
        if((temp = checkNumber(sl, detailProducer.getFieldInfo("hssl").getFieldname())) != null)
          return temp;
        String xsj = detailrow.get("xsj");
        if((temp = checkNumber(xsj, detailProducer.getFieldInfo("xsj").getFieldname())) != null)
          return temp;
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
      String temp = rowInfo.get("tdrq");
      if(temp.equals(""))
        return showJavaScript("alert('提单日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法提单日期！');");
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
      return null;
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
      putDetailInfo(data.getRequest());
    }
  }
  /**
   * 仓库改变
   * */
  class Stroe_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      m_RowInfo.put(req);
      if(m_RowInfo.get("tdrq").equals(""))
        data.setMessage(showJavaScript("alert('开单日期不能空!')"));

      String yfdj =  m_RowInfo.get("yfdj");
      String djlx =  m_RowInfo.get("djlx");
      storeid = req.getParameter("storeid");
      if(storeid.equals(""))
        return;
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      isReport=false;

      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }

      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);

      EngineDataSet dsimport = new EngineDataSet();
      setDataSetProperty(dsimport,ORDER_DETAIL_SQL+"'"+htid+"' and (storeid='"+storeid+"' or storeid is null)");
      dsimport.open();
      BigDecimal bd = new BigDecimal(0);
      BigDecimal byf = new BigDecimal(0);
      double max = 0;
      //String hlts = orderRow.get("hlts").equals("")?"0":orderRow.get("hlts");
      dsimport.first();
      for(int i=0; i < dsimport.getRowCount(); i++)
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("tdhwid", "-1");
        dsDetailTable.setValue("tdid", tdid);
        dsDetailTable.setValue("cpid", dsimport.getValue("cpid"));
        dsDetailTable.setValue("hthwid", dsimport.getValue("hthwid"));
        dsDetailTable.setValue("wzdjid", dsimport.getValue("wzdjid"));

        dsDetailTable.setValue("cjtcl", dsimport.getValue("cjtcl"));
        dsDetailTable.setValue("jzj", dsimport.getValue("jzj"));
        dsDetailTable.setValue("jxts", dsimport.getValue("jxts"));
        dsDetailTable.setValue("hltcl", dsimport.getValue("hltcl"));
        dsDetailTable.setValue("hlts", dsimport.getValue("hlts"));
        yfdj = dsimport.getValue("yfdj");
        String dmsxid = dsimport.getValue("dmsxid");
        dsDetailTable.setValue("dmsxid", dsimport.getValue("dmsxid"));
        String hlts =dsimport.getValue("hlts").equals("")?"0":dsimport.getValue("hlts");
        double dhlts = Double.parseDouble(hlts);
        if(max<dhlts)
          max=dhlts;//2004-4-13取最大回笼天数
        double sl=Double.parseDouble(dsimport.getValue("sl").equals("")?"0":dsimport.getValue("sl"));
        double hssl=Double.parseDouble(dsimport.getValue("hssl").equals("")?"0":dsimport.getValue("hssl"));
        //double hsbl=Double.parseDouble(dsimport.getValue("hsbl").equals("")?"0":dsimport.getValue("hsbl"));
        double stsl=Double.parseDouble(dsimport.getValue("skdsl").equals("")?"0":dsimport.getValue("skdsl"));
        double skdhssl=Double.parseDouble(dsimport.getValue("skdhssl").equals("")?"0":dsimport.getValue("skdhssl"));
        //double hssl =sl/hsbl;
        //double xhssl = hssl/sl*(sl-stsl);
        double xsj = Double.parseDouble(dsimport.getValue("xsj").equals("")?"0":dsimport.getValue("xsj"));
        double dj = Double.parseDouble(dsimport.getValue("dj").equals("")?"0":dsimport.getValue("dj"));

        dsDetailTable.setValue("hssl", String.valueOf(hssl-skdhssl));
        dsDetailTable.setValue("sl", String.valueOf(sl));
        dsDetailTable.setValue("xsje", String.valueOf(xsj*(sl)));
        dsDetailTable.setValue("jje",String.valueOf(dj*(sl)));
        dsDetailTable.setValue("xsj", dsimport.getValue("xsj"));
        dsDetailTable.setValue("zk", dsimport.getValue("zk"));
        dsDetailTable.setValue("dj", dsimport.getValue("dj"));
        dsDetailTable.setValue("bz", dsimport.getValue("bz"));

        byf = byf.add(new BigDecimal(yfdj.equals("")?"0":yfdj).multiply(new BigDecimal(String.valueOf(sl))));

        dsDetailTable.post();

        RowMap detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
        dsimport.next();
      }
      String ts = m_RowInfo.get("hkts");
      String tdrq = m_RowInfo.get("tdrq");
      Date startdate = new SimpleDateFormat("yyyy-MM-dd").parse(tdrq);//提单的开单日期
      Date enddate = new Date(startdate.getTime() + (long)max*(60*60*24*1000));//毫秒
      if(ts.equals(""))
      {
        m_RowInfo.put("hkts",String.valueOf((int)max));
        m_RowInfo.put("hkrq",new SimpleDateFormat("yyyy-MM-dd").format(enddate));
      }
      m_RowInfo.put("yf",engine.util.Format.formatNumber(byf.toString(),"#0.00"));
      m_RowInfo.put("yfdj",yfdj);
      m_RowInfo.put("djlx",djlx);
    }
  }
  /**
   * 改变单据类型时引发的操作
   * */
  class Djlx_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      m_RowInfo.put(req);
      putDetailInfo(req);//保存输入的明细信息
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
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
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
      for(int i=0;i<d_RowInfos.size();i++)
      {
        RowMap detailRow = (RowMap)d_RowInfos.get(i);
        String sl = detailRow.get("sl");
        String temp = null;
        sl = sl.equals("")?"0":sl;
        if((temp = checkNumber(sl, "数量")) != null)
        {
          data.setMessage(showJavaScript("alert('数量非法!')"));
          return;
        }
      }
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
        double dsl = Double.parseDouble(sl.equals("")?"0":sl);
        if(dsl==0)
        {
          EngineDataSet ds = getDetailTable();
          d_RowInfos.remove(i);
          ds.goToRow(i);
          ds.deleteRow();
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
  public B_ImportOrder getb_ImportOrderBean(HttpServletRequest req)
  {
    if(b_ImportOrderBean == null)
      b_ImportOrderBean = B_ImportOrder.getInstance(req);
    return b_ImportOrderBean;
  }
  /**
   * 退货单引用
   * 对应引入合同
   * 得到用于查找合同编号的bean
   * @param req WEB的请求
   * @return 返回用于查找合同编号的bean
   */
  public B_ImportOrderToBackLading getb_ImportThOrderBean(HttpServletRequest req)
  {
    if(b_ImportThOrderBean == null)
      b_ImportThOrderBean = B_ImportOrderToBackLading.getInstance(req);
    return b_ImportThOrderBean;
  }
}
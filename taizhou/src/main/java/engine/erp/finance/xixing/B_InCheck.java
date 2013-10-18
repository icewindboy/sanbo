package engine.erp.finance.xixing;

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
import engine.util.StringUtils;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 内部管理--内部结算管理--</p>
 * <p>Description: 内部管理--内部结算管理--<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 *  * @version 1.0
 */
public final class B_InCheck extends BaseAction implements Operate
{

  public  static final String SHOW_DETAIL =        "80000001";
  public  static final String DETAIL_SALE_ADD =    "80000002";
  public  static final String TD_RETURN_ADD =      "80000003";//提单退货新增
  public  static final String CANCER_APPROVE =     "80000004";
  public  static final String BALANCE_OVER =       "80000005";//完成
  public  static final String IMPORT_TD =          "80000006";//引入提单主-从表
  public  static final String DWTXID_CHANGE =      "80000007";
  public  static final String MASTER_ADD =         "80000008";
  public  static final String MASTER_EDIT =        "80000009";
  public  static final String AUTO_CANCER =        "80000011";
  public  static final String MASTER_DETAIL_POST = "80000012";
  public  static final String DEPT_CHANGE  =       "80000013";
  public  static final String REPORT       =       "80000014";
  public  static final String CONFIRM_RETURN      = "80000015";
  public  static final String CONFIRM_OVER        = "80000016";
  public  static final String KCDEPT_CHANGE  =      "80000017";
  public  static final String CONFIRM_OVER_CANCER = "803423226";


  private static final String MASTER_STRUT_SQL = "SELECT * FROM cw_nbjs WHERE 1<>1 ";
  private static final String MASTER_SEARCH_SQL    = "SELECT * FROM cw_nbjs WHERE 1=1 AND ? AND fgsid =? ?  ?  order by nbjsdh desc";
  private static final String MASTER_SQL    = "SELECT cw_nbjs.* FROM cw_nbjs WHERE 1=1 ? AND cw_nbjs.fgsid =? ?  ? ";

  private static final String JE_SQL    = "select SUM(nvl(je,0))zje FROM cw_nbjs WHERE 1=1 AND ? AND fgsid =? ? ORDER BY nbjsdh DESC ";
  private static final String HXJE_SQL    = "select SUM(nvl(hxje,0))zhxje FROM cw_nbjs WHERE 1=1 AND ? AND fgsid =? ? ORDER BY nbjsdh DESC ";
  private static final String WHXJE_SQL    = "select SUM(nvl(whxje,0))zwhxje FROM cw_nbjs WHERE 1=1 AND ? AND fgsid =? ? ORDER BY nbjsdh DESC ";

  private static final String COLLECT_SQL = "SELECT sum(nvl(a.je,0)-nvl(a.hxje,0))totalWhxje FROM cw_nbjs a WHERE   a.fgsid=? and  a.personid='?'";//汇总

  private static final String DETAIL_STRUT_SQL = "SELECT * FROM cw_nbjshx WHERE 1<>1  order by nbjsmxid ";
  private static final String DETAIL_SQL    = "SELECT * FROM cw_nbjshx WHERE nbjsid='?' order by nbjsmxid ";//

  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM cw_nbjs WHERE nbjsid='?'";
  private static final String AUTO_CANCER_SQL = "SELECT * FROM vw_incheck_autobalance where 1=1 and fgsid=?   ORDER BY jsrq";
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet tdMxTable      = new EngineDataSet();//提单明细

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "cw_nbjs","cw_nbjs");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "cw_nbjshx");
  public  boolean isApprove = false;     //是否在审批状态
  private boolean isMasterAdd = true;    //是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private engine.erp.finance.xixing.ImportCheckProduct ImportCheckProductBean = null; //提单货物引用
  //private LookUp salePriceBean = null; //销售单价的bean的引用, 用于提取销售单价
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  String loginid = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String nbjsid = null;
  private User user = null;
  private int rownum=0;
  public String nbjsmxid=null;
  public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
  private BigDecimal zje=new BigDecimal(0) ;
  public String []zt;
  private BigDecimal zhxje = new BigDecimal(0);//从表总核销金额
  public boolean isReport = false;
  private String zzhxje="";//总核销金额
  private String zzje="";//总金额
  private String zzwhxje="";//总未核销金额
  private String JESQL;
  private String HXJESQL;
  private String WHXJESQL;
  public String dwdm="";
  public String dwmc="";
  private String totalWhxje="";

  public  ArrayList listCorpType = new ArrayList();
  public  ArrayList orderFieldCodes = new ArrayList(); //排序的字段编码
  public  ArrayList orderFieldNames = new ArrayList(); //排序的字段名称
  public  ArrayList selectedOrders  = null;
  private String    orderBy = "";
  private String SEARCHSQL = "";
  private double zjsje=0;//自动核消里核消金额合计



  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_InCheck getInstance(HttpServletRequest request)
  {
    B_InCheck B_InCheckBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_InCheckBean";
      B_InCheckBean = (B_InCheck)session.getAttribute(beanName);
      if(B_InCheckBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        B_InCheckBean = new B_InCheck();
        B_InCheckBean.qtyFormat = loginBean.getQtyFormat();
        B_InCheckBean.priceFormat = loginBean.getPriceFormat();
        B_InCheckBean.sumFormat = loginBean.getSumFormat();
        B_InCheckBean.fgsid = loginBean.getFirstDeptID();
        B_InCheckBean.loginid = loginBean.getUserID();
        B_InCheckBean.loginName = loginBean.getUserName();
        //设置格式化的字段
        B_InCheckBean.user = loginBean.getUser();
        B_InCheckBean.dsMasterTable.setColumnFormat("je", B_InCheckBean.priceFormat);
        B_InCheckBean.dsMasterTable.setColumnFormat("hxje", B_InCheckBean.priceFormat);
        B_InCheckBean.dsMasterTable.setColumnFormat("whxje", B_InCheckBean.priceFormat);


        B_InCheckBean.dsDetailTable.setColumnFormat("jsje", B_InCheckBean.sumFormat);
        session.setAttribute(beanName, B_InCheckBean);
      }
    }
    return B_InCheckBean;
  }
  /**
   * 构造函数
   */
  private B_InCheck()
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
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"nbjsdh"}, new String[]{"SELECT pck_base.billNextCode('cw_nbjs','nbjsdh') from dual"}));
    //dsMasterTable.setSort(new SortDescriptor("", new String[]{"nbjsdh"}, new boolean[]{true}, null, 0));
    dsMasterTable.setTableName("cw_nbjs");
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"nbjshxid"}, new String[]{"s_cw_nbjshx"}));
    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    Master_Detail_Post masterDetailPost = new Master_Detail_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());

    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(TD_RETURN_ADD), masterAddEdit);//
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(MASTER_ADD),  masterAddEdit);
    addObactioner(String.valueOf(MASTER_EDIT),  masterAddEdit);

    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(MASTER_DETAIL_POST), masterDetailPost);
    addObactioner(String.valueOf(CONFIRM_RETURN), masterDetailPost);

    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    //addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_SALE_ADD), new Detail_SALE_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    //addObactioner(String.valueOf(IMPORT_TD), new Import_XSTD());
    //&#$//审核部分
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());


    addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());
    addObactioner(String.valueOf(BALANCE_OVER), new Balance_Over());//完成

    addObactioner(String.valueOf(DEPT_CHANGE),  new Dept_Change());
    addObactioner(String.valueOf(KCDEPT_CHANGE),  new KCDept_Change());
    addObactioner(String.valueOf(AUTO_CANCER),  new Detail_Auto_Cancer());
    addObactioner(String.valueOf(REPORT), new Approve());

    addObactioner(String.valueOf(ORDERBY), new Orderby());
    addObactioner(String.valueOf(CONFIRM_OVER), new Balance_Over());
     addObactioner(String.valueOf(CONFIRM_OVER_CANCER), new Cancer_Approve());

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
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String nbjsdh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('cw_nbjs','nbjsdh') from dual");
        m_RowInfo.put("nbjsdh", nbjsdh);
        m_RowInfo.put("czrq", today);//制单日期
        m_RowInfo.put("czy", loginName);//操作员
        m_RowInfo.put("rq", today);
        m_RowInfo.put("czyid", loginid);
        m_RowInfo.put("fgsid", fgsid);
        m_RowInfo.put("zt", "0");
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
      //detailRow.put("nbjsmxid", rowInfo.get("nbjsmxid_"+i));//
      detailRow.put("jsje", formatNumber(rowInfo.get("jsje_"+i), sumFormat));//结算金额
      detailRow.put("tcl", rowInfo.get("tcl_"+i));//提成率
      detailRow.put("tcj", rowInfo.get("tcj_"+i));//提成奖
      detailRow.put("yhrq", rowInfo.get("yhrq_"+i));//应回日期
      detailRow.put("nbjsmxid", rowInfo.get("nbjsmxid_"+i));//应回日期
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
  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    return dsDetailTable;
  }
  /*得到总核销金额*/
 public final String getZhxje()
 {
   return zzhxje;
 }
  /*得到总金额*/
 public final String getZje()
 {
   return zzje;
 }
  /*得到总金额*/
 public final String getZwhxje()
 {
   return zzwhxje;
 }
  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    String SQL =  combineSQL(DETAIL_SQL,"?",new String[]{(isMasterAdd ? "-1" : nbjsid)});
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
       masterProducer.init(request, loginid);
       detailProducer.init(request, loginid);
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
       if(dsDetailTable.isOpen())
         dsMasterTable.readyRefresh();
       dsMasterTable.refresh();
       //打开从表
       nbjsid = dsMasterTable.getValue("nbjsid");

       openDetailTable(false);
       initRowInfo(true, false, true);
       initRowInfo(false, false, true);

     }
   }
  /**
   * 完成
   */
  class Balance_Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      nbjsid = dsMasterTable.getValue("nbjsid");
      openDetailTable(false);
      if(dsDetailTable.getRowCount()==0)
      {
        if(String.valueOf(BALANCE_OVER).equals(action))
        {
          data.setMessage(showJavaScript("if(confirm('还没有核销!要强制完成吗?')) sumitForm("+CONFIRM_OVER+",-1);"));//保存确认
          return;

        }

        dsMasterTable.setValue("zt","8");
        dsMasterTable.saveChanges();

      }

    }
  }
   /**
    * 取消
    */
   class Cancer_Approve implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {


       dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
       nbjsid = dsMasterTable.getValue("nbjsid");
       openDetailTable(false);
       if(dsDetailTable.getRowCount()>0)
       {
         if(String.valueOf(CANCER_APPROVE).equals(action))
        {
           data.setMessage(showJavaScript("if(confirm('该单据已有核销的货物!要强制取消审批吗，取消后结算的货物将被删除?')) sumitForm("+CONFIRM_OVER_CANCER+",-1);"));//保存确认
         return;
         }

       dsDetailTable.deleteAllRows();
       dsDetailTable.post();
       dsDetailTable.saveChanges();

       }

       ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
       approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(),"in_check");

     }
  }
   //&#$
   /**
    * 添加到审核列表的操作类
    */
   public class Add_Approve  implements Obactioner,ApproveListener
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
       ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
       String content = dsMasterTable.getValue("nbjsdh");
       String deptid = dsMasterTable.getValue("deptid");
       approve.putAproveList(dsMasterTable, dsMasterTable.getRow(),"in_check", content,deptid, this);
     }
     public void processApprove(ApproveResponse[] reponses) throws Exception
     {
       if(reponses==null||reponses.length==0)
         return;
       for(int i=0;i<reponses.length;i++)
       {
         String tmp = reponses[i].getProjectFlowValue();
         if(reponses[i].getProjectFlowCode().equals("sale_special_balance"))
         {
           reponses[i].add();
         }
         else
           reponses[i].skip();
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
      dwdm ="";
      dwmc ="";

      //&#$
      isApprove = false;
      isReport=false;
      zjsje=0;

      zhxje = new BigDecimal(0);//从表总核销金额初始化
      zje=new BigDecimal(0) ;//主表的结算金额
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;

      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginid);
      detailProducer.init(request, loginid);
      if(!dsMasterTable.isOpen())
      {
        dsMasterTable.setQueryString(MASTER_STRUT_SQL);
        dsMasterTable.openDataSet();
      }
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("rq$a", startDay);
      row.put("rq$b", today);

      zt = new String[]{""};
      isMasterAdd = true;
      String SQL = " AND cw_nbjs.zt<>8 AND cw_nbjs.zt<>4 ";
      String MSQL =  combineSQL(MASTER_SQL, "?", new String[]{" and cw_nbjs."+user.getHandleDeptValue("deptid"), fgsid, SQL," order by cw_nbjs.nbjsdh desc"});

      masterProducer.init(data.getRequest(), loginid);
      masterProducer.getWhereInfo().clearWhereValues();
      listCorpType.clear();
      dsMasterTable.setQueryString(MSQL);
      //准备刷新
      dsMasterTable.readyRefresh();
      //排序的字段
      orderBy = "";//"dwdq.areacode, d.dwdm";
      if(selectedOrders == null)
        selectedOrders = new ArrayList();
      else
        selectedOrders.clear();

      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code.equals("1"))
        submitType=true;
      else
        submitType=false;

      orderFieldCodes.clear();
      orderFieldNames.clear();

      FieldInfo[] fields = masterProducer.getAllField();
      for(int i=0; i<fields.length; i++)
      {
        String linkTable = fields[i].getLinktable();
        if(linkTable == null)
        {
          orderFieldCodes.add(masterProducer.getTableAliasName()+"."+fields[i].getFieldcode());
          orderFieldNames.add(fields[i].getFieldname());
        }
        else
        {
          String[] fieldCodes = fields[i].getShowFields();
          String[] fieldNames = fields[i].getShowFieldNames();
          for(int j=0; fieldCodes!=null && j<fieldCodes.length; j++)
          {
            orderFieldCodes.add(linkTable+"."+fieldCodes[j]);
            orderFieldNames.add(fieldNames[j]);
          }
        }
      }

      JESQL =  combineSQL(JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      HXJESQL = combineSQL(HXJE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      WHXJESQL = combineSQL(WHXJE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});


      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      EngineDataSet dshxje = new EngineDataSet();
      setDataSetProperty(dshxje,HXJESQL);
      dshxje.open();
      dshxje.first();
      if(dshxje.getRowCount()<1)
        zzhxje="0";
      else
        zzhxje=dshxje.getValue("zhxje");

      EngineDataSet dswhxje = new EngineDataSet();
      setDataSetProperty(dswhxje,WHXJESQL);
      dswhxje.open();
      dswhxje.first();
      if(dswhxje.getRowCount()<1)
        zzwhxje="0";
      else
        zzwhxje=dswhxje.getValue("zwhxje");


      zzhxje = zzhxje.equals("")?"0":zzhxje;
      zzje = zzje.equals("")?"0":zzje;
      zzwhxje = zzwhxje.equals("")?"0":zzwhxje;

      zzhxje = formatNumber(zzhxje, priceFormat);
      zzwhxje = formatNumber(zzwhxje, priceFormat);
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
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterTable.getInternalRow();
      nbjsid = dsMasterTable.getValue("nbjsid");
      //打开从表
      openDetailTable(false);
    }
  }
  /**
  * ---------------------------------------------------------
  * 主表添加或修改操作的触发类
  */
 class Master_Add implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     //&#$
      isApprove = false;
      /*
     if(String.valueOf(EDIT).equals(action))
     {
       isMasterAdd=false;
       dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
       masterRow = dsMasterTable.getInternalRow();
       nbjsid = dsMasterTable.getValue("nbjsid");
     }
     else{
      */
       isMasterAdd=true;
       //打开从表
     //}
     synchronized(dsDetailTable){
       openDetailTable(isMasterAdd);
     }
     initRowInfo(true, isMasterAdd, true);
     initRowInfo(false, isMasterAdd, true);
     data.setMessage(showJavaScript("masterAdd();"));
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
      zjsje=0;

     if(String.valueOf(EDIT).equals(action)||String.valueOf(MASTER_EDIT).equals(action))
     {
       isMasterAdd=false;
       rownum = Integer.parseInt(data.getParameter("rownum"));
       dsMasterTable.goToRow(rownum);
       masterRow = dsMasterTable.getInternalRow();
       nbjsid = dsMasterTable.getValue("nbjsid");
     }
     else{
       isMasterAdd=true;
       //打开从表
     }
     synchronized(dsDetailTable){
       openDetailTable(isMasterAdd);
     }
     initRowInfo(true, isMasterAdd, true);
     initRowInfo(false, isMasterAdd, true);
     if(String.valueOf(MASTER_ADD).equals(action)||String.valueOf(MASTER_EDIT).equals(action))
       data.setMessage(showJavaScript("masterAdd();"));
     else
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
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      //得到主表主键值
      String nbjsid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        nbjsid = dataSetProvider.getSequence("s_cw_nbjs");
        String nbjsdh = rowInfo.get("nbjsdh");
        String count = dataSetProvider.getSequence("select count(*) from cw_nbjs t where t.nbjsdh='"+nbjsdh+"'");
        if(!count.equals("0"))
          nbjsdh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('cw_nbjs','nbjsdh') from dual");
        ds.setValue("nbjsdh", nbjsdh);//

        ds.setValue("nbjsid", nbjsid);//主健
        ds.setValue("zt","0");
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("czyid", loginid);
        ds.setValue("czy", loginName);//操作员
        ds.setValue("fgsid", fgsid);//分公司
      }
      //保存主表数据
      ds.setValue("personid", rowInfo.get("personid"));
      ds.setValue("deptid", rowInfo.get("deptid"));
      ds.setValue("kc__deptid", rowInfo.get("kc__deptid"));
      ds.setValue("rq", rowInfo.get("rq"));
      ds.setValue("jsdh", rowInfo.get("jsdh"));
      ds.setValue("je", rowInfo.get("je"));
      ds.setValue("zh", rowInfo.get("zh"));
      ds.setValue("yh", rowInfo.get("yh"));
      ds.setValue("ztms", rowInfo.get("ztms"));
      ds.setValue("bz", rowInfo.get("bz"));
      ds.setValue("hxje","0");
      ds.setValue("whxje",rowInfo.get("je"));

      ds.post();
      ds.saveChanges();

      //与合计相关
      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      EngineDataSet dshxje = new EngineDataSet();
      setDataSetProperty(dshxje,HXJESQL);
      dshxje.open();
      dshxje.first();
      if(dshxje.getRowCount()<1)
        zzhxje="0";
      else
        zzhxje=dshxje.getValue("zhxje");

      EngineDataSet dswhxje = new EngineDataSet();
      setDataSetProperty(dswhxje,WHXJESQL);
      dswhxje.open();
      dswhxje.first();
      if(dswhxje.getRowCount()<1)
        zzwhxje="0";
      else
        zzwhxje=dswhxje.getValue("zwhxje");

      zzhxje = zzhxje.equals("")?"0":zzhxje;
      zzje = zzje.equals("")?"0":zzje;
      zzwhxje = zzwhxje.equals("")?"0":zzwhxje;

      zzhxje = formatNumber(zzhxje, priceFormat);
      zzwhxje = formatNumber(zzwhxje, priceFormat);
      zzje = formatNumber(zzje, priceFormat);

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);//重新初始化从表的各行信息
        initRowInfo(false, false, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
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
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("rq");
      if(temp.equals(""))
        return showJavaScript("alert('日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法日期！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择部门！');");
      temp = rowInfo.get("personid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择业务员！');");
      temp = rowInfo.get("je");
      if(temp.equals(""))
        return showJavaScript("alert('金额不能为空');");
      temp = checkNumber(temp,"金额");
      if(temp!=null)
        return temp;
      double dje = Double.parseDouble(rowInfo.get("je"));
      if(dje<0)
        return showJavaScript("alert('金额不能小于0!');");
      return null;
    }
  }
  /**
   * 主从表保存
   */
  class Master_Detail_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();



      ds.goToRow(rownum);
      RowMap rowInfo = getMasterRowinfo();
      zje = new BigDecimal(rowInfo.get("je"));
      BigDecimal zjsje = new BigDecimal(0);
      //校验表单数据
     String temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }

      //保存从表的数据
      RowMap detailrow = null;

      EngineDataSet detail = getDetailTable();
      double sumjsje=0;

      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
        detail.setValue("nbjsid", nbjsid);
        zjsje = zjsje.add(new BigDecimal(detailrow.get("jsje").equals("")?"0":detailrow.get("jsje")));
        double jsje = detailrow.get("jsje").length() > 0 ? Double.parseDouble(detailrow.get("jsje")) : 0;//结算金额
        detail.setValue("jsje", detailrow.get("jsje"));//结算金额
        sumjsje +=jsje;
        nbjsmxid=detailrow.get("nbjsmxid");



        detail.post();
        detail.next();
      }
      double dzje = Double.parseDouble(rowInfo.get("je"));
      if(zje.compareTo(zjsje)!=0&&(String.valueOf(MASTER_DETAIL_POST).equals(action)))
      {
        data.setMessage(showJavaScript("if(confirm('核销金额与结算金额不一致,确认保存吗?')) sumitForm("+CONFIRM_RETURN+",-1);"));
        return;
      }

      ds.setValue("hxje",formatNumber(String.valueOf(sumjsje),priceFormat));


      ds.setValue("whxje",formatNumber(String.valueOf(Double.parseDouble(rowInfo.get("je"))-sumjsje),priceFormat));
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);

      //与合计相关
      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      EngineDataSet dshxje = new EngineDataSet();
      setDataSetProperty(dshxje,HXJESQL);
      dshxje.open();
      dshxje.first();
      if(dshxje.getRowCount()<1)
        zzhxje="0";
      else
        zzhxje=dshxje.getValue("zhxje");

      EngineDataSet dswhxje = new EngineDataSet();
      setDataSetProperty(dswhxje,WHXJESQL);
      dswhxje.open();
      dswhxje.first();
      if(dswhxje.getRowCount()<1)
        zzwhxje="0";
      else
        zzwhxje=dswhxje.getValue("zwhxje");

      zzhxje = zzhxje.equals("")?"0":zzhxje;
      zzje = zzje.equals("")?"0":zzje;
      zzwhxje = zzwhxje.equals("")?"0":zzwhxje;

      zzhxje = formatNumber(zzhxje, priceFormat);
      zzwhxje = formatNumber(zzwhxje, priceFormat);
      zzje = formatNumber(zzje, priceFormat);

      data.setMessage(showJavaScript("backList();"));
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
      RowMap rowInfo = getMasterRowinfo();
      String temp = null;
      RowMap detailrow = null;
      zhxje = new BigDecimal(0);
      if(d_RowInfos.size()==0)
        return null;//从表可以为空!
      BigDecimal zwhje = new BigDecimal(totalWhxje.equals("")?"0":totalWhxje);//总未核销金额
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String jsje = detailrow.get("jsje");
        if((temp = checkNumber(jsje, detailProducer.getFieldInfo("jsje").getFieldname())) != null)
          return temp;
        zhxje = zhxje.add(new BigDecimal(jsje));
      }
      if(zwhje.compareTo(zhxje)<0)
        return showJavaScript("alert('不能核销这么多款!本次总共最多只能核销:"+zwhje+"')");
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
      nbjsid = ds.getValue("nbjsid");
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM cw_nbjs a WHERE a.nbjsid='"+nbjsid+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        data.setMessage(showJavaScript("alert('该单据不能删除!')"));
        return;
      }
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);

      //与合计相关
      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      EngineDataSet dshxje = new EngineDataSet();
      setDataSetProperty(dshxje,HXJESQL);
      dshxje.open();
      dshxje.first();
      if(dshxje.getRowCount()<1)
        zzhxje="0";
      else
        zzhxje=dshxje.getValue("zhxje");

      EngineDataSet dswhxje = new EngineDataSet();
      setDataSetProperty(dswhxje,WHXJESQL);
      dswhxje.open();
      dswhxje.first();
      if(dswhxje.getRowCount()<1)
        zzwhxje="0";
      else
        zzwhxje=dswhxje.getValue("zwhxje");

      zzhxje = zzhxje.equals("")?"0":zzhxje;
      zzje = zzje.equals("")?"0":zzje;
      zzwhxje = zzwhxje.equals("")?"0":zzwhxje;

      zzhxje = formatNumber(zzhxje, priceFormat);
      zzwhxje = formatNumber(zzwhxje, priceFormat);
      zzje = formatNumber(zzje, priceFormat);

      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }

  /**
   * 排序操作
   */
  final class Orderby implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 排序触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      orderBy = data.getParameter("sortColumnStr", "");
      selectedOrders.clear();
      if(orderBy.length() > 0)
      {
        String[] sorts = StringUtils.parseString(orderBy, ",");
        for(int i=0; i<sorts.length; i++){
          selectedOrders.add(sorts[i]);
        }
      }
      String SQL = SEARCHSQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{
                       SQL.length()==0 ? " and 1=1 " : SQL+" ",
                       fgsid+" and ",
                       "cw_nbjs."+user.getHandleDeptValue("deptid"),
                       orderBy.length() > 0 ? "ORDER BY "+orderBy+" desc " : " ORDER BY cw_nbjs.nbjsdh desc"
                       });
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.readyRefresh();
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

      SEARCHSQL= SQL;

      String searchsql =  combineSQL(MASTER_SEARCH_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL,""});//组装SQL语句
      if(!dsMasterTable.getQueryString().equals(searchsql))
      {
        dsMasterTable.setQueryString(searchsql);
        dsMasterTable.setRowMax(null);
      }

       JESQL =  combineSQL(JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
       HXJESQL = combineSQL(HXJE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
       WHXJESQL = combineSQL(WHXJE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      EngineDataSet dshxje = new EngineDataSet();
      setDataSetProperty(dshxje,HXJESQL);
      dshxje.open();
      dshxje.first();
      if(dshxje.getRowCount()<1)
        zzhxje="0";
      else
        zzhxje=dshxje.getValue("zhxje");

      EngineDataSet dswhxje = new EngineDataSet();
      setDataSetProperty(dswhxje,WHXJESQL);
      dswhxje.open();
      dswhxje.first();
      if(dswhxje.getRowCount()<1)
        zzwhxje="0";
      else
        zzwhxje=dswhxje.getValue("zwhxje");


      zzhxje = zzhxje.equals("")?"0":zzhxje;
      zzje = zzje.equals("")?"0":zzje;
      zzwhxje = zzwhxje.equals("")?"0":zzwhxje;

      zzhxje = formatNumber(zzhxje, priceFormat);
      zzwhxje = formatNumber(zzwhxje, priceFormat);
      zzje = formatNumber(zzje, priceFormat);
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
        new QueryColumn(master.getColumn("nbjsdh"), null, null, null, null, "="),//提单编号
        new QueryColumn(master.getColumn("rq"), null, null, null, "a", ">="),//提单日期
        new QueryColumn(master.getColumn("rq"), null, null, null, "b", "<="),//提单日期
        new QueryColumn(master.getColumn("je"), null, null, null, "a", ">="),//
        new QueryColumn(master.getColumn("je"), null, null, null, "b", "<="),//
        new QueryColumn(master.getColumn("nbjsdh"), null, null, null, "a", ">="),//
        new QueryColumn(master.getColumn("nbjsdh"), null, null, null, "b", "<="),//
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//收款部门id
        new QueryColumn(master.getColumn("kc__deptid"), null, null, null, null, "="),//付款部门id
        new QueryColumn(master.getColumn("czy"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("bz"), null, null, null, null, "like"),
        //new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
  /**
   *  自动核销
   */
  class Detail_Auto_Cancer implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());

      String personid = "";
      String nbjsid = "";
      double sjje=0;
      double dje=0;//本单据的收款金额

      dsMasterTable.goToRow(rownum);
      personid = dsMasterTable.getValue("personid");
      nbjsid = dsMasterTable.getValue("nbjsid");
      String je = dsMasterTable.getValue("je");
      if(personid.equals(""))
        return;

      EngineDataSet dscollect = new EngineDataSet();
      setDataSetProperty(dscollect,combineSQL(COLLECT_SQL,"?",new String[]{fgsid,personid}));
      dscollect.open();
      dscollect.first();
      if(dscollect.getRowCount()==1)
        totalWhxje = dscollect.getValue("totalWhxje");
      else
        totalWhxje="";


      sjje = Double.parseDouble(totalWhxje.equals("")?"0":totalWhxje);//总未核消金额
      dje = Double.parseDouble(je.equals("")?"0":je);//本单据实收金额
      if(dje<=sjje)
        sjje = dje;//最多只能核消本单据收款金额
      if(sjje<=zjsje)
        return;

      String SQL = combineSQL(AUTO_CANCER_SQL,"?",new String[]{fgsid,personid});
      EngineDataSet dstmp = new EngineDataSet();
      setDataSetProperty(dstmp,SQL);

      dstmp.open();
      dstmp.first();
      for(int i=0;i<dstmp.getRowCount();i++)
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("nbjshxid", "-1");
        dsDetailTable.setValue("nbjsid", nbjsid);

        dsDetailTable.setValue("cpid", dstmp.getValue("cpid"));

        dsDetailTable.setValue("nbjsmxid", dstmp.getValue("nbjsmxid"));

        double jsje = dstmp.getValue("wjsje").length() > 0 ? Double.parseDouble(dstmp.getValue("wjsje")) : 0;//结算金额

       dsDetailTable.setValue("jsje", dstmp.getValue("wjsje"));

        zjsje +=jsje;
        if(zjsje>=sjje)
        {
          dsDetailTable.setValue("jsje", String.valueOf(jsje-zjsje+sjje));

          dsDetailTable.post();
          break;
        }
        dsDetailTable.post();
        dstmp.next();
      }
      initRowInfo(false,false,true);
    }
  }

  /**
   *
   * 引入内部流转单货物
   * */
  class Detail_SALE_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String nbjsmxids = m_RowInfo.get("nbjsmxids");
      String personid = m_RowInfo.get("personid");
      if(nbjsmxids.length() == 0)
        return;

      EngineDataSet dscollect = new EngineDataSet();
      setDataSetProperty(dscollect,combineSQL(COLLECT_SQL,"?",new String[]{fgsid,personid}));
      dscollect.open();
      dscollect.first();
      if(dscollect.getRowCount()==1)
        totalWhxje = dscollect.getValue("totalWhxje");
      else
        totalWhxje="";

      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "nbjsmxid");
      String[] nbjsmxid = parseString(nbjsmxids,",");//解析出合同货物ID数组
      engine.erp.finance.xixing.ImportCheckProduct ImportCheckProductBean =getsaleOrderBean(req);
      for(int i=0; i < nbjsmxid.length; i++)
      {
        if(nbjsmxid[i].equals("-1"))
          continue;
        if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
        String nbjsid = dsMasterTable.getValue("nbjsid");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, nbjsmxid[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap saleRow = ImportCheckProductBean.getLookupRow(nbjsmxid[i]);

          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("nbjshxid", "-1");
          dsDetailTable.setValue("nbjsid", nbjsid);
          dsDetailTable.setValue("nbjsmxid", nbjsmxid[i]);
          dsDetailTable.setValue("cpid", saleRow.get("cpid"));
          dsDetailTable.setValue("jsje", saleRow.get("wjsje"));//净金额与实收金额的差额

          double jsje = saleRow.get("wjsje").length() > 0 ? Double.parseDouble(saleRow.get("wjsje")) : 0;//结算金额

          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
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
   //删除临时数组的一列数
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
    }
  }

  /**
   *改变部门引发的操作
   * */
  class Dept_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());//保存输入的明细信息
    }
  }
  /**
 *改变付款部门引发的操作
 * */
  class KCDept_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());//保存输入的明细信息
      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      String kc__deptid=rowInfo.get("kc__deptid");
      String deptid=rowInfo.get("deptid");
      if(deptid.equals(""))
      {
        m_RowInfo.put("kc__deptid","-1");
        data.setMessage(showJavaScript("alert('请先选择收款部门')"));
        return;
      }
      else
      {
        m_RowInfo.put("rq",req.getParameter("rq"));
        m_RowInfo.put("nbjsdh",req.getParameter("nbjsdh"));
        m_RowInfo.put("deptid",deptid);
        m_RowInfo.put("kc__deptid",kc__deptid);
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

      String olddwtxId=m_RowInfo.get("dwtxid");
      putDetailInfo(data.getRequest());//保存输入的明细信息
      String dwtxId=m_RowInfo.get("dwtxid");
      RowMap corRow = corpBean.getLookupRow(dwtxId);
        if(olddwtxId.equals(dwtxId))
        {
          return;
        }
        else
        {
          m_RowInfo.put("dwtxId",dwtxId);
          m_RowInfo.put("rq",req.getParameter("rq"));
          m_RowInfo.put("nbjsdh",req.getParameter("nbjsdh"));
          m_RowInfo.put("deptid",corRow.get("deptid"));
          m_RowInfo.put("personid",corRow.get("personid"));
        }
        data.setMessage(showJavaScript("document.form1.jsdh.focus();"));
    }
  }
  /**
   * 得到用于查找合同编号的bean
   * @param req WEB的请求
   * @return 返回用于查找合同编号的bean
   */
  public engine.erp.finance.xixing.ImportCheckProduct getsaleOrderBean(HttpServletRequest req)
  {
    if(ImportCheckProductBean == null)
      ImportCheckProductBean = engine.erp.finance.xixing.ImportCheckProduct.getInstance(req);
    return ImportCheckProductBean;
  }
}
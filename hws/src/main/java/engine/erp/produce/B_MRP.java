package engine.erp.produce;

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
import engine.erp.baseinfo.BasePublicClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 生产--物料需求计划列表</p>
 * <p>Description: 生产--物料需求计划列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_MRP extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";
  public  static final String ONCHANGE = "10031";
  public  static final String CANCEL_APPROVE = "11031";
  //public  static final String DETAIL_ADD_BLANK = "11131";
  public  static final String DETAIL_COPY_ADD = "10050";
  public  static final String SINGLE_PRODUCT_ADD = "10631";
  public  static final String PRODUCT_ONCHANGE = "10002";//在从表中输入产品编码触发事件
  public  static final String COMPLETE = "10003";//完成操作事件
  public  static final String SUMIT_PROPERTY = "10004";//选择规格属性操作事件
  public  static final String SUBPLAN_MRP = "2002";//在物料需求计划主表里面增加分切计划物料需求触发事件

  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_wlxqjh WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM sc_wlxqjh WHERE ? AND fgsid=? ? ORDER BY wlxqh DESC";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_wlxqjhmx WHERE 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sc_wlxqjhmx WHERE wlxqjhid='?' ORDER BY cc,cpid";//
  private static final String UPDATE_SQL = "UPDATE sc_jh SET zt='2' WHERE scjhid= ";//下达物料需求后update生产计划的状态
  private static final String UPDATE_PLAN_SQL = "UPDATE sc_jh SET zt='1' WHERE scjhid= ";//删除物料需求后update生产计划的状态变为审核状态
  private static final String PLAN_DETAIL_SQL = "SELECT * FROM sc_jhmx WHERE scjhid= ";//通过生产计划ID得到计划明细并得到明细是否生成实际BOM
  private static final String BUILD_FACT_MRP = "{CALL pck_produce.buildFactBOMdata(@,'@',@,'@',@,'@',@)}";//生成实际BOM调用存储过程
  private static final String UPDATE_PLANDETAIL_SQL = "UPDATE　sc_jhmx SET sfsc='1' WHERE scjhmxid= ";//UPDATE生产计划明细的sfsc实际BOM子段
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM sc_wlxqjh WHERE wlxqjhid='?'";
  //
  private static final String GET_MRP_DATA = "{CALL pck_produce.getMRPdata(?,@)}";
  //抽取客户实际BOM表数据SQL语句
  private static final String BOM_STRUT_SQL = "SELECT * FROM sc_sjbom WHERE 1<>1";
  private static final String BOM_SQL    = "SELECT * FROM sc_sjbom WHERE scjhmxid=? ORDER BY cc,cpid";
  private EngineDataSet dsFactBom = new EngineDataSet();//客户实际BOM数据集

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_wlxqjh");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_wlxqjhmx");
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态

  private boolean isSubMaterail = false; //是否是增加分切计划物料
  private String jhlx = null;//物料是否是通过分切计划生成的，是jhlx为1
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private LookUp productBean = null; //产品的bean的引用, 用于提取产品
  private LookUp planUseAbleBean = null; //产品的bean的引用, 用于提取产品计划可供量
  private LookUp propertyBean = null; //产品信息的bean的引用, 用于提取产品信息

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String wlxqjhid = null;
  private User user = null;
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  public String SC_PLAN_ADD_STYLE = null;//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
  /**
   * 物料需求计划列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回物料需求计划列表的实例
   */
  public static B_MRP getInstance(HttpServletRequest request)
  {
    B_MRP mrpBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "mrpBean";
      mrpBean = (B_MRP)session.getAttribute(beanName);
      if(mrpBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        mrpBean = new B_MRP();
        mrpBean.qtyFormat = loginBean.getQtyFormat();

        mrpBean.fgsid = loginBean.getFirstDeptID();
        mrpBean.loginId = loginBean.getUserID();
        mrpBean.loginName = loginBean.getUserName();
        mrpBean.loginDept = loginBean.getDeptID();
        mrpBean.user = loginBean.getUser();
        mrpBean.SC_PLAN_ADD_STYLE = loginBean.getSystemParam("SC_PLAN_ADD_STYLE");//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
        mrpBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        mrpBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        //设置格式化的字段
        mrpBean.dsDetailTable.setColumnFormat("xql", mrpBean.qtyFormat);
        mrpBean.dsMasterTable.setColumnFormat("xgl", mrpBean.qtyFormat);
        session.setAttribute(beanName, mrpBean);
      }
    }
    return mrpBean;
  }

  /**
   * 构造函数
   */
  private B_MRP()
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
    setDataSetProperty(dsFactBom, BOM_STRUT_SQL);

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"wlxqh"}, new String[]{"SELECT pck_base.billNextCode('sc_wlxqjh','wlxqh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"wlxqh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"wlxqjhmxid"}, new String[]{"s_sc_wlxqjhmx"}));
    dsDetailTable.setSort(new SortDescriptor("", new String[]{"cc","cpid"}, new boolean[]{false,false}, null, 0));
    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(SUBPLAN_MRP), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());//取消审批
    addObactioner(String.valueOf(DETAIL_COPY_ADD), new DetailCopyAdd());//从表复制添加
    addObactioner(String.valueOf(SINGLE_PRODUCT_ADD), new Single_Product_Add());//从表选择单个产品增加操作
    addObactioner(String.valueOf(PRODUCT_ONCHANGE), new Product_Onchange());
    addObactioner(String.valueOf(COMPLETE), new Complete());
    addObactioner(String.valueOf(SUMIT_PROPERTY), new Sumit_Property());
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
        m_RowInfo.put("zdrq", today);//制单日期
        m_RowInfo.put("zdr", loginName);//操作员
        m_RowInfo.put("rq", today);//计划日期
        m_RowInfo.put("zdrid", loginId);
        m_RowInfo.put("deptid", loginDept);
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
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品id
      detailRow.put("xql", rowInfo.get("xql_"+i));//需求量
      detailRow.put("xgl", rowInfo.get("xgl_"+i));//需购量
      detailRow.put("xqrq", rowInfo.get("xqrq_"+i));//需求日期
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
      detailRow.put("chxz", rowInfo.get("chxz_"+i));//存货性质
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性
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

  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : wlxqjhid;
    SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});

    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    else
      dsDetailTable.refresh();
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
   * 得到物料是通过什么计划生成的。1.分切计划0通用计划
   */
  public final String getPlanType(){
    return jhlx;
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
      row.put("rq$a", startDay);
      row.put("rq$b", today);
      isMasterAdd = true;
      //
      String SQL = " AND zt<>8";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
    }
  }
  /**
  * 选择规格属性的触发类
  */
 class Sumit_Property implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     int num = Integer.parseInt(data.getParameter("rownum"));
     RowMap detailrow = (RowMap)d_RowInfos.get(num);
     String olddmsxid = detailrow.get("dmsxid");
     putDetailInfo(data.getRequest());
     RowMap  rowinfo = (RowMap)d_RowInfos.get(num);
     String dmsxid = rowinfo.get("dmsxid");
     if(olddmsxid.equals(dmsxid))
       return;
     String jlxql = rowinfo.get("jlxql");
     String xql = rowinfo.get("xql");
     String jhkgl = rowinfo.get("jhkgl");
     if(jlxql.equals("") && xql.equals(""))
       return;
     String cpid = rowinfo.get("cpid");
     RowMap productRow = getProductBean(data.getRequest()).getLookupRow(cpid);
     String scdwgs = productRow.get("scdwgs");
     String sxz = getPropertyBean(data.getRequest()).getLookupName(dmsxid);
     String width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
     double d_scdwgs = scdwgs.length()>0 ? Double.parseDouble(scdwgs) : 0;
     double d_width = width.equals("0") ? 1 : Double.parseDouble(width);
     double d_jlxql = jlxql.length()>0 ? Double.parseDouble(jlxql) : 0;
     double d_xql = xql.length()>0 ? Double.parseDouble(xql) : 0;
     double d_jhkgl = jhkgl.length()>0 ? Double.parseDouble(jhkgl) : 0;
     if(jlxql.length()>0)
     {
       if(d_jhkgl<=0)
         rowinfo.put("xgl", jlxql);
       else if(d_jhkgl > d_jhkgl)
         rowinfo.put("xgl", "0");
       else if(d_jhkgl <= d_jlxql)
         rowinfo.put("xgl", String.valueOf(d_jlxql-d_jhkgl));
       rowinfo.put("xql", d_width==0 ? jlxql : formatNumber( String.valueOf(d_scdwgs==0 ? d_jlxql : d_jlxql*d_scdwgs/d_width), qtyFormat));
     }
     else if(xql.length()>0)
     {
       String temp = d_width==0 ? xql : formatNumber(String.valueOf(d_scdwgs==0 ? d_xql : d_xql*d_width/d_scdwgs), qtyFormat);
       rowinfo.put("jlxql", temp);
       double d_temp = temp.length()>0 ? Double.parseDouble(temp) : 0;
       if(d_jhkgl >= d_temp)
         rowinfo.put("xgl", "0");
       else
         rowinfo.put("xgl", String.valueOf(d_temp-d_jhkgl));
     }
   }
  }
  /**
   * 强制完成操作触发的类
   * 手工完成操作
   */
  class Complete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsMasterTable.goToRow(row);
      dsMasterTable.setValue("zt", "8");
      dsMasterTable.post();
      dsMasterTable.saveChanges();
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
      wlxqjhid = dsMasterTable.getValue("wlxqjhid");
      //打开从表
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
      isApprove = false;
      isSubMaterail = String.valueOf(SUBPLAN_MRP).equals(action);
      isMasterAdd = !String.valueOf(EDIT).equals(action);
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        jhlx = dsMasterTable.getValue("jhlx");
        wlxqjhid = dsMasterTable.getValue("wlxqjhid");
      }
      if(isMasterAdd){
        if(isSubMaterail)
          jhlx="1";
        else
          jhlx="0";
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
      isApprove = true;

      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //得到request的参数,值若为null, 则用""代替
      String id = data.getParameter("id", "");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();
      wlxqjhid = dsMasterTable.getValue("wlxqjhid");
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
      String content = dsMasterTable.getValue("wlxqh");
      String deptid = dsMasterTable.getValue("deptid");
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "mrp", content, deptid);
    }
  }
  /**
   * 取消审批的操作类
   */
  class Cancel_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable, dsMasterTable.getRow(), "mrp");
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
      String wlxqjhid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        wlxqjhid = dataSetProvider.getSequence("s_sc_wlxqjh");
        ds.setValue("wlxqjhid", wlxqjhid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
      }
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("wlxqjhid", wlxqjhid);

        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("xql", detailrow.get("xql"));//生产单位需求量
        detail.setValue("xgl", detailrow.get("xgl"));//需购量
        detail.setValue("jlxql", detailrow.get("jlxql"));//计量单位需求量
        detail.setValue("xqrq", detailrow.get("xqrq"));
        detail.setValue("bz", detailrow.get("bz"));//
        detail.setValue("cc", detailrow.get("cc"));//
        detail.setValue("chxz", detailrow.get("chxz"));//超产率
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("gylxid", detailrow.get("gylxid"));
        detail.setValue("scjhmxid", detailrow.get("scjhmxid"));//生差计划明细ID
        detail.setValue("htid", detailrow.get("htid"));//合同ID
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("xql"));//需求量
        detail.next();
      }

      //保存主表数据
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("scjhid", rowInfo.get("scjhid"));//生产计划id
      ds.setValue("rq", rowInfo.get("rq"));//日期
      ds.setValue("ztms", rowInfo.get("ztms"));//状态描述
      ds.setValue("zsl", totalNum.toString());//总需求数量
      ds.setValue("jhlx",jhlx);//该物料计划是通过什么计划生成的，1.分切计划0通用计划
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      String scjhid = rowInfo.get("scjhid");
      ds.post();
      ds.setAfterResolvedSQL(new String[]{UPDATE_SQL + scjhid});
      ds.saveDataSets(new EngineDataSet[]{ds, detail});

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
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
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      ArrayList list = new ArrayList(d_RowInfos.size());
      String scjhmxid=null, cpid=null, dmsxid=null, gylxid=null, unit=null, htid=null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        scjhmxid= detailrow.get("scjhmxid");
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");
        gylxid = detailrow.get("gylxid");
        htid = detailrow.get("htid");
        StringBuffer buf = new StringBuffer().append(htid).append(",").append(scjhmxid).append(",").append(cpid).append(",").append(dmsxid).append(",").append(gylxid);
        unit = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(unit))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(unit);
        String xql = detailrow.get("xql");
        if((temp = checkNumber(xql, "第"+row+"行需求量")) != null)
          return temp;
        String xgl = detailrow.get("xgl");
        if((temp = checkNumber(xgl, "第"+row+"行需购量")) != null)
          return temp;
        temp = detailrow.get("ksrq");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('第"+row+"行非法开始日期！');");
        temp = detailrow.get("wcrq");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('第"+row+"行非法完成日期！');");
      }
      return null;
    }

    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo() throws Exception
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
      temp = rowInfo.get("scjhid");
      if(temp.equals(""))
         return showJavaScript("alert('请选择生产计划！');");
      if(isMasterAdd){
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_wlxqjh WHERE scjhid="+temp);
      if(!count.equals("0"))
        return showJavaScript("alert('该生产计划已下达物料需求！');");
      }
      return null;
    }
  }
  /**
    * 从表增加操作
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
       EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cpid");
       if(!isMasterAdd)
         dsMasterTable.goToInternalRow(masterRow);
       String wlxqjhid = dsMasterTable.getValue("wlxqjhid");
       dsDetailTable.goToRow(row);
       RowMap detailrow = null;
       detailrow = (RowMap)d_RowInfos.get(row);
       locateGoodsRow.setValue(0, cpid);
       if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
       {
         detailrow.put("wlxqjhmxid", "-1");
         detailrow.put("cpid", cpid);
         RowMap productRow = getProductBean(req).getLookupRow(cpid);
         long  ztqq = productRow.get("ztqq").length()>0 ? Long.parseLong(productRow.get("ztqq")) : 0;//总提前期
         Date startdate = new Date();
         Date enddate = new Date(startdate.getTime() + ztqq*60*60*24*1000);
         String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
         detailrow.put("xqrq", endDate);
         detailrow.put("wlxqjhid", isMasterAdd ? "-1" : wlxqjhid);
       }
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
      String scjhid = ds.getValue("scjhid");
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.setAfterResolvedSQL(new String[]{UPDATE_PLAN_SQL + scjhid});
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      //
      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), fgsid, SQL});
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
      EngineDataSet detail = dsMasterTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("wlxqh"), null, null, null),
        new QueryColumn(master.getColumn("rq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("rq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        new QueryColumn(master.getColumn("wlxqjhid"), "sc_wlxqjhmx", "wlxqjhid", "cpid", null, "="),//从表品名
        new QueryColumn(master.getColumn("wlxqjhid"), "VW_MRP_QUERY", "wlxqjhid", "cpbm", "cpbm", "like"),//从表产品编码
        new QueryColumn(master.getColumn("wlxqjhid"), "VW_MRP_QUERY", "wlxqjhid", "product", "product", "like"),//从表品名
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
  /**
   *  从表输入产品编码触发操作
  */
  class Product_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsDetailTable.goToRow(row);
      RowMap detail=(RowMap)d_RowInfos.get(row);
      String cpid = detail.get("cpid");
      RowMap productRow = getProductBean(req).getLookupRow(cpid);
      long  ztqq = productRow.get("ztqq").length()>0 ? Long.parseLong(productRow.get("ztqq")) : 0;//总提前期
      Date startdate = new Date();
      Date enddate = new Date(startdate.getTime() + ztqq*60*60*24*1000);
      String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
      detail.put("xqrq", endDate);
    }
  }
  /**
   *删除生产计划触发的事件
   */
  class Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(req);
      RowMap rowinfo = getMasterRowinfo();
      rowinfo.put("scjhid","");
      dsDetailTable.deleteAllRows();
      d_RowInfos.clear();
    }
  }

  /**
   *  从表复制增加
   */
  class DetailCopyAdd implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String wlxqjhid = dsMasterTable.getValue("wlxqjhid");
      String rownum = data.getParameter("rownum");
      detail.goToRow(Integer.parseInt(rownum));
      DataRow row = new DataRow(detail);
      detail.copyTo(row);
      detail.addRow(row);
      //detail.insertRow(false);
      detail.setValue("wlxqjhid", isMasterAdd ? "-1" : wlxqjhid);
      detail.setValue("iscopy", "1");
      detail.post();
      d_RowInfos.add(new RowMap(detail));
    }
  }

  /**
   *  从表增加操作(增加一个空白行)
   *
  class Detail_Add_Blank implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String wlxqjhid = dsMasterTable.getValue("wlxqjhid");
      detail.insertRow(false);
      detail.setValue("wlxqjhid", isMasterAdd ? "-1" : wlxqjhid);
      detail.setValue("iscopy", "1");
      detail.post();
      d_RowInfos.add(new RowMap());
    }
  }
  /**
   * 从表增加操作既通过生产计划id得到明细产品下达物料需求
   */
  class Detail_Add implements Obactioner
  {
    private EngineDataSet mrpdata = null;
    private EngineDataSet dsPlanDetail = null;
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();
      String scjhid = rowInfo.get("scjhid");
      /**
         * 通过生产计划ID得到生产计划明细数据集
         * 如果计划明细中有没有生成实际BOM的纪录，即!sfsc=1;则调用存储过程生成实际BOM
         */
        if(dsPlanDetail == null)
        {
          dsPlanDetail = new EngineDataSet();
          setDataSetProperty(dsPlanDetail, null);
        }
        dsPlanDetail.setQueryString(PLAN_DETAIL_SQL+scjhid);
        if(!dsPlanDetail.isOpen())
          dsPlanDetail.openDataSet();
        else
          dsPlanDetail.refresh();
        int count = dsPlanDetail.getRowCount();
        dsPlanDetail.first();
        for(int k=0; k<count; k++)
        {
          dsPlanDetail.goToRow(k);
          String scjhmxid = dsPlanDetail.getValue("scjhmxid");
          String hthwid = dsPlanDetail.getValue("hthwid");//销售合同货物ID
          String ksrq = dsPlanDetail.getValue("ksrq");
          String cpid = dsPlanDetail.getValue("cpid");
          String p_dmsxid = dsPlanDetail.getValue("dmsxid");
          String xql = dsPlanDetail.getValue("sl");//计划明细数量
          String sfsc = dsPlanDetail.getValue("sfsc");
          if(!sfsc.equals("1"))
          {
            String sql = combineSQL(BUILD_FACT_MRP, "@", new String[]{scjhmxid,hthwid,cpid,p_dmsxid,xql,ksrq,String.valueOf(count)});
            dsFactBom.updateQuery(new String[]{sql});
            dsPlanDetail.updateQuery(new String[]{UPDATE_PLANDETAIL_SQL + scjhmxid});
            String SQL = combineSQL(BOM_SQL, "?", new String[]{scjhmxid});
            dsFactBom.setQueryString(SQL);
            if(dsFactBom.isOpen())
              dsFactBom.refresh();
            else
              dsFactBom.open();
            dsMasterTable.first();
            String b_cpid=null,b_xql=null, dmsxid=null, sxz=null, width=null,scdwgs=null, scsl=null, scxql=null;
            double d_xql=0, d_width=0, d_scdwgs=0, d_scsl=0;
            RowMap prodRow =null;
            dsFactBom.first();
            for(int i=0; i< dsFactBom.getRowCount(); i++)
            {
              scxql = dsFactBom.getValue("scxql");//存储过程返回的生产需求量
              //如果返回不为空就不用继续计算了
              if(!scxql.equals("")){
                dsFactBom.next();
                continue;
              }
              b_cpid = dsFactBom.getValue("cpid");
              b_xql = dsFactBom.getValue("xql");
              dmsxid = dsFactBom.getValue("dmsxid");
              prodRow = getPropertyBean(data.getRequest()).getLookupRow(b_cpid);
              scdwgs = prodRow.get("scdwgs");
              sxz = getPropertyBean(data.getRequest()).getLookupName(dmsxid);
              width = BasePublicClass.parseEspecialString(sxz,SYS_PRODUCT_SPEC_PROP, "()");
              d_xql = b_xql.length()>0 ? Double.parseDouble(b_xql) : 0;
              d_width = width.equals("0") ? 1 : Double.parseDouble(width);
              d_scdwgs = scdwgs.length()>0 ? Double.parseDouble(scdwgs) : 1;
              if(d_width==1)
                d_scsl= d_xql;
              else
                d_scsl=d_xql*d_scdwgs/d_width;
              scsl = formatNumber(String.valueOf(d_scsl), qtyFormat);
              dsFactBom.setValue("scxql", scsl);
              dsFactBom.post();
              dsFactBom.next();
            }
            dsFactBom.saveChanges();
          }
          dsPlanDetail.next();
        }
        /**
         * 计划明细都生成实际BOM后下达总的物料需求
         * 调用存储过程返回数据集mrpData
         */
        String sql = combineSQL(GET_MRP_DATA, "@", new String[]{scjhid});
        if(mrpdata == null)
        {
          mrpdata = new EngineDataSet();
          setDataSetProperty(mrpdata, null);
        }
        mrpdata.setQueryString(sql);
        if(!mrpdata.isOpen())
          mrpdata.openDataSet();
        else
          mrpdata.refresh();

        dsDetailTable.deleteAllRows();
        d_RowInfos.clear();
        EngineRow row = new EngineRow(dsDetailTable,
                                      new String[]{"scjhmxID","htID","cpID","gylxID","xql","jlxql","xqrq","chxz","cc"});
        mrpdata.first();
        for(int i=0; i<mrpdata.getRowCount(); i++){
          mrpdata.copyTo(row);
          dsDetailTable.addRow(row);
          mrpdata.next();
        }
        //---
        //EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cpid");
        int rowCount=dsDetailTable.getRowCount();
        Hashtable table = new Hashtable(rowCount+1,1);//存放叠加xql即以生产单位换算的需求量
        Hashtable jltable = new Hashtable(rowCount+1);//存放计量单位换算的数量jlxql
        LookUp look = getPlanUseAbleBean(req);
        look.regData(dsDetailTable,new String[]{"cpid","dmsxid"});
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String wlxqjhid = dsMasterTable.getValue("wlxqjhid");
        dsDetailTable.first();
        for(int j=0; j<rowCount; j++){
          dsDetailTable.setValue("wlxqjhid", isMasterAdd ? "" : wlxqjhid);
          String cpid = dsDetailTable.getValue("cpid");
          String dmsxid = dsDetailTable.getValue("dmsxid");
          //double xql = Double.parseDouble(dsDetailTable.getValue("xql"));
          String kgl = getPlanUseAbleBean(req).getLookupName(new String[]{cpid, dmsxid});
          String xql = dsDetailTable.getValue("xql");
          String jlxql = dsDetailTable.getValue("jlxql");
          /**
          String d_cpid = dsDetailTable.getValue("cpid");
          String d_dmsxid = dsDetailTable.getValue("dmsxid");
          String d_sxz = getPropertyBean(req).getLookupName(d_dmsxid);
          String d_width = BasePublicClass.parseEspecialString(d_sxz, "宽度","()");
          double double_scdwgs = scdwgs.length()>0 ? Double.parseDouble(scdwgs) : 1;
          double double_width = d_width.length()>0 ? Double.parseDouble(d_width) : 1;
           */
          BigDecimal curValue = isDouble(jlxql) ? new BigDecimal(jlxql) : new BigDecimal(0);
          BigDecimal total = (BigDecimal)table.get(cpid);
          if(total == null)
            total = curValue;
          else
            total = total.add(curValue);
          table.put("cpid",total);
          double jhkgl = kgl.length()>0 ? Double.parseDouble(kgl) : 0;
          if(jhkgl<0)
            jhkgl=0;
          BigDecimal jhkglVal = new BigDecimal(jhkgl);
          BigDecimal testsl = jhkglVal.subtract(total).doubleValue()>0 ? new BigDecimal(0) : total.subtract(jhkglVal);
          dsDetailTable.setValue("xgl", formatNumber(testsl.toString(), qtyFormat));

          dsDetailTable.next();
        }
        initRowInfo(false, false, false);
    }
  }
  /**
   * 得到总提前期信息的bean
   * @param req WEB的请求
   * @return 返回外币信息bean
   */
  public LookUp getProductBean(HttpServletRequest req)
  {
    if(productBean == null)
      productBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PRODUCT_STOCK);
    return productBean;
  }
  /**
  * 得到计划可供量信息的bean
  * @param req WEB的请求
  * @return 返回外币信息bean
  */
 public LookUp getPlanUseAbleBean(HttpServletRequest req)
 {
   if(planUseAbleBean == null)
     planUseAbleBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PALN_USABLE_NUMBER);
   return planUseAbleBean;
  }
  /**
  * 得到规格属性的bean
  * @param req WEB的请求
  * @return 返回规格属性的bean
  */
 public LookUp getPropertyBean(HttpServletRequest req)
 {
   if(propertyBean == null)
     propertyBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_SPEC_PROPERTY);
     return propertyBean;
 }
}




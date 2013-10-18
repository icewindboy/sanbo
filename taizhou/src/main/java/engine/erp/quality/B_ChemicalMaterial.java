package engine.erp.quality;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.html.*;
import engine.common.*;
import engine.project.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.*;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import engine.html.HtmlTableProducer;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 质量管理--化工原料检验报告单</p>
 * <p>Description: 质量管理--化工原料检验报告单</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */
public final class B_ChemicalMaterial extends BaseAction implements Operate
{
  /**
   * 提取所有信息的SQL语句
   */
  private static final String Master_Quality_SQL = "SELECT * FROM ZL_CHEMICAL where ? ?";
  private static final String Master_STRUT_SQL = "SELECT * FROM ZL_CHEMICAL where 1<>1";
 // private static final String MASTER_SQL="SELECT * FROM ZL_CHEMICAL where filialeID='?' order by chemicalcheckNo desc";
  private static final String Detail_STRUT_SQL="SELECT * FROM ZL_CHEMICALDETAIL where 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM ZL_CHEMICALDETAIL WHERE chemicalcheckID='?' ";
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM ZL_CHEMICAL WHERE chemicalcheckID='?'";
  /**
   * 建立列表信息的数据集
   */
  private EngineDataSet dsMasterTable = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable=new EngineDataSet();//从表

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "ZL_CHEMICAL");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "ZL_CHEMICALDETAIL");
  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;
  /**
   * 是否在添加状态
   */
  public  boolean isMasterAdd = true;
  public  boolean isDetailAdd = false; // 从表是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态

  private RowMap     m_RowInfo = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();

  public  String loginId = "";   //登录员工的ID-->制单人ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名-->制单人
  public  String loginDept = ""; //登录员工的部门
  private String filialeid = null;     //分公司ID
  private String sumFormat = null;
  private User user = null;
  private String chemicalcheckID=null;
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交

  public  static final String CANCLE_APPROVE = "10081";//取消审批
  public  static final String DEPTCHANGE = "11591";//改变车间触发事件
   /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_ChemicalMaterial getInstance(HttpServletRequest request)
  {
    B_ChemicalMaterial b_ChemicalMaterialBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "ChemicalMaterialBean";
      b_ChemicalMaterialBean =(B_ChemicalMaterial)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_ChemicalMaterialBean == null)
      {
        //引用LoginBean
        b_ChemicalMaterialBean = new B_ChemicalMaterial();
        LoginBean loginBean = LoginBean.getInstance(request);
        b_ChemicalMaterialBean.loginId=loginBean.getUserID();
        b_ChemicalMaterialBean.loginName=loginBean.getUserName();
        b_ChemicalMaterialBean.filialeid=loginBean.getFirstDeptID();
        b_ChemicalMaterialBean.loginDept = loginBean.getDeptID();
        b_ChemicalMaterialBean.user = loginBean.getUser();
        b_ChemicalMaterialBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        session.setAttribute(beanName, b_ChemicalMaterialBean);
      }
    }
    return b_ChemicalMaterialBean;
  }
  /**
   * 构造函数
   */
  public B_ChemicalMaterial()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  public  HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "zl_chemical", "zl_chemical");//查询得到数据库中配置的字段
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsMasterTable, Master_STRUT_SQL);
    setDataSetProperty(dsDetailTable, Detail_STRUT_SQL);
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"chemicalcheckID"}, new boolean[]{false}, null, 0));
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"chemicalcheckID"}, new String[]{"S_ZL_CHEMICAL"}));
    dsDetailTable.setSort(new SortDescriptor("", new String[]{"chemicaldetailID"}, new boolean[]{false}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"chemicaldetailID"}, new String[]{"S_ZL_CHEMICALDETAIL"}));
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"chemicalcheckno"}, new String[]{"SELECT pck_base.billNextCode('zl_chemical','chemicalcheckno','') from dual"}));
    //添加操作的触发对象
    Master_Add_Edit masteradd_edit = new Master_Add_Edit();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(ADD),  masteradd_edit);
    addObactioner(String.valueOf(EDIT), masteradd_edit);
    addObactioner(String.valueOf(POST), new Master_Post());
    addObactioner(String.valueOf(POST_CONTINUE),new Master_Post());
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCLE_APPROVE), new Cancle_Approve());
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
  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
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
  //----Implementation of the BaseAction abstract class
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }
  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster,boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    //是否是主表
    if(isMaster){
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();
      if(!isAdd)
        m_RowInfo.put(getMaterTable());
      else
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        m_RowInfo.put("createDate", today);//制单日期
        m_RowInfo.put("creator", loginName);//制单人
        m_RowInfo.put("creatorID", loginId);//制单人id
        m_RowInfo.put("filialeid",filialeid);//制单人分公司ID
        m_RowInfo.put("deptid", loginDept);
      }
    }
    else{
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
      detailRow.put("porduct_name", rowInfo.get("porduct_name_"+i));//产品名称
      detailRow.put("content_request", rowInfo.get("content_request_"+i));//固含量:技术要求
      detailRow.put("content_result", rowInfo.get("content_result_"+i));//固含量:实测结果
      detailRow.put("adhibit_request", formatNumber(rowInfo.get("adhibit_request_"+i), sumFormat));//粘度:技术要求
      detailRow.put("adhibit_result", formatNumber(rowInfo.get("adhibit_result_"+i), sumFormat));//粘度:实测结果
      detailRow.put("beginboil_request",rowInfo.get("beginboil_request_"+i));//沸点初始温度(℃):技术要求
      detailRow.put("beginboil_result", rowInfo.get("beginboil_result_"+i));//沸点初始温度(℃):实测结果
      detailRow.put("endboil_request", rowInfo.get("endboil_request_"+i));//沸点终点温度(℃):技术要求
      detailRow.put("endboil_result", rowInfo.get("endboil_result_"+i));//沸点终点温度(℃):实测结果

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
    if(!dsMasterTable.isOpen())
     dsMasterTable.open();
    return dsMasterTable;
  }
   /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    return dsDetailTable;
  }
    /*打开从表*/
  public final void openDetailTable(boolean isAdd)
  {
    String SQL = isAdd ? "-1" : chemicalcheckID;
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
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      String SQL=combineSQL(Master_Quality_SQL,"?",new String[]{user.getHandleDeptValue("deptid")});
      dsMasterTable.setQueryString(SQL);
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
    }
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
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isMasterAdd = action.equals(String.valueOf(ADD));
      if(!isMasterAdd)
      {
        isDetailAdd = false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        chemicalcheckID = dsMasterTable.getValue("chemicalcheckID");
      }
      else{//打开从表
        isDetailAdd = true;
      }
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      data.setMessage(showJavaScript("toDetail();"));
      //initRowInfo(boolean isMaster,boolean isAdd, boolean isInit)
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发保存操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {

      m_RowInfo.put(data.getRequest());
      putDetailInfo(data.getRequest());
      String deptid = m_RowInfo.get("deptid");                    //部门id
      String personid = m_RowInfo.get("personid");                //人员id
      String dwtxId=m_RowInfo.get("dwtxId");                      //往来单位ID
      //String chemicalcheckNo =m_RowInfo.get("chemicalcheckNo");   //单据号
      String get_date = m_RowInfo.get("get_date");                //到货日期
      String check_num = m_RowInfo.get("check_num");              //抽检数量
      String shareout_date = m_RowInfo.get("shareout_date");      //配料日期
      String humidity = m_RowInfo.get("humidity");                //湿度
      String temperature = m_RowInfo.get("temperature");          //温度
      String check_date = m_RowInfo.get("check_date");            //抽检日期
      //String state = m_RowInfo.get("state");                      //状态
      String approverID = m_RowInfo.get("approverID");            //审批人
      String state_desc = m_RowInfo.get("state_desc");            //状态描述
      String estimation = m_RowInfo.get("estimation");            //试用情况-->判断
      String createDate = m_RowInfo.get("createDate");            //制单日期
      String creatorID = m_RowInfo.get("creatorID");              //制单人ID
      String creator = m_RowInfo.get("creator");                  //制单人
      //String filialeid = m_RowInfo.get("filialeid");              //分公司ID
      if(dwtxId.equals("")){
        data.setMessage(showJavaScript("alert('请选择供应商！');"));
        return;
      }
      if(deptid.equals("")){
       data.setMessage(showJavaScript("alert('请选择检验部门！');"));
       return;
      }
      if(personid.equals("")){
       data.setMessage(showJavaScript("alert('请选择检验员！');"));
      return;
      }
      EngineDataSet ds = getMaterTable();
      if(!isMasterAdd)
       ds.goToInternalRow(masterRow);
       String chemicalcheckID = null;
      if(isMasterAdd){
        ds.insertRow(false);
        chemicalcheckID = dataSetProvider.getSequence("s_zl_chemical");
        ds.setValue("chemicalcheckID", chemicalcheckID);
      }
      ds.setValue("deptid", deptid);
      ds.setValue("personid",personid);
      ds.setValue("dwtxId", dwtxId);
      //ds.setValue("chemicalcheckNo", chemicalcheckNo);
      ds.setValue("get_date", get_date);
      ds.setValue("check_num", check_num);
      ds.setValue("shareout_date", shareout_date);
      ds.setValue("humidity", humidity);
      ds.setValue("temperature", temperature);
      ds.setValue("check_date", check_date);
      ds.setValue("state", "0");
      ds.setValue("approverID", approverID);
      ds.setValue("state_desc", state_desc);
      ds.setValue("estimation", estimation);
      ds.setValue("createDate", createDate);
      ds.setValue("creatorID", creatorID);
      ds.setValue("creator", creator);
      ds.setValue("filialeID", filialeid);
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        detail.setValue("chemicalcheckID", chemicalcheckID);
        detail.setValue("porduct_name", detailrow.get("porduct_name"));
        detail.setValue("content_request", detailrow.get("content_request"));
        detail.setValue("content_result", detailrow.get("content_result"));
        detail.setValue("adhibit_request", detailrow.get("adhibit_request"));
        detail.setValue("adhibit_result", detailrow.get("adhibit_result"));
        detail.setValue("beginboil_request", detailrow.get("beginboil_request"));
        detail.setValue("beginboil_result", detailrow.get("beginboil_result"));
        detail.setValue("endboil_request", detailrow.get("endboil_request"));
        detail.setValue("endboil_result", detailrow.get("endboil_result"));
        detail.post();
        detail.next();
      }
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);//重新初始化从表的各行信息
        detail.empty();
        initRowInfo(false, false, true);//重新初始化从表的各行信息
      }
      else
      data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   * 主表删除操作的触发类
   */
  class Master_Delete implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发删除操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(isMasterAdd){
        data.setMessage(showJavaScript("backList();"));
        return;
      }
      EngineDataSet ds = getMaterTable();
      ds.goToInternalRow(masterRow);
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String tempSQL=null;
      String SQL=combineSQL(Master_Quality_SQL,"?",new String[]{user.getHandleDeptValue("deptid")});
      table.getWhereInfo().setWhereValues(data.getRequest());
      tempSQL = table.getWhereInfo().getWhereQuery();
      if(tempSQL.length() > 0) SQL=SQL+" and "+tempSQL;
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
    }
 }
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
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String chemicalcheckID = dsMasterTable.getValue("chemicalcheckID");
      detail.insertRow(false);
      detail.setValue("chemicalcheckID", isMasterAdd ? "-1" : chemicalcheckID);
      detail.post();
      d_RowInfos.add(new RowMap());
    }
  }
  /*
  *从表删除操作
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
   * 审批操作的触发类
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      String id = data.getParameter("id");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
    }
     else
       dsMasterTable.open();
       isApprove = true;
       chemicalcheckID = dsMasterTable.getValue("chemicalcheckID");
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
      String content = dsMasterTable.getValue("chemicalcheckNo");
      String deptid = dsMasterTable.getValue("deptid");//下达车间
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "chemical_material", content, deptid);
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
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "chemical_material");
    }
  }
}

package engine.erp.equipment;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.RowMap;
import engine.dataset.SequenceDescriptor;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.html.*;
import engine.common.*;
import engine.project.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 设备管理--生产设备购置申请//设备配件购置申请</p>
 * <p>Description: 设备管理--生产设备购置申请//设备配件购置申请</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */
public final class B_EquipmentBuyApply extends BaseAction implements Operate
{
  /**
   * 提取所有信息的SQL语句
   */
  private static final String Master_STRUT_SQL = "SELECT * FROM sb_buyApply where 1<>1";
  private static final String Master_SQL = "SELECT * FROM sb_buyApply where ? and apply_type='?'";
  private static final String Detail_STRUT_SQL="SELECT * FROM sb_buyApplyDetail where 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sb_buyApplyDetail WHERE buy_applyID='?' ";
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM sb_buyApply WHERE buy_applyID='?'";
  private EngineDataSet dsMasterTable = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable = new EngineDataSet();//从表
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sb_buyApply");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sb_buyApplyDetail");
  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;
  public  boolean isMasterAdd = true;
  public  boolean isDetailAdd = false;          // 从表是否在添加状态
  public  boolean isApprove = false;            //是否在审批状态
  private RowMap     m_RowInfo  = new RowMap(); //主表添加行或修改行的引用
  private ArrayList  d_RowInfos = null;         //从表多行记录的引用
  private long  mastereditRow = -1;             //保存主表修改操作的行记录指针
  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;
  //---------------------
  public  String loginID = null;    //登录员工的ID
  //public String loginName = null;   //登录员工的姓名
  private User user = null;        //登陆用户（设置用户部门权限）
  public  String loginDept = null;   //登录员工的部门
  private String creatorID =null;  //操作员ID
  private String creator =null;    //操作员==登录员工的姓名
  private String filialeID = null;  //分公司ID
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public  static final String CANCLE_APPROVE = null;//取消审批
  private String buy_applyID = null;
  public static final String SHOW_DETAIL = "10001";
  public String apply_type =null;
  /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_EquipmentBuyApply getInstance(HttpServletRequest request)
  {
    B_EquipmentBuyApply b_EquipmentBuyApplyBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_EquipmentBuyApplyBean";
      b_EquipmentBuyApplyBean =(B_EquipmentBuyApply)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_EquipmentBuyApplyBean == null)
      {
        b_EquipmentBuyApplyBean = new B_EquipmentBuyApply();
        LoginBean loginBean = LoginBean.getInstance(request);
        b_EquipmentBuyApplyBean.filialeID = loginBean.getFirstDeptID();//分公司id
        b_EquipmentBuyApplyBean.creator=loginBean.getUserName();//制单人
        b_EquipmentBuyApplyBean.user=loginBean.getUser();//登陆用户
        b_EquipmentBuyApplyBean.loginID=loginBean.getUserID();//操作员ID--》登陆用户id
        b_EquipmentBuyApplyBean.loginDept = loginBean.getDeptID();//登陆用户部门
        b_EquipmentBuyApplyBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        session.setAttribute(beanName, b_EquipmentBuyApplyBean);
      }
    }
    return b_EquipmentBuyApplyBean;
  }
  /**
   * 构造函数
   */
  public B_EquipmentBuyApply()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  public  HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "sb_buyapply", "sb_buyapply");//查询得到数据库中配置的字段
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsMasterTable, Master_STRUT_SQL);
    setDataSetProperty(dsDetailTable, Detail_STRUT_SQL);

    dsMasterTable.setSort(new SortDescriptor("", new String[]{"apply_date"}, new boolean[]{true}, null, 0));
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"buy_applyID"}, new String[]{"s_sb_buyApply"}));

    dsDetailTable.setSort(new SortDescriptor("", new String[]{"equipment_code","cpbm"}, new boolean[]{false}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"buy_applydetailID"}, new String[]{"s_sb_buyApplyDetail"}));

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
    addObactioner(SHOW_DETAIL, new ShowDetail());
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
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isMasterAdd; }
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
  private final void initRowInfo(boolean isMaster,boolean isMasterAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    //是否是主表
    if(isMaster){
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();
      if(!isMasterAdd)
        m_RowInfo.put(getMasterTable());
      else
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        m_RowInfo.put("createDate", today);//制单日期
        m_RowInfo.put("creator", creator);//制单人
        m_RowInfo.put("creatorID", loginID);//制单人id
        m_RowInfo.put("filialeid",filialeID);//分公司ID
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
      detailRow.put("cpid", rowInfo.get("cpid_"+i));                                      //产品ID
      detailRow.put("cpbm",rowInfo.get("cpbm_"+i));                                       //产品编码
      detailRow.put("equipmentID", rowInfo.get("equipmentID_"+i));                        //设备ID
      detailRow.put("equipment_code", rowInfo.get("equipment_code_"+i));                   //设备编码
      detailRow.put("equipment_name", rowInfo.get("equipment_name_"+i));                    //设备名称
      detailRow.put("standard_gg", rowInfo.get("standard_gg_"+i));                          //规格型号
      detailRow.put("manufacturer", rowInfo.get("manufacturer_"+i));                        //制造厂
      detailRow.put("price", rowInfo.get("price_"+i));                                      //价格性能比较
      detailRow.put("need_getdate", rowInfo.get("need_getdate_"+i));                        //要求到货日期(配件购置申请)
      detailRow.put("apply_num", rowInfo.get("apply_num_"+i));                              //申购数量

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
  public final EngineDataSet getMasterTable()
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
  public final void openDetailTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : buy_applyID;
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
     * 添加或修改的触发操作
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
      String SQL=combineSQL(Master_SQL,"?",new String[]{user.getHandleDeptValue("deptid"),apply_type});
      dsMasterTable.setQueryString(SQL);
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginID);
      detailProducer.init(request, loginID);
    }
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
        mastereditRow = dsMasterTable.getInternalRow();
        buy_applyID = dsMasterTable.getValue("buy_applyID");
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
      EngineDataSet ds = getMasterTable();
      EngineDataSet detail = getDetailTable();
      String deptid = m_RowInfo.get("deptid");                       //部门id==计划部门
      String personid=m_RowInfo.get("personid");                     //人员ID==计划人
      String apply_cause=m_RowInfo.get("apply_cause");               //申购原因
      String apply_date=m_RowInfo.get("apply_date");                 //申购日期
      //String apply_type=m_RowInfo.get("apply_type");                 //1=设备购置申请,2=配件购置申请
      String main_function=m_RowInfo.get("main_function");           //设备购置申请 主要功能
      String memo = m_RowInfo.get("memo");                           //备注
      String approverID = m_RowInfo.get("approverID");            //审批人
      String state_desc = m_RowInfo.get("state_desc");            //状态描述
      String createDate = m_RowInfo.get("createDate");            //制单日期
      String creatorID = m_RowInfo.get("creatorID");              //制单人ID
      String creator = m_RowInfo.get("creator");
      //校验表单数据
      String temp = checkDetailInfo(data);
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      if(deptid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择申购部门！');"));
        return;
      }
      if(personid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择申购人！');"));
        return;
      }
      if(apply_date.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择申购日期！');"));
        return;
      }
      if(!isMasterAdd)
        ds.goToInternalRow(mastereditRow);
      String buy_applyID = null;
      String drawcode = null;
      if(isMasterAdd){
        ds.insertRow(false);
        buy_applyID = dataSetProvider.getSequence("s_sb_buyApply");
        ds.setValue("buy_applyID", buy_applyID);
        if(apply_type.equals("1"))//1=设备购置申请,2=配件购置申请
       {
          ds.setValue("apply_type","1");
          drawcode = dataSetProvider.getSequence("SELECT pck_base.billNextCode('sb_buyapply','buy_applyno','a') from dual");
        }
       else
       {
          ds.setValue("apply_type","2");
          drawcode = dataSetProvider.getSequence("SELECT pck_base.billNextCode('sb_buyapply','buy_applyno','b') from dual");
       }
      }
      ds.setValue("buy_applyno",drawcode);
      ds.setValue("deptid",deptid);
      ds.setValue("personid", personid);
      ds.setValue("apply_cause", apply_cause);
      ds.setValue("apply_date",apply_date);
      ds.setValue("main_function", main_function);
      ds.setValue("memo",memo);
      ds.setValue("state", "0");                 //状态
      ds.setValue("approverID", approverID);
      ds.setValue("state_desc", state_desc);
      ds.setValue("createDate", createDate);
      ds.setValue("creatorID", creatorID);
      ds.setValue("creator", creator);
      ds.setValue("filialeID", filialeID);
      //保存从表的数据
      RowMap detailrow = null;
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        detail.setValue("buy_applyID", buy_applyID);
        detail.setValue("cpid", detailrow.get("cpid"));                                //产品ID
        detail.setValue("cpbm",detailrow.get("cpbm"));                                 //产品编码
        detail.setValue("equipmentID", detailrow.get("equipmentID"));                  //设备ID
        detail.setValue("equipment_code", detailrow.get("equipment_code"));             //设备编码
        detail.setValue("equipment_name", detailrow.get("equipment_name"));            //设备名称
        detail.setValue("standard_gg", detailrow.get("standard_gg"));                  //规格型号
        detail.setValue("manufacturer", detailrow.get("manufacturer"));                 //制造厂
        detail.setValue("price", detailrow.get("price"));                               //价格性能比较
        detail.setValue("need_getdate", detailrow.get("need_getdate"));                 //要求到货日期(配件购置申请)
        detail.setValue("apply_num", detailrow.get("apply_num"));                       //申购数量
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
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo(RunData data){
      String temp = null;
      RowMap detailrow = null;
      String equipmentID = new String();
      String cpid = new String();
      String equipmentname =new String();
      ArrayList list = new ArrayList(d_RowInfos.size());
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      if(apply_type.equals("1"))//1=设备购置申请,2=配件购置申请
      {
        for(int i=0; i<d_RowInfos.size(); i++)
        {
          detailrow = (RowMap)d_RowInfos.get(i);
          equipmentname = detailrow.get("equipment_name");
          if(equipmentname.equals(""))
            return showJavaScript("alert('第"+(i+1)+"行设备名称不能为空');");
          equipmentID = data.getParameter("equipmentID_"+i);
          if(list.contains(equipmentID)&&!equipmentID.equals(""))
            return showJavaScript("alert('第"+(i+1)+"行设备编码重复');");
          else
            list.add(equipmentID);
        }
      }
      else{
        for(int i=0; i<d_RowInfos.size(); i++)
        {
          detailrow = (RowMap)d_RowInfos.get(i);
          cpid =detailrow.get("cpid");
          if(cpid.equals(""))
            return showJavaScript("alert('第"+(i+1)+"行元件名称不能为空')");
          cpid=data.getParameter("cpid_"+i);
          if(list.contains(cpid)&&!cpid.equals(""))
            return showJavaScript("alert('第"+(i+1)+"行元件编码重复');");
          else
            list.add(cpid);
        }
      }
      return null;
    }
  }
  /**
   * 主表删除操作的触发类
   */
  class Master_Delete implements Obactioner
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
      if(isMasterAdd){
        data.setMessage(showJavaScript("backList();"));
        return;
      }
      EngineDataSet ds = getMasterTable();
      ds.goToInternalRow(mastereditRow);
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   * 从表添加操作的触发类
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMasterTable();
      isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
      if(!isMasterAdd)
        ds.goToInternalRow(mastereditRow);
      String buy_applyID = dsMasterTable.getValue("buy_applyID");
      detail.insertRow(false);
      detail.setValue("buy_applyID", isMasterAdd ? "-1" : buy_applyID);
      detail.post();
      d_RowInfos.add(new RowMap());
    }
  }
  /**
   * 从表删除操作的触发类
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
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      mastereditRow = dsMasterTable.getInternalRow();
      buy_applyID = dsMasterTable.getValue("buy_applyID");
      //打开从表
      openDetailTable(false);
    }
  }

  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String tempSQL=null;
      String SQL=combineSQL(Master_SQL,"?",new String[]{user.getHandleDeptValue("deptid"),apply_type});
      table.getWhereInfo().setWhereValues(data.getRequest());
      tempSQL = table.getWhereInfo().getWhereQuery();
      if(tempSQL.length() > 0){
        SQL=SQL+" and "+tempSQL;
      }
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
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
      String content = dsMasterTable.getValue("buy_applyno");
      String deptid = dsMasterTable.getValue("deptid");//下达车间
      String billName=apply_type.equals("1")?"buy_equipment_requisition":"buy_fitting_requisition";
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), billName, content, deptid);
    }
  }
    /*审批操作*/
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginID);
      detailProducer.init(request, loginID);
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
      buy_applyID = dsMasterTable.getValue("buy_applyID");
      //打开从表
      openDetailTable(false);
      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
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
      String billName=apply_type.equals("1")?"buy_equipment_requisition":"buy_fitting_requisition";
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), billName);
    }
  }
}

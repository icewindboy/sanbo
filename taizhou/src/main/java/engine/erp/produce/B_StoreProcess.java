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
//import engine.erp.store.Select_Batch;


import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 自制收货单和生产领料单列表</p>
 * <p>Description: 自制收货单和生产领料单列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

/**
 * 03.16 15:04 修改 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
 *                 以实现:mantis上库存管理中0000158bug描述的:根据下达的部门，进行提交审批；
 * 03.16 15:25 修改 在Master_Search中去掉判断旧的SQL和新产生的SQL是否相同的if语句. yjg
 * 03.10 09:53 新增 新增当 引入加工单 事件在这里做处理是也要把dmsxid(规格属性取得) yjg
 */

public final class B_StoreProcess extends BaseAction implements Operate
{
  public  static final String DETAIL_SEL_PROCESS = "10801";
  public  static final String RECEIVE_SEL_PROCESS = "10881";
  public  static final String SELF_CANCEL_APPROVE = "16781";
  public  static final String RECEIVE_CANCEL_APPROVE = "19881";
  public  static final String ONCHANGE = "10031";//选择仓库提交
  public  static final String PROP_ONCHANGE = "10041";//输入产品编码或则品名触发事件
  public  static final String TRANSFERSCAN = "10051";//读盘点机触发事件
  public  static final String SHOW_DETAIL = "12500";//调用从表明细资料
  public  static final String REPORT = "2000";//02.23 11:26 新增 为配合小李的报表追踪调用事件而加 yjg
  public  static final String NEXT = "9999";//新增 为上一笔,下一笔打印而加的
  public  static final String PRIOR = "9998";//新增 为上一笔,下一笔打印而加的
  public  static final String MATCHING_BATCH = "2001";//配批号触发事件

  private static final String MASTER_STRUT_SQL = "SELECT * FROM kc_sfdj WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM kc_sfdj WHERE ? AND djxz=? and fgsid=? ? ORDER BY sfdjdh DESC";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM kc_sfdjmx WHERE 1<>1";
  //02.28 23:15  修改 将下面此句的sql,用了'?'来代替原来的sfdjid=.
  //解决了当sdfdjid是空的时候会页面上(主要是contract_instore_bottom.jsp上)会出现sql错误的问题.yjg
  private static final String DETAIL_SQL = "SELECT * FROM kc_sfdjmx WHERE sfdjid='?'";//
  private static final String SELF_SINGLE_PROCESS_SQL = "SELECT * FROM VW_STOREPROCESS_SEL_PROCESS WHERE jgdid= ";//自制收货单引入加工单明细信息
  private static final String SINGLE_PROCESS_SQL = "SELECT * FROM VW_RECEIVE_SEL_PROCESS WHERE jgdid= ";//生产领料单引入加工单明细并根据BOM表从表增加
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM kc_sfdj WHERE djxz='?' and sfdjid='?'";
  //
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "kc_sfdj.3");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "kc_sfdjmx.3");
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态
  public  boolean isDetailAdd = false; //从表是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  public  boolean isReport =false;//02.23 11:26 新增 为配合小李的报表追踪调用事件而加的这个标志 yjg

  private LookUp buyOrderBean = null; //采购单价的bean的引用, 用于提取采购单价
 // private Select_Batch selectBatchBean = null; //选择批号的bean的引用, 用于从表产品选择批号

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  int djxz =1;//单据性质

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  public  String bjfs = ""; //系统的报价方式

  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  public String isHandwork = null; //是否允许手工录入自制入库单
  private User user = null;
  /**
   * 入库单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回入库单列表的实例
   */
  public static B_StoreProcess getInstance(HttpServletRequest request)
  {
    B_StoreProcess storeProcessBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "storeProcessBean";
      storeProcessBean = (B_StoreProcess)session.getAttribute(beanName);
      if(storeProcessBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        storeProcessBean = new B_StoreProcess();
        storeProcessBean.qtyFormat = loginBean.getQtyFormat();

        storeProcessBean.fgsid = loginBean.getFirstDeptID();
        storeProcessBean.loginId = loginBean.getUserID();
        storeProcessBean.loginName = loginBean.getUserName();
        storeProcessBean.loginDept = loginBean.getDeptID();
        storeProcessBean.bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");
        storeProcessBean.isHandwork = loginBean.getSystemParam("KC_HANDIN_STOCK_BILL");
        storeProcessBean.user = loginBean.getUser();
        //设置格式化的字段
        storeProcessBean.dsDetailTable.setColumnFormat("sl", storeProcessBean.qtyFormat);
        storeProcessBean.dsMasterTable.setColumnFormat("zsl", storeProcessBean.qtyFormat);
        session.setAttribute(beanName, storeProcessBean);
      }
    }
    return storeProcessBean;
  }

  /**
   * 构造函数
   */
  private B_StoreProcess()
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
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"sfdjdh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"rkdmxid"}, new String[]{"s_kc_sfdjmx"}));
    //dsDetailTable.setSort(new SortDescriptor("", new String[]{"hthwid"}, new boolean[]{false}, null, 0));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(DETAIL_SEL_PROCESS), new Detail_Sel_Process());//自制收货单引入加工单从表增加操作
    addObactioner(String.valueOf(RECEIVE_SEL_PROCESS), new Receive_Sel_Process());//生产领料单引入加工单从表增加操作
    addObactioner(String.valueOf(SELF_CANCEL_APPROVE), new Cancel_Approve());//自制收货单取消审批操作
    addObactioner(String.valueOf(RECEIVE_CANCEL_APPROVE), new Cancel_Approve());//生产领料单取消审批操作
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(PROP_ONCHANGE), new Onchange());
    addObactioner(String.valueOf(TRANSFERSCAN), new transferScan());
    addObactioner(SHOW_DETAIL, new Show_Detail());//02.17 15:16 新增 新增查看从表明细资料事件发生时的触发操作类. yjg
    addObactioner(String.valueOf(REPORT), new Approve());//2.14 新增报表追此事件 yjg
    //addObactioner(String.valueOf(MATCHING_BATCH), new Matching_Batch());//3.44 新增配批号触发事件
    addObactioner(NEXT, new Move_Cursor_ForPrint());
    addObactioner(PRIOR, new Move_Cursor_ForPrint());
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
        m_RowInfo.put("sfrq", today);//收发日期
        m_RowInfo.put("zdrid", loginId);
        m_RowInfo.put("deptid", loginDept);
        m_RowInfo.put("jsr", loginName);
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
      detailRow.put("cpid", rowInfo.get("cpid_"+i));
      detailRow.put("kwid", rowInfo.get("kwid_"+i));//库位
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
      detailRow.put("ph", rowInfo.get("ph_"+i));//
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
    }
  }
  /**
   *  从表配批号操作，可以看到当前行产品的库存数量

  class Matching_Batch implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      int row = Integer.parseInt(req.getParameter("rownum"));
      String mutibatch = m_RowInfo.get("mutibatch_"+row);
      if(mutibatch.length() == 0)
        return;
      RowMap rowinfo = (RowMap)d_RowInfos.get(row);
      dsDetailTable.goToRow(row);
      String sfdjid = dsDetailTable.getValue("sfdjid");
      String wjid = dsDetailTable.getValue("wjid");
      String cpid = rowinfo.get("cpid");
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[]{"dmsxid","ph"});
      String[] wzmxIDs = parseString(mutibatch,",");
      RowMap detail = null;
      for(int i=0; i < wzmxIDs.length; i++)
      {
        if(wzmxIDs[i].equals("-1"))
          continue;
        RowMap batchRow = getBatchBean(req).getLookupRow(wzmxIDs[i]);
        String ph = batchRow.get("ph");
        String dmsxid = batchRow.get("dmsxid");
        locateGoodsRow.setValue(0, dmsxid);
        locateGoodsRow.setValue(1, ph);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          if(i==0){
            detail = (RowMap)d_RowInfos.get(row);
          }
          else {
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("sfdjid", sfdjid);
            dsDetailTable.setValue("wjid", wjid);
            dsDetailTable.setValue("cpid", cpid);
            dsDetailTable.post();
            detail = new RowMap(dsDetailTable);
            d_RowInfos.add(++row, detail);
          }
          detail.put("wzmxid",batchRow.get("wzmxid"));
          detail.put("ph",batchRow.get("ph"));
          detail.put("dmsxid",batchRow.get("dmsxid"));
          detail.put("kwid",batchRow.get("kwid"));
        }
        else{
          detail = (RowMap)d_RowInfos.get(row);
          detail.put("wzmxid",batchRow.get("wzmxid"));
          detail.put("ph",batchRow.get("ph"));
          detail.put("dmsxid",batchRow.get("dmsxid"));
          detail.put("kwid",batchRow.get("kwid"));
        }
      }
    }
  }
     */
  /**
   * 得到用于选择批号信息的bean
   * @param req WEB的请求
   * @return 返回用于某批号的这一行库存物资明细信息的bean

  public Select_Batch getBatchBean(HttpServletRequest req)
  {
    if(selectBatchBean == null)
      selectBatchBean = Select_Batch.getInstance(req);
    return selectBatchBean;
  }
     */
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
    String sfdjid = dsMasterTable.getValue("sfdjid");
    //02.28 23:15  修改 将下面此句setQueryString中的sql由原来的手动用+号组成sql`改成现在用combineSQL来组成.
    //解决了当sdfdjid是空的时候会页面上(主要是contract_instore_bottom.jsp上)会出现sql错误的问题.yjg
    dsDetailTable.setQueryString(combineSQL(DETAIL_SQL, "?", new String[]{isMasterAdd ? "-1" : sfdjid}));

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
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("zt", "0");
      row.put("sfrq$a", startDay);
      row.put("sfrq$b", today);
      isMasterAdd = true;
      isDetailAdd = false;
      //
      String  SQL = " AND zt<>1";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid","zdrid"),String.valueOf(djxz),fgsid,SQL});

      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
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
      isDetailAdd = false;
      isMasterAdd = String.valueOf(ADD).equals(action);
      isReport = false;//02.23 11:26 新增 为配合小李的报表追踪调用事件而加的这个标志 yjg
      if(!isMasterAdd)
      {
        isMasterAdd = false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        openDetailTable(false);
      }
      else//打开从表
      {
        //02.18 16:45 新增 同步子表 yjg
        synchronized(dsDetailTable){
          openDetailTable(true);
        }
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
    HttpServletRequest request = data.getRequest();
    masterProducer.init(request, loginId);
    detailProducer.init(request, loginId);
    /**
     *报表调从表页面,传递operate='2000'操作
      */
       isReport = String.valueOf(REPORT).equals(action);
       String id=null;
       if(isReport){
         isReport = true;
         isApprove = false;
         id = data.getParameter("id");//得到报表传递的参数既收发单据主表ID
       }
       else{
         isReport = false;
         isApprove = true;//审批操作
         id = data.getParameter("id", "");
      }
    String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{String.valueOf(djxz),id});
    dsMasterTable.setQueryString(sql);
    if(dsMasterTable.isOpen()){
      dsMasterTable.readyRefresh();
      dsMasterTable.refresh();
    }
    else
      dsMasterTable.open();

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
    String content = dsMasterTable.getValue("sfdjdh");
    //03.16 15:04 修改 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
    //以实现:mantis上库存管理中0000158bug描述的:根据下达的部门，进行提交审批；
    if(djxz==3)
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "self_gain_list", content, dsMasterTable.getValue("deptid"));
    if(djxz==4)
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "receive_material_list", content, dsMasterTable.getValue("deptid"));
  }
}
/**
 * 取消审批触发操作
 */
class Cancel_Approve implements Obactioner
  {
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
    ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
    boolean isSelfGain = String.valueOf(SELF_CANCEL_APPROVE).equals(action);
    if(isSelfGain)
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "self_gain_list");
    else
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "receive_material_list");
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
      String sfdjid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        sfdjid = dataSetProvider.getSequence("s_kc_sfdj");
        ds.setValue("sfdjid", sfdjid);
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
          detail.setValue("sfdjid", sfdjid);

        detail.setValue("sl", detailrow.get("sl"));//保存数量
        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("kwid", detailrow.get("kwid"));
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("djxz", String.valueOf(djxz));
        detail.setValue("ph", detailrow.get("ph"));
        detail.setValue("bz", detailrow.get("bz"));//备注
        detail.setValue("fgsid", fgsid);
        //保存用户自定义字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
       for(int j=0; j<fields.length; j++)
       {
         String fieldCode = fields[j].getFieldcode();
         detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        detail.next();
      }

      //保存主表数据
      ds.setValue("storeid", rowInfo.get("storeid"));//仓库id
      ds.setValue("jsr", rowInfo.get("jsr"));//经手人
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("sfdjlbid", rowInfo.get("sfdjlbid"));//收发单据类别ID
      ds.setValue("djxz", String.valueOf(djxz));//单据性质
      ds.setValue("ytid", rowInfo.get("ytid"));//用途ID
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));//购货单位ID
      ds.setValue("sfrq", rowInfo.get("sfrq"));//收发日期
      ds.setValue("zsl", totalNum.toString());//总数量
      ds.setValue("bz", rowInfo.get("bz"));//备注
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      if(djxz==3)
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"sfdjdh"}, new String[]{"SELECT pck_base.billNextCode('kc_sfdj','sfdjdh','f') from dual"}));
      if(djxz==4)
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"sfdjdh"}, new String[]{"SELECT pck_base.billNextCode('kc_sfdj','sfdjdh','e') from dual"}));
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_IN);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_OUT);

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
      //ArrayList list = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        String cpid = detailrow.get("cpid");
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        /*
        if(list.contains(cpid))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(cpid);
        */
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
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
      String temp = rowInfo.get("sfrq");
      if(temp.equals(""))
        return showJavaScript("alert('收发日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法收发日期！');");
      temp = rowInfo.get("storeid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择仓库！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择部门！');");
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
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_IN);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_OUT);
      //
      d_RowInfos.clear();
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
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid","zdrid"), String.valueOf(djxz), fgsid, SQL});
      //03.16 15:25 修改 在Master_Search中去掉判断旧的SQL和新产生的SQL是否相同的if语句. yjg
      //if(!dsMasterTable.getQueryString().equals(SQL))
      //{
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);
      //}
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
        new QueryColumn(master.getColumn("sfdjdh"), null, null, null),
        new QueryColumn(master.getColumn("sfrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("sfrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("storeid"), null, null, null, null, "="),//仓库
        new QueryColumn(master.getColumn("ytid"), null, null, null, null, "="),//用途
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门
        new QueryColumn(master.getColumn("sfdjid"), "kc_sfdjmx", "sfdjid", "cpid", null, "="),//从表品名
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "cpbm", "cpbm", "like"),//从表产品编码
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "product", "product", "like"),//从表产品名称
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
  /**
   *  从表增加操作(增加一个空白行)
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
      String sfdjid = dsMasterTable.getValue("sfdjid");
      detail.insertRow(false);
      detail.setValue("sfdjid", isMasterAdd ? "-1" : sfdjid);
      detail.post();
      d_RowInfos.add(new RowMap());
    }
  }
  /**
   *  自制收货单引入加工单从表增加操作
   */

  class Detail_Sel_Process implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowinfo = getMasterRowinfo();

      String importProcess = rowinfo.get("importProcess");
      if(importProcess.equals(""))
        return;
      String SQL = SELF_SINGLE_PROCESS_SQL + importProcess;
      EngineDataSet processData = null;
      if(processData==null)
      {
        processData = new EngineDataSet();
        setDataSetProperty(processData,null);
      }
      processData.setQueryString(SQL);
      if(!processData.isOpen())
        processData.openDataSet();
      else
        processData.refresh();
      rowinfo.put("deptid", processData.getValue("deptid"));
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wjid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String sfdjid = dsMasterTable.getValue("sfdjid");
      locateGoodsRow.setValue(0, importProcess);
      if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
      {
        for(int i=0; i < processData.getRowCount(); i++)
        {
          processData.goToRow(i);
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("rkdmxid","-1");
          dsDetailTable.setValue("wjid",processData.getValue("wjid"));
          String cpid = processData.getValue("cpid");
          String sl = processData.getValue("sl");
          dsDetailTable.setValue("cpid",processData.getValue("cpid"));
          dsDetailTable.setValue("sl",processData.getValue("sl"));
          //03.10 09:53 新增 新增当 引入加工单 事件在这里做处理是也要把dmsxid(规格属性取得) yjg
          dsDetailTable.setValue("dmsxid",processData.getValue("dmsxid"));
          dsDetailTable.setValue("sfdjid", isMasterAdd ? "" : sfdjid);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          RowMap detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
   }
 }
 /**
   *调用盘点单触发的事件
   */
  class transferScan implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowinfo = getMasterRowinfo();

      String storeid = rowinfo.get("storeid");
      String scanValue= req.getParameter("scanValue");//得到包含产品编码和批号的字符串
      String[][] s=engine.util.StringUtils.getArrays(scanValue);
      String[] cpbmStr = s[0];//产品编码数组
      String[] phStr = s[1];//批号数组
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cpid");
      LookUp prodCodeBean = LookupBeanFacade.getInstance(req,SysConstant.BEAN_PRODUCT_CODE);
      prodCodeBean.regData(cpbmStr);
      for(int i=0; i<cpbmStr.length; i++)
      {
        String cpbm = cpbmStr[i];
        String ph = phStr[i];
        RowMap prodCodeRow = prodCodeBean.getLookupRow(cpbm);
        String cpid = prodCodeRow.get("cpid");
        String p_storeid = prodCodeRow.get("storeid");
        if(cpid.equals("") || (!p_storeid.equals(storeid) && !p_storeid.equals("")))//如果cpid为空或者存放仓库不等于所选仓库继续
          continue;
        locateGoodsRow.setValue(0, cpid);
        if(dsDetailTable.locate(locateGoodsRow, Locate.FIRST)){
          String o_ph = dsDetailTable.getValue("ph");
          if(isHandwork.equals("0")){
            if(o_ph.equals("") || !o_ph.equals(ph))
              dsDetailTable.setValue("ph", ph);
          }
          else{
             if(o_ph.equals(""))
               dsDetailTable.setValue("ph",ph);
             else if(o_ph.equals(ph))
             {
               dsDetailTable.insertRow(false);
               dsDetailTable.setValue("rkdmxid", "-1");
               dsDetailTable.setValue("cpid", cpid);
               dsDetailTable.setValue("ph", ph);
               dsDetailTable.post();
               RowMap detail = new RowMap(dsDetailTable);
               d_RowInfos.add(detail);
             }
          }
        }
        else if(isHandwork.equals("1"))
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("rkdmxid", "-1");
          dsDetailTable.setValue("cpid", cpid);
          dsDetailTable.setValue("ph", ph);
          dsDetailTable.post();
          RowMap detail = new RowMap(dsDetailTable);
          d_RowInfos.add(detail);
        }
      }
    }
  }
 /**
  *  生产领料单引入加工单从表增加操作
  */
 class Receive_Sel_Process implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     //保存输入的明细信息
     putDetailInfo(data.getRequest());
     RowMap rowinfo = getMasterRowinfo();

      String selectProcess = rowinfo.get("selectProcess");
      if(selectProcess.equals(""))
        return;
      EngineDataSet processBomData = null;
      String SQL = SINGLE_PROCESS_SQL + selectProcess;
      if(processBomData==null)
      {
        processBomData = new EngineDataSet();
        setDataSetProperty(processBomData,null);
      }
      processBomData.setQueryString(SQL);
      if(!processBomData.isOpen())
        processBomData.openDataSet();
      else
        processBomData.refresh();
      if(processBomData.getRowCount()<1){
        data.setMessage(showJavaScript("alert('加工单中产品没有设置BOM表')"));
        return;
      }
      if(rowinfo.get("deptid").equals(""))
      rowinfo.put("deptid", processBomData.getValue("deptid"));
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wjid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String sfdjid = dsMasterTable.getValue("sfdjid");
      locateGoodsRow.setValue(0, selectProcess);
      if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
      {
        processBomData.first();
        for(int i=0; i < processBomData.getRowCount(); i++)
        {
          processBomData.goToRow(i);
          double sl = processBomData.getValue("sl").length()>0 ? Double.parseDouble(processBomData.getValue("sl")) : 0;//加工单中产品要加工的数量
          double fsl = processBomData.getValue("fsl").length()>0 ? Double.parseDouble(processBomData.getValue("fsl")) : 0;//生产该产品需要下级原料的数量

          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("rkdmxid","-1");
          dsDetailTable.setValue("wjid", processBomData.getValue("jgdmxid"));
          dsDetailTable.setValue("cpid", processBomData.getValue("cpid"));
          dsDetailTable.setValue("sl", formatNumber(String.valueOf(sl*fsl),qtyFormat));
          dsDetailTable.setValue("wzmxid", processBomData.getValue("wzmxid"));
          dsDetailTable.setValue("dmsxid", processBomData.getValue("dmsxid"));
          dsDetailTable.setValue("kwid", processBomData.getValue("kwid"));
          dsDetailTable.setValue("sfdjid", isMasterAdd ? "" : sfdjid);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          RowMap detailrow = new RowMap(dsDetailTable);
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
      //删除临时数组的一列数据
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
    }
  }
  /**
  * 提交仓库
  */
 class Onchange implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest request = data.getRequest();
     putDetailInfo(request);
   }
 }

 //02.17 15:12 新增 新增一个当页面上单击事件发生时想要查看明细资料 这个单击事件的Show_Detail类 . yjg
  /**
  * 显示从表的列表信息
  */
 class Show_Detail implements Obactioner
 {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterTable.getInternalRow();
      //打开从表
      openDetailTable(false);
    }
  }
  /**
   * 新增 实现翻页为方便打印的类.
   */
  class Move_Cursor_ForPrint implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      boolean isNext = String.valueOf(NEXT).equals(action);
      dsMasterTable.goToInternalRow(masterRow);
      if(isNext)
        dsMasterTable.next();
      else
        dsMasterTable.prior();
      masterRow = dsMasterTable.getInternalRow();

        //dsMasterTable.goToInternalRow(masterRow+1);
        //masterRow = dsMasterTable.getInternalRow();
        //int i = dsMasterTable.getRow();
      synchronized(dsDetailTable){
        openDetailTable(false);
      }
      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
    }
  }

  public long getMasterRow()
  {
    return masterRow;
  }
  /**
   * 得到用于查找合同编号的bean
   * @param req WEB的请求
   * @return 返回用于查找合同编号的bean

  public LookUp getBuyOrderBean(HttpServletRequest req)
  {
    if(buyOrderBean == null)
      buyOrderBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_BUY_ORDER_GOODS);
    return buyOrderBean;
  }
  */
}
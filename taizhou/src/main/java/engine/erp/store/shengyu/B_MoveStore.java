package engine.erp.store.shengyu;

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
import engine.erp.store.*;


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
 * <p>Title: 报损单列表</p>
 * <p>Description: 报损单列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

/**
 * 2004-06-14 09:21 修改原来的batchRow.get("wzmxid")为只赋值为1
 * 03.16 15:04 修改 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
 *                  以实现:根据下达的部门，进行提交审批；
 * 03.16 16:03 加入部门不能为空的判断. yjg
 */

public final class B_MoveStore extends BaseAction implements Operate
{
  //public  static final String DETAIL_ORDERGOODS_ADD = "10801";
  public  static final String CANCEL_APPROVE = "19801";
  public  static final String ONCHANGE = "10031";//选择仓库提交
  public  static final String SHOW_DETAIL = "12500";//响应 调用从表明细资料 事件
  public  static final String REPORT = "2000";//02.23 11:26 新增 为配合小李的报表追踪调用事件而加 yjg
  public  static final String NEXT = "9999";//新增 为上一笔,下一笔打印而加的
  public  static final String PRIOR = "9998";//新增 为上一笔,下一笔打印而加的
  public  static final String RECODE_ACCOUNT = "9996";//2004-4-17 17:48 新增 记帐功能的sql yjg
  public  static final String NEW_TRANSFERSCAN = "10062";//读新盘点机触发事件
  public  static final String TRANSFERSCAN = "10061";//调用盘点单 06.09 11:34 新增因为此盘点功能要新增一个盘点机功能 yjg
  public  static final String TURNPAGE     = "9995";// 新增 为明细表格番页而加的事件
  public  static final String DETAIL_ADD_BLANK = "9994";//从表增加一行
  public  static final String DELETE_BLANK   = "11581";//删除从表中库存量为空的纪录触发事件

  private static final String MASTER_STRUT_SQL = "SELECT * FROM kc_sfdj WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM kc_sfdj WHERE ? AND djxz=? and fgsid=? ? ORDER BY sfrq DESC";
  private static final String TOTALZSL_SQL  = "SELECT SUM(nvl(zsl,0)) tzsl FROM kc_sfdj a WHERE ? AND djxz=? AND fgsid=? ? ORDER BY sfdjdh DESC";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM kc_sfdjmx WHERE 1<>1";
  //02.28 23:15  修改 将下面此句的sql,用了'?'来代替原来的sfdjid=.
  //解决了当sdfdjid是空的时候会页面上(主要是contract_instore_bottom.jsp上)会出现sql错误的问题.yjg
  private static final String DETAIL_SQL = "SELECT * FROM kc_sfdjmx WHERE sfdjid='?'";//
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM kc_sfdj WHERE djxz='?' and sfdjid='?'";
  //查询数据库是否有记账的单据
  private static final String RECODE_DATASQL     = " SELECT COUNT(*) FROM kc_sfdj a WHERE a.zdrid='?' AND  a.djxz ='?' AND a.zt=1";
  //把符合记帐功能的数据全部记帐
  private static final String RECODE             = "UPDATE kc_sfdj a SET a.zt=2 WHERE a.zdrid='?' AND  a.djxz ='?' AND a.zt=1";
  //只取库存物资明细表中的数据
  private static final String STOCK_MATERIAL_LIST
      = " SELECT * FROM "
      + " ( "
      + "   SELECT b.storeid, b.cpid, b.dmsxid, b.ph, SUM(nvl(b.zl,0)) kcsl, SUM(nvl(b.hszl, 0))  hszl "
      + "   FROM kc_wzmx b, vw_kc_dm_exist a "
      + "    WHERE  b.cpid = a.cpid "
      + "    GROUP BY b.storeid, b.cpid, b.dmsxid, b.ph "
      + "  ) b "
      + "  WHERE b.kcsl <> 0 and b.storeid = '@' ";
  //private static final String PRODUCT_PREFIX_SQL = "SELECT codeprefix FROM jc_coderule WHERE coderule=";
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsTotalZsl     = new EngineDataSet();//统计总数量和
  private EngineDataSet dmsxidExistData       = new EngineDataSet();//代码属性是否存在的数据集
  private EngineDataSet dsStockMaterialDetail = new EngineDataSet();//物资明细表的数据集
  public  HtmlTableProducer masterProducer    = new HtmlTableProducer(dsMasterTable, "kc_sfdj.6");
  public  HtmlTableProducer detailProducer    = new HtmlTableProducer(dsDetailTable, "kc_sfdjmx.6");

  private ImportMaterail importMaterailBean = null; //物资明细的bean的引用, 用于提取物资明细

  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态
  public  boolean isDetailAdd = false;   //从表是否在审批状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  public  boolean isReport =false;//02.23 11:26 新增 为配合小李的报表追踪调用事件而加的这个标志 yjg

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  int djxz =1;//单据性质
  public String totalzsl = "0";

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  //public  String bjfs = ""; //系统的报价方式

  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private User user = null;

  //被分页后的数据集中某一个页面中从第几笔记录开始到第几笔数据结束.如第二页的资料范围是从第51-101笔
  public int min = 0;
  public int max = 0;
  public String isRepeat = "0";//重定向，如果本业检测数据不正确的话isrepeat为1。将不翻页
  private double childcount = 0;   //判断仓库是否含有库位
  private ArrayList locateImportMaterial = new ArrayList();//用此对象的时候是,引入库存中的物资里在明细数据集定位
  /**
   * 入库单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回入库单列表的实例
   */
  public static B_MoveStore getInstance(HttpServletRequest request)
  {
    B_MoveStore moveStoreBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "moveStoreBean";
      moveStoreBean = (B_MoveStore)session.getAttribute(beanName);
      if(moveStoreBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        moveStoreBean = new B_MoveStore();
        moveStoreBean.qtyFormat = loginBean.getQtyFormat();

        moveStoreBean.fgsid = loginBean.getFirstDeptID();
        moveStoreBean.loginId = loginBean.getUserID();
        moveStoreBean.loginName = loginBean.getUserName();
        moveStoreBean.loginDept = loginBean.getDeptID();
        moveStoreBean.user = loginBean.getUser();
        //设置格式化的字段
        moveStoreBean.dsDetailTable.setColumnFormat("sl", moveStoreBean.qtyFormat);
        moveStoreBean.dsDetailTable.setColumnFormat("hssl", moveStoreBean.qtyFormat);
        moveStoreBean.dsMasterTable.setColumnFormat("zsl", moveStoreBean.qtyFormat);
        moveStoreBean.dsMasterTable.setColumnFormat("drsl", moveStoreBean.qtyFormat);
        session.setAttribute(beanName, moveStoreBean);
      }
    }
    return moveStoreBean;
  }

  /**
   * 构造函数
   */
  private B_MoveStore()
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
    setDataSetProperty(dsTotalZsl, null);

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"sfdjdh"}, new String[]{"SELECT pck_base.billNextCode('kc_sfdj','sfdjdh','g') from dual"}));
    //dsMasterTable.setSort(new SortDescriptor("", new String[]{"sfdjdh"}, new boolean[]{true}, null, 0));

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
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(SHOW_DETAIL, new Show_Detail());//02.17 20:54 新增 新增查看从表明细资料事件发生时的触发操作类. yjg
    addObactioner(String.valueOf(REPORT), new Approve());//2.14 新增报表追此事件 yjg
    addObactioner(NEXT, new Move_Cursor_ForPrint());
    addObactioner(PRIOR, new Move_Cursor_ForPrint());
    addObactioner(String.valueOf(RECODE_ACCOUNT), new Recode_Account());//套打
    //06.09 11:32 新增 注册一个处理盘点机按钮事件的操作.此操作通过 transferScan() 类来完成.这个类也是要新增的. yjg
    addObactioner(String.valueOf(TRANSFERSCAN), new transferScan());//旧盘点机
    addObactioner(String.valueOf(NEW_TRANSFERSCAN), new transferScan());//新盘点机
    addObactioner(TURNPAGE, new Turn_Page());//翻页事件
    addObactioner(DETAIL_ADD_BLANK, new Detail_Add_Blank());//翻页事件
    addObactioner(DELETE_BLANK, new Delete_Blank());//翻页事件
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
    if(dsTotalZsl != null){
      dsTotalZsl.close();
      dsTotalZsl = null;
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
    for(int i=min; i<=max; i++)
    {
      String cpid = rowInfo.get("cpid_"+i);
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//调出库位
      detailRow.put("kwid", rowInfo.get("kwid_"+i));//调入库位
      detailRow.put("kc__kwid", rowInfo.get("kc__kwid_"+i));//调出库位
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
      detailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+i), qtyFormat));//
      detailRow.put("drsl", formatNumber(rowInfo.get("drsl_"+i), qtyFormat));//
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//
      detailRow.put("ph", rowInfo.get("ph_"+i));//
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
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
      String SQL = " AND zt<>1";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),String.valueOf(djxz),fgsid,SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();

      String tempTzslSql = " AND zt<>1 ";
      tempTzslSql = combineSQL(TOTALZSL_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), String.valueOf(djxz),fgsid,tempTzslSql});
      dsTotalZsl.setQueryString(tempTzslSql);
      if (dsTotalZsl.isOpen())
        dsTotalZsl.refresh();
      else
        dsTotalZsl.openDataSet();
      if (dsTotalZsl.getRowCount()<1)
        totalzsl = "0";
      else
        totalzsl = dsTotalZsl.getValue("tzsl");
    }
  }

  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      locateImportMaterial = new ArrayList();
      isApprove = false;
      isDetailAdd = false;
      isMasterAdd = String.valueOf(ADD).equals(action);
      isReport = false;//02.23 11:26 新增 为配合小李的报表追踪调用事件而加的这个标志 yjg
      if(!isMasterAdd)
      {
        isMasterAdd =false;
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
        isApprove = false;
        id = data.getParameter("id");//得到报表传递的参数既收发单据主表ID
      }
      else{
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

  //&#$
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
      //以实现:根据下达的部门，进行提交审批；
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "move_store_list", content, dsMasterTable.getValue("deptid"));
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
      approve.cancelAprove(dsMasterTable, dsMasterTable.getRow(), "move_store_list");
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
      m_RowInfo.put(request);
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

        //double hsbl = detailrow.get("hsbl").length() > 0 ? Double.parseDouble(detailrow.get("hsbl")) : 0;//换算比例
        double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;
        double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;//换算数量
        double drsl = detailrow.get("drsl").length() > 0 ? Double.parseDouble(detailrow.get("drsl")) : 0;//换算数量
        detail.setValue("sl", detailrow.get("sl"));//保存数量
        detail.setValue("hssl", String.valueOf(hssl));//保存数量String.valueOf(hsbl==0 ? 0 : sl/hsbl)
        detail.setValue("drsl", String.valueOf(drsl));//保存数量String.valueOf(hsbl==0 ? 0 : sl/hsbl)
        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("dmsxid", detailrow.get("dmsxid"));//物资规格属性
        detail.setValue("kwid", detailrow.get("kwid"));
        detail.setValue("djxz", String.valueOf(djxz));
        detail.setValue("ph", detailrow.get("ph"));
        detail.setValue("bz", detailrow.get("bz"));//备注
        detail.setValue("kc__kwid", detailrow.get("kc__kwid"));
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
      ds.setValue("storeid", rowInfo.get("storeid"));//调出仓库id
      ds.setValue("jsr", rowInfo.get("jsr"));//经手人
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("sfdjlbid", rowInfo.get("sfdjlbid"));//收发单据类别ID
      ds.setValue("djxz", String.valueOf(djxz));//单据性质
      ds.setValue("kc__storeid", rowInfo.get("kc__storeid"));//调入仓库ID
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));//购货单位ID
      ds.setValue("ykdrdh", rowInfo.get("ykdrdh"));//购货单位ID
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
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_MOVE);

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action)){
        //data.setMessage(showJavaScript("backList();"));
        isMasterAdd = false;
        masterRow = ds.getInternalRow();//2004-3-30 14:53 新增 如果是保存按钮行为完成后定位数据集 yjg
        initRowInfo(true, isMasterAdd, true);
        initRowInfo(false, isMasterAdd, true);
      }
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      String temp = null;
      String cpid = new String();
      String ph = new String();
      String dmsxid  = new String();
      ArrayList list = new ArrayList(d_RowInfos.size());
      StringBuffer buf = new StringBuffer();
      String combinStr = new String();
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      //ArrayList list = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        ph = detailrow.get("ph");
        dmsxid = detailrow.get("dmsxid");
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
        String hssl = detailrow.get("hssl");
        if((temp = checkNumber(hssl, "第"+row+"行数量")) != null)
          return temp;
        if(hssl.length()>0 && hssl.equals("0"))
          return showJavaScript("alert('第"+row+"行换算数量不能为零！');");
        String drsl = detailrow.get("drsl");
       if((temp = checkNumber(drsl, "第"+row+"行数量")) != null)
         return temp;
       if(drsl.length()>0 && drsl.equals("0"))
          return showJavaScript("alert('第"+row+"行调入数量不能为零！');");
        //将每一行从页面上读取出来的此三个值相连. 02.14 yjg
        buf.append(cpid).append(",");
        buf.append(ph).append(",");
        buf.append(dmsxid);
        combinStr = buf.toString();
        // 新增 :如果cpid, ph, dmsxid的组合有相同的则是不允许的.盘点点中不允许有这样的情况. 02.14 21:19 yjg
        if(list.contains(combinStr))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(combinStr);
        buf.delete(0, buf.length());
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
      String storeid = rowInfo.get("storeid");
      if(storeid.equals(""))
        return showJavaScript("alert('请选择调出仓库！');");
      String kc__storeid = rowInfo.get("kc__storeid");
      if(kc__storeid.equals(""))
        return showJavaScript("alert('请选择调入仓库！');");
      //03.16 16:03 加入部门不能为空的判断. yjg
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
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE);
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), String.valueOf(djxz), fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);

      String tempTzslSql = fixedQuery.getWhereQuery();
      if(tempTzslSql.length() > 0)
        tempTzslSql = " AND "+tempTzslSql;
      tempTzslSql = combineSQL(TOTALZSL_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), String.valueOf(djxz),fgsid,tempTzslSql});
      dsTotalZsl.setQueryString(tempTzslSql);
      if (dsTotalZsl.isOpen())
        dsTotalZsl.refresh();
      else
        dsTotalZsl.openDataSet();
      if (dsTotalZsl.getRowCount()<1)
        totalzsl = "0";
      else
        totalzsl = dsTotalZsl.getValue("tzsl");
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
        new QueryColumn(master.getColumn("sfdjlbid"), null, null, null, null, "="),//单据类别
        new QueryColumn(master.getColumn("storeid"), null, null, null, null, "="),//仓库
        new QueryColumn(master.getColumn("kc__storeid"), null, null, null, null, "="),//调入仓库
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门
        new QueryColumn(master.getColumn("sfdjid"), "kc_sfdjmx", "sfdjid", "cpid", null, "="),//从表品名
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "cpbm", "cpbm", "like"),//从表产品编码
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "product", "product", "like"),//从表产品名称
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "pm", "pm", "like"),//从表产品名称
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "gg", "gg", "like"),//从表产品名称
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("jsr"), null, null, null)
      });
      isInitQuery = true;
    }
  }

  /**
  *  从表增加操作
  */
 class Detail_Add implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     //保存输入的明细信息
     putDetailInfo(data.getRequest());

     String multiIdInput = m_RowInfo.get("multiIdInput");
     if(multiIdInput.length() == 0)
       return;

     if(!isMasterAdd)
       dsMasterTable.goToInternalRow(masterRow);
     String sfdjid = dsMasterTable.getValue("sfdjid");

     //实例化查找数据集的类

     EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wjid");
     String[] wzmxID = parseString(multiIdInput,",");
     for(int i=0; i < wzmxID.length; i++)
     {
       if(!locateImportMaterial.contains(wzmxID[i])&&!(wzmxID[i].equals("-1")))
          locateImportMaterial.add(wzmxID[i]);
       else continue;

       /*
       if(wzmxID[i].equals("-1"))
         continue;
       */

       RowMap detailrow = null;
       String tempWzmxId = wzmxID[i];
       locateGoodsRow.setValue(0, wzmxID[i]);
       //if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
       //{
         RowMap materailRow = getSelectProductBean(req).getLookupRow(wzmxID[i]);
         double zl = materailRow.get("zl").length()>0 ? Double.parseDouble(materailRow.get("zl")) : 0;
         /*BigDecimal hsbl = new BigDecimal(0);
         String hsblStr = materailRow.get("hsbl");
         String sxz = materailRow.get("sxz");
         //try
         //{
           hsbl = calculateExpression(hsblStr, sxz);
         //}
         //catch (Exception ex)
         //{
            //log.error("calculateExpression", ex);
            //hsbl = new BigDecimal(0);
         }
         */
         dsDetailTable.insertRow(false);
         dsDetailTable.setValue("rkdmxid", "-1");
         dsDetailTable.setValue("wzmxid","1");//2004-06-14 09:21 修改原来的batchRow.get("wzmxid")为只赋值为1
         dsDetailTable.setValue("cpid", materailRow.get("cpid"));
         dsDetailTable.setValue("kwid", materailRow.get("kwid"));
         dsDetailTable.setValue("dmsxid", materailRow.get("dmsxid"));
         dsDetailTable.setValue("ph", materailRow.get("ph"));
         dsDetailTable.setValue("sl", materailRow.get("zl"));
         dsDetailTable.setValue("hssl", materailRow.get("hszl"));//2004-06-09 15:40新增 换算重量
         //dsDetailTable.setValue("hssl", String.valueOf(hsbl.doubleValue()*zl));
         dsDetailTable.setValue("sfdjid", isMasterAdd ? "" : sfdjid);
         dsDetailTable.post();
         //创建一个与用户相对应的行
         detailrow = new RowMap(dsDetailTable);
         d_RowInfos.add(detailrow);
      // }

     }
     data.setMessage(showJavaScript("totalCalSl();"));
   }
 }
 /**
  *  从表增加操作(增加一空白行)
  */
 class Detail_Add_Blank implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     //保存输入的明细信息
     putDetailInfo(data.getRequest());
     EngineDataSet detail = getDetailTable();
     EngineDataSet ds = getMaterTable();
     isDetailAdd = String.valueOf(DETAIL_ADD_BLANK).equals(action);
     if(!isMasterAdd)
       ds.goToInternalRow(masterRow);
     String sfdjid = ds.getValue("sfdjid");
     detail.insertRow(false);
     detail.setValue("sfdjid", isMasterAdd ? "-1" : sfdjid);
     detail.post();
     d_RowInfos.add(new RowMap());
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
   * 得到用于查找合同编号的bean
   * @param req WEB的请求
   * @return 返回用于查找合同编号的bean
   */
  public ImportMaterail getSelectProductBean(HttpServletRequest req)
  {
    if(importMaterailBean == null)
      importMaterailBean = ImportMaterail.getInstance(req);
    return importMaterailBean;
  }

  //02.17 17:33 新增 新增一个当页面上单击事件发生时想要查看明细资料 这个单击事件的Show_Detail类 . yjg
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
  //02.18 11:45 新增 新增这个类来处理盘点机事件时要做的操作. yjg
  /**
   * 调用盘点单触发的事件
   * 盘点机做的操作为:
   * 1.盘点机操作首先把符合指定条件的库存物资明细表中的数据全部取过来作为盘点数据记录放到dsDetailTable中用为盘点数据
   * 2.接着比较:
   *       2.1 如果盘点机机器中读入的一条数据能在1.操作中取到的库存物资明细表中的数据定位到.那么,就把这笔设置为盘点数据的scsl设为物资明细的kcsl
   *        2.2 如果盘点机机器中读入的一条数据在库存物资明细表中的数据中定位不到那么还是把这笔盘点机器中的数据插入dsDetailTable中.但scsl设置为0
   *3.保存数据.
   */
   class transferScan implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       HttpServletRequest req = data.getRequest();
       //保存输入的明细信息
       putDetailInfo(data.getRequest());
       //清空明细表dataset和rowinfo中所有数据集
       dsDetailTable.deleteAllRows();
       d_RowInfos.clear();
       //2004-4-23 11:06 新增 为新编码的盘点机而加 yjg
       //boolean isNew = String.valueOf(NEW_TRANSFERSCAN).equals(action);
       RowMap rowinfo = getMasterRowinfo();
       String storeid = rowinfo.get("storeid");
       String scanValue= req.getParameter("scanValue");//得到包含产品编码和批号的字符串
       String[][] s=engine.util.StringUtils.getArrays(scanValue);
       String[] cpbms = s[0];//产品编码数组
       String[] phStr   = s[1];//批号数组
       ArrayList dmsxids = new  ArrayList(cpbms.length);
       ArrayList cpids = new  ArrayList(cpbms.length);
       ArrayList phs = new  ArrayList(cpbms.length);
       LookUp prodCodeBean = LookupBeanFacade.getInstance(req,SysConstant.BEAN_PRODUCT_CODE);
       prodCodeBean.regData(cpbms);
       //取总共执行几次sql
       //int total = cpbms.length/10 + (cpbms.length%10 > 0 ? 1 : 0);
       //int i = 0;//用来记录cpids数组size实际的大小.
       for(int i=0; i<cpbms.length; i++)
       {
         String cpbm = cpbms[i].length()>6?cpbms[i].substring(0, 7):cpbms[i];//2004-4-2 23:12 修改 暂时修改取产品编码前七位 yjg
         RowMap prodCodeRow = prodCodeBean.getLookupRow(cpbm);
         String cpid = prodCodeRow.get("cpid");
         String p_storeid = prodCodeRow.get("storeid");
         if(cpid.equals("") || (!p_storeid.equals(storeid) && !p_storeid.equals("")))
           continue;
         //2004-4-23 11:06 修改 为新编码的盘点机而修改新增 yjg
         String dmsxid = "";
         boolean isNew = cpbms[i].indexOf("-") >-1;
         if(isNew)
         {
           String[] dmsxidArray = parseString(cpbms[i],"-");
           if (dmsxidArray.length < 2 )
             continue;
           dmsxid = dmsxidArray[1];
         }
         else
           dmsxid = getDmsxId(cpid, cpbms[i]);//取得代码属性id
         cpids.add(cpid);
         dmsxids.add(dmsxid);
         phs.add(phStr[i]);
       }
     String sql = combineSQL(STOCK_MATERIAL_LIST, "@", new String[]{storeid});
     dsStockMaterialDetail.setQueryString(sql);
     if(dsStockMaterialDetail.isOpen())
       dsStockMaterialDetail.refresh();
     else
       dsStockMaterialDetail.openDataSet();

     for (int i=0;i<dsStockMaterialDetail.getRowCount();i++)
     {
       String kcsl = dsStockMaterialDetail.getValue("kcsl");
       String hszl = dsStockMaterialDetail.getValue("hszl");
       dsDetailTable.insertRow(false);
       dsDetailTable.setValue("cpid", dsStockMaterialDetail.getValue("cpid"));
       dsDetailTable.setValue("ph", dsStockMaterialDetail.getValue("ph"));
       dsDetailTable.setValue("sl", kcsl);
       dsDetailTable.setValue("hssl", hszl);
       dsDetailTable.setValue("dmsxid", dsStockMaterialDetail.getValue("dmsxid"));
       dsDetailTable.post();
       dsStockMaterialDetail.next();
     }
     dsStockMaterialDetail.closeDataSet();

     EngineRow locateGoodsRow = new EngineRow(dsDetailTable, new String[]{"cpid", "ph", "dmsxid"});
     String zlCardScsl = "0";//用来保存质量合格证上的数量
     String zlCardSql = "";  //取质量合格证上的数量的sql
     for(int i=0; i<cpids.size(); i++)
     {
       String cpid = (String)cpids.get(i);
       String ph = (String)phs.get(i);
       String dmsxid = (String)dmsxids.get(i);
       String kcsl = "0";
       locateGoodsRow.setValue(0, cpid);
       locateGoodsRow.setValue(1, ph);
       locateGoodsRow.setValue(2, dmsxid);
       //如果物资明细表的数据在盘点机数据中定位不到,那么,它的数据设置为0
       if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
       {
         //kcsl = dsDetailTable.getValue("sl");
         dsDetailTable.setValue("sl", "0");//如果盘点机中的数据在库存物资明细中的定位到了那么sl设置为0
          dsDetailTable.setValue("hssl", "0");
         dsDetailTable.post();
       }
       /*else//如果盘点机中的数据没有定位到那么就插入一笔.但此时scsl去合格证上去取.
       {
         zlCardSql  = " SELECT saleNum FROM zl_certifiedCard WHERE cpid='@' and dmsxid= '@' and batNo = '@' ";
         zlCardSql  = combineSQL(zlCardSql, "@", new String[]{cpid, dmsxid, ph});
         zlCardScsl =  dataSetProvider.getSequence(zlCardSql);
         zlCardScsl = zlCardScsl==null?"0":zlCardScsl;
         dsDetailTable.insertRow(false);
         dsDetailTable.setValue("cpid", cpid);
         dsDetailTable.setValue("ph", ph);
         dsDetailTable.setValue("zcsl", "0");
         dsDetailTable.setValue("scsl", zlCardScsl);
         dsDetailTable.setValue("dmsxid", dmsxid);
         dsDetailTable.post();
       }*/
     }
     initRowInfo(false,false,true);
   }
}
/**
   * 删除库存量为空白的操作
   */
  class Delete_Blank implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      //EngineDataSet detail = getDetailTable();
      //while(detail.inBounds())
      for(int i=0; i< d_RowInfos.size(); i++)
      {
        RowMap detailrow = (RowMap)d_RowInfos.get(i);
        String sl = detailrow.get("sl");
        if(sl.equals(""))
        {
          dsDetailTable.goToRow(i);
          d_RowInfos.remove(i);
          dsDetailTable.deleteRow();
          i--;
        }
      }
    }
  }
  /**
   *
   * @param cpid
   * @param cpbm
   * @return
   * @throws Exception
   */
  private String getDmsxId(String cpid, String cpbm) throws Exception
  {
    String tempGgSx = cpbm.length()>7?cpbm.substring(7):cpbm;//2004-4-2 23:12 修改 暂时修改取产品编码前七位 yjg
    String ggsx = String.valueOf(Integer.parseInt(tempGgSx));
    String sql = "select * from kc_dmsx"
               + " where sxz like '%宽度(?)%'"
               + " and cpid = '?' and rownum<2 and isdelete = 0";
    sql = combineSQL(sql, "?", new String[]{ggsx, cpid});
    dmsxidExistData.setQueryString(sql);
    if(!dmsxidExistData.isOpen())
      dmsxidExistData.openDataSet();
    else
      dmsxidExistData.refresh();

    if (dmsxidExistData.getRowCount() == 0 )
    {
      dmsxidExistData.insertRow(false);
      String tempStr = dataSetProvider.getSequence("s_kc_dmsx");
      dmsxidExistData.setValue("dmsxid", tempStr);
      dmsxidExistData.setValue("cpid", cpid);
      dmsxidExistData.setValue("sxz", "宽度(" + ggsx + ")");
      dmsxidExistData.post();
      dmsxidExistData.saveChanges();
    }
    return dmsxidExistData.getValue("dmsxid");
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
  /**
   *2004-4-17 17:48 新增 记帐功能 yjg
   */
  class Recode_Account implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
     //是否有符合记帐的数据，有几条
     String SQL = combineSQL(RECODE_DATASQL, "?", new String[]{loginId, String.valueOf(djxz)});
     String UPDATE_SQL = combineSQL(RECODE, "?", new String[]{loginId, String.valueOf(djxz)});
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
  * 2004-5-2 19:00 明细资料数据集页面翻页功能.
  */
 class Turn_Page implements Obactioner
 {
   /**
    * 按页翻动明细数据集的数据
    */
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     //保存输入的明细信息
     isRepeat="0";
     putDetailInfo(data.getRequest());
     String temp = checkNumRigtt(min,max);
     if(temp!=null){
       isRepeat="1";//重定向，如果本页检测数据不正确的话isrepeat为1。将不翻页
       data.setMessage(temp);
       return;
     }
   }
 }
 //检验数据正确性方法
 private String checkNumRigtt(int tempmin, int tempmax) throws Exception
 {
   String temp = null;
   RowMap detailrow = null;
   //2004-4-21 14:28 为验证产品编码  批号 规格属性组合 是否有重复而设置的.
   String cpid = new String();
   String ph = new String();
   String dmsxid  = new String();
   ArrayList list = new ArrayList(d_RowInfos.size());
   StringBuffer buf = new StringBuffer();
   String combinStr = new String();
   if(d_RowInfos.size()<1)
     return showJavaScript("alert('不能保存空的数据')");
   //ArrayList list = new ArrayList(d_RowInfos.size());
   for(int i=tempmin; i<=tempmax; i++)
   {
     int row = i+1;
     detailrow = (RowMap)d_RowInfos.get(i);
     cpid = detailrow.get("cpid");
     ph = detailrow.get("ph");
     dmsxid = detailrow.get("dmsxid");
     if(cpid.equals(""))
       return showJavaScript("alert('第"+row+"行产品不能为空');");
     String wzmxid = detailrow.get("wzmxid");
     String kwid = detailrow.get("kwid");
     if(wzmxid.equals("") && childcount>0 && kwid.equals(""))
       return showJavaScript("alert('"+row+"行库位不能为空')");
     /**
      if(list.contains(cpid))
      return showJavaScript("alert('第"+row+"行产品重复');");
      else
      list.add(cpid);
      */
     String sl = detailrow.get("sl");
     String hssl = detailrow.get("hssl");
     if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
       return temp;
     if(sl.length()>0 && sl.equals("0"))
       return showJavaScript("alert('第"+row+"行数量不能为零！');");
     if((temp = checkNumber(hssl, "第"+row+"行换算数量")) != null)
       return temp;
     if(hssl.length()>0 && hssl.equals("0"))
       return showJavaScript("alert('第"+row+"行换算数量不能为零！');");
     //将每一行从页面上读取出来的此三个值相连. 02.14 yjg
     buf.append(cpid).append(",");
     buf.append(ph).append(",");
     buf.append(dmsxid);
     combinStr = buf.toString();
     // 新增 :如果cpid, ph, dmsxid的组合有相同的则是不允许的.盘点点中不允许有这样的情况. 02.14 21:19 yjg
     if(list.contains(combinStr))
       return showJavaScript("alert('第"+row+"行产品重复');");
     else
       list.add(combinStr);
     buf.delete(0, buf.length());
   }
   return null;
 }

  public long getMasterRow()
  {
    return masterRow;
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
}


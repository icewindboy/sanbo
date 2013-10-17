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
import engine.erp.store.ImportOrderGoods;
import engine.erp.store.B_SingleOrderGoods;


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
 * <p>Title: 采购入库单列表</p>
 * <p>Description: 采购入库单列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

/**
 * 2004-3-30 14:53 新增 如果是保存按钮行为完成后定位数据集 yjg
 * 2004-3-27 10:24 新增 新增删除空白行功能 yjg
 * 2004-3-27 10:24 修改 去掉当复制当前行的时候连同当前行的sl,hssl也复制过来的行为. yjg
 * 2004-3-27 10:14 修改 注释掉下面这个返回bachList()js函数的功能.
 *                     以实现保存返回功能改成保存功能的要求. yjg
 * 03:26 15:25 新增 当引入进(退)货单时同时也把结算方式带过来给采购入库单主表 yjg
 *                  同时 VW_STOREBILL_SINGLE_ORDER也加入了 jsfsid yjg
 * 03:24 10:39 拷贝当前行功能.
 * 03.19 20:34 新增 实现翻页为方便打印的类.
 * 03.16 15:25 修改 在Master_Search中去掉判断旧的SQL和新产生的SQL是否相同的if语句. yjg
 * 03.16 15:04 修改 将下面的approve.putAproveList()方法再新增一个传入参数.:dsMasterTable.getValue("deptid").
 * 以实现:mantis上库存管理中0000158bug描述的:根据下达的部门，进行提交审批；
 */

public final class B_StoreBill extends BaseAction implements Operate
{
  public  static final String  SINGLE_PRODUCT_ADD = "10811";
  public  static final String DETAIL_ORDERGOODS_ADD = "10801";
  public  static final String SINGLE_SELECT_ORDER = "11231";
  public  static final String ONCHANGE = "10601";
  public  static final String CANCEL_APPROVE = "12345";
  public  static final String STORE_ONCHANGE = "10021";//提交仓库信息
  public  static final String HSBL_ONCHANGE = "10051";//提交换算比例
  public  static final String TRANSFERSCAN = "10061";//调用盘点单
  public  static final String  SHOW_DETAIL = "12500";//02.16 15:45 新增 调用显示每一个入库单号的明细资料.在初始化添加触发事件的时候,此常量就用上了. yjg
  public  static final String REPORT = "2000";//02.23 11:26 新增 为配合小李的报表追踪调用事件而加 yjg
  public  static final String NEXT = "9999";//03.19 新增 为上一笔,下一笔打印而加的
  public  static final String PRIOR = "9998";//03.19 新增 为上一笔,下一笔打印而加的
  public  static final String COPYROW = "9997";//03.24 新增 为上一笔,下一笔打印而加的
  public  static final String DELETE_BLANK = "11581";//删除从表中库存量为空的纪录触发事件
  public  static final String RECODE_ACCOUNT = "9996";//2004-4-17 17:48 新增 记帐功能的sql yjg

  private static final String MASTER_STRUT_SQL = "SELECT * FROM kc_sfdj WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM kc_sfdj WHERE ? AND djxz=? AND fgsid=? ? ORDER BY sfrq DESC";
  private static final String TOTALZSL_SQL  = "SELECT SUM(nvl(zsl,0)) tzsl FROM kc_sfdj  WHERE ? AND djxz=? AND fgsid=? ? ORDER BY sfdjdh DESC";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM kc_sfdjmx WHERE 1<>1";
  //02.28 23:15  修改 将下面此句的sql,用了'?'来代替原来的sfdjid=.
  //解决了当sdfdjid是空的时候会页面上(主要是contract_instore_bottom.jsp上)会出现sql错误的问题.yjg
  private static final String DETAIL_SQL    = "SELECT * FROM kc_sfdjmx WHERE sfdjid='?'";//
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM kc_sfdj WHERE djxz='?' and sfdjid='?'";
  //通过进货单id得到进货单货物的数据
  private static final String BUY_ORDERGOODS_SQL = "SELECT a.djlx, a.jsfsid, b.* FROM cg_htjhd a, cg_htjhdhw b WHERE b.jhdid= ";//
  //查询数据库是否有记账的单据
  private static final String RECODE_DATASQL = " SELECT COUNT(*) FROM kc_sfdj a WHERE a.zdrid='?' AND  a.djxz ='?' AND a.zt=1";
  //把符合记帐功能的数据全部记帐
  private static final String RECODE = "UPDATE kc_sfdj a SET a.zt=2 WHERE a.zdrid='?' AND  a.djxz ='?' AND a.zt=1";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsTotalZsl     = new EngineDataSet();//统计得到总数量的和
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "kc_sfdj.1");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "kc_sfdjmx.1");
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态
  public  boolean isDetailAdd = false;   //从表是否在增加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  public  boolean isReport =false;//02.23 11:26 新增 为配合小李的报表追踪调用事件而加的这个标志 yjg

  private ImportOrderGoods buyOrderGoodsBean = null; //采购交货单和货物的bean的引用, 用于提取采购交货单编号
  private B_SingleOrderGoods orderGoodsMasterBean = null; //采购进货单主表BEAN信息

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  int djxz =1;//单据性质
  public String totalzsl = "0";

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门

  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  public String bjfs = null;//系统报价方式
  public String isHandwork = null; //是否允许手工录入采购入库单
  private User user = null;
  private String sfdjid = null;
  public static String KC_IN_SHOW_PRICE = "0";//采购入库是否显示单价金额.1=显示,0=不显示
  /**
   * 入库单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回入库单列表的实例
   */
  public static B_StoreBill getInstance(HttpServletRequest request)
  {
    B_StoreBill storeBillBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "storeBillBean";
      storeBillBean = (B_StoreBill)session.getAttribute(beanName);
      if(storeBillBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        storeBillBean = new B_StoreBill();
        storeBillBean.qtyFormat = loginBean.getQtyFormat();
        storeBillBean.priceFormat = loginBean.getPriceFormat();
        storeBillBean.sumFormat = loginBean.getSumFormat();

        KC_IN_SHOW_PRICE = loginBean.getSystemParam("KC_IN_SHOW_PRICE");//采购入库是否显示单价金额1=显示单价,0=不显示单价
        storeBillBean.fgsid = loginBean.getFirstDeptID();
        storeBillBean.loginId = loginBean.getUserID();
        storeBillBean.loginName = loginBean.getUserName();
        storeBillBean.loginDept = loginBean.getDeptID();
        storeBillBean.bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");
        storeBillBean.isHandwork = loginBean.getSystemParam("KC_HANDIN_STOCK_BILL");
        storeBillBean.user = loginBean.getUser();
        //设置格式化的字段
        storeBillBean.dsDetailTable.setColumnFormat("sl", storeBillBean.qtyFormat);
        storeBillBean.dsDetailTable.setColumnFormat("hssl", storeBillBean.qtyFormat);
        storeBillBean.dsDetailTable.setColumnFormat("dj", storeBillBean.priceFormat);
        storeBillBean.dsDetailTable.setColumnFormat("je", storeBillBean.sumFormat);
        storeBillBean.dsMasterTable.setColumnFormat("zsl", storeBillBean.qtyFormat);
        storeBillBean.dsMasterTable.setColumnFormat("zje", storeBillBean.qtyFormat);
        session.setAttribute(beanName, storeBillBean);
      }
    }
    return storeBillBean;
  }

  /**
   * 构造函数
   */
  private B_StoreBill()
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

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"sfdjdh"}, new String[]{"SELECT pck_base.billNextCode('kc_sfdj','sfdjdh','a') from dual"}));
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
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(DETAIL_ORDERGOODS_ADD), new Detail_OrderGoods_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(SINGLE_SELECT_ORDER), new Single_Select_Order());
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());
    addObactioner(String.valueOf(STORE_ONCHANGE), new Store_Onchange());
    addObactioner(String.valueOf(HSBL_ONCHANGE), new Store_Onchange());
    addObactioner(String.valueOf(SINGLE_PRODUCT_ADD), new Single_Product_Add());
    addObactioner(String.valueOf(TRANSFERSCAN), new transferScan());
    //02.16 15:45 新增 添加一个触发:当页面上点击上方框架主表中一行时的单击事件所对应的处理类. (单击主表的一行,下面就要显示出此单对应的明细)
    //即单击这个事件是须要处理的.在此处把ShowDetail类注册给这个单击事件.意即为:这个类处理单击事件 yjg
    addObactioner(SHOW_DETAIL, new Show_Detail());
    addObactioner(String.valueOf(REPORT), new Approve());//2.14 新增报表追此事件 yjg
    addObactioner(NEXT, new Move_Cursor_ForPrint());
    addObactioner(PRIOR, new Move_Cursor_ForPrint());
    addObactioner(COPYROW, new Copy_CurrentRow());
    addObactioner(DELETE_BLANK, new Delete_Blank());
    addObactioner(String.valueOf(RECODE_ACCOUNT), new Recode_Account());//套打

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
        m_RowInfo.put("khlx", "A");
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
        row.put("InternalRow", String.valueOf(dsDetail.getInternalRow()));
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
      detailRow.put("kwid", rowInfo.get("kwid_"+i));//
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//规格属性
      detailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+i), qtyFormat));//
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
      detailRow.put("ph", rowInfo.get("ph_"+i));//
      if(isHandwork.equals("1")){
      detailRow.put("dj", formatNumber(rowInfo.get("dj_"+i), priceFormat));//单价
      detailRow.put("je", formatNumber(rowInfo.get("je_"+i), sumFormat));//金额
      }
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
      String SQL = " AND zt<>2";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), String.valueOf(djxz),fgsid,SQL});
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
   *调用盘点机触发的事件
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
   *改变往来单位触发的事件
   */
  class Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      EngineDataSet ds = getMaterTable();
      RowMap rowinfo = getMasterRowinfo();
      String oldDwtxid = rowinfo.get("dwtxid");
      putDetailInfo(data.getRequest());
      String dwtxid = rowinfo.get("dwtxid");
      if(!oldDwtxid.equals(dwtxid)){
        EngineDataSet detail = getDetailTable();
        detail.first();
        while(detail.inBounds())
        {
        String wjid = detail.getValue("wjid");
        if(!wjid.equals(""))
        {
          d_RowInfos.remove(detail.getRow());
          detail.deleteRow();
        }
        else
          detail.next();
        }
      }
    else return;
    }
  }

  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove =false;
      isDetailAdd = false;
      isReport = false;//02.23 11:26 新增 为配合小李的报表追踪调用事件而加的这个标志 yjg
      isMasterAdd = String.valueOf(ADD).equals(action);
      if(!isMasterAdd)
      {
        isMasterAdd = false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        openDetailTable(false);
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
      //得到request的参数,值若为null, 则用""代替
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
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "contract_instore_list", content, dsMasterTable.getValue("deptid"));
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
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "contract_instore_list");
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
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        long internalRow = Long.parseLong(detailrow.get("InternalRow"));
        detail.goToInternalRow(internalRow);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("sfdjid", sfdjid);

        //detail.setValue("cpid", detailrow.get("cpid"));
        //double hsbl = detailrow.get("hsbl").length() > 0 ? Double.parseDouble(detailrow.get("hsbl")) : 0;//换算比例
        double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(detailrow.get("dj")) : 0;//单价
        double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;
        double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;//换算数量
        detail.setValue("sl", detailrow.get("sl"));//保存数量
        detail.setValue("hssl", detailrow.get("hssl"));//保存换算数量
        detail.setValue("dj", detailrow.get("dj"));//单价
        detail.setValue("je", String.valueOf(sl * dj));//金额
        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("kwid", detailrow.get("kwid"));
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("wjid", detailrow.get("wjid"));
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
        totalSum = totalSum.add(detail.getBigDecimal("je"));
        detail.next();
      }

      //保存主表数据
      ds.setValue("storeid", rowInfo.get("storeid"));//仓库id
      ds.setValue("jsr", rowInfo.get("jsr"));//经手人
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("jsfsid", rowInfo.get("jsfsid"));//结算方式id
      ds.setValue("sfdjlbid", rowInfo.get("sfdjlbid"));//汇率
      ds.setValue("djxz", String.valueOf(djxz));//单据性质
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));//购货单位ID
      ds.setValue("sfrq", rowInfo.get("sfrq"));//收发日期
      ds.setValue("zsl", totalNum.toString());//总数量
      ds.setValue("zje", totalSum.toString());//总金额
      ds.setValue("bz", rowInfo.get("bz"));//备注
      ds.setValue("khlx", rowInfo.get("khlx"));//客户类型
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_SALE_IN);

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      //2004-3-27 10:14 修改 注释掉下面这个返回bachList()js函数的功能.以实现保存返回功能改成保存功能的要求. yjg
      else if(String.valueOf(POST).equals(action))
      {
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
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        ph = detailrow.get("ph");
        dmsxid = detailrow.get("dmsxid");
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        String sl = detailrow.get("sl");
        String hsbl = detailrow.get("hsbl");
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
      temp = rowInfo.get("dwtxid");
      if(temp.equals(""))
      return showJavaScript("alert('请选择供货单位！');");
      temp = rowInfo.get("storeid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择仓库！');");
      temp = rowInfo.get("deptid");
     if(temp.equals(""))
        return showJavaScript("alert('请选择部门！');");
      temp = rowInfo.get("khlx");
      if(temp.equals(""))
        return showJavaScript("alert('客户类型不能为空！');");
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
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_SALE_IN);
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),String.valueOf(djxz), fgsid, SQL});
      //03.16 15:25 修改 在Master_Search中去掉判断旧的SQL和新产生的SQL是否相同的if语句. yjg
      //if(!dsMasterTable.getQueryString().equals(SQL))
      //{
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);
      //}

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
      EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("sfdjdh"), null, null, null),
        new QueryColumn(master.getColumn("sfrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("sfrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//购货单位
        new QueryColumn(master.getColumn("storeid"), null, null, null, null, "="),//仓库
        new QueryColumn(master.getColumn("sfdjlbid"), null, null, null, null, "="),//收发单据类别
        new QueryColumn(master.getColumn("jsfsid"), null, null, null, null, "="),//结算方式
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门
        new QueryColumn(master.getColumn("sfdjid"), "kc_sfdjmx", "sfdjid", "cpid", null, "="),//从表产品id
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "cpbm", "cpbm", "like"),//从表产品编码
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "product", "product", "like"),//从表产品名称
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "pm", "pm", "like"),//从表产品名称
        new QueryColumn(master.getColumn("sfdjid"), "VW_KC_SFDJQUERY", "sfdjid", "gg", "gg", "like"),//从表产品名称
        new QueryColumn(master.getColumn("jsr"), null, null, null),
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
  * 提交仓库
  */
 class Store_Onchange implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest request = data.getRequest();
     //boolean isStore = String.valueOf(STORE_ONCHANGE).endsWith(action);
     //if(isStore)
      // m_RowInfo.put(request);
     //else
       putDetailInfo(request);
   }
 }
 /**
  * 从表增加操作（单选产品）
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
     if(!isMasterAdd)
       dsMasterTable.goToInternalRow(masterRow);
     String sfdjid = dsMasterTable.getValue("sfdjid");
     dsDetailTable.goToRow(row);
     RowMap detailrow = null;
     detailrow = (RowMap)d_RowInfos.get(row);
     detailrow.put("rkdmxid", "-1");
     detailrow.put("cpid", cpid);
     detailrow.put("sfdjid", isMasterAdd ? "-1" : sfdjid);
   }
 }

 /**
 *  入库单选择进货单主表从表信息过来触发操作的类
 */

class Single_Select_Order implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest req = data.getRequest();
    //保存输入的明细信息
    putDetailInfo(data.getRequest());
    RowMap rowinfo = getMasterRowinfo();

    String singleOrderGoods = rowinfo.get("singleOrderGoods");//单选得到采购进货单id
    if(singleOrderGoods.equals(""))
      return;
    String SQL = BUY_ORDERGOODS_SQL + singleOrderGoods;
    if(bjfs.equals("1"))
      SQL = SQL + " AND nvl(b.hssl,0)>nvl(b.sjrkhsl,0)";//如果以换算单位报价SQL加个条件换算数量大于实际入库换算数量
    else
      SQL = SQL + " AND nvl(b.sl,0)>nvl(b.sjrkl,0)";//如果以计量单位报价SQL加个条件数量大于实际入库数量
    EngineDataSet buyOrderGoodsData = null;//进货单货物数据集
    if(buyOrderGoodsData==null)
    {
      buyOrderGoodsData = new EngineDataSet();
      setDataSetProperty(buyOrderGoodsData,null);
    }
    buyOrderGoodsData.setQueryString(SQL);
    if(!buyOrderGoodsData.isOpen())
      buyOrderGoodsData.open();
    else
      buyOrderGoodsData.refresh();//打开数据集
    RowMap singleRow = getOrderGoodsMasterBean(req).getLookupRow(singleOrderGoods);//采购合同主表一行信息
    rowinfo.put("storeid", singleRow.get("storeid"));//客户类型
    rowinfo.put("dwtxid", singleRow.get("dwtxid"));
    rowinfo.put("deptid", singleRow.get("deptid"));
    rowinfo.put("khlx", singleRow.get("khlx"));
    //03:26 15:25 新增 当引入进(退)货单时同时也把结算方式带过来给采购入库单主表.
    //                同时 VW_STOREBILL_SINGLE_ORDER也加入了 jsfsid yjg
    rowinfo.put("jsfsid", singleRow.get("jsfsid"));
    //实例化查找数据集的类
    EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wjid");
    if(!isMasterAdd)
      dsMasterTable.goToInternalRow(masterRow);
    String sfdjid = dsMasterTable.getValue("sfdjid");
    for(int i=0; i < buyOrderGoodsData.getRowCount(); i++)
    {
      buyOrderGoodsData.goToRow(i);
      String jhdhwid = buyOrderGoodsData.getValue("jhdhwid");
      locateGoodsRow.setValue(0, jhdhwid);
      if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
      {
        double hssl  = buyOrderGoodsData.getValue("hssl").length() > 0 ? Double.parseDouble(buyOrderGoodsData.getValue("hssl")) : 0;//换算数量
        double  sl  = buyOrderGoodsData.getValue("sl").length() > 0 ? Double.parseDouble(buyOrderGoodsData.getValue("sl")) : 0;//数量
        double sjrkhsl  = buyOrderGoodsData.getValue("sjrkhsl").length() > 0 ? Double.parseDouble(buyOrderGoodsData.getValue("sjrkhsl")) : 0;//实际入库换算数量
        double  sjrkl  = buyOrderGoodsData.getValue("sjrkl").length() > 0 ? Double.parseDouble(buyOrderGoodsData.getValue("sjrkl")) : 0;//实际数量
        //double djlx = buyOrderGoodsData.getValue("djlx").length() > 0 ? Double.parseDouble(buyOrderGoodsData.getValue("djlx")) : 0;
        double wrkhsl = hssl-sjrkhsl;//未入库换算数量
        double wrksl = sl-sjrkl;//未入库数量
        dsDetailTable.insertRow(false);
        String cpid = buyOrderGoodsData.getValue("cpid");

        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("rkdmxid", "-1");
        dsDetailTable.setValue("sfdjid", isMasterAdd ? "-1" : sfdjid);
        dsDetailTable.setValue("wjid", jhdhwid);
        dsDetailTable.setValue("cpid", cpid);
        dsDetailTable.setValue("sl", formatNumber(String.valueOf(wrksl), qtyFormat));
        dsDetailTable.setValue("hssl",formatNumber(String.valueOf(wrkhsl), qtyFormat));
        dsDetailTable.setValue("bz", buyOrderGoodsData.getValue("bz"));
        dsDetailTable.setValue("dj", buyOrderGoodsData.getValue("dj"));
        dsDetailTable.setValue("je", buyOrderGoodsData.getValue("je"));
        //03.07 14:50 新增 在界面上用引入进货单按钮引入进货单时同时也取出规格属性来. yjg
        dsDetailTable.setValue("dmsxid", buyOrderGoodsData.getValue("dmsxid"));
        dsDetailTable.post();
        //创建一个与用户相对应的行
        RowMap detailrow = new RowMap(dsDetailTable);
        detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
        d_RowInfos.add(detailrow);
      }
    }
  }
  }
  /**
   *采购入库单引入进货单货物
   */

  class Detail_OrderGoods_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());

      String importOrderGoods = m_RowInfo.get("importOrderGoods");
      if(importOrderGoods.length() == 0)
        return;

      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wjid");
      String[] jhdhwIDs = parseString(importOrderGoods,",");
      for(int i=0; i < jhdhwIDs.length; i++)
      {
        if(jhdhwIDs[i].equals("-1"))
          continue;
        if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
        String sfdjid = dsMasterTable.getValue("sfdjid");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, jhdhwIDs[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap buyOrderGoodsRow = getBuyOrderGoodsBean(req).getLookupRow(jhdhwIDs[i]);
          double wrksl = buyOrderGoodsRow.get("wrksl").length() > 0 ? Double.parseDouble(buyOrderGoodsRow.get("wrksl")) : 0;//未入库数量
          double wrkhsl = buyOrderGoodsRow.get("wrkhsl").length()>0 ? Double.parseDouble(buyOrderGoodsRow.get("wrkhsl")) : 0; //未入库换算量
          double hsbl = buyOrderGoodsRow.get("hsbl").length() > 0 ? Double.parseDouble(buyOrderGoodsRow.get("hsbl")) : 0;//换算比例
          //double djlx = buyOrderGoodsRow.get("djlx").length() >0 ? Double.parseDouble(buyOrderGoodsRow.get("djlx")) : 0;
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("rkdmxid", "-1");
          dsDetailTable.setValue("sfdjid", isMasterAdd ? "-1" : sfdjid);
          dsDetailTable.setValue("wjid", jhdhwIDs[i]);
          dsDetailTable.setValue("cpid", buyOrderGoodsRow.get("cpid"));
          dsDetailTable.setValue("sl", bjfs.equals("1") ? formatNumber(String.valueOf(hsbl==0 ? 0 : wrkhsl*hsbl), qtyFormat) : String.valueOf(wrkhsl));
          dsDetailTable.setValue("hssl",bjfs.equals("1") ? String.valueOf(wrksl) : formatNumber(String.valueOf(hsbl==0 ? 0 : wrksl/hsbl), qtyFormat));
          dsDetailTable.setValue("bz", buyOrderGoodsRow.get("bz"));
          dsDetailTable.setValue("dj", buyOrderGoodsRow.get("dj"));
          dsDetailTable.setValue("je", buyOrderGoodsRow.get("je"));
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
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
      RowMap detailrow = (RowMap)d_RowInfos.get(rownum);
      long l_row = Long.parseLong(detailrow.get("InternalRow"));
      ds.goToInternalRow(l_row);
      //删除临时数组的一列数据
      d_RowInfos.remove(rownum);
      ds.deleteRow();
    }
  }

  //02.16 16:23 新增 新增一个当页面上单击事件发生时想要查看明细资料 这个单击事件的Show_Detail类 . yjg
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
   * 得到用于查找进货单编号的bean
   * @param req WEB的请求
   * @return 返回用于查找进货单编号的bean
   */
  public ImportOrderGoods getBuyOrderGoodsBean(HttpServletRequest req)
  {
    if(buyOrderGoodsBean == null)
      buyOrderGoodsBean = ImportOrderGoods.getInstance(req);
    return buyOrderGoodsBean;
  }
  /**
   * 得到进货单一行主表信息的bean
   * @param req WEB的请求
   * @return 返回进货单一行主表信息的bean
   */
  public B_SingleOrderGoods getOrderGoodsMasterBean(HttpServletRequest req)
  {
    if(orderGoodsMasterBean == null)
      orderGoodsMasterBean = B_SingleOrderGoods.getInstance(req);
    return orderGoodsMasterBean;
  }
  /**
   * 03.19 20:34 新增 实现翻页为方便打印的类.
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
   * 03:24 10:39 拷贝当前行功能.
   */
  class Copy_CurrentRow implements Obactioner
  {
    /**
     * 取得网页上明细资料表格中当前行的资料,然后再复制指定数量的此行的资料.加入到网页数据集中,显示出来在网页上.
     *  1. 从request中得到页面上当前光标所停留在的行数是下方明细表格中的那一行.设为i行.
     *  2. 接着要从保存网页资料的RowMap集中取出此i行的数据.
     *  3. 将此i行的数据加入到保存网页资料的RowMap集中,连续加入指定数量次.
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //取得网页上光标所停留在的那一行的行数.
      int currentRow = Integer.parseInt(data.getParameter("rownum"));
      //取得使用复制功能的时候,默认一次复制几笔. yjg
      int tCopyNumber = Integer.parseInt(data.getParameter("tCopyNumber"));
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      RowMap detailRow = (RowMap)d_RowInfos.get(currentRow);
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String sfdjid = dsMasterTable.getValue("sfdjid");
      RowMap rowinfo = null;
     long swapdetailRow = -1;
     for(int i=0; i<d_RowInfos.size(); i++)
     {
       rowinfo = (RowMap)d_RowInfos.get(i);
       swapdetailRow = Long.parseLong(rowinfo.get("InternalRow"));
       //2004-4-30 12:52  新增 以改正:复制功能复制的时候,除页面上最后一笔以外的数据都不能保留在复制前输入的数量(还未保存)值. yjg
       detail.goToInternalRow(swapdetailRow);
       //double hsbl = rowinfo.get("hsbl").length() > 0 ? Double.parseDouble(rowinfo.get("hsbl")) : 0;//换算比例
       double sl = rowinfo.get("sl").length() > 0 ? Double.parseDouble(rowinfo.get("sl")) : 0;
       double hssl = rowinfo.get("hssl").length() > 0 ? Double.parseDouble(rowinfo.get("hssl")) : 0;//换算数量
       double dj = rowinfo.get("dj").length() > 0 ? Double.parseDouble(rowinfo.get("dj")) : 0;//单价

       detail.setValue("cpid", rowinfo.get("cpid"));
       detail.setValue("sl", rowinfo.get("sl"));//保存数量
       //直接取页面上的hssl值
       detail.setValue("hssl", String.valueOf(hssl));//保存换算数量
       detail.setValue("dj", String.valueOf("dj"));//单价
       detail.setValue("kwid", String.valueOf(sl * dj));//金额
       detail.setValue("dmsxid", rowinfo.get("dmsxid"));
       detail.setValue("wjid", rowinfo.get("wjid"));
       detail.setValue("djxz", String.valueOf(djxz));
       detail.setValue("ph", rowinfo.get("ph"));
       detail.setValue("bz", rowinfo.get("bz"));//备注
       detail.setValue("fgsid", fgsid);
       detail.post();
      }

      for (int i =0; i<tCopyNumber; i++)
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("rkdmxid", "-1");
        dsDetailTable.setValue("sfdjid", isMasterAdd ? "-1" : sfdjid);
        dsDetailTable.setValue("wjid", detailRow.get("wjid"));
        dsDetailTable.setValue("cpid", detailRow.get("cpid"));
        //2004-3-27 10:24 修改 去掉当复制当前行的时候连同当前行的sl,hssl也复制过来的行为. yjg
        //dsDetailTable.setValue("sl", detailRow.get("sl"));
        //dsDetailTable.setValue("hssl",detailRow.get("hssl"));
        dsDetailTable.setValue("bz", detailRow.get("bz"));
        dsDetailTable.setValue("dj", detailRow.get("dj"));
        dsDetailTable.setValue("je", detailRow.get("je"));
        dsDetailTable.setValue("ph", detailRow.get("ph"));
        dsDetailTable.setValue("kwid", detailRow.get("kwid"));
        //03.07 14:50 新增 在界面上用引入进货单按钮引入进货单时同时也取出规格属性来. yjg
        dsDetailTable.setValue("dmsxid", detailRow.get("dmsxid"));
        dsDetailTable.post();
        //创建一个与用户相对应的行
        //RowMap detailrow = new RowMap(dsDetailTable);
        //d_RowInfos.add(detailrow);
      }
      initRowInfo(false, false, true);
    }
  }
  /**
   * 2004-3-27 10:24 新增 新增删除空白行功能 yjg
   * 删除数量为空白的操作
   */
  class Delete_Blank implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      //EngineDataSet detail = getDetailTable();
      //while(detail.inBounds())
      for(int i=0; i< d_RowInfos.size(); i++)
      {
        RowMap detailrow = (RowMap)d_RowInfos.get(i);
        long internalRow = Long.parseLong(detailrow.get("InternalRow"));
        detail.goToInternalRow(internalRow);
        String sl = detailrow.get("sl");
        //02.25 11:14 修改 修改删除一行库存量为空的一笔记录的条件改为 电脑库存数量, 实际库存数量全部都是零的才删除 yjg
        //03.23 11:36 修改 将上方的02.25 11:14 bug中所做的修改的基础上再修改:现在又改回了:
        //                删除一行库存量为空的一笔记录的条件改为:实际库存数量是空的. yjg
        if(sl.equals(""))
        {
          d_RowInfos.remove(i);
          dsDetailTable.deleteRow();
          i--;
        }
      }
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




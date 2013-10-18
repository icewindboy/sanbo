package engine.erp.subsection;//得到包

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
import engine.erp.buy.Import_MrpGoods;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/*************************************************
 * <p>Title: 办事处申请单</p>
 * <p>Description: 办事处申请单</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 王林川
 *************************************************/

public final class B_DirectApply extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";//单击显示框架下方从表信息事件
  public static final String COMPLETE = "1002";//完成操作
  public static final String AUTO_MIN_PRICE = "1020";
  public static final String DETAIL_IMPORT_MRP = "1080";
  public static final String CANCLE_APPROVE = "1090";//取消审批
  public static final String SINGLE_IMPORT_ADD = "1191";//选择物料需求计划主表，引入全部从表信息操作类
  public  static final String WB_ONCHANGE = "10031";//选择外币类别触发事件
  public  static final String DEPT_CHANGE = "10032";//提交选者部门


  private static final String MASTER_STRUT_SQL = "SELECT * FROM fb_sqd WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM fb_sqd WHERE ? AND fgsid=? ? ORDER BY sqbh DESC";
  //private static final String Quality_SQL = "SELECT * FROM cw_cgyfk WHERE ? ? ORDER BY yfkbh DESC";//
  private static final String DETAIL_SQL    = "SELECT a.* FROM fb_sqdhw a ,kc_dm b where a.cpid=b.cpid and a.sqdid='?' order by b.cpbm";//
  private static final String DETAIL_STRUCYT_SQL    = "SELECT a.* FROM fb_sqdhw a ,kc_dm b where 1<>1 order by b.cpbm";//
  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM fb_sqd WHERE sqdid='?'";
  //提取物料需求计划明细的SQL语句
  private static final String MRP_DETAIL_SQL = "SELECT * FROM sc_wlxqjhmx WHERE nvl(xgl,0)>nvl(ygl,0) AND chxz=2 AND wlxqjhid= ";
  /**
   * 提取采购报价最低价的数据
   */
  private static final String BUY_PRICE_SQL = "SELECT b.cpid, b.dwtxid, b.bj FROM cg_bj b WHERE b.cgbjid = pck_store.getMinBuyPriceID(?) ";
      //"CALL pck_store.getMinBuyPriceData(?, @)";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsBuyPrice = null;
  private LookUp foreignBean = null; //外币信息的bean的引用

  //public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable,"fb_sqd");//
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable,"fb_sqd","fb_sqd");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable,"fb_sqdhw");
  public  boolean isApprove = false;     //是否在审批状态
  private boolean isMasterAdd = true;    //是否在添加状态
  public boolean isDetailAdd = false;   // 从表是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private LookUp b_DirectApplyBean = null; //采购报价的bean的引用, 用于提取采购报价
  public Import_MrpGoods mrpGoodsBean = null;//
  private LookUp productBean = null; //产品信息的bean的引用, 用于提取产品信息

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  public String bjfs = "";   //报价方式
  private User user = null;//new一个登录员工的对象包含部门权限
  private String sqdid= null;
//  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  /**
   * 采购申请单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_DirectApply getInstance(HttpServletRequest request)
  {
    B_DirectApply b_DirectApplyBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_DirectApplyBean";
      b_DirectApplyBean = (B_DirectApply)session.getAttribute(beanName);
      if(b_DirectApplyBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        b_DirectApplyBean = new B_DirectApply();
        b_DirectApplyBean.qtyFormat = loginBean.getQtyFormat();
        b_DirectApplyBean.priceFormat = loginBean.getPriceFormat();
        b_DirectApplyBean.sumFormat = loginBean.getSumFormat();

        b_DirectApplyBean.fgsid = loginBean.getFirstDeptID();
        b_DirectApplyBean.loginId = loginBean.getUserID();
        b_DirectApplyBean.loginName = loginBean.getUserName();
        b_DirectApplyBean.loginDept = loginBean.getDeptID();
        b_DirectApplyBean.bjfs = "0";//loginBean.getSystemParam("BUY_PRICLE_METHOD");
//        b_DirectApplyBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        b_DirectApplyBean.user = loginBean.getUser();
        //设置格式化的字段
        b_DirectApplyBean.dsDetailTable.setColumnFormat("sl", b_DirectApplyBean.qtyFormat);
        b_DirectApplyBean.dsDetailTable.setColumnFormat("hssl", b_DirectApplyBean.qtyFormat);
        b_DirectApplyBean.dsDetailTable.setColumnFormat("dj", b_DirectApplyBean.priceFormat);
        b_DirectApplyBean.dsDetailTable.setColumnFormat("je", b_DirectApplyBean.priceFormat);
        b_DirectApplyBean.dsMasterTable.setColumnFormat("zsl", b_DirectApplyBean.sumFormat);
        b_DirectApplyBean.dsMasterTable.setColumnFormat("zje", b_DirectApplyBean.sumFormat);
        session.setAttribute(beanName, b_DirectApplyBean);
      }
    }
    return b_DirectApplyBean;
  }

  /**
   * 构造函数
   */
  private B_DirectApply()
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
    setDataSetProperty(dsDetailTable, DETAIL_STRUCYT_SQL);
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"sqbh"}, new String[]{"SELECT pck_base.billNextCode('fb_sqd','sqbh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"sqbh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"sqdhwid"}, new String[]{"s_fb_sqdhw"}));
    dsDetailTable.setTableName("fb_sqdhw");
    //dsDetailTable.setSort(new SortDescriptor("", new String[]{"cpbm"}, new boolean[]{false}, null, 0));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_IMPORT_MRP), new Detail_Import_Mrp());//引入物料需求计划明细
    addObactioner(String.valueOf(SINGLE_IMPORT_ADD), new Single_Import_Add());//引入物料需求计划主表，从表数据全部过来
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(DEPT_CHANGE), new DeptChange());//部门变动触发操作
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCLE_APPROVE), new Cancle_Approve());
    addObactioner(String.valueOf(AUTO_MIN_PRICE), new Master_Buy_Price());//填入最低报价
    addObactioner(String.valueOf(COMPLETE), new Complete());//完成操作
    addObactioner(String.valueOf(SHOW_DETAIL), new ShowDetail());
    addObactioner(String.valueOf(WB_ONCHANGE), new Wb_Onchange());//选择外币触发事件
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
   * 初始化行信息
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
        m_RowInfo.put("czrq", today);//制单日期
        m_RowInfo.put("czy", loginName);//操作员
        m_RowInfo.put("sqrq", today);//申请日期
        m_RowInfo.put("czyid", loginId);//操作员ID
        m_RowInfo.put("deptid", loginDept);//部门
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
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//数量
      detailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+i), qtyFormat));//换算数量
      detailRow.put("dj", formatNumber(rowInfo.get("dj_"+i), priceFormat));//单价
      detailRow.put("je", formatNumber(rowInfo.get("je_"+i), sumFormat));//金额
      //detailRow.put("ybje", formatNumber(rowInfo.get("ybje_"+i), sumFormat));//原币金额
      detailRow.put("dwtxid", rowInfo.get("dwtxid_"+i));//单位通讯
      detailRow.put("xqrq", rowInfo.get("xqrq_"+i));//需求日期
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
       detailRow.put("yt", rowInfo.get("yt_"+i));//用途
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//代码属性ID
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
    sqdid = dsMasterTable.getValue("sqdid");
    String SQL = isMasterAdd ? "-1" : sqdid;
    SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});
    dsDetailTable.setQueryString(SQL);

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

  //重载getDetailRowinfos. 为了在bottom页面上取得cpid.
  public final RowMap[] getDetailRowinfos(String cpid) {
  RowMap[] rows = new RowMap[d_RowInfos.size()];
  d_RowInfos.toArray(rows);
  return rows;
  }


  //得到一行信息
  public final RowMap getLookupRow(String sqdhwid)
  {
    RowMap row = new RowMap();
    if(sqdhwid == null || sqdhwid.equals(""))
      return row;//返回
    EngineRow locateRow = new EngineRow(dsDetailTable, "sqdhwid");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
    if(locateRow == null)
      locateRow = new EngineRow(getDetailTable(), "sqdhwid");
    locateRow.setValue(0, sqdhwid);
    if(getDetailTable().locate(locateRow, Locate.FIRST))
      row.put(getDetailTable());
    return row;
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
      isDetailAdd =false;
      isMasterAdd = true;
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
      row.put("sqrq$a", startDay);
      row.put("sqrq$b", today);
      //初始化时清空数据集
      String SQL = " AND zt<>8";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
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
      isApprove = false;
      isDetailAdd = false;
      isMasterAdd = String.valueOf(ADD).equals(action);
      if(!isMasterAdd)
      {
        isMasterAdd=false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
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
      String deptid = dsMasterTable.getValue("deptid");
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterTable.getValue("sqbh");
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "direct_apply", content,deptid);
    }
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
   * 完成操作出发类
   * 手工完成操作，没有约束
   */
  class Complete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getMaterTable();
      int row = Integer.parseInt(data.getParameter("rownum"));
      ds.goToRow(row);
      ds.setValue("zt", "8");
      ds.post();
      ds.saveChanges();

    }
  }
  /**
   *选择外币触发事件
   */
  class Wb_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());

      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      String wbid = rowInfo.get("wbid");
      RowMap foreignRow = getForeignBean(req).getLookupRow(wbid);
      String hl = foreignRow.get("hl");
      rowInfo.put("wbid",wbid);
      rowInfo.put("hl",hl);
      double curhl = hl.length()>0 ? Double.parseDouble(hl) : 0 ;
      for(int j=0; j<d_RowInfos.size(); j++)
      {
        RowMap detailrow = (RowMap)d_RowInfos.get(j);
        String je = detailrow.get("je");
        double curje = isDouble(je) ? Double.parseDouble(je) : 0 ;
        // detailrow.put("ybje", formatNumber(curhl==0 ? "" : String.valueOf(curje/curhl),qtyFormat));
      }
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
     approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "buy_apply_list");
   }
 }

 /**
 *改变车间触发的事件
 */
class DeptChange implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest req = data.getRequest();
    m_RowInfo.put(req);
    boolean isDept = String.valueOf(DEPT_CHANGE).equals(action);
    if(!isDept)
    {
      dsDetailTable.deleteAllRows();
      d_RowInfos.clear();
    }
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
      String sqdid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        sqdid = dataSetProvider.getSequence("s_fb_sqd");
        ds.setValue("sqdid", sqdid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("czyid", loginId);
        ds.setValue("czy", loginName);//操作员
      }
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);
      //EngineDataSet detail = getDetailTable();
      dsDetailTable.first();
      for(int i=0; i<dsDetailTable.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          dsDetailTable.setValue("sqdid", sqdid);
        double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(detailrow.get("dj")) : 0;//单价
        double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;//产品数量
        double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;//产品数量
        double je = bjfs.equals("0")?sl*dj:hssl*dj;
        dsDetailTable.setValue("dj", detailrow.get("dj"));//
        dsDetailTable.setValue("sl", detailrow.get("sl"));
        dsDetailTable.setValue("hssl", detailrow.get("hssl"));
        dsDetailTable.setValue("cpid", detailrow.get("cpid"));
        String dwtxid = detailrow.get("dwtxid");
        dsDetailTable.setValue("dwtxid", dwtxid);
        dsDetailTable.setValue("je", String.valueOf(je));
        dsDetailTable.setValue("xqrq", detailrow.get("xqrq"));//需求日期
        dsDetailTable.setValue("bz", detailrow.get("bz"));//备注
        dsDetailTable.setValue("yt", detailrow.get("yt"));//用途
        dsDetailTable.setValue("dmsxid", detailrow.get("dmsxid"));
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          dsDetailTable.setValue(fieldCode, detailrow.get(fieldCode));
        }
        dsDetailTable.post();
        String s= dsDetailTable.getValue("je");
        totalNum = totalNum.add(dsDetailTable.getBigDecimal("sl"));
        totalSum = totalSum.add(dsDetailTable.getBigDecimal("je"));
        dsDetailTable.next();
      }

      //保存主表数据
      //ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      String sqrq = rowInfo.get("sqrq");
      ds.setValue("deptid", rowInfo.get("deptid"));//部门ID
      ds.setValue("qgr", rowInfo.get("qgr"));//请购人
      ds.setValue("djlx", rowInfo.get("djlx"));//单据类型，1为调拨申请，-1为调回申请
      ds.setValue("sqrq", rowInfo.get("sqrq"));//申请日期
      ds.setValue("cgyy", rowInfo.get("cgyy"));//采购原因
      ds.setValue("zsl", totalNum.toString());//总数量
      ds.setValue("zje", totalSum.toString());//总金额
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        dsDetailTable.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        dsDetailTable.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action))
        isMasterAdd = false;
        initRowInfo(true, false, true);
        initRowInfo(false, false, true);//重新初始化从表的各行信息

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
        return showJavaScript("alert('不能保存空数据')");
      java.sql.Date sqrq = java.sql.Date.valueOf(getMasterRowinfo().get("sqrq"));
      ArrayList list = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        String cpid = detailrow.get("cpid");
        String dmsxid = detailrow.get("dmsxid");
        String dwtxid = detailrow.get("dwtxid");
        String wlxqjhmxid = detailrow.get("wlxqjhmxid");
        StringBuffer buf = new StringBuffer().append(wlxqjhmxid).append(",").append(cpid).append(",").append(dmsxid).append(",").append(dwtxid);
        String cpiddmsxid = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(cpiddmsxid))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(cpiddmsxid);
        String sl = detailrow.get("sl");
        if(sl.equals(""))
          return showJavaScript("alert('第"+row+"行数量不能为空！');");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");

        String dj = detailrow.get("dj");
        if(dj.length()>0 && (temp = checkNumber(dj, "第"+row+"行单价")) != null)
          return temp;
        temp = detailrow.get("xqrq");
        if(temp.equals(""))
          return showJavaScript("alert('第"+row+"行需求日期不能为空！');");
        if(!isDate(temp))
          return showJavaScript("alert('第"+row+"行非法需求日期！');");
        java.sql.Date xqrq = java.sql.Date.valueOf(temp);
        //if(xqrq.before(sqrq))
          //return showJavaScript("alert('第"+row+"行需求日期不能小于申请日期！');");
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
      String temp = rowInfo.get("sqrq");
      if(temp.equals(""))
        return showJavaScript("alert('申请日期不能为空！');");
      if(!isDate(temp))
        return showJavaScript("alert('非法申请日期！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('部门不能为空！');");
      temp = rowInfo.get("qgr");
       if(temp.equals(""))
        return showJavaScript("alert('请购人不能为空！');");
      temp = rowInfo.get("djlx");
       if(temp.equals(""))
        return showJavaScript("alert('单据类型不能为空！');");
      temp = rowInfo.get("cgyy");
      if(temp.getBytes().length > getMaterTable().getColumn("cgyy").getPrecision())
        return showJavaScript("alert('您输入的采购原因的内容太长了！');");
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
      //
      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }
  class Search implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<cardID >notifyObactioners</cardID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      masterProducer.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = masterProducer.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
        SQL = " and "+SQL;
      SQL=combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      System.out.print(SQL);
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
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
        new QueryColumn(master.getColumn("sqbh"), null, null, null),
        new QueryColumn(master.getColumn("sqrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("sqrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("sqdid"), "fb_sqdhw", "sqdid", "cpid", null, "="),//从表产品id
        new QueryColumn(master.getColumn("sqdid"), "VW_FB_SQDHW", "sqdid", "cpbm", "cpbm", "left_like"),//从表品名规格
        new QueryColumn(master.getColumn("sqdid"), "VW_FB_SQDHW", "sqdid", "product", "product", "like"),//从表产品编码
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("sqdid"), "VW_FB_SQDHW", "sqdid", "sxz", "sxz", "like")
      });
      isInitQuery = true;
    }
  }
  /**
  *  根据物料需求计划明细从表增加操作
  */

 class Detail_Import_Mrp implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     //保存输入的明细信息
     putDetailInfo(data.getRequest());
     RowMap rowInfo = getMasterRowinfo();

     String mutiimportmrp = m_RowInfo.get("mutiimportmrp");
     if(mutiimportmrp.length() == 0)
       return;

     //实例化查找数据集的类
     EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wlxqjhmxid");
     String[] wlxqjhmxID = parseString(mutiimportmrp,",");
     if(!isMasterAdd)
       dsMasterTable.goToInternalRow(masterRow);
     String sqdid = dsMasterTable.getValue("sqdid");
     for(int i=0; i < wlxqjhmxID.length; i++)
     {
       if(wlxqjhmxID[i].equals("-1"))
         continue;
       RowMap detailrow = null;
       locateGoodsRow.setValue(0, wlxqjhmxID[i]);
       if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
       {
         RowMap importMrpGoodsRow = getMrpGoodsBean(req).getLookupRow(wlxqjhmxID[i]);
         String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
         double wgl = importMrpGoodsRow.get("wgl").length()>0 ? Double.parseDouble(importMrpGoodsRow.get("wgl")) : 0;//未采购的数量
         double hsbl = importMrpGoodsRow.get("hsbl").length() > 0 ? Double.parseDouble(importMrpGoodsRow.get("hsbl")) : 0;//换算比例
         String xqrq = importMrpGoodsRow.get("xqrq").equals("") ? today : importMrpGoodsRow.get("xqrq");
         dsDetailTable.insertRow(false);
         dsDetailTable.setValue("sqdhwid", "-1");
         dsDetailTable.setValue("wlxqjhmxid",wlxqjhmxID[i]);
         dsDetailTable.setValue("cpid", importMrpGoodsRow.get("cpid"));
        // dsDetailTable.setValue("sl", bjfs.equals("1") ? String.valueOf(hsbl==0 ? 0 : wgl/hsbl) : importMrpGoodsRow.get("wgl"));
         dsDetailTable.setValue("hssl", String.valueOf(hsbl==0 ? wgl : wgl/hsbl));
         dsDetailTable.setValue("sl",  importMrpGoodsRow.get("wgl"));
         dsDetailTable.setValue("xqrq", xqrq);
         dsDetailTable.setValue("bz", importMrpGoodsRow.get("bz"));
         dsDetailTable.setValue("dmsxid", importMrpGoodsRow.get("dmsxid"));
         dsDetailTable.setValue("sqdid", isMasterAdd ? "" : sqdid);
         dsDetailTable.post();
         //创建一个与用户相对应的行
         detailrow = new RowMap(dsDetailTable);
         d_RowInfos.add(detailrow);
       }
     }
   }
  }
  /**
   *  选择物料需求计划主表从表数据全部引入操作
   */

  class Single_Import_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String singleImportMrp = m_RowInfo.get("singleImportMrp");
      if(singleImportMrp.equals(""))
        return;
      String SQL = MRP_DETAIL_SQL+singleImportMrp;
      EngineDataSet tempMrpData = null;
      if(tempMrpData==null)
      {
        tempMrpData = new EngineDataSet();
        setDataSetProperty(tempMrpData,null);
      }
      tempMrpData.setQueryString(SQL);
      if(!tempMrpData.isOpen())
        tempMrpData.openDataSet();
      else
        tempMrpData.refresh();
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wlxqjhmxid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String sqdid = dsMasterTable.getValue("sqdid");
      for(int i=0; i<tempMrpData.getRowCount(); i++)
      {
        tempMrpData.goToRow(i);
        if(!tempMrpData.getValue("chxz").equals("2"))
          continue;
        String wlxqjhmxid = tempMrpData.getValue("wlxqjhmxid");
        String cpid = tempMrpData.getValue("cpid");
        locateGoodsRow.setValue(0, wlxqjhmxid);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap prodRow = getProductBean(req).getLookupRow(cpid);
          String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
          double xgl = tempMrpData.getValue("xgl").length()>0 ? Double.parseDouble(tempMrpData.getValue("xgl")) : 0;//需求计划中需要采购的数量
          double hsbl = prodRow.get("hsbl").length() > 0 ? Double.parseDouble(prodRow.get("hsbl")) : 0;//换算比例
          double ygl = tempMrpData.getValue("ygl").length()>0 ? Double.parseDouble(tempMrpData.getValue("ygl")) : 0;//需求计划中已经采购的数量
          double wgl = xgl-ygl>0 ? xgl-ygl : 0;
          if(wgl==0)
            continue;
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("sqdhwid", "-1");
          dsDetailTable.setValue("wlxqjhmxid",wlxqjhmxid);
          dsDetailTable.setValue("cpid", cpid);
          //dsDetailTable.setValue("sl", bjfs.equals("1") ? String.valueOf(hsbl==0 ? 0 : wgl/hsbl) : String.valueOf(wgl));
          dsDetailTable.setValue("hssl",String.valueOf(hsbl==0 ? wgl : wgl/hsbl));
          dsDetailTable.setValue("sl",String.valueOf(wgl));
          dsDetailTable.setValue("xqrq", tempMrpData.getValue("xqrq").equals("") ? today : tempMrpData.getValue("xqrq"));
          dsDetailTable.setValue("bz", tempMrpData.getValue("bz"));
          dsDetailTable.setValue("dmsxid", tempMrpData.getValue("dmsxid"));
          dsDetailTable.setValue("sqdid", isMasterAdd ? "" : sqdid);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          RowMap detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
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
      String sqdid = dsMasterTable.getValue("sqdid");
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      detail.insertRow(false);
      detail.setValue("sqdid", isMasterAdd ? "-1" : sqdid);
      detail.setValue("xqrq", today);
      detail.post();
      RowMap detailrow = new RowMap(detail);
      d_RowInfos.add(detailrow);
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
   * 显示从表的列表信息
   */
  class ShowDetail implements Obactioner
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
   *  自动填入最低供应及报价
   */
  class Master_Buy_Price implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      if(dsBuyPrice == null)
      {
        dsBuyPrice = new EngineDataSet();
        setDataSetProperty(dsBuyPrice, null);
      }

      //EngineRow priceRow = new EngineRow(dsBuyPrice, "cpid");
      RowMap detail = null;
      boolean isCanRework = true; //在取消审批实从表一行纪录是否被合同引用
      String zt = m_RowInfo.get("zt");
      String hl = m_RowInfo.get("hl");
      double d_hl = hl.length()>0 ? Double.parseDouble(hl) : 0;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detail = (RowMap)d_RowInfos.get(i);
        if(zt.equals("0"))
          isCanRework = engine.erp.baseinfo.BasePublicClass.isRevamp(dsDetailTable, "skhtl", i);
        if(!isCanRework)
          continue;//如果该纪录被合同引用供应商将不能改
        String SQL = combineSQL(BUY_PRICE_SQL, "?", new String[]{detail.get("cpid")});
        dsBuyPrice.setQueryString(SQL);
        if(dsBuyPrice.isOpen())
          dsBuyPrice.refresh();
        else
          dsBuyPrice.open();

        String dwtxid = dsBuyPrice.getValue("dwtxid");
        String dj = dsBuyPrice.getValue("bj");
        detail.put("dwtxid", dwtxid);
        detail.put("dj", dj);
        double sl = detail.get("sl").length()>0 ? Double.parseDouble(detail.get("sl")) : 0;
        double djVal = dj.length()>0 ? Double.parseDouble(dj) : 0;
        //detail.put("je",formatNumber(String.valueOf(sl*djVal), sumFormat));
        //if(d_hl!=0)
        // detail.put("ybje", formatNumber(String.valueOf(sl*djVal/d_hl), sumFormat));
      }
      /**
       dsDetailTable.first();
       for(int i=0; i<dsDetailTable.getRowCount(); i++)
       {
       String SQL = combineSQL(BUY_PRICE_SQL, "?", new String[]{dsDetailTable.getValue("cpid")});
       dsBuyPrice.setQueryString(SQL);
       if(dsBuyPrice.isOpen())
          dsBuyPrice.refresh();
        else
          dsBuyPrice.open();

        String dwtxid = dsBuyPrice.getValue("dwtxid");
        String dj = dsBuyPrice.getValue("bj");
        dsDetailTable.setValue("dwtxid", dwtxid);
        dsDetailTable.setValue("dj", dj);
        detail = (RowMap)d_RowInfos.get(dsDetailTable.getRow());
        double sl = detail.get("sl").length()>0 ? Double.parseDouble(detail.get("sl")) : 0;
        double djVal = dj.length()>0 ? Double.parseDouble(dj) : 0;
        dsDetailTable.setValue("je",String.valueOf(sl*djVal));
        dsDetailTable.post();
        detail.put("dwtxid", dwtxid);
        detail.put("dj", dj);
        detail.put("je", formatNumber(String.valueOf(sl*djVal), sumFormat));
        // }
        dsDetailTable.next();
      }
      dsDetailTable.post();
      */
    }
  }
  /**
     * 得到用于查找物料需求计划信息的bean
     * @param req WEB的请求
     * @return 返回用于查找物料需求计划信息的bean
     */
    public Import_MrpGoods getMrpGoodsBean(HttpServletRequest req)
    {
      if(mrpGoodsBean == null)
        mrpGoodsBean = Import_MrpGoods.getInstance(req);
      return mrpGoodsBean;
    }
    /**
     * 得到用于查找产品单价的bean
     * @param req WEB的请求
     * @return 返回用于查找产品单价的bean
     */
    public LookUp getProductBean(HttpServletRequest req)
    {
      if(productBean == null)
        productBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PRODUCT);
      return productBean;
    }
    /**
     * 得到外币信息的bean
     * @param req WEB的请求
     * @return 返回外币信息bean
     */
    public LookUp getForeignBean(HttpServletRequest req)
    {
      if(foreignBean == null)
        foreignBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_FOREIGN_CURRENCY);
      return foreignBean;
    }
}


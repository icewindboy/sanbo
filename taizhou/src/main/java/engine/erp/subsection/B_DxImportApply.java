package engine.erp.subsection;

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

import com.borland.dx.dataset.*;
/**
 *提货单引入合同
 * */
public final class B_DxImportApply extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "9001";

  private static final String MASTER_STRUT_SQL = "SELECT  * FROM fb_sqd WHERE 1<>1 ";
  private static final String MASTER_SQL = "SELECT  * FROM fb_sqd WHERE sqdid in(SELECT DISTINCT sqdid FROM VW_FB_SQD WHERE 1=1 AND ? AND fgsid=? ? ) ";
  //"SELECT * FROM VW_FB_SQD WHERE zt=1 and abs(nvl(skdsl,0))<abs(nvl(sl,0)) AND ? AND fgsid=? ? ";


  /*这样会出现引合同的次数超过一次时，无论是否引用完，都会出现在top页面上，需要在top页面上使用lookupBean，engine.project.LookUp ykslBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TDHW_YKTDSL); //已开提单数量 2004.12.03 wlc
      = "SELECT * FROM fb_sqd WHERE sqdid IN ( "
      + " SELECT b.sqdid FROM fb_sqd a, fb_sqdhw b, xs_tdhw c, vw_xs_wzdj d "
      + " WHERE a.sqdid=b.sqdid AND b.sqdhwid=c.sqdhwid(+) AND b.wzdjid=d.wzdjid "
      + " AND a.zt=1 AND ? AND a.fgsid=? ? "
      + " GROUP BY b.sqdid, b.sl "
      + " HAVING abs(SUM(nvl(c.sl,0)))<abs(SUM(nvl(b.sl,0))) "
      + ") ORDER BY htbh DESC";
      //= "SELECT * from fb_sqd WHERE 1=1 ? ORDER BY htbh DESC";
*/
  private static final String DETAIL_STRUT_SQL
      = "SELECT * FROM fb_sqdhw WHERE 1<>1";
  private static final String DETAIL_SQL
      = "select * From VW_FB_SQD  where  1=1 and sqdid= ";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsSearchTable  = new EngineDataSet();//

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable,"fb_sqd","fb_sqd");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "fb_sqdhw");

  private boolean isMasterAdd = true;    //是否在添加状态

  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private LookUp salePriceBean = null; //销售单价的bean的引用, 用于提取销售单价
  //private B_SaleGoodsSelect saleGoodsBean=null;
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public String srcFrm = "";
  public String multiIdInput = "";
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID

  private String sqdid = null;
  private User user = null;
  private String dwtxid ="";
  private String djlx = "";
  private String storeid = "";
  private String personid ="";

  private String khlx = "";
  private String jsfsid = "";
  private String sendmodeid = "";
  private String yfdj = "";
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_DxImportApply getInstance(HttpServletRequest request)
  {
    B_DxImportApply b_DxImportApplyBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_DxImportApplyBean";
      b_DxImportApplyBean = (B_DxImportApply)session.getAttribute(beanName);
      if(b_DxImportApplyBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        b_DxImportApplyBean = new B_DxImportApply();
        b_DxImportApplyBean.qtyFormat = loginBean.getQtyFormat();
        b_DxImportApplyBean.priceFormat = loginBean.getPriceFormat();
        b_DxImportApplyBean.sumFormat = loginBean.getSumFormat();

        b_DxImportApplyBean.fgsid = loginBean.getFirstDeptID();
        b_DxImportApplyBean.loginId = loginBean.getUserID();
        b_DxImportApplyBean.loginName = loginBean.getUserName();

        b_DxImportApplyBean.user = loginBean.getUser();
        session.setAttribute(beanName, b_DxImportApplyBean);
      }
    }
    return b_DxImportApplyBean;
  }

  /**
   * 构造函数
   */
  private B_DxImportApply()
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

//    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"htbh"}, new String[]{"SELECT pck_base.billNextCode('fb_sqd','htbh') FROM dual"}));
//    dsMasterTable.setSort(new SortDescriptor("", new String[]{"htbh"}, new boolean[]{true}, null, 0));

//    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"sqdhwid"}, new String[]{"s_xs_hthw"}));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    storeid="-1";
    dwtxid = "-1";


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
    deleteObservers();
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
        m_RowInfo.clear();//清除旧数据
      if(!isAdd)
        m_RowInfo.put(getMaterTable());//不是新增时,推入主表当前行
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
  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    return dsDetailTable;
  }
  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    sqdid = dsMasterTable.getValue("sqdid");//关链
    //isMasterAdd为真是返回空的从表数据集(主表新增时,从表要打开)
    dsDetailTable.setQueryString(DETAIL_SQL + (isMasterAdd ? "-1" : ("'"+sqdid+"'")));
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
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      HttpServletRequest request = data.getRequest();
      storeid = request.getParameter("storeid");//必选
      sendmodeid= request.getParameter("sendmodeid");//
      djlx = request.getParameter("djlx");//单据类型，1为调拨申请，-1为调回申请
      khlx = request.getParameter("khlx");//非必选
      dwtxid = request.getParameter("dwtxid");//非必选
      personid = request.getParameter("personid");//非必选
      jsfsid = request.getParameter("jsfsid");//非必选
      yfdj = request.getParameter("yfdj");//
      /*if(storeid.equals(""))
        return;*/
      String SQL = "";
      if(!djlx.equals(""))
        SQL = SQL+" AND djlx='"+djlx+"' ";
      if(!dwtxid.equals(""))
        SQL = " AND dwtxid='"+dwtxid+"' ";
 //     if(!personid.equals(""))
 //       SQL = SQL+" AND personid='"+personid+"' ";
 //     if(!jsfsid.equals(""))
 //       SQL = SQL+" AND jsfsid='"+jsfsid+"' ";
 //     if(!sendmodeid.equals(""))
 //       SQL = SQL+" AND sendmodeid='"+sendmodeid+"' ";
      srcFrm = request.getParameter("srcFrm");
      multiIdInput = request.getParameter("srcVar");
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("a$sqrq$a", startDay);
      row.put("a$sqrq$b", today);
      //row.put("zt", "0");
      isMasterAdd = true;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.readyRefresh();
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
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
      initRowInfo(false,false,true);
    }
  }

  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {

      isMasterAdd = String.valueOf(ADD).equals(action);//true主表新增
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));//查看或修改
        masterRow = dsMasterTable.getInternalRow();//返回当前行指针(long)
      }
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);

     // data.setMessage(showJavaScript("toDetail();"));
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
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();//得到WHERE子句
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = SQL+" AND (storeid='"+storeid+"' OR storeid IS NULL) ";
//      if(!personid.equals(""))
//        SQL = SQL+" AND a.personid='"+personid+"'";
      if(!djlx.equals(""))
        SQL = SQL+" AND djlx='"+djlx+"'";
      if(!dwtxid.equals(""))
        SQL = SQL+" AND dwtxid='"+dwtxid+"'";
//      if(!jsfsid.equals(""))
//        SQL = SQL+" AND a.jsfsid='"+jsfsid+"'";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.readyRefresh();//以便dbNavigator刷新数据集
      openDetailTable(true);
    }
    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;//已初始化查询条件
      EngineDataSet master = dsMasterTable;
      if(!master.isOpen())
        master.open();//打开主表数据集
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("a", new QueryColumn[]{
        new QueryColumn(master.getColumn("sqbh"), null, null, null),
        new QueryColumn(master.getColumn("sqrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("sqrq"), null, null, null, "b", "<="),
//        new QueryColumn(master.getColumn("ksrq"), null, null, null, "a", ">="),
//        new QueryColumn(master.getColumn("ksrq"), null, null, null, "b", "<="),
//        new QueryColumn(master.getColumn("jsrq"), null, null, null, "a", ">="),
//        new QueryColumn(master.getColumn("jsrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),
      });
      isInitQuery = true;//初始化完成
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
  /*
  public B_SaleGoodsSelect getSaleGoodsBean(HttpServletRequest req)
  {
    if(saleGoodsBean == null)
      saleGoodsBean = B_SaleGoodsSelect.getInstance(req);
    return saleGoodsBean;
  }
  */
}

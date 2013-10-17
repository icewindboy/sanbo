package engine.erp.sale.dafa;

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
public final class B_ImportOrderToBackLading extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "9001";

  private static final String MASTER_STRUT_SQL = "SELECT  * FROM xs_ht WHERE 1<>1 ";
  private static final String MASTER_SQL    = "SELECT * from xs_ht WHERE 1=1 ? ORDER BY htbh DESC";

  private static final String DETAIL_STRUT_SQL = "SELECT * FROM xs_hthw WHERE 1<>1";
  private static final String DETAIL_SQL    = "select * From VW_SALE_HTHW  where 1=1 AND zt<>4  and htid= ";
  private static final String HTHW_SQL    = "select * From VW_SALE_HTHW where 1=1 AND zt<>4  AND ? AND fgsid=? ? ";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsSearchTable  = new EngineDataSet();//

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "xs_ht");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "xs_hthw");

  private boolean isMasterAdd = true;    //是否在添加状态

  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private LookUp salePriceBean = null; //销售单价的bean的引用, 用于提取销售单价
  //private B_SaleGoodsSelect saleGoodsBean=null;
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID

  private String htid = null;
  private User user = null;
  public String dwtxid ="";
  public String djlx = "";
  public String storeid = "";
  public String personid ="";
  public String srcFrm = "";
  public String multiIdInput = "";
  public String khlx = "";
  public String jsfsid = "";
  public String sendmodeid = "";
  public String yfdj = "";
  private String isnet = "";
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_ImportOrderToBackLading getInstance(HttpServletRequest request)
  {
    B_ImportOrderToBackLading b_ImportOrderToBackLadingBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_ImportOrderToBackLadingBean";
      b_ImportOrderToBackLadingBean = (B_ImportOrderToBackLading)session.getAttribute(beanName);
      if(b_ImportOrderToBackLadingBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        b_ImportOrderToBackLadingBean = new B_ImportOrderToBackLading();
        b_ImportOrderToBackLadingBean.qtyFormat = loginBean.getQtyFormat();
        b_ImportOrderToBackLadingBean.priceFormat = loginBean.getPriceFormat();
        b_ImportOrderToBackLadingBean.sumFormat = loginBean.getSumFormat();

        b_ImportOrderToBackLadingBean.fgsid = loginBean.getFirstDeptID();
        b_ImportOrderToBackLadingBean.loginId = loginBean.getUserID();
        b_ImportOrderToBackLadingBean.loginName = loginBean.getUserName();

        b_ImportOrderToBackLadingBean.user = loginBean.getUser();
        session.setAttribute(beanName, b_ImportOrderToBackLadingBean);
      }
    }
    return b_ImportOrderToBackLadingBean;
  }

  /**
   * 构造函数
   */
  private B_ImportOrderToBackLading()
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
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"htbh"}, new boolean[]{true}, null, 0));

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
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
    deleteObservers();
  }
  //得到一行信息
  public final RowMap getLookupRow(String hthwid)
  {
    RowMap row = new RowMap();
    if(hthwid == null || hthwid.equals(""))
      return row;//返回
    EngineRow locateRow = new EngineRow(dsDetailTable, "hthwid");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
    if(locateRow == null)
      locateRow = new EngineRow(getDetailTable(), "hthwid");
    locateRow.setValue(0, hthwid);
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
    htid = dsMasterTable.getValue("htid");//关链
    //isMasterAdd为真是返回空的从表数据集(主表新增时,从表要打开)
    dsDetailTable.setQueryString(DETAIL_SQL + (isMasterAdd ? "-1" : ("'"+htid+"' and (storeid is null or storeid='"+storeid+"')")));
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

      djlx = request.getParameter("djlx");//

      khlx = request.getParameter("khlx");//非必选
      dwtxid = request.getParameter("dwtxid");//非必选
      personid = request.getParameter("personid");//非必选
      jsfsid = request.getParameter("jsfsid");//非必选
      yfdj = request.getParameter("yfdj");
//      isnet = request.getParameter("isnet");

      if(storeid.equals(""))
        return;
      String SQL = " AND zt<>8  AND (storeid='"+storeid+"' or storeid is null) ";

      if(!khlx.equals(""))
        SQL = SQL+" AND khlx='"+khlx+"' ";
      if(!dwtxid.equals(""))
        SQL = SQL+" AND dwtxid='"+dwtxid+"' ";
      if(!personid.equals(""))
        SQL = SQL+" AND personid='"+personid+"' ";
      if(!jsfsid.equals(""))
        SQL = SQL+" AND jsfsid='"+jsfsid+"' ";
      if(!sendmodeid.equals(""))
        SQL = SQL+" AND sendmodeid='"+sendmodeid+"' ";
      if(!yfdj.equals(""))
        SQL = SQL+" AND yfdj='"+yfdj+"' ";
//      if(!isnet.equals(""))
//        SQL = SQL+" AND isnet='"+isnet+"' ";

      srcFrm = request.getParameter("srcFrm");
      multiIdInput = request.getParameter("srcVar");

      //if(storeid.equals("")||djlx.equals("")||dwtxid.equals("")||personid.equals("")||khlx.equals(""))
      //  return;
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("htrq$a", startDay);
      row.put("htrq$b", today);
      row.put("zt", "0");
      isMasterAdd = true;
      EngineDataSet tmp = new EngineDataSet();
      SQL = combineSQL(HTHW_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      setDataSetProperty(tmp,SQL);
      tmp.open();
      StringBuffer buf = null;
      ArrayList contain = new ArrayList();
      int j=0;
      tmp.first();
      for(int i=0;i<tmp.getRowCount();i++)
      {
        String htid = tmp.getValue("htid");
        if(!contain.contains(htid))
        {
          contain.add(htid);
          if(buf == null)
            buf = new StringBuffer("AND htid IN(").append(htid);
          else
            buf.append(",").append(htid);
          j = j+1;
        }
        if(j>=500)
          break;
        tmp.next();
      }
      if(buf == null)
        buf =new StringBuffer();
      else
      buf.append(")");
      SQL = buf.toString();
      if(SQL.equals(""))
        SQL=" and 1<>1 ";

      //String SQL = " AND jsfsid='"+jsfsid+"' AND personid='"+personid+"'  AND khlx ='"+khlx+"'  AND dwtxid ='"+dwtxid+"' AND (storeid='"+storeid+"' or storeid is null) ";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
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
      SQL = SQL+" AND (storeid='"+storeid+"' or storeid is null) ";
      if(!personid.equals(""))
        SQL = SQL+" AND personid='"+personid+"'";
      if(!khlx.equals(""))
        SQL = SQL+" AND khlx='"+khlx+"'";
      if(!dwtxid.equals(""))
        SQL = SQL+" AND dwtxid='"+dwtxid+"'";
      if(!jsfsid.equals(""))
        SQL = SQL+" AND jsfsid='"+jsfsid+"'";
//      SQL = SQL+ " AND isnet="+isnet;
      EngineDataSet tmp = new EngineDataSet();
      SQL = combineSQL(HTHW_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      setDataSetProperty(tmp,SQL);
      tmp.open();
      StringBuffer buf = null;
      ArrayList contain = new ArrayList();
      int j=0;
      tmp.first();
      for(int i=0;i<tmp.getRowCount();i++)
      {
        String htid = tmp.getValue("htid");
        if(!contain.contains(htid))
        {
          contain.add(htid);
          if(buf == null)
            buf = new StringBuffer("AND htid IN(").append(htid);
          else
            buf.append(",").append(htid);
          j= j+1;
        }
        if(j>=500)
          break;
        tmp.next();
      }
      if(buf == null)
        buf =new StringBuffer();
      else
      buf.append(")");
      SQL = buf.toString();
      if(SQL.equals(""))
        SQL=" and 1<>1 ";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{SQL});
      if(!dsMasterTable.getQueryString().equals(SQL))
      {
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);//以便dbNavigator刷新数据集
      }
      //openDetailTable(true);
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
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("htbh"), null, null, null),
        new QueryColumn(master.getColumn("htrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("htrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("ksrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("ksrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("jsrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("jsrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "="),
      });
      isInitQuery = true;//初始化完成
    }
  }
}
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
import engine.common.LoginBean;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import engine.common.*;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 库存管理_移库单引办事处调拨单</p>
 * <p>Description: 库存管理_移库单引办事处调拨单</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author 王林川
 * @version 1.0
 */

public final class ImportInstepGoods extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "9001";

  private static final String MASTER_STRUT_SQL = "SELECT  * FROM xs_td WHERE 1<>1 ";
  private static final String MASTER_SQL
      = "SELECT  * FROM xs_td WHERE tdid in(SELECT DISTINCT tdid FROM VW_INSTEP_OUTSTORE ) AND ? AND fgsid=? ?  ";
  //2004-4-23 18:15 修改 VW_SALE_OUTSTORE加入了isrefer条件 yjg
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM VW_INSTEP_OUTSTORE WHERE 1<>1 ";
  private static final String IMPORT_INSTEPGOOD_SQL
      = "SELECT * FROM VW_INSTEP_OUTSTORE where tdid=";

  private EngineDataSet dsMasterTable  = new EngineDataSet();
  private EngineDataSet dsDetailTable  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null;
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
//  private String dwtxid = null;
  private String tdid = null;
  private String storeid = null;
  private String kc__storeid = null;
  private boolean isMasterAdd = true;    //是否在添加状态

  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private User user = null;
  public String srcFrm = "";
  public String multiIdInput = "";

  /**
   * 得到采购合同信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回采购合同信息的实例
   */
  public static ImportInstepGoods getInstance(HttpServletRequest request)
  {
    ImportInstepGoods importInstepGoodsBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "importInstepGoodsBean";
      importInstepGoodsBean = (ImportInstepGoods)session.getAttribute(beanName);
      if(importInstepGoodsBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        importInstepGoodsBean = new ImportInstepGoods();
        importInstepGoodsBean.qtyFormat = loginBean.getQtyFormat();
        importInstepGoodsBean.fgsid = loginBean.getFirstDeptID();
        importInstepGoodsBean.user = loginBean.getUser();
        session.setAttribute(beanName, importInstepGoodsBean);
      }
    }
    return importInstepGoodsBean;
  }

  /**
   * 构造函数
   */
  private ImportInstepGoods()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /**
   * Implement this engine.project.OperateCommon abstract method
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
    addObactioner(String.valueOf(SHOW_DETAIL), new ShowDetail());
    addObactioner(String.valueOf(ADD), new Master_Add_Edit());
    addObactioner(String.valueOf(EDIT),new Master_Add_Edit());
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
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
    }
    if(dsMasterTable != null){
      dsMasterTable.close();
      dsMasterTable = null;
    }
    log = null;
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
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }
  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("a$kdrq$a", startDay);
      row.put("a$kdrq$b", today);
      //row.put("zt", "0");
      isMasterAdd = true;
      srcFrm = request.getParameter("srcFrm");
      multiIdInput = request.getParameter("srcVar");
      storeid = request.getParameter("storeid");
      kc__storeid = request.getParameter("kc__storeid");
      String SQL = "";
//      String djlx = request.getParameter("djlx");
      if(!storeid.equals("")){
        SQL += " AND storeid="+storeid;
      }
      if(!kc__storeid.equals("")){
        SQL += " AND kc__storeid="+kc__storeid;
      }
      String aSQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"),fgsid,SQL});
      dsMasterTable.setQueryString(aSQL);
      dsMasterTable.setRowMax(null);
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

  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    tdid = dsMasterTable.getValue("tdid");//关链
    //isMasterAdd为真是返回空的从表数据集(主表新增时,从表要打开)
    dsDetailTable.setQueryString(IMPORT_INSTEPGOOD_SQL + (isMasterAdd ? "-1" : ("'"+tdid+"'")));
    if(dsDetailTable.isOpen())
      dsDetailTable.refresh();
    else
      dsDetailTable.open();
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

  /*
  *得到一行信息
  */

  public final RowMap getLookupRow(String tdhwid)
  {
    RowMap row = new RowMap();
    if(tdhwid == null || tdhwid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsDetailTable, "tdhwid");
    locateRow.setValue(0, tdhwid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  /**
   *  查询操作
   */
  class Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();//得到WHERE子句
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      if(!storeid.equals("")){
        SQL += " AND storeid="+storeid;
      }
      if(!kc__storeid.equals("")){
        SQL += " AND kc__storeid="+kc__storeid;
      }
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
        new QueryColumn(master.getColumn("tdbh"), null, null, null),
        new QueryColumn(master.getColumn("kdrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("kdrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),
      });
      isInitQuery = true;//初始化完成
    }

  }
  public final EngineDataSet getOneTable()
  {
    return dsDetailTable;
  }
}

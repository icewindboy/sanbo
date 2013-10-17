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
 * <p>Title: 生产——工人工资汇总显示</p>
 * <p>Description: 生产——工人工资汇总显示</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_WorkerWage extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";

  private static final String MASTER_STRUT_SQL = "SELECT * FROM VW_SC_WORKLOAD WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM VW_SC_WORKLOAD WHERE fgsid=? ? ORDER BY rq,deptid,personid";
  private static final String MASTER_SUMSTRUCT_SQL = "SELECT SUM(cq) cqhz, SUM(rb) rbhz, SUM(yb) ybhz, SUM(qj) qjhz, SUM(je) jehz, SUM(zjjgz) jjgzhz FROM VW_SC_WORKLOAD WHERE 1<>1 ";
  private static final String MASTER_SUM_SQL = "SELECT SUM(cq) cqhz, SUM(rb) rbhz, SUM(yb) ybhz, SUM(qj) qjhz, SUM(je) jehz, SUM(zjjgz) jjgzhz FROM VW_SC_WORKLOAD WHERE fgsid=? ? ";
  //private static final String MASTER_SHOW_SQL    = "SELECT * FROM sc_grgzl WHERE personid=? AND　deptid=? AND rq=to_date('?','YYYY-MM-DD') AND fgsid=? ";//
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM VW_SC_WORKLOAD_DETAIL WHERE 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM VW_SC_WORKLOAD_DETAIL WHERE personid=? AND　deptid=? AND rq=to_date('?','YYYY-MM-DD') AND fgsid=? ";//

  private EngineDataSet dsMasterTable  = new EngineDataSet();//工资表
  private EngineDataSet dsSum  = new EngineDataSet();//合计
  private EngineDataSet dsDetailTable  = new EngineDataSet();//工资表(工人工作量从表)
  private EngineDataSet dsBuyPrice = null;

  //public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable,"sc_grgzlmx");//工人工作量从表

  private long    masterRow = -1;         //保存工人工作量表查看操作的行记录指针
  private long    Row = -1;         //保存工人工资行记录指针
  private RowMap  m_RowInfo = new RowMap(); //主表添加行或修改行的引用
  private RowMap  d_RowInfo = new RowMap(); //从表一行记录的引用


  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String gzlid = null;//工作量ＩＤ
  private String personid = null;
  private String deptid = null;
  private String rq = null;
  /**
   * 工人工资列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回工人工资列表的实例
   */
  public static B_WorkerWage getInstance(HttpServletRequest request)
  {
    B_WorkerWage workerWageBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "workerWageBean";
      workerWageBean = (B_WorkerWage)session.getAttribute(beanName);
      if(workerWageBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        workerWageBean = new B_WorkerWage();
        workerWageBean.qtyFormat = loginBean.getQtyFormat();
        workerWageBean.priceFormat = loginBean.getPriceFormat();
        workerWageBean.sumFormat = loginBean.getSumFormat();

        workerWageBean.fgsid = loginBean.getFirstDeptID();
        workerWageBean.loginId = loginBean.getUserID();
        workerWageBean.loginName = loginBean.getUserName();
        workerWageBean.loginDept = loginBean.getDeptID();
        //设置格式化的字段
        workerWageBean.dsMasterTable.setColumnFormat("jehz", workerWageBean.priceFormat);
        workerWageBean.dsMasterTable.setColumnFormat("jjgzhz", workerWageBean.priceFormat);
        workerWageBean.dsDetailTable.setColumnFormat("sl", workerWageBean.qtyFormat);
        workerWageBean.dsDetailTable.setColumnFormat("de", workerWageBean.priceFormat);
        workerWageBean.dsDetailTable.setColumnFormat("desl", workerWageBean.qtyFormat);
        workerWageBean.dsDetailTable.setColumnFormat("jjgz", workerWageBean.priceFormat);
        workerWageBean.dsSum.setColumnFormat("jjgzhz", workerWageBean.priceFormat);
        workerWageBean.dsSum.setColumnFormat("jehz", workerWageBean.priceFormat);
        session.setAttribute(beanName, workerWageBean);
      }
    }
    return workerWageBean;
  }

  /**
   * 构造函数
   */
  private B_WorkerWage()
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
   setDataSetProperty(dsMasterTable, null);
   setDataSetProperty(dsDetailTable, null);
   setDataSetProperty(dsSum, null);

   addObactioner(String.valueOf(INIT), new Init());
   addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
   //addObactioner(String.valueOf(EDIT), new Master_Edit());
   addObactioner(SHOW_DETAIL, new ShowDetail());
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
   /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表一列的信息*/
  public final RowMap getDetailRowinfos() {
    return d_RowInfo;
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
    if(dsSum != null){
      dsSum.close();
      dsSum = null;
    }
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfo = null;
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

  /*得到工资表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到工人工作量从表表对象*/
  public final EngineDataSet getDetailTable(){return dsDetailTable;}
  /*得到工资合计对象*/
  public final EngineDataSet getSumTable(){return dsSum;}
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
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      //初始化查询项目和内容
       RowMap row = fixedQuery.getSearchRow();
       row.clear();
       row.put("deptid", loginDept);
      //初始化时清空数据集
       if(dsMasterTable.isOpen() && dsMasterTable.getRowCount() > 0)
        dsMasterTable.empty();
       dsMasterTable.setQueryString(MASTER_STRUT_SQL);
       dsMasterTable.setRowMax(null);
       if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
         dsDetailTable.empty();
       if(dsSum.isOpen() && dsSum.getRowCount() > 0)
         dsSum.empty();
       data.setMessage(showJavaScript("showFixedQuery();"));
    }
  }

  /**
   * 主表添加或修改操作的触发类

   class Master_Edit implements Obactioner
   {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      Row = dsMasterTable.getInternalRow();
      personid = dsMasterTable.getValue("personid");
      deptid = dsMasterTable.getValue("deptid");
      rq = dsMasterTable.getValue("rq");
      String SQL = combineSQL(MASTER_SHOW_SQL, "?", new String[]{personid,deptid,rq,fgsid});
      dsMasterShowTable.setQueryString(SQL);
      if(dsMasterShowTable.isOpen())
        dsMasterShowTable.refresh();
      else
        dsMasterShowTable.open();
      data.setMessage(showJavaScript("toDetail();"));
    }
  }
     */
  /*打开从表*/
  public final void openDetailTable()
  {
    String SQL = DETAIL_SQL +  gzlid;

    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    else
      dsDetailTable.refresh();
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
      personid = dsMasterTable.getValue("personid");
      deptid = dsMasterTable.getValue("deptid");
      rq = dsMasterTable.getValue("rq");

      String SQL = combineSQL(DETAIL_SQL, "?", new String[]{personid,deptid,rq,fgsid});
      dsDetailTable.setQueryString(SQL);
      if(!dsDetailTable.isOpen())
        dsDetailTable.open();
      else
        dsDetailTable.refresh();
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
      String sql = combineSQL(MASTER_SUM_SQL, "?", new String[]{fgsid, SQL});
      SQL = combineSQL(MASTER_SQL, "?", new String[]{fgsid, SQL});
      if(!dsMasterTable.getQueryString().equals(SQL))
      {
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);
      }
      dsSum.setQueryString(sql);
      if(dsSum.isOpen())
        dsSum.refresh();
      else
        dsSum.openDataSet();
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
        new QueryColumn(master.getColumn("rq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("rq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),//员工ID
        //new QueryColumn(master.getColumn("gzlid"), "sc_grgzlmx", "gzlid", "cpid", null, "="),//从表品名
        //new QueryColumn(master.getColumn("gzlid"), "VW_SCGRGZL_QUERY", "gzlid", "cpbm", "cpbm", "like"),//从表产品编码
        //new QueryColumn(master.getColumn("gzlid"), "VW_SCGRGZL_QUERY", "gzlid", "product", "product", "like"),//从表品名
      });
      isInitQuery = true;
    }
  }
}

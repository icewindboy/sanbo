package engine.erp.jit;

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
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 生产管理—加工单单选任务单主表</p>
 * <p>Description: 生产管理—加工单单选任务单主表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */
public final class B_ScjhSingleSelect extends BaseAction implements Operate
{
  //private static final String PROCESS_SINGLE_TASK_SQL = " SELECT DISTINCT wlxqjhid, wlxqh, deptid, rq, fgsid,zsl,zdr,jhlx,scjhid FROM VW_TASK_SINGLESEL_MRP "
  //                                                    + " WHERE (scdeptid IS NULL OR scdeptid='?' ) AND fgsid='?' ";

  //生产加工单明细数据.从sc_sjbom中取出
  /*
  private static final String SC_JGD_DETAIL_SJBOM = "select gxfdid, cc, wgcl, cpid, dmsxid,  sl,  scsl, sjbomid, scjhmxid, scjhid, "
      + " tag, scjhidtmp,deptid  from ( select m.*, n.scjhid scjhidtmp from ( "
      + " SELECT h.gxfdid, m.cc, m.wgcl, m.cpid, j.dmsxid, j.sl, m.scxql scsl, m.sjbomid, m.scjhmxid, g.scjhid, "
      + " g.deptid, g.jhh, g.scjhid||'#'||h.gxfdid||m.cc||m.wgcl||m.cpid||j.dmsxid||m.sjbomid||m.scjhmxid||f.deptid||g.jhh tag "
      + " FROM sc_gylxmx h, sc_jhmx j, sc_sjbom m , sc_gylx e, sc_gxfd f, sc_jh g "
      + " WHERE j.scjhmxid = m.scjhmxid AND h.gylxid = e.gylxid "
      + "       AND e.cpid = m.cpid AND nvl(m.dmsxid,0) = e.dmsxid "
      + "       AND m.chxz = 1 AND f.gxfdid = h.gxfdid and j.scjhid = g.scjhid and g.fgsid = ?  "
      + " GROUP BY h.gxfdid, m.cc, m.wgcl, m.cpid, j.dmsxid,j.sl, m.scxql, m.sjbomid, m.scjhmxid,f.deptid, g.deptid, g.scjhid, g.jhh "
      + " ORDER BY g.jhh, m.cc DESC, MAX(h.list) DESC ) m, (select distinct k.scjhid  from sc_jh k, sc_jgd l where l.scjhid=k.scjhid) n where m.scjhid = n.scjhid(+) ) where scjhidtmp is null ";
*/
  private static final String SC_JGD_DETAIL_SJBOM ="select a.* from sc_jh a where a.zt=1 and a.scjhid not in(select b.scjhid from sc_process b) and a.fgsid=? ? ";
  private EngineDataSet dsSingleTask  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null;
  private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false; //是否已经初始化查询条件
  /**
   * 得到任务单主表信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回任务单主表信息的实例
   */
  public static B_ScjhSingleSelect getInstance(HttpServletRequest request)
  {
    B_ScjhSingleSelect B_ScjhSingleSelectBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_ScjhSingleSelectBean";
      B_ScjhSingleSelectBean = (B_ScjhSingleSelect)session.getAttribute(beanName);
      if(B_ScjhSingleSelectBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        B_ScjhSingleSelectBean = new B_ScjhSingleSelect();
        B_ScjhSingleSelectBean.qtyFormat = loginBean.getQtyFormat();

        B_ScjhSingleSelectBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, B_ScjhSingleSelectBean);
      }
    }
    return B_ScjhSingleSelectBean;
  }

  /**
   * 构造函数
   */
  private B_ScjhSingleSelect()
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
    setDataSetProperty(dsSingleTask, null);

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
  }
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
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
    if(dsSingleTask != null){
      dsSingleTask.close();
      dsSingleTask = null;
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
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;


      String SQL = combineSQL(SC_JGD_DETAIL_SJBOM, "?", new String[]{fgsid});
      dsSingleTask.setQueryString(SQL);
      dsSingleTask.setRowMax(null);
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
      SQL = combineSQL(SC_JGD_DETAIL_SJBOM, "?", new String[]{ fgsid, SQL});
      if(!dsSingleTask.getQueryString().equals(SQL))
      {
        dsSingleTask.setQueryString(SQL);
        dsSingleTask.setRowMax(null);//以便dbNavigator刷新数据集
      }
    }
    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;//已初始化查询条件
      EngineDataSet master = dsSingleTask;
      if(!master.isOpen())
        master.open();//打开主表数据集
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("jhh"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("djh"), null, null, null, null, "=")
      });
      isInitQuery = true;//初始化完成
    }
  }
    /*
  *得到一行信息
  */
  public final RowMap getLookupRow(String tag)
  {
    RowMap row = new RowMap();
    if(tag == null || tag.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsSingleTask, "tag");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "tag");
    locateRow.setValue(0, tag);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsSingleTask;
  }
}



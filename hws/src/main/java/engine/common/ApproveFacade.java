package engine.common;

import java.text.*;
import java.util.*;

import javax.servlet.http.*;

import com.borland.dx.dataset.LoadListener;
import com.borland.dx.dataset.SortDescriptor;
import com.borland.dx.dataset.LoadEvent;
import com.borland.dx.dataset.MasterLinkDescriptor;
import com.borland.dx.dataset.DataSetData;
import engine.action.*;
import engine.dataset.*;
import engine.web.observer.*;
import engine.util.StringUtils;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

  /*
  审批记录明细ID	spjlmxID		NUMBER(12)	TRUE	FALSE	TRUE
  人员ID	personid	审批人	NUMBER(6)	FALSE	TRUE	FALSE
  审批项目特殊明细ID	spxmtsmxID		NUMBER(9)	FALSE	TRUE	FALSE
  审批项目明细ID	spxmmxID		NUMBER(9)	FALSE	TRUE	FALSE
  审批记录ID	spjlID		NUMBER(11)	FALSE	TRUE	FALSE
  审批等级	spdj		NUMBER(2)	FALSE	FALSE	FALSE
  审批名称	spmc		VARCHAR2(32)	FALSE	FALSE	FALSE
  通过标志	sftg	0=未审批,1=通过,2=驳回	NUMBER(1)	FALSE	FALSE	FALSE
  审批日期	sprq		DATE	FALSE	FALSE	FALSE
  审批意见	spyj	审批意见	VARCHAR2(256)	FALSE	FALSE	FALSE
  URL	URL		VARCHAR2(128)	FALSE	FALSE	FALSE*/
public final class ApproveFacade extends BaseAction implements Operate
{
  //提取审批项目的主键字段名信息
  //private static final String APPROVE_KEY_SQL = "SELECT zjzdm FROM sp_xm WHERE spxmbm='@'";
  //提取审批项目的主键字段名,不通过值信息等信息
  private static final String APPROVE_INFO_SQL = "SELECT dybm, zjzdm, ztzdm, ztmszdm, ztbtgz, zttgz FROM sp_xm WHERE spxmbm='@'";
  //审批项目中特殊审批列表
  private static final String SPECIAL_APPROVE_SQL
      = "SELECT m.spxmmxid, m.spxmtsmxid, t.tsmxbm, m.tsz FROM sp_xm x, sp_xmmx m, sp_xmtsmx t "
      + "WHERE m.spxmtsmxid = t.spxmtsmxid AND m.spxmid=x.spxmid AND x.spxmbm='@'";
  //添加审批列表的SQL
  private static final String ADD_APPROVE_SQL = "{CALL pck_approve.addapprovelist(?,'@','@',@,@,@,'@')}";
  //提交审批
  private static final String SUBMIT_APPROVE_SQL = "{CALL pck_approve.submitApprove(@,@,@,'@')}";
  //取消审批
  private static final String CANCEL_APPROVE_SQL = "{CALL pck_approve.cancelApprove('@',@,@)}";
  //当前人员待审批项目
  private static final String APPROVE_LIST_SQL = "{CALL pck_approve.getApproveList(?,@)}";
      /*= "SELECT j.spjlid, j.spxmbm, j.spxmmc, j.spnr, j.zjid, j.deptid, j.personid, j.tjsprq "
      + "FROM sp_jl j, sp_spr s, ("
      + "   SELECT a.spxmmxid, a.spjlid FROM ("
      + "    SELECT m.spxmmxid, m.spjlid, m.spdj,"
      + "      (SELECT MIN(t.spdj) FROM sp_jlmx t WHERE (t.sftg IS NULL OR t.sftg=0) AND t.spjlid = m.spjlid) mindj"
      + "    FROM sp_jlmx m WHERE m.sftg IS NULL OR m.sftg=0"
      + "  ) a WHERE a.spdj = a.mindj"
      + ") b WHERE j.spjlid = b.spjlid AND b.spxmmxid = s.spxmmxid "
      + "AND (j.sfjs IS NULL OR j.sfjs=0) AND s.personid =@ ";*/
  //一条当前人员审批记录
  private static final String APPROVE_FOR_ID_SQL
      = "SELECT j.spjlid, j.spxmbm, j.spxmmc, j.spnr, j.zjid, j.deptid, j.personid, j.tjsprq "
      + "FROM  sp_jl j WHERE j.spjlid=@ ";
  //一条审批记录对应的明细
  private static final String APPROVE_DETAIL_ONE_SQL = "SELECT * FROM sp_jlmx WHERE spjlid=@ ";
  //当前人员待审批项目明细
  private static final String APPROVE_DETAIL_SQL = "SELECT * FROM sp_jlmx WHERE spjlid IN (@)";
  //审批项目明细的结构SQL
  private static final String APPROVE_DETAIL_STRUCT_SQL = "SELECT * FROM sp_jlmx WHERE 1<>1";
  //得到审批项目流程可审批的人员id列表,以逗号分割
  private static final String APPROVE_PERSON_SQL = "SELECT pck_approve.getApprovePerson(@) FROM dual";
  //审批项目流程数量
  private static final String APPROVE_FLOW_COUNT_SQL
      = "SELECT COUNT(d.spxmmxid) FROM sp_xm m, sp_xmmx d WHERE m.spxmid=d.spxmmxid AND m.spxmbm='@'";

  //添加到审批列表的常量
  private static final int FLOW_OPERATE_SUCCEED = 1;  //成功
  private static final int FLOW_NO_EXIST        = -2001;  //所填的项目不存在
  private static final int FLOW_NO_DEFINE       = -2002;  //没有定义审批流程
  private static final int FLOW_NO_PERSON       = -2003;  //审批流程没有定义审批人
  private static final int FLOW_ALREADY_EXECUTE = -2004;  //已经在审批之中
  private static final int FLOW_STAT_ERROR      = -2005;  //单据的状态有误

  //private static Hashtable approvePool = new Hashtable();//ApproveFacade的缓池

  private EngineDataSet dsSpecialApprove = null;//审批项目特殊明细
  //private EngineDataSet dsApproveProject = null;//审批项目
  private EngineDataSet dsApproveOne = null;//保存审批的一条记录,用于同步其他用户的数据

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表

  private EngineRow approvelistRow = null;//用于定位审批列表
  private EngineRow detailPostRow = null;  //提交数据是定位数据

  public  String retuUrl = null;
  private String spjlid = null;   //保存审批记录id
  private String spjlmxid = null; //保存审批明细id

  private String loginId = null;   //登录员工的ID
  private String loginName = null; //登录员工的姓名
  private String deptid = null;
  private String fgsid = null;   //分公司ID

  public  boolean isFromMain = false;
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static ApproveFacade getInstance(HttpServletRequest request)
  {
    ApproveFacade approveFacadeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "approveFacadeBean";
      approveFacadeBean = (ApproveFacade)session.getAttribute(beanName);
      if(approveFacadeBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        approveFacadeBean = new ApproveFacade();
        approveFacadeBean.fgsid = loginBean.getFirstDeptID();
        approveFacadeBean.loginId = loginBean.getUserID();
        approveFacadeBean.loginName = loginBean.getUserName();
        approveFacadeBean.deptid = loginBean.getDeptID();
        session.setAttribute(beanName, approveFacadeBean);
        //approvePool.put(session.getId(), approveFacadeBean);
      }
    }
    return approveFacadeBean;
  }

  /**
   * 构造函数
   */
  private ApproveFacade()
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
    //dsMasterTable.addLoadListener(new MasterLoadListener());

    setDataSetProperty(dsMasterTable, null);
    setDataSetProperty(dsDetailTable, APPROVE_DETAIL_STRUCT_SQL);
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"spxmbm","deptid","tjsprq","personid"}, new boolean[]{false, false, false, false}, null, 0));
    dsDetailTable.setMasterLink(new MasterLinkDescriptor(dsMasterTable, new String[] {"spjlid"}, new String[] {"spjlid"}, false, false, true));
    dsDetailTable.setSort(new SortDescriptor("", new String[]{"spdj"}, new boolean[]{false}, null, 0));

    Edit edit = new Edit();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(EDIT), edit);
    addObactioner(String.valueOf(APPROVE), edit);
    addObactioner(String.valueOf(POST), new Post());
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
    //将实例从缓冲池中移去
    String id = event.getSession().getId();
    //approvePool.remove(id);
    //释放数据集
    if(dsMasterTable != null)
    {
      dsMasterTable.closeDataSet();
      dsMasterTable = null;
    }
    if(dsDetailTable != null)
    {
      dsDetailTable.closeDataSet();
      dsDetailTable = null;
    }
    deleteObservers();    //释放所有触发对象
    approvelistRow = null;//用于定位审批列表
    //detailLocateRow = null;//用于定位审批项目明细
    detailPostRow = null;  //提交数据是定位数据
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
   * 定位主表的记录
   * @param spjlid 审批记录id
   * @return 返回是否有该条记录
   */
  private boolean locateApproveList(String spjlid)
  {
    if(spjlid == null)
      return false;
    EngineDataSet dsApprovelist = getMaterTable();
    if(approvelistRow == null)
      approvelistRow = new EngineRow(dsApprovelist, "spjlid");
    approvelistRow.setValue(0, spjlid);
    return dsApprovelist.locate(approvelistRow, LocateUtil.FIRST);
  }

  /**
   * 得到审批状态
   * @return 返回审批状态
   */
  public String getAproveState(int row)
  {
    EngineDataSet detail = getDetailTable();
    if(!detail.isOpen())
      return "";

    getMaterTable().goToRow(row);

    String nextValue = null;
    String priorValue = null;
    detail.first();
    while(detail.inBounds())
    {
      int sftg = detail.getBigDecimal("sftg").intValue();
      if(sftg == 0)//未审批的
      {
        nextValue = detail.getValue("spmc");
        break;
      }
      priorValue = detail.getValue("spmc");
      detail.next();
    }
    StringBuffer result = new StringBuffer();
    if(priorValue != null)
      result.append(priorValue).append(":通过, ");
    result.append(nextValue).append(":未审批");
    return result.toString();
  }

  /**
   * 打开审批列表数据
   */
  public final void openApproveData(){
    if(!dsMasterTable.isOpen())
    {
      dsMasterTable.setQueryString(combineSQL(APPROVE_LIST_SQL, "@", new String[]{loginId}));
      dsMasterTable.openDataSet();
    }
    else
      dsMasterTable.refresh();

    openDetailTable();
  }

  /**
   * 得到表对象
   * @return 返回主表数据集对象
   */
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /**
   * 得到从表对象
   * @return 返回从表数据集对象
   */
  public final EngineDataSet getDetailTable()
  {
    return dsDetailTable;
  }

  /**
   * 打开从表
   */
  private final void openDetailTable()
  {
    EngineDataSet ds = getMaterTable();
    StringBuffer buf = null;
    int rowCount = ds.getRowCount();
    for(int i=0; i<rowCount; i++)
    {
      if(buf == null)
        buf = new StringBuffer();
      buf.append(ds.getValue("spjlid"));
      if(i < rowCount-1)//不是最后一次循环
        buf.append(",");
      ds.next();
    }

    if(buf == null)
      return;

    String sql = combineSQL(APPROVE_DETAIL_SQL, "@", new String[]{buf.toString()});
    dsDetailTable.setQueryString(sql);
    if(dsDetailTable.isOpen())
      dsDetailTable.refresh();
    else
      dsDetailTable.openDataSet();
  }

/*
  final class MasterLoadListener implements LoadListener, java.io.Serializable
  {
    public void dataLoaded(LoadEvent e) {
      openDetailTable();
    }
  }
*/
  /**
   * 初始化操作的触发类
   */
  final class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      isFromMain = false;
    }
  }

  /**
   * 查看审批信息
   */
  final class Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(String.valueOf(APPROVE).equals(action))
      {
        retuUrl = data.getParameter("src");
        retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
        isFromMain = true;
      }

      String temp = data.getParameter("rownum");
      spjlid = null;
      spjlmxid = null;
      //定位数据. 用定位数据集代替goToRow, 因为可能别人已经审批过该条记录
      if(!locateApproveList(temp))
      {
        if(isFromMain){
          data.getResponse().sendRedirect(retuUrl);
          data.setMessage("#");
        }
        return;// data.setMessage(showJavaScript("alert('您所要审批的记录,已经');"));
      }
      if(!nextApprove())
      {
        if(isFromMain){
          data.getResponse().sendRedirect(retuUrl);
          data.setMessage("#");
        }
        return;
      }

      spjlid = temp;
      spjlmxid = dsDetailTable.getValue("spjlmxid");
      //重定向URL
      String url = dsDetailTable.getValue("url");
      StringBuffer path = new StringBuffer(url);
      path.append(url.indexOf("?") > -1 ? "&" : "?");
      path.append(OPERATE_KEY).append("=").append(APPROVE);
      path.append("&id=").append(dsMasterTable.getValue("zjid"));
      url = path.toString();
      //
      data.getResponse().sendRedirect(url);
      data.setMessage("#");
    }

    /**
     * 定位从表数据集体
     * @return 返回是具有从表记录
     */
    private boolean nextApprove()
    {
      EngineDataSet detail = getDetailTable();
      if(!detail.isOpen())
        return false;

      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        int sftg = detail.getBigDecimal("sftg").intValue();
        if(sftg == 0)//未审批的
          return true;
        detail.next();
      }
      return false;
    }
  }

  /**
   * 提交审批操作的触发类，并同步所有在线的人员的审批记录
   */
  final class Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //定位数据. 用定位数据集代替goToRow, 因为可能别人已经审批过该条记录
      if(!locateApproveList(spjlid))
        return;// data.setMessage(showJavaScript("alert('您所要审批的记录,已经');"));
      if(!locateApproveDetail(spjlmxid))
        return;

      String sftg = data.getRequest().getParameter("sftg");
      sftg = "1".equals(sftg) ? "1" : "2";
      String spyj = data.getRequest().getParameter("spyj");
      spyj = spyj == null ? "" : StringUtils.replaceQuotatemark(spyj);
      String result = combineSQL(SUBMIT_APPROVE_SQL, "@", new String[]{spjlmxid, loginId, sftg, spyj});
      try{
        dataSetResolver.updateQuery(new String[]{result});
      }
      catch(Exception ex){
        log.error("ApproveFacade[Post]", ex);
        StringBuffer buf = new StringBuffer("<script language='javascript'>showMessage('");
        buf.append(engine.util.StringUtils.toHTML(ex.getMessage()));
        buf.append("',true,").append(isFromMain && retuUrl != null ? "\""+retuUrl+"\"" : "null");
        buf.append(");location.href='#'</script>");
        //isFromMain && retuUrl != null)//如果从main.jsp过来
        data.setMessage(buf.toString());
      }
      //dsDetailTable.setValue("sftg", info.get("sftg").equals("1") ? "1" : "2");
      //dsDetailTable.setValue("");
      //syncUserApprove(spjlid);
    }

    /**
     * 定位从表的记录
     * @param key 字段值
     * @param isDetailId false:key的值是spjlid字段的值, true:key的值是spjlmxid字段的值
     * @return
     */
    private boolean locateApproveDetail(String key)
    {
      if(key == null)
        return false;
      EngineDataSet detail = getDetailTable();
      if(!detail.isOpen())
        return false;

      if(detailPostRow == null)
        detailPostRow = new EngineRow(detail, "spjlmxid");
      detailPostRow.setValue(0, key);
      return detail.locate(detailPostRow, LocateUtil.FIRST);
    }
  }
  //------------------------------------------------------------------------------
  /**
   * 同步所有的登陆用户审批对象
   * @param spjlid 审批记录ID
   * @param personids 与该记录有关系的人员列表
   * @throws Exception 异常
   *
  private void syncUserApprove(String spjlid) throws Exception
  {
    //将特定分隔符的字符串转化为数组
    String result = combineSQL(APPROVE_PERSON_SQL, "@", new String[]{spjlid});
    result = dataSetProvider.getSequence(result);
    ArrayList personids = null;
    if(result == null)
      personids = new ArrayList();
    else{
      String[] temp = parseString(result, ",");
      personids = new ArrayList(temp.length);
      for(int i=0; i<temp.length; i++)
        personids.add(temp[i]);
    }
    //如果该审批项目没有结束,则得到主表数据和从表数据
    DataSetData detailData = null;
    if(personids.size() > 0)
    {
      //得到主表数据
      String sql = combineSQL(APPROVE_FOR_ID_SQL, "@", new String[]{spjlid});
      if(dsApproveOne == null)
      {
        dsApproveOne = new EngineDataSet();
        setDataSetProperty(dsApproveOne, null);
      }
      dsApproveOne.setQueryString(sql);
      if(dsApproveOne.isOpen())
        dsApproveOne.refresh();
      else
        dsApproveOne.open();

      //得到从表数据
      sql = combineSQL(APPROVE_DETAIL_ONE_SQL, "@", new String[]{spjlid});
      ProvideInfo provideInfo = new ProvideInfo(sql);
      provideInfo.setLoadDataUseSelf(false);
      detailData = dataSetProvider.getDataSetData(new ProvideInfo[]{provideInfo})[0];
    }
    //同步数据
    synchronized(approvePool)
    {
      //得到字段和值
      Object[] objs = approvePool.values().toArray();
      for(int i=0; i < objs.length; i++)
      {
        ApproveFacade approve = (ApproveFacade)objs[i];
        synchronized(approve)
        {
          //是否对该用户有效
          if(personids.indexOf(approve.loginId) > -1)
          {
            if(approve.locateApproveList(spjlid))
              approve.dsMasterTable.deleteRow();
            //添加审批主表记录
            if(dsApproveOne.getRowCount() > 0)
            {
              EngineRow masterData = new EngineRow(approve.dsMasterTable);
              dsApproveOne.copyTo(masterData);
              approve.dsMasterTable.addRow(masterData);
            }
            approve.dsMasterTable.resetPendingStatus(true);
            //添加审批从表信息记录
            if(detailData != null)
            {
              if(!approve.dsDetailTable.isOpen())
                approve.dsDetailTable.open();
              detailData.loadDataSet(approve.dsDetailTable);
            }
          }
          else if(approve.locateApproveList(spjlid)){
            //已经设置级联删除，从表记录会跟者删除的
            approve.dsMasterTable.deleteRow();
            approve.dsMasterTable.resetPendingStatus(true);
          }
        }
      }
    }
  }*/

  /**
   * 审批项目是否需要审批
   * @param projectCode 审批项目编码
   * @throws Exception 异常
   */
  public boolean isNeedApprove(String projectCode) throws Exception
  {
    String sql = combineSQL(APPROVE_FLOW_COUNT_SQL, "@", new String[]{projectCode});
    return !dataSetProvider.getSequence(sql).equals("0");
  }

  /**
   * 提交审批
   * @param ds 要提交审批的数据集
   * @param row 数据集的第几行
   * @param projectCode 审批项目编码
   * @param content 审批内容
   * @throws Exception 异常
   *
  public void putAproveList(EngineDataSet ds, int row, String projectCode, String content) throws Exception
  {
    putAproveList(ds, row, projectCode, content, this.deptid);
  }
  */

  /**
   * 提交审批
   * @param ds 要提交审批的数据集
   * @param row 数据集的第几行
   * @param projectCode 审批项目编码
   * @param content 审批内容
   * @param deptid 下达部门ID
   * @throws Exception 异常
   */
  public void putAproveList(EngineDataSet ds, int row, String projectCode, String content, String deptid)
      throws Exception
  {
    putAproveList(ds, row, projectCode, content, deptid, null);
  }

  /**
   * 提交审批到审批列表中（添加审批记录）
   * @param ds 要提交审批的数据集
   * @param row 数据集的第几行
   * @param projectCode 审批项目编码
   * @param content 审批内容
   * @param listener 审批监听器
   * @throws Exception 异常

  public void putAproveList(EngineDataSet ds, int row, String projectCode,
                            String content, ApproveListener listener) throws Exception
  {
    putAproveList(ds, row, projectCode, content, this.deptid, listener);
  }
  */

  /**
   * 提交审批到审批列表中（添加审批记录）
   * @param ds 要提交审批的数据集
   * @param row 数据集的第几行
   * @param projectCode 审批项目编码
   * @param content 审批内容
   * @param listener 审批监听器
   * @throws Exception 异常
   */
  public void putAproveList(EngineDataSet ds, int row, String projectCode,
                            String content, String deptid, ApproveListener listener) throws Exception
  {
    if(deptid==null)
     deptid = this.deptid;
    content = content == null ?  "" : engine.util.StringUtils.getUnicodeSubString(content, 128);
    //String s = content == null ? "" :  ? content.substring(0, 64): content;
    //String content = "HT000006合同编号2合同编号3合同编号4合同编号5合同编号6合同编号7合同编号8合同编号9合同编号10合同编号11合同编号12合同编号13合同编号14合同编号15合同编号";
    //String s = new String(content.getBytes(), 0, 128);
    synchronized(ds)
    {
      String sql = combineSQL(APPROVE_INFO_SQL, "@", new String[]{projectCode});
      EngineDataSet dsTemp = new EngineDataSet();
      dsTemp.setProvider(dataSetProvider);
      dsTemp.setQueryString(sql);
      dsTemp.openDataSet();
      if(dsTemp.getRowCount() == 0){
        dsTemp.closeDataSet();
        throw new Exception("审批项目未定义");
      }
      //得到主键字段名称
      String keyColumn = dsTemp.getValue("zjzdm");
      ds.goToRow(row);
      String keyValue = ds.getValue(keyColumn);
      StringBuffer checkSQL = new StringBuffer();
      checkSQL.append("SELECT COUNT(*) FROM ").append(dsTemp.getValue("dybm"));
      checkSQL.append(" WHERE ").append(dsTemp.getValue("ztzdm")).append(" ='")
              .append(dsTemp.getValue("ztbtgz")).append("'");
      checkSQL.append(" AND ").append(keyColumn).append("='").append(keyValue).append("'");
      if(!dataSetProvider.getSequence(checkSQL.toString()).equals("1")){
        dsTemp.closeDataSet();
        throw new Exception("该记录已经不能提交审批！");
      }
      dsTemp.closeDataSet();
      //
      String noExecute = "";
      if(listener != null)
      {
        sql = combineSQL(SPECIAL_APPROVE_SQL, "@", new String[]{projectCode});
        EngineDataSet data = new EngineDataSet();
        data.setProvider(dataSetProvider);
        data.setQueryString(sql);
        data.openDataSet();
        if(data.getRowCount() > 0)
        {
          ApproveResponse[] reponses = new ApproveResponse[data.getRowCount()];
          data.first();
          for(int i=0; i<reponses.length; i++)//m.spxmtsmxid, t.tsmxbm, m.tsz
          {
            reponses[i] = new ApproveResponse(data.getValue("spxmmxid"), //data.getValue("spxmtsmxid"),
                              data.getValue("tsmxbm"), data.getValue("tsz"));
            data.next();
          }
          //执行监听器
          listener.processApprove(reponses);
          //处理执行后的结果, 得到不通过审批项目的id
          StringBuffer buf = null;
          for(int i=0; i<reponses.length; i++)
          {
            if(reponses[i].response)
              continue;
            buf = buf == null ? new StringBuffer(reponses[i].projectFlowId)
                  : buf.append(",").append(reponses[i].projectFlowId);
          }
          if(buf != null)
            noExecute = buf.toString();
        }
        data.closeDataSet();
      }
      //滚动数据集
      ds.goToRow(row);
      //添加到审核列表
      sql = combineSQL(ADD_APPROVE_SQL, "@", new String[]{projectCode, content, loginId, deptid, keyValue, noExecute});
      EngineDataSet dsApproveProject = new EngineDataSet();
      dsApproveProject.setProvider(dataSetProvider);
      dsApproveProject.setQueryString(sql);
      dsApproveProject.openDataSet();
      String spjlid = null;
      try{
        String resultCap = dsApproveProject.getValue("resultCap");
        int result = dsApproveProject.getBigDecimal("resultCode").intValue();
        switch(result){
          case FLOW_NO_EXIST:
          case FLOW_NO_PERSON:
          case FLOW_ALREADY_EXECUTE:
            throw new Exception(resultCap);
          case FLOW_NO_DEFINE:
            String approveName = dsApproveProject.getValue("spridzdm");
            ds.setValue(approveName, loginId);
          case FLOW_OPERATE_SUCCEED:
            String stateColumn = dsApproveProject.getValue("ztzdm");
            String stateDescColumn = dsApproveProject.getValue("ztmszdm");
            String state = result == FLOW_OPERATE_SUCCEED ? dsApproveProject.getValue("ztddz") : dsApproveProject.getValue("zttgz");
            ds.setValue(stateColumn, state);
            ds.setValue(stateDescColumn, resultCap);
            ds.post();
            ds.resetPendingStatus(ds.getInternalRow(), true);
            break;
        }
        spjlid = dsApproveProject.getValue("spjlid");
      }
      finally{
        if(dsApproveProject != null)
        {
          dsApproveProject.closeDataSet();
          dsApproveProject = null;
        }
      }
      //同步其他用户数据
      //syncUserApprove(spjlid);
    }
  }

  /**
   * 最后审批通过的人取消审批
   * @param ds 要提交审批的数据集
   * @param row 数据集的第几行
   * @param projectCode 审批项目编码
   * @throws Exception 异常
   */
  public void cancelAprove(EngineDataSet ds, int row, String projectCode) throws Exception
  {
    synchronized(ds)
    {
      String sql = combineSQL(APPROVE_INFO_SQL, "@", new String[]{projectCode});
      EngineDataSet dsTemp = new EngineDataSet();
      dsTemp.setProvider(dataSetProvider);
      dsTemp.setQueryString(sql);
      dsTemp.openDataSet();
      if(dsTemp.getRowCount() == 0){
        dsTemp.closeDataSet();
        throw new Exception("审批项目未定义");
      }
      //得到主键字段名称
      String keyColumn = dsTemp.getValue("zjzdm");
      ds.goToRow(row);
      String keyValue = ds.getValue(keyColumn);
      StringBuffer checkSQL = new StringBuffer();
      checkSQL.append("SELECT COUNT(*) FROM ").append(dsTemp.getValue("dybm"));
      checkSQL.append(" WHERE ").append(dsTemp.getValue("ztzdm")).append(" ='")
              .append(dsTemp.getValue("zttgz")).append("'");
      checkSQL.append(" AND ").append(keyColumn).append("='").append(keyValue).append("'");
      if(!dataSetProvider.getSequence(checkSQL.toString()).equals("1")){
        dsTemp.closeDataSet();
        throw new Exception("该记录已经不能取消审批！");
      }
      //取消审核
      sql = combineSQL(CANCEL_APPROVE_SQL, "@", new String[]{projectCode, loginId, keyValue});
      dataSetResolver.updateQuery(new String[]{sql});
      //更新数据集
      String stateColumn = dsTemp.getValue("ztzdm");
      String stateDescColumn = dsTemp.getValue("ztmszdm");
      String state = dsTemp.getValue("ztbtgz");
      ds.setValue(stateColumn, state);
      ds.setValue(stateDescColumn, "");
      ds.post();
      ds.resetPendingStatus(ds.getInternalRow(), true);
    }
  }
}
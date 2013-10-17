package engine.erp.store;


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
 * <p>Title: 库存管理—外贸出库单引入提单主表</p>
 * <p>Description: 库存管理—外贸出库单引入提单主表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */
/**
 * 2004-4-23 17:37 修改 VW_SALEOUTSTORE_SINGLE_LADDING加入了isrefer条件 yjg
 * 2004-4-23 10:46 下面这样先判断rquest是否为空.然后再给值的原因.主要想防止,;因分页后造成的参数的丢失 yjg
 * 2004-4-21 19:00 新增 在init类中新增readyRefresh()方法,防止出"用尽的ResultSet" yjg
 */
public final class B_SingleOutLadding extends BaseAction implements Operate
{
  //2004-4-23 17:37 修改 VW_SALEOUTSTORE_SINGLE_LADDING加入了isrefer条件 yjg
  private static final String SINGLE_LADDING_SQL = "SELECT * FROM VW_OUTSALESTORE_SINGLE_LADDING WHERE fgsid= ";
  private static final String DETAIL_SQL         = "SELECT * FROM VW_OUTSALE_OUTSTORE WHERE tdId ='?'";//

  private EngineDataSet dsLadding  = new EngineDataSet();
  private EngineDataSet dsDtLadding  = new EngineDataSet();
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDtLadding, "kc_sfdjmxusetd.2");
  public  static final String SHOW_DETAIL            = "12500";//调用从表明细资料
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  public  String loginId   = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private User user = null; //定义一个存放用户信息的实例
  private String qtyFormat = null;
  private String sumFormat = null;

  public String srcFrm=null;           //传递的原form的名称
  public String multiIdInput = null;   //多选的ID组合串
  public boolean isMultiSelect = false;
  public String inputName[] = null;
  public String fieldName[] = null;
  public String methodName = null;
  private String dwtxid = null;
  private String storeid = null;

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  /**
   * 得到销售出库单引入提单主表信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售出库单引入提单主表信息的实例
   */
  public static B_SingleOutLadding getInstance(HttpServletRequest request)
  {
    B_SingleOutLadding singleOutLaddingBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "singleOutLaddingBean";
      singleOutLaddingBean = (B_SingleOutLadding)session.getAttribute(beanName);
      if(singleOutLaddingBean == null)
      {
        singleOutLaddingBean = new B_SingleOutLadding();
        LoginBean loginBean = LoginBean.getInstance(request);
        singleOutLaddingBean.loginId = loginBean.getUserID();
        singleOutLaddingBean.loginName = loginBean.getUserName();
        singleOutLaddingBean.loginDept = loginBean.getDeptID();

        singleOutLaddingBean.qtyFormat = loginBean.getQtyFormat();
        singleOutLaddingBean.sumFormat = loginBean.getSumFormat();

        singleOutLaddingBean.fgsid = loginBean.getFirstDeptID();
        singleOutLaddingBean.dsLadding.setColumnFormat("zsl", singleOutLaddingBean.qtyFormat);
        singleOutLaddingBean.dsLadding.setColumnFormat("zje", singleOutLaddingBean.sumFormat);
        session.setAttribute(beanName, singleOutLaddingBean);
      }
    }
    return singleOutLaddingBean;
  }

  /**
   * 构造函数
   */
  private B_SingleOutLadding()
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
    setDataSetProperty(dsLadding, null);
    setDataSetProperty(dsDtLadding, null);
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(SHOW_DETAIL, new Show_Detail());
    //addObactioner(String.valueOf(FIXED_SEARCH), new Search());
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
    if(dsLadding != null){
      dsLadding.close();
      dsLadding = null;
    }
    if(dsDtLadding != null){
      dsDtLadding.close();
      dsDtLadding = null;
    }
    if(detailProducer != null)
    {
      detailProducer.release();
      detailProducer = null;
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

      //2004-4-23 10:46 下面这样先判断rquest是否为空.然后再给值的原因.主要想防止,;因分页后造成的参数的丢失 yjg
      srcFrm = request.getParameter("srcFrm")==null?srcFrm:request.getParameter("srcFrm");//
      multiIdInput = request.getParameter("srcVar")==null?multiIdInput:request.getParameter("srcVar");//
      isMultiSelect = request.getParameter("multi") == null?isMultiSelect:request.getParameter("multi").equals("1");
      inputName = request.getParameterValues("srcVar")==null?inputName:request.getParameterValues("srcVar");
      fieldName = request.getParameterValues("fieldVar") == null?fieldName:request.getParameterValues("fieldVar");
      methodName = request.getParameter("method") == null?methodName:request.getParameter("method");//request.getParameter("method");

      //初始化查询项目和内容
      //initQueryItem(request);
      //fixedQuery.getSearchRow().clear();
      //替换可变字符串，组装SQL
      String SQL = SINGLE_LADDING_SQL + fgsid;
      String dwtxid = request.getParameter("dwtxid");
      String storeid = request.getParameter("storeid");
      String djlx = request.getParameter("djlx");
      djlx = djlx==null?"":djlx;
      if(!dwtxid.equals(""))
        SQL = SQL + " AND dwtxid= " + dwtxid;
      if(!storeid.equals(""))
        SQL = SQL + " AND storeid= " + storeid;
      if(!djlx.equals(""))
        SQL = SQL + " AND djlx= " + djlx;
      dsLadding.setQueryString(SQL);
      if(dsLadding.isOpen()){
        //2004-4-21 19:00 新增 在init类中新增readyRefresh()方法,防止出"用尽的ResultSet" yjg
        dsLadding.readyRefresh();
        dsLadding.refresh();
      }
       else
         dsLadding.openDataSet();
      detailProducer.init(request, loginId);
    }
  }
  /*
  *得到一行信息
  */
  public final RowMap getLookupRow(String tdid)
  {
    RowMap row = new RowMap();
    if(tdid == null || tdid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsLadding, "tdid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "tdid");
    locateRow.setValue(0, tdid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsLadding;
  }
  class Show_Detail implements Obactioner
 {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsLadding.goToRow(Integer.parseInt(data.getParameter("rownum")));
      //masterRow = dsLadding.getInternalRow();
      //打开从表
      openDetailTable(false);
    }
  }
  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){
    if(!dsDtLadding.isOpen())
      dsDtLadding.openDataSet();
    return dsDtLadding;
  }
  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    String tdid = dsLadding.getValue("tdid");
    //02.28 23:15  修改 将下面此句setQueryString中的sql由原来的手动用+号组成sql`改成现在用combineSQL来组成.
    //解决了当sdfdjid是空的时候会页面上(主要是contract_instore_bottom.jsp上)会出现sql错误的问题.yjg
    dsDtLadding.setQueryString(combineSQL(DETAIL_SQL, "?", new String[]{isMasterAdd ? "-1" : tdid}));
    if(dsDtLadding.isOpen()){
      dsDtLadding.readyRefresh();
      dsDtLadding.refresh();
    }
    else
      dsDtLadding.openDataSet();
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
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(DETAIL_SQL, "?", new String[]{fgsid, dwtxid, storeid, SQL});
      if(!dsDtLadding.getQueryString().equals(SQL))
      {
        dsDtLadding.setQueryString(SQL);
        dsDtLadding.setRowMax(null);
      }
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsDtLadding;
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("tdbh"), null, null, null),
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),//产品
        new QueryColumn(master.getColumn("tdrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("tdrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("cpbm"), null, null, null, null, "like"),//产品编码
        new QueryColumn(master.getColumn("product"), null, null, null, null, "like"),//产品
      });
      isInitQuery = true;
    }
  }
  public final String getFixedQueryValue(String col)
{
  return fixedQuery.getSearchRow().get(col);
  }
}



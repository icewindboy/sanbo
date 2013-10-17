package engine.erp.sale.xixing;

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
* <p>Title: 促销产品管理</p>
* <p>Copyright: right reserved (c) 2004</p>
* <p>Company: ENGINE</p>
* <p>Author: 胡康宁</p>
* @version 1.0
*/

public final class B_ProductPromotion extends BaseAction implements Operate
{

  private static final String CPFHDJ_SQL = "SELECT a.* FROM xs_promotion a,kc_dm b WHERE a.cpid=b.cpid  ? order by b.cpbm,a.dwtxid ";
  private static final String CPFHDJ_STRUCT_SQL = "SELECT a.* FROM xs_promotion a,kc_dm b WHERE  a.cpid=b.cpid and 1<>1 ORDER BY b.cpbm  ";//

  private static final String BATCH_SQL = "SELECT a.dwtxid FROM VW_SALE_KHXYED a WHERE a.dwtxid NOT IN(SELECT b.dwtxid FROM xs_promotion b WHERE b.cpid='?')";//

  private EngineDataSet dsCpfhdjTable = new EngineDataSet();//数据集
  private EngineDataSet dsBatchAddTable = new EngineDataSet();
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = false;
  private long    editrow = 0;
  public  String retuUrl = null;

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsCpfhdjTable, "xs_promotion");
  public  HtmlTableProducer table = new HtmlTableProducer(dsCpfhdjTable, "xs_promotion", "a");//查询得到数据库中配置的字段

  public  String loginid = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String fgsid = null;   //分公司ID
  public boolean isReport = false;
  public  boolean isApprove = false;     //是否在审批状态
  private ArrayList d_RowInfos = null; //多行记录的引用

  public  static final String SELECT_PRODUCT           = "9005";
  public  static final String SXZ_CHANGE               = "9006";
  public  static final String AFTER_SELECT_PRODUCT     = "9006";
  public  static final String AFTER_POST               = "9007";
  public  static final String REPORT                   = "9008";      //报表追踪
  public static final String BATCHINIT                 = "9009";
  public static final String BATCHPOST                 = "9010";
  public static final String BATCHADD                  = "9011";
  public  static final String PRODUCT_CHANGE           = "9012";
  private QueryFixedItem fixedQuery = new QueryFixedItem();//定义固定查询类
  public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
  private boolean isInitQuery = false;
  public String dwdm="";
  public String dwmc="";
  private User user = null;//登陆用户（设置用户部门权限）


  /**
   * 从会话中得到银行信用卡信息的实例
   * @param request jsp请求
   * @return 返回银行信用卡信息的实例
   */

  public static B_ProductPromotion getInstance(HttpServletRequest request)
  {
    B_ProductPromotion b_ProductPromotionBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_ProductPromotionBean";
      b_ProductPromotionBean = (B_ProductPromotion)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_ProductPromotionBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_ProductPromotionBean = new B_ProductPromotion();
        b_ProductPromotionBean.fgsid = loginBean.getFirstDeptID();
        b_ProductPromotionBean.loginid = loginBean.getUserID();
        b_ProductPromotionBean.user = loginBean.getUser();
        b_ProductPromotionBean.loginName = loginBean.getUserName();
        session.setAttribute(beanName, b_ProductPromotionBean);//加入到session中
      }
    }
    return b_ProductPromotionBean;
  }
  /**
   * 构造函数
   */
  private B_ProductPromotion()
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
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsCpfhdjTable, combineSQL(CPFHDJ_SQL,"?",new String[]{""}));
    setDataSetProperty(dsBatchAddTable, CPFHDJ_STRUCT_SQL);    //提取出全部数据
    //dsCpfhdjTable.setSequence(new SequenceDescriptor(new String[]{"cpfhdjid"}, new String[]{"s_xs_promotion"})); //设置主健的sequence
    dsCpfhdjTable.setTableName("xs_promotion");
    dsBatchAddTable.setTableName("xs_promotion");
    dsBatchAddTable.addLoadListener(new com.borland.dx.dataset.LoadListener() {
      public void dataLoaded(LoadEvent e)
      {
        initRowInfo(true);
      }
    });
    //dsCpfhdjTable.setSort(new SortDescriptor("", new String[]{"cpfhdjid"}, new boolean[]{false}, null, 0));
    //添加操作的触发对象
    Cpfhdj_Add_Edit add_edit = new Cpfhdj_Add_Edit();
    addObactioner(String.valueOf(ADD), add_edit);                  //新增
    addObactioner(String.valueOf(EDIT), add_edit);                 //修改
    addObactioner(String.valueOf(INIT), new Cpfhdj_Init());  //初始化 operate=0
    addObactioner(String.valueOf(POST), new Cpfhdj_Post());  //保存
    addObactioner(String.valueOf(DEL), new Cpfhdj_Del()); //删除

    addObactioner(String.valueOf(SELECT_PRODUCT), new Select_Product());
    addObactioner(String.valueOf(AFTER_SELECT_PRODUCT), new Select_Product());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());//查询
    addObactioner(String.valueOf(BATCHINIT), new Batch_Add_Init());

    addObactioner(String.valueOf(BATCHADD), new Batch_Add());
    addObactioner(String.valueOf(BATCHINIT), new Batch_Add_Init());
    addObactioner(String.valueOf(BATCHPOST), new Batch_Post());
    addObactioner(String.valueOf(PRODUCT_CHANGE), new Cpfhdj_Change());

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
      String operate = request.getParameter("operate");
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsCpfhdjTable.isOpen() && dsCpfhdjTable.changesPending())
        dsCpfhdjTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsCpfhdjTable != null){
      dsCpfhdjTable.close();
      dsCpfhdjTable = null;
    }
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
    }
    log = null;
    rowInfo = null;
  }
  public final EngineDataSet getBatchAddTable()
  {
    return dsBatchAddTable;
  }
  /**
   * 得到子类的类名
   * 实现BaseAction中的抽象方法
   * 日志中调用
   * @return 返回子类的类名
   */
  protected Class childClassName()
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
  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd)
      rowInfo.put(getOneTable());
  }
  /**
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    rowInfo.put(request);
    int rownum = dsBatchAddTable.getRowCount();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("dwtxid", rowInfo.get("dwtxid_"+i));//
      d_RowInfos.set(i,detailRow);
    }
  }
  /**
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   */
  private final void initRowInfo(boolean isInit)
  {
    if(d_RowInfos == null)
      d_RowInfos = new ArrayList(dsBatchAddTable.getRowCount());
    else if(isInit)
      d_RowInfos.clear();
    dsBatchAddTable.first();
    for(int i=0; i<dsBatchAddTable.getRowCount(); i++)
    {
      RowMap row = new RowMap(dsBatchAddTable);
      d_RowInfos.add(row);
      dsBatchAddTable.next();
    }
  }
  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    //if(!dsCpfhdjTable.isOpen())
    //dsCpfhdjTable.open();
    return dsCpfhdjTable;
  }
  /**
   *得到表的一行信息
   * */
  public final RowMap getRowinfo() {return rowInfo;}


  //------------------------------------------
  //操作实现的类:初始化;新增,修改,删除
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class Cpfhdj_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dwdm ="";
      dwmc ="";
      HttpServletRequest request = data.getRequest();
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      fixedQuery.getSearchRow().clear();
      masterProducer.init(request, loginid);
      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code.equals("1"))
        submitType=true;
      else
        submitType=false;

      String MSQL =  combineSQL(CPFHDJ_SQL, "?", new String[]{ ""});
      dsCpfhdjTable.setQueryString(MSQL);
      dsCpfhdjTable.setRowMax(null);
    }
  }
  /**
   * 选择新产品
   **/
  class Select_Product implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      rowInfo.put(data.getRequest());
      String tdhwid = rowInfo.get("seltdhwid");
      String wzdjid = dataSetProvider.getSequence("select wzdjid from xs_tdhw where tdhwid='"+tdhwid+"'");
      if(wzdjid!=null)
        rowInfo.put("wzdjid",wzdjid);
      if(action.equals(String.valueOf(SELECT_PRODUCT)))
        rowInfo.put("tdhwid",tdhwid);
      else
        rowInfo.put("xs__tdhwid",tdhwid);
    }
  }

  class Search implements Obactioner
  {
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<cardID >notifyObactioners</cardID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      table.getWhereInfo().setWhereValues(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if(!SQL.equals(""))
        SQL= " and "+SQL;
      SQL=combineSQL(CPFHDJ_SQL, "?", new String[]{SQL});
      dsCpfhdjTable.setQueryString(SQL);
      dsCpfhdjTable.setRowMax(null);
    }
}

/**
* 添加或修改操作的触发类
*/
class Cpfhdj_Add_Edit implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    isAdd = action.equals(String.valueOf(ADD));
    if(!isAdd)
    {
      dsCpfhdjTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      editrow = dsCpfhdjTable.getInternalRow();
    }
    initRowInfo(isAdd, true);
    //data.setMessage(showJavaScript("toDetail();"));
  }
}
/**
* 添加或修改操作的触发类
*/
class Cpfhdj_Change implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest request = data.getRequest();
    rowInfo.put(request);
    String cpid =rowInfo.get("cpid");
    String djlx =  rowInfo.get("djlx");
    String dj = dataSetProvider.getSequence("select "+djlx+" from xs_wzdj where cpid='"+cpid+"'");
    rowInfo.put("dj",dj);
    rowInfo.put("zk","");
    rowInfo.put("prom_price","");
    //initRowInfo(isAdd, true);
  }
}
/**批量增回**/
    class Batch_Add_Init implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest request = data.getRequest();
        rowInfo = new RowMap();

        dsBatchAddTable.setQueryString(CPFHDJ_STRUCT_SQL);
        dsBatchAddTable.setRowMax(null);
        if(dsBatchAddTable.isOpen() && dsBatchAddTable.getRowCount() > 0)
        dsBatchAddTable.empty();
      }
  }
  /**批量增回**/
  class Batch_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      rowInfo.put(request);
      String cpid =request.getParameter("cpid");
      String djlx =  rowInfo.get("djlx");
      String dj = dataSetProvider.getSequence("select "+djlx+" from xs_wzdj where cpid='"+cpid+"'");
      rowInfo.put("dj",dj);
      String SQL = combineSQL(BATCH_SQL, "?", new String[]{cpid});
      if(!dsBatchAddTable.getQueryString().equals(SQL))
      {
        dsBatchAddTable.setQueryString(SQL);
        dsBatchAddTable.setRowMax(null);
      }
    }
  }
  /**批量保存**/
    class Batch_Post implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest request = data.getRequest();
        rowInfo.put(request);

        String[] sel =request.getParameterValues("sel");
        String cpid =request.getParameter("cpid");
        String prom_price =request.getParameter("prom_price");
        String startdate =request.getParameter("startdate");
        String enddate =request.getParameter("enddate");
        String djlx =request.getParameter("djlx");
        String zk =request.getParameter("zk");

        if(sel==null||sel.length==0)
          return;
        if(checkNumber(prom_price, "促销单价") != null)
        {
          data.setMessage(showJavaScript("alert('促销单价无效!')"));
          return;
        }
        if(checkNumber(zk, "折扣") != null)
        {
          data.setMessage(showJavaScript("alert('折扣无效!')"));
          return;
        }
        if(sel==null||sel.length==0)
        {
          data.setMessage(showJavaScript("alert('请选择客户!')"));
          return;
        }
        String temp =startdate;
        if(temp.equals("") || !isDate(temp))
        {
          data.setMessage(showJavaScript("alert('非法日期!')"));
          return;
        }
        temp =enddate;
        if(temp.equals("") || !isDate(temp))
        {
          data.setMessage(showJavaScript("alert('非法日期!')"));
          return;
        }
        try{
        Date ksdate = new SimpleDateFormat("yyyy-MM-dd").parse(startdate);
        Date jsdate = new SimpleDateFormat("yyyy-MM-dd").parse(enddate);
        if(ksdate.compareTo(jsdate)>0){
          data.setMessage(showJavaScript("alert('非法日期!')"));
          return;
        }
        }catch(Exception e){}

        putDetailInfo(request);
        for(int i=0;i<sel.length;i++)
        {
          int j= Integer.parseInt(sel[i]);
          RowMap derow = (RowMap)d_RowInfos.get(j);
          dsCpfhdjTable.insertRow(false);
          dsCpfhdjTable.setValue("cpid",cpid);
          dsCpfhdjTable.setValue("dwtxid",derow.get("dwtxid"));
          String memo = rowInfo.get("memo");
          dsCpfhdjTable.setValue("startdate",rowInfo.get("startdate"));
          dsCpfhdjTable.setValue("enddate",rowInfo.get("enddate"));
          dsCpfhdjTable.setValue("prom_price",rowInfo.get("prom_price"));
          dsCpfhdjTable.setValue("memo",rowInfo.get("memo"));
          dsCpfhdjTable.setValue("zk",rowInfo.get("zk"));
          dsCpfhdjTable.setValue("djlx",rowInfo.get("djlx"));
          dsCpfhdjTable.post();
        }
        dsCpfhdjTable.saveChanges();
        dsCpfhdjTable.refresh();
      }
  }
  /**
   * 保存操作的触发类
   */
  class Cpfhdj_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      //t.wzdjid,t.tdhwid,t.deptid,t.dwtxid,t.xs__tdhwid,t.cpid,t.dj,t.fkqk,t.dmsxid
      //需判断数据表中是否有相同的往来单位，产品，规格属性，部门，分公司
      rowInfo.put(data.getRequest());


      String dwtxid = rowInfo.get("dwtxid");
      String cpid = rowInfo.get("cpid");
      String prom_price = rowInfo.get("prom_price");
      String startdate = rowInfo.get("startdate");
      String enddate = rowInfo.get("enddate");
      String memo = rowInfo.get("memo");
      String djlx =rowInfo.get("djlx");
      String zk =rowInfo.get("zk");
      //String startdate = rowInfo.get("startdate");
      String temp = "";
      if(dwtxid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入购货单位!')"));
        return;
      }
      if(djlx.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择定价类型!')"));
        return;
      }
      if(startdate.equals(""))
      {
        data.setMessage(showJavaScript("alert('开始时间不能空!')"));
        return;
      }
      if(enddate.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入结束时间!')"));
        return;
      }
      if(cpid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入产品!')"));
        return;
      }
      if(checkNumber(zk, "折扣") != null)
      {
        data.setMessage(showJavaScript("alert('折扣无效!')"));
        return;
      }
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_promotion t WHERE t.dwtxid='"+dwtxid+"'  AND t.cpid='"+cpid+"'");
      if(count!=null&&!count.equals("0"))
      {
        if(isAdd||!count.equals("1"))
        {
          data.setMessage(showJavaScript("alert('往来单位，产品不能重复!')"));
          return;
        }
      }
      if((temp = checkNumber(prom_price, "促销单价")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if(isAdd)
      {
        ds.insertRow(false);
        isAdd=false;
      }
      else
        ds.goToInternalRow(editrow);
      ds.setValue("prom_price", prom_price);
      ds.setValue("startdate", startdate);
      ds.setValue("dwtxid", dwtxid);
      ds.setValue("cpid", cpid);
      ds.setValue("enddate", enddate);
      ds.setValue("memo", memo);
      ds.setValue("djlx", djlx);
      ds.setValue("zk", zk);

      ds.post();
      ds.saveChanges();
      editrow = ds.getInternalRow();
      //data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class Cpfhdj_Del implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
    }
  }
}
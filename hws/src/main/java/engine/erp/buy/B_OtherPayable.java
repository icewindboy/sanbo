package engine.erp.buy;

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
* <p>Title: 其他应付款</p>
* <p>Copyright: right reserved (c) 2004</p>
* <p>Company: ENGINE</p>
* <p>Author: 杨来</p>
* @version 1.0
*/

public final class B_OtherPayable extends BaseAction implements Operate
{

  private static final String Balance_SQL = "SELECT * FROM cg_other_fund  WHERE 1=1 and ? AND fgsid=? ?  order by otherfundno desc ";
  private static final String EDIT_SQL = "SELECT * FROM cg_other_fund  WHERE otherfundid=? order by otherfundno desc ";
  private static final String Balance_STRUCT_SQL = "SELECT * FROM cg_other_fund  WHERE 1<>1 order by otherfundno desc ";//
  private EngineDataSet dsPayableTable = new EngineDataSet();//数据集
  private RowMap m_RowInfo = new RowMap();
  public  boolean isAdd = false;
  private long    editrow = 0;
  public  String retuUrl = null;
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsPayableTable, "cg_other_fund");
  //public  HtmlTableProducer table = new HtmlTableProducer(dsPayableTable, "cg_other_fund", "a");//查询得到数据库中配置的字段
  public  String loginid = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String fgsid = null;   //分公司ID
  public boolean isReport = false;
  public  boolean isApprove = false;     //是否在审批状态
  private ArrayList d_RowInfos = null; //多行记录的引用
  public  static final String SALE_OVER                = "9003";          //完成
  public  static final String SELECT_PRODUCT           = "9005";
  public  static final String SXZ_CHANGE               = "9006";
  public  static final String AFTER_SELECT_PRODUCT     = "9006";
  public  static final String AFTER_POST               = "9007";
  public  static final String REPORT                   = "9008";      //报表追踪
  public static final String BATCHINIT                 = "9009";
  public static final String BATCHPOST                 = "9010";
  public  static final String DWTXID_CHANGE            = "1007";
  public  static final String CANCER_APPROVE            = "1004";
  public  static final String PRODUCT_CHANGE           = "9012";
  private QueryFixedItem fixedQuery = new QueryFixedItem();//定义固定查询类
  public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
  private boolean isInitQuery = false;
  public String dwdm="";
  public String dwmc="";
  private User user = null;//登陆用户（设置用户部门权限）
  private String otherfundid = null;
  public String []zt;
  /**
   * 从会话中得到银行信用卡信息的实例
   * @param request jsp请求
   * @return 返回银行信用卡信息的实例
   */

  public static B_OtherPayable getInstance(HttpServletRequest request)
  {
    B_OtherPayable otherPayableBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "otherPayableBean";
      otherPayableBean = (B_OtherPayable)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(otherPayableBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        otherPayableBean = new B_OtherPayable();
        otherPayableBean.fgsid = loginBean.getFirstDeptID();
        otherPayableBean.loginid = loginBean.getUserID();
        otherPayableBean.user = loginBean.getUser();
        otherPayableBean.loginName = loginBean.getUserName();
        session.setAttribute(beanName, otherPayableBean);//加入到session中
      }
    }
    return otherPayableBean;
  }
  /**
   * 构造函数
   */
  private B_OtherPayable()
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
    setDataSetProperty(dsPayableTable, combineSQL(Balance_SQL,"?",new String[]{""}));
    dsPayableTable.setSequence(new SequenceDescriptor(new String[]{"otherfundid"}, new String[]{"S_CG_OTHER_FUND"})); //设置主健的sequence
    dsPayableTable.setTableName("cg_other_fund");
    //dsPayableTable.setSort(new SortDescriptor("", new String[]{"otherfundid"}, new boolean[]{false}, null, 0));
    //添加操作的触发对象
    Balance_Add_Edit add_edit = new Balance_Add_Edit();
    addObactioner(String.valueOf(ADD), add_edit);                  //新增
    addObactioner(String.valueOf(EDIT), add_edit);                 //修改
    addObactioner(String.valueOf(INIT), new Balance_Init());  //初始化 operate=0
    addObactioner(String.valueOf(POST), new Balance_Post());  //保存
    addObactioner(String.valueOf(DEL), new Balance_Del()); //删除
    addObactioner(String.valueOf(SELECT_PRODUCT), new Select_Product());
    addObactioner(String.valueOf(AFTER_SELECT_PRODUCT), new Select_Product());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());//查询
    addObactioner(String.valueOf(PRODUCT_CHANGE), new Balance_Change());
    addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());

    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(REPORT), new Approve());

    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());
    addObactioner(String.valueOf(SALE_OVER), new Over());//完成
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
      if(dsPayableTable.isOpen() && dsPayableTable.changesPending())
        dsPayableTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsPayableTable != null){
      dsPayableTable.close();
      dsPayableTable = null;
    }
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
    }
    log = null;
    m_RowInfo = null;
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
    if(isInit && m_RowInfo.size() > 0)
      m_RowInfo.clear();
    if(!isAdd)
      m_RowInfo.put(getOneTable());
    else
    {
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String otherfundno="";
      m_RowInfo.put("czrq", today);//制单日期
      m_RowInfo.put("czy", loginName);//操作员
      m_RowInfo.put("kdrq", today);
      m_RowInfo.put("czyid", loginid);
      m_RowInfo.put("otherfunddate", today);
      m_RowInfo.put("zt", "0");
      otherfundno = dataSetProvider.getSequence("SELECT pck_base.billNextCode('cg_other_fund','otherfundno') from dual");
      m_RowInfo.put("otherfundno", otherfundno);
      m_RowInfo.put("jbr", loginName);
      m_RowInfo.put("fgsid", fgsid);
    }
  }
  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    //if(!dsPayableTable.isOpen())
    //dsPayableTable.open();
    return dsPayableTable;
  }
  /**
   *得到表的一行信息
   * */
  public final RowMap getRowinfo() {return m_RowInfo;}


  //------------------------------------------
  //操作实现的类:初始化;新增,修改,删除
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class Balance_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove= false;
      dwdm ="";
      dwmc ="";
      zt = new String[]{""};
      HttpServletRequest request = data.getRequest();
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      fixedQuery.getSearchRow().clear();
      masterProducer.init(request, loginid);
      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code!=null&&code.equals("1"))
        submitType=true;
      else
        submitType=false;


      String SQL = " AND zt<>8   AND zt<>4 ";
      String MSQL =  combineSQL(Balance_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsPayableTable.setQueryString(MSQL);
      dsPayableTable.setRowMax(null);


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
      m_RowInfo.put(data.getRequest());
      String tdhwid = m_RowInfo.get("seltdhwid");
      String wzdjid = dataSetProvider.getSequence("select wzdjid from xs_tdhw where tdhwid='"+tdhwid+"'");
      if(wzdjid!=null)
        m_RowInfo.put("wzdjid",wzdjid);
      if(action.equals(String.valueOf(SELECT_PRODUCT)))
        m_RowInfo.put("tdhwid",tdhwid);
      else
        m_RowInfo.put("xs__tdhwid",tdhwid);
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
      HttpServletRequest request = data.getRequest();
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;

      zt = data.getRequest().getParameterValues("zt");
      if(!(zt==null))
      {
        StringBuffer sbzt = null;
        for(int i=0;i<zt.length;i++)
        {
          if(sbzt==null)
            sbzt= new StringBuffer(" AND zt IN(");
          sbzt.append(zt[i]+",");
        }
        if(sbzt == null)
          sbzt =new StringBuffer();
        else
          sbzt.append("-99)");
        SQL = SQL+sbzt.toString();
      }
      else
        zt = new String[]{""};

      String MSQL = combineSQL(Balance_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      if(!dsPayableTable.getQueryString().equals(MSQL))
      {
        dsPayableTable.setQueryString(MSQL);
        dsPayableTable.setRowMax(null);//以便dbNavigator刷新数据集
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
      EngineDataSet master = dsPayableTable;
      if(!master.isOpen())
        master.open();//打开主表数据集
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("otherfundno"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("otherfunddate"), null, null, null, "a", ">="),//制单日期
        new QueryColumn(master.getColumn("otherfunddate"), null, null, null, "b", "<="),//制单日期
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门id
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("sprid"), "emp", "personid", "xm", "xm", "like"),//从表品名
        new QueryColumn(master.getColumn("czy"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("jbr"), null, null, null, null, "like")
      });
      isInitQuery = true;//初始化完成
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
      dsPayableTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsPayableTable.getValue("otherfundno");
      String deptid = dsPayableTable.getValue("deptid");
      String otherfundid = dsPayableTable.getValue("otherfundid");
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM cg_other_fund a WHERE a.otherfundid='"+otherfundid+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        dsPayableTable.readyRefresh();
        return;
      }
      approve.putAproveList(dsPayableTable, dsPayableTable.getRow(),"other_payable_aprove", content,deptid);
    }
  }
  //&#$
  /**
   * 审批操作的触发类
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd=false;
      String id=null;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginid);
      if(String.valueOf(REPORT).equals(action))
      {
        isReport=true;
        isApprove = false;
        id=request.getParameter("id");
      }else
      {
        isApprove = true;
        id = data.getParameter("id", "");
      }
      String sql = combineSQL(EDIT_SQL, "?", new String[]{id});
      dsPayableTable.setQueryString(sql);
      if(dsPayableTable.isOpen())
        dsPayableTable.readyRefresh();
      dsPayableTable.refresh();

      otherfundid = id;
      initRowInfo(false,false);
    }
  }
  /**
   * 取消审批
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsPayableTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      String otherfundid = dsPayableTable.getValue("otherfundid");
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM cg_other_fund a WHERE a.otherfundid='"+otherfundid+"'");
      if(zt!=null&&!zt.equals("1"))
      {
        dsPayableTable.readyRefresh();
        return;
      }
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsPayableTable,dsPayableTable.getRow(),"other_payable_aprove");
    }
  }
  /**
   * 作废
   */
  class Cancer implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String otherfundid = data.getParameter("rownum");

      String zt = dataSetProvider.getSequence("SELECT a.zt FROM cg_other_fund a WHERE a.otherfundid='"+otherfundid+"'");
      if(zt!=null&&(zt.equals("8")||zt.equals("9")||zt.equals("4")))
      {
        dsPayableTable.readyRefresh();
        return;
      }

      dsPayableTable.setQueryString(combineSQL(EDIT_SQL,"?",new String[]{otherfundid}));
      if(!dsPayableTable.isOpen())
        dsPayableTable.openDataSet();
      else
        dsPayableTable.refresh();
      dsPayableTable.setValue("zt", "4");
      dsPayableTable.saveChanges();
      dsPayableTable.readyRefresh();
    }
  }
  /**
   * 完成
   */
  class Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsPayableTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      dsPayableTable.setValue("zt","8");
      dsPayableTable.saveChanges();
    }
  }
/**
* 添加或修改操作的触发类
*/
class Balance_Add_Edit implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    isApprove= false;
    isAdd = action.equals(String.valueOf(ADD));
    if(!isAdd)
    {
      dsPayableTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      editrow = dsPayableTable.getInternalRow();
    }
    initRowInfo(isAdd, true);
    //data.setMessage(showJavaScript("toDetail();"));
  }
}
/**
* 添加或修改操作的触发类
*/
class Balance_Change implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest request = data.getRequest();
    m_RowInfo.put(request);
    String personid =m_RowInfo.get("personid");
    String djlx =  m_RowInfo.get("djlx");
    String dj = dataSetProvider.getSequence("select "+djlx+" from xs_wzdj where personid='"+personid+"'");
    m_RowInfo.put("dj",dj);
    m_RowInfo.put("zk","");
    m_RowInfo.put("prom_price","");
    //initRowInfo(isAdd, true);
  }
}
  /**
   * 保存操作的触发类
   */
  class Balance_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      //t.otherfundid t.personid t.dwtxid t.deptid t.jsfsid t.otherfundno t.otherfunddate  t.otherfunditem t.otherfund t.khlx t.jbr t.bz
      //t.czrq t.czy t.czyid t.fgsid t.custaddr

      //需判断数据表中是否有相同的往来单位，产品，规格属性，部门，分公司
      m_RowInfo.put(data.getRequest());
      String dwtxid = m_RowInfo.get("dwtxid");
      String personid = m_RowInfo.get("personid");
      String deptid = m_RowInfo.get("deptid");
      String jsfsid = m_RowInfo.get("jsfsid");
      String otherfundno = m_RowInfo.get("otherfundno");
      String otherfunddate = m_RowInfo.get("otherfunddate");
      String otherfunditem =m_RowInfo.get("otherfunditem");
      String otherfund =m_RowInfo.get("otherfund");
      String khlx =m_RowInfo.get("khlx");
      String jbr =m_RowInfo.get("jbr");
      String temp = "";
      if(dwtxid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入客户!')"));
        return;
      }
      if(personid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择业务员!')"));
        return;
      }
      if(jbr.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择经办人!')"));
        return;
      }
      if(deptid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择部门!')"));
        return;
      }
      if(otherfunddate.equals(""))
      {
        data.setMessage(showJavaScript("alert('开始时间不能空!')"));
        return;
      }
      if(checkNumber(otherfund, "金额") != null)
      {
        data.setMessage(showJavaScript("alert('金额无效!')"));
        return;
      }
      if(jsfsid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择结算方式!')"));
        return;
      }
      if(khlx.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择客户类型!')"));
        return;
      }

      if(isAdd)
      {
        //String otherfundno = m_RowInfo.get("otherfundno");
        String count = dataSetProvider.getSequence("select count(*) from cg_other_fund t where t.otherfundno='"+otherfundno+"'");
        if(!count.equals("0"))
        {
          otherfundno = dataSetProvider.getSequence("SELECT pck_base.billNextCode('cg_other_fund','otherfundno') from dual");
        }
        ds.insertRow(false);
        otherfundid = dataSetProvider.getSequence("s_cg_other_fund");
        ds.setValue("otherfundid", otherfundid);//主健
        ds.setValue("zt","0");
        ds.setValue("otherfundno",otherfundno);
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("czyid", loginid);
        ds.setValue("czy", loginName);//操作员
        ds.setValue("fgsid", fgsid);//分公司
        isAdd=false;
      }
      else
        ds.goToInternalRow(editrow);
      ds.setValue("khlx", khlx);
      ds.setValue("deptid", deptid);
      ds.setValue("dwtxid", dwtxid);
      ds.setValue("personid", personid);
      ds.setValue("otherfundno", otherfundno);
      ds.setValue("jsfsid", jsfsid);
      ds.setValue("otherfunddate", otherfunddate);
      ds.setValue("otherfunditem", otherfunditem);
      ds.setValue("otherfund", otherfund);
      ds.setValue("jsfsid", jsfsid);
      ds.setValue("jbr", jbr);
      ds.setValue("bz",m_RowInfo.get("bz"));
      ds.setValue("custaddr",m_RowInfo.get("custaddr"));
      ds.post();
      ds.saveChanges();
      editrow = ds.getInternalRow();
      //data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }
  /**
   *改变购货单位时引发的操作
   * */
  class Dwtxid_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP);
      String olddwtxId=m_RowInfo.get("dwtxid");
      m_RowInfo.put(data.getRequest());
      String dwtxid=m_RowInfo.get("dwtxid");
      RowMap corRow = corpBean.getLookupRow(dwtxid);
      if(olddwtxId.equals(dwtxid))
        return;
      else
      {
        m_RowInfo.put("dwtxid",dwtxid);
        m_RowInfo.put("kdrq",req.getParameter("kdrq"));
        m_RowInfo.put("otherfundno",req.getParameter("otherfundno"));
        m_RowInfo.put("personid",corRow.get("personid"));
        m_RowInfo.put("deptid",corRow.get("deptid"));
      }
    }
  }

  /**
   * 删除操作的触发类
   */
  class Balance_Del implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      if(!isAdd)
      {
        ds.goToInternalRow(editrow);
        //ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
        ds.deleteRow();
        ds.saveChanges();
      }
      data.setMessage(showJavaScript("backList();"));
    }
  }
}
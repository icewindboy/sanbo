package engine.erp.sale.shengyu;


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
 * <p>Title: 新产品发货登记表</p>
 * <p>Copyright: right reserved (c) 2004</p>
 * <p>Company: ENGINE</p>
 * <p>Author: 胡康宁</p>
 * @version 1.0
 */

public final class B_NewProductRegister extends BaseAction implements Operate
{
  private static final String CPFHDJ_SQL = "SELECT a.* FROM xs_cpfhdj a,kc_dm b WHERE a.cpid=b.cpid and a.fgsid='?' ? order by a.djh desc, b.cpbm desc";
  //private static final String CPFHDJ_SQL = "SELECT a.* FROM xs_cpfhdj a WHERE a.fgsid='?' ? ";
  private static final String APPROVE_SQL = "SELECT * FROM xs_cpfhdj WHERE cpfhdjid='?' ";//用于审批时候提取一条记录
  private EngineDataSet dsCpfhdjTable = new EngineDataSet();//数据集
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = false;
  private long    editrow = 0;
  public  String retuUrl = null;

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsCpfhdjTable, "xs_cpfhdj");
  public  HtmlTableProducer table = new HtmlTableProducer(dsCpfhdjTable, "xs_cpfhdj", "a");//查询得到数据库中配置的字段

  public  String loginid = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String fgsid = null;   //分公司ID
  public boolean isReport = false;
  public  boolean isApprove = false;     //是否在审批状态
  private String cpfhdjid = null;
  public  static final String CANCER_APPROVE           = "9002";
  public  static final String OVER                     = "9003";          //完成
  public  static final String SALE_CANCER              = "9004";          //作废
  public  static final String SELECT_PRODUCT           = "9005";
  public  static final String SXZ_CHANGE               = "9006";
  public  static final String AFTER_SELECT_PRODUCT     = "9006";
  public  static final String AFTER_POST               = "9007";
  public  static final String DWTXID_CHANGE   = "1007";
  public  static final String REPORT                   = "9008";      //报表追踪
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

  public static B_NewProductRegister getInstance(HttpServletRequest request)
  {
    B_NewProductRegister b_NewProductRegisterBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_NewProductRegisterBean";
      b_NewProductRegisterBean = (B_NewProductRegister)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_NewProductRegisterBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_NewProductRegisterBean = new B_NewProductRegister();
        b_NewProductRegisterBean.fgsid = loginBean.getFirstDeptID();
        b_NewProductRegisterBean.loginid = loginBean.getUserID();
        b_NewProductRegisterBean.user = loginBean.getUser();
        b_NewProductRegisterBean.loginName = loginBean.getUserName();
        session.setAttribute(beanName, b_NewProductRegisterBean);//加入到session中
      }
    }
    return b_NewProductRegisterBean;
  }
  /**
   * 构造函数
   */
  private B_NewProductRegister()
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
    setDataSetProperty(dsCpfhdjTable, combineSQL(CPFHDJ_SQL,"?",new String[]{fgsid,""}));                        //提取出全部数据
    //dsCpfhdjTable.setSequence(new SequenceDescriptor(new String[]{"cpfhdjid"}, new String[]{"s_xs_cpfhdj"})); //设置主健的sequence
    dsCpfhdjTable.setSequence(new SequenceDescriptor(new String[]{"djh"}, new String[]{"SELECT pck_base.billNextCode('xs_cpfhdj','djh') from dual"}));
    dsCpfhdjTable.setTableName("xs_cpfhdj");
    //dsCpfhdjTable.setSort(new SortDescriptor("", new String[]{"cpfhdjid"}, new boolean[]{false}, null, 0));
    //添加操作的触发对象
    Cpfhdj_Add_Edit add_edit = new Cpfhdj_Add_Edit();
    addObactioner(String.valueOf(ADD), add_edit);                  //新增
    addObactioner(String.valueOf(EDIT), add_edit);                 //修改
    addObactioner(String.valueOf(INIT), new Cpfhdj_Init());  //初始化 operate=0
    addObactioner(String.valueOf(POST), new Cpfhdj_Post());  //保存
    addObactioner(String.valueOf(DEL), new Cpfhdj_Del()); //删除
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());//取消审批
    addObactioner(String.valueOf(SALE_CANCER), new Cancer());//作废
    addObactioner(String.valueOf(OVER), new Over());//完成
    addObactioner(String.valueOf(SELECT_PRODUCT), new Select_Product());
    addObactioner(String.valueOf(AFTER_SELECT_PRODUCT), new Select_Product());
    addObactioner(String.valueOf(AFTER_POST), new After_Post());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());//查询
    addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());
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
    else
    {
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      rowInfo.put("czrq", today);//制单日期
      rowInfo.put("czy", loginName);//操作员
      rowInfo.put("czyid", loginid);
      rowInfo.put("zt", "0");
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
      isReport = false;
      isApprove = false;
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

      String MSQL =  combineSQL(CPFHDJ_SQL, "?", new String[]{fgsid, ""});
      dsCpfhdjTable.setQueryString(MSQL);
      dsCpfhdjTable.setRowMax(null);
    }
  }
  /**
   * 审批操作的触发类
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginid);
      //得到request的参数,值若为null, 则用""代替
      String id = null;
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
      String sql = combineSQL(APPROVE_SQL, "?", new String[]{id});
      dsCpfhdjTable.setQueryString(sql);
      if(dsCpfhdjTable.isOpen())
        dsCpfhdjTable.readyRefresh();
      dsCpfhdjTable.refresh();
      initRowInfo(false, true);
    }
  }
  /**
   * 完成
   */
  class Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsCpfhdjTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      cpfhdjid = dsCpfhdjTable.getValue("cpfhdjid");
      dsCpfhdjTable.setValue("zt","8");
      dsCpfhdjTable.saveChanges();
    }
  }
  /**
   * 作废
   */
  class Cancer implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsCpfhdjTable.goToRow(row);
      dsCpfhdjTable.setValue("zt", "4");
      dsCpfhdjTable.saveChanges();
    }
  }
  /**
   * 取消审批
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsCpfhdjTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsCpfhdjTable,dsCpfhdjTable.getRow(),"newproduct_register");
    }
  }
  /**
   * 添加到审核列表的操作类
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsCpfhdjTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      cpfhdjid = dsCpfhdjTable.getValue("cpfhdjid");
      String content = dsCpfhdjTable.getValue("djh");
      String deptid = dsCpfhdjTable.getValue("deptid");
      approve.putAproveList(dsCpfhdjTable, dsCpfhdjTable.getRow(), "newproduct_register", content,deptid);
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
      SQL=combineSQL(CPFHDJ_SQL, "?", new String[]{fgsid,SQL});
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
      isReport = false;
      isApprove = false;
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
   *改变购货单位时引发的操作
   * */
  class Dwtxid_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP);
      rowInfo.put(data.getRequest());//保存输入的明细信息
      String dwtxid=rowInfo.get("dwtxid");
      RowMap corRow = corpBean.getLookupRow(dwtxid);
      rowInfo.put("personid",corRow.get("personid"));
      rowInfo.put("deptid",corRow.get("deptid"));
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
      String wzdjid = rowInfo.get("wzdjid");
      String deptid = rowInfo.get("deptid");
      String dwtxid = rowInfo.get("dwtxid");
      String cpid = rowInfo.get("cpid");
      String dj = rowInfo.get("dj");
      String dmsxid = rowInfo.get("dmsxid");
      String jhsl = rowInfo.get("jhsl");
      String temp = "";
      if(dwtxid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入购货单位!')"));
        return;
      }
      if(deptid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入部门!')"));
        return;
      }
      if(cpid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入产品!')"));
        return;
      }
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_cpfhdj t WHERE t.dwtxid='"+dwtxid+"' AND t.deptid='"+deptid+"' AND t.cpid='"+cpid+"' AND t.dmsxid='"+dmsxid+"'  AND t.fgsid='"+fgsid+"' ");
      if(count!=null&&!count.equals("0"))
      {
        if(isAdd||!count.equals("1"))
        {
          data.setMessage(showJavaScript("alert('往来单位，产品，规格属性，部门四者不能重复!')"));
          return;
        }
      }
      if(dj.equals(""))
      {
        data.setMessage(showJavaScript("alert('单价不能空!')"));
        return;
      }
      if(jhsl.equals(""))
      {
        data.setMessage(showJavaScript("alert('计划数量不能空!')"));
        return;
      }
      if((temp = checkNumber(dj, "单价")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if((temp = checkNumber(jhsl, "计划数量")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if(isAdd)
      {
        String ncount = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_cpfhdj t WHERE t.dwtxid='"+dwtxid+"' AND t.deptid='"+deptid+"' AND t.cpid='"+cpid+"'  AND t.fgsid='"+fgsid+"' ");
        if(ncount!=null&&ncount.equals("1"))
        {
          data.setMessage(showJavaScript("alert('往来单位，产品，部门不能重复!')"));
          return;
        }
        ds.insertRow(false);
        cpfhdjid = dataSetProvider.getSequence("s_xs_cpfhdj");
        ds.setValue("cpfhdjid",cpfhdjid);
        ds.setValue("zt","0");
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("czyid", loginid);
        ds.setValue("czy", loginName);//操作员
        ds.setValue("fgsid", fgsid);//分公司
        isAdd=false;

      }
      else
        ds.goToInternalRow(editrow);
      ds.setValue("wzdjid", wzdjid);
      ds.setValue("deptid", deptid);
      ds.setValue("dwtxid", dwtxid);
      ds.setValue("cpid", cpid);
      ds.setValue("dj", dj);
      ds.setValue("dmsxid", dmsxid);
      ds.setValue("jhsl", jhsl);
      ds.post();
      ds.saveChanges();
      editrow = ds.getInternalRow();
      //data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }
  /**
   * 保存操作的触发类
   */
  class After_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      rowInfo.put(data.getRequest());
      String tdhwid = rowInfo.get("tdhwid");
      String xs__tdhwid = rowInfo.get("xs__tdhwid");
      String fkqk = rowInfo.get("fkqk");
      dsCpfhdjTable.setValue("xs__tdhwid", xs__tdhwid);
      dsCpfhdjTable.setValue("tdhwid", tdhwid);
      dsCpfhdjTable.setValue("fkqk", fkqk);
      dsCpfhdjTable.post();
      dsCpfhdjTable.saveChanges();
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



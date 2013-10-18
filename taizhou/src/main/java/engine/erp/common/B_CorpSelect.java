package engine.erp.common;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineDataView;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.common.User;
import engine.project.*;
import engine.html.HtmlTableProducer;
import engine.util.log.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
/**
 * <p>Title: 基础信息维护--往来单位选择窗体类</p>
 * <p>Description: 基础信息维护--往来单位选择窗体类<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

/**
 * 选择往来单位的用法：<br>
 * <textarea rows="25" name="S2" cols="120">
 * 单选往来单位的html代码：
 * <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=row.get("dwtxid")%>'>
   <input type="text" name="dwmc" value='单位名称' style="width:365" class="edline" readonly>
   <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc','fieldVar=dwtxid&fieldVar=dwmc',form1.dwtxid.value)">
   <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';">
   javascript函数CustSingleSelect的参数的说明：
   1.form1：表单的name或id
   2.srcVar=dwtxid&srcVar=dwmc:需要回填输入框控件的name
   3.srcVar=dwtxid&srcVar=dwmc:与上面相对应的字段名称
   4.form1.dwtxid.value:当前往来单位ID
   多选往来单位的html代码：
   //将marketactionBean.notin变量推入到session中保存
   session.setAttribute("notin", marketactionBean.notin);(notin的值："SELECT dwtxid FROM xs_schdkh WHERE schdid=")
   ...
   <INPUT TYPE="HIDDEN" NAME="multiIdInput" VALUE="" onchange="submitForm('<%=Operate.CUST_MULTI_SELECT%>')">
   <input name="image" class="img" type="image" title="新增" onClick="CustMultiSelect('form1','srcVar=multiIdInput','','notin')" src="../images/add.gif" border="0">
   多选后，将调用multiIdInput的onchange函数
   javascript函数CustMultiSelect的参数的说明：
   1.form1：表单的name或id
   2.srcVar=multiIdInput:需要回填输入框控件的name。选中多个单位时,将改变该控件的值。格式：单位id1,单位id2,单位id3。
   3.notin:marketactionBean.notin
   4.form1.dwtxid.value:当前往来单位ID保存在session中的key。
 * </textarea>
 */
public final class B_CorpSelect extends BaseAction implements Operate
{
  public  static final String AREA_CHANGE = "10001";

  //private String CORP_STRUCT_SQL = "SELECT dwtxid,dwdm,dwmc,dqh,addr,tel,lxr,zjm,email,cz,khh,zh,nsrdjh FROM dwtx WHERE 1<>1";
  private static final String CORP_SQL
      = "SELECT t.dwtxid, t.dwdm, t.dwmc, t.dqh, t.addr, t.tel, t.lxr, t.zjm, t.email, t.cz, t.khh, t.zh, "
      + " t.zp, t.nsrdjh, t.yfkje, t.yskje, t.personid, t.deptid, t.yfdj, e.xm "
      + "FROM dwtx t, emp e WHERE t.personid = e.personid(+) "
      //+ "AND @ t.dwtxid IN (SELECT l.dwtxid FROM dwtx_lx l WHERE l.ywlx IN (@)) "
      + "AND @ 1=1 "
      + "AND (@ OR t.deptid IS NULL) AND t.isdelete=0 AND t.fgsid=@ "
      + "ORDER BY t.dwdm";

  private EngineDataSet dsOneTable = new EngineDataSet();//保存类别树状信息的数据集
  public  HtmlTableProducer table = new HtmlTableProducer(dsOneTable, "corp_select", "t");

  public String[] inputName = null;
  public String[] fieldName = null;
  public String   srcFrm=null;
  public boolean  isMultiSelect = false;//是否是多选的
  public String   multiIdInput = null;  //多选的ID组合串

  private String  methodName = null;   //调用window.opener中的方法

  private String  fgsid = null;        //分公司ID
  private User    currUser = null;     //当前登录用户对象

  private String  corpSQL = CORP_SQL;
  private String  custType = "1";
  /**
   * 得到往来单位信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回往来单位信息的实例
   */
  public static B_CorpSelect getInstance(HttpServletRequest request)
  {
    B_CorpSelect corpSelectBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "corpSelectBean";
      corpSelectBean = (B_CorpSelect)session.getAttribute(beanName);
      if(corpSelectBean == null)
      {
        corpSelectBean = new B_CorpSelect(LoginBean.getInstance(request).getUser());
        session.setAttribute(beanName, corpSelectBean);
      }
    }
    return corpSelectBean;
  }

  /**
   * 构造函数
   */
  public B_CorpSelect(User user)
  {
    this.currUser = user;
    this.fgsid = user.getFilialeId();
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  /**
   * Implement this engine.project.OperateCommon abstract method
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsOneTable, null);

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
    addObactioner(String.valueOf(CUST_CHANGE), new CodeSearch());
    addObactioner(String.valueOf(CUST_NAME_CHANGE), new NameSearch());
    addObactioner(AREA_CHANGE, new AreaChange());
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
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsOneTable != null)
    {
      dsOneTable.closeDataSet();
      dsOneTable = null;
    }
    super.deleteObservers();
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
   * 初始化表单数据及其SQL语句
   * @return 返回子类的类名
   */
  private void init(RunData data) throws Exception
  {
    table.getWhereInfo().clearWhereValues();
    String ywlx = data.getParameter("ywlx","");
    if(ywlx.length() == 0)
      throw new Exception("错误的客户类型");
    custType = ywlx;
    //得到关闭窗体前要调用的方法
    methodName = data.getParameter("method");
    //剔除不需要出现的产品记录
    String method = data.getParameter("notin");
    corpSQL = CORP_SQL;
    if(method != null && !method.equals(""))
    {
      HttpSession session = data.getRequest().getSession();
      String notin = (String)session.getAttribute(method);
      if(notin != null)
      {
        session.removeAttribute(method);
        corpSQL = BaseAction.combineSQL(corpSQL, "@", new String[]{"t.dwtxid NOT IN ("+ notin +") AND @ "});
      }
    }
    srcFrm = data.getParameter("srcFrm");
    //是否是多选
    String multi = data.getParameter("multi");
    isMultiSelect = multi!=null && multi.equals("1");
    if(isMultiSelect)
      multiIdInput = data.getParameter("srcVar");
    else
    {
      inputName = data.getParameterValues("srcVar");
      fieldName = data.getParameterValues("fieldVar");
      /*String curID = request.getParameter("curID");
      if(curID!= null)
      {
        curID = curID.trim();
        try{
          Integer.parseInt(curID);
          SQL = SQL==null ? (PRODUCT_SQL +" WHERE cpid="+ curID) : (" WHERE cpid="+ curID);
        }catch(Exception ex){}
      }*/
    }
  }

  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  final class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      init(data);
      table.init(data.getRequest(), currUser.getUserId());
      String deptids = currUser.getHandleDeptValue("t.deptid");
      String SQL = BaseAction.combineSQL(corpSQL, "@", new String[]{"", deptids, fgsid});
      getOneTable().setQueryString(SQL);
      getOneTable().setRowMax(null);
      data.setMessage(showJavaScript("showFixedQuery()"));
    }
  }

  /**
   * 通过产品编码得到产品信息的触发类
   */
  final class CodeSearch implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      init(data);
      String dwdm = data.getParameter("code", "");
      table.getWhereInfo().putWhereValue("dwdm", dwdm);
      String deptids = currUser.getHandleDeptValue("t.deptid");
      String SQL = BaseAction.combineSQL(corpSQL, "@", new String[]{"t.dwdm LIKE '"+dwdm+"%' AND", deptids, fgsid});
      EngineDataSet ds = getOneTable();
      ds.setQueryString(SQL);
      if(ds.isOpen())
      {
        ds.readyRefresh();
        ds.refresh();
      }
      else
        ds.openDataSet();
    }
  }

  /**
   * 通过产品品名规格得到产品信息的触发类
   */
  final class NameSearch implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      init(data);
      String name = data.getParameter("name", "");
      table.getWhereInfo().putWhereValue("dwmc", name);
      String deptids = currUser.getHandleDeptValue("t.deptid");
      String SQL = BaseAction.combineSQL(corpSQL, "@", new String[]{"t.dwmc LIKE '%"+name+"%' AND",  deptids, fgsid});
      EngineDataSet ds = getOneTable();
      ds.setQueryString(SQL);
      if(ds.isOpen())
      {
        ds.readyRefresh();
        ds.refresh();
      }
      else
        ds.openDataSet();
    }
  }

  /**
   * 地区变更的触发类
   */
  final class AreaChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String areaid = data.getParameter("areaid", "");
      table.getWhereInfo().clearWhereValues();
      table.getWhereInfo().putWhereValue("dqh", areaid);
      String SQL = areaid.length()==0 ? "" : "t.dqh = "+areaid + " AND";
      String deptids = currUser.getHandleDeptValue("t.deptid");
      SQL = BaseAction.combineSQL(corpSQL, "@", new String[]{SQL, custType, deptids, fgsid});
      getOneTable().setQueryString(SQL);
      getOneTable().setRowMax(null);
    }
  }

  /**
   * 查询操作的触发类
   */
  final class Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      table.getWhereInfo().setWhereValues(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      String deptids = currUser.getHandleDeptValue("t.deptid");
      SQL = BaseAction.combineSQL(corpSQL, "@", new String[]{SQL.length()==0 ? "" :SQL+" AND",  deptids, fgsid});
      getOneTable().setQueryString(SQL);
      getOneTable().setRowMax(null);
    }
  }

  /*得到表对象，Implement this engine.project.OperateCommon abstract method*/
  public final EngineDataSet getOneTable()
  {
    return dsOneTable;
  }

  /**
   * 得到需要调用window.opener中的方法的名称
   * @return 方法的名称
   */
  public String getMethodName()
  {
    if(methodName != null && methodName.length() == 0)
      return null;
    return methodName;
  }

  public Log getLog()
  {
    return log;
  }
}
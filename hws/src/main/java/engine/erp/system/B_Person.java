package engine.erp.system;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineDataView;
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
import engine.erp.baseinfo.*;

import java.util.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.Locate;
/**
 * <p>Title: 基础维护－员工权限维护</p>
 * <p>Description: 基础维护－员工权限维护<br>
 * 在列表中需要得到往来单位的信息的方法：先调getPersonsData(String[] personIDs)方法得到数据,
 * 再调getPersonName(String personId)得到往来单位名称并定位数据集。或用调用getPersonRow(String personId)得到一行信息</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public class B_Person extends BaseAction implements Operate
{
  //与员工操作相关变量
  public static final int OPERATE_INIT   = 0;//刚刚进入页面
  public static final int OPERATE_ADD    = 20;//添加
  public static final int OPERATE_EDIT   = 21;//浏览或编辑
  public static final int OPERATE_DEL    = 22;//删除
  public static final int OPERATE_POST   = 23;//提交
  public static final int OPERATE_SEARCH = 25;//查找
  //登录管理相关操作
  public static final int OPERATE_CLEAR  = 31;//清楚密码
  public static final int OPERATE_CHANGINFO= 32;//更改用户登录信息
  //在部门树下添加员工信息
  public static final int OPERATE_DEPT_ADD_PERSON = 10001;
  //
  private static final String ROLE_LIMIT_SQL
      = "SELECT a.roleid, b.limitid, b.nodeid, b.priviligeid FROM rolelimit a, limitlist b "
      + "WHERE a.limitid = b.limitid AND b.isdelete=0 ORDER BY a.roleid";
  //
  private static final String PERSON_STRUCT_SQL
      = "SELECT personid, bm, xm, username, userpass, deptid, phone, email, isuse, isdelete FROM emp WHERE 1<>1";
  private static final String PERSON_SQL
      = "SELECT personid, bm, xm, username, userpass, deptid, phone, email, isuse, isdelete FROM emp WHERE isdelete=0 AND deptid IN (SELECT deptid FROM bm WHERE  isdelete=0) ? ORDER BY bm";
  private static final String PERSON_LIMIT_STRUCT_SQL = "SELECT * FROM personlimits WHERE 1<>1";
  private static final String PERSON_LIMIT_SQL
      = "SELECT * FROM personlimits WHERE roleid IS NULL AND personid=";//提取人员权限的SQL语句
  private static final String PERSON_ROLE_SQL
      = "SELECT * FROM personlimits WHERE roleid IS NOT NULL";//提取人员角色的SQL语句
  //人员部门权限
  private static final String PERSON_DEPT_HANDLE = "SELECT personid, deptid, qxlx FROM jc_bmqx WHERE personid='@'";
  //人员仓库权限
  private static final String PERSON_STORE_HANDLE = "SELECT personid, storeid FROM jc_ckqx WHERE personid='@'";
  //部门数据
  private static final String DEPT_TREE_SQL
      = "SELECT deptid, dm, parentdeptid, mc, firstid FROM bm WHERE isdelete=0 AND deptid>0 ORDER BY dm";
  //private static final String PERSON_LOOKUP_SQL
      //= "SELECT a.personid, a.deptid, a.bm, a.xm FROM emp a, bm b WHERE a.deptid = b.deptid AND b.ismember=0";
  //其他页面引用人员地信息有关
  //private EngineRow locateRow = null;

  public  EngineDataSet dsPerson  = new EngineDataSet();
  private EngineDataSet dsPersonLimits  = new EngineDataSet();
  private EngineDataSet dsDeptTree   = new EngineDataSet();
  private EngineDataSet dsRoleLimits = new EngineDataSet();   //所有角色权限列表
  public  EngineDataSet dsPersonRole  = new EngineDataSet();  //人员角色对照表
  public  EngineDataSet dsDeptHandle = new EngineDataSet();   //人员部门权限
  public  EngineDataSet dsStoreHandle = new EngineDataSet();  //人员仓库权限

  public  RowMap rowInfo = new RowMap(); //添加行或修改行的引用
  public  RowMap rowSearch = new RowMap(); //保存查找地的状态

  public  static final boolean isAdd = false;//时候在添加状态
  public  boolean isDeptAddPerson = false;//是否是在部门树下添加员工
  public  String deptid = null;//保存在部门树下添加员工传递的部门id
  private long editrow = 0;//保存修改记录的记录行

  private HtmlTree nodeTree = null;//打印角色以外权限树的对象
  private HtmlTree deptTree = null;//打印部门树对象
  public String nodeTreeInfo = null; //保存结点权限树的HTML文本的字符串
  public String deptTreeInfo = null; //保存部门权限树的HTML文本的字符串
  public StringBuffer roleLimitScript = new StringBuffer();//保存勾选角色信息的JavaScript函数

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  /**
   * 得到人员信息的实例
   * @param request jsp请求
   * @return 返回人员信息的实例
   */
  public static B_Person getInstance(HttpServletRequest request)
  {
    B_Person personBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      personBean = (B_Person)session.getAttribute("personBean_aa");
      if(personBean == null)
      {
        personBean = new B_Person();
        session.setAttribute("personBean_aa", personBean);
      }
    }
    return personBean;
  }

  private B_Person()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
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

  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsDeptTree != null){
      dsDeptTree.closeDataSet();
      dsDeptTree = null;
    }
    if(dsPerson !=null){
      dsPerson.close();
      dsPerson = null;
    }
    if(dsPersonLimits != null){
      dsPersonLimits.closeDataSet();
      dsPersonLimits = null;
    }
    if(dsPersonRole != null){
      dsPersonRole.closeDataSet();
      dsPersonRole = null;
    }
    if(dsDeptHandle != null){
      dsDeptHandle.closeDataSet();
      dsDeptHandle = null;
    }
    if(dsStoreHandle != null){
      dsStoreHandle.closeDataSet();
      dsStoreHandle = null;
    }
    if(dsRoleLimits != null){
      dsRoleLimits.closeDataSet();
      dsRoleLimits = null;
    }
    rowInfo = null;
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private void jbInit() throws Exception {
    setDataSetProperty(dsDeptTree, DEPT_TREE_SQL);
    setDataSetProperty(dsPerson, PERSON_STRUCT_SQL); //ORDER BY bm
    setDataSetProperty(dsPersonLimits, PERSON_LIMIT_STRUCT_SQL);
    setDataSetProperty(dsRoleLimits, ROLE_LIMIT_SQL);
    setDataSetProperty(dsPersonRole, null);
    setDataSetProperty(dsStoreHandle, null);
    setDataSetProperty(dsDeptHandle, null);

    //dsPerson.setSort(new SortDescriptor("", new String[]{"bm"}, new boolean[]{false}, null, 0));
    dsPersonLimits.setSequence(new SequenceDescriptor(new String[]{"personlimitid"}, new String[]{"s_personlimits"}));
    dsPersonRole.setSequence(new SequenceDescriptor(new String[]{"personlimitid"}, new String[]{"s_personlimits"}));
    Add_Edit addedit = new Add_Edit();
    addObactioner(String.valueOf(OPERATE_INIT), new Init());
    addObactioner(String.valueOf(OPERATE_SEARCH), new Search());
    addObactioner(String.valueOf(OPERATE_ADD), addedit);
    addObactioner(String.valueOf(OPERATE_EDIT), addedit);
    addObactioner(String.valueOf(OPERATE_DEL), new Delete());
    addObactioner(String.valueOf(OPERATE_POST), new Post());
    addObactioner(String.valueOf(OPERATE_CLEAR), new ClearPassword());
    addObactioner(String.valueOf(OPERATE_CHANGINFO), new ChangeLoginInfo());
    addObactioner(String.valueOf(OPERATE_DEPT_ADD_PERSON), new DeptAddPerson());
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
        if(data == null)
          return showMessage("无效操作", false);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsPerson.isOpen() && dsPerson.changesPending())
        dsPerson.reset();
      if(dsPersonRole.isOpen() && dsPersonRole.changesPending())
        dsPersonRole.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * 在部门树下添加员工
   */
  class DeptAddPerson implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      deptid = data.getParameter("deptid");
      if(deptid == null)
        deptid = "null";
      isDeptAddPerson = true;
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      row.put("deptid", deptid);
      String SQL = combineSQL(PERSON_SQL, "?", new String[]{"AND deptid='"+deptid+"'"});
      dsPerson.setQueryString(SQL);
      dsPerson.setRowMax(null);
    }
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
      isDeptAddPerson = false;
      deptid = null;
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      //初始化角色权限点击的JavaScript函数
      initRoleLimitScript();
      //
      dsPerson.setQueryString(PERSON_STRUCT_SQL);
      dsPerson.readyRefresh();
      if(dsDeptTree.isOpen())
        dsDeptTree.refresh();
      else
        dsDeptTree.openDataSet();

      B_LimitsInfo.getInstance(data.getRequest()).init();
      data.setMessage(showJavaScript("showFixedQuery();"));
    }

    /**
     * 初始化角色权限点击的JavaScript函数
     */
    private void initRoleLimitScript()
    {
      if(dsRoleLimits.isOpen())
        dsRoleLimits.refresh();
      else
        dsRoleLimits.openDataSet();
      //复位StringBuffer的内容
      roleLimitScript.setLength(0);
      dsRoleLimits.first();
      String roleid = dsRoleLimits.getRowCount() > 0 ? dsRoleLimits.getValue("roleid") : null;
      if(roleid != null)
        roleLimitScript.append("if(roleid=='").append(roleid).append("'){\n");

      for(int i=0; i<dsRoleLimits.getRowCount(); i++)
      {
        String newRoleid = dsRoleLimits.getValue("roleid");
        if(!newRoleid.equals(roleid))
        {
          roleid = newRoleid;
          roleLimitScript.append("} if(roleid=='").append(roleid).append("'){\n");
        }
        roleLimitScript.append("obj = document.all['chklimit").append(dsRoleLimits.getValue("nodeid"));
        roleLimitScript.append("_").append(dsRoleLimits.getValue("priviligeid"));
        roleLimitScript.append("_").append(dsRoleLimits.getValue("limitid")).append("'];");
        roleLimitScript.append("if(obj != null) obj.checked=ischeck;\n");
        dsRoleLimits.next();
      }
      if(roleid != null)
        roleLimitScript.append("}\n");
      dsRoleLimits.closeDataSet();
    }
  }

  /**
   * 初始化操作的触发类
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
      SQL = combineSQL(PERSON_SQL, "?", new String[]{SQL});
      if(!dsPerson.getQueryString().equals(SQL))
      {
        dsPerson.setQueryString(SQL);
        dsPerson.setRowMax(null);
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
      EngineDataSet master = dsPerson;
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("bm"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("xm"), null, null, null),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("username"), null, null, null),
        new QueryColumn(master.getColumn("isuse"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }

  /**
   * 添加或修改操作的触发类
   */
  class Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //isAdd = action.equals(String.valueOf(OPERATE_ADD));
      String personid = "";
      if(!isAdd){
        dsPerson.goToRow(Integer.parseInt(data.getParameter("rownum")));
        personid = dsPerson.getValue("personid");
        editrow = dsPerson.getInternalRow();
        dsPersonLimits.setQueryString(PERSON_LIMIT_SQL+personid);
        if(dsPersonLimits.isOpen())
          dsPersonLimits.refresh();
        else
          dsPersonLimits.open();
      }
      else if(!dsPersonLimits.isOpen())
        dsPersonLimits.open();

      String deptSQL = combineSQL(PERSON_DEPT_HANDLE, "@", new String[]{personid});
      String storeSQL = combineSQL(PERSON_STORE_HANDLE, "@", new String[]{personid});
      dsDeptHandle.setQueryString(deptSQL);
      dsStoreHandle.setQueryString(storeSQL);
      if(dsDeptHandle.isOpen())
        dsDeptHandle.refresh();
      else
        dsDeptHandle.openDataSet();
      //
      if(dsStoreHandle.isOpen())
        dsStoreHandle.refresh();
      else
        dsStoreHandle.openDataSet();

      initRowInfo(isAdd, true);
      makeLimitTreeInfo(data.getRequest(), isAdd);
      //
      data.setMessage(showJavaScript("location.href='personedit.jsp';"));
    }
  }

  /**
   * 保存操作的触发类
   */
  class Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //校验数据
      HttpServletRequest request = data.getRequest();
      rowInfo.put(request);
      String username = rowInfo.get("username");
      if(username.length() < 1)
      {
        data.setMessage(showJavaScript("alert('员工登录名不能为空！');"));
        return;
      }
      dsPerson.goToInternalRow(editrow);
      if(!dsPerson.getValue("username").equals(username))
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('emp','username','"+username+"','isdelete=0') from dual");
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('登录名("+ username +")已经存在!');"));
          return;
        }
      }
      /*
      String bm = rowInfo.get("bm");
      String xm = rowInfo.get("xm");
      String temp = null;
      if(bm.equals(""))
      {
        data.setMessage(showJavaScript("alert('员工编码不能为空！');"));
        return;
      }
      else if((temp = checkInt(bm, "员工编码")) != null){
        data.setMessage(temp);
        return;
      }

      if(xm.equals(""))
      {
        data.setMessage(showJavaScript("alert('员工姓名不能为空！');"));
        return;
      }

      if(rowInfo.get("deptid").equals(""))
      {
        data.setMessage(showJavaScript("alert('员工所属部门不能为空！');"));
        return;
      }

      if(isAdd || !bm.equals(dsPerson.getValue("bm")))
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('emp','bm','"+bm+"','isdelete=0') from dual");
        if(!count.equals("0"))
        {
          if(isAdd)
            initRowInfo(true, false);
          data.setMessage(showJavaScript("alert('编码("+ bm +")已经存在!');"));
          return;
        }
      }
      //保存员工数据
      String personid = isAdd ? dataSetProvider.getSequence("s_emp") : dsPerson.getValue("personid");
      if(isAdd)
      {
        dsPerson.insertRow(false);
        dsPerson.setValue("personid", personid);
        dsPerson.setValue("isdelete", "0");
        dsPerson.setValue("isuse", "0");
        dsPerson.setValue("isoff", "0");
        dsPerson.setValue("username", "YG"+bm);
      }
      else
      dsPerson.setValue("bm", bm);
      dsPerson.setValue("xm", xm);
      dsPerson.setValue("deptid", rowInfo.get("deptid"));
      dsPerson.setValue("sfzhm", rowInfo.get("sfzhm"));
      dsPerson.setValue("phone", rowInfo.get("phone"));
      dsPerson.setValue("email", rowInfo.get("email"));
      dsPerson.setValue("zw", rowInfo.get("zw"));
      dsPerson.setValue("zc", rowInfo.get("zc"));
      dsPerson.setValue("study", rowInfo.get("study"));
      dsPerson.setValue("lb", rowInfo.get("lb"));
      dsPerson.setValue("zzmm", rowInfo.get("zzmm"));
      dsPerson.setValue("mz", rowInfo.get("mz"));
      dsPerson.setValue("jg", rowInfo.get("jg"));
      dsPerson.setValue("sex", rowInfo.get("sex"));
      dsPerson.setValue("date_born", rowInfo.get("date_born"));
      dsPerson.setValue("date_in", rowInfo.get("date_in"));
      dsPerson.setValue("addr", rowInfo.get("addr"));
      dsPerson.setValue("bz", rowInfo.get("bz"));*/
      String personid = dsPerson.getValue("personid");
      dsPerson.setValue("username", username);
      dsPerson.post();
      //处理人员所属角色和权限
      boolean roleChange = rowInfo.get("rolechange").equals("1");
      boolean isChange = rowInfo.get("limitchange").equals("1");
      if(!isAdd && isChange)
        dsPersonLimits.deleteAllRows();
      if(isAdd || isChange)
      {
        String[] chklimits = request.getParameterValues("chklimit");
        for(int i=0; chklimits!=null && i<chklimits.length; i++)
        {
          String[] limitinfo = parseString(chklimits[i], "_");
          if(limitinfo == null || limitinfo.length<3)
            continue;
          dsPersonLimits.insertRow(false);
          dsPersonLimits.setValue("personlimitid", "-1");
          dsPersonLimits.setValue("personid", personid);
          dsPersonLimits.setValue("limitid", limitinfo[2]);
          dsPersonLimits.post();
        }
      }
      //角色变更
      if(!isAdd && roleChange)
      {
        EngineRow row = new EngineRow(dsPersonRole, "personid");
        row.setValue(0, dsPerson.getValue("personid"));
        while(dsPersonRole.locate(row, Locate.FIRST))
          dsPersonRole.deleteRow();
      }
      if(isAdd || roleChange)
      {
        String[] chkroles = request.getParameterValues("chkrole");
        for(int i=0; chkroles!=null && i<chkroles.length; i++)
        {
          dsPersonRole.insertRow(false);
          dsPersonRole.setValue("personlimitid", "-1");
          dsPersonRole.setValue("personid", personid);
          dsPersonRole.setValue("roleid", chkroles[i]);
          dsPersonRole.post();
        }
      }
      //仓库变更
      boolean storeChange = rowInfo.get("storechange").equals("1");
      if(storeChange)
      {
        if(!isAdd)
          dsStoreHandle.deleteAllRows();

        String[] chkstores = request.getParameterValues("chkstore");
        for(int i=0; chkstores!=null && i<chkstores.length; i++)
        {
          dsStoreHandle.insertRow(false);
          dsStoreHandle.setValue("personid", personid);
          dsStoreHandle.setValue("storeid", chkstores[i]);
          dsStoreHandle.post();
        }
      }
      //部门变更
      boolean deptChange = rowInfo.get("deptchange").equals("1");
      if(deptChange)
      {
        if(!isAdd)
          dsDeptHandle.deleteAllRows();


        dsDeptTree.first();
        for(int i=0; i<dsDeptTree.getRowCount(); i++)
        {
          String deptid = dsDeptTree.getValue("deptid");
          String chkdept = data.getParameter("chkdept_"+deptid, "2");
          if("1".equals(chkdept) || "0".equals(chkdept))
          {
            dsDeptHandle.insertRow(false);
            dsDeptHandle.setValue("personid", personid);
            dsDeptHandle.setValue("deptid", deptid);
            dsDeptHandle.setValue("qxlx", chkdept);
            dsDeptHandle.post();
          }
          dsDeptTree.next();
        }
      }

      dsPerson.saveDataSets(new EngineDataSet[]{dsPerson, dsPersonLimits, dsPersonRole,
          dsStoreHandle, dsDeptHandle}, null);
      data.setMessage(showJavaScript("location.href='personlist.jsp';"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsPerson.goToRow(Integer.parseInt(data.getParameter("rownum")));
      dsPerson.setValue("isdelete", "1");
      dsPerson.post();
      dsPerson.saveChanges();
      dsPerson.deleteRow();
      dsPerson.resetPendingStatus(true);
    }
  }

  /**
   * 更改登录信息的触发类
   */
  class ClearPassword implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsPerson.goToRow(Integer.parseInt(data.getParameter("rownum")));
      dsPerson.setValue("userpass", "");
      dsPerson.post();
      dsPerson.saveChanges();
    }
  }

  /**
   * 更改登录信息的触发类*(启用还是禁用)
   */
  class ChangeLoginInfo implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String rownum = data.getParameter("rownum");
      String temp = null;
      if((temp = checkInt(rownum, "操作")) != null)
      {
        data.setMessage(showJavaScript(temp));
        return;
      }
      String isuse = data.getParameter("isuse_"+rownum);
      if(isuse == null || !isuse.equals("1"))
        isuse = "0";
      dsPerson.goToRow(Integer.parseInt(rownum));
      if(isuse.equals(dsPerson.getValue("isuse")))
        return;
      if(isuse.equals("1") && dsPerson.getValue("username").length() < 1)
      {
        data.setMessage(showJavaScript("alert('请先设置用户登录名！')"));
        return;
      }
      dsPerson.setValue("isuse", isuse);
      dsPerson.post();
      dsPerson.saveChanges();
      /*
      dsPerson.first();
      for(int i=0; i<dsPerson.getRowCount(); i++)
      {
        String username = request.getParameter("username_"+dsPerson.getRow());
        if(username != null && !dsPerson.getValue("username").equals(username))
        {
          dsPerson.setValue("username", username);
          dsPerson.post();
        }
        String isuse = request.getParameter("isuse_"+dsPerson.getRow());
        if(isuse != null && !dsPerson.getValue("isuse").equals(isuse))
        {
          dsPerson.setValue("isuse", isuse);
          dsPerson.post();
        }
        dsPerson.next();
      }*/
    }
  }
  /**
   * 初始化列信息
   * @param isAdd 是否是添加
   * @param isInit 是否重新初始化行信息
   * @throws Exception 异常
   */
  private void initRowInfo(boolean isAdd, boolean isInit) throws Exception
  {
    if(!dsPerson.isOpen())
      dsPerson.open();
    //是否时添加操作
    if(isInit || rowInfo.size() > 0)
      rowInfo.clear();

    if(isAdd){
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('emp','bm','','',6) from dual");
      rowInfo.put("bm", code);
    }
    else
      rowInfo.put(dsPerson);
    if(isDeptAddPerson)
      rowInfo.put("deptid", deptid);
  }

  /**
   * 制造网页要显示的结点树的权限信息
   * @param request web的请求对象
   * @param isAdd 是否是添加
   */
  private void makeLimitTreeInfo(HttpServletRequest request, boolean isAdd)
  {
    B_LimitsInfo limitsInfoBean = B_LimitsInfo.getInstance(request);
    EngineDataView dvNodeInfo = limitsInfoBean.getNodeInfoData();//所有界面的数据
    EngineDataView dvLimitlist = limitsInfoBean.getLimitlistData();//所有界面具有的权限的数据
    //保存网页要显示的结点树的权限信息（如&nbsp;<input type='checkbox'>）
    List vLimitTreeInfo = B_Role.getLimitTreeInfo(dvNodeInfo, dvLimitlist, dsPersonLimits, isAdd, new ArrayList(dvNodeInfo.getRowCount()));
    //得到结点权限树的HTML文本的字符串
    if(nodeTree == null)
    {
      nodeTree = new HtmlTree();
      nodeTree.setMouseUpFuction(null);
      nodeTree.setMouseOutFuction(null);
      nodeTree.setTreeTableClass("");
    }
    nodeTree.setTreeProperty(dvNodeInfo, "界面权限树", "nodeid", "nodecode", "parentnodeid", "nodename", vLimitTreeInfo);
    nodeTreeInfo = nodeTree.printHtmlTree(false);
    dvNodeInfo.close();
    dvNodeInfo = null;
    dvLimitlist.close();
    dvLimitlist = null;

    //打印部门权限树
    List deptControl = getDeptHandleControl(dsDeptTree);//得到部门权限控件信息
    if(deptTree == null)
    {
      deptTree = new HtmlTree("dt_");
      deptTree.setMouseUpFuction(null);
      deptTree.setMouseOutFuction(null);
      deptTree.setNodeClickFuction(null);
      deptTree.setTreeTableClass("");
    }
    deptTree.setTreeProperty(dsDeptTree, "部门权限树", "deptid", "dm", "parentdeptid", "mc", deptControl);
    deptTreeInfo = deptTree.printHtmlTree(false);
  }

  /**
   * 得到与部门相对应的输入框信息
   * @param dsDeptInfo 部门数据信息
   * @return 返回部门相对应的输入框信息
   */
  private List getDeptHandleControl(EngineDataSet dsDeptInfo)
  {
    EngineRow locateRow = new EngineRow(dsDeptHandle, "deptid");
    ArrayList listDeptControl = new ArrayList(dsDeptInfo.getRowCount()+1);
    dsDeptInfo.first();
    int maxLength = 0;
    for(int i=0; i<dsDeptInfo.getRowCount(); i++)
    {
      int length = dsDeptInfo.getValue("mc").getBytes().length;
      maxLength = length > maxLength ? length : maxLength;
      dsDeptInfo.next();
    }
    dsDeptInfo.first();
    for(int i=0; i<dsDeptInfo.getRowCount(); i++)
    {
      StringBuffer buf = new StringBuffer();
      String deptid = dsDeptInfo.getValue("deptid");
      int length = dsDeptInfo.getValue("mc").getBytes().length;
      for(; length < maxLength; length++)
        buf.append("&nbsp;");
      locateRow.setValue(0, deptid);
      //得到权限类型
      String prifix = "<input type=radio class=checkbox onChange=chkdept_onchange() name=chkdept_";
      String qxlx = dsDeptHandle.locate(locateRow, Locate.FIRST) ? dsDeptHandle.getValue("qxlx") : "2";
      buf.append(prifix).append(deptid).append(" value='2'").append("2".equals(qxlx) ? " checked>" : ">").append("无&nbsp;");
      buf.append(prifix).append(deptid).append(" value='1'").append("1".equals(qxlx) ? " checked>" : ">").append("部门级&nbsp;");
      buf.append(prifix).append(deptid).append(" value='0'").append("0".equals(qxlx) ? " checked>" : ">").append("个人级");
      listDeptControl.add(buf);
      dsDeptInfo.next();
    }
    return listDeptControl;
  }

  /**
   * 根据多个人员ID得到人员角色对照表
   */
  public void openUsersRoles()
  {
    int currCount = dsPerson.getRowCount();
    String[] personids = new String[currCount];
    dsPerson.first();
    for(int i=0; i<currCount; i++)  {
      personids[i] = dsPerson.getValue("personid");
      dsPerson.next();
    }
    openUsersRoles(personids);
  }
  /**
   * 根据多个人员ID得到人员角色对照表
   * @param personIDs 人员ID数组
   */
  private void openUsersRoles(String[] personIDs)
  {
    if(personIDs == null)
      return;
    int num = personIDs.length;
    String SQL = PERSON_ROLE_SQL + (num >0 ? " AND personid IN (" : " AND 1<>1");
    for(int i=0; i<num; i++)
    {
      String personid = personIDs[i].equals("") ? "-1" : personIDs[i];
      if(i== num-1)
        SQL += personid + ")";
      else
        SQL += personid + ",";
    }
    dsPersonRole.setQueryString(SQL);
    if(dsPersonRole.isOpen())
      dsPersonRole.refresh();
    else
      dsPersonRole.open();
  }

  /**
   * 打开部门下的所有员工
   * @param deptid 部门ID, 若＝null, 则提取所有的人员

  private void openDeptPersons(String deptid)
  {
    String SQL = null;
    if(deptid == null)
      SQL = PERSON_LOOKUP_SQL;
    else if(deptid.equals(""))
    {
      deptid = "-1";
      SQL = PERSON_LOOKUP_SQL +" AND a.deptid ="+ deptid;
    }
    if(!dsStoreHandle.isOpen() || !SQL.equals(dsStoreHandle.getQueryString()))
    {
      dsStoreHandle.setQueryString(SQL);
      if(dsStoreHandle.isOpen())
        dsStoreHandle.refresh();
      else
        dsStoreHandle.open();
    }
  }
  /**
   * 网页动态的得到人员的下拉框项目
   * @param deptid 部门ID
   * @param personid 人员ID
   * @return 返回列表
   * @throws Exception 异常
   */
  public final String dhtmlPersonsOption(HttpServletRequest request)
  {
    String selectid = request.getParameter("selectid");
    if(selectid == null || selectid.equals(""))
      return "";

    StringBuffer buf = new StringBuffer();
    /*buf.append("<script language='javascript'>");
    buf.append("var obj = parent.document.all['");
    buf.append(selectid);
    buf.append("'];");
    buf.append("obj.length = 0;");
    buf.append("parent.addSelectOption('"+ selectid +"','','');");
    String deptid = request.getParameter("deptid");
    if(deptid != null && !deptid.equals(""))
    {
      openDeptPersons(deptid);
      dsStoreHandle.first();
      while(dsStoreHandle.inBounds())
      {
        buf.append("parent.addSelectOption('");
        buf.append(selectid);
        buf.append("','");
        buf.append(dsStoreHandle.getValue("xm"));
        buf.append("','");
        buf.append(dsStoreHandle.getValue("personid"));
        buf.append("');");
        dsStoreHandle.next();
      }
    }
    buf.append("</script>");*/
    return buf.toString();
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
}
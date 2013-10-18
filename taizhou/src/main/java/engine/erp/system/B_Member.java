package engine.erp.system;

import engine.project.*;
import engine.dataset.*;
import engine.erp.baseinfo.*;

import javax.servlet.http.*;
//import engine.erp.baseinfo.B_ProductItem;
import com.borland.dx.dataset.*;
import java.util.*;
/**
 * <p>Title: 基础信息维护--会员信息管理</p>
 * <p>Description: 基础信息维护--会员信息管理</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public final class B_Member extends SingleOperate
{
  /*
  private static final String EMP_SQL = "select * from emp where isdelete=0 ";
  private static final String EMP_LOOKUP_SQL = "select * from emp ";
  private static final String YHZH_SQL = "select * from yhzh ";
  private static final String BM_LOOKUP_SQL = "select * from bm where ismember= 1 ";
  */

  private static final String MEMBER_STRUCT_SQL = "SELECT * FROM bm WHERE 1<>1";
  private static final String MEMBER_SQL = "SELECT * FROM bm WHERE isdelete=0 AND ismember=1 ";
  private static final String BANK_ACCOUNT_SQL = "SELECT * FROM yhzh WHERE deptid=";
  private static final String MEMBER_PERSON_SQL = "SELECT * FROM emp WHERE isdelete=0 AND deptid=";

  private static final String PERSON_LIMIT_SQL
      = "SELECT * FROM personlimits WHERE roleid IS NULL AND personid=";//提取人员权限的SQL语句
  private static final String PERSON_ROLE_SQL
      = "SELECT * FROM personlimits WHERE roleid IS NOT NULL AND personid=";//提取人员角色的SQL语句

  public EngineDataSet dsMember = dsOneTable;
  public EngineDataSet dsMember_person = new EngineDataSet();//会员的登陆信息
  public EngineDataSet dsBankAccount = new EngineDataSet();
  public EngineDataSet dsPersonRole = new EngineDataSet();
  public EngineDataSet dsPersonLimits = new EngineDataSet();
  //public EngineDataSet dsMember_temp= new EngineDataSet();

  private boolean isInitQuery = false;
  private QueryBasic fixedQuery = null;
  public String retuUrl = null;

  private HtmlTree nodeTree = null;//保存打印部门树的对象
  private Vector vLimitTreeInfo = null;  //保存网页要显示的结点树的权限信息（如&nbsp;<input type='checkbox'>）
  public String nodeTreeInfo = null; //保存结点权限树的HTML文本的字符串
  //public boolean isSelf= true;
  //public static final int OPERATE_COPY_SELF = 1002;
  //public static final int OPERATE_COPY_OTHER = 1003;

  private EngineRow locateRow = null;
  /**
   * 得到往来单位信息的实例
   * @param request jsp请求
   * @return 返回往来单位信息的实例
   */
  public static B_Member getInstance(HttpServletRequest request)
  {
    B_Member memberBean = null;
    HttpSession session = request.getSession();
    synchronized (session) {
      memberBean =  (B_Member)session.getAttribute("memberBean");
      if(memberBean==null){
        memberBean = new B_Member();
        session.setAttribute("memberBean",memberBean);
      }
      return memberBean;
    }
  }
  /**
   * 构造函数
   */
  private B_Member()
  {
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
    setDataSetProperty(dsMember, MEMBER_STRUCT_SQL);
    setDataSetProperty(dsMember_person, null);
    setDataSetProperty(dsBankAccount, null);
    setDataSetProperty(dsPersonRole, null);
    setDataSetProperty(dsPersonLimits, null);

    dsMember.setSort(new SortDescriptor("", new String[]{"dm"}, new boolean[]{false}, null, 0));
    //dsMember_person.setSequence(new SequenceDescriptor(new String[]{"personid"}, new String[]{"s_emp"}));
    dsBankAccount.setSequence(new SequenceDescriptor(new String[]{"yhzhid"}, new String[]{"s_yhzh"}));
  }

  /**
   * Implement this engine.project.OperateCommon abstract method
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMember !=null)
    {
      dsMember.close();
      dsMember = null;
    }
    if(dsMember_person !=null)
    {
      dsMember_person.close();
      dsMember_person = null;
    }
    if(dsPersonLimits !=null)
    {
      dsPersonLimits.close();
      dsPersonLimits = null;
    }
    if(dsPersonRole !=null)
    {
      dsPersonRole.close();
      dsPersonRole = null;
    }
    if(dsBankAccount !=null)
    {
      dsBankAccount.close();
      dsBankAccount = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
  }

  /**
   * Implement this engine.project.OperateCommon abstract method
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }
  /**
   * 保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws java.lang.Exception 异常
   */
  protected final String postOperate(HttpServletRequest request, HttpServletResponse response)
      throws Exception
  {
    EngineDataSet ds = getOneTable();
    //校验数据
    rowInfo.put(request);

    String dm = rowInfo.get("dm");
    String mc = rowInfo.get("mc");
    String qc = rowInfo.get("qc");
    String dept_addr = rowInfo.get("dept_addr");
    String dept_phone = rowInfo.get("dept_phone");
    String dept_fax = rowInfo.get("dept_fax");
    String hzr = rowInfo.get("hzr");
    String frdb = rowInfo.get("frdb");
    String dept_email = rowInfo.get("dept_email");
    String gjjyl = rowInfo.get("gjjyl");
    String rcsj = rowInfo.get("rcsj");
    String hytsxx = rowInfo.get("hytsxx");

    String yhid = rowInfo.get("yhid");//display the yhmc,but the value is  yhid;
    String zh = rowInfo.get("zh");

    if(mc.equals(""))
      return showJavaScript("alert('名称不可为空')");
    if(dm.equals(""))
      return showJavaScript("alert('席位不可为空')");

    if(isAdd  || !ds.getValue("dm").equals(dm))
    {
      String count = "select count(*) from bm where isdelete=0 and ismember=1 and dm='"+dm+"'";
      count = dataSetProvider.getSequence(count);
      if(!count.equals("0")){
        return showJavaScript("alert('该席位已经存在');");
      }
    }
    String deptid = dataSetProvider.getSequence("s_bm");
    String personid = isAdd ? dataSetProvider.getSequence("s_emp") : dsMember_person.getValue("personid");
    if(isAdd)
    {
      ds.insertRow(false);//insert after or before it
      ds.setValue("deptid", deptid);
      ds.setValue("isdelete", "0");
      ds.setValue("parentdeptid","0");
      ds.setValue("ismember","1");

      dsBankAccount.insertRow(false);
      dsBankAccount.setValue("yhzhid","-1");
      dsBankAccount.setValue("deptid",deptid);
      dsBankAccount.setValue("fgsid",deptid);

      dsMember_person.insertRow(false);
      dsMember_person.setValue("personid",personid);
      dsMember_person.setValue("isdelete","0");
      dsMember_person.setValue("isUse","0");
      dsMember_person.setValue("deptid", deptid);
      dsMember_person.setValue("username", "HY"+dm);
    }

    dsBankAccount.setValue("yhid",yhid);
    dsBankAccount.setValue("zh",zh);
    dsBankAccount.post();

    dsMember_person.setValue("bm",dm);
    dsMember_person.setValue("xm",mc);
    dsMember_person.post();

    ds.setValue("dm", dm);
    ds.setValue("mc", mc);
    ds.setValue("qc", qc);
    ds.setValue("dept_addr", dept_addr);
    ds.setValue("dept_phone",dept_phone);
    ds.setValue("dept_fax", dept_fax);
    ds.setValue("hzr", hzr);
    ds.setValue("frdb", frdb);
    ds.setValue("dept_email", dept_email);
    ds.setValue("gjjyl", gjjyl);
    ds.setValue("rcsj", rcsj);
    ds.setValue("hytsxx", hytsxx);
    ds.post();

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

    if(!isAdd && roleChange)
    {
      dsPersonRole.deleteAllRows();
      /*EngineRow row = new EngineRow(dsPersonRole, "personid");
      row.setValue(0, dsPerson.getValue("personid"));
      while(dsPersonRole.locate(row, Locate.FIRST))
        dsPersonRole.deleteRow();*/
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

    ds.saveDataSets(new EngineDataSet[]{ds, dsBankAccount, dsMember_person, dsPersonLimits, dsPersonRole}, null);//构成一个事务，同时提交
    return showJavaScript("backList();");
  }
  /**
   * 删除操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   */

  protected final String deleteOperate(HttpServletRequest request, HttpServletResponse response)
  {
    EngineDataSet ds = getOneTable();
    ds.goToRow(Integer.parseInt(request.getParameter("rownum")));
    openOtherTable(true);

    ds.setValue("isdelete", "1");
    ds.post();

    dsMember_person.setValue("isdelete","1");
    dsMember_person.post();

    dsPersonLimits.deleteAllRows();
    dsPersonRole.deleteAllRows();
    ds.saveDataSets(new EngineDataSet[]{ds, dsMember_person, dsPersonLimits, dsPersonRole}, null);
    //删除本地数据不提交到数据库
    ds.deleteRow();
    ds.resetPendingStatus(true);
    return "";
  }
  /**
   * 其他操作，父类未定义的操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @param operate 网页的操作类型
   * @return 返回HTML或javascipt的语句
   */
  protected final String otherOperate(HttpServletRequest request, HttpServletResponse response, int operate) throws Exception
  {
    switch(operate)
    {
      case Operate.ADD:
      case Operate.EDIT:
        openOtherTable(isAdd);//打开其他表
        String yhid =  dsBankAccount.getValue("yhid");
        String zh = dsBankAccount.getValue("zh");
        rowInfo.put("zh",zh);
        rowInfo.put("yhid",yhid);
        makeLimitTreeInfo(request, isAdd);
        return showJavaScript("toDetail();");

      case Operate.FIXED_SEARCH:
        fixedQuery.setSearchValue(request);
        String SQL = fixedQuery.getWhereQuery();
        SQL = MEMBER_SQL +(SQL.equals("")? "" : "AND (" +SQL +")") +" ORDER BY dm";
        if(!getOneTable().getQueryString().equals(SQL))
        {
          getOneTable().setQueryString(SQL);
          getOneTable().setRowMax(null);
        }
        break;
      //初始化
      case Operate.INIT:
        retuUrl = request.getParameter("src");
        retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
        //初始化查询项目和内容
        initQueryItem(request);
        fixedQuery.getSearchRow().clear();
        dsOneTable.setQueryString(MEMBER_STRUCT_SQL);
        dsOneTable.setRowMax(null);
        return showJavaScript("showFixedQuery();");
    }
    return "";
  }

  /**
   * 初始化列信息 Implement this engine.project.OperateCommon abstract method
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  protected final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd)
      rowInfo.put(getOneTable());
  }

  /*得到表对象，Implement this engine.project.OperateCommon abstract method*/
  public final EngineDataSet getOneTable()
  {
    return dsOneTable;
  }

  /*得到一列的信息，Implement this engine.project.OperateCommon abstract method*/
  public final RowMap getRowinfo() {    return rowInfo;  }

  /**
   * 取得分段的商品数据
   * @param rowMin 分段的最小行标
   * @param rowMax 分段的最大行标
   */
  public void fetchData(int rowMin, int rowMax, HttpServletRequest request)
  {
    //String min = String.valueOf(rowMin);
    //String max = String.valueOf(rowMax);
    if(dsOneTable.getRowMin() != rowMin || dsOneTable.getRowMax() != rowMax)
    {
      dsOneTable.setRowMin(rowMin);
      dsOneTable.setRowMax(rowMax);
      if(dsOneTable.isOpen())
        dsOneTable.refresh();
      else
        dsOneTable.open();
    }

    initQueryItem(request);
  }

  /**
   * 初始化查询的各个列
   * @param request web请求对象
   */
  public void initQueryItem(HttpServletRequest request)
  {
    if(isInitQuery)
      return;
    EngineDataSet ds = getOneTable();
    if(!ds.isOpen())
      ds.open();
    //初始化固定的查询项目
    fixedQuery = new QueryFixedItem();
    fixedQuery.addShowColumn("bm", new QueryColumn[]{
      new QueryColumn(ds.getColumn("dm"), null, null, null),
      new QueryColumn(ds.getColumn("mc"), null, null, null),
      new QueryColumn(ds.getColumn("dept_phone"), null, null, null),
      new QueryColumn(ds.getColumn("dept_fax"), null, null, null),
      new QueryColumn(ds.getColumn("hzr"), null, null, null),
      new QueryColumn(ds.getColumn("dept_email"), null, null, null),
      new QueryColumn(ds.getColumn("rcsj"), null, null, null,"a",">="),
      new QueryColumn(ds.getColumn("rcsj"),null,null,null,"b","<="),
      new QueryColumn(ds.getColumn("qc"), null, null, null),
      new QueryColumn(ds.getColumn("dept_addr"), null, null, null),
      new QueryColumn(ds.getColumn("frdb"), null, null, null),
      new QueryColumn(ds.getColumn("gjjyl"), null, null, null),
      new QueryColumn(ds.getColumn("hytsxx"), null, null, null)
    });

    isInitQuery = true;
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
   * 制造网页要显示的结点树的权限信息
   * @param request web的请求对象
   * @param isAdd 是否是添加
   */
  private void makeLimitTreeInfo(HttpServletRequest request, boolean isAdd)
  {
    B_LimitsInfo limitsInfoBean = B_LimitsInfo.getInstance(request);
    EngineDataView dvNodeInfo = limitsInfoBean.getNodeInfoData();//所有界面的数据
    EngineDataView dvLimitlist = limitsInfoBean.getLimitlistData();//所有界面具有的权限的数据
    if(vLimitTreeInfo == null)
      vLimitTreeInfo = new Vector(dvNodeInfo.getRowCount());
    else if(vLimitTreeInfo.size() > 0)
      vLimitTreeInfo.clear();

    B_Role.getLimitTreeInfo(dvNodeInfo, dvLimitlist, dsPersonLimits, isAdd, vLimitTreeInfo);

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
  }

  /**
   *
   */
  private void openOtherTable(boolean isAdd)
  {
    String deptid = isAdd ? "-2":dsOneTable.getValue("deptid");
    dsBankAccount.setQueryString(BANK_ACCOUNT_SQL + deptid);
    if(dsBankAccount.isOpen())
      dsBankAccount.refresh();
    else
      dsBankAccount.open();

    dsMember_person.setQueryString(MEMBER_PERSON_SQL + deptid);
    if(dsMember_person.isOpen())
      dsMember_person.refresh();
    else
      dsMember_person.open();

    String personid = isAdd ? "-1" : dsMember_person.getValue("personid");
    dsPersonRole.setQueryString(PERSON_ROLE_SQL + personid);
    dsPersonLimits.setQueryString(PERSON_LIMIT_SQL + personid);
    if(dsPersonRole.isOpen())
      dsPersonRole.refresh();
    else
      dsPersonRole.open();

    if(dsPersonLimits.isOpen())
      dsPersonLimits.refresh();
    else
      dsPersonLimits.open();
  }
}
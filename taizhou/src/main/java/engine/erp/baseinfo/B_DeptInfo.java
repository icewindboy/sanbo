package engine.erp.baseinfo;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.jsp.PageContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.borland.dx.dataset.Locate;
import com.borland.dx.dataset.SortDescriptor;
import com.sanbo.erp.domain.model.Department;
import com.sanbo.erp.domain.repository.DepartmentDao;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineDataView;
import engine.dataset.EngineRow;
import engine.dataset.RowMap;
import engine.project.CommonClass;
import engine.project.LookupBeanFacade;
import engine.project.SysConstant;
import engine.util.MessageFormat;
import engine.util.StringUtils;


/**
 * Title:        基础信息--部门信息维护
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      Golden
 * @author 江海岛
 * @version 1.0
 */

public class B_DeptInfo extends CommonClass //implements LookUp
{
  //各种操作的静态变量
  public static final int DEPT_ADD_CHILD = 10;//添加子结点
  public static final int DEPT_EDIT_VIEW = 11;//浏览或编辑部门结点
  public static final int DEPT_PERSON    = 12;//部门下的人员列表
  public static final int DEPT_DELETE    = 13;//删除部门结点
  public static final int DEPT_PASTE     = 14;//粘贴部门结点

  public static final int DEPT_TREE_POST = 21;//部门树的被动提交
  //与deptedit.jsp有关的操作
  public static final int DEPT_EDIT_POST = 22;//提交
  //

  private EngineDataSet dsDeptInfo = new EngineDataSet();//保存部门树状信息的数据集
  public  EngineDataSet dsDeptData = new EngineDataSet();//保存需要修改的数据集
  public  EngineDataSet dsRootDept = new EngineDataSet();//跟节点名称

  //private EngineDataSet dsLocateDept = new EngineDataSet();//用于查找会员或部门的信息，并提供导出下拉框

  private HtmlTree deptTree = null;//保存打印部门树的对象

  public  int tempOperate = DEPT_EDIT_VIEW;//临时保存上次的操作类型
  private String parentId = null;          //保存父结点ID
  private String firstid  = null;          //顶节点ID
  private String parentCode = "";          //保存父结点的编码
  private String deptid   = null;          //保存当前结点
  public  boolean isShowSelect = false;
  //得到部门树的根节点信息
  private static final String ROOT_DEPT_SQL = "SELECT * FROM bm WHERE deptid=0";
  //得到部门树数据（除了根节点）
  private static final String TREE_DEPT_SQL
      = "SELECT (deptid||'_'||firstid) deptid, dm, parentdeptid, mc, firstid FROM bm "
      + "WHERE isdelete=0 AND deptid>0";
  private static final String deptSQL
      = "SELECT * FROM bm WHERE deptid=";
  private static final String codeSQL
      = "SELECT count(*) FROM bm WHERE isdelete=0 AND dm='";
  //部门下人员个数的SQL语句
  private static final String PERSON_COUNT_SQL
      = "SELECT COUNT(*) FROM emp WHERE isdelete=0 AND deptid=";
  //子部门个数的SQL语句
  private static final String CHILD_COUNT_SQL
      = "SELECT COUNT(*) FROM bm WHERE isdelete=0 AND parentdeptid=";
  //更新子节点编码
  private static final String UPDATE_CHILD_CODE
      = "UPDATE bm SET dm ='{new}'||substr(dm, length('{old}')+1, length(dm)-length('{old}')) "
      + "WHERE length(dm) > length('{old}') AND substr(dm, 1, length('{old}')) = '{old}' AND isdelete=0";
  //跟子节点机构属性
  private static final String UPDATE_DEPT_PROP
      = "UPDATE bm SET ismember='{ismember}', firstid='{firstid}' "
      + "WHERE length(dm) > length('{old}') AND substr(dm, 1, length('{old}')) = '{old}' AND isdelete=0";
  //各个跟节点ID
  private static final String GET_FIRST_ID
      = "SELECT firstid FROM bm WHERE deptid='?'";

  public  boolean isDeptAdd = true;//部门的设置是否处在添加状态
  public  boolean isRoot = false;   //修改的是否是根节点（即总公司信息）
  private String  pathCode = null; //保存树展开的路径

  /**
   * 得到部门信息的实例
   * @param request jsp请求
   * @return 返回部门信息的实例
   */
  public static B_DeptInfo getInstance(HttpServletRequest request)
  {
    B_DeptInfo deptBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      deptBean = (B_DeptInfo)session.getAttribute("deptBean_aa");
      if(deptBean == null)
      {
        deptBean = new B_DeptInfo();
        session.setAttribute("deptBean_aa", deptBean);
      }
    }
    return deptBean;
  }
  /**
   * 构造函数
   */
  private B_DeptInfo() {
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
    if(dsDeptInfo != null){
      dsDeptInfo.closeDataSet();
      dsDeptInfo = null;
    }
    if(dsDeptData != null){
      dsDeptData.closeDataSet();
      dsDeptData = null;
    }
    if(dsRootDept != null){
      dsRootDept.closeDataSet();
      dsRootDept = null;
    }
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private void jbInit() throws Exception {
    setDataSetProperty(dsDeptInfo, TREE_DEPT_SQL);
    setDataSetProperty(dsDeptData, "SELECT * FROM bm WHERE 1<>1");
    setDataSetProperty(dsRootDept, ROOT_DEPT_SQL);
    //setDataSetProperty(dsLocateDept, LOCATE_DEPT_SQL);

    dsDeptInfo.setSort(new SortDescriptor("", new String[]{"dm"}, new boolean[]{false}, null, 0));
    //dsLocateDept.setSort(new SortDescriptor("", new String[]{"dm"}, new boolean[]{false}, null, 0));
    //dsDeptData.setSequence(new SequenceDescriptor(new String[]{"deptid"}, new String[]{"s_bm"}));
  }

  public String doPost(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    response.setHeader("Pragma","no-cache");
    response.setHeader("Cache-Control","no-cache");
    response.setDateHeader("Expires", 0);

    String sRe = "";
    if(request.getParameter("operate") == null || request.getParameter("operate").equals(""))
      return sRe;
    int operator = -1;
    try{
      operator = Integer.parseInt(request.getParameter("operate"));
    }
    catch(Exception ex){
      return sRe;
    }

    if(request.getMethod().equalsIgnoreCase("GET"))
    {
      String nodeID = request.getParameter("nodeID");
      if(nodeID == null || nodeID.trim().equals(""))
        return showJavaScript("location.href='../blank.htm';");

      String[] deptids = null;
      switch(operator)
      {
        //查看编辑记录
        case DEPT_EDIT_VIEW:
        case DEPT_ADD_CHILD:
          isDeptAdd = operator == DEPT_ADD_CHILD;
          deptids = StringUtils.parseString(nodeID, "_", "");
          if(isDeptAdd)
          {
            isRoot = false;
            parentId = deptids[0];
            firstid = deptids.length == 1 ? "0" : deptids[1].length() == 0 ? "0" : deptids[1];
            isShowSelect = firstid.equals("0");
          }
          else
          {
            deptid = deptids[0];
            isRoot = deptid.equals("0");
            firstid = isRoot ? "0" : deptids[1].length() == 0 ? "0" : deptids[1];
            isShowSelect = firstid.equals("0") || deptid.equals(firstid);
          }
          /*查找记录*/
          dsDeptData.setQueryString(deptSQL+ (isDeptAdd ? parentId : deptid));
          if(dsDeptData.isOpen())
            dsDeptData.refresh();
          else
            dsDeptData.openDataSet();

          if(dsDeptData.getRowCount() < 1)//没有记录,不是根结点
            if(!isDeptAdd || !parentId.equals("0"))
              return showJavaScript("alert('没有该条记录, 可能已经被别的用户更改！'); location.href='../blank.htm';");

          if(isDeptAdd)
            parentCode = parentId.equals("0") ? "" : dsDeptData.getValue("dm");

          break;
      }
    }
    else//POST方法
    {
      String deptcode = null, deptoldcode = null;
      String ismember = null, ismember_old = null;
      switch(operator)
      {
        //deptedit.jsp提交动作
        case DEPT_EDIT_POST:

          String selfCode = request.getParameter("self_code");
          if(!isRoot){
            String temp = checkInt(selfCode, "部门编码");
            if(temp !=null)
              return temp;
          }

          if(isDeptAdd){
            if(parentId == null || parentId.trim().equals(""))
              return showJavaScript("alert('请选择要添加下级部门的部门！'); location.href='../blank.htm';");
          }
          else
          {
            if(deptid == null || deptid.trim().equals(""))
              return showJavaScript("alert('请选择需要修改的部门！'); location.href='../blank.htm';");
          }

          RowMap row = new RowMap();
          row.put(request);
          //是否根节点
          if(!isRoot)
          {
            //判断编码是否已经存在
            String count = null;
            deptcode = request.getParameter("prefix_code")+ selfCode;
            ismember = row.get("ismember");
            if(isDeptAdd)
              count = dataSetProvider.getSequence(codeSQL + deptcode + "'");
            else{
              ismember_old = dsDeptData.getValue("ismember");
              deptoldcode = dsDeptData.getValue("dm");
              if(!deptoldcode.equals(deptcode))
                count = dataSetProvider.getSequence(codeSQL + deptcode + "'");
            }
            if(count != null && !count.equals("0"))
              return showJavaScript("alert('该编码已经存在!');");

            if(isDeptAdd)
            {
              dsDeptData.insertRow(false);
              String deptid = dataSetProvider.getSequence("s_bm");
              dsDeptData.setValue("deptid", deptid);
              dsDeptData.setValue("parentdeptid", parentId);
              dsDeptData.setValue("isdelete", "0");
              dsDeptData.setValue("firstid", ismember.equals("1") && firstid.equals("0") ? deptid : firstid);
            }
            else if(ismember_old.equals("0") && ismember.equals("1"))
              dsDeptData.setValue("firstid", deptid);
            else if(ismember_old.equals("1") && ismember.equals("0"))
              dsDeptData.setValue("firstid", "0");

            dsDeptData.setValue("ismember", ismember);
            dsDeptData.setValue("dm", deptcode);
            dsDeptData.setValue("mc", row.get("mc"));
            dsDeptData.setValue("dept_addr",  row.get("dept_addr"));
            dsDeptData.setValue("dept_phone", row.get("dept_phone"));
            dsDeptData.setValue("dept_fax",   row.get("dept_fax"));
            dsDeptData.setValue("dept_email", row.get("dept_email"));
            dsDeptData.setValue("dept_bank", row.get("dept_bank"));
            dsDeptData.setValue("dept_account", row.get("dept_account"));
//            dsDeptData.setValue("dept_post", row.get("dept_post"));
            dsDeptData.setValue("hzr", row.get("hzr"));
            dsDeptData.setValue("hzrdh", row.get("hzrdh"));
            dsDeptData.setValue("isWork", row.get("isWork"));
            dsDeptData.post();
            //同步树的记录
            if(isDeptAdd)
            {
              dsDeptData.saveChanges();
              dsDeptInfo.insertRow(false);
              dsDeptInfo.setValue("deptid", dsDeptData.getValue("deptid")+ "_"+ dsDeptData.getValue("firstid"));
              dsDeptInfo.setValue("parentdeptid", dsDeptData.getValue("parentdeptid"));
              dsDeptInfo.setValue("dm", dsDeptData.getValue("dm"));
              dsDeptInfo.setValue("mc", dsDeptData.getValue("mc"));
              dsDeptInfo.post();
            }
            //更改了部门编码
            else if(!deptoldcode.equals(deptcode) || !ismember_old.equals(ismember))
            {
              Hashtable table = new Hashtable();//
              table.put("old", deptoldcode);
              table.put("new", deptcode);
              boolean isMemChanged  = !ismember_old.equals(ismember);
              boolean isCodeChanged = !deptoldcode.equals(deptcode);
              String[] sqls = new String[isCodeChanged && isMemChanged ? 2 : 1];
              if(isMemChanged)
              {
                table.put("ismember", ismember);
                table.put("firstid", ismember.equals("1") ? deptid : "0");
                sqls[0] =engine.util.MessageFormat.format(UPDATE_DEPT_PROP, table);
              }
              if(isCodeChanged)
                sqls[isMemChanged ? 1 : 0] = MessageFormat.format(UPDATE_CHILD_CODE, table);

              dsDeptData.saveChanges(sqls, null);
              dsDeptInfo.refresh();
            }
            else
            {
              dsDeptData.saveChanges();
              if(locateDataSet(dsDeptInfo, "deptid", deptid, Locate.FIRST))
              {
                dsDeptInfo.setValue("dm", dsDeptData.getValue("dm"));
                dsDeptInfo.setValue("mc", dsDeptData.getValue("mc"));
                dsDeptInfo.post();
              }
            }

            //刷新lookup
            LookupBeanFacade.refreshLookup(SysConstant.BEAN_WORKSHOP);
            LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT);
            LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT_LIST);
            LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT_BRANCH);
            LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT_BOTH);
            LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT_ALL);
            //
            LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE);
            LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_BRANCH);
            //
            pathCode = deptcode;
          }
          else
          {
            dsRootDept.setValue("ismember", "0");
            dsRootDept.setValue("mc", row.get("mc"));
            dsRootDept.setValue("dept_addr",  row.get("dept_addr"));
            dsRootDept.setValue("dept_phone", row.get("dept_phone"));
            dsRootDept.setValue("dept_fax",   row.get("dept_fax"));
            dsRootDept.setValue("dept_email", row.get("dept_email"));
            dsRootDept.setValue("dept_bank", row.get("dept_bank"));
            dsRootDept.setValue("dept_account", row.get("dept_account"));
//            dsRootDept.setValue("dept_post", row.get("dept_post"));
            dsRootDept.setValue("hzr", row.get("hzr"));
            dsRootDept.setValue("hzrdh", row.get("hzrdh"));
            dsRootDept.post();
            try{
              dsRootDept.saveChanges();
            }catch(Exception ex){
              dsRootDept.reset();
              throw ex;
            }
          }
          sRe = showJavaScript("submitTree();");
          break;

        //部门的删除动作
        case DEPT_DELETE:
          String deleteStr = request.getParameter("curNodeID");
          String deleteId = StringUtils.parseString(deleteStr, "_")[0];
          if(deleteId == null || deleteId.trim().equals(""))
            return showJavaScript("alert('请选择部门！');");
          if(!dataSetProvider.getSequence(PERSON_COUNT_SQL + deleteId).equals("0"))
            return showJavaScript("alert('该部门下有人员！');");
          if(!dataSetProvider.getSequence(CHILD_COUNT_SQL + deleteId).equals("0"))
            return showJavaScript("alert('该部门下有子部门！');");
          /*查找记录*/
          dsDeptData.setQueryString(deptSQL+ deleteId.trim());
          if(dsDeptData.isOpen())
            dsDeptData.refresh();
          else
            dsDeptData.openDataSet();

          if(dsDeptData.getRowCount() < 1)
            return showJavaScript("alert('没有该条记录, 可能已经被别的用户更改！');");

          setDefaultOperate(operator);

          dsDeptData.setValue("isdelete", "1");
          dsDeptData.post();
          dsDeptData.saveChanges();

          //刷新lookup
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_WORKSHOP);
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT);
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT_LIST);
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT_BRANCH);
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT_BOTH);
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT_ALL);
          //
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE);
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_BRANCH);
          //同步树的数据
          EngineRow locateRow = new EngineRow(dsDeptInfo, "deptid");
          locateRow.setValue(0, deleteStr);
          if(dsDeptInfo.locate(locateRow, Locate.FIRST))
            dsDeptInfo.deleteRow();

          dsDeptInfo.prior();
          pathCode = dsDeptInfo.getValue("dm");
          break;

        //部门的张贴动作
        case DEPT_PASTE:
          String cutStr = request.getParameter("cutNodeID");
          String pasteStr = request.getParameter("curNodeID");
          String cutId = StringUtils.parseString(cutStr, "_")[0];
          String pasteId = StringUtils.parseString(pasteStr, "_")[0];
          if(cutId == null || cutId.trim().equals(""))
            return showJavaScript("alert('请选择需要剪切部门！');");
          if(pasteId == null || pasteId.trim().equals(""))
            return showJavaScript("alert('请选择粘贴的目标部门！');");
          cutId = cutId.trim();
          pasteId = pasteId.trim();
          String tmp_firstid = null;
          String tmp_ismember = null;
          /*查找记录*/
          if(!pasteId.equals("0")){//是否是根节点
            dsDeptData.setQueryString(deptSQL+ pasteId);
            if(dsDeptData.isOpen())
              dsDeptData.refresh();
            else
              dsDeptData.openDataSet();
            if(dsDeptData.getRowCount() < 1)
              return showJavaScript("alert('没有粘贴部门的记录, 可能已经被别的用户更改！');");
            deptcode = dsDeptData.getValue("dm");
            tmp_firstid = dsDeptData.getValue("firstid");
            tmp_ismember = dsDeptData.getValue("ismember");
          }
          else{
            deptcode = "";
            tmp_firstid = "0";
            tmp_ismember = "0";
          }

          dsDeptData.setQueryString(deptSQL+ cutId);
          if(dsDeptData.isOpen())
            dsDeptData.refresh();
          else
            dsDeptData.openDataSet();
          if(dsDeptData.getRowCount() < 1)
            return showJavaScript("alert('没有剪切部门的记录, 可能已经被别的用户更改！');");

          deptoldcode = dsDeptData.getValue("dm");//剪切部门的编号
          if(!pasteId.equals("0")){
            if(deptcode.startsWith(deptoldcode))
              return showJavaScript("alert('不能将父部门粘贴到子部门下！')");
          }
          //更新代码和顶节点ID
          deptcode = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('bm','dm','"
            + deptcode +"','isdelete=0') from dual");
          String[] updateSql = new String[2];
          Hashtable table = new Hashtable();//
          table.put("old", deptoldcode);
          table.put("new", deptcode);
          updateSql[1] = MessageFormat.format(UPDATE_CHILD_CODE, table);
          table.put("ismember", tmp_ismember);
          table.put("firstid", tmp_ismember.equals("1") ? tmp_firstid : "0");
          updateSql[0] = MessageFormat.format(UPDATE_DEPT_PROP, table);
          //
          dsDeptData.setValue("firstid", tmp_ismember.equals("1") ? tmp_firstid : "0");
          dsDeptData.setValue("ismember", tmp_ismember);
          dsDeptData.setValue("dm",deptcode);
          dsDeptData.setValue("parentdeptid", pasteId);
          dsDeptData.saveChanges(updateSql, null);
          //刷行lookup
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_WORKSHOP);
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT);
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT_LIST);
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT_BRANCH);
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT_BOTH);
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_DEPT_ALL);
          //
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE);
          LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_BRANCH);

          setDefaultOperate(operator);
          //同步树的数据
          dsDeptInfo.refresh();
          pathCode = deptcode;
          break;

        //刷新树
        case DEPT_TREE_POST:
          setDefaultOperate(operator);
          break;
      }
    }
    return sRe;
  }

  /**
   * 设置默认的操作
   * @param operate 当前操作
   */
  private void setDefaultOperate(int operate)
  {
    tempOperate = operate;
    if(tempOperate < DEPT_ADD_CHILD || tempOperate > DEPT_PERSON)
      tempOperate = DEPT_EDIT_VIEW;
  }
  /**
   * 得到部门树的HTML文本的字符串
   * @param pageContext 返回部门树的HTML字符串
   */
  public void getDeptTree(PageContext pageContext)
  {
      ApplicationContext context = new ClassPathXmlApplicationContext("/spring/spring-sanbo.xml");
      //ComboPooledDataSource dataSource = (ComboPooledDataSource)context.getBean("dataSource");
      DepartmentDao departmentDao = (DepartmentDao)context.getBean("departmentDao");
      Department department = departmentDao.getDepartmentById(1);
      
    if(!dsDeptInfo.isOpen())
      dsDeptInfo.openDataSet();
    if(!dsRootDept.isOpen())
    {
      dsRootDept.openDataSet();
      if(dsRootDept.getRowCount() < 1)
      {
        dsRootDept.insertRow(false);
        dsRootDept.setValue("deptid","0");
        dsRootDept.setValue("parentdeptid", "0");
        dsRootDept.setValue("mc", "0");
        dsRootDept.setValue("mc", "公司名称");
        dsRootDept.setValue("isdelete", "0");
        dsRootDept.setValue("ismember", "0");
        dsRootDept.post();
        dsRootDept.saveChanges();
      }
    }
    if(deptTree == null)
    {
      deptTree = new HtmlTree();
      deptTree.setTreeProperty(dsDeptInfo, dsRootDept.getValue("mc"), "deptid", "dm", "parentdeptid", "mc");
    }
    else
      deptTree.rootCaption = dsRootDept.getValue("mc");
    deptTree.printHtmlTree(pageContext, pathCode);
  }

  /**
   * 得到部门信息的数据视图
   * @return 返回部门信息的数据视图
   *
  public EngineDataView getDeptInfoView()
  {
    if(!dsDeptInfo.isOpen())
      dsDeptInfo.open();
    return dsDeptInfo.cloneEngineDataView();
  }*/

  /**
   * 提取添加部门结点的代码
   * @return 返回部门编码
   * @throws Exception 异常
   */
  public String[] getDeptCode() throws Exception
  {
    String code = null;
    if(isDeptAdd)
      code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('bm','dm','"
           + parentCode +"','isdelete=0') from dual");
    else
      code = dsDeptData.getValue("dm");
    int leng = code.length()-2;
    return new String[]{code.substring(0, leng), code.substring(leng)};
  }

  /**
   * 得到所有的部门信息列表
   * @return 部门信息列表
   */
  public synchronized EngineDataView getAllDept()
  {
    if(!dsDeptInfo.isOpen())
      dsDeptInfo.open();
    return dsDeptInfo.cloneEngineDataView();
  }
  /**
   * 得到第一行的部门ID
   * @return 部门信息列表
  public final String getFisrtRowDept()
  {
    EngineDataView ds = getAllDept();
    ds.first();
    String deptid = ds.getValue("deptid");
    ds.close();
    return deptid;
  }
  /**
   * 根据部门ID或会员ID提取部门或会员名称
   * @param deptid 部门ID或会员ID
   * @return 部门或会员名称
   *
  public synchronized String getDeptName(String deptid)
  {
    if(deptid == null || deptid.equals(""))
      return "";

    if(!dsLocateDept.isOpen())
      dsLocateDept.open();
    EngineDataSet ds = dsLocateDept;
    if(locateDataSet(dsLocateDept, "deptid", deptid, Locate.FIRST))
      //若是会员返回摊位号
      return ds.getValue("mc"); //ds.getValue("ismember").equals("1") ? ds.getValue("dm")
    else
      return "";
  }

  /**
   * 得到部门列表的页面的<select>控件的<option></option>的字符串
   * @param currDeptid 当前部门ID，即是需要选定的部门ID
   * @return 部门信息列表
   *
  public synchronized String getDeptForOption(String currDeptid)
  {
    if(!dsLocateDept.isOpen())
      dsLocateDept.open();

    StringBuffer buf = new StringBuffer();
    EngineDataSet ds = dsLocateDept;//getAllDept();
    ds.first();
    while(ds.inBounds())
    {
      if(ds.getValue("ismember").equals("1"))//是否是会员
      {
        ds.next();
        continue;
      }
      String deptcode = ds.getValue("dm");
      createDeptOptionTree(ds, deptcode, 0, currDeptid, buf);
    }
    return buf.toString();
  }
  /**
   * 得到会员部门列表的页面的select控件的option的字符串
   * @param memberId 当前会员部门ID，即是需要选定的部门ID
   * @return 会员部门信息列表
   *
  public synchronized String getMemberForOption(String memberId)
  {
    if(!dsLocateDept.isOpen())
      dsLocateDept.open();
    return dataSetToOption(dsLocateDept, "deptid", "dm", memberId, "ismember", "1");
  }
  /**
   * 创建各个树结点
   * @param fatherCode 父结点编号
   * @param level 层
   * @return 返回是否还有子结点
   *
  private boolean createDeptOptionTree(EngineDataSet ds, String fatherCode, int level,
                                       String currDeptid, StringBuffer buf)
  {
    String deptid    = ds.getValue("deptid");
    String deptcode  = ds.getValue("dm");
    String deptname  = ds.getValue("mc");
    boolean hasChild = false;//是否还有子结点
    boolean isLeaf = false;

    if(ds.next())
    {
      String nextCode = ds.getValue("dm");
      isLeaf = !nextCode.startsWith(deptcode);
      hasChild = nextCode.startsWith(fatherCode);
    }
    else
    {
      isLeaf = true;
      hasChild = false;
    }

    buf.append("<option value=");
    buf.append(deptid);
    if(deptid.equals(currDeptid))
      buf.append(" selected");
    buf.append(">");
    for(int i=0; i<level; i++)
      buf.append("&nbsp;");

    buf.append(deptname);
    buf.append("</option>");

    if(!isLeaf)
    {
      //打印子结点
      while(true)
      {
        boolean isHasChild = createDeptOptionTree(ds, deptcode, level+1, currDeptid, buf);
        if(!isHasChild)
          break;
      }

      hasChild = ds.inBounds() && ds.getValue("dm").startsWith(fatherCode);
    }
    return hasChild;
  }*/
 /*
 public static void main(String[] agv){
   try{
     double b  = Double.parseDouble("NaN");
     System.out.println(b);
   }
   catch(NumberFormatException ex){
   }
 }*/
}
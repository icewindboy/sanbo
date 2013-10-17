package engine.erp.system;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.*;
import java.math.BigDecimal;

import com.borland.dx.dataset.*;
import engine.dataset.*;
import engine.util.*;
import engine.util.log.LogHelper;
import engine.project.*;
import engine.common.LoginBean;
import engine.action.BaseAction;
import engine.erp.baseinfo.*;

/**
 * <p>Title: 基础维护－角色维护</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public class B_Role extends CommonClass
{
  //与角色操作相关变量
  public static final int OPERATE_ADD    = 20;//添加
  public static final int OPERATE_EDIT   = 21;//浏览或编辑
  public static final int OPERATE_DEL    = 22;//删除
  public static final int OPERATE_POST   = 23;//提交

  private static final String ROLE_LIMIT_SQL = "SELECT * FROM rolelimit WHERE roleid='@'";//提取角色权限的SQL语句
  //private static final String DEL_ROLE_LIMIT_SQL = "DELETE FROM rolelimit WHERE roleid='@'";//提取角色权限的SQL语句

  public  EngineDataSet dsRole  = new EngineDataSet();
  private EngineDataSet dsRoleLimits  = new EngineDataSet();
  public  RowMap rowInfo = new RowMap(); //添加行或修改行的引用

  private HtmlTree nodeTree = null;//保存打印部门树的对象
  private Vector vLimitTreeInfo = null;  //保存网页要显示的结点树的权限信息（如&nbsp;<input type='checkbox'>）

  public boolean isAdd = true;//时候在添加状态
  public String nodeTreeInfo = null; //保存结点权限树的HTML文本的字符串

  /**
   * 角色列表的实例
   * @param request jsp请求
   * @return 角色列表的实例
   */
  public static B_Role getInstance(HttpServletRequest request)
  {
    B_Role roleBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      roleBean = (B_Role)session.getAttribute("roleBean_aa");
      if(roleBean == null)
      {
        roleBean = new B_Role();
        session.setAttribute("roleBean_aa", roleBean);
      }
    }
    return roleBean;
  }
  /**
   * 构造函数
   */
  private B_Role()
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
    dsRole.close();
    dsRole = null;
    dsRoleLimits.close();
    dsRoleLimits = null;

    vLimitTreeInfo = null;
    rowInfo = null;
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private void jbInit() throws Exception {
    setDataSetProperty(dsRole, "SELECT * FROM roleinfo");
    setDataSetProperty(dsRoleLimits, BaseAction.combineSQL(ROLE_LIMIT_SQL, "@", new String[]{"-1"}));

    dsRole.setSort(new SortDescriptor("", new String[]{"rolecode"}, new boolean[]{false}, null, 0));
    dsRoleLimits.setSequence(new SequenceDescriptor(new String[]{"rolelimitid"}, new String[]{"s_rolelimit"}));
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
    response.setHeader("Pragma","no-cache");
    response.setHeader("Cache-Control","no-cache");
    response.setDateHeader("Expires", 0);

    String sRe = "";
    if(request.getParameter("operate") == null || request.getParameter("operate").equals(""))
      return sRe;
    int operate = -1;
    try{
      operate = Integer.parseInt(request.getParameter("operate"));
    }
    catch(Exception ex){
      return sRe;
    }

    try{
      sRe = doService(request, response, operate);
    }
    catch(Exception ex){
      if(dsRole.changesPending())
        dsRole.reset();
      sRe = LoginBean.showMessage(ex.getMessage(), true);
      log.error("doService", ex);
    }

    return sRe;
  }
  /**
   * BEAN的逻辑部分
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @param operate 网页的操作类型
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private String doService(HttpServletRequest request, HttpServletResponse response, int operate) throws Exception
  {
    String sRe= "";
    switch(operate)
    {
      case Operate.INIT:
        B_LimitsInfo.getInstance(request).init();
        break;
      //
      case OPERATE_ADD:
      case OPERATE_EDIT:
        isAdd = operate == OPERATE_ADD;
        if(!isAdd){
          dsRole.goToRow(Integer.parseInt(request.getParameter("rownum")));
          dsRoleLimits.setQueryString(BaseAction.combineSQL(ROLE_LIMIT_SQL, "@", new String[]{dsRole.getValue("roleid")}));
          if(dsRoleLimits.isOpen())
            dsRoleLimits.refresh();
          else
            dsRoleLimits.open();
        }
        else if(!dsRoleLimits.isOpen())
          dsRoleLimits.open();

        initRowInfo(isAdd);
        makeLimitTreeInfo(request, isAdd);
        sRe = LoginBean.showJavaScript("location.href='roleedit.jsp';");
        break;
      //
      case OPERATE_POST:
        //校验数据
        rowInfo.put(request);
        String rolecode = rowInfo.get("rolecode");
        String rolename = rowInfo.get("rolename");
        String memo = rowInfo.get("memo");
        if(rolecode.equals(""))
          return LoginBean.showJavaScript("alert('角色编码不能为空！');");
        if(rolename.equals(""))
          return LoginBean.showJavaScript("alert('角色名称不能为空！');");

        if(isAdd || !rolecode.equals(dsRole.getValue("rolecode")))
        {
          String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('roleinfo','rolecode','"+rolecode+"') from dual");
          if(!count.equals("0"))
          {
            if(isAdd)
              initRowInfo(true);
            return LoginBean.showJavaScript("alert('编码("+ rolecode +")已经存在!');");
          }
        }
        //保存角色数据
        String roleid = isAdd ? dataSetProvider.getSequence("s_roleinfo") : dsRole.getValue("roleid");
        if(isAdd)
        {
          dsRole.insertRow(false);
          dsRole.setValue("roleid", roleid);
        }
        dsRole.setValue("rolecode", rolecode);
        dsRole.setValue("rolename", rolename);
        dsRole.setValue("memo", memo);
        dsRole.post();
        //处理角色权限
        boolean isChange = rowInfo.get("limitchange").equals("1");
        if(!isAdd && isChange)
          dsRoleLimits.deleteAllRows();

        if(isAdd || isChange)
        {
          String[] chklimits = request.getParameterValues("chklimit");
          for(int i=0; chklimits!=null && i<chklimits.length; i++)
          {
            String[] limitinfo = parseString(chklimits[i], "_");
            if(limitinfo == null || limitinfo.length<3)
              continue;
            dsRoleLimits.insertRow(false);
            dsRoleLimits.setValue("rolelimitid", "-1");
            dsRoleLimits.setValue("roleid", roleid);
            dsRoleLimits.setValue("limitid", limitinfo[2]);
            dsRoleLimits.post();
          }
          dsRole.saveDataSets(new EngineDataSet[]{dsRole, dsRoleLimits}, null);
        }
        else
          dsRole.saveChanges();

        sRe = LoginBean.showJavaScript("location.href='rolelist.jsp';");
        break;
      //
      case OPERATE_DEL:
        dsRole.goToRow(Integer.parseInt(request.getParameter("rownum")));
        dsRoleLimits.setQueryString(BaseAction.combineSQL(ROLE_LIMIT_SQL, "@", new String[]{dsRole.getValue("roleid")}));
        if(dsRoleLimits.isOpen())
          dsRoleLimits.refresh();
        else
          dsRoleLimits.open();
        dsRoleLimits.deleteAllRows();
        dsRole.deleteRow();
        dsRole.saveDataSets(new EngineDataSet[]{dsRoleLimits, dsRole}, null);
        break;
    }
    return sRe;
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   */
  private void initRowInfo(boolean isAdd) throws Exception
  {
    if(!dsRole.isOpen())
      dsRole.open();
    //是否时添加操作
    if(rowInfo.size() > 0)
      rowInfo.clear();

    if(isAdd){
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('roleinfo','rolecode','','',6) from dual");
      rowInfo.put("rolecode", code);
    }
    else
      rowInfo.put(dsRole);
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

    getLimitTreeInfo(dvNodeInfo, dvLimitlist, dsRoleLimits, isAdd, vLimitTreeInfo);

    //得到结点权限树的HTML文本的字符串
    if(nodeTree == null)
    {
      nodeTree = new HtmlTree();
      nodeTree.setMouseUpFuction(null);
      nodeTree.setMouseOutFuction(null);
      nodeTree.setTreeTableClass("limittreebox");
    }
    nodeTree.setTreeProperty(dvNodeInfo, "界面权限树", "nodeid", "nodecode", "parentnodeid", "nodename", vLimitTreeInfo);
    nodeTreeInfo = nodeTree.printHtmlTree(true);
    dvNodeInfo.close();
    dvNodeInfo = null;
    dvLimitlist.close();
    dvLimitlist = null;
  }

  /**
   * 得到结点数的权限信息
   * @param dvNodeInfo 所有界面的数据
   * @param dvLimitlist 所有界面具有的权限的数据
   * @param dsHasLimits 人员或角色具有权限的数据
   * @param isAdd 是否是添加记录状态
   * @param vLimitTreeInfo 保存网页要显示的结点树的权限信息（如&nbsp;<input type='checkbox'>）
   * @return 保存网页要显示的结点树的权限信息checkbox的列表
   */
  public synchronized static List getLimitTreeInfo(EngineDataView dvNodeInfo,
      EngineDataView dvLimitlist, DataSet dsHasLimits, boolean isAdd, List vLimitTreeInfo)
  {
    if(vLimitTreeInfo==null)
      vLimitTreeInfo = new ArrayList();
    String nodeid = null;
    //用于查询一个界面的所有权限的row
    EngineRow rowLimitlist = new EngineRow(dvLimitlist, "nodeid");
    //查询是否具有该权限的row
    EngineRow rowHasLimit = isAdd ? null : new EngineRow(dsHasLimits, "limitid");
    dvNodeInfo.first();
    for(int i=0; i<dvNodeInfo.getRowCount(); i++)
    {
      StringBuffer buf = new StringBuffer();
      nodeid = dvNodeInfo.getValue("nodeid");
      rowLimitlist.setValue(0, nodeid);
      //保存临时的节点id
      String temp = nodeid;
      dvLimitlist.first();
      int locate = Locate.FIRST;//&nbsp;<input type='checkbox'>
      while(dvLimitlist.locate(rowLimitlist, locate))
      {
        locate = Locate.NEXT;
        //字符串:节点id_特定操作权限id(添加修改删除权限的id)_节点操作权限id
        String value = nodeid +"_"+ dvLimitlist.getValue("priviligeid") +"_"+ dvLimitlist.getValue("limitid");
        buf.append("&nbsp;<input type=checkbox name=chklimit class=checkbox onChange=chklimit_onchange() value='");
        buf.append(value + "' id='chklimit").append(value).append("'");
        //查询当前角色是否具有该权限
        if(!isAdd)
        {
          rowHasLimit.setBigDecimal(0, dvLimitlist.getBigDecimal("limitid"));
          if(dsHasLimits.locate(rowHasLimit, Locate.FIRST))
            buf.append(" checked");
        }

        buf.append(">");
        buf.append(dvLimitlist.getValue("priviligename"));
      }
      vLimitTreeInfo.add(buf);
      dvNodeInfo.next();
    }
    return vLimitTreeInfo;
  }

  /**
   * 得到所有的角色列表
   * @return 返回数据集(EngineDataView)
   */
  public synchronized EngineDataView getRoleData()
  {
    if(!dsRole.isOpen())
      dsRole.open();
    return dsRole.cloneEngineDataView();
  }

  /**
   * 根据部门ID提取部门名称
   * @param deptid 部门序号
   * @return 部门名称
   */
  public synchronized String getRoleName(String roleid)
  {
    if(roleid == null || roleid.equals(""))
      return "";
    String rolename= null;
    EngineDataView ds = getRoleData();
    if(locateDataSet(ds, "roleid", roleid, Locate.FIRST))
      rolename = ds.getValue("rolename");
    else
      rolename = "";
    ds.close();
    ds = null;
    return rolename;
  }
}

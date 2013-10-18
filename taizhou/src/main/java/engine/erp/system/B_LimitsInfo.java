package engine.erp.system;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.*;

import com.borland.dx.dataset.*;
import engine.dataset.*;
import engine.util.*;
import engine.util.log.LogHelper;
import engine.project.*;
import engine.common.LoginBean;

/**
 * <p>Title: 基础维护－权限信息维护</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public class B_LimitsInfo extends CommonClass
{
  /*//与地区操作相关变量
  public static final int OPERATE_ADD    = 20;//添加
  public static final int OPERATE_EDIT   = 21;//浏览或编辑
  public static final int OPERATE_DEL    = 22;//删除
  public static final int OPERATE_POST   = 23;//提交
  */
  private static final String NODE_INFO_SQL = "SELECT * FROM nodeinfo WHERE isdelete=0";
  private static final String LIMIT_LIST_SQL= "SELECT v.limitid, v.nodeid, v.priviligeid, v.priviligecode, v.priviligename FROM VW_NODE_LIMITS v";

  private EngineDataSet dsNodeInfo  = new EngineDataSet();
  private EngineDataSet dsLimitlist = new EngineDataSet();
  //private EngineDataSet dsNodePrivilige  = new EngineDataSet();
  //public  RowMap rowInfo = new RowMap();  //添加行或修改行的引用

  private boolean isNodeInfoInit = true;
  private boolean isLimitListInit = true;
  /**
   * 外币的实例
   * @param request jsp请求
   * @return 外币的实例
   */
  public static B_LimitsInfo getInstance(HttpServletRequest request)
  {
    B_LimitsInfo limitsInfoBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      limitsInfoBean = (B_LimitsInfo)session.getAttribute("limitsInfoBean_aa");
      if(limitsInfoBean == null)
      {
        limitsInfoBean = new B_LimitsInfo();
        session.setAttribute("limitsInfoBean_aa", limitsInfoBean);
      }
    }
    return limitsInfoBean;
  }

  /**
   * 构造函数
   */
  private B_LimitsInfo()
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
    if(dsNodeInfo != null)
    {
      dsNodeInfo.close();
      dsNodeInfo = null;
    }
    if(dsLimitlist != null){
      dsLimitlist.close();
      dsLimitlist = null;
    }
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private void jbInit() throws Exception {
    setDataSetProperty(dsNodeInfo, NODE_INFO_SQL);
    setDataSetProperty(dsLimitlist, LIMIT_LIST_SQL);

    dsNodeInfo.setSort(new SortDescriptor("", new String[]{"nodecode"}, new boolean[]{false}, null, 0));
    dsNodeInfo.setSequence(new SequenceDescriptor(new String[]{"nodeid"}, new String[]{"s_nodeinfo"}));

    dsLimitlist.setMasterLink(new MasterLinkDescriptor(dsNodeInfo, new String[]{"nodeid"}, new String[]{"nodeid"}, false, false, false));
    dsLimitlist.setSort(new SortDescriptor("", new String[]{"nodeid","priviligecode"}, new boolean[]{false,false}, null, 0));
    //dsNodePrivilige.setSort(new SortDescriptor("", new String[]{"priviligecode"}, new boolean[]{false}, null, 0));
    //dsNodePrivilige.setSequence(new SequenceDescriptor(new String[]{"priviligeid"}, new String[]{"s_nodePrivilige"}));
  }

  public void init()
  {
    this.isNodeInfoInit = true;
    this.isLimitListInit = true;
  }

  private void openNodeInfoData()
  {
    if(isNodeInfoInit)
    {
      if(dsNodeInfo.isOpen())
        dsNodeInfo.refresh();
      else
        dsNodeInfo.open();

      isNodeInfoInit = false;
    }
    else if(dsNodeInfo.isOpen())
      dsNodeInfo.open();
  }

  private void openLimitListData()
  {
    if(isLimitListInit)
    {
      if(dsLimitlist.isOpen())
        dsLimitlist.refresh();
      else
        dsLimitlist.open();

      isLimitListInit = false;
    }
    else if(!dsLimitlist.isOpen())
      dsLimitlist.open();
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
    /*response.setHeader("Pragma","no-cache");
    response.setHeader("Cache-Control","no-cache");
    response.setDateHeader("Expires", 0);
    */
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
      if(dsNodeInfo.changesPending())
        dsNodeInfo.reset();
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
    /*switch(operate)
    {
      case Operate.INIT:
        break;
      //
      case OPERATE_ADD:
      case OPERATE_EDIT:
        isAdd = operate == OPERATE_ADD;
        if(!isAdd)
          dsNodeInfo.goToRow(Integer.parseInt(request.getParameter("rownum")));
        initRowInfo(isAdd);
        sRe = LoginBean.showJavaScript("location.href='roleedit.jsp';");
        break;
      //
      case OPERATE_POST:
        rowInfo.put(request);
        String rolecode = rowInfo.get("rolecode");
        String rolename = rowInfo.get("rolename");
        String memo = rowInfo.get("memo");
        if(rolecode.equals(""))
          return LoginBean.showJavaScript("alert('角色编码不能为空！');");
        if(rolename.equals(""))
          return LoginBean.showJavaScript("alert('角色名称不能为空！');");

        if(isAdd || !rolecode.equals(dsNodeInfo.getValue("rolecode")))
        {
          String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('roleinfo','rolecode','"+rolecode+"') from dual");
          if(!count.equals("0"))
          {
            if(isAdd)
              initRowInfo(true);
            return LoginBean.showJavaScript("alert('编码("+ rolecode +")已经存在!');");
          }
        }

        if(isAdd)
        {
          dsNodeInfo.insertRow(false);
          dsNodeInfo.setValue("roleid", "-1");
        }
        dsNodeInfo.setValue("rolecode", rolecode);
        dsNodeInfo.setValue("rolename", rolename);
        dsNodeInfo.setValue("memo", memo);
        dsNodeInfo.post();
        dsNodeInfo.saveChanges();

        sRe = LoginBean.showJavaScript("location.href='rolelist.jsp';");
        break;
      //
      case OPERATE_DEL:
        dsNodeInfo.goToRow(Integer.parseInt(request.getParameter("rownum")));
        dsNodeInfo.deleteRow();
        dsNodeInfo.saveChanges();
        break;
    }*/
    return sRe;
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加

  private void initRowInfo(boolean isAdd) throws Exception
  {
    if(!dsNodeInfo.isOpen())
      dsNodeInfo.open();
    //是否时添加操作
    if(rowInfo.size() > 0)
      rowInfo.clear();

    if(isAdd){
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('roleinfo','rolecode','','',6) from dual");
      rowInfo.put("rolecode", code);
    }
    else
      rowInfo.put(dsNodeInfo);
  }
  */

  public synchronized EngineDataView getLimitlistData()
  {
    openNodeInfoData();
    return dsLimitlist.cloneEngineDataView();
  }

  public synchronized EngineDataView getNodeInfoData()
  {
    openLimitListData();
    return dsNodeInfo.cloneEngineDataView();
  }
/*
  public EngineDataView getNodePriviligeData()
  {
    if(!dsNodePrivilige.isOpen())
      dsNodePrivilige.open();
    return dsNodePrivilige.cloneEngineDataView();
  }
  /**
   * 根据界面ID得到该界面所拥有的所有权限的记录
   * @param interna1Row dsNodeInfo数据集的内部行数
   * @return 界面的所有权限的记录
  public synchronized RowMap[] getNodeAllLimits(long interna1Row)
  {
    if(!dsLimitlist.isOpen())
      dsLimitlist.open();
    if(!dsNodePrivilige.isOpen())
      dsNodePrivilige.open();

    dsNodeInfo.goToInternalRow(interna1Row);
    dsLimitlist.first();
    for(int i=0; i<dsLimitlist.getRowCount(); i++)
    {
      dsLimitlist.next();
    }
  }
  /**
   * 根据权限的ID得到权限一行信息包括权限编码，名称
   * @param priviligeId 权限ID
   * @return 权限的信息

  public synchronized RowMap getPriviligeInfo(String priviligeId)
  {
    if(!dsNodePrivilige.isOpen())
      dsNodePrivilige.open();
    if(nodePrivigeRow == null)
      nodePrivigeRow = new EngineRow(dsNodePrivilige, "priviligeid");

    nodePrivigeRow.setValue(0, priviligeId);

    RowMap row = null;
    if(dsNodePrivilige.locate(nodePrivigeRow, Locate.FIRST))//定位数据集
      row = new RowMap(dsNodePrivilige);

    return row;
  }
  */
}
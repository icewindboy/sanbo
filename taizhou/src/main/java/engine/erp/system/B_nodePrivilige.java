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
 * <p>Title: 基础信息－权限维护</p>
 * <p>Description: 基础信息－权限维护</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public class B_nodePrivilige extends SingleOperate
{
  private EngineRow locateRow = null;
  public  String retuUrl = null;

  /**
   * 得到权限维护的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回权限维护的实例
   */
  public static B_nodePrivilige getInstance(HttpServletRequest request)
  {
    B_nodePrivilige nodePriviligeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_nodePriviligeBean";
      nodePriviligeBean = (B_nodePrivilige)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(nodePriviligeBean == null)
      {
        nodePriviligeBean = new B_nodePrivilige();
        session.setAttribute(beanName, nodePriviligeBean);
      }
    }
    return nodePriviligeBean;
  }


  public B_nodePrivilige()
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
    setDataSetProperty(dsOneTable, "SELECT * FROM nodePrivilige");
    dsOneTable.setSort(new SortDescriptor("", new String[]{"priviligeCode"}, new boolean[]{false}, null, 0));
    dsOneTable.setSequence(new SequenceDescriptor(new String[]{"priviligeId"}, new String[]{"s_nodePrivilige"}));
  }

  /**
   * Implement this engine.project.OperateCommon abstract method
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    dsOneTable.close();
    dsOneTable = null;
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
    String priviligeCode = rowInfo.get("priviligeCode");
    String priviligeName = rowInfo.get("priviligeName");
    if(priviligeCode.equals(""))
      return LoginBean.showJavaScript("alert('编码不能为空！');");
    if(priviligeName.equals(""))
      return LoginBean.showJavaScript("alert('名称不能为空！');");

    if(!isAdd)
      ds.goToInternalRow(editrow);

    if(isAdd || !priviligeCode.equals(ds.getValue("priviligeCode")))
    {
      String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('nodePrivilige','priviligeCode','"+priviligeCode+"') from dual");
      if(!count.equals("0"))
      {
        return LoginBean.showJavaScript("alert('编码("+ priviligeCode +")已经存在!');");
      }
    }

    if(isAdd)
    {
      ds.insertRow(false);
      ds.setValue("priviligeId", "-1");
    }
    ds.setValue("priviligeCode", priviligeCode);
    ds.setValue("priviligeName", priviligeName);
    ds.setValue("priviligeMemo", rowInfo.get("priviligeMemo"));
    ds.post();
    ds.saveChanges();
    return LoginBean.showJavaScript("parent.hideInterFrame();");
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
    ds.deleteRow();
    ds.saveChanges();
    return "";
  }
  /**
   * 其他操作，父类未定义的操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @param operate 网页的操作类型
   * @return 返回HTML或javascipt的语句
   */
  protected final String otherOperate(HttpServletRequest request, HttpServletResponse response, int operate)
  {
    switch(operate){
      case Operate.INIT:
        retuUrl = request.getParameter("src");
        retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
        break;
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

    if(isAdd){
      ;
    }
    else
      rowInfo.put(getOneTable());
  }

  /*得到表对象，Implement this engine.project.OperateCommon abstract method*/
  public final EngineDataSet getOneTable()
  {
    if(!dsOneTable.isOpen())
      dsOneTable.open();
    return dsOneTable;
  }

  /*得到一列的信息，Implement this engine.project.OperateCommon abstract method*/
  public final RowMap getRowinfo() {    return rowInfo;  }

  /**
   * 得到权限列表的页面的<select>控件的<option></option>的字符串
   * @param mixId 初始化选中的权限ID
   * @param lx 权限类型
   * @return 权限列表
   */
  public synchronized final String getNodePriviligeForOption(String priviligeId)
  {
    return engine.action.BaseAction.dataSetToOption(getOneTable(), "priviligeId", "priviligeName", priviligeId, null, null);
  }
  /**
   * 根据权限ID得到权限名称
   * @param bankId 初始化选中的权限ID
   * @return 权限名称
   */
  public final String getPriviligeName(String priviligeId)
  {
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "priviligeId");
    locateRow.setValue(0, priviligeId);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      return getOneTable().getValue("priviligeName");
    else
      return "";
  }
  /**
   * 根据权限ID得到权限的一行信息
   * @param bankId 初始化选中的权限ID
   * @return 权限的一行信息
   */
  public final RowMap getPriviligeRow(String priviligeId)
  {
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "priviligeId");
    locateRow.setValue(0, priviligeId);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      return new RowMap(getOneTable());
    else
      return null;
  }
}

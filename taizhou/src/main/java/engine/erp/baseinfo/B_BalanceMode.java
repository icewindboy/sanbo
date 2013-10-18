package engine.erp.baseinfo;

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
 * <p>Title: 基础维护－结算方式维护</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public class B_BalanceMode extends CommonClass
{
  //与地区操作相关变量
  public static final int OPERATE_ADD    = 20;//添加
  public static final int OPERATE_EDIT   = 21;//浏览或编辑
  public static final int OPERATE_DEL    = 22;//删除
  public static final int OPERATE_POST   = 23;//提交

  public  boolean isAdd = true;    //是否在添加状态
  public EngineDataSet dsBalance  = new EngineDataSet();
  public RowMap rowInfo    = new RowMap(); //添加行或修改行的引用

  private long editrow = 0;
  private EngineRow locateRow = null;

  /**
   * 结算方式的实例
   * @param request jsp请求
   * @return 返回结算方式的实例
   */
  public static B_BalanceMode getInstance(HttpServletRequest request)
  {
    B_BalanceMode balanceModeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      balanceModeBean = (B_BalanceMode)session.getAttribute("balanceModeBean_aa");
      if(balanceModeBean == null)
      {
        balanceModeBean = new B_BalanceMode();
        session.setAttribute("balanceModeBean_aa", balanceModeBean);
      }
    }
    return balanceModeBean;
  }

  private B_BalanceMode()
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
    dsBalance.close();
    dsBalance = null;
    log = null;
    rowInfo = null;
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private void jbInit() throws Exception {
    setDataSetProperty(dsBalance, "SELECT * FROM jsfs");

    dsBalance.setSort(new SortDescriptor("", new String[]{"dm"}, new boolean[]{false}, null, 0));
    dsBalance.setSequence(new SequenceDescriptor(new String[]{"jsfsid"}, new String[]{"s_jsfs"}));
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
      if(dsBalance.changesPending())
        dsBalance.reset();
      sRe = engine.action.BaseAction.showMessage(ex.getMessage(), true);
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
      //
      case OPERATE_ADD:
      case OPERATE_EDIT:
        isAdd = operate == OPERATE_ADD;
        if(!isAdd)
        {
          dsBalance.goToRow(Integer.parseInt(request.getParameter("rownum")));
          editrow = dsBalance.getInternalRow();
        }
        initRowInfo(isAdd, true);
        break;
      //
      case OPERATE_POST:
        //校验数据
        rowInfo.put(request);
        String dm = rowInfo.get("dm");
        String jsfs = rowInfo.get("jsfs");
        String kmdm = rowInfo.get("kmdm");
        if(dm.equals(""))
          return engine.action.BaseAction.showJavaScript("alert('编码不能为空！');");
        if(jsfs.equals(""))
          return engine.action.BaseAction.showJavaScript("alert('结算方式不能为空！');");
        //if(countrycode.length())//长度
        if(isAdd || !dm.equals(dsBalance.getValue("dm")))
        {
          String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('jsfs','dm','"+dm+"') from dual");
          if(!count.equals("0"))
          {
            if(isAdd)
              initRowInfo(true, false);
            return engine.action.BaseAction.showJavaScript("alert('编码("+ dm +")已经存在!');");
          }
        }
        if(isAdd || !jsfs.equals(dsBalance.getValue("jsfs")))
        {
          String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('jsfs','jsfs','"+jsfs+"') from dual");
          if(!count.equals("0"))
            return engine.action.BaseAction.showJavaScript("alert('结算方式("+ jsfs +")已经存在!');");
        }

        if(isAdd)
        {
          dsBalance.insertRow(false);
          dsBalance.setValue("jsfsid", "-1");
        }
        else
          dsBalance.goToInternalRow(editrow);
        dsBalance.setValue("dm", dm);
        dsBalance.setValue("jsfs", jsfs);
        dsBalance.setValue("kmdm", kmdm);
        dsBalance.post();
        dsBalance.saveChanges();
        //更新静态变量数量
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_BALANCE_MODE);

        sRe = engine.action.BaseAction.showJavaScript("parent.hideInterFrame();");
        break;
      //
      case OPERATE_DEL:
        dsBalance.goToRow(Integer.parseInt(request.getParameter("rownum")));
        dsBalance.deleteRow();
        dsBalance.saveChanges();
        //更新静态变量数量
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_BALANCE_MODE);

        break;
    }
    return sRe;
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   */
  private void initRowInfo(boolean isAdd, boolean isInit) throws Exception
  {
    if(!dsBalance.isOpen())
      dsBalance.open();
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();

    if(isAdd){
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('jsfs','dm','','',6) from dual");
      rowInfo.put("dm", code);
    }
    else
      rowInfo.put(dsBalance);
  }
  /**
   * 得到结算方式的名称
   * @param balanceId 初始化选中的结算方式ID
   * @return 结算方式名称
   */
  public final String getBalanceName(String balanceId)
  {
    if(balanceId == null || balanceId.equals(""))
      return "";
    if(!dsBalance.isOpen())
      dsBalance.open();
    if(locateRow == null)
      locateRow = new EngineRow(dsBalance, "jsfsid");
    locateRow.setValue(0, balanceId);
    if(dsBalance.locate(locateRow, Locate.FIRST))
      return dsBalance.getValue("jsfs");
    else
      return "";
  }
  /**
   * 得到结算方式列表的页面的<select>控件的<option></option>的字符串
   * @param balanceId 初始化选中的结算方式ID
   * @return 结算方式列表
   */
  public final String getBalanceForOption(String balanceId)
  {
    if(!dsBalance.isOpen())
      dsBalance.open();
    return dataSetToOption(dsBalance, "jsfsid", "jsfs", balanceId, null, null);
  }
}
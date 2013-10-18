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
 * <p>Title: 基础维护－地区维护</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public class B_Area extends CommonClass //implements LookUp
{
  //与地区操作相关变量
  public static final int AREA_ADD    = 20;//添加
  public static final int AREA_EDIT   = 21;//浏览或编辑
  public static final int AREA_DEL    = 22;//删除
  public static final int AREA_POST   = 23;//提交

  public boolean isAdd = true;//时候在添加状态

  public EngineDataSet dsArea  = new EngineDataSet();

  public  RowMap rowInfo =  new RowMap(); //添加行或修改行的引用

  private EngineRow locateRow =null;

  public  String retuUrl = null;

  /**
   * 得到地区信息的实例
   * @param request jsp请求
   * @return 返回地区信息的实例
   */
  public static B_Area getInstance(HttpServletRequest request)
  {
    B_Area areaBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      areaBean = (B_Area)session.getAttribute("areaBean_aa");
      if(areaBean == null)
      {
        areaBean = new B_Area();
        session.setAttribute("areaBean_aa", areaBean);
      }
    }
    return areaBean;
  }

  private B_Area()
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
    if(dsArea != null)
    {
      dsArea.close();
      dsArea = null;
    }
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private void jbInit() throws Exception {
    setDataSetProperty(dsArea, "SELECT * FROM dwdq ORDER BY areacode");

    //dsArea.setSort(new SortDescriptor("", new String[]{"areacode"}, new boolean[]{false}, null, 0));
    dsArea.setSequence(new SequenceDescriptor(new String[]{"dqh"}, new String[]{"s_dwdq"}));
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
      if(dsArea.changesPending())
        dsArea.reset();
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
      case Operate.INIT:
        retuUrl = request.getParameter("src");
        retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
        break;
      //
      case AREA_ADD:
        isAdd = true;
        initRowInfo(true, true);
        break;
      //
      case AREA_EDIT:
        dsArea.goToRow(Integer.parseInt(request.getParameter("rownum")));
        isAdd = false;
        initRowInfo(false, true);
        break;
      //
      case AREA_POST:
        rowInfo.put(request);
        String areacode = rowInfo.get("areacode");
        String dqmc = rowInfo.get("dqmc");
        String sfws = rowInfo.get("sfws");
        if(sfws.length()== 0)
          sfws = "0";
        if(areacode.equals(""))
          return engine.action.BaseAction.showJavaScript("alert('地区编码不能为空！');");
        if(dqmc.equals(""))
          return engine.action.BaseAction.showJavaScript("alert('地区名称不能为空！');");
        //if(countrycode.length())//长度
        if(isAdd || !areacode.equals(dsArea.getValue("areacode")))
        {
          String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('dwdq','areacode','"+areacode+"') from dual");
          if(!count.equals("0"))
          {
            if(isAdd)
              initRowInfo(true, false);
            return engine.action.BaseAction.showJavaScript("alert('编码("+ areacode +")已经存在!');");
          }
        }
        if(isAdd || !dqmc.equals(dsArea.getValue("dqmc")))
        {
          String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM dwdq WHERE dqmc='"+dqmc+"'");
          if(!count.equals("0"))
            return engine.action.BaseAction.showJavaScript("alert('名称("+ dqmc +")已经存在!');");
        }
        if(isAdd)
        {
          dsArea.insertRow(false);
          //dsArea.setValue("dqh", "-1");
        }
        dsArea.setValue("areacode", areacode);
        dsArea.setValue("dqmc", dqmc);
        dsArea.setValue("sfws", sfws);
        dsArea.post();
        dsArea.saveChanges();
        //刷新数据集，保持数据的同步
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_AREA);
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_AREA_CODE);
        sRe = engine.action.BaseAction.showJavaScript("parent.hideInterFrame();");
        break;
      //
      case AREA_DEL:
        dsArea.goToRow(Integer.parseInt(request.getParameter("rownum")));
        dsArea.deleteRow();
        dsArea.saveChanges();
        //刷新数据集，保持数据的同步
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_AREA);
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_AREA_CODE);
        break;
    }
    return sRe;
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   */
  private void initRowInfo(boolean isAdd, boolean isInit) throws Exception
  {
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();

    if(isAdd){
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('dwdq','areacode','','',6) from dual");
      rowInfo.put("areacode", code);
    }
    else
      rowInfo.put(dsArea);
  }

  //---Implement this engine.project.LookUp method
  /**
   * 得到地区列表的页面的<select>控件的<option></option>的字符串
   * @param areaId 初始化选中的地区ID
   * @return 地区列表

  public final String getList(String areaId)
  {
    if(!dsArea.isOpen())
      dsArea.open();
    return dataSetToOption(dsArea, "dqh", "dqmc", areaId, null, null);
  }
  /**
   * 根据地区ID得到地区名称
   * @param areaId 初始化选中的地区ID

  public final String getLookupName(String areaId)
  {
    if(areaId == null || areaId.equals(""))
      return "";
    if(!dsArea.isOpen())
      dsArea.open();
    if(locateRow == null)
      locateRow = new EngineRow(dsArea, "dqh");
    locateRow.setValue(0, areaId);
    if(dsArea.locate(locateRow, Locate.FIRST))
      return dsArea.getValue("dqmc");
    else
      return "";
  }
  public void regData(String[] ids) throws Exception
  {
    return;
  }
  public RowMap getLookupRow(String id) throws Exception
  {
    throw new java.lang.UnsupportedOperationException("Method getLookupRow() not yet implemented.");
  }
  public String getList(String selectid, RowMap paramlist) throws Exception
  {
    throw new java.lang.UnsupportedOperationException("Method getList() not yet implemented.");
  }
  */
}
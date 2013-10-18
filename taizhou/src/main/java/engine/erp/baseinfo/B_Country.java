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
 * <p>Title: 基础维护－国家维护</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public class B_Country extends CommonClass //implements LookUp
{
  //与国家操作相关变量
  public static final int COUNTRY_ADD    = 11;//添加
  public static final int COUNTRY_RE_ADD = 12;//再添
  public static final int COUNTRY_EDIT   = 13;//浏览或编辑
  public static final int COUNTRY_DEL    = 14;//删除
  public static final int COUNTRY_POST   = 15;//提交

  public boolean isAdd = true;//时候在添加状态

  private EngineRow locateRow = null;
  public EngineDataSet dsCountry = new EngineDataSet();

  public  RowMap rowCountry = new RowMap();  //添加行或修改行的引用
  /**
   * 国家列表的实例
   * @param request jsp请求
   * @return 国家列表的实例
   */
  public static B_Country getInstance(HttpServletRequest request)
  {
    B_Country countryBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      countryBean = (B_Country)session.getAttribute("countryBean_aa");
      if(countryBean == null)
      {
        countryBean = new B_Country();
        session.setAttribute("countryBean_aa", countryBean);
      }
    }
    return countryBean;
  }
  /**
   * 构造函数
   */
  private B_Country()
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
    dsCountry.close();
    dsCountry = null;
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private void jbInit() throws Exception {
    setDataSetProperty(dsCountry, "SELECT * FROM country");

    dsCountry.setSort(new SortDescriptor("", new String[]{"countrycode"}, new boolean[]{false}, null, 0));
    dsCountry.setSequence(new SequenceDescriptor(new String[]{"cdm"}, new String[]{"s_country"}));
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
      if(dsCountry.changesPending())
        dsCountry.reset();
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
      case COUNTRY_ADD:
        isAdd = true;
        initRowInfo(true, true);
        break;
      //
      case COUNTRY_EDIT:
        dsCountry.goToRow(Integer.parseInt(request.getParameter("rownum")));
        isAdd = false;
        initRowInfo(false, true);
        break;
      //
      case COUNTRY_POST:
      case COUNTRY_RE_ADD:
        rowCountry.put(request);
        String countrycode = rowCountry.get("countrycode");
        String mc = rowCountry.get("mc");
        if(countrycode.equals(""))
          return engine.action.BaseAction.showJavaScript("alert('国家编码不能为空！');");
        if(mc.equals(""))
          return engine.action.BaseAction.showJavaScript("alert('国家名称不能为空！');");

        if(isAdd || !countrycode.equals(dsCountry.getValue("countrycode")))
        {
          String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('country','countrycode','"+countrycode+"') from dual");
          if(!count.equals("0"))
          {
            if(isAdd)
              initRowInfo(true, false);
            return engine.action.BaseAction.showJavaScript("alert('编码("+ countrycode +")已经存在!');");
          }
        }
        if(isAdd || !mc.equals(dsCountry.getValue("mc")))
        {
          String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM country WHERE mc='"+mc+"'");
          if(!count.equals("0"))
            return engine.action.BaseAction.showJavaScript("alert('名称("+ mc +")已经存在!');");
        }
        if(isAdd)
        {
          dsCountry.insertRow(false);
          //dsCountry.setValue("cdm", "-1");
        }
        dsCountry.setValue("countrycode", countrycode);
        dsCountry.setValue("mc", mc);
        dsCountry.post();
        dsCountry.saveChanges();
        //刷新数据集，保持数据的同步
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_COUNTRY);

        if(operate == COUNTRY_POST)
          sRe = engine.action.BaseAction.showJavaScript("parent.hideInterFrame();");
        else
          initRowInfo(true, true);//再添加
        break;
      //
      case COUNTRY_DEL:
        dsCountry.goToRow(Integer.parseInt(request.getParameter("rownum")));
        dsCountry.deleteRow();
        dsCountry.saveChanges();
        //刷新数据集，保持数据的同步
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_COUNTRY);
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
    if(!dsCountry.isOpen())
      dsCountry.open();
    //是否时添加操作
    if(isInit && rowCountry.size() > 0)
      rowCountry.clear();

    if(isAdd){
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('country','countrycode','','',6) from dual");
      rowCountry.put("countrycode", code);
    }
    else
      rowCountry.put(dsCountry);
  }

  //----Implement this engine.project.LookUp method
  /**
   * 得到国家列表的页面的<select>控件的<option></option>的字符串
   * @param countryId 初始化选中的国家ID
   * @return 国家列表

  public final String getList(String countryId)
  {
    if(!dsCountry.isOpen())
      dsCountry.open();
    return dataSetToOption(dsCountry, "cdm", "mc", countryId, null, null);
  }
  /**
   * 根据国家ID得到国家名称
   * @param countryId 初始化选中的国家ID
   * @return 国家名称

  public final String getLookupName(String countryId)
  {
    if(countryId == null || countryId.equals(""))
      return "";
    if(!dsCountry.isOpen())
      dsCountry.open();
    EngineDataView ds = dsCountry.cloneEngineDataView();
    if(locateRow == null)
      locateRow = new EngineRow(ds, "cdm");
    locateRow.setValue(0, countryId);
    String mc = "";
    if(ds.locate(locateRow, Locate.FIRST))
      mc = ds.getValue("mc");
    ds.close();
    return mc;
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
  }*/
}
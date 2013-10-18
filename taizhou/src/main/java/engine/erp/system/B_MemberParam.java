package engine.erp.system;

import engine.project.*;
import engine.dataset.*;
import engine.common.LoginBean;
import engine.util.StringUtils;

import javax.servlet.http.*;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 基础信息维护--系统参数列表</p>
 * <p>Description: 基础信息维护--系统参数列表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public final class B_MemberParam extends SingleOperate
{
  private static final String DETAIL_SQL = "SELECT * FROM systemParam WHERE paramid=";//
  private EngineRow locateRow = null;
  public  String retuUrl = null;


  /**
   * 构造函数
   */
  public B_MemberParam()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsOneTable, "SELECT * FROM systemParam where isShow = 1");
    dsOneTable.setSort(new SortDescriptor("", new String[]{"code"}, new boolean[]{false}, null, 0));
    dsOneTable.setSequence(new SequenceDescriptor(new String[]{"paramid"}, new String[]{"s_systemParam"}));
  }

  /**
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
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
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
    String code = rowInfo.get("code");
    String name = rowInfo.get("name");
    String value = rowInfo.get("value");
    String deptid = rowInfo.get("deptid");

    String bz = rowInfo.get("bz");

    if(code.equals(""))
      return LoginBean.showJavaScript("alert('参数编号不能为空！');");
    if(value.equals(""))
      return LoginBean.showJavaScript("alert('参数值不能为空！');");



    if(!isAdd)
      ds.goToInternalRow(editrow);

    if(isAdd || !code.equals(ds.getValue("code")))
    {
      String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('systemParam','code','"+code+"') from dual");
      if(!count.equals("0"))
      {
        return LoginBean.showJavaScript("alert('参数编号("+ code +")已经存在!');");
      }
    }

    if(isAdd)
    {
      ds.insertRow(false);
      ds.setValue("paramid ", "-1");
    }
    ds.setValue("code",code);
    ds.setValue("name",name);
    ds.setValue("value",value);
    ds.setValue("deptid",deptid);
   // ds.setValue("isShow", isShow);
    ds.setValue("bz", rowInfo.get("bz"));
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
  public final RowMap getRowinfo()
      {
      return rowInfo;
     }

  /**
   * 得到系统参数列表的页面的<select>控件的<option></option>的字符串
   * @param mixId 初始化选中的系统参数ID
   * @param lx 系统参数类型
   * @return 系统参数列表
   */
  public synchronized final String getSystemParamForOption(String paramid)
  {
    return dataSetToOption(getOneTable(), "paramid", "code", paramid, null, null);
  }
  /**
   * 根据系统参数ID得到系统参数一行信息
   * @param bankId 初始化选中的系统参数ID
   * @return 系统参数名称
   */
  public synchronized final String getSystemParamName(String paramid)
  {
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "paramid");
    locateRow.setValue(0, paramid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      return getOneTable().getValue("code");
    else
      return "";
  }
}

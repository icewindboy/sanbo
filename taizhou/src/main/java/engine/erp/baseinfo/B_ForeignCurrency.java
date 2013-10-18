package engine.erp.baseinfo;

import engine.project.*;
import engine.dataset.*;
import engine.common.LoginBean;

import javax.servlet.http.*;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 基础信息维护--外币维护</p>
 * <p>Description: 基础信息维护--外币维护</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public final class B_ForeignCurrency extends SingleOperate //implements LookUp
{
  private EngineRow locateRow = null;
  /**
   * 外币的实例
   * @param request jsp请求
   * @return 外币的实例
   */
  public static B_ForeignCurrency getInstance(HttpServletRequest request)
  {
    B_ForeignCurrency foreignCurrencyBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      foreignCurrencyBean = (B_ForeignCurrency)session.getAttribute("foreignCurrencyBean_aa");
      if(foreignCurrencyBean == null)
      {
        foreignCurrencyBean = new B_ForeignCurrency();
        session.setAttribute("foreignCurrencyBean_aa", foreignCurrencyBean);
      }
    }
    return foreignCurrencyBean;
  }
  /**
   * 构造函数
   */
  private B_ForeignCurrency()
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
    setDataSetProperty(dsOneTable, "SELECT * FROM wb");

    dsOneTable.setSort(new SortDescriptor("", new String[]{"dm"}, new boolean[]{false}, null, 0));
    dsOneTable.setSequence(new SequenceDescriptor(new String[]{"wbid"}, new String[]{"s_wb"}));
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
    String dm = rowInfo.get("dm");
    String mc = rowInfo.get("mc");
    String hl = rowInfo.get("hl");
    String ff = rowInfo.get("ff");
    String gd = rowInfo.get("gd");
    String fh = rowInfo.get("fh");
    if(dm.equals(""))
      return engine.action.BaseAction.showJavaScript("alert('编码不能为空！');");
    if(mc.equals(""))
      return engine.action.BaseAction.showJavaScript("alert('外币名称不能为空！');");
    if(hl.equals(""))
      return engine.action.BaseAction.showJavaScript("alert('汇率不能为空！');");
    try{
      Double.parseDouble(hl);
    }
    catch(Exception ex){
      return engine.action.BaseAction.showJavaScript("alert('非法汇率！');");
    }
    if(ff.equals(""))
      return engine.action.BaseAction.showJavaScript("alert('请选择固定汇率报价方式！');");
    if(gd.equals(""))
      return engine.action.BaseAction.showJavaScript("alert('请选择是否固定汇率！');");

    if(isAdd || !dm.equals(ds.getValue("dm")))
    {
      String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('wb','dm','"+dm+"') from dual");
      if(!count.equals("0"))
      {
        if(isAdd)
          initRowInfo(true, false);
        return engine.action.BaseAction.showJavaScript("alert('编码("+ dm +")已经存在!');");
      }
    }

    if(isAdd)
    {
      ds.insertRow(false);
      ds.setValue("wbid", "-1");
    }
    else
      ds.goToInternalRow(editrow);
    ds.setValue("dm", dm);
    ds.setValue("mc", mc);
    ds.setValue("hl", hl);
    ds.setValue("ff", ff);
    ds.setValue("gd", gd);
    ds.setValue("fh", fh);
    ds.post();
    ds.saveChanges();
    //刷新数据集，保持数据的同步
    LookupBeanFacade.refreshLookup(SysConstant.BEAN_FOREIGN_CURRENCY);
    return engine.action.BaseAction.showJavaScript("parent.hideInterFrame();");
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
    //刷新数据集，保持数据的同步
    LookupBeanFacade.refreshLookup(SysConstant.BEAN_FOREIGN_CURRENCY);

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
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('wb','dm','','',6) from dual");
      rowInfo.put("dm", code);
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

  //---Implement this engine.project.LookUp method
  /*得到一列的信息，Implement this engine.project.OperateCommon abstract method*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 得到银行列表的页面的<select>控件的<option></option>的字符串
   * @param bankId 初始化选中的银行ID
   * @return 银行列表

  public synchronized final String getList(String foreignId)
  {
    return dataSetToOption(getOneTable(), "wbid", "mc", foreignId, null, null);
  }
  /**
   * 根据银行ID得到银行名称
   * @param bankId 初始化选中的银行ID
   * @return 银行名称

  public synchronized final String getLookupName(String foreignId)
  {
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "wbid");
    locateRow.setValue(0, foreignId);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      return getOneTable().getValue("mc");
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

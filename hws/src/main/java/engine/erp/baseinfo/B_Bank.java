package engine.erp.baseinfo;

import engine.project.*;
import engine.dataset.*;
import engine.common.LoginBean;

import javax.servlet.http.*;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 基础信息维护--银行列表</p>
 * <p>Description: 基础信息维护--银行列表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */
public final class B_Bank extends SingleOperate //implements LookUp
{
  private EngineRow locateRow = null;

  /**
   * 结算方式的实例
   * @param request jsp请求
   * @return 返回结算方式的实例
   */
  public static B_Bank getInstance(HttpServletRequest request)
  {
    B_Bank bankBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      bankBean = (B_Bank)session.getAttribute("bankBean_aa");
      if(bankBean == null)
      {
        bankBean = new B_Bank();
        session.setAttribute("bankBean_aa", bankBean);
      }
    }
    return bankBean;
  }
  /**
   * 构造函数
   */
  private B_Bank()
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
    setDataSetProperty(dsOneTable, "SELECT * FROM yh");

    dsOneTable.setSort(new SortDescriptor("", new String[]{"dm"}, new boolean[]{false}, null, 0));
    dsOneTable.setSequence(new SequenceDescriptor(new String[]{"yhid"}, new String[]{"s_yh"}));
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
    String yhmc = rowInfo.get("yhmc");
    if(dm.equals(""))
      return engine.action.BaseAction.showJavaScript("alert('编码不能为空！');");
    if(yhmc.equals(""))
      return engine.action.BaseAction.showJavaScript("alert('银行名称不能为空！');");
    //if(countrycode.length())//长度
    if(isAdd || !dm.equals(ds.getValue("dm")))
    {
      String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('yh','dm','"+dm+"') from dual");
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
      ds.setValue("yhid", "-1");
    }
    else
      ds.goToInternalRow(editrow);
    ds.setValue("dm", dm);
    ds.setValue("yhmc", yhmc);
    ds.setValue("addr", rowInfo.get("addr"));
    ds.setValue("tel", rowInfo.get("tel"));
    ds.setValue("lxr", rowInfo.get("lxr"));
    ds.post();
    ds.saveChanges();
    //刷新数据集，保持数据的同步
    LookupBeanFacade.refreshLookup(SysConstant.BEAN_BANK);
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
    LookupBeanFacade.refreshLookup(SysConstant.BEAN_BANK);
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
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('yh','dm','','',6) from dual");
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

  /*得到一列的信息，Implement this engine.project.OperateCommon abstract method*/
  public final RowMap getRowinfo() {    return rowInfo;  }

  //---Implement this engine.project.LookUp method
  /**
   * 得到银行列表的页面的<select>控件的<option></option>的字符串
   * @param bankId 初始化选中的银行ID
   * @return 银行列表

  public final String getList(String bankId)
  {
    return dataSetToOption(getOneTable(), "yhid", "yhmc", bankId, null, null);
  }
  /**
   * 根据银行ID得到银行名称
   * @param bankId 初始化选中的银行ID
   * @return 银行名称

  public final String getLookupName(String bankId)
  {
    if(bankId == null || bankId.equals(""))
      return "";
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "yhid");
    locateRow.setValue(0, bankId);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      return getOneTable().getValue("yhmc");
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
  }*/
}
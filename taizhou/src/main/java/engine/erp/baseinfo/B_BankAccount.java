package engine.erp.baseinfo;

import engine.project.*;
import engine.dataset.*;
import engine.common.LoginBean;

import javax.servlet.http.*;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 基础信息维护--银行帐号列表</p>
 * <p>Description: 基础信息维护--银行帐号列表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public final class B_BankAccount extends SingleOperate //implements LookUp
{
  private static final String BANKACCOUNT_SQL = "SELECT * FROM yhzh WHERE fgsid=";

  private EngineRow locateRow = null;
  private String fgsid = null;//分公司ID
  private B_Bank bankBean = null;
  /**
   * 得到银行帐号列表的实例
   * @param request jsp请求
   * @return 返回银行帐号列表的实例
   */
  public static B_BankAccount getInstance(HttpServletRequest request)
  {
    B_BankAccount bankAccountBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      bankAccountBean = (B_BankAccount)session.getAttribute("bankAccountBean_aa");
      if(bankAccountBean == null)
      {
        String fgsid = LoginBean.getInstance(request).getFirstDeptID();
        bankAccountBean = new B_BankAccount(fgsid);
        bankAccountBean.bankBean = B_Bank.getInstance(request);
        session.setAttribute("bankAccountBean_aa", bankAccountBean);
      }
    }
    return bankAccountBean;
  }
  /**
   * 构造函数
   */
  public B_BankAccount(String fgsid)
  {
    this.fgsid = fgsid;
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
    setDataSetProperty(dsOneTable, BANKACCOUNT_SQL + fgsid);

    dsOneTable.setSort(new SortDescriptor("", new String[]{"yhid","zh"}, new boolean[]{false,false}, null, 0));
    dsOneTable.setSequence(new SequenceDescriptor(new String[]{"yhzhid"}, new String[]{"s_yhzh"}));
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
    String zh = rowInfo.get("zh");
    String yhid = rowInfo.get("yhid");
    String zhlx = rowInfo.get("zhlx");
    if(zh.equals(""))
      return engine.action.BaseAction.showJavaScript("alert('帐号不能为空！');");
    if(yhid.equals(""))
      return engine.action.BaseAction.showJavaScript("alert('帐号不能没有所属银行！');");
    if(zhlx.equals(""))
      return engine.action.BaseAction.showJavaScript("alert('请选择帐号类型！');");

    if(isAdd || !zh.equals(ds.getValue("zh")))
    {
      String count = "SELECT pck_base.fieldCodeCount('yhzh','zh','"+zh+"','yhid="+yhid+"') from dual";
      count = dataSetProvider.getSequence(count);
      if(!count.equals("0"))
        return engine.action.BaseAction.showJavaScript("alert('该银行编码("+ zh +")已经存在!');");
    }

    if(isAdd)
    {
      ds.insertRow(false);
      ds.setValue("yhzhid", "-1");
      ds.setValue("fgsid", fgsid);
    }
    else
      ds.goToInternalRow(editrow);
    ds.setValue("zh", zh);
    ds.setValue("yhid", yhid);
    ds.setValue("zhmc", rowInfo.get("zhmc"));
    ds.setValue("zhlx", rowInfo.get("zhlx"));
    ds.post();
    ds.saveChanges();
    //刷新数据集，保持数据的同步
    LookupBeanFacade.refreshLookup(SysConstant.BEAN_BANK_ACCOUNT);

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
    LookupBeanFacade.refreshLookup(SysConstant.BEAN_BANK_ACCOUNT);
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
    /*
    if(isAdd){
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('yh','dm','','',6) from dual");
      rowInfo.put("dm", code);
    }
    else*/
    if(!isAdd)
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

  public synchronized final String getList(String accoutId)
  {
    /*String idColumn = "yhzhid";
    String bankName = null;
    EngineDataSet ds = getOneTable();
    StringBuffer buf = new StringBuffer();
    ds.first();
    for(int i=0; i<ds.getRowCount(); i++)
    {
      String id = ds.getValue(idColumn);
      buf.append("<option value='");
      buf.append(id);
      if(id.equals(accoutId))
        buf.append("' selected>");
      else
        buf.append("'>");

      bankName = bankBean.getBankName(id);
      buf.append(bankName + " "+ ds.getValue("zhmc"));
      buf.append("</option>");
      ds.next();
    }
    return buf.toString();
    return dataSetToOption(getOneTable(), "yhzhid", "zhmc", accoutId, null, null);
  }
  /**
   * 根据银行ID得到银行名称
   * @param bankId 初始化选中的银行ID
   * @return 银行名称

  public synchronized final String getLookupName(String accoutId)
  {
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "yhzhid");
    locateRow.setValue(0, accoutId);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      return getOneTable().getValue("zhmc");
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

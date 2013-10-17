package engine.erp.baseinfo;

import engine.project.*;
import engine.dataset.*;
import engine.common.LoginBean;
import engine.util.StringUtils;
import javax.servlet.http.*;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 基础信息维护--发票类别 </p>
 * <p>Description: 基础信息维护--发票类别 </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */
public final class B_Fplb extends SingleOperate //implements LookUp
{
  private static final String DETAIL_SQL = "SELECT * FROM jc_fplb WHERE fplbId=";//
  private EngineRow locateRow = null;
  public  String retuUrl = null;

  /**
   * 得到发票类别的实例
   * @param request jsp请求
   * @return 返回发票类别的实例
   */
  public static B_Fplb getInstance(HttpServletRequest request)
  {
    B_Fplb fplbBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      fplbBean = (B_Fplb)session.getAttribute("fplbBean");
      if(fplbBean == null)
      {
        fplbBean = new B_Fplb();
        session.setAttribute("fplbBean", fplbBean);
      }
    }
    return fplbBean;
  }

  /**
   * 构造函数
   */
  private B_Fplb()
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
    setDataSetProperty(dsOneTable, "SELECT * FROM jc_fplb");
    dsOneTable.setSort(new SortDescriptor("", new String[]{"pxh"}, new boolean[]{false}, null, 0));
    dsOneTable.setSequence(new SequenceDescriptor(new String[]{"fplbId"}, new String[]{"s_jc_fplb"}));
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
    String mc = rowInfo.get("mc");
    String sl = rowInfo.get("sl");
    String pxh = rowInfo.get("pxh");
    String sylx = rowInfo.get("sylx");
    String temp = null;

    if(mc.equals(""))
      return showJavaScript("alert('发票名称不能为空！');");
    if(sylx.length() == 0)
      return showJavaScript("alert('请选择使用类型！');");
    if(sl.equals(""))
      sl = "0";
    else{
      temp = checkNumber(sl,"税率"); //税率只能为数字类型
      if(temp!= null)
        return showJavaScript("alert('税率只能为数字！');");
    }
    if(pxh.equals(""))
      return showJavaScript("alert('排序号不能为空！');");
    if(!isAdd)
      ds.goToInternalRow(editrow);
    if(isAdd)
    {
      ds.insertRow(false);
      ds.setValue("fplbId", "-1");
    }
    ds.setValue("mc", mc);
    ds.setValue("sl", sl);
    ds.setValue("pxh", pxh);
    ds.setValue("sylx", sylx);
    ds.post();
    ds.saveChanges();
    //刷新数据集，保持数据的同步
    if(sylx.equals("1"))
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_BUY_INVOICE_TYPE);
    else
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_SALE_INVOICE_TYPE);

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
    String sylx = ds.getValue("sylx");
    ds.deleteRow();
    ds.saveChanges();
    //刷新数据集，保持数据的同步
    if(sylx.equals("1"))
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_BUY_INVOICE_TYPE);
    else
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_SALE_INVOICE_TYPE);
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
  public final RowMap getRowinfo() {    return rowInfo;  }
}

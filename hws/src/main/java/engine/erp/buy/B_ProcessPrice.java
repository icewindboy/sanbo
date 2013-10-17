package engine.erp.buy;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 外加工信息维护</p>
 * <p>Description: 外加工信息维护</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class  B_ProcessPrice  extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1909";
  /**
   * 提取外加工信息维护的SQL语句
   */
  private static final String  B_PROCESSPRISE_SQL =
      "SELECT * FROM sc_wjgjg, kc_dm WHERE sc_wjgjg.cpid = kc_dm.cpid ? ORDER BY kc_dm.cpbm ";
  private static final String  B_PROCESSPRISE_STRUCT_SQL =
      "SELECT * FROM sc_wjgjg WHERE 1<>1 ";
  /**
   * 保存外加工信息维护的数据集
   */
  private EngineDataSet dsProcessPrice = new EngineDataSet();
  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;

  /**
   * 保存用户输入的信息
   */
  private RowMap rowInfo = new RowMap();

  /**
   * 是否在添加状态
   */
  public  boolean isAdd = true;

  /**
   * 保存修改操作的行记录指针
   */
  private long    editrow = 0;

  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;
    /**
  * 定义固定查询类
   */
  private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false; //是否已经初始化查询条件
  /**
   * 得到外加工信息维护的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回外加工信息维护的实例
   */
  public static  B_ProcessPrice  getInstance(HttpServletRequest request)
  {
     B_ProcessPrice b_ProcessPriceBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_ProcessPriceBean";
      b_ProcessPriceBean = (B_ProcessPrice)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_ProcessPriceBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_ProcessPriceBean = new  B_ProcessPrice();
        //设置格式化的字段
        b_ProcessPriceBean.dsProcessPrice.setColumnFormat("dj", loginBean.getPriceFormat());
        session.setAttribute(beanName, b_ProcessPriceBean);
      }
    }
    return b_ProcessPriceBean;
  }
  /**
   * 构造函数
   */
  private  B_ProcessPrice()
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
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsProcessPrice, null);
    dsProcessPrice.setSequence(new SequenceDescriptor(new String[]{"wjgjgid"}, new String[]{"s_sc_wjgjg"}));
    dsProcessPrice.setTableName("sc_wjgjg");
    //添加操作的触发对象
     B_ProcessPrice_Add_Edit add_edit = new  B_ProcessPrice_Add_Edit();
    addObactioner(String.valueOf(INIT), new  B_ProcessPrice_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new  B_ProcessPrice_Post());
    addObactioner(String.valueOf(DEL), new  B_ProcessPrice_Delete());
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
  }

  //----Implementation of the BaseAction abstract class
  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String operate = request.getParameter(OPERATE_KEY);
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsProcessPrice.isOpen() && dsProcessPrice.changesPending())
        dsProcessPrice.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsProcessPrice != null){
      dsProcessPrice.close();
      dsProcessPrice = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
  }

  //----Implementation of the BaseAction abstract class
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }
  /**
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd)
      rowInfo.put(getOneTable());
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsProcessPrice.isOpen())
      dsProcessPrice.open();
    return dsProcessPrice;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo()
  {
    return rowInfo;
  }

  /**
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }
  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class  B_ProcessPrice_Init implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //初始化时清空数据集
      //if(dsProcessPrice.isOpen() && dsProcessPrice.getRowCount() > 0)
        //dsProcessPrice.empty();
      String SQL = combineSQL(B_PROCESSPRISE_SQL, "?", new String[]{""});
      dsProcessPrice.setQueryString(SQL);
      dsProcessPrice.setRowMax(null);
      //data.setMessage(showJavaScript("showFixedQuery()"));//初始化弹出查询界面
    }
  }
  /**
   * 添加查询操作的触发类
   */
  class FIXED_SEARCH implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL( B_PROCESSPRISE_SQL, "?", new String[]{SQL});
      if(!dsProcessPrice.getQueryString().equals(SQL))
      {
        dsProcessPrice.setQueryString(SQL);
        dsProcessPrice.setRowMax(null);
      }
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      fixedQuery = new QueryFixedItem();
      EngineDataSet master = dsProcessPrice;
      if(!master.isOpen())
        master.open();

      //初始化固定的查询项目
      fixedQuery.addShowColumn("sc_wjgjg", new QueryColumn[]{
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("wjgjgid"), "VW_SC_WJGJGQUERY", "wjgjgid", "product","product","like"),
        new QueryColumn(master.getColumn("wjgjgid"), "VW_SC_WJGJGQUERY", "wjgjgid", "cpbm","cpbm","like"),
        new QueryColumn(master.getColumn("dfcph"), null, null,null),
      });
      isInitQuery = true;
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class  B_ProcessPrice_Add_Edit implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsProcessPrice.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsProcessPrice.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class  B_ProcessPrice_Post implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发保存操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      rowInfo.put(data.getRequest());
      String dwtxid = rowInfo.get("dwtxid");
      String cpid  = rowInfo.get("cpid");
      String dj = rowInfo.get("dj");
      String yhtj  = rowInfo.get("yhtj");
      String dfcph  = rowInfo.get("dfcph");
      String bz  = rowInfo.get("bz");
      if(dwtxid.equals("")){
        data.setMessage(showJavaScript("alert('供应商ID不能为空！');"));
        return;
      }
      if(cpid.equals("")){
        data.setMessage(showJavaScript("alert('产品ID不能为空！');"));
        return;
      }
      if(dj.equals("")){
        data.setMessage(showJavaScript("alert('单价不能为空！');"));
        return;
      }

      if(!isAdd)
        ds.goToInternalRow(editrow);

      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("wjgjgid", "-1");
      }
      ds.setValue("dwtxid",dwtxid);
      ds.setValue("cpid",cpid);
      ds.setValue("dj",dj);
      ds.setValue("yhtj",yhtj);
      ds.setValue("dfcph",dfcph);
      ds.setValue("bz",bz);
      ds.post();
      ds.saveChanges();
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class  B_ProcessPrice_Delete implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发删除操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
    }
  }
}


package engine.erp.store;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.*;
import engine.project.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 库存管理--收发单据类别</p>
 * <p>Description: 库存管理--收发单据类别</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 张新文
 * @version 1.0
 */

public final class B_StoreBillKind extends BaseAction implements Operate
{
  /**
   * 提取收发单据列表所有信息的SQL语句
   */
  private static final String STOREBILLKIND_SQL = "SELECT * FROM KC_SFDJLB ORDER BY djxz, lbbm ";//
  private static final String SEARCH_SQL = "SELECT * FROM KC_SFDJLB ? ORDER BY djxz, lbbm ";//


  /**
   * 建立收发单据列表信息的数据集
   */
  private EngineDataSet dsWorkStore = new EngineDataSet();

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
  private long  editrow = 0;

  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();

  /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_StoreBillKind getInstance(HttpServletRequest request)
  {
    B_StoreBillKind b_StoreBillKindBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_StoreBillKindBean";
      b_StoreBillKindBean = (B_StoreBillKind)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_StoreBillKindBean == null)
      {
        b_StoreBillKindBean = new B_StoreBillKind();
        session.setAttribute(beanName, b_StoreBillKindBean);
      }
    }
    return b_StoreBillKindBean;
  }
  /**
   * 构造函数
   */
  private B_StoreBillKind()
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
    setDataSetProperty(dsWorkStore, STOREBILLKIND_SQL);
    dsWorkStore.setSort(new SortDescriptor("", new String[]{"djxz", "lbbm"}, new boolean[]{false, false}, null, 0));
    dsWorkStore.setSequence(new SequenceDescriptor(new String[]{"SFDJLBID"}, new String[]{"S_KC_SFDJLB"}));
    //添加操作的触发对象
    B_WorkProcedure_Add_Edit add_edit = new B_WorkProcedure_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_WorkProcedure_Post());
    addObactioner(String.valueOf(DEL), new B_WorkProcedure_Delete());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
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
      if(dsWorkStore.isOpen() && dsWorkStore.changesPending())
        dsWorkStore.reset();
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
    if(dsWorkStore != null){
      dsWorkStore.close();
      dsWorkStore = null;
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
   * 初始化列信息
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
    if(!dsWorkStore.isOpen())
      dsWorkStore.open();
    return dsWorkStore;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }
  /**
   * 初始化操作的触发类
   */
  class B_WorkProcedure_Init implements Obactioner
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
      dsWorkStore.setQueryString(STOREBILLKIND_SQL);
      dsWorkStore.setRowMax(null);
    }
  }

  /**
   * 添加或修改操作的触发类
   */
  class B_WorkProcedure_Add_Edit implements Obactioner
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
        dsWorkStore.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsWorkStore.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_WorkProcedure_Post implements Obactioner
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
      String djxz = rowInfo.get("djxz");
      String lbmc = rowInfo.get("lbmc");
      //2004-4-23 22:00 新增 类别编码 yjg
      String lbbm = rowInfo.get("lbbm");
      if(djxz.equals("")){
        data.setMessage(showJavaScript("alert('单据性质不能为空！');"));
        return;
      }
      if(!isAdd)
        ds.goToInternalRow(editrow);

      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("SFDJLBID", "-1");
      }
      ds.setValue("djxz", djxz);
      ds.setValue("lbmc", lbmc);
      ds.setValue("lbbm", lbbm);
      ds.post();
      ds.saveChanges();
      if(djxz.equals("1"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_SALE_IN);//合同入库单
      else if(djxz.equals("2"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_SALE_OUT);//合同出库单
      else if(djxz.equals("3"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_IN);//自制入库单
      else if(djxz.equals("4"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_OUT);//生产领料单
      else if(djxz.equals("5"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PROCESS_IN);//外加工入库单
      else if(djxz.equals("6"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PROCESS_OUT);//外加工发料单
      else if(djxz.equals("7"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_LOSS);//报损单
      else if(djxz.equals("8"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_MOVE);//移库单
      else if(djxz.equals("9"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_OTHER_IN);//其它入库单
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class B_WorkProcedure_Delete implements Obactioner
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
      String djxz = "";
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      djxz = ds.getValue("djxz");
      ds.deleteRow();
      ds.saveChanges();
      if(djxz.equals("1"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_SALE_IN);//合同入库单
      else if(djxz.equals("2"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_SALE_OUT);//合同出库单
      else if(djxz.equals("3"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_IN);//自制入库单
      else if(djxz.equals("4"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PRODUCE_OUT);//生产领料单
      else if(djxz.equals("5"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PROCESS_IN);//外加工入库单
      else if(djxz.equals("6"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_PROCESS_OUT);//外加工发料单
      else if(djxz.equals("7"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_LOSS);//报损单
      else if(djxz.equals("8"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_MOVE);//移库单
      else if(djxz.equals("9"))
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_OTHER_IN);//其它入库单
    }
  }
  /**
   *  查询操作
   */
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " where "+SQL;
      SQL = combineSQL(SEARCH_SQL, "?", new String[]{SQL});
      //03.16 15:25 修改 在Master_Search中去掉判断旧的SQL和新产生的SQL是否相同的if语句. yjg
      //if(!dsMasterTable.getQueryString().equals(SQL))
      //{
        dsWorkStore.setQueryString(SQL);
        dsWorkStore.setRowMax(null);
      //}
    }
    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet dsWorkStore = getOneTable();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(dsWorkStore.getColumn("djxz"), null, null, null),
        new QueryColumn(dsWorkStore.getColumn("lbmc"), null, null, null),
        new QueryColumn(dsWorkStore.getColumn("lbbm"), null, null, null)
      });
      isInitQuery = true;
    }

  }
}


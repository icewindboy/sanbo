package engine.erp.sale;

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
 * 销售管理--客户产品折扣,
 * 定义具体客户要购买的产品的单价和折扣
 * 客户产品历史记录
 * */
public final class B_CustomerProductDiscount extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  /**
   * 提取客户信誉额度信息的SQL语句
   */
  private static final String CUSTOMER_PRODUCT_DISCOUNT_SQL = "SELECT * FROM xs_khcpzk WHERE fgsid=? ? ORDER BY dwtxid";//
  /**
   * 保存客户产品折扣信息的数据集
   */
  private EngineDataSet dsxs_khcpzkTable = new EngineDataSet();
  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;
  /**
   * 保存用户输入的信息
   * 临时存放,便于修改
   */
  private RowMap rowInfo = new RowMap();
  /**
   * 是否在添加状态
   * 与修改状态区别开
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

  private boolean isInitQuery = false;
  public  String loginName = ""; //登录员工的姓名
  private String fgsID = null;   //分公司ID
  /**
  * 定义固定查询类
   */
  private QueryFixedItem fixedQuery = new QueryFixedItem();
  /**
   * 得到客户信誉额度信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回报价资料信息的实例
   */
  public static B_CustomerProductDiscount getInstance(HttpServletRequest request)
  {
    B_CustomerProductDiscount xs_khcpzkbean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "xs_khcpzkbean";
      xs_khcpzkbean = (B_CustomerProductDiscount)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(xs_khcpzkbean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsID = loginBean.getFirstDeptID();
        xs_khcpzkbean = new B_CustomerProductDiscount(fgsID);
        xs_khcpzkbean.loginName = loginBean.getUserName();
        //设置格式化的字段
        xs_khcpzkbean.dsxs_khcpzkTable.setColumnFormat("zk", loginBean.getPriceFormat());
        xs_khcpzkbean.dsxs_khcpzkTable.setColumnFormat("dj", loginBean.getPriceFormat());
        xs_khcpzkbean.dsxs_khcpzkTable.setColumnFormat("bj", loginBean.getPriceFormat());
        session.setAttribute(beanName, xs_khcpzkbean);
      }
    }
    return xs_khcpzkbean;
  }
  /**
   * 构造函数
   */
  private B_CustomerProductDiscount(String fgsid)
  {
    this.fgsID = fgsid;
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
    setDataSetProperty(dsxs_khcpzkTable, combineSQL(CUSTOMER_PRODUCT_DISCOUNT_SQL, "?", new String[]{fgsID,""}));
    dsxs_khcpzkTable.setSort(new SortDescriptor("", new String[]{"dwtxId","cpId"}, new boolean[]{false,false}, null, 0));
    //dsxs_khcpzkTable.setSequence(new SequenceDescriptor(new String[]{"cpId","dwtxId"}, new String[]{"s_xs_khcpzk","s_xs_khcpzk"}));
    //添加操作的触发对象
    B_CustomerProductDiscount_Add_Edit add_edit = new B_CustomerProductDiscount_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_CustomerProductDiscount_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_CustomerProductDiscount_Post());
    addObactioner(String.valueOf(DEL), new B_CustomerProductDiscount_Delete());
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
      if(dsxs_khcpzkTable.isOpen() && dsxs_khcpzkTable.changesPending())
        dsxs_khcpzkTable.reset();
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
    if(dsxs_khcpzkTable != null){
      dsxs_khcpzkTable.close();
      dsxs_khcpzkTable = null;
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
    if(!dsxs_khcpzkTable.isOpen())
      dsxs_khcpzkTable.open();
    return dsxs_khcpzkTable;
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

  //---------------------------------------------------------------------
  //以下是操作实现的类(共5个内部类)
  //---------------------------------------------------------------------

  /**
   * 初始化操作的触发类
   */
  class B_CustomerProductDiscount_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      fixedQuery.getSearchRow().clear();
      //data.setMessage(showJavaScript("showFixedQuery()"));
    }
  }
  /**
   * 添加查询操作的触发类
   */
  class FIXED_SEARCH implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      QueryBasic queryBasic = fixedQuery;
      queryBasic.setSearchValue(data.getRequest());
      String SQL = queryBasic.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(CUSTOMER_PRODUCT_DISCOUNT_SQL, "?", new String[]{fgsID, SQL});
      if(!dsxs_khcpzkTable.getQueryString().equals(SQL))
      {
        dsxs_khcpzkTable.setQueryString(SQL);
        dsxs_khcpzkTable.setRowMax(null);
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
      EngineDataSet master = dsxs_khcpzkTable;
      if(!master.isOpen())
       master.open();
      //初始化固定的查询项目
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("khcpdm"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("dj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("dj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("zk"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("zk"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("bj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("bj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("jjbl"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("jjbl"), null, null, null, "b", "<="),
      });
      isInitQuery = true;
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class B_CustomerProductDiscount_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsxs_khcpzkTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsxs_khcpzkTable.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_CustomerProductDiscount_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      rowInfo.put(data.getRequest());
      String cpId = rowInfo.get("cpId");
      String khcpdm  = rowInfo.get("khcpdm");
      String dj  = rowInfo.get("dj");
      String zk  = rowInfo.get("zk");
      String bj  = rowInfo.get("bj");
      String jjbl  = rowInfo.get("jjbl");
      String bz  = rowInfo.get("bz");
      String dwtxId  = rowInfo.get("dwtxId");
      String dmsxid  = rowInfo.get("dmsxid");
      if(!isAdd)
        ds.goToInternalRow(editrow);
      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("fgsID",fgsID);
       }
      ds.setValue("cpId", cpId);
      ds.setValue("dwtxId", dwtxId);
      ds.setValue("khcpdm",khcpdm);
      ds.setValue("dj",dj);
      ds.setValue("zk",zk);
      ds.setValue("bj",bj);
      ds.setValue("jjbl",jjbl);
      ds.setValue("dmsxid",dmsxid);
      ds.setValue("bz",bz);
      ds.post();
      ds.saveChanges();
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }
  /**
   * 删除操作的触发类
   */
  class B_CustomerProductDiscount_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
    }
  }
}


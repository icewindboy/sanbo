package engine.erp.quality;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.html.HtmlTableProducer;

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
 * <p>Title: 质量管理--检验器具信息</p>
 * <p>Description: 质量管理--检验项目管理<</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */

public final class B_ToolInfo extends BaseAction implements Operate
{
  /**
   * 提取收发单据列表所有信息的SQL语句
   */
  private static final String Quality_SQL = "SELECT * FROM zl_toolInfo";//
  /**
   * 建立收发单据列表信息的数据集
   */
  private EngineDataSet dsWorkCheck = new EngineDataSet();
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
  //private String zdrid=null, zdr=null;
  public  HtmlTableProducer table = new HtmlTableProducer(dsWorkCheck, "zl_toolInfo", "zl_toolInfo");//查询得到数据库中配置的字段

  /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_ToolInfo getInstance(HttpServletRequest request)
  {
    B_ToolInfo b_ToolInfoBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_ToolInfoBean";
      b_ToolInfoBean = (B_ToolInfo)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_ToolInfoBean == null)
      {
        b_ToolInfoBean = new B_ToolInfo();
        LoginBean loginBean = LoginBean.getInstance(request);
        //b_ToolInfoBean.zdrid=loginBean.getUserID();
        //b_ToolInfoBean.zdr=loginBean.getUserName();
        session.setAttribute(beanName, b_ToolInfoBean);
      }
    }
    return b_ToolInfoBean;
  }
  /**
   * 构造函数
   */
  private B_ToolInfo()
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
    setDataSetProperty(dsWorkCheck, null);
    dsWorkCheck.setSort(new SortDescriptor("", new String[]{"toolID"}, new boolean[]{false}, null, 0));
    dsWorkCheck.setSequence(new SequenceDescriptor(new String[]{"toolID"}, new String[]{"S_zl_toolInfo"}));
    //添加操作的触发对象
    B_WorkProcedure_Add_Edit add_edit = new B_WorkProcedure_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_WorkProcedure_Post());
    addObactioner(String.valueOf(DEL), new B_WorkProcedure_Delete());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
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
      if(dsWorkCheck.isOpen() && dsWorkCheck.changesPending())
        dsWorkCheck.reset();
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
    if(dsWorkCheck != null){
      dsWorkCheck.close();
      dsWorkCheck = null;
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
    if(!dsWorkCheck.isOpen())
      dsWorkCheck.open();
    return dsWorkCheck;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
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
     * @param  arg    触发者对象调用<toolID >notifyObactioners</toolID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      table.getWhereInfo().clearWhereValues();
      dsWorkCheck.setQueryString(Quality_SQL);
      if(dsWorkCheck.isOpen())
        dsWorkCheck.refresh();
      else
        dsWorkCheck.open();
      data.setMessage(showJavaScript("showFixedQuery();"));
    }
  }
  /**
   * 初始化操作的触发类
   */
  class Search implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<toolID >notifyObactioners</toolID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      table.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
        SQL = " WHERE "+SQL;
      SQL=Quality_SQL+SQL;
      dsWorkCheck.setQueryString(SQL);
      dsWorkCheck.setRowMax(null);
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
     * @param  arg    触发者对象调用<toolID >notifyObactioners</toolID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsWorkCheck.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsWorkCheck.getInternalRow();
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
     * @param  arg    触发者对象调用<toolID >notifyObactioners</toolID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      rowInfo.put(data.getRequest());
      String toolTypeID  = rowInfo.get("toolTypeID");//器具分类ID
      String deptid = rowInfo.get("deptid");//部门id
      String toolCode = rowInfo.get("toolCode");//器具编码
      String toolName = rowInfo.get("toolName");//器具名称
      String toolSpec = rowInfo.get("toolSpec");//器具规格型号
      String toolMemo = rowInfo.get("toolMemo");//器具备注
      if(!isAdd)
        ds.goToInternalRow(editrow);
      //if(toolTypeID.equals("")){
        //data.setMessage(showJavaScript("alert('检验标准编码不能为空！');"));
        //return;
      //}
      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("toolID", "-1");
      }
      ds.setValue("toolTypeID",toolTypeID);
      ds.setValue("deptid", deptid);
      ds.setValue("toolCode", toolCode);
      ds.setValue("toolName", toolName);
      ds.setValue("toolSpec", toolSpec);
      ds.setValue("toolMemo", toolMemo);
      ds.post();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_QUALITY_TOOLTYPE);//同步刷新数据
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
     * @param  arg    触发者对象调用<toolID >notifyObactioners</toolID >方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_QUALITY_TOOLTYPE);//同步刷新数据
    }
  }
}

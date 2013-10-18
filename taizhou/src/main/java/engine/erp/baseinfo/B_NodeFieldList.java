package engine.erp.baseinfo;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.action.BaseAction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 系统管理--界面信息维护--系统管理--权限代码维护--右键--界面字段</p>
 * <p>Description: 系统管理--权限代码维护--界面字段</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class B_NodeFieldList extends BaseAction implements Operate
{
  public static final String CHANGE_ISSHOW = "1001";
  /**
   * 提取界面显示字段的SQL语句
   */
  private static final String NODEFIELD_SQL = "SELECT * FROM nodeField WHERE nodeID= ";
  /**
   * 保存界面字段信息的数据集
   */
  private EngineDataSet dsnodefieldlist = new EngineDataSet();
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
  private String nodeID=null;
  /**
   * 得到界面显示字段信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回界面显示字段信息的实例
   */
  public static B_NodeFieldList getInstance(HttpServletRequest request)
  {
    B_NodeFieldList b_NodeFieldListBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_NodeFieldListBean";
      b_NodeFieldListBean = (B_NodeFieldList)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_NodeFieldListBean == null)
      {
        b_NodeFieldListBean = new B_NodeFieldList();
        session.setAttribute(beanName, b_NodeFieldListBean);
      }
    }
    return b_NodeFieldListBean;
  }
  /**
   * 构造函数
   */
  private B_NodeFieldList()
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
    setDataSetProperty(dsnodefieldlist, null);
    dsnodefieldlist.setSort(new SortDescriptor("", new String[]{"tableName","orderNum"}, new boolean[]{false, false}, null, 0));
    dsnodefieldlist.setSequence(new SequenceDescriptor(new String[]{"nodeFieldID"}, new String[]{"s_nodeField"}));
    //添加操作的触发对象
    B_NodeFieldList_Add_Edit add_edit = new B_NodeFieldList_Add_Edit();
    B_NodeFieldList_Post post = new B_NodeFieldList_Post();
    addObactioner(String.valueOf(INIT), new B_NodeFieldList_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_NodeFieldList_Post());
    addObactioner(String.valueOf(DEL), post);
    addObactioner(CHANGE_ISSHOW, post);
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
    try{
      String opearate = request.getParameter(OPERATE_KEY);
      if(opearate != null && opearate.trim().length() > 0)
      {
        RunData data = notifyObactioners(opearate, request, response, null);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsnodefieldlist.isOpen() && dsnodefieldlist.changesPending())
        dsnodefieldlist.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsnodefieldlist != null){
      dsnodefieldlist.close();
      dsnodefieldlist = null;
    }
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
    if(!dsnodefieldlist.isOpen())
      dsnodefieldlist.open();
    return dsnodefieldlist;
  }
  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }

  /**
   * 得到界面字段列表的页面的<select>控件的<option></option>的字符串
   * @param mixId 初始化选中的仓库ID
   * @return 界面显示字段列表
   */
  public synchronized final String getStoreForOption(String nodeID)
  {
    return dataSetToOption(getOneTable(), "nodeID", "fieldName", nodeID, null, null);
  }
  /**
   * 根据界面ID得到界面显示字段一行信息
   * @param bankId 初始化选中的界面ID
   * @return 显示名称
   */
  public synchronized final String getStoreName(String nodeID)
  {
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "nodeID");
    locateRow.setValue(0, nodeID);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      return getOneTable().getValue("fieldName");
    else
      return "";
  }
  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class B_NodeFieldList_Init implements Obactioner
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
      nodeID = data.getParameter("nodeID");
      nodeID = nodeID!= null ? nodeID.trim() : nodeID;
      dsnodefieldlist.setQueryString(NODEFIELD_SQL + nodeID);
      if(dsnodefieldlist.isOpen())
        dsnodefieldlist.refresh();
      else
        dsnodefieldlist.open();
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class B_NodeFieldList_Add_Edit implements Obactioner
  {
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
        dsnodefieldlist.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsnodefieldlist.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }
  /**
   * 保存操作的触发类
   */
  class B_NodeFieldList_Post implements Obactioner
  {
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
      if(action.equals(CHANGE_ISSHOW))
      {
        ds.goToRow(Integer.parseInt(rowInfo.get("rownum")));
        ds.setValue("isshow", ds.getValue("isshow").equals("1") ? "0" : "1");
        ds.post();
        ds.saveChanges();
      }
      else
      {
        String tableCode = rowInfo.get("tableCode");
        String tableName = rowInfo.get("tableName");
        String fieldName = rowInfo.get("fieldName");
        String fieldCode = rowInfo.get("fieldCode");
        String linkTable = rowInfo.get("linkTable");
        String enumValues = rowInfo.get("enumValues");
        String orderNum = rowInfo.get("orderNum");
        String isShow = rowInfo.get("isShow");
        String temp = null;
        if(isShow.equals("on") )
        {
          isShow="1";
        }
        else
        {
          isShow="0";
        }
          if(tableName.equals("")){
           data.setMessage(showJavaScript("alert('所属表名不能为空！');"));
         return;
        }
        if(fieldName.equals("")){
         data.setMessage(showJavaScript("alert('显示名称不能为空！');"));
         return;
        }
        if(fieldCode.equals("")){
         data.setMessage(showJavaScript("alert('字段名不能为空！');"));
         return;
        }
        if(orderNum.equals("")){
          orderNum="0";
         data.setMessage(showJavaScript("alert('排序号不能为空！');"));
         return;
        }
        else if((temp = BaseAction.checkNumber(orderNum,"排序号要为数字"))!=null)
        {
          data.setMessage(showJavaScript(temp));
          return;
        }
        if(!isAdd)
        {
          ds.goToInternalRow(editrow);
          ds.setValue("ischange", "1");
        }
        else
        {
          ds.insertRow(false);
          ds.setValue("nodeFieldID","-1");
          ds.setValue("isBak", "0");
          ds.setValue("nodeid", nodeID);
        }
        ds.setValue("tableCode", tableCode);
        ds.setValue("fieldName", fieldName);
        ds.setValue("tableName", tableName);
        ds.setValue("linkTable", linkTable);
        ds.setValue("fieldCode", fieldCode);
        ds.setValue("enumValues", enumValues);
        ds.setValue("orderNum", orderNum);
        ds.setValue("isShow", isShow);
        ds.post();
        ds.saveChanges();
        data.setMessage(showJavaScript("parent.hideInterFrame();"));
      }
    }
  }
  /**
   * 删除操作的触发类
   */
  class B_NodeFieldList_Delete implements Obactioner
  {
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
package engine.erp.quality;

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
 * <p>Title: 质量管理--检验类型管理</p>
 * <p>Description: 质量管理--检验类型管理<</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */

public final class B_CheckType extends BaseAction implements Operate
{
  /**
   *提取检验类型列表所有信息的SQL语句
   */
  private  static   final   String  Quality_SQL="SELECT * FROM ZL_CHECKTYPE order by checktypeid";
  /**
   * 建立检验类型列表信息的数据集
   */
  private EngineDataSet  dsTypeCheck=new  EngineDataSet();
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
  /**
 * 得到检验类型信息的实例
 * @param request jsp请求
 * @param isApproveStat 是否在审批状态
 * @return 返回收发单据信息的实例
 */
public static B_CheckType getInstance(HttpServletRequest request)
{
  B_CheckType b_TypeCheckBean = null;
  HttpSession session = request.getSession(true);
  synchronized(session)
  {
    String beanName = "TypeCheckBean";
    b_TypeCheckBean = (B_CheckType)session.getAttribute(beanName);
    //判断该session是否有该bean的实例
    if(b_TypeCheckBean == null)
    {
      b_TypeCheckBean = new B_CheckType();
      session.setAttribute(beanName, b_TypeCheckBean);
    }
  }
  return b_TypeCheckBean;
  }
  /**
   * 构造函数
   */
  private B_CheckType()
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
   setDataSetProperty(dsTypeCheck, Quality_SQL);
    //dsTypeCheck.setSort(new SortDescriptor("", new String[]{"ID"}, new boolean[]{false}, null, 0));
       // dsTypeCheck.setSequence(new SequenceDescriptor(new String[]{"ID"}, new String[]{"S_ZL_CHECKTYPE"}));
    //添加操作的触发对象
    B_WorkProcedure_Add_Edit add_edit = new B_WorkProcedure_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_WorkProcedure_Post());
    addObactioner(String.valueOf(DEL), new B_WorkProcedure_Delete());
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
      if(dsTypeCheck.isOpen() && dsTypeCheck.changesPending())
        dsTypeCheck.reset();
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
    if(dsTypeCheck != null){
      dsTypeCheck.close();
      dsTypeCheck = null;
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
    if(!dsTypeCheck.isOpen())
      dsTypeCheck.open();
    return dsTypeCheck;
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
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
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
        dsTypeCheck.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsTypeCheck.getInternalRow();
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
      String checktypeid = rowInfo.get("checkstyle");
      String wzlbid = rowInfo.get("wzlbid");

      if(wzlbid.equals("")){
        data.setMessage(showJavaScript("alert('请选择类别！');"));
      return;
      }
      if(!isAdd)
        ds.goToInternalRow(editrow);

      if(isAdd)
      {
        ds.insertRow(false);
        //ds.setValue("ID", "-1");
       String count  = dataSetProvider.getSequence("select count(*) FROM ZL_CHECKTYPE WHERE  checktypeid='"+checktypeid+"' and wzlbid='"+wzlbid+"'");
       if(!count.equals("0")){
         data.setMessage(showJavaScript("alert('已有相同检验类型和类别 ！');"));
         return;
       }
      }

      ds.setValue("checktypeid", checktypeid);
      ds.setValue("wzlbid", wzlbid);
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
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
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

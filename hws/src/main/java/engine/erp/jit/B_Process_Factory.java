package engine.erp.jit;

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
import engine.project.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 加工厂设置--</p>
 * <p>Description: 加工厂设置</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */

public final class B_Process_Factory extends BaseAction implements Operate
{
  /**
   * 提取工序分段信息的SQL语句
   */
  private static final String WORKPROCEDURE_SQL = "SELECT * FROM process_factory WHERE fctryid=";//

  /**
   * 保存工序分段信息的数据集
   */
  private EngineDataSet dsWorkProcedure = new EngineDataSet();

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
   * 得到工序分段信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回仓库信息的实例
   */
  public static B_Process_Factory getInstance(HttpServletRequest request)
  {
   B_Process_Factory B_Process_FactoryBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_Process_FactoryBean";
      B_Process_FactoryBean = (B_Process_Factory)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(B_Process_FactoryBean == null)
      {
        B_Process_FactoryBean = new B_Process_Factory();
        session.setAttribute(beanName, B_Process_FactoryBean);
      }
    }
    return B_Process_FactoryBean;
  }
  /**
   * 构造函数
   */
  private B_Process_Factory()
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
    setDataSetProperty(dsWorkProcedure, "SELECT * FROM process_factory ORDER BY fctrybm");
    dsWorkProcedure.setSort(new SortDescriptor("", new String[]{"fctrybm"}, new boolean[]{false}, null, 0));
    dsWorkProcedure.setSequence(new SequenceDescriptor(new String[]{"fctryid"}, new String[]{"s_process_factory"}));
    //添加操作的触发对象
    B_Process_Factory_Add_Edit add_edit = new B_Process_Factory_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_Process_Factory_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_Process_Factory_Post());
    addObactioner(String.valueOf(DEL), new B_Process_Factory_Delete());
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
      if(dsWorkProcedure.isOpen() && dsWorkProcedure.changesPending())
        dsWorkProcedure.reset();
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
    if(dsWorkProcedure != null){
      dsWorkProcedure.close();
      dsWorkProcedure = null;
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
    else
    {
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('process_factory','fctrybm','','',3) from dual");
      rowInfo.put("fctrybm", code);
    }
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsWorkProcedure.isOpen())
      dsWorkProcedure.open();
    return dsWorkProcedure;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_Process_Factory_Init implements Obactioner
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
  class B_Process_Factory_Add_Edit implements Obactioner
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
        dsWorkProcedure.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsWorkProcedure.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_Process_Factory_Post implements Obactioner
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
      String fctrybm = rowInfo.get("fctrybm");
      String fctryname = rowInfo.get("fctryname");
      String fctrybz = rowInfo.get("fctrybz");
      if(fctryname.equals("")){
        data.setMessage(showJavaScript("alert('执行工厂名称不能为空！');"));
        return;
      }
      if(!isAdd)
        ds.goToInternalRow(editrow);

      if(isAdd || !fctryname.equals(ds.getValue("fctryname")))
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('process_factory','fctryname','"+fctryname+"') from dual");
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('执行工厂名称("+ fctryname +")已经存在!');"));
          return;
        }
      }
      if(isAdd || !fctrybm.equals(ds.getValue("fctrybm")))
     {
       String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('process_factory','fctrybm','"+fctrybm+"') from dual");
       if(!count.equals("0"))
       {
         data.setMessage(showJavaScript("alert('执行工厂编号("+ fctrybm +")已经存在!');"));
         return;
       }
      }

      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("fctryid", "-1");
      }
      ds.setValue("fctrybm", fctrybm);
      ds.setValue("fctryname", fctryname);
      ds.setValue("fctrybz", fctrybz);
      ds.post();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PROCESS_FACTORY_TYPE);
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class B_Process_Factory_Delete implements Obactioner
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
      String fctryid = ds.getValue("fctryid");
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_gylxz WHERE fctryid="+fctryid);
      if(!count.equals("0")){
        data.setMessage(showJavaScript("alert('该执行工厂已被引用不能删除！')"));
        return;
      }
      else{
      ds.deleteRow();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PROCESS_FACTORY_TYPE);
      }
    }
  }
}

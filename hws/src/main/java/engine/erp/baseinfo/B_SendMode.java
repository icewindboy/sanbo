package engine.erp.baseinfo;

import engine.action.BaseAction;
import engine.action.Operate;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.web.observer.Obactioner;
import engine.common.LoginBean;

import engine.project.SysConstant;
import engine.project.LookupBeanFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

/**
 * <p>Title: 发货方式</p>
 * <p>Copyright: right reserved (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */
public final class B_SendMode extends BaseAction implements Operate
{
  private static final String SENDMODE_SQL = "SELECT * FROM jc_SendMode WHERE 1=1 ? ";
  private EngineDataSet dsB_SendMode = new EngineDataSet();//数据集
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = true;
  private long    editrow = 0;
  public  String retuUrl = null;
  /**
   * 从会话中得到银行信用卡信息的实例
   * @param request jsp请求
   * @return 返回银行信用卡信息的实例
   */
  public static B_SendMode getInstance(HttpServletRequest request)
  {
    B_SendMode B_SendModeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_SendModeBean";
      B_SendModeBean = (B_SendMode)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(B_SendModeBean == null)
      {
        B_SendModeBean = new B_SendMode();
        session.setAttribute(beanName, B_SendModeBean);//加入到session中
      }
    }
    return B_SendModeBean;
  }
  /**
   * 主册监听器
   * 构造函数
   */
  private B_SendMode()
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
    setDataSetProperty(dsB_SendMode, combineSQL(SENDMODE_SQL,"?",new String[]{""}));

    dsB_SendMode.setSort(new SortDescriptor("", new String[]{"sendmodecode"}, new boolean[]{false}, null, 0));//设置排序方式
    dsB_SendMode.setSequence(new SequenceDescriptor(new String[]{"SendModeID"}, new String[]{"S_JC_SendMode"}));//设置主健的sequence
    //添加操作的触发对象
    B_SendMode_Add_Edit add_edit = new B_SendMode_Add_Edit();

    addObactioner(String.valueOf(INIT), new B_SendMode_Init());//初始化 operate=0
    addObactioner(String.valueOf(ADD), add_edit);//新增
    addObactioner(String.valueOf(EDIT), add_edit);//修改
    addObactioner(String.valueOf(POST), new B_SendMode_Post());//保存
    addObactioner(String.valueOf(DEL), new B_SendMode_Delete());//删除
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
      if(dsB_SendMode.isOpen() && dsB_SendMode.changesPending())
        dsB_SendMode.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }

  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsB_SendMode != null){
      dsB_SendMode.close();
      dsB_SendMode = null;
    }
    log = null;
    rowInfo = null;
  }
  /**
   * 得到子类的类名
   * 实现BaseAction中的抽象方法
   * 日志中调用
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
      //调用存贮过程
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('jc_SendMode','SendModeCode','','',3) from dual");
      rowInfo.put("sendmodecode", code);
      }
  }
  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsB_SendMode.isOpen())
      dsB_SendMode.open();
    return dsB_SendMode;
  }

  /**
   *得到表的一行信息
   * */
  public final RowMap getRowinfo() {return rowInfo;}


  //==========================================
  //操作实现的类:初始化;新增,修改,删除
  //==========================================
  /**
   * 初始化操作的触发类
   */
  class B_SendMode_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class B_SendMode_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsB_SendMode.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsB_SendMode.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }
  /**
   * 保存操作的触发类
   */
  class B_SendMode_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      rowInfo.put(data.getRequest());
      String sendmodecode = rowInfo.get("sendmodecode");
      String sendmode = rowInfo.get("sendmode");
      if(sendmodecode.equals("")){
        data.setMessage(showJavaScript("alert('发货方式编码不能为空！');"));
        return;
      }
      if(sendmode.equals("")){
        data.setMessage(showJavaScript("alert('发货方式长度不能为空！');"));
        return;
      }

      if(!isAdd)
        ds.goToInternalRow(editrow);
      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("SendModeID", "-1");
      }
      ds.setValue("sendmodecode", sendmodecode);
      ds.setValue("sendmode", sendmode);
      ds.post();
      ds.saveChanges();
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
       LookupBeanFacade.refreshLookup(SysConstant.BEAN_SEND_MODE);
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }
  /**
   * 删除操作的触发类
   */
  class B_SendMode_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_BANK_CREDIT_CARD);
    }
  }
}
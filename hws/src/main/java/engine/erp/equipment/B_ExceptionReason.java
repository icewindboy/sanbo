package engine.erp.equipment;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.*;
import engine.common.*;
import engine.project.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 设备管理--故障原因设置</p>
 * <p>Description: 设备管理--故障原因设置<</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */
public final class B_ExceptionReason extends BaseAction implements Operate
{
  private static final String  equipment_SQL = "select * from sb_exceptionreason";
  private EngineDataSet dsexceptionreason= new EngineDataSet();
  private EngineRow locateRow = null;//用于定位数据集
  private RowMap rowInfo = new RowMap();//保存用户输入的信息
  public  boolean isAdd = true;//是否在添加状态
  private long editrow = 0;//保存修改操作的行记录指针
  public  String retuUrl = null;//点击返回按钮的URL
  /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_ExceptionReason getInstance(HttpServletRequest request)
  {
    B_ExceptionReason b_ExceptionReason = null;
    HttpSession session = request.getSession(true);
    synchronized(session)
    {
      String beanName = "b_ExceptionReason";
      b_ExceptionReason = (B_ExceptionReason)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_ExceptionReason == null)
      {
        b_ExceptionReason = new B_ExceptionReason();
        session.setAttribute(beanName,b_ExceptionReason);
      }
    }
    return b_ExceptionReason;
  }
  /**
    * 构造函数
    */
  public B_ExceptionReason()
  {
    try
    {
      jbInit();
    }
    catch(Exception ex)
    {
      log.error("jbInit", ex);
    }
  }

  /**
  * 初始化函数
  * @throws Exception 异常信息
  */
 protected void jbInit() throws Exception
 {
   setDataSetProperty(dsexceptionreason,equipment_SQL);
   dsexceptionreason.setSort(new SortDescriptor("", new String[]{"excepreasoncode"}, new boolean[]{false}, null, 0));
   dsexceptionreason.setSequence(new SequenceDescriptor(new String[]{"excepreasonid"}, new String[]{"s_sb_exceptionreason"}));
   //添加操作的触发对象
   addObactioner(String.valueOf(INIT), new B_ExceptionReason_Init());
   addObactioner(String.valueOf(ADD), new B_ExceptionReason_Add_Edit());
   addObactioner(String.valueOf(EDIT), new B_ExceptionReason_Add_Edit());
   addObactioner(String.valueOf(POST), new B_ExceptionReason_Post());
   addObactioner(String.valueOf(DEL), new B_ExceptionReason_Delete());
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
     if(dsexceptionreason.isOpen() && dsexceptionreason.changesPending())
       dsexceptionreason.reset();
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
   if(dsexceptionreason != null){
     dsexceptionreason.close();
     dsexceptionreason = null;
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
    if(!dsexceptionreason.isOpen())
      dsexceptionreason.open();
    return dsexceptionreason;
  }
  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_ExceptionReason_Init implements Obactioner
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
  class B_ExceptionReason_Add_Edit implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     isAdd = action.equals(String.valueOf(ADD));
     if(!isAdd)
     {
       dsexceptionreason.goToRow(Integer.parseInt(data.getParameter("rownum")));
       editrow = dsexceptionreason.getInternalRow();
     }
     initRowInfo(isAdd, true);
   }
  }
  /**
   * 保存操作的触发类
   */
class B_ExceptionReason_Post implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    EngineDataSet ds = getOneTable();
    //校验数据
    rowInfo.put(data.getRequest());
    String excepReasonCode = rowInfo.get("excepReasonCode");
    String excepReasonName = rowInfo.get("excepReasonName");
    if(!isAdd)
      ds.goToInternalRow(editrow);

    if(isAdd)
    {
      ds.insertRow(false);
      ds.setValue("excepreasonid", "-1");
    }
    ds.setValue("excepReasonCode", excepReasonCode);
    ds.setValue("excepReasonName", excepReasonName);
    ds.post();
    ds.saveChanges();
    data.setMessage(showJavaScript("parent.hideInterFrame();"));
    LookupBeanFacade.refreshLookup(SysConstant.BEAN_EQUIPMENT_EXCEPTIONREASON);//同步刷新数据
   }
  }
  /**
   * 删除操作的触发类
   */
 class B_ExceptionReason_Delete implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     EngineDataSet ds = getOneTable();
     String excepreasonid=data.getParameter("excepreasonid");
     String sql="select count(*) from (select a.excepreasonid from sb_mainresultdetail a where a.excepreasonid='"+excepreasonid+"'"+
                " union"+
                " select b.excepreasonid from sb_applydetail b where b.excepreasonid='"+excepreasonid+"')";
    String count = dataSetProvider.getSequence(sql);
    if(!count.equals("0")){
     data.setMessage(showJavaScript("alert('已被引用不能删除！');"));
   return;
    }
     ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
     ds.deleteRow();
     ds.saveChanges();
     LookupBeanFacade.refreshLookup(SysConstant.BEAN_QUALITY_TOOLTYPE);//同步刷新数据
   }
  }
}

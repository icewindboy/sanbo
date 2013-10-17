package engine.erp.system;

import engine.action.BaseAction;
import engine.action.Operate;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.web.observer.Obactioner;
import engine.common.*;
import engine.html.*;
import engine.project.*;
import java.text.*;
import java.lang.String;
import java.util.*;
import java.util.ArrayList;
import javax.servlet.http.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

public final class B_SystemLog extends BaseAction implements Operate
{
  private static final String RZ_SQL = "SELECT * FROM jc_rz WHERE 1=1 ? ";
  private EngineDataSet dsB_SystemLog = new EngineDataSet();//数据集
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = true;
  private long    editrow = 0;
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String fgsid = null;   //分公司ID

  public static final String DATABASE_CLEAN = "1068";

  public static B_SystemLog getInstance(HttpServletRequest request)
  {
    B_SystemLog B_SendModeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_SystemLogBean";
      B_SendModeBean = (B_SystemLog)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(B_SendModeBean == null)
      {
        B_SendModeBean = new B_SystemLog();
        session.setAttribute(beanName, B_SendModeBean);//加入到session中
      }
    }
    return B_SendModeBean;
  }
  /**
   * 主册监听器
   * 构造函数
   */
  private B_SystemLog()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsB_SystemLog != null){
      dsB_SystemLog.close();
      dsB_SystemLog = null;
    }
    log = null;
    rowInfo = null;
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsB_SystemLog, combineSQL(RZ_SQL,"?",new String[]{""}));

    dsB_SystemLog.setSort(new SortDescriptor("", new String[]{"czrq"}, new boolean[]{false}, null, 0));//设置排序方式
    dsB_SystemLog.setSequence(new SequenceDescriptor(new String[]{"rqID"}, new String[]{"S_JC_RZ"}));//设置主健的sequence

    addObactioner(String.valueOf(INIT), new B_SystemLog_Init());//初始化 operate=0
    addObactioner(String.valueOf(DEL), new B_SystemLog_Delete());//删除
    addObactioner(String.valueOf(DATABASE_CLEAN), new DataBase_clean());
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
      if(dsB_SystemLog.isOpen() && dsB_SystemLog.changesPending())
        dsB_SystemLog.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * 初始化操作的触发类
   */
  class B_SystemLog_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
    }
  }
    /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsB_SystemLog.isOpen())
      dsB_SystemLog.open();
    return dsB_SystemLog;
  }
  protected Class childClassName()
  {
    return getClass();
  }
  public final RowMap getRowinfo() {return rowInfo;}
  /**
   * 删除操作的触发类
   */
  class B_SystemLog_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
    }
  }
  class DataBase_clean implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet clean = new EngineDataSet();
      setDataSetProperty(clean,"SELECT * FROM jc_rz");
      clean.open();
      clean.deleteAllRows();
      //clean.empty();
      clean.post();
      clean.saveChanges();
      dsB_SystemLog.readyRefresh();
    }
  }
}






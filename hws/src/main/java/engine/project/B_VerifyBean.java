package engine.project;


import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.project.*;
import engine.html.*;
import engine.common.*;
import java.util.*;
import java.text.*;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
import engine.report.util.ReportData;
/**
 * <p>Title: </p>
 * <p>Description: <br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class B_VerifyBean extends BaseAction implements Operate
{
  private static final String REFERENCED_SQL        = "select count(*) from ?  where ? ? ?";//
  /**
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_VerifyBean getInstance(HttpServletRequest request)
  {
    B_VerifyBean B_VerifyBeanBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_VerifyBeanBean";
      B_VerifyBeanBean = (B_VerifyBean)session.getAttribute(beanName);
      if(B_VerifyBeanBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        B_VerifyBeanBean = new B_VerifyBean();
        session.setAttribute(beanName, B_VerifyBeanBean);
      }
    }
    return B_VerifyBeanBean;
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
        if(data == null)
          return showMessage("无效操作", false);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
  }
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();//Object类的方法
  }

  /**
   * table 表名或视图名
   * field 字段名
   * value 字段值
   * other 其他SQL条件
   * @param htid
   * @return
   */
  public  boolean hasReferenced(String table,String field,String value,String other)
  {
    if(table==null||table.equals(""))
      return false;
    if(field==null||field.equals(""))
      return false;
    if(value==null||value.equals(""))
      return false;
    String count="";
    String sql = combineSQL(REFERENCED_SQL,"?",new String []{table,field,"="+value});
    if(other!=null||!other.equals(""))
      sql = sql+other;
    try
    {
      count = dataSetProvider.getSequence(sql);
    }
    catch(Exception e)
    {
      return false;
    }
    if(!count.equals("0"))
      return true;
    else
      return false;
  }

  /**
   * @param table 表名或视图名
   * @param field 字段名
   * @param value 字段值
   * @return
   */
  public  boolean hasReferenced(String table,String field,String value)
  {
    if(table==null||table.equals(""))
      return false;
    if(field==null||field.equals(""))
      return false;
    if(value==null||value.equals(""))
      return false;
    String count="";
    String sql = combineSQL(REFERENCED_SQL,"?",new String []{table,field,"="+value});
    try
    {
      count = dataSetProvider.getSequence(sql);
    }
    catch(Exception e)
    {
      return false;
    }
    if(!count.equals("0"))
      return true;
    else
      return false;
  }
}
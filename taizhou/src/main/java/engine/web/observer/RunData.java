package engine.web.observer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */

public class RunData implements java.io.Serializable
{

  /**
   * The servlet request interface.
   */
  private HttpServletRequest req;

  /**
   * The servlet response interface.
   */
  private HttpServletResponse res;

  /**
   * 保存返回信息的消息.
   */
  private String message;

  /**
   * 得到servlet请求.
   * @return servlet请求.
   */
  public HttpServletRequest getRequest()
  {
      return this.req;
  }

  /**
   * 得到servlet响应.
   * @return servlet响应.
   */
  public HttpServletResponse getResponse()
  {
      return this.res;
  }

  /**
   * 得到servlet的session信息.
   * @return the session.
   */
  public HttpSession getSession()
  {
      return this.getRequest().getSession();
  }

  /**
   * 得到sevlet请求的参数
   * @return name 返回请求参数
   */
  public String getParameter(String name)
  {
    return this.getRequest().getParameter(name);
  }

  /**
   * 得到sevlet请求的参数
   * @patam req HttpServletRequest请求
   * @param name 参数名
   * @param nullvalue 得到的值是null返回的默认值
   * @return name 返回请求参数
   */
  public static String getParameter(HttpServletRequest req, String name, String nullvalue)
  {
    String param = req.getParameter(name);
    if(param == null)
      return nullvalue;
    return  param;
  }

  /**
   * 得到sevlet请求的参数
   * @param name 参数名
   * @param nullvalue 得到的值是null返回的默认值
   * @return name 返回请求参数
   */
  public String getParameter(String name, String nullvalue)
  {
    String param = this.getRequest().getParameter(name);
    if(param == null)
      return nullvalue;
    return  param;
  }

  /**
   * 得到sevlet请求的参数数组
   * @return name 返回请求参数数组
   */
  public String[] getParameterValues(String name)
  {
    return this.getRequest().getParameterValues(name);
  }
  /**
   * 得到servlet的session信息.
   * @param create 若true,则如果不存在此session就创建一个新的
   * @return the session.
   */
  public HttpSession getSession(boolean create)
  {
      return this.getRequest().getSession(create);
  }

  /**
   * 是否有信息
   * @return true 如果信息被设置.
   */
  public boolean hasMessage()
  {
      return this.message != null && this.message.length() > 0;
  }

  /**
   * 得到调用完执行者的返回信息,并清空保存在RunData中的信息
   * @return a string.
   */
  public String getMessage()
  {
    String msg = hasMessage() ? this.message : null;
    setMessage(null);
    return msg;
  }

  /**
   * 执行者可以调用这个方法设置信息.
   * @param msg 信息.
   */
  public void setMessage (String msg)
  {
      this.message = msg;
  }

  /**
   * 得到用户的浏览器代理信息
   * @return 返回浏览器代理信息.
   */
  public String getUserAgent()
  {
    return this.getRequest().getHeader( "User-Agent" );
  }

  /**
   * 设置servlet请求.
   * @param req servlet请求
   */
  void setRequest(HttpServletRequest req)
  {
      this.req = req;
  }

  /**
   * 设置servlet响应。
   * @param res servlet响应。
   */
  void setResponse(HttpServletResponse res)
  {
      this.res = res;
  }
}
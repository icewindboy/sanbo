package engine.common;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: 基础HttpServlet</p>
 * <p>Description: 一些公用的方法</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */

public abstract class BaseHttpServlet extends HttpServlet
{

  /**
   * 设置浏览器的没有缓存
   * @param response WEB的响应对像
   */
  public static void browserNoCache(HttpServletResponse response)
  {
    //关闭netscape浏览器缓存
    response.setHeader("Pragma","no-cache");
    //关闭IE浏览器缓存
    response.setHeader("Cache-Control","no-cache");
    response.setHeader("Cache-Control","no-store");
    response.setHeader("Cache-Control","post-check=0");
    response.setHeader("Cache-Control","pre-check=0");
    response.setHeader("Cache-Control","must-revalidate");
    //去掉代理服务器的缓存
    response.setHeader("Expires","0");
    //response.setDateHeader("Expires", 0);
  }
}

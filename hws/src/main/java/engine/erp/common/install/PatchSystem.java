package engine.erp.common.install;

import javax.servlet.http.*;
import java.util.Properties;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.*;
/**
 * <p>Title: 升级系统类</p>
 * <p>Description: 升级系统类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 江海岛
 * @version 1.0
 */

public final class PatchSystem extends BaseAction implements Operate
{
  public static final String CONFIG = "10000";
  public static final String PATCH  = "10001";

  private transient Properties props = null;
  private String webinfPath = null;
  private String filePath = null;
  public  boolean isRemote = false;

  /**
   * 得到安装系统的实例
   * @param request jsp请求
   * @return 返回安装的实例
   */
  public static PatchSystem getInstance(HttpServletRequest request)
  {
    PatchSystem patchSystemBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      patchSystemBean = (PatchSystem)session.getAttribute("patchSystemBean");
      if(patchSystemBean == null)
      {
        patchSystemBean = new PatchSystem();
        session.setAttribute("patchSystemBean", patchSystemBean);
      }
    }
    return patchSystemBean;
  }

  /**
   * 构造函数
   */
  private PatchSystem() {
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
  private final void jbInit() throws java.lang.Exception
  {
    addObactioner(CONFIG, new Config());
    addObactioner(PATCH,  new Patch());
  }

  /**
   * Implement this engine.project.BaseAction abstract method
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName() {
    return getClass();
  }

  //----Implementation of the BaseAction abstract class
  /**
   * JSP调用的函数. 业务操作的入口函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request, HttpServletResponse response) throws java.lang.Exception
  {
     try{
       String operate = request.getParameter(OPERATE_KEY);
       if(operate != null && operate.length()> 0)
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
       //if(dsMasterTable.isOpen() && dsMasterTable.changesPending())
         //dsMasterTable.reset();
       log.error("doService", ex);
       return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * session失效是触发的事件
   * @param event session绑定事件
   */
  public void valueUnbound(HttpSessionBindingEvent event) {
    ;
  }

  /**
   * 判断是否是本机安装并设置保存数据库连接文件的目录路径
   * @param request WEB请求
   * @param webinfpath WEB-INF目录的路径
   * @return 返回是否是本机安装
   * @throws IOException IO异常
   */
  public boolean isLocalHost(HttpServletRequest request, String webinfpath)
      throws IOException
  {
    if(!request.getRemoteAddr().equals("127.0.0.1"))
      return false;

    webinfPath = webinfpath;
    filePath = webinfpath + File.separator + "conf" + File.separator + InstallSystem.DBCONFIGNAME;
    return true;
  }

  /**
   * 数据库连接的属性文件的值
   * @param key 键
   * @return 返回值
   */
  public String getPropertyValue(String key) throws IOException
  {
    if(props==null)
      props = InstallSystem.getConfigInfo(filePath);
    if(props==null)
      return "";
    String s = (String)props.get(key);
    return s == null ? "" : s;
  }

  /**
   * 得到当前日期
   * @return
   */
  private String getCurrentDate()
  {
    return new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
  }

  /**
   * 配置数据库连接类
   */
  class Config implements Obactioner
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
      String url = data.getParameter("url", "");
      if(url.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请输入数据库连接的URL');"));
        return;
      }
      String username = data.getParameter("username", "").toUpperCase();
      if(username.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请输入连接数据库的用户名');"));
        return;
      }
      //
      String password = data.getParameter("password", "");
      if(password.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请输入连接数据库的用户密码');"));
        return;
      }
      //表空间名称
      String tablespace = data.getParameter("tablespace", "").toUpperCase();
      if(tablespace.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请输入表空间名称');"));
        return;
      }
      //索引空间名称
      String tableindex = data.getParameter("tableindex", "").toUpperCase();
      if(tableindex.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请输入索引空间名称');"));
        return;
      }
      //
      String minnumber = data.getParameter("minnumber", "");//
      String min = checkInt(minnumber, "连接池最小连接数");
      if(min != null)
      {
        data.setMessage(min);
        return;
      }

      String maxnumber = data.getParameter("maxnumber", "");//
      String max = checkInt(maxnumber, "连接池最大连接数");
      if(max != null)
      {
        data.setMessage(max);
        return;
      }
      //保存配值到内存
      props.put("url", url);
      props.put("username", username);
      props.put("password", password);
      props.put("tablespace", tablespace);
      props.put("tableindex", tableindex);
      props.put("maxnumber", maxnumber);
      props.put("minnumber", minnumber);
      //保存配置文件
      InstallSystem.updateConfigInfo(props, filePath);
      data.setMessage(showJavaScript("alert('配置成功！');"));
    }
  }

  /**
   * 升级补丁类
   */
  class Patch implements Obactioner
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
      //更新数据库的必要信息, 从sql文件中读取sql语句然后执行, 在更新基础数据
      String patchFileName = webinfPath + File.separator + "sql" + File.separator
                           + "patch.properties";
      //String afterFileName = webinfPath + File.separator + "sql" + File.separator
      //                     + "patch" + getCurrentDate() + ".properties";
      File patchFile = new File(patchFileName);
      if(!patchFile.exists() && !patchFile.isFile())
      {
        data.setMessage(showJavaScript("alert('没有需要升级的信息');"));
        return;
      }
      //属性提供者和更新者属性
      setProviderResolver();
      //取得数据库的连接
      if(props==null)
        props = InstallSystem.getConfigInfo(filePath);
      String driver = (String)props.get("driver");
      String url = (String)props.get("url");
      String username = (String)props.get("username");
      String password = (String)props.get("password");
      Class.forName(driver).newInstance();
      Connection conn = DriverManager.getConnection(url, username, password);
      //
      try{
        if(InstallSystem.resolveDataBase(patchFileName, props, conn, dataSetProvider, dataSetResolver))
          data.setMessage(showJavaScript("errorinfo();"));
        else
          data.setMessage(showJavaScript("alert('升级成功');"));
      }
      finally{
        try{
          if(conn != null)
            conn.close();
        }
        catch(SQLException ex){}
        //更改文件名称
        //patchFile.renameTo(new File(afterFileName));
      }
    }
  }
}
package engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.PropertyConfigurator;

import engine.util.EngineRuntimeException;
import engine.util.StringUtils;

/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public final class EngineServlet extends HttpServlet
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 8949846429285544661L;
private static String databaseConfigFile = null;
  private static String databaseType = null;
  private static String lookupConfigFile = null;
  private static boolean isDevelop = false;

  private static long lookupAccessTime = -1;
  private static long lookupModifiedTime = -1;

  private static boolean isInit = false;

  public void init() throws ServletException {
    super.init();
    if(isInit)
      return;
    ServletConfig servletConfig = getServletConfig();
    String webpath = getServletContext().getRealPath("/");

    databaseType        = servletConfig.getInitParameter("dbType");
    String dbConfig     = servletConfig.getInitParameter("dbConfig");
    String lookupConfig = servletConfig.getInitParameter("lookupConfig");
    String logConfig    = servletConfig.getInitParameter("logConfig");
    String mode         = servletConfig.getInitParameter("mode");

    isDevelop = mode != null && mode.equalsIgnoreCase("develop");
    databaseConfigFile = webpath + dbConfig;
    if(lookupConfig != null && lookupConfig.length() > 0)
      lookupConfigFile = webpath + lookupConfig;
    isInit = true;
    //注册log4j属性
    if(logConfig != null){
      FileInputStream io = null;
      try{
        io = new FileInputStream(webpath + logConfig);
        //io.re
        Properties config = new Properties();
        config.load(io);
        Set entrySet = config.entrySet();
        Map.Entry[] entrys = (Map.Entry[])entrySet.toArray(new Map.Entry[entrySet.size()]);
        for(int i=0; i<entrys.length; i++)
        {
          String value = (String)entrys[i].getValue();
          if(value.indexOf("{webapp}") > -1){
            value = StringUtils.replace(value, "{webapp}", webpath);
            entrys[i].setValue(value);
          }
        }
        PropertyConfigurator.configure(config);
      }
      catch(IOException ex){
//        System.out.println("reg log4j config file error:");
        ex.printStackTrace();
      }
      finally{
        try{
          io.close();
        }
        catch(Exception ex){}
      }
    }
  }

  public synchronized static String getDatabaseConfigFile(){
    if(!isInit)
      throw new EngineRuntimeException("the EngineServlet no initialize!");
    return databaseConfigFile;
  }

  public synchronized static boolean isOracleDatabase(){
    if(!isInit)
      throw new EngineRuntimeException("the EngineServlet no initialize!");
    return databaseType == null || databaseType.length() == 0 ||
           databaseType.trim().equalsIgnoreCase("oracle");
  }

  public synchronized static File getLookupConfigFile(){
    if(!isInit)
      throw new EngineRuntimeException("the EngineServlet no initialize!");
    if(lookupConfigFile == null)
      return null;

    if(lookupAccessTime < 0)
    {
      lookupAccessTime = System.currentTimeMillis();
      File file = new File(lookupConfigFile);
      lookupModifiedTime = file.lastModified();
      return file;
    }
    else if(isDevelop)
    {
      long currTime = System.currentTimeMillis();
      if(currTime-lookupAccessTime > 5*60*1000)
      {
        lookupAccessTime = currTime;
        File file = new File(lookupConfigFile);
        long currLookupTime = file.lastModified();
        if(lookupModifiedTime != currLookupTime)
        {
          lookupModifiedTime = currLookupTime;
          return file;
        }
        else
          return null;
      }
      return null;
    }
    else
      return null;
  }
  public void destroy(){
    super.destroy();
    isInit = false;
  }
}
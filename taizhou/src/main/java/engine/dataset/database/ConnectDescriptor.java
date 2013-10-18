package engine.dataset.database;

import java.io.Serializable;
import java.util.Map;
/**
 * <p>Title: 连接描述类</p>
 * <p>Description: 保存数据库连接描述信息</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: engine3</p>
 * @author hukn
 * @version 1.0
 */

public final class ConnectDescriptor implements Serializable
{
  //---------------------------------------------------------
  //--数据库配置文件键
  //---------------------------------------------------------
  private final static String DB_DRIVEER      = "password";
  private final static String DB_URL          = "url";
  private final static String DB_USER         = "username";
  private final static String DB_PASSWORD     = "password";
  private final static String DB_MAX          = "maxnumber";
  private final static String DB_MIN          = "minnumber";

  private transient String url       = null;
  private transient String username  = null;
  private transient String password  = null;
  private transient String driver    = null;
  private transient int max          = -1;
  private transient int min          = 1;

  public ConnectDescriptor(){}

  public ConnectDescriptor(Map config)
  {
    this.driver   = (String)config.get(DB_DRIVEER);
    this.url      = (String)config.get(DB_URL);
    this.username = (String)config.get(DB_USER);
    this.password = (String)config.get(DB_PASSWORD);
    setMax((String)config.get(DB_MAX));
    setMin((String)config.get(DB_MIN));
  }

  public String getDriver()
  {
    return driver;
  }

  public void setDriver(String driver)
  {
    this.driver = driver;
  }

  public int getMax()
  {
    return max;
  }

  public void setMax(String max)
  {
    try{
      this.max = Integer.parseInt(max);
    }
    catch(Exception ex){
      this.max = -1;
    }
  }

  public void setMax(int max)
  {
    this.max = max;
  }

  public int getMin()
  {
    return min;
  }

  public void setMin(int min)
  {
    this.min = min;
  }

  public void setMin(String min)
  {
    try{
      this.min = Integer.parseInt(min);
    }
    catch(Exception ex){
      this.min = -1;
    }
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }
}
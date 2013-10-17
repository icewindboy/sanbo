package engine.util;

/**
 * <p>Title: 属性管理类</p>
 * <p>Description: 属性管理类
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */
import java.util.*;
import java.io.*;

public class PropertyManager implements java.io.Serializable{

  private static final String propsName = "/public.properties";

  private static Hashtable htManagers = null;

  /**
   * 提取相应属性文件的管理器
   * @return 返回属性管理器
   */
  public static DefaultPropertyManager getPublicPropertyManager()
  {
    return getPropertyManager(propsName, false, null, null);
  }
  /**
   * 提取相应属性文件的管理器
   * @param isKeyToCap 是否将所有的key装化为大写的
   * @return 返回属性管理器
   */
  public static DefaultPropertyManager getPublicPropertyManager(boolean isKeyToCap)
  {
    return getPropertyManager(propsName, isKeyToCap, null, null);
  }
  /**
   * 提取相应属性文件的管理器
   * @param resourceURI 资源文件（属性文件）的URI
   * @return 返回属性管理器
   */
  public static DefaultPropertyManager getPropertyManager(String resourceURI)
  {
    return getPropertyManager(resourceURI, false, null, null);
  }
  /**
   * 提取相应属性文件的管理器
   * @param resourceURI 资源文件（属性文件）的URI
   * @param isKeyToCap 是否将所有的key装化为大写的
   * @param encoding 读取文件的编码方式 若为null则用平台默认的编码方式
   * @return 返回属性管理器
   */
  public static DefaultPropertyManager getPropertyManager(String resourceURI,
      boolean isKeyToCap, String encoding)
  {
    return getPropertyManager(resourceURI, isKeyToCap, encoding, null);
  }
  /**
   * 提取相应属性文件的管理器
   * @param resourceURI 资源文件（属性文件）的URI
   * @param isKeyToCap 是否将所有的key装化为大写的
   * @param encoding 读取文件的编码方式 若为null则用平台默认的编码方式
   * @param filePath 保存属性文件的路径
   * @return 返回属性管理器
   */
  public static synchronized DefaultPropertyManager getPropertyManager(String resourceURI,
      boolean isKeyToCap, String encoding, String filePath)
  {
    DefaultPropertyManager propsManager = null;
    if(htManagers == null)
    {
      htManagers = new Hashtable(8);
      propsManager = new DefaultPropertyManager(resourceURI, isKeyToCap, encoding, filePath);
      htManagers.put(resourceURI, propsManager);
    }
    else
    {
      propsManager = (DefaultPropertyManager)htManagers.get(resourceURI);
      if(propsManager == null)
      {
        propsManager = new DefaultPropertyManager(resourceURI, isKeyToCap, encoding, filePath);
        htManagers.put(resourceURI, propsManager);
      }
    }
    return propsManager;
  }
}
package engine.util;

import java.util.*;
import java.io.*;
import engine.util.log.*;
/**
 * <p>Title: 单个属性管理的类/p>
 * <p>Description: 单个属性管理的类
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */

/**
 * 单个属性管理的类
 */
public class DefaultPropertyManager implements java.io.Serializable
{
  private Properties properties = null;
  private Object propertiesLock = new Object();
  private String resourceURI;//资源文件（属性文件）的URI
  private String writeFilePath;
  private String encoding;
  private boolean isKeyToCap;
  private static final String DEFAULT_FILE_PATH = "path";//默认保存属性文件的的键值
  private Log log =  new Log(getClass());
  /**
   * 构造函数
   * Creates a new PropertyManager
   * @param resourceURI 资源文件的URI
   */
  public DefaultPropertyManager(String resourceURI) {
    this(resourceURI, false);
  }
  /**
   * 构造函数
   * Creates a new PropertyManager
   * @param resourceURI 资源文件的URI
   * @param filePath 保存属性文件的路径
   */
  public DefaultPropertyManager(String resourceURI, boolean isKeyToCap) {
    this(resourceURI, isKeyToCap, null);
  }
  /**
   * 构造函数
   * Creates a new PropertyManagere
   * @param resourceURI 资源文件的URI
   * @param isKeyToCap 是否将所有的key装化为大写的
   * @param encoding 读取文件的编码方式 若为null则用平台默认的编码方式
   */
  public DefaultPropertyManager(String resourceURI, boolean isKeyToCap, String encoding) {
    this(resourceURI, isKeyToCap, encoding, null);
  }
  /**
   * 构造函数
   * Creates a new PropertyManager
   * @param resourceURI 资源文件的URI
   * @param isKeyToCap 是否将所有的key装化为大写的
   * @param encoding 读取文件的编码方式 若为null则用平台默认的编码方式
   * @param filePath 保存属性文件的路径
   */
  public DefaultPropertyManager(String resourceURI, boolean isKeyToCap, String encoding, String filePath) {
    this.resourceURI = resourceURI;
    this.isKeyToCap = isKeyToCap;
    this.writeFilePath = filePath;
    this.encoding = encoding;
  }
  /**
   * 设置保存属性文件的路径
   * @param filePath 保存属性文件的路径
   */
  public void setFilePath(String filePath)  {
    this.writeFilePath = filePath;
  }
  /**
   * 提取一个属性值.所有的属性保存在public.properties文件中，改文件在calsspath中可以访问。提取属性是一个很快的操作
   * Gets a property. properties are stored in public.properties.
   * The properties file should be accesible from the classpath.
   * Additionally, it should have a path field that gives the full path to where the file is located.
   * Getting properties is a fast operation.
   * @param name the name of the property to get.
   * @return the property specified by name.
   */
  public String getProp(String name) {
      //If properties aren't loaded yet. We also need to make this thread safe, so synchronize...
      if (properties == null) {
          synchronized(propertiesLock) {
              //Need an additional check
              if (properties == null) {
                  loadProps();
              }
          }
      }
      String property = properties.getProperty(name);
      if (property == null) {
          return null;
      }
      else {
          return property.trim();
      }
  }

  /**
   * 设置属性。每次设置属性后，属性文件必须要保存到硬盘中
   * Sets a property. Because the properties must be saved to disk
   * every time a property is set, property setting is relatively slow.
   * @param name 属性名
   * @param value 属性值
   */
  public void setProp(String name, String value) {
      //Only one thread should be writing to the file system at once.
      synchronized (propertiesLock) {
          //Create the properties object if necessary.
          if (properties == null) {
              loadProps();
          }
          properties.setProperty(name, value);
          saveProps();
      }
  }

  /**
   * 删除属性
   * @param name 属性名
   */
  public void deleteProp(String name) {
      //Only one thread should be writing to the file system at once.
      synchronized (propertiesLock) {
          //Create the properties object if necessary.
          if (properties == null) {
              loadProps();
          }
          properties.remove(name);
          saveProps();
      }
  }

  /**
   * 得到所有属性名称
   * @return 返回所有属性名称
   */
  public Enumeration propNames() {
      //If properties aren't loaded yet. We also need to make this thread safe, so synchronize...
      if (properties == null) {
          synchronized(propertiesLock) {
              //Need an additional check
              if (properties == null) {
                  loadProps();
              }
          }
      }
      return properties.propertyNames();
  }

  /**
   * 从硬盘上装载属性文件
   * Loads properties from the disk.
   */
  private void loadProps() {
      properties = new Properties();
      InputStream in = null;
      try {
          in = getClass().getResourceAsStream(resourceURI);
          ParseProperties.parse(properties, in, encoding, isKeyToCap);
          //properties.load(in);
      }
      catch (Exception e) {
        log.error("Reading properties in DefaultPropertyManager.loadProps()", e);
      }
      finally {
        try {
          in.close();
        } catch (Exception e) { }
      }
  }

  /**
   * 保存属性到硬盘中
   * Saves properties to disk.
   */
  private void saveProps() {
      //Now, save the properties to disk. In order for this to work, the user
      //needs to have set the path field in the properties file. Trim
      //the String to make sure there are no extra spaces.
      String path = writeFilePath ==null ? getProp(DEFAULT_FILE_PATH) : writeFilePath.trim();
      if(path == null)
        return;
      OutputStream out = null;
      try {
          out = new FileOutputStream(path);
          properties.store(out, new java.util.Date().toString());
      }
      catch (Exception ioe) {
        log.error("Writing properties to "+ path +". Ensure that the path exists and that the process has permission to write to it!", ioe);
      }
      finally {
        try {
          out.close();
        } catch (Exception e) { }
      }
  }

  /**
   * 判断属性文件是否可读
   * Returns true if the properties are readable.
   * @return 如果可读返回true
   */
  public boolean propFileIsReadable() {
      try {
          InputStream in = getClass().getResourceAsStream(resourceURI);
          return true;
      }
      catch (Exception e) {
          return false;
      }
  }

  /**
   * 判断属性文件是否存在
   * Returns true if the public.properties file exists where the path property purports that it does.
   * @return 如果public.properties文件存在返回true
   */
  public boolean propFileExists() {
      String path = writeFilePath ==null ? getProp(DEFAULT_FILE_PATH) : writeFilePath.trim();;
      if( path == null ) {
          return false;
      }
      File file = new File(path);
      return file.isFile();
  }

  /**
   * 判断属性文件是否具有可写
   * Returns true if the properties are writable.
   * @return 如果可写返回true
   */
  public boolean propFileIsWritable()
  {
    String path = writeFilePath ==null ? getProp(DEFAULT_FILE_PATH) : writeFilePath.trim();
    if(path == null)
      return false;
    File file = new File(path);
    return file.isFile() ? true : file.canWrite() ? true : false;
  }
}
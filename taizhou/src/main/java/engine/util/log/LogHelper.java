package engine.util.log;

import org.apache.log4j.*;
import java.io.*;
/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */

public class LogHelper extends Logger implements Serializable{
  /**
   * 构造函数
   * @param name 实例的名称
   */
  public LogHelper(String name)
  {
    super(name);
  }

  private static LogHelperFactory myFactory = new LogHelperFactory();

  /**
   * 得到LogHelper的实例
   * @param name 实例的名称
   * @return 返回LogHelper的实例
   */
  public static LogHelper getLogHelper(String name) {
    return (LogHelper)Logger.getLogger(name, myFactory);
  }
  /**
   * 得到LogHelper的实例
   * @param clazz 需要创建实例的类
   * @return 返回LogHelper的实例
   */
  public static LogHelper getLogHelper(Class clazz) {
    return getLogHelper(clazz.getName());
  }

  /**
   * 重载父类的方法, 得到LogHelper的实例, 这是需要强制转化, 如：(LogHelper)getLogger("name")
   * @param name 实例的名称
   * @return 返回LogHelper的实例
   */
  public static Logger getLogger(String name) {
    return getLogHelper(name);
  }

  /**
   * 重载父类的方法, 得到LogHelper的实例, 这是需要强制转化, 如：(LogHelper)getLogger("name")
   * @param clazz 需要创建实例的类
   * @return 返回LogHelper的实例
   */
  public static Logger getLogger(Class clazz) {
    return getLogHelper(clazz.getName());
  }

  /**
   * 重载父类的方法, 得到LogHelper的实例, 这是需要强制转化, 如：(LogHelper)getLogger("name")
   * @param name 实例的名称
   * @return 返回LogHelper的实例
   */
  public static Category getInstance(String name) {
    return getLogHelper(name);
  }

  /**
   * 重载父类的方法, 得到LogHelper的实例, 这是需要强制转化, 如：(LogHelper)getLogger("name")
   * @param clazz 需要创建实例的类
   * @return 返回LogHelper的实例
   */
  public static Category getInstance(Class clazz) {
    return getLogHelper(clazz.getName());
  }

  private void writeObject(ObjectOutputStream oos) throws IOException
  {
    //oos.defaultWriteObject();
  }

  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException
  {
    //ois.defaultReadObject();
  }

}
package engine.util.log;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
/**
 * <p>Title: 日志助手工厂</p>
 * <p>Description: 用于创建新的对象{@link LogHelper}的工厂
 * A factory that makes new {@link LogHelper} objects.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */

public class LogHelperFactory implements LoggerFactory, java.io.Serializable
{
  /**The constructor should be public as it will be
   called by configurators in different packages. */
  public LogHelperFactory()
  {
  }

  /**
   * 实现LoggerFactory的方法
   * @param name Logger实例的名称
   * @return Logger实例
   */
  public Logger makeNewLoggerInstance(String name) {
    return new LogHelper(name);
  }
}
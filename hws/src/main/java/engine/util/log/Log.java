package engine.util.log;

import java.io.*;
import org.apache.log4j.Logger;
/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public final class Log implements Serializable
{
  private String className = null;
  private transient Logger log = null;

  public Log(){}

  public Log(String name){
    this.className = name;
  }

  public Log(Class logClass){
    this.className = logClass.getName();
  }

  public void setName(String name){
    this.className = name;
  }

  public String getName(){
    return this.className;
  }

  public void info(Object message) {
    getLog().info(message);
  }

  public void info(Object message, Throwable t) {
    getLog().info(message, t);
  }

  public void debug(Object message) {
    getLog().debug(message);
  }

  public void debug(Object message, Throwable t) {
    getLog().debug(message, t);
  }

  public void warn(Object message) {
    getLog().warn(message);
  }

  public void warn(Object message, Throwable t) {
    getLog().warn(message, t);
  }

  public void error(Object message) {
    getLog().error(message);
  }

  public void error(Object message, Throwable t) {
    getLog().error(message, t);
  }

  public void fatal(Object message) {
    getLog().fatal(message);
  }

  public void fatal(Object message, Throwable t) {
    getLog().fatal(message, t);
  }

  public boolean isDebugEnabled(){
    return getLog().isDebugEnabled();
  }

  public boolean isInfoEnabled(){
    return getLog().isInfoEnabled();
  }

  private Logger getLog()
  {
    if(log == null)
      log = Logger.getLogger(className);
    return log;
  }
}
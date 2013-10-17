package engine.report.util;

import java.util.Map;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import engine.report.event.*;
import engine.util.StringUtils;
import engine.util.log.Log;
/**
 * <p>Title: 报表监听器</p>
 * <p>Description: 报表监听器
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */

public final class ReportListener implements java.io.Serializable
{

  private static Log log = new Log("ProcessListener");

  public static final String TEMPLET_PROVIDE_LISTENER  = "TempletProvideListener";

  public static final String TEMPLET_AFTER_PROVIDED_LISTENER = "TempletAfterProvideListener";

  public static final String TEMPLET_LOAD_LISTENER = "TempletLoadListener";

  public static final String REPORT_PROVIDE_SQL_LISTENER = "ReportProvideSqlListener";

  public static final String REPORT_DATA_LOADING_LISTENER = "ReportDataLoadingListener";

  public static final String REPORT_DATA_LOADED_LISTENER = "ReportDataLoadedListener";

  private Map listeners = null;

  /**
   * 处理各个监听器
   * @param listenerInfos 监听器属性信息
   */
  public void processListener(Map listenerInfos)
  {
    if(listenerInfos == null)
      return;
    clear();
    processListeners(this, listenerInfos);
  }

  /**
   * 得到报表模板对象
   * @return 返回报表模板对象
   */
  public TempletProvideListener getTempletProvideListener()
  {
    return (TempletProvideListener)getListeners().get(TEMPLET_PROVIDE_LISTENER);
  }

  /**
   * 得到报表模板对象
   * @return 返回报表模板对象
   */
  public TempletAfterProvideListener getTempletAfterProvideListener()
  {
    return (TempletAfterProvideListener)getListeners().get(TEMPLET_AFTER_PROVIDED_LISTENER);
  }

  /**
   * 得到报表模板装载的监听器
   * @return 报表模板装载的监听器
   */
  public TempletLoadListener getTempletLoadListener()
  {
    return (TempletLoadListener)getListeners().get(TEMPLET_LOAD_LISTENER);
  }

  /**
   * 得到报表数据装载前的监听器实例
   * @return 报表数据装载前的监听器实例
   */
  public ReportProvideSqlListener getReportProvideSqlListener()
  {
    return (ReportProvideSqlListener)getListeners().get(REPORT_PROVIDE_SQL_LISTENER);
  }

  /**
   * 得到报表数据装载的监听器实例
   * @return 报表数据装载前的监听器实例
   */
  public ReportDataLoadingListener getReportDataLoadingListener()
  {
    return (ReportDataLoadingListener)getListeners().get(REPORT_DATA_LOADING_LISTENER);
  }

  /**
   * 得到报表数据装载后的监听器实例
   * @return 报表数据装载后的监听器实例
   */
  public ReportDataLoadedListener getReportDataLoadedListener()
  {
    return (ReportDataLoadedListener)getListeners().get(REPORT_DATA_LOADED_LISTENER);
  }

  /**
   * 得到保存监听器的hash
   * @return 返回监听器的hash
   */
  private Map getListeners()
  {
    if(listeners == null)
      listeners = new Hashtable();
    return listeners;
  }

  /**
   * 清楚监听器
   */
  public void clear()
  {
    if(listeners != null)
      listeners.clear();
  }

  /**
   * 释放资源
   */
  public void release()
  {
    listeners = null;
  }

  /**
   * 处理提供模板的监听器
   * @param reportListener 报表各个监听器的容器
   * @param listenerInfos  监听器属性信息
   */
  private static final void processListeners(
      ReportListener reportListener, Map listenerInfos)
  {
    synchronized(listenerInfos)
    {
      Map mapListener = reportListener.getListeners();

      String className = (String)listenerInfos.get(TEMPLET_PROVIDE_LISTENER);
      Object obj = getInstance(TEMPLET_PROVIDE_LISTENER, className);
      if(obj != null)
        mapListener.put(TEMPLET_PROVIDE_LISTENER, obj);

      className = (String)listenerInfos.get(TEMPLET_AFTER_PROVIDED_LISTENER);
      obj = getInstance(TEMPLET_AFTER_PROVIDED_LISTENER, className);
      if(obj != null)
        mapListener.put(TEMPLET_AFTER_PROVIDED_LISTENER, obj);

      className = (String)listenerInfos.get(TEMPLET_LOAD_LISTENER);
      obj = getInstance(TEMPLET_LOAD_LISTENER, className);
      if(obj != null)
        mapListener.put(TEMPLET_LOAD_LISTENER, obj);

      className = (String)listenerInfos.get(REPORT_PROVIDE_SQL_LISTENER);
      obj = getInstance(REPORT_PROVIDE_SQL_LISTENER, className);
      if(obj != null)
        mapListener.put(REPORT_PROVIDE_SQL_LISTENER, obj);

      className = (String)listenerInfos.get(REPORT_DATA_LOADING_LISTENER);
      obj = getInstance(REPORT_DATA_LOADING_LISTENER, className);
      if(obj != null)
        mapListener.put(REPORT_DATA_LOADING_LISTENER, obj);

      className = (String)listenerInfos.get(REPORT_DATA_LOADED_LISTENER);
      obj = getInstance(REPORT_DATA_LOADED_LISTENER, className);
      if(obj != null)
        mapListener.put(REPORT_DATA_LOADED_LISTENER, obj);
    }
  }

  /**
   * 得到实例
   * @param listenerName 监听器键值
   * @param className 类名
   * @return 返回实例
   */
  private static final Object getInstance(String listenerName, String className)
  {
    if(className == null)
      return null;

    try{
      if(listenerName.equals(TEMPLET_PROVIDE_LISTENER))
        return (TempletProvideListener)Class.forName(className).newInstance();

      if(listenerName.equals(TEMPLET_AFTER_PROVIDED_LISTENER))
        return (TempletAfterProvideListener)Class.forName(className).newInstance();

      else if(listenerName.equals(TEMPLET_LOAD_LISTENER))
        return (TempletLoadListener)Class.forName(className).newInstance();

      else if(listenerName.equals(REPORT_PROVIDE_SQL_LISTENER))
        return (ReportProvideSqlListener)Class.forName(className).newInstance();

      else if(listenerName.equals(REPORT_DATA_LOADING_LISTENER))
        return (ReportDataLoadingListener)Class.forName(className).newInstance();

      else if(listenerName.equals(REPORT_DATA_LOADED_LISTENER))
        return (ReportDataLoadedListener)Class.forName(className).newInstance();
    }
    catch(ClassNotFoundException ex){
      getLog().warn("Not found the class of "+className, ex);
    }
    catch(ClassCastException ex){
      getLog().warn("Incorrect Cast Class "+ className + " for class "+listenerName, ex);
    }
    catch(Exception ex){
      getLog().warn("Exception for get instance of class "+className, ex);
    }
    return null;
  }


  /**
   * 得到写日志对象
   * @return 返回写日志对象
   */
  private static final Log getLog()
  {
    return log;
  }
}
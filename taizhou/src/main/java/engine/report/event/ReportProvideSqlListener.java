package engine.report.event;

import java.io.Serializable;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
/**
 * <p>Title: 报表数据装载前的监听器</p>
 * <p>Description: 报表数据装载前的监听器</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */
public interface ReportProvideSqlListener extends Serializable
{
  /**
   * 报表数据装载前的事件调用的方法
   * @param request WEB请求
   * @param templet 报表模板对象
   * @param context 报表上下文对象
   */
  public String getQuery(ServletRequest request, TempletData templet, ContextData context);
}
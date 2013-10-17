package engine.report.event;

import java.io.Serializable;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
/**
 * <p>Title: report</p>
 * <p>Description: 报表工具</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public interface TempletAfterProvideListener extends Serializable
{

  /**
   * 报表模板打开后的事件调用的方法
   * @param request WEB请求
   * @param templet 报表模板对象
   * @param context 报表上下文对象
   * @param response 模板装载的响应对象
   */
  public void templetAfterProvide(ServletRequest request, TempletData templet,
                                  ContextData context, TempletProvideResponse response);

}
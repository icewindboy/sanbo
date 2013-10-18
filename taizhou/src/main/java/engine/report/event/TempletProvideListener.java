package engine.report.event;

import java.io.Serializable;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;

/**
 * <p>Title: 提供报表模板监听器</p>
 * <p>Description: 提供报表模板监听器</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */
public interface TempletProvideListener extends Serializable
{

  /**
   * 提供模板对象
   * @param request http请求
   * @param templet 报表模板对象，可能是null值
   * @param context 报表上下文对象
   * @return 返回模板对象
   */
  public TempletData getTemplet(ServletRequest request, TempletData templet,
                                ContextData context);
}
package engine.report.event;

import java.io.Serializable;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;

/**
 * <p>Title: 报表数据打开后的监听器</p>
 * <p>Description: 报表数据打开后的监听器</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */

public interface ReportDataLoadedListener extends Serializable
{

  /**
   * 报表数据打开后的事件调用的方法
   * @param request WEB请求
   * @param templet 报表模板对象
   * @param context 报表上下文对象
   * @param dsRep   报表数据对象对象
   */
  public void dataLoaded(ServletRequest request, TempletData templet,
                         ContextData context, EngineDataSet dsRep);

}
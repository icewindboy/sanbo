package engine.erp.store.xixing;


import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.report.event.TempletProvideResponse;
import engine.report.event.TempletAfterProvideListener;
import engine.report.util.ReportData;
import engine.dataset.*;
import engine.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import engine.erp.sale.shengyu.B_Sale_CheckAccount;
/**
 * <p>Title:  </p>
 * <p>Description:  主从表的数据集分别提供,放入在RowMap中</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 胡康宁
 * @version 1.0
 */

public final class B_AssistantMaterialPrint implements TempletAfterProvideListener
{
  /**
   * 报表模板打开后的事件调用的方法
   * @param request WEB请求
   * @param templet 报表模板对象
   * @param context 报表上下文对象
   * @param response 模板装载的响应对象
   */
  public void templetAfterProvide(ServletRequest request, TempletData templet, ContextData context, TempletProvideResponse response)
  {
    //引用LoginBean
    HttpServletRequest req = (HttpServletRequest)request;
    ReportData td = (ReportData)req.getAttribute("dzd");
    context.addReportData(td);
  }
}
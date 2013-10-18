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
 * <p>Description:  ���ӱ����ݼ��ֱ��ṩ,������RowMap��</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author ����
 * @version 1.0
 */

public final class B_StoreAssistantCollectPrint implements TempletAfterProvideListener
{
  /**
   * ����ģ��򿪺���¼����õķ���
   * @param request WEB����
   * @param templet ����ģ�����
   * @param context ���������Ķ���
   * @param response ģ��װ�ص���Ӧ����
   */
  public void templetAfterProvide(ServletRequest request, TempletData templet, ContextData context, TempletProvideResponse response)
  {
    //����LoginBean
    HttpServletRequest req = (HttpServletRequest)request;
    ReportData td = (ReportData)req.getAttribute("dzd");
    context.addReportData(td);
  }
}
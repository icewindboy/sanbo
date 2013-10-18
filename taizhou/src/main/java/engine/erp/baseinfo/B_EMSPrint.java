package engine.erp.baseinfo;

import java.util.*;
import engine.html.*;
import engine.dataset.*;
import engine.project.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadedListener;
import engine.report.event.TempletAfterProvideListener;
import engine.report.event.TempletProvideResponse;
import engine.report.util.ReportData;

public final class B_EMSPrint implements TempletAfterProvideListener
{
  /**
   * 得到信封的寄件人信息
   * @return 信封的寄件人信息
   */
  public synchronized static RowMap getEnvelopFromInfo(){
    EngineDataSet temp = new EngineDataSet();
    temp.setProvider(new EngineDataSetProvider());
    temp.setQueryString("SELECT mc, dept_addr, dept_phone FROM bm where deptid=0");
    temp.openDataSet();
    RowMap row = new RowMap();
    if(temp.getRowCount() > 0)
      row.put(temp);
    return row;
  }

  /**
   * 报表模板打开后的事件调用的方法
   * @param request WEB请求
   * @param templet 报表模板对象
   * @param context 报表上下文对象
   * @param response 模板装载的响应对象
   */
  public void templetAfterProvide(ServletRequest request, TempletData templet, ContextData context, TempletProvideResponse response)
  {
    RowMap row = new RowMap();
    row.put(request);
    String dwmc = row.get("dwmc");
    String jjr = row.get("jjr");
    ReportData repData = new ReportData();
    repData.addReportData("ds", row);
    context.addReportData(repData);
  }
}
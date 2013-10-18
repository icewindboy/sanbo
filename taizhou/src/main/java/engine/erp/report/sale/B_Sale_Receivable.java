package engine.erp.report.sale;
import engine.action.BaseAction;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.report.util.HtmlTable;
import engine.report.util.Tag;
import engine.dataset.EngineDataSet;
import engine.dataset.sql.QueryWhere;
import engine.report.event.ReportDataLoadingListener;
import engine.report.event.ReportDataLoadedListener;
/**
 * <p>Title: 销售-应收帐款明细表
 * <p>Description: 应收帐款明细表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company:engine </p>
 * @version 1.0
 */
public final class  B_Sale_Receivable  implements ReportDataLoadedListener
{
  /**
   *
   * @param req
   * @param templet
   * @param context页面显示控件类
   * @param dsRep数据集
   * @param where条件
   */
  public void dataLoaded(javax.servlet.ServletRequest request, TempletData templet,
                         ContextData context,EngineDataSet dsRep)
  {
    double startsum=0;//期初余额
    dsRep.first();
    for(int i=0;i<dsRep.getRowCount();i++)
    {
      if(i==0)
      {
        startsum = dsRep.getValue("ye").length() > 0 ? Double.parseDouble(dsRep.getValue("ye")) : 0;//期初余额
      }
      else
      {
        double jf=dsRep.getValue("jf").length() > 0 ? Double.parseDouble(dsRep.getValue("jf")) : 0; //借方
        double df=dsRep.getValue("df").length() > 0 ? Double.parseDouble(dsRep.getValue("df")) : 0 ;//贷方
        startsum=startsum+jf-df;
        dsRep.setValue("ye",String.valueOf(startsum));
        dsRep.post();
      }
      dsRep.next();
    }
  }
}
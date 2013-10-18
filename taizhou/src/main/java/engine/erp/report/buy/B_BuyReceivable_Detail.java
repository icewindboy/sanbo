package engine.erp.report.buy;

import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadedListener;
import engine.dataset.sql.QueryWhere;

/**
 * <p>Title: 采购应付帐款明细帐</p>
 * <p>Description: 采购应付帐款明细帐</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public class B_BuyReceivable_Detail implements ReportDataLoadedListener
{

  public B_BuyReceivable_Detail()
  {
  }
  public void dataLoaded(ServletRequest parm1, TempletData parm2, ContextData parm3, EngineDataSet parm4)
  {
    int row = parm4.getRowCount();
    double total=0;
    for(int i=0; i<row; i++)
    {
      parm4.goToRow(i);
      String zy = parm4.getValue("zy");
      double xf = parm4.getValue("xf").length()>0 ? Double.parseDouble(parm4.getValue("xf")) : 0;//应付
      double yf = parm4.getValue("yf").length()>0 ? Double.parseDouble(parm4.getValue("yf")) : 0;//已付
      double ye = parm4.getValue("ye").length()>0 ? Double.parseDouble(parm4.getValue("ye")) : 0;//余额
      if(zy.equals("期初余额"))
        total = ye;
      if(zy.equals("结算") || zy.equals("采购预付款"))
        total = total - yf;
      if(!zy.equals("期初余额") && !zy.equals("结算") && !zy.equals("采购预付款"))
        total = total + xf;
      parm4.setValue("ye", String.valueOf(total));
    }
    /**@todo Implement this engine.report.event.ReportDataLoadedListener method*/
    //throw new java.lang.UnsupportedOperationException("Method dataLoaded() not yet implemented.");
  }
}



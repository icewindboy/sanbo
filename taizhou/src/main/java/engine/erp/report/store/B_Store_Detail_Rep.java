package engine.erp.report.store;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.project.*;
import engine.html.*;
import engine.common.*;
import java.math.BigDecimal;

import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadedListener;

/**
 * <p>Title:  存货明细帐</p>
 * <p>Description: 存货明细帐</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public class B_Store_Detail_Rep implements ReportDataLoadedListener
{

  public B_Store_Detail_Rep()
  {
  }
  public void dataLoaded(ServletRequest parm1, TempletData parm2, ContextData parm3, EngineDataSet parm4)
  {
    int row = parm4.getRowCount();
    BigDecimal totalSL= new BigDecimal(0);
    BigDecimal totalHSSL = new BigDecimal(0);
    BigDecimal totalDJ=new BigDecimal(0);
    BigDecimal totalJE=new BigDecimal(0);
    for(int i=0; i<row; i++)
    {
      parm4.goToRow(i);
      String zy = parm4.getValue("zy");
      BigDecimal jcsl = parm4.getValue("jcsl").length()>0 ? new BigDecimal(parm4.getValue("jcsl")) : new BigDecimal(0);//结存数量
      BigDecimal srsl = parm4.getValue("srsl").length()>0 ? new BigDecimal(parm4.getValue("srsl")) : new BigDecimal(0);//收入数量
      BigDecimal fcsl = parm4.getValue("fcsl").length()>0 ? new BigDecimal(parm4.getValue("fcsl")) : new BigDecimal(0);//发出数量
      BigDecimal jchssl = parm4.getValue("jchssl").length()>0 ? new BigDecimal(parm4.getValue("jchssl")) : new BigDecimal(0);//结存数量
      BigDecimal srhssl = parm4.getValue("srhssl").length()>0 ? new BigDecimal(parm4.getValue("srhssl")) : new BigDecimal(0);//收入数量
      BigDecimal fchssl = parm4.getValue("fchssl").length()>0 ? new BigDecimal(parm4.getValue("fchssl")) : new BigDecimal(0);//发出数量
      //BigDecimal jcje = parm4.getValue("jcje").length()>0 ? new BigDecimal(parm4.getValue("jcje")) : new BigDecimal(0);//结存金额
      //BigDecimal srje = parm4.getValue("srje").length()>0 ? new BigDecimal(parm4.getValue("srje")) : new BigDecimal(0);//收入金额
      //BigDecimal fcje = parm4.getValue("fcje").length()>0 ? new BigDecimal(parm4.getValue("fcje")) : new BigDecimal(0);//发出余额
      if(zy.equals("期初余额")){
        totalSL = jcsl;
        totalHSSL = jchssl;
      }
      if(zy.equals("入库") || zy.equals("损益")){
        totalSL = totalSL.add(srsl);
        totalHSSL = totalHSSL.add(srhssl);
        //totalJE = totalJE.add(srje);
        //totalDJ = totalSL.compareTo(new BigDecimal(0))==0 ? new BigDecimal(0) : totalJE.divide(totalSL,6, BigDecimal.ROUND_HALF_UP);
      }
      if(zy.equals("出库")){
        totalSL = totalSL.subtract(fcsl);
        totalHSSL = totalHSSL.subtract(fchssl);
        //totalJE = totalJE.subtract(fcje);
        //totalDJ = totalSL.compareTo(new BigDecimal(0))==0 ? new BigDecimal(0) : totalJE.divide(totalSL,6, BigDecimal.ROUND_HALF_UP);
      }
      parm4.setValue("jcsl",totalSL.toString());
      parm4.setValue("jchssl", totalHSSL.toString());
      //parm4.setValue("jcdj", totalDJ.toString());
      //parm4.setValue("jcje", totalJE.toString());
    }
    /**@todo Implement this engine.report.event.ReportDataLoadedListener method*/
    //throw new java.lang.UnsupportedOperationException("Method dataLoaded() not yet implemented.");
  }
}



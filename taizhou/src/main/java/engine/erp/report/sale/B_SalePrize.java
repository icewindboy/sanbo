package engine.erp.report.sale;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadedListener;
import engine.dataset.sql.QueryWhere;
/**
 * <p>Title: 销售应付帐款汇总帐</p>
 * <p>Description: 销售应付帐款汇总帐</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @version 1.0
 */
public class B_SalePrize implements ReportDataLoadedListener
{

  public B_SalePrize()
  {
  }
  public void dataLoaded(ServletRequest parm1, TempletData parm2, ContextData parm3, EngineDataSet parm4)
  {
    QueryWhere where = parm3.getQueryWhere();
    String ksrq = where.getWhereValue("a$rq$a");//查询条件开始日期
    String jsrq = where.getWhereValue("a$rq$b");//查询条件结束日期
    String djxz = where.getWhereValue("a$djxz");//查询条件数据来源
    //String personid = where.getWhereValue("a$personid");//查询条件数据来源
    Object[] objects = parm2.getTablesAndOther();
    for(int i=0; i<objects.length; i++)
    {
      if(objects[i] instanceof String)
      {
        String s = (String)objects[i];
        if(s.startsWith("<SCRIPT LANGUAGE='javascript' id='where'>"))
        {
          s = "<SCRIPT LANGUAGE='javascript' id='where'>var ksrq='"+ksrq+"'; var jsrq='"+jsrq+"'; var djxz='"+djxz+"'; </SCRIPT>";
          parm2.setOther(i, s);
        }
      }
    }
  }
}



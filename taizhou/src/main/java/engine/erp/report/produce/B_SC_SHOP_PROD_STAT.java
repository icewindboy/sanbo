package engine.erp.report.produce;

import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadedListener;
import engine.dataset.sql.QueryWhere;

/**
 * <p>Title: 车间产品完成情况表-得到查询条件</p>
 * <p>Description: 车间产品完成情况表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public class B_SC_SHOP_PROD_STAT implements ReportDataLoadedListener
{

  public B_SC_SHOP_PROD_STAT()
  {
  }
  public void dataLoaded(ServletRequest parm1, TempletData parm2, ContextData parm3, EngineDataSet parm4)
  {
    QueryWhere where = parm3.getQueryWhere();
    String ksrq = where.getWhereValue("a$rq$a");//查询条件开始日期
    String jsrq = where.getWhereValue("a$rq$b");//查询条件结束日期
    String deptid = where.getWhereValue("a$deptid");//查询条件部门
    Object[] objects = parm2.getTablesAndOther();
    for(int i=0; i<objects.length; i++)
    {
      if(objects[i] instanceof String)
      {
        String s = (String)objects[i];
        if(s.startsWith("<SCRIPT LANGUAGE='javascript' id='where'>"))
        {
          s = "<SCRIPT LANGUAGE='javascript' id='where'>var ksrq='"+ksrq+"'; var jsrq='"+jsrq+"'; var deptid='"+deptid+"';</SCRIPT>";
          parm2.setOther(i, s);
        }
      }
    }
  }
}



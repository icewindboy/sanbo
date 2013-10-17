package engine.erp.report.store;

import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadedListener;
import engine.dataset.sql.QueryWhere;

/**
 * <p>Title: 库存收发单据汇总表</p>
 * <p>Description: 库存收发单据汇总表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public class B_KC_BILL_INOUT_COLLECT implements ReportDataLoadedListener
{
  public B_KC_BILL_INOUT_COLLECT()
  {
  }
  public void dataLoaded(ServletRequest parm1, TempletData parm2, ContextData parm3, EngineDataSet parm4)
  {
    QueryWhere where = parm3.getQueryWhere();
    String ksrq = where.getWhereValue("a$rq$a");//查询条件开始日期
    String jsrq = where.getWhereValue("a$rq$b");//查询条件结束日期
    String djxz = where.getWhereValue("a$djxz");//查询条件单据性质
    String storeid = where.getWhereValue("a$storeid");//查询条件仓库
    String deptid = where.getWhereValue("a$deptid");//查询条件部门
    String dwtxid = where.getWhereValue("a$dwtxid");//查询条件往来单位
    String jsr = where.getWhereValue("a$jsr");//查询条件经手人
    String zt = where.getWhereValue("a$zt");//查询条件状态
    Object[] objects = parm2.getTablesAndOther();
    for(int i=0; i<objects.length; i++)
    {
      if(objects[i] instanceof String)
      {
        String s = (String)objects[i];
        if(s.startsWith("<SCRIPT LANGUAGE='javascript' id='where'>"))
        {
          s = "<SCRIPT LANGUAGE='javascript' id='where'>var ksrq='"+ksrq+"'; var jsrq='"+jsrq+"'; var storeid='"+storeid+"'; var djxz='"+djxz+"';"
            + " var deptid='"+deptid+"'; var dwtxid='"+dwtxid+"'; var jsr='"+jsr+"'; var zt='"+zt+"';</SCRIPT>";
          parm2.setOther(i, s);
        }
      }
    }
  }
}



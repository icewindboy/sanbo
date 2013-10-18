package engine.erp.report.jit;



import java.util.Hashtable;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadingListener;
import engine.dataset.sql.QueryWhere;
import engine.util.MessageFormat;

/**
 * <p>Title: 生产:工人工资汇总 </p>
 * <p>Description: 生产:工人工资汇总 </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @version 1.0
 */

public class B_WorkWageCollect implements ReportDataLoadingListener
{
  private Hashtable fieldTable = new Hashtable();
  //数据来源表示提单
  private static final String MASTER_SQL    =
      "select * from ("
      + " SELECT xm,mc,deptid,personid,gzzid,gzzmc,SUM(nvl(work_wage,0))work_wage, "
      + "  SUM(nvl(over_wage,0))over_wage, SUM(nvl(night_wage,0))night_wage, SUM(nvl(bounty,0))bounty, "
      + "  SUM(nvl(amerce,0))amerce, SUM(nvl(over_hour,0))over_hour, SUM(nvl(night_hour,0))night_hour,"
      + "  SUM(nvl(hour_wage,0))hour_wage,SUM(nvl(piece_wage,0))piece_wage,SUM(nvl(total,0))total,null bz"
      + "  FROM VW_SC_WORKLOAD t WHERE 1=1 {startdate} {enddate} {mc} {gzzmc} {xm} {pid} GROUP BY xm,mc,deptid,personid,gzzid,gzzmc ORDER BY deptid"
      + ")t {advance} ";
  /**
   * 报表数据打开时的事件调用的方法。1:组装SQL语句，提取数据
   * @param request WEB请求
   * @param templet 报表模板对象
   * @param context 报表上下文对象
   * @param dsRep   报表数据对象对象
   * @param whereQuery 查询条件的所有值
   */
  public void dataLoading(ServletRequest request, TempletData templet, ContextData context,
                          EngineDataSet dsRep, QueryWhere whereQuery)
  {

    String startdate = whereQuery.getWhereValue("a$ksrq");
    String enddate = whereQuery.getWhereValue("a$jsrq");

    String mc = whereQuery.getWhereValue("a$mc");
    String xm = whereQuery.getWhereValue("a$xm");
    String gzzmc = whereQuery.getWhereValue("a$gzzmc");
    //String fgsid = whereQuery.getWhereValue("a$fgsid");
    String pid = whereQuery.getWhereValue("a$pid");

    if(xm.length() > 0)
      xm = "AND t.xm like '%"+xm+"%'";
    if(mc.length() > 0)
      mc = " AND t.mc like '%"+mc+"%'";
    if(gzzmc.length() > 0)
      gzzmc = " AND t.gzzmc like '%"+gzzmc+"%'";

    String sttdrq ="";
    String edtdrq ="";
    sttdrq=startdate;
    edtdrq=enddate;

    if(sttdrq.length() > 0)
      startdate = " and t.rq >= to_date('"+sttdrq+"', 'YYYY-MM-DD')";
    if(edtdrq.length() > 0)
      enddate  = " and t.rq <= to_date('"+edtdrq+"', 'YYYY-MM-DD')";


    if(pid.length() > 0)
      pid = " AND t.deptid in (select q.deptid from jc_bmqx q where q.personid='"+pid+"' )";


    fieldTable.clear();
    fieldTable.put("startdate", startdate);
    fieldTable.put("enddate", enddate);

    fieldTable.put("xm", xm);

    fieldTable.put("mc", mc);
    fieldTable.put("gzzmc", gzzmc);
    fieldTable.put("pid", pid);


    StringBuffer buf = new StringBuffer();

    String advance = context.getAdvanceWhere().getWhereQuery();
    if(advance.length()>0)
      buf.append(buf.length() > 0 ? "AND " : " WHERE").append(advance);
    fieldTable.put("advance", buf.toString());


    String sql = MessageFormat.format( MASTER_SQL, fieldTable);

    dsRep.setQueryString(sql);
    if(dsRep.isOpen())
      dsRep.refresh();
    else
      dsRep.openDataSet();

  }
}

package engine.erp.report.store;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;
import java.math.BigDecimal;
import engine.report.util.*;
import engine.util.MessageFormat;
import engine.util.StringUtils;
import engine.dataset.EngineDataSet;
import engine.dataset.sql.QueryWhere;
import engine.report.event.ReportDataLoadingListener;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 江海岛
 * @version 1.0
 */

public class BatchnoInoutDetail implements ReportDataLoadingListener
{
  private Hashtable fieldTable = new Hashtable();

  //提取包含未记帐的存货收发明细
  private final static String BATCHNO_DETAIL_SQL
      = "SELECT * FROM ("
      + "  SELECT to_date('{startdate}', 'YYYY-MM-DD') sfrq , NULL djxz, '' djmc, '期初数量' djh,"
      + "         0 srsl, 0 fcsl, (SUM(nvl(a.srsl,0))-SUM(nvl(a.fcsl,0))) jcsl, NULL id"
      + "  FROM   vw_kc_storebill a"
      + "  WHERE  a.sfrq < to_date('{startdate}', 'YYYY-MM-DD') AND {ph} AND {dmsxid}"
      + "  AND    a.cpid='{cpid}' AND  a.storeid='{storeid}' AND a.fgsid='{fgsid}'"
      // --提取收入与发出的数量
      + "  UNION ALL"
      + "  SELECT a.sfrq, a.djxz, a.djmc, a.sfdjdh djh, a.srsl, a.fcsl, NULL jcsl, a.sfdjid"
      + "  FROM   vw_kc_storebill a"
      + "  WHERE  a.sfrq >= to_date('{startdate}', 'YYYY-MM-DD')"
      + "  AND    a.sfrq <= to_date('{enddate}', 'YYYY-MM-DD') AND {ph} AND {dmsxid}"
      + "  AND    a.cpid='{cpid}' AND  a.storeid='{storeid}' AND a.fgsid='{fgsid}'"
      + ") a "
      + "ORDER BY sfrq";

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
    String storeid = whereQuery.getWhereValue("a$storeid");
    String cpid = whereQuery.getWhereValue("a$cpid");
    String dmsxid = whereQuery.getWhereValue("a$dmsxid");
    String ph = whereQuery.getWhereValue("a$ph");
    String fgsid = whereQuery.getWhereValue("a$fgsid");
    ph = ph.length() > 0 ? "a.ph='"+ ph +"'" : "a.ph IS NULL";
    dmsxid = dmsxid.length() > 0 ? "a.dmsxid='"+ dmsxid +"'" : "a.dmsxid IS NULL";

    fieldTable.clear();
    fieldTable.put("startdate", startdate);
    fieldTable.put("enddate", enddate);
    fieldTable.put("storeid", storeid);
    fieldTable.put("cpid", cpid);
    fieldTable.put("dmsxid", dmsxid);
    fieldTable.put("ph", ph);
    fieldTable.put("fgsid", fgsid);
    String sql = MessageFormat.format(BATCHNO_DETAIL_SQL, fieldTable);
    dsRep.setQueryString(sql);
    if(dsRep.isOpen())
      dsRep.refresh();
    else
      dsRep.openDataSet();

    if(dsRep.getRowCount() == 0)
      return;

    //计算数据集的结存数量
    dsRep.first();
    BigDecimal jcsl = dsRep.getBigDecimal("jcsl");
    while(dsRep.next())
    {
      jcsl = jcsl.add(dsRep.getBigDecimal("srsl")).subtract(dsRep.getBigDecimal("fcsl"));
      dsRep.setBigDecimal("jcsl", jcsl);
      dsRep.post();
    }
  }
}
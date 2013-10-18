package engine.erp.report.store;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;
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

public class BatchnoInoutCollect implements ReportDataLoadingListener
{
  private static Hashtable groupTable = new Hashtable();
  //分组字段的常量
  //private static final String GROUP_BY =
      //"a.cpbm, a.product, a.cpid, a.sxz, a.jldw, b.storeid, b.ckmc, a.ph,";
  static{
    groupTable.put("cpbm", "a.cpbm");
    groupTable.put("product", "a.product");
    groupTable.put("sxz", "a.dmsxid, a.sxz");
    groupTable.put("jldw", "a.jldw");
    groupTable.put("hsdw", "a.hsdw");
    groupTable.put("ckmc", "b.storeid, b.ckmc");
    groupTable.put("ph", "a.ph");
  }

  private EngineDataSet dsPropData = null;

  private Hashtable fieldTable = new Hashtable();

  //类别属性
  private static final String SPEC_PROPERTY_SQL
      = "SELECT a.dlsxID, a.sxmc FROM kc_dmlbsx a WHERE a.wzlbid='@'";
  //提取包含未记帐的存货收发明细
  private final static String BATCHNO_COLLECT_SQL
      = "SELECT * FROM ( "
      + " SELECT {COLUMN} SUM(a.qcsl) qcsl, SUM(a.qchssl) qchssl, "
      + "       SUM(a.srsl) srsl, SUM(a.fcsl) fcsl, SUM(a.srhssl) srhssl, SUM(a.fchssl) fchssl,"
      + "       (SUM(a.qcsl)+SUM(a.srsl)-SUM(a.fcsl)) jcsl , (SUM(a.qchssl)+SUM(a.srhssl)-SUM(a.fchssl)) jchssl "
      + " FROM ("
      // --得到期初结存
      + "  SELECT c.storeid, c.cpid, c.cpbm, c.product, a.dmsxid, e.sxz, c.jldw, c.hsdw, a.ph,"
      + "      (SUM(nvl(a.srsl,0))-SUM(nvl(a.fcsl,0))) qcsl, "
      + "      (SUM(nvl(a.srhssl,0))-SUM(nvl(a.fchssl,0))) qchssl, "
      + "      0 srsl, 0 fcsl, 0 srhssl, 0 fchssl "
      + "  FROM   vw_kc_storebill a, vw_kc_dm c, kc_chlb d, kc_dmsx e"
      + "  WHERE  a.dmsxid=e.dmsxid(+) AND a.cpid=c.cpid  AND c.chlbid=d.chlbid"
      + "  AND    a.sfrq < to_date('{startdate}', 'YYYY-MM-DD')"
      + "  {sxz} {ph} {cpbm} {wzlbid} {storeid} {chlbid}"
      + "  AND    a.fgsid={fgsid}"
      + "  GROUP BY c.storeid, c.cpid, c.cpbm, c.product, a.dmsxid, e.sxz, c.jldw, c.hsdw, a.ph"
      // --提取收入与发出的数量
      + "  UNION ALL"
      + "  SELECT c.storeid, c.cpid, c.cpbm, c.product, a.dmsxid, e.sxz, c.jldw, c.hsdw, a.ph,"
      + "         0 qcsl, 0 qchssl, "
      + "         SUM(nvl(a.srsl,0)) srsl, SUM(nvl(a.fcsl,0)) fcsl, "
      + "         SUM(nvl(a.srhssl,0)) srhssl, SUM(nvl(a.fchssl,0)) fchssl "
      + "  FROM   vw_kc_storebill a, vw_kc_dm c, kc_chlb d, kc_dmsx e"
      + "  WHERE  a.dmsxid=e.dmsxid(+) AND a.cpid=c.cpid  AND c.chlbid=d.chlbid"
      + "  AND    a.sfrq >= to_date('{startdate}', 'YYYY-MM-DD')"
      + "  AND    a.sfrq <= to_date('{enddate}', 'YYYY-MM-DD')"
      + "  {sxz} {ph} {cpbm} {wzlbid} {storeid} {chlbid}"
      + "  AND    a.fgsid={fgsid}"
      + "  GROUP BY c.storeid, c.cpid, c.cpbm, c.product, a.dmsxid, e.sxz, c.jldw, c.hsdw, a.ph"
      + " ) a, kc_ck b WHERE a.storeid = b.storeid {GROUP BY} "
      + ") T {advance} ";
      //+ "GROUP BY a.cpbm, a.product, a.cpid, a.sxz, a.jldw, b.storeid, b.ckmc, a.ph";

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
    String chlbid = whereQuery.getWhereValue("a$chlbid");
    String wzlbid = whereQuery.getWhereValue("a$wzlbid");
    String tmpWzlbid = wzlbid;
    String cpbm = whereQuery.getWhereValue("a$cpbm");
    String ph = whereQuery.getWhereValue("a$ph");
    String fgsid = whereQuery.getWhereValue("a$fgsid");
    if(storeid.length() > 0)
      storeid = "AND a.storeid="+storeid;
    if(chlbid.length() > 0)
      chlbid = "AND c.chlbid="+chlbid;
    if(cpbm.length() > 0)
      cpbm = "AND c.cpbm LIKE'"+ cpbm +"%'";
    if(ph.length() > 0)
      ph = "AND upper(a.ph) LIKE '%"+ ph.toUpperCase()+"%'";

    String sxz = "";
    if(wzlbid.length() > 0)
    {
      //whereQuery.putWhereValue("a$wzlbid", "");
      wzlbid = " AND ( c.wzlbid = " + wzlbid + "  or c.wzlbid in ( select k.wzlbid from kc_dmlb k where k.parentid=" + wzlbid + " ) )";
      if(dsPropData == null)
      {
        dsPropData =  new EngineDataSet();
        dsPropData.setProvider(context.getDataSetProvider());
      }
      sxz = getPropertyQuery(request, whereQuery, tmpWzlbid);
    }

    fieldTable.clear();
    fieldTable.put("startdate", startdate);
    fieldTable.put("enddate", enddate);
    fieldTable.put("storeid", storeid);
    fieldTable.put("chlbid", chlbid);
    fieldTable.put("wzlbid", wzlbid);
    fieldTable.put("cpbm", cpbm);
    fieldTable.put("ph", ph);
    fieldTable.put("sxz", sxz);
    fieldTable.put("fgsid", fgsid);
    String advance = context.getAdvanceWhere().getWhereQuery();
    fieldTable.put("advance", advance.length()>0 ? " WHERE "+advance : "");
    //分组的字段
    String[] columns = context.getShowColumns();
    boolean notCpid = true;
    StringBuffer buf = new StringBuffer();
    for(int i=0; i<columns.length; i++)
    {
      String key = columns[i].toLowerCase();
      String value = (String)groupTable.get(columns[i]);
      if(value == null)
        continue;
      buf.append(value).append(",");
      if(notCpid && (key.equals("cpbm") || key.equals("product")))
      {
        buf.append("a.cpid,");
        notCpid = false;
      }
    }
    fieldTable.put("COLUMN", buf.toString());

    String groupby = "";
    if(buf.length() > 0)
    {
      buf.setLength(buf.length()-1);
      groupby = "GROUP BY " + buf.toString();
    }
    fieldTable.put("GROUP BY", groupby);

    String sql = MessageFormat.format(BATCHNO_COLLECT_SQL, fieldTable);
    dsRep.setQueryString(sql);
    if(dsRep.isOpen())
      dsRep.refresh();
    else
      dsRep.openDataSet();
  }

  private String getPropertyQuery(ServletRequest request, QueryWhere whereQuery, String wzlbid)
  {
    StringBuffer buf = new StringBuffer();
    String sql = StringUtils.combine(SPEC_PROPERTY_SQL, "@", new String[]{wzlbid});
    if(!dsPropData.isOpen())
    {
      dsPropData.setQueryString(sql);
      dsPropData.open();
    }
    else if(!sql.equals(dsPropData.getQueryString()))
    {
      dsPropData.setQueryString(sql);
      dsPropData.refresh();
    }

    dsPropData.first();
    for(int i=0; i<dsPropData.getRowCount(); i++)
    {
      String sxzKey = "sx_" + dsPropData.getValue("dlsxid");
      String sxzValue = whereQuery.getWhereValue(sxzKey);
      if(sxzValue != null && sxzValue.length() > 0)
      {
        buf.append("AND upper(e.sxz) LIKE '%").append(dsPropData.getValue("sxmc"));
        buf.append("(").append(sxzValue.toUpperCase()).append(")%' ");
        whereQuery.putWhereValue(sxzKey, "");
      }
      dsPropData.next();
    }
    return buf.toString();
  }
}

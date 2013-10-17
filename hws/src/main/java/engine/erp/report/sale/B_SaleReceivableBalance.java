package engine.erp.report.sale;

import java.util.Hashtable;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadingListener;
import engine.dataset.sql.QueryWhere;
import engine.util.MessageFormat;

/**
 * <p>Title: 销售应收帐款余额</p>
 * <p>Description: 销售应收帐款余额</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @version 1.0
 */

public class B_SaleReceivableBalance implements ReportDataLoadingListener
{
  private Hashtable fieldTable = new Hashtable();
  //数据来源表示提单
  private final static String LADING_RECEIVABLE_SQL
  = "SELECT * FROM ("
  + "  SELECT areacode,dqmc, dwmc,dwdm,dwtxid,khlx, sum(nvl(qichuye,0))qichuye, sum(nvl(zjf,0))zjf,sum(nvl(zdf,0))zdf,sum(nvl(qichuye,0)+nvl(zjf,0)-nvl(zdf,0))qimeye,sum(nvl(zsl,0))zsl "
  + "  FROM "
  + "  ( "
  //    --得到期初余额(始)
  + "    SELECT areacode,dqmc, dwmc,dwdm,dwtxid,khlx,(sum(nvl(jf,0))-sum(nvl(df,0)))qichuye, NULL zjf, NULL zdf, NULL qimeye,NULL zsl "
  + "    FROM "
  + "    ( "
  + "      SELECT q.areacode,q.dqmc,c.dwmc,c.dwdm,t.dwtxid,t.khlx,NULL qichuye,SUM(nvl(w.jje,0))jf,NULL df,NULL ysk,NULL zsl" //--借金额SELECT q.areacode,q.dqmc,c.dwmc,c.dwdm,t.dwtxid,NULL qichuye,SUM(nvl(t.zje,0))jf,NULL df,NULL ysk,NULL zsl
  + "      FROM xs_td t,xs_tdhw w, dwtx c, dwdq q"
  + "      WHERE t.dwtxid=c.dwtxid and t.tdid=w.tdid AND c.dqh=q.dqh {dwtxid} {personid} {areacode} {khlx} {tdzt} "//(p_tdcheck_in = '1' OR t.zt IN(1,2,8))"
  + "      AND t.tdrq < to_date('{startdate}', 'YYYY-MM-DD')  "//--2004-5-13 初始化提单不计入应收款"
  + "      AND t.fgsid={fgsid}"
  + "      GROUP BY c.dwmc,t.dwtxid,t.khlx,c.dwdm,q.areacode,q.dqmc"
  + "      UNION ALL"
  + "      SELECT q.areacode,q.dqmc,c.dwmc,c.dwdm,t.dwtxid,t.khlx,NULL qichuye,NULL jf,SUM(nvl(t.je,0)*t.djxz)df,NULL ysk,NULL zsl"//--贷金额"
  + "      FROM cw_xsjs t,dwtx c, dwdq q"
  + "      WHERE t.dwtxid=c.dwtxid AND c.dqh=q.dqh {dwtxid} {personid} {areacode} {khlx} {jszt}   "
  + "      AND t.rq < to_date('{startdate}', 'YYYY-MM-DD') AND t.fgsid={fgsid}"
  //--得到期初余额
  + "      GROUP BY c.dwmc,t.dwtxid,t.khlx,c.dwdm,q.areacode,q.dqmc"
  //+ "      UNION ALL"
  //+ "      SELECT q.areacode,q.dqmc,c.dwmc,c.dwdm,t.dwtxid,t.khlx,NULL qichuye,NULL jf,NULL df,sum(nvl(t.ysk,0)) ysk,NULL zsl"//--应收款"
  //+ "      FROM xs_ysk t,dwtx c,dwdq q"
  //+ "      WHERE t.dwtxid=c.dwtxid AND c.dqh=q.dqh {dwtxid} {yskpersonid} {areacode} {khlx} "
  //+ "      AND t.fgsid={fgsid}"
  //+ "      GROUP BY c.dwmc,t.dwtxid,t.khlx,c.dwdm,q.areacode,q.dqmc"
  + "    )e"
  + "    GROUP BY dwmc,dwtxid, khlx,dwdm,areacode,dqmc"
  // -----得到期初余额(未)
  //--得到本时间段的借金额与贷金额
  + "    UNION ALL"
  + "    SELECT areacode,dqmc, dwmc,dwdm,dwtxid,khlx,NULL qichuye,SUM(nvl(njf,0))zjf,SUM(nvl(ndf,0))zdf,NULL qimeye,SUM(nvl(zsl,0))zsl"
  + "    FROM"
  + "    ("
  + "       SELECT q.areacode,q.dqmc, c.dwmc,c.dwdm,t.dwtxid,t.khlx,SUM(nvl(w.jje,0))njf,NULL ndf,SUM(nvl(w.sl,0))zsl"//--借金额
  + "       FROM xs_td t,xs_tdhw w, dwtx c, dwdq q"
  + "       WHERE t.dwtxid=c.dwtxid and t.tdid=w.tdid AND c.dqh=q.dqh {dwtxid} {personid} {areacode} {khlx} {tdzt} "//(p_tdcheck_in = '1' OR t.zt IN(1,2,8))"
  + "       AND t.tdrq >= to_date('{startdate}', 'YYYY-MM-DD') AND t.tdrq <= to_date('{enddate}', 'YYYY-MM-DD')"
  + "       AND t.fgsid={fgsid}"//--2004-5-13 初始化提单不计入应收款"
  + "       GROUP BY c.dwmc,t.dwtxid,t.khlx,c.dwdm,q.areacode,q.dqmc"
  + "       UNION ALL"
  + "       SELECT q.areacode,q.dqmc, c.dwmc,c.dwdm,t.dwtxid,t.khlx,NULL njf, SUM(nvl(t.je,0)*t.djxz)ndf,NULL zsl"//--贷金额
  + "       FROM cw_xsjs t, dwtx c, dwdq q"
  + "       WHERE t.dwtxid=c.dwtxid AND c.dqh=q.dqh {dwtxid} {personid} {areacode} {khlx}  {jszt}   "//(p_jscheck_in = '1' OR (l.zt = 1 OR l.zt = 8))"
  + "       AND t.rq >= to_date('{startdate}', 'YYYY-MM-DD') AND t.rq <= to_date('{enddate}', 'YYYY-MM-DD')"
  + "       AND t.fgsid={fgsid}"//--2004-5-13 初始化提单不计入应收款"
  + "       GROUP BY c.dwmc,t.dwtxid,t.khlx,c.dwdm,q.areacode,q.dqmc"
  + "    )h"
  + "    GROUP BY dwmc,dwtxid,khlx,dwdm,areacode,dqmc"
  + "  )p"
  + "  GROUP BY dwmc,dwtxid,khlx,dwdm,areacode,dqmc "
  + ")t {advance} ";//p_condition_in=1 OR (t.zjf <> 0 OR t.zdf<>0 OR qimeye<>0)";

  //数据来源表示发票
  private final static String INVOICE_RECEIVABLE_SQL
  = "SELECT * FROM ( "
  + "  SELECT areacode,dqmc,dwmc,dwdm,dwtxid,null khlx, sum(nvl(qichuye,0))qichuye, sum(nvl(zjf,0))zjf,sum(nvl(zdf,0))zdf,sum(nvl(qichuye,0)+nvl(zjf,0)-nvl(zdf,0))qimeye,SUM(nvl(zsl,0))zsl"
  + "  FROM"
  + "  ("
  //    --得到发票期初余额
  + "    SELECT areacode,dqmc, dwmc,dwdm,dwtxid,null khlx,sum(nvl(jf,0)-nvl(df,0)+nvl(ysk,0))qichuye, NULL zjf, NULL zdf, NULL qimeye,SUM(nvl(zsl,0))zsl"
  + "    FROM"
  + "    ("
  + "      SELECT q.areacode,q.dqmc, c.dwmc,c.dwdm,t.dwtxid,null khlx,NULL qichuye,SUM(nvl(m.jshj,0))jf,NULL df,NULL ysk,SUM(nvl(m.sl,0))zsl"
  + "      FROM cw_xsfp t, cw_xsfpmx m, dwtx c, dwdq q"
  + "      WHERE t.xsfpid=m.xsfpid AND t.dwtxid=c.dwtxid AND c.dqh=q.dqh {dwtxid} {personid} {areacode} {fpzt} "//(p_tdcheck_in = '1' OR t.zt = 1)"
  + "      AND t.kprq < to_date('{startdate}', 'YYYY-MM-DD')"
  + "      AND t.fgsid={fgsid}"
  + "      GROUP BY c.dwmc,t.dwtxid,c.dwdm,q.areacode,q.dqmc"
  + "      UNION ALL"
  + "      SELECT q.areacode,q.dqmc, c.dwmc,c.dwdm,t.dwtxid,null khlx,NULL qichuye,NULL jf,SUM(nvl(t.je,0)*t.djxz)df,NULL ysk,NULL zsl"
  + "      FROM cw_xsjs t, dwtx c, dwdq q"
  + "      WHERE t.dwtxid=c.dwtxid AND c.dqh=q.dqh {dwtxid} {personid} {areacode}  {jszt}    "//(p_jscheck_in = '1' OR (l.zt = 1 OR l.zt = 8))"
  + "      AND t.rq < to_date('{startdate}', 'YYYY-MM-DD')"
  + "      AND t.fgsid={fgsid}"
  + "      GROUP BY c.dwmc,t.dwtxid,c.dwdm,q.areacode,q.dqmc"
  //发票暂时不需要应收款的
  + "    )d"
  + "    GROUP BY dwmc,dwdm,dwtxid,areacode,dqmc"
  + "    UNION ALL"
  + "    SELECT areacode, dqmc, dwmc, dwdm, dwtxid, null khlx, NULL qichuye,SUM(nvl(njf,0))zjf,SUM(nvl(ndf,0))zdf,NULL qimeye,SUM(nvl(zsl,0))zsl"
  + "    FROM "//--得到本时间段的借金额与贷金额
  + "    ("
  //--销售发票
  + "      SELECT q.areacode,q.dqmc, c.dwmc,c.dwdm,t.dwtxid,null khlx,SUM(nvl(m.jshj,0))njf,NULL ndf,SUM(nvl(m.sl,0))zsl"//--借方
  + "      FROM cw_xsfp t ,cw_xsfpmx m, dwtx c, dwdq q"
  + "      WHERE t.xsfpid=m.xsfpid AND t.dwtxid=c.dwtxid AND c.dqh=q.dqh {dwtxid} {personid} {areacode} {fpzt} "//(p_tdcheck_in = '1' OR t.zt = 1)"
  + "      AND t.kprq >= to_date('{startdate}', 'YYYY-MM-DD') AND t.kprq <= to_date('{enddate}', 'YYYY-MM-DD')"
  + "      AND t.fgsid={fgsid}"
  + "      GROUP BY c.dwmc,t.dwtxid,c.dwdm,q.areacode,q.dqmc"
  //--销售结算
  + "      UNION ALL"
  + "      SELECT q.areacode,q.dqmc, c.dwmc,c.dwdm,t.dwtxid,null khlx,NULL njf, SUM(nvl(t.je,0)*t.djxz)ndf,NULL zsl"//--贷方
  + "      FROM cw_xsjs t, dwtx c, dwdq q"
  + "      WHERE t.dwtxid=c.dwtxid AND c.dqh=q.dqh {dwtxid} {personid} {areacode}  {jszt}    "//(p_jscheck_in = '1' OR (l.zt = 1 OR l.zt = 8))"
  + "      AND t.rq >= to_date('{startdate}', 'YYYY-MM-DD') AND t.rq <= to_date('{enddate}', 'YYYY-MM-DD')"
  + "      AND t.fgsid={fgsid}"
  + "      GROUP BY c.dwmc,t.dwtxid,c.dwdm,q.areacode,q.dqmc"
  + "    )d"
  + "    GROUP BY dwmc ,dwtxid,dwdm,areacode,dqmc"
  + "  ) b"
  + "  GROUP BY dwmc,dwtxid,dwdm,areacode,dqmc"
  + ")t {advance} ";//p_condition_in=1 OR (t.zjf <> 0 OR t.zdf<>0 OR qimeye<>0)";

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
    String fgsid = whereQuery.getWhereValue("a$fgsid");

    String dwtxid = whereQuery.getWhereValue("a$dwtxid");
    String personid = whereQuery.getWhereValue("a$personid");
    //应收款初始化用
    String yskpersonid = personid;
    String areacode = whereQuery.getWhereValue("a$areacode");
    String khlx = whereQuery.getWhereValue("a$khlx");
    String tdzt = whereQuery.getWhereValue("a$zt");
    String jszt = whereQuery.getWhereValue("a$jzt");
    String djxz = whereQuery.getWhereValue("a$djxz");
    String nokhlx = whereQuery.getWhereValue("a$nokhlx");
    //发票用
    String fpzt = "";
    if(dwtxid.length() > 0)
      dwtxid = "AND t.dwtxid="+dwtxid;
    if(personid.length() > 0)
      personid = "AND t.personid="+personid;
    if(yskpersonid.length() > 0)
      yskpersonid = " AND t.personid="+yskpersonid;
    if(areacode.length() > 0)
      areacode = " AND q.areacode='"+areacode+"'";
    if(khlx.length() > 0)
      khlx = " AND t.khlx='"+khlx+"'";
    if(tdzt.equals("0"))
      tdzt = " AND t.zt in (3,8) ";
    else
      tdzt="  AND t.zt in(2,3,8)";
    if(jszt.equals("0"))
     jszt = " AND t.zt in(1,8) ";
    else
      jszt=" and t.zt in(0,9,1,8) ";
    if(djxz.equals("2"))
      fpzt = " AND t.zt = 1 ";

    fieldTable.clear();
    fieldTable.put("startdate", startdate);
    fieldTable.put("enddate", enddate);
    fieldTable.put("fgsid", fgsid);
    fieldTable.put("dwtxid", dwtxid);
    fieldTable.put("personid", personid);
    fieldTable.put("yskpersonid", yskpersonid);
    fieldTable.put("areacode", areacode);
    fieldTable.put("khlx", khlx);
    if(djxz.equals("2"))
      fieldTable.put("fpzt", fpzt);
    else
      fieldTable.put("tdzt", tdzt);

    fieldTable.put("jszt", jszt);
    //
    StringBuffer buf = new StringBuffer();
    String condition = whereQuery.getWhereValue("a$sg");
    if(condition.equals("1"))
      buf.append("WHERE (t.zjf <> 0 OR t.zdf<>0 OR qimeye<>0)");
    String advance = context.getAdvanceWhere().getWhereQuery();
    if(advance.length()>0)
      buf.append(buf.length() > 0 ? "AND " : " WHERE").append(advance);
    fieldTable.put("advance", buf.toString());

    String SQL=LADING_RECEIVABLE_SQL;
    if(nokhlx.equals("1"))
      SQL = "select areacode,dqmc, dwmc,dwdm,dwtxid,sum(nvl(qichuye,0))qichuye, sum(nvl(zjf,0))zjf,sum(nvl(zdf,0))zdf,sum(nvl(qimeye,0))qimeye,sum(nvl(zsl,0))zsl from("+LADING_RECEIVABLE_SQL+") group by areacode,dqmc, dwmc,dwdm,dwtxid";

    boolean isLading = whereQuery.getWhereValue("a$djxz").equals("1");
    String sql = MessageFormat.format(isLading ? SQL : INVOICE_RECEIVABLE_SQL, fieldTable);
    dsRep.setQueryString(sql);
    if(dsRep.isOpen())
      dsRep.refresh();
    else
      dsRep.openDataSet();

  }
}



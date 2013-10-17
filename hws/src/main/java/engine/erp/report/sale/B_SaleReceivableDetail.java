package engine.erp.report.sale;

import java.util.Hashtable;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadingListener;
import engine.dataset.sql.QueryWhere;
import engine.util.MessageFormat;
import java.util.*;
import java.text.*;
/**
 * <p>Title: 销售应收帐款明细</p>
 * <p>Description: 销售应收帐款明细</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @version 1.0
 */

public class B_SaleReceivableDetail implements ReportDataLoadingListener
{
  private Hashtable fieldTable = new Hashtable();
  //数据来源表示提单
  private final static String LADING_RECEIVABLE_SQL
  = "SELECT * FROM ("
  + "  SELECT NULL id,NULL areacode,NULL dwdm,NULL dwmc, to_date('{qcdate}', 'YYYY-MM-DD')rq,  '期初余额' zy,NULL jf, NULL df, nvl(jf,0)+nvl(ysk,0)-nvl(df,0) ye, NULL lx, NULL djh, NULL jsfs, NULL ywy,NULL khlx "
  + "  FROM "
  + "  ( "
  + "  SELECT "
  + "    ( "
  + "     SELECT SUM(nvl(b.jje,0))"
  + "     FROM xs_td t,dwtx w,dwdq q,xs_tdhw b"
  + "     WHERE t.dwtxid=w.dwtxid AND t.tdid=b.tdid"
  + "     AND w.dqh=q.dqh"
  + "     AND t.tdrq <to_date('{startdate}', 'YYYY-MM-DD')  {areacode}  {dwtxid}  {personid} {khlx} {tdzt}"
  + "     AND  t.fgsid = {fgsid}"
  + "     AND t.isinit=0"//初始化提单不计入应收款
  + "     ) jf,"
  + "     ("
  + "     SELECT SUM(nvl(t.je,0))"
  + "     FROM cw_xsjs t,dwtx w,dwdq q"
  + "     WHERE t.dwtxid=w.dwtxid"
  + "     AND w.dqh=q.dqh"
  + "     AND t.rq <to_date('{startdate}', 'YYYY-MM-DD') {areacode} {dwtxid}  {khlx} {personid} {jszt} "
  + "     AND  t.fgsid = {fgsid}"
  + "     ) df,"
  + "     ("
  + "     SELECT SUM(nvl(t.ysk,0))ysk"
  + "     FROM xs_ysk t,dwtx w,dwdq q"
  + "     WHERE  t.dwtxid=w.dwtxid"
  + "     AND w.dqh=q.dqh    {areacode} {dwtxid} {khlx} {personid}"
  + "     AND  t.fgsid = {fgsid}"
  + "     ) ysk"
  + "     FROM dual"
  + "     ) a"
  + "     UNION ALL"
  + "     SELECT  t.tdid id,q.areacode,f.dwdm, f.dwmc, t.tdrq,  decode(t.djlx, 1, '销货', -1, '销退', '') zy,"
  + "          sum(nvl(b.jje,0)) jf, NULL df, NULL, NULL lx, t.tdbh,   j.jsfs,  e.xm,t.khlx "//--zy 摘要"
  + "     FROM xs_td t, jsfs j, emp e,dwtx f,dwdq q,xs_tdhw b"
  + "     WHERE t.jsfsid = j.jsfsid AND t.tdid=b.tdid"
  + "     AND f.dqh=q.dqh"
  + "     AND t.personid = e.personid"
  + "     AND t.dwtxid=f.dwtxid"
  + "     AND   t.tdrq >=to_date('{startdate}', 'YYYY-MM-DD')"
  + "     AND t.tdrq <=to_date('{enddate}', 'YYYY-MM-DD')    {areacode}   {dwtxid} {personid} {khlx} {tdzt}"
  + "     AND  t.fgsid = {fgsid}"
//--2004-5-13 初始化提单不计入应收款
  + "     AND t.isinit=0"
  + "     GROUP BY t.tdid ,q.areacode,f.dwdm, f.dwmc, t.tdrq,t.tdbh,   j.jsfs,  e.xm,t.khlx,t.djlx"
//--销售结算数据
  + "     UNION ALL"
  + "     SELECT t.xsjsid id,q.areacode,f.dwdm,f.dwmc, t.rq,    '结算' zy, NULL jf, t.je df, NULL,"
  + "     NULL lx, t.djh,   j.jsfs,  e.xm,t.khlx"
  + "     FROM cw_xsjs t, jsfs j, emp e,dwtx f,dwdq q"
  + "     WHERE t.jsfsid = j.jsfsid"
  + "     AND t.personid = e.personid"
  + "     AND t.dwtxid=f.dwtxid"
  + "     AND t.rq >=to_date('{startdate}', 'YYYY-MM-DD') AND t.rq <=to_date('{enddate}', 'YYYY-MM-DD')"
  + "     AND f.dqh=q.dqh  {areacode} {dwtxid}  {personid} {khlx} {jszt}"
  + "     AND t.fgsid = {fgsid}"
  + "     ) t"
  + "     ORDER BY rq;  ";
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
  + "      SELECT q.areacode,q.dqmc, c.dwmc,c.dwdm,t.dwtxid,null khlx,NULL qichuye,NULL jf,SUM(nvl(t.je,0))df,NULL ysk,NULL zsl"
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
  + "      SELECT q.areacode,q.dqmc, c.dwmc,c.dwdm,t.dwtxid,null khlx,NULL njf, SUM(nvl(t.je,0))ndf,NULL zsl"//--贷方
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

    String qcdate="";

    if(!startdate.equals(""))
    {
      //String tdrq = m_RowInfo.get("tdrq");
      try{
      Date sdate = new SimpleDateFormat("yyyy-MM-dd").parse(startdate);//提单的开单日期
      Date endate = new Date(sdate.getTime() - (long)(60*60*24*1000));//毫秒
      qcdate = new SimpleDateFormat("yyyy-MM-dd").format(endate);
      }catch(Exception e){}
    }

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
      tdzt = " AND t.zt=8 ";
    else
      tdzt="  AND t.zt in(2,8)";
    if(jszt.equals("0"))
     jszt = " AND t.zt in(1,8) ";
    else
      jszt=" and t.zt in(0,9,1,8) ";
    if(djxz.equals("2"))
      fpzt = " AND t.zt = 1 ";

    fieldTable.clear();
    fieldTable.put("qcdate", qcdate);
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



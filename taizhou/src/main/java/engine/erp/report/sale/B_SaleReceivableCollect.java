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
 * <p>Title: 销售应收帐款汇总</p>
 * <p>Description: 销售应收帐款汇总</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @version 1.0
 */
public class B_SaleReceivableCollect implements ReportDataLoadingListener
{
  private Hashtable fieldTable = new Hashtable();
  //数据来源表示提单
  private final static String LADING_RECEIVABLE_SQL
  = "SELECT * FROM ("
  + "SELECT nvl(aye,0)aye,nvl(cye,0)cye,nvl(acye,0)acye, nvl(axsje,0)axsje,nvl(cxsje,0)cxsje, nvl(acxsje,0)acxsje,nvl(assje,0) assje,nvl(cssje,0)cssje,nvl(acssje,0)acssje,nvl(aqian,0)aqian,nvl(cqian,0)cqian,nvl(acqian,0)acqian,dwmc,dwtxid,dwdm FROM ("
  + "  SELECT SUM(aye) aye,SUM(cye) cye,SUM(acye) acye, SUM(axsje)axsje,SUM(cxsje)cxsje, SUM(acxsje)acxsje,SUM(assje) assje, SUM(cssje) cssje,SUM(acssje) acssje,sum(nvl(aye,0)+nvl(axsje,0)-nvl(assje,0))aqian,sum(nvl(cye,0)+nvl(cxsje,0)-nvl(cssje,0))cqian,sum(nvl(acye,0)+nvl(acxsje,0)-nvl(acssje,0))acqian,dwmc,dwtxid,dwdm"
  + "  FROM"
  + "  ("
  + "  SELECT SUM(nvl(axfje,0)+nvl(aysk,0)-nvl(ayfje,0)) aye,SUM(nvl(cxfje,0)+nvl(cysk,0)-nvl(cyfje,0)) cye, SUM(nvl(acxfje,0)+nvl(acysk,0)-nvl(acyfje,0)) acye,NULL axsje, NULL assje, NULL cxsje, NULL acxsje,NULL cssje,NULL acssje,  dwmc,dwdm,dwtxid"
  + "  FROM"
  + "  ("//---------------------a
  + "  SELECT c.dwdm,c.dwmc,  sum(nvl(x.jje,0)) axfje,NULL cxfje,NULL acxfje, NULL ayfje, NULL cyfje,NULL acyfje,NULL aysk,NULL cysk,NULL acysk,t.dwtxid"
  + "  FROM xs_td t,xs_tdhw x, dwtx c"//--得到上期需付款"
  + "  WHERE t.dwtxid=c.dwtxid"
  + "  AND t.tdid=x.tdid"
  + "  AND t.tdrq<to_date('{startdate}', 'YYYY-MM-DD') AND  t.fgsid = {fgsid}"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND  t.zt= 8"
  + "  AND t.khlx='A'"//----
  + "  GROUP BY c.dwmc, c.dwdm,t.dwtxid"
  + "  UNION ALL"
  + "  SELECT b.dwdm,b.dwmc, NULL axfje,NULL cxfje,NULL acxfje, SUM(nvl(t.je,0)) ayfje , NULL cyfje,NULL acyfje,NULL aysk,NULL cysk,NULL acysk,t.dwtxid"
  + "  FROM cw_xsjs t, dwtx b"// --得到上期已付款
  + "  WHERE t.dwtxid=b.dwtxid"
  + "  AND t.rq<to_date('{startdate}', 'YYYY-MM-DD')"
  + "  AND  t.fgsid = {fgsid}"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND  t.zt IN(1,8)"
  + "  AND t.khlx='A'"//-----
  + "  GROUP BY b.dwmc, b.dwdm,t.dwtxid"
  + "  UNION ALL"
  + "  SELECT b.dwdm,b.dwmc,  NULL axfje,NULL cxfje, NULL acxfje,NULL ayfje , NULL cyfje,NULL acyfje,sum(nvl(ysk,0))aysk,NULL cysk,NULL acysk,t.dwtxid"
  + "  FROM xs_ysk t, dwtx b"// --得到上期已付款
  + "  WHERE t.dwtxid=b.dwtxid"
  + "  AND  t.fgsid = {fgsid}"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND t.khlx='A'"//-----
  + "  GROUP BY b.dwmc,b.dwdm,t.dwtxid"
  + "  UNION ALL"//------------------------------c
  + "  SELECT c.dwdm,c.dwmc, NULL axfje, sum(nvl(x.jje,0)) cxfje,NULL acxfje, NULL ayfje, NULL cyfje,NULL acyfje,NULL aysk,NULL cysk,NULL acysk,t.dwtxid"
  + "  FROM xs_td t,xs_tdhw x, dwtx c"//--得到上期需付款
  + "  WHERE t.dwtxid=c.dwtxid"
  + "  AND t.tdid=x.tdid"
  + "  AND t.tdrq<to_date('{startdate}', 'YYYY-MM-DD') AND  t.fgsid = {fgsid}"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND t.zt =8"
  + "  AND t.khlx='C'"
  + "  GROUP BY c.dwmc, c.dwdm,t.dwtxid"
  + "  UNION ALL"
  + "  SELECT b.dwdm,b.dwmc,  NULL axfje, NULL cxfje,NULL acxfje, NULL ayfje, SUM(nvl(t.je,0)) cyfje ,NULL acyfje,NULL aysk,NULL cysk,NULL acysk,t.dwtxid"
  + "  FROM cw_xsjs t, dwtx b"// --得到上期已付款
  + "  WHERE t.dwtxid=b.dwtxid"
  + "  AND t.rq<to_date('{startdate}', 'YYYY-MM-DD')"
  + "  AND  t.fgsid = {fgsid}"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND  t.zt IN(1,8)"
  + "  AND t.khlx='C'"
  + "  GROUP BY b.dwmc, t.khlx,b.dwdm,t.dwtxid"
  + "  UNION ALL"
  + "  SELECT b.dwdm,b.dwmc, NULL axfje, NULL cxfje,NULL acxfje, NULL ayfje, NULL cyfje ,NULL acyfje ,NULL aysk ,sum(nvl(ysk,0))cysk,NULL acysk ,t.dwtxid"
  + "  FROM xs_ysk t, dwtx b"// --得到上期已付款
  + "  WHERE t.dwtxid=b.dwtxid"
  + "  AND  t.fgsid = {fgsid}"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND t.khlx='C'"
  + "  GROUP BY b.dwmc,b.dwdm,t.dwtxid"
  + "  UNION ALL"//------------------------------ac
  + "  SELECT c.dwdm,c.dwmc, NULL axfje,NULL cxfje, sum(nvl(x.jje,0)) acxfje, NULL ayfje, NULL cyfje,NULL acyfje,NULL aysk,NULL cysk,NULL acysk,t.dwtxid"
  + "  FROM xs_td t,xs_tdhw x, dwtx c"//--得到上期需付款
  + "  WHERE t.dwtxid=c.dwtxid"
  + "  AND t.tdid=x.tdid"
  + "  AND t.tdrq<to_date('{startdate}', 'YYYY-MM-DD') AND  t.fgsid = {fgsid}"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND  t.zt =8"
  + "  GROUP BY c.dwmc, c.dwdm,t.dwtxid"
  + "  UNION ALL"
  + "  SELECT b.dwdm,b.dwmc, NULL axfje, NULL cxfje,NULL acxfje, NULL ayfje,NULL cyfje,  SUM(nvl(t.je,0)) acyfje ,NULL aysk,NULL cysk,NULL acysk,t.dwtxid"
  + "  FROM cw_xsjs t, dwtx b"// --得到上期已付款
  + "  WHERE t.dwtxid=b.dwtxid"
  + "  AND t.rq<to_date('{startdate}', 'YYYY-MM-DD')"
  + "  AND  t.fgsid = {fgsid}"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND  t.zt IN(1,8)"
  + "  GROUP BY b.dwmc, b.dwdm,t.dwtxid"
  + "  UNION ALL"
  + "  SELECT b.dwdm,b.dwmc, NULL axfje, NULL cxfje, NULL acxfje,NULL ayfje, NULL cyfje ,NULL acyfje ,NULL aysk ,NULL cysk,sum(nvl(t.ysk,0))acysk,t.dwtxid"
  + "  FROM xs_ysk t, dwtx b "//--得到上期已付款
  + "  WHERE t.dwtxid=b.dwtxid"
  + "  AND  t.fgsid = {fgsid}"
  + "  {dwtxid}"
  + "  {personid}"
  + "  GROUP BY b.dwmc,b.dwdm,t.dwtxid,t.khlx"
  + "  ) e"
  + "  GROUP BY  dwmc,dwdm,dwtxid"//----------------------得到余额

    //提单数据
  + "  UNION ALL"
  + "  SELECT null aye,null cye,null acye,sum(nvl(r.jje,0))axsje,null assje,NULL cxsje,NULL acxsje,NULL cssje,NULL acssje,c.dwmc,c.dwdm,t.dwtxid"
  + "  FROM xs_td t,xs_tdhw r,dwtx c"
  + "  WHERE  t.tdid=r.tdid"
  + "  AND t.dwtxid=c.dwtxid"
  + "  AND t.tdrq >= to_date('{startdate}', 'YYYY-MM-DD') AND t.tdrq <= to_date('{enddate}', 'YYYY-MM-DD')"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND  t.fgsid = {fgsid}"
  + "  AND t.zt =8"
  + "  AND t.khlx='A'"//---
  + "  group by c.dwmc,c.dwdm,t.dwtxid"
    //--销售结算数据
  + "  UNION ALL"
  + "  SELECT null aye,NULL cye,null acye,null axsje,sum(nvl(t.je,0)) assje,null cxsje,null acxsje,NULL cssje ,NULL acssje,c.dwmc,c.dwdm,t.dwtxid"
  + "  FROM cw_xsjs t,dwtx c"
  + "  WHERE   t.rq >= to_date('{startdate}', 'YYYY-MM-DD') AND t.rq <= to_date('{enddate}', 'YYYY-MM-DD')"
  + "  AND t.dwtxid=c.dwtxid"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND  t.fgsid = {fgsid}"
  + "  AND  t.zt IN(1,8)"
  + "  AND t.khlx='A'"//----
  + "  GROUP BY  c.dwmc,c.dwdm,t.dwtxid"
  + "  UNION ALL"//----------------------------------------------------------------c
  + "  SELECT null aye,NULL cye,null acye,NULL axsje,NULL assje,sum(nvl(r.jje,0))cxsje,null acxsje,null cssje,null acssje,c.dwmc,c.dwdm,t.dwtxid"
  + "  FROM xs_td t,xs_tdhw r,dwtx c"
  + "  WHERE  t.tdid=r.tdid"
  + "  AND t.dwtxid=c.dwtxid"
  + "  AND t.tdrq >= to_date('{startdate}', 'YYYY-MM-DD') AND t.tdrq < to_date('{enddate}', 'YYYY-MM-DD')"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND  t.fgsid = {fgsid}"
  + "  AND  t.zt =8"
  + "  AND t.khlx='C'"//----
  + "  group by c.dwmc,c.dwdm,t.dwtxid"
    //--销售结算数据
  + "  UNION ALL"
  + "  SELECT null aye,NULL cye,null acye,NULL axsje,NULL assje,null cxsje,null acxsje,sum(nvl(t.je,0))cssje,null acssje, c.dwmc,c.dwdm,t.dwtxid"
  + "  FROM cw_xsjs t,dwtx c"
  + "  WHERE   t.rq >= to_date('{startdate}', 'YYYY-MM-DD') AND t.rq <= to_date('{enddate}', 'YYYY-MM-DD')"
  + "  AND t.dwtxid=c.dwtxid"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND  t.fgsid = {fgsid}"
  + "  AND  t.zt IN(1,8)"
  + "  AND t.khlx='C'"
  + "  GROUP BY  c.dwmc,c.dwdm,t.dwtxid"
  + "  UNION ALL"//----------------------------------------------------------------ac
  + "  SELECT null aye,NULL cye,null acye,NULL axsje,NULL assje,NULL cxsje,sum(nvl(r.jje,0))acxsje,null cssje,null acssje,c.dwmc,c.dwdm,t.dwtxid"
  + "  FROM xs_td t,xs_tdhw r,dwtx c"
  + "  WHERE  t.tdid=r.tdid"
  + "  AND t.dwtxid=c.dwtxid"
  + "  AND t.tdrq >= to_date('{startdate}', 'YYYY-MM-DD') AND t.tdrq <= to_date('{enddate}', 'YYYY-MM-DD')"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND  t.fgsid = {fgsid}"
  + "  AND  t.zt =8"
  + "  group by c.dwmc,c.dwdm,t.dwtxid"
    //--销售结算数据
  + "  UNION ALL"
  + "  SELECT null aye,NULL cye,null acye,NULL axsje,NULL assje,null cxsje,null acxsje,NULL cssje,sum(nvl(t.je,0)) acssje, c.dwmc,c.dwdm,t.dwtxid"
  + "  FROM cw_xsjs t,dwtx c"
  + "  WHERE   t.rq >= to_date('{startdate}', 'YYYY-MM-DD') AND t.rq <= to_date('{enddate}', 'YYYY-MM-DD')"
  + "  AND t.dwtxid=c.dwtxid"
  + "  {dwtxid}"
  + "  {personid}"
  + "  AND  t.fgsid = {fgsid}"
  + "  AND  t.zt IN(1,8)"
  + "  GROUP BY  c.dwmc,c.dwdm,t.dwtxid"
  + "  ) t GROUP BY dwmc, dwdm,dwtxid "
  + " )m {display}"
  + "   )T {advance} ";
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
    String display = whereQuery.getWhereValue("a$display");
    //应收款初始化用
    //String yskpersonid = personid;
    //String areacode = whereQuery.getWhereValue("a$areacode");
    //String khlx = whereQuery.getWhereValue("a$khlx");
    //String tdzt = whereQuery.getWhereValue("a$zt");
    //String jszt = whereQuery.getWhereValue("a$jzt");
    //String djxz = whereQuery.getWhereValue("a$djxz");
    //String nokhlx = whereQuery.getWhereValue("a$nokhlx");

    //String qcdate="";

    //if(!startdate.equals(""))
    //{
      //String tdrq = m_RowInfo.get("tdrq");
     // try{
    //  Date sdate = new SimpleDateFormat("yyyy-MM-dd").parse(startdate);//提单的开单日期
    //  Date endate = new Date(sdate.getTime() - (long)(60*60*24*1000));//毫秒
    //  qcdate = new SimpleDateFormat("yyyy-MM-dd").format(endate);
    //  }catch(Exception e){}
    //}

    //发票用
    String fpzt = "";
    if(dwtxid.length() > 0)
      dwtxid = "AND t.dwtxid="+dwtxid;
    if(personid.length() > 0)
      personid = "AND t.personid="+personid;
    if(display.equals("1"))
      display = "";
    else
      display =
      " where m.aye !=0 or m.cye !=0 or m.acye !=0 or  m.axsje !=0 or "
      +" m.cxsje !=0 or  m.acxsje !=0 or m.assje !=0 or  m.cssje !=0 or "
      +" m.acssje !=0 or m.aqian !=0 or m.cqian !=0 or m.acqian !=0 " ;
    /*
    if(yskpersonid.length() > 0)
      yskpersonid = " AND t.personid="+yskpersonid;
    if(areacode.length() > 0)
      areacode = " AND q.areacode='"+areacode+"'";
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
    */

    fieldTable.clear();
    //fieldTable.put("qcdate", qcdate);
    fieldTable.put("startdate", startdate);
    fieldTable.put("enddate", enddate);
    fieldTable.put("fgsid", fgsid);
    fieldTable.put("dwtxid", dwtxid);
    fieldTable.put("personid", personid);
    fieldTable.put("display", display);
    //fieldTable.put("yskpersonid", yskpersonid);
    //fieldTable.put("areacode", areacode);
    //if(djxz.equals("2"))
    //  fieldTable.put("fpzt", fpzt);
   // else
    //  fieldTable.put("tdzt", tdzt);

   // fieldTable.put("jszt", jszt);
    //
    StringBuffer buf = new StringBuffer();
    //String condition = whereQuery.getWhereValue("a$sg");
    //if(condition.equals("1"))
    //  buf.append("WHERE (t.zjf <> 0 OR t.zdf<>0 OR qimeye<>0)");
    String advance = context.getAdvanceWhere().getWhereQuery();
    if(advance.length()>0)
      buf.append(buf.length() > 0 ? "AND " : " WHERE").append(advance);
    fieldTable.put("advance", buf.toString());

    String SQL=LADING_RECEIVABLE_SQL;

    boolean isLading =true; //whereQuery.getWhereValue("a$djxz").equals("1");
    String sql = MessageFormat.format( SQL , fieldTable);
    dsRep.setQueryString(sql);
    if(dsRep.isOpen())
      dsRep.refresh();
    else
      dsRep.openDataSet();

  }
}



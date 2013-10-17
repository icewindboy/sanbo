package engine.erp.report.store;

import java.util.Hashtable;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadingListener;
import engine.dataset.sql.QueryWhere;
import engine.util.MessageFormat;

/**
 * <p>Title: 库存库存量表</p>
 * <p>Description: 库存收发单据汇总表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨建国
 * @version 1.0
 */

public class B_InOutStoreAssortCollect implements ReportDataLoadingListener
{
  private Hashtable fieldTable = new Hashtable();
  //提取包含未记帐的存货收发明细
  private final static String STOCK_NUMBER_SQL
      = " SELECT * FROM ( "
      + " SELECT  cpid, dmsxid, cpbm, sxz,product, zjm, sum(nvl(qcsl, 0)) qcjc,  "
      + "   sum(nvl(cgrk, 0)) cgrk, "
      + "   sum(nvl(cgtkrk,0)) cgtkrk, sum(nvl(jgrk,0)) jgrk, sum(nvl(ccprk,0)) ccprk, "
      + "   sum(nvl(qtrk,0)) qtrk,sum(nvl(ykrk,0)) ykrk, sum(nvl(syrk,0)) syrk, "
      + "   sum(nvl(scllck,0)) scllck, sum(nvl(wjgck,0)) wjgck, "
      + "   sum(nvl(xsck,0)) xsck, sum(nvl(xstkck,0)) xstkck, sum(nvl(ggcxck,0)) ggcxck, "
      + "   sum(nvl(xszsck,0)) xszsck,sum(nvl(qtckck,0)) qtckck, SUM(nvl( ykckck, 0)) ykckck, "
      + "   sum(nvl(a.cgrk,0)+nvl(cgtkrk,0)+nvl(a.jgrk,0)+nvl(ccprk,0)+nvl(qtrk,0)+nvl(ykrk,0)+nvl(syrk,0)) zrk, "
      + "   sum(nvl(a.scllck,0)+nvl(wjgck,0)+nvl(a.xsck,0)+nvl(xstkck,0)  +nvl(ggcxck,0)+nvl(xszsck,0)+nvl(qtckck,0)+nvl(ykckck,0)) zck, "
      + "   sum(nvl(qcsl, 0) + (nvl(a.cgrk,0)+nvl(cgtkrk,0)+nvl(a.jgrk,0)+nvl(ccprk,0)+nvl(qtrk,0)+nvl(ykrk,0)+nvl(syrk,0)) "
      + "          - (nvl(a.scllck,0)+nvl(wjgck,0)+nvl(a.xsck,0)+nvl(xstkck,0)+nvl(ggcxck,0)+nvl(xszsck,0)+nvl(qtckck,0)+nvl(ykckck,0)) "
      + "      ) qmjc "
      + " FROM ( "//期初结存
      + "        SELECT a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm,  "
      + "               SUM(nvl(srsl, 0)-nvl(fcsl, 0)) qcsl, NULL cgrk, NULL cgtkrk, "
      + "               NULL jgrk,NULL ccprk,NULL qtrk,NULL ykrk,NULL syrk,NULL scllck, "
      + "               NULL wjgck,NULL xsck,NULL xstkck, "
      + "               NULL ggcxck,NULL xszsck,NULL qtckck,0 ykckck "
      + "       FROM vw_kc_storebill a, vw_kc_dm b, kc_dmsx c, kc_chlb d, kc_dmlb e, kc_ck f "
      + "       WHERE a.cpid = b.cpid(+) AND a.dmsxid = c.dmsxid(+) AND b.chlbid = d.chlbid "
      + "            AND b.wzlbid = e.wzlbid AND a.storeid = f.storeid(+) "
      + "            AND a.sfrq < to_date('{startdate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
      + "            and a.storeid in (select storeid from jc_ckqx where personid = {personid} ) "
      + "            {cpid} {storeid} {chlbid} {startcpbm} {pm} {gg}  {wzlbid} {p_sfdjcheck_in} "
      + "       GROUP BY a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm "
      + " UNION ALL "//(采购入库 -- 采购退库 1) cgrk, cgtkrk
      + " SELECT  a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm,  NULL qcsl, "
      + "         SUM(decode(srlx, 1,nvl(srsl, 0), -1, 0)) cgrk, "
      + "         SUM(decode(srlx, -1,nvl(srsl, 0), 1, 0)) cgtkrk, "
      + "         NULL jgrk,NULL ccprk,NULL qtrk,NULL ykrk,NULL syrk,NULL scllck,NULL wjgck, "
      + "         NULL xsck,NULL xstkck,NULL ggcxck,NULL xszsck,NULL qtckck,NULL ykckck "
      + " FROM vw_kc_storebill a, vw_kc_dm b, kc_dmsx c, kc_chlb d, kc_dmlb e, kc_ck f, kc_sfdjlb g "
      + " WHERE a.djxz = 1 AND a.sfdjlbid = g.sfdjlbid(+) "
      + "       AND a.cpid = b.cpid(+) AND a.dmsxid = c.dmsxid(+) AND b.chlbid = d.chlbid "
      + "       AND b.wzlbid = e.wzlbid AND a.storeid = f.storeid(+) "
      + "       AND  a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
      + "       and a.storeid in (select storeid from jc_ckqx where personid = {personid}) "
      + "       {cpid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} {wzlbid} {p_sfdjcheck_in} "
      + " GROUP BY a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm "
      + " UNION ALL "//加工入库(数据来源:5-外加工入库单，56-坯布加工单) jgrk
      + " SELECT  a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm , NULL qcsl, "
      + "         NULL cgrk, NULL cgtkrk,SUM(nvl(srsl, 0)) jgrk, "
      + "         NULL ccprk,NULL qtrk,NULL ykrk,NULL syrk,NULL scllck,NULL wjgck, "
      + "         NULL xsck,NULL xstkck, "
      + "         NULL ggcxck,NULL xszsck,NULL qtckck,NULL ykckck "
      + " FROM vw_kc_storebill a, vw_kc_dm b, kc_dmsx c, kc_chlb d, kc_dmlb e, kc_ck f, kc_sfdjlb g "
      + " WHERE a.djxz IN(5, 56) AND a.sfdjlbid = g.sfdjlbid(+) "
      + "       AND a.cpid = b.cpid(+) AND a.dmsxid = c.dmsxid(+) AND b.chlbid = d.chlbid "
      + "       AND b.wzlbid = e.wzlbid AND a.storeid = f.storeid(+) "
      + "       AND  a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
      + "       and a.storeid in (select storeid from jc_ckqx where personid = {personid}) "
      + "       {cpid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} {wzlbid} {p_sfdjcheck_in} "
      + " GROUP BY a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm "
      + " UNION ALL " //产成品入库(数据来源:3-自制收货单)
      + " SELECT  a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm , "
      + "         NULL qcsl, NULL cgrk, NULL cgtkrk,NULL jgrk, "
      + "         SUM(nvl(srsl, 0)) ccprk,NULL qtrk,NULL ykrk,NULL syrk,NULL scllck,NULL wjgck, "
      + "         NULL xsck,NULL xstkck,NULL ggcxck,NULL xszsck,NULL qtckck,NULL ykckck "
      + " FROM vw_kc_storebill a, vw_kc_dm b, kc_dmsx c, kc_chlb d, kc_dmlb e, kc_ck f, kc_sfdjlb g "
      + " WHERE a.djxz = 3 AND a.sfdjlbid = g.sfdjlbid(+) "
      + "       AND a.cpid = b.cpid(+) AND a.dmsxid = c.dmsxid(+) AND b.chlbid = d.chlbid "
      + "       AND b.wzlbid = e.wzlbid AND a.storeid = f.storeid(+) "
      + "       AND  a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
      + "       and a.storeid in (select storeid from jc_ckqx where personid = {personid}) "
      + "       {cpid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} {wzlbid} {p_sfdjcheck_in} "
      + " GROUP BY a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm "
      + " UNION ALL "//--其他入库(数据来源:其他入口单-9)
      + " SELECT  a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm , "
      + "         NULL qcsl, NULL cgrk, NULL cgtkrk,NULL jgrk,NULL ccprk, "
      + "         SUM(nvl(srsl, 0)) qtrk,NULL ykrk,NULL syrk,NULL scllck,NULL wjgck,NULL xsck,NULL xstkck, "
      + "         NULL ggcxck,NULL xszsck,NULL qtckck,NULL ykckck "
      + " FROM vw_kc_storebill a, vw_kc_dm b, kc_dmsx c, kc_chlb d, kc_dmlb e, kc_ck f, kc_sfdjlb g "
      + " WHERE a.djxz = 9 AND a.sfdjlbid = g.sfdjlbid(+) "
      + "       AND a.cpid = b.cpid(+) AND a.dmsxid = c.dmsxid(+) AND b.chlbid = d.chlbid(+) "
      + "       AND b.wzlbid = e.wzlbid(+) AND a.storeid = f.storeid(+) "
      + "       AND  a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
      + "       and a.storeid in (select storeid from jc_ckqx where personid = {personid}) "
      + "       {cpid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} {wzlbid} {p_sfdjcheck_in} "
      + " GROUP BY a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm "
      + " UNION ALL "//--移库入库(数据来源:移库单-调入 -1 ,同价调拨单 -2 调入)
      + " SELECT  a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm,  "
      + "         NULL qcsl, NULL cgrk, NULL cgtkrk,NULL jgrk,NULL ccprk, "
      + "         NULL qtrk,SUM(nvl(srsl, 0)) ykrk,NULL syrk,NULL scllck,NULL wjgck,NULL xsck,NULL xstkck, "
      + "         NULL ggcxck,NULL xszsck,NULL qtckck,NULL ykckck "
      + " FROM vw_kc_storebill a, vw_kc_dm b, kc_dmsx c, kc_chlb d, kc_dmlb e, kc_ck f, kc_sfdjlb g "
      + " WHERE a.djxz IN (-1) AND a.sfdjlbid = g.sfdjlbid(+) "
      + "    AND a.cpid = b.cpid(+) AND a.dmsxid = c.dmsxid(+) AND b.chlbid = d.chlbid(+) "
      + "    AND b.wzlbid = e.wzlbid(+) AND a.storeid = f.storeid(+) "
      + "    AND  a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
      + "    and a.storeid in (select storeid from jc_ckqx where personid = {personid}) "
      + "    {cpid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} {wzlbid} {p_sfdjcheck_in} "
      + " GROUP BY a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm "
      + " UNION ALL "//--损益(数据来源:损益单 7)
      + " SELECT  a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm, "
      + "    NULL qcsl, NULL cgrk, NULL cgtkrk,NULL jgrk,NULL ccprk, "
      + "    NULL qtrk,NULL ykrk,SUM(nvl(srsl, 0)) syrk,NULL scllck,NULL wjgck,NULL xsck,NULL xstkck, "
      + "    NULL ggcxck,NULL xszsck,NULL qtckck,NULL ykckck "
      + " FROM vw_kc_storebill a, vw_kc_dm b, kc_dmsx c, kc_chlb d, kc_dmlb e, kc_ck f, kc_sfdjlb g "
      + " WHERE a.djxz = 7 AND a.sfdjlbid = g.sfdjlbid(+) "
      + "  AND a.cpid = b.cpid(+) AND a.dmsxid = c.dmsxid(+) AND b.chlbid = d.chlbid(+) "
      + "  AND b.wzlbid = e.wzlbid(+) AND a.storeid = f.storeid(+) "
      + "    AND  a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
      + "    and a.storeid in (select storeid from jc_ckqx where personid = {personid}) "
      + "    {cpid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} {wzlbid} {p_sfdjcheck_in} "
      + " GROUP BY a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm "
      + " UNION ALL "//--生产领料(4)
      + " SELECT  a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm,  "
      + "    NULL qcsl, NULL cgrk, NULL cgtkrk,NULL jgrk,NULL ccprk, "
      + "    NULL qtrk,NULL ykrk,NULL syrk,SUM(nvl(fcsl, 0)) scllck,NULL wjgck,NULL xsck,NULL xstkck, "
      + "    NULL ggcxck,NULL xszsck,NULL qtckck,NULL ykckck "
      + " FROM vw_kc_storebill a, vw_kc_dm b, kc_dmsx c, kc_chlb d, kc_dmlb e, kc_ck f, kc_sfdjlb g "
      + " WHERE a.djxz = 4 AND a.sfdjlbid = g.sfdjlbid(+) "
      + "  AND a.cpid = b.cpid(+) AND a.dmsxid = c.dmsxid(+) AND b.chlbid = d.chlbid(+) "
      + "  AND b.wzlbid = e.wzlbid(+) AND a.storeid = f.storeid(+) "
      + "  AND a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
      + "  and a.storeid in (select storeid from jc_ckqx where personid = {personid}) "
      + "  {cpid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} {wzlbid} {p_sfdjcheck_in} "
      + " GROUP BY a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm "
      + " UNION ALL "//--加工出库(数据来源:外加工发料单 6)
      + " SELECT  a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm,  "
      + "   NULL qcsl, NULL cgrk, NULL cgtkrk,NULL jgrk,NULL ccprk, "
      + "   NULL qtrk,NULL ykrk,NULL syrk,SUM(nvl(fcsl, 0)) scllck,SUM(nvl(fcsl, 0)) wjgck,NULL xsck, "
      + "   NULL xstkck,NULL ggcxck,NULL xszsck,NULL qtckck,NULL ykckck "
      + " FROM vw_kc_storebill a, vw_kc_dm b, kc_dmsx c, kc_chlb d, kc_dmlb e, kc_ck f, kc_sfdjlb g "
      + " WHERE a.djxz = 6 AND a.sfdjlbid = g.sfdjlbid(+) "
      + "   AND a.cpid = b.cpid(+) AND a.dmsxid = c.dmsxid(+) AND b.chlbid = d.chlbid(+) "
      + "   AND b.wzlbid = e.wzlbid(+) AND a.storeid = f.storeid(+) "
      + "   AND a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
      + "   and a.storeid in (select storeid from jc_ckqx where personid = {personid}) "
      + "  {cpid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} {wzlbid} {p_sfdjcheck_in} "
      + " GROUP BY a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm "
      + " UNION ALL "//销售出库,销售退库, include 同价调拨单11 同价调回单12 促销调拨单13
      + " SELECT  a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm,  "
      + "   NULL qcsl, NULL cgrk, NULL cgtkrk,NULL jgrk,NULL ccprk, "
      + "   NULL qtrk,NULL ykrk,NULL syrk,NULL scllck,NULL wjgck, "
      + "    SUM(decode(i.djlx, 1, "
      + "                      decode(g.srlx, 1, nvl(fcsl, 0), -1, 0), "
      + "                       11, decode(g.srlx, 1, nvl(fcsl, 0), -1, 0), 0 ) "
      + "      ) xsck, "
      + "    SUM(decode(i.djlx, -1, "
      + "                  decode(g.srlx, 1, 0, -1, nvl(fcsl, 0) ), "
      + "               12, decode(g.srlx, 1, 0, -1, nvl(fcsl, 0) ), 0 ) "
      + "       ) xstkck, "
      + "   SUM(decode(i.djlx, 2, nvl(fcsl, 0), 13, nvl(fcsl, 0), 0 )) ggcxck, "//13 促销调拨单 2 ggcxck
      + "   SUM(decode(i.djlx, 3, nvl(fcsl, 0),0)) xszsck,NULL qtckck, NULL ykckck "
      + " FROM vw_kc_storebill a, vw_kc_dm b, kc_dmsx c, kc_chlb d, kc_dmlb e, kc_ck f, kc_sfdjlb g, "
      + "  kc_sfdjmx h, xs_td i, xs_tdhw j "
      + " WHERE a.djxz = 2 AND a.sfdjlbid = g.sfdjlbid(+) "
      + "   AND a.cpid = b.cpid(+) AND a.dmsxid = c.dmsxid(+) AND b.chlbid = d.chlbid(+) "
      + "   AND b.wzlbid = e.wzlbid(+) AND a.storeid = f.storeid(+) "
      + "   AND a.sfdjid = h.sfdjid AND A.rkdmxid = h.rkdmxid AND h.wjid = j.tdhwid AND j.tdid = i.tdid AND i.djlx IN (1,-1,2,3,13, 11, 12) "// 同价调拨单 同价调回单 促销调拨单
      + "   AND a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
      + "   and a.storeid in (select storeid from jc_ckqx where personid = {personid}) "
      + "   {cpid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} {wzlbid} {p_sfdjcheck_in} "
      + " GROUP BY a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm "
      + " UNION ALL "//--其他出库 10
      + " SELECT  a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm,  "
      + "     NULL qcsl, NULL cgrk, NULL cgtkrk,NULL jgrk,NULL ccprk, "
      + "     NULL qtrk,NULL ykrk,NULL syrk,NULL scllck,NULL wjgck,NULL xsck,NULL xstkck,NULL ggcxck, "
      + "     NULL xszsck,SUM(nvl(fcsl, 0)) qtckck,NULL ykckck "
      + " FROM vw_kc_storebill a, vw_kc_dm b, kc_dmsx c, kc_chlb d, kc_dmlb e, kc_ck f, kc_sfdjlb g "
      + " WHERE a.djxz = 10 AND a.sfdjlbid = g.sfdjlbid(+) "
      + "   AND a.cpid = b.cpid(+) AND a.dmsxid = c.dmsxid(+) AND b.chlbid = d.chlbid(+) "
      + "   AND b.wzlbid = e.wzlbid(+) AND a.storeid = f.storeid(+) "
      + "   AND a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
      + "   and a.storeid in (select storeid from jc_ckqx where personid = {personid}) "
      + "   {cpid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} {wzlbid} {p_sfdjcheck_in} "
      + "   GROUP BY a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm "
      + " UNION ALL "//--移库出库(数据来源:移库单8,同价调回单11)
      + " SELECT  a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm,  "
      + "    NULL qcsl, NULL cgrk, NULL cgtkrk,NULL jgrk, "
      + "    NULL ccprk,NULL qtrk,NULL ykrk,NULL syrk,NULL scllck,NULL wjgck,NULL xsck,NULL xstkck, "
      + "    NULL ggcxck,NULL xszsck,NULL qtckck,SUM(nvl(fcsl, 0)) ykckck "
      + " FROM vw_kc_storebill a, vw_kc_dm b, kc_dmsx c, kc_chlb d, kc_dmlb e, kc_ck f, kc_sfdjlb g "
      + " WHERE a.djxz IN (8) AND a.sfdjlbid = g.sfdjlbid(+) "
      + "   AND a.cpid = b.cpid(+) AND a.dmsxid = c.dmsxid(+) AND b.chlbid = d.chlbid(+) "
      + "   AND b.wzlbid = e.wzlbid(+) AND a.storeid = f.storeid(+) "
      + "   AND a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
      + "   and a.storeid in (select storeid from jc_ckqx where personid = {personid}) "
      + "   {cpid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} {wzlbid} {p_sfdjcheck_in} "
      + " GROUP BY a.cpid, a.dmsxid, b.cpbm, c.sxz,b.product, b.zjm "
      + " ) a  GROUP BY cpid, dmsxid, cpbm, sxz,product, zjm order by cpbm  ) T {advance} ";

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
    String personid = whereQuery.getWhereValue("a$personid");
    String cpid = whereQuery.getWhereValue("a$cpid");
    String storeid = whereQuery.getWhereValue("a$storeid");
    String chlbid = whereQuery.getWhereValue("a$chlbid");
    String startcpbm = whereQuery.getWhereValue("a$startcpbm");
    String endcpbm = whereQuery.getWhereValue("a$endcpbm");
    String pm = whereQuery.getWhereValue("a$pm");
    String gg = whereQuery.getWhereValue("a$gg");
    String wzlbid = whereQuery.getWhereValue("a$wzlbid");
    String p_sfdjcheck_in = whereQuery.getWhereValue("a$p_sfdjcheck_in");
    if(cpid.length() > 0)
      cpid = "AND a.cpid="+cpid;
    if(storeid.length() > 0)
      storeid = "AND a.storeid="+storeid;
    if(chlbid.length() > 0)
      chlbid = "AND b.chlbid="+chlbid;
    if(startcpbm.length() > 0)
      startcpbm = "AND b.cpbm >= '"+ startcpbm +"'";
    if(endcpbm.length() > 0)
      endcpbm = "AND b.cpbm <= '"+ endcpbm +"'";
    if(pm.length() > 0)
      pm = "AND b.pm LIKE '%"+ pm +"%'";
    if(gg.length() > 0)
      gg = "AND b.gg LIKE '%"+ gg +"%'";
    if(wzlbid.length() > 0)
      wzlbid = " AND ( b.wzlbid = " + wzlbid + "  or b.wzlbid in ( select k.wzlbid from kc_dmlb k where k.parentid=" + wzlbid + " ) )";
    if(p_sfdjcheck_in.length() > 0)
      p_sfdjcheck_in = "AND ((" + p_sfdjcheck_in  + " = '1' AND a.Isrefer = 1) OR ( " + p_sfdjcheck_in + "= '0' AND a.zt IN (2, 8)))" ;

    fieldTable.clear();
    fieldTable.put("startdate", startdate);
    fieldTable.put("enddate", enddate);
    fieldTable.put("fgsid", fgsid);
    fieldTable.put("personid", personid);
    fieldTable.put("storeid", storeid);
    fieldTable.put("cpid", cpid);
    fieldTable.put("storeid", storeid);
    fieldTable.put("chlbid", chlbid);
    fieldTable.put("startcpbm", startcpbm);
    fieldTable.put("endcpbm", endcpbm);
    fieldTable.put("pm", pm);
    fieldTable.put("gg", gg);
    fieldTable.put("wzlbid", wzlbid);
    fieldTable.put("p_sfdjcheck_in", p_sfdjcheck_in);
    String advance = context.getAdvanceWhere().getWhereQuery();
    fieldTable.put("advance", advance.length()>0 ? " WHERE "+advance : "");

    String sql = MessageFormat.format(STOCK_NUMBER_SQL, fieldTable);
    dsRep.setQueryString(sql);
    if(dsRep.isOpen())
      dsRep.refresh();
    else
      dsRep.openDataSet();
  }

  /*public void dataLoaded(ServletRequest parm1, TempletData parm2, ContextData parm3, EngineDataSet parm4)
  {
    QueryWhere where = parm3.getQueryWhere();
    String ksrq = where.getWhereValue("a$sfrq$a");//查询条件开始日期
    String jsrq = where.getWhereValue("a$sfrq$b");//查询条件结束日期
    String storeid = where.getWhereValue("a$storeid");//查询条件仓库
    Object[] objects = parm2.getTablesAndOther();
    for(int i=0; i<objects.length; i++)
    {
      if(objects[i] instanceof String)
      {
        String s = (String)objects[i];
        if(s.startsWith("<SCRIPT LANGUAGE='javascript' id='where'>"))
        {
          s = "<SCRIPT LANGUAGE='javascript' id='where'>var ksrq='"+ksrq+"'; var jsrq='"+jsrq+"'; var storeid='"+storeid+"'; </SCRIPT>";
          parm2.setOther(i, s);
        }
      }
    }
  }*/

}



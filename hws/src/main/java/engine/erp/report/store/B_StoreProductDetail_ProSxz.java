package engine.erp.report.store;

import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.dataset.*;
import engine.report.event.ReportDataLoadedListener;
import engine.report.event.ReportDataLoadingListener;
import engine.dataset.sql.QueryWhere;
import engine.util.*;
import java.util.*;
import java.math.*;
import com.borland.dx.dataset.*;
import engine.erp.baseinfo.BasePublicClass;
import engine.common.LoginBean;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title: 存货明细表处理属性值，解析规格属性取出宽度，按这个宽度分类汇总查询出来的数据</p>
 * <p>Description: 存货收发汇总表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨建国
 * @version 1.0
 */

public class B_StoreProductDetail_ProSxz implements ReportDataLoadingListener//ReportDataLoadedListener, ReportDataLoadingListener
{
  private Hashtable fieldTable = new Hashtable();
  private EngineDataSet dsStockNumber = new EngineDataSet();
  private String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  private String SYS_PRODUCT_SPEC_LENGTH = null;//生产用位换算的相关规格属性名称 得到的值为“长度”
   //提取包含未记帐的存货收发明细
   private final static String STOCK_NUMBER_SQL
     = "SELECT * FROM ("
       + "SELECT cpid, cpbm, pm, gg, jldw, hsdw, sxz, sum(nvl(kcsl,0)) kcsl,sum(nvl(kchssl,0)) kchssl, sum(nvl(wjzssl,0)) wjzssl, "
       +"    sum(nvl(wjzfsl,0)) wjzfsl, sum(nvl(kcsl,0))+sum(nvl(hjsl,0)) hjsl, "
       +"    sum(nvl(kchssl,0))+sum(nvl(hjhssl,0)) hjhssl, ckdgs "
       + "FROM"
       + "("
       //  --得到期初结存期初未记帐的单据也计算
       + "  SELECT  cpid, cpbm, pm, gg, jldw, hsdw, sxz, (SUM(nvl(srsl,0))-SUM(nvl(fcsl,0))) kcsl, "
       + "    (SUM(nvl(srhssl,0))-SUM(nvl(fchssl,0))) kchssl, "
       + "    NULL wjzssl, NULL wjzfsl, NULL hjsl, NULL hjhssl, ckdgs"
       + "  FROM "
       + "  ("
       + "    SELECT c.cpid, c.cpbm, c.pm, c.gg, c.jldw, c.hsdw, b.sxz, SUM(nvl(a.srsl,0)) srsl, "
       + "       SUM(nvl(a.fcsl,0)) fcsl, SUM(nvl(a.srhssl,0)) srhssl, SUM(nvl(a.fchssl,0)) fchssl, d.ckdgs  "
       + "    FROM vw_kc_storebill a, kc_dm c, kc_dmsx b , kc_chlb d "
       + "    WHERE a.cpid=c.cpid AND a.dmsxid=b.dmsxid(+) AND c.chlbid = d.chlbid(+) "
       + "    AND a.sfrq < to_date('{startdate}', 'YYYY-MM-DD') AND a.fgsid={fgsid} "
       + "    {cpid} {dmsxid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} "
       /*AND (p_productid_in IS NULL OR a.cpid = p_productid_in)"
       + "    AND (p_storeid_in IS NULL OR a.storeid = p_storeid_in)"
       + "    AND (p_stocksort_in IS NULL OR c.chlbid = p_stocksort_in)"
       + "    AND (p_startcpbm_in IS NULL OR c.cpbm >= p_startcpbm_in)"
       + "    AND (p_endcpbm_in IS NULL OR c.cpbm <= p_endcpbm_in)"
       + "    AND (p_pm_in IS NULL OR c.pm LIKE v_pm)"
       + "    AND (p_gg_in IS NULL OR c.gg LIKE v_gg)"*/
       + "    GROUP BY c.cpid,c.cpbm, c.pm, c.gg, c.jldw, c.hsdw, b.sxz, d.ckdgs "
       + "  ) e"
       + "  GROUP BY cpid, cpbm, pm, gg, jldw, hsdw, sxz, ckdgs"
       + "  UNION ALL"
   //       --得到查询段时间记帐数量
       + "  SELECT  cpid, cpbm, pm, gg, jldw, hsdw, sxz, (SUM(nvl(srsl,0))-SUM(nvl(fcsl,0))) kcsl, "
       + "     (SUM(nvl(srhssl,0))-SUM(nvl(fchssl,0))) kchssl, "
       + "     NULL wjzssl, NULL wjzfsl, NULL hjsl, NULL hjhssl, ckdgs "
       + "  FROM "
       + "  ("
       + "    SELECT c.cpid, c.cpbm, c.pm, c.gg, c.jldw, c.hsdw, b.sxz, SUM(nvl(a.srsl,0)) srsl, "
       + "      SUM(nvl(a.fcsl,0)) fcsl, SUM(nvl(a.srhssl,0)) srhssl, SUM(nvl(a.fchssl,0)) fchssl, d.ckdgs "
       + "      FROM vw_kc_storebill a, kc_dm c,kc_dmsx b , kc_chlb d "//得到查询段时间记帐数量
       + "      WHERE a.cpid=c.cpid AND a.dmsxid=b.dmsxid(+) AND c.chlbid = d.chlbid(+) AND (a.zt=2 OR  a.zt=8) "
       + "      AND  a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid}"
       + "      {cpid} {dmsxid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg}  "
       + "      GROUP BY c.cpid,c.cpbm, c.pm, c.gg, c.jldw, c.hsdw, b.sxz, d.ckdgs "
       + "  ) e"
       + "  GROUP BY cpid, cpbm, pm, gg, jldw, hsdw, sxz, ckdgs"
       + "  UNION ALL"
       + "  SELECT  cpid, cpbm, pm, gg, jldw, hsdw,sxz, NULL kcsl, NULL kchssl, wjzssl, wjzfsl, nvl(wjzssl,0)-nvl(wjzfsl,0) hjsl, "
       + "    nvl(wjzsrhssl,0)-nvl(wjzfchssl,0) hjhssl,ckdgs "
       + "  FROM "
         // --得到该时间段输入输出数据
       + "  ("
       + "    SELECT c.cpid, c.cpbm, c.pm, c.gg, c.jldw, c.hsdw, b.sxz, SUM(nvl(a.srsl,0)) wjzssl, "
       + "      SUM(nvl(a.fcsl,0)) wjzfsl, SUM(nvl(a.srhssl,0)) wjzsrhssl, "
       + "      SUM(nvl(a.fchssl,0)) wjzfchssl, d.ckdgs "
       + "   FROM vw_kc_storebill a, kc_dm c,kc_dmsx b, kc_chlb d  "
       + "   WHERE a.cpid=c.cpid AND a.dmsxid=b.dmsxid(+) AND c.chlbid = d.chlbid(+) AND a.zt<>2 AND a.zt<>8"
       + "    AND  a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<to_date('{enddate}', 'YYYY-MM-DD') AND a.fgsid={fgsid}"
       + "    {cpid} {dmsxid} {storeid} {chlbid} {startcpbm} {endcpbm} {pm} {gg} "
       + "    GROUP BY c.cpid,c.cpbm, c.pm, c.gg, c.jldw, c.hsdw, b.sxz, d.ckdgs"
       + "  ) f"
       + ") g GROUP BY cpid, cpbm, pm, gg, jldw, hsdw, sxz, ckdgs ORDER BY cpbm"
      + ") T {advance} ";

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
    HttpServletRequest req = (HttpServletRequest)request;
    LoginBean loginBean = LoginBean.getInstance(req);
    SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//系统参数
    SYS_PRODUCT_SPEC_LENGTH = loginBean.getSystemParam("SYS_PRODUCT_SPEC_LENGTH");//系统参数
    if(dsRep.isOpen())
     {
       dsRep.closeDataSet();
       dsRep.setProvider(null);
     }
     else
      dsRep.setProvider(null);
    dsStockNumber.setProvider(context.getDataSetProvider());
    String startdate = whereQuery.getWhereValue("a$sfrq$a");
    String enddate = whereQuery.getWhereValue("a$sfrq$b");
    String fgsid = whereQuery.getWhereValue("a$fgsid");
    String cpid = whereQuery.getWhereValue("a$cpid");
    String dmsxid = whereQuery.getWhereValue("a$dmsxid");
    String storeid = whereQuery.getWhereValue("a$storeid");
    String chlbid = whereQuery.getWhereValue("a$chlbid");
    String startcpbm = whereQuery.getWhereValue("a$cpbm$a");
    String endcpbm = whereQuery.getWhereValue("a$cpbm$b");
    String pm = whereQuery.getWhereValue("a$pm");
    String gg = whereQuery.getWhereValue("a$gg");
    //String ph = whereQuery.getWhereValue("a$ph");
    if(cpid.length() > 0)
      cpid = "AND a.cpid="+cpid;
    if(dmsxid.length() > 0)
      dmsxid = "AND a.dmsxid="+dmsxid;
    //if(ph.length() > 0)
    //  ph = "AND a.ph LIKE '%"+ ph +"%'";
    if(storeid.length() > 0)
      storeid = "AND a.storeid="+storeid;
    if(chlbid.length() > 0)
      chlbid = "AND c.chlbid="+chlbid;
    if(startcpbm.length() > 0)
      startcpbm = "AND c.cpbm >= '"+ startcpbm +"'";
    if(endcpbm.length() > 0)
      endcpbm = "AND c.cpbm <= '"+ endcpbm +"'";
    if(pm.length() > 0)
      pm = "AND c.pm LIKE '%"+ pm +"%'";
    if(gg.length() > 0)
      gg = "AND c.gg LIKE '%"+ gg +"%'";

    fieldTable.clear();
    fieldTable.put("startdate", startdate);
    fieldTable.put("enddate", enddate);
    fieldTable.put("fgsid", fgsid);
    fieldTable.put("storeid", storeid);
    fieldTable.put("cpid", cpid);
    fieldTable.put("dmsxid", dmsxid);
    //fieldTable.put("ph", ph);
    fieldTable.put("chlbid", chlbid);
    fieldTable.put("startcpbm", startcpbm);
    fieldTable.put("endcpbm", endcpbm);
    fieldTable.put("pm", pm);
    fieldTable.put("gg", gg);
    String advance = context.getAdvanceWhere().getWhereQuery();
    fieldTable.put("advance", advance.length()>0 ? " WHERE "+advance : "");

    String sql = MessageFormat.format(STOCK_NUMBER_SQL, fieldTable);
    dsStockNumber.setQueryString(sql);
    if(dsStockNumber.isOpen())
      dsStockNumber.refresh();
    else
      dsStockNumber.openDataSet();

    dsRep.setQueryString(sql);
   if(dsRep.isOpen())
     dsRep.refresh();
   else
     dsRep.openDataSet();
     //把属性值比如（宽度(2020)厂家(大发)等级(A)）设置为（宽度(2020)）;
    dsStockNumber.first();
    String sxz = null, width= null, ckdgs = null, length = null;
   for(int i=0; i<dsStockNumber.getRowCount(); i++){
     sxz = dsStockNumber.getValue("sxz");
     width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
     ckdgs = dsStockNumber.getValue("ckdgs");
     if (!ckdgs.equals("3"))
     {
       if(width.equals("0"))
       {
         dsStockNumber.next();
         continue;
       }
       String temp = SYS_PRODUCT_SPEC_PROP+"("+width+")";
       dsStockNumber.setValue("sxz", temp);
       dsStockNumber.post();
       dsStockNumber.next();
     }
     else
     {
       width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
       length = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_LENGTH, "()");
       if(width.equals("0")&&length.equals("0"))
       {
         dsStockNumber.next();
         continue;
       }
       else
       {
         String temp = length.equals("0") ? "" : SYS_PRODUCT_SPEC_LENGTH+"("+length+")";
         temp += width.equals("0") ? "": SYS_PRODUCT_SPEC_PROP+"("+width+")";
         dsStockNumber.setValue("sxz", temp);
         dsStockNumber.post();
         dsStockNumber.next();
       }
     }
   }
   //处理数据的横向汇总
   procDynamicData(dsRep, dsStockNumber);
   dsStockNumber.closeDataSet();
  }
  /**
   * 处理数据的横向汇总.把属性值宽度相同的数据的 帐存数量 未记帐收数量 未记帐发数量 合计 都合起来
   * @param dsCollect 汇总的数据
   * @param dsAssort 未横向汇总的数据
   */
  private void procDynamicData(EngineDataSet dsCollect, EngineDataSet dsAssort)
  {
    //dmsxid, cpid, cpbm, pm, gg, jldw, qcjc, qcdj, qcje, srsl, srdj, srje, fcsl, fcdj, fcje, qmsl, qmdj,qmje, sxz
    if(dsCollect.hasColumn("cpid") == null)
      dsCollect.addColumn(dsAssort.getColumn("cpid").cloneColumn());
    if(dsCollect.hasColumn("cpbm") == null)
      dsCollect.addColumn(dsAssort.getColumn("cpbm").cloneColumn());
    if(dsCollect.hasColumn("pm") == null)
      dsCollect.addColumn(dsAssort.getColumn("pm").cloneColumn());
    if(dsCollect.hasColumn("gg") == null)
      dsCollect.addColumn(dsAssort.getColumn("gg").cloneColumn());
    if(dsCollect.hasColumn("jldw") == null)
      dsCollect.addColumn(dsAssort.getColumn("jldw").cloneColumn());
    //if(dsCollect.hasColumn("ph") == null)
    //  dsCollect.addColumn(dsAssort.getColumn("ph").cloneColumn());
    if(dsCollect.hasColumn("sxz") == null)
      dsCollect.addColumn(dsAssort.getColumn("sxz").cloneColumn());
    if(dsCollect.hasColumn("kcsl") == null)
      dsCollect.addColumn(dsAssort.getColumn("kcsl").cloneColumn());
    if(dsCollect.hasColumn("kchssl") == null)
      dsCollect.addColumn(dsAssort.getColumn("kchssl").cloneColumn());
    if(dsCollect.hasColumn("wjzssl") == null)
      dsCollect.addColumn(dsAssort.getColumn("wjzssl").cloneColumn());
    if(dsCollect.hasColumn("wjzfsl") == null)
      dsCollect.addColumn(dsAssort.getColumn("wjzfsl").cloneColumn());
    if(dsCollect.hasColumn("hjsl") == null)
      dsCollect.addColumn(dsAssort.getColumn("hjsl").cloneColumn());
    if(dsCollect.hasColumn("hjhssl") == null)
      dsCollect.addColumn(dsAssort.getColumn("hjhssl").cloneColumn());

    EngineRow row = new EngineRow(dsCollect, new String[]{"cpid", "sxz"});
    dsAssort.first();
    for(int i=0; i<dsAssort.getRowCount(); i++)
    {
      row.setValue(0, dsAssort.getValue("cpid"));
      row.setValue(1, dsAssort.getValue("sxz"));
      if(!dsCollect.locate(row, Locate.FIRST))
      {
        dsCollect.insertRow(false);
        dsCollect.setValue("cpid", dsAssort.getValue("cpid"));
        dsCollect.setValue("cpbm", dsAssort.getValue("cpbm"));
        dsCollect.setValue("pm", dsAssort.getValue("pm"));
        dsCollect.setValue("gg", dsAssort.getValue("gg"));
        dsCollect.setValue("jldw", dsAssort.getValue("jldw"));
        //dsCollect.setValue("ph", dsAssort.getValue("ph"));//期初结存
        dsCollect.setValue("sxz", dsAssort.getValue("sxz"));//期初单价
        dsCollect.setValue("kcsl", dsAssort.getValue("kcsl"));//期初金额
        dsCollect.setValue("kchssl", dsAssort.getValue("kchssl"));//查询段时间内收入单价
        dsCollect.setValue("wjzssl", dsAssort.getValue("wjzssl"));//查询段时间内收入数量
        dsCollect.setValue("wjzfsl", dsAssort.getValue("wjzfsl"));//查询段时间内收入金额
        dsCollect.setValue("hjsl", dsAssort.getValue("hjsl"));//查询段时间内发出数量
        dsCollect.setValue("hjhssl", dsAssort.getValue("hjhssl"));//查询段时间内发出单价
      }
      else{
        BigDecimal kcslValue = dsCollect.getBigDecimal("kcsl").add(dsAssort.getBigDecimal("kcsl"));
        BigDecimal kchsslValue = dsCollect.getBigDecimal("kchssl").add(dsAssort.getBigDecimal("kchssl"));
        BigDecimal wjzsslValue = dsCollect.getBigDecimal("wjzssl").add(dsAssort.getBigDecimal("wjzssl"));
        BigDecimal wjzfsllValue = dsCollect.getBigDecimal("wjzfsl").add(dsAssort.getBigDecimal("wjzfsl"));
        BigDecimal hjslValue = dsCollect.getBigDecimal("hjsl").add(dsAssort.getBigDecimal("hjsl"));
        BigDecimal hjhsslValue = dsCollect.getBigDecimal("hjhssl").add(dsAssort.getBigDecimal("hjhssl"));
        dsCollect.setBigDecimal("kcsl", kcslValue);
        dsCollect.setBigDecimal("kchssl", kchsslValue);
        dsCollect.setBigDecimal("wjzssl", wjzsslValue);
        dsCollect.setBigDecimal("wjzfsl", wjzfsllValue);
        dsCollect.setBigDecimal("hjsl", hjslValue);
        dsCollect.setBigDecimal("hjhssl", hjhsslValue);
      }
      dsCollect.post();
      dsAssort.next();
    }
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



package engine.erp.report.sale;

import java.util.Hashtable;
import java.math.BigDecimal;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadingListener;
import engine.dataset.sql.QueryWhere;
import engine.util.MessageFormat;
import engine.action.BaseAction;
import java.util.*;
import java.text.SimpleDateFormat;
import engine.action.Operate;
import engine.dataset.EngineDataSet;
import engine.dataset.RowMap;
import engine.common.LoginBean;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.EngineDataSet;
import engine.report.event.ReportDataLoadedListener;
import engine.dataset.sql.QueryWhere;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 客户信誉报表</p>
 * <p>Description: 客户信誉报表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @version 1.0
 */

public class B_Customer_xyed implements ReportDataLoadingListener
{
  private static final String CUSTOMER_XYED_SQL
      =  "SELECT * FROM "
      + "( SELECT  q.areacode,q.dqmc,v.dwdm,f.xm,v.dwmc,f.quchu,v.wfje,v.xyed,v.cxyed"
      + "  FROM "
      + "( SELECT to_date('{startdate}','yyyy-mm-dd') rq, e.dwtxid,e.xm, NULL jje, null hk, SUM(nvl(jf,0)-nvl(df,0)+nvl(ysk,0)) quchu"
      + "  FROM "
      + "  ( "
      + "  SELECT t.dwtxid,b.xm, SUM(nvl(t.zje,0))jf, NULL df, NULL hk,NULL ysk  FROM xs_td t,emp b"
      + "  WHERE t.personid=b.personid and t.tdrq < to_date('{startdate}','yyyy-mm-dd') {dwtxid}  {personid}  AND t.fgsid={fgsid} AND t.zt = 8"
      + "  GROUP BY t.dwtxid,b.xm"

      + "  UNION ALL"
      + "  SELECT t.dwtxid,b.xm, NULL jf,  SUM(nvl(t.je,0))df, NULL hk,NULL ysk  FROM cw_xsjs t,emp b"
      + "  WHERE  t.personid=b.personid and  t.rq < to_date('{startdate}','yyyy-mm-dd') {dwtxid} {personid}  AND t.fgsid={fgsid} AND t.zt = 8"
      + "  GROUP BY t.dwtxid,b.xm"
  //  --应收款
      + "  UNION ALL"
      + " SELECT t.dwtxid,b.xm, NULL jf, NULL df, NULL hk,sum(nvl(t.ysk,0)) ysk FROM  xs_ysk t,emp b"
      + "  WHERE t.personid=b.personid and   t.fgsid={fgsid} {dwtxid} {personid} "
      + " GROUP BY t.dwtxid,b.xm"
      + "     )e"
      + " GROUP BY e.dwtxid,e.xm) f,VW_XS_DQWHLJE v, dwdq q"
      + " WHERE f.dwtxid=v.dwtxid and v.dqh=q.dqh {areacode} "
      + " GROUP BY q.areacode,q.dqmc,v.dwdm,v.dwmc,f.xm,f.quchu,v.wfje,v.xyed,v.cxyed"
      + " ) T {advance}";

  private EngineDataSet dsData = new EngineDataSet();//数据集

  private Hashtable fieldTable = new Hashtable();
  public void dataLoading(ServletRequest request, TempletData templet,
                          ContextData context, EngineDataSet dsRep,
                          QueryWhere whereQuery)
  {
    //查询条件
    dsData.setProvider(context.getDataSetProvider());
    String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    //String startDate = whereQuery.getWhereValue(today);
    //String endDate = whereQuery.getWhereValue(today);
    String dwtxid = whereQuery.getWhereValue("a$dwtxid");
    String fgsid = whereQuery.getWhereValue("a$fgsid");
    String enddate = whereQuery.getWhereValue("a$jsrq");
    String personid = whereQuery.getWhereValue("a$personid");
    String areacode = whereQuery.getWhereValue("a$areacode");
    fieldTable.clear();
    if(enddate.equals(""))
      fieldTable.put("startdate", today);
    else
      fieldTable.put("startdate", enddate);
    //fieldTable.put("enddate", today);
    fieldTable.put("dwtxid", dwtxid.length()>0 ? "AND t.dwtxid="+dwtxid : "");
    fieldTable.put("personid", personid.length()>0 ? "AND t.personid="+personid : "");
    fieldTable.put("areacode", areacode.length()>0 ? "AND q.areacode="+areacode : "");

    fieldTable.put("fgsid", fgsid);
    String advance = context.getAdvanceWhere().getWhereQuery();
    fieldTable.put("advance", advance.length()>0 ? "WHERE "+advance : "");

    String sql = MessageFormat.format(CUSTOMER_XYED_SQL, fieldTable);
    dsData.setQueryString(sql);
    if(dsData.isOpen())
      dsData.refresh();
    else
      dsData.openDataSet();

    if(dsRep.isOpen())
      dsRep.close();


    dsRep.setProvider(null);
    Column col = null;
    //初始化新数据集的字段


    if(dsRep.hasColumn("areacode") == null)
    {
      col = (Column)dsData.getColumn("areacode").clone();
      dsRep.addColumn(col);
    }
    if(dsRep.hasColumn("dqmc") == null)
    {
      col = (Column)dsData.getColumn("dqmc").clone();
      dsRep.addColumn(col);
    }

    if(dsRep.hasColumn("dwdm") == null)
    {
      col = (Column)dsData.getColumn("dwdm").clone();
      dsRep.addColumn(col);
    }
    if(dsRep.hasColumn("dwmc") == null)
    {
      col = (Column)dsData.getColumn("dwmc").clone();
      dsRep.addColumn(col);
    }
    if(dsRep.hasColumn("xm") == null)
    {
      col = (Column)dsData.getColumn("xm").clone();
      dsRep.addColumn(col);
    }
    if(dsRep.hasColumn("quchu") == null)
    {
      col = (Column)dsData.getColumn("quchu").clone();
      dsRep.addColumn(col);
    }
    if(dsRep.hasColumn("wfje") == null)
    {
      col = (Column)dsData.getColumn("wfje").clone();
      dsRep.addColumn(col);
    }
    if(dsRep.hasColumn("xyed") == null)
   {
     col = (Column)dsData.getColumn("xyed").clone();
     dsRep.addColumn(col);
    }
    if(dsRep.hasColumn("cxyed") == null)
    {
      col = (Column)dsData.getColumn("cxyed").clone();

      dsRep.addColumn(col);
    }


    dsRep.openDataSet();
    procDynamicData(dsRep, dsData);
    dsData.closeDataSet();
  }
  /**
   * 处理数据的横向汇总
   * @param dsCollect 汇总的数据
   * @param dsAssort 未横向汇总的数据
   */
  private void procDynamicData(EngineDataSet dsRep, EngineDataSet dsData)
  {
    String areacode = null;
    String dqmc = null;
    String dwdm = null;
    String dwmc = null;
    String xm = null;
    String  quchu= null;
    String wfje = null;
    String xyed = null;
    String cxyed = null;

    dsData.first();
    for(int i=0; i<dsData.getRowCount(); i++)
    {
      areacode = dsData.getValue("areacode");
      dqmc = dsData.getValue("dqmc");

       dwdm = dsData.getValue("dwdm");
       dwmc = dsData.getValue("dwmc");
       xm = dsData.getValue("xm");
       quchu=dsData.getValue("quchu");
       wfje= dsData.getValue("wfje");
       xyed= dsData.getValue("xyed");
       cxyed=dsData.getValue("cxyed");

        dsRep.insertRow(false);
        dsRep.setValue("areacode", areacode);
        dsRep.setValue("dqmc", dqmc);

        dsRep.setValue("dwdm", dwdm);
        dsRep.setValue("dwmc", dwmc);
        dsRep.setValue("xm", xm);
        dsRep.setValue("quchu", quchu);
        dsRep.setValue("wfje", wfje);
        dsRep.setValue("xyed", xyed);
        dsRep.setValue("cxyed", cxyed);
        dsRep.post();



      dsData.next();
    }

   }
}

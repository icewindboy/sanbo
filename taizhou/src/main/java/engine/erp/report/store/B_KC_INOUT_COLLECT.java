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
 * <p>Title: 存货收发汇总表</p>
 * <p>Description: 存货收发汇总表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public class B_KC_INOUT_COLLECT implements ReportDataLoadingListener//ReportDataLoadedListener, ReportDataLoadingListener
{
  public B_KC_INOUT_COLLECT()
  {
  }
  /**
  public void dataLoaded(ServletRequest parm1, TempletData parm2, ContextData parm3, EngineDataSet parm4)
  {
    QueryWhere where = parm3.getQueryWhere();
    String ksrq = where.getWhereValue("a$rq$a");//查询条件开始日期
    String jsrq = where.getWhereValue("a$rq$b");//查询条件结束日期
    String storeid = where.getWhereValue("a$storeid");//查询条件仓库
    String zt = where.getWhereValue("a$zt");
    Object[] objects = parm2.getTablesAndOther();
    for(int i=0; i<objects.length; i++)
    {
      if(objects[i] instanceof String)
      {
        String s = (String)objects[i];
        if(s.startsWith("<SCRIPT LANGUAGE='javascript' id='where'>"))
        {
          s = "<SCRIPT LANGUAGE='javascript' id='where'>var ksrq='"+ksrq+"'; var jsrq='"+jsrq+"'; var storeid='"+storeid+"'; var zt='"+zt+"'</SCRIPT>";
          parm2.setOther(i, s);
        }
      }
    }
  }
  */
  private final static String STOCK_COLLECT_SQL = "{CALL PCK_STORE_REP.stock_collect(?, '@','@','@','@','@','@','@','@','@','@')}";
  private EngineDataSet dsStockCollect = new EngineDataSet();
  private String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……

  /**
   * 报表数据打开时的事件调用的方法。
   * 1:组装SQL语句，提取数据, 2:处理横向汇总的数据
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
    if(dsRep.isOpen())
    {
      //删除不需要的列
      dsRep.closeDataSet();
      dsRep.setProvider(null);
      Column[] cols = dsRep.getColumns();
      for(int i=0; i < cols.length; i++)
      {
        String colname = cols[i].getColumnName();
        if(colname.equals("dmsxid"))
          dsRep.dropColumn(cols[i]);
        else
          continue;
      }
    }
    else
      dsRep.setProvider(null);
   //提取未横向汇总的数据
   //whereQuery.putWhereValue();
    dsStockCollect.setProvider(context.getDataSetProvider());
    String startDate = whereQuery.getWhereValue("a$rq$a");
   String endDate = whereQuery.getWhereValue("a$rq$b");
   String storeid = whereQuery.getWhereValue("a$storeid");
   String chlbid = whereQuery.getWhereValue("a$chlbid");
   String cpid = whereQuery.getWhereValue("a$cpid");
   String dmsxid = whereQuery.getWhereValue("a$dmsxid");
   String fgsid = whereQuery.getWhereValue("a$fgsid");
   String zt = whereQuery.getWhereValue("a$zt");
   String pm = whereQuery.getWhereValue("a$pm");
   String sql = StringUtils.combine(STOCK_COLLECT_SQL, "@",
                                    new String[]{startDate, endDate, storeid, chlbid,cpid,dmsxid,zt,pm,fgsid});
   dsStockCollect.setQueryString(sql);
   if(dsStockCollect.isOpen())
     dsStockCollect.refresh();
   else
     dsStockCollect.openDataSet();
   //把属性值比如（宽度(2020)厂家(大发)等级(A)）设置为（宽度(2020)）;
   dsStockCollect.first();
   String sxz = null, width= null;
   for(int i=0; i<dsStockCollect.getRowCount(); i++){
     sxz = dsStockCollect.getValue("sxz");
     width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
     if(width.equals("0"))
     {
       dsStockCollect.next();
       continue;
     }
     String temp = SYS_PRODUCT_SPEC_PROP+"("+width+")";
     dsStockCollect.setValue("sxz", temp);
     dsStockCollect.post();
     dsStockCollect.next();
   }
   //处理数据的横向汇总
   dsRep.openDataSet();
   procDynamicData(dsRep, dsStockCollect);
   dsStockCollect.closeDataSet();
  }
   /**
   * 处理数据的横向汇总
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
    if(dsCollect.hasColumn("qcjc") == null)
      dsCollect.addColumn(dsAssort.getColumn("qcjc").cloneColumn());
    if(dsCollect.hasColumn("qcdj") == null)
      dsCollect.addColumn(dsAssort.getColumn("qcdj").cloneColumn());
    if(dsCollect.hasColumn("qcje") == null)
      dsCollect.addColumn(dsAssort.getColumn("qcje").cloneColumn());
    if(dsCollect.hasColumn("srsl") == null)
      dsCollect.addColumn(dsAssort.getColumn("srsl").cloneColumn());
    if(dsCollect.hasColumn("srdj") == null)
      dsCollect.addColumn(dsAssort.getColumn("srdj").cloneColumn());
    if(dsCollect.hasColumn("srje") == null)
      dsCollect.addColumn(dsAssort.getColumn("srje").cloneColumn());
    if(dsCollect.hasColumn("fcsl") == null)
      dsCollect.addColumn(dsAssort.getColumn("fcsl").cloneColumn());
    if(dsCollect.hasColumn("fcdj") == null)
      dsCollect.addColumn(dsAssort.getColumn("fcdj").cloneColumn());
    if(dsCollect.hasColumn("fcje") == null)
      dsCollect.addColumn(dsAssort.getColumn("fcje").cloneColumn());
    if(dsCollect.hasColumn("qmsl") == null)
      dsCollect.addColumn(dsAssort.getColumn("qmsl").cloneColumn());
    if(dsCollect.hasColumn("qmdj") == null)
      dsCollect.addColumn(dsAssort.getColumn("qmdj").cloneColumn());
    if(dsCollect.hasColumn("qmje") == null)
      dsCollect.addColumn(dsAssort.getColumn("qmje").cloneColumn());
    if(dsCollect.hasColumn("sxz") == null)
      dsCollect.addColumn(dsAssort.getColumn("sxz").cloneColumn());

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
        dsCollect.setValue("qcjc", dsAssort.getValue("qcjc"));//期初结存
        dsCollect.setValue("qcdj", dsAssort.getValue("qcdj"));//期初单价
        dsCollect.setValue("qcje", dsAssort.getValue("qcje"));//期初金额
        dsCollect.setValue("srdj", dsAssort.getValue("srdj"));//查询段时间内收入单价
        dsCollect.setValue("srsl", dsAssort.getValue("srsl"));//查询段时间内收入数量
        dsCollect.setValue("srje", dsAssort.getValue("srje"));//查询段时间内收入金额
        dsCollect.setValue("fcsl", dsAssort.getValue("fcsl"));//查询段时间内发出数量
        dsCollect.setValue("fcdj", dsAssort.getValue("fcdj"));//查询段时间内发出单价
        dsCollect.setValue("fcje", dsAssort.getValue("fcje"));//查询段时间内发出金额
        dsCollect.setValue("qmsl", dsAssort.getValue("qmsl"));//期末数量
        dsCollect.setValue("qmdj", dsAssort.getValue("qmdj"));//期末单价
        dsCollect.setValue("qmje", dsAssort.getValue("qmje"));//期末金额
        dsCollect.setValue("sxz", dsAssort.getValue("sxz"));
      }
      else{
        BigDecimal qcjcValue = dsCollect.getBigDecimal("qcjc").add(dsAssort.getBigDecimal("qcjc"));
        BigDecimal qcdjValue = dsCollect.getBigDecimal("qcdj").add(dsAssort.getBigDecimal("qcdj"));
        BigDecimal qcjeValue = dsCollect.getBigDecimal("qcje").add(dsAssort.getBigDecimal("qcje"));
        BigDecimal srdjValue = dsCollect.getBigDecimal("srdj").add(dsAssort.getBigDecimal("srdj"));
        BigDecimal srslValue = dsCollect.getBigDecimal("srsl").add(dsAssort.getBigDecimal("srsl"));
        BigDecimal srjeValue = dsCollect.getBigDecimal("srje").add(dsAssort.getBigDecimal("srje"));
        BigDecimal fcslValue = dsCollect.getBigDecimal("fcsl").add(dsAssort.getBigDecimal("fcsl"));
        BigDecimal fcdjValue = dsCollect.getBigDecimal("fcdj").add(dsAssort.getBigDecimal("fcdj"));
        BigDecimal fcjeValue = dsCollect.getBigDecimal("fcje").add(dsAssort.getBigDecimal("fcje"));
        BigDecimal qmslValue = dsCollect.getBigDecimal("qmsl").add(dsAssort.getBigDecimal("qmsl"));
        BigDecimal qmdjValue = dsCollect.getBigDecimal("qmdj").add(dsAssort.getBigDecimal("qmdj"));
        BigDecimal qmjeValue = dsCollect.getBigDecimal("qmje").add(dsAssort.getBigDecimal("qmje"));
        dsCollect.setBigDecimal("qcjc", qcjcValue);
        dsCollect.setBigDecimal("qcdj", qcdjValue);
        dsCollect.setBigDecimal("qcje", qcjeValue);
        dsCollect.setBigDecimal("srdj", srdjValue);
        dsCollect.setBigDecimal("srsl", srslValue);
        dsCollect.setBigDecimal("srje", srjeValue);
        dsCollect.setBigDecimal("fcsl", fcslValue);
        dsCollect.setBigDecimal("fcdj", fcdjValue);
        dsCollect.setBigDecimal("fcje", fcjeValue);
        dsCollect.setBigDecimal("qmsl", qmslValue);
        dsCollect.setBigDecimal("qmdj", qmdjValue);
        dsCollect.setBigDecimal("qmje", qmjeValue);
      }
      dsCollect.post();

      dsAssort.next();
    }
    /**
    //计算收入合计，支出合计，结存
    dsCollect.first();
    for(int i=0; i<dsCollect.rowCount(); i++)
    {
      BigDecimal inTotal = new BigDecimal(0);
      for(int j=0; j<inColumns.size(); j++)
      {
        String colName = (String)inColumns.get(j);
        inTotal = inTotal.add(dsCollect.getBigDecimal(colName));
      }
      dsCollect.setBigDecimal("in_total", inTotal);
      //
      BigDecimal outTotal = new BigDecimal(0);
      for(int j=0; j<outColumns.size(); j++)
      {
        String colName = (String)outColumns.get(j);
        outTotal = outTotal.add(dsCollect.getBigDecimal(colName));
      }
      dsCollect.setBigDecimal("out_total", outTotal);
      //
      BigDecimal total = dsCollect.getBigDecimal("beforetotal");
      total = inTotal.subtract(outTotal).add(total);
      dsCollect.setBigDecimal("total", total);
      dsCollect.post();
      dsCollect.next();

    }
          */
    //
  }
}



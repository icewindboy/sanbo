package engine.erp.report.store;

import java.util.Hashtable;
import java.util.ArrayList;
import java.math.BigDecimal;
import javax.servlet.ServletRequest;
import engine.report.util.*;
import engine.util.*;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.sql.QueryWhere;
import engine.report.event.ReportDataLoadingListener;
import com.borland.dx.dataset.*;

/**
 * <p>Title: 收发类别分类汇总报表类</p>
 * <p>Description: 收发类别分类汇总报表类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 江海岛
 * @version 1.0
 */

public class B_AssortCollect implements ReportDataLoadingListener
{
  private final static String ASSORT_COLLECT_SQL //"{CALL PCK_STORE_REP.AssortCollect(?, '@','@','@','@','@')}";
      = "SELECT * FROM ( "
      + "  SELECT a.*, b.bm cpbm, b.mc product FROM ( "
      //--得到期初结存
      //--cpid, cpbm, product, sxz, beforetotal, billtype(i/o), sfdjlbid, lbmc, sl
      + "    SELECT  wzlbid cpid, (srsl-fcsl) beforetotal, 'a' billtype, 0 sl, NULL sfdjlbid, NULL lbmc "
      + "    FROM ( "
       //--得到期初结存入库数量,出库数量
      + "      SELECT c.wzlbid, SUM(nvl(a.srsl,0)) srsl, SUM(nvl(a.fcsl,0)) fcsl " //收入数量,发出数量
      + "      FROM   vw_kc_storebill a, vw_kc_dm c, kc_chlb d, kc_dmsx e "
      + "      WHERE  a.dmsxid=e.dmsxid(+) AND a.cpid=c.cpid  "
      + "      AND    c.chlbid=d.chlbid    AND a.sfrq < to_date('{startdate}', 'YYYY-MM-DD') "
      + "      {storeid} {chlbid} AND a.fgsid={fgsid} "//a.a.storeid c.chlbid
      + "      GROUP BY c.wzlbid "
      + "    ) e "
      //--提取收入与发出的数量     (-1,1,3,5,7,9)
      //--cpid, cpbm, product, sxz, beforetotal, billtype(i/o), sfdjlbid, lbmc, sl
      + "    UNION ALL "
      + "    SELECT t.wzlbid cpid, NULL beforetotal, t.billtype, SUM(nvl(t.sl,0)) sl, t.sfdjlbid, l.lbmc "
      + "    FROM ( "
      //  --得到该时间段收入数据,支出数据
      + "      SELECT c.wzlbid, a.sfdjlbid, "
      + "             decode(a.djxz, 2, 'o', 4, 'o', 6, 'o', 8, 'o', 'i')  billtype,  "
      + "             decode(a.djxz, 2, a.fcsl, 4, a.fcsl, 6, a.fcsl, 8, a.fcsl, a.srsl) sl "
      + "      FROM   vw_kc_storebill a, vw_kc_dm c, kc_chlb d, kc_dmsx e "
      + "      WHERE  a.dmsxid=e.dmsxid(+) AND a.cpid=c.cpid  AND c.chlbid=d.chlbid "
      + "      AND    a.sfrq>=to_date('{startdate}', 'YYYY-MM-DD') AND a.sfrq<=to_date('{enddate}', 'YYYY-MM-DD') "
      + "      {storeid} {chlbid} AND a.fgsid={fgsid} "//a.a.storeid c.chlbid
      + "    ) t, kc_sfdjlb l  "
      + "    WHERE t.sfdjlbid = l.sfdjlbid(+) "
      + "    GROUP BY t.wzlbid, t.billtype, t.sfdjlbid, l.lbmc "
      + "  ) a, kc_dmlb b "
      + "  WHERE a.cpid = b.wzlbid "
      + ") T "
      + "{advance} ORDER BY billtype, cpbm";

  private Hashtable fieldTable = new Hashtable();
  private EngineDataSet dsAssort = new EngineDataSet();
  private int inColumnCount = 0;  //收入字段的数量
  private int outColumnCount = 0; //支出的字段的数量

  /**
   * 报表数据打开时的事件调用的方法。
   * 1:组装SQL语句，提取数据, 2:处理横向汇总的数据, 3:处理横向汇总的模板
   * @param request WEB请求
   * @param templet 报表模板对象
   * @param context 报表上下文对象
   * @param dsRep   报表数据对象对象
   * @param whereQuery 查询条件的所有值
   */
  public void dataLoading(ServletRequest request, TempletData templet, ContextData context,
                          EngineDataSet dsRep, QueryWhere whereQuery)
  {
    HtmlTable table  = getDynamicTable(templet);
    if(table == null)
      throw new ReportException("the report's templet has not table which id is dynamic");

    //dsRep.setSort(null);
    if(dsRep.isOpen())
    {
      //删除不需要的列
      dsRep.closeDataSet();
      dsRep.setProvider(null);
      Column[] cols = dsRep.getColumns();
      for(int i=0; i < cols.length; i++)
      {
        String colname = cols[i].getColumnName();
        if(colname.equals("cpid") || colname.equals("cpbm") || colname.equals("product") ||
           /*colname.equals("sxz")  || */colname.equals("beforetotal"))
          continue;
        dsRep.dropColumn(cols[i]);
      }
    }
    else
      dsRep.setProvider(null);
    //提取未横向汇总的数据
    dsAssort.setProvider(context.getDataSetProvider());
    String startDate = whereQuery.getWhereValue("stock$ksrq");
    String endDate = whereQuery.getWhereValue("stock$jsrq");
    String storeid = whereQuery.getWhereValue("stock$storeid");
    String chlbid = whereQuery.getWhereValue("stock$chlbid");
    String fgsid = whereQuery.getWhereValue("stock$fgsid");
    fieldTable.clear();
    fieldTable.put("startdate", startDate);
    fieldTable.put("enddate", endDate);
    fieldTable.put("storeid", storeid.length()>0 ? "AND a.storeid="+storeid : "");
    fieldTable.put("chlbid", chlbid.length()>0 ? "AND c.chlbid="+chlbid : "");
    fieldTable.put("fgsid", fgsid);
    String advance = context.getAdvanceWhere().getWhereQuery();
    fieldTable.put("advance", advance.length()>0 ? "WHERE "+advance : "");

    String sql = MessageFormat.format(ASSORT_COLLECT_SQL, fieldTable);;
    dsAssort.setQueryString(sql);
    if(dsAssort.isOpen())
      dsAssort.refresh();
    else
      dsAssort.openDataSet();
    //处理数据的横向汇总
    dsRep.openDataSet();
    procDynamicData(dsRep, dsAssort);
    dsAssort.closeDataSet();
    //dsRep.setSort(new SortDescriptor[]{});
    //处理动态模板
    procDynamicTable(dsRep, table);
  }

  /**
   * 处理数据的横向汇总
   * @param dsCollect 汇总的数据
   * @param dsAssort 未横向汇总的数据
   */
  private void procDynamicData(EngineDataSet dsCollect, EngineDataSet dsAssort)
  {
    //cpid, cpbm, product, sxz, beforetotal, billtype(i/o), sfdjlbid, lbmc, sl
    if(dsCollect.hasColumn("cpid") == null)
      dsCollect.addColumn(dsAssort.getColumn("cpid").cloneColumn());
    if(dsCollect.hasColumn("cpbm") == null)
      dsCollect.addColumn(dsAssort.getColumn("cpbm").cloneColumn());
    if(dsCollect.hasColumn("product") == null)
      dsCollect.addColumn(dsAssort.getColumn("product").cloneColumn());
    /*if(dsCollect.hasColumn("sxz") == null)
      dsCollect.addColumn(dsAssort.getColumn("sxz").cloneColumn());*/
    if(dsCollect.hasColumn("beforetotal") == null)
      dsCollect.addColumn(dsAssort.getColumn("beforetotal").cloneColumn());

    ArrayList inColumns = new ArrayList(); //收入的字段列表
    ArrayList outColumns = new ArrayList();//支出的字段列表
    boolean isIn = true;
    Column colinfo = dsAssort.getColumn("sl");
    dsAssort.first();
    for(int i=0; i<dsAssort.getRowCount(); i++)
    {
      String billtype = dsAssort.getValue("billtype");
      //期初数据
      if(billtype.equals("a"))
      {
        dsCollect.insertRow(false);
        dsCollect.setValue("cpid", dsAssort.getValue("cpid"));
        dsCollect.setValue("cpbm", dsAssort.getValue("cpbm"));
        dsCollect.setValue("product", dsAssort.getValue("product"));
        /*dsCollect.setValue("sxz", dsAssort.getValue("sxz"));*/
        dsCollect.setBigDecimal("beforetotal", dsAssort.getBigDecimal("beforetotal"));
        dsAssort.next();
        continue;
      }

      boolean isCurIn = billtype.equals("i");
      if(isIn && !isCurIn)
      {
        Column coltemp = colinfo.cloneColumn();
        coltemp.setColumnName("in_total");
        coltemp.setCaption("合计");
        dsCollect.addColumn(coltemp);
      }
      isIn = isCurIn;
      //得到字段名称
      String sfdjlbid = dsAssort.getValue("sfdjlbid");
      //如果收发单据类别id为空，就归入其他的项目
      String colName = (isIn ? "in_" : "out_") + (sfdjlbid.length() == 0 ? "other" : sfdjlbid);

      if(dsCollect.hasColumn(colName) == null)
      {
        Column coltemp = colinfo.cloneColumn();
        coltemp.setColumnName(colName);
        coltemp.setCaption(colName.equals("in_other") || colName.equals("out_other")
                           ? "其他" : dsAssort.getValue("lbmc"));
        dsCollect.addColumn(coltemp);
        //
        if(isIn)
          inColumns.add(colName);
        else
          outColumns.add(colName);
      }
      EngineRow row = new EngineRow(dsCollect, new String[]{"cpid"/*, "sxz"*/});
      row.setValue(0, dsAssort.getValue("cpid"));
      /*row.setValue(1, dsAssort.getValue("sxz"));*/
      if(!dsCollect.locate(row, Locate.FIRST))
      {
        dsCollect.insertRow(false);
        dsCollect.setValue("cpid", dsAssort.getValue("cpid"));
        dsCollect.setValue("cpbm", dsAssort.getValue("cpbm"));
        dsCollect.setValue("product", dsAssort.getValue("product"));
        /*dsCollect.setValue("sxz", dsAssort.getValue("sxz"));*/
      }
      BigDecimal slValue = dsCollect.getBigDecimal(colName).add(dsAssort.getBigDecimal("sl"));
      dsCollect.setBigDecimal(colName, slValue);
      dsCollect.post();

      dsAssort.next();
    }
    //收入和支出的合计字段
    if(isIn){
      Column coltemp = colinfo.cloneColumn();
      coltemp.setColumnName("in_total");
      coltemp.setCaption("合计");
      dsCollect.addColumn(coltemp);
    }
    Column coltemp = colinfo.cloneColumn();
    coltemp.setColumnName("out_total");
    coltemp.setCaption("合计");
    dsCollect.addColumn(coltemp);
    //
    coltemp = colinfo.cloneColumn();
    coltemp.setColumnName("total");
    coltemp.setCaption("结存");
    dsCollect.addColumn(coltemp);
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
    //
    inColumnCount = inColumns.size();
    outColumnCount = outColumns.size();
  }

  /**
   * 处理动态模板
   * @param dsCollect 汇总的数据
   * @param table 模板对象的动态表格
   */
  private void procDynamicTable(EngineDataSet dsCollect, HtmlTable table)
  {
    HtmlTableRow[] rows = table.getTableRows();
    if(rows.length != 3)
      throw new ReportException("the report's templet dynamic table row count error");
    //--1
    //处理标题行
    HtmlTableCell[] cells = rows[0].getCells();
    for(int i=0; i<cells.length; i++)
    {
      String id = (String)cells[i].getCellInfo().get(Tag.ID);
      if(id == null)
        continue;
      else if(id.equals("intotal"))
        cells[i].getCellInfo().put(Tag.COLSPAN, String.valueOf(inColumnCount+1));
      else if(id.equals("outtotal"))
        cells[i].getCellInfo().put(Tag.COLSPAN, String.valueOf(outColumnCount+1));
    }
    //--2
    //处理标题行有动态的标题
    rows[1].clearAllCells();
    Column[] cols = dsCollect.getColumns();
    //设置单元格的属性:align="center" valign="middle" nowrap="true"
    Hashtable cellInfo = new Hashtable();
    cellInfo.put(Tag.ALIGN, Tag.ALIGN_CENTER);
    cellInfo.put(Tag.VALIGN, Tag.VALIGN_MIDDLE);
    cellInfo.put(Tag.NOWRAP, Tag.TRUE);
    //处理收入字段
    for(int i=0; i<cols.length; i++)
    {
      String colName = cols[i].getColumnName();
      //如果是收入的字段
      if(colName.startsWith("in_"))
      {
        HtmlTableCell temp = new HtmlTableCell((Hashtable)cellInfo.clone());
        temp.addContent(cols[i].getCaption());
        rows[1].addCell(temp);
      }
    }
    //处理支出字段
    for(int i=0; i<cols.length; i++)
    {
      String colName = cols[i].getColumnName();
      //如果是收入的字段
      if(colName.startsWith("out_"))
      {
        HtmlTableCell temp = new HtmlTableCell((Hashtable)cellInfo.clone());
        temp.addContent(cols[i].getCaption());
        rows[1].addCell(temp);
      }
    }
    //--3
    //处理数据行
    cells = rows[2].getCells();
    rows[2].clearAllCells();
    HtmlTableCell totalCell = null;
    for(int i=0; i < cells.length; i++)
    {
      String id = (String)cells[i].getCellInfo().get(Tag.ID);
      if(id == null)
        continue;
      else if(id.equals("preserve"))//preserve表示将模板的单元格保留的标志
        rows[2].addCell(cells[i]);
      else if(id.equals("total"))   //表示最后的合计字段，需要特殊处理
        totalCell = cells[i];
    }
    cellInfo.clear();
    cellInfo.put(Tag.ALIGN, Tag.ALIGN_RIGHT);
    cellInfo.put(Tag.VALIGN, Tag.VALIGN_MIDDLE);
    cellInfo.put(Tag.NOWRAP, Tag.TRUE);
    Hashtable valueInfo = new Hashtable();
    //处理收入字段
    for(int i=0; i<cols.length; i++)
    {
      String colName = cols[i].getColumnName();
      //如果是收入的字段
      if(colName.startsWith("in_"))
      {
        HtmlTableCell temp = new HtmlTableCell((Hashtable)cellInfo.clone());
        Hashtable value = (Hashtable)valueInfo.clone();
        value.put(Tag.FIELD, colName);
        temp.addValue(value);
        rows[2].addCell(temp);
      }
    }
    //处理支出字段
    for(int i=0; i<cols.length; i++)
    {
      String colName = cols[i].getColumnName();
      //如果是收入的字段
      if(colName.startsWith("out_"))
      {
        HtmlTableCell temp = new HtmlTableCell((Hashtable)cellInfo.clone());
        Hashtable value = (Hashtable)valueInfo.clone();
        value.put(Tag.FIELD, colName);
        temp.addValue(value);
        rows[2].addCell(temp);
      }
    }
    rows[2].addCell(totalCell);
  }

  /**
   * 得到动态表对象
   * @param templet 报表模板对象
   * @return 返回动态表对象
   */
  private HtmlTable getDynamicTable(TempletData templet)
  {
    HtmlTable[] tables = templet.getTables();
    for(int i=0; i<tables.length; i++)
    {
      String tableid = (String)tables[i].getTableInfo().get(Tag.ID);
      if(tableid != null && tableid.equals("dynamic"))
        return tables[i];
    }
    return null;
  }
}
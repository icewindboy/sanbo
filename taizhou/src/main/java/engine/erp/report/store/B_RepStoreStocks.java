package engine.erp.report.store;

import java.util.Hashtable;
import java.util.ArrayList;
import java.math.BigDecimal;
import javax.servlet.ServletRequest;
import engine.report.util.*;
import engine.util.*;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineDataView;
import engine.dataset.EngineRow;
import engine.dataset.sql.QueryWhere;
import engine.report.event.ReportDataLoadingListener;
import com.borland.dx.dataset.*;
import engine.common.LoginBean;
import javax.servlet.http.HttpServletRequest;
import engine.erp.baseinfo.BasePublicClass;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title: 库存分库结存表类</p>
 * <p>Description: 库存分库结存表类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 杨建国
 * @version 1.0
 */

public class B_RepStoreStocks implements ReportDataLoadingListener
{
  private final static String StoreStocks_SQL
      = " SELECT * FROM ( "
      + "  SELECT b.cpid, d.dmsxid, c.storeid, b.cpbm, d.sxz, b.jldw, c.ckmc, b.pm, b.gg, b.product, e.ckdgs, SUM(nvl(a.srsl,0) - nvl(a.fcsl,0)) zl "
      + "  FROM vw_kc_storebill a, vw_kc_dm b, kc_ck c, kc_dmsx d, kc_chlb e"
      + "  WHERE "
      + "      a.cpid = b.cpid(+) AND a.dmsxid = d.dmsxid(+) "
      + "      AND a.storeid = c.storeid(+) "
      + "      AND a.storeid = b.storeid(+) "
      + "      AND a.cpid = d.cpid(+) "
      + "      AND b.chlbid = e.chlbid(+) "
      + "      {pm} {gg}  {cpbm} {chlbid} {yf} "
      + "      GROUP BY b.cpid, d.dmsxid, c.storeid, b.cpbm, d.sxz, b.jldw, c.ckmc,b.pm,b.gg,b.product,e.ckdgs "
      + " ) T "
      + " {advance} ORDER BY cpbm ";

  private final static String StoreStocks_Struct_SQL
      = "  SELECT b.cpid, d.dmsxid, c.storeid, b.cpbm, d.sxz, b.jldw, c.ckmc, b.pm, b.gg, b.product, e.ckdgs, SUM(a.srsl-a.fcsl) zl, SUM(0) storeTotal  "
      + "  FROM vw_kc_storebill a, vw_kc_dm b, kc_ck c, kc_dmsx d, kc_chlb e"
      + "  WHERE 1<>1 "
      + "  GROUP BY b.cpid, d.dmsxid, c.storeid, b.cpbm, d.sxz, b.jldw, c.ckmc,b.pm,b.gg,b.product,e.ckdgs ";


  private Hashtable fieldTable   = new Hashtable();
  private EngineDataSet dsAssort = new EngineDataSet();
  private EngineDataSet dsTmpProcessSxz = new EngineDataSet();
  private int storeColumnCount   = 0;  //库房字段的数量
  private String SYS_PRODUCT_SPEC_PROP   = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  private String SYS_PRODUCT_SPEC_LENGTH = null;//生产用位换算的相关规格属性名称 得到的值为“长度”

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
    HttpServletRequest req = (HttpServletRequest)request;
    LoginBean loginBean = LoginBean.getInstance(req);
    SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//系统参数
    SYS_PRODUCT_SPEC_LENGTH = loginBean.getSystemParam("SYS_PRODUCT_SPEC_LENGTH");//系统参数

    HtmlTable table  = getDynamicTable(templet);
    if(table == null)
      throw new ReportException("the report's templet has not table which id is dynamic");

    if(dsRep.isOpen())
     {
       //删除不需要的列
       dsRep.closeDataSet();
       dsRep.setProvider(null);
       Column[] cols = dsRep.getColumns();
       for(int i=0; i < cols.length; i++)
       {
         String colname = cols[i].getColumnName();
         if( colname.equals("cpid") || colname.equals("cpbm") || colname.equals("pm")
            || colname.equals("gg") || colname.equals("product")
            || colname.equals("dmsxid") || colname.equals("sxz")
            || colname.equals("jldw")
          )
           continue;
         dsRep.dropColumn(cols[i]);
       }
     }
     else
       dsRep.setProvider(null);
    //提取未横向汇总的数据
    if ( dsAssort.isOpen() ) dsAssort.closeDataSet();
    if ( dsTmpProcessSxz.isOpen() ) dsTmpProcessSxz.closeDataSet();
    dsAssort.setProvider(context.getDataSetProvider());
    dsTmpProcessSxz.setProvider(context.getDataSetProvider());
    String cpbm = whereQuery.getWhereValue("stock$cpbm");
    String pm = whereQuery.getWhereValue("stock$pm");
    String gg = whereQuery.getWhereValue("stock$gg");
    String yf = whereQuery.getWhereValue("stock$yf");
    String chlbid = whereQuery.getWhereValue("stock$chlbid");
    String fgsid = whereQuery.getWhereValue("stock$fgsid");
    Calendar calendar = new GregorianCalendar();//为了取得年份
    String currentYear = String.valueOf(calendar.get(Calendar.YEAR));
    fieldTable.clear();
    fieldTable.put("cpbm", cpbm.length() > 0 ? " AND b.cpbm = '"+ cpbm + "'" : "");
    fieldTable.put("pm", pm.length() > 0 ? " AND b.pm = '"+ pm + "'" : "");
    fieldTable.put("gg", gg.length() > 0 ? " AND b.gg = '"+ gg + "'" : "");
    fieldTable.put("yf", yf.length() > 0 ? " AND to_date(to_char(a.sfrq,'yyyy-mm'),'yyyy-mm')<= to_date('" +  currentYear + "-" + yf +"', 'yyyy-mm')": "");
    fieldTable.put("chlbid", chlbid.length() > 0 ? " AND b.chlbid = " + chlbid : "");
    fieldTable.put("fgsid", fgsid);
    String advance = context.getAdvanceWhere().getWhereQuery();
    fieldTable.put("advance", advance.length()>0 ? " WHERE " + advance : "");

    String sql = MessageFormat.format(StoreStocks_SQL, fieldTable);;
    dsAssort.setQueryString(sql);
    if(dsAssort.isOpen())
      dsAssort.refresh();
    else
      dsAssort.openDataSet();

    dsTmpProcessSxz.setQueryString(StoreStocks_Struct_SQL);
    if(dsTmpProcessSxz.isOpen())
      dsTmpProcessSxz.refresh();
    else
      dsTmpProcessSxz.openDataSet();

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
    if(dsCollect.hasColumn("pm") == null)
      dsCollect.addColumn(dsAssort.getColumn("pm").cloneColumn());
    if(dsCollect.hasColumn("gg") == null)
      dsCollect.addColumn(dsAssort.getColumn("gg").cloneColumn());
    if(dsCollect.hasColumn("product") == null)
      dsCollect.addColumn(dsAssort.getColumn("product").cloneColumn());
    if(dsCollect.hasColumn("dmsxid") == null)
      dsCollect.addColumn(dsAssort.getColumn("dmsxid").cloneColumn());
    if(dsCollect.hasColumn("sxz") == null)
      dsCollect.addColumn(dsAssort.getColumn("sxz").cloneColumn());
    if(dsCollect.hasColumn("ckdgs") == null)
      dsCollect.addColumn(dsAssort.getColumn("ckdgs").cloneColumn());
    if(dsCollect.hasColumn("jldw") == null)
      dsCollect.addColumn(dsAssort.getColumn("jldw").cloneColumn());

    /*
     因为查询出来的数据是只要求按宽度值来汇总各库房的数据.
     所以在横向汇总出来dsAssort数据集后,就须紧接着处理dsAssort中的sxz字段.
     1.取得sxz字段值解析出来宽度 ,长度
     2.一般产品按只解析出来 gg*宽度. 如果是平装纸的话(ckdgs=3),则取sxz中的长度*宽度
       2.1 如:把属性值比如（宽度(2020)厂家(大发)等级(A)）设置为（宽度(2020)）;
     3.把dsAssort中的每一笔记录的sxz字段上的值都修改成按2中解析规则得到的新的字串.保存dsAssort,使sxz成为新的解析得到的值.
    */
    String sxz = null, width= null, ckdgs = null, length = null;
    dsAssort.first();
    for(int i=0; i<dsAssort.getRowCount(); i++){
      sxz = dsAssort.getValue("sxz");
      width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
      ckdgs = dsAssort.getValue("ckdgs");
      if (!ckdgs.equals("3"))
      {
        if(width.equals("0"))
        {
          dsAssort.next();
          continue;
        }
        String temp = SYS_PRODUCT_SPEC_PROP+"("+width+")";
        dsAssort.setValue("sxz", temp);
        dsAssort.post();
        dsAssort.next();
      }
      else
      {
        width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
        length = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_LENGTH, "()");
        if(width.equals("0")&&length.equals("0"))
        {
          dsCollect.next();
          continue;
        }
        else
        {
          String temp = length.equals("0") ? "" : SYS_PRODUCT_SPEC_LENGTH+"("+length+")";
          temp += width.equals("0") ? "": SYS_PRODUCT_SPEC_PROP+"("+width+")";
          dsAssort.setValue("sxz", temp);
          dsAssort.post();
          dsAssort.next();
        }
      }
    }

    ArrayList storeColumns = new ArrayList(); //从dsAssort给dsCollect添加上去的库房
    ArrayList storeColumnsName = new ArrayList();
    boolean isIn = true;
    Column colinfo = dsAssort.getColumn("zl");
    dsAssort.first();
    /*
    此循环做的工作是把dsAssort数据集中的数据横向汇总成dsCollect中的数据
    */
    for(int i=0; i<dsAssort.getRowCount(); i++)
    {
      String storeid = dsAssort.getValue("storeid");
      //如果库房为空.就归入其它库房
      String colName =  "store_" + (storeid.length() == 0 ? "other" : storeid);
      //从dsAssort取一条记录.得到它的storeid字段名字,如这个名字在dsCollect的表结构中没有
      //则修改dsCollect的结构为其新增一列库房id字段:
      if(dsCollect.hasColumn(colName) == null)
      {
        String storeName = dsAssort.getValue("ckmc");
        Column coltemp = colinfo.cloneColumn();
        coltemp.setColumnName(colName);
        coltemp.setCaption(storeName);
        dsCollect.addColumn(coltemp);
        storeColumns.add(colName);
        storeColumnsName.add(storeName);
      }
      EngineRow row = new EngineRow(dsCollect, new String[]{"cpid", "sxz"});
      String locateCpid = dsAssort.getValue("cpid");
      String locateSxz = dsAssort.getValue("sxz");

      row.setValue(0, locateCpid);
      row.setValue(1, locateSxz);
      /*row.setValue(1, dsAssort.getValue("sxz"));*/
      if(!dsCollect.locate(row, Locate.FIRST))
      {
        String cpid = dsAssort.getValue("cpid");
        String cpbm = dsAssort.getValue("cpbm");
        String product = dsAssort.getValue("product");
        String dmsxid = dsAssort.getValue("dmsxid");
        String tmpsxz = dsAssort.getValue("sxz");
        String pm = dsAssort.getValue("pm");
        String gg = dsAssort.getValue("gg");
        String jldw = dsAssort.getValue("jldw");
        String tmpckdgs = dsAssort.getValue("ckdgs");

        dsCollect.insertRow(false);
        dsCollect.setValue("cpid", cpid);
        dsCollect.setValue("cpbm", cpbm);
        dsCollect.setValue("product", product);
        dsCollect.setValue("dmsxid", dmsxid);
        dsCollect.setValue("sxz", tmpsxz);
        dsCollect.setValue("pm", pm);
        dsCollect.setValue("gg", gg);
        dsCollect.setValue("jldw", jldw);
        dsCollect.setValue("ckdgs", tmpckdgs);
      }
      BigDecimal slValue = dsCollect.getBigDecimal(colName).add(dsAssort.getBigDecimal("zl"));
      String s = slValue.toString();
      dsCollect.setBigDecimal(colName, slValue);
      dsCollect.post();
      dsAssort.next();
    }
    //把dsCollect中每一笔记录的库房字段表示的本库房存量合计起来作为本笔记录的总合计
    Column coltemp = colinfo.cloneColumn();
    coltemp.setColumnName("storeTotal");
    coltemp.setCaption("合计");
    dsCollect.addColumn(coltemp);
    dsCollect.first();
    for(int i=0; i<dsCollect.rowCount(); i++)
    {
      BigDecimal inTotal = new BigDecimal(0);
      for(int j=0; j<storeColumns.size(); j++)
      {
        String colName = (String)storeColumns.get(j);
        inTotal = inTotal.add(dsCollect.getBigDecimal(colName));
      }
      String tmpt = inTotal.toString();
      dsCollect.setBigDecimal("storeTotal", inTotal);
      dsCollect.post();
      dsCollect.next();
    }
    storeColumnCount = storeColumns.size();
    /*if ( dsTmpProcessSxz.isOpen() )
      dsTmpProcessSxz.closeDataSet();
    把所有sxz相等的记录各库房数据汇总
    for (int k = 0; k<storeColumns.size(); k++)
    {
      String colName = (String)storeColumns.get(k);
      if(dsTmpProcessSxz.hasColumn(colName) == null)
      {
        String storeName = (String)storeColumnsName.get(k);
        Column coltempSxz = colinfo.cloneColumn();
        coltempSxz.setColumnName(colName);
        coltempSxz.setCaption(storeName);
        dsTmpProcessSxz.addColumn(coltempSxz);
      }
    }

    if(dsTmpProcessSxz.isOpen())
     dsTmpProcessSxz.refresh();
   else
      dsTmpProcessSxz.openDataSet();
   */
    EngineRow row = new EngineRow(dsCollect, new String[]{"cpid", "sxz"});
    dsCollect.first();
    for(int i=0; i<dsCollect.getRowCount(); i++)
    {
      String tmpGg = dsCollect.getValue("gg");
      String tmpCkdgs = dsCollect.getValue("ckdgs");
      //判断是否平装纸.如是:则赋gg=gg*宽度(即:gg*sxz).如否:则赋gg=长度*宽度(即:sxz)
      if (tmpCkdgs.equals("3"))
      {
        width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
        length = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_LENGTH, "()");
        tmpGg = width + "*" + length;
      }
      else
      {
        String tmpSxz = dsCollect.getValue("sxz");
        width = BasePublicClass.parseEspecialString(tmpSxz, SYS_PRODUCT_SPEC_PROP, "()");
        tmpGg = tmpGg + width;
      }
      dsCollect.setValue("gg", tmpGg);
      dsCollect.post();
      dsCollect.next();
    }
  }

  /**
   * 处理动态模板
   * @param dsCollect 汇总的数据
   * @param table 模板对象的动态表格
   */
  private void procDynamicTable(EngineDataSet dsCollect, HtmlTable table)
  {
    HtmlTableRow[] rows = table.getTableRows();
    if(rows.length != 2)
      throw new ReportException("the report's templet dynamic table row count error");
    //--0 处理标题行
    //网页模板表格的表头的样子即是:产品编码 存货名称 规格 单位 [原料仓库 纸品仓库 ....] 合计.
    //在每一次查询的时候表头是须要动态的加入原料仓库,纸品仓库...
    //把dsCollect数据集中有的每一个store_字段当成一个td加入到网页模板中去
    HtmlTableCell[] cells = rows[0].getCells();
    rows[0].clearAllCells();
    for(int i=0; i<cells.length; i++)
    {
      String id = (String)cells[i].getCellInfo().get(Tag.ID);
      if(id == null)
        continue;
      else if(id.equals("preserve"))//preserve表示将模板的单元格保留的标志
        rows[0].addCell(cells[i]);
    }
    Column[] cols = dsCollect.getColumns();
    //设置单元格的属性:align="center" valign="middle" nowrap="true"
    Hashtable cellInfo = new Hashtable();
    cellInfo.put(Tag.ALIGN, Tag.ALIGN_CENTER);
    cellInfo.put(Tag.VALIGN, Tag.VALIGN_MIDDLE);
    cellInfo.put(Tag.NOWRAP, Tag.TRUE);
    //用dsCollect的列数做为循环长度.取得列名为store_开头的则表明这个字段是库存字段,
    //那么则作为一个td加进到这个tr中去:如: 产品编码 存货名称 规格 单位 [成品仓库 纸品仓库 ....] 合计
    HtmlTableCell titleTotalCell = null;
    for(int i=0; i<cols.length; i++)
    {
      String colName = cols[i].getColumnName();
      //如果是库房字段那么就新增添加一个td,td显示出来的是什么样的字:如:成品仓库.即:标题.是从数据集中取出来的
      if(colName.startsWith("store_"))
      {
        HtmlTableCell temp = new HtmlTableCell((Hashtable)cellInfo.clone());
        String tmpCaption = cols[i].getCaption();
        temp.addContent(tmpCaption);
        rows[0].addCell(temp);
      }
      else if (colName.equals("storeTotal"))
      {
        titleTotalCell = new HtmlTableCell((Hashtable)cellInfo.clone());
        String tmpCaption = cols[i].getCaption();
        titleTotalCell.addContent(tmpCaption);
      }
    }
    rows[0].addCell(titleTotalCell);
    //--2
    //处理数据行
    cells = rows[1].getCells();
    rows[1].clearAllCells();
    HtmlTableCell totalCell = null;
    for(int i=0; i < cells.length; i++)
    {
      String id = (String)cells[i].getCellInfo().get(Tag.ID);
      if(id == null)
        continue;
      else if(id.equals("preserve"))//preserve表示将模板的单元格保留的标志
        rows[1].addCell(cells[i]);
      else if(id.equals("total"))   //表示最后的合计字段，需要特殊处理
        totalCell = cells[i];
    }
    cellInfo.clear();
    cellInfo.put(Tag.ALIGN, Tag.ALIGN_RIGHT);
    cellInfo.put(Tag.VALIGN, Tag.VALIGN_MIDDLE);
    cellInfo.put(Tag.NOWRAP, Tag.TRUE);
    Hashtable valueInfo = new Hashtable();

    for(int i=0; i<cols.length; i++)
    {
      String colName = cols[i].getColumnName();
      if(colName.startsWith("store_"))
      {
        HtmlTableCell temp = new HtmlTableCell((Hashtable)cellInfo.clone());
        Hashtable value = (Hashtable)valueInfo.clone();
        value.put(Tag.FIELD, colName);
        temp.addValue(value);
        rows[1].addCell(temp);
      }
      /*else if (colName.equals("storeTotal"))
      {
        HtmlTableCell temp = new HtmlTableCell((Hashtable)cellInfo.clone());
        Hashtable value = (Hashtable)valueInfo.clone();
        value.put(Tag.FIELD, colName);
        totalCell.addValue(value);
      }*/
    }
    rows[1].addCell(totalCell);
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
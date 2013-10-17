package engine.erp.report.sale;


import java.util.Hashtable;
import java.util.ArrayList;
import java.math.BigDecimal;
import javax.servlet.ServletRequest;
import engine.report.util.*;
import engine.util.*;
//import engine.dataset.EngineDataSet;
import engine.dataset.*;
import engine.dataset.sql.QueryWhere;
import engine.report.event.ReportDataLoadingListener;
import com.borland.dx.dataset.*;

/**
 * <p>Title: 销售分栏帐</p>
 * <p>Description: 销售分栏帐</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 胡康宁
 * @version 1.0
 */

public class B_SaleReportFlz implements ReportDataLoadingListener
{

  //private final static String ASSORT_COLLECT_SQL = "{CALL PCK_STORE_REP.AssortCollect(?, '@','@','@','@','@')}";
  private final static String ASSORT_COLLECT_SQL = "select * from VW_XS_TDHW where 1=1 ? ";
  private final static String FLZ_SQL = "SELECT * FROM VW_SALE_FLZ_MX where flzid=";



  String s1 = "SELECT areacode,dqmc,dwdm,dwmc,SUM(nvl(sqje,0))sqje";//,sum(nvl(t.sl,0))asl,sum(nvl(t.jje,0))ajje
  String s2 = " FROM( ";

  String s3 = " SELECT t.areacode,t.dqmc,t.dwdm,t.dwmc,NULL sqje";//,sum(nvl(t.sl,0))asl,sum(nvl(t.jje,0))ajje
  String s4 = " FROM VW_XS_TDHW t ";
  String s5 ="  where t.tdrq>=to_date('";//2004-4-2
  String s6 = "','yyyy-mm-dd')  AND t.tdrq<to_date('";//2004-4-27
  String s7 = "','yyyy-mm-dd')";//---------
  String s71 = "  GROUP BY t.areacode,t.dqmc,t.dwdm,t.dwmc";
  String s8 = "  UNION ALL";

  String s9 = " SELECT areacode,dqmc,dwdm,dwmc,(sum(nvl(xsje,0))-sum(nvl(ssje,0)))sqje,";//NULL asl,NULL ajje
  String s10 = "FROM( ";
  String s11 = "SELECT f.areacode,f.dqmc,e.dwdm,e.dwmc,SUM(nvl(c.je,0))ssje,NULL xsje"
             + "  FROM xs_td a,xs_tdhw b,cw_xsjs c,cw_xsjshx d,dwtx e,dwdq f "
             + "   WHERE a.tdid=b.tdid    AND c.xsjsid=d.xsjsid AND b.tdhwid=d.tdhwid AND a.dwtxid=e.dwtxid AND e.dqh=f.dqh ";
  String s12 = " AND a.tdrq<to_date('";//2004-4-27
  String s13 = "','yyyy-mm-dd')  GROUP BY f.areacode,f.dqmc,e.dwdm,e.dwmc "
             + " UNION ALL "
             + " SELECT f.areacode,f.dqmc,e.dwdm,e.dwmc,NULL ssje,SUM(nvl(b.jje,0))xsje"
             + "  FROM xs_td a,xs_tdhw b,dwtx e,dwdq f "
             + "   WHERE a.tdid=b.tdid "
             + "   AND a.dwtxid=e.dwtxid "
             + "  AND e.dqh=f.dqh ";
  String s14 = "  AND a.tdrq<to_date('";//2004-4-27
  String s15 = "','yyyy-MM-DD')"
             + "  GROUP BY f.areacode,f.dqmc,e.dwdm,e.dwmc "
             + "  )GROUP BY areacode,dqmc,dwdm,dwmc "
             + "  )GROUP BY areacode,dqmc,dwdm,dwmc ";



  private EngineDataSet dsAssort = new EngineDataSet();
  private int inColumnCount = 0;  //收入字段的数量
  private int outColumnCount = 0; //支出的字段的数量

  private EngineDataSetProvider dataSetProvider =null;
  private EngineDataSetResolver dataSetResolver = null;

  protected String provideMethodName = "provideData";//提供数据的方法名称
  protected String resolveMethodName = "resovleData";//提交数据的方法名称

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
    String flzid =  whereQuery.getWhereValue("a$flzid");//分栏
    String lb =  whereQuery.getWhereValue("a$lb");//是否包括未记帐(在视图VW_XS_TDHW中,lb=1包括未记帐;lb=2不包括未记帐)
    String fgsid = whereQuery.getWhereValue("a$fgsid");
    if(flzid == null||flzid == "")
      throw new ReportException("必选分栏!");
    if(lb == null||lb == "")
      lb="1";//包括未记帐的单据
    String startDate = whereQuery.getWhereValue("a$ksrq");
    String endDate = whereQuery.getWhereValue("a$jsrq");
    EngineDataSet tmp = new EngineDataSet();
    try
    {
    setDataSetProperty(tmp,FLZ_SQL+flzid);
    tmp.open();
    }catch(Exception e){}
    int count = tmp.getRowCount();//新增的主列(即count*2列)
    ArrayList wzlbs = new ArrayList();
    ArrayList cpids = new ArrayList();
    RowMap detail = null;
    tmp.first();
    for(int i=0;i<tmp.getRowCount();i++)
    {
      detail = new RowMap();
      if(tmp.getValue("cpid").equals(""))
      {
        //wzlbid
        detail.put("wzmc",tmp.getValue("wzmc"));//新增的表头名
        detail.put("bm",tmp.getValue("bm"));
        wzlbs.add(detail);
      }else
      {
        //cpid
        detail.put("pm",tmp.getValue("pm"));
        detail.put("cpbm",tmp.getValue("cpbm"));//新增的表头名
        cpids.add(detail);
      }
      tmp.next();
    }
    StringBuffer sb = new StringBuffer();
    for(int i=0;i<wzlbs.size();i++)
    {
      String ss1="";
      String ss3="";
      detail = new RowMap();
      detail = (RowMap)wzlbs.get(i);
      s1=s1+",sum(nvl(wzsl"+i+",0))wzsl"+i+"sl,sum(nvl(wzjje"+i+",0))wzjje"+i;//,sum(nvl(t.sl,0))asl,sum(nvl(t.jje,0))ajje
      ss3 = s3+",sum(nvl(t.sl,0))wzsl"+i+",sum(nvl(t.jje,0))wzjje"+i;
      sb.append(ss3);
      sb.append(s4);
      sb.append(s5+startDate);
      sb.append(s6+endDate);
      sb.append(s7+" and t.cpbm like '"+detail.get("bm")+"%' and fgsid="+fgsid);
      sb.append(s71);
      sb.append(s8);
    }
    StringBuffer sbcpids = new StringBuffer();
    for(int i=0;i<cpids.size();i++)
    {
      String ss1="";
      String ss3="";
      detail = new RowMap();
      detail = (RowMap)cpids.get(i);
      s1=s1+",sum(nvl(cpsl"+i+",0))cpsl"+i+"sl,sum(nvl(cpjje"+i+",0))cpjje"+i;//,sum(nvl(t.sl,0))asl,sum(nvl(t.jje,0))ajje
      ss3 = s3+",sum(nvl(t.sl,0))wzsl"+i+",sum(nvl(t.jje,0))wzjje"+i;
      sb.append(ss3);
      sb.append(s4);
      sb.append(s5+startDate);
      sb.append(s6+endDate);
      sb.append(s7+" and t.cpbm ='"+detail.get("cpbm")+"' and fgsid="+fgsid);
      sb.append(s71);
      sb.append(s8);
    }
    StringBuffer allsb = new StringBuffer();
    allsb.append(s1);
    allsb.append(sb.toString());
    allsb.append(sbcpids.toString());
    allsb.append(s9);
    allsb.append(s10);
    allsb.append(s11);
    allsb.append(s12+endDate);
    allsb.append(s13);
    allsb.append(s14+endDate);
    allsb.append(s15);


    HtmlTable table  = getDynamicTable(templet);//得到动态表对象
    if(table == null)
      throw new ReportException("the report's templet has not table which id is dynamic");
    /*
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
           colname.equals("sxz")  || colname.equals("beforetotal"))
          continue;
        dsRep.dropColumn(cols[i]);
      }
    }
    else
      dsRep.setProvider(null);
    */
    //提取未横向汇总的数据
    dsAssort.setProvider(context.getDataSetProvider());
    //String startDate = whereQuery.getWhereValue("stock$ksrq");
    //String endDate = whereQuery.getWhereValue("stock$jsrq");
    //String storeid = whereQuery.getWhereValue("stock$storeid");
    //String chlbid = whereQuery.getWhereValue("stock$chlbid");
    //String fgsid = whereQuery.getWhereValue("stock$fgsid");
    //String sql = StringUtils.combine(ASSORT_COLLECT_SQL, "@",new String[]{startDate, endDate, storeid, chlbid, fgsid});
    String sql =allsb.toString();


    dsAssort.setQueryString(sql);
    if(dsAssort.isOpen())
      dsAssort.refresh();
    else
      dsAssort.openDataSet();
    //处理数据的横向汇总
    dsRep.openDataSet();

    //procDynamicData(dsRep, dsAssort);
    dsRep = dsAssort;////////////////////////////////////////////
    dsAssort.closeDataSet();
    //dsRep.setSort(new SortDescriptor[]{});
    //处理动态模板
    procDynamicTable(dsRep, table);//---------------------------------
  }



  protected void setDataSetProperty(EngineDataSet cds, String sql)  throws Exception
  {
    if(dataSetProvider == null)
      dataSetProvider = new EngineDataSetProvider();
    if(dataSetResolver == null)
      dataSetResolver = new EngineDataSetResolver();
    cds.setProvider(dataSetProvider);
    cds.setResolver(dataSetResolver);

    //设置数据提供和提交方法名称
    cds.setProvideMethodName(provideMethodName);
    cds.setResolveMethodName(resolveMethodName);

    if(sql != null)
      cds.setQueryString(sql);
  }

  /**
   * 处理数据的横向汇总
   * @param dsCollect 汇总的数据
   * @param dsAssort 未横向汇总的数据

  private void procDynamicData(EngineDataSet dsCollect, EngineDataSet dsAssort)
  {
    //cpid, cpbm, product, sxz, beforetotal, billtype(i/o), sfdjlbid, lbmc, sl
    if(dsCollect.hasColumn("cpid") == null)
      dsCollect.addColumn(dsAssort.getColumn("cpid").cloneColumn());
    if(dsCollect.hasColumn("cpbm") == null)
      dsCollect.addColumn(dsAssort.getColumn("cpbm").cloneColumn());
    if(dsCollect.hasColumn("product") == null)
      dsCollect.addColumn(dsAssort.getColumn("product").cloneColumn());
    if(dsCollect.hasColumn("sxz") == null)
      dsCollect.addColumn(dsAssort.getColumn("sxz").cloneColumn());
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
        dsCollect.setValue("sxz", dsAssort.getValue("sxz"));
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
      EngineRow row = new EngineRow(dsCollect, new String[]{"cpid", "sxz"});
      row.setValue(0, dsAssort.getValue("cpid"));
      row.setValue(1, dsAssort.getValue("sxz"));
      if(!dsCollect.locate(row, Locate.FIRST))
      {
        dsCollect.insertRow(false);
        dsCollect.setValue("cpid", dsAssort.getValue("cpid"));
        dsCollect.setValue("cpbm", dsAssort.getValue("cpbm"));
        dsCollect.setValue("product", dsAssort.getValue("product"));
        dsCollect.setValue("sxz", dsAssort.getValue("sxz"));
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
  */
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
    HtmlTable[] tables = templet.getTables();//得到模板文件里所有的表对象数组
    for(int i=0; i<tables.length; i++)
    {
      String tableid = (String)tables[i].getTableInfo().get(Tag.ID);//得到table 的id属性质
      if(tableid != null && tableid.equals("dynamic"))
        return tables[i];//返回id='dynamic'的表对象
    }
    return null;//没有id='dynamic'的表对象
  }
}
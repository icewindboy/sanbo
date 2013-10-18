package engine.erp.report.sale;
import engine.action.BaseAction;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.report.util.HtmlTable;
import engine.report.util.Tag;
import engine.dataset.EngineDataSet;
import engine.dataset.sql.QueryWhere;
import engine.report.event.ReportDataLoadingListener;
/**
 * <p>Title: 销售--销售日报表--</p>
 * <p>Description: 销售--销售日报表--<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company:engine </p>
 * @version 1.0
 */
public final class  B_Sale_Day_Income  implements ReportDataLoadingListener//实现报表模版的监听器（数据在装载时的查询条件）
{
  private static final String SALE_CUSTOMER_COLLECT
        ="SELECT a.rq,a.dwtxid,a.je,a.zt,c.dwmc,c.dwdm ,a.fgsid ,a.xsjsid ,a.hxje,a.whxje,d.areacode,d.dqmc"
        + " FROM cw_xsjs a,dwtx c ,dwdq d"
        + " WHERE  a.dwtxid=c.dwtxid and c.dqh=d.dqh  ? "
        + " GROUP BY a.rq,a.dwtxid,a.je,a.zt,c.dwmc,c.dwdm ,a.fgsid ,a.xsjsid ,a.hxje,a.whxje,d.areacode,d.dqmc "
        + " ORDER BY a.rq,c.dwdm ";//   --收入日报表---按客户汇总   --zt:0=未审核;1=已审核;
  private static final String SALE_YH_COLLECT
         = "SELECT a.rq,a.yh,a.zh,a.je,a.zt ,a.fgsid ,a.xsjsid ,a.hxje,a.whxje"
         + "  FROM cw_xsjs a "
         + "   WHERE a.djxz='1'  ? "
         + "  GROUP BY a.rq,a.yh,a.zh,a.zt ,a.fgsid ,a.xsjsid,a.je ,a.hxje,a.whxje"
         + "  ORDER BY a.yh ";  //---收入日报表---按帐号汇总
  private static final String SALE_SALER_COLLECT
         =" SELECT a.rq,a.je,a.zt,c.xm,a.personid,d.mc ,a.fgsid,a.xsjsid  ,a.hxje,a.whxje"
         + " FROM cw_xsjs a,emp c,bm d "
         + " WHERE a.personid=c.personid(+) AND a.deptid=d.deptid(+)  ? "
         + " GROUP BY a.rq,a.je,a.zt,c.xm,a.personid,d.mc ,a.fgsid,a.xsjsid  ,a.hxje,a.whxje"
         + " ORDER BY a.personid ";// --收入日报表---按业务员汇总   --zt:0=未审核;1=已审核;9=审批中(未审)
  private static final String SALE_JSFS_COLLECT
         = " SELECT a.rq,a.je,a.zt,c.jsfs ,a.fgsid ,a.xsjsid ,a.hxje,a.whxje"
         + " FROM cw_xsjs a,jsfs c "
         + " WHERE  a.jsfsid=c.jsfsid(+)  ? "
         + " GROUP BY a.rq,a.je,a.zt,c.jsfs  ,a.fgsid ,a.xsjsid ,a.hxje,a.whxje"
         + " ORDER BY c.jsfs "; // --收入日报表---按结算方式  --zt:0=未审核;1=已审核;9=审批中(未审)
  /**
   *
   * @param req
   * @param templet
   * @param context页面显示控件类
   * @param dsRep数据集
   * @param where条件
   */
  public void dataLoading(ServletRequest req, TempletData templet, ContextData context,
                          EngineDataSet dsRep, QueryWhere where)
  {
    String dataSrc = where.getWhereValue("A$ZT");//得到where条件页面数据来源的条件
    String collectType = where.getWhereValue("A$HZFF");//得到where条件页面汇总方法的条件
    //把这两个where条件设置为空
    //where.putWhereValue("A$ZT", "");
    where.putWhereValue("A$ZT", dataSrc);
    where.putWhereValue("A$HZFF", "");
    //设置小计列名为null既不进行小计
    context.setGroupColumn(null);
    HtmlTable[] tables = templet.getTables();//得到模版htm中的表，返回数组

    String whereSql = where.getWhereQuery();//得到页面的查询条件
    String SQL = null;
    //根据客户汇总
    if(collectType.equals("0"))
    {
      SQL =  BaseAction.combineSQL(SALE_CUSTOMER_COLLECT, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      context.clearTemps();
      context.addShowSumColumn("dwmc");
      context.addShowSumColumn("je");
      context.addShowSumColumn("hxje");
      context.addShowSumColumn("whxje");
    }
    //根据银行汇总
   else if(collectType.equals("1")){
      SQL = BaseAction.combineSQL(SALE_YH_COLLECT, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      context.clearTemps();
      context.addShowSumColumn("yh");
      context.addShowSumColumn("zh");
      context.addShowSumColumn("je");
      context.addShowSumColumn("hxje");
      context.addShowSumColumn("whxje");
    }
    //根据业务员汇总
    else if(collectType.equals("2")){
      SQL = BaseAction.combineSQL(SALE_SALER_COLLECT, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      context.clearTemps();
      context.addShowSumColumn("mc");
      context.addShowSumColumn("xm");
      context.addShowSumColumn("je");
      context.addShowSumColumn("hxje");
      context.addShowSumColumn("whxje");
    }
    //根据银行汇总
   else if(collectType.equals("3")){
      SQL = BaseAction.combineSQL(SALE_JSFS_COLLECT, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      context.clearTemps();
      context.addShowSumColumn("jsfs");
      context.addShowSumColumn("je");
      context.addShowSumColumn("hxje");
      context.addShowSumColumn("whxje");
    }
    dsRep.setQueryString(SQL);
    if(dsRep.isOpen())
      dsRep.refresh();
    else
      dsRep.open();//打开数据集
    //进货单存货汇总
    if(collectType.equals("0"))
    {
      /**
       *循环模版中的表，根据数据来源和汇总方式确定显示的表
       */
      for(int i=0; i<tables.length; i++)
      {
        String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
        if(name==null)//第一个Table没有设置名字，但这个表要显示
          continue;
        if(name.equals("dwmincen") )
          tables[i].setEnable();//设为显示
        else
          tables[i].setDisable();//设为不显示
      }
      //
    }
    //进货单供应商汇总
   else if( collectType.equals("1"))
   {
      /**
      *循环模版中的表，根据数据来源和汇总方式确定显示的表
       */
     for(int i=0; i<tables.length; i++)
     {
       String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
       if(name==null)//第一个Table没有设置名字，但这个表要显示
          continue;
       if(name.equals("bank"))
         tables[i].setEnable();//设为显示
       else
         tables[i].setDisable();//设为不显示
     }
    }
    //发票存货汇总
  else  if(collectType.equals("2"))
    {
      /**
       *循环模版中的表，根据数据来源和汇总方式确定显示的表
       */
     for(int i=0; i<tables.length; i++)
     {
       String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
       if(name==null)//第一个Table没有设置名字，但这个表要显示
          continue;
       if(name.equals("saler"))
         tables[i].setEnable();//设为显示
       else
         tables[i].setDisable();//设为不显示
     }
    }
    //发票供应山汇总
   else if(collectType.equals("3"))
    {
      /**
       *循环模版中的表，根据数据来源和汇总方式确定显示的表
       */
     for(int i=0; i<tables.length; i++)
     {
       String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
       if(name==null)//第一个Table没有设置名字，但这个表要显示
          continue;
       if(name.equals("jsfs"))//设为显示
         tables[i].setEnable();//设为显示
       else
         tables[i].setDisable();//设为不显示
     }
    }
    where.putWhereValue("A$ZT", dataSrc);
    where.putWhereValue("A$HZFF", collectType);
  }
}
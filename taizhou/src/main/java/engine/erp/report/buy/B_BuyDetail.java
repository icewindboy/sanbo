package engine.erp.report.buy;

import engine.action.BaseAction;
import javax.servlet.ServletRequest;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.report.util.HtmlTable;
import engine.report.util.Tag;
import engine.dataset.EngineDataSet;
import engine.dataset.sql.QueryWhere;
import engine.report.event.ReportDataLoadingListener;
import engine.report.event.ReportDataLoadedListener;

/**
 * <p>Title: 采购--采购汇总报表（四种不同方式显示）</p>
 * <p>Description: 采购--采购汇总报表（四种不同方式显示）<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_BuyDetail implements ReportDataLoadingListener, ReportDataLoadedListener//实现报表模版的监听器（数据在装载时的查询条件）
{
  private static final String BUY_JHD_PRODUCT_COLLECT//根据进货单为数据来源并且以存货汇总的SQL语句
      = "SELECT  sum(nvl(b.sl,0)) sl, sum(nvl(b.je,0)) je,b.dmsxid, "
      + "decode(sum(nvl(b.sl,0)), 0 ,0, sum(nvl(b.je,0))/sum(nvl(b.sl,0))) dj, "
      + "c.cpbm, c.pm, c.gg, c.jldw, c.hsdw, d.sxz  "
      + "FROM  cg_htjhd a, cg_htjhdhw b, vw_kc_dm_exist c, kc_dmsx d "
      + "WHERE a.jhdid = b.jhdid AND b.cpid = c.cpid AND b.dmsxid=d.dmsxid(+) ? "
      + "GROUP BY c.cpbm, c.pm, c.gg, c.jldw, c.hsdw, d.sxz,b.dmsxid ORDER BY c.cpbm ";
  private static final String BUY_JHD_CORP_COLLECT//根据进货单为数据来源并且以供应商汇总的SQL语句
      = "SELECT sum(nvl(b.sl,0)) sl, sum(nvl(b.je,0)) je, c.dwmc, c.dwdm, a.dwtxid "
      + "FROM  cg_htjhd a, cg_htjhdhw b,  dwtx c "
      + "WHERE a.jhdid = b.jhdid AND a.dwtxid = c.dwtxid ? "
      + "GROUP BY c.dwmc, c.dwdm, a.dwtxid ORDER BY c.dwdm";
  private static final String BUY_FP_PRODUCT_COLLECT//根据发票为数据来源并且以存货汇总的SQL语句
      = "SELECT  sum(nvl(b.sl,0)) sl, sum(nvl(b.jshj,0)) jshj, b.dmsxid, sum(nvl(b.se,0)) se, "
      + "sum(nvl(b.je,0)) je, b.cpid, c.cpbm, c.pm, c.gg, c.jldw, c.hsdw, d.sxz "
      + "FROM (SELECT a.*, a.kprq jhrq FROM cw_cgfp a) a,cw_cgfpmx b, vw_kc_dm_exist c, kc_dmsx d "
      + "WHERE a.cgfpid=b.cgfpid AND b.cpid = c.cpid AND b.dmsxid=d.dmsxid(+) ? "
      + "GROUP BY a.fgsid, a.kprq, a.zt, b.cpid, c.cpbm, c.pm, c.gg, c.jldw, c.hsdw, d.sxz,b.dmsxid ORDER BY c.cpbm";
  private static final String BUY_FP_CORP_COLLECT//根据发票为数据来源并且以供应商汇总的SQL语句
      = "SELECT sum(nvl(b.sl,0)) sl, sum(nvl(b.jshj,0)) jshj, "
      + "sum(nvl(b.se,0)) se, sum(nvl(b.je,0)) je, c.dwmc, c.dwdm, a.dwtxid "
      + "FROM (SELECT a.*, a.kprq jhrq FROM cw_cgfp a) a ,cw_cgfpmx b,dwtx c "
      + "WHERE a.cgfpid=b.cgfpid AND a.dwtxid=c.dwtxid ? "
      + "GROUP BY c.dwmc, c.dwdm, a.dwtxid ORDER BY c.dwdm ";

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
    String dataSrc = where.getWhereValue("A$TYPE");//得到页面选择的数据来源
    String collectType = where.getWhereValue("A$HZFF");//得到where条件（页面的汇总方法）
    String khlx = where.getWhereValue("A$KHLX");//得到页面的客户类型
    //把这两个where条件设置为空
    where.putWhereValue("A$TYPE", "");
    where.putWhereValue("A$HZFF", "");
    if(dataSrc.equals("1"))//如果数据来源是按照发票来的，把查询条件客户类型设为空
      where.putWhereValue("A$KHLX","");
    //设置小计列名为null既不进行小计
    context.setGroupColumn(null);
    HtmlTable[] tables = templet.getTables();//得到模版htm中的表，返回数组

    String whereSql = where.getWhereQuery();//得到页面的查询条件
    String SQL = null;
    //根据进货单为数据来源并且以存货汇总
    if(dataSrc.equals("") && collectType.equals(""))
    {
      SQL =  BaseAction.combineSQL(BUY_JHD_PRODUCT_COLLECT, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      context.clearTemps();
      context.addShowSumColumn("sl");
      context.addShowSumColumn("je");
    }
    //根据进货单为数据来源并且以供应商汇总
    if(dataSrc.equals("") && collectType.equals("1")){
      SQL = BaseAction.combineSQL(BUY_JHD_CORP_COLLECT, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      context.clearTemps();
      context.addShowSumColumn("sl");
      context.addShowSumColumn("je");
    }
    //根据发票为数据来源并且以存货汇总
    if(dataSrc.equals("1") && collectType.equals("")){
      SQL = BaseAction.combineSQL(BUY_FP_PRODUCT_COLLECT, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      context.clearTemps();
      context.addShowSumColumn("sl");
      context.addShowSumColumn("je");
      context.addShowSumColumn("jshj");
      context.addShowSumColumn("se");
    }
    //根据票为数据来源并且以供应商汇总
    if(dataSrc.equals("1") && collectType.equals("1")){
      SQL = BaseAction.combineSQL(BUY_FP_CORP_COLLECT, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      context.clearTemps();
      context.addShowSumColumn("sl");
      context.addShowSumColumn("je");
      context.addShowSumColumn("jshj");
      context.addShowSumColumn("se");
    }
    dsRep.setQueryString(SQL);
    if(dsRep.isOpen())
      dsRep.refresh();
    else
      dsRep.open();//打开数据集
    //进货单存货汇总
    if(dataSrc.equals("") && collectType.equals(""))
    {
      /**
       *循环模版中的表，根据数据来源和汇总方式确定显示的表
       */
      for(int i=0; i<tables.length; i++)
      {
        String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
        if(name==null)//第一个Table没有设置名字，但这个表要显示
          continue;
        if(name.equals("jhdchhz") )
          tables[i].setEnable();//设为显示
        else
          tables[i].setDisable();//设为不显示
      }
      //
    }
    //进货单供应商汇总
    if(dataSrc.equals("") && collectType.equals("1"))
   {
      /**
      *循环模版中的表，根据数据来源和汇总方式确定显示的表
       */
     for(int i=0; i<tables.length; i++)
     {
       String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
       if(name==null)//第一个Table没有设置名字，但这个表要显示
          continue;
       if(name.equals("jhdgyshz"))
         tables[i].setEnable();//设为显示
       else
         tables[i].setDisable();//设为不显示
     }
    }
    //发票存货汇总
    if(dataSrc.equals("1") && collectType.equals(""))
    {
      /**
       *循环模版中的表，根据数据来源和汇总方式确定显示的表
       */
     for(int i=0; i<tables.length; i++)
     {
       String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
       if(name==null)//第一个Table没有设置名字，但这个表要显示
          continue;
       if(name.equals("fpchhz"))
         tables[i].setEnable();//设为显示
       else
         tables[i].setDisable();//设为不显示
     }
    }
    //发票供应山汇总
    if(dataSrc.equals("1") && collectType.equals("1"))
    {
      /**
       *循环模版中的表，根据数据来源和汇总方式确定显示的表
       */
     for(int i=0; i<tables.length; i++)
     {
       String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
       if(name==null)//第一个Table没有设置名字，但这个表要显示
          continue;
       if(name.equals("fpgyshz"))//设为显示
         tables[i].setEnable();//设为显示
       else
         tables[i].setDisable();//设为不显示
     }
    }
    //生成SQL语句后把这个两个临时变量在推入
    where.putWhereValue("A$TYPE", dataSrc);
    where.putWhereValue("A$HZFF", collectType);
    if(dataSrc.equals("1"))
      where.putWhereValue("A$KHLX", khlx);
  }
  public void dataLoaded(ServletRequest parm1, TempletData parm2, ContextData parm3, EngineDataSet parm4)
 {
   QueryWhere where = parm3.getQueryWhere();
   String khlx = where.getWhereValue("a$khlx");//查询条件客户类型
   String ksrq = where.getWhereValue("a$jhrq$a");//查询条件开始日期
   String jsrq = where.getWhereValue("a$jhrq$b");//查询条件结束日期
   String type = where.getWhereValue("a$type");//查询条件数据来源
   String zt = where.getWhereValue("a$zt");
   Object[] objects = parm2.getTablesAndOther();
   for(int i=0; i<objects.length; i++)
   {
     if(objects[i] instanceof String)
     {
       String s = (String)objects[i];
       if(s.startsWith("<SCRIPT LANGUAGE='javascript' id='where'>"))
       {
         s = "<SCRIPT LANGUAGE='javascript' id='where'>var ksrq='"+ksrq+"'; var jsrq='"+jsrq+"'; khlx='"+khlx+"'; var type='"+type+"'; var zt='"+zt+"';</SCRIPT>";
         parm2.setOther(i, s);
       }
     }
   }
  }
}
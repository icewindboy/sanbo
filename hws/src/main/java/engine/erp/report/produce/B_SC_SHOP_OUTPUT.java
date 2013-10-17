package engine.erp.report.produce;

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
 * <p>Title: 生产--产量统计报表（三种不同条件显示不同的报表）</p>
 * <p>Description: 生产--产量统计报表（三种不同条件显示不同的报表）<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_SC_SHOP_OUTPUT implements ReportDataLoadingListener//实现报表模版的监听器（数据在装载时的查询条件）
{
  private static final String SC_JGD_OUTPUT//根据生产加工单查询的SQL语句
      = "SELECT sum(nvl(b.sl,0)) sl, c.scydw, c.cpbm, c.pm, c.gg, d.sxz "
      + "FROM sc_jgd a, sc_jgdmx b, vw_kc_dm_exist c, kc_dmsx d "
      + "WHERE a.jgdid=b.jgdid AND b.cpid = c.cpid AND b.dmsxid=d.dmsxid(+) ? "
      + "GROUP BY c.cpbm, c.pm, c.gg, c.scydw, d.sxz ORDER BY c.cpbm ";
  private static final String SC_ZZSHD_OUTPUT//根据自制收货单查询的SQL语句
      = "SELECT  sum(nvl(b.sl,0)) sl, sum(nvl(b.je,0)) je, "
      + "decode(sum(nvl(b.sl,0)), 0 ,0, sum(nvl(b.je,0))/sum(nvl(b.sl,0))) dj, "
      + "c.cpbm, c.pm, c.gg, c.scydw, d.sxz  "
      + "FROM  (SELECT a.*, a.sfrq rq FROM kc_sfdj a) a, kc_sfdjmx b, vw_kc_dm_exist c, kc_dmsx d "
      + "WHERE a.sfdjid = b.sfdjid AND a.djxz='3' AND b.cpid = c.cpid AND b.dmsxid=d.dmsxid(+) ? "
      + "GROUP BY c.cpbm, c.pm, c.gg, c.scydw, d.sxz ORDER BY c.cpbm ";
  private static final String SC_GRGZL_OUTPUT//根据发票为数据来源并且以存货汇总的SQL语句
      = "SELECT a.gx, sum(nvl(a.sl,0)) sl, sum(nvl(a.jjgz,0)) je, "
      + "c.cpbm, c.pm, c.gg, c.scydw, d.mc, e.sxz "
      + "FROM vw_sc_workload_detail a, vw_kc_dm_exist c, bm d, kc_dmsx e "
      + "WHERE a.cpid=c.cpid AND a.dmsxid=e.dmsxid AND a.deptid=d.deptid ? "
      + "GROUP BY a.gx, c.cpbm, c.pm, c.gg, c.scydw, d.mc, e.sxz ORDER BY d.mc, c.cpbm ";
  private static String storeid = null;
  private static String deptid = null;
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
    String dataSrc = where.getWhereValue("A$TYPE");//得到页面所选择的统计依据
    storeid = where.getWhereValue("A$STOREID");
    deptid = where.getWhereValue("A$DEPTID");
    //把统计依据where条件设置为空
    where.putWhereValue("A$TYPE", "");
    //设置小计列名为null既不进行小计
    context.setGroupColumn(null);
    HtmlTable[] tables = templet.getTables();//得到模版htm中的表，返回数组

    String whereSql = where.getWhereQuery();//得到页面的查询条件
    String SQL = null;
    //根据自制收货单统计
    if(dataSrc.equals(""))
    {
      SQL =  BaseAction.combineSQL(SC_ZZSHD_OUTPUT, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      context.clearTemps();
      context.addShowSumColumn("sl");
      context.addShowSumColumn("je");
    }
    //根据生产加工单统计
    if(dataSrc.equals("1")){
      SQL = BaseAction.combineSQL(SC_JGD_OUTPUT, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      where.putWhereValue("A$STOREID","");
      context.clearTemps();
      context.addShowSumColumn("sl");
    }
    //根据工人工作量统计
    if(dataSrc.equals("2")){
      SQL = BaseAction.combineSQL(SC_GRGZL_OUTPUT, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      where.putWhereValue("A$STOREID","");
      context.clearTemps();
      context.addShowSumColumn("sl");
      context.addShowSumColumn("je");
    }
    dsRep.setQueryString(SQL);
    if(dsRep.isOpen())
      dsRep.refresh();
    else
      dsRep.open();//打开数据集
    //根据自制收货单显示的Table
    if(dataSrc.equals(""))
    {
      /**
       *循环模版中的表，根据数据来源和汇总方式确定显示的表
       */
      for(int i=0; i<tables.length; i++)
      {
        String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
        if(name==null)//第一个Table没有设置名字，但这个表要显示
          continue;
        if(name.equals("zzshd"))
          tables[i].setEnable();//设为显示
        else
          tables[i].setDisable();//设为不显示
      }
      //
    }
    //进货单供应商汇总
    if(dataSrc.equals("1"))
    {
      /**
       *循环模版中的表，根据数据来源和汇总方式确定显示的表
       */
      for(int i=0; i<tables.length; i++)
      {
        String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
        if(name==null)//第一个Table没有设置名字，但这个表要显示
          continue;
        if(name.equals("jgd"))
          tables[i].setEnable();//设为显示
        else
          tables[i].setDisable();//设为不显示
      }
    }
    //发票存货汇总
    if(dataSrc.equals("2"))
    {
      /**
       *循环模版中的表，根据数据来源和汇总方式确定显示的表
       */
      for(int i=0; i<tables.length; i++)
     {
       String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
       if(name==null)//第一个Table没有设置名字，但这个表要显示
         continue;
       if(name.equals("grgzl"))
         tables[i].setEnable();//设为显示
       else
         tables[i].setDisable();//设为不显示
     }
    }
    where.putWhereValue("A$TYPE", dataSrc);
    where.putWhereValue("A$STOREID", storeid);
    where.putWhereValue("A$DEPTID", deptid);
  }
}
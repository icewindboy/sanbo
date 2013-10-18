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
import engine.report.event.ReportDataLoadedListener;
import com.borland.dx.dataset.*;

/**
 * <p>Title: 生产--工人工作量统计（根据不同方式显示1.员工+产品2.日期+产品）</p>
 * <p>Description: 生产--工人工作量统计<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_SC_WORKER_WORKLOAD implements ReportDataLoadingListener, ReportDataLoadedListener//实现报表模版的监听器（数据在装载时的查询条件）
{
  private static final String SC_YG_WORKLOAD//根据员工+产品显示的SQL
      = "SELECT NULL rq, a.deptid bmid, a.personid id, a.gx, sum(nvl(a.sl,0)) sl, sum(nvl(a.jjgz,0)) jjgz, "
      + "b.cpbm, b.pm,b.gg, b.jldw, d.xm, d.bm, c.mc,  c.dm, e.sxz  "
      + "FROM vw_sc_workload_detail a, vw_kc_dm_exist b,  bm c, emp d, kc_dmsx e "
      + "WHERE a.cpid = b.cpid  AND  a.deptid=c.deptid  AND a.personid=d.personid AND a.dmsxid=e.dmsxid(+) ? "
      + "GROUP BY a.rq, a.deptid, a.personid, a.gx, b.cpbm, b.pm, b.gg, b.jldw, c.mc, c.dm, d.bm, d.xm,e.sxz ";
  private static final String SC_RQ_WORKLOAD//根据自制收货单查询的SQL语句
      = "SELECT a.rq, a.deptid bmid, a.personid id, a.gx, sum(nvl(a.sl,0)) sl, sum(nvl(a.jjgz,0)) jjgz, "
      + "b.cpbm, b.pm,b.gg, b.jldw, null xm, null bm, c.mc,  c.dm, e.sxz  "
      + "FROM vw_sc_workload_detail a, vw_kc_dm_exist b,  bm c, kc_dmsx e "
      + "WHERE a.cpid = b.cpid  AND  a.deptid=c.deptid AND a.dmsxid=e.dmsxid(+) ? "
      + "GROUP BY a.rq, a.deptid, a.personid, a.gx, b.cpbm, b.pm, b.gg, b.jldw, c.mc, c.dm,e.sxz ";
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
    String dataSrc = where.getWhereValue("A$TJFF");//得到页面所选择的统计方法
    //把统计方法where条件设置为空
    where.putWhereValue("A$TJFF", "");
    //设置小计列名为null既不进行小计
    context.setGroupColumn(null);
    HtmlTable[] tables = templet.getTables();//得到模版htm中的表，返回数组

    String whereSql = where.getWhereQuery();//得到页面的查询条件
    String SQL = null;
    //根据员工+产品显示
    if(dataSrc.equals(""))
    {
      SQL =  BaseAction.combineSQL(SC_YG_WORKLOAD, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      context.clearTemps();
      context.addShowSumColumn("sl");
      context.addShowSumColumn("jjgz");
      dsRep.setQueryString(SQL);
      if(dsRep.isOpen())
        dsRep.refresh();
      else
        dsRep.open();//打开数据集
      dsRep.setSort(new SortDescriptor("", new String[]{"dm", "bm", "cpbm"}, new boolean[]{false,false,false}, null, 0));
      for(int i=0; i<tables.length; i++)
      {
        String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
        if(name==null)//第一个Table没有设置名字，但这个表要显示
          continue;
        if(name.equals("ygxs"))
          tables[i].setEnable();//设为显示
        else
          tables[i].setDisable();//设为不显示
      }
    }
    //根据日期+产品显示
    if(dataSrc.equals("1")){
      SQL =  BaseAction.combineSQL(SC_RQ_WORKLOAD, "?", new String[]{whereSql.length()==0 ? "" : " AND " + whereSql});
      context.clearTemps();
      context.addShowSumColumn("sl");
      context.addShowSumColumn("jjgz");
      dsRep.setQueryString(SQL);
      if(dsRep.isOpen())
        dsRep.refresh();
      else
        dsRep.open();//打开数据集
      dsRep.setSort(new SortDescriptor("", new String[]{"rq", "dm","cpbm"}, new boolean[]{false,false,false}, null, 0));
      for(int i=0; i<tables.length; i++)
      {
        String name = (String)tables[i].getTableInfo().get(Tag.NAME);//得到表的名字name
        if(name==null)//第一个Table没有设置名字，但这个表要显示
          continue;
        if(name.equals("rqxs"))
          tables[i].setEnable();//设为显示
        else
          tables[i].setDisable();//设为不显示
      }
    }
    where.putWhereValue("A$TJFF", dataSrc);
  }
  public void dataLoaded(ServletRequest parm1, TempletData parm2, ContextData parm3, EngineDataSet parm4)
 {
   QueryWhere where = parm3.getQueryWhere();
   String ksrq = where.getWhereValue("a$rq$a");//查询条件开始日期
   String jsrq = where.getWhereValue("a$rq$b");//查询条件结束日期
   Object[] objects = parm2.getTablesAndOther();
   for(int i=0; i<objects.length; i++)
   {
     if(objects[i] instanceof String)
     {
       String s = (String)objects[i];
       if(s.startsWith("<SCRIPT LANGUAGE='javascript' id='where'>"))
       {
         s = "<SCRIPT LANGUAGE='javascript' id='where'>var ksrq='"+ksrq+"'; var jsrq='"+jsrq+"'; </SCRIPT>";
         parm2.setOther(i, s);
       }
     }
   }
  }
}
package engine.erp.report.sale;

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
import engine.report.event.TempletProvideResponse;
import engine.report.event.TempletAfterProvideListener;

/**
 * <p>Title: 业务员奖金</p>
 * <p>Description: 业务员奖金</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author
 * @version 1.0
 */

public class B_SalerPrize implements TempletAfterProvideListener
{
  //private final static String FIELD_SQL = "SELECT * FROM rl_gzkxsz ";
  private EngineDataSet dsFieldTable = new EngineDataSet();
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
  /**
   * 报表数据打开时的事件调用的方法。
   * 1:组装SQL语句，提取数据, 2:处理横向汇总的数据, 3:处理横向汇总的模板
   * @param request WEB请求
   * @param templet 报表模板对象
   * @param context 报表上下文对象
   * @param dsRep   报表数据对象对象
   * @param whereQuery 查询条件的所有值
   */
  public void templetAfterProvide(ServletRequest req, TempletData templetData,
                                  ContextData context, TempletProvideResponse templetRes)
  {
    if(dsFieldTable.getProvider() == null)
      dsFieldTable.setProvider(context.getDataSetProvider());
    dsFieldTable.setQueryString("select * from xs_jjgssz ORDER BY PXH");
    if(dsFieldTable.isOpen())
      dsFieldTable.refresh();
    else
      dsFieldTable.openDataSet();
    templetData.clearFields();
    StringBuffer bufFields = new StringBuffer();
    StringBuffer bufTitles = new StringBuffer();
    StringBuffer bufTds = new StringBuffer();
    bufFields.append("<field name='dm' caption='部门编码' group='true' datatype='varchar'>");
    bufFields.append("<field name='ygbm' caption='员工编码' group='true' datatype='varchar'>");
    bufFields.append("<field name='mc' caption='部门' group='true' datatype='varchar'>");
    bufFields.append("<field name='xm' caption='员工姓名' group='true' datatype='varchar'>");
    dsFieldTable.first();
    for(int i=0; i<dsFieldTable.getRowCount(); i++)
    {
      String fname = dsFieldTable.getValue("dyzdm");
      String mc = dsFieldTable.getValue("mc");
      String lx = dsFieldTable.getValue("lx");
      //<field name="xm" caption="姓名" group="true" datatype="varchar">
      bufFields.append("<field name='").append(fname).append("'");
      bufFields.append(" caption='").append(mc).append("'datatype='");
      bufFields.append(lx.equals("4")? "number' sum='true'>": "varchar'>");
      //<td align="center"  nowrap="true" field="xm">业务员</td>
      bufTitles.append("<td align='center' nowrap='true' field='");
      bufTitles.append(fname).append("'>");
      bufTitles.append(mc).append("</td>");
      //<td nowrap="true"><value field="cpbm"></td>
      bufTds.append("<td nowrap='true' align='right'><value field='").append(fname).append("'");
      bufTds.append("></td>");
      dsFieldTable.next();
    }
    HtmlTable table = getDynamicTable(templetData);
    templetData.addFields("a", bufFields.toString());
    HtmlTableRow[] rows = table.getTableRows();
    if(rows.length != 2)
      throw new ReportException("the report's templet dynamic table row count error");
    //--0 处理标题行
    HtmlTableCell[] cells0 = rows[0].getCells();
    rows[0].clearAllCells();
    for(int i=0; i<cells0.length; i++)
    {
      String id = (String)cells0[i].getCellInfo().get(Tag.ID);
      if(id == null)
        continue;
      else if(id.equals("preserve"))//preserve表示将模板的单元格保留的标志
        rows[0].addCell(cells0[i]);
    }
    rows[0].addCells(bufTitles.toString());
    //--1
    HtmlTableCell[] cells1 = rows[1].getCells();
    rows[1].clearAllCells();
    for(int i=0; i<cells1.length; i++)
    {
      String id = (String)cells1[i].getCellInfo().get(Tag.ID);
      if(id == null)
        continue;
      else if(id.equals("preserve"))//preserve表示将模板的单元格保留的标志
        rows[1].addCell(cells1[i]);
    }
    rows[1].addCells(bufTds.toString());
    templetRes.needInit(true);
  }
}
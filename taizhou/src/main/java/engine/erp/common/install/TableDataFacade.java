package engine.erp.common.install;

import java.util.Map;
import java.util.Hashtable;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.OutputStream;
import java.io.PrintWriter;

import engine.dataset.*;
import engine.dataset.sql.Expression;
import engine.util.log.LogHelper;
import engine.util.StringUtils;
import engine.report.util.*;
import engine.report.util.Tag;

import com.borland.dx.dataset.Column;
//import

/**
 * <p>Title: 数据库数据的升级(通常是基础数据) </p>
 * <p>Description: 数据库数据的升级(通常是基础数据)</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 江海岛
 * @version 1.0
 */

public final class TableDataFacade implements Tag
{
  public final static String GREATER_HTML = "#&gt;";
  public final static String LESS_HTML = "#&lt;";
  //双引号的替换
  public final static String DOUBLE_COMMA_HTML = "#&quot;";
  public final static String DOUBLE_COMMA = "\"";
  //
  public final static String COMMA_HTML = "#&quo;";
  public final static String COMMA = "'";

  //数据提供者，用于提取数据
  private EngineDataSetProvider provider = null;
  //数据更新者，用于更新数据
  private EngineDataSetResolver resolver = null;
  //需要升级的数据
  private TempletData data = null;
  //写日志对象
  private LogHelper log = LogHelper.getLogHelper(getClass());
  //
  private static final String TABLE_SQL = "SELECT * FROM ";
  /**
   * 构造函数，创建一个数据升级的实例
   * @param provider 数据提供者，用于提取数据
   * @param resolver 数据更新者，用于更新数据
   * @param data 需要升级的数据对象
   */
  private TableDataFacade(EngineDataSetProvider provider, EngineDataSetResolver resolver)
  {
    this.provider = provider;
    this.resolver = resolver;
  }

  /**
   * 执行数据更新
   * @return 返回是否有出错信息
   */
  private boolean updateTablesData()
  {
    boolean hasError = false;
    HtmlTable[] tables = data.getEnableTables();
    for(int i=0; i<tables.length; i++)
    {
      Map tableInfo = tables[i].getTableInfo();
      String tableName = (String)tableInfo.get(DATASET);
      if(tableName == null || tableName.length() == 0)
        continue;

      String sql = TABLE_SQL + tableName;
      EngineDataSet ds = new EngineDataSet();
      ds.setProvider(provider);
      ds.setResolver(resolver);
      ds.setQueryString(sql);
      try{
        updateTableData(tables[i], ds);
      }
      catch(Exception ex){
        log.error("升级数据", ex);
        hasError = true;
      }
      ds.closeDataSet();
      ds = null;
    }
    return hasError;
  }

  /**
   * 更新一个表的数据
   * @param table 表数据对象
   * @return 返回是否有出错信息
   */
  private void updateTableData(HtmlTable table, EngineDataSet ds) throws SQLException
  {
    ds.openDataSet();
    //得到主键的数组
    String[] ids = null;
    String idStr = (String)table.getTableInfo().get(KEY);
    //数据文件没有主键信息，则到数据库中去取
    if(idStr != null && idStr.length() > 0)
      ids = StringUtils.parseString(idStr, ",");
    else
    {
      ds.processElement();
      ids = ds.getRowIds();
    }
    //如果多没有，就删除所有的行，然后再插入所有的行
    if(ids == null)
    {
      log.warn("没有主键：" + ds.getQueryString());
      ds.deleteAllRows();
    }
    EngineRow locateRow = ids == null ? null : new EngineRow(ds, ids);
    //更新数据
    HtmlTableRow[] rows = table.getTableRows();
    for(int i=0; i<rows.length; i++)
    {
      Map rowInfo = rows[i].getRowInfo();
      if(rowInfo == null || rowInfo.size() == 0)
        continue;
      //该行数据是否存在
      boolean isExist = false;
      if(locateRow != null)
      {
        for(int j=0; j<ids.length; j++)
        {
          String value = (String)rowInfo.get(ids[j]);
          if(value == null)
            continue;
          locateRow.setValue(j, value);
        }
        isExist = ds.locate(locateRow, LocateUtil.FIRST);
      }
      //是否定位到数据，没有定位的话，则插入数据
      if(!isExist)
        ds.insertRow(false);
      Map.Entry[] entrys = (Map.Entry[])rowInfo.entrySet().toArray(new Map.Entry[rowInfo.size()]);
      for(int j=0; j<entrys.length; j++)
      {
        String key = (String)entrys[j].getKey();
        String value = (String)entrys[j].getValue();
        //还原大于号 和 小于号
        value = StringUtils.replace(value, GREATER_HTML, Expression.GREATER);
        value = StringUtils.replace(value, LESS_HTML, Expression.LESS);
        //还原双引号和单引号
        value = StringUtils.replace(value, DOUBLE_COMMA_HTML, DOUBLE_COMMA);
        value = StringUtils.replace(value, COMMA_HTML, COMMA);
        //
        ds.setValue(key, value);
        ds.post();
      }
    }
    //if(ds.changesPending())
    ds.saveChanges();
  }

  /**
   * 执行数据更新
   * @param provider 数据提供者，用于提取数据
   * @param resolver 数据更新者，用于更新数据
   * @param data 需要升级的数据对象
   * @return 保存表信息的对象数组
   */
  public static boolean updateTablesData(EngineDataSetProvider provider,
                                EngineDataSetResolver resolver, TempletData data)
  {
    synchronized(provider)
    {
      TableDataFacade updateObj = new TableDataFacade(provider, resolver);
      updateObj.data = data;
      return updateObj.updateTablesData();
    }
  }

  /**
   * 备份数据库的数据到对象中
   * @param tablesName 表名
   * @return 返回是否有错误信息
   */
  private HtmlTable[] bakTablesData(String[] tablesName)
  {
    ArrayList tables = new ArrayList(tablesName.length);
    //HtmlTable[] tables = new HtmlTable[tablesName.length];
    for(int i=0; i<tablesName.length; i++)
    {
      String sql = TABLE_SQL + tablesName[i];
      EngineDataSet ds = new EngineDataSet();
      ds.setProvider(provider);
      ds.setQueryString(sql);
      try{
        HtmlTable table = bakTableData(ds, tablesName[i]);
        tables.add(table);
      }
      catch(SQLException ex){
        log.error("备份数据", ex);
      }
      ds.closeDataSet();
      ds = null;
    }
    return (HtmlTable[])tables.toArray(new HtmlTable[tables.size()]);
  }

  /**
   * 备份数据库的一个表数据到对象中
   * @param tableName 表名
   * @return 保存表信息的对象
   */
  private HtmlTable bakTableData(EngineDataSet ds, String tableName) throws SQLException
  {
    ds.openDataSet();
    ds.processElement();
    //设置表对象的信息
    String[] ids = ds.getRowIds();
    Map tableInfo = new Hashtable();
    tableInfo.put(DATASET, tableName);
    if(ids != null)
    {
      for(int i=0; i<ids.length; i++)
        ids[i] = ids[i].toLowerCase();
      tableInfo.put(KEY, StringUtils.getArrayValue(ids));
    }
    HtmlTable table = new HtmlTable(tableInfo);
    //得到字段名数组
    String[] fieldsName = new String[ds.getColumnCount()];
    for(int i=0; i<fieldsName.length; i++)
      fieldsName[i] = ds.getColumn(i).getServerColumnName().toLowerCase();
    //添加表对象的行信息
    ds.first();
    for(int i=0; i<ds.getRowCount(); i++)
    {
      Map rowInfo = new Hashtable(fieldsName.length+1, 1);
      for(int j=0; j<fieldsName.length; j++)
      {
        String value = ds.getValue(j);
        //替换大于号 和 小于号
        value = StringUtils.replace(value, Expression.GREATER, GREATER_HTML);
        value = StringUtils.replace(value, Expression.LESS, LESS_HTML);
        //替换双引号和单引号
        value = StringUtils.replace(value, DOUBLE_COMMA, DOUBLE_COMMA_HTML);
        value = StringUtils.replace(value, COMMA, COMMA_HTML);
        //
        rowInfo.put(fieldsName[j], value);
      }
      table.addRow(new HtmlTableRow(rowInfo));
      ds.next();
    }
    return table;
  }

  /**
   * 备份数据库的数据到对象中,如果字段值中含有&gt;或&lt;将会被装化为#&amp;gt;或#&amp;lt;
   * @param provider 数据提供者，用于提取数据
   * @param tablesName 表名数组
   * @return 保存表信息的对象数组
   */
  public static HtmlTable[] bakTablesData(EngineDataSetProvider provider, String[] tablesName)
  {
    synchronized(provider)
    {
      TableDataFacade updateObj = new TableDataFacade(provider, null);
      return updateObj.bakTablesData(tablesName);
    }
  }

  /**
   * 备份数据库的数据到输出流
   * @param provider 数据提供者，用于提取数据
   * @param tablesName 表名数组
   * @param os 输出流
   */
  public static void bakTablesData(EngineDataSetProvider provider, String[] tablesName, OutputStream os)
  {
    synchronized(provider)
    {
      PrintWriter write = null;
      try{
        write = new PrintWriter(os, true);
        HtmlTable[] tables = bakTablesData(provider, tablesName);
        for(int i=0; i<tables.length; i++)
        {
          if(i>0)
            write.println();
          write.print("<%--export "+ tables[i].getRowCount() +" record--%>");
          write.print(tables[i].toString());
        }
      }
      finally{
        if(write != null)
        {
          write.flush();
          write.close();
        }
      }
    }
  }
}
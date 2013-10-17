package engine.report.html;

import java.io.Serializable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.math.BigDecimal;

import engine.util.context.Context;
import engine.dataset.EngineDataSet;
import engine.dataset.RowMap;
import engine.util.*;
import engine.report.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */
public final class HtmlProducer implements Serializable, Tag
{
  private TempletData  templet = null;
  private ContextData  context = null;

  private ReportData[] reportDatas = null;
  private ReportData   reportData = null;
  private Map[]        templetFields = null;
  private String[]     showFields = null;
  private String[]     sumFields = null;
  private String       groupField = null;

  private float[]      dynamicTableWidths = null;

  private Map groupValues = null;
  private BigDecimal[] totalValues = null;

  private Map hmSummaryValues    = null;

  private HtmlHelper helper = null;

  public void clear()
  {
    templet = null;
    context = null;
    reportDatas = null;
    reportData = null;
    templetFields = null;
    showFields = null;
    sumFields = null;
    groupField = null;
    if(groupValues != null)
      groupValues.clear();
    totalValues = null;
    if(hmSummaryValues != null)
      hmSummaryValues.clear();
    helper = null;
  }


  public void realase()
  {
    clear();
    hmSummaryValues = null;
    groupValues = null;
  }

  public void createHTMLs(TempletData data, ContextData context, HtmlHelper helper)
  {
    synchronized(data)
    {
      this.templet = data;
      this.context = context;
      this.helper = helper;

      templetFields = templet.getFields();

      reportDatas = context==null ? null :
                    helper.isPrintBlank() ? null : context.getReportDatas();

      showFields = context==null ? null : context.getShowColumns();
      sumFields  = context==null ? null : context.getShowSumColumns();
      groupField = context==null ? null : context.getGroupColumn();

      if(reportDatas != null)
      {
        for(int i=0; i<reportDatas.length; i++)
        {
          reportData = reportDatas[i];
          createHTML(helper);
        }
      }
      else
        createHTML(helper);
    }
  }

  /**
   * 从提供的数据集中提取需要的数据
   * @param dateSetName 数据集名称
   * @param rownum 数据集第几行
   * @param fieldName 字段名称
   * @param fieldInfo 字段信息
   * @return 返回提取到的数据
   */
  private String getDataSetFieldValue(String dateSetName, int row, String fieldName, Map fieldInfo)
  {
    if(reportData == null)
      return "";
    Object obj = reportData.getReportData(dateSetName);
    if(obj == null)
      return "";

    String format = getFieldFormat(fieldInfo);
    String zeroformat = fieldInfo != null ? (String)fieldInfo.get(ZEROFORMAT): null;
    return reportData.getReportDataValue(dateSetName, row, fieldName, format, zeroformat);
  }

  /**
   * 得到数据集的记录数
   * @param dateSetName 数据集的名称
   * @return 返回数据集的记录数
   */
  private int getDataSetRowCount(String dateSetName)
  {
    if(reportData == null)
      return 1;
    //
    return reportData.getReportDataRowCount(dateSetName);
  }

  /**
   * 创建多张HTML
   */
  private void createHTML(HtmlHelper helper)
  {
    HtmlTable[] htmlTables = templet.getEnableTables();
    if(htmlTables == null || htmlTables.length == 0)
      return;
    Table[] tables = new Table[htmlTables.length];
    if(htmlTables.length == 1)
    {
      int columnCount = showFields == null ? htmlTables[0].getColumnCount() : showFields.length;
      tables[0] = new Table();
      setTableProperty(htmlTables[0], tables[0], helper.isExcel());
      procDynamicTable(htmlTables, 0, tables);
    }
    else
    {
      boolean hasDynamic = false;
      int numDynamic = -1;
      for(int i=0; i<htmlTables.length; i++)
      {
        String type = (String)htmlTables[i].getTableInfo().get(TYPE);
        if(!hasDynamic && type!=null && type.equalsIgnoreCase(TYPE_DYNAMIC))
        {
          hasDynamic = true;
          numDynamic = i;
          int columnCount = showFields == null ? htmlTables[i].getColumnCount() : showFields.length;
          tables[i] = new Table();
          setTableProperty(htmlTables[i], tables[i], helper.isExcel());
        }
        else if(type!=null && !type.equalsIgnoreCase(TYPE_SUMMARY))
        {
          tables[i] = new Table();
          procStaticTable(htmlTables[i], tables[i]);
        }
      }
      if(numDynamic > -1)
        procDynamicTable(htmlTables, numDynamic, tables);
      else
        printTable(htmlTables, numDynamic, tables);
    }
  }

  /**
   * 处理动态表
   * @param tables 要处理的动态表的设置信息
   * @param num 要处理的动态表在数组中的位置
   * @param tables 所有的表
   */
  private void procDynamicTable(HtmlTable[] htmlTables, int dynamicNum, Table[] tables)
  {
    HtmlTable dyHtmlTable = htmlTables[dynamicNum];

    Table dyTable = tables[dynamicNum];

    Map dyTableInfo = dyHtmlTable.getTableInfo();

    String tableDataSet = (String)dyTableInfo.get(DATASET);

    boolean fillnull = isTrue((String)dyTableInfo.get(FILLNULL));

    int header = parseInt((String)dyTableInfo.get(HEADER));

    HtmlTableRow[] rows = dyHtmlTable.getTableRows();
    if(header > rows.length)
      header = rows.length;

    List headerRows = getShowRows(rows, 0, header, true);

    List bodyRows = getShowRows(rows, header, rows.length, false);
    for(int i=0; headerRows !=null && i<headerRows.size(); i++)
      fillCell(dyTable, rows[i], (HtmlTableCell[])headerRows.get(i), 0, dyTableInfo, true);

    int totalNum = getDataSetRowCount(tableDataSet);
    boolean isNeedSum = reportData != null && sumFields != null && sumFields.length > 0 && totalNum > 0;
    boolean isCalculate = helper.isCalculate();

    for(int n=0; isNeedSum && isCalculate && n<totalNum; n++)
    {
      String curGroupValue = groupField==null ? null : getDataSetFieldValue(tableDataSet, n, groupField, null);
      calculateGroup(tableDataSet, n, groupField, curGroupValue);
    }
    helper.setIsCalculate(false);

    int minRow = helper.getMinRow();
    int detailNum = helper.getMaxRow() > totalNum ? totalNum : helper.getMaxRow();
    String priorGroupValue = null;
    for(int n=minRow; n<detailNum; n++)
    {
      boolean isPrintGroup = groupField !=null && isNeedSum;
      if(isPrintGroup)
      {
        String curGroupValue = getDataSetFieldValue(tableDataSet, n, groupField, null);
        isPrintGroup = n==minRow ? false : priorGroupValue!=null && !priorGroupValue.equals(curGroupValue);
        for(int i=0; isPrintGroup && i<bodyRows.size(); i++)
          fillGroupCell(dyTable, rows[header+i], (HtmlTableCell[])bodyRows.get(i), dyTableInfo, priorGroupValue, true);

        priorGroupValue = curGroupValue;
      }
      for(int i=0; bodyRows!=null && i<bodyRows.size(); i++)
        fillCell(dyTable, rows[header+i], (HtmlTableCell[])bodyRows.get(i), n, dyTableInfo, false);
      calculateSummary(n, true);
      if(isNeedSum && n == detailNum-1)
      {
        if(groupField != null)
        {
          if(n<totalNum-1)
          {
            String curGroupValue = getDataSetFieldValue(tableDataSet, n+1, groupField, null);
            isPrintGroup = priorGroupValue!=null && !priorGroupValue.equals(curGroupValue);
          }
          else
            isPrintGroup = true;
        }

        for(int i=0; isPrintGroup && i<bodyRows.size(); i++)
          fillGroupCell(dyTable, rows[header+i], (HtmlTableCell[])bodyRows.get(i), dyTableInfo, priorGroupValue, true);

        for(int i=0; n==totalNum-1 && i<bodyRows.size(); i++)
          fillGroupCell(dyTable, rows[header+i], (HtmlTableCell[])bodyRows.get(i), dyTableInfo, null, false);
      }
    }

    for(int j=detailNum-minRow; header<rows.length && fillnull && j<helper.getPrintRow(); j++)
    {
      for(int i=0; bodyRows!=null && i<bodyRows.size(); i++)
        fillCell(dyTable, rows[header+i], (HtmlTableCell[])bodyRows.get(i), -1, dyTableInfo, false);
    }
    printTable(htmlTables, dynamicNum, tables);
  }

  /**
   * 添加表格
   * @param tables 包含表信息
   * @param tables 几个表的组合
   */
  private void printTable(HtmlTable[] htmlTables, int dynamicNum, Table[] tables)
  {
    int colCount = dynamicNum < 0 ? 0 : htmlTables[dynamicNum].getColumnCount();
    for(int j=0; j<tables.length; j++)
    {
      String type = (String)htmlTables[j].getTableInfo().get(TYPE);
      if(type != null && type.equalsIgnoreCase(TYPE_SUMMARY))
      {
        tables[j] = new Table();
        procStaticTable(htmlTables[j], tables[j]);
      }
      else if(helper.isExcel() && j!=dynamicNum)
      {
        tables[j].procOneCellTable(colCount);
      }
    }
    //
    Object[] objs = templet.getTablesAndOther();
    for(int i=0; i<objs.length; i++)
    {
      if(objs[i] instanceof HtmlTable) {
        HtmlTable htmlTable = (HtmlTable)objs[i];
        if(htmlTable.isDisable())
          continue;
        int index = indexOf(htmlTables, htmlTable);
        if(index > -1)
          helper.printTable(tables[index]);
      }
      else
        helper.print(String.valueOf(objs[i]));
    }
  }

  /**
   * 填充单元格
   * @param table 需要填充的表格的
   * @param row 该表格的改行的信息
   * @param cells 该表格的改行的所有单元格信息
   * @param rownum 数组的下标数。-1表示没有数据集,给填空行用。
   * @param tableInfo 相关表信息
   * @param isTitle 是否是标题行
   */
  private void fillCell(Table table, HtmlTableRow htmlRow, HtmlTableCell[] htmlCells,
                        int rownum, Map tableInfo, boolean isTitle)
  {
    Row row = new Row(isTitle);
    Map rowInfo = htmlRow.getRowInfo();
    setRowProperty(row, rowInfo);
    //
    if(rownum > -1)
    {
      String onClick = (String)rowInfo.get(ONCLICK);
      if(onClick != null)
        onClick = procRowPattern(onClick, rownum, tableInfo);
      String onDblClick = (String)rowInfo.get(ONDBLCLICK);
      if(onDblClick != null)
        onDblClick = procRowPattern(onDblClick, rownum, tableInfo);

      row.setOnClick(onClick);
      row.setOnDblClick(onDblClick);
    }
    //
    table.addRow(row);
    for(int j=0; j <htmlCells.length; j++)
    {
      Map cellInfo = htmlCells[j].getCellInfo();
      boolean hasValue = htmlCells[j].hasValueInfo() && rownum >= 0;
      String cellValue = hasValue ? getCellValue(htmlCells[j], rownum, tableInfo) : null;
      Cell cell = new Cell(cellValue);
      setCellProperty(cell, cellInfo, tableInfo);
      row.addCell(cell);
    }
  }

  /**
   * 填充小计或合计一行单元格
   * @param table 需要填充的表格的
   * @param row 该表格的改行的信息
   * @param cells 该表格的改行的所有单元格信息
   * @param tableInfo 相关表信息
   * @param groupKey 分组的key
   * @param isGroup 是否是小计
   */
  private void fillGroupCell(Table table, HtmlTableRow htmlRow, HtmlTableCell[] htmlCells,
                             Map tableInfo, String groupKey, boolean isGroup)
  {
    Row row = new Row();
    setRowProperty(row, htmlRow.getRowInfo());
    table.addRow(row);

    for(int j=0; j <htmlCells.length; j++)
    {
      Map cellInfo = htmlCells[j].getCellInfo();
      Map[] fieldValues = htmlCells[j].getFieldValueInfos();
      Map sumfield = null;
      int index = -1;
      for(int i=0; i<fieldValues.length; i++)
      {
        String field = (String)fieldValues[i].get(FIELD);
        index = StringUtils.indexOfIgnoreCase(this.sumFields, field);
        if(index > -1)
        {
          sumfield = fieldValues[i];
          break;
        }
      }
      Cell cell = null;
      if(sumfield == null)
      {
        cell = new Cell(j > 0 ? "" : isGroup ? "小计" : "合计");
        setCellProperty(cell, cellInfo, tableInfo);
        if(j == 0)
          cell.setAlign("center");
      }
      else
      {
        String format = getFieldFormat(sumfield);
        String zeroformat = (String)sumfield.get(ZEROFORMAT);
        BigDecimal fieldValue = isGroup ? ((BigDecimal[])groupValues.get(groupKey))[index] : totalValues[index];
        String fillValue = "";
        if(zeroformat != null && (fieldValue == null || fieldValue.doubleValue() == 0))
          fillValue = zeroformat;
        else if(format != null && fieldValue != null)
          fillValue = Format.formatNumber(fieldValue, format);
        else if(fieldValue != null)
          fillValue = String.valueOf(fieldValue);

        if(j ==0)
          fillValue = (isGroup ? "小计" : "合计") + fillValue;
        cell = new Cell(fillValue);
        setCellProperty(cell, cellInfo, tableInfo);
      }
      row.addCell(cell);
    }
  }

  /**
   * 得到各行的可显示的单元格数组
   * @param htmlRows 模板行数组
   * @param min 最小下标（大于等于最小）
   * @param max 最大下标（小于最大）
   * @param isHeader 是否是表头
   * @return 返回各行的可显示的单元格数组
   */
  private List getShowRows(HtmlTableRow[] htmlRows, int min, int max, boolean isHeader)
  {
    if(min > max)
      return null;
    List list = new ArrayList(max-min);
    for(int i = min; i < max; i++)
    {
      HtmlTableCell[] cells = getShowCells(htmlRows[i].getCells(), isHeader);
      list.add(cells);
    }
    return list;
  }

  /**
   * 得到可显示的单元格数组
   * @param cells 行的全部单元格数组
   * @param isHeader 是否是表头
   * @return 可显示的单元格数组
   */
  private HtmlTableCell[] getShowCells(HtmlTableCell[] cells, boolean isHeader)
  {
    if(showFields == null)
      return cells;
    List newCells = new ArrayList(showFields.length+1);
    for(int j=0; isHeader && j<cells.length; j++)
    {
      Map cellinfo = cells[j].getCellInfo();
      String field = (String)cellinfo.get(FIELD);
      if(field == null)
        continue;
      if(field.equals("*") && !newCells.contains(cells[j]))
        newCells.add(cells[j]);
    }
    //
    for(int i=0; i<showFields.length; i++)
    {
      if(isHeader)
      {
        for(int j=0; j<cells.length; j++)
        {
          Map cellinfo = cells[j].getCellInfo();
          String field = (String)cellinfo.get(FIELD);
          if(field == null)
            continue;
          if(field.indexOf(",") > 0)
          {
            int colspan = parseInt((String)cellinfo.get(COLSPAN), 0);
            String[] fieldName = StringUtils.parseString(field, ",");
            if(StringUtils.indexOfIgnoreCase(fieldName, showFields[i]) < 0)
              continue;
            if(!newCells.contains(cells[j]))
            {
              if(colspan > 0)
                cellinfo.put(COLSPAN, "1");
              newCells.add(cells[j]);
              break;
            }
            else if(colspan > 0)
            {
              cellinfo.put(COLSPAN, String.valueOf(colspan+1));
              break;
            }
          }
          else if(field.equalsIgnoreCase(showFields[i]))
          {
            newCells.add(cells[j]);
            break;
          }
        }
      }
      else
      {
        for(int j=0; j<cells.length; j++)
        {
          Map[] fieldValues = cells[j].getFieldValueInfos();
          for(int k=0; fieldValues!=null && k<fieldValues.length; k++)
          {
            String field = (String)fieldValues[k].get(FIELD);
            if(field == null)
              continue;
            if(field.equals("*"))
            {
              if(!newCells.contains(cells[j]))
              {
                newCells.add(cells[j]);
                break;
              }
            }
            else if(field.equalsIgnoreCase(showFields[i]))
            {
              newCells.add(cells[j]);
              break;
            }
          }
        }
      }
    }

    HtmlTableCell[] htmlCells = new HtmlTableCell[newCells.size()];
    newCells.toArray(htmlCells);
    return htmlCells;
  }

  //private int indexof
  /**
   * 处理要填充的单元格的值
   * @param cellValue 单元格的值
   * @param rownum 第几行
   * @param tableInfo 相关表信息
   * @return 返回处理后的值
   */
  private String getCellValue(HtmlTableCell htmlCell, int rownum, Map tableInfo)
  {
    StringBuffer buf = new StringBuffer();
    Object[] objs = htmlCell.getValueInfos();
    for(int i=0; i<objs.length; i++)
    {
      if(objs[i] instanceof String)
      {
        String s = procRowPattern((String)objs[i], rownum, tableInfo);
        buf.append(s);
      }
      else if(objs[i] instanceof Map)
        buf.append(getFieldValue((Map)objs[i], rownum, tableInfo));
    }
    return buf.toString();
  }
  /**
   * 处理静态表
   * @param htmlTable 要设置的静态表的设置信息
   * @param table 要设置的静态表
   */
  private void procStaticTable(HtmlTable htmlTable, Table table)
  {
    setTableProperty(htmlTable, table, false);
    //
    HtmlTableRow[] rows = htmlTable.getTableRows();
    for(int i=0; i<rows.length; i++)
      fillCell(table, rows[i], rows[i].getCells(), 0, htmlTable.getTableInfo(), false);
  }

  /**
   * 计算每页的统计值
   * @param rownum 数据集第几行
   * @param isAdd 是否时增加，是：加上该行的数值，反之：减去该行的数值
   */
  private void calculateSummary(int rownum, boolean isAdd)
  {
    if(hmSummaryValues == null)
      return;
    if(hmSummaryValues.size() < 1)
      return;
    HashMap hmTemp = new HashMap();
    Iterator it = hmSummaryValues.keySet().iterator();
    String key, dataSet, fieldName;
    double value, fieldValue;
    while(it.hasNext())
    {
      key = (String)it.next();
      try{
        value = Double.parseDouble((String)hmSummaryValues.get(key));
      }
      catch(Exception e){
        value = 0;
      }
      int index = key.indexOf(",");
      if(index < 0)
        return;

      dataSet = key.substring(0, index);
      fieldName = key.substring(index + 1);
      try{
        fieldValue = Double.parseDouble( getDataSetFieldValue(dataSet, rownum, fieldName, null) );
      }
      catch(Exception e){
        fieldValue = 0;
      }

      hmTemp.put(key, ""+(isAdd ? value+fieldValue : value-fieldValue));
    }
    hmSummaryValues = hmTemp;
  }

  /**
   * 得到字段的值,根据所属Table的信息判断是否是总结字段
   * @param fieldInfo 要到提取值的保存的字段信息
   * @param rownum 第几行
   * @param tableInfo 相关的表信息
   * @return 返回字段的值
   */
  private String getFieldValue(Map fieldInfo, int rownum, Map tableInfo)
  {
    String dateSetName = (String)(fieldInfo.get(DATASET) != null ? fieldInfo.get(DATASET) : tableInfo.get(DATASET));
    String fieldValue = "";
    if(reportData == null)
      return fieldValue;
    if(dateSetName == null)
      return fieldValue;

    String type = (String)tableInfo.get(TYPE);
    String fieldName = (String)fieldInfo.get(FIELD);
    String sum = (String)fieldInfo.get(SUM);

    if(type!=null && type.equalsIgnoreCase(TYPE_SUMMARY) && isTrue(sum))
    {
      if(hmSummaryValues == null)
        hmSummaryValues = new HashMap();
      String summaryKey = dateSetName+","+fieldName;
      fieldValue = (String)hmSummaryValues.get(summaryKey);
      if(fieldValue == null)
      {
        hmSummaryValues.put(summaryKey,"0");
        fieldValue = "零圆整";
      }
      else
      {
        String chinese = (String)fieldInfo.get(CHINESEFORMAT);
        String format = getFieldFormat(fieldInfo);
        String zeroformat = (String)fieldInfo.get(ZEROFORMAT);
        format = (format!=null && format.trim().length() > 0) ? format.trim() : null;
        if(isTrue(chinese))
          fieldValue = Format.toChineseCurrency(fieldValue);
        else
        {
          if(fieldValue.equals("0") && zeroformat != null)
            fieldValue = zeroformat;
          else
            fieldValue = format!= null ? Format.formatNumber(new BigDecimal(fieldValue),format) : fieldValue;
        }
      }
      return fieldValue;
    }
    return getDataSetFieldValue(dateSetName, rownum, fieldName, fieldInfo);
  }

  /**
   * 设置HTML的单元格的信息
   * @param cell 要设置的HTML的单元格
   * @param cellInfo 单元格的详细信息
   * @param tableInfo 相关表信息
   */
  private static void setCellProperty(Cell cell, Map cellInfo, Map tableInfo)
  {
    int colspan = parseInt((String)cellInfo.get(COLSPAN), 1);
    int rowspan = parseInt((String)cellInfo.get(ROWSPAN), 1);
    cell.setColspan(colspan);
    cell.setRowspan(rowspan);

    boolean nowrap = isTrue((String)cellInfo.get(NOWRAP));
    cell.setNoWrap(nowrap);
    cell.setId((String)cellInfo.get(ID));
    cell.setName((String)cellInfo.get(NAME));
    cell.setClass((String)cellInfo.get(CLASS));
    cell.setStyle((String)cellInfo.get(STYLE));
    cell.setHeight((String)cellInfo.get(HEIGHT));
    cell.setAlign((String)cellInfo.get(ALIGN));
    cell.setValign((String)cellInfo.get(VALIGN));
    boolean font_bold = isTrue((String)cellInfo.get(FONT_BOLD));
    cell.setIsBold(font_bold);
  }

  /**
   * 设置HTML的行的信息
   * @param row 要设置的HTML的行
   * @param rowInfo 行的详细信息
   */
  private static void setRowProperty(Row row, Map rowInfo)
  {
    row.setId((String)rowInfo.get(ID));
    row.setName((String)rowInfo.get(NAME));
    row.setClass((String)rowInfo.get(CLASS));
    row.setStyle((String)rowInfo.get(STYLE));
    row.setHeight((String)rowInfo.get(HEIGHT));
    row.setAlign((String)rowInfo.get(ALIGN));
    row.setValign((String)rowInfo.get(VALIGN));
  }
  /**
   * 设置HTML表的信息
   * @param htmlTable 设置HTML表的详细信息
   * @param table 要设置的HTML表
   * @param tableWidths 表格的各个列宽的数组
   */
  private static void setTableProperty(HtmlTable htmlTable, Table table, boolean isExcel)
  {

    Map tableInfo = htmlTable.getTableInfo();
    table.setWidth("95%");
    table.setId((String)tableInfo.get(ID));
    table.setName((String)tableInfo.get(NAME));
    table.setClass((String)tableInfo.get(CLASS));
    table.setStyle((String)tableInfo.get(STYLE));
    if(isExcel)
      table.setBorder(1);
    else
    {
      int border = parseInt((String)tableInfo.get(BORDER));
      table.setBorder(border);
    }
    table.setCellspacing(1);
    table.setCellpadding(1);
  }

  /**
   * 计算分组（小计和合计）
   * @param datasetName 数据源名称
   * @param row 数据源的行数
   * @param groupField 分组的字段名称
   * @param groupKey 分组的key
   */
  private void calculateGroup(String datasetName, int row, String groupField, String groupKey)
  {
    if(groupValues == null && groupField != null)
      groupValues = new Hashtable();
    if(totalValues == null)
      totalValues = new BigDecimal[sumFields.length];

    for(int j=0; j<totalValues.length; j++)
    {
      String value = getDataSetFieldValue(datasetName, row, sumFields[j], null);
      if(!isDouble(value))
        continue;
      BigDecimal curValue = new BigDecimal(value);
      totalValues[j] = totalValues[j] == null ? curValue : totalValues[j].add(curValue);

      if(groupField != null)
      {
        BigDecimal[] values = (BigDecimal[])groupValues.get(groupKey);
        if(values==null)
        {
          values = new BigDecimal[sumFields.length];
          groupValues.put(groupKey, values);
        }
        values[j] = values[j] == null ? curValue : values[j].add(curValue);
      }
    }
  }


  /**
   * 得到字段的格式化串的信息
   * @param fieldinfo 字段信息
   * @return 返回字段的格式化串的信息
   */
  private String getFieldFormat(Map fieldInfo)
  {
    return fieldInfo == null ? null : replaceObject((String)fieldInfo.get(FORMAT), context);
  }

  /**
   * 判断字符串对象是true字符串, 若是null或其他, 则返回false
   * @param value 字符串对象
   * @return 是否是true字符串
   */
  private static boolean isTrue(String value){
    if(value == null)
      return false;
    return value.equalsIgnoreCase(TRUE) || value.equals("1");
  }

  /**
   * 将字符串解析为整型,若出错,则返回0
   * @param value 字符串
   * @return 整形值
   */
  private static int parseInt(String value)
  {
    return parseInt(value, 0);
  }

  /**
   * 将字符串解析为整型,若出错,则返回默认值(defaultnum)
   * @param value 字符串
   * @return 整形值
   */
  private static int parseInt(String value, int defaultnum)
  {
    try{
      return Integer.parseInt(value);
    }
    catch(Exception ex){
      return defaultnum;
    }
  }

  /**
   * 将字符串解析为浮点型,若出错,则返回默认值(defaultnum)
   * @param value 字符串
   * @return 浮点型
   */
  private static float parseFloat(String value, float defaultnum)
  {
    try{
      return Float.parseFloat(value);
    }
    catch(Exception ex){
      return defaultnum;
    }
  }

  /**
   * 检测字符串是否是数字型的
   * @param value 需要检测的字符串
   * @return 返回是否是Double型的
   */
  private static boolean isDouble(String value)
  {
    try{
      Double.parseDouble(value);
    }catch(Exception ex){
      return false;
    }
    return true;
  }

  /**
   * 处理行的替换的字符串(特殊变量的替换)
   * @param pattern 需要处理的字段串
   * @param rownum 行数
   * @param tableInfo 表信息
   * @return 返回处理过的
   */
  private String procRowPattern(String pattern, int rownum, Map tableInfo)
  {
    String dataSetName = (String)tableInfo.get(DATASET);
    MessageFormat mess = new MessageFormat(pattern);
    String[] fieldNames = mess.getArgumentNames();
    Map fieldVales = null;
    for(int i=0; fieldNames!=null && i<fieldNames.length; i++)
    {
      if(fieldVales == null)
        fieldVales = new Hashtable();

      String value = getConstantValue(fieldNames[i]);
      if(value == null)
        value = rownum > -1 ? getDataSetFieldValue(dataSetName, rownum, fieldNames[i], null) : "";
      fieldVales.put(fieldNames[i], value);
    }
    return mess.format(fieldVales);
  }

  private String getConstantValue(String key)
  {
    if(key.startsWith("where."))
    {
      key = key.substring(6);
      return context == null ? null : context.getQueryWhere().getWhereValue(key);
    }
    Map contants = (Map)context.get("constant");
    if(contants == null)
      return null;
    return (String)contants.get(key);
  }


  private static String replaceObject(String value, Context context)
  {
    if(value == null)
      return null;
    Map contants = (Map)context.get("constant");
    if(contants == null)
      return value;
    return MessageFormat.format(value, contants);
  }

  /**
   * 得到字符串在字符串数组中的下标.若不存在该对象,则返回-1
   * @param objs 字符串数组
   * @param obj 字符串
   * @return 返回数组中的下标
   */
  private static final int indexOf(Object[] objs, Object obj)
  {
    if(obj == null)
      return -1;
    for(int i=0; i < objs.length; i++)
    {
      if(objs[i] == obj)
        return i;
    }
    return -1;
  }
}

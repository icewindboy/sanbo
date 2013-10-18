package engine.report.pdf;

import java.awt.Point;
import java.io.Serializable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;
import java.math.BigDecimal;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.PdfWriter;

import engine.util.context.Context;
import engine.dataset.EngineDataSet;
import engine.dataset.RowMap;
import engine.util.*;
import engine.report.util.*;
/**
 * Title:        print
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ITRD
 * @author hukn
 * @version 1.0
 */

public final class PdfProducer implements Serializable, Tag
{
  private Map htFonts = null;

  private static BaseFont     bfChinese  = null;
  private static BaseFont     bfChinese_bold = null;
  private int          defaultFontSize = 10;
  private Document     document   = null;
  private PdfWriter    writer    = null;
  private TempletData  templet = null;
  private ContextData  context = null;

  private ReportData[] reportDatas = null;
  private ReportData   reportData = null;
  private Map[]        templetFields = null;
  private String[]     showFields = null;
  private String[]     sumFields = null;
  private String       groupField = null;

  private float[]      dynamicTableWidths = null;

  private BigDecimal[] groupValues = null;
  private BigDecimal[] totalValues = null;

  private Map hmSummaryValues    = null;
  private Map hmSubtotalValues   = null;

  private HtmlTempletPrint templetPrint = null;
  private PrecisionPrint precisionPrint = null;

  /**
   * 复位临时变量
   */
  public void clear()
  {
    defaultFontSize = 10;
    document = null;
    writer = null;
    templet = null;
    context = null;
    reportDatas = null;
    reportData = null;
    templetFields = null;
    showFields = null;
    sumFields = null;
    groupField = null;
    groupValues = null;
    totalValues = null;
    if(hmSummaryValues != null)
      hmSummaryValues.clear();
    if(hmSubtotalValues != null)
      hmSubtotalValues.clear();
  }

  /**
   * 释放资源
   */
  public void realase()
  {
    clear();
    hmSummaryValues = null;
    hmSubtotalValues = null;
  }

  /**
   * 生成pdf文档
   * @param data 模板对象
   * @param context 数据上下文
   * @param os 输出流
   * @throws BadElementException 异常
   * @throws DocumentException 异常
   */
  public void createPdfs(TempletData data, ContextData context, OutputStream os) throws Exception
  {
    createPdfs(data, context, os, null);
  }

  /**
   * 生成pdf文档
   * @param data 模板对象
   * @param context 数据上下文
   * @param os 输出流
   * @param fonts 中文字体库
   * @throws BadElementException 异常
   * @throws DocumentException 异常
   */
  public void createPdfs(TempletData data, ContextData context, OutputStream os, Map fonts)
      throws BadElementException, DocumentException, IOException
  {
    synchronized(data)
    {
      this.templet = data;
      this.context = context;
      this.htFonts = fonts;
      if(bfChinese == null)
        bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
      if(bfChinese_bold == null)
        bfChinese_bold = BaseFont.createFont("STSongStd-Light,Bold", "UniGB-UCS2-H", false);

      this.createPdfs(os);
    }
  }

  /**
   * 生成pdf文档
   * @param os 输出流
   * @throws BadElementException 异常
   * @throws DocumentException 异常
   */
  private void createPdfs(OutputStream os) throws BadElementException, DocumentException
  {
    templetFields = templet.getFields();

    reportDatas = context==null ? null : context.getReportDatas();
    showFields = context==null ? null : context.getShowColumns();
    sumFields  = context==null ? null : context.getShowSumColumns();
    groupField = context==null ? null : context.getGroupColumn();
    Map page = this.templet.getPage();
    if(page == null)
      throw new PdfException("The tag of page is null");

    String printType = (String)this.templet.getPage().get(TYPE);
    if(printType == null)
      throw new PdfException("The attribute of type in page tag is null");

    if(printType.equalsIgnoreCase(TYPE_PRECISION)){
      if(precisionPrint == null)
        precisionPrint = new PrecisionPrint();
      precisionPrint.createPdfs(os);
    }
    else{
      if(templetPrint == null)
        templetPrint = new HtmlTempletPrint();
      templetPrint.createPdfs(os);
    }
  }

  /**
   * 读取中文字体并保存字体类
   * @param fontName 字体名称
   * @return 中文字体
   */
  private BaseFont getBaseFonts(String fontName)
  {
    if(fontName == null)
      return null;
    Object[] fontAttr = (Object[])htFonts.get(fontName);
    if(fontAttr == null)
      return null;

    String fontfile = (String)fontAttr[0];
    long lastModi = ((Long)fontAttr[1]).longValue();
    BaseFont baseFont = (BaseFont)fontAttr[2];
    try{
      if(baseFont == null)
      {
        baseFont = BaseFont.createFont(fontfile, BaseFont.IDENTITY_H, true);
        fontAttr[2] = baseFont;
      }
      else
      {
        File file = new File(fontfile);
        if(file.lastModified() != lastModi)
        {
          baseFont = BaseFont.createFont(fontfile, BaseFont.IDENTITY_H, true);
          fontAttr[1] = new Long(file.lastModified());
          fontAttr[2] = baseFont;
        }
      }
    }
    catch(Exception e){    }

    return baseFont;
  }

  /**
   * 从提供的数据集中提取需要的数据
   * @param dateSetName 数据集名称
   * @param row 数据集第几行
   * @param fieldName 字段名称
   * @param fieldInfo 字段信息
   * @return 返回提取到的数据
   */
  private String getDataSetFieldValue(String dateSetName, int row, String fieldName, Map fieldInfo)
  {
    if(reportData == null)
    {
      return "";
    }
    if(reportData.getReportData(dateSetName) == null)
    {
      return "";
    }

    String format = getFieldFormat(fieldInfo);
    String zeroformat = fieldInfo != null ? (String)fieldInfo.get(ZEROFORMAT): null;
    return reportData.getReportDataValue(dateSetName ,row, fieldName, format, zeroformat);
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


  final class HtmlTempletPrint implements Serializable
  {

    private void createDocument()
    {
      Rectangle pageSize = null;
      Map page = templet.getPage();
      try{
        float width  = Float.parseFloat((String)page.get(PAGE_WIDTH)) *72f/2.54f;
        float height = Float.parseFloat((String)page.get(PAGE_HEIGHT))*72f/2.54f;
        pageSize = new Rectangle(width, height);
      }
      catch(Exception e){
        pageSize = new Rectangle(595, 842);
      }
      float marginLeft, marginRight, marginTop, marginBottom;
      try{
        marginLeft = (float)(Float.parseFloat((String)page.get(MAGIN_LEFT)) *72/2.54);
      }
      catch(Exception e){ marginLeft = 36;  }

      try{
        marginRight = (float)(Float.parseFloat((String)page.get(MAGIN_RIGHT))*72/2.54);
      }
      catch(Exception e){ marginRight = 36;  }

      try{
        marginTop = (float)(Float.parseFloat((String)page.get(MAGIN_TOP))*72/2.54);
      }
      catch(Exception e){ marginTop = 36;  }

      try{
        marginBottom = (float)(Float.parseFloat((String)page.get(MAGIN_BOTTOM))*72/2.54);
      }
      catch(Exception e){ marginBottom = 36;  }

      try{
        defaultFontSize = Integer.parseInt((String)page.get(FONT_SIZE));
      }
      catch(Exception e){ defaultFontSize = 10;  }

      document = new Document(pageSize, marginLeft, marginRight, marginTop, marginBottom);
    }

    /**
     * 创建多张PDF
     * @param os 输出流
     * @throws Exception
     */
    public void createPdfs(OutputStream os) throws BadElementException, DocumentException
    {
      try{
        createDocument();

        writer = PdfWriter.getInstance(document, os);
        Map page = templet.getPage();

        if(page != null)
          writer.setPageEvent(new PdfPageEvents(page));
        document.addCreator("ENGINE");
        document.open();

        if(isTrue((String)page.get(DIALOG)))
          writer.addJavaScript(PdfAction.javaScript("this.print(true);\r", writer));

        if(reportDatas != null)
        {
          for(int i=0; i<reportDatas.length; i++)
          {
            reportData = reportDatas[i];
            createPdf();
          }
        }
        else
          createPdf();
      }
      catch(BadElementException e){
        throw e;
      }
      catch(DocumentException e){
        throw e;
      }
      finally{
        try{
          if(document !=null && document.isOpen())
            document.close();
        }
        catch(Exception e){}
      }

    }

    /**
     * 创建多张PDF
     * @throws Exception
     */
    private void createPdf() throws BadElementException, DocumentException
    {
      HtmlTable[] htmlTables = templet.getEnableTables();
      if(htmlTables == null || htmlTables.length == 0)
        return;

      Table[] pdfTables = new Table[htmlTables.length];

      if(htmlTables.length == 1)
      {
        int columnCount = showFields == null ? htmlTables[0].getColumnCount() : showFields.length;
        pdfTables[0] = new Table(columnCount);
        dynamicTableWidths = getTableWidths(htmlTables[0].getTableInfo(), columnCount, true);
        setPdfTableProperty(htmlTables[0], pdfTables[0], dynamicTableWidths);
        procDynamicTable(htmlTables, 0, pdfTables);
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
            pdfTables[i] = new Table(columnCount);
            dynamicTableWidths = getTableWidths(htmlTables[i].getTableInfo(), columnCount, true);
            setPdfTableProperty(htmlTables[i], pdfTables[i], dynamicTableWidths);
          }
          else
          {
            pdfTables[i] = new Table(htmlTables[i].getColumnCount());
            procStaticTable(htmlTables[i], pdfTables[i]);
          }
        }
        if(numDynamic > -1)
          procDynamicTable(htmlTables, numDynamic, pdfTables);
        else
          for(int j=0; j<pdfTables.length; j++) {
            document.add(pdfTables[j]);
          }
      }
    }

    private void procDynamicTable(HtmlTable[] htmlTables, int dynamicNum, Table[] pdfTables)
        throws BadElementException, DocumentException
    {
      HtmlTable dyHtmlTable = htmlTables[dynamicNum];

      Table dyPdfTable = pdfTables[dynamicNum];

      Map dyTableInfo = dyHtmlTable.getTableInfo();

      String tableDataSet = (String)dyTableInfo.get(DATASET);

      boolean fillnull = isTrue((String)dyTableInfo.get(FILLNULL));

      int header = parseInt((String)dyTableInfo.get(HEADER));

      int maxrow = parseInt((String)dyTableInfo.get(MAXROW), -1);
      //
      HtmlTableRow[] rows = dyHtmlTable.getTableRows();
      if(header > rows.length)
        header = rows.length;

      List headerRows = getShowRows(rows, 0, header, true);

      List bodyRows = getShowRows(rows, header, rows.length, false);

      for(int i=0; i<headerRows.size(); i++)
        fillCell(dyPdfTable, (HtmlTableCell[])headerRows.get(i), 0, dyTableInfo);

      boolean isOneRowOverPage = false;
      boolean isFitsPage = true;
      boolean isNewPage = false;

      int detailNum = getDataSetRowCount(tableDataSet);

      boolean isNeedSum = reportData != null && sumFields != null && sumFields.length > 0 && detailNum > 0;
      String priorGroupValue = null;
      int rowcount = 1;
      for(int n=0; n<detailNum; n++)
      {
        boolean isNeedCalc = !(isNewPage && !isFitsPage);
        boolean isPrintGroup = groupField !=null && isNeedSum;
        if(isPrintGroup)
        {
          String curGroupValue = getDataSetFieldValue(tableDataSet, n, groupField, null);
          isPrintGroup =  priorGroupValue!=null && !priorGroupValue.equals(curGroupValue);
          if(isPrintGroup)
          {
            rowcount = fillGroupRow(htmlTables, dynamicNum, pdfTables, isOneRowOverPage, maxrow,
                                    rowcount, header, headerRows, bodyRows, rows, true);
            dyPdfTable = pdfTables[dynamicNum];
          }
          if(isNeedCalc)
            calculateGroup(tableDataSet, n, groupField, isPrintGroup);
          priorGroupValue = curGroupValue;
        }
        else if(isNeedSum && isNeedCalc)
          calculateGroup(tableDataSet, n, groupField, false);

        for(int i=0; i<bodyRows.size(); i++)
          fillCell(dyPdfTable, (HtmlTableCell[])bodyRows.get(i), n, dyTableInfo);

        ++rowcount;
        isFitsPage = fitsPage(htmlTables, pdfTables, false);
        isOneRowOverPage = isOneRowOverPage || (n==0 && !isFitsPage);
        //
        isNewPage = !isOneRowOverPage && (!isFitsPage || (isFitsPage && maxrow >0 && rowcount > maxrow));
        if (isNewPage)
        {
          if(!isFitsPage)
          {
            n--;
            for(int j=header; j< rows.length; j++)
              dyPdfTable.deleteLastRow();
          }
          else
            calculateSummary( n, true);
          rowcount = 1;
          resetDynamicTable(htmlTables, dynamicNum, pdfTables, header, headerRows);
          resetSubtotal();
          dyPdfTable = pdfTables[dynamicNum];
        }
        else
          calculateSummary( n, true);

        if(isNeedSum && n == detailNum-1)
        {
          if(groupField != null)
          {
            rowcount = fillGroupRow(htmlTables, dynamicNum, pdfTables, isOneRowOverPage, maxrow,
                                  rowcount, header, headerRows, bodyRows, rows, true);
            dyPdfTable = pdfTables[dynamicNum];
          }
          rowcount = fillGroupRow(htmlTables, dynamicNum, pdfTables, isOneRowOverPage, maxrow,
                                  rowcount, header, headerRows, bodyRows, rows, false);
          dyPdfTable = pdfTables[dynamicNum];
        }

      }
      while(fillnull && !isOneRowOverPage)
      {
        if(header == rows.length)
          break;

        for(int i=0; i<bodyRows.size(); i++)
        {
          fillCell(dyPdfTable, (HtmlTableCell[])bodyRows.get(i), -1, dyTableInfo);
          ++rowcount;
          isFitsPage = fitsPage(htmlTables, pdfTables, true);
          if (!isFitsPage || (isFitsPage && maxrow >0 && rowcount > maxrow))
          {
            fillnull = false;
            if(!isFitsPage)
              dyPdfTable.deleteLastRow();
            break;
          }
        }
      }
      addTable(htmlTables, pdfTables, true);
    }

    private int fillGroupRow(HtmlTable[] htmlTables, int dynamicNum, Table[] pdfTables,
                             boolean isOneRowOverPage, int maxrow, int rowcount, int header,
                             List headerRows, List bodyRows, HtmlTableRow[] rows, boolean isGroup)
        throws BadElementException, DocumentException
    {
      HtmlTable dyHtmlTable = htmlTables[dynamicNum];
      Table dyPdfTable = pdfTables[dynamicNum];
      Map dyTableInfo = dyHtmlTable.getTableInfo();
      for(int i=0; i<bodyRows.size(); i++)
        fillGroupCell(dyPdfTable, (HtmlTableCell[])bodyRows.get(i), dyTableInfo, isGroup);

      ++rowcount;
      boolean isFitsPage_g = fitsPage(htmlTables, pdfTables, false);
      //
      boolean isNewPage_g = !isOneRowOverPage && (!isFitsPage_g || (isFitsPage_g && maxrow >0 && rowcount > maxrow));
      if (isNewPage_g)
      {
        if(!isFitsPage_g)
        {
           for(int j=header; j< rows.length; j++)
             dyPdfTable.deleteLastRow();
        }

        rowcount = 1;
        resetDynamicTable(htmlTables, dynamicNum, pdfTables, header, headerRows);
        dyPdfTable = pdfTables[dynamicNum];
        if(!isFitsPage_g)
        {
          for(int i=0; i<bodyRows.size(); i++)
            fillGroupCell(dyPdfTable, (HtmlTableCell[])bodyRows.get(i), dyTableInfo, isGroup);

          ++rowcount;
        }
      }
      return rowcount;
    }

    private void resetDynamicTable(HtmlTable[] htmlTables, int dynamicNum, Table[] pdfTables,
                                   int header, List headerRows)
        throws BadElementException, DocumentException
    {
      HtmlTable dyHtmlTable = htmlTables[dynamicNum];
      Map dyTableInfo = dyHtmlTable.getTableInfo();
      addTable(htmlTables, pdfTables, false);
      document.newPage();

      Table oldPdfTable = pdfTables[dynamicNum];
      Table dyPdfTable = new Table(oldPdfTable.columns());
      pdfTables[dynamicNum] = dyPdfTable;
      setPdfTableProperty(dyHtmlTable, dyPdfTable, dynamicTableWidths);

      for(int i=0; i<headerRows.size(); i++)
        fillCell(dyPdfTable, (HtmlTableCell[])headerRows.get(i), 0, dyTableInfo);
    }

    private void addTable(HtmlTable[] tables, Table[] pdfTables, boolean isLastPage)
        throws BadElementException, DocumentException
    {
      for(int j=0; j<pdfTables.length; j++)
      {
        String type = (String)tables[j].getTableInfo().get(TYPE);
        if(isLastPage && type != null && type.equalsIgnoreCase(TYPE_SUBTOTAL))
          continue;
        else if(!isLastPage && type != null && type.equalsIgnoreCase(TYPE_TOTAL))
          continue;
        else if( type != null &&
                (type.equalsIgnoreCase(TYPE_SUMMARY)
                || type.equalsIgnoreCase(TYPE_TOTAL)
                || type.equalsIgnoreCase(TYPE_SUBTOTAL))
                )
        {
          pdfTables[j] = new Table(tables[j].getColumnCount());
          procStaticTable(tables[j], pdfTables[j]);
        }
        document.add(pdfTables[j]);
      }
    }

    private boolean fitsPage(HtmlTable[] tables, Table[] pdfTables, boolean isLastPage)
    {
      float pageHeight = document.getPageSize().height() - document.topMargin() - document.bottomMargin();
      float remainHeight = pageHeight;
      for(int i=0; i<pdfTables.length; i++)
      {
        String type = (String)tables[i].getTableInfo().get(TYPE);
        if(isLastPage && type != null && type.equalsIgnoreCase(TYPE_SUBTOTAL))
          continue;
        else if(!isLastPage && type != null && type.equalsIgnoreCase(TYPE_TOTAL))
          continue;
        if(i==0)
          remainHeight = writer.getTableBottom(pdfTables[i]);
        else
        {
          remainHeight -= (pageHeight - writer.getTableBottom(pdfTables[i]));
        }
      }
      return (int)remainHeight > 0;
    }

    private void fillCell(Table pdfTable, HtmlTableCell[] cells, int rownum, Map tableInfo)
        throws BadElementException
    {
      for(int j=0; j <cells.length; j++)
      {
        Map cellInfo = cells[j].getCellInfo();

        int font_size = parseInt((String)cellInfo.get(FONT_SIZE), defaultFontSize);

        boolean font_bold = isTrue((String)cellInfo.get(FONT_BOLD));

        boolean hasValue = cells[j].hasValueInfo() && rownum >= 0;
        String cellValue = hasValue ? getCellValue(cells[j], rownum, tableInfo) : "";

        BaseFont bf = getBaseFonts((String)cellInfo.get(FONT_FACE));
        if(bf == null)
          bf = font_bold ? bfChinese_bold : bfChinese;
        Cell pdfCell = new Cell(new Phrase(cellValue, new Font(bf, font_size)));

        setPdfCellProperty(pdfCell, cellInfo, tableInfo);

        pdfTable.addCell(pdfCell);
      }
    }

    private void fillGroupCell(Table pdfTable, HtmlTableCell[] cells, Map tableInfo, boolean isGroup)
        throws BadElementException
    {
      Cell pdfCell=null;
      for(int j=0; j <cells.length; j++)
      {
        Map cellInfo = cells[j].getCellInfo();
        int font_size = parseInt((String)cellInfo.get(FONT_SIZE), defaultFontSize);
        boolean font_bold = isTrue((String)cellInfo.get(FONT_BOLD));
        Map[] fieldValues = cells[j].getFieldValueInfos();
        Map sumfield = null;
        int index = -1;
        for(int i=0; i<fieldValues.length; i++)
        {
          String field = (String)fieldValues[i].get(FIELD);
          index = StringUtils.indexOfIgnoreCase(sumFields, field);
          if(index > -1)
          {
            sumfield = fieldValues[i];
            break;
          }
        }
        BaseFont bf = getBaseFonts((String)cellInfo.get(FONT_FACE));
        if(bf == null)
          bf = font_bold ? bfChinese_bold : bfChinese;
        if(sumfield == null)
        {
          pdfCell = new Cell(new Phrase(j > 0 ? "" : isGroup ? "小计" : "合计", new Font(bf, font_size)));
          setPdfCellProperty(pdfCell, cellInfo, tableInfo);
          if(j == 0)
            pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        }
        else
        {
          String format = getFieldFormat(sumfield);
          String zeroformat = (String)sumfield.get(ZEROFORMAT);
          BigDecimal fieldValue = isGroup ? groupValues[index] : totalValues[index];
          String fillValue = "";
          if(zeroformat != null && (fieldValue == null || fieldValue.doubleValue() == 0))
            fillValue = zeroformat;
          else if(format != null && fieldValue != null)
            fillValue = Format.formatNumber(fieldValue, format);
          else if(fieldValue != null)
            fillValue = String.valueOf(fieldValue);
          if(j ==0)
            fillValue = (isGroup ? "小计" : "合计") + fillValue;
          pdfCell = new Cell(new Phrase(fillValue, new Font(bf, font_size)));
          setPdfCellProperty(pdfCell, cellInfo, tableInfo);
        }
        pdfTable.addCell(pdfCell);
      }
    }

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

    private HtmlTableCell[] getShowCells(HtmlTableCell[] cells, boolean isHeader)
    {
      if(showFields == null)
        return cells;
      List newCells = new ArrayList(showFields.length);
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
              if(field.equalsIgnoreCase(showFields[i]))
              {
                newCells.add(cells[j]);
                break;
              }
            }
          }
        }
      }
      //
      HtmlTableCell[] htmlCells = new HtmlTableCell[newCells.size()];
      newCells.toArray(htmlCells);
      return htmlCells;
    }

    private String getCellValue(HtmlTableCell htmlCell, int rownum, Map tableInfo)
    {
      StringBuffer buf = new StringBuffer();
      Object[] objs = htmlCell.getValueInfos();
      for(int i=0; i<objs.length; i++)
      {
        if(objs[i] instanceof String)
        {
          String s = procRowPattern((String)objs[i], rownum, tableInfo);// : (String)objs[i];
          buf.append(s);
        }
        else if(objs[i] instanceof Map)
          buf.append(getFieldValue((Map)objs[i], rownum, tableInfo));
      }
      return buf.toString();
    }

    private void procStaticTable(HtmlTable table, Table pdfTable) throws BadElementException, DocumentException
    {
      float[] widths = getTableWidths(table.getTableInfo(), pdfTable.columns(), false);
      setPdfTableProperty(table, pdfTable, widths);
      HtmlTableRow[] rows = table.getTableRows();
      for(int i=0; i<rows.length; i++)
        fillCell(pdfTable, rows[i].getCells(), 0, table.getTableInfo());
    }

    private void calculateSummary(int rownum, boolean isAdd)
    {
      if(hmSummaryValues == null)
        return;
      if(hmSummaryValues.size() < 1)
        return;
      Iterator it = hmSummaryValues.keySet().iterator();
      String key, dataSet, fieldName;
      BigDecimal fieldValue;
      BigDecimal[] totalValues = null;
      while(it.hasNext())
      {
        key = (String)it.next();
        int index = key.indexOf(",");
        if(index < 0)
          return;

        totalValues = (BigDecimal[])hmSummaryValues.get(key);
        dataSet = key.substring(0, index);
        fieldName = key.substring(index + 1);
        try{
          fieldValue = new BigDecimal( getDataSetFieldValue(dataSet, rownum, fieldName, null) );
        }
        catch(Exception e){
          fieldValue = new BigDecimal(0);
        }
        totalValues[0] = totalValues[0].add(fieldValue);
        totalValues[1] = totalValues[1].add(fieldValue);
      }
    }

    private void resetSubtotal()
    {
      if(hmSummaryValues == null)
        return;
      if(hmSummaryValues.size() < 1)
        return;

      Iterator it = hmSummaryValues.keySet().iterator();
      String key, dataSet, fieldName;
      BigDecimal[] totalValues = null;
      while(it.hasNext())
      {
        key = (String)it.next();
        int index = key.indexOf(",");
        if(index < 0)
          return;

        totalValues = (BigDecimal[])hmSummaryValues.get(key);
        totalValues[0] = new BigDecimal(0);
      }
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

      if(isTrue(sum) && type!=null &&
         (type.equalsIgnoreCase(TYPE_SUMMARY) || type.equalsIgnoreCase(TYPE_SUBTOTAL) ||
          type.equalsIgnoreCase(TYPE_TOTAL))
        )
      {
        if(hmSummaryValues == null)
          hmSummaryValues = new Hashtable();
        String summaryKey = new StringBuffer(dateSetName).append(",").append(fieldName).toString();
        BigDecimal[] totalValues = (BigDecimal[])hmSummaryValues.get(summaryKey);

        if(totalValues == null)
        {
          hmSummaryValues.put(summaryKey, new BigDecimal[]{new BigDecimal(0), new BigDecimal(0)});
          fieldValue = "零圆整";
        }
        else
        {
          boolean isSubtotal = type.equalsIgnoreCase(TYPE_SUBTOTAL);
          fieldValue = isSubtotal ? totalValues[0].toString() : totalValues[1].toString();
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
              fieldValue = format!= null ? Format.formatNumber(fieldValue, format) : fieldValue;
          }
        }
        return fieldValue;
      }

      return getDataSetFieldValue(dateSetName, rownum, fieldName, fieldInfo);
    }

    private void setPdfCellProperty(Cell pdfCell, Map cellInfo, Map tableInfo)
    {
      int colspan = parseInt((String)cellInfo.get(COLSPAN), 1);
      int rowspan = parseInt((String)cellInfo.get(ROWSPAN), 1);
      pdfCell.setColspan(colspan);
      pdfCell.setRowspan(rowspan);

      String nowrap = (String)cellInfo.get(NOWRAP);
      int maxLines = parseInt(nowrap, 0);
      if(maxLines <= 0 && isTrue(nowrap))
        maxLines = 1;
      if(maxLines > 0)
        pdfCell.setMaxLines(maxLines);

      if(tableInfo.get(CELLHEIGHT) != null)
        pdfCell.setWidth((String)tableInfo.get(CELLHEIGHT));

      try{
        float borderWidth = Float.parseFloat((String)cellInfo.get(BORDER));
        pdfCell.setBorderWidth(borderWidth);
      }
      catch(Exception nfex){}

      String align = (String)cellInfo.get(ALIGN);
      if(align != null && align.trim().length() > 0)
        pdfCell.setHorizontalAlignment(align.trim());
      String valign = (String)cellInfo.get(VALIGN);
      if(valign != null && valign.trim().length() > 0)
        pdfCell.setVerticalAlignment(valign.trim());

      String border_left = (String)cellInfo.get(BORDER_LEFT);
      String border_right = (String)cellInfo.get(BORDER_RIGHT);
      String border_top = (String)cellInfo.get(BORDER_TOP);
      String border_bottom = (String)cellInfo.get(BORDER_BOTTOM);
      if(border_left != null || border_right != null || border_top != null || border_bottom != null)
      {
        int left = parseInt(border_left, Rectangle.LEFT);
        if(left < 1)
          left = 0;
        int right = parseInt(border_right, Rectangle.RIGHT);
        if(right < 1)
          right = 0;
        int top = parseInt(border_top, Rectangle.TOP);
        if(top < 1)
          top = 0;
        int bottom = parseInt(border_bottom, Rectangle.BOTTOM);
        if(bottom < 1)
          bottom = 0;
        pdfCell.setBorder(left | right | top | bottom);
      }
    }

    private void setPdfTableProperty(HtmlTable table, Table pdfTable, float[] tableWidths)
    {
      pdfTable.setWidth(100);
      Map tableInfo = table.getTableInfo();

      try{
        int border = Integer.parseInt((String)tableInfo.get(BORDER));
        pdfTable.setBorderWidth(border);
      }
      catch(Exception nfex){}

      try{
        float cellspacing = Float.parseFloat((String)tableInfo.get(CELLSPACING));
        pdfTable.setSpacing(cellspacing);
      }
      catch(Exception nfex){}

      try{
        float cellpadding = Float.parseFloat((String)tableInfo.get(CELLPADDING));
        pdfTable.setPadding(cellpadding);
      }
      catch(Exception nfex){}

      try{
        float offset = Float.parseFloat((String)tableInfo.get(OFFSET));
        pdfTable.setOffset(offset);
      }
      catch(Exception nfex){}

      String border_left = (String)tableInfo.get(BORDER_LEFT);
      String border_right = (String)tableInfo.get(BORDER_RIGHT);
      String border_top = (String)tableInfo.get(BORDER_TOP);
      String border_bottom = (String)tableInfo.get(BORDER_BOTTOM);
      if(border_left != null || border_right != null || border_top != null || border_bottom != null)
      {
        int left = parseInt(border_left, Rectangle.LEFT);
        if(left < 1)
          left = 0;
        int right = parseInt(border_right, Rectangle.RIGHT);
        if(right < 1)
          right = 0;
        int top = parseInt(border_top, Rectangle.TOP);
        if(top < 1)
          top = 0;
        int bottom = parseInt(border_bottom, Rectangle.BOTTOM);
        if(bottom < 1)
          bottom = 0;

        pdfTable.setBorder(left | right | top | bottom);
      }

      if(tableWidths != null)
      {
        try
        {
          pdfTable.setWidths(tableWidths);
        }
        catch(BadElementException e)
        {
          System.err.println("setPdfTableProperty:"+e.getMessage());
          e.printStackTrace();
        }
      }
    }

    private float[] getTableWidths(Map tableInfo,  int columnCount, boolean isDynamic)
    {
      Map temp = (Map)tableInfo.get(WIDTHS);
      String width = temp == null ? null : (String)temp.get(VALUE);
      String[] allwidths = width == null ? null : StringUtils.parseString(width, ",");
      String[] widths = null;
      if(isDynamic && allwidths != null && templetFields !=null)
      {
        List widthsList = new ArrayList(templetFields.length);

        if(showFields != null)
        {
          for(int i=0; i<showFields.length; i++)
          {
            if(i >= allwidths.length)
              break;
            for(int j=0; j<templetFields.length; j++)
            {
              String fieldname = (String)templetFields[j].get(NAME);
              if(fieldname.equalsIgnoreCase(showFields[i]))
              {
                if(j < allwidths.length)
                  widthsList.add(allwidths[j]);
                break;
              }
            }
          }
        }
        else
        {
          for(int i=0; i<templetFields.length; i++)
          {
            if(i >= allwidths.length)
              break;
            widthsList.add(allwidths[i]);
          }
        }
        widths = new String[widthsList.size()];
        widthsList.toArray(widths);
      }
      else
        widths = allwidths;

      float[] fWidths = null;
      if(widths != null)
      {
        float allWidth=0;
        fWidths = new float[columnCount];
        int i = 0;
        int widthCount = widths.length > fWidths.length ? fWidths.length : widths.length;
        for(; i < widthCount; i++)
        {
          fWidths[i] = parseFloat(widths[i], 0);
          allWidth += fWidths[i];
        }
        for(; i < fWidths.length; i++)
        {
          if(allWidth < 100)
            fWidths[i] = (100-allWidth)/(fWidths.length - widths.length);
        }
      }
      return fWidths;
    }

    private void calculateGroup(String datasetName, int row, String groupField, boolean isNewCalcGroup)
    {
      if(groupValues == null && groupField != null)
        groupValues = new BigDecimal[sumFields.length];
      if(totalValues == null)
        totalValues = new BigDecimal[sumFields.length];
      for(int j=0; j<totalValues.length; j++)
      {
        if(groupField != null && isNewCalcGroup)
          groupValues[j] = null;

        String value = getDataSetFieldValue(datasetName, row, sumFields[j], null);
        if(!isDouble(value))
          continue;
        BigDecimal curValue = new BigDecimal(value);
        totalValues[j] = totalValues[j] == null ? curValue : totalValues[j].add(curValue);

        if(groupField != null)
          groupValues[j] = groupValues[j] == null ? curValue : groupValues[j].add(curValue);
      }
    }
  }

  /**
   * 设置页脚
   * <p>Title: 设置页脚的类</p>
   * <p>Description: 设置每页的页码和总页数</p>
   * <p>Copyright: Copyright (c) 2001</p>
   * <p>Company: ITRD</p>
   * @author hukn
   * @version 1.0
   */
  final class PdfPageEvents extends PdfPageEventHelper implements Serializable
  {

    private PdfContentByte cb;
    private PdfTemplate[] templates  = new PdfTemplate[3];
    private String[]      pageTexts  = new String[3];
    private String[]      footerText = new String[3];
    private float[]       pageTextWidths = new float[3];

    private String pageText;

    /**
     * 构造函数
     * @param pageInfo 页面信息的HashMap
     */
    public PdfPageEvents(Map hmPageInfo)
    {
      String[] keys = new String[]{FOOTER_LEFT, FOOTER_CENTER, FOOTER_RIGHT};
      for(int i=0; i<keys.length; i++)
      {
        String footer = (String)hmPageInfo.get(keys[i]);
        if(footer != null) {
          pageText = null;
          footerText[i] = getFooter(footer);
          pageTexts[i] = pageText;
        }
      }
      pageText = null;
    }


    public void onOpenDocument(PdfWriter writer, Document document) {
      cb = writer.getDirectContent();
      for(int i=0; i<pageTexts.length; i++){
        if(pageTexts[i] == null)
          continue;
        pageTextWidths[i] = pageTexts[i].length() == 0 ? 0 : bfChinese.getWidthPoint(pageTexts[i], 8);
        templates[i] = cb.createTemplate(50+pageTextWidths[i], 50);
      }
    }


    public void onEndPage(PdfWriter writer, Document document)
    {
      float x;
      if(footerText[0] != null)
      {
        x = document.leftMargin();
        String left = StringUtils.replace(footerText[0], "&P", String.valueOf(writer.getPageNumber()));
        printFooter(x, left, 0);
      }
      if(footerText[1] != null)
      {
        String center = StringUtils.replace(footerText[1], "&P", String.valueOf(writer.getPageNumber()));
        x = document.getPageSize().width() - document.leftMargin() - document.rightMargin();
        x = (x - bfChinese.getWidthPoint(center, 8) - pageTextWidths[1])/2;
        x += document.leftMargin();
        printFooter(x, center, 1);
      }
      if(footerText[2] != null)
      {
        pageText = null;
        String right = StringUtils.replace(footerText[2], "&P", String.valueOf(writer.getPageNumber()));
        x = document.getPageSize().width() - document.leftMargin() - document.rightMargin();
        x -= bfChinese.getWidthPoint(right, 8) - pageTextWidths[2] - bfChinese.getWidthPoint("123", 8);
        printFooter(x, right, 2);
      }
    }

    public void onCloseDocument(PdfWriter writer, Document document) {
      for(int i=0; i<templates.length; i++)
      {
        if(templates[i] == null)
          continue;
        templates[i].beginText();
        templates[i].setFontAndSize(bfChinese, 8);
        templates[i].showText(writer.getPageNumber()-1 + pageTexts[i]);
        templates[i].endText();
      }
      cb = null;
      templates = null;
      pageTexts = null;
      footerText = null;
      pageTextWidths = null;
    }

    //
    private String getFooter(String footer)
    {
      int index = footer.indexOf("&N");
      if(index >-1)
      {
        pageText = footer.substring(index + "&N".length());
        footer = footer.substring(0, index);
      }
      return footer;
    }

    //
    private void printFooter(float x, String footer, int suffix)
    {
      float fontWidth = bfChinese.getWidthPoint(footer, 8);
      float bottom = document.bottomMargin();
      float y = bottom-fontWidth-8 > 0 ? bottom/2-8 : bottom-8;
      cb.beginText();
      cb.setFontAndSize(bfChinese, 8);
      cb.setTextMatrix(x, y);
      cb.showText(footer);
      cb.endText();
      if(templates[suffix] != null){
        cb.addTemplate(templates[suffix], x + fontWidth, y);
      }
    }
  }

  final class PrecisionPrint implements Serializable
  {
    private PdfContentByte cb =null;
    private float paperWidth, paperHeight;

    /**
     * 创建Docment对象的实例
     */
    private void createDocument()
    {
      Rectangle pageSize = null;
      Map page = templet.getPage();
      try{
        paperWidth  = Float.parseFloat((String)page.get(PAGE_WIDTH)) *72f/2.54f;
        paperHeight = Float.parseFloat((String)page.get(PAGE_HEIGHT))*72f/2.54f;
        pageSize = new Rectangle(paperWidth, paperHeight);
      }
      catch(Exception e){
        pageSize = new Rectangle(595, 842);
      }
      try{
        defaultFontSize = Integer.parseInt((String)page.get(FONT_SIZE));
      }
      catch(Exception e){ defaultFontSize = 10;  }

      document = new Document(pageSize, 0, 0, 0, 0);
    }

    /**
     * 加载数据并创建套打的PDF
     * @param os 输出流
     * @throws Exception
     */
    public void createPdfs(OutputStream os) throws BadElementException, DocumentException
    {
      createDocument();
      //
      printBill(os);

      cb = null;
    }

    /**
     * 创建套打的PDF
     * @param os 输出流
     * @throws Exception
     */
    private void printBill(OutputStream os) throws BadElementException, DocumentException
    {
      try{
        writer = PdfWriter.getInstance(document, os);
        document.addCreator("ENGINE");
        document.open();
        writer.addJavaScript(PdfAction.javaScript("this.print(true);\r", writer));
        cb = writer.getDirectContent();
        if(reportDatas == null || templet == null)
          return;

        HtmlTable[] htmlTables = templet.getEnableTables();
        if(htmlTables == null)
          return;

        HtmlTable head = null, body = null, foot = null;
        for(int i=0; i<htmlTables.length; i++)
        {
          Map tableInfo = htmlTables[i].getTableInfo();
          if(tableInfo == null)
            continue;
          String type = (String)tableInfo.get(TYPE);
          if(type.equalsIgnoreCase(HEAD))
          {
            if(head == null)
              head = htmlTables[i];
          }
          else if(type.equalsIgnoreCase(BODY))
          {
            if(body == null)
              body = htmlTables[i];
          }
          else if(type.equalsIgnoreCase(FOOT))
          {
            if(foot == null)
              foot = htmlTables[i];
          }
        }

        for(int i=0; i<reportDatas.length; i++)
        {
          reportData = reportDatas[i];
          cb.beginText();

          if(head != null)
            printHeadOrFoot(false, head);

          if(body != null)
            printBody(body);

          if(foot != null)
            printHeadOrFoot(true, foot);

          cb.endText();
          if(reportDatas.length >1 && i != reportDatas.length-1)
            document.newPage();
        }
      }
      finally{
        if(document !=null && document.isOpen())
          document.close();
      }
    }

    private void printHeadOrFoot(boolean isFoot, HtmlTable htmlTable)
    {
      HtmlTableCell[] htmlCells = getFirstCells(htmlTable);
      if(htmlCells == null)
        return;
      Map tableInfo = htmlTable.getTableInfo();
      String defaultDetSetName = (String)tableInfo.get(DATASET);
      BaseFont defaultFont = getBaseFonts((String)tableInfo.get(FONT_FACE));

      float x, y;
      for(int i=0; i < htmlCells.length; i++)
      {
        Map fieldInfo = htmlCells[i].getCellInfo();
        String fieldName = (String)fieldInfo.get(FIELD);
        String value = getDataSetFieldValue(defaultDetSetName, 0, fieldName, fieldInfo);

        if(value == null || value.length() == 0)
          continue;

        BaseFont bf = getBaseFonts((String)fieldInfo.get(FONT_FACE));
        boolean isBold = isTrue((String)fieldInfo.get(FONT_BOLD));
        if(bf == null)
          bf = defaultFont != null ? defaultFont : isBold ? bfChinese_bold : bfChinese;

        x = parseFloat((String)fieldInfo.get(X), 0) * 72f/2.5f;
        y = paperHeight - parseFloat((String)fieldInfo.get(Y), 0) * 72f/2.5f;

        int font_size = parseInt((String)fieldInfo.get(FONT_SIZE), defaultFontSize);
        cb.setFontAndSize(bf, font_size);

        int totalLength = value.getBytes().length;
        int maxLength = parseInt((String)fieldInfo.get(MAXLENGTH), totalLength);
        int align = convertAlign((String)fieldInfo.get(ALIGN));
        //System.out.println("htmlCells["+i+"]:"+value+",x="+x+",y="+y+",align:"+align);
        if(totalLength <= maxLength)
          cb.showTextAligned(align, value, x, y, 0);
        else
        {
          if(isFoot && isTrue((String)fieldInfo.get(MULTIROW)))
          {
            while(totalLength < maxLength)
            {
              String print = StringUtils.getUnicodeSubString(value, maxLength);
              cb.showTextAligned(align, print, x, y, 0);
              y -= bf.getWidthPoint("过", font_size);
              value = value.substring(print.length());
              totalLength = value.getBytes().length;
            }
          }
          cb.showTextAligned(
              align,
              totalLength <= maxLength ? value : StringUtils.getUnicodeSubString(value, maxLength),
              x, y, 0);
        }
      }
    }

    private void printBody(HtmlTable htmlTable)
    {
      HtmlTableCell[] htmlCells = getFirstCells(htmlTable);
      if(htmlCells == null)
        return;

      Map tableInfo = htmlTable.getTableInfo();
      String defaultDetSetName = (String)tableInfo.get(DATASET);
      BaseFont bf = getBaseFonts((String)tableInfo.get(FONT_FACE));
      if(bf == null)
        bf = bfChinese;
      float fStartY = paperHeight - parseFloat((String)tableInfo.get(Y), 0) * 72f/2.5f;
      float fRowSpace = parseFloat((String)tableInfo.get(SPACE), 0)* 72f/2.5f;

      int num = getDataSetRowCount(defaultDetSetName);
      for(int j=0; j< num; j++)
      {
        boolean isOverOne = false;
        float fPrintedY = fStartY;
        for(int i=0; i < htmlCells.length; i++)
        {
          Map fieldInfo = htmlCells[i].getCellInfo();
          int font_size = parseInt((String)tableInfo.get(FONT_SIZE), defaultFontSize);
          cb.setFontAndSize(bf, font_size);
          String fieldName = (String)fieldInfo.get(FIELD);
          String value = getDataSetFieldValue(defaultDetSetName, j, fieldName, fieldInfo);
          if(value == null || value.length() == 0)
            continue;

          float fStartX = parseFloat((String)fieldInfo.get(X), 0)* 72f/2.5f;
          int totalLength = value.getBytes().length;
          int maxLength = parseInt((String)fieldInfo.get(MAXLENGTH), totalLength);
          int align = convertAlign((String)fieldInfo.get(ALIGN));

          boolean nowrap = isTrue((String)fieldInfo.get(NOWRAP));
          boolean isMulti = isTrue((String)fieldInfo.get(MULTIROW));
          if(nowrap || isMulti)
          {
            if(nowrap)
              isOverOne = true;
            float fCellY = fStartY;
            while(totalLength < maxLength)
            {
              String print = StringUtils.getUnicodeSubString(value, maxLength);
              cb.showTextAligned(align, print, fStartX, fCellY, 0);
              fCellY -= bf.getWidthPoint("过", font_size);
              value = value.substring(print.length());
              totalLength = value.getBytes().length;
            }
            if(totalLength > 0)
            {
              cb.showTextAligned(
                  align,
                  totalLength <= maxLength ? value : StringUtils.getUnicodeSubString(value, maxLength),
                  fStartX, fCellY, 0);
              fCellY -= bf.getWidthPoint("过", font_size);
            }
            if(fCellY < fPrintedY)
              fPrintedY = fCellY;
          }
          else
            cb.showTextAligned(align, StringUtils.getUnicodeSubString(value, maxLength), fStartX, fStartY, 0);
        }
        if(isOverOne)
          fStartY = fPrintedY;
        fStartY -= fRowSpace;
      }
    }


    private HtmlTableCell[] getFirstCells(HtmlTable htmlTable)
    {
      if(htmlTable.getRowCount() < 1)
        return null;
      HtmlTableRow row = htmlTable.getTableRow(0);
      return row.getCells();
    }

    private int convertAlign(String align)
    {
      if(align == null)
        return PdfContentByte.ALIGN_LEFT;
      else if(align.equalsIgnoreCase(ALIGN_RIGHT))
        return PdfContentByte.ALIGN_RIGHT;
      else if(align.equalsIgnoreCase(ALIGN_CENTER))
        return PdfContentByte.ALIGN_CENTER;
      else
        return PdfContentByte.ALIGN_LEFT;
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

  /**
   * 得到常量的值
   * @param key 键
   * @return 返回值
   */
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

  /**
   * 将字符串中含有常量的特殊字符替换掉
   * @param value 替换前字符串
   * @param context 上下文信息
   * @return 返回替换后字符串
   */
  private static String replaceObject(String value, Context context)
  {
    if(value == null)
      return null;
    Map contants = (Map)context.get("constant");
    if(contants == null)
      return value;
    return MessageFormat.format(value, contants);
  }
}
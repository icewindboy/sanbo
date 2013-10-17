package engine.report.util;

import java.io.Serializable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;
import java.net.URL;

import engine.util.ParseProperties;
import engine.util.ParseUnit;
import engine.util.HtmlParser;
import engine.report.util.*;
/**
 * Title:        print
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ITRD
 * @author hukn
 * @version 1.0
 */

public final class ReportTempletParser implements Serializable, Tag
{

  private static ReportTempletParser templetParser = new ReportTempletParser();//

  private ParseUnit[] units = null;

  private HtmlParser htmlParser = new HtmlParser();

  private int unitNum = 0;
  private TempletData templetInfo = null;

  /**
   * 私有化构造函数
   */
  private ReportTempletParser(){}

  /**
   * 得到保存模板的Map
   * @return 返回保存模板的Map
   */
  private static TempletData getTemplet(TempletData templet)
  {
    templetParser.templetInfo = templet;
    templetParser.parseHtml();
    TempletData info = templetParser.templetInfo;
    templetParser.release();
    return info;
  }

  /**
   * 传入url创建模板
   * @param templetUrl 模板的url
   * @return 返回模板对象
   * @throws IOException
   */
  public static TempletData createTemplet(URL templetUrl) throws IOException
  {
    return createTemplet(templetUrl, null);
  }
  /**
   * 传入url创建模板
   * @param templetUrl 模板的url
   * @param replaces 特定字符的信息,key:原字符,value：新字符
   * @return 返回模板对象
   * @throws IOException
   */
  public static TempletData createTemplet(URL templetUrl, Map replaces) throws IOException
  {
    return createTemplet(null, templetUrl, null);
  }
  /**
   * 传入url创建模板
   * @param templet 模板的实例, 重用该实例, 不用在new出实例来
   * @param templetUrl 模板的url
   * @param replaces 特定字符的信息,key:原字符,value：新字符
   * @return 返回模板对象
   * @throws IOException
   */
  public static TempletData createTemplet(TempletData templet, URL templetUrl, Map replaces) throws IOException
  {
    synchronized(templetParser)
    {
      templetParser.units = templetParser.htmlParser.parseUrl(templetUrl, replaces);
      return getTemplet(templet);
    }
  }

  /**
   * 传入动态模板信息创建模板
   * @param templetBuf 动态模板信息
   * @return 返回模板对象
   * @throws IOException
   */
  public static TempletData createTemplet(StringBuffer templetBuf) throws IOException
  {
    return createTemplet(templetBuf, null);
  }

  /**
   * 传入动态模板信息创建模板
   * @param templetBuf 动态模板信息
   * @param replaces 特定字符的信息,key:原字符,value：新字符
   * @return 返回模板对象
   * @throws IOException
   */
  public static TempletData createTemplet(StringBuffer templetBuf, Map replaces) throws IOException
  {
    return createTemplet(null, templetBuf, null);
  }

  /**
   * 传入动态模板信息创建模板
   * @param templet 模板的实例, 重用该实例, 不用在new出实例来
   * @param templetBuf 动态模板信息
   * @param replaces 特定字符的信息,key:原字符,value：新字符
   * @return 返回模板对象
   * @throws IOException
   */
  public static TempletData createTemplet(TempletData templet, StringBuffer templetBuf, Map replaces) throws IOException
  {
    synchronized(templetParser)
    {
      templetParser.units = templetParser.htmlParser.parseString(templetBuf.toString(), replaces);
      return getTemplet(templet);
    }
  }

  /**
   * 传入动态模板信息创建模板
   * @param in 包含模板信息的流
   * @return 返回模板对象
   * @throws IOException
   */
  public static TempletData createTemplet(InputStream in) throws IOException
  {
    return createTemplet(in, null);
  }

  /**
   * 传入动态模板信息创建模板
   * @param in 包含模板信息的流
   * @param replaces 特定字符的信息,key:原字符,value：新字符
   * @return 返回模板对象
   * @throws IOException
   */
  public static TempletData createTemplet(InputStream in, Map replaces) throws IOException
  {
    return createTemplet(null, in, null);
  }

  /**
   * 传入动态模板信息创建模板
   * @param templet 模板的实例, 重用该实例, 不用在new出实例来
   * @param in 包含模板信息的流
   * @param replaces 特定字符的信息,key:原字符,value：新字符
   * @return 返回模板对象
   * @throws IOException
   */
  public static TempletData createTemplet(TempletData templet, InputStream in, Map replaces) throws IOException
  {
    if(in == null)
      throw new NullPointerException("input stream is null");
    synchronized(templetParser)
    {
      templetParser.units = templetParser.htmlParser.parseFile(in, "",replaces);
      return getTemplet(templet);
    }
  }

  /**
   * 传入文件路径信息创建模板
   * @param templetFile 模板文件路径
   * @return 返回模板对象
   * @throws IOException
   */
  public static TempletData createTemplet(String templetFile) throws IOException
  {
    return createTemplet(templetFile, null);
  }

  /**
   * 传入文件路径信息创建模板
   * @param templetFile 模板文件路径
   * @param replaces 特定字符的信息,key:原字符,value：新字符
   * @return 返回模板对象
   * @throws IOException
   */
  public static TempletData createTemplet(String templetFile, Map replaces) throws IOException
  {
    return createTemplet(null, templetFile, null);
  }
  /**
   * 传入文件路径信息创建模板
   * @param templet 模板的实例, 重用该实例, 不用在new出实例来
   * @param templetFile 模板文件路径
   * @param replaces 特定字符的信息,key:原字符,value：新字符
   * @return 返回模板对象
   * @throws IOException
   */
  public static TempletData createTemplet(TempletData templet, String templetFile, Map replaces) throws IOException
  {
    synchronized(templetParser)
    {
      templetParser.units = templetParser.htmlParser.parse(templetFile, replaces);
      return getTemplet(templet);
    }
  }

  private void parseHtml()
  {
    unitNum =0;
    if(templetInfo == null)
      templetInfo = new TempletData();
    else
      templetInfo.clear();

    ParseUnit unit;
    String tagName;
    while(unitNum < units.length)
    {
      unit = units[unitNum];
      unitNum++;
      if (!unit.isTag())
        continue;
      tagName = unit.getTagName();
      if(unit.isStartTag() || unit.isEmptyTag())
      {
        if (tagName.equals(TABLE))
        {
          HtmlTable htmlTable = new HtmlTable(unit.getAttributes());
          templetInfo.addTable(htmlTable);
          //
          if(unit.isStartTag())
            parseTable(htmlTable);
        }
        else if(tagName.equals(TITLE))
          templetInfo.setTitle((String)unit.getAttribute(NAME));
        else if(tagName.equals(PAGE))//
          templetInfo.setPage(unit.getAttributes());
        else if(tagName.equals(DATASET))
        {
          templetInfo.addDataSet(unit.getAttributes());
          if(unit.isStartTag())
          {
            String datasetName = (String)unit.getAttribute(NAME);
            parseDataSet(datasetName);
          }
        }
        else if(tagName.equals(LINK))
          templetInfo.addOther(unit.toString());
        else if(tagName.equals(IFRAME))
          templetInfo.addOther(unit.toString()+"</"+IFRAME+">");
        else if(tagName.equals(STYLE) || tagName.equals(SCRIPT))
          parseOther(unit);
        else if(tagName.equals(FIELD))
          templetInfo.addField(unit.getAttributes());
        else if(tagName.equals(WHERE))
          templetInfo.addWhere(unit.getAttributes());
        else if(tagName.equals(PARAM))
          templetInfo.addParam(unit.getAttributes());
      }
    }
  }

  private void parseDataSet(String datasetName)
  {
    while(unitNum < units.length)
    {
      ParseUnit unit = units[unitNum];
      unitNum++;
      if(!unit.isTag())
        continue;
      String tagName = unit.getTagName();
      if(unit.isStartTag())
      {
        if(tagName.equals(FIELD))
        {
          Map field = unit.getAttributes();
          templetInfo.addField(datasetName, field);
        }
        else if(tagName.equals(WHERE))
        {
          Map where = unit.getAttributes();
          where.put(DATASET, datasetName);
          templetInfo.addWhere(where);
        }
        else if(tagName.equals(PARAM))
        {
          Map param = unit.getAttributes();
          param.put(DATASET, datasetName);
          templetInfo.addParam(param);
        }
      }
      else if(unit.isEndTag())
      {
        if (tagName.equals(DATASET))
          break;
      }
    }
  }

  private void parseTable(HtmlTable fatherTable)
  {
    ParseUnit unit;
    String tagName;
    while(unitNum < units.length)
    {
      unit = units[unitNum];
      unitNum++;
      if(!unit.isTag())
        continue;
      tagName = unit.getTagName();
      if(unit.isStartTag() || unit.isEmptyTag())
      {
        if(tagName.equals(TR))
        {
          HtmlTableRow tableRow = new HtmlTableRow((Map)unit.getAttributes());
          fatherTable.addRow(tableRow);
          if(unit.isStartTag())
            parseTr(tableRow);
        }
        else if(tagName.equals(WIDTHS))
        {
          fatherTable.getTableInfo().put(WIDTHS, unit.getAttributes());
        }
      }
      else if(unit.isEndTag())
      {
        if (tagName.equals(TABLE))
          break;
      }
    }
  }

  private void parseTr(HtmlTableRow tableRow)
  {
    ParseUnit unit;
    String tagName;
    while(unitNum < units.length)
    {
      unit = units[unitNum];
      unitNum++;
      if(!unit.isTag())
        continue;
      tagName = unit.getTagName();
      if(unit.isStartTag())
      {
        if(tagName.equals(TD))
        {
          HtmlTableCell cell = new HtmlTableCell((Map)unit.getAttributes());
          tableRow.addCell(cell);
          parseTd(cell);
        }
      }
      else if(unit.isEndTag())
      {
        if (tagName.equals(TR))
          break;
      }
    }
  }

  private void parseTd(HtmlTableCell cell)
  {
    StringBuffer buf = new StringBuffer();
    ParseUnit unit;
    String tagName;
    while(unitNum < units.length)
    {
      unit = units[unitNum];
      unitNum++;
      if(!unit.isTag())
      {
        if(unit.isComment() || unit.isDeclaration() || unit.isJsp())
          continue;
        buf.append(unit.toString());
        continue;
      }
      tagName = unit.getTagName();
      if(unit.isStartTag() || unit.isEmptyTag())
      {
        if(tagName.equals(VALUE))
        {
          if(buf.length() > 0)
          {
            cell.addContent(buf.toString());
            buf = new StringBuffer();
          }
          cell.addValue(unit.getAttributes());
        }
        else if (tagName.equals(BR))
          buf.append("\n");
        else
          buf.append(unit.toString());
      }
      else if(unit.isEndTag())
      {
        if (tagName.equals(TD))
        {
          if(buf.length() > 0)
            cell.addContent(buf.toString());
          break;
        }
        else
          buf.append(unit.toString());
      }
    }
  }

  /**
   * 分析HTML的td标签
   */
  final static void addCells(HtmlTableRow tableRow, String cells)
  {
    synchronized(templetParser)
    {
      ParseUnit[] units = null;
      try{
        units = templetParser.htmlParser.parseString(cells);
      }
      catch(Exception ex){
        throw new engine.util.EngineRuntimeException(ex.getMessage(), ex);
      }
      //
      for(int i=0; i < units.length; i++)
      {
        ParseUnit unit = units[i];
        if(!unit.isTag())
          continue;
        String tagName = unit.getTagName();
        if(unit.isStartTag() && tagName.equals(TD))
        {
          HtmlTableCell cell = new HtmlTableCell((Map)unit.getAttributes());
          tableRow.addCell(cell);
          StringBuffer buf = new StringBuffer();
          for(i++; i < units.length; i++)
          {
            unit = units[i];
            if(!unit.isTag())
            {
              if(unit.isComment() || unit.isDeclaration() || unit.isJsp())
                continue;
              buf.append(unit.toString());
              continue;
            }
            tagName = unit.getTagName();
            if(unit.isStartTag() || unit.isEmptyTag())
            {
              if(tagName.equals(VALUE))
              {
                if(buf.length() > 0)
                {
                  cell.addContent(buf.toString());
                  buf = new StringBuffer();
                }
                cell.addValue(unit.getAttributes());
              }
              else if (tagName.equals(BR))
                buf.append("\n");
              else
                buf.append(unit.toString());
            }
            else if(unit.isEndTag())
            {
              if (tagName.equals(TD))
              {
                if(buf.length() > 0)
                  cell.addContent(buf.toString());
                break;
              }
              else
                buf.append(unit.toString());
            }
          }
        }
      }
    }
  }

  /**
   * 在模板中添加field标签信息
   * @param templet 报表模板对象
   * @param datasetName dataset名称
   * @param fields field标签信息字符串
   */
  final static void addFields(TempletData templet, String datasetName, String fields)
  {
    synchronized(templetParser)
    {
      ParseUnit[] units = null;
      try{
        units = templetParser.htmlParser.parseString(fields);
      }
      catch(Exception ex){
        throw new engine.util.EngineRuntimeException(ex.getMessage(), ex);
      }
      //
      for(int i=0; i < units.length; i++)
      {
        ParseUnit unit = units[i];
        if(!unit.isTag())
          continue;
        String tagName = unit.getTagName();
        if(unit.isStartTag() && tagName.equals(FIELD))
        {
          Map field = unit.getAttributes();
          templet.addField(datasetName, field);
        }
      }
    }
  }

  private void parseOther(ParseUnit startTagUnit)
  {
    StringBuffer buf = new StringBuffer(startTagUnit.toString());
    if(startTagUnit.isEmptyTag())
    {
      templetInfo.addOther(buf.toString());
      return;
    }

    int startLength = buf.length();
    ParseUnit unit;
    String tagName;
    while(unitNum < units.length)
    {
      unit = units[unitNum];
      unitNum++;
      if(!unit.isTag())
      {
        if(unit.isComment() || unit.isDeclaration() || unit.isJsp())
          continue;
        buf.append(unit.toString());
        continue;
      }
      tagName = unit.getTagName();
      if(unit.isEndTag())
      {
        if (tagName.equals(STYLE) || tagName.equals(SCRIPT))
        {
          if(buf.length() > startLength)
          {
            buf.append(unit.toString());
            templetInfo.addOther(buf.toString());
          }
          break;
        }
        else
          buf.append(unit.toString());
      }
    }
  }

  /**
   * 释放资源
   */
  private void release()
  {
    this.units = null;
    this.unitNum = 0;
    this.templetInfo = null;
  }
}
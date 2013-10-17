package engine.web.html;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import com.borland.dx.dataset.*;
import engine.dataset.*;
import engine.dataset.sql.*;
import engine.util.StringUtils;
import engine.util.Format;
import engine.util.MessageFormat;
import engine.util.log.Log;
import engine.web.lookup.Lookup;
import engine.web.lookup.LookupFacade;
import engine.web.taglib.*;
/**
 * Title:        HTML表格内容的生成器
 * Description:  HTML表格内容的生成器
 * Copyright:    Copyright (c) 2001
 * Company:      engine
 * @author hukn
 * @version 1.0
 */

public final class HtmlTableProducer implements java.io.Serializable
{
  private static MessageFormat msgFormat = new MessageFormat();
  //
  private String tableName = null;
  private String tableAliasName = null;
  private DataSet dsRepData = null;
  private boolean isRegData = false;
  private boolean isInitQuery = false;

  private TableInfo tableInfo = new TableInfo();//
  private QueryWhere whereInfo = new DefaultQueryWhere();
  private HtmlPrintCellListener listener = null;

  private Log log = new Log(getClass());

  private Map initParams = null;

  /**
   * 构造函数
   * @param dsData 需要打印的数据集
   * @param tableName 表名
   */
  public HtmlTableProducer(DataSet dsData, String tableName)
  {
    this(dsData, tableName, null);
  }

  /**
   * 构造函数
   * @param dsData 需要打印的数据集
   * @param tableName 表名
   * @param tableAliasName 表的别名
   */
  public HtmlTableProducer(DataSet dsData, String tableName, String tableAliasName)
  {
    this.dsRepData = dsData;
    this.tableName = tableName;
    this.tableAliasName = tableAliasName == null ? tableName : tableAliasName;
  }

  public String getTableName()
  {
    return this.tableName;
  }

  public String getTableAliasName()
  {
    return this.tableAliasName;
  }
  /**
   * 初始化
   * @param request WEB请求
   * @param personid 登录人员ID
   * @throws Exception 异常
   */
  public void init(HttpServletRequest request, String personid) throws Exception
  {
    initWhereInfo(request);
    tableInfo.openTableInfo(request, personid, tableName);
    isRegData = false;
  }

  /**
   * 将Table的各个字段标题信息, 打印到网页上
   * @param pageContext  JSP页面上下文
   * @param style title的分格
   * @throws Exception 异常
   */
  public void printTitle(PageContext pageContext, String style) throws Exception
  {
    printTitle(pageContext, style, false);
  }

  /**
   * 将Table的各个字段标题信息, 打印到网页上
   * @param pageContext  JSP页面上下文
   * @param style title的分格
   * @param isBak 是表示用户自定义的字段，否表示用户自定义字段和要显示的字段
   * @throws Exception 异常
   */
  public void printTitle(PageContext pageContext, String style, boolean isBak) throws Exception
  {
    regLookUp();
    FieldInfo[] fields = isBak ? tableInfo.getBakFieldCodes() : tableInfo.getListFieldCodes();
    JspWriter out = pageContext.getOut();
    for(int i=0; i<fields.length; i++)
    {
      printField(fields[i], "<td nowrap ", style, null, out, HtmlPrintCellResponse.PRINT_TITLE, 0);
    }
  }
  /**
   * 将td信息, 打印到网页上
   * @param pageContext  JSP页面上下文
   * @param style td的分格
   * @throws Exception 异常
   */
  public void printCells(PageContext pageContext, String style) throws Exception
  {
    regLookUp();

    FieldInfo[] fields = tableInfo.getListFieldCodes();
    HtmlPrintCellResponse rowResponse = null;

    JspWriter out = pageContext.getOut();

    Column col = null;
    String header = null;
    for(int i=0; i<fields.length; i++)
    {
      int fieldType = fields[i].getType();
      if(fieldType == FieldInfo.VIRTUAL_TYPE)
        printField(fields[i], "<td nowrap ", style, null, out, HtmlPrintCellResponse.PRINT_BODY, -1);//-1表示虚拟的
      else
      {
        col = dsRepData.getColumn(fields[i].getFieldcode());
        int dataType = col.getDataType();
        int ordinal = col.getOrdinal();
        if(fieldType == FieldInfo.SELECT_TYPE || fields[i].getLinktable() !=null)
          header = "<td nowrap ";
        else if(dataType == Variant.BIGDECIMAL || dataType == Variant.INT || dataType == Variant.FLOAT || dataType == Variant.DOUBLE)
          header = "<td nowrap align=right ";
        else
          header = "<td nowrap ";
        printField(fields[i], header, style, null, out, HtmlPrintCellResponse.PRINT_BODY, ordinal);
      }
    }
  }

  /**
   * 打印空白的td信息到网页上
   * @param pageContext  JSP页面上下文
   * @param style td的分格
   * @throws Exception 异常
   */
  public void printBlankCells(PageContext pageContext, String style) throws Exception
  {
    printBlankCells(pageContext, style, false);
  }

  /**
   * 打印空白的td信息到网页上
   * @param pageContext  JSP页面上下文
   * @param style td的分格
   * @param isBak 是表示用户自定义的字段，否表示用户自定义字段和要显示的字段
   * @throws Exception 异常
   */
  public void printBlankCells(PageContext pageContext, String style, boolean isBak) throws Exception
  {
    regLookUp();

    FieldInfo[] fields = isBak ? tableInfo.getBakFieldCodes() : tableInfo.getListFieldCodes();

    JspWriter out = pageContext.getOut();

    for(int i=0; i<fields.length; i++)
      printField(fields[i], "<td ", style, "&nbsp;", out, HtmlPrintCellResponse.PRINT_BLANK, 0);
  }

  /**
   * 将Table的字段信息
   * @param field 字段对象
   * @param header tag头
   * @param style tag风格
   * @param content 内容
   * @param out JspWriter对象
   * @param printType 打印的类型:表头,表体,表空白行
   * @param ordinal 字段在数据集的顺序(打印表体时，需要用到)
   * @throws Exception 异常
   */
  private void printField(FieldInfo field, String header, String style, String content, JspWriter out, int printType, int ordinal) throws Exception
  {
    String[] showFields = field.getShowFields();

    StringBuffer cellHeader = new StringBuffer(header);
    if(style != null)
      cellHeader.append(style);
    if(showFields == null)
    {
      if(printType == HtmlPrintCellResponse.PRINT_TITLE)
        content = field.getFieldname();
      else if(printType == HtmlPrintCellResponse.PRINT_BODY)
        content = ordinal < 0 ? "" : transFieldValue(field, EngineDataSet.getValue(dsRepData, ordinal));

      print(field, cellHeader, new StringBuffer(content), out, printType);
    }
    else
    {
      RowMap row = null;
      if(printType == HtmlPrintCellResponse.PRINT_BODY)
      {
        Lookup lookUp = tableInfo.getLookup(field.getFieldcode());
        row = lookUp == null ? null : lookUp.getLookupRow(EngineDataSet.getValue(dsRepData, ordinal));
      }
      int count = showFields.length;
      for(int j=0; j < count; j++)
      {
        if(printType == HtmlPrintCellResponse.PRINT_TITLE)
          content = field.getShowFieldName(j);
        else if(printType == HtmlPrintCellResponse.PRINT_BODY)
          content = row == null ? "&nbsp;" : (j == 0 && field.getUrl() != null)
                                ? appendUrl(field, row.get(showFields[j])) : row.get(showFields[j]);

        if(j < count-1)
        {
          cellHeader.append(">").append(content).append("</td>").append(header);
          if(style != null)
            cellHeader.append(style);
        }
      }
      print(field, cellHeader, new StringBuffer(content), out, printType);
    }
  }

  /**
   * 打印信息
   * @param out JSP输入对象
   * @param rowResponse HtmlPrintCellResponse响应对象
   * @throws IOException IO异常
   */
  private void print(FieldInfo field, StringBuffer cellHeader, StringBuffer cellContent, JspWriter out, int printType) throws Exception
  {
    HtmlPrintCellResponse rowResponse = new HtmlPrintCellResponse(field, cellHeader, cellContent, printType);
    if(listener != null)
      listener.printCell(out, rowResponse, dsRepData);
    if(!rowResponse.response)
      return;

    out.print(rowResponse.getCellHeader());
    out.print(">");
    if(rowResponse.getCellContent() != null)
      out.print(rowResponse.getCellContent());
    out.print("</td>");
  }

  /**
   * 设置打印html行的监听器
   * @param listener html行的监听器
   */
  public void setHtmlPrintCellListener(HtmlPrintCellListener listener)
  {
    this.listener = listener;
  }

  /**
   * 得到当前用户自定义的各个字段
   * @return 返回自定义的各个字段数组
   */
  public FieldInfo[] getBakFieldCodes() //throws Exception
  {
    regLookUp();
    //
    return tableInfo.getBakFieldCodes();
  }

  /**
   * 得到当前用户列表的各个字段
   * @return 返回字段数组
   */
  public FieldInfo[] getListFieldCodes() //throws Exception
  {
    regLookUp();
    //
    return tableInfo.getListFieldCodes();
  }

  /**
   * 得到所有可用字段
   * @return 返回字段数组
   */
  public FieldInfo[] getAllField() //throws Exception
  {
    regLookUp();
    //
    return tableInfo.getAllFields();
  }

  /**
   * 根据字段编码得到字段信息对象
   * @param fieldCode 字段编码
   * @return 字段信息对象
   */
  public FieldInfo getFieldInfo(String fieldCode)
  {
    if(fieldCode == null)
      throw new NullPointerException("the fieldCode must not null.");
    FieldInfo field = (FieldInfo)tableInfo.hashFieldInfos.get(fieldCode.toLowerCase());
    if(field == null){
      String msg = "the field of "+ fieldCode + " not exist.";
      log.error(msg);
      throw new NullPointerException(msg);
    }
    return field;
  }

  /**
   * 得到字段编码对应的LookUp实例对象, 若:没有设置关联表,则为null
   * @param fieldCode 字段编码
   * @return 返回字段编码对应的LookUp实例对象
   */
  public Lookup getLookup(String fieldCode)
  {
    return tableInfo.getLookup(fieldCode);
  }

  /**
   * 转换字段的值用于显示。比如将外键关联的字段转换为相应的值
   * @param fieldCode 字段编码
   * @param fieldValue 给字段在数据库中的值
   * @return 返回装换后的值
   * @throws Exception 异常
   */
  public String transFieldValue(FieldInfo field, String fieldValue) throws Exception
  {
    regLookUp();
    //
    String retu = "";
    synchronized(field){
      String linkTable = field.getLinktable();
      int input = field.getType();
      if(linkTable!=null)
      {
        Lookup lookupBean = (Lookup)tableInfo.getLookup(field.getFieldcode());
        if(lookupBean != null)
          retu = lookupBean.getLookupName(fieldValue);
      }
      else if(input == FieldInfo.SELECT_TYPE)
      {
        List[] enumValues = field.getEnumvalues();
        int index = enumValues[0].indexOf(fieldValue);
        retu = index > -1 ? (String)enumValues[1].get(index) : fieldValue;
      }
      else
        retu = fieldValue;
    }
    //
    return field.getUrl() == null ? retu : appendUrl(field, retu);
  }

  /**
   * 追加超级链接
   * @param field 字段对象
   * @param fieldValue 字段值
   * @return 返回追加超级链接后的值
   */
  private String appendUrl(FieldInfo field, String fieldValue)
  {
    String url = field.getUrl();
    if(url == null)
      return fieldValue;
    if(fieldValue == null || fieldValue.length() ==0)
      return fieldValue;

    url = StringUtils.replace(url, "$", "&");
    StringBuffer buf = new StringBuffer("<a ");
    synchronized(msgFormat)
    {
      msgFormat.applyPattern(url);
      String[] params = msgFormat.getArgumentNames();
      Map arguments = new Hashtable();
      for(int i=0; i<params.length; i++)
        arguments.put(params[i], EngineDataSet.getValue(dsRepData, dsRepData.getColumn(params[i]).getOrdinal()));
      //
      buf.append(msgFormat.format(arguments)).append(">").append(fieldValue).append("</a>");
    }
    return buf.toString();
  }
  /**
   * 得到字段或用户自己定义字段,修改状态的显示字符串
   * @param fieldCode 字段编码
   * @param fieldValue 字段在数据库中的值
   * @param inputName 控件名称
   * @param style 控件的风格
   * @param isReadOnly 是否只读
   * @param isMaxLength 是否自动添控件的maxlength的属性
   * @return 返回最终的控件
   * @throws Exception 异常
   */
  public String getFieldInput(FieldInfo field, String fieldValue, String inputName,
                              String style, boolean isReadOnly, boolean isMaxLength) throws Exception
  {
    regLookUp();
    //
    if(isMaxLength)
    {
      StringBuffer max = new StringBuffer("maxlength=");
      max.append(dsRepData.getColumn(field.getFieldcode()).getPrecision());
      if(style != null)
        max.append(" ").append(style);
      style = max.toString();
    }

    StringBuffer buf = new StringBuffer();
    synchronized(field)
    {
      String linkTable = field.getLinktable();
      int input = field.getType();
      if(linkTable!=null)
      {
        Lookup lookupBean = (Lookup)tableInfo.getLookup(field.getFieldcode());
        if(lookupBean != null)
        {
          buf.append("<input type='text' name='");
          buf.append(inputName).append("' id='").append(inputName).append("' value='");
          buf.append(lookupBean.getLookupName(fieldValue)).append("' ");
          if(style != null)
            buf.append(style);
          buf.append(isReadOnly ? " readonly>" : ">");
        }
      }
      else if(input == FieldInfo.SELECT_TYPE)
      {
        List[] enumValues = field.getEnumvalues();
        if(isReadOnly)
        {
          int index = enumValues[0].indexOf(fieldValue);
          buf.append("<input type='text' name='").append(inputName).append("' id='").append(inputName);
          buf.append("' readonly value='").append(index > -1 ? (String)enumValues[0].get(index) : fieldValue).append("' ");
          if(style != null)
            buf.append(style);
          buf.append(">");
        }
        else
        {
          Select select = new Select();
          select.setName(inputName);
          select.setValue(fieldValue);
          if(style != null)
            select.setStyle(style);

          select.getStartTag(buf);
          Option option = new Option();
          for(int i=0; i<enumValues[0].size(); i++){
            option.setValue((String)enumValues[0].get(i));
            option.getStartTag(buf);
            buf.append(enumValues[1].get(i));
            option.getEndTag(buf);
          }
          select.getEndTag(buf);
          option.release();
          select.release();
        }
        return buf.toString();
      }
      else if(input == FieldInfo.MEMO_TYPE)
      {
        buf = new StringBuffer("<textarea rows='3' name='").append(inputName);
        buf.append("' id='").append(inputName).append("' ");
        if(style != null)
          buf.append(style);
        if(isReadOnly)
          buf.append(" readonly");
        buf.append(">").append(fieldValue).append("</textarea>");
      }
      else
      {
        buf = new StringBuffer("<input type='text' name='").append(inputName);
        buf.append("' id='").append(inputName).append("' value='").append(fieldValue).append("' ");
        if(style != null)
          buf.append(style);
        if(isReadOnly)
          buf.append(" readonly");
        buf.append(">");
      }
    }
    return buf.toString();
  }

  /**
   * 在网页上打印查询条件信息
   * @param pageContext JSP页面上下文
   * @throws IOException 打印异常
   * @throws Exception 异常
   */
  public void printWhereInfo(PageContext pageContext) throws IOException, Exception
  {
    initWhereInfo((HttpServletRequest)pageContext.getRequest());
    printWhereInfo(pageContext, whereInfo);
  }

  public void initWhereInfo(HttpServletRequest req) throws Exception
  {
    if(isInitQuery)
      return;

    initParams = initConstant(req, initParams);
    whereInfo.setInitValues(initParams);

    QueryWhereField[] fields = tableInfo.getWhereFields(tableName);
    whereInfo.clearColumns();
    whereInfo.addWhereField(tableAliasName, fields);
    whereInfo.clearWhereValues();
    isInitQuery = true;
  }

  /**
   * 在网页上打印查询条件信息
   * @param pageContext JSP页面上下文
   * @param where 查询条件信息对象
   * @throws IOException 打印异常
   * @throws Exception 异常
   */
  public static void printWhereInfo(PageContext pageContext, QueryWhere where) throws IOException, Exception
  {
    synchronized(where)
    {

      JspWriter out = pageContext.getOut();
      HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
      //
      QueryWhereField[] fields = where.getShowWhereFields();
      String[] columnNames = where.getShowFieldFullNames();
      int count = columnNames.length;
      engine.web.taglib.Select select = null;
      int k=0;
      while(k < count)
      {
        out.println("<tr>");
        for(int j=0; j < 2; j++)
        {
          out.print("<td nowrap class='td'>");
          if(k < count)
          {
            String name = columnNames[k];
            //
            int dataType = fields[k].getIntDataType();
            String type = fields[k].getType();
            boolean isLookup = type.equals("lookup");
            boolean lookupHasCode = false;
            String codeOnchange = null;
            if(isLookup)
            {
              codeOnchange = (String)fields[k].getAttribute("codeOnchange");
              lookupHasCode = codeOnchange != null && codeOnchange.length() > 0;
            }
            boolean isDate = dataType == Variant.DATE || dataType == Variant.TIMESTAMP;
            boolean span = lookupHasCode || (!isDate && fields[k].isSpan());
            if(j==1 && span)
              out.print("&nbsp;</td><td nowrap class='td'>&nbsp;</td></tr><tr><td nowrap class='td'>");
            out.print(fields[k].getColumn().getCaption());
            out.print("</td><td nowrap class='td'");
            out.print(span ? "colspan='3'>" : ">");
            if(type.equals(QueryWhereField.TEXT) || isLookup)
            {
              if(isLookup)
              {
                String lookupcode = name+"$_code";
                String lookupname = name+"$_name";
                String[] srcVars = null;
                if(lookupHasCode)
                {
                  srcVars = new String[]{"srcVar="+name+"&srcVar="+lookupcode+"&srcVar="+lookupname};
                  codeOnchange = StringUtils.combine(codeOnchange, "@", srcVars);
                  out.print("<input class='edbox' style='width:70' name='");
                  out.print(lookupcode);
                  out.print("' value='");
                  out.print(where.getWhereValue(lookupcode));
                  out.print("'");
                  out.print(" onchange=\"");
                  out.print(codeOnchange);
                  out.print("\">&nbsp;");
                }
                else
                  srcVars = new String[]{"srcVar="+name+"&srcVar="+lookupname};
                //
                String nameOnchange = (String)fields[k].getAttribute("nameOnchange");
                boolean nameIsRead = nameOnchange != null && nameOnchange.length() > 0;
                out.print("<input class=");
                out.print(nameIsRead ? "'edbox'" : "'edline' readonly");
                out.print(" style='width:230' name='");
                out.print(lookupname);
                out.print("' value='");
                out.print(where.getWhereValue(lookupname));
                out.print("'");
                if(nameIsRead)
                {
                  nameOnchange = StringUtils.combine(nameOnchange, "@", srcVars);
                  out.print(" onchange=\"");
                  out.print(nameOnchange);
                  out.print("\"");
                }
                out.print("><input type='hidden' name='"+name+"' value='"+where.getWhereValue(name)+"'>");
                out.print("<input type='image' style='cursor:hand' align='absmiddle' src='../images/view.gif' border=0 onClick=\"");//<img
                String onClick = (String)fields[k].getAttribute("onClick");
                if(onClick != null && onClick.length() > 0)
                {
                  onClick = StringUtils.combine(onClick, "@", srcVars);
                  out.print(onClick);
                }
                out.print("\"><img style='cursor:hand' align='absmiddle' src='../images/delete.gif' border=0 onClick=\"");
                out.print(name); out.print(".value='';");
                if(lookupHasCode)
                {
                  out.print(lookupcode);
                  out.print(".value='';");
                }
                out.print(lookupname); out.print(".value='';\">");
              }
              else
              {
                out.print("<input class='edbox' name='");
                out.print(name);
                out.print("' value='");
                out.print(where.getWhereValue(name));
                out.print("' style='width:");
                out.print(isDate ? "130'" : span ? "300'" : "160'");
                if(isDate)
                  out.print(" maxlength='10' onChange='checkDate(this)'");
                out.print(">");
                if(isDate)
                {
                  out.print("<A href='#'><IMG title=选择日期 onClick='selectDate(");
                  out.print(name);
                  out.print(");' height=20 src='../images/seldate.gif' width=20 align=absMiddle border=0></A>");
                }
              }
            }
            else if(type.equals(QueryWhereField.RADIO) || type.equals(QueryWhereField.MULTI))
            {
              String value = fields[k].getValue();
              if(value == null)
                out.print("&nbsp;");
              else
              {
                String initvalue = null;
                String[] initvalues = null;
                boolean isMulti = type.equals(QueryWhereField.MULTI);
                if(isMulti)
                  initvalues = where.getWhereValues(name);
                else
                  initvalue = where.getWhereValue(name);
                List[] lists = StringUtils.getEnumValues(value);
                for(int i=0; i<lists[0].size(); i++)
                {
                  String temp = (String)lists[0].get(i);
                  if(isMulti)
                    out.print("<input type='checkbox' onKeyDown='return getNextElement()' name='");
                  else
                    out.print("<input type='radio' onKeyDown='return getNextElement()' name='");

                  out.print(name);
                  out.print("' value='");
                  out.print(temp);

                  if(isMulti)
                    out.print(initvalues == null || StringUtils.indexOf(initvalues, temp) < 0 ? "'>" : "' checked>");
                  else
                    out.print(temp.equals(initvalue) ? "' checked>" : "'>");
                  out.print(lists[1].get(i));
                }
              }
            }
            else if(type.equals(QueryWhereField.SELECT))
            {
              String initvalue = where.getWhereValue(name);//得到初始化值
              String lookupTable = fields[k].getLookup();
              String value = fields[k].getValue();
              if(select == null)
              {
                select = new engine.web.taglib.Select();
                select.setPageContext(pageContext);
              }
              try
              {
                String onselect = (String)fields[k].getAttribute("onselect");
                if(onselect != null)
                  select.setOnSelect(onselect);
                //
                select.setAddNull("1");
                select.setStyle("width:160");
                select.setName(name);
                select.setValue(initvalue);
                StringBuffer buf = select.getStartTag();

                if(value != null)
                {
                  List[] enumValues = StringUtils.getEnumValues(value);
                  Option option = new Option();
                  for(int i=0; i<enumValues[0].size(); i++){
                    option.setValue((String)enumValues[0].get(i));
                    option.getStartTag(buf);
                    buf.append(enumValues[1].get(i));
                    option.getEndTag(buf);
                  }
                  option.release();
                }
                else if(lookupTable != null)
                {
                  Lookup lookup = LookupFacade.getInstance(request, lookupTable);
                  String lookupKey = lookupTable+"$key";
                  pageContext.setAttribute(lookupKey, lookup);
                  select.setLookup(lookupKey);
                }
                select.getEndTag(buf);
                out.print(buf);
              }
              finally{
                select.release();
              }
            }
            out.println("</td>");
            if(span)
            {
              k++;
              break;
            }
          }
          else
            out.print("&nbsp;</td><td nowrap class='td'>&nbsp;</td>");

          k++;
        }
        out.println("</tr>");
      }
    }
  }

  /**
   * 得到查询条件信息
   * @return
   */
  public QueryWhere getWhereInfo()
  {
    return whereInfo;
  }

  /**
   * 释放资源
   */
  public void release()
  {
    this.dsRepData = null;
    this.tableName = null;
    if(tableInfo != null)
    {
      this.tableInfo.release();
      this.tableInfo = null;
    }
    if(whereInfo != null)
    {
      whereInfo.clearColumns();
      whereInfo.clearWhereValues();
    }
  }

  /**
   * 为外键的字段注册
   * @throws Exception 异常
   */
  private void regLookUp()// throws Exception
  {
    if(!isRegData){
      tableInfo.regDatas(dsRepData);
      isRegData = true;
    }
  }

  /**
   * 初始化用于替换的pdf常量
   * @return 返回常量的hash
   */
  public static final Map initConstant(HttpServletRequest req)
  {
    return initConstant(req, null);
  }

  /**
   * @todo 引用LoginBean的问题
   * 初始化用于替换的pdf常量
   * @param constants 常量map。先清除里面的所有对象在初始化常量
   * @return 返回常量的hash
   */
  public static final Map initConstant(HttpServletRequest req, Map inits)
  {
    if(inits == null)
      inits = new Hashtable();
    synchronized(inits)
    {
      return inits;
    }
  }
}
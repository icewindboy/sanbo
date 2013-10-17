package engine.action;

import java.math.BigDecimal;
import java.util.StringTokenizer;         
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingListener;
import javax.ejb.EJBObject;

import engine.project.LookupBeanFacade;
import engine.web.observer.Obationable;
import engine.util.Format;
import engine.util.StringUtils;
import engine.util.log.Log;
import engine.util.calc.SimpleCalculator;
import engine.util.MessageFormat;
import engine.util.calc.ExpressionException;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineDataSetProvider;
import engine.dataset.EngineDataSetResolver;
import engine.dataset.RowMap;
import engine.dataset.ProvideInfo;
import com.borland.dx.dataset.*;
import com.sanbo.common.DateUtil;

import java.io.*;

/**
 * Title:        基础Action类
 * Description:  业务逻辑类需要继承的基础类<br>
1. 业务逻辑类<br>
1.1 public final class B_MonthClose extends BaseAction implements Operate<br>
    它继承了BaseAction类并实现了Operate接口.<br>
<br>
1.1 静态方法getInstance().<br>
    每一类的都应该有的静态方法getInstance(),主要是用来得到类的实例。类同与jsp中的usebean的方法。<br>
并可防止jsp中usebean的id不同造成在session用不同的实例。<br>
    主要的工作过程是判断session是否有该bean的实例存在。若没有则创建一个新的实例。<br>
<br>
1.3 构造函数<br>
    构造函数主要初始化一个类实例的内部对象。例子在构造函数中调用jbinit()方法来初始化。<br>
1.3.1<br>
    观察者模式－触发者 <br>
    Obationable.addObactioner(java.lang.String action, Obactioner o) 父类的父类engine.mo.observer.Obationable的方法<br>
    会注册 添加操作的触发对象<br>
    (即:例如:在页面上双击的时候,页面提交的时候等事件. "注册 添加操作的触发对象"的意思也就是我的这个类能处理哪些页面的鼠标事件.<br>
     比如:页面提交的时候这个类触发事件要去执行哪方法, 或者也可以把这个触发操作注册到另外的类上去.<br>
    ).<br>
    主要的业务逻辑就是放在被注册的到的那些方法或别的类中.<br>
<br>
1.3.2<br>
    Operate:是一个接口,只定义了一些常量.这些常量主要被用在<br>
    注册一个对于某个页面事件(如单击造成了这个页面提交事件的发生)对应的操作对象.<br>
    addObactioner(String.valueOf(INIT), new B_MonthClose_Init());<br>
    addObactioner(String.valueOf(POST), new B_MonthClose_Post());<br>
    比如上面的 INIT 即是Operte.INIT = 0. <br>
<br>
1.3.3<br>
    BaseAction.setDataSetProperty(EngineDataSet cds, java.lang.String sql) 父类的方法<br>
    为数据集设置数据提供对象和数据提交对象的属性或与SessionBean连接的属性. <br>
<br>
1.3.3<br>
    EngineDataSet处理数据库的操作对象，不用自己处理与数据添加,修改,删除和事务有关的操作。<br>
不用自己写Connection, ResultSet, Statement及维护事务等的直接DB操作。<br>
<br>
1.4 实现父类的抽象函数doService方法。<br>
   然后,剩下主要的工作就是等待jsp页面上的事件发生.<br>
   调用doService方法, jsp页面通过这个方法调用类的业务逻辑.<br>
<br>
1.4.1 doService 返回 String 类型.供页面使用.<br>
<br>
1.4.2 doService 主要的操作就是调用父类的方法<br>
   RunData data = notifyObactioners(opearate, request, response, null);<br>
   此代码重要的就是 notifyObactioners 这个方法.<br>
   这个方法是 Obationable观察者模式－触发者 中的notifyObactioners 触发相应的action行为所注册的所有执行者对象<br>
   在这里就会根据opearate参数执行前面初始化时注册的对应的操作.(很多情况下是由一个类负担此功能的.)<br>
<br>
1.4.3 这个方法返回RunData集.然后再由doService返回给jsp页面.<br>
1.4.4 notifyObactioners会调用注册的所有执行者对象.<br>
   而执行者对象里面写着的就是所有的关键业务逻辑.<br>
   每一个执行都对象都必须实现 观察者模式－执行者 Obactioner 中的这个执行者execute()的方法.<br>
   execute(java.lang.String action, Obationable o, RunData data, java.lang.Object arg) <br>
   这个方法当触发者对象调用Obactionable的notifyObactioners方法 将调用所有执行者(Obactioner)的这个方法. <br>
 * Copyright:    Copyright (c) 2002
 * Company:      ENGINE
 * @author 江海岛
 * @version 1.0
 */

public abstract class BaseAction extends Obationable implements HttpSessionBindingListener, Serializable
{
  protected EJBObject sessionBeanRemote; // SessionBean的连接的类
  protected EngineDataSetProvider dataSetProvider;    //
  protected EngineDataSetResolver dataSetResolver;
  //protected EngineDataSet cdsTemp;              //临时的数据集
  protected String provideMethodName = "provideData";//提供数据的方法名称
  protected String resolveMethodName = "resovleData";//提交数据的方法名称

  protected Log log = new Log(childClassName());//LogHelper.getLogHelper(childClassName());//日志对象

  //protected BaseAction(){}

  public void valueBound(HttpSessionBindingEvent event){ return; }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected abstract Class childClassName();

  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public abstract String doService(HttpServletRequest request, HttpServletResponse response) throws Exception;

  /**
   * 确却的查找并定位数据集（在本地的数据集中找不到的情况下，到数据库中去找，找到后加到入参的数据集中）
   * @param ds 数据集
   * @param fieldName 字段名
   * @param value 查找字段的值
   * @param locatedb_sql 本地的数据集中找不到的情况下到数据库查找的SQL
   * @return 是否找到
   */
  protected boolean sureLocateData(EngineDataSet cds, ReadRow locateRow, String locatedb_sql) throws Exception
  {
    if(cds == null)
      return false;
    //如果数据集没有打开直接打开
    if(!cds.isOpen())
    {
      cds.setQueryString(locatedb_sql);
      cds.open();
      return cds.getRowCount() > 0;
    }

    if(locateRow == null)
      return false;

    //查找记录
    if(cds.locate(locateRow, Locate.FIRST))
      return true;
    else
    {
      //当前数据集找不到，到数据库找
      if(locatedb_sql == null)
        return false;
      //到数据库查找
      locateFromDB(cds, locatedb_sql);
      return cds.locate(locateRow, Locate.FIRST);
    }
  }
  /**
   * 在本地的数据集中找不到的情况下，到数据库中去找，找到后加到入参的数据集中
   * @param cds 需要添加的数据集
   * @param sql SQL语句
   */
  protected void locateFromDB(EngineDataSet cds, String sql)  throws Exception
  {
    EngineDataSet locateds = new EngineDataSet();
    locateds.setProvider(getProvider());
    locateds.setQueryString(sql);
    locateds.openDataSet();
    if(locateds.getRowCount() == 0)
      return;

    DataRow row = new DataRow(cds);
    for(int i=0; i<locateds.getRowCount(); i++)
    {
      locateds.copyTo(row);
      cds.addRow(row);
      cds.post();
    }
    locateds.closeDataSet();
    cds.resetPendingStatus(true);
    //ProvideInfo[] provider = new ProvideInfo[]{new ProvideInfo(sql)};
    //provider = dataSetProvider.getDataSetData_info(provider);
    //provider[0].getProvideData().loadDataSet(cds);
    //provider = null;
  }

  /**
   * 设置数据提供和提交对象属性
   */
  protected void setProviderResolver()
  {
    getProvider();

    getResolver();
  }

  /**
   * 设置数据提供对象属性
   */
  protected EngineDataSetProvider getProvider()
  {
    if(dataSetProvider == null)
      dataSetProvider = new EngineDataSetProvider();
    if(sessionBeanRemote!= null && dataSetProvider.getSessionBeanRemote() == null)
      dataSetProvider.setSessionBeanRemote(sessionBeanRemote);

    return dataSetProvider;
  }

  /**
   * 设置数据提交对象属性
   */
  protected EngineDataSetResolver getResolver()
  {
    if(dataSetResolver == null)
      dataSetResolver = new EngineDataSetResolver();
    if(sessionBeanRemote!= null && dataSetResolver.getSessionBeanRemote()== null)
      dataSetResolver.setSessionBeanRemote(sessionBeanRemote);

    return dataSetResolver;
  }

  /**
   * 为数据集设置与SessionBean连接的属性
   * @param cds 数据集
   * @param sql SQL语句
   */
  protected void setDataSetProperty(EngineDataSet cds, String sql)
  {
    if(cds == null)
      throw new RuntimeException("the EngineDataSet is null");
    //设置数据提供和提交对象
    setProviderResolver();


    cds.setProvider(dataSetProvider);
    cds.setResolver(dataSetResolver);
    //设置数据提供和提交方法名称
    cds.setProvideMethodName(provideMethodName);
    cds.setResolveMethodName(resolveMethodName);

    if(sql != null)
      cds.setQueryString(sql);
  }

  /**
   * 用特定的分割符分割字符窜, 返回字符串数组
   * @param s 要分割的字符串
   * @param sep 分割符
   * @return 分割后的字符串数组
   */
  public final static String[] parseString(String s, String sep)
  {
    return StringUtils.parseString(s, sep);
  }

  /**
   * 组装SQL语句
   * @param src 未组转之前的SQL语句
   * @param sep 特定字符，用于被替换成特定的值
   * @param values 用于替换字符的值
   * @return 组装后SQL语句
   */
  public static String combineSQL(String src, String sep, String[] values)
  {
    return StringUtils.combine(src, sep, values);
  }

  /**
   * 组装主键的WHERE IN 子句 即:1,2,3
   * @param ds 数据集
   * @param columnName WHERE条件字段名称
   * @return 返回装后的SQL语句
   */
  public static String getWhereIn(DataSet ds, String columnName, String blankValue)
  {
    synchronized(ds)
    {
      int ordinal = ds.getColumn(columnName).getOrdinal();
      StringBuffer buf = null;
      ds.first();
      for(int i=0; i< ds.getRowCount(); i++)
      {
        if(buf == null)
          buf = new StringBuffer(EngineDataSet.getValue(ds, ordinal));
        else
          buf.append(",").append(EngineDataSet.getValue(ds, ordinal));
        ds.next();
      }
      return buf == null ? (blankValue == null ? "" : blankValue) : buf.toString();
    }
  }
  //------------------------------------------------------------------------
  //一些公用的方法
  //------------------------------------------------------------------------
  /**
   * 查找并定位数据集<br>
   * 列如:locateDataSet(dsvDept,"nhxh",tbzsjyxmData.format("vcbm"),Locate.FIRST)
   * @param ds 数据集
   * @param FieldName 字段名
   * @param value 查找的值
   * @param option 查找的条件
   * @return 是否找到
   */
  public synchronized static boolean locateDataSet(DataSet ds, String FieldName, String value, int option)
  {
    if(FieldName.equalsIgnoreCase("") || FieldName == null)
      return false;

    DataRow rowLocate = new DataRow(ds, FieldName);
    if(ds.getColumn(FieldName).getDataType() == com.borland.dx.dataset.Variant.BIGDECIMAL)
    {
      if(value.equalsIgnoreCase("") || value == null)
        return false;
      rowLocate.setBigDecimal(FieldName, new BigDecimal(value));
    }
    else if(ds.getColumn(FieldName).getDataType() == com.borland.dx.dataset.Variant.STRING)
      rowLocate.setString(FieldName,value);

    boolean isLoacte = ds.locate(rowLocate,option);
    rowLocate = null;
    return isLoacte;
  }
  /**
   * 将BigDecimal格式成指定的格式的字符串
   * @param bg 要格式的BigDecimal
   * @param pattern 格式化的字符串，如#,##0.00
   * @return 返回格式化过的字符串
   */
  public synchronized static String formatNumber(Object obj, String pattern)
  {
    return Format.formatNumber(obj, pattern);
  }
  /**
   * 将float格式成指定的格式的字符串
   * @param f 要格式的float
   * @param pattern 格式化的字符串，如#,##0.00
   * @return 返回格式化过的字符串
   */
  public synchronized static String formatNumber(float f, String pattern)
  {
    return Format.formatNumber(f, pattern);
  }
  /**
   * 将double格式成指定的格式的字符串
   * @param d 要格式的double
   * @param pattern 格式化的字符串，如#,##0.00
   * @return 返回格式化过的字符串
   */
  public synchronized static String formatNumber(double d, String pattern)// throws Exception
  {
    return Format.formatNumber(d, pattern);
  }

  /**
   * 将数据集转化为网页的下拉框的option的内容
   * @param ds 数据集
   * @param idColumn id列名,即要得到的值
   * @param capColumn 显示的名称
   * @param selectIdValue 初始化option时，默认选中的id列的值
   * @param existColumn 过滤的列名，即只打印以该列名为条件的值，若＝null，则不起作用
   * @param existValue  过滤的列名的值
   * @return 包含option的字符串
   */
  public static String dataSetToOption(DataSet ds, String idColumn,
      String capColumn, String selectIdValue, String existColumn, String existValue)
  {
    return dataSetToOption(ds, idColumn, new String[]{capColumn}, selectIdValue, existColumn, existValue);
  }

  /**
   * 将数据集转化为网页的下拉框的option的内容
   * @param ds 数据集
   * @param idColumn id列名,即要得到的值
   * @param capColumns 显示的名称
   * @param selectIdValue 初始化option时，默认选中的id列的值
   * @param existColumn 过滤的列名，即只打印以该列名为条件的值，若＝null，则不起作用
   * @param existValue  过滤的列名的值
   * @return 包含option的字符串
   */
  public static String dataSetToOption(DataSet ds, String idColumn,
      String capColumns[], String selectIdValue, String existColumn, String existValue)
  {
    return LookupBeanFacade.dataSetToOption(ds, idColumn, capColumns, selectIdValue, existColumn, existValue);
    /*StringBuffer buf = new StringBuffer();
    synchronized(ds)
    {
      ds.first();
      int idOrdianal = ds.getColumn(idColumn).getOrdinal();
      int[] capOrdinals = new int[capColumns.length];
      for(int i=0; i<capOrdinals.length; i++)
        capOrdinals[i] = ds.getColumn(capColumns[i]).getOrdinal();

      for(int i=0; i<ds.getRowCount(); i++)
      {
        if(existColumn != null && !EngineDataSet.getValue(ds, ds.getColumn(existColumn).getOrdinal()).equals(existValue))
        {
          ds.next();
          continue;
        }
        String id = EngineDataSet.getValue(ds, idOrdianal);
        buf.append("<option value='");
        buf.append(id);
        if(id.equals(selectIdValue))
          buf.append("' selected>");
        else
          buf.append("'>");

        int j=0;
        for(; j<capOrdinals.length-1; j++)
        {
          buf.append(EngineDataSet.getValue(ds, capOrdinals[j]));
          buf.append(" ");
        }
        buf.append(EngineDataSet.getValue(ds, capOrdinals[j]));
        buf.append("</option>");
        ds.next();
      }
    }
    return buf.toString();*/
  }

  /**
   * 将数组（必须两个数组）转化为网页的下拉框的option的内容
   * @param lists 两个数组, 一个用于id, 一个用于显示
   * @param selectedIndex 选择的数组下标数
   * @return 包含option的字符串
   */
  public static String listToOption(List[] lists, int selectedIndex)
  {
    return LookupBeanFacade.listToOption(lists, selectedIndex);
    /*StringBuffer buf = new StringBuffer();
    synchronized(lists)
    {
      for(int i=0; i<lists[0].size(); i++)
      {
        buf.append("<option value='");
        buf.append(lists[0].get(i));
        if(i == selectedIndex)
          buf.append("' selected>");
        else
          buf.append("'>");

        buf.append(lists[1].get(i));
        buf.append("</option>");
      }
    }
    return buf.toString();*/
  }
  /**
   * 显示客户端的JavaScript
   * @param script JavaScript语句的内容
   * @return 返回那容<script>+ script +</script>
   */
  public static String showJavaScript(String script){
    return new StringBuffer("<script>").append(script).append("</script>").toString();
  }

  /**
   * 显示HTMl的信息给客户端
   * @param value 需要信息的值
   * @param isError 是否是错误信息
   * @return 返回HTML格式的字符串
   */
  public synchronized static String showMessage(String value, boolean isError)
  {
    StringBuffer buf = new StringBuffer("<script language='javascript'>showMessage('");
    buf.append(StringUtils.toHTML(value));
    buf.append("',").append(isError ? "true" : "false").append(")</script>");
    return buf.toString();
  }
  /**
   * 得到调整输入框长度的JavaScript语句,默认从下标0开始
   * @param fieldNames 需要调整的字段数组
   * @param formName 表单名称
   * @parem length 需要调整输入框数组长度
   * @return 返回调整输入框长度的JavaScript语句
   */
  public static String adjustInputSize(String[] fieldNames, String formName, int length)
  {
    return adjustInputSize(fieldNames, formName, length, 0);
  }
  /**
   * 得到调整输入框长度的JavaScript语句
   * @param fieldNames 需要调整的字段数组
   * @param formName 表单名称
   * @parem length 需要调整输入框数组长度
   * @parem start 从那个下标开始
   * @return 返回调整输入框长度的JavaScript语句
   */
  public static String adjustInputSize(String[] fieldNames, String formName, int length, int start)
  {
    if(fieldNames == null)
      return "";
    if(start < 0)
      start = 0;

    int maxlength = start + length;

    formName = formName == null ? "" : formName+".";
    StringBuffer buf = new StringBuffer();
    for(int i=0; i<fieldNames.length; i++)
    {
      buf.append("obj=GetInputControl('").append(fieldNames[i]);
      buf.append("',8);");
      for(int j=start; j<maxlength; j++)
      {
        buf.append("obj.AddInputSize(").append(formName).append(fieldNames[i]);
        buf.append("_").append(j).append(");");
      }
      buf.append("obj.AdjustInputSize();");
      //var cpbmObj = GetInputControl('cpbm','form1',8);
      //cpbmObj.AddInputSize('cpbm_0', 20);
      //cpbmObj.AdjustInputSize();
    }
    return buf.toString();
  }

  /**
   * 得到各个制定的网页输入框的style属性中的width长度,若names为null,返回null.
   * 入参names数组和mins数组长度必须相同，否则回抛出数组越界异常. 但入参mins可为null
   * @param values 保存值的数组
   * @param names 制定名称的数组
   * @param mins 最小长度数组
   * @return 返回象素为单位的长度的数组
   */
  public static int[] getMaxStyleWidth(RowMap[] values, String[] names, int[] mins)
  {
    if(names == null)
      return null;
    synchronized(values)
    {
      int[] widths = new int[names.length];
      RowMap valueRow = null;
      String value = null;
      for(int i=0; i<values.length; i++)
      {
        valueRow = values[i];
        for(int j=0; j<names.length; j++)
        {
          value = valueRow.get(names[j]);
          int width = getStyleWidth(value, mins == null ? 0 : mins[j]);
          if(widths[j] < width)
            widths[j] = width;
        }
      }
      return widths;
    }
  }

  /**
   * 得到网页输入框的style属性中的width长度
   * @param value 值
   * @param min 最小长度
   * @return 返回象素为单位的长度
   */
  public static int getStyleWidth(String value, int min)
  {
    if(value == null)
      return min;
    int width = 80*value.getBytes().length/13;
    return width > min ? width : min;
  }
  /**
   * 检测字符串是否是数字型的
   * @param value 需要检测的字符串
   * @param caption javascipt需要显示的标题
   * @return 若返回null表示是数字，非null为javasrcip语句
   */
  public static String checkNumber(String value, String caption)
  {
    if("".equals(value) || "NaN".equals(value))
      return showJavaScript("alert('"+caption+" 不能为空！');");
    try{
      Double.parseDouble(value);
    }catch(Exception ex){
      return showJavaScript("alert('非法 "+caption+"！');");
    }
    return null;
  }
  /**
   * 检测字符串是否是数字型的
   * @param value 需要检测的字符串
   * @param caption javascipt需要显示的标题
   * @return 若返回null表示是数字，非null为javasrcip语句
   */
  public final static String checkNumber(String value, String caption, boolean canZero)
  {
    if("".equals(value) || "NaN".equals(value))
      return showJavaScript("alert('"+caption+" 不能为空！');");
    try{
      double d= Double.parseDouble(value);
      if(!canZero && d==0)
        return showJavaScript("alert('"+caption+"不能为零');");
    }catch(Exception ex){
      return showJavaScript("alert('非法 "+caption+"！');");
    }
    return null;
  }
  /**
   * 检测字符串是否是数字型的
   * @param value 需要检测的字符串
   * @param caption javascipt需要显示的标题
   * @param min 最小值
   * @param max 最大值
   * @return 若返回null表示是数字，非null为javasrcip语句
   */
  public final static String checkNumber(String value, String caption, double min, double max)
  {
    if("".equals(value) || "NaN".equals(value))
      return showJavaScript("alert('"+caption+" 不能为空！');");
    double dValue;
    try{
      dValue = Double.parseDouble(value);
    }catch(Exception ex){
      return showJavaScript("alert('非法 "+caption+"！');");
    }
    if(dValue < min)
      return showJavaScript("alert('"+caption+" 值太小！');");
    if(dValue > max)
      return showJavaScript("alert('"+caption+" 值太大！');");

    return null;
  }

  /**
   * 检测字符串是否是整型的
   * @param value 需要检测的字符串
   * @param caption javascipt需要显示的标题
   * @return 若返回null表示是整型，非null为javasrcip语句
   */
  public final static String checkInt(String value, String caption)
  {
    if("".equals(value) || "NaN".equals(value))
      return showJavaScript("alert('"+caption+" 不能为空！');");
    try{
      Integer.parseInt(value);
    }catch(Exception ex){
      return showJavaScript("alert('非法 "+caption+"！');");
    }
    return null;
  }
  /**
   * 检测字符串是否是数字型的
   * @param value 需要检测的字符串
   * @return 返回是否是Double型的
   */
  public final static boolean isDouble(String value)
  {
    try{
      if(value.equals("NaN"))
        return false;
      Double.parseDouble(value);
    }catch(Exception ex){
      return false;
    }
    return true;
  }

  /**
   * 检测字符串是否是日期型的
   * @param value 需要检测的字符串
   * @return 返回是否是日期型的
   */
  public final static boolean isDate(String value)
  {
    try{
    	DateUtil.strToDate(value);
    }catch(Exception ex){
      return false;
    }
    return true;
  }

  /**
   * 得到网页的状态的style，class的名称
   * @param type 类型
   * @return 返回style，class的名称
   */
  public final static String getStyleName(String type)
  {
    if(type.equals("0"))//未审批
      return "class=td";
    else if(type.equals("1"))//已审批
      return "class=tdApproved";
    else if(type.equals("2"))//已生成物料, 提单的已出库, 进货单的入库确认
      return "class=tdMrp";
    else if(type.equals("3"))//已下达任务
      return "class=tdTasked";
    else if(type.equals("4"))//作废
      return "class=tdBlankout";
    else if(type.equals("8"))//完成, 出库确认
      return "class=tdComplete";
    else if(type.equals("9"))//审批中
      return "class=tdApproving";
    else if(type.equals("-1"))//不存在的状态值,表示提单已经被引用(出库)
      return "class=tdOutStore";
    else
      return "class=td";
  }

  /**
   * 解析规格属性值表达式。规格属性格式:key1(value1) key1(value2)
   * @param sxz 数据集中的属性值
   * @return 返回解析后的Map
   */
  public final static Map parseSpecProperty(String sxz)
  {
    if(sxz == null)
      return null;
    //分解属性值
    Map mapSxz = new Hashtable();
    String[] sxzs = StringUtils.parseString(sxz, "()");
    String key = null,  value = null;
    for(int i=0; i<sxzs.length; i++)
    {
      if(i%2 == 0)
        key = sxzs[i].trim();
      else
        mapSxz.put(key, sxzs[i].trim());
    }
    return mapSxz;
  }

  /**
   * 字符串表达式四则运算.可用字符0123456789.+-%/^()
   * @param expression 字符串表达式
   * @return 返回四则运算结果
   * @throws ExpressionException 表达式运算异常
   */
  public static final BigDecimal calculateExpression(String expression,
      String specProperty) throws ExpressionException
  {
    Map map = specProperty==null || specProperty.length()==0? null : parseSpecProperty(specProperty);
    expression = MessageFormat.format(expression, map);
    return SimpleCalculator.arithmetic(expression);
  }

  /**
   * 检测四则运算字符串表达式的正确性
   * @param expression 字符串表达式
   * @throws ExpressionException 表达式运算异常
   */
  public static final void verifyExpression(String expression) throws ExpressionException
  {
    Hashtable table = new Hashtable();
    MessageFormat msgformat = new MessageFormat(expression);
    String[] keys = msgformat.getArgumentNames();
    for(int i=0; i<keys.length; i++){
      table.put(keys[i], "1");
    }
    expression = msgformat.format(table);
    SimpleCalculator.verify(expression);
  }
}

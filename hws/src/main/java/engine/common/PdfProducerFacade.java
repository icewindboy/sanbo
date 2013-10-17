package engine.common;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Iterator;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import engine.util.ParseProperties;
import engine.util.StringUtils;
import engine.action.*;
import engine.dataset.*;
import engine.web.observer.*;
import engine.report.util.ContextData;
import engine.report.util.ReportData;
import engine.report.util.TempletData;
import engine.report.util.ReportTempletParser;
import engine.report.util.Tag;
import engine.report.util.ReportListener;
import engine.report.event.*;
import engine.report.pdf.PdfProducer;
import engine.report.html.HtmlProducer;
import engine.report.html.HtmlHelper;
import engine.dataset.sql.QueryWhere;
import engine.dataset.sql.DefaultQueryWhere;
import engine.dataset.sql.QueryWhereField;
import engine.dataset.sql.DefaultQueryWhereField;
import engine.util.Format;

import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.SortDescriptor;
/**
 * Title:        print
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ENGINE
 * @author 江海岛
 * @version 1.0
 */
/**
 * 2004.4.17 高级查询是对显示的字段再剔除。起到的作用相当于过滤的功能。所以应该加在数据集的外面
 */
public final class PdfProducerFacade extends BaseAction implements Operate, Serializable
{
  public static final String SHOW_DETAIL = "showdetail";
  public static final String ADVANCE_TABLE = "T";

  public  String retuUrl = null;

  private TempletData templet = null;              //保存模板对象
  public  ContextData context = null;              //报表上下文
  private ReportData  reportData = null;           //保存报表数据

  private DefaultHtmlHelper htmlHelper = new DefaultHtmlHelper();//生成HTML报表的助手
  private HtmlProducer htmlProducer = new HtmlProducer();//生成HTML报表的助手
  private PdfProducer pdfProducer = null;          //PdfProducer生成器


  public  QueryWhere whereQueryItem = new DefaultQueryWhere(); //模板初始化的查询条件
  private QueryWhere advanceQueryTemp = new DefaultQueryWhere(); //模板初始化的高级查询条件
  private QueryWhere advanceQuery = new DefaultQueryWhere(); //模板初始化的高级查询条件

  private EngineDataSet dsRep = null;        //
  private Map datasetInfo = null;            //数据集对应的信息
  //private Object lock = new Object();

  //private Map loginInitsCons = null;
  public  String reportDirPath = "";

  public  String[] canGroupColumn = null;       //保存用户选择后能够用于小计的字段数组

  /**
   * 报表打印的实例
   * @param request jsp请求
   * @return 返回销售合同列表的实例
   */
  public static PdfProducerFacade getInstance(HttpServletRequest request)
  {
    PdfProducerFacade pdfProducerBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "pdfProducerBean";
      pdfProducerBean = (PdfProducerFacade)session.getAttribute(beanName);
      if(pdfProducerBean == null)
      {
        pdfProducerBean = new PdfProducerFacade();
        session.setAttribute(beanName, pdfProducerBean);
      }
    }
    return pdfProducerBean;
  }

  /**
   * 报表打印用于显示明细的实例
   * @param request jsp请求
   * @param code 页面编码
   * @return 返回销售合同列表的实例
   */
  public static PdfProducerFacade getInstance(HttpServletRequest request, String code)
  {
    PdfProducerFacade pdfProducerBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "pdfProducerBeanX" + (code!=null ? code : "");
      pdfProducerBean = (PdfProducerFacade)session.getAttribute(beanName);
      if(pdfProducerBean == null)
      {
        pdfProducerBean = new PdfProducerFacade();
        session.setAttribute(beanName, pdfProducerBean);
      }
    }
    return pdfProducerBean;
  }

  /**
   * 构造函数
   */
  private PdfProducerFacade()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }

  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private final void jbInit() throws java.lang.Exception
  {
    //添加触发对象
    Init init = new Init();
    Search mastSearch = new Search(false);
    Search detailSearch = new Search(true);
    ResetReport resetReport = new ResetReport();
    ShowPdf showPdf = new ShowPdf();
    addObactioner(String.valueOf(INIT), init);
    addObactioner(String.valueOf(FIXED_SEARCH), mastSearch);
    addObactioner(String.valueOf(SHOW_PDF), showPdf);
    //显示明细:1.初始化, 2.重置中间变量的属性, 3.查询数据
    addObactioner(SHOW_DETAIL, init);
    addObactioner(SHOW_DETAIL, resetReport);
    addObactioner(SHOW_DETAIL, detailSearch);
    //打印单据:1.初始化, 2.重置中间变量的属性, 3.查询数据, 4.显示pdf文件
    addObactioner(String.valueOf(PRINT_BILL), init);
    addObactioner(String.valueOf(PRINT_BILL), resetReport);
    addObactioner(String.valueOf(PRINT_BILL), mastSearch);
    addObactioner(String.valueOf(PRINT_BILL), showPdf);
    //套打单据:1.初始化, 2.重置中间变量的属性, 4.显示pdf文件
    addObactioner(String.valueOf(PRINT_PRECISION), init);
    addObactioner(String.valueOf(PRINT_PRECISION), resetReport);
    addObactioner(String.valueOf(PRINT_PRECISION), showPdf);
  }

  //----Implementation of the BaseAction abstract class
  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String opearate = request.getParameter(OPERATE_KEY);
      if(opearate != null && opearate.trim().length() > 0)
      {
        RunData data = notifyObactioners(opearate, request, response, null);
        if(data == null)
          return showMessage("无效操作", false);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    deleteObservers();    //释放所有触发对象
    if(pdfProducer != null)
      pdfProducer.clear();
    if(htmlProducer != null)
      htmlProducer.clear();

    log = null;
  }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }

  /**
   * 是否有高级查询的字段
   * @return 返回是否有高级查询的字段
   */
  public boolean hasAdvanceField()
  {
    return this.advanceQueryTemp.getFieldCount() > 0;
  }

  /**
   * 得到高级查询的字段数组
   * @return 返回高级查询的字段数组
   */
  public QueryWhereField[] getWhereFields()
  {
    return this.advanceQueryTemp.getWhereFields();
  }

  /**
   * 得到高级查询的字段名称数组
   * @return 返回高级查询的字段名称数组
   */
  public String[] getWhereFieldNames()
  {
    return this.advanceQueryTemp.getFieldFullNames();
  }

  /**
   * 得到查询的表单名称
   * @param defaluValue 默认值
   * @return 查询的表单名称
   */
  public String getPageForm(String defaluValue)
  {
    if(templet == null || templet.getPage() == null)
      return defaluValue;
    String formName = (String)templet.getPage().get(Tag.QUERY_FORM);
    return formName == null || formName.length() ==0 ? defaluValue : formName;
  }

  /**
   * 得到查询的TABLE名称
   * @param defaluValue 默认值
   * @return 返回查询的TABLE名称
   */
  public String getPageTable(String defaluValue)
  {
    if(templet == null || templet.getPage() == null)
      return defaluValue;
    String tableName = (String)templet.getPage().get(Tag.QUERY_TABLE);
    return tableName == null || tableName.length() == 0 ? defaluValue : tableName;
  }

  /**
   * 打印报表
   * @param rowmin 最小行
   * @param rowmax 最大行
   * @param pagesize 一页可打印的行数
   * @return 返回报表的html代码
   */
  public void printReport(PageContext pageContext, int rowmin, int rowmax, int pagesize)
  {
    if(templet == null)
      return;
    htmlHelper.setMinRow(rowmin);
    htmlHelper.setMaxRow(rowmax);
    htmlHelper.setPrintRow(pagesize);
    if(htmlHelper.isNeedReprint())
    {
      htmlHelper.setWriter(pageContext.getOut());
      htmlHelper.needReprint();
      htmlProducer.createHTMLs(templet, context, htmlHelper);
      htmlHelper.setIsPrintBlank(false);
      htmlHelper.flush();
    }
  }

  public void printExcelReport(PageContext pageContext)
  {
    if(templet == null)
      return;
    int max = dsRep==null||!dsRep.isOpen() ? 0 : dsRep.getRowCount();
    htmlHelper.setWriter(pageContext.getOut());
    htmlHelper.setIsExcel(true);
    htmlHelper.setMinRow(0);
    htmlHelper.setMaxRow(max);
    htmlHelper.setPrintRow(max);
    htmlHelper.needReprint();
    htmlProducer.createHTMLs(templet, context, htmlHelper);
    htmlHelper.setIsPrintBlank(false);
    htmlHelper.setIsExcel(false);
    htmlHelper.flush();
  }
  /**
   * 得到数据集的总记录
   * @return 返回数据集的总记录
   */
  public String getRowCount()
  {
    return dsRep == null ? "0" : dsRep.isOpen() ? String.valueOf(dsRep.getRowCount()) : "0";
  }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {

    public synchronized void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //初始化报表
      initReport(data);
      //生成空白报表
      htmlProducer.clear();
      htmlHelper.clear();
      htmlHelper.setIsPrintBlank(true);
      data.setMessage(showJavaScript("showFixedQuery();"));
    }

    /**
     * 初始化报表
     * @param data 运行数据
     * @throws Exception 异常
     */
    private void initReport(RunData data) throws Exception
    {
      //得到模板名称和报表数据key
      String templetName = data.getParameter("code","");
      String contextkey = data.getParameter("context");
      //String datakey = data.getParameter("data");
      //
      HttpSession session = data.getRequest().getSession();
      String templetPath = templetName.toLowerCase();
      //创建上下文对象
      if(contextkey != null)
      {
        context = (ContextData)session.getAttribute(contextkey);
        session.removeAttribute(contextkey);
      }
      else //没有传递上下文数据
      {
        if(context == null)
          context = new ContextData();
        else
          context.clear();
      }

      context.setDataSetProvider(getProvider());
      context.setQueryWhere(whereQueryItem);
      context.setAdvanceWhere(advanceQuery);
      //context.put("where", whereQueryItem);
      //判断报表模板的信息
      if(templetPath.endsWith(".buf"))//动态报表
      {
        StringBuffer bufTemplet = (StringBuffer)session.getAttribute(templetName);
        session.removeAttribute(templetName);
        templet = ReportTempletParser.createTemplet(bufTemplet);
      }
      else if(templetPath.startsWith("http://") || templetPath.startsWith("ftp://"))//url
        templet = ReportTempletParser.createTemplet(new URL(templetName));
      else//从classpath读取模板
        templet = getTemplet(reportDirPath+File.separator+templetPath, null, context, data.getRequest());
      //--监听器--
      //模板转载后的监听器
      boolean needInit = true;
      TempletAfterProvideListener afterloadListener = context.getReportListeners().getTempletAfterProvideListener();
      if(afterloadListener != null)
      {
        TempletProvideResponse response = new TempletProvideResponse();
        afterloadListener.templetAfterProvide(data.getRequest(), templet, context, response);
        needInit = response.isNeedInit();
      }

      //其他页面调用网页就直接打印pdf
      if(contextkey != null)
        printPdf(data.getResponse());
      //没有传递上下文数据
      else if(needInit)
      {
        //是否有传递报表数据的数据
        //2004。6。11用监听器代替传递报表数据的数据
        /*if(datakey != null)
        {
          ReportData[] repdatas = (ReportData[])session.getAttribute(datakey);
          if(repdatas == null)
            return;
          //2004。5。7 暂时不去除session中的数据了
          //session.removeAttribute(datakey);
          context.addReportDatas(repdatas);
        }*/
        if(reportData == null)
          reportData = new ReportData();
        else
          reportData.clear();

        //在上下文对象中添加一张报表数据
        context.addReportData(reportData);
        initReportData(data);
      }
      //得到字体的信息
      //getFonts(fontsPropertyPath);
    }

    /**
     * 初始化报表数据属性
     * @param data WEB请求和响应数据
     * @throws Exception
     */
    private void initReportData(RunData data) throws Exception
    {
      //页面显示字段等信息
      templet.setContextPageData(context);
      //保存可小计的字段数组
      canGroupColumn = context.getGroupColumns();

      //处理数据源
      /**
       * @todo 以后需要处理多个数据源的问题
       */
      Map[] datasets = templet.getDataSets();
      String datasetname = null;
      for(int i=0; datasets!=null && i<datasets.length; i++)
      {
        datasetInfo = datasets[i];
        datasetname = (String)datasets[i].get(Tag.NAME);
        if(dsRep!= null)
        {
          dsRep.closeDataSet();
          dsRep = null;
        }
        dsRep = new EngineDataSet();
        setDataSetProperty(dsRep, null);
        reportData.addReportData(datasetname, dsRep);
      }
      boolean isDataSet = datasetname != null && datasetname.length() > 0;
      //设置初始化的参数值
      whereQueryItem.clearColumns();
      advanceQueryTemp.clearColumns();
      advanceQueryTemp.emptyWhereValues();
      //初始化高级查询条件
      String sqlType = (String)datasetInfo.get(Tag.TYPE);
      if(Tag.SQL.equalsIgnoreCase(sqlType))
      {
        Map[] fields = templet.getFields();
        for(int i=0; fields!=null && i<fields.length; i++)
        {
          //String dataset = (String)fields[i].get(Tag.DATASET);
          //if(dataset != null && !dataset.equals(datasetname))
            //continue;
          //剔除不显示在高级查询中的字段
          String general = (String)fields[i].get(Tag.GENERAL);
          if(StringUtils.isTrue(general))
            continue;
          String name = (String)fields[i].get(Tag.NAME);
          QueryWhereField qField = new DefaultQueryWhereField(fields[i]);
          qField.setColumnName(name);
          advanceQueryTemp.addWhereField(ADVANCE_TABLE, qField);
        }
      }

      //初始化查询条件
      Map[] wheres = templet.getWheres();
      for(int i=0; wheres !=null && i<wheres.length; i++)
      {
        String dataset = (String)wheres[i].get(Tag.DATASET);
        if(dataset != null && !dataset.equals(datasetname))
          continue;
        /*String field = (String)wheres[i].get(Tag.FIELD);
        String caption = (String)wheres[i].get(Tag.CAPTION);
        String datatype = (String)wheres[i].get(Tag.DATA_TYPE);
        String linktable = (String)wheres[i].get(Tag.LINK_TABLE);
        String linkcolumn = (String)wheres[i].get(Tag.LINK_COLUMN);
        String querycolumn = (String)wheres[i].get(Tag.QUERY_COLUMN);
        String extendname = (String)wheres[i].get(Tag.EXTEND_NAME);
        String opersign = (String)wheres[i].get(Tag.OPER_SIGN);
        String type = (String)wheres[i].get(Tag.TYPE);
        String value = (String)wheres[i].get(Tag.VALUE);
        String lookup = (String)wheres[i].get(Tag.LOOKUP);
        String script = (String)wheres[i].get(Tag.SCRIPT);
        String span = (String)wheres[i].get(Tag.SPAN);
        //
        String initValue = (String)wheres[i].get(Tag.INIT_VALUE);
        String need = (String)wheres[i].get(Tag.NEED);
        //
        QueryWhereField qField = new DefaultQueryWhereField(field, caption, datatype, linktable,
            linkcolumn, querycolumn, extendname, opersign, type, StringUtils.isTrue(span), value,
            lookup, script, initValue, true, StringUtils.isTrue(need));
        */
        QueryWhereField qField = new DefaultQueryWhereField(wheres[i]);
        whereQueryItem.addWhereField(datasetname, qField);
      }
      //初始化的参数值
      Map[] params = templet.getParams();
      for(int i=0; params!=null && i<params.length; i++)
      {
        String dataSet = (String)params[i].get(Tag.DATASET);
        if(dataSet != null && !dataSet.equals(datasetname))
          continue;

        String name = (String)params[i].get(Tag.NAME);
        String dataType = (String)params[i].get(Tag.DATA_TYPE);
        String linktable = (String)params[i].get(Tag.LINK_TABLE);
        String linkcolumn = (String)params[i].get(Tag.LINK_COLUMN);
        String querycolumn = (String)params[i].get(Tag.QUERY_COLUMN);
        String opersign = (String)params[i].get(Tag.OPER_SIGN);
        if(opersign == null || opersign.length() == 0)
          opersign = "=";
        //
        String value = (String)params[i].get(Tag.VALUE);
        //if(value != null)
          //whereQueryItem.putWhereValue(isDataSet ? datasetname+"$"+name : name, value);
        QueryWhereField qField = new DefaultQueryWhereField(name, null, dataType, linktable,
            linkcolumn, querycolumn, null, opersign, false);
        //设置初始化值
        if(value != null)
          qField.setInitValue(value);
        whereQueryItem.addWhereField(datasetname, qField);
      }
      //清楚所有的输入值，并初始化输入值
      Map inits = procInitConstant(data.getRequest());
      context.put("constant", inits);
      whereQueryItem.setInitValues(inits);
      //清楚所有的输入值，并初始化输入值
      whereQueryItem.clearWhereValues();
    }

    /**
     * 处理初始化的常量值
     * @param req WEB请求和响应数据
     * @return 返回初始化的常量值的Map
     */
    private Map procInitConstant(HttpServletRequest req)
    {
      Map inits = engine.html.HtmlTableProducer.initConstant(req);
      //--监听器--
      TempletLoadListener loadListener = context.getReportListeners().getTempletLoadListener();
      //处理自己提供的常量
      if(loadListener != null)
      {
        Map map = loadListener.getInitConstant(req);
        if(map != null)
        {
          if(inits != null)
            inits.putAll(map);
          else
            inits = map;
        }
      }
      return inits;
    }

  }

  /**
   * 查询操作的触发类
   */
  class Search implements Obactioner
  {
    //是否是数据穿透的报表页面
    private boolean isDetail = false;

    public Search(boolean isDetail)
    {
      this.isDetail = isDetail;
    }

    public synchronized void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //初始化报表
      String temp = queryDataSet(data);
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      //复位,清除所有中间变量
      htmlProducer.clear();
      htmlHelper.clear();
    }

    private void procAdvanceQuery(RunData data)
    {
      if(advanceQueryTemp.getFieldCount() < 1)
        return;
      //最大20条
      //2004.4.17 高级查询是对显示的字段再剔除。起到的作用相当于过滤的功能。所以应该加在数据集的外面
      //.并将最外层的表别名定死
      //String datasetname = (String)datasetInfo.get(Tag.NAME);
      advanceQuery.clearColumns();
      advanceQuery.emptyWhereValues();
      for(int i=1; i<=20; i++)
      {
        String item = data.getParameter("item_"+i);
        String value = data.getParameter("value_"+i);//
        String opsign = data.getParameter("opsign_"+i);//
        if(item==null || value==null || item.length() == 0 || value.length()==0 ||
           opsign==null || opsign.length()==0)
          continue;
        String lnsign = data.getParameter("lnsign_"+i);
        QueryWhereField fieldObj = advanceQueryTemp.getWhereField(item);
        if(fieldObj == null)
          continue;
        QueryWhereField field = (QueryWhereField)fieldObj.clone();
        field.setOperSign(opsign);
        field.setLinkSign(lnsign);
        field.setExtendName(String.valueOf(i));
        advanceQuery.addWhereField(ADVANCE_TABLE, field);
        advanceQuery.putWhereValue(item+"$"+i, value);
      }
    }

    /**
     * 查询数据
     */
    private String queryDataSet(RunData data)
    {
      //设置查询条件值
      //whereQueryItem.emptyWhereValues(); //一些隐藏的信息将也会被清楚。比如fgsid
      whereQueryItem.setWhereValues(data.getRequest());
      String[] columns = null;
      String[] sortColumns = null;
      String groupfield = null;
      //不是数据穿透的报表
      if(isDetail)
      {
        columns = context.getShowColumns();
      }
      else
      {
        //清除原来选择的字段
        context.clearTemps();
        String tempColumn = null;
        //选中的显示字段数组
        columns = data.getParameterValues("showColumn");
        tempColumn = data.getParameter("allColumnStr", "");
        String[] allColumns = parseString(tempColumn, ",");
        context.setColumns(allColumns);
        //sorts
        tempColumn = data.getParameter("sortColumnStr", "");
        sortColumns = parseString(tempColumn, ",");
        context.setSortColumns(sortColumns);
        //小计字段
        boolean isshowgroup = data.getParameter("isshowgroup","").equals("1");
        whereQueryItem.putWhereValue("isshowgroup", isshowgroup ? "1" : "0");
        groupfield = isshowgroup? data.getParameter("groupColumn") : null;
        context.setGroupColumn(groupfield);
        //canGroupColumn
        tempColumn = data.getParameter("groupColumnStr", "");
        canGroupColumn = parseString(tempColumn, ",");
        //添加选择的字段
        context.addShowColumns(columns);
      }
      //校验需要强制输入的值
      String[] needNames = whereQueryItem.getNeedFieldFullNames();
      QueryWhereField[] needFields = null;
      for(int i=0; needNames!=null && i<needNames.length; i++)
      {
        String[] value = whereQueryItem.getWhereValues(needNames[i]);
        if(value!=null && (value.length > 1 || (value.length == 1 && value[0].length()>0)))
          continue;
        if(needFields == null)
          needFields = whereQueryItem.getNeedWhereFields();
        return showJavaScript("alert('"+ needFields[i].getCaption()+"不能为空！');");
      }
      //
      StringBuffer groupColumn = null;
      StringBuffer sumColumn = null;
      for(int i=0; columns!=null && i<columns.length; i++)
      {
        if(columns[i] == null || columns[i].length() == 0)
          continue;
        if(context.isSumColumn(columns[i]))
        {
          sumColumn = sumColumn == null ? new StringBuffer() : sumColumn.append(",");
          sumColumn.append("SUM("+columns[i]+") "+ columns[i]);
          //
          context.addShowSumColumn(columns[i]);//
        }
        else
        {
          groupColumn = groupColumn == null ? new StringBuffer() : groupColumn.append(",");
          groupColumn.append(columns[i]);
          //附属字段
          String include = (String)templet.getField(columns[i]).get(Tag.INCLUDE);
          if(include!=null && include.length() > 0)
          {
            groupColumn.append(",");
            groupColumn.append(include);
          }
        }
      }
      //
      procAdvanceQuery(data);
      //--监听器--
      //得到报表数据装载的监听器
      ReportDataLoadingListener repLoading = context.getReportListeners().getReportDataLoadingListener();
      if(repLoading == null)
      {
        String sql = null;
        String provide = null;
        ReportProvideSqlListener provideSQL = context.getReportListeners().getReportProvideSqlListener();
        String tableName = (String)datasetInfo.get(Tag.OBJECT);

        String sqlType = (String)datasetInfo.get(Tag.TYPE);
        if(Tag.PROCEDURE.equalsIgnoreCase(sqlType))
        {
          tableName = "CALL " + (provideSQL == null ? tableName :
                      provideSQL.getQuery(data.getRequest(), templet, context));
          String[] values = whereQueryItem.getFieldFullNames();
          for(int i=0; i<values.length; i++)
          {
            values[i] = whereQueryItem.getWhereValue(values[i]);
          }

          sql = combineSQL(tableName, "@", values);
        }
        //有提供sql的监听器
        else if(provideSQL != null){
          StringBuffer buf = new StringBuffer("SELECT ");
          if(groupColumn == null && sumColumn==null)
              buf.append("*");
          else
          {
            if(groupColumn != null)
              buf.append(groupColumn);
            if(sumColumn != null)
              buf.append(groupColumn != null ? "," : "").append(sumColumn);
          }
          buf.append(" FROM (");
          buf.append(provideSQL.getQuery(data.getRequest(), templet, context));
          buf.append(")");
          //GROUP BY
          if(sumColumn != null && groupColumn != null)
            buf.append(" GROUP BY ").append(groupColumn);
          sql = buf.toString();
        }
        else
        {
          boolean hasWhere = false;
          String whereStr = whereQueryItem.getWhereQuery();
          String advanceStr = advanceQuery.getWhereQuery();
          //2004.4.17 高级查询是对显示的字段再剔除。起到的作用相当于过滤的功能。所以应该加在数据集的外面
          /*
          if(whereStr.length() > 0 && advanceStr.length() > 0)
            whereStr = new StringBuffer("(").append(advanceStr).append(") AND ").append(whereStr).toString();
          else if(advanceStr.length() > 0)
            whereStr = advanceStr;
          */
          //组装select from 若有高级查询，加个嵌套
          StringBuffer buf = new StringBuffer(advanceStr.length() > 0 ? "SELECT * FROM ( SELECT " : "SELECT ");
          if(groupColumn == null && sumColumn==null)
              buf.append("*");
          else
          {
            if(groupColumn != null)
              buf.append(groupColumn);
            if(sumColumn != null)
              buf.append(groupColumn != null ? "," : "").append(sumColumn);
          }
          //FROM
          buf.append(" FROM ");
          String tableAlias = (String)datasetInfo.get(Tag.NAME);
          if(tableName.indexOf("select") > -1)
            buf.append("(").append(tableName).append(") ").append(tableAlias);
          else
            buf.append(tableName).append(" ").append(tableAlias);
          //WHERE
          if(whereStr.length() > 0)
            buf.append(" WHERE ").append(whereStr);
          //GROUP BY
          if(sumColumn != null && groupColumn != null)
            buf.append(" GROUP BY ").append(groupColumn);
          //若有高级查询，加个嵌套
          if(advanceStr.length() > 0)
            buf.append(") ").append(ADVANCE_TABLE).append(" WHERE ").append(advanceStr);

          sql = buf.toString();
        }
        dsRep.setQueryString(sql);
        if(dsRep.isOpen())
        {
          dsRep.setSort(null);
          dsRep.refresh();
        }
        else
          dsRep.openDataSet();
      }
      else
        repLoading.dataLoading(data.getRequest(), templet, context, dsRep, whereQueryItem);
      //小计排序
      sortColumns = context.getSortColumns();
      int count = (groupfield != null && groupfield.length()>0 ? 1 : 0) + (sortColumns!=null ? sortColumns.length : 0);
      if(count > 0)
      {
        String[] allGroups = new String[count];//groupfield != null || sortColumns != null
        if(groupfield != null)
        {
          allGroups[0] = groupfield;
          if(sortColumns!=null && sortColumns.length > 0){
            for(int i=1; i<count; i++)
              allGroups[i] = sortColumns[i-1];
          }
        }
        else
          allGroups = sortColumns;

        boolean[] decss = new boolean[count];
        //for(int i=0; i<decss.length; i++)
          //decss[i] = false;
        dsRep.setSort(new SortDescriptor("", allGroups, decss, null, 0));
      }
      //--监听器--
      //数据装载后监听器
      ReportDataLoadedListener repLoaded = context.getReportListeners().getReportDataLoadedListener();
      if(repLoaded != null)
        repLoaded.dataLoaded(data.getRequest(), templet, context, dsRep);

      return null;
      //return showJavaScript("showReport();");
    }
  }

  /**
   * 显示明细报表
   */
  class ResetReport implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = null;
      data.setMessage(null);
      htmlHelper.setIsPrintBlank(false);
    }
  }

  /**
   * 显示PDF的触发类
   */
  class ShowPdf implements Obactioner
  {
    public synchronized void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //初始化报表
      printPdf(data.getResponse());
    }
  }

  /**
   * 套打的触发类
   *
  class PrintPrecision implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //初始化报表
      synchronized(lock)
      {
        printPdf(data.getResponse());
      }
    }
  }*/

  /**
   * 生成报表
   * @param response web响应
   */
  private void printPdf(HttpServletResponse response) throws Exception
  {
    //生成pdf数据
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ServletOutputStream servletOut = response.getOutputStream();
    try{
      if(pdfProducer == null)
        pdfProducer = new PdfProducer();
      pdfProducer.createPdfs(templet, context, baos);
      response.setContentLength(baos.size());
      baos.writeTo(servletOut);
      servletOut.flush();
    }
    finally{
      if(pdfProducer != null)
        pdfProducer.clear();
      try{
        if(servletOut != null)
          servletOut.close();
      }
      catch(IOException ex){
        log.warn("close object of servletOut exception:", ex);
      }
    }
  }

  //
  private Hashtable htFonts = null;    //保存字体信息，作为参数后将保存为实际的字体对象
  private long fileLastModi;           //保存字体文件最后更改的时间
  /**
   * 字体描述信息
   * @param fontsPropertyPath 字体描述文件的路径
   */
  private void getFonts(String fontsPropertyPath)
  {
    File fontsPropertis = null;
    try{
      //判断文件的最后更改时间是否跟保存的一致。否:重新读取文件
      fontsPropertis = new File(fontsPropertyPath);
      if(!fontsPropertis.exists())
        return;
      if(fontsPropertis.lastModified() == fileLastModi)
        return;
      htFonts = ParseProperties.parse(new FileInputStream(fontsPropertis));
      fileLastModi = fontsPropertis.lastModified();
    }
    catch(Exception e){}

    if(htFonts == null)
      return;

    Iterator it = htFonts.keySet().iterator();
    while(it.hasNext())
    {
      Object obj = it.next();
      //保存字体的属性的数组，分别保存字体文件路径，最后更改时间和创建的BaseFont类
      Object[] fontAttribute = (Object[])htFonts.get(obj);
      String fileName = (String)htFonts.get(obj);
      if(fileName != null)
      {
        File fontFile = new File(fontsPropertis.getParentFile(), fileName);
        if(fontAttribute == null)
        {
          fontAttribute = new Object[3];
          fontAttribute[0] = fontFile.getAbsolutePath();
          fontAttribute[1] = new Long(fontFile.lastModified());
        }
        else
          fontAttribute[0] = fontFile.getAbsolutePath();

        htFonts.put(obj, fontAttribute);
      }
    }
    this.htFonts = htFonts;
  }

  /**
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getWhereFieldValue(String col)
  {
    return whereQueryItem.getWhereValue(col);
  }

  /**
   * 得到报表标题
   * @return 返回报表标题
   */
  public final String getTitle()
  {
    return templet == null ? "" : templet.getTitle();
  }

  /**
   * 得到模板对象
   * @param templetPath 模本文件名称
   * @param inits 初始化的常量
   * @param context 模板上下文
   * @param req WEB请求
   * @return 返回模板对象
   * @throws IOException 读取文件异常
   */
  private static final TempletData getTemplet(String templetPath, Map inits,
      ContextData context, HttpServletRequest req) throws IOException
  {
    //"/WEB-INF/report/"+
    String propsName   = templetPath +".properties";
    String templetName = templetPath +".htm";
    //InputStream in = PdfProducerFacade.class.getResourceAsStream(propsName);
    FileInputStream in = null;
    TempletProvideListener provideListener = null;
    TempletData templet = null;
    synchronized(context)
    {
      //处理属性文件
      try{
        in = new FileInputStream(propsName);
        Properties props = new Properties();
        props.load(in);
        ReportListener listeners= context.getReportListeners();
        listeners.processListener(props);
        provideListener = listeners.getTempletProvideListener();
      }
      catch(FileNotFoundException ex){}
      //--监听器--
      //处理模板
      if(provideListener == null)
      {
        //in = PdfProducerFacade.class.getResourceAsStream(templetName);
        in = new FileInputStream(templetName);
        //if(in == null)
          //throw new NullPointerException("the file of "+ templetPath +" is not exist ！");
        templet = ReportTempletParser.createTemplet(templet, in, null);
        try{
          if(in != null)
            in.close();
        }catch(IOException ioe){}
      }
      else
        templet = provideListener.getTemplet(req, templet, context);
    }
    return templet;
  }
}

package engine.common;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

import engine.util.log.Log;
import engine.report.util.*;
import engine.report.pdf.PdfProducer;
import engine.web.observer.RunData;
/**
 * <p>Title: 打印单据的Servlet</p>
 * <p>Description: 打印单据的Servlet</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */

public final class BillPrintServlet extends BaseHttpServlet
{
  private static final String CONTENT_TYPE = "application/pdf";

  protected Log log = new Log(getClass());//日志对像

  private String reportDirPath = null;
  //private TempletData templet = null;              //保存模板对象
  public  ContextData context = new ContextData(); //报表上下文

  private PdfProducer pdfProducer = null;          //PdfProducer生成器

  private Object lock = new Object();

  //Initialize global variables
  public void init() throws ServletException
  {
    reportDirPath = getServletContext().getRealPath("/WEB-INF/report");
  }

  //Process the HTTP Get request
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    doService(request, response);
  }

  //Process the HTTP Post request
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    doService(request, response);
  }

  //Clean up resources
  public void destroy()
  {
    if(pdfProducer != null)
      pdfProducer.clear();
    if(context != null)
      context.clear();
    log = null;
  }

  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private void doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      synchronized(lock)
      {
        response.setContentType(CONTENT_TYPE);
        browserNoCache(response);
        //engine.common.PdfProducerFacade pdf = engine.common.PdfProducerFacade.getInstance(request);
        //pdf.doService(request, response);
        printBill(request, response);
      }
    }
    catch(Exception ex){
      log.error("doService", ex);
    }
  }

  /**
   * 打印单据
   * @param request WEB请求
   * @param response WEB响应
   * @throws Exception IO异常
   */
  private void printBill(HttpServletRequest request, HttpServletResponse response)
      throws Exception
  {
    //创建上下文对象
    context.clear();
    //得到模板名称和报表数据key
    String templetName = RunData.getParameter(request, "code","");
    String datakey = RunData.getParameter(request, "data", "");
    //
    HttpSession session = request.getSession();
    String templetPath = templetName.toLowerCase();
    //判断报表模板的信息
    TempletData templet = null;
    if(templetPath.endsWith(".buf"))//动态报表
    {
      StringBuffer bufTemplet = (StringBuffer)session.getAttribute(templetName);
      session.removeAttribute(templetName);
      templet = ReportTempletParser.createTemplet(bufTemplet);
    }
    //从classpath读取模板
    else
      templet = ReportTempletParser.createTemplet(reportDirPath+File.separator+templetPath+".bill", null);

    //传递报表数据的数据
    ReportData[] repdatas = (ReportData[])session.getAttribute(datakey);
    session.removeAttribute(datakey);
    context.addReportDatas(repdatas);
    //生成报表
    printPdf(response, templet, context);
  }

  /**
   * 生成报表
   * @param response web响应
   */
  private void printPdf(HttpServletResponse response,
                        TempletData templet, ContextData context) throws Exception
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
}
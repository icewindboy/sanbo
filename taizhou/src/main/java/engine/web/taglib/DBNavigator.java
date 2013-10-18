package engine.web.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

import engine.util.StringUtils;
import engine.dataset.EngineDataSet;

/**
 * Title:        显示网页页数
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      JAC
 * @author hukn
 * @version 1.0
 */

public class DBNavigator extends TagSupport
{
  private EngineDataSet dsPage = null;
  private int pageSize =25;
  private int recordCount = 0;
  private boolean isPrint= true;
  private String goProperty = "style='border-style:solid; border-width:1px; border-color:#3399ff; background-color:lightcyan; color:red; font-size:8pt; width: auto'";
  private String attributeKey = null;
  private String form = null;
  private String operate = null;
  private boolean disable = false;

  private PaginationInfo paginationInfo = null;
  /**
   *设置输出每一页的大小
   *@param pageSize 输出每一页的大小
   */
  public void setPageSize(String pageSize)
  {
    try{
      this.pageSize = Integer.parseInt(pageSize);
    }
    catch(NumberFormatException e){
      this.pageSize = 25;
    }
  }

  /**
   * 设置数据集
   * @param dataSetKey 数据集保存在pageContext的中的key
   */
  public void setDataSet(String dataSetKey) throws Exception
  {
    Object o = pageContext.findAttribute(dataSetKey);
    if(o != null && o instanceof EngineDataSet)
    {
      this.dsPage = (EngineDataSet)pageContext.findAttribute(dataSetKey);
      this.recordCount = dsPage.getTrueRowCount();
    }
  }

  /**
   * 需要提交的表单名称
   * @param form 表单名称
   */
  public void setForm(String form)
  {
    this.form = form==null || form.length()==0 ? null : form;
  }

  /**
   * 设置提交表单的的javascript函数，若函数为空，则直接调用form.submit();
   * @param operate 提交表单的的javascript函数
   */
  public void setOperate(String operate)
  {
    this.operate = operate==null || operate.length()==0 ? null : operate;
  }

  /**
   * 设置是否打印分页信息的属性
   * @param isPrint 是否打印分页信息
   */
  public void setIsPrint(String isPrint)
  {
    this.isPrint = !(isPrint!= null && isPrint.toLowerCase().equals("false"));
  }

  /**
   *设置超级链接的Class风格
   *@param linkClass 超级链接的Class风格
   */
  public void setGoProperty(String goProperty)
  {
    this.goProperty = goProperty;
  }

  /**
   *设置当前网页URL
   *@param jspUrl 当前网页URL
   */
  public void setJspUrl(String jspUrl)
  {
    if(jspUrl == null)
      return;
    this.attributeKey = jspUrl;
  }

  public void setDisable(String disable){
    this.disable = disable !=null && (disable.equals("1") || disable.equalsIgnoreCase("true"));
  }

  /**
   * 标签的开始函数
   */
  public int doStartTag() throws JspTagException
  {
    HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
    HttpSession session = pageContext.getSession();
    if(attributeKey == null)
      attributeKey = req.getRequestURL().toString();

    paginationInfo = PaginationInfo.getInstance(session, attributeKey+".dbnavigator");
    boolean isChange = PaginationInfo.pageControl(paginationInfo, req, pageSize, recordCount, disable);
    boolean isReget = false;
    int min = paginationInfo.getStartRecord();
    int max = paginationInfo.getEndRecord();
    if(min != dsPage.getRowMin() || max != dsPage.getRowMax() || isChange)
    {
      isReget = true;
      dsPage.setRowMin(min);
      dsPage.setRowMax(max);
      if(dsPage.isOpen())
        dsPage.refresh();
      else
        dsPage.openDataSet();
    }

    if(!isPrint)
      return isReget ? EVAL_BODY_INCLUDE : SKIP_BODY;
    try
    {
      paginationInfo.setForm(form);
      paginationInfo.setOperate(operate);
      JspWriter out = pageContext.getOut();
      StringBuffer buf = PaginationInfo.getPrintInfo(paginationInfo, attributeKey, goProperty);
      out.println(buf);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
      System.out.println("DBNavigator: Error printing info:" + ioe);
    }

    return isReget ? EVAL_BODY_INCLUDE : SKIP_BODY;
  }

  /**
   * 释放资源
   */
  public void release() {
    super.release();
    this.pageSize =25;
    this.recordCount = 0;
    this.isPrint = true;
    this.attributeKey = null;
    this.paginationInfo = null;
    this.goProperty = "style='border-style:solid; border-width:1px; border-color:#3399ff; background-color:lightcyan; color:red; font-size:8pt; width: auto'";
    this.disable = false;
  }
}

/**
 * <p>Title: 页码信息</p>
 * <p>Description: 保存分页页码的信息</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author hukn
 * @version 1.0
 */
final class PaginationInfo
{
  private int pageCount = 0;

  private int recordCount = 0;

  private int pageSize = 25;

  private int startRecord = 0;

  private int endRecord = 0;

  private int pageNo = 1;

  private String form = null;

  private String operate = null;

  private boolean isChange = false;

  /**
   * 计算页码信息
   * @param pageNo 第几页
   */
  void calculate(int pageNo)
  {
    pageCount = recordCount % pageSize > 0 ? recordCount/pageSize + 1 : recordCount/pageSize;

    if(pageNo < 1)
      pageNo = 1;
    else if(pageNo > pageCount)
      pageNo = pageCount;
    this.pageNo = pageNo;

    startRecord = (pageNo - 1) * pageSize;
    endRecord = pageNo * pageSize - 1;
    if(endRecord > recordCount -1)
      endRecord = recordCount -1;

    isChange = false;
  }

  void setPageSize(int pageSize)
  {
    if(this.pageSize == pageSize)
      return;
    this.pageSize = pageSize;
    this.isChange = true;
  }

  void setRecordCount(int recordCount)
  {
    if(this.recordCount == recordCount)
       return;
    this.recordCount = recordCount;
    this.isChange = true;
  }

  /**
   * 需要提交的表单名称
   * @param form 表单名称
   */
  void setForm(String form)
  {
    this.form = form;
  }

  /**
   * 设置操作符号，格式operate=2: 表示操作对应的控件名称operate, 相应的操作符号2
   * @param operate 操作符号
   */
  void setOperate(String operate)
  {
    this.operate = operate;
  }

  int getPageCount()  { return pageCount; }

  int getPageSize()   { return pageSize; }

  int getRecordCount(){ return recordCount; }

  int getStartRecord(){ return startRecord; }

  int getEndRecord()  { return endRecord; }

  int getPageNo()     { return pageNo; }


  private boolean isInit = false;
  public String firstDisableImage = null;
  public String prevDisableImage = null;
  public String nextDisableImage = null;
  public String lastDisableImage = null;

  public String firstEnableImage = null;
  public String prevEnableImage = null;
  public String nextEnableImage = null;
  public String lastEnableImage = null;

  public static PaginationInfo getInstance(HttpSession session, String attributeKey)
  {
    synchronized(session)
    {
      PaginationInfo paginationInfo = (PaginationInfo)session.getAttribute(attributeKey);
      if(paginationInfo == null)
      {
        paginationInfo = new PaginationInfo();
        session.setAttribute(attributeKey, paginationInfo);
      }
      return paginationInfo;
    }
  }

  public static boolean pageControl(PaginationInfo paginationInfo, HttpServletRequest request,
      int pageSize, int recordCount, boolean isDisable)
  {
    synchronized(paginationInfo)
    {
      if(!paginationInfo.isInit){
        String context = request.getContextPath();
        paginationInfo.firstDisableImage = context+"/images/arrow_1st_n.gif";
        paginationInfo.prevDisableImage  = context+"/images/arrow_left_n.gif";
        paginationInfo.nextDisableImage  = context+"/images/arrow_right_n.gif";
        paginationInfo.lastDisableImage  = context+"/images/arrow_end_n.gif";
        paginationInfo.firstEnableImage  = context+"/images/arrow_1st.gif";
        paginationInfo.prevEnableImage   = context+"/images/arrow_left.gif";
        paginationInfo.nextEnableImage   = context+"/images/arrow_right.gif";
        paginationInfo.lastEnableImage   = context+"/images/arrow_end.gif";
        paginationInfo.isInit = true;
      }
      boolean isChange = paginationInfo.recordCount != recordCount;
      paginationInfo.setPageSize(pageSize);
      paginationInfo.setRecordCount(recordCount);

      int pageNo = 1;
      try{
        pageNo = isDisable ? paginationInfo.pageNo : Integer.parseInt(request.getParameter("pageNo"));
      }
      catch(Exception ex){
        pageNo = paginationInfo.pageNo;
      }

      if(paginationInfo.isChange || paginationInfo.pageNo != pageNo)
        paginationInfo.calculate(pageNo);

      return isChange;
    }
  }

  public static StringBuffer getPrintInfo(PaginationInfo paginationInfo, String attributeKey, String goProperty)
  {
    synchronized(paginationInfo)
    {
      String jspUrl = attributeKey + (attributeKey.indexOf("?")<0 ? "?" : "&");

      int totalPage = paginationInfo.getPageCount();
      int pageNo = paginationInfo.getPageNo();
      int recordCount = paginationInfo.getRecordCount();
      boolean hasNext = pageNo != totalPage;
      boolean hasPrev = pageNo > 1;

      StringBuffer buf = new StringBuffer();
      boolean isGet = paginationInfo.form == null;

      String firstDisableImage = paginationInfo.firstDisableImage;
      String prevDisableImage = paginationInfo.prevDisableImage;
      String nextDisableImage = paginationInfo.nextDisableImage;
      String lastDisableImage = paginationInfo.lastDisableImage;

      String firstEnableImage = paginationInfo.firstEnableImage;
      String prevEnableImage  = paginationInfo.prevEnableImage;
      String nextEnableImage  = paginationInfo.nextEnableImage;
      String lastEnableImage  = paginationInfo.lastEnableImage;
      if(isGet)
      {

        if(hasPrev)
          buf.append("<a href='").append(jspUrl).append("pageNo=1'>");
        buf.append("<img src='").append(hasPrev ? firstEnableImage : firstDisableImage).append("' border=0 alt='首页'>");
        if(hasPrev)
          buf.append("</a>");
        buf.append("&nbsp;");

        if(hasPrev)
          buf.append("<a href='").append(jspUrl).append("pageNo=").append(pageNo-1).append("'>");
        buf.append("<img src='").append(hasPrev ? prevEnableImage : prevDisableImage).append("' border=0 alt='前一页'>");
        if(hasPrev)
          buf.append("</a>");
        buf.append("&nbsp;");

        if(hasNext)
          buf.append("<a href='").append(jspUrl).append("pageNo=").append(pageNo+1).append("'>");
        buf.append("<img src='").append(hasNext ? nextEnableImage : nextDisableImage).append("' border=0 alt='后一页'>");
        if(hasNext)
          buf.append("</a>");
        buf.append("&nbsp;");

        if(hasNext)
          buf.append("<a href='").append(jspUrl).append("pageNo=").append(totalPage).append("'>");
        buf.append("<img src='").append(hasNext ? lastEnableImage : lastDisableImage).append("' border=0 alt='尾页'>");
        if(hasNext)
          buf.append("</a>");
        buf.append("&nbsp;");

        buf.append("到第<input type='text' name='goPage' size ='3' value=").append(pageNo);
        buf.append(totalPage > 1 ? " " : " readonly ");
        buf.append(goProperty);
        buf.append("' onchange=\"location.href='").append(jspUrl).append("pageNo='+this.value\">页&nbsp;");

        buf.append("共").append(totalPage).append("页&nbsp;").append(recordCount).append("条记录");
      }
      else
      {
        buf.append("<script language='javascript'>function navigatorOper(page){");
        buf.append(paginationInfo.form).append(".pageNo.value=page;");
        if(paginationInfo.operate != null)
          buf.append(paginationInfo.operate);
        else
          buf.append(paginationInfo.form).append(".submit();");

        buf.append("}</script>");
        buf.append("<input type='hidden' name='pageNo' value=''>");

        if(hasPrev)
          buf.append("<a href='javascript:onClick=navigatorOper(1);'>");
        buf.append("<img src='").append(hasPrev ? firstEnableImage : firstDisableImage).append("' border=0 alt='首页'>");
        if(hasPrev)
          buf.append("</a>");
        buf.append("&nbsp;");

        if(hasPrev)
          buf.append("<a href='javascript:onClick=navigatorOper(").append(pageNo-1).append(")'>");
        buf.append("<img src='").append(hasPrev ? prevEnableImage : prevDisableImage).append("' border=0 alt='前一页'>");
        if(hasPrev)
          buf.append("</a>");
        buf.append("&nbsp;");

        if(hasNext)
          buf.append("<a href='javascript:onClick=navigatorOper(").append(pageNo+1).append(")'>");
        buf.append("<img src='").append(hasNext ? nextEnableImage : nextDisableImage).append("' border=0 alt='后一页'>");
        if(hasNext)
          buf.append("</a>");
        buf.append("&nbsp;");

        if(hasNext)
          buf.append("<a href='javascript:onClick=navigatorOper(").append(totalPage).append(")'>");
        buf.append("<img src='").append(hasNext ? lastEnableImage : lastDisableImage).append("' border=0 alt='尾页'>");
        if(hasNext)
          buf.append("</a>");
        buf.append("&nbsp;");

        buf.append("到第<input type='text' name='goPage' size ='3' value=").append(pageNo);
        buf.append(totalPage > 1 ? " " : " readonly ");
        buf.append(goProperty);
        buf.append("' onchange='navigatorOper(this.value);'>页&nbsp;");;

        buf.append("共").append(totalPage).append("页&nbsp;").append(recordCount).append("条记录");
      }
      return buf;
    }
  }
}

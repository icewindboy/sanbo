package engine.web.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * Title:        自定义的Beans
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      JAC
 * @author hukn
 * @version 1.0
 */

public class PagesControl extends TagSupport
{
  private String linkClass = null;
  private String jspUrl = null;
  private boolean hasNext = false;
  private boolean hasPrev = false;

  private int pageSize =25;
  private int pageNumber = 1;
  private int recordCount = 0;
  private String attributeKey;
  /**
   *设置具体输出哪一页
   *@param pageNumber 输出哪一页
   */
  public void setPageNumber(String pageNumber)
  {
    try{
      this.pageNumber = Integer.parseInt(pageNumber);
    }
    catch(NumberFormatException e){
      this.pageNumber = 1;
    }
  }
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
   *设置列表包含的记录总数
   *@param pageSize 输出列表包含的记录总数
   */
  public void setRecordCount(String recordCount)
  {
    try{
      this.recordCount = Integer.parseInt(recordCount);
    }
    catch(NumberFormatException e){
      this.recordCount = 0;
    }
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
  /**
   *设置超级链接的Class风格
   *@param linkClass 超级链接的Class风格
   */
  public void setLinkClass(String linkClass)
  {
    this.linkClass = linkClass;
  }
  /**
   * 从Session得到分页信息, 并保存信息
   */
  private void DoPageControl()
  {
    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
    if(this.attributeKey == null)
      this.attributeKey = request.getRequestURL().toString();

    String PageNumber = request.getParameter("pageNumber");
    String PageSize = request.getParameter("pageSize");

    int[] pageInfo = null;
    if(PageNumber == null || PageSize == null)
    {
      try{
        pageInfo = (int[])(pageContext.getSession().getAttribute(attributeKey));
      }
      catch(IllegalStateException isex){}

      if(pageInfo != null && pageInfo[4] == recordCount)
      {
        pageNumber = pageInfo[0];
        pageSize = pageInfo[1];
      }
      else if(pageInfo == null ||(pageInfo != null && pageInfo[4] != recordCount))
      {
        if(pageInfo == null)
          pageInfo = new int[5];
        pageInfo[0] = pageNumber;
        pageInfo[1] = pageSize;
        pageInfo[2] = 0;
        pageInfo[3] = pageSize > recordCount ? recordCount-1 : pageSize-1;
        pageInfo[4] = recordCount;
      }
      pageContext.getSession().setAttribute(attributeKey, pageInfo);
    }
    else
    {
      setPageNumber(PageNumber);
      setPageSize(PageSize);
      pageInfo = new int[5];
      pageInfo[0] = pageNumber;
      pageInfo[1] = pageSize;
      try{
        pageInfo[2] = Integer.parseInt(request.getParameter("rowMin"));
      }catch(NumberFormatException e){
        pageInfo[2] = 0;
      }
      try{
        pageInfo[3] = Integer.parseInt(request.getParameter("rowMax"));
      }catch(NumberFormatException e){
        pageInfo[3] = -1;
      }
      pageInfo[4] = recordCount;
      pageContext.getSession().setAttribute(attributeKey, pageInfo);
    }
  }
  /**
   *
   */
  public int doStartTag() throws JspTagException
  {
    DoPageControl();
    //
    int pos = attributeKey.indexOf("?");
    this.jspUrl = attributeKey + (pos<0 ? "?" : "&");

    int allPage = pageSize ==0 ? 0 : recordCount/pageSize;
    double allpageover = pageSize ==0 ? 0 : (double)recordCount/(double)pageSize;
    if(allpageover > allPage)
      allPage++;
    if(pageNumber > allPage)
      pageNumber = allPage;

    if(recordCount >this.pageNumber*this.pageSize)
      hasNext = true;
    else
      hasNext = false;

    if(pageNumber >1)
      hasPrev = true;
    else
      hasPrev = false;

    try
    {
      JspWriter out = pageContext.getOut();
      String firstHref = null, prevHref = null, nextHref = null, lastHref = null;
      if(hasPrev)
      {
        firstHref = "<a href='"+ jspUrl +"pageNumber=1";
        firstHref += "&pageSize="+ pageSize +"&rowMin=0&rowMax="+ (pageSize-1) +"'";
        if(linkClass!=null)
          firstHref += " class='" + linkClass + "'";
        firstHref += ">";

        int prevmin = pageSize*(pageNumber-2);
        int prevmax = pageSize*(pageNumber-1)-1;
        prevHref = "<a href='"+ jspUrl +"pageNumber="+ (pageNumber-1);
        prevHref += "&pageSize="+ pageSize +"&rowMin="+ prevmin +"&rowMax="+ prevmax +"'";
        if(linkClass!=null)
          prevHref += " class='" + linkClass + "'";
        prevHref += ">";
      }

      String sOut = hasPrev ? firstHref+"最前</a>&nbsp;" : "最前&nbsp;";
      sOut += hasPrev ? prevHref+"上一页</a>&nbsp;" : "上一页&nbsp;";
      sOut += pageNumber +"/"+ allPage +"&nbsp;";

      if(hasNext)
      {
        int nextmin = pageSize*(pageNumber);
        int nextmax;
        if((pageNumber+1) == allPage)
          nextmax = recordCount-1;
        else
          nextmax = pageSize*(pageNumber+1)-1;

        nextHref = "<a href='"+ jspUrl +"pageNumber=" + (pageNumber+1);
        nextHref += "&pageSize="+ pageSize +"&rowMin="+ nextmin +"&rowMax="+ nextmax +"'";
        if(linkClass!=null)
          nextHref += " class='" + linkClass + "'";
        nextHref += ">";

        lastHref = "<a href='"+ jspUrl +"pageNumber=" + allPage;
        lastHref += "&pageSize="+ pageSize +"&rowMin="+ pageSize*(allPage-1) +"&rowMax="+ (recordCount-1) +"'";
        if(linkClass!=null)
          lastHref += " class='" + linkClass + "'";
        lastHref += ">";
      }
      sOut += hasNext ? nextHref+"下一页</a>&nbsp;" : "下一页&nbsp;";
      sOut += hasNext ? lastHref+"最后</a>&nbsp;" : "最后&nbsp;";

      sOut += "转到第<input type=\"text\" name=\"goPage\" size =\"3\" value=\""+pageNumber+"\""
        + "style=\"border-style:solid; border-width:1px; border-color:#3399ff; background-color:lightcyan; color:red; font-size:8pt; width: auto\" onchange=\"javascript:"
        + "if(isNaN(goPage.value)){alert('您输入的不是数字！');return;}"
        + "if(goPage.value > "+ allPage +"){goPage.value="+ allPage +";}"
        + "if(goPage.value < 1){return;}"
        + "if(goPage.value == "+ pageNumber +"){return;}"
        + "var min = (goPage.value-1)*"+ pageSize +";"
        + "var max = goPage.value*" + pageSize +"-1;"
        + "if(max>"+ recordCount +"-1){max ="+ recordCount +"-1;}"
        + "location.href='"+ jspUrl
        + "pageNumber='+goPage.value+'&pageSize="+ pageSize +"&rowMin='+ min +'&rowMax='+ max\">页&nbsp;";

      out.println(sOut);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
      System.out.println("PagesControl: Error printing info:" + ioe);
    }
    return(SKIP_BODY);
  }
}
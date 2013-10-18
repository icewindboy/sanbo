<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@page import="engine.action.Operate,engine.common.PdfProducerFacade"%><%!
  String code = null;
%><%
  String operate = request.getParameter(Operate.OPERATE_KEY);
  if(String.valueOf(PdfProducerFacade.SHOW_DETAIL).equals(operate))
    code = request.getParameter("code");
  engine.common.PdfProducerFacade pdf = engine.common.PdfProducerFacade.getInstance(request, code);
  pdf.reportDirPath = getServletContext().getRealPath("/WEB-INF/report");
  String retu = pdf.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
%>
<html>
<head>
<title>英捷ERP系统</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript">
  function showFixedQuery()
  {
    showFrame('fixedQuery',true,"",true);//setTimeout("showQuery()", 500);
  }
  function showPdf()
  {
    location.href='pdfprint.jsp?operate=<%=pdf.SHOW_PDF%>&code=<%=code%>';
  }
</script>
<body oncontextmenu="window.event.returnValue=true">
  <br>
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%int iPage = loginBean.getPageSize()+2;%>
      <pc:navigator id="showdetailNav" recordCount="<%=pdf.getRowCount()%>" pageSize="<%=String.valueOf(iPage)%>"/></td>
      <TD align="right" NOWRAP>
<input name="search2" type="button" accessKey="q" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
      <input name="print" type="button" class="button" onClick="showPdf()" accessKey="p" value="打印(P)">
      <input name="print" type="button" class="button" onClick="showExcel()" accessKey="e" value="Excel(E)">
      <%if(pdf.retuUrl!=null){ String loc = "location.href='"+ pdf.retuUrl+"';";
      %><input name="button2" type="button" class="button" accessKey="c" onClick="location.href='<%=pdf.retuUrl%>'" value="返回(C)">
      <%}%></TD>
    </tr>
  </table>
<%
  int min = showdetailNav.getRowMin(request);
  int max = showdetailNav.getRowMax(request);
  pdf.printReport(pageContext, min, max+1, iPage);
%>
<%@ include file="pdf_query.jsp"%>
<%out.print(retu);%>
</body>
</html>
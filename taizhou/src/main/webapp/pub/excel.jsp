<%@ page contentType="application/vnd.ms-excel; charset=UTF-8" %><%@
include file="../pub/init.jsp"%><%@
taglib uri="/WEB-INF/pagescontrol.tld" prefix="pc"%><%
  engine.common.PdfProducerFacade pdf = engine.common.PdfProducerFacade.getInstance(request);
  //String retu = pdf.doService(request, response);
  response.setHeader("Content-Disposition", "attachment; filename=export.xls");
%><pc:ExcelHtml/>
<body oncontextmenu="window.event.returnValue=true">
<%pdf.printExcelReport(pageContext);%>
</body>
</html>
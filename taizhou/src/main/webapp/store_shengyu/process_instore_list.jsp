<%--外加工入库单 框架网页--%>
<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%><%
  String pageCode = "process_instore_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  String query = request.getQueryString();
  String operate = request.getParameter("operate");
  String init = String.valueOf(engine.action.Operate.INIT);
%>
<html>
<head><title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
</head>
<FRAMESET frameborder="0" framespacing="3" border="1" bordercolor="#f0f0e0" rows=*,160>
  <FRAME name='top' src='process_instore_top.jsp?<%=query==null ? "" : query%>' scrolling="auto">
  <FRAME id="bottom" name='bottom' src=<%=operate != null && operate.equals(init) ? "../blank.htm" : "process_instore_bottom.jsp"%> scrolling="auto">
  <NOFRAMES>
    <body>
    <p>此网页使用了框架，但您的浏览器不支持框架。</p>
    </body>
  </NOFRAMES>
</FRAMESET>
</HTML>

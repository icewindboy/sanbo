<%--移库单 框架网页--%>
<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%><%
  String pageCode = "move_store_list";
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
  <FRAME name='top' src='move_store_top.jsp?<%=query==null ? "" : query%>' scrolling="auto">
  <FRAME id="bottom" name='bottom' src=<%=operate != null && operate.equals(init) ? "../blank.htm" : "move_store_bottom.jsp"%> scrolling="auto">
  <NOFRAMES>
    <body>
    <p>此网页使用了框架，但您的浏览器不支持框架。</p>
    </body>
  </NOFRAMES>
</FRAMESET>
</HTML>

<%--生产领料单 框架网页--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%><%
  String pageCode = "receive_material_list";
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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<FRAMESET frameborder="0" framespacing="3" border="1" bordercolor="#f0f0e0" rows=*,160>
  <FRAME name='top' src='receive_material_top.jsp?<%=query==null ? "" : query%>' scrolling="auto">
  <FRAME id="bottom" name='bottom' src=<%=operate != null && operate.equals(init) ? "../blank.htm" : "receive_material_bottom.jsp"%> scrolling="auto">
  <NOFRAMES>
    <body>
    <p>此网页使用了框架，但您的浏览器不支持框架。</p>
    </body>
  </NOFRAMES>
</FRAMESET>
</HTML>

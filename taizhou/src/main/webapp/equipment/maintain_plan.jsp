<!--保养计划--><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%
  String pageCode = "maintain_plan";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;//权限管理
  String query = request.getQueryString();//null或查询串
  String operate = request.getParameter("operate");//操作
  String init = String.valueOf(engine.action.Operate.INIT);//初始化操作
%>
<html>
<head><title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<FRAMESET rows=*,133 cols="*" framespacing="3" frameborder="0" border="1" bordercolor="#f0f0e0">
  <FRAME name='top' src='maintain_plan_top.jsp?<%=query==null ? "" : query%>' scrolling="auto">
  <FRAME id="bottom" name='bottom' src=<%=operate != null && operate.equals(init) ? "../blank.htm" : "maintain_plan_bottom.jsp"%> scrolling="auto">
  <NOFRAMES>
  <body>
    <p>此网页使用了框架，但您的浏览器不支持框架。</p>
  </body>
  </NOFRAMES>
</FRAMESET>
</HTML>
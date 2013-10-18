<!--设备配件购置申请--><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%
  String pageCode = "buy_fitting_requisition";
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
<FRAMESET rows=*,153 cols="*" framespacing="3" frameborder="0" border="1" bordercolor="#f0f0e0">
  <FRAME name='top' src='buy_fitting_requisition_top.jsp?<%=query==null ? "" : query%>' scrolling="auto">
  <FRAME id="bottom" name='bottom' src=<%=operate != null && operate.equals(init) ? "../blank.htm" : "buy_fitting_requisition_bottom.jsp"%> scrolling="auto">
  <NOFRAMES>
  <body>
  <frameset rows="*,80" frameborder="NO" border="0" framespacing="0">
    <p>此网页使用了框架，但您的浏览器不支持框架。</p>
    <frame name="bottomFrame" scrolling="NO" noresize>
  </frameset>
  </body>
  </NOFRAMES>
</FRAMESET>
</HTML>
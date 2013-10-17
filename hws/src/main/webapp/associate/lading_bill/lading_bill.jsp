<%@page contentType="text/html; charset=UTF-8" %>
<%request.setCharacterEncoding("UTF-8");
  //session.setMaxInactiveInterval(900);
  response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","max-age=0");
  response.addHeader("Cache-Control","pre-check=0");
  response.addHeader("Cache-Control","post-check=0");
  response.setDateHeader("Expires", 0);
  engine.common.OtherLoginBean loginBean = engine.common.OtherLoginBean.getInstance(request);
  if(!loginBean.isLogin(request, response))
    return;
%><%@ taglib uri="/WEB-INF/pagescontrol.tld" prefix="pc"%><%
  String pageCode = "lading_bill";

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
<FRAMESET frameborder="0" framespacing="3" border="1" bordercolor="#f0f0e0" rows=*,160>
  <FRAME name='top' src='lading_bill_top.jsp?<%=query==null ? "" : query%>' scrolling="auto">
  <FRAME id="bottom" name='bottom' src=<%=operate != null && operate.equals(init) ? request.getContextPath()+"/blank.htm" : "lading_bill_buttom.jsp"%> scrolling="auto">
  <NOFRAMES>
    <body>
    <p>此网页使用了框架，但您的浏览器不支持框架。</p>
    </body>
  </NOFRAMES>
</FRAMESET>
</HTML>
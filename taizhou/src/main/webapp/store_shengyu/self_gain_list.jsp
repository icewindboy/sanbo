<%--�������ϵ� �����ҳ--%>
<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%><%
  String sysparam = loginBean.getSystemParam("SC_INSTORE_SHOW_ADD_FIELD");
  if(sysparam!= null && sysparam.equals("1"))
  {
    response.sendRedirect("newself_gain_list.jsp?operate=0&src=../pub/main.jsp");
    return;
  }
  String pageCode = "self_gain_list";
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
  <FRAME name='top' src='self_gain_top.jsp?<%=query==null ? "" : query%>' scrolling="auto">
  <FRAME id="bottom" name='bottom' src=<%=operate != null && operate.equals(init) ? "../blank.htm" : "self_gain_bottom.jsp"%> scrolling="auto">
  <NOFRAMES>
    <body>
    <p>����ҳʹ���˿�ܣ��������������֧�ֿ�ܡ�</p>
    </body>
  </NOFRAMES>
</FRAMESET>
</HTML>

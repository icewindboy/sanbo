<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%><%
  String query = request.getQueryString();
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<frameset cols="250,*" frameborder="0" framespacing="3" border="1" bordercolor="#f0f0e0" framespacing="0" rows="*">
  <frame id="depttree" name="depttree" src="client_bom_tree.jsp?<%=query==null ? "" : query%>" scrolling="no">
  <frame id="deptcontext" name="deptcontext" src="client_bom_edit.jsp" scrolling="auto">
</frameset>
<noframes>
<body bgcolor="#FFFFFF" text="#000000">
</body>
</noframes>
</html>
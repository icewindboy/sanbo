<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<html>
<HEAD>
<title>演示系统(<%=request.getServerName()%>)</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</HEAD>
<SCRIPT language="javascript">
function logout(){
  window.open("logout.jsp","logout","top=0,left=0,width=5,height=5,scrollbars=no,toolbar=no,menubar=no,resizable=no,locationbar=no");
}
</SCRIPT>
<FRAMESET frameBorder=0 cols="*,100%" onunload="logout();">
<FRAME name=hidden src="about:blank" noresize scrolling=no>
<FRAME id=bottompage src="public.jsp" noresize scrolling=no>
<NOFRAMES>
 <body>
  <p>This page uses frames, but your browser doesn't support them.</p>
 </body>
</NOFRAMES>
</FRAMESET>
</html>
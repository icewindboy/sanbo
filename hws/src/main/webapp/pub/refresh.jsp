<%@ page contentType="text/html; charset=UTF-8" %><%@ page import="engine.common.UserFacade"%><%@ include file="../pub/init.jsp"%><%
  //response.setHeader("Refresh", "20");
%><%--META HTTP-EQUIV="Refresh" CONTENT="5;URL=http://host/path"--%>
<script language="javascript" src="../scripts/validate.js"></script><script language='javascript'><%
  out.print("parent.online_user_num.innerText='在线人数:");
  out.print(UserFacade.size());
  out.print("';");
  if(loginBean.getUser().hasMessage())
    out.print("openurl('','user_online_message.jsp',null,null,410,220,true)");
%></script>
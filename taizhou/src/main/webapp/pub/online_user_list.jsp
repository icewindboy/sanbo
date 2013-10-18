<%@page contentType="text/html; charset=UTF-8" %><%@ page import="engine.common.*"%><%@ include file="../pub/init.jsp"%><%
%><html>
<head>
<title>在线人员列表</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<BODY oncontextmenu="window.event.returnValue=false">
<table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
  <tr class="tableTitle">
    <td nowrap height=18 width=10></td>
    <td nowrap width=60>发短消息</td>
    <td nowrap>用户名</td>
    <td nowrap>真实姓名</td>
    <td nowrap>部门</td>
  </tr>
  <%User[] users = UserFacade.getUsers();
    int i;
    for(i=0; i<users.length; i++){
  %>
  <tr>
    <td class="td" nowrap width=10><%=i+1%></td>
    <td class="td" nowrap align="center"><input name="image" class="img" type="image"
title='发短消息' src='../images/messages.gif' border=0 onClick="sengMessage(<%=users[i].getUserId()%>)"></td>
    <td class="td" nowrap><%=users[i].getLonginName()%></td>
    <td class="td" nowrap><%=users[i].getUserName()%></td>
    <td class="td" nowrap><%=users[i].getDeptName()%></td>
  </tr>
  <%}
    for(; i < 8; i++){
  %>
  <tr>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
  </tr>
  <%}%>
</table>
<script language="javascript">
function sengMessage(userid)
{
  lockScreenToWait("处理中, 请稍候！");
  location.href = 'send_online_message.jsp?operate=0&userid='+userid;
}
initDefaultTableRow('tableview1',1);
</script>
</body>
</html>
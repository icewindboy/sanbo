<%@page contentType="text/html; charset=UTF-8" %><%@ page import="engine.common.*"%><%@ include file="../pub/init.jsp"%><%!
  String userid = null;
  String username = null;
%><%
  String operate = request.getParameter("operate");
  String text = null;
  if("0".equals(operate))
  {
    userid = request.getParameter("userid");
    userid = userid == null ? "" : userid;
    User toUser = UserFacade.getUser(userid);
    if(toUser == null)
    {
      out.print("<script language=javascript'>alert('该用户已经离线！');</script>");
      return;
    }
    username = toUser.getUserName();
  }
  else if("1".equals(operate))
  {
    String touserid = request.getParameter("userid");
    if(touserid == null)
      out.print("<script language=javascript'>alert('请选择要发送消息的用户！');</script>");
    else
    {
      text = request.getParameter("message");
      UserMessage message = new UserMessage(loginBean.getUserID(),
          loginBean.getUserName(), touserid, text);
      text = null;
      try{
        UserFacade.sendMessage(message);
        out.print("<script language='javascript'>window.close();</script>");
        return;
      }catch(UserNotFoundException ex){
        out.print("<script language='javascript'>alert('该用户已经离线！');</script>");
      }
    }
  }
%><html>
<head>
<title>发送消息</title><META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<BODY oncontextmenu="window.event.returnValue=false">
<form name="form1" method="post" action="send_online_message.jsp" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="userid" VALUE="<%=userid%>">
<table class="editformbox" width="100%" height="100%" CELLSPACING=1 CELLPADDING=0>
<tr><td>
  <table CELLSPACING=1 CELLPADDING=0 align="center" bgcolor="#f0f0f0">
    <tr>
      <td>
        <table CELLSPACING="1" CELLPADDING="1" BORDER="0">
          <tr>
            <td noWrap class="tdTitle">接受人</td>
            <td noWrap class="td"><%=username%></td>
          </tr>
          <tr>
            <td noWrap class="tdTitle">消息内容</td>
            <td noWrap class="td"><textarea name="message" style="width:320;height:150" title="可以使用Ctrl+Enter直接发送消息" onkeydown="EnterSendMsg()"><%=text==null ? "" : text%></textarea></td>
          </tr>
        </table>
			</td>
    </tr>
  </table>
  <table width="200" border="0" align="center" cellpadding="1" cellspacing="1">
    <tr>
      <td nowrap class="td" align="center"><input name="button" type="button" class="button" onClick="submitForm(1);" value="发送(S)" onKeyDown="return getNextElement();">
        <pc:shortcut key="s" script='submitForm(1);'/>
        <input name="button2" type="button" class="button" onClick="window.close();" value="关闭(C)" onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script='window.close();'/>
      </td>
    </tr>
  </table>
</td></tr>
</table>
</form>
<script language="javascript">
function EnterSendMsg(){
  if(event.keyCode == 13 && event.ctrlKey)
    submitForm(1);
}
function submitForm(oper)
{
  if(form1.message.value.trim() == '')
  {
    alert("消息内容不能为空");
    return;
  }
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
</body>
</html>
<%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%><%
  engine.common.UserMessage message = loginBean.getUser().getMessage();
%><html>
<head>
<title>消息</title><META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>

<BODY oncontextmenu="window.event.returnValue=false">
<table class="editformbox" width="100%" height="100%" CELLSPACING=1 CELLPADDING=0>
<tr><td>
  <table CELLSPACING=1 CELLPADDING=0 align="center" bgcolor="#f0f0f0">
    <tr>
      <td> <table CELLSPACING="1" CELLPADDING="1" BORDER="0">
          <tr>
            <td noWrap class="tdTitle">发送人</td>
            <td noWrap class="td"><%=message.getFromUserName()%></td>
            <td noWrap class="tdTitle">发送时间</td>
            <td noWrap class="td"><%=message.getSendDateString()%></td>
          </tr>
          <tr>
            <td noWrap class="tdTitle">消息内容</td>
            <td noWrap class="td" colspan="3"><textarea name="message" style="width:320;height:150" readonly><%=message.getMessage()%></textarea></td>
          </tr>
        </table>
			</td>
    </tr>
  </table>
  <table width="200" border="0" align="center" cellpadding="1" cellspacing="1">
    <tr>
      <td nowrap class="td" align="center"><input name="button" type="button" class="button"
onClick="sengMessage('<%=message.getFromUserId()%>');" value="回复(R)" onKeyDown="return getNextElement();">
        <pc:shortcut key="r" script='<%="sengMessage("+message.getFromUserId()+");"%>'/>
        <input name="button2" type="button" class="button" onClick="window.close();" value="关闭(C)" onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script='window.close();'/>
      </td>
    </tr>
  </table>
</td></tr>
</table>
<script language="javascript">
function sengMessage(userid)
{
  lockScreenToWait("处理中, 请稍候！");
  location.href = 'send_online_message.jsp?operate=0&userid='+userid;
}
</script>
</body>
</html>
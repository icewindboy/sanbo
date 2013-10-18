<%@ page contentType="text/html; charset=UTF-8" %><%
  engine.erp.common.install.PatchSystem patchSystem = engine.erp.common.install.PatchSystem.getInstance(request);
  if(!patchSystem.isLocalHost(request, getServletContext().getRealPath("/WEB-INF")))
  {
    out.print("只能在本机升级程序！");
    return;
  }
  session.setMaxInactiveInterval(1200);
  String retu = patchSystem.doService(request, response);
  String curUrl = request.getRequestURL().toString();
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../../scripts/validate.js"></script>
<script language="javascript">
function submitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function errorinfo()
{
  alert('升级过程中有错误信息, 请查看日志文件(engine.log)。');
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <br>
  <table align="center" WIDTH="500" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"><b>升级系统</b></td>
  </tr></table><br>
  <table id="tableview1" border="0" cellpadding=1 cellspacing=1 class="table" align="center" width="600">
    <tr>
      <td align="center"><b>在升级系统前请备份数据</b></td>
    </tr>
    <tr>
      <td align="center"><input type="button" name="Submit" class="button" value=" 升级系统 " onClick="submitForm(<%=patchSystem.PATCH%>)">
      </td>
    </tr>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',0);</SCRIPT>
</form>
<%=retu%>
</BODY>
</Html>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%engine.erp.system.B_NodeInfo nodeBean = engine.erp.system.B_NodeInfo.getInstance(request);
  engine.erp.system.B_nodePrivilige nodePrivilige = engine.erp.system.B_nodePrivilige.getInstance(request);
  String retu = nodeBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  RowMap row = nodeBean.rowInfo;
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">界面权限表</TD>
  </TR></TABLE>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <TABLE BORDER="0" CELLPADDING="1" CELLSPACING="3">
    <TR>
      <td noWrap class="tableTitle">&nbsp;权限代号&nbsp;</td>
      <td noWrap class="td"><pc:select name="priviligeId" style="width:222">
          <%=nodePrivilige.getNodePriviligeForOption(row.get("priviligeId"))%>
      </pc:select> </td>
    </TR>
    <TR>
      <td colspan="2" noWrap class="tableTitle"><br> <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+");"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value="关闭(C)">
        <pc:shortcut key="c" script='parent.hideFrameNoFresh()'/>
      </td>
    </TR>
  </TABLE>
</form><%out.print(retu);%>
</BODY>
</Html>
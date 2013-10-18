<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("balancemodelist", request, response))
    return;
  engine.erp.baseinfo.B_BalanceMode balanceBean = engine.erp.baseinfo.B_BalanceMode.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script>
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr><TD NOWRAP align="center">辅助信息</TD></tr></table>
<%String retu = balanceBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = balanceBean.dsBalance;
  RowMap row = balanceBean.rowInfo;
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">&nbsp;编&nbsp;码&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dm" VALUE="<%=row.get("dm")%>" SIZE="40" MAXLENGTH="6" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;结算方式&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="jsfs" VALUE="<%=row.get("jsfs")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("jsfs").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;科目代码&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="kmdm" VALUE="<%=row.get("kmdm")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("kmdm").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=balanceBean.OPERATE_POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+ balanceBean.OPERATE_POST +")"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value="关闭(C)">
        <pc:shortcut key="c" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
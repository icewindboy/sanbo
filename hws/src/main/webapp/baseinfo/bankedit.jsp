<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("banklist", request, response))
    return;
  engine.erp.baseinfo.B_Bank bankBean = engine.erp.baseinfo.B_Bank.getInstance(request);
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
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr><TD NOWRAP align="center">银行信息</TD></tr></table>
<%String retu = bankBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = bankBean.getOneTable();
  RowMap row = bankBean.getRowinfo();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">编&nbsp;码</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dm" VALUE="<%=row.get("dm")%>" SIZE="40" MAXLENGTH="6" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;银行名称&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="yhmc" VALUE="<%=row.get("yhmc")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("yhmc").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;地&nbsp;址&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="addr" VALUE="<%=row.get("addr")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("addr").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;电&nbsp;话&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="tel" VALUE="<%=row.get("tel")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("tel").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;联系人&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="lxr" VALUE="<%=row.get("lxr")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("lxr").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 ">
        <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +")"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
        <pc:shortcut key="s" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
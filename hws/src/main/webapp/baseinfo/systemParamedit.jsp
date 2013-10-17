<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="0">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("systemParamlist", request, response))
    return;
%><jsp:useBean id="systemParamBean" scope="session" class="engine.erp.system.B_SystemParam"/>
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
<table WIDTH="90%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">系统参数表</TD>
  </tr></table>
<%String retu = systemParamBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURI();
  EngineDataSet ds = systemParamBean.getOneTable();
  RowMap    row = systemParamBean.getRowinfo();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">&nbsp;参数编号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="code"  style="width:260" VALUE="<%=row.get("code")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("code").getPrecision()%>" CLASS="edline"  readonly >
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;参数名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="name" style="width:260" VALUE="<%=row.get("name")%>" SIZE="40" CLASS="edline" readonly>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;参数值</td>
      <td noWrap class="td"><input type="TEXT" name="value" style="width:260" value="<%=row.get("value")%>" size="40" maxlength="<%=ds.getColumn("value").getPrecision()%>" class="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;备注</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="bz"  style="width:260" VALUE="<%=row.get("bz")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("bz").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br> <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+");"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value="关闭(C)">
        <pc:shortcut key="c" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form><%out.print(retu);%>
</BODY>
</Html>
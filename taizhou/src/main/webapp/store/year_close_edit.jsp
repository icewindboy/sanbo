<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%String pageCode = "produce_use";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.B_StoreClose  StoreCloseBean  =  engine.erp.store.B_StoreClose.getInstance(request);
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
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">月结</TD>
  </tr></table>
<%
 String retu = StoreCloseBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();

  boolean isEdit = StoreCloseBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);
  String readonly = isEdit ? "" : "readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">&nbsp;日期</td>
      <td noWrap class="td">
    <INPUT TYPE="TEXT" NAME="jsrq" VALUE="<%=StoreCloseBean.jsrq%>" SIZE="20"  CLASS="edbox" <%=readonly%>>
    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.jsrq);"></a>
      </td>
    </tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <%if(isEdit){%>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 月结 "><%}%>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
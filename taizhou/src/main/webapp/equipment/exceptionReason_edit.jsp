<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String pageCode  = "exceptionReason";
%>
<%if(!loginBean.hasLimits("exceptionReason", request, response))
    return;
  engine.erp.equipment.B_ExceptionReason  ExceptionReasonBean=engine.erp.equipment.B_ExceptionReason.getInstance(request);
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
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
    <TD NOWRAP align="center">故障原因设置</TD>
  </tr></table>
<%String retu = ExceptionReasonBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
  return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = ExceptionReasonBean.getOneTable();
  RowMap row = ExceptionReasonBean.getRowinfo();
  //boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
	<tr>
	  <td width="" noWrap class="tdtitle">故障原因编码</td>
	  <td width="" noWrap class="td">
          <INPUT TYPE="text" NAME="excepReasonCode" style="width:130" VALUE="<%=row.get("excepReasonCode")%>" class=edbox>
	  </td>
        </tr>
	<tr>
	  <td noWrap class="tdtitle">故障原因</td>
	  <td noWrap class="td">
          <textarea name="excepReasonName" rows="4" onKeyDown="return getNextElement();" style="width:240"><%=row.get("excepReasonName")%></textarea></td>
	</tr>
	<td colspan="2" noWrap class="tableTitle"> &nbsp;&nbsp;&nbsp;&nbsp;
          <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 ">
	  <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
	</td>
	</tr>
  </table>
</form>
</BODY>
</Html>
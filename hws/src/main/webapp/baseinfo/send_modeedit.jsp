<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "send_mode";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
if(!loginBean.hasLimits("send_mode", request, response))
    return;
   engine.erp.baseinfo.B_SendMode B_SendModeBean  =   engine.erp.baseinfo.B_SendMode.getInstance(request);
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
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">发货方式</TD>
  </tr></table>
<%
  String retu = B_SendModeBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = B_SendModeBean.getOneTable();
  RowMap row = B_SendModeBean.getRowinfo();
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
  String edClass = isCanAdd?"class=edbox":"class=ednone";
  String readonly = isCanAdd ?"":"readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="SendModeID" VALUE="<%=row.get("SendModeID")%>">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;发货方式编码</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="SendModeCode" VALUE="<%=row.get("SendModeCode")%>" SIZE="30" MAXLENGTH="<%=ds.getColumn("SendModeCode").getPrecision()%>" CLASS="ednone" readonly >
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;发货方式</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="SendMode" VALUE="<%=row.get("SendMode")%>" SIZE="30" MAXLENGTH="<%=ds.getColumn("SendMode").getPrecision()%>" <%=edClass%> <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
      <%if(loginBean.hasLimits(pageCode,op_add)) { String save = "sumitForm("+Operate.POST+",-1)";%>
        <%if(isCanAdd)%><input name="button"title = "保存"  type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存(S) "><pc:shortcut key="s" script='<%=save%>'/><%}%>
        <%if(loginBean.hasLimits(pageCode,op_add)) { String clo = "parent.hideFrameNoFresh()";%>
        <input name="button2" type="button" title = "关闭" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(C) "><pc:shortcut key="c" script='<%=clo%>'/><%}%>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
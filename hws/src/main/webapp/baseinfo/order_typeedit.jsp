<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "order_type";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
if(!loginBean.hasLimits(pageCode, request, response))
    return;
   engine.erp.baseinfo.B_OrderType B_OrderTypeBean  =   engine.erp.baseinfo.B_OrderType.getInstance(request);
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
    <TD NOWRAP align="center">合同类型设置</TD>
  </tr></table>
<%
  String retu = B_OrderTypeBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = B_OrderTypeBean.getOneTable();
  RowMap row = B_OrderTypeBean.getRowinfo();
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
  String edClass = isCanAdd?"class=edbox":"class=ednone";
  String readonly = isCanAdd?"":"readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="ordertypeid" VALUE="<%=row.get("ordertypeid")%>">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;合同类型编号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="ordertypecode" VALUE="<%=row.get("ordertypecode")%>" SIZE="30" MAXLENGTH="<%=ds.getColumn("ordertypecode").getPrecision()%>" CLASS="ednone" readonly >
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;合同类型</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="ordertype" VALUE="<%=row.get("ordertype")%>" SIZE="30" MAXLENGTH="<%=ds.getColumn("ordertype").getPrecision()%>" <%=edClass%> <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
      <%if(loginBean.hasLimits(pageCode,op_add)) { String save = "showInterFrame("+Operate.POST+",-1)";%>
        <%if(isCanAdd)%><input name="button"title = "保存"  type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存(S) "><pc:shortcut key="s" script='<%=save%>'/><%}%>
        <%if(loginBean.hasLimits(pageCode,op_add)) { String clo = "parent.hideFrameNoFresh()";%>
        <input name="button2" type="button" title = "关闭" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(C) "><pc:shortcut key="c" script='<%=clo%>'/><%}%>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
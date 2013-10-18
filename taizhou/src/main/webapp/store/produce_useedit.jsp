<%--仓库用途编辑页面--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
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
  engine.erp.store.B_ProduceUse  produceUseBean  =  engine.erp.store.B_ProduceUse.getInstance(request);
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
    <TD NOWRAP align="center">用途信息</TD>
  </tr></table>
<%String retu = produceUseBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = produceUseBean.getOneTable();
  RowMap row = produceUseBean.getRowinfo();
  boolean isEdit = produceUseBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);
  String readonly = isEdit ? "" : "readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;用途编号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="ytbh" VALUE="<%=row.get("ytbh")%>" SIZE="20" MAXLENGTH="<%=ds.getColumn("ytbh").getPrecision()%>" CLASS="edbox" <%=readonly%>></td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;用途名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="ytmc" VALUE="<%=row.get("ytmc")%>" SIZE="20" MAXLENGTH="<%=ds.getColumn("ytmc").getPrecision()%>" CLASS="edbox" <%=readonly%>>
      </td>
    </tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <%if(isEdit){%>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 "><%}%>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "quality_reason";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("quality_reason", request, response))
    return;
  engine.erp.quality.B_QualityReason  ReasonBean=engine.erp.quality.B_QualityReason.getInstance(request);
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
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr><TD NOWRAP align="center">质量原因</TD></tr></table>
<%String retu = ReasonBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = ReasonBean.getOneTable();
  RowMap row = ReasonBean.getRowinfo();
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
  String edClass = "class=edbox";
  String readonly = isCanAdd?"":"readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">&nbsp;原因编码</td>
       <td noWrap class="td">
        <%String reasonCode = row.get("reasonCode");%>
        <INPUT TYPE="text" NAME="reasonCode" style="width:130" VALUE="<%=reasonCode%>" <%=edClass%>>
      </td>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;原因名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="reasonName" VALUE="<%=row.get("reasonName")%>" style="WIDTH:130" MAXLENGTH="<%=ds.getColumn("reasonName").getPrecision()%>" <%=edClass%>>
      </td>
    </tr>
    <td colspan="2" noWrap class="tableTitle">
      <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 ">
      <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
    </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
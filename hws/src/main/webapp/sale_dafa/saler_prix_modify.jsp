<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "saler_prix";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
if(!loginBean.hasLimits("saler_prix", request, response))
    return;
  engine.erp.sale.B_SalerPrixDetail b_SalerPrixDetailBean  =  engine.erp.sale.B_SalerPrixDetail.getInstance(request);
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
    <TD NOWRAP align="center">更正</TD>
  </tr></table>
<%
  String retu = b_SalerPrixDetailBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  //EngineDataSet ds = b_SalerPrixDetailBean.getDetailTable();
  RowMap row = b_SalerPrixDetailBean.getDetailRow();
  //boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
  String edClass = "class=edbox";//isCanAdd?"class=edbox":"class=ednone";
  String readonly =""; //isCanAdd?"":"readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="tdhwid" VALUE="<%=row.get("tdhwid")%>">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;差价提成率(%)</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="cjtcl" VALUE="<%=row.get("cjtcl")%>" SIZE="30"  <%=edClass%> <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;计息天数</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="jxts" VALUE="<%=row.get("jxts")%>" SIZE="30"  <%=edClass%> <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;回款天数</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="hlts" VALUE="<%=row.get("hlts")%>" SIZE="30"  <%=edClass%> <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;基准价</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="jzj" VALUE="<%=row.get("jzj")%>" SIZE="30"  <%=edClass%> <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">回款提成率(%)</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="hltcl" VALUE="<%=row.get("hltcl")%>" SIZE="30"  <%=edClass%> <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <%
          String ss= "sumitForm("+b_SalerPrixDetailBean.DETAIL_POST+");";
         %>
         <input name="button" type="button" class="button" onClick="<%=ss%>" value=" 保存(S) ">
         <pc:shortcut key="s" script='<%=ss%>'/>
        <input name="button2" type="button" class="button" onClick="window.close()" value=" 关闭(X) ">
         <pc:shortcut key="x" script='window.close()'/>

      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
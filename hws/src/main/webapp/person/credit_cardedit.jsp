<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "credit_card";
%>
<%
if(!loginBean.hasLimits("credit_card", request, response))
    return;
  engine.erp.person.B_CreditCard b_creditcardBean  =  engine.erp.person.B_CreditCard.getInstance(request);
  String retu = b_creditcardBean.doService(request, response);
  //out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_creditcardBean.getOneTable();
  RowMap row = b_creditcardBean.getRowinfo();
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
  String edClass = isCanAdd?"class=edbox":"class=ednone";
  String readonly = isCanAdd?"":"readonly";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>

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
    <TD NOWRAP align="center">银行信用卡信息</TD>
  </tr></table>

<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="xykID" VALUE="<%=row.get("xykID")%>">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;信用卡编号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="xykbh" VALUE="<%=row.get("xykbh")%>" SIZE="30" MAXLENGTH="<%=ds.getColumn("xykbh").getPrecision()%>" CLASS="ednone" readonly >
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;信用卡名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="xykmc" VALUE="<%=row.get("xykmc")%>" SIZE="30" MAXLENGTH="<%=ds.getColumn("xykmc").getPrecision()%>" <%=edClass%> <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;信用卡号长度</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="xykhcd" VALUE="<%=row.get("xykhcd")%>" SIZE="30" MAXLENGTH="<%=ds.getColumn("xykhcd").getPrecision()%>" <%=edClass%> <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <%if(isCanAdd){
          String ss= "sumitForm("+Operate.POST+");";
         %>
         <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存(S) ">
         <pc:shortcut key="s" script='<%=ss%>'/>
        <%}%>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(X) ">
         <pc:shortcut key="x" script='parent.hideFrameNoFresh()'/>

      </td>
    </tr>
  </table>
</form>
<%out.print(retu);%>
</BODY>
</Html>
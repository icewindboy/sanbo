<%@ page contentType="text/html; charset=UTF-8" import="engine.erp.baseinfo.*"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
  if(!loginBean.hasLimits("propertylist", request, response))
    return;
  engine.erp.baseinfo.SalePropertiesBean salePropertiesBean=engine.erp.baseinfo.SalePropertiesBean.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">枚举值定义</TD>
  </tr>
</table>
<%
  String retu = salePropertiesBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
   <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">&nbsp;枚举ID:</td>
      <td noWrap class="td"><%=salePropertiesBean.venumkey%>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;枚举值:</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="venumvalue" VALUE="<%=salePropertiesBean.venumvalue%>" SIZE="35"  CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=SalePropertiesBean.ENUM_POST%>,<%=Integer.parseInt(request.getParameter("rownum"))%>);" value=" 保存 ">
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
      </td>
    </tr>
  </table>
</form>
<script LANGUAGE="javascript">
</BODY>
</Html>

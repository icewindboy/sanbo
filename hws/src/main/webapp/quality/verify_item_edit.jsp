<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "verify_item";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
  if(!loginBean.hasLimits("verify_item", request, response))
    return;
  engine.erp.quality.B_CheckItem quality_CheckBean = engine.erp.quality.B_CheckItem.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
var str=null;
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function showCheckID(){
if(form1.checkstyle.value=='7'){
checkID.style.display='block';
dw.style.display='none';
standard.style.display='none';
form1.unit.value="";
form1.appeal.value="";
}
else{
dw.style.display='block';
standard.style.display='block';
 }
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">检验项目管理</TD>
  </tr></table>
<%String retu = quality_CheckBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = quality_CheckBean.getOneTable();
  RowMap row = quality_CheckBean.getRowinfo();
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
  boolean isCanEdit = loginBean.hasLimits(pageCode, op_edit);
  String edClass = "class=edbox";
  String readonly = isCanAdd?"":"readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="checktype" value="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td width="55" noWrap class="tableTitle">&nbsp;代码</td>
      <td width="157" noWrap class="td">
      <%String code = row.get("code");%>
       <INPUT TYPE="text" NAME="code" style="width:130" VALUE="<%=code%>" <%=edClass%> onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">检验类型</td>
      <td noWrap class="td">
        <pc:select name='checkstyle' style="width:130" addNull='1' value='<%=row.get("checktype")%>' onSelect="showCheckID()">
        <pc:option value="1">原纸</pc:option>
        <pc:option value="2">原膜</pc:option>
        <pc:option value="3">成品纸</pc:option>
        <pc:option value="4">成品膜</pc:option>
        <pc:option value="5">辅助材料</pc:option>
        <pc:option value="6">化工原料</pc:option>
        <pc:option value="7">外观</pc:option>
        <pc:option value="8">生产过程</pc:option>
        </pc:select>
</td>
    </tr>
    <tr id="checkID">
      <td noWrap class="tableTitle">检验项目</td>
      <td noWrap class="td">
     <INPUT TYPE="TEXT" NAME="checkItem" VALUE="<%=row.get("checkItem")%>" style="WIDTH:130" MAXLENGTH="<%=ds.getColumn("checkItem").getPrecision()%>" <%=edClass%>>
       </td>
    </tr>
    <tr id="dw">
      <td noWrap class="tableTitle">单位</td>
      <td noWrap class="td">
      <INPUT TYPE="text" NAME="unit" style="width:130" VALUE="<%=row.get("unit")%>" <%=edClass%>></td>
    </tr>
    <tr id="standard">
      <td noWrap class="tableTitle">标准要求</td>
      <td noWrap class="td">
      <INPUT TYPE="text" NAME="appeal" style="width:130" VALUE="<%=row.get("appeal")%>" <%=edClass%>></td>
    </tr>
    <td colspan="2" noWrap class="tableTitle">&nbsp;&nbsp;&nbsp;&nbsp;<input name="button" type="button" class="button" onClick="if(form1.v_checkstyle.value==''){alert('请选择检验类型'); return;}sumitForm(<%=Operate.POST%>);" value=" 保存 ">
      <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
    </td>
    </tr>
  </table>
</form>
</BODY>
<script language="javascript">
if(form1.checkstyle.value=='7')
{
checkID.style.display='block';
dw.style.display='none';
standard.style.display='none';
form1.unit.value="";
form1.appeal.value="";
}
</script>
</Html>
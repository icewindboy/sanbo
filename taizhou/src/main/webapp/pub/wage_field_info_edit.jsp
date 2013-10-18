<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String pageCode = "wage_field_info";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("wage_field_info", request, response))
    return;
  engine.erp.person.B_WageFieldInfo b_WageFieldInfoBean  =  engine.erp.person.B_WageFieldInfo.getInstance(request);
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
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr><TD NOWRAP align="center">工资款项设置</TD></tr></table>
<%String retu = b_WageFieldInfoBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_WageFieldInfoBean.getOneTable();
  RowMap row = b_WageFieldInfoBean.getRowinfo();
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);

  String edClass = true?"class=edbox":"class=edline";
  String readonly = true?"":"readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table  BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td width="67" noWrap class="tableTitle">&nbsp;名 称 <br></td>
      <td width="129"  noWrap class="td"><input type="TEXT" name="mc" value="<%=row.get("mc")%>" style="WIDTH:110" maxlength="<%=ds.getColumn("mc").getPrecision()%>"  onKeyDown="return getNextElement();" <%=edClass%> <%=readonly%>></td>
      <td width="75"  noWrap class="tableTitle">类 型</td>
      <td width="254" class="td">
         <%String lx = row.get("lx");%>
        <pc:select name="lx" style="width:110" value="<%=lx%>">
        <pc:option value="1">字符型</pc:option> <pc:option value="2">文本型</pc:option>
        <pc:option value="4">数值型</pc:option> </pc:select></td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">长 度</td>
      <td noWrap class="td"><input type="TEXT" name="cd" value="<%=row.get("cd")%>" style="WIDTH:110" maxlength="<%=ds.getColumn("cd").getPrecision()%>"  onKeyDown="return getNextElement();" <%=edClass%> <%=readonly%>>
      </td>
      <td noWrap class="tableTitle">精 度</td>
      <td noWrap class="td"><input type="TEXT" name="jd" value="<%=row.get("jd")%>" style="WIDTH:110" maxlength="<%=ds.getColumn("jd").getPrecision()%>"  onKeyDown="return getNextElement();" <%=edClass%> <%=readonly%>></td>
    </tr>
    <tr>
      <td height="21" noWrap class="tableTitle">来 源</td>
      <td noWrap class="td">
      <%String ly = row.get("ly");%>
         <pc:select name="ly" style="width:110" value="<%=ly%>">
        <pc:option value="1">直接输入</pc:option> <pc:option value="2">公式计算</pc:option><pc:option value="3">取计件工资</pc:option>
        </pc:select></td>
      </td>
      <td noWrap class="tableTitle">排序号</td>
      <td noWrap class="td"><input type="TEXT" name="pxh" value="<%=row.get("pxh")%>" style="WIDTH:110" maxlength="<%=ds.getColumn("pxh").getPrecision()%>"  onKeyDown="return getNextElement();" <%=edClass%> <%=readonly%>></td>
    </tr>
     <tr>
      <td height="21" noWrap class="tableTitle">计算公式</td>
      <td noWrap colspan="3" class="td"><input type="TEXT" name="jsgs" value="<%=row.get("jsgs")%>" style="WIDTH:325" maxlength="<%=ds.getColumn("jsgs").getPrecision()%>" class="ednone" readonly>
      </td>
     </tr>
    <tr>
    <td colspan="4"  class="tableTitle">
        <%if(isCanAdd){String ss= "sumitForm("+Operate.POST+");";
         %>
         <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存(S) ">
         <pc:shortcut key="s" script='<%=ss%>'/>
        <%}%>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(C) ">
        <pc:shortcut key="c" script='parent.hideFrameNoFresh()'/>
    </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
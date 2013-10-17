<%--收发单据类别--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String pageCode = "wage_field_expression";
%>
<%
if(!loginBean.hasLimits("wage_field_expression", request, response))
    return;
  engine.erp.person.B_WageFieldEexpression b_WageFieldEexpressionBean  =  engine.erp.person.B_WageFieldEexpression.getInstance(request);
  String retu = b_WageFieldEexpressionBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<SCRIPT LANGUAGE="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR><TD colspan="6" align="center" NOWRAP>定义款项公式</TD></TR>
</TABLE>
<%
  EngineDataSet list = b_WageFieldEexpressionBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  boolean canedit = loginBean.hasLimits(pageCode,op_add)||loginBean.hasLimits(pageCode,op_edit);
  String edClass = canedit?"class=edbox":"class=edline";
  String readonly = canedit?"":"readonly";
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center"><tr><td>
<table class="editformbox" CELLSPACING=1 CELLPADDING=0  WIDTH="90%">
     <tr>
     <td colspan="2">
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr >
      <td class="tableTitle" nowarp width="20%">款项</td>
      <td class="tableTitle" nowarp width="80%">款项间公式关系</td>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
        if(list.getValue("ly").equals("2")){
    %>
    <tr >
      <td class="td" nowarp width="20%"><input type="text" name="mc" value="<%=list.getValue("mc")%>" style="width:100%"  onBlur="valueChange('<%=i%>')" class="ednone" readonly></td>
      <td class="td" nowarp width="80%"><input type="text" name="jsgs_<%=i%>" value="<%=list.getValue("jsgs")%>" style="width:100%"  onBlur="valueChange('<%=i%>')" <%=edClass%> <%=readonly%>></td>
    </tr>
    <%}list.next();}%>
  </table>
 </td>
 </tr>
      <%if(canedit){%>
      <tr>
        <td class="tableTitle" nowarp>运算符:</td>
        <td class="td"><input name="plus" type="button" value=" + " onClick="plusClick('plus')">&nbsp;<input name="subtract" type="button" value=" - " onClick="plusClick('subtract')">&nbsp;<input name="button3" type="button" value=" * " onClick="plusClick('*')">&nbsp;<input name="button4" type="button" value=" / " onClick="plusClick('chu')">&nbsp;<input name="button5" type="button" value=" ( " onClick="plusClick('left')">&nbsp;<input name="button6" type="button" value=" ) " onClick="plusClick('right')"></td>
      </tr>
      <tr>
        <td class="tableTitle">项目</td>
        <td>
        <%
         list.first();
         for(int j=0;j<list.getRowCount();j++)
         {
            String field=list.getValue("dyzdm");
         %>
             <input name="plus" style="width:150" type="button" value="<%=list.getValue("mc")+"("+field+")"%>" onClick="plusClick('<%=field%>')" >
         <%
         list.next();
         }
         %>
        </td>
      </tr>
      <%}%>
      <tr>
      <tr>
        <td class="tableTitle">&nbsp;</td><td class="tableTitle">&nbsp;</td>
      </tr>
      <tr>
      <td  noWrap class="tableTitle" colspan="2">
      <%
        if(loginBean.hasLimits(pageCode,op_add)||loginBean.hasLimits(pageCode,op_edit)){
          String ss= "sumitForm("+Operate.POST+");";
      %>
        <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存(S) ">
        <pc:shortcut key="s" script='<%=ss%>'/>
      <%
        }
        if(b_WageFieldEexpressionBean.retuUrl!=null)
        {
          String s = "location.href='"+b_WageFieldEexpressionBean.retuUrl+"'";
      %>
      <input name="button2222232" type="button" align="Right" class="button" onClick="location.href='<%=b_WageFieldEexpressionBean.retuUrl%>'" value=" 返回(C) "border="0">
      <pc:shortcut key="c" script="<%=s%>" />
      <%}%>
      </td>
      </tr>
</table>
      </td>
      </tr>
</table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<%out.print(retu);%>
<SCRIPT LANGUAGE="javascript">
var currentJsgsobj;
function valueChange(i)
{
     currentJsgsobj = document.all['jsgs_'+i];
}
function plusClick(name)
{
 if(currentJsgsobj==null)return;
  else if(name=="plus")
 currentJsgsobj.value=currentJsgsobj.value+"+";
  else if(name=="subtract")
 currentJsgsobj.value=currentJsgsobj.value+"-";
  else if(name=="*")
 currentJsgsobj.value=currentJsgsobj.value+"*";
  else if(name=="chu")
 currentJsgsobj.value=currentJsgsobj.value+"/";
  else if(name=="left")
 currentJsgsobj.value=currentJsgsobj.value+"(";
  else if(name=="right")
 currentJsgsobj.value=currentJsgsobj.value+")";
else
currentJsgsobj.value=currentJsgsobj.value+name;

}
</SCRIPT>
</body>
</html>
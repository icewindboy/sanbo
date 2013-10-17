<%--工人工资计算公式设置页面--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("wage_formula", request, response))
    return;
  engine.erp.produce.B_WageFormula wageFormulaBean = engine.erp.produce.B_WageFormula.getInstance(request);
  String pageCode = "wage_formula";
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
    <TD NOWRAP align="center">工人工资公式设置</TD>
  </tr></table>
<%String retu = wageFormulaBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  RowMap row = wageFormulaBean.getRowinfo();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3" align="center">
    <tr>
      <td noWrap class="tableTitle">日班=</td>
      <td noWrap class="td">出勤时间<INPUT TYPE="TEXT" NAME="rb" VALUE="<%=row.get("rb")%>" style="width:180" CLASS="edbox" onKeyDown="return getNextElement();" >
      </td>
    </tr>
    <tr>
    <td noWrap class="tableTitle">夜班=</td>
      <td noWrap class="td">出勤时间<INPUT TYPE="TEXT" NAME="yb" VALUE="<%=row.get("yb")%>" style="width:180" CLASS="edbox" onKeyDown="return getNextElement();" >
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">计件工时=</td>
      <td noWrap class="td">数量/定额数量*<INPUT TYPE="TEXT" NAME="jjgs" VALUE="<%=row.get("jjgs")%>" style="width:145" CLASS="edbox" onKeyDown="return getNextElement();" >
      </td>
    </tr>
    <tr>
    <td noWrap class="tdTitle">工资=</td>
    <td noWrap class="td"><input type="radio" name="gz" value="1"<%=row.get("gz").equals("1") ? " checked" : ""%> checked>计件工资
    <input type="radio" name="gz" value="0"<%=row.get("gz").equals("0") ? " checked" : ""%>>计时工资
    </td>
    </tr>
    <tr >
    <td colspan="2" noWrap class="tdTitle" align="center">警告:(日半夜班出勤时间格式只能用>或>=;例如“>=4,0.5;>8,1”)</td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
         <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
        <input name="button2" type="button" class="button" onClick="location.href='<%=wageFormulaBean.retuUrl%>'" value=" 关闭(X)">
         <% String back ="location.href='"+wageFormulaBean.retuUrl+"'" ;%>
         <pc:shortcut key="x" script='<%=back%>'/>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
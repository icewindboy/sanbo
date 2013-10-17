<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "prix_formula";
%>
<%
if(!loginBean.hasLimits("prix_formula", request, response))
    return;
  engine.erp.sale.dafa.B_PrixFormula b_PrixFormulaBean  =  engine.erp.sale.dafa.B_PrixFormula.getInstance(request);
  String retu = b_PrixFormulaBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  RowMap row = b_PrixFormulaBean.getKhdjRow();
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
  String edClass = isCanAdd?"class=edbox":"class=ednone";
  String readonly = isCanAdd?"":"readonly";
  engine.project.LookUp pruductKindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_KIND);//物资类别

  ArrayList opkey = new ArrayList(); opkey.add("A");opkey.add("B"); opkey.add("C");  opkey.add("D"); opkey.add("E");opkey.add("F");
  ArrayList opval = new ArrayList(); opval.add("一级客户");opval.add("二级客户"); opval.add("三级客户");  opval.add("现金客户"); opval.add("四级客户");opval.add("五级客户");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};
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
    <TD NOWRAP align="center">客户等级系数</TD>
  </tr></table>

<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;客户价值</td>
      <td class="td"  align="right">
        <pc:select name="xydj" addNull="0"    style="width:150"  >
        <%=b_PrixFormulaBean.listToOption(lists, opkey.indexOf(row.get("xydj")))%>
      </pc:select>
     </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;调整系数(%)</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="adjustxs" VALUE="<%=row.get("adjustxs")%>" SIZE="30" style="width:150" class="edFocused_r" >
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;回款期限</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="rtnlimit" VALUE="<%=row.get("rtnlimit")%>" SIZE="30" style="width:150" class="edFocused_r" >
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;资金占有费率(%)</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="fundxs" VALUE="<%=row.get("fundxs")%>" SIZE="30" style="width:150" class="edFocused_r" >
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <%if(true){
          String ss= "sumitForm("+b_PrixFormulaBean.KHDJ_POST+");";
         %>
         <input name="button" type="button" class="button" onClick="sumitForm(<%=b_PrixFormulaBean.KHDJ_POST%>);" value=" 保存(S) ">
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
<%--编码规则首页--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate"%>
<%!//String retuUrl = null;
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
%><%String pageCode = "coderule";
  if(!loginBean.hasLimits("coderule", request, response))
    return;
  engine.erp.system.CodeRule codeRule = engine.erp.system.CodeRule.getInstance(request);
  /*String operate = request.getParameter(Operate.OPERATE_KEY);
  if(operate!=null && operate.equals(String.valueOf(Operate.INIT)))
  {
    retuUrl = request.getParameter("src");
    retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
  }*/
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script LANGUAGE="javascript">
  function showDetail(type, row)
  {
    selectRow();
    lockScreenToWait("处理中, 请稍候！");
    if(type == 'product')
      parent.bottom.location.href='product_rule.jsp?operate=<%=codeRule.EDIT%>&type='+row;
    else
      parent.bottom.location.href='coderule_right.jsp?operate=<%=codeRule.EDIT%>&type='+row;
    unlockScreenWait();
  }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="4" align="center" NOWRAP>编码规则</TD>
  </TR>
</TABLE>
<%codeRule.doService(request, response);
  EngineDataSet list = codeRule.getCodeRuleList();//主表数据集
  String curUrl = request.getRequestURL().toString();%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>&nbsp;</td>
      <td nowrap>规则名称</td>
    </tr>
    <%
      int count = list.getRowCount();//行数
      int i=0;
      list.first();
      for(; i<count; i++){
    %>
    <tr onClick="showDetail('<%=list.getValue("coderule")%>','<%=list.getRow()%>')">
       <td class="td" width="20" align="center" nowrap><%=i+1%></td>
       <td class="td" nowrap>&nbsp;<%=list.getValue("rulename")%></td>
    </tr>
    <%  list.next();
      }
    %>
  </table>
      <%--TR>
     <TD WIDTH="10%" NOWRAP class="tdTitle" align="center">1</TD>
     <TD WIDTH="90%" NOWRAP CLASS=td align="center"><a href="product_rule.jsp?operate=<%=Operate.INIT%>&type=product">产品编码规则</a></TD>
    </TR>
    <TR>
    <TD WIDTH="10%" NOWRAP class="tdTitle" align="center">2</TD>
    <TD WIDTH="90%" NOWRAP CLASS=td align="center"><a href="coderulelist.jsp?operate=<%=Operate.INIT%>&type=xs_ht">销售合同编码规则</a></TD>
    </TR--%>
  <script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</body>
</html>

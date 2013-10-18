<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate ,java.util.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%
  engine.erp.jit.View_Scjh View_ScjhBean = engine.erp.jit.View_Scjh.getInstance(request);
  String pageCode = "sale_invoice";
  //if(!loginBean.hasLimits(pageCode, request, response))
  //return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String retu = View_ScjhBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = View_ScjhBean.getOneTable();

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

<script language="javascript">
function sumitForm(oper){
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">生产计划货物</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right">
    </td>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20></td>
      <td nowrap>生产计划号</td>
      <td nowrap>单号</td>
      <td nowrap>产品编码</td>
      <td nowrap>产品名称</td>
      <td nowrap>规格属性</td>
    </tr>
    <%
      int count = list.getRowCount();
      int i=0;
      list.first();
      for(; i<count; i++)   {
    %>
    <tr >
      <td class="td"> </td>
      <td nowrap  id="processdm_<%=i%>" class="td"><%=list.getValue("jhh")%></td>
      <td nowrap  id="cpbm_<%=i%>" class="td"><%=list.getValue("djh")%></td>
      <td nowrap  id="djh_<%=i%>" class="td"><%=list.getValue("cpbm")%></td>
      <td nowrap  id="product_<%=i%>" class="td"><%=list.getValue("product")%></td>
      <td nowrap align="center"  class="td" ><%=list.getValue("sxz")%></td>
    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
  </form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
</SCRIPT>
<%out.print(retu);%>
</body>
</html>
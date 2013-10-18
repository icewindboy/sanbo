<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.* ,engine.project.*"%>
<%
  //String pageCode = "lading_bill";
  //if(!loginBean.hasLimits(pageCode, request, response))
  //  return;
  engine.erp.sale.xixing.B_Sale_Output b_Sale_OutputBean = engine.erp.sale.xixing.B_Sale_Output.getInstance(request);
  String retu = b_Sale_OutputBean.doService(request, response);
  String curUrl = request.getRequestURL().toString();
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
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript">
  function backList()
  {
    location.href='../sale_xixing/lading_bill_top.jsp';
  }
function openwin(url)
{
  //parent.location.href="sale_product_capacity.jsp?operate=0&htid="+rownum;
  //paraStr = "sale_product_capacity.jsp?operate=0&htid="+rownum;
  openSelectUrl(url, "SingleCustSelector", winopt2);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD NOWRAP align="center">出库情况</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table id="tableviews" width="90%" border="0" cellspacing="1" cellpadding="1"  align="center">
  <tr class="tableTitle">
  <td nowrap align="left">单据号:<%=b_Sale_OutputBean.tdbh%></td>
  </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>出库单编号</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>数量</td>
      <td nowrap>批号</td>
    </tr>
    <%
      int iPage = loginBean.getPageSize();
      EngineDataSet dsHthwJhh = b_Sale_OutputBean.getJhhTable();
      int i=0;
      dsHthwJhh.first();
      for(; i < dsHthwJhh.getRowCount(); i++) {
        //String ul = "location.href='../store/outputlist_edit.jsp?operate=2000&code=produce_plan&id="+dsHthwJhh.getValue("sfdjid")+"'";
        String ul = "'../store/outputlist_edit.jsp?operate=2000&code=produce_plan&id="+dsHthwJhh.getValue("sfdjid")+"'";
    %>
    <tr  onclick="openwin(<%=ul%>)">
      <td class="td"><%=dsHthwJhh.getValue("sfdjdh")%></td>
      <td class="td"><%=dsHthwJhh.getValue("cpbm")%></td>
      <td class="td"><%=dsHthwJhh.getValue("product")%></td>
      <td class="td"><%=dsHthwJhh.getValue("sxz")%></td>
      <td class="td"><%=dsHthwJhh.getValue("sl")%></td>
      <td class="td"><%=dsHthwJhh.getValue("ph")%></td>
    </tr>
    <%
      dsHthwJhh.next();
      }
      for(; i < iPage; i++){
        out.print("<tr><td class='td' nowrap>&nbsp;</td><td class='td' nowrap></td>");
        out.print("<td class='td' nowrap></td><td class='td' nowrap></td>");
        out.print("<td class='td' nowrap></td>");
        out.print("<td class='td' nowrap></td></tr>");
      }
    %>
  </table>

  </form>
  <SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
 </SCRIPT>
</body>
</html>
<%--计件工作量--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%
  engine.erp.jit.PieceRate pieceRateBean = engine.erp.jit.PieceRate.getInstance(request);
  String pageCode = "piece_rate";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;

  String retu = pieceRateBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  //pieceRateBean.doService(request, response);
  synchronized(pieceRateBean){
  EngineDataSet list = pieceRateBean.getDetailTable();
    if(list.changesPending())
      return;
       //pieceRateBean.openDetailTable(false);
    HtmlTableProducer detailTable = pieceRateBean.detailProducer;
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
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <%detailTable.printTitle(pageContext, "height='20'");%>
    </tr>
    <%
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++){
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <%detailTable.printCells(pageContext, "class=td");%>
    </tr>
      <%list.next();
      }
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <%detailTable.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);
</script>
</form>
</BODY>
<%}%>
</Html>
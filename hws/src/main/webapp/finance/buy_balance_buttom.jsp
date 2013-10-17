<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "buy_balance";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.finance.B_BuyBalance b_BuyBalanceBean = engine.erp.finance.B_BuyBalance.getInstance(request);
  b_BuyBalanceBean.doService(request, response);
  synchronized(b_BuyBalanceBean){
    EngineDataSet list = b_BuyBalanceBean.getDetailTable();
    HtmlTableProducer detailProducer = b_BuyBalanceBean.detailProducer;//从表表格生成器
    engine.project.LookUp buyLadingBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_ORDER_STOCK);
    engine.project.LookUp productBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
    buyLadingBean.regData(list,"jhdhwid");
    productBean.regData(list,"cpid");
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
<form name="form1" action="" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <td nowrap>进货单编号</td>
      <td nowrap>进货单货物</td>
      <%detailProducer.printTitle(pageContext, "height='20'");%><%--打印从表表头--%>
    </tr>
    <%
      if(list.changesPending())
        b_BuyBalanceBean.openDetailTable(b_BuyBalanceBean.masterIsAdd());
      BigDecimal t_jsje = new BigDecimal(0);
      int count = list.getRowCount();
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)   {
        RowMap tdRow = buyLadingBean.getLookupRow(list.getValue("jhdhwid"));
        RowMap productRow = productBean.getLookupRow(list.getValue("cpid"));
        String jsje = list.getValue("jsje");
        if(b_BuyBalanceBean.isDouble(jsje))
          t_jsje = t_jsje.add(new BigDecimal(jsje));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td><%--行号--%>
      <td class="td" nowrap><%=tdRow.get("jhdbm")%></td>
      <td class="td" nowrap><%=productRow.get("product")%></td>
      <%detailProducer.printCells(pageContext, "class=td");%><%--打印从表表格内容--%>
    </tr>
      <%list.next();
      }
      i = count+1;
      %>
        <tr>
        <td class="tdTitle" nowrap>合计</td>
        <td class="td" nowrap>&nbsp;</td>
        <td class="td" nowrap></td>
        <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_jsje%>' readonly></td>
      </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%><%--打印空格--%>
    </tr>
    <%}
  }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
  <%
  String pageCode = "sale_order_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.sale.B_SaleOrder saleOrderBean = engine.erp.sale.B_SaleOrder.getInstance(request);
  synchronized(saleOrderBean){
  saleOrderBean.doService(request, response);

  EngineDataSet list = saleOrderBean.getDetailTable();
  HtmlTableProducer detailProducer = saleOrderBean.detailProducer;
  boolean isProductInvoke = saleOrderBean.isProductInvoke;//生产里调用

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
      <%detailProducer.printTitle(pageContext, "height='20'");%>
      <%if(isProductInvoke){%><td class="td" nowrap>已计划量</td><%}%>
    </tr>
    <%
      if(list.changesPending())
        saleOrderBean.openDetailTable(saleOrderBean.masterIsAdd());
      BigDecimal t_hssl = new BigDecimal(0);
      BigDecimal t_sl = new BigDecimal(0);
      BigDecimal t_skdsl = new BigDecimal(0);
      BigDecimal t_jje = new BigDecimal(0);
      BigDecimal t_wbje = new BigDecimal(0);

      int i=0;
      int count = list.getRowCount();
      list.first();
      for(; i<list.getRowCount(); i++)   {
        String hssl = list.getValue("hssl");
        if(saleOrderBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
        String sl = list.getValue("sl");
        if(saleOrderBean.isDouble(sl))
          t_sl = t_sl.add(new BigDecimal(sl));
        String skdsl = list.getValue("skdsl");
        if(saleOrderBean.isDouble(skdsl))
          t_skdsl = t_skdsl.add(new BigDecimal(skdsl));
        String jje = list.getValue("jje");
        if(saleOrderBean.isDouble(jje))
          t_jje = t_jje.add(new BigDecimal(jje));
        String wbje = list.getValue("wbje");
        if(saleOrderBean.isDouble(wbje))
          t_wbje = t_wbje.add(new BigDecimal(wbje));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <%detailProducer.printCells(pageContext, "class=td");%>
      <%if(isProductInvoke){%><td class="td" nowrap><%=list.getValue("yjhsl")%></td><%}%>
    </tr>
      <%list.next();
      }
      i=count+1;
      %>
      <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_skdsl%>' readonly></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_jje%>' readonly></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_wbje%>' readonly></td>
      <td class="td" nowrap></td><td class="td" nowrap></td>
      <%if(isProductInvoke){%><td class="td" nowrap></td><%}%>
      </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%>
      <%if(isProductInvoke){%><td class="td" nowrap></td><%}%>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
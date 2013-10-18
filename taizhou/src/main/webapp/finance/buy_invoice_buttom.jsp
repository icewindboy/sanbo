<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "buy_invoice";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.finance.B_BuyInvoice b_BuyInvoiceBean = engine.erp.finance.B_BuyInvoice.getInstance(request);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//引用产品
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性

synchronized(b_BuyInvoiceBean){
  b_BuyInvoiceBean.doService(request, response);
  EngineDataSet list = b_BuyInvoiceBean.getDetailTable();
  HtmlTableProducer detailProducer = b_BuyInvoiceBean.detailProducer;//从表表格生成器
  prodBean.regData(list,"cpid");
  propertyBean.regData(list,"dmsxid");
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
      <td nowrap height='20'>原辅料编号</td>
      <td nowrap height='20'>原辅料名称</td>
      <td nowrap height='20'>原辅料规格</td>
      <td nowrap>规格属性</td>
      <td nowrap height='20'>数量</td>
      <td nowrap height='20'>单位</td>
      <td nowrap height='20'>金额</td>
      <%--detailProducer.printTitle(pageContext, "height='20'");--%><%--打印从表表头--%>
    </tr>
    <%
      if(list.changesPending())
        b_BuyInvoiceBean.openDetailTable(b_BuyInvoiceBean.masterIsAdd());
      BigDecimal t_je = new BigDecimal(0);
      BigDecimal t_jshj = new BigDecimal(0);
      BigDecimal t_gs = new BigDecimal(0);
      int count = list.getRowCount();
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)   {
        String je = list.getValue("je");
        if(b_BuyInvoiceBean.isDouble(je))
          t_je = t_je.add(new BigDecimal(je));
        String jshj = list.getValue("jshj");
        if(b_BuyInvoiceBean.isDouble(jshj))
          t_jshj = t_jshj.add(new BigDecimal(jshj));
        String gs = list.getValue("gs");
        if(b_BuyInvoiceBean.isDouble(gs))
          t_gs = t_gs.add(new BigDecimal(gs));
        RowMap productRow = prodBean.getLookupRow(list.getValue("cpid"));
        RowMap propertyRow = propertyBean.getLookupRow(list.getValue("dmsxID"));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td><%--行号--%>
      <td align="left" nowrap class="td"><%=productRow.get("cpbm")%></td>
      <td align="left" nowrap class="td"><%=productRow.get("pm")%></td>
      <td align="left" nowrap class="td"><%=productRow.get("gg")%></td>
      <td align="left" nowrap class="td"><%=propertyRow.get("sxz")%></td>
      <td align="right" nowrap class="td"><%=list.getValue("sl")%></td>
      <td align="left" nowrap class="td"><%=productRow.get("jldw")%></td>
      <td align="right" nowrap class="td"><%=list.getValue("je")%></td>
      <%--detailProducer.printCells(pageContext, "class=td");--%><%--打印从表表格内容--%>
    </tr>
      <%list.next();
        }
        i=count+1;
        %>
        <tr>
        <td class="tdTitle" nowrap>合计</td>
        <td class="td" nowrap>&nbsp;</td>
        <td class="td" nowrap></td>
        <td align="right" class="td"></td>
        <td class="td" nowrap></td>
        <td class="td" nowrap></td>
        <td align="right" class="td"></td>
        <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_je%>' readonly></td>
      </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <%--detailProducer.printBlankCells(pageContext, "class=td");--%><%--打印空格--%>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
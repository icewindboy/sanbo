<%--采购进货单框架下方合同明细显示--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>

<%
  String pageCode = "check_buy_ordergoods";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.buy.B_CheckBuyOrderGoods checkBuyOrderGoodsBean = engine.erp.buy.B_CheckBuyOrderGoods.getInstance(request);
  checkBuyOrderGoodsBean.doService(request, response);
  EngineDataSet list = checkBuyOrderGoodsBean.getDetailTable();
  synchronized(list){
    if(list.changesPending())
      checkBuyOrderGoodsBean.openDetailTable(false);
    HtmlTableProducer detailProducer = checkBuyOrderGoodsBean.detailProducer;
  //设置打印td的监听器，用于打印产品编码和计量单位
  //detailProducer.setHtmlPrintCellListener(new DetailCellListener());
  //engine.project.LookUp salePriceBean = checkBuyOrderGoodsBean.getSalePriceBean(request);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp buyOrderBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_ORDER_GOODS);

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
      <td nowrap height='20'>所属订单</td>
      <td nowrap height='20'>产品编码</td>
      <td nowrap height='20'>品名 规格</td>
      <td nowrap height='20'>图号</td>
      <td nowrap height='20'>单位</td>
      <td nowrap height='20'>数量</td>
      <td nowrap height='20'>备注</td>
    </tr>
    <%
    prodBean.regData(list,"cpid");
    buyOrderBean.regData(list,"hthwid");
    list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
    	  RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));
    	  RowMap  buyOrderRow= buyOrderBean.getLookupRow(list.getValue("hthwid"));
    %>
    <tr>
      <td class="td" nowrap><%=i %></td>
       <td nowrap class=td><%=buyOrderRow.get("htbh")%></td>
      <td nowrap class=td><%=prodRow.get("cpbm")%></td>
      <td nowrap class=td><%=prodRow.get("product")%></td>
      <td nowrap class=td><%=prodRow.get("th")%></td>
      <td nowrap class=td><%=prodRow.get("jldw")%></td>
      <td nowrap class=td><%=list.getValue("sl")%></td>
      <td nowrap class=td><%=list.getValue("bz")%></td>
    </tr>    
      <%list.next();
      }
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
    </tr>
    <%}
  }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
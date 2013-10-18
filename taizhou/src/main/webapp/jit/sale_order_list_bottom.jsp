<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
  <%
  String pageCode = "sale_order";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.jit.B_SaleOrderLlist saleOrderListBean = engine.erp.jit.B_SaleOrderLlist.getInstance(request);
  synchronized(saleOrderListBean){
  saleOrderListBean.doService(request, response);

  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp salePriceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_PRICE);
  EngineDataSet list = saleOrderListBean.getDetailTable();
  HtmlTableProducer detailProducer = saleOrderListBean.detailProducer;
  boolean isProductInvoke = true;//生产里调用
  propertyBean.regData(list,"dmsxid");
  salePriceBean.regData(list,"wzdjid");

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
      <td nowrap height='20'>产品编码</td>
      <td nowrap height='20'>品名规格（花号/款式）</td>
      <td nowrap height='20'>数量</td>
      <td nowrap height='20'>单位</td>
      <td nowrap height='20'>销售价</td>
      <td nowrap height='20'>折扣(%)</td>
      <td nowrap height='20'>单价</td>
      <td nowrap height='20'>金额</td>
      <%--detailProducer.printTitle(pageContext, "height='20'");--%>
      <%if(isProductInvoke){%><td class="td" nowrap>已计划量</td><%}%>
    </tr>
    <%
      if(list.changesPending())
        saleOrderListBean.openDetailTable(saleOrderListBean.masterIsAdd());
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
        if(saleOrderListBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
        String sl = list.getValue("sl");
        if(saleOrderListBean.isDouble(sl))
          t_sl = t_sl.add(new BigDecimal(sl));
        String skdsl = list.getValue("skdsl");
        if(saleOrderListBean.isDouble(skdsl))
          t_skdsl = t_skdsl.add(new BigDecimal(skdsl));
        String jje = list.getValue("jje");
        if(saleOrderListBean.isDouble(jje))
          t_jje = t_jje.add(new BigDecimal(jje));
        String wbje = list.getValue("wbje");
        if(saleOrderListBean.isDouble(wbje))
          t_wbje = t_wbje.add(new BigDecimal(wbje));
        RowMap priceRow = salePriceBean.getLookupRow(list.getValue("wzdjid"));
        RowMap propertyRow = propertyBean.getLookupRow(list.getValue("dmsxID"));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <td align="left" nowrap class="td"><%=priceRow.get("cpbm")%></td>
      <td align="left" nowrap class="td"><%=priceRow.get("product")%></td>
      <td align="right" nowrap class="td"><%=list.getValue("sl")%></td>
      <td align="left" nowrap class="td"><%=priceRow.get("jldw")%></td>
      <td align="right" nowrap class="td"><%=list.getValue("xsj")%></td>
      <td align="right" nowrap class="td"><%=list.getValue("zk")%></td>
      <td align="right" nowrap class="td"><%=list.getValue("dj")%></td>
      <td align="right" nowrap class="td"><%=list.getValue("jje")%></td>
      <%--detailProducer.printCells(pageContext, "class=td");--%>
      <%if(isProductInvoke){%><td class="td"  align="right"  nowrap><%=list.getValue("yjhsl")%></td><%}%>
    </tr>
      <%list.next();
      }
      i=count+1;
      %>
      <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_jje%>' readonly></td>
      <%if(isProductInvoke){%><td class="td" nowrap></td><%}%>
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
      <td class="td" nowrap>&nbsp;</td>
      <%--detailProducer.printBlankCells(pageContext, "class=td");--%>
      <%if(isProductInvoke){%><td class="td" nowrap></td><%}%>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
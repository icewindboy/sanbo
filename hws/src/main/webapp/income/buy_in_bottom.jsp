<%--采购进货单框架下方合同明细显示--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%><%!
  /*class DetailCellListener implements HtmlPrintCellListener
  {
    public void printCell(JspWriter out, HtmlPrintCellResponse reponse, DataSet ds) throws Exception
    {
      if(!reponse.getField().getFieldcode().equals("CPID"))
        return;
      StringBuffer header = new StringBuffer();
      switch(reponse.getPrintType())
      {
        case HtmlPrintCellResponse.PRINT_TITLE:
          header.append("<td class=td nowrap>产品编码</td>").append(reponse.getCellHeader());
          reponse.getCellContent().append("</td><td class=td nowrap>计量单位");
          reponse.getCellContent().append("</td><td class=td nowrap>换算单位");
          break;
        case HtmlPrintCellResponse.PRINT_BODY:
          RowMap row = reponse.getField().getLookUp().getLookupRow(ds.getBigDecimal("cpid").toString());
          header.append("<td class=td nowrap>").append(row.get("cpbm")).append("</td>").append(reponse.getCellHeader());
          reponse.getCellContent().append("</td><td class=td nowrap>").append(row.get("jldw"));
          reponse.getCellContent().append("</td><td class=td nowrap>").append(row.get("hsdw"));
          break;
        case HtmlPrintCellResponse.RRINT_BLANK:
          header.append("<td class=td nowrap>&nbsp;</td>").append(reponse.getCellHeader());
          reponse.getCellContent().append("</td><td class=td nowrap>&nbsp;");
          reponse.getCellContent().append("</td><td class=td nowrap>&nbsp;");
          break;
      }
      reponse.setCellHeader(header);
    }
  }*/
     %><%
       String pageCode = "income_in";
     if(!loginBean.hasLimits(pageCode, request, response))
       return;
     engine.erp.income.InBuy_In inbuyInBean = engine.erp.income.InBuy_In.getInstance(request);
     engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
     engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
     engine.project.LookUp buyOrderBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_ORDER_GOODS);

     inbuyInBean.doService(request, response);
     EngineDataSet list = inbuyInBean.getDetailTable();
     prodBean.regData(list,"cpid");
     propertyBean.regData(list, "dmsxid");
     synchronized(list){
       buyOrderBean.regData(list,"hthwid");
       if(list.changesPending())
         inbuyInBean.openDetailTable(false);
       HtmlTableProducer detailProducer = inbuyInBean.detailProducer;
       //设置打印td的监听器，用于打印产品编码和计量单位
       //detailProducer.setHtmlPrintCellListener(new DetailCellListener());
       //engine.project.LookUp salePriceBean = inbuyInBean.getSalePriceBean(request);
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
      <td nowrap height='20'>订单号</td>
      <td nowrap height='20'>产品编码</td>
      <td nowrap height='20'>品名规格</td>
      <td nowrap height='20'>规格属性</td>
      <td nowrap height='20'>数量</td>
       <td nowrap>单位</td>
       <td nowrap height='20'>单价</td>
       <td nowrap height='20'>金额</td>
       <td nowrap>实际入库量</td>
       <td nowrap height='20'>交货日期</td>
       <td nowrap height='20'>供应商料号</td>
       <td nowrap height='20'>备注</td>

     <%-- <%detailProducer.printTitle(pageContext, "height='20'");%>--%>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
      RowMap  buyOrderRow= buyOrderBean.getLookupRow(list.getValue("hthwid"));
      RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));
    %>
    <tr>
    <td class="td" nowrap><%=i+1%></td>
          <td class="td" nowrap><%=buyOrderRow.get("htbh")%></td>
     <%--<%detailProducer.printCells(pageContext, "class=td");%>--%>
          <td class="td" nowrap height='20'><%=prodRow.get("cpbm")%></td>
          <td class="td" nowrap height='20'><%=prodRow.get("product")%></td>
          <td class="td" nowrap><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
   <%-- <td nowrap height='20'><%=list.getValue("hssl")%></td>--%>
          <td class="td" nowrap height='20'><%=list.getValue("sl")%></td>
          <td class="td" nowrap><%=prodRow.get("jldw")%></td>
          <td class="td" nowrap height='20'><%=list.getValue("dj")%></td>
          <td class="td" nowrap height='20'><%=list.getValue("je")%> </td>
          <td class="td" nowrap><%=list.getValue("sjrkl")%></td>
          <td class="td" nowrap height='20'><%=list.getValue("jhrq")%> </td>
          <td class="td" nowrap height='20'><%=list.getValue("gyszyh")%> </td>
          <td class="td" nowrap height='20'><%=list.getValue("bz")%> </td>

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
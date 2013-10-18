<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "freight_account";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.finance.xixing.B_SaleFreightAccount b_SaleFreightAccountBean = engine.erp.finance.xixing.B_SaleFreightAccount.getInstance(request);
  synchronized(b_SaleFreightAccountBean){
  b_SaleFreightAccountBean.doService(request, response);

    EngineDataSet list = b_SaleFreightAccountBean.getDetailTable();

    engine.project.LookUp saleLadingBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_LADING_BILL);
    engine.project.LookUp productBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
    engine.project.LookUp saleCarryBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_CARRY_THING);//b_SaleFreightAccountBean.getBuyPriceBean(request);
    saleCarryBean.regData(list,"tdcyqkid");
    //saleLadingBean.regData(list,"tdcyqkID");
    //productBean.regData(list,"cpid");
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
      <td nowrap>销售单据号</td>
      <td nowrap>起始地</td>
      <td nowrap>目的地</td>
      <td nowrap>运费</td>
      <td nowrap>核销金额</td>
    </tr>
    <%
      if(list.changesPending())
        b_SaleFreightAccountBean.openDetailTable(b_SaleFreightAccountBean.masterIsAdd());
      BigDecimal t_jsje = new BigDecimal(0);
      BigDecimal t_tcj = new BigDecimal(0);
      int count = list.getRowCount();
      RowMap cyqkRow = null;
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)   {
        String tdcyqkid= list.getValue("tdcyqkid");
        cyqkRow = saleCarryBean.getLookupRow(tdcyqkid);
        String jsje = list.getValue("jsje");
        if(b_SaleFreightAccountBean.isDouble(jsje))
          t_jsje = t_jsje.add(new BigDecimal(jsje));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td><%--行号--%>
      <td class="td" nowrap><%=cyqkRow.get("tdbh")%></td>
      <td class="td" nowrap><%=cyqkRow.get("qsd")%></td>
      <td class="td" nowrap><%=cyqkRow.get("mdd")%></td>
      <td class="td" nowrap><%=cyqkRow.get("fy")%></td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=jsje%>' readonly></td>

    </tr>
      <%list.next();
      }
      i = count+1;
      %>
        <tr>
        <td class="tdTitle" nowrap>合计</td>
        <td class="td" nowrap>&nbsp;</td>
        <td class="td" nowrap></td>
        <td align="right" class="td"></td>
        <td align="right" class="td"></td>
        <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_jsje%>' readonly></td>
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
    </tr>
    <%}
  }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
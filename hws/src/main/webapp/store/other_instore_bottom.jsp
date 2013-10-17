<%
/**
 * <p>Title:库存管理——其它入库单列表明细表格页面</p>
 * <p>Description: 库存管理——其它入库单列表明细表格页面.</p>
 * <p>             此页面被放到了other_instore_list.jsp主框架网页的下方框架内.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 杨建国
 * @version 1.0
 */
%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "other_instore_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.B_OtherInStore otherInStoreBean = engine.erp.store.B_OtherInStore.getInstance(request);
  otherInStoreBean.doService(request, response);
  EngineDataSet list = otherInStoreBean.getDetailTable();
  HtmlTableProducer detailProducer = otherInStoreBean.detailProducer;
  synchronized(list){
    if(list.changesPending())
      otherInStoreBean.openDetailTable(false);
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
<form name="form1" action="" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <%detailProducer.printTitle(pageContext, "height='20'");%>
    </tr>
    <%
      BigDecimal t_zsl = new BigDecimal(0);
       BigDecimal t_hssl = new BigDecimal(0);
       String sl = "0";
      String hssl = "0";
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++) {
        sl = list.getValue("sl");
      hssl = list.getValue("hssl");
      if(otherInStoreBean.isDouble(sl))
        t_zsl = t_zsl.add(new BigDecimal(sl));
      if(otherInStoreBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <%detailProducer.printCells(pageContext, "class=td");%>
    </tr>
      <%list.next();
      }
           %>
      <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
     <td class=td>&nbsp;</td>
     <td class=td>&nbsp;</td>
     <td class=td align=right><%=t_zsl%></td>
      <td class=td align=right><%=t_hssl%></td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td >&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td >&nbsp;</td>
    </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
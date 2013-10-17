<%@ page contentType="text/html; charset=UTF-8" %>
<%request.setCharacterEncoding("UTF-8");
  //session.setMaxInactiveInterval(900);
  response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","max-age=0");
  response.addHeader("Cache-Control","pre-check=0");
  response.addHeader("Cache-Control","post-check=0");
  response.setDateHeader("Expires", 0);
  engine.common.OtherLoginBean loginBean = engine.common.OtherLoginBean.getInstance(request);
  if(!loginBean.isLogin(request, response))
    return;
%><%@ taglib uri="/WEB-INF/pagescontrol.tld" prefix="pc"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
  <%

  engine.erp.sale.B_OtherSaleOrder saleOrderBean = engine.erp.sale.B_OtherSaleOrder.getInstance(request);
  synchronized(saleOrderBean){
  saleOrderBean.doService(request, response);

  EngineDataSet list = saleOrderBean.getDetailTable();

  boolean isProductInvoke = saleOrderBean.isProductInvoke;//生产里调用
  engine.project.LookUp salePriceBean = engine.project.OtherLookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_PRICE);
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/scripts/public.css" type="text/css">
</head>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/validate.js"></script>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/rowcontrol.js"></script>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/tabcontrol.js"></script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <td nowrap height='20'>产品编码</td>
      <td nowrap height='20'>品名</td>
      <td nowrap height='20'>规格</td>
      <td nowrap height='20'>单位</td>
      <td nowrap height='20'>换算数量</td>
      <td nowrap height='20'>数量</td>
      <td nowrap height='20'>单价</td>
      <td nowrap height='20'>金额</td>
      <td nowrap height='20'>交货日期</td>
      <td nowrap height='20'>备注</td>
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
    	RowMap productRow = salePriceBean.getLookupRow(list.getValue("wzdjid"));
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
      <td class="td" nowrap height='20'><%=productRow.get("cpbm") %></td>
      <td class="td" nowrap height='20'><%=productRow.get("pm") %></td>
      <td class="td" nowrap height='20'><%=productRow.get("gg") %></td>
      <td class="td" nowrap height='20'><%=productRow.get("jldw") %></td>
      <td class="td" nowrap height='20' align="right" ><%= list.getValue("hssl") %></td>
      <td class="td" nowrap height='20' align="right" ><%= list.getValue("sl") %></td>
      <td class="td" nowrap height='20' align="right" ><%= list.getValue("dj") %></td>
      <td class="td" nowrap height='20' align="right" ><%= list.getValue("jje") %></td>
      <td class="td" nowrap height='20'><%= list.getValue("jhrq") %></td>
      <td class="td" nowrap height='20'><%= list.getValue("bz") %></td>
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

      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_jje%>' readonly></td>
      <td class="td" nowrap></td>

      <td class="td" nowrap></td>
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
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>      
 
      <%if(isProductInvoke){%><td class="td" nowrap></td><%}%>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
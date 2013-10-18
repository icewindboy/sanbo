<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "send_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.sale.xixing.B_SendBill b_SendBillBean = engine.erp.sale.xixing.B_SendBill.getInstance(request);
  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_ORDER_GOODS);//销售合同货物
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//引用产品
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  synchronized(b_SendBillBean){
    b_SendBillBean.doService(request, response);
    EngineDataSet list = b_SendBillBean.getDetailTable();
    HtmlTableProducer detailProducer = b_SendBillBean.detailProducer;//从表表格生成器
    list.first();
    saleOrderBean.regData(list,"hthwid");
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
<script language="javascript" >
function openrelationwin(id)
{
  paraStr =  "sale_tdhw_outputlist.jsp?operate=0&id="+id;
  openSelectUrl(paraStr, "SingleCustSelector", winopt2);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10><%--if(!tdid.equals("")){%><INPUT class="button" onClick="openrelationwin(<%=tdid%>)" type="button" value="出库情况" name="button2" onKeyDown="return getNextElement();"><%}--%></td>
      <td  nowrap>订单号</td>
      <td  nowrap>产品代码</td>
      <td  nowrap>品名 规格</td>
      <td nowrap>数量</td>
      <td nowrap>单位</td>
     <%-- <td nowrap>换算数量</td>
      <td nowrap>换算单位</td>--%>
      <td nowrap>折扣(%)</td>
      <td nowrap>单价</td>
      <td nowrap>金额</td>
      <td nowrap>备注</td>
      <td nowrap>未出库数量</td>
      <td nowrap>出库数量</td>
      <%--detailProducer.printTitle(pageContext, "height='20'");--%><%--打印从表表头--%>
    </tr>
    <%
      if(list.changesPending())
        b_SendBillBean.openDetailTable(b_SendBillBean.masterIsAdd());
      BigDecimal t_hssl = new BigDecimal(0);
      BigDecimal t_sl = new BigDecimal(0);
      BigDecimal t_xsje = new BigDecimal(0);
      BigDecimal t_jje = new BigDecimal(0);
      double t_wcksl=0.0 ;
      BigDecimal t_sthssl = new BigDecimal(0);
      BigDecimal t_stsl = new BigDecimal(0);
      int count=list.getRowCount();
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)   {
        String hssl = list.getValue("hssl");
        if(b_SendBillBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
        String sl = list.getValue("sl");
        if(b_SendBillBean.isDouble(sl))
          t_sl = t_sl.add(new BigDecimal(sl));
        String xsje = list.getValue("xsje");
        if(b_SendBillBean.isDouble(xsje))
          t_xsje = t_xsje.add(new BigDecimal(xsje));
        String jje = list.getValue("jje");
        if(b_SendBillBean.isDouble(jje))
          t_jje = t_jje.add(new BigDecimal(jje));
        String sthssl = list.getValue("sthssl");
        if(b_SendBillBean.isDouble(sthssl))
          t_sthssl = t_sthssl.add(new BigDecimal(sthssl));
        String stsl = list.getValue("stsl");
        if(b_SendBillBean.isDouble(stsl))
          t_stsl = t_stsl.add(new BigDecimal(stsl));
        double wcksl = Double.parseDouble(sl)-Double.parseDouble(stsl.equals("")?"0":stsl);
          t_wcksl = t_wcksl+wcksl;
        RowMap  saleOrderRow= saleOrderBean.getLookupRow(list.getValue("hthwid"));
        RowMap productRow = prodBean.getLookupRow(list.getValue("cpid"));
        RowMap propertyRow = propertyBean.getLookupRow(list.getValue("dmsxID"));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td><%--行号--%>
      <td align="left" nowrap class="td"><%=saleOrderRow.get("htbh")%></td>
      <td align="left" nowrap class="td"><%=productRow.get("cpbm")%></td>
      <td align="left" nowrap class="td"><%=productRow.get("product")%></td>

      <td align="right" nowrap class="td"><%=list.getValue("sl")%></td>
      <td align="left" nowrap class="td"><%=productRow.get("jldw")%></td>
    <%--  <td align="right" nowrap class="td"><%=list.getValue("hssl")%></td>
      <td align="left" nowrap class="td"><%=productRow.get("hsdw")%></td>--%>
      <td align="right" nowrap  class="td"><%=list.getValue("zk")%></td>
      <td align="right" nowrap class="td"><%=list.getValue("dj")%></td>
      <td align="right" nowrap class="td"><%=list.getValue("jje")%></td>
      <td align="left" nowrap class="td"><%=list.getValue("bz")%></td>
      <td align="right" class="td"  nowrap><%=wcksl%></td>
     <td align="right" class="td" nowrap><%=list.getValue("stsl")%></td>
      <%--detailProducer.printCells(pageContext, "class=td");--%><%--打印从表表格内容--%>
    </tr>
      <%list.next();
      }
      %>
      <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class="td" nowrap>&nbsp;</td>
      <td align="right" class="td"></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly> </td>
      <td class="td" nowrap></td>
     <%-- <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly> </td>
      <td align="right" class="td"></td>--%>
      <td align="right" class="td"></td>
      <td align="right" class="td"></td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_jje%>' readonly></td>
      <td align="right" class="td"></td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_wcksl%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_stsl%>' readonly></td>

      </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <%--detailProducer.printBlankCells(pageContext, "class=td");--%><%--打印空格--%>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
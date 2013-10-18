<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "directsell";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.sale.xixing.B_DirectSell b_DirectSellBean = engine.erp.sale.xixing.B_DirectSell.getInstance(request);
  synchronized(b_DirectSellBean){
    b_DirectSellBean.doService(request, response);
    EngineDataSet list = b_DirectSellBean.getDetailTable();
    HtmlTableProducer detailProducer = b_DirectSellBean.detailProducer;//从表表格生成器
    list.first();
    String tdid = list.getValue("tdid");
    engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//引用产品
    engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性

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
  //parent.location.href="sale_tdhw_outputlist.jsp?operate=0&id="+id;
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10><%--if(!tdid.equals("")){%><INPUT class="button" onClick="openrelationwin(<%=tdid%>)" type="button" value="出库情况" name="button2" onKeyDown="return getNextElement();"><%}--%></td>
      <td  nowrap>产品代码</td>
      <td  nowrap>品名规格</td>
      <td nowrap>数量</td>
      <td nowrap>单位</td>
      <td wnowrap>零售价</td>
      <td wnowrap>折扣(%)</td>
      <td wnowrap>销售价</td>
      <td nowrap>金额</td>
      <td nowrap>备注</td>
      <%--detailProducer.printTitle(pageContext, "height='20'");--%><%--打印从表表头--%>
    </tr>
    <%
      if(list.changesPending())
        b_DirectSellBean.openDetailTable(b_DirectSellBean.masterIsAdd());

      BigDecimal t_hssl = new BigDecimal(0);
      BigDecimal t_sl = new BigDecimal(0);
      BigDecimal t_xsje = new BigDecimal(0);
      BigDecimal t_jje = new BigDecimal(0);
      BigDecimal t_sthssl = new BigDecimal(0);
      BigDecimal t_stsl = new BigDecimal(0);

      int count=list.getRowCount();
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)   {
        RowMap productRow = prodBean.getLookupRow(list.getValue("cpid"));
        String hssl = list.getValue("hssl");
        if(b_DirectSellBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
        String sl = list.getValue("sl");
        if(b_DirectSellBean.isDouble(sl))
          t_sl = t_sl.add(new BigDecimal(sl));
        String xsje = list.getValue("xsje");
        if(b_DirectSellBean.isDouble(xsje))
          t_xsje = t_xsje.add(new BigDecimal(xsje));
        String jje = list.getValue("jje");
        if(b_DirectSellBean.isDouble(jje))
          t_jje = t_jje.add(new BigDecimal(jje));
        String sthssl = list.getValue("sthssl");
        if(b_DirectSellBean.isDouble(sthssl))
          t_sthssl = t_sthssl.add(new BigDecimal(sthssl));
        String stsl = list.getValue("stsl");
        if(b_DirectSellBean.isDouble(stsl))
          t_stsl = t_stsl.add(new BigDecimal(stsl));
        double wcksl = Double.parseDouble(sl)-Double.parseDouble(stsl.equals("")?"0":stsl);
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td><%--行号--%>

      <td class="td" nowrap><%=productRow.get("cpbm")%></td>
      <td class="td" nowrap><%=productRow.get("product")%></td>
      <td class="td" nowrap><%=list.getValue("sl")%></td>
      <td class="td" nowrap><%=productRow.get("jldw")%></td>
      <td class="td" nowrap><%=list.getValue("xsj")%></td>
      <td class="td" nowrap><%=list.getValue("zk")%></td>
      <td class="td" nowrap><%=list.getValue("dj")%></td>
      <td class="td" nowrap><%=list.getValue("jje")%></td>
      <td class="td" nowrap><%=list.getValue("bz")%></td>
      <%--detailProducer.printCells(pageContext, "class=td");--%><%--打印从表表格内容--%>
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
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
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
      <%--detailProducer.printBlankCells(pageContext, "class=td");--%><%--打印空格--%>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
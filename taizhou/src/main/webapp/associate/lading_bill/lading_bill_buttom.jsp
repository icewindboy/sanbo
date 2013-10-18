<%@page contentType="text/html; charset=UTF-8" %>
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
  String pageCode = "lading_bill";

  engine.erp.sale.dafa.OtherLadingBill b_ladingbillBean = engine.erp.sale.dafa.OtherLadingBill.getInstance(request);
  synchronized(b_ladingbillBean){
    b_ladingbillBean.doService(request, response);
    EngineDataSet list = b_ladingbillBean.getDetailTable();
    HtmlTableProducer detailProducer = b_ladingbillBean.detailProducer;//从表表格生成器
    list.first();
    String tdid = list.getValue("tdid");
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
      <td nowrap width=10></td>
      <%detailProducer.printTitle(pageContext, "height='20'");%><%--打印从表表头--%>
    </tr>
    <%
      if(list.changesPending())
        b_ladingbillBean.openDetailTable(b_ladingbillBean.masterIsAdd());

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
        String hssl = list.getValue("hssl");
        if(b_ladingbillBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
        String sl = list.getValue("sl");
        if(b_ladingbillBean.isDouble(sl))
          t_sl = t_sl.add(new BigDecimal(sl));
        String xsje = list.getValue("xsje");
        if(b_ladingbillBean.isDouble(xsje))
          t_xsje = t_xsje.add(new BigDecimal(xsje));
        String jje = list.getValue("jje");
        if(b_ladingbillBean.isDouble(jje))
          t_jje = t_jje.add(new BigDecimal(jje));
        String sthssl = list.getValue("sthssl");
        if(b_ladingbillBean.isDouble(sthssl))
          t_sthssl = t_sthssl.add(new BigDecimal(sthssl));
        String stsl = list.getValue("stsl");
        if(b_ladingbillBean.isDouble(stsl))
          t_stsl = t_stsl.add(new BigDecimal(stsl));
        double wcksl = Double.parseDouble(sl)-Double.parseDouble(stsl.equals("")?"0":stsl);
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td><%--行号--%>
      <%detailProducer.printCells(pageContext, "class=td");%><%--打印从表表格内容--%>
    </tr>
      <%list.next();
      }
      %>
      <tr>
      <td class="tdTitle" nowrap>合计</td>

      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
      <td align="right" class="td"></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_jje%>' readonly></td>
      <td align="right" class="td"></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_sthssl%>' readonly></td>
      </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>

      <td class="td" nowrap>&nbsp;</td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%><%--打印空格--%>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
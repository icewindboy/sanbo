<%--生产任务单框架下方合同明细显示--%>
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
          //header.append("<td class=td nowrap>申请单号</td>").append(reponse.getCellHeader());
          header.append("<td class=td nowrap>产品编码</td>").append(reponse.getCellHeader());
          reponse.getCellContent().append("</td><td class=td nowrap>单位");
          break;
        case HtmlPrintCellResponse.PRINT_BODY:
          RowMap row = reponse.getField().getLookUp().getLookupRow(ds.getBigDecimal("cpid").toString());
          //header.append("<td class=td nowrap>").append(row.get("cpbm")).append("</td>").append(reponse.getCellHeader());
          header.append("<td class=td nowrap>").append(row.get("cpbm")).append("</td>").append(reponse.getCellHeader());
          reponse.getCellContent().append("</td><td class=td nowrap>").append(row.get("jldw"));
          break;
        case HtmlPrintCellResponse.RRINT_BLANK:
          //header.append("<td class=td nowrap>&nbsp;</td>").append(reponse.getCellHeader());
          header.append("<td class=td nowrap>&nbsp;</td>").append(reponse.getCellHeader());
          reponse.getCellContent().append("</td><td class=td nowrap>&nbsp;");
          break;
      }
      reponse.setCellHeader(header);
    }
  }*/
%><%
  String pageCode = "produce_process";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.produce.B_ProduceProcess produceProcessBean = engine.erp.produce.B_ProduceProcess.getInstance(request);
  produceProcessBean.doService(request, response);
  EngineDataSet list = produceProcessBean.getDetailTable();
  //System.out.println("RowCount:"+list.getRowCount());
  synchronized(list){
    if(list.changesPending())
      produceProcessBean.openDetailTable(false);
    //System.out.println("RowCount:"+list.getRowCount());
    HtmlTableProducer detailProducer = produceProcessBean.detailProducer;
    //设置打印td的监听器，用于打印产品编码和计量单位
    //detailProducer.setHtmlPrintCellListener(new DetailCellListener());
    //engine.project.LookUp salePriceBean = produceProcessBean.getSalePriceBean(request);
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
      <%detailProducer.printTitle(pageContext, "height='20'");%>
    </tr>
    <%BigDecimal t_zsl = new BigDecimal(0), t_hssl = new BigDecimal(0), t_scsl=new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<list.getRowCount(); i++) {
        list.goToRow(i);
        String sl = list.getValue("sl");
        if(produceProcessBean.isDouble(sl))
          t_zsl = t_zsl.add(new BigDecimal(sl));
        String scsl = list.getValue("scsl");
        if(produceProcessBean.isDouble(scsl))
          t_scsl = t_scsl.add(new BigDecimal(scsl));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <%detailProducer.printCells(pageContext, "class=td");%>
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
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
       </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}
  }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
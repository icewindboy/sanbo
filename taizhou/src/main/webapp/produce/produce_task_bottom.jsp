<%--生产任务框架下方任务明细显示--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
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
  String pageCode = "produce_task";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.produce.B_ProduceTask produceTaskBean = engine.erp.produce.B_ProduceTask.getInstance(request);
  produceTaskBean.doService(request, response);
  EngineDataSet list = produceTaskBean.getDetailTable();
  //System.out.println("RowCount:"+list.getRowCount());
  synchronized(list){
    if(list.changesPending())
      produceTaskBean.openDetailTable(false);
    //System.out.println("RowCount:"+list.getRowCount());
    HtmlTableProducer detailProducer = produceTaskBean.detailProducer;
    //设置打印td的监听器，用于打印产品编码和计量单位
  //detailProducer.setHtmlPrintCellListener(new DetailCellListener());
    //engine.project.LookUp salePriceBean = produceTaskBean.getSalePriceBean(request);
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
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <%detailProducer.printCells(pageContext, "class=td");%>
    </tr>
      <%list.next();
      }
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
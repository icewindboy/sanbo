<%--生产计划框架下方合同明细显示--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%><%!
  class DetailCellListener implements HtmlPrintCellListener
  {
    public void printCell(JspWriter out, HtmlPrintCellResponse reponse, DataSet ds) throws Exception
    {
      return;
      /*if(!reponse.getField().getFieldcode().equals("CPID"))
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
      reponse.setCellHeader(header);*/
    }
  }
%><%
  String pageCode = "produce_plan";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.produce.B_ProducePlan producePlanBean = engine.erp.produce.B_ProducePlan.getInstance(request);
  engine.erp.produce.PlanSelectSale planSelectSaleBean = engine.erp.produce.PlanSelectSale.getInstance(request);
  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_SALE_ORDER_GOODS);//根据销售合同货物id得到合同编号
  producePlanBean.doService(request, response);
  RowMap  masterRow= producePlanBean.getMasterRowinfo();
  EngineDataSet ds = producePlanBean.getMaterTable();
  String zt = ds.getValue("zt");
  EngineDataSet list = producePlanBean.getDetailTable();
  //RowMap[] detailRows= producePlanBean.getDetailRowinfos();
  //System.out.println("RowCount:"+list.getRowCount());
  synchronized(list){
    if(list.changesPending())
      producePlanBean.openDetailTable(false);
    //System.out.println("RowCount:"+list.getRowCount());
    HtmlTableProducer detailProducer = producePlanBean.detailProducer;
    //设置打印td的监听器，用于打印产品编码和计量单位
    //detailProducer.setHtmlPrintCellListener(new DetailCellListener());
    //engine.project.LookUp salePriceBean = producePlanBean.getSalePriceBean(request);
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
      <td nowrap width=20></td>
      <%detailProducer.printTitle(pageContext, "height='20'");%>
    </tr>
    <%saleOrderBean.regData(list,"hthwid");
      list.first();
      int i=0;
      //RowMap detail =null;
      boolean isSaleOrder = true;
      for(; i<list.getRowCount(); i++)   {
        list.goToRow(i);
        String hthwid = list.getValue("hthwid");
        RowMap saleOrderRow = saleOrderBean.getLookupRow(hthwid);
        String dwtxid = saleOrderRow.get("dwtxid");
        isSaleOrder = !hthwid.equals("");
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <td>
       <input name="image" class="img" type="image" title="生成实际BOM" onClick="BuildFactBom('form1','scjhmxid=<%=list.getValue("scjhmxid")%>&hthwid=<%=list.getValue("hthwid")%>&cpid=<%=list.getValue("cpid")%>&xql=<%=list.getValue("sl")%>&ksrq=<%=list.getValue("ksrq")%>&scjhh=<%=masterRow.get("jhh")%>&zt=<%=zt%>&dmsxid=<%=list.getValue("dmsxid")%>')" src='../images/view.gif' border="0">
       </td>
      <%detailProducer.printCells(pageContext, "class=td");%>
    </tr>
      <%list.next();
      }
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}
  }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);
    function BuildFactBom(frmName, srcVar, methodName,notin)
     {
       var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
       var winName= "BuildFactBom";
       paraStr = "../produce/build_fact_bom.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
       if(methodName+'' != 'undefined')
         paraStr += "&method="+methodName;
       if(notin+'' != 'undefined')
       paraStr += "&notin="+notin;
       newWin =window.open(paraStr,winName,winopt);
       newWin.focus();
  }
</script>
</form>
</BODY>
</Html>
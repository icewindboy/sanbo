<%--采购合同框架下方合同明细显示--%>
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
  String pageCode = "buy_order";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.buy.xixing.B_BuyOrder buyOrderBean = engine.erp.buy.xixing.B_BuyOrder.getInstance(request);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  engine.project.LookUp importApplyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_APPLY_GOODS);

  buyOrderBean.doService(request, response);
  EngineDataSet list = buyOrderBean.getDetailTable();
   // RowMap masterRow = buyOrderBean.getMasterRowinfo();
  //RowMap[] detailRows= buyOrderBean.getDetailRowinfos();
  prodBean.regData(list,"cpid");
  propertyBean.regData(list, "dmsxid");
  importApplyBean.regData(list,"cgsqdhwid");
  synchronized(list){
    //EngineDataSet list = buyOrderBean.getDetailTable();
    if(list.changesPending())
      buyOrderBean.openDetailTable(false);
    HtmlTableProducer detailProducer = buyOrderBean.detailProducer;

    //设置打印td的监听器，用于打印产品编码和计量单位
    //detailProducer.setHtmlPrintCellListener(new DetailCellListener());
    //engine.project.LookUp salePriceBean = buyOrderBean.getSalePriceBean(request);
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
      <td nowrap height='20'>请购单编号</td>
      <td nowrap height='20'>原辅料编号</td>
      <td nowrap height='20'>原辅料名称规格</td>
      <td nowrap>规格属性</td>
      <td nowrap height='20'>数量</td>
      <td nowrap>单位</td>
      <td nowrap height='20'>换算数量</td>
      <td nowrap height='20'>换算单位</td>
      <td nowrap height='20'>单价</td>
      <td nowrap height='20'>金额</td>
      <td nowrap height='20'>到货量</td>
      <td nowrap>已入库量</td>
      <td nowrap height='20'>交货日期</td>
      <td nowrap height='20'>备注</td>
      <%--detailProducer.printTitle(pageContext, "height='20'");--%>
    </tr>
    <%BigDecimal t_sl = new BigDecimal(0),t_hssl=new BigDecimal(0),t_je=new BigDecimal(0),t_sjjhl=new BigDecimal(0),t_sjrkl=new BigDecimal(0);
      list.first();
      int i=0;
      //RowMap detail = null;
      for(; i<list.getRowCount(); i++)   {
        list.goToRow(i);
        String sl = list.getValue("sl");
        String hssl = list.getValue("hssl");
        String sjjhl = list.getValue("sjjhl");
        String sjrkl = list.getValue("sjrkl");
         String je = list.getValue("je");

        if(buyOrderBean.isDouble(sl))
          t_sl = t_sl.add(new BigDecimal(sl));
        if(buyOrderBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
        if(buyOrderBean.isDouble(sjjhl))
          t_sjjhl = t_sjjhl.add(new BigDecimal(sjjhl));
        if(buyOrderBean.isDouble(sjrkl))
           t_sjrkl = t_sjrkl.add(new BigDecimal(sjrkl));
        if(buyOrderBean.isDouble(je))
           t_je = t_je.add(new BigDecimal(je));

        //detail = detailRows[i];
        RowMap  importApplyRow= importApplyBean.getLookupRow(list.getValue("cgsqdhwid"));
        RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));

    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <td class="td" nowrap><%=importApplyRow.get("sqbh")%></td>


     <%-- <td nowrap height='20'><%=list.getValue("cgsqdhwid") %></td>--%>
      <td class="td" nowrap height='20'><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap height='20'><%=prodRow.get("product")%></td>
      <td class="td" nowrap><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
      <td class="td" align="right" nowrap height='20'><%=list.getValue("sl")%></td>
      <td class="td" nowrap><%=prodRow.get("jldw")%></td>
     <td class="td" align="right" nowrap height='20'><%=list.getValue("hssl")%></td>
     <td class="td" nowrap><%=prodRow.get("hsdw")%></td>
      <td class="td" align="right" nowrap height='20'><%=list.getValue("dj")%> </td>
      <td class="td" align="right" nowrap height='20'><%=list.getValue("je")%> </td>
      <td class="td" align="right" nowrap height='20'><%=list.getValue("sjjhl")%> </td>
      <td class="td" align="right" nowrap height='20'><%=list.getValue("sjrkl")%> </td>
      <td class="td" nowrap height='20'><%=list.getValue("jhrq")%> </td>
      <td class="td" nowrap height='20'><%=list.getValue("bz")%> </td>
      <%--detailProducer.printCells(pageContext, "class=td");--%>
    </tr>
      <%list.next();
      }
      %>

      <tr class="td" >
      <td nowrap align="center"><b>合计</b></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly> </td>
      <td>&nbsp;</td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly> </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_je%>' readonly> </td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_sjjhl%>' readonly> </td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_sjrkl%>' readonly> </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
   </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td><td class="td" nowrap>&nbsp;</td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}
  }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
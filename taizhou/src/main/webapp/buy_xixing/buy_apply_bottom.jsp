<%--采购申请单框架下方合同明细显示dddd--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "buy_apply";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.buy.xixing.BuyApply buyApplyBean = engine.erp.buy.xixing.BuyApply.getInstance(request);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  buyApplyBean.doService(request, response);
  EngineDataSet list = buyApplyBean.getDetailTable();
  prodBean.regData(list,"cpid");
  propertyBean.regData(list, "dmsxid");
    corpBean.regData(list,"dwtxId");
  synchronized(list){
    if(list.changesPending())
      buyApplyBean.openDetailTable(false);
    HtmlTableProducer detailProducer = buyApplyBean.detailProducer;
  //设置打印td的监听器，用于打印产品编码和计量单位
  //detailProducer.setHtmlPrintCellListener(new DetailCellListener());
  //engine.project.LookUp salePriceBean = buyApplyBean.getSalePriceBean(request);
%>
<html>
<head>
<title>
</title>
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
      <td nowrap height='20'>原辅料编号</td>
      <td height='20' nowrap>品名规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>用途</td>
      <td nowrap height='20'>采购单位</td>
      <td nowrap height='20'>需购数量</td>
      <td nowrap height='20'>单价</td>
       <td nowrap height='20'>金额</td>
      <td nowrap height='20'>实开合同量</td>
      <td nowrap height='20'>单位</td>
      <td nowrap height='20'>需求日期</td>
      <td nowrap height='20'>备注</td>
     <%-- <%detailProducer.printTitle(pageContext, "height='20'");%>--%>
    </tr>
    <%BigDecimal t_sl = new BigDecimal(0),t_skhtl=new BigDecimal(0),t_je = new BigDecimal(0);
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
        list.goToRow(i);
        String sl = list.getValue("sl");
        String skhtl = list.getValue("skhtl");
        String je = list.getValue("je");
        if(buyApplyBean.isDouble(sl))
        t_sl = t_sl.add(new BigDecimal(sl));
        if(buyApplyBean.isDouble(skhtl))
        t_skhtl = t_skhtl.add(new BigDecimal(skhtl));
        if(buyApplyBean.isDouble(je))
        t_je = t_je.add(new BigDecimal(je));
        RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));
    %>
    <tr>
       <td class="td" class="td" nowrap><%=i+1%></td>

       <td class="td" nowrap height='20'><%=prodRow.get("cpbm")%></td>
       <td class="td" nowrap height='20'><%=prodRow.get("product")%></td>
       <td class="td" nowrap><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
         <td class="td" nowrap height='20'><%=list.getValue("yt")%></td>
       <td class="td" nowrap><%=corpBean.getLookupName(list.getValue("dwtxId"))%></td>
       <td class="td" align="right" nowrap height='20'><%=list.getValue("sl")%></td>
       <td class="td" align="right" nowrap height='20'><%=list.getValue("dj")%></td>
       <td class="td" align="right" nowrap height='20'><%=list.getValue("je")%></td>
       <td class="td" align="right" nowrap height='20'><%=list.getValue("skhtl")%></td>
       <td class="td" nowrap><%=prodRow.get("jldw")%></td>
       <td class="td" nowrap height='20'><%=list.getValue("xqrq")%></td>
       <td class="td" nowrap height='20'><%=list.getValue("bz")%></td>


      <%--<%detailProducer.printCells(pageContext, "class=td");%>--%>
    </tr>
      <%list.next();
      }%>

      <tr class="td" >
      <td nowrap align="center"><b>合计</b></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly> </td>
      <td>&nbsp;</td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_je%>' readonly> </td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_skhtl%>' readonly> </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
   </tr>

<%
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%>
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
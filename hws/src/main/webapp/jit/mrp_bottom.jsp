<%--物料需求计划框架下方合同明细显示--%>
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
          header.append("<td class=td nowrap>产品编码</td>").append(reponse.getCellHeader());
          reponse.getCellContent().append("</td><td class=td nowrap>单位");
          break;
        case HtmlPrintCellResponse.PRINT_BODY:
          RowMap row = reponse.getField().getLookUp().getLookupRow(ds.getBigDecimal("cpid").toString());
          header.append("<td class=td nowrap>").append(row.get("cpbm")).append("</td>").append(reponse.getCellHeader());
          reponse.getCellContent().append("</td><td class=td nowrap>").append(row.get("jldw"));
          break;
        case HtmlPrintCellResponse.RRINT_BLANK:
          header.append("<td class=td nowrap>&nbsp;</td>").append(reponse.getCellHeader());
          reponse.getCellContent().append("</td><td class=td nowrap>&nbsp;");
          break;
      }
      reponse.setCellHeader(header);
    }
  }*/
%><%
  String pageCode = "produce_plan";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.jit.MRP mrpBean = engine.erp.jit.MRP.getInstance(request);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_STOCK);
  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_ORDER);//根据htid得到合同编号
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工序
  engine.project.LookUp planBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCE_PLAN);//根据生产计划id得到生产计划号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp planUseAbleBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PALN_USABLE_NUMBER);//通过产品ID和规格属性ID得到计划可供量
  mrpBean.doService(request, response);
  EngineDataSet list = mrpBean.getDetailTable();
  synchronized(list){
    if(list.changesPending())
      mrpBean.openDetailTable(false);
    HtmlTableProducer detailProducer = mrpBean.detailProducer;
    prodBean.regData(list,"cpid");
    propertyBean.regData(list, "dmsxid");
    saleOrderBean.regData(list, "htid");
    technicsRouteBean.regConditionData(list,"cpid");
    //设置打印td的监听器，用于打印产品编码和计量单位
    //detailProducer.setHtmlPrintCellListener(new DetailCellListener());
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
     <td nowrap><%=detailProducer.getFieldInfo("htid").getFieldname()%></td>
     <%
       for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length;i++)
         out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
     %>
     <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
     <td nowrap><%=detailProducer.getFieldInfo("jlxql").getFieldname()%></td>
     <td nowrap>单位</td>
     <td nowrap><%=detailProducer.getFieldInfo("xql").getFieldname()%></td>
     <td nowrap>生产用单位</td>
     <%--<td nowrap><%=detailProducer.getFieldInfo("gylxid").getFieldname()%></td>--%>
     <td nowrap><%=detailProducer.getFieldInfo("xgl").getFieldname()%></td>
     <td nowrap><%=detailProducer.getFieldInfo("xqrq").getFieldname()%></td>
     <td nowrap><%=detailProducer.getFieldInfo("cc").getFieldname()%></td>
     <td nowrap><%=detailProducer.getFieldInfo("yprwl").getFieldname()%></td>
     <td nowrap><%=detailProducer.getFieldInfo("chxz").getFieldname()%></td>
     <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++){
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
     <%RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));
        String chxz = list.getValue("chxz");
        chxz = chxz.equals("1") ? "自制件" : (chxz.equals("2") ? "外购件" : (chxz.equals("3") ? "外协件" : "虚拟件"));
      %>
      <td class="td" nowrap><%=saleOrderBean.getLookupName(list.getValue("htid"))%></td>
      <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodRow.get("product")%></td>
      <%--03.05 18:21 新增 修改规格属性为可输入的.并且新增onChange时的函数.yjg--%>
      <td class="td" nowrap><%=propertyBean.getLookupName(list.getValue("dmsxid"))%>
      </td>
      <td class="td" nowrap align=right><%=list.getValue("jlxql")%></td>
      <td class="td" nowrap><%=prodRow.get("jldw")%></td>
      <td class="td" nowrap align=right><%=list.getValue("xql")%></td>
      <td class="td" nowrap><%=prodRow.get("scdwgs")%></td>
      <%--<td class="td" nowrap><%=technicsRouteBean.getLookupName(list.getValue("gylxid"))%></td>--%>
      <td class="td" nowrap align="right"><%=list.getValue("xgl")%></td>
      <td class="td" nowrap><%=list.getValue("xqrq")%></td>
      <td class="td" nowrap align="right"><%=list.getValue("cc")%></td>
      <td class="td" nowrap align="right"><%=list.getValue("yprwl")%></td>
      <td class="td" nowrap align="right"><%=chxz%></td>
      <td class="td" nowrap align="right"><%=list.getValue("bz")%></td>
    </tr>
      <%list.next();
      }
      for(; i < 4; i++){
    %>
    <tr>
    <td class="td" nowrap></td><td class="td" nowrap></td><td class="td" nowrap></td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}
  }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
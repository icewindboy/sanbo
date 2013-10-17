<%--车间流转单--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%
  engine.erp.jit.ShopFlow shopFlowBean = engine.erp.jit.ShopFlow.getInstance(request);
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//通过jgdmxid得到加工单号(?任务单号?)
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//存货信息
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  String pageCode = "shop_flow";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  synchronized(shopFlowBean){
    shopFlowBean.doService(request, response);
    EngineDataSet list = shopFlowBean.getDetailTable();
    HtmlTableProducer detailProducer = shopFlowBean.detailProducer;
    prodBean.regData(list,"cpid");
    processBean.regData(list,"jgdmxID");
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
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <td nowrap><%=detailProducer.getFieldInfo("jgdmxid").getFieldname()%></td>
       <%
         for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-1;i++)
           out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
       %>
       <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
       <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
       <td nowrap>计量单位</td>
       <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
    </tr>
    <%
      int i=0;
      list.first();
      BigDecimal t_zsl = new BigDecimal (0);
      for(; i<list.getRowCount(); i++){
        String sl = list.getValue("sl");
        if(shopFlowBean.isDouble(sl))
          t_zsl = t_zsl.add(new BigDecimal(sl));
        RowMap processRow = processBean.getLookupRow(list.getValue("jgdmxID"));
        RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));
        String sx = propertyBean.getLookupName(list.getValue("dmsxid"));

    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <td class="td" nowrap><%=processRow.get("jgdh")%></td>
      <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodRow.get("product")%></td>
      <td class="td" nowrap><%=sx%></td>
      <td class="td" nowrap align="right"><%=list.getValue("sl")%></td>
      <td class="td" nowrap><%=prodRow.get("jldw")%></td>
      <td class="td" nowrap><%=list.getValue("bz")%></td>
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
      <td class="td" nowrap align="right"><%=t_zsl%></td>
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
    <%}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);
</script>
</form>
</BODY>
<%}%>
</Html>


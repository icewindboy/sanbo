<%--生产计划框架下方合同明细显示--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "produce_plan";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.jit.ProducePlan producePlanBean = engine.erp.jit.ProducePlan.getInstance(request);

  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_SALE_ORDER_GOODS);//根据销售合同货物id得到合同编号
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_STOCK);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性

  producePlanBean.doService(request, response);
  RowMap  masterRow= producePlanBean.getMasterRowinfo();
  EngineDataSet ds = producePlanBean.getMaterTable();
  String zt = ds.getValue("zt");
  String jhh = ds.getValue("jhh");
  EngineDataSet list = producePlanBean.getDetailTable();
  synchronized(list){
    if(list.changesPending())
      producePlanBean.openDetailTable(false);
    HtmlTableProducer detailProducer = producePlanBean.detailProducer;
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
      <%
        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length;i++)
          out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
   %>
      <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
      <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
      <td nowrap>次品率(%)</td>
      <td nowrap><%=detailProducer.getFieldInfo("ksrq").getFieldname()%></td>
      <td nowrap><%=detailProducer.getFieldInfo("wcrq").getFieldname()%></td>
      <td nowrap><%=detailProducer.getFieldInfo("jgyq").getFieldname()%></td>
    </tr>
    <%
      prodBean.regData(list,"cpid");
   saleOrderBean.regData(list,"hthwid");
   propertyBean.regData(list,"dmsxid");
   BigDecimal t_zsl = new BigDecimal(0), t_hssl = new BigDecimal(0), t_scsl=new BigDecimal(0);
   list.first();
   int i=0;
   //RowMap detail =null;
   boolean isSaleOrder = true;
   for(; i<list.getRowCount(); i++){
     list.goToRow(i);
     RowMap prodRow= prodBean.getLookupRow(list.getValue("cpid"));
     String dmsxid = list.getValue("dmsxid");
     String sx = propertyBean.getLookupName(dmsxid);
     String cpbm = prodRow.get("cpbm");
     String product = prodRow.get("product");
     String sl = list.getValue("sl");
     String ksrq = list.getValue("ksrq");
     String wcrq = list.getValue("wcrq");
     String jgyq = list.getValue("jgyq");
     String cpl = list.getValue("cpl");
     if(producePlanBean.isDouble(sl))
       t_zsl = t_zsl.add(new BigDecimal(sl));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <td class="td" nowrap><%=cpbm%></td>
      <td class="td" nowrap><%=product%></td>
      <td class="td" nowrap><%=sx%></td>
      <td class="td" nowrap><%=sl%></td>
      <td class="td" nowrap><%=cpl%></td>
      <td class="td" nowrap><%=ksrq%></td>
      <td class="td" nowrap><%=wcrq%></td>
      <td class="td" nowrap><%=jgyq%></td>
    </tr>
      <%list.next();
        }
    %>
    <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class=td>&nbsp;</td>
    <td class=td>&nbsp;</td>
        <td class=td>&nbsp;</td>
      <td class=td><%=t_zsl.toString()%></td>
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
    </tr>
    <%}
  }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);
    function BuildFactBom(frmName, srcVar, methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
      var winName= "BuildFactBom";
      paraStr = "../jit/build_fact_bom.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
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
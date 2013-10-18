<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "self_gain_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.shengyu.B_SelfGain selfGainBean = engine.erp.store.shengyu.B_SelfGain.getInstance(request);
  synchronized(selfGainBean){
  selfGainBean.doService(request, response);
  EngineDataSet list = selfGainBean.getDetailTable();
  HtmlTableProducer detailProducer = selfGainBean.detailProducer;
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//通过加工单明细id得到加工单号
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  if(list.changesPending())
      selfGainBean.openDetailTable(false);
  prodBean.regData(list,"cpid");
  propertyBean.regData(list,"dmsxid");
  processBean.regData(list,"jgdmxid");
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
<td class="td" nowrap></td>
     <td height='20' nowrap><%=detailProducer.getFieldInfo("jgdmxid").getFieldname()%></td>
    <%
       for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-1;i++)
         out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
    %>
    <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
    <td nowrap><%=detailProducer.getFieldInfo("batchno").getFieldname()%></td>
    <td nowrap><%=detailProducer.getFieldInfo("drawnum").getFieldname()%></td>
    <td height='20' nowrap>计量单位</td>
    <td nowrap><%=detailProducer.getFieldInfo("drawbignum").getFieldname()%></td>
    <td height='20' nowrap>换算单位</td>
    <td nowrap><%=detailProducer.getFieldInfo("kwid").getFieldname()%></td>
    <td nowrap><%=detailProducer.getFieldInfo("memo").getFieldname()%></td>
    <td nowrap><%=detailProducer.getFieldInfo("djh").getFieldname()%></td>
    </tr>
     <%BigDecimal t_zsl = new BigDecimal(0), t_hssl = new BigDecimal(0), t_scsl=new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<list.getRowCount(); i++) {
        list.goToRow(i);
        String sl = list.getValue("drawNum");
        if(selfGainBean.isDouble(sl))
          t_zsl = t_zsl.add(new BigDecimal(sl));
        String hssl = list.getValue("drawBigNum");
        if(selfGainBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
        /*String scsl = list.getValue("produceNum");
        if(selfGainBean.isDouble(scsl))
          t_scsl = t_scsl.add(new BigDecimal(scsl));
        */
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
     <%RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));%>
     <td class="td" nowrap><%=processBean.getLookupName(list.getValue("jgdmxid"))%></td>
     <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
     <td class="td" nowrap><%=prodRow.get("product")%></td>
     <td class="td" nowrap>
     <%=propertyBean.getLookupName(list.getValue("dmsxid"))%>
     </td>
     <td class="td" align="center" nowrap><%=list.getValue("batchno")%></td>
     <%--<td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawbignum_<%=i%>" name="drawbignum_<%=i%>" value='<%=list.getValue("drawbignum")%>' maxlength='<%=list.getColumn("drawbignum").getPrecision()%>' onblur="sl_onchange(<%=i%>, true)"<%=readonly%>></td>
     <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
     --%>
     <td class="td" align="right" nowrap><%=list.getValue("drawnum")%></td>
     <td class="td" nowrap><%=prodRow.get("jldw")%></td>
    <td class="td" align="right" nowrap><%=list.getValue("drawbignum")%></td>
     <td class="td" nowrap><%=prodRow.get("hsdw")%></td>
     <%--<td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="producenum_<%=i%>" name="producenum_<%=i%>" value='<%=list.getValue("producenum")%>' maxlength='<%=list.getColumn("producenum").getPrecision()%>' onblur="producesl_onchange(<%=i%>)"<%=readonly%>></td>
     <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly></td>
     --%>
     <td class="td" nowrap>
     <%=storeAreaBean.getLookupName(list.getValue("kwid"))%>
     </td>
     <td class="td" nowrap align="center"><%=list.getValue("memo")%></td>
    <td class="td" nowrap align="center"><%=list.getValue("djh")%></td>
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
         <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
    <td class="td" nowrap></td><td class="td" nowrap></td><td class="td" nowrap></td>
    <td class="td" nowrap></td>
      </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td><td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}
    }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
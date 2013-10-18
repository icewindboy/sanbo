<%--生产任务单框架下方合同明细显示--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "process_card";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.jit.B_ProcessCard B_ProcessCardBean = engine.erp.jit.B_ProcessCard.getInstance(request);
  B_ProcessCardBean.doService(request, response);
  EngineDataSet list = B_ProcessCardBean.getDetailTable();
  //EngineDataSet ds = B_ProcessCardBean.getMaterTable();
  //System.out.println("RowCount:"+list.getRowCount());
  synchronized(list){
    if(list.changesPending())
      return;
      //B_ProcessCardBean.openDetailTable(false);
    //System.out.println("RowCount:"+list.getRowCount());
    engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
    engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性LookUp
    engine.project.LookUp wlxqBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_MRP_GOODS);
    engine.project.LookUp producePlanBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PLAN);
    engine.project.LookUp htbhBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PLAN_DETAIL);

    HtmlTableProducer detailProducer = B_ProcessCardBean.detailProducer;
    prodBean.regData(list,"cpid");
    htbhBean.regData(list,"scjhmxid");
    propertyBean.regData(list, "dmsxid");
    //producePlanBean.regData(ds, "scjhid");
    String scjhmxid = list.getValue("scjhmxid");
    String htbh = htbhBean.getLookupName(scjhmxid);
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
<td nowrap><%=detailProducer.getFieldInfo("rcvnmbr").getFieldname()%></td>
<td height='20' nowrap>计量单位</td>
<td nowrap><%=detailProducer.getFieldInfo("jhrq").getFieldname()%></td>
<td nowrap><%=detailProducer.getFieldInfo("sjrq").getFieldname()%></td>
<td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
    </tr>
    <%BigDecimal t_zsl = new BigDecimal(0), t_hssl = new BigDecimal(0), t_scsl=new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<list.getRowCount(); i++) {
        list.goToRow(i);

        String sl = list.getValue("rcvnmbr");
        if(B_ProcessCardBean.isDouble(sl))
          t_zsl = t_zsl.add(new BigDecimal(sl));
        String scsl = list.getValue("rcvbgnmbr");
        if(B_ProcessCardBean.isDouble(scsl))
          t_scsl = t_scsl.add(new BigDecimal(scsl));
        RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));

        String dmsxid = list.getValue("dmsxid");
        String sx = propertyBean.getLookupName(dmsxid);

    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodRow.get("product")%></td>
    <td class="td" nowrap><%=sx%></td>
    <td class="td" nowrap align="right"><%=list.getValue("rcvnmbr")%></td>
    <td class="td" nowrap><%=prodRow.get("jldw")%></td>
    <td class="td" nowrap><%=list.getValue("jhrq")%></td>
    <td class="td" nowrap><%=list.getValue("sjrq")%></td>
    <td class="td" nowrap><%=list.getValue("bz")%></td>
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
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
      <td align="right" class="td"></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td><td class="td" nowrap></td>
       </tr>
      <%
        for(; i < 4; i++){
    %>
    <tr>

      <%detailProducer.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}
  }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
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
  engine.erp.jit.B_ProduceProcess produceProcessBean = engine.erp.jit.B_ProduceProcess.getInstance(request);
  produceProcessBean.sfwjg="0";
  produceProcessBean.doService(request, response);
  EngineDataSet list = produceProcessBean.getDetailTable();
  EngineDataSet ds = produceProcessBean.getMaterTable();
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性LookUp
  engine.project.LookUp wlxqBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_MRP_GOODS);
  //engine.project.LookUp produceCardBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PROCESS_CARD_DETAIL);
  engine.project.LookUp produceCardBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PROCESS_CARD_DETAIL);
  //System.out.println("RowCount:"+list.getRowCount());
  synchronized(list){
    if(list.changesPending())
      produceProcessBean.openDetailTable(false);
    //System.out.println("RowCount:"+list.getRowCount());
    HtmlTableProducer detailProducer = produceProcessBean.detailProducer;
    prodBean.regData(list,"cpid");
    wlxqBean.regData(list,"wlxqjhmxid");
    propertyBean.regData(list, "dmsxid");
    //String scjhh = producePlanBean.getLookupName(scjhid);
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
      <%
        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length;i++)
          out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
      %>
<td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
      <%--td nowrap><%--=detailProducer.getFieldInfo("gylxid").getFieldname()%></td--%>
<td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
<td height='20' nowrap>计量单位</td>
<td height='20' nowrap>换算数量</td>
<td height='20' nowrap>换算单位</td>
<%--td nowrap><%=detailProducer.getFieldInfo("scsl").getFieldname()%></td>
      <td nowrap><%=detailProducer.getFieldInfo("ypgzl").getFieldname()%></td>
<td height='20' nowrap>生产单位</td--%>
<td nowrap><%=detailProducer.getFieldInfo("cpl").getFieldname()%>%</td>
<td nowrap><%=detailProducer.getFieldInfo("ksrq").getFieldname()%></td>
<td nowrap><%=detailProducer.getFieldInfo("wcrq").getFieldname()%></td>
<td nowrap><%=detailProducer.getFieldInfo("jgyq").getFieldname()%></td>
    </tr>
    <%BigDecimal t_zsl = new BigDecimal(0), t_hssl = new BigDecimal(0), t_scsl=new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<list.getRowCount(); i++) {
        list.goToRow(i);
        String wgcl = list.getValue("wgcl");
        wgcl = wgcl.equals("1")?"入库":"流转";
        String sl = list.getValue("sl");
        if(produceProcessBean.isDouble(sl))
          t_zsl = t_zsl.add(new BigDecimal(sl));
        String hssl = list.getValue("hssl");
        if(produceProcessBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
        RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));
        String wlxqjhmxid = list.getValue("wlxqjhmxid");
        String dmsxid = list.getValue("dmsxid");
        String sx = propertyBean.getLookupName(dmsxid);
        RowMap wlxqrow = wlxqBean.getLookupRow(wlxqjhmxid);
        String wlxqh = wlxqrow.get("WLXQH");
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodRow.get("product")%></td>
    <td class="td" nowrap><%=sx%></td>
    <td class="td" nowrap align="right"><%=list.getValue("sl")%></td>
    <td class="td" nowrap><%=prodRow.get("jldw")%></td>
    <td class="td" nowrap align="right"><%=list.getValue("hssl")%></td>
    <td class="td" nowrap><%=prodRow.get("hsdw")%></td>
    <%--td class="td" nowrap align="right"><%=list.getValue("scsl")%></td>
    <td class="td" nowrap><%=prodRow.get("scydw")%></td--%>
    <td class="td" nowrap><%=list.getValue("cpl")%></td>
    <td class="td" nowrap><%=list.getValue("ksrq")%></td>
    <td class="td" nowrap><%=list.getValue("wcrq")%></td>
    <td class="td" nowrap><%=list.getValue("jgyq")%></td>
    </tr>
      <%list.next();
        }
        i=count+1;
      %>
      <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap>&nbsp</td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
      <td align="right" class="td"></td>
      <td align="right" class="td"></td>
      <td align="right" class="td"></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
       </tr>
      <%
        for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
    </tr>
    <%}
  }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
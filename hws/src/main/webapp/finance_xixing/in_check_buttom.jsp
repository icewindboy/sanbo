<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "in_check";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.finance.xixing.B_InCheck b_InCheckBean = engine.erp.finance.xixing.B_InCheck.getInstance(request);
  synchronized(b_InCheckBean){
  b_InCheckBean.doService(request, response);
    EngineDataSet list = b_InCheckBean.getDetailTable();
    HtmlTableProducer detailProducer = b_InCheckBean.detailProducer;//从表表格生成器
   // engine.project.LookUp saleLadingBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_LADING_BILL);
   engine.project.LookUp incheckBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_IN_CHECK_BILL);
    engine.project.LookUp productBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
    //saleLadingBean.regData(list,"nbjsmxid");
    incheckBean.regData(list,"nbjsmxid");
    productBean.regData(list,"cpid");
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
<form name="form1" action="" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <td nowrap>内部流转单编号</td>
      <td nowrap>品名规格</td>
      <td nowrap>核销金额</td>
      <%--detailProducer.printTitle(pageContext, "height='20'");--%><%--打印从表表头--%>
    </tr>
    <%
      if(list.changesPending())
        b_InCheckBean.openDetailTable(b_InCheckBean.masterIsAdd());
      BigDecimal t_jsje = new BigDecimal(0);
      BigDecimal t_tcj = new BigDecimal(0);
      int count = list.getRowCount();
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++){
        //RowMap tdRow = saleLadingBean.getLookupRow(list.getValue("nbjsmxid"));
        RowMap tdRow = incheckBean.getLookupRow(list.getValue("nbjsmxid"));
        RowMap productRow = productBean.getLookupRow(list.getValue("cpid"));
        String jsje = list.getValue("jsje");
        if(b_InCheckBean.isDouble(jsje))
          t_jsje = t_jsje.add(new BigDecimal(jsje));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td><%--行号--%>
      <td class="td" nowrap><%=tdRow.get("nbjsdh")%></td>
      <td class="td" nowrap><%=productRow.get("product")%></td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=jsje%>' readonly></td>
      <%--detailProducer.printCells(pageContext, "class=td");--%><%--打印从表表格内容--%>
    </tr>
      <%list.next();
      }
      i = count+1;
      %>
        <tr>
        <td class="tdTitle" nowrap>合计</td>
        <td class="td" nowrap>&nbsp;</td>
        <td class="td" nowrap></td>
        <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_jsje%>' readonly></td>
      </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <%--detailProducer.printBlankCells(pageContext, "class=td");--%><%--打印空格--%>
    </tr>
    <%}
  }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
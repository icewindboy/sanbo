<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "loan_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.finance.B_LoanMange B_LoanMangeBean = engine.erp.finance.B_LoanMange.getInstance(request);
  synchronized(B_LoanMangeBean){
    B_LoanMangeBean.doService(request, response);
    EngineDataSet list = B_LoanMangeBean.getDetailTable();
    HtmlTableProducer detailProducer = B_LoanMangeBean.detailProducer;//从表表格生成器
    engine.project.LookUp loanBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_LOAN);
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
      <td nowrap height='20'>还款单号</td>
      <td nowrap height='20'>还款日期</td>
      <td nowrap height='20'>还款金额</td>
     <td nowrap height='20'>剩余本息</td>
    </tr>
    <%

      loanBean.regData(list,"loanid");
      BigDecimal t_sl = new BigDecimal(0);
      BigDecimal t_je = new BigDecimal(0);
      BigDecimal t_se = new BigDecimal(0);
      BigDecimal t_jshj = new BigDecimal(0);

      int count = list.getRowCount();
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)   {
        String je = list.getValue("retnfund");
        if(B_LoanMangeBean.isDouble(je))
          t_je = t_je.add(new BigDecimal(je));

        RowMap loanRow = loanBean.getLookupRow(list.getValue("loanid"));
        String yearate = loanRow.get("yearate");
        String loanmxid = list.getValue("loanmxid");
        String loanid = list.getValue("loanid");
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td><%--行号--%>
      <td nowrap class="td"><%=list.getValue("rtncode")%></td>
      <td nowrap class="td"><%=list.getValue("retndate")%></td>
      <td nowrap class="td" align="right" align="right"><%=list.getValue("retnfund")%></td>
      <td nowrap class="td" align="right" align="right"><%=B_LoanMangeBean.getSYBX(loanid,loanmxid,yearate)%></td>
    </tr>
        <%
        list.next();
        }
        i=count+1;
        %>
        <tr>
        <td class="tdTitle" nowrap>合计</td>
        <td class="td" nowrap>&nbsp;</td>
        <td class="td" nowrap>&nbsp;</td>
        <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_je%>' readonly></td>
        <td class="td" nowrap>&nbsp;</td>
      </tr>
      <%
      for(; i < 4; i++){
      %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
<%--销售冲帐单--%><%@ page contentType="text/html; charset=UTF-8" %><%@
include file="../pub/init.jsp"%><%@
page import="engine.dataset.*,engine.project.Operate,engine.report.util.ReportData,java.math.BigDecimal,engine.html.*,java.util.ArrayList"
%><%
  engine.erp.sale.shengyu.B_Sale_CheckAccount b_Sale_CheckAccountBean = engine.erp.sale.shengyu.B_Sale_CheckAccount.getInstance(request);//得到实例(初始化实例变量)
  RowMap[] drows = b_Sale_CheckAccountBean.getXsRowinfos();
  RowMap masterRow = b_Sale_CheckAccountBean.masterRow;
  ReportData td = new ReportData();
  td.addReportData("ds", masterRow);
  td.addReportData("list", drows);
  request.setAttribute("dzd",td);
  pageContext.forward("../pub/pdfprint.jsp?operate="+Operate.PRINT_PRECISION+"&code=shyu_dzd_print");
%>
<%@ page contentType="text/html; charset=UTF-8" %><%@
include file="../pub/init.jsp"%>
<%-- 仓库辅助材料收发存汇总表 --%>
<%@
page import="engine.dataset.*,engine.project.Operate,engine.report.util.ReportData,java.math.BigDecimal,engine.html.*,java.util.ArrayList"
%>
<%
  engine.erp.store.xixing.B_StoreAssistantMaterialCollect B_StoreAssistantMaterialCollectBean = engine.erp.store.xixing.B_StoreAssistantMaterialCollect.getInstance(request);//得到实例(初始化实例变量)
  RowMap[] drows = B_StoreAssistantMaterialCollectBean.getXsRowinfos();
  RowMap masterRow = B_StoreAssistantMaterialCollectBean.masterRow;
  ReportData td = new ReportData();
  td.addReportData("ds", masterRow);
  td.addReportData("list", drows);
  request.setAttribute("dzd",td);
  pageContext.forward("../pub/pdfprint.jsp?operate="+Operate.PRINT_PRECISION+"&code=store_assist_collect_print");
%>
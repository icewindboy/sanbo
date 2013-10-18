<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList,engine.report.util.ReportData"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%>
<%
  engine.erp.sale.B_SalerPrix b_SalerPrixBean = engine.erp.sale.B_SalerPrix.getInstance(request);
  String pageCode = "saler_prix";
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
<BODY oncontextmenu="window.event.returnValue=true"  onLoad="syncParentDiv('tableview1');">
<%

  EngineDataSet tmp=b_SalerPrixBean.getDetailTable();
  EngineDataSet list = b_SalerPrixBean.getMaterTable();
  EngineDataSet dsField = b_SalerPrixBean.getJjTable();
  RowMap[] drows = b_SalerPrixBean.getDetailRowinfos();

  StringBuffer sb = new StringBuffer();

  session.setAttribute("saler_prix.buf",sb);
  response.sendRedirect("../pub/pdf.jsp?code=saler_prix.buf");
%>






<%--要打印的
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
     <td nowrap class="tdTitle">员工姓名</td>
     <td nowrap class="tdTitle">部门</td>
     <%
       dsField.first();
       for(int i=0;i<dsField.getRowCount();i++)
       {
         if(dsField.getValue("sfxs").equals("1"))
           out.println("<td nowrap class=\"tdTitle\">"+dsField.getValue("mc")+"</td>");
         if(dsField.getValue("ly").equals("2")&&dsField.getValue("sfkxg").equals("1"))
         dsField.next();
       }
      %>
      </tr>
        <%
          int i=0;
          for(; i < drows.length; i++) {
            RowMap drow = drows[i];
            String personid = drow.get("personid");
            RowMap prow=personBean.getLookupRow(drow.get("personid"));
         %>
      <tr>
      <td class="td" nowrap ><%=prow.get("xm")%></td>
      <td class="td" nowrap ><%=deptBean.getLookupName(drow.get("deptid"))%></td>
      <%
        dsField.first();
        for(int j=0;j<dsField.getRowCount();j++)
        {
          String fieldName = dsField.getValue("dyzdm")+"_"+i;
          if(dsField.getValue("sfxs").equals("1"))
          {
      %>
      <td nowrap class="td"><%=engine.util.Format.formatNumber(drow.get(dsField.getValue("dyzdm")).equals("")?"0":drow.get(dsField.getValue("dyzdm")),"#0.00") %></td>
      <%
          }
          dsField.next();
        }
      %>
    </tr>
    <%
      }
    %>
  </table>
--%>













































</BODY>
</Html>
<%@page contentType="text/html; charset=UTF-8" %>
<%request.setCharacterEncoding("UTF-8");
  //session.setMaxInactiveInterval(900);
  response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","max-age=0");
  response.addHeader("Cache-Control","pre-check=0");
  response.addHeader("Cache-Control","post-check=0");
  response.setDateHeader("Expires", 0);
  engine.common.OtherLoginBean loginBean = engine.common.OtherLoginBean.getInstance(request);
  if(!loginBean.isLogin(request, response))
    return;
%><%@ taglib uri="/WEB-INF/pagescontrol.tld" prefix="pc"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "report_destroy_list";

  engine.erp.store.OtherDestroy destroyBean = engine.erp.store.OtherDestroy.getInstance(request);
  destroyBean.doService(request, response);
  EngineDataSet list = destroyBean.getDetailTable();
  HtmlTableProducer detailProducer = destroyBean.detailProducer;
  synchronized(list){
    if(list.changesPending())
      destroyBean.openDetailTable(false);
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/scripts/public.css" type="text/css">
</head>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/validate.js"></script>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/rowcontrol.js"></script>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/tabcontrol.js"></script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <%detailProducer.printTitle(pageContext, "height='20'");%>
    </tr>
    <%
      BigDecimal t_zsl = new BigDecimal(0);
       BigDecimal t_hssl = new BigDecimal(0);
       String sl = "0";
      String hssl = "0";
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++) {
        sl = list.getValue("sl");
       hssl = list.getValue("hssl");
       if(destroyBean.isDouble(sl))
         t_zsl = t_zsl.add(new BigDecimal(sl));
       if(destroyBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <%detailProducer.printCells(pageContext, "class=td");%>
    </tr>
      <%list.next();
      }
    %>
      <tr>
     <td class="tdTitle" nowrap>合计</td>
<td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td>
     <td class=td align=right><%=t_zsl%></td>
      <td class=td align=right><%=t_hssl%></td>
     <td class=td>&nbsp;</td>

    </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
<%
/**
 * <p>Title:������������̵㵥��ϸ���ҳ��</p>
 * <p>Description: ������������̵㵥��ϸ���ҳ��.</p>
 * <p>             ��ҳ�汻�ŵ���store_check.jsp�������ҳ���·������.</p>
 * <p>             Ϊ����Ҫ ���һ�����е���ʱ����ָõ����ڲ���ϸ�� 02.17 ��������ҳ��.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author ���
 * @version 1.0
 */
%>
<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "store_check";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.shengyu.B_StoreCheck storeCheckBean = engine.erp.store.shengyu.B_StoreCheck.getInstance(request);
  storeCheckBean.doService(request, response);
  EngineDataSet list = storeCheckBean.getDetailTable();
  HtmlTableProducer detailProducer = storeCheckBean.detailProducer;
  synchronized(list){
    if(list.changesPending())
      storeCheckBean.openDetailTable(false);
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
      <td nowrap width=10></td>
      <%detailProducer.printTitle(pageContext, "height='20'");%>
    </tr>
    <%
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++) {
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <%detailProducer.printCells(pageContext, "class=td");%>
    </tr>
      <%list.next();
      }
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
<%
/**
 * <p>Title:�������������絥��ϸ����ҳ��</p>
 * <p>Description: �������������絥��ϸ����ҳ��.</p>
 * <p>             ��ҳ�汻�ŵ���report_destroy_list.jsp�������ҳ���·������.</p>
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
  String pageCode = "report_destroy_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.shengyu.B_Destroy destroyBean = engine.erp.store.shengyu.B_Destroy.getInstance(request);
  destroyBean.doService(request, response);
  EngineDataSet list = destroyBean.getDetailTable();
  HtmlTableProducer detailProducer = destroyBean.detailProducer;
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//���ʹ������
  synchronized(list){
    if(list.changesPending())
      destroyBean.openDetailTable(false);
    prodBean.regData(new String[]{"1"});
    propertyBean.regData(new String[]{"1"});
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
      <%
        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-1;i++)
        out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
      %>
      <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
      <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
      <td nowrap>������λ</td>
      <td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
      <td nowrap>���㵥λ</td>
      <td nowrap><%=detailProducer.getFieldInfo("ph").getFieldname()%></td>
      <td nowrap><%=detailProducer.getFieldInfo("kwid").getFieldname()%></td>
      <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
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
    <%RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));
    %>
           <td class="td" nowrap><%=i+1%></td>
    <td class="td" nowrap><%=prodRow.get("cpbm")%>
    </td>
    <td class="td" nowrap><%=prodRow.get("product")%></td>
    <%--03.05 18:21 ���� �޸Ĺ������Ϊ�������.��������onChangeʱ�ĺ���.yjg--%>
    <td class="td" nowrap><%=propertyBean.getLookupName(list.getValue("dmsxid"))%>
    </td>
    <td class="td" nowrap align="right"><%=list.getValue("sl")%></td>
    <td class="td" nowrap><%=prodRow.get("jldw")%></td>
    <td class="td" nowrap align="right"><%=list.getValue("hssl")%></td>
    <td class="td" nowrap><%=prodRow.get("hsdw")%></td>
    <td class="td" nowrap><%=list.getValue("ph")%></td>
    <td class="td" nowrap>
    <%=storeAreaBean.getLookupName(list.getValue("kwid"))%>
    </td>
    <td class="td" nowrap align="right"><%=list.getValue("bz")%></td>
    </tr>
      <%list.next();
      }
    %>
      <tr>
     <td class="tdTitle" nowrap>�ϼ�</td>
     <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td><td class=td align="right"><%=t_zsl%></td>
    <td class=td align=right></td>
     <td class=td align="right"><%=t_hssl%><td class=td align="right"></td>
     <td class=td>&nbsp;</td>
    <td class=td>&nbsp;</td><td class=td>&nbsp;</td>
    </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td><td class=td>&nbsp;</td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
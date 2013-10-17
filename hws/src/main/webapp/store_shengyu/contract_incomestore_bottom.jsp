<%
/**
 * <p>Title:���������ɹ���ⵥ��ϸ���ҳ��</p>
 * <p>Description: ���������ɹ���ⵥ��ϸ���ҳ��.</p>
 * <p>             ��ҳ�汻�ŵ���contract_incomestore_list.jsp�������ҳ���·������.</p>
 * <p>             Ϊ����Ҫ ���һ�����е���ʱ����ָõ����ڲ���ϸ�� 02.16 ��������ҳ��.</p>
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
  String pageCode = "contract_incomestore_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.shengyu.B_StoreOutBill storeOutBillBean = engine.erp.store.shengyu.B_StoreOutBill.getInstance(request);
  storeOutBillBean.doService(request, response);
  EngineDataSet list = storeOutBillBean.getDetailTable();
  HtmlTableProducer detailProducer = storeOutBillBean.detailProducer;
  boolean isHandwork = loginBean.getSystemParam("KC_HANDIN_STOCK_BILL").equals("1");//�õ��Ƿ�����ֹ����ӵ�ϵͳ����
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);//LookUp��λ��Ϣ
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//LookUp��Ʒ��Ϣ
  engine.project.LookUp buyOrderGoodsBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_ORDER_STOCK);//�ɹ���ͬ
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//���ʹ������
  prodBean.regData(list,"cpid");
  buyOrderGoodsBean.regData(list,"wjid");
  propertyBean.regData(list,"dmsxid");
  synchronized(list){
    if(list.changesPending())
      storeOutBillBean.openDetailTable(false);
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
        for (int i=0;i<detailProducer.getFieldInfo("wjid").getShowFieldNames().length;i++)
          out.println("<td nowrap>"+detailProducer.getFieldInfo("wjid").getShowFieldName(i)+"<//td>");
   %>
   <%
     for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-2;i++)
       out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
   %>
     <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
     <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
     <td nowrap>������λ</td>
     <td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
     <td nowrap>���㵥λ</td>
     <%--if(isHandwork){%>
     <td nowrap>����</td>
     <td nowrap>���</td>
   <%}--%>
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
      for(; i<list.getRowCount(); i++){
        sl = list.getValue("sl");
        hssl = list.getValue("hssl");
        if(storeOutBillBean.isDouble(sl))
          t_zsl = t_zsl.add(new BigDecimal(sl));
        if(storeOutBillBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
                         <%RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));
                         %>
                        <%RowMap buyOrderGoodsRow=buyOrderGoodsBean.getLookupRow(list.getValue("wjid"));%>
                        <td class="td" nowrap><%=buyOrderGoodsRow.get("jhdbm")%></td>
                        <td class="td" nowrap><%=buyOrderGoodsRow.get("htbh")%></td>
                         <td class="td" nowrap>
                        <%=prodRow.get("cpbm")%>
                        </td>
                        <td class="td" nowrap><%=prodRow.get("product")%></td>
                        <%--03.05 18:21 ���� �޸Ĺ������Ϊ�������.��������onChangeʱ�ĺ���.yjg--%>
                        <td class="td" nowrap><%=propertyBean.getLookupName(list.getValue("dmsxid"))%>
                        </td>
                        <td class="td" nowrap align=right><%=list.getValue("sl")%></td>
                        <td class="td" nowrap><%=prodRow.get("jldw")%></td>
                         <td class="td" nowrap align=right><%=list.getValue("hssl")%></td>
                         <td class="td" nowrap><%=prodRow.get("hsdw")%></td>
                         <%--if(isHandwork){%>
                         <td class="td" nowrap><%=list.getValue("dj")%></td>
                         <td class="td" nowrap><%=list.getValue("je")%></td>
                        <%}--%>
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
      <td class=td>&nbsp;</td><td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td align=right><%=t_zsl%></td>
      <td class=td>&nbsp;</td>
      <td class=td align=right><%=t_hssl%></td><td class=td>&nbsp;</td>
      <td class=td >&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td>
    </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <%detailProducer.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}
    }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
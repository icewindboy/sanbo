<%
/**
 * <p>Title:库存管理——采购入库单明细表格页面</p>
 * <p>Description: 库存管理——采购入库单明细表格页面.</p>
 * <p>             此页面被放到了contract_incomestore_list.jsp主框架网页的下方框架内.</p>
 * <p>             为的是要 点击一下已有单据时会出现该单据内部的细节 02.16 新增这张页面.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 杨建国
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
  boolean isHandwork = loginBean.getSystemParam("KC_HANDIN_STOCK_BILL").equals("1");//得到是否可以手工增加的系统参数
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);//LookUp库位信息
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//LookUp产品信息
  engine.project.LookUp buyOrderGoodsBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_ORDER_STOCK);//采购合同
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
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
     <td nowrap>计量单位</td>
     <td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
     <td nowrap>换算单位</td>
     <%--if(isHandwork){%>
     <td nowrap>单价</td>
     <td nowrap>金额</td>
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
                        <%--03.05 18:21 新增 修改规格属性为可输入的.并且新增onChange时的函数.yjg--%>
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

      <td class="tdTitle" nowrap>合计</td>
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
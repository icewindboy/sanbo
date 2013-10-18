<%
/**
 * <p>Title:库存管理——移库单列表明细表格页面</p>
 * <p>Description: 库存管理——移库单列表明细表格页面.</p>
 * <p>             此页面被放到了move_store_list.jsp主框架网页的下方框架内.</p>
 * <p>             为的是要 点击一下已有单据时会出现该单据内部的细节 02.17 新增这张页面.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 杨建国
 * @version 1.0
 */
%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "in_balance_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.xixing.B_InBalance inBalanceBean = engine.erp.store.xixing.B_InBalance.getInstance(request);
  inBalanceBean.doService(request, response);
  EngineDataSet list = inBalanceBean.getDetailTable();
  HtmlTableProducer detailProducer = inBalanceBean.detailProducer;
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  synchronized(list){
    if(list.changesPending())
      inBalanceBean.openDetailTable(false);
    prodBean.regData(list,"cpid");
    propertyBean.regData(list,"dmsxid");
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
<form name="form1" action="" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
       <%
         for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-2;i++)
           out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
       %>
       <%--03.09 12:06 修改 调整了表格中的规格属性, 数量,换算数量td的排列位置. 下面的具体显示这些值的jsp scripts也做了相应调整. yjg--%>
       <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
       <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
       <td nowrap>计量单位</td>
       <td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
       <td nowrap>换算单位</td>
       <td nowrap><%=detailProducer.getFieldInfo("dj").getFieldname()%></td>
       <td nowrap><%=detailProducer.getFieldInfo("je").getFieldname()%></td>
       <td nowrap>备注</td>
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
       if(inBalanceBean.isDouble(sl))
         t_zsl = t_zsl.add(new BigDecimal(sl));
       if(inBalanceBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));

    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <%RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));
      %>
      <td class="td" nowrap>
      <%=prodRow.get("cpbm")%>
      </td>
      <td class="td" nowrap align="center">
      <%=prodRow.get("product")%>
      </td>
      <%--02:14:50 新增一个规格属性 dmsxid td. 为新增一个规格属性而新增的.因为要它来和cpid, ph组合来确定只唯一性--%>
      <td class="td" nowrap align="center">
      <%=propertyBean.getLookupName(list.getValue("dmsxid"))%>
      </td>
      <td class="td" nowrap align="right"><%=list.getValue("sl")%></td>
      <td class="td" nowrap><%=prodRow.get("jldw")%></td>
      <td class="td" nowrap align="right"><%=list.getValue("hssl")%></td>
      <td class="td" nowrap><%=prodRow.get("hsdw")%></td>
      <td class="td" nowrap align="right"><%=list.getValue("dj")%></td>
      <td class="td" nowrap align="right"><%=list.getValue("je")%></td>
      <td class="td" nowrap align="right"><%=list.getValue("bz")%></td>
    </tr>
      <%list.next();
      }
     %>
      <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
      <td class=td align=right><%=t_zsl%></td><td class=td></td>
      <td class=td align=right><%=t_hssl%></td>
      <td class=td align=right></td>
      <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td>
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
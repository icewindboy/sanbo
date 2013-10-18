<%--采购进货单框架下方合同明细显示--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "cloth_outprocess";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.buy.xixing.B_ClothOutProcess b_ClothOutProcessBean = engine.erp.buy.xixing.B_ClothOutProcess.getInstance(request);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//LookUp部门
  b_ClothOutProcessBean.doService(request, response);
  synchronized(b_ClothOutProcessBean){
    EngineDataSet list = b_ClothOutProcessBean.getWjgcpTable();
    prodBean.regData(list,"cpid");
    propertyBean.regData(list, "dmsxid");
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
<form name="form1" action="" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <td nowrap height='20'>材料编号</td>
      <td nowrap height='20'>名称规格</td>
      <td nowrap height='20'>规格属性</td>
      <td nowrap>数量</td>
      <td nowrap height='20'>单位</td>
      <td nowrap height='20'>加工完成单价</td>
      <td nowrap height='20'>金额</td>
    </tr>
    <%

         if(list.changesPending())
         b_ClothOutProcessBean.openDetailTable(b_ClothOutProcessBean.masterIsAdd());

         BigDecimal t_sl = new BigDecimal(0), t_je = new BigDecimal(0);
         list.first();
         int i=0;
         int count=list.getRowCount();
        for(; i<list.getRowCount(); i++)   {
        list.goToRow(i);
        String sl = list.getValue("sl");
        String je = list.getValue("je");
       if(b_ClothOutProcessBean.isDouble(sl))
        t_sl = t_sl.add(new BigDecimal(sl));
       if(b_ClothOutProcessBean.isDouble(je))
        t_je = t_je.add(new BigDecimal(je));
        RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));
    %>
    <tr>
           <td class="td" nowrap><%=i+1%></td>
           <td class="td" nowrap height='20'><%=prodRow.get("cpbm")%></td>
           <td class="td" nowrap height='20'><%=prodRow.get("product")%></td>
           <td class="td" nowrap><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
           <td class="td" nowrap height='20' align="right"><%=list.getValue("sl")%></td>
           <td class="td" nowrap><%=prodRow.get("jldw")%></td>
           <td class="td" nowrap height='20' align="right"><%=list.getValue("jgwdj")%></td>
           <td class="td" nowrap height='20' align="right"><%=list.getValue("je")%></td>
    </tr>
      <%list.next();
      }
      %>
      <tr class="td" >
      <td nowrap align="center"><b>合计</b></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
     <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly> </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
   <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_je%>' readonly></td>
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
          <td class="td" nowrap>&nbsp;</td>
          <td class="td" nowrap>&nbsp;</td>
          <td class="td" nowrap>&nbsp;</td>
    </tr>
    <%}
  }%>
</table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>

</BODY>
</Html>
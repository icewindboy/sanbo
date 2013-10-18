<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "buy_fitting_requisition";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.equipment.B_EquipmentBuyApply b_EquipmentBuyApplyBean = engine.erp.equipment.B_EquipmentBuyApply.getInstance(request);
  LookUp productBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_PRODUCT);//产品/元件
  String retu = b_EquipmentBuyApplyBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
   {
     out.print(retu);
     return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = b_EquipmentBuyApplyBean.getDetailTable();
  productBean.regData(list,"cpid");
  RowMap productRow = productBean.getLookupRow(list.getValue("cpid"));
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
<form name="form1" action="" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <%
        int iPage = loginBean.getPageSize()-10;
  %>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td height="20" colspan="7" nowrap>配件型号比较</td>
    </tr>
    <tr class="tableTitle">
      <td  nowrap >元件编码</td>
      <td  nowrap>元件名称</td>
      <td  nowrap>型号规格</td>
      <td  nowrap>制造厂</td>
      <td  nowrap>价格性能比较</td>
      <td  nowrap>申购数量</td>
      <td  nowrap>要求到货日期</td>
    </tr>
      <%
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++){
     %>
    <tr>
      <td id="cpbm_<%=i%>" class="td" nowrap><%=productRow.get("cpbm")%></td><!--元件编码-->
      <td id="pm_<%=i%>" nowrap class=td><%=productRow.get("pm")%></td><!--元件名称-->
      <td id="gg_<%=i%>" nowrap class=td><%=productRow.get("gg")%></td><!--型号规格-->
      <td id="manufacturer_<%=i%>" nowrap class=td><%=list.getValue("manufacturer")%></td><!--制造厂-->
      <td id="price_<%=i%>" nowrap class=td><%=list.getValue("price")%></td><!--价格性能比较-->
      <td id="apply_num_<%=i%>" nowrap class=td><%=list.getValue("apply_num")%></td><!--申购数量-->
      <td id="need_getdate_<%=i%>" nowrap class=td><%=list.getValue("need_getdate")%></td><!--要求到货日期-->
    </tr>
    <%  list.next();
     }
    for(; i < 5; i++){
   %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
    </tr>
     <%}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
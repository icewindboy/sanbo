<%--销售分栏设置--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
  <%
  String pageCode = "sale_flz";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.sale.B_SaleColumnSet b_SaleColumnSetBean = engine.erp.sale.B_SaleColumnSet.getInstance(request);
  b_SaleColumnSetBean.doService(request, response);
  EngineDataSet list = b_SaleColumnSetBean.getDetailTable();
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//LookUp产品信息
  engine.project.LookUp wzlbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_KIND);//物资类别
  wzlbBean.regData(list,"wzlbid");
  prodBean.regData(list,"cpid");
  synchronized(list){
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
  <table id="tableview1" width="60%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <td  nowarp class="tabletitle"  width="40%">样品代码</td>
      <td  nowarp class="tabletitle"  width="60%">栏目</td>
    </tr>
    <%
      if(list.changesPending())
        b_SaleColumnSetBean.openDetailTable(b_SaleColumnSetBean.masterIsAdd());
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)   {
        String cpid=list.getValue("cpid");
        String wzlbid=list.getValue("wzlbid");
        RowMap productRow = prodBean.getLookupRow(cpid);
        RowMap wzRow = wzlbBean.getLookupRow(wzlbid);
        String b=productRow.get("cpbm");
        if(!wzlbid.equals(""))
          b=wzRow.get("bm");
        String p =productRow.get("product");
        if(!wzlbid.equals(""))
          p=wzRow.get("mc");
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <td  nowarp class="td"><%=b%></td>
      <td  nowarp class="td"><%=p%></td>
    </tr>
      <%list.next();
      }
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList,engine.erp.finance.*,engine.common.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "out_process_balance";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.finance.xixing.B_OutProcessBalance b_OutProcessBalanceBean = engine.erp.finance.xixing.B_OutProcessBalance.getInstance(request);
  b_OutProcessBalanceBean.doService(request, response);
  synchronized(b_OutProcessBalanceBean){
    EngineDataSet list = b_OutProcessBalanceBean.getDetailTable();
    HtmlTableProducer detailProducer = b_OutProcessBalanceBean.detailProducer;//从表表格生成器
    engine.project.LookUp buyLadingBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_ORDER_STOCK);
    engine.project.LookUp productBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
    engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性

    buyLadingBean.regData(list,"jgdmxid"); //交货单货物ID
    productBean.regData(list,"cpid");  //产品ID
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
<form name="form1" action="" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <td height='20' nowrap>加工单编号</td>
      <td height='20' nowrap>产品编码</td>
      <td height='20' nowrap>品名 规格</td>
      <td height='20' nowrap>规格属性</td>
      <td height='20' nowrap>单位</td>
      <td height='20' nowrap>应付金额</td>
      <td height='20' nowrap>核销金额</td>
      <td height='20' nowrap>差额</td>
      <%--detailProducer.printTitle(pageContext, "height='20'");--%><%--打印从表表头--%>
    </tr>
    <%
      if(list.changesPending())
        b_OutProcessBalanceBean.openDetailTable(b_OutProcessBalanceBean.masterIsAdd());
      BigDecimal t_jsje = new BigDecimal(0);
      BigDecimal t_jje = new BigDecimal(0);
      BigDecimal t_bce = new BigDecimal(0);
      int count = list.getRowCount();
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)   {
        RowMap jhdRow = buyLadingBean.getLookupRow(list.getValue("jgdmxid"));
        RowMap productRow = productBean.getLookupRow(list.getValue("cpid"));
        RowMap propertyRow = propertyBean.getLookupRow(list.getValue("dmsxID"));
        String jsje = list.getValue("jsje");
        if(b_OutProcessBalanceBean.isDouble(jsje))
          t_jsje = t_jsje.add(new BigDecimal(jsje));

        BigDecimal bhwje =new BigDecimal(jhdRow.get("je").equals("")?"0":jhdRow.get("je"));
        BigDecimal bjsje =new BigDecimal(list.getValue("jsje"));
        BigDecimal bce =bhwje.subtract(bjsje);

        t_jje=t_jje.add(new BigDecimal(jhdRow.get("je").equals("")?"0":jhdRow.get("je")));
        t_bce = t_bce.add(bce);
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td><%--行号--%>
      <td class="td" nowrap><%=jhdRow.get("jgdh")%></td>
      <td class="td" nowrap><%=productRow.get("cpbm")%></td>
      <td class="td" nowrap><%=productRow.get("product")%></td>
      <td align="left" nowrap class="td"><%=propertyRow.get("sxz")%></td>
      <td align="left" nowrap class="td"><%=productRow.get("jldw")%></td>
      <td class="td" nowrap><%=jhdRow.get("je")%></td>
      <td class="td" nowrap><%=list.getValue("jsje")%></td>
      <td class="td" nowrap><%=bce.toString()%></td>
      <%--detailProducer.printCells(pageContext, "class=td");--%><%--打印从表表格内容--%>
    </tr>
      <%list.next();
      }
      i = count+1;
      %>
        <tr>
        <td class="tdTitle" nowrap>合计</td>
        <td class="td" nowrap>&nbsp;</td>
        <td class="td" nowrap></td>
        <td class="td" nowrap></td>
        <td class="td" nowrap></td>
        <td class="td" nowrap></td>
        <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_jje%>' readonly></td>
        <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_jsje%>' readonly></td>
        <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_bce%>' readonly></td>
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
      <td class="td" nowrap>&nbsp;</td>
      <%--etailProducer.printBlankCells(pageContext, "class=td");--%><%--打印空格--%>
    </tr>
    <%}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
<%}%>
</Html>
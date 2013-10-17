<%--工人工资汇总（工人工作量列表）框架下方合同明细显示--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "work_wage";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.produce.B_WorkerWage workerWageBean = engine.erp.produce.B_WorkerWage.getInstance(request);
  workerWageBean.doService(request, response);
  EngineDataSet list = workerWageBean.getDetailTable();
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工艺路线类型
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//通过加工单明细id得到加工单号
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
      <td width=10></td>
      <td nowrap>加工单号</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名</td>
      <td nowrap>规格</td>
      <td nowrap>单位</td>
      <td nowrap>工艺类型</td>
      <td nowrap>工序</td>
      <td nowrap>数量</td>
      <td nowrap>定额数量</td>
      <td nowrap>计件单价</td>
      <td nowrap>计件工资</td>
    </tr>
    <%processBean.regData(list, "jgdmxid");
      prodBean.regData(list, "cpid");
      technicsRouteBean.regData(list, "gylxid");
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
        list.goToRow(i);
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td class="td" nowrap><%=processBean.getLookupName(list.getValue("jgdmxid"))%></td>
      <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodRow.get("pm")%></td>
      <td class="td" nowrap><%=prodRow.get("gg")%></td>
      <td class="td" nowrap><%=prodRow.get("jldw")%></td>
      <td class="td" nowrap><%=technicsRouteBean.getLookupName(list.getValue("gylxid"))%></td>
      <td class="td" nowrap><%=list.getValue("gx")%></td>
      <td class="td" nowrap align="right"><%=list.getValue("sl")%></td>
      <td class="td" nowrap align="right"><%=list.getValue("desl")%></td>
      <td class="td" nowrap align="right"><%=list.getValue("de")%></td>
      <td class="td" nowrap align="right"><%=list.getValue("jjgz")%></td>
    </tr>
      <%list.next();
      }
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
    </tr>
    <%}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
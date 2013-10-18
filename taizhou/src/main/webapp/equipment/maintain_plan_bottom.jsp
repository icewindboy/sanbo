<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "maintain_plan";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.equipment.B_MaintainPlan b_MaintainPlanBean = engine.erp.equipment.B_MaintainPlan.getInstance(request);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);//人员
  LookUp productBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT);//产品编码
  String retu = b_MaintainPlanBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
   {
     out.print(retu);
     return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = b_MaintainPlanBean.getDetailTable();
  EngineDataSet result_list = b_MaintainPlanBean.getresultDetailTable();

  productBean.regData(list,"cpid");
  personBean.regData(list,"personid");//approverID
  RowMap productRow =productBean.getLookupRow(list.getValue("cpid"));

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
	  <td nowrap >元件编码</td>
	  <td nowrap >元件名称</td>
	  <td nowrap >保养内容</td>
	  <td nowrap>计划开始时间</td>
	  <td nowrap>计划完成时间</td>
	  <td nowrap>完成情况</td>
	  <td nowrap>实际开始时间</td>
	  <td nowrap>实际完成时间</td>
	  <td nowrap>保养人</td>
	</tr>
           <%
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++){
     %>
	<tr>
	  <td id="cpbm_<%=i%>" class="td" nowrap><%=productRow.get("cpbm")%></td><!--元件编码-->
	  <td id="cpmc_<%=i%>" class="td" nowrap><%=productBean.getLookupName(list.getValue("cpid"))%></td><!--元件名称-->
	  <td id="maintain_content_<%=i%>" class="td" nowrap><%=list.getValue("maintain_content")%></td><!--保养内容-->
	  <td id="plan_startdate_<%=i%>" nowrap class=td><%=list.getValue("plan_startdate")%></td><!--计划开始时间-->
	  <td id="plan_finishdate_<%=i%>" nowrap class=td><%=list.getValue("plan_finishdate")%></td><!--计划完成时间-->
	  <td id="finish_circs_<%=i%>" nowrap class=td><%=list.getValue("finish_circs")%></td><!--完成情况-->
	  <td id="fact_startdate_<%=i%>" nowrap class=td><%=list.getValue("fact_startdate")%></td><!--实际开始时间-->
	  <td id="fact_finishdate_<%=i%>" nowrap class=td><%=list.getValue("fact_finishdate")%></td><!--实际完成时间-->
	  <td id="personid_<%=i%>" nowrap class=td><%=personBean.getLookupName(list.getValue("personid"))%></td><!--保养人-->
	</tr>
           <%  list.next();
           }
          for(; i < 5; i++){
          %>
	<tr>
	  <td class="td" nowrap>&nbsp;</td>
	  <td class="td" nowrap>&nbsp;</td>
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
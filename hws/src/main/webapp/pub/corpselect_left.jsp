<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet, engine.project.*, engine.erp.baseinfo.B_Area"%><%
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = LookupBeanFacade.getLookupDataSet(SysConstant.BEAN_AREA);
  if(list==null)
  {
    B_Area areaBean = B_Area.getInstance(request);
    list = areaBean.dsArea;
  }
  synchronized(list)
  {
    if(!list.isOpen())
      list.open();%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
<script language="javascript" src="../scripts/validate.js"></script>
</head>
<script language="javascript">
function GotoNode(pID)
{
  selectRow();
  parent.context.location.href = "corpselect_right.jsp?operate=<%=engine.erp.common.B_CorpSelect.AREA_CHANGE%>&areaid="+pID;
}
</SCRIPT>
<BODY TopMargin=0 LeftMargin=0>
<TABLE id="headbar" WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR><TD NOWRAP>地区列表</TD></TR></TABLE>
<table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
  <tr class="tableTitle"><td nowrap>编号</td><td nowrap>地区</td>
  </tr>
  <%long l = list.getInternalRow();
    int count = list.getRowCount();//行数
    list.first();
    for(int i=0; i<count; i++){
  %>
  <tr onClick="GotoNode(<%=list.getValue("dqh")%>)">
     <td class="td" nowrap><%=list.getValue("areacode")%></td>
     <td class="td" nowrap><%=list.getValue("dqmc")%></td>
  </tr>
  <%  list.next();
    }
    list.goToInternalRow(l);
  }%>
</table>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</BODY>
</Html>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.*"%><%
  engine.erp.common.B_ApproveResult approveResultBean = engine.erp.common.B_ApproveResult.getInstance(request);
  String retu = approveResultBean.doService(request, response);
  if(retu.equals("#"))
    return;
  else if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = approveResultBean.getOneTable();
  EngineDataSet detail = approveResultBean.getDetailTable();
  String curUrl = request.getRequestURL().toString();
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
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

<SCRIPT LANGUAGE="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>审批情况列表</TD>
  </TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
      <TD align="right"><%if(approveResultBean.retuUrl!=null){%><input name="button" type="button" class="button" onClick="location.href='<%=approveResultBean.retuUrl%>'" value=" 返回 " border="0"><%}%></TD>
    </TR>
  </TABLE>
<%list.first();
  for (int j=0;j<list.getRowCount();j++){
%>
<table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="95%" bgcolor="#f0f0f0" align="center">
  <tr>
   <td noWrap class="tdTitle">提交时间:</td>
   <td noWrap class="td"><%=list.getValue("tjsprq")%></td>
   <td noWrap class="tdTitle">提交部门:</td>
   <td noWrap class="td"><%=list.getValue("tjbm")%></td>
   <td noWrap class="tdTitle">提交人:</td>
   <td noWrap class="td"><%=list.getValue("tjr")%></td>
  </tr>
</table>
  <table id="tableview<%=j%>" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td height="20" nowrap>审批名称</td>
      <td nowrap>审批结果</td>
      <td nowrap>审批人</td>
      <td nowrap>审批日期</td>
      <td nowrap>审批意见</td>
    </tr>
    <%deptBean.regData(detail, "deptid");
      personBean.regData(detail, "personid");
      String deptid = list.getValue("deptid");
      if(deptid.length() > 0)
        approveResultBean.openPersonTable(deptid);

      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        String sftg = detail.getValue("sftg");
    %>
    <tr >
      <td class="td" nowrap><%=detail.getValue("spmc")%></td>
      <td class="td" nowrap><%=sftg.equals("1") ? "通过" : (sftg.equals("0") ? "未审" : "驳回")%></td>
      <td class="td" nowrap><%=sftg.equals("0") ? approveResultBean.getPersonString() : detail.getValue("spr")%></td>
      <td class="td" nowrap><%=detail.getValue("sprq")%></td>
      <td class="td" nowrap><%=detail.getValue("spyj")%></td>
    </tr>
    <%  detail.next();
      }%>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview<%=j%>',1);</SCRIPT>
  <%
    list.next();
  }
  %>
</form><%out.print(retu);%>
</body>
</html>
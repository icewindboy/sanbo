<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.*"%>
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
<BODY oncontextmenu="window.event.returnValue=true"><%
  engine.common.ApproveFacade approveFacade = engine.common.ApproveFacade.getInstance(request);
  String retu = approveFacade.doService(request, response);
  if(retu.equals("#"))
    return;
  else if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  response.setHeader("Refresh", "20");
  LookUp deptBean   = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  approveFacade.openApproveData();
  EngineDataView list = approveFacade.getMaterTable().cloneEngineDataView();
  String curUrl = request.getRequestURL().toString();
%>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>审批列表</TD>
  </TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
      <td class="td" nowrap><%String count = String.valueOf(list.getRowCount());
       int iPage = loginBean.getPageSize(); String pageSize = String.valueOf(iPage);%>
      <pc:navigator id="approveNav" recordCount="<%=count%>" pageSize="<%=pageSize%>"/></td>
      <TD align="right"><%if(approveFacade.retuUrl!=null){%><input name="button" type="button" class="button" onClick="location.href='<%=approveFacade.retuUrl%>'" value=" 返回 " border="0"><%}%></TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>审批名称</td>
      <td nowrap>审批内容</td>
      <td nowrap>提交部门</td>
      <td nowrap>提交时间</td>
      <td nowrap>提交人</td>
      <td nowrap>审批状态</td>
      <td width="35" height="20" nowrap>审批</td>
    </tr>
    <%deptBean.regData(list, "deptid");
      personBean.regData(list, "personid");
      int min = approveNav.getRowMin(request);
      int max = approveNav.getRowMax(request);
      list.goToRow(min);
      int i=min;
      for(; i<=max && i<list.getRowCount(); i++)
      {
    %>
    <tr onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getValue("spjlid")%>)">
      <td class="td" nowrap><%=list.getValue("spxmmc")%></td>
      <td class="td" nowrap><%=list.getValue("spnr")%></td>
      <td class="td" nowrap><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td class="td" nowrap><%=list.getValue("tjsprq")%></td>
      <td class="td" nowrap><%=personBean.getLookupName(list.getValue("personid"))%></td>
      <td class="td" nowrap><%=approveFacade.getAproveState(list.getRow())%></td>
      <td class="td" align="center" nowrap><input name="image2" class="img" type="image" title="审批" onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getValue("spjlid")%>)" src="../images/edit.gif" border="0">
      </td>
    </tr>
    <%  list.next();
      }
      list.close();
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form><%out.print(retu);%>
</body>
</html>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%><%@ page import="engine.dataset.*,engine.project.*"%><%
  response.setHeader("Refresh", "20");
  engine.common.ApproveFacade approveFacade = engine.common.ApproveFacade.getInstance(request);
  LookUp deptBean   = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  approveFacade.openApproveData();
  EngineDataView list = approveFacade.getMaterTable().cloneEngineDataView();
%>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
  <TR>
  <td class="tdTitle" style="text-align:left" nowrap>我的审批任务（共<%=list.getRowCount()%>项）&nbsp;&nbsp;<%if(list.getRowCount()>10){%><A HREF="approvelist.jsp?operate=<%=Operate.INIT%>&src=../pub/main.jsp"><img src="../images/bulletin.gif" border="0">更多审批...</A><%}%></td>
  <TD class="tdTitle" align="left">&nbsp;&nbsp;</TD>
  </TR>
</TABLE>
<table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="0" align="center" class="table">
  <tr class="tableTitle" height="20">
    <td nowrap>审批名称</td>
    <td nowrap>审批内容</td>
    <td nowrap>提交部门</td>
    <td nowrap>提交时间</td>
    <td nowrap>提交人</td>
    <td nowrap>审批状态</td>
  </tr>
  <%deptBean.regData(list, "deptid");
    personBean.regData(list, "personid");
    list.first();
    int i=0;
    for(; i < 10 && i<list.getRowCount(); i++)
    {
  %>
  <tr>
    <td class="td" nowrap><a href="approvelist.jsp?operate=<%=Operate.APPROVE%>&rownum=<%=list.getValue("spjlid")%>&src=../pub/main.jsp"><%=list.getValue("spxmmc")%></a></td>
    <td class="td" nowrap><%=list.getValue("spnr")%></td>
    <td class="td" nowrap><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
    <td class="td" nowrap><%=list.getValue("tjsprq")%></td>
    <td class="td" nowrap><%=personBean.getLookupName(list.getValue("personid"))%></td>
    <td class="td" nowrap><%=approveFacade.getAproveState(list.getRow())%></td>
  </tr>
  <%  list.next();
    }
    list.close();
    for(; i < 10; i++)
      out.println("<tr><td class=td nowrap>&nbsp;</td><td class=td nowrap>&nbsp;</td><td class=td nowrap>&nbsp;</td><td class=td nowrap>&nbsp;</td><td class=td nowrap>&nbsp;</td><td class=td nowrap>&nbsp;</td></tr>");
  %>
</table><SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
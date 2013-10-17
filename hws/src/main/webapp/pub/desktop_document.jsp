<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.*"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>

<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script language="javascript">
  function toDetail()
  {
  location.href='desktop_document_edit.jsp';
  }
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
</script>
<%
  engine.erp.person.B_Desktop_File b_FileBean = engine.erp.person.B_Desktop_File.getInstance(request);
  String retu = b_FileBean.doService(request, response);
  if(retu.indexOf("backList();")>-1 )
  {
    out.print(retu);
    return;
  }
  LookUp deptBean   = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT_ALL);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = b_FileBean.getOneTable();

%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="init" VALUE="">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
  <TR>
  <td class="tdTitle" style="text-align:left" nowrap>我需要阅读的文件（共<%=list.getRowCount()%>项）&nbsp;&nbsp;<%if(list.getRowCount()>10){%><A HREF="approvelist.jsp?operate=<%=Operate.INIT%>&src=../pub/main.jsp"><img src="../images/bulletin.gif" border="0">更多审批...</A><%}%></td>
  <TD class="tdTitle" align="left">&nbsp;&nbsp;</TD>
  </TR>
</TABLE>
<table id="tableview2" width="100%" border="0" cellspacing="1" cellpadding="0" align="center" class="table">
  <tr class="tableTitle" height="20">
    <td nowrap></td>
    <td nowrap>类型</td>
    <td nowrap>程度</td>
    <td nowrap>文件主题</td>
    <td nowrap>文件标题</td>
    <td nowrap>发件部门</td>
    <td nowrap>发件日期</td>
    <td nowrap>发件人</td>
  </tr>
  <%
    deptBean.regData(list, "deptid");
    personBean.regData(list, "personid");
    int i=0;
    list.first();
    for(; i < 10 && i<list.getRowCount(); i++)
    {
  %>
  <tr>
    <td class="td" nowrap>
     <input name="image2" class="img" type="image" title="查看"  onClick="sumitForm(<%=b_FileBean.VIEW_DETAIL%>,<%=i%>)" src="../images/edit.gif" border="0">
    </td>
    <td class="td" nowrap><%=list.getValue("file_type")%></td>
    <td class="td" width="80" nowrap><%=list.getValue("filelevel").equals("2")?"紧急":(list.getValue("filelevel").equals("1")?"缓慢":"普通")%></td>
     <td class="td" nowrap><%=list.getValue("topic")%></td>
     <td class="td" nowrap><%=list.getValue("caption")%></td>
     <td class="td" nowrap><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
     <td class="td" nowrap><%=list.getValue("senddate")%></td>
      <td class="td" nowrap><%=list.getValue("sendperson")%></td>
  </tr>
  <%  list.next();
    }
    list.close();
    for(; i < 10; i++)
      out.println("<tr><td class=td nowrap>&nbsp;</td><td class=td nowrap>&nbsp;</td><td class=td nowrap>&nbsp;</td><td class=td nowrap>&nbsp;</td><td class=td nowrap>&nbsp;</td><td class=td nowrap>&nbsp;</td><td class=td nowrap>&nbsp;</td><td class=td nowrap>&nbsp;</td></tr>");
  %>
</table>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview2',1);</SCRIPT>
</form>

<%out.print(retu);%>



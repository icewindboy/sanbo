<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.person.*"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>

<% engine.erp.person.B_Document_move documentBean = engine.erp.person.B_Document_move.getInstance(request);
  String retu = documentBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }

  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = documentBean.getPersonTable();

  String count = String.valueOf(list.getRowCount());
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_ALL);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  boolean isdetailview = documentBean.isdetaillook;
  boolean isAdd=documentBean.isMasterAdd;
%>
<html><head><title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function  gotopage()
{
  if(<%=isdetailview%>)
    location.href='document_move_edit.jsp';
  else
    location.href='document_move.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">收件人员列表</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
 <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <%--td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = 15; String pageSize = ""+iPage;%>
     <pc:navigator  id="self_gain_listNav" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" /--%>
     <td class="td" nowrap><b>收件人数：<%=count%></b></td>

      <td class="td" align="right">

        <input name="button2" type="button" class="button" onClick="gotopage();" value="关闭(C)" onKeyDown="return getNextElement();"></td>

    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
       <td  class="td">&nbsp;</td>
      <td nowrap height='20'>编码</td>
      <td nowrap height='20'>姓名</td>
      <td nowrap height='20'>部门</td>
      <td nowrap height='20'>职务</td>
      <td nowrap height='20'>性别</td>
      <td nowrap height='20'>已读意见</td>
    </tr>
    <%
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)   {

        RowMap personRow = personBean.getLookupRow(list.getValue("personid"));

        String b=list.getValue("personid");
        if(b.equals("-1"))
           continue;
    %>
    <tr>

     <td  class="td"> <%if(isAdd){%><input name="image32" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=documentBean.PERSON_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle"> <%}%></td>

       <td  class="td"><%=personRow.get("bm")%></td>
      <td  class="td"><%=personRow.get("xm")%></td>
      <td  class="td"><%=deptBean.getLookupName(personRow.get("deptid"))%></td>
      <td  class="td"><%=personRow.get("zw")%></td>
      <td  class="td"><%=personRow.get("sex").equals("1") ? "男" : "女"%></td>
       <td  class="td"><%=list.getValue("sm")%></td>
    </tr>
    <%  list.next();
      }

      for(; i < 15; i++){
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
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  //hideFrame('searchframe');
  showFrame('fixedQuery', true, "", true);
}
function sumitSearchFrame(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  searchform.operate.value = oper;
  searchform.submit();
}
function showSearchFrame(){
  hideFrame('fixedQuery');
  showFrame('searchframe', true, "", true);
}
</SCRIPT>

<%out.print(retu);%>
</body>
</html>
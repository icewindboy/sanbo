<%--销售分栏设置--%><%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_cancel = "op_cancel";
  String op_approve = "op_approve";
%><%
  engine.erp.sale.B_SaleColumnSet b_SaleColumnSetBean = engine.erp.sale.B_SaleColumnSet.getInstance(request);
  String pageCode = "sale_flz";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String retu = b_SaleColumnSetBean.doService(request, response);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
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

<script language="javascript">
function toDetail(){
  parent.location.href='sale_flz_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='sale_flz_bottom.jsp?operate=<%=b_SaleColumnSetBean.EDIT%>&rownum='+masterRow;
  unlockScreenWait();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">销售分栏帐列表</TD>
  </TR></TABLE>
<%
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);//显示明细或返回
    return;
  }
  EngineDataSet list = b_SaleColumnSetBean.getMaterTable();
  String curUrl = request.getRequestURL().toString();
%>
<%
  String key = "datasetlist";
  pageContext.setAttribute(key, list);
  int iPage = loginBean.getPageSize();
  String pageSize = String.valueOf(iPage);
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="60%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
      </td>
      <td class="td" nowrap align="right"><%if(hasSearchLimit){%><input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询 " onKeyDown="return getNextElement();"><%}%>
        <%if(b_SaleColumnSetBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="parent.location.href='<%=b_SaleColumnSetBean.retuUrl%>'" value=" 返回 " onKeyDown="return getNextElement();"><%}%>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="60%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width="10%" align="center">
      <%if(loginBean.hasLimits(pageCode, op_add)){%>
        <input name="image" class="img" type="image" title="新增" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
      <%}%>
      </td>
      <td  nowarp class="tabletitle"  width="20%">名称</td>
      <td  nowarp class="tabletitle" width="60%">备注</td>
    </tr>
    <%
      int i=0;
      int count = list.getRowCount();
      list.first();
      for(; i<count; i++)   {
        String rowClass ="0";
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap>
          <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
     </td>
    <td  nowarp class="td"><%=list.getValue("mc")%></td>
    <td  nowarp class="td"><%=list.getValue("bz")%></td>
    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
        out.print("<tr>");
        out.print("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%if(!b_SaleColumnSetBean.masterIsAdd()){
    int row = b_SaleColumnSetBean.getSelectedRow();
    if(row >= 0)
      out.print("showSelected('tr_"+ row +"');");
  }%>
</SCRIPT>
<% out.print(retu);%>
</body>
</html>
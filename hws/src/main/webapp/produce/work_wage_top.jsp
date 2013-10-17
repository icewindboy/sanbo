<%--工人工资汇总列表--%><%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%><%
  engine.erp.produce.B_WorkerWage workerWageBean = engine.erp.produce.B_WorkerWage.getInstance(request);
  String pageCode = "work_wage";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
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
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='work_wage_bottom.jsp?operate=<%=workerWageBean.SHOW_DETAIL%>&rownum='+masterRow;
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
    <TD NOWRAP align="center">工人工资汇总</TD>
  </TR></TABLE>
<%String retu = workerWageBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  EngineDataSet list = workerWageBean.getMaterTable();
  EngineDataSet sumList = workerWageBean.getSumTable();
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
       <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-6; String pageSize = String.valueOf(iPage);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
     </td>
       <td class="td" nowrap align="right"><%if(hasSearchLimit){%><input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
          <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <%if(workerWageBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="parent.location.href='<%=workerWageBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="parent.location.href='"+workerWageBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=20 align="center"></td>
      <td nowrap height='20'>日期</td>
      <td nowrap>部门</td>
      <td nowrap>员工</td>
      <td nowrap>出勤</td>
      <td nowrap>日班</td>
      <td nowrap>夜班</td>
      <td nowrap>请假</td>
      <td nowrap>总计件工资</td>
      <td nowrap>总金额</td>
    </tr>
    <%personBean.regData(list,"personid");
      workShopBean.regData(list,"deptid");
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++)   {
    %>
    <tr id="tr_<%=list.getRow()%>" onClick="showDetail(<%=list.getRow()%>)">
      <td  align="center" nowrap></td>
      <td class="td" nowrap><%=list.getValue("rq")%></td>
      <td class="td" nowrap><%=workShopBean.getLookupName(list.getValue("deptid"))%></td>
      <td class="td" nowrap><%=personBean.getLookupName(list.getValue("personid"))%></td>
      <td class="td" align="right" nowrap><%=list.getValue("cq")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("rb")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("yb")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("qj")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("zjjgz")%></td>
      <td class="td" align="right"nowrap><%=list.getValue("je")%></td>
    </tr>
    <%  list.next();
      }
      for(; i < iPage-1; i++){
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
    </tr>
    <%}%>
    <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" align="right" nowrap><%=sumList.isOpen() ? sumList.getValue("cqhz") : "0"%></td>
      <td class="td" align="right" nowrap><%=sumList.isOpen() ? sumList.getValue("rbhz") : "0"%></td>
      <td class="td" align="right" nowrap><%=sumList.isOpen() ? sumList.getValue("ybhz") : "0"%></td>
      <td class="td" align="right" nowrap><%=sumList.isOpen() ? sumList.getValue("qjhz") : "0"%></td>
      <td class="td" align="right" nowrap><%=sumList.isOpen() ? sumList.getValue("jjgzhz") : "0"%></td>
      <td class="td" align="right" nowrap><%=sumList.isOpen() ? sumList.getValue("jehz") : "0"%></td>
    </tr>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  <%if(hasSearchLimit){%>showFrame('fixedQuery', true, "", true);<%}%>
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
function hide()
{
  hideFrame('fixedQuery');
}
</SCRIPT>
<%if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
           <TR>
              <TD class="td" nowrap>车间</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:130">
         <%=workShopBean.getList(workerWageBean.getFixedQueryValue("deptid"))%></pc:select></TD>
              <TD class="td" nowrap>员工</TD>
              <TD nowrap class="td"><pc:select name="personid" addNull="1" style="width:130">
         <%=personBean.getList(workerWageBean.getFixedQueryValue("personid"))%></pc:select></TD>
            </TR>
           <TR>
              <TD nowrap class="td">日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="rq$a" value='<%=workerWageBean.getFixedQueryValue("rq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(rq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="rq$b" style="WIDTH: 130px" name="rq$b" value='<%=workerWageBean.getFixedQueryValue("rq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(rq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
           <pc:shortcut key="x" script='hide();'/>
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%} out.print(retu);%>
</body>
</html>
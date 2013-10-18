<%--工人工资汇总列表--%><%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%><%
  engine.erp.jit.WorkWage workerWageBean = engine.erp.jit.WorkWage.getInstance(request);
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
       int iPage = loginBean.getPageSize(); String pageSize = String.valueOf(iPage);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
     </td>
       <td class="td" nowrap align="right"><%if(hasSearchLimit){%><input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
          <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <%if(workerWageBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="location.href='<%=workerWageBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="location.href='"+workerWageBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td width=20 rowspan="2" align="center" nowrap>序号</td>
      <td rowspan="2" nowrap>部门</td>
      <td rowspan="2" nowrap>员工姓名</td>
      <td rowspan="2" nowrap>计件工资</td>
      <td rowspan="2" nowrap>计时工资</td>
      <td rowspan="2" nowrap>奖励工资</td>
      <td height="4" colspan="2" nowrap>加班补贴</td>
      <td colspan="2" nowrap>夜班津贴</td>
      <td rowspan="2" nowrap>扣款</td>
      <td rowspan="2" nowrap>总金额</td>
    </tr>
    <tr class="tableTitle">
      <td height="10" nowrap>小时</td>
      <td nowrap>金额</td>
      <td nowrap>小时</td>
      <td nowrap>金额</td>
    </tr>
    <%
      personBean.regData(list,"personid");
      workShopBean.regData(list,"deptid");
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++){
    %>
    <tr id="tr_<%=list.getRow()%>" onClick="showDetail(<%=list.getRow()%>)">
      <td  align="center" nowrap><%=i+1%></td>
      <td class="td" nowrap><%=workShopBean.getLookupName(list.getValue("deptid"))%></td>
      <td class="td" nowrap><%=personBean.getLookupName(list.getValue("personid"))%></td>
      <td class="td" align="right" nowrap><%=list.getValue("piece_wage")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("work_wage")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("bounty")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("over_hour")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("over_wage")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("night_hour")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("night_wage")%></td>
      <td class="td" align="right"nowrap><%=list.getValue("amerce")%></td>
      <td class="td" align="right"nowrap><%=list.getValue("total")%></td>
    </tr>
    <%  list.next();
      }
      for(; i < iPage-1; i++){
    %>
    <tr>
      <td  align="center" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" align="right" nowrap>&nbsp;</td>
      <td class="td" align="right" nowrap>&nbsp;</td>
      <td align="right" nowrap class="td">&nbsp;</td>
      <td align="right" nowrap class="td">&nbsp;</td>
      <td align="right" nowrap class="td">&nbsp;</td>
      <td align="right" nowrap class="td">&nbsp;</td>
      <td align="right" nowrap class="td">&nbsp;</td>
      <td align="right" nowrap class="td">&nbsp;</td>
      <td class="td" align="right"nowrap></td>
    </tr>
    <%}%>
    <%
      String ttotal = sumList.isOpen()?sumList.getValue("ttotal"):"";
      String twork_wage = sumList.isOpen()?sumList.getValue("twork_wage"):"";
      String tover_wage = sumList.isOpen()?sumList.getValue("tover_wage"):"";
      String tnight_wage = sumList.isOpen()?sumList.getValue("tnight_wage"):"";
      String tbounty = sumList.isOpen()?sumList.getValue("tbounty"):"";
      String tover_hour = sumList.isOpen()?sumList.getValue("tover_hour"):"";
      String tnight_hour = sumList.isOpen()?sumList.getValue("tnight_hour"):"";
      String tpiece_wage = sumList.isOpen()?sumList.getValue("tpiece_wage"):"";
      String tamerce = sumList.isOpen()?sumList.getValue("tamerce"):"";
    %>
    <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" align="right" nowrap><%=tpiece_wage%></td>
      <td class="td" align="right" nowrap><%=twork_wage%></td>
      <td class="td" align="right" nowrap><%=tbounty%></td>
      <td class="td" align="right" nowrap><%=tover_hour%></td>
      <td class="td" align="right" nowrap><%=tover_wage%></td>
      <td class="td" align="right" nowrap><%=tnight_hour%></td>
      <td class="td" align="right" nowrap><%=tnight_wage%></td>
      <td class="td" align="right" nowrap><%=tamerce%></td>
      <td class="td" align="right" nowrap><%=ttotal%></td>
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
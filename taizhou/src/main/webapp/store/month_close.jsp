<%--月结页面--%>
<%
/**
 * 02.25 21:01 新增 新增加上判断那个月是当前应该月结的月的判断,并且给tr加了id号.为的是用顔色标出当前要月结的项
 */
%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%><%
  engine.erp.store.B_MonthClose monthCloseBean = engine.erp.store.B_MonthClose.getInstance(request);
  String pageCode = "month_close";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String loginId = monthCloseBean.loginId;
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
  location.href='month_close.jsp';
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
    <TD NOWRAP align="center">月末结账</TD>
  </TR></TABLE>
<%String retu = monthCloseBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  //得到kc_yj表中的存储在hash表中的记录资料.
  EngineDataSet list = monthCloseBean.getOneTable();
  //02.25 16:26 新增 取得应该是当前月结月份的操作. yjg
  EngineDataSet dsCmcd = monthCloseBean.dsCurrentMonthCloseDate;
  //HtmlTableProducer monthCloseTable = monthCloseBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="100%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap></td>
    </tr>
  </table>
  <table id="tableview1" width="27%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td>年月</td>
      <td>是否结帐</td>
    </tr>
    <%list.first();
      int i=0;
      int j=-1; //j是用来记录月结月的行号的.

      int count = list.getRowCount();
      for(; i<count; i++){
        list.goToRow(i);
        String sfyj =list.getValue("sfyj");//得到是否月结字段的标志.在下面生成html的时候,
        //要依照每一条记录的此标志来决定是不是须要给他双击事件或显示月结按钮,
        //以便使这条记录能提交表单
    %>
    <tr ID="tr<%=i%>">
    <%--02.25 21:01 新增 新增加上判断那个月是当前应该月结的月的判断,并且给tr加了id号.为的是用顔色标出当前要月结的项 yjg--%>
    <%
      if ( dsCmcd.getValue("nf").equals(list.getValue("nf")) && dsCmcd.getValue("yf").equals(list.getValue("yf")))
        j = i;
    %>
      <td align="center">
      <%=(list.getValue("nf") + "." + list.getValue("yf"))%>
      </td>
      <td align="center">
      <%if(!sfyj.equals("0")){%>
       √
        <%}%>
      </td>
    </tr>
    <%  list.next();
      }
    %>
  </table>
  <table width="6%" border="0" align="center">
    <tr>
      <td>
       <input type="submit" name="Submit" class="button" value=" 月结 "
       onClick="if ( confirm('确认月结吗?') ) {sumitForm(<%=Operate.POST%>,<%=list.getRow()%>);}">
    </td>
	<td class="td" nowrap align="right">
        <%if(monthCloseBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="location.href='<%=monthCloseBean.retuUrl%>'" value=" 返回 " onKeyDown="return getNextElement();"><%}%>
      </td>
    </tr>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
  initDefaultTableRow('tableview1',1);
  showSelected("tr" + "<%=j%>");
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
</SCRIPT>
<%out.print(retu);%>
</body>
</html>
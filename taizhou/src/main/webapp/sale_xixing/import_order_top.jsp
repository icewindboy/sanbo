<%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_cancel = "op_cancel";
  String op_approve = "op_approve";
%><%
  engine.erp.sale.xixing.B_ImportOrder b_ImportOrderBean = engine.erp.sale.xixing.B_ImportOrder.getInstance(request);
  String pageCode = "send_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  synchronized(b_ImportOrderBean){
  String retu = b_ImportOrderBean.doService(request, response);
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
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript">
function toDetail(){
  parent.location.href='sale_order_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='import_order_buttom.jsp?operate=<%=b_ImportOrderBean.EDIT%>&rownum='+masterRow;
  unlockScreenWait();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function checkTr(row)
{
  if(form1.sel.length+''=='undefined')
    checkRow(form1.sel.checked);
  else
    checkRow(form1.sel[row].checked);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">销售合同列表</TD>
  </TR></TABLE>
<%
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);//显示明细或返回
    return;
  }
  EngineDataSet list = b_ImportOrderBean.getMaterTable();
  HtmlTableProducer table = b_ImportOrderBean.masterProducer;
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
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
      </td>
      <td class="td" nowrap align="right">
      <%if(hasSearchLimit){%>
      <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
     <pc:shortcut key="q" script='showFixedQuery();'/>
     <%}%>
        <%if(b_ImportOrderBean.retuUrl!=null){
         String ret = "parent.location.href='"+b_ImportOrderBean.retuUrl+"'";
        %>
      <input name="button22" type="button" class="button" onClick="parent.location.href='<%=b_ImportOrderBean.retuUrl%>'" value="返回(C)" onKeyDown="return getNextElement();">
      <pc:shortcut key="c" script='<%=ret%>'/>
      <%}%>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <%table.printTitle(pageContext, "height='20'");%><!--打印表头-->
    </tr>
    <%
      int i=0;
      int count = list.getRowCount();
      list.first();
      for(; i<count; i++)   {
        //&#$
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0")){
          isInit = true;
        }
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String loginId=b_ImportOrderBean.loginId;
        String sprid=list.getValue("sprid");
        String rowzt =list.getValue("zt");
        String czyid = list.getValue("czyid");
        boolean isCancer=rowzt.equals("1")&& loginId.equals(sprid);
        if(sprid.equals(""))
          isCancer = rowzt.equals("1")&&loginId.equals(czyid);
        //boolean cancer=rowzt.equals("0")||rowzt.equals("8")||rowzt.equals("4");
        //boolean iscancancer=b_ImportOrderBean.isCanCancel(list.getValue("htid"));
       // boolean iscanover = b_ImportOrderBean.isCanOver(list.getValue("htid"));
    %>
    <tr id="tr_<%=list.getRow()%>"  onClick="showDetail(<%=list.getRow()%>)">
      <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("</tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%if(!b_ImportOrderBean.masterIsAdd()){
    int row = b_ImportOrderBean.getSelectedRow();
    if(row >= 0)
      out.print("showSelected('tr_"+ row +"');");
  }%>
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
<%if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD nowrap class="td">合同日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="a$htrq$a" value='<%=b_ImportOrderBean.getFixedQueryValue("a$htrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
              <A href="#"><IMG title=选择日期 onClick="selectDate(a$htrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="a$htrq$b" style="WIDTH: 130px" name="a$htrq$b" value='<%=b_ImportOrderBean.getFixedQueryValue("a$htrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(a$htrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap class="td">有效期始</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="a$ksrq$a" value='<%=b_ImportOrderBean.getFixedQueryValue("a$ksrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(a$ksrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
                <TD align="center" nowrap class="td">--</TD>
                <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="a$ksrq$b" value='<%=b_ImportOrderBean.getFixedQueryValue("a$ksrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                 <A href="#"><IMG title=选择日期 onClick="selectDate(a$ksrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
               <TD nowrap class="td">有效期止</TD>
               <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="a$jsrq$a" value='<%=b_ImportOrderBean.getFixedQueryValue("a$jsrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(a$jsrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
               <TD align="center" nowrap class="td">--</TD>
               <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="a$jsrq$b" value='<%=b_ImportOrderBean.getFixedQueryValue("a$jsrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(a$jsrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
             </TR>
             <TR>
               <TD class="td" nowrap>合同编号</TD>
               <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="a$htbh" name="a$htbh" value='<%=b_ImportOrderBean.getFixedQueryValue("a$htbh")%>' maxlength='13' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>购货单位</TD>
              <TD  nowrap class="td">
                <input type="hidden" name="a$dwtxid" value='<%=b_ImportOrderBean.getFixedQueryValue("a$dwtxid")%>'>
                <input type="text" name="salererName" value='<%=b_ImportOrderBean.getFixedQueryValue("salererName")%>' style="width:130" class="edline" readonly>
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=a$dwtxid&srcVar=salererName','fieldVar=a$dwtxid&fieldVar=dwmc',fixedQueryform.a$dwtxid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="a$dwtxid.value='';salererName.value='';">
             </TD>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center">
                <% String ss = "sumitFixedQuery("+Operate.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script="<%=ss%>" />
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                 <pc:shortcut key="x" script="hideFrame('fixedQuery')" />
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%} out.print(retu);}%>
</body>
</html>
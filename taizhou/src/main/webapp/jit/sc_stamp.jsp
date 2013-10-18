<%--车间流转单--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%>
<%
  engine.erp.jit.B_Stamp B_StampBean = engine.erp.jit.B_Stamp.getInstance(request);
  String pageCode = "sc_stamp";
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
function toDetail(){
  location.href='sc_stamp_edit.jsp';
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function showAddFrame(){
  hideFrame('fixedQuery');
  showFrame('detailDiv', true, "", true);
   }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">印花记录</TD>
  </TR>
</TABLE>
<%String retu = B_StampBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = B_StampBean.getMaterTable();
  HtmlTableProducer table = B_StampBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  String loginId = B_StampBean.loginId;

%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = String.valueOf(iPage);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right">

        <%if(B_StampBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="location.href='<%=B_StampBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="location.href='"+B_StampBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
    </tr>
  </table>
 <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"><%if(loginBean.hasLimits(pageCode, op_add)){%>
        <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
         <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/><%}%></td>
      <td class="td" align="center" nowrap>制单日期</td>
      <td class="td" align="center" nowrap>印花总数</td>
      <td class="td" align="center" nowrap>新厂总数</td>
      <td class="td" align="center" nowrap>老厂总数</td>
      <td class="td" align="center" nowrap>地毯</td>
      <td class="td" align="center" nowrap>半高档</td>
      <td class="td" align="center" nowrap>普通</td>
      <td class="td" align="center" nowrap>拉舍尔</td>
      <td class="td" align="left" nowrap>备注</td>
    </tr>
    <%
      int i=0;
      int count = list.getRowCount();
      list.first();
      for(; i<count; i++){
        list.goToRow(i);
        /*
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0"))
          isInit = true;
        //提交审批是否只有制单人可以提交
        String zdrid = list.getValue("zdrid");
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String sprid = list.getValue("sprid");
        String zt=list.getValue("zt");
        boolean isCancel = zt.equals("1");
        isCancel = isCancel && loginId.equals(sprid);
        boolean isComplete  = !list.getValue("zt").equals("8")&&loginBean.hasLimits(pageCode, op_over);//有强制完成的权限;
        boolean isShow = loginId.equals(zdrid) && isInit;
        */
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="selectRow();">
      <td class="td" align="center" nowrap>
        <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
      </td>
      <td class="td" align="center" nowrap><%=list.getValue("zdrq")%></td>
      <td class="td" align="center" nowrap><%=list.getValue("yhzsl")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("xczsl")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("lczsl")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("dtan")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("bgad")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("putn")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("lsr")%></td>
      <td class="td" align="left" nowrap><%=list.getValue("memo")%></td>
    </tr>
    <%  list.next();
      }
     %>
      <%
      for(; i < iPage; i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%if(!B_StampBean.masterIsAdd()){
    int row = B_StampBean.getSelectedRow();
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
function hide()
{
  hideFrame('fixedQuery');
}
//产品编码选择
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cjlzdid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product', obj.value);
}
//产品名称改变时的选择
function productNameSelect(obj,i)
{
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cjlzdid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product',obj.value);
}
</SCRIPT>
<%if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
        <TABLE cellspacing=3 cellpadding=0 border=0>
            <%B_StampBean.table.printWhereInfo(pageContext);%>

              <TD nowrap colspan=2 height=30 align="center"><INPUT name="button" type="button" class="button" title="查询(ALT+F)"   value="查询(F)" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)"  onKeyDown="return getNextElement();">
            <pc:shortcut key="f" script='<%="sumitFixedQuery("+Operate.FIXED_SEARCH+")"%>'/>
                <INPUT name="button2" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)"  onClick="hideFrame('fixedQuery')"  onKeyDown="return getNextElement();">
            <pc:shortcut key="t" script="hideFrame('fixedQuery')"/>
              </TD>

         </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%} out.print(retu);%>
</body>
</html>
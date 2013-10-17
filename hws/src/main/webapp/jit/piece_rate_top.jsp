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
  engine.erp.jit.PieceRate pieceRateBean = engine.erp.jit.PieceRate.getInstance(request);
  String pageCode = "piece_rate";
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
  location.href='piece_rate_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  location.href='piece_rate_bottom.jsp?operate=<%=pieceRateBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
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
    <TD NOWRAP align="center">计件工作量列表</TD>
  </TR>
</TABLE>
<%String retu = pieceRateBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//通过工作组id得到工作组名称
  EngineDataSet list = pieceRateBean.getMaterTable();
  HtmlTableProducer table = pieceRateBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  String loginId = pieceRateBean.loginId;
  if (list.isOpen()) list.refresh(); else list.openDataSet();
  prodBean.regData(list,"cpid");
  deptBean.regData(list,"deptid");
  workGroupBean.regConditionData(list,"gzzid");
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = String.valueOf(iPage);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right"><%if(hasSearchLimit){%><input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <%if(pieceRateBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="location.href='<%=pieceRateBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="location.href='"+pieceRateBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
    </tr>
  </table>
 <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"><%if(loginBean.hasLimits(pageCode, op_add)){%>
        <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
      <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/><%}%></td>
      <%table.printTitle(pageContext, "height='20'");%>
      <%--td nowrap><%=table.getFieldInfo("piece_code").getFieldname()%></td>
      <td nowrap><%=table.getFieldInfo("piece_date").getFieldname()%></td>
      <td nowrap><%=table.getFieldInfo("deptid").getFieldname()%></td>
      <td nowrap><%=table.getFieldInfo("gzzid").getFieldname()%></td>
      <%
        for (int i=0;i<table.getFieldInfo("cpid").getShowFieldNames().length-2;i++)
          out.println("<td nowrap>"+table.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
      %>
      <td nowrap><%=table.getFieldInfo("zt").getFieldname()%></td--%>
    </tr>
    <%
      BigDecimal t_piece_num = new BigDecimal(0), t_proc_num = new BigDecimal(0);
      String piece_num = "0", proc_num ="0";
      int i=0;
      int count = list.getRowCount();
      list.first();
      for(; i<count; i++){
        list.goToRow(i);
        piece_num = list.getValue("piece_num");
        String zt=list.getValue("zt");
        boolean isInit = false;
        String rowClass =zt;
        if(rowClass.equals("0"))
          isInit = true;
        String zdrid = list.getValue("zdrid");
        String sprid = list.getValue("sprid");
        boolean isCancel = zt.equals("1");
        isCancel = isCancel && loginId.equals(sprid);
        if(pieceRateBean.isDouble(piece_num))
          t_piece_num = t_piece_num.add(new BigDecimal(piece_num));
        if(pieceRateBean.isDouble(proc_num))
          t_proc_num = t_proc_num.add(new BigDecimal(proc_num));
        boolean isComplete  = !list.getValue("zt").equals("8")&&loginBean.hasLimits(pageCode, op_over);//有强制完成的权限;
        boolean isShow = loginId.equals(zdrid) && isInit;
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)"  onClick="selectRow();" >
      <td <%=rowClass%> align="center" nowrap>
        <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
        <%if(isShow){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
        <%if(isCancel){%><input name="image2" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=pieceRateBean.CANCEL_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
        <%if(isComplete&&zt.equals("1")){%><input name="image5" class="img" type="image" title='完成' onClick="if(confirm('是否强制完成该纪录？'))sumitForm(<%=pieceRateBean.COMPLETE%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>
      </td>
      <%table.printCells(pageContext, rowClass);%>
      <%--td <%=rowClass%> nowrap><%=list.getValue("piece_code")%></td>
      <td <%=rowClass%> nowrap><%=list.getValue("piece_date")%></td>
      <td <%=rowClass%> nowrap><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td <%=rowClass%> nowrap><%=workGroupBean.getLookupName(list.getValue("gzzid"))%></td>
      <%RowMap  prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td <%=rowClass%> nowrap><%=prodRow.get("cpbm")%></td>
      <td <%=rowClass%> nowrap><%=prodRow.get("product")%></td>

      <td <%=rowClass%> nowrap><%=list.getValue("zt").equals("0")?"未完成":"完成"%></td--%>
    </tr>
    <%  list.next();
        //i=count+1;
      }
      for(; i < iPage; i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td></tr>");
      }
     %>
      <%--tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class="td" nowrap>&nbsp;</td>
     <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap align=right></td>
      <td class="td" nowrap></td>
      </tr>
      <%
      for(; i < iPage; i++){
      %>
       <tr>
      <td class="tdTitle" nowrap></td>
      <td class="td" nowrap>&nbsp;</td>
     <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap align="right"></td>
      <td class="td" nowrap align=right></td>
      <td class="td" nowrap></td>
      </tr%>
      <%
      }
    --%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%if(!pieceRateBean.masterIsAdd()){
    int row = pieceRateBean.getSelectedRow();
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
function productCodeSelect(obj,srcVars)
{
  ProdCodeChange(document.all['prod'], obj.form.name, srcVars,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function productNameSelect(obj,srcVars)
{
  ProdNameChange(document.all['prod'], obj.form.name, srcVars,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
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
            <%pieceRateBean.table.printWhereInfo(pageContext);%>
              <TD nowrap colspan=4 height=30 align="center"><INPUT name="button" type="button" class="button" title="查询(ALT+F)"   value="查询(F)" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)"  onKeyDown="return getNextElement();">
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
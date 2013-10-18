<%--工人工作量（按工作组输入）列表--%><%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%
  engine.erp.produce.B_WorkloadGroup workloadGroupBean = engine.erp.produce.B_WorkloadGroup.getInstance(request);
  String pageCode = "workload_group";
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
  parent.location.href='workload_group_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='workload_group_bottom.jsp?operate=<%=workloadGroupBean.SHOW_DETAIL%>&rownum='+masterRow;
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
    <TD NOWRAP align="center">工人工作量（按工作组输入）列表</TD>
  </TR></TABLE>
<%String retu = workloadGroupBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工艺路线类型
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//通过工作组id得到工作组名称
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  EngineDataSet list = workloadGroupBean.getMaterTable();
  HtmlTableProducer table = workloadGroupBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-7; String pageSize = String.valueOf(iPage);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right"><%if(hasSearchLimit){%><input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
         <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <%if(workloadGroupBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="parent.location.href='<%=workloadGroupBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="parent.location.href='"+workloadGroupBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"><%if(loginBean.hasLimits(pageCode, op_add)){%>
        <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
      <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/><%}%></td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%BigDecimal t_zsl = new BigDecimal(0),t_zhssl = new BigDecimal(0),t_zgf = new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++)   {
        list.goToRow(i);
        String sl = list.getValue("sl");
        if(workloadGroupBean.isDouble(sl))
          t_zsl = t_zsl.add(new BigDecimal(sl));
       String zgf = list.getValue("zgf");
       if(workloadGroupBean.isDouble(zgf))
          t_zgf = t_zgf.add(new BigDecimal(zgf));
        String zt = list.getValue("zt");
        boolean isComplete  = !zt.equals("8") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td  align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%if(isComplete){%><input name="image5" class="img" type="image" title='完成' onClick="if(confirm('是否强制完成该纪录？'))sumitForm(<%=workloadGroupBean.COMPLETE%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>
      </td>
      <%table.printCells(pageContext, "class=td");%>
    </tr>
    <%  list.next();
      }
      i=count+1;
    %>
     <tr>
     <td class="tdTitle" nowrap>合计</td>
     <td class="td" nowrap>&nbsp;</td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
    <td class="td" nowrap></td>
     <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
     <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zgf%>' readonly></td>
     <td class="td" nowrap></td>
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
      <%
      for(; i < iPage; i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%if(!workloadGroupBean.masterIsAdd()){
    int row = workloadGroupBean.getSelectedRow();
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
           <TD class="td" nowrap>单据号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="djh" name="djh" value='<%=workloadGroupBean.getFixedQueryValue("djh")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>车间</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:130">
         <%=workShopBean.getList(workloadGroupBean.getFixedQueryValue("deptid"))%></pc:select></TD>
            </TR>
           <TR>
              <TD nowrap class="td">日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="rq$a" value='<%=workloadGroupBean.getFixedQueryValue("rq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(rq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="rq$b" style="WIDTH: 130px" name="rq$b" value='<%=workloadGroupBean.getFixedQueryValue("rq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(rq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>工作组</TD>
              <TD nowrap class="td"><pc:select name="gzzid" addNull="1" style="width:130">
              <%=workGroupBean.getList(workloadGroupBean.getFixedQueryValue("gzzid"))%></pc:select></TD>
              <TD class="td" nowrap>班次</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="bc" name="bc" value='<%=workloadGroupBean.getFixedQueryValue("bc")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
             <TR>
              <TD class="td" nowrap>经手人</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="jdr" name="jdr" value='<%=workloadGroupBean.getFixedQueryValue("jdr")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>机台</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="jt" name="jt" value='<%=workloadGroupBean.getFixedQueryValue("jt")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
            <TR>
             <TR>
            <TD class="td" nowrap>员工</TD>
              <TD nowrap class="td"><pc:select name="bmgzlid$personid" addNull="1" style="width:130">
              <%=personBean.getList(workloadGroupBean.getFixedQueryValue("bmgzlid$personid"))%></pc:select></TD>
              <TD nowrap class="td">产品名称</TD>
               <td nowrap class="td"><input class="EDLine" style="WIDTH:130px" name="product" value='<%=workloadGroupBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="gzzgzlid$cpid" value="<%=workloadGroupBean.getFixedQueryValue("gzzgzlid$cpid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=gzzgzlid$cpid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.gzzgzlid$cpid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="gzzgzlid$cpid.value='';product.value='';">
            </TD>
            </TR>
             <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = workloadGroupBean.getFixedQueryValue("zt");%>
                <input type="radio" name="zt" value=""<%=zt.equals("")?" checked" :""%>>
                全部
                <input type="radio" name="zt" value="0"<%=zt.equals("0")?" checked" :""%>>
                未完成
                <input type="radio" name="zt" value="8"<%=zt.equals("8")?" checked" :""%>>
                已完成
                </TD>
            </TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+ Operate.FIXED_SEARCH +",-1)"%>'/>
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
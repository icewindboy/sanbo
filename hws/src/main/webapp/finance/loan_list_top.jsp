<%--销售发票管理--%><%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*,engine.project.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_cancel = "op_cancel";
%>
<%
  engine.erp.finance.B_LoanMange B_LoanMangeBean = engine.erp.finance.B_LoanMange.getInstance(request);
  String pageCode = "loan_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//引用结算方式
  engine.project.LookUp bankBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK);
  engine.project.LookUp bankaccountBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK_ACCOUNT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);

  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  synchronized(B_LoanMangeBean){
    String retu = B_LoanMangeBean.doService(request, response);
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
  // 转到主从明细
  parent.location.href='loan_list_edit.jsp';
}
function showDetail(masterRow){
  //单击主表的一行激活的操作
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='loan_list_buttom.jsp?operate=<%=B_LoanMangeBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function showFixedQuery(){
   showFrame('fixedQuery', true, "", true);//显示层
}
</script>

<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">贷款情况管理</TD>
  </TR>
</TABLE>
<%
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = B_LoanMangeBean.getMaterTable();//主表数据集
  HtmlTableProducer table = B_LoanMangeBean.masterProducer;//主表数据的表格打印
  String curUrl = request.getRequestURL().toString();
  RowMap m_RowInfo =B_LoanMangeBean.getMasterRowinfo();


%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
          <%
            String key = "saleinvoce";
            pageContext.setAttribute(key, list);
            int iPage = loginBean.getPageSize()-6;
            String pageSize = String.valueOf(iPage);
          %>
     <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
      <td class="td" nowrap align="right">
        <%
          if(hasSearchLimit){
        %>
        <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script="showFixedQuery()" />
       <%}%>
        <%if(B_LoanMangeBean.retuUrl!=null){String ret = "parent.location.href='"+B_LoanMangeBean.retuUrl+"'";%>
       <input name="button22" type="button" class="button" onClick="parent.location.href='<%=B_LoanMangeBean.retuUrl%>'" value="返回(X)" onKeyDown="return getNextElement();">
      <pc:shortcut key="x" script="<%=ret%>" />
      <%}%>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
    <td>
      <%if(loginBean.hasLimits(pageCode, op_add)){
       String add = "sumitForm("+Operate.ADD+",-1)";
       %>
        <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
        <pc:shortcut key="a" script='<%=add%>'/>
      <%}%>
   </td>
      <td nowrap>单据号</td>
      <td nowrap>贷款单据号</td>
      <td nowrap height='20'>银行</td>
      <td nowrap height='20'>发生日期</td>
      <td nowrap height='20'>贷款金额</td>
    <%table.printTitle(pageContext, "height='20'");%><!--打印表头-->
    </tr>
    <%
      bankBean.regData(list, "yhid");
      //bankaccountBean.regConditionData("yhid", new String[]{});
      personBean.regData(list, "personid");
      bankaccountBean.regData(list, "yhzhid");
      int count = list.getRowCount();//行数
      int i=0;
      list.first();
      for(; i<count; i++)   {
        boolean isInit = false;
        String rowClass =list.getValue("state");
        if(rowClass.equals("0")){
          isInit = true;
          rowClass ="class=td";
        }
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String loginId=B_LoanMangeBean.loginId;
        String approveid=list.getValue("approveid");
        String state =list.getValue("state");
        String creatorid = list.getValue("creatorid");

        boolean isCancer=state.equals("1") && loginId.equals(approveid);
        boolean cancer=state.equals("0")||state.equals("8")||state.equals("4");
        boolean isCanDelete = loginBean.hasLimits(pageCode, op_delete)&&creatorid.equals(loginId);

        boolean cansubmit=false;
        boolean submitType = B_LoanMangeBean.submitType;//用于判断true=仅制定人可提交,false=有权限人可提交

        if(submitType&&creatorid.equals(loginId))
          cansubmit=true;
        else if(!submitType)
          cansubmit=true;

        boolean isEnd = state.equals("1")||state.equals("9")||state.equals("8");
        String edClass = isEnd ? "class=edline" : "class=edbox";
        String detailClass = isEnd ? "class=edline" : "class=edFocused";
        String detailClass_r = isEnd ? "class=edline" : "class=edFocused_r";
        String readonly = isEnd ? " readonly" : "";


        String loanid = list.getValue("loanid");
        String yearate = list.getValue("yearate");

    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
       <td <%=rowClass%> align="center" nowrap>
           <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%if(isCanDelete&&state.equals("0")&&loginId.equals(creatorid)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0"><%}%>
      <%if(isInit&&cansubmit){%>
           <input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
     <%if(isCancer){%>
          <input name="image3" class="img" type="image"  title='取消审批' onClick="sumitForm(<%=B_LoanMangeBean.CANCER_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
     <%
       if((state.equals("1"))&&(loginBean.hasLimits(pageCode, op_over))){
         String dd = B_LoanMangeBean.getBX(loanid,yearate);
         if(dd.equals("over")){
      %>
          <input name="image3" class="img" type="image"      title='完成' onClick="if(confirm('完成后不能修改,确认要完成吗？'))sumitForm(<%=B_LoanMangeBean.INVOICE_OVER%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0">
      <%}}%>
      </td>

      <td nowrap <%=rowClass%> ><%=list.getValue("djh")%></td>
      <td nowrap <%=rowClass%> ><%=list.getValue("loancode")%></td>
      <td nowrap <%=rowClass%> ><%=bankBean.getLookupName(list.getValue("yhid"))%></td>
      <td nowrap <%=rowClass%> ><%=list.getValue("createdate")%></td>
      <td nowrap <%=rowClass%> align="right"><%=list.getValue("loanfund")%></td>
     <%table.printCells(pageContext, rowClass);%>
   </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
        out.print("<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");
        table.printBlankCells(pageContext, "class=td");
        out.print("</tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%
  if(!B_LoanMangeBean.masterIsAdd()){
    int row = B_LoanMangeBean.getSelectedRow();
    if(row >= 0)
      out.print("showSelected('tr_"+ row +"');");//打印标识颜色
  }
%>
function sumitFixedQuery(oper)
{
  //执行查询,提交表单
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
</SCRIPT>
<%if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;">
    <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
    <div class="queryPop" id="fixedQuery" name="fixedQuery">
      <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
      <TABLE cellspacing=3 cellpadding=0 border=0>
        <TR>
          <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <tr>
              <td noWrap class="tdTitle">&nbsp;贷款单号</td>
              <td noWrap class="td"><INPUT TYPE="TEXT"  NAME="loancode" VALUE="<%=B_LoanMangeBean.getFixedQueryValue("loancode")%>"  style="WIDTH:180"  class="edbox" >
              </td>
            </TR>
            <TR>
              <td noWrap class="tdTitle" align="center">&nbsp;单据号码&nbsp;</td>
              <td noWrap class="td"><input type="text" name="djh$a" value='<%=B_LoanMangeBean.getFixedQueryValue("djh$a")%>' maxlength='50' style="width:180" class="edbox"></td>
              <td noWrap class="tdTitle" align="center">--</td>
              <td noWrap class="td"><input type="text" name="djh$b" value='<%=B_LoanMangeBean.getFixedQueryValue("djh$b")%>' maxlength='50' style="width:180" class="edbox"></td>
            </TR>
            <tr>
            <td noWrap class="tdTitle">银行</td>
            <td noWrap class="td" >
            <pc:select name="yhid" addNull="1" style="width:180"  onSelect="yhchange();" >
            <%=bankBean.getList(B_LoanMangeBean.getFixedQueryValue("yhid"))%> </pc:select>
             </td>
            <td noWrap class="tdTitle">帐号</td>
            <td noWrap class="td" >
            <pc:select name="yhzhid" addNull="1" style="width:180" >
            <%=bankaccountBean.getList(B_LoanMangeBean.getFixedQueryValue("yhzhid"))%> </pc:select>
             </td>
             </tr>
            <TR>
              <TD nowrap class="tdTitle">贷款日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 180px" name="loandate$a" value='<%=B_LoanMangeBean.getFixedQueryValue("loandate$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(loandate$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 180px" name="loandate$b" value='<%=B_LoanMangeBean.getFixedQueryValue("loandate$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(loandate$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <td nowrap class="tdTitle">贷款金额</td>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 180px" name="loanfund$a" value='<%=B_LoanMangeBean.getFixedQueryValue("loanfund$a")%>' maxlength='20'  onKeyDown="return getNextElement();">
              </TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 180px" name="loanfund$b" value='<%=B_LoanMangeBean.getFixedQueryValue("loanfund$b")%>' maxlength='20' onKeyDown="return getNextElement();">
               </TD>
            </TR>
             <TR>
                <td nowrap class="tdTitle">制单人</td>
                <td noWrap class="td"><input type="text" name="creator" value='<%=B_LoanMangeBean.getFixedQueryValue("creator")%>' maxlength='6' style="width:180" class="edbox"></td>
                <td noWrap class="tdTitle">业务员</td>
                <td   noWrap class="td">
                <pc:select name="personid" addNull="1" style="width:180">
                  <%=personBean.getList(B_LoanMangeBean.getFixedQueryValue("personid"))%> </pc:select>
                 </td>
            </TR>
            <tr>
              <td noWrap class="tdTitle">&nbsp;摘要</td>
              <td noWrap colspan="3" class="td"><INPUT TYPE="TEXT"  NAME="summary" VALUE="<%=B_LoanMangeBean.getFixedQueryValue("summary")%>"  style="WIDTH:430"  class="edbox" >
              </td>
            </TR>
            <TR>
              <TD class="tdTitle" nowrap>审核情况</TD>
              <TD colspan="3" nowrap class="td">
                <%
                  String [] zt = B_LoanMangeBean.zt;
                  String zt0="";
                  String zt9="";
                  String zt1="";
                  String zt8="";
                  String zt4="";
                  for(int k=0;k<zt.length;k++)
                  {
                    if(zt[k].equals("0"))
                      zt0 = "checked";
                    else if(zt[k].equals("9"))
                      zt9 = "checked";
                    else if(zt[k].equals("1"))
                      zt1 = "checked";
                    else if(zt[k].equals("8"))
                      zt8 = "checked";
                    else if(zt[k].equals("4"))
                      zt4 = "checked";
                  }
                %>
                <input type="checkbox" name="zt" value="0" <%=zt0%>>未审
                <input type="checkbox" name="zt" value="9" <%=zt9%>>审批中
                <input type="checkbox" name="zt" value="1" <%=zt1%>>已审
                <input type="checkbox" name="zt" value="8" <%=zt8%>>完成
            </TD>
            </TR>
            <TR>
              <%if(loginBean.hasLimits(pageCode,op_add)){ String qu = "sumitFixedQuery("+B_LoanMangeBean.FIXED_SEARCH+",-1)";%>
              <TD nowrap colspan=3 height=30 align="center"><INPUT class="button" title = "查询" onClick="sumitFixedQuery(<%=B_LoanMangeBean.FIXED_SEARCH%>)" type="button" value=" 查询(Q) " name="button" onKeyDown="return getNextElement();"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
              <%if(loginBean.hasLimits(pageCode,op_add)){ String clo = "hideFrame('fixedQuery')";%>
                <TD nowrap colspan=3 height=30 align="center"><INPUT class="button" title = "关闭"  onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(C) " name="button2" onKeyDown="return getNextElement();"><pc:shortcut key="c" script='<%=clo%>'/><%}%>
              </TD>
            </TR>
          </TABLE></TD>
        </TR>
      </TABLE>
    </DIV>
  </form>
<%
 out.print(retu);}}%>
</body>
</html>
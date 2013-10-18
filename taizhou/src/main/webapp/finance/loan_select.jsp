<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate ,java.util.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
  private boolean isMultiSelect = false;
  private String inputName[] = null;
  private String fieldName[] = null;
  private String methodName = null;
%>
<%
  engine.erp.finance.Select_Loan Select_LoanBean = engine.erp.finance.Select_Loan.getInstance(request);
  String pageCode = "rtn_loan_list";
  if(!loginBean.hasLimits(pageCode, request, response))
  return;

  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//引用结算方式
  engine.project.LookUp bankBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK);
  engine.project.LookUp bankaccountBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK_ACCOUNT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);


  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String retu = Select_LoanBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }


  String operate = request.getParameter("operate");
  if(operate !=null && operate.equals(String.valueOf(Operate.INIT)))
  {
    srcFrm = request.getParameter("srcFrm");
    multiIdInput = request.getParameter("srcVar");
    isMultiSelect = request.getParameter("multi") != null && request.getParameter("multi").equals("1");
    inputName = request.getParameterValues("srcVar");
    fieldName = request.getParameterValues("fieldVar");
    methodName = request.getParameter("method");
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = Select_LoanBean.getOneTable();
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
function sumitForm(oper){
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function selectCorp(row)
{
<%if(isMultiSelect){%>
  var multiId = '';
  for(var i=0;i<form1.elements.length;i++)
  {
    var e = form1.elements[i];
    if(e.type == "checkbox" && e.name!='checkform' && e.checked == true)
      multiId += e.value+',';
  }
  if(multiId != ''){
    multiId += '-1';
  <%if(multiIdInput != null){
      String mutiId = "window.opener."+ srcFrm+"."+ multiIdInput;
      out.print(mutiId+".value=multiId;");
      out.print(mutiId+".onchange();");
      //"window.opener.sumitForm("+Operate.CUST_MULTI_SELECT+");");
    }%>
  }
<%}else{%>
  var obj;
  if(row +'' == 'undefined')
  {
    var rodioObj = gCheckedObj(form1, false);
    if(rodioObj != null)
      row = rodioObj.value;
    else
      return;
  }
<%
  if(inputName != null && fieldName != null)
  {
    int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
    for(int i=0; i< length; i++)
    {
      out.println("obj = document.all['"+fieldName[i]+"_'+row];");
      out.print  ("window.opener."+ srcFrm+"."+inputName[i]);
      out.print(".value=");     out.println("obj."+ (fieldName[i].equals("loanid")?"value;":"innerText;"));
    }
  }
  if(methodName != null)
    out.print("window.opener."+ methodName +";");
}
%>
window.close();
}
function checkRadio(row){
  selectRow();
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = <%=isMultiSelect ? "!form1.sel.checked" : "true"%>;
  else
    form1.sel[row].checked = <%=isMultiSelect ? "!form1.sel[row].checked" : "true"%>;
}
function CustSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=20 left=50";
  var winName= "SingleCustSelector";
  paraStr = "../pub/corpselect.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&curID="+curID;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">贷款列表</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right">
        <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectCorp();" value="选用(S)" onKeyDown="return getNextElement();">
        <pc:shortcut key="s" script='selectCorp();'/>
        <%}%>
        <%if(false){%>
        <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/>
        <%}%>
        <input  type="button" class="button" onClick="window.close();" value="返回(X)" onKeyDown="return getNextElement();">
        <pc:shortcut key="X" script='window.close();'/>
    </td>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20></td>
      <td nowrap>贷款单号</td>
      <td nowrap height='20'>银行</td>
      <td nowrap>贷款日期</td>
      <td nowrap>贷款金额</td>
    </tr>
    <%
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr <%if(!isMultiSelect){%>onDblClick="checkRadio(<%=i%>);selectCorp(<%=i%>);"<%}%>>
      <td class="td"><input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>></td>

      <td nowrap onClick="checkRadio(<%=i%>)" id="loancode_<%=i%>" class="td"><%=list.getValue("loancode")%><input type="hidden" name="loanid_<%=i%>" value="<%=list.getValue("loanid")%>"></td>
      <td nowrap onClick="checkRadio(<%=i%>)" class="td" ><%=bankBean.getLookupName(list.getValue("yhid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="loandate_<%=i%>" class="td"><%=list.getValue("loandate")%></td>
      <td nowrap align="right" id="loanfund_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("loanfund")%></td>
    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
  </form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
//------------------------------以下与查询相关
function sumitFixedQuery(oper)
{
  //执行查询,提交表单
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  //点击查询按钮打开查询对话框
  <%if(hasSearchLimit){%>
   showFrame('fixedQuery', true, "", true);//显示层
   <%}%>
}
</SCRIPT>
<%if(hasSearchLimit){%><%--查询权限--%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD nowrap class="td">提单编号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="loancode" name="loancode" value='<%=Select_LoanBean.getFixedQueryValue("loancode")%>' maxlength='50' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>购货单位</TD>
            </TR>
            <TR>
              <TD nowrap class="td">提单日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="loandate$a" value='<%=Select_LoanBean.getFixedQueryValue("loandate$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(loandate$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="loandate$b" value='<%=Select_LoanBean.getFixedQueryValue("loandate$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(loandate$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center">
                <% String ss = "sumitFixedQuery("+Operate.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script="<%=ss%>" />
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                 <pc:shortcut key="x" script="hideFrame('fixedQuery')" />              </TD>
            </TR>
          </TABLE>
       </TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%} out.print(retu);%>
</body>
</html>
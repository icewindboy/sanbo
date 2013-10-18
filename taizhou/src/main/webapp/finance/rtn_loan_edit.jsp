<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,java.text.*,java.util.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%>
<%
  if(!loginBean.hasLimits("rtn_loan_list", request, response))
    return;
  engine.erp.finance.B_ReturnMoney B_ReturnMoneyBean  =   engine.erp.finance.B_ReturnMoney.getInstance(request);
  //String pageCode = "acount_in_out";
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
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">
  function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='rtn_loan_list.jsp';
}
function yhchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_BANK_ACCOUNT%>', 'yhzhid', 'yhid', eval('form1.yhid.value'), '');
   var yhid=document.all['yhid'];
   var v_yhid=document.all['v_yhid'];
   var yhzhid=document.all['yhzhid'];
   var v_yhzhid=document.all['v_yhzhid'];
   if(yhid==''||v_yhid=='')
   {
     yhzhid.value='';
     v_yhzhid.value='';
   }
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
function je_onchange()
{
  var jeObj = document.all['loandate'];
  if(jeObj.value=="")
    return;
  if(isNaN(jeObj.value)){
    alert('输入的金额非法!');
    jeObj.value='';
    return ;
    }
}
function selctbilloflading()
{
    form1.loanid.value='';
    OrderSingleSelect('form1','srcVar=loanid','fieldVar=loanid','',"sumitForm(<%=B_ReturnMoneyBean.SELECT_LOAN%>,-1)");
}
function OrderSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
{
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
    var winName= "SingleladingSelector";
    paraStr = "../finance/loan_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
}
function hkrqOnchange(obj)
{
  checkDate(obj);
  var td = document.form1.retndate.value;//还
  var datestr = td.replace(/-/gi, "/");
  var dt = new Date(datestr);
  var tds = dt.getTime();
  var hk = document.form1.loandate.value;//借
  datestr = hk.replace(/-/gi, "/");
  dt = new Date(datestr);
  var hks = dt.getTime();
  if(hks>tds)
    alert("回款日期非法!");
}
function selecDate()
{
  selectDate(document.form1.retndate);

  var td = document.form1.retndate.value;//还
  var datestr = td.replace(/-/gi, "/");
  var dt = new Date(datestr);
  var tds = dt.getTime();
  var hk = document.form1.loandate.value;//借
  datestr = hk.replace(/-/gi, "/");
  dt = new Date(datestr);
  var hks = dt.getTime();
  if(hks>tds)
  {
    alert("回款日期非法!");
    document.form1.retndate.value="";
  }

}
</script>
<%
  String retu = B_ReturnMoneyBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  //engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//引用结算方式
  //engine.project.LookUp bankBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK);
  //engine.project.LookUp bankaccountBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK_ACCOUNT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);

  engine.project.LookUp loanBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_LOAN);

  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = B_ReturnMoneyBean.getOneTable();
  RowMap masterRow = B_ReturnMoneyBean.getRowinfo();
  String state=masterRow.get("state");
  boolean isEnd = state.equals("1")||state.equals("9")||state.equals("8");
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=edline" : "class=edFocused";
  String detailClass_r = isEnd ? "class=edline" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  loanBean.regData(ds,"loanid");
  String ioflag = masterRow.get("ioflag");
  RowMap loanRow = loanBean.getLookupRow(masterRow.get("loanid"));
%>
<BODY oncontextmenu="window.event.returnValue=true" >
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();"  >
<INPUT TYPE="HIDDEN" NAME="operate" value="">
<INPUT TYPE="HIDDEN" NAME="rownum" value="">
<table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
  <tr valign="top">
    <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table"><tr><td class="activeVTab">还 款 单</td></tr></table>
    <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
    <tr>
    <td>
    <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
    <tr>
     <td noWrap class="tdTitle">&nbsp;贷款单号</td>
     <td noWrap class="td">
     <INPUT TYPE="hidden" NAME="loanid" VALUE="<%=masterRow.get("loanid")%>"  style='width:180'  class="edline" readonly >
     <INPUT TYPE="TEXT" NAME="loancode" VALUE="<%=loanRow.get("loancode")%>"  style='width:180'  class="edline" readonly >
     <%if(!isEnd){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="selctbilloflading()"><%}%>
     </td>
     <td noWrap class="tdTitle">&nbsp;还款单号</td>
     <td noWrap class="td"><INPUT TYPE="TEXT" NAME="rtncode" VALUE="<%=masterRow.get("rtncode")%>"  style='width:180'   <%=edClass%> <%=readonly%>  >
     </td>
    </tr>
    <tr>
     <td noWrap class="tdTitle">&nbsp;银行</td>
     <td noWrap class="td">
     <INPUT TYPE="TEXT" NAME="yhmc" VALUE="<%=loanRow.get("yhmc")%>"  style='width:180'  class="edline" readonly >
     </td>
     <td noWrap class="tdTitle">&nbsp;帐号</td>
     <td noWrap class="td">
     <INPUT TYPE="TEXT" NAME="zh" VALUE="<%=loanRow.get("zh")%>"  style='width:180'  class="edline" readonly >
     </td>
    </tr>
    <tr>
      <td noWrap class="tdTitle">&nbsp;贷款日期</td>
      <td noWrap class="td">
     <INPUT TYPE="TEXT" NAME="loandate" VALUE='<%=loanRow.get("loandate")%>'  style="WIDTH:180"    class="edline" readonly  >
      </td>
    <td noWrap class="tdTitle">&nbsp;还款日期</td>
    <td noWrap class="td">
    <INPUT TYPE="TEXT" NAME="retndate" VALUE="<%=masterRow.get("retndate")%>"  style='width:180'  <%=edClass%> <%=readonly%> onchange="hkrqOnchange(this)" >
    <%if(!isEnd){%><img align="absmiddle" src="../images/seldate.gif"  border="0" title="选择日期" onclick="selecDate()"><%}%>
    </td>
    </tr>
    <tr>
      <td noWrap class="tdTitle">&nbsp;信贷员</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="creditor" VALUE='<%=loanRow.get("creditor")%>'  style="WIDTH:180"    class="edline" readonly  >
      </td>
      <td noWrap class="tdTitle">&nbsp;还款金额</td>
      <td noWrap  class="td"><INPUT TYPE="TEXT" NAME="retnfund" VALUE="<%=masterRow.get("retnfund")%>"  style="WIDTH:180"    <%=detailClass_r%> <%=readonly%>>
      </td>
    </tr>
    <tr>
    <td noWrap class="tdTitle">&nbsp;提交部门</td>
    <td noWrap class="td">
    <%
      if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:180' class='edline' readonly >");
      else {
    %>
     <pc:select name="deptid" addNull="1" style="width:180" onSelect="deptchange();">
     <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
    <%}%></td>
    <td noWrap class="tdTitle">经手人</td>
    <td noWrap class="td">
    <%if(isEnd) out.print("<input type='text' value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:180' class='edline' readonly>");
    else {%>
    <pc:select name="personid" addNull="1" style="width:180">
      <%=personBean.getList(masterRow.get("personid"),"deptid",masterRow.get("deptid"))%> </pc:select>
     <%}%></td>
    </tr>
    <tr>
      <td noWrap class="tdTitle">&nbsp;摘要</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="bz" VALUE="<%=masterRow.get("bz")%>"  style="WIDTH:180"    <%=edClass%> <%=readonly%>>
      </td>
    </tr>
         </table>
        </td>
      </tr>
    </table>
  </td>
</tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("createdate")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("creator")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
               <%if(true){%>
               <%if(!isEnd){%>
               <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 ">
               <%}%>
              <%if(!B_ReturnMoneyBean.isReport){%>
              <input name="btnback" type="button"  style="width:50" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script='<%="backList();"%>'/>
              <%}%>
              <%}%>
            </td>
          </tr>
        </table>
       </td>
    </tr>
  </table>
</form>
<%//&#$
if(B_ReturnMoneyBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
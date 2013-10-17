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
  engine.erp.sale.ImportLading ImportLadingBean = engine.erp.sale.ImportLading.getInstance(request);
  String pageCode = "sale_balance";
  if(!loginBean.hasLimits(pageCode, request, response))
  return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  //引用结算方式
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);

  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  String retu = ImportLadingBean.doService(request, response);
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
  EngineDataSet list = ImportLadingBean.getOneTable();
  ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("-1");
  ArrayList opval = new ArrayList(); opval.add("提货单"); opval.add("退货单");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};
  String twldw=request.getParameter("dwtxid");
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
      out.print(".value=");     out.println("obj."+ (fieldName[i].equals("tdid")?"value;":"innerText;"));
    }
  }
  if(methodName != null)
    out.print("window.opener."+ methodName +";");
}
%>
window.close();
}
function checkRadio(row){
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
    <TD NOWRAP align="center">货物提单选择</TD>
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
        <input name="button1" type="button" class="button" onClick="selectCorp();" value=" 选用 " onKeyDown="return getNextElement();">
        <%}%>
        <%if(hasSearchLimit){%>
        <input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询 " onKeyDown="return getNextElement();">
        <%}%>
        <input  type="button" class="button" onClick="window.close();" value=" 返回 " onKeyDown="return getNextElement();"></td>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20></td>
      <td nowrap>提单编号</td>
      <td nowrap>提单日期</td>
      <td nowrap>结算方式</td>
      <td nowrap>总金额</td>
      <td nowrap>未收金额</td>
      <td nowrap>购货单位</td>
      <td nowrap>业务员</td>
      <td nowrap>部门</td>
    </tr>
    <%
      deptBean.regData(list,"deptid");
      corpBean.regData(list,"dwtxid");
      personBean.regData(list,"personid");
      //prodBean.regData(list,"cpid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr <%if(!isMultiSelect){%>onDblClick="checkRadio(<%=i%>);selectCorp(<%=i%>);"<%}%>>
      <td class="td"><input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="tdbh_<%=i%>" class="td"><%=list.getValue("tdbh")%><input type="hidden" name="tdid_<%=i%>" value="<%=list.getValue("tdid")%>"></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="tdrq_<%=i%>" class="td"><%=list.getValue("tdrq")%></td>
      <td nowrap align="center" id="jsfsid_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=balanceModeBean.getLookupName(list.getValue("jsfsid"))%></td>
      <td nowrap align="center" id="zsje_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("zsje")%></td>
      <td nowrap align="center" id="dwtxId_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("zwje")%></td>
      <td nowrap align="center" id="dwtxId_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=corpBean.getLookupName(list.getValue("dwtxId"))%></td>
      <td nowrap align="center" id="personid_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=personBean.getLookupName(list.getValue("personid"))%></td>
      <td nowrap align="center" id="deptid_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
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
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="tdbh" name="tdbh" value='<%=ImportLadingBean.getFixedQueryValue("tdbh")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>购货单位</TD>
              <TD nowrap class="td">
                <%if(twldw.equals("")){%>
                 <input type="hidden" name="dwtxid" value='<%=ImportLadingBean.getFixedQueryValue("dwtxid")%>'>
                <input type="text" name="buyerName" value='<%=ImportLadingBean.getFixedQueryValue("buyerName")%>' style="width:130" class="edline" readonly>
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=buyerName','fieldVar=dwtxid&fieldVar=dwmc',fixedQueryform.dwtxid.value)"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';buyerName.value='';">
                <%}else{%>
                <input type="hidden" name="dwtxid" value='<%=twldw%>'>
                <%}%>
              </TD>

            </TR>
            <TR>
              <TD nowrap class="td">提单日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="tdrq$a" value='<%=ImportLadingBean.getFixedQueryValue("tdrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(tdrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="tdrq$b" value='<%=ImportLadingBean.getFixedQueryValue("tdrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(tdrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
              <%=deptBean.getList(ImportLadingBean.getFixedQueryValue("deptid"))%></pc:select>
              </TD>
              <TD class="td" nowrap>提单类型</TD>
              <TD nowrap class="td">
                <pc:select name="djlx" addNull="1" style="width:130" >
                <%=ImportLadingBean.listToOption(lists, opkey.indexOf(ImportLadingBean.getFixedQueryValue("djlx")))%>
               </pc:select>
              </TD>
             </TR>
            <TR>
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
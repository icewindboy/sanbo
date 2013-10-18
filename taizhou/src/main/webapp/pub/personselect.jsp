<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
  private boolean isMultiSelect = false;
  private String inputName[] = null;
  private String fieldName[] = null;
  private String methodName = null;
%>
<%
engine.erp.common.B_PersonSelect personSelectBean = engine.erp.common.B_PersonSelect.getInstance(request);
  String retu = personSelectBean.doService(request, response);
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
  EngineDataSet list = personSelectBean.getOneTable();
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
%>
<html><head><title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function selectRow(row)
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
      out.print(mutiId+".onchange();");//"window.opener.sumitForm("+Operate.CUST_MULTI_SELECT+");");
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
      out.print(".value=");     out.println("obj."+ (fieldName[i].equals("personid")?"value;":"innerText;"));
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
function showSearchFrame(){
  showFrame('searchframe1', true, "", true);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">人员列表</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = 15; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
      <td class="td" align="right">
        <%if(list.getRowCount()>0){%><input name="button1" type="button" class="button" onClick="selectRow();" value="选用(X)" onKeyDown="return getNextElement();">
        <pc:shortcut key="x" script='selectRow();'/><%}%>
        <input name="search" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/>
        <input name="button2" type="button" class="button" onClick="window.close();" value="关闭(C)" onKeyDown="return getNextElement();"></td>
        <pc:shortcut key="c" script='window.close();'/>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td width=12 align="center" nowrap><%=isMultiSelect ? "<input type='checkbox' name='checkform' onclick='checkAll(form1,this);' onKeyDown='return getNextElement();'>" : "&nbsp;"%></td>
      <td nowrap height='20'>编码</td>
      <td nowrap height='20'>姓名</td>
      <td nowrap height='20'>部门</td>
      <td nowrap height='20'>职务</td>
      <td nowrap height='20'>性别</td>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
    %>
    <tr <%if(!isMultiSelect){%>onDblClick="checkRadio(<%=i%>);selectRow(<%=i%>);"<%}%>>
      <td nowrap class="td"><%if(isMultiSelect){%><input type="checkbox" name="sel" value="<%=list.getValue("personid")%>" onKeyDown="return getNextElement();"><%}else{%><input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>><%}%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="bm_<%=i%>" class="td"><%=list.getValue("bm")%><input type="hidden" id="personid_<%=i%>" value='<%=list.getValue("personid")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="xm_<%=i%>" class="td"><%=list.getValue("xm")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="deptname_<%=i%>" class="td"><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="zw_<%=i%>" class="td"><%=list.getValue("zw")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sex_<%=i%>" class="td"><%=list.getValue("sex").equals("1") ? "男" : "女"%></td>
    </tr>
    <%  list.next();
      }
      for(; i < 15; i++){
    %>
    <tr>
      <td class="td" height='20'></td>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  //hideFrame('searchframe');
  showFrame('fixedQuery', true, "", true);
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
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <table border="0" cellpadding="0" cellspacing="3">
      <INPUT TYPE="HIDDEN" NAME="operate2" VALUE="">
      <tr>
        <td noWrap class="td" align="center">员工编码</td>
        <td noWrap class="td"><input type="text" name="bm" value='<%=personSelectBean.getFixedQueryValue("bm")%>' style="width:180" class="edbox"></td>
      </tr>
      <tr>
        <td noWrap class="td" align="center">&nbsp;员工姓名&nbsp;</td>
        <td noWrap class="td"><input type="text" name="xm" value='<%=personSelectBean.getFixedQueryValue("xm")%>' style="width:180" class="edbox"></td>
      </tr>
      <tr>
        <td noWrap class="td" align="center">部门</td>
        <td noWrap class="td"><pc:select name="deptid" addNull="1" style="width:180">
            <%=deptBean.getList(personSelectBean.getFixedQueryValue("deptid"))%></pc:select></td>
      </tr>
      <tr>
        <td colspan="2" noWrap align="center" height="30"><input name="button" type="button" class="button" onClick="sumitFixedQuery(<%=personSelectBean.FIXED_SEARCH%>);" value=" 查询 ">
          <input name="button22" type="button" class="button" onClick="hideFrame('fixedQuery')" value=" 关闭 ">
        </td>
      </tr>
    </table>
  </DIV>
</form>
<%out.print(retu);%>
</body>
</html>
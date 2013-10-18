<!--引设备名称--><%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.*,java.util.*"%><%!
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
  private String personid = null;
  private String bm = null;
  private String xm = null;
  private String isdelete = null;
%>
<%
//if(!loginBean.hasLimits("record_card", request, response))
// return;
String retu="";
engine.erp.jit.B_JitPersonBean B_JitPersonBeanBean = engine.erp.jit.B_JitPersonBean.getInstance(request);
engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);//设备
engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);

String operate = request.getParameter("operate");
if(operate !=null && operate.equals(String.valueOf(Operate.INIT)))
{
  srcFrm = request.getParameter("srcFrm");
  multiIdInput = request.getParameter("srcVar");
  isMultiSelect = request.getParameter("multi") != null && request.getParameter("multi").equals("1");
  inputName = request.getParameterValues("srcVar");
  fieldName = request.getParameterValues("fieldVar");
  methodName = request.getParameter("methodName");
  personid = request.getParameter("personid");
  bm=request.getParameter("bm");
  xm=request.getParameter("xm");
  isdelete=request.getParameter("isdelete");
  retu = B_JitPersonBeanBean.doService(request, response);
}
if(operate !=null && operate.equals(String.valueOf(Operate.FIXED_SEARCH)))
{
  retu = B_JitPersonBeanBean.doService(request, response);
}
  if(retu.indexOf("location.href=")>-1)
    return;
  //LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//部门
  EngineDataSet list = B_JitPersonBeanBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  deptBean.regData(list,"deptid");
  deptBean.regData(list,"user_deptid");
  String mm = B_JitPersonBeanBean.codeget();
  //String fieldValue=B_JitPersonBeanBean.code_name_Change(bm,tname,operate,isdelete);

  bm=request.getParameter("bm");
  String name=request.getParameter("xm");
  String gzzid=request.getParameter("gzzid");
  String deptid=request.getParameter("deptid");
  String isdelete=request.getParameter("isdelete");

  if(String.valueOf(Operate.PROD_CHANGE).equals(operate)|| String.valueOf(Operate.PROD_NAME_CHANGE).equals(operate))
  {
    srcFrm = request.getParameter("srcFrm");
    inputName = request.getParameterValues("srcVar");
    fieldName = request.getParameterValues("fieldVar");
    methodName = request.getParameter("methodName");
    String m = B_JitPersonBeanBean.codeget()==null?"pp":(String)B_JitPersonBeanBean.codeget();
    out.print("<script language='javascript'>");
    String m0 = B_JitPersonBeanBean.codeget();
    String fieldValue = B_JitPersonBeanBean.code_name_Change(bm,name,operate,isdelete);
    if(fieldValue.equals("more")){
      String srcVar=null;
      String fieldVar=null;
      String fVar=null;
      for(int i=0;i<inputName.length;i++){
        srcVar=srcVar+"srcVar="+inputName[i]+"&";
      }
      srcVar=srcVar.substring(4,srcVar.lastIndexOf("&"));

      for(int i=0;i<fieldName.length;i++){
        fVar+="fieldVar="+fieldName[i]+"&";
      }
      fVar=fVar.substring(4,fVar.lastIndexOf("&"));
      out.print("opener.SingleSelectperson('"+srcFrm+"','"+srcVar+"','"+fVar+"','"+gzzid+"','"+deptid+"','"+name+"','"+ methodName+"')");
    }
    else if(fieldValue.equals("none")){
      out.print("alert('该员工不存在');");
      out.print("parent."+srcFrm+"."+inputName[0]+".value='';");
      out.print("parent."+srcFrm+"."+inputName[1]+".value='';");
      //out.print("parent."+srcFrm+"."+inputName[2]+".value='';");
    }
    else {
        RowMap equipmentRow =personBean.getLookupRow(fieldValue);
        String xm=personBean.getLookupName(fieldValue);
        String personidl=equipmentRow.get("bm");
        out.print("opener."+srcFrm+"."+inputName[0]+".value='"+fieldValue+"';");
        out.print("opener."+srcFrm+"."+inputName[1]+".value='"+xm+"';");
        //out.print("parent."+srcFrm+"."+inputName[2]+".value='"+xm+"';");
        if(methodName != null&&!methodName.equals(""))
          out.print("window.opener."+ methodName +";");
        out.print("window.close()");
    }
    out.print("</script>");
  }
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
function sumitForm(oper, row)
{
  //新增或修改
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function selectequipment(row)
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
      out.print(".value=");
      out.println("obj."+ (fieldName[i].equals("personid")?"value;":"innerText;"));
    }
  }
  if(methodName != null&&!methodName.equals(""))
    out.print("window.opener."+ methodName +";");
}
%>
window.close();
}
function Radio(row){
  selectRow();
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = <%=isMultiSelect ? "!form1.sel.checked" : "true"%>;
  else
    form1.sel[row].checked = <%=isMultiSelect ? "!form1.sel[row].checked" : "true"%>;
}
function checkRadio(row){
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = !form1.sel.checked;
  else
    form1.sel[row].checked = !form1.sel[row].checked;
}
</script>

<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">人员列表</TD>
  </TR>
</TABLE>

<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
   <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
      int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
      <td class="td" nowrap align="right">
     <%if(list.getRowCount()>0){%>
      <input name="button1" type="button" class="button" onClick="selectequipment();" value=" 选用 " onKeyDown="return getNextElement();">
    <%}%>
      <input name="search" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/>
      <input  type="button" class="button" onClick="window.close();" value=" 返回(C)" onKeyDown="return getNextElement();">
      <pc:shortcut key="c" script='window.close();'/>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
	<tr class="tableTitle">
	  <td nowrap></td>
	  <td nowrap>员工编码</td>
	  <td nowrap>姓名</td>
          <td nowrap>部门</td>
	  <td nowrap>职务</td>
	  <td nowrap>性别</td>
	 <%-- <td nowrap>制造厂</td>
	  <td nowrap>制造日期</td>
          <td nowrap>购买日期</td>
	  <td nowrap>使用部门</td>
	  <td nowrap>存放地点</td>
	  <td nowrap>购置金额</td>
	  <td nowrap>购入部门</td>
	  <td nowrap>开始使用日期</td>--%>
	</tr>
       <%
          list.first();
          int i=0;
          for(;i<list.getRowCount();i++)
          {
       %>
          <tr onDblClick="Radio(<%=i%>);selectequipment(<%=i%>);">
	  <td class="td"><input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="bm_<%=i%>" class="td"><%=list.getValue("bm")%><input type="hidden" id="personid_<%=i%>" value='<%=list.getValue("personid")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="xm_<%=i%>" class="td"><%=list.getValue("xm")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="deptname_<%=i%>" class="td"><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="zw_<%=i%>" class="td"><%=list.getValue("zw")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sex_<%=i%>" class="td"><%=list.getValue("sex").equals("1") ? "男" : "女"%></td>
	  <%--<td class="td" nowarp class=td id="standard_gg_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("standard_gg")%></td>
	  <td class="td" nowarp class=td id="unit_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("unit")%></td>
	  <td class="td" nowarp class=td id="manufacturer_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("manufacturer")%></td>
	  <td class="td" nowarp class=td id="manufacturedate_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("manufacturedate")%></td>
          <td class="td" nowarp class=td id="buy_date_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("buy_date")%></td>
          <td  id="use_deptid_<%=i%>" style="display:none"><%=list.getValue("use_deptid")%></td>
	  <td class="td" nowarp class=td id="use_dept_<%=i%>" onClick="Radio(<%=i%>)"><%=deptBean.getLookupName(list.getValue("use_deptid"))%></td>
	  <td class="td" nowarp class=td id="depositary_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("depositary")%></td>
	  <td class="td" nowarp class=td id="buy_money_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("buy_money")%></td>
	  <td class="td" nowarp class=td id="deptid_<%=i%>" onClick="Radio(<%=i%>)"><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
          <td class="td" nowarp class=td id="use_date_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("use_date")%></td>--%>
	</tr>
        <%  list.next();
          }
          for(; i < loginBean.getPageSize(); i++){
        %>
	<tr >
	  <td >&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <%--<td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
          <td nowrap class="td">&nbsp;</td--%>
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
        <td noWrap class="td"><input type="text" name="bm" value='<%=B_JitPersonBeanBean.getFixedQueryValue("bm")%>' style="width:180" class="edbox"></td>
      </tr>
      <tr>
        <td noWrap class="td" align="center">&nbsp;员工姓名&nbsp;</td>
        <td noWrap class="td"><input type="text" name="xm" value='<%=B_JitPersonBeanBean.getFixedQueryValue("xm")%>' style="width:180" class="edbox"></td>
      </tr>
      <tr>
        <td noWrap class="td" align="center">部门</td>
        <td noWrap class="td"><pc:select name="deptid" addNull="1" style="width:180">
            <%=deptBean.getList(B_JitPersonBeanBean.getFixedQueryValue("deptid"))%></pc:select></td>
      </tr>
      <tr>
        <td colspan="2" noWrap align="center" height="30"><input name="button" type="button" class="button" onClick="sumitFixedQuery(<%=B_JitPersonBeanBean.FIXED_SEARCH%>);" value=" 查询 ">
          <input name="button22" type="button" class="button" onClick="hideFrame('fixedQuery')" value=" 关闭 ">
        </td>
      </tr>
    </table>
  </DIV>
</form>
<%out.print(retu);%>
</body>
</html>
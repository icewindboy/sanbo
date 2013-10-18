<%--生产加工单单选任务单主表--%><%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate ,java.util.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%!
  private String srcFrm = null;         //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
  private boolean isMultiSelect = true;
  private String inputName[] = null;
  private String fieldName[] = null;
  private String methodName = null;
%>
<%
  engine.erp.jit.B_Process_SingleSelTask singleSelTaskBean = engine.erp.jit.B_Process_SingleSelTask.getInstance(request);
  String pageCode = "produce_process";
  if(!loginBean.hasLimits(pageCode, request, response))
  return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性LookUp
  engine.project.LookUp producePlanBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PLAN);
  String retu = singleSelTaskBean.doService(request, response);
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
    String multi = request.getParameter("multi");
    isMultiSelect = request.getParameter("multi") != null && request.getParameter("multi").equals("1");
    inputName = request.getParameterValues("srcVar");
    fieldName = request.getParameterValues("fieldVar");
    methodName = request.getParameter("method");
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = singleSelTaskBean.getOneTable();
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
function selectProduce(row)
{
<%if(isMultiSelect){%>
  var multiId = '';
  var s = ""
  for(var i=0;i<form1.elements.length;i++)
  {
    var e = form1.elements[i];
    if(e.type == "checkbox" && e.name!='checkform' && e.checked == true)
    {
      var v = form1.elements[i+1];
      if ( s=="" )
        s+=v.value;
      else
      {
        pos = s.indexOf(v.value);
        if (pos<0)
        {
          alert("选择的多笔数据只能属于同一个生产计划号");return;
        }
      }
      multiId += e.value+',';
    }
  }
  if(multiId != ''){
    multiId += '-1';
  <%if(multiIdInput != null){
      String mutiId = "window.opener."+ srcFrm +"."+ multiIdInput;
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
      out.print(".value=");     out.println("obj."+ (fieldName[i].equals("wlxqjhid")?"value;":"innerText;"));
    }
  }
  if(methodName != null)
    out.print("window.opener."+ methodName +";");
}
%>
window.close();
}
/*function checkRadio(row){
   selectRow();
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = <%=isMultiSelect ? "!form1.sel.checked" : "true"%>;
  else
    form1.sel[row].checked = <%=isMultiSelect ? "!form1.sel[row].checked" : "true"%>;
}
*/
function checkRadio(row){
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = !form1.sel.checked;
  else
    form1.sel[row].checked = !form1.sel[row].checked;
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
    <TD NOWRAP align="center">生产计划列表</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right">
        <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduce();" value=" 选用 " onKeyDown="return getNextElement();">
        <%}%>
        <input  type="button" class="button" onClick="window.close();" value=" 返回(C)" onKeyDown="return getNextElement();">
          <pc:shortcut key="c" script='window.close();'/></td>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>生产计划号</td>
      <td nowrap>制定车间</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>需求量</td>
      <td nowrap>日期</td>
    </tr>
    <%
      deptBean.regData(list,"deptid");
       producePlanBean.regData(list, "scjhid");
       prodBean.regData(list,"cpid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++){
        String dmsxid = list.getValue("dmsxid");
        String sxz = propertyBean.getLookupName(dmsxid);
        String scjhid = list.getValue("scjhid");
        String scjhh = producePlanBean.getLookupName(scjhid);
        String xql = list.getValue("sl");
        String deptid = list.getValue("deptid");
        RowMap prodRow= prodBean.getLookupRow(list.getValue("cpid"));
        String cpbm = prodRow.get("cpbm");
        String product = prodRow.get("product");
    %>
    <tr onClick="checkTr(<%=i%>)">
       <td class="td"><input type="checkbox" name="sel" value='<%=list.getValue("tag")%>' onKeyDown="return getNextElement();">
       <input type="hidden" name="scjhid_<%=i%>" value="<%=scjhid%>">
      </td>
      <%--input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>></td--%>
      <td nowrap onClick="checkRadio(<%=i%>)" id="scjhh_<%=i%>" class="td"><%=scjhh%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="deptid_<%=i%>" class="td"> <%=deptBean.getLookupName(deptid)%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="cpbm_<%=i%>" class="td"><%=cpbm%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="product_<%=i%>" class="td"><%=product%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sxz_<%=i%>" class="td"><%=sxz%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="xql_<%=i%>" class="td"><%=xql%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="xql_<%=i%>" class="td"></td>
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
    </tr>
    <%}%>
  </table>
  </form><%out.print(retu);%>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</body>
</html>

<%--销售出库单引用销售提单--%>
<%
/**
 *2004-3-5 15:50 修改 修改原来显示的供应商caption为现在的客户caption yjg
 */
%>
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
  engine.erp.store.B_SingleLadding singleLaddingBean = engine.erp.store.B_SingleLadding.getInstance(request);
  String pageCode = "outputlist";
  if(!loginBean.hasLimits(pageCode, request, response))
  return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);//仓库

  String retu = singleLaddingBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String operate = "0";//request.getParameter("operate");
  //if(operate !=null && operate.equals(String.valueOf(Operate.INIT)))
  //{
    srcFrm = singleLaddingBean.srcFrm;//request.getParameter("srcFrm");
    multiIdInput = singleLaddingBean.multiIdInput;//request.getParameter("srcVar");
    isMultiSelect = singleLaddingBean.isMultiSelect;//request.getParameter("multi") != null && request.getParameter("multi").equals("1");
    inputName = singleLaddingBean.inputName;//request.getParameterValues("srcVar");
    fieldName = singleLaddingBean.fieldName;//request.getParameterValues("fieldVar");
    methodName = singleLaddingBean.methodName;//request.getParameter("method");
  //}
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = singleLaddingBean.getOneTable();
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
  for(var i=0;i<form1.elements.length;i++)
  {
    var e = form1.elements[i];
    if(e.type == "checkbox" && e.name!='checkform' && e.checked == true)
      multiId += e.value+',';
  }
  if(multiId != ''){
    multiId += '-1';
  <%if(multiIdInput != null){
      String mutiId = "parent.opener."+ srcFrm+"."+ multiIdInput;
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
      out.print  ("parent.opener."+ srcFrm+"."+inputName[i]);
      out.print(".value=");     out.println("obj."+ (fieldName[i].equals("tdid")?"value;":"innerText;"));
    }
  }
  if(methodName != null)
    out.print("parent.opener."+ methodName +";");
}
%>
parent.close();
}
function checkRadio(row){
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = <%=isMultiSelect ? "!form1.sel.checked" : "true"%>;
  else
    form1.sel[row].checked = <%=isMultiSelect ? "!form1.sel[row].checked" : "true"%>;
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='ladding_single_bottom.jsp?operate=<%=singleLaddingBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">销售提单列表</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-6; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right">
        <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduce();" title="选用(ALT+E)" value="选用(E)" onKeyDown="return getNextElement();">
        <pc:shortcut key="e" script='<%="selectProduce()"%>'/>
        <%}%>
        <input  type="button" class="button" onClick="parent.close();" title="返回(ALT+C)" value="返回(C)" onKeyDown="return getNextElement();">
       <pc:shortcut key="c" script='<%="parent.close()"%>'/>
        </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20></td>
      <td nowrap>单据号</td>
      <td nowrap>日期</td>
      <td nowrap>客户</td><%--2004-3-5 15:50 修改 修改原来显示的供应商caption为现在的客户caption yjg--%>
      <td nowrap>客户类型</td>
      <td nowrap>仓库</td>
      <td nowrap>部门</td>
      <td nowrap>业务员</td>
    </tr>
    <%personBean.regData(list, "personid");
      deptBean.regData(list,"deptid");
      corpBean.regData(list, "dwtxid");
      storeBean.regData(list,"storeid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++){
    %>
    <tr <%if(!isMultiSelect){%>onDblClick="checkRadio(<%=i%>);selectProduce(<%=i%>);"<%}%>>
      <td class="td">
      <input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>></td>
      <td nowrap onClick="checkRadio(<%=i%>);showDetail(<%=list.getRow()%>)" id="tdbh_<%=i%>" class="td"><%=list.getValue("tdbh")%><input type="hidden" name="tdid_<%=i%>" value="<%=list.getValue("tdid")%>"></td>
      <td nowrap onClick="checkRadio(<%=i%>);showDetail(<%=list.getRow()%>)" id="tdrq_<%=i%>" class="td"><%=list.getValue("tdrq")%></td>
      <td nowrap onClick="checkRadio(<%=i%>);showDetail(<%=list.getRow()%>)" id="dwtxid_<%=i%>" class="td"><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>);showDetail(<%=list.getRow()%>)" id="dwtxid_<%=i%>" class="td"><%=list.getValue("khlx")%></td>
      <td nowrap onClick="checkRadio(<%=i%>);showDetail(<%=list.getRow()%>)" id="storeid_<%=i%>" class="td"><%=storeBean.getLookupName(list.getValue("storeid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>);showDetail(<%=list.getRow()%>)" id="deptid_<%=i%>" class="td"><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>);showDetail(<%=list.getRow()%>)" id="personid_<%=i%>" class="td"><%=personBean.getLookupName(list.getValue("personid"))%></td>
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
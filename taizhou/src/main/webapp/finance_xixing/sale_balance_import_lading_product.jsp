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
  engine.erp.finance.xixing.ImportLadingProduct ImportLadingProductBean = engine.erp.finance.xixing.ImportLadingProduct.getInstance(request);
  String pageCode = "sale_balance";
  if(!loginBean.hasLimits(pageCode, request, response))
  return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);

  String retu = ImportLadingProductBean.doService(request, response);
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
  EngineDataSet list = ImportLadingProductBean.getOneTable();
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
<script language="javascript" src="../scripts/frame.js"></script>
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
      out.print(".value=");     out.println("obj."+ (fieldName[i].equals("rkdmxid")?"value;":"innerText;"));
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
    <TD NOWRAP align="center">出库单货物选择</TD>
  </TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "datasetlist"; pageContext.setAttribute(key, list);   int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
      </td>
      <td class="td" align="right">
        <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectCorp();" value="选用(S)" onKeyDown="return getNextElement();">
        <pc:shortcut key="s" script='selectCorp();'/>
        <%}if(false){%>
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
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>出库单编号</td>
      <td nowrap>日期</td>
      <td nowrap>购货单位</td>
      <td nowrap>产品</td>
      <td nowrap>产品编码</td>
      <td nowrap>总金额</td>
      <td nowrap>未收金额</td>
      <td nowrap>部门</td>

    </tr>
    <%
      deptBean.regData(list,"deptid");
      corpBean.regData(list,"dwtxid");
      //personBean.regData(list,"personid");
      prodBean.regData(list,"cpid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++){
        RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));
    %>
    <tr <%if(!isMultiSelect){%>onDblClick="checkRadio(<%=i%>);selectCorp(<%=i%>);"<%}%>>
      <td class="td">
        <%if(isMultiSelect){%>
        <input type="checkbox" name="sel" value="<%=list.getValue("rkdmxid")%>" onKeyDown="return getNextElement();">
        <%}else{%>
        <input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>>
        <%}%><input type="hidden" id="rkdmxid_<%=i%>" value='<%=list.getValue("rkdmxid")%>'>
      </td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sfdjdh_<%=i%>" class="td"><%=list.getValue("sfdjdh")%><input type="hidden" name="sfdjid_<%=i%>" value="<%=list.getValue("sfdjid")%>"></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sfrq_<%=i%>" class="td"><%=list.getValue("sfrq")%></td>
      <td nowrap align="center" id="dwtxId_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=corpBean.getLookupName(list.getValue("dwtxId"))%></td>
      <td nowrap align="center" id="cpid_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("product")%></td>
      <td nowrap align="center"  class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("cpbm")%></td>
      <td nowrap align="center"  class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("zje")%></td>
      <td nowrap align="center"  class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("wjsje")%></td>
      <td nowrap align="center" id="deptid_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
     <%-- <td nowrap align="center" id="personid_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=personBean.getLookupName(list.getValue("personid"))%></td>--%>
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
</SCRIPT>
<% out.print(retu);%>
</body>
</html>
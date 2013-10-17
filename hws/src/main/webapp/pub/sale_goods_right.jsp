<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%
  engine.erp.common.B_SaleGoodsSelect productSelect = engine.erp.common.B_SaleGoodsSelect.getInstance(request);
  String retu = productSelect.doService(request, response);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = productSelect.getOneTable();
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
function selectProduct(row)
{
<%if(productSelect.isMultiSelect){%>
  var multiId = '';
  for(var i=0;i<form1.elements.length;i++)
  {
    var e = form1.elements[i];
    if(e.type == "checkbox" && e.name!='checkform' && e.checked == true)
      multiId += e.value+',';
  }
  if(multiId != ''){
    multiId += '-1';
  <%if(productSelect.multiIdInput != null){
      String mutiId = "parent.opener."+productSelect.srcFrm+"."+productSelect.multiIdInput;
      out.print(mutiId+".value=multiId;");
      out.print(mutiId+".onchange();");//"window.opener.sumitForm("+Operate.PROD_MULTI_SELECT+");");
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
  String inputName[] = productSelect.inputName;
  String fieldName[] = productSelect.fieldName;
  if(inputName != null && fieldName != null)
  {
    int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
    for(int i=0; i< length; i++)
    {
      out.println("obj = document.all['"+fieldName[i]+"_'+row];");
      out.print("parent.opener."+productSelect.srcFrm+"."+inputName[i]);
      out.print(".value=");
      out.println("obj."+
                  (fieldName[i].equalsIgnoreCase("wzdjid") || fieldName[i].equalsIgnoreCase("cpid")||
                  fieldName[i].equalsIgnoreCase("wzlbid") || fieldName[i].equalsIgnoreCase("isprops") ||
                  fieldName[i].equalsIgnoreCase("pm") || fieldName[i].equalsIgnoreCase("gg") ||
                  fieldName[i].equalsIgnoreCase("hsbl") || fieldName[i].equalsIgnoreCase("ztqq")?"value;":"innerText;"));
    }
  }
  if(productSelect.getMethodName() != null)
    out.print("parent.opener."+productSelect.getMethodName()+";");
}
%>
parent.close();
}
function checkRadio(row){
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = !form1.sel.checked;
  else
    form1.sel[row].checked = !form1.sel[row].checked;
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">产品明细选择</TD>
  </TR></TABLE>
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = 20;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="20"/></td>
      <td class="td" align="right"><input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
      <pc:shortcut key="q" script='showFixedQuery();'/>
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value="选用(X)" onKeyDown="return getNextElement();">
        <pc:shortcut key="x" script='selectProduct();'/>
      <%}%>
        <input  type="button" class="button" onClick="parent.close();" value="返回(C)" onKeyDown="return getNextElement();"></td>
        <pc:shortcut key="c" script='parent.close();'/>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><%if(productSelect.isMultiSelect){%><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'><%}%></td>
      <td class="td" nowrap>编码</td>
      <td class="td" nowrap>助记码</td>
      <td class="td" nowrap>品名 规格</td>
      <td class="td" nowrap>单位</td>
      <%--td class="td" nowrap>库存量</td--%>
      <%--td class="td" nowrap>可供量</td--%>
      <td class="td" nowrap>计划单价</td>
      <td class="td" nowrap>销售价</td>
  

    </tr>
    <%int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr onClick="selectRow()" <%if(!productSelect.isMultiSelect){%>onDblClick="checkRadio(<%=i%>);selectProduct(<%=i%>);"<%}%>>
      <td nowrap align="center" class="td">
        <%if(productSelect.isMultiSelect){%>
        <input type="checkbox" name="sel" value="<%=list.getValue("wzdjid")%>" onKeyDown="return getNextElement();">
        <%}else{%>
        <input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>>
        <%}%><input type="hidden" id="wzdjid_<%=i%>" value='<%=list.getValue("wzdjid")%>'>
        <input type="hidden" id="cpid_<%=i%>" value='<%=list.getValue("cpid")%>'>
        <input type="hidden" id="wzlbid_<%=i%>" value='<%=list.getValue("wzlbid")%>'>
        <input type="hidden" id="pm_<%=i%>" value='<%=list.getValue("pm")%>'>
        <input type="hidden" id="gg_<%=i%>" value='<%=list.getValue("gg")%>'>
        <input type="hidden" id="hsbl_<%=i%>" value='<%=list.getValue("hsbl")%>'>
        <input type="hidden" id="ztqq_<%=i%>" value='<%=list.getValue("ztqq")%>'>
        <input type="hidden" id="isprops_<%=i%>" value='<%=list.getValue("isprops")%>'>
      </td>
      <td nowrap id="cpbm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("cpbm")%></td>
      <td nowrap id="zjm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("zjm")%></td>
      <td nowrap id="product_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("product")%></td>
      <td nowrap id="jldw_<%=i%>" align="center" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("jldw")%></td>
      <%--td nowrap id="kcsl_<%=i%>" align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("kcsl")%></td--%>
      <%--td nowrap id="kckgl_<%=i%>" align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("kckgl")%></td--%>
      <td nowrap id="jhdj_<%=i%>" align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("jhdj")%></td>
      <td nowrap id="xsj_<%=i%>" align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("xsj")%></td>
    

    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>

      

      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
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
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0><%productSelect.table.printWhereInfo(pageContext);%>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form><%out.print(retu);%>
</body>
</html>
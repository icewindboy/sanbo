<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%><%@ page import="engine.dataset.*, engine.project.Operate"%><%
  engine.erp.store.B_GetCardValue getCarddValueBean = engine.erp.store.B_GetCardValue.getInstance(request);
  String retu = getCarddValueBean.doService(request, response);
  if(retu.indexOf("windows.close")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = getCarddValueBean.getOneTable();
  String operate = request.getParameter("operate");
  String srcFrm = getCarddValueBean.srcFrm;
  if(String.valueOf(getCarddValueBean.BACHNO_SEARCH).equals(operate))
  {
    try{
      int count = list.getRowCount();
      //out.print("<script language='javascript' src='../scripts/validate.js'></script>");
      out.print("<script language='javascript'>");
      if(count > 1 || count == 0)
      {
        if(count>1)
          out.print("parent.CardInfoOpen();");
    }
    else
    {
      String[] inputName = getCarddValueBean.inputName;
      String[] fieldName = getCarddValueBean.fieldName;
      if(inputName != null && fieldName != null)
      {
        int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
         for(int i=0; i< length; i++)
         {
           String value = list.getValue(fieldName[i]);
           out.print("parent."); out.print(srcFrm); out.print("."); out.print(inputName[i]);
           out.print(".value='"); out.print(value); out.print("';");
         }
      }
      if(getCarddValueBean.methodName != null && count==1)
        out.print("parent."+getCarddValueBean.methodName+";");
    }
    out.print("</script>");
    return;
  }
  catch(Exception e){
    getCarddValueBean.getLog().error("getCardInfo", e);
  }
  }
  //String multiIdInput = propertySelectBean.multiIdInput;
  String[] inputName = getCarddValueBean.inputName;
  String[] fieldName = getCarddValueBean.fieldName;
  String methodName = getCarddValueBean.methodName;
  boolean isMultiSelect = false;
  String curUrl = request.getRequestURL().toString();
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  prodBean.regData(list,"cpid");
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
function selectPropRow(row)
{
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
      out.print(".value=");     out.println("obj.value;");
    }
  }
  if(methodName != null)
    out.print("window.opener."+ methodName +";");
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
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="760" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">合格证信息列表</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table id="tbcontrol" width="760" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%//String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); /*String pageSize = ""+iPage;*/%>
      <%--pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/--%></td>
      <td class="td" align="right">
        <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectPropRow();" value="选用(S)" onKeyDown="return getNextElement();">
        <pc:shortcut key="s" script='selectPropRow();'/><%}%>
        <input name="button2" type="button" class="button" onClick="window.close();" value="关闭(C)" onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script='window.close();'/></td>
    </tr>
  </table>
  <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td width=12 align="center" nowrap><%=isMultiSelect ? "<input type='checkbox' name='checkform' onclick='checkAll(form1,this);' onKeyDown='return getNextElement();'>" : "&nbsp;"%></td>
      <td nowrap height='20'>产品编码</td>
      <td nowrap height='20'>品名规格</td>
      <td nowrap height='20'>规格属性</td>
      <td nowrap height='20'>生产批号</td>
      <td nowrap height='20'>重量</td>
      <td nowrap height='20'>长度</td>
      <td nowrap height='20'>张数</td>
      <td nowrap height='20'>状态</td>
    </tr>
    <%
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
        RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));
    %>
    <tr onDblClick="selectPropRow(<%=i%>);" onClick="checkRadio(<%=i%>);">
      <td nowrap width=12 align="center" class='td'><input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>>
         <input type="hidden" id="dmsxid_<%=i%>" value='<%=list.getValue("dmsxid")%>'>
      <input type="hidden" id="cpid_<%=i%>" value='<%=list.getValue("cpid")%>'>
      <input type="hidden" id="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>'>
      <input type="hidden" id="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>'>
      <input type="hidden" id="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>'>
      <input type="hidden" id="hsbl_<%=i%>" value='<%=prodRow.get("hsbl")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)"  class="td"><input type="text" id="cpbm_<%=i%>" class="ednone" value='<%=prodRow.get("cpbm")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)"  class="td"><input type="text" id="product_<%=i%>" class="ednone" value='<%=prodRow.get("product")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)"  class="td"><input type="text" id="sxz_<%=i%>" class="ednone" value='<%=list.getValue("sxz")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)"  class="td"><input type="text" id="batno_<%=i%>"  class="ednone" value='<%=list.getValue("batno")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)"  class="td"><input type="text" id="salenum_<%=i%>"  class="ednone" value='<%=list.getValue("saleNum")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)"  class="td"><input type="text" id="producenum_<%=i%>"  class="ednone" value='<%=list.getValue("produceNum")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)"  class="td"><input type="text" id="pagenum_<%=i%>"  class="ednone" value='<%=list.getValue("pageNum")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)"  class="td"><input type="text" id="zt_<%=i%>"  class="ednone" value='<%=list.getValue("zt").equals("1") ? "已入库":""%>'></td>
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
      <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
</SCRIPT>
<%out.print(retu);%>
</body>
</html>

<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%
  engine.erp.common.B_PropertySelect propertySelectBean = engine.erp.common.B_PropertySelect.getInstance(request);
  String retu = propertySelectBean.doService(request, response);
  if(retu.indexOf("windows.close")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = propertySelectBean.getOneTable();
  String operate = request.getParameter("operate");
  if(String.valueOf(Operate.PROD_PROP_NAME_CHANGE).equals(operate))
  {
    try{
      int count = list.getRowCount();
      //out.print("<script language='javascript' src='../scripts/validate.js'></script>");
      out.print("<script language='javascript'>");
      if(count > 1 || count == 0)
      {
        if(count==0)
        {
          String sys = loginBean.getSystemParam("SYS_PUBLIC_SELECT");
          if(sys.equals("1"))
            out.print("parent.PropertySelectOpen();");
          else if(sys.equals("2"))
            out.print("alert('不存在该规格属性！');");
        }
        else
          out.print("parent.PropertySelectOpen();");
      }
      else
      {
        String[] inputName = propertySelectBean.inputName;
        String[] fieldName = propertySelectBean.fieldName;
        if(inputName != null && fieldName != null)
        {
          int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
          String prefix= "parent." + propertySelectBean.srcFrm + ".";
          list.first();
          for(int i=0; i< length; i++)
          {
            out.print(prefix); out.print(inputName[i]);
            out.print(".value='");
            out.print(list.getValue(fieldName[i]));
            out.print("';");
          }
        }
        if(propertySelectBean.methodName != null && count==1)
          out.print("parent."+propertySelectBean.methodName+";");
      }
      out.print("</script>");
      return;
    }
    catch(Exception e){
      propertySelectBean.getLog().error("propNameChange", e);
    }
  }
  String srcFrm = propertySelectBean.srcFrm;
  String multiIdInput = propertySelectBean.multiIdInput;
  String[] inputName = propertySelectBean.inputName;
  String[] fieldName = propertySelectBean.fieldName;
  String methodName = propertySelectBean.methodName;
  boolean isMultiSelect = propertySelectBean.isMultiSelect;
  String curUrl = request.getRequestURL().toString();
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
  if(row == 'add')
  {
    sumitForm('<%=propertySelectBean.POST%>');
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
      out.print(".value=");     out.println("obj."+ (fieldName[i].equals("dmsxid")?"value;":"innerText;"));
    }
  }
  if(methodName != null)
    out.print("window.opener."+ methodName +";");
  EngineDataSet ds = propertySelectBean.dsSpecProperty;
%>
window.close();
}
var specCount = <%=ds.getRowCount()%>;
function checkRadio(row){
  if(form1.sel.length+''=='undefined')
  {
    form1.sel.checked = true;
    if(specCount > 0 && row < 1)
      return;
    form1.sel.focus();
  }
  else
  {
    form1.sel[row].checked = true;
    if(specCount > 0 && row < 1)
      return;
    form1.sel[row].focus();
  }
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">产品规格属性列表</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%//String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); /*String pageSize = ""+iPage;*/%>
      <%--pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/--%></td>
      <td class="td" align="right"><input name="search" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery();'/>
        <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectPropRow();" value="选用(S)" onKeyDown="return getNextElement();">
        <pc:shortcut key="s" script='selectPropRow();'/><%}%>
        <input name="button2" type="button" class="button" onClick="window.close();" value="关闭(C)" onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script='window.close();'/></td>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td width=12 align="center" nowrap><%=isMultiSelect ? "<input type='checkbox' name='checkform' onclick='checkAll(form1,this);' onKeyDown='return getNextElement();'>" : "&nbsp;"%></td>
      <td nowrap height='20'>产品规格属性</td>
    </tr>
    <%RowMap row = propertySelectBean.rowInfo;
      if(ds.getRowCount() > 0)
      {
        //out.print(<td class='td'>&nbsp;</td><td class='td'>");
        StringBuffer buf = new StringBuffer();
        buf.append("<tr onDblClick=\"checkRadio(0);selectPropRow('add')\" onClick='selectRow()'>");
        buf.append("<td nowrap class='td'>").append("<input type='radio' name='sel' onKeyDown='return getNextElement();' value='add'></td>");
        buf.append("<td nowrap onClick=\"checkRadio(0)\" class='td'>");
        ds.first();
        for(int j=0; j<ds.getRowCount(); j++)
        {
          buf.append(ds.getValue("sxmc")).append(":<input class=edbox size=12 maxlength=16 name='sxmc_");
          buf.append(j).append("' value='").append(row.get("sxmc_"+j)).append("' onKeyDown='return getNextElement();'>");
          ds.next();
        }
        out.print(buf.toString());
        out.print("</td>");
      }
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
    %>
    <tr onDblClick="checkRadio(<%=ds.getRowCount()>0 ? i+1 : i%>);selectPropRow(<%=i%>);" onClick="selectRow()">
      <td nowrap width=12 align="center" class='td'><input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>><input type="hidden" id="dmsxid_<%=i%>" value='<%=list.getValue("dmsxid")%>'></td>
      <td nowrap onClick="checkRadio(<%=ds.getRowCount()>0 ? i+1 : i%>)" id="sxz_<%=i%>" class="td"><%=list.getValue("sxz")%></td>
    </tr>
    <%  list.next();
      }
      if(ds.getRowCount() > 0)
        i++;
      for(; i < iPage; i++){
    %>
    <tr>
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
</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
          <TABLE cellspacing=3 cellpadding=0 border=0>
<%ds.first();
  StringBuffer buf = new StringBuffer();
  row = propertySelectBean.searchRow;
  for(int j=0; j<ds.getRowCount(); j++)
  {
    buf.append("<TR><TD nowrap class='td'>").append(ds.getValue("sxmc"));
    boolean isNumber = ds.getValue("sxlx").equals("number");
    buf.append("</TD><TD class='td' nowrap><input class=edbox style='WIDTH:");
    if(isNumber)
    {
      buf.append("88' maxlength=10 name='sxmc_").append(j).append("_min' value='");
      buf.append(row.get("sxmc_"+j+"_min")).append("' onKeyDown='return getNextElement();'> -- <input class=edbox style='WIDTH:");
      buf.append("88' maxlength=10 name='sxmc_").append(j).append("_max' value='");
      buf.append(row.get("sxmc_"+j+"_max")).append("' onKeyDown='return getNextElement();'>");
    }
    else
    {
      buf.append("200' maxlength=20 name='sxmc_").append(j).append("' value='");
      buf.append(row.get("sxmc_"+j)).append("' onKeyDown='return getNextElement();'>");
    }
    buf.append("</TD></TR>");
    ds.next();
  }
  out.print(buf.toString());
%>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)'/>
                <input class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script="hideFrame('fixedQuery')"/>
              </TD>
            </TR>
          </TABLE></TD></TR></TABLE></DIV>
</form>
<%out.print(retu);%>
</body>
</html>
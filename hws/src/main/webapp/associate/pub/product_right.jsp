<%@page contentType="text/html; charset=UTF-8" %>
<%request.setCharacterEncoding("UTF-8");
  //session.setMaxInactiveInterval(900);
  response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","max-age=0");
  response.addHeader("Cache-Control","pre-check=0");
  response.addHeader("Cache-Control","post-check=0");
  response.setDateHeader("Expires", 0);
  engine.common.OtherLoginBean loginBean = engine.common.OtherLoginBean.getInstance(request);
  if(!loginBean.isLogin(request, response))
    return;
%><%@ taglib uri="/WEB-INF/pagescontrol.tld" prefix="pc"%>
<%@ page import="engine.dataset.*, engine.project.*"%>
<%engine.erp.common.OtherProductSelect productSelect = engine.erp.common.OtherProductSelect.getInstance(request);
  String retu = productSelect.doService(request, response);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = productSelect.getOneTable();
%>
<html><head><title>选择产品</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/scripts/public.css" type="text/css">
</head>
<%if(retu.indexOf("toProduct()") > 0)
  {
    out.println("<script language='javascript'>function toProduct(){location.href='../baseinfo/productinfomodify.jsp?"
      +"operate=" + "OPERATE_COPY_OTHER" + "&src="+ curUrl+"';}</script>");
    out.print(retu);
    return;
  }
  LookUp productKindBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT_KIND);
%>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/validate.js"></script>

<script language="javascript">
function sumitForm(oper, row)
{
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
      out.println("obj."+ (fieldName[i].equalsIgnoreCase("cpid") || fieldName[i].equalsIgnoreCase("wzlbid") ||
                           fieldName[i].equalsIgnoreCase("chxz") || fieldName[i].equalsIgnoreCase("isprops") ||
                           fieldName[i].equalsIgnoreCase("pm")   || fieldName[i].equalsIgnoreCase("gg") ||
                           fieldName[i].equalsIgnoreCase("scydw")|| fieldName[i].equalsIgnoreCase("scdwgs") ||
                           fieldName[i].equalsIgnoreCase("ztqq") || fieldName[i].equalsIgnoreCase("hsbl") ||
                           fieldName[i].equalsIgnoreCase("isbatchno") || fieldName[i].equalsIgnoreCase("chlbid") ||
                           fieldName[i].equalsIgnoreCase("sjbcps") || fieldName[i].equalsIgnoreCase("sjlbhl") ||
                           fieldName[i].equalsIgnoreCase("sjlbsf")? "value;":"innerText;"));
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
    form1.sel.checked = <%=productSelect.isMultiSelect ? "!form1.sel.checked" : "true"%>;
  else
    form1.sel[row].checked = <%=productSelect.isMultiSelect ? "!form1.sel[row].checked" : "true"%>;
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">可选物资列表</TD>
  </TR></TABLE>
<table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
  <tr>
    <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="20"/></td>
      <td class="td" align="right" nowrap>
        <input name="search" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery();'/>
<%if(list.getRowCount()>0){%><input name="button1" type="button" class="button" onClick="selectProduct();" value="选用(X)" onKeyDown="return getNextElement();">
        <pc:shortcut key="x" script='selectProduct();'/><%}%>
        <input name="button2" type="button" class="button" onClick="parent.close();" value="返回(C)" onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script='parent.close();'/></td>
  </tr>
</table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td height="20" width=12 nowrap><%=productSelect.isMultiSelect ? "<input type='checkbox' name='checkform' onclick='checkAll(form1,this);' onKeyDown='return getNextElement();'>" : "&nbsp;"%></td>
      <td nowrap>物资编码</td>
      <td nowrap>助记码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>单位</td>
      <td nowrap>换算单位</td>
      <td nowrap>图号</td>
      <td nowrap>条形码</td>
      <td nowrap>ABC分类</td>
      <td nowrap>存货类别</td>
      <td nowrap>存货性质</td>
    </tr>
    <%list.first();
    int i=0;
    for(; i<list.getRowCount(); i++)   {
      String wzlbid = list.getValue("wzlbid");
      String chxz = list.getValue("chxz");
  %>
    <tr onClick="selectRow()" <%if(!productSelect.isMultiSelect){%>onDblClick="checkRadio(<%=i%>);selectProduct(<%=i%>);"<%}%>>
      <td class="td">
        <%if(productSelect.isMultiSelect){%>
        <input type="checkbox" name="sel" value="<%=list.getValue("cpid")%>" onKeyDown="return getNextElement();">
        <%}else{%>
        <input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>>
        <%}%><input type="hidden" id="cpid_<%=i%>" value='<%=list.getValue("cpid")%>'>
        <input type="hidden" id="wzlbid_<%=i%>" value='<%=wzlbid%>'>
        <input type="hidden" id="chxz_<%=i%>" value='<%=chxz%>'>
        <input type="hidden" id="pm_<%=i%>" value='<%=list.getValue("pm")%>'>
        <input type="hidden" id="gg_<%=i%>" value='<%=list.getValue("gg")%>'>
        <%--input type="hidden" id="hsdw_<%=i%>" value='<%=list.getValue("hsdw")%>'--%>
        <input type="hidden" id="isprops_<%=i%>" value='<%=list.getValue("isprops")%>'>
        <input type="hidden" id="scydw_<%=i%>" value='<%=list.getValue("scydw")%>'>
        <input type="hidden" id="scdwgs_<%=i%>" value='<%=list.getValue("scdwgs")%>'>
        <input type="hidden" id="ztqq_<%=i%>" value='<%=list.getValue("ztqq")%>'>
        <input type="hidden" id="hsbl_<%=i%>" value='<%=list.getValue("hsbl")%>'>
        <input type="hidden" id="isbatchno_<%=i%>" value='<%=list.getValue("isbatchno")%>'>
        <input type="hidden" id="chlbid_<%=i%>" value='<%=list.getValue("chlbid")%>'>
      <%if("essen".equals(loginBean.getSystemParam("SYS_CUST_NAME"))){
      %><input type="hidden" id="sjbcps_<%=i%>" value='<%=list.getValue("sjbcps")%>'>
        <input type="hidden" id="sjlbhl_<%=i%>" value='<%=list.getValue("sjlbhl")%>'>
        <input type="hidden" id="sjlbsf_<%=i%>" value='<%=list.getValue("sjlbsf")%>'><%}%>
      </td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="cpbm_<%=i%>" class="td"><%=list.getValue("cpbm")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="zjm_<%=i%>" class="td"><%=list.getValue("zjm")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="product_<%=i%>" class="td"><%=list.getValue("product")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="jldw_<%=i%>" class="td"><%=list.getValue("jldw")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="hsdw_<%=i%>" class="td"><%=list.getValue("hsdw")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="th_<%=i%>" class="td"><%=list.getValue("th")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="txm_<%=i%>" class="td"><%=list.getValue("txm")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="abc_<%=i%>" class="td"><%=list.getValue("abc")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="wzlbname_<%=i%>" class="td"><%=productKindBean.getLookupName(wzlbid)%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="chxzname_<%=i%>" class="td"><%=chxz.equals("1") ? "自制件" : chxz.equals("2") ? "外购件" : chxz.equals("3") ? "外协件" : chxz.equals("4") ? "虚拟件" : ""%></td>
    </tr>
    <%  list.next();
    }
    for(; i < 20; i++){
  %>
    <tr>
      <td class="td"></td>
      <td nowrap class="td">&nbsp;</td>
      <td class="td"></td>
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
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
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
          <TABLE cellspacing=3 cellpadding=0 border=0><%productSelect.table.printWhereInfo(pageContext);%>
            <%--TR>
              <TD nowrap class="td">物资编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 160px" name="cpbm" value='<%=productSelect.getFixedQueryValue("cpbm")%>' onKeyDown="return getNextElement();">
              </TD>
              <TD class="td" nowrap>品名 规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 160px" name="product" value='<%=productSelect.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();">
              </TD>
            </TR>
            <TR>
              <TD nowrap class="td">助记码</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 160px" name="zym" value='<%=productSelect.getFixedQueryValue("zym")%>' onKeyDown="return getNextElement();"></TD>
              <TD nowrap class="td">图号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 160px" name="th" value='<%=productSelect.getFixedQueryValue("th")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>条形码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 160px" name="txm" value='<%=productSelect.getFixedQueryValue("txm")%>' onKeyDown="return getNextElement();">
              </TD>
              <TD class="td" nowrap>物资类别</TD>
              <TD class="td" nowrap><pc:select name="wzlbid" addNull="1" style="width:160">
                <%=productKindBean.getList(productSelect.getFixedQueryValue("wzlbid"))%></pc:select></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>ABC分类</TD>
              <TD class="td" nowrap><pc:select name="abc" style="width:110" addNull="1" style="width:160" value='<%=productSelect.getFixedQueryValue("abc")%>'>
			  	<pc:option value="A">A</pc:option><pc:option value="B">B</pc:option><pc:option value="C">C</pc:option>
              </pc:select></TD>
              <TD class="td" nowrap>存货性质</TD>
              <TD class="td" nowrap><pc:select name="chxz" addNull="1" style="width:160" value='<%=productSelect.getFixedQueryValue("chxz")%>'>
               <pc:option value="1">自制件</pc:option><pc:option value="2">外购件</pc:option>
			   <pc:option value="3">外协件</pc:option><pc:option value="4">虚拟件</pc:option>
              </pc:select></TD>
            </TR--%>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <input class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
          </TABLE></TD></TR></TABLE></DIV>
</form>
<%if("1".equals(request.getParameter("init")))out.print(productSelect.showJavaScript("showFixedQuery();"));
out.print(retu);%>
</body>
</html>
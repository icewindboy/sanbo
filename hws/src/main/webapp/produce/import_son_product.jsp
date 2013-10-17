<%--物料可替换件中引入子件--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.*, engine.erp.baseinfo.B_Product"%>
<%
  engine.erp.produce.ImportSonProduct ImportSonProductBean = engine.erp.produce.ImportSonProduct.getInstance(request);//创建实例
  LookUp productKindBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT_KIND);
  String retu = ImportSonProductBean.doService(request, response);
  String curUrl = request.getRequestURL().toString();
%>
<html><head><title>选择产品</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(retu.indexOf("toProduct()") > 0)
  {
    out.println("<script language='javascript'>function toProduct(){location.href='../baseinfo/productinfomodify.jsp?"
      +"operate=" + B_Product.OPERATE_COPY_OTHER + "&src="+ curUrl+"';}</script>");
    out.print(retu);
    return;
  }%>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function selectProduct(row)
{
<%if(ImportSonProductBean.isMultiSelect){%>
  var multiId = '';
  for(var i=0;i<form1.elements.length;i++)
  {
    var e = form1.elements[i];
    if(e.type == "checkbox" && e.name!='checkform' && e.checked == true)
      multiId += e.value+',';
  }
  if(multiId != ''){
    multiId += '-1';
  <%if(ImportSonProductBean.multiIdInput != null){
      String mutiId = "window.opener."+ImportSonProductBean.srcFrm+"."+ImportSonProductBean.multiIdInput;
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
  String inputName[] = ImportSonProductBean.inputName;
  String fieldName[] = ImportSonProductBean.fieldName;
  if(inputName != null && fieldName != null)
  {
    int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
    for(int i=0; i< length; i++)
    {
      out.println("obj = document.all['"+fieldName[i]+"_'+row];");
      out.print("window.opener."+ImportSonProductBean.srcFrm+"."+inputName[i]);
      out.print(".value=");
      out.println("obj."+ (fieldName[i].equalsIgnoreCase("cpid") || fieldName[i].equalsIgnoreCase("wzlbid") || fieldName[i].equalsIgnoreCase("chxz") ||
                           fieldName[i].equalsIgnoreCase("pm") || fieldName[i].equalsIgnoreCase("gg")|| fieldName[i].equalsIgnoreCase("bomid")?"value;":"innerText;"));
    }
  }
  if(ImportSonProductBean.getMethodName() != null)
    out.print("window.opener."+ImportSonProductBean.getMethodName()+";");
}
%>
window.close();
}
function checkRadio(row){
  selectRow();
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = <%=ImportSonProductBean.isMultiSelect ? "!form1.sel.checked" : "true"%>;
  else
    form1.sel[row].checked = <%=ImportSonProductBean.isMultiSelect ? "!form1.sel[row].checked" : "true"%>;
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">可选物资列表</TD>
  </TR></TABLE>
<%EngineDataSet list = ImportSonProductBean.getOneTable();
  //pageContext.setAttribute("divName", "searchframe1");
  //pageContext.setAttribute("hideFunction", "hideFrame('searchframe1')");
  //pageContext.setAttribute("submitFunction", "sumitForm("+Operate.MASTER_SEARCH+")");
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
<table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
  <tr>
    <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="15"/></td>
      <td class="td" align="right">
        <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value=" 选用 " onKeyDown="return getNextElement();">
        <%}%>
        <input name="button2" type="button" class="button" onClick="window.close();" value=" 返回(C)" onKeyDown="return getNextElement();"></td>
       <pc:shortcut key="c" script='window.close();'/>
  </tr>
</table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td height="20" width=12 nowrap><%=ImportSonProductBean.isMultiSelect ? "<input type='checkbox' name='checkform' onclick='checkAll(form1,this);' onKeyDown='return getNextElement();'>" : "&nbsp;"%></td>
      <td nowrap>物资编码</td>
      <td nowrap>助记码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>单位</td>
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
      String bomid=list.getValue("bomid");
  %>
    <tr <%if(!ImportSonProductBean.isMultiSelect){%>onDblClick="checkRadio(<%=i%>);selectProduct(<%=i%>);"<%}%>>
      <td class="td">
        <%if(ImportSonProductBean.isMultiSelect){%>
        <input type="checkbox" name="sel" value="<%=list.getValue("cpid")%>" onKeyDown="return getNextElement();">
        <%}else{%>
        <input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>>
        <%}%><input type="hidden" id="cpid_<%=i%>" value='<%=list.getValue("cpid")%>'>
        <input type="hidden" id="wzlbid_<%=i%>" value='<%=wzlbid%>'>
        <input type="hidden" id="chxz_<%=i%>" value='<%=chxz%>'>
        <input type="hidden" id="pm_<%=i%>" value='<%=list.getValue("pm")%>'>
        <input type="hidden" id="gg_<%=i%>" value='<%=list.getValue("gg")%>'>
        <input type="hidden" id="bomid_<%=i%>" value='<%=list.getValue("bomid")%>'>
      </td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="cpbm_<%=i%>" class="td"><%=list.getValue("cpbm")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="zjm_<%=i%>" class="td"><%=list.getValue("zjm")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="product_<%=i%>" class="td"><%=list.getValue("product")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="jldw_<%=i%>" class="td"><%=list.getValue("jldw")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="th_<%=i%>" class="td"><%=list.getValue("th")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="txm_<%=i%>" class="td"><%=list.getValue("txm")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="abc_<%=i%>" class="td"><%=list.getValue("abc")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="wzlbname_<%=i%>" class="td"><%=productKindBean.getLookupName(wzlbid)%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="chxzname_<%=i%>" class="td"><%=chxz.equals("1") ? "自制件" : chxz.equals("2") ? "外购件" : chxz.equals("3") ? "外协件" : chxz.equals("4") ? "虚拟件" : ""%></td>
    </tr>
    <%  list.next();
    }
    for(; i < 15; i++){
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
    </tr>
    <%}%>
  </table>
  </form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
</SCRIPT>
<%out.print(retu);%>
</body>
</html>
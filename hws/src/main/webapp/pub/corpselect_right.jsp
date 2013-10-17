<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<%engine.erp.common.B_CorpSelect corpSelectBean = engine.erp.common.B_CorpSelect.getInstance(request);
  String retu = corpSelectBean.doService(request, response);
  EngineDataSet ds = corpSelectBean.getOneTable();
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp areaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_AREA);
%>
<html><head><title>选择客户</title>
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
function selectCust(row)
{
<%if(corpSelectBean.isMultiSelect){%>
  var multiId = '';
  for(var i=0;i<form1.elements.length;i++)
  {
    var e = form1.elements[i];
    if(e.type == "checkbox" && e.name!='checkform' && e.checked == true)
      multiId += e.value+',';
  }
  if(multiId != ''){
    multiId += '-1';
  <%if(corpSelectBean.multiIdInput != null){
      String mutiId = "parent.opener."+corpSelectBean.srcFrm+"."+corpSelectBean.multiIdInput;
      out.print(mutiId+".value=multiId;");
      out.print(mutiId+".onchange();");//"parent.opener.sumitForm("+Operate.CUST_MULTI_SELECT+");");
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
  String inputName[] = corpSelectBean.inputName;
  String fieldName[] = corpSelectBean.fieldName;
  if(inputName != null && fieldName != null)
  {
    int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
    for(int i=0; i< length; i++)
    {
      out.println("obj = document.all['"+fieldName[i]+"_'+row];");
      out.print  ("parent.opener."+corpSelectBean.srcFrm+"."+inputName[i]);
      out.println(".value=obj.innerText;");
      //out.println("obj.innerText;");
      /*+ (fieldName[i].equalsIgnoreCase("dwtxid") || fieldName[i].equalsIgnoreCase("khh") ||
                           fieldName[i].equalsIgnoreCase("nsrdjh") || fieldName[i].equalsIgnoreCase("zh")  ||
                           fieldName[i].equalsIgnoreCase("hsbl") || fieldName[i].equalsIgnoreCase("yfkje") ||
                           fieldName[i].equalsIgnoreCase("dqh") || fieldName[i].equalsIgnoreCase("yfdj") ||
                           fieldName[i].equalsIgnoreCase("zp")
                           ? "value;" : "innerText;"));
      */
    }
  }
  if(corpSelectBean.getMethodName() != null)
    out.print("parent.opener."+corpSelectBean.getMethodName()+";");
}
%>
parent.close();
}
function checkRadio(row){
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = <%=corpSelectBean.isMultiSelect ? "!form1.sel.checked" : "true"%>;
  else
    form1.sel[row].checked = <%=corpSelectBean.isMultiSelect ? "!form1.sel[row].checked" : "true"%>;
}
function showSearchFrame(){
  showFrame('searchframe1', true, "", true);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">可选客户列表</TD>
  </TR></TABLE>
<%EngineDataSet list = corpSelectBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table id="tbcontrol" width="100%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="20"/></td>
      <td class="td" align="right" nowrap>
        <input name="search" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery();'/>
        <%if(list.getRowCount()>0){%><input name="button1" type="button" class="button" onClick="selectCust();" value="选用(X)" onKeyDown="return getNextElement();">
        <pc:shortcut key="x" script='selectCust();'/><%}%>
        <input name="button2" type="button" class="button" onClick="parent.close();" value="返回(C)" onKeyDown="return getNextElement();"></td>
        <pc:shortcut key="c" script='parent.close();'/>
    </tr>
  </table>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td width=12 align="center" nowrap><%=corpSelectBean.isMultiSelect ? "<input type='checkbox' name='checkform' onclick='checkAll(form1,this);' onKeyDown='return getNextElement();'>" : "&nbsp;"%></td>
      <td nowrap>单位编号</td>
      <td nowrap>助记码</td>
      <td nowrap>单位名称</td>
      <td nowrap>区号</td>
      <td nowrap>地区</td>
      <td nowrap>地址</td>
      <td nowrap>电话</td>
      <td nowrap>传真</td>
      <td nowrap>联系人</td>
    </tr>
    <%list.first();
      int i=0;
      boolean hasYfdj = list.hasColumn("yfdj") != null;
      for(; i<list.getRowCount(); i++)   {
        String dqh = list.getValue("dqh");
        RowMap areaRow = areaBean.getLookupRow(dqh);
    %>
    <tr onClick="selectRow()" <%if(!corpSelectBean.isMultiSelect){%>onDblClick="checkRadio(<%=i%>);selectCust(<%=i%>);"<%}%>>
      <td width=12 align="center" class="td" nowrap>
        <%if(corpSelectBean.isMultiSelect){%>
        <input type="checkbox" name="sel" value="<%=list.getValue("dwtxid")%>" onKeyDown="return getNextElement();">
        <%}else{%>
        <input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>>
        <%}%><div style="display:none" id="dwtxid_<%=i%>"><%=list.getValue("dwtxid")%></div>
        <div style="display:none" id="khh_<%=i%>"><%=list.getValue("khh")%></div>
        <div style="display:none" id="zh_<%=i%>"><%=list.getValue("zh")%></div>
        <div style="display:none" id="nsrdjh_<%=i%>"><%=list.getValue("nsrdjh")%></div>
        <div style="display:none" id="dqh_<%=i%>"><%=dqh%></div>
        <div style="display:none" id="yfkje_<%=i%>"><%=list.getValue("yfkje")%></div>
        <div style="display:none" id="yskje_<%=i%>"><%=list.getValue("yskje")%></div>
        <div style="display:none" id="zp_<%=i%>"><%=list.getValue("zp")%></div>
        <div style="display:none" id="personid_<%=i%>"><%=list.getValue("personid")%></div>
        <div style="display:none" id="xm_<%=i%>"><%=list.getValue("xm")%></div>
        <%if(hasYfdj){%><div style="display:none" id="yfdj_<%=i%>"><%=list.getValue("yfdj")%></div><%}%>
      </td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="dwdm_<%=i%>" class="td"><%=list.getValue("dwdm")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="zjm_<%=i%>" class="td"><%=list.getValue("zjm")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="dwmc_<%=i%>" class="td"><%=list.getValue("dwmc")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="areacode_<%=i%>" class="td"><%=areaRow.get("areacode")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="dqmc_<%=i%>" class="td"><%=areaRow.get("dqmc")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="addr_<%=i%>" class="td"><%=list.getValue("addr")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="tel_<%=i%>" class="td"><%=list.getValue("tel")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="cz_<%=i%>" class="td"><%=list.getValue("cz")%></td>
      <td onClick="checkRadio(<%=i%>)" nowrap id="lxr_<%=i%>" class="td"><%=list.getValue("lxr")%></td>
    </tr>
    <%  list.next();
      }
      for(; i < 20; i++){
    %>
    <tr>
      <td class="td"></td>
      <td nowrap class="td">&nbsp;</td>
      <td nowrap class="td">&nbsp;</td>
      <td nowrap class="td">&nbsp;</td>
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
    <TABLE cellspacing=3 cellpadding=0 border=0 align=center>
      <TR>
        <TD><table cellspacing=3 cellpadding=0 border=0><%corpSelectBean.table.printWhereInfo(pageContext);%>
            <%--tr>
              <td nowrap class="td">单位编号</td>
              <td class="td" nowrap><input class="edbox" style="WIDTH: 160px" name="dwdm" value='<%=corpSelectBean.getFixedQueryValue("dwdm")%>' onKeyDown="return getNextElement();">
              </td>
              <td class="td" nowrap align="center">单位名称</td>
              <td class="td" nowrap><input class="edbox" style="WIDTH: 160px" name="dwmc" value='<%=corpSelectBean.getFixedQueryValue("dwmc")%>' onKeyDown="return getNextElement();">
              </td>
            </tr>
            <tr>
              <td nowrap class="td">地区</td>
              <td nowrap class="td"><pc:select name="dqh" addNull="1" style="width:160">
                  <%=areaBean.getList(corpSelectBean.getFixedQueryValue("dqh"))%></pc:select></td>
              <td nowrap class="td">地址</td>
              <td nowrap class="td"><input class="edbox" style="WIDTH: 160px" name="addr" value='<%=corpSelectBean.getFixedQueryValue("addr")%>' onKeyDown="return getNextElement();"></td>
            </tr>
            <tr>
              <td class="td" nowrap>电话</td>
              <td class="td" nowrap> <input class="edbox" style="WIDTH: 160px" name="tel" value='<%=corpSelectBean.getFixedQueryValue("tel")%>' onKeyDown="return getNextElement();">
              </td>
              <td class="td" nowrap>传真</td>
              <td class="td" nowrap><input class="edbox" style="WIDTH: 160px" name="cz" value='<%=corpSelectBean.getFixedQueryValue("cz")%>' onKeyDown="return getNextElement();">
              </td>
            </tr>
            <tr>
              <td class="td" nowrap>电子邮件</td>
              <td class="td"><input class="edbox" style="WIDTH: 160px" name="email"  value='<%=corpSelectBean.getFixedQueryValue("email")%>' onKeyDown="return getNextElement();"></td>
              <td class="td">联系人</td>
              <td class="td"><input class="edbox" style="WIDTH: 160px" name="lxr"  value='<%=corpSelectBean.getFixedQueryValue("lxr")%>' onKeyDown="return getNextElement();"></td>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <td class="td">助记码</td>
              <td class="td"><input type="text" class="edbox"style="WIDTH: 160px" name="zjm" value='<%=corpSelectBean.getFixedQueryValue("zjm")%>'></td>
            </tr--%>
            <tr>
              <td nowrap colspan=5 height=30 align="center"><input class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <input class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </td>
            </tr>
          </table></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%if("1".equals(request.getParameter("init")))out.print(corpSelectBean.showJavaScript("showFixedQuery();"));
out.print(retu);%>
</body>
</html>
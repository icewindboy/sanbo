<%--生产计划多选销售合同主表界面--%>
<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
  private String selmethod = null;  //
%><%
  engine.erp.jit.PlanSelectSaleMaster planSelectSaleMasterBean = engine.erp.jit.PlanSelectSaleMaster.getInstance(request);
  String pageCode = "produce_plan";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  String retu = planSelectSaleMasterBean.doService(request, response);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp brandBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BRAND_ITEM);//

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
    selmethod = request.getParameter("selmethod");
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = planSelectSaleMasterBean.getOneTable();
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
  var multiId = '';

  for(var i=0;i<form1.elements.length;i++)
  {
    var e = form1.elements[i];
    //alert("name:"+e.name+",type:"+e.type + ",checked:"+e.checked);
    if(e.type == "checkbox" && e.name!='checkform' && e.checked == true)
      multiId += e.value+',';
  }
  if(multiId.length > 0){
    multiId += '-1';
  <%if(multiIdInput != null){
      String pref =  "window.opener."+srcFrm+".";
      out.print(pref + multiIdInput +".value=multiId;");
      out.print(pref + selmethod+".value=getMethodValue();");
      out.print(pref + multiIdInput +".onchange();");
    }%>
  }
  window.close();
}
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
function getMethodValue()
{
  var selmethodval = 0;
  if(form1.selmethod[0].checked)
    selmethodval = form1.selmethod[0].value;
  else if(form1.selmethod[1].checked)
    selmethodval = form1.selmethod[1].value;
  return selmethodval;
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">销售合同清单</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "97"; pageContext.setAttribute(key, list);
       int iPage = 19; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
       </tr>
       <tr>
      <td class="td" align="right"><%--input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();"--%>
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value=" 选用 " onKeyDown="return getNextElement();">
      <%}%>
        <input  type="button" class="button" onClick="window.close();" value=" 返回(C)" onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script='window.close();'/></td>
    </tr>
	<tr>
	<td nowrap class="td"><input name="selmethod" type="radio" value="0" checked>
        <b>订货量全额生产(订单数量和可供量差额生产)</b>
        <input name="selmethod" type="radio" value="1"><b>可供量考虑最低库存量</b>
       <input name="selmethod" type="radio" value="2"><b>订货量全额生产(不考虑可供量和最低库存量)</b>
      </td>
	  <td></td>
	</tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>合同编号</td>
      <td nowrap>合同日期</td>
      <td nowrap>需方单位</td>
      <td nowrap>开始日期</td>
      <td nowrap>结束日期</td>
      <td nowrap>总数量</td>
      <!--td nowrap>总金额</td-->
      <td nowrap>业务员</td>
      <td nowrap>包装要求</td>
      <td nowrap>包边布颜色</td>
      <td nowrap>水 洗 标</td>
      <td nowrap>商    标</td>
      <td nowrap>备注</td>
    </tr>
    <%
      brandBean.regData(list,"brandid");
      corpBean.regData(list,"dwtxid");
      personBean.regData(list,"personid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr onClick="checkTr(<%=i%>)">
      <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=list.getValue("htid")%>' onKeyDown="return getNextElement();"><input type="hidden" id="htid_<%=i%>" value='<%=list.getValue("htid")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="htbh_<%=i%>" class="td"><%=list.getValue("htbh")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="htrq_<%=i%>" class="td"><%=list.getValue("htrq")%></td>
      <td nowrap id="dwtxid_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="ksrq_<%=i%>" class="td"><%=list.getValue("ksrq")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jsrq_<%=i%>" class="td"><%=list.getValue("jsrq")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="zsl_<%=i%>" class="td" align="right"><%=list.getValue("zsl")%></td>
      <%--<td nowrap onClick="checkRadio(<%=i%>)" id="zje_<%=i%>" class="td" align="right"><%=list.getValue("zje")%></td>--%>
      <td nowrap onClick="checkRadio(<%=i%>)" id="personid_<%=i%>" class="td" ><%=personBean.getLookupName(list.getValue("personid"))%></td>

      <td nowrap onClick="checkRadio(<%=i%>)" id="pkgdmd_<%=i%>" class="td" ><%=list.getValue("pkgdmd")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="pkgcl_<%=i%>" class="td" ><%=list.getValue("pkgcl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="wash_<%=i%>" class="td" ><%=list.getValue("wash")%></td>
      <td noWrap  class="td">
          <%
          RowMap brandTypeRow = brandBean.getLookupRow(list.getValue("brandid"));
          %>
      <%=brandTypeRow.get("brandname")%>
      </td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="qtxx_<%=i%>" class="td" ><%=list.getValue("qtxx")%></td>
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
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    <td class="td"></td>
    </tr>
    <%}%>
  </table>
</form>
<%out.print(retu);%>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</body>
</html>
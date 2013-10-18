<!--引设备名称--><%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.*,java.util.*"%><%!
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
  private String equipmentid = null;
  private String equipmentCode = null;
  private String equipmentName = null;

%>
<%
if(!loginBean.hasLimits("record_card", request, response))
 return;
String retu="";
engine.erp.equipment.B_EquipmentSelect b_EquipmentSelectBean = engine.erp.equipment.B_EquipmentSelect.getInstance(request);
engine.project.LookUp equipmentBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_EQUIPMENT);//设备
String operate = request.getParameter("operate");
if(operate !=null && operate.equals(String.valueOf(Operate.INIT)))
{
  srcFrm = request.getParameter("srcFrm");
  multiIdInput = request.getParameter("srcVar");
  isMultiSelect = request.getParameter("multi") != null && request.getParameter("multi").equals("1");
  inputName = request.getParameterValues("srcVar");
  fieldName = request.getParameterValues("fieldVar");
  methodName = request.getParameter("methodName");
  equipmentid = request.getParameter("equipmentid");
  equipmentCode=request.getParameter("equipmentCode");
  equipmentName=request.getParameter("equipmentName");
  retu = b_EquipmentSelectBean.doService(request, response);
}
  if(retu.indexOf("location.href=")>-1)
    return;
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//部门
  EngineDataSet list = b_EquipmentSelectBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  deptBean.regData(list,"deptid");
  deptBean.regData(list,"user_deptid");
  if(String.valueOf(Operate.PROD_CHANGE).equals(operate)|| String.valueOf(Operate.PROD_NAME_CHANGE).equals(operate))
  {
    srcFrm = request.getParameter("srcFrm");
    inputName = request.getParameterValues("srcVar");
    fieldName = request.getParameterValues("fieldVar");
    methodName = request.getParameter("methodName");
    String code=request.getParameter("code");
    String name=request.getParameter("name");
    out.print("<script language='javascript'>");
    String fieldValue=b_EquipmentSelectBean.code_name_Change(code,name,operate);
    if(fieldValue.equals("more")){
      String srcVar=null;
      String fieldVar=null;
      for(int i=0;i<inputName.length;i++){
        srcVar=srcVar+"srcVar="+inputName[i]+"&";
      }
      srcVar=srcVar.substring(4,srcVar.lastIndexOf("&"));

      for(int i=0;i<fieldName.length;i++){
        fieldVar=fieldVar+"fieldVar="+fieldName[i]+"&";
      }
      fieldVar=fieldVar.substring(4,fieldVar.lastIndexOf("&"));
      out.print("parent.equipmentSingleSelect('"+srcFrm+"','"+srcVar+"','"+fieldVar+"','"+methodName+"','"+code+"','"+name+"')");
    }
    else if(fieldValue.equals("none")){
      out.print("alert('该设备不存在');");
      out.print("parent."+srcFrm+"."+inputName[0]+".value='';");
      out.print("parent."+srcFrm+"."+inputName[1]+".value='';");
      out.print("parent."+srcFrm+"."+inputName[2]+".value='';");
    }
    else {
        String equipmentName=equipmentBean.getLookupName(fieldValue);
        RowMap equipmentRow =equipmentBean.getLookupRow(fieldValue);
        String equipmentCode=equipmentRow.get("equipment_code");
        out.print("parent."+srcFrm+"."+inputName[0]+".value='"+fieldValue+"';");
        out.print("parent."+srcFrm+"."+inputName[1]+".value='"+equipmentCode+"';");
        out.print("parent."+srcFrm+"."+inputName[2]+".value='"+equipmentName+"';");
        if(methodName != null&&!methodName.equals(""))
          out.print("window.parent."+ methodName +";");
    }
    out.print("</script>");
  }
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
function sumitForm(oper, row)
{
  //新增或修改
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function selectequipment(row)
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
      out.print(".value=");
      out.println("obj."+ (fieldName[i].equals("equipmentid")?"value;":"innerText;"));
    }
  }
  if(methodName != null&&!methodName.equals(""))
    out.print("window.opener."+ methodName +";");
}
%>
window.close();
}
function Radio(row){
  selectRow();
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = <%=isMultiSelect ? "!form1.sel.checked" : "true"%>;
  else
    form1.sel[row].checked = <%=isMultiSelect ? "!form1.sel[row].checked" : "true"%>;
}
</script>

<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">设备履历卡</TD>
  </TR>
</TABLE>

<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
   <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
      int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
      <td class="td" nowrap align="right">
     <%if(list.getRowCount()>0){%>
      <input name="button1" type="button" class="button" onClick="selectequipment();" value=" 选用 " onKeyDown="return getNextElement();">
    <%}%>
      <input  type="button" class="button" onClick="window.close();" value=" 返回(C)" onKeyDown="return getNextElement();">
      <pc:shortcut key="c" script='window.close();'/>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
	<tr class="tableTitle">
	  <td nowrap></td>
	  <td nowrap>设备编码</td>
	  <td nowrap>设备名称</td>
	  <td nowrap>型号规格</td>
	  <td nowrap>单位</td>
	  <td nowrap>制造厂</td>
	  <td nowrap>制造日期</td>
          <td nowrap>购买日期</td>
	  <td nowrap>使用部门</td>
	  <td nowrap>存放地点</td>
	  <td nowrap>购置金额</td>
	  <td nowrap>购入部门</td>
	  <td nowrap>开始使用日期</td>
	</tr>
       <%
          list.first();
          int i=0;
          for(;i<list.getRowCount();i++)
          {
       %>
          <tr onDblClick="Radio(<%=i%>);selectequipment(<%=i%>);">
	  <td class="td"><input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>></td>
	  <td class="td" nowarp class=td  onClick="Radio(<%=i%>)" style="display:none"><input type="hidden" name="equipmentid_<%=i%>" value="<%=list.getValue("equipmentid")%>"></td>
	  <td class="td" nowarp class=td id="equipment_code_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("equipment_code")%></td>
	  <td class="td" nowarp class=td id="equipment_name_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("equipment_name")%></td>
	  <td class="td" nowarp class=td id="standard_gg_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("standard_gg")%></td>
	  <td class="td" nowarp class=td id="unit_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("unit")%></td>
	  <td class="td" nowarp class=td id="manufacturer_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("manufacturer")%></td>
	  <td class="td" nowarp class=td id="manufacturedate_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("manufacturedate")%></td>
          <td class="td" nowarp class=td id="buy_date_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("buy_date")%></td>
          <td  id="use_deptid_<%=i%>" style="display:none"><%=list.getValue("use_deptid")%></td>
	  <td class="td" nowarp class=td id="use_dept_<%=i%>" onClick="Radio(<%=i%>)"><%=deptBean.getLookupName(list.getValue("use_deptid"))%></td>
	  <td class="td" nowarp class=td id="depositary_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("depositary")%></td>
	  <td class="td" nowarp class=td id="buy_money_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("buy_money")%></td>
	  <td class="td" nowarp class=td id="deptid_<%=i%>" onClick="Radio(<%=i%>)"><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
          <td class="td" nowarp class=td id="use_date_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("use_date")%></td>
	</tr>
        <%  list.next();
          }
          for(; i < loginBean.getPageSize(); i++){
        %>
	<tr >
	  <td >&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
	  <td nowrap class="td">&nbsp;</td>
          <td nowrap class="td">&nbsp;</td>
	</tr>
        <%}%>
  </table>
</form>
<script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
</script>
<%out.print(retu);%>
</body>
</html>

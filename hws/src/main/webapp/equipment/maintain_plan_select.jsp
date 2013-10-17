<!--引保养计划单--><%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
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
  private String maintainPlanID = null;
%>
<%
String operate = request.getParameter("operate");
if(operate !=null && operate.equals(String.valueOf(Operate.INIT)))
{
  srcFrm = request.getParameter("srcFrm");
  multiIdInput = request.getParameter("srcVar");
  isMultiSelect = request.getParameter("multi") != null && request.getParameter("multi").equals("1");
  inputName = request.getParameterValues("srcVar");
  fieldName = request.getParameterValues("fieldVar");
  methodName = request.getParameter("method");
  maintainPlanID = request.getParameter("maintainPlanID");
  }
  engine.erp.equipment.B_MaintainPlanSelect b_MaintainPlanSelectBean = engine.erp.equipment.B_MaintainPlanSelect.getInstance(request);
  String retu = b_MaintainPlanSelectBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//部门
  LookUp personBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_PERSON);//人员
  LookUp equipmentBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_EQUIPMENT);//设备
  EngineDataSet list = b_MaintainPlanSelectBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  deptBean.regData(list,"deptid");
  personBean.regData(list,"personid");
  equipmentBean.regData(list,"equipmentid");
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
      out.println("obj."+ (fieldName[i].equals("maintainapplyid")?"value;":"innerText;"));
    }
  }
  if(methodName != null)
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
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD NOWRAP align="center">设备保养计划</TD>
  </TR>
</TABLE>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
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
      <pc:shortcut key="c" script='window.close();'/></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20>
      </td>
      <td nowrap>单据号</td>
      <td nowrap>设备编码</td>
      <td nowrap>设备名称</td>
      <td nowrap>型号规格</td>
      <td nowrap>制造厂</td>
      <td nowrap>购买日期</td>
      <td nowrap>计划部门</td>
      <td nowrap>计划人</td>
      <td nowrap>计划日期</td>
     </tr>
       <%
          list.first();
          int i=0;
          for(;i<list.getRowCount();i++)
          {
            RowMap equipmentRow = equipmentBean.getLookupRow(list.getValue("equipmentid"));
      %>
      <tr onDblClick="Radio(<%=i%>);selectequipment(<%=i%>);">
      <td nowrap class=td>
      <input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>></td>
      <td id="maintainPlanID_<%=i%>"  style="display:none" ><%=list.getValue("maintainplanid")%></td>
      <td id="equipmentid_<%=i%>"  style="display:none" ><%=list.getValue("equipmentid")%></td>
      <td class="td" nowarp id="maintainPlanNO_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("maintainPlanNO")%></td><!--单据号-->
      <td class="td" nowarp id="equipment_code_<%=i%>" onClick="Radio(<%=i%>)"><%=equipmentRow.get("equipment_code")%></td><!--设备编码-->
      <td class="td" nowarp id="equipment_name_<%=i%>" onClick="Radio(<%=i%>)"><%=equipmentRow.get("equipment_name")%></td><!--设备名称-->
      <td class="td" nowarp id="standard_gg_<%=i%>" onClick="Radio(<%=i%>)"><%=equipmentRow.get("standard_gg")%></td><!--型号规格-->
      <td class="td" nowarp id="manufacturer_<%=i%>" onClick="Radio(<%=i%>)"><%=equipmentRow.get("manufacturer")%></td>
      <td class="td" nowarp id="buy_date_<%=i%>" onClick="Radio(<%=i%>)"><%=equipmentRow.get("buy_date")%></td>
      <td class="td" nowarp id="deptid_<%=i%>" onClick="Radio(<%=i%>)"><%=deptBean.getLookupName(list.getValue("deptid"))%></td><!--计划部门-->
      <td class="td" nowarp id="personid_<%=i%>" onClick="Radio(<%=i%>)"><%=personBean.getLookupName(list.getValue("personid"))%></td><!--计划人-->
      <td class="td" nowarp id="plan_date_<%=i%>" onClick="Radio(<%=i%>)"><%=list.getValue("plan_date")%></td><!--计划日期-->
     </tr>
      <%  list.next();
      }
      for(; i < loginBean.getPageSize()-4; i++){
      %>
      <tr id="tr_2">
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
    </tr>
   <%}%>
  </table>
</form>
<script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
</script>
<%out.print(retu);%>
</body>
</html>

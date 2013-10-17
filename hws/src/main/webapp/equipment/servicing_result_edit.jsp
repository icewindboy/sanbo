<!--设备维修记录-->
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*,java.math.BigDecimal,java.text.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode  = "equipment_result";
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
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='equipment_result.jsp';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=pm_'+i+'',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=pm_'+i+'',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function deptchange(){
  associateSelect(document.all['prod'], 'emp', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
///////////////////////////////////////////////////选择设备
function equipmentSingleSelect(frmName, srcVar,fieldVar,methodName,code,name,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=930 height=570 top=0 left=0";
  var winName= "GoodsProdSelector";
  paraStr = "equipment_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&code="+code+"&name="+name+"&methodName="+methodName;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
function equipmentCodeSelect(obj,srcVars,methodName)
{
  equipmentCodeChange(document.all['prod'], obj.form.name, srcVars,
                 'fieldVar=equipmentid&fieldVar=equipment_code&fieldVar=equipment_name&fieldVar=use_deptid&fieldVar=use_dept', obj.value,methodName);
}
function equipmentNameSelect(obj,srcVars,method)
{
  equipmentNameChange(document.all['prod'], obj.form.name, srcVars,
                 'fieldVar=equipmentid&fieldVar=equipment_code&fieldVar=equipment_name', obj.value,method);
}

function equipmentCodeChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin)
{
  paraStr = "equipment_select.jsp?operate=53&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&code="+code+"&methodName="+methodName;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  iframeObj.src=paraStr;
}

function equipmentNameChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin)
{
  paraStr = "equipment_select.jsp?operate=54&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&name="+code;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  iframeObj.src=paraStr;
}
//////////////////////////////////////////////////////////////
function servicingSingleSelect(frmName, srcVar,fieldVar,methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=930 height=570 top=0 left=0";
  var winName= "GoodsProdSelector";
  paraStr = "servicing_requisition_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
function clear(){
form1.maintainapplyNO.value='';
}
</script>
<%
  if(!loginBean.hasLimits("equipment_result", request, response))
    return;
  engine.erp.equipment.B_EquipmentResult b_EquipmentResultBean = engine.erp.equipment.B_EquipmentResult.getInstance(request);
  LookUp productBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT);//产品编码
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);//检验员
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//部门
  LookUp equipmentBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_EQUIPMENT);//设备
  LookUp exceptionReasonBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_EQUIPMENT_EXCEPTIONREASON);//故障原因
  b_EquipmentResultBean.billType="0";
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<%
  String retu = b_EquipmentResultBean.doService(request, response);
if(retu.indexOf("backList();")>-1)
{
  out.print(retu);
  return;
}
String curUrl = request.getRequestURL().toString();
EngineDataSet ds = b_EquipmentResultBean.getMasterTable();
EngineDataSet list = b_EquipmentResultBean.getDetailTable();
RowMap masterRow = b_EquipmentResultBean.getMasterRowinfo();
RowMap[] detailRows= b_EquipmentResultBean.getDetailRowinfos();
productBean.regData(list,"cpid");
deptBean.regData(ds,"deptid");
equipmentBean.regData(ds,"equipmentid");
exceptionReasonBean.regData(list,"excepReasonid");
RowMap equipmentRow =equipmentBean.getLookupRow(masterRow.get("equipmentid"));
boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);
String stateval =masterRow.get("state");
boolean isdel=stateval.equals("0");
boolean isEnd=b_EquipmentResultBean.isApprove||(!b_EquipmentResultBean.masterIsAdd()&&!stateval.equals("0"));
isEnd=isEnd||!(b_EquipmentResultBean.masterIsAdd()?isCanAdd:isCanEdit);
String edClass = isEnd ? "class=edline" : "class=edbox";
String noneClass= isEnd ? "class=ednone" : "class=edbox";
String readonly = isEnd ? " readonly" : "";
String title= stateval.equals("1") ? "已审批":(stateval.equals("9") ? "审批中" : (stateval.equals("0")?"未审批":"未审批" ));
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();"  >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="billType" VALUE="0">
  <INPUT TYPE="HIDDEN" NAME="maintainapplyID" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td height="367">
<table width="150" height="17" border=0 CELLPADDING=0 CELLSPACING=0 class="table">
          <tr>
            <td width="150" class="activeVTab">设备维修记录(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="92%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="750" bgcolor="#f0f0f0">
    <tr>
      <td noWrap class="tdTitle">单据号</td>
      <td noWrap class="td"><input  type="text" name="maintainResultNO" value='<%=masterRow.get("maintainResultNO")%>' maxlength='32'  style="width:110"  class=edline onKeyDown="return getNextElement();" readonly></td>
       <td noWrap class="tdTitle">申请单号</td>
      <td noWrap class="td"><input  type="text" name="maintainapplyNO" value='<%=masterRow.get("maintainapplyNO")%>' maxlength='32'  style="width:110"  class=edline onKeyDown="return getNextElement();" readonly></td>
     <TD class="tdTitle" nowrap>设备名称</TD>
      <TD colspan="3" nowrap class="td">
     <INPUT <%=edClass%> <%=readonly%> style="WIDTH:70" id="equipment_code" name="equipment_code" value='<%=equipmentRow.get("equipment_code")%>' onKeyDown="return getNextElement();" onchange="equipmentCodeSelect(this,'srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name','sumitForm(<%=b_EquipmentResultBean.PUT_EQUIPMENTID%>)')">
     <INPUT <%=edClass%> <%=readonly%> style="WIDTH:180" id="equipment_name" name="equipment_name" value='<%=equipmentBean.getLookupName(masterRow.get("equipmentid"))%>' onKeyDown="return getNextElement();" onchange="equipmentNameSelect(this,'srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name','sumitForm(<%=b_EquipmentResultBean.PUT_EQUIPMENTID%>)')">
     <INPUT TYPE="HIDDEN" NAME="equipmentid" value="<%=masterRow.get("equipmentID")%>">
     <%if(!isEnd){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="equipmentSingleSelect('form1','srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name&srcVar=standard_gg&srcVar=use_dept&srcVar=use_deptid','fieldVar=equipmentid&fieldVar=equipment_code&fieldVar=equipment_name&fieldVar=standard_gg&fieldVar=use_dept&fieldVar=use_deptid','clear();','','')"><%}%>
     <%if(!isEnd){%><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="equipmentid.value='';equipment_code.value='';equipment_name.value='';standard_gg.value='';use_dept.value='';">
      <%}%></TD>
      </td>
    </tr>
    <tr>
     <td noWrap class="tdTitle">型号规格</td>
      <td noWrap><input type="text" name="standard_gg" value='<%=equipmentRow.get("standard_gg")%>' maxlength='20' style="width:110" class=edline readonly onKeyDown="return getNextElement();" >
      <td noWrap class="tdTitle">使用部门</td>
      <td noWrap class="td">
      <input type="text" name="use_dept"  value='<%=deptBean.getLookupName(equipmentRow.get("use_deptid"))%>' maxlength='20' style="width:110" class=edline readonly onKeyDown="return getNextElement();" >
      <input type="hidden" name="use_deptid" value='<%=equipmentRow.get("use_deptid")%>' ></td>
     <td noWrap class="tdTitle">维修日期</td>
      <td  noWrap class="td"> <input type="text" name="maintain_date" value='<%=masterRow.get("maintain_date")%>' maxlength='10' style="width:85" <%=edClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
     <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.maintain_date);"></a>
     <%}%></td>
    </tr>
    <tr>
     <%
//停用日期
String stop_date=masterRow.get("stop_date");
String stop_date_hour="";
String stop_date_minute="";
String newstop_date ="";
if(stop_date.length()>10){
  GregorianCalendar calendar=new GregorianCalendar();
  Date ksdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stop_date);
  calendar.setTime(ksdate);
  stop_date_hour=String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
  if(stop_date_hour.length()<2)stop_date_hour="0"+stop_date_hour;
  stop_date_minute=String.valueOf(calendar.get(Calendar.MINUTE));
  if(stop_date_minute.length()<2)stop_date_minute="0"+stop_date_minute;
  newstop_date =stop_date.substring(0,10).trim();
}
else newstop_date=stop_date;

String start_date=masterRow.get("start_date");
String start_date_hour="";
String start_date_minute="";
String newstart_date="";
if(start_date.length()>10){
  GregorianCalendar start_calendar=new GregorianCalendar();
  Date startdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
  start_calendar.setTime(startdate);
  start_date_hour=String.valueOf(start_calendar.get(Calendar.HOUR_OF_DAY));
  if(start_date_hour.length()<2)start_date_hour="0"+start_date_hour;
  start_date_minute=String.valueOf(start_calendar.get(Calendar.MINUTE));
  if(start_date_minute.length()<2)start_date_minute="0"+start_date_minute;
  newstart_date =start_date.substring(0,10).trim();
}
else newstart_date=start_date;
%>
     <td noWrap class="tdTitle">停用日期</td>
      <td colspan="3" noWrap class="td">
     <input type="text" name="stop_date" value='<%=newstop_date%>' maxlength='10' style="width:85"  <%=edClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
     <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.stop_date);"></a><%}%>
     <input type="text" name="stop_date_hour" value='<%=stop_date_hour%>' maxlength='20' style="width:30" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                     :
     <input type="text" name="stop_date_minute" value='<%=stop_date_minute%>' maxlength='20' style="width:30" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
      </td>
      <td noWrap class="tdTitle">启用日期</td>
      <td colspan="3" noWrap class="td">
    <input type="text" name="start_date" value='<%=newstart_date%>' maxlength='10' style="width:85" <%=edClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
     <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.start_date);"></a><%}%>
     <input  type="text" name="start_date_hour" value='<%=start_date_hour%>' maxlength='20' style="width:30" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                     :
     <input type="text" name="start_date_minute" value='<%=start_date_minute%>' maxlength='20' style="width:30" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
      </td>
    </tr>
    <tr>
    <td noWrap class="tdTitle">维修部门</td>
      <td noWrap class="td"><%if(!isEnd){%>
      <pc:select name="deptid"  style="width:110" addNull='1' value = '<%=masterRow.get("deptid")%>'>
      <%=deptBean.getList()%>
      </pc:select>
      <%}else{%>
      <input type="text"  value='<%=deptBean.getLookupName(masterRow.get("deptid"))%>'  style="width:110"  <%=edClass%> <%=readonly%>>
      <%}%> </td>
      <td noWrap class="tdTitle">维修类别</td>
      <td noWrap class="td">
     <%if(!isEnd){%>
        <pc:select name='maintain_type' style="width:110" addNull='0' value='<%=masterRow.get("maintain_type")%>'>
        <pc:option value="0">一般</pc:option>
        <pc:option value="1">重要</pc:option>
        </pc:select>
    <%}else{%>
    <input type="text"  value='<%
           String mymaintain_type=masterRow.get("maintain_type");
           if(mymaintain_type.equals("0"))out.print("一般");
           if(mymaintain_type.equals("1"))out.print("重要");
           %>'  style="width:110" <%=edClass%> <%=readonly%>>
    <%}%>
    </td>
      <td noWrap class="tdTitle">&nbsp;</td>
      <td noWrap class="td">&nbsp;</td>
      <td noWrap class="tdTitle">&nbsp;</td>
      <td noWrap class="td">&nbsp;</td>
    </tr>
    <tr>
      <td colspan="8" noWrap class="td">
  <div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
       <table id="tableview1" width="1300" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
      <tr class="tableTitle">
        <%if(!isEnd){%><td height='20' align="center" nowrap>
       <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
       <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/>
        </td><%}%>
        <td  nowrap with="80">故障描述</td>
        <td  nowrap>故障发生时间</td>
        <td  nowrap>计划排故障时间</td>
        <td  nowrap>故障产生原因</td>
        <td  nowrap>故障排除方法</td>
        <td  nowrap with="50">元件编码</td>
        <td  nowrap with="250">元件名称</td>
        <td  nowrap>故障排除时间</td>
        <td  nowrap>故障排除人</td>
        <td  nowrap with="70">备注</td>
      </tr>
        <%
          int i=0;
          int personidChangeTD=0;
          RowMap detail = null;
          for(; i<detailRows.length; i++)   {
          detail = detailRows[i];
          personidChangeTD=i+1;
         RowMap productRow =productBean.getLookupRow(detail.get("cpid")); %>
        <tr id="rowinfo_<%=i%>">
        <%if(!isEnd){%><td class="td"><div align="center">
         <input name="image232" type="image" class="img" title="选择元件" onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=pm_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product','','')" src="../images/select_prod.gif" width="15" height="15" border="0">
         <input name="image23" type="image" class="img" title="删除" ONCLICK="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)"  src="../images/del.gif" width="13" height="13" border="0">
       </div></td><%}%>
        <td class="td">
       <input type="text" name="fault_depict_<%=i%>" value='<%=detail.get("fault_depict")%>' maxlength='20' style="width:80" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" >
        </td>
        <td class="td">
<%
         String fault_time=detail.get("fault_time");//故障发生时间
         String faulthour="";
         String faultminute="";
         String newfault_time="";
         if(fault_time.length()>10){
           GregorianCalendar fault_calendar=new GregorianCalendar();
           Date faulttime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fault_time);
           fault_calendar.setTime(faulttime);
           faulthour=String.valueOf(fault_calendar.get(Calendar.HOUR_OF_DAY));
           if(faulthour.length()<2)faulthour="0"+faulthour;
           faultminute=String.valueOf(fault_calendar.get(Calendar.MINUTE));
           if(faultminute.length()<2)faultminute="0"+faultminute;
           newfault_time =fault_time.substring(0,10).trim();
         }
         else newfault_time=fault_time;

        String plan_debar_time=detail.get("plan_debar_time");//计划排故障时间
        String plan_debarhour="";
        String plan_debarminute="";
        String newplan_debar_time="";
        if(plan_debar_time.length()>10){
          GregorianCalendar plan_debar_calendar=new GregorianCalendar();
          Date plan_debartime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(plan_debar_time);
          plan_debar_calendar.setTime(plan_debartime);
          plan_debarhour=String.valueOf(plan_debar_calendar.get(Calendar.HOUR_OF_DAY));
          if(plan_debarhour.length()<2)plan_debarhour="0"+plan_debarhour;
          plan_debarminute=String.valueOf(plan_debar_calendar.get(Calendar.MINUTE));
          if(plan_debarminute.length()<2)plan_debarminute="0"+plan_debarminute;
          newplan_debar_time =plan_debar_time.substring(0,10).trim();
        }
         else newplan_debar_time=plan_debar_time;

      String debar_time=detail.get("debar_time");//排故障时间
      String debarhour="";
      String debarminute="";
      String newdebar_time="";
      if(debar_time.length()>10){
        GregorianCalendar debar_calendar=new GregorianCalendar();
        Date debartime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(debar_time);
        debar_calendar.setTime(debartime);
        debarhour=String.valueOf(debar_calendar.get(Calendar.HOUR_OF_DAY));
        if(debarhour.length()<2)debarhour="0"+debarhour;
        debarminute=String.valueOf(debar_calendar.get(Calendar.MINUTE));
        if(debarminute.length()<2)debarminute="0"+debarminute;
        newdebar_time =debar_time.substring(0,10).trim();
      }
         else newdebar_time=debar_time;
%>
       <input type="text" name="fault_time_<%=i%>" value='<%=newfault_time%>' maxlength='10' style="width:65" <%=noneClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
       <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.fault_time_<%=i%>);"></a><%}%>
       <input  type="text" name="fault_time_hour_<%=i%>" value='<%=faulthour%>' maxlength='20' style="width:20" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                     :
       <input type="text" name="fault_time_minute_<%=i%>" value='<%=faultminute%>' maxlength='20' style="width:20" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
        </td>
        <td class="td">
        <input type="text" name="plan_debar_time_<%=i%>" value='<%=newplan_debar_time%>' maxlength='10' style="width:65" <%=noneClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
       <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(plan_debar_time_<%=i%>);"></a><%}%>
       <input  type="text" name="plan_debar_time_hour_<%=i%>" value='<%=plan_debarhour%>' maxlength='20' style="width:20" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                     :
       <input type="text" name="plan_debar_time_minute_<%=i%>" value='<%=plan_debarminute%>' maxlength='20' style="width:20" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
        </td>
        <td class="td">
        <%if(!isEnd){%>
        <pc:select name='<%="excepReasonID_"+i%>' addNull="1" style="width:110"  value='<%=detail.get("excepReasonID")%>'>
        <%=exceptionReasonBean.getList()%> </pc:select>
        <%}else{%>
        <input type="text" value='<%=exceptionReasonBean.getLookupName(detail.get("excepReasonID"))%>'  style="width:130"  <%=noneClass%> <%=readonly%>>
        <%}%>
        </td>
        <td class="td"><input type="text" name="debar_method_<%=i%>" value='<%=detail.get("debar_method")%>' maxlength='20' style="width:100%" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" ></td>
        <input type="hidden" name="cpid_<%=i%>" value='<%=detail.get("cpid")%>'>
        <td class="td"><input type="text" <%=noneClass%> <%=readonly%> id="cpbm" name="cpbm_<%=i%>"  maxlength='20' style="width:100%" value='<%=productRow.get("cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this,<%=i%>)"></td>
        <td class="td"><input type="text" <%=noneClass%> <%=readonly%> id="cpmc" name="pm_<%=i%>"  maxlength='20' style="width:250" value='<%=productBean.getLookupName(detail.get("cpid"))%>' onKeyDown="return getNextElement();"  onchange="productNameSelect(this,<%=i%>)"></td>
        <td class="td"><input type="text" name="debar_time_<%=i%>" value='<%=newdebar_time%>' maxlength='10' style="width:65" <%=noneClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
       <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.debar_time_<%=i%>);"></a><%}%>
       <input  type="text" name="debar_time_hour_<%=i%>" value='<%=debarhour%>' maxlength='20' style="width:20" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                     :
       <input type="text" name="debar_time_minute_<%=i%>" value='<%=debarminute%>' maxlength='20' style="width:20" <%=noneClass%> <%=readonly%>onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
        </td>
        <td class="td">
      <%if(!isEnd){%>
      <pc:select name='<%="personid_"+i%>' addNull="1" style="width:110" value='<%=detail.get("personid")%>'>
      <%=personBean.getList()%> </pc:select>
      <%}else{%>
       <input type="text"  value='<%=personBean.getLookupName(detail.get("personid"))%>'  style="width:130"  <%=noneClass%> <%=readonly%>>
        <%}%></td>
        <td class="td"><input type="text" name="memo_<%=i%>" value='<%=detail.get("memo")%>' maxlength='20' style="width:70" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" ></td>
      </tr>
       <%
        list.next();
        }
        for(; i <5; i++){
        %>
        <tr id="rowinfo_<%=i%>">
        <%if(!isEnd){%><td class="td">&nbsp;</td><%}%>
        <td class="td">&nbsp;</td>
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
     </div></tr>
    <tr>
      <td  noWrap class="tdTitle">停产原因</td>
      <td colspan="7" noWrap class="td" ><textarea name="stop_causation" rows="4"   onKeyDown="return getNextElement();" <%=edClass%> <%=readonly%> style="width:690;height: 65"><%=masterRow.get("stop_causation")%></textarea></td>
    </tr>
    <tr>
      <td  noWrap class="tdTitle">结论</td>
      <td colspan="7" noWrap class="td"><textarea name="verdict" rows="4" onKeyDown="return getNextElement();" <%=edClass%> <%=readonly%> style="width:690;height: 65"><%=masterRow.get("verdict")%></textarea></td>
    </tr>
     </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("createDate")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("creator")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
              <%if(!isEnd&&isCanAdd){%>
              <input name="button22"  style="width:80" type="button" class="button" onClick="servicingSingleSelect('form1','srcVar=maintainapplyID&srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name&srcVar=standard_gg&srcVar=use_deptid&srcVar=use_dept&srcVar=maintainapplyNO','fieldVar=maintainapplyID&fieldVar=equipmentid&fieldVar=equipment_code&fieldVar=equipment_name&fieldVar=standard_gg&fieldVar=use_deptid&fieldVar=use_dept&fieldVar=maintainapplyNO','sumitForm(<%=b_EquipmentResultBean.EQUIPMENT_CHANGE%>)','');" value="引入维修申请">
              <input name="button2"  style="width:80" type="button" class="button" onClick="if(!checkpersonid()){return;}sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
               <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <%}if(!isEnd&&isCanAdd){%>
              <input name="btnback"  style="width:80" type="button" class="button" onClick="if(!checkpersonid()){return;}sumitForm(<%=Operate.POST%>);"  value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
               <%}if(isdel&&isCanDelete&&!b_EquipmentResultBean.masterIsAdd()){
              String del="if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+","+request.getParameter("rownum")+")";
              %>
              <input name="btnback2" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>)" value="删除(D)">
              <pc:shortcut key="x" script='<%=del%>'/>
              <%}%>
              <input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
              <pc:shortcut key="c" script='backlist();'/>
            </td>
          </tr>
        </table>
       </td>
    </tr>
  </table>
</form>
</BODY>
<script language="javascript">initDefaultTableRow('tableview1',1);</script>
<script language="javascript">
function checkpersonid(){
    var personidChangeTD=<%=personidChangeTD-1%>;
    var personid_Obj;
    for(i=0;i<personidChangeTD+1;i++){
      personid_Obj='personid_'+i;
      personid_Obj=document.all[personid_Obj].value;
      if(personid_Obj==''){alert('第'+(i+1)+'行记录<故障排除人>不能为空');return false;}
    }
    return true;
}
</script>
<%if(b_EquipmentResultBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</Html>
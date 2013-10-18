<!--设备保养记录-->
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
</script>
<%
  if(!loginBean.hasLimits("equipment_result", request, response))
    return;
  engine.erp.equipment.B_EquipmentResult b_EquipmentResultBean = engine.erp.equipment.B_EquipmentResult.getInstance(request);
  LookUp productBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT);//产品编码
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);//检验员
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//部门
  LookUp equipmentBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_EQUIPMENT);//设备
  b_EquipmentResultBean.billType="1";
%>
<BODY oncontextmenu="window.event.returnValue=true">
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
RowMap equipmentRow = equipmentBean.getLookupRow(masterRow.get("equipmentid"));
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
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0>
    <tr>
      <td align="center" height="5"></td>
    </tr>
  </table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="billType" VALUE="1">
  <INPUT TYPE="HIDDEN" NAME="maintainPlanID" VALUE='<%=masterRow.get("maintainPlanID")%>'>
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760"><tr valign="top"><td>
    <table width="150" height="17" border=0 CELLPADDING=0 CELLSPACING=0 class="table">
      <tr>
        <td width="150" class="activeVTab">设备保养记录(<%=title%>)</td>
      </tr>
    </table>
    <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="92%">
      <tr> <td>
        <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
          <tr>
            <td noWrap class="tdTitle">单据号</td>
            <td noWrap class="td"><input  type="text" name="maintainResultNO" value='<%=masterRow.get("maintainResultNO")%>' maxlength='32'  style="width:110"  class=edline onKeyDown="return getNextElement();" readonly></td>
            <TD nowrap class="tdTitle">设备名称</TD>
            <TD colspan="3" nowrap class="td">
              <INPUT <%=edClass%> <%=readonly%> style="WIDTH:70" id="equipment_code" name="equipment_code" value='<%=equipmentRow.get("equipment_code")%>' onKeyDown="return getNextElement();" onchange="equipmentCodeSelect(this,'srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name&srcVar=use_deptid&srcVar=use_dept','sumitForm(<%=b_EquipmentResultBean.PUT_EQUIPMENTID%>)')">
              <INPUT <%=edClass%> <%=readonly%> style="WIDTH:180" id="equipment_name" name="equipment_name" value='<%=equipmentBean.getLookupName(masterRow.get("equipmentid"))%>' onKeyDown="return getNextElement();" onchange="equipmentNameSelect(this,'srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name&srcVar=use_deptid&srcVar=use_dept','sumitForm(<%=b_EquipmentResultBean.PUT_EQUIPMENTID%>)')">
              <INPUT TYPE="HIDDEN" NAME="equipmentid" value="<%=masterRow.get("equipmentid")%>">
              <%if(!isEnd){%>
              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="equipmentSingleSelect('form1','srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name&srcVar=standard_gg&srcVar=use_dept&srcVar=use_deptid','fieldVar=equipmentid&fieldVar=equipment_code&fieldVar=equipment_name&fieldVar=standard_gg&fieldVar=use_dept&fieldVar=use_deptid','sumitForm(<%=b_EquipmentResultBean.PUT_EQUIPMENTID%>)','','')">
              <%}%>
              <%if(!isEnd){%>
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="equipmentid.value='';equipment_code.value='';equipment_name.value='';standard_gg.value='';use_dept.value='';use_deptid.value='';">
              <%}%>
            </TD>
            <td noWrap class="tdTitle">型号规格</td>
            <td noWrap><input type="text" name="standard_gg" value='<%=equipmentRow.get("standard_gg")%>' maxlength='20' style="width:110" class=edline readonly onKeyDown="return getNextElement();" onchange="hl_onchange();">
            </td>
          </tr>
          <tr>
            <td noWrap class="tdTitle">使用部门</td>
            <td noWrap class="td"> <input type="text" name="use_dept"  value='<%=deptBean.getLookupName(equipmentRow.get("use_deptid"))%>' maxlength='20' style="width:110" class=edline readonly onKeyDown="return getNextElement();" >
              <input type="hidden" name="use_deptid" value='<%=equipmentRow.get("use_deptid")%>' ></td></td>
          <td noWrap class="tdTitle">保养日期</td>
          <td  noWrap class="td"> <input type="text" name="maintain_date" value='<%=masterRow.get("maintain_date")%>' maxlength='10' style="width:85" <%=edClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
            <%if(!isEnd){%>
            <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.maintain_date);"></a>
            <%}%>
          </td>
          <td noWrap class="tdTitle">保养部门</td>
          <td  noWrap class="td">
            <%if(!isEnd){%>
            <pc:select name="deptid"  style="width:110" addNull='1' value = '<%=masterRow.get("deptid")%>'>
            <%=deptBean.getList()%> </pc:select>
            <%}else{%>
            <input type="text"  value='<%=deptBean.getLookupName(masterRow.get("deptid"))%>'  style="width:110"  <%=edClass%> <%=readonly%>>
            <%}%>
          </td>
          <td noWrap class="tdTitle">保养类别</td>
          <td noWrap class="td">
            <%if(!isEnd){%>
            <pc:select name='by_type' style="width:110" addNull='0' value='<%=masterRow.get("by_type")%>'>
            <pc:option value="1">周保养</pc:option> <pc:option value="2">月保养</pc:option>
            <pc:option value="3">季度保养</pc:option> <pc:option value="4">半年保养</pc:option>
            <pc:option value="5">年度保养</pc:option> </pc:select>
            <%}else{%>
            <input type="text"  value='<%
         String myby_type=masterRow.get("by_type");
            if(myby_type.equals("1"))out.print("周保养");
            if(myby_type.equals("2"))out.print("月保养");
            if(myby_type.equals("3"))out.print("季度保养");
            if(myby_type.equals("4"))out.print("半年保养");
            if(myby_type.equals("5"))out.print("年度保养");
           %>' style="width:110" <%=edClass%> <%=readonly%>>
            <%}%>
          </td>
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
            <td colspan="3" noWrap class="td"> <input type="text" name="stop_date" value='<%=newstop_date%>' maxlength='10' style="width:85" <%=edClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
              <%if(!isEnd){%>
              <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.stop_date);"></a>
              <%}%>
              <input type="text" name="stop_date_hour" value='<%=stop_date_hour%>' maxlength='20' style="width:30"  <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onchange="hl_onchange();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
              :
              <input type="text" name="stop_date_minute" value='<%=stop_date_minute%>' maxlength='20' style="width:30" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
            </td>
            <td noWrap class="tdTitle">启用日期</td>
            <td colspan="3" noWrap class="td"><input type="text" name="start_date" value='<%=newstart_date%>' maxlength='10' style="width:85" <%=edClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
              <%if(!isEnd){%>
              <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.start_date);"></a>
              <%}%>
              <input  type="text" name="start_date_hour" value='<%=start_date_hour%>' maxlength='20' style="width:30" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
              :
              <input type="text" name="start_date_minute" value='<%=start_date_minute%>' maxlength='20' style="width:30" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
            </td>
          </tr>
          <tr>
            <%
             int i=0;
             RowMap detail = null;
             int height=0;
             //if(detailRows.length>10)height=10*28;
             //else if(i>5) height=300;
             //else height=5*30;
             if(detailRows.length>10)
               height=10*28+20;
             else if(detailRows.length<5)
               height=5*32;
             else height=(detailRows.length*23)+58;
            %>
            <td colspan="8"  noWrap class="tdTitle">
              <div style="display:block;width:750;height=<%=height%>;overflow-y:auto;overflow-x:auto;">
                <table id="tableview1" width="1070" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                  <tr class="tableTitle">
                    <%if(!isEnd){%>
                    <td nowrap width="50"> <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
                      <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/>
                    </td>
                    <%}%>
                    <td nowrap width="80">计划单号</td>
                    <td nowrap width="70">元件编码</td>
                    <td nowrap with="">元件名称</td>
                    <td nowrap width="">内容</td>
                    <td  nowrap width="160">实际开始时间</td>
                    <td  nowrap width="160">实际完成时间</td>
                    <td  nowrap width="100">保养人</td>
                    <td  nowrap width="">完成情况</td>
                    <td  nowrap width="">备注</td>
                  </tr>
                  <%
                   int personidChangeTD=0;
                   for(; i<detailRows.length; i++)   {
                     detail = detailRows[i];
                     personidChangeTD=i+1;
                   RowMap productRow =productBean.getLookupRow(detail.get("cpid"));
                  %>
                  <tr>
                    <%if(!isEnd){%>
                    <td class="td"> <div align="center">
                        <input name="image232" type="image" class="img" title="选择元件" <%=detail.get("mainplandetailid").equals("")?"":"style='display:none'"%> onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=pm_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product','','')" src="../images/select_prod.gif" width="15" height="15" border="0">
                        <input name="image23" type="image" class="img" title="删除"  ONCLICK="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)"  src="../images/del.gif" width="13" height="13" border="0">
                      </div></td>
                    <%}%>
                    <td class="td">
                       <input height=23 type="text" readonly class=ednone name="maintainPlanNO_<%=i%>" style="width:100%" value='<%=detail.get("maintainPlanNO")%>' onKeyDown="return getNextElement();">
                       <INPUT TYPE="HIDDEN" NAME="mainplandetailid_<%=i%>" value='<%=detail.get("mainplandetailid")%>'>
                    </td>
                    <td class="td"> <input type="hidden" name="cpid_<%=i%>" value='<%=detail.get("cpid")%>'>
                      <input type="text" <%=detail.get("mainplandetailid").equals("")&&!isEnd?"class=edbox":"readonly class=ednone"%> id="cpbm" name="cpbm_<%=i%>"   style="width:100%" value='<%=productRow.get("cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this,<%=i%>)">
                    </td>
                    <td class="td"><input type="text" <%=detail.get("mainplandetailid").equals("")&&!isEnd?"class=edbox":"readonly class=ednone"%> name="pm_<%=i%>"   style="width:100%" value='<%=productBean.getLookupName(detail.get("cpid"))%>' onKeyDown="return getNextElement();"  onchange="productNameSelect(this,<%=i%>)"></td>
                    <td class="td"><input type="text" <%=detail.get("mainplandetailid").equals("")&&!isEnd?"class=edbox":"readonly class=ednone"%> name="content_<%=i%>" value='<%=detail.get("content")%>'  style="width:100%" onKeyDown="return getNextElement();" ></td>
                    <%
                     /*实际开始时间*/
                       String fact_startdate=detail.get("fact_startdate");
                       String fact_startdate_hour="";
                       String fact_startdate_minute="";
                       String newfact_startdate="";
                       if(fact_startdate.length()>10){
                         GregorianCalendar fact_start_calendar=new GregorianCalendar();
                         Date factstartdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fact_startdate);
                         fact_start_calendar.setTime(factstartdate);
                         fact_startdate_hour=String.valueOf(fact_start_calendar.get(Calendar.HOUR_OF_DAY));
                         if(fact_startdate_hour.length()<2)fact_startdate_hour="0"+fact_startdate_hour;
                         fact_startdate_minute=String.valueOf(fact_start_calendar.get(Calendar.MINUTE));
                         if(fact_startdate_minute.length()<2)fact_startdate_minute="0"+fact_startdate_minute;
                         newfact_startdate =fact_startdate.substring(0,10).trim();
                       }
                       else newfact_startdate=fact_startdate;
                        /*实际完成时间*/
                       String fact_finishdate=detail.get("fact_finishdate");
                       String fact_finishdate_hour="";
                       String fact_finishdate_minute="";
                       String newfact_finishdate="";
                       if(fact_finishdate.length()>10){
                         GregorianCalendar fact_finish_calendar=new GregorianCalendar();
                         Date factfinishdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fact_finishdate);
                         fact_finish_calendar.setTime(factfinishdate);
                         fact_finishdate_hour=String.valueOf(fact_finish_calendar.get(Calendar.HOUR_OF_DAY));
                         if(fact_finishdate_hour.length()<2)fact_finishdate_hour="0"+fact_finishdate_hour;
                         fact_finishdate_minute=String.valueOf(fact_finish_calendar.get(Calendar.MINUTE));
                         if(fact_finishdate_minute.length()<2)fact_finishdate_minute="0"+fact_finishdate_minute;
                         newfact_finishdate =fact_finishdate.substring(0,10).trim();
                       }
                       else newfact_finishdate=fact_finishdate;
                     %>
                    <td class="td"><input type="text" name="fact_startdate_<%=i%>" value='<%=newfact_startdate%>' maxlength='10' style="width:65" <%=noneClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
                      <%if(!isEnd){%>
                      <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.fact_startdate_<%=i%>);"></a>
                      <%}%>
                      <input type="text" name="fact_startdate_hour_<%=i%>" value='<%=fact_startdate_hour%>' maxlength='20' style="width:20" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                      :
                      <input type="text" name="fact_startdate_minute_<%=i%>" value='<%=fact_startdate_minute%>' maxlength='20' style="width:20" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                    </td>
                    <td class="td"><input type="text" name="fact_finishdate_<%=i%>" value='<%=newfact_finishdate%>' maxlength='10' style="width:65" <%=noneClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
                      <%if(!isEnd){%>
                      <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.fact_finishdate_<%=i%>);"></a>
                      <%}%>
                      <input type="text" name="fact_finishdate_hour_<%=i%>" value='<%=fact_finishdate_hour%>' maxlength='20' style="width:20" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                      :
                      <input type="text" name="fact_finishdate_minute_<%=i%>" value='<%=fact_finishdate_minute%>' maxlength='20' style="width:20" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                    </td>
                    <td class="td">
                      <%if(!isEnd){%>
                      <pc:select name='<%="personid_"+i%>' addNull="1" style="width:100" value='<%=detail.get("personid")%>'>
                      <%=personBean.getList()%> </pc:select>
                      <%}else{%>
                      <input type="text"  value='<%=personBean.getLookupName(detail.get("personid"))%>'  style="width:110"  <%=noneClass%> <%=readonly%>>
                      <%}%>
                    </td>
                    <td class="td"><input type="text" name="finish_circs_<%=i%>" value='<%=detail.get("finish_circs")%>' maxlength='20' style="width:100%" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" ></td>
                    <td class="td"><input type="text" name="memo_<%=i%>" value='<%=detail.get("memo")%>' maxlength='20' style="width:100%" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" ></td>
                  </tr>
                  <%
                    list.next();
                     }
                    for(; i <5; i++){
                  %>
                  <tr>
                    <%if(!isEnd){%>
                    <td class="td">&nbsp;</td>
                    <%}%>
                    <td class="td" height=22>&nbsp;</td>
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
              </div></td>
          </tr>
          <tr>
            <td  noWrap class="tdTitle">结论</td>
            <td colspan="7" noWrap class="td"><textarea name="verdict" rows="4" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" style="width:690;height: 65"><%=masterRow.get("verdict")%></textarea></td>
          </tr>
        </table></td></tr>
    </table></td></tr>
    <tr>
      <td> <table CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("createDate")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("creator")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
              <%if(!isEnd&&isCanAdd){%>
              <input name="button22"  style="width:80" type="button" class="button" onClick="maintainplanSelect();" value="引保养计划">
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
              <pc:shortcut key="c" script='backlist();'/> </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
</BODY>
<script language="javascript">initDefaultTableRow('tableview1',1);</script>
<script language="javascript">
function maintainplanSingleSelect(frmName, srcVar,fieldVar,methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=750 height=570 top=0 left=0";
  var winName= "GoodsProdSelector";
  paraStr = "maintain_plan_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar;
  if(methodName+'' != 'undefined')
   paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
   paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
function maintainplanSelect(){
  maintainplanSingleSelect('form1',
                           'srcVar=maintainPlanID&srcVar=equipmentid',
                           'fieldVar=maintainPlanID&fieldVar=equipmentid',
                           'sumitForm(<%=b_EquipmentResultBean.EQUIPMENT_CHANGE%>)');
}
function checkpersonid(){
    var personidChangeTD=<%=personidChangeTD-1%>;
    var personid_Obj;
    for(i=0;i<personidChangeTD+1;i++){
      personid_Obj='personid_'+i;
      personid_Obj=document.all[personid_Obj].value;
      if(personid_Obj==''){alert('第'+(i+1)+'行记录<保养人>不能为空');return false;}
    }
    return true;
}
</script>
<%if(b_EquipmentResultBean.isApprove){%>
<jsp:include page="../pub/approve.jsp" flush="true"/>
<%}%>
<%out.print(retu);%>
</Html>

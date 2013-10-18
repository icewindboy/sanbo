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
  String pageCode  = "maintain_plan";
%>
<%
  if(!loginBean.hasLimits("maintain_plan", request, response))
    return;
  engine.erp.equipment.B_MaintainPlan b_MaintainPlanBean = engine.erp.equipment.B_MaintainPlan.getInstance(request);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);//检验员
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//部门
  LookUp productBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_PRODUCT);//产品/元件
  LookUp equipmentBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_EQUIPMENT);//设备
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
function maintainSingleSelect(frmName, srcVar,fieldVar,methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=930 height=570 top=0 left=0";
  var winName= "GoodsProdSelector";
  paraStr = "maintain_requisition_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='maintain_plan.jsp';
}

function plandeptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
function clear(){
  form1.maintainapplyNO.value='';
  form1.apply_deptid.value='';
  form1.proposerid.value='';
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
</script>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<%
String retu = b_MaintainPlanBean.doService(request, response);
if(retu.indexOf("backList();")>-1)
{
out.print(retu);
return;
}
String curUrl = request.getRequestURL().toString();
EngineDataSet ds = b_MaintainPlanBean.getMasterTable();
EngineDataSet list = b_MaintainPlanBean.getDetailTable();

RowMap masterRow = b_MaintainPlanBean.getMasterRowinfo();
RowMap[] detailRows= b_MaintainPlanBean.getDetailRowinfos();
personBean.regData(ds,"proposerid");
personBean.regData(ds,"personid");
deptBean.regData(ds,"deptid");
deptBean.regData(ds,"apply_deptid");
productBean.regData(list,"cpid");
equipmentBean.regData(ds,"equipmentid");
RowMap equipmentRow = equipmentBean.getLookupRow(masterRow.get("equipmentid"));
boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);
String stateval =masterRow.get("state");
boolean isdel=stateval.equals("0");
boolean isEnd=b_MaintainPlanBean.isApprove||(!b_MaintainPlanBean.masterIsAdd()&&!stateval.equals("0"));
isEnd=isEnd||!(b_MaintainPlanBean.masterIsAdd()?isCanAdd:isCanEdit);
String edClass = isEnd ? "class=edline" : "class=edbox";
String noneClass= isEnd ? "class=ednone" : "class=edbox";
String readonly = isEnd ? " readonly" : "";
String title= stateval.equals("1") ? "已审批":(stateval.equals("9") ? "审批中" : (stateval.equals("0")?"未审批":"未审批" ));
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="maintainapplyID" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="750">
    <tr valign="top">
      <td><table width="150" border=0 CELLPADDING=0 CELLSPACING=0 class="table">
          <tr>
            <td width="150" class="activeVTab">设备保养计划(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
    <tr>
      <td noWrap class="tdTitle">单据号</td>
      <td noWrap class="td"><input type="text" name="maintainPlanNO" value='<%=masterRow.get("maintainPlanNO")%>' maxlength='32' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
      </td>
      <td noWrap class="tdTitle">申请单号</td>
      <td  noWrap class="td"><input type="text" name="maintainapplyNO" value='<%=masterRow.get("maintainapplyNO")%>' maxlength='32' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
      <td noWrap class="tdTitle">设备编码</td>
      <td colspan="3" noWrap class="td">
    <INPUT <%=edClass%> <%=readonly%> style="WIDTH:70" id="equipment_code" name="equipment_code" value='<%=equipmentRow.get("equipment_code")%>' onKeyDown="return getNextElement();" onchange="equipmentCodeSelect(this,'srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name','sumitForm(<%=b_MaintainPlanBean.PUT_EQUIPMENTID%>)')">
     <INPUT <%=edClass%> <%=readonly%> style="WIDTH:180" id="equipment_name" name="equipment_name" value='<%=equipmentBean.getLookupName(masterRow.get("equipmentID"))%>' onKeyDown="return getNextElement();" onchange="equipmentNameSelect(this,'srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name','sumitForm(<%=b_MaintainPlanBean.PUT_EQUIPMENTID%>)')">
     <INPUT TYPE="HIDDEN" NAME="equipmentid" value="<%=masterRow.get("equipmentID")%>">
     <%if(!isEnd){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="equipmentSingleSelect('form1','srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name&srcVar=standard_gg&srcVar=manufacturer&srcVar=buy_date&srcVar=use_date','fieldVar=equipmentid&fieldVar=equipment_code&fieldVar=equipment_name&fieldVar=standard_gg&fieldVar=manufacturer&fieldVar=buy_date&fieldVar=use_date','clear();','','')">
     <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="equipmentid.value='';equipment_code.value='';equipment_name.value='';standard_gg.value='';manufacturer.value='';buy_date.value='';use_date.value='';apply_dept.value='';proposer.value='';maintainapplyNO.value='';">
      <%}%></td>
         </tr>
    <tr>
       <td noWrap class="tdTitle">型号规格</td>
      <td  noWrap class="td"><input type="text" name="standard_gg" value='<%=equipmentRow.get("standard_gg")%>'  name="Input2" maxlength='32' style="width:110" class="edline" readonly onKeyDown="return getNextElement();"></td>
      <td noWrap class="tdTitle">制造厂</td>
      <td colspan="3" noWrap class="td"><input type="text" name="manufacturer" value='<%=equipmentRow.get("manufacturer")%>' maxlength='32' style="width:260" class="edline" readonly></td>

    </tr>
    <tr>
     <td noWrap class="tdTitle">购买日期</td>
      <td  noWrap class="td"><input type="text" name="buy_date" value='<%=equipmentRow.get("buy_date")%>' maxlength='32' style="width:85" class="edline" readonly onKeyDown="return getNextElement();">
      </td>
      <td noWrap class="tdTitle">开始使用日期</td>
      <td  noWrap class="td"><input type="text" name="use_date" value='<%=equipmentRow.get("use_date")%>' maxlength='32' style="width:85" class="edline" readonly  onKeyDown="return getNextElement();">
      </td>
      <td noWrap class="tdTitle">申请部门</td>
      <td noWrap class="td">
      <input type="text" name="apply_dept" value='<%=deptBean.getLookupName(masterRow.get("apply_deptid"))%>'  style="width:110"  class="edline"  readonly>
      <input type="text" name="apply_deptid" value='<%=masterRow.get("apply_deptid")%>' style="display:none" ></td>
      <td noWrap class="tdTitle">申请人</td>
      <td  noWrap class="td">
      <input type="text" name="proposer" value='<%=personBean.getLookupName(masterRow.get("proposerid"))%>'  style="width:110" class="edline"  readonly>
      <input type="text" name="proposerid" value='<%=masterRow.get("proposerid")%>' style="display:none">
      </td>
    </tr>
    <tr>
      <td noWrap class="tdTitle">计划部门</td>
      <td noWrap class="td"><%if(!isEnd){%>
                  <pc:select name="deptid"  style="width:110"  addNull='1' value='<%=masterRow.get("deptid")%>' onSelect="plandeptchange();" >
                  <%=deptBean.getList()%>
                  </pc:select>
                  <%}else{%>
                  <input type="text"  value='<%=deptBean.getLookupName(masterRow.get("deptid"))%>'  style="width:110"  <%=edClass%> <%=readonly%>>
                  <%}%> </td>
      <td noWrap class="tdTitle">计划人</td>
      <td  noWrap class="td"> <%if(!isEnd){%>
                  <pc:select name="personid"  style="width:110"  addNull='1' value='<%=masterRow.get("personid")%>'>
                  <%=personBean.getList()%>
                  </pc:select>
                  <%}else{%>
                  <input type="text"  value='<%=personBean.getLookupName(masterRow.get("personid"))%>'  style="width:110"  <%=edClass%> <%=readonly%>>
                  <%}%></td>
      <td noWrap class="tdTitle">计划日期</td>
      <td  noWrap class="td"><input type="text" name="plan_date" value='<%=masterRow.get("plan_date")%>' maxlength='32' style="width:85" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();">
     <%if(!isEnd){%><A href="#"><IMG style="cursor:hand" title="选择日期" onClick="selectDate(plan_date);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
      <%}%></td>
      <td noWrap class="tdTitle">&nbsp;</td>
      <td noWrap class="td">&nbsp;</td>
    </tr>
    <tr>
      <td colspan="8" noWrap class="td"><div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
       <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
      <tr class="tableTitle">
        <%if(!isEnd){%><td nowrap width="50"><div align="center">
       <input name="image22" type="image" class="img" title="添加(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add.gif"  border="0">
       <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/>
       </div></td><%}%>
        <td nowrap  width='60'>元件编码</td>
        <td nowrap  width='250'>元件名称</td>
        <td nowrap  width='100'>保养内容</td>
        <td nowrap  width='180'>计划开始时间</td>
        <td nowrap  width='180'>计划完成时间</td>
      </tr>
      <%
          int i=0;
          int cpidChangeTD=0;
          RowMap detail = null;
          for(; i<detailRows.length; i++)   {
          detail = detailRows[i];
          cpidChangeTD=i+1;
          RowMap productRow =productBean.getLookupRow(detail.get("cpid"));
      %>
        <tr id="rowinfo_<%=i%>">
         <%if(!isEnd){%><td class="td"><div align="center">
         <input name="image2" type="image" class="img" title="选择元件" onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=pm_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product','','')" src="../images/select_prod.gif" width="15" height="15" border="0">
        <input name="image23" type="image" class="img" title="删除" ONCLICK="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)"  src="../images/del.gif" width="13" height="13" border="0">
          </div></td><%}%>
       <input type="hidden" name="cpid_<%=i%>" value='<%=detail.get("cpid")%>'>
        <td class="td"><INPUT <%=noneClass%> <%=readonly%> style="width:60" name="cpbm_<%=i%>" value='<%=productRow.get("cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this,<%=i%>)"></td>
        <td class="td"><INPUT <%=noneClass%> <%=readonly%> style="width:250" name="pm_<%=i%>" value='<%=productBean.getLookupName(detail.get("cpid"))%>' onKeyDown="return getNextElement();" onchange="productNameSelect(this,<%=i%>)">
        </td>
        <td class="td"><input type="text" name="maintain_content_<%=i%>" value='<%=detail.get("maintain_content")%>'  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();"></td>
              <%
        /*计划开始时间*/
          String plan_startdate=detail.get("plan_startdate");
          String plan_startdate_hour="";
          String plan_startdate_minute="";
          String newplan_startdate="";
          if(plan_startdate.length()>10){
            GregorianCalendar plan_start_calendar=new GregorianCalendar();
            Date planstartdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(plan_startdate);
            plan_start_calendar.setTime(planstartdate);
            plan_startdate_hour=String.valueOf(plan_start_calendar.get(Calendar.HOUR_OF_DAY));
            if(plan_startdate_hour.length()<2)plan_startdate_hour="0"+plan_startdate_hour;
            plan_startdate_minute=String.valueOf(plan_start_calendar.get(Calendar.MINUTE));
            if(plan_startdate_minute.length()<2)plan_startdate_minute="0"+plan_startdate_minute;
            newplan_startdate =plan_startdate.substring(0,10).trim();
          }
          else newplan_startdate=plan_startdate;
          /*计划完成时间*/
         String plan_finishdate=detail.get("plan_finishdate");
         String plan_finishdate_hour="";
         String plan_finishdate_minute="";
         String newplan_finishdate="";
         if(plan_finishdate.length()>10){
           GregorianCalendar plan_finish_calendar=new GregorianCalendar();
           Date planfinishdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(plan_finishdate);
           plan_finish_calendar.setTime(planfinishdate);
           plan_finishdate_hour=String.valueOf(plan_finish_calendar.get(Calendar.HOUR_OF_DAY));
           if(plan_finishdate_hour.length()<2)plan_finishdate_hour="0"+plan_finishdate_hour;
           plan_finishdate_minute=String.valueOf(plan_finish_calendar.get(Calendar.MINUTE));
           if(plan_finishdate_minute.length()<2)plan_finishdate_minute="0"+plan_finishdate_minute;
           newplan_finishdate =plan_finishdate.substring(0,10).trim();
         }
          else newplan_finishdate=plan_finishdate;
        %>
       <td class="td"><input type="text" style="width:65" name="plan_startdate_<%=i%>" value='<%=newplan_startdate%>'  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onChange="checkDate(this)">
       <%if(!isEnd){%><A href="#"><IMG style="cursor:hand" title=选择日期 onClick="selectDate(plan_startdate_<%=i%>);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
       <%}%>
        <input type="text" name="plan_startdate_hour_<%=i%>" value='<%=plan_startdate_hour%>' maxlength='20' style="width:30" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
          :
        <input type="text" name="plan_startdate_minute_<%=i%>" value='<%=plan_startdate_minute%>' maxlength='20' style="width:30" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
       </td>
        <td class="td"><input type="text" style="width:65" name="plan_finishdate_<%=i%>" value='<%=newplan_finishdate%>'  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onChange="checkDate(this)">
        <%if(!isEnd){%><A href="#"><IMG style="cursor:hand" title=选择日期 onClick="selectDate(plan_finishdate_<%=i%>);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
       <%}%>
        <input type="text" name="plan_finishdate_hour_<%=i%>" value='<%=plan_finishdate_hour%>' maxlength='20' style="width:30" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>23)this.value=this.value%24;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
          :
        <input type="text" name="plan_finishdate_minute_<%=i%>" value='<%=plan_finishdate_minute%>' maxlength='20' style="width:30" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'');if(this.value>59)this.value=this.value%60;" onafterpaste="this.value=this.value.replace(/\D/g,'')">
       </td>
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
      </tr>
      <%}%>
       </table>
     </div>
     <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
       <tr>
      <td width="46" class="td"> <p align="right"><strong>备注:</strong></td>
      <td class="td"><textarea name="memo" rows="3" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" style="width: 700;height:65" cols="20"><%=masterRow.get("memo")%></textarea>
      </td>
       </tr>
     </table></td>
    </tr>
     </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="750" align="center">
          <tr>
            <td class="td"><b>制单日期:</b><%=masterRow.get("createDate")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("creator")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
             <%if(!isEnd&&isCanAdd){%>
              <input name="btnback3" type="button" class="button" onClick="maintainSingleSelect('form1','srcVar=maintainapplyID&srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name&srcVar=standard_gg&srcVar=manufacturer&srcVar=buy_date&srcVar=use_date&srcVar=maintainapplyNO&srcVar=apply_dept&srcVar=proposer','fieldVar=maintainapplyID&fieldVar=equipmentid&fieldVar=equipment_code&fieldVar=equipment_name&fieldVar=standard_gg&fieldVar=manufacturer&fieldVar=buy_date&fieldVar=use_date&fieldVar=maintainapplyNO&fieldVar=apply_dept&fieldVar=proposer','sumitForm(<%=b_MaintainPlanBean.EQUIPMENT_CHANGE%>)','');"
              value="引入保养申请单">
              <input name="button2" type="button" class="button" onClick="if(!checkcpid()){return};sumitForm(<%=Operate.POST_CONTINUE%>);"  value="保存添加(N)">
              <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <%}if(!isEnd&&isCanAdd){%>
              <input name="btnback" type="button" class="button" onClick="if(!checkcpid()){return};sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
               <%}if(isdel&&isCanDelete&&!b_MaintainPlanBean.masterIsAdd()){
              String del="if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+","+request.getParameter("rownum")+")";%>
              <input name="btnback2" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>)" value="删除(D)">
              <pc:shortcut key="x" script='<%=del%>'/>
              <%}%>
              <input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
              <pc:shortcut key="c" script='backlist();'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
</script>
<script language="javascript">
function checkcpid(){
    var cpidChangeTD=<%=cpidChangeTD-1%>;
    var cpid_Obj;
    for(i=0;i<cpidChangeTD+1;i++){
      cpid_Obj='cpid_'+i;
      cpid_Obj=document.all[cpid_Obj].value;
      if(cpid_Obj==''){alert('第'+(i+1)+'行记录<元件编码、元件名称>不能为空');return false;}
    }
    return true;
}
</script>
<%if(b_MaintainPlanBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
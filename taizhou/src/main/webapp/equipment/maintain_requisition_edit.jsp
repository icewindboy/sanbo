<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "maintain_requisition";
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
  function ApplyMultiSelect(frmName, srcVar, methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
  var winName= "GoodsProdSelector";
  paraStr = "../buy/buy_plan_select.jsp";
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
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function backList()
{
  location.href="maintain_requisition.jsp";
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
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid',  eval('form1.deptid.value'), '');
}
function showtitle(){
  if(form1.bill_sort.value=='0')detail.innerText='维修内容';
  if(form1.bill_sort.value=='1')detail.innerText='保养内容';
  if(form1.bill_sort.value=='')detail.innerText='内容';
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
</script>
<%
  if(!loginBean.hasLimits("maintain_requisition", request, response))
    return;
  engine.erp.equipment.B_MaintainRequisition b_MaintainRequisitionBean = engine.erp.equipment.B_MaintainRequisition.getInstance(request);
  LookUp productBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT);//产品编码
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);//检验员
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//部门
  LookUp equipmentBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_EQUIPMENT);//设备
  LookUp exceptionReasonBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_EQUIPMENT_EXCEPTIONREASON);//故障原因
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<%
String retu = b_MaintainRequisitionBean.doService(request, response);
if(retu.indexOf("backList();")>-1)
{
out.print(retu);
return;
}
String curUrl = request.getRequestURL().toString();
EngineDataSet ds = b_MaintainRequisitionBean.getMasterTable();
EngineDataSet list = b_MaintainRequisitionBean.getDetailTable();
RowMap masterRow = b_MaintainRequisitionBean.getMasterRowinfo();
RowMap[] detailRows= b_MaintainRequisitionBean.getDetailRowinfos();
productBean.regData(list,"cpid");
deptBean.regData(ds,"deptid");
equipmentBean.regData(ds,"equipmentid");
exceptionReasonBean.regData(list,"excepReasonID");
RowMap equipmentRow = equipmentBean.getLookupRow(masterRow.get("equipmentid"));
boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);
String stateval =masterRow.get("state");
boolean isdel=stateval.equals("0");
boolean isEnd=b_MaintainRequisitionBean.isApprove||(!b_MaintainRequisitionBean.masterIsAdd()&&!stateval.equals("0"));
isEnd=isEnd||!(b_MaintainRequisitionBean.masterIsAdd()?isCanAdd:isCanEdit);
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
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table width="200" border=0 CELLPADDING=0 CELLSPACING=0 class="table">
          <tr>
            <td class="activeVTab" id='lx'>设备维修、保养申请(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
    <tr>
      <td noWrap class="tdTitle">单据号</td>
      <td noWrap class="td"><input type="text" name="maintainapplyNO" value='<%=masterRow.get("maintainapplyNO")%>' maxlength='32' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
      </td>
      <td noWrap class="tdTitle">设备名称</td>
      <td colspan="3" noWrap class="td">
     <INPUT style="WIDTH:70" id="equipment_code" name="equipment_code" value='<%=equipmentRow.get("equipment_code")%>' <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onchange="equipmentCodeSelect(this,'srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name','sumitForm(<%=b_MaintainRequisitionBean.PUT_EQUIPMENTID%>)')">
     <INPUT style="WIDTH:180" id="equipment_name" name="equipment_name" value='<%=equipmentBean.getLookupName(masterRow.get("equipmentID"))%>' <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onchange="equipmentNameSelect(this,'srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name','sumitForm(<%=b_MaintainRequisitionBean.PUT_EQUIPMENTID%>)')">
     <INPUT TYPE="HIDDEN" NAME="equipmentid" value="<%=masterRow.get("equipmentid")%>">
      <%if(!isEnd){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="equipmentSingleSelect('form1','srcVar=equipmentid&srcVar=equipment_code&srcVar=equipment_name&srcVar=standard_gg&srcVar=manufacturer&srcVar=buy_date&srcVar=use_date','fieldVar=equipmentid&fieldVar=equipment_code&fieldVar=equipment_name&fieldVar=standard_gg&fieldVar=manufacturer&fieldVar=buy_date&fieldVar=use_date','sumitForm(<%=b_MaintainRequisitionBean.PUT_EQUIPMENTID%>)','','')">
      <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="equipmentid.value='';equipment_code.value='';equipment_name.value='';standard_gg.value='';manufacturer.value='';buy_date.value='';use_date.value='';">
      <%}%></td>
      <td noWrap class="tdTitle">型号规格</td>
      <td  noWrap class="td"><input type="text" name="standard_gg" value='<%=equipmentRow.get("standard_gg")%>' maxlength='32' style="width:110" class="edline" readonly onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td noWrap class="tdTitle">制造厂</td>
      <td colspan="3" noWrap class="td">
       <input type="text" name="manufacturer" value='<%=equipmentRow.get("manufacturer")%>' maxlength='32' style="width:260" class="edline" readonly></td>
      <td noWrap class="tdTitle">购买日期</td>
      <td  noWrap class="td"><input type="text" name="buy_date" value='<%=equipmentRow.get("buy_date")%>' maxlength='32' style="width:85" class="edline" readonly onKeyDown="return getNextElement();">
      </td>
      <td noWrap class="tdTitle">开始使用日期</td>
      <td  noWrap class="td"><input type="text" name="use_date" value='<%=equipmentRow.get("use_date")%>' maxlength='32' style="width:85" class="edline" readonly onKeyDown="return getNextElement();">
      </td>
    </tr>
    <tr>
      <td noWrap class="tdTitle">申请部门</td>
      <td noWrap class="td"><%if(!isEnd){%>
        <pc:select name="deptid"  style="width:110"  addNull='1' value='<%=masterRow.get("deptid")%>' onSelect="deptchange();">
        <%=deptBean.getList()%>
        </pc:select>
        <%}else{%>
        <input type="text"  value='<%=deptBean.getLookupName(masterRow.get("deptid"))%>'  style="width:110"  <%=edClass%> <%=readonly%>>
        <%}%>
      </td>
      <td noWrap class="tdTitle">申请人</td>
      <td  noWrap class="td">
<%if(!isEnd){%>
<pc:select name="personid" addNull="1" style="width:110" value='<%=masterRow.get("personid")%>'>
<%=personBean.getList()%> </pc:select>
<%}else{%>
<input type="text" value='<%=personBean.getLookupName(masterRow.get("personid"))%>'  style="width:130"  <%=edClass%> <%=readonly%>>
<%}%>
</td>
      <td noWrap class="tdTitle">申请日期</td>
      <td  noWrap class="td">
     <input type="text" name="applydate" value='<%=masterRow.get("applydate")%>' maxlength='10' style="width:70" <%=edClass%> <%=readonly%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
     <%if(!isEnd){%> <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.applydate);"></a>
     <%}%> </td>
      <td noWrap class="tdTitle">单据类型</td>
      <td  noWrap class="td">
               <%if(!isEnd){%>
                 <pc:select name='bill_sort' addNull='1' style="width:110" value='<%=masterRow.get("bill_sort")%>' onSelect="showtitle();">
                 <pc:option value="0">维修申请</pc:option>
                 <pc:option value="1">保养申请</pc:option>
                 </pc:select>
                 <%}else{%>
                  <input type="text"  value='<%
                 String mybillsort=masterRow.get("bill_sort");
                if(mybillsort.equals("0"))out.print("维修申请");
                if(mybillsort.equals("1"))out.print("保养申请");
                 %>'  style="width:130" <%=edClass%> <%=readonly%>>
                  <%}%> <td>
      </tr>
       </table>
       <div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
       <tr class="tableTitle">
       <%if(!isEnd){%><td nowrap width="50">
       <input name="image22" type="image" class="img" title="添加(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add.gif"  border="0">
       <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/>
       </td><%}%>
      <td  nowrap width="60">元件编码</td>
      <td  nowrap width="240">元件名称</td>
      <td  nowrap id="detail">内容</td>
      <td  nowrap>备注</td>
      <td  nowrap width="110">故障原因</td>
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
     <td class="td"><INPUT <%=noneClass%> <%=readonly%> style="WIDTH:60"  name="cpbm_<%=i%>" value='<%=productRow.get("cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this,<%=i%>)"></td>
     <td class="td"><INPUT <%=noneClass%> <%=readonly%> style="WIDTH:240"  name="pm_<%=i%>" value='<%=productBean.getLookupName(detail.get("cpid"))%>' onKeyDown="return getNextElement();" onchange="productNameSelect(this,<%=i%>)">
    </td>
      <td class="td"><input type="text" name="content_<%=i%>" value='<%=detail.get("content")%>' maxlength='20' style="width:100%" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();"  maxlength='<%=list.getColumn("content").getPrecision()%>'></td>
      <td class="td"><input type="text" name="memo_<%=i%>" value='<%=detail.get("memo")%>' maxlength='20' style="width:100%" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" ></td>
      <td class="td">
      <%if(!isEnd){%>
      <pc:select name='<%="excepReasonID_"+i%>' addNull="1" style="width:110"  value='<%=detail.get("excepReasonID")%>'>
      <%=exceptionReasonBean.getList()%> </pc:select>
      <%}else{%>
      <input type="text" value='<%=exceptionReasonBean.getLookupName(detail.get("excepReasonID"))%>'  style="width:130"  <%=noneClass%> <%=readonly%>>
      <%}%>
      </td>
      </tr>
        <%
        list.next();
        }
        for(; i <5; i++){
        %>
       <tr id="rowinfo_<%=i%>">
      <%if(!isEnd){%> <td class="td">&nbsp;</td><%}%>
       <td class="td">&nbsp;</td>
       <td class="td">&nbsp;</td>
       <td class="td">&nbsp;</td>
       <td class="td">&nbsp;</td>
       <td class="td">&nbsp;</td>
       </tr>
          <%}%>
     </table></div>
     <table cellspacing=0 cellpadding=0 width="760" align="center">
       <tr>
      <td width="86"  class="td"><div align="center"><strong>原因</strong></div></td>
      <td width="672"  class="td"><textarea name="buy_cause" <%=edClass%> <%=readonly%> rows="2" onKeyDown="return getNextElement();" style="width:650; height: 65" cols="20"><%=masterRow.get("buy_cause")%></textarea></td>
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
        <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
          <tr>
            <td class="td"><b>制单日期:</b><%=masterRow.get("createDate")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("creator")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
               <%if(!isEnd&&isCanAdd){%>
              <input name="button2" type="button" class="button" onClick="if(!checkcpid()){return};if(!checkexcepReasonID()){return};sumitForm(<%=Operate.POST_CONTINUE%>);"  value="保存添加(N)">
              <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <%}if(!isEnd&&isCanAdd){
               %>
              <input name="btnback" type="button" class="button" onClick="if(!checkcpid()){return};if(!checkexcepReasonID()){return};sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
               <%}if(isdel&&isCanDelete&&!b_MaintainRequisitionBean.masterIsAdd()){
              String del="if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+","+request.getParameter("rownum")+")";
              %>
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
<script language="javascript">initDefaultTableRow('tableview1',1);</script>
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
function checkexcepReasonID(){
    var cpidChangeTD=<%=cpidChangeTD-1%>;
    var excepReasonID_Obj;
    for(i=0;i<cpidChangeTD+1;i++){
      excepReasonID_Obj='excepReasonID_'+i;
      excepReasonID_Obj=document.all[excepReasonID_Obj].value;
      if(excepReasonID_Obj==''){alert('第'+(i+1)+'行记录<故障原因>不能为空');return false;}
    }
    return true;
}
</script>
<%if(b_MaintainRequisitionBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</Html>
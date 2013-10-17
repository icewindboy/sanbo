<!--设备维修、保养记录-->
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*,engine.project.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "equipment_result";
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
function toDetail(billType){
  if(billType=='0')location.href='servicing_result_edit.jsp';
  if(billType=='1')location.href='maintain_result_edit.jsp';
}
function sumitForm(oper,row,billType)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.billType.value = billType;
  form1.submit();
}
function showFixedQuery(){
  //点击查询按钮打开查询对话框
   showFrame('fixedQuery', true, "", true);//显示层
}
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
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
engine.erp.equipment.B_EquipmentResult b_EquipmentResultBean = engine.erp.equipment.B_EquipmentResult.getInstance(request);
LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);//人员
LookUp deptBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_DEPT);//部门
LookUp equipmentBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_EQUIPMENT);//设备
if(!loginBean.hasLimits("equipment_result", request, response))
  return;
String retu = b_EquipmentResultBean.doService(request, response);
if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
{
  out.print(retu);
  return;
}
%>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD height="17" align="center" NOWRAP>设备维修、保养记录</TD>
  </TR></TABLE>
<%
EngineDataSet list = b_EquipmentResultBean.getMasterTable();//主表数据集
String curUrl = request.getRequestURL().toString();
RowMap m_RowInfo =b_EquipmentResultBean.getMasterRowinfo();

boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);
boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
String SYS_APPROVE_ONLY_SELF =b_EquipmentResultBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
boolean isApproveOnly = SYS_APPROVE_ONLY_SELF.equals("1") ? true : false;//true仅制单人可提交
personBean.regData(list,"approverid");
deptBean.regData(list,"deptid");
equipmentBean.regData(list,"equipmentid");
String loginID = b_EquipmentResultBean.loginID;
String state=null;
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="billType" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
    <td class="td" nowrap>
    <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
      int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
         </td>
</td>
<td class="td" nowrap align="right">
<input name="search22" type="button" class="button"  style=""  onClick="sumitForm(<%=Operate.ADD%>,'','0')" value="新增维修记录" onKeyDown="return getNextElement();">
<input name="search222" type="button" class="button"  style=""  onClick="sumitForm(<%=Operate.ADD%>,'','1')" value="新增保养记录" onKeyDown="return getNextElement();">
<input name="search" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)">
  <pc:shortcut key="q" script="showFixedQuery()"/>
<%if(b_EquipmentResultBean.retuUrl!=null){%><input name="button2" type="button" align="Right"
class="button" onClick="location.href='<%=b_EquipmentResultBean.retuUrl%>'" value=" 返回 " border="0"><%}%>
</td>
</tr>
</table>
 <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td  align="center">&nbsp;</td>
      <td nowrap >单据号</td>
      <td nowrap>单据类型</td>
      <td nowrap>维修申请单号</td>
      <td nowrap>设备编码</td>
      <td nowrap>设备名称</td>
      <td nowrap>型号规格</td>
      <td nowrap>日期</td>
      <td nowrap>使用部门</td>
      <td nowrap>停用日期</td>
      <td nowrap>启用日期</td>
      <td nowrap>状态</td>
      <td nowrap>审批人</td>
      <td nowrap>描述状态</td>
      <td nowrap>制单日期</td>
      <td nowrap>制单人</td>
    </tr>
     <%
       list.first();
      int i=0;
      for(;i<list.getRowCount();i++)
      {
        boolean isInit = false;
        String stateval =list.getValue("state");
        if(stateval.equals("0"))isInit = true;
        String creatorID = list.getValue("creatorID");//制单人ID
        String approverID = list.getValue("approverID");//审批人ID
        boolean isShow = isApproveOnly ? (loginID.equals(creatorID) && isInit) : isInit;//如果时只有制单人才可以提交审批。制单人等于登录人才显示
        boolean isCancelApprove =  stateval.equals("1");
        isCancelApprove = isCancelApprove && loginID.equals(approverID);//是否可以取消合同
        String rowClass = engine.action.BaseAction.getStyleName(stateval);
        if(stateval.equals("")) stateval="未审";
        if(stateval.equals("0")) stateval="未审";
        if(stateval.equals("1")) stateval="已审";
        if(stateval.equals("2")) stateval="已入库";
        if(stateval.equals("9")) stateval="审批中";
        RowMap equipmentRow = equipmentBean.getLookupRow(list.getValue("equipmentid"));
        String isEdit=loginBean.hasLimits(pageCode, op_edit)&&stateval.equals("0")?"修改":"查看";
       %>
<tr id="tr_1" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>,<%=list.getValue("billType")%>)">
<td nowrap class=td><div align="center">
<input name="image1" class="img" type="image" title="<%=isEdit%>" onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>,<%=list.getValue("billType")%>)" src="../images/edit.gif" border="0">
<%if(isShow){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>,<%=list.getValue("billType")%>)" src="../images/approve.gif" border="0"><%}%>
<%if(isCancelApprove){%><input name="image3" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=b_EquipmentResultBean.CANCLE_APPROVE%>,<%=list.getRow()%>,<%=list.getValue("billType")%>)" src="../images/clear.gif" border="0"><%}%>
</div></td>
      <td nowrap <%=rowClass%>><%=list.getValue("maintainResultNO")%></td><!--单据号-->
      <td nowrap <%=rowClass%>><%String mybilltype=list.getValue("billType");
      if(mybilltype.equals("0"))out.print("维修记录");
      if(mybilltype.equals("1"))out.print("保养记录");
      %></td><!--单据类型-->
      <td nowrap <%=rowClass%>><%=list.getValue("maintainapplyNO")%></td><!--维修申请单号-->
      <td nowrap <%=rowClass%>><%=equipmentRow.get("equipment_code")%></td><!--设备编码-->
      <td nowrap <%=rowClass%>><%=equipmentRow.get("equipment_name")%></td><!--设备名称-->
      <td nowrap <%=rowClass%>><%=equipmentRow.get("standard_gg")%></td><!--型号规格-->
      <td nowrap <%=rowClass%>><%=list.getValue("maintain_date")%></td><!--日期-->
      <td nowrap <%=rowClass%>><%=deptBean.getLookupName(list.getValue("deptid"))%></td><!--使用部门-->
      <td nowrap <%=rowClass%>><%=list.getValue("stop_date")%></td><!--停用日期-->
      <td nowrap <%=rowClass%>><%=list.getValue("start_date")%></td><!--启用日期-->
      <td nowrap <%=rowClass%>>
      <%String mybilltype2=list.getValue("billType");
       String sp_xm=null;
       if(mybilltype2.equals("0"))sp_xm="servicing_result";
       if(mybilltype2.equals("1"))sp_xm="maintain_result";
      %>
      <a href='javascript:'onClick="openUrlOpt1('../pub/approve_result.jsp?operate=0&project=<%=sp_xm%>&id=<%=list.getValue("maintainresultid")%>')">
      <%=stateval%></a></td><!--状态-->
      <td nowrap <%=rowClass%>><%=personBean.getLookupName(list.getValue("approverID"))%></td><!--审批人-->
      <td nowrap <%=rowClass%>><%=list.getValue("state_desc")%></td><!--描述状态-->
      <td nowrap <%=rowClass%>><%=list.getValue("createDate")%></td><!--制单日期-->
      <td nowrap <%=rowClass%>><%=list.getValue("creator")%></td><!--制单人-->
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr >
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
      <td nowrap>&nbsp;</td>
    </tr>
    <%}%>
  </table>
</form>
<script LANGUAGE="javascript">
  initDefaultTableRow('tableview1',1);
      function sumitFixedQuery(oper)
      {
        lockScreenToWait("处理中, 请稍候！");
        fixedQueryform.operate.value = oper;
        fixedQueryform.submit();
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
      function openwin(rownum)
      {
        paraStr = "bill_edit.jsp?operate=0&htid="+rownum;
        openSelectUrl(paraStr, "NewLadingBill", winopt2);
      }
</script>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <%b_EquipmentResultBean.table.printWhereInfo(pageContext);%>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+ Operate.FIXED_SEARCH +",-1)"%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
                  <pc:shortcut key="x" script="hideFrame('fixedQuery')"/>
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%out.print(retu);%>
</body>
</html>

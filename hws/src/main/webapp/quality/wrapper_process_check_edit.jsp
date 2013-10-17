<!--镀铝纸生产过程检验--><%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "wrapper_process_check";
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
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='wrapper_process_check.jsp';
}

function corpQueryCodeSelect(obj,srcVars)
{
  ProvideCodeChange(document.all['prod'], obj.form.name, srcVars,
                    'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpQueryNameSelect(obj,srcVars)
{
  ProvideNameChange(document.all['prod'], obj.form.name, srcVars,
                    'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function productCodeSelect(obj)
{
  form1.dmsxid,value = "";
  form1.sxz.value="";
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=pm',
  'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value, '');//getPrintModeValue()
}
function productNameSelect(obj)
{
ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=pm',
  'fieldVar=cpid&fieldVar=cpbm&fieldVar=pm&fieldVar=product', obj.value, '');
}
function propertyNameSelect(obj,cpid, srcVar)
{
  PropertyNameChange(document.all['prod'], obj.form.name, srcVar,
                     'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
</script>
<%
  if(!loginBean.hasLimits("wrapper_process_check", request, response))
    return;
  engine.erp.quality.B_ProcessCheck b_ProcessCheckBean = engine.erp.quality.B_ProcessCheck.getInstance(request);
  LookUp checkitemBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_QUALITY_CHECKITEM_PROC);//检验项目
  LookUp workshopBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_WORKSHOP);//车间信息
  LookUp productBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT);//产品编码
  LookUp propertyBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_SPEC_PROPERTY);//规格属性
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);//检验员
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//部门
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<%
String retu = b_ProcessCheckBean.doService(request, response);
if(retu.indexOf("backList();")>-1)
{
out.print(retu);
return;
}
String curUrl = request.getRequestURL().toString();
EngineDataSet ds = b_ProcessCheckBean.getMasterTable();
EngineDataSet list = b_ProcessCheckBean.getDetailTable();
//HtmlTableProducer masterProducer = b_ProcessCheckBean.masterProducer;
RowMap masterRow = b_ProcessCheckBean.getMasterRowinfo();
RowMap[] detailRows= b_ProcessCheckBean.getDetailRowinfos();
propertyBean.regData(ds,"dmsxid");
productBean.regData(ds,"cpid");
deptBean.regData(ds,"deptid");
checkitemBean.regData(list,"checkItemID");
RowMap productRow =productBean.getLookupRow(masterRow.get("cpid"));
boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);
String stateval =masterRow.get("state");
boolean isdel=stateval.equals("0");
boolean isEnd=b_ProcessCheckBean.isApprove||(!b_ProcessCheckBean.masterIsAdd()&&!stateval.equals("0"));
isEnd=isEnd||!(b_ProcessCheckBean.masterIsAdd()?isCanAdd:isCanEdit);
String edClass = isEnd ? "class=edline" : "class=edbox";
String noneClass= isEnd ? "class=ednone" : "class=edbox";
String readonly = isEnd ? " readonly" : "";
String title= stateval.equals("1") ? "已审批":(stateval.equals("9") ? "审批中" : (stateval.equals("0")?"未审批":"未审批" ));

%>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="750" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="djlx" value='1'>
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="750">
    <tr valign="top">
      <td><table width="190" border=0 CELLPADDING=0 CELLSPACING=0 class="table">
          <tr>
            <td width="100%" class="activeVTab">镀铝纸生产过程检验(<%=title%>)</td>
          </tr>
        </table>
        <table width="106%" CELLPADDING=0 CELLSPACING=1 class="editformbox"  >
          <tr>
            <td>
              <table  BORDER="0" CELLPADDING="1" CELLSPACING="1" bgcolor="#f0f0f0">
                <tr>
                  <td  noWrap class="tdTitle">单据号<br></td>
                  <td  noWrap class="td"><input type="text" name="processcheckNo" value='<%=masterRow.get("processcheckNo")%>' maxlength='32' style="width:110" class="edline" readonly></td>
                 <td  noWrap class="tdTitle">检验部门</td>
                  <td  noWrap class="td">
                  <%if(!isEnd){%>
                    <pc:select name="deptid"  style="width:110"  addNull='1'>
                  <%=deptBean.getList(masterRow.get("deptid"))%>
                  </pc:select>
                  <%}else{%>
                  <input type="text"  value='<%=deptBean.getLookupName(masterRow.get("deptid"))%>'  style="width:110"  <%=edClass%> <%=readonly%>>
                  <%}%>
                  </td>
                      <TD align="center" nowrap class="tdtitle">产品名称</TD>
                  <td colspan="3"  noWrap class="td"> <input type="hidden" name="cpid" value='<%=masterRow.get("cpid")%>'>
                    <INPUT <%=edClass%> <%=readonly%> style="WIDTH:70" id="cpbm" name="cpbm" value='<%=productRow.get("cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)">
                    <INPUT <%=edClass%> <%=readonly%>style="WIDTH:180" id="cpmc" name="pm" value='<%=productBean.getLookupName(masterRow.get("cpid"))%>' onKeyDown="return getNextElement();" onchange="productNameSelect(this)">
                    <%if(!isEnd){%> <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=cpbm&srcVar=pm','fieldVar=cpid&fieldVar=cpbm&fieldVar=product','','')">
                    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpbm.value='';pm.value='';cpid.value='';sxz.value='';">
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">规格属性</td>
                  <td colspan="3"><input name="sxz" class=edline readonly value='<%=propertyBean.getLookupName(masterRow.get("dmsxid"))%>' style="width:220" onchange="if(form1.cpid.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid.value)" onKeyDown="return getNextElement();" >
                    <input type="hidden" id="dmsxid"  name="dmsxid" value='<%=masterRow.get("dmsxid")%>'>
                    <input type="hidden" id="isprops" name="isprops" value="">
                    <%if(!isEnd){%><img style='cursor:hand;' src='../images/view.gif' border=0 onClick="if(form1.cpid.value==''){alert('请先输入产品');return;}if(form1.isprops.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid','sxz',form1.cpid.value)">
                    <img style='cursor:hand;' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid.value='';sxz.value='';">
                    <%}%>
                 </td>
                  <td noWrap class="tdTitle">检验数量</td>
                  <td  noWrap class="td"><input type="text" name="checknum"  value='<%=masterRow.get("checknum")%>' maxlength='32' style="width:110" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"></td>
                  <td noWrap class="tdTitle">不合格数</td>
                  <td  noWrap class="td"><input type="text" name="rejectnum" value='<%=masterRow.get("rejectnum")%>' maxlength='32' style="width:110" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"></td>
                </tr>
                <tr>
                <td noWrap class="tdTitle">车间</td>
                  <td  noWrap class="td">
                    <%if(!isEnd){%>
                     <pc:select name="plantid" style="width:110" addNull='1' value='<%=masterRow.get("plantid")%>'  >
                      <%=workshopBean.getList()%> </pc:select>
                      <%}else{%>
                  <input type="text"  value='<%=workshopBean.getLookupName(masterRow.get("plantid"))%>'  style="width:130"  <%=edClass%> <%=readonly%>>
                  <%}%> </td>
             <td  noWrap class="tdTitle">结论</td>
                  <td  noWrap class="td">
          <%if(!isEnd){%>
         <pc:select name='check_verdict' addNull='1' style="width:110" value='<%=masterRow.get("check_verdict")%>'>
           <pc:option value="1">合格</pc:option>
           <pc:option value="0">不合格</pc:option>
        </pc:select>
          <%}else{%>
           <input type="text"  value='<%
String mycheck=masterRow.get("check_verdict");
if(mycheck.equals("1"))out.print("合格");
if(mycheck.equals("0"))out.print("不合格");%>'  style="width:110" <%=edClass%> <%=readonly%>>
                  <%}%>
              </td>
             </tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
                      <table id="tableview1" width="1100" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                        <tr class="tableTitle">
                          <td nowrap width="30">
                          <%if(!isEnd){%>
                          <img style='cursor:hand' src='../images/add.gif' BORDER=0 ONCLICK="sumitForm(<%=Operate.DETAIL_ADD%>)" title="添加"><%}%></td>
                          <td nowrap width="70">工序名称</td>
                          <td nowrap width="100">生产编号</td>
                          <td nowrap width="120">检验项目</td>
                          <td nowrap width="50">单位</td>
                          <td nowrap width="120">标准要求</td>
                          <td nowrap width="110">生产日期</td>
                          <td nowrap width="110">抽样日期</td>
                          <td nowrap width="70">检测结果</td>
                          <td nowrap width="70">检验结论</td>
                          <td nowrap width="70">检验员</td>
                          <td nowrap width="110">备注</td>
                        </tr>
                       <%
                         int i=0;
           RowMap detail = null;
           for(; i<detailRows.length; i++)   {
             detail = detailRows[i];
             RowMap checkitemRow =checkitemBean.getLookupRow(detail.get("checkItemID"));
                      %>
                        <tr id="rowinfo_<%=i%>">
                     <td class="td"><div align="center">
                      <%if(!isEnd){%><img style='cursor:hand' src='../images/del.gif' BORDER=0 ONCLICK="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" title="删除"></div><%}%></td>
                          <td class="td">
                           <%if(!isEnd){%>
                             <pc:select name='<%="procedurename_"+i%>' style="width:80"  addNull='1' value='<%=detail.get("procedurename")%>'>
                               <pc:option value="1">底涂</pc:option>
                      <pc:option value="2">镀铝</pc:option>
                      <pc:option value="3">面涂</pc:option>
                      <pc:option value="4">回潮</pc:option>
                      <pc:option value="5">纵切</pc:option>
                      <pc:option value="6">压纹</pc:option>
                      <pc:option value="7">横切</pc:option>
                          </pc:select>
                          <%}else{%>
                  <input type="text" value='<%
                    String my_procedurename=detail.get("procedurename");
                      if(my_procedurename.equals("1"))out.print("底涂");
                      if(my_procedurename.equals("2"))out.print("镀铝");
                      if(my_procedurename.equals("3"))out.print("面涂");
                      if(my_procedurename.equals("4"))out.print("回潮");
                      if(my_procedurename.equals("5"))out.print("纵切");
                      if(my_procedurename.equals("6"))out.print("压纹");
                      if(my_procedurename.equals("7"))out.print("横切");
                  %>' style="width:110"   <%=noneClass%> <%=readonly%>>
                  <%}%>
                          </td>
                     <td nowrap class=td >
                    <input type="text" name="serial_num_<%=i%>" value='<%=detail.get("serial_num")%>' maxlength='32' style="width:100%" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();"></td>
                    <td nowrap class="td" >
                    <%if(!isEnd){
                    %>
                    <pc:select name='<%="checkItemID_"+i%>' style="width:130"  value='<%=detail.get("checkItemID")%>' onSelect='<%="checkitemIDChange(form1.checkItemID_"+i+".value,"+i+");"%>'>
                    <%=checkitemBean.getList()%>
                    </pc:select>
                    <%}else{%>
                    <input type="text"  value='<%=checkitemBean.getLookupName(detail.get("checkItemID"))%>'  style="width:100%"   <%=noneClass%> <%=readonly%>>
                    <%}%> </td>
                    <td class="td" >
                    <!--单位--><input type="text" name="unit_<%=i%>" value='<%=checkitemRow.get("unit")%>' maxlength='32' style="width:100%" class='edline' readonly onKeyDown="return getNextElement();"></td>
                   <td class="td" ><!--appeal-->
                    <!--标准要求--><input type="text" name="appeal_<%=i%>" value='<%=checkitemRow.get("appeal")%>' maxlength='32' style="width:100%" class='edline' readonly onKeyDown="return getNextElement();"></td>
                    <td class="td">
           <INPUT  <%=noneClass%> <%=readonly%> style="WIDTH: 80px" name="produce_date_<%=i%>" value='<%=detail.get("produce_date")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
           <%if(!isEnd){%><A href="#"><IMG title=选择日期 onClick="selectDate(produce_date_<%=i%>);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A><%}%></td>
           <td class="td">
           <INPUT  <%=noneClass%> <%=readonly%> style="WIDTH: 80px" name="check_date_<%=i%>" value='<%=detail.get("check_date")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
           <%if(!isEnd){%><A href="#"><IMG title=选择日期 onClick="selectDate(check_date_<%=i%>);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A><%}%></td>
            <td class="td">
           <input type="text" name="checkResult_<%=i%>" value='<%=detail.get("checkResult")%>' maxlength='32' style="width:100%"  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();"></td>
           <td class="td">
            <%if(!isEnd){%>
            <pc:select name='<%="check_verdict_"+i%>' style="width:100%"  value='<%=detail.get("check_verdict")%>' >
              <pc:option value="1">合格</pc:option>
             <pc:option value="0">不合格</pc:option>
                      </pc:select>
          <%}else{%>
           <input type="text"  value='<%=detail.get("check_verdict").equals("1")?"合格":"不合格"%>'  style="width:100%"   <%=noneClass%> <%=readonly%>>
                  <%}%>
                  </td>
                 <td class="td">
                  <%if(!isEnd){%>
                  <pc:select name='<%="personid_"+i%>' value='<%=detail.get("personid")%>' style="width:100%"  >
                  <%=personBean.getList()%> </pc:select>
                  <%}else{%>
                  <input type="text"  value='<%=personBean.getLookupName(detail.get("personid"))%>'  style="width:100%" <%=noneClass%> <%=readonly%>>
                  <%}%>
                  </td>
                  <td class="td">
                   <input type="text" name="remark_<%=i%>" value='<%=detail.get("remark")%>' maxlength='32' style="width:100%"  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();"></td>
                  </tr>
                       <%
                         list.next();
           }
           for(; i <5; i++){
                     %>
                        <tr id="rowinfo_<%=i%>">
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td" >&nbsp;</td>
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
                  <table>
                   <tr>
                    <td width="63" class="td"> <p align="center"><strong>备注:</strong></td>
                    <td class="td"> <textarea name="remark"  rows="2" onKeyDown="return getNextElement();" style="width: 670;height: 65" cols="20"  <%=edClass%> <%=readonly%>><%=masterRow.get("remark")%></textarea>
                   </td>
                   </tr>
                 </table>
                </tr>
                </table>
              </table>
            </td>
          </tr>
      </td>
    </tr>
    <tr>
      <td>
       <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
        </table>
        <table CELLSPACING=0 CELLPADDING=0 width="750" align="center">
          <tr>
            <td class="td"><b>制单日期:</b><%=masterRow.get("createDate")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("creator")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
             <%if(!isEnd&&isCanAdd){%>
             <input name="btnback3" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
             <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
             <%}if(!isEnd&&isCanAdd){
               %>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
              <%}if(isdel&&isCanDelete&&!b_ProcessCheckBean.masterIsAdd()){
                String del="if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+","+request.getParameter("rownum")+")";
              %>
              <input name="btnback2" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>)" value="删除(D)">
              <pc:shortcut key="x" script='<%=del%>'/>
              <%}%>
              <input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
                <pc:shortcut key="c" script='backList()'/>
              <%if(!b_ProcessCheckBean.isMasterAdd){%>
              <input type="button"  style="width:60" class="button" value="打印(P)" onclick="location.href='../pub/pdfprint.jsp?code=wrapper_process_check_bill&operate=<%=Operate.PRINT_BILL%>&a$processcheckID=<%=masterRow.get("processcheckID")%>&src=../quality/wrapper_process_check_edit.jsp'">
              <pc:shortcut key="p" script='print()'/><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
function checkitemIDChange(checkitemID,index)
{
  iframeObj=document.all['prod'],
  paraStr = "wrapper_process_check_bottom.jsp?checkitemID="+checkitemID+"&index="+index;
  //if(methodName+'' != 'undefined')
  //paraStr += "&method="+methodName;
  //if(notin+'' != 'undefined')
  //paraStr += "&notin="+notin;
  //alert(index);
  iframeObj.src=paraStr;
}
function SelectcheckItem(checkitemid){
  <%
  RowMap checkItemRow =checkitemBean.getLookupRow("186");
  %>
}
  function print()
              {
   location.href='../pub/pdfprint.jsp?code=wrapper_process_check_bill&operate=<%=Operate.PRINT_BILL%>&a$processcheckID=<%=masterRow.get("processcheckID")%>&src=../quality/wrapper_process_check_edit.jsp'
     }
</script>
<script language="javascript">initDefaultTableRow('tableview1',1);</script>
<%if(b_ProcessCheckBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
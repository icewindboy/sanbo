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
  String pageCode  = "buy_fitting_requisition";
%>
<%
  if(!loginBean.hasLimits("buy_fitting_requisition", request, response))
    return;
  engine.erp.equipment.B_EquipmentBuyApply b_EquipmentBuyApplyBean = engine.erp.equipment.B_EquipmentBuyApply.getInstance(request);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);//检验员
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//部门
  LookUp productBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_PRODUCT);//设备
  b_EquipmentBuyApplyBean.apply_type="2";
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
  location.href="buy_fitting_requisition.jsp";
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value);
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value);
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
</script>

<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<%
String retu = b_EquipmentBuyApplyBean.doService(request, response);
if(retu.indexOf("backList();")>-1)
{
out.print(retu);
return;
}
String curUrl = request.getRequestURL().toString();
EngineDataSet ds = b_EquipmentBuyApplyBean.getMasterTable();
EngineDataSet list = b_EquipmentBuyApplyBean.getDetailTable();

RowMap masterRow = b_EquipmentBuyApplyBean.getMasterRowinfo();
RowMap[] detailRows= b_EquipmentBuyApplyBean.getDetailRowinfos();
personBean.regData(ds,"personid");
deptBean.regData(ds,"deptid");
productBean.regData(list,"cpid");
boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);
String stateval =masterRow.get("state");
boolean isdel=stateval.equals("0");
boolean isEnd=b_EquipmentBuyApplyBean.isApprove||(!b_EquipmentBuyApplyBean.masterIsAdd()&&!stateval.equals("0"));
isEnd=isEnd||!(b_EquipmentBuyApplyBean.masterIsAdd()?isCanAdd:isCanEdit);
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
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td width="160" class="activeVTab" id='lx'>元件购置申请(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <tr>
                  <td noWrap class="tdTitle">单据号</td>
                  <td noWrap class="td"><input type="text" name="buy_applyNO" value='<%=masterRow.get("buy_applyNO")%>' maxlength='32' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  </td>
                  <td noWrap class="tdTitle">申购部门</td>
                  <td noWrap class="td"> <%if(!isEnd){%>
                 <pc:select name="deptid" style="width:110" addNull='1' value='<%=masterRow.get("deptid")%>' onSelect="deptchange();">
                 <%=deptBean.getList()%>
                  </pc:select>
                  <%}else{%>
                  <input type="text"  value='<%=deptBean.getLookupName(masterRow.get("deptid"))%>'  style="width:110"  <%=edClass%> <%=readonly%>>
                  <%}%> </td>
                  <td noWrap class="tdTitle">申购人</td>
                  <td  noWrap class="td"> <%if(!isEnd){%>
                  <pc:select name="personid"  style="width:110"  addNull='1' value='<%=masterRow.get("personid")%>'>
                  <%=personBean.getList()%>
                  </pc:select>
                  <%}else{%>
                  <input type="text"  value='<%=personBean.getLookupName(masterRow.get("personid"))%>'  style="width:110"  <%=edClass%> <%=readonly%>>
                  <%}%></td>
                  <td noWrap class="tdTitle">申购日期</td>
                  <td  noWrap class="td"><input type="text" name="apply_date" value='<%=masterRow.get("apply_date")%>' maxlength='32' style="width:85" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onChange="checkDate(this)">
                  <%if(!isEnd){%><A href="#"><IMG style="cursor:hand" title=选择日期 onClick="selectDate(apply_date);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
                  <%}%></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">申购原因</td>
                  <td colspan="5"  noWrap class="td">
                  <input type="text" name="apply_cause" value='<%=masterRow.get("apply_cause")%>' maxlength='32' style="width:95%" <%=edClass%> <%=readonly%>></td>
                  <td noWrap class="tdTitle">&nbsp;</td>
                  <td noWrap class="td">&nbsp;</td>
                </tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
                      <table id="tableview1" width="830" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                        <tr class="tableTitle">
                       <%if(!isEnd){%><td nowrap width="50" rowspan="2"><div align="center">
                          <input name="image22" type="image" class="img" title="添加(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add.gif"  border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/>
                           </div></td><%}%>
                          <td height="18" colspan="7" nowrap>元件型号比较</td>
                        </tr>
                        <tr class="tableTitle">
                           <td width="60" nowrap>元件编码</td>
                          <td width="100" nowrap>元件名称</td>
                          <td width="155"  nowrap>型号规格</td>
                          <td width="180"  nowrap>制造厂</td>
                          <td width="80"  nowrap>价格性能比较</td>
                          <td width="80"  nowrap>申购数量</td>
                          <td width="85"  nowrap>要求到货日期</td>
                        </tr>
                          <%
                            int i=0;
                            RowMap detail = null;
                            for(; i<detailRows.length; i++)   {
                            detail = detailRows[i];
                            RowMap productRow = productBean.getLookupRow(detail.get("cpid"));
                         %>
                          <tr id="rowinfo_<%=i%>">
                          <%if(!isEnd){%><td class="td"><div align="center">
                          <img name="image2" type="image"  title="选择元件" onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=pm_<%=i%>&srcVar=gg_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=pm&fieldVar=gg','','')"  src="../images/select_prod.gif" width="15" height="15" border="0">
                          <img style='cursor:hand'type="image" class="img" title="删除" src='../images/delete.gif' BORDER=0 ONCLICK="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)"></div></td><%}%>
                          <td class="td">
                         <input type="hidden" name="cpid_<%=i%>" value='<%=detail.get("cpid")%>'>
                          <input type="text" name="cpbm_<%=i%>" value='<%=productRow.get("cpbm")%>' maxlength='32' style="width:100%" class="edline" readonly></td>
                          <td class="td">
                          <input type="text" name="pm_<%=i%>" value='<%=productRow.get("pm")%>' maxlength='32' style="width:100%" class="edline" readonly></td>
                          <td class="td">
                          <input type="text" name="gg_<%=i%>" value='<%=productRow.get("gg")%>' maxlength='32' style="width:100%" class="edline" readonly></td>
                          <td class="td">
                          <input type="text" name="manufacturer_<%=i%>" value='<%=detail.get("manufacturer")%>' maxlength='32' style="width:100%" <%=noneClass%> <%=readonly%>></td>
                          <td class="td">
                          <input type="text" name="price_<%=i%>" value='<%=detail.get("price")%>' maxlength='32' style="width:100%" <%=noneClass%> <%=readonly%>></td>
                          <td class="td">
                          <input type="text" name="apply_num_<%=i%>" value='<%=detail.get("apply_num")%>' maxlength='32' style="width:100%" <%=noneClass%> <%=readonly%> onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"></td>
                          <td class="td">
                          <input type="text" name="need_getdate_<%=i%>" value='<%=detail.get("need_getdate")%>' maxlength='32' style="width:70" <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" onChange="checkDate(this)">
                         <%if(!isEnd){%><A href="#"><IMG style="cursor:hand" title=选择日期 onClick="selectDate(need_getdate_<%=i%>);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
                       <%}%></td>
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
                        </tr>
                        <%}%>
                      </table>
                    </div></td>
                </tr>
              </table>
            </td>
          </tr>
		  <tr>
			<td colspan="=8">
			 <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
                <tr>
                  <td width="57" class="td"><div align="center"><strong>备注</strong></div></td>
                  <td width="701" class="td"><textarea name="memo" rows="2" onKeyDown="return getNextElement();" style="width: 690; height: 65" cols="20"><%=masterRow.get("memo")%></textarea></td>
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
              <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);"  value="保存添加(N)">
              <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <%}if(!isEnd&&isCanAdd){%>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
               <%}if(isdel&&isCanDelete&&!b_EquipmentBuyApplyBean.masterIsAdd()){
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
<script language="javascript">initDefaultTableRow('tableview1',1);</script>
<%if(b_EquipmentBuyApplyBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</Html>
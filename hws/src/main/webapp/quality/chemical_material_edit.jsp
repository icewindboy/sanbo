<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap,engine.html.*"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "chemical_material";
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
  if(!loginBean.hasLimits("chemical_material", request, response))
    return;

  engine.erp.quality.B_ChemicalMaterial chemical_materialBean = engine.erp.quality.B_ChemicalMaterial.getInstance(request);
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);
  LookUp corpBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_CORP);//往来单位
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
 // LookUp productBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT);//产品编码
 // LookUp propertyBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_SPEC_PROPERTY);//规格属性
%>
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
  location.href='chemical_material.jsp';
}
 function openSelect(frmName, srcVar, methodName,notin)
{
   //引入库单货物
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "contract_instore_select2.jsp";
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }

function corpCodeSelect(obj)
{
  ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, '');
}
function corpNameSelect(obj)
{
  ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, '');
}
function corpCydwCodeSelect(obj)
{
  TransportCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwt_dwtxid&srcVar=dwt_dwdm&srcVar=dwt_dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpCydwNameSelect(obj)
{
  TransportNameChange(document.all['prod'], obj.form.name, 'srcVar=dwt_dwtxid&srcVar=dwt_dwdm&srcVar=dwt_dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function productCodeSelect(obj, i)
{
  //ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
               //  'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value);
}
function productNameSelect(obj,i)
{
 // ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
               //  'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value);
}
function propertyNameSelect(obj,cpid,i)
{
  //PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                // 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid',  eval('form1.deptid.value'), '');
}
</script>

<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<%String retu = chemical_materialBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = chemical_materialBean.getMaterTable();
  EngineDataSet list = chemical_materialBean.getDetailTable();
  HtmlTableProducer masterProducer = chemical_materialBean.masterProducer;
 // HtmlTableProducer detailProducer = chemical_materialBean.detailProducer;
  RowMap masterRow = chemical_materialBean.getMasterRowinfo();
  //RowMap detailRow = chemical_materialBean.getDetailRowinfos();
  RowMap[] detailRows= chemical_materialBean.getDetailRowinfos();
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit = loginBean.hasLimits(pageCode, op_edit);
  String state=masterRow.get("state");
  boolean isEnd=chemical_materialBean.isApprove||(!chemical_materialBean.masterIsAdd()&&!state.equals("0"));
  isEnd=isEnd||!(chemical_materialBean.masterIsAdd()?isCanAdd:isCanEdit);
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String noneClass= isEnd ? "class=ednone" : "class=edbox";
  String readonly = isEnd ? " readonly" : "";
  //String title= state.equals("1") ? "已审批":(state.equals("9") ? "审批中" : (state.equals("0")?"未审批":"" ));
  String title = state.equals("1")?"已审批" :(state.equals("9") ? "审批中": (state.equals("0") ? "未审批":"未审批"));
  boolean isAdd = chemical_materialBean.isDetailAdd;
  boolean isdel=state.equals("0");
  String deptid = masterRow.get("deptid");
  String approverID = masterRow.get("approverID");
  boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, approverID);//判断登陆员工是否有操作改制单人单据的权限
  deptBean.regData(ds,"deptid");
  corpBean.regData(ds,"dwtxid");
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="djlx" value='1'>
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="750">
    <tr valign="top">
      <td><table width="200" border=0 CELLPADDING=0 CELLSPACING=0 class="table">
          <tr>
            <td width="200" class="activeVTab">化工原料检验报告单(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <tr>
                  <td  noWrap class="tdTitle">单据号<br></td>
                  <td  noWrap class="td"><input type="text" name="chemicalcheckNo" value='<%=masterRow.get("chemicalcheckNo")%>' maxlength='32' style="width:110" class="edline" readonly></td>
                  <TD class="tdtitle" nowrap>供应商</TD>
                  <TD nowrap class="td" colspan="3"> <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text"  <%=edClass%> <%=readonly%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="corpCodeSelect(this)">
                    <input type="text" <%=edClass%> <%=readonly%> name="dwmc" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:180"  onchange="corpNameSelect(this)">
                    <%if(!isEnd){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value)">
                    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
                  <%}%>
                  </TD>
                  <td noWrap class="tdTitle">供货日期</td>
                  <td noWrap class="td"><input type="text" name="get_date" value='<%=masterRow.get("get_date")%>' maxlength='10' style="width:80" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=readonly%>>
                  <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.get_date);"></a><%}%>
                  </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle">供货数量</td>
                  <td  noWrap class="td"><input type="text" name="check_num" value='<%=masterRow.get("check_num")%>' maxlength='32' style="width:110"  <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                  </td>
                  <td  noWrap class="tdtitle">配料日期 </td>
                  <td  noWrap class="td"><INPUT style="WIDTH: 85" name="shareout_date" value='<%=masterRow.get("shareout_date")%>' maxlength='10' <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=readonly%>>
                    <%if(!isEnd){%><A href="#"><IMG title=选择日期 onClick="selectDate(shareout_date);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A><%}%></td>
                  <td  noWrap class="tdTitle">湿度</TD>
                  <td><input type="text" name="humidity" value='<%=masterRow.get("humidity")%>' maxlength='32' style="width:110"  <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();"></td>
                  <td  noWrap class="tdTitle">温度</TD>
                  <td><input type="text" name="temperature" value='<%=masterRow.get("temperature")%>' maxlength='32' style="width:110"  <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();"></td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle">检验部门</td>
                  <td noWrap class="td">
                    <%if(!isEnd){%>
                     <pc:select name="deptid" style="width:110" addNull="1"  value='<%=masterRow.get("deptid")%>'  onSelect = " deptchange();"  >
                      <%=deptBean.getList()%> </pc:select>
                      <%}else{%>
                  <input type="text"  value='<%=deptBean.getLookupName(masterRow.get("deptid"))%>'  style="width:130"  <%=edClass%> <%=readonly%>>
                  <%}%>
                  </td>
                  <td  noWrap class="tdTitle">检验员</TD>
                  <td  noWrap class="td">
                     <%if(!isEnd){%>
                     <pc:select name="personid" style="width:110" addNull="1" value='<%=masterRow.get("personid")%>'>
                      <%=personBean.getList()%> </pc:select>
                  <%}else{%>
                  <input type="text"  value='<%=personBean.getLookupName(masterRow.get("personid"))%>'  style="width:130"   <%=edClass%> <%=readonly%>>
                  <%}%>
                  </td>
                  <td  noWrap class="tdTitle">检验日期</td>
                  <td  noWrap class="td" colspan="3"><INPUT  <%=edClass%> <%=readonly%> style="WIDTH: 85px" name="check_date" value='<%=masterRow.get("check_date")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                   <%if(!isEnd){%><A href="#"><IMG title=选择日期 onClick="selectDate(check_date);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A><%}%>
                  </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle">判断</TD>
                  <td colspan="3"><input type="text" name="estimation" value='<%=masterRow.get("estimation")%>' maxlength='32' style="width:350" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();"></td>
                  <td  noWrap class="tdtitle">&nbsp;</td>
                  <td colspan="3"  noWrap class="td">&nbsp;</td>
                </tr>
                <tr>
                  <td colspan="8" noWrap class="td">
                   <div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
                      <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                        <tr class="tableTitle">
                          <td width="20" height="20" rowspan="2" nowrap>
                           </td>
                           <td rowspan="2" nowrap >
                     <%if(!isEnd){%><input name="image22" type="image" class="img" title="添加" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add.gif"  border="0">
                           <%}%>
                          </td>
                          <td rowspan="2" nowrap >产品名称</td>
                          <td colspan="2" nowrap>固含量</td>
                          <td colspan="2" nowrap>粘度</td>
                          <td colspan="2" nowrap >沸点初始温度(℃)</td>
                          <td colspan="2" nowrap >沸点终点温度(℃)</td>
                        </tr>
                        <tr class="tableTitle">
                          <td nowrap>技术要求</td>
                          <td nowrap>实测结果</td>
                          <td nowrap>技术要求</td>
                          <td nowrap>实测结果</td>
                          <td nowrap>技术要求</td>
                          <td nowrap>实测结果</td>
                          <td nowrap>技术要求</td>
                          <td nowrap>实测结果</td>
                        </tr>
                      <%
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                      %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap align="center"><%=i+1%></td>
                        <td class="td" nowrap align="center">
                         <%if(!isEnd){%> <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                         <%}%>
                        </td>
                        <td nowrap>
                          <input type="text" name="porduct_name_<%=i%>" value='<%=detail.get("porduct_name")%>'  style="width:100%"  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" maxlength='<%=list.getColumn("porduct_name").getPrecision()%>' >
                        </td>
                        <td nowrap>
                          <input type="text" name="content_request_<%=i%>" value='<%=detail.get("content_request")%>'  style="width:100%"  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" maxlength='<%=list.getColumn("content_request").getPrecision()%>' >
                        </td>
                        <td nowrap>
                          <input type="text" name="content_result_<%=i%>" value='<%=detail.get("content_result")%>'  style="width:100%"  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" maxlength='<%=list.getColumn("content_result").getPrecision()%>' >
                        </td>
                        <td nowrap>
                          <input type="text" name="adhibit_request_<%=i%>" value='<%=detail.get("adhibit_request")%>'  style="width:100%"  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" maxlength='<%=list.getColumn("adhibit_request").getPrecision()%>'>
                        </td>
                        <td nowrap>
                          <input type="text" name="adhibit_result_<%=i%>" value='<%=detail.get("adhibit_result")%>'  style="width:100%"  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" maxlength='<%=list.getColumn("adhibit_result").getPrecision()%>'>
                        </td>
                        <td nowrap>
                          <input type="text" name="beginboil_request_<%=i%>" value='<%=detail.get("beginboil_request")%>'  style="width:100%"  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" maxlength='<%=list.getColumn("beginboil_request").getPrecision()%>'>
                        </td>
                        <td nowrap>
                          <input type="text" name="beginboil_result_<%=i%>" value='<%=detail.get("beginboil_result")%>'  style="width:100%"  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" maxlength='<%=list.getColumn("beginboil_result").getPrecision()%>'>
                        </td>
                        <td nowrap>
                          <input type="text" name="endboil_request_<%=i%>" value='<%=detail.get("endboil_request")%>'  style="width:100%"  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" maxlength='<%=list.getColumn("endboil_request").getPrecision()%>'>
                        </td>
                        <td nowrap>
                          <input type="text" name="endboil_result_<%=i%>" value='<%=detail.get("endboil_result")%>'  style="width:100%"  <%=noneClass%> <%=readonly%> onKeyDown="return getNextElement();" maxlength='<%=list.getColumn("endboil_result").getPrecision()%>'>
                        </td>
                        </tr>
                          <%
                        list.next();
                      }
                      for(; i < 4; i++){
                  %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td nowrap>&nbsp;</td>
                      </tr>
                      <%}%>
                      </table>
                    </div></td>
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
           <td width="439" class="td"><b>制单日期:</b><%=masterRow.get("createDate")%></td>
            <td width="29" class="td"></td>
            <td width="264" align="right" class="td"><b>制单人:</b><%=masterRow.get("creator")%>
          </td>
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
              <%}if(isdel&&isCanDelete&&!chemical_materialBean.isMasterAdd){
              String del="if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+","+request.getParameter("rownum")+")";
              %>
              <input name="btnback2" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>)" value="删除(D)">
              <pc:shortcut key="x" script='<%=del%>'/>
              <%}%>
              <input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
               <pc:shortcut key="c" script='<%="backList();"%>'/>
              <%if(!chemical_materialBean.isMasterAdd){%>
             <input type="button"  style="width:60" class="button" value="打印(P)" onclick="location.href='../pub/pdfprint.jsp?code=chemical_material_bill&operate=<%=Operate.PRINT_BILL%>&a$chemicalcheckID=<%=masterRow.get("chemicalcheckID")%>&src=../quality/chemical_material_edit.jsp'">
              <pc:shortcut key="p" script='print()'/><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
function print()
{
     location.href='../pub/pdfprint.jsp?code=chemical_material_bill&operate=<%=Operate.PRINT_BILL%>&a$chemicalcheckID=<%=masterRow.get("chemicalcheckID")%>&src=../quality/chemical_material_edit.jsp'
}
</script>
<script language="javascript">initDefaultTableRow('tableview1',1);
</script>
<%if(chemical_materialBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
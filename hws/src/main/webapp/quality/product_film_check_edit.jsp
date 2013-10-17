<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.quality.B_ProductCheck productBean=engine.erp.quality.B_ProductCheck.getInstance(request);
  String pageCode = "product_film_check";
  productBean.checkType="4";
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
  location.href='product_film_check.jsp';
}
function productCodeSelect(obj)
{
  form1.dmsxid,value = "";
  form1.sxz.value="";
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=pm&srcVar=jldw&srcVar=wzlbid',
  'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=wzlbid', obj.value, 'sumitForm(<%=productBean.PRODUCTCHANGE%>,-1)');//getPrintModeValue()
}
function productNameSelect(obj)
{
ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=pm&srcVar=pm&srcVar=jldw&srcVar=wzlbid',
  'fieldVar=cpid&fieldVar=cpbm&fieldVar=pm&fieldVar=product&fieldVar=jldw&fieldVar=wzlbid', obj.value, 'sumitForm(<%=productBean.PRODUCTCHANGE%>,-1)');
}
/**
 * iframeObj IFrame的对象
 * lookup    Lookup Bean 的名称
 * frmName   表单名称
 * srcVar    表单各个需要取值的控件名称字符串。如:srcVar=dmsxid_0&srcVar=sxz_0
 * fieldVar  与各个需要取值的控件名称相对应的字段名称字符串。如:fieldVar=dmsxid&fieldVar=sxz
 * curID     当前要得到的ID的值
 * methodName  对输入框赋值后的要调用的方法名称
 */
function getPrintModeValue()
{
  //alert(form1.wzlbid.value);
  getRowValue(document.all['prod'], 'kc_chlb', "form1", "srcVar=dygs", "fieldVar=dygs", form1.wzlbid.value, "");
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
function corpCodeSelect(obj)
{
  corpChange();
  ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpNameSelect(obj)
{
  corpChange();
  ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpChange(){
  form1.cpid.value='';
  form1.cpbm.value='';
  form1.pm.value='';
  form1.jldw.value='';
  form1.dmsxid.value='';
  form1.sxz.value='';
}
function add_Check(){
  if(form1.cpid.value==''){
    alert('请选择产品');
    return false;
  }
  else if(form1.v_check_verdict.value==''){
    alert('请选择检验结论');
    return false;
  }
  else if(form1.v_deptid.value==''){
    alert('请选择检验部门');
    return false;
  }
  else if(form1.v_personid.value==''){
    alert('请选择检验员');
    return false;
  }
  else{
    return true;
  }
}

  /*日期大小比较函数
*支持年－月－日这样的格式
  */
function compareDate(DateOne,DateTwo)
{
  var OneMonth = DateOne.substring(5,DateOne.lastIndexOf ("-"));
  var OneDay = DateOne.substring(DateOne.length,DateOne.lastIndexOf ("-")+1);
  var OneYear = DateOne.substring(0,DateOne.indexOf ("-"));
  var TwoMonth = DateTwo.substring(5,DateTwo.lastIndexOf ("-"));
  var TwoDay = DateTwo.substring(DateTwo.length,DateTwo.lastIndexOf ("-")+1);
  var TwoYear = DateTwo.substring(0,DateTwo.indexOf ("-"));
  if (Date.parse(OneMonth+"/"+OneDay+"/"+OneYear) > Date.parse(TwoMonth+"/"+TwoDay+"/"+TwoYear))
  {
    return 1;
  }
  else if (Date.parse(OneMonth+"/"+OneDay+"/"+OneYear) == Date.parse(TwoMonth+"/"+TwoDay+"/"+TwoYear))
  {
    return 0;
  }
  else if (Date.parse(OneMonth+"/"+OneDay+"/"+OneYear) < Date.parse(TwoMonth+"/"+TwoDay+"/"+TwoYear))
  {
    return -1;
  }
  else
  {
    return 2;
  }
}
function prodChange(){
  form1.receivedetailid.value='';
  form1.sfdjdh.value='';
  form1.dmsxid.value='';
  form1.sxz.value='';
}
</script>
<%
  String retu = productBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp checkItemBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_QUALITY_CHECKITEM_FACE);
  engine.project.LookUp tooltypeBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_QUALITY_CHECKSTANDARD);
  engine.project.LookUp rkdmBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_RECEIVE_MATERIAL_DETALL);//入库单明细
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//部门
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);//人员
  engine.project.LookUp proBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//产品编码
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//规格属性
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);//仓库
  //BEAN_QUALITY_CHECKITEM
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = productBean.getMaterTable();
  EngineDataSet list = productBean.getDetailTable();//
  EngineDataSet facies = productBean.getFaciesTable();
  EngineDataSet app = productBean.getAppendTable();
  HtmlTableProducer masterProducer = productBean.masterProducer;
  HtmlTableProducer detailProducer = productBean.detailProducer;
  RowMap masterRow = productBean.getMasterRowinfo();
  RowMap[] detailRows= productBean.getDetailRowinfos();
  RowMap[] faciesRows= productBean.getFaciesRowinfos();
  String deptid = masterRow.get("deptid");
  tooltypeBean.regData(ds,"standardid");
  //checkItemBean.regData(ds,"checkItemid");
  deptBean.regData(ds,"deptid");
  propertyBean.regData(ds,"dmsxid");
  rkdmBean.regData(ds,"receivedetailid");
  proBean.regData(ds,"cpid");
  //boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, czyid);//判断登陆员工是否有操作改制单人单据的权限
  boolean isHasDeptLimit=true;

  //RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
  RowMap productRow =proBean.getLookupRow(masterRow.get("cpid"));
  boolean isCanAmend = loginBean.hasLimits(pageCode, op_edit);
  boolean isCanDelete = loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd=loginBean.hasLimits(pageCode, op_add);
  boolean isEnd =!(productBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  String state=masterRow.get("state");
  String title = state.equals("1") ? "已审核":(state.equals("9") ? "审批中":(state.equals("0") ? "未审核":"未审核"));
  boolean isAdd = productBean.isDetailAdd;
  boolean isCanChange=(isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit))&&(state.equals("0")||state.equals(""));
  String edClass = isCanChange? "class=edbox":"class=edline";
  String readonly =isCanChange? "":"readonly";
%>

<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <input type="hidden" name="isprops" value="">
  <input type="hidden" name="wzlbid" value='<%=masterRow.get("wzlbid")%>'>
  <input type="hidden" name="billID" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">成品膜检验报告单(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                 <tr>
                  <td noWrap class="tdTitle">检验单号</td>
                  <td noWrap class="td"><input type="text" name="productCheckNo" value='<%=masterRow.get("productCheckNo")%>' maxlength='<%=ds.getColumn("productCheckNo").getPrecision()%>' style="width:110" onKeyDown="return getNextElement();" class='edline' readonly></td>
                  <td noWrap class="tdTitle">自制收货单号</td>
                  <td noWrap class="td">
                    <input type="hidden" name="receivedetailid" value='<%=masterRow.get("receivedetailid")%>'>
                    <input type="text" name="sfdjdh"  value='<%=rkdmBean.getLookupName(masterRow.get("receivedetailid"))%>' maxlength='<%=ds.getColumn("productCheckNo").getPrecision()%>' style="width:110" onKeyDown="return getNextElement();" class='edline' readonly></td>
                  <TD align="center" nowrap class="tdtitle">产品名称</TD>
                  <td colspan="3"  noWrap class="td"> <input type="hidden" name="cpid" value="<%=masterRow.get("cpid")%>">
                    <INPUT type=text <%=edClass%> <%=readonly%> style="WIDTH:70" id="cpbm" name="cpbm" value='<%=productRow.get("cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)">
                    <INPUT type=text <%=edClass%> <%=readonly%> style="WIDTH:180" id="cpmc" name="pm" value='<%=proBean.getLookupName(masterRow.get("cpid"))%>' onKeyDown="return getNextElement();" onchange="productNameSelect(this)">
                    <%if(isCanChange){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=cpbm&srcVar=pm&srcVar=wzlbid&srcVar=jldw','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=wzlbid&fieldVar=jldw','','sumitForm(<%=productBean.PRODUCTCHANGE%>,-1)')">
                    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpbm.value='';cpmc.value='';cpid.value='';dmsxid.value='';sxz.value='';">
                    <%}%>
                  </td>
                 <tr>

                  <td noWrap class="tdTitle">规格属性</td>
                  <td colspan="3">
                     <input name="sxz" class=edline readonly value='<%=propertyBean.getLookupName(masterRow.get("dmsxid"))%>' style="width:220" onchange="if(form1.cpid.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid.value)" onKeyDown="return getNextElement();" >
                     <input type="hidden" id="dmsxid" name="dmsxid" value="<%=masterRow.get("dmsxid")%>">
                     <%if(isCanChange){%><img style='cursor:hand;' src='../images/view.gif' border=0 onClick="if(form1.cpid.value==''){alert('请先输入产品');return;}if(form1.isprops.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid','sxz',form1.cpid.value)">
                     <img style='cursor:hand;' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid.value='';sxz.value='';">
                    <%}%>
                  </td>
                   <td noWrap class="tdTitle">单位</td>
                   <td noWrap class="td">
                     <input type="text" name="jldw" value='<%=productRow.get("jldw")%>' style="width:110"  onKeyDown="return getNextElement();" class='edline' readonly>
                   </td>
                    <td noWrap class="tdTitle">生产批号</td>
                    <td noWrap class="td">
                     <input type="text" name="productno" value='<%=masterRow.get("productno")%>' style="width:110"  onKeyDown="return getNextElement();" <%=edClass%> <%=readonly%>>
                    </td>
                  </tr>
                  <tr>
                    <td noWrap class="tdTitle">抽检日期</td>
                    <td noWrap class="td">
                      <input type="text" name="buyCheckDate" value='<%=masterRow.get("buyCheckDate")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                      <%if(isCanChange){%>
                      <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.buyCheckDate);"></a>
                      <%}%>
                    </td>
                   <td noWrap class="tdTitle">抽检数量</td>
                   <td noWrap class="td">
                    <input type="text" name="check_num" value='<%=masterRow.get("check_num")%>' maxlength='<%=ds.getColumn("check_num").getPrecision()%>' style="width:110"  onKeyDown="return getNextElement();" <%=edClass%> <%=readonly%>>
                   </td>
                   <td noWrap class="tdTitle">检验依据</td>
                   <td noWrap class="td">
                    <%if(!isCanChange)out.print("<input type='text' value='"+tooltypeBean.getLookupName(masterRow.get("standardid"))+"' style='width:110' class='edline' readonly>");
                       else {%>
                     <pc:select name="standardid"  addNull="1"  style="width:110" value='<%=masterRow.get("standardid")%>'>
                      <%=tooltypeBean.getList()%> </pc:select>
                     <%}%>
                   </td>
                   <td noWrap class="tdTitle">不合格数</td>
                   <td noWrap class="td">
                    <input type="text" name="reject_num" value='<%=masterRow.get("reject_num")%>'  style="width:110"  onKeyDown="return getNextElement();" <%=edClass%> <%=readonly%>>
                   </td>
                  </tr>
                  <tr>
                  <td noWrap class="tdTitle">检验部门</td>
                  <td noWrap class="td">
                    <%if(!isCanChange)out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                     else {%>
                     <pc:select name="deptid"  addNull="1"  style="width:110" value='<%=masterRow.get("deptid")%>' onSelect="deptchange();">
                      <%=deptBean.getList()%> </pc:select>
                     <%}%>
                  </td>
                  <td noWrap class="tdTitle">检验员</td>
                  <td noWrap class="td">
                    <%if(!isCanChange)out.print("<input type='text' value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:110' class='edline' readonly>");
                     else {%>
                       <pc:select name="personid"  addNull="1"  style="width:110" value='<%=masterRow.get("personid")%>'>
                      <%=personBean.getList()%> </pc:select>
                     <%}%>
                  </td>
                  <td noWrap class="tdTitle">检验结论</td>
                  <td noWrap class="td"><%String verdict=masterRow.get("check_verdict").equals("1")?"合格":"不合格";%>
                    <%if(!isCanChange)out.print("<input type='text' value='"+verdict+"' style='width:110' class='edline' readonly>");
                     else {%>
                      <pc:select name="check_verdict" style="width:110" value='<%=masterRow.get("check_verdict")%>'>
                       <pc:option value=""></pc:option>
                       <pc:option value="0">不合格</pc:option>
                       <pc:option value="1">合格</pc:option>
                      </pc:select>
                     <%}%>
                  </td>
                 </tr>
                 <tr>
                  <td colspan="8" noWrap class="td">
                     <table width="100%" cellpadding=0 cellspacing=0 dwcopytype="CopyTableRow">
                      <tr>
                        <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="javascript:" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">技术指标</a></div></td>
                        <td nowrap><div id="tabDivINFO_EX_1" class="normalTab"><a class="tdTitle" href="javascript:" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_1');return false;">外观</a></div></td>
                        <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="javascript:">&nbsp;</a></td>
                      </tr>
                    </table>
                    <%--技术指标--%>
                      <%
                      int j=0;
                      RowMap detail = null;
                      int height=0;
                      if(detailRows.length>10)height=10*28;
                      else height=j*26;
                      %>
                    <div style="display:block;width:750;height=<%=height%>;overflow-y:auto;" id="cntDivINFO_EX_0"  class="tabContent">
                    <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <%--detailProducer.printTitle(pageContext, "height='20'");--%>
                      <td nowrap height=20>检验项目</td>
                      <td nowrap>单位</td>
                      <td nowrap>标准要求</td>
                      <td nowrap>检验结果</td>
                      <td nowrap>结论</td>
                      </tr>
                      <%
                      for(; j<detailRows.length; j++)   {
                      detail = detailRows[j];%>
                     <tr>
                       <td>
                        <input type="hidden" id="checkitemid_<%=j%>" name="checkitemid_<%=j%>" value='<%=detail.get("checkitemid")%>'>
                        <input type="text" class=ednone readonly id="checkitem_<%=j%>" name="checkitem_<%=j%>" value='<%=detail.get("checkitem")%>'>
                       </td>
                       <td>
                        <input type="text" class=ednone readonly id="unit_<%=j%>" name="unit_<%=j%>" value='<%=detail.get("unit")%>'>
                       </td>
                       <td>
                        <input type="text" class=ednone readonly id="techRequest_<%=j%>" name="techRequest_<%=j%>" value='<%=detail.get("techRequest")%>'>
                       </td>
                       <td>
                        <input type="text"  <%=readonly%> class=<%=isCanChange?"edbox":"ednone"%> style="width:150" id="checkResult_<%=j%>" name="checkResult_<%=j%>" value='<%=detail.get("checkResult")%>'>
                       </td>
                       <td><%String value=detail.get("check_verdict").equals("1")?"合格":"不合格";%>
                       <%if(!isCanChange)out.print("<input type='text' value='"+value+"' style='width:110' class='ednone' readonly>");
                         else {%>
                         <pc:select name='<%="fcheck_verdict_"+j%>' addNull="1" style="width:110" value='<%=detail.get("check_verdict")%>'>
                           <pc:option value="0">不合格</pc:option>
                           <pc:option value="1">合格</pc:option>
                         </pc:select><%}%>
                       </td>
                     </tr>
                     <%
                       }
                       for(; j < 5;j++){
                       %>
                      <tr id="rowinfo_<%=j%>">
                         <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                         <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                      </tr>
                      <%}%>
                    </table>
                    </div>
                    <%--外观--%>
                     <%
                      int i=0;
                      RowMap face=null;
                      int face_height=0;
                      if(faciesRows.length>10)face_height=10*28;
                      else face_height=i*26;
                    %>
                    <div id="cntDivINFO_EX_1" style="display:none;width:750;height:<%=face_height%>;overflow-y:auto;overflow-x:auto;" class="tabContent">
                      <table id="tableview2" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                        <tr class="tableTitle">
                        <td class="td" nowrap width=15></td>
                          <td nowrap width="25"><div align="center">
                            <%if(loginBean.hasLimits(pageCode, op_add)&&(state.equals("0")||state.equals(""))){%>
                             <input name="image222" type="image" class="img" title="添加" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add.gif"  border="0">
                            <%}%>
                            </div></td>
                          <td  height='20' nowrap width="110">检验项目</td>
                          <td  nowrap width="110">结论</td>
                          <td  height='20' nowrap>备注</td>
                        </tr>
                        <%
                      for(; i<faciesRows.length; i++)   {
                        face=faciesRows[i];
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap width='15' align=center><%=i+1%>
                        </td>
                         <td class="td" nowrap align=center>
                          <%if(isCanChange){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <td class="td" nowrap>
                        <%if(!isCanChange)out.print("<input type='text' value='"+checkItemBean.getLookupName(face.get("checkitemid"))+"' style='width:110' class='ednone' readonly>");
                           else {%>
                          <pc:select name='<%="checkitemid_face_"+i%>' addNull="1" style="width:110" value='<%=face.get("checkitemid")%>'>
                           <%=checkItemBean.getList()%>
                           </pc:select>
                           <%}%>
                        </td>
                        <td class="td" nowrap><%String value=face.get("check_verdict").equals("1")?"合格":"不合格";%>
                        <%if(!isCanChange)out.print("<input type='text' value='"+value+"' style='width:110' class='ednone' readonly>");
                         else {%>
                          <pc:select name='<%="result_"+i%>' addNull="1" style="width:110" value='<%=face.get("result")%>'>
                           <pc:option value="0">不合格</pc:option>
                           <pc:option value="1">合格</pc:option>
                           </pc:select> <%}%>
                          </script>
                        </td>
                        <td class="td" nowrap>
                        <input type="text"  <%=readonly%> class=<%=isCanChange?"edbox":"ednone"%> style="width:100%" name="remark_<%=i%>" value='<%=face.get("remark")%>'>
                        </td>
                       </tr>
                      <%
                      }
                      for(; i < 5; i++){
                      %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                      </tr>
                      <%}%>
                      </table>
                    </div>
                    <script language="javascript">initDefaultTableRow('tableview2',1);</script></td>
                   </td>
                </tr>
                <tr>
                  <td width="46" class="td" > <p align="center"><strong>接头:</strong></td>
                  <td class="td" colspan="7">
                    <textarea <%=readonly%> name="tie_in" rows="4" onKeyDown="return getNextElement();" style="width: 690" cols="20"><%=masterRow.get("tie_in")%></textarea>
                  </td>
                </tr>
                <tr>
                  <td width="46" class="td" > <p align="center"><strong>备注:</strong></td>
                  <td class="td" colspan="7">
                    <textarea <%=readonly%> name="memo" rows="4" onKeyDown="return getNextElement();" style="width:690" cols="20"><%=masterRow.get("memo")%></textarea>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
          <tr>
            <td class="td"><b>制单日期:</b><%=masterRow.get("createdate")%>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("creator")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
             <%if(isCanChange){%>
              <input name="btnback" class="button" type="button" style="width:150" value="引入自制收货单货物(W)" onClick="selctbilloflading()" border="0">
              <input type="hidden" name="selectedtdid" value=''  >
              <pc:shortcut key="w" script='selctbilloflading();'/>
              <%}%>
              <%if(isCanChange){%><input name="button2"  style="width:80" type="button" class="button" onClick="if(!add_Check()){return};sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存新增(N)">
              <pc:shortcut key="n" script='<%="if(!add_Check()){return};sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/><%}%>
              <%if(isCanChange){%><input name="btnback" type="button" class="button" onClick="if(!add_Check()){return};sumitForm(<%=Operate.POST%>);" style="width:90" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="if(!add_Check()){return};sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
              <%if(isCanDelete&&(state.equals("0")||state.equals(""))&&!productBean.masterIsAdd()){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
                <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%if(!productBean.isApprove && !productBean.isReport){%><input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
              <pc:shortcut key="c" script='backList();'/><%}%>
               <%if(!productBean.masterIsAdd()){%>
                <input type="button"  style="width:60" class="button" value="打印(P)" onclick="location.href='../pub/pdfprint.jsp?code=product_film_check_bill&operate=<%=Operate.PRINT_BILL%>&a$productcheckid=<%=masterRow.get("productcheckid")%>&src=../quality/product_film_check_edit.jsp'">
              <pc:shortcut key="p" script='print()'/> </td>
              <%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
  function delMaster(){
  if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);
    }
 function print(){
  location.href='../pub/pdfprint.jsp?code=product_film_check_bill&operate=<%=Operate.PRINT_BILL%>&a$productcheckid=<%=masterRow.get("productcheckid")%>&src=../quality/product_film_check_edit.jsp'
 }
  function selctbilloflading()
  {
      //OrderSingleSelect('form1','srcVar=selectedtdid&srcVar=productno','fieldVar=receivedetailid&fieldVar=ph','',"sumitForm(<%=productBean.PRODUCT_ADD%>,-1)",'4');
      OrderSingleSelect('form1','srcVar=receivedetailid&srcVar=cpid&srcVar=dmsxid&srcVar=selectedtdid&srcVar=productno','fieldVar=receivedetailid&fieldVar=cpid&fieldVar=dmsxid&fieldVar=receivedetailid&fieldVar=ph','',"sumitForm(<%=productBean.PRODUCT_ADD%>,-1)",'4');
  }
  function OrderSingleSelect(frmName,srcVar,fieldVar,curID,methodName,checktypeid)
  {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=750 height=450  top=0 left=0";
      var winName= "SingleladingSelector";
      paraStr = "receive_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&curID="+curID+"&checktypeid="+checktypeid;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(checktypeid+'' != 'undefined')
        paraStr += "&checktypeid="+checktypeid;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
}
      initDefaultTableRow('tableview1',1);
      INFO_EX = new TabControl('INFO_EX',0);
      AddTabItem(INFO_EX,'INFO_EX_0','tabDivINFO_EX_0','cntDivINFO_EX_0');
      AddTabItem(INFO_EX,'INFO_EX_1','tabDivINFO_EX_1','cntDivINFO_EX_1');
      if (window.top.StatFrame+''!='undefined'){ var tmp_curtab=window.top.StatFrame.GetRegisterVar('INFO_EX');if (tmp_curtab!='') {SetActiveTab(INFO_EX,tmp_curtab);}}
  </SCRIPT>
<%if(productBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
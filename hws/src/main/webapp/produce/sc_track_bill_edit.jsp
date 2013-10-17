<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.produce.B_SC_TrackBill b_TrackbillBean = engine.erp.produce.B_SC_TrackBill.getInstance(request);
  String pageCode = "sc_track_bill";
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
function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function masterproductCodeSelect(obj)
  {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=pm&srcVar=scdwgs','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=scdwgs',obj.value);
  }
  function masterproductNameSelect(obj)
  {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=pm&srcVar=scdwgs','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=scdwgs',obj.value);
  }
function productCodeSelect(obj, i)
  {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=pm_'+i+'&srcVar=scdwgs_'+i+'&srcVar=hsbl_'+i,
                      'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=scdwgs&filedVar=hsbl', obj.value);
  }
  function productNameSelect(obj,i)
  {
    ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=pm_'+i+'&srcVar=scdwgs_'+i+'&srcVar=hsbl_'+i,
                      'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=scdwgs&filedVar=hsbl', obj.value);
  }
  function propertyNameSelect(obj,cpid,i)
  {
    PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=guige_'+i,
                         'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
   }
   function masterpropertyNameSelect(obj,cpid)
 {
   PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid&srcVar=guige',
                        'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
   }
   function corpCodeSelect(obj)
{
  ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpNameSelect(obj)
{
  ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
   function ProdSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
{
  paraStr = "../pub/productselect.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+curID;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  openSelectUrl(paraStr, "SingleProdSelector", winopt2)
}
function backList()
{
  location.href='sc_track_bill.jsp';
}
function getid(i)
{

  form1.action='dispedit.jsp';
  form1.rownum.value = i;
  form1.operate.value =<%=b_TrackbillBean.GETID%>;
  form1.submit();
}
function leixingchange(){
   sumitForm(<%=b_TrackbillBean.PANDUAN%>,-1)
}
function getcount(){
   sumitForm(<%=b_TrackbillBean.COUNT%>,-1)
   }
   function ProdSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
{
  paraStr = "../pub/productselect.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+curID;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  openSelectUrl(paraStr, "SingleProdSelector", winopt2)
}
function customerCodeSelect(obj)
{
    CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户名称
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
function showFixedQuery(){
  //点击查询按钮打开查询对话框
   showFrame('fixedQuery', true, "", true);//显示层
}
</script>

<%

  String retu = b_TrackbillBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp guigeBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp dwtxBean =  engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp trackBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCE_TRACK_TYPE);
  engine.project.LookUp gongyiBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_TECHNICS_NAME);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_LIST);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_TrackbillBean.getMaterTable();
  EngineDataSet list = b_TrackbillBean.getdetailTable();//引用过来的数据集
  EngineDataSet list_detail =b_TrackbillBean.getdetail_de_Table();
  dwtxBean.regData(ds,"dwtxid");
  storeBean.regData(ds,"storeid");
  prodBean.regData(list,"cpid");
  guigeBean.regData(list,"dmsxid");
  deptBean.regData(list,"deptid");
  RowMap masterRow = b_TrackbillBean.getMasterRowinfo();//主表一行
  RowMap[] detailRows= b_TrackbillBean.getDeRowinfos();//从表多行

  RowMap corpRow = null;
  RowMap prodrow = null;
  RowMap masterprodrow= null;


  //boolean isEnd =  b_SaleInvoiceBean.isReport||b_SaleInvoiceBean.isApprove || (!b_SaleInvoiceBean.masterIsAdd() && !zt.equals("0"));
  //没有结束,在修改状态,并有删除权限
  boolean isCanDelete =  !b_TrackbillBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);
  boolean isCanEdit = loginBean.hasLimits(pageCode, op_edit);
  String edClass = isCanEdit ? "class=edbox" :"class=edline";
  String detailClass = isCanEdit ?  "class=edFocused" :  "class=edline";
  String detailClass_r = isCanEdit ? "class=edFocused_r" : "class=ednone_r";
  String readonly = isCanEdit ? "" : " readonly";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  boolean count=list.getRowCount()==0?true:false;
%>
  <BODY oncontextmenu="window.event.returnValue=true" >
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0>
    <tr>
      <td align="center" height="5"></td>
    </tr>
  </table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="940">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
          <tr>
            <td class="activeVTab">生产跟踪卡
              <%if(isCanEdit){%>
              <img style='cursor:hand'  title="引入领料单(W)"  src='../images/view.gif' border=0 onClick="selctbilloflading()">
              <pc:shortcut key="w" script="selctbilloflading()" />
              <%}%>
            </td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td> <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0" width="100%">
                <tr>
                <td  noWrap class="tdTitle">跟踪单类型</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <pc:select name="track_type_ID" addNull="1" style="width:110" onSelect="leixingchange()"  >
                    <%=trackBean.getList(masterRow.get("track_type_ID"))%> </pc:select>

                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">生产跟踪卡号</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="track_bill_no" value='<%=masterRow.get("track_bill_no")%>' maxlength='<%=ds.getColumn("track_bill_no").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="track_bill_no" value='<%=masterRow.get("track_bill_no")%>' maxlength='<%=ds.getColumn("track_bill_no").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">供应商
                    <%corpRow = dwtxBean.getLookupRow(masterRow.get("dwtxid"));%>
                  </td>
                  <td  noWrap colspan='3' class="td"> <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <%if(isCanEdit){%>
                    <input type="text" <%=detailClass_r%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="corpCodeSelect(this)" >
                    <%}%>
                    <input type="text" <%=detailClass_r%> name="dwmc" value='<%=dwtxBean.getLookupName(masterRow.get("dwtxid"))%>'  onKeyDown="return getNextElement();"  style="width:200"  onchange="corpNameSelect(this)" >
                    <%if(isCanEdit){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value);">
                    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwdm.value='';dwmc.value='';">
                    <%}%>
                  </td>

                </tr>
                <tr>
                  <td height='20' class="tdTitle" nowrap>品名规格
                    <%masterprodrow = prodBean.getLookupRow(masterRow.get("cpid"));%>
                  </td>
                  <td height='20' colspan="3" nowrap class="td"><table BORDER="0" CELLPADDING="1" CELLSPACING="0"><tr>
                    <td nowrap class="td"><input type="hidden" name="scdwgs" value='<%=masterprodrow.get("scdwgs")%>' ><input type="hidden" id="cpid" name="cpid" value='<%=masterRow.get("cpid")%>' >
                    <input type="text" <%=edClass%> style="width:65" onKeyDown="return getNextElement();" name="cpbm" value='<%=masterprodrow.get("cpbm")%>' onchange="masterproductCodeSelect(this);countPlanWeight(-1)" >
                    <input type="text" <%=edClass%> style="width:180" id="pm" name="pm"  value='<%=masterprodrow.get("product")%>'  onchange="masterproductNameSelect(this);countPlanWeight(-1)">
                    </td>
                    <%if(isCanEdit){%>
                    <td  nowrap class="td"><img id='linliaopm_addimg' style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=cpbm&srcVar=pm&srcVar=scdwgs','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=scdwgs',form1.cpid.value);countPlanWeight(-1);"></td>
                    <td  nowrap class="td"><img id='linliaopm_delimg' style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cpbm.value='';pm.value='';"></td>
                    <%}%></tr></table>
                  </td>
                  <td height='20' nowrap class="tdTitle">规格属性</td>
                  <td height='20' colspan="3" nowrap class="td"><table BORDER="0" CELLPADDING="1" CELLSPACING="0"><tr>
                  <td nowrap class="td"><input type="text" <%=edClass%> id="guige" name="guige" value='<%=guigeBean.getLookupName(masterRow.get("dmsxID"))%>' style="width:280" onchange="if(form1.cpid.value==''){alert('请先输入产品');return;}masterpropertyNameSelect(this,form1.cpid.value);countPlanWeight(-1)" onKeyDown="return getNextElement();" <%=readonly%>>
                  <input type="hidden" id="dmsxid" name="dmsxid" value="<%=masterRow.get("dmsxid")%>">
                  </td>
                    <%if(isCanEdit){%>

                     <td  nowrap class="td"><img id='linliaoguige_addimg' style='cursor:hand' src='../images/view.gif' border=0
onClick="if(form1.cpid.value==''){alert('请先输入产品');return;}if('<%=masterRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}
      PropertySelect('form1','dmsxid','guige',form1.cpid.value);countPlanWeight(-1)"> </td>
                      <td  nowrap class="td"><img id='linliaoguige_delimg' style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid.value='';guige.value='';"></td>
                    <%}%></tr></table>
                  </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle">仓库</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <pc:select name="storeid" addNull="1" style="width:110"   >
                    <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                    <%}else{%>
                    <input type="text" name="storeid" value='<%=storeBean.getLookupName(masterRow.get("storeid"))%>' maxlength='<%=ds.getColumn("storeid").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" readonly>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">检验员</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="checker" value='<%=masterRow.get("checker")%>' maxlength='<%=ds.getColumn("checker").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="checker" value='<%=masterRow.get("checker")%>' maxlength='<%=ds.getColumn("checker").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                     <td  noWrap class="tdTitle">等级</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="grade" value='<%=masterRow.get("grade")%>' maxlength='<%=ds.getColumn("grade").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="grade" value='<%=masterRow.get("grade")%>' maxlength='<%=ds.getColumn("grade").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>


                </tr>
                <tr>
                  <td  noWrap class="tdTitle">毛重</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="gross_weight" value='<%=masterRow.get("gross_weight")%>' maxlength='<%=ds.getColumn("gross_weight").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="gross_weight" value='<%=masterRow.get("gross_weight")%>' maxlength='<%=ds.getColumn("gross_weight").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">净重</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="net_weight" value='<%=masterRow.get("net_weight")%>' maxlength='<%=ds.getColumn("net_weight").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="net_weight" value='<%=masterRow.get("net_weight")%>' maxlength='<%=ds.getColumn("net_weight").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">检验结果</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="check_result" value='<%=masterRow.get("check_result")%>' maxlength='<%=ds.getColumn("check_result").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="check_result" value='<%=masterRow.get("check_result")%>' maxlength='<%=ds.getColumn("check_result").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">经手人</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="handler" value='<%=masterRow.get("handler")%>' maxlength='<%=ds.getColumn("handler").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="handler" value='<%=masterRow.get("handler")%>' maxlength='<%=ds.getColumn("handler").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                </tr>
                <%if(!masterRow.get("type_prop").equals("2")){%>
                <tr>
                  <td  noWrap class="tdTitle">达因面</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="dyne_side" value='<%=masterRow.get("dyne_side")%>' maxlength='<%=ds.getColumn("dyne_side").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="dyne_side" value='<%=masterRow.get("dyne_side")%>' maxlength='<%=ds.getColumn("dyne_side").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">热封面</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="hot_side" value='<%=masterRow.get("hot_side")%>' maxlength='<%=ds.getColumn("hot_side").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="hot_side" value='<%=masterRow.get("hot_side")%>' maxlength='<%=ds.getColumn("hot_side").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>

                </tr>
                <%}%>
                <tr>
                  <td  noWrap class="tdTitle">制单人</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="creator" value='<%=masterRow.get("creator")%>' maxlength='<%=ds.getColumn("creator").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="creator" value='<%=masterRow.get("creator")%>' maxlength='<%=ds.getColumn("creator").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">审核人</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="approver" value='<%=masterRow.get("approver")%>' maxlength='<%=ds.getColumn("approver").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="approver" value='<%=masterRow.get("approver")%>' maxlength='<%=ds.getColumn("approver").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">原料批号</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="material_no" value='<%=masterRow.get("material_no")%>' maxlength='<%=ds.getColumn("material_no").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="material_no" value='<%=masterRow.get("material_no")%>' maxlength='<%=ds.getColumn("material_no").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">总损耗
                    <input type="hidden" name="selectedtdid" value=""></td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="tot_ull" value='<%=masterRow.get("tot_ull")%>' maxlength='<%=ds.getColumn("tot_ull").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="tot_ull" value='<%=masterRow.get("tot_ull")%>' maxlength='<%=ds.getColumn("tot_ull").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <%if(!masterRow.get("type_prop").equals("1")){%>
                  <td  noWrap class="tdTitle">正品率</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="good_ratio" value='<%=masterRow.get("good_ratio")%>' maxlength='<%=ds.getColumn("good_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="good_ratio" value='<%=masterRow.get("good_ratio")%>' maxlength='<%=ds.getColumn("good_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">副品率</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="hypo_ratio" value='<%=masterRow.get("hypo_ratio")%>' maxlength='<%=ds.getColumn("hypo_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="hypo_ratio" value='<%=masterRow.get("hypo_ratio")%>' maxlength='<%=ds.getColumn("hypo_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">废品率</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="waster_ratio" value='<%=masterRow.get("waster_ratio")%>' maxlength='<%=ds.getColumn("waster_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="waster_ratio" value='<%=masterRow.get("waster_ratio")%>' maxlength='<%=ds.getColumn("waster_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <%}%>
                  <td  noWrap class="tdTitle">总成品率</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="tot_ratio" value='<%=masterRow.get("tot_ratio")%>' maxlength='<%=ds.getColumn("tot_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="tot_ratio" value='<%=masterRow.get("tot_ratio")%>' maxlength='<%=ds.getColumn("tot_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                    <input type="hidden" name="type_prop" value='<%=masterRow.get("type_prop")%>' ></td>
                </tr>
                <%
              RowMap detailrow =null;
              gongyiBean.regData(list, "gymcID");
              prodBean.regData(list,"cpId");
              guigeBean.regData(list,"dmsxID");
              list.first();
              for(int i=0;i<detailRows.length;i++){
              detailrow = detailRows[i];
              String track_type = detailrow.get("track_type");
              boolean isPutong  = track_type.length() == 0 || track_type.equals("0");
              boolean isDitu    = track_type.equals("1");
              boolean isFenqie  = track_type.equals("2");
              boolean isHengqie = track_type.equals("3");
                 %>

                <%if(isFenqie){%>
                <%--分切工序--%>
                <tr>
                  <td colspan="8" noWrap class="td"> <table cellspacing=0 width="100%" cellpadding=0>
                      <tr>
                        <td nowrap class="activeVTab" colspan="8"> <%=gongyiBean.getLookupName(detailrow.get("gymcID"))%>
                          <img style='cursor:hand'  title="查看分切信息"  src='../images/select_prod.gif' border=0 onClick="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格');return;}getid(<%=i%>);">
                          <%if(isCanEdit){%>
                          <img style='cursor:hand' src='../images/delete.gif' BORDER=0 title="清空该分切内部所有信息" onClick="if(confirm('是否删除所有该分切内部的记录？')) sumitForm(<%=b_TrackbillBean.All_Disp_DEL%>,<%=i%>)" >
                          <img style='cursor:hand'  title="引入工序信息"  src='../images/view.gif' border=0 onClick="dispart('<%=gongyiBean.getLookupName(detailrow.get("gymcID"))%>',<%=i%>)">
                          <%}%>
                        </td>
                        <td class="lastTab" valign=bottom width=100% align=right></td>
                      </tr>
                    </table>
                    <%--context--%>
                    <%prodrow = prodBean.getLookupRow(detailrow.get("cpid"));%>
                    <table id="tableview_fq_<%=i%>" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr >
                        <td class="tdTitle" nowrap>生产日期</td>
                        <td height='20' class="td" nowrap><input type="text" name="prod_date_<%=i%>"  value='<%=detailrow.get("prod_date")%>' maxlength='10' style="width:65" <%=detailClass_r%> onChange="checkDate(this)" onKeyDown="return getNextElement();" >
                          <%if(isCanEdit){%>
                          <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(prod_date_<%=i%>);"></a>
                          <%}%>
                        </td>
                        <td height='20' class="tdTitle" nowrap>品名规格</td>  <td height='20' colspan="3" nowrap class="td"><table BORDER="0" CELLPADDING="1" CELLSPACING="0"><tr>
                        <td nowrap class="td"><input type="hidden" name="hsbl_<%=i%>" value='<%=prodrow.get("hsbl")%>' ><input type="hidden" name="scdwgs_<%=i%>" value='<%=prodrow.get("scdwgs")%>' ><input type="hidden" id="cpid_<%=i%>" name="cpid_<%=i%>" value='<%=detailrow.get("cpid")%>' >
                          <input type="text" <%=edClass%> style="width:65" onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodrow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>);countPlanWeight(<%=i%>)" >
                          <input type="text" <%=edClass%> style="width:180" id="pm_<%=i%>" name="pm_<%=i%>"  value='<%=prodrow.get("product")%>'  onchange="productNameSelect(this,<%=i%>);countPlanWeight(<%=i%>)">
                          </td>
                          <%if(isCanEdit){%>
                          <td  nowrap class="td"><td height='20' colspan="3" nowrap class="td"><img id="pm_addimg_<%=i%>" style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=pm_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=hsbl_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=scdwgs&fieldVar=hsbl',form1.cpid_<%=i%>.value);countPlanWeight(<%=i%>);"> </td>
                          <td  nowrap class="td"><td height='20' colspan="3" nowrap class="td"><img id="pm_delimg_<%=i%>" style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid_<%=i%>.value='';cpbm_<%=i%>.value='';pm_<%=i%>.value='';scdwgs_<%=i%>.value='';hsbl_<%=i%>.value=''"> </td>
                          <%}%></tr></table>
                        </td>
                        <td height='20' nowrap class="tdTitle">规格属性</td> <td height='20' colspan="3" nowrap class="td"><table BORDER="0" CELLPADDING="1" CELLSPACING="0"><tr>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> id="guige_<%=i%>" name="guige_<%=i%>" value='<%=guigeBean.getLookupName(detailrow.get("dmsxID"))%>' style="width:280" onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>);countPlanWeight(<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>>
                          <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detailrow.get("dmsxid")%>">
                          </td>
                          <%if(isCanEdit){%>
                          <td  nowrap class="td"><img id="guige_addimg_<%=i%>" style='cursor:hand' src='../images/view.gif' border=0
onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=detailrow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}
      PropertySelect('form1','dmsxid_<%=i%>','guige_<%=i%>',form1.cpid_<%=i%>.value);countPlanWeight(<%=i%>)"> </td>
                         <td  nowrap class="td"><img  id="guige_delimg_<%=i%>" style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';guige_<%=i%>.value='';"> </td>
                          <%}%></tr></table>
                        </td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>质量验证</td>
                        <td colspan="3" nowrap class="td">

                        <input type="hidden" name="length2_<%=i%>" value='<%=detailrow.get("length2")%>' >
                        <input type="hidden" name="weight2_<%=i%>" value='' >
                        <input type="hidden" name="length_<%=i%>" value='' >
                        <input type="hidden" name="weight_<%=i%>" value='<%=b_TrackbillBean.all_disp_weight%>' >

                        <input type="hidden" name="return_num_<%=i%>" value='' >
                        <input type="text" <%=detailClass_r%>  style="width:280" onKeyDown="return getNextElement();" id="qual_validate_<%=i%>"  name="qual_validate_<%=i%>"     value='<%=detailrow.get("qual_validate")%>'  maxlength='<%=list.getColumn("qual_validate").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">原断纸数</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="old_break_<%=i%>"  name="old_break_<%=i%>"     value='<%=detailrow.get("old_break")%>'   maxlength='<%=list.getColumn("old_break").getPrecision()%>'></td>
                        <td nowrap class="tdTitle">断纸次数</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="new_break_<%=i%>"  name="new_break_<%=i%>"     value='<%=detailrow.get("new_break")%>'   maxlength='<%=list.getColumn("new_break").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>端面不齐(mm)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="end_side_<%=i%>"  name="end_side_<%=i%>"     value='<%=detailrow.get("end_side")%>'   maxlength='<%=list.getColumn("end_side").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>计划损耗(m)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_length_<%=i%>"  name="plan_length_<%=i%>"   value='<%=detailrow.get("plan_length")%>'  onchange="countPlanWeight(<%=i%>)"   maxlength='<%=list.getColumn("plan_length").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">计划损耗(kg)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_weigth_<%=i%>"  name="plan_weigth_<%=i%>"   value='<%=detailrow.get("plan_weigth")%>'    maxlength='<%=list.getColumn("plan_weigth").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(m)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_length_<%=i%>"  name="fact_length_<%=i%>"     value='<%=detailrow.get("fact_length")%>'  maxlength='<%=list.getColumn("fact_length").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(kg)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_weight_<%=i%>"  name="fact_weight_<%=i%>"    value='<%=detailrow.get("fact_weight")%>'   maxlength='<%=list.getColumn("fact_weight").getPrecision()%>'  >
                         <img style='cursor:hand'  title="计算实际损耗"  src='../images/nextbill.gif' border=0 onClick="countprod_ratio(<%=i%>);">
                         <img style='cursor:hand'  title="清空实际损耗"  src='../images/delete.gif' border=0 onClick="del_prod_ratio(<%=i%>);"></td>
                        <td class="tdTitle" nowrap>奖罚(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="award_<%=i%>"  name="award_<%=i%>"     value='<%=detailrow.get("award")%>'   maxlength='<%=list.getColumn("award").getPrecision()%>'  >
                             <img style='cursor:hand'  title="计算奖罚"  src='../images/nextbill.gif' border=0 onClick="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格');return;}countaward(<%=i%>);"></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>损耗原因</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="ull_reason_<%=i%>"  name="ull_reason_<%=i%>"     value='<%=detailrow.get("ull_reason")%>'   maxlength='<%=list.getColumn("ull_reason").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">质量说明</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="qual_desc_<%=i%>"  name="qual_desc_<%=i%>"     value='<%=detailrow.get("qual_desc")%>'   maxlength='<%=list.getColumn("qual_desc").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">批号</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="prod_batno_<%=i%>"  name="prod_batno_<%=i%>"     value='<%=detailrow.get("prod_batno")%>'   maxlength='<%=list.getColumn("prod_batno").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">工序成品率</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="prod_ratio_<%=i%>"  name="prod_ratio_<%=i%>"     value='<%=detailrow.get("prod_ratio")%>'   maxlength='<%=list.getColumn("prod_ratio").getPrecision()%>'  >
                          <img style='cursor:hand'  title="计算成品率"  src='../images/nextbill.gif' border=0 onClick="countprod_ratio(<%=i%>);">
                          <img style='cursor:hand'  title="清空成品率"  src='../images/delete.gif' border=0 onClick="del_prod_ratio(<%=i%>);"></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>班次</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="team_no_<%=i%>"  name="team_no_<%=i%>"     value='<%=detailrow.get("team_no")%>'   maxlength='<%=list.getColumn("team_no").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>机台</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="machine_no_<%=i%>"  name="machine_no_<%=i%>"     value='<%=detailrow.get("machine_no")%>'   maxlength='<%=list.getColumn("machine_no").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>经手人</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="handler_<%=i%>"  name="handler_<%=i%>"     value='<%=detailrow.get("handler")%>'   maxlength='<%=list.getColumn("handler").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>制单人</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="creator_<%=i%>"  name="creator_<%=i%>"     value='<%=detailrow.get("creator")%>'   maxlength='<%=list.getColumn("creator").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>审核人</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="approver_<%=i%>"  name="approver_<%=i%>"     value='<%=detailrow.get("approver")%>'   maxlength='<%=list.getColumn("approver").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap align="right">横向序号</td>
                        <td class="tdTitle" nowrap align="right">1</td>
                        <td class="tdTitle" nowrap align="right">2</td>
                        <td class="tdTitle" nowrap align="right">3</td>
                        <td class="tdTitle" nowrap align="right">4</td>
                        <td class="tdTitle" nowrap align="right">5</td>
                        <td class="tdTitle" nowrap align="right">6</td>
                        <td class="tdTitle" nowrap align="right">7</td>
                        <td class="tdTitle" nowrap align="right">8</td>
                        <td class="tdTitle" nowrap align="right">9</td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap align="right">分切宽度</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width1_<%=i%>"  name="disp_width1_<%=i%>"     value='<%=detailrow.get("disp_width1")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width2_<%=i%>"  name="disp_width2_<%=i%>"     value='<%=detailrow.get("disp_width2")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width3_<%=i%>"  name="disp_width3_<%=i%>"     value='<%=detailrow.get("disp_width3")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width4_<%=i%>"  name="disp_width4_<%=i%>"     value='<%=detailrow.get("disp_width4")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width5_<%=i%>"  name="disp_width5_<%=i%>"     value='<%=detailrow.get("disp_width5")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width6_<%=i%>"  name="disp_width6_<%=i%>"     value='<%=detailrow.get("disp_width6")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width7_<%=i%>"  name="disp_width7_<%=i%>"     value='<%=detailrow.get("disp_width7")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width8_<%=i%>"  name="disp_width8_<%=i%>"     value='<%=detailrow.get("disp_width8")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width9_<%=i%>"  name="disp_width9_<%=i%>"     value='<%=detailrow.get("disp_width9")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                      </tr>
                    </table>
                    <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview_fq_<%=i%>',0);</SCRIPT>
                  </td>
                </tr>
                <% }%>
                <%if(isPutong){%>
                <%--普通工序--%>
                <tr>
                  <td colspan="8" noWrap class="td"> <table cellspacing=0 width="100%" cellpadding=0>
                      <tr>
                        <td nowrap class="activeVTab"> <%=gongyiBean.getLookupName(detailrow.get("gymcID"))%>
                          <%if(isCanEdit){%>
                          <img style='cursor:hand'  title="引入工序信息"  src='../images/view.gif' border=0 onClick="dispart('<%=gongyiBean.getLookupName(detailrow.get("gymcID"))%>',<%=i%>)">
                          <%}%>
                        </td>
                        <td class="lastTab" valign=bottom width=100% align=right></td>
                      </tr>
                    </table>
                    <%--context--%>
                    <%prodrow = prodBean.getLookupRow(detailrow.get("cpid"));%>
                    <table id="tableview_pt_<%=i%>" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class=tabletitle>
                        <td class="tdTitle" nowrap>生产日期</td>
                        <td height='20' class="td" nowrap><input type="text" name="prod_date_<%=i%>"  value='<%=detailrow.get("prod_date")%>' maxlength='10' style="width:65" <%=detailClass_r%> onChange="checkDate(this)" onKeyDown="return getNextElement();" >
                          <%if(isCanEdit){%>
                          <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(prod_date_<%=i%>);"></a>
                          <%}%>
                        </td>
                        <td height='20' class="tdTitle" nowrap>品名规格</td> <td height='20' colspan="3" nowrap class="td"><table BORDER="0" CELLPADDING="1" CELLSPACING="0"><tr>
                    <td nowrap class="td"><input type="hidden" name="truebl_<%=i%>" value='' ><input type="hidden" name="hsbl_<%=i%>" value='<%=prodrow.get("hsbl")%>' ><input type="hidden" name="scdwgs_<%=i%>" value='<%=prodrow.get("scdwgs")%>' ><input type="hidden" id="cpid_<%=i%>" name="cpid_<%=i%>" value='<%=detailrow.get("cpid")%>' >
                          <input type="text" <%=edClass%> style="width:65" onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodrow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>);countPlanWeight(<%=i%>)" >
                          <input type="text" <%=edClass%> style="width:180" id="pm_<%=i%>" name="pm_<%=i%>"  value='<%=prodrow.get("product")%>'  onchange="productNameSelect(this,<%=i%>);countPlanWeight(<%=i%>)">
                          </td>
                          <%if(isCanEdit){%>
                          <td  nowrap class="td"><td height='20' colspan="3" nowrap class="td"><img id="pm_addimg_<%=i%>" style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=pm_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=hsbl_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=scdwgs&fieldVar=hsbl',form1.cpid_<%=i%>.value);countPlanWeight(<%=i%>);"> </td>
                          <td  nowrap class="td"><td height='20' colspan="3" nowrap class="td"><img id="pm_delimg_<%=i%>" style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid_<%=i%>.value='';cpbm_<%=i%>.value='';pm_<%=i%>.value='';scdwgs_<%=i%>.value='';hsbl_<%=i%>.value=''"> </td>
                          <%}%></tr></table>
                        </td>
                        <td height='20' nowrap class="tdTitle">规格属性</td> <td height='20' colspan="3" nowrap class="td"><table BORDER="0" CELLPADDING="1" CELLSPACING="0"><tr>
                    <td nowrap class="td"><input type="text" <%=detailClass_r%> id="guige_<%=i%>" name="guige_<%=i%>" value='<%=guigeBean.getLookupName(detailrow.get("dmsxID"))%>' style="width:280" onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>);countPlanWeight(<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>>
                          <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detailrow.get("dmsxid")%>">
                          </td>
                          <%if(isCanEdit){%>
                          <td  nowrap class="td"><img id="guige_addimg_<%=i%>" style='cursor:hand' src='../images/view.gif' border=0
onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=detailrow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}
      PropertySelect('form1','dmsxid_<%=i%>','guige_<%=i%>',form1.cpid_<%=i%>.value);countPlanWeight(<%=i%>)"> </td>
                         <td  nowrap class="td"><img  id="guige_delimg_<%=i%>" style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';guige_<%=i%>.value='';"> </td>
                          <%}%></tr></table>
                        </td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>质量验证</td>
                        <td colspan="3" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:280" onKeyDown="return getNextElement();" id="qual_validate_<%=i%>"  name="qual_validate_<%=i%>"     value='<%=detailrow.get("qual_validate")%>'  maxlength='<%=list.getColumn("qual_validate").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">原断纸数</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="old_break_<%=i%>"  name="old_break_<%=i%>"     value='<%=detailrow.get("old_break")%>'   maxlength='<%=list.getColumn("old_break").getPrecision()%>'></td>
                        <td nowrap class="tdTitle">断纸次数</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="new_break_<%=i%>"  name="new_break_<%=i%>"     value='<%=detailrow.get("new_break")%>'   maxlength='<%=list.getColumn("new_break").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>端面不齐(mm)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="end_side_<%=i%>"  name="end_side_<%=i%>"     value='<%=detailrow.get("end_side")%>'   maxlength='<%=list.getColumn("end_side").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>计划损耗(m)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_length_<%=i%>"  name="plan_length_<%=i%>"   value='<%=detailrow.get("plan_length")%>'  onchange="countPlanWeight(<%=i%>)"  maxlength='<%=list.getColumn("plan_length").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">计划损耗(kg)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_weigth_<%=i%>"  name="plan_weigth_<%=i%>"   value='<%=detailrow.get("plan_weigth")%>'  maxlength='<%=list.getColumn("plan_weigth").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(m)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_length_<%=i%>"  name="fact_length_<%=i%>"     value='<%=detailrow.get("fact_length")%>'  maxlength='<%=list.getColumn("fact_length").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(kg)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_weight_<%=i%>"  name="fact_weight_<%=i%>"     value='<%=detailrow.get("fact_weight")%>'   maxlength='<%=list.getColumn("fact_weight").getPrecision()%>'  >
                        <img style='cursor:hand'  title="计算实际损耗"  src='../images/nextbill.gif' border=0 onClick="countprod_ratio(<%=i%>);">
                        <img style='cursor:hand'  title="清空实际损耗"  src='../images/delete.gif' border=0 onClick="del_prod_ratio(<%=i%>);"></td>
                        <td class="tdTitle" nowrap>奖罚(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="award_<%=i%>"  name="award_<%=i%>"     value='<%=detailrow.get("award")%>'   maxlength='<%=list.getColumn("award").getPrecision()%>'  >
                             <img style='cursor:hand'  title="计算奖罚"  src='../images/nextbill.gif' border=0 onClick="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格');return;}countaward(<%=i%>);"></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>损耗原因</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="ull_reason_<%=i%>"  name="ull_reason_<%=i%>"     value='<%=detailrow.get("ull_reason")%>'   maxlength='<%=list.getColumn("ull_reason").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">质量说明</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="qual_desc_<%=i%>"  name="qual_desc_<%=i%>"     value='<%=detailrow.get("qual_desc")%>'   maxlength='<%=list.getColumn("qual_desc").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">批号</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="prod_batno_<%=i%>"  name="prod_batno_<%=i%>"     value='<%=detailrow.get("prod_batno")%>'   maxlength='<%=list.getColumn("prod_batno").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">工序成品率</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="prod_ratio_<%=i%>"  name="prod_ratio_<%=i%>"     value='<%=detailrow.get("prod_ratio")%>'   maxlength='<%=list.getColumn("prod_ratio").getPrecision()%>'  >
                           <img style='cursor:hand'  title="计算成品率"  src='../images/nextbill.gif' border=0 onClick="countprod_ratio(<%=i%>);">
                           <img style='cursor:hand'  title="清空成品率"  src='../images/delete.gif' border=0 onClick="del_prod_ratio(<%=i%>);"></td>
                      </tr>
                      <tr>
                        <td height="19" nowrap class="tdTitle">长度(m)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="length_<%=i%>"  name="length_<%=i%>"     value='<%=detailrow.get("length")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格');return;}countWeight(<%=i%>)"  maxlength='<%=list.getColumn("length").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>重量(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="weight_<%=i%>"  name="weight_<%=i%>"     value='<%=detailrow.get("weight")%>' maxlength='<%=list.getColumn("weight").getPrecision()%>'  ></td>
                        <%if(!masterRow.get("type_prop").equals("1")){%>
                        <td class="tdTitle" nowrap>长度2(m)<br></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="length2_<%=i%>"  name="length2_<%=i%>"     value='<%=detailrow.get("length2")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格');return;}countWeight(<%=i%>)"  maxlength='<%=list.getColumn("length2").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>重量2(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="weight2_<%=i%>"  name="weight2_<%=i%>"     value='<%=detailrow.get("weight2")%>'  maxlength='<%=list.getColumn("weight2").getPrecision()%>'  ></td>
                         <%}else{%>
                           <input type="hidden" name="length2_<%=i%>" value='<%=detailrow.get("length2")%>' ><input type="hidden" name="weight2_<%=i%>" value='<%=detailrow.get("weight2")%>' >
                        <%}%>

                        <td class="tdTitle" nowrap>退料(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="return_num_<%=i%>"  name="return_num_<%=i%>"     value='<%=detailrow.get("return_num")%>'  maxlength='<%=list.getColumn("return_num").getPrecision()%>'  ></td>
                        <%if(masterRow.get("type_prop").equals("1")){%>
                        <td height="19" nowrap class="tdTitle"></td>
                        <td height="19" nowrap class="tdTitle"></td>
                        <td height="19" nowrap class="tdTitle"></td>
                        <td height="19" nowrap class="tdTitle"></td>
                        <%}%>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>班次</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="team_no_<%=i%>"  name="team_no_<%=i%>"     value='<%=detailrow.get("team_no")%>'   maxlength='<%=list.getColumn("team_no").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>机台</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="machine_no_<%=i%>"  name="machine_no_<%=i%>"     value='<%=detailrow.get("machine_no")%>'   maxlength='<%=list.getColumn("machine_no").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>经手人</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="handler_<%=i%>"  name="handler_<%=i%>"     value='<%=detailrow.get("handler")%>'   maxlength='<%=list.getColumn("handler").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>制单人</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="creator_<%=i%>"  name="creator_<%=i%>"     value='<%=detailrow.get("creator")%>'   maxlength='<%=list.getColumn("creator").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>审核人</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="approver_<%=i%>"  name="approver_<%=i%>"     value='<%=detailrow.get("approver")%>'   maxlength='<%=list.getColumn("approver").getPrecision()%>'  ></td>
                      </tr>
                    </table>
                    <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview_pt_<%=i%>',1);</SCRIPT>
                  </td>
                </tr>
                <%}%>
                <%if(isHengqie){%>
                <%--横切工序--%>
                <tr>
                  <td colspan="8" noWrap class="td"> <table cellspacing=0 width="100%" cellpadding=0>
                      <tr>
                        <td nowrap class="activeVTab"> <%=gongyiBean.getLookupName(detailrow.get("gymcID"))%>
                          <%if(isCanEdit){%>
                          <img style='cursor:hand'  title="引入工序信息"  src='../images/view.gif' border=0 onClick="dispart('<%=gongyiBean.getLookupName(detailrow.get("gymcID"))%>',<%=i%>)">
                          <%}%>
                        </td>
                        <td class="lastTab" valign=bottom width=100% align=right></td>
                      </tr>
                    </table>
                    <%--context--%>
                    <%prodrow = prodBean.getLookupRow(detailrow.get("cpid"));%>
                    <table id="tableview_hq_<%=i%>" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class=tabletitle>
                        <td class="tdTitle" nowrap>生产日期</td>
                        <td height='20' colspan="2" nowrap class="td"><input type="text" name="prod_date_<%=i%>"  value='<%=detailrow.get("prod_date")%>' maxlength='10' style="width:65" <%=detailClass_r%> onChange="checkDate(this)" onKeyDown="return getNextElement();" >
                          <%if(isCanEdit){%>
                          <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(prod_date_<%=i%>);"></a>
                          <%}%>
                        </td>
                        <td height='20' class="tdTitle" nowrap>品名规格</td> <td height='20' colspan="3" nowrap class="td"><table BORDER="0" CELLPADDING="1" CELLSPACING="0"><tr>
                    <td nowrap class="td"><input type="hidden" name="truebl_<%=i%>" value='' ><input type="hidden" name="hsbl_<%=i%>" value='<%=prodrow.get("hsbl")%>' ><input type="hidden" name="scdwgs_<%=i%>" value='<%=prodrow.get("scdwgs")%>' ><input type="hidden" id="cpid_<%=i%>" name="cpid_<%=i%>" value='<%=detailrow.get("cpid")%>' >
                          <input type="text" <%=edClass%> style="width:65" onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodrow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>);countPlanWeight(<%=i%>)" >
                          <input type="text" <%=edClass%> style="width:180" id="pm_<%=i%>" name="pm_<%=i%>"  value='<%=prodrow.get("product")%>'  onchange="productNameSelect(this,<%=i%>);countPlanWeight(<%=i%>)">
                          </td>
                          <%if(isCanEdit){%>
                          <td  nowrap class="td"><td height='20' colspan="3" nowrap class="td"><img id="pm_addimg_<%=i%>" style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=pm_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=hsbl_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=scdwgs&fieldVar=hsbl',form1.cpid_<%=i%>.value);countPlanWeight(<%=i%>);"> </td>
                          <td  nowrap class="td"><td height='20' colspan="3" nowrap class="td"><img id="pm_delimg_<%=i%>" style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid_<%=i%>.value='';cpbm_<%=i%>.value='';pm_<%=i%>.value='';scdwgs_<%=i%>.value='';hsbl_<%=i%>.value=''"> </td>
                          <%}%></tr></table>
                        </td>
                        <td height='20' nowrap class="tdTitle">规格属性</td> <td height='20' colspan="3" nowrap class="td"><table BORDER="0" CELLPADDING="1" CELLSPACING="0"><tr>
                    <td nowrap class="td"><input type="text" <%=detailClass_r%> id="guige_<%=i%>" name="guige_<%=i%>" value='<%=guigeBean.getLookupName(detailrow.get("dmsxID"))%>' style="width:280" onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>);countPlanWeight(<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>>
                          <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detailrow.get("dmsxid")%>">
                          </td>
                          <%if(isCanEdit){%>
                          <td  nowrap class="td"><img id="guige_addimg_<%=i%>" style='cursor:hand' src='../images/view.gif' border=0
onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=detailrow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}
      PropertySelect('form1','dmsxid_<%=i%>','guige_<%=i%>',form1.cpid_<%=i%>.value);countPlanWeight(<%=i%>)"> </td>
                         <td  nowrap class="td"><img  id="guige_delimg_<%=i%>" style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';guige_<%=i%>.value='';"> </td>
                          <%}%></tr></table>
                        </td>
                         <td class="td" nowrap></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>质量验证</td>
                        <td colspan="5" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="qual_validate_<%=i%>"  name="qual_validate_<%=i%>"     value='<%=detailrow.get("qual_validate")%>'  maxlength='<%=list.getColumn("qual_validate").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">原断纸数</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:50" onKeyDown="return getNextElement();" id="old_break_<%=i%>"  name="old_break_<%=i%>"     value='<%=detailrow.get("old_break")%>'   maxlength='<%=list.getColumn("old_break").getPrecision()%>'></td>
                        <td nowrap class="tdTitle">断纸次数</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="new_break_<%=i%>"  name="new_break_<%=i%>"     value='<%=detailrow.get("new_break")%>'   maxlength='<%=list.getColumn("new_break").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>端面不齐(mm)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="end_side_<%=i%>"  name="end_side_<%=i%>"     value='<%=detailrow.get("end_side")%>'   maxlength='<%=list.getColumn("end_side").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>计划损耗(m)</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_length_<%=i%>"  name="plan_length_<%=i%>"   value='<%=detailrow.get("plan_length")%>' onchange="countPlanWeight(<%=i%>)"  maxlength='<%=list.getColumn("plan_length").getPrecision()%>'  ></td>
                        <td align="right" nowrap class="tdTitle">计划损耗(kg)</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_weigth_<%=i%>"  name="plan_weigth_<%=i%>"   value='<%=detailrow.get("plan_weigth")%>'  maxlength='<%=list.getColumn("plan_weigth").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(m)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:50" onKeyDown="return getNextElement();" id="fact_length_<%=i%>"  name="fact_length_<%=i%>"     value='<%=detailrow.get("fact_length")%>'  maxlength='<%=list.getColumn("fact_length").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(kg)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_weight_<%=i%>"  name="fact_weight_<%=i%>"     value='<%=detailrow.get("fact_weight")%>'  maxlength='<%=list.getColumn("fact_weight").getPrecision()%>'  >
                         <img style='cursor:hand'  title="计算实际损耗"  src='../images/nextbill.gif' border=0 onClick="countprod_ratio(<%=i%>);">
                          <img style='cursor:hand'  title="清空实际损耗"  src='../images/delete.gif' border=0 onClick="del_prod_ratio(<%=i%>);"></td>
                        <td class="tdTitle" nowrap>奖罚(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="award_<%=i%>"  name="award_<%=i%>"     value='<%=detailrow.get("award")%>'   maxlength='<%=list.getColumn("award").getPrecision()%>'  >
                              <img style='cursor:hand'  title="计算奖罚"  src='../images/nextbill.gif' border=0 onClick="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格');return;}countaward(<%=i%>);"></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>损耗原因</td>
                        <td colspan="3" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="ull_reason_<%=i%>"  name="ull_reason_<%=i%>"     value='<%=detailrow.get("ull_reason")%>'   maxlength='<%=list.getColumn("ull_reason").getPrecision()%>'  ></td>
                        <td colspan="2" nowrap class="tdTitle">质量说明</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:150" onKeyDown="return getNextElement();" id="qual_desc_<%=i%>"  name="qual_desc_<%=i%>"     value='<%=detailrow.get("qual_desc")%>'   maxlength='<%=list.getColumn("qual_desc").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">批号</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="prod_batno_<%=i%>"  name="prod_batno_<%=i%>"     value='<%=detailrow.get("prod_batno")%>'   maxlength='<%=list.getColumn("prod_batno").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">工序成品率</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="prod_ratio_<%=i%>"  name="prod_ratio_<%=i%>"     value='<%=detailrow.get("prod_ratio")%>'   maxlength='<%=list.getColumn("prod_ratio").getPrecision()%>'  >
                        <img style='cursor:hand'  title="计算成品率"  src='../images/nextbill.gif' border=0 onClick="countprod_ratio(<%=i%>);">
                        <img style='cursor:hand'  title="清空成品率"  src='../images/delete.gif' border=0 onClick="del_prod_ratio(<%=i%>);"></td>
                      </tr>
                      <tr>
                        <td height="19" nowrap class="tdTitle">长度(m)</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="length_<%=i%>"  name="length_<%=i%>"     value='<%=detailrow.get("length")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格');return;}countWeight(<%=i%>)"  maxlength='<%=list.getColumn("length").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">重量(kg)</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="weight_<%=i%>"  name="weight_<%=i%>"     value='<%=detailrow.get("weight")%>' maxlength='<%=list.getColumn("weight").getPrecision()%>'  ></td>
                        <%if(!masterRow.get("type_prop").equals("1")){%>
                        <td class="tdTitle" nowrap>长度2(m)<br></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:50" onKeyDown="return getNextElement();" id="length2_<%=i%>"  name="length2_<%=i%>"     value='<%=detailrow.get("length2")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格');return;}countWeight(<%=i%>)"  maxlength='<%=list.getColumn("length2").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>重量2(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="weight2_<%=i%>"  name="weight2_<%=i%>"     value='<%=detailrow.get("weight2")%>'  maxlength='<%=list.getColumn("weight2").getPrecision()%>'  ></td>
                        <%}%>
                        <td class="tdTitle" nowrap>退料(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="return_num_<%=i%>"  name="return_num_<%=i%>2"     value='<%=detailrow.get("return_num")%>'  maxlength='<%=list.getColumn("return_num").getPrecision()%>'  ></td>
                        <%if(masterRow.get("type_prop").equals("1")){%>
                        <td height="19" nowrap class="tdTitle"></td>
                        <td height="19" nowrap class="tdTitle"></td>
                        <td height="19" nowrap class="tdTitle"></td>
                        <td height="19" nowrap class="tdTitle"></td>
                        <%}%>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>班次</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="team_no_<%=i%>"  name="team_no_<%=i%>"     value='<%=detailrow.get("team_no")%>'   maxlength='<%=list.getColumn("team_no").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">机台</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="machine_no_<%=i%>"  name="machine_no_<%=i%>"     value='<%=detailrow.get("machine_no")%>'   maxlength='<%=list.getColumn("machine_no").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>经手人</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:50" onKeyDown="return getNextElement();" id="handler_<%=i%>"  name="handler_<%=i%>"     value='<%=detailrow.get("handler")%>'   maxlength='<%=list.getColumn("handler").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>制单人</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="creator_<%=i%>"  name="creator_<%=i%>"     value='<%=detailrow.get("creator")%>'   maxlength='<%=list.getColumn("creator").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>审核人</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="approver_<%=i%>"  name="approver_<%=i%>"     value='<%=detailrow.get("approver")%>'   maxlength='<%=list.getColumn("approver").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                         <td class="tdTitle" nowrap >合格品（重量）</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_weight1_<%=i%>"  name="ok_weight1_<%=i%>"     value='<%=detailrow.get("ok_weight1")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格属性');return;}sl_onchange(<%=i%>)"   maxlength='<%=list.getColumn("ok_weight1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_weight2_<%=i%>"  name="ok_weight2_<%=i%>"     value='<%=detailrow.get("ok_weight2")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格属性');return;}sl_onchange(<%=i%>)"   maxlength='<%=list.getColumn("ok_weight2").getPrecision()%>'  ></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_weight3_<%=i%>"  name="ok_weight3_<%=i%>"     value='<%=detailrow.get("ok_weight3")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格属性');return;}sl_onchange(<%=i%>)"   maxlength='<%=list.getColumn("ok_weight3").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap >合格品（张数）</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_sheet1_<%=i%>"  name="ok_sheet1_<%=i%>"     value='<%=detailrow.get("ok_sheet1")%>'   maxlength='<%=list.getColumn("ok_sheet1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_sheet2_<%=i%>"  name="ok_sheet2_<%=i%>"     value='<%=detailrow.get("ok_sheet2")%>'   maxlength='<%=list.getColumn("ok_sheet2").getPrecision()%>'  ></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_sheet3_<%=i%>"  name="ok_sheet3_<%=i%>"     value='<%=detailrow.get("ok_sheet3")%>'   maxlength='<%=list.getColumn("ok_sheet3").getPrecision()%>'  ></td>

                        <td class="tdTitle" nowrap>分切编号</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="disp_no_<%=i%>"  name="disp_no_<%=i%>"     value='<%=detailrow.get("disp_no")%>'   maxlength='<%=list.getColumn("disp_no").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap ></td>
                        <td class="tdTitle" nowrap ></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap >副品（重量）</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="hypo_weight1_<%=i%>"  name="hypo_weight1_<%=i%>"     value='<%=detailrow.get("hypo_weight1")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格属性');return;}sl_onchange(<%=i%>)"   maxlength='<%=list.getColumn("hypo_weight1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="hypo_weight2_<%=i%>"  name="hypo_weight2_<%=i%>"     value='<%=detailrow.get("hypo_weight2")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格属性');return;}sl_onchange(<%=i%>)"   maxlength='<%=list.getColumn("hypo_weight2").getPrecision()%>'  ></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="hypo_weight3_<%=i%>"  name="hypo_weight3_<%=i%>"     value='<%=detailrow.get("hypo_weight3")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格属性');return;}sl_onchange(<%=i%>)"  maxlength='<%=list.getColumn("hypo_weight3").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap >副品（张数）</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="hypo_sheet1_<%=i%>"  name="hypo_sheet1_<%=i%>"     value='<%=detailrow.get("hypo_sheet1")%>'   maxlength='<%=list.getColumn("hypo_sheet1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="hypo_sheet2_<%=i%>"  name="hypo_sheet2_<%=i%>"     value='<%=detailrow.get("hypo_sheet2")%>'   maxlength='<%=list.getColumn("hypo_sheet2").getPrecision()%>'  ></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="hypo_sheet3_<%=i%>"  name="hypo_sheet3_<%=i%>"     value='<%=detailrow.get("hypo_sheet3")%>'   maxlength='<%=list.getColumn("hypo_sheet3").getPrecision()%>'  ></td>

                        <td class="tdTitle" nowrap>副品原因</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="hypo_reason_<%=i%>"  name="hypo_reason_<%=i%>"     value='<%=detailrow.get("hypo_reason")%>'   maxlength='<%=list.getColumn("hypo_reason").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>副品部门</td>
                        <td  noWrap class="td">
                          <%if(isCanEdit){%>
                          <pc:select  name='<%="hypo_deptid_"+i%>'  addNull="1" style="width:90"   >
                          <%=deptBean.getList(detailrow.get("hypo_deptid"))%>
                          </pc:select>
                          <%}else{%>
                          <input type="text" name="hypo_deptid_<%=i%>" value='<%=deptBean.getLookupName(masterRow.get("hypo_deptid"))%>' maxlength='<%=list.getColumn("hypo_deptid").getPrecision()%>'  style="width:90" <%=detailClass_r%> onKeyDown="return getNextElement();" readonly>
                          <%}%>
                        </td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap >废品（重量）</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="wast_weight1_<%=i%>"  name="wast_weight1_<%=i%>"     value='<%=detailrow.get("wast_weight1")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格属性');return;}sl_onchange(<%=i%>)"   maxlength='<%=list.getColumn("wast_weight1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="wast_weight2_<%=i%>"  name="wast_weight2_<%=i%>"     value='<%=detailrow.get("wast_weight2")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格属性');return;}sl_onchange(<%=i%>)"   maxlength='<%=list.getColumn("wast_weight2").getPrecision()%>'  ></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="wast_weight3_<%=i%>"  name="wast_weight3_<%=i%>"     value='<%=detailrow.get("wast_weight3")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格属性');return;}sl_onchange(<%=i%>)"   maxlength='<%=list.getColumn("wast_weight3").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap >废品（张数）</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="wast_sheet1_<%=i%>"  name="wast_sheet1_<%=i%>"     value='<%=detailrow.get("wast_sheet1")%>'   maxlength='<%=list.getColumn("wast_sheet1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="wast_sheet2_<%=i%>"  name="wast_sheet2_<%=i%>"     value='<%=detailrow.get("wast_sheet2")%>'   maxlength='<%=list.getColumn("wast_sheet2").getPrecision()%>'  ></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="wast_sheet3_<%=i%>"  name="wast_sheet3_<%=i%>"     value='<%=detailrow.get("wast_sheet3")%>'   maxlength='<%=list.getColumn("wast_sheet3").getPrecision()%>'  ></td>

                        <td class="tdTitle" nowrap>废品原因</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="wast_reason<%=i%>"  name="wast_reason_<%=i%>"     value='<%=detailrow.get("wast_reason")%>'   maxlength='<%=list.getColumn("wast_reason").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>废品部门</td>
                        <td  noWrap class="td">
                          <%if(isCanEdit){%>
                          <pc:select  name='<%="wast_deptid"+i%>'   addNull="1" style="width:90"  >
                          <%=deptBean.getList(detailrow.get("wast_deptid"))%>
                          </pc:select>
                          <%}else{%>
                          <input type="text" name="wast_deptid_<%=i%>" value='<%=deptBean.getLookupName(masterRow.get("wast_deptid"))%>' maxlength='<%=list.getColumn("wast_deptid").getPrecision()%>'  style="width:90" <%=detailClass_r%> onKeyDown="return getNextElement();" readonly>
                          <%}%>
                        </td>
                      </tr>
                    </table>
                    <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview_hq_<%=i%>',1);</SCRIPT>
                  </td>
                </tr>
                <%}%>
                <%if(isDitu){%>
                <%--底涂工序--%>
               <tr>
                  <td colspan="8" noWrap class="td"> <table cellspacing=0 width="100%" cellpadding=0>
                      <tr>
                        <td nowrap class="activeVTab"> <%=gongyiBean.getLookupName(detailrow.get("gymcID"))%>
                          <%if(isCanEdit){%>
                          <img style='cursor:hand'  title="引入工序信息"  src='../images/view.gif' border=0 onClick="dispart('<%=gongyiBean.getLookupName(detailrow.get("gymcID"))%>',<%=i%>)">
                          <%}%>
                        </td>
                        <td class="lastTab" valign=bottom width=100% align=right></td>
                      </tr>
                    </table>
                    <%--context--%>
                    <%prodrow = prodBean.getLookupRow(detailrow.get("cpid"));%>
                    <table id="tableview_pt_<%=i%>" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class=tabletitle>
                        <td class="tdTitle" nowrap>生产日期</td>
                        <td height='20' class="td" nowrap><input type="text" name="prod_date_<%=i%>"  value='<%=detailrow.get("prod_date")%>' maxlength='10' style="width:65" <%=detailClass_r%> onChange="checkDate(this)" onKeyDown="return getNextElement();" >
                          <%if(isCanEdit){%>
                          <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(prod_date_<%=i%>);"></a>
                          <%}%>
                        </td>
                        <td height='20' class="tdTitle" nowrap>品名规格</td> <td height='20' colspan="3" nowrap class="td"><table BORDER="0" CELLPADDING="1" CELLSPACING="0"><tr>
                    <td nowrap class="td"><input type="hidden" name="truebl_<%=i%>" value='' ><input type="hidden" name="hsbl_<%=i%>" value='<%=prodrow.get("hsbl")%>' ><input type="hidden" name="scdwgs_<%=i%>" value='<%=prodrow.get("scdwgs")%>' ><input type="hidden" id="cpid_<%=i%>" name="cpid_<%=i%>" value='<%=detailrow.get("cpid")%>' >
                          <input type="text" <%=edClass%> style="width:65" onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodrow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>);countPlanWeight(<%=i%>)" >
                          <input type="text" <%=edClass%> style="width:180" id="pm_<%=i%>" name="pm_<%=i%>"  value='<%=prodrow.get("product")%>'  onchange="productNameSelect(this,<%=i%>);countPlanWeight(<%=i%>)">
                          </td>
                          <%if(isCanEdit){%>
                          <td  nowrap class="td"><td height='20' colspan="3" nowrap class="td"><img id="pm_addimg_<%=i%>" style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=pm_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=hsbl_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=scdwgs&fieldVar=hsbl',form1.cpid_<%=i%>.value);countPlanWeight(<%=i%>);"> </td>
                          <td  nowrap class="td"><td height='20' colspan="3" nowrap class="td"><img id="pm_delimg_<%=i%>" style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid_<%=i%>.value='';cpbm_<%=i%>.value='';pm_<%=i%>.value='';scdwgs_<%=i%>.value='';hsbl_<%=i%>.value=''"> </td>
                          <%}%></tr></table>
                        </td>
                        <td height='20' nowrap class="tdTitle">规格属性</td> <td height='20' colspan="3" nowrap class="td"><table BORDER="0" CELLPADDING="1" CELLSPACING="0"><tr>
                    <td nowrap class="td"><input type="text" <%=detailClass_r%> id="guige_<%=i%>" name="guige_<%=i%>" value='<%=guigeBean.getLookupName(detailrow.get("dmsxID"))%>' style="width:280" onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>);countPlanWeight(<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>>
                          <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detailrow.get("dmsxid")%>">
                          </td>
                          <%if(isCanEdit){%>
                          <td  nowrap class="td"><img id="guige_addimg_<%=i%>" style='cursor:hand' src='../images/view.gif' border=0
onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=detailrow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}
      PropertySelect('form1','dmsxid_<%=i%>','guige_<%=i%>',form1.cpid_<%=i%>.value);countPlanWeight(<%=i%>)"> </td>
                         <td  nowrap class="td"><img  id="guige_delimg_<%=i%>" style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';guige_<%=i%>.value='';"> </td>
                          <%}%></tr></table>
                        </td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>质量验证</td>
                        <td colspan="3" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:280" onKeyDown="return getNextElement();" id="qual_validate_<%=i%>"  name="qual_validate_<%=i%>"     value='<%=detailrow.get("qual_validate")%>'  maxlength='<%=list.getColumn("qual_validate").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">原断纸数</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="old_break_<%=i%>"  name="old_break_<%=i%>"     value='<%=detailrow.get("old_break")%>'   maxlength='<%=list.getColumn("old_break").getPrecision()%>'></td>
                        <td nowrap class="tdTitle">断纸次数</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="new_break_<%=i%>"  name="new_break_<%=i%>"     value='<%=detailrow.get("new_break")%>'   maxlength='<%=list.getColumn("new_break").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>端面不齐(mm)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="end_side_<%=i%>"  name="end_side_<%=i%>"     value='<%=detailrow.get("end_side")%>'   maxlength='<%=list.getColumn("end_side").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>计划损耗(m)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_length_<%=i%>"  name="plan_length_<%=i%>"   value='<%=detailrow.get("plan_length")%>' onchange="countPlanWeight(<%=i%>)"  maxlength='<%=list.getColumn("plan_length").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">计划损耗(kg)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_weigth_<%=i%>"  name="plan_weigth_<%=i%>"   value='<%=detailrow.get("plan_weigth")%>'  maxlength='<%=list.getColumn("plan_weigth").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(m)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_length_<%=i%>"  name="fact_length_<%=i%>"     value='<%=detailrow.get("fact_length")%>'   maxlength='<%=list.getColumn("fact_length").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(kg)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_weight_<%=i%>"  name="fact_weight_<%=i%>"     value='<%=detailrow.get("fact_weight")%>'    maxlength='<%=list.getColumn("fact_weight").getPrecision()%>'  >
                         <img style='cursor:hand'  title="计算实际损耗"  src='../images/nextbill.gif' border=0 onClick="countprod_ratio(<%=i%>);">
                         <img style='cursor:hand'  title="清空实际损耗"  src='../images/delete.gif' border=0 onClick="del_prod_ratio(<%=i%>);"></td>
                        <td class="tdTitle" nowrap>奖罚(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="award_<%=i%>"  name="award_<%=i%>"     value='<%=detailrow.get("award")%>'   maxlength='<%=list.getColumn("award").getPrecision()%>'  >
                        <img style='cursor:hand'  title="计算奖罚"  src='../images/nextbill.gif' border=0 onClick="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格');return;}countaward(<%=i%>);"></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>损耗原因</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="ull_reason_<%=i%>"  name="ull_reason_<%=i%>"     value='<%=detailrow.get("ull_reason")%>'   maxlength='<%=list.getColumn("ull_reason").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">质量说明</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="qual_desc_<%=i%>"  name="qual_desc_<%=i%>"     value='<%=detailrow.get("qual_desc")%>'   maxlength='<%=list.getColumn("qual_desc").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">批号</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="prod_batno_<%=i%>"  name="prod_batno_<%=i%>"     value='<%=detailrow.get("prod_batno")%>'   maxlength='<%=list.getColumn("prod_batno").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">工序成品率</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="prod_ratio_<%=i%>"  name="prod_ratio_<%=i%>"     value='<%=detailrow.get("prod_ratio")%>'   maxlength='<%=list.getColumn("prod_ratio").getPrecision()%>'  >
                         <img style='cursor:hand'  title="计算成品率"  src='../images/nextbill.gif' border=0 onClick="countprod_ratio(<%=i%>);">
                         <img style='cursor:hand'  title="清空成品率"  src='../images/delete.gif' border=0 onClick="del_prod_ratio(<%=i%>);"></td>
                      </tr>
                      <tr>
                        <td height="19" nowrap class="tdTitle">长度(m)</td>
                        <td class="td" nowrap><input type="hidden" name="length2_<%=i%>" value='<%=detailrow.get("length2")%>' ><input type="hidden" name="weight2_<%=i%>" value='<%=detailrow.get("weight2")%>' ><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="length_<%=i%>"  name="length_<%=i%>"     value='<%=detailrow.get("length")%>' onchange="if((form1.cpid_<%=i%>.value=='')&&(form1.guige_<%=i%>.value=='')){alert('请先输入产品和规格');return;}countWeight(<%=i%>)"  maxlength='<%=list.getColumn("length").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>重量(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="weight_<%=i%>"  name="weight_<%=i%>"     value='<%=detailrow.get("weight")%>'   maxlength='<%=list.getColumn("weight").getPrecision()%>'  ></td>
                        <%if(!masterRow.get("type_prop").equals("1")){%>
                        <td class="tdTitle" nowrap>涂料1</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="dope_one_<%=i%>"  name="dope_one_<%=i%>"     value='<%=detailrow.get("dope_one")%>'   maxlength='<%=list.getColumn("dope_one").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>涂料2</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="dope_two_<%=i%>"  name="dope_two_<%=i%>"     value='<%=detailrow.get("dope_two")%>'   maxlength='<%=list.getColumn("dope_two").getPrecision()%>'  ></td>
                        <%}%>
                        <td class="tdTitle" nowrap>退料(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="return_num_<%=i%>"  name="return_num_<%=i%>"     value='<%=detailrow.get("return_num")%>'  maxlength='<%=list.getColumn("return_num").getPrecision()%>'  ></td>
                        <%if(masterRow.get("type_prop").equals("1")){%>
                        <td height="19" nowrap class="tdTitle"></td>
                        <td height="19" nowrap class="tdTitle"></td>
                        <td height="19" nowrap class="tdTitle"></td>
                        <td height="19" nowrap class="tdTitle"></td>
                        <%}%>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>班次</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="team_no_<%=i%>"  name="team_no_<%=i%>"     value='<%=detailrow.get("team_no")%>'   maxlength='<%=list.getColumn("team_no").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>机台</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="machine_no_<%=i%>"  name="machine_no_<%=i%>"     value='<%=detailrow.get("machine_no")%>'   maxlength='<%=list.getColumn("machine_no").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>经手人</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="handler_<%=i%>"  name="handler_<%=i%>"     value='<%=detailrow.get("handler")%>'   maxlength='<%=list.getColumn("handler").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>制单人</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="creator_<%=i%>"  name="creator_<%=i%>"     value='<%=detailrow.get("creator")%>'   maxlength='<%=list.getColumn("creator").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>审核人</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="approver_<%=i%>"  name="approver_<%=i%>"     value='<%=detailrow.get("approver")%>'   maxlength='<%=list.getColumn("approver").getPrecision()%>'  ></td>
                      </tr>
                    </table>
                    <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview_pt_<%=i%>',1);</SCRIPT>
                  </td>
                </tr>
                <%}%>
                <%list.next();
                       }%>
              </table></td>
    </tr>
  </table>
  <tr>
    <td> <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
        <tr>
          <td colspan="3" noWrap class="tableTitle">
            <%if(isCanEdit){%>

             <input name="count" type="button" class="button" onClick="getcount();" value="比率计算">

             <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回">
            <%}%>
            <input name="btnback" type="button" class="button" onClick="backList();" value=" 返回 ">
          </td>
        </tr>
      </table></td>
  </tr>
</table>
</form>
<script language="javascript">
  <%=b_TrackbillBean.adjustInputSize(new String[]{"cpbm","pm","guige"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}

    function OrderSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
      var winName= "SingleladingSelector";
      paraStr = "../produce/SC_Trackbill_import_lading.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+curID;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
    }
    function selctbilloflading()
    {
      form1.selectedtdid.value='';
      //CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc','fieldVar=dwtxid&fieldVar=dwmc',form1.dwtxid.value)
      OrderSingleSelect('form1','srcVar=selectedtdid','fieldVar=drawDetailID','',"sumitForm(<%=b_TrackbillBean.DETAIL_SALE_ADD%>,-1)");
      }

      function SingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
      {
        var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
        var winName= "SingleladingSelector";
        paraStr = "../produce/sc_trackdetail_import_lading.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&gx="+curID;
        if(methodName+'' != 'undefined')
          paraStr += "&method="+methodName;
        if(notin+'' != 'undefined')
          paraStr += "&notin="+notin;
        openUrlOpt2(paraStr);
        //newWin =window.open(paraStr,winName,winopt);
        //newWin.focus();
      }
      function dispart(gx, i)
      {//batchno_ handleperson_ creator_ approver_
        SingleSelect('form1',
        'srcVar=selectedtdid&srcVar=pm_'+ i +'&srcVar=cpid_'+ i +'&srcVar=cpbm_'+ i +'&srcVar=dmsxid_'+i +'&srcVar=guige_'+i+'&srcVar=scdwgs_'+i+'&srcVar=hsbl_'+i
        +'&srcVar=qual_validate_'+i +'&srcVar=old_break_'+i +'&srcVar=new_break_'+i +'&srcVar=plan_weigth_'+i +'&srcVar=fact_weight_'+i +'&srcVar=award_'+i +'&srcVar=ull_reason_'+i+'&srcVar=machine_no_'+i+'&srcVar=team_no_'+i
        +'&srcVar=prod_batno_'+i +'&srcVar=handler_'+i +'&srcVar=creator_'+i +'&srcVar=approver_'+i,
        'fieldVar=receivedetailid&fieldVar=pm&fieldVar=cpid&fieldVar=cpbm&fieldVar=dmsxid&fieldVar=sxz&fieldVar=scdwgs&fieldVar=hsbl'
        +'&fieldVar=zlyz&fieldVar=ydzs&fieldVar=dzcs&fieldVar=jhsh&fieldVar=sjsh&fieldVar=jf&fieldVar=shyy&fieldVar=jt&fieldVar=bc'
        +'&fieldVar=batchno&fieldVar=handleperson&fieldVar=creator&fieldVar=approver',gx, "countWeight("+i+");");
      }
      function sl_onchange(i)
{
      var oldhsblObj = document.all['hsbl_'+i];
      var sxzObj = document.all['guige_'+i];
      unitConvert(document.all['prod'], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'countsheet('+i+')');
}


   function countsheet(i)
      {

          var okweight1Value=document.all['ok_weight1_'+i];
           if(okweight1Value.value=="")
             okweight1Value.value="0";
          var okweight2Value=document.all['ok_weight2_'+i];
          if(okweight2Value.value=="")
             okweight2Value.value="0";
          var okweight3Value=document.all['ok_weight3_'+i];
          if(okweight3Value.value=="")
             okweight3Value.value="0";
          var hypoweight1Value=document.all['hypo_weight1_'+i];
          if(hypoweight1Value.value=="")
             hypoweight1Value.value="0";
          var hypoweight2Value=document.all['hypo_weight2_'+i];
          if(hypoweight2Value.value=="")
             hypoweight2Value.value="0";
          var hypoweight3Value=document.all['hypo_weight3_'+i];
          if(hypoweight3Value.value=="")
             hypoweight3Value.value="0";
          var wastweight1Value=document.all['wast_weight1_'+i];
          if(wastweight1Value.value=="")
             wastweight1Value.value="0";
          var wastweight2Value=document.all['wast_weight2_'+i];
          if(wastweight2Value.value=="")
             wastweight2Value.value="0";
          var wastweight3Value=document.all['wast_weight3_'+i];
          if(wastweight3Value.value=="")
             wastweight3Value.value="0";

          var oksheet1Value=document.all['ok_sheet1_'+i];
          var oksheet2Value=document.all['ok_sheet2_'+i];
          var oksheet3Value=document.all['ok_sheet3_'+i];
          var hyposheet1Value=document.all['hypo_sheet1_'+i];
          var hyposheet2Value=document.all['hypo_sheet2_'+i];

          var hyposheet3Value=document.all['hypo_sheet3_'+i];

          var wastsheet1Value=document.all['wast_sheet1_'+i];
          var wastsheet2Value=document.all['wast_sheet2_'+i];
          var wastsheet3Value=document.all['wast_sheet3_'+i];
          var hsblValue=document.all['truebl_'+i];
          oksheet1Value.value=formatQty(parseFloat(okweight1Value.value)*parseFloat(hsblValue.value));
          oksheet2Value.value=formatQty(parseFloat(okweight2Value.value)*parseFloat(hsblValue.value));
          oksheet3Value.value=formatQty(parseFloat(okweight3Value.value)*parseFloat(hsblValue.value));
          hyposheet1Value.value=formatQty(parseFloat(hypoweight1Value.value)*parseFloat(hsblValue.value));
          hyposheet2Value.value=formatQty(parseFloat(hypoweight2Value.value)*parseFloat(hsblValue.value));
          hyposheet3Value.value=formatQty(parseFloat(hypoweight3Value.value)*parseFloat(hsblValue.value));

          wastsheet1Value.value=formatQty(parseFloat(wastweight1Value.value)*parseFloat(hsblValue.value));
          wastsheet2Value.value=formatQty(parseFloat(wastweight2Value.value)*parseFloat(hsblValue.value));
          wastsheet3Value.value=formatQty(parseFloat(wastweight3Value.value)*parseFloat(hsblValue.value));


      }//计算张数
      function countaward(i){

         var fact_weight=document.all['fact_weight_'+i];
            if(fact_weight.value=="")
             fact_weight.value="0";
        var plan_weight=document.all['plan_weigth_'+i];
        if(plan_weight.value=="")
             plan_weight.value="0";
         var award=document.all['award_'+i];
         award.value=formatQty(parseFloat(plan_weight.value)-parseFloat(fact_weight.value));
      }//计算奖罚
      //计算实际损耗和本道成品率
      function countprod_ratio(i){

        var wei1Value=document.all['weight_'+i];
        if(wei1Value.value=="")
          wei1Value.value="0";
        var wei2Value=document.all['weight2_'+i];
        if(wei2Value.value=="")
          wei2Value.value="0";
        var return_numValue=document.all['return_num_'+i];
        if(return_numValue.value=="")
          return_numValue.value="0";
        var fact_weiValue=document.all['fact_weight_'+i];
        var prod_ratioValue=document.all['prod_ratio_'+i];
        var factlengthValue=document.all['fact_length_'+i];
        if(i!=0)
              { var j=i-1;
        var scygsValue=document.all['scdwgs_'+j];
        if(scygsValue.value==""){
          alert("请输入上道工序品名");
        return;}
        var firstReturn_num=document.all['return_num_'+j];
        var firstCpmc=document.all['pm_'+j];
        var firstCpbm=document.all['cpbm_'+j];
        var firstlen=document.all['length_'+j];

        var firstPmAddImg=document.all['pm_addimg_'+j];

        var firstPmDelImg=document.all['pm_delimg_'+j];
        var firstGuigeAddImg=document.all['guige_addimg_'+j];
        var firstGuigeDelImg=document.all['guige_delimg_'+j];


        //
        var firstlen2=document.all['length2_'+j];
        var guigeValue=document.all['guige_'+j];

        if(guigeValue.value==""){
          alert("请输入上道工序规格属性");
        return;}
        var widthValue = parseString(guigeValue.value, '宽度(', ')', '(');
        var firstwei1Value=document.all['weight_'+j];
        if(firstwei1Value.value=="")
          firstwei1Value.value="0";
        var firstwei2Value=document.all['weight2_'+j];
        if(firstwei2Value.value=="")
          firstwei2Value.value="0";
        if(parseFloat(firstwei1Value.value)+parseFloat(firstwei2Value.value)==0)
          prod_ratioValue.value=0;
        else
          prod_ratioValue.value=formatQty((parseFloat(wei1Value.value)+parseFloat(wei2Value.value))/(parseFloat(firstwei1Value.value)+parseFloat(firstwei2Value.value)));
        fact_weiValue.value=formatQty(parseFloat(firstwei1Value.value)+parseFloat(firstwei2Value.value)-parseFloat(wei1Value.value)-parseFloat(wei2Value.value)-parseFloat(return_numValue.value));

        if(parseFloat(widthValue)==0)
          factlengthValue.value=0;
        else
        factlengthValue.value=formatQty(parseFloat(scygsValue.value)*parseFloat(fact_weiValue.value)/parseFloat(widthValue));
        firstwei1Value.className="ednone";firstwei1Value.readOnly=true;
        firstwei2Value.className="ednone";firstwei2Value.readOnly=true;
        firstReturn_num.className="ednone";firstReturn_num.readOnly=true;
        firstCpmc.className="ednone";firstCpmc.readOnly=true;
        firstCpbm.className="ednone";firstCpbm.readOnly=true;
        firstlen.className="ednone";firstlen.readOnly=true;
        firstlen2.className="ednone";firstlen2.readOnly=true;
        guigeValue.className="ednone";guigeValue.readOnly=true;


        firstPmAddImg.style.display="none";
        firstPmDelImg.style.display="none";
        firstGuigeAddImg.style.display="none";
        firstGuigeDelImg.style.display="none";

        }
        else {

          var linliaoCpmc=document.all['pm'];
          var linliaoCpbm=document.all['cpbm'];
          var PmAddImg=document.all['linliaopm_addimg'];

          var PmDelImg=document.all['linliaopm_delimg'];
          var GuigeAddImg=document.all['linliaoguige_addimg'];
          var GuigeDelImg=document.all['linliaoguige_delimg'];
          var scygsValue=document.all['scdwgs'];
          if(scygsValue.value==""){
            alert("请输入领料品名");
          return;}
          var guigeValue=document.all['guige'];
          if(guigeValue.value==""){
            alert("请输入领料规格属性");
          return;}
          var widthValue = parseString(guigeValue.value, '宽度(', ')', '(');
          var net_weightValue=document.all['net_weight'];
          if(net_weightValue.value=="")
            net_weightValue.value="0";
          if(parseFloat(wei1Value.value)+parseFloat(wei2Value.value)==0)
            prod_ratioValue.value=0;
          else
            prod_ratioValue.value=formatQty((parseFloat(wei1Value.value)+parseFloat(wei2Value.value))/parseFloat(net_weightValue.value));
          fact_weiValue.value=formatQty(parseFloat(net_weightValue.value)-parseFloat(wei1Value.value)-parseFloat(wei2Value.value)-parseFloat(return_numValue.value));
          if(parseFloat(widthValue)==0)
            factlengthValue.value=0;
          else

            factlengthValue.value=formatQty(parseFloat(scygsValue.value)*parseFloat(fact_weiValue.value)/parseFloat(widthValue));
          net_weightValue.className="edline";net_weightValue.readOnly=true;
          linliaoCpmc.className="edline";linliaoCpmc.readOnly=true;
          linliaoCpbm.className="edline";linliaoCpbm.readOnly=true;
          guigeValue.className="edline";guigeValue.readOnly=true;
          PmAddImg.style.display="none";
          PmDelImg.style.display="none";
          GuigeAddImg.style.display="none";
          GuigeDelImg.style.display="none";
        }
      }
     //使上道工序不可编辑的变成可编辑，把本到工序的实际损耗和成品率清空
      function del_prod_ratio(i){



        var fact_weiValue=document.all['fact_weight_'+i];
        fact_weiValue.value="";
        var prod_ratioValue=document.all['prod_ratio_'+i];
        prod_ratioValue.value="";
        var factlengthValue=document.all['fact_length_'+i];
        factlengthValue.value="";
        if(i!=0)
              { var j=i-1;

        var firstReturn_num=document.all['return_num_'+j];
        var firstCpmc=document.all['pm_'+j];
        var firstCpbm=document.all['cpbm_'+j];
        var firstlen=document.all['length_'+j];

        var firstPmAddImg=document.all['pm_addimg_'+j];

        var firstPmDelImg=document.all['pm_delimg_'+j];
        var firstGuigeAddImg=document.all['guige_addimg_'+j];
        var firstGuigeDelImg=document.all['guige_delimg_'+j];


        //
        var firstlen2=document.all['length2_'+j];
        var guigeValue=document.all['guige_'+j];


        var firstwei1Value=document.all['weight_'+j];

        var firstwei2Value=document.all['weight2_'+j];

        firstwei1Value.className="edFocused_r";firstwei1Value.readOnly=false;
        firstwei2Value.className="edFocused_r";firstwei2Value.readOnly=false;
        firstReturn_num.className="edFocused_r";firstReturn_num.readOnly=false;
        firstCpmc.className="edFocused_r";firstCpmc.readOnly=false;
        firstCpbm.className="edFocused_r";firstCpbm.readOnly=false;
        firstlen.className="edFocused_r";firstlen.readOnly=false;
        firstlen2.className="edFocused_r";firstlen2.readOnly=false;
        guigeValue.className="edFocused_r";guigeValue.readOnly=false;
        firstPmAddImg.style.display="block";
        firstPmDelImg.style.display="block";
        firstGuigeAddImg.style.display="block";
        firstGuigeDelImg.style.display="block";

        }
        else {

          var linliaoCpmc=document.all['pm'];
          var linliaoCpbm=document.all['cpbm'];
          var PmAddImg=document.all['linliaopm_addimg'];

          var PmDelImg=document.all['linliaopm_delimg'];
          var GuigeAddImg=document.all['linliaoguige_addimg'];
          var GuigeDelImg=document.all['linliaoguige_delimg'];

          var guigeValue=document.all['guige'];


          var net_weightValue=document.all['net_weight'];

          net_weightValue.className="edFocused_r";net_weightValue.readOnly=false;
          linliaoCpmc.className="edFocused_r";linliaoCpmc.readOnly=false;
          linliaoCpbm.className="edFocused_r";linliaoCpbm.readOnly=false;
          guigeValue.className="edFocused_r";guigeValue.readOnly=false;
          PmAddImg.style.display="block";
          PmDelImg.style.display="block";
          GuigeAddImg.style.display="block";
          GuigeDelImg.style.display="block";
        }
      }
     //计算计划重量
 function countPlanWeight(i)
           {


           if(i==-1){
           var m=i+1;
           var plan_weight=document.all['plan_weigth_'+m];
           var planlength=document.all['plan_length_'+m];
           var scdwgs=document.all['scdwgs'];
           if(scdwgs.value==""){
            scdwgs.value=0;
            }
           var guige=document.all['guige'];
           if(guige.value==""){  guige.value=0;}
           var width= parseString(guige.value, '宽度(', ')', '(');
           plan_weight.value=formatQty(parseFloat(planlength.value)*parseFloat(width)/parseFloat(scdwgs.value));
           }
           else{
           var plan_weight=document.all['plan_weigth_'+i];
           var planlengthValue=document.all['plan_length_'+i];
           if(planlengthValue.value=="")
           planlengthValue.value="0";
           if(i==0){
           var scdwgs=document.all['scdwgs'];
           if(scdwgs.value==""){
           alert("请输入领料的品名");
           return;}
           var guige=document.all['guige'];
           if(guige.value==""){
           alert("请输入领料的规格属性");
           return;}
           var width= parseString(guige.value, '宽度(', ')', '(');
           plan_weight.value=formatQty(parseFloat(planlengthValue.value)*parseFloat(width)/parseFloat(scdwgs.value));
         }
         if(i>0){
         var j=i-1;
         var scdwgs=document.all['scdwgs_'+j];

         if(scdwgs.value==""){
         alert("请输入上道工序品名");
         return;}
         var guige=document.all['guige_'+j];

         if(guige.value==""){
         alert("请输入上道工序的规格属性");
         return;}
         var width= parseString(guige.value, '宽度(', ')', '(');
         plan_weight.value=formatQty(parseFloat(planlengthValue.value)*parseFloat(width)/parseFloat(scdwgs.value));
         } }


           }
           //计算重量
 function countWeight(i)
      {

       var scygsValue=document.all['scdwgs_'+i];
       if(scygsValue.value==""){
       alert("请输入品名");
       return;}
       var guigeValue=document.all['guige_'+i];
       if(guigeValue.value==""){
       alert("请输入规格属性");
       return;}
       var widthValue = parseString(guigeValue.value, '宽度(', ')', '(');
       var lengthValue1=document.all['length_'+i];
       if(lengthValue1.value=="")
       lengthValue1.value="0";
       var lengthValue2=document.all['length2_'+i];
       if(lengthValue2.value=="")
       lengthValue2.value="0";
       var weight=document.all['weight_'+i];
       var weight2=document.all['weight2_'+i];
       if(parseFloat(scygsValue.value)==0){
        weight.value=0;
        weight2.value=0;
        plan_weight.value=0;

        }
        else{
        weight.value=formatQty(parseFloat(lengthValue1.value)*parseFloat(widthValue)/parseFloat(scygsValue.value));

        weight2.value=formatQty(parseFloat(lengthValue2.value)*parseFloat(widthValue)/parseFloat(scygsValue.value));

        }
        }

       function parseString(sxz,s,t,y){
    if(sxz=='' || s=='' || t=='' || y=='')
       return '';
    else{

      leng = sxz.length;//得到字符串长度
      start= sxz.indexOf(s);//得到sxz第一个s出现的位置
      startValue = sxz.substring(start, leng);//截掉第一个出现位置前面的字符
      end = startValue.indexOf(t);
      temp = startValue.substring(0,end);
      cur= temp.indexOf(y);
      value = temp.substring(cur+1, temp.length);

      return value;
    }
 }

</script>
<%out.print(retu);%>
</BODY>
</Html>

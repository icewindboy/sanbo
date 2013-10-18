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
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function productCodeSelect(obj, i)
  {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=ztqq_'+i,
                      'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=ztqq', obj.value,'product_change('+i+')');
  }
  function productNameSelect(obj,i)
  {
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=ztqq_'+i,
                      'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=ztqq', obj.value,'product_change('+i+')');
  }
  function propertyNameSelect(obj,cpid,i)
  {
    PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                         'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
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
  if('form1.length'+i+'.value'==''||'form1.length2'+i+'.value'=='')
  {
  alert('请先填写长度，用于计算出重量');
  return;}
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
  RowMap[] detail_de_Rows= b_TrackbillBean.getDetailRowinfos();//从表的从表多行
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
                  <td  noWrap class="tdTitle">生产跟踪卡号</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="track_bill_no" value='<%=masterRow.get("track_bill_no")%>' maxlength='<%=ds.getColumn("track_bill_no").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="track_bill_no" value='<%=masterRow.get("track_bill_no")%>' maxlength='<%=ds.getColumn("track_bill_no").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                   <td  noWrap class="tdTitle">供应商<%corpRow = dwtxBean.getLookupRow(masterRow.get("dwtxid"));%></td>
                  <td  noWrap colspan='3' class="td"> <input type="hidden" name="dwtxid_" value='<%=masterRow.get("dwtxid")%>'>
                    <%if(isCanEdit){%><input type="text" <%=detailClass_r%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="prodCodeSelect(this)" ><%}%>
                    <input type="text" <%=detailClass_r%> name="dwmc" value='<%=dwtxBean.getLookupName(masterRow.get("dwtxid"))%>'  onKeyDown="return getNextElement();"  style="width:200"  onchange="prodNameChange(this)" >
                    <%if(isCanEdit){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value);">
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">生产跟踪单类型名称</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <pc:select name="track_type_ID" addNull="1" style="width:110" onSelect="leixingchange()"  >
                    <%=trackBean.getList(masterRow.get("track_type_ID"))%> </pc:select>
                    <%}else{%>
                    <input type="text" name="track_type_ID" value='<%=trackBean.getLookupName(masterRow.get("track_type_ID"))%>' maxlength='<%=ds.getColumn("track_type_ID").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" readonly>
                    <%}%>
                  </td>
                </tr>
                <tr>
                 <td height='20' class="tdTitle" nowrap>品名规格 <%masterprodrow = prodBean.getLookupRow(masterRow.get("cpid"));%></td>
                        <td height='20' colspan="3" nowrap class="td"><input type="hidden" id="cpid" name="cpid" value='<%=masterRow.get("cpid")%>' >
                          <input type="text" <%=edClass%> style="width:65" onKeyDown="return getNextElement();" name="cpbm" value='<%=masterprodrow.get("cpbm")%>' onchange="productCodeSelect(this)" >
                          <input type="text" <%=edClass%> style="width:180" id="pm" name="pm"  value='<%=masterprodrow.get("product")%>'  onchange="productNameSelect(this,0)">
                          <%if(isCanEdit){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value);">
                          <%}%>
                        </td>
                        <td height='20' nowrap class="tdTitle">规格属性</td>
                        <td height='20' colspan="3" nowrap class="td"><input type="text" <%=edClass%> id="guige" name="guige" value='<%=guigeBean.getLookupName(masterRow.get("dmsxID"))%>' style="width:200" onchange="if(form1.cpid.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid.value)" onKeyDown="return getNextElement();" <%=readonly%>>
                          <input type="hidden" id="dmsxid" name="dmsxid" value="<%=masterRow.get("dmsxid")%>">
                          <%if(isCanEdit){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0
onClick="if(form1.cpid.value==''){alert('请先输入产品');return;}if('<%=masterRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}
      PropertySelect('form1','dmsxid','guige',form1.cpid.value)"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid.value='';guige.value='';">
                          <%}%>
                        </td>
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
                  <td  noWrap class="tdTitle">等级</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="grade" value='<%=masterRow.get("grade")%>' maxlength='<%=ds.getColumn("grade").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="grade" value='<%=masterRow.get("grade")%>' maxlength='<%=ds.getColumn("grade").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
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
                    <input type="text" name="prod_ratio" value='<%=masterRow.get("prod_ratio")%>' maxlength='<%=ds.getColumn("prod_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="tot_ratio" value='<%=masterRow.get("prod_ratio")%>' maxlength='<%=ds.getColumn("prod_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">副品率</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="hypo_ratio" value='<%=masterRow.get("hypo_ratio")%>' maxlength='<%=ds.getColumn("hypo_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="tot_ratio" value='<%=masterRow.get("hypo_ratio")%>' maxlength='<%=ds.getColumn("hypo_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">废品率</td>
                  <td  noWrap class="td">
                    <%if(isCanEdit){%>
                    <input type="text" name="waster_ratio" value='<%=masterRow.get("waster_ratio")%>' maxlength='<%=ds.getColumn("waster_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" >
                    <%}else{%>
                    <input type="text" name="tot_ratio" value='<%=masterRow.get("waster_ratio")%>' maxlength='<%=ds.getColumn("waster_ratio").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"  <%=readonly%>>
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
                <INPUT TYPE="HIDDEN" NAME="scdwgs_<%=i%>" value="">
                <INPUT TYPE="HIDDEN" NAME="hsbl_<%=i%>" value="">
                <%if(isFenqie){%>
                <%--分切工序--%>
                <tr>
                  <td colspan="8" noWrap class="td"> <table cellspacing=0 width="100%" cellpadding=0>
                      <tr>
                        <td nowrap class="activeVTab" colspan="8"> <%=gongyiBean.getLookupName(detailrow.get("gymcID"))%>
                          <img style='cursor:hand'  title="查看分切信息"  src='../images/view.gif' border=0 onClick="getid(<%=i%>);">
                          <%if(isCanEdit){%>
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
                        <td height='20' class="tdTitle" nowrap>品名规格</td>
                        <td height='20' colspan="3" nowrap class="td"><input type="hidden" id="cpid_<%=i%>" name="cpid_<%=i%>" value='<%=detailrow.get("cpid")%>' >
                          <input type="text" <%=edClass%> style="width:65" onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodrow.get("cpbm")%>' onchange="productCodeSelect(this)" >
                          <input type="text" <%=edClass%> style="width:180" id="pm_<%=i%>" name="pm_<%=i%>"  value='<%=prodrow.get("product")%>'  onchange="productNameSelect(this,0)">
                          <%if(isCanEdit){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value);">
                          <%}%>
                        </td>
                        <td height='20' nowrap class="tdTitle">规格属性</td>
                        <td height='20' colspan="3" nowrap class="td"><input type="text" <%=detailClass_r%> id="guige_<%=i%>" name="guige_<%=i%>" value='<%=guigeBean.getLookupName(detailrow.get("dmsxID"))%>' style="width:200" onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value)" onKeyDown="return getNextElement();" <%=readonly%>>
                          <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detailrow.get("dmsxid")%>">
                          <%if(isCanEdit){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0
onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=detailrow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}
      PropertySelect('form1','dmsxid_<%=i%>','guige_<%=i%>',form1.cpid.value)"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';guige_<%=i%>.value='';">
                          <%}%>
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
                        <td class="tdTitle" nowrap>计划损耗(长度)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_length_<%=i%>"  name="plan_length_<%=i%>"   value='<%=detailrow.get("plan_length")%>'   maxlength='<%=list.getColumn("plan_length").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">计划损耗(重量)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_weigth_<%=i%>"  name="plan_weigth_<%=i%>"   value='<%=detailrow.get("plan_weigth")%>'   maxlength='<%=list.getColumn("plan_weigth").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(长度)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_length_<%=i%>"  name="fact_length_<%=i%>"     value='<%=detailrow.get("fact_length")%>'   maxlength='<%=list.getColumn("fact_length").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(kg)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_weight_<%=i%>"  name="fact_weight_<%=i%>"     value='<%=detailrow.get("fact_weight")%>'   maxlength='<%=list.getColumn("fact_weight").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>奖罚(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="award_<%=i%>"  name="award_<%=i%>"     value='<%=detailrow.get("award")%>'   maxlength='<%=list.getColumn("award").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>损耗原因</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="ull_reason_<%=i%>"  name="ull_reason_<%=i%>"     value='<%=detailrow.get("ull_reason")%>'   maxlength='<%=list.getColumn("ull_reason").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">质量说明</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="qual_desc_<%=i%>"  name="qual_desc_<%=i%>"     value='<%=detailrow.get("qual_desc")%>'   maxlength='<%=list.getColumn("qual_desc").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">批号</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="prod_batno_<%=i%>"  name="prod_batno_<%=i%>"     value='<%=detailrow.get("prod_batno")%>'   maxlength='<%=list.getColumn("prod_batno").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">工序成品率</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="prod_ratio_<%=i%>"  name="prod_ratio_<%=i%>"     value='<%=detailrow.get("prod_ratio")%>'   maxlength='<%=list.getColumn("prod_ratio").getPrecision()%>'  ></td>
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
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width1_<%=i%>4"  name="disp_width1_<%=i%>"     value='<%=detailrow.get("disp_width1")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width2_<%=i%>5"  name="disp_width2_<%=i%>"     value='<%=detailrow.get("disp_width2")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width3_<%=i%>5"  name="disp_width3_<%=i%>"     value='<%=detailrow.get("disp_width3")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width4_<%=i%>5"  name="disp_width4_<%=i%>"     value='<%=detailrow.get("disp_width4")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width5_<%=i%>5"  name="disp_width5_<%=i%>"     value='<%=detailrow.get("disp_width5")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width6_<%=i%>6"  name="disp_width6_<%=i%>"     value='<%=detailrow.get("disp_width6")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width7_<%=i%>5"  name="disp_width7_<%=i%>"     value='<%=detailrow.get("disp_width7")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width8_<%=i%>5"  name="disp_width8_<%=i%>"     value='<%=detailrow.get("disp_width8")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="disp_width9_<%=i%>5"  name="disp_width9_<%=i%>"     value='<%=detailrow.get("disp_width9")%>'   maxlength='<%=list.getColumn("disp_width1").getPrecision()%>'  ></td>
                      </tr>
                    </table>
                    <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview_fq_<%=i%>',0);</SCRIPT>
                  </td>
                </tr>
                <% }%>
                <%if(isPutong){%>
                <%--普通工序--%>
                <tr>
                  <td colspan="3" noWrap class="td"> <table cellspacing=0 width="100%" cellpadding=0>
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
                        <td height='20' class="tdTitle" nowrap>品名规格</td>
                        <td height='20' colspan="3" nowrap class="td"><input type="hidden" id="cpid_<%=i%>" name="cpid_<%=i%>" value='<%=detailrow.get("cpid")%>' >
                          <input type="text" <%=edClass%> style="width:65" onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodrow.get("cpbm")%>' onchange="productCodeSelect(this)" >
                          <input type="text" <%=edClass%> style="width:180" id="pm_<%=i%>" name="pm_<%=i%>"  value='<%=prodrow.get("product")%>'  onchange="productNameSelect(this,0)">
                          <%if(isCanEdit){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value);">
                          <%}%>
                        </td>
                        <td  noWrap class="tdTitle">规格属性</td>
                        <td height='20' colspan="3" nowrap class="td"><input type="text" <%=detailClass_r%> id="guige_<%=i%>" name="guige_<%=i%>" value='<%=guigeBean.getLookupName(detailrow.get("dmsxID"))%>' style="width:200" onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value)" onKeyDown="return getNextElement();" <%=readonly%>>
                          <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detailrow.get("dmsxid")%>">
                          <%if(isCanEdit){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0
onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=detailrow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}
      PropertySelect('form1','dmsxid_<%=i%>','guige_<%=i%>',form1.cpid.value)"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';guige_<%=i%>.value='';">
                          <%}%>
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
                        <td class="tdTitle" nowrap>计划损耗(长度)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_length_<%=i%>"  name="plan_length_<%=i%>"   value='<%=detailrow.get("plan_length")%>'   maxlength='<%=list.getColumn("plan_length").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">计划损耗(重量)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_weigth_<%=i%>"  name="plan_weigth_<%=i%>"   value='<%=detailrow.get("plan_weigth")%>'   maxlength='<%=list.getColumn("plan_weigth").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(长度)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_length_<%=i%>"  name="fact_length_<%=i%>"     value='<%=detailrow.get("fact_length")%>'   maxlength='<%=list.getColumn("fact_length").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(kg)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_weight_<%=i%>"  name="fact_weight_<%=i%>"     value='<%=detailrow.get("fact_weight")%>'   maxlength='<%=list.getColumn("fact_weight").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>奖罚(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="award_<%=i%>"  name="award_<%=i%>"     value='<%=detailrow.get("award")%>'   maxlength='<%=list.getColumn("award").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>损耗原因</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="ull_reason_<%=i%>"  name="ull_reason_<%=i%>"     value='<%=detailrow.get("ull_reason")%>'   maxlength='<%=list.getColumn("ull_reason").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">质量说明</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="qual_desc_<%=i%>"  name="qual_desc_<%=i%>"     value='<%=detailrow.get("qual_desc")%>'   maxlength='<%=list.getColumn("qual_desc").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">批号</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="prod_batno_<%=i%>"  name="prod_batno_<%=i%>"     value='<%=detailrow.get("prod_batno")%>'   maxlength='<%=list.getColumn("prod_batno").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">工序成品率</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="prod_ratio_<%=i%>"  name="prod_ratio_<%=i%>"     value='<%=detailrow.get("prod_ratio")%>'   maxlength='<%=list.getColumn("prod_ratio").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td height="19" nowrap class="tdTitle">长度</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="length_<%=i%>"  name="length_<%=i%>"     value='<%=detailrow.get("length")%>'   maxlength='<%=list.getColumn("length").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>重量</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="weight_<%=i%>"  name="weight_<%=i%>"     value='<%=detailrow.get("weight")%>'   maxlength='<%=list.getColumn("weight").getPrecision()%>'  ></td>
                        <%if(!masterRow.get("type_prop").equals("1")){%>
                        <td class="tdTitle" nowrap>长度2<br></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="length2_<%=i%>"  name="length2_<%=i%>"     value='<%=detailrow.get("length")%>'   maxlength='<%=list.getColumn("length").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>重量2</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="weight2_<%=i%>"  name="weight2_<%=i%>"     value='<%=detailrow.get("weight2")%>'   maxlength='<%=list.getColumn("weight2").getPrecision()%>'  ></td>
                        <%}%>
                        <td class="tdTitle" nowrap>退料</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="return_num_<%=i%>"  name="return_num_<%=i%>"     value='<%=detailrow.get("return_num")%>'   maxlength='<%=list.getColumn("return_num").getPrecision()%>'  ></td>
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
                  <td colspan="3" noWrap class="td"> <table cellspacing=0 width="100%" cellpadding=0>
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
                        <td height='20' class="tdTitle" nowrap>品名规格</td>
                        <td height='20' colspan="3" nowrap class="td"><input type="hidden" id="cpid_<%=i%>" name="cpid_<%=i%>" value='<%=detailrow.get("cpid")%>' >
                          <input type="text" <%=edClass%> style="width:65" onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodrow.get("cpbm")%>' onchange="productCodeSelect(this)" >
                          <input type="text" <%=edClass%> style="width:180" id="pm_<%=i%>" name="pm_<%=i%>"  value='<%=prodrow.get("product")%>'  onchange="productNameSelect(this,0)">
                          <%if(isCanEdit){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value);">
                          <%}%>
                        </td>
                        <td  noWrap class="tdTitle">规格属性</td>
                        <td height='20' colspan="3" nowrap class="td"><input type="text" <%=detailClass_r%> id="guige_<%=i%>" name="guige_<%=i%>" value='<%=guigeBean.getLookupName(detailrow.get("dmsxID"))%>' style="width:200" onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value)" onKeyDown="return getNextElement();" <%=readonly%>>
                          <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detailrow.get("dmsxid")%>">
                          <%if(isCanEdit){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0
onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=detailrow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}
      PropertySelect('form1','dmsxid_<%=i%>','guige_<%=i%>',form1.cpid.value)"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';guige_<%=i%>.value='';">
                          <%}%>
                        </td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>质量验证</td>
                        <td colspan="5" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:270" onKeyDown="return getNextElement();" id="qual_validate_<%=i%>"  name="qual_validate_<%=i%>"     value='<%=detailrow.get("qual_validate")%>'  maxlength='<%=list.getColumn("qual_validate").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">原断纸数</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:50" onKeyDown="return getNextElement();" id="old_break_<%=i%>"  name="old_break_<%=i%>"     value='<%=detailrow.get("old_break")%>'   maxlength='<%=list.getColumn("old_break").getPrecision()%>'></td>
                        <td nowrap class="tdTitle">断纸次数</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="new_break_<%=i%>"  name="new_break_<%=i%>"     value='<%=detailrow.get("new_break")%>'   maxlength='<%=list.getColumn("new_break").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>端面不齐(mm)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="end_side_<%=i%>"  name="end_side_<%=i%>"     value='<%=detailrow.get("end_side")%>'   maxlength='<%=list.getColumn("end_side").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>计划损耗(长度)</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_length_<%=i%>"  name="plan_length_<%=i%>"   value='<%=detailrow.get("plan_length")%>'   maxlength='<%=list.getColumn("plan_length").getPrecision()%>'  ></td>
                        <td align="right" nowrap class="tdTitle">计划损耗(重量)</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_weigth_<%=i%>"  name="plan_weigth_<%=i%>"   value='<%=detailrow.get("plan_weigth")%>'   maxlength='<%=list.getColumn("plan_weigth").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(长度)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:50" onKeyDown="return getNextElement();" id="fact_length_<%=i%>"  name="fact_length_<%=i%>"     value='<%=detailrow.get("fact_length")%>'   maxlength='<%=list.getColumn("fact_length").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(kg)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_weight_<%=i%>"  name="fact_weight_<%=i%>"     value='<%=detailrow.get("fact_weight")%>'   maxlength='<%=list.getColumn("fact_weight").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>奖罚(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="award_<%=i%>"  name="award_<%=i%>"     value='<%=detailrow.get("award")%>'   maxlength='<%=list.getColumn("award").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>损耗原因</td>
                        <td colspan="3" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="ull_reason_<%=i%>"  name="ull_reason_<%=i%>"     value='<%=detailrow.get("ull_reason")%>'   maxlength='<%=list.getColumn("ull_reason").getPrecision()%>'  ></td>
                        <td colspan="2" nowrap class="tdTitle">质量说明</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:150" onKeyDown="return getNextElement();" id="qual_desc_<%=i%>"  name="qual_desc_<%=i%>"     value='<%=detailrow.get("qual_desc")%>'   maxlength='<%=list.getColumn("qual_desc").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">批号</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="prod_batno_<%=i%>"  name="prod_batno_<%=i%>"     value='<%=detailrow.get("prod_batno")%>'   maxlength='<%=list.getColumn("prod_batno").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">工序成品率</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="prod_ratio_<%=i%>"  name="prod_ratio_<%=i%>"     value='<%=detailrow.get("prod_ratio")%>'   maxlength='<%=list.getColumn("prod_ratio").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td height="19" nowrap class="tdTitle">长度</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="length_<%=i%>"  name="length_<%=i%>"     value='<%=detailrow.get("length")%>'   maxlength='<%=list.getColumn("length").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">重量</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="weight_<%=i%>"  name="weight_<%=i%>"     value='<%=detailrow.get("weight")%>'   maxlength='<%=list.getColumn("weight").getPrecision()%>'  ></td>
                        <%if(!masterRow.get("type_prop").equals("1")){%>
                        <td class="tdTitle" nowrap>长度2<br></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:50" onKeyDown="return getNextElement();" id="length2_<%=i%>"  name="length2_<%=i%>"     value='<%=detailrow.get("length")%>'   maxlength='<%=list.getColumn("length").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>重量2</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="weight2_<%=i%>"  name="weight2_<%=i%>"     value='<%=detailrow.get("weight2")%>'   maxlength='<%=list.getColumn("weight2").getPrecision()%>'  ></td>
                        <%}%>
                        <td class="tdTitle" nowrap>退料</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="return_num_<%=i%>"  name="return_num_<%=i%>2"     value='<%=detailrow.get("return_num")%>'   maxlength='<%=list.getColumn("return_num").getPrecision()%>'  ></td>
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
                        <td class="tdTitle" nowrap >合格品（张数）</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_sheet1_<%=i%>"  name="ok_sheet1_<%=i%>"     value='<%=detailrow.get("ok_sheet1")%>'   maxlength='<%=list.getColumn("ok_sheet1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_sheet2_<%=i%>"  name="ok_sheet2_<%=i%>"     value='<%=detailrow.get("ok_sheet2")%>'   maxlength='<%=list.getColumn("ok_sheet2").getPrecision()%>'  ></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_sheet3_<%=i%>2"  name="ok_sheet3_<%=i%>"     value='<%=detailrow.get("ok_sheet3")%>'   maxlength='<%=list.getColumn("ok_sheet3").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap >合格品（重量）</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_weight1_<%=i%>"  name="ok_weight1_<%=i%>"     value='<%=detailrow.get("ok_weight1")%>'   maxlength='<%=list.getColumn("ok_weight1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_weight2_<%=i%>"  name="ok_weight2_<%=i%>"     value='<%=detailrow.get("ok_weight2")%>'   maxlength='<%=list.getColumn("ok_weight2").getPrecision()%>'  ></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_weight3_<%=i%>"  name="ok_weight3_<%=i%>"     value='<%=detailrow.get("ok_weight3")%>'   maxlength='<%=list.getColumn("ok_weight3").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>分切编号</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="disp_no_<%=i%>"  name="disp_no_<%=i%>"     value='<%=detailrow.get("disp_no")%>'   maxlength='<%=list.getColumn("disp_no").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap ></td>
                        <td class="tdTitle" nowrap ></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap >副品（张数）</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="hypo_sheet1_<%=i%>"  name="hypo_sheet1_<%=i%>"     value='<%=detailrow.get("hypo_sheet1")%>'   maxlength='<%=list.getColumn("hypo_sheet1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="hypo_sheet2_<%=i%>"  name="hypo_sheet2_<%=i%>"     value='<%=detailrow.get("hypo_sheet2")%>'   maxlength='<%=list.getColumn("hypo_sheet2").getPrecision()%>'  ></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="hypo_sheet3_<%=i%>"  name="hypo_sheet3_<%=i%>"     value='<%=detailrow.get("hypo_sheet3")%>'   maxlength='<%=list.getColumn("hypo_sheet3").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap >副品（重量）</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_weight1_<%=i%>"  name="hypo_weight1_<%=i%>"     value='<%=detailrow.get("hypo_weight1")%>'   maxlength='<%=list.getColumn("hypo_weight1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_weight2_<%=i%>"  name="hypo_weight2_<%=i%>"     value='<%=detailrow.get("hypo_weight2")%>'   maxlength='<%=list.getColumn("hypo_weight2").getPrecision()%>'  ></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="ok_weight3_<%=i%>2"  name="hypo_weight3_<%=i%>"     value='<%=detailrow.get("hypo_weight3")%>'   maxlength='<%=list.getColumn("hypo_weight3").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>副品原因</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="hypo_reason_<%=i%>"  name="hypo_reason_<%=i%>"     value='<%=detailrow.get("hypo_reason")%>'   maxlength='<%=list.getColumn("hypo_reason").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>副品部门</td>
                        <td  noWrap class="td">
                          <%if(isCanEdit){%>
                          <pc:select  name='<%="wast_deptid"+i%>'  addNull="1" style="width:90"   >
                          <%=deptBean.getList(detailrow.get("hypo_deptid"))%>
                          </pc:select>
                          <%}else{%>
                          <input type="text" name="hypo_deptid_<%=i%>" value='<%=deptBean.getLookupName(masterRow.get("hypo_deptid"))%>' maxlength='<%=list.getColumn("hypo_deptid").getPrecision()%>'  style="width:90" <%=detailClass_r%> onKeyDown="return getNextElement();" readonly>
                          <%}%>
                        </td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap >废品（张数）</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="wast_sheet1_<%=i%>"  name="wast_sheet1_<%=i%>"     value='<%=detailrow.get("wast_sheet1")%>'   maxlength='<%=list.getColumn("wast_sheet1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="wast_sheet2_<%=i%>"  name="wast_sheet2_<%=i%>"     value='<%=detailrow.get("wast_sheet2")%>'   maxlength='<%=list.getColumn("wast_sheet2").getPrecision()%>'  ></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="wast_sheet3_<%=i%>2"  name="hypo_sheet3_<%=i%>"     value='<%=detailrow.get("wast_sheet3")%>'   maxlength='<%=list.getColumn("wast_sheet3").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap >废品（重量）</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="wast_weight1_<%=i%>"  name="wast_weight1_<%=i%>"     value='<%=detailrow.get("wast_weight1")%>'   maxlength='<%=list.getColumn("wast_weight1").getPrecision()%>'  ></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="wast_weight2_<%=i%>"  name="wast_weight2_<%=i%>"     value='<%=detailrow.get("wast_weight2")%>'   maxlength='<%=list.getColumn("wast_weight2").getPrecision()%>'  ></td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%> style="width:54.5" onKeyDown="return getNextElement();" id="wast_weight3_<%=i%>2"  name="wast_weight3_<%=i%>"     value='<%=detailrow.get("wast_weight3")%>'   maxlength='<%=list.getColumn("wast_weight3").getPrecision()%>'  ></td>
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
                <tr >
                  <td colspan="3" noWrap class="td"> <table cellspacing=0 width="100%" cellpadding=0>
                      <tr>
                        <td nowrap class="activeVTab"><%=gongyiBean.getLookupName(detailrow.get("gymcID"))%>
                          <%if(isCanEdit){%>
                          <img style='cursor:hand'  title="引入工序信息"  src='../images/view.gif' border=0 onClick="GXlading('<%=gongyiBean.getLookupName(detailrow.get("gymcID"))%>',<%=i%>)">
                          <%}%>
                        </td>
                        <td class="lastTab" valign=bottom width=100% align=right></td>
                      </tr>
                    </table>
                    <%prodrow = prodBean.getLookupRow(detailrow.get("cpid"));%>
                    <table id="tableview_dt_<%=i%>" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class=tabletitle>
                        <td class="tdTitle" nowrap>生产日期</td>
                        <td height='20' class="td" nowrap><input type="text" name="prod_date_<%=i%>"  value='<%=detailrow.get("prod_date")%>' maxlength='10' style="width:65" <%=detailClass_r%> onChange="checkDate(this)" onKeyDown="return getNextElement();" >
                          <%if(isCanEdit){%>
                          <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(prod_date_<%=i%>);"></a>
                          <%}%>
                        </td>
                        <td height='20' class="tdTitle" nowrap>品名规格</td>
                        <td height='20' colspan="3" nowrap class="td"><input type="hidden" id="cpid_<%=i%>" name="cpid_<%=i%>" value='<%=detailrow.get("cpid")%>' >
                          <input type="text" <%=edClass%> style="width:65" onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodrow.get("cpbm")%>' onchange="productCodeSelect(this)" >
                          <input type="text" <%=edClass%> style="width:180" id="pm_<%=i%>" name="pm_<%=i%>"  value='<%=prodrow.get("product")%>'  onchange="productNameSelect(this,0)">
                          <%if(isCanEdit){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value);">
                          <%}%>
                        </td>
                        <td  noWrap class="tdTitle">规格属性</td>
                        <td height='20' colspan="3" nowrap class="td"><input type="text" <%=detailClass_r%> id="guige_<%=i%>" name="guige_<%=i%>" value='<%=guigeBean.getLookupName(detailrow.get("dmsxID"))%>' style="width:200" onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value)" onKeyDown="return getNextElement();" <%=readonly%>>
                          <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detailrow.get("dmsxid")%>">
                          <%if(isCanEdit){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0
onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=detailrow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}
      PropertySelect('form1','dmsxid_<%=i%>','guige_<%=i%>',form1.cpid.value)"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';guige_<%=i%>.value='';">
                          <%}%>
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
                        <td class="tdTitle" nowrap>计划损耗(长度)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_length_<%=i%>"  name="plan_length_<%=i%>"   value='<%=detailrow.get("plan_length")%>'   maxlength='<%=list.getColumn("plan_length").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">计划损耗(重量)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="plan_weigth_<%=i%>"  name="plan_weigth_<%=i%>"   value='<%=detailrow.get("plan_weigth")%>'   maxlength='<%=list.getColumn("plan_weigth").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(长度)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_length_<%=i%>"  name="fact_length_<%=i%>"     value='<%=detailrow.get("fact_length")%>'   maxlength='<%=list.getColumn("fact_length").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">实际损耗(kg)</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="fact_weight_<%=i%>"  name="fact_weight_<%=i%>"     value='<%=detailrow.get("fact_weight")%>'   maxlength='<%=list.getColumn("fact_weight").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>奖罚(kg)</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="award_<%=i%>"  name="award_<%=i%>"     value='<%=detailrow.get("award")%>'   maxlength='<%=list.getColumn("award").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td class="tdTitle" nowrap>损耗原因</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="ull_reason_<%=i%>"  name="ull_reason_<%=i%>"     value='<%=detailrow.get("ull_reason")%>'   maxlength='<%=list.getColumn("ull_reason").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">质量说明</td>
                        <td colspan="2" nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:190" onKeyDown="return getNextElement();" id="qual_desc_<%=i%>"  name="qual_desc_<%=i%>"     value='<%=detailrow.get("qual_desc")%>'   maxlength='<%=list.getColumn("qual_desc").getPrecision()%>'  ></td>
                        <td nowrap class="tdTitle">批号</td>
                        <td nowrap class="td"><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="prod_batno_<%=i%>"  name="prod_batno_<%=i%>"     value='<%=detailrow.get("prod_batno")%>'   maxlength='<%=list.getColumn("prod_batno").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap align="right">工序成品率</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:90" onKeyDown="return getNextElement();" id="prod_ratio_<%=i%>"  name="prod_ratio_<%=i%>"     value='<%=detailrow.get("prod_ratio")%>'   maxlength='<%=list.getColumn("prod_ratio").getPrecision()%>'  ></td>
                      </tr>
                      <tr>
                        <td height="19" nowrap class="tdTitle">长度</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="length_<%=i%>"  name="length_<%=i%>"     value='<%=detailrow.get("length")%>'   maxlength='<%=list.getColumn("length").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>重量</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="weight_<%=i%>"  name="weight_<%=i%>"     value='<%=detailrow.get("weight")%>'   maxlength='<%=list.getColumn("weight").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>涂料1<br></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="dope_one_<%=i%>"  name="dope_one_<%=i%>"     value='<%=detailrow.get("dope_one")%>'   maxlength='<%=list.getColumn("dope_one").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>涂料2</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="dope_two_<%=i%>"  name="dope_two_<%=i%>"     value='<%=detailrow.get("dope_two")%>'   maxlength='<%=list.getColumn("dope_two").getPrecision()%>'  ></td>
                        <td class="tdTitle" nowrap>退料</td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  style="width:90" onKeyDown="return getNextElement();" id="return_num_<%=i%>2"  name="return_num_<%=i%>2"     value='<%=detailrow.get("return_num")%>'   maxlength='<%=list.getColumn("return_num").getPrecision()%>'  ></td>
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
                    <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview_dt_<%=i%>',1);</SCRIPT>
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
            <input name="count" type="button" class="button" onClick="getcount();" value="计算成品率">
            <%}if(isCanEdit){
             String we= "sumitForm("+Operate.POST_CONTINUE+");";
             String po = "sumitForm("+Operate.POST+");";
            %>
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
        +'&fieldVar=batchno&fieldVar=handleperson&fieldVar=creator&fieldVar=approver',gx, "alert('1');");
      }
      function GXlading(gx,i)
      {
        form1.selectedtdid.value='';
        //CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc','fieldVar=dwtxid&fieldVar=dwmc',form1.dwtxid.value)//sumitForm(<%=b_TrackbillBean.GX_ADD%>,'')
        SingleSelect('form1','srcVar=selectedtdid&srcVar=pm_'+i,'fieldVar=receivedetailid&fieldVar=fieldVar=cpid',gx);
       }
</script>
<%out.print(retu);%>
</BODY>
</Html>

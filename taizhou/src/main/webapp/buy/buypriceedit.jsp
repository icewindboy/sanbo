<%--采购报价编辑页面--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
  String op_provider="op_provider";
%>
<% String pageCode = "buyprice";
  if(!loginBean.hasLimits("buyprice", request, response))
    return;
  engine.erp.buy.BuyPrice  buyPriceBean = engine.erp.buy.BuyPrice.getInstance(request);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);//外币信息
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
  //form1.sflsbj.value='0';
}
function productCodeSelect(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value,'product_change()');
}
function productHsbjSelect(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=hsdw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=hsdw', obj.value);
}
function productNameSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value,'product_change()');
}
function productNameHsbjSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=hsdw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=hsdw', obj.value);
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
function propertyNameSelect(obj,cpid)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid&srcVar=sxz',
                 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}



function product_change(){
    //document.all['dmsxid'].value="";
    //document.all['sxz'].value="";
    //document.all['widths_'+i].value="";
    //associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '', true);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">报价资料信息</TD>
  </tr></table>
<%String retu = buyPriceBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = buyPriceBean.getOneTable();
  RowMap row = buyPriceBean.getRowinfo();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isProvider=loginBean.hasLimits(pageCode, op_provider);//供应商权限
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  boolean isEdit = (buyPriceBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit))&&isProvider;//在增加的时候又增加操作，否则必须有修改权限
  boolean isHistory=ds.getValue("sflsbj").equals("1");
  String readonly = isEdit && !isHistory? "" : "readonly";
  String tableClass = isEdit&&!isHistory ? "edbox" : "edline";

%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=row.get("dwtxid")%>'>
  <INPUT TYPE="HIDDEN" NAME="cpid" value='<%=row.get("cpid")%>'>
  <table BORDER="0" cellpadding="1" cellspacing="3" align='center'>
   <%
     corpBean.regData(ds,"dwtxid");
     prodBean.regData(ds,"cpid");
   %>
   <%if(isProvider){%>
   <tr>
   <td noWrap class="tableTitle">供应商编码</td>
    <%RowMap corpRow = corpBean.getLookupRow(row.get("dwtxid"));%>
   <td nowrap class="td"><input type="text" name="dwdm" value='<%=corpRow.get("dwdm")%>' style="width:100" class="<%=tableClass%>" onKeyDown="return getNextElement();" onchange="corpCodeSelect(this);" <%=readonly%>>
   </td>
   <td noWrap class="tableTitle">供应商</td>
   <td nowrap class="td">
    <input type="text" name="dwmc" value='<%=corpBean.getLookupName(row.get("dwtxid"))%>' style="width:200" class="<%=tableClass%>" onKeyDown="return getNextElement();" onchange="corpNameSelect(this);" <%=readonly%>>
     <%if(!isHistory&&isProvider){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value)">
      <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
       <%}%></td>
    </tr>
    <%}%>
    <tr>
    <%RowMap prodRow = prodBean.getLookupRow(row.get("cpid"));%>
    <td noWrap class="tableTitle">产品编码</td>
      <td nowrap class="td">
      <%if(!isHsbj){%>
     <input type="text" onKeyDown="return getNextElement();" name="cpbm" value='<%=prodRow.get("cpbm")%>' style="width:100" class="<%=tableClass%>" onchange="productCodeSelect(this)" <%=readonly%>>
     <%if(!isHistory&&isProvider){%><img style='cursor:hand' title='单选物资' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw',form1.cpid.value,'product_change()')">
     <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cpbm.value='';product.value='';jldw.value='';"></td>
       <%}}else{%>
     <input type="text" onKeyDown="return getNextElement();" name="cpbm" value='<%=prodRow.get("cpbm")%>' style="width:100" class="<%=tableClass%>" onchange="productHsbjSelect(this)" <%=readonly%>>
     <%if(!isHistory&&isProvider){%> <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=hsdw','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=hsdw',form1.cpid.value)">
     <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cpbm.value='';product.value='';hsdw.value='';"></td>
     <%}}%>
     </td>
      <td noWrap class="tableTitle">品名规格</td>
      <td nowrap class="td">
     <input type="text" onKeyDown="return getNextElement();" name="product" value='<%=prodRow.get("product")%>' style="width:200" class="<%=tableClass%>" onchange=<%=isHsbj ? "productNameHsbjSelect(this)" : "productNameSelect(this)"%> <%=readonly%>>
   </td>
    </tr>
     <tr>
     <%if(isHsbj){%>
      <td noWrap class="tableTitle">换算单位</td>
      <td nowrap class="td"><input class="edline" name="hsdw" value='<%=prodRow.get("hsdw")%>' style="width:100" class="edline" onKeyDown="return getNextElement();" readonly>
      </td>
      <%}else{%>
      <td noWrap class="tableTitle">计量单位</td>
      <td nowrap class="td"><input class="edline" name="jldw" value='<%=prodRow.get("jldw")%>' style="width:100" class="edline" onKeyDown="return getNextElement();" readonly>
      </td>
      <%}%>
    </tr>
    <tr>
      <td noWrap class="tableTitle">报价时间</td>
      <TD class="td" nowrap><INPUT class="<%=tableClass%>" style="WIDTH: 100px" name="ksrq" value='<%=row.get("ksrq")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=readonly%>>
     <%if(!isHistory&&isProvider){%><A href="#"><IMG title=选择日期 onClick="selectDate(ksrq);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0><%}%></A></TD>
      <INPUT type='hidden' name="jsrq" value='<%=row.get("jsrq")%>'></td>
      <INPUT type='hidden' name="ksrq2" value='<%=ds.getValue("ksrq")%>'></td>

     <td noWrap class="tableTitle">备注</td>
      <td noWrap colspan="4" class="td"><INPUT TYPE="TEXT" NAME="bz" VALUE="<%=row.get("bz")%>" style="width:200" MAXLENGTH="<%=ds.getColumn("bz").getPrecision()%>" class="<%=tableClass%>" onKeyDown="return getNextElement();" <%=readonly%>>
      </td>
      </tr>
     <tr>
     <td noWrap class="tableTitle">原币报价</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="bj" VALUE="<%=row.get("bj")%>" style="width:100" MAXLENGTH="<%=ds.getColumn("bj").getPrecision()%>" class="<%=tableClass%>" onKeyDown="return getNextElement();" onchange="price_onchange(false)" <%=readonly%>>
      </td>
      <td noWrap class="tableTitle">外币类别</td>
      <td noWrap class="td"><%if(!isHistory){%>
      <%RowMap wbRow = wbBean.getLookupRow(row.get("wbid"));
        String sumit = "if(form1.wbid.value!='"+row.get("wbid")+"')sumitForm("+buyPriceBean.WB_ONCHANGE+")";%>
      <%if(!isEdit) out.print("<input type='text' value='"+wbRow.get("mc")+"' style='width:100' class='edline' readonly>");
      else {%>
      <pc:select name="wbid" addNull="1" style="width:100" onSelect="<%=sumit%>">
      <%=wbBean.getList(row.get("wbid"))%> </pc:select>
      <%}}else{%><INPUT TYPE="TEXT" VALUE="<%=wbBean.getLookupName(row.get("wbid"))%>" class='edline' readonly><%}%>
      </td>
    </tr>
    <tr>
       <td noWrap align="center" class="tableTitle">汇率</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="hl" VALUE="<%=row.get("hl")%>" style="width:100" MAXLENGTH="<%=ds.getColumn("hl").getPrecision()%>" class="<%=tableClass%>" onKeyDown="return getNextElement();" onchange="hl_onchange()" <%=readonly%>>
      </td>
       <td noWrap class="tableTitle">外币报价</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="wbbj" VALUE="<%=row.get("wbbj")%>" style="width:100" MAXLENGTH="<%=ds.getColumn("wbbj").getPrecision()%>" class="<%=tableClass%>" onKeyDown="return getNextElement();" onchange="price_onchange(true)" <%=readonly%>>
      </td>
    </tr>
    <tr>
    <td noWrap class="tableTitle">优惠条件</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="yhtj" VALUE="<%=row.get("yhtj")%>" style="width:100" MAXLENGTH="<%=ds.getColumn("yhtj").getPrecision()%>" class="<%=tableClass%>" onKeyDown="return getNextElement();" <%=readonly%>>
      </td>
      <td noWrap class="tableTitle">供应商料号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="gyslh" VALUE="<%=row.get("gyslh")%>" style="width:100" MAXLENGTH="<%=ds.getColumn("gyslh").getPrecision()%>" class="<%=tableClass%>" onKeyDown="return getNextElement();" <%=readonly%>>
      </td>
    </tr>
    <tr>
    <%if(!buyPriceBean.isAdd){%>
    <td noWrap class="tableTitle" colspan='2' align='left'>修改前的报价存为历史报价<input type="checkbox" name="sflsbj" value="1"  <%=ds.getValue("sflsbj").equals("1") ? "checked disabled" : ""%>></td>
   <%}%>
   </tr>
      <td colspan="4" noWrap class="tableTitle"><br>
        <%if((ds.getValue("sflsbj").equals("0")||buyPriceBean.isAdd)&&isProvider){%><input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(X)">
        <pc:shortcut key="x" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
<script language="javascript">
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function price_onchange(isBigUnit)
    {
      var bjObj = document.form1.bj;//报价
      var hlObj = document.form1.hl;//汇率
      var wbbjObj = document.form1.wbbj;//外币报价
      var obj = isBigUnit ? wbbjObj : bjObj;
      var showText = isBigUnit ? "输入的外币报价非法" : "输入的原币报价非法";
      var showText2 = isBigUnit ? "输入的外币报价不能小于零" : "输入的原币报价不能小于零";
      var changeObj = isBigUnit ? bjObj : wbbjObj;
      if(obj.value=="")
        return;
      if(isNaN(obj.value))
      {
        alert(showText);
        obj.focus();
        return;
      }
      if(obj.value<=0)
      {
        alert(showText2);
        obj.focus();
        return;
      }
      if(hlObj.value=="")
        return;
      if(isNaN(hlObj.value)){
        alert('汇率非法');
        return;
      }
      if(hlObj.value!="" && !isNaN(hlObj.value)){
        changeObj.value = formatQty(isBigUnit ? (parseFloat(wbbjObj.value)*parseFloat(hlObj.value)) : (parseFloat(bjObj.value)/parseFloat(hlObj.value)));
        if(isBigUnit)
          bjObj.value=changeObj.value;
        else
          wbbjObj.value=changeObj.value;
      }
    }
    function hl_onchange()
    {
      var bjObj1 = form1.bj;//报价
      var hlObj1 = form1.hl;//汇率
      var wbbjObj1 = form1.wbbj;//外币报价
      if(hlObj1.value=="")
        return;
      if(isNaN(hlObj1.value)){
        alert('汇率非法');
        return;
      }
      if(bjObj1.value!="" && !isNaN(bjObj1.value))
        wbbjObj1.value = formatQty(parseFloat(bjObj1.value)/parseFloat(hlObj1.value));
      if(bjObj1.value=="" && wbbjObj1.value!="" && !isNaN(wbbjObj1.value))
        bjObj1.value = formatQty(parseFloat(wbbjObj1.value)*parseFloat(hlObj1.value));
    }
    function getValue(){
      if(form1.sflsbj.status) form1.sflsbj.value='1';
      else form1.sflsbj.value='0';
      //window.alert(window.form1.sflsbj.value);
    }
</script>
</BODY>
</Html>

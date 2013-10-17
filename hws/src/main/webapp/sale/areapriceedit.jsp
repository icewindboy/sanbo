<%--驾驶员行驶地区价格编辑页面--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%>
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
<% String pageCode = "areaprice";
  if(!loginBean.hasLimits("areaprice", request, response))
    return;

  engine.erp.sale.AreaPrice  areaPriceBean = engine.erp.sale.AreaPrice.getInstance(request);



  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);//外币信息
%>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">
  function areaChange(isCode){
    //连接的对象。编码更改对应地区更改
    var linkObj = FindSelectObject(isCode ? "dqmc" : "dqh");
    if(linkObj == null)
      return;
    var areaid = isCode ? form1.dqh.value : form1.dqmc.value;
    linkObj.SetSelectedKey(areaid);
}
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
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function productHsbjSelect(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=hsdw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=hsdw', obj.value);
}
function productNameSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function productNameHsbjSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=hsdw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=hsdw', obj.value);
}

</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">行驶地区价格信息</TD>
  </tr></table>
<%String retu = areaPriceBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = areaPriceBean.getOneTable();
  RowMap row = areaPriceBean.getRowinfo();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  //boolean isProvider=loginBean.hasLimits(pageCode, op_provider);//供应商权限
  //boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  boolean isEdit = (areaPriceBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));//在增加的时候又增加操作，否则必须有修改权限
 // boolean isHistory=ds.getValue("sflsbj").equals("1");
  //String readonly = isEdit && !isHistory? "" : "readonly";
  String tableClass = isEdit ? "edbox" : "edline";
  ArrayList opkey = new ArrayList(); opkey.add("1");opkey.add("0");
  ArrayList opval = new ArrayList(); opval.add("送货用车"); opval.add("非送货用车");
  ArrayList[] list_prop  = new ArrayList[]{opkey, opval};

%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">


  <table BORDER="0" cellpadding="1" cellspacing="3" align='center'>


   <tr>
   <td noWrap class="tableTitle">地区价格编码</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="area_code" VALUE="<%=row.get("area_code")%>" class="<%=tableClass%>" style="width:100" MAXLENGTH="<%=ds.getColumn("area_code").getPrecision()%>"  onKeyDown="return getNextElement();"  >
      </td>
      <td noWrap class="tableTitle">地区价格名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="area_name" VALUE="<%=row.get("area_name")%>" class="<%=tableClass%>" style="width:100" MAXLENGTH="<%=ds.getColumn("area_name").getPrecision()%>"  onKeyDown="return getNextElement();">
      </td>
    </tr>
    <tr>
     <td  align="right" class="tableTitle">是否送货用车</td>
     <td nowrap class="td"> <pc:select name="isSendcar" addNull="1" style="width:90" >
     <%=areaPriceBean.listToOption(list_prop, opkey.indexOf(row.get("isSendcar")))%>
      </pc:select></td>

     </tr>
      <tr>
    <td noWrap class="tableTitle">每趟工资(元)</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="per_price" VALUE="<%=row.get("per_price")%>" class="<%=tableClass%>" style="width:100" MAXLENGTH="<%=ds.getColumn("per_price").getPrecision()%>"  onKeyDown="return getNextElement();"  >
      </td>
       <td noWrap class="tableTitle">每趟运费</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="per_fee" VALUE="<%=row.get("per_fee")%>" class="<%=tableClass%>" style="width:100" MAXLENGTH="<%=ds.getColumn("per_fee").getPrecision()%>"  onKeyDown="return getNextElement();"  >
      </td>


    </tr>
     <tr>
     <td noWrap class="tableTitle">工资价格(元/kg)</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="wage_price" VALUE="<%=row.get("wage_price")%>" class="<%=tableClass%>" style="width:100" MAXLENGTH="<%=ds.getColumn("wage_price").getPrecision()%>"  onKeyDown="return getNextElement();">
      </td>
    <td noWrap class="tableTitle">运费价格(元/kg)</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="fee_price" VALUE="<%=row.get("fee_price")%>" class="<%=tableClass%>" style="width:100" MAXLENGTH="<%=ds.getColumn("fee_price").getPrecision()%>"  onKeyDown="return getNextElement();">
      </td>

    </tr>

    <tr>
     <td noWrap class="tableTitle">工资客户系数</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="wage_cust" VALUE="<%=row.get("wage_cust")%>" class="<%=tableClass%>" style="width:100" MAXLENGTH="<%=ds.getColumn("wage_cust").getPrecision()%>"  onKeyDown="return getNextElement();">
      </td>
     <td noWrap class="tableTitle">运费客户系数</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="fee_cust" VALUE="<%=row.get("fee_cust")%>" class="<%=tableClass%>" style="width:100" MAXLENGTH="<%=ds.getColumn("fee_cust").getPrecision()%>"  onKeyDown="return getNextElement();">
      </td>

    </tr>
   <td colspan="4" noWrap class="tableTitle"><br>
        <%if(areaPriceBean.isAdd||isEdit){%><input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(X)">
        <pc:shortcut key="x" script='parent.hideFrameNoFresh()'/>
      </td>
  </table>
</form>
<script language="javascript">
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}


    function getValue(){
      if(form1.sflsbj.status) form1.sflsbj.value='1';
      else form1.sflsbj.value='0';
      //window.alert(window.form1.sflsbj.value);
    }
</SCRIPT>
</BODY>
</Html>
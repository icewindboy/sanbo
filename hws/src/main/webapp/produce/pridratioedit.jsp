<%--成品率物资编辑页面--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.dataset.*"%>
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
<% String pageCode = "ProdRatio";
  if(!loginBean.hasLimits("ProdRatio", request, response))
    return;

  engine.erp.produce.ProdRatio prodRatioeBean = engine.erp.produce.ProdRatio.getInstance(request);
  LookUp areaBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_AREA);
  LookUp areaCodeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_AREA_CODE);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);//外币信息
  RowMap prodrow= null;
  RowMap prodrow2= null;
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
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function productHsbjSelect(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=kc__cpid&srcVar=kc_cpbm&srcVar=kc_product',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function productNameSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function productNameHsbjSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=kc__cpid&srcVar=kc_cpbm&srcVar=kc_product',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}

</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=90 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">成品率物资</TD>
  </tr></table>
<%String retu = prodRatioeBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = prodRatioeBean.getOneTable();
  RowMap row = prodRatioeBean.getRowinfo();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  //boolean isProvider=loginBean.hasLimits(pageCode, op_provider);//供应商权限
  //boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  boolean isCanEdit = loginBean.hasLimits(pageCode, op_edit);//在增加的时候又增加操作，否则必须有修改权限
 // boolean isHistory=ds.getValue("sflsbj").equals("1");
  //String readonly = isEdit && !isHistory? "" : "readonly";
  String tableClass = isCanEdit ? "edbox" : "edline";

%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">


  <table BORDER="0" cellpadding="1" cellspacing="3" align='center'>

<tr>
    <td noWrap class="tableTitle">编码</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="ratio_code" VALUE="<%=row.get("ratio_code")%>" class="<%=tableClass%>" style="width:100" MAXLENGTH="<%=ds.getColumn("ratio_code").getPrecision()%>"  onKeyDown="return getNextElement();"  >
      </td>

    </tr>
    <tr>
     <td noWrap class="tableTitle">名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="ratio_name" VALUE="<%=row.get("ratio_name")%>" class="<%=tableClass%>" style="width:100" MAXLENGTH="<%=ds.getColumn("ratio_name").getPrecision()%>"  onKeyDown="return getNextElement();">
      </td>
      </tr>
    <tr>
    <%prodrow = prodBean.getLookupRow(row.get("cpid"));%>
      <td noWrap class="tableTitle">入库物资品名规格</td>
      <td nowrap class="td">
      <%if(isCanEdit){%>
     <INPUT TYPE="HIDDEN" NAME="cpid" VALUE="<%=row.get("cpid")%>">
     <input type="text" onKeyDown="return getNextElement();" name="cpbm" value='<%=prodrow.get("cpbm")%>' style="width:100" class="<%=tableClass%>" onchange="productCodeSelect(this)" >
     <input type="text" onKeyDown="return getNextElement();" name="product" value='<%=prodrow.get("product")%>' style="width:200" class="<%=tableClass%>" onchange="productNameSelect(this)" >
     <img style='cursor:hand' title='单选物资' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=cpbm&srcVar=product','fieldVar=cpid&fieldVar=cpbm&fieldVar=product',form1.cpid.value)">
     <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cpbm.value='';product.value='';"></td>
       <%}else{%>
      <input type="text" onKeyDown="return getNextElement();" name="cpbm" value='<%=prodrow.get("cpbm")%>' style="width:100" class="<%=tableClass%>"  readonly>
      <input type="text" onKeyDown="return getNextElement();" name="product" value='<%=prodrow.get("product")%>' style="width:200" class="<%=tableClass%>"  readonly>
 <%}%>
   </td>
     </tr>
      <tr>
    <%prodrow2=prodBean.getLookupRow(row.get("kc__cpid"));%>
      <td noWrap class="tableTitle">出库物资品名规格</td>
     <td nowrap class="td">
      <%if(isCanEdit){%>
     <INPUT TYPE="HIDDEN" NAME="kc__cpid" VALUE="<%=row.get("kc__cpid")%>">
     <input type="text" onKeyDown="return getNextElement();" name="kc_cpbm" value='<%=prodrow2.get("cpbm")%>' style="width:100" class="<%=tableClass%>" onchange="productHsbjSelect(this)" >
     <input type="text" onKeyDown="return getNextElement();" name="kc_product" value='<%=prodrow2.get("product")%>' style="width:200" class="<%=tableClass%>" onchange="productNameHsbjSelect(this)" >
     <img style='cursor:hand' title='单选物资' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=kc__cpid&srcVar=kc_cpbm&srcVar=kc_product','fieldVar=cpid&fieldVar=cpbm&fieldVar=product',form1.kc__cpid.value)">
     <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="kc__cpid.value='';kc_cpbm.value='';kc_product.value='';"></td>
       <%}else{%>
      <input type="text" onKeyDown="return getNextElement();" name="kc_cpbm" value='<%=prodrow2.get("cpbm")%>' style="width:100" class="<%=tableClass%>"  readonly>
      <input type="text" onKeyDown="return getNextElement();" name="kc_product" value='<%=prodrow2.get("product")%>' style="width:200" class="<%=tableClass%>"  readonly>
  <%}%>
     </tr>
   <td colspan="4" noWrap class="tableTitle"><br>
        <%if(isCanEdit){%><input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
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
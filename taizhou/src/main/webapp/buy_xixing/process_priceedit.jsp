<%--生产外加工价格编辑页面--%>
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
%>
<%String pageCode="process_price";
  if(!loginBean.hasLimits("process_price", request, response))
    return;
  engine.erp.buy.xixing.B_ProcessPrice   b_ProcessPriceBean = engine.erp.buy.xixing.B_ProcessPrice .getInstance(request);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function productCodeSelect(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function productNameSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function corpCodeSelect(obj)
{
  ProcessCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpNameSelect(obj)
{
  ProcessNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25  marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">外加工报价信息</TD>
  </tr></table>
<%String retu = b_ProcessPriceBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_ProcessPriceBean.getOneTable();
  RowMap row = b_ProcessPriceBean.getRowinfo();
  boolean isEdit = b_ProcessPriceBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);//在增加的时候又增加操作，否则必须有修改权限
  String readonly = isEdit ? "" : "readonly";
  String tableClass = isEdit ? "edbox" : "edline";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=row.get("dwtxid")%>'>
  <INPUT TYPE="HIDDEN" NAME="cpid" value='<%=row.get("cpid")%>'>
  <table BORDER="0" cellpadding="1" cellspacing="3">
<tr>
  <td noWrap class="tableTitle" align="left">加工厂</td>
    <%RowMap corpRow = corpBean.getLookupRow(row.get("dwtxid"));%>
     <td nowrap class="td">
      <input type="text" name="dwdm" value='<%=corpRow.get("dwdm")%>' style="width:80" class="<%=tableClass%>" onKeyDown="return getNextElement();" onchange="corpCodeSelect(this);" <%=readonly%>>
      <input type="text" name="dwmc" value='<%=corpBean.getLookupName(row.get("dwtxid"))%>' style="width:180" class="<%=tableClass%>" onKeyDown="return getNextElement();" onchange="corpNameSelect(this);" <%=readonly%>>
      <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProcessSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value)">
      <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
     </td>
</tr>

<tr>
    <%RowMap prodRow=prodBean.getLookupRow(row.get("cpid"));%>
    <td noWrap class="tableTitle">产品编码</td>
      <td nowrap class="td">
     <input type="text" onKeyDown="return getNextElement();" name="cpbm" value='<%=prodRow.get("cpbm")%>' style="width:80" class="<%=tableClass%>" onchange="productCodeSelect(this)" <%=readonly%>>
     <input type="text" onKeyDown="return getNextElement();" name="product" value='<%=prodRow.get("product")%>' style="width:180" class="<%=tableClass%>" onchange="productNameSelect(this)" <%=readonly%>>
     <img style='cursor:hand' title='单选物资' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw',form1.cpid.value)">
    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cpbm.value='';product.value='';jldw.value='';"></td>
     </td>

    </tr>
     <tr cellspacing="2">
    <%-- <td noWrap class="tableTitle">规格属性</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="sxz" VALUE="<%=row.get("sxz")%>" onKeyDown="return getNextElement();" style="width:180" MAXLENGTH="<%=ds.getColumn("sxz").getPrecision()%>" class="<%=tableClass%>" <%=readonly%>>
      </td>--%>
      <td align="center" noWrap class="tableTitle">计量单位</td>
      <td nowrap class="td"><input class="edline" name="jldw" value='<%=prodRow.get("jldw")%>' style="width:130" class="edline" onKeyDown="return getNextElement();" readonly>
      </td>
         <td  noWrap class="tdtitle" align="left">是否提供模具</td>
      <%String sftgmj = row.get("sftgmj");%>
      <td  noWrap class="td"> 是
      <input type="radio" name="sftgmj" value="1" <%=sftgmj.equals("1")?" checked" :""%>>
        否
      <input type="radio" name="sftgmj" value="2" <%=sftgmj.equals("2")?" checked" :""%>>
    </td>
    </tr>
    <tr>
     </td>
       <td noWrap class="tableTitle">加工费</td>
      <td noWrap class="td" >
      <INPUT TYPE="TEXT" NAME="dj" VALUE="<%=row.get("dj")%>" onKeyDown="return getNextElement();" style="width:180" MAXLENGTH="<%=ds.getColumn("dj").getPrecision()%>" class="<%=tableClass%>" <%=readonly%>>
      </td>


    <td noWrap class="tableTitle">优惠条件</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="yhtj" VALUE="<%=row.get("yhtj")%>" onKeyDown="return getNextElement();" style="width:130" MAXLENGTH="<%=ds.getColumn("yhtj").getPrecision()%>" class="<%=tableClass%>" <%=readonly%>>
      </td>
    </tr>
    <tr>
    <%--<td noWrap class="tableTitle">加工费</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="yhtj" VALUE="<%=row.get("yhtj")%>" onKeyDown="return getNextElement();" style="width:180" MAXLENGTH="<%=ds.getColumn("yhtj").getPrecision()%>" class="<%=tableClass%>" <%=readonly%>>
      </td>--%>

    </tr>
    <tr>
      <td noWrap class="tableTitle">对方产品号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dfcph" VALUE="<%=row.get("dfcph")%>" onKeyDown="return getNextElement();" style="width:180" MAXLENGTH="<%=ds.getColumn("dfcph").getPrecision()%>" class="<%=tableClass%>" <%=readonly%>>
      </td>
      <td noWrap class="tableTitle">报价日期</td>
       <td class="td" nowrap> <input type="text"  style="width:75" onKeyDown="return getNextElement();" name="bjrq" id="bjrq"value='<%=row.get("bjrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)">
       <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.bjrq);"></a>
       </td>
    </tr>
    <tr>


        <td noWrap class="tableTitle">备注</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="bz" VALUE="<%=row.get("bz")%>" onKeyDown="return getNextElement();" style="width:260" MAXLENGTH="<%=ds.getColumn("bz").getPrecision()%>" class="<%=tableClass%>" <%=readonly%>>

      </td>


    <tr align="center">
      <td colspan="2" noWrap class="tableTitle" ><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value='保存(S)'>
        <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(X)">
        <pc:shortcut key="x" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
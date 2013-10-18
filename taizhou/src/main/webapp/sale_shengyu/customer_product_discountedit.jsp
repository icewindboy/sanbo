<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.*"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
  String op_provider="op_provider";
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<%String pageCode = "customer_product_discount";
  if(!loginBean.hasLimits("customer_product_discount", request, response))
    return;
  engine.erp.sale.shengyu.B_CustomerProductDiscount  b_customerproductdiscountBean = engine.erp.sale.shengyu.B_CustomerProductDiscount.getInstance(request);
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
//客户编码
function customerCodeSelect(obj)
{
    CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户名称
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//产品编码
function productCodeSelect(obj)
{
    SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=product&srcVar=cpbm&srcVar=jldw&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fieldVar=jldw',obj.value);
}
//产品名称
function productNameSelect(obj)
{
    SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=product&srcVar=cpbm&srcVar=jldw&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fieldVar=jldw',obj.value);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">客户产品价格</TD>
  </tr></table>
<%
  String retu = b_customerproductdiscountBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_customerproductdiscountBean.getOneTable();
  RowMap row = b_customerproductdiscountBean.getRowinfo();

  String cpid = row.get("cpid");
  RowMap productRow = prodBean.getLookupRow(cpid);
  String isprops=productRow.get("isprops");
  RowMap corpRow = corpBean.getLookupRow(row.get("dwtxId"));

  boolean isEdit = (b_customerproductdiscountBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));//在增加的时候又增加操作，否则必须有修改权限
  boolean isHistory=ds.getValue("sflsbj").equals("1");
  String readonly = (isEdit && !isHistory)||b_customerproductdiscountBean.isAdd? "" : "readonly";
  String tableClass = (isEdit && !isHistory)||b_customerproductdiscountBean.isAdd? "edbox" : "edline";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <TR>
      <TD align="center" nowrap  class="tableTitle">往来单位</TD>
      <td nowrap class="td" colspan=3>
      <input class="<%=tableClass%>" type="text" name="dwdm" class="edbox" style="width:100" onKeyDown="return getNextElement();"  value='<%=corpRow.get("dwdm")%>' onchange="customerCodeSelect(this)" <%=readonly%>>
      <input class="<%=tableClass%>" type="text" name="dwmc"  style="width:220" value='<%=corpRow.get("dwmc")%>' class="edbox"  onchange="customerNameSelect(this)" <%=readonly%> >
      <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=row.get("dwtxId")%>'>
    <%--  <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect'form1','srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc','fieldVar=dwtxid&fieldVar=product&fieldVar=cpbm',form1.cpid.value()">--%>
      <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value)">
      <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwdm.value='';dwmc.value='';">
      </td>
     </tr>
      <TR>
          <TD align="center" nowrap  class="tableTitle">产品</TD>
          <td nowrap class="td" colspan="3">
          <INPUT TYPE="HIDDEN" NAME="cpid" value="<%=row.get("cpid")%>">
          <input class="<%=tableClass%>" style="WIDTH:100" name="cpbm" value='<%=productRow.get("cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)" >
          <INPUT class="<%=tableClass%>" style="WIDTH:220" id="product" name="product" value='<%=productRow.get("product")%>' maxlength='10' onKeyDown="return getNextElement();"  onchange="productNameSelect(this)" <%=readonly%>>
          <img style='cursor:hand' src='../images/view.gif' border=0 onClick="SaleProdSingleSelect('form1','srcVar=cpid&srcVar=product&srcVar=cpbm&srcVar=jldw&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fieldVar=jldw',form1.cpid.value)" <%=readonly%>>
          <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="form1.cpid.value='';form1.product.value='';form1.cpbm.value='';">
          </td>
      </tr>

     <tr>
      <td noWrap class="tableTitle">&nbsp;价格</td>
      <td noWrap class="td"><INPUT class="<%=tableClass%>" TYPE="TEXT" NAME="dj" VALUE="<%=row.get("dj")%>" style="width:180" MAXLENGTH="<%=ds.getColumn("dj").getPrecision()%>" class="edbox"  onKeyDown="return getNextElement();"  <%=readonly%>>
      </td>
      <td noWrap class="tableTitle">&nbsp;单位</td>
      <td nowrap class="td"><input class="edline" name="jldw" value='<%=productRow.get("jldw")%>' style="width:100" class="edline" onKeyDown="return getNextElement();" readonly>
      </td>
     </tr>


     <tr>
      <td noWrap class="tableTitle">报价时间</td>
      <TD class="td" nowrap><INPUT class="<%=tableClass%>" style="WIDTH: 100px" name="ksrq" value='<%=row.get("ksrq")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=readonly%>>
     <%if(b_customerproductdiscountBean.isAdd||!isHistory){%><A href="#"><IMG title=选择日期 onClick="selectDate(ksrq);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0><%}%></A></TD>
      <INPUT type='hidden' name="jsrq" value='<%=row.get("jsrq")%>'></td>
      <INPUT type='hidden' name="ksrq2" value='<%=ds.getValue("ksrq")%>'></td>
    </tr>

     <tr>
      <td noWrap class="tableTitle">&nbsp;备注</td>
      <td noWrap class="td"><INPUT class="<%=tableClass%>" TYPE="TEXT" NAME="bz" VALUE="<%=row.get("bz")%>" style="width:180" MAXLENGTH="<%=ds.getColumn("bz").getPrecision()%>" class="edbox"  onKeyDown="return getNextElement();" <%=readonly%>>
      </td>
    </tr>

      <tr>
      <%if(!b_customerproductdiscountBean.isAdd){%>
      <td noWrap class="tableTitle" colspan='2' align='left'>修改前的报价存为历史报价<input type="checkbox" name="sflsbj" value="1"  <%=ds.getValue("sflsbj").equals("1") ? "checked disabled" : ""%>></td>
      <%}%>
   </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <%String s = "sumitForm("+Operate.POST+");";%>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
        <pc:shortcut key="s" script="<%=s%>" />
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value="关闭(X)">
        <pc:shortcut key="x" script="parent.hideFrameNoFresh()" />
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
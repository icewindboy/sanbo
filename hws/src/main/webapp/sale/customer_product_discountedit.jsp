<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
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
<%if(!loginBean.hasLimits("customer_product_discount", request, response))
    return;
  engine.erp.sale.B_CustomerProductDiscount  b_customerproductdiscountBean = engine.erp.sale.B_CustomerProductDiscount.getInstance(request);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性

%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">客户产品折扣</TD>
  </tr></table>
<%
  String retu = b_customerproductdiscountBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_customerproductdiscountBean.getOneTable();
  RowMap row = b_customerproductdiscountBean.getRowinfo();
  propertyBean.regData(ds,"dmsxID");
  String cpid = row.get("cpid");
  RowMap productRow = prodBean.getLookupRow(cpid);
  String isprops=productRow.get("isprops");
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=row.get("dwtxId")%>'>
  <INPUT TYPE="HIDDEN" NAME="cpid" value='<%=row.get("cpid")%>'>
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;客户</td>
      <td nowrap class="td">
     <input type="text" name="dwmc" value='<%=corpBean.getLookupName(row.get("dwtxid"))%>' style="width:180" class="edline" readonly>
     <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc','fieldVar=dwtxid&fieldVar=dwmc',form1.dwtxid.value)">
     <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';">
      </td>
     </tr>
    <tr>
    <td noWrap class="tableTitle">&nbsp;品名 规格</td>
      <td nowrap class="td"><input type="text" name="pm" value='<%=prodBean.getLookupName(row.get("cpid"))%>' style="width:180" class="edline" readonly>
       <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=pm','fieldVar=cpid&fieldVar=pm',form1.cpid.value)">
       <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';pm.value='';">
     </td>
    </tr>
    <tr>
    <td noWrap class="tableTitle">&nbsp;规格属性</td>
    <td class="td" nowrap>
     <input type="hidden" name="dmsxid" value="<%=row.get("dmsxid")%>">
    <input  type="text" class="ednone"   onKeyDown="return getNextElement();"  name="sxz" value='<%=propertyBean.getLookupName(row.get("dmsxid"))%>' onchange=""  >
    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if('<%=row.get("cpid")%>'==''){alert('请先输入产品');return;}if('<%=isprops%>'=='0'){alert('该产品无规格属性!');return;}PropertySelect('form1','dmsxid','sxz','<%=cpid%>')">
    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid.value='';sxz.value='';">
    </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;客户产品编号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="khcpdm" VALUE="<%=row.get("khcpdm")%>" style="width:180" MAXLENGTH="<%=ds.getColumn("khcpdm").getPrecision()%>" class="edbox"  onKeyDown="return getNextElement();">
      </td>
     </tr>
    <tr>
    <td noWrap class="tableTitle">&nbsp;单价</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dj" VALUE="<%=row.get("dj")%>" style="width:180" MAXLENGTH="<%=ds.getColumn("dj").getPrecision()%>" class="edbox"  onKeyDown="return getNextElement();">
      </td>
    </tr>
     <tr>
      <td noWrap class="tableTitle">&nbsp;折扣</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="zk" VALUE="<%=row.get("zk")%>" style="width:180" MAXLENGTH="<%=ds.getColumn("zk").getPrecision()%>" class="edbox"  onKeyDown="return getNextElement();">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;报价</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="bj" VALUE="<%=row.get("bj")%>" style="width:180" MAXLENGTH="<%=ds.getColumn("bj").getPrecision()%>" class="edbox"  onKeyDown="return getNextElement();">
      </td>
    </tr>
    <tr>
    <td noWrap class="tableTitle">&nbsp;奖金比率(%)</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="jjbl" VALUE="<%=row.get("jjbl")%>" style="width:180" MAXLENGTH="<%=ds.getColumn("jjbl").getPrecision()%>" class="edbox"  onKeyDown="return getNextElement();">
      </td>
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
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,engine.erp.sale.*" %>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("customer_credit_list", request, response))
    return;
  CustomerCreditBean customercreditbean = CustomerCreditBean.getInstance(request);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

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
    <TD NOWRAP align="center">客户信誉额度</TD>
  </tr></table>
<%String retu = customercreditbean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = customercreditbean.getOneTable();
  RowMap row = customercreditbean.getRowinfo();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=row.get("dwtxId")%>'>
  <INPUT TYPE="HIDDEN" NAME="xyedID" value='<%=row.get("xyedID")%>'>
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
    <TD align="center" nowrap  class="tableTitle">&nbsp;往来单位</TD>
      <td nowrap class="td">
     <input type="text" name="dwmc" value='<%=corpBean.getLookupName(row.get("dwtxid"))%>' style="width:180" class="edline" readonly>
     <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc','fieldVar=dwtxid&fieldVar=dwmc',form1.dwtxid.value)">
      </td>
     </tr>
     <tr>
      <td align="center" noWrap class="tableTitle">&nbsp;信誉额度</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="xyed" VALUE="<%=row.get("xyed")%>" style="width:180" MAXLENGTH="<%=ds.getColumn("xyed").getPrecision()%>" class="edbox"  onKeyDown="return getNextElement();">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;信誉等级</td>
      <td noWrap class="td">
     <INPUT TYPE="TEXT" NAME="xydj" VALUE="<%=row.get("xydj")%>" style="width:180" MAXLENGTH="<%=ds.getColumn("xydj").getPrecision()%>" class="edbox"  onKeyDown="return getNextElement();">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;回款天数</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="hkts" VALUE="<%=row.get("hkts")%>" style="width:180" MAXLENGTH="<%=ds.getColumn("hkts").getPrecision()%>" class="edbox"  onKeyDown="return getNextElement();">
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 ">
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
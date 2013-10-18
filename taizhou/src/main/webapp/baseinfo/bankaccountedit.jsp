<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.*"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("bankaccountlist", request, response))
    return;
  engine.erp.baseinfo.B_BankAccount accountBean = engine.erp.baseinfo.B_BankAccount.getInstance(request);
  LookUp bankBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_BANK);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script>
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">银行帐号信息</TD>
  </tr></table>
<%String retu = accountBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = accountBean.getOneTable();
  RowMap row = accountBean.getRowinfo();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">&nbsp;银&nbsp;行&nbsp;</td>
      <td noWrap class="td"><pc:select name="yhid" style="width:240">
       <%=bankBean.getList(row.get("yhid"))%></pc:select>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;帐&nbsp;号&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="zh" VALUE='<%=row.get("zh")%>' style="width:240" MAXLENGTH='<%=ds.getColumn("zh").getPrecision()%>' CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;帐号名称&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="zhmc" VALUE='<%=row.get("zhmc")%>' style="width:240" MAXLENGTH='<%=ds.getColumn("zhmc").getPrecision()%>' CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;帐户类型&nbsp;</td>
      <td noWrap class="td"><%boolean lx = row.get("zhlx").equals("2");%>
        <INPUT TYPE=RADIO NAME="zhlx" VALUE="1"<%=lx ? "" : " checked"%>>现金帐户&nbsp;
        <INPUT TYPE=RADIO NAME="zhlx" VALUE="2"<%=lx ? " checked" : ""%>>银行帐户</td>
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +")"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value="关闭(C)">
        <pc:shortcut key="c" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
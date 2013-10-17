<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("foreignCurrencyList", request, response))
    return;
  engine.erp.baseinfo.B_ForeignCurrency foreignCurrencyBean = engine.erp.baseinfo.B_ForeignCurrency.getInstance(request);
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
<%String retu = foreignCurrencyBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = foreignCurrencyBean.getOneTable();
  RowMap row = foreignCurrencyBean.getRowinfo();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">&nbsp;编码&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dm" VALUE='<%=row.get("dm")%>' style="width:240" MAXLENGTH='<%=ds.getColumn("mc").getPrecision()%>' CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;外币名称&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="mc" VALUE='<%=row.get("mc")%>' style="width:240" MAXLENGTH='<%=ds.getColumn("mc").getPrecision()%>' CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;外币符号&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="fh" VALUE='<%=row.get("fh")%>' style="width:240" MAXLENGTH='<%=ds.getColumn("fh").getPrecision()%>' CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;汇&nbsp;率&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="hl" VALUE='<%=row.get("hl")%>' style="width:240" MAXLENGTH='<%=ds.getColumn("hl").getPrecision()%>' CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;报价方式&nbsp;</td>
      <td noWrap class="td">
        <%boolean lx = row.get("ff").equals("2");%>
        <INPUT TYPE=RADIO NAME="ff" VALUE="1"<%=lx ? "" : " checked"%>>直接汇率法(外币X汇率=本币)<br>
        <INPUT TYPE=RADIO NAME="ff" VALUE="2"<%=lx ? " checked" : ""%>>间接汇率法(外币/汇率=本币)</td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;固定汇率&nbsp;</td>
      <td noWrap class="td"><%lx = row.get("gd").equals("0");%>
        <INPUT TYPE=RADIO NAME="gd" VALUE="1"<%=lx ? "" : " checked"%>>是&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <INPUT TYPE=RADIO NAME="gd" VALUE="0"<%=lx ? " checked" : ""%>>否</td>
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+");"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value="关闭(C)">
        <pc:shortcut key="c" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
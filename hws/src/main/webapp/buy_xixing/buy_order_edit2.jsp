<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("buy_order", request, response))
    return;
  engine.erp.buy.xixing.Buy_DefItem httkBean = engine.erp.buy.xixing.Buy_DefItem.getInstance(request);
  String retu = httkBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = httkBean.getOneTable();
  RowMap row = httkBean.getRowinfo();
  String asdf=ds.getValue("itemvalue");
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();   //提交
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">默认条款设置</TD>
  </TR></TABLE>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <TABLE BORDER="0" CELLPADDING="1" CELLSPACING="3">
    <TR>
      <td noWrap class="tableTitle">条款内容</td>
  <%--    <td noWrap class="td"><INPUT TYPE="TEXT" NAME="itemvalue" VALUE="<%=row.get("itemvalue")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("itemvalue").getPrecision()%>" CLASS="edbox">--%>
      <td class="td" align="right"><textarea name="itemvalue" cols="110" rows="10" ><%=ds.getValue("itemvalue")%></textarea></td>
      </td>
    </TR>
    <TR>
      <td colspan="2" noWrap class="tableTitle"><br> <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+");"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value="关闭(C)">
        <pc:shortcut key="c" script='parent.hideFrameNoFresh()'/>
      </td>
    </TR>
  </TABLE>
</form><%out.print(retu);%>
</BODY>
</Html>
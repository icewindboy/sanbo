<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("wordbooklist", request, response))
    return;
  engine.erp.baseinfo.B_Wordbook workbookBean = engine.erp.baseinfo.B_Wordbook.getInstance(request);
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
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR><TD NOWRAP align="center">辅助信息</TD></TR></TABLE>
<%String retu = workbookBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = workbookBean.dsWorkbook;
  RowMap row = workbookBean.rowInfo;
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <TABLE BORDER="0" CELLPADDING="1" CELLSPACING="3">
    <TR>
      <td noWrap class="tableTitle">&nbsp;编&nbsp;码&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dm" VALUE="<%=row.get("dm")%>" SIZE="40" MAXLENGTH="6" CLASS="edbox">
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">&nbsp;名&nbsp;称&nbsp;</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="mc" VALUE="<%=row.get("mc")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("mc").getPrecision()%>" CLASS="edbox">
      </td>
    </TR>
    <TR>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=workbookBean.OPERATE_POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+workbookBean.OPERATE_POST+");"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value="关闭(C)">
        <pc:shortcut key="c" script='parent.hideFrameNoFresh()'/>
      </td>
    </TR>
  </TABLE>
</form>
</BODY>
</Html>
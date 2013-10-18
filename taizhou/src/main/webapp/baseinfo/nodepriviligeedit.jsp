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
<%if(!loginBean.hasLimits("nodepriviligelist", request, response))
    return;
  engine.erp.system.B_nodePrivilige nodePrivilige = engine.erp.system.B_nodePrivilige.getInstance(request);
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
    <TD NOWRAP align="center">权限信息</TD>
  </TR></TABLE>
<%String retu = nodePrivilige.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = nodePrivilige.getOneTable();
  RowMap row = nodePrivilige.getRowinfo();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <TABLE BORDER="0" CELLPADDING="1" CELLSPACING="3">
    <TR>
      <td noWrap class="tableTitle">&nbsp;权限编码</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="priviligeCode" VALUE="<%=row.get("priviligeCode")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("priviligeCode").getPrecision()%>" CLASS="edbox">
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">&nbsp;权限名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="priviligeName" VALUE="<%=row.get("priviligeName")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("priviligeName").getPrecision()%>" CLASS="edbox">
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">&nbsp;权限描述</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="priviligeMemo" VALUE="<%=row.get("priviligeMemo")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("priviligeMemo").getPrecision()%>" CLASS="edbox"></td>
    </TR>
    <TR>
      <td colspan="2" noWrap class="tableTitle"><br> <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+");"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value="关闭(C)">
        <pc:shortcut key="c" script='parent.hideFrameNoFresh()'/>
      </td>
    </TR>
  </TABLE>
</form>
</BODY>
</Html>
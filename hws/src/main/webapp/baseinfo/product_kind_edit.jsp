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
<%if(!loginBean.hasLimits("product_kind", request, response))
    return;
  engine.erp.baseinfo.B_ProductKind prodKindBean =  engine.erp.baseinfo.B_ProductKind.getInstance(request);
  String retu = prodKindBean.doService(request, response);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function submitTree()
{
  parent.depttree.form1.operate.value = '';
  parent.depttree.form1.submit();
}
</script>
<%if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = prodKindBean.dsProductKindData;
  RowMap row = prodKindBean.rowInfo;
%>
<BODY oncontextmenu="window.event.returnValue=<%=true%>">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR><TD NOWRAP>&nbsp;</TD></TR></TABLE>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <TABLE BORDER="0" CELLPADDING="1" CELLSPACING="3">
    <TR>
      <td noWrap class="tableTitle">类别编码</td>
      <td noWrap class="td" > <TABLE CELLSPACING="0" CELLPADDING="0" BORDER="0">
          <TR VALIGN=MIDDLE>
            <TD NOWRAP CLASS="td"><%=row.get("parentCode")%>-
              <INPUT TYPE="TEXT" NAME="self_code" VALUE="<%=row.get("self_code")%>" SIZE="<%=prodKindBean.getNodeLevelLength()%>" MAXLENGTH="<%=prodKindBean.getNodeLevelLength()%>"  CLASS="edbox"></TD>
          </TR>
        </TABLE></td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">类别名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="mc" VALUE="<%=row.get("mc")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("mc").getPrecision()%>" CLASS="edbox">
      </td>
    </TR>
    <TR>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm('<%=prodKindBean.NODE_EDIT_POST%>');" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+prodKindBean.NODE_EDIT_POST+");"%>'/>
        <input name="button2" type="button" class="button" onClick="location.href='../blank.htm'" value="返回(C)">
        <pc:shortcut key="c" script="location.href='../blank.htm'"/>
      </td>
    </TR>
  </TABLE>
</form>
<%out.print(retu);%>
</BODY>
</Html>
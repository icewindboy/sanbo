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
<%if(!loginBean.hasLimits("fplblist", request, response))
    return;
  engine.erp.baseinfo.B_Fplb fplbBean = engine.erp.baseinfo.B_Fplb.getInstance(request);
  String retu = fplbBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = fplbBean.getOneTable();
  RowMap row = fplbBean.getRowinfo();
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
    <TD NOWRAP align="center">发票类别</TD>
  </TR></TABLE>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <TABLE BORDER="0" CELLPADDING="1" CELLSPACING="3">
    <TR>
      <td noWrap class="tableTitle">发票名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="mc" VALUE="<%=row.get("mc")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("mc").getPrecision()%>" CLASS="edbox">
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">税率%</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="sl" VALUE="<%=row.get("sl")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("sl").getPrecision()%>" CLASS="edbox">
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">使用类型</td>
      <td noWrap class="td"><pc:select name='sylx' value='<%=row.get("sylx")%>' style='width:250'><pc:option value="1">采购</pc:option><pc:option value="2">销售</pc:option></pc:select>
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">排序号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="pxh" VALUE="<%=row.get("pxh")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("pxh").getPrecision()%>" CLASS="edbox"></td>
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
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
<%if(!loginBean.hasLimits("rolelist", request, response))
    return;
  engine.erp.system.B_Role roleBean = engine.erp.system.B_Role.getInstance(request);
  engine.erp.system.B_LimitsInfo limitsInfoBean = engine.erp.system.B_LimitsInfo.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script>
function sumitForm(oper, row)
{
  var code = form1.rolecode.value = form1.rolecode.value.trim();
  var name = form1.rolename.value = form1.rolename.value.trim();
  if(code == '')
  {
    alert("角色编码不能为空！");
    return;
  }
  if(name == '')
  {
    alert("角色名称不能为空！");
    return;
  }
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function GotoNode(nodeid, isNode)
{
  for(i=0; i<form1.chklimit.length; i++)
  {
    var chkvalue = form1.chklimit[i].value;
    var index = chkvalue.indexOf("_");
    if(index < 0)
      continue;
    if(chkvalue.substring(0, index) == nodeid)
    {
      form1.chklimit[i].checked = !form1.chklimit[i].checked;
      chklimit_onchange();
    }
  }
}
function chklimit_onchange()
{
  form1.limitchange.value="1";
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR><TD NOWRAP align="center">角色信息</TD></TR></TABLE>
<%String retu = roleBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  RowMap row = roleBean.rowInfo;
  EngineDataSet ds = roleBean.dsRole;
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="limitchange" VALUE="0">
	<table BORDER="0" CELLPADDING="0" CELLSPACING="0" width="760" align="center">
		<tr>
			<td>
			  <TABLE BORDER="0" CELLPADDING="1" CELLSPACING="3" width="100%">
			    <TR>
            <td noWrap class="tableTitle">角色编码</td>
			      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="rolecode" VALUE="<%=row.get("rolecode")%>" SIZE="15" MAXLENGTH="6" CLASS="edbox"></td>
            <td noWrap class="tableTitle">角色名称</td>
			      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="rolename" VALUE="<%=row.get("rolename")%>" SIZE="30" MAXLENGTH="<%=ds.getColumn("rolename").getPrecision()%>" CLASS="edbox"></td>
            <td noWrap class="tableTitle">角色描述</td>
			      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="memo" VALUE="<%=row.get("memo")%>" SIZE="50" MAXLENGTH="<%=ds.getColumn("memo").getPrecision()%>" CLASS="edbox"></td>
		    	</TR>
		    </TABLE>
			</td>
		</tr>
		<tr>
			<td><%=roleBean.nodeTreeInfo%></td>
    </tr>
    <tr>
      <td noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=roleBean.OPERATE_POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+roleBean.OPERATE_POST+");"%>'/>
        <input name="button2" type="button" class="button" onClick="location.href='rolelist.jsp'" value="返回(C)">
        <pc:shortcut key="c" script="location.href='rolelist.jsp'"/>
      </td>
    </tr>
	</table>
</form>
</BODY>
</Html>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*"%>
<html>
<head>
<title>角色列表</title>
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

<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function showInterFrame(oper, rownum){
  showFrame('interframe1', true, url);
}
function hideInterFrame(){
  hideFrame('interframe1');
  form1.submit();
}
function hideFrameNoFresh(){
  hideFrame('interframe1');
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">角色信息列表</TD>
  </TR></TABLE>
<%String retu = roleBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  if(!roleBean.dsRole.isOpen())
    roleBean.dsRole.open();

  EngineDataView list = roleBean.dsRole.cloneEngineDataView();
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tableview1" width="760" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <COLGROUP width=80 valign="middle">
    <COLGROUP width=150 valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP width=60 align="center">
    <tr class="tableTitle">
      <td>角色编号</td>
      <td>角色名称</td>
      <td>角色描述</td>
      <td><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="sumitForm(<%=roleBean.OPERATE_ADD%>,-1)" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='<%="sumitForm("+ roleBean.OPERATE_ADD +",-1)"%>'/>
      </td>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
    %>
    <tr onDblClick="sumitForm(<%=roleBean.OPERATE_EDIT%>,<%=i%>)">
      <td class="td" nowrap><%=list.format("rolecode")%></td>
      <td class="td"><%=list.format("rolename")%></td>
      <td class="td"><%=list.format("memo")%></td>
      <td class="td"><input name="image2" class="img" type="image" title="修改" onClick="sumitForm(<%=roleBean.OPERATE_EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=roleBean.OPERATE_DEL%>,<%=i%>)" src="../images/del.gif" border="0">
      </td>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
	<script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</script>
  </form>
  <iframe id="interframe1" src="" class="frameBox" width="350" height="150" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
</body>
</html>

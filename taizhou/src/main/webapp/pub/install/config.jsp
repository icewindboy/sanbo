<%@ page contentType="text/html; charset=UTF-8" %>
<%engine.erp.common.install.PatchSystem patchSystem = engine.erp.common.install.PatchSystem.getInstance(request);
  if(!patchSystem.isLocalHost(request, getServletContext().getRealPath("/WEB-INF")))
  {
    out.print("只能在本机配置程序！");
    return;
  }
  session.setMaxInactiveInterval(600);
  String retu = patchSystem.doService(request, response);
  String curUrl = request.getRequestURL().toString();
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../../scripts/validate.js"></script>
<script language="javascript">
function submitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <br>
  <table align="center" WIDTH="500" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"><b>配置数据库连接</b></td>
  </tr></table><br>
  <table id="tableview1" border="0" cellpadding=1 cellspacing=1 class="table" align="center" width="500">
    <tr>
      <td class="td"><b>Driver</b></td><%String driver = patchSystem.getPropertyValue("driver");
      if(driver==null ||driver.length() == 0) driver = "oracle.jdbc.driver.OracleDriver";%>
      <td class="td"><input type="text" name="driver" value='<%=driver%>' class="edbox" size="50"></td>
    </tr>
    <tr>
      <td class="td"><b>数据库连接的URL</b><br>例:jdbc:oracle:thin:@IP:1521:SID</td>
      <td> <input type="text" name="url" value='<%=patchSystem.getPropertyValue("url")%>' class="edbox" size="50" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>连接数据库的用户名</b><br>(不存在将自动创建)</td>
      <td><input type="text" name="username" value='<%=patchSystem.getPropertyValue("username")%>' class="edbox" size="50" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>连接数据库的用户密码</b></td>
      <td><input type="text" name="password" value='<%=patchSystem.getPropertyValue("password")%>' class="edbox" size="50" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>表空间名称</b><br>(不存在将自动创建)</td>
      <td><input type="text" name="tablespace" value='<%String table= patchSystem.getPropertyValue("tablespace"); out.print(table.length()==0 ? "ENGINE_TB" : table);%>' class="edbox" size="50" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>索引空间名称</b><br>(不存在将自动创建)</td>
      <td><input type="text" name="tableindex" value='<%String index= patchSystem.getPropertyValue("tableindex"); out.print(index.length()==0 ? "ENGINE_IDX" : index);%>' class="edbox" size="50" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>连接池最小连接数</b></td>
      <td><input type="text" name="minnumber" value='<%=patchSystem.getPropertyValue("minnumber")%>' class="edbox" size="50" maxlength="5" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>连接池最大连接数</b></td>
      <td><input type="text" name="maxnumber" value='<%=patchSystem.getPropertyValue("maxnumber")%>' class="edbox" size="50" maxlength="5" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td colspan="2" align="center">
         <input type="button" name="Submit" class="button" value=" 保存配置 " onClick="submitForm(<%=patchSystem.CONFIG%>)">
      </td>
    </tr>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',0);</SCRIPT>
</form>
<%=retu%>
</BODY>
</Html>
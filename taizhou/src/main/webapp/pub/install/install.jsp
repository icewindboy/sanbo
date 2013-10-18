<%@ page contentType="text/html; charset=UTF-8" %>
<%
engine.erp.common.install.InstallSystem installSystem = engine.erp.common.install.InstallSystem.getInstance(request);
//  if(!installSystem.isLocalHost(request, getServletContext().getRealPath("/WEB-INF")))
//  {
//    out.print("只能在本机安装程序！");
//    return;
//  }
  session.setMaxInactiveInterval(1200);
  String retu = installSystem.doService(request, response);
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
function continueDlg()
{
  if(confirm('有错误信息是否继续安装?'))
    submitForm(<%=installSystem.POST_CONTINUE%>);
}
function errorinfo()
{
  alert('安装过程中有错误信息, 请查看日志文件(engine.log)。');
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <br>
  <table align="center" WIDTH="500" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"><b>数据库配置</b></td>
  </tr></table><br>
  <table id="tableview1" border="0" cellpadding=1 cellspacing=1 class="table" align="center" width="500">
    <tr>
      <td class="td"><b>Driver</b></td><%String driver = installSystem.getPropertyValue("driver");
      if(driver==null ||driver.length() == 0) driver = "oracle.jdbc.driver.OracleDriver";%>
      <td class="td"><input type="text" name="driver" value='<%=driver%>' class="edbox" size="50"></td>
    </tr>
    <tr>
      <td class="td"><b>数据库连接的URL</b><br>例:jdbc:oracle:thin:@IP:1521:SID</td>
      <td> <input type="text" name="url" value='<%=installSystem.getPropertyValue("url")%>' class="edbox" size="50" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>SYS用户名</b></td>
      <td><input type="text" name="sysuser" value="sys" class="edbox" size="50" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>SYS密码</b></td>
      <td><input type="text" name="syspassword" value='' class="edbox" size="50" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>连接数据库的用户名</b><br>(不存在将自动创建)</td>
      <td><input type="text" name="username" value='<%=installSystem.getPropertyValue("username")%>' class="edbox" size="50" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>连接数据库的用户密码</b></td>
      <td><input type="text" name="password" value='<%=installSystem.getPropertyValue("password")%>' class="edbox" size="50" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>表空间名称</b><br>(不存在将自动创建)</td>
      <td><input type="text" name="tablespace" value='<%=installSystem.getPropertyValue("tablespace")%>' class="edbox" size="50" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>索引空间名称</b><br>(不存在将自动创建)</td>
      <td><input type="text" name="tableindex" value='<%=installSystem.getPropertyValue("tableindex")%>' class="edbox" size="50" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>连接池最小连接数</b></td>
      <td><input type="text" name="minnumber" value='<%=installSystem.getPropertyValue("minnumber")%>' class="edbox" size="50" maxlength="5" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td class="td"><b>连接池最大连接数</b></td>
      <td><input type="text" name="maxnumber" value='<%=installSystem.getPropertyValue("maxnumber")%>' class="edbox" size="50" maxlength="5" onKeyDown="return getNextElement();"></td>
    </tr>
    <tr>
      <td colspan="2" align="center">
         <input type="button" name="Submit" class="button" value=" 只配置用户 " onClick="submitForm(<%=installSystem.ONLY_CONFIG%>)">
         </td>
    </tr>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',0);</SCRIPT>
</form>
<%=retu%>
</BODY>
</Html>
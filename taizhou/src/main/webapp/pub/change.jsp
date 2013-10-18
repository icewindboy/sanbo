<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import = "java.math.BigDecimal"%><%
  String retu = loginBean.doPost(request, response);
  if(retu.equals("ok"))
  {
    String show = loginBean.showJavaScript("alert('修改密码成功！');location.href='public.jsp';");
    out.print(show);
    return;
  }
%>
<html>
<head>
<title>设置新密码</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
  function submitForm(oper, row)
  {
    if(strlen(form1.oldpass.value) > 32)
    {
      alert("您输入的旧密码太长了！");
      return;
    }
    if(strlen(form1.newpass.value) > 32)
    {
      alert("您输入的新密码太长了！");
      return;
    }
    if(strlen(form1.cofirmpass.value) > 32)
    {
      alert("您输入的确认密码太长了！");
      return;
    }
    lockScreenToWait("处理中, 请稍候！");
    var oldpass = form1.oldpass.value;
    var newpass = form1.newpass.value;
    var cofirmpass = form1.cofirmpass.value;
    try{
      form1.oldpass.value = toolobj.encode(oldpass);
      form1.newpass.value = toolobj.encode(newpass);
      form1.cofirmpass.value = toolobj.encode(cofirmpass);
      form1.encode.value = "1";
    }
    catch(err){
      form1.encode.value = "0";
      form1.oldpass.value = oldpass;
      form1.newpass.value = newpass;
      form1.cofirmpass.value = cofirmpass;
    }
    form1.operate.value = oper;
    form1.submit();
  }
  function strlen(str)
  {
    var i;
    var len;
    len = 0;
    for (i=0; i<str.length; i++)
      len += str.charCodeAt(i)>255 ? 2 : 1;
    return len;
  }
</script>
</script>
<BODY oncontextmenu="window.event.returnValue=false">

<form name="form1" method="post" action="change.jsp" onsubmit="return false">
  <input type="hidden" name="operate" value="">
  <input type="hidden" NAME="encode" value="1">
  <table width="100%" height="100%" cellpadding="0" cellspacing="5">
    <tr>
      <td align="center" valign="middle"> <table border=0 CELLSPACING=0 CELLPADDING=0 class="table" width="240">
          <tr>
            <td class="activeVTab" height="20">设置新密码</td>
          </tr>
        </table>
        <table style="border:1px solid #104a7b; background-color:#f0f0f0;" CELLSPACING=1 CELLPADDING=0 align="center" width="240">
          <tr>
            <td><table CELLSPACING="2" CELLPADDING="0" BORDER="0" bgcolor="#f0f0f0">
                <tr>
                  <td noWrap class="tdTitle">&nbsp;用 户 名&nbsp;</td>
                  <td noWrap class="td"><%=loginBean.getLoginUser()%></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">&nbsp;旧 密 码&nbsp;</td>
                  <td noWrap class="td"><input type="password" class="edbox" style="width:160" name="oldpass" value='' maxlength='32' onKeyDown="return getNextElement();"></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">&nbsp;新 密 码&nbsp;</td>
                  <td noWrap class="td"><input type="password" class="edbox" style="width:160" name="newpass" value='' maxlength='32' onKeyDown="return getNextElement();"></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">&nbsp;确认密码&nbsp;</td>
                  <td noWrap class="td"><input type="password" class="edbox" style="width:160" name="cofirmpass" value='' maxlength='32' onKeyDown="return getNextElement();"></td>
                </tr>
                <tr>
                  <td colspan="2" noWrap class="tdTitle"><br>
                    <INPUT type="button" name="login" class="button" value=" 确定 " onclick="submitForm(<%=loginBean.CHANGE_PASS%>)">
                    &nbsp;
                    <%if(!loginBean.isNeedChange()){%><INPUT type="button" name="login2" class="button" value=" 取消 " onclick="location.href='public.jsp';"><%}%></td>
                </tr>
              </table></td>
          </tr>
        </table></td>
    </tr>
  </table>
</form><%out.print(retu);%>
</body>
</html>
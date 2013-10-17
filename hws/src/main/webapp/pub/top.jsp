<%@ page contentType="text/html; charset=UTF-8" %><%request.setCharacterEncoding("UTF-8");%>
<%engine.common.LoginBean loginBean = engine.common.LoginBean.getInstance(request);
  if(!loginBean.isLogin(request, response))
    return;
%>
<html>
<head>
<title>head</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK rel="stylesheet" href="../scripts/public.css" type="text/css">
<SCRIPT language=javascript>
function expand()
{
  if(parent.fset.cols=="0,*")
  {
    //document.all.expandpic.src="../images/img_01.gif";
    parent.fset.cols="150,*";
  }
  else
  {
    //document.all.expandpic.src="../images/img_02.gif";
    parent.fset.cols="0,*";
  }
}
</SCRIPT>
</head>
<body text="#000000" style="margin-left: 0px;">
  <TABLE bgColor="#0d73bd" border=0 cellPadding=0 cellSpacing=0 width="100%">
    <TR>
      <TD width=269><IMG height=65 src="../images/pic_top_01.gif" width=269></TD>

    <TD align=left><img src="../images/projectname.gif" width="392" height="39" onclick="window.open('');">&nbsp;</TD>
        <TD align=right width=30>&nbsp;</TD>
      </TR>
  </TABLE>
  <TABLE width="100%" border="0" cellspacing="0" cellpadding="0" height="19" style="border-bottom: 1px solid #104a7b">
    <TR valign="middle">
      <TD height="18" align="left" class="toolbar"><img class="toolbar" src="../images/selectcorp.gif" width="16" height="16" border="0" alt="显示/隐藏导航栏" style="cursor:hand" onClick="expand()"></TD>
      <TD width="*" class="toolbar">【<%=loginBean.isMember() ? "会员:"+loginBean.getUserName() : "部门："+loginBean.getDeptName()+"&nbsp;员工："+loginBean.getUserName()%>&nbsp;日期：<%=loginBean.getAccountDate()%>】</TD>
      <TD width="181" align="right" class="toolbar">
        <!--img class="toolbar" src="../images/back.gif" width="16" height="16" alt="后退" style="cursor:hand" onClick="parent.frames['mainFrame'].history.back()"-->
        <img class="toolbar" src="../images/closepage.gif" width="16" height="16" border="0" alt="关闭页面" style="cursor:hand" onClick="parent.main.location.href='../main.jsp'">
      </TD>
    </TR>
  </TABLE>
</body>
</html>
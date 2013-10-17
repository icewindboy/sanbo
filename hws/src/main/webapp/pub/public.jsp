<%@ page contentType="text/html; charset=UTF-8" %><%@ page import="engine.common.UserFacade"%><%@ include file="../pub/init.jsp"%>
<html>
<head>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK rel="stylesheet" href="../scripts/public.css" type="text/css">
<STYLE type="text/css">
.botton_out {
    border-top:1 solid #ffffff; border-left:1 solid #ffffff;
    border-bottom:1 solid #000000; border-right:1 solid #000000;
}
.botton_in {
    border-top:1 solid #000000; border-left:1 solid #000000;
    border-bottom:1 solid #ffffff; border-right:1 solid #ffffff;
}
.botton_normal {
    border:1 solid #BCD7F4;
}
</STYLE>
</head>
<%String isReadFile = loginBean.getSystemParam("SYS_READ_CODEBAR_FILE");
  if("1".equals(isReadFile))
    out.print(loginBean.showJavaScript("scaner.isReadFile=1;"));
%>
<body oncontextmenu="return true;" onload="bodyLoad();" text="#000000" style="margin-left: 0px; margin-top: 0px; margin-right: 0px; margin-bottom: 0px;" scroll=no>
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
<tr><td height="64">
  <TABLE width="100%" height="45" border=0 cellPadding=0 cellSpacing=0 bgColor="#0d73bd">
    <TR>
      <TD width=269><IMG height=45 src="../images/pic_top_01.gif" width=269 ondblclick="window.open('')"></TD>
    <TD align=left>&nbsp;</TD>
        <TD align=right width=30>&nbsp;</TD>
      </TR>
  </TABLE>
  <TABLE width="100%" height="19" border="0" cellspacing="0" cellpadding="0" style="border-bottom: 1px solid #104a7b">
    <TR valign="middle">
      <TD width="*" class="toolbar">【<%=loginBean.isMember() ? "会员:"+loginBean.getUserName() : "&nbsp;员工："+loginBean.getUserName()%>&nbsp;日期：<%=loginBean.getAccountDate()%>&nbsp;<a id='online_user_num' href="javascript:" onclick="openurl('online_user_list','online_user_list.jsp',410,220,true,false,true).focus();">在线人数:<%=UserFacade.size()%></a>】</TD>
      <TD width="181" align="right" class="toolbar">
        <table border="0" cellspacing="0" cellpadding="0"><tr>
        <td width=21 height=20 align="center" class="botton_normal"
          onmouseover="this.className='botton_out';" onmousedown="this.className='botton_in';"
          onmouseout="this.className='botton_normal';" onmouseup="this.className='botton_normal';"><img class="toolbar" src="../images/back.gif" alt="后退" style="cursor:hand" onClick="history.go(-1)"></td>
        <td width=3></td>
        <td width=21 height=20 align="center" class="botton_normal"
          onmouseover="this.className='botton_out';" onmousedown="this.className='botton_in';"
          onmouseout="this.className='botton_normal';" onmouseup="this.className='botton_normal';"><img class="toolbar" src="../images/forward.gif" alt="前进" style="cursor:hand" onClick="history.go(1)"></td>
        <td width=3></td>
        <td width=21 height=20 align="center" class="botton_normal"
          onmouseover="this.className='botton_out';" onmousedown="this.className='botton_in';"
          onmouseout="this.className='botton_normal';" onmouseup="this.className='botton_normal';"><img class="toolbar" src="../images/refresh.gif" alt="刷新" style="cursor:hand" onClick="refresh()"></td>
        <td width=3></td>
        <td width=21 height=20 align="center" class="botton_normal"
          onmouseover="this.className='botton_out';" onmousedown="this.className='botton_in';"
          onmouseout="this.className='botton_normal';" onmouseup="this.className='botton_normal';"><img class="toolbar" src="../images/password.gif" border="0" alt="修改密码" style="cursor:hand" onClick="location.href='change.jsp'"></td>
        <td width=3></td>
        <td width=21 height=20 align="center" class="botton_normal"
          onmouseover="this.className='botton_out';" onmousedown="this.className='botton_in';"
          onmouseout="this.className='botton_normal';" onmouseup="this.className='botton_normal';"><img class="toolbar" height="19" width="19" src="../images/closepage.gif" border="0" alt="重新登录" style="cursor:hand" onClick="location.href='../login.jsp?operate=<%=loginBean.RE_LOGIN%>'"></td>
        <td width=3></td>
        <td width=21 height=20 align="center" class="botton_normal"
          onmouseover="this.className='botton_out';" onmousedown="this.className='botton_in';"
          onmouseout="this.className='botton_normal';" onmouseup="this.className='botton_normal';"><img class="toolbar" src="../images/home.gif" border="0" alt="到默认页面" style="cursor:hand" onClick="goMainPage();"></td>
        <td width=6></td>
        </tr></table>
      </TD>
    </TR>
  </TABLE>
</td></tr>
<tr><td height="100%">
  <iframe id="public_left_menu" src="left.jsp" style="display:block; position:absolute; z-index:2; top:66; left:0; width:150; height:500" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
  <table width="100%" height="100%" border="1" cellpadding="0" cellspacing="0">
    <tr>
      <td width="150" nowrap>&nbsp;</td>
      <td width="100%"><iframe id="main" src="main.jsp" border="1" style="display:block; position:absolute; z-index:1;" width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="auto"></iframe></td>
    </tr>
  </table>
</td></tr></table>
<iframe id="public_inside" src="refresh.jsp" style="display:none;" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
</body>
<script language="javascript">
  function refresh()
  {
    var forms = main.document.forms;
    if(forms+'' == 'undefined')
      main.location.reload(true);
    else if(forms.length > 0)
      main.document.forms[0].submit();
    else
      main.location.reload(true);
  }

  var menuStyle = document.all.public_left_menu.style;
  var isOuting = false;//是否正在弹出中
  var width = 5;
  function bodyLoad()
  {
    //if(document.all.main.offsetTop > 66)
    menuStyle.top=document.all.main.offsetTop;
    menuStyle.height=document.all.main.offsetHeight;
    //document.all.public_left_menu.src = 'left.jsp';
    //document.all.public_left_menu.menureload();
  }
  function slideOut()
  {

  }
  function endSlideOut()
  {
    isOuting = false;
  }
  function slideIn()
  {
   // var left = parseInt(menuStyle.left);
    //var speed = 15;
   // while(!isOuting && left > -145)
   // {
   //   speed += 15;
   //   left -= width;
   //   setTimeout("document.all.public_left_menu.style.left="+ left, speed);
   // }
  }
  function goMainPage()
  {
    document.all.main.src = "../pub/main.jsp";
  }
  function disableKey()
  {
    keyCode = window.event.keyCode;
    if(keyCode == 8 || (keyCode==78 && event.ctrlKey)) //back,ctrl+n
      return false;
  }
  function openurl(winName,url,iWidth,iHeight,isCenter,isResizable,isScrollbars){
    if(isCenter+'' == 'undefined')
      isCenter = false;
    if(isResizable+'' == 'undefined')
      isResizable = false;
    if(isScrollbars+'' == 'undefined')
      isScrollbars = false;

    var left = 0;
    var top = 0;
    if(iWidth+'' == 'undefined' && iWidth <= 0)
      iWidth = screen.width-10;
    else if(iWidth > screen.width-10)
      iWidth = screen.width-10;
    else if(isCenter)
      left = (screen.width-10 - iWidth)/2;

    if(iHeight+'' == 'undefined' && iHeight <= 0)
      iHeight = screen.height-130;
    else if(iHeight > screen.height-130)
      iHeight = screen.height-130;
    else if(isCenter)
      top = (screen.height-130 - iHeight)/2;

    if(winName +'' == 'undefined')
      winName = "";

    var winretu = window.open(url,winName,"left="+left+",top="+top+",width="+ iWidth +",height="+ iHeight
                + ",menubar=no,toolbar=no,status=no,scrollbars="+(isScrollbars ? "yes" : "no")
                + ",resizable="+ (isResizable ? "yes" : "no"));//790,height=455
    return winretu;
  }
  //document.onclick=slideIn;
  document.onkeydown = disableKey;
  window.onresize = bodyLoad;
</script>
</html>
<%@ page contentType="text/html; charset=UTF-8" %><%request.setCharacterEncoding("UTF-8");%>
<%@ page import = "java.math.BigDecimal"%>
<html>
<head>
<title>英捷ERP系统</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="scripts/public.css" type="text/css">
<script language="javascript" src="scripts/validate.js"></script>
</head>
<%String retu = engine.common.LoginBean.doPost(request, response);
  engine.common.LoginBean loginBean = engine.common.LoginBean.getInstance(request);
  //loginBean.setSysType("1");
  session.setMaxInactiveInterval(180);
  boolean isReLogin = String.valueOf(loginBean.RE_LOGIN).equals(request.getParameter("operate"));
  if(!isReLogin && loginBean.isLogin())
  {
    boolean isIe6 = request.getHeader("User-Agent").indexOf("MSIE 6") > -1;
    StringBuffer show = isIe6 ? new StringBuffer() :
      new StringBuffer("<object id=closer type='application/x-oleobject' classid='clsid:adb880a6-d8ff-11cf-9377-00aa003b7a11'><param name='Command' value='Close'></object>");
    show.append("<script language='JavaScript'>var Width = screen.width-10;  var Height = screen.height-56;");
    show.append("var newwin = window.open('").append(loginBean.isNeedChange() ? "pub/change.jsp" : "pub/public.jsp");
    show.append("','").append(new BigDecimal(Math.floor(1000000*Math.random())).toString());
    show.append("','left=0,top=0,width='+ Width +',height='+ Height +',menubar=no,toolbar=no,status=no,scrollbars=no,resizable=yes');");
    show.append("self.focus();");
    show.append(isIe6 ? "opener=null;window.close();" : "closer.Click();");
    show.append("newwin.focus();</script>");
    out.print(show.toString());
    return;
  }
%>
<script language="javascript" src="scripts/validate.js"></script>
<script language="javascript">
<%out.print(retu);%>
if(self!=top)
  top.location=self.location;

function operateClick()
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = <%=loginBean.LOGIN%>;
  form1.submit();
}

function clientDate()
{
  tmpDate = new Date();
  date = tmpDate.getDate();
  month= tmpDate.getMonth() + 1 ;
  year= tmpDate.getYear();
  var datestring = year + "年" + month + "月" + date + "日 ";

  myArray=new Array(6);
  myArray[0]="星期日"
  myArray[1]="星期一"
  myArray[2]="星期二"
  myArray[3]="星期三"
  myArray[4]="星期四"
  myArray[5]="星期五"
  myArray[6]="星期六"
  weekday=tmpDate.getDay();
  return datestring + myArray[weekday];
}

function loginError()
{
  alert("错误的用户名或密码！");
}
</script>
<%String currURL = request.getRequestURL().toString();
%>
<body onLoad="form1.screen.value = getScreenType();">
<table width="100%" height="100%" cellpadding="0" cellspacing="5">
  <tr>
    <td align="center" valign="middle">
      <table width="600" height="289" border="0" align="center" cellpadding="0" cellspacing="3" background="images/first.jpg" class="loginbox">
        <tr>
          <td width="54%" height="109" rowspan="3" valign="top">
            <font color="#000080"><script language="javascript">document.write(clientDate());</script></font>
          </td>
          <td width="46%" height="57" valign="top"> <p align="right">
            <font color="#FF7410">『</font>
            <A href="javascript:void(0)" onclick='this.style.behavior="url(#default#homepage)";this.setHomePage("<%=currURL%>")' target=_self>设为首页</A>
            <font color="#FF7410">』</font></p>
          </td>
        </tr>
        <tr>
          <td width="46%" height="24" valign="top">　</td>
        </tr>
        <tr>
          <td width="46%" height="23" valign="top">
<p align="right">&nbsp;&nbsp;&nbsp;</td>
        </tr>
        <tr>
          <td width="54%" height="163" valign="top" rowspan="2">
            <form name="form1" method="POST" action="<%=currURL%>" onSubmit="return false;">
              <input type="hidden" NAME="screen" value="1">
              <input type="hidden" NAME="operate" value="">
              <table border="0" width="103%">
                <tr align="center">
                  <td colspan="2">
                  <%
                  String errorMessage = request.getParameter("errorMessage");
                  if(errorMessage != null && !loginBean.getUser().isValid())
                    out.print("<script language='javascript'>alert('请重新登录！您被强制下线了。')</script>");
                  else if ( errorMessage != null ) {
                    //if(errorMessage.equals("nologin"))
                    out.print("<script language='javascript'>alert('请重新登录！您可能遇到一下情况：\n1.服务端重置\n2.会话失效\n</script>");
                  }
                  %>
                  </td>
                </tr>
                <tr>
                  <td width="47%" class="tdTitle"><div align="right">日&nbsp;&nbsp;&nbsp;&nbsp;期：</div></td>
                  <td width="53%"><input name="ednone" type="text" onChange="chkDate(this)" value="<%=loginBean.getAccountDate()%>" size="14" style="border:none; background-color:transparent;" readonly>
                    <%--a href="#"><img src="images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="rootSelectDate(accountdate);"></a--%>
                  </td>
                </tr>
                <tr>
                  <td width="47%" class="tdTitle"><div align="right">用 户 名：</div></td>
                  <td width="53%"><input type="text" name="user" size="14" value="<%=loginBean.getLoginUser()%>"></td>
                </tr>
                <tr>
                  <td width="47%" class="tdTitle"><div align="right">密&nbsp;&nbsp;&nbsp;&nbsp;码：</div></td>
                  <td width="53%"><input type="password" name="password" size="14" value="">
                  </td>
                </tr>
                <tr>
                  <td width="47%"></td>
                  <td width="53%"><INPUT type="submit" name="login" class="button" value=" 登录 " onclick="operateClick()"></td>
                </tr>
                <tr>
                  <td width="47%"></td>
                  <td width="53%">&nbsp;</td>
                </tr>
              </table>
            </form>
          <td width="46%" height="37" valign="top"><p align="left">&nbsp; </p></td>
        </tr>
        <tr>
          <td width="46%" height="99" valign="middle"></td>
        </tr>
        <tr>
          <td height="14" colspan="2" align="right" valign="top">
            <font color="#0000FF">杭州英捷软件系统有限公司&nbsp;&nbsp;版权所有 1999-2001</font>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
  <script language="javascript">
    form1.user.focus();
  </script>
</body>
</html>
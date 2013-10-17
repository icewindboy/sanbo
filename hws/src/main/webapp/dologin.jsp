<%@ page contentType="text/html; charset=utf-8" %><%@ page import = "java.math.BigDecimal"%><%request.setCharacterEncoding("utf-8");
  //关闭netscape浏览器缓存
  response.setHeader("Pragma","no-cache");
  //关闭IE浏览器缓存
  response.setHeader("Cache-Control","no-cache");
  response.setHeader("Cache-Control","no-store");
  response.setHeader("Cache-Control","post-check=0");
  response.setHeader("Cache-Control","pre-check=0");

  response.setDateHeader("Expires", 0);

  String retu = engine.common.LoginBean.doPost(request, response);
  engine.common.LoginBean loginBean = engine.common.LoginBean.getInstance(request);
  if(loginBean.isLogin())
  {
    boolean isIe6 = request.getHeader("User-Agent").indexOf("MSIE 6") > -1;
    StringBuffer show = isIe6 ? new StringBuffer() :
      new StringBuffer("<object id=closer type='application/x-oleobject' classid='clsid:adb880a6-d8ff-11cf-9377-00aa003b7a11'><param name='Command' value='Close'></object>");
    show.append("<script language='JavaScript'>var Width = screen.width-10;  var Height = screen.height-56;");
    show.append("var newwin = window.open('").append(loginBean.isNeedChange() ? "pub/change.jsp" : "pub/engine.jsp");
    show.append("','").append(new BigDecimal(Math.floor(1000000*Math.random())).toString());
    show.append("','left=0,top=0,width='+ Width +',height='+ Height +',menubar=no,toolbar=no,status=no,scrollbars=no,resizable=yes');");
    show.append("self.focus();");
    show.append(isIe6 ? "opener=null;window.close();" : "closer.Click();");
    show.append("newwin.focus();</script>");
    out.print(show.toString());
    return;
  }
%>
<script language="javascript">
<%=retu%>
function loginError(info)
{
  alert(info);
  location.href='login.jsp';
}
</script>

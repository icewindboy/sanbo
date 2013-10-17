<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<html>
<head><title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
<link rel="stylesheet" href="../scripts/menu.css" type="text/css">
</head>
<SCRIPT language="javascript" src="../scripts/dynamiclayer.js"></SCRIPT>
<script language="javascript" src="../scripts/menu.js"></script>
<script language="javascript">
var isHide = true;
var cancelIn = false;
var outId;<%--是否取消滑进--%>
var inId; <%--是否取消滑出--%>
function GotoNode(pID, url)
{
  if(parent.isOuting)
    return;
  if(url != '')
  {
    //parent.slideIn();
    //lockScreenForOpen("初始化中, 请稍候！", parent.main.document);
    var opea = "operate=<%=engine.project.Operate.INIT%>&src=../pub/main.jsp";
    parent.main.location.href=url +(url.indexOf("?")>-1 ? "&" : "?")+opea;
    //parent.main.lockScreenForOpen();
  }
}
function mouseOver()
{
  cancelIn = true;
  clearTimeout(inId);
  if(isHide)
    outId = setTimeout('sildeOut();', 700);
}
function sildeOut()
{
  parent.slideOut();
  isHide = false;
}
function mouseOut()
{
  cancelIn = false;
  clearTimeout(outId);
  if(!isHide)
    setTimeout('sildeIn();', 500);
}
function sildeIn()
{
  if(!cancelIn)
  {
    parent.slideIn();
    isHide = true;
  }
}
</script>
<BODY onkeydown="return false" scroll="no" onresize="menureload()" style="border:1px solid #FFFFFF; border-right:1px solid #000000; border-bottom:1px solid #000000;"
><%--/style="border-right: 1px solid #104a7b"--%>
<%--table id="tb_slide" align=left width="100%" height="100%" border=0 cellspacing=0 cellpadding=0 onselectstart="return false"
 onMouseDown="sildeOut()" onMouseOver="mouseOver()" onMouseOut="mouseOut()" style="border:1px solid #777777; border-right:2px solid #707070; border-bottom:2px solid #707070;">
  <tr><td height="100%"--%>
<%=loginBean.getMenuData()%><%--/td>
  </tr>
</table--%>
</BODY>
<script language="javascript">bodyOnLoad()</script>
</Html>
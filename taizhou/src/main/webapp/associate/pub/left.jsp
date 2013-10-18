<%@ page contentType="text/html; charset=UTF-8" %><%request.setCharacterEncoding("UTF-8");
  //session.setMaxInactiveInterval(900);
  response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","max-age=0");
  response.addHeader("Cache-Control","pre-check=0");
  response.addHeader("Cache-Control","post-check=0");
  response.setDateHeader("Expires", 0);
  engine.common.OtherLoginBean loginBean = engine.common.OtherLoginBean.getInstance(request);
  if(!loginBean.isLogin(request, response))
    return;
%><%@ taglib uri="/WEB-INF/pagescontrol.tld" prefix="pc"%>
<html>
<head><title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/scripts/public.css" type="text/css">
<link rel="stylesheet" href="<%=request.getContextPath() %>/scripts/menu.css" type="text/css">
</head>
<SCRIPT language="javascript" src="<%=request.getContextPath() %>/scripts/dynamiclayer.js"></SCRIPT>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/menu.js"></script>
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

<BODY onkeydown="return false" scroll="no" onresize="menureload()" onMouseDown="sildeOut()" onMouseOver="mouseOver()" onMouseOut="mouseOut()" style="border:1px solid #FFFFFF; border-right:1px solid #000000; border-bottom:1px solid #000000;">

<% if(loginBean.isSales()&&loginBean.isSupply()){ %>
<DIV id='menulayer0Div' class='menulayer'>
<DIV id='barlayer0Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(0)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;供应商管理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer0Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('281','<%=request.getContextPath() %>/associate/buyinfo/buyinfo.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;采购公告</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('282','<%=request.getContextPath() %>/associate/buyprice/buyprice.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;我的报价</td></tr>
<%--tr><td nowrap class=treeMenuItem onClick=GotoNode('283','<%=request.getContextPath() %>/buy/buy_order.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;采购合同</td></tr--%>
</table>
</DIV>
<DIV id='uplayer0Div' class='uplayer'><IMG height=16 width=16 src='<%=request.getContextPath() %>/images/scrollup.gif' onmousedown="this.src='<%=request.getContextPath() %>/images/scrollup2.gif';menuscrollup()" onmouseout="this.src='<%=request.getContextPath() %>/images/scrollup.gif';menuscrollstop()" onmouseup="this.src='<%=request.getContextPath() %>/images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer0Div' class='downlayer'><IMG height=16 width=16 src='<%=request.getContextPath() %>/images/scrolldown.gif' onmousedown="this.src='<%=request.getContextPath() %>/images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='<%=request.getContextPath() %>/images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='<%=request.getContextPath() %>/images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<DIV id='menulayer1Div' class='menulayer'>
<DIV id='barlayer1Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(1)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;经销商管理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer1Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('20','<%=request.getContextPath() %>/associate/order/sale_order_list.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;采购订单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('21','<%=request.getContextPath() %>/associate/lading_bill/lading_bill.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;采购到货单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('23','<%=request.getContextPath() %>/associate/destroy/report_destroy_list.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;损溢单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('22','<%=request.getContextPath() %>/associate/output/outputlist.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;销售出货单</td></tr>
<tr><td nowrap class=treeMenuItem><hr class='menuSperator'></td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('299','../sale/customer_product_discount.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;库存明细</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('29','../finance/sale_invoice.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;销售明细报表</td></tr>
</table></DIV>
<DIV id='uplayer1Div' class='uplayer'><IMG height=16 width=16 src='<%=request.getContextPath() %>/images/scrollup.gif' onmousedown="this.src='<%=request.getContextPath() %>/images/scrollup2.gif';menuscrollup()" onmouseout="this.src='<%=request.getContextPath() %>/images/scrollup.gif';menuscrollstop()" onmouseup="this.src='<%=request.getContextPath() %>/images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer1Div' class='downlayer'><IMG height=16 width=16 src='<%=request.getContextPath() %>/images/scrolldown.gif' onmousedown="this.src='<%=request.getContextPath() %>/images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='<%=request.getContextPath() %>/images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='<%=request.getContextPath() %>/images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<SCRIPT language='javascript'>function bodyOnLoad(){init(22, 2, 'menulayer', 'iconlayer', 'barlayer', 'uplayer', 'downlayer', 'Div');}
</SCRIPT>
<%}else if(loginBean.isSupply()){ %>
<DIV id='menulayer0Div' class='menulayer'>
<DIV id='barlayer0Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(0)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;供应商管理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer0Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('281','<%=request.getContextPath() %>/associate/buyinfo/buyinfo.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;采购公告</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('282','<%=request.getContextPath() %>/associate/buyprice/buyprice.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;我的报价</td></tr>
<%--tr><td nowrap class=treeMenuItem onClick=GotoNode('283','<%=request.getContextPath() %>/buy/buy_order.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;采购合同</td></tr--%>
</table>
</DIV>
<DIV id='uplayer0Div' class='uplayer'><IMG height=16 width=16 src='<%=request.getContextPath() %>/images/scrollup.gif' onmousedown="this.src='<%=request.getContextPath() %>/images/scrollup2.gif';menuscrollup()" onmouseout="this.src='<%=request.getContextPath() %>/images/scrollup.gif';menuscrollstop()" onmouseup="this.src='<%=request.getContextPath() %>/images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer0Div' class='downlayer'><IMG height=16 width=16 src='<%=request.getContextPath() %>/images/scrolldown.gif' onmousedown="this.src='<%=request.getContextPath() %>/images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='<%=request.getContextPath() %>/images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='<%=request.getContextPath() %>/images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<SCRIPT language='javascript'>function bodyOnLoad(){init(22, 1, 'menulayer', 'iconlayer', 'barlayer', 'uplayer', 'downlayer', 'Div');}
</SCRIPT>
<%}else if(loginBean.isSales()){  %>
<DIV id='menulayer0Div' class='menulayer'>
<DIV id='barlayer0Div' class='barlayer'>
<TABLE border='0' cellPadding='0' cellSpacing='0' height='22' onclick='menubarpush(0)' width='100%'><tr><td nowrap class='menuTabBar'>&nbsp;经销商管理&nbsp;</td></tr></TABLE></DIV>
<DIV id='iconlayer0Div' class='iconlayer'>
<table CELLSPACING='0' CELLPADDING='0' BORDER='0' width='100%' onMouseOut=treeMenuMouseOut() onMouseOver=treeMenuMouseOver()>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('281','<%=request.getContextPath() %>/associate/order/sale_order_list.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;采购订单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('282','<%=request.getContextPath() %>/associate/lading_bill/lading_bill.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;采购到货单</td></tr>

<tr><td nowrap class=treeMenuItem onClick=GotoNode('283','<%=request.getContextPath() %>/associate/lading_bill/lading_bill.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;损溢单</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('284','<%=request.getContextPath() %>/associate/lading_bill/lading_bill.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;销售出货单</td></tr>
<tr><td nowrap class=treeMenuItem><hr class='menuSperator'></td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('285','<%=request.getContextPath() %>/associate/lading_bill/lading_bill.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;库存明细</td></tr>
<tr><td nowrap class=treeMenuItem onClick=GotoNode('286','<%=request.getContextPath() %>/associate/lading_bill/lading_bill.jsp')><img align='absmiddle' border=0 src='<%=request.getContextPath() %>/images/tree/fs.gif'>&nbsp;销售明细报表</td></tr>
</table>
</DIV>
<DIV id='uplayer0Div' class='uplayer'><IMG height=16 width=16 src='<%=request.getContextPath() %>/images/scrollup.gif' onmousedown="this.src='<%=request.getContextPath() %>/images/scrollup2.gif';menuscrollup()" onmouseout="this.src='<%=request.getContextPath() %>/images/scrollup.gif';menuscrollstop()" onmouseup="this.src='<%=request.getContextPath() %>/images/scrollup.gif';menuscrollstop()"></DIV>
<DIV id='downlayer0Div' class='downlayer'><IMG height=16 width=16 src='<%=request.getContextPath() %>/images/scrolldown.gif' onmousedown="this.src='<%=request.getContextPath() %>/images/scrolldown2.gif';menuscrolldown()" onmouseout="this.src='<%=request.getContextPath() %>/images/scrolldown.gif';menuscrollstop()" onmouseup="this.src='<%=request.getContextPath() %>/images/scrolldown.gif';menuscrollstop()"></DIV>
</DIV>
<SCRIPT language='javascript'>function bodyOnLoad(){init(22, 1, 'menulayer', 'iconlayer', 'barlayer', 'uplayer', 'downlayer', 'Div');}
</SCRIPT>
<%} %>
</BODY>

<script language="javascript">bodyOnLoad()</script>

</Html>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
<link rel="stylesheet" href="../scripts/menu.css" type="text/css">
<script language=javascript src="../scripts/menu.js"></script>
</head>
<%engine.erp.baseinfo.B_ProductKind prodKindBean = engine.erp.baseinfo.B_ProductKind.getInstance(request);
  String curUrl = request.getRequestURL().toString();
%>
<script language="javascript">
function nodeShowMenu(pID, isRoot, isNode, isFirst){}
function GotoNode(pID, isNode)
{
  parent.context.location.href = "sale_goods_right.jsp?operate=<%=engine.erp.common.B_SaleGoodsSelect.PRODUCT_KIND_CHANGE%>&wzlbid="+pID;
}
</SCRIPT>
<BODY TopMargin=0 LeftMargin=0 onload="bodyLoad()">
<TABLE id="headbar" WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR><TD NOWRAP>物资类别</TD></TR></TABLE>
<div id="treebar" style="display:block;width:100%;height:100%;overflow-y:auto;overflow-x:auto;">
<%=prodKindBean.getStaticProductKindTreeHTML()%>
</div>
<div id='menu_dept' class='menuBar'></div>
<SCRIPT language="javascript">
function bodyLoad()
{
  document.all.treebar.style.height=document.body.clientHeight-document.all.headbar.offsetHeight;
  document.all.treebar.style.width=document.body.clientWidth;
}
window.onresize = bodyLoad;
</SCRIPT>
</BODY>
</Html>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
<link rel="stylesheet" href="../scripts/menu.css" type="text/css">
<script Language=JavaScript Src="../scripts/menu.js"></script>
</head>
<%if(!loginBean.hasLimits("product_kind", request, response))
    return;
  engine.erp.baseinfo.B_ProductKind prodKindBean =  engine.erp.baseinfo.B_ProductKind.getInstance(request);
  String retu = prodKindBean.doService(request, response);
%>
<script language="javascript">
function nodeShowMenu(pID, isRoot, isNode, isFirst)
{
  form1.curNodeID.value = isRoot ? "0" : pID;
  showMenu_normal(isRoot, isNode, isFirst);
}
function GotoNode(pID, isNode)
{
  var operate = form1.operate.value;
  if(operate == <%=prodKindBean.NODE_PRODUCT%>)
    parent.deptcontext.location.href = "productinfoset.jsp?src=../blank.htm&operate=<%=engine.action.Operate.PROD_CHANGE%>&wzlbid="+pID;
  else if(operate == <%=prodKindBean.NODE_PROPERTY%>)
    parent.deptcontext.location.href = "product_kind_props.jsp?src=../blank.htm&operate=<%=engine.action.Operate.INIT%>&wzlbid="+pID;
  else if(operate == <%=prodKindBean.NODE_ADD_CHILD%>)
    parent.deptcontext.location.href = "product_kind_edit.jsp?src=../blank.htm&operate=<%=prodKindBean.NODE_ADD_CHILD%>&nodeID="+pID;
  else
    parent.deptcontext.location.href = "product_kind_edit.jsp?src=../blank.htm&operate=<%=prodKindBean.NODE_EDIT_VIEW%>&nodeID="+pID;
}
function GotoNodeProperty()
{
  form1.operate.value = <%=prodKindBean.NODE_PROPERTY%>;
  parent.deptcontext.location.href = "product_kind_props.jsp?src=../blank.htm&operate=<%=engine.action.Operate.INIT%>&wzlbid="+form1.curNodeID.value;
}
function GotoNodeView()
{
  form1.operate.value = <%=prodKindBean.NODE_EDIT_VIEW%>;
	parent.deptcontext.location.href = "product_kind_edit.jsp?src=../blank.htm&operate=<%=prodKindBean.NODE_EDIT_VIEW%>&nodeID="+form1.curNodeID.value;
}
function GotoNodeList()
{
  form1.operate.value = <%=prodKindBean.NODE_PRODUCT%>;
	parent.deptcontext.location.href = "productinfoset.jsp?src=../blank.htm&operate=<%=engine.action.Operate.PROD_CHANGE%>&wzlbid="+form1.curNodeID.value
}
function GotoNodeDel()
{
  if(!confirm("是否删除该类别？"))
    return;
  form1.operate.value = <%=prodKindBean.NODE_DELETE%>;
  form1.submit();
}
function GotoNodeCut()
{
  var link;
	var cutID=document.form1.cutNodeID.value;
	if (cutID+''!=''){
    link = eval("link_"+cutID);
		link.style.backgroundColor = "transparent";
	}
	//HighlightDefaultNode();
  var curNodeID = form1.curNodeID.value;
  link = eval("link_"+curNodeID);
	link.style.backgroundColor = "#d0d0d0";
	document.form1.cutNodeID.value=curNodeID;
}
function GotoNodePaste()
{
  form1.operate.value = <%=prodKindBean.NODE_PASTE%>;
  form1.submit();
}
function GotoAddChild()
{
  form1.operate.value = <%=prodKindBean.NODE_ADD_CHILD%>;
  parent.deptcontext.location.href = "product_kind_edit.jsp?src=../blank.htm&operate=<%=prodKindBean.NODE_ADD_CHILD%>&nodeID="+form1.curNodeID.value;
}
</script>
<%if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
%>
<BODY TopMargin=0 LeftMargin=0 oncontextmenu="window.event.returnValue=false" onload="bodyLoad()">
<table id="headbar" width="100%" border=0 cellspacing=0 cellpadding=0 class="headbar"><tr><td NOWRAP>物资类别设置</td></tr></table>
<div id="treebar" style="display:block;width:100%;height:100%;overflow-y:auto;overflow-x:auto;">
<%=prodKindBean.getProductKindTreeHTML()%>
</div>
<form name="form1" action="<%=curUrl%>" method="POST">
  <INPUT TYPE="HIDDEN" NAME="cutNodeID" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="<%=prodKindBean.getDefaultOperate()%>">
  <INPUT TYPE="HIDDEN" NAME="curNodeID" VALUE="">
</form>
<script language="javascript">
function bodyLoad()
{
  document.all.treebar.style.height=document.body.clientHeight-document.all.headbar.offsetHeight;
  document.all.treebar.style.width=document.body.clientWidth;
}
window.onresize = bodyLoad;
//isRoot:是否是根， isNode：是否结点(其下还有叶子)
function showMenu_normal(isRoot, isNode, isFirst)
{
  mi_edit.style.display  =isRoot ? "none": "block";
  mi_sperator.style.display = isRoot ? "none": "block";
  //mi_product.style.display = isRoot ? "none": "block";
  mi_property.style.display = !isRoot ? "block": "none";
  mi_del.style.display  = (isRoot || isNode) ? "none": "block";
  //是否有剪切操作
  isCut = form1.cutNodeID.value+''!='';
  if(isRoot){
    mi_cut.style.display  = "none";
    //mi_paste.style.display= "none";
  }
  else {
    mi_cut.style.display  = isCut ? "none" : "block";
  }
  mi_paste.style.display= isCut ? "block" : "none";
  OnMouseUp('menu_dept');
}
</script>
<div id='menu_dept' class='menuBar' onMouseOver="EnterMenu()" onMouseOut="LeaveMenu('menu_dept');">
  <table border='0' height='30' width='76' cellspacing='1'>
    <tr id='mi_edit' onmouseup='GotoNodeView()' class='menuItemOut' outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap valign="middle">&nbsp;&nbsp;浏览/修改</td>
    </tr>
    <tr id='mi_product' onmouseup='GotoNodeList()' class='menuItemOut' outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap>&nbsp;&nbsp;物资列表</td>
    </tr>
    <tr id='mi_property' onmouseup='GotoNodeProperty()' class='menuItemOut' outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap title='与物资的规格属性相关'>&nbsp;&nbsp;类别属性</td>
    </tr>
    <tr id='mi_sperator' class='menuItemOut'>
      <td nowrap><hr class='menuSperator'></td>
    </tr>
    <tr id='mi_addnode' onmouseup='GotoAddChild()' class='menuItemOut' outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap>&nbsp;&nbsp;增加子类别</td>
    </tr>
    <tr id='mi_del' onmouseup='GotoNodeDel()' class='menuItemOut' outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap>&nbsp;&nbsp;删除</td>
    </tr>
    <tr id='mi_cut' onmouseup='GotoNodeCut()' class='menuItemOut' outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap>&nbsp;&nbsp;剪切</td>
    </tr>
    <tr id='mi_paste' onmouseup='GotoNodePaste()' class='menuItemOut' style="display:none" outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap>&nbsp;&nbsp;粘贴</td>
    </tr>
  </table>
</div>
<%out.print(retu);%>
</BODY>
</Html>

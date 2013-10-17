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
<%if(!loginBean.hasLimits("bom", request, response))
    return;
  engine.erp.produce.B_Bom bomBean = engine.erp.produce.B_Bom.getInstance(request);
  String retu = bomBean.doService(request, response);
%>
<script language="javascript">
var selectObj, copyObj;
function nodeShowMenu(pID, isRoot, isNode, obj, path)
{
  form1.curNodeID.value = pID;
  selectObj = obj;
  showMenu_normal(isRoot, isNode);
}
function GotoNode(pID, isNode)
{
  parent.deptcontext.location.href = "bom_edit.jsp?operate=<%=bomBean.NODE_EDIT_VIEW%>&nodeID="+pID;
}
function GotoNodeView()
{
	parent.deptcontext.location.href = "bom_edit.jsp?operate=<%=bomBean.NODE_EDIT_VIEW%>&nodeID="+form1.curNodeID.value;
}
function GotoNodeDel()
{
  if(!confirm("是否删除该类别？"))
    return;
  form1.operate.value = <%=bomBean.NODE_DELETE%>;
  form1.submit();
}
function GotoNodeCopy()
{
  var link;
	var cutID= form1.cutNodeID.value;
	if (cutID+''!=''){
    //link = eval("link_"+cutID);
    if(copyObj != null)
      copyObj.style.backgroundColor = "transparent";
	}
	//HighlightDefaultNode();
  var curNodeID = form1.curNodeID.value;
  //link = eval("link_"+curNodeID);
  copyObj = selectObj;
	copyObj.style.backgroundColor = "#d0d0d0";
	form1.cutNodeID.value=curNodeID;
}
function CancleCopy()
{
  if(copyObj != null)
    copyObj.style.backgroundColor = "transparent";
  form1.cutNodeID.value = "";
}
function GotoNodePaste()
{
  parent.deptcontext.location.href = "bom_edit.jsp?operate=<%=bomBean.NODE_PASTE%>&curNodeID="+form1.curNodeID.value+"&cutNodeID="+form1.cutNodeID.value;
}
function GotoAddChild()
{
  parent.deptcontext.location.href = "bom_edit.jsp?operate=<%=bomBean.NODE_ADD_CHILD%>&nodeID="+form1.curNodeID.value;
}
function NodeExpand(nodeid, curPath)
{
  form1.pathcode.value = curPath;
  form1.operate.value=<%=bomBean.NODE_EXPAND%>;
  form1.curNodeID.value = nodeid;
  form1.submit();
}
function NodeMouseDown(path)
{
  //alert(path);
  form1.pathcode.value = path;
}
</SCRIPT>
<%if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
%>
<BODY TopMargin=0 LeftMargin=0 oncontextmenu="window.event.returnValue=false" onload="bodyLoad()">
<form name="form1" action="<%=curUrl%>" method="POST">
  <INPUT TYPE="HIDDEN" NAME="cutNodeID" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="curNodeID" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="pathcode" VALUE="<%=bomBean.getPathCode()%>">
</form>
<TABLE id="headbar" WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR><TD NOWRAP>BOM表结构图</TD></TR></TABLE>
<div id="treebar" style="display:block;width=100%;height=100%;overflow-y:auto;overflow-x:auto;">
<%=bomBean.getBomTreeHTML()%>
</div>
<SCRIPT language=javascript>
function bodyLoad()
{
  document.all.treebar.style.height=document.body.clientHeight-document.all.headbar.offsetHeight;
  document.all.treebar.style.width=document.body.clientWidth;
}
window.onresize = bodyLoad;
//isRoot:是否是根， isNode：是否结点(其下还有叶子)
function showMenu_normal(isRoot, isNode)
{
  mi_edit.style.display = isRoot ? "none": "block";
  mi_addnode.style.display = isRoot ? "block": "none";
  mi_sperator.style.display = isRoot ? "none": "block";
  mi_del.style.display  = isRoot ? "none": isNode ? "block" : "none";
  //是否有剪切操作
  isCopy = form1.cutNodeID.value+''!='';
  if(isRoot){
    mi_copy.style.display  = "none";
    //mi_paste.style.display= "none";
  }
  else {
    mi_copy.style.display  = isCopy ? "none" : "block";
  }
  mi_paste.style.display= isCopy && copyObj != selectObj ? "block" : "none";
  OnMouseUp('menu_dept');
}
</SCRIPT>
<div id='menu_dept' class='menuBar' onMouseOver="EnterMenu()" onMouseOut="LeaveMenu('menu_dept');">
  <table border='0' height='10' width='76' cellspacing='1'>
    <tr id='mi_edit' onmouseup='GotoNodeView()' class='menuItemOut' outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap valign="middle">&nbsp;&nbsp;浏览/修改</td>
    </tr>
    <tr id='mi_sperator' class='menuItemOut'>
      <td nowrap><hr class='menuSperator'></td>
    </tr>
    <tr id='mi_addnode' onmouseup='GotoAddChild()' class='menuItemOut' outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap>&nbsp;&nbsp;增加</td>
    </tr>
    <tr id='mi_del' onmouseup='GotoNodeDel()' class='menuItemOut' outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap>&nbsp;&nbsp;删除</td>
    </tr>
    <tr id='mi_copy' onmouseup='GotoNodeCopy()' class='menuItemOut' outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap>&nbsp;&nbsp;复制配料</td>
    </tr>
    <tr id='mi_paste' onmouseup='GotoNodePaste()' class='menuItemOut' style="display:none" outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap>&nbsp;&nbsp;粘贴配料</td>
    </tr>
  </table>
</div>
<%out.print(retu);%>
</BODY>
</Html>
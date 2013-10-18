<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*"%>
<html>
<head>
<title>组织结构</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
<link rel="stylesheet" href="../scripts/menu.css" type="text/css">
<script Language=JavaScript Src="../scripts/menu.js"></script>
</head>
<%if(!loginBean.hasLimits("deptlist", request, response))
    return;
  engine.erp.baseinfo.B_DeptInfo deptBean =  engine.erp.baseinfo.B_DeptInfo.getInstance(request);
  String retu = deptBean.doPost(request, response);
%>
<script language="javascript">
function nodeShowMenu(pID, isRoot, isNode)
{
  form1.curNodeID.value = pID;
  showMenu_normal(isRoot, isNode);
}
function GotoNode(pID, isNode)
{
  var operate = form1.operate.value;
  if(operate == <%=deptBean.DEPT_PERSON%>)
    parent.deptcontext.location.href = "../person/employee_info.jsp?operate=<%=engine.action.Operate.INIT%>&deptid="+pID;
  else if(operate == <%=deptBean.DEPT_ADD_CHILD%>)
    parent.deptcontext.location.href = "deptedit.jsp?operate=<%=deptBean.DEPT_ADD_CHILD%>&nodeID="+pID;
  else
    parent.deptcontext.location.href = "deptedit.jsp?operate=<%=deptBean.DEPT_EDIT_VIEW%>&nodeID="+pID;
}
function GotoNodeView()
{
  form1.operate.value = <%=deptBean.DEPT_EDIT_VIEW%>;
	parent.deptcontext.location.href = "deptedit.jsp?operate=<%=deptBean.DEPT_EDIT_VIEW%>&nodeID="+form1.curNodeID.value;
}
function GotoNodeList()
{
  form1.operate.value = <%=deptBean.DEPT_PERSON%>;
	parent.deptcontext.location.href = "../person/employee_info.jsp?operate=<%=engine.action.Operate.INIT%>&deptid="+form1.curNodeID.value
}
function GotoNodeDel()
{
  if(!confirm("是否删除该部门？"))
    return;
  form1.operate.value = <%=deptBean.DEPT_DELETE%>;
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
  form1.operate.value = <%=deptBean.DEPT_PASTE%>;
  form1.submit();
}
function GotoAddChild()
{
  form1.operate.value = <%=deptBean.DEPT_ADD_CHILD%>;
  parent.deptcontext.location.href = "deptedit.jsp?operate=<%=deptBean.DEPT_ADD_CHILD%>&nodeID="+form1.curNodeID.value;
}
</script>
<%if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
%>
<BODY TopMargin=0 LeftMargin=0 oncontextmenu="window.event.returnValue=false" onload="bodyLoad()">
<TABLE id="headbar" WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR><TD NOWRAP>部门设置</TD></TR></TABLE>
<form name="form1" action="<%=curUrl%>" method="POST">
  <INPUT TYPE="HIDDEN" NAME="cutNodeID" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="<%=deptBean.tempOperate%>">
  <INPUT TYPE="HIDDEN" NAME="curNodeID" VALUE="">
</form>
<div id="treebar" style="display:block;width=100%;height=90%;overflow-y:auto;overflow-x:auto;">
<%deptBean.getDeptTree(pageContext);%>
</div>
<%--script>
function ShowHide(Layer, isLast, ImageNode, ImageIcon)
{
  if(Layer.style.display=='none'){
    Layer.style.display='block';
    ImageNode.src= isLast ? '../images/clo.gif' : '../images/co.gif';
    ImageIcon.src= '../images/fc.gif';
  }
  else{
    Layer.style.display='none';
    ImageNode.src= isLast ? '../images/clc.gif' : '../images/cc.gif';
    ImageIcon.src= '../images/fo.gif';
  }
}
</script>
<TABLE align=left cellspacing=1 cellpadding=1 class="treebox" border=0>
  <TR>
    <TD valign=top>
      <table CELLSPACING="0" CELLPADDING="0" BORDER="0" width="100%">
        <tr>
          <td nowrap class=node onMouseUp="nodeShowMenu(0, true)" onMouseOut="LeaveMenu('menu_dept')">高达物流分销系统</td>
        </tr>
        <tr id='tr_node_1'>
          <td nowrap class=node><img id='img_line_1' border=0 src='../images/clc.gif' onClick='ShowHide(tr_context_1,false,img_line_1,img_icon_1)'><img id='img_icon_1' border=0 src='../images/fc.gif' onClick='ShowHide(tr_context_1,false,img_line_1,img_icon_1)'><a href='javascript:void(0)' onClick="GotoNode(1)">&nbsp;总论</a></td>
        </tr>
        <tr id='tr_context_1' style='display:none'>
          <td nowrap class=node>
            <table CELLSPACING="0" CELLPADDING="0" BORDER="0" width="100%">
              <tr id='tr_node_2'>
                <td nowrap class=node><IMG BORDER=0 SRC="../images/line.gif"><img id='img_line_2' border=0 src='../images/cc.gif' onClick='ShowHide(tr_context_2,false,img_line_2,img_icon_2)'><img id='img_icon_2' border=0 src='../images/fc.gif' onClick='ShowHide(tr_context_2,false,img_line_2,img_icon_2)'>
                  <a class='td' HREF="javascript:void(0)" onclick="GotoNode(7);">&nbsp;CMM概论</a></td>
              </tr>
              <tr id='tr_context_2'>
                <td nowrap class=node>
                  <table CELLSPACING="0" CELLPADDING="0" BORDER="0" width="100%">
                    <tr>
                      <td nowrap class=node><IMG BORDER=0 SRC="../images/line.gif"><IMG BORDER=0 SRC="../images/line.gif"><img border=0 src='../images/line_t.gif'><img border=0 src='../images/fs.gif'><a class='td' HREF="#" onclick="GotoNode(7);return false;">CMM的理论基础</a></td>
                    </tr>
                    <tr>
                      <td nowrap class=node><IMG BORDER=0 SRC="../images/line.gif"><IMG BORDER=0 SRC="../images/line.gif"><img border=0 src='../images/line_b_c.gif'><img border=0 src='../images/fs.gif'><a class=td>CMM发展过程</a></td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr id='tr_node_3'>
                <td nowrap class=node><IMG BORDER=0 SRC="../images/line_b_c.gif"><img id='img_line_3' border=0 src='../images/clc.gif' onClick='ShowHide(tr_context_3,false,img_line_3,img_icon_3)'><img id='img_icon_3' border=0 src='../images/fc.gif' onClick='ShowHide(tr_context_3,false,img_line_3,img_icon_3)'>
                  <a class='td' HREF="javascript:void(0)" onclick="GotoNode(7);">&nbsp;个体软件过程</a></td>
              </tr>
              <tr id='tr_context_3'>
                <td nowrap class=node>
                  <table CELLSPACING="0" CELLPADDING="0" BORDER="0" width="100%">
                    <tr>
                      <td nowrap class=node><IMG BORDER=0 width="16" SRC="../images/transparent.gif"><IMG BORDER=0 width="16" SRC="../images/transparent.gif"><img border=0 src='../images/line_t.gif'><img border=0 src='../images/fs.gif'><a class='td' HREF="#" onclick="GotoNode(7);return false;">个体软件过程概述</a></td>
                    </tr>
                    <tr>
                      <td nowrap class=node><IMG BORDER=0 width="16" SRC="../images/transparent.gif"><IMG BORDER=0 width="16" SRC="../images/transparent.gif"><img border=0 src='../images/line_b_c.gif'><img border=0 src='../images/fs.gif'><a class=td>个体软件过程框架</a></td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </TD>
        </tr>
      </TABLE>
    </TD>
  </tr>
</TABLE>
右键菜单的DIV--%>
<script Language=JavaScript>
function bodyLoad()
{
  document.all.treebar.style.height=document.body.clientHeight-document.all.headbar.offsetHeight;
  document.all.treebar.style.width=document.body.clientWidth;
}
window.onresize = bodyLoad;
//isRoot:是否是根， isNode：是否结点(其下还有叶子)
function showMenu_normal(isRoot, isNode)
{
  //mi_edit.style.display  =isRoot ? "none": "block";
  mi_sperator.style.display = isRoot ? "none": "block";
  //mi_person.style.display = isRoot ? "none": "block";
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
    <%--tr id='mi_person' onmouseup='GotoNodeList()' class='menuItemOut' outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap>&nbsp;&nbsp;员工列表</td>
    </tr--%>
    <tr id='mi_sperator' class='menuItemOut'>
      <td nowrap><hr class='menuSperator'></td>
    </tr>
    <tr id='mi_addnode' onmouseup='GotoAddChild()' class='menuItemOut' outClass='menuItemOut' overClass='menuItemOver' onmouseover='P_OnMouseOver(this)' onmouseout='P_OnMouseOut(this)' onclick='LeaveMenu("menu_dept")'>
      <td nowrap>&nbsp;&nbsp;增加下级</td>
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

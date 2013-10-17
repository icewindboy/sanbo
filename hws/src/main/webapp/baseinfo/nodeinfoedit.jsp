<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*"%>
<html>
<head>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<%engine.erp.system.B_NodeInfo nodeBean = engine.erp.system.B_NodeInfo.getInstance(request);
  String retu = nodeBean.doService(request, response);%>
<script language="javascript">
function sumitForm(oper, row)
{
  var selfcode = form1.self_code.value = form1.self_code.value.trim();
  var deptname = form1.nodeName.value = form1.nodeName.value.trim();
  if(selfcode == '')
  {
    alert("界面编码不能为空！");
    return;
  }
  if(selfcode.length < 2)
  {
    alert('界面编码的长度不够！');
    return;
  }
  if(deptname == '')
  {
    alert("界面名称不能为空！");
    return;
  }
  disableActions();
  form1.submit();
}
function submitTree()
{
  parent.nodeinfotree.form1.operate.value = <%=nodeBean.NODE_TREE_POST%>;
  parent.nodeinfotree.form1.submit();
}
</script>
<%if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = nodeBean.dsNodeData;
  if(!ds.isOpen())
    ds.open();
  EngineRow row = new EngineRow(ds);
  if(!nodeBean.isDeptAdd)
    ds.copyTo(row);
%>
<BODY oncontextmenu="window.event.returnValue=<%=true%>">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR><TD NOWRAP>&nbsp;</TD></TR></TABLE>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="<%=nodeBean.NODE_EDIT_POST%>">
  <TABLE BORDER="0" CELLPADDING="1" CELLSPACING="3">
    <TR>
      <td noWrap class="tableTitle">界面编号</td>
      <td noWrap class="td" > <TABLE CELLSPACING="0" CELLPADDING="0" BORDER="0">
          <TR VALIGN=MIDDLE>
            <%String[] codes = nodeBean.getDeptCode();%>
            <TD NOWRAP CLASS="td"><%=codes[0]%>- <INPUT TYPE="HIDDEN" NAME="prefix_code" VALUE="<%=codes[0]%>">
              <INPUT TYPE="TEXT" NAME="self_code" VALUE="<%=codes[1]%>" SIZE="2" MAXLENGTH="2"  CLASS="edbox"></TD>
          </TR>
        </TABLE></td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">界面名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="nodeName" VALUE="<%=row.getValue("nodeName")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("nodeName").getPrecision()%>" CLASS="edbox">
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">URL</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="url" VALUE="<%=row.getValue("url")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("url").getPrecision()%>" CLASS="edbox">
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">内部编码</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="interCode" VALUE="<%=row.getValue("interCode")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("interCode").getPrecision()%>" CLASS="edbox">
      </td>
    </TR>

    <TR>
      <td noWrap class="tableTitle">是否生产</td>
      <%String isjit = row.getValue("isjit");%>
      <td noWrap class="td">
        <input type="radio" name="isjit" value="1"<%=isjit.equals("1") ? " checked" : ""%>>是
        <input type="radio" name="isjit" value="0"<%=!isjit.equals("1") ? " checked" : ""%>>否</td>
     </TR>

      <TR>
        <td colspan="2" noWrap class="tableTitle"><br> <input name="button" type="button" class="button" onClick="sumitForm();" value="保存(S)">
          <pc:shortcut key="s" script='<%="sumitForm();"%>'/>
        <input name="button2" type="button" class="button" onClick="location.href='../blank.htm'" value=" 退出 ">
          <pc:shortcut key="c" script="location.href='../blank.htm'"/>
        </td>
    </TR>
  </TABLE>
</form>
<%out.print(retu);%>
</BODY>
</Html>

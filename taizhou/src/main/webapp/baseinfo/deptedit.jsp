<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*"%>
<html>
<head>
<title>部门查看修改</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("deptlist", request, response))
    return;
  engine.erp.baseinfo.B_DeptInfo deptBean = engine.erp.baseinfo.B_DeptInfo.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script>
<%String retu = deptBean.doPost(request, response);%>
function sumitForm(oper, row)
{
  var deptname = form1.mc.value = form1.mc.value.trim();
  <%if(deptBean.isDeptAdd || !deptBean.isRoot){%>
  var selfcode = form1.self_code.value = form1.self_code.value.trim();
  if(selfcode == '')
  {
    alert("部门编码不能为空！");
    return;
  }
  if(selfcode.length < 2)
  {
    alert('部门编码的长度不够！');
    return;
  }
  <%}%>
  if(deptname == '')
  {
    alert("部门名称不能为空！");
    return;
  }
  disableActions();
  form1.submit();
}
function submitTree()
{
  parent.depttree.form1.operate.value = <%=deptBean.DEPT_TREE_POST%>;
  parent.depttree.form1.submit();
}
</script>
<%if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = (deptBean.isRoot && !deptBean.isDeptAdd) ? deptBean.dsRootDept : deptBean.dsDeptData;
  if(!ds.isOpen())
    ds.open();
  EngineRow row = new EngineRow(deptBean.dsDeptData);
  if(!deptBean.isDeptAdd)
    ds.copyTo(row);
%>
<BODY oncontextmenu="window.event.returnValue=<%=true%>">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR><TD NOWRAP>&nbsp;</TD></TR></TABLE>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="<%=deptBean.DEPT_EDIT_POST%>">
  <TABLE BORDER="0" CELLPADDING="1" CELLSPACING="3">
    <TR>
      <td noWrap class="tableTitle">部门编码</td>
      <td noWrap class="td" > <TABLE CELLSPACING="0" CELLPADDING="0" BORDER="0">
          <TR VALIGN=MIDDLE>
            <%if(!deptBean.isDeptAdd && deptBean.isRoot) out.print("&nbsp;"); else{ String[] codes = deptBean.getDeptCode();%>
            <TD NOWRAP CLASS="td"><%=codes[0]%>-
              <INPUT TYPE="HIDDEN" NAME="prefix_code" VALUE="<%=codes[0]%>"> <INPUT TYPE="TEXT" NAME="self_code" VALUE="<%=codes[1]%>" SIZE="2" MAXLENGTH="2"  CLASS="edbox"></TD>
            <%}%>
          </TR>
        </TABLE></td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">部门名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="mc" VALUE="<%=row.getValue("mc")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("mc").getPrecision()%>" CLASS="edbox">
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">部门地址</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dept_addr" VALUE="<%=row.getValue("dept_addr")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("dept_addr").getPrecision()%>" CLASS="edbox">
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">办公电话</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dept_phone" VALUE="<%=row.getValue("dept_phone")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("dept_phone").getPrecision()%>" CLASS="edbox">
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">传真</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dept_fax" VALUE="<%=row.getValue("dept_fax")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("dept_fax").getPrecision()%>" CLASS="edbox"></td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">电子邮件</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dept_email" VALUE="<%=row.getValue("dept_email")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("dept_email").getPrecision()%>" CLASS="edbox"></td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">负责人</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="hzr" VALUE="<%=row.getValue("hzr")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("hzr").getPrecision()%>" CLASS="edbox"></td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">负责人电话</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="hzrdh" VALUE="<%=row.getValue("hzrdh")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("hzrdh").getPrecision()%>" CLASS="edbox"></td>
    </TR>
    <%if(!deptBean.isRoot){%>
    <TR>
      <td noWrap class="tableTitle">是否车间</td><%String isWork = row.getValue("isWork");%>
      <td noWrap class="td"><input type="radio" name="isWork" value="1"<%=isWork.equals("1") ? " checked" : ""%>>是
        <input type="radio" name="isWork" value="0"<%=!isWork.equals("1") ? " checked" : ""%>>否</td>
    </TR>
    <%}%>
    <TR>
      <td colspan="2" noWrap class="tableTitle"><br> <input name="button" type="button" class="button" onClick="sumitForm();" value="保存(S)">
        <pc:shortcut key="s" script='sumitForm();'/>
        <input name="button2" type="button" class="button" onClick="location.href='../blank.htm'" value="退出(C)">
        <pc:shortcut key="c" script="location.href='../blank.htm'"/>
      </td>
    </TR>
  </TABLE>
</form>
<%out.print(retu);%>
</BODY>
</Html>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "bug_grade";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("bug_grade", request, response))
    return;
  engine.erp.quality.B_BugGrade BugBean=engine.erp.quality.B_BugGrade.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script>
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr><TD NOWRAP align="center">缺陷等级</TD></tr></table>
<%String retu = BugBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = BugBean.getOneTable();
  RowMap row = BugBean.getRowinfo();
  //boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
  String edClass = "class=edbox";
  //String readonly = isCanAdd?"":"readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">&nbsp;缺陷编码</td>
       <td noWrap class="td">
        <%String bugCode = row.get("bugCode");%>
        <INPUT TYPE="text" NAME="bugCode" style="width:130" VALUE="<%=bugCode%>" <%=edClass%> >
      </td>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;缺陷名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="bugName" VALUE="<%=row.get("bugName")%>" style="WIDTH:130" MAXLENGTH="<%=ds.getColumn("bugName").getPrecision()%>" <%=edClass%> >
      </td>
    </tr>
    <td colspan="2" noWrap class="tableTitle">&nbsp;&nbsp;
      <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 ">
      <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
    </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
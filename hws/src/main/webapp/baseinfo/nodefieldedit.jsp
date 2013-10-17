<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
  if(!loginBean.hasLimits("storelist", request, response))
    return;
  engine.erp.baseinfo.B_NodeFieldList b_NodeFieldListBean=engine.erp.baseinfo.B_NodeFieldList.getInstance(request);
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
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">界面列表信息</TD>
  </tr>
</table>
<%
  String retu = b_NodeFieldListBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_NodeFieldListBean.getOneTable();
  RowMap row = b_NodeFieldListBean.getRowinfo();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;表编号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="tableCode" VALUE="<%=row.get("tableCode")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("tableCode").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
   <tr>
      <td noWrap class="tableTitle">&nbsp;所属表名</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="tableName" VALUE="<%=row.get("tableName")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("tableName").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
   <tr>
      <td noWrap class="tableTitle">&nbsp;显示名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="fieldName" VALUE="<%=row.get("fieldName")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("fieldName").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;字段名</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="fieldCode" VALUE="<%=row.get("fieldCode")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("fieldCode").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
        <tr>
      <td noWrap class="tableTitle">&nbsp;关联表名</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="linkTable" VALUE="<%=row.get("linkTable")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("linkTable").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
            <tr>
      <td noWrap class="tableTitle">&nbsp;枚举值</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="enumValues" VALUE="<%=row.get("enumValues")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("enumValues").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;排序号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="orderNum" VALUE="<%=row.get("orderNum")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("orderNum").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
     <tr>
      <td noWrap class="tableTitle">&nbsp;是否显示</td>
      <td noWrap class="td">
     <%=(row.get("isShow").equals("1"))?"<INPUT TYPE='checkbox' NAME='isShow'  checked>":"<INPUT TYPE='checkbox' NAME='isShow'  >"
     %>
      </td>
    </tr>

    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 ">
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
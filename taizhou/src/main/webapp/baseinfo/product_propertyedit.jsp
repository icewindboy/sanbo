<%--物资规格属性编辑页面--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate, java.util.ArrayList"%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%>
<% String pageCode = "productinfoset";
  if(!loginBean.hasLimits("productinfoset", request, response))
    return;
  engine.erp.baseinfo.PropertyEdit  propertyEditBean = engine.erp.baseinfo.PropertyEdit.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">规格属性</TD>
  </tr></table>
<%String retu = propertyEditBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = propertyEditBean.getOneTable();
  RowMap rowinfo = propertyEditBean.getMasterRowinfo();
  EngineDataSet father = propertyEditBean.getFatherTable();
  RowMap row = propertyEditBean.getMasterRowinfo();
  boolean isEdit = loginBean.hasLimits(pageCode, op_edit);//有修改权限
  String readonly = isEdit ? "" : "readonly";
  String tableClass = isEdit ? "edbox" : "edline";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <%int rowcount = father.getRowCount();
      boolean isAdd = propertyEditBean.isAdd;
      ArrayList keys = propertyEditBean.keys;
      String sxmc=null, sxz=null;
      StringBuffer buf = new StringBuffer();
      buf.append("<tr>").append("<td nowrap class='td'>");
      father.first();
      for(int i=0; i<(isAdd ? rowcount : keys.size()); i++)
      {
        sxmc = isAdd ? father.getValue("sxmc") : keys.get(i).toString();
        buf.append(sxmc).append(":<input class=edbox size=12 maxlength=16 name='sxmc_");
        buf.append(i).append("' value='").append(rowinfo.get(sxmc)).append("'").append(">");
        father.next();
      }
      out.print(buf.toString());
      out.print("</td></tr>");
    %>
    <tr>
      <td colspan="2" noWrap class="tableTitle" align="center"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
    <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(X)">
     <pc:shortcut key="x" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
<%--执行工厂设置--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%String pageCode = "sc_process_factory";
  if(!loginBean.hasLimits("sc_process_factory", request, response))
    return;
  engine.erp.jit.B_Process_Factory  B_Process_FactoryBean  =  engine.erp.jit.B_Process_Factory.getInstance(request);
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
    <TD NOWRAP align="center">执行工厂设置</TD>
  </tr></table>
<%String retu = B_Process_FactoryBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  engine.project.LookUp workshop = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = B_Process_FactoryBean.getOneTable();
  RowMap row = B_Process_FactoryBean.getRowinfo();
  boolean isEdit = B_Process_FactoryBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);//在增加的时候又增加操作，否则必须有修改权限
  String readonly = isEdit ? "" : "readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;执行工厂编号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="fctrybm" VALUE="<%=row.get("fctrybm")%>" SIZE="50" MAXLENGTH="<%=ds.getColumn("fctrybm").getPrecision()%>" CLASS="edFocused" <%=readonly%>></td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;执行工厂名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="fctryname" VALUE="<%=row.get("fctryname")%>" SIZE="50" MAXLENGTH="<%=ds.getColumn("fctryname").getPrecision()%>" CLASS="edFocused"  <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;执行工厂描述</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="fctrybz" VALUE="<%=row.get("fctrybz")%>" SIZE="50" MAXLENGTH="<%=ds.getColumn("fctrybz").getPrecision()%>" CLASS="edFocused"  <%=readonly%>>
      </td>
    </tr>
      <td colspan="2" noWrap class="tableTitle"><br><%if(isEdit){%>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
        <%}%><input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(X)">
        <pc:shortcut key="x" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
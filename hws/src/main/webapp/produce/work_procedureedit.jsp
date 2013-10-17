<%--生产工序分段编辑页面--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
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
<%String pageCode = "work_procedure";
  if(!loginBean.hasLimits("work_procedure", request, response))
    return;
  engine.erp.produce.B_WorkProcedure  b_WorkProcedureBean  =  engine.erp.produce.B_WorkProcedure.getInstance(request);
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
    <TD NOWRAP align="center">工序分段信息</TD>
  </tr></table>
<%String retu = b_WorkProcedureBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  engine.project.LookUp workshop = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_WorkProcedureBean.getOneTable();
  RowMap row = b_WorkProcedureBean.getRowinfo();
  boolean isEdit = b_WorkProcedureBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);//在增加的时候又增加操作，否则必须有修改权限
  String readonly = isEdit ? "" : "readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;工段编号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="gdbh" VALUE="<%=row.get("gdbh")%>" SIZE="20" MAXLENGTH="<%=ds.getColumn("gdbh").getPrecision()%>" CLASS="edFocused" <%=readonly%>></td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;工段名称<em>*</em></td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="gdmc" VALUE="<%=row.get("gdmc")%>" SIZE="20" MAXLENGTH="<%=ds.getColumn("gdmc").getPrecision()%>" CLASS="edFocused"  <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;所属车间<em>*</em></td>
      <td noWrap class="td">
        <pc:select name="deptid" className="edFocused" addNull="1" style="width:130" disable='<%=isEdit?"0":"1"%>'>
        <%=workshop.getList(row.get("deptid"))%></pc:select>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;工段描述</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="gdms" VALUE="<%=row.get("gdms")%>" SIZE="20" MAXLENGTH="<%=ds.getColumn("gdms").getPrecision()%>" CLASS="edFocused"  <%=readonly%>>
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
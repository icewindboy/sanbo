<%--工艺路线组设置--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
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
<%String pageCode = "sc_gylxz";
  if(!loginBean.hasLimits("sc_gylxz", request, response))
    return;
  engine.erp.jit.B_Sc_Gylxz  B_Sc_GylxzBean  =  engine.erp.jit.B_Sc_Gylxz.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" >
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">工艺路线组设置</TD>
  </tr></table>
<%String retu = B_Sc_GylxzBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  engine.project.LookUp workshop = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = B_Sc_GylxzBean.getOneTable();
  RowMap row = B_Sc_GylxzBean.getRowinfo();
  boolean isEdit = B_Sc_GylxzBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);//在增加的时候又增加操作，否则必须有修改权限
  String readonly = isEdit ? "" : "readonly";

  engine.project.LookUp workshopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp workfactoryBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PROCESS_FACTORY_TYPE);
  workshopBean.regData(ds, "deptid");
  workfactoryBean.regData(ds, "fctryid");
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;工艺路线组编号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="gylxzbm" VALUE="<%=row.get("gylxzbm")%>" SIZE="50" MAXLENGTH="<%=ds.getColumn("gylxzbm").getPrecision()%>" CLASS="edFocused" <%=readonly%>></td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;工艺路线组名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="gylxzmc" VALUE="<%=row.get("gylxzmc")%>" SIZE="50" MAXLENGTH="<%=ds.getColumn("gylxzmc").getPrecision()%>" CLASS="edFocused"  <%=readonly%>>
      </td>
    </tr>

    <tr>
      <td noWrap class="tableTitle">&nbsp;执行工厂</td>
      <td noWrap class="td">
      <pc:select name="fctryid" className="edFocused" addNull="1" style="width:110" >
      <%=workfactoryBean.getList(row.get("fctryid"))%> </pc:select>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;所属车间</td>
      <td noWrap class="td">
      <pc:select name="deptid" className="edFocused" addNull="1" style="width:110" >
      <%=workshopBean.getList(row.get("deptid"))%> </pc:select>
      </td>
    </tr>

    <tr>
      <td noWrap class="tableTitle">&nbsp;工艺路线组描述</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="gylxzbz" VALUE="<%=row.get("gylxzbz")%>" SIZE="50" MAXLENGTH="<%=ds.getColumn("gylxzbz").getPrecision()%>" CLASS="edFocused"  <%=readonly%>>
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
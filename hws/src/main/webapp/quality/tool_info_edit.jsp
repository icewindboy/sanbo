<%@ page contentType="text/html; charset=UTF-8"%><%@ include
file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "tool_info";
%>

<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("tool_info", request, response))
    return;
//engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
engine.erp.quality.B_ToolInfo toolInfoBean=engine.erp.quality.B_ToolInfo.getInstance(request);
engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
//engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
engine.project.LookUp tooltypeBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_QUALITY_TOOLTYPE);
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
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0
CLASS="headbar"><tr><TD NOWRAP
align="center">检验器具信息</TD></tr></table>
<%String retu = toolInfoBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = toolInfoBean.getOneTable();
  RowMap row = toolInfoBean.getRowinfo();
  boolean isCanAdd =loginBean.hasLimits(pageCode,op_add)||loginBean.hasLimits(pageCode, op_edit);//权限
  boolean issearch = loginBean.hasLimits(pageCode,op_search);
  String edClass = isCanAdd?"class=edbox":"class=ednone";
  String readonly = isCanAdd?"":"readonly";
  //prodBean.regData(ds,"deptid");
  //propertyBean.regData(ds,"deptid");
  deptBean.regData(ds,"deptid");
  tooltypeBean.regData(ds,"toolTypeID");
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
<INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
<table BORDER="0" cellpadding="1" cellspacing="3">
<tr>
 <td noWrap class="tableTitle">&nbsp;器具分类:</td>
   <%--<input type="text" onKeyDown="return getNextElement();" <%=edClass%> name="toolTypeID" value='<%=tooltypeBean.getLookupName(ds.getValue("toolTypeID"))%>' style="width:130" onchange="productNameSelect(this)"  <%=readonly%>>--%>
   <td noWrap class="td">
   <%if(!isCanAdd) out.print("<input type='text' value='"+tooltypeBean.getLookupName(ds.getValue("toolTypeID"))+"' style='width:100' class='edline' readonly>");
     else {%>
      <pc:select name="toolTypeID" addNull="1" style="width:130">
      <%=tooltypeBean.getList(ds.getValue("toolTypeID"))%> </pc:select>
     <%}%>
  </td>
 <td noWrap class="tableTitle">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;部门名称:</td>
 <td noWrap class="td">
  <%String onChange = "if(form1.deptid.value!='"+row.get("deptid")+"')";%>
   <%if(!isCanAdd) out.print("<input type='text' value='"+deptBean.getLookupName(row.get("deptid"))+"' style='width:100' class='edline' readonly>");
     else {%>
      <pc:select name="deptid" addNull="1" style="width:130">
      <%=deptBean.getList(row.get("deptid"))%> </pc:select>
     <%}%>
  </td>
</tr>
<tr>
 <td noWrap class="tableTitle">&nbsp;器具编码:</td>
  <td nowrap class="td">
   <input type="text" name="toolCode" value='<%=row.get("toolCode")%>' style="width:130" <%=edClass%> <%=readonly%>>
  </td>
 <td noWrap class="tableTitle">&nbsp;&nbsp;&nbsp;器具规格型号:</td>
  <td nowrap class="td">
   <input type="text" name="toolSpec" value='<%=row.get("toolSpec")%>' style="width:130" <%=edClass%> <%=readonly%>>
  </td>
</tr>
<tr>
 <td noWrap class="tableTitle">&nbsp;器具名称:</td>
  <td nowrap class="td">
   <input type="text" name="toolName" value='<%=row.get("toolName")%>' style="width:130" <%=edClass%> <%=readonly%>>
  </td>
 <td noWrap class="tableTitle">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;器具备注:</td>
  <td nowrap class="td">
   <input type="text" name="toolMemo" value='<%=row.get("toolMemo")%>' style="width:130" <%=edClass%> <%=readonly%>>
  </td>
</tr>
 <td><input type="hidden" width="200"></td><td colspan="2" noWrap class="tableTitle">
      <%if(isCanAdd){%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input name="button" type="button" class="button"
      onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 "><%}%>
      <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
 </td>
</tr>
</table>
</form>
</BODY>
</Html>
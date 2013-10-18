<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "check_type";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
  if(!loginBean.hasLimits("check_type", request, response))
    return;
  engine.project.LookUp kindBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT_KIND);
  //kindBean.regData(list,"wzlbid");
  engine.erp.quality.B_CheckType quality_CheckBean = engine.erp.quality.B_CheckType.getInstance(request);

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
    <TD NOWRAP align="center">检验类型管理</TD>
  </tr></table>
<%String retu = quality_CheckBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = quality_CheckBean.getOneTable();
  RowMap row = quality_CheckBean.getRowinfo();
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
  String edClass = "class=edbox";
  String readonly = isCanAdd?"":"readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">&nbsp;检验类型</td>
      <td noWrap class="td">
       <pc:select name='checkstyle' style="width:130" value='<%=row.get("checktypeid")%>'>
        <pc:option value="0"></pc:option>
        <pc:option value="1">原纸</pc:option>
        <pc:option value="2">原膜</pc:option>
        <pc:option value="3">成品纸</pc:option>
        <pc:option value="4">成品膜</pc:option>
        <pc:option value="5">辅助材料</pc:option>
        <pc:option value="6">化工原料</pc:option>
        <pc:option value="7">外观</pc:option>
        <pc:option value="8">生产过程</pc:option>
      </pc:select>
</td>
      </td>
    </tr>
    <tr>

      <td noWrap class="tableTitle">&nbsp;类别</td>
      <td noWrap class="td">
    <pc:select name="wzlbid"  addNull="1"  style="width:130"  value='<%=row.get("wzlbID")%>'>
      <%=kindBean.getList()%>
       </pc:select>
     </td>
 </tr>
    <td colspan="2" noWrap class="tableTitle">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <input name="button" type="button" class="button" onClick="if(form1.v_checkstyle.value==''){alert('请选择检验类型！'); return;}sumitForm(<%=Operate.POST%>);" value=" 保存 ">
      <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
    </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
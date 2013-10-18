<%--生产工作中心编辑页面--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
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
<%String pageCode = "work_center" ;
  if(!loginBean.hasLimits("work_center", request, response))
    return;
  engine.erp.produce.B_WorkCenter  b_WorkCenterBean = engine.erp.produce.B_WorkCenter.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
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
    <TD NOWRAP align="center">工作中心信息</TD>
  </tr></table>
<%String retu = b_WorkCenterBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_WorkCenterBean.getOneTable();
  RowMap row = b_WorkCenterBean.getRowinfo();
  boolean isEdit = b_WorkCenterBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);//在增加的时候又增加操作，否则必须有修改权限
  String readonly = isEdit ? "" : "readonly";
  String isgjzx = row.get("sfgjgzzx").equals("1") ? "是" : "否";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
    <tr>
      <td noWrap class="tableTitle">工作中心编号<em>*</em></td>
      <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 180px" name="gzzxbh" value='<%=row.get("gzzxbh")%>' maxlength='10' <%=readonly%>>
      </td>
    </tr>
    <tr>
    <td noWrap class="tableTitle">工作中心名称<em>*</em></td>
      <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 180px" name="gzzxmc" value='<%=row.get("gzzxmc")%>' <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">所属部门</td>
      <td noWrap class="td">
      <%if(!isEdit)out.print("<input type='text' value='"+deptBean.getLookupName(row.get("deptid"))+"' style='width:180' class='ednone' readonly>");
      else {%><pc:select name="deptid" addNull="1" style="width:180">
      <%=deptBean.getList(row.get("deptid"))%> </pc:select>
      <%}%>
       </td>
    </tr>
	<tr>
      <td noWrap class="tableTitle">是否关键中心</td>
      <td noWrap class="td">
     <%if(!isEdit)out.print("<input type='text' value='"+isgjzx+"' style='width:160' class='ednone' readonly>");
      else {%><input type="radio" name="sfgjgzzx" value="1"<%=row.get("sfgjgzzx").equals("1") ? " checked" : ""%> checked>是
       <input type="radio" name="sfgjgzzx" value="0"<%=row.get("sfgjgzzx").equals("0") ? " checked" : ""%>>否
      <%}%></td>
	<tr>
    <td noWrap class="tableTitle">设备数</td>
      <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 180px" name="sbs" value='<%=row.get("sbs")%>' maxlength='10' onchange='reshow();' <%=readonly%>>
      </td>
    </tr>
	<tr>
    <td noWrap class="tableTitle">人工数</td>
      <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 180px" name="rgs" value='<%=row.get("rgs")%>' maxlength='10' onchange='reshow();' <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">利用率</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="lyl" VALUE="<%=row.get("lyl")%>" style="width:180" CLASS="edbox" onchange='reshow();' <%=readonly%>>%
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br><%if(isEdit){%>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
      <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
        <%}%><input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(X)">
         <pc:shortcut key="x" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
 <script language='javascript'>
   function reshow()
{
  var objsbs = form1.sbs;
  var objrgs = form1.rgs;
  var objlyl = form1.lyl;
  if(isNaN(objsbs.value))
  {
    alert('非法设备数');
    return;
  }
  sbs = parseInt(objsbs.value);
  if(sbs<1)
  {
    alert('设备数不能小于1');
    return;
  }
  if(isNaN(objrgs.value))
  {
    alert('非法人工数');
    return;
  }
  rgs = parseInt(objrgs.value);
 if(rgs<1)
 {
   alert('人工数不能小于1');
   return;
 }
  if(isNaN(objlyl.value))
  {
    alert('非法人利用率');
    return;
 }
 rgs = parseInt(objrgs.value);
 len = objlyl.value.length
 if(rgs<0 || len>3)
 {
 alert('利用率不在范围内');
 return;
 }
  }
reshow();
</script>
</BODY>
</Html>
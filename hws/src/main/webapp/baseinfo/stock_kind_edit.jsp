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
<%String pageCode = "stock_kind";
if(!loginBean.hasLimits("stock_kind", request, response))
    return;
  engine.erp.baseinfo.B_StocksKind  stocksKindBean  =  engine.erp.baseinfo.B_StocksKind.getInstance(request);
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
    <TD NOWRAP align="center">存货类别名称</TD>
  </tr></table>
<%String retu = stocksKindBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = stocksKindBean.getOneTable();
  RowMap row = stocksKindBean.getRowinfo();
  boolean isEdit = stocksKindBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);
  String readonly = isEdit ? "" : "readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">排序号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="pxh" VALUE="<%=row.get("pxh")%>" SIZE="20" CLASS="edbox" <%=readonly%>></td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">存货名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="chmc" VALUE="<%=row.get("chmc")%>" SIZE="20" MAXLENGTH="<%=ds.getColumn("chmc").getPrecision()%>" CLASS="edbox" <%=readonly%>>
      </td>
    </tr>
    <tr>
    <%String ckdgs= row.get("ckdgs");%>
      <td noWrap class="tableTitle">出库单格式</td>
      <td noWrap class="td"><%if(!isEdit){%><input type='text' value="<%=ckdgs.equals("0") ? "不套打" : (ckdgs.equals("1") ? "膜格式套打" : "纸格式套打")%>" style='width:130' class='edline' readonly>
      <%}else{%>
                      <pc:select name="ckdgs" style="width:130" value='<%=ckdgs%>'>
                      <pc:option value='0'>不套打</pc:option>
                      <pc:option value='1'>膜格式套打</pc:option>
                      <pc:option value='2'>纸格式套打</pc:option>
                      </pc:select>
      <%}%>
      </td>
    </tr>
      <td colspan="2" noWrap class="tableTitle"><br><%if(isEdit){%>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
       <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(X)">
         <pc:shortcut key="x" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
<script language='javascript'>
  function reshow()
{
  var objpxh = form1.pxh;
  if(isNaN(objpxh.value) )
  {
    alert('排序号非法');
    return;
  }
  if(objpxh.value == '' )
  {
    alert('排序号不能为空');
    return;
  }
}
  </script>
</BODY>
</Html>

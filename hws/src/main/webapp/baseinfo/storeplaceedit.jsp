<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("storeplacelist", request, response))
    return;
  engine.erp.baseinfo.B_StorePlace  storePlaceBean = engine.erp.baseinfo.B_StorePlace.getInstance(request);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
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
    <TD NOWRAP align="center">库位信息</TD>
  </tr></table>
<%String retu = storePlaceBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = storePlaceBean.getOneTable();
  RowMap row = storePlaceBean.getRowinfo();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;所在仓库</td>
      <td noWrap class="td"><pc:select name="storeid" style="width:180">
       <%=storeBean.getList(row.get("storeid"))%></pc:select>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;库位编码</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dm" VALUE="<%=row.get("dm")%>" style="width:180" MAXLENGTH="<%=ds.getColumn("dm").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;库位名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="mc" VALUE="<%=row.get("mc")%>" style="width:180" MAXLENGTH="<%=ds.getColumn("mc").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+");"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value="关闭(C)">
        <pc:shortcut key="c" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
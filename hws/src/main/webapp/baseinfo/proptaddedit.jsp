<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.*,engine.erp.baseinfo.*,java.util.*"%>
<html>
<head>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
  if(!loginBean.hasLimits("propertylist", request, response))
    return;
  engine.erp.baseinfo.SalePropertiesBean propertylist=engine.erp.baseinfo.SalePropertiesBean.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

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
    <TD NOWRAP align="center">界面列表信息</TD>
  </tr>
</table>
<%
  ArrayList opkey = new ArrayList(); opkey.add("xs_ht"); opkey.add("xs_hthw");
  ArrayList opval = new ArrayList(); opval.add("销售订单主表"); opval.add("销售订单从表");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};
  ArrayList num_opkey = new ArrayList(); num_opkey.add("1"); num_opkey.add("2");num_opkey.add("3");
  ArrayList num_opval = new ArrayList(); num_opval.add("字符型"); num_opval.add("文本型");num_opval.add("枚举型");
  ArrayList[] num_lists  = new ArrayList[]{num_opkey, num_opval};
  String retu = propertylist.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = propertylist.getOneTable();
  RowMap row = propertylist.getRowinfo();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;表名称</td>
      <td noWrap class="td">
        <pc:select name="tableCode"  style="width:130" >
        <%=SalePropertiesBean.listToOption(lists, opkey.indexOf(row.get("tableCode")))%>
        </pc:select>
      </td>
    </tr>
   <tr>
      <td noWrap class="tableTitle">&nbsp;属性名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="fieldName" VALUE="<%=row.get("fieldName")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("fieldName").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
   <tr>
      <td noWrap class="tableTitle">&nbsp;数据长度</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dataLen" VALUE="<%=row.get("dataLen")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("dataLen").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;类型</td>
      <td noWrap class="td">
        <pc:select name="inputType"  style="width:130" >
        <%=SalePropertiesBean.listToOption(num_lists, num_opkey.indexOf(row.get("inputType")))%>
        </pc:select>
      </td>
    </tr>
        <tr>
      <td noWrap class="tableTitle">&nbsp;描述</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="describe" VALUE="<%=row.get("describe")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("describe").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <%String sav = "sumitForm("+Operate.POST+");";%>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S) ">
        <pc:shortcut key="s" script='<%=sav%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(X) ">
        <pc:shortcut key="x" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>



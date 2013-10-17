<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.* ,engine.project.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "sale_order_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.sale.B_Store_Capacity b_Store_CapacityBean = engine.erp.sale.B_Store_Capacity.getInstance(request);

%>
<%
  String retu = b_Store_CapacityBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD NOWRAP align="center">货物库存量参考</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" align="right">
      <input name="button2" type="button" class="button" onClick="window.close()" value=" 关闭 "></td>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap  width=70>产品编码</td>
      <td nowrap>品名</td>
      <td nowrap>规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>合同数量</td>
      <td nowrap>当前库存量</td>
      <td nowrap>可供量</td>
      <td nowrap>差额</td>
      <td nowrap>批号</td>
    </tr>
    <%//b_Store_CapacityBean.fetchData(loginBean.getRowMin(request), loginBean.getRowMax(request), true);
      int iPage = loginBean.getPageSize()-10;
      RowMap [] detail = b_Store_CapacityBean.getDetailRowinfos();
      int i=0;
      for(; i < detail.length; i++) {
        RowMap detailRow = detail[i];
    %>
    <tr >
      <td class="td" nowrap><%=detailRow.get("cpbm")%></td>
      <td class="td"><%=detailRow.get("pm")%></td>
      <td class="td"><%=detailRow.get("gg")%></td>
      <td class="td"><%=detailRow.get("sxz")%></td>
      <td class="td"><%=detailRow.get("sl")%></td>
      <td class="td"><%=detailRow.get("zl")%></td>
      <td class="td" nowrap><%=detailRow.get("xskgl")%></td>
      <td class="td" nowrap><%=detailRow.get("ce")%></td>
      <td class="td" nowrap><%=detailRow.get("ph")%></td>
    </tr>
    <%
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
  </form>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1); </SCRIPT>
<%out.print(retu);%>
</body>
</html>
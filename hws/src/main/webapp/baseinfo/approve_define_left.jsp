<%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*,engine.project.*"%>
<%
  engine.erp.system.B_ApproveDefine b_ApproveDefineBean = engine.erp.system.B_ApproveDefine.getInstance(request);
  String pageCode = "approve_define";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
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
function toDetail(){
  parent.location.href='approve_define_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='approve_define_right.jsp?operate=<%=b_ApproveDefineBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">审批项目列表</TD>
  </TR>
</TABLE>
<%
  String retu = b_ApproveDefineBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = b_ApproveDefineBean.getMaterTable();//主表数据集
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
    <td>&nbsp;</td>
    <td>审批项目</td>
    </tr>
    <%
      int count = list.getRowCount();//行数
      int i=0;
      list.first();
      for(; i<count; i++){
    %>
    <tr id="tr_<%=list.getRow()%>" onClick="showDetail(<%=list.getRow()%>)">
       <td align="center" nowrap><%=i+1%></td>
       <td class="td" nowrap><%=list.getValue("spxmmc")%></td>
    </tr>
    <%  list.next();
      }
    %>
  </table>
</form>
<script LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
</script>
</body>
</html>

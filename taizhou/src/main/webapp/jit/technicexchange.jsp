<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function submitForm(oper, row)
{
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function saveForm(oper, row){
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
</script>
<%
  String pageCode = "technics_route";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
 engine.erp.jit.B_TechnicsRoute b_TechnicsRouteBean = engine.erp.jit.B_TechnicsRoute.getInstance(request);
 String result = b_TechnicsRouteBean.doService(request, response);
 engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_NAME);
 String curUrl = request.getRequestURL().toString();
%>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
      <TD NOWRAP align="center">可选工序</TD>
  </TR>
</TABLE>
  <table id="tableview1" width="80%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap colspan="2"><input name="image" class="img" type="image" title="新增" onClick="submitForm(<%=b_TechnicsRouteBean.TECNICE_XCHANGE_ADD%>,-1)" src="../images/add_big.gif" border="0"></td>
      <td nowrap>工序名称</td>
      <td nowrap>调整率</td>
      <td nowrap>基准单价</td>
    </tr>
    <%
      java.util.ArrayList tecRows = b_TechnicsRouteBean.gettecnicRows();
      EngineDataSet list = b_TechnicsRouteBean.getDetailTable();
      String gxfdid=list.getValue("gxfdid");
      RowMap detail = null;
      int i=0;
      for(; i<tecRows.size(); i++)   {
        detail = (RowMap)tecRows.get(i);
    %>
    <tr>
      <td class="td" nowrap align="center" width="10"><%=i+1%></td>
      <td class="td" nowrap align="center" width="20"><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) submitForm(<%=b_TechnicsRouteBean.TECNICE_XCHANGE_DEL%>,<%=i%>)" src="../images/del.gif" border="0">
      </td>
      <td  noWrap class="td">
      <pc:select name='<%="gymcid_"+i%>' addNull="1" style="width:110">
        <%=technicsNameBean.getList(detail.get("gymcid"), "gxfdid", list.getValue("gxfdid"))%></pc:select></td>
     <td  noWrap class="td">
    <input type="text" style="width:80" class="edFocused_r" name="quot_<%=i%>" id="quot_<%=i%>" value=<%=detail.get("quot")%>>
    </td>
     <td  noWrap class="td">
    <input type="text" style="width:80" class="edFocused_r" name="deje_<%=i%>" id="deje_<%=i%>" value=<%=detail.get("deje")%>>
    </td>
    </tr>
    <%
      }
      for(; i < 8; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
    </tr>
    <%}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);
    function hide(){
      parent.hideFrameNoFresh()
    }
  </script>
  <TABLE width="75%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
      <TD align="center">
        <input name="button" type="button" class="button" onClick="saveForm(<%=b_TechnicsRouteBean.TECNICE_XCHANGE_POST%>);" value="确定(O)">
        <pc:shortcut key="o" script=''/>
        <input name="button2" type="button" class="button" onClick="hide()" value="关闭(X)">
        <pc:shortcut key="x" script='<%="hide()"%>'/>
      </TD>
    </TR>
  </TABLE>
</form>
<%out.print(result);%>
</BODY>
</Html>
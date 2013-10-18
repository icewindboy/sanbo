<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>

<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "certified_card_set";
%>
<%if(!loginBean.hasLimits("certified_card_set", request, response))
    return;
  //engine.erp.quality.B_CheckItem quality_CheckBean =engine.erp.quality.B_CheckItem.getInstance(request);
  engine.erp.quality.B_CertifiedCardSet quality_CheckBean =engine.erp.quality.B_CertifiedCardSet.getInstance(request);
  //engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project);
  String retu = quality_CheckBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script>
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
</head>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="70%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>合格证打印设置</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = quality_CheckBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);//op_edit
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="70%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>
     <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
</td> <TD align="right"> <input name="button" type="button" align="Right"
  class="button" onClick="if(confirm('是否保存记录？')) sumitForm(<%=Operate.POST%>);" value=" 保存 "border="0">
<%if(quality_CheckBean.retuUrl!=null){%><input name="button2222232" type="button" align="Right"
  class="button" onClick="location.href='<%=quality_CheckBean.retuUrl%>'" value=" 返回 "border="0"><%}%></TD>
</TR>
  </TABLE>
  <table id="tableview1" width="70%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td  nowarp>存货类别名称</td>
      <td  nowarp>排序号</td>
      <td  nowarp width="130">打印格式</td>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td" nowarp>&nbsp;<%=list.getValue("chmc")%></td>
      <td class="td" nowarp>&nbsp;<%=list.getValue("pxh")%></td>
      <td class="td" nowarp width:110>
        <pc:select name='<%="dygs_"+i%>' style="width:130" value='<%=list.getValue("dygs")%>'>
        <pc:option value="1">卷筒纸</pc:option>
        <pc:option value="2">膜格式</pc:option>
        <pc:option value="3">平张纸</pc:option>
      </pc:select></td>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize()-13; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
    </tr>
    <%}%>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<%out.print(retu);%>
</body>
</html>
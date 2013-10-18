<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "system_log";
%>
<%
  if(!loginBean.hasLimits("system_log", request, response))
    return;
   engine.erp.system.B_SystemLog b_SystemLogBean  =   engine.erp.system.B_SystemLog.getInstance(request);
  String retu = b_SystemLogBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
  out.print(retu);
%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<SCRIPT LANGUAGE="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function hideInterFrame(){//隐藏FRAME
    hideFrame('detailDiv');
    form1.submit();
  }
  function hideFrameNoFresh(){
    hideFrame('detailDiv');
  }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>系统日志</TD>
  </TR>
</TABLE>
<%
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);//引用人员信息

  EngineDataSet list = b_SystemLogBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  boolean isCanDelete = true;
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit = false;
  personBean.regData(list, "personid");
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="600" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <td class="td" nowrap>
     <%
         String key = "ppdfsgg";
         pageContext.setAttribute(key, list);
         int iPage = loginBean.getPageSize();
         String pageSize = ""+iPage;
    %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
    <TR>
    <TD align="right">
    <input name = "button" type = "button" align = "Right" title = "清空" class = "button" onClick = "sumitForm(<%=b_SystemLogBean.DATABASE_CLEAN%>,-1)" value = " 清空(D) " border = "0">
    <input name="button2" type="button" align="Right" title = "返回" class="button" onClick="location.href='<%=b_SystemLogBean.retuUrl%>'" value=" 返回(C) "border="0">
    <pc:shortcut key="c" script=''/>
   </TD></TR>
  </TABLE>
  <table id="tableview1" width="600" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <COLGROUP valign="middle" span="4">
    <COLGROUP width=60 align="center">
    <tr class="tableTitle">
      <td></td>
      <td>登录姓名</td>
      <td>操作日期</td>
      <td>IP地址</td>
      <td>操作模式</td>
    </tr>
    <%
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr <%if(isCanEdit){%>onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)"<%}%> >
      <td class="td">
      <%if(isCanDelete){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0"><%}%>
      </td>
      <td class="td"><%=personBean.getLookupName(list.getValue("personid"))%></td>
      <td class="td"><%=list.getValue("czrq")%></td>
      <td class="td"><%=list.getValue("ip")%></td>
      <td class="td"><%=list.getValue("czms")%></td>
    </tr>
    <%
      list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="300" height="200" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>

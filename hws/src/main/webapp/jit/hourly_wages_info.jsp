<%--车间流转单--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal,engine.erp.baseinfo.BasePublicClass"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%
  String pageCode = "hourly_wages_info";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.jit.HourlyWagesInfo hourWageInfoBean = engine.erp.jit.HourlyWagesInfo.getInstance(request);
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String retu = hourWageInfoBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
%>
<jsp:include page="../baseinfo/script.jsp" flush="true"/>
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
  function showInterFrame(oper, rownum)
  {
    var url = "hourly_wages_info_edit.jsp.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
  }

  function hideInterFrame()//隐藏FRAME
  {
    lockScreenToWait("处理中, 请稍候！");
    hideFrame('detailDiv');
    form1.submit();
  }
  function hideFrameNoFresh(){
    hideFrame('detailDiv');
  }
  function showFixedQuery()
  {
    showFrame('fixedQuery', true, "", true);
  }
  function deptChange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('fixedQueryform.deptid.value'), '',true);
   //associateSelect(document.all['prod1'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jsr', 'jsr', eval('form1.jsr.value'), '',true);
  }
  function sumitFixedQuery(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    fixedQueryform.operate.value = <%=Operate.FIXED_SEARCH%>;
    fixedQueryform.operate.value = oper;
    fixedQueryform.submit();
  }
  //返回
function buttonEventC()
{
  location.href='<%=hourWageInfoBean.retuUrl%>';
  }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe style="display:none" id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>车间计时工资设置</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = hourWageInfoBean.getOneTable();
  HtmlTableProducer table = hourWageInfoBean.masterProducer;
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  deptBean.regData(list,"deptid");
  personBean.regConditionData(list, "deptid");
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="90%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>
     <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
   </td>

   <TD align="right">
       <%if (hasSearchLimit) {%>
       <input name="buttonq" type="button" align="Right" class="button" title="查询(ALT+Q)" value="查询(Q)" onClick="showFixedQuery()" border="0">
        <pc:shortcut key="q" script="showFixedQuery()"/>
      <%}%>
    <%if(hourWageInfoBean.retuUrl!=null){%><input name="button2222232" type="button" align="Right"
   class="button" onClick="location.href='<%=hourWageInfoBean.retuUrl%>'" title="返回(ALT+C)"  value=" 返回(C) "border="0"><%}%>
      <pc:shortcut key="c" script="buttonEventC()"/>
   </TD>

    </TR>
  </TABLE>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td width="45"><%if(loginBean.hasLimits(pageCode, op_add)){%><input name="image" class="img" type="image" title="新增" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <%}%>
      </td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td">
      <%if(loginBean.hasLimits(pageCode, op_edit)){%><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%}%>
      <%if(loginBean.hasLimits(pageCode, op_delete)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      <%}%></td>
     <%table.printCells(pageContext, "class=td");%>
    </tr>
    <%  list.next();
      }
    %>
    <%
      for(; i < loginBean.getPageSize();i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td></tr>");
      }
      %>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<%out.print(retu);%>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" id="cc" align="right" style="width:400"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="400" height="250" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" id="ss" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <table BORDER="0" cellpadding="1" cellspacing="3">
      <tr>
        <td noWra >&nbsp;车间</td>
        <td><pc:select name="deptid" addNull="1" style="width:110" onSelect="deptChange()">
          <%=deptBean.getList()%> </pc:select> </td>
      </tr>
      <tr>
        <td noWrap>&nbsp;人员</td>
        <td><pc:select name="personid" addNull="1" style="width:110"> <%=personBean.getList()%>
          </pc:select> </td>
      </tr>
      <td noWrap colspan="2"> <div align="center">
          <input name="button" type="button" class="button" title="查询(ALT+F)"   value="查询(F)" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>);">
          <pc:shortcut key="f" script='<%="sumitFixedQuery("+Operate.FIXED_SEARCH+")"%>'/>
          <input name="button2" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)" onClick="hideFrame('fixedQuery')">
          <pc:shortcut key="t" script="hideFrame('fixedQuery')"/> </div></td>
    </table>
  </div>
</form>
</body>
</html>
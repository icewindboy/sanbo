<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "common_unit";
%>
<%
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
   engine.erp.baseinfo.B_CommonUnit commonUnitBean  =   engine.erp.baseinfo.B_CommonUnit.getInstance(request);
  String retu = commonUnitBean.doService(request, response);
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

<script LANGUAGE="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum){
    var url = "common_unit_edit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
  }
  function hideInterFrame(){//隐藏FRAME
    hideFrame('detailDiv');
    form1.submit();
  }
  function hideFrameNoFresh(){
    hideFrame('detailDiv');
  }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>常用单位设置</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = commonUnitBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="600" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR><TD align="right">
    <%if(loginBean.hasLimits(pageCode,op_add)) { String ret = "location.href='("+commonUnitBean.retuUrl+",-1)";%>
    <%if(commonUnitBean.retuUrl!=null)%>
    <input name="button2222232" type="button" align="Right" title = "返回" class="button" onClick="location.href='<%=commonUnitBean.retuUrl%>'" value=" 返回(C) "border="0"><pc:shortcut key="c" script='<%=ret%>'/><%}%>
   </TD></TR>
  </TABLE>
  <table id="tableview1" width="600" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <COLGROUP valign="middle" span="4">
    <COLGROUP width=60 align="center">
    <tr class="tableTitle">
      <td>
      <%if(loginBean.hasLimits(pageCode,op_add)) { String add = "showInterFrame("+Operate.ADD+",-1)";%>
      <%if(isCanAdd)%><input name="image" class="img" type="image" title="新增" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0"><pc:shortcut key="a" script='<%=add%>'/><%}%>
      </td>
      <td>单位编号</td>
      <td>单位名称</td>
    </tr>
    <%
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr <%if(isCanEdit){%>onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)"<%}%> >
      <td class="td">
      <%if(isCanEdit){%><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0"><%}%>
      <%if(isCanDelete){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0"><%}%>
      </td>
      <td class="td"><%=list.getValue("code")%></td>
      <td class="td"><%=list.getValue("name")%></td>
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
    </tr>
    <%}%>
  </table>
  <script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</script>
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
<%=retu%>
</body>
</html>

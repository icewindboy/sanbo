<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%if(!loginBean.hasLimits("nodepriviligelist", request, response))
    return;
  engine.erp.system.B_nodePrivilige nodePrivilige = engine.erp.system.B_nodePrivilige.getInstance(request);
  String retu = nodePrivilige.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
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
    var url = "nodepriviligeedit.jsp?operate="+oper+"&rownum="+rownum;
    showFrame('interframe1', true, url);
  }
  function hideInterFrame(){
    hideFrame('interframe1');
    form1.submit();
  }
  function hideFrameNoFresh(){
    hideFrame('interframe1');
  }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">界面权限</TD>
  </TR></TABLE>
<%EngineDataSet list = nodePrivilige.getOneTable();
  String curUrl = request.getRequestURL().toString();
%>
<INPUT TYPE="HIDDEN" NAME="operate2" VALUE="">
<INPUT TYPE="HIDDEN" NAME="rownum2" VALUE="">
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="600" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
      <TD align="right">
        <%if(nodePrivilige.retuUrl!=null){%>
        <input name="button2222232" type="button" align="Right"
  class="button" onClick="location.href='<%=nodePrivilige.retuUrl%>'" value=" 返回 "border="0">
        <%}%>
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="600" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <COLGROUP valign="middle">
	<COLGROUP width=60 align="center">
	<COLGROUP valign="middle">
    <COLGROUP width=60 align="center">
    <tr class="tableTitle">
      <td>权限编码</td>
      <td>权限名称</td>
      <td>权限描述</td>
      <td><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/>
      </td>
    </tr>
    <%list.first();
      int i=0;
      for(int j=0; j<list.getRowCount(); j++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td"><%=list.format("priviligeCode")%></td>
      <td class="td"><%=list.format("priviligeName")%></td>
      <td class="td"><%=list.format("priviligeMemo")%></td>
      <td class="td"><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      </td>
    </tr>
    <%  i++;
        list.next();
      }
      for(; i < loginBean.getPageSize()-2; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table><script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</script>
  <%out.print(retu);%>
</form>
  <iframe id="interframe1" src="" class="frameBox" width="330" height="180" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
</body>
</html>

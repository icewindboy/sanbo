<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%//所有权限列表
  engine.erp.system.B_nodePrivilige nodePrivilige = engine.erp.system.B_nodePrivilige.getInstance(request);
  engine.erp.system.B_NodeInfo nodeBean = engine.erp.system.B_NodeInfo.getInstance(request);
  String retu = nodeBean.doService(request, response);//
  if(retu.indexOf("location.href=")>-1)
    return;
  EngineDataSet list = nodeBean.dsNodePrivilige;
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

<script LANGUAGE="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum){
    var url = "priviligeedit.jsp?operate="+oper+"&rownum="+rownum;
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
    <TD NOWRAP align="center">界面权限维护表</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <br>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <COLGROUP valign="middle" span="3">
    <COLGROUP valign="middle" align="center" width="45">
    <tr class="tableTitle">
      <td>权限代号</td>
      <td>权限编码</td>
      <td>权限名称</td>
      <td><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/>
      </td>
    </tr>
    <%list.first();
      int i=0;
      for(int j=0; j<list.getRowCount(); j++)   {
        String id = list.getValue("priviligeId");
        RowMap row = nodePrivilige.getPriviligeRow(id);
    %>
    <tr onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td"><%=id%></td>
      <td class="td"><%=row.get("priviligeCode")%></td>
      <td class="td"><%=row.get("priviligeName")%></td>
      <td class="td"><input name="image2" class="img" type="image" title="修改" onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      </td>
    </tr>
    <%    i++;
        list.next();}
        for(; i < 17; i++){

    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
<script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
  function submitFixedQuery(oper)
  {
    lockScreenToWait("处理中, 请稍候！");
    fixedQueryform.operate.value = oper;
    fixedQueryform.submit();
  }
  function showFixedQuery(){
    //hideFrame('searchframe');
    showFrame('fixedQuery', true, "", true);
  }
</script>
  </form>
  <form name="fixedQueryform" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
    <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
    <div class="queryPop" id="fixedQuery" name="fixedQuery">
      <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
        <TABLE BORDER="0" CELLPADDING="1" CELLSPACING="3">
          <TR>
            <td noWrap class="tableTitle">&nbsp;权限代号&nbsp;</td>
            <td noWrap class="td"><pc:select name="priviligeId" style="width:222">
                <%=nodePrivilige.getNodePriviligeForOption(nodeBean.rowInfo.get("priviligeId"))%>
            </pc:select></td>
          </TR>
          <TR>
            <td colspan="2" noWrap class="tableTitle"><input name="button" type="button" class="button" onClick="submitFixedQuery(<%=Operate.POST%>);" value="保存(S)">
              <pc:shortcut key="s" script='<%="submitFixedQuery("+Operate.POST+");"%>'/>
              <input name="button2" type="button" class="button" onClick="hideFrame('fixedQuery')" value="关闭(C)">
              <pc:shortcut key="c" script="hideFrame('fixedQuery')"/>
            </td>
          </TR>
        </TABLE>
      </div>
    </div>
  </form>
  <%out.print(retu);%>
  <%--frame id="interframe1" src="" class="frameBox" width="330" height="150" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe--%>
</body>
</html>

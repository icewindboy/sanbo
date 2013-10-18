<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%
  if(!loginBean.hasLimits("storelist", request, response))
    return;
  engine.erp.baseinfo.B_NodeFieldList b_NodeFieldListBean  =  engine.erp.baseinfo.B_NodeFieldList.getInstance(request);
  String retu = b_NodeFieldListBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
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
    //设置表单隐藏字段的值(operate,rownum)
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum){
    var url = "nodefieldedit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
  }
  function hideInterFrame(){
    hideFrame('detailDiv');
    form1.submit();
  }
 function hideInterFrame()//隐藏FRAME
 {
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
    <TD colspan="6" align="center" NOWRAP>界面字段列表</TD>
  </TR>
</TABLE>
  <%
    out.print(retu);
    EngineDataSet list = b_NodeFieldListBean.getOneTable();
    String curUrl = request.getRequestURL().toString();
  %>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
      <TD align="right">
  <%
    if(b_NodeFieldListBean.retuUrl!=null)
    {
  %>
<input name="button2222232" type="button" align="Right"
  class="button" onClick="location.href='<%=b_NodeFieldListBean.retuUrl%>'" value=" 返回 "border="0">
  <%}%>
    </TD>
    </TR>
  </TABLE>
<table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>表编号</td>
      <td nowrap>所属表名</td>
      <td nowrap>显示名称</td>
      <td nowrap>字段名</td>
      <td nowrap>关联表名</td>
      <td nowrap>枚举值</td>
      <td nowrap>排序号</td>
      <td nowrap>是否显示</td>
      <td width=45 align="center" nowrap><input name="image" class="img" type="image" title="新增" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0"></td>
    </tr>
    <%
      list.first();
     int i=0;
     for(; i<list.getRowCount(); i++)
     {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td" nowrap><%=list.getValue("tableCode")%></td>
      <td class="td" nowrap><%=list.getValue("tableName")%></td>
      <td class="td" nowrap><%=list.getValue("fieldName")%></td>
      <td class="td" nowrap><%=list.getValue("fieldCode")%></td>
      <td class="td" nowrap><%=list.getValue("linkTable")%></td>
      <td class="td" nowrap><%=list.getValue("enumValues")%></td>
      <td class="td" nowrap><%=list.getValue("orderNum")%></td>
      <td class="td" nowrap align="center"><INPUT TYPE='checkbox' NAME='isShow' onClick='sumitForm("<%=b_NodeFieldListBean.CHANGE_ISSHOW%>",<%=list.getRow()%>)'<%=list.getValue("isShow").equals("1")?" checked" :""%>></td>
      <td class="td" nowrap>
      <input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%--<input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">--%>
      </td>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
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
  <script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="320" height="325" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>

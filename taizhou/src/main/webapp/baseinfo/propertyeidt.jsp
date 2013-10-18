<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.erp.baseinfo.*,engine.action.Operate,engine.project.*"%>
<%
  if(!loginBean.hasLimits("propertylist",request,response))
   return;
  SalePropertiesBean salsepropertiesbean=SalePropertiesBean.getInstance(request);
%>
<html>
<head>
<title>销售订单属性定义</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript">
function toDetail(){
  location.href='enumvaluelist.jsp';
}
function showInterFrame(oper, rownum){
  var url = "proptaddedit.jsp?operate="+oper+"&rownum="+rownum;
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
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
</script>
<%

  String retu = salsepropertiesbean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = salsepropertiesbean.getOneTable();
  String curUrl = request.getRequestURL().toString();
%>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD NOWRAP align="center">销售订单属性定义</TD>
  </TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="curUrl" VALUE="">
  <table id="tbcontrol" width="80%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap></td>
      <td class="td" align="right">
        <%String ret = "location.href='"+salsepropertiesbean.retuUrl+"'";%>
        <input name="button22" type="button" class="button" onClick="location.href='<%=salsepropertiesbean.retuUrl%>'" value=" 返回(X) ">
        <pc:shortcut key="x" script='<%=ret%>'/>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="80%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>表名称</td>
      <td nowrap>属性名称</td>
      <td nowrap>数据长度</td>
      <td nowrap>类型</td>
      <td nowrap>描述</td>
      <td nowrap>枚举值</td>
      <td nowrap width=40 align="center">
      <%String add = "showInterFrame("+Operate.ADD+",-1)";%>
      <input name="image" class="img" type="image" title="新增(A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
       <pc:shortcut key="a" script='<%=add%>'/>
      </td>
    </tr>
    <%
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <INPUT TYPE="HIDDEN" NAME="nodeFieldID" VALUE=<%=list.getValue("nodeFieldID")%>>
    <INPUT TYPE="HIDDEN" NAME="fieldCode" VALUE=<%=list.getValue("fieldCode")%>>
      <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td" nowrap><%=list.getValue("tableName")%></td>
      <td class="td" nowrap><%=list.getValue("fieldName")%></td>
      <td class="td" nowrap><%=list.getValue("dataLen")%></td>
      <td class="td" nowrap><%=SalePropertiesBean.chckList(list.getValue("inputType"))%></td>
      <td class="td" nowrap><%=list.getValue("describe")%></td>
      <td class="td" nowrap><%if(list.getValue("inputType").equals("3")){%>
      <a href="javascript:sumitForm(<%=SalePropertiesBean.ENUM_DEFINE%>,<%=list.getRow()%>)">枚举值定义</a>
      <%}%></td>
      <td class="td" nowrap><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <input name="image3" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      </td>
    </tr>
    <%  list.next();
      }
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr>
      <td class="td">&nbsp;</td>
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
<script LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
</script>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="320" height="325" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>

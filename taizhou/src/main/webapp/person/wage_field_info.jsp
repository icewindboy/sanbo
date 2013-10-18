<%--收发单据类别--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String pageCode = "wage_field_info";
if(!loginBean.hasLimits("wage_field_info", request, response))
    return;
  engine.erp.person.B_WageFieldInfo b_WageFieldInfoBean  =  engine.erp.person.B_WageFieldInfo.getInstance(request);
  String retu = b_WageFieldInfoBean.doService(request, response);
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
    var url = "wage_field_info_edit.jsp?operate="+oper+"&rownum="+rownum;
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
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>工资款项设置</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = b_WageFieldInfoBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>
     <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
    </td>
    <TD align="right">
     <%
     if(b_WageFieldInfoBean.retuUrl!=null){
       String s = "location.href='"+b_WageFieldInfoBean.retuUrl+"'";
     %>
     <input name="button2222232" type="button" align="Right"  class="button" onClick="location.href='<%=b_WageFieldInfoBean.retuUrl%>'" value=" 返回(C) "border="0">
     <pc:shortcut key="c" script="<%=s%>" />
    <%}%>
    </TD>
  </TR>
  </TABLE>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr  hight="20" class="tableTitle">
      <td nowarp style="width:50" >
      <input name="image" class="img" type="image" title="新增(A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/>
      </td>
      <td  nowarp style="width:100">名 称</td>
      <td  nowarp style="width:50">类 型</td>
      <td  nowarp style="width:30">长 度</td>
      <td  nowarp style="width:30">精 度</td>
      <td  nowarp style="width:50">来 源</td>
      <td  nowarp style="width:550">计算公式</td>
      <td  nowarp style="width:30">排序号</td>
    </tr>
    <%
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td" nowarp>&nbsp; <input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      </td>
      <td class="td" nowarp><%=list.getValue("mc")%> </td>
      <td class="td" nowarp><%=list.getValue("lx").equals("1")?"字符型":(list.getValue("lx").equals("2")?"文本型":"数值型")%></td>
      <td class="td" nowarp align="right"><%=list.getValue("cd")%></td>
      <td class="td" nowarp align="right"><%=list.getValue("jd")%></td>
      <td class="td" nowarp><%=list.getValue("ly").equals("1")?"直接输入":"公式计算"%></td>
      <td class="td" nowarp align="left"><%=list.getValue("jsgs")%></td>
      <td class="td" nowarp align="right"><%=list.getValue("pxh")%></td>

    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td">&nbsp;</td><td class="td"></td>
      <td class="td"></td><td class="td"></td>
      <td class="td"></td><td class="td"></td>
      <td class="td"></td><td class="td"></td>
    </tr>
    <%}%>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form><%out.print(retu);%>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="450" height="200" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>
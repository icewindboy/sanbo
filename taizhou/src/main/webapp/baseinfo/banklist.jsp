<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<title>银行列表</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("banklist", request, response))
    return;
  engine.erp.baseinfo.B_Bank bankBean = engine.erp.baseinfo.B_Bank.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>

<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">银行列表</TD>
  </TR></TABLE>
<%String retu = bankBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;

  EngineDataSet list = bankBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <COLGROUP width=70 valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP width=70 align="center">
    <tr class="tableTitle">
      <td>编码</td>
      <td>银行名称</td>
      <td>地址</td>
      <td>电话</td>
      <td>联系人</td>
      <td><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/>
      </td>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=i%>)">
      <td class="td"><%=list.format("dm")%></td>
      <td class="td"><%=list.format("yhmc")%></td>
      <td class="td"><%=list.getValue("addr")%></td>
      <td class="td"><%=list.getValue("tel")%></td>
      <td class="td"><%=list.getValue("lxr")%></td>
      <td class="td"> <input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i%>)" src="../images/del.gif" border="0">
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
    </tr>
    <%}%>
  </table>
  <script LANGUAGE="javascript">
    initDefaultTableRow('tableview1',1);
    function sumitForm(oper, row)
    {
      lockScreenToWait("处理中, 请稍候！");
      form1.operate.value = oper;
      form1.rownum.value = row;
      form1.submit();
    }
    function showInterFrame(oper, rownum)
  {
    var url = "bankedit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
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
  </form>
  <div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="350" height="225" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>

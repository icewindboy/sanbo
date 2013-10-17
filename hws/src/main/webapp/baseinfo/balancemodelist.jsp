<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*"%>
<html>
<head>
<title>结算方式列表</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("balancemodelist", request, response))
    return;
  engine.erp.baseinfo.B_BalanceMode balanceBean = engine.erp.baseinfo.B_BalanceMode.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>

<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">结算方式列表</TD>
  </TR></TABLE>
<%String retu = balanceBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  if(!balanceBean.dsBalance.isOpen())
    balanceBean.dsBalance.open();

  EngineDataSet list = balanceBean.dsBalance;
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tableview1" width="600" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <COLGROUP width=130 valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP width=70 align="center">
    <tr class="tableTitle">
      <td>编码</td>
      <td>结算方式</td>
      <td>科目代码</td>
      <td><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="showInterFrame(<%=balanceBean.OPERATE_ADD%>,-1)" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='<%="showInterFrame("+ balanceBean.OPERATE_ADD +",-1)"%>'/>
      </td>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
    %>
    <tr onDblClick="showInterFrame(<%=balanceBean.OPERATE_EDIT%>,<%=i%>)">
      <td class="td"><%=list.format("dm")%></td>
      <td class="td"><%=list.format("jsfs")%></td>
      <td class="td"><%=list.getValue("kmdm")%></td>
      <td class="td"> <input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=balanceBean.OPERATE_EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=balanceBean.OPERATE_DEL%>,<%=i%>)" src="../images/del.gif" border="0">
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
    var url = "balancemodeedit.jsp?operate="+oper+"&rownum="+rownum;
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
      <TD><iframe id="interframe1" src="" width="340" height="180" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>

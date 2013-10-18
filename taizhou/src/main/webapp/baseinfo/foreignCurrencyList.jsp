<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<title>外币列表</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("foreignCurrencyList", request, response))
    return;
  engine.erp.baseinfo.B_ForeignCurrency foreignCurrencyBean = engine.erp.baseinfo.B_ForeignCurrency.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>

<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">外币列表</TD>
  </TR></TABLE>
<%String retu = foreignCurrencyBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;

  EngineDataSet list = foreignCurrencyBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap valign="middle" width=70>编码</td>
      <td nowrap>外币名称</td>
      <td nowrap>外币符号</td>
      <td nowrap>汇率</td>
      <td nowrap valign="middle" align="center">报价方式</td>
      <td nowrap width=100 align="center">固定汇率</td>
      <td nowrap width=70 align="center"><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/>
      </td>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=i%>)">
      <td nowrap class="td"><%=list.getValue("dm")%></td>
      <td nowrap class="td"><%=list.getValue("mc")%></td>
      <td nowrap class="td"><%=list.getValue("fh")%></td>
      <td nowrap class="td" align="right"><%=list.getValue("hl")%></td>
      <td nowrap class="td"><%=list.getValue("ff").equals("2") ? "间接汇率法" : "直接汇率法"%></td>
      <td nowrap class="td"><%=list.getValue("gd").equals("0") ? "否" : "是"%></td>
      <td nowrap class="td"> <input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i%>)" src="../images/del.gif" border="0">
      </td>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
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
    var url = "foreignCurrencyEdit.jsp?operate="+oper+"&rownum="+rownum;
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
      <TD><iframe id="interframe1" src="" width="328" height="265" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>

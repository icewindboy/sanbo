<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%if(!loginBean.hasLimits("fplblist", request, response))
    return;
  engine.erp.baseinfo.B_Fplb fplbBean = engine.erp.baseinfo.B_Fplb.getInstance(request);
  String retu = fplbBean.doService(request, response);
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
  function showInterFrame(oper, rownum)
  {
    var url = "fplbedit.jsp?operate="+oper+"&rownum="+rownum;
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
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">发票类别 </TD>
  </TR></TABLE>
<%EngineDataSet list = fplbBean.getOneTable();
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
        <%if(fplbBean.retuUrl!=null){%>
        <input name="button2222232" type="button" align="Right"
  class="button" onClick="location.href='<%=fplbBean.retuUrl%>'" value=" 返回 "border="0">
        <%}%>
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="600" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td width=45 nowrap><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/>
      </td>
      <td nowrap>发票名称</td>
      <td nowrap>税率%</td>
      <td nowrap>使用类型</td>
      <td nowrap>排序号</td>
    </tr>
    <%list.first();
      int i=0;
      for(int j=0; j<list.getRowCount(); j++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td" nowrap><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      </td>
      <td class="td" nowrap><%=list.getValue("mc")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("sl")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("sylx").equals("1") ? "采购" : "销售"%></td>
      <td class="td" align="right" nowrap><%=list.getValue("pxh")%></td>
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
      <td class="td"></td>
    </tr>
    <%}%>
  </table><script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
<%out.print(retu);%>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="330" height="210" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>

<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%String pageCode = "corplist";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.baseinfo.B_Corp corpBean = engine.erp.baseinfo.B_Corp.getInstance(request);
  String retu = corpBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
%><script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  var filename = form1.file_name.value.trim();
  if(filename == ''){
    alert('请输入文件名！');
    return;
  }
  var filepath = form1.file1.value.trim();
  if(filepath == ''){
    alert('请选择要上传的文件！');
    return;
  }
  lockScreenToWait("处理中, 请稍候！");
  //form1.operate.value = oper;
  form1.submit();
}
function hideInterFrame(){
  parent.hideInterFrame();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">相关附件</TD>
  </TR></TABLE>
<form name="form1" enctype="multipart/form-data" method="post" action="<%=curUrl%>?operate=<%=corpBean.FILE_UPLOAD%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <%--INPUT TYPE="HIDDEN" NAME="operate" VALUE=""--%>
  <TABLE BORDER="0" CELLPADDING="1" CELLSPACING="3">
    <TR>
      <td noWrap class="tableTitle">&nbsp;文件名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="file_name" SIZE="40" MAXLENGTH="32" CLASS="edbox" value='<%=corpBean.fileIsAdd ? "" : corpBean.dsCorpFile.getValue("file_name")%>'>
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">&nbsp;上传文件</td>
      <td noWrap class="td"><input type="file" name="file1" SIZE="29" CLASS="edbox"></td>
    </TR>
    <TR>
      <td colspan="2" noWrap class="tableTitle"><br> <input name="button" type="button" class="button" onClick="sumitForm(<%=corpBean.FILE_UPLOAD%>);" value="保存(S)" title="ALT+S">
        <pc:shortcut key="s" script='<%="sumitForm("+ corpBean.FILE_UPLOAD +")"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value="关闭(C)" title="ALT+C">
        <pc:shortcut key="c" script='<%="parent.hideFrameNoFresh()"%>'/></td>
    </TR>
  </TABLE>
</form><%out.print(retu);%>
</BODY>
</Html>
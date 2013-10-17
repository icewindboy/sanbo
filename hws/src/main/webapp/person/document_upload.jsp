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
<%String pageCode = "employee_info";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.person.B_Document_move documentBean = engine.erp.person.B_Document_move.getInstance(request);
  String retu = documentBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
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
function selectFile(fileObj){
  if(form1.file_name.value != "")
    return;
  var fileName = fileObj.value;
  var index;
  if((index = fileName.lastIndexOf("/")) > -1)
    fileName = fileName.substring(index+1);
  else if((index = fileName.lastIndexOf("\\")) > -1)
    fileName = fileName.substring(index+1);
  form1.file_name.value = fileName;
}
function hidewin(){

   opener.sumitForm(<%=documentBean.FILE_ADD%>,-1);
   //opener.location.reload();
  window.close();

}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">相关附件</TD>
  </TR></TABLE>
<form name="form1" enctype="multipart/form-data" method="post" action="<%=request.getRequestURI()%>?operate=<%=documentBean.FILE_UPLOAD%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <TABLE BORDER="0" CELLPADDING="1" CELLSPACING="3">
    <TR>
      <td noWrap class="tableTitle">&nbsp;文件名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="file_name" SIZE="40" MAXLENGTH="32" CLASS="edbox" value=''>
      </td>
    </TR>
    <TR>
      <td noWrap class="tableTitle">&nbsp;上传文件</td>
      <td noWrap class="td"><input type="file" name="file1" SIZE="29" CLASS="edbox" onchange="selectFile(this)"></td>
    </TR>
    <TR>
      <td colspan="2" noWrap class="tableTitle">
    <br><input name="button" type="button" class="button" onClick="sumitForm(<%=documentBean.FILE_UPLOAD%>);" value="上传(S)" accessKey="s" title="ALT+S">
        <input name="button2" type="button" class="button" onClick="window.close()" value="关闭(C)" accessKey="c"  title="ALT+C">
    </TR>
  </TABLE>
</form><%out.print(retu);%>
</BODY>
</Html>
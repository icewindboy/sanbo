<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%><%
  String pageCode = "ems_print";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  RowMap row = engine.erp.baseinfo.B_EMSPrint.getEnvelopFromInfo();
  String printlx = row.get("printlx");
  String printto = "../pub/pdfprint.jsp?operate="+Operate.PRINT_PRECISION+"&code=ems_print";
  if(printlx.equals("2"))
    printto = "../pub/pdfprint.jsp?operate="+Operate.PRINT_PRECISION+"&code=ems_shengtong_print";

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
<script language="javascript">
function submitForm(oper, row)
{
  form1.submit();
}
//客户编码
function customerCodeSelect(obj)
{
    CustomerCodeChange('2',document.all['prod'], obj.form.name,
                       'srcVar=dwmc&srcVar=dwdm&srcVar=lxr&srcVar=tel&srcVar=zp&srcVar=addr',
                       'fieldVar=dwmc&fieldVar=dwdm&fieldVar=lxr&fieldVar=tel&fieldVar=zp&fieldVar=addr',
                       obj.value,'');
}
function printo()
{
  if(form1.printlx.value=='2')
    form1.code.value='ems_shengtong_print';
  else
    form1.code.value='ems_print';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>

<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar"><tr>
    <td NOWRAP align="center"></td>
  </tr></table>
<form name="form1" action="../pub/pdfprint.jsp?operate=<%=Operate.PRINT_PRECISION%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
<INPUT TYPE="hidden" NAME="code" VALUE="ems_print">
<TABLE WIDTH="10%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD NOWRAP align="center">邮政特快专递</TD>
  </TR>
</table>
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
    </tr>
  </TABLE>
<table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
  <td>
 <table CELLSPACING="2" CELLPADDING="0" BORDER="0" width="100%" bgcolor="#f0f0f0">
      <tr>
      <TD class="tdTitle" nowrap>打印类型</TD>
      <td noWrap class="td">
    <pc:select name='printlx' value='<%=row.get("printlx")%>' onSelect="printo()" style='width:200'>
    <pc:option value="1">EMS</pc:option>
    <pc:option value="2">申通</pc:option>
    </pc:select>
      </td>
      <td noWrap class="tdTitle" >收件单位</td>
      <td class="td">
        <input type="text" class="edbox"  name="dwdm" style="width:100" onKeyDown="return getNextElement();" value='' onchange="customerCodeSelect(this)" >
        <input type="text" class="edbox"  name="dwmc"  onKeyDown="return getNextElement();" value='' style="width:250" onchange="customerNameSelect(this)"  >
        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('form1','srcVar=dwmc&srcVar=dwdm&srcVar=lxr&srcVar=tel&srcVar=zp&srcVar=addr','fieldVar=dwmc&fieldVar=dwdm&fieldVar=lxr&fieldVar=tel&fieldVar=zp&fieldVar=addr','','');">
      </td>
      </tr>
      <tr>
        <td noWrap class="tdTitle">寄件人</td>
        <td class="td"><input type="text" name="jjr" value='<%=loginBean.getUserName()%>' maxlength='100' style="width:200" class="edbox" onKeyDown="return getNextElement();"></td>
        <td noWrap class="tdTitle">收件人</td>
        <td class="td"><input type="text" name="lxr" value=''  style="width:300" class="edbox" onKeyDown="return getNextElement();"></td>
      </tr>
      <tr>
        <td noWrap class="tdTitle">寄件人电话</td>
        <td class="td"><input type="text" name="jjrtel" value='<%=row.get("dept_phone")%>' maxlength='32' style="width:200" class="edbox" onKeyDown="return getNextElement();"></td>
        <td noWrap class="tdTitle">收件人电话</td>
        <td noWrap class="td"><input type="text" name="tel" value=''  style="width:300" class="edbox" onKeyDown="return getNextElement();"></td>
      </tr>
      <tr>
        <td noWrap class="tdTitle">寄件单位</td>
        <td noWrap class="td"> <input type="text" name="mc" value='<%=row.get("mc")%>'  style="width:200" class="edbox" onKeyDown="return getNextElement();"></td>
        <td noWrap class="tdTitle"></td>
        <td noWrap class="td"></td>
     </tr>
     <tr>
        <td noWrap class="tdTitle">寄件人地址</td>
        <td noWrap class="td"><input type="text" name="jjaddr" value='<%=row.get("dept_addr")%>'  style="width:200" class="edbox" onKeyDown="return getNextElement();"></td>
        <td noWrap class="tdTitle">收件人地址</td>
        <td noWrap class="td"><input type="text" name="addr" value='' style="width:300" class="edbox" onKeyDown="return getNextElement();"></td>
     </tr>
     <tr>
        <td noWrap class="tdTitle">寄件人邮政编码</td>
        <td noWrap class="td"><input type="text" name="jjzip" value='' maxlength='80' style="width:80" class="edbox" onKeyDown="return getNextElement();"></td>
        <td noWrap class="tdTitle">收件人邮政编码</td>
        <td noWrap class="td"><input type="text" name="zp" value='' maxlength='80' style="width:80" class="edbox" onKeyDown="return getNextElement();"></td>
    </tr>
   </table>
   </td>
    </tr>
   </table>
  <table CELLSPACING=0 CELLPADDING=0 width="100%">
    <tr>
      <td noWrap class="tableTitle"><br>
        <input name="btnback" type="button" title = "打印" class="button" onClick="submitForm()" value=" 打印(P) "><pc:shortcut key="p" script='submitForm()'/>
        <input name="btnback" type="button" title = "返回" class="button" onClick="location.href='../pub/main.jsp'" value=" 返回(C) "><pc:shortcut key="c" script="location.href='../pub/main.jsp'"/>
      </td>
    </tr>
  </table>
</form>
</body>
</html>

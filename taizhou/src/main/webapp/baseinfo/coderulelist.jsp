<%--编码规则编辑页面--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%String pageCode = "coderule";
  if(!loginBean.hasLimits("coderule", request, response))
    return;
  engine.erp.system.CodeRule codeRuleBean = engine.erp.system.CodeRule.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='coderule.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
    <tr>
     <td align="center" NOWRAP>编码规则</td>
     </tr></table>
<%String retu = codeRuleBean.doService(request, response);
  if(retu.indexOf("backList();")>-1 )
    {
      out.print(retu);
      return;
  }
  String curUrl = request.getRequestURL().toString();
  String type = codeRuleBean.getCodeRuleCaption();
  EngineDataSet ds = codeRuleBean.getOneTable();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
<INPUT TYPE="HIDDEN" NAME="rulename" VALUE="<%=type%>">
  <table BORDER="0" CELLPADDING="0" CELLSPACING="0" align="center">
    <tr valign="top"><td>
	<table border="0" cellpadding="0" cellspacing="2">
      <tr valign="top">
       <td width=" 00">
      <table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
       <tr>
       <td class="activeVTab"><%=type%>
       </td>
       </tr>
       </table>
       <table class="limittreebox" CELLSPACING=0 CELLPADDING=0 width="100%">
        <tr>
        <td> <TABLE CELLSPACING="1" CELLPADDING="5" WIDTH="100%" BORDER="0" bgcolor="#f0f0f0">
        <TR>
        <TD  NOWRAP class="tdTitle">前缀</TD>
         <TD  NOWRAP CLASS=td><INPUT TYPE="text" CLASS="edbox" SIZE=16 MAXLENGTH=16 NAME="codeprefix" VALUE="<%=ds.getValue("codeprefix")%>" onchange='reshow();'></TD>
         <TD NOWRAP CLASS=td>（ 最大为 16）</TD>
         </TR>
         <TR>
         <TD NOWRAP CLASS=tdTitle>格式</TD>
         <TD NOWRAP CLASS=td>
         <INPUT TYPE=RADIO NAME="dateformat" VALUE="YYMMDD" <%=ds.getValue("dateformat").equals("YYMMDD") ? "checked" : ""%> onchange='reshow();'>YY-MM-DD-XXXXX</TD>
         <TD NOWRAP  class="td"><B>顺序号位数</B>
          <INPUT TYPE="TEXT" CLASS="edbox" SIZE=6 NAME="autolen" VALUE="<%=ds.getValue("autolen")%>" onchange='reshow();' ></TD>
         </TR>
          <TR>
          <TD CLASS=td>&nbsp;</TD>
          <TD NOWRAP CLASS=td><INPUT TYPE=RADIO NAME="dateformat" VALUE="YYMM" <%=ds.getValue("dateformat").equals("YYMM") ? "checked" : ""%> onchange='reshow();'>YY-MM-XXXXX</TD>
          <TD NOWRAP CLASS=td>&nbsp;</TD>
           </TR>
           <TR>
           <TD CLASS=td>&nbsp;</TD>
           <TD NOWRAP CLASS=td>
           <INPUT TYPE=RADIO NAME="dateformat" VALUE="YY" onchange='reshow();' <%=ds.getValue("dateformat").equals("YY") ? "checked" : ""%>>YY-XXXXX</TD>
            <TD NOWRAP CLASS=td>&nbsp;</TD>
            </TR>
            <TR>
            <TD CLASS=td>&nbsp;</TD>
            <TD NOWRAP CLASS=td>
            <INPUT TYPE=RADIO NAME="dateformat" VALUE="" onchange='reshow();' <%=ds.getValue("dateformat").equals("") ? "checked" : ""%>>XXXXX</TD>
             <TD NOWRAP CLASS=td>&nbsp;&nbsp;</TD>
            </TR>
            <TR>
            <TD NOWRAP CLASS=tdTitle>编码总长度</TD>
            <TD COLSPAN=2 NOWRAP CLASS=td><INPUT CLASS="edbox" SIZE=12 NAME="totallen" VALUE="" onchange='reshow();' READONLY>
            （最大为 32）
            </TD>
            </TR>
            <TR>
            <TD NOWRAP CLASS=tdTitle>编码示例</TD>
            <TD COLSPAN=2 NOWRAP CLASS=td><INPUT TYPE="TEXT" NAME="demoCode" VALUE="" SIZE="42" MAXLENGTH="32" ONFOCUS="blur();"  onchange='reshow();' CLASS="edline" READONLY></TD>
            </TR>
            <TR>
            <TD CLASS=td>&nbsp;</TD>
            <TD COLSPAN=2 NOWRAP CLASS=td>(“-”只为示例显示，不包含于编码中)</TD>
            </TR>
            </TABLE>
            </td>
            </tr>
            </table> </td>
           </tr>
        </table>
        <table CELLSPACING=0 CELLPADDING=0 width="100%">
        <tr>
          <td noWrap class="tableTitle"><br>
            <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
            <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
            <input name="button2" type="button" class="button" onClick="backList();" value="返回(C)">
            <pc:shortcut key="c" script='backList();'/>
          </td></tr>
      </table>
      </td></tr></table>
      </td>
    </tr>
  </table>
</form> <%out.print(retu);%>
<script language='javascript'>
  function reshow()
{
  var objCodeprefix = form1.codeprefix;
  var objDateformat = form1.dateformat;
  var objAutolen = form1.autolen;
  if(isNaN(objAutolen.value) || objAutolen.value=='')
  {
    alert('顺序号位数不能为空');
    return;
  }
  tmpDate = new Date();
  yea= ''+tmpDate.getYear();
  year = yea.substring(2,4);
  month= ''+(tmpDate.getMonth()+1);
  date = ''+tmpDate.getDate();
  month = month.length<2 ? '0'+month : month;
  date = date.length<2 ? '0'+date : date;
  //
  autolen = parseInt(objAutolen.value);
  if(autolen<1)
  {
    alert('顺序号位数不能小于1');
    return;
  }

  len = objCodeprefix.value.length + autolen;
  demo = objCodeprefix.value+'-';
  if(objDateformat[0].checked)
  {
    len += 6
    demo += year+'-'+month+'-'+date;
  }
  if(objDateformat[1].checked)
  {
    len += 4
    demo += year+'-'+month;
  }
  if(objDateformat[2].checked)
  {
    len += 2
    demo += year;
  }
  if(len > 32)
  {
    alert('大于编码');
    return;
  }
  demo += '-';
  for(i=0; i<autolen; i++)
    demo += i==autolen-1 ? '1' : '0';

  var objTotallen = form1.totallen;
  var objDemoCode = form1.demoCode;
  objTotallen.value = len;
  objDemoCode.value = demo;
}
reshow();
</script>
</BODY>
</Html>
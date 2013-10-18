<%@ page contentType="text/html; charset=UTF-8" %><%!
  String[] tableDefaultFields = new String[] {
    "", /*"jc_yjrq",*/ "", "",
    "", "", //"ROLEINFO",
    /*"SC_GRGZGS",*/ "",     "", //"SYSTEMPARAM",
    ""
  };
  String[] tableNames = new String[]{
    "JC_CODERULE", /*"jc_yjrq",*/ "NODEINFO", "NODEPRIVILIGE",
    "LIMITLIST", "NODEFIELD", //"ROLEINFO",
    /*"SC_GRGZGS",*/ "SP_XM",     "SP_XMTSMX", //"SYSTEMPARAM",
    "WHEREFIELD"
  };//,"XS_JJJSGS""RL_GZKXSZ"
  String[] tableCaptions = new String[]{
    "JC_CODERULE:编码规则", /*"jc_yjrq:月结日期",*/       "NODEINFO:节点信息",     "NODEPRIVILIGE:基本权限",
    "LIMITLIST:所有权限列表","NODEFIELD:节点字段定义", //"ROLEINFO:角色信息",
    /*"SC_GRGZGS:工人工资设置",*/ "SP_XM:审批项目",       "SP_XMTSMX:审批项目特殊明细", //"SYSTEMPARAM:系统参数",
    "WHEREFIELD:查询条件定义"
  };//","XS_JJJSGS:奖金计算公式"RL_GZKXSZ:工资款项设置"
%><%engine.erp.common.install.ExportTableData exportBean = new engine.erp.common.install.ExportTableData();
  String retu = exportBean.doService(request, response);
  String curUrl = request.getRequestURL().toString();
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../../scripts/validate.js"></script>
<script language="javascript" src="../../scripts/rowcontrol.js"></script>
<script language="javascript" src="../../scripts/tabcontrol.js"></script>
<script language="javascript">
function submitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <br>
  <table align="center" WIDTH="500" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"><b>数据导出</b></td>
  </tr></table><br>
  <table id="tableview1" border="0" cellpadding=1 cellspacing=1 class="table" align="center" width="600">
    <tr>
      <td class="td">导出路径:
        <input type="text" name="filename" value='<%=getServletContext().getRealPath("/WEB-INF/sql/erport"+exportBean.getCurrentDate()+".txt")%>' class="edbox" style="width:100%" onKeyDown="return getNextElement();">
      </td>
    </tr>
    <%for(int i=0; i<tableNames.length; i++){%>
    <tr>
      <td class="td"><input type="checkbox" name="tablename" value="<%=tableNames[i]%>" checked>&nbsp;<%=tableCaptions[i]%>
      <input type="hidden" name="default" value="<%=tableDefaultFields[i]%>">
      </td>
    </tr><%}%>
    <tr>
      <td align="center"> <input type="button" name="Submit" class="button" value=" 导出 " onClick="submitForm(<%=exportBean.POST%>)">
      </td>
    </tr>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',0);</SCRIPT>
</form>
<%=retu%>
</BODY>
</Html>
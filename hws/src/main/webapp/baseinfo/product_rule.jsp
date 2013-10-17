<%--产品编码规则页面--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%String pageCode = "coderule";
    if(!loginBean.hasLimits("coderule", request, response))
    return;
  engine.erp.system.CodeRule codeRuleBean = engine.erp.system.CodeRule.getInstance(request);
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
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function backList()
{
  location.href='../blank.htm';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><tr>
    <td NOWRAP align="center">编码规则</td>
  </tr></table>
<%String retu = codeRuleBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  String caption = codeRuleBean.getCodeRuleCaption();
  EngineDataSet ds = codeRuleBean.getOneTable();
  java.util.ArrayList numberList = codeRuleBean.numberList;
  java.util.ArrayList typeList = codeRuleBean.typeList;
  //String[] segments = codeRuleBean.getProductList();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
<INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
<INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
<%--INPUT TYPE="HIDDEN" NAME="rulename" VALUE="<%=type%>"--%>
<TABLE WIDTH="300" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
<tr valign="top">
      <td> <table border="0" cellpadding="0" cellspacing="2">
          <tr valign="top">
            <td width="100"> <table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
                <tr>
                  <td class="activeVTab"><%=caption%></td>
                </tr>
              </table>
              <table class="limittreebox" CELLSPACING=0 CELLPADDING=1 width="100%">
                <TR>
                  <TD NOWRAP CLASS=tdTitle>总级数:</TD>
                  <TD NOWRAP CLASS=td><INPUT CLASS="edbox" SIZE=12 NAME="segmentnum" VALUE="<%=numberList.size()%>" READONLY>
                    （ 最大为 10 ）</TD>
                </TR>
                <TR>
                  <TD NOWRAP CLASS=tdTitle>编码总长度:</TD>
                  <TD NOWRAP CLASS=td><INPUT CLASS="edbox" SIZE=12 NAME="totallen" VALUE="<%=codeRuleBean.totallen%>" READONLY></TD>
                </TR>
                <TR>
                  <TD COLSPAN=2 CLASS=td><table id="tableview1" width="300" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <TD width=30 nowrap>序号</TD>
                        <TD nowrap>类型</TD>
                        <TD nowrap>位数</TD>
                        <TD width=30 nowrap>操作</TD>
                      </TR>
                      <%int i=0;
                      for(; i<numberList.size(); i++){
                        String type = (String)typeList.get(i);
                     %>
                      <TR>
                        <TD CLASS=td><%=i+1%></TD>
                        <TD CLASS=td><%=type.equals("uc") ? "字符型(大写)" :
                                        type.equals("lc") ? "字符型(小写)" :
                                        type.equals("c") ? "字符型(混合)" : "数字型"%></TD>
                        <TD CLASS=td><%=numberList.get(i)%></TD>
                        <TD CLASS=td><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) submitForm(<%=codeRuleBean.PRODUCT_CODE_DEL%>, <%=i%>)" src="../images/del.gif" border="0" ></TD>
                      </TR>
                      <%} if(i < 10){%>
                      <TR>
                        <TD CLASS=td><%=++i%></TD>
                        <TD CLASS=td><pc:select name="type" style="width:100" value='n'>
                          <pc:option value='n'>数字型</pc:option>
                          <pc:option value='uc'>字符型(大写)</pc:option>
                          <pc:option value='lc'>字符型(小写)</pc:option>
                          <pc:option value='c'>字符型(混合)</pc:option>
                        </pc:select></TD>
                        <TD CLASS=td><INPUT TYPE=TEXT CLASS=edbox style="width:80" maxlength="3" NAME="newsegment" ></TD>
                        <TD CLASS=td></TD>
                      </TR>
                      <%} for(; i < 10; i++){%>
                      <TR>
                        <TD CLASS=td>&nbsp;</TD>
                        <TD CLASS=td></TD>
                        <TD CLASS=td></TD>
                        <TD CLASS=td></TD>
                      </TR>
                      <%}%>
                    </TABLE></TD>
                </TR>
                <TR>
                  <TD NOWRAP CLASS=tdTitle>顺序号位数:</TD>
                  <TD NOWRAP CLASS=td><INPUT TYPE="test" CLASS="edbox" SIZE=8 NAME="autolen" VALUE="<%=ds.getValue("autolen")%>" maxlength=8>
                    （ 最大为 8 ）&nbsp;</TD>
                </TR>
                <TR>
                  <TD NOWRAP CLASS=tdTitle>编码示例:</TD>
                     <%String democode = "";
                    for(int k=0; k<numberList.size(); k++)
                    {
                      for(int j=0; j<Integer.parseInt((String)numberList.get(k)); j++)
                        democode += "0";
                      democode += "-";
                    }
                    for(int g=0; g<Integer.parseInt(ds.getValue("autolen")); g++)
                      democode += "0";
                    %>
                  <TD NOWRAP CLASS=td><INPUT CLASS="edline" SIZE=42 NAME="demoCode" VALUE="<%=democode%>"  READONLY></TD>
                </TR>
                <TR>
                  <TD CLASS=td>&nbsp;</TD>
                  <TD COLSPAN=2 NOWRAP CLASS=td>(“-”只为示例显示，不包含于编码中)</TD>
                </TR>
              </TABLE></td>
          </tr>
        </table></td>
 </tr>
 </table>
 <table CELLSPACING=0 CELLPADDING=0 width="100%">
 <tr>
 <td noWrap class="tableTitle">
 <input name="button" type="button" class="button" onClick="submitForm(<%=codeRuleBean.PRODUCT_CODE_POST%>);" value="保存(S)">
                    <pc:shortcut key="s" script='<%="sumitForm("+ codeRuleBean.PRODUCT_CODE_POST +",-1)"%>'/>
 <input name="button2" type="button" class="button" onClick="backList();" value="返回(C)">
                    	  <pc:shortcut key="c" script='backList();'/>
  </td>
   </tr>
   </table>
    </td></tr></table>
    </td>
   </tr>
  </table>
  <script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
<%out.print(retu);%>
</BODY>
</Html>

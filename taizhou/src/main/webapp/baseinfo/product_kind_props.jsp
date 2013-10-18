<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*"%><%
  if(!loginBean.hasLimits("product_kind", request, response))
    return;
  engine.erp.baseinfo.B_ProductKindProps kindPropsBean = engine.erp.baseinfo.B_ProductKindProps.getInstance(request);
  String retu = kindPropsBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = kindPropsBean.getOneTable();
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
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=<%=true%>">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD NOWRAP align="left"><%=kindPropsBean.getFirstKindName(request)%>类别属性列表</TD>
  </TR>
</TABLE>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table border="0" cellspacing="1" cellpadding="1" align="left">
    <tr>
      <td> <table id="tableview1" border="0" width="300" cellspacing="1" cellpadding="1" class="table" align="left">
          <tr class="tableTitle">
            <td nowrap width=10></td>
            <td width=25 height='20' align="center" nowrap><input name="image" class="img" type="image" title="新增" onClick="sumitForm('<%=kindPropsBean.ADD%>')" src="../images/add_big.gif" border="0"></td>
            <td nowrap>属性名称</td>
            <td nowrap>属性类型</td>
          </tr>
          <%RowMap[] detailRows = kindPropsBean.getRowinfo();
      RowMap detail = null;
      int sxmcMax = ds.getColumn("sxmc").getPrecision();
      int i=0;
      for(; i<detailRows.length; i++){
        detail = detailRows[i];
    %>
          <tr id="rowinfo_<%=i%>">
            <td class="td" nowrap><%=i+1%></td>
            <td class="td" nowrap align="center"><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=kindPropsBean.DEL%>,<%=i%>)" src="../images/delete.gif" border="0"></td>
            <td class="td" nowrap><input type="text" class="edbox" style="width:100%" onKeyDown="return getNextElement();" name="sxmc_<%=i%>" value='<%=detail.get("sxmc")%>' maxlength='<%=sxmcMax%>'></td>
            <td class="td" nowrap>
              <pc:select name='<%="sxlx_"+i%>' style="width:100%" value='<%=detail.get("sxlx")%>'>
                <pc:option value='number'>数字型</pc:option>
                <pc:option value='varchar'>字符型(混合)</pc:option>
                <pc:option value='upperchar'>字符型(大写)</pc:option>
                <pc:option value='lowerchar'>字符型(小写)</pc:option>
              </pc:select>
            </td>
          </tr>
          <%}
          for(; i < 5; i++){%>
          <tr id="rowinfo_<%=i%>">
            <td class="td">&nbsp;</td>
            <td class="td">&nbsp;</td>
            <td class="td">&nbsp;</td>
            <td class="td">&nbsp;</td>
          </tr>
          <%}%>
        </table></td>
    </tr>
    <tr>
      <td> <table border="0" cellspacing="1" cellpadding="1" width="100%">
          <tr>
            <td align="center">
              <%if(!kindPropsBean.isFirstKind()){%><input name="button" type="button" class="button" onClick="sumitForm(<%=kindPropsBean.COPY_FIRST_CODE%>);" value="复制大类属性(K)">
              <pc:shortcut key="k" script='<%="sumitForm("+kindPropsBean.COPY_FIRST_CODE+");"%>'/><%}%>
              <input name="button" type="button" class="button" onClick="sumitForm(<%=kindPropsBean.POST%>);" value="保存(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+kindPropsBean.POST+");"%>'/>
              <input name="button2" type="button" class="button" onClick="location.href='<%=kindPropsBean.retuUrl%>'" value="返回(C)"></td>
              <pc:shortcut key="c" script='<%="location.href="+kindPropsBean.retuUrl%>'/>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</script>
<%out.print(retu);%>
</BODY>
</Html>

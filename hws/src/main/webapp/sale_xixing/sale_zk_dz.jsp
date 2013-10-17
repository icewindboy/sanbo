<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.*,java.math.BigDecimal,java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "product_price";
%>
<%
if(!loginBean.hasLimits("product_price", request, response))
    return;
  engine.erp.sale.xixing.B_Sale_CheckAccount b_Sale_CheckAccountBean = engine.erp.sale.xixing.B_Sale_CheckAccount.getInstance(request);//得到实例(初始化实例变量)
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);//engine.project.SysConstant.BEAN_CORP 外来单位JAVABEAN
  String retu = b_Sale_CheckAccountBean.doService(request, response);//src=../pub/main.jsp(初始化函数取值)
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  RowMap[] xsdsrows = b_Sale_CheckAccountBean.getXsRowinfos();
  RowMap[] jsdsrows = b_Sale_CheckAccountBean.getJsRowinfos();
  RowMap[] thdzrows = b_Sale_CheckAccountBean.getThRowinfos();
  RowMap[] qtdzrows = b_Sale_CheckAccountBean.getQtRowinfos();
  int rownum = xsdsrows.length<jsdsrows.length?jsdsrows.length:xsdsrows.length;
  rownum = rownum<thdzrows.length?thdzrows.length:rownum;
  rownum = rownum<qtdzrows.length?qtdzrows.length:rownum;
  RowMap masterRow = b_Sale_CheckAccountBean.masterRow;

%>
<html>
<head>
<title>英捷ERP系统</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value  = row;
    form1.submit();
  }
  function sumitFixedQuery(oper)
 {
   lockScreenToWait("处理中, 请稍候！");
   fixedQueryform.operate.value = oper;
   fixedQueryform.submit();
  }
  function showFixedQuery()
  {
    showFrame('fixedQuery',true,"",true);
  }
//客户编码
function customerCodeSelect(obj)
{
    CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户名称
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
</script>
<body oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <br>
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" align="right" nowrap>
      <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
      <input name="button2" type="button" class="button" onClick="location.href='../pub/main.jsp'" value="返回(C)" onKeyDown="return getNextElement();">
      <script language='javascript'>GetShortcutControl(67,"location.href='../pub/main.jsp';");</script>
      </TD>
    </tr>
  </table>
<table border="0" cellspacing="1" cellpadding="1" width="95%" align="center">
<tr class="tableTitle">
    <td align="center" valign="middle" class="td" class="tdTitle" style="font-size:20">销售对帐单</td>
</tr>
<tr>
    <td align="center" valign="middle" class="td">
<table border="0" cellspacing="1" cellpadding="1" width="95%" align="center">
<tr class="tableTitle">
  <td nowrap align="left" class="td" colspan="2"><%=masterRow.get("nf")%>年<%=masterRow.get("yf")%>月<%=masterRow.get("startday")%>日至<%=masterRow.get("yf")%>月<%=masterRow.get("endday")%>日</td>
  <td nowrap class="td" >月份<%=masterRow.get("yf")%></td>
  <td>&nbsp;</td>
</tr>
<tr class="tableTitle">
   <td align="left" valign="left" nowrap class="td">客户：<%=masterRow.get("dwmc")%></td>
   <td nowrap class="td" >地址：<%=masterRow.get("addr")%></td>
   <td nowrap class="td" >电话：<%=masterRow.get("tel")%></td>
   <td nowrap class="td" >传真：<%=masterRow.get("cz")%></td>
</tr>
</table>

</td>
</tr>
</table>
<table id="tableview1" border="0" cellspacing="1" cellpadding="1" width="95%" align="center" class="table">
  <tr class="tableTitle">
    <td colspan="6" align="center" valign="middle" nowrap class="td">本月销售</td>
    <td colspan="4" align="center" valign="middle" nowrap class="td">本月收款</td>
    <td colspan="4" align="center" valign="middle" nowrap class="td">本月退货</td>
    <td colspan="4" align="center" valign="middle" nowrap class="td">其他应收款</td>
  </tr>
  <tr class="tableTitle">
    <td width="2%"  align="center" valign="middle" nowrap class="td">月</td>
    <td width="2%"  align="center" valign="middle" nowrap class="td">日</td>
    <td width="12%"  align="center" valign="middle" nowrap class="td">摘要</td>
    <td width="9%"  align="center" valign="middle" nowrap class="td">销售金额</td>
    <td width="4%"  align="center" valign="middle" nowrap class="td">折率</td>
    <td width="12%"  align="center" valign="middle" nowrap class="td">折后实际金额</td>
    <td width="2%"  align="center" valign="middle" nowrap class="td">月</td>
    <td width="2%"  align="center" valign="middle" nowrap class="td">日</td>
    <td width="8%"  align="center" valign="middle" nowrap class="td">收款方式</td>
    <td width="5%"  align="center" valign="middle" nowrap class="td">金额</td>
    <td width="3%"  align="center" valign="middle" nowrap class="td">月</td>
    <td width="3%"  align="center" valign="middle" nowrap class="td">日</td>
    <td width="9%"  align="center" valign="middle" nowrap class="td">折率摘要</td>
    <td width="8%"  align="center" valign="middle" nowrap class="td">折后金额</td>
    <td width="2%"  align="center" valign="middle" nowrap class="td">月</td>
    <td width="2%"  align="center" valign="middle" nowrap class="td">日</td>
    <td width="4%"  align="center" valign="middle" nowrap class="td">摘要</td>
    <td width="9%"  align="center" valign="middle" nowrap class="td">金额</td>
  </tr>
  <%
    int i = 0;
  for(;i<rownum;i++){
  %>
  <tr>
    <%if(i<xsdsrows.length){
     RowMap xsrow = xsdsrows[i];
    %>
    <td class="td" nowrap><%=xsrow.get("yf")%></td>
    <td class="td" nowrap><%=xsrow.get("dei")%></td>
    <td class="td" nowrap><%=xsrow.get("zy")%></td>
    <td align="right" class="td" nowrap><%=xsrow.get("xsje")%></td>
    <td align="right" class="td" nowrap><%=xsrow.get("zk")%></td>
    <td align="right" class="td" nowrap><%=xsrow.get("jje")%></td>
    <%}else{%>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td align="right" class="td" nowrap>&nbsp;</td>
    <td align="right" class="td" nowrap>&nbsp;</td>
    <td align="right" class="td" nowrap>&nbsp;</td>
    <%}%>
    <%if(i<jsdsrows.length){
     RowMap jsrow = jsdsrows[i];
    %>
    <td class="td" nowrap><%=jsrow.get("yf")%></td>
    <td class="td" nowrap><%=jsrow.get("dei")%></td>
    <td align="right" nowrap class="td"><%=jsrow.get("jsfs")%></td>
    <td align="right" nowrap class="td"><%=jsrow.get("je")%></td>
    <%}else{%>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <%}%>
    <%if(i<thdzrows.length){
     RowMap thdsrow = thdzrows[i];
    %>
    <td class="td" nowrap><%=thdsrow.get("yf")%></td>
    <td class="td" nowrap><%=thdsrow.get("dei")%></td>
    <td align="right" nowrap class="td"><%=thdsrow.get("zy")%></td>
    <td align="right" nowrap class="td"><%=thdsrow.get("tjje")%></td>
    <%}else{%>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <%}%>
    <%if(i<qtdzrows.length){
     RowMap qtdsrow = qtdzrows[i];
    %>
    <td class="td" nowrap><%=qtdsrow.get("yf")%></td>
    <td class="td" nowrap><%=qtdsrow.get("dei")%></td>
    <td align="right" nowrap class="td"><%=qtdsrow.get("zy")%></td>
    <td align="right" nowrap class="td"><%=qtdsrow.get("fy")%></td>
    <%}else{%>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <%}%>
  </tr>
  <%}%>
  <%for(;i<17;i++){%>
  <tr>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td align="right" class="td" nowrap>&nbsp;</td>
    <td align="right" class="td" nowrap>&nbsp;</td>
    <td align="right" class="td" nowrap>&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
  </tr>
  <%}%>
  <tr class="tableTitle">
    <td colspan="3" nowrap class="td">合计</td>
    <td align="right" class="td" nowrap><%=masterRow.get("zxsje")%></td>
    <td align="right" class="td" nowrap>&nbsp;</td>
    <td align="right" class="td" nowrap><%=masterRow.get("zjje")%></td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td"><%=masterRow.get("zjsje")%></td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td align="right" nowrap class="td"><%=masterRow.get("zthje")%></td>
    <td colspan="4" align="right" nowrap class="td"><%=masterRow.get("zfy")%></td>
  </tr>
  <tr class="tableTitle">
    <td colspan="3" nowrap class="td">前月结欠收款 </td>
    <td colspan="3" nowrap class="td"><input class='ednone' name='Input22' style='width:130' value="" maxlength='10' readonly></td>
    <td colspan="3" align="right" nowrap class="td">本月共收款</td>
    <td colspan="4" align="right" nowrap class="td" >
        <input class='ednone' name='Input' style='width:130' value="<%=masterRow.get("zjsje")%>" maxlength='10' readonly>
      </td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td colspan="4" align="right" nowrap class="td">&nbsp;</td>
  </tr>
  <tr class="tableTitle">
    <td colspan="3" nowrap class="td">本月总销售</td>
    <td colspan="3" align="right" nowrap class="td">
        <input class='ednone' class="td" name='Input2' style='width:130' value="<%=masterRow.get("zxsje")%>" maxlength='10' readonly>
      </td>
    <td colspan="3" align="right" nowrap class="td">本月总退货</td>
    <td colspan="4" align="right" class="td" nowrap class="td">
        <input class='ednone' name='Input3' style='width:130' value="<%=masterRow.get("zthje")%>" maxlength='10' readonly>
      </td>
    <td align="right" nowrap class="td">&nbsp;</td>
    <td colspan="4" align="right" nowrap class="td">&nbsp;</td>
  </tr>
</table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
         <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD align="center" nowrap class="td">往来单位</TD>
              <td nowrap class="td" colspan=3>
              <input type="text" class="edbox" style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=b_Sale_CheckAccountBean.getFixedQueryValue("dwdm")%>' onchange="customerCodeSelect(this)" >
              <input type="text" name="dwmc"  style="width:260" value='<%=b_Sale_CheckAccountBean.getFixedQueryValue("dwmc")%>' class="edbox"  onchange="customerNameSelect(this)" >
              <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=b_Sale_CheckAccountBean.getFixedQueryValue("dwtxid")%>'>
              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)">
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
              </td>
           </tr>
           <TR>
              <td noWrap class="td" align="center">年份</td>
              <td noWrap class="td">
              <input type="text" name="nf" value='<%=b_Sale_CheckAccountBean.getFixedQueryValue("nf")%>' maxlength='6' style="width:80" class="edbox">
              </td>
            <td noWrap class="td" align="center">月份</td>
           <td class="td" ><%String yf =b_Sale_CheckAccountBean.getFixedQueryValue("yf"); %>
            <pc:select name="yf" style="width:80" value="<%=yf%>" >
              <pc:option value="01">01</pc:option>
              <pc:option value="02">02</pc:option>
              <pc:option value="03">03</pc:option>
              <pc:option value="04">04</pc:option>
              <pc:option value="05">05</pc:option>
              <pc:option value="06">06</pc:option>
              <pc:option value="07">07</pc:option>
              <pc:option value="08">08</pc:option>
              <pc:option value="09">09</pc:option>
              <pc:option value="10">10</pc:option>
              <pc:option value="11">11</pc:option>
              <pc:option value="12">12</pc:option>
            </pc:select>
            </td>
             </TR>
            <TR>
              <TD colspan="4" nowrap class="td" align="center">
                <% String qu = "sumitFixedQuery("+b_Sale_CheckAccountBean.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=b_Sale_CheckAccountBean.FIXED_SEARCH%>)" type="button" value="查询(K)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="k" script='<%=qu%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(x)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script="hideFrame('fixedQuery')"/>
              </TD>
            </TR>
          </TABLE>
        </TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="320" height="325" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>
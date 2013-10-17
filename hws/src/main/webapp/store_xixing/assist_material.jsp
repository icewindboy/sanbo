<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.*,java.math.BigDecimal,java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "sale_zk_dz";
%>
<%
if(!loginBean.hasLimits("assist_material", request, response))
    return;
  engine.erp.store.xixing.B_AssistantMaterial B_AssistantMaterialBean = engine.erp.store.xixing.B_AssistantMaterial.getInstance(request);//得到实例(初始化实例变量)
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);//engine.project.SysConstant.BEAN_CORP 外来单位JAVABEAN
  String retu = B_AssistantMaterialBean.doService(request, response);//src=../pub/main.jsp(初始化函数取值)
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  RowMap[] xsdsrows = B_AssistantMaterialBean.getXsRowinfos();
  RowMap masterRow =B_AssistantMaterialBean.masterRow;

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
//打印
function ladingprint(d)
{
  location.href='assist_material_print.jsp';
}
//客户名称
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
function productCodeSelect(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product','fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function productNameSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product','fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function showExcel()
{
    window.open("../pub/excel.jsp", "", "location=no,menubar=no,toolbar=no,status=no,scrollbars=no");
}
</script>
<body oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <br>
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" align="right" nowrap>
       <%if(!masterRow.get("product").equals("")){%>
       <input name="btnback"  style="width:80"  type="button" class="button" onClick="ladingprint()" value="打印(P)">
       <pc:shortcut key="p" script='ladingprint()' />
       <%}%>
      <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
      <input name="button2" type="button" class="button" onClick="location.href='../pub/main.jsp'" value="返回(C)" onKeyDown="return getNextElement();">
      <script language='javascript'>GetShortcutControl(67,"location.href='../pub/main.jsp';");</script>
      </TD>
    </tr>
  </table>
<table border="0" cellspacing="1" cellpadding="1" width="95%" align="center">
<tr class="tableTitle">
    <td align="center" valign="middle" class="td" class="tdTitle" style="font-size:20">&nbsp;</td>
</tr>
<tr class="tableTitle">
    <td align="center" valign="middle" class="td" class="tdTitle" style="font-size:18">辅助材料明细帐</td>
</tr>
<tr>
    <td align="center" valign="middle" class="td">
<table border="0" cellspacing="1" cellpadding="1" width="95%" align="center">
<tr class="tableTitle">
  <td nowrap align="left" class="td" >物资</td>
  <td nowrap align="left"  class="td" >
  <INPUT class="edline" style="WIDTH:70" id="cpbm" name="cpbm" value='<%=masterRow.get("cpbm")%>' onKeyDown="return getNextElement();" >
  <INPUT class="edline" style="WIDTH:130" id="product" name="product" value='<%=masterRow.get("product")%>' onKeyDown="return getNextElement();" >
 </td>
  <td nowrap align="left" class="td" >日期</td>
  <td nowrap align="left"  class="td" >
  <INPUT class="edline" style="WIDTH: 130px" name="ksrq" value='<%=masterRow.get("ksrq")%>' maxlength='10'  onKeyDown="return getNextElement();">
  --<INPUT class="edline" style="WIDTH: 130px" name="jsrq" value='<%=masterRow.get("jsrq")%>' maxlength='10'  onKeyDown="return getNextElement();">
 </td>
  <td nowrap align="left" class="td" >计量单位:</td>
  <td nowrap align="left"  class="td" >
  <INPUT class="edline" style="WIDTH: 130px" name="jldw" value='<%=masterRow.get("jldw")%>' maxlength='10'  onKeyDown="return getNextElement();">
 </td>
</tr>
</table>

</td>
</tr>
</table>
<table id="tableview1" border="0" cellspacing="1" cellpadding="1" width="95%" align="center" class="table">
  <tr class="tableTitle">
    <td width="2%" rowspan="2" align="center" valign="middle" nowrap class="td">日期</td>
    <td width="8%" rowspan="2" align="center" valign="middle" nowrap class="td">单据号</td>

    <td colspan="2" align="center" valign="middle" nowrap class="td">期初数</td>
    <td colspan="2" align="center" valign="middle" nowrap class="td">本期入库</td>
    <td colspan="2" align="center" valign="middle" nowrap class="td">本期出库</td>
    <td colspan="2" align="center" valign="middle" nowrap class="td">本期结存</td>
  </tr>
  <tr class="tableTitle">
    <td width="8%"  align="center" valign="middle" nowrap class="td">数量</td>
    <td width="8%"  align="center" valign="middle" nowrap class="td">金额</td>
    <td width="8%"  align="center" valign="middle" nowrap class="td">数量</td>
    <td width="8%"  align="center" valign="middle" nowrap class="td">金额</td>
    <td width="8%"  align="center" valign="middle" nowrap class="td">数量</td>
    <td width="8%"  align="center" valign="middle" nowrap class="td">金额</td>
    <td width="8%"  align="center" valign="middle" nowrap class="td">数量</td>
    <td width="8%"  align="center" valign="middle" nowrap class="td">金额</td>
  </tr>
  <%
  int rownum = xsdsrows.length;
  double bqsl =0;
  double bqje=0;
  int i = 0;
  for(;i<rownum;i++){
    RowMap xsrow = xsdsrows[i];
  %>
  <tr>
    <td class="td" nowrap><%=xsrow.get("sfrq")%></td>
    <td class="td" nowrap><%=xsrow.get("sfdjdh")%></td>
    <td align="left" class="td" nowrap><%=xsrow.get("cqsl")%></td>
    <td align="left" nowrap class="td"><%=xsrow.get("qcje")%></td>
    <td align="left" nowrap class="td"><%=xsrow.get("srsl")%></td>
    <td align="left" class="td" nowrap><%=xsrow.get("srje")%></td>
    <td align="left" nowrap class="td"><%=xsrow.get("fcsl")%></td>
    <td align="left" nowrap class="td"><%=xsrow.get("fcje")%></td>
    <td align="left" class="td" nowrap><%=xsrow.get("bqsl")%></td>
    <td align="left" nowrap class="td"><%=xsrow.get("bqje")%></td>
  </tr>
  <%}%>
  <%for(;i<22;i++){%>
  <tr>
    <td class="td" nowrap>&nbsp;</td>
    <td align="left" nowrap class="td">&nbsp;</td>
    <td align="left" nowrap class="td">&nbsp;</td>
    <td align="left" nowrap class="td">&nbsp;</td>
    <td align="left" nowrap class="td">&nbsp;</td>
    <td align="left" nowrap class="td">&nbsp;</td>
    <td align="left" nowrap class="td">&nbsp;</td>
    <td align="left" nowrap class="td">&nbsp;</td>
    <td align="left" nowrap class="td">&nbsp;</td>
    <td align="left" nowrap class="td">&nbsp;</td>
  </tr>
  <%}%>
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
              <TD class="td" nowrap>产品</TD>
              <TD class="td" nowrap colspan="3">
              <INPUT class="edbox" style="WIDTH:70" id="cpbm" name="cpbm" value='<%=B_AssistantMaterialBean.getFixedQueryValue("cpbm")%>' onchange="productCodeSelect(this)" onKeyDown="return getNextElement();" >
              <INPUT class="edbox" style="WIDTH:130" id="product" name="product" value='<%=B_AssistantMaterialBean.getFixedQueryValue("product")%>' onchange="productNameSelect(this)" onKeyDown="return getNextElement();" >
              <INPUT TYPE="HIDDEN" NAME="cpid" value="<%=B_AssistantMaterialBean.getFixedQueryValue("cpid")%>">
              <INPUT TYPE="HIDDEN" NAME="jldw" value="<%=B_AssistantMaterialBean.getFixedQueryValue("jldw")%>">
               <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw')">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cpbm.value='';product.value='';jldw.value='';">
              </TD>
            </TR>
           <TR>
              <TD nowrap class="td">收发日期</TD>
              <TD class="td" nowrap>
               <INPUT class="edbox" style="WIDTH: 130px" name="ksrq" value='<%=B_AssistantMaterialBean.getFixedQueryValue("ksrq")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(ksrq);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="sfrq$b" style="WIDTH: 130px" name="jsrq" value='<%=B_AssistantMaterialBean.getFixedQueryValue("jsrq")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jsrq);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD colspan="4" nowrap class="td" align="center">
                <% String qu = "sumitFixedQuery("+B_AssistantMaterialBean.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=B_AssistantMaterialBean.FIXED_SEARCH%>)" type="button" value="查询(K)" name="button" onKeyDown="return getNextElement();">
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

<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,java.text.*,java.util.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.sale.xixing.B_CustomerUnitiveDiscount b_CustomerUnitiveDiscountBean = engine.erp.sale.xixing.B_CustomerUnitiveDiscount.getInstance(request);
  String pageCode = "custmer_unitive_discount";
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
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='custmer_unitive_discount.jsp';
}
</script>
<%
  String retu = b_CustomerUnitiveDiscountBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = b_CustomerUnitiveDiscountBean.getOneTable();
  RowMap masterRow = b_CustomerUnitiveDiscountBean.getRowinfo();


  ArrayList CPopkey = new ArrayList(); CPopkey.add("A");CPopkey.add("B"); CPopkey.add("C");
  ArrayList CPopval = new ArrayList(); CPopval.add("A");CPopval.add("B"); CPopval.add("C");
  ArrayList[] CPlists  = new ArrayList[]{CPopkey, CPopval};

  ArrayList opkey = new ArrayList(); opkey.add("lsj");opkey.add("ccj"); opkey.add("msj");  opkey.add("qtjg1"); opkey.add("qtjg2");opkey.add("qtjg3");
  ArrayList opval = new ArrayList(); opval.add("零售价");opval.add("出厂价"); opval.add("门市价");  opval.add("其他价格1"); opval.add("其他价格2");opval.add("其他价格3");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};

%>
<BODY oncontextmenu="window.event.returnValue=true" >
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();"  >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr><td align="center" height="5"></td></tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="500">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">新产品发货登记表</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
              <tr>
                 <td noWrap class="tdTitle">信誉等级</td>
                 <td noWrap class="td">
                 <input type="text" class="edbox" name="xydj" onKeyDown="return getNextElement();" value='<%=masterRow.get("xydj")%>' style="width:130"   >
                 </td>
              </tr>
              <tr>
                  <td noWrap class="tdTitle">产品类别</td>
                  <td noWrap class="td">
                   <%String cplx=masterRow.get("cplx");%>
                  <pc:select name='cplx' addNull="1"   combox="1"    style="width:130" value="<%=cplx%>" >
                 <%=b_CustomerUnitiveDiscountBean.listToOption(CPlists, CPopkey.indexOf(masterRow.get("cplx")))%>
                 </pc:select>
                  </td>
              </tr>
              <tr>
                  <td noWrap class="tdTitle">定价类型</td><%--购货单位--%>
                  <td  noWrap class="td">
                    <pc:select name='djlx' addNull="1"    style="width:130"  >
                   <%=b_CustomerUnitiveDiscountBean.listToOption(lists, opkey.indexOf(masterRow.get("djlx")))%>
                 </pc:select>
                  </td>
             </tr>
              <tr>
                 <td noWrap class="tdTitle">折扣(%)</td>
                 <td noWrap class="td">
                 <input type="text" class="edbox" name="zk" onKeyDown="return getNextElement();" value='<%=masterRow.get("zk")%>' style="width:130"   >
                 </td>
              </tr>
        </table>
        </td>
      </tr>
    </table>
  </td>
</tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td colspan="3" noWrap class="tableTitle">
               <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 ">
              <input name="btnback" type="button"  style="width:50" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script='<%="backList();"%>'/>
            </td>
          </tr>
        </table>
       </td>
    </tr>
  </table>
</form>
</form>
</BODY>
</Html>
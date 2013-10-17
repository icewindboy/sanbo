<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,java.text.*,java.util.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.sale.xixing.B_ProductPromotion b_ProductPromotionBean = engine.erp.sale.xixing.B_ProductPromotion.getInstance(request);
  String pageCode = "produce_promotion";
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
  location.href='produce_promotion.jsp';
}
</script>
<%
  String retu = b_ProductPromotionBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//引用产品
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//引用往来单位
  engine.project.LookUp proPriceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PROD_PRICE);//

  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = b_ProductPromotionBean.getOneTable();
  proPriceBean.regData(list,"cpid");
  RowMap masterRow = b_ProductPromotionBean.getRowinfo();
  String zt=masterRow.get("zt");
  boolean isEnd = zt.equals("1")||zt.equals("9")||zt.equals("8");
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=edline" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String title = zt.equals("0") ? ("未审批") : (zt.equals("9") ? "审批中" :(zt.equals("4")?"已作废":(zt.equals("8")?"完成":"已审")) );
  String cpid = masterRow.get("cpid");
  RowMap productRow = prodBean.getLookupRow(cpid);
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
  RowMap prodPriceRow = proPriceBean.getLookupRow(cpid);
  String djlx = masterRow.get("djlx");
  String dj = prodPriceRow.get(djlx);


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
            <td class="activeVTab">促销产品管理</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
              <tr>
                  <td noWrap class="tdTitle">客户名称</td><%--购货单位--%>
                  <td colspan="3" noWrap class="td">
                    <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text"  <%=edClass%> <%=readonly%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="customerCodeSelect(this)" >
                    <input type="text" <%=edClass%> <%=readonly%> name="dwmc" onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:200"   onchange="customerNameSelect(this)"  >
                    <%if(!isEnd){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="selectCustomer()"><%}%>
                  </td>
             </tr>
             <tr>
                  <TD align="center" nowrap class="tdTitle">产品</TD>
                  <td nowrap class="td" colspan="3">
                  <INPUT TYPE="hidden" NAME="dj" value="<%=dj%>">
                  <INPUT TYPE="hidden" NAME="cpid" value="<%=cpid%>">
                  <input <%=edClass%> <%=readonly%> style="WIDTH:70" name="cpbm" value='<%=productRow.get("cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)" >
                  <INPUT <%=edClass%> <%=readonly%> style="WIDTH:200" id="product" name="product" value='<%=productRow.get("product")%>' maxlength='10' onKeyDown="return getNextElement();"  onchange="productNameSelect(this)">
                  <%if(!isEnd){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="SaleProdSingleSelect('form1','srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm','','sumitForm(<%=b_ProductPromotionBean.PRODUCT_CHANGE%>)')">
                  <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';product.value='';cpbm.value='';"><%}%>
                  </td>
             </tr>
              <tr>
                  <td noWrap class="tdTitle">定价类型</td>
                  <td class="td" nowrap align="left">
                  <%if(b_ProductPromotionBean.isAdd){%>
                  <pc:select name='djlx' addNull="0" onSelect='<%="sumitForm("+b_ProductPromotionBean.PRODUCT_CHANGE+")"%>'  style="width:130"  >
                  <%=b_ProductPromotionBean.listToOption(lists, opkey.indexOf(masterRow.get("djlx")))%>
                  </pc:select>
                  <%}else{%>
                  <pc:select name='djlx' addNull="1" onSelect='<%="sumitForm("+b_ProductPromotionBean.PRODUCT_CHANGE+")"%>'  style="width:130"  >
                  <%=b_ProductPromotionBean.listToOption(lists, opkey.indexOf(masterRow.get("djlx")))%>
                  </pc:select>
                   <%}%>
                 </td>
                  <td  nowrap class="tdTitle">客户折扣(%)</td>
                 <td class="td" nowrap ><input type="text" class="edbox" style="WIDTH: 130px" id="zk" onchange="getprice()"  name="zk" value='<%=masterRow.get("zk")%>'></td>
            </tr>
            <tr>
                <td noWrap class="tdTitle">促销单价</td>
                <td noWrap class="td">
                <input type="text" <%=edClass%> <%=readonly%> name="prom_price" onKeyDown="return getNextElement();" onchange="prom_pricechange()" value='<%=masterRow.get("prom_price")%>' style="width:110"  >
                </td>
            </tr>
            <tr>
              <td align="center" nowrap class="tdtitle">时间段</td>
              <td nowrap class="td">
                <input class="edbox" style="WIDTH: 130px" name="startdate" value='<%=masterRow.get("startdate")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <a href="javascript:"><img title=选择日期 onClick="selectDate(startdate);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></a></td>
              <td align="center" nowrap class="td">--</td>
              <td class="td" nowrap>
                <input class="edbox" style="WIDTH: 130px" name="enddate" value='<%=masterRow.get("enddate")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <a href="javascript:"><img title=选择日期 onClick="selectDate(enddate);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></a></td>
            </tr>
            <tr>
                <td  noWrap class="tdTitle">备注</td><%--其他信息--%>
                <td  noWrap class="td">
                <input type="text" <%=edClass%> <%=readonly%> name="memo" onKeyDown="return getNextElement();" value='<%=masterRow.get("memo")%>' style="width:200"  >
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
               <%if(!b_ProductPromotionBean.isApprove){%>
               <%if(!isEnd){%>
               <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 ">
               <input name="button3"  style="width:50" type="button" class="button" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>);" value="删除(D)">
               <%}%>

              <%if(zt.equals("1")){%>
              <input name="button" type="button" class="button" onClick="sumitForm(<%=b_ProductPromotionBean.AFTER_POST%>);" value=" 保存 "><%}%>
              <%if(!b_ProductPromotionBean.isReport){%>
              <input name="btnback" type="button"  style="width:50" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script='<%="backList();"%>'/>
              <%}%>
              <%}%>
            </td>
          </tr>
        </table>
       </td>
    </tr>
  </table>
</form>
<script language="javascript">
  function zkchange()
  {
    var djObj = document.all['dj'];
    var zkObj = document.all['zk'];
    var prom_priceObj = document.all['prom_price'];
    var djlxObj = document.all['djlx'];
    if(djlxObj.value=='')
    {
      zkObj.value='';
      alert('请选择定价类型!');
      return;
    }
    if(djObj.value=='')
    {
      zkObj.value='';
      alert('该产品所选定价类型没定价!');
      return;
    }
    if(isNaN(djObj.value))
    {
      zkObj.value='';
      alert('该产品所选定价类型没定价!');
      return ;
    }
    if(isNaN(zkObj.value))
    {
      zkObj.value='';
      alert('折扣非法!');
      return ;
    }
    prom_priceObj.value=parseFloat(djObj.value)*parseFloat(zkObj.value)/100;
  }
  function prom_pricechange()
  {
    var djObj = document.all['dj'];
    var zkObj = document.all['zk'];
    var prom_priceObj = document.all['prom_price'];
    var djlxObj = document.all['djlx'];
    if(djlxObj.value=='')
    {
      zkObj.value='';
      alert('请选择定价类型!');
      return;
    }
    if(djObj.value==''||djObj.value=='0')
    {
      zkObj.value='';
      alert('该产品所选定价类型没定价!');
      return;
    }
    if(isNaN(djObj.value))
    {
      prom_priceObj.value='';
      alert('该产品所选定价类型没定价!');
      return ;
    }
    if(isNaN(prom_priceObj.value))
    {
      zkObj.value='';
      alert('促销单价非法!');
      return ;
    }
    zkObj.value=parseFloat(prom_priceObj.value)*100/parseFloat(djObj.value);
  }
function getprice()
{
    getRowValue(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PROD_PRICE%>', 'form1', 'srcVar=dj', 'fieldVar='+eval('form1.djlx.value'),eval('form1.cpid.value'));
    unlockScreenWait();
    zkchange();
}
function add()
{
 if(form1.mdeptid.value=='')
 {
   alert('请选择部门!');
   return;
 }
   sumitForm(<%=Operate.DETAIL_ADD%>);
}
//产品编码
function productCodeSelect(obj)
{
  SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',obj.value,'sumitForm(<%=b_ProductPromotionBean.PRODUCT_CHANGE%>)');
}
//产品名称
function productNameSelect(obj)
{
  SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',obj.value,'sumitForm(<%=b_ProductPromotionBean.PRODUCT_CHANGE%>)');
}
//选择购货单位
function selectCustomer()
{
  CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value);
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
<%//&#$
if(b_ProductPromotionBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
<%--销售管理--客户产品折扣,定义具体客户要购买的产品的单价和折扣--%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.util.*"%>
<%
if(!loginBean.hasLimits("produce_promotion", request, response))
    return;
  engine.erp.sale.xixing.B_ProductPromotion b_ProductPromotionBean = engine.erp.sale.xixing.B_ProductPromotion.getInstance(request);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  String retu = b_ProductPromotionBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }

  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = b_ProductPromotionBean.getBatchAddTable();

  RowMap contentrow = b_ProductPromotionBean.getRowinfo();
  ArrayList opkey = new ArrayList(); opkey.add("lsj");opkey.add("ccj"); opkey.add("msj");  opkey.add("qtjg1"); opkey.add("qtjg2");opkey.add("qtjg3");
  ArrayList opval = new ArrayList(); opval.add("零售价");opval.add("出厂价"); opval.add("门市价");  opval.add("其他价格1"); opval.add("其他价格2");opval.add("其他价格3");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};
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
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">
  var isonchange=false;
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function onNavigator()
{
    if(isonchange)
    {
     //alert('已选产品,是否手工保存?');
     //return;
      sumitForm(<%=b_ProductPromotionBean.BATCHPOST%>,-1);
    }else    sumitForm("",-1);
}
function inputonchange()
{
  isonchange=true;
}
function backList()
{
  location.href='produce_promotion.jsp';
}

function selectproduct()
{

  SaleProdSingleSelect('form1','srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',form1.cpid.value,'sumitForm(<%=b_ProductPromotionBean.BATCHADD%>,-1)')
}
//产品编码
function productCodeSelect(obj)
{
  SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',obj.value,'sumitForm(<%=b_ProductPromotionBean.BATCHADD%>,-1)');
}
//产品名称
function productNameSelect(obj)
{
  SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',obj.value,'sumitForm(<%=b_ProductPromotionBean.BATCHADD%>,-1)');
}
</script>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<script language="javascript">var scaner=parent.scaner;</script>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="760" BORDER=0 CELLSPACING=0 CELLPADDING=0>
    <tr>
      <td align="center" height="5"></td>
    </tr>
  </table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0>
          <tr>
            <td class="activeVTab">产品批量增加 </td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
             <tr>
                  <TD align="center" nowrap class="tdTitle">产品</TD>
                  <td nowrap class="td" colspan="3">
                  <INPUT TYPE="HIDDEN" NAME="dj" value="<%=contentrow.get("dj")%>">
                  <INPUT TYPE="HIDDEN" NAME="cpid" value="<%=contentrow.get("cpid")%>">
                  <input class="edbox"  style="WIDTH:100" name="cpbm" value='<%=contentrow.get("cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)" >
                  <INPUT class="edbox"  style="WIDTH:300" id="product" name="product" value='<%=contentrow.get("product")%>' maxlength='10' onKeyDown="return getNextElement();"  onchange="productNameSelect(this)">
                  <img style='cursor:hand' src='../images/view.gif' border=0 onClick="selectproduct()">
                  </td>
          </tr>
          <tr>
                  <td noWrap class="tdTitle">定价类型</td>
                  <td class="td" nowrap align="left">
                  <pc:select name='djlx' addNull="0"   onSelect='<%="sumitForm("+b_ProductPromotionBean.PRODUCT_CHANGE+")"%>'  style="width:130"  >
                  <%=b_ProductPromotionBean.listToOption(lists, opkey.indexOf(contentrow.get("djlx")))%>
                  </pc:select>
                 </td>
                  <td  nowrap class="tdTitle">客户折扣(%)</td>
                 <td class="td" nowrap ><input type="text" class="edbox" style="width:200" id="zk" onchange="zkchange()"  name="zk" value='<%=contentrow.get("zk")%>'></td>
                <td noWrap class="tdTitle">促销单价</td>
                <td noWrap class="td">
                <input type="text" class="edbox" name="prom_price" onKeyDown="return getNextElement();" onchange="prom_pricechange()" value='<%=contentrow.get("prom_price")%>' style="width:110"  >
                </td>
          </tr>
          <tr>
              <td align="center" nowrap class="tdtitle">时间段</td>
              <td nowrap class="td">
                <input class="edbox" style="WIDTH: 100px" name="startdate" value='<%=contentrow.get("startdate")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <a href="javascript:"><img title=选择日期 onClick="selectDate(startdate);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></a></td>
              <td align="center" nowrap class="td">--</td>
              <td class="td" nowrap>
                <input class="edbox" style="WIDTH:100px" name="enddate" value='<%=contentrow.get("enddate")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <a href="javascript:"><img title=选择日期 onClick="selectDate(enddate);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></a></td>
                <td noWrap class="tdTitle">备注</td>
                <td noWrap class="td">
                <input type="text" class="edbox" name="memo" onKeyDown="return getNextElement();" value='<%=contentrow.get("memo")%>' style="width:200"  >
                </td>
             </tr>
             <tr>
                  <td class="td" colspan="8" nowrap>
                 <%

                     String key = "ppdfsgg";
                     pageContext.setAttribute(key, list);
                     int iPage = loginBean.getPageSize();
                     String pageSize = ""+iPage;
                     %>
                  <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"  form="form1" operate="onNavigator();" />
                </tr>
                <tr>
                  <td colspan="8" noWrap class="td">
                    <div style="display:block;width:750;height=3200;overflow-y:auto;overflow-x:auto;">
                      <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                        <tr class="tableTitle">
                          <td width=10  nowrap>
                            <input type='checkbox' name='checkform' onclick='checkAll(form1,this);inputonchange();'>
                          </td>
                          <td  nowrap>客户代码</td>
                          <td  nowrap>客户名称</td>

                        </tr>
                        <%
                          corpBean.regData(list,"dwtxid");
                          int i=0;
                          RowMap[] rows = b_ProductPromotionBean.getDetailRowinfos();
                          for(; i<rows.length; i++)
                          {
                            RowMap row = rows[i];
                            String dwtxid = row.get("dwtxid");
                            RowMap corpRow = corpBean.getLookupRow(row.get("dwtxid"));
                        %>
                        <tr >
                          <td class="td" nowrap>
                            <input type="checkbox" name="sel" value='<%=i%>' onclick="inputonchange()" onKeyDown="return getNextElement();">
                            <input type="hidden" name="dwtxid_<%=i%>" value='<%=dwtxid%>' onKeyDown="return getNextElement();">
                          </td>
                          <td class="td" style="width:60" nowrap><%=corpRow.get("dwdm")%></td>
                          <td class="td" style="width:200" nowrap align="left"><%=corpRow.get("dwmc")%></td>
                        </tr>
                        <%
                          //list.next();
                          }
                          for(; i < iPage; i++){
                            out.print("<tr><td>&nbsp;</td>");
                            out.print("<td>&nbsp;</td><td>&nbsp;</td>");
                            out.print("</tr>");
                          }
                        %>
                      </table>
                    </div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table></td>
    </tr>
    <tr>
      <td> <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
          <tr>
            <td colspan="3" noWrap class="tableTitle">
              <input name="btnback" type="button" class="button" title="保存" value="保存"  onClick="sumitForm(<%=b_ProductPromotionBean.BATCHPOST%>,-1)">
              <input name="btnback" type="button" class="button" title="返回" value="返回" onClick="backList();">
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
 </form>
<SCRIPT language="javascript">
initDefaultTableRow('tableview1',1);
function zkchange()
{
  var djObj = document.all['dj'];
  var zkObj = document.all['zk'];
  var prom_priceObj = document.all['prom_price'];
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


</SCRIPT>
<%out.print(retu);%>
</BODY>
</Html>
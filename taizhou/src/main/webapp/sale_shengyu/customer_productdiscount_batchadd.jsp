<%--销售管理--客户产品折扣,定义具体客户要购买的产品的单价和折扣--%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.util.*"%>
<%
if(!loginBean.hasLimits("customer_product_discount", request, response))
    return;
  engine.erp.sale.shengyu.B_CustomerProductDiscount b_customerproductdiscountBean = engine.erp.sale.shengyu.B_CustomerProductDiscount.getInstance(request);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  String retu = b_customerproductdiscountBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  RowMap row2 = b_customerproductdiscountBean.getRowinfo();
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = b_customerproductdiscountBean.getBatchAddTable();

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
      sumitForm(<%=b_customerproductdiscountBean.BATCHPOST%>,-1);
    }else    sumitForm("",-1);
}
function inputonchange()
{
  isonchange=true;
}
function backList()
{
  location.href='customer_product_discount.jsp';
}
//输入购货单位代码
function customerCodeSelect(obj)
{
  CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value,'sumitForm(<%=b_customerproductdiscountBean.BATCHADD%>,-1)');
}
function selectCustomer()
{
  CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=b_customerproductdiscountBean.BATCHADD%>,-1)');
}
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value,'sumitForm(<%=b_customerproductdiscountBean.BATCHADD%>,-1)');
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
                  <td noWrap class="tdTitle">客户名称</td><%--购货单位--%>
                  <td colspan="3" noWrap class="td">
                    <input type="hidden" name="dwtxid" value='<%=b_customerproductdiscountBean.adddwtxid%>'>
                    <input type="text" class="edbox" style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=b_customerproductdiscountBean.adddwdm%>' onchange="customerCodeSelect(this)" >
                    <input type="text" class="edbox" name="dwmc" onKeyDown="return getNextElement();" value='<%=b_customerproductdiscountBean.adddwmc%>' style="width:200"   onchange="customerNameSelect(this)" >
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="selectCustomer()">
                  </td>

                  <td noWrap class="tdTitle">报价时间</td>
                  <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 100px" name="ksrq"  maxlength='10' value='<%=row2.get("ksrq")%>' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                  <A href="#"><IMG title=选择日期 onClick="selectDate(ksrq);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
                  <INPUT type='hidden' name="jsrq" value='<%=row2.get("jsrq")%>'></td>

               <%--   <td noWrap class="tdTitle">单价</td>
                  <td  noWrap class="td">
                    <input type="text" class="edbox" style="width:80" name="dj"  value='<%=b_customerproductdiscountBean.adddj%>' >
                  </td>--%>
                  <td noWrap class="tdTitle">&nbsp;</td>
                  <td noWrap class="td">&nbsp;</td>
                </tr>
                <tr>
                <%if(!b_customerproductdiscountBean.isBatchAdd){%>
                <td noWrap class="tdTitle" colspan='2' align='left'>修改前的报价存为历史报价<input type="checkbox" name="sflsbj" value="1"></td>
                <%}%>
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
                          <td width=80 nowrap>产品编码</td>
                          <td width=150 nowrap>品名规格</td>
                          <td width=150 nowrap>单价</td>
                          <td width=250 nowrap>备   注</td>
                        </tr>
                        <%
                          prodBean.regData(list,"cpid");
                          int i=0;
                          RowMap[] rows = b_customerproductdiscountBean.getDetailRowinfos();
                          for(; i<rows.length; i++)
                          {
                            RowMap row = rows[i];
                            String cpid = row.get("cpid");
                            RowMap productRow = prodBean.getLookupRow(row.get("cpid"));
                        %>
                        <tr onclick="selectRow()" >
                          <td class="td" nowrap>
                            <input type="checkbox" name="sel" value='<%=i%>' onclick="inputonchange()" onKeyDown="return getNextElement();">
                            <input type="hidden" name="cpid_<%=i%>" value='<%=cpid%>' onKeyDown="return getNextElement();">
                          </td>
                          <td class="td" nowrap><%=productRow.get("cpbm")%></td>
                          <td class="td" nowrap align="left"><%=productRow.get("product")%></td>
                          <td class="td" nowrap ><input type="text" class="edbox" style="width:200" id="dj_<%=i%>"  name="dj_<%=i%>" value='<%=row.get("dj")%>'></td>
                          <td class="td" nowrap ><input type="text" class="edbox" style="width:200" id="bz_<%=i%>"  name="bz_<%=i%>" value='<%=row.get("bz")%>'></td>
                        </tr>
                        <%
                          list.next();
                          }
                          for(; i < iPage; i++){
                            out.print("<tr><td>&nbsp;</td>");
                            out.print("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");
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
              <input name="btnback" type="button" class="button" title="保存" value="保存"  onClick="sumitForm(<%=b_customerproductdiscountBean.BATCHPOST%>,-1)">
              <input name="btnback" type="button" class="button" title="返回" value="返回" onClick="backList();">
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
 </form>
<SCRIPT language="javascript">
  initDefaultTableRow('tableview1',1);
  <%=b_customerproductdiscountBean.adjustInputSize(new String[]{"bz"}, "form1", list.getRowCount())%>
</SCRIPT>
<%out.print(retu);%>
</BODY>
</Html>
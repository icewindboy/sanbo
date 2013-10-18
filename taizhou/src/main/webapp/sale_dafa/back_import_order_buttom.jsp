<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
  String op_search = "op_search";
  String pageCode = "lading_bill";
%><%
  engine.erp.sale.dafa.B_ImportOrderToBackLading b_ImportOrderToBackLadingBean = engine.erp.sale.dafa.B_ImportOrderToBackLading.getInstance(request);
  synchronized(b_ImportOrderToBackLadingBean){
  String retu = b_ImportOrderToBackLadingBean.doService(request, response);
  engine.project.LookUp salePriceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_PRICE);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = b_ImportOrderToBackLadingBean.getDetailTable();
  srcFrm = b_ImportOrderToBackLadingBean.srcFrm;
  multiIdInput =  b_ImportOrderToBackLadingBean.multiIdInput;
  String curUrl = request.getRequestURL().toString();
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  RowMap[] detailRows= b_ImportOrderToBackLadingBean.getDetailRowinfos();//从表多行
  salePriceBean.regData(list,"wzdjid");
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
function selectProduct(row)
{
  var multiId = '';
  for(var i=0;i<form1.elements.length;i++)
  {
    var e = form1.elements[i];
    if(e.type == "checkbox" && e.name!='checkform' && e.checked == true)
      multiId += e.value+',';//选中的checkbox的值组成的由逗号分隔的字符串
  }
  if(multiId.length > 0)
  {
    multiId += '-1';//-1结束
  <%
    if(multiIdInput != null)
    {
      String mutiId = "parent.opener."+srcFrm+"."+multiIdInput;//源表单及其组件
      out.print(mutiId+".value=multiId;");
      out.print(mutiId+".onchange();");
    }
  %>
  }
  parent.close();
}
function checkRadio(row)
{
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = !form1.sel.checked;
  else
    form1.sel[row].checked = !form1.sel[row].checked;
}
function checkTr(row)
{
  if(form1.sel.length+''=='undefined')
    checkRow(form1.sel.checked);
  else
    checkRow(form1.sel[row].checked);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td">
      <%
        int iPage = loginBean.getPageSize()-10;
      %>
      </td>
      <td class="td" align="right">
      <%if(detailRows.length>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value=" 选用 " onKeyDown="return getNextElement();">
      <%}%>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>交货日期</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>计量单位</td>
      <td nowrap>总数量</td>
      <td nowrap>已提量</td>
      <td nowrap>单价</td>
      <td nowrap>销售价</td>
      <td nowrap>折扣</td>
      <td nowrap>备注</td>
    </tr>
    <%
      int i=0;
      for(; i<detailRows.length; i++){
        RowMap detailrow = (RowMap)detailRows[i];
        RowMap prodRow = salePriceBean.getLookupRow(detailrow.get("wzdjid"));
    %>
    <tr onclick="checkTr(<%=i%>)">
      <td nowrap align="center" class="td">
      <input type="checkbox" name="sel" value='<%=detailrow.get("hthwid")%>' onKeyDown="return getNextElement();">
      <input type="hidden" id="hthwid_<%=i%>" value='<%=detailrow.get("hthwid")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jhrq_<%=i%>" class="td"><%=detailrow.get("jhrq")%></td>
      <td nowrap align="center" id="cpbm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("cpbm")%></td>
      <td nowrap align="center" id="product_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("product")%></td>
      <td nowrap align="center" id="jldw_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("jldw")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sl_<%=i%>" class="td"><%=detailrow.get("sl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sl_<%=i%>" class="td"><%=detailrow.get("skdsl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="dj_<%=i%>" class="td"><%=detailrow.get("dj")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="xsj_<%=i%>" class="td"><%=detailrow.get("xsj")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="zk_<%=i%>" class="td"><%=detailrow.get("zk")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="bz_<%=i%>" class="td"><%=detailrow.get("bz")%></td>
 </tr>
    <%  //list.next();
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td"></td>
      <td nowrap class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
</SCRIPT>
<% out.print(retu);}%>
</body>
</html>
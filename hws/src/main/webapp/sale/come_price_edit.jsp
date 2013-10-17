<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function submitForm(oper, row)
{
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function saveForm(oper, row){
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
</script>
<%
  String pageCode = "product_price";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.sale.ComeProductPrice b_ComeProductBean = engine.erp.sale.ComeProductPrice.getInstance(request);//得到实例(初始化实例变量)
  String result = b_ComeProductBean.doService(request, response);
  engine.project.LookUp areaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_AREA);

  //engine.project.LookUp workSectBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_PROCEDURE);//引用车间
  String curUrl = request.getRequestURL().toString();
%>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
      <TD NOWRAP align="center">产品销售定价设定</TD>
  </TR>
</TABLE>
  <table id="tableview1" width="80%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap colspan="2">
     <input name="image" class="img" type="image" title="新增" onClick="submitForm(<%=b_ComeProductBean.PRICE_ADD%>,-1)" src="../images/add_big.gif" border="0"></td>
      <td height='20' nowrap>厂家型号</td>
      <td height='20' nowrap>基准价</td>
    </tr>
    <%
      RowMap[] detailRows = b_ComeProductBean.getSectionRows();
      RowMap detail = null;
      int i=0;
      for(; i<detailRows.length; i++)   {
        detail = (RowMap)detailRows[i];
    %>
    <tr>
      <td class="td" nowrap align="center" width="10"><%=i%></td>
      <td class="td" nowrap align="center" width="20"><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) submitForm(<%=b_ComeProductBean.PRICE_DEL%>,<%=i%>)" src="../images/del.gif" border="0">
      </td>
      <td noWrap class="td"><input id='<%="cjno_"+i%>' class=edFocused_r name='<%="cjno_"+i%>' value='<%=detail.get("cjno")%>' type="text"></td>
      <td noWrap class="td"><input id='<%="xs_jzj_"+i%>' class=edFocused_r name='<%="xs_jzj_"+i%>' value='<%=detail.get("xs_jzj")%>' type="text"></td>
    </tr>
    <%
      }
      for(; i < 4; i++){

    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
    </tr>
    <%}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);
    function hide(){
      parent.hideFrameNoFresh()
    }
  </script>
  <TABLE width="75%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
      <TD align="center">
        <input name="button" type="button" class="button" onClick="saveForm(<%=b_ComeProductBean.PRICE_POST%>);" value="确定(O)">
        <pc:shortcut key="o" script='<%="saveForm("+ b_ComeProductBean.PRICE_POST +")"%>'/>

      </TD>
    </TR>
  </TABLE>
</form>
<%out.print(result);%>
</BODY>
</Html>
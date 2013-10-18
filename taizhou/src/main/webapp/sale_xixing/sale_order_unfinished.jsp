<%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_cancel = "op_cancel";
  String op_approve = "op_approve";
%><%
  engine.erp.sale.xixing.B_UnfinishedSaleOrder b_UnfinishedSaleOrderBean = engine.erp.sale.xixing.B_UnfinishedSaleOrder.getInstance(request);
  String pageCode = "sale_order";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String retu = b_UnfinishedSaleOrderBean.doService(request, response);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);

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
<script language="javascript">
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">销售合同列表</TD>
  </TR></TABLE>
<%
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);//显示明细或返回
    return;
  }
  EngineDataSet masterlist = b_UnfinishedSaleOrderBean.getMaterTable();
  String curUrl = request.getRequestURL().toString();
%>
<%
  String key = "masterlist";
  pageContext.setAttribute(key, masterlist);
  int iPage = loginBean.getPageSize();
  String pageSize = String.valueOf(iPage);
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center">合同号</td>
      <td nowrap align="center">客户类型</td>
      <td nowrap align="center">单位代码</td>
      <td nowrap align="center">客户名称</td>
      <td nowrap align="center">总数量</td>
      <td nowrap align="center">总金额</td>
      <td nowrap align="center">业务员</td>
      <td nowrap align="center">制单人</td>
    </tr>
    <%
      BigDecimal t_zsl = new BigDecimal(0), t_zje = new BigDecimal(0);
      int i=0;
      int count = masterlist.getRowCount();
      masterlist.first();
      for(; i<count; i++)   {
        String zsl = masterlist.getValue("zsl");
        if(b_UnfinishedSaleOrderBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        String zje = masterlist.getValue("zje");
        if(b_UnfinishedSaleOrderBean.isDouble(zje))
          t_zje = t_zje.add(new BigDecimal(zje));
    %>
    <tr  >
      <td  class=td nowrap><%=masterlist.getValue("htbh")%></td>
      <td  class=td nowrap><%=masterlist.getValue("khlx")%></td>
      <td class=td  nowrap><%=masterlist.getValue("dwdm")%></td>
      <td class=td  nowrap><%=masterlist.getValue("dwmc")%></td>
      <td class=td  nowrap align="right"><%=masterlist.getValue("zsl")%></td>
      <td class=td  nowrap align="right"><%=masterlist.getValue("zje")%></td>
      <td class=td  nowrap><%=masterlist.getValue("xm")%></td>
      <td class=td  nowrap><%=masterlist.getValue("czy")%></td>
    </tr>
    <%
      masterlist.next();
      }
      i=count+1;
    %>
      <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zje%>' readonly></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      </tr>
    <%
      for(; i < iPage; i++){
        out.print("<tr><td class='td' nowrap>&nbsp;</td><td class='td' nowrap></td>");
        out.print("<td class='td' nowrap></td><td class='td' nowrap></td>");
        out.print("<td class='td' nowrap></td><td class='td' nowrap></td>");
        out.print("<td class='td' nowrap></td><td class='td' nowrap></td>");
        out.print("</tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
</SCRIPT>
<% out.print(retu);%>
</body>
</html>
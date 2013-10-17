<%
/**
 * 03.03 14:52 新增 新增编号为9的其它入库单select选项. 因为库存中又新增了一个其它入库单的功能.
 *                  同时其它部分也跟随做了相应的改动 yjg
 */
%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "store_bill_kind";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("store_bill_kind", request, response))
    return;
  engine.erp.store.B_StoreBillKind  storeBillKindBean  =  engine.erp.store.B_StoreBillKind.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script>
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr><TD NOWRAP align="center">单据类别信息</TD></tr></table>
<%String retu = storeBillKindBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = storeBillKindBean.getOneTable();
  RowMap row = storeBillKindBean.getRowinfo();
  boolean isCanAdd =false;
  isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
  String edClass = isCanAdd?"class=edbox":"class=ednone";
  String readonly = isCanAdd?"":"readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">&nbsp;单据名称</td>
       <td noWrap class="td">
        <%String djxz = row.get("djxz");if(isCanAdd){%>
        <pc:select name="djxz" style="width:130" value="<%=djxz%>">
          <pc:option value="1">采购入库单</pc:option>
          <pc:option value="2">销售出库单</pc:option>
          <pc:option value="3">自制收货单</pc:option>
          <pc:option value="4">生产领料单</pc:option>
          <pc:option value="5">外加工入库单</pc:option>
          <pc:option value="6">外加工发料单</pc:option>
          <pc:option value="7">损溢单</pc:option>
          <pc:option value="8">移库单</pc:option>
          <pc:option value="9">其它入库单</pc:option><%--03.03 14:52 新增 新增编号为9的其它入库单select选项. 因为库存中又新增了一个其它入库单的功能. yjg--%>
          <pc:option value="10">其它出库单</pc:option>
          <pc:option value="11">同价调拨单</pc:option>
        </pc:select>
       <%}else{
       if (djxz.equals("1"))
          out.print("采购入库单");
       else if(djxz.equals("2"))
         out.print("销售出库单");
       else if(djxz.equals("3"))
         out.print("自制入库单");
       else if(djxz.equals("4"))
         out.print("生产领料单");
       else if(djxz.equals("5"))
         out.print("外加工入库单");
       else if(djxz.equals("6"))
         out.print("外加工发料单");
       else if(djxz.equals("7"))
         out.print("报损单");
       else if(djxz.equals("8"))
         out.print("移库单");
       else if(djxz.equals("9"))
         out.print("其它入库单");
       else if(djxz.equals("10"))
         out.print("其它出库单");
       else if(djxz.equals("11"))
         out.print("同价调拨单");
}
        %>
      </td>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;类别名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="lbmc" VALUE="<%=row.get("lbmc")%>" style="WIDTH:130" MAXLENGTH="<%=ds.getColumn("lbmc").getPrecision()%>" <%=edClass%> <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;类别编码</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="lbbm" VALUE="<%=row.get("lbbm")%>" style="WIDTH:130" MAXLENGTH="<%=ds.getColumn("lbbm").getPrecision()%>" <%=edClass%> <%=readonly%>>
      </td>
    </tr>
     <tr>
      <td noWrap class="tableTitle">&nbsp;收入类型</td>
      <td noWrap class="td">
        正:<INPUT TYPE="radio" NAME="srlx" VALUE="1"  style="WIDTH:30" <%=(row.get("srlx").equals("1")?"checked":"")%> <%=readonly%>>
        负:<INPUT TYPE="radio" NAME="srlx" VALUE="-1" style="WIDTH:30" <%= (row.get("srlx").equals("1")?"":"checked")%>  <%=readonly%>>
      </td>
    </tr>
    <td colspan="2" noWrap class="tableTitle">
      <%if(isCanAdd){%><input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 "><%}%>
      <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭 ">
    </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
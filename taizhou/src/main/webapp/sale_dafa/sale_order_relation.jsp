<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.* ,engine.project.*"%>
<%
  String pageCode = "sale_order_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.sale.B_Sale_Order_Relation b_Sale_Order_RelationBean = engine.erp.sale.B_Sale_Order_Relation.getInstance(request);
  String retu = b_Sale_Order_RelationBean.doService(request, response);
  String curUrl = request.getRequestURL().toString();
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
  function backList()
  {
    location.href='sale_order_list.jsp';
  }
function openJHDwin(scjhid)
{
  paraStr = "../produce/produce_subplan_edit.jsp?operate=2000&code=produce_plan&scjhid="+scjhid;
  openSelectUrl(paraStr, "SCJHCustSelector", winopt2);
}
function openWLXQwin(wlxqjhid)
{
  paraStr = "../produce/mrp_edit.jsp?operate=2000&code=mrp_top&wlxqjhid="+wlxqjhid;
  openSelectUrl(paraStr, "WLXQCustSelector", winopt2);
}
function openRWDwin(rwdid)
{
  paraStr = "../produce/produce_task_edit.jsp?operate=2000&code=produce_task&rwdid="+rwdid;
  openSelectUrl(paraStr, "RWDCustSelector", winopt2);
}
function openTHDwin(id,djlx)
{
  if(djlx==1)
  paraStr = "../sale_dafa/lading_bill_edit.jsp?operate=645355666&code=lading_bill&id="+id;
  else
    paraStr = "../sale_dafa/back_lading_bill_edit.jsp?operate=645355666&code=lading_bill&id="+id;
  openSelectUrl(paraStr, "THDCustSelector", winopt2);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD NOWRAP align="center">业务全程追踪</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" align="right">
      <%--<input name="button2" type="button" class="button" onClick="window.close()" value=" 关闭 ">--%>
      <input name="button2" type="button" class="button" onClick="backList();" value="返回(C)">
      <pc:shortcut key="c" script="backList();" />
      </td>
    </tr>
  </table>
  <table id="tableviews" width="90%" border="0" cellspacing="1" cellpadding="1"  align="center">
  <tr class="tableTitle">
  <td nowrap>合同号:<%=b_Sale_Order_RelationBean.htbh%></td>
  </tr>
  </table>
  <table id="tbcontro2" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" align="left">计划号:</td>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>计划号</td>
      <td nowrap>物料需求号</td>
    </tr>
    <%
      int iPage = loginBean.getPageSize()-10;
      EngineDataSet dsHthwJhh = b_Sale_Order_RelationBean.getJhhTable();
      dsHthwJhh.first();
      for(int i=0; i < dsHthwJhh.getRowCount(); i++) {
        String ul = "location.href='../produce/produce_subplan_edit.jsp?operate=2000&code=produce_plan&scjhid="+dsHthwJhh.getValue("scjhid")+"'";
        String wlxqjhul = "";
        String wlxqh = dsHthwJhh.getValue("wlxqh");
        String wlxqjhid = dsHthwJhh.getValue("wlxqjhid");
        if(!wlxqjhid.equals(""))
          wlxqjhul= "location.href='../produce/produce/mrp_edit.jsp?operate=2000&code=mrp_top&wlxqjhid="+dsHthwJhh.getValue("wlxqjhid")+"'";

    %>
    <tr >
      <td class="td" onclick="openJHDwin(<%=dsHthwJhh.getValue("scjhid")%>)"><%=dsHthwJhh.getValue("jhh")%></td>
      <td class="td" onclick="openWLXQwin(<%=dsHthwJhh.getValue("wlxqjhid")%>)" ><%=dsHthwJhh.getValue("wlxqh")%></td>
    </tr>
    <%
      dsHthwJhh.next();
      }
    %>
  </table>
  <table id="tbcontro3" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" align="left">任务单号:</td>
    </tr>
  </table>
  <table id="tableview2" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>任务单号</td>
      <td nowrap>日期</td>
      <td nowrap>总数量</td>
      <td nowrap>制单人</td>
      <td nowrap>计划号</td>
      <td nowrap>说明</td>
      <td nowrap>状态</td>
    </tr>
    <%
      EngineDataSet dsHthwRwd = b_Sale_Order_RelationBean.getRwdTable();
      dsHthwRwd.first();
      for(int i=0; i < dsHthwRwd.getRowCount(); i++) {
        String ul = "location.href='../produce/produce_task_edit.jsp?operate=2000&code=produce_task&rwdid="+dsHthwRwd.getValue("rwdid")+"'";

    %>
    <tr onclick="openRWDwin(<%=dsHthwRwd.getValue("rwdid")%>)">
      <td class="td"><%=dsHthwRwd.getValue("rwdh")%></td>
      <td class="td"><%=dsHthwRwd.getValue("rq")%></td>
      <td class="td"><%=dsHthwRwd.getValue("zsl")%></td>
      <td class="td"><%=dsHthwRwd.getValue("zdr")%></td>
      <td class="td"><%=dsHthwRwd.getValue("jhh")%></td>
    <td class="td"><%=dsHthwRwd.getValue("sm")%></td>
    <td class="td"><%=dsHthwRwd.getValue("zt")%></td>
    </tr>
    <%
      dsHthwRwd.next();
      }
    %>
  </table>
<table id="tbcontro4" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" align="left">发货单号:</td>
    </tr>
</table>
<table id="tableview4" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>单据号</td>
      <td nowrap>日期</td>
    </tr>
    <%
      EngineDataSet dsHthwThd = b_Sale_Order_RelationBean.getThdTable();
      dsHthwThd.first();
      for(int i=0; i < dsHthwThd.getRowCount(); i++) {
        String ul = "location.href='../sale/lading_bill_edit.jsp?operate=645355666&code=lading_bill&id="+dsHthwThd.getValue("tdid")+"'";
        String djlx = dsHthwThd.getValue("djlx");
        if(djlx.equals("-1"))
          ul = "location.href='../sale/back_lading_bill_edit.jsp?operate=645355666&code=lading_bill&id="+dsHthwThd.getValue("tdid")+"'";
    %>
    <tr onclick="openTHDwin(<%=dsHthwThd.getValue("tdid")%>,<%=djlx%>)">
      <td class="td"><%=dsHthwThd.getValue("tdbh")%></td>
      <td class="td"><%=dsHthwThd.getValue("tdrq")%></td>
    </tr>
    <%
      dsHthwThd.next();
      }
    %>
  </table>
  </form>
  <SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
initDefaultTableRow('tableview2',1);
initDefaultTableRow('tableview4',1);
 </SCRIPT>
</body>
</html>
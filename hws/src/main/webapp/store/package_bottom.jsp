<%--采购合同框架下方合同明细显示--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%><%!

%><%
  String pageCode = "package_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.B_Package b_PackageBean = engine.erp.store.B_Package.getInstance(request);
  b_PackageBean.doService(request, response);
  EngineDataSet list = b_PackageBean.getPxkcTable();
  synchronized(list){
    if(list.changesPending())
      b_PackageBean.openDetailTable();


  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
 engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//LookUp产品信息
 prodBean.regData(list,"cpid");
 propertyBean.regData(list,"dmsxid");
 RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));
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
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
 <td class="td" nowrap></td>
                  <td nowrap>包装辅料</td>
                  <td nowrap>规格属性</td>
                  <td nowrap>数量</td>
                   <td nowrap>单位</td>
                  <td nowrap>备注</td>
    </tr>
    <%
       BigDecimal t_sl = new BigDecimal(0);
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
        String sl =list.getValue("sl");
          if(b_PackageBean.isDouble(sl))
         t_sl = t_sl.add(new BigDecimal(sl));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <td class="td" nowrap align="left"><%=prodBean.getLookupName(list.getValue("cpid"))%></td>
      <td class="td" nowrap align="left"><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>



      <td class="td" nowrap align="right"><%=list.getValue("sl")%></td>
       <td class="td" nowrap align="right"><%=prodRow.get("jldw")%></td>
      <td class="td" nowrap align="right"><%=list.getValue("bz")%></td>


    </tr>
      <%list.next();
      }%>
         <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class="td" nowrap>&nbsp;</td>

      <td class="td" nowrap></td>

    <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
      <td class="td" nowrap></td>
       <td class="td" nowrap></td>


      </tr>
      <%for(; i < 4; i++){
    %>

    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
     <td class="td" nowrap>&nbsp;</td>



    </tr>
    <%}
  }%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
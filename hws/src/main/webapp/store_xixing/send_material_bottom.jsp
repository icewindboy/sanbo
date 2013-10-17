<%
/**
 * <p>Title:库存管理——生产领料单列表明细表格页面</p>
 * <p>Description: 库存管理——采购入库单明细表格页面.</p>
 * <p>             此页面被放到了outputlist_bottom.jsp主框架网页的下方框架内.</p>
 * <p>             为的是要 点击一下已有单据时会出现该单据内部的细节 02.16 新增这张页面.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author
 * @version 1.0
 */
%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "send_material_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.xixing.B_SendMaterial sendMaterialBean = engine.erp.store.xixing.B_SendMaterial.getInstance(request);
  sendMaterialBean.doService(request, response);
  EngineDataSet list = sendMaterialBean.getDetailTable();
  HtmlTableProducer detailProducer = sendMaterialBean.detailProducer;
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//通过加工单明细id得到加工单号
  engine.project.LookUp drawMaterialBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DRAW_MATERIAL_DETALL);//通过生产领料单明细id得到领料单号
  String Type = sendMaterialBean.drawType;
  boolean isDrawType = Type.equals("1");//1为生产领料单
  String showFieldTitle = isDrawType?detailProducer.getFieldInfo("jgdmxid").getFieldname():"领料单号";
  synchronized(list){
    if(list.changesPending())
      sendMaterialBean.openDetailTable(false);
    prodBean.regData(list,"cpid");
    propertyBean.regData(list,"dmsxid");
    processBean.regData(list,"jgdmxid");
    drawMaterialBean.regData(list, "backDrawID");
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
<form name="form1" action="" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <td height='20' nowrap><%=showFieldTitle%></td>
      <%
        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-2;i++)
          out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
      %>
      <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
      <td nowrap><%=detailProducer.getFieldInfo("drawnum").getFieldname()%></td>
      <td height='20' nowrap>计量单位</td>
      <td nowrap><%=detailProducer.getFieldInfo("drawbignum").getFieldname()%></td>
      <td height='20' nowrap>换算单位</td>
      <td nowrap><%=detailProducer.getFieldInfo("drawprice").getFieldname()%></td>
      <td nowrap><%=detailProducer.getFieldInfo("drawsum").getFieldname()%></td>
 <%--     <td nowrap><%=detailProducer.getFieldInfo("producenum").getFieldname()%></td>
      <td height='20' nowrap>生产单位</td>  --%>
      <%--if(SC_OUTSTORE_SHOW_ADD_FIELD.equals("1")){%>
      <td nowrap>工艺路线</td>
      <td nowrap>工序</td>
      <td nowrap>计件单价</td>
      <td nowrap>计件工资</td>
      <td nowrap>工序2</td>
      <td nowrap>计件单价2</td>
      <td nowrap>计件工资2</td>
      <%}--%>
      <td nowrap><%=detailProducer.getFieldInfo("batchno").getFieldname()%></td>
      <td nowrap><%=detailProducer.getFieldInfo("kwid").getFieldname()%></td>
      <td nowrap><%=detailProducer.getFieldInfo("memo").getFieldname()%></td>
    </tr>
    <%BigDecimal t_zsl = new BigDecimal(0), t_hssl = new BigDecimal(0), t_scsl=new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<list.getRowCount(); i++) {
        list.goToRow(i);
        String sl = list.getValue("drawNum");
        if(sendMaterialBean.isDouble(sl))
          t_zsl = t_zsl.add(new BigDecimal(sl));
        String hssl = list.getValue("drawBigNum");
        if(sendMaterialBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
        String scsl = list.getValue("produceNum");
        if(sendMaterialBean.isDouble(scsl))
          t_scsl = t_scsl.add(new BigDecimal(scsl));
    %>
    <tr>
      <td class="td" nowrap><%=i+1%></td>
      <%RowMap  prodRow= prodBean.getLookupRow(list.getValue("cpid"));%>
      <td class="td" nowrap><%=(isDrawType?processBean.getLookupName(list.getValue("jgdmxid")):drawMaterialBean.getLookupName(list.getValue("backDrawID")))%></td>
      <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodRow.get("product")%></td>
      <td class="td" nowrap>
      <%=propertyBean.getLookupName(list.getValue("dmsxid"))%>
      </td>
      <td class="td" align="right" nowrap><%=list.getValue("drawnum")%></td>
      <td class="td" nowrap><input type="text" class=ednone size=8 id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly>
      </td>
      <td class="td" align="right" nowrap><%=list.getValue("drawBigNum")%></td>
      <td class="td" nowrap><input type="text" class=ednone size=8 id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly>
      </td>
       <td class="td" align="right" nowrap><%=list.getValue("drawprice")%></td>
       <td class="td" align="right" nowrap><%=list.getValue("drawsum")%></td>
   <%--   <td class="td" align="right" nowrap><%=list.getValue("producenum")%></td>
      <td class="td" nowrap><%=prodRow.get("scydw")%></td> --%>
      <td class="td" align="center" nowrap><%=list.getValue("batchno")%></td>
      <td class="td" nowrap>
      <%=storeAreaBean.getLookupName(list.getValue("kwid"))%>
      </td>
      <td class="td" nowrap align="center"><%=list.getValue("memo")%></td>
    </tr>
    <%list.next();
    }
    i=count+1;
   %>
   <tr>
   <td class="tdTitle" nowrap>合计</td>
   <td class="td" nowrap>&nbsp;</td>
   <td class="td" nowrap></td>
   <td class="td" nowrap></td>
   <td class="td" nowrap></td>
   <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
   <td class="td" nowrap></td>
   <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
<%--   <td align="right" class="td"></td>
   <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
--%><td align="right" class="td"></td>
   <td align="right" class="td"></td>
   <td align="right" class="td"></td>
   <td align="right" class="td"></td>
   <td class="td" nowrap></td>
   <td class="td" nowrap></td>
    </tr>
      <%
      for(; i < 4; i++){
    %>
    <tr>
     <td class="tdTitle" nowrap></td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
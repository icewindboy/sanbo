<%
/**
 * <p>Title:���������������ϵ��б���ϸ���ҳ��</p>
 * <p>Description: ���������ɹ���ⵥ��ϸ���ҳ��.</p>
 * <p>             ��ҳ�汻�ŵ���outputlist_bottom.jsp�������ҳ���·������.</p>
 * <p>             Ϊ����Ҫ ���һ�����е���ʱ����ָõ����ڲ���ϸ�� 02.16 ��������ҳ��.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author
 * @version 1.0
 */
%>
<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%>
<%@ page import="com.borland.dx.dataset.DataSet"%>
<%
  String pageCode = "receive_material_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.shengyu.B_ReceiveMaterial receiveMaterialBean = engine.erp.store.shengyu.B_ReceiveMaterial.getInstance(request);
  synchronized(receiveMaterialBean){
  receiveMaterialBean.doService(request, response);
  EngineDataSet list = receiveMaterialBean.getDetailTable();
  HtmlTableProducer detailProducer = receiveMaterialBean.detailProducer;
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//���ʹ������
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//ͨ���ӹ�����ϸid�õ��ӹ�����
  engine.project.LookUp drawMaterialBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DRAW_MATERIAL_DETALL);//ͨ���������ϵ���ϸid�õ����ϵ���
  String Type = receiveMaterialBean.drawType;
  boolean isDrawType = Type.equals("1");//1Ϊ�������ϵ�
  String showFieldTitle = isDrawType?detailProducer.getFieldInfo("jgdmxid").getFieldname():"���ϵ���";
  if(list.changesPending())
      receiveMaterialBean.openDetailTable(false);
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
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
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
      <td height='20' nowrap>������λ</td>
      <%-- <td nowrap><%=detailProducer.getFieldInfo("drawbignum").getFieldname()%></td>
      <td height='20' nowrap>���㵥λ</td>--%>
      <td nowrap><%=detailProducer.getFieldInfo("drawbignum").getFieldname()%></td>
      <td height='20' nowrap>���㵥λ</td>
      <%--if(SC_OUTSTORE_SHOW_ADD_FIELD.equals("1")){%>
      <td nowrap>����·��</td>
      <td nowrap>����</td>
      <td nowrap>�Ƽ�����</td>
      <td nowrap>�Ƽ�����</td>
      <td nowrap>����2</td>
      <td nowrap>�Ƽ�����2</td>
      <td nowrap>�Ƽ�����2</td>
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
        if(receiveMaterialBean.isDouble(sl))
          t_zsl = t_zsl.add(new BigDecimal(sl));
        String hssl = list.getValue("drawBigNum");
        if(receiveMaterialBean.isDouble(hssl))
          t_hssl = t_hssl.add(new BigDecimal(hssl));
        String scsl = list.getValue("produceNum");
        if(receiveMaterialBean.isDouble(scsl))
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
      <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly>
      </td>
      <td class="td" align="right" nowrap><%=list.getValue("drawbignum")%></td>
      <td class="td" nowrap><%=prodRow.get("hsdw")%></td>
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
   <td class="tdTitle" nowrap>�ϼ�</td>
   <td class="td" nowrap>&nbsp;</td>
   <td class="td" nowrap></td>
   <td class="td" nowrap></td>
   <td class="td" nowrap></td>
   <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
   <td class="td" nowrap></td>
   <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
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
   <td align="right" class="td"></td>
   <td align="right" class="td"></td>
   <td class="td" nowrap></td>
   <td class="td" nowrap></td>
    </tr>
    <%}}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
</form>
</BODY>
</Html>
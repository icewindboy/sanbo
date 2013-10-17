<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
  String op_copyadd = "op_copyadd";
%><%
  String sysparam = loginBean.getSystemParam("SC_INSTORE_SHOW_ADD_FIELD");
  if(sysparam!= null && sysparam.equals("1"))
  {
    request.getRequestDispatcher("/store/newself_gain_edit.jsp").forward(request, response);
    //httrequest.get.for("newself_gain_edit.jsp?operate=61");
    return;
  }
  engine.erp.store.shengyu.B_SelfGain selfGainBean = engine.erp.store.shengyu.B_SelfGain.getInstance(request);
  selfGainBean.isout="0";
  String pageCode = "self_gain_list";
  boolean hasCopyLimit = loginBean.hasLimits(pageCode, op_copyadd);
%>
<jsp:include page="../pub/scan_bar.jsp" flush="true"/>
<jsp:include page="../baseinfo/script.jsp" flush="true"/>
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
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("������, ���Ժ�");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='self_gain_list.jsp';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isbatchno_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=scydw&fieldVar=scdwgs&fieldVar=isbatchno&fieldVar=isprops', obj.value,'product_change('+i+')');
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isbatchno_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=scydw&fieldVar=scdwgs&fieldVar=isbatchno&fieldVar=isprops', obj.value,'product_change('+i+')');
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
}
function deptChange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'handleperson', 'deptid', eval('form1.deptid.value'), '');
   associateSelect(document.all['prodA'], '<%=engine.project.SysConstant.BEAN_WORK_GROUP%>', 'gzzid', 'deptid', eval('form1.deptid.value'), '',true);
}
function product_change(i){
  document.all['dmsxid_'+i].value="";
  document.all['sxz_'+i].value="";
  document.all['widths_'+i].value="";
  //associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '', true);
}

</script>
<%String retu = selfGainBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_STORE_UNIT_STYLE = selfGainBean.SC_STORE_UNIT_STYLE;//������λ�͸���λ���㷽ʽ1=ǿ�ƻ���,0=����ֵʱ����
  String KC_PRODUCE_UNIT_STYLE = selfGainBean.KC_PRODUCE_UNIT_STYLE;//������λ��������λ���㷽ʽ1=ǿ�ƻ���,0=����ֵʱ����
  String SYS_PRODUCT_SPEC_PROP = selfGainBean.SYS_PRODUCT_SPEC_PROP;
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//ͨ��������id�õ�����������
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//ͨ���ӹ�����ϸid�õ��ӹ�����
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  //engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//���ʹ������
  engine.project.LookUp produceInBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_PRODUCE_IN);//�������
  engine.project.LookUp produceUseBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_USE);//��;
  engine.project.LookUp beanSalePriceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PROD_PRICE);//ȡ�����ۼ�
  //engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = selfGainBean.getMaterTable();
  EngineDataSet list = selfGainBean.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//�õ���½�û����۷�ʽ��ϵͳ����
  boolean isHandwork = loginBean.getSystemParam("KC_HANDIN_STOCK_BILL").equals("1");//�õ��Ƿ�����ֹ����ӵ�ϵͳ����
  boolean isHsbj = bjfs.equals("1");//�������1���Ի��㵥λ����
  //HtmlTableProducer masterProducer = selfGainBean.masterListProducer;
  HtmlTableProducer masterProducer = selfGainBean.masterProducer;
  HtmlTableProducer detailProducer = selfGainBean.detailProducer;
  //HtmlTableProducer detailProducer = selfGainBean.detailProducer;
  RowMap masterRow = selfGainBean.getMasterRowinfo();
  RowMap[] detailRows= selfGainBean.getDetailRowinfos();
  String zt=masterRow.get("state");
  if(selfGainBean.isApprove)
  {
    workShopBean.regData(ds,"deptid");
    storeBean.regData(ds, "storeid");
    produceInBean.regData(ds, "sfdjbid");
  }
  boolean isEnd = selfGainBean.isReport || selfGainBean.isApprove || (!selfGainBean.masterIsAdd() && !zt.equals("0"));//��ʾ�Ѿ���˻������
  boolean isCanDelete = !isEnd && !selfGainBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//û�н���,���޸�״̬,����ɾ��Ȩ��
  isEnd = isEnd || !(selfGainBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));


  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("�����"/* �����:"+ds.getValue("shr")*/) : (zt.equals("9") ? "������" : (zt.equals("2") ? "����" : "δ���"));
  boolean isAdd = selfGainBean.isDetailAdd;
  boolean isPriorNext = false;//saleOutStoreBean.isApprove?false:!saleOutStoreBean.masterIsAdd();
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');onload();">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prodA" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="760" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">�����ջ���(<%=title%>)
			<%--03.19 20:32 ���� ����Ϊ���㲻�˳���ҳ��Ϳ���ֱ�Ӵ�ӡxxx_top.jsp���г��ĵ��ݶ��ӵ���һ��,��һ�ʰ�ť yjg--%>
              <%
              //����������ʱ����ʾ����һ����һ��
              if (isPriorNext)//!selfGainBean.masterIsAdd()
              {
                ds.goToInternalRow(selfGainBean.getMasterRow());
                boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
                if (!isAtFirst)
              {%>
              <a href="#" title="����һ��(ALT+Z)" onClick="sumitForm(<%=selfGainBean.PRIOR%>)">&lt</a>
              <pc:shortcut key='z' script='<%="sumitForm("+selfGainBean.PRIOR+")"%>'/>
             <%}
               if (!isAtLast)
              {%>
              <a href="#" title="����һ��(ALT+X)" onClick="sumitForm(<%=selfGainBean.NEXT%>)">&gt</a>
              <pc:shortcut key='x' script='<%="sumitForm("+selfGainBean.NEXT+")"%>'/>
             <%}
              }%>
           </td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%workShopBean.regData(ds,"deptid"); produceUseBean.regData(ds,"ytid");
                  String handleperson = masterRow.get("handleperson");
                  String checkor = masterRow.get("checkor");
                  if(!isEnd){
                    storeAreaBean.regConditionData(ds, "storeid");
                    personBean.regConditionData(ds, "deptid");
                    workGroupBean.regConditionData(ds,"deptid");
                  }
                 %>
                  <tr>
                  <INPUT TYPE="HIDDEN" NAME="receiveID"  VALUE="<%=masterRow.get("receiveID")%>">
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("receiveCode").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="receiveCode" value='<%=masterRow.get("receiveCode")%>' maxlength='<%=ds.getColumn("receiveCode").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("receiveDate").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="receiveDate" value='<%=masterRow.get("receiveDate")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="ѡ������" onclick="selectDate(form1.receiveDate);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+workShopBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:110" onSelect="deptChange()">
                      <%=workShopBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                   </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("gzzid").getFieldname()%></td>
                    <td noWrap class="td">
                    <%--String sumitGroup = "sumitForm("+workloadGroupBean.GROUP_DETAIL_ADD+");";--%>
                   <%if(isEnd) out.print("<input type='text' value='"+workGroupBean.getLookupName(masterRow.get("gzzid"))+"' style='width:110' class='edline' readonly>");
                   else {%>
                   <pc:select name="gzzid" addNull="1" style="width:110" >
                   <%=workGroupBean.getList(masterRow.get("gzzid"),"deptid",masterRow.get("deptid"))%> </pc:select>
                   <%}%>
                   </td>
                </tr>
                <tr>
                <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("handleperson").getFieldname()%></td>
                  <td  noWrap class="td"><%if(isEnd){%> <input type="text" name="handleperson" value='<%=masterRow.get("handleperson")%>' maxlength='<%=ds.getColumn("handleperson").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  <%}else {%>
                  <pc:select combox="1" className="edFocused" name="handleperson" value="<%=handleperson%>" style="width:110">
                  <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%></pc:select>
                  <%}%>
                  </td>
                <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjlbid").getFieldname()%></td>
                <td  noWrap class="td">
                 <%if(isEnd) out.print("<input type='text' value='"+produceInBean.getLookupName(masterRow.get("sfdjlbid"))+"' style='width:110' class='edline' readonly>");
                 else {
                   String submit = "sumitForm("+selfGainBean.ONCHANGE+")";
                 %>
                 <pc:select name="sfdjlbid" addNull="0" style="width:110" onSelect="<%=submit%>">
                 <%=produceInBean.getList(masterRow.get("sfdjlbid"))%> </pc:select>
                 <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "if(form1.storeid.value!='"+masterRow.get("storeid")+"'){sumitForm("+selfGainBean.ONCHANGE+");}";%>
                    <%if(isEnd) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="1" style="width:110" onSelect="<%=sumit%>">
                      <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                    <%}%>
                  </td>
         <%--
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("djh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="djh" value='<%=masterRow.get("djh")%>' maxlength='<%=ds.getColumn("djh").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>  --%>
                  </tr>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("memo").getFieldname()%></td>
                  <td noWrap class="td" colspan="3"><input type="text" name="memo" value='<%=masterRow.get("memo")%>' maxlength='<%=ds.getColumn("memo").getPrecision()%>' style="width:300" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                  </tr>
                <%/*��ӡ�û��Զ�����Ϣ*/
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                //2004-5-2 16:43 Ϊ����ϸ���ݼ������ҳ����
                String count = String.valueOf(list.getRowCount());
                int iPage = 30;
                String pageSize = String.valueOf(iPage);
                %>
                  <tr> <td colspan="8" noWrap class="td">
                   <pc:navigator id="navigator" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+selfGainBean.TURNPAGE+")"%>' disable='<%=selfGainBean.isRepeat.equals("1") ? "1" : "0"%>'/>
                   </td></tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=15>
                        <input class="edFocused_r"  name="tCopyNumber" value="<%=request.getParameter("tCopyNumber")==null?"1":request.getParameter("tCopyNumber")%>"  size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                        </td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){%>
                          <input name="image" class="img" type="image" title="����(ALT+A)" onClick="buttonEventA()" src="../images/add_big.gif" border="0">
                           <pc:shortcut key="a" script="buttonEventA()"/>
                          <%}%>
                        </td>
                          <td height='20' nowrap><%=detailProducer.getFieldInfo("jgdmxid").getFieldname()%></td>
                 <td nowrap><%=detailProducer.getFieldInfo("djh").getFieldname()%></td>
                          <%
                          for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-1;i++)
                            out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
                          %>
                          <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("batchno").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("drawnum").getFieldname()%></td>
                          <td height='20' nowrap>������λ</td>
                          <td nowrap><%=detailProducer.getFieldInfo("drawbignum").getFieldname()%></td>
                          <td height='20' nowrap>���㵥λ</td>
                          <td nowrap><%=detailProducer.getFieldInfo("kwid").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("memo").getFieldname()%></td>

                          <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      propertyBean.regData(list,"dmsxid");
                      processBean.regData(list,"jgdmxid");
                      beanSalePriceBean.regData(list, "cpid");
                      BigDecimal t_sl = new BigDecimal(0), t_scsl = new BigDecimal(0), t_hssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      //2004-5-2 16:43 Ϊ��ϸ����ҳ�����ҳ
                      int min = navigator.getRowMin(request);
                      int max = navigator.getRowMax(request);
                      //����ȡ�ñ�ÿһҳ�����ݷ�Χ
                      selfGainBean.min = min;
                      selfGainBean.max = max > detailRows.length-1 ? detailRows.length-1 : max;
                      ArrayList cpidList = new ArrayList(max-min+1);
                      ArrayList dmsxidList = new ArrayList(max-min+1);
                      for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        String cpid = detail.get("cpid");
                        String dmsxid = detail.get("dmsxid");
                        cpidList.add(cpid);
                        dmsxidList.add(dmsxid);
                      }
                      prodBean.regData((String[])cpidList.toArray(new String[cpidList.size()]));
                      propertyBean.regData((String[])dmsxidList.toArray(new String[dmsxidList.size()]));//02.15 ���� ����ע��dmsxid����id��Ϊ��ע������ҳ��ͻ���� yjg
                      beanSalePriceBean.regData((String[])cpidList.toArray(new String[cpidList.size()]));

                      list.goToRow(min);
                      //2004-5-2 16:43 �޸� ��ԭ����i<detailRows.length�޸ĳ����ڵ�i<=max && i<list.getRowCount();
                      for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        String sl = detail.get("drawnum");
                        if(selfGainBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String hssl = detail.get("drawbignum");
                        if(selfGainBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String scsl = detail.get("producenum");
                        if(selfGainBean.isDouble(scsl))
                          t_scsl = t_scsl.add(new BigDecimal(scsl));

                        String dmsxid = detail.get("dmsxid");
                        String sx = propertyBean.getLookupName(dmsxid);
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//ҳ�滻��������
                        String kwName = "kwid_"+i;
                        String dmsxidName = "dmsxid_"+i;
                        String jgdmxid=detail.get("jgdmxid");
                        boolean isimport = !jgdmxid.equals("");//����ӹ������ӱ��Ʒ���뵱ǰ�в����޸�
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(!isEnd && !isimport){%>
                          <input type="hidden" name="mutibatch_<%=i%>" value="" onchange="sumitForm(<%=selfGainBean.MATCHING_BATCH%>,<%=i%>)">
                          <input name="image" class="img" type="image" title="������" onClick="if(form1.cpid_<%=i%>.value==''){alert('�������Ʒ');return;}if(form1.storeid.value==''){alert('�������Ʒ'); return;}BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+form1.cpid_<%=i%>.value+'&storeid='+form1.storeid.value+'&dmsxid='+form1.dmsxid_<%=i%>.value)" src="../images/edit.old.gif" border="0">
                          <img style='cursor:hand' title='��ѡ����' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=scydw_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=isprops_<%=i%>&srcVar=isbatchno_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=scydw&fieldVar=scdwgs&fieldVar=isprops&fieldVar=isbatchno','&storeid='+form1.storeid.value, 'product_change(<%=i%>)')">
                          <%}if(!isEnd){%>
                          <input name="image" class="img" type="image" title="���Ƶ�ǰ��" onClick="if(form1.cpid_<%=i%>.value==''){alert('�������Ʒ');return;}sumitForm(<%=selfGainBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <input name="image" class="img" type="image" title="ɾ��" onClick="sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                          <%if(!zt.equals("0")){%><input name="image" class="img" type="image" title="�ϸ�֤��ӡ" onClick='certifyCardPrint(form1.cpid_<%=i%>.value,form1.lsj_<%=i%>.value)' src="../images/print.gif" border="0"><%}%>
                        </td>
                        <%RowMap prodRow= prodBean.getLookupRow(detail.get("cpid"));
                          RowMap prodSalePriceRow= beanSalePriceBean.getLookupRow(detail.get("cpid"));
                          String lsj = prodSalePriceRow.get("lsj");
                        %>
                        <td class="td" nowrap><%=processBean.getLookupName(detail.get("jgdmxid"))%></td>
                        <td class="td" nowrap align="center"><input type="text" <%=isimport?"class=ednone":"class=edbox" %> onKeyDown="return getNextElement();" name="djh_<%=i%>" id="djh_<%=i%>" value='<%=detail.get("djh")%>' maxlength='<%=list.getColumn("djh").getPrecision()%>' <%=isimport?"readonly":""%>></td>
                        <td class="td" nowrap>
                         <input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                         <input type="hidden" name="lsj_<%=i%>" value="<%=lsj%>">
                        <input type="hidden" name="isbatchno_<%=i%>" value="<%=prodRow.get("isbatchno")%>">
                        <input type="hidden" name="hsbl_<%=i%>" value="<%=prodRow.get("hsbl")%>">
                        <input type="hidden" name="scdwgs_<%=i%>" value="<%=prodRow.get("scdwgs")%>">
                        <input type="hidden" name="widths_<%=i%>" value="<%=widths%>">
                        <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                        <input type="text" <%=isimport ? "class=ednone" : detailClass%>  onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                        <td class="td" nowrap>
                        <input <%=detailClass%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('���������Ʒ');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('���������Ʒ');return;}if(form1.isprops_<%=i%>.value=='0'){alert('������û�й������');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="batchno_<%=i%>" name="batchno_<%=i%>" value='<%=detail.get("batchno")%>' maxlength='<%=list.getColumn("batchno").getPrecision()%>' <%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawnum_<%=i%>" name="drawnum_<%=i%>" value='<%=detail.get("drawnum")%>' maxlength='<%=list.getColumn("drawnum").getPrecision()%>' onblur="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawbignum_<%=i%>" name="drawbignum_<%=i%>" value='<%=detail.get("drawbignum")%>' maxlength='<%=list.getColumn("drawbignum").getPrecision()%>' onblur="sl_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                        <iframe id=<%="prod"+i%> src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                        <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="producenum_<%=i%>" name="producenum_<%=i%>" value='<%=detail.get("producenum")%>' maxlength='<%=list.getColumn("producenum").getPrecision()%>' onblur="producesl_onchange(<%=i%>)"<%=readonly%>></td>
                        <input type="hidden" class=ednone onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly>
                        </td>
                        <td class="td" nowrap>
                        <%if(isEnd) out.print("<input type='text' style='width:110' value='"+storeAreaBean.getLookupName(detail.get("kwid"))+"' class='ednone' readonly>");
                        else {%>
                        <pc:select addNull="1" className="edFocused" name="<%=kwName%>" style='width:110'>
                        <%=storeAreaBean.getList(detail.get("kwid"), "storeid", masterRow.get("storeid"))%></pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap align="center"><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" name="memo_<%=i%>" id="memo_<%=i%>" value='<%=detail.get("memo")%>' maxlength='<%=list.getColumn("memo").getPrecision()%>'<%=readonly%>></td>

                      </tr>
                      <%
                      }
                      for(; i < min+4; i++){
                  %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                      <td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                   <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>�ϼ�</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                         <!--<td class="td"><input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                         -->
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>

                      </tr>
                    </table></div>
                    </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
          <tr>
            <td class="td"><b>�Ǽ�����:</b><%=masterRow.get("createDate")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>�Ƶ���:</b><%=masterRow.get("creator")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
            <%--if(!isEnd && hasCopyLimit){%>
             <input type="hidden" name="masterid" value="">
             <input name="btnback" type="button" class="button"  title="�����ջ���(ALT+R)"style='width:95' value="�����ջ���(R)"
             onClick="buttonEventR()"><pc:shortcut key="r" script='<%="buttonEventR()"%>'/><%}--%>
             <%if(!isEnd){%>
             <input type="hidden" name="importProcess" value="">
             <input name="btnback" type="button" class="button"  title="���ӹ���(ALT+Q)"style='width:85' value="���ӹ���(Q)"
             onClick="buttonEventQ()">
                <pc:shortcut key="q" script='<%="buttonEventQ()"%>'/>

              <input type="hidden" name="scanValue" value="">
                <input type="button" class="button" title="�̵��(E)" value="�̵��(E)" style='width:65' onClick="buttonEventE(false)">
                <pc:shortcut key="e" script='<%="buttonEventE(false)"%>'/>
                 <%--input type="button" class="button" title="���̵��(R)" value="���̵��(R)" style='width:80' onClick="buttonEventE(true)">
                <pc:shortcut key="r" script='<%="buttonEventE(true)"%>'/--%>
              <input name="button2" type="button" class="button" title="ɾ������Ϊ����(ALT+X)" value="ɾ������Ϊ����(X)" style='width:120' onClick="sumitForm(<%=selfGainBean.DELETE_BLANK%>);">
                <pc:shortcut key="x" script='<%="sumitForm("+selfGainBean.DELETE_BLANK+")"%>'/>
               <input name="button2" type="button" class="button" title="�������(ALT+N)" value="�������(N)" style='width:80' onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
                <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="����(ALT+S)" value="����(S)" style='width:50' onClick="sumitForm(<%=Operate.POST%>);">
                <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%}%>
              <%if(isCanDelete && !selfGainBean.isReport){%><input name="button3" type="button" class="button" title="ɾ��(ALT+D)" style='width:50' value="ɾ��(D)" onClick="buttonEventD();">
                <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" ��ӡ "--%>
              <%if(!selfGainBean.isApprove && !selfGainBean.isReport){%><input name="btnback" type="button" class="button" title="����(ALT+C)" style='width:50' value="����(C)" onClick="backList();">
                <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
                <%--03.09 11:43 ���� �����رհ�ť�ṩ������ҳ���Ǳ��������ʱʹ��. yjg--%>
                <%if(selfGainBean.isReport){%><input name="btnback" type="button" class="button" title="�ر�(ALT+T)" value="�ر�(T)"  style='width:50' onClick="window.close()">
                <pc:shortcut key="t" script='<%="window.close()"%>'/>
               <%}%>
                <%--03.13 15:37 ���� ������ӡ���ݰ�ť�������Ųɹ���ⵥҳ���ϵ����ݴ�ӡ����. yjg--%>
              <input type="button" class="button" title="��ӡ(ALT+P)" value="��ӡ(P)" style='width:50' onclick="buttonEventP();">
                <pc:shortcut key="p" script='<%="buttonEventP()"%>'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
  function onload(){
    <%=selfGainBean.adjustInputSize(new String[]{"cpbm","product", "jldw","djh", "batchno", "drawnum","sxz", "memo", "hsdw", "drawbignum"},  "form1", selfGainBean.max-min+1, min)%>
  }
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function propertyChange(i){
       var sxzObj = document.all['sxz_'+i];
       //var scdwgsObj = document.all['scdwgs_'+i];
       if(sxzObj.value=='')
         return;
       var widthObj = document.all['widths_'+i];
       widthValue = parseString(sxzObj.value, '<%=SYS_PRODUCT_SPEC_PROP%>(', ')', '(');
       if(widthValue=='')
         return;
       widthObj.value =  widthValue;
       if(widthObj.value=='' || isNaN(widthObj.value))
         return;
       var slObj = document.all['drawnum_'+i];
       var hsslObj = document.all['drawbignum_'+i];
       //var scslObj = document.all['producenum_'+i];
       var hsblObj = document.all['hsbl_'+i];
       if(slObj.value=='' )//&& scslObj.value==''
         return;
       if(hsblObj.value=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
         hsslObj.value = slObj.value;
       else
         hsslObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
       //if(slObj.value!='')
       //  scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthValue));
       /*else if(slObj.value=='' && scslObj.value!=''){
         slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthValue)/parseFloat(scdwgsObj.value));

       }
       */
     }
     function big_change(){
       if(<%=detailRows.length%><1)
         return;
       for(t=0; t<<%=detailRows.length%>; t++){
         sl_onchange(t,false);
       }
     }
     function sl_onchange(i, isBigUnit)
     {
       var oldhsblObj = document.all['hsbl_'+i];
       var sxzObj = document.all['sxz_'+i];
       unitConvert(document.all['prod'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'newsl_onchange('+i+','+isBigUnit+')');
     }
     function newsl_onchange(i, isBigUnit)
     {
       var slObj = document.all['drawnum_'+i];
       var hsslObj = document.all['drawbignum_'+i];
       var scslObj = document.all['producenum_'+i];
       var hsblObj = document.all['truebl_'+i];
       var scdwgsObj = document.all['scdwgs_'+i];//������ʽ
       var obj = isBigUnit ? hsslObj : slObj;//07-19 ��Ϊ��û���˻�������,����ȫ�������slOj.��ͬ
       var widthObj = document.all['widths_'+i];//������ԵĿ��
       var showText = isBigUnit ? "����Ļ��������Ƿ�" : "����������Ƿ�";
       var showText2 = isBigUnit ? "����Ļ�������С����" : "���������С����";
       var changeObj = isBigUnit ? slObj : hsslObj;
       /*alert(hsblObj.value+"hsbl");
       alert(changeObj.value+"change");
       alert(obj.value+"obj")
       */
       if(changeObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1')//�Ƿ�ǿ��ת��
          return;
        if(obj.value=="")
          return;
        if(isNaN(obj.value))
        {
          alert(showText);
          obj.focus();
          return;
        }
        if(obj.value<=0)
        {
          alert(showText2);
          obj.focus();
          return;
        }
        if(hsblObj.value=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
        {
          return;//changeObj.value = obj.value;
        }
        else
        {
          changeObj.value = formatQty(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
        }
        cal_tot('sl');
        cal_tot('hssl');
        /*if(scslObj.value!="" && '<%=KC_PRODUCE_UNIT_STYLE%>'!='1')
          return;
        else{
          if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
            scslObj.value= isBigUnit ? changeObj.value : slObj.value;
          else if(isBigUnit)
            scslObj.value = formatQty(hsblObj.value=="" ? parseFloat(hsslObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value) : parseFloat(hsslObj.value)*parseFloat(hsblObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
          else if(!isBigUnit)
            scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
        }
        cal_tot('scsl');
        */
       }
       function producesl_onchange(i)
       {
         var oldhsblObj = document.all['hsbl_'+i];
         var sxzObj = document.all['sxz_'+i];
         unitConvert(document.all['prod'], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'newproducesl_onchange('+i+')');
       }
       function newproducesl_onchange(i)
       {
         var slObj = document.all['drawnum_'+i];
         var hsslObj = document.all['drawbignum_'+i];
         var scslObj = document.all['producenum_'+i];
         var hsblObj = document.all['truebl_'+i];
         var scdwgsObj = document.all['scdwgs_'+i];//������ʽ
         var widthObj = document.all['widths_'+i];//������ԵĿ��
         if(slObj.value!="" && '<%=KC_PRODUCE_UNIT_STYLE%>'!='1')//���������������Ƿ�ǿ��ת��
           return;
         if(scslObj.value=="")
           return;
         if(isNaN(scslObj.value))
         {
           alert('��������������Ƿ�');
           obj.focus();
           return;
         }
         if(scslObj.value<=0)
         {
           alert('�������������С����');
           obj.focus();
           return;
         }
         if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
           return;//slObj.value= scslObj.value;
         else
           slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthObj.value)/parseFloat(scdwgsObj.value));
         if(hsslObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1')
           return;
         if(hsblObj.value=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
           return;//hsslObj.value = slobj.value;
         else
          hsslObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
         cal_tot('scsl');
         cal_tot('sl');
         cal_tot('hssl');
       }
       min = <%=selfGainBean.min%>;
       max = <%=selfGainBean.max%>;
       newmax = <%=detailRows.length%> >= max+1 ?  max+1 : <%=detailRows.length%>;
       function cal_tot(type)
       {
         var tmpObj;
         var tot=0;
         for(i=min ; i<newmax; i++)
         {
           if(type == 'sl')
             tmpObj = document.all['drawnum_'+i];
           else if(type == 'scsl')
             tmpObj = document.all['producenum_'+i];
           else if(type == 'hssl')
             tmpObj = document.all['drawbignum_'+i];
           else
             return;
           if(tmpObj.value!="" && !isNaN(tmpObj.value))
             tot += parseFloat(tmpObj.value);
         }
         if(type == 'sl')
           document.all['t_sl'].value = formatQty(tot);
         if(type == 'scsl')
           document.all['t_scsl'].value = formatQty(tot);
         if(type == 'hssl')
           document.all['t_hssl'].value = formatQty(tot);
    }
  function BatchMultiSelect(frmName, srcVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "BatchSelector";
    paraStr = "../store/select_batch.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function ImportSelfSelect(frmName, srcVar, fieldVar,methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "BatchSelector";
    paraStr = "../store/single_self_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function ImportProcessSelect(frmName,srcVar,fieldVar,curID,isout,storeid,methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
    var winName= "ImportProcessSelector";
    paraStr = "../store_shengyu/import_process.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+curID+"&isout="+isout+"&storeid="+storeid;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function transferScan(isNew)//�����̵��
  {
    var scanValueObj = form1.scanValue;
    scanValueObj.value = scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "self_gain_edit.jsp", "IT3CW32d.DLL")%>');//�õ�������Ʒ��������ŵ��ַ���
    //alert(scanValueObj.value);
      if(isNew)
       sumitForm(<%=selfGainBean.NEW_TRANSFERSCAN%>);
         else
      sumitForm(<%=selfGainBean.TRANSFERSCAN%>);
  }

  function buttonEventE(isNew)
  {
    if(form1.storeid.value=='')
    {
      alert('��ѡ��ֿ�');return;
    }
    transferScan(isNew);
  }
  //���ӹ���
  function buttonEventQ()
  {
    if(form1.storeid.value=='')
    {
      alert('��ѡ��ֿ�');
      return;
    }
     ImportProcessSelect('form1','srcVar=importProcess','fieldVar=jgdid',form1.deptid.value,'0',form1.storeid.value,'sumitForm(<%=selfGainBean.SELF_SEL_PROCESS%>)')
  }
     //���������ջ���
     function buttonEventR()
     {
        ImportSelfSelect('form1','srcVar=masterid','fieldVar=receiveid','sumitForm(<%=selfGainBean.COPY_SELF%>)')
     }
  //ɾ��
  function buttonEventD()
  {
     if(confirm('�Ƿ�ɾ���ü�¼��'))sumitForm(<%=Operate.DEL%>);
  }
  function buttonEventP()
  {
   location.href='../pub/pdfprint.jsp?code=self_gain_edit_bill&operate=<%=selfGainBean.PRINT_BILL%>&a$sfdjid=<%=masterRow.get("receiveid")%>&src=../store_shengyu/self_gain_edit.jsp'
  }

  function buttonEventA()
  {
      if(form1.storeid.value==''){alert('��ѡ��ֿ�');return;}sumitForm(<%=Operate.DETAIL_ADD%>);
  }
  function certifyCardPrint(cpid, xsj)
  {
    var paraStr = "../store_shengyu/barcode_print.jsp?cpid="+cpid+"&xsj="+xsj;
    window.open(paraStr, "CertifyCardPrint");
    //document.all.iprint.src=paraStr;
  }
</script>
<%if(selfGainBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
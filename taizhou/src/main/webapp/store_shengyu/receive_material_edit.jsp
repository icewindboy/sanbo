<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.store.shengyu.B_ReceiveMaterial receiveMaterialBean = engine.erp.store.shengyu.B_ReceiveMaterial.getInstance(request);
  receiveMaterialBean.isout="0";
  String pageCode = "receive_material_list";
  String SC_OUTSTORE_SHOW_ADD_FIELD =receiveMaterialBean.SC_OUTSTORE_SHOW_ADD_FIELD;//�ύ�����Ƿ�ֻ���Ƶ��˿����ύ,1=���Ƶ��˿��ύ,0=��Ȩ���˿��ύ
  //boolean hasApproveLimit = isApprove && loginBean.hasLimits(pageCode, op_approve);
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
  location.href='receive_material_list.jsp';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=scydw&fieldVar=scdwgs&fieldVar=isprops', obj.value,'product_change('+i+')');
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=scydw&fieldVar=scdwgs&fieldVar=isprops', obj.value,'product_change('+i+')');
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
}
function deptChange(){
   associateSelect(document.all['prod1'], '<%=engine.project.SysConstant.BEAN_WORK_GROUP%>', 'gzzid', 'deptid', eval('form1.deptid.value'), '',true);
   //associateSelect(document.all['prod2'], '<%=engine.project.SysConstant.BEAN_WORK_GROUP%>', 'sc__gzzid', 'deptid', eval('form1.deptid.value'), '',true);
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'handleperson', 'deptid', eval('form1.deptid.value'), '',true);
}
function detailtechnicschange(i){//�ӱ�ѡ����·���¼�
  associateSelect(document.all['prod_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'gx_'+i, 'gylxid', eval('form1.gylxid_'+i+'.value'), '', true);
  associateSelect(document.all['prodA_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'gx2_'+i, 'gylxid', eval('form1.gylxid_'+i+'.value'), '', true);
  }
  function product_change(i){
  document.all['dmsxid_'+i].value="";
  document.all['sxz_'+i].value="";
  document.all['widths_'+i].value="";
  if(<%=SC_OUTSTORE_SHOW_ADD_FIELD.equals("1")%>)
  associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '', true);
  }
</script>
<%String retu = receiveMaterialBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_STORE_UNIT_STYLE = receiveMaterialBean.SC_STORE_UNIT_STYLE;//������λ�͸���λ���㷽ʽ1=ǿ�ƻ���,0=����ֵʱ����
  String KC_PRODUCE_UNIT_STYLE = receiveMaterialBean.KC_PRODUCE_UNIT_STYLE;//������λ��������λ���㷽ʽ1=ǿ�ƻ���,0=����ֵʱ����
  //03.15 21:15 �޸� �� workShopBean ��ȡֵ��ԭ����engine.project.SysConstant.BEAN_WORKSHOP��Ϊ���ڵ�engine.project.SysConstant.BEAN_DEPT��ʵ��ȡ�����в��ŵ�Ŀ��. yjg
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//���ݹ���·��id�õ�����
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE);//����
  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//ͨ��������id�õ�����������
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//ͨ���ӹ�����ϸid�õ��ӹ�����
  engine.project.LookUp drawMaterialBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DRAW_MATERIAL_DETALL);//ͨ���������ϵ���ϸid�õ����ϵ���
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//���ʹ������
  engine.project.LookUp produceOutBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_PRODUCE_OUT);//�������
  engine.project.LookUp produceUseBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_USE);//��;
  //engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = receiveMaterialBean.getMaterTable();
  EngineDataSet list = receiveMaterialBean.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//�õ���½�û����۷�ʽ��ϵͳ����
  boolean isHsbj = bjfs.equals("1");//�������1���Ի��㵥λ����
  HtmlTableProducer masterProducer =null;
  if(SC_OUTSTORE_SHOW_ADD_FIELD.equals("1"))
    masterProducer = receiveMaterialBean.masterProducer;
  else
    masterProducer = receiveMaterialBean.newmasterProducer;
  HtmlTableProducer detailProducer = receiveMaterialBean.detailProducer;
  RowMap masterRow = receiveMaterialBean.getMasterRowinfo();
  RowMap[] detailRows= receiveMaterialBean.getDetailRowinfos();
  String zt=masterRow.get("state");
  if(receiveMaterialBean.isApprove)
  {
    workShopBean.regData(ds,"deptid");
    storeBean.regData(ds, "storeid");
    produceOutBean.regData(ds, "sfdjlbid");
    produceUseBean.regData(ds,"ytid");
  }
  boolean isEnd = receiveMaterialBean.isReport || receiveMaterialBean.isApprove || (!receiveMaterialBean.masterIsAdd() && !zt.equals("0"));//��ʾ�Ѿ���˻������
  boolean isCanDelete = !isEnd && !receiveMaterialBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//û�н���,���޸�״̬,����ɾ��Ȩ��
  isEnd = isEnd || !(receiveMaterialBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));


  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("�����"/* �����:"+ds.getValue("shr")*/) : (zt.equals("9") ? "������" : (zt.equals("2") ? "����" : "δ���"));
  boolean isAdd = receiveMaterialBean.isDetailAdd;
  String Type = receiveMaterialBean.drawType;
  boolean isDrawType = Type.equals("1");//1Ϊ�������ϵ�

  String showFieldTitle = isDrawType ? detailProducer.getFieldInfo("jgdmxid").getFieldname() : "���ϵ���";
  String SYS_PRODUCT_SPEC_PROP = receiveMaterialBean.SYS_PRODUCT_SPEC_PROP;
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');onloadOn();">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prod1" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prod2" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="760" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="processid" value="<%=masterRow.get("processid")%>">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">�������ϵ�(<%=title%>)
            <%
            //����������ʱ����ʾ����һ����һ��
              if (!receiveMaterialBean.masterIsAdd())
              {
                ds.goToInternalRow(receiveMaterialBean.getMasterRow());
                boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
                if (!isAtFirst)
              {%>
              <a href="#" title="����һ��(ALT+Z)" onClick="sumitForm(<%=receiveMaterialBean.PRIOR%>)">&lt</a>
              <pc:shortcut key='z' script='<%="sumitForm("+receiveMaterialBean.PRIOR+")"%>'/>
             <%}
               if (!isAtLast)
              {%>
              <a href="#" title="����һ��(ALT+X)" onClick="sumitForm(<%=receiveMaterialBean.NEXT%>)">&gt</a>
              <pc:shortcut key='x' script='<%="sumitForm("+receiveMaterialBean.NEXT+")"%>'/>
             <%} }%>
            </td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%
                  workShopBean.regData(ds,"deptid"); String handleperson = masterRow.get("handleperson");
                  RowMap  sfdjldRow= produceOutBean.getLookupRow(masterRow.get("sfdjlbid"));
                  String srlx = sfdjldRow.get("srlx");
                  String checkor = masterRow.get("checkor");
                 if(!isEnd){
                   workGroupBean.regConditionData(ds,"deptid");
                   storeAreaBean.regConditionData(ds, "storeid");
                   personBean.regConditionData(ds, "deptid");
                 }
                 %>
                  <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("drawcode").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="drawcode" value='<%=masterRow.get("drawcode")%>' maxlength='<%=ds.getColumn("drawcode").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("drawdate").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="drawdate" value='<%=masterRow.get("drawdate")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="ѡ������" onclick="selectDate(form1.drawdate);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "if(form1.storeid.value!='"+masterRow.get("storeid")+"'){sumitForm("+receiveMaterialBean.ONCHANGE+");}";%>
                    <%if(isEnd) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="1" style="width:110" onSelect="<%=sumit%>">
                      <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                    <%}%>
                  </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjlbid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) {
                      out.print("<input type='text' value='"+sfdjldRow.get("lbmc")+"' style='width:110' class='edline' readonly>");
                      out.print("<input type='hidden' name='srlx' value="+sfdjldRow.get("srlx")+">");
                    }
                    else {
                       String submit = "sumitForm("+receiveMaterialBean.ONCHANGE+")";
                     %>
                    <pc:select name="sfdjlbid" addNull="0" style="width:110" onSelect="<%=submit%>">
                    <%=produceOutBean.getList(masterRow.get("sfdjlbid"))%> </pc:select>
                     <input type="hidden" name="srlx" value='<%=sfdjldRow.get("srlx")%>'>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">���ϲ���</td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+workShopBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:110" onSelect="deptChange()">
                      <%=workShopBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                   </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("handleperson").getFieldname()%></td>
                  <td  noWrap class="td"><%if(isEnd){%> <input type="text" name="handleperson" value='<%=masterRow.get("handleperson")%>' maxlength='<%=ds.getColumn("handleperson").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  <%}else {%>
                  <pc:select combox="1" className="edFocused" name="handleperson" value="<%=handleperson%>" style="width:110" >
                  <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%></pc:select>
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
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("memo").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="memo" value='<%=masterRow.get("memo")%>' maxlength='<%=ds.getColumn("memo").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                  </tr>
                   <tr>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("ytid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+produceUseBean.getLookupName(masterRow.get("ytid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="ytid" addNull="1" style="width:110">
                      <%=produceUseBean.getList(masterRow.get("ytid"))%> </pc:select>
                    <%}%>
                  </td>
                  <%if(SC_OUTSTORE_SHOW_ADD_FIELD.equals("1")){%>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("netnum").getFieldname()%></td>
                  <td noWrap class="td">
                    <input type="text" name="netnum" value='<%=masterRow.get("netnum")%>' maxlength='<%=ds.getColumn("netnum").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dyneside").getFieldname()%></td>
                  <td  noWrap class="td">
                  <input type="text" name="dyneside" value='<%=masterRow.get("dyneside")%>' maxlength='<%=ds.getColumn("dyneside").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("hotside").getFieldname()%></td>
                  <td  noWrap class="td">
                  <input type="text" name="hotside" value='<%=masterRow.get("hotside")%>' maxlength='<%=ds.getColumn("hotside").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                    <%}%>
                  <%if(!SC_OUTSTORE_SHOW_ADD_FIELD.equals("1")){%>
                      <td></td>
                      <td></td>
                      <td></td>
                      <td></td>
                      <td></td>
                      <td></td>
                      <%}%>
                  </tr>
                  <%if(SC_OUTSTORE_SHOW_ADD_FIELD.equals("1")){%>
                  <tr>
                 <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("gzzid").getFieldname()%></td>
                    <td noWrap class="td">
                    <%--String sumitGroup = "sumitForm("+workloadGroupBean.GROUP_DETAIL_ADD+");";--%>
                   <%if(isEnd) out.print("<input type='text' value='"+workGroupBean.getLookupName(masterRow.get("gzzid"))+"' style='width:110' class='edline' readonly>");
                   else {%>
                   <pc:select name="gzzid" addNull="1" style="width:110" >
                   <%=workGroupBean.getList(masterRow.get("gzzid"),"deptid",masterRow.get("deptid"))%> </pc:select>
                   <%}%>
                   </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("zgf").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="zgf" value='<%=masterRow.get("zgf")%>' maxlength='<%=ds.getColumn("zgf").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                   <td noWrap class="tdTitle">������2</td>
                   <td noWrap class="td">
                   <%if(isEnd) out.print("<input type='text' value='"+workGroupBean.getLookupName(masterRow.get("sc__gzzid"))+"' style='width:110' class='edline' readonly>");
                   else {%>
                   <pc:select name="sc__gzzid" addNull="1" style="width:110" >
                   <%=workGroupBean.getList(masterRow.get("sc__gzzid"),"deptid",masterRow.get("deptid"))%> </pc:select>
                   <%}%>
                   </td>
                   <td noWrap class="tdTitle">�ܹ���2</td>
                    <td noWrap class="td"><input type="text" name="zgf2" value='<%=masterRow.get("zgf2")%>' maxlength='<%=ds.getColumn("zgf2").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  </tr>
                  <tr>
                   <td noWrap class="tdTitle">���</td>
                    <td noWrap class="td"><input type="text" name="bc" value='<%=masterRow.get("bc")%>' <%=edClass%> maxlength='<%=ds.getColumn("bc").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                   <td></td>
                    <td></td>
                     <td></td>
                      <td></td>
                       <td></td>
                        <td></td>
                  </tr>
                <%}%>
                <%//*��ӡ�û��Զ�����Ϣ*/
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                //2004-5-2 16:43 Ϊ����ϸ���ݼ������ҳ����
                String count = String.valueOf(list.getRowCount());
                int iPage = 30;
                String pageSize = String.valueOf(iPage);
                %>
                 <tr> <td colspan="8" noWrap class="td">
                   <pc:navigator id="navigator" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+receiveMaterialBean.TURNPAGE+")"%>' disable='<%=receiveMaterialBean.isRepeat.equals("1") ? "1" : "0"%>'/>
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
                          <td height='20' nowrap><%=showFieldTitle%></td>
                         <%
                        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-2;i++)
                          out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
                        %>
                          <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("batchno").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("drawnum").getFieldname()%></td>
                          <td height='20' nowrap>������λ</td>
                          <td nowrap><%=detailProducer.getFieldInfo("drawbignum").getFieldname()%></td>
                          <td height='20' nowrap>���㵥λ</td>
                          <%--<td nowrap><%=detailProducer.getFieldInfo("producenum").getFieldname()%></td>
                          <td height='20' nowrap>������λ</td>--%>
                          <%if(SC_OUTSTORE_SHOW_ADD_FIELD.equals("1")){%>
                          <td nowrap>����·��</td>
                          <td nowrap>����</td>
                          <td nowrap>�Ƽ�����</td>
                          <td nowrap>�Ƽ�����</td>
                          <td nowrap>����2</td>
                          <td nowrap>�Ƽ�����2</td>
                          <td nowrap>�Ƽ�����2</td>
                          <%}%>
                          <td nowrap><%=detailProducer.getFieldInfo("kwid").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("memo").getFieldname()%></td>
                          <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      propertyBean.regData(list,"dmsxid");
                      processBean.regData(list,"jgdmxid");
                      drawMaterialBean.regData(list, "backDrawID");
                      //technicsRouteBean.regData(list, "gylxid");
                      if(!isEnd){
                        technicsRouteBean.regConditionData(list,"cpid");
                       //technicsNameBean.regConditionData(list,"gylxid");
                      }
                      BigDecimal t_jjgz = new BigDecimal(0),t_jjgz2 = new BigDecimal(0);
                      BigDecimal t_sl = new BigDecimal(0), t_scsl = new BigDecimal(0), t_hssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      int min = navigator.getRowMin(request);
                      int max = navigator.getRowMax(request);
                      //����ȡ�ñ�ÿһҳ�����ݷ�Χ
                      receiveMaterialBean.min = min;
                      receiveMaterialBean.max = max > detailRows.length-1 ? detailRows.length-1 : max;
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

                      list.goToRow(min);
                      //2004-5-2 16:43 �޸� ��ԭ����i<detailRows.length�޸ĳ����ڵ�i<=max && i<list.getRowCount();
                      for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        String sl = detail.get("drawnum");
                        if(receiveMaterialBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String hssl = detail.get("drawbignum");
                        if(receiveMaterialBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String scsl = detail.get("producenum");
                        if(receiveMaterialBean.isDouble(scsl))
                          t_scsl = t_scsl.add(new BigDecimal(scsl));
                        String jjgz = detail.get("jjgz");
                        if(receiveMaterialBean.isDouble(jjgz))
                          t_jjgz = t_jjgz.add(new BigDecimal(jjgz));
                        String jjgz2 = detail.get("jjgz2");
                       if(receiveMaterialBean.isDouble(jjgz2))
                          t_jjgz2 = t_jjgz2.add(new BigDecimal(jjgz2));
                        String dmsxid = detail.get("dmsxid");
                        String sx = propertyBean.getLookupName(dmsxid);
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//ҳ�滻��������
                        String kwName = "kwid_"+i;
                        String dmsxidName = "dmsxid_"+i;
                        String gxName = "gx_"+i;
                        String gylxidName = "gylxid_"+i;
                        String gx = detail.get("gylxid").length()>0 ? detail.get("gx") : "";
                        String gxName2 = "gx2_"+i;
                        String gx2 = detail.get("gylxid").length()>0 ? detail.get("gx2") : "";
                        String jgdmxid=detail.get("jgdmxid");
                        boolean isimport = !jgdmxid.equals("");//����ӹ������ӱ��Ʒ���뵱ǰ�в����޸�
                        String test2 = "getJjdjValue2("+i+");";
                    %>
                      <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(!isEnd && !isimport){%>
                          <input type="hidden" name="mutibatch_<%=i%>" value="" onchange="sumitForm(<%=receiveMaterialBean.MATCHING_BATCH%>,<%=i%>)">
                          <img style='cursor:hand' title='��ѡ����' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=scydw_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=isprops_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=scydw&fieldVar=scdwgs&fieldVar=isprops','&storeid='+form1.storeid.value,'product_change(<%=i%>)')">
                          <%}if(!isEnd){%>
                      <input name="image" class="img" type="image" title="������" onClick="if(form1.cpid_<%=i%>.value==''){alert('�������Ʒ');return;}if(form1.storeid.value==''){alert('�������Ʒ'); return;}BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+form1.cpid_<%=i%>.value+'&storeid='+form1.storeid.value+'&dmsxid='+form1.dmsxid_<%=i%>.value)" src="../images/edit.old.gif" border="0">
                      <input name="image" class="img" type="image" title="���Ƶ�ǰ��" onClick="if(form1.cpid_<%=i%>.value==''){alert('�������Ʒ');return;}sumitForm(<%=receiveMaterialBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <input name="image" class="img" type="image" title="ɾ��" onClick="sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                          String bb = drawMaterialBean.getLookupName(detail.get("backDrawID"));
                        %>
                        <td class="td" nowrap><%=(isDrawType?processBean.getLookupName(detail.get("jgdmxid")):drawMaterialBean.getLookupName(detail.get("backDrawID")))%></td>
                        <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="hidden" name="hsbl_<%=i%>" value="<%=prodRow.get("hsbl")%>">
                        <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                        <input type="hidden" name="scdwgs_<%=i%>" value="<%=prodRow.get("scdwgs")%>">
                        <input type="hidden" name="widths_<%=i%>" value="<%=widths%>">
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
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="batchno_<%=i%>" name="batchno_<%=i%>" value='<%=detail.get("batchno")%>' maxlength='<%=list.getColumn("batchno").getPrecision()%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('�������Ʒ');return;}if(form1.storeid.value==''){alert('�������Ʒ'); return;}BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+form1.cpid_<%=i%>.value+'&storeid='+form1.storeid.value+'&row='+<%=i%>+'&dmsxid='+form1.dmsxid_<%=i%>.value+'&ph='+form1.batchno_<%=i%>.value, true, <%=i%>, true,'methodName=sl_onchange(<%=i%>, false)')" <%=readonly%>>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawnum_<%=i%>" name="drawnum_<%=i%>" value='<%=detail.get("drawnum")%>' maxlength='<%=list.getColumn("drawnum").getPrecision()%>' onblur="sl_onchange(<%=i%>, false)"<%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly>
                          <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="producenum_<%=i%>" name="producenum_<%=i%>" value='<%=detail.get("producenum")%>' maxlength='<%=list.getColumn("producenum").getPrecision()%>' onblur="producesl_onchange(<%=i%>)" <%=readonly%>>
                          <input type="hidden" class=ednone onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly>
                        </td>
                        <td class="td" align="right" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawbignum_<%=i%>" name="drawbignum_<%=i%>" value='<%=detail.get("drawbignum")%>' maxlength='<%=list.getColumn("drawbignum").getPrecision()%>' onblur="sl_onchange(<%=i%>, true)"<%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                        <iframe id="prod_<%=i%>" src="" width="0" height=0 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                        <td class="td" nowrap>
                        <%if(isEnd) out.print("<input type='text' style='width:110' value='"+storeAreaBean.getLookupName(detail.get("kwid"))+"' class='ednone' readonly>");
                        else {%>
                        <pc:select addNull="1" className="edFocused" name="<%=kwName%>" style='width:110'>
                        <%=storeAreaBean.getList(detail.get("kwid"), "storeid", masterRow.get("storeid"))%></pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap align="center"><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" name="memo_<%=i%>" id="memo_<%=i%>" value='<%=detail.get("memo")%>' maxlength='<%=list.getColumn("memo").getPrecision()%>'<%=readonly%>></td>
                      </tr>
                      <%list.next();
                      }
                      for(; i < min+4; i++){
                  %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>�ϼ�</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td"><input id="t_hssl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td align="right" class="td">&nbsp;</td>
                        <td align="right" class="td">&nbsp;</td>
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
            <%--if(!isEnd && Type.equals("1")){%>
             <input type="hidden" name="selectProcess" value=""><input type="hidden" name="selectMaterail" value="" onchange="sumitForm(<%=receiveMaterialBean.SELECT_MATERAIL%>)">
             <input name="btnback" type="button" class="button" title="���ӹ���(ALT+Q)" value="���ӹ���(Q)" style="width:80"
              onClick="buttonEventQ()"><pc:shortcut key="q" script='<%="buttonEventQ()"%>'/>
              <input name="btnback" type="button" class="button" title="���ӹ�������(ALT+Z)" value="���ӹ�������(Z)" style="width:110"
              onClick="buttonEventZ()">
              <pc:shortcut key="z" script='<%="buttonEventZ()"%>'/>
             <%}--%>
             <%--if(!isEnd && !Type.equals("1")){%>
             <input type="hidden" name="selectReceive" value="" onchange="sumitForm(<%=receiveMaterialBean.IMPORT_RECEIVE_ADD%>)">
             <input name="btnback" type="button" class="button" title="�����ϵ�(ALT+W)" value="�����ϵ�(W)" style="width:80"
              onClick="buttonEventW()">
              <pc:shortcut key="w" script='<%="buttonEventW()"%>'/>
             <%}--%>
              <%if(!isEnd){%>
                <input type="hidden" name="scanValue" value="">
                <%--input type="button" class="button" title="�̵��(E)" value="�̵��(E)" style='width:65' onClick="buttonEventE(false)">
                <pc:shortcut key="e" script='<%="buttonEventE(false)"%>'/--%>
                 <%--input type="button" class="button" title="���̵��(R)" value="���̵��(R)" style='width:80' onClick="buttonEventE(true)">
                <pc:shortcut key="r" script='<%="buttonEventE(true)"%>'/--%>
                <input name="button2" type="button" class="button" title="ɾ������Ϊ����(ALT+X)" value="ɾ������Ϊ����(X)" style='width:120' onClick="sumitForm(<%=receiveMaterialBean.DELETE_BLANK%>);">
                <pc:shortcut key="x" script='<%="sumitForm("+receiveMaterialBean.DELETE_BLANK+")"%>'/>
                <input name="button2" type="button" class="button" title="�������(ALT+N)" value="�������(N)" style="width:80" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
                <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button"  title="����(ALT+S)" value="����(S)" style="width:50" onClick="sumitForm(<%=Operate.POST%>);">
              <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
            <%}%>
              <%if(isCanDelete && !receiveMaterialBean.isReport){%><input name="button3" type="button" class="button" title="ɾ��(ALT+D)" value="ɾ��(D)" onClick="buttonEventD();" style="width:50">
                <pc:shortcut key="d" script="buttonEventD()"/>
             <%}%>
              <%if(!receiveMaterialBean.isApprove && !receiveMaterialBean.isReport){%>
                <input name="btnback" type="button" class="button" title="����(ALT+C)" value="����(C)" onClick="backList();" style="width:50">
                <pc:shortcut key="c" script='<%="backList()"%>'/>
            <%}%>
               <%--03.09 11:43 ���� �����رհ�ť�ṩ������ҳ���Ǳ��������ʱʹ��. yjg--%>
                <%if(receiveMaterialBean.isReport){%><input name="btnback" type="button" class="button" title="�ر�(ALT+T)" value="�ر�(T)" onClick="window.close()" style="width:50">
                <pc:shortcut key="t" script='<%="window.close()"%>'/>
               <%}%>
                <%--03.13 14:49 ���� ������ӡ���ݰ�ť�������Ųɹ���ⵥҳ���ϵ����ݴ�ӡ����. yjg--%>
              <input type="button" class="button" title="��ӡ(ALT+P)" value="��ӡ(P)" onclick="buttonEventP()" style="width:50">
                <pc:shortcut key="p" script='<%="buttonEventP()"%>'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
  initDefaultTableRow('tableview1',1);
function onloadOn(){
   <%=SC_OUTSTORE_SHOW_ADD_FIELD.equals("1") ?
  receiveMaterialBean.adjustInputSize(new String[]{"cpbm","product", "jldw", "hsdw", "batchno", "drawnum", "drawbignum", "sxz", "memo","producenum","scydw"},  "form1", receiveMaterialBean.max-min+1, min)
  : receiveMaterialBean.adjustInputSize(new String[]{"cpbm","product", "jldw",  "hsdw", "batchno", "drawnum", "drawbignum","sxz", "memo","producenum","scydw"},  "form1", receiveMaterialBean.max-min+1, min)%>
}
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    min = <%=receiveMaterialBean.min%>;
    max = <%=receiveMaterialBean.max%>;
    newmax = <%=detailRows.length%> >= max+1 ?  max+1 : <%=detailRows.length%>;
  //ѡ����õ��Ƽ����ۣ�������Ƽ�����
  /*function getJjdjValue(i){
    getRowValue(document.all['prod_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'form1', 'srcVar=jjdj_'+i+'&srcVar=jjff_'+i, 'fieldVar=deje&fieldVar=jjff',eval('form1.v_gx_'+i+'.value') , 'jjdj_onchange('+i+',false)');
  }
  function getJjdjValue2(i){
   getRowValue(document.all['prodA_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'form1', 'srcVar=jjdj2_'+i+'&srcVar=jjff2_'+i, 'fieldVar=deje&fieldVar=jjff',eval('form1.v_gx2_'+i+'.value') , 'getjjgz2_onchange('+i+',false)');
  }
  function technicsChange(i){
    //���ӵĶ��󡣱�����Ķ�Ӧ��������
    var gylxid = eval('form1.gylxid_'+i+'.value');
    for(m=min;m<newmax; m++)
      {
      var linkObj = FindSelectObject("gylxid_"+m);
      if(linkObj == null)
        return;
      linkObj.SetSelectedKey(gylxid);
      linkObj.OnSelect();
    }
  }
  function technicsNameChange(i){
    //���ӵĶ��󡣱�����Ķ�Ӧ��������
    var gx = eval('form1.gx_'+i+'.value');
   for(y=min;y<newmax;y++)
     {
     var linkObj = FindSelectObject("gx_"+y);
     if(linkObj == null)
       return;
     linkObj.SetSelectedKey(gx);
     linkObj.OnSelect();
   }
  }
  function technicsNameChange2(i){
    //���ӵĶ��󡣱�����Ķ�Ӧ��������
    var gx2 = eval('form1.gx2_'+i+'.value');
    for(o=min;o<newmax;o++)
    {
      var linkObj = FindSelectObject("gx2_"+o);
      if(linkObj == null)
        return;
      linkObj.SetSelectedKey(gx2);
      linkObj.OnSelect();
    }
  }
    */
  function propertyChange(i){
    var sxzObj = document.all['sxz_'+i];
    var scdwgsObj = document.all['scdwgs_'+i];
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
    var scslObj = document.all['producenum_'+i];
    var hsblObj = document.all['hsbl_'+i];
    if(slObj.value=='' && scslObj.value=='')
      return;
    if(slObj.value!='')
      scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthValue));
    else if(slObj.value=='' && scslObj.value!=''){
      slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthValue)/parseFloat(scdwgsObj.value));
      if(hsblObj.value=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
         hsslObj.value = slObj.value;
      else
         hsslObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
    }
    sl_onchange(i,false);
  }
  function big_change(){
    if(<%=detailRows.length%><1)
      return;
    for(t=0; t<<%=detailRows.length%>; t++){
      sl_onchange(t,false);
    }
  }
  /*isShow = <%=SC_OUTSTORE_SHOW_ADD_FIELD.equals("1") ? true : false%>;
  function jjdj_onchange(i, isBigUnit){
    var sl = document.all['drawnum_'+i];
    var hssl = document.all['drawbignum_'+i];
    var scsl = document.all['producenum_'+i];
    var width = document.all['widths_'+i];//������ԵĿ��
    if(isShow){
      var jjdj = document.all['jjdj_'+i];
      var jjgz = document.all['jjgz_'+i];
      var sfc = document.all['jjff_'+i];//�Ƽ����۵ķ�����0=������λ����1=������λ����2=���㵥λ����3=���ϵ�������λ������(����ָϵͳ�����Ŀ��)
    if((sfc.value!='3' && sfc.value!='4') || sfc.value=='')
    {
      jjgz.value='';
    }
    if(sfc.value=="3" ){//�������Ե��۳��Կ��
      if(width.value!="" && width.value!="0" && !isNaN(width.value))
        jjgz.value = formatSum(parseFloat(sl.value) * parseFloat(jjdj.value)/parseFloat(width.value));
      else
        jjgz.value='';
    }
    if(sfc.value=="4")//�������Ե��۳��Կ��
      jjgz.value = formatSum(parseFloat(sl.value) * parseFloat(jjdj.value));
    //cal_tot('jjgz');
    }
  }
  */
  /*function getjjgz2_onchange(i,isBigUnit){
    var sl2 = document.all['drawnum_'+i];
    var hssl2 = document.all['drawbignum_'+i];
    var scsl2 = document.all['producenum_'+i];
    var width = document.all['widths_'+i];//������ԵĿ��
    if(isShow){
      var jjdj2 = document.all['jjdj2_'+i];
      var jjgz2 = document.all['jjgz2_'+i];
      var sfc2 = document.all['jjff2_'+i];//�Ƽ����۵ķ�����0=������λ����1=������λ����2=���㵥λ����3=���ϵ�������λ������(����ָϵͳ�����Ŀ��)
      if((sfc2.value!='3' && sfc2.value!='4') || sfc2.value=='')
      {
        jjgz2.value='';
      }
      if(sfc2.value=="3" ){//�������Ե��۳��Կ��
        if(width.value!="" && width.value!="0" && !isNaN(width.value))
          jjgz2.value = formatSum(parseFloat(sl2.value) * parseFloat(jjdj2.value)/parseFloat(width.value));
        else
          jjgz2.value='';
      }
      if(sfc2.value=="4")//�������Ե��۳��Կ��
        jjgz2.value = formatSum(parseFloat(sl2.value) * parseFloat(jjdj2.value));
      //cal_tot('jjgz2');
    }
  }*/
  function sl_onchange(i, isBigUnit)
  {
    var oldhsblObj = document.all['hsbl_'+i];
    var sxzObj = document.all['sxz_'+i];
    unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'newsl_onchange('+i+','+isBigUnit+')');
  }
  function newsl_onchange(i, isBigUnit)
  {
    var slObj = document.all['drawnum_'+i];
    var hsslObj = document.all['drawbignum_'+i];
    var scslObj = document.all['producenum_'+i];
    var width = document.all['widths_'+i];//������ԵĿ��
    var hsblObj = document.all['truebl_'+i];
    var obj = isBigUnit ? hsslObj : slObj;
    var widthObj = document.all['widths_'+i];//������ԵĿ��
    var showText = isBigUnit ? "����Ļ��������Ƿ�" : "����������Ƿ�";
    var showText2 = isBigUnit ? "����Ļ�������С����" : "���������С����";
    var changeObj = isBigUnit ? slObj : hsslObj;
    var scdwgsObj = document.all['scdwgs_'+i];//������ʽ
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
    //cal_tot('hssl');
    //if(isShow){
    //cal_tot('jjgz');
    //cal_tot('jjgz2');
    /*}

    if(scslObj.value!="" && '<%=KC_PRODUCE_UNIT_STYLE%>'!='1')
    return;
     if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
       scslObj.value= isBigUnit ? changeObj.value : slObj.value;
     else if(!isBigUnit)
       scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
     cal_tot('scsl');
        */
  }
  function producesl_onchange(i)
  {
    var oldhsblObj = document.all['hsbl_'+i];
    var sxzObj = document.all['sxz_'+i];
    unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'newproducesl_onchange('+i+')');
  }
  function newproducesl_onchange(i)
  {
    var slObj = document.all['drawnum_'+i];
    var hsslObj = document.all['drawbignum_'+i];
    var scslObj = document.all['producenum_'+i];
    var hsblObj = document.all['truebl_'+i];

    var scdwgsObj = document.all['scdwgs_'+i];//������ʽ
    var widthObj = document.all['widths_'+i];//������ԵĿ��
    if(scslObj.value=="")
      return;
    if(isNaN(scslObj.value))
    {
      alert('��������������Ƿ�');
      scslObj.focus();
      return;
    }
    /*if(scslObj.value<=0)
    {
      alert('�������������С����');
      scslObj.focus();
      return;
    }
    */
    if(slObj.value!="" && '<%=KC_PRODUCE_UNIT_STYLE%>'!='1'){//���������������Ƿ�ǿ��ת��
      return;
    }
    if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
      slObj.value= scslObj.value;
    else
      slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthObj.value)/parseFloat(scdwgsObj.value));
    cal_tot('scsl');
    cal_tot('sl');
    //cal_tot('hssl');
  }

  function cal_tot(type)
  {
    var tmpObj;
      var tot=0;
      for(i=0; i<<%=detailRows.length%>; i++)
      {
        if(type == 'sl'){
          tmpObj = document.all['drawnum_'+i];
        }
        else if(type == 'scsl')
          tmpObj = document.all['producenum_'+i];
        else if(type == 'hssl')
          tmpObj = document.all['drawbignum_'+i];
        else if(type == 'jjgz')
          tmpObj = document.all['jjgz_'+i];
        else if(type == 'jjgz2')
          tmpObj = document.all['jjgz2_'+i];
        else
          return;
        if(tmpObj.value!="" && !isNaN(tmpObj.value))
          tot += parseFloat(tmpObj.value);
      }
      if(type == 'sl'){
        document.all['t_sl'].value = formatQty(tot);
      }
      if(type == 'scsl')
        document.all['t_scsl'].value = formatQty(tot);
      if(type == 'hssl')
        document.all['t_hssl'].value = formatQty(tot);
      if(type == 'jjgz'){
        document.all['t_jjgz'].value = formatQty(tot);
        form1.zgf.value = formatSum(tot);
      }
      if(type == 'jjgz2'){
       document.all['t_jjgz2'].value = formatQty(tot);
       form1.zgf2.value = formatSum(tot);
      }
    }
    function ReceiveSelProcess(frmName,srcVar,fieldVar,curID,isout,storeid,methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
      var winName= "ReceiveSelProcess";
      paraStr = "../store_shengyu/draw_single_process.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar
             +"&deptid="+curID+"&isout="+isout+"&storeid="+storeid+"&drawType=<%=srlx%>";
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
    }
    function SelectProcessMaterail(frmName,srcVar,fieldVar,curID,isout,storeid,methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
      var winName= "ReceiveSelProcess";
      paraStr = "../store_shengyu/select_processmaterail.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar
              +"&deptid="+curID+"&isout="+isout+"&storeid="+storeid+"&drawType=<%=srlx%>";
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
    }
    function BackMaterail(frmName, srcVar, methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
      var winName= "GoodsProdSelector";
      paraStr = "../store/import_receiveproduct.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
    }
    /**
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
    */
    /**
     *@para frmName form���.ָʾ��srcVar�������Ǹ������.�ں���Ĳ�������Ƕ���������Ԫ�ؽ��в�����.
     *@para srcVar frmName���Ԫ��.��ֵ�ǿ�����һ���Ԫ��.��Ҫ���ú����Ĳ����еõ���ЩԪ��,Ȼ���ٸ���ҳ���е�srcVar��ֵ.��:��zl��sl_0
     *@para whichCall ָ�����ô�js�����ǵ�˭.�ڱ�ҳ����.ͼ����ú��������������.
     *@para i ָ��������.��Ҫ��Ҫ�������sl_i������srcVar
     *@para isAdjustKwid �Ƿ���Ҫ����kwid.��Ϊ�����۳��ⵥ�е�wzsxid��Ϊ�յ�ʱ��Ͳ������޸�kwid.��Ҫ������Ϊtrue.
     *@para methodName ��ҳ����ķ���.
     */
    function BatchMultiSelect(frmName, srcVar, whichCall, i, isAdjustKwid, methodName,notin)
    {
      //whichCall:������ʾ����������������ߵ�ͼ���¼������Ļ�������������¼�������.�������¼����Լ���Ӧ�Ĳ�ͬ���ദ��
      //������¼�whichCall:true��isMultiSelect=0.ͼ���¼�:false��isMultiSelect=1.
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
      var winName= "BatchSelector";
      //if ( !isAdjustKwid) alert("kk");
      var kwSrcVarCondition = isAdjustKwid?"&srcVarTwo=kwid_"+i:"";
      var kwFieldVarCondition = isAdjustKwid?"&fieldVar=kwid":"";
      paraStr = "../store/select_batch.jsp?operate="+(whichCall?58:0) + "&srcFrm="+frmName+"&"+srcVar+(whichCall?"&isMultiSelect=0":"&isMultiSelect=1")
              + "&srcVarTwo=drawnum_"+i
              + "&srcVarTwo=batchno_"+i
              + "&srcVarTwo=dmsxid_"+i
              + "&srcVarTwo=sxz_"+i
              + kwSrcVarCondition
              + "&fieldVar=zl&fieldVar=ph&fieldVar=dmsxid&fieldVar=sxz"
              + kwFieldVarCondition;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      var iframeObj = document.all['prod'];
      iframeObj.src = paraStr;
    }
    function transferScan(isNew)//�����̵��
    {
      var scanValueObj = form1.scanValue;
      scanValueObj.value = scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "self_gain_edit.jsp", "IT3CW32d.DLL")%>');//�õ�������Ʒ��������ŵ��ַ���
      //alert(scanValueObj.value);
      if(isNew)
        sumitForm(<%=receiveMaterialBean.NEW_TRANSFERSCAN%>);
      else
        sumitForm(<%=receiveMaterialBean.TRANSFERSCAN%>);
    }
    function buttonEventE(isNew)//���̵������
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
      if (form1.srlx.value=="" ){
        alert("����ָ���������");
        return;
      }
      ReceiveSelProcess('form1','srcVar=selectProcess','fieldVar=jgdid',form1.deptid.value,'0',form1.storeid.value, 'sumitForm(<%=receiveMaterialBean.RECEIVE_SEL_PROCESS%>)');
    }
    //���ӹ�������
    function buttonEventZ()
    {
      if(form1.deptid.value=='')
      {
        alert('��ѡ����');
        return;
      }
      if(form1.storeid.value=='')
      {
        alert('��ѡ��ֿ�');
        return;
      }
      if (form1.srlx.value=="" ){
        alert("����ָ���������");
        return;
      }
      SelectProcessMaterail('form1','srcVar=selectMaterail','fieldVar=jgdwlid',form1.deptid.value,'0',form1.storeid.value,'sumitForm(<%=receiveMaterialBean.SELECT_MATERAIL%>)');
    }
    //���ϵ������ϵ�
    function buttonEventW()
    {
      if(form1.storeid.value=="")
      {
        alert('��ѡ��ֿ�');
        return;
      }
      BackMaterail('form1','srcVar=selectReceive&storeid='+form1.storeid.value+'&isout=0');
    }
    //ɾ��
    function buttonEventD()
    {
      if(confirm('�Ƿ�ɾ���ü�¼��'))sumitForm(<%=Operate.DEL%>);
        }
  //��ӡ
    function buttonEventP()
    {
      location.href='../pub/pdfprint.jsp?code=receive_material_edit_bill&operate=<%=receiveMaterialBean.PRINT_BILL%>&a$sfdjid=<%=masterRow.get("drawid")%>&src=../store_shengyu/receive_material_edit.jsp';
    }
    //����
    function buttonEventA()
    {
      if(form1.storeid.value==''){alert('��ѡ��ֿ�');return;}sumitForm(<%=Operate.DETAIL_ADD%>);
        }

  function showDetail(masterRow){
    selectRow();
  }
</script>
<%if(receiveMaterialBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
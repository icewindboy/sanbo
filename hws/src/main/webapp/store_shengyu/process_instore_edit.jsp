<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.store.shengyu.B_SelfGain selfGainBean = engine.erp.store.shengyu.B_SelfGain.getInstance(request);
  selfGainBean.isout="1";
  String isout = "1";
  String pageCode = "process_instore_list";
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
<%--OBJECT id="scaner" classid="clsid:3FE58C97-FA6F-45AC-A983-0BD55A403FFA"
codebase="./ScanBarCodeProj.inf" width=0 height=0></OBJECT--%>
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
  location.href='process_instore_list.jsp';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=scydw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isbatchno_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=hsbl&fieldVar=scdwgs&fieldVar=isbatchno', obj.value, 'product_change('+i+')');
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=scydw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isbatchno_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=hsbl&fieldVar=scdwgs&fieldVar=isbatchno', obj.value, 'product_change('+i+')');
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
}
function deptChange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'handleperson', 'deptid', eval('form1.deptid.value'), '');
}
function corpCodeSelect(obj)
{
  ProcessCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpNameSelect(obj)
{
  ProcessNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
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
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//ͨ���ӹ�����ϸid�õ��ӹ�����
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//���ʹ������
  engine.project.LookUp produceInBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_PROCESS_IN);//�������
  engine.project.LookUp produceUseBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_USE);//��;
  //engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = selfGainBean.getMaterTable();
  EngineDataSet list = selfGainBean.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//�õ���½�û����۷�ʽ��ϵͳ����
  boolean isHandwork = loginBean.getSystemParam("KC_HANDIN_STOCK_BILL").equals("1");//�õ��Ƿ�����ֹ����ӵ�ϵͳ����
  boolean isHsbj = bjfs.equals("1");//�������1���Ի��㵥λ����
  HtmlTableProducer masterProducer = selfGainBean.masterProducerProcess;
  HtmlTableProducer detailProducer = selfGainBean.detailProducer;
  RowMap masterRow = selfGainBean.getMasterRowinfo();
  RowMap[] detailRows= selfGainBean.getDetailRowinfos();
  String zt=masterRow.get("state");
  String creatorID = masterRow.get("creatorID");//�õ��õ��ݵ��Ƶ�Աid
  String loginId = selfGainBean.loginId;
  if(selfGainBean.isApprove)
  {
    workShopBean.regData(ds,"deptid");
    storeBean.regData(ds, "storeid");
    produceInBean.regData(ds, "sfdjbid");
    corpBean.regData(ds,"dwtxid");
  }
  boolean isEnd = selfGainBean.isReport || selfGainBean.isApprove || (!selfGainBean.masterIsAdd() && !zt.equals("0"));//��ʾ�Ѿ���˻������
  boolean isCanDelete = !isEnd && !selfGainBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete)
                        && loginId.equals(creatorID);//û�н���,���޸�״̬,����ɾ��Ȩ��,2004-08-04 ���ҵ�½�˵����Ƶ���
  isEnd = isEnd
          || !(selfGainBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit))
          || !loginId.equals(creatorID);//2004-08-04 ���� ֻ�е�ǰ��½�����Ƶ��˵�ʱ��ſ����޸� yjg

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//�����û����Զ����ֶ�
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
            <td class="activeVTab">��ӹ���ⵥ(<%=title%>)
                        <%--03.19 20:32 ���� ����Ϊ���㲻�˳���ҳ��Ϳ���ֱ�Ӵ�ӡxxx_top.jsp���г��ĵ��ݶ��ӵ���һ��,��һ�ʰ�ť yjg--%>
              <%
              //����������ʱ����ʾ����һ����һ��
              if (isPriorNext)
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
                  corpBean.regData(ds,"dwtxid");
                  if(!isEnd){
                    storeAreaBean.regConditionData(ds, "storeid");
                    personBean.regConditionData(ds, "deptid");
                  }
                 %>
                  <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("receiveCode").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="receiveCode" value='<%=masterRow.get("receiveCode")%>' maxlength='<%=ds.getColumn("receiveCode").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("receiveDate").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="receiveDate" value='<%=masterRow.get("receiveDate")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="ѡ������" onclick="selectDate(form1.sfrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">����</td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:110" onSelect="deptChange()">
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
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
                   <%--<td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjlbid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+produceInBean.getLookupName(masterRow.get("sfdjlbid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="sfdjlbid" style="width:110">
                      <%=produceInBean.getList(masterRow.get("sfdjlbid"))%> </pc:select>
                    <%}%>
                  </td>--%>
                </tr>
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "if(form1.storeid.value!='"+masterRow.get("storeid")+"'){sumitForm("+selfGainBean.ONCHANGE+");}";%>
                    <%if(isEnd) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="1" style="width:110" onSelect="<%=sumit%>">
                      <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("handleperson").getFieldname()%></td>
                  <td  noWrap class="td"><%if(isEnd){%> <input type="text" name="handleperson" value='<%=masterRow.get("handleperson")%>' maxlength='<%=ds.getColumn("handleperson").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  <%}else {%>
                  <pc:select combox="1" className="edFocused" name="handleperson" value="<%=handleperson%>" style="width:110">
                  <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%></pc:select>
                  <%}%>
                  </td>
                  <%--<td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("ytid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+produceUseBean.getLookupName(masterRow.get("ytid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="ytid" addNull="1" style="width:110">
                      <%=produceUseBean.getList(masterRow.get("ytid"))%> </pc:select>
                    <%}%>
                  </td>--%>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("memo").getFieldname()%></td>
                  <td noWrap class="td" ><input type="text" name="memo" value='<%=masterRow.get("memo")%>' maxlength='<%=ds.getColumn("memo").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                  </tr>
                  <%--<tr>
                   <td  noWrap class="tdTitle">�ӹ���</td>
                   <%RowMap corpRow = corpBean.getLookupRow(masterRow.get("dwtxid"));%>
                  <td  noWrap class="td" colspan="3"><input type="text" name="dwdm" value='<%=corpRow.get("dwdm")%>' style="width:60" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpCodeSelect(this);" <%=readonly%>>
                  <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                  <input type="text" name="dwmc" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:170" <%=edClass%> onchange="corpNameSelect(this);" <%=readonly%>>
                  <%if(!isEnd){%>
                  <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProcessSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value);"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
                  <%}%>
                  </td>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
                  </tr>--%>
                <%/*��ӡ�û��Զ�����Ϣ*/
                int j=0;
                while(j < mBakFields.length){
                  out.print("<tr>");
                  for(int k=0; k<4; k++)
                  {
                    out.print("<td noWrap class='tdTitle'>");
                    out.print(j < mBakFields.length ? mBakFields[j].getFieldname() : "&nbsp;");
                    out.print("</td><td noWrap class='td'");
                    if(j < mBakFields.length)
                    {
                      boolean isMemo = mBakFields[j].getType() == FieldInfo.MEMO_TYPE;
                      out.print(isMemo ? " colspan=7>" : ">");
                      String filedcode = mBakFields[j].getFieldcode();
                      String style = (isMemo ? "style='width:690'" : "style='width:110'")+ " onKeyDown='return getNextElement();'";
                      out.print(masterProducer.getFieldInput(mBakFields[j], masterRow.get(filedcode), filedcode, style, isEnd, true));
                      out.print("</td>");
                      if(isMemo)
                        break;
                    }
                    else
                      out.print(">&nbsp;</td>");
                    j++;
                  }
                  out.println("</tr>");
                }
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                //2004-5-2 16:43 Ϊ����ϸ���ݼ������ҳ����
                String count = String.valueOf(list.getRowCount());
                int iPage = 30;
                String pageSize = String.valueOf(iPage);
                %>
                 <tr> <td colspan="8" noWrap class="td">
                   <pc:navigator id="process_instore_listNav" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+selfGainBean.TURNPAGE+")"%>' disable='<%=selfGainBean.isRepeat.equals("1")?"1":"0"%>'/>
                   </td></tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=15>
                        <input class="edFocused_r"  name="tCopyNumber" value="<%=request.getParameter("tCopyNumber")==null?"1":request.getParameter("tCopyNumber")%>"  size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                        </td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){//!isEnd false
                          %>
                          <input name="image" class="img" type="image" title="����(ALT+A)" onClick="buttonEventA()" src="../images/add_big.gif" border="0">
                           <pc:shortcut key="a" script="buttonEventA()"/>
                          <%}%>
                        </td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("jgdmxid").getFieldname()%></td>
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
                          <td nowrap><%=detailProducer.getFieldInfo("drawprice").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("drawsum").getFieldname()%></td>
                          <%--<td nowrap><%=detailProducer.getFieldInfo("producenum").getFieldname()%></td>
                          <td height='20' nowrap>������λ</td>
                          --%>
                          <td nowrap><%=detailProducer.getFieldInfo("kwid").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("memo").getFieldname()%></td>
                          <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      propertyBean.regData(list,"dmsxid");
                      processBean.regData(list,"jgdmxid");
                      BigDecimal t_sl = new BigDecimal(0), t_scsl = new BigDecimal(0), t_hssl = new BigDecimal(0),t_je=new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      //2004-5-2 16:43 Ϊ��ϸ����ҳ�����ҳ
                      int min = process_instore_listNav.getRowMin(request);
                      int max = process_instore_listNav.getRowMax(request);
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
                        String je = detail.get("drawSum");
                        if(selfGainBean.isDouble(je))
                          t_je = t_je.add(new BigDecimal(je));
                        String dmsxid = detail.get("dmsxid");
                        String sx = propertyBean.getLookupName(dmsxid);
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//ҳ�滻��������
                        String kwName = "kwid_"+i;
                        String dmsxidName = "dmsxid_"+i;
                        String jgdmxid=detail.get("jgdmxid");
                        boolean isimport = !jgdmxid.equals("");//����ӹ������ӱ��Ʒ���뵱ǰ�в����޸�
                    %>
                      <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                    <iframe id="prod_<%=i%>" src="" width="0" height=0 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd && !isimport){%>
                          <%--input type="hidden" name="mutibatch_<%=i%>" value="" onchange="sumitForm(<%=selfGainBean.MATCHING_BATCH%>,<%=i%>)">
                          <input name="image" class="img" type="image" title="������" onClick="if(form1.cpid_<%=i%>.value==''){alert('�������Ʒ');return;}if(form1.storeid.value==''){alert('�������Ʒ'); return;}BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+form1.cpid_<%=i%>.value+'&storeid='+form1.storeid.value+'&dmsxid='+form1.dmsxid_<%=i%>.value)" src="../images/edit.old.gif" border="0"--%>
                          <img style='cursor:hand' title='��ѡ����' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=isprops_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops','&storeid='+form1.storeid.value,'product_change(<%=i%>)')">
                          <%}if(!isEnd){%>
                          <input name="image" class="img" type="image" title="���Ƶ�ǰ��" onClick="if(form1.cpid_<%=i%>.value==''){alert('�������Ʒ');return;}sumitForm(<%=selfGainBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <input name="image" class="img" type="image" title="ɾ��" onClick="sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td><%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));%>
                        <td class="td" nowrap><%=processBean.getLookupName(detail.get("jgdmxid"))%></td>
                        <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                         <input type="hidden" name="isbatchno_<%=i%>" value="<%=prodRow.get("isbatchno")%>">
                        <input type="hidden" name="hsbl_<%=i%>" value="<%=prodRow.get("hsbl")%>">
                        <input type="hidden" name="scdwgs_<%=i%>" value="<%=prodRow.get("scdwgs")%>">
                        <input type="hidden" name="widths_<%=i%>" value="<%=widths%>">
                        <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                        <input type="text" <%=isimport ? "class=ednone" : detailClass%>  onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                         <td class="td" nowrap><input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                        <td class="td" nowrap>
                        <input <%=detailClass%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('���������Ʒ');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%> >
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('���������Ʒ');return;}if(form1.isprops_<%=i%>.value=='0'){alert('������û�й������');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="batchno_<%=i%>" name="batchno_<%=i%>" value='<%=detail.get("batchno")%>' maxlength='<%=list.getColumn("batchno").getPrecision()%>' onchange="BatchNoChange(<%=i%>)" <%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawnum_<%=i%>" name="drawnum_<%=i%>" value='<%=detail.get("drawnum")%>' maxlength='<%=list.getColumn("drawnum").getPrecision()%>' onblur="sl_onchange(<%=i%>, false)"<%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawbignum_<%=i%>" name="drawbignum_<%=i%>" value='<%=detail.get("drawbignum")%>' maxlength='<%=list.getColumn("drawbignum").getPrecision()%>' onblur="sl_onchange(<%=i%>, true)"<%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>

                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawprice_<%=i%>" name="drawprice_<%=i%>" value='<%=detail.get("drawprice")%>' maxlength='<%=list.getColumn("drawprice").getPrecision()%>' onblur="price_onchange(<%=i%>, false)"<%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="drawsum_<%=i%>" name="drawsum_<%=i%>" value='<%=detail.get("drawsum")%>' readonly></td>
                        <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="producenum_<%=i%>" name="producenum_<%=i%>" value='<%=detail.get("producenum")%>' maxlength='<%=list.getColumn("producenum").getPrecision()%>' onblur="producesl_onchange(<%=i%>)"<%=readonly%>>
                        <input type="hidden" class=ednone onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly></td>
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
                        <td class="td">&nbsp;</td>
                        <!--<td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        -->
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
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td align="right" class="td">
                          <input id="t_je" name="t_je" type="hidden" class="ednone_r" style="width:100%" value='<%=t_je%>' readonly>
                          <input id="t_scsl" name="t_scsl" type="hidden" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly>
                        </td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <!--<td class="td"></td>
                        <td class="td"></td>
                        <td align="right" class="td">&nbsp;</td>
                        --><td class="td">&nbsp;</td>
                        <td class="td"></td>
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
             <%if(!isEnd){%>
             <input type="hidden" name="importProcess" value="">
             <input name="btnback" type="button" class="button"  title="���ӹ���(ALT+Q)"style='width:85' value="���ӹ���(Q)"
             onClick="buttonEventQ()">
                <pc:shortcut key="q" script='<%="buttonEventQ()"%>'/>
              <%--<input type="hidden" name="scanValue" value="">
               <input type="button" class="button" title="�̵��(E)" value="�̵��(E)" style='width:70' onClick="buttonEventE()">
                <pc:shortcut key="e" script='<%="buttonEventE()"%>'/>
             --%>
              <input name="button2" type="button" class="button" title="ɾ������Ϊ����(ALT+X)" value="ɾ������Ϊ����(X)" style='width:120' onClick="sumitForm(<%=selfGainBean.DELETE_BLANK%>);">
                <pc:shortcut key="x" script='<%="sumitForm("+selfGainBean.DELETE_BLANK+")"%>'/>
               <input name="button2" type="button" class="button" title="�������(ALT+N)" value="�������(N)" style='width:80' onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
                <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="����(ALT+S)" value="����(S)" style='width:80' onClick="sumitForm(<%=Operate.POST%>);">
                <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%}%>
              <%if(isCanDelete && !selfGainBean.isReport){%><input name="button3" type="button" class="button" title="ɾ��(ALT+D)" style='width:60' value="ɾ��(D)" onClick="buttonEventD();">
                <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" ��ӡ "--%>
              <%if(!selfGainBean.isApprove && !selfGainBean.isReport){%><input name="btnback" type="button" class="button" title="����(ALT+C)" style='width:60' value="����(C)" onClick="backList();">
                <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
                <%--03.09 11:43 ���� �����رհ�ť�ṩ������ҳ���Ǳ��������ʱʹ��. yjg--%>
                <%if(selfGainBean.isReport){%><input name="btnback" type="button" class="button" title="�ر�(ALT+T)" value="�ر�(T)"  style='width:60' onClick="window.close()">
                <pc:shortcut key="t" script='<%="window.close()"%>'/>
               <%}%>
                <%--03.13 15:37 ���� ������ӡ���ݰ�ť�������Ųɹ���ⵥҳ���ϵ����ݴ�ӡ����. yjg--%>
              <input type="button" class="button" title="��ӡ(ALT+P)" value="��ӡ(P)" style='width:60' onclick="buttonEventP();">
                <pc:shortcut key="p" script='<%="buttonEventP()"%>'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
  function onload(){
    <%=selfGainBean.adjustInputSize(new String[]{"cpbm","product", "jldw", "batchno", "drawnum","drawprice","drawsum","sxz", "memo","drawbignum","producenum","hsdw", "scydw"},  "form1", selfGainBean.max-min+1, min)%>
  }
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
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
       var djObj = document.all['drawprice_'+i];
       var jeObj = document.all['drawsum_'+i];
       if(slObj.value=='' && scslObj.value=='')
         return;
       if(slObj.value!='')
         scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthValue));
       else if(slObj.value=='' && scslObj.value!=''){
         slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthValue)/parseFloat(scdwgsObj.value));
         if(djObj.value!='' && !isNaN(djObj.value))
           jeObj = formatQty(parseFloat(scslObj.value)*parseFloat(widthValue)*parseFloat(djObj.value)/parseFloat(scdwgsObj.value));
         if(hsblObj=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
            hsslObj.value = slobj.value;
         else
            hsslObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
       }
       cal_tot('sl');
       cal_tot('scsl');
       cal_tot('hssl');
       cal_tot('je');
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
       unitConvert(document.all['prod'], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'newsl_onchange('+i+','+isBigUnit+')');
     }
     function newsl_onchange(i, isBigUnit)
     {
       var slObj = document.all['drawnum_'+i];
       var hsslObj = document.all['drawbignum_'+i];
       var scslObj = document.all['producenum_'+i];
       var hsblObj = document.all['truebl_'+i];
       var djObj = document.all['drawprice_'+i];
       var jeObj = document.all['drawsum_'+i];
       var scdwgsObj = document.all['scdwgs_'+i];//������ʽ
       var obj = isBigUnit ? hsslObj : slObj;
       var widthObj = document.all['widths_'+i];//������ԵĿ��
       var showText = isBigUnit ? "����Ļ��������Ƿ�" : "����������Ƿ�";
       var showText2 = isBigUnit ? "����Ļ�������С����" : "���������С����";
       var changeObj = isBigUnit ? slObj : hsslObj;
        if(changeObj.value !="" && '<%=SC_STORE_UNIT_STYLE%>'!='1')//�Ƿ�ǿ��ת��
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
          return;//changeObj.value = obj.value;
        else
        {
          changeObj.value = formatQty(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
        }
        if(isBigUnit){
          if(djObj.vlaue!="" && !isNaN(djObj.vlaue))
            jeObj.value =formatQty(parseFloat(changeObj.value)*parseFloat(djObj.vlaue));
        }
        else{
          if(djObj.vlaue!="" && !isNaN(djObj.vlaue))
            jeObj.value =formatQty(parseFloat(slObj.value)*parseFloat(djObj.vlaue));
        }
        cal_tot('sl');
        cal_tot('hssl');
        cal_tot('je');
        if(scslObj.value!="" && '<%=KC_PRODUCE_UNIT_STYLE%>'!='1')
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
         var djObj = document.all['drawprice_'+i];
         var jeObj = document.all['drawsum_'+i];
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
           slObj.value= scslObj.value;
         else
           slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthObj.value)/parseFloat(scdwgsObj.value));
         if(djObj.value!="" && !isNaN(djObj.value))
           jeObj.value =formatQty(parseFloat(slObj.value)*parseFloat(djObj.vlaue));
         cal_tot('scsl');
         cal_tot('sl');
         cal_tot('je');
         if(hsslObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1')
           return;
         if(hsblObj=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
           hsslObj.value = slobj.value;
         else
          hsslObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
         cal_tot('hssl');
       }
       function price_onchange(i)
       {
         var slObj = document.all['drawnum_'+i];
         var hsslObj = document.all['drawbignum_'+i];
         var scslObj = document.all['producenum_'+i];
         var djObj = document.all['drawprice_'+i];
         var jeObj = document.all['drawsum_'+i];
         var hsblObj = document.all['hsbl_'+i];
         var scdwgsObj = document.all['scdwgs_'+i];//������ʽ
         var widthObj = document.all['widths_'+i];//������ԵĿ��
         if(djObj.value=="")
           return;
         if(isNaN(djObj.value))
         {
           alert('����ĵ��۷Ƿ�');
           obj.focus();
           return;
         }
         if(djObj.value<=0)
         {
           alert('����ĵ���С����');
           obj.focus();
           return;
         }
         if(djObj.value!="" && !isNaN(djObj.value) && slObj.value!="" && !isNaN(slObj.value))
           jeObj.value =formatSum(parseFloat(slObj.value)*parseFloat(djObj.value));
         cal_tot('je');
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
           else if(type == 'je')
             tmpObj = document.all['drawsum_'+i];
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
         if(type == 'je')
           document.all['t_je'].value = formatQty(tot);
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
  function ImportProcessSelect(frmName,srcVar,fieldVar,curID,isout,storeid, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
    var winName= "ImportProcessSelector";
    paraStr = "../store/import_process.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+curID+"&isout="+isout+"&storeid="+storeid;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function transferScan()//�����̵��
  {
    var scanValueObj = form1.scanValue;
    scanValueObj.value = scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "self_gain_edit.jsp", "IT3CW32d.DLL")%>');//�õ�������Ʒ��������ŵ��ַ���
    //alert(scanValueObj.value);
    sumitForm(<%=selfGainBean.TRANSFERSCAN%>);
  }
  function buttonEventE()
  {
    if(form1.storeid.value=='')
    {
      alert('��ѡ��ֿ�');return;
    }
    transferScan();
  }
  //���ӹ���
  function buttonEventQ()
  {
    if(form1.storeid.value=='')
    {
      alert('��ѡ��ֿ�');
      return;
    }
     ImportProcessSelect('form1','srcVar=importProcess','fieldVar=jgdid',form1.deptid.value,<%=isout%>,form1.storeid.value,'sumitForm(<%=selfGainBean.SELF_SEL_PROCESS%>)')
  }
  //ɾ��
  function buttonEventD()
  {
     if(confirm('�Ƿ�ɾ���ü�¼��'))sumitForm(<%=Operate.DEL%>);
  }
  function buttonEventP()
  {
   location.href='../pub/pdfprint.jsp?code=process_instore_edit_bill&operate=<%=selfGainBean.PRINT_BILL%>&a$sfdjid=<%=masterRow.get("receiveid")%>&src=../store/process_instore_edit.jsp'
  }

  function buttonEventA()
  {
      if(form1.storeid.value==''){alert('��ѡ��ֿ�');return;}sumitForm(<%=Operate.DETAIL_ADD%>);
  }
  function BatchNoChange(i){
   getRowNumberValue(document.all['prod_'+i], 'form1', 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i+'&srcVar=batchno_'+i+'&srcVar=drawnum_'+i+'&srcVar=producenum_'+i+'&srcVar=drawbignum_'+i, 'fieldVar=dmsxid&fieldVar=sxz&fieldVar=batno&fieldVar=salenum&fieldVar=producenum&fieldVar=pagenum', eval('form1.cpid_'+i+'.value'), eval('form1.dmsxid_'+i+'.value'), eval('form1.batchno_'+i+'.value'),'sl_onchange('+i+',false)');
 }
 /**
  * �õ��ϸ�֤������
  * iframeObj IFrame�Ķ���
  * lookup    Lookup Bean ������
  * frmName   ������
  * srcVar    ��������Ҫȡֵ�Ŀؼ������ַ�������:srcVar=dmsxid_0&srcVar=sxz_0
  * fieldVar  �������Ҫȡֵ�Ŀؼ��������Ӧ���ֶ������ַ�������:fieldVar=dmsxid&fieldVar=sxz
  * idVar     ��ǰҪ�õ���ID��ֵ����:idVar=1&idVar=2
  * methodName  �������ֵ���Ҫ���õķ�������
*/
 function getRowNumberValue(iframeObj, frmName, srcVar, fieldVar, cpidValue, dmsxidValue, Value, methodName)
 {
   if(srcVar+'' == 'undefined' || fieldVar+'' == 'undefined' || Value+'' == 'undefined')
     return;

   if(frmName+'' == 'undefined')
     frmName = '';
   if(methodName +'' =='undefined')
     methodName = '';
   var url = "../store/getCardValue.jsp?operate=2003&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&batchno="+Value +"&cpid="+cpidValue+"&dmsxid="+dmsxidValue+"&method="+methodName;
   iframeObj.src = url;
 }
 //ѡ������
function CardInfoOpen(){openSelectUrl("../store/getCardValue.jsp", "cardinfo", winopt2);}
  function showDetail(masterRow){
    selectRow();
  }
</script>
<%if(selfGainBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
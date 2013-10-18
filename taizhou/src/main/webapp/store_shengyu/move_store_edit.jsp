<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.store.shengyu.B_MoveStore moveStoreBean = engine.erp.store.shengyu.B_MoveStore.getInstance(request);
  moveStoreBean.djxz = 8;
  String pageCode = "move_store_list";
  //boolean hasApproveLimit = isApprove && loginBean.hasLimits(pageCode, op_approve);
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
  location.href='move_store_list.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<jsp:include page="../pub/scan_bar.jsp" flush="true"/>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<%String retu = moveStoreBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//���㷽ʽ
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//���ʹ������
  engine.project.LookUp moveBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_MOVE);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = moveStoreBean.getMaterTable();
  EngineDataSet list = moveStoreBean.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//�õ���½�û����۷�ʽ��ϵͳ����
  boolean isHsbj = bjfs.equals("1");//�������1���Ի��㵥λ����
  HtmlTableProducer masterProducer = moveStoreBean.masterProducer;
  HtmlTableProducer detailProducer = moveStoreBean.detailProducer;
  RowMap masterRow = moveStoreBean.getMasterRowinfo();
  RowMap[] detailRows= moveStoreBean.getDetailRowinfos();
  String zt=masterRow.get("zt");
  //2004-6-8 14:57 Ϊ����ϸ���ݼ������ҳ����
  String count = String.valueOf(list.getRowCount());
  int iPage = 20;
  String pageSize = String.valueOf(iPage);
  if(moveStoreBean.isApprove)
  {
    personBean.regData(ds, "personid");
    personBean.regData(ds, "deptid");
    personBean.regData(ds, "storeid");
    personBean.regData(ds, "kc__storeid");
  }
  boolean isEnd = moveStoreBean.isReport || moveStoreBean.isApprove || (!moveStoreBean.masterIsAdd() && !zt.equals("0"));//��ʾ�Ѿ���˻������
  boolean isCanDelete = !isEnd && !moveStoreBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//û�н���,���޸�״̬,����ɾ��Ȩ��
  isEnd = isEnd || !(moveStoreBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  //FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//�����û����Զ����ֶ�
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("�����"/* �����:"+ds.getValue("shr")*/) : (zt.equals("9") ? "������" : (zt.equals("2")?"����":"δ���"));
  String SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//������λ�͸���λ���㷽ʽ1=ǿ�ƻ���,0=����ֵʱ����
%>
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
            <td class="activeVTab">�ƿⵥ(<%=title%>)
            <%//����������ʱ����ʾ����һ����һ��
              if (!moveStoreBean.masterIsAdd())
              {
                ds.goToInternalRow(moveStoreBean.getMasterRow());
                boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
                if (!isAtFirst)
              {%>
              <a href="#" title="����һ��(ALT+Z)" onClick="sumitForm(<%=moveStoreBean.PRIOR%>)">&lt</a>
              <pc:shortcut key='z' script='<%="sumitForm("+moveStoreBean.PRIOR+")"%>'/>
             <%}
               if (!isAtLast)
              {%>
              <a href="#" title="����һ��(ALT+X)" onClick="sumitForm(<%=moveStoreBean.NEXT%>)">&gt</a>
              <pc:shortcut key='x' script='<%="sumitForm("+moveStoreBean.NEXT+")"%>'/>
             <%}
               }
            %>
            </td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%corpBean.regData(ds,"dwtxid");String jsr=masterRow.get("jsr");
                 if(!isEnd){
                   storeAreaBean.regConditionData(ds,"storeid");
                 }
                 %>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjdh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sfdjdh" value='<%=masterRow.get("sfdjdh")%>' maxlength='<%=ds.getColumn("sfdjdh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sfrq" value='<%=masterRow.get("sfrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="ѡ������" onclick="selectDate(form1.sfrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:110" onSelect="deptChange()"><%--2004-3-30 15:44 ���� �����������������ʹ�����˸������Լ���ͬ���Ŷ����js���� yjg--%>
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                   </td>
                   <%--<td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjlbid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+moveBean.getLookupName(masterRow.get("sfdjlbid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="sfdjlbid" addNull="0" style="width:110">
                      <%=moveBean.getList(masterRow.get("sfdjlbid"))%> </pc:select>
                    <%}%>
                  </td>
                   --%>
                </tr>
                <tr>
                  <%--<td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("ykdrdh").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="ykdrdh" value='<%=masterRow.get("ykdrdh")%>' maxlength='<%=ds.getColumn("ykdrdh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  --%>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="1" style="width:110" onSelect="compare();">
                      <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("kc__storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "if(form1.kc__storeid.value!='"+masterRow.get("kc__storeid")+"'){sumitForm("+moveStoreBean.ONCHANGE+");}";%>
                    <%if(isEnd) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("kc__storeid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="kc__storeid" addNull="1" style="width:110" onSelect="<%=sumit%>">
                      <%=storeBean.getList(masterRow.get("kc__storeid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsr").getFieldname()%></td>
                  <td class="td" nowrap>
                  <%if(isEnd){%> <input type="text" name="jsr" value='<%=masterRow.get("jsr")%>' maxlength='<%=ds.getColumn("jsr").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  <%}else {%>
                  <pc:select combox="1" className="edFocused" name="jsr" value="<%=jsr%>" style="width:110">
                  <%=personBean.getList()%></pc:select>
                  <%}%>
                  </td>
                  </tr>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bz").getFieldname()%></td>
                  <td noWrap class="td" colspan="3"><input type="text" name="bz" value='<%=masterRow.get("bz")%>' maxlength='<%=ds.getColumn("bz").getPrecision()%>' style="width:280" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
                  </tr>
                <%/*��ӡ�û��Զ�����Ϣ*/
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                %>
                <tr>
                  <td class="td" colspan="8" nowrap>
                   <pc:navigator id="move_store_listNav" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+moveStoreBean.TURNPAGE+")"%>' disable='<%=moveStoreBean.isRepeat.equals("1")?"1":"0"%>'/>
                 </td>
               </tr>
                <tr>
                  <td colspan="8" noWrap class="td">
                   <div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){%>
                          <input type="hidden" name="multiIdInput" value="" onchange="sumitForm(<%=Operate.DETAIL_ADD%>)">
                          <input name="image" class="img" type="image" title="����(ALT+A)" onClick="buttonEventA()" src="../images/add_big.gif" border="0">
                           <pc:shortcut key="a" script='<%="buttonEventA()"%>'/>
                          <%}%>
                        </td>
                         <%
                        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-2;i++)
                          out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
                        %>
                        <%--03.09 12:06 �޸� �����˱���еĹ������, ����,��������td������λ��. ����ľ�����ʾ��Щֵ��jsp scriptsҲ������Ӧ����. yjg--%>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td nowrap>������λ</td>
                        <td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
                        <td nowrap>���㵥λ</td>
                        <td nowrap>����</td>
                        <td nowrap>������λ</td>
                        <td nowrap>�����λ</td>
                        <td nowrap>��ע</td>
                      </tr>
                      <%
                       personBean.regConditionData(ds, "deptid");
                      // prodBean.regData(list,"cpid");
                      //propertyBean.regData(list,"dmsxid");
                      //buyOrderBean.regData(list,"hthwid");
                      int i=0;
                      RowMap detail = null;
                      int min = move_store_listNav.getRowMin(request);
                      int max = move_store_listNav.getRowMax(request);
                      //����ȡ�ñ�ÿһҳ�����ݷ�Χ
                      moveStoreBean.min = min;
                      moveStoreBean.max = max > detailRows.length-1 ? detailRows.length-1 : max;
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
                      BigDecimal t_sl = new BigDecimal(0),  t_hssl = new BigDecimal(0), t_drsl = new BigDecimal(0);;
                      list.goToRow(min);
                       for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(moveStoreBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String hssl = detail.get("hssl");
                        if(moveStoreBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String dckwName = "kwid_"+i;//������λ
                        String dckwMcName = "kwmc_"+i;//������λ
                        String drkwName = "kc__kwid_"+i;//�����λ
                        String drkwMcName = "kc__kwmc_"+i;//�����λ
                        String wzmxid = detail.get("wzmxid");
                        String dmsxidName = "dmsxid_"+i;
                    %>
                      <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                        <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd){%>
                      <input name="image" class="img" type="image" title="��ѡ����" src="../images/select_prod.gif" border="0"
                            onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=isprops_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops','&storeid='+form1.storeid.value,'product_change(<%=i%>)')">
                          <input name="image" class="img" type="image" title="ɾ��" onClick="sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                       <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                        String hsbl = prodRow.get("hsbl");
                        detail.put("hsbl",hsbl);
                        String isprops = prodRow.get("isprops");
                        %>
                        <td class="td" nowrap>
                        <input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="text" <%=detailClass%>   style="width:100%" onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=readonly%>>
                        <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'>
                        <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                        </td>
                        <td class="td" nowrap align="center">
                            <input type="text" <%=detailClass%>  style="width:100%"  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=readonly%>>
                        </td>
                        <%--02:14:50 ����һ��������� dmsxid td. Ϊ����һ��������Զ�������.��ΪҪ������cpid, ph�����ȷ��ֻΨһ��--%>
                        <td class="td" nowrap align="center">
                            <input type="HIDDEN"  id="dmsxid_<%=i%>"  name="dmsxid_<%=i%>"  value='<%=detail.get("dmsxid")%>'>
                            <input  <%=detailClass%>  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('���������Ʒ');return;} propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" <%=readonly%>>
                            <%if(!isEnd){%>
                            <img style='cursor:hand' src='../images/view.gif' title="�������" border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('���������Ʒ');return;}if(form1.isprops_<%=i%>.value=='0'){alert('�ò�Ʒ�޹������!');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>', form1.cpid_<%=i%>.value)">
                            <img style='cursor:hand' src='../images/delete.gif' title="ɾ��" border=0 onClick="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                            <%}%>
                        </td>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)"<%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone  onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone  onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                        <td class="td" nowrap align="center">
                          <input type="hidden" id="wzmxid_<%=i%>" name="wzmxid_<%=i%>" value="<%=detail.get("wzmxid")%>">
                          <input type="text" <%=detailClass%>  style="width:100%" onKeyDown="return getNextElement();" id="ph_<%=i%>" name="ph_<%=i%>" value='<%=detail.get("ph")%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('�������Ʒ');return;}BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+form1.cpid_<%=i%>.value+'&storeid='+form1.storeid.value+'&row='+<%=i%><%=detail.get("dmsxid").equals("")?"":"+'&sxz='+"+"'" + propertyBean.getLookupName(detail.get("dmsxid")) + "'"%><%="+'&ph='+"+"form1.ph_"+i+".value"%>, true, <%=i%>, <%=!wzmxid.equals("")?false:true%>, 'methodName=sl_onchange(<%=i%>, false)')" <%=readonly%>>
                        </td>
                        <td class="td" nowrap>
                          <%if(isEnd || !wzmxid.equals(""))
                            {
                             out.print("<input type='text' name="+ dckwMcName +" value='"+storeAreaBean.getLookupName(detail.get("kwid"))+"' class='ednone' readonly>");
                             out.print("<input type='hidden' name=" + dckwName + " value='"+detail.get("kwid") + "'>");
                            }
                           else {%>
                        <pc:select addNull="1" className="edFocused" name="<%=dckwName%>">
                        <%=storeAreaBean.getList(detail.get("kwid"), "storeid", masterRow.get("storeid"))%></pc:select>
                        <%}%>
                        <td class="td" nowrap>
                        <%if(isEnd)
                          {
                             out.print("<input type='text' name="+ drkwMcName +" value='"+storeAreaBean.getLookupName(detail.get("kc__kwid"))+"' class='ednone' readonly>");
                             out.print("<input type='hidden' name=" + drkwName + " value='"+detail.get("kc__storeid") + "'>");
                          }
                        else {%>
                        <pc:select addNull="1" className="edFocused" name="<%=drkwName%>">
                        <%=storeAreaBean.getList(detail.get("kc__kwid"), "storeid", masterRow.get("kc__storeid"))%></pc:select>
                        <%}%>
                        </td>

                        <td class="td" nowrap align="right"><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>'<%=readonly%>></td>
                      </tr>
                      <%list.next();
                      }
                      for(; i < 4; i++){
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
                        <%--03.09 11:43 �޸� ȥ�����涨��widths[]����ļ��д���,ͬʱ�޸�t_hssl, t_sl�Ŀ��Ϊwidth:100% yjg--%>
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                       <td class="td">&nbsp;</td>
                        <td class="td"></td>
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
            <td class="td"><b>�Ǽ�����:</b><%=masterRow.get("zdrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>�Ƶ���:</b><%=masterRow.get("zdr")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
              <%if(!isEnd){%>
              <input name="button2" type="button" class="button" title="�������(ALT+N)" value="�������(N)" style="width:75" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
                <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="����(ALT+S)" value="����(S)"  onClick="sumitForm(<%=Operate.POST%>);">
                <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%--<input name="button4" type="button" class="button" title="����һ��(ALT+W)" value="����һ��(W)" style="width:75" onClick="buttonEventW()">
              <pc:shortcut key="w" script='<%="buttonEventW()"%>'/>
              <input name="button3" type="button" class="button" title="ɾ��ʵ�ʿ����Ϊ����(ALT+R)" style="width:75" value="ɾ������(R)" onClick="sumitForm(<%=moveStoreBean.DELETE_BLANK%>);">
               <pc:shortcut key="r" script='<%="sumitForm("+moveStoreBean.DELETE_BLANK+")"%>'/>-%>
              <%--02.18 11:36 ���� �����̵����ť yjg--%>
               <%--06.09 11:27 ���� �����̵����ť yjg
               <input type="hidden" name="scanValue" value="">
               <input type="button" class="button" title="�̵��(E)" value="�̵��(E)" style="width:75" onClick="buttonEventE(false)">
              <pc:shortcut key="e" script='<%="buttonEventE(false)"%>'/>
               --%>
               <%}%>
              <%if(isCanDelete && !moveStoreBean.isReport){%><input name="button3" type="button" class="button" title="ɾ��(ALT+D)" value="ɾ��(D)"  onClick="buttonEventD();" value=" ɾ�� ">
                <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%if(!moveStoreBean.isApprove && !moveStoreBean.isReport){%>
               <input name="btnback" type="button" class="button" title="����(ALT+C)" value="����(C)" onClick="backList();">
                <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
              <%--03.09 11:43 ���� �����رհ�ť�ṩ������ҳ���Ǳ��������ʱʹ��. yjg--%>
              <%if(moveStoreBean.isReport){%>
                <input name="btnback" type="button" class="button" title="�ر�(ALT+T)" value="�ر�(T)" onClick="window.close()">
                  <pc:shortcut key="t" script='<%="window.close()"%>'/>
              <%}%>
              <%--03.15 10:28 ���� ������ӡ���ݰ�ť�������Ųɹ���ⵥҳ���ϵ����ݴ�ӡ����. yjg--%>
              <input type="button" class="button" title="��ӡ(ALT+P)" value="��ӡ(P)"  onclick="buttonEventP()">
                  <pc:shortcut key="p" script='<%="buttonEventP()"%>'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
initDefaultTableRow('tableview1',1);
<%=moveStoreBean.adjustInputSize(new String[]{"hssl","sl", "kwid", "bz", "cpbm", "sxz", "product", "jldw", "ph", "hsdw"},  "form1", (moveStoreBean.max-min+1), min)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function compare()
    {
      var storeid = form1.storeid;
      var kc__storeid = form1.kc__storeid;
      if(storeid.value == kc__storeid.value){
        alert('����ֿⲻ��������ֿ����');
        return;
      }
    }
    function totalCalSl()
    {
      for (i=<%=min%>;i<=<%=max%>;i++)
      {
        sl_onchange(i, false);
      }
    }
    function sl_onchange(i, isBigUnit)
    {
      var oldhsblObj = document.all['hsbl_'+i];
      var sxzObj = document.all['sxz_'+i];
      unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'nsl_onchange('+i+','+isBigUnit+')');
    }

    function nsl_onchange(i, isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      //var hsblObj = document.all['hsbl_'+i];
      var hsblObj = document.all['truebl_'+i];
      var hsslObj = document.all['hssl_'+i];
      //var drslObj = document.all['drsl_'+i];
      var obj = isBigUnit ? hsslObj : slObj;
      var showText = isBigUnit ? "����Ļ��������Ƿ�" : "����������Ƿ�";
      var showText0 = "����ĵ��������Ƿ�";
      var changeObj = isBigUnit ? slObj : hsslObj;
      if(obj.value=="")
        return;
      /*if(drslObj.value=="")
        return;
      if(isNaN(drslObj.value)){
        alert(showText0);
        obj.focus();
        return;

    } */
    /*if(isNaN(drslObj.value)){
        alert(showText);
        obj.focus();
        return;

    }*/
    //2004-3-30 15:27 �޸� javascript�޸�ҳ���Ͽ���������ֵ������,����������λ,
    //����,��������������ϵ��������������Ч:���޸����е�һ�������һ���ǿյĻ������ı�
    if(!(changeObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1'))//�Ƿ�ǿ��ת��
    {
      if(hsblObj.value!="" && !isNaN(hsblObj.value))
        changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
    }
    if ( isNaN( changeObj.value ) ) changeObj.value = "";
    cal_tot('sl');
    cal_tot('hssl');
    //cal_tot('drsl');
    }
    function cal_tot(type)
    {
      var tmpObj;
      var tot=0;
      for(i=<%=min%>;i<=<%=max%>;i++)
        {
        if(type == 'sl')
          tmpObj = document.all['sl_'+i];
        else if(type == 'hssl')
          tmpObj = document.all['hssl_'+i];
        //else if(type == 'drsl')
        //  tmpObj = document.all['drsl_'+i];
        else  return;
        if(tmpObj.value!="" && !isNaN(tmpObj.value))
          tot += parseFloat(tmpObj.value);
      }
      if(type == 'sl')
        document.all['t_sl'].value = formatQty(tot);
      else if(type == 'hssl')
        document.all['t_hssl'].value = formatQty(tot);
      //else if(type == 'drsl')
      //  document.all['t_drsl'].value = formatQty(tot);
    }
    function MaterailSIngleSelect(frmName, srcVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
    var winName= "MaterailSIngleSelect";
    paraStr = "../store/select_materail.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
//ɾ��
 function buttonEventD()
 {
   if(confirm('�Ƿ�ɾ���ü�¼��'))sumitForm(<%=Operate.DEL%>);
 }
 //��ӡ
 function buttonEventP()
 {
   location.href='../pub/pdfprint.jsp?code=move_store_edit_bill&operate=<%=moveStoreBean.PRINT_BILL%>&a$sfdjid=<%=masterRow.get("sfdjid")%>&src=../store_shengyu/move_store_edit.jsp';
 }
 //����
function buttonEventA()
{
  if(form1.storeid.value==''){alert('��ѡ������ֿ�'); return;}MaterailSIngleSelect('form1','srcVar=multiIdInput&storeid='+form1.storeid.value);
 }//����һ��
 function buttonEventW()
  {
    if(form1.storeid.value==''){alert('��ѡ��ֿ�');return;}
    sumitForm(<%=moveStoreBean.DETAIL_ADD_BLANK%>);
  }
 //�̵���¼�.
  function buttonEventE(isNew)//�¾��̵������
  {
    if(form1.storeid.value=='')
    {
      alert('��ѡ��ֿ�');return;
    }
    //2004-06-09 11:28�¾��̵������ yjg
    transferScan(isNew);
  }
      <%--02.18 11:34 ���� ���������̵㵥��js����. yjg--%>
  function transferScan(isNew)//�����̵��
 {

   var scanValueObj = form1.scanValue;
       scanValueObj.value = scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "store_check_edit.jsp", "IT3CW32d.DLL")%>');//�õ�������Ʒ��������ŵ��ַ���
       if(scanValueObj.value=='')
         return;
       //2004-4-23 11:32 �¾��̵������ yjg
       if(isNew)
         sumitForm(<%=moveStoreBean.NEW_TRANSFERSCAN%>);
       else
         sumitForm(<%=moveStoreBean.TRANSFERSCAN%>);
  }
 //2004-3-30 15:44 ���� �����������������ʹ�����˸������Լ���ͬ���Ŷ����js���� yjg
function deptChange()
{
  associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jsr', 'deptid', eval('form1.deptid.value'), '');
 }
 function productCodeSelect(obj, i)
 {
   ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                  'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value,'product_change('+i+')');
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value,'product_change('+i+')');
}
    <%--2004-4-23 14:24���� �������ڹ������ѡ���js���� yjg--%>
function propertyNameSelect(obj,cpid,i)
{
      PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                         'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
function product_change(i){
  document.all['dmsxid_'+i].value="";
  document.all['sxz_'+i].value="";
  //document.all['widths_'+i].value="";
  //associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '', true);
}
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
          + "&srcVarTwo=sl_"+i
          + "&srcVarTwo=ph_"+i
          + kwSrcVarCondition+"&srcVarTwo=dmsxid_"+i+"&srcVarTwo=sxz_"+i+"&srcVarTwo=wzmxid_"+i
          + "&fieldVar=zl&fieldVar=ph"
          + kwFieldVarCondition+"&fieldVar=dmsxid&fieldVar=sxz&fieldVar=wzmxid";
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  var iframeObj = document.all['prod'];
  iframeObj.src = paraStr;
  //newWin =window.open(paraStr,winName,winopt);
  //newWin.focus();
}
function batchSelectOpen(){openSelectUrl("../store/select_batch.jsp", "BatchSelector", winopt2);}
  function showDetail(masterRow){
    selectRow();
  }
</script>
<%if(moveStoreBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
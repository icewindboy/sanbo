<%--��ó���ⵥ�ӱ�--%>
<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
  String op_print  = "op_print";
%><%
  engine.erp.store.shengyu.B_OutSaleStore b_OutSaleStore = engine.erp.store.shengyu.B_OutSaleStore.getInstance(request);
  b_OutSaleStore.djxz = 13;
  String pageCode = "putoutlist";
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
  location.href='putoutlist.jsp';
}
function corpCodeSelect(obj)
{
  CustomerCodeChange('2',document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=b_OutSaleStore.ONCHANGE%>)');
}
function corpNameSelect(obj)
{
     CustomerNameChange('2', document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=b_OutSaleStore.ONCHANGE%>)');
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                         'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
function ladingprint(sfdjid)
{
    location.href='../pub/pdfprint.jsp?operate=<%=Operate.PRINT_PRECISION%>&code=yuz_outstore_print&sfdjid='+sfdjid;
}
</script>
<%String retu = b_OutSaleStore.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp saleOrderGoodsBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.VW_BEAN_XPORT_LADING_DETAIL);//������ó�ᵥ
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  engine.project.LookUp saleOutBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_OUT_SALE);//��������
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//���ʹ������
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_OutSaleStore.getMaterTable();
  EngineDataSet list = b_OutSaleStore.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//�õ���½�û����۷�ʽ��ϵͳ����
  boolean isHsbj = bjfs.equals("1");//�������1���Ի��㵥λ����
  HtmlTableProducer masterProducer = b_OutSaleStore.masterProducer;
  HtmlTableProducer detailProducer = b_OutSaleStore.detailProducer;
  RowMap masterRow = b_OutSaleStore.getMasterRowinfo();
  RowMap[] detailRows = b_OutSaleStore.getDetailRowinfos();
  String tempJustName = "";
  String zt=masterRow.get("zt");
  //2004-5-2 16:43 Ϊ����ϸ���ݼ������ҳ����
  String count = String.valueOf(list.getRowCount());
  int iPage = 20;
  String pageSize = String.valueOf(iPage);

  if(b_OutSaleStore.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  boolean isEnd = b_OutSaleStore.isReport || b_OutSaleStore.isApprove || (!b_OutSaleStore.masterIsAdd() && !zt.equals("0"));//��ʾ�Ѿ���˻������
  boolean isCanDelete = !isEnd && !b_OutSaleStore.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//û�н���,���޸�״̬,����ɾ��Ȩ��
  isEnd = isEnd || !(b_OutSaleStore.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//�����û����Զ����ֶ�
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("�����"/* �����:"+ds.getValue("shr")*/) : (zt.equals("9") ? "������" : (zt.equals("2")?"����":"δ���"));
  String SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//������λ�͸���λ���㷽ʽ1=ǿ�ƻ���,0=����ֵʱ����
  boolean isPriorNext = false;//saleOutStoreBean.isApprove?false:!saleOutStoreBean.masterIsAdd();

  boolean isCanPrint = loginBean.hasLimits(pageCode, op_print) ; //�Ƿ���Ȩ�޴�ӡ,ֻ���Ƶ��˲��д�ӡȨ��


%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<jsp:include page="../pub/scan_bar.jsp" flush="true"/>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="iprint" style="display:none"></iframe>
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
            <td class="activeVTab">��ó���ⵥ(<%=title%>)
            <%//����������ʱ����ʾ����һ����һ��
              if (isPriorNext)//!b_OutSaleStore.masterIsAdd()
              {
                ds.goToInternalRow(b_OutSaleStore.getMasterRow());
                boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
              %>
              <%if (!isAtFirst)
              {%>
              <a href="#" title="����һ��(ALT+Z)" onClick="sumitForm(<%=b_OutSaleStore.PRIOR%>)">&lt</a>
              <pc:shortcut key='z' script='<%="sumitForm("+b_OutSaleStore.PRIOR+")"%>'/>
             <%}%>
               <%if (!isAtLast)
              {%>
              <a href="#" title="����һ��(ALT+X)" onClick="sumitForm(<%=b_OutSaleStore.NEXT%>)">&gt</a>
              <pc:shortcut key='x' script='<%="sumitForm("+b_OutSaleStore.NEXT+")"%>'/>
             <%}
              }%>
   </td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%
                corpBean.regData(ds,"dwtxid");String jsr=masterRow.get("jsr");
                //saleOutBean.regConditionData(ds,"sfdjlbid");
                //storeBean.regData(ds, "storeid");
                  if(!isEnd)
                        storeAreaBean.regConditionData(ds, "storeid");
                 %>
                  <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjdh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sfdjdh" value='<%=masterRow.get("sfdjdh")%>' maxlength='<%=ds.getColumn("sfdjdh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sfrq" value='<%=masterRow.get("sfrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="ѡ������" onclick="selectDate(form1.sfrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "if(form1.storeid.value!='"+masterRow.get("storeid")+"')sumitForm("+b_OutSaleStore.ONCHANGE+")";%>
                    <%if(isEnd) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="0" onSelect="<%=sumit%>" style="width:110" >
                      <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
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
                </tr>
                <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwtxid").getFieldname()%></td>
                   <%
                     RowMap corpRow = corpBean.getLookupRow(masterRow.get("dwtxid"));
                     RowMap sfdjlbRow = saleOutBean.getLookupRow(masterRow.get("sfdjlbid"));
                     String srlx = sfdjlbRow.get("srlx");
                     srlx = srlx.equals("")?"1":srlx;
                     sfdjlbRow.put("srlx", srlx);
                   %>
                  <td  noWrap class="td" colspan="3"><input type="text" name="dwdm" value='<%=corpRow.get("dwdm")%>' style="width:60" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpCodeSelect(this);" <%=readonly%>>
                  <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <%--02.18 15:47 �޸� ������λ ѡ��ĳ� ��������ı���. ͬʱ������Ӧ�ڴ��ı����ڻس��Զ���������ģ����ѯ���ڵ��¼� yjg--%>
                  <input type="text" name="dwmc" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:170" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpNameSelect(this);" <%=readonly%>>
                  <%if(!isEnd){%>
                  <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=b_OutSaleStore.ONCHANGE%>)');"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';sumitForm(<%=b_OutSaleStore.ONCHANGE%>)">
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("khlx").getFieldname()%></td>
                  <td noWrap class="td">
                  <%String khlx=masterRow.get("khlx");%>
                  <%if(isEnd) out.print("<input type='text' value='"+masterRow.get("khlx")+"' style='width:50' class='edline' readonly>");
                  else {%>
                  <pc:select name="khlx" style="width:50" value='<%=khlx%>'>
                  <pc:option value='A'>A</pc:option>
                  <pc:option value='C'>C</pc:option>
                  </pc:select>
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsr").getFieldname()%></td>
                  <td class="td" nowrap>
                  <%if(isEnd){%> <input type="text" name="jsr" value='<%=masterRow.get("jsr")%>' maxlength='<%=ds.getColumn("jsr").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  <%}else {%><%--2004-4-23 21:36 �޸� ���۳��ⵥ������Ĭ��Ϊ�� yjg--%>
                  <pc:select combox="1" className="edFocused" name="jsr" value="<%=jsr%>" style="width:110">
                  <%=personBean.getList()%></pc:select>
                  <%}%>
                  </td>
                  </tr>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsfsid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+balanceBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="jsfsid" addNull="1" style="width:110">
                      <%=balanceBean.getList(masterRow.get("jsfsid"))%> </pc:select>
                    <%}%>
                  </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjlbid").getFieldname()%></td>
                   <td  noWrap class="td">
                    <%if(isEnd)
                     {
                      out.print("<input type='text' value='"+sfdjlbRow.get("lbmc")+"' style='width:110' class='edline' readonly>");
                      out.print("<input type='hidden' name='srlx' value="+sfdjlbRow.get("srlx")+">");
                     }
                    else {
                      String submit = "sumitForm("+b_OutSaleStore.ONCHANGE+")";
                      String aaa=masterRow.get("sfdjlbid");
                    %>
                    <pc:select name="sfdjlbid" addNull="0" style="width:110" onSelect="<%=submit%>">
                      <%=saleOutBean.getList(masterRow.get("sfdjlbid"))%> </pc:select>
                     <input type="hidden" name="srlx" value='<%=sfdjlbRow.get("srlx")%>'>
                    <%}%>
                  </td>

                  </tr>
                <%/*��ӡ�û��Զ�����Ϣ*/
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                %>
                <tr>
                  <td class="td" colspan="8" nowrap>
                   <pc:navigator id="putoutlistNav" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+b_OutSaleStore.TURNPAGE+")"%>' disable='<%=b_OutSaleStore.isRepeat.equals("1")?"1":"0"%>'/>
                 </td>
               </tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){%>
                          <input type="hidden" name="importLadingGoods" value="�����ᵥ����" onChange="sumitForm(<%=b_OutSaleStore.DETAIL_LADINGGOODS_ADD%>)">
                          <input type="hidden" name="signleimportLadingGoods" value="�����ᵥ����" onChange="sumitForm(<%=b_OutSaleStore.SINGLELADDING_DETAIL_LADINGGOODS_ADD%>)">
                          <input name="image" class="img" type="image" title="�����ᵥ����(ALT+A)" onClick="buttonEventA()" src="../images/add_big.gif" border="0">
                           <pc:shortcut key="a" script='buttonEventA()'/>
                           <input class="edFocused_r"  name="tCopyNumber" value="<%=request.getParameter("tCopyNumber")==null?"1":request.getParameter("tCopyNumber")%>" title="����(ALT+A)" size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                          <%}%>
                        </td>
                        <td nowrap>�ᵥ���</td>
                        <td nowrap>����</td>
                         <%
                        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-2;i++)
                          out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
                        %>
                         <td nowrap>�������</td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td nowrap>������λ</td>
                        <td nowrap><%=detailProducer.getFieldInfo("ph").getFieldname()%></td>

                        <%
                          if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
                          {
                        %>
                           <td nowrap>����</td>
                           <td nowrap>���</td>
                        <%
                          }
                        %>
                        <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                      </tr>
                    <%
                      personBean.regConditionData(ds, "deptid");
                      RowMap detail = null;
                      int i=0;
                      int min = putoutlistNav.getRowMin(request);
                      int max = putoutlistNav.getRowMax(request);
                      //����ȡ�ñ�ÿһҳ�����ݷ�Χ
                      b_OutSaleStore.min = min;
                      b_OutSaleStore.max = max > detailRows.length-1 ? detailRows.length-1 : max;
                      ArrayList cpidList = new ArrayList(max-min+1);
                      ArrayList dmsxidList = new ArrayList(max-min+1);
                      ArrayList wjidlist = new ArrayList(max-min+1);
                      for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        String cpid = detail.get("cpid");
                        String dmsxid = detail.get("dmsxid");
                        String wjid = detail.get("wjid");

                        cpidList.add(cpid);
                        wjidlist.add(wjid);
                        dmsxidList.add(dmsxid);
                      }
                      prodBean.regData((String[])cpidList.toArray(new String[cpidList.size()]));
                      propertyBean.regData((String[])dmsxidList.toArray(new String[dmsxidList.size()]));//02.15 ���� ����ע��dmsxid����id��Ϊ��ע������ҳ��ͻ���� yjg
                      saleOrderGoodsBean.regData((String[])wjidlist.toArray(new String[wjidlist.size()]));
                      //buyOrderBean.regData(list,"hthwid");
                      BigDecimal t_sl = new BigDecimal(0), t_je = new BigDecimal(0), t_hssl = new BigDecimal(0);
                      list.goToRow(min);
                     //2004-5-2 16:43 �޸� ��ԭ����i<detailRows.length�޸ĳ����ڵ�i<=max && i<list.getRowCount();
                      for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(b_OutSaleStore.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String hssl = detail.get("hssl");
                        if(b_OutSaleStore.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String je = detail.get("je");
                        if(b_OutSaleStore.isDouble(je))
                          t_je = t_je.add(new BigDecimal(je));
                        String wzmxid = detail.get("wzmxid");
                        String kwName = "kwid_"+i;
                         String kwMcName = "kwmc_"+i;
                        String kwid = detail.get("kwid");
                        String cpid = detail.get("cpid");
                    %>
                      <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                         <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd){%>
                          <input type="hidden" name="mutibatch_<%=i%>" value="" onchange="sumitForm(<%=b_OutSaleStore.SELECTBATCH%>,<%=i%>)">
                          <input name="image" class="img" type="image" title="������" onClick="BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+<%=detail.get("cpid")%>+'&storeid='+form1.storeid.value<%=detail.get("dmsxid").equals("")?"":"+'&sxz='+"+"'" + propertyBean.getLookupName(detail.get("dmsxid")) + "'"%>)" src="../images/select_prod.gif" border="0">
                          <input name="image" class="img" type="image" title="���Ƶ�ǰ��" onClick="sumitForm(<%=b_OutSaleStore.COPYROW%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <input name="image" class="img" type="image" title="ɾ��" onClick="sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td><%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                        String hsbl = prodRow.get("hsbl");
                        String isprop = prodRow.get("isprops");
                        detail.put("hsbl",hsbl);%>
                        <%RowMap saleOrderGoodsRow=saleOrderGoodsBean.getLookupRow(detail.get("wjid"));%>
                        <td class="td" nowrap><%=saleOrderGoodsRow.get("tdbh")%></td>
                        <td class="td" nowrap><input type="text" class="ednone"  onKeyDown="return getNextElement();" id="dh_<%=i%>" name="dh_<%=i%>" value='<%=detail.get("dh")%>' maxlength='<%=list.getColumn("dh").getPrecision()%>' readonly></td>
                        <td class="td" nowrap><%=prodRow.get("cpbm")%>
                         <input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                         <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'>
                         <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                        </td>
                        <td class="td" nowrap><%=prodRow.get("product")%>

                        </td>
                        <td class="td" nowrap>
                        <input <%=detailClass_r%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('���������Ʒ');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('���������Ʒ');return;}if(form1.isprops_<%=i%>=='0'){alert('������û�й������');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap><%=prodRow.get("jldw")%>
                          <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)" <%=readonly%>>
                          <input type="hidden" id="wzmxid_<%=i%>" name="wzmxid_<%=i%>" value="<%=detail.get("wzmxid")%>">
                        </td>
                         <td class="td" nowrap> <input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="ph_<%=i%>" name="ph_<%=i%>" value='<%=detail.get("ph")%>' style="width:100%"  onchange="BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+<%=detail.get("cpid")%>+'&storeid='+form1.storeid.value+'&row='+<%=i%><%=detail.get("dmsxid").equals("")?"":"+'&sxz='+"+"'" + propertyBean.getLookupName(detail.get("dmsxid")) + "'"%><%="+'&ph='+"+"form1.ph_"+i+".value"%>, true, <%=i%>, <%=!wzmxid.equals("")?false:true%>, 'methodName=sl_onchange(<%=i%>, false)')" <%=readonly%>>
                         </td>
                        <%
                          if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
                          {
                        %>
                           <td class="td" nowrap>
                              <input type="text" class="ednone"  onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' onchange="dj_onchange(<%=i%>)" maxlength='<%=list.getColumn("dj").getPrecision()%>' readonly>
                           </td>
                           <td class="td" nowrap align="right">
                             <input type="text" class="ednone_r"  id="je_<%=i%>" name="je_<%=i%>" value='<%=detail.get("je")%>' readonly>
                           </td>
                        <%
                          }
                        %>
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

                       <%
                          if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
                          {
                        %>
                          <td class="td"></td>
                          <td class="td">&nbsp;</td>
                        <%
                          }
                        %>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>С��</td>

                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>

                        <td class="td">&nbsp;</td>
                         <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r"    style="width:100%" value='<%=t_sl%>' readonly></td>
                         <td class="td">&nbsp;</td>
                         <td align="right" class="td">&nbsp;</td>
                         <td align="right" class="td"><input type="hidden" id="t_hssl" name="t_hssl" type="text" class="ednone_r"  style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <%
                          if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
                          {
                        %>
                          <td class="td"></td>
                          <td align="right" class="td">
                            <input id="t_je" name="t_je" type="text" class="ednone_r" value='<%=t_je%>'  style="width:100%" readonly>
                          </td>
                        <%
                          }
                        %>

                      </tr>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>�ϼ�</td>
                        <td class="td">&nbsp;</td>

                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                       <td class="td">&nbsp;</td>
                        <td align="right" class="td"><%=b_OutSaleStore.masterIsAdd()&&!b_OutSaleStore.isApprove?"0":ds.getValue("zsl")%></td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td">&nbsp;</td>
                        <td align="right" class="td"><%--=b_OutSaleStore.masterIsAdd()&&!b_OutSaleStore.isApprove?"0":b_OutSaleStore.totalDeatilHssl--%></td>
                        <%
                          if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
                          {
                        %>
                          <td class="td"></td>
                          <td align="right" class="td"></td>
                        <%
                          }
                        %>
                      </tr>
                    </table></div>
                    </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle">��ע</td><%--������Ϣ--%>
                  <td colspan="7" noWrap class="td"><textarea name="bz" rows="3" onKeyDown="return getNextElement();" style="width:690" <%=(zt.equals("9")||zt.equals("8"))?"readonly":""%> ><%=masterRow.get("bz")%></textarea></td>
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
              <%if(!isEnd){
                  String importTitle = srlx.equals("1")?"����ó�ᵥ(Q)":"����ó�˻���(Q)";
              %>
              <input type="hidden" name="singleSelectLadding" value=""><%--02.16 �޸� �� button��ʾ�����Ƹ�Ϊ ��������� yjg--%>
              <input name="btnback" type="button" class="button"  title="<%=importTitle%>" value="<%=importTitle%>" style="width:100" onClick="buttonEventQ()">
              <pc:shortcut key="q" script='<%="buttonEventQ()"%>'/>

              <input name="button2" type="button" class="button" title="�������(ALT+N)" value="�������(N)" style="width:75" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
              <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="����(ALT+S)" value="����(S)"  onClick="sumitForm(<%=Operate.POST%>);">
              <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
             <%}%>
              <%if(isCanDelete && !b_OutSaleStore.isReport){%><input name="button3" type="button" class="button" title="ɾ��(ALT+D)" value="ɾ��(D)" onClick="buttonEventD()">
              <pc:shortcut key="d" script="buttonEventD()"/>
             <%}%>
             <%--2004-3-27 11:32 ���� ����ɾ������Ϊ���еİ�ť yjg--%>
             <%if(!isEnd && !b_OutSaleStore.isReport){%>
             <input name="button3" type="button" class="button" title="ɾ������Ϊ����(ALT+R)" value="ɾ������(R)" style="width:75" onClick="buttonEventR()">
             <pc:shortcut key="r" script="buttonEventR()"/>
              <%}%>

              <%if(isCanPrint){%>
             <input name="btnback11"  style="width:50"  type="button" class="button" onClick="ladingprint('<%=masterRow.get("sfdjid")%>');" value="��ӡ(P)">
              <%}%>

              <%if(!b_OutSaleStore.isApprove && !b_OutSaleStore.isReport){%><input name="btnback" type="button" class="button" title="����(ALT+C)" value="����(C)"  onClick="backList();">
              <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
                <%if(b_OutSaleStore.isReport){%><input name="btnback" type="button" class="button" title="�ر�(ALT+T)" value="�ر�(T)"  onClick="window.close()">
                <pc:shortcut key="t" script='<%="window.close()"%>'/>
                <%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
  initDefaultTableRow('tableview1',1);
  <%--02.27 17:54 ���� ���� ��������jsp��������������ҳ�е���ϸ���ϲ��ֵ�input�ĳ���. yjg--%>
  <%=b_OutSaleStore.adjustInputSize(
    b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1")? new String[]{"hssl","sl", "bz", "je", "dj", "ph"}:new String[]{"hssl","sl", "bz", "ph"},
   "form1", (b_OutSaleStore.max-min+1), min)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
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
      var djObj = document.all['dj_'+i];
      var jeObj = document.all['je_'+i];
      var sxz = document.all['sxz_'+i];
      var obj = isBigUnit ? hsslObj : slObj;
      var showText = isBigUnit ? "����Ļ��������Ƿ�" : "����������Ƿ�";
      var changeObj = isBigUnit ? slObj : hsslObj;
      <%if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
        {
     %>
       if (!isNaN(slObj.value)&&!isNaN(djObj.value))
       {
         jeObj.value = formatSum(slObj.value*djObj.value);
       }
    <%}%>
      if(obj.value=="")
        return;
      if(isNaN(obj.value))
      {
        alert(showText);
        obj.focus();
        return;
      }
      //2004-3-30 15:27 �޸� javascript�޸�ҳ���Ͽ���������ֵ������,����������λ,
      //����,��������������ϵ��������������Ч:���޸����е�һ�������һ���ǿյĻ������ı�
      //���˲�������������������¶�Ҫȥ��.
     if(!(changeObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1'))//�Ƿ�ǿ��ת��.1=ǿ�ƻ���,0=����ֵʱ����
       {
       if(hsblObj.value!="" && !isNaN(hsblObj.value))
       {
         /*alert(hsblObj.value);
         alert(slObj.value);
         alert(hsslObj.value);
         alert(sxz.value);
         */
         changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
         //unitConvert(document.all['prod'], "form1", "srcVar=sl_"+i+"&srcVar=hssl_"+i, "exp=1122/{��}&exp=11*22/{��}", sxz.value, null);
       }
       //changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
       //else
       //  changeObj.value ="";

     }
     cal_tot('sl');
     cal_tot('hssl');
    <%if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
      {
   %>
     cal_tot('je');
   <%}%>

     }
     function dj_onchange(i)
     {
       var slObj = document.all['sl_'+i];
       var djObj = document.all['dj_'+i];
       var jeObj = document.all['je_'+i];
       if(djObj.value=="")
         return;
       if(isNaN(djObj.value))
       {
         alert("����ĵ��۷Ƿ�");
         djObj.focus();
         return;
       }
       if (!isNaN(slObj.value)&&!isNaN(djObj.value))
       {
         jeObj.value = formatSum(slObj.value*djObj.value);
       }
       cal_tot('je');
     }
     function cal_tot(type)
     {
       var tmpObj;
       var tot=0;
    for(i=<%=min%>; i<=<%=max%>;i++){
      if(type == 'sl')
        tmpObj = document.all['sl_'+i];
      else if(type == 'hssl')
        tmpObj = document.all['hssl_'+i];
      else if  (type=='je')
        tmpObj = document.all['je_'+i];
      else
        return;
      if(tmpObj.value!="" && !isNaN(tmpObj.value))
        tot += parseFloat(tmpObj.value);
    }
    if(type == 'sl')
      document.all['t_sl'].value = formatQty(tot);
    else if(type == 'hssl')
      document.all['t_hssl'].value = formatQty(tot);
    else if(type == 'je')
      document.all['t_je'].value = formatSum(tot);
   }
function LadingGoodsMultiSelect(frmName, srcVar, methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
  var winName= "GoodsProdSelector";
  paraStr = "../store/import_saleoutlading_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&djlx=<%=srlx%>";
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
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
function LaddingSingleSelect(frmName,srcVar,fieldVar,curID,storeid,methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
  var winName= "LaddingSingleSelect";
  paraStr = "../store/laddingout_single_list.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&multi=true"
          +"&dwtxid="+curID+"&storeid="+storeid+"&djlx=<%=srlx%>";
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  openSelectUrl(paraStr, "LaddingSingleSelect", winopt2);
  //newWin =window.open(paraStr,winName,winopt);
  //newWin.focus();

}
function certifyCardPrint(oper, row)
{
  var paraStr = "../store_shengyu/barcode_print.jsp?operate="+oper+"&rownum="+row;
  //openSelectUrl(paraStr, "CertifyCardPrint", winopt2);
  window.open(paraStr, "CertifyCardPrint");
  //document.all.iprint.src=paraStr;
}
function transferScan(isNew)//�����̵��
{
  //alert(scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "contract_instore_edit", "IT3CW32d.DLL")%>'));
  var scanValueObj = form1.scanValue;
   scanValueObj.value = scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "putoutlist_edit.jsp", "IT3CW32d.DLL")%>');//�õ�������Ʒ��������ŵ��ַ���
     if(scanValueObj.value=='')
       return;
     //2004-4-23 11:32 �¾��̵������ yjg
   if(isNew)
     sumitForm(<%=b_OutSaleStore.NEW_TRANSFERSCAN%>);
   else
     sumitForm(<%=b_OutSaleStore.TRANSFERSCAN%>);
}
//���������
function buttonEventQ()
{
  if (form1.srlx.value=="" ){
    alert("����ָ���������");
    return;
  }
  LaddingSingleSelect('form1','srcVar=singleSelectLadding&srcVar=signleimportLadingGoods','fieldVar=tdid',form1.dwtxid.value,form1.storeid.value,'sumitForm(<%=b_OutSaleStore.SINGLE_LADDING%>)');
}
     //�̵���¼�.
  function buttonEventE(isNew)//�¾��̵������
  {
    if(form1.storeid.value=='')
    {
      alert('��ѡ��ֿ�');return;
    }
    //2004-4-23 11:32 �¾��̵������ yjg
    transferScan(isNew);
  }
     //ɾ��
     function buttonEventD()
     {
   if(confirm('�Ƿ�ɾ���ü�¼��'))  sumitForm(<%=Operate.DEL%>);
     }
     //��ӡ
     function buttonEventP()
     {
    <%
      String billName = "";
    //���۳����Ƿ���ʾ���۽��.1=��ʾ,0=����ʾ
    if ( b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("0") )
      billName="putoutlist_edit_bill";
    else
      billName="putoutlist_edit_bill0";
    billName = "putoutlist_bill_wrapper_print";
     %>
    location.href='../pub/pdfprint.jsp?code=<%=billName%>&operate=<%=Operate.PRINT_PRECISION%>&sfdjid=<%=masterRow.get("sfdjid")%>';
     //location.href='putoutlist_bill_wrapper_print.jsp?operate=<%=b_OutSaleStore.WRAPPER_PRINT%>';
       }
       //ͼ�������¼�
       function buttonEventA()
       {
         if(form1.storeid.value==''){alert('��ѡ��ֿ�');return;}
         if(form1.dwtxid.value==''){alert('��ѡ�񹩻���λ');return;}
         if(form1.srlx.value==''){alert('��ѡ�񵥾�');return;}
         LadingGoodsMultiSelect('form1','srcVar=importLadingGoods&dwtxid='+form1.dwtxid.value+'&storeid='+form1.storeid.value);
       }
  //ɾ�������ǿհ׵���
  function buttonEventR()
  {
      sumitForm(<%=b_OutSaleStore.DELETE_BLANK%>)
  }
      //2004-3-30 15:44 ���� �����������������ʹ�����˸������Լ���ͬ���Ŷ����js���� yjg
  function deptChange()
  {
    associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jsr', 'deptid', eval('form1.deptid.value'), '');
  }
  function showDetail(masterRow){
    selectRow();
  }
</script>
<%if(b_OutSaleStore.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
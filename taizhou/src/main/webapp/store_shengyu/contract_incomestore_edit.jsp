<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.store.shengyu.B_StoreOutBill storeOutBillBean = engine.erp.store.shengyu.B_StoreOutBill.getInstance(request);
  storeOutBillBean.djxz = 14;
  String pageCode = "contract_incomestore_list";
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
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("������, ���Ժ�");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
//function showInterFrame(oper, rownum){
//    var url = "constract_instore_edit.jsp?operate="+oper+"&rownum="+rownum;
//    document.all.interframe.src = url;
    //showFrame('detailDiv',true,"",true);
//  }
function backList()
{
  location.href='contract_incomestore_list.jsp';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value,'product_change('+i+')');
}
function corpCodeSelect(obj)
{
  //02.18 15:47 CustomerCodeChange����ʹ�ò���1��ProvideCodeChange������������ͬ��.���ҹ�Ӧ�� yjg
  CustomerCodeChange('1',document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=storeOutBillBean.ONCHANGE%>)');
  //ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 //'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=storeOutBillBean.ONCHANGE%>)');
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value,'product_change('+i+')');
}
function corpNameSelect(obj)
{
     CustomerNameChange('1', document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=storeOutBillBean.ONCHANGE%>)');
}
<%--03.05 16:54 ���� �������ڹ������ѡ���js���� yjg--%>
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
</script>
<%String retu = storeOutBillBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp buyOrderGoodsBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_ORDER_STOCK);//�ɹ���ͬ
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//LookUp������λ
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//LookUp������Ϣ
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//LookUp��Ʒ��Ϣ
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);//LookUp��Ա��Ϣ
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);//LookUp�ֿ���Ϣ
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//���ʹ������
  engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//LookUp���㷽ʽ
  engine.project.LookUp buyInBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_IN_SALE);//LookUp��ó��ⵥ�������
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);//LookUp��λ��Ϣ
  engine.project.LookUp firstkindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_KIND);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = storeOutBillBean.getMaterTable();//�õ�����
  EngineDataSet list = storeOutBillBean.getDetailTable();//�õ��ӱ�
  HtmlTableProducer masterProducer = storeOutBillBean.masterProducer;
  HtmlTableProducer detailProducer = storeOutBillBean.detailProducer;
  RowMap masterRow = storeOutBillBean.getMasterRowinfo();//��������Ϣ
  RowMap[] detailRows= storeOutBillBean.getDetailRowinfos();//�ӱ�����Ϣ
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//�õ���½�û����۷�ʽ��ϵͳ����

  boolean isHandwork = loginBean.getSystemParam("KC_HANDIN_STOCK_BILL").equals("1");//�õ��Ƿ�����ֹ����ӵ�ϵͳ����
  boolean isShowPrice = loginBean.getSystemParam("KC_IN_SHOW_PRICE").equals("1");//�õ��Ƿ�����ֹ����ӵ�ϵͳ����
  boolean isshow = isShowPrice;

  //isHandwork = false;//03:23 21:59 ��Ϊ�ɹ���ⵥ�������ֶ�����.�����ڴ˴�д�������ֶ�����. yjg
  String deptid = masterRow.get("deptid");//�õ��õ��ݵ��Ƶ�����id
  String zdrid = masterRow.get("zdrid");//�õ��õ��ݵ��Ƶ�Աid
  boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, zdrid);//�жϵ�½Ա���Ƿ��в������Ƶ��˵��ݵ�Ȩ��
  String zt=masterRow.get("zt");
  if(storeOutBillBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  boolean isEnd = storeOutBillBean.isReport || storeOutBillBean.isApprove || (!storeOutBillBean.masterIsAdd() && !zt.equals("0"));//��ʾ�Ѿ���˻������
  boolean isCanDelete = !isEnd && !storeOutBillBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//û�н���,���޸�״̬,����ɾ��Ȩ��
  isEnd = isEnd || !(storeOutBillBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//�����û����Զ����ֶ�
  String edClass = (isEnd || !isHasDeptLimit) ? "class=edline" : "class=edbox";
  String detailClass = (isEnd || !isHasDeptLimit) ? "class=ednone" : "class=edFocused";
  String detailClass_r = (isEnd || !isHasDeptLimit) ? "class=ednone_r" : "class=edFocused_r";
  String readonly = (isEnd || !isHasDeptLimit) ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("�����"/* �����:"+ds.getValue("shr")*/) : (zt.equals("9") ? "������" : (zt.equals("2")?"����":"δ���"));
  boolean isAdd = storeOutBillBean.isDetailAdd;
  String SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//������λ�͸���λ���㷽ʽ1=ǿ�ƻ���,0=����ֵʱ����
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<%-- 2004-3-31 14:25 �޸� �̵������δ����滻Ϊ�������һ��include����һ���ļ�<OBJECT id="scaner" classid="clsid:3FE58C97-FA6F-45AC-A983-0BD55A403FFA"
codebase="./ScanBarCodeProj.inf" width=0 height=0></OBJECT>
--%>
<jsp:include page="../pub/scan_bar.jsp" flush="true"/>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="760" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="currentRow" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">��ó��ⵥ(<%=title%>)
            <%//����������ʱ����ʾ����һ����һ��
              if (!storeOutBillBean.masterIsAdd())
              {
              ds.goToInternalRow(storeOutBillBean.getMasterRow());
              boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
              %>
              <%if (!isAtFirst)
              {%>
              <a href="#" title="����һ��(ALT+Z)" onClick="sumitForm(<%=storeOutBillBean.PRIOR%>)">&lt</a>
              <pc:shortcut key='z' script='<%="sumitForm("+storeOutBillBean.PRIOR+")"%>'/>
             <%}%>
               <%if (!isAtLast)
              {%>
              <a href="#" title="����һ��(ALT+X)" onClick="sumitForm(<%=storeOutBillBean.NEXT%>)">&gt</a>
              <pc:shortcut key='x' script='<%="sumitForm("+storeOutBillBean.NEXT+")"%>'/>
             <%}
              }%>
            </td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%corpBean.regData(ds,"dwtxid");String jsr=masterRow.get("jsr");
                  if(!isEnd)
                    storeAreaBean.regConditionData(ds, "storeid");
                  RowMap sfdjlbRow = buyInBean.getLookupRow(masterRow.get("sfdjlbid"));
                  String srlx = sfdjlbRow.get("srlx");
                  srlx = srlx.equals("")?"1":srlx;
                  sfdjlbRow.put("srlx", srlx);
                %>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjdh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sfdjdh" value='<%=masterRow.get("sfdjdh")%>' maxlength='<%=ds.getColumn("sfdjdh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sfrq" value='<%=masterRow.get("sfrq")%>' maxlength='10' style="width:90" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd && isHasDeptLimit){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="ѡ������" onclick="selectDate(form1.sfrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "if(form1.storeid.value!='"+masterRow.get("storeid")+"'){sumitForm("+storeOutBillBean.STORE_ONCHANGE+");}";%>
                    <%if(isEnd || !isHasDeptLimit) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="1" style="width:110" onSelect="<%=sumit%>">
                      <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd || !isHasDeptLimit) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:110" onSelect="deptChange()"><%--2004-3-30 15:44 ���� �����������������ʹ�����˸������Լ���ͬ���Ŷ����js���� yjg--%>
                       <%=deptBean.getList(masterRow.get("deptid"))%>
                    </pc:select>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwtxid").getFieldname()%></td><%--������λ--%>
                <%RowMap corpRow = corpBean.getLookupRow(masterRow.get("dwtxid"));%>
                  <td  noWrap class="td" colspan="3"><input type="text" name="dwdm" value='<%=corpRow.get("dwdm")%>' style="width:60" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpCodeSelect(this);" <%=readonly%>>
                   <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>' >
                    <%--02.18 15:47 �޸� ����Ӧ�� ѡ��ĳ� ��������ı���. ͬʱ������Ӧ�ڴ��ı����ڻس��Զ���������ģ����ѯ���ڵ��¼� yjg--%>
                   <input type="text" name="dwmc" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:180" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpNameSelect(this);" <%=readonly%>>
                    <%if(!isEnd && isHasDeptLimit){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=storeOutBillBean.ONCHANGE%>)');">
                    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';sumitForm(<%=storeOutBillBean.ONCHANGE%>)">
                    <%}%>
                  </td>
                        <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("khlx").getFieldname()%></td>
                  <td noWrap class="td">
                  <%String khlx=masterRow.get("khlx");%>
                  <%if(isEnd || !isHasDeptLimit) out.print("<input type='text' value='"+masterRow.get("khlx")+"' style='width:50' class='edline' readonly>");
                  else {%>
                  <pc:select name="khlx" style="width:50" value='<%=khlx%>'>
                  <pc:option value='A'>A</pc:option>
                  <pc:option value='C'>C</pc:option>
                  </pc:select>
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
                      String submit = "sumitForm("+storeOutBillBean.ONCHANGE+")";
                    %>
                    <pc:select name="sfdjlbid" addNull="0" style="width:110" onSelect="<%=submit%>">
                      <%=buyInBean.getList(masterRow.get("sfdjlbid"))%> </pc:select>
                     <input type="hidden" name="srlx" value='<%=sfdjlbRow.get("srlx")%>'>
                    <%}%>
                  </td>
                  </tr>
                  <tr>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsfsid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd || !isHasDeptLimit) out.print("<input type='text' value='"+balanceBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:80' class='edline' readonly>");
                    else {%>
                    <pc:select name="jsfsid" addNull="1" style="width:80">
                      <%=balanceBean.getList(masterRow.get("jsfsid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsr").getFieldname()%></td>
                  <td class="td" nowrap>
                  <%if(isEnd || !isHasDeptLimit){%> <input type="text" name="jsr" value='<%=masterRow.get("jsr")%>' maxlength='<%=ds.getColumn("jsr").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  <%}else {%>
                  <pc:select combox="1" className="edFocused" name="jsr" value="<%=jsr%>" style="width:110">
                  <%=personBean.getList()%></pc:select>
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bz").getFieldname()%></td>
                  <td noWrap class="td" colspan="3"><input type="text" name="bz" value='<%=masterRow.get("bz")%>' maxlength='<%=ds.getColumn("bz").getPrecision()%>' style="width:290" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td>
                  </td>
                  <td></td>
                  <td></td>
                  <td></td>
                  </tr>
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
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="right" nowrap>
                         <%if(!isEnd && isHasDeptLimit){%>
                          <input class="edFocused_r"  name="tCopyNumber" value="<%=request.getParameter("tCopyNumber")==null?"1":request.getParameter("tCopyNumber")%>" title="����(ALT+A)" size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                          <%}%>
                          <%if(!isEnd && isHandwork && isHasDeptLimit){%>
                          <input name="image" class="img" type="image" title="����(ALT+A)" onClick="buttonEventA()" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script='buttonEventA()'/>
                          <%}%>
                        </td>
                        <%
                        for (int i=0;i<detailProducer.getFieldInfo("wjid").getShowFieldNames().length;i++)
                          out.println("<td nowrap>"+detailProducer.getFieldInfo("wjid").getShowFieldName(i)+"<//td>");
                        %>
                         <%
                        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-2;i++)
                          out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
                        %>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td nowrap>������λ</td>
                        <td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
                        <td nowrap>���㵥λ</td>
                         <%if(isshow){%>
                        <td nowrap>����</td>
                        <td nowrap>���</td>
                        <%}%>
                        <td nowrap><%=detailProducer.getFieldInfo("ph").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("kwid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      buyOrderGoodsBean.regData(list,"wjid");
                      propertyBean.regData(list,"dmsxid");
                      personBean.regConditionData(ds, "deptid");
                      BigDecimal t_sl = new BigDecimal(0), t_je = new BigDecimal(0), t_hssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++){
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(storeOutBillBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String hssl = detail.get("hssl");
                        if(storeOutBillBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String je = detail.get("je");
                        if(storeOutBillBean.isDouble(je))
                          t_je = t_je.add(new BigDecimal(je));
                        String kwName = "kwid_"+i;
                        String dmsxidName = "dmsxid_"+i;
                        String wjid=detail.get("wjid");
                        boolean isimport = !wjid.equals("");//����ɹ����������ӱ��Ʒ���뵱ǰ�в����޸�
                        String cpid = detail.get("cpid");
                        String Class = isimport  ? "class=ednone" : detailClass;//�ӱ�Classģʽ
                    %>
                      <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                        <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd && !isimport && isHandwork && isHasDeptLimit){%>
                          <input type="hidden" name="singleIdInput_<%=i%>" value="">
                          <input name="image" class="img" type="image" title="��ѡ����" src="../images/select_prod.gif" border="0"
                          onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=isprops_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops','&storeid='+form1.storeid.value,'product_change(<%=i%>)')">
                          <%}if(!isEnd && isHasDeptLimit){%>
                          <input name="image" class="img" type="image" title="���Ƶ�ǰ��" onClick="sumitForm(<%=storeOutBillBean.COPYROW%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <input name="image" class="img" type="image" title="ɾ��" onClick="sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td><%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                          String hsbl = prodRow.get("hsbl");
                          String isprops = prodRow.get("isprops");
                          detail.put("hsbl",hsbl);
                         %>
                        <% RowMap buyOrderGoodsRow=buyOrderGoodsBean.getLookupRow(detail.get("wjid"));%>
                        <td class="td" nowrap><%=buyOrderGoodsRow.get("jhdbm")%></td>
                        <td class="td" nowrap><%=buyOrderGoodsRow.get("htbh")%></td>
                         <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="text" <%=Class%>  onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>>
                        <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'>
                        <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''></td>
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                        </td>
                        <td class="td" nowrap><input type="text" <%=Class%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                        <%--03.05 18:21 ���� �޸Ĺ������Ϊ�������.��������onChangeʱ�ĺ���.yjg--%>
                        <td class="td" nowrap><input <%=detailClass%>  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>'
                           onchange="if(form1.cpid_<%=i%>.value==''){alert('���������Ʒ');return;} propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=(!isEnd) ? "": "readonly"%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('���������Ʒ');return;}if(form1.isprops_<%=i%>.value=='0'){alert('������û�й������');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value)">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class="ednone" id='jldw_<%=i%>' name='jldw_<%=i%>' value='<%=prodRow.get("jldw")%>' readonly></td>
                         <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" nowrap><input type='text' class="ednone" id='hsdw_<%=i%>' name='hsdw_<%=i%>' value='<%=prodRow.get("hsdw")%>' readonly>
                          <%if(!isshow){%>
                         <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' maxlength='<%=list.getColumn("dj").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=isHandwork?readonly:"readonly"%>></td>
                         <input type="hidden" class=ednone_r yDown="return getNextElement();" id="je_<%=i%>" name="je_<%=i%>" value='<%=detail.get("je")%>' maxlength='<%=list.getColumn("je").getPrecision()%>' readonly></td>
                        <%}%>
                         <%if(isshow){%>
                         <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' maxlength='<%=list.getColumn("dj").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=isHandwork?readonly:"readonly"%>></td>
                         <td class="td" nowrap><input type="text" class=ednone_r yDown="return getNextElement();" id="je_<%=i%>" name="je_<%=i%>" value='<%=detail.get("je")%>' maxlength='<%=list.getColumn("je").getPrecision()%>' readonly></td>
                        <%}%>
                        <td class="td" nowrap><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="ph_<%=i%>" name="ph_<%=i%>" value='<%=detail.get("ph")%>' maxlength='<%=list.getColumn("ph").getPrecision()%>'<%=readonly%>></td>
                        <td class="td" nowrap>
                        <%if(isEnd) out.print("<input type='text' name="+kwName+" value='"+storeAreaBean.getLookupName(detail.get("kwid"))+"' class='ednone' readonly>");
                        else {%>
                        <pc:select addNull="1" className="edFocused" name="<%=kwName%>">
                        <%=storeAreaBean.getList(detail.get("kwid"), "storeid", masterRow.get("storeid"))%></pc:select>
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
                        <td class="td">&nbsp;</td><%if(isshow){%><td class="td">&nbsp;</td><td class="td">&nbsp;</td><%}%>
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
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td class="td"></td>
                        <td class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td align="right" class="td"><%if(!isshow){%>
                        <input id="t_je" name="t_je" type="hidden" class="ednone_r" style="width:100%" value='<%=t_je%>' readonly>
                        <%}%>
                         </td>
                        <td class="td">&nbsp;</td>

                        <%if(isshow){%>
                          <td align="right" class="td"><input id="t_je" name="t_je" type="text" class="ednone_r" style="width:100%" value='<%=t_je%>' readonly>
                          </td>
                       <td class="td">&nbsp;</td>
                        <%}%>
                        <td class="td">&nbsp;</td>
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
            <td class="td"><b>�Ǽ�����:</b><%=masterRow.get("zdrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>�Ƶ���:</b><%=masterRow.get("zdr")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
              <%if(!isEnd && isHasDeptLimit){
                  //String importTitle = srlx.equals("1")?"���������(Q)":"�����˻���(Q)";
              %>
              <input type="hidden" name="singleOrderGoods" value="" >
              <%--03.18 14:28 ����һ�п�ʼ, ��������⼸��buttont�ϼ�������Ӧ�Ŀ�ݼ�.��:<pc:.../>�������ǩ.ͬʱ,button��onclick�¼�ΪbuttonEventX().yjg--%>
              <input name="btnback" type="button" class="button" title="������ó��(��)����(ALT+Q)"  value="������ó��(��)����(Q)" style="width:140" onClick="buttonEventQ()">
              <pc:shortcut key="q" script='<%="buttonEventQ()"%>'/>
              <input type="hidden" name="ImportIncomeOrderGoods" value="" onchange="sumitForm(<%=storeOutBillBean.DETAIL_ORDERGOODS_ADD%>)">
              <input name="btnback" type="button" class="button" title="������ó��(��)����(ALT+W)" value="������ó(��)��������(W)" style="width:155" onClick="buttonEventW()">
              <pc:shortcut key="w" script='buttonEventW()'/> <input type="hidden" name="scanValue" value="">
              <%--<input type="button" class="button" value="�̵��(E)" title="�̵��(ALT+E)" onClick="buttonEventE()">
              <pc:shortcut key="e" script="buttonEventE()"/>
              --%>
              <input name="button2" type="button" class="button" title="�������(ALT+N)" value="�������(N)" style="width:75" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" >
              <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="����(ALT+S)" value="����(S)" onClick="sumitForm(<%=Operate.POST%>);" >
              <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%}%>
              <%--2004-3-27 11:32 ���� ����ɾ������Ϊ���еİ�ť yjg--%>
              <%if(!isEnd && isHasDeptLimit && !storeOutBillBean.isReport){%>
              <input name="button3" type="button" class="button" title="ɾ������Ϊ����(ALT+R)" value="ɾ������(R)"  style="width:75" onClick="buttonEventR()">
              <pc:shortcut key="r" script="buttonEventR()"/>
              <%}%>
              <%--02.23 11:46 ���� ������ʾ�����⼸����ť�������м���isReport���� yjg--%>
              <%if(isCanDelete && isHasDeptLimit && !storeOutBillBean.isReport){%>
              <input name="button3" type="button" class="button" title="ɾ��(ALT+D)" onClick="buttonEventD()" value="ɾ��(D)">
              <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%if(!storeOutBillBean.isApprove && !storeOutBillBean.isReport){%>
              <input name="btnback" type="button" class="button" title="����(ALT+C)" onClick="backList();" value="����(C)">
              <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
              <%--03.08 21:14 ���� �����رհ�ť�ṩ������ҳ���Ǳ��������ʱʹ��. yjg--%>
              <%if(storeOutBillBean.isReport){%>
              <input name="btnback" type="button" class="button" title="�ر�(ALT+T)" value="�ر�(T)" onClick="window.close()" >
              <pc:shortcut key="t" script='<%="window.close()"%>'/>
              <%}%>
              <%--03.02 21:26 ���� ������ӡ���ݰ�ť�������Ųɹ���ⵥҳ���ϵ����ݴ�ӡ����. yjg--%>
              <input type="button" class="button" title="��ӡ(ALT+P)" value="��ӡ(P)" onclick="buttonEventP()">
              <pc:shortcut key="p" script='<%="buttonEventP()"%>'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=storeOutBillBean.adjustInputSize(new String[]{"cpbm", "product", "ph", "hssl", "sl", "sxz", "bz", "kwid", "jldw", "hsdw"},  "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
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
    var bjfs = <%=bjfs%>;
    var isHandwork = <%=loginBean.getSystemParam("KC_HANDIN_STOCK_BILL")%>;
    //isHandwork = 0;//03.24 15:59 �޸� (js)����ŵ��ݲ���Ҫ�ֶ������������ڴ˴�д��Ϊ�����ֶ�. yjg

    var obj = isBigUnit ? hsslObj : slObj;
    var showText = isBigUnit ? "����Ļ��������Ƿ�" : "����������Ƿ�";
    var changeObj = isBigUnit ? slObj : hsslObj;
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
    if(!(changeObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1'))//�Ƿ�ǿ��ת��
    {
      if(hsblObj.value!="" && !isNaN(hsblObj.value)){
        changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
      }
        //03.05 20:40 �޸� ע�͵����´���.��Ϊ,���汻ע�͵��Ĵ������ɻ�������������һ��ı�. yjg
        /*if(isBigUnit)
        hsslObj.value=changeObj.value;
        else
        slObj.value=changeObj.value;
        */
      }
    //03.05 21:30 �޸� ������˴�ԭ���Ĵ����if����:if ( isHandwork.valu == "1")
    //                ��Ϊ ���ڵ� if (isHandword == "1")
    if(isHandwork == "1"){
      if(isNaN(djObj.value))
      {
        alert('����ĵ��۷Ƿ�');
        return;
      }
      if(djObj.value=="")
        return;
      if(bjfs==1)
        jeObj.value=formatPrice(parseFloat(hsslObj.value)*parseFloat(djObj.value));
      if(bjfs==0)
        jeObj.value=formatPrice(parseFloat(slObj.value)*parseFloat(djObj.value));
      cal_tot('je');
    }
    cal_tot('sl');
    cal_tot('hssl');
  }
    function cal_tot(type)
    {
      var tmpObj;
      var tot=0;
      for(i=0; i<<%=detailRows.length%>; i++)
      {
        if(type == 'sl')
          tmpObj = document.all['sl_'+i];
        else if(type == 'hssl')
          tmpObj = document.all['hssl_'+i];
        else if(type == 'je')
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
        document.all['t_je'].value = formatQty(tot);
    }
  function OrderGoodsMultiSelect(frmName, srcVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "../store/import_incomegoods_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&djlx=<%=srlx.equals("1")?"2":"-2"%>";
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function OrderGoodsSingleSelect(frmName,srcVar,fieldVar,curID,storeid,methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "OrderSingleSelector";
    paraStr = "../store/ordergoods_income_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar
            +"&dwtxid="+curID+"&storeid="+storeid+"&djlx=<%=srlx.equals("1")?"2":"-2"%>";
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function transferScan()//�����̵��
  {
    //alert(scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "contract_instore_edit", "IT3CW32d.DLL")%>'));
    var scanValueObj = form1.scanValue;
    scanValueObj.value = scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "contract_instore_edit.jsp", "IT3CW32d.DLL")%>');//�õ�������Ʒ��������ŵ��ַ���
      if(scanValueObj.value=='')
        return;
    sumitForm(<%=storeOutBillBean.TRANSFERSCAN%>);
      }
      //03.18 10:42 ���� �����￪ʼ����������buttonEventX()����,��Ҫ���������ʱ�����ڸ���ҳ��ӿ�ݼ�.��ԭ����button��onclick�¼���Ĵ���ŵ��˴�����. yjg
      //���԰���ǰ��button���¼�,�������˳���.�ŵ������﹩����. yjg
      //��������ˣ�����(ALT+Q)
  function buttonEventQ()
  {
    if (form1.srlx.value=="" ){
    alert("����ָ���������");
    return;
  }
    if (form1.storeid.value=='')
    {
      alert('��ѡ��ֿ�');
      return;
    }
    OrderGoodsSingleSelect('form1','srcVar=singleOrderGoods','fieldVar=jhdid',form1.dwtxid.value,form1.storeid.value,'sumitForm(<%=storeOutBillBean.SINGLE_SELECT_ORDER%>)');
  }
      //��������ˣ�����(ALT+W)
   function buttonEventW()
   {
     if (form1.srlx.value=="" ){
       alert("����ָ���������");
       return;
     }
     if(form1.dwtxid.value=='')
     {
       alert('��ѡ�񹩻���λ');
       return;
     }
     if (form1.storeid.value=='')
     {
       alert('��ѡ��ֿ�');return;
     }
     OrderGoodsMultiSelect('form1','srcVar=ImportIncomeOrderGoods&dwtxid='+form1.dwtxid.value+'&storeid='+form1.storeid.value);
   }
  function buttonEventE()
  {
    if(form1.storeid.value=='')
    {
      alert('��ѡ��ֿ�');return;
    }
    transferScan();
  }
  //ɾ��
  function buttonEventD()
  {
    if(confirm('�Ƿ�ɾ���ü�¼��'))sumitForm(<%=Operate.DEL%>);
  }
      //��ӡ
  function buttonEventP()
  {
     location.href='../pub/pdfprint.jsp?code=constract_instore_edit_bill&operate=<%=storeOutBillBean.PRINT_BILL%>&a$sfdjid=<%=masterRow.get("sfdjid")%>&src=../store_shengyu/contract_instore_edit.jsp';
  }
       //�����հ���
  function buttonEventA()
  {
    if(form1.storeid.value=='')
    {
      alert('��ѡ��ֿ�');return;
    }
    sumitForm(<%=Operate.DETAIL_ADD%>)
   }
    //ɾ�������ǿհ׵���
  function buttonEventR()
  {
    sumitForm(<%=storeOutBillBean.DELETE_BLANK%>)
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
<%if(storeOutBillBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/>
<%}%>
<%
  out.print(retu);
%>
</BODY>
</Html>

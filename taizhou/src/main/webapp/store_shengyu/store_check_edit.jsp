<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.store.shengyu.B_StoreCheck storeCheckBean = engine.erp.store.shengyu.B_StoreCheck.getInstance(request);
  String pageCode = "store_check";
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
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">
var isBatAdd = false;
function sumitForm(oper, row)
{
    //02.25 12:06 ���� �����������жϲ��źͲֿⲻ��Ϊ�յ��ж�.��Ϊ��Ҫ��Ϊ����ʾ����yi������ʱ������javasript����.
    //�̵㵥�����������:showInterFrame(pdid, deptid, storeid, row).��deptid,��storeidΪ��ʱ�ͻ����.
    if (form1.deptid.value == "" || form1.storeid.value == "")
    {
      alert("���Ż�ֿⲻ��Ϊ��!");
      return;
    }
    if ( !checkDetails(oper) ) {return;}
    //��Ҫ����֤�ǲ����в�Ʒ����, ����, �����������ͬ��
    lockScreenToWait("������, ���Ժ�");
    form1.rownum.value = row;
    form1.operate.value = oper;
    form1.submit();
}
function backList()
{
  location.href='store_check.jsp';
}
function checkRadio(row){
  if(form1.sel.length + ''=='undefined')
    form1.sel.checked = !form1.sel.checked;
  else
    form1.sel[row].checked = !form1.sel[row].checked;
}
function submitAdd(row)
{
  lockScreenToWait("������, ���Ժ�");
  form1.operate.value = isBatAdd ? <%=storeCheckBean.DETAIL_BIG_ADD%> : <%=Operate.DETAIL_ADD%>;
  if (row+''!='undefined') form1.rownum.value = row; else form1.rownum.value=-1;
  form1.submit();
}
function showText(obj){
  if(obj=='2'){
   document.all.chlbtext.style.display = 'block';
   document.all.cpbmtext.style.display = 'none';
   document.all.selectKind.style.display = 'none';
   //document.all.dmsxidtext.style.display = 'none';
  }
  else if(obj=='3'){
    document.all.cpbmtext.style.display = 'block';
    document.all.chlbtext.style.display = 'none';
    document.all.selectKind.style.display = 'none';
    //document.all.dmsxidtext.style.display = 'none';
  }
}
function hideText(){
    document.all.cpbmtext.style.display = 'none';
    document.all.chlbtext.style.display = 'none';
    document.all.selectKind.style.display = 'none';
    //document.all.dmsxidtext.style.display = 'none';
}
function showFixedQuery(isBat){
  isBatAdd = isBat;
  showFrame('fixedQuery', true, "", true);
  document.all.chlbtext.style.display = 'none';
  document.all.cpbmtext.style.display = 'none';
  document.all.selectKind.style.display = 'none';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value,'product_change('+i+')');
}
function prodchange(i){
  document.all['zcsl_'+i].value="0";
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value,'product_change('+i+')');
}
//02.20 18:21 ������������Ϊ ������ �鿴���������絥��ͼ�� ����.ͬʱ��Ӧ��js��DIVҲ�������� yjg
function showInterFrame(pdid, deptid,storeid,row)
{
     var url = "../store_shengyu/check_make_destroy.jsp?operate=0&pdid="+pdid+"&deptid="+deptid+"&storeid="+storeid+"&rownum="+row;
     document.location.href = url;
     //showFrame('detailDiv',true,"",true);
}
function hideFrameNoFresh()
{
  hideFrame('detailDiv');
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
  form1.cpbm_a.value=eval("form1.cpbm_"+i+".value");
  form1.cpbm_b.value=eval("form1.cpbm_"+i+".value");
  for (j=0;j<form1.scope.length;j++)
  {
    if (form1.scope[j].value=='3')
    {
      form1.scope[j].checked=true;
    }
  }
  submitAdd(i);
  //document.all['widths_'+i].value="";
  //associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '', true);
}
</script>
<%String retu = storeCheckBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp stockKindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STOCKS_KIND);//������
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//LookUp������Ϣ
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);//LookUp��Ա��Ϣ
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);//LookUp�ֿ���Ϣ
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//���ʹ������
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = storeCheckBean.getMaterTable();//�õ�����
  EngineDataSet list = storeCheckBean.getDetailTable();//�õ��ӱ�
  HtmlTableProducer masterProducer = storeCheckBean.masterProducer;
  HtmlTableProducer detailProducer = storeCheckBean.detailProducer;
  RowMap masterRow = storeCheckBean.getMasterRowinfo();//��������Ϣ
  RowMap[] detailRows= storeCheckBean.getDetailRowinfos();//�ӱ�����Ϣ
  String zt=masterRow.get("zt");
  String zdrid = masterRow.get("zdrid");//�õ��õ��ݵ��Ƶ�Աid
  String loginId = storeCheckBean.loginId;
  //2004-5-2 16:43 Ϊ����ϸ���ݼ������ҳ����
  String count = String.valueOf(list.getRowCount());
  int iPage = 30;
  String pageSize = String.valueOf(iPage);

  if(storeCheckBean.isApprove)
  {
    storeBean.regData(ds, "storeid");
    deptBean.regData(ds, "deptid");
  }
  boolean isEnd = storeCheckBean.isApprove || (!storeCheckBean.masterIsAdd() && !zt.equals("0"));//��ʾ�Ѿ���˻������
  boolean isCanDelete = !isEnd && !storeCheckBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete)
                        && loginId.equals(zdrid);//û�н���,���޸�״̬,����ɾ��Ȩ��,2004-08-04 ���ҵ�½�˵����Ƶ���
  isEnd = isEnd
          || !(storeCheckBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit))
          || !loginId.equals(zdrid);//2004-08-04 ���� ֻ�е�ǰ��½�����Ƶ��˵�ʱ��ſ����޸� yjg

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//�����û����Զ����ֶ�
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("�����"/* �����:"+ds.getValue("shr")*/) : (zt.equals("9") ? "������" : "δ���");
  boolean isAdd = storeCheckBean.isDetailAdd;
  String SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//������λ�͸���λ���㷽ʽ1=ǿ�ƻ���,0=����ֵʱ����
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<jsp:include page="../pub/scan_bar.jsp" flush="true"/>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="760" BORDER=0 CELLSPACING=0 CELLPADDING=0>
    <tr>
      <td align="center" height="5"></td>
    </tr>
  </table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="old_scsl" value="">
  <INPUT TYPE="HIDDEN" NAME="old_hssl" value="">
  <INPUT TYPE="HIDDEN" NAME="old_ce" value="">
  <INPUT TYPE="HIDDEN" NAME="beginRNo" value="">
  <INPUT TYPE="HIDDEN" NAME="endRNo" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0>
          <tr>
            <td class="activeVTab">����̵㵥(<%=title%>)
              <%
              //����������ʱ����ʾ����һ����һ��
              if (!storeCheckBean.masterIsAdd())
              {
                ds.goToInternalRow(storeCheckBean.getMasterRow());
                boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
              %>
              <%if (!isAtFirst)
              {%>
              <a href="#" title="����һ��(ALT+Z)" onClick="sumitForm(<%=storeCheckBean.PRIOR%>)">&lt</a>
              <pc:shortcut key='z' script='<%="sumitForm("+storeCheckBean.PRIOR+")"%>'/>
             <%}%>
               <%if (!isAtLast)
              {%>
              <a href="#" title="����һ��(ALT+X)" onClick="sumitForm(<%=storeCheckBean.NEXT%>)">&gt</a>
              <pc:shortcut key='x' script='<%="sumitForm("+storeCheckBean.NEXT+")"%>'/>
             <%}//
              }%>
            </td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td> <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%storeBean.regData(ds,"storeid");
                  deptBean.regData(ds,"deptid");
                  String pdr=masterRow.get("pdr");
                  storeAreaBean.regConditionData(ds, "storeid");
                  String sumit = "if(form1.storeid.value !='"+masterRow.get("storeid")+"'){sumitForm("+storeCheckBean.STORECHANGE+");}";
                %>
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("pdhm").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="pdhm" value='<%=masterRow.get("pdhm")%>' maxlength='<%=ds.getColumn("pdhm").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("rq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="rq" value='<%=masterRow.get("rq")%>' maxlength='10' style="width:90" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="ѡ������" onclick="selectDate(form1.sfrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) {
                      out.print("<input name='storeidName' type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                      out.print("<input name='storeid' type='hidden' value='"+masterRow.get("storeid")+"'>");
                    }else {%>
                    <pc:select name="storeid" addNull="1" style="width:110" onSelect="<%=sumit%>">
                    <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd){
                      out.print("<input  type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                      out.print("<input  name='deptid' type='hidden' value='"+masterRow.get("deptid")+"' readonly>");
                    }else {%>
                    <pc:select  name="deptid" addNull="1" style="width:110" onSelect="deptChange()"><%--2004-3-30 15:44 ���� �����������������ʹ�����˸������Լ���ͬ���Ŷ����js���� yjg--%>
                       <%=deptBean.getList(masterRow.get("deptid"))%>
                    </pc:select>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("pdr").getFieldname()%></td>
                  <td class="td" nowrap>
                    <%if(isEnd){%>
                    <input type="text"  value='<%=masterRow.get("pdr")%>' maxlength='<%=ds.getColumn("pdr").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                    <%}else {%>
                    <pc:select combox="1" className="edFocused" name="pdr" value="<%=pdr%>" style="width:110">
                    <%=personBean.getList()%></pc:select>
                    <%}%>
                  </td>
                  <td></td>
                  <td></td>
                  <td></td>
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
                  <td class="td" colspan="8" nowrap>
                   <pc:navigator id="store_checkNav" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+storeCheckBean.TURNPAGE+")"%>'/>
                 </td>
               </tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                      <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                        <tr class="tableTitle">
                          <td nowrap width=10></td>
                          <td height='20' align="center" nowrap>
                            <%if(!isEnd){%>
                            <input name="image" class="img" type="image" title="���(ALT+A)" onClick="showFixedQuery(false);" src="../images/add_big.gif" border="0">
                             <input class="edFocused_r"  name="tCopyNumber" value="<%=request.getParameter("tCopyNumber")==null?"1":request.getParameter("tCopyNumber")%>" size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                             <pc:shortcut key="a" script='<%="showFixedQuery(false)"%>'/>
                            <%}%>
                          </td>
                          <td nowrap>��Ʒ����</td>
                          <td nowrap>Ʒ����񣨻��ſ�ʽ��</td>
                          <td nowrap>�������</td>
                          <td nowrap>���Կ������</td>
                          <td nowrap>ʵ�ʿ������</td>
                          <td nowrap>������λ</td>
                          <td nowrap>���Կ�滻������</td>
                          <td nowrap>ʵ�ʿ�滻������</td>
                          <td nowrap>���㵥λ</td>
                          <%--02.19 20:36 �޸��ʴ�����, ������� ��Ϊ���ڵ����Ƶ��Կ������  ʵ�ʿ������ yjg --%>
                          <td nowrap>����</td>
                          <td nowrap>��λ</td>
                          <td nowrap>ӯ��</td>
                          <td nowrap>���</td>
                        </tr>
                        <%
                      personBean.regConditionData(ds, "deptid");
                      BigDecimal t_zcsl = new BigDecimal(0),t_scsl = new BigDecimal(0), t_ce = new BigDecimal(0),t_hssl = new BigDecimal(0), t_zchssl = new BigDecimal(0);
                      double d_ce = 0;
                      String ce = null;
                      String isLoss = null;
                      int i=0;
                      RowMap detail = null;
                      //2004-5-2 16:43 Ϊ��ϸ����ҳ�����ҳ
                      int min = store_checkNav.getRowMin(request);
                      int max = store_checkNav.getRowMax(request);
                      //����ȡ�ñ�ÿһҳ�����ݷ�Χ
                      storeCheckBean.min = min;
                      storeCheckBean.max = max > detailRows.length-1 ? detailRows.length-1 : max;
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
                        String zcsl = detail.get("zcsl");
                        if(storeCheckBean.isDouble(zcsl))
                          t_zcsl = t_zcsl.add(new BigDecimal(zcsl));
                        String scsl = detail.get("scsl");
                        if(storeCheckBean.isDouble(scsl))
                          t_scsl = t_scsl.add(new BigDecimal(scsl));
                        String hssl = detail.get("hssl");
                        if(storeCheckBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));

                        String zchssl = detail.get("zchssl");
                       if(storeCheckBean.isDouble(zchssl))
                          t_zchssl = t_zchssl.add(new BigDecimal(zchssl));
                        double d_zcsl = zcsl.length()>0 ?  Double.parseDouble(zcsl) : 0;
                        double d_scsl = scsl.length()>0 ?  Double.parseDouble(scsl) : 0;
                        d_ce = d_scsl - d_zcsl;
                        ce = String.valueOf(d_ce);
                        t_ce = t_ce.add(new BigDecimal(ce));
                        isLoss = d_scsl>d_zcsl ? "ӯ" : (d_scsl==d_zcsl ? "ƽ" : "��");
                        String bz = detail.get("bz");
                        String cpid = detail.get("cpid");
                        String wzmxid = detail.get("wzmxid");
                        String kwName = "kwid_"+i;
                        String kwMcName = "kwMc_"+i;
                        String kwid = detail.get("kwid");
                    %>
                        <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                          <td class="td" nowrap><%=i+1%></td>
                          <td class="td" nowrap align="center">
                           <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                            <%if(!isEnd){%>
                            <input type="hidden" name="singleIdInput_<%=i%>" value="">
                            <input name="image" class="img" type="image" title="��ѡ����" src="../images/select_prod.gif" border="0"
                             onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=isprops_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops','&storeid='+form1.storeid.value,'product_change(<%=i%>)')">
                            <input name="image" class="img" type="image" title="���Ƶ�ǰ��" onClick="sumitForm(<%=storeCheckBean.COPYROW%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                            <input name="image" class="img" type="image" title="ɾ��" onClick="sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                            <%}%>
                          </td>
                          <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                                    String isprops = prodRow.get("isprops");
                                    detail.put("hsbl",prodRow.get("hsbl"));
                         %>
                          <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                            <input type="text" <%=bz.equals("1") ? "class=ednone" :  detailClass%>  style="width:100%" onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=bz.equals("1") ? "readonly" : readonly%>>
                            <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'>
                            <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                            <input type='hidden' id='isprops_<%=i%>' name='isprops_<%=i%>' value=''>
                          </td>
                          <td class="td" nowrap align="center">
                            <input type="text" <%=bz.equals("1") ? "class=ednone" :  detailClass%>   onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=bz.equals("1") ? "readonly" : readonly%>>
                          </td>
                          <%--02:14:50 ����һ��������� dmsxid td. Ϊ����һ��������Զ�������.��ΪҪ������cpid, ph�����ȷ��ֻΨһ��--%>
                          <td class="td" nowrap align="center">
                            <input type="HIDDEN"  id="dmsxid_<%=i%>"  name="dmsxid_<%=i%>"  value='<%=detail.get("dmsxid")%>'>
                            <input  <%=detailClass%>  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('���������Ʒ');return;} propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" <%=bz.equals("1") ? "readonly" : readonly%>>
                            <%if(!isEnd){%>
                            <img style='cursor:hand' src='../images/view.gif' title="�������" border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('���������Ʒ');return;}if(form1.isprops_<%=i%>.value=='0'){alert('�ò�Ʒ�޹������!');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>', form1.cpid_<%=i%>.value)">
                            <img style='cursor:hand' src='../images/delete.gif' title="ɾ��" border=0 onClick="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                            <%}%>
                            </td>
                          <td class="td" nowrap align="right"><input type="text" class="ednone_r"   onKeyDown="return getNextElement();" id="zcsl_<%=i%>" name="zcsl_<%=i%>" value='<%=detail.get("zcsl")%>' maxlength='<%=list.getColumn("zcsl").getPrecision()%>' readonly></td>
                          <td class="td" nowrap align="right"> <input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="scsl_<%=i%>" name="scsl_<%=i%>" value='<%=detail.get("scsl")%>' onFocus="saveCurrentVariable(<%=i%>)"  onChange="sl_onchange(<%=i%>, false)" maxlength='<%=list.getColumn("scsl").getPrecision()%>' <%=readonly%>>
                          <td class="td" nowrap align="right"><input type="text" class="ednone_r"   onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                          <td class="td" nowrap align="right"><input type="text" class="ednone_r"   style="width:100%" onKeyDown="return getNextElement();" id="zchssl_<%=i%>" name="zchssl_<%=i%>" value='<%=detail.get("zchssl")%>' readonly></td>
                          <td class="td" nowrap align="right"> <input type="text" <%=detailClass_r%>   style="width:100%" onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' onFocus="saveCurrentVariable(<%=i%>)" onChange="sl_onchange(<%=i%>, true)" maxlength='<%=list.getColumn("hssl").getPrecision()%>' <%=readonly%>>
                          </td>
                          <%--
                          <td class="td" nowrap><%=prodRow.get("jldw")%></td>
                          <td class="td" nowrap><%=prodRow.get("hsdw")%></td>
                          --%>
                          <td class="td" nowrap align="right"><input type="text" class="ednone"  style="width:100%" onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                          <%--02.23 15:13 �޸�js�¼�onblurΪonChange�¼� ���--%>
                          <td class="td" nowrap align="center"><input type="text" <%=bz.equals("1") ? "class=ednone" :  detailClass%>  style="width:100" onKeyDown="return getNextElement();" id="ph_<%=i%>" name="ph_<%=i%>" value='<%=detail.get("ph")%>' <%=bz.equals("1") ? "readonly" : readonly%>>
                          </td>
                          <td class="td" nowrap>
                        <%if(isEnd)
                          {
                           out.print("<input type='text' name="+kwMcName+" value='"+storeAreaBean.getLookupName(detail.get("kwid"))+"' class='ednone' readonly>");
                           out.print("<input type='hidden' name="+kwName+" value='"+detail.get("kwid")+"'>");
                          }
                        else {%>
                        <pc:select addNull="1" className="edFocused" name="<%=kwName%>">
                        <%=storeAreaBean.getList(detail.get("kwid"), "storeid", masterRow.get("storeid"))%></pc:select>
                        <%}%>
                        </td>
                          <td class="td" nowrap align="center"><input id="isLoss_<%=i%>" name="isLoss_<%=i%>" type="text" class="ednone"  value='<%=isLoss%>' readonly style="width:100%"></td>
                          <td class="td" nowrap align="right"><input id="ce_<%=i%>" name="ce_<%=i%>" type="text" class="ednone_r"  value='<%=ce%>' readonly></td>
                        </tr>
                        <%list.next();
                        }
                        for(; i < 4; i++){
                  %>
                        <tr id="rowinfo_<%=i%>">
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                        </tr>
                        <%}%>
                        <tr id="rowinfo_end">
                          <td class="td">&nbsp;</td>
                          <td class="tdTitle" nowrap>�ϼ�
                          </td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td"><input id="t_zcsl" name="t_zcsl" type="text" class="ednone_r" style="width:100%" value='<%=t_zcsl%>' readonly></td>
                          <td class="td"><input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
                          <td align="right" class="td">&nbsp;</td>
                          <td class="td"><input id="t_zchssl" name="t_zchssl" type="text" class="ednone_r" style="width:100%" value='<%=t_zchssl%>' readonly></td>
                          <td class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                          <td align="right" class="td">&nbsp;</td>
                          <td align="right" class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td align="right" class="td"><input id="t_ce" name="t_ce" type="text" class="ednone_r" style="width:100%" value='<%=t_ce%>' readonly></td>
                        </tr>
                      </table>
                    </div></td>
                </tr>
              </table></td>
          </tr>
        </table></td>
    </tr>
    <tr>
      <td> <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
          <tr>
            <td class="td"><b>�Ǽ�����:</b><%=masterRow.get("zdrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>�Ƶ���:</b><%=masterRow.get("zdr")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
              <%if(!isEnd){%>
              <input name="button4" type="button" class="button" title="����һ��(ALT+W)" value="����һ��(W)" style="width:75" onClick="buttonEventW()">
              <pc:shortcut key="w" script='<%="buttonEventW()"%>'/>
              <input name="button3" type="button" class="button" title="ɾ��ʵ�ʿ����Ϊ����(ALT+R)" style="width:75" value="ɾ������(R)" onClick="sumitForm(<%=storeCheckBean.DELETE_BLANK%>);">
               <pc:shortcut key="r" script='<%="sumitForm("+storeCheckBean.DELETE_BLANK+")"%>'/>
              <%--02.18 11:36 ���� �����̵����ť yjg--%>
              <input type="hidden" name="scanValue" value="">
              <%--input type="button" class="button" title="�̵��(E)" value="�̵��(E)" style="width:75" onClick="buttonEventE(false)">
              <pc:shortcut key="e" script='<%="buttonEventE(false)"%>'/--%>
              <input name="button2" type="button" class="button" title="�������(ALT+N)" value="�������(N)" style="width:75" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
              <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="����(ALT+S)" value="����(S)"  onClick="sumitForm(<%=Operate.POST%>);">
              <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%}%>
              <%/*if(!isEnd){*/
              %>
              <%--02.20 18:21 ���ϲ鿴���������絥��ͼ��.ͬʱ��Ӧ��js��DIVҲ�������� yjg--%>
               <%--02.24 0:59 ���� ��������ʾ���鿴���׵���ť��������.����û���̵�ŵ�����²���ʾ����.����һ���������ϵ�ʱ����ʾ yjg--%>
              <%
                if ( !masterRow.get("pdhm").equals("") )
                { String dispTitle = masterRow.get("sfdjid").equals("") ? "�������絥" : "�鿴���絥";
              %>
              <input name="viewDestory" type="button" class="button" style='width:90' title="<%=dispTitle%>(ALT+F)" value="<%=dispTitle%>(F)" onClick="buttonEventF()" src='../images/edit.old.gif' border="0">
              <pc:shortcut key="f" script='<%="buttonEventF()"%>'/>
              <%
                }
              /*}*/
              %>
              <%if(isCanDelete){%>
              <input name="button3" type="button" class="button" title="ɾ��(ALT+D)" value="ɾ��(D)" onClick="buttonEventD();">
              <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%if(!storeCheckBean.isApprove){%>
              <input name="btnback" type="button" class="button" title="����(ALT+C)" value="����(C)" onClick="backList();">
              <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
              <%--<input name="import" type="button" class="button" title="" value="import" onClick="sumitForm(<%=storeCheckBean.IMPORT_DATA%>);">--%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
<script language="javascript">initDefaultTableRow('tableview1',1);
  <%--02.27 17:54 ���� ���� ��������jsp��������������ҳ�е���ϸ���ϲ��ֵ�input�ĳ���. yjg--%>
<%=storeCheckBean.adjustInputSize(new String[]{"cpbm","product", "jldw", "ph", "sxz", "zcsl", "scsl", "isLoss", "ce", "kwid", "zchssl", "hssl"},  "form1", (storeCheckBean.max-min+1), min)%>
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
    var scslObj = document.all['scsl_'+i];
    var zcslObj = document.all['zcsl_'+i];
    var hsslObj = document.all['hssl_'+i];
    //var hsblObj = document.all['hsbl_'+i];
    var hsblObj = document.all['truebl_'+i];
    var ceObj = document.all['ce_'+i];
    var isLossObj = document.all['isLoss_'+i];

    var obj = isBigUnit ? hsslObj : scslObj;
     var showText = isBigUnit ? "����Ļ��������Ƿ�" : "����������Ƿ�";
     var changeObj = isBigUnit ? scslObj : hsslObj;
     if(obj.value==""){
       //2004-4-1 21:40 ���� �������������������벻�Ϸ���ʱ��һ��Ҫ�ϼ�ֵһ��Ҫ��ȥ��ý���ʱ���������ľ��е�ֵ. yjg
       if ( isBigUnit )
         document.all['t_hssl'].value -= form1.old_hssl.value;
      else
        document.all['t_scsl'].value -= form1.old_scsl.value;
       return;
     }

     if(isNaN(obj.value))
     {
       //2004-4-1 21:40 ���� �������������������벻�Ϸ���ʱ��һ��Ҫ�ϼ�ֵһ��Ҫ��ȥ��ý���ʱ���������ľ��е�ֵ. yjg
       if ( isBigUnit )
         document.all['t_hssl'].value -= form1.old_hssl.value;
      else
        document.all['t_scsl'].value -= form1.old_scsl.value;
       alert(showText);
       obj.focus();
       return;
     }
     /*2004-3-30 15:27 �޸� javascript�޸�ҳ���Ͽ���������ֵ������,����������λ,*/
     //����,��������������ϵ��������������Ч:���޸����е�һ�������һ���ǿյĻ������ı�
     if(!(changeObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1'))//�Ƿ�ǿ��ת��
     {
       if(hsblObj.value!="" && !isNaN(hsblObj.value))
         changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(scslObj.value)/parseFloat(hsblObj.value)));
       else
         changeObj.value ="";
     }
     //02.20 14:53 �޸��ж�ӯ�������Ϊ��������. yjg
    var result = parseFloat(scslObj.value) - parseFloat(zcslObj.value)
    isLossObj.value = result >0  ? "ӯ" : (result==0 ? "ƽ" : "��");
    ceObj.value = formatQty(result);//formatQty(parseFloat(scslObj.value) - parseFloat(zcslObj.value));
    cal_all(i);
  }
  function cal_all(i)
  {
    cal_tot('scsl', i);
    cal_tot('ce', i);
    cal_tot('hssl', i);
  }
  function cal_tot(type, i)
  {
    var tmpObj;
    var tot=0;
    //for(i=0; i<<%=detailRows.length%>; i++)
    //{
      if(type == 'scsl')
        tmpObj = document.all['scsl_'+i];
      else if(type == 'ce')
          tmpObj = document.all['ce_'+i];
      else if(type == 'hssl')
          tmpObj = document.all['hssl_'+i];
      else
        return;
      if(isNaN(tmpObj.value) || tmpObj.value == "")
        return;
      //  tot += parseFloat(tmpObj.value);
      //}
    if(type == 'scsl')
      document.all['t_scsl'].value = formatQty(
          parseFloat(document.all['t_scsl'].value)
          -parseFloat(form1.old_scsl.value)
          +parseFloat(tmpObj.value)
          );
    else if(type == 'ce')
      document.all['t_ce'].value = formatQty(
          parseFloat(document.all['t_ce'].value)
          -parseFloat(form1.old_ce.value)
          +parseFloat(tmpObj.value)
          );
    else if (type == 'hssl')
      document.all['t_hssl'].value = formatQty(
          parseFloat(document.all['t_hssl'].value)
          -parseFloat(form1.old_hssl.value)
          +parseFloat(tmpObj.value)
          );
  }
  function saveCurrentVariable(i)
  {
    var isScVariable = !isNaN(document.all['scsl_'+i].value) && document.all['scsl_'+i].value != "";
    var isHsVariable = !isNaN(document.all['hssl_'+i].value) && document.all['hssl_'+i].value != "";
    var isCeVariable = !isNaN(document.all['ce_'+i].value) && document.all['ce_'+i].value != "";

    form1.old_scsl.value = isScVariable?document.all['scsl_'+i].value:0;
    form1.old_hssl.value = isHsVariable?document.all['hssl_'+i].value:0;
    form1.old_ce.value = isCeVariable?document.all['ce_'+i].value:0;
  }
  function OrderGoodsMultiSelect(frmName, srcVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "../store/import_ordergoods_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
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
    paraStr = "../store/ordergoods_single_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&dwtxid="+curID+"&storeid="+storeid;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function showTreeTable(){
    document.all.selectKind.style.display = 'block';
    document.all.cpbmtext.style.display = 'none';
    document.all.chlbtext.style.display = 'none';
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
     sumitForm(<%=storeCheckBean.NEW_TRANSFERSCAN%>);
   else
     sumitForm(<%=storeCheckBean.TRANSFERSCAN%>);
  }
</script>
<%
  EngineDataSet dsProductSort = storeCheckBean.getProductSortTable();//�õ�����������ݼ�
%>
  <div class="queryPop" id="fixedQuery" name="fixedQuery" >
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD height="20" CLASS="td"><b>��&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��</b></TD>
              <TD nowrap class="td"><pc:select name="add_storeid" style="width:160">
                <%=storeBean.getList(masterRow.get("storeid"))%></pc:select> </TD>
            </TR>
            <TR>
              <TD colspan="2" NOWRAP CLASS="td"><b>�����Χ</b></TD>
            </TR>
            <TR>
              <TD colspan="2" NOWRAP CLASS="td"> <INPUT TYPE=RADIO NAME="scope" VALUE="1" onClick="hideText();" CHECKED>
                ���д��</TD>
            </TR>
            <TR>
              <TD class="td"><INPUT TYPE="RADIO" NAME="scope" VALUE="2" onClick="showText(this.value);">
                ��������ɸѡ</TD>
              <TD CLASS="td" id="chlbtext" name="chlbtext" style="display:block"><pc:select name="chlbid" style="width:170">
                <%=stockKindBean.getList()%></pc:select> </TD>
            </TR>
            <TR>
              <TD colspan="2" CLASS="td"> <INPUT TYPE="RADIO" NAME="scope" VALUE="3" onClick="showText(this.value);">
                ָ��������뷶Χ</TD>
            </TR>
            <TR id="cpbmtext" style="display:block">
              <TD colspan="2" CLASS="td">
                ���뷶Χ<input type="text" name="cpbm_a" style="width:70" class="edbox">
                ��
                <input type="text" name="cpbm_b" style="width:70" class="edbox">
            </TD>
           </TR>
            <TR>
              <TD colspan="2" CLASS="td"> <INPUT TYPE="RADIO" NAME="scope" VALUE="4" onClick="showTreeTable();" >
                ָ��ĳ��������</TD>
            </TR>
            <TD colspan="2" CLASS="td"> <div id="selectKind" name="selectKind" style="display:none;width:300;height:200;overflow-y:auto;overflow-x:auto;">
                <table id="tableview2" width="260" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                  <tr class="tableTitle">
                    <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
                    <td nowrap>����</td>
                    <td nowrap>����</td>
                  </tr>
                  <%
               int rowcount = dsProductSort.getRowCount();
               dsProductSort.first();
               for(int k=0; k<rowcount; k++){
              %>
                  <tr>
                    <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=dsProductSort.getValue("wzlbid")%>' onKeyDown="return getNextElement();"></td>
                    <td nowrap align="right" class="td" onClick="checkRadio(<%=k%>)"><%=dsProductSort.getValue("bm")%>
                      <input name="hidden" type="hidden" id="wzlbid<%=k%>" value='<%=dsProductSort.getValue("wzlbid")%>'></td>
                    <td nowrap align="right" class="td" onClick="checkRadio(<%=k%>)"><%=dsProductSort.getValue("mc")%></td>
                  </tr>
                  <%dsProductSort.next();
               }%>
                </TABLE>
              </DIV></TD>
          </TABLE>
      <TR>
        <TD colspan="4" nowrap class="td" align="center"><INPUT type="button" class="button" title="ȷ��(ALT+M)" value="ȷ��(M)" onClick="submitAdd()"   name="button" onKeyDown="return getNextElement();">
               <pc:shortcut key="m" script='<%="submitAdd()"%>'/>
          <INPUT onClick="hideFrame('fixedQuery')" type="button"  class="button" title="�ر�(ALT+T)" value="�ر�(T)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="t" script='<%="buttonEventT()"%>'/>
        </TD>
      </TR>
    </TABLE>
  </DIV>

  <div class="queryPop" id="detailDiv" name="detailDiv">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=0 cellpadding=0 border=0>
      <TR>
        <TD><iframe id="interframe1" src="" width="800" height="400" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
        </TD>
      </TR>
    </TABLE>
  </div>
  </form>
<SCRIPT language="javascript">
  initDefaultTableRow('tableview2',1);
  function buttonEventW()
  {
    if(form1.storeid.value==''){alert('��ѡ��ֿ�');return;}
    sumitForm(<%=storeCheckBean.DETAIL_ADD_BLANK%>);
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
   if(confirm('�Ƿ�ɾ���ü�¼��'))sumitForm(<%=Operate.DEL%>);
 }
 //��ӡ
 function buttonEventP()
 {
    location.href='../pub/pdfprint.jsp?code=store_check_edit_bill&operate=<%=storeCheckBean.PRINT_BILL%>&a$pdid=<%=masterRow.get("pdid")%>&src=../store_shengyu/store_check_edit.jsp';
 }
 //�鿴���������浥
 <%
   if ( !masterRow.get("pdhm").equals("") )
   {
 %>
 function buttonEventF()
 {
   showInterFrame(<%=masterRow.get("pdid")%>, <%=masterRow.get("deptid")%>,<%=masterRow.get("storeid")%>,<%=0%>);
 }
 <%}%>
   //�رղ�ѯС�Ӵ�
   function buttonEventT()
   {
     hideFrame('fixedQuery');
   }
   //2004-3-30 15:44 ���� �����������������ʹ�����˸������Լ���ͬ���Ŷ����js���� yjg
  function deptChange()
  {
    associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'pdr', 'deptid', eval('form1.deptid.value'), '');
  }
  function checkDetails(oper)
  {

   //if ( oper !=<%=storeCheckBean.TURNPAGE%> ) return true;
    if ( <%=max%>==0 ) return true;
    for (j=<%=min%>;j<=<%=max%>;j++)
   {
      obj = document.all['hssl_'+j];
      if(obj+''!='undefined' && isNaN(obj.value))
        obj.value = "";
      obj = document.all['scsl_'+j];
      if(obj+''!='undefined' && isNaN(obj.value))
        obj.value = "";
   }
    for (i=<%=min%>;i<=<%=max%>;i++)
    {
      hsslobj = document.all['hssl_'+i];
      scslobj = document.all['scsl_'+i];
      if ((hsslobj+''!='undefined' && isNaN(hsslobj.value))||
          (scslobj+''!='undefined' && isNaN(scslobj.value)) )
      {
         alert("��"+(i+1)+"�����ݲ��Ϸ�");
         return false;
      }
      else
        return true;
    }
  }
  function showDetail(masterRow){
    selectRow();
  }
</SCRIPT>
<%if(storeCheckBean.isApprove){%>
<jsp:include page="../pub/approve.jsp" flush="true"/>
<%}%>
<%out.print(retu);%>
</BODY>
</Html>
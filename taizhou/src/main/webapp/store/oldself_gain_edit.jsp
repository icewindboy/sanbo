<%--库存自制收货单从表--%>
<%
/**
 * 03.13 15:37 新增 新增打印单据按钮来把这张采购入库单页面上的内容打印出来. yjg
 * 03.10 10:37 修改 修改规格属性为可输入的.并且新增onChange时的js函数.yjg
 * 03.09 12:06 修改 调整了表格中的规格属性, 数量td的排列位置. 下面的具体显示这些值的jsp scripts也做了相应调整. yjg
 * 03.09 11:43 新增 新增关闭按钮提供给当此页面是被报表调用时使用. yjg
 */
%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.store.B_SelfGain selfGainBean = engine.erp.store.B_SelfGain.getInstance(request);
  selfGainBean.isout="0";
  String pageCode = "self_gain_list";
  //boolean hasApproveLimit = isApprove && loginBean.hasLimits(pageCode, op_approve);
%>
<jsp:include page="../pub/scan_bar.jsp" flush="true"/>
<jsp:include page="../baseinfo/script.jsp" flush="true"/>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
  lockScreenToWait("处理中, 请稍候！");
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
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=scydw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isbatchno_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=hsbl&fieldVar=scdwgs&fieldVar=isbatchno', obj.value);
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=scydw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isbatchno_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=hsbl&fieldVar=scdwgs&fieldVar=isbatchno', obj.value);
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
}
function deptChange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'handleperson', 'deptid', eval('form1.deptid.value'), '');
}
</script>
<%String retu = selfGainBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_STORE_UNIT_STYLE = selfGainBean.SC_STORE_UNIT_STYLE;//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  String KC_PRODUCE_UNIT_STYLE = selfGainBean.KC_PRODUCE_UNIT_STYLE;//计量单位和生产单位换算方式1=强制换算,0=仅空值时换算
  String SYS_PRODUCT_SPEC_PROP = selfGainBean.SYS_PRODUCT_SPEC_PROP;
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//通过加工单明细id得到加工单号
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  //engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp produceInBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_PRODUCE_IN);//单据类别
  engine.project.LookUp produceUseBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_USE);//用途
  //engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = selfGainBean.getMaterTable();
  EngineDataSet list = selfGainBean.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHandwork = loginBean.getSystemParam("KC_HANDIN_STOCK_BILL").equals("1");//得到是否可以手工增加的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  HtmlTableProducer masterProducer = selfGainBean.masterProducer;
  HtmlTableProducer detailProducer = selfGainBean.detailProducer;
  RowMap masterRow = selfGainBean.getMasterRowinfo();
  RowMap[] detailRows= selfGainBean.getDetailRowinfos();
  String zt=masterRow.get("state");
  if(selfGainBean.isApprove)
  {
    workShopBean.regData(ds,"deptid");
    storeBean.regData(ds, "storeid");
    produceInBean.regData(ds, "sfdjbid");
  }
  boolean isEnd = selfGainBean.isReport || selfGainBean.isApprove || (!selfGainBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !selfGainBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(selfGainBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("9") ? "审批中" : (zt.equals("2") ? "记帐" : "未审核"));
  boolean isAdd = selfGainBean.isDetailAdd;
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
            <td class="activeVTab">自制收货单(<%=title%>)
			<%--03.19 20:32 新增 新增为方便不退出此页面就可以直接打印xxx_top.jsp上列出的单据而加的上一笔,下一笔按钮 yjg--%>
              <%
              //当是新增的时候不显示出上一笔下一笔
              if (!selfGainBean.masterIsAdd())
              {
                ds.goToInternalRow(selfGainBean.getMasterRow());
                boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
                if (!isAtFirst)
              {%>
              <a href="#" title="到上一笔(ALT+Z)" onClick="sumitForm(<%=selfGainBean.PRIOR%>)">&lt</a>
              <pc:shortcut key='z' script='<%="sumitForm("+selfGainBean.PRIOR+")"%>'/>
             <%}
               if (!isAtLast)
              {%>
              <a href="#" title="到上一笔(ALT+X)" onClick="sumitForm(<%=selfGainBean.NEXT%>)">&gt</a>
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
                  }
                 %>
                  <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("receiveCode").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="receiveCode" value='<%=masterRow.get("receiveCode")%>' maxlength='<%=ds.getColumn("receiveCode").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("receiveDate").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="receiveDate" value='<%=masterRow.get("receiveDate")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.sfrq);"></a>
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
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjlbid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+produceInBean.getLookupName(masterRow.get("sfdjlbid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="sfdjlbid" addNull="1" style="width:110">
                      <%=produceInBean.getList(masterRow.get("sfdjlbid"))%> </pc:select>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
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
                  <pc:select combox="1" className="edFocused" name="handleperson" value="<%=handleperson%>" style="width:110">
                  <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%></pc:select>
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("ytid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+produceUseBean.getLookupName(masterRow.get("ytid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="ytid" addNull="1" style="width:110">
                      <%=produceUseBean.getList(masterRow.get("ytid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("memo").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="memo" value='<%=masterRow.get("memo")%>' maxlength='<%=ds.getColumn("memo").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                  </tr>
                <%/*打印用户自定义信息*/
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
                        <td nowrap width=15>
                        <input class="edFocused_r"  name="tCopyNumber" value="<%=request.getParameter("tCopyNumber")==null?"1":request.getParameter("tCopyNumber")%>"  size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                        </td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){%>
                          <input name="image" class="img" type="image" title="新增(ALT+A)" onClick="buttonEventA()" src="../images/add_big.gif" border="0">
                           <pc:shortcut key="a" script="buttonEventA()"/>
                          <%}%>
                        </td>
                          <td height='20' nowrap><%=detailProducer.getFieldInfo("jgdmxid").getFieldname()%></td>
                          <td height='20' nowrap>产品编码</td>
                          <td height='20' nowrap>品名 规格</td>
                          <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("batchno").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("drawbignum").getFieldname()%></td>
                          <td height='20' nowrap>换算单位</td>
                          <td nowrap><%=detailProducer.getFieldInfo("drawnum").getFieldname()%></td>
                          <td height='20' nowrap>计量单位</td>
                          <td nowrap><%=detailProducer.getFieldInfo("producenum").getFieldname()%></td>
                          <td height='20' nowrap>生产单位</td>
                          <td nowrap><%=detailProducer.getFieldInfo("kwid").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("memo").getFieldname()%></td>
                          <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      propertyBean.regData(list,"dmsxid");
                      processBean.regData(list,"jgdmxid");
                      BigDecimal t_sl = new BigDecimal(0), t_scsl = new BigDecimal(0), t_hssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
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
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                        String kwName = "kwid_"+i;
                        String dmsxidName = "dmsxid_"+i;
                        String jgdmxid=detail.get("jgdmxid");
                        boolean isimport = !jgdmxid.equals("");//引入加工单，从表产品编码当前行不能修改
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(!isEnd && !isimport){%>
                          <%--input type="hidden" name="mutibatch_<%=i%>" value="" onchange="sumitForm(<%=selfGainBean.MATCHING_BATCH%>,<%=i%>)">
                    <input name="image" class="img" type="image" title="配批号" onClick="if(form1.cpid_<%=i%>.value==''){alert('请输入产品');return;}if(form1.storeid.value==''){alert('请输入产品'); return;}BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+form1.cpid_<%=i%>.value+'&storeid='+form1.storeid.value+'&dmsxid='+form1.dmsxid_<%=i%>.value)" src="../images/edit.old.gif" border="0"--%>
                          <img style='cursor:hand' title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=scydw_<%=i%>&srcVar=isprops_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=isbatchno_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=isprops&fieldVar=hsbl&fieldVar=scdwgs&fieldVar=isbatchno','&storeid='+form1.storeid.value)">

                          <%}if(!isEnd){%>
                          <input name="image" class="img" type="image" title="复制当前行" onClick="if(form1.cpid_<%=i%>.value==''){alert('请输入产品');return;}sumitForm(<%=selfGainBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <input name="image" class="img" type="image" title="删除" onClick="sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td><%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));%>
                        <td class="td" nowrap><%=processBean.getLookupName(detail.get("jgdmxid"))%></td>
                        <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="hidden" name="isbatchno_<%=i%>" value="<%=prodRow.get("isbatchno")%>">
                        <input type="hidden" name="hsbl_<%=i%>" value="<%=prodRow.get("hsbl")%>">
                        <input type="hidden" name="scdwgs_<%=i%>" value="<%=prodRow.get("scdwgs")%>">
                        <input type="hidden" name="widths_<%=i%>" value="<%=widths%>">
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                        <input type="text" <%=isimport ? "class=ednone" : detailClass%>  onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                         <td class="td" nowrap><input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                        <td class="td" nowrap>
                        <input <%=detailClass%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if(form1.isprops_<%=i%>.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="batchno_<%=i%>" name="batchno_<%=i%>" value='<%=detail.get("batchno")%>' maxlength='<%=list.getColumn("batchno").getPrecision()%>'<%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawbignum_<%=i%>" name="drawbignum_<%=i%>" value='<%=detail.get("drawbignum")%>' maxlength='<%=list.getColumn("drawbignum").getPrecision()%>' onblur="sl_onchange(<%=i%>, true)"<%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawnum_<%=i%>" name="drawnum_<%=i%>" value='<%=detail.get("drawnum")%>' maxlength='<%=list.getColumn("drawnum").getPrecision()%>' onblur="sl_onchange(<%=i%>, false)"<%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="producenum_<%=i%>" name="producenum_<%=i%>" value='<%=detail.get("producenum")%>' maxlength='<%=list.getColumn("producenum").getPrecision()%>' onblur="producesl_onchange(<%=i%>)"<%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly></td>
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
                      for(; i < 4; i++){
                  %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                       <td class="td"><input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
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
            <td class="td"><b>登记日期:</b><%=masterRow.get("zdrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("zdr")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
             <%if(!isEnd){%>
             <input type="hidden" name="importProcess" value="">
             <input name="btnback" type="button" class="button"  title="引加工单(ALT+Q)"style='width:85' value="引加工单(Q)"
             onClick="buttonEventQ()">
                <pc:shortcut key="q" script='<%="buttonEventQ()"%>'/>
              <input type="hidden" name="scanValue" value="">
                <input type="button" class="button" title="盘点机(E)" value="盘点机(E)" style='width:65' onClick="buttonEventE(false)">
                <pc:shortcut key="e" script='<%="buttonEventE(false)"%>'/>
                 <input type="button" class="button" title="新盘点机(R)" value="新盘点机(R)" style='width:80' onClick="buttonEventE(true)">
                <pc:shortcut key="r" script='<%="buttonEventE(true)"%>'/>
              <input name="button2" type="button" class="button" title="删除数量为空行(ALT+X)" value="删除数量为空行(X)" style='width:120' onClick="sumitForm(<%=selfGainBean.DELETE_BLANK%>);">
                <pc:shortcut key="x" script='<%="sumitForm("+selfGainBean.DELETE_BLANK+")"%>'/>
               <input name="button2" type="button" class="button" title="保存添加(ALT+N)" value="保存添加(N)" style='width:80' onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
                <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="保存(ALT+S)" value="保存(S)" style='width:80' onClick="sumitForm(<%=Operate.POST%>);">
                <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%}%>
              <%if(isCanDelete && !selfGainBean.isReport){%><input name="button3" type="button" class="button" title="删除(ALT+D)" style='width:60' value="删除(D)" onClick="buttonEventD();">
                <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <%if(!selfGainBean.isApprove && !selfGainBean.isReport){%><input name="btnback" type="button" class="button" title="返回(ALT+C)" style='width:60' value="返回(C)" onClick="backList();">
                <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
                <%--03.09 11:43 新增 新增关闭按钮提供给当此页面是被报表调用时使用. yjg--%>
                <%if(selfGainBean.isReport){%><input name="btnback" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)"  style='width:60' onClick="window.close()">
                <pc:shortcut key="t" script='<%="window.close()"%>'/>
               <%}%>
                <%--03.13 15:37 新增 新增打印单据按钮来把这张采购入库单页面上的内容打印出来. yjg--%>
              <input type="button" class="button" title="打印(ALT+P)" value="打印(P)" style='width:60' onclick="buttonEventP();">
                <pc:shortcut key="p" script='<%="buttonEventP()"%>'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
  function onload(){
    <%=selfGainBean.adjustInputSize(new String[]{"cpbm","product", "jldw", "batchno", "drawnum","sxz", "memo","drawbignum","producenum","hsdw", "scydw"},  "form1", detailRows.length)%>
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
       if(slObj.value=='' && scslObj.value=='')
         return;
       if(slObj.value!='')
         scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthValue));
       else if(slObj.value=='' && scslObj.value!=''){
         slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthValue)/parseFloat(scdwgsObj.value));
         if(hsblObj=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
            hsslObj.value = slobj.value;
         else
            hsslObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
       }
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
       var slObj = document.all['drawnum_'+i];
       var hsslObj = document.all['drawbignum_'+i];
       var scslObj = document.all['producenum_'+i];
       var hsblObj = document.all['hsbl_'+i];
       var scdwgsObj = document.all['scdwgs_'+i];//生产公式
       var obj = isBigUnit ? hsslObj : slObj;
       var widthObj = document.all['widths_'+i];//规格属性的宽度
       var showText = isBigUnit ? "输入的换算数量非法" : "输入的数量非法";
       var showText2 = isBigUnit ? "输入的换算数量小于零" : "输入的数量小于零";
       var changeObj = isBigUnit ? slObj : hsslObj;
        if(changeObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1')//是否强制转换
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
        if(hsblObj=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
          changeObj.value = obj.value;
        else
          changeObj.value = formatQty(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
        cal_tot('sl');
        cal_tot('hssl');
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
         var slObj = document.all['drawnum_'+i];
         var hsslObj = document.all['drawbignum_'+i];
         var scslObj = document.all['producenum_'+i];
         var hsblObj = document.all['hsbl_'+i];
         var scdwgsObj = document.all['scdwgs_'+i];//生产公式
         var widthObj = document.all['widths_'+i];//规格属性的宽度
         if(slObj.value!="" && '<%=KC_PRODUCE_UNIT_STYLE%>'!='1')//生产数量与数量是否强制转换
           return;
         if(scslObj.value=="")
           return;
         if(isNaN(scslObj.value))
         {
           alert('输入的生产数量非法');
           obj.focus();
           return;
         }
         if(scslObj.value<=0)
         {
           alert('输入的生产数量小于零');
           obj.focus();
           return;
         }
         if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
           slObj.value= scslObj.value;
         else
           slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthObj.value)/parseFloat(scdwgsObj.value));
         if(hsslObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1')
           return;
         if(hsblObj=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
           hsslObj.value = slobj.value;
         else
          hsslObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
         cal_tot('scsl');
         cal_tot('sl');
         cal_tot('hssl');
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
  function ImportProcessSelect(frmName,srcVar,fieldVar,curID,isout,storeid,methodName,notin)
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
  function transferScan(isNew)//调用盘点机
  {
    var scanValueObj = form1.scanValue;
    scanValueObj.value = scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "self_gain_edit.jsp", "IT3CW32d.DLL")%>');//得到包含产品编码和批号的字符串
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
      alert('请选择仓库');return;
    }
    transferScan(isNew);
  }
  //引加工单
  function buttonEventQ()
  {
    if(form1.storeid.value=='')
    {
      alert('请选择仓库');
      return;
    }
     ImportProcessSelect('form1','srcVar=importProcess','fieldVar=jgdid',form1.deptid.value,'0',form1.storeid.value,'sumitForm(<%=selfGainBean.SELF_SEL_PROCESS%>)')
  }
  //删除
  function buttonEventD()
  {
     if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);
  }
  function buttonEventP()
  {
   location.href='../pub/pdfprint.jsp?code=self_gain_edit_bill&operate=<%=selfGainBean.PRINT_BILL%>&a$receiveid=<%=masterRow.get("receiveid")%>&src=../store/self_gain_edit.jsp'
  }

  function buttonEventA()
  {
      if(form1.storeid.value==''){alert('请选择仓库');return;}sumitForm(<%=Operate.DETAIL_ADD%>);
  }
</script>
<%if(selfGainBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
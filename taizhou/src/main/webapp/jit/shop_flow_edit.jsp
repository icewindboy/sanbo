<%--车间流转单--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal,engine.erp.baseinfo.BasePublicClass"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%
  engine.erp.jit.ShopFlow shopFlowBean = engine.erp.jit.ShopFlow.getInstance(request);
  String pageCode = "shop_flow";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
%>
<jsp:include page="../baseinfo/script.jsp" flush="true"/>
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
  location.href='shop_flow.jsp';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function corpCodeSelect(obj)
{
  //02.18 15:47 CustomerCodeChange函数使用参数1与ProvideCodeChange函数功能是相同的.查找供应商 yjg
  CustomerCodeChange('1',document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
  //ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 //'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(10601)');
}
function productNameSelect(obj,i)
{
   ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
            'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.valu);
}

function corpNameSelect(obj)
{
     CustomerNameChange('1', document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
     'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
function deptChange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_WORK_GROUP%>', 'gzzid', 'deptid', eval('form1.deptid.value'), '',true);
   //associateSelect(document.all['prod2'], '<%=engine.project.SysConstant.BEAN_WORK_GROUP%>', 'sc__gzzid', 'deptid', eval('form1.bm_deptid.value'), '',true);
   //associateSelect(document.all['prod1'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jsr', 'jsr', eval('form1.jsr.value'), '',true);
}
function deptChange0(){
   sumitForm(<%=shopFlowBean.RECEIVE_GZZ_ONCHANGE%>,-1)
   /*associateSelect(document.all['prod2'], '<%=engine.project.SysConstant.BEAN_WORK_GROUP%>', 'sc__gzzid', 'deptid', eval('form1.bm_deptid.value'), '',true);
   if (form1.sc__gzzid.value=='')
     associateSelect(document.all['prod1'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jsr', 'deptid', eval('form1.bm_deptid.value'), '',true);
   else
     associateSelect(document.all['prod1'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jsr', 'deptid', eval('form1.sc__gzzid.value'), '',true);
  */
  }
</script>
<%String retu = shopFlowBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_PRODUCE_UNIT_STYLE = shopFlowBean.SC_PRODUCE_UNIT_STYLE;
  String SYS_PRODUCT_SPEC_PROP =shopFlowBean.SYS_PRODUCT_SPEC_PROP;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//存货信息
  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_SALE_ORDER_GOODS);//根据销售合同货物id得到合同编号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);

  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//通过工作组id得到工作组名称
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//通过jgdmxid得到加工单号(?任务单号?)
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = shopFlowBean.getMaterTable();
  EngineDataSet list = shopFlowBean.getDetailTable();
  HtmlTableProducer masterProducer = shopFlowBean.masterProducer;
  HtmlTableProducer detailProducer = shopFlowBean.detailProducer;
  RowMap masterRow = shopFlowBean.getMasterRowinfo();
  RowMap[] detailRows= shopFlowBean.getDetailRowinfos();
  String registeredPersonBean = masterRow.get("sc__gzzid").equals("")?engine.project.SysConstant.BEAN_PERSON:engine.project.SysConstant.BEAN_GZZ_PERSON;
  boolean isGzzChoiced = masterRow.get("sc__gzzid").equals("")?false:true;
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, registeredPersonBean);


  String zt=masterRow.get("zt");
  boolean isEnd = shopFlowBean.isReport || shopFlowBean.isApprove || (!shopFlowBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !shopFlowBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(shopFlowBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  String deptid = masterRow.get("deptid");//得到该单据的制单部门id
  String zdrid = masterRow.get("zdrid");//得到该单据的制单员id
  boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, zdrid);//判断登陆员工是否有操作该制单人单据的权限

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核") : (zt.equals("9") ? "审批中" : (zt.endsWith("2") ? "已生成物料需求" : (zt.equals("3") ? "已下达任务" : (zt.equals("8") ? "已完成" : "未审核"))));
  boolean isAdd = shopFlowBean.isDetailAdd;
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<script language="javascript">var scaner=parent.scaner;</script>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prod1" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prod2" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prod3" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
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
            <td class="activeVTab">车间流转单(<%=title%>)
            </td>
          </tr>
        </table>
             <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%deptBean.regData(ds,"deptid");
                  personBean.regConditionData(ds, "deptid");
                  String jsr=masterRow.get("jsr");
                  workGroupBean.regConditionData(ds,"deptid");
                  String gzzidtmp = masterRow.get("sc__gzzid");
                  String deptidtmp = masterRow.get("bm_deptid");
                  String tmpPersonLookUpKeyValue = isGzzChoiced?gzzidtmp:deptidtmp;
                  String tmpPersonLookUpKey = isGzzChoiced?"gzzid":"deptid";
                  if (isGzzChoiced)
                    personBean.regConditionData("gzzid", new String[]{tmpPersonLookUpKeyValue});
                  else
                    personBean.regConditionData("deptid", new String[]{tmpPersonLookUpKeyValue});
                  //personBean.regData(list,"personid");

                %>
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("cjlzdh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="cjlzdh" value='<%=masterRow.get("cjlzdh")%>' maxlength='<%=ds.getColumn("cjlzdh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                   <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110" onSelect="deptChange()">
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("gzzid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+workGroupBean.getLookupName(masterRow.get("gzzid"))+"' style='width:110' class='edline' readonly>");
                   else {%>
                   <pc:select name="gzzid" addNull="1" style="width:110">
                   <%=workGroupBean.getList(masterRow.get("gzzid"),"deptid",masterRow.get("deptid"))%> </pc:select>
                   <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("wgrq").getFieldname()%></td>
                  <td noWrap class="td">
                    <input type="text" name="wgrq" value='<%=masterRow.get("wgrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.wgrq);"></a>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bm_deptid").getFieldname()%></td>
                  <td  noWrap class="td">
                      <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("bm_deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="bm_deptid" addNull="1" style="width:110" onSelect="deptChange0()">
                      <%=deptBean.getList(masterRow.get("bm_deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sc__gzzid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+workGroupBean.getLookupName(masterRow.get("sc__gzzid"))+"' style='width:110' class='edline' readonly>");
                   else {%>
                   <pc:select name="sc__gzzid" addNull="1" style="width:110" onSelect="deptChange0()">
                   <%=workGroupBean.getList(masterRow.get("sc__gzzid"),"deptid",masterRow.get("bm_deptid"))%> </pc:select>
                   <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsr").getFieldname()%></td>
                  <td class="td" nowrap>
                    <%if(isEnd || !isHasDeptLimit){%> <input type="text" name="jsr" value='<%=masterRow.get("jsr")%>' maxlength='<%=ds.getColumn("jsr").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  <%}else {%>
                  <pc:select combox="1" className="edFocused" name="jsr" value="<%=jsr%>" style="width:110">
                    <%=personBean.getList(masterRow.get("jsr"), tmpPersonLookUpKey, tmpPersonLookUpKeyValue)%></pc:select>
                  </pc:select>
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsrq").getFieldname()%></td>
                  <td noWrap class="td">
                    <input type="text" name="jsrq" value='<%=masterRow.get("jsrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.jsrq);"></a>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bz").getFieldname()%></td>
                  <td noWrap class="td" colspan="3">
                    <input type="text" name="bz" value='<%=masterRow.get("bz")%>' maxlength='<%=ds.getColumn("bz").getPrecision()%>' style="width:290" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>>
                  </td>
                  <td noWrap class="tdTitle">&nbsp;</td>
                  <td noWrap class="td" colspan="3">&nbsp;</td>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
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
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                           <%if(!isEnd){%>
                          <%--<input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">--%>
                          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%>
                        </td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("jgdmxID").getFieldname()%></td>
                         <%
                        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-1;i++)
                          out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
                        %>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      processBean.regData(list,"jgdmxID");
                      propertyBean.regData(list,"dmsxid");
                      //importApplyBean.regData(list,"hthwid");
                      BigDecimal t_sl = new BigDecimal(0), t_hssl = new BigDecimal(0),t_ywcl = new BigDecimal(0), t_scsl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++){
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(shopFlowBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String dmsxid = detail.get("dmsxid");
                        String sx = propertyBean.getLookupName(dmsxid);
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                        String dmsxidName = "dmsxid_"+i;
                        String jgdmxID = detail.get("jgdmxID");
                        boolean isimport = !jgdmxID.equals("");//从表当前行引入销售合同
                        String cpid = detail.get("cpid");
                        //取得任务单LOOKUP bean用来得到任务单号
                        RowMap processRow = processBean.getLookupRow(detail.get("jgdmxID"));
                        RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                        String hsbl = prodRow.get("hsbl");
                        String isprop = prodRow.get("isprops");
                        detail.put("hsbl",hsbl);
                        String Class = isimport  ? "class=ednone" : detailClass;//从表Class模式
                    %>
                      <tr id="rowinfo_<%=i%>" onClick="selectRow()">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd && !isimport){%>
                          <input type="hidden" name="singleIdInput_<%=i%>" value="">
                          <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0" onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=isprops_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fielfVar=jldw&fieldVar=scydw&filedVar=scdwgs&fieldVar=isprops&filedVar=ztqq','')">
                          <%}if(!isEnd){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <td class="td" nowrap><%=processRow.get("jgdh")%></td>
                        <td class="td" nowrap>
                        <input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="hidden" name="widths_<%=i%>" value="<%=widths%>">
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                        <input type="text" <%=Class%> onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>>
                        </td>
                       <td class="td" nowrap><input type="text" <%=Class%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                        <td class="td" nowrap>
                        <input <%=(isimport || isEnd) ? "class=ednone" : detailClass%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=isimport ? "readonly" : readonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd && !isimport){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if(form1.isprops_<%=i%>.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>','<%=cpid%>')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>'  <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class="ednone" style="width:60" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass%>   style="width:65" onKeyDown="return getNextElement();" name="bz_<%=i%>"  id="bz_<%=i%>" value='<%=detail.get("bz")%>' <%=readonly%> ></td>

                        <%FieldInfo[] bakFields = detailProducer.getBakFieldCodes();
                          for(int k=0; k<bakFields.length; k++)
                        {
                          String fieldCode = bakFields[k].getFieldcode();
                          out.print("<td class='td' nowrap>");
                          out.print(detailProducer.getFieldInput(bakFields[k], detail.get(fieldCode), fieldCode+"_"+k, "style='width:65'", isEnd, true));
                          out.println("</td>");
                        }
                        %></tr>
                        <%
                          list.next();
                      }
                      for(; i < 4; i++){
                     %>
                      <tr id="rowinfo_<%=i%>">
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
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
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
              <input type="hidden" name="drawSingleProcessTask" value="" onchange="sumitForm(<%=shopFlowBean.DRAW_SINGLE_PROCESSTASK%>)">
              <INPUT type="hidden" NAME="selmethod" value="">
              <input name="btnback" class="button" type="button" value="引加工单(W)" style="width:100" onClick="buttonEventW();">
              <pc:shortcut key="w" script="buttonEventW();"/>
             <input name="button2" type="button" class="button" title="保存添加(ALT+N)" value="保存添加(N)" style="width:75" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" >
              <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="保存(ALT+S)" value="保存(S)" onClick="sumitForm(<%=Operate.POST%>);" >
              <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%}%>
              <%--02.23 11:46 新增 新增显示下面这几个按钮的条件中加上isReport条件 yjg--%>
              <%if(isCanDelete && isHasDeptLimit && !shopFlowBean.isReport){%>
              <input name="button3" type="button" class="button" title="删除(ALT+D)" onClick="buttonEventD()" value="删除(D)">
              <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%if(!shopFlowBean.isApprove && !shopFlowBean.isReport){%>
              <input name="btnback" type="button" class="button" title="返回(ALT+C)" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
              <%--03.08 21:14 新增 新增关闭按钮提供给当此页面是被报表调用时使用. yjg--%>
              <%if(shopFlowBean.isReport){%>
              <input name="btnback" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)" onClick="window.close()" >
              <pc:shortcut key="t" script='<%="window.close()"%>'/>
              <%}%>
              <%--03.02 21:26 新增 新增打印单据按钮来把这张采购入库单页面上的内容打印出来. yjg--%>
              <input type="button" class="button" title="打印(ALT+P)" value="打印(P)" onclick="buttonEventP()">
              <pc:shortcut key="p" script='<%="buttonEventP()"%>'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=shopFlowBean.adjustInputSize(new String[]{"cpbm","product","sl", "sxz", "bz", "jldw"}, "form1", detailRows.length)%>
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
    var slObj = document.all['sl_'+i];
    var scslObj = document.all['scsl_'+i];
    if(slObj.value=='' && scslObj.value=='')
      return;
    else if(slObj.value!='')
      scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthValue));
    else if(slObj.value=='' && scslObj.value!='')
      slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthValue)/parseFloat(scdwgsObj.value));
  }
  function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
  }
  function importsale()
  {
    OrderMultiSelect('form1','srcVar=importorder&selmethod=selmethod');
  }
  function OrderMultiSelect(frmName, srcVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "../produce_huazheng/plan_select_sale.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function sl_onchange(i, isBigUnit)
  {
    var slObj = document.all['sl_'+i];

    var obj = slObj;

    var showText = "输入的数量非法";
    var showText2 = "输入的数量小于零";
    var changeObj = slObj ;

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
       cal_tot('sl');
    }
    function cal_tot(type)
    {
      var tmpObj;
      var tot=0;
      for(i=0; i<<%=detailRows.length%>; i++)
        {
        if(type == 'sl')
          tmpObj = document.all['sl_'+i];
        else if(type == 'scsl')
          tmpObj = document.all['scsl_'+i];
        else
          return;
        if(tmpObj.value!="" && !isNaN(tmpObj.value))
          tot += parseFloat(tmpObj.value);
      }
      if(type == 'sl')
        document.all['t_sl'].value = formatQty(tot);
      if(type == 'scsl')
        document.all['t_scsl'].value = formatQty(tot);
    }
    function Import_Produce_Task(frmName,srcVar,fieldVar,deptid, curID, methodName,notin)
    {
      if (form1.deptid.value=='')
      {
        alert ("请选择完工车间");return;
      }
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
      var winName= "Import_Porduce_Task";
      paraStr = "../jit/shop_flow_select_process.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&"+deptid+"&multi=1";
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
    }
    //删除
    function buttonEventD()
    {
       if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);
    }
    function buttonEventW()
    {
      Import_Produce_Task("form1", "srcVar=drawSingleProcessTask","fieldVar=jgdid", "deptid="+form1.deptid.value);
    }
  </script>
  <%if(shopFlowBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</body>
</html>


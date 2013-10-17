<%--库存发料单从表--%>
<%
/**
 * 03.15 21:15 修改 将 workShopBean 的取值由原来的engine.project.SysConstant.BEAN_WORKSHOP改为现在的engine.project.SysConstant.BEAN_DEPT以实现取出所有部门的目地. yjg
 * 03.13 14:49 新增 新增打印单据按钮来把这张采购入库单页面上的内容打印出来. yjg
 * 03.09 11:43 新增 新增关闭按钮提供给当此页面是被报表调用时使用. yjg
 * 03.09 12:06 修改 调整了表格中的规格属性, 数量,换算数量td的排列位置. 下面的具体显示这些值的jsp scripts也做了相应调整. yjg
 * 03.09 11:43 修改 去掉定义widths[]数组的几行代码,同时修改t_hssl, t_sl的宽度为width:100% yjg
 * 2004.11.10 新增  库存量 wlc
 * 2004.12.14 去掉生产数量
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
  engine.erp.store.xixing.B_SendMaterial sendMaterialBean = engine.erp.store.xixing.B_SendMaterial.getInstance(request);
  sendMaterialBean.isout="1";
  String pageCode = "send_material_list";
  String SC_OUTSTORE_SHOW_ADD_FIELD =sendMaterialBean.SC_OUTSTORE_SHOW_ADD_FIELD;//是否显示生产发料单的附加字段
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
  location.href='send_material_list.jsp';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scdwgs_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=hsbl&fieldVar=scdwgs', obj.value,'product_change('+i+')');
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scdwgs_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=hsbl&fieldVar=scdwgs', obj.value,'product_change('+i+')');
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

function corpCodeSelect(obj)
{
  InsideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=sendMaterialBean.ONCHANGE_CORP%>)');
}
function corpNameSelect(obj)
{
  InsideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=sendMaterialBean.ONCHANGE_CORP%>)');
}

function detailtechnicschange(i){//从表选择工艺路线事件
  associateSelect(document.all['prod_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'gx_'+i, 'gylxid', eval('form1.gylxid_'+i+'.value'), '', true);
  associateSelect(document.all['prodA_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'gx2_'+i, 'gylxid', eval('form1.gylxid_'+i+'.value'), '', true);
  }
  function product_change(i){
  document.all['dmsxid_'+i].value="";
  document.all['sxz_'+i].value="";
  document.all['widths_'+i].value="";
  if(<%=SC_OUTSTORE_SHOW_ADD_FIELD.equals("1")%>)
  associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '', true);
  sumitForm(<%=sendMaterialBean.MATERIAL_ON_CHANGE%>,i)
  }

</script>
<%String retu = sendMaterialBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_STORE_UNIT_STYLE = sendMaterialBean.SC_STORE_UNIT_STYLE;//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  String KC_PRODUCE_UNIT_STYLE = sendMaterialBean.KC_PRODUCE_UNIT_STYLE;//计量单位和生产单位换算方式1=强制换算,0=仅空值时换算
  //03.15 21:15 修改 将 workShopBean 的取值由原来的engine.project.SysConstant.BEAN_WORKSHOP改为现在的engine.project.SysConstant.BEAN_DEPT以实现取出所有部门的目地. yjg
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工序
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE);//工序
  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//通过工作组id得到工作组名称
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//通过加工单明细id得到加工单号
  engine.project.LookUp drawMaterialBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DRAW_MATERIAL_DETALL);//通过生产发料单明细id得到发料单号
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp produceOutBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_PROCESS_OUT);//单据类别
  engine.project.LookUp produceUseBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_USE);//用途
  engine.project.LookUp productStockBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_STORE_STOCK);//2004.11.12库存量 wlc

  //engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = sendMaterialBean.getMaterTable();
  EngineDataSet list = sendMaterialBean.getDetailTable();
  EngineDataSet p_stock = sendMaterialBean.getProductStockData();//2004.11.12库存量 wlc

  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  HtmlTableProducer masterProducer =null;
  if(SC_OUTSTORE_SHOW_ADD_FIELD.equals("1"))
    masterProducer = sendMaterialBean.masterProducer;
  else
    masterProducer = sendMaterialBean.newmasterProducer;
  HtmlTableProducer detailProducer = sendMaterialBean.detailProducer;
  RowMap masterRow = sendMaterialBean.getMasterRowinfo();
  RowMap[] detailRows= sendMaterialBean.getDetailRowinfos();
//  RowMap[] p_stockRows = sendMaterialBean.getPorductStockInfos();//2004.11.12库存量 wlc
  String zt=masterRow.get("state");
  if(sendMaterialBean.isApprove)
  {
    workShopBean.regData(ds,"deptid");
    storeBean.regData(ds, "storeid");
    produceOutBean.regData(ds, "sfdjlbid");
    produceUseBean.regData(ds,"ytid");
   }
  boolean isEnd = sendMaterialBean.isReport || sendMaterialBean.isApprove || (!sendMaterialBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !sendMaterialBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(sendMaterialBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("9") ? "审批中" : (zt.equals("2") ? "记帐" : "未审核"));
  boolean isAdd = sendMaterialBean.isDetailAdd;
  String Type = sendMaterialBean.drawType;
  boolean isDrawType = Type.equals("1");//1为生产发料单
  String show = isDrawType? "发料单" : "退料单";
  String showFieldTitle = isDrawType ? detailProducer.getFieldInfo("jgdmxid").getFieldname() : "发料单号";
  String SYS_PRODUCT_SPEC_PROP = sendMaterialBean.SYS_PRODUCT_SPEC_PROP;
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
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab"><%=show%>(<%=title%>)
            <%
            //当是新增的时候不显示出上一笔下一笔
              if (!sendMaterialBean.masterIsAdd())
              {
                ds.goToInternalRow(sendMaterialBean.getMasterRow());
                boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
                if (!isAtFirst)
              {%>
              <a href="#" title="到上一笔(ALT+Z)" onClick="sumitForm(<%=sendMaterialBean.PRIOR%>)">&lt</a>
              <pc:shortcut key='z' script='<%="sumitForm("+sendMaterialBean.PRIOR+")"%>'/>
             <%}
               if (!isAtLast)
              {%>
              <a href="#" title="到上一笔(ALT+X)" onClick="sumitForm(<%=sendMaterialBean.NEXT%>)">&gt</a>
              <pc:shortcut key='x' script='<%="sumitForm("+sendMaterialBean.NEXT+")"%>'/>
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
                  corpBean.regData(ds,"dwtxid");
                 if(!isEnd){
                   workGroupBean.regConditionData(ds,"deptid");
                   storeAreaBean.regConditionData(ds, "storeid");
                   personBean.regConditionData(ds, "deptid");
                 }
                 %>
                  <tr>
                  <td  noWrap class="tdTitle">发料单号</td>
                  <td noWrap class="td"><input type="text" name="drawcode" value='<%=masterRow.get("drawcode")%>' maxlength='<%=ds.getColumn("drawcode").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle">发料日期</td>
                  <td noWrap class="td"><input type="text" name="drawdate" value='<%=masterRow.get("drawdate")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.drawdate);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "if(form1.storeid.value!='"+masterRow.get("storeid")+"'){sumitForm("+sendMaterialBean.ONCHANGE+");}";%>
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
                       String submit = "sumitForm("+sendMaterialBean.ONCHANGE+")";
                     %>
                    <pc:select name="sfdjlbid" addNull="0" style="width:110" onSelect="<%=submit%>">
                    <%=produceOutBean.getList(masterRow.get("sfdjlbid"))%> </pc:select>
                     <input type="hidden" name="srlx" value='<%=sfdjldRow.get("srlx")%>'>
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

                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("ytid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+produceUseBean.getLookupName(masterRow.get("ytid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="ytid" addNull="1" style="width:110">
                      <%=produceUseBean.getList(masterRow.get("ytid"))%> </pc:select>
                    <%}%>
                  </td>
                  </tr>
                   <tr>
                  <td  noWrap class="tdTitle">内部往来单位</td><%--内部往来单位--%>
                  <%RowMap corpRow = corpBean.getLookupRow(masterRow.get("dwtxid"));%>
                  <td  noWrap class="td" colspan="3"><input type="text" name="dwdm" value='<%=corpRow.get("dwdm")%>' style="width:60" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpCodeSelect(this);" <%=readonly%>>
                  <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>' >
                  <input type="text" name="dwmc" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:180" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpNameSelect(this);" <%=readonly%>>
                  <%if(!isEnd){%>
                  <img style='cursor:hand' src='../images/view.gif' border=0 onClick="InsideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=sendMaterialBean.ONCHANGE_CORP%>)');"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';sumitForm(<%=sendMaterialBean.ONCHANGE_CORP%>)">
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
                   <td noWrap class="tdTitle">工作组2</td>
                   <td noWrap class="td">
                   <%if(isEnd) out.print("<input type='text' value='"+workGroupBean.getLookupName(masterRow.get("sc__gzzid"))+"' style='width:110' class='edline' readonly>");
                   else {%>
                   <pc:select name="sc__gzzid" addNull="1" style="width:110" >
                   <%=workGroupBean.getList(masterRow.get("sc__gzzid"),"deptid",masterRow.get("deptid"))%> </pc:select>
                   <%}%>
                   </td>
                   <td noWrap class="tdTitle">总工费2</td>
                    <td noWrap class="td"><input type="text" name="zgf2" value='<%=masterRow.get("zgf2")%>' maxlength='<%=ds.getColumn("zgf2").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  </tr>
                  <tr>
                   <td noWrap class="tdTitle">班次</td>
                    <td noWrap class="td"><input type="text" name="bc" value='<%=masterRow.get("bc")%>' <%=edClass%> maxlength='<%=ds.getColumn("bc").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                   <td></td>
                    <td></td>
                     <td></td>
                      <td></td>
                       <td></td>
                        <td></td>
                  </tr>
                <%}%>
                <%//*打印用户自定义信息*/
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
                //2004-5-2 16:43 为给明细数据集加入分页功能
                String count = String.valueOf(list.getRowCount());
                int iPage = 30;
                String pageSize = String.valueOf(iPage);
                %>
                 <tr> <td colspan="8" noWrap class="td">
                   <pc:navigator id="navigator" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+sendMaterialBean.TURNPAGE+")"%>' disable='<%=sendMaterialBean.isRepeat.equals("1") ? "1" : "0"%>'/>
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
                          <input name="image" class="img" type="image" title="新增(ALT+A)" onClick="buttonEventA()" src="../images/add_big.gif" border="0">
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
                          <td height='20' nowrap>计量单位</td>
                          <td nowrap><%=detailProducer.getFieldInfo("drawbignum").getFieldname()%></td>
                          <td height='20' nowrap>换算单位</td>
                          <td height='20' nowrap>单价</td>
                          <td height='20' nowrap>金额</td>
                      <%--    <td nowrap><%=detailProducer.getFieldInfo("producenum").getFieldname()%></td>
                          <td height='20' nowrap>生产单位</td>  --%>
                          <td height='20' nowrap>库存量</td>
                          <%if(SC_OUTSTORE_SHOW_ADD_FIELD.equals("1")){%>
                          <td nowrap>工艺路线</td>
                          <td nowrap>工序</td>
                          <td nowrap>计件单价</td>
                          <td nowrap>计件工资</td>
                          <td nowrap>工序2</td>
                          <td nowrap>计件单价2</td>
                          <td nowrap>计件工资2</td>
                          <%}%>
                          <td nowrap><%=detailProducer.getFieldInfo("kwid").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("memo").getFieldname()%></td>
                          <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      propertyBean.regData(list,"dmsxid");
                      processBean.regData(list,"jgdmxid");
                      drawMaterialBean.regData(list, "backDrawID");
                      productStockBean.regData(p_stock,new String[]{ "fgsid","storeid","cpid"}); //2004.11.12库存量 wlc
                      //technicsRouteBean.regData(list, "gylxid");
                      if(!isEnd){
                        technicsRouteBean.regConditionData(list,"cpid");
                       //technicsNameBean.regConditionData(list,"gylxid");
                      }
                      BigDecimal t_jjgz = new BigDecimal(0),t_jjgz2 = new BigDecimal(0);
                      BigDecimal t_sl = new BigDecimal(0), t_scsl = new BigDecimal(0), t_hssl = new BigDecimal(0),t_drawsum = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      int min = navigator.getRowMin(request);
                      int max = navigator.getRowMax(request);
                      //类中取得笔每一页的数据范围
                      sendMaterialBean.min = min;
                      sendMaterialBean.max = max > detailRows.length-1 ? detailRows.length-1 : max;
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
                      propertyBean.regData((String[])dmsxidList.toArray(new String[dmsxidList.size()]));//02.15 新增 新增注册dmsxid属性id因为不注册下面页面就会出错 yjg

                      list.goToRow(min);
                      //2004-5-2 16:43 修改 将原来的i<detailRows.length修改成现在的i<=max && i<list.getRowCount();
                      for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        //2004.11.12库存量
                //        String fgs = p_stockRows[i].get("fgsid")==null ? "0":p_stockRows[i].get("fgsid");
                //        String sto = p_stockRows[i].get("storeid")==null? "-1":p_stockRows[i].get("storeid");
                //        String cp  = p_stockRows[i].get("cpid")==null ? "-1":p_stockRows[i].get("cpid");
                        String sl = detail.get("drawnum");
                        if(sendMaterialBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String hssl = detail.get("drawbignum");
                        if(sendMaterialBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String scsl = detail.get("producenum");
                        if(sendMaterialBean.isDouble(scsl))
                          t_scsl = t_scsl.add(new BigDecimal(scsl));
                        String jjgz = detail.get("jjgz");
                        if(sendMaterialBean.isDouble(jjgz))
                          t_jjgz = t_jjgz.add(new BigDecimal(jjgz));
                        String jjgz2 = detail.get("jjgz2");
                       if(sendMaterialBean.isDouble(jjgz2))
                          t_jjgz2 = t_jjgz2.add(new BigDecimal(jjgz2));
                       String drawsum = detail.get("drawsum");
                       if(sendMaterialBean.isDouble(drawsum))
                          t_drawsum = t_drawsum.add(new BigDecimal(drawsum));
                        String dmsxid = detail.get("dmsxid");
                        String sx = propertyBean.getLookupName(dmsxid);
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                        String kwName = "kwid_"+i;
                        String dmsxidName = "dmsxid_"+i;
                        String gxName = "gx_"+i;
                        String gylxidName = "gylxid_"+i;
                        String gx = detail.get("gylxid").length()>0 ? detail.get("gx") : "";
                        String gxName2 = "gx2_"+i;
                        String gx2 = detail.get("gylxid").length()>0 ? detail.get("gx2") : "";
                        String jgdmxid=detail.get("jgdmxid");
                        boolean isimport = !jgdmxid.equals("");//引入加工单，从表产品编码当前行不能修改
                        String test2 = "getJjdjValue2("+i+");";
                    %>
                      <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(!isEnd && !isimport){%>
                          <input type="hidden" name="mutibatch_<%=i%>" value="" onchange="sumitForm(<%=sendMaterialBean.MATCHING_BATCH%>,<%=i%>)">
                          <img style='cursor:hand' title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=scydw_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=isprops_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=isprops&fieldVar=hsbl&fieldVar=scdwgs','&storeid='+form1.storeid.value,'product_change(<%=i%>)')">
                          <%}if(!isEnd){%>
                      <!--input name="image" class="img" type="image" title="配批号" onClick="if(form1.cpid_.value==''){alert('请输入产品');return;}if(form1.storeid.value==''){alert('请输入产品'); return;}BatchMultiSelect('form1','srcVar=mutibatch_&cpid='+form1.cpid_.value+'&storeid='+form1.storeid.value+'&dmsxid='+form1.dmsxid_.value)" src="../images/edit.old.gif" border="0"!-->
                      <input name="image" class="img" type="image" title="复制当前行" onClick="if(form1.cpid_<%=i%>.value==''){alert('请输入产品');return;}sumitForm(<%=sendMaterialBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除第<%=(i+1)%>条记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
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
                        <input <%=detailClass%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if(form1.isprops_<%=i%>.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="batchno_<%=i%>" name="batchno_<%=i%>" value='<%=detail.get("batchno")%>' maxlength='<%=list.getColumn("batchno").getPrecision()%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请输入产品');return;}if(form1.storeid.value==''){alert('请输入产品'); return;}BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+form1.cpid_<%=i%>.value+'&storeid='+form1.storeid.value+'&row='+<%=i%>+'&dmsxid='+form1.dmsxid_<%=i%>.value+'&ph='+form1.batchno_<%=i%>.value, true, <%=i%>, true,'methodName=sl_onchange(<%=i%>, false)')" <%=readonly%>>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawnum_<%=i%>" name="drawnum_<%=i%>" value='<%=detail.get("drawnum")%>' maxlength='<%=list.getColumn("drawnum").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)"<%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly>  </td>
                        <td class="td" nowrap> <input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawbignum_<%=i%>" name="drawbignum_<%=i%>" value='<%=detail.get("drawbignum")%>' maxlength='<%=list.getColumn("drawbignum").getPrecision()%>' onchange="hssl_onchange(<%=i%>, false)"<%=readonly%>>  </td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly>  </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="drawprice_<%=i%>" name="drawprice_<%=i%>" value='<%=detail.get("drawprice")%>' onchange="drawprice_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="drawsum_<%=i%>" name="drawsum_<%=i%>" value='<%=detail.get("drawsum")%>' maxlength='<%=list.getColumn("drawsum").getPrecision()%>' onchange="je_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="producenum_<%=i%>" name="producenum_<%=i%>" value='<%=detail.get("producenum")%>' maxlength='<%=list.getColumn("producenum").getPrecision()%>' onchange="producesl_onchange(<%=i%>)" <%=readonly%>>

                        <input type="hidden" class=ednone onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly>
                        <td class="td" align="center" nowrap><input type="text" name="kcsl_<%=i%>" id="kcsl_<%=i%>" class=ednone_r onKeyDown="return getNextElement();" value='<%=productStockBean.getLookupName(new String[]{masterRow.get("fgsid"),masterRow.get("storeid"),detail.get("cpid")})%>' readonly></td> <%-- //2004.11.12库存量 --%>
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
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        </td><td class="td">&nbsp;</td>
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
                        <td class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td"><input id="t_drawsum" name="t_drawsum" type="text" class="ednone_r" style="width:100%" value='<%=t_drawsum%>' readonly></td>
                        <input id="t_scsl" name="t_scsl" type="hidden" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly>
                        <td align="right" class="td">&nbsp;</td>
                        <td align="right" class="td">&nbsp;</td>
                        <td align="right" class="td">&nbsp;</td>
                      </tr>
                    </table>
                       </div>
                    </td>
                </tr>
                                <tr>
          <td noWrap class="tdTitle">备注</td>
          <td colspan="8" class="td"><textarea name="memo" cols="110" rows="2" ><%=masterRow.get("memo")%></textarea></td>
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
            <td  class="td"><b>登记日期:</b><%=masterRow.get("createDate")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("creator")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
        <%--    <%if(!isEnd && Type.equals("1")){%>
             <input type="hidden" name="selectProcess" value=""><input type="hidden" name="selectMaterail" value="" onchange="sumitForm(<%=sendMaterialBean.SELECT_MATERAIL%>)">
             <input name="btnback" type="button" class="button" title="引加工单(ALT+Q)" value="引加工单(Q)" style="width:80"
              onClick="buttonEventQ()"><pc:shortcut key="q" script='<%="buttonEventQ()"%>'/>
              <input name="btnback" type="button" class="button" title="引加工单物料(ALT+Z)" value="引加工单物料(Z)" style="width:110"
              onClick="buttonEventZ()">
              <pc:shortcut key="z" script='<%="buttonEventZ()"%>'/>
             <%}%>  --%>
             <%if(!isEnd && !Type.equals("1")){%>
             <input type="hidden" name="selectReceive" value="" onchange="sumitForm(<%=sendMaterialBean.IMPORT_RECEIVE_ADD%>)">
             <input name="btnback" type="button" class="button" title="引发料单(ALT+W)" value="引发料单(W)" style="width:80"
              onClick="buttonEventW()">
              <pc:shortcut key="w" script='<%="buttonEventW()"%>'/>
             <%}%>
              <%if(!isEnd){%>
              <%--  <input type="hidden" name="scanValue" value="">
                <input type="button" class="button" title="盘点机(E)" value="盘点机(E)" style='width:65' onClick="buttonEventE(false)">
                <pc:shortcut key="e" script='<%="buttonEventE(false)"%>'/>
                 <%--input type="button" class="button" title="新盘点机(R)" value="新盘点机(R)" style='width:80' onClick="buttonEventE(true)">
                <pc:shortcut key="r" script='<%="buttonEventE(true)"%>'/--%>
                <input name="button2" type="button" class="button" title="删除数量为空行(ALT+X)" value="删除数量为空行(X)" style='width:120' onClick="sumitForm(<%=sendMaterialBean.DELETE_BLANK%>);">
                <pc:shortcut key="x" script='<%="sumitForm("+sendMaterialBean.DELETE_BLANK+")"%>'/>
                <input name="button2" type="button" class="button" title="保存添加(ALT+N)" value="保存添加(N)" style="width:80" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
                <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button"  title="保存(ALT+S)" value="保存(S)" style="width:50" onClick="sumitForm(<%=Operate.POST%>);">
              <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
            <%}%>
              <%if(isCanDelete && !sendMaterialBean.isReport){%><input name="button3" type="button" class="button" title="删除(ALT+D)" value="删除(D)" onClick="buttonEventD();" style="width:50">
                <pc:shortcut key="d" script="buttonEventD()"/>
             <%}%>
              <%if(!sendMaterialBean.isApprove && !sendMaterialBean.isReport){%>
                <input name="btnback" type="button" class="button" title="返回(ALT+C)" value="返回(C)" onClick="backList();" style="width:50">
                <pc:shortcut key="c" script='<%="backList()"%>'/>
            <%}%>
               <%--03.09 11:43 新增 新增关闭按钮提供给当此页面是被报表调用时使用. yjg--%>
                <%if(sendMaterialBean.isReport){%><input name="btnback" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)" onClick="window.close()" style="width:50">
                <pc:shortcut key="t" script='<%="window.close()"%>'/>
               <%}%>
                <%--03.13 14:49 新增 新增打印单据按钮来把这张采购入库单页面上的内容打印出来. yjg--%>
              <input type="button" class="button" title="打印(ALT+P)" value="打印(P)" onclick="buttonEventP()" style="width:50">
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
  sendMaterialBean.adjustInputSize(new String[]{"cpbm","product", "jldw", "batchno", "drawnum","drawbignum","hsdw","sxz", "memo","scydw","kcsl","drawprice","drawsum"},  "form1", sendMaterialBean.max-min+1, min)
  : sendMaterialBean.adjustInputSize(new String[]{"cpbm","product", "jldw", "batchno", "drawnum","drawbignum","hsdw","sxz", "memo","scydw","kcsl","drawprice","drawsum"},  "form1", sendMaterialBean.max-min+1, min)%>
}
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    min = <%=sendMaterialBean.min%>;
    max = <%=sendMaterialBean.max%>;
    newmax = <%=detailRows.length%> >= max+1 ?  max+1 : <%=detailRows.length%>;
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
    var width = document.all['widths_'+i];//规格属性的宽度
    var hsblObj = document.all['truebl_'+i];
    var djObj = document.all['drawprice_'+i];//单价
    var jeObj = document.all['drawsum_'+i];//金额
    var obj = slObj;
    var widthObj = document.all['widths_'+i];//规格属性的宽度
    var showText = "输入的数量非法";
    var showText2 ="输入的数量小于零";
    var scdwgsObj = document.all['scdwgs_'+i];//生产公式
     if(obj.value=="")
       return;
     if(isNaN(obj.value))
     {
       alert(showText);
       obj.focus();
       return;
     }
     if(slObj.value<0){
       alert(showText2);
       return;
     }
     cal_tot('sl');
     cal_tot('hssl');
     //if(isShow){
     //cal_tot('jjgz');
       //cal_tot('jjgz2');
     //}
     if('<%=bjfs%>'=="0")//报价方式为按计量单位报价时判断
      {
        if(slObj.value=="")
        return;
        if(isNaN(slObj.value)){
          alert("输入的数量非法");
          slObj.focus();
          return;
        }
       if(hsblObj.value!="" && !isNaN(hsblObj.value) && hsblObj.value!="0")
         hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
       else
         hsslObj.value=formatQty(parseFloat(slObj.value)/1);
       cal_tot('hssl');
       cal_tot('sl');
       if(isNaN(djObj.value))
       {
         alert('输入的单价非法');
         return;
       }
       if(djObj.value=="")
         return;
       if(slObj.value!="" && !isNaN(slObj.value)){
         jeObj.value = formatQty(parseFloat(slObj.value) * parseFloat(djObj.value));
       }
       else{
         jeObj.value='';
       }
     }
     else//按换算单位报价的判断和计算
     {
      if(slObj.value=="")
         return;
       if(isNaN(slObj.value)){
         alert("输入的换算数量非法");
         slObj.focus();
         return;
       }
       if(hsblObj.value!="" && !isNaN(hsblObj.value) && hsblObj.value!="0")
        hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
       else
        hsslObj.value=formatQty(parseFloat(slObj.value)/1);
       cal_tot('sl');
       cal_tot('hssl');

       if(djObj.value=="")
         return;
       if(hsslObj.value!="" && !isNaN(hsslObj.value)){
         jeObj.value = formatQty(parseFloat(hsslObj.value) * parseFloat(djObj.value));
       }
         else{
         jeObj.value='';
       }
     }
//     if(scslObj.value!="" && '<%=KC_PRODUCE_UNIT_STYLE%>'!='1')
//       return;
     if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
       scslObj.value= hsslObj.value;
     else if(!isBigUnit)
       scslObj.value = formatQty(parseFloat(hsslObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
     cal_tot('scsl');
  }
  function hssl_onchange(i, isBigUnit)
  {
    var oldhsblObj = document.all['hsbl_'+i];
    var sxzObj = document.all['sxz_'+i];
    unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'newhssl_onchange('+i+','+isBigUnit+')');
  }
  function newhssl_onchange(i, isBigUnit)
  {
    var slObj = document.all['drawnum_'+i];
    var hsslObj = document.all['drawbignum_'+i];
    var scslObj = document.all['producenum_'+i];
    var width = document.all['widths_'+i];//规格属性的宽度
    var hsblObj = document.all['truebl_'+i];
    var djObj = document.all['drawprice_'+i];//单价
    var jeObj = document.all['drawsum_'+i];//金额
    var obj =  hsslObj ;
    var widthObj = document.all['widths_'+i];//规格属性的宽度
    var showText = "输入的换算数量非法" ;
    var showText2 = "输入的换算数量小于零" ;
    var scdwgsObj = document.all['scdwgs_'+i];//生产公式
     if(obj.value=="")
       return;
     if(isNaN(obj.value))
     {
       alert(showText);
       obj.focus();
       return;
     }
     if(hsslObj.value<0){
        alert(showText2);
        return;
     }
     cal_tot('sl');
     cal_tot('hssl');
     //if(isShow){
     //cal_tot('jjgz');
       //cal_tot('jjgz2');
     //}
           if('<%=bjfs%>'=="0")//报价方式为按计量单位报价时判断
        {
        if(hsslObj.value=="")
          return;
        if(isNaN(hsslObj.value)){
          alert("输入的换算数量非法");
          hsslObj.focus();
          return;
        }
        if(hsblObj.value!="" && !isNaN(hsblObj.value)&& hsblObj.value!="0")
          slObj.value= formatQty(parseFloat(hsslObj.value) * parseFloat(hsblObj.value));
        else
          slObj.value=formatQty(parseFloat(hsslObj.value) * 1);
        cal_tot('sl');
        cal_tot('hssl');
        if(isNaN(djObj.value))
        {
          alert('输入的单价非法');
          return;
        }
        if(djObj.value=="")
          return;
        if(slObj.value!="" && !isNaN(slObj.value)){
          jeObj.value = formatQty(parseFloat(slObj.value) * parseFloat(djObj.value));
        }
        else{
          jeObj.value='';
        }
      }
      else//按换算单位报价的判断和计算
      {
        if(hsslObj.value=="")
          return;
        if(isNaN(hsslObj.value)){
          alert("输入的数量非法");
          hsslObj.focus();
          return;
        }

        if(hsblObj.value!="" && !isNaN(hsblObj.value) && hsblObj.value!="0")
      slObj.value= formatQty(parseFloat(hsslObj.value) * parseFloat(hsblObj.value));
        else
          slObj.value=formatQty(parseFloat(hsslObj.value) * 1);
        cal_tot('hssl');
        cal_tot('sl');

        if(djObj.value=="")
          return;
        if(hsslObj.value!="" && !isNaN(hsslObj.value)){
          jeObj.value = formatPrice(parseFloat(hsslObj.value) * parseFloat(djObj.value));
        }
        else{
          jeObj.value='';
        }
      }
     //if(hsblObj.value!="" && !isNaN(hsblObj.value) && hsblObj.value!="0")
     //  slObj.value= formatQty(parseFloat(hsslObj.value)*parseFloat(hsblObj.value));
    // else
    //      slObj.value=formatQty(parseFloat(hsslObj.value)*1);
     if(scslObj.value!="" && '<%=KC_PRODUCE_UNIT_STYLE%>'!='1')
       return;
     if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
       scslObj.value= hsslObj.value;
     else if(!isBigUnit)
       scslObj.value = formatQty(parseFloat(hsslObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
     cal_tot('scsl');
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

    var scdwgsObj = document.all['scdwgs_'+i];//生产公式
    var widthObj = document.all['widths_'+i];//规格属性的宽度
    if(scslObj.value=="")
      return;
    if(isNaN(scslObj.value))
    {
      alert('输入的生产数量非法');
      scslObj.focus();
      return;
    }
    if(scslObj.value<=0)
    {
      alert('输入的生产数量小于零');
      scslObj.focus();
      return;
    }

    if(hsslObj.value!="" && '<%=KC_PRODUCE_UNIT_STYLE%>'!='1'){//生产数量与数量是否强制转换
      return;
    }
    if(hsblObj.value!="" && !isNaN(hsblObj.value) && hsblObj.value!="0")
      slObj.value= formatQty(parseFloat(hsslObj.value)*parseFloat(hsblObj.value));
    else
      slObj.value=formatQty(parseFloat(hsslObj.value)*1);
    if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
      hsslObj.value= scslObj.value;
    else
      hsslObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthObj.value)/parseFloat(scdwgsObj.value));
    if(hsblObj.value!="" && !isNaN(hsblObj.value) && hsblObj.value!="0")
      slObj.value= formatQty(parseFloat(hsslObj.value)*parseFloat(hsblObj.value));
    else
      slObj.value=formatQty(parseFloat(hsslObj.value)*1);
    cal_tot('scsl');
    cal_tot('sl');
    cal_tot('hssl');
  }
  function drawprice_onchange(i, isBigUnit)
  {
    var drawnumObj = document.all['drawnum_'+i];
    var drawbignumObj = document.all['drawbignum_'+i];
    //var hsblObj = document.all['truebl_'+i];
    var drawpriceObj = document.all['drawprice_'+i];
    var drawsumObj = document.all['drawsum_'+i];
    //var sjrklObj = document.all['sjrkl_'+i];
    var bjfs = <%=bjfs%>;
    //var hlObj = form1.hl;
  if(drawpriceObj.value==""){
    drawsumObj.value='';
    return;
  }
  if(isNaN(drawpriceObj.value)){
    alert("输入的单价非法");
    drawpriceObj.focus();
    return;
  }
  if(drawpriceObj.value<0){
    alert("不能输入小于等于零的数")
        return;
  }
  if(bjfs==0)
  {
    if(drawnumObj.value!="" && !isNaN(drawnumObj.value)){
      drawsumObj.value = formatQty(parseFloat(drawnumObj.value) * parseFloat(drawpriceObj.value));
    }
    else{
      drawsumObj.value='';
    }
  }
  else
  {
    if(drawbignumObj.value!="" && !isNaN(drawbignumObj.value)){
      drawsumObj.value = formatQty(parseFloat(drawbignumObj.value) * parseFloat(drawpriceObj.value));
    }
    else{
      drawsumObj.value='';
    }
  }
  cal_tot('drawsum');
  cal_tot('sjrkl');
  }
  function je_onchange(i, isBigUnit)
{
 var slObj = document.all['drawnum_'+i];
 var djObj = document.all['drawprice_'+i];
 var jeObj = document.all['drawsum_'+i];
 var hsslObj = document.all['drawbignum_'+i];
 var hsblObj = document.all['truebl_'+i];
 var bjfs = <%=bjfs%>;
 var obj = jeObj;
 var showText = isBigUnit ? "输入的原币金额非法" : "输入的金额非法";
 var showText2 = isBigUnit ? "输入的原币金额小于零" : "输入的金额小于零";
 var changeObj = jeObj
 if(obj.value=="")
   return;
 if(isNaN(obj.value))
 {
   alert(showText);
   obj.focus();
   return;
 }
 if(!isBigUnit){
   if(bjfs==0){
     if(slObj.value!="" && !isNaN(slObj.value && slObj.value!="0")){
       djObj.value = Math.abs(formatPrice(parseFloat(jeObj.value) / parseFloat(slObj.value)));
       if(jeObj.value*slObj.value<0)
       {
         hsslObj.value=formatQty(-1*hsslObj.value);
         slObj.value=formatQty(-1*slObj.value);
       }
     }
   }
   else{

     if(hsslObj.value!="" && !isNaN(hsslObj.value && hsslObj.value!="0"))
     {
       djObj.value =Math.abs(formatPrice(parseFloat(jeObj.value) / parseFloat(hsslObj.value)));
       if(jeObj.value*slObj.value<0)
       {
         hsslObj.value=formatQty(-1*hsslObj.value);
         slObj.value=formatQty(-1*slObj.value);
       }
     } //Math.abs
   }
 }
 cal_tot('je');
    }

  function cal_tot(type)
  {
      var tmpObj;
      var temObj1;
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
        else if(type == 'drawsum')
          tmpObj = document.all['drawsum_'+i];
        else
          return;
        if(tmpObj.value!="" && !isNaN(tmpObj.value))
          tot += parseFloat(tmpObj.value);
      }
      if(type == 'sl'){
        document.all['t_sl'].value = formatQty(tot);
      }
      if(type == 'drawsum'){
        document.all['t_drawsum'].value = formatQty(tot);
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
      paraStr = "../store/draw_single_process.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar
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
      paraStr = "../store/select_processmaterail.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar
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
     *@para frmName form表格.指示出srcVar等是在那个表单里的.在后面的操作里就是对这个表单里的元素进行操作的.
     *@para srcVar frmName里的元素.此值是可能是一组表单元素.主要是让后续的操作中得到这些元素,然后再给本页面中的srcVar赋值.如:把zl给sl_0
     *@para whichCall 指出调用此js函数是的谁.在本页面下.图标调用和批号输入框两种.
     *@para i 指出数据行.主要是要组成类似sl_i这样的srcVar
     *@para isAdjustKwid 是否须要调整kwid.因为在销售出库单中当wzsxid不为空的时候就不允许修改kwid.须要调整则为true.
     *@para methodName 本页面类的方法.
     */
    function BatchMultiSelect(frmName, srcVar, whichCall, i, isAdjustKwid, methodName,notin)
    {
      //whichCall:参数表示调用配批号是由左边的图标事件触发的还是批号输入框事件触发的.这两个事件各自己对应的不同的类处理
      //输入框事件whichCall:true及isMultiSelect=0.图标事件:false及isMultiSelect=1.
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
    function transferScan(isNew)//调用盘点机
    {
      var scanValueObj = form1.scanValue;
      scanValueObj.value = scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "self_gain_edit.jsp", "IT3CW32d.DLL")%>');//得到包含产品编码和批号的字符串
      //alert(scanValueObj.value);
      if(isNew)
        sumitForm(<%=sendMaterialBean.NEW_TRANSFERSCAN%>);
      else
        sumitForm(<%=sendMaterialBean.TRANSFERSCAN%>);
    }
    function buttonEventE(isNew)//旧盘点机调用
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
      if (form1.srlx.value=="" ){
        alert("请先指定单据类别");
        return;
      }
      ReceiveSelProcess('form1','srcVar=selectProcess','fieldVar=jgdid',form1.deptid.value,'0',form1.storeid.value, 'sumitForm(<%=sendMaterialBean.RECEIVE_SEL_PROCESS%>)');
    }
    //引加工单物料
    function buttonEventZ()
    {
      if(form1.deptid.value=='')
      {
        alert('请选择部门');
        return;
      }
      if(form1.storeid.value=='')
      {
        alert('请选择仓库');
        return;
      }
      if (form1.srlx.value=="" ){
        alert("请先指定单据类别");
        return;
      }
      SelectProcessMaterail('form1','srcVar=selectMaterail','fieldVar=jgdwlid',form1.deptid.value,'0',form1.storeid.value,'sumitForm(<%=sendMaterialBean.SELECT_MATERAIL%>)');
    }
    //退料单引发料单
    function buttonEventW()
    {
      if(form1.storeid.value=="")
      {
        alert('请选择仓库');
        return;
      }
      BackMaterail('form1','srcVar=selectReceive&storeid='+form1.storeid.value+'&isout=0');
    }
    //删除
    function buttonEventD()
    {
      if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);
        }
  //打印
    function buttonEventP()
    {
      location.href='../pub/pdfprint.jsp?code=send_material_edit_bill&operate=<%=sendMaterialBean.PRINT_BILL%>&a$sfdjid=<%=masterRow.get("drawid")%>&src=../store_xixing/send_material_edit.jsp';
    }
    //新增
    function buttonEventA()
    {
      if(form1.storeid.value==''){alert('请选择仓库');return;}sumitForm(<%=Operate.DETAIL_ADD%>);
        }

  function showDetail(masterRow){
    selectRow();
  }
</script>
<%if(sendMaterialBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
 </BODY>
</Html>
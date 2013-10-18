<%--大发自制收货单编辑页面从表--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
  String op_copyadd = "op_copyadd";
%><%
  engine.erp.store.B_NewSelfGain newSelfGainBean = engine.erp.store.B_NewSelfGain.getInstance(request);
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
  location.href='newself_gain_list.jsp';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=scydw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isbatchno_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=hsbl&fieldVar=scdwgs&fieldVar=isbatchno', obj.value,'product_change('+i+')');
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=scydw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isbatchno_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=hsbl&fieldVar=scdwgs&fieldVar=isbatchno', obj.value,'product_change('+i+')');
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
}
function M_productCodeSelect(obj, i)
{
   ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=wlcpid_'+i+'&srcVar=wlcpbm_'+i+'&srcVar=wlproduct_'+i+'&srcVar=wljldw_'+i+'&srcVar=wlscydw_'+i+'&srcVar=wlscdwgs_'+i+'&srcVar=wlisprops_'+i,
                    'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops', obj.value);
}
function M_productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=wlcpid_'+i+'&srcVar=wlcpbm_'+i+'&srcVar=wlproduct_'+i+'&srcVar=wljldw_'+i+'&srcVar=wlscydw_'+i+'&srcVar=wlscdwgs_'+i+'&srcVar=wlisprops_'+i,
                    'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops', obj.value);
}
function M_propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=wldmsxid_'+i+'&srcVar=wlsxz_'+i,
                       'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'M_propertyChange('+i+')');
   }
  function detailtechnicschange(i){//从表选择工艺路线事件
    associateSelect(document.all['prod_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'gx_'+i, 'gylxid', eval('form1.gylxid_'+i+'.value'), '', true);
    associateSelect(document.all['prodA_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'gx2_'+i, 'gylxid', eval('form1.gylxid_'+i+'.value'), '', true);
    associateSelect(document.all['prodB_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'gx3_'+i, 'gylxid', eval('form1.gylxid_'+i+'.value'), '', true);
  }
  function typeschange(i){//从表选择工艺路线事件

    associateSelect(document.all['prodC_'+i], '<%=engine.project.SysConstant.BEAN_PACKAGE%>', 'package_id_'+i, 'funditemid', eval('form1.funditemid_'+i+'.value'), '', true);
  }
  function product_change(i){
    document.all['dmsxid_'+i].value="";
    document.all['sxz_'+i].value="";
    document.all['widths_'+i].value="";
    associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '', true);
  }
  function deptChange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_WORK_GROUP%>', 'gzzid', 'deptid', eval('form1.deptid.value'), '',true);
   associateSelect(document.all['prod2'], '<%=engine.project.SysConstant.BEAN_WORK_GROUP%>', 'sc__gzzid', 'deptid', eval('form1.deptid.value'), '',true);
   associateSelect(document.all['prod3'], '<%=engine.project.SysConstant.BEAN_WORK_GROUP%>', 'sc__gzzid2', 'deptid', eval('form1.deptid.value'), '',true);
   associateSelect(document.all['prod1'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'handleperson', 'deptid', eval('form1.deptid.value'), '',true);
  }
</script>
<%String retu = newSelfGainBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_STORE_UNIT_STYLE = newSelfGainBean.SC_STORE_UNIT_STYLE;//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  String KC_PRODUCE_UNIT_STYLE = newSelfGainBean.KC_PRODUCE_UNIT_STYLE;//计量单位和生产单位换算方式1=强制换算,0=仅空值时换算
  String SYS_PRODUCT_SPEC_PROP = newSelfGainBean.SYS_PRODUCT_SPEC_PROP;
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工序
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE);//工序
  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//通过工作组id得到工作组名称
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
  engine.project.LookUp packageBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PACKAGE);//用途
  engine.project.LookUp typeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PACKAGETYPE);//物资规格属性
  //engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = newSelfGainBean.getMaterTable();
  EngineDataSet list = newSelfGainBean.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHandwork = loginBean.getSystemParam("KC_HANDIN_STOCK_BILL").equals("1");//得到是否可以手工增加的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  HtmlTableProducer masterProducer = newSelfGainBean.masterProducer;
  HtmlTableProducer detailProducer = newSelfGainBean.detailProducer;
  RowMap masterRow = newSelfGainBean.getMasterRowinfo();
  RowMap[] detailRows= newSelfGainBean.getDetailRowinfos();
  String zt=masterRow.get("state");
  String creatorID = masterRow.get("creatorID");//得到该单据的制单员id
  String loginId = newSelfGainBean.loginId;
  if(newSelfGainBean.isApprove)
  {
    workShopBean.regData(ds,"deptid");
    storeBean.regData(ds, "storeid");
    produceInBean.regData(ds, "sfdjbid");
  }
  boolean isEnd = newSelfGainBean.isReport || newSelfGainBean.isApprove || (!newSelfGainBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !newSelfGainBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete)
                        && loginId.equals(creatorID);//没有结束,在修改状态,并有删除权限,2004-08-04 并且登陆人等于制单人
  isEnd = isEnd
          || !(newSelfGainBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit))
          || !loginId.equals(creatorID);//2004-08-04 新增 只有当前登陆人是制单人的时候才可以修改 yjg


  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("9") ? "审批中" : (zt.equals("2") ? "记帐" : "未审核"));
  boolean isAdd = newSelfGainBean.isDetailAdd;
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');onload();">
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
            <td class="activeVTab">自制收货单(<%=title%>)
                        <%--03.19 20:32 新增 新增为方便不退出此页面就可以直接打印xxx_top.jsp上列出的单据而加的上一笔,下一笔按钮 yjg--%>
              <%
              //当是新增的时候不显示出上一笔下一笔
              if (!newSelfGainBean.masterIsAdd())
              {
                ds.goToInternalRow(newSelfGainBean.getMasterRow());
                boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
                if (!isAtFirst)
              {%>
              <a href="#" title="到上一笔(ALT+Z)" onClick="sumitForm(<%=newSelfGainBean.PRIOR%>)">&lt</a>
              <pc:shortcut key='z' script='<%="sumitForm("+newSelfGainBean.PRIOR+")"%>'/>
             <%}
               if (!isAtLast)
              {%>
              <a href="#" title="到上一笔(ALT+X)" onClick="sumitForm(<%=newSelfGainBean.NEXT%>)">&gt</a>
              <pc:shortcut key='x' script='<%="sumitForm("+newSelfGainBean.NEXT+")"%>'/>
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
                    workGroupBean.regConditionData(ds,"deptid");
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
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.receiveDate);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "if(form1.storeid.value!='"+masterRow.get("storeid")+"'){sumitForm("+newSelfGainBean.ONCHANGE+");}";%>
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
                   <tr>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bc").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="bc" value='<%=masterRow.get("bc")%>' <%=edClass%> maxlength='<%=ds.getColumn("bc").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("gzzid").getFieldname()%></td>
                    <td noWrap class="td">
                    <%--String sumitGroup = "sumitForm("+workloadGroupBean.GROUP_DETAIL_ADD+");";--%>
                   <%if(isEnd) out.print("<input type='text' value='"+workGroupBean.getLookupName(masterRow.get("gzzid"))+"' style='width:110' class='edline' readonly>");
                   else {%>
                   <pc:select name="gzzid" addNull="1" style="width:110" >
                   <%=workGroupBean.getList(masterRow.get("gzzid"),"deptid",masterRow.get("deptid"))%> </pc:select>
                   <%}%>
                   </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sc__gzzid").getFieldname()%></td>
                    <td noWrap class="td">
                    <%--String sumitGroup = "sumitForm("+workloadGroupBean.GROUP_DETAIL_ADD+");";--%>
                   <%if(isEnd) out.print("<input type='text' value='"+workGroupBean.getLookupName(masterRow.get("sc__gzzid"))+"' style='width:110' class='edline' readonly>");
                   else {%>
                   <pc:select name="sc__gzzid" addNull="1" style="width:110" >
                   <%=workGroupBean.getList(masterRow.get("sc__gzzid"),"deptid",masterRow.get("deptid"))%> </pc:select>
                   <%}%>
                   </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sc__gzzid2").getFieldname()%></td>
                    <td noWrap class="td">
                    <%--String sumitGroup = "sumitForm("+workloadGroupBean.GROUP_DETAIL_ADD+");";--%>
                   <%if(isEnd) out.print("<input type='text' value='"+workGroupBean.getLookupName(masterRow.get("sc__gzzid2"))+"' style='width:110' class='edline' readonly>");
                   else {%>
                   <pc:select name="sc__gzzid2" addNull="1" style="width:110" >
                   <%=workGroupBean.getList(masterRow.get("sc__gzzid2"),"deptid",masterRow.get("deptid"))%> </pc:select>
                   <%}%>
                   </td>
                  </tr>
                  <tr>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("totalNum").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="totalNum" value='<%=masterRow.get("totalNum")%>' maxlength='<%=ds.getColumn("totalNum").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();"  readonly></td>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("zgf").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="zgf" value='<%=masterRow.get("zgf")%>' maxlength='<%=ds.getColumn("zgf").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("zgf2").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="zgf2" value='<%=masterRow.get("zgf2")%>' maxlength='<%=ds.getColumn("zgf2").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("zgf3").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="zgf3" value='<%=masterRow.get("zgf3")%>' maxlength='<%=ds.getColumn("zgf3").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                   </tr>
                    <tr>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("zlyz").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="zlyz" value='<%=masterRow.get("zlyz")%>' <%=edClass%> maxlength='<%=ds.getColumn("zlyz").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("ydzs").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="ydzs" value='<%=masterRow.get("ydzs")%>' <%=edClass%> maxlength='<%=ds.getColumn("ydzs").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dzcs").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="dzcs" value='<%=masterRow.get("dzcs")%>' <%=edClass%> maxlength='<%=ds.getColumn("dzcs").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jf").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="jf" value='<%=masterRow.get("jf")%>' <%=edClass%> maxlength='<%=ds.getColumn("jf").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  </tr>
                   <tr>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhsh").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="jhsh" value='<%=masterRow.get("jhsh")%>' <%=edClass%> maxlength='<%=ds.getColumn("jhsh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sjsh").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="sjsh" value='<%=masterRow.get("sjsh")%>'  <%=edClass%> maxlength='<%=ds.getColumn("sjsh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("shyy").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="shyy" value='<%=masterRow.get("shyy")%>' <%=edClass%> maxlength='<%=ds.getColumn("shyy").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jt").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="jt" value='<%=masterRow.get("jt")%>' <%=edClass%> maxlength='<%=ds.getColumn("jt").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
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
                //2004-5-2 16:43 为给明细数据集加入分页功能
                String count = String.valueOf(list.getRowCount());
                int iPage = 30;
                String pageSize = String.valueOf(iPage);
                %>
                 <tr> <td colspan="8" noWrap class="td">
                   <pc:navigator id="self_gain_listNav" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+newSelfGainBean.TURNPAGE+")"%>' disable='<%=newSelfGainBean.isRepeat.equals("1")?"1":"0"%>'/>
                   </td></tr>
                <tr>
                  <td colspan="8" noWrap class="td">
                    <div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
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

                          <td height='20' nowrap>包装类别</td>
                         <td height='20' nowrap>包装方式</td>



                          <td nowrap><%=detailProducer.getFieldInfo("gylxid").getFieldname()%></td>
                          <td nowrap>工序1</td>
                          <td nowrap>计件单价1</td>
                          <td nowrap>计件工资1</td>
                          <td nowrap>工序2</td>
                          <td nowrap>计件单价2</td>
                          <td nowrap>计件工资2</td>
                          <td nowrap>工序3</td>
                          <td nowrap>计件单价3</td>
                          <td nowrap>计件工资3</td>
                          <td nowrap><%=detailProducer.getFieldInfo("kwid").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("memo").getFieldname()%></td>
                          <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      propertyBean.regData(list,"dmsxid");
                      technicsNameBean.regData(list,"gylxid");
                      String aaaa=list.getValue("package_id");
                       packageBean.regData(list,"package_id");
                      processBean.regData(list,"jgdmxid");
                      technicsRouteBean.regData(list, "gylxid");
                      typeBean.regData(list,"funditemid");
                      if(!isEnd){
                        technicsRouteBean.regConditionData(list,"cpid");
                        technicsNameBean.regConditionData(list,"gylxid");
                        packageBean.regConditionData(list,"funditemid");
                      }
                      BigDecimal t_jjgz = new BigDecimal(0), t_jjgz2 = new BigDecimal(0), t_jjgz3 = new BigDecimal(0);
                      BigDecimal t_sl = new BigDecimal(0), t_scsl = new BigDecimal(0), t_hssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      //2004-5-2 16:43 为明细资料页面加入页
                      int min = self_gain_listNav.getRowMin(request);
                      int max = self_gain_listNav.getRowMax(request);
                      //类中取得笔每一页的数据范围
                      newSelfGainBean.min = min;
                      newSelfGainBean.max = max > detailRows.length-1 ? detailRows.length-1 : max;
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
                        String jjgz = detail.get("jjgz");
                       if(newSelfGainBean.isDouble(jjgz))
                          t_jjgz = t_jjgz.add(new BigDecimal(jjgz));
                       String jjgz2 = detail.get("jjgz2");
                       if(newSelfGainBean.isDouble(jjgz2))
                          t_jjgz2 = t_jjgz2.add(new BigDecimal(jjgz2));
                       String jjgz3 = detail.get("jjgz3");
                       if(newSelfGainBean.isDouble(jjgz3))
                          t_jjgz3 = t_jjgz3.add(new BigDecimal(jjgz3));
                        String sl = detail.get("drawnum");
                        if(newSelfGainBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String hssl = detail.get("drawbignum");
                        if(newSelfGainBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String scsl = detail.get("producenum");
                        if(newSelfGainBean.isDouble(scsl))
                          t_scsl = t_scsl.add(new BigDecimal(scsl));
                        String dmsxid = detail.get("dmsxid");
                        String sx = propertyBean.getLookupName(dmsxid);
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                        String kwName = "kwid_"+i;
                        String dmsxidName = "dmsxid_"+i;
                        String gxName = "gx_"+i;
                        String gxName2 = "gx2_"+i;
                        String gxName3 = "gx3_"+i;
                        String gylxidName = "gylxid_"+i;
                        String gx = detail.get("gylxid").length()>0 ? detail.get("gx") : "";
                        String gx2 = detail.get("gylxid").length()>0 ? detail.get("gx2") : "";
                        String gx3 = detail.get("gylxid").length()>0 ? detail.get("gx3") : "";
                        String jgdmxid=detail.get("jgdmxid");
                        boolean isimport = !jgdmxid.equals("");//引入加工单，从表产品编码当前行不能修改
                        String test="getJjdjValue("+i+");";
                        String test2 = "getJjdjValue2("+i+");";
                        String test3 = "getJjdjValue3("+i+");";
                    %>
                      <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                        <td class="td" nowrap><%=i+1%>

                        <iframe id="prod_<%=i%>" src="" width="0" height=0 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                        <iframe id="prodA_<%=i%>" src="" width="0" height=0 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                        <iframe id="prodB_<%=i%>" src="" width="0" height=0 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                        <iframe id="prodC_<%=i%>" src="" width="0" height=0 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>


						</td>
                        <td class="td" nowrap align="center">
                          <%if(!isEnd && !isimport){%>
                          <img style='cursor:hand' title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=scydw_<%=i%>&srcVar=isprops_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=isbatchno_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=isprops&fieldVar=hsbl&fieldVar=scdwgs&fieldVar=isbatchno','&storeid='+form1.storeid.value,'product_change(<%=i%>)')">
                          <%}if(!isEnd){%>
                          <input name="image" class="img" type="image" title="复制" onClick="if(form1.cpid_<%=i%>.value==''){alert('请输入产品');return;}sumitForm(<%=newSelfGainBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <input type="checkbox" name="isdel_<%=i%>" value="1" <%=detail.get("isdel").equals("1") ? "checked" : ""%>>
                          <input name="image" class="img" type="image" title="删除" onClick="sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
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
                        <input type="text" <%=detailClass%> id="sxz_<%=i%>" name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if(form1.isprops_<%=i%>.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="batchno_<%=i%>" name="batchno_<%=i%>" value='<%=detail.get("batchno")%>' maxlength='<%=list.getColumn("batchno").getPrecision()%>' onchange="BatchNoChange(<%=i%>)" <%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawbignum_<%=i%>" name="drawbignum_<%=i%>" value='<%=detail.get("drawbignum")%>' maxlength='<%=list.getColumn("drawbignum").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="drawnum_<%=i%>" name="drawnum_<%=i%>" value='<%=detail.get("drawnum")%>' maxlength='<%=list.getColumn("drawnum").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)"<%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="producenum_<%=i%>" name="producenum_<%=i%>" value='<%=detail.get("producenum")%>' maxlength='<%=list.getColumn("producenum").getPrecision()%>' onchange="producesl_onchange(<%=i%>)"<%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly></td>

                      <td class="td" nowrap title="包装类别批量修改" onDblClick="bzlb(<%=i%>)">
                        <%if(isEnd){%> <input type="text" <%=detailClass%> style="width:150" onKeyDown="return getNextElement();" id="v_funditemid_<%=i%>" name="v_funditemid_<%=i%>" value='<%=typeBean.getLookupName(detail.get("funditemid"))%>' maxlength='<%=list.getColumn("funditemid").getPrecision()%>' readonly>
                        <%}else {
                          String typeName="funditemid_"+i;
                          String temp2 ="typeschange("+i+");";%>
                          <pc:select name="<%=typeName%>" addNull="1" style="width:110"  onSelect="<%=temp2%>">
                           <%=typeBean.getList(detail.get("funditemid"))%> </pc:select>
                        <%}%>
                       </td>

                         <td class="td" nowrap  nowrap title="包装方式批量修改" onDblClick="bzfs(<%=i%>)">
                           <%if(isEnd){%> <input type="text" <%=detailClass%> style="width:150" onKeyDown="return getNextElement();" id="v_package_id_<%=i%>" name="v_package_id_<%=i%>" value='<%=packageBean.getLookupName(detail.get("package_id"))%>' maxlength='<%=list.getColumn("package_id").getPrecision()%>' readonly>
                        <%}else {
                          String packageName="package_id_"+i; %>
                          <pc:select name="<%=packageName%>" addNull="1" style="width:110">
                         <%=packageBean.getList(detail.get("package_id"),"funditemid",detail.get("funditemid"))%>
                         </pc:select>
                        <%}%></td>
                         <td class="td" nowrap title="工艺路线批量修改" onDblClick="technicsChange(<%=i%>)">
                        <%if(isEnd)out.print("<input type='text' style='width:90' value='"+technicsRouteBean.getLookupName(detail.get("gylxid"))+"' class='ednone' readonly>");
                        else {%>
                          <%String temp ="detailtechnicschange("+i+");"; %>
                        <pc:select name="<%=gylxidName%>" style='width:90' addNull="1" onSelect="<%=temp%>">
                        <%=technicsRouteBean.getList(detail.get("gylxid"),"cpid",detail.get("cpid"))%> </pc:select>
                        <%}%>
                        </td>

                        <td class="td" nowrap title="工序批量修改" onDblClick="technicsNameChange(<%=i%>)">
                        <input type="hidden"  id="jjff_<%=i%>" name="jjff_<%=i%>" value="<%=detail.get("jjff")%>">
                        <%if(isEnd){%><input type="text" <%=detailClass%> style="width:100" onKeyDown="return getNextElement();" id="gx_<%=i%>" name="gx_<%=i%>" value='<%=detail.get("gx")%>' maxlength='<%=list.getColumn("gx").getPrecision()%>' readonly>
                        <%}else {%>
                        <pc:select combox='1' addNull="1" name="<%=gxName%>" value="<%=gx%>" style="width:100" onSelect="<%=test%>" >
                        <%=technicsNameBean.getList(detail.get("gx"), "gylxid", detail.get("gylxid"))%> </pc:select>
                        <%}%>
                        </td>

                        <td class="td" nowrap><input type="text" class=ednone_r  onKeyDown="return getNextElement();" id="jjdj_<%=i%>" name="jjdj_<%=i%>" value='<%=detail.get("jjdj")%>' maxlength='<%=list.getColumn("jjdj").getPrecision()%>' readonly></td>
                        <td class="td" nowrap><input type="text" class=ednone_r  onKeyDown="return getNextElement();" id="jjgz_<%=i%>" name="jjgz_<%=i%>" value='<%=detail.get("jjgz")%>' maxlength='<%=list.getColumn("jjgz").getPrecision()%>' readonly></td>
                        <td class="td" nowrap title="工序批量修改" onDblClick="technicsNameChange2(<%=i%>)">
                        <input type="hidden" id="jjff2_<%=i%>" name="jjff2_<%=i%>" value="<%=detail.get("jjff2")%>">
                        <%if(isEnd){%><input type="text" <%=detailClass%> style="width:100" onKeyDown="return getNextElement();" id="gx2_<%=i%>" name="gx2_<%=i%>" value='<%=detail.get("gx2")%>' maxlength='<%=list.getColumn("gx2").getPrecision()%>' readonly>
                        <%}else {%>
                        <pc:select combox='1' addNull="1" name="<%=gxName2%>" value="<%=gx2%>" style="width:100" onSelect="<%=test2%>" >
                        <%=technicsNameBean.getList(detail.get("gx2"), "gylxid", detail.get("gylxid"))%> </pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" class=ednone_r  onKeyDown="return getNextElement();" id="jjdj2_<%=i%>" name="jjdj2_<%=i%>" value='<%=detail.get("jjdj2")%>' maxlength='<%=list.getColumn("jjdj2").getPrecision()%>' readonly></td>
                        <td class="td" nowrap><input type="text" class=ednone_r  onKeyDown="return getNextElement();" id="jjgz2_<%=i%>" name="jjgz2_<%=i%>" value='<%=detail.get("jjgz2")%>' maxlength='<%=list.getColumn("jjgz2").getPrecision()%>' readonly></td>
                        <td class="td" nowrap title="工序批量修改" onDblClick="technicsNameChange3(<%=i%>)">
                        <input type="hidden" id="jjff3_<%=i%>" name="jjff3_<%=i%>" value="<%=detail.get("jjff3")%>">
                        <%if(isEnd){%><input type="text" <%=detailClass%> style="width:100" onKeyDown="return getNextElement();" id="gx3_<%=i%>" name="gx3_<%=i%>" value='<%=detail.get("gx3")%>' maxlength='<%=list.getColumn("gx3").getPrecision()%>' readonly>
                        <%}else {%>
                        <pc:select combox='1' addNull="1" name="<%=gxName3%>" value="<%=gx3%>" style="width:100" onSelect="<%=test3%>" >
                        <%=technicsNameBean.getList(detail.get("gx3"), "gylxid", detail.get("gylxid"))%> </pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" class=ednone_r  onKeyDown="return getNextElement();" id="jjdj3_<%=i%>" name="jjdj3_<%=i%>" value='<%=detail.get("jjdj3")%>' maxlength='<%=list.getColumn("jjdj3").getPrecision()%>' readonly></td>
                        <td class="td" nowrap><input type="text" class=ednone_r  onKeyDown="return getNextElement();" id="jjgz3_<%=i%>" name="jjgz3_<%=i%>" value='<%=detail.get("jjgz3")%>' maxlength='<%=list.getColumn("jjgz3").getPrecision()%>' readonly></td>
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
                      <tr id="rowinfo_<%=i%>"><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
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
                   <td class="td">&nbsp;</td>
                   <td class="td">&nbsp;</td>
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                   <td class="td"><input id="t_jjgz" name="t_jjgz" type="text" class="ednone_r" style="width:100%" value='<%=t_jjgz%>' readonly></td>
                  <td align="right" class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                   <td class="td"><input id="t_jjgz2" name="t_jjgz2" type="text" class="ednone_r" style="width:100%" value='<%=t_jjgz2%>' readonly></td>
                  <td align="right" class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                   <td class="td"><input id="t_jjgz3" name="t_jjgz3" type="text" class="ednone_r" style="width:100%" value='<%=t_jjgz3%>' readonly></td>
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
            <td class="td"><b>登记日期:</b><%=masterRow.get("createDate")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("creator")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
             <%if(!isEnd){%>
             <input type="hidden" name="importProcess" value=""><input type="hidden" name="scanValue" value="">
             <input name="btnback" type="button" class="button"  title="引加工单(ALT+Q)"style='width:85' value="引加工单(Q)"onClick="buttonEventQ()">
                <pc:shortcut key="q" script='<%="buttonEventQ()"%>'/>
                <input type="button" class="button" title="盘点机(E)" value="盘点机(E)" style='width:65' onClick="buttonEventE(false)">
                <pc:shortcut key="e" script='<%="buttonEventE(false)"%>'/>

                <input name="button2" type="button" class="button" title="多选删除(ALT+X)" value="多选删除(X)" style='width:80' onClick="sumitForm(<%=newSelfGainBean.BATCH_DEL%>);">
                <pc:shortcut key="x" script='<%="sumitForm("+newSelfGainBean.BATCH_DEL+")"%>'/>
              <input name="button2" type="button" class="button" title="删数量为空行(ALT+X)" value="删数量为空行(X)" style='width:110' onClick="sumitForm(<%=newSelfGainBean.DELETE_BLANK%>);">
                <pc:shortcut key="x" script='<%="sumitForm("+newSelfGainBean.DELETE_BLANK+")"%>'/>
               <%}%>
                <%if(!isEnd){%>
             <input name="button2" type="button" class="button" title="保存添加(ALT+N)" value="保存添加(N)" style='width:80' onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
                <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="保存(ALT+S)" value="保存(S)" style='width:50' onClick="sumitForm(<%=Operate.POST%>);">
                <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%}%>
              <%if(isCanDelete && !newSelfGainBean.isReport){%><input name="button3" type="button" class="button" title="删除(ALT+D)" style='width:50' value="删除(D)" onClick="buttonEventD();">
                <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <%if(!newSelfGainBean.isApprove && !newSelfGainBean.isReport){%><input name="btnback" type="button" class="button" title="返回(ALT+C)" style='width:50' value="返回(C)" onClick="backList();">
                <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
                <%--03.09 11:43 新增 新增关闭按钮提供给当此页面是被报表调用时使用. yjg--%>
                <%if(newSelfGainBean.isReport){%><input name="btnback" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)"  style='width:50' onClick="window.close()">
                <pc:shortcut key="t" script='<%="window.close()"%>'/>
               <%}%>
                <%--03.13 15:37 新增 新增打印单据按钮来把这张采购入库单页面上的内容打印出来. yjg--%>
              <input type="button" class="button" title="打印(ALT+P)" value="打印(P)" style='width:50' onclick="buttonEventP();">
                <pc:shortcut key="p" script='<%="buttonEventP()"%>'/>
            </td>
          </tr>
          </table>
        <td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);

  function onload(){
    <%=newSelfGainBean.adjustInputSize(new String[]{"cpbm","product", "jldw","v_package_id", "batchno", "drawnum","sxz", "memo","drawbignum","producenum","hsdw", "scydw","jjdj","jjgz","jjdj2","jjgz2","jjdj3","jjgz3"},  "form1",  newSelfGainBean.max-min+1, min)%>
  }

  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    min = <%=newSelfGainBean.min%>;
    max = <%=newSelfGainBean.max%>;
    newmax = <%=detailRows.length%> >= max+1 ?  max+1 : <%=detailRows.length%>;
    function technicsChange(i){
    //连接的对象。编码更改对应地区更改eval('form1.v_gx_'+i+'.value')
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
    //连接的对象。编码更改对应地区更改
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
  //连接的对象。编码更改对应地区更改
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
  function technicsNameChange3(i){
  //连接的对象。编码更改对应地区更改
  var gx3 = eval('form1.gx3_'+i+'.value');
  for(g=min;g<newmax;g++)
  {
    var linkObj = FindSelectObject("gx3_"+g);
    if(linkObj == null)
      return;
    linkObj.SetSelectedKey(gx3);
    linkObj.OnSelect();
  }
  }
  function bzlb(i){
  //连接的对象。编码更改对应地区更改
  var funditemid = eval('form1.funditemid_'+i+'.value');
  for(g=min;g<newmax;g++)
  {
    var linkObj = FindSelectObject("funditemid_"+g);
    if(linkObj == null)
      return;
    linkObj.SetSelectedKey(funditemid);
    linkObj.OnSelect();
  }
  }
  function bzfs(i){
  //连接的对象。编码更改对应地区更改
  var package_id = eval('form1.package_id_'+i+'.value');
  for(g=min;g<newmax;g++)
  {
    var linkObj = FindSelectObject("package_id_"+g);
    if(linkObj == null)
      return;
    linkObj.SetSelectedKey(package_id);
    linkObj.OnSelect();
  }
  }

  //选择工序得到计件单价，并计算计件工资
  function getJjdjValue(i){
    getRowValue(document.all['prod_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'form1', 'srcVar=jjdj_'+i+'&srcVar=jjff_'+i, 'fieldVar=deje&fieldVar=jjff',eval('form1.v_gx_'+i+'.value') , 'getjjgz_onchange('+i+',false)');
  }
  function getJjdjValue2(i){
     getRowValue(document.all['prodA_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'form1', 'srcVar=jjdj2_'+i+'&srcVar=jjff2_'+i, 'fieldVar=deje&fieldVar=jjff',eval('form1.v_gx2_'+i+'.value') , 'getjjgz2_onchange('+i+',false)');
  }
  function getJjdjValue3(i){
    getRowValue(document.all['prodB_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'form1', 'srcVar=jjdj3_'+i+'&srcVar=jjff3_'+i, 'fieldVar=deje&fieldVar=jjff',eval('form1.v_gx3_'+i+'.value') , 'getjjgz3_onchange('+i+',false)');
  }
  function getjjgz_onchange(i,isBigUnit){
    var sl = document.all['drawnum_'+i];
    var hssl = document.all['drawbignum_'+i];
    var scsl = document.all['producenum_'+i];
    var jjdj = document.all['jjdj_'+i];
    var jjgz = document.all['jjgz_'+i];
    var sfc = document.all['jjff_'+i];//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
    if(sfc.value=='3' || sfc.value=='4' || sfc.value==''){
      jjgz.value='';
    }
    if(sfc.value=='5' && jjdj.value!="" && !isNaN(jjdj.value)){
      jjgz.value=jjdj.value;
    }
    if(sfc.value=='0'){
       if(sl.value !="" && !isNaN(sl.value) && jjdj.value!="" && !isNaN(jjdj.value))
         jjgz.value= formatSum(parseFloat(sl.value) * parseFloat(jjdj.value));
    }
    if(sfc.value=='1'){
      if(sl.value !="" && !isNaN(sl.value) && jjdj.value!="" && !isNaN(jjdj.value))
        jjgz.value= formatSum(parseFloat(scsl.value) * parseFloat(jjdj.value));
    }
    if(sfc.value=='2'){
      if(sl.value !="" && !isNaN(sl.value) && jjdj.value!="" && !isNaN(jjdj.value))
        jjgz.value= formatSum(parseFloat(hssl.value) * parseFloat(jjdj.value));
    }
    //cal_tot('jjgz');
  }
  function getjjgz2_onchange(i,isBigUnit){
    var sl2 = document.all['drawnum_'+i];
    var hssl2 = document.all['drawbignum_'+i];
    var scsl2 = document.all['producenum_'+i];
    var jjdj2 = document.all['jjdj2_'+i];
    var jjgz2 = document.all['jjgz2_'+i];
    var sfc2 = document.all['jjff2_'+i];//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
    if(sfc2.value=='3' || sfc2.value=='4' || sfc2.value==''){
      jjgz2.value='';
    }
    if(sfc2.value=='5' && jjdj2.value!="" && !isNaN(jjdj2.value)){
      jjgz2.value=jjdj2.value;
    }
    if(sfc2.value=='0'){
       if(sl2.value !="" && !isNaN(sl2.value) && jjdj2.value!="" && !isNaN(jjdj2.value))
         jjgz2.value= formatSum(parseFloat(sl2.value) * parseFloat(jjdj2.value));
    }
    if(sfc2.value=='1'){
      if(sl2.value !="" && !isNaN(sl2.value) && jjdj2.value!="" && !isNaN(jjdj2.value))
        jjgz2.value= formatSum(parseFloat(scsl2.value) * parseFloat(jjdj2.value));
    }
    if(sfc2.value=='2'){
      if(sl2.value !="" && !isNaN(sl2.value) && jjdj2.value!="" && !isNaN(jjdj2.value))
        jjgz2.value= formatSum(parseFloat(hssl2.value) * parseFloat(jjdj2.value));
    }
    //cal_tot('jjgz2');
  }
  function getjjgz3_onchange(i,isBigUnit){
    var sl3 = document.all['drawnum_'+i];
    var hssl3 = document.all['drawbignum_'+i];
    var scsl3 = document.all['producenum_'+i];
    var jjdj3 = document.all['jjdj3_'+i];
    var jjgz3 = document.all['jjgz3_'+i];
    var sfc3 = document.all['jjff3_'+i];//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
    if(sfc3.value=='3' || sfc3.value=='4' || sfc3.value==''){
      jjgz3.value='';
    }
    if(sfc3.value=='5' && jjdj3.value!="" && !isNaN(jjdj3.value)){
      jjgz3.value=jjdj3.value;
    }
    if(sfc3.value=='0'){
       if(sl3.value !="" && !isNaN(sl3.value) && jjdj3.value!="" && !isNaN(jjdj3.value))
         jjgz3.value= formatSum(parseFloat(sl3.value) * parseFloat(jjdj3.value));
    }
    if(sfc3.value=='1'){
      if(sl3.value !="" && !isNaN(sl3.value) && jjdj3.value!="" && !isNaN(jjdj3.value))
        jjgz3.value= formatSum(parseFloat(scsl3.value) * parseFloat(jjdj3.value));
    }
    if(sfc3.value=='2'){
      if(sl3.value !="" && !isNaN(sl3.value) && jjdj3.value!="" && !isNaN(jjdj3.value))
        jjgz3.value= formatSum(parseFloat(hssl3.value) * parseFloat(jjdj3.value));
    }
    //cal_tot('jjgz3');
  }
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
       hsslObj.value = slobj.value;
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
  var jjdjObj = document.all['jjdj_'+i];
  var jjgzObj = document.all['jjgz_'+i];
  var hsblObj = document.all['truebl_'+i];
  var sfcObj = document.all['jjff_'+i];//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
  var jjdjObj2 = document.all['jjdj2_'+i];
  var jjgzObj2 = document.all['jjgz2_'+i];
  var sfcObj2 = document.all['jjff2_'+i];//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
  var jjdjObj3 = document.all['jjdj3_'+i];
  var jjgzObj3 = document.all['jjgz3_'+i];
  var sfcObj3 = document.all['jjff3_'+i];//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
  var scdwgsObj = document.all['scdwgs_'+i];//生产公式
  var sxzObj = document.all['sxz_'+i];//生产公式
  var obj = isBigUnit ? hsslObj : slObj;
  var widthObj = document.all['widths_'+i];//规格属性的宽度
  var showText = isBigUnit ? "输入的换算数量非法" : "输入的数量非法";
  var showText2 = isBigUnit ? "输入的换算数量小于零" : "输入的数量小于零";
  var changeObj = isBigUnit ? slObj : hsslObj;
  if(obj.value=="")
     return;
   if(isNaN(obj.value))
   {
     alert(showText);
     obj.focus();
     return;
   }
   if(obj.value<0)
   {
     alert(showText2);
     obj.focus();
     return;
   }
   if(sfcObj.value=="3" || sfcObj.value=="4" || sfcObj.value=='')
     jjgzObj.value='';
   if(sfcObj2.value=="3" || sfcObj2.value=="4" || sfcObj2.value=='')
     jjgzObj2.value='';
   if(sfcObj3.value=="3" || sfcObj3.value=="4" || sfcObj3.value=='')
   {
     jjgzObj3.value='';
   }
   if(sfcObj.value=="5" && jjdjObj.value!="" && !isNaN(jjdjObj.value))
     jjgzObj.value=jjdjObj.value;
   if(sfcObj2.value=="5" && jjdjObj2.value!="" && !isNaN(jjdjObj2.value))
     jjgzObj2.value=jjdjObj2.value;
   if(sfcObj3.value=="5" && jjdjObj3.value!="" && !isNaN(jjdjObj3.value))
   {
     jjgzObj3.value=jjdjObj3.value;
   }
   if(isBigUnit){
       if(hsslObj.value !="" && !isNaN(hsslObj.value)){
           if(sfcObj.value=="2" && jjdjObj.value!="" && !isNaN(jjdjObj.value))
             jjgzObj.value= formatSum(parseFloat(hsslObj.value) * parseFloat(jjdjObj.value));
           if(sfcObj2.value=="2" && jjdjObj2.value!="" && !isNaN(jjdjObj2.value))
             jjgzObj2.value= formatSum(parseFloat(hsslObj.value) * parseFloat(jjdjObj2.value));
           if(sfcObj3.value=="2" && jjdjObj3.value!="" && !isNaN(jjdjObj3.value))
             jjgzObj3.value= formatSum(parseFloat(hsslObj.value) * parseFloat(jjdjObj3.value));
       }
       if(changeObj.value=="" || '<%=SC_STORE_UNIT_STYLE%>'=='1'){//是否强制转换
         if(hsblObj.value=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
           changeObj.value = obj.value;
         else
           changeObj.value = formatQty(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
         //计算计件工资
         if(slObj.value !="" && !isNaN(slObj.value) ){
           if(sfcObj.value=="0" && jjdjObj.value!="" && !isNaN(jjdjObj.value))
             jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj.value));
           if(sfcObj2.value=="0" && jjdjObj2.value!="" && !isNaN(jjdjObj2.value))
             jjgzObj2.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj2.value));
           if(sfcObj3.value=="0" && jjdjObj3.value!="" && !isNaN(jjdjObj3.value))
             jjgzObj3.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj3.value));
         }
       }
   }
   else{
     if(slObj.value !="" && !isNaN(slObj.value)){
       if(sfcObj.value=='0' && jjdjObj.value!="" && !isNaN(jjdjObj.value))
         jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj.value));
       if(sfcObj2.value=='0' && jjdjObj2.value!="" && !isNaN(jjdjObj2.value))
         jjgzObj2.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj2.value));
       if(sfcObj3.value=='0' && jjdjObj3.value!="" && !isNaN(jjdjObj3.value))
           jjgzObj3.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj3.value));
     }
     if(changeObj.value=="" || '<%=SC_STORE_UNIT_STYLE%>'=='1'){//是否强制转换
       if(hsblObj.value=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
         changeObj.value = obj.value;
       else
         changeObj.value = formatQty(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
     }
     //计算计件工资
     if(changeObj.value !="" && !isNaN(changeObj.value) ){
       if(sfcObj.value=="2" && jjdjObj.value!="" && !isNaN(jjdjObj.value))
         jjgzObj.value= formatSum(parseFloat(changeObj.value) * parseFloat(jjdjObj.value));
       if(sfcObj2.value=="2" && jjdjObj2.value!="" && !isNaN(jjdjObj2.value))
         jjgzObj2.value= formatSum(parseFloat(changeObj.value) * parseFloat(jjdjObj2.value));
       if(sfcObj3.value=="2" && jjdjObj3.value!="" && !isNaN(jjdjObj3.value))
         jjgzObj3.value= formatSum(parseFloat(changeObj.value) * parseFloat(jjdjObj3.value));
     }
   }
   if(scslObj.value!="" && '<%=KC_PRODUCE_UNIT_STYLE%>'!='1'){
     if(sfcObj.value=='1' && scslObj.value !="" && !isNaN(scslObj.value) && jjdjObj.value!="" && !isNaN(jjdjObj.value))
         jjgzObj.value= formatSum(parseFloat(scslObj.value) * parseFloat(jjdjObj.value));
     if(sfcObj2.value=='1' && scslObj.value !="" && !isNaN(scslObj.value) && jjdjObj2.value!="" && !isNaN(jjdjObj2.value))
         jjgzObj2.value= formatSum(parseFloat(scslObj.value) * parseFloat(jjdjObj2.value));
     if(sfcObj3.value=='1' && scslObj.value !="" && !isNaN(scslObj.value) && jjdjObj3.value!="" && !isNaN(jjdjObj3.value))
         jjgzObj3.value= formatSum(parseFloat(scslObj.value) * parseFloat(jjdjObj3.value));
     //cal_tot('sl');
     //cal_tot('hssl');
     //cal_tot('jjgz');
     //cal_tot('jjgz2');
     //cal_tot('jjgz3');
     return;
   }
   else
   {
     //alert("sdf_" +widthObj.value + "_"+parseFloat(scdwgsObj.value)+"_" + hsblObj.value);
     if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
       scslObj.value= isBigUnit ? changeObj.value : slObj.value;
     else{
       if(isBigUnit)
         scslObj.value = formatQty(hsblObj.value=="" ? parseFloat(hsslObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value) : parseFloat(hsslObj.value)*parseFloat(hsblObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
       if(!isBigUnit)
         scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
     }
     if(sfcObj.value=='1' && scslObj.value !="" && !isNaN(scslObj.value) && jjdjObj.value!="" && !isNaN(jjdjObj.value))
         jjgzObj.value= formatSum(parseFloat(scslObj.value) * parseFloat(jjdjObj.value));
     if(sfcObj2.value=='1' && scslObj.value !="" && !isNaN(scslObj.value) && jjdjObj2.value!="" && !isNaN(jjdjObj2.value))
         jjgzObj2.value= formatSum(parseFloat(scslObj.value) * parseFloat(jjdjObj2.value));
     if(sfcObj3.value=='1' && scslObj.value !="" && !isNaN(scslObj.value) && jjdjObj3.value!="" && !isNaN(jjdjObj3.value))
         jjgzObj3.value= formatSum(parseFloat(scslObj.value) * parseFloat(jjdjObj3.value));
   }
   //cal_tot('sl');
   //cal_tot('hssl');
   //cal_tot('jjgz');
   //cal_tot('jjgz2');
   //cal_tot('jjgz3');
   //cal_tot('scsl');
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
   var jjdjObj = document.all['jjdj_'+i];
   var jjgzObj = document.all['jjgz_'+i];
   var hsblObj = document.all['truebl_'+i];
   var sfcObj = document.all['jjff_'+i];//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
   var jjdjObj2 = document.all['jjdj2_'+i];
   var jjgzObj2 = document.all['jjgz2_'+i];
   var sfcObj2 = document.all['jjff2_'+i];//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
   var jjdjObj3 = document.all['jjdj3_'+i];
   var jjgzObj3 = document.all['jjgz3_'+i];
   var sfcObj3 = document.all['jjff3_'+i];//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
   var scdwgsObj = document.all['scdwgs_'+i];//生产公式
   var widthObj = document.all['widths_'+i];//规格属性的宽度
   if(scslObj.value=="")
     return;
   if(isNaN(scslObj.value))
   {
     alert('输入的生产数量非法');
     obj.focus();
     return;
   }
   if(scslObj.value<0)
   {
     alert('输入的生产数量小于零');
     obj.focus();
     return;
   }
   if(sfcObj.value=="3" || sfcObj.value=="4" || sfcObj.value=='')
     jjgzObj.value='';
   if(sfcObj2.value=="3" || sfcObj2.value=="4" || sfcObj2.value=='')
     jjgzObj2.value='';
   if(sfcObj3.value=="3" || sfcObj3.value=="4" || sfcObj3.value=='')
     jjgzObj3.value='';
   if(sfcObj.value=="5" && jjdjObj.value!="" && !isNaN(jjdjObj.value))
     jjgzObj.value=jjdjObj.value;
   if(sfcObj2.value=="5" && jjdjObj2.value!="" && !isNaN(jjdjObj2.value))
     jjgzObj2.value=jjdjObj2.value;
   if(sfcObj3.value=="5" && jjdjObj3.value!="" && !isNaN(jjdjObj3.value))
     jjgzObj3.value=jjdjObj3.value;
   if(sfcObj.value=="1" && jjdjObj.value!="" && !isNaN(jjdjObj.value))
      jjgzObj.value= formatSum(parseFloat(scslObj.value) * parseFloat(jjdjObj.value));
   if(sfcObj2.value=="1" && jjdjObj2.value!="" && !isNaN(jjdjObj2.value))
      jjgzObj2.value= formatSum(parseFloat(scslObj.value) * parseFloat(jjdjObj2.value));
   if(sfcObj3.value=="1" && jjdjObj3.value!="" && !isNaN(jjdjObj3.value))
      jjgzObj3.value= formatSum(parseFloat(scslObj.value) * parseFloat(jjdjObj3.value));
   if(slObj.value!="" && '<%=KC_PRODUCE_UNIT_STYLE%>'!='1'){//生产数量与数量是否强制转换
     if(sfcObj.value=="0")
       jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj.value));
     if(sfcObj2.value=="0")
       jjgzObj2.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj2.value));
     if(sfcObj3.value=="0")
       jjgzObj3.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj3.value));
     //cal_tot('jjgz');
     //cal_tot('jjgz2');
     //cal_tot('jjgz3');
     return;
   }
   if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
     slObj.value= scslObj.value;
   else
     slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthObj.value)/parseFloat(scdwgsObj.value));
   if(slObj.value !="" && !isNaN(slObj.value)){
     if(sfcObj.value=="0"  && jjdjObj.value!="" && !isNaN(jjdjObj.value))
       jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj.value));
     if(sfcObj2.value=="0"  && jjdjObj2.value!="" && !isNaN(jjdjObj2.value))
       jjgzObj2.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj2.value));
     if(sfcObj3.value=="0"  && jjdjObj3.value!="" && !isNaN(jjdjObj3.value))
       jjgzObj3.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj3.value));
   }
   //cal_tot('jjgz');
   //cal_tot('jjgz2');
   //cal_tot('jjgz3');
   if(hsslObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1'){
     if(sfcObj.vlaue=="2")
       jjgzObj.value= formatSum(parseFloat(hsslObj.value) * parseFloat(jjdjObj.value));
     if(sfcObj2.vlaue=="2")
       jjgzObj2.value= formatSum(parseFloat(hsslObj.value) * parseFloat(jjdjObj2.value));
     if(sfcObj3.vlaue=="2")
       jjgzObj3.value= formatSum(parseFloat(hsslObj.value) * parseFloat(jjdjObj3.value));
     //cal_tot('jjgz');
     //cal_tot('jjgz2');
     //cal_tot('jjgz3');
     return;
   }
   if(hsblObj.value=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
     hsslObj.value = slObj.value;
   else
     hsslObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
   if(hsslObj.value !="" && !isNaN(hsslObj.value) ){
     if(sfcObj.vlaue=="2" && jjdjObj.value!="" && !isNaN(jjdjObj.value))
       jjgzObj.value= formatSum(parseFloat(hsslObj.value) * parseFloat(jjdjObj.value));
     if(sfcObj2.vlaue=="2" && jjdjObj2.value!="" && !isNaN(jjdjObj2.value))
       jjgzObj2.value= formatSum(parseFloat(hsslObj.value) * parseFloat(jjdjObj2.value));
     if(sfcObj3.vlaue=="2" && jjdjObj3.value!="" && !isNaN(jjdjObj3.value))
       jjgzObj3.value= formatSum(parseFloat(hsslObj.value) * parseFloat(jjdjObj3.value));
    }
   //cal_tot('jjgz');
   //cal_tot('jjgz2');
   //cal_tot('jjgz3');
   //cal_tot('scsl');
   //cal_tot('sl');
   //cal_tot('hssl');
  }
  function cal_tot(type)
  {
    var tmpObj;
    var tot=0;
    for(i=min; i<<%=detailRows.length%>; i++)
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
      else if(type == 'jjgz3')
        tmpObj = document.all['jjgz3_'+i];
      else
        return;
      if(tmpObj.value!="" && !isNaN(tmpObj.value))
        tot += parseFloat(tmpObj.value);
    }
    if(type == 'sl'){
      document.all['t_sl'].value = formatQty(tot);
      form1.totalNum.value = formatQty(tot);
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
    if(type == 'jjgz3'){
      document.all['t_jjgz3'].value = formatQty(tot);
      form1.zgf3.value = formatSum(tot);
    }
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
    if(isNew)
       sumitForm(<%=newSelfGainBean.NEW_TRANSFERSCAN%>);
    else
      sumitForm(<%=newSelfGainBean.TRANSFERSCAN%>);
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
     ImportProcessSelect('form1','srcVar=importProcess','fieldVar=jgdid',form1.deptid.value,'0',form1.storeid.value,'sumitForm(<%=newSelfGainBean.SELF_SEL_PROCESS%>)')
  }
     //复制自制收货单
     function buttonEventR()
     {
        ImportSelfSelect('form1','srcVar=masterid','fieldVar=receiveid','sumitForm(<%=newSelfGainBean.COPY_SELF%>)')
     }
  //删除
  function buttonEventD()
  {
     if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);
  }
  function buttonEventP()
  {
   location.href='../pub/pdfprint.jsp?code=self_gain_edit_bill&operate=<%=newSelfGainBean.PRINT_BILL%>&a$sfdjid=<%=masterRow.get("receiveid")%>&src=../store/self_gain_edit.jsp'
  }
  function buttonEventA()
  {
      if(form1.storeid.value==''){alert('请选择仓库');return;}sumitForm(<%=Operate.DETAIL_ADD%>);
  }
  function BatchNoChange(i){
    getRowNumberValue(document.all['prod_'+i], 'form1', 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i+'&srcVar=batchno_'+i+'&srcVar=drawnum_'+i+'&srcVar=producenum_'+i+'&srcVar=drawbignum_'+i, 'fieldVar=dmsxid&fieldVar=sxz&fieldVar=batno&fieldVar=salenum&fieldVar=producenum&fieldVar=pagenum', eval('form1.cpid_'+i+'.value'), eval('form1.dmsxid_'+i+'.value'), eval('form1.batchno_'+i+'.value'),'combineBatchNoEvent('+i+')');
  }
  function combineBatchNoEvent(i)
  {
    propertyChange(i);
    sl_onchange(i,false)
  }
  /**
   * 得到合格证的数据
   * iframeObj IFrame的对象
   * lookup    Lookup Bean 的名称
   * frmName   表单名称
   * srcVar    表单各个需要取值的控件名称字符串。如:srcVar=dmsxid_0&srcVar=sxz_0
   * fieldVar  与各个需要取值的控件名称相对应的字段名称字符串。如:fieldVar=dmsxid&fieldVar=sxz
   * idVar     当前要得到的ID的值。如:idVar=1&idVar=2
   * methodName  对输入框赋值后的要调用的方法名称
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
  //选择属性
function CardInfoOpen(){openSelectUrl("../store/getCardValue.jsp", "cardinfo", winopt2);}
  function showDetail(masterRow){
    selectRow();
  }
</script>
<%if(newSelfGainBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>

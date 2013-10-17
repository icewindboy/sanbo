<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  //String op_approve ="op_approve";
  String op_instore ="op_instore";
%><%
  engine.erp.buy.B_BuyOrderGoodsInit buyOrderGoodsInitBean = engine.erp.buy.B_BuyOrderGoodsInit.getInstance(request);
  String pageCode = "buy_ordergoods";
  //boolean hasApproveLimit = isApprove && loginBean.hasLimits(pageCode, op_approve);

%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/scripts/public.css" type="text/css">
</head>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/validate.js"></script>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/rowcontrol.js"></script>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/tabcontrol.js"></script>
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
  location.href='buy_ordergoodsinit.jsp';
}
function corpCodeSelect(obj)
{
  ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=buyOrderGoodsInitBean.ONCHANGE%>)');
}
function corpNameSelect(obj)
{
  ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=buyOrderGoodsInitBean.ONCHANGE%>)');
}
function corpCydwCodeSelect(obj)
{
  TransportCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwt_dwtxid&srcVar=dwt_dwdm&srcVar=dwt_dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpCydwNameSelect(obj)
{
  TransportNameChange(document.all['prod'], obj.form.name, 'srcVar=dwt_dwtxid&srcVar=dwt_dwdm&srcVar=dwt_dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw', obj.value,'sumitForm(<%=buyOrderGoodsInitBean.PRODUCT_ONCHANGE%>)');
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw', obj.value,'sumitForm(<%=buyOrderGoodsInitBean.PRODUCT_ONCHANGE%>)');
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
function Add()
{
  if(form1.storeid.value=='')
  {
    alert('请先选择仓库');
    return;
  }
  sumitForm(<%=Operate.DETAIL_ADD%>)
}
</script>
<%String retu = buyOrderGoodsInitBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//LookUp部门
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp buyOrderBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_ORDER_GOODS);
  engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//LookUp结算方式
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//LookUp规格属性
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = buyOrderGoodsInitBean.getMaterTable();
  EngineDataSet list = buyOrderGoodsInitBean.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  HtmlTableProducer masterProducer = buyOrderGoodsInitBean.masterProducer;
  HtmlTableProducer detailProducer = buyOrderGoodsInitBean.detailProducer;
  RowMap masterRow = buyOrderGoodsInitBean.getMasterRowinfo();
  RowMap[] detailRows= buyOrderGoodsInitBean.getDetailRowinfos();
  String zt=masterRow.get("zt");
  String deptid = masterRow.get("deptid");
  String czyid = masterRow.get("czyid");
  boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, czyid);//判断登陆员工是否有操作改制单人单据的权限
  if(buyOrderGoodsInitBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
    storeBean.regData(ds, "storeid");
    deptBean.regData(ds, "deptid");
    corpBean.regData(ds, "dwt_dwtxid");
  }
  boolean isCanAmend = true;//判断取消审批后主表数据是否能修改
  boolean isCanRework = true;//判断取消审批后从表数据是否能修改
  if(zt.equals("0"))
    isCanAmend = engine.erp.baseinfo.BasePublicClass.isRework(list,"sjrkl");//取消审批后从表实际入库量如果有一条大于零，主表不能修改。
  String djlx=masterRow.get("djlx");//单据类型
  boolean isEnd = buyOrderGoodsInitBean.isRep || buyOrderGoodsInitBean.isApprove || (!buyOrderGoodsInitBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !buyOrderGoodsInitBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(buyOrderGoodsInitBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = (isEnd || !isHasDeptLimit) ? "class=edline" : "class=edbox";
  String detailClass = (isEnd || !isHasDeptLimit) ? "class=ednone" : "class=edFocused";
  String detailClass_r = (isEnd || !isHasDeptLimit) ? "class=ednone_r" : "class=edFocused_r";
  String readonly = (isEnd || !isHasDeptLimit) ? " readonly" : "";
  String masterReadonly = isCanAmend ? readonly : "readonly";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("2") ? "已入库" : (zt.equals("9") ? "审批中" : "未审核"));
  String head = djlx.equals("1") ? "采购进货单" : "采购退货单";
  boolean isAdd = buyOrderGoodsInitBean.isDetailAdd;
  boolean  isRead = buyOrderGoodsInitBean.isRep || buyOrderGoodsInitBean.isApprove || (!buyOrderGoodsInitBean.masterIsAdd() && (zt.equals("9") || zt.equals("2")));//表示在审批中或已入库不能修改
  isRead = isRead || !(buyOrderGoodsInitBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  String slReadonly = (isRead || !isHasDeptLimit) ? "readonly" : "";//数量和单价在审批的时候可以修改
  String slClass_r = (isRead || !isHasDeptLimit) ? "class=ednone_r" : "class=edFocused_r";
 %>
<BODY oncontextmenu="window.event.returnValue=true"  onLoad="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="80%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="djlx" value='<%=djlx%>'>
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="80%">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab"><%=head%>(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%corpBean.regData(ds,"dwtxid");storeBean.regData(ds, "storeid");personBean.regData(ds, "personid");
                  if(!isEnd)
                    personBean.regConditionData(ds, "deptid");
                %>
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhdbm").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="jhdbm" value='<%=masterRow.get("jhdbm")%>' maxlength='<%=ds.getColumn("jhdbm").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="jhrq" value='<%=masterRow.get("jhrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=masterReadonly%>>
                    <%if(!isEnd && isCanAmend && isHasDeptLimit){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.jhrq);"></a>
                    <%}%>
                  </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%String store_Change = "if(form1.storeid.value!='"+masterRow.get("storeid")+"')sumitForm("+buyOrderGoodsInitBean.STORE_CHANGE+")";%>
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:90' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="1" style="width:90" onSelect="<%=store_Change%>"> <%=storeBean.getList(masterRow.get("storeid"))%>
                    </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%String onChange = "if(form1.deptid.value!='"+masterRow.get("deptid")+"')sumitForm("+buyOrderGoodsInitBean.DEPT_CHANGE+")";%>
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:111" onSelect="<%=onChange%>"> <%=deptBean.getList(masterRow.get("deptid"))%>
                    </pc:select>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwtxid").getFieldname()%></td><%--供货单位--%>
                  <%RowMap corpRow = corpBean.getLookupRow(masterRow.get("dwtxid"));%>
                  <td  noWrap class="td" colspan="3"><input type="text" name="dwdm" value='<%=corpRow.get("dwdm")%>' style="width:60" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpCodeSelect(this);" <%=masterReadonly%>>
                        <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>' >
                  <input type="text" name="dwmc" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:180" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpNameSelect(this);" <%=masterReadonly%>>
                  <%if(!isEnd && isCanAmend && isHasDeptLimit){%>
                  <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=buyOrderGoodsInitBean.ONCHANGE%>)');"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';sumitForm(<%=buyOrderGoodsInitBean.ONCHANGE%>)">
                  <%}%>
                  </td>
                                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("khlx").getFieldname()%></td>
                  <td noWrap class="td">
                    <%String khlx=masterRow.get("khlx");%>
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+masterRow.get("khlx")+"' style='width:50' class='edline' readonly>");
                    else {%>
                    <pc:select name="khlx" style="width:50" value='<%=khlx%>'>
                      <pc:option value=''></pc:option>
                      <pc:option value='A'>A</pc:option>
                      <pc:option value='C'>C</pc:option>
                      </pc:select>
                    <%}%>
                  </td>

                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="personid" addNull="1" style="width:110">
                    <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                </tr>


                <tr>
                  <td colspan="8" noWrap class="td">
                      <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                        <tr class="tableTitle">
                          <td nowrap width=10></td>
                          <td height='20' align="center" nowrap>
                            <%if(!isEnd && isHasDeptLimit){%>
                            <input name="image" class="img" type="image" title="新增(A)" onClick="Add();" src="../images/add_big.gif" border="0">
                            <pc:shortcut key="a" script='Add();'/><%}%>
                          </td>
                          <td height='20' nowrap><%=detailProducer.getFieldInfo("hthwid").getFieldname()%></td>
                          <td height='20' nowrap>产品编码</td>
                          <td height='20' nowrap>品名 规格</td>
                          <td nowrap>图号</td>
                          <td height='20' nowrap>换算数量</td>
                          <td height='20' nowrap>换算单位</td>
                          <td height='20' nowrap>数量</td>
                          <td height='20' nowrap>计量单位</td>
                          <td nowrap><%=detailProducer.getFieldInfo("sjrkl").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("jhrq").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("gyszyh").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>

                        </tr>
                        <%prodBean.regData(list,"cpid");
                          buyOrderBean.regData(list,"hthwid");
                          propertyBean.regData(list,"dmsxid");
                          BigDecimal t_sl = new BigDecimal(0), t_hssl = new BigDecimal(0), t_je = new BigDecimal(0), t_sjrkl = new BigDecimal(0);
                          int i=0;
                          RowMap detail = null;
                          for(; i<detailRows.length; i++)   {
                            detail = detailRows[i];
                            BigDecimal b_djlx = new BigDecimal(djlx);
                            String s_sl = detail.get("sl");
                            BigDecimal sl = s_sl.length() > 0 ? b_djlx.multiply(new BigDecimal(s_sl)) : new BigDecimal(0);
                            String s_hssl = detail.get("hssl");
                            BigDecimal hssl = s_hssl.length() > 0 ? b_djlx.multiply(new BigDecimal(s_hssl)) : new BigDecimal(0);
                            String s_je = detail.get("je");
                            BigDecimal je = s_je.length() > 0 ? b_djlx.multiply(new BigDecimal(s_je)) : new BigDecimal(0);
                            String sjrkl = detail.get("sjrkl");
                            if(buyOrderGoodsInitBean.isDouble(sjrkl))
                              t_sjrkl = t_sjrkl.add(new BigDecimal(sjrkl));

                            t_sl = t_sl.add(sl);
                            t_hssl = t_hssl.add(hssl);
                            t_je = t_je.add(je);

                            String hthwid=detail.get("hthwid");
                            boolean isimport = !hthwid.equals("");//如果是引入合同当前行产品编码不能输入
                            if(zt.equals("0"))
                              isCanRework = engine.erp.baseinfo.BasePublicClass.isRevamp(list, "sjrkl", i);//进货单状态在未审状态时，判断该条纪录是否能被修改
                            String detailReadonly = isCanRework ? readonly : "readonly";
                            boolean isline = isimport || !isCanRework || !isHasDeptLimit;
                            String Class = isline ? "class=ednone" : detailClass;//从表Class模式
                         %>
                        <tr id="rowinfo_<%=i%>">
                          <td class="td" nowrap><%=i+1%></td>
                          <td class="td" nowrap align="center">
                            <%if(!isEnd && !isimport && isCanRework && isHasDeptLimit){%>
                          <input type="hidden" name="singleIdInput_<%=i%>" value="">
                          <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                          onClick="ProdSingleSelect('form1','srcVar=singleIdInput_<%=i%>','fieldVar=cpid', form1.storeid.value,'sumitForm(<%=buyOrderGoodsInitBean.SINGLE_PRODUCT_ADD%>,<%=i%>)')">
                          <%}if(!isEnd && isCanRework && isHasDeptLimit){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                            <%}%>
                          </td>
                          <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                          String hsbl = prodRow.get("hsbl");
                          detail.put("hsbl", hsbl);
                         %>
                          <%RowMap  buyOrderRow= buyOrderBean.getLookupRow(detail.get("hthwid"));%>
                          <td class="td" nowrap><%=buyOrderRow.get("htbh")%></td>
                          <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                          <input type="text" <%=Class%> onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>><input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'></td>
                          <td class="td" nowrap><input type="text" <%=Class%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>></td>
                           <td class="td" nowrap><%=prodRow.get("th")%>              </td>
                          <td class="td" nowrap><input type="text" <%=isHsbj ? slClass_r : "class=ednone_r"%> onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=hssl%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=isHsbj ? slReadonly : "readonly"%>></td>
                          <td class="td" nowrap><input type="text" class=ednone style="width:100%" onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                          <td class="td" nowrap><input type="text" <%=isHsbj ? "class=ednone_r" : slClass_r%> onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=sl%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=isHsbj ? "readonly" : slReadonly%>></td>
                          <td class="td" nowrap><input type="text" class=ednone style="width:100%" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                          <td class="td" nowrap><input type="text" class=ednone_r onKeyDown="return getNextElement();" id="sjrkl_<%=i%>" name="sjrkl_<%=i%>" value='<%=detail.get("sjrkl")%>' maxlength='<%=list.getColumn("sjrkl").getPrecision()%>' readonly></td>
                         <td class="td" nowrap><input type="text" <%=detailClass%> style="width:65" onKeyDown="return getNextElement();" name="jhrq_<%=i%>" id="jhrq_<%=i%>"value='<%=detail.get("jhrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)"></td>
                          <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="gyszyh_<%=i%>" id="gyszyh_<%=i%>"value='<%=detail.get("gyszyh")%>' maxlength='10' <%=readonly%>></td>
                          <td class="td" nowrap align="right"><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>'<%=readonly%>></td>

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


                        </tr>
                        <%}%>
                        <tr id="rowinfo_end">
                          <td class="td">&nbsp;</td>
                          <td class="tdTitle" nowrap>合计</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td align="right" class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_hssl.toString(),loginBean.getQtyFormat()) %>' readonly></td>
                          <td class="td">&nbsp;</td>
                          <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_sl.toString(),loginBean.getQtyFormat())%>' readonly></td>
                          <td class="td">&nbsp;</td>
                         <td class="td">&nbsp;</td>
                          <td align="right" class="td">&nbsp;</td>
                          <td class="td"></td>
                          <td class="td">&nbsp;</td>
                        </tr>
                      </table>

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
        <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("czrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("czy")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
             <%if(!isEnd){%>
              <input type="hidden" name="singleOrder" value="引入合同" >
              <input name="btnback" class="button" type="button" value="引入合同(W)" style="width:100" onClick="if(form1.storeid.value==''){alert('请选择仓库');return;}OrderSingleSelect('form1','srcVar=singleOrder','fieldVar=htid',form1.dwtxid.value,form1.storeid.value,'sumitForm(<%=buyOrderGoodsInitBean.SINGLE_SELECT_ORDER%>)')">
                <pc:shortcut key="w" script="importOrder();"/>
              <input type="hidden" name="importOrder" value="引合同货物" onchange="sumitForm(<%=buyOrderGoodsInitBean.DETAIL_ORDER_ADD%>)">
              <input name="btnback" class="button" type="button" value="引合同货物(E)" style="width:100" onClick="if(form1.dwtxid.value==''){alert('请选择供货单位');return;}if(form1.storeid.value==''){alert('请选择仓库');return;}OrderMultiSelect('form1','srcVar=importOrder&dwtxid='+form1.dwtxid.value+'&storeid='+form1.storeid.value)" border="0">
                <pc:shortcut key="e" script="importOrderGoods();"/>
              <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
                <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
                 <%}if(!isRead || !isHasDeptLimit){%>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
              <%if(isCanDelete && isCanAmend && !buyOrderGoodsInitBean.isRep){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
             <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <%if(!buyOrderGoodsInitBean.isApprove && !buyOrderGoodsInitBean.isRep){%><input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
              	  <pc:shortcut key="c" script='backList();'/><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=buyOrderGoodsInitBean.adjustInputSize(new String[]{"cpbm","product", "sl", "hssl", "dj", "je","sxz","sjrkl","gyszyh","bz"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
  }
    function importOrder(){
      if(form1.storeid.value=='')
      {
        alert('请选择仓库');return;
      }
     OrderSingleSelect('form1','srcVar=singleOrder','fieldVar=htid',form1.dwtxid.value,form1.storeid.value,'sumitForm(<%=buyOrderGoodsInitBean.SINGLE_SELECT_ORDER%>)')
     }
     function importOrderGoods(){
       if(form1.dwtxid.value=='')
       {
         alert('请选择供货单位');
         return;
       }
       if(form1.storeid.value=='')
       {
         alert('请选择仓库');
         return;
       }
       OrderMultiSelect('form1','srcVar=importOrder&dwtxid='+form1.dwtxid.value+'&storeid='+form1.storeid.value)
     }
       function sl_onchange(i, isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      var hsslObj = document.all['hssl_'+i];
      var hsblObj = document.all['hsbl_'+i];
      var djObj = document.all['dj_'+i];
      var jeObj = document.all['je_'+i];
      var sjrklObj = document.all['sjrkl_'+i];
      var djlx = <%=djlx%>;
      if('<%=bjfs%>'=="0")//报价方式为按计量单位报价时判断
      {
        if(slObj.value=="")
          return;
        if(isNaN(slObj.value)){
          alert("输入的数量非法");
          slObj.focus();
          return;
        }
        if(slObj.value<=0){
          alert("不能输入小于等于零的数")
              return;
        }
        if(hsblObj.value!="" && !isNaN(hsblObj.value))
          hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
        else
          hsslObj.value="";
        if(isNaN(djObj.value))
        {
          alert('输入的单价非法');
          return;
        }
        if(djObj.value=="")
          return;
        else if(slObj.value!="" && !isNaN(slObj.value))
          jeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value));
      }
      else//按换算单位报价的判断和计算
      {
        if(hsslObj.value=="")
          return;
        if(isNaN(hsslObj.value)){
          alert("输入的换算数量非法");
          hsslObj.focus();
          return;
        }
          if(hsslObj.value<=0){
            alert("不能输入小于等于零的数")
            return;
        }
        if(hsblObj.value!="" && !isNaN(hsblObj.value))
          slObj.value= formatQty(parseFloat(hsslObj.value) * parseFloat(hsblObj.value));
        else
          slObj.value="";
        if(djObj.value=="")
          return;
        if(hsslObj.value!="" && !isNaN(hsslObj.value))
          jeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value));
      }
      cal_tot('je');
      cal_tot('sl');
      cal_tot('hssl');
    }
    function dj_onchange(i, isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      var hsslObj = document.all['hssl_'+i];
      var hsblObj = document.all['hsbl_'+i];
      var djObj = document.all['dj_'+i];
      var jeObj = document.all['je_'+i];
      var sjrklObj = document.all['sjrkl_'+i];
      var bjfs = <%=bjfs%>;
      if(djObj.value=="")
        return;
      if(isNaN(djObj.value)){
        alert("输入的单价非法");
        djObj.focus();
        return;
      }
      if(bjfs==0)
      {
        if(hsblObj.value!="" && !isNaN(hsblObj.value))
          hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
        else
          hsslObj.value="";
        if(djObj.value=="")
          jeObj.value=0;
        else if(slObj.value!="" && !isNaN(slObj.value))
           jeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value));
      }
      else
      {
        if(hsblObj.value!="" && !isNaN(hsblObj.value))
          slObj.value= formatQty(parseFloat(hsslObj.value) * parseFloat(hsblObj.value));
        else
          slObj.value="";
        if(djObj.value=="")
          jeObj.value=0;
        else if(hsslObj.value!="" && !isNaN(hsslObj.value))
          jeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value));
      }
      cal_tot('je');
      cal_tot('sjrkl');
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
        else if(type == 'sjrkl')
          tmpObj = document.all['sjrkl_'+i];
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
      else if(type == 'sjrkl')
        document.all['t_sjrkl'].value = formatSum(tot);
    }
  function OrderMultiSelect(frmName, srcVar, methodName,notin)
 {
   var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
   var winName= "GoodsProdSelector";
   paraStr = "../buy/import_order_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
   if(methodName+'' != 'undefined')
     paraStr += "&method="+methodName;
   if(notin+'' != 'undefined')
   paraStr += "&notin="+notin;
   newWin =window.open(paraStr,winName,winopt);
   newWin.focus();
  }
  function OrderSingleSelect(frmName,srcVar,fieldVar,curID,storeid,methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "OrderSingleSelector";
    paraStr = "../buy/order_single_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&dwtxid="+curID+"&storeid="+storeid;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function ProdSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
  {
    paraStr = "../pub/productselect.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&storeid="+curID;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    openSelectUrl(paraStr, "SingleProdSelector", winopt2)
  }
</script>
<%if(buyOrderGoodsInitBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
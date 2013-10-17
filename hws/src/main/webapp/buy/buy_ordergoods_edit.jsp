<%--采购进货单编辑页面从表--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
  String op_instore ="op_instore";
%><%
  engine.erp.buy.B_BuyOrderGoods buyOrderGoodsBean = engine.erp.buy.B_BuyOrderGoods.getInstance(request);
  String pageCode = "buy_ordergoods";
  //boolean hasApproveLimit = isApprove && loginBean.hasLimits(pageCode, op_approve);

%>
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
  location.href='buy_ordergoods.jsp';
}
function corpCodeSelect(obj)
{
  ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=buyOrderGoodsBean.ONCHANGE%>)');
}
function corpNameSelect(obj)
{
  ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=buyOrderGoodsBean.ONCHANGE%>)');
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
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value,'product_change('+i+')');
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value,'product_change('+i+')');
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




  function product_change(i){
    document.all['dmsxid_'+i].value="";
    document.all['sxz_'+i].value="";
    //document.all['widths_'+i].value="";
    //associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '', true);
}
</script>
<%String retu = buyOrderGoodsBean.doService(request, response);
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
  engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = buyOrderGoodsBean.getMaterTable();
  EngineDataSet list = buyOrderGoodsBean.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  //System.out.println("bjfs = "+ bjfs);
  String SYS_CUST_NAME = loginBean.getSystemParam("SYS_CUST_NAME");//客户名称,用于客制化程序
  //if ( !SYS_CUST_NAME.equals("essen") ) bjfs = "0";//如果是essen的话,那么程序就一没有换算数量,一直用计量单位来报价
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  HtmlTableProducer masterProducer = buyOrderGoodsBean.masterProducer;
  HtmlTableProducer detailProducer = buyOrderGoodsBean.detailProducer;
  RowMap masterRow = buyOrderGoodsBean.getMasterRowinfo();
  RowMap[] detailRows= buyOrderGoodsBean.getDetailRowinfos();
  String zt=masterRow.get("zt");
  String deptid = masterRow.get("deptid");
  String czyid = masterRow.get("czyid");
  boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, czyid);//判断登陆员工是否有操作改制单人单据的权限
  if(buyOrderGoodsBean.isApprove)
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
  boolean isEnd = buyOrderGoodsBean.isRep || buyOrderGoodsBean.isApprove || (!buyOrderGoodsBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !buyOrderGoodsBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(buyOrderGoodsBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = (isEnd || !isHasDeptLimit) ? "class=edline" : "class=edbox";
  String detailClass = (isEnd || !isHasDeptLimit) ? "class=ednone" : "class=edFocused";
  String detailClass_r = (isEnd || !isHasDeptLimit) ? "class=ednone_r" : "class=edFocused_r";
  String readonly = (isEnd || !isHasDeptLimit) ? " readonly" : "";
  String masterReadonly = isCanAmend ? readonly : "readonly";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("2") ? "已入库" : (zt.equals("9") ? "审批中" : "未审核"));
  String head = djlx.equals("1") ? "采购进货单" : "采购退货单";
  boolean isAdd = buyOrderGoodsBean.isDetailAdd;
  boolean  isRead = buyOrderGoodsBean.isRep || buyOrderGoodsBean.isApprove || (!buyOrderGoodsBean.masterIsAdd() && (zt.equals("9") || zt.equals("2")));//表示在审批中或已入库不能修改
  isRead = isRead || !(buyOrderGoodsBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  String slReadonly = isEnd ?  "readonly" : "";//数量和单价在审批的时候可以修改,汇率在审批通过也可以修改
  String slClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String dmsxClass = (isRead || !isHasDeptLimit) ? "class=ednone" : "class=edFocused";
  String shdzClass = isEnd? "class=edline":"class=edbox";
  String shdzReadonly = isEnd? "readonly":"";
 %>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="djlx" value='<%=djlx%>'>
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="860">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">到货送检单(<%=title%>)</td>
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
                    <%String store_Change = "if(form1.storeid.value!='"+masterRow.get("storeid")+"')sumitForm("+buyOrderGoodsBean.STORE_CHANGE+")";%>
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:90' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="1" style="width:90" onSelect="<%=store_Change%>"> <%=storeBean.getList(masterRow.get("storeid"))%>
                    </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%String onChange = "if(form1.deptid.value!='"+masterRow.get("deptid")+"')sumitForm("+buyOrderGoodsBean.DEPT_CHANGE+")";%>
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
                  <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=buyOrderGoodsBean.ONCHANGE%>)');"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';sumitForm(<%=buyOrderGoodsBean.ONCHANGE%>)">
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

                  <td noWrap class="tdTitle">收货地址</td>
                  <td noWrap class="td" colspan="3"><input type="text" <%=shdzClass%> name="shdz" onKeyDown="return getNextElement();" value='<%=masterRow.get("shdz")%>' style="width:280"   <%=shdzReadonly%>></td>

                </tr>

                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:850;overflow-y:auto;overflow-x:auto;">
                      <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                          <td nowrap width=15>
                        </td>
                          <td height='20' align="center" nowrap>

                          </td>
                          <td height='20' rowspan="2" nowrap>所属订单</td>
                          <td height='20' colspan="4" nowrap>产品</td>
                          <td height='20' rowspan="2" nowrap>送检量</td>


                          <td height='20' nowrap colspan="4" >检验结果</td>

                          <td nowrap rowspan="2" >摘要</td>

                        </tr>
                        <tr class="tableTitle">
                          <td nowrap width=15>
                        </td>
                          <td height='20' align="center" nowrap>

                          </td>
                          <td height='20' nowrap>产品编码</td>
                          <td height='20' nowrap>品名 规格</td>
                          <td height='20' nowrap>图号</td>
                          <td height='20' nowrap>计量单位</td>
                          
                          

                          <td nowrap>检验结果</td>
                          <td nowrap>总接收量</td>
                          <td nowrap>检验说明</td>
                          <td nowrap>允许入库</td>


                        </tr>
                        <%prodBean.regData(list,"cpid");
                          buyOrderBean.regData(list,"hthwid");
                          propertyBean.regData(list,"dmsxid");
                          BigDecimal t_sl = new BigDecimal(0), t_hssl = new BigDecimal(0), t_je = new BigDecimal(0),t_ybje = new BigDecimal(0), t_sjrkl = new BigDecimal(0);
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
                            String s_ybje = detail.get("ybje");
                            BigDecimal ybje = s_ybje.length() > 0 ? b_djlx.multiply(new BigDecimal(s_ybje)) : new BigDecimal(0);
                            String sjrkl = detail.get("sjrkl");
                            if(buyOrderGoodsBean.isDouble(sjrkl))
                              t_sjrkl = t_sjrkl.add(new BigDecimal(sjrkl));

                            t_sl = t_sl.add(sl);
                            t_hssl = t_hssl.add(hssl);
                            t_je = t_je.add(je);
                            t_ybje = t_ybje.add(ybje);

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
                         <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                            <%if(!isEnd && !isimport && isCanRework && isHasDeptLimit){%>
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
                          <input type="text" class=ednone onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>'   readonly>
                          <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'>
                          <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                           <input type='hidden' id='isprops_<%=i%>' name='isprops_<%=i%>' value='<%=prodRow.get("isprops")%>'>
                          <input type="hidden" <%=slClass_r%> onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' onchange="dj_onchange(<%=i%>, false)" <%=slReadonly%>>
                          <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="je_<%=i%>" name="je_<%=i%>" value='<%=detail.get("je")%>' maxlength='<%=list.getColumn("je").getPrecision()%>' onchange="je_onchange(<%=i%>, false)" <%=readonly%>>
                          <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="ybje_<%=i%>" name="ybje_<%=i%>" value='<%=detail.get("ybje")%>' maxlength='<%=list.getColumn("ybje").getPrecision()%>' onchange="je_onchange(<%=i%>, true)" <%=readonly%>>

                           </td>
                          <td class="td" nowrap><%=prodRow.get("product")%></td>
                          <td class="td" nowrap><%=prodRow.get("th")%></td>
                          <td class="td" nowrap><%=prodRow.get("jldw")%></td>
                          
                          <td class="td" nowrap><input type="text" <%=slClass_r%> onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=sl%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=slReadonly%>></td>
                          
                          
                          
                          <td class="td" nowrap><input type="hidden" class=ednone_r onKeyDown="return getNextElement();" id="check_result_<%=i%>" name="check_result_<%=i%>" value='<%=detail.get("check_result")%>' maxlength='<%=list.getColumn("check_result")%>' readonly>
                          <%String check_result = detail.get("check_result");%>
                          <%if(check_result.equals("A")){%>合格<%}else if (check_result.equals("B")){%>挑选<%}else if (check_result.equals("C")){%>让步<%}else if (check_result.equals("D")){%>退货<%}%>
                          
                          </td>
                          <td class="td" nowrap><%=detail.get("check_number")%>
                          <input type="hidden" class=ednone_r onKeyDown="return getNextElement();" id="check_number_<%=i%>" name="check_number_<%=i%>" value='<%=detail.get("check_number")%>' maxlength='<%=list.getColumn("check_number").getPrecision()%>' readonly></td>
                         <td class="td" nowrap><%=detail.get("check_explain")%>
                         <input type="hidden" class=ednone_r onKeyDown="return getNextElement();" id="check_explain_<%=i%>" name="check_explain_<%=i%>" value='<%=detail.get("check_explain")%>' maxlength='<%=list.getColumn("check_explain")%>' readonly>
                         
                         </td>
                          <td class="td" nowrap><input type="hidden" class=ednone_r onKeyDown="return getNextElement();" id="check_permit_<%=i%>" name="check_permit_<%=i%>" value='<%=detail.get("check_permit")%>' maxlength='<%=list.getColumn("check_permit")%>' readonly>
                          <%String check_permit = detail.get("check_permit");%>
                          <%if(check_permit.equals("1")){%>是<%}else if (check_permit.equals("-1")){%>否<%}%>
                          
                          </td>
                          <td class="td" nowrap align="right">
                          <input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>'<%=readonly%>>
                          </td>
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
                        </tr>
                        <%}%>
                        <tr id="rowinfo_end">
                          <td class="td">&nbsp;</td>
                          <td class="tdTitle" nowrap>合计</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>

                          <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_sl.toString(),loginBean.getQtyFormat())%>' readonly></td>
                          <td class="td">&nbsp;</td>

                          <td align="right" class="td"><input id="t_sjrkl" name="t_sjrkl" type="text" class="ednone_r" style="width:100%" value='<%=t_sjrkl%>' readonly></td>
                          <td align="right" class="td">&nbsp;</td>
                          <td class="td"></td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                        </tr>
                      </table>
                    </div>
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
              <input name="btnback" class="button" type="button" value="引入合同(W)" style="width:100" onClick="if(form1.storeid.value==''){alert('请选择仓库');return;}OrderSingleSelect('form1','srcVar=singleOrder','fieldVar=htid',form1.dwtxid.value,form1.storeid.value,'sumitForm(<%=buyOrderGoodsBean.SINGLE_SELECT_ORDER%>)')">
                <pc:shortcut key="w" script="importOrder();"/>
              <input type="hidden" name="importOrder" value="引合同货物" onchange="sumitForm(<%=buyOrderGoodsBean.DETAIL_ORDER_ADD%>)">
              <input name="btnback" class="button" type="button" value="引合同货物(E)" style="width:100" onClick="if(form1.dwtxid.value==''){alert('请选择供货单位');return;}if(form1.storeid.value==''){alert('请选择仓库');return;}OrderMultiSelect('form1','srcVar=importOrder&dwtxid='+form1.dwtxid.value+'&storeid='+form1.storeid.value)" border="0">
                <pc:shortcut key="e" script="importOrderGoods();"/>
              <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
                <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
              <%if(isCanDelete && isCanAmend && !buyOrderGoodsBean.isRep){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
             <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <%if(!buyOrderGoodsBean.isApprove && !buyOrderGoodsBean.isRep){%><input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
              	  <pc:shortcut key="c" script='backList();'/><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=buyOrderGoodsBean.adjustInputSize(new String[]{"cpbm","product", "sl", "dj", "je","sxz","sjrkl","gyszyh","bz","ybje"}, "form1", detailRows.length)%>
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
     OrderSingleSelect('form1','srcVar=singleOrder','fieldVar=htid',form1.dwtxid.value,form1.storeid.value,'sumitForm(<%=buyOrderGoodsBean.SINGLE_SELECT_ORDER%>)')
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
     function big_change(isBigunit){
        if(<%=detailRows.length%><1)
          return;
        for(t=0; t<<%=detailRows.length%>; t++){
          sl_onchange(t,isBigunit);
        }
     }
     function hl_onchange()
     {
     var hlObj = form1.hl;
     if(hlObj.value=="")
       return;
     if(hlObj.value==0) {
       alert('汇率不能为零');
       return;
     }
     if(isNaN(hlObj.value)){
       alert('输入的汇率非法');
       return;
     }
     var bjfs = <%=bjfs%>;
     isBigunit = (bjfs==0) ? false : true;
     for(k=0; k<<%=detailRows.length%>; k++)
       {
       sl_onchange(k,isBigunit);
       /**
       var slObj = document.all['sl_'+i];
       var djObj = document.all['dj_'+i];
       var ybjeObj = document.all['ybje_'+i];
       if(slObj.value=="" || isNaN(slObj.value) || isNaN(djObj.value))
         continue;
       if(djObj.value=="")
         continue;
       ybjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
     */
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
       var hsslObj = document.all['hssl_'+i];
       var djObj = document.all['dj_'+i];
       var jeObj = document.all['je_'+i];
       var hsblObj = document.all['truebl_'+i];
       var sjrklObj = document.all['sjrkl_'+i];
       var hlObj = form1.hl;
       var ybjeObj = document.all['ybje_'+i];
       var djlx = <%=djlx%>;
       var bjfs = <%=bjfs%>;
       if(bjfs==0)//报价方式为按计量单位报价时判断
       {
         if(slObj.value=="")
         return;
         if(isNaN(slObj.value)){
           alert("输入的数量非法");
           slObj.focus();
           return;
         }
         if(slObj.value<0){
           alert("不能输入小于等于零的数")
               return;
        }
         <%if (!SYS_CUST_NAME.equals("essen"))
         {
         %>
        if(hsblObj.value!="" && !isNaN(hsblObj.value) && hsblObj.value!="0")
          hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
        else
          hsslObj.value="";
        cal_tot('hssl');
        <%}%>
        cal_tot('sl');
        if(isNaN(djObj.value))
        {
          alert('输入的单价非法');
          return;
        }
        if(djObj.value=="")
          return;
        if(slObj.value!="" && !isNaN(slObj.value)){
          jeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value));
          if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
            ybjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
          else
            ybjeObj.value ='';
        }
        else{
          jeObj.value='';
          ybjeObj.value ='';
        }
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
          if(hsslObj.value<0){
            alert("不能输入小于等于零的数")
            return;
        }
        if(hsblObj.value!="" && !isNaN(hsblObj.value))
        {
          slObj.value= formatQty(parseFloat(hsslObj.value) * parseFloat(hsblObj.value));
        }
        else
          slObj.value="";
        cal_tot('sl');
        cal_tot('hssl');

        if(djObj.value=="")
          return;
        if(hsslObj.value!="" && !isNaN(hsslObj.value)){
          jeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value));
          if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
            ybjeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
          else
            ybjeObj.value ='';
        }
        else{
          jeObj.value='';
          ybjeObj.value ='';
        }
      }

      cal_tot('je');
      cal_tot('ybje');
    }
    function dj_onchange(i, isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      var hsslObj = document.all['hssl_'+i];
      var hsblObj = document.all['truebl_'+i];
      var djObj = document.all['dj_'+i];
      var jeObj = document.all['je_'+i];
      var sjrklObj = document.all['sjrkl_'+i];
      var bjfs = <%=bjfs%>;
      var hlObj = form1.hl;
      var ybjeObj = document.all['ybje_'+i];
      if(djObj.value==""){
        jeObj.value='';
        ybjeObj.value='';
        return;
      }
      if(isNaN(djObj.value)){
        alert("输入的单价非法");
        djObj.focus();
        return;
      }
      if(bjfs==0)
      {
        if(slObj.value!="" && !isNaN(slObj.value)){
           jeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value));
           if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
             ybjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
           else
             ybjeObj.value ='';
        }
        else{
          jeObj.value='';
          ybjeObj.value ='';
        }
      }
      else
      {
        if(hsslObj.value!="" && !isNaN(hsslObj.value)){
          jeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value));
          if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
            ybjeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
          else
             ybjeObj.value ='';
        }
        else{
          jeObj.value='';
          ybjeObj.value ='';
        }
      }
      cal_tot('je');
      cal_tot('ybje');
      cal_tot('sjrkl');
    }
    function je_onchange(i, isBigUnit)
   {
     var slObj = document.all['sl_'+i];
     var djObj = document.all['dj_'+i];
     var jeObj = document.all['je_'+i];
     var hsslObj = document.all['hssl_'+i];
     var hsblObj = document.all['truebl_'+i];
     var hlObj = form1.hl;
     var bjfs = <%=bjfs%>;
     var ybjeObj = document.all['ybje_'+i];
     var obj = isBigUnit ? ybjeObj : jeObj;
     var showText = isBigUnit ? "输入的原币金额非法" : "输入的金额非法";
     var showText2 = isBigUnit ? "输入的原币金额小于零" : "输入的金额小于零";
     var changeObj = isBigUnit ? jeObj : ybjeObj;
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
     if(!isBigUnit){
       if(bjfs==0){
         if(slObj.value!="" && !isNaN(slObj.value && slObj.value!="0"))
           djObj.value = formatPrice(parseFloat(jeObj.value) / parseFloat(slObj.value));
       }
       else{
         if(hsslObj.value!="" && !isNaN(hsslObj.value && hsslObj.value!="0"))
           djObj.value = formatPrice(parseFloat(jeObj.value) / parseFloat(hsslObj.value));
       }
       if(hlObj.value!="" && !isNaN(hlObj.value) && hlObj!="0")
         ybjeObj.value = formatSum(parseFloat(jeObj.value)/parseFloat(hlObj.value));
       else
         ybjeObj.value='';
     }
     else{
       if(bjfs==0){
         if(hlObj.value!="" && !isNaN(hlObj.value) && hlObj!="0")
           jeObj.value = formatSum(parseFloat(ybjeObj.value) * parseFloat(hlObj.value));
         if(hlObj.value!="" && !isNaN(hlObj.value) && hlObj!="0" && slObj.value!="" && !isNaN(slObj.value) && slObj.value!="0")
           djObj.value = formatPrice(parseFloat(ybjeObj.value)* parseFloat(hlObj.value) / parseFloat(slObj.value));
       }
       else{
         if(hlObj.value!="" && !isNaN(hlObj.value) && hlObj!="0")
            jeObj.value = formatSum(parseFloat(ybjeObj.value) * parseFloat(hlObj.value));
         if(hlObj.value!="" && !isNaN(hlObj.value) && hlObj!="0" && hsslObj.value!="" && !isNaN(hsslObj.value) && hsslObj.value!="0")
           djObj.value = formatPrice(parseFloat(ybjeObj.value)* parseFloat(hlObj.value) / parseFloat(hsslObj.value));
       }
     }
     cal_tot('je');
     cal_tot('ybje');
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
        else if(type == 'ybje')
          tmpObj = document.all['ybje_'+i];
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
      else if(type == 'ybje')
        document.all['t_ybje'].value = formatSum(tot);
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
<%if(buyOrderGoodsBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
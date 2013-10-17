<%--损溢单从表--%>
<%
/**
 * 2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg
 * 2004-3-30 15:27 修改 javascript修改页面上可以输入数值的数量,换算数量栏位,
 *   数量,换算数量互动关系仅在如此情况下有效:当修改其中的一个如果另一个是空的话则跟随改变
 */
%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.store.B_Destroy destroyBean = engine.erp.store.B_Destroy.getInstance(request);
  destroyBean.djxz = 7;
  String pageCode = "report_destroy_list";
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
  location.href='report_destroy_list.jsp';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsbl_'+i+'&srcVar=hsdw_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsbl&fieldVar=hsdw', obj.value, 'product_change('+i+')');
}
function productNameSelect(obj,i)
{
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsbl_'+i+'&srcVar=hsdw_'+i+'&storeid='+form1.storeid.value,
               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsbl&fieldVar=hsdw', obj.value,'product_change('+i+')');
}
<%--2004-4-23 14:24新增 新增用于规格属性选择的js函数 yjg--%>
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
<%String retu = destroyBean.doService(request, response);
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
  engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp storeLossBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_LOSS);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  engine.project.LookUp pdhmBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_CHECK);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = destroyBean.getMaterTable();
  EngineDataSet list = destroyBean.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  HtmlTableProducer masterProducer = destroyBean.masterProducer;
  HtmlTableProducer detailProducer = destroyBean.detailProducer;
  RowMap masterRow = destroyBean.getMasterRowinfo();
  RowMap[] detailRows= destroyBean.getDetailRowinfos();
  String zt=masterRow.get("zt");
  String zdrid = masterRow.get("zdrid");//得到该单据的制单员id
  String loginId = destroyBean.loginId;
  //2004-5-2 16:43 为给明细数据集加入分页功能
  String count = String.valueOf(list.getRowCount());
  int iPage = 30;
  String pageSize = String.valueOf(iPage);
  if(destroyBean.isApprove)
  {
    personBean.regData(ds, "personid");
  }
  boolean isEnd = destroyBean.isReport || destroyBean.isApprove || (!destroyBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成2.14
  boolean isCanDelete = !isEnd && !destroyBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete)
                        && loginId.equals(zdrid);//没有结束,在修改状态,并有删除权限,2004-08-04 并且登陆人等于制单人
  isEnd = isEnd
          || !(destroyBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit))
          || !loginId.equals(zdrid);//2004-08-04 新增 只有当前登陆人是制单人的时候才可以修改 yjg


  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("9") ? "审批中" : (zt.equals("2")?"记帐":"未审核"));
  boolean isAdd = destroyBean.isDetailAdd;
  String SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
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
            <td class="activeVTab">损溢单(<%=title%>)
             <%
              boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
              %>
              <%if (!isAtFirst)
              {%>
              <a href="#" title="到上一笔(ALT+Z)" onClick="sumitForm(<%=destroyBean.PRIOR%>)">&lt</a>
              <pc:shortcut key='z' script='<%="sumitForm("+destroyBean.PRIOR+")"%>'/>
             <%}%>
               <%if (!isAtLast)
              {%>
              <a href="#" title="到上一笔(ALT+X)" onClick="sumitForm(<%=destroyBean.NEXT%>)">&gt</a>
              <pc:shortcut key='x' script='<%="sumitForm("+destroyBean.NEXT+")"%>'/>
             <%}%>
          </td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%corpBean.regData(ds,"dwtxid");String jsr = masterRow.get("jsr");
                  if(!isEnd)
                    storeAreaBean.regConditionData(ds,"storeid");
                  %>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjdh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sfdjdh" value='<%=masterRow.get("sfdjdh")%>' maxlength='<%=ds.getColumn("sfdjdh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sfrq" value='<%=masterRow.get("sfrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.sfrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:110" onSelect="deptChange()"><%--2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg--%>
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                   </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjlbid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+storeLossBean.getLookupName(masterRow.get("sfdjlbid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="sfdjlbid" addNull="0" style="width:110">
                      <%=storeLossBean.getList(masterRow.get("sfdjlbid"))%> </pc:select>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "if(form1.storeid.value!='"+masterRow.get("storeid")+"'){sumitForm("+destroyBean.STORE_ONCHANGE+");}";%>
                    <%if(isEnd) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="1" style="width:110" onSelect="<%=sumit%>">
                      <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jfkm").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="jfkm" value='<%=masterRow.get("jfkm")%>' maxlength='<%=ds.getColumn("jfkm").getPrecision()%>' style="width:110" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bz").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="bz" value='<%=masterRow.get("bz")%>' maxlength='<%=ds.getColumn("bz").getPrecision()%>' style="width:110" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsr").getFieldname()%></td>
                  <td  noWrap class="td"><%if(isEnd){%> <input type="text" name="jsr" value='<%=masterRow.get("jsr")%>' maxlength='<%=ds.getColumn("jsr").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  <%}else {%>
                  <pc:select combox="1" className="edFocused" name="jsr" value="<%=jsr%>" style="width:110">
                  <%=personBean.getList()%></pc:select>
                  <%}%>
                  </td>
                  </tr>
                  <tr>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("pdid").getFieldname()%>
                   </td>
                   <td  noWrap class="td"><input type="text" class="edline" name="pdhm" value="<%=pdhmBean.getLookupName(masterRow.get("pdid"))%>" readonly></td>
                  </tr>
                <%
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                %>
                <tr>
                  <td class="td" colspan="8" nowrap>
                   <pc:navigator id="report_destroy_listNav" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+destroyBean.TURNPAGE+")"%>'/>
                 </td>
               </tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){%>
                          <input name="image" class="img" type="image" title="新增(ALT+A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+Operate.DETAIL_ADD+")"%>'/>
                          <%}%>
                        </td>
                         <td nowrap>产品编码</td>
                         <td nowrap>品名 规格</td>
                         <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                          <td nowrap>计量单位</td>
                          <td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
                          <td nowrap>换算单位</td>
                          <td nowrap><%=detailProducer.getFieldInfo("ph").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("kwid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                      </tr>
                    <%
                      BigDecimal t_sl = new BigDecimal(0),t_hssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      //2004-5-2 16:43 为明细资料页面加入页
                      int min = report_destroy_listNav.getRowMin(request);
                      int max = report_destroy_listNav.getRowMax(request);

                      destroyBean.min = min;
                      destroyBean.max = max > detailRows.length-1 ? detailRows.length-1 : max;
                      ArrayList cpidList = new ArrayList(max-min+1);
                      ArrayList dmsxidList = new ArrayList(max-min+1);
                      for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        String cpid = detail.get("cpid");

                        cpidList.add(cpid);

                      }
                      prodBean.regData((String[])cpidList.toArray(new String[cpidList.size()]));
                      //propertyBean.regData((String[])dmsxidList.toArray(new String[dmsxidList.size()]));//02.15 新增 新增注册dmsxid属性id因为不注册下面页面就会出错 yjg

                      list.goToRow(min);
                      for(i=min; i<=max && i<detailRows.length; i++)
                      {
                        detail = detailRows[i];
                      String sl = detail.get("sl");
                      if(destroyBean.isDouble(sl))
                        t_sl = t_sl.add(new BigDecimal(sl));
                      String hssl = detail.get("hssl");
                      if(destroyBean.isDouble(hssl))
                        t_hssl = t_hssl.add(new BigDecimal(hssl));
                      String kwName = "kwid_"+i;
                      //String dmsxidName = "dmsxid_"+i;
                    %>
                      <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                        <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd){%>
                        <input type="hidden" name="mutibatch_<%=i%>" value="" onchange="sumitForm(<%=destroyBean.SELECTBATCH%>,<%=i%>)">
                         <input type="hidden" name="singleIdInput_<%=i%>" value="">
                          <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                            onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=hsbl_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl','&storeid='+form1.storeid.value,'product_change(<%=i%>)')">
                         <input name="image" class="img" type="image" title="配批号"
                            onClick="if(form1.cpid_<%=i%>.value==''){alert('请输入产品');return;}BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+form1.cpid_<%=i%>.value+'&storeid='+form1.storeid.value+'&dmsxid='+form1.dmsxid_<%=i%>.value)" src="../images/edit.old.gif" border="0">
                          <input name="image" class="img" type="image" title="删除" onClick="sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td><%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                        String hsbl = prodRow.get("hsbl");
                        detail.put("hsbl",hsbl);
                        String isprop = prodRow.get("isprops");
                        %>
                        <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=readonly%>>
                        <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'>
                        <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=readonly%>></td>
                        <%--03.05 18:21 新增 修改规格属性为可输入的.并且新增onChange时的函数.yjg--%>

                          <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onChange="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone  onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone  onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="ph_<%=i%>" name="ph_<%=i%>" value='<%=detail.get("ph")%>' maxlength='<%=list.getColumn("ph").getPrecision()%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请输入产品');return;}BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+form1.cpid_<%=i%>.value+'&storeid='+form1.storeid.value+'&row='+<%=i%>+'&dmsxid='+form1.dmsxid_<%=i%>.value<%="+'&ph='+"+"form1.ph_"+i+".value"%>, true, <%=i%>, <%=true%>,'methodName=sl_onchange(<%=i%>, false)')"  <%=readonly%>></td>
                          <td class="td" nowrap>
                            <%if(isEnd) out.print("<input type='text' name=" + kwName + " value='"+storeAreaBean.getLookupName(detail.get("kwid"))+"' class='ednone' readonly>");
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
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                       <td class="td">&nbsp;</td><td class="td">&nbsp;</td>

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
              <%if(!isEnd){%><input name="button2" type="button" class="button" title="保存添加(ALT+N)" value="保存添加(N)"  onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
                 <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="保存ALT+S)" value="保存(S)" onClick="sumitForm(<%=Operate.POST%>);">
                <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%}%>
              <%if(isCanDelete && !destroyBean.isReport){%>
                <input name="button3" type="button" class="button" title="删除(ALT+D)" value="删除(D)" onClick="buttonEventD();">
                  <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%if(!destroyBean.isApprove && !destroyBean.isReport){%>
                <input name="btnback" type="button" class="button" title="返回(ALT+C)" value="返回(C)" onClick="backList();">
                 <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
              <%--03.09 11:43 新增 新增关闭按钮提供给当此页面是被报表调用时使用. yjg--%>
              <%if(destroyBean.isReport){%>
               <input name="btnback" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)" onClick="window.close()">
                 <pc:shortcut key="t" script='<%="window.close()"%>'/>
              <%}%>
              <%--03.15 10:28 新增 新增打印单据按钮来把这张采购入库单页面上的内容打印出来. yjg--%>
              <input type="button" class="button" title="打印(ALT+P)" value="打印(P)" onclick="buttonEventP()">
                <pc:shortcut key="p" script='<%="buttonEventP()"%>'/>

            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=destroyBean.adjustInputSize(new String[]{"cpbm","product", "jldw", "ph", "hssl", "sl", "kwid", "sxz", "bz"},  "form1", (destroyBean.max-min+1), min)%>
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
     var obj = isBigUnit ? hsslObj : slObj;
     var showText = isBigUnit ? "输入的换算数量非法" : "输入的数量非法";
     var changeObj = isBigUnit ? slObj : hsslObj;
     if(obj.value=="")
       return;
     if(isNaN(obj.value)){
       alert(showText);
       obj.focus();
       return;
     }
     //2004-3-30 15:27 修改 javascript修改页面上可以输入数值的数量,换算数量栏位,
     //数量,换算数量互动关系仅在如此情况下有效:当修改其中的一个如果另一个是空的话则跟随改变
     if(!(changeObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1'))//是否强制转换
       {
       if(hsblObj.value!="" && !isNaN(hsblObj.value))
         changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
     }
     cal_tot('sl');
     cal_tot('hssl');
   }
   function cal_tot(type)
   {
     var tmpObj;
     var tot=0;
      for(i=<%=min%>; i<=<%=max%>; i++)
        {
        if(type == 'sl')
          tmpObj = document.all['sl_'+i];
        else if(type == 'hssl')
          tmpObj = document.all['hssl_'+i];
        else
          return;
        if(tmpObj.value!="" && !isNaN(tmpObj.value))
          tot += parseFloat(tmpObj.value);
      }
      if(type == 'sl')
        document.all['t_sl'].value = formatQty(tot);
      else if(type == 'hssl')
        document.all['t_hssl'].value = formatQty(tot);
   }
   //删除
   function buttonEventD()
   {
     if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);
   }
        //打印
   function buttonEventP()
   {
     location.href='../pub/pdfprint.jsp?code=report_destroy_edit_bill&operate=<%=destroyBean.PRINT_BILL%>&a$sfdjid=<%=masterRow.get("sfdjid")%>&src=../store/report_destroy_edit.jsp';
   }
       //2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg
   function deptChange()
   {
     associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jsr', 'deptid', eval('form1.deptid.value'), '');
   }
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
             + "&srcVarTwo=sl_"+i
             + "&srcVarTwo=ph_"+i
             + kwSrcVarCondition+"&srcVarTwo=dmsxid_"+i+"&srcVarTwo=sxz_"+i
             + "&fieldVar=zl&fieldVar=ph"
             + kwFieldVarCondition+"&fieldVar=dmsxid&fieldVar=sxz";
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
<%//&#$
if(destroyBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
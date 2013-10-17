<%--采购合同编辑页面从表--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.income.IN_BuyOrder InbuyOrderBean = engine.erp.income.IN_BuyOrder.getInstance(request);
  String pageCode = "income_order";
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
  location.href='buy_order.jsp';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function productHsbjSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=hsdw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=hsdw', obj.value);
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function productNameHsbjSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=hsdw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=hsdw', obj.value);
}
function corpCodeSelect(obj)
{
  ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=InbuyOrderBean.ONCHANGE%>)');
}
function corpNameSelect(obj)
{
  ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=InbuyOrderBean.ONCHANGE%>)');
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
</script>
<%String retu = InbuyOrderBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);
  engine.project.LookUp importApplyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_APPLY_GOODS);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  engine.project.LookUp countryBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_COUNTRY);
  engine.project.LookUp payBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = InbuyOrderBean.getMaterTable();
  EngineDataSet list = InbuyOrderBean.getDetailTable();
  HtmlTableProducer masterProducer = InbuyOrderBean.masterProducer;
  HtmlTableProducer detailProducer = InbuyOrderBean.detailProducer;
  RowMap masterRow = InbuyOrderBean.getMasterRowinfo();
  RowMap[] detailRows= InbuyOrderBean.getDetailRowinfos();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  String zt=masterRow.get("zt");
  String deptid = masterRow.get("deptid");
  String czyid = masterRow.get("czyid");
  boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, czyid);//判断登陆员工是否有操作改制单人单据的权限
  if(InbuyOrderBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
    deptBean.regData(ds, "deptid");
  }
  boolean isCanAmend = true;//判断取消审批后主表数据是否能修改
  boolean isCanRework = true;//判断取消审批后从表数据是否能修改
  if(zt.equals("0"))
    isCanAmend = engine.erp.baseinfo.BasePublicClass.isRework(list,"sjjhl");//取消审批后从表实际进货量如果有一条大于零，主表不能修改。
  boolean isEnd = InbuyOrderBean.isReport || InbuyOrderBean.isApprove || (!InbuyOrderBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !InbuyOrderBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(InbuyOrderBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = (isEnd || !isHasDeptLimit) ? "class=ednone" : "class=edFocused";
  String detailClass_r = (isEnd || !isHasDeptLimit) ? "class=ednone_r" : "class=edFocused_r";
  String readonly = (isEnd || !isHasDeptLimit) ? " readonly" : "";
  String masterReadonly = isCanAmend ? readonly : "readonly";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("9") ? "审批中" : (zt.equals("0") ? "未审核" : (zt.equals("4") ? "已作废" : (zt.equals("8") ? "已完成" : ""))));
  boolean isAdd = InbuyOrderBean.isDetailAdd;
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">进口订单(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                <%corpBean.regData(ds,"dwtxid");
                  if(!isEnd)
                    personBean.regConditionData(ds, "deptid");
                %>
                 <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("htbh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="htbh" value='<%=masterRow.get("htbh")%>' maxlength='<%=ds.getColumn("htbh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("htrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="htrq" value='<%=masterRow.get("htrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=masterReadonly%>>
                    <%if(!isEnd && isCanAmend && isHasDeptLimit){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.htrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">合同有效期</td>
                  <td colspan="3" noWrap class="td">
                    <input type="text" name="ksrq" value='<%=masterRow.get("ksrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=masterReadonly%>>
                    <%if(!isEnd && isCanAmend && isHasDeptLimit){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.ksrq);"></a>
                    <%}%>
                    -- <input type="text" name="jsrq" value='<%=masterRow.get("jsrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=masterReadonly%>>
                    <%if(!isEnd && isCanAmend && isHasDeptLimit){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.jsrq);"></a>
                    <%}%>
                  </td>

                </tr>
                <tr>
                <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("qddd").getFieldname()%></td>
                  <td noWrap class="td">
                    <input type="text" name="qddd" value='<%=masterRow.get("qddd")%>' maxlength='<%=ds.getColumn("qddd").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"<%=masterReadonly%>>
                  </td>
                 <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwtxid").getFieldname()%></td><%--购货单位--%>
                <%RowMap corpRow = corpBean.getLookupRow(masterRow.get("dwtxid"));%>
                  <td  noWrap class="td" colspan="3"><input type="text" name="dwdm" value='<%=corpRow.get("dwdm")%>' style="width:60" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpCodeSelect(this);" <%=masterReadonly%>>
				  <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>' >
                   <input type="text" name="dwmc" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:190" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpNameSelect(this);" <%=masterReadonly%>>
                    <%if(!isEnd && isCanAmend && isHasDeptLimit){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=InbuyOrderBean.ONCHANGE%>)');"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';sumitForm(<%=InbuyOrderBean.ONCHANGE%>)">
                    <%}%>
                  </td>
                 <%-- <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("khlx").getFieldname()%></td>
                  <td noWrap class="td">
                    <%String khlx=masterRow.get("khlx");%>
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+masterRow.get("khlx")+"' style='width:85' class='edline' readonly>");
                    else {%>
                    <pc:select name="khlx"  style="width:110" value='<%=khlx%>'>
                      <pc:option value=''></pc:option>
                      <pc:option value='A'>A</pc:option>
                      <pc:option value='C'>C</pc:option>
                      </pc:select>
                    <%}%>
                  </td>--%>
                  <td noWrap class="tdTitle">客户电话</td>
                   <td noWrap class="td"><input  type="text" class=edline name="tel" value='<%=corpRow.get("tel")%>' style="width:120"  onKeyDown="return getNextElement();" readonly></td>
                  </tr>
                    <tr>
                     <td noWrap class="tdTitle">客户地址</td>
                    <td noWrap class="td" colspan="3"><input  type="text" class=edline name="addr" value='<%=corpRow.get("addr")%>' style="width:300"  onKeyDown="return getNextElement();" readonly></td>
                   <td noWrap class="tdTitle">客户传真</td>
                   <td noWrap class="td"><input  type="text" class=edline name="cz" value='<%=corpRow.get("cz")%>' style="width:120"  onKeyDown="return getNextElement();" readonly></td>
                    <td noWrap class="tdTitle">客户Email</td>
                    <td noWrap class="td"><input  type="text" class=edline name="email" value='<%=corpRow.get("email")%>' style="width:120"  onKeyDown="return getNextElement();" readonly></td>
                    </tr>
                    <tr>
                    <td noWrap class="tdTitle">国家地区</td>
                    <td noWrap class="td"><input  type="text" class=edline name="cdm" value='<%=countryBean.getLookupName(corpRow.get("cdm"))%>' style="width:120"  onKeyDown="return getNextElement();" readonly></td>
                   <td noWrap class="tdTitle">价格条款</td>
                    <td noWrap class="td">
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+masterRow.get("jgtk")+"' style='width:110' class='edline' readonly>");
                    else{%>
                    <pc:select name="jgtk"  style="width:110" onSelect="">
                    <pc:option value='FOB'>FOB</pc:option>
                    <pc:option value='CFR'>CFR</pc:option>
                    <pc:option value='CIF'>CIF</pc:option>
                    <pc:option value='FCA'>FCA</pc:option>
                    <pc:option value='CPT'>CPT</pc:option>
                    <pc:option value='CIP'>CIP</pc:option>
                    </pc:select>
                    <%}%>
                    </TD>
                    <td noWrap class="tdTitle">付款方式</td>
                    <td noWrap class="td">
                     <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+payBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:110' class='edline' readonly>");
                    else{%>
                    <pc:select name="jsfsid" addNull="1" style="width:110" onSelect="">
                    <%=payBean.getList(masterRow.get("jsfsid"))%> </pc:select>
                    <%}%>
                    </td>
                    <td noWrap class="tdTitle">外币类别</td>
                    <td noWrap class="td">
                     <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+wbBean.getLookupName(masterRow.get("wbid"))+"' style='width:110' class='edline' readonly>");
                    else{%>
                       <pc:select name="wbid" addNull="1" style="width:110" onSelect="">
                    <%=wbBean.getList(masterRow.get("wbid"))%> </pc:select>
                       <%}%>
                    </td>
                    </tr>
                    <tr>
                    <td noWrap class="tdTitle">总金额</td>
                    <td noWrap class="td"><input type="text" name="zje" value='<%=masterRow.get("zje")%>' maxlength='12' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                   <td noWrap class="tdTitle">装运港口</td>
                    <td noWrap class="td"><input type="text" name="shprt" value='<%=masterRow.get("shprt")%>' maxlength='12' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"<%=masterReadonly%>></td>
                   <td noWrap class="tdTitle">目的港口</td>
                   <td noWrap class="td"><input type="text" name="aidprt" value='<%=masterRow.get("aidprt")%>' maxlength='12' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"<%=masterReadonly%>></td>
                    <td noWrap class="tdTitle">佣金率</td>
                    <td noWrap class="td"><input type="text" name="brkrg" value='<%=masterRow.get("brkrg")%>' maxlength='12' style="width:105" <%=edClass%> onKeyDown="return getNextElement();"<%=masterReadonly%>>%</td>
                    </tr>
                    <tr>
                    <td noWrap class="tdTitle">溢短装</td>
                    <td noWrap class="td"><input type="text" name="lose" value='<%=masterRow.get("lose")%>' maxlength='12' style="width:105" <%=edClass%> onKeyDown="return getNextElement();"<%=masterReadonly%>>%</td>
                      <td noWrap class="tdTitle">装运期限</td>
                    <td noWrap class="td"><input type="text" name="shptrm" value='<%=masterRow.get("shptrm")%>' maxlength='12' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"<%=masterReadonly%>></td>
                 <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%String onChange = "if(form1.deptid.value!='"+masterRow.get("deptid")+"')sumitForm("+InbuyOrderBean.DEPT_CHANGE+")";%>
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:100' class='edline' readonly>");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110" onSelect="<%=onChange%>">
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="personid" addNull="1" style="width:110">
                      <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
               </tr>

                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
               <tr>
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
                //int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                           <%if(!isEnd && isHasDeptLimit){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>,-1)" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%>
                        </td>
                        <%--td height='20' nowrap><%=detailProducer.getFieldInfo("cgsqdhwid").getFieldname()%></td--%>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名规格</td>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=isHsbj ? "换算数量" : "数量"%></td>
                        <td height='20' nowrap><%=isHsbj ? "换算单位" : "单位"%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("dj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("je").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sjjhl").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sjrkl").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jhrq").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("gyszyh").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                     // importApplyBean.regData(list,"cgsqdhwid");
                      propertyBean.regData(list, "dmsxid");
                      BigDecimal t_sl = new BigDecimal(0), t_je = new BigDecimal(0), t_ybje = new BigDecimal(0),t_sjjhl = new BigDecimal(0),t_sjrkl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(InbuyOrderBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String je = detail.get("je");
                        if(InbuyOrderBean.isDouble(je))
                          t_je = t_je.add(new BigDecimal(je));
                        String ybje = detail.get("ybje");
                        if(InbuyOrderBean.isDouble(ybje))
                          t_ybje = t_ybje.add(new BigDecimal(ybje));
                        String sjjhl = detail.get("sjjhl");
                        if(InbuyOrderBean.isDouble(sjjhl))
                          t_sjjhl = t_sjjhl.add(new BigDecimal(sjjhl));
                        String sjrkl = detail.get("sjrkl");
                        if(InbuyOrderBean.isDouble(sjrkl))
                          t_sjrkl = t_sjrkl.add(new BigDecimal(sjrkl));
                        String cgsqdhwid=detail.get("cgsqdhwid");
                        if(zt.equals("0"))
                          isCanRework = engine.erp.baseinfo.BasePublicClass.isRevamp(list, "sjjhl", i);//合同状态在未审状态时，判断该条纪录是否能被修改
                        String detailReadonly = isCanRework ? readonly : "readonly";
                        boolean isimport = !cgsqdhwid.equals("");//引入采购申请单，从表产品编码当前行不能修改
                        boolean isline = isimport || !isCanRework || !isHasDeptLimit;
                        String Class = isline ? "class=ednone" : detailClass;//从表Class模式
                        RowMap  importApplyRow= importApplyBean.getLookupRow(detail.get("cgsqdhwid"));
                        RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                        String product = prodRow.get("product");
                        detail.put("product", product);
                        %>
                        <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                         <%if(!isEnd && !isimport && isCanRework && isHasDeptLimit){if(!isHsbj){%>
                          <img style='cursor:hand' title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw',form1.cpid_<%=i%>.value)">
                          <%}else{%>
                          <img style='cursor:hand'  title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=hsdw_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=hsdw',form1.cpid_<%=i%>.value)">
                          <%}
                          }if(!isEnd && isCanRework && isHasDeptLimit){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <%--td class="td" nowrap><%=importApplyRow.get("sqbh")%></td--%>
                        <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <%if(!isHsbj){%><input type="text" <%=Class%> onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)"<%=isimport ? "readonly" : detailReadonly%>>
                        <%}else{%>
                        <input type="text" <%=Class%> onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productHsbjSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>>
                        <%}%>
                        </td>
                         <td class="td" nowrap><%if(isHsbj){%>
                        <input type="text" <%=Class%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=product%>' onchange="productNameHsbjSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>></td>
                        <%}else{%>
                        <input type="text" <%=Class%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=product%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>><%}%></td>
                        <td class="td" nowrap>
                        <input <%=Class%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=isimport ? "readonly" : detailReadonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd && !isimport && isCanRework){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=prodRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value)">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="cg_onchange(<%=i%>, false)"<%=readonly%>></td>
                        <td class="td" nowrap><%if(!isHsbj){%><input type="text" class=ednone style="width:60" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly><%}else{%>
                        <input type="text" class=ednone style="width:60" onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly><%}%></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' maxlength='<%=list.getColumn("dj").getPrecision()%>' onchange="cg_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="je_<%=i%>" name="je_<%=i%>" value='<%=detail.get("je")%>' maxlength='<%=list.getColumn("je").getPrecision()%>' onchange="je_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap align="right"><%=detail.get("sjjhl")%></td>
                          <td class="td" nowrap align="right"><%=detail.get("sjrkl")%></td>
                        <td class="td" nowrap><input type="text" <%=detailClass%> style="width:65" onKeyDown="return getNextElement();" name="jhrq_<%=i%>" id="jhrq_<%=i%>"value='<%=detail.get("jhrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)"><%if(!isEnd && isCanRework){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.jhrq_<%=i%>);"></a><%}%></td>
                        <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="gyszyh_<%=i%>" id="gyszyh_<%=i%>"value='<%=detail.get("gyszyh")%>' maxlength='<%=list.getColumn("gyszyh").getPrecision()%>' <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>'<%=readonly%>></td>
                        <%FieldInfo[] bakFields = detailProducer.getBakFieldCodes();
                        for(int k=0; k<bakFields.length; k++)
                        {
                          String fieldCode = bakFields[k].getFieldcode();
                          out.print("<td class='td' nowrap>");
                          out.print(detailProducer.getFieldInput(bakFields[k], detail.get(fieldCode), fieldCode+"_"+k, "style='width:65'", isEnd, true));
                          out.println("</td>");
                        }
                        %>
                      </tr>
                      <%
                        list.next();
                      }
                      for(; i < 4; i++){
                  %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_je" name="t_je" type="text" class="ednone_r" style="width:100%" value='<%=t_je%>' readonly></td>
                        <td align="right" class="td"><input id="t_sjjhl" name="t_sjjhl" type="text" class="ednone_r" style="width:100%" value='<%=t_sjjhl%>' readonly></td>
                        <td align="right" class="td"><input id="t_sjrkl" name="t_sjrkl" type="text" class="ednone_r" style="width:100%" value='<%=t_sjrkl%>' readonly></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
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
            <td class="td"><b>登记日期:</b><%=masterRow.get("czrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("czy")%></td>
          </tr>
          <tr>
              <td colspan="3" noWrap class="tableTitle">
           <%--    <%if(!isEnd ){%><input type="hidden" name="importapply" value="" onchange="sumitForm(<%=InbuyOrderBean.DETAIL_APPLY_ADD%>)">
             <input name="btnback" class="button" type="button" value="引入申请单(W)" style="width:100" onClick="importApply();" border="0">
                <pc:shortcut key="w" script="importApply();"/>
             <%}%>
              <%if(!isEnd){%><input type="hidden" name="select" value="" onchange="sumitForm(<%=InbuyOrderBean.DETAIL_PRICE_ADD%>)">
             <input name="btnback" class="button" type="button" value="供应商报价(B)" style="width:100" onClick="corpPrice();" border="0">
                <pc:shortcut key="b" script="corpPrice();"/>
             <%}--%>
              <%if(!isEnd){%><input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" style="width:90" value="保存添加(N)">
                <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" style="width:90" value="保存返回(S">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
              <%if(isCanDelete && isCanAmend && !InbuyOrderBean.isReport){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
                <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <%if(!InbuyOrderBean.isApprove && !InbuyOrderBean.isReport){%><input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
              <pc:shortcut key="c" script='backList();'/><%}%>
              <%if(!InbuyOrderBean.masterIsAdd()){%><input type="button" class="button" value=" 打印 " onclick="location.href='../pub/pdfprint.jsp?code=buy_order_edit_bill&operate=<%=InbuyOrderBean.PRINT_BILL%>&a$htid=<%=masterRow.get("htid")%>&src=../buy/buy_order_edit.jsp'"><%}%>
              <%if(!InbuyOrderBean.masterIsAdd()){%><input type="button" class="button" style="width:100" value="催货涵打印 " onclick="location.href='../pub/pdfprint.jsp?code=provide_corp_letter&operate=<%=InbuyOrderBean.PRINT_BILL%>&a$htid=<%=masterRow.get("htid")%>&src=../buy/buy_order_edit.jsp'"><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%--=InbuyOrderBean.adjustInputSize(new String[]{"cpbm", "product","sl", "dj", "je","ybje","sxz","gyszyh","bz"}, "form1", detailRows.length)--%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function delMaster(){
  if(confirm('是否删除该记录？'))
    sumitForm(<%= Operate.DEL%>,-1);
  }
    function importApply(){
      if(form1.dwtxid.value==''){alert('请选择往来单位'); return;}
      ApplyMultiSelect('form1','srcVar=importapply&dwtxid='+form1.dwtxid.value);
  }
  function corpPrice(){
      if(form1.dwtxid.value==''){alert('请选择往来单位'); return;}
    goodsMultiSelect('form1','srcVar=select&dwtxid='+form1.dwtxid.value);
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
      for(k=0; k<<%=detailRows.length%>; k++)
        {
        cg_onchange(k);
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
    function cg_onchange(i, isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      var djObj = document.all['dj_'+i];
      var jeObj = document.all['je_'+i];
      var hlObj = form1.hl;
     // var ybjeObj = document.all['ybje_'+i];
      if(slObj.value=="")
        return;
      if(isNaN(slObj.value)){
        alert("输入的数量非法");
        slObj.focus();
        return;
      }
      cal_tot('sl');
      if(djObj.value=="")
        return;
      if(isNaN(djObj.value)){
        alert("输入的单价非法");
        djObj.focus();
        return;
      }
      if(slObj.value!="" && !isNaN(slObj.value))
        jeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value));
      else
        jeObj.value='';
      //if(slObj.value!="" && !isNaN(slObj.value) && hlObj.value!=0 && !isNaN(hlObj.value))
     //   ybjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
     // else
     //   ybjeObj.value='';
     // if(hlObj.value=="" || hlObj.value==0)
     //   ybjeObj.value ="";
      cal_tot('je');
      document.all['zje'].value=document.all['t_je'].value;
    // cal_tot('ybje');
    }
    function je_onchange(i, isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      var djObj = document.all['dj_'+i];
      var jeObj = document.all['je_'+i];
     // var hlObj = form1.hl;
     // var ybjeObj = document.all['ybje_'+i];
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
      if(obj.value<=0)
      {
        alert(showText2);
        obj.focus();
        return;
      }

      if(!isBigUnit && slObj.value!="" && !isNaN(slObj.value && slObj.value!="0")){
        djObj.value = formatPrice(parseFloat(jeObj.value) / parseFloat(slObj.value));
      }
    //  if(hlObj.value!="" && !isBigUnit && hlObj!="0")
     //   ybjeObj.value = formatSum(parseFloat(jeObj.value)/parseFloat(hlObj.value));
    //  if(isBigUnit && hlObj.value!="" && !isNaN(hlObj.value) && hlObj!="0")
     //   jeObj.value = formatSum(parseFloat(ybjeObj.value) * parseFloat(hlObj.value));
      if(isBigUnit && hlObj.value!="" && !isNaN(hlObj.value) && hlObj!="0" && slObj.value!="" && !isNaN(slObj.value) && slObj.value!="0")
        djObj.value = formatPrice(parseFloat(ybjeObj.value)* parseFloat(hlObj.value) / parseFloat(slObj.value));
      cal_tot('je');
     // cal_tot('ybje');
    }
    function cal_tot(type)
    {
      var tmpObj;
      var tot=0;
      for(i=0; i<<%=detailRows.length%>; i++)
      {
        if(type == 'sl')
          tmpObj = document.all['sl_'+i];
        else if(type == 'je')
          tmpObj = document.all['je_'+i];
       // else if(type == 'ybje')
       //   tmpObj = document.all['ybje_'+i];
        else
          return;
        if(tmpObj.value!="" && !isNaN(tmpObj.value))
          tot += parseFloat(tmpObj.value);
      }
      if(type == 'sl')
        document.all['t_sl'].value = formatQty(tot);
      else if(type == 'je')
        document.all['t_je'].value = formatSum(tot);
     // else if(type == 'ybje')
     //   document.all['t_ybje'].value = formatSum(tot);
    }
    function ApplyMultiSelect(frmName, srcVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "../buy/import_apply_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function goodsMultiSelect(frmName, srcVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "../buy/buy_goods_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
</script>
<%if(InbuyOrderBean.isApprove && !InbuyOrderBean.isReport){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
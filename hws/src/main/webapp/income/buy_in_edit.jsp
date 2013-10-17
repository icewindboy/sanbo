<%--外贸采购进货单编辑页面从表--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
  String op_instore ="op_instore";
%><%
  engine.erp.income.InBuy_In inbuyInBean = engine.erp.income.InBuy_In.getInstance(request);
  String pageCode = "income_in";
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
  location.href='buy_in.jsp';
}
function corpCodeSelect(obj)
{
  ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=inbuyInBean.ONCHANGE%>)');
}
function corpNameSelect(obj)
{
  ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=inbuyInBean.ONCHANGE%>)');
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
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value);
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value);
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
<%String retu = inbuyInBean.doService(request, response);
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
  EngineDataSet ds = inbuyInBean.getMaterTable();
  EngineDataSet list = inbuyInBean.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  HtmlTableProducer masterProducer = inbuyInBean.masterProducer;
  HtmlTableProducer detailProducer = inbuyInBean.detailProducer;
  RowMap masterRow = inbuyInBean.getMasterRowinfo();
  RowMap[] detailRows= inbuyInBean.getDetailRowinfos();
  String zt=masterRow.get("zt");
  String deptid = masterRow.get("deptid");
  String czyid = masterRow.get("czyid");
  boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, czyid);//判断登陆员工是否有操作改制单人单据的权限
  if(inbuyInBean.isApprove)
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
  boolean isEnd = inbuyInBean.isRep || inbuyInBean.isApprove || (!inbuyInBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !inbuyInBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(inbuyInBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = (isEnd || !isHasDeptLimit) ? "class=edline" : "class=edbox";
  String detailClass = (isEnd || !isHasDeptLimit) ? "class=ednone" : "class=edFocused";
  String detailClass_r = (isEnd || !isHasDeptLimit) ? "class=ednone_r" : "class=edFocused_r";
  String readonly = (isEnd || !isHasDeptLimit) ? " readonly" : "";
  String masterReadonly = isCanAmend ? readonly : "readonly";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("2") ? "已入库" : (zt.equals("9") ? "审批中" : "未审核"));
  String head = djlx.equals("2") ? "进口货物进货单" : "进口货物购退货单";
  boolean isAdd = inbuyInBean.isDetailAdd;
  boolean  isRead = inbuyInBean.isRep || inbuyInBean.isApprove || (!inbuyInBean.masterIsAdd() && (zt.equals("9") || zt.equals("2")));//表示在审批中或已入库不能修改
  isRead = isRead || !(inbuyInBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  String slReadonly = (isRead || !isHasDeptLimit) ? "readonly" : "";//数量和单价在审批的时候可以修改,汇率在审批通过也可以修改
  String slClass_r = (isRead || !isHasDeptLimit) ? "class=ednone_r" : "class=edFocused_r";
  String dmsxClass = (isRead || !isHasDeptLimit) ? "class=ednone" : "class=edFocused";
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
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
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
                    <%String store_Change = "if(form1.storeid.value!='"+masterRow.get("storeid")+"')sumitForm("+inbuyInBean.STORE_CHANGE+")";%>
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:90' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="1" style="width:90" onSelect="<%=store_Change%>"> <%=storeBean.getList(masterRow.get("storeid"))%>
                    </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%String onChange = "if(form1.deptid.value!='"+masterRow.get("deptid")+"')sumitForm("+inbuyInBean.DEPT_CHANGE+")";%>
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
                  <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=inbuyInBean.ONCHANGE%>)');"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';sumitForm(<%=inbuyInBean.ONCHANGE%>)">
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("wbid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+wbBean.getLookupName(masterRow.get("wbid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="wbid" style="width:90" value='<%=wbBean.getLookupName(masterRow.get("wbid"))%>'>
                      <%=wbBean.getList(masterRow.get("wbid"))%>
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
               <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwt_dwtxid").getFieldname()%></td>
                  <%RowMap corpCydwRow = corpBean.getLookupRow(masterRow.get("dwt_dwtxid"));%>
                  <td  noWrap class="td" colspan="3"><input type="text" name="dwt_dwdm" value='<%=corpCydwRow.get("dwdm")%>' style="width:60" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpCydwCodeSelect(this);" <%=masterReadonly%>>
                                    <input type="hidden" name="dwt_dwtxid" value='<%=masterRow.get("dwt_dwtxid")%>'>
                    <input type="text" name="dwt_dwmc" value='<%=corpBean.getLookupName(masterRow.get("dwt_dwtxid"))%>' style="width:180" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpCydwNameSelect(this);" <%=masterReadonly%>>
                    <%if(!isEnd && isCanAmend && isHasDeptLimit){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="TransportSingleSelect('form1','srcVar=dwt_dwtxid&srcVar=dwt_dwmc&srcVar=dwt_dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwt_dwtxid.value);"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwt_dwtxid.value='';dwt_dwmc.value='';dwt_dwdm.value='';">
                    <%}%>
                  </td>
              <%--      <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("fplx").getFieldname()%></td>
                  <td noWrap class="td">
                    <%String fplx=masterRow.get("fplx");%>
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+masterRow.get("fplx")+"' style='width:50' class='edline' readonly>");
                    else {%>
                    <pc:select name="fplx" style="width:80" value='<%=fplx%>'>
                      <pc:option value=''></pc:option>
                      <pc:option value='增值税'>增值税</pc:option>
                      <pc:option value='企业普票'>企业普票</pc:option>
                    <pc:option value='一般收据 '>一般收据 </pc:option>
                      </pc:select>
                    <%}%>
                  </td>--%>

                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsfsid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+balanceBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:90' class='edline' readonly>");
                    else {%>
                    <pc:select name="jsfsid" addNull="1" style="width:90"> <%=balanceBean.getList(masterRow.get("jsfsid"))%>
                    </pc:select>
                    <%}%>
                  </td>
                  <td></td><td></td>
                  <td></td><td></td>

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
                          <td nowrap width=15>
                        <input class="edFocused_r"  name="tCopyNumber" value="<%=request.getParameter("tCopyNumber")==null?"1":request.getParameter("tCopyNumber")%>"  size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                        </td>
                          <td height='20' align="center" nowrap>
                            <%if(!isEnd && isHasDeptLimit){%>
                            <input name="image" class="img" type="image" title="新增(A)" onClick="Add();" src="../images/add_big.gif" border="0">
                            <pc:shortcut key="a" script='Add();'/><%}%>
                          </td>
                          <td height='20' nowrap><%=detailProducer.getFieldInfo("hthwid").getFieldname()%></td>
                          <td height='20' nowrap>产品编码</td>
                          <td height='20' nowrap>品名规格</td>

                          <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                          <td height='20' nowrap>数量</td>
                          <td height='20' nowrap>单位</td>
                          <td nowrap>换算数量</td>
                          <td height='20' nowrap>换算单位</td>
                          <td nowrap><%=detailProducer.getFieldInfo("dj").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("je").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("sjrkl").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("jhrq").getFieldname()%></td>
                         <%--<td nowrap><%=detailProducer.getFieldInfo("dhqk").getFieldname()%></td>--%>
                           <td nowrap><%=detailProducer.getFieldInfo("gyszyh").getFieldname()%></td>
                          <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                          <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                        </tr>
                        <%prodBean.regData(list,"cpid");
                          buyOrderBean.regData(list,"hthwid");
                          propertyBean.regData(list,"dmsxid");
                          BigDecimal t_sl = new BigDecimal(0), t_je = new BigDecimal(0), t_sjrkl = new BigDecimal(0), t_hssl = new BigDecimal(0);
                          int i=0;
                          RowMap detail = null;
                          for(; i<detailRows.length; i++)   {
                            detail = detailRows[i];
                            BigDecimal b_djlx = new BigDecimal(1);
                            String s_sl = detail.get("sl");
                            BigDecimal sl = s_sl.length() > 0 ? b_djlx.multiply(new BigDecimal(s_sl)) : new BigDecimal(0);
                            String s_hssl = detail.get("hssl");
                            BigDecimal hssl = s_hssl.length() > 0 ? b_djlx.multiply(new BigDecimal(s_hssl)) : new BigDecimal(0);
                            String s_je = detail.get("je");
                            BigDecimal je = s_je.length() > 0 ? b_djlx.multiply(new BigDecimal(s_je)) : new BigDecimal(0);
                            String sjrkl = detail.get("sjrkl");
                            if(inbuyInBean.isDouble(sjrkl))
                              t_sjrkl = t_sjrkl.add(new BigDecimal(sjrkl));


                            t_sl = t_sl.add(sl);
                           // t_hssl = t_hssl.add(hssl);
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
                           <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                           <%if(!isEnd && !isimport && isCanRework && isHasDeptLimit){%>
                           <img style='cursor:hand' title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=isprops_<%=i%>&storeid='+form1.storeid.value,
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops')">

                           <%}if(!isEnd && isCanRework && isHasDeptLimit){%>
                           <input name="image" class="img" type="image" title="复制当前行" onClick="if(form1.cpid_<%=i%>.value==''){alert('请输入产品');return;}sumitForm(<%=inbuyInBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
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
                          <input type="text" <%=Class%> onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>>
                          <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'>
                          <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                          <input type='hidden' id='isprops_<%=i%>' name='isprops_<%=i%>' value='<%=prodRow.get("isprops")%>'></td>
                          <td class="td" nowrap>
                          <input type="text" <%=Class%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>></td>
                          <td class="td" nowrap>
                          <input <%=detailClass_r%>  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>>
                          <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                          <%if(!isRead){
                           String aaa=prodRow.get("isprops");%>
                          <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=prodRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value)">
                          <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                          <%}%>
                          </td>
                          <td class="td" nowrap><input type="text" <%=isHsbj ? "class=ednone_r" : detailClass_r%> onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=isHsbj ? "readonly" : readonly%>></td>
                          <td class="td" nowrap><input type="text" class=ednone style="width:100%" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                          <td class="td" nowrap><input type="text" <%=isHsbj ? slClass_r : "class=ednone_r"%> onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=hssl%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=isHsbj ? slReadonly : "readonly"%>></td>
                          <td class="td" nowrap><input type="text" class=ednone style="width:100%" onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                          <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' onchange="dj_onchange(<%=i%>, false)" <%=readonly%>></td>
                          <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="je_<%=i%>" name="je_<%=i%>" value='<%=detail.get("je")%>' maxlength='<%=list.getColumn("je").getPrecision()%>' onchange="je_onchange(<%=i%>, false)" <%=readonly%>></td>
                          <td class="td" nowrap><input type="text" class=ednone_r onKeyDown="return getNextElement();" id="sjrkl_<%=i%>" name="sjrkl_<%=i%>" value='<%=detail.get("sjrkl")%>' maxlength='<%=list.getColumn("sjrkl").getPrecision()%>' readonly></td>
                       <%--  <td class="td" nowrap><input type="text" <%=detailClass%> style="width:65" onKeyDown="return getNextElement();" name="jhrq_<%=i%>" id="jhrq_<%=i%>"value='<%=detail.get("jhrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)"></td>--%>
                         <td class="td" nowrap><input type="text" <%=detailClass%> style="width:65" onKeyDown="return getNextElement();" name="jhrq_<%=i%>" id="jhrq_<%=i%>"value='<%=detail.get("jhrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)"><%if(!isEnd && isCanRework){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.jhrq_<%=i%>);"></a><%}%></td>
                         <%--   <td class="td" nowrap align="right"><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="dhqk_<%=i%>" id="dhqk_<%=i%>" value='<%=detail.get("dhqk")%>' maxlength='<%=list.getColumn("dhqk").getPrecision()%>'<%=readonly%>></td>--%>
                         <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="gyszyh_<%=i%>" id="gyszyh_<%=i%>"value='<%=detail.get("gyszyh")%>' maxlength='<%=list.getColumn("gyszyh").getPrecision()%>' <%=readonly%>></td>


                          <td class="td" nowrap align="right"><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>'<%=readonly%>></td>
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
                          <td class="td">&nbsp;</td>

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

                          <td align="right" class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_hssl.toString(),loginBean.getQtyFormat()) %>' readonly></td>
                          <td class="td">&nbsp;</td>

                          <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_sl.toString(),loginBean.getQtyFormat())%>' readonly></td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td align="right" class="td"><input id="t_je" name="t_je" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_je.toString(),loginBean.getSumFormat())%>' readonly></td>
                          <td align="right" class="td"><input id="t_sjrkl" name="t_sjrkl" type="text" class="ednone_r" style="width:100%" value='<%=t_sjrkl%>' readonly></td>
                          <td class="td"></td>
                          <td align="right" class="td">&nbsp;</td>
                          <td class="td"></td>
                          <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
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
              <input type="hidden" name="singleOrder" value="引入订单" >
              <input name="btnback" class="button" type="button" value="引入订单(W)" style="width:100" onClick="if(form1.storeid.value==''){alert('请选择仓库');return;}OrderSingleSelect('form1','srcVar=singleOrder','fieldVar=htid',form1.dwtxid.value,form1.storeid.value,'sumitForm(<%=inbuyInBean.SINGLE_SELECT_ORDER%>)')">
                <pc:shortcut key="w" script="importOrder();"/>
              <input type="hidden" name="importOrder" value="引订单货物" onchange="sumitForm(<%=inbuyInBean.DETAIL_ORDER_ADD%>)">
              <input name="btnback" class="button" type="button" value="引订单货物(E)" style="width:100" onClick="if(form1.dwtxid.value==''){alert('请选择供货单位');return;}if(form1.storeid.value==''){alert('请选择仓库');return;}OrderMultiSelect('form1','srcVar=importOrder&dwtxid='+form1.dwtxid.value+'&storeid='+form1.storeid.value)" border="0">
                <pc:shortcut key="e" script="importOrderGoods();"/>
              <input name="button2" type="button" class="button" title="删除数量为零行(ALT+X)" value="删除数量为零行(X)" style='width:120' onClick="sumitForm(<%=inbuyInBean.DELETE_BLANK%>);">
                <pc:shortcut key="x" script='<%="sumitForm("+inbuyInBean.DELETE_BLANK+")"%>'/>
              <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
                <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <%--   <%}if(!isRead || !isHasDeptLimit){%>--%>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
              <%if(isCanDelete && isCanAmend && !inbuyInBean.isRep){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
             <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <%if(!inbuyInBean.isApprove && !inbuyInBean.isRep){%><input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
              	  <pc:shortcut key="c" script='backList();'/><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=inbuyInBean.adjustInputSize(new String[]{"cpbm","product", "sl", "dj", "je","sxz","sjrkl","bz"}, "form1", detailRows.length)%>
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
     OrderSingleSelect('form1','srcVar=singleOrder','fieldVar=htid',form1.dwtxid.value,form1.storeid.value,'sumitForm(<%=inbuyInBean.SINGLE_SELECT_ORDER%>)')
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
       //var ybjeObj = document.all['ybje_'+i];
       var djlx = <%=djlx%>;
       if('<%=bjfs%>'=="0")//报价方式为按计量单位报价时判断
       {
         //alert("sadf");
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

        if(hsblObj.value!="" && !isNaN(hsblObj.value) && hsblObj.value!="0")
          hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
        else
          hsslObj.value="";
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
          jeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value));
       //  if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
         //   ybjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
        //  else
         //   ybjeObj.value ='';
        }
        else{
          jeObj.value='';
         // ybjeObj.value ='';
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
          slObj.value= formatQty(parseFloat(hsslObj.value) * parseFloat(hsblObj.value));
        else
          slObj.value="";
        cal_tot('sl');
        cal_tot('hssl');

        if(djObj.value=="")
          return;
        if(hsslObj.value!="" && !isNaN(hsslObj.value)){
          jeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value));
          //if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
          //  ybjeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
          //else
          //  ybjeObj.value ='';
        }
          else{
          jeObj.value='';
         // ybjeObj.value ='';
        }
      }

      cal_tot('je');
      //cal_tot('ybje');
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
      //var ybjeObj = document.all['ybje_'+i];
      if(djObj.value==""){
        jeObj.value='';
       // ybjeObj.value='';
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
          // if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
            // ybjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
           //else
             //ybjeObj.value ='';
        }
        else{
          jeObj.value='';
         // ybjeObj.value ='';
        }
      }
      else
      {
        if(hsslObj.value!="" && !isNaN(hsslObj.value)){
          jeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value));
          //if(hlObj.value!='' && !isNaN(hlObj.value) && hlObj.value!=0)
         //   ybjeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
         // else
         //    ybjeObj.value ='';
        }
        else{
          jeObj.value='';
         // ybjeObj.value ='';
        }
      }
      cal_tot('je');
      //cal_tot('ybje');
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
     //var ybjeObj = document.all['ybje_'+i];
     var obj = isBigUnit ? ybjeObj : jeObj;
     var showText =  "输入的金额非法";
     var showText2 = "输入的金额小于零";
     var changeObj = jeObj;
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
      // if(hlObj.value!="" && !isNaN(hlObj.value) && hlObj!="0")
      //   ybjeObj.value = formatSum(parseFloat(jeObj.value)/parseFloat(hlObj.value));
     //  else
     //    ybjeObj.value='';
     }
   /*  else{
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
     }*/
     cal_tot('je');
     //cal_tot('ybje');
    }
    function cal_tot(type)
    {
      var tmpObj;
      var tot=0;
      for(var t=0; t<<%=detailRows.length%>; t++)
      {
        if(type == 'sl')
          tmpObj = document.all['sl_'+t];
        else if(type == 'je')
          tmpObj = document.all['je_'+t];
        else if(type == 'sjrkl')
          tmpObj = document.all['sjrkl_'+t];
        else
          return;
        if(tmpObj.value!="" && !isNaN(tmpObj.value))
          tot =tot+parseFloat(tmpObj.value);
      }
      if(type == 'sl')
        document.all['t_sl'].value = formatQty(tot);
      else if(type == 'je')
        document.all['t_je'].value = formatSum(tot);
      else if(type == 'sjrkl')
        document.all['t_sjrkl'].value = formatSum(tot);
    }
    function OrderMultiSelect(frmName, srcVar, methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
      var winName= "GoodsProdSelector";
      paraStr = "../income/import_incomeorder_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
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
      paraStr = "../income/inomeorder_single_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&dwtxid="+curID+"&storeid="+storeid+"&djlx=2";
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
<%if(inbuyInBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
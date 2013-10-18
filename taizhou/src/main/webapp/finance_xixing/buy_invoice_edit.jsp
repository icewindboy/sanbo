<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.finance.xixing.B_BuyInvoice b_BuyInvoiceBean = engine.erp.finance.xixing.B_BuyInvoice.getInstance(request);
  String pageCode = "buy_invoice";
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
  location.href='buy_invoice.jsp';
}
function customerCodeSelect(obj)
{
    CustomerCodeChange('1',document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=khh&srcVar=zh&srcVar=sh','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=khh&fieldVar=zh&fieldVar=nsrdjh',obj.value,'sumitForm(<%=b_BuyInvoiceBean.DWTXID_CHANGE%>,-1)');
}
function customerNameChange(obj)
{
    CustomerNameChange('1',document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=khh&srcVar=zh&srcVar=sh','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=khh&fieldVar=zh&fieldVar=nsrdjh',obj.value,'sumitForm(<%=b_BuyInvoiceBean.DWTXID_CHANGE%>,-1)');
}
function deptchange()
{
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
function fplbchange()
{
  sumitForm(<%=b_BuyInvoiceBean.FPLB_CHANGE%>,-1);
}
</script>

<%
  String retu = b_BuyInvoiceBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp jhdBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_ORDER_STOCK);
  engine.project.LookUp buyPriceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//b_BuyInvoiceBean.getBuyPriceBean(request);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp invoiceTypeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_INVOICE_TYPE);//发票种类

  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_BuyInvoiceBean.getMaterTable();
  EngineDataSet list = b_BuyInvoiceBean.getDetailTable();//引用过来的数据集
  HtmlTableProducer masterProducer = b_BuyInvoiceBean.masterProducer;
  HtmlTableProducer detailProducer = b_BuyInvoiceBean.detailProducer;
  RowMap masterRow = b_BuyInvoiceBean.getMasterRowinfo();//主表一行
  RowMap[] detailRows= b_BuyInvoiceBean.getDetailRowinfos();//从表多行
  String zt=ds.getValue("zt");
  //&#$
  if(b_BuyInvoiceBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  boolean isEnd =  b_BuyInvoiceBean.isReport||b_BuyInvoiceBean.isApprove || (!b_BuyInvoiceBean.masterIsAdd() && !zt.equals("0"));

  boolean isCanDelete = !isEnd && !b_BuyInvoiceBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);
  isEnd = isEnd || !(b_BuyInvoiceBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=edline" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核") : ("未审核");
  boolean count=list.getRowCount()==0?true:false;
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
  String prodreanonly = readonly;
  String prodClass = detailClass;
  String price_method = b_BuyInvoiceBean.getPriceMethod();//得到报价方式
  String hsslClass = price_method.equals("1")? "class=edFocused_r" : "class=ednone_r";//1=以换算单位报价,0=主单位报价
  String hsslReadonly = price_method.equals("1")? "" : "readonly";
  String slClass = price_method.equals("0")? "class=edFocused_r" : "class=ednone_r";//
  String slReadonly = price_method.equals("0")? "" : "readonly";
  hsslClass = isEnd ?"class=edline":hsslClass;
  slClass = isEnd ?"class=edline":slClass;
  hsslReadonly = isEnd ?"readonly":hsslReadonly;
  slReadonly = isEnd ?"readonly":slReadonly;

%>
<BODY oncontextmenu="window.event.returnValue=true"  onLoad="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="zt" value="<%=masterRow.get("zt")%>">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">采购发票(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0" width="100%">
                <%corpBean.regData(ds,"dwtxid");%>
                  <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("kprq").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="kprq" value='<%=masterRow.get("kprq")%>' maxlength='10' style="width:110" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();" >
                  <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.kprq);"></a>
                  <%}%></td>
                   <td  noWrap class="tdTitle">单据号</td>
                   <td  noWrap class="td"><input type="text" name="fphm" value='<%=masterRow.get("fphm")%>' maxlength='<%=ds.getColumn("fphm").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                   <td  noWrap class="tdTitle">发票号码</td>
                   <td  noWrap class="td"><input type="text" name="sjhm" value='<%=masterRow.get("sjhm")%>' maxlength='<%=ds.getColumn("sjhm").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                   <td  noWrap class="tdTitle">发票类别</td>
                   <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+invoiceTypeBean.getLookupName(masterRow.get("fplbid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="fplbid" onSelect="fplbchange();"   style="width:110">
                    <%=invoiceTypeBean.getList(masterRow.get("fplbid"))%>
                    </pc:select>
                    <%}%></td>
                 </tr>
                 <tr>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwtxid").getFieldname()%></td><%--供货单位--%>
                  <td  noWrap colspan='3' class="td">
                    <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text" <%=detailClass%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="customerCodeSelect(this)" <%=readonly%>>
                    <input type="text" <%=detailClass%> name="dwmc" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>'  onKeyDown="return getNextElement();"  style="width:200"  onchange="customerNameChange(this)" <%=readonly%>>
                    <%if(!isEnd){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=khh&srcVar=zh&srcVar=sh','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=khh&fieldVar=zh&fieldVar=nsrdjh',form1.dwtxid.value,'sumitForm(<%=b_BuyInvoiceBean.DWTXID_CHANGE%>,-1)');">
                    <%}%>
                  </td>
                 </td>

                 <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("khh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="khh" value='<%=masterRow.get("khh")%>' maxlength='<%=ds.getColumn("khh").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"<%=readonly%>></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("zh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="zh" value='<%=masterRow.get("zh")%>' maxlength='<%=ds.getColumn("zh").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"<%=readonly%>></td>
                 </tr>
                 <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dz").getFieldname()%></td>
                  <td noWrap class="td" colspan="3"><input type="text" align="left" <%=edClass%> name="dz" value='<%=masterRow.get("dz")%>' maxlength='<%=ds.getColumn("dz").getPrecision()%>' style="width:100%"  onKeyDown="return getNextElement();"<%=readonly%>></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sh" value='<%=masterRow.get("sh")%>' maxlength='<%=ds.getColumn("sh").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"<%=readonly%>></td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsfsid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+balanceModeBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="jsfsid"  style="width:110">
                    <%=balanceModeBean.getList(masterRow.get("jsfsid"))%>
                    </pc:select>
                    <%}%></td>
                 </tr>
                 <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%
                      String onChange ="if(form1.deptid.value!='"+masterRow.get("deptid")+"')sumitForm("+Operate.DEPT_CHANGE+")";
                    %>
                    <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110"  onSelect="deptchange();" >
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                   </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>
                  <td   noWrap class="td">
                    <%
                      if(!isEnd)
                        personBean.regConditionData(ds, "deptid");
                      if(isEnd) out.print("<input type='text' value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="personid" style="width:110">
                      <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
               </tr>
                 <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bz").getFieldname()%></td>
                  <td  class="td" colspan="5"><input type="text" align="left" <%=edClass%> name="bz" value='<%=masterRow.get("bz")%>' maxlength='<%=ds.getColumn("bz").getPrecision()%>' style="width:100%"  onKeyDown="return getNextElement();"<%=readonly%>></td>
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
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){%>
                          <%if(b_BuyInvoiceBean.canOperate){%><input name="image" class="img" type="image" title="新增" onClick="sumitForm(<%=b_BuyInvoiceBean.DETAIL_ADD_NULL%>)" src="../images/add_big.gif" border="0"><%}%>
                          <input class="edFocused_r"  name="tCopyNumber" value="1" title="拷贝(ALT+A)" size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                          <%}%>
                        </td>
                        <td height='20' nowrap>进货单编号</td>
                        <td height='20' nowrap>原辅料编号</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td height='20' nowrap>规格属性</td>
                        <%--<td nowrap>换算数量</td>
                        <td height='20' nowrap>换算单位</td>--%>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("wsdj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("je").getFieldname()%></td>
                        <td nowrap>税率(%)</td>
                        <td nowrap><%=detailProducer.getFieldInfo("hsdj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("se").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jshj").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%
                      propertyBean.regData(list,"dmsxID");
                      prodBean.regData(list,"cpid");
                      jhdBean.regData(list,"jhdhwid");
                      //buyPriceBean.regData(list,"jhdhwid");
                      BigDecimal t_sl = new BigDecimal(0),t_hssl = new BigDecimal(0), t_se = new BigDecimal(0), t_je = new BigDecimal(0),t_jshj = new BigDecimal(0),t_gs = new BigDecimal(0);
                      int i=0;
                      String[] widthName = new String[]{"sl", "hsdj", "wsdj","je","gsl","gs","zzsl","se"};
                      int[] widthMin = new int[]{60, 60, 70,60,60,60,60,60,60,150};
                      int[] widths = b_BuyInvoiceBean.getMaxStyleWidth(detailRows, widthName, widthMin);
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(b_BuyInvoiceBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String hssl = detail.get("hssl");
                        if(b_BuyInvoiceBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String je = detail.get("je");
                        if(b_BuyInvoiceBean.isDouble(je))
                          t_je = t_je.add(new BigDecimal(je));
                        //String jje = detail.get("jje");
                        String gs = detail.get("gs");
                        if(b_BuyInvoiceBean.isDouble(gs))
                          t_gs= t_gs.add(new BigDecimal(gs));
                        String se = detail.get("se");
                        if(b_BuyInvoiceBean.isDouble(se))
                          t_se= t_se.add(new BigDecimal(se));
                        String jshj = detail.get("jshj");
                        if(b_BuyInvoiceBean.isDouble(jshj))
                          t_jshj = t_jshj.add(new BigDecimal(jshj));
                        String cpid=detail.get("cpid");
                        RowMap prodRow = buyPriceBean.getLookupRow(detail.get("cpid"));
                        RowMap  jhdRow= jhdBean.getLookupRow(detail.get("jhdhwid"));
                        String isprops=prodRow.get("isprops");
                        String isdel=prodRow.get("isdelete");

                        if(!detail.get("jhdhwid").equals(""))
                          {
                          prodClass="class=ednone";
                          prodreanonly="readonly";
                          }else
                          {
                            prodClass = detailClass;
                            prodreanonly = readonly;
                          }
                     %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%>
                         <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                        <input type="HIDDEN"  id="cpid_<%=i%>"  name="cpid_<%=i%>"  value='<%=detail.get("cpid")%>'  >
                        <input type='HIDDEN'  id='hsbl_<%=i%>'    name='hsbl_<%=i%>'    value='<%=prodRow.get("hsbl")%>'>
                        <input type='HIDDEN' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                        <input type="HIDDEN"  id="dmsxid_<%=i%>"  name="dmsxid_<%=i%>"  value='<%=detail.get("dmsxid")%>'  >
                        <input type="HIDDEN" <%=hsslClass%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>,true)" <%=hsslReadonly%>>
                        <input type="HIDDEN" style="width:20" class='ednone_r'  onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>'  readonly>

                        </td>
                        <td class="td" nowrap align="center">
                          <%if(!isEnd){%>
                           <%if(detail.get("jhdhwid").equals("")){%><img style='cursor:hand' title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=hsdw_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsbl&fieldVar=hsdw')"><%}%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <input name="image" class="img" type="image" title="复制当前行" onClick="sumitForm(<%=b_BuyInvoiceBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <%}%></td>
                        <td class="td" nowrap><%=jhdRow.get("jhdbm")%></td>
                        <td class="td" nowrap><input type="text" <%=prodClass%>  onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=prodreanonly%>></td><!--产品编码(存货代码)--->
                        <td class="td" nowrap><input type="text" <%=prodClass%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>'  onchange="productNameSelect(this,<%=i%>)"   <%=prodreanonly%>></td><!--品名 规格(存货名称与规格)--->
                        <td class="td" nowrap>
                        <input type="text"  <%=detailClass_r%>  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="propertiesInput(this,form1.cpid_<%=i%>.value,<%=i%>)"  <%=isEnd?"readonly":" "%> >
                        <%if(!isEnd){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=isprops%>'=='0'){alert('该产品无规格属性!');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value)"><%}%>
                        <%if(!isEnd&&!isprops.equals("0")){%><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';"><%}%>
                        </td>
                        <%--<td class="td" nowrap align="right"> <input type="text" <%=hsslClass%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>,true)" <%=hsslReadonly%>></td>
                        <td class="td" nowrap><input type="text" style="width:20" class='ednone_r'  onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>'  readonly></td>--%>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>"     value='<%=detail.get("sl")%>'   maxlength='<%=list.getColumn("sl").getPrecision()%>'   onchange="sl_onchange(<%=i%>,false)" <%=readonly%>></td><!--数量-->
                        <td class="td" nowrap><input type="text" class=ednone style="width:100%" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td><!--计量单位--->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="wsdj_<%=i%>" name="wsdj_<%=i%>" value='<%=detail.get("wsdj")%>' maxlength='<%=list.getColumn("wsdj").getPrecision()%>' onchange="dj_onchange(<%=i%>, false)"></td><!--无税单价-->
                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'  align="right"     style="width:<%=widths[2]%>" onKeyDown="return getNextElement();" id="je_<%=i%>" name="je_<%=i%>"     value='<%=detail.get("je")%>'   maxlength='<%=list.getColumn("je").getPrecision()%>'   readonly></td>
                        </td>

                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:<%=widths[2]%>" onKeyDown="return getNextElement();" id="zzsl_<%=i%>" name="zzsl_<%=i%>" value='<%=detail.get("zzsl")%>' maxlength='<%=list.getColumn("zzsl").getPrecision()%>' onchange="zzsl_onchange(<%=i%>, false)" <%=readonly%>></td><!--税率-->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%> align="right"    style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="hsdj_<%=i%>" name="hsdj_<%=i%>" value='<%=detail.get("hsdj")%>' maxlength='<%=list.getColumn("hsdj").getPrecision()%>'  onchange="hsdj_onchange(<%=i%>, true)" <%=readonly%>></td><!--含税单价-->
                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'   align="right"     style="width:<%=widths[5]%>" onKeyDown="return getNextElement();" name="se_<%=i%>" id="se_<%=i%>"     value='<%=detail.get("se")%>'   maxlength='<%=list.getColumn("se").getPrecision()%>'   readonly></td><!--含税金额-->
                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'  align="right"     style="width:<%=widths[5]%>" onKeyDown="return getNextElement();" name="jshj_<%=i%>" id="jshj_<%=i%>" value='<%=detail.get("jshj")%>' maxlength='<%=list.getColumn("jshj").getPrecision()%>' readonly></td><!--价税合计-->
                         <%
                          FieldInfo[] bakFields = detailProducer.getBakFieldCodes();
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
                        <td class="td"><input id="t_hssl" name="t_hssl" type="hidden" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <%--<td class="td" align="right"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td class="td">&nbsp;</td>--%>
                        <td class="td" align="right"><input id="t_sl" name="t_sl" type="text" class='ednone_r' style="width:<%=widths[0]%>" value='<%=t_sl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" align="right"><input id="t_je" name="t_je" type="text" class='ednone_r' style="width:70" value='<%=t_je%>' style="width:<%=widths[2]%>" readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" align="right"><input id="t_se" name="t_se" type="text" class='ednone_r' style="width:70" value='<%=t_se%>' style="width:<%=widths[2]%>" readonly></td>
                        <td class="td" align="right"><input id="t_jshj" name="t_jshj" type="text" class='ednone_r' style="width:70" value='<%=t_jshj%>' style="width:<%=widths[2]%>" readonly></td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
                    <SCRIPT LANGUAGE="javascript">rowinfo = new RowControl();
                    <%for(int k=0; k<i; k++)
                      {
                        out.print("AddRowItem(rowinfo,'rowinfo_"+k+"');");
                      }%>AddRowItem(rowinfo,'rowinfo_end');InitRowControl(rowinfo);</SCRIPT></td>
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
             <%if(!isEnd){%>
             <input type="hidden" name="selectedtdid" value="">
             <input name="btnback"  style="width:100" class="button" type="button" value="引入采购单(W)" onClick="selctbilloflading()" border="0">
             <pc:shortcut key="w" script='selctbilloflading()'/>
             <input type="hidden" name="multiIdInput" value="引入采购单货物" onchange="sumitForm(<%=b_BuyInvoiceBean.JHDHW%>,-1)">
             <input name="btnback"  style="width:120" class="button" type="button" value="引入采购单货物(I)" onClick="impot()" border="0">
             <pc:shortcut key="i" script="impot()"/>

              <input name="button2"  style="width:110" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value=" 保存新增(N) ">
              <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <input name="btnback"  style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
              <%if(isCanDelete&&!b_BuyInvoiceBean.isReport){
              String del ="if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+");";
              String detNull = "sumitForm("+b_BuyInvoiceBean.DEL_NULL+");";
               %>
              <input name="button3"  style="width:120"  style="width:110" type="button" class="button" title="删除数量为空行(R)" value="删除数量为空行(R)" onClick="<%=detNull%>">
              <pc:shortcut key="r" script="<%=detNull%>" />
              <input name="button3"  style="width:60" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value="删除(D)">
              <pc:shortcut key="d" script='<%=del%>'/>
              <%}%>
              <%}%>
              <input type="button"  style="width:60" class="button" value="打印(P)" onclick="location.href='../pub/pdfprint.jsp?code=buy_invoice_edit_bill&operate=<%=Operate.PRINT_BILL%>&a$cgfpid=<%=masterRow.get("cgfpid")%>&src=../finance_xixing/buy_invoice_edit.jsp'">
              <pc:shortcut key="p" script='print()'/>
             <%if(!b_BuyInvoiceBean.isReport){%>
             <input name="btnback"  style="width:60" type="button" class="button" onClick="backList();" value="返回(C)">
             <pc:shortcut key="c" script='backList()'/>
            <%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
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
  var slObj = document.all['sl_'+i];
  var wsdjObj = document.all['wsdj_'+i];
  var jeObj = document.all['je_'+i];
  var hsblObj = document.all['truebl_'+i];
  var hsslObj = document.all['hssl_'+i];
  var hsdjObj = document.all['hsdj_'+i];
  var zzslObj = document.all['zzsl_'+i];
  var seObj = document.all['se_'+i];
  var jshjObj = document.all['jshj_'+i];
  var obj = isBigUnit ? hsslObj : slObj;//判断是那个对象
  var showText = isBigUnit ? "输入的换算数量非法！" : "输入的数量非法！";
  var changeObj = isBigUnit ? slObj : hsslObj;//要发生变化的对象
  obj.value = obj.value.trim();

  if(obj.value=="")
    return;
  if(zzslObj.value=="")
    zzslObj.value="17";
  if(isNaN(zzslObj.value)){
    alert("输入的税率非法");
    zzslObj.focus();
    return;
  }
  if(isNaN(slObj.value)){
    alert("输入的数量非法");
    slObj.focus();
    return;
  }
  if(wsdjObj.value=="")
    wsdjObj.value=0;
  if(isNaN(wsdjObj.value)){
    alert("输入的无税单价非法");
    wsdjObj.focus();
    return;
  }
  changeObj.value = formatQty((parseFloat(slObj.value)/parseFloat(hsblObj.value)));
  jeObj.value=formatQty(parseFloat(slObj.value)*parseFloat(wsdjObj.value));
  seObj.value=formatQty(parseFloat(slObj.value)*parseFloat(wsdjObj.value)*parseFloat(zzslObj.value)*0.01);
  jshjObj.value=formatQty(parseFloat(jeObj.value)*(1+parseFloat(zzslObj.value)*0.01));
  cal_tot('sl');
  cal_tot('je');
  cal_tot('hssl');
  cal_tot('se');
  cal_tot('jshj');
}
function dj_onchange(i, isBigUnit)
{
  var slObj = document.all['sl_'+i];
  var jeObj = document.all['je_'+i];
  var wsdjObj = document.all['wsdj_'+i];

  var hsblObj = document.all['truebl_'+i];
  var hsslObj = document.all['hssl_'+i];

  var hsdjObj = document.all['hsdj_'+i];
  var zzslObj = document.all['zzsl_'+i];
  var seObj = document.all['se_'+i];
  var jshjObj = document.all['jshj_'+i];
  if(zzslObj.value=="")
    zzslObj.value="17";
  if(isNaN(zzslObj.value)){
    alert("输入的税率非法");
    zzslObj.focus();
    return;
  }
  if(slObj.value=="")
    return;
  if(isNaN(slObj.value)){
    alert("输入的数量非法");
    slObj.focus();
    return;
  }
  if(wsdjObj.value=="")
    return;
  if(isNaN(wsdjObj.value)){
    alert("输入的无税单价非法");
    wsdjObj.focus();
    return;
  }
  jeObj.value=formatQty(parseFloat(slObj.value)* parseFloat(wsdjObj.value));
  seObj.value=formatQty(parseFloat(slObj.value)* parseFloat(wsdjObj.value)*parseFloat(zzslObj.value)*0.01);
  hsdjObj.value=formatQty(parseFloat(wsdjObj.value)*(parseFloat(zzslObj.value)*0.01+1));
  jshjObj.value=formatQty(parseFloat(jeObj.value)*(1+parseFloat(zzslObj.value)*0.01));
  cal_tot('je');
  cal_tot('se');
  cal_tot('jshj');

}
function zzsl_onchange(i, isBigUnit)
{
  var slObj = document.all['sl_'+i];

  var hsblObj = document.all['truebl_'+i];
  var hsslObj = document.all['hssl_'+i];

  var hsdjObj = document.all['hsdj_'+i];
  var wsdjObj = document.all['wsdj_'+i];
  var jeObj = document.all['je_'+i];
  var zzslObj = document.all['zzsl_'+i];
  var seObj = document.all['se_'+i];
  var jshjObj = document.all['jshj_'+i];
  if(zzslObj.value=="")
    zzslObj.value="17";
  if(isNaN(zzslObj.value)){
    alert("输入的税率非法");
    zzslObj.focus();
    return;
  }
  if(slObj.value=="")
    return;
  if(isNaN(slObj.value)){
    alert("输入的数量非法");
    slObj.focus();
    return;
  }
  if(wsdjObj.value=="")
    wsdjObj.value=0;
  if(isNaN(wsdjObj.value)){
    alert("输入的无税单价非法");
    wsdjObj.focus();
    return;
  }
  seObj.value=formatQty(parseFloat(slObj.value)* parseFloat(wsdjObj.value)*parseFloat(zzslObj.value)*0.01);
  hsdjObj.value=formatQty(parseFloat(wsdjObj.value)*(parseFloat(zzslObj.value)*0.01+1));
  jshjObj.value=formatQty(parseFloat(jeObj.value)*(1+parseFloat(zzslObj.value)*0.01));
  cal_tot('se');
  cal_tot('jshj');
}
function gsl_onchange(i)
{
  var slObj = document.all['sl_'+i];
  var hsdjObj = document.all['hsdj_'+i];
  var wsdjObj = document.all['wsdj_'+i];
  var jeObj = document.all['je_'+i];
  var zzslObj = document.all['zzsl_'+i];
  var seObj = document.all['se_'+i];
  var jshjObj = document.all['jshj_'+i];
  if(zzslObj.value=="")
    zzslObj.value="17";
  if(isNaN(gslObj.value)){
    alert("输入的关税率非法");
    gslObj.focus();
    return;
  }
  jshjObj.value=formatQty(parseFloat(jeObj.value)*(1+parseFloat(zzslObj.value)*0.01));
  cal_tot('se');
  cal_tot('jshj');
}
function hsdj_onchange(i, isBigUnit)
{
  var slObj = document.all['sl_'+i];
  var hsdjObj = document.all['hsdj_'+i];
  var wsdjObj = document.all['wsdj_'+i];
  var jeObj = document.all['je_'+i];
  var zzslObj = document.all['zzsl_'+i];
  var seObj = document.all['se_'+i];
  var jshjObj = document.all['jshj_'+i];

  if(hsdjObj.value=="")
    return;
  if(zzslObj.value=="")
    zzslObj.value="17";
  if(isNaN(hsdjObj.value)){
    alert("输入的含税单价非法");
    hsdjObj.focus();
    return;
  }
  if(slObj.value=="")
  {
    alert("数量不能空!");
    slObj.focus();
    return;
  }
  if(isNaN(slObj.value)){
    alert("输入的数量非法");
    slObj.focus();
    return;
  }
  if(wsdjObj.value=="")
    wsdjObj.value=0;
  if(isNaN(wsdjObj.value)){
    alert("输入的无税单价非法");
    wsdjObj.focus();
    return;
  }
  if(zzslObj.value=="")
    zzslObj.value="17";
  wsdjObj.value = formatQty(parseFloat(hsdjObj.value))/(parseFloat(zzslObj.value)*0.01+1);
  jeObj.value=formatQty(parseFloat(slObj.value) * parseFloat(wsdjObj.value));
  seObj.value=formatQty(parseFloat(jshjObj.value) - parseFloat(slObj.value) *parseFloat(wsdjObj.value));
  jshjObj.value=formatQty(parseFloat(jeObj.value) *(1+parseFloat(zzslObj.value)*0.01));
  cal_tot('je');
  cal_tot('se');
  cal_tot('jshj');
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
    else if(type == 'jshj')
      tmpObj = document.all['jshj_'+i];
    else if(type == 'se')
      tmpObj = document.all['se_'+i];
    else if(type == 'je')
      tmpObj = document.all['je_'+i];
    else
      return;
    if(tmpObj.value!="" && !isNaN(tmpObj.value))
      tot += parseFloat(tmpObj.value);
  }
  if(type == 'sl')
    document.all['t_sl'].value = formatQty(tot);
  else if(type == 'hssl')
    document.all['t_hssl'].value = formatQty(tot);
  else if(type == 'jshj')
    document.all['t_jshj'].value = formatQty(tot);
  else if(type == 'se')
    document.all['t_se'].value = formatQty(tot);
  else if(type == 'je')
    document.all['t_je'].value = formatSum(tot);
}
function impot()
{
  if(form1.dwtxid.value=='')
   {
    alert('请选择供货单位');
    return;
    }
    if(form1.deptid.value=='')
     {
      alert('请选择部门');
      return;
    }
    if(form1.personid.value=='')
     {
      alert('请选择业务员');
      return;
    }
 OrderMultiSelect('form1','srcVar=multiIdInput&dwtxid='+form1.dwtxid.value+'&deptid='+form1.deptid.value+'&personid='+form1.personid.value);
}
function OrderMultiSelect(frmName, srcVar, methodName,notin)
{
     var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
     var winName= "OrdersSelector";
     paraStr = "../finance/import_buy_ordergoods.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
     if(methodName+'' != 'undefined')
       paraStr += "&method="+methodName;
     if(notin+'' != 'undefined')
     paraStr += "&notin="+notin;
     newWin =window.open(paraStr,winName,winopt);
     newWin.focus();
}
function OrderSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
  {
   var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
   var winName= "SingleladingSelector";
   paraStr = "../finance/import_stocking.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&dwtxid="+curID;
   if(methodName+'' != 'undefined')
     paraStr += "&method="+methodName;
   if(notin+'' != 'undefined')
     paraStr += "&notin="+notin;
   newWin =window.open(paraStr,winName,winopt);
   newWin.focus();
}
function selctbilloflading()
{
    form1.selectedtdid.value='';
    OrderSingleSelect('form1','srcVar=selectedtdid','fieldVar=jhdID&personid='+form1.personid.value,form1.dwtxid.value,"sumitForm(<%=b_BuyInvoiceBean.DETAIL_SALE_ADD%>,-1)");
}
function print()
{
   location.href='../pub/pdfprint.jsp?code=buy_invoice_edit_bill&operate=<%=Operate.PRINT_BILL%>&a$cgfpid=<%=masterRow.get("cgfpid")%>&src=../finance_xixing/buy_invoice_edit.jsp'
}
//产品编码
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsbl_'+i+'&srcVar=hsdw_'+i,'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsbl&fieldVar=hsdw', obj.value);
}
//产品名称
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsbl_'+i+'&srcVar=hsdw_'+i,'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsbl&fieldVar=hsdw', obj.value);
}
</script>
<%//&#$
if(b_BuyInvoiceBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
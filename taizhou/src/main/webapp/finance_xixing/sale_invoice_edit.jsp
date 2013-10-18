<%--销售发票--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.finance.xixing.B_SaleInvoice b_SaleInvoiceBean = engine.erp.finance.xixing.B_SaleInvoice.getInstance(request);
  String pageCode = "sale_invoice";
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
  location.href='sale_invoice.jsp';
}
//购货单位代码
function customerCodeSelect(obj)
{
    CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=khh&srcVar=zh&srcVar=sh&srcVar=dz','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=khh&fieldVar=zh&fieldVar=nsrdjh&fieldVar=addr',obj.value,'sumitForm(<%=b_SaleInvoiceBean.DWTXID_CHANGE%>,-1)');
}
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=khh&srcVar=zh&srcVar=sh&srcVar=dz','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=khh&fieldVar=zh&fieldVar=nsrdjh&fieldVar=addr',obj.value,'sumitForm(<%=b_SaleInvoiceBean.DWTXID_CHANGE%>,-1)');
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
function fplbchange()
{
  sumitForm(<%=b_SaleInvoiceBean.FPLB_CHANGE%>,-1);
}
</script>

<%
  String retu = b_SaleInvoiceBean.doService(request, response);
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
  //engine.project.LookUp saleLadingBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_LADING_BILL);
  engine.project.LookUp OutputLadingBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_LADING_OUTTPUT);//得到销售出库单号
  engine.project.LookUp salePriceBean = b_SaleInvoiceBean.getSalePriceBean(request);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp invoiceTypeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_INVOICE_TYPE);//发票种类

  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_SaleInvoiceBean.getMaterTable();
  EngineDataSet list = b_SaleInvoiceBean.getDetailTable();//引用过来的数据集
  HtmlTableProducer masterProducer = b_SaleInvoiceBean.masterProducer;
  HtmlTableProducer detailProducer = b_SaleInvoiceBean.detailProducer;
  RowMap masterRow = b_SaleInvoiceBean.getMasterRowinfo();//主表一行
  RowMap[] detailRows= b_SaleInvoiceBean.getDetailRowinfos();//从表多行
  String zt=masterRow.get("zt");
  //&#$
  if(b_SaleInvoiceBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  String fpzl=masterRow.get("fpzl");
  boolean isEnd =  b_SaleInvoiceBean.isReport||b_SaleInvoiceBean.isApprove || (!b_SaleInvoiceBean.masterIsAdd() && !zt.equals("0"));
  //没有结束,在修改状态,并有删除权限
  boolean isCanDelete = !isEnd && !b_SaleInvoiceBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);
  isEnd = isEnd || !(b_SaleInvoiceBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=edline" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("0") ? ("未审核") : ("已审核");
  boolean count=list.getRowCount()==0?true:false;
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
%>
<BODY oncontextmenu="window.event.returnValue=true" onLoad="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">销售发票(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0" width="100%">
                <%corpBean.regData(ds,"dwtxid");%>
                  <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("kprq").getFieldname()%></td>
                  <td  noWrap class="td">
                  <input type="text" name="kprq" value='<%=masterRow.get("kprq")%>' maxlength='<%=ds.getColumn("kprq").getPrecision()%>' style="width:110" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();" >
                  <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.kprq);"></a><%}%>
                  </td>
                  <td  noWrap class="tdTitle">单据号</td>
                  <td  noWrap class="td"><input type="text" name="fphm" value='<%=masterRow.get("fphm")%>' maxlength='<%=ds.getColumn("fphm").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                   <td  noWrap class="tdTitle">发票号码</td>
                  <td  noWrap class="td"><input type="text" name="sjhm" value='<%=masterRow.get("sjhm")%>' maxlength='<%=ds.getColumn("sjhm").getPrecision()%>' style="width:110" class="edbox" onKeyDown="return getNextElement();" ></td>
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

                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwtxid").getFieldname()%></td><%--购货单位--%>
                  <td colspan="3" noWrap class="td">
                    <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text" <%=detailClass%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="customerCodeSelect(this)" <%=readonly%>>
                    <input type="text" <%=detailClass%>  name="dwmc"  onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:255"  onchange="customerNameSelect(this)"    <%=readonly%>>
                    <%if(!isEnd){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=khh&srcVar=zh&srcVar=sh&srcVar=dz','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=khh&fieldVar=zh&fieldVar=nsrdjh&fieldVar=addr',form1.dwtxid.value,'sumitForm(<%=b_SaleInvoiceBean.DWTXID_CHANGE%>,-1)');">
                    <%}%>
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
                 <td  noWrap class="tdTitle">结算方式</td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+balanceModeBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="jsfsid"  style="width:110">
                    <%=balanceModeBean.getList(masterRow.get("jsfsid"))%>
                    </pc:select><%}%>
                 </td>
                 </tr>
                 <tr>
                  <td noWrap class="tdTitle">收款部门</td>
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
              <%--   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>
                  <td   noWrap class="td">
                    <%
                      if(!isEnd)
                        personBean.regConditionData(ds, "deptid");
                      if(isEnd) out.print("<input type='text' value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="personid" style="width:110">
                      <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                 </td>--%>
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
                          <input type="hidden" name="selectedsfdjid" value="">
                          <input name="image" class="img" type="image" title="新增(A)" onClick="selctbilloflading()" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script="selctbilloflading()" />
                          <%}%>
                        </td>
                        <td height='20' nowrap>调拨单号</td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("wsdj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("je").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("zzsl").getFieldname()%>(%)</td>
                        <td nowrap><%=detailProducer.getFieldInfo("hsdj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("se").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jshj").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%
                      propertyBean.regData(list,"dmsxID");
                      prodBean.regData(list,"cpid");

                      OutputLadingBean.regData(list,"rkdmxid");

                      salePriceBean.regData(list,"tdhwid");
                      BigDecimal t_sl = new BigDecimal(0), t_se = new BigDecimal(0), t_je = new BigDecimal(0),t_jshj = new BigDecimal(0);
                      int i=0;
                      String[] widthName = new String[]{"sl", "hsdj", "wsdj","je","zzsl","se"};
                      int[] widthMin = new int[]{60, 60, 70,60,60,60,60,150};
                      int[] widths = b_SaleInvoiceBean.getMaxStyleWidth(detailRows, widthName, widthMin);
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(b_SaleInvoiceBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String je = detail.get("je");
                        if(b_SaleInvoiceBean.isDouble(je))
                          t_je = t_je.add(new BigDecimal(je));
                        String jje = detail.get("jje");
                        String se = detail.get("se");
                        if(b_SaleInvoiceBean.isDouble(se))
                          t_se= t_se.add(new BigDecimal(se));
                        String jshj = detail.get("jshj");
                        if(b_SaleInvoiceBean.isDouble(jshj))
                          t_jshj = t_jshj.add(new BigDecimal(jshj));
                        String cpid = detail.get("cpid");

                     %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(!isEnd){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%></td>
                        <%
                        RowMap prodRow = prodBean.getLookupRow(detail.get("cpid"));
                        RowMap  saleLadingRow= OutputLadingBean.getLookupRow(detail.get("rkdmxid"));
                        String isprops=prodRow.get("isprops");
                        String isdel=prodRow.get("isdelete");
                        %>
                        <td class="td" nowrap><%=saleLadingRow.get("sfdjdh")%></td>
                        <td class="td" nowrap><%=prodRow.get("cpbm")%></td><!--产品编码(存货代码)--->
                        <td class="td" nowrap><%=prodRow.get("product")%><input type="hidden"  id="dmsxid_<%=i%>"  name="dmsxid_<%=i%>"  value='<%=detail.get("dmsxid")%>' ></td><!--品名 规格(存货名称与规格)--->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>"     value='<%=detail.get("sl")%>'   maxlength='<%=list.getColumn("sl").getPrecision()%>'   onchange="sl_onchange(<%=i%>, false)" <%=readonly%>></td><!--数量-->
                        <td class="td" nowrap><%=prodRow.get("jldw")%></td><!--计量单位--->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="wsdj_<%=i%>" name="wsdj_<%=i%>" value='<%=detail.get("wsdj")%>' maxlength='<%=list.getColumn("wsdj").getPrecision()%>' onchange="dj_onchange(<%=i%>, false)"  <%=readonly%>></td><!--无税单价-->
                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'  align="right"     style="width:<%=widths[2]%>" onKeyDown="return getNextElement();" id="je_<%=i%>" name="je_<%=i%>"     value='<%=detail.get("je")%>'   maxlength='<%=list.getColumn("je").getPrecision()%>'   readonly></td><!--无税金额-->
                        <%String clss = fpzl.equals("1")?detailClass_r:"class='ednone_r'";%>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  style="width:<%=widths[2]%>" onKeyDown="return getNextElement();" id="zzsl_<%=i%>" name="zzsl_<%=i%>" value='<%=detail.get("zzsl")%>' maxlength='<%=list.getColumn("zzsl").getPrecision()%>' onchange="zzsl_onchange(<%=i%>, false)"  <%=readonly%> ></td><!--税率-->
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  align="right"    style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="hsdj_<%=i%>" name="hsdj_<%=i%>" value='<%=detail.get("hsdj")%>' maxlength='<%=list.getColumn("hsdj").getPrecision()%>'  onchange="hsdj_onchange(<%=i%>, true)" <%=readonly%>></td><!--含税单价-->
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
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
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
                    </table>
                    <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bz").getFieldname()%></td><%--其他信息--%>
                   <td colspan="7" noWrap class="td"><textarea name="bz" rows="3" onKeyDown="return getNextElement();" style="width:690" <%=(zt.equals("9"))?"readonly":""%> ><%=masterRow.get("bz")%></textarea></td>
                </tr>

                  </div>
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
             <input name="btnback" class="button" type="button" value="引入调拨单(W)" onClick="selctbilloflading()" border="0">
             <pc:shortcut key="w" script="selctbilloflading()" />
             <input type="hidden" name="rkdmxids" value="" onchange='sumitForm(<%=b_SaleInvoiceBean.PRODUCT_ADD%>,-1)' >
             <input name="btnback" class="button" type="button" value="引入调拨单货物(E)" onClick="selctHthwOfLading()" border="0">
             <pc:shortcut key="e" script="selctHthwOfLading()" />
            <%}%>
            <%
              if(zt.equals("9")){
            String we= "sumitForm("+b_SaleInvoiceBean.FPHMPOST+");";
            %>
            <input name="button2" type="button" class="button" onClick="sumitForm(<%=b_SaleInvoiceBean.FPHMPOST%>);" value="保存">
            <pc:shortcut key="n" script="<%=we%>" />
            <%}%>

            <%
              if(!isEnd){
            String we= "sumitForm("+Operate.POST_CONTINUE+");";
            String po = "sumitForm("+Operate.POST+");";
            %>
            <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存继续(N)">
            <pc:shortcut key="n" script="<%=we%>" />
            <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
            <pc:shortcut key="s" script="<%=po%>" />
              <%}if(isCanDelete){
              String del = "if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+");";
              %>
           <input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value="删除(D)">
            <pc:shortcut key="d" script="<%=del%>" />
           <%}%>
            <%
              if(!isEnd){

            %>
           <input type="button" class="button" value="打印(P)" onclick="location.href='../pub/pdfprint.jsp?code=xs_invoice_edit_bill&operate=<%=Operate.PRINT_BILL%>&a$xsfpid=<%=masterRow.get("xsfpid")%>&src=../finance/sale_invoice_edit.jsp'">
           <%}%>
           <%
           String pr = "location.href='../pub/pdfprint.jsp?code=xs_invoice_edit_bill&operate="+Operate.PRINT_BILL+"&a$xsfpid="+masterRow.get("xsfpid")+"&src=../finance/sale_invoice_edit.jsp'";%>
           <pc:shortcut key="p" script="<%=pr%>" />

           <%if(!b_SaleInvoiceBean.isReport&&!zt.equals("9")){%>
           <input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C) ">
           <pc:shortcut key="c" script="backList();" />
           <%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatwsdj(srcStr){ return formatNumber(srcStr, '<%=loginBean.getWsdjPriceFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function sl_onchange(i, isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      var wsdjObj = document.all['wsdj_'+i];
      var jeObj = document.all['je_'+i];

      var hsdjObj = document.all['hsdj_'+i];
      var zzslObj = document.all['zzsl_'+i];
      var seObj = document.all['se_'+i];
      var jshjObj = document.all['jshj_'+i];
      if(zzslObj.value=="")
        return;
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
        jeObj.value=formatQty(parseFloat(slObj.value) * parseFloat(wsdjObj.value));
      cal_tot('sl');
      cal_tot('je');

      seObj.value=formatQty(parseFloat(slObj.value) * parseFloat(wsdjObj.value)*parseFloat(zzslObj.value)*0.01);
      jshjObj.value=formatQty(parseFloat(slObj.value) * parseFloat(hsdjObj.value));
      cal_tot('se');
      cal_tot('jshj');

    }
    function dj_onchange(i, isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      var jeObj = document.all['je_'+i];
      var wsdjObj = document.all['wsdj_'+i];

      var hsdjObj = document.all['hsdj_'+i];
      var zzslObj = document.all['zzsl_'+i];
      var seObj = document.all['se_'+i];
      var jshjObj = document.all['jshj_'+i];
      if(zzslObj.value=="")
        return;
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
        jeObj.value=formatQty(parseFloat(slObj.value) * parseFloat(wsdjObj.value));
        cal_tot('je');

        seObj.value=formatQty(parseFloat(slObj.value) * parseFloat(wsdjObj.value)*parseFloat(zzslObj.value)*0.01);
        jshjObj.value=formatQty(parseFloat(jeObj.value) + parseFloat(seObj.value));
        hsdjObj.value=formatQty(parseFloat(wsdjObj.value)*(parseFloat(zzslObj.value)*0.01+1));
        cal_tot('se');
        cal_tot('jshj');

    }
    function zzsl_onchange(i, isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      var hsdjObj = document.all['hsdj_'+i];
      var wsdjObj = document.all['wsdj_'+i];
      var jeObj = document.all['je_'+i];
      var zzslObj = document.all['zzsl_'+i];
      var seObj = document.all['se_'+i];
      var jshjObj = document.all['jshj_'+i];
      if(zzslObj.value=="")
        return;
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
        seObj.value=formatQty(parseFloat(slObj.value) * parseFloat(wsdjObj.value)*parseFloat(zzslObj.value)*0.01);
        jshjObj.value=formatQty(parseFloat(jeObj.value) + parseFloat(seObj.value));
        hsdjObj.value=formatQty(parseFloat(wsdjObj.value)*(parseFloat(zzslObj.value)*0.01+1));
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
        if(zzslObj.value=="")
        {
          alert("税率不能空!");
          zzslObj.focus();
          return;
        }
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
        if(parseFloat(wsdjObj.value)==0)
          return;
        wsdjObj.value = formatwsdj(parseFloat(hsdjObj.value))/(parseFloat(zzslObj.value)*0.01+1);
        jshjObj.value=formatQty(parseFloat(slObj.value) * parseFloat(hsdjObj.value));
        seObj.value=formatQty(parseFloat(jshjObj.value) - parseFloat(slObj.value) *parseFloat(wsdjObj.value));

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

      else if(type == 'jshj')
        document.all['t_jshj'].value = formatQty(tot);
      else if(type == 'se')
        document.all['t_se'].value = formatQty(tot);

      else if(type == 'je')
        document.all['t_je'].value = formatSum(tot);
    }
    //dwtxid=1,dwtxid=dwtxid
function selctbilloflading()
{
    form1.selectedsfdjid.value='';
    OrderSingleSelect('form1','srcVar=selectedsfdjid','fieldVar=sfdjid',form1.dwtxid.value,"sumitForm(<%=b_SaleInvoiceBean.DETAIL_SALE_ADD%>,-1)");
}
function OrderSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
{
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
    var winName= "SingleladingSelector";
    paraStr = "../finance/sale_invoice_import_lading.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&dwtxid="+curID;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
}
function TdhwMultiSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
   {
     var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
     var winName= "MultiProdSelector";
     paraStr = "../finance/sale_invoice_import_lading_product.jsp?operate=0&multi=1&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&dwtxid="+curID;
     if(methodName+'' != 'undefined')
       paraStr += "&method="+methodName;
     if(notin+'' != 'undefined')
       paraStr += "&notin="+notin;
     newWin =window.open(paraStr,winName,winopt);
     newWin.focus();
}

function selctHthwOfLading()
{
  if(form1.dwtxid.value=='')
  {
  alert('请选择购货单位');
  return;
  }
  TdhwMultiSelect('form1','srcVar=rkdmxids','fieldVar=sfdjid',form1.dwtxid.value,"sumitForm(<%=b_SaleInvoiceBean.PRODUCT_ADD%>,-1)");
}
</script>
<%//&#$
if(b_SaleInvoiceBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
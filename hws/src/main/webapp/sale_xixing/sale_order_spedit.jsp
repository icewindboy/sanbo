<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,java.text.*,java.util.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.sale.xixing.B_SaleOrder saleOrderBean = engine.erp.sale.xixing.B_SaleOrder.getInstance(request);
  String pageCode = "sale_order";
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
  location.href='sale_order.jsp';
}
function addDate(sdate,sl)
{
  var datestr = sdate.replace(/-/gi, "/");
  var dt = new Date(datestr);
  var dt2 = new Date(dt.getYear() + "/" + (dt.getMonth() + 1) + "/" + (dt.getDate()+sl));
  var obj = dt2.getYear() + "-" + (dt2.getMonth() + 1) + "-" + dt2.getDate();
  return obj;
}
//产品编码
function productCodeSelect(obj, i)
{
    SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=wzdjid_'+i+'&srcVar=cpid_'+i+'&dwtxid='+form1.dwtxid.value,'fieldVar=wzdjid&fieldVar=cpid',obj.value,'sumitForm(<%=saleOrderBean.DETAIL_CHANGE%>,'+i+')');
}
//产品名称
function productNameSelect(obj, i)
{
    SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=wzdjid_'+i+'&srcVar=cpid_'+i+'&dwtxid='+form1.dwtxid.value,'fieldVar=wzdjid&fieldVar=cpid',obj.value,'sumitForm(<%=saleOrderBean.DETAIL_CHANGE%>,'+i+')');
}
//客户编码
function customerCodeSelect(obj)
{
    CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwtxid&srcVar=dwmc&srcVar=dh&srcVar=dz&srcVar=lxr&srcVar=cz','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=tel&fieldVar=addr&fieldVar=lxr&fieldVar=cz',obj.value,'sumitForm(<%=saleOrderBean.DWTXID_CHANGE%>,-1)');
}
//客户名称
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwtxid&srcVar=dwmc&srcVar=dh&srcVar=dz&srcVar=lxr&srcVar=cz','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=tel&fieldVar=addr&fieldVar=lxr&fieldVar=cz',obj.value,'sumitForm(<%=saleOrderBean.DWTXID_CHANGE%>,-1)');
}
function propertiesInput(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i, 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
  //PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i, 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value, 'sumitForm(<%=saleOrderBean.DETAIL_CHANGE%>,'+i+')');
}
function add()
{
  if(form1.dwtxid.value=='')
  {
    alert('请选择购货单位!');
    return;
  }
 sumitForm(<%=Operate.DETAIL_ADD%>);
}
function openwin(rownum)
{
  paraStr = "sale_product_capacity.jsp?operate=0&htid="+rownum;
  openSelectUrl(paraStr, "SingleCustSelector", winopt2);
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                     'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
</script>

<%String retu = saleOrderBean.doService(request, response);
  if(retu.indexOf("backList();")>-1 || retu.indexOf("toFee")>-1 || retu.indexOf("toDock")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门

  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp salePriceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_PRICE);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//LookUp产品信息
  engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//引用结算方式
  engine.project.LookUp sendModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SEND_MODE);//发货方式
  engine.project.LookUp orderTypeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_ORDER_TYPE);//合周类型

  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = saleOrderBean.getMaterTable();
  EngineDataSet list = saleOrderBean.getDetailTable();
  EngineDataSet regData = saleOrderBean.getRegDataTable();
  //RowMap[] qtfyRows= saleOrderBean.getTdqtfyRowinfos();//从表多行
  boolean isnew = saleOrderBean.getState();
  boolean master = engine.erp.sale.B_Sale_Judgement.isUsed(list,"skdsl");//合同是否已被引用
  boolean mustConversion = saleOrderBean.conversion;//是否需要强制转换
  HtmlTableProducer masterProducer = saleOrderBean.masterProducer;
  HtmlTableProducer detailProducer = saleOrderBean.detailProducer;
  RowMap masterRow = saleOrderBean.getMasterRowinfo();
  RowMap jsgsRow=saleOrderBean.getJjjsgs();//业务员奖金计算
  String xsjzj=jsgsRow.get("xsjzj");//销售价增加
  String tclzj=jsgsRow.get("tclzj");//提成率增加
  String hltszj=jsgsRow.get("hltszj");//回笼天数增加
  String xsjjs=jsgsRow.get("xsjjs");//销售价减少
  String tcljs=jsgsRow.get("tcljs");//提成率减少
  String hltsjs=jsgsRow.get("hltsjs");//回笼天数减少
  RowMap[] detailRows= saleOrderBean.getDetailRowinfos();
  String zt=masterRow.get("zt");
  //&#$
  if(saleOrderBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  orderTypeBean.regData(ds, "ordertypeid");
  sendModeBean.regData(ds, "sendmodeid");
  prodBean.regData(regData,"cpid");
  salePriceBean.regData(regData,"wzdjid");
  boolean isEnd =  saleOrderBean.isReport||saleOrderBean.isApprove || (!saleOrderBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !saleOrderBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(saleOrderBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  String czyid= masterRow.get("czyid");
  String loginid= saleOrderBean.loginid;
  isEnd = isEnd||!czyid.equals(loginid);

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=edline" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  String title = zt.equals("0") ? ("未审批") : (zt.equals("9") ? "审批中" :(zt.equals("4")?"已作废":(zt.equals("8")?"完成":"已审")) );
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
  boolean count=list.getRowCount()==0?true:false;
  String xyhkts = saleOrderBean.hkts;
  String jglx = saleOrderBean.jglx;
%>
<BODY oncontextmenu="window.event.returnValue=true" onLoad="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();"  >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <input type="HIDDEN" name="wzdjid" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">销售订单</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("htbh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="htbh" value='<%=masterRow.get("htbh")%>' maxlength='<%=ds.getColumn("htbh").getPrecision()%>' style="width:110"  <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("htrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="htrq" value='<%=masterRow.get("htrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.htrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwtxid").getFieldname()%></td><%--购货单位--%>
                  <td colspan="3" noWrap class="td">
                    <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text" <%=master?"class=edline":detailClass%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="customerCodeSelect(this)" <%=master?"readonly":readonly%>>
                    <input type="text" <%=master?"class=edline":detailClass%> name="dwmc"  onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:255" onchange="customerNameSelect(this)"  <%=master?"readonly":readonly%>>
                    <%if(!isEnd&&!master){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dh&srcVar=dz&srcVar=lxr&srcVar=cz','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=tel&fieldVar=addr&fieldVar=lxr&fieldVar=cz',form1.dwtxid.value,'sumitForm(<%=saleOrderBean.DWTXID_CHANGE%>,-1)');">
                    <%}%>
                  </td>
                </tr>
                <tr>
                 <td noWrap class="tdTitle">电&nbsp;&nbsp;&nbsp;&nbsp;话</td>
                 <td noWrap class="td"><input type="text" name="dh" value='<%=masterRow.get("dh")%>' maxlength='<%=ds.getColumn("dh").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                 <td noWrap class="tdTitle">传&nbsp;&nbsp;&nbsp;&nbsp;真</td>
                 <td noWrap class="td"><input type="text" name="cz" value='<%=masterRow.get("cz")%>' maxlength='<%=ds.getColumn("cz").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                 <td noWrap class="tdTitle">地&nbsp;&nbsp;&nbsp;&nbsp;址</td>
                 <td noWrap class="td"><input type="text" name="dz" value='<%=masterRow.get("dz")%>' maxlength='<%=ds.getColumn("dz").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                 <td noWrap class="tdTitle">联系人</td>
                 <td noWrap class="td"><input type="text" name="lxr" value='<%=masterRow.get("lxr")%>' maxlength='<%=ds.getColumn("lxr").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                 </tr>
                 <tr>
                 <td noWrap class="tdTitle">部&nbsp;&nbsp;&nbsp;&nbsp;门</td>
                 <td noWrap class="td">
                    <%
                      //String onChange ="if(form1.deptid.value!='"+masterRow.get("deptid")+"')sumitForm("+saleOrderBean.DEPT_CHANGE+")";
                    %>
                    <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:80' class='edline' readonly >");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110"  onSelect="deptchange();" >
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                 </td>
                  <td  noWrap class="tdTitle">结算方式</td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+balanceModeBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="jsfsid"  style="width:110">
                    <%=balanceModeBean.getList(masterRow.get("jsfsid"))%>
                    </pc:select>
                    <%}%></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>
                  <td noWrap class="td"><%--为部门添加触发事件--%>
                    <%
                      if(!isEnd)
                        personBean.regConditionData(ds, "deptid");
                      if(isEnd) out.print("<input type='text' value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:80' class='edline' readonly>");
                    else {%>
                    <pc:select name="personid" addNull="1" style="width:110">
                      <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%>
                     </pc:select>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">发货方式</td>
                  <td  noWrap class="td">
                    <%
                      RowMap sendRow = sendModeBean.getLookupRow(masterRow.get("sendmodeid"));
                      if(isEnd) out.print("<input type='hidden' name='sendmodeid' value='"+masterRow.get("sendmodeid")+"' style='width:85' class='edline' readonly><input type='text'  value='"+sendRow.get("sendmode")+"' style='width:85' class='edline' readonly>");
                    else {%>
                    <pc:select name="sendmodeid" addNull="1" style="width:110" >
                      <%=sendModeBean.getList(masterRow.get("sendmodeid"))%> </pc:select>
                    <%}%>
                  </td>
                  </tr>
                  <tr>
                  <td noWrap class="tdTitle">订单类型</td>
                  <td  noWrap class="td">
                    <%
                    RowMap orderTypeRow = orderTypeBean.getLookupRow(masterRow.get("ordertypeid"));
                    if(isEnd) out.print("<input type='text' value='"+orderTypeRow.get("ordertype")+"' style='width:85' class='edline' readonly>");
                    else {%>
                    <pc:select name="ordertypeid" addNull="1" style="width:110" >
                      <%=orderTypeBean.getList(masterRow.get("ordertypeid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">计划发货日期</td>
                  <td noWrap class="td"><input type="text" name="jhfhrq" value='<%=masterRow.get("jhfhrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.jhfhrq);"></a>
                    <%}%>
                  </td>
                 <td noWrap class="tdTitle">是否生产</td>
                 <td noWrap class="td">
                  <%if(!isEnd){%>
                  <INPUT TYPE="radio" NAME="isproduce" VALUE="1" <%=(masterRow.get("isproduce").equals("1"))?"checked":((masterRow.get("isproduce").equals(""))?"checked":"")%>>是
                  <INPUT TYPE="radio" NAME="isproduce" VALUE="0" <%=(masterRow.get("isproduce").equals("0"))?"checked":""%>>否
                  <%}else{%><%=(masterRow.get("isproduce").equals("1"))?"是":"否"%><%}%>
                 </td>
                  <td noWrap class="tdTitle">客户类型</td>
                  <td width="120" class="td">
                  <%
                    String khlx = masterRow.get("khlx");
                     if(isEnd){
                     out.print("<input  name='khlx' type='text' value='"+masterRow.get("khlx")+"' style='width:110' class='edline' readonly>");
                    }else{%>
                  <pc:select name="khlx" style="width:110" addNull="1" value="<%=khlx%>"  >
                    <pc:option value="A">A</pc:option> <pc:option value="C">C</pc:option>
                  </pc:select>
                  <%}%>
                 </td>
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
                  <td colspan="8" noWrap class="td">
                   <div style="display:block;width:900;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){%>
                          <input type="hidden" name="multiIdInput" value="" onchange="sumitForm(<%=Operate.DETAIL_ADD%>)">
                          <input name="image" class="img" type="image" title="新增" onClick="add()" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script='add()'/>
                          <input class="edFocused_r"  name="tCopyNumber" value="1" title="拷贝(ALT+A)" size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                          <%}%>
                        </td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名规格</td>
                        <td nowrap>规格属性</td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>单位</td>
                        <%--td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
                        <td height='20' nowrap>换算单位</td--%>
                     <%--   <td nowrap>销售价</td>
                        <td nowrap><%=detailProducer.getFieldInfo("zk").getFieldname()%>(%)</td>
                        <td nowrap><%=detailProducer.getFieldInfo("dj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jje").getFieldname()%></td>--%>
                        <td nowrap height='20'>开单数量</td>
                        <td nowrap height='20'>出库数量</td>
                        <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%
                      propertyBean.regData(list,"dmsxID");
                      BigDecimal t_hssl = new BigDecimal(0), t_sl = new BigDecimal(0), t_xsje = new BigDecimal(0), t_jje = new BigDecimal(0),t_wbje = new BigDecimal(0);
                      BigDecimal t_skdsl = new BigDecimal(0);
                      BigDecimal t_skdhssl= new BigDecimal(0);
                      BigDecimal t_stsl = new BigDecimal(0);
                      BigDecimal t_sthssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String hssl = detail.get("hssl");
                        if(saleOrderBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String sl = detail.get("sl");
                        if(saleOrderBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String skdsl = list.getValue("skdsl");
                        if(saleOrderBean.isDouble(skdsl))
                          t_skdsl = t_skdsl.add(new BigDecimal(skdsl));
                        String skdhssl = list.getValue("skdhssl");
                        if(saleOrderBean.isDouble(skdhssl))
                          t_skdhssl = t_skdhssl.add(new BigDecimal(skdhssl));
                        String stsl = list.getValue("stsl");
                        if(saleOrderBean.isDouble(stsl))
                          t_stsl = t_stsl.add(new BigDecimal(stsl));
                        String sthssl = list.getValue("sthssl");
                        if(saleOrderBean.isDouble(sthssl))
                          t_sthssl = t_sthssl.add(new BigDecimal(sthssl));
                        String xsje = detail.get("xsje");
                        if(saleOrderBean.isDouble(xsje))
                          t_xsje = t_xsje.add(new BigDecimal(xsje));
                        String jje = detail.get("jje");
                        if(saleOrderBean.isDouble(jje))
                          t_jje = t_jje.add(new BigDecimal(jje));
                        String wbje = detail.get("wbje");
                        if(saleOrderBean.isDouble(wbje))
                          t_wbje = t_wbje.add(new BigDecimal(wbje));
                        boolean iscanedit = engine.erp.sale.B_Sale_Judgement.isUsedRow(list,"skdsl",i);
                        RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                        String wzdjid=detail.get("wzdjid");
                        RowMap priceRow = salePriceBean.getLookupRow(detail.get("wzdjid"));
                        String hsbl = priceRow.get("hsbl");
                        String hkts=priceRow.get("hkts");//?
                        String hktcl=priceRow.get("hktcl");//?
                        String kckgl=priceRow.get("kckgl");//库成可供量?
                        String ztqq=priceRow.get("ztqq");//货物提前期
                        String cpid=priceRow.get("cpid");
                        String xstcl=priceRow.get("xstcl");
                        String xs_jzj=priceRow.get("xsjzj");

                        String ccj = priceRow.get("ccj");
                        String msj = priceRow.get("msj");
                        String lsj = priceRow.get("lsj");
                        String qtjg1 = priceRow.get("qtjg1");
                        String qtjg2 = priceRow.get("qtjg2");
                        String qtjg3 = priceRow.get("qtjg3");

                        xs_jzj = priceRow.get(jglx);

                        if(!xyhkts.equals(""))
                          hkts = xyhkts;

                        String isprops=priceRow.get("isprops");
                        String isdel=priceRow.get("isdelete");
                        detail.put("hsbl", hsbl);
                        String propertieschange = "propertiesInput(this,form1.cpid_"+i+".value,"+i+")";
                        if(isprops.equals("0"))
                          propertieschange="";

                    %>
                      <tr id="rowinfo_<%=i%>">
                         <td class="td" nowrap><%=i+1%></td>
                         <td class="td" nowrap align="center">
                         <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd&&!iscanedit){%>
                          <img style='cursor:hand' src='../images/select_prod.gif' border=0 onClick="SaleProdSingleSelect('form1','srcVar=wzdjid_<%=i%>&srcVar=cpid_<%=i%>&srcVar=xsj_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&dwtxid='+form1.dwtxid.value,'fieldVar=wzdjid&fieldVar=cpid&fieldVar=lsj&fieldVar=cpbm&fieldVar=product','','sumitForm(<%=saleOrderBean.DETAIL_CHANGE%>,<%=i%>)')">
                          <%--img style='cursor:hand' src='../images/select_prod.gif' border=0 onClick="SaleProdSingleSelect('form1','srcVar=wzdjid_<%=i%>&srcVar=cpid_<%=i%>&dwtxid='+form1.dwtxid.value,'fieldVar=wzdjid&fieldVar=cpid','','sumitForm(<%=saleOrderBean.DETAIL_CHANGE%>,<%=i%>)')"--%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除第<%=(i+1)%>条记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <input name="image" class="img" type="image" title="复制当前行" onClick="sumitForm(<%=saleOrderBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <%}%>
                         </td>
                         <td class="td" nowrap>
                                <input type="text" <%=(!isEnd&&!iscanedit)?detailClass:"class=ednone"%>  onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=priceRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=(!isEnd&&!iscanedit)?readonly:"readonly"%>>

                                <input type="hidden" name="wzdjid_<%=i%>" value="<%=detail.get("wzdjid")%>">
                                <input type='HIDDEN'  id="hkts_<%=i%>"    name='hkts_<%=i%>'    value='<%=hkts%>'>
                                <input type='HIDDEN'  id="hktcl_<%=i%>"   name='hktcl_<%=i%>'   value='<%=priceRow.get("hktcl")%>'>
                                <input type='HIDDEN'  id='hsbl_<%=i%>'    name='hsbl_<%=i%>'    value='<%=priceRow.get("hsbl")%>'>
                                <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                                <input type='HIDDEN'  id="kckgl_<%=i%>"   name='kckgl_<%=i%>'   value='<%=priceRow.get("kckgl")%>'>
                                <input type='HIDDEN'  id="ztqq_<%=i%>"    name='ztqq_<%=i%>'    value='<%=priceRow.get("ztqq")%>'>
                                <input type='HIDDEN'  id="xstcl_<%=i%>"    name='xstcl_<%=i%>'    value='<%=priceRow.get("xstcl")%>'>
                                <input type="HIDDEN"  id="xsje_<%=i%>"    name="xsje_<%=i%>"    value='<%=detail.get("xsje")%>'   >
                                <input type="HIDDEN"  id="t_xsje"         name="t_xsje"  class="ednone_r" style="width:70" value='<%=t_xsje%>' readonly>

                                <input type="HIDDEN"  id="cpid_<%=i%>"  name="cpid_<%=i%>"  value='<%=cpid%>' >
                                <input type="HIDDEN"  id="xs_jzj_<%=i%>"  name="xs_jzj_<%=i%>"  value='<%=xs_jzj%>' >

                                <input type="hidden" class='ednone_r'  onKeyDown="return getNextElement();" id="wbje_<%=i%>" name="wbje_<%=i%>" value='<%=detail.get("wbje")%>' readonly>
                                <input type="hidden" class='ednone_r'  onKeyDown="return getNextElement();" id="jzj_<%=i%>" name="jzj_<%=i%>" value='<%=detail.get("jzj")%>' readonly>
                                <input type="hidden" class='ednone_r'  onKeyDown="return getNextElement();" id="cjtcl_<%=i%>" name="cjtcl_<%=i%>" value='<%=detail.get("cjtcl")%>' readonly>
                                <input type="hidden" class='ednone_r'  onKeyDown="return getNextElement();" id="jxts_<%=i%>" name="jxts_<%=i%>" value='<%=detail.get("jxts")%>' readonly>
                                <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="hlts_<%=i%>" name="hlts_<%=i%>" value='<%=detail.get("hlts")%>' <%=readonly%>>
                                <input type="hidden" class='ednone_r'  onKeyDown="return getNextElement();" id="hltcl_<%=i%>" name="hltcl_<%=i%>" value='<%=detail.get("hltcl")%>' readonly>
                                <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" name="jhrq_<%=i%>" value='<%=detail.get("jhrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)">

                               <input type="hidden"  <%=detailClass_r%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>'  onblur="sl_onchange(<%=i%>, true)"  <%=readonly%>>
                               <input type='hidden' class="ednone" style="width:20" value='<%=priceRow.get("hsdw")%>'>
                        </td>
                        <td class="td" nowrap><input type="text" <%=(!isEnd&&!iscanedit)?detailClass:"class=ednone"%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=priceRow.get("product")%>'  onchange="productNameSelect(this,<%=i%>)"   <%=(!isEnd&&!iscanedit)?readonly:"readonly"%>></td>
                        <td class="td" nowrap>
                        <input class="edFocused" id="sxz_<%=i%>" name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();">
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=prodRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value)">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        </td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onblur="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" style="width:20" class='ednone_r'  onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=priceRow.get("jldw")%>'  readonly></td>
                        <input type="hidden"  class="ednone"  onKeyDown="return getNextElement();" id="xsj_<%=i%>" name="xsj_<%=i%>" value='<%=detail.get("xsj")%>'  readonly>
                        <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="zk_<%=i%>" name="zk_<%=i%>" value='<%=detail.get("zk")%>' >
                        <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>'>
                        <input type="hidden" class='ednone_r' size=16 onKeyDown="return getNextElement();" id="jje_<%=i%>" name="jje_<%=i%>" value='<%=detail.get("jje")%>' readonly>
                        <td><input type="text" class='ednone_r' size=10 onKeyDown="return getNextElement();" value='<%=detail.get("skdsl")%>' readonly></td>
                        <td><input type="text" class='ednone_r' size=10 onKeyDown="return getNextElement();" value='<%=detail.get("stsl")%>' readonly></td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" name="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>'<%=readonly%>></td>
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
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
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
                        <td class="td"></td>
                      <%--  <td class="td"></td>
                        <td class="td"><input id="t_jje" name="t_jje" type="text" class="ednone_r" style="width:100%" value='<%=t_jje%>' readonly></td>--%>
                        <td class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_skdsl%>' readonly></td>
                        <td class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_stsl%>' readonly></td>
                        <td class="td"></td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table>
                   </div>
                    <SCRIPT LANGUAGE="javascript">rowinfo = new RowControl();
                    <%for(int k=0; k<i; k++)
                      {
                        out.print("AddRowItem(rowinfo,'rowinfo_"+k+"');");
                      }%>AddRowItem(rowinfo,'rowinfo_end');InitRowControl(rowinfo);</SCRIPT></td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("qtxx").getFieldname()%></td><%--其他信息--%>
                  <td colspan="7" noWrap class="td"><textarea name="qtxx" rows="3" onKeyDown="return getNextElement();" style="width:690"<%=readonly%>><%=masterRow.get("qtxx")%></textarea></td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("czrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("czy")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
            <input name="btnback"  style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
              <%if(!saleOrderBean.isApprove){%>
              <%if(!isEnd){%>
              <input name="button2"  style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
              <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <%
                if(isCanDelete&&!master){
                  String del = "if(confirm('是否删除该记录？')) sumitForm("+Operate.DEL+");";
                  String detNull = "sumitForm("+saleOrderBean.DEL_NULL+");";
              %>
              <input name="button3"  style="width:110" type="button" class="button" title="删除数量为空行(R)" value="删除数量为空行(R)" onClick="<%=detNull%>">
              <pc:shortcut key="r" script="<%=detNull%>" />
               <input name="button3"  style="width:50" type="button" class="button" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>);" value="删除(D)">
               <pc:shortcut key="d" script='<%=del%>'/>
               <%}%><%}%>
              <%String print = "location.href='../pub/pdfprint.jsp?code=xs_order_edit_bill&operate="+Operate.PRINT_BILL+"&a$htid="+masterRow.get("htid")+"&src=../sale/sale_order_edit.jsp'";%>
              <input type="button" class="button"  style="width:50" value="打印(P)" onclick="location.href='../pub/pdfprint.jsp?code=xs_order_edit_bill&operate=<%=Operate.PRINT_BILL%>&a$htid=<%=masterRow.get("htid")%>&src=../sale/sale_order_edit.jsp'">
              <pc:shortcut key="p" script='<%=print%>'/>
              <%if(!saleOrderBean.isReport){%>
              <input name="btnback" type="button"  style="width:50" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script='<%="backList();"%>'/>
              <%}%><%}%>
              <%--<input type="button" class="button" value=" 打印 " onclick="location.href='../pub/showdetail.jsp?operate=showdetail&code=sale_order_edit_bill&src=../store/sale_order_edit.jsp'">--%>
            </td>
          </tr>
        </table>
       </td>
    </tr>
  </table>
</form>
<script language="javascript">
initDefaultTableRow('tableview1',1);
<%=saleOrderBean.adjustInputSize(new String[]{"cpbm","product","sxz","hssl","sl","xsj","zk", "dj","bz"}, "form1", detailRows.length)%>
function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
function formatInt(srcStr){ return formatNumber(srcStr, '#0');}
function sl_onchange(i, isBigUnit)
{
  var oldhsblObj = document.all['hsbl_'+i];
  var sxzObj = document.all['sxz_'+i];
  unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'nsl_onchange('+i+','+isBigUnit+')');
}
function nsl_onchange(i, isBigUnit)
{
  var slObj = document.all['sl_'+i];
  var xsjObj = document.all['xsj_'+i];
  var xsjeObj = document.all['xsje_'+i];
  var djObj = document.all['dj_'+i];
  var jjeObj = document.all['jje_'+i];
  var wbjeObj = document.all['wbje_'+i];
  var hsblObj = document.all['truebl_'+i];
  var hsslObj = document.all['hssl_'+i];
  var kckglObj = document.all['kckgl_'+i];//库成可供量
  var ztqqObj = document.all['ztqq_'+i];//货物提前期
  var jhrqObj = document.all['jhrq_'+i];//交货日期

  var obj = isBigUnit ? hsslObj : slObj;//判断是那个对象
  var showText = isBigUnit ? "输入的换算数量非法！" : "输入的数量非法！";
  var changeObj = isBigUnit ? slObj : hsslObj;//要发生变化的对象
  obj.value = obj.value.trim();
  if(obj.value=="")
    return;
  if(isNaN(obj.value)){
    alert(showText);
    obj.focus();
    return;
  }
  if(!hsblObj.value=="")
  {
    <%
      if(mustConversion){
    %>
      changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
    <%}else {%>
      if(hsslObj.value==""||slObj.value=="")
        changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
      <%}%>
  }
    if(xsjObj.value!="" && !isNaN(xsjObj.value))
      xsjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(xsjObj.value));
    if(djObj.value!="" && !isNaN(djObj.value))
      jjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value));
    if(djObj.value!="" && !isNaN(djObj.value))
    if(kckglObj.value=="")
      kckglObj.value="0";
    if(ztqqObj.value=="")
     ztqqObj.value="0";
      if(parseFloat(slObj.value)> parseFloat(kckglObj.value))
      {
        jhrqObj.value=addDate(document.form1.htrq.value,parseFloat(ztqqObj.value));
      }
      else
      {
       jhrqObj.value='<%=new SimpleDateFormat("yyyy-MM-dd").format(new Date())%>';
      }
    cal_tot('sl');
    cal_tot('hssl');
    cal_tot('xsje');
    cal_tot('jje');
    cal_tot('wbje');
  }
  function dj_onchange(i, isRebate)
  {
    var slObj = document.all['sl_'+i];
    var zkObj = document.all['zk_'+i];
    var xsjObj = document.all['xsj_'+i];
    var xsjeObj = document.all['xsje_'+i];
    var djObj = document.all['dj_'+i];
    var jjeObj = document.all['jje_'+i];
    var wbjeObj = document.all['wbje_'+i];
    var jzjObj = document.all['jzj_'+i];
    var cjtclObj = document.all['cjtcl_'+i];
    var jxtsObj = document.all['jxts_'+i];
    var hltsObj = document.all['hlts_'+i];
    var hktclObj = document.all['hktcl_'+i];
    var jhrqObj = document.all['jhrq_'+i];
    var hktsObj = document.all['hkts_'+i];
    var xstclObj = document.all['xstcl_'+i];
    var obj = isRebate ? zkObj : djObj;
    var showText = isRebate ? "输入的折扣非法！" : "输入的单价非法！";
    var changeObj = isRebate ? djObj : zkObj;

    if(obj.value=="")
      return;
    if(isNaN(obj.value)){
      alert(showText);
      obj.focus();
      return ;
    }
    changeObj.value = formatQty(isRebate ? (parseFloat(xsjObj.value)*parseFloat(zkObj.value)/100) : (parseFloat(djObj.value)/parseFloat(xsjObj.value)*100));
    if(slObj.value!="" && !isNaN(slObj.value))
    {
      xsjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(xsjObj.value));
      jjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value));
    }else{alert("请输入数量");djObj.value="";zkObj.value="";return;}

    hltsObj.value=formatInt(hltsObj.value);
    cjtclObj.value=formatQty(cjtclObj.value)
    jxtsObj.value=hltsObj.value;
    cal_tot('xsje');
    cal_tot('jje');
    cal_tot('wbje');
  }
  function cal_tot(type)
  {
    var tmpObj;
    var tot=0;
    for(i=0; i<<%=detailRows.length%>; i++)
    {
      if(type == 'sl')
        tmpObj = document.all['sl_'+i];
      else if(type == 'xsje')
        tmpObj = document.all['xsje_'+i];
      else if(type == 'hssl')
        tmpObj = document.all['hssl_'+i];
      else if(type == 'jje')
        tmpObj = document.all['jje_'+i];
      else if(type == 'wbje')
        tmpObj = document.all['wbje_'+i];
      else
        return;
      if(tmpObj.value!="" && !isNaN(tmpObj.value))
        tot += parseFloat(tmpObj.value);
    }
    if(type == 'sl')
      document.all['t_sl'].value = formatQty(tot);
    else if(type == 'xsje')
      document.all['t_xsje'].value = formatSum(tot);
    else if(type == 'jje')
      document.all['t_jje'].value = formatSum(tot);
    //else if(type == 'wbje')
   //   document.all['t_wbje'].value = formatSum(tot);
  }
  function OrderMultiSelect(frmName, srcVar, methodName,notin)
 {
   var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
   var winName= "OrdersSelector";
   paraStr = "../sale/cust_prod_history_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
   if(methodName+'' != 'undefined')
     paraStr += "&method="+methodName;
   if(notin+'' != 'undefined')
   paraStr += "&notin="+notin;
   newWin =window.open(paraStr,winName,winopt);
   newWin.focus();
  }
  function checkdwst()
  {
    if(form1.dwtxid.value=='')
    {
      alert('请选择购货单位');
      return;
    }
    if(form1.khlx.value=='')
    {
      alert('请选择客户类型');
      return;
    }
    OrderMultiSelect('form1','srcVar=importwzdjids&lb=1&dwtxid='+form1.dwtxid.value+'&khlx='+form1.khlx.value)
        //lb=1表示客户历史记录
  }
function producthelp()
{
  if(form1.dwtxid.value=='')
  {
    alert('请选择购货单位');
    return;
  }
  if(form1.khlx.value=='')
  {
    alert('请选择客户类型');
    return;
  }
    OrderMultiSelect('form1','srcVar=importwzdjids&lb=2&dwtxid='+form1.dwtxid.value+'&khlx='+form1.khlx.value)
        //lb=2表示客户产品帮助
}
</script>
<%//&#$
if(saleOrderBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
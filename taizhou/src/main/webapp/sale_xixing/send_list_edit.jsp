<%--销售提单--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%>
<%
  engine.erp.sale.xixing.B_SendBill b_SendBillBean = engine.erp.sale.xixing.B_SendBill.getInstance(request);
  String pageCode = "send_list";
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
  location.href='send_list.jsp';
}
//输入购货单位代码
function customerCodeSelect(obj)
{
  if(form1.kdrq.value=='')
  {
    alert('请输入开单日期!');
    return;
  }
  else
  {
    if(!checkDate(form1.kdrq))
      return;
  }
  CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=personid&srcVar=deptid','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=personid&fieldVar=deptid',obj.value,'sumitForm(<%=b_SendBillBean.DWTXID_CHANGE%>,-1)');
}
//选择购货单位
function selectCustomer()
{
  if(form1.kdrq.value=='')
  {
    alert('请输入开单日期!');
    return;
  }
  else
  {
    if(!checkDate(form1.kdrq))
      return;
  }
CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=b_SendBillBean.DWTXID_CHANGE%>,-1)');
}
//输入购货单位名称
function customerNameSelect(obj)
{
  if(form1.kdrq.value=='')
  {
    alert('请输入提单日期!');
    return;
  }
  else
  {
    if(!checkDate(form1.kdrq))
      return;
  }
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=personid&srcVar=deptid','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=personid&fieldVar=deptid',obj.value,'sumitForm(<%=b_SendBillBean.DWTXID_CHANGE%>,-1)');
}
//运输单位
function TransportDmChange(obj, i)
{
    TransportCodeChange(document.all['prod'], obj.form.name,'srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc',obj.value);
}
function TransportName(obj, i)
{
    TransportNameChange(document.all['prod'], obj.form.name,'srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc',obj.value);
}
//提单日期变化--引起回款日期变化
function tdrqOnchange()
{
  checkDate(document.form1.kdrq);
  if(document.form1.hkts.value=="")
    return;
  else
  {
    document.form1.hkrq.value=addDate(document.form1.kdrq.value,parseFloat(document.form1.hkts.value));
  }
}
function selectTdrq()
{
  selectDate(document.form1.kdrq);
  if(document.form1.hkts.value=="")
    return;
  else
  {
    document.form1.hkrq.value=addDate(document.form1.kdrq.value,parseFloat(document.form1.hkts.value));
  }
}
//回款天数变化--引起回款日期变化
function hktsOnchange()
{
  if(isNaN(document.form1.hkts.value)){
    alert("输入数据非法!");
    document.form1.hkts.value="";
    return;
    }
    document.form1.hkrq.value=addDate(document.form1.kdrq.value,parseFloat(document.form1.hkts.value));
}
//引起回款日期变化--回款天数变化
function hkrqOnchange()
{
  var td = document.form1.kdrq.value;
  var datestr = td.replace(/-/gi, "/");
  var dt = new Date(datestr);
  var tds = dt.getTime();

  var hk = document.form1.hkrq.value;
  datestr = hk.replace(/-/gi, "/");
  dt = new Date(datestr);
  var hks = dt.getTime();

  document.form1.hkts.value=(hks-tds)/(1000*60*60*24);
}
function addDate(sdate,sl)
{
  var datestr = sdate.replace(/-/gi, "/");
  var dt = new Date(datestr);
  var dt2 = new Date(dt.getYear() + "/" + (dt.getMonth() + 1) + "/" + (dt.getDate()+sl));
  var obj = dt2.getYear() + "-" + (dt2.getMonth() + 1) + "-" + dt2.getDate();
  return obj;
}
//从表新增
function detailAdd()
{
if(form1.dwtxid.value=='')
{
alert('请选择购货单位!');
return;
}
if(form1.storeid.value=='')
{
alert('请选择仓库!');
return;
}
if(form1.deptid.value=='')
{
alert('请选择部门!');
return;
}
//if(form1.personid.value=='')
//{
//alert('请选择业务员!');
//return;
//}
sumitForm(<%=Operate.DETAIL_ADD%>);
}
//冲帐
function chongzhangAdd()
{
if(form1.dwtxid.value=='')
{
  alert('请选择购货单位!');
  return;
}
if(form1.storeid.value=='')
{
  alert('请选择仓库!');
  return;
}
if(form1.deptid.value=='')
{
  alert('请选择部门!');
  return;
}
if(form1.personid.value=='')
{
  alert('请选择业务员!');
  return;
}
sumitForm(<%=b_SendBillBean.CHONGZHANG_ADD%>);
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
</script>
<%
  String retu = b_SendBillBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//引用结算方式
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//引用往来单位
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//引用产品
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);//引用人员信息
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);//引用仓库信息
  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_ORDER_GOODS);//销售合同货物
  engine.project.LookUp creditBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP_CREDIT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp sendModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SEND_MODE);//发货方式
  //engine.project.LookUp productStockBean2 = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_STORE_STOCK);//当前库存   根据cpid,storeid,汇总取库量
  engine.project.LookUp productStockBean2 = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_STORE_STOCK2);//当前库存 对应规格属性取库存量
  engine.project.LookUp ykslBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TDHW_YKTDSL); //已开提单数量 2004.11.26 wlc

  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_SendBillBean.getMaterTable();
  EngineDataSet list = b_SendBillBean.getDetailTable();

//  b_SendBillBean.setPorductStockTable();  //2004.11.12库存量 wlc
//  EngineDataSet p_stock = b_SendBillBean.getProductStockData(); //2004.11.12库存量 wlc
    String tempfgsid = b_SendBillBean.getfgsid()==null? "0":b_SendBillBean.getfgsid() ;

  EngineDataSet cyqktable = b_SendBillBean.getCyqkTable();
  EngineDataSet qtfytable = b_SendBillBean.getQtfyTable();


  HtmlTableProducer masterProducer = b_SendBillBean.masterProducer;
  HtmlTableProducer detailProducer = b_SendBillBean.detailProducer;


  RowMap masterRow = b_SendBillBean.getMasterRowinfo();//主表一行
  RowMap[] detailRows= b_SendBillBean.getDetailRowinfos();//从表多行
//  RowMap[] p_stockRows = b_SendBillBean.getPorductStockInfos();//2004.11.12库存量 wlc
  RowMap[] cyqkRows= b_SendBillBean.getTdcyqkRowinfos();//从表多行
  RowMap[] qtfyRows= b_SendBillBean.getTdqtfyRowinfos();//从表多行


  creditBean.regData(ds,"dwtxid");
  ykslBean.regData(list,"hthwid");

  if(b_SendBillBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  String djlx=masterRow.get("djlx");
  sendModeBean.regData(ds, "sendmodeid");


  String zt=masterRow.get("zt");


  boolean isEnd =  b_SendBillBean.isReport||b_SendBillBean.isApprove || (!b_SendBillBean.masterIsAdd() && !zt.equals("0"));
  //没有结束,在修改状态,并有删除权限
  boolean isCanDelete = !isEnd && !b_SendBillBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);
  isEnd = isEnd || !(b_SendBillBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));


  String czyid= masterRow.get("czyid");
  String loginid= b_SendBillBean.loginId;
  isEnd = isEnd||!czyid.equals(loginid);

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=edline" : "class=edFocused";
  String prodClass = detailClass;
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  String prodreanonly = readonly;
  String title = zt.equals("0") ? ("未审批") : ((zt.equals("9") ? "审批中" :(zt.equals("4")?"已作废":(zt.equals("8")?"完成":"已审"))) );

  boolean count=list.getRowCount()==0?true:false;
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));


  boolean canedt = zt.equals("0")||zt.equals("1")||zt.equals("2");
  String djandjjeClass = canedt ?"class=edFocused_r": "class=ednone_r" ;
  String dwtxClass = count? "class=edbox":"class=edline";
  String dwtxRead = count? " " : "readonly";
  boolean mustConversion = b_SendBillBean.conversion;//是否需要强制转换
  String djhclass = zt.equals("2")?"class=edbox":"class=edline";
  String djhreadonly = zt.equals("2")?"":"readonly";


%>
<BODY id="docbody" oncontextmenu="window.event.returnValue=true"  onLoad="syncParentDiv('tableview1');">
<iframe id="iprint" style="display:none"></iframe>
<script language="javascript">
//2004.9.30 jac add
function certifyCardPrint(cpid, xsj)
{
  var paraStr = "../store_xixing/barcode_print.jsp?cpid="+cpid+"&xsj="+xsj;
  window.open(paraStr, "CertifyCardPrint");
  //document.all.iprint.src=paraStr;
}
</script>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="djlx" value="<%=djlx%>">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">销售发货通知单</td>
          </tr>
        </table>
  <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0" width="100%">
                 <tr>
                    <td  noWrap class="tdTitle">提货单编号</td>
                    <td  noWrap class="td"><input type="text" name="tdbh" value='<%=masterRow.get("tdbh")%>' maxlength='<%=ds.getColumn("tdbh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                    <td  noWrap class="tdTitle">开单日期</td>
                    <td  noWrap class="td">
                    <input type="text" name="kdrq" value='<%=masterRow.get("kdrq")%>' maxlength='10' style="width:110" <%=edClass%>  >
                    <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectTdrq()"></a><%}%>
                    </td>
                    <td noWrap class="tdTitle">部门</td>
                    <td noWrap class="td">
                    <%
                      String onChange ="if(form1.deptid.value!='"+masterRow.get("deptid")+"')sumitForm("+Operate.DEPT_CHANGE+")";
                    %>
                    <%if(isEnd||!count) out.print("<input type='hidden' name='deptid' value='"+masterRow.get("deptid")+"'><input type='text'  value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110"  onSelect="deptchange();" >
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                    </td>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>
                    <td  noWrap class="td">
                    <%
                      if(!isEnd)
                        personBean.regConditionData(ds, "deptid");
                      if(isEnd||!count) out.print("<input type='hidden' name='personid' value='"+masterRow.get("personid")+"'><input type='text'   value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:110' class='edline' readonly>");
                      else {%>
                     <pc:select name="personid"  addNull="1" style="width:110" >
                      <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                   </td>
               </tr>
               <tr>
                  <td noWrap class="tdTitle">客户名称</td><%--购货单位--%>
                  <td colspan="3" noWrap class="td">
                    <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text" <%=dwtxClass%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="customerCodeSelect(this)" <%=dwtxRead%>>
                    <input type="text" <%=dwtxClass%> name="dwmc" onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:200"   onchange="customerNameSelect(this)"  <%=dwtxRead%>>
                    <%if(count){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="selectCustomer()">
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">客户地址</td>
                  <td colspan="3" noWrap class="td">
                    <input type="text" <%=dwtxClass%> name="dz" onKeyDown="return getNextElement();" value='<%=masterRow.get("dz")%>' style="width:280"   <%=dwtxRead%>>
                  </td>
              </tr>
              <tr>
                <td   noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                <td  noWrap class="td">
                  <%if(isEnd||!count){%>
                  <input type='hidden' name='storeid' value='<%=masterRow.get("storeid")%>'>
                  <input type='text' value='<%=storeBean.getLookupName(masterRow.get("storeid"))%>' style='width:110' class='edline' readonly>
                  <%
                  }else {%>
                  <pc:select name="storeid" addNull="1" style="width:110">
                    <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                  <%}%>
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
                  <td noWrap class="tdTitle">提货人</td>
                  <td noWrap class="td">
                    <input type="text" <%=edClass%> name="thr" onKeyDown="return getNextElement();" value='<%=masterRow.get("thr")%>' style="width:110"   <%=readonly%>>
                  </td>
                  <td noWrap class="tdTitle">经办人</td>
                  <td noWrap class="td" >
                  <%if(isEnd)
                    {
                    out.print("<input type='text' value='"+masterRow.get("jbr")+"' style='width:110' class='edline' readonly>");
                    }else
                    {String jbr=masterRow.get("jbr");%>
                  <pc:select name="jbr" addNull="1" style="width:110"   combox="1" value="<%=jbr%>">
                  <%=personBean.getList()%> </pc:select>
                   <%}%></td>
                 </tr>
                 <tr>
                  <td  noWrap class="tdTitle">结算方式</td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='hidden' name='jsfsid' value='"+masterRow.get("jsfsid")+"'><input type='text'   value='"+balanceModeBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="jsfsid"  addNull="1"  style="width:110">
                    <%=balanceModeBean.getList(masterRow.get("jsfsid"))%>
                    </pc:select>
                    <%}%></td>
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
                  <td noWrap class="tdTitle">计划发货日期</td>
                  <td noWrap class="td"><input type="text" name="jhfhrq" value='<%=masterRow.get("jhfhrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.jhfhrq);"></a>
                    <%}%>
                  </td>
                    <td  noWrap class="tdTitle">实际出库日期</td>
                    <td  noWrap class="td">
                    <input type="text" name="tdrq" value='<%=masterRow.get("tdrq")%>' maxlength='10' style="width:110" class="edline" readonly   >
                    </td>
                </tr>
                <tr>
                <td noWrap class="tdTitle">付款期限</td>
                  <td noWrap class="td"><input type="text" name="fkqxs" value='<%=masterRow.get("fkqxs")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.fkqxs);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">--</td>
                  <td noWrap class="td"><input type="text" name="fkqxm" value='<%=masterRow.get("fkqxm")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.fkqxm);"></a>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){%><%if(b_SendBillBean.canOperate){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="detailAdd()" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script="detailAdd()" />
                          <%--input name="image" class="img" type="image" title="冲帐(C)" onClick="chongzhangAdd()" src="../images/add.old.gif" border="0">
                          <pc:shortcut key="a" script="chongzhangAdd()" /--%>
                         <%}%>
                          <input class="edFocused_r"  name="tCopyNumber" value="1" title="拷贝(ALT+A)" size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                          <%}%>
                        </td>
                        <td height='20' nowrap>订单号</td>
                        <td height='20' nowrap>产 品 代 码</td>
                        <td height='20' nowrap>品名规格</td>
                        <td height='20' nowrap>规格属性</td>
                        <td height='20' nowrap>订货量</td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                      <%--  <td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
                        <td nowrap>换算单位</td>--%>
                        <td nowrap><%=detailProducer.getFieldInfo("xsj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("zk").getFieldname()%>(%)</td>
                        <td nowrap><%=detailProducer.getFieldInfo("dj").getFieldname()%></td>
                        <td nowrap>金额</td>
                        <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                        <td nowrap>出库量</td>
                        <td nowrap>未出库量</td>
                        <td nowrap>库存量</td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%
                      propertyBean.regData(list,"dmsxid");
                      prodBean.regData(list,"cpid");
                      saleOrderBean.regData(list,"tdid");
                      //productStockBean.regData(list,new String[]{ "fgsid","storeid","cpid"}); //2004.11.12库存量 wlc
                      productStockBean2.regData(list,new String[]{ "fgsid","storeid","dmsxid"}); //2005.10.25库存量 zjb
                      BigDecimal t_sl = new BigDecimal(0), t_xsje = new BigDecimal(0), t_jje = new BigDecimal(0),t_hssl = new BigDecimal(0),t_kdsl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];

                        RowMap productRow = prodBean.getLookupRow(detail.get("cpid"));

                 //       String fgs = p_stockRows[i].get("fgsid")==null ? "0":p_stockRows[i].get("fgsid");
                  //      String sto = p_stockRows[i].get("storeid")==null? "-1":p_stockRows[i].get("storeid");
                 //       String cp  = p_stockRows[i].get("cpid")==null ? "-1":p_stockRows[i].get("cpid");

                        String isprops=productRow.get("isprops");
                        String cpid = detail.get("cpid");
                        String hsbl = productRow.get("hsbl");
                        detail.put("hsbl", hsbl);
                        RowMap  saleOrderRow= saleOrderBean.getLookupRow(detail.get("hthwid"));
                        //String kcl= productStockBean.getLookupName(new String[]{tempfgsid,masterRow.get("storeid"),detail.get("cpid")});
                        String kcl= productStockBean2.getLookupName(new String[]{tempfgsid,masterRow.get("storeid"),detail.get("dmsxid")});
                        boolean bh = detail.get("hthwid").equals("");
                        String instrstyle = bh?"class=edFocused_r": "class=ednone_r" ;

                        String hssl = detail.get("hssl");
                        String sl = detail.get("sl");
                        String kdsl = detail.get("kdsl");
                        String xsje = detail.get("xsje");
                        String jje = detail.get("jje");
                        String stsl = detail.get("stsl");
                        System.out.println(detail.get("hthwid"));
                        RowMap ddsl = ykslBean.getLookupRow(detail.get("hthwid"));
                        String wkdsl = ddsl.get("wksl")==""?"0":ddsl.get("wksl");
                        double dstsl = Double.parseDouble(stsl.equals("")?"0":stsl);
                        double dhssl=0;
                        double dsl=0;
                        double dkdsl=0;
                        double dxsje=0;
                        double djje=0;
                        String wtsl = "";
                        if(b_SendBillBean.isDouble(sl))
                        {
                          // 数量说明：isyrdd==true是否引入订单，wkdsl==null表示第一次引用此订单的此种货物，所以要以给出全部数量sl
                          String l = (wkdsl!="")? wkdsl:"0";
                          l=(l=="0")?sl:l;
                          dsl=b_SendBillBean.isyrdd==true?Double.parseDouble(l):Double.parseDouble(sl);
                          t_sl = t_sl.add(new BigDecimal(dsl));
                        }
                        if(b_SendBillBean.isDouble(hssl))
                        {
                          dhssl=Double.parseDouble(hssl);
                          t_hssl = t_hssl.add(new BigDecimal(dhssl));
                        }

                        if(b_SendBillBean.isDouble(kdsl))
                        {
                          dkdsl=Double.parseDouble(kdsl);
                          t_kdsl = t_kdsl.add(new BigDecimal(dkdsl));
                        }
                        if(b_SendBillBean.isDouble(jje))
                        {
                          djje=Double.parseDouble(jje);
                          t_jje = t_jje.add(new BigDecimal(djje));
                        }
                        if(!detail.get("hthwid").equals(""))
                        {
                          prodClass="class=ednone";
                          prodreanonly="readonly";
                        }
                        else
                        {
                          prodClass = detailClass;
                          prodreanonly = readonly;
                        }
                        RowMap propertyRow = propertyBean.getLookupRow(detail.get("dmsxID"));
                        wtsl = engine.util.Format.formatNumber(dsl-dstsl,"#00.00");
                     %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                         <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd){%>
                          <%if(detail.get("hthwid").equals("")){%>
                          <%--img style='cursor:hand' src='../images/select_prod.gif' border=0 onClick="SaleProdSingleSelect('form1','srcVar=wzdjid_<%=i%>&srcVar=cpid_<%=i%>&storeid='+form1.storeid.value+'&dwtxid='+form1.dwtxid.value,'fieldVar=wzdjid&fieldVar=cpid','','sumitForm(<%=b_SendBillBean.DETAIL_CHANGE%>,<%=i%>)')"--%>
                           <img style='cursor:hand' src='../images/select_prod.gif' border=0 onClick="SaleProdSingleSelect('form1','srcVar=wzdjid_<%=i%>&srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=xsj_<%=i%>&srcVar=jldw_<%=i%>&storeid='+form1.storeid.value+'&dwtxid='+form1.dwtxid.value,'fieldVar=wzdjid&fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=lsj&fieldVar=jldw','','sumitForm(<%=b_SendBillBean.MATERIAL_ON_CHANGE%>,<%=i%>)')">
                          <%}%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <input name="image" class="img" type="image" title="复制当前行" onClick="sumitForm(<%=b_SendBillBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <%} %> <%--if(!zt.equals("0")){%><input name="image" class="img" type="image" title="合格证打印" onClick='certifyCardPrint(form1.cpid_<%=i%>.value,form1.xsj_<%=i%>.value)' src="../images/print.gif" border="0"><%} --%>
                        </td>

                        <td class="td" nowrap>
                           <input type="hidden" name="zxh"  value='<%=detail.get("zxh")%>' >
                           <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=hsbl%>'>
                           <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                           <input type='hidden' id='hthwid_<%=i%>' name='hthwid_<%=i%>' value='<%=detail.get("hthwid")%>'>
                           <input type='hidden' id='cpid_<%=i%>' name='cpid_<%=i%>' value='<%=detail.get("cpid")%>'>
                           <input type="hidden" id="wzdjid_<%=i%>" name="wzdjid_<%=i%>" value='<%=detail.get("wzdjid")%>' >
                           <input type="text" style="width:80" name="htbh" class='ednone' onKeyDown="return getNextElement();"  value='<%=saleOrderRow.get("htbh")%>'  readonly>
                        </td><!--合同编号-->
                        <td class="td" nowrap><input type="text"  <%=prodClass%>  onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=productRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=prodreanonly%>></td><!--产品编码(存货代码)--->
                        <td class="td" nowrap><input type="text"  <%=prodClass%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=productRow.get("product")%>'  onchange="productNameSelect(this,<%=i%>)"   <%=prodreanonly%>></td><!--品名 规格(存货名称与规格)--->

                        <td class="td" nowrap>
                        <input type="text"  <%=prodClass%>  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="propertiesInput(this,form1.cpid_<%=i%>.value,<%=i%>)"  <%=prodreanonly%> >
                        <input type="HIDDEN"  id="dmsxid_<%=i%>"  name="dmsxid_<%=i%>"  value='<%=detail.get("dmsxid")%>' <%=bh?"":"readonly"%> >
                      <%--  <%if(canedt){%>--%>
                       <%if(!isEnd&&!isprops.equals("0")){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if('<%=detail.get("cpid")%>'==''){alert('请先输入产品');return;}if('<%=isprops%>'=='0'){alert('该产品无规格属性!');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>','<%=cpid%>','sumitForm(<%=b_SendBillBean.DETAIL_CHANGE%>,<%=i%>)')"><%}%>
                        <%if(!isEnd&&!isprops.equals("0")){%>
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>

                        <td class="td" nowrap><input type="text"  class="ednone_r"  onKeyDown="return getNextElement();" id="kdsl_<%=i%>" name="kdsl_<%=i%>" value='<%=detail.get("kdsl")%>'   readonly></td><!--订货量-->
                        <%--数量说明：isyrdd==true是否引入订单，wkdsl==0表示第一次引用此订单的此种货物，所以要以给出全部数量sl  --%>
                        <td class="td" nowrap><input type="text"  <%=djandjjeClass%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>'  onchange="sl_onchange(<%=i%>, false)" <%=canedt?"":" readonly"%>></td><!--数量-->

                        <td class="td" nowrap><input type='text' class="ednone" id='jldw_<%=i%>' name='jldw_<%=i%>' value='<%=productRow.get("jldw")%>'></td><!--计量单位--->
                       <%-- <td class="td" nowrap><input type="text"  <%=djandjjeClass%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' size='<%=list.getColumn("hssl").getPrecision()-6%>' onchange="sl_onchange(<%=i%>, true)"  <%=canedt?"":" readonly"%>></td><!--换算数量-->
                        <td class="td" nowrap><input type='text' class="ednone" size=10 value='<%=productRow.get("hsdw")%>'></td>--%>
                        <input type="hidden"  name="hssl_<%=i%>" value='<%=detail.get("hssl")%>'  readonly>
                        <input type="hidden"   name="hsdw_<%=i%>" value='<%=productRow.get("hsdw")%>'  readonly>
                        <td class="td" nowrap><input type="text" class='ednone_r'    onKeyDown="return getNextElement();" id="xsj_<%=i%>" name="xsj_<%=i%>" value='<%=detail.get("xsj")%>' readonly></td><!--销售价-->
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>   onKeyDown="return getNextElement();" id="zk_<%=i%>" name="zk_<%=i%>" value='<%=detail.get("zk")%>'  onchange="dj_onchange(<%=i%>, true)" <%=readonly%>></td><!--折扣-->
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>   onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' onchange="dj_onchange(<%=i%>, false)"  <%=readonly%> ></td><!--单价-->
                        <td class="td" nowrap><input type="text" class='ednone_r'  onKeyDown="return getNextElement();" id="jje_<%=i%>" name="jje_<%=i%>" value='<%=detail.get("jje")%>'  readonly></td><!--净金额-->
                        <td class="td" nowrap align="left"><input type="text"  <%=detailClass%>  onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>" value='<%=detail.get("bz")%>'   <%=readonly%> ></td><!--备注-->
                        <td class="td" nowrap align="right"><%=detail.get("stsl") %></td><!--出库数量-->
                        <td class="td" nowrap align="right"><%=wtsl %></td>
                        <td class="td" nowrap><input type="text" size=10 class=ednone_r onKeyDown="return getNextElement();" value='<%=kcl%>' readonly><!--库存量-->

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
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap> 合计 </td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap><input id="t_kdsl" name="t_kdsl" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_kdsl.toString(),loginBean.getSumFormat())%>' readonly></td>
                        <td class="td" nowrap><input id="t_sl" name="t_sl" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_sl.toString(),loginBean.getSumFormat())%>' readonly></td>
                        <%--<td class="td" nowrap><input id="t_xsje" name="t_xsje" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_xsje.toString(),loginBean.getPriceFormat())%>'  readonly></td>--%>
                        <td class="td">&nbsp;</td>
                       <%-- <td class="td" nowrap><input id="t_hssl" name="t_hssl" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_hssl.toString(),loginBean.getSumFormat())%>' readonly></td>
                        <td class="td">&nbsp;</td>--%>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap><input id="t_jje" name="t_jje" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_jje.toString(),loginBean.getPriceFormat())%>'  readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
                    <SCRIPT LANGUAGE="javascript">rowinfo = new RowControl();
                      <%for(int k=0; k<i; k++)
                        {
                         out.print("AddRowItem(rowinfo,'rowinfo_"+k+"');");
                       }%>
                      AddRowItem(rowinfo,'rowinfo_end');
                      InitRowControl(rowinfo);
                      initDefaultTableRow('tableview1',1);
                    </SCRIPT>
                  </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle">备注</td><%--其他信息--%>
                  <td colspan="7" noWrap class="td"><textarea name="bz" rows="3" onKeyDown="return getNextElement();" style="width:690" <%=(zt.equals("9")||zt.equals("8"))?"readonly":""%> ><%=masterRow.get("bz")%></textarea></td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>

 <tr>
  <td>
    <%--
  <table cellspacing=0 width="100%" cellpadding=0>
     <tr>
     <td nowrap>
     <div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">承运情况</a></div>
     </td>
     <td nowrap>
     <div id="tabDivINFO_EX_1" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_1');return false;">其他费用</a></div>
     </td>
     <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a>
     </td>
     </tr>
  </table>


<div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:760;height:140;overflow-y:auto;overflow-x:auto;">
<table id="tableview2" width="745" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
   <tr class="tableTitle">
     <td nowrap width=10></td>
     <td height='20' align="center" nowrap width=30>
      <%
        boolean cyend = zt.equals("8")||zt.equals("9");
        String cyClass = cyend ? "class=ednone" : "class=edbox";
        String cyClass_r = cyend ? "class=ednone_r" : "class=edFocused_r";
        String cyreadonly = cyend?"readonly":"";
       if(!cyend&&detailRows.length>0&&czyid.equals(loginid)){
       %>
      <%if(b_SendBillBean.canOperate){%>
      <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=b_SendBillBean.CYQK_ADD%>)" src="../images/add_big.gif" border="0">
      <%}%>
      <%}%>
     </td>
     <td nowrap>类型</td>
     <td nowrap>承运商</td>
     <td nowrap>起始地</td>
     <td nowrap>目的地</td>
     <td nowrap>摘要</td>
     <td nowrap>费用</td>
   </tr>
   <%
     int k = 0;
     int cyrowcount = cyqkRows.length;
     RowMap cyrow = null;
     for(;k<cyrowcount;k++)
     {
       cyrow = cyqkRows[k];
       RowMap dwpRow =corpBean.getLookupRow(cyrow.get("dwtxId"));
   %>
   <tr id="rowinfo_<%=k%>">
     <td class="td" nowrap><%=k+1%></td>
     <td class="td" align="center" nowrap>
     <%if(cyrowcount>0&&!cyrow.get("dwtxid").equals("")){%><input name="image32" class="img" type="image" title="打印承运单据" onClick="location.href='../pub/pdfprint.jsp?code=shyu_sendlist_ty_print&operate=<%=Operate.PRINT_BILL%>&a$tdcyqkid=<%=cyrow.get("tdcyqkid")%>&src=../sale_xixing/send_list_edit.jsp'" src="../images/print.gif" border="0"><%}%>
     <%if(!zt.equals("8")&&czyid.equals(loginid)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_SendBillBean.CYQK_DEL%>,<%=k%>)" src="../images/delete.gif" border="0"><%}%>
     </td>
     <td class="td" nowrap>
     <%
       String lx = cyrow.get("lx");
       if(cyend){
        out.print("<input  name='lx_'"+k+" type='hidden' value='"+cyrow.get("lx")+"' style='width:110' class='edline' readonly>");
        out.print(lx.equals("")?"":(lx.equals("1")?"托运单":(lx.equals("2")?"中途运送单":"搬运单")));
       }else{
     %>
     <pc:select name='<%="lx_"+k%>' style="width:80" addNull="1" value="<%=lx%>"  >
     <pc:option value="1">托运单</pc:option> <pc:option value="2">中途运送单</pc:option><pc:option value="3">搬运单</pc:option>
     </pc:select>
     <%}%>
     </td>
    <td noWrap class="td" >
      <input type="hidden" name="dwtxid_<%=k%>" value='<%=cyrow.get("dwtxid")%>'>
      <input type="text" <%=cyClass%> <%=cyreadonly%>  style="width:70" onKeyDown="return getNextElement();" name="dwdm_<%=k%>" value='<%=dwpRow.get("dwdm")%>' onchange="TransportDmChange(this,<%=k%>)" >
      <input type="text" <%=cyClass%> <%=cyreadonly%>  name="dwmc_<%=k%>" onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(cyrow.get("dwtxId"))%>' style="width:200"   onchange="TransportName(this,<%=k%>)">
      <%if(!cyend){%>
      <img style='cursor:hand' src='../images/view.gif' border=0 onClick="TransportSingleSelect('form1','srcVar=dwtxid_<%=k%>&srcVar=dwdm_<%=k%>&srcVar=dwmc_<%=k%>','fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc')">
      <%}%>
     </td>
     <td class="td" nowrap><input type="text" name="qsd_<%=k%>" value='<%=cyrow.get("qsd")%>' maxlength='32' style="width:70"  <%=cyClass%> <%=cyreadonly%>  ></td>
     <td class="td" nowrap><input type="text" name="mdd_<%=k%>" value='<%=cyrow.get("mdd")%>' maxlength='32' style="width:70"  <%=cyClass%>  <%=cyreadonly%> ></td>
     <td class="td" nowrap><input type="text" name="zy_<%=k%>"  value='<%=cyrow.get("zy")%>' maxlength='32' style="width:100"  <%=cyClass%>  <%=cyreadonly%> ></td>
     <td class="td" nowrap><input type="text" name="fy_<%=k%>"  value='<%=cyrow.get("fy")%>' maxlength='32' style="width:60"  <%=cyClass_r%> <%=cyreadonly%>  ></td>
   </tr>
   <%
     }
     for(; k < 4; k++){
   %>
     <tr id="rowinfo_<%=k%>">
       <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
       <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
       <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
     </tr>
   <%}%>
 </table>
 <script language="javascript">initDefaultTableRow('tableview2',1);</script>
</div>
<div id="cntDivINFO_EX_1" class="tabContent" style="display:none;width:760;height:140;overflow-y:auto;overflow-x:hidden;">
 <center>
   <table id="tableview3" width="745" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
     <tr class="tableTitle">
       <td nowrap width=10></td>
     <td height='20' align="center" nowrap width=30>
      <%
       if(!cyend&&detailRows.length>0&&czyid.equals(loginid)){
      if(b_SendBillBean.canOperate){
      %>
      <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=b_SendBillBean.QTFY_ADD%>)" src="../images/add_big.gif" border="0">
      <pc:shortcut key="a" script="sumitForm(<%=Operate.QTFY_ADD%>)" />
      <%}%>
      <%}%>
     </td>
       <td nowrap>类型</td>
       <td nowrap>费用</td>
       <td nowrap>摘要</td>
     </tr>
   <%
     int n = 0;
     int qtrowcount = qtfyRows.length;
     RowMap qtrow = null;
     for(;n<qtrowcount;n++)
     {
       qtrow = qtfyRows[n];

   %>
     <tr id="rowinfo_<%=n%>">
       <td class="td"><%=n+1%></td>
       <td class="td" nowrap>
        <%if(!zt.equals("8")&&czyid.equals(loginid)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_SendBillBean.QTFY_DEL%>,<%=n%>)" src="../images/delete.gif" border="0"><%}%>
       </td>
       <td class="td" nowrap><input type="text" name="fylx_<%=n%>" value='<%=qtrow.get("fylx")%>' maxlength='100' style="width:100%"   <%=cyClass%> <%=cyreadonly%>  ></td>
       <td class="td" nowrap><input type="text" name="qtfy_<%=n%>"  value='<%=qtrow.get("fy")%>' maxlength='100' style="width:100%"    <%=cyClass%> <%=cyreadonly%>  ></td>
       <td class="td" nowrap><input type="text" name="qtzy_<%=n%>"  value='<%=qtrow.get("zy")%>' maxlength='100' style="width:100%"    <%=cyClass%> <%=cyreadonly%> ></td>
     </tr>
     <%
     }
       for(; n < 4; n++){
     %>
       <tr id="rowinfo_<%=n%>">
         <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
         <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
       </tr>
   <%}%>
   </table>
   <script language="javascript">initDefaultTableRow('tableview3',1);</script>
 </center>
</div>
</center>
</div>
<div id="cntDivINFO_EX_9" class="tabContent" style="display:none;width:750;height:110;overflow-y:auto;overflow-x:hidden;"></div>


     --%>
<SCRIPT LANGUAGE="javascript">
  INFO_EX = new TabControl('INFO_EX',0);
  AddTabItem(INFO_EX,'INFO_EX_0','tabDivINFO_EX_0','cntDivINFO_EX_0');
  AddTabItem(INFO_EX,'INFO_EX_1','tabDivINFO_EX_1','cntDivINFO_EX_1');
 if (window.top.StatFrame+''!='undefined')
 {
  var tmp_curtab=window.top.StatFrame.GetRegisterVar('INFO_EX');
  if (tmp_curtab!='')
   {
     SetActiveTab(INFO_EX,tmp_curtab);
   }
 }
</SCRIPT>
   <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760"></table>
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
             <input type="hidden" name="importOrder" value="引入订单" onchange="sumitForm(<%=b_SendBillBean.DETAIL_SALE_ADD%>,-1)">
             <input name="btnback" style="width:80" class="button" type="button" value="引入订单(W)" onClick="checkdwst()" border="0">
             <pc:shortcut key="w" script="checkdwst()" />
             <%--input type="hidden" name="importOrderproduct" value="引入订单货物" onchange="sumitForm(<%=b_SendBillBean.PRODUCT_ADD%>,-1)">
             <input name="btnback" style="width:100" class="button" type="button" value="引入订单货物(E)" onClick="checkdwtxid()" border="0"--%>
             <pc:shortcut key="e" script="checkdwtxid()" />
             <%
               }if(!isEnd){
               String retun = "sumitForm("+Operate.POST_CONTINUE+")";
              %>
              <input name="button2"  style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存新增(N)">
              <pc:shortcut key="n" script="<%=retun%>" />
              <%
               }
               if(zt.equals("0")&&czyid.equals(loginid))
               {
                 if(!b_SendBillBean.isReport){
                   String post ="sumitForm("+Operate.POST+");";
               %>
              <input name="btnback" style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
              <pc:shortcut key="s" script="<%=post%>" />
               <%}
               }if(zt.equals("1")||zt.equals("2")){
                 if(!b_SendBillBean.isReport/*czyid.equals(loginid)*/){
                   String appost ="sumitForm("+b_SendBillBean.APPROVED_MASTER_ADD+");";
             %>
              <input name="btnback" style="width:80" type="button" class="button" onClick="sumitForm(<%=b_SendBillBean.APPROVED_MASTER_ADD%>);" value="保存(S)">
              <pc:shortcut key="s" script="<%=appost%>" />
              <%
              }}if(isCanDelete&&!b_SendBillBean.isReport&&czyid.equals(loginid)){
               String del = "if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+");";
               String detNull = "sumitForm("+b_SendBillBean.DEL_NULL+");";
              %>
              <input name="button3"  style="width:110" type="button" class="button" title="删除空行(R)" value="删除数量为空行(R)" onClick="<%=detNull%>">
              <pc:shortcut key="r" script="<%=detNull%>" />
              <input name="button3"  style="width:60" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value="删除(D)">
              <pc:shortcut key="d" script="<%=del%>" />
              <%
              }if(!b_SendBillBean.isReport){
              %>
               <%if(zt.equals("1")||zt.equals("2")||zt.equals("8")){%>
              <input type="hidden" class="button" value="打包单(P)" onclick="location.href='../pub/pdfprint.jsp?code=shyu_sendlist_package_print&operate=<%=Operate.PRINT_BILL%>&a$tdid=<%=masterRow.get("tdid")%>&src=../sale_xixing/send_list_edit.jsp'">
              <%}%>
              <%if(zt.equals("8")){%>
              <input type="button" class="button" value="打印(P)" onclick="location.href='../pub/pdfprint.jsp?code=shyu_sendlist_edit_print&operate=<%=Operate.PRINT_BILL%>&a$tdid=<%=masterRow.get("tdid")%>&src=../sale_xixing/send_list_edit.jsp'">
              <%}%>
              <input name="btnback"  style="width:50" type="button" class="button" onClick="sumitForm(<%=b_SendBillBean.BACK%>)" value="返回(C)">
              <pc:shortcut key="c" script="backList();" /><%}%>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</form>
<script language="javascript">

  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
  <%=b_SendBillBean.adjustInputSize(new String[]{"cpbm","product","kdsl","sl","jldw","xsj","zk", "dj","jje","bz"}, "form1", detailRows.length)%>
    function sl_onchange(i, isBigUnit)
    {
      var oldhsblObj = document.all['hsbl_'+i];
      var sxzObj = document.all['sxz_'+i];
      unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'nsl_onchange('+i+','+isBigUnit+')');
     }
  function nsl_onchange(i, isBigUnit)
  {
    var slObj = document.all['sl_'+i];
    var kdslObj = document.all['kdsl_'+i];
    var xsjObj = document.all['xsj_'+i];
    var djObj = document.all['dj_'+i];
    var zkObj = document.all['zk_'+i];
    var jjeObj = document.all['jje_'+i];
    var hsblObj = document.all['truebl_'+i];
    var hsslObj = document.all['hssl_'+i];

    var obj = isBigUnit ? hsslObj : slObj;
    var showText = isBigUnit ? "输入的换算数量非法！" : "输入的数量非法！";//显示错误信息
    var changeObj = isBigUnit ? slObj : hsslObj;//所调用的控件
    if(obj.value=="")
      return;
    if(obj.value.trim()=="")
      return;
    if(isNaN(obj.value)){
      alert(showText);
      obj.focus();
      return;
    }
    if(!isNaN(parseFloat(xsjObj.value)*parseFloat(zkObj.value)*0.01))
      djObj.value= parseFloat(xsjObj.value)*parseFloat(zkObj.value)*0.01;
    else
      djObj.value=0;

    <%if(!isEnd){%>
      kdslObj.value=slObj.value;
    <%}%>

    if(!hsblObj.value=="")
  {
    <%
    if(mustConversion){
    //要强制转换
    %>
    changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
    <%}else {%>
      if(hsslObj.value==""||slObj.value=="")
        changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
      <%}%>
  }
    if(djObj.value!="" && !isNaN(djObj.value))
      jjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(djObj.value));

    cal_tot('sl');
    <%if(!isEnd){%>
      cal_tot('kdsl');
    <%}%>
    cal_tot('jje');
    //cal_tot('hssl');
  }
  function dj_onchange(i, isRebate)
  {
    var slObj = document.all['sl_'+i];
    var zkObj = document.all['zk_'+i];
    var xsjObj = document.all['xsj_'+i];
    var djObj = document.all['dj_'+i];
    var jjeObj = document.all['jje_'+i];

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
    if(parseFloat(xsjObj.value)==0)
      changeObj.value = formatQty(isRebate ? (parseFloat(xsjObj.value)*parseFloat(zkObj.value)/100) : 0);
    else
      changeObj.value = formatQty(isRebate ? (parseFloat(xsjObj.value)*parseFloat(zkObj.value)/100) : (parseFloat(djObj.value)/parseFloat(xsjObj.value)*100));
    if(isNaN(changeObj.value))
      changeObj.value = "";
    if(slObj.value!="" && !isNaN(slObj.value)&&!isNaN(djObj.value)&&djObj.value!="")
      jjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(djObj.value));
    cal_tot('jje');
  }
  function cal_tot(type)
  {
    var tmpObj;
    var tot=0;
    for(i=0; i<<%=detailRows.length%>; i++)
    {
      if(type == 'sl')
        tmpObj = document.all['sl_'+i];
      else if(type == 'kdsl')
        tmpObj = document.all['kdsl_'+i];
      else if(type == 'jje')
        tmpObj = document.all['jje_'+i];
      else
        return;
      if(tmpObj.value!="" && !isNaN(tmpObj.value))
        tot += parseFloat(tmpObj.value);
    }
    if(type == 'sl')
      document.all['t_sl'].value = formatQty(tot);
    else if(type == 'kdsl')
      document.all['t_kdsl'].value = formatPrice(tot);
    else if(type == 'jje')
      document.all['t_jje'].value = formatPrice(tot);
  }


 function prodsMultiSelect(frmName, srcVar)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=500 height=400 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "../sale_xixing/import_apply_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
 function goodsMultiSelect(frmName, srcVar, storeid)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "../sale/lading_sel_product.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&curID="+storeid;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
function OrderSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
  {
   var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=800 height=600  top=0 left=0";
   var winName= "SingleOrderSelector";
   paraStr = "../sale/import_order.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+curID;
   if(methodName+'' != 'undefined')
     paraStr += "&method="+methodName;
   if(notin+'' != 'undefined')
     paraStr += "&notin="+notin;
   newWin =window.open(paraStr,winName,winopt);
   newWin.focus();
  }
function propertiesInput(obj,cpid,i)
  {
    PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i, 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value, 'sumitForm(<%=b_SendBillBean.DETAIL_CHANGE%>,'+i+')');
  }
function selctorder()
  {
  if(form1.dwtxid.value=='')
  {
    alert('请选择购货单位');
    return;
  }
  if(form1.storeid.value=='')
  {
    alert('请选择仓库');
    return;
  }
  form1.selectedhtid.value='';
    OrderSingleSelect('form1','srcVar=selectedhtid','fieldVar=htid','&dwtxid='+form1.dwtxid.value+'&djlx='+form1.djlx.value+'&storeid='+form1.storeid.value,"sumitForm(<%=b_SendBillBean.IMPORT_ORDER%>,-1)");
  }
function checkdwst()
  {
  if(form1.storeid.value=='')
  {
    alert('请选择仓库');
    return;
  }
  OrderMultiSelect('form1','srcVar=importOrder&dwtxid='+form1.dwtxid.value+'&khlx='+form1.khlx.value+'&storeid='+form1.storeid.value+'&djlx='+form1.djlx.value+'&personid='+form1.personid.value+'&jsfsid='+form1.jsfsid.value+'&sendmodeid='+form1.sendmodeid.value)
}
function OrderMultiSelect(frmName, srcVar, methodName,notin)
{
     var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
     var winName= "OrdersSelector";
     paraStr = "../sale_xixing/import_order.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
     if(methodName+'' != 'undefined')
       paraStr += "&method="+methodName;
     if(notin+'' != 'undefined')
     paraStr += "&notin="+notin;
     openSelectUrl(paraStr, winName, winopt2);
}
function checkdwtxid()
{
  if(form1.dwtxid.value=='')
  {
    alert('请选择购货单位');
    return;
  }
  if(form1.storeid.value=='')
  {
    alert('请选择仓库');
    return;
  }
  if(form1.personid.value=='')
  {
    alert('请选择业务员');
    return;
  }
  if(form1.jsfsid.value=='')
  {
    alert('请选择结算方式');
    return;
  }
  MultiSelectOrder('form1','srcVar=importOrderproduct&dwtxid='+form1.dwtxid.value+'&storeid='+form1.storeid.value+'&djlx=1&personid='+form1.personid.value+'&jsfsid='+form1.jsfsid.value)
}
function MultiSelectOrder(frmName, srcVar, methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
  var winName= "OrdersSelector";
  paraStr = "../sale_xixing/select_order_product.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}

//产品编码
function productCodeSelect(obj, i)
{
  SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=wzdjid_'+i+'&srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=xsj_'+i+'&srcVar=jldw_'+i,'fieldVar=wzdjid&fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=lsj&fieldVar=jldw&storeid='+form1.storeid.value,obj.value+'&dwtxid='+form1.dwtxid.value,'sumitForm(<%=b_SendBillBean.MATERIAL_ON_CHANGE%>,'+i+')');
}
//产品名称
function productNameSelect(obj, i)
{
  SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=wzdjid_'+i+'&srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=xsj_'+i+'&srcVar=jldw_'+i,'fieldVar=wzdjid&fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=lsj&fieldVar=jldw&storeid='+form1.storeid.value,obj.value+'&dwtxid='+form1.dwtxid.value,'sumitForm(<%=b_SendBillBean.MATERIAL_ON_CHANGE%>,'+i+')');
}
function priceToNull(i)
{
  document.all['sl_'+i].value='';
  document.all['hssl_'+i].value='';
  document.all['dj_'+i].value='';
  document.all['jje_'+i].value='';

}
</script>
<%//&#$
if(b_SendBillBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>

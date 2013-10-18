<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%>
<%
  engine.erp.sale.B_LadingBill b_ladingbillBean = engine.erp.sale.B_LadingBill.getInstance(request);
  String pageCode = "lading_bill";

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
  location.href='lading_bill.jsp';
}
//打印
function ladingprint(tdid)
{
  location.href='../pub/pdfprint.jsp?operate=<%=Operate.PRINT_PRECISION%>&code=xs_lading_bill_print&tdid='+tdid;
}
//输入购货单位代码
function customerCodeSelect(obj)
{
  if(form1.tdrq.value=='')
  {
    alert('请输入提单日期!');
    return;
  }
  else
  {
    if(!checkDate(form1.tdrq))
      return;
  }
  CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=lxr&srcVar=dh','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=lxr&fieldVar=tel',obj.value,'sumitForm(<%=b_ladingbillBean.DWTXID_CHANGE%>,-1)');
}
//选择购货单位
function selectCustomer()
{
  if(form1.tdrq.value=='')
  {
    alert('请输入提单日期!');
    return;
  }
  else
  {
    if(!checkDate(form1.tdrq))
      return;
  }
CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=lxr&srcVar=dh','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=lxr&fieldVar=tel',form1.dwtxid.value,'sumitForm(<%=b_ladingbillBean.DWTXID_CHANGE%>,-1)');
}
//输入购货单位名称
function customerNameSelect(obj)
{
  if(form1.tdrq.value=='')
  {
    alert('请输入提单日期!');
    return;
  }
  else
  {
    if(!checkDate(form1.tdrq))
      return;
  }
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=lxr&srcVar=dh','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=lxr&fieldVar=tel',obj.value,'sumitForm(<%=b_ladingbillBean.DWTXID_CHANGE%>,-1)');
}
//提单日期变化--引起回款日期变化
function tdrqOnchange()
{
  checkDate(document.form1.tdrq);
  if(document.form1.hkts.value=="")
    return;
  else
  {
    document.form1.hkrq.value=addDate(document.form1.tdrq.value,parseFloat(document.form1.hkts.value));
  }
}
function selectTdrq()
{
  selectDate(document.form1.tdrq);
  if(document.form1.hkts.value=="")
    return;
  else
  {
    document.form1.hkrq.value=addDate(document.form1.tdrq.value,parseFloat(document.form1.hkts.value));
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
    document.form1.hkrq.value=addDate(document.form1.tdrq.value,parseFloat(document.form1.hkts.value));
}
//引起回款日期变化--回款天数变化
function hkrqOnchange()
{
  var td = document.form1.tdrq.value;
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
//运输单位
function TransportChange(obj)
{
    CustomerCodeChange('4',document.all['prod'], obj.form.name,'srcVar=dwdm2&srcVar=dwt_dwtxId&srcVar=dwmc2','fieldVar=dwdm&fieldVar=dwtxId&fieldVar=dwmc',obj.value);
}
function TransportNameChange(obj)
{
  CustomerNameChange('4',document.all['prod'], obj.form.name,'srcVar=dwdm2&srcVar=dwt_dwtxId&srcVar=dwmc2','fieldVar=dwdm&fieldVar=dwtxId&fieldVar=dwmc',obj.value);
}
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
if(form1.khlx.value=='')
{
alert('请选择客户类型!');
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
sumitForm(<%=Operate.DETAIL_ADD%>);
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
</script>
<%
  String retu = b_ladingbillBean.doService(request, response);
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
  engine.project.LookUp salePriceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_PRICE);
  engine.project.LookUp sendModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SEND_MODE);//发货方式

  //engine.project.LookUp salePriceBean = b_ladingbillBean.getSalePriceBean(request);//销售价
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_ladingbillBean.getMaterTable();
  EngineDataSet list = b_ladingbillBean.getDetailTable();
  HtmlTableProducer masterProducer = b_ladingbillBean.masterProducer;
  HtmlTableProducer detailProducer = b_ladingbillBean.detailProducer;

  creditBean.regData(ds,"dwtxid");
  RowMap masterRow = b_ladingbillBean.getMasterRowinfo();//主表一行
  RowMap[] detailRows= b_ladingbillBean.getDetailRowinfos();//从表多行
  String zt=masterRow.get("zt");//0.初始化,1.已开提单(已经审核),4.作废,9.出库
  if(b_ladingbillBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  String djlx=masterRow.get("djlx");
  sendModeBean.regData(ds, "sendmodeid");
  boolean isEnd =  b_ladingbillBean.isReport||b_ladingbillBean.isApprove || (!b_ladingbillBean.masterIsAdd() && !zt.equals("0"));
  //没有结束,在修改状态,并有删除权限
  boolean isCanDelete = !isEnd && !b_ladingbillBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);
  isEnd = isEnd || !(b_ladingbillBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  //FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=edline" : "class=edFocused";
  String prodClass = detailClass;
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  String prodreanonly = readonly;
  String title = zt.equals("0") ? ("未审批") : ((zt.equals("9") ? "审批中" :(zt.equals("4")?"已作废":(zt.equals("8")?"完成":"已审"))) );
  String tdlx=djlx.equals("1")?"提货单":"退货单";
  boolean count=list.getRowCount()==0?true:false;
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
  RowMap dwpRow =corpBean.getLookupRow(masterRow.get("dwt_dwtxId"));
  boolean canedt = zt.equals("0")||zt.equals("1")||zt.equals("2");
  String djandjjeClass = canedt ?"class=edFocused_r": "class=ednone_r" ;
  String dwtxClass = count? "class=edbox":"class=edline";
  String dwtxRead = count? " " : "readonly";
  boolean mustConversion = b_ladingbillBean.conversion;//是否需要强制转换
  String djhclass = zt.equals("2")?"class=edbox":"class=edline";
  String djhreadonly = zt.equals("2")?"":"readonly";
%>
<BODY id="docbody" oncontextmenu="window.event.returnValue=true"  onLoad="syncParentDiv('tableview1');">
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
            <td class="activeVTab">提货单(<%=title%>)</td>
          </tr>
        </table>
  <table class="editformbox"  width="100%" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0" width="100%">
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("tdrq").getFieldname()%></td>
                  <td  noWrap class="td">
                  <input type="text" name="tdrq" value='<%=masterRow.get("tdrq")%>' maxlength='10' style="width:110" <%=edClass%> onChange="tdrqOnchange()"  >
                  <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectTdrq()"></a><%}%>
                  </td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("tdbh").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="tdbh" value='<%=masterRow.get("tdbh")%>' maxlength='<%=ds.getColumn("tdbh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                 <td noWrap class="tdTitle">部门</td>
                 <td noWrap class="td"><%---为部门添加触发事件--%>
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
                    <pc:select name="personid" style="width:110">
                      <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                 </td>

        </tr>
        <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwtxid").getFieldname()%></td><%--购货单位--%>
                  <td colspan="3" noWrap class="td">
                    <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text" <%=dwtxClass%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="customerCodeSelect(this)" <%=dwtxRead%>>
                    <input type="text" <%=dwtxClass%> name="dwmc" onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:200"   onchange="customerNameSelect(this)"  <%=dwtxRead%>>
                    <%if(count){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="selectCustomer()">
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">地址</td>
                  <td colspan="3" noWrap class="td">
                    <input type="text" <%=dwtxClass%> name="dz" onKeyDown="return getNextElement();" value='<%=masterRow.get("dz")%>' style="width:280"   <%=dwtxRead%>>
                  </td>
          </tr>
          <tr>
                  <td noWrap class="tdTitle">联系人</td>
                  <td noWrap class="td">
                    <input type="text" <%=dwtxClass%> name="lxr" onKeyDown="return getNextElement();" value='<%=masterRow.get("lxr")%>' style="width:110"   <%=dwtxRead%>>
                  </td>
                  <td noWrap class="tdTitle">电话</td>
                  <td  noWrap class="td">
                    <input type="text" <%=dwtxClass%> name="dh" onKeyDown="return getNextElement();" value='<%=masterRow.get("dh")%>' style="width:110"   <%=dwtxRead%>>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("hkts").getFieldname()%></td>
                  <td noWrap class="td">
                  <input type="text" name="hkts" value='<%=masterRow.get("hkts")%>' maxlength='<%=ds.getColumn("hkts").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" onchange="hktsOnchange()" <%=readonly%>>
                  </td>
                  <td  noWrap class="tdTitle">结算方式</td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='hidden' name='jsfsid' value='"+masterRow.get("jsfsid")+"'><input type='text'   value='"+balanceModeBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="jsfsid"  addNull="1"  style="width:110">
                    <%=balanceModeBean.getList(masterRow.get("jsfsid"))%>
                    </pc:select>
                    <%}%></td>

      </tr>
      <tr>
                <td   noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                <td  noWrap class="td">
                  <%if(isEnd||!count) out.print("<input type='hidden' name='storeid' value='"+masterRow.get("storeid")+"'><input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                  else {%>
                  <pc:select name="storeid" addNull="1" style="width:110">
                    <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle">客户类型</td>
                  <td width="120" class="td">
                  <%
                   // String t="sumitForm("+saleOrderBean.KHLX_CHANGE+",-1)";//saleOrderBean.DETAIL_CHANGE;
                    String khlx = masterRow.get("khlx");
                     if(isEnd||!count){
                     //out.print(khlx.equals("A")?"A":"C");
                     out.print("<input  name='khlx' type='text' value='"+masterRow.get("khlx")+"' style='width:110' class='edline' readonly>");
                    }else{%>
                  <pc:select name="khlx" style="width:80" addNull="1" value="<%=khlx%>"  >
                    <pc:option value="A">A</pc:option> <pc:option value="C">C</pc:option>
                  </pc:select>
                  <%}%>
                 </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("hkrq").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="hkrq" value='<%=masterRow.get("hkrq")%>' maxlength='10' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" onChange="checkDate(this);hkrqOnchange()">
                  <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.hkrq);hkrqOnchange()"></a><%}%></td>

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
                <td   noWrap class="tdTitle">运费单价(元/公斤)</td>
                <td   noWrap class="td">
                  <%
                    String yfdj = masterRow.get("yfdj");
                     if(isEnd){
                     //out.print(khlx.equals("A")?"A":"C");
                     out.print("<input  name='yfdj' type='text' value='"+masterRow.get("yfdj")+"' style='width:110' class='edline' readonly>");
                    }else{%>
                   <input type="text" name="yfdj" value='<%=masterRow.get("yfdj")%>' maxlength='10' style="width:110" <%=edClass%> onChange="yfdjonchange()" onKeyDown="return getNextElement();"<%=readonly%>>
                 <%}%>
                </td>
                <td   noWrap class="tdTitle"><%=masterProducer.getFieldInfo("yf").getFieldname()%></td>
                <td   noWrap class="td">
                  <%
                    String yf = masterRow.get("yf");
                     if(isEnd||!count){
                     //out.print(khlx.equals("A")?"A":"C");
                     out.print("<input  name='yf' type='text' value='"+masterRow.get("yf")+"' style='width:110' class='edline' readonly>");
                    }else{%>
                    <input type="text" name="yf" value='<%=masterRow.get("yf")%>' maxlength='10' style="width:110" <%=edClass%>  onKeyDown="return getNextElement();"<%=readonly%>>
                <%}%>
               </td>
                <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("ddfy").getFieldname()%></td>
                <td   noWrap class="td"><input type="text" name="ddfy" value='<%=masterRow.get("ddfy")%>' maxlength='10' style="width:110" <%=djandjjeClass%>  onKeyDown="return getNextElement();"  <%=canedt?"":" readonly"%>></td>
                <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("djh").getFieldname()%></td>
                <td   noWrap class="td"><input type="text" name="djh" value='<%=masterRow.get("djh")%>' maxlength='10' style="width:110" <%=djhclass%>  onKeyDown="return getNextElement();" <%=djhreadonly%>></td>
       </tr>
       <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwt_dwtxId").getFieldname()%></td>
                  <td noWrap class="td" colspan="3">
                    <input type="hidden" name="dwt_dwtxId" value='<%=masterRow.get("dwt_dwtxId")%>'>
                    <input type="text" <%=detailClass%> style="width:70" onKeyDown="return getNextElement();" name="dwdm2" value='<%=dwpRow.get("dwdm")%>' onchange="TransportChange(this)" <%=readonly%>>
                    <input type="text" <%=detailClass%> name="dwmc2" onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(masterRow.get("dwt_dwtxId"))%>' style="width:200"   onchange="TransportNameChange(this)" <%=readonly%>>
                    <%if(!isEnd){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="TransportSingleSelect('form1','srcVar=dwt_dwtxId&srcVar=dwdm2&srcVar=dwmc2','fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc',form1.dwt_dwtxId.value)">
                    <%}%>
                   </td>
       </tr>
       <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){%><%if(b_ladingbillBean.canOperate){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="detailAdd()" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script="detailAdd()" /><%}%>
                          <input class="edFocused_r"  name="tCopyNumber" value="1" title="拷贝(ALT+A)" size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                          <%}%>
                        </td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("hthwid").getFieldname()%></td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td height='20' nowrap>规格属性</td>
                        <td nowrap>换算数量</td>
                        <td height='20' nowrap>换算单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("xsj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("xsje").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("zk").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("dj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jje").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("ckrq").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sthssl").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("stsl").getFieldname()%></td>
                        <td nowrap>未出库数量</td>



                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%
                      propertyBean.regData(list,"dmsxid");

                      prodBean.regData(list,"cpid");
                      saleOrderBean.regData(list,"tdid");
                      salePriceBean.regData(list,"hthwid");
                      BigDecimal t_sl = new BigDecimal(0), t_xsje = new BigDecimal(0), t_jje = new BigDecimal(0),t_hssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String hssl = detail.get("hssl");
                        String sl = detail.get("sl");
                        String xsje = detail.get("xsje");
                        String jje = detail.get("jje");
                        String stsl = detail.get("stsl");
                        double dstsl = Double.parseDouble(stsl.equals("")?"0":stsl);
                        double dhssl=0;
                        double dsl=0;
                        double dxsje=0;
                        double djje=0;
                        String wtsl = "";
                        if(b_ladingbillBean.isDouble(hssl))
                        {
                          dhssl=Double.parseDouble(hssl);
                          if(masterRow.get("djlx").equals("-1")&&dhssl<0)
                          {
                            dhssl=-1*dhssl;
                          }
                          t_hssl = t_hssl.add(new BigDecimal(dhssl));
                        }
                        if(b_ladingbillBean.isDouble(sl))
                        {
                          dsl=Double.parseDouble(sl);
                          if(masterRow.get("djlx").equals("-1")&&dsl<0)
                          {
                            dsl=-1*dsl;
                          }
                          t_sl = t_sl.add(new BigDecimal(dsl));
                        }
                        if(b_ladingbillBean.isDouble(xsje))
                        {
                          dxsje=Double.parseDouble(xsje);
                          if(masterRow.get("djlx").equals("-1")&&dxsje<0)
                          {
                            dxsje=-1*dxsje;
                          }
                          t_xsje = t_xsje.add(new BigDecimal(dxsje));
                        }
                        if(b_ladingbillBean.isDouble(jje))
                        {
                          djje=Double.parseDouble(jje);
                          if(masterRow.get("djlx").equals("-1")&&djje<0)
                          {
                            djje=-1*djje;
                          }
                          t_jje = t_jje.add(new BigDecimal(djje));
                        }
                        if(!detail.get("hthwid").equals(""))
                          {
                          prodClass="class=ednone";
                          prodreanonly="readonly";
                          }else
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
                          <img style='cursor:hand' src='../images/select_prod.gif' border=0 onClick="if(form1.khlx.value==''){alert('请选择客户类型');return};form1.dmsxid_<%=i%>.value='';SaleProdSingleSelect('form1','srcVar=wzdjid_<%=i%>&srcVar=xsj_<%=i%>&srcVar=cpid_<%=i%>&storeid='+form1.storeid.value,'fieldVar=wzdjid&fieldVar=xsj&fieldVar=cpid','','sumitForm(<%=b_ladingbillBean.DETAIL_CHANGE%>,<%=i%>)')">
                          <%}%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <input name="image" class="img" type="image" title="复制当前行" onClick="sumitForm(<%=b_ladingbillBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <%}%></td>
                        <td class="td" nowrap>
                        <%
                          RowMap productRow = prodBean.getLookupRow(detail.get("cpid"));
                          String isprops=productRow.get("isprops");
                          String cpid = detail.get("cpid");
                          String hsbl = productRow.get("hsbl");
                          detail.put("hsbl", hsbl);
                          //RowMap prodRow = salePriceBean.getLookupRow(detail.get("wzdjid"));
                          RowMap  saleOrderRow= saleOrderBean.getLookupRow(detail.get("hthwid"));
                          boolean bh = detail.get("hthwid").equals("");
                          String instrstyle = bh?"class=edFocused_r": "class=ednone_r" ;
                        %>
                           <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=hsbl%>'>
                           <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                           <input type='hidden' id='hthwid_<%=i%>' name='hthwid_<%=i%>' value='<%=detail.get("hthwid")%>'>
                           <input type='hidden' id='cpid_<%=i%>' name='cpid_<%=i%>' value='<%=detail.get("cpid")%>'>
                        <input type="text"  style="width:80" class='ednone' onKeyDown="return getNextElement();" maxlength='13' value='<%=saleOrderRow.get("htbh")%>'  readonly>
                        </td><!--合同编号-->
                        <td class="td" nowrap><input type="text" <%=prodClass%>  onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=productRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=prodreanonly%>></td><!--产品编码(存货代码)--->
                        <td class="td" nowrap>
                          <input type="text" <%=prodClass%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=productRow.get("product")%>'  onchange="productNameSelect(this,<%=i%>)"   <%=prodreanonly%>></td>
                        </td><!--品名 规格(存货名称与规格)--->
                        <td class="td" nowrap>
                        <input type="text"  <%=djandjjeClass%>  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="propertiesInput(this,form1.cpid_<%=i%>.value,<%=i%>)"  <%=canedt?"":" readonly"%> >
                        <input type="HIDDEN"  id="dmsxid_<%=i%>"  name="dmsxid_<%=i%>"  value='<%=detail.get("dmsxid")%>' <%=bh?"":"readonly"%> >
                        <%if(canedt){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if('<%=detail.get("cpid")%>'==''){alert('请先输入产品');return;}if('<%=isprops%>'=='0'){alert('该产品无规格属性!');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>','<%=cpid%>')"><%}%>
                        <%if(!isEnd&&!isprops.equals("0")){%><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';"><%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=djandjjeClass%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)"  <%=canedt?"":" readonly"%>></td><!--换算数量--->
                        <td class="td" nowrap><input type="text" style="width:20" class='ednone_r'  onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=productRow.get("hsdw")%>'  readonly></td>
                        <td class="td" nowrap><input type="text" <%=djandjjeClass%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onblur="sl_onchange(<%=i%>, false)" <%=canedt?"":" readonly"%>></td><!--数量-->
                        <td class="td" nowrap><%=productRow.get("jldw")%></td><!--计量单位--->
                        <td class="td" nowrap><input type="text" class='ednone_r'   onKeyDown="return getNextElement();" id="xsj_<%=i%>" name="xsj_<%=i%>" value='<%=detail.get("xsj")%>' readonly></td><!--销售价-->
                        <td class="td" nowrap><input type="text" class='ednone_r'   onKeyDown="return getNextElement();" id="xsje_<%=i%>" name="xsje_<%=i%>" value='<%=String.valueOf(dxsje)%>' readonly></td><!--销售金额-->
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>   onKeyDown="return getNextElement();" id="zk_<%=i%>" name="zk_<%=i%>" value='<%=detail.get("zk")%>'  onchange="dj_onchange(<%=i%>, true)" <%=readonly%>></td><!--折扣-->
                        <td class="td" nowrap>
                            <input type="text" <%=detailClass_r%>   onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' onchange="dj_onchange(<%=i%>, false)"  <%=readonly%> >
                            <input type="HIDDEN" id="wzdjid_<%=i%>" name="wzdjid_<%=i%>" value='<%=detail.get("wzdjid")%>' >
                        </td><!--单价-->
                        <td class="td" nowrap><input type="text" class='ednone_r'  onKeyDown="return getNextElement();" id="jje_<%=i%>" name="jje_<%=i%>" value='<%=String.valueOf(djje)%>'  readonly></td><!--净金额-->
                        <td class="td" nowrap align="left"><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='100'  <%=readonly%> ></td><!--备注-->
                        <td class="td" nowrap align="right"><%=detail.get("ckrq") %></td>
                        <td class="td" nowrap align="right"><%=detail.get("sthssl") %></td><!--实提换算数量-->
                        <td class="td" nowrap align="right"><%=detail.get("stsl") %></td><!--出库数量-->
                        <td class="td" nowrap align="right"><%=wtsl %></td>

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
                        <td class="td" nowrap><input id="t_hssl" name="t_hssl" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_hssl.toString(),loginBean.getSumFormat())%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap><input id="t_sl" name="t_sl" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_sl.toString(),loginBean.getSumFormat())%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap><input id="t_xsje" name="t_xsje" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_xsje.toString(),loginBean.getPriceFormat())%>'  readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap><input id="t_jje" name="t_jje" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_jje.toString(),loginBean.getPriceFormat())%>'  readonly></td>
                        <td class="td">&nbsp;</td>
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
             <input type="hidden" name="importOrder" value="引入合同" onchange="sumitForm(<%=b_ladingbillBean.DETAIL_SALE_ADD%>,-1)">
             <input name="btnback" style="width:80" class="button" type="button" value="引入合同(W)" onClick="checkdwst()" border="0">
             <pc:shortcut key="w" script="checkdwst()" />
             <input type="hidden" name="importOrderproduct" value="引入合同货物" onchange="sumitForm(<%=b_ladingbillBean.PRODUCT_ADD%>,-1)">
             <input name="btnback" style="width:100" class="button" type="button" value="引入合同货物(E)" onClick="checkdwtxid()" border="0">
             <pc:shortcut key="e" script="checkdwtxid()" />
             <%
               }if(!isEnd){
               String retun = "sumitForm("+Operate.POST_CONTINUE+")";
              %>
              <input name="button2"  style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存新增(N)">
              <pc:shortcut key="n" script="<%=retun%>" />
              <%
               }
               if(zt.equals("0"))
               {
                 if(!b_ladingbillBean.isReport){
                   String post ="sumitForm("+Operate.POST+");";
               %>
              <input name="btnback" style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
              <pc:shortcut key="s" script="<%=post%>" />
               <%}
               }if(zt.equals("1")||zt.equals("2")){
                 if(!b_ladingbillBean.isReport){
                   String appost ="sumitForm("+b_ladingbillBean.APPROVED_MASTER_ADD+");";
             %>
              <input name="btnback" style="width:80" type="button" class="button" onClick="sumitForm(<%=b_ladingbillBean.APPROVED_MASTER_ADD%>);" value="保存(S)">
              <pc:shortcut key="s" script="<%=appost%>" />
              <%
              }}if(isCanDelete&&!b_ladingbillBean.isReport){
               String del = "if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+");";
               String detNull = "sumitForm("+b_ladingbillBean.DEL_NULL+");";
              %>
              <input name="button3"  style="width:110" type="button" class="button" title="删除空行(R)" value="删除数量为空行(R)" onClick="<%=detNull%>">
              <pc:shortcut key="r" script="<%=detNull%>" />
              <input name="button3"  style="width:60" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value="删除(D)">
              <pc:shortcut key="d" script="<%=del%>" />
              <%
              }if(!b_ladingbillBean.isReport){
              %>
              <input type="button" class="button" value="打印(P)" onclick="location.href='../pub/pdfprint.jsp?code=ladding_bill_edit_print&operate=<%=Operate.PRINT_BILL%>&a$tdid=<%=masterRow.get("tdid")%>&src=../sale/lading_bill_edit.jsp'">
              <%--<input type="button" class="button" value="回单打印(D)" onclick="location.href='../pub/pdfprint.jsp?code=ladding_bill_return_print&operate=<%=Operate.PRINT_BILL%>&a$tdid=<%=masterRow.get("tdid")%>&src=../sale/lading_bill_edit.jsp'">--%>
              <input name="btnback"  style="width:50" type="button" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script="backList();" /><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
  initDefaultTableRow('tableview1',1);
  <%=b_ladingbillBean.adjustInputSize(new String[]{"cpbm","product","sxz","hssl","sl","xsj","xsje", "zk","dj","jje","bz"}, "form1", detailRows.length)%>
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
    var xsjObj = document.all['xsj_'+i];
    var xsjeObj = document.all['xsje_'+i];
    var djObj = document.all['dj_'+i];
    var jjeObj = document.all['jje_'+i];
    //var hsblObj = document.all['hsbl_'+i];
    var hsblObj = document.all['truebl_'+i];
    var hsslObj = document.all['hssl_'+i];
    var yfObj = document.all['yf'];
    var yfdjObj = document.all['yfdj'];
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
    if(yfdjObj.value=="")
      yfdjObj.value="0";
    /*
    if(parseFloat(obj.value)<0){
      alert(showText);
      obj.focus();
      return;
    }*/
    <%
    if(mustConversion){
    //要强制转换
    %>
    changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
    <%}else {%>
      if(hsslObj.value==""||slObj.value=="")
        changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
      <%}%>
    //changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
    if(xsjObj.value!="" && !isNaN(xsjObj.value))
      xsjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(xsjObj.value));
    if(djObj.value!="" && !isNaN(djObj.value))
      jjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(djObj.value));
    cal_tot('sl');
    cal_tot('xsje');
    cal_tot('jje');
    cal_tot('hssl');
    //--------------------------------------------
    var t_slObj = document.all['t_sl'];
    yfObj.value =parseFloat(yfdjObj.value)*parseFloat(t_slObj.value);
  }
  function dj_onchange(i, isRebate)
  {
    var slObj = document.all['sl_'+i];
    var zkObj = document.all['zk_'+i];
    var xsjObj = document.all['xsj_'+i];
    var xsjeObj = document.all['xsje_'+i];
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
      changeObj.value = formatPrice(isRebate ? (parseFloat(xsjObj.value)*parseFloat(zkObj.value)/100) : 0);
    else
      changeObj.value = formatPrice(isRebate ? (parseFloat(xsjObj.value)*parseFloat(zkObj.value)/100) : (parseFloat(djObj.value)/parseFloat(xsjObj.value)*100));    if(slObj.value!="" && !isNaN(slObj.value))
      xsjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(xsjObj.value));
    if(slObj.value!="" && !isNaN(slObj.value))
      jjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(djObj.value));
    cal_tot('xsje');
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
      else if(type == 'xsje')
        tmpObj = document.all['xsje_'+i];
      else if(type == 'hssl')
        tmpObj = document.all['hssl_'+i];
      else if(type == 'jje')
        tmpObj = document.all['jje_'+i];
      else
        return;
      if(tmpObj.value!="" && !isNaN(tmpObj.value))
        tot += parseFloat(tmpObj.value);
    }
    if(type == 'sl')
    {
      document.all['t_sl'].value = formatQty(tot);
      yfdjonchange();
    }
    else if(type == 'xsje')
      document.all['t_xsje'].value = formatPrice(tot);
    else if(type == 'hssl')
      document.all['t_hssl'].value = formatQty(tot);
    else if(type == 'jje')
      document.all['t_jje'].value = formatPrice(tot);
  }
 function prodsMultiSelect(frmName, srcVar)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=500 height=400 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "../sale/import_apply_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
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
 //OrderMultiSelect('form1','srcVar=importOrder&dwtxid='+form1.dwtxid.value)
function OrderMultiSelect(frmName, srcVar, methodName,notin)
 {

   var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
   var winName= "OrdersSelector";
   paraStr = "../sale/import_order.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
   if(methodName+'' != 'undefined')
     paraStr += "&method="+methodName;
   if(notin+'' != 'undefined')
   paraStr += "&notin="+notin;

   openSelectUrl(paraStr, winName, winopt2);
   //newWin =window.open(paraStr,winName,winopt);
   //newWin.focus();
  }
function MultiSelectOrder(frmName, srcVar, methodName,notin)
   {
     var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
     var winName= "OrdersSelector";
     paraStr = "../sale/select_order_product.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
     if(methodName+'' != 'undefined')
       paraStr += "&method="+methodName;
     if(notin+'' != 'undefined')
     paraStr += "&notin="+notin;
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
    PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i, 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value, 'sumitForm(<%=b_ladingbillBean.DETAIL_CHANGE%>,'+i+')');
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
    OrderSingleSelect('form1','srcVar=selectedhtid','fieldVar=htid','&dwtxid='+form1.dwtxid.value+'&djlx='+form1.djlx.value+'&storeid='+form1.storeid.value,"sumitForm(<%=b_ladingbillBean.IMPORT_ORDER%>,-1)");
  }
function checkdwst()
  {
  if(form1.storeid.value=='')
  {
    alert('请选择仓库');
    return;
  }
  OrderMultiSelect('form1','srcVar=importOrder&dwtxid='+form1.dwtxid.value+'&storeid='+form1.storeid.value+'&djlx='+form1.djlx.value+'&personid='+form1.personid.value+'&khlx='+form1.khlx.value+'&jsfsid='+form1.jsfsid.value+'&sendmodeid='+form1.sendmodeid.value+'&yfdj='+form1.yfdj.value)
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
  if(form1.khlx.value=='')
  {
    alert('请选择客户类型');
    return;
  }
  if(form1.jsfsid.value=='')
  {
    alert('请选择结算方式');
    return;
  }
  MultiSelectOrder('form1','srcVar=importOrderproduct&dwtxid='+form1.dwtxid.value+'&storeid='+form1.storeid.value+'&djlx='+form1.djlx.value+'&personid='+form1.personid.value+'&khlx='+form1.khlx.value+'&jsfsid='+form1.jsfsid.value)
}
  function checkdwstandstore()
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
  HistoryProdMultiSelect('form1','srcVar=multiIdInput&dwtxid='+form1.dwtxid.value+'&storeid='+form1.storeid.value)
}
function HistoryProdMultiSelect(frmName, srcVar, methodName,notin)
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
//产品编码
function productCodeSelect(obj, i)
{
    SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=wzdjid_'+i+'&srcVar=xsj_'+i+'&srcVar=cpid_'+i+'&storeid='+form1.storeid.value,'fieldVar=wzdjid&fieldVar=xsj&fieldVar=cpid',obj.value,'sumitForm(<%=b_ladingbillBean.DETAIL_CHANGE%>,'+i+')');
}
//产品名称
function productNameSelect(obj, i)
{
    SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=wzdjid_'+i+'&srcVar=xsj_'+i+'&srcVar=cpid_'+i+'&storeid='+form1.storeid.value,'fieldVar=wzdjid&fieldVar=xsj&fieldVar=cpid',obj.value,'sumitForm(<%=b_ladingbillBean.DETAIL_CHANGE%>,'+i+')');
}
function yfdjonchange()
{
  var tslObj = document.all['t_sl'];
  var yfObj = document.all['yf'];
  var yfdjObj = document.all['yfdj'];
  if(isNaN(form1.yfdj.value)){
    alert("非法运费单价");
    form1.yfdj.focus();
    return;
    }
  if(tslObj=="")
    return;
  form1.yf.value = parseFloat(tslObj.value)*parseFloat(form1.yfdj.value);
}
</script>
<%//&#$
if(b_ladingbillBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
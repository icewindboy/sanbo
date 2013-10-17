<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "advance_payment";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
</head>
<%if(!loginBean.hasLimits("advance_payment", request, response))
    return;
    engine.project.LookUp currencyBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);//货币LOOKUP
    engine.erp.finance.B_AdvancePayment paymentBean  =  engine.erp.finance.B_AdvancePayment.getInstance(request);
    engine.project.LookUp DEPTBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_DEPT);//部门LOOKUP
    engine.project.LookUp balanceBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_BALANCE_MODE);//结算方式
    engine.project.LookUp personBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PERSON);//人员
    engine.project.LookUp companyBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);//往来单位LOOKUP
    engine.project.LookUp bankBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_BANK);//往来单位LOOKUP
%>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();//
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
function corpCodeSelect(obj)
{
  ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                    'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, '');
}
function corpNameSelect(obj)
{
  ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                    'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, '');
}
function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
function price_onchange(isBigUnit)
{
  var jeObj = document.form1.je;//报价
  var hlObj = document.form1.hl;//汇率
  var ybjeObj = document.form1.ybje;//外币报价
  var obj = isBigUnit ? ybjeObj : jeObj;
  var showText = isBigUnit ? "输入的外币金额非法" : "输入的金额非法";
  var showText2 = isBigUnit ? "输入的外币报价不能小于零" : "输入的原币报价不能小于零";
  var changeObj = isBigUnit ? jeObj : ybjeObj;//
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
  if(hlObj.value=="")
    return;
  if(isNaN(hlObj.value)){
    alert('汇率非法');
    return;
  }
  if(hlObj.value!="" && !isNaN(hlObj.value)){
    changeObj.value = formatQty(isBigUnit ? (parseFloat(ybjeObj.value)*parseFloat(hlObj.value)) : (parseFloat(jeObj.value)/parseFloat(hlObj.value)));
    if(isBigUnit)
      jeObj.value=changeObj.value;
    else
      ybjeObj.value=changeObj.value;
  }
}
function hl_onchange()
{
  var jeObj1 = document.form1.je;//报价
  var hlObj1 = document.form1.hl;//汇率
  var ybjeObj1 = document.form1.ybje;//外币报价
  if(hlObj1.value=="")
    return;
  if(isNaN(hlObj1.value)){
    alert('汇率非法');
    return;
  }
  if(jeObj1.value!="" && !isNaN(jeObj1.value))
    ybjeObj1.value = formatQty(parseFloat(jeObj1.value)/parseFloat(hlObj1.value));
  if(jeObj1.value=="" && ybjeObj1.value!="" && !isNaN(ybjeObj1.value))
    jeObj1.value = formatQty(parseFloat(ybjeObj1.value)*parseFloat(hlObj1.value));
}
function backList()
{
  location.href='advance_payment.jsp?operate=0&src=../pub/main.jsp';
}
function checkStr(){
  if(form1.dwtxid.value==''){
    alert('请输入往来单位');return false;
  }
  else if(form1.v_deptid.value==''){
    alert('部门不能为空');return false;
  }
  else{return true;}
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">

<%String retu = paymentBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  String state=null;
  EngineDataSet ds = paymentBean.getOneTable();
  RowMap row = paymentBean.getRowinfo();
  //boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
  //String edClass = "class=edbox";
  //String readonly = isCanAdd?"":"readonly";
  DEPTBean.regData(ds,"deptid");
  balanceBean.regData(ds,"jsfsid");
  currencyBean.regData(ds,"wbId");
  personBean.regData(ds,"personid");
  companyBean.regData(ds,"dwtxId");
  String zt=row.get("zt");
  boolean isCanAmend=(paymentBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit))&&(zt.equals("0")||zt.equals(""));
  String edClass =!isCanAmend ? "class=edline" : "class=edbox";
  String Readonly =!isCanAmend ? "readonly" : "";
  //out.print(Readonly+"  "+edClass);
  if(zt.equals("9")){state="审批中";}
  else if(zt.equals("1")){state="已审";}
  else {state="未审";}
%>
<%--table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar">
<tr><TD NOWRAP align="center">采购(加工)预付款</TD></tr></table--%>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
    <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
<INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
<table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="600">
  <tr valign="top">
   <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table"></td>
   <td class="activeVTab">采购预付款(<%=state%>)</td>
  </tr>
 </table>
     <table class="editformbox" CELLSPACING=1 CELLPADDING=0 height="215" bgcolor="#f0f0f0">
    <tr>
     <%
       RowMap wbRow = currencyBean.getLookupRow(row.get("wbid"));
       String sumit = "if(form1.wbid.value!='"+row.get("wbid")+"')sumitForm("+paymentBean.WB_ONCHANGE+")";

      %>
      <td noWrap class="tdTitle">&nbsp;&nbsp;&nbsp;往来单位</td>
      <%--<td noWrap class="td">
       <INPUT TYPE="text" NAME="dwtxId" VALUE='<%=row.get("dwtxId")%>'>
        <pc:select name="dwtxId"  addNull="1"  style="width:130" value='<%=row.get("dwtxId")%>'>
           <%=companyBean.getList()%>
        </pc:select>
      </td>--%>
      <%RowMap corpRow = companyBean.getLookupRow(row.get("dwtxid"));%>
      <td  noWrap class="td" colspan="4">
        <input type="text" name="dwdm" value='<%=corpRow.get("dwdm")%>' style="width:60" <%=edClass%> <%=Readonly%> onKeyDown="return getNextElement();" onchange="corpCodeSelect(this);">
        <input type="hidden" name="dwtxid" value='<%=row.get("dwtxid")%>' style="width:60" <%=edClass%> >
        <input type="text" name="dwmc" value='<%=companyBean.getLookupName(row.get("dwtxId"))%>' style="width:190" <%=edClass%> <%=Readonly%> onKeyDown="return getNextElement();" onchange="corpNameSelect(this);">
        <%if(isCanAmend){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm&srcVar=zh&srcVar=yh','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm&fieldVar=zh&fieldVar=khh',form1.dwtxid.value,'');">
        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value=''">
        <%}%>
      </td>
    </tr>
    <tr>
      <td noWrap class="tdTitle">&nbsp;&nbsp;&nbsp;部门</td>
       <td noWrap class="td"><%if(!isCanAmend){%><input type="text" <%=edClass%>  value='<%=DEPTBean.getLookupName(row.get("deptid"))%>' readonly><%}else{%>
        <pc:select name="deptid"  addNull="1"  style="width:130;" value='<%=row.get("deptid")%>' onSelect="deptchange();">
           <%=DEPTBean.getList()%>
        </pc:select>
      </td><%}%>
      <td noWrap class="tdTitle">&nbsp;结算方式</td>
      <td noWrap class="td"><%if(!isCanAmend){%><input type="text" <%=edClass%>  value='<%=balanceBean.getLookupName(row.get("jsfsid"))%>' readonly><%}else{%>
       <pc:select name="jsfsid" style="width:130" addNull="1"  value='<%=row.get("jsfsid")%>'>
       <%=balanceBean.getList()%>
        </pc:select></td><%}%>
    </tr>
    <tr>
      <td noWrap class="tdTitle">&nbsp;&nbsp;&nbsp;人员</td>
       <td noWrap class="td"><%if(!isCanAmend){%><input type="text" <%=edClass%>  value='<%=personBean.getLookupName(row.get("personid"))%>' readonly><%}else{%>
        <pc:select name="personid"  addNull="1"  style="width:130" value='<%=row.get("personid")%>'>
           <%=personBean.getList()%>
        </pc:select>
      </td><%}%>
      <td noWrap class="tdTitle">&nbsp;预付款编号</td>
      <td noWrap class="td">
        <INPUT TYPE="TEXT" NAME="yfkbh" VALUE="<%=row.get("yfkbh")%>" style="WIDTH:130" <%=edClass%> <%=Readonly%> onKeyDown="return getNextElement();">
      </td>
    </tr>
    <tr>
      <td noWrap class="tdTitle">&nbsp;&nbsp;&nbsp;预付类型</td>
       <td noWrap class="td"><%if(!isCanAmend){%><input type="text" <%=edClass%>  value='<%=row.get("dygs").equals("0") ?"采购预付款":"加工预付款"%>' readonly><%}else{%>
        <pc:select name="yflx" style="width:130" value='<%=row.get("dygs")%>'>
        <pc:option value="0">采购预付款</pc:option>
        <pc:option value="1">加工预付款</pc:option>
        </pc:select></td><%}%>
      <td noWrap class="tdTitle">&nbsp;预付日期</td>
      <td noWrap class="td">
        <INPUT TYPE="TEXT" NAME="yfrq" VALUE="<%=row.get("yfrq")%>" style="WIDTH:130"  <%=edClass%>  <%=Readonly%> onKeyDown="return getNextElement();">
        <%if(isCanAmend){%><A href="#"><IMG title=选择日期 onClick="selectDate(yfrq);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
        <%}%>
      </td>
    </tr>
    <tr>
     <td noWrap class="tdTitle">&nbsp;&nbsp;&nbsp;外币</td>
       <td noWrap class="td"><%if(!isCanAmend){%><input type="text" <%=edClass%>  value='<%=currencyBean.getLookupName(row.get("wbId"))%>' readonly><%}else{%>
        <pc:select name="wbid"  addNull="1"  style="width:130" value='<%=row.get("wbId")%>' onSelect="<%=sumit%>">
           <%=currencyBean.getList()%>
        </pc:select>
      </td><%}%>
      <td noWrap class="tdTitle">&nbsp;汇率</td>
       <td noWrap class="td">
        <INPUT TYPE="text" NAME="hl" style="width:130" VALUE="<%=row.get("hl")%>" <%=edClass%> <%=Readonly%> onKeyDown="return getNextElement();" onchange="hl_onchange()">
      </td>
    </tr>
    <tr>
      <td noWrap class="tdTitle">&nbsp;&nbsp;&nbsp;金额</td>
      <td noWrap class="td">
        <INPUT TYPE="TEXT" NAME="je" style="WIDTH:130"VALUE="<%=row.get("je")%>" <%=edClass%> <%=Readonly%> onKeyDown="return getNextElement();" onchange="price_onchange(false)">
      </td>
      <td noWrap class="tdTitle">&nbsp;外币金额</td>
       <td noWrap class="td">
        <INPUT TYPE="text" NAME="ybje" style="width:130" VALUE="<%=row.get("ybje")%>" onKeyDown="return getNextElement();" onchange="price_onchange(true)" <%=edClass%> <%=Readonly%>>
      </td>
    </tr>
    <tr>
    <td noWrap class="tdTitle">&nbsp;&nbsp;&nbsp;帐号</td>
      <td noWrap class="td">
        <INPUT TYPE="TEXT" NAME="zh" style="WIDTH:130"VALUE="<%=row.get("zh")%>" <%=edClass%>  <%=Readonly%> onKeyDown="return getNextElement();">
      </td>
      <td noWrap class="tdTitle">&nbsp;银行</td>
      <td noWrap class="td">
        <INPUT TYPE="TEXT" NAME="yh" style="WIDTH:130"VALUE="<%=row.get("yh")%>" <%=edClass%>  <%=Readonly%> onKeyDown="return getNextElement();">
      </td>
    </tr>
    <tr>
    <td noWrap class="tdTitle">&nbsp;&nbsp;&nbsp;预付描述</td>
      <td noWrap class="td">
        <INPUT TYPE="TEXT" NAME="yfms" style="WIDTH:130"VALUE="<%=row.get("yfms")%>" <%=edClass%> <%=Readonly%> onKeyDown="return getNextElement();">
      </td>
    </tr>
    <tr>
      <td noWrap class="tdTitle"><%--&nbsp;状态描述--%></td>
       <td noWrap class="td">
        <INPUT TYPE="hidden" NAME="ztms" style="width:130" VALUE="<%=row.get("ztms")%>" <%=edClass%> <%=Readonly%> onKeyDown="return getNextElement();">
      </td>
      <td noWrap class="tdTitle"><%--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;分公司--%></td>
      <td noWrap class="td">
        <INPUT TYPE="hidden" NAME="fgsid" style="WIDTH:130"VALUE="<%=row.get("fgsID")%>" <%=edClass%> <%=Readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tdTitle"><%--&nbsp;操作员--%></td>
       <td noWrap class="td">
        <INPUT TYPE="hidden" NAME="czyid" style="width:130" VALUE="<%=row.get("czyid")%>">
        <INPUT TYPE="hidden" NAME="czy" style="width:130" VALUE="<%=row.get("czy")%>" <%=edClass%> onKeyDown="return getNextElement();">
      </td>
      <td noWrap class="tdTitle"><%--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;操作日期--%></td>
      <td noWrap class="td">
        <INPUT TYPE="Hidden" NAME="czrq" style="WIDTH:130"VALUE="<%=row.get("czrq")%>" <%=edClass%> onKeyDown="return getNextElement();">
      </td>
    </tr>
   </table>
    <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
    <tr>
      <td class="td"><b>操作日期:</b><%=row.get("czrq")%></td>
      <td class="td"></td>
      <td class="td" align="right"><b>操作员:</b><%=row.get("czy")%></td>
    </tr>
    <tr>
    <td colspan="4" noWrap class="tdTitle">
      <%if(isCanAmend){%><input name="button" type="button" class="button" onClick="if(checkStr()){sumitForm(<%=Operate.POST%>);}" value="保存(S)">
      <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
      <%if(!paymentBean.isAdd&&loginBean.hasLimits(pageCode, op_delete)&&(zt.equals("0")||zt.equals(""))){%><input name="button" type="button" class="button" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>)" value="删除(D)"><%}%>
      <%if(!paymentBean.isApprove){%><input name="button2" type="button" class="button" onClick="backList()" value="返回(C)"><%}%>
      <pc:shortcut key="c" script="backList()"/>
    </td>
    </tr>
   </table>
</table>
</form>
<%if(paymentBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
</BODY>
</Html>
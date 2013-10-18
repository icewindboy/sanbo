<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,java.text.*,java.util.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.sale.shengyu.B_OtherBalance otherBalanceBean = engine.erp.sale.shengyu.B_OtherBalance.getInstance(request);
  String pageCode = "other_balance";
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
  location.href='other_balance.jsp';
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
</script>
<%
  String retu = otherBalanceBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);//引用人员信息
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//引用结算方式
  engine.project.LookUp fundTypeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_FUND_TYPE);//

  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//引用往来单位
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = otherBalanceBean.getOneTable();
  RowMap masterRow = otherBalanceBean.getRowinfo();
  String zt=masterRow.get("zt");
  boolean isEnd = zt.equals("1")||zt.equals("9")||zt.equals("8");
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=edline" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  corpBean.regData(list, "dwtxid");
  //String title = zt.equals("0") ? ("未审批") : (zt.equals("9") ? "审批中" :(zt.equals("4")?"已作废":(zt.equals("8")?"完成":"已审")) );
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
%>
<BODY oncontextmenu="window.event.returnValue=true" >
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();"  >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr><td align="center" height="5"></td></tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="500">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">其他应收款</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
            <tr>
                <td noWrap class="tdTitle">单据号</td>
                <td noWrap class="td">
                <input type="text" class="edline" readonly name="otherfundno" onKeyDown="return getNextElement();"  value='<%=masterRow.get("otherfundno")%>' style="width:110"  >
                </td>
              <td align="center" nowrap class="tdtitle">开单日期</td>
              <td nowrap class="td">
                <input  <%=edClass%>  style="WIDTH: 130px" name="otherfunddate"  value='<%=masterRow.get("otherfunddate")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();"  <%=readonly%> >
                <%if(!isEnd){%><a href="javascript:"><img title=选择日期 onClick="selectDate(otherfunddate);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></a> <%}%></td>
               <td noWrap class="tdTitle">部门</td>
               <td noWrap class="td">
                    <%
                      String onChange ="if(form1.deptid.value!='"+masterRow.get("deptid")+"')sumitForm("+Operate.DEPT_CHANGE+")";
                    %>
                    <%if(isEnd) out.print("<input type='hidden' name='deptid' value='"+masterRow.get("deptid")+"'><input type='text'  value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110"  onSelect="deptchange();" >
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                    </td>
                    <td noWrap class="tdTitle">业务员</td>
                    <td  noWrap class="td">
                    <%
                      if(!isEnd)
                        personBean.regConditionData(list, "deptid");
                      if(isEnd) out.print("<input type='hidden' name='personid' value='"+masterRow.get("personid")+"'><input type='text'   value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:110' class='edline' readonly>");
                      else {%>
                     <pc:select name="personid"  addNull="1" style="width:110" >
                      <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                   </td>
            </tr>
              <tr>
                  <td noWrap class="tdTitle">客户名称</td>
                  <td colspan="3" noWrap class="td">
                    <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text" <%=edClass%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="customerCodeSelect(this)" <%=readonly%>>
                    <input type="text" <%=edClass%> name="dwmc" onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:200"   onchange="customerNameSelect(this)"  <%=readonly%>>
                    <%if(!isEnd){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="selectCustomer()">
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">客户地址</td>
                  <td colspan="3" noWrap class="td">
                    <input type="text" <%=edClass%> name="custaddr" onKeyDown="return getNextElement();" value='<%=masterRow.get("custaddr")%>' style="width:280"   <%=readonly%>>
                  </td>
             </tr>
              <tr>
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
                  <td  noWrap class="tdTitle">结算方式</td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='hidden' name='jsfsid' value='"+masterRow.get("jsfsid")+"'><input type='text'   value='"+balanceModeBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="jsfsid"  addNull="1"  style="width:110">
                    <%=balanceModeBean.getList(masterRow.get("jsfsid"))%>
                    </pc:select>
                    <%}%></td>
                <td noWrap class="tdTitle">金额</td>
                <td noWrap class="td">
                <input type="text" <%=edClass%> <%=readonly%> name="otherfund" onKeyDown="return getNextElement();"  value='<%=masterRow.get("otherfund")%>' style="width:110"  >
                </td>
            </tr>
            <tr>
                  <td  noWrap class="tdTitle">单据项目</td>
                  <td  noWrap class="td">
                  <%if(isEnd)
                    {
                    out.print("<input type='text' value='"+masterRow.get("otherfunditem")+"' style='width:110' class='edline' readonly>");
                    }else
                    {String otherfunditem=masterRow.get("otherfunditem");%>
                  <pc:select name="otherfunditem" addNull="1" style="width:110"   combox="1" value="<%=otherfunditem%>">
                  <%=fundTypeBean.getList()%> </pc:select>
                   <%}%></td>
            </tr>
                <tr>
                  <td  noWrap class="tdTitle">备注</td><%--其他信息--%>
                  <td colspan="7" noWrap class="td"><textarea name="bz" rows="3" onKeyDown="return getNextElement();"  <%=(zt.equals("9")||zt.equals("8")||zt.equals("1"))?"readonly":""%>  style="width:690" <%=(zt.equals("9")||zt.equals("8"))?"readonly":""%> ><%=masterRow.get("bz")%></textarea></td>
                </tr>
         </table>
        </td>
      </tr>
    </table>
  </td>
</tr>
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
               <%if(!otherBalanceBean.isApprove&&!otherBalanceBean.isReport){%>
               <%if(!isEnd){%>
               <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 ">
               <input name="button3"  style="width:50" type="button" class="button" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>);" value="删除(D)">
               <%}%>
              <%if(zt.equals("1")||zt.equals("8")){%>
              <input type="button" class="button" value="打印(P)" onclick="location.href='../pub/pdfprint.jsp?code=shyu_otherbalance_print&operate=<%=Operate.PRINT_BILL%>&a$otherfundid=<%=masterRow.get("otherfundid")%>&src=../sale_shengyu/other_balance_edit.jsp'">
              <%}%>
              <%--if(zt.equals("1")){%>
              <input name="button" type="button" class="button" onClick="sumitForm(<%=otherBalanceBean.AFTER_POST%>);" value=" 保存 "><%}--%>
              <input name="btnback" type="button"  style="width:50" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script='<%="backList();"%>'/>
              <%}%>
            </td>
          </tr>
        </table>
       </td>
    </tr>
  </table>
</form>
<script language="javascript">
  function zkchange()
  {
    var djObj = document.all['dj'];
    var zkObj = document.all['zk'];
    var prom_priceObj = document.all['prom_price'];
    var djlxObj = document.all['djlx'];
    if(djlxObj.value=='')
    {
      zkObj.value='';
      alert('请选择定价类型!');
      return;
    }
    if(djObj.value=='')
    {
      zkObj.value='';
      alert('该产品所选定价类型没定价!');
      return;
    }
    if(isNaN(djObj.value))
    {
      zkObj.value='';
      alert('该产品所选定价类型没定价!');
      return ;
    }
    if(isNaN(zkObj.value))
    {
      zkObj.value='';
      alert('折扣非法!');
      return ;
    }
    prom_priceObj.value=parseFloat(djObj.value)*parseFloat(zkObj.value)/100;
  }
  function prom_pricechange()
  {
    var djObj = document.all['dj'];
    var zkObj = document.all['zk'];
    var prom_priceObj = document.all['prom_price'];
    var djlxObj = document.all['djlx'];
    if(djlxObj.value=='')
    {
      zkObj.value='';
      alert('请选择定价类型!');
      return;
    }
    if(djObj.value==''||djObj.value=='0')
    {
      zkObj.value='';
      alert('该产品所选定价类型没定价!');
      return;
    }
    if(isNaN(djObj.value))
    {
      prom_priceObj.value='';
      alert('该产品所选定价类型没定价!');
      return ;
    }
    if(isNaN(prom_priceObj.value))
    {
      zkObj.value='';
      alert('促销单价非法!');
      return ;
    }
    zkObj.value=parseFloat(prom_priceObj.value)*100/parseFloat(djObj.value);
  }
function getprice()
{
    getRowValue(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PROD_PRICE%>', 'form1', 'srcVar=dj', 'fieldVar='+eval('form1.djlx.value'),eval('form1.cpid.value'));
    unlockScreenWait();
    zkchange();
}
function add()
{
 if(form1.mdeptid.value=='')
 {
   alert('请选择部门!');
   return;
 }
   sumitForm(<%=Operate.DETAIL_ADD%>);
}
//产品编码
function productCodeSelect(obj)
{
  SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',obj.value,'sumitForm(<%=otherBalanceBean.PRODUCT_CHANGE%>)');
}
//产品名称
function productNameSelect(obj)
{
  SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',obj.value,'sumitForm(<%=otherBalanceBean.PRODUCT_CHANGE%>)');
}
//选择购货单位
function selectCustomer()
{
  CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=custaddr&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=otherBalanceBean.DWTXID_CHANGE%>,-1)');
}
//客户编码
function customerCodeSelect(obj)
{
  CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=custaddr','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr',obj.value,'sumitForm(<%=otherBalanceBean.DWTXID_CHANGE%>,-1)');
}
//客户名称
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=custaddr','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr',obj.value,'sumitForm(<%=otherBalanceBean.DWTXID_CHANGE%>,-1)');
}
</script>
<%//&#$
if(otherBalanceBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
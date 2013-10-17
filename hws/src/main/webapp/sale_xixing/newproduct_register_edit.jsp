<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,java.text.*,java.util.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.sale.xixing.B_NewProductRegister b_NewProductRegisterBean = engine.erp.sale.xixing.B_NewProductRegister.getInstance(request);
  String pageCode = "newproduct_register";
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
  location.href='newproduct_register.jsp';
}
</script>
<%
  String retu = b_NewProductRegisterBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//引用产品
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//引用往来单位
  engine.project.LookUp saleProdBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_LADING_BILL);//提单货物
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = b_NewProductRegisterBean.getOneTable();
  RowMap masterRow = b_NewProductRegisterBean.getRowinfo();
  String zt=masterRow.get("zt");
  boolean isEnd = zt.equals("1")||zt.equals("9")||zt.equals("8");
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=edline" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String title = zt.equals("0") ? ("未审批") : (zt.equals("9") ? "审批中" :(zt.equals("4")?"已作废":(zt.equals("8")?"完成":"已审")) );
  String cpid = masterRow.get("cpid");
  String wzdjid = masterRow.get("wzdjid");
  String tdhwid = masterRow.get("tdhwid");
  String xs__tdhwid = masterRow.get("xs__tdhwid");
  if(b_NewProductRegisterBean.isReport||b_NewProductRegisterBean.isApprove)
  {
  prodBean.regData(list,"cpid");
  propertyBean.regData(list,"dmsxid");
  corpBean.regData(list, "dwtxid");
  saleProdBean.regData(list, "tdhwid");
  }

  RowMap productRow = prodBean.getLookupRow(cpid);
  RowMap propertyRow = propertyBean.getLookupRow(masterRow.get("dmsxID"));
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
  RowMap saleprodRow =saleProdBean.getLookupRow(masterRow.get("tdhwid"));
  RowMap xsprodpRow =saleProdBean.getLookupRow(xs__tdhwid);
  String tdbh = xsprodpRow.get("tdbh");
  String isprops=productRow.get("isprops");
%>
<BODY oncontextmenu="window.event.returnValue=true" >
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();"  >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr><td align="center" height="5"></td></tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="wzdjid" value="<%=wzdjid%>">
  <INPUT TYPE="HIDDEN" NAME="seltdhwid" value="">
  <INPUT TYPE="HIDDEN" NAME="cpid" value="<%=cpid%>">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">新产品发货登记表</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
              <tr>
                 <td noWrap class="tdTitle">单据号</td>
                 <td noWrap class="td">
                 <input type="text" class="edline" name="djh" onKeyDown="return getNextElement();" value='<%=masterRow.get("djh")%>' style="width:110"  readonly >
                 </td>
                  <td noWrap class="tdTitle">部门</td>
                  <td noWrap class="td">
                  <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:140' class='edline' readonly ><input type='hidden'  name='deptid' value='"+masterRow.get("deptid")+"' style='width:80' class='edline' readonly >");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:120"  >
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
             </tr>
              <tr>
                  <td noWrap class="tdTitle">客户名称</td><%--购货单位--%>
                  <td colspan="5" noWrap class="td">
                    <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text"  <%=edClass%> <%=readonly%> style="width:100" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="customerCodeSelect(this)" >
                    <input type="text" <%=edClass%> <%=readonly%> style="width:300" name="dwmc" onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>'    onchange="customerNameSelect(this)"  >
                    <%if(!isEnd){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="selectCustomer()"><%}%>
                  </td>
             </tr>
             <tr>
                  <TD align="center" nowrap class="tdTitle">产品</TD>
                  <td nowrap class="td" colspan="5">
                  <input <%=edClass%> <%=readonly%> style="WIDTH:100" name="cpbm" value='<%=productRow.get("cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)" >
                  <INPUT <%=edClass%> <%=readonly%> style="WIDTH:300" id="product" name="product" value='<%=productRow.get("product")%>' maxlength='10' onKeyDown="return getNextElement();"  onchange="productNameSelect(this)">
                  <%if(!isEnd){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="SaleProdSingleSelect('form1','srcVar=cpid&srcVar=product&srcVar=cpbm&srcVar=wzdjid&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fieldVar=wzdjid')">
                  <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';product.value='';cpbm.value='';wzdjid.value='';"><%}%>
                  </td>
             </tr>
              <tr>
                <td noWrap class="tdTitle">单价</td>
                <td noWrap class="td">
                <input type="text" <%=edClass%> <%=readonly%> name="dj" onKeyDown="return getNextElement();" value='<%=masterRow.get("dj")%>' style="width:110"  >
                </td>
                <td noWrap class="tdTitle">计划数量</td>
                <td noWrap class="td">
                <input type="text" <%=edClass%> <%=readonly%> name="jhsl" onKeyDown="return getNextElement();" value='<%=masterRow.get("jhsl")%>' style="width:110"  >
                </td>
                  <TD align="center" nowrap class="tdTitle">规格属性</TD>
                  <td class="td" nowrap>
                  <input type="text" <%=edClass%> <%=readonly%> style="WIDTH:140"  name="sxz" value='<%=propertyBean.getLookupName(masterRow.get("dmsxid"))%>' onchange="propertiesInput(this,form1.cpid.value)"   >
                  <input type="HIDDEN"  id="dmsxid"  name="dmsxid"  value='<%=masterRow.get("dmsxid")%>'  >
                  <%if(!isEnd){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid.value==''){alert('请先输入产品');return;}if('<%=isprops%>'=='0'){alert('该产品无规格属性!');return;}PropertySelect('form1','dmsxid','sxz',form1.cpid.value)">
                  <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid.value='';sxz.value='';"><%}%>
                  </td>
            </tr>
            <tr>
                <td noWrap class="tdTitle">清单号</td>
                <td noWrap class="td">
                <INPUT TYPE="HIDDEN" NAME="tdhwid" value="<%=tdhwid%>">
                <input type="text" <%=zt.equals("1")?"class='edbox'":"class='edline'"%> <%=zt.equals("1")?"":"readonly"%> name="tdbh" onKeyDown="return getNextElement();" value='<%=saleprodRow.get("tdbh")%>' style="width:110"  >
                <%if(zt.equals("1")){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="selctbilloflading()">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="sl.value='';tdrq.value='';tdbh.value='';tdhwid.value='';"><%}%>
                </td>
                <td noWrap class="tdTitle">发货日期</td>
                <td noWrap class="td">
                <input type="text" class="edline" name="tdrq" onKeyDown="return getNextElement();" value='<%=saleprodRow.get("tdrq")%>' style="width:110" readonly >
                </td>
                <td noWrap class="tdTitle">发出数量</td>
                <td noWrap class="td">
                <input type="text" class="edline" name="sl" onKeyDown="return getNextElement();" value='<%=saleprodRow.get("sl")%>' style="width:110"  readonly >
                </td>
            </tr>
            <tr>
                <td noWrap class="tdTitle">返单清单号</td>
                <td noWrap class="td">
                <INPUT TYPE="HIDDEN" NAME="xs__tdhwid" value="<%=xs__tdhwid%>">
                <input type="text" <%=zt.equals("1")?"class='edbox'":"class='edline'"%> <%=zt.equals("1")?"":"readonly"%> name="xstdbh" onKeyDown="return getNextElement();" value='<%=xsprodpRow.get("tdbh")%>' style="width:110"  >
                <%if(zt.equals("1")){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="afterselect()">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="xssl.value='';xstdrq.value='';xstdbh.value='';xs__tdhwid.value='';"><%}%>
                </td>
                <td noWrap class="tdTitle">返单日期</td>
                <td noWrap class="td">
                <input type="text" class="edline" name="xstdrq" onKeyDown="return getNextElement();" value='<%=xsprodpRow.get("tdrq")%>' style="width:110"  readonly >
                </td>
                <td noWrap class="tdTitle">返单数量</td>
                <td noWrap class="td">
                <input type="text" class="edline" name="xssl" onKeyDown="return getNextElement();" value='<%=xsprodpRow.get("sl")%>' style="width:110" readonly >
                </td>
            </tr>
            <tr>
                <td  noWrap class="tdTitle">反馈情况</td><%--其他信息--%>
                <td colspan="7" noWrap class="td"><textarea name="fkqk" <%=zt.equals("1")?"class='edbox'":"class='edline'"%> rows="3" onKeyDown="return getNextElement();" style="width:690" ><%=masterRow.get("fkqk")%></textarea></td>
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
               <%if(!b_NewProductRegisterBean.isApprove){%>
               <%if(!isEnd){%>
               <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存 ">
               <input name="button3"  style="width:50" type="button" class="button" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>);" value="删除(D)"><%}%>
              <%if(zt.equals("1")){%>
              <input name="button" type="button" class="button" onClick="sumitForm(<%=b_NewProductRegisterBean.AFTER_POST%>);" value=" 保存 "><%}%>
              <%if(!b_NewProductRegisterBean.isReport){%>
              <input name="btnback" type="button"  style="width:50" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script='<%="backList();"%>'/>
              <%}%>
              <%}%>
            </td>
          </tr>
        </table>
       </td>
    </tr>
  </table>
</form>
<script language="javascript">
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
  SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1&srcVar=wzdjid','fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fieldVar=wzdjid',obj.value);
}
//产品名称
function productNameSelect(obj)
{
  SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1&srcVar=wzdjid','fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fieldVar=wzdjid',obj.value);
}
//选择购货单位
function selectCustomer()
{
  CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=b_NewProductRegisterBean.DWTXID_CHANGE%>,-1)');
}
//客户编码
function customerCodeSelect(obj)
{
  CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value,'sumitForm(<%=b_NewProductRegisterBean.DWTXID_CHANGE%>,-1)');
}
//客户名称
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value,'sumitForm(<%=b_NewProductRegisterBean.DWTXID_CHANGE%>,-1)');
}
function selctbilloflading()
{
    OrderSingleSelect('form1','srcVar=seltdhwid','fieldVar=tdhwid',form1.cpid.value,"sumitForm(<%=b_NewProductRegisterBean.SELECT_PRODUCT%>,-1)");
}
function afterselect()
{
    OrderSingleSelect('form1','srcVar=seltdhwid','fieldVar=tdhwid',form1.cpid.value,"sumitForm(<%=b_NewProductRegisterBean.AFTER_SELECT_PRODUCT%>,-1)");
}
function OrderSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
  var winName= "OrdersSelector";
  paraStr = "../sale_xixing/newprodcut_selec_tdbh.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&cpid="+curID+"&dmsxid="+form1.dmsxid.value+"&dwtxid="+form1.dwtxid.value+"&deptid="+form1.deptid.value;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
  paraStr += "&notin="+notin;
  openSelectUrl(paraStr, winName, winopt2);
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
function propertiesInput(obj,cpid)
{
    PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid&srcVar=sxz', 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value, 'sumitForm(<%=b_NewProductRegisterBean.SXZ_CHANGE%>,-1)');
}
</script>
<%//&#$
if(b_NewProductRegisterBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
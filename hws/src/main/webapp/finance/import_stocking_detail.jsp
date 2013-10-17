<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.*"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
  String op_search = "op_search";
  String pageCode = "buy_invoice";
%><%
  engine.erp.finance.SelectBuyGoods SelectBuyGoodsBean = engine.erp.finance.SelectBuyGoods.getInstance(request);//创建实例
  String retu = SelectBuyGoodsBean.doService(request, response);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String operate = request.getParameter("operate");
  if(operate !=null && operate.equals(String.valueOf(Operate.INIT)))
  {
    srcFrm = request.getParameter("srcFrm");
    multiIdInput = request.getParameter("srcVar");
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = SelectBuyGoodsBean.getOneTable();
  ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("-1");
  ArrayList opval = new ArrayList(); opval.add("提货单"); opval.add("退货单");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String wldw=SelectBuyGoodsBean.dwtxid;
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

<script language="javascript">
function selectProduct(row)
{
  var multiId = '';
  for(var i=0;i<form1.elements.length;i++)
  {
    var e = form1.elements[i];
    if(e.type == "checkbox" && e.name!='checkform' && e.checked == true)
      multiId += e.value+',';//选中的checkbox的值组成的由逗号分隔的字符串
  }
  if(multiId.length > 0)
  {
    multiId += '-1';//-1结束
  <%
    if(multiIdInput != null)
    {
      String mutiId = "window.opener."+srcFrm+"."+multiIdInput;//源表单及其组件
      out.print(mutiId+".value=multiId;");
      out.print(mutiId+".onchange();");
    }
  %>
  }
  window.close();
}
function checkRadio(row)
{
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = !form1.sel.checked;
  else
    form1.sel[row].checked = !form1.sel[row].checked;
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD NOWRAP align="center">采购单货物明细</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td">
      <%
        String key = "97";
        pageContext.setAttribute(key, list);
        int iPage = loginBean.getPageSize();
        String pageSize = ""+iPage;
      %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right">
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value=" 选用 " onKeyDown="return getNextElement();">
      <%}%>
        <%if(hasSearchLimit){%>
        <input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询 " onKeyDown="return getNextElement();">
        <%}%>
        <input  type="button" class="button" onClick="window.close();" value=" 返回 " onKeyDown="return getNextElement();">
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>采购单编号</td>
      <td nowrap>客户类型</td>
      <td nowrap>交货日期</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>计量单位</td>
      <td nowrap>总数量</td>
      <td nowrap>未交数量</td>
      <td nowrap>单价</td>
      <td nowrap>销售价</td>
      <td nowrap>贡货商</td>
    </tr>
    <%
      prodBean.regData(list,"cpid");
      deptBean.regData(list,"deptid");
      corpBean.regData(list,"dwtxid");
      propertyBean.regData(list,"dmsxid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++){
        RowMap propertyRow = propertyBean.getLookupRow(list.getValue("dmsxID"));
    %>
    <tr>
    <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=list.getValue("jhdhwid")%>' onKeyDown="return getNextElement();"><input type="hidden" id="jhdhwid_<%=i%>" value='<%=list.getValue("jhdhwid")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jhdbm_<%=i%>" class="td"><%=list.getValue("jhdbm")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="khlx_<%=i%>" class="td"><%=list.getValue("khlx")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jhrq_<%=i%>" class="td"><%=list.getValue("jhrq")%></td>
      <td nowrap align="center" id="cpbm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("cpbm")%></td>
      <td nowrap align="center" id="product_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("product")%></td>
      <td nowrap align="center" id="sxz_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=propertyRow.get("sxz")%></td>
      <td nowrap align="center" id="jldw_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("jldw")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="zsl_<%=i%>" class="td"><%=list.getValue("zsl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sl_<%=i%>" class="td"><%=list.getValue("sl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="dj_<%=i%>" class="td"><%=list.getValue("dj")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="xsj_<%=i%>" class="td"><%=list.getValue("xsj")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="dwtxid_<%=i%>" class="td"><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td>
    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td"></td>
      <td nowrap class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
//------------------------------以下与查询相关
function sumitFixedQuery(oper)
{
  //执行查询,提交表单
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  //点击查询按钮打开查询对话框
  <%if(hasSearchLimit){%>
   showFrame('fixedQuery', true, "", true);//显示层
   <%}%>
}
</SCRIPT>
<%if(hasSearchLimit){%><%--查询权限--%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD nowrap class="td">交货单编码</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="jhdbm" name="jhdbm" value='<%=SelectBuyGoodsBean.getFixedQueryValue("jhdbm")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>供货单位</TD>
              <TD nowrap class="td">
                <%if(wldw.equals("")){%>
                <input type="hidden" name="dwtxid" value='<%=SelectBuyGoodsBean.getFixedQueryValue("dwtxid")%>'>
                <input type="text" name="buyerName" value='<%=SelectBuyGoodsBean.getFixedQueryValue("buyerName")%>' style="width:130" class="edline" readonly>
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=buyerName','fieldVar=dwtxid&fieldVar=dwmc',fixedQueryform.dwtxid.value)"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';buyerName.value='';">
                <%}else{%>
                <input type="hidden" name="dwtxid" value='<%=wldw%>'>
                <%}%>
            </TD>
            </TR>
            <TR>
              <TD nowrap class="td">交货日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="jhrq$a" value='<%=SelectBuyGoodsBean.getFixedQueryValue("jhrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jhrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="jhrq$b" value='<%=SelectBuyGoodsBean.getFixedQueryValue("jhrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jhrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
              <%=deptBean.getList(SelectBuyGoodsBean.getFixedQueryValue("deptid"))%></pc:select>
              </TD>
              <TD class="td" nowrap>提单类型</TD>
              <TD nowrap class="td">
                <pc:select name="djlx" addNull="1" style="width:130" >
                <%=SelectBuyGoodsBean.listToOption(lists, opkey.indexOf(SelectBuyGoodsBean.getFixedQueryValue("djlx")))%>
               </pc:select>
              </TD>
             </TR>
            <TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center">
                <% String ss = "sumitFixedQuery("+Operate.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script="<%=ss%>" />
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                 <pc:shortcut key="x" script="hideFrame('fixedQuery')" />              </TD>
            </TR>
          </TABLE>
       </TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%} out.print(retu);%>
</body>
</html>
<%--销售管理--客户产品折扣,定义具体客户要购买的产品的单价和折扣--%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.util.*"%>
<%
if(!loginBean.hasLimits("customer_product_discount", request, response))
    return;
  engine.erp.sale.xixing.B_CustomerProductDiscount b_customerproductdiscountBean = engine.erp.sale.xixing.B_CustomerProductDiscount.getInstance(request);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  String retu = b_customerproductdiscountBean.doService(request, response);//location.href='baln.htm'
  ArrayList opkey = new ArrayList(); opkey.add("ccj"); opkey.add("msj"); opkey.add("lsj"); opkey.add("qtjg1"); opkey.add("qtjg2");opkey.add("qtjg3");
  ArrayList opval = new ArrayList(); opval.add("出厂价"); opval.add("门市价"); opval.add("零售价"); opval.add("其他价格1"); opval.add("其他价格2");opval.add("其他价格3");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type"  content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript" src="../scripts/frame.js"></script>
<SCRIPT language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value  = row;
  form1.submit();
}
function tobachadd(oper, row)
{
  location.href='customer_productdiscount_batchadd.jsp?operate='+oper;
}
function showInterFrame(oper, rownum){
  var url = "customer_product_discountedit.jsp?operate="+oper+"&rownum="+rownum;
  document.all.interframe1.src = url;
  showFrame('detailDiv',true,"",true);
}
function hideInterFrame()//隐藏FRAME
{
  hideFrame('detailDiv');
  form1.submit();
}
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery()
{
  showFrame('fixedQuery',true,"",true);
}
function hideFrameNoFresh()
{
  hideFrame('detailDiv');
}
//客户编码
function customerCodeSelect(obj)
{
    CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户名称
function customerNameSelect(obj)
{
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//产品编码
function productCodeSelect(obj)
{
    SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',obj.value);
}
//产品名称
function productNameSelect(obj)
{
    SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',obj.value);
}
</SCRIPT>
<%
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = b_customerproductdiscountBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
%>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD nowrap align="center">客户产品折扣</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <TABLE width="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
     <td class="td" nowrap>
     <%
         String key = "ppdfsgg";
         pageContext.setAttribute(key, list);
         int iPage = loginBean.getPageSize();
         String pageSize = ""+iPage;
         %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
      <TD align="right">
        <INPUT class="button" onClick="tobachadd(<%=b_customerproductdiscountBean.BATCHINIT%>)" type="button" value="批量增加(Q)" name="Query" onKeyDown="return getNextElement();">
         <pc:shortcut key="b" script='<%="sumitForm("+b_customerproductdiscountBean.BATCHINIT+"))"%>' />
        <INPUT class="button" onClick="showFixedQuery()" type="button" value="查询(Q)" name="Query" onKeyDown="return getNextElement();">
         <pc:shortcut key="q" script="showFixedQuery()" />
         <%String s = "location.href='"+b_customerproductdiscountBean.retuUrl+"'";%>
        <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=b_customerproductdiscountBean.retuUrl%>'" value="返回(C)" border="0">
        <pc:shortcut key="c" script="<%=s%>" />
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=45>
      <% String add = "showInterFrame("+Operate.ADD+",-1)";%>
      <input name="image" class="img" type="image" title="新增(A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script="<%=add%>" />
      </td>
      <td nowrap>客户代码</td>
      <td nowrap>客户</td>
      <td nowrap>产品代码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>单位</td>
      <td nowrap>折扣(%)</td>
      <td nowrap>定价类型</td>
      <td nowrap>备注</td>
      </tr>
      <%
      corpBean.regData(list, "dwtxid");
      prodBean.regData(list,"cpid");
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)
      {
        RowMap corpRow = corpBean.getLookupRow(list.getValue("dwtxid"));
        RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));
      %>
      <tr onclick="selectRow()" onclick="selectRow();showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td" nowrap width=45>
      <input name="image" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      </td>
      <td class="td" nowrap><%=corpRow.get("dwdm")%></td>
      <td class="td" nowrap><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td>
      <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodRow.get("product")%></td><!--品名 规格-->
      <td class="td" nowrap><%=prodRow.get("jldw")%></td><!--计量单位-->
      <td class="td" nowrap><%=list.getValue("zk")%></td>
      <td class="td" nowrap align="right">
      <%
       int index =  opkey.indexOf(list.getValue("djlx"));
       out.print(index==-1?"":opval.get(index));
      %>
      </td>
      <td class="td" nowrap><%=list.getValue("bz")%></td>
      </tr>
      <%
        list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
      %>
      <tr>
      <td class="td" nowrap>&nbsp;</td>
       <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="0">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD align="center" nowrap class="td">往来单位</TD>
              <td nowrap class="td" colspan=3>
              <input type="text" name="dwdm" class="edbox" style="width:70" onKeyDown="return getNextElement();"  value='<%=b_customerproductdiscountBean.dwdm%>' onchange="customerCodeSelect(this)" >
              <input type="text" name="dwmc"  style="width:260" value='<%=b_customerproductdiscountBean.dwmc%>' class="edbox"  onchange="customerNameSelect(this)" >
              <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=b_customerproductdiscountBean.getFixedQueryValue("dwtxid")%>'>
              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)">
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwdm.value='';dwmc.value='';">
              </td>
             </tr>
            <TR>
                <TD align="center" nowrap class="td">产品代码</TD>
                <td nowrap class="td" colspan="3">
                <INPUT TYPE="HIDDEN" NAME="cpid" value="">
                <input class="edbox" style="WIDTH:70" name="cpbm" value='<%=b_customerproductdiscountBean.cpbm%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)" >
                <INPUT class="edbox" style="WIDTH:260" id="product" name="product" value='<%=b_customerproductdiscountBean.product%>' maxlength='10' onKeyDown="return getNextElement();"  onchange="productNameSelect(this)">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="SaleProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=product&srcVar=cpbm&issale=1','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',fixedQueryform.cpid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="fixedQueryform.cpid.value='';fixedQueryform.product.value='';fixedQueryform.cpbm.value='';">
                </td>
            </tr>
             <TR>
              <td align="center" nowrap class="td">折扣</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH: 60px" name="zk$a" value='<%=b_customerproductdiscountBean.getFixedQueryValue("dj$a")%>'  onKeyDown="return getNextElement();">--
              <INPUT class="edbox"  style="WIDTH: 60px" name="zk$b" value='<%=b_customerproductdiscountBean.getFixedQueryValue("dj$b")%>'  onKeyDown="return getNextElement();">
              </td>
            </TR>
            </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center">
               <%String q = "sumitFixedQuery("+b_customerproductdiscountBean.FIXED_SEARCH+")";%>
               <INPUT class="button" onClick="sumitFixedQuery(<%=b_customerproductdiscountBean.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script="<%=q%>" />
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script="hideFrame('fixedQuery')" />
              </TD>
            </TR>
    </TABLE>
  </DIV>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="500" height="250" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>
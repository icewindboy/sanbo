<%--销售管理--客户产品折扣,定义具体客户要购买的产品的单价和折扣--%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%
if(!loginBean.hasLimits("customer_product_discount", request, response))
    return;
  engine.erp.sale.B_CustomerProductDiscount b_customerproductdiscountBean = engine.erp.sale.B_CustomerProductDiscount.getInstance(request);
   //engine.project.SysConstant.BEAN_CORP 外来单位JAVABEAN
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  //engine.project.SysConstant.BEAN_PRODUCT  存货信息JAVABEAN
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性

  String retu = b_customerproductdiscountBean.doService(request, response);//location.href='baln.htm'
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

<SCRIPT language="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value  = row;
    form1.submit();
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
      <td nowrap>客户</td>
      <td nowrap>品名 规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>单位</td>
      <td nowrap>客户产品编号</td>
      <td nowrap>单价</td>
      <td nowrap>折扣(%)</td>
      <td nowrap>报价</td>
      <td nowrap>奖金比率(%)</td>
      </tr>
      <%
      corpBean.regData(list, "dwtxid");
      prodBean.regData(list,"cpid");
      propertyBean.regData(list,"dmsxID");
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {

      %>
      <tr onclick="selectRow()" onDblClick="selectRow();showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td" nowrap width=45>
      <input name="image" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      </td>
      <td class="td" nowrap><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td>
      <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td class="td" nowrap><%=prodRow.get("product")%></td><!--品名 规格-->
      <td class="td" nowrap><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
      <td class="td" nowrap><%=prodRow.get("jldw")%></td><!--计量单位-->
      <td class="td" nowrap><%=list.getValue("khcpdm")%></td>
      <td class="td" nowrap><%=list.getValue("dj")%></td>
      <td class="td" nowrap><%=list.getValue("zk")%></td>
      <td class="td" nowrap><%=list.getValue("bj")%></td>
      <td class="td" nowrap><%=list.getValue("jjbl")%></td>
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
              <TD align="center" nowrap class="td">客户</TD>
              <TD class="td" nowrap>
                <input class="EDLine" style="WIDTH:130px" name="dwmc" value='' onKeyDown="return getNextElement();">
                <input type="hidden" name="dwtxid" value=""> <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc','fieldVar=dwtxid&fieldVar=dwmc',fixedQueryform.dwtxid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';">
              </TD>
              <TD align="center" nowrap class="td">品名 规格</TD>
               <td nowrap class="td">
                <input class="EDLine" style="WIDTH:130px" name="pm" value='' onKeyDown="return getNextElement();">
                <INPUT TYPE="HIDDEN" NAME="cpid" value="">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=pm','fieldVar=cpid&fieldVar=pm',fixedQueryform.cpid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';pm.value='';">
            </td>
            </TR>
             <TR>
              <td align="center" nowrap class="td">客户产品编号</td>
              <td class="td" nowrap><INPUT class="edbox" id="khcpdm" style="WIDTH: 130px" name="khcpdm" value='<%=b_customerproductdiscountBean.getFixedQueryValue("khcpdm")%>' onKeyDown="return getNextElement();">
              </td>
              <td align="center" nowrap class="td">单价</td>
              <td class="td" nowrap>
              <INPUT class="edbox" style="WIDTH: 60px" name="dj$a" value='<%=b_customerproductdiscountBean.getFixedQueryValue("dj$a")%>' onKeyDown="return getNextElement();">--
              <INPUT class="edbox" id="dj$b" style="WIDTH: 60px" name="dj" value='<%=b_customerproductdiscountBean.getFixedQueryValue("dj$b")%>' onKeyDown="return getNextElement();"></td>
            </TR>
             <TR>
              <td align="center" nowrap class="td">折扣</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH: 60px" name="zk$a" value='<%=b_customerproductdiscountBean.getFixedQueryValue("dj$a")%>'  onKeyDown="return getNextElement();">--
              <INPUT class="edbox"  style="WIDTH: 60px" name="zk$b" value='<%=b_customerproductdiscountBean.getFixedQueryValue("dj$b")%>'  onKeyDown="return getNextElement();">
              </td>
              <td align="center" nowrap class="td">报价</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH: 60px" name="bj$a" value='<%=b_customerproductdiscountBean.getFixedQueryValue("bj$a")%>'  onKeyDown="return getNextElement();">--
              <INPUT class="edbox"  style="WIDTH: 60px" name="bj$b" value='<%=b_customerproductdiscountBean.getFixedQueryValue("bj$b")%>'  onKeyDown="return getNextElement();">
              </td>
            </TR>
             <TR>
              <td align="center" nowrap class="td">奖金比例</td>
              <td class="td" nowrap>
              <INPUT class="edbox"  style="WIDTH: 60px" name="jjbl$a" value='<%=b_customerproductdiscountBean.getFixedQueryValue("jjbl$a")%>'  onKeyDown="return getNextElement();">--
              <INPUT class="edbox"  style="WIDTH: 60px" name="jjbl$b" value='<%=b_customerproductdiscountBean.getFixedQueryValue("jjbl$b")%>'  onKeyDown="return getNextElement();">
              </td>
              <td align="center" nowrap class="td"></td>
              <td class="td" nowrap>
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
      <TD><iframe id="interframe1" src="" width="320" height="325" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>
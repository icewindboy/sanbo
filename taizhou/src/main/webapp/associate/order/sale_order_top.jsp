<%@page contentType="text/html; charset=UTF-8" %>
<%request.setCharacterEncoding("UTF-8");
  //session.setMaxInactiveInterval(900);
  response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","max-age=0");
  response.addHeader("Cache-Control","pre-check=0");
  response.addHeader("Cache-Control","post-check=0");
  response.setDateHeader("Expires", 0);
  engine.common.OtherLoginBean loginBean = engine.common.OtherLoginBean.getInstance(request);
  if(!loginBean.isLogin(request, response))
    return;
%><%@ taglib uri="/WEB-INF/pagescontrol.tld" prefix="pc"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_cancel = "op_cancel";
  String op_approve = "op_approve";
%><%
  engine.erp.sale.B_OtherSaleOrder saleOrderBean = engine.erp.sale.B_OtherSaleOrder.getInstance(request);
  String pageCode = "sale_order_list";

  boolean hasSearchLimit = true;//loginBean.hasLimits(pageCode, op_search);
  synchronized(saleOrderBean){
  String retu = saleOrderBean.doService(request, response);
  engine.project.LookUp deptBean = engine.project.OtherLookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp personBean = engine.project.OtherLookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);

%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/scripts/public.css" type="text/css">
</head>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/validate.js"></script>

<script language="javascript">
function toDetail(){
  parent.location.href='sale_order_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='sale_order_bottom.jsp?operate=<%=saleOrderBean.EDIT%>&rownum='+masterRow;
  unlockScreenWait();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function openrelationwin(id,bh)
{
  parent.location.href="sale_order_relation.jsp?operate=0&id="+id+"&bh="+bh;
}
function showInterFrame(){
  paraStr =  "sale_order_unfinished.jsp?operate=0";
  openSelectUrl(paraStr, "SingleCustSelector", winopt2);
}
function hideInterFrame(){
  //隐藏FRAME
  hideFrame('detailDiv');
  form1.submit();
}
function hideFrameNoFresh(){
  hideFrame('detailDiv');
  }

function customerCodeSelect(obj)
{
  //客户编码
  CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}

function customerNameSelect(obj)
{
  //客户名称
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
function showFixedQuery(){
  showFrame('fixedQuery', true, "", true);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">订单列表</TD>
  </TR></TABLE>
<%
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);//显示明细或返回
    return;
  }
  EngineDataSet list = saleOrderBean.getMaterList();
  String curUrl = request.getRequestURL().toString();
  boolean isProductInvoke = saleOrderBean.isProductInvoke;//生产里调用
%>
<%
  String key = "datasetlist";
  pageContext.setAttribute(key, list);
  int iPage = loginBean.getPageSize();
  String pageSize = String.valueOf(iPage);
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
      </td>
      <td class="td" nowrap align="right">

       <%if(hasSearchLimit){%>
       <input name="search2" type="button" class="button"  style="width:60"  onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
       <pc:shortcut key="q" script="showFixedQuery()" />
       <%}%>
        <%if(saleOrderBean.retuUrl!=null){
          String s = "parent.location.href='"+saleOrderBean.retuUrl+"'";
           %>
         <input name="button22" type="button" class="button"  style="width:60"  onClick="parent.location.href='<%=saleOrderBean.retuUrl%>'" value=" 返回(C) " onKeyDown="return getNextElement();">
         <pc:shortcut key="c" script="<%=s%>" />
        <%}%>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td  align="center">
      <%if(!isProductInvoke){
       String add = "sumitForm("+Operate.ADD+",-1)";
       %>
        <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="<%=request.getContextPath() %>/images/add_big.gif" border="0">
        <pc:shortcut key="a" script='<%=add%>'/>
      <%}%>
      </td>
      <td nowrap height='20'>订单编号</td>
      <td nowrap height='20'>制单日期</td>
      <td nowrap height='20'>客户类型</td>
      <td nowrap height='20'>总数量</td>
      <td nowrap height='20'>总金额</td>
      <td nowrap height='20'>其他信息</td><!--打印表头-->
    </tr>
    <%
      BigDecimal t_zsl = new BigDecimal(0), t_zje = new BigDecimal(0);
      int i=0;
      int count = list.getRowCount();
      list.first();
      for(; i<count; i++)   {
 
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0")){
          isInit = true;
        }
        String zsl = list.getValue("zsl");
        if(saleOrderBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        String zje = list.getValue("zje");
        if(saleOrderBean.isDouble(zje))
          t_zje = t_zje.add(new BigDecimal(zje));
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String loginId=saleOrderBean.loginId;
        String sprid=list.getValue("sprid");
        String rowzt =list.getValue("zt");
        String czyid = list.getValue("czyid");
        String htid = list.getValue("htid");
        String htbh = "'"+list.getValue("htbh")+"'";
        boolean isCancer=rowzt.equals("1") && loginId.equals(sprid);
        boolean cancer=rowzt.equals("0")||rowzt.equals("8")||rowzt.equals("4");
        boolean iscanDepose=saleOrderBean.isCanCancel(htid);

        boolean cansubmit=false;
        boolean submitType = saleOrderBean.submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
        if(submitType&&czyid.equals(loginId))
          cansubmit=true;
        else if(!submitType)
          cansubmit=true;
        String dbl = "onDblClick=sumitForm("+Operate.EDIT+","+list.getRow()+")";
        if(isProductInvoke)
          dbl="";
    %>
    <tr id="tr_<%=list.getRow()%>" <%=dbl%> onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap>
      <%if(!isProductInvoke){%>
      <input name="image2" class="img" type="image" title='修改' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%if(isInit&&cansubmit){%><input name="image3" class="img" type="image"  title='提交审批' onClick="if(confirm('确认要提交吗？'))sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
      <%if(isCancer){%>
         <input name="image3" class="img" type="image"  title='取消审批' onClick="if(confirm('确认要取消审批吗？'))sumitForm(<%=saleOrderBean.CANCER_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
     <%if(!rowzt.equals("9")&&!rowzt.equals("4")&&iscanDepose&&!rowzt.equals("8")&&loginId.equals(czyid)){%>
         <input name="image3" class="img" type="image"   title='作废' onClick="if(confirm('确实要做废吗？'))sumitForm(<%=saleOrderBean.SALE_CANCER%>,<%=htid%>)" src="../images/close.gif" border="0"><%}%>
     <%
       if(rowzt.equals("1")){
      %>
         <input name="image3" class="img" type="image"   title='完成' onClick="if(confirm('确认要完成吗？'))sumitForm(<%=saleOrderBean.SALE_OVER%>,<%=htid%>)" src="../images/ok.gif" border="0"><%}%>
     <%}%>
     </td>
      <td class="td" nowrap height='20'><%=list.getValue("htbh") %></td>
      <td class="td" nowrap height='20'><%=list.getValue("czrq") %></td>
      <td class="td" nowrap height='20'><%=list.getValue("khlx") %></td>
      <td class="td" nowrap height='20' align="right" ><%=list.getValue("zsl") %></td>
      <td class="td" nowrap height='20' align="right" ><%=list.getValue("zje") %></td>
      <td class="td" nowrap height='20'><%=list.getValue("qtxx") %></td>
       
    </tr>
    <%
      list.next();
      }
      i=count+1;
    %>
      <tr>
      <td class="tdTitle" nowrap>本页合计</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zje%>' readonly></td>

       <td class="td" nowrap></td>
      </tr>
      <tr>
      <td class="tdTitle" nowrap>总合计</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=saleOrderBean.getZsl()%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=saleOrderBean.getZje()%>' readonly></td>

       <td class="td" nowrap></td>
      </tr>
    <%
      for(; i < iPage; i++){
        out.print("<tr>");
        out.print("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");
        out.print("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");


        out.print("</tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%if(!saleOrderBean.masterIsAdd()){
    int row = saleOrderBean.getSelectedRow();
    if(row >= 0)
      out.print("showSelected('tr_"+ row +"');");
  }%>
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function sumitSearchFrame(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  searchform.operate.value = oper;
  searchform.submit();
}
function showSearchFrame(){
  hideFrame('fixedQuery');
  showFrame('searchframe', true, "", true);
}
function openwin(rownum)
{
  paraStr = "bill_edit.jsp?operate=0&htid="+rownum;
  openSelectUrl(paraStr, "NewLadingBill", winopt2);
}
</SCRIPT>
<%if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD nowrap class="td">合同日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="htrq$a" value='<%=saleOrderBean.getFixedQueryValue("htrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(htrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="htrq$b" style="WIDTH: 130px" name="htrq$b" value='<%=saleOrderBean.getFixedQueryValue("htrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(htrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap class="td">有效期始</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="ksrq$a" value='<%=saleOrderBean.getFixedQueryValue("ksrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(ksrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="ksrq$b" value='<%=saleOrderBean.getFixedQueryValue("ksrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(ksrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap class="td">有效期止</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="jsrq$a" value='<%=saleOrderBean.getFixedQueryValue("jsrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jsrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="jsrq$b" value='<%=saleOrderBean.getFixedQueryValue("jsrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jsrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
              <TD nowrap class="td">制单日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="czrq$a" value='<%=saleOrderBean.getFixedQueryValue("czrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(czrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="czrq$b" value='<%=saleOrderBean.getFixedQueryValue("czrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(czrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>合同编号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="htbh$a" name="htbh$a" value='<%=saleOrderBean.getFixedQueryValue("htbh$a")%>' maxlength='13' onKeyDown="return getNextElement();"></TD>
              <TD nowrap class="td">--</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="htbh$b" name="htbh$b" value='<%=saleOrderBean.getFixedQueryValue("htbh$b")%>' maxlength='13' onKeyDown="return getNextElement();"></TD>
            </TR>
            <tr>
            <TD class="td" nowrap>产品编码</TD>
            <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="htid$cpbm$a" name="htid$cpbm$a" value='<%=saleOrderBean.getFixedQueryValue("htid$cpbm$a")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
            <TD nowrap class="td">--</TD>
            <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="htid$cpbm$b" name="htid$cpbm$b" value='<%=saleOrderBean.getFixedQueryValue("htid$cpbm$b")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
            </tr>
            <TR>
              <TD class="td" nowrap>品名</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="htid$pm" name="htid$pm" value='<%=saleOrderBean.getFixedQueryValue("htid$pm")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="htid$gg" name="htid$gg" value='<%=saleOrderBean.getFixedQueryValue("htid$gg")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
            </TR>


            <TR>
              <TD nowrap colspan=5 height=30 align="center">
                <% String ss = "sumitFixedQuery("+Operate.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                 <pc:shortcut key="x" script="hideFrame('fixedQuery')" />
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="800" height="300" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<% out.print(retu);}}%>
</body>
</html>
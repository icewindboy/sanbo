<%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_cancel = "op_cancel";
  String op_approve = "op_approve";
%><%
  engine.erp.jit.B_SaleOrderLlist saleOrderListBean = engine.erp.jit.B_SaleOrderLlist.getInstance(request);
  String pageCode = "sale_order";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  synchronized(saleOrderListBean){
  String retu = saleOrderListBean.doService(request, response);
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
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
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='sale_order_list_bottom.jsp?operate=<%=saleOrderListBean.SHOW_DETAIL%>&rownum='+masterRow;
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
    <TD NOWRAP align="center">销售合同列表</TD>
  </TR></TABLE>
<%
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);//显示明细或返回
    return;
  }
  EngineDataSet list = saleOrderListBean.getMaterTable();
  HtmlTableProducer table = saleOrderListBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  boolean isProductInvoke = true;
%>
<%
  String key = "salerorder";
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
        <%if(!isProductInvoke&&!saleOrderListBean.retuUrl.equals("")){
          String s = "parent.location.href='"+saleOrderListBean.retuUrl+"'";
           %>
         <input name="button22" type="button" class="button"  style="width:60"  onClick="parent.location.href='<%=saleOrderListBean.retuUrl%>'" value=" 返回(C) " onKeyDown="return getNextElement();">
         <pc:shortcut key="c" script="<%=s%>" />
        <%}%>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td  align="center">
      <%if(loginBean.hasLimits(pageCode, op_add)&&!isProductInvoke){
       String add = "sumitForm("+Operate.ADD+",-1)";
       %>
        <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
        <pc:shortcut key="a" script='<%=add%>'/>
      <%}%>
      </td>
      <%table.printTitle(pageContext, "height='20'");%><!--打印表头-->
    </tr>
    <%
      BigDecimal t_zsl = new BigDecimal(0), t_zje = new BigDecimal(0);
      int i=0;
      int count = list.getRowCount();
      list.first();
      for(; i<count; i++)   {
        //&#$
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0")){
          isInit = true;
        }
        String zsl = list.getValue("zsl");
        if(saleOrderListBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        String zje = list.getValue("zje");
        if(saleOrderListBean.isDouble(zje))
          t_zje = t_zje.add(new BigDecimal(zje));
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String loginid=saleOrderListBean.loginid;
        String sprid=list.getValue("sprid");
        String rowzt =list.getValue("zt");
        String czyid = list.getValue("czyid");
        String htid = list.getValue("htid");
        String htbh = "'"+list.getValue("htbh")+"'";
        boolean isCancer=rowzt.equals("1") && loginid.equals(sprid);
        boolean cancer=rowzt.equals("0")||rowzt.equals("8")||rowzt.equals("4");
        boolean iscanDepose=false;

        boolean cansubmit=false;
        boolean submitType = false;//用于判断true=仅制定人可提交,false=有权限人可提交
        if(submitType&&czyid.equals(loginid))
          cansubmit=true;
        else if(!submitType)
          cansubmit=true;
        String dbl = "onDblClick=sumitForm("+Operate.EDIT+","+list.getRow()+")";
        if(isProductInvoke)
          dbl="";
    %>
    <tr id="tr_<%=list.getRow()%>" <%=dbl%> onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap>
     </td>
      <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%
      list.next();
      }
      for(; i < iPage; i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%if(!saleOrderListBean.masterIsAdd()){
    int row = saleOrderListBean.getSelectedRow();
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
              <TD class="td" nowrap>合同编号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="htbh$a" name="htbh$a" value='<%=saleOrderListBean.getFixedQueryValue("htbh$a")%>' maxlength='13' onKeyDown="return getNextElement();"></TD>
              <TD nowrap class="td">--</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="htbh$b" name="htbh$b" value='<%=saleOrderListBean.getFixedQueryValue("htbh$b")%>' maxlength='13' onKeyDown="return getNextElement();"></TD>
            </TR>
            <TR>
              <TD nowrap class="td">计划发货时间</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="jhfhrq$a" value='<%=saleOrderListBean.getFixedQueryValue("jhfhrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jhfhrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="jhfhrq$b" value='<%=saleOrderListBean.getFixedQueryValue("jhfhrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jhfhrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap class="td">合同日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="htrq$a" value='<%=saleOrderListBean.getFixedQueryValue("htrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(htrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="htrq$b" style="WIDTH: 130px" name="htrq$b" value='<%=saleOrderListBean.getFixedQueryValue("htrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(htrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD align="center" nowrap class="td">客户名称</TD>
              <td nowrap class="td" colspan=3>
              <input type="text" class="edbox" style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=saleOrderListBean.getFixedQueryValue("dwdm")%>' onchange="customerCodeSelect(this)" >
              <input type="text" name="dwmc"  style="width:260" value='<%=saleOrderListBean.getFixedQueryValue("dwmc")%>' class="edbox"  onchange="customerNameSelect(this)" >
              <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=saleOrderListBean.getFixedQueryValue("dwtxid")%>'>
              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)">
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
              </td>
             </TR>
             <TR>
              <TD nowrap class="td">制单日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="czrq$a" value='<%=saleOrderListBean.getFixedQueryValue("czrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(czrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="czrq$b" value='<%=saleOrderListBean.getFixedQueryValue("czrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(czrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:130">
             <%=deptBean.getList(saleOrderListBean.getFixedQueryValue("deptid"))%></pc:select>
            </TD>
              <TD class="td" nowrap>业务员</TD>
              <TD class="td" nowrap>
                <pc:select name="personid" addNull="1" style="width:100">
                  <%=personBean.getList(saleOrderListBean.getFixedQueryValue("personid"))%>
                </pc:select>
               </TD>
             </tr>
              <tr>
              <TD class="td" nowrap>审批人</TD>
              <TD class="td" nowrap>
                <pc:select name="sprid" addNull="1" style="width:100">
                  <%=personBean.getList(saleOrderListBean.getFixedQueryValue("sprid"))%>
                </pc:select>
             </TD>
              <TD class="td" nowrap>制单人</TD>
              <TD class="td" nowrap>
              <INPUT class="edbox" style="WIDTH:160" id="czy" name="czy" value='<%=saleOrderListBean.getFixedQueryValue("czy")%>' maxlength='16' onKeyDown="return getNextElement();">
             </TD>
             </tr>
            <TR>
              <TD class="td" nowrap>审核情况</TD>
              <TD colspan="3" nowrap class="td">
                <%
                  String [] zt = saleOrderListBean.zt;
                  String zt0="";
                  String zt9="";
                  String zt1="";
                  String zt8="";
                  String zt4="";
                  for(int k=0;k<zt.length;k++)
                  {
                    if(zt[k].equals("0"))
                      zt0 = "checked";
                    else if(zt[k].equals("9"))
                      zt9 = "checked";
                    else if(zt[k].equals("1"))
                      zt1 = "checked";
                    else if(zt[k].equals("8"))
                      zt8 = "checked";
                    else if(zt[k].equals("4"))
                      zt4 = "checked";
                  }
                %>
                <%if(!isProductInvoke){%><input type="checkbox" name="zt" value="0" <%=zt0%>>未审
                <input type="checkbox" name="zt" value="9" <%=zt9%>>审批中<%}%>
                <input type="checkbox" name="zt" value="1" <%=zt1%>>已审
                <input type="checkbox" name="zt" value="8" <%=zt8%>>完成
                <input type="checkbox" name="zt" value="4" <%=zt4%>>作废
            </TD>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center">
                <% String ss = "sumitFixedQuery("+Operate.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script="<%=ss%>" />
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
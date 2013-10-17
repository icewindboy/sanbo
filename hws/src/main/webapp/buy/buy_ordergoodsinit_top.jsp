<%--采购进货单列表--%>
<%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal, java.util.ArrayList,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over   = "op_over";
  String op_instore ="op_instore";
%><%
  engine.erp.buy.B_BuyOrderGoodsInit buyOrderGoodsInitBean = engine.erp.buy.B_BuyOrderGoodsInit.getInstance(request);
  String pageCode = "buy_ordergoodsinit";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  boolean hasInstore = loginBean.hasLimits(pageCode, op_instore);
  //String SYS_APPROVE_ONLY_SELF =buyOrderGoodsInitBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  //boolean isApproveOnly = SYS_APPROVE_ONLY_SELF.equals("1") ? true : false;//true仅制单人可提交
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
function toDetail(){
  parent.location.href='buy_ordergoodsinit_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='buy_ordergoodsinit_bottom.jsp?operate=<%=buyOrderGoodsInitBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">采购货物初始化</TD>
  </TR></TABLE>
<%String retu = buyOrderGoodsInitBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  EngineDataSet list = buyOrderGoodsInitBean.getMaterTable();
  HtmlTableProducer table = buyOrderGoodsInitBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  RowMap masterRow = buyOrderGoodsInitBean.getMasterRowinfo();
  String djlx = masterRow.get("djlx");
  String loginId = buyOrderGoodsInitBean.loginId;
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);
      int iPage = loginBean.getPageSize()-7; String pageSize = String.valueOf(iPage);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
    <td class="td" nowrap align="right">
        <%
          if(loginBean.hasLimits(pageCode, op_add)){
          String addinit = "sumitForm("+buyOrderGoodsInitBean.LADING_INIT+",-1)";
          String initOver = "if(confirm('完成后不能再修改,确实要完成吗？'))sumitForm("+buyOrderGoodsInitBean.LADING_INIT_OVER+","+list.getRow()+")";
        %>
        <%if(buyOrderGoodsInitBean.sys_init.equals("0")){%>
        <input name="addtd" class="button" type="button" value="完成初始化(V)" onClick="<%=initOver%>" onKeyDown="return getNextElement();">&nbsp;&nbsp;
        <pc:shortcut key="v" script="<%=initOver%>"/>

        <input name="addtd" class="button" type="button" value="新增初始数据(I)" onClick="sumitForm(<%=buyOrderGoodsInitBean.LADING_INIT%>,-1)" onKeyDown="return getNextElement();">&nbsp;&nbsp;
        <pc:shortcut key="i" script="<%=addinit%>"/><%}%>
        <%}if(hasSearchLimit){%>
         <input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <%if(buyOrderGoodsInitBean.retuUrl!=null){%>
         <input name="button22" type="button" class="button" onClick="parent.location.href='<%=buyOrderGoodsInitBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="parent.location.href='"+buyOrderGoodsInitBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
       </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=30 align="center"></td>
      <%table.printTitle(pageContext, "height='20'");%></td>
     </tr>
    <%BigDecimal t_zsl = new BigDecimal(0), t_zje = new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++)   {
        list.goToRow(i);

        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0"))
          isInit = true;

        String czyid = list.getValue("czyid");

        String jhdid = list.getValue("jhdid");
        boolean isReference = buyOrderGoodsInitBean.isReference(jhdid);//进货单是否被入库，如果入库颜色要变化
        rowClass = engine.action.BaseAction.getStyleName(isReference ? "-1" : rowClass);
        String zt = list.getValue("zt");
        String sprid = list.getValue("sprid");
        boolean isCancel = zt.equals("1");
        boolean isComplete  = zt.equals("2") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
        isCancel =  isCancel && loginId.equals(sprid);//是否可以取消审批
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
    <td nowrap width=30 align="center">
      <%if(zt.equals("1") && hasInstore){%><input name="image3" class="img" type="image" title='入库确认' onClick="if(confirm('是否入库确认？'))sumitForm(<%=buyOrderGoodsInitBean.INSTORE_CONFIRM%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>
     </td>
   <%table.printCells(pageContext, rowClass);%>
    </tr>
    <% list.next();
      }
      i=count+1;
     %>

      <%
      for(; i < iPage; i++){
        out.print("<tr><td>&nbsp;</td>");
        table.printBlankCells(pageContext, "class=td");//打印空的格子
        out.print("</tr>");
      }
    %>
  </table>
</form>
<script LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%if(!buyOrderGoodsInitBean.masterIsAdd()){
    int row = buyOrderGoodsInitBean.getSelectedRow();
    if(row >= 0)
      out.print("showSelected('tr_"+ row +"');");
  }%>
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  <%if(hasSearchLimit){%>showFrame('fixedQuery', true, "", true);<%}%>
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
function hide()
{
  hideFrame('fixedQuery');
}
</script>
<%if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
           <TR>
            <%
             ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("-1");
             ArrayList opval = new ArrayList(); opval.add("进货单"); opval.add("退货单");
             ArrayList[] lists  = new ArrayList[]{opkey, opval};
            %>
              <TD class="td" nowrap>单据号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="jhdbm" name="jhdbm" value='<%=buyOrderGoodsInitBean.getFixedQueryValue("jhdbm")%>' maxlength='32' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>单据类型</TD>
              <TD nowrap class="td">
                <pc:select name="djlx" addNull="1" style="width:160" >
                <%=buyOrderGoodsInitBean.listToOption(lists, opkey.indexOf(buyOrderGoodsInitBean.getFixedQueryValue("djlx")))%>
               </pc:select>
              </TD>
            </TR>
            <TR>
              <TD nowrap class="td">交货日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="jhrq$a" style="WIDTH: 130px" name="jhrq$a" value='<%=buyOrderGoodsInitBean.getFixedQueryValue("jhrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jhrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center"></TD>
              <TD class="td" nowrap><INPUT class="edbox" id="jhrq$b" style="WIDTH: 130px" name="jhrq$b" value='<%=buyOrderGoodsInitBean.getFixedQueryValue("jhrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jhrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
              <TR>
              <TD class="td" nowrap>供货单位</TD>
              <TD nowrap class="td"><input type="hidden" name="dwtxid" value='<%=buyOrderGoodsInitBean.getFixedQueryValue("dwtxid")%>'>
                <input type="text" name="buyerName" value='<%=buyOrderGoodsInitBean.getFixedQueryValue("dwtxid")%>' style="width:130" class="edline" readonly>
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=buyerName','fieldVar=dwtxid&fieldVar=dwmc',fixedQueryform.dwtxid.value)"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';buyerName.value='';"></TD>
               <TD class="td" nowrap>承运单位</TD>
                <TD nowrap class="td"><input type="hidden" name="dwt_dwtxid" value='<%=buyOrderGoodsInitBean.getFixedQueryValue("dwt_dwtxid")%>'>
                <input type="text" name="transName" value='<%=buyOrderGoodsInitBean.getFixedQueryValue("dwt_dwtxid")%>' style="width:130" class="edline" readonly>
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="TransportSingleSelect('fixedQueryform','srcVar=dwt_dwtxid&srcVar=transName','fieldVar=dwtxid&fieldVar=dwmc',fixedQueryform.dwtxid.value)"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwt_dwtxid.value='';transName.value='';"></TD>
              </TR>
               <TR>
              <TD align="center" nowrap class="td">产品名称</TD>
               <td nowrap class="td" colspan="3"><input class="EDLine" style="WIDTH:330px" name="product" value='<%=buyOrderGoodsInitBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="jhdid" value="<%=buyOrderGoodsInitBean.getFixedQueryValue("jhdid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=jhdid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.jhdid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="jhdid.value='';product.value='';">
            </td>
            </TR>
             <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="jhdid$cpbm" name="jhdid$cpbm" value='<%=buyOrderGoodsInitBean.getFixedQueryValue("jhdid$cpbm")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="jhdid$product" name="jhdid$product" value='<%=buyOrderGoodsInitBean.getFixedQueryValue("jhdid$product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
               <TR>
               <TD nowrap class="td">仓库</TD>
              <TD nowrap class="td"><pc:select name="storeid" addNull="1" style="width:160">
              <%=storeBean.getList(buyOrderGoodsInitBean.getFixedQueryValue("storeid"))%></pc:select>
              </TD>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
         <%=deptBean.getList(buyOrderGoodsInitBean.getFixedQueryValue("deptid"))%></pc:select>
           </TD>
             </TR>
            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = buyOrderGoodsInitBean.getFixedQueryValue("zt");%>
                <input type="radio" name="zt" value=""<%=zt.equals("")?" checked" :""%>>
                全部
                <input type="radio" name="zt" value="2"<%=zt.equals("2")?" checked" :""%>>
                已入库
                <%--input type="radio" name="zt" value="8"<%=zt.equals("8")?" checked" :""%>>
                完成--%>
                </TD>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
            <pc:shortcut key="f" script='<%="sumitFixedQuery("+ Operate.FIXED_SEARCH +",-1)"%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
                 <pc:shortcut key="x" script='hide();'/>
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%} out.print(retu);%>
</body>
</html>

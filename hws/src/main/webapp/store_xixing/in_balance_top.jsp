<%--库存移库库单列表--%>
<%
/**
 * 02.28 19:45 新增  新增下面的这个新增的图标.因为原来不小心改程序的时候去掉了. 现在在加上了. yjg
 */
%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_cancel = "op_cancel";
  String op_record = "op_record";
%><%
  engine.erp.store.xixing.B_InBalance inBalanceBean = engine.erp.store.xixing.B_InBalance.getInstance(request);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  engine.project.LookUp moveBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_MOVE);
  //系统参数：SYS_APPROVE_ONLY_SELF 用于判断1=仅制定人可提交,0=有权限人可提交
  boolean isZdridApprove = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF").equals("1")?true:false;
  String pageCode = "in_balance_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  boolean hasRecord = loginBean.hasLimits(pageCode, op_record);
  String loginId = inBalanceBean.loginId;
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
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript">
function toDetail(){
  parent.location.href='in_balance_edit.jsp';
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}

function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='in_balance_bottom.jsp?operate=<%=inBalanceBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">内部流转单</TD>
  </TR></TABLE>
<%String retu = inBalanceBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = inBalanceBean.getMaterTable();
  HtmlTableProducer table = inBalanceBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="100%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "1111"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-6; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right">
       <%if(loginBean.hasLimits(pageCode, op_add)){%>
         <%--02.28 19:45 新增  新增下面的这个新增的图标.因为原来不小心改程序的时候去掉了. 现在在加上了. yjg--%>
         <input name="add" class="button" type="button" title="新增(ALT+A)" value="新增内部流转(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)">
          <pc:shortcut key="a" script='<%="sumitForm("+Operate.ADD+", -1)"%>'/>
      <%}%>
   <%--   <input name="search2" type="button" class="button" title="记账(ALT+Z)" value="记账(Z)" onClick="if(confirm('是否确定记帐'))sumitForm(<%=inBalanceBean.RECODE_ACCOUNT%>)"  onKeyDown="return getNextElement();">
       <pc:shortcut key="z" script='<%="sumitForm("+ inBalanceBean.RECODE_ACCOUNT +",-1)"%>'/>--%>
       <%if(hasSearchLimit){%>
        <input name="search2" type="button" class="button" title="查询(ALT+Q)" value="查询(Q)" onClick="showFixedQuery()"  onKeyDown="return getNextElement();">
         <pc:shortcut key="q" script="showFixedQuery()"/>
      <%}%>
        <%if(inBalanceBean.retuUrl!=null){%>
         <input name="button22" type="button" class="button" title="返回(ALT+C)" value="返回(C)" onClick="buttonEventC()" onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script='buttonEventC()'/>
      <%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"></td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%
      BigDecimal tt_zsl = new BigDecimal(0),tt_zje = new BigDecimal(0);
      String zsl = "0",zje="0";
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++)   {
        list.goToRow(i);
        zsl = list.getValue("zsl");
        if(inBalanceBean.isDouble(zsl))
          tt_zsl = tt_zsl.add(new BigDecimal(zsl));
        zje = list.getValue("zje");
        if(inBalanceBean.isDouble(zje))
          tt_zje = tt_zje.add(new BigDecimal(zje));
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        String zt3332       =list.getValue("zt");
        String zt       =list.getValue("zt");
        if(rowClass.equals("0"))
          isInit = true;
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String sprid = list.getValue("sprid");
        boolean isCancel = zt.equals("1");
        isCancel = isCancel && loginId.equals(sprid);
        //是否制单人可以提交审批还.
        isInit = isZdridApprove?(loginId.equals(list.getValue("zdrid"))&&isInit):isInit;
    %>
    <%--02.17 20:49 新增onclick事件 yjg--%>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
      <%if(isInit){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
      <%if(isCancel){%><input name="image3" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=inBalanceBean.CANCEL_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
      <%if(zt.equals("1")&&hasRecord){%><input name="image5" class="img" type="image" title='记账' onClick="if(confirm('是否确定记帐'))sumitForm(<%=inBalanceBean.RECODE_ACCOUNT_BY_ROW%>,<%=list.getRow()%>)" src="../images/edit.old.gif" border="0"><%}%>
      <%if((zt.equals("1")||zt.equals("0"))&&loginBean.hasLimits(pageCode, op_cancel)){%>
       <input name="image4" class="img" type="image"   title='作废' onClick="if(confirm('确实要做废吗？'))sumitForm(<%=inBalanceBean.LADDING_CANCER%>,<%=list.getValue("nbjsid")%>)" src="../images/close.gif" border="0"><%}%></td>
      <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%  list.next();
      }
     %>
    <tr><td class="tdTitle" nowrap>本页合计</td>
     <td class=td>&nbsp;</td>
     <td class=td>&nbsp;</td>
     <td class=td>&nbsp;</td>
     <td class=td align=right><%=tt_zsl%></td>
     <td class=td align=right><%=tt_zje%></td>
     <td class=td>&nbsp;</td>
     <td class=td align=right></td>
     <td class=td>&nbsp;</td>
     <td class=td>&nbsp;</td>
     <td class=td>&nbsp;</td>
     <td class=td>&nbsp;</td>
   </tr>
   <tr><td class="tdTitle" nowrap>总合计</td>
       <td class=td>&nbsp;</td>
       <td class=td>&nbsp;</td>
       <td class=td>&nbsp;</td>
       <td class=td align=right><%=inBalanceBean.totalzsl%></td>
       <td class=td align=right><%=inBalanceBean.totalzje%></td>
       <td class=td>&nbsp;</td>
       <td class=td align=right></td>
       <td class=td>&nbsp;</td>
       <td class=td>&nbsp;</td>
       <td class=td>&nbsp;</td>
       <td class=td>&nbsp;</td>
   </tr>
    <%
      for(; i < iPage; i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
    <%if(!inBalanceBean.masterIsAdd()){
    int row = inBalanceBean.getSelectedRow();
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
//返回
 function buttonEventC()
 {
   parent.location.href='<%=inBalanceBean.retuUrl%>';
 }
  //关闭查询小视窗
  function buttonEventT()
  {
    hideFrame('fixedQuery');
  }
  //产品编码选择
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=sfdjid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product', obj.value);
}
//产品名称改变时的选择
function productNameSelect(obj,i)
{
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=dwdm&srcVar=dwtxid&srcVar=buyerName','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
</SCRIPT>
<%if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD class="td" nowrap>单据号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="nbjsdh" name="nbjsdh" value='<%=inBalanceBean.getFixedQueryValue("nbjsdh")%>' onKeyDown="return getNextElement();"></TD>
             <TD class="td" nowrap>收部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
                <%=deptBean.getList(inBalanceBean.getFixedQueryValue("deptid"))%></pc:select>
              </TD>
            </TR>
            <TR>
             <TD class="td" nowrap>发部门</TD>
              <TD nowrap class="td"><pc:select name="kc__deptid" addNull="1" style="width:160">
                <%=deptBean.getList(inBalanceBean.getFixedQueryValue("kc__deptid"))%></pc:select>
              </TD>
            </TR>
            <TR>
              <TD nowrap class="td">收发日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="jsrq$a" value='<%=inBalanceBean.getFixedQueryValue("jsrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jsrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="jsrq$b" style="WIDTH: 130px" name="jsrq$b" value='<%=inBalanceBean.getFixedQueryValue("jsrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jsrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="nbjsid$product" name="nbjsid$product" value='<%=inBalanceBean.getFixedQueryValue("nbjsid$product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
               <TR>
              <TD align="center" nowrap class="td">产品名称</TD>
               <td nowrap class="td" colspan="3"><input class="EDLine" style="WIDTH:330px" name="product" value='<%=inBalanceBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="nbjsid" value="<%=inBalanceBean.getFixedQueryValue("nbjsid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=nbjsid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.nbjsid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="nbjsid.value='';product.value='';">
            </td>
            </TR>
             <TR>

             <TD class="td" nowrap>规格属性</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="nbjsid$sxz" name="nbjsid$sxz" value='<%=inBalanceBean.getFixedQueryValue("nbjsid$sxz")%>' onKeyDown="return getNextElement();"></TD>

              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="nbjsid$cpbm" name="nbjsid$cpbm" value='<%=inBalanceBean.getFixedQueryValue("nbjsid$cpbm")%>' onKeyDown="return getNextElement();"></TD>

            </TR>
            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = inBalanceBean.getFixedQueryValue("zt");%>
                <input type="radio" name="zt" value=""<%=zt.equals("")?" checked" :""%>>
                全部
                <input type="radio" name="zt" value="9"<%=zt.equals("9")?" checked" :""%>>
                审批中
                <input type="radio" name="zt" value="0"<%=zt.equals("0")?" checked" :""%>>
                未审
                <input type="radio" name="zt" value="1"<%=zt.equals("1")?" checked" :""%>>
                已审
                <input type="radio" name="zt" value="2"<%=zt.equals("2")?" checked" :""%>>
                记帐
                <input type="radio" name="zt" value="4"<%=zt.equals("4")?" checked" :""%>>
                作废
              </TD>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center">
                <INPUT type="button" class="button" title="查询(ALT+F)" value="查询(F)" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)"  name="button" onKeyDown="return getNextElement();">
                  <pc:shortcut key="f" script='<%="sumitFixedQuery("+Operate.FIXED_SEARCH+")"%>'/>
                <INPUT  type="button" class="button" title="关闭(ALT+T)" value="关闭(T)" onClick="hideFrame('fixedQuery')" name="button2" onKeyDown="return getNextElement();">
                  <pc:shortcut key="t" script='<%="buttonEventT()"%>'/>
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
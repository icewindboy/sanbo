<%
/**
 * 02.17 新增 外贸出库单主表
 *       实际这张页面是从原来的putoutlist.jsp直接copy改一个名字过来的.目地是须要实现框架网页 yjg
 * 03.08 16:41 此张页面中由 HtmlTableProducer打印出来的表格中的资料数据又新增了审批人字段
 *             和给状态加上查看审批意见的超链接.但页面并没有做任何改动.改动的是nodefield数据库中的这张表. yjg
 */

%>
<%@ page contentType="text/html; charset=gb2312"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";

%><%
  String pageCode = "putoutlist";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.store.shengyu.B_OutSaleStore outSaleStoreBean = engine.erp.store.shengyu.B_OutSaleStore.getInstance(request);
  synchronized(outSaleStoreBean){
    outSaleStoreBean.djxz = 13;
    boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
    String loginId = outSaleStoreBean.loginId;
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript">
function toDetail(){
  parent.location.href='putoutlist_edit.jsp';
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
  parent.bottom.location.href='putoutlist_bottom.jsp?operate=<%=outSaleStoreBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
}

</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">外贸出库单列表</TD>
  </TR></TABLE>
<%String retu = outSaleStoreBean.doService(request, response);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
    engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
    engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
    engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
    engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
    engine.project.LookUp saleOutBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_SALE_OUT);
    //系统参数：SYS_APPROVE_ONLY_SELF 用于判断1=仅制定人可提交,0=有权限人可提交
  boolean isZdridApprove = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF").equals("1")?true:false;
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = outSaleStoreBean.getMaterListTable();
  HtmlTableProducer table = outSaleStoreBean.masterListProducer;
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="100%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "1111"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-6; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
      </td>
       <td class="td" nowrap align="right">
        <%if(loginBean.hasLimits(pageCode, op_add)){%>
         <input name="add" class="button" type="button" title="新增(ALT+A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" value="新增(A)" onKeyDown="return getNextElement();">
        <pc:shortcut key="a" script='<%="sumitForm("+Operate.ADD+", -1)"%>'/>
       <%}%>
       <input name="search2" type="button" class="button" title="记账(ALT+Z)" value="记账(Z)" onClick="if(confirm('是否确定记帐'))sumitForm(<%=outSaleStoreBean.RECODE_ACCOUNT%>)"  onKeyDown="return getNextElement();">
       <pc:shortcut key="z" script='<%="sumitForm("+ outSaleStoreBean.RECODE_ACCOUNT +",-1)"%>'/>
       <%if(hasSearchLimit){%><input name="search2" type="button" class="button" title="查询(ALT+Q)" value="查询(Q)"  onClick="showFixedQuery()" onKeyDown="return getNextElement();">
         <pc:shortcut key="q" script='<%="showFixedQuery()"%>'/>
       <%}%>
         <%if(outSaleStoreBean.retuUrl!=null){%><input name="button22" type="button" class="button" title="返回(ALT+C)" value="返回(C)" onClick="buttonEventC()"  onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script='<%="buttonEventC()"%>'/>
      <%}%>
     </td>
    </tr>
  </table>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"></td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%
      BigDecimal tt_zsl = new BigDecimal(0);
      String zsl = "0";
      //saleOutBean.regConditionData(list,"sfdjlbid");
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++)   {
        list.goToRow(i);
        zsl = list.getValue("zsl");
        if(outSaleStoreBean.isDouble(zsl))
          tt_zsl = tt_zsl.add(new BigDecimal(zsl));
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0"))
          isInit = true;
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String sprid = list.getValue("sprid");
        String zt = list.getValue("zt");
        boolean isCancel = zt.equals("1");
        isCancel = loginId.equals(sprid) && isCancel;
        //是否制单人可以提交审批还.
    	isInit = isZdridApprove?(loginId.equals(list.getValue("zdrid"))&&isInit):isInit;
            int aa=list.getRow();
    %>
    <%--02.17 10:50 新增 新增onclick事件.yjg--%>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
      <%if(isInit){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
      <%if(isCancel){%><input name="image3" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=outSaleStoreBean.CANCEL_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%></td>
      <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%  list.next();
      }
       %>
    <tr>
     <td class="tdTitle" nowrap>本页合计</td>
     <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td><td class=td>&nbsp;</td>
     <td class=td align=right><%=tt_zsl%></td>
     <td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td>
   </tr>
   <tr>
     <td class="tdTitle" nowrap>总合计</td>
     <td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td>
      <td class=td>&nbsp;</td><td class=td>&nbsp;</td>
     <td class=td align=right><%=outSaleStoreBean.totalzsl%></td>
     <td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td><td class=td>&nbsp;</td>
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
<%if(!outSaleStoreBean.masterIsAdd()){
    int row = outSaleStoreBean.getSelectedRow();
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
    parent.location.href='<%=outSaleStoreBean.retuUrl%>';
  }
  //关闭查询小视窗
function buttonEventT()
{
hideFrame('fixedQuery');
}
//客户编码
function corpCodeSelect(obj)
{
    CustomerCodeChange('2',document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=buyerName','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户名称
function corpNameSelect(obj)
{
  CustomerNameChange('2',document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=buyerName','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//产品编码选择
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=sfdjid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product', obj.value);
}
//产品名称改变时的选择
function productNameSelect(obj,i)
{
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=sfdjid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product',obj.value);
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
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="sfdjdh" name="sfdjdh" value='<%=outSaleStoreBean.getFixedQueryValue("sfdjdh")%>' onKeyDown="return getNextElement();"></TD>
              <TD nowrap class="td">仓库</TD>
              <TD nowrap class="td"><pc:select name="storeid" addNull="1" style="width:160">
            <%=storeBean.getList(outSaleStoreBean.getFixedQueryValue("storeid"))%></pc:select>
           </TD>
           </TR>
            <TR>
              <TD nowrap class="td">单据类别</TD>
              <TD nowrap class="td"><pc:select name="sfdjlbid" addNull="1" style="width:160">
            <%=saleOutBean.getList(outSaleStoreBean.getFixedQueryValue("sfdjlbid"))%></pc:select>
           </TD>
              <TD nowrap class="td">结算方式</TD>
              <TD nowrap class="td"><pc:select name="jsfsid" addNull="1" style="width:160">
            <%=balanceBean.getList(outSaleStoreBean.getFixedQueryValue("jsfsid"))%></pc:select>
           </TD>
           </TR>
            <TR>
              <TD nowrap class="td">收发日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="sfrq$a" value='<%=outSaleStoreBean.getFixedQueryValue("sfrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(sfrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="sfrq$b" style="WIDTH: 130px" name="sfrq$b" value='<%=outSaleStoreBean.getFixedQueryValue("sfrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(sfrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <tr>
                <TD class="td" nowrap>部门</TD>
                <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
                 <%=deptBean.getList(outSaleStoreBean.getFixedQueryValue("deptid"))%></pc:select>
                </td>
              <TD align="center" nowrap class="td">经手人</TD>
               <td nowrap class="td" colspan="3"><input class="edbox" name="jsr" value='<%=outSaleStoreBean.getFixedQueryValue("jsr")%>'>
            </td>
          </tr>
            <TR>
              <TD class="td" nowrap>购货单位</TD>
              <TD nowrap class="td" colspan="3">
                <input type="hidden" name="dwtxid" value='<%=outSaleStoreBean.getFixedQueryValue("dwtxid")%>'>
                <input type="text" class="edbox" style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=outSaleStoreBean.getFixedQueryValue("dwdm")%>' onchange="corpCodeSelect(this)" >
                <input type="text" name="buyerName" value='<%=outSaleStoreBean.getFixedQueryValue("buyerName")%>' style="width:130" class="edbox" onchange="corpNameSelect(this)">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=buyerName&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';buyerName.value='';dwdm.value='';"></TD>
               </TR>
            <TR>
            <TR>
              <TD class="td" nowrap>品名</TD>
              <TD nowrap class="td">
                <input type="text" class="edbox" style="width:70"  name="sfdjid$pm" onKeyDown="return getNextElement();" value='<%=outSaleStoreBean.getFixedQueryValue("sfdjid$pm")%>'>
                规格
                <input type="text" class="edbox" style="width:70"  name="sfdjid$gg" onKeyDown="return getNextElement();" value='<%=outSaleStoreBean.getFixedQueryValue("sfdjid$gg")%>'>
              </TD>
            </TR>
            <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap colspan="3"><INPUT class="edbox" style="WIDTH:70" id="sfdjid$cpbm" name="sfdjid$cpbm" value='<%=outSaleStoreBean.getFixedQueryValue("sfdjid$cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)">
              <INPUT class="edbox" style="WIDTH:130" id="cpmc" name="cpmc" value='<%=outSaleStoreBean.getFixedQueryValue("cpmc")%>' onKeyDown="return getNextElement();" onchange="productNameSelect(this)">
              <INPUT TYPE="HIDDEN" NAME="sfdjid" value="<%=outSaleStoreBean.getFixedQueryValue("sfdjid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=sfdjid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product',fixedQueryform.sfdjid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="sfdjid.value='';sfdjid$cpbm.value='';cpmc.value='';">
              </TD>
            </TR>

            <TR>
              <TD class="td" nowrap>规格属性</TD>
                <TD nowrap class="td"> <input type="text" class="edbox" style="width:70"  name="sfdjid$sxz" onKeyDown="return getNextElement();" value='<%=outSaleStoreBean.getFixedQueryValue("sfdjid$sxz")%>'>
                </TD>
              </TD>
            </TR>

            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = outSaleStoreBean.getFixedQueryValue("zt");%>
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
                </TD>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT type="button" class="button" title="查询(ALT+F)" value="查询(F)" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)"  name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+Operate.FIXED_SEARCH+")"%>'/>
                <INPUT type="button" class="button" title="关闭(ALT+T)" value="关闭(T)" onClick="hideFrame('fixedQuery')" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="t" script='<%="buttonEventT()"%>'/>
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%} out.print(retu);}%>
</body>
</html>

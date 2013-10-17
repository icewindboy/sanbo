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
  engine.erp.buy.B_CheckBuyOrderGoods checkBuyOrderGoodsBean = engine.erp.buy.B_CheckBuyOrderGoods.getInstance(request);
  String pageCode = "check_buy_ordergoods";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  boolean hasInstore = loginBean.hasLimits(pageCode, op_instore);
  String SYS_APPROVE_ONLY_SELF =checkBuyOrderGoodsBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  boolean isApproveOnly = SYS_APPROVE_ONLY_SELF.equals("1") ? true : false;//true仅制单人可提交
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
  parent.location.href='check_buy_ordergoods_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='check_buy_ordergoods_bottom.jsp?operate=<%=checkBuyOrderGoodsBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
//客户编码
function customerCodeSelect(obj)
{
    ProvideCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户名称
function customerNameSelect(obj)
{
  ProvideNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户编码
function customerDwCodeSelect(obj)
{
    TransportCodeChange(document.all['prod'], obj.form.name,'srcVar=dwt_dwdm&srcVar=dwt_dwtxid&srcVar=dwt_dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
//客户名称
function customerDwNameSelect(obj)
{
  TransportNameChange(document.all['prod'], obj.form.name,'srcVar=dwt_dwdm&srcVar=dwt_dwtxid&srcVar=dwt_dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">进货检验单列表</TD>
  </TR></TABLE>
<%String retu = checkBuyOrderGoodsBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  EngineDataSet list = checkBuyOrderGoodsBean.getMaterTable();
  HtmlTableProducer table = checkBuyOrderGoodsBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  RowMap masterRow = checkBuyOrderGoodsBean.getMasterRowinfo();
  String djlx = masterRow.get("djlx");
  String loginId = checkBuyOrderGoodsBean.loginId;
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-5; String pageSize = String.valueOf(iPage);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right">
         <%if(hasSearchLimit){%><input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <%if(checkBuyOrderGoodsBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="parent.location.href='<%=checkBuyOrderGoodsBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="parent.location.href='"+checkBuyOrderGoodsBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=30 align="center"></td>
      <td nowrap height='20'>单据号</td>
      <td nowrap height='20'>供货单位</td>
      <td nowrap height='20'>部门</td>
      <td nowrap height='20'>业务员</td>
      <td nowrap height='20'>制单人</td>
    </tr>
    <%BigDecimal t_zsl = new BigDecimal(0), t_zje = new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++)   {
        list.goToRow(i);
        String zsl = list.getValue("zsl");
        if(checkBuyOrderGoodsBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        String zje = list.getValue("zje");
        if(checkBuyOrderGoodsBean.isDouble(zje))
          t_zje = t_zje.add(new BigDecimal(zje));
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0"))
          isInit = true;

        String czyid = list.getValue("czyid");
       //提交审批是否只有制单人可以提交
       //isApproveOnly = isApproveOnly && loginId.equals(zdrid);//如果时只有制单人才可以提交审批。制单人等于登录人才显示
        boolean isShow = isApproveOnly ? (loginId.equals(czyid) && isInit) : isInit;//如果时只有制单人才可以提交审批。制单人等于登录人才显示

        String jhdid = list.getValue("jhdid");
        boolean isReference = checkBuyOrderGoodsBean.isReference(jhdid);//进货单是否被入库，如果入库颜色要变化
        rowClass = engine.action.BaseAction.getStyleName(isReference ? "-1" : rowClass);
        String zt = list.getValue("zt");
        String sprid = list.getValue("sprid");
        boolean isCancel = zt.equals("1");
        boolean isComplete  = zt.equals("2") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
        isCancel =  isCancel && loginId.equals(sprid);//是否可以取消审批
        String dwtxid = list.getValue("dwtxid");
        RowMap dwRow = corpBean.getLookupRow(dwtxid);
        String deptid = list.getValue("deptid");
        RowMap deptRow = deptBean.getLookupRow(deptid);
        String personid = list.getValue("personid");
        RowMap personRow = personBean.getLookupRow(personid);        
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap>
      <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      
      <td nowrap class=td><%=list.getValue("JHDBM") %></td>
      <td nowrap class=td><%=dwRow.get("dwmc") %></td>
       <td nowrap class=td><%=deptRow.get("mc") %></td>
      <td nowrap class=td><%=personRow.get("xm") %></td>
      <td nowrap class=td><%=list.getValue("CZY") %></td>
    </tr>
    <%  list.next();
      }
      i=count+1;
     %>

      <%
      for(; i < iPage; i++){
        out.print("<tr>");
        out.print("<td>&nbsp;</td>");
        out.print("<td>&nbsp;</td>");
        out.print("<td>&nbsp;</td>");
        out.print("<td>&nbsp;</td>");
        out.print("<td>&nbsp;</td>");
        out.print("<td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<script LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%if(!checkBuyOrderGoodsBean.masterIsAdd()){
    int row = checkBuyOrderGoodsBean.getSelectedRow();
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
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="jhdbm" name="jhdbm" value='<%=checkBuyOrderGoodsBean.getFixedQueryValue("jhdbm")%>' maxlength='32' onKeyDown="return getNextElement();"></TD>

            </TR>

            <TR>
            <%String dwtxid = checkBuyOrderGoodsBean.getFixedQueryValue("dwtxid");
              RowMap dwRow = corpBean.getLookupRow(dwtxid);%>
              <TD align="center" nowrap class="td">购货单位</TD>
              <td nowrap class="td" colspan=3>
              <input type="text" class="edbox" style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=dwRow.get("dwdm")%>' onchange="customerCodeSelect(this)" >
              <input type="text" name="dwmc"  style="width:260" value='<%=dwRow.get("dwmc")%>' class="edbox"  onchange="customerNameSelect(this)" >
              <INPUT TYPE="HIDDEN" NAME="dwtxid" value='<%=dwtxid%>'>
              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwtxid.value)">
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
              </td>
            </TR>
               <TR>
              <TD align="center" nowrap class="td">产品名称</TD>
               <td nowrap class="td" colspan="3"><input class="EDLine" style="WIDTH:330px" name="product" value='<%=checkBuyOrderGoodsBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="jhdid" value="<%=checkBuyOrderGoodsBean.getFixedQueryValue("jhdid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=jhdid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.jhdid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="jhdid.value='';product.value='';">
            </td>
            </TR>
             <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="jhdid$cpbm" name="jhdid$cpbm" value='<%=checkBuyOrderGoodsBean.getFixedQueryValue("jhdid$cpbm")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="jhdid$product" name="jhdid$product" value='<%=checkBuyOrderGoodsBean.getFixedQueryValue("jhdid$product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
               <TR>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
         <%=deptBean.getList(checkBuyOrderGoodsBean.getFixedQueryValue("deptid"))%></pc:select>
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

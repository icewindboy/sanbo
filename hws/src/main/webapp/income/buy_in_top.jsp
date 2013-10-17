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
  engine.erp.income.InBuy_In inbuyInBean = engine.erp.income.InBuy_In.getInstance(request);
  String pageCode = "income_in";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  boolean hasInstore = loginBean.hasLimits(pageCode, op_instore);
  String SYS_APPROVE_ONLY_SELF =inbuyInBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
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
  parent.location.href='buy_in_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='buy_in_bottom.jsp?operate=<%=inbuyInBean.SHOW_DETAIL%>&rownum='+masterRow;
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
    <TD NOWRAP align="center">进口货物进货单列表</TD>
  </TR></TABLE>
<%String retu = inbuyInBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  EngineDataSet list = inbuyInBean.getMaterTable();
  HtmlTableProducer table = inbuyInBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  RowMap masterRow = inbuyInBean.getMasterRowinfo();
  String djlx = masterRow.get("djlx");
  String loginId = inbuyInBean.loginId;
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
          <%if(loginBean.hasLimits(pageCode, op_add)){%><input name="search3" type="button" class="button" onClick="sumitForm(<%=Operate.ADD%>,-1)" value="新增进货单(A)" onKeyDown="return getNextElement();">
          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/>
         <%--
         <input name="search4" type="button" class="button" onClick="sumitForm(<%=inbuyInBean.THD_ADD%>,-1)" value="新增退货单(T)" onKeyDown="return getNextElement();">
          <pc:shortcut key="t" script='<%="sumitForm("+ inbuyInBean.THD_ADD +",-1)"%>'/>
         --%>
         <%}%>
         <%if(hasSearchLimit){%><input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <%if(inbuyInBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="parent.location.href='<%=inbuyInBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="parent.location.href='"+inbuyInBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=30 align="center"></td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%BigDecimal t_zsl = new BigDecimal(0), t_zje = new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++)   {
        list.goToRow(i);
        String zsl = list.getValue("zsl");
        if(inbuyInBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        String zje = list.getValue("zje");
        if(inbuyInBean.isDouble(zje))
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
        boolean isReference = inbuyInBean.isReference(jhdid);//进货单是否被入库，如果入库颜色要变化
        rowClass = engine.action.BaseAction.getStyleName(isReference ? "-1" : rowClass);
        String zt = list.getValue("zt");
        String sprid = list.getValue("sprid");
        boolean isCancel = zt.equals("1");
        boolean isComplete  = zt.equals("2") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
        isCancel =  isCancel && loginId.equals(sprid);//是否可以取消审批
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%if(isShow){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
     <%if(isCancel){%><input name="image3" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=inbuyInBean.CANCLE_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
      <%if(zt.equals("1") && hasInstore){%><input name="image3" class="img" type="image" title='入库确认' onClick="if(confirm('是否入库确认？'))sumitForm(<%=inbuyInBean.INSTORE_CONFIRM%>,<%=list.getRow()%>)" src="../images/edit.old.gif" border="0"><%}%>
     <%--if(isComplete){%><input name="image5" class="img" type="image" title='完成' onClick="if(confirm('是否强制完成该纪录？'))sumitForm(<%=inbuyInBean.COMPLETE%>,<%=list.getRow()%>)" src="../images/edit.old.gif" border="0"><%}--%></td>
   <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%  list.next();
      }
      i=count+1;
     %>
      <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
      <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zje%>' readonly></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>

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
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%if(!inbuyInBean.masterIsAdd()){
    int row = inbuyInBean.getSelectedRow();
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
            <%
             ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("-1");
             ArrayList opval = new ArrayList(); opval.add("进货单"); opval.add("退货单");
             ArrayList[] lists  = new ArrayList[]{opkey, opval};
            %>
              <TD class="td" nowrap>单据号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="jhdbm" name="jhdbm" value='<%=inbuyInBean.getFixedQueryValue("jhdbm")%>' maxlength='32' onKeyDown="return getNextElement();"></TD>
             <%-- <TD class="td" nowrap>单据类型</TD>
              <TD nowrap class="td">
                <pc:select name="djlx" addNull="1" style="width:160" >
                <%=inbuyInBean.listToOption(lists, opkey.indexOf(inbuyInBean.getFixedQueryValue("djlx")))%>
               </pc:select>
              </TD>--%>
            </TR>
            <TR>
              <TD nowrap class="td">交货日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="jhrq$a" style="WIDTH: 130px" name="jhrq$a" value='<%=inbuyInBean.getFixedQueryValue("jhrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jhrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="jhrq$b" style="WIDTH: 130px" name="jhrq$b" value='<%=inbuyInBean.getFixedQueryValue("jhrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jhrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
            <%String dwtxid = inbuyInBean.getFixedQueryValue("dwtxid");
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
            <%String dwt_dwtxid = inbuyInBean.getFixedQueryValue("dwt_dwtxid");
              RowMap corpRow = corpBean.getLookupRow(dwt_dwtxid);%>
              <TD align="center" nowrap class="td">承运单位</TD>
              <td nowrap class="td" colspan=3>
              <input type="text" class="edbox" style="width:70" onKeyDown="return getNextElement();" name="dwt_dwdm" value='<%=corpRow.get("dwdm")%>' onchange="customerDwCodeSelect(this)" >
              <input type="text" name="dwt_dwmc"  style="width:260" value='<%=corpRow.get("dwmc")%>' class="edbox"  onchange="customerDwNameSelect(this)" >
              <INPUT TYPE="HIDDEN" NAME="dwt_dwtxid" value='<%=dwt_dwtxid%>'>
              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="TransportSingleSelect('fixedQueryform','srcVar=dwt_dwtxid&srcVar=dwt_dwmc&srcVar=dwt_dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',fixedQueryform.dwt_dwtxid.value)">
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';">
              </td>
            </TR>
               <TR>
              <TD align="center" nowrap class="td">产品名称</TD>
               <td nowrap class="td" colspan="3"><input class="EDLine" style="WIDTH:330px" name="product" value='<%=inbuyInBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="jhdid" value="<%=inbuyInBean.getFixedQueryValue("jhdid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=jhdid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.jhdid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="jhdid.value='';product.value='';">
            </td>
            </TR>
             <TR>

             <TD class="td" nowrap>规格属性</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="jhdid$sxz" name="jhdid$sxz" value='<%=inbuyInBean.getFixedQueryValue("sxz")%>' onKeyDown="return getNextElement();"></TD>

              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="jhdid$cpbm" name="jhdid$cpbm" value='<%=inbuyInBean.getFixedQueryValue("jhdid$cpbm")%>' onKeyDown="return getNextElement();"></TD>

            </TR>
               <TR>
               <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="jhdid$product" name="jhdid$product" value='<%=inbuyInBean.getFixedQueryValue("jhdid$product")%>' onKeyDown="return getNextElement();"></TD>
               <TD nowrap class="td">仓库</TD>
              <TD nowrap class="td"><pc:select name="storeid" addNull="1" style="width:160">
              <%=storeBean.getList(inbuyInBean.getFixedQueryValue("storeid"))%></pc:select>
              </TD>
              </TR>
              <TR>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
         <%=deptBean.getList(inbuyInBean.getFixedQueryValue("deptid"))%></pc:select>
           </TD>
             </TR>
            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = inbuyInBean.getFixedQueryValue("zt");%>
                <input type="checkbox" name="zt" value="9"<%=zt.equals("9")?" checked" :""%>>
                审批中
                <input type="checkbox" name="zt" value="0"<%=zt.equals("0")?" checked" :""%>>
                未审
                <input type="checkbox" name="zt" value="1"<%=zt.equals("1")?" checked" :""%>>
                已审
                <input type="checkbox" name="zt" value="2"<%=zt.equals("2")?" checked" :""%>>
                已入库未确认
                <input type="checkbox" name="zt" value="8"<%=zt.equals("8")?" checked" :""%>>
                已完全入库
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
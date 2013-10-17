<%--车间流转单--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%>
<%
  engine.erp.jit.ShopFlow shopFlowBean = engine.erp.jit.ShopFlow.getInstance(request);
  String pageCode = "shop_flow";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
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
  parent.location.href='shop_flow_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='shop_flow_bottom.jsp?operate=<%=shopFlowBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function showAddFrame(){
  hideFrame('fixedQuery');
  showFrame('detailDiv', true, "", true);
   }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">车间流转单</TD>
  </TR>
</TABLE>
<%String retu = shopFlowBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  EngineDataSet list = shopFlowBean.getMaterTable();
  HtmlTableProducer table = shopFlowBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  String loginId = shopFlowBean.loginId;
  String SYS_APPROVE_ONLY_SELF =shopFlowBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  boolean isApproveOnly = SYS_APPROVE_ONLY_SELF.equals("1") ? true : false;//true仅制单人可提交
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-7; String pageSize = String.valueOf(iPage);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right"><%if(hasSearchLimit){%><input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <%if(shopFlowBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="parent.location.href='<%=shopFlowBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="parent.location.href='"+shopFlowBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
    </tr>
  </table>
 <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"><%if(loginBean.hasLimits(pageCode, op_add)){%>
        <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
      <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/><%}%></td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%
     list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++){
        list.goToRow(i);
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0"))
          isInit = true;
        //提交审批是否只有制单人可以提交
        String zdrid = list.getValue("zdrid");
        boolean isShow = isApproveOnly ? (loginId.equals(zdrid) && isInit) : isInit;//如果时只有制单人才可以提交审批。制单人等于登录人才显示
        rowClass = engine.action.BaseAction.getStyleName(rowClass);

        String sprid = list.getValue("sprid");
        String zt=list.getValue("zt");
        boolean isCancelApprove =  zt.equals("1");
        isCancelApprove = isCancelApprove && loginId.equals(sprid);//是否可以取消合同
        boolean isComplete  = zt.equals("1") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
        //boolean isComplete  = zt.equals("1") && !zt.equals("8") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
        //boolean isRepeal  = isCanCancle && !zt.equals("8") && !zt.equals("2") && !zt.equals("4") && loginBean.hasLimits(pageCode, op_cancel);//有废除合同的权限
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%if(isShow){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
      <%if(isCancelApprove){%><input name="image3" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=shopFlowBean.CANCEL_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
    <%if(isComplete){%><input name="image5" class="img" type="image" title='完成' onClick="if(confirm('是否强制完成该纪录？'))sumitForm(<%=shopFlowBean.COMPLETE%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>
     <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%  list.next();
      }
      i=count+1;
     %>
      <tr>
      <td class="tdTitle" nowrap></td>
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
<%if(!shopFlowBean.masterIsAdd()){
    int row = shopFlowBean.getSelectedRow();
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
//产品编码选择
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cjlzdid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product', obj.value);
}
//产品名称改变时的选择
function productNameSelect(obj,i)
{
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cjlzdid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product',obj.value);
}
</SCRIPT>
<%if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>

    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
        <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD class="td" nowrap>流转编号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="cjlzdh" name="cjlzdh" value='<%=shopFlowBean.getFixedQueryValue("cjlzdh")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
                <%=deptBean.getList(shopFlowBean.getFixedQueryValue("deptid"))%></pc:select>
              </TD>
            </TR>
            <TR>
              <TD nowrap class="td">完工日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="wgrq$a" value='<%=shopFlowBean.getFixedQueryValue("wgrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(wgrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
              </TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="wgrq$b" style="WIDTH: 130px" name="wgrq$b" value='<%=shopFlowBean.getFixedQueryValue("wgrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(wgrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
              </TD>
             </TR>
             <TR>
              <TD nowrap class="td">接收日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="jsrq$a" value='<%=shopFlowBean.getFixedQueryValue("jsrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jsrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
              </TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="jsrq$b" style="WIDTH: 130px" name="jsrq$b" value='<%=shopFlowBean.getFixedQueryValue("jsrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jsrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
              </TD>
             </TR>
             <TR>
              <TD align="center" nowrap class="td">产品名称</TD>
              <TD class="td" nowrap colspan="3"><INPUT class="edbox" style="WIDTH:70" id="cjlzdid$cpbm" name="cjlzdid$cpbm" value='<%=shopFlowBean.getFixedQueryValue("cjlzdid$cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)">
              <INPUT class="edbox" style="WIDTH:130" id="cpmc" name="cpmc" value='<%=shopFlowBean.getFixedQueryValue("cpmc")%>' onKeyDown="return getNextElement();" onchange="productNameSelect(this)">
              <INPUT TYPE="HIDDEN" NAME="cjlzdid" value="<%=shopFlowBean.getFixedQueryValue("cjlzdid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cjlzdid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product',fixedQueryform.cjlzdid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cjlzdid.value='';cjlzdid$cpbm.value='';cpmc.value='';">
              </TD>
            </TR>
            <TR>
              <TD class="td" nowrap>规格</td>
                <td><input type="text" class="edbox" style="width:70"  name="cjlzdid$gg" onKeyDown="return getNextElement();" value='<%=shopFlowBean.getFixedQueryValue("cjlzdid$gg")%>'>
              </TD>
            </TR>
            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = shopFlowBean.getFixedQueryValue("zt");%>
                <input type="radio" name="zt" value=""<%=zt.equals("")?" checked" :""%>>
                全部
                <input type="radio" name="zt" value="9"<%=zt.equals("9")?" checked" :""%>>
                审批中
                <input type="radio" name="zt" value="0"<%=zt.equals("0")?" checked" :""%>>
                未审
                <input type="radio" name="zt" value="1"<%=zt.equals("1")?" checked" :""%>>
                已审
                <input type="radio" name="zt" value="8"<%=zt.equals("8")?" checked" :""%>>
                已完成
                </TD>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT name="button" type="button" class="button" title="查询(ALT+F)"   value="查询(F)" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)"  onKeyDown="return getNextElement();">
            <pc:shortcut key="f" script='<%="sumitFixedQuery("+Operate.FIXED_SEARCH+")"%>'/>
                <INPUT name="button2" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)"  onClick="hideFrame('fixedQuery')"  onKeyDown="return getNextElement();">
            <pc:shortcut key="t" script="hideFrame('fixedQuery')"/>
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
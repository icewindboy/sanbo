<%--生产计划列表--%>
<%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%
  engine.erp.jit.ProducePlan producePlanBean = engine.erp.jit.ProducePlan.getInstance(request);
  engine.erp.jit.PlanMakeProcess planMakeProcess = engine.erp.jit.PlanMakeProcess.getInstance(request);
  String pageCode = "produce_plan";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String SC_PLAN_ADD_STYLE = producePlanBean.SC_PLAN_ADD_STYLE;//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划
  String SYS_APPROVE_ONLY_SELF =producePlanBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
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
  parent.location.href='produce_plan_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='produce_plan_bottom.jsp?operate=<%=producePlanBean.SHOW_DETAIL%>&rownum='+masterRow;
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
//生产计划点击添加按钮的操作
function addExcute()
{
  if('<%=SC_PLAN_ADD_STYLE%>'=='')
    sumitForm(<%=Operate.ADD%>,-1);
  else if('<%=SC_PLAN_ADD_STYLE%>'=='0')
    showAddFrame();//弹出选择框，选择生成什么计划
  else if('<%=SC_PLAN_ADD_STYLE%>'=='1')
    sumitForm(<%=Operate.ADD%>,-1);//直接生成通用计划
  else if('<%=SC_PLAN_ADD_STYLE%>'=='2')
    sumitForm(<%=producePlanBean.SUBPLAN%>,-1);//直接生成分切计划
}
function toSubDetail(){
  parent.location.href='produce_subplan_edit.jsp';
}
function toSubDetail(){
  parent.location.href='produce_subplan_edit.jsp';
}
function toProduce_Process(scjhid){
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
  var winName= "MakeProduce_Process";
  paraStr = "../jit/produce_process.jsp?operate=0&scjhid="+scjhid;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
function sumitAddPlan()
{
  lockScreenToWait("处理中, 请稍候！");
  if(fixedQueryform.lx[0].checked)
    fixedQueryform.operate.value = <%=Operate.ADD%>;
  else
    fixedQueryform.operate.value = <%=producePlanBean.SUBPLAN%>;
  fixedQueryform.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">生产计划维护列表</TD>
  </TR></TABLE>
<%String retu = producePlanBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp scplanBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PLAN);

  EngineDataSet list = producePlanBean.getMaterTable();
  HtmlTableProducer table = producePlanBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  String loginId = producePlanBean.loginId;

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
     <input name="add2" type="button" class="button"  value="引销售合同新增(N)" onKeyDown="return getNextElement();" onClick="sumitForm(<%=Operate.ADD%>,-1);">
         <pc:shortcut key="n" script='LookSaleOrder()'/>
     <input name="add3" type="button" class="button"  value="引外贸合同新增(W)" onKeyDown="return getNextElement();" onClick="sumitForm(<%=producePlanBean.ADD_WM%>,-1);">
         <pc:shortcut key="w" script='LookSaleOrder()'/>
     <input name="search3" type="button" class="button" onClick="LookSaleOrder()" value=" 查看销售合同(T)" onKeyDown="return getNextElement();">
         <pc:shortcut key="t" script='LookSaleOrder()'/>
          <%if(hasSearchLimit){%><input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
         <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <%if(producePlanBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="parent.location.href='<%=producePlanBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="parent.location.href='"+producePlanBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"><%if(loginBean.hasLimits(pageCode, op_add)){%>
        <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1);" src="../images/add_big.gif" border="0">
      <pc:shortcut key="a" script='<%="sumitForm("+Operate.ADD +", -1)"%>'/><%}%>
      </td>
      <td  nowrap height='20'>合同号</td>
      <td   nowrap height='20'>单位名称</td>
      <td    nowrap height='20'>业务员</td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%
      scplanBean.regData(list,"scjhid");
      BigDecimal t_zsl = new BigDecimal(0), t_zje = new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++){
        list.goToRow(i);
        String zsl = list.getValue("zsl");
        if(producePlanBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0"))
          isInit = true;
        String zdrid = list.getValue("zdrid");
        //提交审批是否只有制单人可以提交
        //isApproveOnly = isApproveOnly && loginId.equals(zdrid);//如果时只有制单人才可以提交审批。制单人等于登录人才显示
        boolean isShow = isApproveOnly ? (loginId.equals(zdrid) && isInit) : isInit;//如果时只有制单人才可以提交审批。制单人等于登录人才显示
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String zt = list.getValue("zt");
        String sprid = list.getValue("sprid");
        String scjhid = list.getValue("scjhid");
        String deptid = list.getValue("deptid");
        boolean isCancel = zt.equals("1");
        isCancel = isCancel && loginId.equals(sprid);//是否显示取消审批按钮，审批人等于登陆人
        boolean isShowMrp = !zt.equals("0") && !zt.equals("9");//显示生成物料需求的条件
        boolean isShowTask = producePlanBean.isShowTask(scjhid);
        isShowTask = isShowTask && !zt.equals("9") && !zt.equals("1") && !zt.equals("0");//显示下达车间任务的条件
        boolean isComplete  = !zt.equals("0") && !zt.equals("9") && !zt.equals("8") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
        RowMap scjhrow = scplanBean.getLookupRow(list.getValue("scjhid"));
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="left" nowrap><input name="image2" class="img" type="image" title='<%=(loginBean.hasLimits(pageCode, op_edit) && zt.equals("0")) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%if(isShow){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
     <%if(isCancel){%><input name="image2" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=producePlanBean.CANCEL_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>

     <%--if(isShowMrp){%><input name="image" class="img" type="image" title=<%=zt.equals("1") ? "生成物料需求" : "查看物料需求"%> onClick="MakeMrp('form1','scjhid='+<%=list.getValue("scjhid")%>+'&deptid='+<%=list.getValue("deptid")%>+'&rownum='+<%=list.getRow()%>+'&jhlx='+<%=list.getValue("jhlx")%>)" src='../images/edit.old.gif' border="0"><%}%>
    <%if(isShowTask){%><input name="image" class="img" type="image" title="加工单" onClick="sumitForm(<%=producePlanBean.BUILD_PROCESS_BILL%>,<%=list.getRow()%>)" src="../images/select_prod.gif" border="0"><%}--%>

      <%--if(isShowTask){%><input name="image" class="img" type="image" title="下达车间任务" onClick="MakeProduceTask('form1','scjhid='+<%=list.getValue("scjhid")%>+'&row='+<%=list.getRow()%>+'&jhlx='+<%=list.getValue("jhlx")%>+'&deptid='+<%=list.getValue("deptid")%>)" src='../images/select_prod.gif' border="0"><%}--%>
     <%if(isComplete){%><input name="image5" class="img" type="image" title='完成' onClick="if(confirm('是否强制完成该纪录？'))sumitForm(<%=producePlanBean.COMPLETE%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%></td>
    <td  <%=rowClass%> align="left"  nowrap height='20'><%=scjhrow.get("htbh")%></td><td  <%=rowClass%> align="left" nowrap height='20'><%=scjhrow.get("dwmc")%></td><td  <%=rowClass%> align="left" nowrap height='20'><%=scjhrow.get("xm")%></td>
    <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%  list.next();
      }
      i=count+1;
     %>
      <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
      <td class="td" nowrap></td>
      <td align="right" class="td"></td>
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
        out.print("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%if(!producePlanBean.masterIsAdd()){
    int row = producePlanBean.getSelectedRow();
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
  hideFrame('detailDiv');
  <%if(hasSearchLimit){%>showFrame('fixedQuery', true, "", true);<%}%>
}
function sumitSearchFrame(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  searchform.operate.value = oper;
  searchform.submit();
}
function MakeProduceTask(frmName, srcVar, methodName,notin)//下达生产任务
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
  var winName= "MakeProduceTask";
  paraStr = "../jit/plan_make_process_edit.jsp?operate=10002&srcFrm="+frmName+"&"+srcVar;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
function MakeMrp(frmName, srcVar, methodName,notin)//下达物料需求
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
  var winName= "MakeMrp";
  paraStr = "../jit/plan_make_mrp.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
function hide()
{
  hideFrame('detailDiv');
}
function LookSaleOrder()
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
  var winName= "GoodsProdSelector";
  paraStr = "sale_order_list.jsp?operate=0&src=../pub/main.jsp";
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
function productCodeSelect(obj,srcVars)
{
  ProdCodeChange(document.all['prod'], obj.form.name, srcVars,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function productNameSelect(obj,srcVars)
{
  ProdNameChange(document.all['prod'], obj.form.name, srcVars,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
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
            <%producePlanBean.searchTable.printWhereInfo(pageContext);%>
          <%--
           <TR>
              <TD class="td" nowrap>计划编号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="jhh" name="jhh" value='<%=producePlanBean.getFixedQueryValue("jhh")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:130">
              <%=deptBean.getList(producePlanBean.getFixedQueryValue("deptid"))%></pc:select>
            </TR>
           <TR>
              <TD nowrap class="td">计划日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="jhrq$a" value='<%=producePlanBean.getFixedQueryValue("jhrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jhrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="jhrq$b" style="WIDTH: 130px" name="jhrq$b" value='<%=producePlanBean.getFixedQueryValue("jhrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jhrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap class="td">开始日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="scjhid$a" value='<%=producePlanBean.getFixedQueryValue("scjhid$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(scjhid$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="scjhid$b" value='<%=producePlanBean.getFixedQueryValue("scjhid$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(scjhid$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD nowrap class="td">完成日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 130px" name="scjhid$c" value='<%=producePlanBean.getFixedQueryValue("scjhid$c")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(scjhid$c);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="scjhid$d" value='<%=producePlanBean.getFixedQueryValue("scjhid$d")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(scjhid$d);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD align="center" nowrap class="td">产品名称</TD>
               <td nowrap class="td" colspan="3"><input class="EDLine" style="WIDTH:330px" name="product" value='<%=producePlanBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="scjhid" value="<%=producePlanBean.getFixedQueryValue("scjhid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=scjhid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.scjhid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="scjhid.value='';product.value='';">
            </td>
            </TR>
             <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="scjhid$cpbm" name="scjhid$cpbm" value='<%=producePlanBean.getFixedQueryValue("scjhid$cpbm")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="scjhid$product" name="scjhid$product" value='<%=producePlanBean.getFixedQueryValue("scjhid$product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = producePlanBean.getFixedQueryValue("zt");%>
                <input type="radio" name="zt" value=""<%=zt.equals("")?" checked" :""%>>
                全部
                <input type="radio" name="zt" value="0"<%=zt.equals("0")?" checked" :""%>>
                未审
                <input type="radio" name="zt" value="1"<%=zt.equals("1")?" checked" :""%>>
                已审
                <input type="radio" name="zt" value="9"<%=zt.equals("9")?" checked" :""%>>
                审批中</TD>
                </TR>
                <TR>
                 <TD class="td" nowrap>&nbsp;</TD>
                 <TD colspan="3" nowrap class="td">
                <input type="radio" name="zt" value="2"<%=zt.equals("2")?" checked" :""%>>
                已生成需求
                <input type="radio" name="zt" value="3"<%=zt.equals("3")?" checked" :""%>>
                已下达任务
                <input type="radio" name="zt" value="8"<%=zt.equals("8")?" checked" :""%>>
                已完成</TD>
            </TR>--%>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+ Operate.FIXED_SEARCH +",-1)"%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
          </TABLE>
       </TD>
      </TR>
    </TABLE>
  </DIV>
  <div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
       <TABLE cellspacing=2 cellpadding=0 border=0>
        <TR>
         <TD>
          <TR>
          <TD>&nbsp;&nbsp;&nbsp;&nbsp;</TD>
          <TD class="td" colspan=3 align="center" nowrap>
          <input type="radio" name="lx" value="0"checked>通用计划
          <input type="radio" name="lx" value="1">分切计划
          </TD>
          <TD>&nbsp;&nbsp;&nbsp;&nbsp;</TD>
         </TR>
        </TD>
       </TR>
      <TR>
      <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitAddPlan()" type="button" value=" 确定(Z)" name="button" onKeyDown="return getNextElement();">
       <pc:shortcut key="z" script='sumitAddPlan()'/>
       <INPUT class="button" onClick="hideFrame('detailDiv')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
       <pc:shortcut key="x" script='hide();'/>
      </TD>
     </TR>
   </TABLE>
   </TD>
   </TR>
</TABLE>
</div>
</form>
<%} out.print(retu);%>
</body>
</html>
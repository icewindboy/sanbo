<%--生产加工单列表--%><%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over   = "op_over";
%><%
  engine.erp.jit.B_ProduceProcess produceProcessBean = engine.erp.jit.B_ProduceProcess.getInstance(request);
  String pageCode = "produce_process";
  produceProcessBean.sfwjg="0";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String SC_PLAN_ADD_STYLE =produceProcessBean.SC_PLAN_ADD_STYLE;//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划
  boolean isApproveOnly = produceProcessBean.SYS_APPROVE_ONLY_SELF.equals("1") ? true : false;//true仅制单人可提交
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
  parent.location.href='produce_process_edit.jsp';
}
function toSubDetail(){
  parent.location.href='produce_subprocess_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='produce_process_bottom.jsp?operate=<%=produceProcessBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
//生产加工单点击添加按钮的操作，是增加分切计划的加工单还是增加通用计划的加工单
function addExcute()
{
  //if('<%=SC_PLAN_ADD_STYLE%>'=='')
  //  sumitForm(<%=Operate.ADD%>,-1);
  //else if('<%=SC_PLAN_ADD_STYLE%>'=='0')
  //  showAddFrame();//弹出选择框，选择生成什么计划物料
  //else if('<%=SC_PLAN_ADD_STYLE%>'=='1')
    sumitForm(<%=Operate.ADD%>,-1);//直接生成通用计划物料
  //else if('<%=SC_PLAN_ADD_STYLE%>'=='2')
  //  sumitForm(<%=produceProcessBean.SUBTASK_ADD%>,-1);//直接生成分切计划物料
}
function showAddFrame(){
  hideFrame('fixedQuery');
  showFrame('detailDiv', true, "", true);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">生产加工单列表</TD>
  </TR></TABLE>
<%String retu = produceProcessBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);

  EngineDataSet list = produceProcessBean.getMaterListTable();//主表数据集
  HtmlTableProducer table = produceProcessBean.masterListProducer;//主表数据的表格打印

  //EngineDataSet list = produceProcessBean.getMaterTable();
  //HtmlTableProducer table = produceProcessBean.masterProducer;

  String curUrl = request.getRequestURL().toString();
  String loginId = produceProcessBean.loginId;

%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-6; String pageSize = String.valueOf(iPage);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right"><%if(hasSearchLimit){%><input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <%if(produceProcessBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="parent.location.href='<%=produceProcessBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="parent.location.href='"+produceProcessBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center">
       <%if(loginBean.hasLimits(pageCode, op_add)){%>
        <input name="image" class="img" type="image" title="新增(A)" onClick="addExcute()" src="../images/add_big.gif" border="0">
        <pc:shortcut key="a" script='addExcute()'/><%}%></td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%BigDecimal t_zsl = new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++){
        String zsl = list.getValue("zsl");
        if(produceProcessBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        String zt = list.getValue("zt");
        String zdrid = list.getValue("zdrid");
        String sprid = list.getValue("sprid");
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        boolean isCancel = zt.equals("1");
        isCancel = isCancel && loginId.equals(sprid);//是否显示取消审批按钮，审批人等于登陆人
        if(rowClass.equals("0"))
          isInit = true;
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        boolean isComplete  = zt.equals("1") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
        boolean isShow = isApproveOnly ? (loginId.equals(zdrid) && isInit) : isInit;//如果时只有制单人才可以提交审批。制单人等于登录人才显示
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td  align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
    <%if(isShow){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
     <%if(isCancel){%><input name="image2" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=produceProcessBean.CANCEL_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
      <%if(isComplete){%><input name="image5" class="img" type="image" title='完成' onClick="if(confirm('是否强制完成该纪录？'))sumitForm(<%=produceProcessBean.COMPLETE%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>
      </td>
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
     <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td><td class="td" nowrap></td><td class="td" nowrap></td>
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
<%if(!produceProcessBean.masterIsAdd()){
    int row = produceProcessBean.getSelectedRow();
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
function sumitProcess()
{
  lockScreenToWait("处理中, 请稍候！");
  if(fixedQueryform.lx[0].checked)
    fixedQueryform.operate.value = <%=Operate.ADD%>;
  else
    fixedQueryform.operate.value = <%=produceProcessBean.SUBTASK_ADD%>;
  fixedQueryform.submit();
}
//产品编码选择
function productCodeSelect(obj, srcVars)
{
  ProdCodeChange(document.all['prod'], obj.form.name, srcVars,'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
//产品名称改变时的选择
function productNameSelect(obj,srcVars)
{
    ProdNameChange(document.all['prod'], obj.form.name, srcVars,'fieldVar=cpid&fieldVar=cpbm&fieldVar=product',obj.value);
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
           <%--produceProcessBean.searchTable.printWhereInfo(pageContext);--%>
           <TR>
              <TD class="td" nowrap>加工单号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="jgdh" name="jgdh" value='<%=produceProcessBean.getFixedQueryValue("jgdh")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:130">
             <%=workShopBean.getList(produceProcessBean.getFixedQueryValue("deptid"))%></pc:select>
            </TR>
           <TR>
              <TD nowrap class="td">日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="kdrq$a" value='<%=produceProcessBean.getFixedQueryValue("kdrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(kdrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="kdrq$b" style="WIDTH: 130px" name="kdrq$b" value='<%=produceProcessBean.getFixedQueryValue("kdrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(kdrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD align="center" nowrap class="td">产品名称</TD>
              <TD class="td" nowrap colspan=3><INPUT class="edbox" style="WIDTH:70" id="jgdid$cpbm" name="jgdid$cpbm" value='<%=produceProcessBean.getFixedQueryValue("jgdid$cpbm")%>' onKeyDown="return getNextElement();" onchange="productCodeSelect(this)">
              <INPUT class="edbox" style="WIDTH:130" id="cpmc" name="cpmc" value='<%=produceProcessBean.getFixedQueryValue("cpmc")%>' onKeyDown="return getNextElement();" onchange="productNameSelect(this)">
              <INPUT TYPE="HIDDEN" NAME="jgdid" value="<%=produceProcessBean.getFixedQueryValue("jgdid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=jgdid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product',fixedQueryform.jgdid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="jgdid.value='';jgdid$cpbm.value='';cpmc.value=''">
            </td>
            </TR>
             <TR>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="jgdid$product" name="jgdid$product" value='<%=produceProcessBean.getFixedQueryValue("jgdid$product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
             <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = produceProcessBean.getFixedQueryValue("zt");%>
                <input type="radio" name="zt" value=""<%=zt.equals("")?" checked" :""%>>
                全部
                <input type="radio" name="zt" value="0"<%=zt.equals("0")?" checked" :""%>>
                未完成
                <input type="radio" name="zt" value="8"<%=zt.equals("8")?" checked" :""%>>
                已完成
                </TD>
            </TR>
           <TR>
              <TD class="td" nowrap>单号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="djh$a" name="djh$a" value='<%=produceProcessBean.getFixedQueryValue("djh$a")%>' maxlength='13' onKeyDown="return getNextElement();"></TD>
              <TD nowrap class="td">--</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="djh$b" name="djh$b" value='<%=produceProcessBean.getFixedQueryValue("djh$b")%>' maxlength='13' onKeyDown="return getNextElement();"></TD>
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
          <input type="radio" name="lx" value="0"checked>通用生产加工单
          <input type="radio" name="lx" value="1">分切生产加工单
          </TD>
          <TD>&nbsp;&nbsp;&nbsp;&nbsp;</TD>
         </TR>
        </TD>
       </TR>
      <TR>
      <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitProcess();" type="button" value=" 确定(Z)" name="button" onKeyDown="return getNextElement();">
       <pc:shortcut key="z" script='sumitProcess()'/>
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
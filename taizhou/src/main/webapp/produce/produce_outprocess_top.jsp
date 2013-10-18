<%--生产委外加工单列表--%><%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%
  engine.erp.produce.B_ProduceProcess produceOutProcessBean = engine.erp.produce.B_ProduceProcess.getInstance(request);
  String pageCode = "produce_outprocess";
  produceOutProcessBean.sfwjg ="1";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String SC_PLAN_ADD_STYLE =produceOutProcessBean.SC_PLAN_ADD_STYLE;//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划
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
  parent.location.href='produce_outprocess_edit.jsp';
}
function toSubDetail(){
  parent.location.href='produce_suboutprocess_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='produce_outprocess_bottom.jsp?operate=<%=produceOutProcessBean.SHOW_DETAIL%>&rownum='+masterRow;
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
    sumitForm(<%=Operate.ADD%>,-1);
 }
function showAddFrame(){
  hideFrame('fixedQuery');
  showFrame('detailDiv', true, "", true);
   }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">委外加工单列表</TD>
  </TR></TABLE>
<%String retu = produceOutProcessBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  EngineDataSet list = produceOutProcessBean.getMaterTable();
  HtmlTableProducer table = produceOutProcessBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  String loginId = produceOutProcessBean.loginId;

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
        <%if(produceOutProcessBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="parent.location.href='<%=produceOutProcessBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="parent.location.href='"+produceOutProcessBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"><%if(loginBean.hasLimits(pageCode, op_add)){%>
        <input name="image" class="img" type="image" title="新增(A)" onClick="addExcute()" src="../images/add_big.gif" border="0">
        <pc:shortcut key="a" script='addExcute()'/><%}%></td>
        <td nowrap align="center">加工厂</td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%corpBean.regData(list,"dwtxid");
      BigDecimal t_zsl = new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++)   {
        String zsl = list.getValue("zsl");
        if(produceOutProcessBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        String zt = list.getValue("zt");
        boolean isComplete  = zt.equals("0") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td  align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%if(isComplete){%><input name="image5" class="img" type="image" title='完成' onClick="if(confirm('是否强制完成该纪录？'))sumitForm(<%=produceOutProcessBean.COMPLETE%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>
      </td>
      <td nowrap class="td" align="left"><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td>
      <%table.printCells(pageContext, "class=td");%>
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
     <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
    <td class="td" nowrap></td>
     </tr>
      <%
      for(; i < iPage; i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td><td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
<%if(!produceOutProcessBean.masterIsAdd()){
    int row = produceOutProcessBean.getSelectedRow();
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
    fixedQueryform.operate.value = <%=produceOutProcessBean.SUBTASK_ADD%>;
  fixedQueryform.submit();
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
              <TD class="td" nowrap>加工单号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="jgdh" name="jgdh" value='<%=produceOutProcessBean.getFixedQueryValue("jgdh")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:130">
         <%=workShopBean.getList(produceOutProcessBean.getFixedQueryValue("deptid"))%></pc:select>
            </TR>
           <TR>
              <TD nowrap class="td">日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="rq$a" value='<%=produceOutProcessBean.getFixedQueryValue("rq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(rq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="rq$b" style="WIDTH: 130px" name="rq$b" value='<%=produceOutProcessBean.getFixedQueryValue("rq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(rq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD align="center" nowrap class="td">产品名称</TD>
               <td nowrap class="td" colspan="3"><input class="EDLine" style="WIDTH:330px" name="product" value='<%=produceOutProcessBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="jgdid" value="<%=produceOutProcessBean.getFixedQueryValue("jgdid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=jgdid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.jgdid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="jgdid.value='';product.value='';">
            </td>
            </TR>
             <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="jgdid$cpbm" name="jgdid$cpbm" value='<%=produceOutProcessBean.getFixedQueryValue("jgdid$cpbm")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="jgdid$product" name="jgdid$product" value='<%=produceOutProcessBean.getFixedQueryValue("jgdid$product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
             <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = produceOutProcessBean.getFixedQueryValue("zt");%>
                <input type="radio" name="zt" value=""<%=zt.equals("")?" checked" :""%>>
                全部
                <input type="radio" name="zt" value="0"<%=zt.equals("0")?" checked" :""%>>
                未完成
                <input type="radio" name="zt" value="8"<%=zt.equals("8")?" checked" :""%>>
                已完成
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
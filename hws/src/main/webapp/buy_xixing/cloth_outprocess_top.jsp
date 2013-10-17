<%--坯布外加工单主表--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%
  engine.erp.buy.xixing.B_ClothOutProcess B_ClothOutProcessBean = engine.erp.buy.xixing.B_ClothOutProcess.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  String pageCode = "cloth_outprocess";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String SYS_APPROVE_ONLY_SELF =B_ClothOutProcessBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  boolean isApproveOnly = SYS_APPROVE_ONLY_SELF.equals("1") ? true : false;//true仅制单人可提交
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="refresh" content="20">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript">
function toDetail(){
  //lockScreenToWait("处理中, 请稍候！");
  parent.location.href='cloth_outprocess_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='cloth_outprocess_bottom.jsp?operate=<%=B_ClothOutProcessBean.SHOW_DETAIL%>&rownum='+masterRow;
  unlockScreenWait();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function productCodeSelect(obj, i)
{
ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=pbwjgdid&srcVar=cpbm&srcVar=product','fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function productNameSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=pbwjgdid&srcVar=cpbm&srcVar=product','fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">坯布外加工单</TD>
  </TR></TABLE>
<%
  String retu = B_ClothOutProcessBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = B_ClothOutProcessBean.getMaterTable();
  HtmlTableProducer table = B_ClothOutProcessBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  String loginId = B_ClothOutProcessBean.loginId;
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="100%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "1111"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-7; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right"><%if(hasSearchLimit){%><input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
         <input name="search3" type="button" class="button" title="记账(ALT+Z)" value="记账(Z) " onClick="if(confirm('是否确定记帐'))sumitForm(<%=B_ClothOutProcessBean.RECODE_ACCOUNT%>)"  onKeyDown="return getNextElement();">
       <pc:shortcut key="z" script='<%="sumitForm("+ B_ClothOutProcessBean.RECODE_ACCOUNT +",-1)"%>'/>
        <%if(B_ClothOutProcessBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="parent.location.href='<%=B_ClothOutProcessBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="parent.location.href='"+B_ClothOutProcessBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/><%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"><%if(loginBean.hasLimits(pageCode, op_add)){%>
        <input name="image" class="img" type="image" title="新增(ALT+A)"  onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
       <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/>
      <%}%></td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%BigDecimal t_zsl = new BigDecimal(0), t_zje = new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++)
      {
        list.goToRow(i);
        //String zsl = list.getValue("zsl");
        //if(B_ClothOutProcessBean.isDouble(zsl))
         // t_zsl = t_zsl.add(new BigDecimal(zsl));
        //String zje = list.getValue("zje");
        //if(B_ClothOutProcessBean.isDouble(zje))
          //t_zje = t_zje.add(new BigDecimal(zje));
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0"))
          isInit = true;

        String czyid = list.getValue("czyid");
        //提交审批是否只有制单人可以提交
        //isApproveOnly = isApproveOnly && loginId.equals(zdrid);//如果时只有制单人才可以提交审批。制单人等于登录人才显示
        boolean isShow = isApproveOnly ? (loginId.equals(czyid) && isInit) : isInit;//如果时只有制单人才可以提交审批。制单人等于登录人才显示
        rowClass = engine.action.BaseAction.getStyleName(rowClass);

        String sprid = list.getValue("sprid");
        String zt = list.getValue("zt");
        boolean isCancel = zt.equals("1");
        isCancel =  isCancel && loginId.equals(sprid);//是否可以取消审批
        boolean isComplete  = zt.equals("1") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="toDetail(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
      <%if(isShow){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
      <%if(isCancel){%><input name="image3" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=B_ClothOutProcessBean.CANCLE_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
       <%if(isComplete){%><input name="image5" class="img" type="image" title='完成' onClick="if(confirm('完成后不可修改，是否完成？'))sumitForm(<%=B_ClothOutProcessBean.COMPLETE%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%></td>
         <%table.printCells(pageContext, rowClass);%>
    </tr>

    <%  list.next();
      }
     %>
     <%for(; i < iPage; i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
    <%if(!B_ClothOutProcessBean.masterIsAdd())
    {
      int row = B_ClothOutProcessBean.getSelectedRow();
      if(row >= 0)
        out.print("showSelected('tr_"+ row +"');");
    }%>
function showFixedQuery()
{
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
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
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

              <TD class="td" nowrap>外加工单号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="djh" name="djh" value='<%=B_ClothOutProcessBean.getFixedQueryValue("djh")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:130">
         <%=deptBean.getList(B_ClothOutProcessBean.getFixedQueryValue("deptid"))%></pc:select>
           </TD>
           </TR>
           <TR>
              <TD height="22" nowrap class="td">收货日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="ddrq$a" value='<%=B_ClothOutProcessBean.getFixedQueryValue("ddrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(ddrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="ddrq$b" style="WIDTH: 130px" name="ddrq$b" value='<%=B_ClothOutProcessBean.getFixedQueryValue("ddrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(ddrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>

            <TR>
              <TD align="center" nowrap class="td">材料名称规格</TD>
               <td nowrap class="td" colspan="3">

                <input class="EDBox" style="WIDTH:70px" name="cpbm" value='<%=B_ClothOutProcessBean.getFixedQueryValue("cpbm")%>'onchange="productCodeSelect(this)" onKeyDown="return getNextElement();" >
                <input class="EDBox" style="WIDTH:260px" name="product" value='<%=B_ClothOutProcessBean.getFixedQueryValue("product")%>' onchange="productNameSelect(this)" onKeyDown="return getNextElement();">
                <INPUT TYPE="HIDDEN" NAME="pbwjgdid" value="<%=B_ClothOutProcessBean.getFixedQueryValue("pbwjgdid")%>">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=pbwjgdid&srcVar=product&srcVar=cpbm','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',fixedQueryform.pbwjgdid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="pbwjgdid.value='';product.value='';cpbm.value='';">
            </td>
            </TR>
            <%--<TR>
               <TD align="center" nowrap class="td">材料名称规格</TD>
               <td nowrap class="td" colspan="3">
                <input class="EDBox" style="WIDTH:70px" name="cpbm" value='<%=B_ClothOutProcessBean.getFixedQueryValue("cpbm")%>'onchange="productCodeSelect(this)" onKeyDown="return getNextElement();" >
                <input class="EDBox" style="WIDTH:260px" name="product" value='<%=B_ClothOutProcessBean.getFixedQueryValue("product")%>'  onchange="productNameSelect(this)"  onKeyDown="return getNextElement();">
                <INPUT TYPE="hidden" NAME="pbwjgdid" value="<%=B_ClothOutProcessBean.getFixedQueryValue("pbwjgdid")%>">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=product&srcVar=cpbm','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',fixedQueryform.pbwjgdid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="pbwjgdid.value='';product.value='';cpbm.value=''">
            </td>
            </TR>--%>



            <TR>
            <TD class="td" nowrap>规格属性</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="pbwjgdid$sxz" name="pbwjgdid$sxz" value='<%=B_ClothOutProcessBean.getFixedQueryValue("sxz")%>' onKeyDown="return getNextElement();"></TD>

             <%-- <TD class="td" nowrap>规格属性</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="sxz" name="sxz" value='<%=B_ClothOutProcessBean.getFixedQueryValue("sxz")%>' onKeyDown="return getNextElement();"></TD>--%>
              <TD class="td" nowrap>业务员</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="jbr" name="jbr" value='<%=B_ClothOutProcessBean.getFixedQueryValue("jbr")%>' onKeyDown="return getNextElement();"></TD>


           </TR>
            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = B_ClothOutProcessBean.getFixedQueryValue("zt");%>
                <input type="radio" name="zt" value=""<%=zt.equals("")?" checked" :""%>>
                全部
                <input type="radio" name="zt" value="9"<%=zt.equals("9")?" checked" :""%>>
                审批中
                <input type="radio" name="zt" value="0"<%=zt.equals("0")?" checked" :""%>>
                未审
                <input type="radio" name="zt" value="1"<%=zt.equals("1")?" checked" :""%>>
                已审
                <input type="radio" name="zt" value="2"<%=zt.equals("2")?" checked" :""%>>
                记账
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
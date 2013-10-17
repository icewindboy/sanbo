<%--采购申请单主表aaa--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.erp.buy.xixing.BuyApply buyApplyBean = engine.erp.buy.xixing.BuyApply.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  String pageCode = "buy_apply";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String SYS_APPROVE_ONLY_SELF =buyApplyBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
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
  parent.location.href='buy_apply_edit.jsp';
}
function showDetail(masterRow){
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='buy_apply_bottom.jsp?operate=<%=buyApplyBean.SHOW_DETAIL%>&rownum='+masterRow;
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
    <TD NOWRAP align="center">采购申请单列表</TD>
  </TR></TABLE>
<%String retu = buyApplyBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = buyApplyBean.getMaterTable();
  EngineDataSet detail = buyApplyBean.getDetailTable();
  HtmlTableProducer table = buyApplyBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  String loginId = buyApplyBean.loginId;
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
        <%if(buyApplyBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="parent.location.href='<%=buyApplyBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="parent.location.href='"+buyApplyBean.retuUrl+"'" ;%>
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

      StringBuffer sb=new StringBuffer();
      for(; i<count; i++){
        int j;
        int isBuyDone=-1; //isBuyDone :开合同；isStoreDone：入库 0表示‘没有’，1表示‘部分’，2表示‘全部’
        double skhtl=0.0,tempHssl=0.0;
        list.goToRow(i);
        synchronized(detail){
          buyApplyBean.openDetailTable(false);
          detail.first();
          for(j=0;j<detail.getRowCount();j++){
            tempHssl = Double.parseDouble(detail.getValue("hssl").trim().length()>0?detail.getValue("hssl"):"0");
            skhtl = Double.parseDouble(detail.getValue("skhtl").trim().length()>0?detail.getValue("skhtl"):"0");
            if(skhtl == 0 && isBuyDone == 2){isBuyDone = 1; break;}
            if(skhtl >= tempHssl && isBuyDone == 0 && skhtl != 0){isBuyDone = 1; break;}
            if(skhtl > 0 && skhtl < tempHssl ){isBuyDone = 1; break;}
            if(skhtl >= tempHssl && skhtl != 0){isBuyDone = 2;}
            if(skhtl == 0){isBuyDone = 0;}
            detail.next();
          }
          if(isBuyDone == 1)
            sb.append("已部分下单");
          else if (isBuyDone == 2)
            sb.append("已全部下单");
          list.setValue("ztms",sb.toString());
          sb.delete(0,sb.length());
        }
        String zsl = list.getValue("zsl");
        if(buyApplyBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        String zje = list.getValue("zje");
        if(buyApplyBean.isDouble(zje))
          t_zje = t_zje.add(new BigDecimal(zje));
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0"))
          isInit = true;

        String czyid = list.getValue("czyid");
        //提交审批是否只有制单人可以提交
        //isApproveOnly = isApproveOnly && loginId.equals(zdrid);//如果时只有制单人才可以提交审批。制单人等于登录人才显示
        boolean isShow = isApproveOnly ? (loginId.equals(czyid) && isInit) : isInit;//如果时只有制单人才可以提交审批。制单人等于登录人才显示
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        boolean isrefer=buyApplyBean.hasReferenced("VW_KC_CGDD","cgsqdid",list.getValue("cgsqdid"));//是否被采购合同引用
        String sprid = list.getValue("sprid");
        String zt = list.getValue("zt");
        boolean isCancel = zt.equals("1");
        isCancel =  isCancel && loginId.equals(sprid)&&!isrefer;//是否可以取消审批
        boolean kk=loginBean.hasLimits(pageCode, op_over);
        boolean isComplete  = zt.equals("1") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
      <td <%=rowClass%> align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
      <%if(isShow){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
      <%if(isCancel){%><input name="image3" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=buyApplyBean.CANCLE_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
       <%if(isComplete){%><input name="image5" class="img" type="image" title='完成' onClick="if(confirm('是否完成该纪录？'))sumitForm(<%=buyApplyBean.COMPLETE%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%></td>
      <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%  list.next();
      }
      i=count+1;%>
      <tr>
      <td class="tdTitle" nowrap>合计</td>
      <td class="td" nowrap>&nbsp;</td>
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
      </tr>
      <%for(; i < iPage; i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
    <%if(!buyApplyBean.masterIsAdd())
    {
      int row = buyApplyBean.getSelectedRow();
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
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
 <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
     <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
         <%buyApplyBean.masterProducer.printWhereInfo(pageContext);%>
            <TR>
              <TD nowrap colspan=3 height=30 align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <INPUT class="button"  onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <INPUT class="button" onClick="hideFrame('fixedQuery');" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
  <div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv');refresh();" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="550" height="300" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</form>
<%out.print(retu);%>
</body>
</html>
<%--if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD class="td" nowrap>编号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="sqbh" name="sqbh" value='<%=buyApplyBean.getFixedQueryValue("sqbh")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:130">
         <%=deptBean.getList(buyApplyBean.getFixedQueryValue("deptid"))%></pc:select>
           </TD>
           </TR>
           <TR>
              <TD height="22" nowrap class="td">申请日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="sqrq$a" value='<%=buyApplyBean.getFixedQueryValue("sqrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(sqrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="sqrq$b" style="WIDTH: 130px" name="sqrq$b" value='<%=buyApplyBean.getFixedQueryValue("sqrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(sqrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD align="center" nowrap class="td">原辅料名称</TD>
               <td nowrap class="td" colspan="3"><input class="EDLine" style="WIDTH:250px" name="product" value='<%=buyApplyBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="cgsqdid" value="<%=buyApplyBean.getFixedQueryValue("cgsqdid")%>"><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cgsqdid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.cgsqdid.value)">
               <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cgsqdid.value='';product.value='';">
            </td>
            </TR>
            <TR>
              <!--
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="cgsqdid$cpbm" name="cgsqdid$cpbm" value='<%=buyApplyBean.getFixedQueryValue("cgsqdid$cpbm")%>' onKeyDown="return getNextElement();"></TD>
              -->
              <TD class="td" nowrap>规格属性</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="sxz" name="sxz" value='<%=buyApplyBean.getFixedQueryValue("sxz")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>业务员</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="cgsqdid$product" name="cgsqdid$product" value='<%=buyApplyBean.getFixedQueryValue("cgsqdid$product")%>' onKeyDown="return getNextElement();"></TD>


           </TR>
            <TR>
              <TD class="td" nowrap>审核情况</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = buyApplyBean.getFixedQueryValue("zt");%>
                <input type="radio" name="zt" value=""<%=zt.equals("")?" checked" :""%>>
                全部
                <input type="radio" name="zt" value="9"<%=zt.equals("9")?" checked" :""%>>
                审批中
                <input type="radio" name="zt" value="0"<%=zt.equals("0")?" checked" :""%>>
                未审
                <input type="radio" name="zt" value="1"<%=zt.equals("1")?" checked" :""%>>
                已审
                <input type="radio" name="zt" value="8"<%=zt.equals("8")?" checked" :""%>>
                完成
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
</html>--%>
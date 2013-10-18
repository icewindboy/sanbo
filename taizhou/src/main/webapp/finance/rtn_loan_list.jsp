<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.*"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.html.*"%>
<%@ page import="java.util.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "rtn_loan_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.finance.B_ReturnMoney B_ReturnMoneyBean = engine.erp.finance.B_ReturnMoney.getInstance(request);
  String retu = B_ReturnMoneyBean.doService(request, response);
%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<SCRIPT LANGUAGE="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function toDetail(oper, row){
  // 转到主从明细
  lockScreenToWait("处理中, 请稍候！");
  location.href='rtn_loan_edit.jsp?operate='+oper+'&rownum='+row;
}
function toretn(oper, row){
  // 转到主从明细
  lockScreenToWait("处理中, 请稍候！");
  location.href='rtn_loan_edit.jsp?operate='+oper+'&rownum='+row;
}
function sumitFixedQuery(oper)
 {
   lockScreenToWait("处理中, 请稍候！");
   fixedQueryform.operate.value = oper;
   fixedQueryform.submit();
 }
function showFixedQuery()
 {
   showFrame('fixedQuery', true, "", true);
 }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>还款情况管理</TD>
  </TR>
</TABLE>
<%
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = B_ReturnMoneyBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanDel=loginBean.hasLimits(pageCode, op_delete);

  engine.project.LookUp loanBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_LOAN);
  LookUp financeNativeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATIVE);
  LookUp financeEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);
  engine.project.LookUp bankaccountBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK_ACCOUNT);
  engine.project.LookUp bankBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  HtmlTableProducer table = B_ReturnMoneyBean.masterProducer;
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//引用结算方式
  if(B_ReturnMoneyBean.isApprove)
  {
    personBean.regData(list, "personid");
  }

  String typeClass = "class=edbox";
  String readonly = "";
  boolean isCanDelete = loginBean.hasLimits(pageCode, op_delete);
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE width="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
      <td class="td" nowrap>
      <%
        String key = "acountinout"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-7; String pageSize = String.valueOf(iPage);
       %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
    </td>
      <td class="td" align="right">
      <%
       if(loginBean.hasLimits(pageCode,op_add)){
        String qu = "showFixedQuery()";
        String add = "toDetail("+Operate.ADD+",-1)";
        String th = "toretn("+B_ReturnMoneyBean.ADD+",-1)";
      %>
      <%if(isCanSearch)%><input name="search" type="button" title = "查询" class="button" onClick="showFixedQuery()" value=" 查询(Q)"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
      <%if(loginBean.hasLimits(pageCode,op_add)) {  String ret = "location.href='("+B_ReturnMoneyBean.retuUrl+",-1)";%>
      <%if(B_ReturnMoneyBean.retuUrl!=null)%>
    <input name="button2222232" type="button" align="Right" title = "返回" class="button" onClick="location.href='<%=B_ReturnMoneyBean.retuUrl%>'" value=" 返回(C) "border="0"><pc:shortcut key="c" script='<%=ret%>'/><%}%>
   </TD>
   </TR>
   </TABLE>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td>
      <%if(loginBean.hasLimits(pageCode, op_add)){
       String add = "sumitForm("+Operate.ADD+",-1)";
       %>
        <input name="image" class="img" type="image" title="新增(A)" onClick="toDetail(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
        <pc:shortcut key="a" script='<%=add%>'/>
      <%}%>
      </td>
      <td nowrap>单据号</td>
      <td nowrap>贷款单据号</td>
      <td nowrap height='20'>银行</td>
      <td nowrap height='20'>贷款日期</td>
      <td nowrap height='20'>贷款金额</td>
      <td nowrap height='20'>还款日期</td>
      <td nowrap height='20'>还款金额</td>
      <td nowrap>剩余本息</td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%
      personBean.regConditionData(list,"deptid");
      loanBean.regData(list,"loanid");
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)
      {
        boolean isInit = false;
        String rowzt =list.getValue("state");
        String rowClass =list.getValue("state");
        if(rowClass.equals("0")){
          isInit = true;
          rowClass ="class=td";
        }

        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String sprid=list.getValue("approveid");
        String loginid=B_ReturnMoneyBean.loginid;
        String czyid = list.getValue("creatorid");
        boolean isCancer=rowzt.equals("1") && loginid.equals(sprid);
        String todetail =  "toDetail("+Operate.EDIT+","+list.getRow()+")";
        RowMap loanRow = loanBean.getLookupRow(list.getValue("loanid"));
        String yearate = loanRow.get("yearate");
        String loanmxid = list.getValue("loanmxid");
        String loanid = list.getValue("loanid");

    %>
    <tr id="tr_<%=list.getRow()%>" onclick="selectRow();" <%if(isCanEdit){%>onDblClick="selectRow();toDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)"<%}%> >
      <td <%=rowClass%> >
      <%if(isCanEdit){%><input name="image2" class="img" type="image" title="修改" onClick="toDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0"><%}%>
      <%if(isCanDelete&&rowzt.equals("0")&&loginid.equals(czyid)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0"><%}%>
      <%if(rowzt.equals("0")&&loginid.equals(czyid)){%><input name="image3" class="img" type="image" title='提交审批' onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
       <%if(rowzt.equals("1")&&loginid.equals(czyid)){%><input name="image3" class="img" type="image"  title='取消审批' onClick="if(confirm('确实要取消审批吗？'))sumitForm(<%=B_ReturnMoneyBean.CANCER_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
      <%if(rowzt.equals("1")){%><input name="image3" class="img" type="image" title='完成' onClick="if(confirm('确认完成吗？'))sumitForm(<%=B_ReturnMoneyBean.OVER%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>

      </td>
      <td nowrap <%=rowClass%> ><%=list.getValue("djh")%></td>
      <td nowrap <%=rowClass%> ><%=loanRow.get("loancode")%></td>
      <td nowrap <%=rowClass%> ><%=loanRow.get("yhmc")%></td>
      <td nowrap <%=rowClass%> ><%=loanRow.get("loandate")%></td>
      <td nowrap <%=rowClass%> align="right"><%=loanRow.get("loanfund")%></td>
      <td nowrap <%=rowClass%> ><%=list.getValue("retndate")%></td>
      <td nowrap <%=rowClass%> align="right"><%=list.getValue("retnfund")%></td>
      <td nowrap <%=rowClass%> align="right"><%=rowzt.equals("1")?B_ReturnMoneyBean.getBX(loanid,list.getValue("loanmxid"),yearate):""%></td>
     <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%
      list.next();
      }
      for(; i < 25; i++){
        out.print("<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");
        table.printBlankCells(pageContext, "class=td");//打印空的格子
        out.print("</tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;">
    <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
    <div class="queryPop" id="fixedQuery" name="fixedQuery">
      <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
      <TABLE cellspacing=3 cellpadding=0 border=0>
        <TR>
          <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <tr>
              <td noWrap class="tdTitle">&nbsp;贷款单号</td>
              <td noWrap class="td"><INPUT TYPE="TEXT"  NAME="loanid$loancode" VALUE="<%=B_ReturnMoneyBean.getFixedQueryValue("loanid$loancode")%>"  style="WIDTH:180"  class="edbox" >
              </td>
            </TR>
            <TR>
              <td noWrap class="tdTitle" align="center">&nbsp;单据号码&nbsp;</td>
              <td noWrap class="td"><input type="text" name="djh$a" value='<%=B_ReturnMoneyBean.getFixedQueryValue("djh$a")%>' maxlength='50' style="width:180" class="edbox"></td>
              <td noWrap class="tdTitle" align="center">--</td>
              <td noWrap class="td"><input type="text" name="djh$b" value='<%=B_ReturnMoneyBean.getFixedQueryValue("djh$b")%>' maxlength='50' style="width:180" class="edbox"></td>
            </TR>
            <TR>
              <TD nowrap class="tdTitle">还款日期</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 180px" name="retndate$a" value='<%=B_ReturnMoneyBean.getFixedQueryValue("retndate$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(retndate$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 180px" name="retndate$b" value='<%=B_ReturnMoneyBean.getFixedQueryValue("retndate$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(retndate$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <td nowrap class="tdTitle">还款金额</td>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH: 180px" name="retnfund$a" value='<%=B_ReturnMoneyBean.getFixedQueryValue("retnfund$a")%>' maxlength='20'  onKeyDown="return getNextElement();">
              </TD>
              <TD align="center" nowrap class="td">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 180px" name="retnfund$b" value='<%=B_ReturnMoneyBean.getFixedQueryValue("retnfund$b")%>' maxlength='20' onKeyDown="return getNextElement();">
               </TD>
            </TR>
             <TR>
                <td nowrap class="tdTitle">制单人</td>
                <td noWrap class="td"><input type="text" name="creator" value='<%=B_ReturnMoneyBean.getFixedQueryValue("creator")%>' maxlength='6' style="width:180" class="edbox"></td>
                <td noWrap class="tdTitle">业务员</td>
                <td   noWrap class="td">
                <pc:select name="personid" addNull="1" style="width:180">
                  <%=personBean.getList(B_ReturnMoneyBean.getFixedQueryValue("personid"))%> </pc:select>
                 </td>
            </TR>
            <tr>
              <td noWrap class="tdTitle">&nbsp;摘要</td>
              <td noWrap colspan="3" class="td"><INPUT TYPE="TEXT"  NAME="bz" VALUE="<%=B_ReturnMoneyBean.getFixedQueryValue("bz")%>"  style="WIDTH:430"  class="edbox" >
              </td>
            </TR>
            <TR>
              <TD class="tdTitle" nowrap>审核情况</TD>
              <TD colspan="3" nowrap class="td">
                <%
                  String [] zt = B_ReturnMoneyBean.zt;
                  String zt0="";
                  String zt9="";
                  String zt1="";
                  String zt8="";
                  String zt4="";
                  for(int k=0;k<zt.length;k++)
                  {
                    if(zt[k].equals("0"))
                      zt0 = "checked";
                    else if(zt[k].equals("9"))
                      zt9 = "checked";
                    else if(zt[k].equals("1"))
                      zt1 = "checked";
                    else if(zt[k].equals("8"))
                      zt8 = "checked";
                    else if(zt[k].equals("4"))
                      zt4 = "checked";
                  }
                %>
                <input type="checkbox" name="zt" value="0" <%=zt0%>>未审
                <input type="checkbox" name="zt" value="9" <%=zt9%>>审批中
                <input type="checkbox" name="zt" value="1" <%=zt1%>>已审
                <input type="checkbox" name="zt" value="8" <%=zt8%>>完成
            </TD>
            </TR>
            <TR>
              <%if(loginBean.hasLimits(pageCode,op_add)){ String qu = "sumitFixedQuery("+B_ReturnMoneyBean.OPERATE_SEARCH+",-1)";%>
              <TD nowrap colspan=3 height=30 align="center"><INPUT class="button" title = "查询" onClick="sumitFixedQuery(<%=B_ReturnMoneyBean.OPERATE_SEARCH%>)" type="button" value=" 查询(Q) " name="button" onKeyDown="return getNextElement();"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
              <%if(loginBean.hasLimits(pageCode,op_add)){ String clo = "hideFrame('fixedQuery')";%>
                <TD nowrap colspan=3 height=30 align="center"><INPUT class="button" title = "关闭"  onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(C) " name="button2" onKeyDown="return getNextElement();"><pc:shortcut key="c" script='<%=clo%>'/><%}%>
              </TD>
            </TR>
          </TABLE></TD>
        </TR>
      </TABLE>
    </DIV>
  </form>
<%out.print(retu);%>
</body>
</html>
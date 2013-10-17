<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.*"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.*,engine.html.*"%>
<%@ page import="java.util.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "acount_in_out";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.finance.B_AcountInOut b_AcountInOutBean = engine.erp.finance.B_AcountInOut.getInstance(request);
  String retu = b_AcountInOutBean.doService(request, response);
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
    location.href='acount_in_out_edit.jsp?operate='+oper+'&rownum='+row;
  }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>帐户收支情况</TD>
  </TR>
</TABLE>
<%
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = b_AcountInOutBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanDel=loginBean.hasLimits(pageCode, op_delete);

  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  LookUp financeNativeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATIVE);
  LookUp financeEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  HtmlTableProducer table = b_AcountInOutBean.masterProducer;
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//引用结算方式

  String typeClass = "class=edbox";
  String readonly = "";
  boolean isCanDelete = loginBean.hasLimits(pageCode, op_delete);
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE width="90%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
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
      %>
      <%if(isCanSearch)%><input name="search" type="button" title = "查询" class="button" onClick="showFixedQuery()" value=" 查询(Q)"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
    <%if(loginBean.hasLimits(pageCode,op_add)) {  String ret = "location.href='("+b_AcountInOutBean.retuUrl+",-1)";%>
    <%if(b_AcountInOutBean.retuUrl!=null)%>
    <input name="button2222232" type="button" align="Right" title = "返回" class="button" onClick="location.href='<%=b_AcountInOutBean.retuUrl%>'" value=" 返回(C) "border="0"><pc:shortcut key="c" script='<%=ret%>'/><%}%>
   </TD></TR>
  </TABLE>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <COLGROUP valign="middle" span="4">
    <COLGROUP width=60 align="center">
    <tr class="tableTitle">
      <td>
      <%if(loginBean.hasLimits(pageCode,op_add)) { String add = "toDetail("+Operate.ADD+",-1)";%>
      <input name="image" class="img" type="image" title="新增" onClick="toDetail(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%=add%>'/>
      <%}%>
        </td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%
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
        String loginid=b_AcountInOutBean.loginid;
        String czyid = list.getValue("creatorid");
        boolean isCancer=rowzt.equals("1") && loginid.equals(sprid);
    %>
    <tr id="tr_<%=list.getRow()%>" onclick="selectRow();" <%if(isCanEdit){%>onDblClick="selectRow();toDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)"<%}%> >
      <td class="td">
      <%if(isCanEdit){%><input name="image2" class="img" type="image" title="修改" onClick="toDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0"><%}%>
      <%if(isCanDelete&&rowzt.equals("0")&&loginid.equals(czyid)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0"><%}%>
      <%if(rowzt.equals("0")&&loginid.equals(czyid)){%><input name="image3" class="img" type="image" title='提交审批' onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
       <%if(isCancer){%><input name="image3" class="img" type="image"  title='取消审批' onClick="if(confirm('确实要取消审批吗？'))sumitForm(<%=b_AcountInOutBean.CANCER_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
      <%--if(rowzt.equals("1")){%><input name="image3" class="img" type="image" title='完成' onClick="if(confirm('确认完成吗？'))sumitForm(<%=b_AcountInOutBean.OVER%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}--%>

      </td>
     <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%
      list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
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
  <form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;">
    <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
    <div class="queryPop" id="fixedQuery" name="fixedQuery">
      <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
      <TABLE cellspacing=3 cellpadding=0 border=0>
        <TR>
          <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <td noWrap class="td" align="center">&nbsp;单据号码&nbsp;</td>
              <td noWrap class="td"><input type="text" name="billcode" value='<%=b_AcountInOutBean.getFixedQueryValue("billcode")%>' maxlength='50' style="width:120" class="edbox"></td>
              <td noWrap class="td" align="center">收支日期</td>
              <td nowrap class="td"><input type="text"  <%=typeClass%> name="ioDate" value='' maxlength="10" style="width:120"  onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=readonly%>>
              <a href="#"><img src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onClick="selectDate(date_born);"></a>
             </TR>
            <TR>
              <td nowrap class="tdTitle">收入</td>
              <td noWrap class="td"><input type="text" name="inMoney" value='<%=b_AcountInOutBean.getFixedQueryValue("inMoney")%>' maxlength='6' style="width:120" class="edbox"></td>
              <td nowrap class="tdTitle">支出</td>
              <td noWrap class="td"><input type="text" name="outMoney" value='<%=b_AcountInOutBean.getFixedQueryValue("outMoney")%>' maxlength='6' style="width:120" class="edbox"></td>
             </TR>
             <TR>
                <td nowrap class="tdTitle">制单人</td>
                <td noWrap class="td"><input type="text" name="creator" value='<%=b_AcountInOutBean.getFixedQueryValue("creator")%>' maxlength='6' style="width:120" class="edbox"></td>
                <td noWrap class="td" align="center">部门</td>
                <td noWrap class="td">
                <pc:select name="jg"  style="width:120">
                <%=deptBean.getList(b_AcountInOutBean.getFixedQueryValue("bm"))%>
              </pc:select></td>
            </TR>
            <TR>
              <%if(loginBean.hasLimits(pageCode,op_add)){ String qu = "sumitFixedQuery("+b_AcountInOutBean.OPERATE_SEARCH+",-1)";%>
              <TD nowrap colspan=3 height=30 align="center"><INPUT class="button" title = "查询" onClick="sumitFixedQuery(<%=b_AcountInOutBean.OPERATE_SEARCH%>)" type="button" value=" 查询(Q) " name="button" onKeyDown="return getNextElement();"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
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
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.*,engine.project.*,com.borland.dx.dataset.Locate"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_cancel = "op_cancel";
  String op_approve = "op_approve";

  String pageCode = "employee_change";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.person.shengyu.B_EmployeeChange b_employeechange = engine.erp.person.shengyu.B_EmployeeChange.getInstance(request);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_LIST);
  engine.project.LookUp personclassBean = engine.project.LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_CLASS);
  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
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
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript">
  function toDetail()
  {
  location.href='employee_change_detail.jsp';
  }
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
</script>
<%
  String retu = b_employeechange.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
%>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR><TD NOWRAP align="center">职工信息变动</TD></TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
     <%
       String key = "loginmanagedata";
       EngineDataSet list = b_employeechange.getMaterTable();
       pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize();
       String pageSize = ""+iPage;
       String s = "location.href='"+b_employeechange.retuUrl+"'";
     %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator>
     </td>
      <td class="td" align="right">
        <%if(isCanSearch){%>
        <input name="search" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)">
        <pc:shortcut key="q" script="showFixedQuery()" />
        <%}%>
        <input name="button2" type="button" class="button" onClick="location.href='<%=b_employeechange.retuUrl%>'" value=" 返回(C) "></td>
        <pc:shortcut key="c" script="<%=s%>" />
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <COLGROUP width=70 valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP valign="middle">
    <COLGROUP width=50 align="center">
    <tr class="tableTitle">
      <td nowrap>
       <%
       if(loginBean.hasLimits(pageCode, op_add)){
        %>
         <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
         <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/>
       <%}%>
       </td>
       <td   class="td" nowrap>员工编码</td>
       <td  class="td" nowrap>姓名</td>
       <td  class="td" nowrap>性别</td>
       <td  class="td" nowrap>工种</td>
       <td  class="td" nowrap>原职务</td>
       <td  class="td" nowrap>劳动合同号</td>
       <td  class="td" width = 80 nowrap>变动类别</td>
       <td  class="td" nowrap>变动原因</td>
       <td  class="td" width = 60 nowrap>变动日期</td>
       <td  class="td" nowrap>提交部门</td>
       <td  class="td" nowrap>备注</td>
       <td  class="td" nowrap>操作员</td>
       <td  class="td" nowrap>状态</td>
        <td  class="td" nowrap>审批人</td>
      <%-- <td nowrap>制单日期</td>--%>

    </tr>
    <%
      ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");opkey.add("4");opkey.add("5");
      ArrayList opval = new ArrayList(); opval.add("职员离职"); opval.add("部门调动"); opval.add("类别变动");opval.add("职务变迁");opval.add("职员复职");
      ArrayList[] lists  = new ArrayList[]{opkey, opval};
      personBean.regData(list, "personid");
      int i=0;
      list.first();
      for(; i < list.getRowCount(); i++) {
         String ss=list.getValue("chg_type");
         //RowMap  m_RowInfo = b_employeechange.getMasterRowinfo();
         String personid = list.getValue("personid");
         RowMap personRow = personBean.getLookupRow(personid);
         String bm = personRow.get("bm");
         String xm = personRow.get("xm");
         int te = list.getBigDecimal("chg_type").intValue() - 1;
         boolean isInit = false;
         String state = list.getValue("state");
         String rowClass =list.getValue("state");
         if(rowClass.equals("0")){
           isInit = true;
         }
         rowClass = engine.action.BaseAction.getStyleName(rowClass);
         String chang_ID = list.getValue("chang_ID");
         String showstring = "<a href='javascript:' onClick=\"if('"+state+"'=='0') {return;} else {openUrlOpt1('../pub/approve_result.jsp?operate=0&project=employee_change&id="+chang_ID+"');}\">已审</a>";
         String shenpizong = "<a href='javascript:' onClick=\"if('"+state+"'=='0') {return;} else {openUrlOpt1('../pub/approve_result.jsp?operate=0&project=employee_change&id="+chang_ID+"');}\">审批中</a>";
         String loginId=b_employeechange.loginId;
         String approveID=list.getValue("approveID");
         String rowstate =list.getValue("state");
         boolean canApprove = false;
         boolean canCancer = rowstate.equals("1");
         if(approveID.equals(""))
         {
           canApprove = isInit&&loginBean.getUserID().equals(list.getValue("creatorID"));
           canCancer = canCancer&&loginBean.getUserID().equals(list.getValue("creatorID"));
         }
         else
         {
           canApprove = isInit&&loginBean.getUserID().equals(approveID);
           canCancer = canCancer&&loginBean.getUserID().equals(approveID);
         }

         boolean cansubmit=false;
        boolean submitType = b_employeechange.submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
        String creatorID = list.getValue("creatorID");
        if(submitType&&creatorID.equals(loginId))
          cansubmit=true;
        else if(!submitType&&loginBean.hasLimits(pageCode, op_over))
          cansubmit=true;
        else if(!submitType&&creatorID.equals(loginId))
          cansubmit=true;
    %>
    <tr onDblClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)" >
      <td  nowrap align="center" <%=rowClass%>>
         <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
          <%
          //if(isInit&&loginBean.hasLimits(pageCode, op_approve)){;isInit&&cansubmit
          if(isInit&&cansubmit){
          %>
        <input name="image3" class="img" type="image"  title='提交审批' onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0">
         <%}%>
    <%if(canCancer){%>
         <input name="image3" class="img" type="image"  title='取消审批' onClick="sumitForm(<%=b_employeechange.CANCER_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
     </td>
      <td  nowrap <%=rowClass%>><%=personRow.get("bm")%></td>
      <td  nowrap <%=rowClass%>><%=xm%></td>
      <td  nowrap <%=rowClass%>><%=personRow.get("sex").equals("1")?"男":"女"%></td>
      <td  nowrap <%=rowClass%>><%=personclassBean.getLookupName(personRow.get("lb"))%></td>
      <%String kk=list.getValue("chg_type");%>
      <td  nowrap <%=rowClass%>><%=kk.equals("4")?list.getValue("chg_before"):""%></td>
      <td  nowrap <%=rowClass%>><%=b_employeechange.getHth(personid)%></td>
      <td  nowrap <%=rowClass%> align="left"><%=opval.get(te)%></td>
      <td  nowrap <%=rowClass%>><%=list.getValue("chg_reason")%></td>
      <td  nowrap <%=rowClass%>><%=list.getValue("chg_date")%></td>
      <td  nowrap <%=rowClass%>><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td  nowrap <%=rowClass%>><%=list.getValue("memo")%></td>
      <td  nowrap <%=rowClass%>><%=list.getValue("creator")%></td>
     <%-- <td class="td" nowrap <%=rowClass%>><%=list.getValue("lsh")%></td>--%>
      <td  nowrap <%=rowClass%>><%=list.getValue("state").equals("0")?"未审":(list.getValue("state").equals("1")?showstring:shenpizong)%></td>
      <td  nowrap <%=rowClass%> ><%=personBean.getLookupName(list.getValue("approveID"))%></td>
</tr>
    <%
        list.next();
      }
      for(; i < iPage; i++){
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
       <td class="td"></td>
       <td class="td"></td>
       <td class="td"></td>
       <td class="td"></td>
</tr>
    <%}%>
  </table>
  </form>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);

  function sumitFixedQuery(oper)
  {
    lockScreenToWait("处理中, 请稍候！");
    fixedQueryform.operate.value = oper;
    fixedQueryform.submit();
  }
  function showFixedQuery(){
    showFrame('fixedQuery', true, "", true);
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
  </SCRIPT>
  <form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
    <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
    <div class="queryPop" id="fixedQuery" name="fixedQuery">
      <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
      <TABLE cellspacing=3 cellpadding=0 border=0>
        <TR>
          <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
             <%--<TD nowrap class="td">单据号</TD>
              <TD class="td" nowrap>
              <input type="text" name="username" value='<%=b_employeechange.getFixedQueryValue("djh")%>' maxlength='6' style="width:131" class="edbox">
              </TD>--%>


            </TR>
            <TR>
              <td noWrap class="td" align="center">员工编码</td>
              <td noWrap class="td"><input type="text" name="bm" value='<%=b_employeechange.getFixedQueryValue("bm")%>' maxlength='20' style="width:131" class="edbox"></td>
              <td noWrap class="td" align="center">&nbsp;员工姓名&nbsp;</td>
              <td noWrap class="td"><input type="text" name="xm" value='<%=b_employeechange.getFixedQueryValue("xm")%>' maxlength='50' style="width:131" class="edbox"></td>
            </TR>
            <TR>
              <TD nowrap class="td">变动日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="chg_date$a" value='<%=b_employeechange.getFixedQueryValue("chg_date$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(chg_date$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
               </TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="chg_date$b" style="WIDTH: 130px" name="chg_date$b" value='<%=b_employeechange.getFixedQueryValue("chg_date$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(chg_date$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
              </TD>
            </TR>
            <TR>
            <%--  <TD nowrap class="td">制单日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="createDate$a" value='<%=b_employeechange.getFixedQueryValue("createDate$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(createDate$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="createDate$b" style="WIDTH: 130px" name="createDate$b" value='<%=b_employeechange.getFixedQueryValue("createDate$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(createDate$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>--%>
            </TR>
            <TR>
              <td noWrap class="td" align="center">操作员</td>
              <td noWrap class="td">
              <input type="text" name="creator" value='<%=b_employeechange.getFixedQueryValue("creator")%>' maxlength='50' style="width:130px" class="edbox">
              </td>
            <td noWrap class="td" align="center">变动类型</td>
               <td>
                <pc:select name="chg_type"  addNull="1" style="width:130" >
                <%=b_employeechange.listToOption(lists, opkey.indexOf(b_employeechange.getFixedQueryValue("chg_type")))%>
                </pc:select>
               </td>
            <%--<TD nowrap class="td">登录名</TD>
              <TD class="td" nowrap>
              <input type="text" name="username" value='<%=b_employeechange.getFixedQueryValue("username")%>' maxlength='6' style="width:131" class="edbox">
              </TD>--%>
            </TR>
            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%String state = b_employeechange.getFixedQueryValue("state");%>
                <input type="radio" name="state" value=""<%=state.equals("")?" checked" :""%>>
                全部
                <input type="radio" name="state" value="9"<%=state.equals("9")?" checked" :""%>>
                审批中
                <input type="radio" name="state" value="0"<%=state.equals("0")?" checked" :""%>>
                未审
                <input type="radio" name="state" value="1"<%=state.equals("1")?" checked" :""%>>
                已审
                </TD>
            </TR>
            <TR>
              <TD nowrap colspan=3 height=30 align="center">
                <% String qu = "sumitFixedQuery("+b_employeechange.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=b_employeechange.FIXED_SEARCH%>)" type="button" value=" 查询(K) " name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="k" script="<%=qu%>" />
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script="hideFrame('fixedQuery')" />
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
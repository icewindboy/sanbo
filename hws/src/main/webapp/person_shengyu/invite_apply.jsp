<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.*,engine.html.*"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over   = "op_over";
  String pageCode = "invite_apply";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.person.shengyu.B_InviteApply b_InviteApplyBean = engine.erp.person.shengyu.B_InviteApply.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_BOTH);//BEAN_DEPT
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp dutyBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_DUTY);
  HtmlTableProducer table = b_InviteApplyBean.masterProducer;
  EngineDataSet list = b_InviteApplyBean.getMaterTable();
  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanDel=loginBean.hasLimits(pageCode, op_delete);


  String SYS_APPROVE_ONLY_SELF =b_InviteApplyBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  boolean isApproveOnly = SYS_APPROVE_ONLY_SELF.equals("1") ? true : false;//true仅制单人可提交
  RowMap m_RowInfo = b_InviteApplyBean.getMasterRowinfo();   //行到主表的一行信息
  String loginId = b_InviteApplyBean.loginId;
  LookUp personNativeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATIVE);
  LookUp personEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);


  String typeClass = "class=edbox";
  String readonly = "";
  //boolean isCanEdit = true;
  boolean isCanDelete = true;

  EngineDataSet dsDWTX = b_InviteApplyBean.getMaterTable();

%>
<%
  String retu = b_InviteApplyBean.doService(request, response);
if(retu.indexOf("location.href=")>-1)
{
  out.print(retu);
  return;
}
String curUrl = request.getRequestURL().toString();

//ArrayList opkey = new ArrayList(); opkey.add("0"); opkey.add("2");

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
  location.href='invite_apply_edit.jsp';
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
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD NOWRAP align="center">招聘申请</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
     <%
       String key = "loginmanagedata";
EngineDataSet tmp=b_InviteApplyBean.getMaterTable();
pageContext.setAttribute(key, tmp);
int iPage = loginBean.getPageSize();
String pageSize = ""+iPage;
     %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator>
     </td>
      <td class="td" align="right">
        <%if(loginBean.hasLimits(pageCode,op_add)) { String qu = "showFixedQuery()";%>
        <%if(isCanSearch)%><input name="search" type="button" title = "查询"  class="button" onClick="showFixedQuery()" value=" 查询(Q) "><pc:shortcut key="q" script='<%=qu%>'/><%}%>
        <%if(loginBean.hasLimits(pageCode,op_add)) { String ret = "location.href='("+b_InviteApplyBean.retuUrl+",-1)";%>
        <input name="button2" type="button" title = "返回" class="button" onClick="location.href='<%=b_InviteApplyBean.retuUrl%>'" value=" 返回(C) "></td><pc:shortcut key="c" script='<%=ret%>'/><%}%>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
 <%--     <td nowrap>
      <%if(loginBean.hasLimits(pageCode,op_add))  {String add = "sumitForm("+Operate.ADD+",-1)";%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0"><pc:shortcut key="a" script='<%=add%>'/>
     <%}%>
     </td>--%>
      <td nowrap>
       <%
       if(loginBean.hasLimits(pageCode, op_add)){
        %>
         <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
         <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/>
       <%}%>
       </td>
       <td nowrap height='20'>申请单编号</td>
        <td nowrap height='20'>申请部门</td>
        <td nowrap height='20'>申请人</td>
        <td nowrap height='20'>需求日期</td>
        <td nowrap height='20'>工作性质</td>
        <td nowrap height='20'>职位</td>
        <td nowrap height='20'>人数</td>
        <td nowrap height='20'>已安排计划数</td>

     <%table.printTitle(pageContext, "height='20'");%>
     </tr>
   <%--  <tr>
   </td>
   <td nowrap>申请部门</td>
   <td nowrap>申请人</td>
   <td nowrap>需求日期</td>
   <td nowrap>工作性质</td>
   <td nowrap>职位</td>
   <td nowrap>人数</td>
   <td nowrap>已安排计划数</td>
   <td nowrap>状态</td>
   <td nowrap>审批人</td>
   <td nowrap>状态描述</td>
   <td nowrap>制单人</td>
   <td nowrap>制单日期</td>
   <td nowrap>备注</td>
   </tr>--%>
    <%//b_InviteApplyBean.fetchData(loginBean.getRowMin(request), loginBean.getRowMax(request), true);
      int i=0;
     list.first();
     int count = list.getRowCount();
     for(; i < count; i++) {
       list.goToRow(i);
       boolean isInit = false;
       String rowClass =list.getValue("state");
       if(rowClass.equals("0"))
         isInit=true;
       String creatorID=list.getValue("creatorID");
       boolean isShow = isApproveOnly ? (loginId.equals(creatorID) && isInit) : isInit;//如果时只有制单人才可以提交审批。制单人等于登录人才显示
       String apply_ID = list.getValue("apply_ID");
       //boolean isReference = b_InviteApplyBean.isReference(apply_ID);//进货单是否被入库，如果入库颜色要变化
       rowClass = engine.action.BaseAction.getStyleName(rowClass);
       //rowClass = engine.action.BaseAction.getStyleName(isReference ? "-1" : rowClass);
       String zt = list.getValue("state");
       String approveID = list.getValue("approveID");
       boolean isCancel = zt.equals("1");
       boolean kk=loginBean.hasLimits(pageCode, op_over);
       boolean isComplete  = zt.equals("1") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
      isCancel =  isCancel && loginId.equals(approveID);//是否可以取消审批


    %>
    <tr onDblClick="sumitForm(<%=b_InviteApplyBean.VIEW_DETAIL%>,<%=i%>)" >
      <td class="td" nowrap align="center">
      <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=b_InviteApplyBean.VIEW_DETAIL%>,<%=i%>)" src="../images/edit.gif" border="0">
      <%if(isShow){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
      <%if(isCancel){%><input name="image3" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=b_InviteApplyBean.CANCLE_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
       <%if(isComplete){%><input name="image5" class="img" type="image" title='完成' onClick="if(confirm('是否强制完成该纪录？'))sumitForm(<%=b_InviteApplyBean.COMPLETE%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%></td>
      </td>

      <td nowrap <%=rowClass%> height='20'><%=list.getValue("apply_code")%></td>
       <td nowrap <%=rowClass%> height='20'><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
       <td nowrap <%=rowClass%> height='20'><%=personBean.getLookupName(list.getValue("personid"))%></td>
       <td nowrap <%=rowClass%> height='20'><%=list.getValue("need_date")%></td>
       <td nowrap <%=rowClass%> height='20'><%=list.format("job_kind")%></td>
       <td nowrap <%=rowClass%>  height='20'><%=list.format("job")%></td>
       <td nowrap <%=rowClass%> height='20' align="right"><%=list.format("recruit_num")%></td>
       <td nowrap <%=rowClass%> height='20' align="right"><%=list.getValue("recruit_numa")%></td>

       <%table.printCells(pageContext, rowClass);%>
       <%--  <td class="td" nowrap><%=deptBean.getLookupName(list.getValue("deptid"))%></>
       <td class="td" nowrap><%=personBean.getLookupName(list.getValue("personid"))%></td>
       <td class="td" nowrap><%=list.format("need_date")%></td>
       <td class="td" nowrap><%=list.format("job_kind")%></td>
       <td class="td">&nbsp;</td>
       <td class="td" nowrap><%=list.format("recruit_num")%></td>
       <td class="td">&nbsp;</td>
       <td class="td">&nbsp;</td>
       <td class="td">&nbsp;</td>
       <td class="td"><%=list.getValue("creator")%></td>
       <td class="td"><%=list.getValue("createDate")%></td>
       <td class="td" nowrap><%=list.format("memo")%></td>--%>
     </tr>

    <%
      list.next();
     }
     for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
       <td class="td">&nbsp;</td>

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
            <%--<td noWrap class="td" align="center">&nbsp;培训编号&nbsp;</td>
            <td noWrap class="td"><input type="text" name="pxbh" value='<%=b_InviteApplyBean.getFixedQueryValue("pxbh")%>' maxlength='50' style="width:120" class="edbox"></td>--%>
                   <TD class="tdtitle" nowrap>申请部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:130">
         <%=deptBean.getList(b_InviteApplyBean.getFixedQueryValue("deptid"))%></pc:select>
               <td class="tdtitle" nowrap>姓名</td>
                <TD nowrap class="td"><pc:select name="personid" addNull="1" style="width:130">
         <%=personBean.getList(b_InviteApplyBean.getFixedQueryValue("personid"))%></pc:select>

             </TR>
        <TR>
              <TD nowrap class="tdtitle">需求日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="need_date$a" style="WIDTH: 130px" name="need_date$a" value='<%=b_InviteApplyBean.getFixedQueryValue("need_date$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(need_date$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="need_date$b" style="WIDTH: 130px" name="need_date$b" value='<%=b_InviteApplyBean.getFixedQueryValue("need_date$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(need_date$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>



                              <TR>
              <TD nowrap class="tdtitle">制单日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="createDate$a" style="WIDTH: 130px" name="createDate$a" value='<%=b_InviteApplyBean.getFixedQueryValue("createDate$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(createDate$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="createDate$b" style="WIDTH: 130px" name="createDate$b" value='<%=b_InviteApplyBean.getFixedQueryValue("createDate$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(createDate$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>

                             <TR>
              <TD class="tdtitle" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
              <%//String s = b_InviteJobBean.getFixedQueryValue("state");
                String [] state = b_InviteApplyBean.state;
                String zt0="";
                String zt9="";
                String zt1="";
                String zt8="";
                String zt4="";
                for(int k=0;k<state.length;k++)
                {
                  if(state[k].equals("0"))
                    zt0 = "checked";
                  else if(state[k].equals("9"))
                    zt9 = "checked";
                  else if(state[k].equals("1"))
                    zt1 = "checked";
                  else if(state[k].equals("8"))
                    zt8 = "checked";
                  else if(state[k].equals(""))
                    zt4 = "checked";
                }
                %>

                <input type="checkbox" name="state" value="0"<%=zt0%>>
                未审
                <input type="checkbox" name="state" value="9"<%=zt9%>>
                审批中
                <input type="checkbox" name="state" value="1"<%=zt1%>>
                已审
                <input type="checkbox" name="state" value="8"<%=zt8%>>
                已完成
               <input type="checkbox" name="state" value="" <%=zt4%>>
                全部
                </TD>
            </TR>



            <TR>
              <%if(loginBean.hasLimits(pageCode,op_add))  {String qu = "sumitFixedQuery("+b_InviteApplyBean.OPERATE_SEARCH+",-1)";%>
              <TD nowrap colspan=3 height=30 align="center">
                 <INPUT class="button" title = "查询" onClick="sumitFixedQuery(<%=b_InviteApplyBean.OPERATE_SEARCH%>)" type="button" value=" 查询(Q) " name="button" onKeyDown="return getNextElement();"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
                 <%if(loginBean.hasLimits(pageCode,op_add))  {String clo = "hideFrame('fixedQuery')";%>
                 <td class="td" nowrap align="center">
                 <INPUT class="button" title = "关闭" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(C) " name="button2" onKeyDown="return getNextElement();"><pc:shortcut key="c" script='<%=clo%>'/><%}%>
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
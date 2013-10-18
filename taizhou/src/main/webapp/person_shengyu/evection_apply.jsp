<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.*"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "evection_apply";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.person.shengyu.B_EvectionApply b_EvectionApplyBean = engine.erp.person.shengyu.B_EvectionApply.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_BOTH);//BEAN_DEPT
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp dutyBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_DUTY);
  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanDel=loginBean.hasLimits(pageCode, op_delete);

  RowMap m_RowInfo = b_EvectionApplyBean.getMasterRowinfo();   //行到主表的一行信息
  LookUp personNativeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATIVE);
  LookUp personEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);

  String typeClass = "class=edbox";
  String readonly = "";
  //boolean isCanEdit = true;
  boolean isCanDelete = true;

  EngineDataSet dsDWTX = b_EvectionApplyBean.getMaterTable();

%>
<%
  String retu = b_EvectionApplyBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();

  ArrayList opkey = new ArrayList(); opkey.add("0"); opkey.add("2");

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
  location.href='evection_apply_edit.jsp';
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
    <TD NOWRAP align="center">出差申请</TD>
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
       EngineDataSet tmp=b_EvectionApplyBean.getMaterTable();
       pageContext.setAttribute(key, tmp);
       int iPage = loginBean.getPageSize();
       String pageSize = ""+iPage;
     %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator>
     </td>
      <td class="td" align="right">
        <%if(loginBean.hasLimits(pageCode,op_add)) { String qu = "showFixedQuery()";%>
        <%if(isCanSearch)%><input name="search" type="button" title = "查询"  class="button" onClick="showFixedQuery()" value=" 查询(Q) "><pc:shortcut key="q" script='<%=qu%>'/><%}%>
        <%if(loginBean.hasLimits(pageCode,op_add)) { String ret = "location.href='("+b_EvectionApplyBean.retuUrl+",-1)";%>
        <input name="button2" type="button" title = "返回" class="button" onClick="location.href='<%=b_EvectionApplyBean.retuUrl%>'" value=" 返回(C) "></td><pc:shortcut key="c" script='<%=ret%>'/><%}%>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>
      <%if(loginBean.hasLimits(pageCode,op_add))  {String add = "sumitForm("+Operate.ADD+",-1)";%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0"><pc:shortcut key="a" script='<%=add%>'/>
     <%}%>
     </td>
      <td nowrap>姓名</td>
      <td nowrap>部门</td>
      <td nowrap>职称</td>
      <td nowrap>代理人</td>
      <td nowrap>出差地点</td>
      <td nowrap>出差原因</td>
      <td nowrap>旅费预算</td>
      <td nowrap>预支旅费</td>


    </tr>
    <%//b_EvectionApplyBean.fetchData(loginBean.getRowMin(request), loginBean.getRowMax(request), true);
      EngineDataSet list = b_EvectionApplyBean.getMaterTable();
      int i=0;
      list.first();
      for(; i < list.getRowCount(); i++) {
    %>
    <tr onDblClick="sumitForm(<%=b_EvectionApplyBean.VIEW_DETAIL%>,<%=i%>)" >
      <td class="td" nowrap align="center">
      <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=b_EvectionApplyBean.VIEW_DETAIL%>,<%=i%>)" src="../images/edit.gif" border="0">
       <%if((loginBean.hasLimits(pageCode,op_delete))){%>
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i %>)" src="../images/del.gif" border="0">
      <%}%>
      </td>
      <td class="td" nowrap><%=personBean.getLookupName(list.getValue("personid"))%></td>
      <td class="td" nowrap><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td class="td" nowrap><%=list.format("duty")%></td>
      <td class="td" nowrap><%=list.format("deputy")%></td>
      <td class="td" nowrap><%=list.format("evection_addr")%></td>
      <td class="td" nowrap><%=list.format("evection_reason")%></td>
      <td class="td" nowrap align="right"><%=list.format("fee_budget")%></td>
      <td class="td" nowrap align="right"><%=list.format("fee_prepay")%></td>
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
              <td noWrap class="td"><input type="text" name="pxbh" value='<%=b_EvectionApplyBean.getFixedQueryValue("pxbh")%>' maxlength='50' style="width:120" class="edbox"></td>--%>
              <td noWrap class="tdtitle" align="center">&nbsp;姓名&nbsp;</td>
              <td noWrap class="td"><input type="text" id="evection_ID$xm" name="evection_ID$xm" value='<%=b_EvectionApplyBean.getFixedQueryValue("evection_ID$xm")%>' maxlength='50' style="width:131" class="edbox"></td>
              <td noWrap class="tdtitle" align="center">职务</td>
              <TD nowrap class="td"><pc:select name="duty" addNull="1" style="width:160">
             <%=dutyBean.getList(b_EvectionApplyBean.getFixedQueryValue("duty"))%></pc:select>
              <%--<td noWrap class="td"><input type="text" id="pxjs" name="pxjs" value='<%=b_EvectionApplyBean.getFixedQueryValue("pxjs")%>' maxlength='6' style="width:120" class="edbox"></td>--%>
             </TR>
            <TR>
              <TD class="tdtitle" nowrap>部门</TD>
              <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:130">
         <%=deptBean.getList(b_EvectionApplyBean.getFixedQueryValue("deptid"))%></pc:select>
            </TR>
            <TR>
              <%if(loginBean.hasLimits(pageCode,op_add))  {String qu = "sumitFixedQuery("+b_EvectionApplyBean.OPERATE_SEARCH+",-1)";%>
              <TD nowrap colspan=3 height=30 align="center">
                 <INPUT class="button" title = "查询" onClick="sumitFixedQuery(<%=b_EvectionApplyBean.OPERATE_SEARCH%>)" type="button" value=" 查询(Q) " name="button" onKeyDown="return getNextElement();"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
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




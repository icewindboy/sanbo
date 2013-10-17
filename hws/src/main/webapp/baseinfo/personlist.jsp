<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, com.borland.dx.dataset.Locate"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
if(!loginBean.hasLimits("personlist", request, response))
    return;
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_LIST);
  engine.erp.system.B_Person personBean = engine.erp.system.B_Person.getInstance(request);
  engine.erp.system.B_Role roleBean = engine.erp.system.B_Role.getInstance(request);
  engine.erp.system.B_LimitsInfo limitsInfoBean = engine.erp.system.B_LimitsInfo.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript">
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
    <TD NOWRAP align="center">员工信息列表</TD>
  </TR></TABLE>
<%String retu = personBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
     <%String key = "loginmanagedata"; pageContext.setAttribute(key, personBean.dsPerson);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"><%personBean.openUsersRoles();%></pc:dbNavigator></td>
      <td class="td" align="right"><input name="search" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)">
        <pc:shortcut key="s" script='showFixedQuery()'/>
        <input name="button2" type="button" class="button" onClick="location.href='../pub/main.jsp'" value="返回(C)"></td>
        <pc:shortcut key="c" script="location.href='../pub/main.jsp'"/>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=45 align="center">操作
        <%--input name="image" class="img" type="image" title="新增" onClick="sumitForm(<%=personBean.OPERATE_ADD%>,-1)" src="../images/add.gif" border="0"--%>
      </td>
      <td nowrap width=70>员工编码</td>
      <td nowrap>员工姓名</td>
      <td nowrap>登录名</td>
      <td nowrap>部门</td>
      <td nowrap>角色</td>
      <td nowrap>电话</td>
      <td nowrap>电子邮件</td>
      <td nowrap>登录状态</td>
    </tr>
    <%//personBean.fetchData(loginBean.getRowMin(request), loginBean.getRowMax(request), true);
      EngineDataSet list = personBean.dsPerson;
      EngineDataSet roleinfo = personBean.dsPersonRole;
      if(!roleinfo.isOpen())
        personBean.openUsersRoles();
      list.first();
      int i=0;
      for(; i < list.getRowCount(); i++)
      {
        //得到角色名称
        StringBuffer rolename=new StringBuffer();
        int locate = Locate.FIRST;
        while(personBean.locateDataSet(roleinfo, "personid", list.getValue("personid"), locate))
        {
          locate = Locate.NEXT;
          rolename.append(roleBean.getRoleName(roleinfo.getValue("roleid")));
          rolename.append(" ");
        }
        int row = list.getRow();
    %>
    <tr onDblClick="sumitForm(<%=personBean.OPERATE_EDIT%>,<%=row%>)">
      <td class="td" nowrap><input name="image2" class="img" type="image" title="修改" onClick="sumitForm(<%=personBean.OPERATE_EDIT%>,<%=row%>)" src="../images/edit.gif" border="0">
        <%--input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=personBean.OPERATE_DEL%>,<%=i%>)" src="../images/del.gif" border="0"--%>
        <input name="image" type="image" title="清除密码" onClick="if(confirm('是否清除该员工密码？')) sumitForm(<%=personBean.OPERATE_CLEAR%>,<%=row%>)" src="../images/clear.gif" border="0">
      </td>
      <td class="td" nowrap align="center"><%=list.format("bm")%></td>
      <td class="td" nowrap><%=list.format("xm")%></td>
      <td class="td" nowrap><%=list.format("username")%></td>
      <td class="td" nowrap><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td class="td" nowrap><%=rolename.toString()%></td>
      <td class="td" nowrap><%=list.format("phone")%></td>
      <td class="td" nowrap><a href="mailto:<%=list.format("email")%>"><%=list.format("email")%></a></td>
      <td class="td" nowrap nowrap width=100><%boolean isuse = list.getValue("isuse").equals("1");
	  	%><input type="radio" name="isuse_<%=row%>" value="1" onclick='<%=isuse ? "return;" : ""%>sumitForm(<%=personBean.OPERATE_CHANGINFO%>,<%=row%>)'<%=isuse?" checked":""%>>启用
        <input type="radio" name="isuse_<%=row%>" value="0" onclick='<%=!isuse ? "return;" : ""%>sumitForm(<%=personBean.OPERATE_CHANGINFO%>,<%=row%>)'<%=isuse?"":" checked"%>>禁用</td>
    </tr>
    <%  list.next();
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
    </tr>
    <%}%>
  </table>
  </form>
  <script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
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
  </script>
  <form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
    <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">

  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <td noWrap class="td" align="center">员工编码</td>
              <td noWrap class="td"><input type="text" name="bm" value='<%=personBean.getFixedQueryValue("bm")%>' maxlength='6' style="width:180" class="edbox"></td>
            </TR>
            <TR>
              <td noWrap class="td" align="center">&nbsp;员工姓名&nbsp;</td>
              <td noWrap class="td"><input type="text" name="xm" value='<%=personBean.getFixedQueryValue("xm")%>' maxlength='50' style="width:180" class="edbox"></td>
            </TR>
            <%if(!personBean.isDeptAddPerson){%>
            <TR>
              <td noWrap class="td" align="center">登录名</td>
              <td noWrap class="td"><input type="text" name="username" value='<%=personBean.getFixedQueryValue("username")%>' maxlength='50' style="width:180" class="edbox"></td>
            </TR>
            <TR>
              <td noWrap class="td" align="center">部门</td>
              <td noWrap class="td"><pc:select name="deptid" style="width:180" addNull="1">
                <%=deptBean.getList(personBean.getFixedQueryValue("deptid"))%></pc:select></td>
            </TR>
            <%}%>
            <TR>
              <TD nowrap colspan=3 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=personBean.OPERATE_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
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

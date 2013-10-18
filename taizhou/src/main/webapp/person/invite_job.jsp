<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.*,java.math.BigDecimal,java.util.*"%>
<%

  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "invite_job";

  if(!loginBean.hasLimits("invite_job", request, response))
    return;
  engine.erp.person.B_InviteJob B_InviteJobBean = engine.erp.person.B_InviteJob.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_LIST);
  String retu = B_InviteJobBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = B_InviteJobBean.getInviteTable();
  String curUrl = request.getRequestURL().toString();
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type"  content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<SCRIPT language="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value  = row;
    form1.submit();
  }
  function sumitFixedQuery(oper)
 {

   lockScreenToWait("处理中, 请稍候！");
   fixedQueryform.operate.value = oper;
   fixedQueryform.submit();
  }
  function executeQuery()
  {
    if(fixedQueryform.nf.value==""||fixedQueryform.yf.value=="");
    {
      alert("年份,月份必须都选择才有效!");
      return;
    }
  }
  function showFixedQuery()
   {
    showFrame('fixedQuery',true,"",true);
   }
   function showImportForm()
    {
     showFrame('importForm',true,"",true);
   }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD nowrap align="center">招聘计划</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <TABLE width="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>
     <%
         String key = "ppdfsgg";
         pageContext.setAttribute(key, list);
         int iPage = loginBean.getPageSize();
         String pageSize = ""+iPage;
      %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
        <%
          String t="sumitForm("+B_InviteJobBean.MONTH_CHANGE+",-1)";//saleOrderBean.DETAIL_CHANGE;
        %>
      <TD align="right">
      <%if(loginBean.hasLimits(pageCode,op_add)) { String save = "sumitForm("+Operate.POST+",-1)";%>
      <input name="button" type="button" title = "保存" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)"><pc:shortcut key="s" script='<%=save%>'/><%}%>
      <%if(loginBean.hasLimits(pageCode,op_add)) { String qu = "showFixedQuery()";%>
      <INPUT class="button" title = "查询" onClick="showFixedQuery()" type="button" value="查询(Q)" name="Query" onKeyDown="return getNextElement();"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
      <%if(loginBean.hasLimits(pageCode,op_add)) { String f = "location.href='("+B_InviteJobBean.retuUrl+",-1)";%>
      <input name="button2" type="button" align="Right" title = "返回" class="button" onClick="location.href='<%=B_InviteJobBean.retuUrl%>'" value="返回(C)" border="0"><pc:shortcut key="c" script='<%=f%>'/><%}%>
      </TD>
    </TR>
  </TABLE>

  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr>
       <td rowspan="2"  class="tableTitle" nowrap>
       <input type="hidden" name="multiIdInput" value="" onchange="">
       <input name="image" class="img" type="image" title="新增" onClick="showImportForm()" src="../images/add.gif" border="0">
       </td>
      <td  class="tableTitle" rowspan="2" align="center" nowrap height="36">招聘部门</td>
      <td  class="tableTitle" colspan="12" align="center" nowrap height="16">计划招聘人数</td>
      <td  class="tableTitle" rowspan="2" align="center" nowrap height="36">编外招聘</td>
    </tr>
    <tr>
      <td  class="tableTitle" nowrap align="center" height="14">一月</td>
      <td  class="tableTitle" nowrap align="center" height="14">二月</td>
      <td  class="tableTitle" align="center" nowrap height="14">三月</td>
      <td  class="tableTitle" align="center" nowrap height="14">四月</td>
      <td  class="tableTitle" height="14" align="center" nowrap>五月</td>
      <td  class="tableTitle" height="14" align="center" nowrap>六月</td>
      <td  class="tableTitle" height="14" align="center" nowrap>七月</td>
      <td  class="tableTitle" height="14" align="center" nowrap>八月</td>
      <td  class="tableTitle" height="14" align="center" nowrap>九月</td>
      <td  class="tableTitle" height="14" align="center" nowrap>十月</td>
      <td  class="tableTitle" height="14" align="center" nowrap>十一月</td>
      <td  class="tableTitle" height="14" align="center" nowrap>十二月</td>
    </tr>
        <%
        //RowMap[] rows=B_InviteJobBean.getDetailRowinfos();
        //RowMap detail = null;
        int i=0;
        list.first();
        for(; i<list.getRowCount(); i++)
        {
          //detail = rows[i];
          String deptid = list.getValue("deptid");
          String fieldclass="edFocused_r";
          String readOnly="";
        %>
       <tr>
       <td class="td" width=45 align="center">
        <input name="image3" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle">
       </td>
      <td class="td" nowrap align="left"><%=deptBean.getLookupName(list.getValue("deptid"))%><INPUT TYPE="HIDDEN" NAME="deptid_<%=i%>" VALUE="<%=list.getValue("deptid")%>" style="width:100%"  CLASS="edFocused_r"></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="jan_<%=i%>" VALUE="<%=list.getValue("jan")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="feb_<%=i%>" VALUE="<%=list.getValue("feb")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="mar_<%=i%>" VALUE="<%=list.getValue("mar")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="apr_<%=i%>" VALUE="<%=list.getValue("apr")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="may_<%=i%>" VALUE="<%=list.getValue("may")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="jun_<%=i%>" VALUE="<%=list.getValue("jun")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="jul_<%=i%>" VALUE="<%=list.getValue("jul")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="aug_<%=i%>" VALUE="<%=list.getValue("aug")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="sep_<%=i%>" VALUE="<%=list.getValue("sep")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="oct_<%=i%>" VALUE="<%=list.getValue("oct")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="nov_<%=i%>" VALUE="<%=list.getValue("nov")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="dec_<%=i%>" VALUE="<%=list.getValue("dec")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
      <td class="td" nowrap align="left"><INPUT TYPE="TEXT" NAME="bwzp_<%=i%>" VALUE="<%=list.getValue("bwzp")%>" style="width:100%"  CLASS="<%=fieldclass%>" onKeyDown="return getNextElement();" <%=readOnly%>></td>
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
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
            <TABLE cellspacing=3 cellpadding=0 border=0>
             <TR>
                  <td align="center" nowrap class="td">年份</td>
                  <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 100px" name="zpnf" value='<%=B_InviteJobBean.getFixedQueryValue("zpnf")%>' onKeyDown="return getNextElement();">
                  </td>
                  <td align="center" nowrap class="td">部门</td>
                  <td class="td" nowrap>
                  <pc:select name="deptid"  style="width:120" addNull="1">
                  <%=deptBean.getList(B_InviteJobBean.getFixedQueryValue("deptid"))%>
                  </pc:select>
                  </td>
             </TR>
            </TABLE>
      </TD>
    </TR>
    <TR>
      <TD colspan="4" nowrap class="td" align="center">
      <%if(loginBean.hasLimits(pageCode,op_add)) { String qu = "sumitFixedQuery("+B_InviteJobBean.FIXED_SEARCH+",-1)";%>
      <INPUT class="button" title ="查询" onClick="sumitFixedQuery(<%=B_InviteJobBean.FIXED_SEARCH%>)" type="button" value=" 查询(Q) " name="button" onKeyDown="return getNextElement();"><pc:shortcut key="q" script='<%=qu%>'/><%}%>
      <%if(loginBean.hasLimits(pageCode,op_add)) { String col = "hideFrame('fixedQuery')";%>
      <INPUT class="button" title = "关闭"  onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(C) " name="button2" onKeyDown="return getNextElement();"><pc:shortcut key="c" script='<%=col%>'/><%}%>
      </TD>
   </TR>
 </TABLE>
  </DIV>
   <div class="queryPop" id="importForm" name="importForm">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('importForm')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
            <TABLE cellspacing=3 cellpadding=0 border=0>
             <TR>
            <td align="center" nowrap class="td">部门</td>
            <td class="td" nowrap>
            <pc:select name="buid"  style="width:120" addNull="1">
            <%=deptBean.getList(B_InviteJobBean.getFixedQueryValue("deptid"))%>
            </pc:select>
            </td>
            </TR>
            </TABLE>
      </TD>
    </TR>
    <TR>
      <TD colspan="4" nowrap class="td" align="center">
      <%if(loginBean.hasLimits(pageCode,op_add)) { String addd = "sumitFixedQuery("+Operate.ADD+",-1)";%>
      <INPUT class="button" title = "引入" onClick="sumitFixedQuery(<%=Operate.ADD%>)" type="button" value=" 引入(A) " name="button" onKeyDown="return getNextElement();"><pc:shortcut key="a" script='<%=addd%>'/><%}%>
      <%if(loginBean.hasLimits(pageCode,op_add)) { String cloo = "hideFrame('importForm')";%>
      <INPUT class="button" title = "关闭" onClick="hideFrame('importForm')" type="button" value=" 关闭(C) " name="button2" onKeyDown="return getNextElement();"><pc:shortcut key="c" script='<%=cloo%>'/><%}%>
      </TD>
   </TR>
 </TABLE>
  </DIV>
</form>
<%out.print(retu);%>
</body>
</html>
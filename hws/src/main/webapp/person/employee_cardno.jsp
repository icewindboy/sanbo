<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "employee_cardno";
%>
<%
if(!loginBean.hasLimits("employee_cardno", request, response))
    return;
  engine.erp.person.B_EmployeeCardNo b_employeecardnoBean = engine.erp.person.B_EmployeeCardNo.getInstance(request);
  engine.project.LookUp creditCardBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK_CREDIT_CARD);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT_LIST);
  engine.project.LookUp personclassBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON_CLASS);
  String retu = b_employeecardnoBean.doService(request, response);//location.href='baln.htm'

%>
<%
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = b_employeecardnoBean.getDetailTable();
  String curUrl = request.getRequestURL().toString();
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  boolean isCanSearch =loginBean.hasLimits(pageCode, op_search);
  String edClass = isCanEdit?"class=edbox":"class=ednone";
  String readonly = isCanEdit?"":"readonly";
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
  function hideInterFrame()//隐藏FRAME
  {
    hideFrame('interframe1');
    form1.submit();
  }

  function sumitFixedQuery(oper)
 {
   lockScreenToWait("处理中, 请稍候！");
   fixedQueryform.operate.value = oper;
   fixedQueryform.submit();
  }

  function showFixedQuery()
   {
    showFrame('fixedQuery',true,"",true);
   }

  function hideFrameNoFresh()
  {
    hideFrame('interframe1');
  }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
<TR>
    <TD nowrap align="center">员工信用卡号设置</TD>
</TR>
</TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <INPUT TYPE="HIDDEN" NAME="sumrow"  VALUE="<%=list.getRowCount()%>">
  <TABLE width="75%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
     <td class="td" nowrap>
     <%
         String key = "ppdfsgg";
         pageContext.setAttribute(key, list);
         int iPage = loginBean.getPageSize();
         String pageSize = ""+iPage;
         String s = "location.href='"+b_employeecardnoBean.retuUrl+"'";
         %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
     </td>
      <TD align="right">
       <%
         if(isCanEdit){
           String ss= "sumitForm("+Operate.POST+");";
       %>
       <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存(S) ">
       <pc:shortcut key="s" script='<%=ss%>'/>
       <%}%>
      <%if(isCanSearch){%>
      <INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询(Q) " name="Query" onKeyDown="return getNextElement();">
      <pc:shortcut key="q" script='showFixedQuery()'/>
      <%}%>
      <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=b_employeecardnoBean.retuUrl%>'" value=" 返回(C) "border="0">
      <pc:shortcut key="c" script="<%=s%>" />
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="80%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>职工编号</td>
      <td nowrap>姓名</td>
      <td nowrap>部门</td>
      <td nowrap>类别</td>
      <td nowrap>信用卡名称</td>
      <td nowrap>储蓄卡号</td>
    </tr>
    <%
    int i=0;
    RowMap[] rows = b_employeecardnoBean.getDetailRowinfos();
    for(; i<rows.length; i++)
    {
    RowMap row = rows[i];
    String xykidName = "xykID_"+i;
     %>
    <tr>
      <td class="td" nowrap align="rigth" width="10%"><INPUT TYPE="text" align="rigth" NAME="bm_<%=i%>" VALUE="<%=row.get("bm")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("bm").getPrecision()%>" class='ednone_r' readonly></td>
      <td class="td" nowrap align="left" width="10%"><%=row.get("xm")%><INPUT TYPE="hidden" align="left" NAME="xm_<%=i%>" VALUE="<%=row.get("xm")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("xm").getPrecision()%>" class='ednone_r' readonly></td>
      <td class="td" nowrap align="left" width="20%"><%=row.get("mc")%><INPUT TYPE="hidden" align="left" NAME="mc_<%=i%>" VALUE="<%=row.get("mc")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("mc").getPrecision()%>" class='ednone_r' readonly><INPUT TYPE="hidden" NAME="personid_<%=i%>" VALUE="<%=row.get("personid")%>"  MAXLENGTH="<%=list.getColumn("personid").getPrecision()%>" class='ednone_r'></td>
      <td class="td" nowrap align="left" width="10%"><%=row.get("lb")%><INPUT TYPE="hidden" align="left" NAME="lb_<%=i%>" VALUE="<%=row.get("lb")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("lb").getPrecision()%>" class='ednone_r' readonly></td>
      <td class="td" nowrap width="20%">
      <%if(isCanEdit){%>
      <pc:select name="<%=xykidName%>"  style="width:200" >
         <%=creditCardBean.getList(row.get("xykID"))%>
      </pc:select>
     <%}else{
       out.print(creditCardBean.getLookupName(row.get("xykID")));
       }
       %>
      </td>
      <td class="td" nowrap align="right" width="20%"><INPUT TYPE="TEXT" NAME="ygxykh_<%=i%>" VALUE="<%=row.get("ygxykh")%>" style="width:100%" MAXLENGTH="<%=list.getColumn("ygxykh").getPrecision()%>" <%=edClass%> <%=readonly%>></td>
     </tr>
    <%
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
                <TD align="center" nowrap class="td">部门</TD>
                  <td class="td" nowrap>
                  <pc:select name="deptid"  style="width:150" addNull="1">
                  <%=deptBean.getList(b_employeecardnoBean.getFixedQueryValue("deptid"))%>
                  </pc:select>
                  </td>
                 <td align="center" nowrap class="td">职工编号</td>
                 <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 150" name="bm" value='<%=b_employeecardnoBean.getFixedQueryValue("bm")%>' onKeyDown="return getNextElement();">
                </td>
              </TR>
              <TR>
                  <td align="center" nowrap class="td">姓名</td>
                  <td class="td" align="left" nowrap>
                    <INPUT class="edbox" style="WIDTH: 150" name="xm" value='<%=b_employeecardnoBean.getFixedQueryValue("xm")%>' onKeyDown="return getNextElement();">
                  </td>
                  <td align="center" nowrap class="td">类别</td>
                  <td class="td" nowrap>
                  <pc:select name="lb"  style="width:150" addNull="1" >
                 <%=personclassBean.getList(b_employeecardnoBean.getFixedQueryValue("lb"))%>
                 </pc:select>
                  </td>
             </TR>
             <TR>
                  <td align="center" nowrap class="td">信用卡名称</td>
                  <td class="td" nowrap >
                  <pc:select name="xykID"  style="width:150" addNull="1" ><%=creditCardBean.getList(b_employeecardnoBean.getFixedQueryValue("xykID"))%></pc:select>
                  </td>
                  <td align="center" nowrap class="td">储蓄卡帐号</td>
                  <td class="td" nowrap>
                    <INPUT class="edbox" style="WIDTH: 150" name="ygxykh" value='<%=b_employeecardnoBean.getFixedQueryValue("ygxykh")%>' onKeyDown="return getNextElement();">
                  </td>
             </TR>
            </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center">
                <%String qu ="sumitFixedQuery("+b_employeecardnoBean.FIXED_SEARCH+")"; %>
                <INPUT class="button" onClick="sumitFixedQuery(<%=b_employeecardnoBean.FIXED_SEARCH%>)" type="button" value=" 查询(k) " name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="k" script='<%=qu%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X) " name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script="hideFrame('fixedQuery')"/>
              </TD>
            </TR>
    </TABLE>
  </DIV>
</form><%out.print(retu);%>
</body>
</html>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_approve ="op_approve";
  String pageCode = "employee_change";
  if(!loginBean.hasLimits("employee_change", request, response))
    return;
  B_EmployeeChange b_employeeinfoChangeBean = B_EmployeeChange.getInstance(request);
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT_LIST);
  LookUp MdeptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT_LIST);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  LookUp dutyBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_DUTY);
  LookUp personclassBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_CLASS);
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
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script language="javascript">
function sumitForm(oper, row)
{
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function checkBdlx()
{
  if(form1.bdlx.value == "")
  alert("请选择变动类型!");
  else if(form1.bdlx.value == "5")
    PersonMultiSelect('form1','srcVar=multiIdInput&isoff=1','undefined','undefined');
  else
    PersonMultiSelect('form1','srcVar=multiIdInput','undefined','undefined');
}
function backList()
{
  location.href='employee_change.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true" >
<%
  String retu = b_employeeinfoChangeBean.doService(request, response);
  if(retu.indexOf("location")>-1)
  {
    out.print(retu);
  }
  String curUrl = request.getRequestURL().toString();
  RowMap m_RowInfo = b_employeeinfoChangeBean.getMasterRowinfo();   //行到主表的一行信息
  RowMap[] detailrowinfos= b_employeeinfoChangeBean.getDetailRowinfos();//从表的一行信息
  String zt=m_RowInfo.get("zt");
  boolean isEnd =zt.equals("1")||zt.equals("9")||zt.equals("8");  //b_employeeinfoChangeBean.isApprove || (!b_employeeinfoChangeBean.masterIsAdd() && !zt.equals("0"));
  //isEnd = isEnd || !(b_employeeinfoChangeBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  boolean canedit = !isEnd&&loginBean.hasLimits(pageCode, op_edit)||b_employeeinfoChangeBean.masterIsAdd();
  boolean canDel = !isEnd&&loginBean.hasLimits(pageCode, op_delete)||b_employeeinfoChangeBean.masterIsAdd();
  String edClass = canedit?"class=edbox": "class=edline";
  String detailClass = canedit ?"class=edFocused": "class=ednone";
  String detailClass_r = canedit ? "class=edFocused_r": "class=ednone_r" ;
  String readonly = canedit ?"": "readonly" ;
%>
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
  </tr>
</table>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
  <td width="400"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
  <td  class="activeVTab">员工信息变动(  <%if(m_RowInfo.get("zt").equals("0")){%>
       未审
       <%}else if(m_RowInfo.get("zt").equals("1")){%>
       已审
       <%}else if(m_RowInfo.get("zt").equals("9")){%>
       审批中
       <%}else if(m_RowInfo.get("zt").equals("8")){%>
       完成
      <%}%>)</td>
  </tr>
</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
  <td>
<table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">
     <INPUT TYPE="HIDDEN" NAME="personid"  VALUE="<%=m_RowInfo.get("personid")%>">
     <tr>
    <td nowrap class="tdTitle">单据号</td>
      <td  nowrap class="td"><input type="text" name="lsh" value='<%=m_RowInfo.get("lsh")%>'  style="width:100" <%=edClass%> <%=readonly%>>
    </td>
    <%
    ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");opkey.add("4");opkey.add("5");
    ArrayList opval = new ArrayList(); opval.add("职员离职"); opval.add("部门调动"); opval.add("类别变动");opval.add("职务变迁");opval.add("职员复职");
    ArrayList[] lists  = new ArrayList[]{opkey, opval};
    String ss=m_RowInfo.get("bdlx");
    %>
     <td nowrap class="tdTitle">变动类型</td>
     <td noWrap class="td">
     <%
     String t="sumitForm("+b_employeeinfoChangeBean.BDLX_CHANGE+",-1)";
     int te=0;
    try{
      te=Integer.parseInt(ss)-1;
      }catch(Exception e){te=0;}
     if(!b_employeeinfoChangeBean.isMasterAdd){%><input type="HIDDEN" name="bdlx" class="ednone" value="<%=opkey.get(te)%>"><%out.print(opval.get(te));}else{%>
     <pc:select name="bdlx"  style="width:130"   onSelect="<%=t%>">
     <%=b_employeeinfoChangeBean.listToOption(lists, opkey.indexOf(m_RowInfo.get("bdlx")))%>
     </pc:select>
    <%}%>
     </td>
     <td nowrap class="tdTitle">变动日期</td>
     <td nowrap class="td"><input type="text" name="bdrq" value='<%=m_RowInfo.get("bdrq")%>' maxlength="10" style="width:106" <%=edClass%> onChange="checkDate(this)" <%=readonly%>>
     <%if(canedit){%><a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(bdrq);"></a><%}%>
     </td>

     </tr>
     <tr>
      <td nowrap class="tdTitle">变动原因</td>
      <td colspan="3" nowrap class="td"><input type="text" name="bdyy" value='<%=m_RowInfo.get("bdyy")%>'  style="width:327" <%=edClass%> <%=readonly%>>
    </td>
     <td nowrap class="tdTitle">提交部门</td>
     <td nowrap class="td">
     <%
     if(!b_employeeinfoChangeBean.isMasterAdd){%>
     <input type="HIDDEN" name="deptid" class="ednone" value="<%=m_RowInfo.get("deptid")%>">
     <%out.print(deptBean.getLookupName(m_RowInfo.get("deptid")));}else{%>
       <pc:select name='deptid'  style="width:111" >
       <%=MdeptBean.getList(m_RowInfo.get("deptid"))%>
       </pc:select>
     <%}%>
     </td>
    </tr>
     <tr>
     <td colspan="6" nowrap>
     <table cellspacing=0 width="100%" cellpadding=0>
     <tr>
         <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" >明细情况</a></div></td>
         <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
     </tr>
     </table>
<div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:750;height:300;overflow-y:auto;overflow-x:auto;">
     <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
     <tr class="tableTitle">
           <td nowrap>
          <%if(canedit){%>
          <input type="hidden" name="multiIdInput" value="" onchange="sumitForm(<%=Operate.DETAIL_ADD%>)">
          <input name="image" class="img" type="image" title="新增(A)" onClick="checkBdlx()" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='checkBdlx()'/>
          <%}%>
          </td>
           <td>姓名</td>
           <td>变动前</td>
           <td>变动后</td>
           <td>备注</td>
      </tr>
     <%
      RowMap zgjtdetail = null;
      for(int i=0; i<detailrowinfos.length; i++)
      {
        zgjtdetail = detailrowinfos[i];
        String bdhID="bdhID_"+i;
     %>
     <tr>
     <td class="td" width=45 align="center">
     <%if(canedit){%>
     <input name="image3" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_employeeinfoChangeBean.DETAIL_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle"><%}%>
     </td>
     <td class="td" align="left"><INPUT TYPE="hidden" NAME="personid_<%=i%>" VALUE="<%=zgjtdetail.get("personid")%>" ><%=personBean.getLookupName(zgjtdetail.get("personid"))%> </td>
     <%
      //职员离职
       if(m_RowInfo.get("bdlx").equals("1"))
       {
      %>
      <td class="td" align="right">
      <INPUT TYPE="hidden" NAME="bdqID_<%=i%>" VALUE="在职" >在职</td>
      <td class="td" align="right"><INPUT TYPE="hidden" NAME="bdhID_<%=i%>" VALUE="离职" >离职</td>
     <%
      }
      //部门调动
      else if(m_RowInfo.get("bdlx").equals("2")){%>
      <INPUT TYPE="hidden" NAME="bdqID_<%=i%>" VALUE="<%=zgjtdetail.get("bdqID")%>" >
      <td class="td" align="right"><%=deptBean.getLookupName(zgjtdetail.get("bdqID"))%></td>
      <td class="td" align="right">
      <%if(!canedit) out.print("<input type='text' value='"+deptBean.getLookupName(zgjtdetail.get("bdhID"))+"' style='width:110' class='edline' readonly >");
      else {%>
       <pc:select name='<%=bdhID%>'  style="width:111" >
       <%=deptBean.getList(zgjtdetail.get("bdhID"))%>
       </pc:select>
      <%}%>
      </td>
      <%
       }
       //类别变动
      else if(m_RowInfo.get("bdlx").equals("3")){
        String bdq=zgjtdetail.get("bdqID");
       %>
      <INPUT TYPE="hidden" NAME="bdqID_<%=i%>" VALUE="<%=zgjtdetail.get("bdqID")%>" >
      <td class="td" align="right"><%=personclassBean.getLookupName(zgjtdetail.get("bdqID"))%></td>
      <td class="td" align="right">
      <%if(!canedit) out.print("<input type='text' value='"+personclassBean.getLookupName(zgjtdetail.get("bdhID"))+"' style='width:110' class='edline' readonly >");
      else {%>
       <pc:select name='<%=bdhID%>'  style="width:111" >
       <%=personclassBean.getList(zgjtdetail.get("bdhID"))%>
       </pc:select>
      <%}%>
      </td>
      <%
       }
       //职务变迁
      else if(m_RowInfo.get("bdlx").equals("4")){%>
      <INPUT TYPE="hidden" NAME="bdqID_<%=i%>" VALUE="<%=zgjtdetail.get("bdqID")%>" >
      <td class="td" align="left"><%=zgjtdetail.get("bdqID")%></td>
      <td class="td" align="left">
      <%if(!canedit) out.print("<input type='text' value='"+dutyBean.getLookupName(zgjtdetail.get("bdhID"))+"' style='width:110' class='edline' readonly >");
      else {%>
       <pc:select name='<%=bdhID%>'  style="width:111" >
       <%=dutyBean.getList(zgjtdetail.get("bdhID"))%>
       </pc:select>
      <%}%>
      </td>
      <%
       }
       //职务变迁
      else if(m_RowInfo.get("bdlx").equals("5")){%>
      <INPUT TYPE="hidden" NAME="bdqID_<%=i%>" VALUE="离职" >
      <td class="td" align="left">离职</td>
      <td class="td" align="left"><INPUT TYPE="hidden" NAME="bdhID_<%=i%>" VALUE="复职" >复职
      </td>
       <%}%>
          <td class="td" align="left" width="60%">
          <INPUT TYPE="TEXT" NAME="bz_<%=i%>" <%=edClass%> VALUE="<%=zgjtdetail.get("bz")%>" style="width:100%"  <%=readonly%>>
          </td>
      </tr>
      <%}%>
      </table>
      <script language="javascript">initDefaultTableRow('tableview1',1);</script>
      </div>
</td>
</tr>
</table>
</td>
</tr>
</table>
</table>
    <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
      <tr>
        <td class="td"><b>登记日期:</b><%=m_RowInfo.get("czrq")%></td>
        <td class="td"></td>
        <td class="td" align="right"><b>制单人:</b><%=m_RowInfo.get("czy")%></td>
      </tr>
      <tr>
        <td colspan="3" class="td" align="center">
          <%--<%if(loginBean.hasLimits(pageCode, op_add)&&b_employeeinfoChangeBean.masterIsAdd()){%>--%>
          <%if(loginBean.hasLimits(pageCode, op_add)&&canedit){%>
          <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value='保存新增(N)'>
          <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
          <%}if(canedit){%>
          <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value='保存返回(S)'>
          <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
          <%}if(canDel){%>
          <input name="btnback2" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>,<%=request.getParameter("rownum")%>)" value='  删除  '>
          <%}%>
          <input name="btnback" type="button" class="button" onClick="backList();" value='  返回(C)  '>
          <pc:shortcut key="c" script='<%="backList();"%>'/>
        </td>
      </tr>
    </table>
</form>
<%//&#$
if(b_employeeinfoChangeBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</body>
</html>
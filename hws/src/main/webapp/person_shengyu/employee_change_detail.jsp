<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
    <%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
    <%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.person.shengyu.*"%>
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
  engine.project.LookUp predeptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  EngineDataSet ds = b_employeeinfoChangeBean.getMaterTable();
  EngineDataSet list = b_employeeinfoChangeBean.getDetailTable();

  RowMap masterRow = b_employeeinfoChangeBean.getMasterRowinfo();

  if(b_employeeinfoChangeBean.isApprove)
  {
    // corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
    //storeBean.regData(ds, "storeid");
    deptBean.regData(ds, "deptid");
    // corpBean.regData(ds, "dwt_dwtxid");
  }
  personBean.regData(ds, "personid");

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
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript">
  function sumitForm(oper, row)
{
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function checkBdlx()
{
  if(form1.chg_type.value == "")
    alert("请选择变动类型!");
  else
    PersonSingleSelect('form1','srcVar=personid','fieldVar=personid','undefined',methodName='sumitForm(<%=b_employeeinfoChangeBean.PERSON_ONCHANGE%>)');
      }
      function backList()
      {
        location.href='employee_change.jsp';
      }
      function corpCodeSelect(obj)
      {
        //PersonSingleSelect(document.all['prod'], obj.form.name, 'srcVar=personid&srcVar=xm',
                // 'fieldVar=personid&fieldVar=xm', obj.value, 'sumitForm(<%=b_employeeinfoChangeBean.PERSON_ONCHANGE%>)');
      PersonSingleSelect('form1','srcVar=personid&srcVar=xm','fieldVar=personid&fieldVar=xm','undefined',methodName='sumitForm(<%=b_employeeinfoChangeBean.PERSON_ONCHANGE%>)');
                   }
/////////////////////////////////////
 function SingleSelectperson(frmName, srcVar,fieldVar,methodName,bm,xm,notin)
 {
   var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=930 height=570 top=0 left=0";
   var winName= "GoodsProdSelector";
   paraStr = "../pub/person_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&bm="+bm+"&xm="+xm+"&isdelete=0&methodName="+methodName;
   if(methodName+'' != 'undefined')
     paraStr += "&method="+methodName;
   if(notin+'' != 'undefined')
     paraStr += "&notin="+notin;
   newWin =window.open(paraStr,winName,winopt);
   newWin.focus();
 }
 function CodeSelectPerson(obj,srcVars,methodName)
 {
   CodeChangePerson(document.all['prod'], obj.form.name, srcVars,
                  'fieldVar=personid&fieldVar=bm&fieldVar=xm&isdelete=0', obj.value,methodName);
 }
 function CodeChangePerson(iframeObj, frmName,srcVar,fieldVar,bm,methodName,notin)
 {
   paraStr = "../pub/person_select.jsp?operate=53&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&isdelete=0&bm="+bm+"&methodName="+methodName;
   if(methodName+'' != 'undefined')
     paraStr += "&method="+methodName;
   if(notin+'' != 'undefined')
     paraStr += "&notin="+notin;
   iframeObj.src=paraStr;
 }
 function NameSelectPerson(obj,srcVars,methodName)
 {
   NameChangePerson(document.all['prod'], obj.form.name, srcVars,
                  'fieldVar=personid&fieldVar=bm&fieldVar=xm&isdelete=0', obj.value,methodName);
 }
 function NameChangePerson(iframeObj, frmName,srcVar,fieldVar,xm,methodName,notin)
 {
   paraStr = "../pub/person_select.jsp?operate=54&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&isdelete=0&xm="+xm+"&methodName="+methodName;
   if(methodName+'' != 'undefined')
     paraStr += "&method="+methodName;
   if(notin+'' != 'undefined')
     paraStr += "&notin="+notin;
   iframeObj.src=paraStr;
 }
</script>
<BODY oncontextmenu="window.event.returnValue=true" >
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<%
  String retu = b_employeeinfoChangeBean.doService(request, response);
if(retu.indexOf("location")>-1)
{
  out.print(retu);
}
String curUrl = request.getRequestURL().toString();
RowMap m_RowInfo = b_employeeinfoChangeBean.getMasterRowinfo();   //行到主表的一行信息
RowMap[] detailrowinfos= b_employeeinfoChangeBean.getDetailRowinfos();//从表的一行信息
String state=m_RowInfo.get("state");
boolean isEnd =state.equals("1")||state.equals("9");  //b_employeeinfoChangeBean.isApprove || (!b_employeeinfoChangeBean.masterIsAdd() && !state.equals("0"));
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
  <td  class="activeVTab">员工信息变动</td>
  </tr>
</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
  <td>
<table cellspacing="1" cellpadding="1" border="0"  bgcolor="#f0f0f0">
<%-- <INPUT TYPE="HIDDEN" NAME="personid"  VALUE="<%=m_RowInfo.get("personid")%>">--%>
    <%RowMap personRow = personBean.getLookupRow(m_RowInfo.get("personid"));%>
     <tr>
     <%--    <td nowrap class="tdTitle">姓名</td>
     <td nowrap class="td">
<%if(!b_employeeinfoChangeBean.isMasterAdd){%>
     <input type="HIDDEN" name="personid" class="ednone" value="<%=m_RowInfo.get("personid")%>">
      <%out.print(personBean.getLookupName(m_RowInfo.get("personid")));}else{%>
        <pc:select name='personid' addNull="1" onSelect='<%="sumitForm("+b_employeeinfoChangeBean.PERSON_ONCHANGE+")"%>' style="width:111" >
       <%=personBean.getList(m_RowInfo.get("personid"))%>
           </pc:select>
     <%}%>
     </td> --%>
          <td noWrap class="tdTitle">姓名</td>
          <td colspan="3" noWrap class="td">
          <INPUT  style="WIDTH:70" <%=edClass%> id="xmbm" name="xmbm" value='<%=personRow.get("bm")%>' onKeyDown="return getNextElement();" onchange="CodeSelectPerson(this,'srcVar=personid&srcVar=xmbm&srcVar=xm','sumitForm(<%=b_employeeinfoChangeBean.PERSON_ONCHANGE%>)')">
          <INPUT  style="WIDTH:100" <%=edClass%> id="xm" name="xm" value='<%=personBean.getLookupName(m_RowInfo.get("personid"))%>'    onKeyDown="return getNextElement();" onchange="NameSelectPerson(this,'srcVar=personid&srcVar=xmbm&srcVar=xm','sumitForm(<%=b_employeeinfoChangeBean.PERSON_ONCHANGE%>)')">
          <INPUT TYPE="hidden" NAME="personid" value="<%=m_RowInfo.get("personid")%>">
          <%if(canedit){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="SingleSelectperson('form1','srcVar=personid&srcVar=xmbm&srcVar=xm','fieldVar=personid&fieldVar=bm&fieldVar=xm','sumitForm(<%=b_employeeinfoChangeBean.PERSON_ONCHANGE%>)','','')">
          <%}%></td>

    <%--    <td nowrap class="tdTitle">员工编码</td>
      <td  nowrap class="td"><input type="text" name="bm" value='<%=personRow.get("bm")%>'  style="width:100" class=edline readonly>
    </td>--%>



    <%-- <td nowrap class="tdtitle">性别</td>
    <td align="center" nowrap class="td"> 男
    <input type="radio" name="male" value="radiobutton" checked>女
    <input type="radio" name="female" value="radiobutton">
    </td>--%>
    <td nowrap class="tdTitle">性别</td>
      <td  nowrap class="td"><input type="text" name="bm" value='<%=personRow.get("sex").equals("")?"":(personRow.get("sex").equals("1")?"男":"女")%>'  style="width:100" class=edline readonly>
    </td>

</tr>
<tr>
<td nowrap class="tdtitle">入厂时间</td>
      <td nowrap class="td">
      <input type="text" name="date_in" value="<%=personRow.get("date_in")%>" maxlength="10" style="width:100" class=edline onChange="checkDate(this)" readonly>
     <%--<a href="employee_change_detail.jsp"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(rcrq);"></a>--%>
      </td>
    <%
      ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");opkey.add("4");opkey.add("5");
     ArrayList opval = new ArrayList(); opval.add("职员离职"); opval.add("部门调动"); opval.add("类别变动");opval.add("职务变迁");opval.add("职员复职");
     ArrayList[] lists  = new ArrayList[]{opkey, opval};
     String ss=m_RowInfo.get("chg_type");
    %>
     <td nowrap class="tdTitle">变动类型</td>
     <td noWrap class="td">
     <%
       String t="sumitForm("+b_employeeinfoChangeBean.PERSON_ONCHANGE+",-1)";
    int te=0;
    try{
      te=Integer.parseInt(ss)-1;
      }catch(Exception e){te=0;}
     if(!b_employeeinfoChangeBean.isMasterAdd){%><input type="HIDDEN" name="chg_type" class="ednone" value="<%=opkey.get(te)%>"><%out.print(opval.get(te));}else{%>
       <pc:select name="chg_type"  style="width:130"   onSelect="<%=t%>">
     <%=b_employeeinfoChangeBean.listToOption(lists, opkey.indexOf(m_RowInfo.get("chg_type")))%>
         </pc:select>
    <%}%>
     </td>
    <td nowrap class="tdTitle">变动日期</td>
     <td nowrap class="td"><input type="text" name="chg_date" value='<%=m_RowInfo.get("chg_date")%>' maxlength="10" style="width:80" <%=edClass%> onChange="checkDate(this)" <%=readonly%>>
     <%if(canedit){%><a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(chg_date);"></a><%}%>
     </td>

</tr>
<tr>
<td nowrap class="tdTitle">提交部门</td>
     <td nowrap class="td"><%
     if(!b_employeeinfoChangeBean.isMasterAdd){%>
     <input type="HIDDEN" name="deptid" class="ednone" value="<%=m_RowInfo.get("deptid")%>">
     <%out.print(deptBean.getLookupName(m_RowInfo.get("deptid")));}else{%>
       <pc:select name='deptid'  style="width:111" >
       <%=MdeptBean.getList(m_RowInfo.get("deptid"))%>
         </pc:select>
     <%}%>
     </td>
<td nowrap class="tdtitle">原职务</td>
      <td nowrap class="td">
     <%String ggd =m_RowInfo.get("chg_type");%>
     <%if(ggd.equals("4")){%>
      <input type="text" name="zw" value="<%=m_RowInfo.get("chg_before")%>" maxlength="10" style="width:100" class=edline onChange="checkDate(this)" readonly>
     <%} else if(!ggd.equals("4")){%>
     <input type="text" name="zw" value="<%=personRow.get("zw")%>" maxlength="10" style="width:100" class=edline onChange="checkDate(this)" readonly>
     <%}%>
     <%--<a href="employee_change_detail.jsp"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(rcrq);"></a>--%>
      </td>

      <%--    <td nowrap class="tdTitle">原职务</td>
     <td nowrap class="td">
       <%String gg1 =m_RowInfo.get("chg_type");%>
     <%if((canedit)&&gg1.equals("4")||b_employeeinfoChangeBean.isMasterAdd){%>
       <pc:select name="yzw" addNull="1" style="width:131">
       <%=dutyBean.getList(m_RowInfo.get("chg_before"))%>
         </pc:select>
     <%}else{%>
     <input type="text"  value='<%=dutyBean.getLookupName(m_RowInfo.get("zw"))%>'  style="width:130" class="edline"  readonly>
     <%}%>
     </td>
      --%>
   <td nowrap class="tdTitle">变动后职务</td>
   <td nowrap class="td">
   <%String gg1 =m_RowInfo.get("chg_type");%>
   <%if(b_employeeinfoChangeBean.isMasterAdd){%>
   <%if((canedit)&&gg1.equals("4")){%>
     <pc:select name="bdhzw" addNull="1" style="width:131">
   <%=dutyBean.getList(m_RowInfo.get("chg_after"))%>
          </pc:select>
   <%}else{%>
     <input type="text"  value='<%=gg1.equals("5")?personRow.get("zw"):""%>'  style="width:130" class="edline"  readonly>
     <%}%>
   <%}else if(!gg1.equals("4")) {%>
       <input type="text"  value='<%=gg1.equals("5")?personRow.get("zw"):""%>'  style="width:130" class="edline"  readonly>
     <%}else if(canedit){%>
       <pc:select name="bdhzw" addNull="1" style="width:131">
   <%=dutyBean.getList(m_RowInfo.get("chg_after"))%>
          </pc:select>
  <%}else{%>
  <input type="HIDDEN" name="bdhzw" class="ednone" value="<%=m_RowInfo.get("chg_after")%>">
     <%out.print(dutyBean.getLookupName(m_RowInfo.get("chg_after")));%>
        <%}%>
   </td>
 </tr>
    <tr>
    <td nowrap class="tdTitle">变动原因</td>
    <td colspan="3" nowrap class="td"><input type="text" name="chg_reason" value='<%=m_RowInfo.get("chg_reason")%>'  style="width:330" <%=edClass%> <%=readonly%>>
    </td>

  <td nowrap class="tdtitle">原部门</td>
      <td nowrap class="td">
      <input type="hidden" name="ydeptid" value="<%=personRow.get("deptid")%>">
      <%String gga =m_RowInfo.get("chg_type");%>
      <%if(gga.equals("2")){%>
      <input type="text" name="deptid3" value="<%=predeptBean.getLookupName(m_RowInfo.get("chg_before"))%>" maxlength="10" style="width:100" class=edline onChange="checkDate(this)" readonly>
    <%}
       else if(!gga.equals("2")){%>
      <input type="text" name="deptid3" value="<%=predeptBean.getLookupName(personRow.get("deptid"))%>" maxlength="10" style="width:100" class=edline onChange="checkDate(this)" readonly>
    <%}%>
        </td>
        <%--<a href="employee_change_detail.jsp"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(rcrq);"></a>--%>
        <%--<td nowrap class="tdTitle">原部门</td>
     <td nowrap class="td">
     <%String gg =m_RowInfo.get("chg_type");%>
     <%if((canedit)&&gg.equals("2")||b_employeeinfoChangeBean.isMasterAdd){%>
      <input type="HIDDEN" name="deptid1"  id="deptid1" class="ednone" value="<%=m_RowInfo.get("deptid")%>">
       <pc:select name="deptid1" addNull="1" style="width:131">
<%String ff =deptBean.getList(m_RowInfo.get("chg_before"));%>
     <%=deptBean.getList(m_RowInfo.get("chg_before"))%>
    </pc:select>
     <%}else{%>
     <input type="text"  value=''  style="width:130" class="edline"  readonly>
     <%}%>
        </td>--%>
<tr>
 <td nowrap class="tdTitle">变动后部门</td>
     <td nowrap class="td">
<%String gg =m_RowInfo.get("chg_type");%>
     <%if(b_employeeinfoChangeBean.isMasterAdd){%>
     <%if((canedit)&&gg.equals("2")){%>
          <%--  <input type="HIDDEN" name="deptid1"  id="deptid1" class="ednone" value="<%=m_RowInfo.get("deptid")%>">--%>
          <pc:select name="deptid2" addNull="1" style="width:131">
<%String ff1 =deptBean.getList(m_RowInfo.get("chg_after"));%>
     <%=deptBean.getList(m_RowInfo.get("chg_after"))%>
            </pc:select>
     <%}else{%>
     <input type="text"  value='<%=gg.equals("5")?deptBean.getLookupName(personRow.get("deptid")):""%>'  style="width:130" class="edline"  readonly>
     <%}%>
     <%}else if(!gg.equals("2")) {%>
     <input type="text"  value='<%=gg.equals("5")?deptBean.getLookupName(personRow.get("deptid")):""%>'  style="width:130" class="edline"  readonly>
     <%}else if(canedit){%>
       <pc:select name="deptid2" addNull="1" style="width:131">
   <%=deptBean.getList(m_RowInfo.get("chg_after"))%>
            </pc:select>
  <%}else{%>
            <input type="HIDDEN" name="deptid2" class="ednone" value="<%=m_RowInfo.get("chg_after")%>">
     <%out.print(deptBean.getLookupName(m_RowInfo.get("chg_after")));%>
       <%}%>
     </td>
     <%--<td nowrap class="tdtitle">原类别</td>
      <td nowrap class="td">
      <input type="text" name="lb1" value="<%=personRow.get("lb")%>" maxlength="10" style="width:100" class=edline onChange="checkDate(this)" readonly>

     </td>--%>

     <td nowrap class="tdtitle">原类别</td>
            <td nowrap class="td">
     <%String ggc =m_RowInfo.get("chg_type");%>
     <%if(ggc.equals("3")){%>
      <input type="text" name="lb1" value="<%=m_RowInfo.get("chg_before")%>" maxlength="10" style="width:100" class=edline onChange="checkDate(this)" readonly>
     <%} else if(!ggd.equals("3")){%>
     <input type="text" name="lb1" value="<%=personRow.get("lb")%>" maxlength="10" style="width:100" class=edline onChange="checkDate(this)" readonly>
     <%}%>
     <%--<a href="employee_change_detail.jsp"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(rcrq);"></a>--%>
      </td>

     <td nowrap class="tdTitle">变动后类别</td>
     <td nowrap class="td">
     <%String gg2 =m_RowInfo.get("chg_type");%>
     <%if(b_employeeinfoChangeBean.isMasterAdd){%>
      <%if((canedit)&&gg.equals("3")){%>
        <pc:select name="lb"  style="width:131">
       <%=personclassBean.getList(m_RowInfo.get("lb"))%>
            </pc:select>
     <%}else{%>
     <input type="text"  value='<%=gg2.equals("5")?personRow.get("lb"):""%>'  style="width:130" class="edline"  readonly>
     <%}%>
          <%}else if(!gg2.equals("3")) {%>
          <input type="text"  value='<%=gg2.equals("5")?personRow.get("lb"):""%>'  style="width:130" class="edline"  readonly>
     <%}else if(canedit){%>
       <pc:select name="lb" addNull="1" style="width:131">
   <%=personclassBean.getList(m_RowInfo.get("chg_after"))%>
            </pc:select>
  <%}else{%>
  <input type="text"  value='<%=personclassBean.getLookupName(m_RowInfo.get("chg_after"))%>'  style="width:110" class="edline"  readonly>
  <%}%>
     </td>
</tr>

<%--<td nowrap class="tdTitle">变动前部门</td>
     <td nowrap class="td"><%
     if(!b_employeeinfoChangeBean.isMasterAdd){%>
    <input type="HIDDEN" name="deptid1"  id="deptid1" class="ednone" value="<%=m_RowInfo.get("deptid")%>">
     <%out.print(deptBean.getLookupName(m_RowInfo.get("deptid")));}else{%>
       <pc:select name='deptid1' addNull="1" style="width:111" >
       <%=deptBean.getList(m_RowInfo.get("deptid"))%>
         </pc:select>
     <%}%>
     </td>
    </tr>
    <tr>
    <td nowrap class="tdTitle">备注</td>
    <td colspan="3" nowrap class="td"><input type="text" name="memo" value='<%=m_RowInfo.get("memo")%>'  style="width:330" <%=edClass%> <%=readonly%>>
    </td>
 <%--   <td nowrap class="tdTitle">变动后部门</td>
     <td nowrap class="td">
     <%if((canedit)){%>
       <pc:select name="deptid2" addNull="1" style="width:131">
     <%=deptBean.getList(m_RowInfo.get("deptid"))%>
    </pc:select>
     <%}else{%>
     <input type="text"  value='<%=deptBean.getLookupName(m_RowInfo.get("deptid"))%>'  style="width:130" class="edline"  readonly>
     <%}%>
</td>--%>
<%--<td nowrap class="tdTitle">变动后部门</td>
     <td nowrap class="td"><%
     if(!b_employeeinfoChangeBean.isMasterAdd){%>
     <input type="HIDDEN" name="deptid2" class="ednone" value="<%=m_RowInfo.get("chg_after")%>">
     <%out.print(deptBean.getLookupName(m_RowInfo.get("chg_after")));}else{%>
       <pc:select name='deptid2'  style="width:111" >
       <%=MdeptBean.getList(m_RowInfo.get("deptid"))%>
         </pc:select>
     <%}%>
</td>--%>

    </tr>
     <tr>
     <td colspan="6" nowrap>
     <table cellspacing=0 width="100%" cellpadding=0>
<tr>
     <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" >物品移交</a></div></td>
     <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
     </tr>
     </table>
     <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:750;height:250;overflow-y:auto;overflow-x:auto;">
                      <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
                        <tr class="tableTitle">
                          <td nowrap height="17"> 移交物品</td>
                          <td height="17">移交情况</td>
                          <td height="17">接收人签字</td>
                          <td height="17">备注</td>
                        </tr>
                        <%
                          int i = 0;
     RowMap zgjtdetail = null;
     int k = detailrowinfos.length;
     //boolean isHaveDetailInfo = k!=0;
     //k = k==0?7:k;
     //String remove_goods[]={"工作移交", "作业指导书",
     //                         "车间配件", "宿舍鞋柜钥匙", "菜票借支", "财务欠（借）款", "其他借款"};
     for(; i<k;i++){
       //if ( isHaveDetailInfo )
       zgjtdetail = detailrowinfos[i];
       String remove_goods = zgjtdetail.get("remove_goods");
       String remove_thing = zgjtdetail.get("remove_thing");
       String accepter = zgjtdetail.get("accepter");
       String memo = zgjtdetail.get("memo");

                        %>
                        <tr>
                        <td class="td" align="center">
                            <input type="text" class="ednone" name="remove_goods_<%=i%>" id="remove_goods_<%=i%>" value="<%=remove_goods%>" maxlength="10" size="15" readonly>
                          <td class="td" align="center">
                            <input type="text" class="edbox" name="remove_thing_<%=i%>" id="remove_thing_<%=i%>" value="<%=remove_thing%>"  maxlength="10" size="15" <%=readonly%>>
                          </td>
                          <td class="td" align="center">
                            <input type="text" class="edbox" name="accepter_<%=i%>" id="accepter_<%=i%>" value="<%=accepter%>" maxlength="10" size="15" <%=readonly%>>
                          </td>
                          <td class="td" align="center">
                            <input type="text" class="edbox" name="memo_<%=i%>" id="memo_<%=i%>" value="<%=memo%>" maxlength="10" size="15" <%=readonly%>>
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
        <td class="td"><b>登记日期:</b><%=m_RowInfo.get("createDate")%></td>
        <td class="td"></td>
        <td class="td" align="right"><b>制单人:</b><%=m_RowInfo.get("creator")%></td>
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
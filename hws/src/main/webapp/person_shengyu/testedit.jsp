<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.shengyu.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "test";
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("test", request, response))
  return;
B_EmpTry b_EmpTryBean = B_EmpTry.getInstance(request);
LookUp areaBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_AREA);
LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT_BOTH);//BEAN_DEPT
LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
LookUp countryBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_COUNTRY);
LookUp dutyBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_DUTY);
LookUp personclassBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_CLASS);
LookUp personctechBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_TECH);
LookUp personNationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATION);
LookUp personPolityBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_POLITY);
LookUp personEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);
LookUp personNativeBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_NATIVE);
%>

<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">

  function sumitForm(oper, row)
{
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='../person_shengyu/test.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
  </tr>
</table>
<%
  String retu = b_EmpTryBean.doService(request, response);
if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
{
  out.print(retu);
  return;
}
String curUrl = request.getRequestURL().toString();
RowMap m_RowInfo = b_EmpTryBean.getMasterRowinfo();   //行到主表的一行信息
String s = m_RowInfo.get("personid");

//RowMap[] pxkcRows= b_EmpTryBean.getCcsqRowinfos();//从表的多行信息


EngineDataSet ds = b_EmpTryBean.getMaterTable();
EngineDataSet dsDWTX_LXR = null;//b_EmpTryBean.getDetailTable();


if(b_EmpTryBean.isApprove)
{
  //corpBean.regData(ds, "dwtxid");
  personBean.regData(ds, "personid");
  //storeBean.regData(ds, "storeid");
  deptBean.regData(ds, "deptid");
  //corpBean.regData(ds, "dwt_dwtxid");
}
boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit)||b_EmpTryBean.masterIsAdd();
boolean isCanDelete =(loginBean.hasLimits(pageCode, op_delete))||b_EmpTryBean.masterIsAdd();
String typeClass = (isCanEdit)?"class=edFocused": "class=edline";
String readonly = (isCanEdit)?"":"readonly";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
  <td width="400"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
  <td  class="activeVTab">员工试用情况</td>
  </tr>
</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
  <td>
<table cellspacing="4" cellpadding="3" border="0" width="100%" bgcolor="#f0f0f0">
<%--<%personBean.regConditionData(ds, "personid");%>--%>
              <tr>
                  <td nowrap class="tdTitle">姓名</td>
                  <td nowrap class="td"><input type="text" name="emp_name" value='<%=m_RowInfo.get("emp_name")%>' maxlength='<%=ds.getColumn("emp_name").getPrecision()%>' style="width:120" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
     <td nowrap class="tdTitle">性别</td>
     <%String emp_sex = m_RowInfo.get("emp_sex");%>
      <td colspan="1" nowrap class="td">
                  男
      <input type="radio" name="emp_sex" value="1" <%=emp_sex.equals("1")?" checked" :""%> checked>
                  女
      <input type="radio" name="emp_sex" value="0" <%=emp_sex.equals("0")?" checked" :""%>></td>

     <td nowrap class="tdTitle">民族</td>
     <td nowrap class="td">
     <%if((isCanEdit)){%>
       <pc:select name="emp_nation" style="width:131">
     <%=personNationBean.getList(m_RowInfo.get("emp_nation"))%>
                      </pc:select>
     <%}else{%>
     <input type="text"  value='<%=personNationBean.getLookupName(m_RowInfo.get("emp_nation"))%>'  style="width:130" class="edline"  readonly>
     <%}%>
     </td>

     <%--  <td nowrap class="tdTitle">培训项目</td>
     <td nowrap class="td"><input type="text" name="train_proj" value='<%=m_RowInfo.get("train_proj")%>' maxlength='<%=ds.getColumn("train_proj").getPrecision()%>' style="width:120" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>--%>
     </tr>
     <tr>

     <td nowrap class="tdTitle">身份证号</td>
                  <td nowrap class="td"><input type="text" name="emp_ic" value='<%=m_RowInfo.get("emp_ic")%>' maxlength='<%=ds.getColumn("emp_ic").getPrecision()%>' style="width:120" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>

    <td nowrap class="tdTitle">电话</td>
                  <td nowrap class="td"><input type="text" name="emp_phone" value='<%=m_RowInfo.get("emp_phone")%>' maxlength='<%=ds.getColumn("emp_phone").getPrecision()%>' style="width:120" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>

   <td nowrap class="tdTitle">政治面貌</td>
     <td nowrap class="td">
     <%if((isCanEdit)){%>
       <pc:select name="emp_polity" style="width:131">
     <%=personPolityBean.getList(m_RowInfo.get("emp_polity"))%>
                      </pc:select>
     <%}else{%>
     <input type="text"  value='<%=personPolityBean.getLookupName(m_RowInfo.get("emp_polity"))%>'  style="width:130" class="edline"  readonly>
     <%}%>
     </td>
            <%
              ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");opkey.add("4");opkey.add("5");
                  ArrayList opval = new ArrayList(); opval.add("很满意"); opval.add("满意"); opval.add("一般");opval.add("不满意");opval.add("很差");
                  ArrayList[] lists  = new ArrayList[]{opkey, opval};
                  String ss=m_RowInfo.get("tech_material_ok");
                  String tt=m_RowInfo.get("techcher_ok");
            %>
  </tr>
     <tr>
       <td nowrap class="tdTitle">学历</td>
     <td nowrap class="td">
     <%if((isCanEdit)){%>
       <pc:select name="emp_study" style="width:131">
     <%=personEducationBean.getList(m_RowInfo.get("emp_study"))%>
         </pc:select>
     <%}else{%>
     <input type="text"  value='<%=personEducationBean.getLookupName(m_RowInfo.get("emp_study"))%>'  style="width:130" class="edline"  readonly>
     <%}%>
     </td>


     <td nowrap  colspan="1" class="tdTitle">出生日期</td>
     <%-- <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhrq").getFieldname()%></td>--%>
                  <td noWrap class="td"><input type="text" name="date_born" value='<%=m_RowInfo.get("date_born")%>' maxlength='10' style="width:85" class="edbox" onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                  <%if(isCanEdit){%>
                  <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.date_born);"></a>
       <%}%>


<td nowrap  colspan="1" class="tdTitle">进厂日期</td>
     <%-- <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhrq").getFieldname()%></td>--%>
                  <td noWrap class="td"><input type="text" name="date_in" value='<%=m_RowInfo.get("date_in")%>' maxlength='10' style="width:85" class="edbox" onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                  <%if(isCanEdit){%>
                  <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.date_in);"></a>
       <%}%>
</tr>
     <tr>

       <td nowrap class="tdTitle">籍贯</td>
     <td nowrap class="td">
     <%if((isCanEdit)){%>
       <pc:select name="emp_native"  style="width:131">
      <%=personNativeBean.getList(m_RowInfo.get("emp_native"))%>
         </pc:select>
     <%}else{%>
     <input type="text"  value='<%=personNativeBean.getLookupName(m_RowInfo.get("emp_native"))%>'  style="width:130" class="edline"  readonly>
     <%}%>
     </td>

     <td nowrap class="tdTitle">试用部门</td>
     <td nowrap class="td">
     <%if((isCanEdit)){%>
       <pc:select name="deptid" addNull="1" style="width:131">
     <%=deptBean.getList(m_RowInfo.get("deptid"))%>
         </pc:select>
     <%}else{%>
     <input type="text"  value='<%=deptBean.getLookupName(m_RowInfo.get("deptid"))%>'  style="width:130" class="edline"  readonly>
     <%}%>
     </td>

      <td nowrap class="tdTitle">试用职务</td>
      <td nowrap class="td">
       <%if((isCanEdit)){%>
         <pc:select name="try_job" addNull="1" style="width:131">
       <%=dutyBean.getList(m_RowInfo.get("try_job"))%>
         </pc:select>
     <%}else{%>
     <input type="text"  value='<%=dutyBean.getLookupName(m_RowInfo.get("try_job"))%>'  style="width:130" class="edline"  readonly>
     <%}%>
     </td>
</tr>
      <tr>
           <td nowrap  class="tdTitle">试用情况</td>
           <td colspan="3" nowrap class="td"> <input type="text" name="try_state" value='<%=m_RowInfo.get("try_state")%>' maxlength='64' style="width:300" class=edFocused onKeyDown="return getNextElement();" ></td>
      </tr>
<tr>

    <td nowrap class="tdTitle">能否转正</td>
     <%String isDirect = m_RowInfo.get("isDirect");%>
      <td colspan="4" nowrap class="td">
                    是
      <input type="radio" name="isDirect" value="1" <%=isDirect.equals("1")?" checked" :""%> checked>
                    否
       <input type="radio" name="isDirect" value="2" <%=isDirect.equals("2")?" checked" :""%>></td>
     </tr>
              <tr>
                  <td nowrap  class="tdTitle">备注</td>
                  <td colspan="3" nowrap class="td"> <input type="text" name="memo" value='<%=m_RowInfo.get("memo")%>' maxlength='64' style="width:300" class=edFocused onKeyDown="return getNextElement();" >
                  </td>

                </tr>
     </tr>
    <tr>
     <td colspan="6" nowrap>
     <table cellspacing=0 width="100%" cellpadding=0>
</table>
<SCRIPT LANGUAGE="javascript">INFO_EX = new TabControl('INFO_EX',0);
  AddTabItem(INFO_EX,'INFO_EX_0','tabDivINFO_EX_0','cntDivINFO_EX_0');
  if (window.top.StatFrame+''!='undefined'){ var tmp_curtab=window.top.StatFrame.GetRegisterVar('INFO_EX');if (tmp_curtab!='') {SetActiveTab(INFO_EX,tmp_curtab);}}
  </SCRIPT>
</td>
</tr>
</table>
<tr>
<td> </td>
</tr>
</table>
</table>
    <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
      <tr>
        <td noWrap class="tableTitle">
        <%if(loginBean.hasLimits(pageCode,op_add)){
          String add = "sumitForm("+Operate.POST_CONTINUE+",-1)";
        %>
          <%if(isCanEdit)%>
          <input name="button2" type="button" class="button" title = "保存添加"onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value='保存添加(N)'><pc:shortcut key="n" script='<%=add%>'/><%}%>
           <%if(loginBean.hasLimits(pageCode,op_add)){
             String reu = "sumitForm("+Operate.POST+",-1)";
           %>
          <%if(isCanEdit)%><input name="button" type="button" title = "保存返回" class="button" onClick="sumitForm(<%=Operate.POST%>);" value='保存返回(S)'><pc:shortcut key="s" script='<%=reu%>'/><%}%>
          <%if(loginBean.hasLimits(pageCode,op_add)){
            String del = "sumitForm("+b_EmpTryBean.DELETE_RETURN+",-1)";
           %>
          <%if(isCanDelete)%><input name="btnback2" type="button" class="button" title = "删除"  onClick="if(confirm('是否删除该记录？'))sumitForm(<%=b_EmpTryBean.DELETE_RETURN%>,<%=request.getParameter("rownum")%>)" value='  删除(D)  '><pc:shortcut key="d" script='<%=del%>'/><%}%>
           <%if(loginBean.hasLimits(pageCode,op_add)){
             String ret = "backList()";
           %>
          <input name="btnback" type="button" title = "返回" class="button" onKeyDown="return onInputKeyboard();" onClick="backList();" value='  返回(C)  '><pc:shortcut key="c" script='<%=ret%>'/><%}%>
        </td>
      </tr>
    </table>
</form>
<%out.print(retu);%>
</body>
</html>
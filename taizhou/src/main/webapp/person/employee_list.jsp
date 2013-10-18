<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("employee_info", request, response))
    return;
  B_EmployeeInfo b_employeeinfoBean = B_EmployeeInfo.getInstance(request);
  LookUp areaBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_AREA);
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT_LIST);
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

function toDetail(){
  location.href='employee_info.jsp';
}

function backList()
{
  location.href='employee_info.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
  </tr>
</table>
<%
  String retu = b_employeeinfoBean.doService(request, response);
  if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();

  RowMap m_RowInfo = b_employeeinfoBean.getMasterRowinfo();   //行到主表的一行信息

  RowMap[] zgjtqkRows= b_employeeinfoBean.getZgjtqkRowinfos();//职工家庭的多行信息
  RowMap[] zgjyqkRows= b_employeeinfoBean.getZgjyqkRowinfos();//职工教育的多行信息
  RowMap[] zggzjyRows= b_employeeinfoBean.getZggzjlRowinfos();//职工工作经历多行信息
  RowMap[] zgpxqkRows= b_employeeinfoBean.getZgpxqkRowinfos();//职工培训情况多行信息
  RowMap[] zgjcqkRows= b_employeeinfoBean.getZgjcqkRowinfos();//职工奖惩情况多行信息
  RowMap[] zgtcahRows= b_employeeinfoBean.getZgtcahRowinfos();//职工特长爱好情况多行信息
  RowMap[] zgbxqkRows= b_employeeinfoBean.getZgbxqkRowinfos();//职工保险情况多行信息
  RowMap[] zgqtxxRows= b_employeeinfoBean.getZgqtxxRowinfos();//职工其他情况多行信息
  RowMap[] zgxxbdRows= b_employeeinfoBean.getZgxxbdRowinfos();//职工信息变动



  EngineDataSet dsDWTX = b_employeeinfoBean.getMaterTable();
  EngineDataSet dsDWTX_LXR = null;//b_employeeinfoBean.getDetailTable();

  EngineDataSet dsrl_zgjtqk = b_employeeinfoBean.getzgjtqkTable();
  EngineDataSet dsrl_zgjyqk = b_employeeinfoBean.getZgjyqkTable();
  EngineDataSet dsrl_zggzjl = b_employeeinfoBean.getZggzjlTable();
  EngineDataSet dsrl_zgpxqk = b_employeeinfoBean.getzgpxqkTable();
  EngineDataSet dsrl_zgjcqk = b_employeeinfoBean.getzgjcqkTable();
  EngineDataSet dsrl_zgtcah = b_employeeinfoBean.getzgtcahTable();
  EngineDataSet dsrl_zgbxqk = b_employeeinfoBean.getzgbxqkTable();
  EngineDataSet dsrl_zgxxbd = b_employeeinfoBean.getzgxxbdTable();
  EngineDataSet dsrl_zgqtxx = b_employeeinfoBean.getzgqtxxTable();

  ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");opkey.add("4");opkey.add("5");
  ArrayList opval = new ArrayList(); opval.add("职员离职"); opval.add("部门调动"); opval.add("类别变动");opval.add("职务变迁");opval.add("职员复职");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};
  boolean isdel=m_RowInfo.get("isDelete").equals("2");

  String typeClass = isdel ? "class=edline" : "class=edFocused";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
  <td width="400"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
  <td  class="activeVTab">员工信息卡</td>
  </tr>
</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
  <td>
<table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">
     <INPUT TYPE="HIDDEN" NAME="personid"  VALUE="<%=m_RowInfo.get("personid")%>">
     <tr>
     <td nowrap class="tdTitle">员工编码</td>
     <td nowrap class="td"><input type="text" name="bm" value='<%=m_RowInfo.get("bm")%>' maxlength="6" style="width:130" <%=typeClass%> onKeyDown="return getNextElement();"></td>
     <td nowrap class="tdTitle">姓名</td>
     <td nowrap class="td"><input type="text" name="xm" value='<%=m_RowInfo.get("xm")%>' maxlength='<%=dsDWTX.getColumn("xm").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();"></td>
     <td nowrap class="tdTitle">部门</td>
     <td nowrap class="td">
     <pc:select name="deptid"  style="width:131">
     <%=deptBean.getList(m_RowInfo.get("deptid"))%>
      </pc:select>
     </td>
     </tr>
     <tr>
     <td nowrap class="tdTitle">类别</td>
     <td nowrap class="td"><pc:select name="lb"  style="width:131"><%=personclassBean.getList(m_RowInfo.get("lb"))%></pc:select></td>
     <td nowrap class="tdTitle">职务</td>
     <td nowrap class="td"><pc:select name="zw" addNull="1" style="width:131"><%=dutyBean.getList(m_RowInfo.get("zw"))%></pc:select></td>
     <td nowrap class="tdTitle">职称</td>
     <td nowrap class="td"><pc:select name="zc" addNull="1" style="width:131"><%=personctechBean.getList(m_RowInfo.get("zc"))%></pc:select></td>
     </tr>
     <tr>
     <td nowrap class="tdTitle">性别</td>
     <td nowrap class="td">
     <%
     boolean isMan = !m_RowInfo.get("sex").equals("0");
     %>
     <input type=RADIO name="sex" value='1'<%=isMan ? " checked" : ""%>>男&nbsp; <input type=RADIO name="sex" value='0'<%=isMan ? "" : " checked"%>>女 </td>
     <td nowrap class="tdTitle">电子邮件</td>
     <td nowrap class="td"><input type="text" name="email" value='<%=m_RowInfo.get("email")%>' maxlength='<%=dsDWTX.getColumn("email").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();"></td>
     <td nowrap class="tdTitle">民族</td>
     <td nowrap class="td"><pc:select name="mz" style="width:131"><%=personNationBean.getList(m_RowInfo.get("mz"))%></pc:select></td>
     </tr>
     <tr>
     <td nowrap class="tdTitle">身份证号</td>
     <td nowrap class="td"><input type="text" name="sfzhm" value='<%=m_RowInfo.get("sfzhm")%>' maxlength='<%=dsDWTX.getColumn("sfzhm").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();"></td>
     <td nowrap class="tdTitle">电话</td>
     <td nowrap class="td"><input type="text" name="phone" value='<%=m_RowInfo.get("phone")%>' maxlength='<%=dsDWTX.getColumn("phone").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();"></td>
     <td nowrap class="tdTitle">政治面貌</td>
     <td nowrap class="td"><pc:select name="zzmm" style="width:131"><%=personPolityBean.getList(m_RowInfo.get("zzmm"))%></pc:select></td>
     </tr>
     <tr>
     <td nowrap class="tdTitle">学历</td>
     <td nowrap class="td"><pc:select name="study" style="width:131"> <%=personEducationBean.getList(m_RowInfo.get("study"))%></pc:select></td>
     <td nowrap class="tdTitle">出生日期</td>
     <td nowrap class="td"><input type="text" name="date_born" value='<%=m_RowInfo.get("date_born")%>' maxlength="10" style="width:106" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
     <%if(!isdel){%><a href="#"><img src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onClick="selectDate(date_born);"></a><%}%>
     </td>
     <td nowrap class="tdTitle">进厂日期</td>
     <td nowrap class="td"><input type="text" name="date_in" value='<%=m_RowInfo.get("date_in")%>' maxlength="10" style="width:106" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
     <%if(!isdel){%><a href="#"><img src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onClick="selectDate(date_in);"></a><%}%></td>
     </tr>
     <tr>
     <td nowrap class="tdTitle">籍贯</td>
     <td nowrap class="td">
       <pc:select name="jg"  style="width:131">
     <%=personNativeBean.getList(m_RowInfo.get("jg"))%>
     </pc:select></td>
     <td nowrap class="tdTitle">地址</td>
     <td colspan="3" nowrap class="td"><input type="text" name="addr" value='<%=m_RowInfo.get("addr")%>' maxlength='<%=dsDWTX.getColumn("addr").getPrecision()%>' style="width:410" <%=typeClass%> onKeyDown="return getNextElement();"></td>
     </tr>
     <tr>
     <td nowrap class="tdTitle">手机</td>
     <td nowrap class="td"><input type="text" name="mobile" value='<%=m_RowInfo.get("mobile")%>' maxlength='<%=dsDWTX.getColumn("mobile").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();"></td>
     <td nowrap class="tdTitle">备注</td>
     <td colspan="3" nowrap class="td"><input type="text" name="bz" value='<%=m_RowInfo.get("bz")%>' maxlength='<%=dsDWTX.getColumn("bz").getPrecision()%>' style="width:410" <%=typeClass%> onKeyDown="return getNextElement();"></td>
     </tr>
     <tr>
     <td colspan="6" nowrap>
    <table cellspacing=0 width="100%" cellpadding=0>
     <tr>
         <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">家庭情况</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_1" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_1');return false;">教育情况</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_2" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_2');return false;">工作经历</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_3" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_3');return false;">培训情况</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_4" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_4');return false;">奖惩情况</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_5" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_5');return false;">特长爱好</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_6" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_6');return false;">变动情况</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_7" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_7');return false;">调薪情况</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_8" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_8');return false;">保险情况</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_9" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_9');return false;">其他</a></div></td>
         <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
     </tr>
     </table>
     <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:750;height:110;overflow-y:auto;overflow-x:auto;">
     <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
     <tr class="tableTitle">
          <td nowrap>
          <%if(!isdel){%>
          <input name="image" class="img" type="image" title="新增" onClick="sumitForm(<%=b_employeeinfoBean.ZGJTQK_ADD%>,-1)" src="../images/add.gif" border="0">
          <%}%>
           </td>
           <td>称谓</td>
           <td>姓名</td>
           <td>职业</td>
           <td>出生日期</td>
           <td>备注</td>
    </tr>
     <%
      RowMap zgjtdetail = null;
      for(int i=0; i<zgjtqkRows.length; i++)
      {
        zgjtdetail = zgjtqkRows[i];
     %>
      <tr>
          <td class="td" width=45 align="center">
          <input name="image3" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_employeeinfoBean.ZGJTQK_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle">
          </td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="cf_<%=i%>" VALUE="<%=zgjtdetail.get("cf")%>" style="width:130" MAXLENGTH="<%=dsrl_zgjtqk.getColumn("cf").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="xm_<%=i%>" VALUE="<%=zgjtdetail.get("xm")%>" style="width:130" MAXLENGTH="<%=dsrl_zgjtqk.getColumn("xm").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="zy_<%=i%>" VALUE="<%=zgjtdetail.get("zy")%>" style="width:130" MAXLENGTH="<%=dsrl_zgjtqk.getColumn("zy").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="csrq_<%=i%>" VALUE="<%=zgjtdetail.get("csrq")%>" style="width:90" MAXLENGTH="10" <%=typeClass%> onChange="checkDate(this)">
          <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(csrq_<%=i%>);"></a></td>
          <td class="td" align="right"><INPUT TYPE="TEXT" NAME="bz_<%=i%>" VALUE="<%=zgjtdetail.get("bz")%>" style="width:150" MAXLENGTH="<%=dsrl_zgjtqk.getColumn("bz").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
      </tr>
      <%}%>
      </table>
      <script language="javascript">initDefaultTableRow('tableview1',1);</script>
      </div>
<div id="cntDivINFO_EX_1" class="tabContent" style="display:none;width:750;height:110;overflow-y:auto;overflow-x:hidden;">
    <center>
        <table id="tableview2" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
            <tr class="tableTitle">
                  <td  width=45 align="center" nowrap>
                  <%if(!isdel){%>
                  <input name="image4" class="img" type="image" title="新增" onClick="sumitForm(<%=b_employeeinfoBean.ZGJYQK_ADD%>,-1)" src="../images/add.gif" border="0">
                 <%}%>
                  </td>
                  <td>毕业学校</td>
                  <td>所学专业</td>
                  <td>证明人</td>
                  <td>开始日期</td>
                  <td>结束日期</td>
                  <td>备注</td>
            </tr>
     <%
      RowMap zgjydetail = null;
      for(int i=0; i<zgjyqkRows.length; i++)
      {
        zgjydetail = zgjyqkRows[i];
     %>
            <tr>
                <td class="td" align="center">
                <input name="image32" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_employeeinfoBean.ZGJYQK_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle">
                </td>
                <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="byxx_<%=i%>" VALUE="<%=zgjydetail.get("byxx")%>" style="width:100" MAXLENGTH="<%=dsrl_zgjyqk.getColumn("byxx").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="sxzy_<%=i%>" VALUE="<%=zgjydetail.get("sxzy")%>" style="width:100" MAXLENGTH="<%=dsrl_zgjyqk.getColumn("sxzy").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="zmr_<%=i%>" VALUE="<%=zgjydetail.get("zmr")%>" style="width:100" MAXLENGTH="<%=dsrl_zgjyqk.getColumn("zmr").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                <td class="td" align="right">
                <INPUT TYPE="TEXT" NAME="jy_kssj_<%=i%>" VALUE="<%=zgjydetail.get("kssj")%>" style="width:106" MAXLENGTH="10" <%=typeClass%>  onChange="checkDate(this)">
                <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(jy_kssj_<%=i%>);">
                </a>
                </td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="jy_jssj_<%=i%>" VALUE="<%=zgjydetail.get("jssj")%>" style="width:106" MAXLENGTH="10" <%=typeClass%>  onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(jy_jssj_<%=i%>);"></a></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="jy_bz_<%=i%>" VALUE="<%=zgjydetail.get("bz")%>" style="width:100" MAXLENGTH="<%=dsrl_zgjyqk.getColumn("bz").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
            </tr>
            <%}%>
        </table>
        <script language="javascript">initDefaultTableRow('tableview2',1);</script>
    </center>
</div>
<div id="cntDivINFO_EX_2" class="tabContent" style="display:none;width:750;height:110;overflow-y:auto;overflow-x:hidden;">
      <center>
            <table id="tableview3" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
                <tr class="tableTitle">
                    <td nowrap><%if(!isdel){%><input name="image5" class="img" type="image" title="新增" onClick="sumitForm(<%=b_employeeinfoBean.ZGGZJL_ADD%>,-1)" src="../images/add.gif" border="0"><%}%></td>
                    <td>工作单位</td>
                    <td>职务</td>
                    <td>开始日期</td>
                    <td>结束日期</td>
                    <td>备注</td>
                </tr>
     <%
      RowMap zggzjldetail = null;
      for(int i=0; i<zggzjyRows.length; i++)
      {
        zggzjldetail = zggzjyRows[i];
     %>
                <tr>
                    <td class="td"  width=45 align="center">
                    <input name="image33" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_employeeinfoBean.ZGGZJL_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle" ></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="gzdw_<%=i%>" VALUE="<%=zggzjldetail.get("gzdw")%>" style="width:130" MAXLENGTH="<%=dsrl_zggzjl.getColumn("gzdw").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="zw_<%=i%>" VALUE="<%=zggzjldetail.get("zw")%>" style="width:130" MAXLENGTH="<%=dsrl_zggzjl.getColumn("zw").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="gz_kssj_<%=i%>" VALUE="<%=zggzjldetail.get("kssj")%>" style="width:106" MAXLENGTH="10" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
                    <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(gz_kssj_<%=i%>);"></a></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="gz_jssj_<%=i%>" VALUE="<%=zggzjldetail.get("jssj")%>" style="width:106" MAXLENGTH="10" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
                    <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(gz_jssj_<%=i%>);"></a></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="gz_bz_<%=i%>" VALUE="<%=zggzjldetail.get("bz")%>" style="width:130" MAXLENGTH="<%=dsrl_zggzjl.getColumn("bz").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                </tr>
            <%}%>
            </table>
            <script language="javascript">initDefaultTableRow('tableview3',1);</script>
      </center>
</div>
<div id="cntDivINFO_EX_3" class="tabContent" style="display:none;width:750;height:110;overflow-y:auto;overflow-x:hidden;">
      <center>
            <table width="100%" border="0" cellpadding="0" cellspacing="1" class="table" id="tableview4" dwcopytype="CopyTableRow">
                <tr class="tableTitle">
                    <td  width=45 align="center" nowrap><%if(!isdel){%><input name="image5" class="img" type="image" title="新增" onClick="sumitForm(<%=b_employeeinfoBean.ZGPXQK_ADD%>,-1)" src="../images/add.gif" border="0"><%}%></td>
                    <td>培训项目</td>
                    <td>组织机构</td>
                    <td>开始日期</td>
                    <td>结束日期</td>
                    <td>备注</td>
                </tr>
     <%
      RowMap zgpxqkdetail = null;
      for(int i=0; i<zgpxqkRows.length; i++)
      {
        zgpxqkdetail = zgpxqkRows[i];
     %>
                <tr >
                    <td class="td" align="center">
                    <input name="image33" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_employeeinfoBean.ZGPXQK_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle"></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="pxxm_<%=i%>" VALUE="<%=zgpxqkdetail.get("pxxm")%>" style="width:130" MAXLENGTH="<%=dsrl_zgpxqk.getColumn("pxxm").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="zzjg_<%=i%>" VALUE="<%=zgpxqkdetail.get("zzjg")%>" style="width:130" MAXLENGTH="<%=dsrl_zgpxqk.getColumn("zzjg").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="px_kssj_<%=i%>" VALUE="<%=zgpxqkdetail.get("kssj")%>" style="width:90" MAXLENGTH="10" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
                    <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(px_kssj_<%=i%>);"></a></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="px_jssj_<%=i%>" VALUE="<%=zgpxqkdetail.get("jssj")%>" style="width:90" MAXLENGTH="10" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
                    <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(px_jssj_<%=i%>);"></a></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="px_bz_<%=i%>" VALUE="<%=zgpxqkdetail.get("bz")%>" style="width:150" MAXLENGTH="<%=dsrl_zgpxqk.getColumn("bz").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                </tr>
                <%}%>
            </table>
            <script language="javascript">initDefaultTableRow('tableview4',1);</script>
      </center>
</div>
<div id="cntDivINFO_EX_4" class="tabContent" style="display:none;width:750;height:110;overflow-y:auto;overflow-x:hidden;">
      <center>
            <table width="100%" border="0" cellpadding="0" cellspacing="1" class="table" id="tableview5" dwcopytype="CopyTableRow">
                  <tr class="tableTitle">
                      <td  width=45 align="center" nowrap><%if(!isdel){%><input name="image5" class="img" type="image" title="新增" onClick="sumitForm(<%=b_employeeinfoBean.ZGJCQK_ADD%>,-1)" src="../images/add.gif" border="0"><%}%></td>
                      <td>奖惩结果</td>
                      <td>原因</td>
                      <td>日期</td>
                      <td>备注</td>
                  </tr>
     <%
      RowMap zgjcqkdetail = null;
      for(int i=0; i<zgjcqkRows.length; i++)
      {
        zgjcqkdetail = zgjcqkRows[i];
     %>
                  <tr>
                        <td class="td" align="center">
                        <input name="image33" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_employeeinfoBean.ZGJCQK_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle"></td>
                        <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="jcjg_<%=i%>" VALUE="<%=zgjcqkdetail.get("jcjg")%>" style="width:180" MAXLENGTH="<%=dsrl_zgjcqk.getColumn("jcjg").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                        <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="yy_<%=i%>" VALUE="<%=zgjcqkdetail.get("yy")%>" style="width:180" MAXLENGTH="<%=dsrl_zgjcqk.getColumn("yy").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                        <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="rq_<%=i%>" VALUE="<%=zgjcqkdetail.get("rq")%>" style="width:80" MAXLENGTH="10" <%=typeClass%> onChange="checkDate(this)">
                        <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(rq_<%=i%>);"></a></td>
                        <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="jc_bz_<%=i%>" VALUE="<%=zgjcqkdetail.get("bz")%>" style="width:200" MAXLENGTH="<%=dsrl_zgjcqk.getColumn("bz").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                  </tr>
                  <%}%>
            </table>
            <script language="javascript">initDefaultTableRow('tableview5',1);</script>
      </center>
</div>
<div id="cntDivINFO_EX_5" class="tabContent" style="display:none;width:750;height:110;overflow-y:auto;overflow-x:hidden;">
    <center>
        <table width="100%" border="0" cellpadding="0" cellspacing="1" class="table" id="tableview6" dwcopytype="CopyTableRow">
              <tr class="tableTitle">
                  <td  width=45 align="center" nowrap><%if(!isdel){%><input name="image5" class="img" type="image" title="新增" onClick="sumitForm(<%=b_employeeinfoBean.ZGTCAH_ADD%>,-1)" src="../images/add.gif" border="0"><%}%></td>
                  <td>特长/爱好</td>
                  <td>等级/证书</td>
                  <td>发证机构</td>
                  <td>发证日期</td>
                  <td>备注</td>
              </tr>
     <%
      RowMap zgtcahdetail = null;
      for(int i=0; i<zgtcahRows.length; i++)
      {
        zgtcahdetail = zgtcahRows[i];
     %>
              <tr >
                <td class="td" align="center">
                  <input name="image33" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_employeeinfoBean.ZGTCAH_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle"></td>
                <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="tcah_<%=i%>" VALUE="<%=zgtcahdetail.get("tcah")%>" style="width:120" MAXLENGTH="<%=dsrl_zgtcah.getColumn("tcah").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="djzs_<%=i%>" VALUE="<%=zgtcahdetail.get("djzs")%>" style="width:120" MAXLENGTH="<%=dsrl_zgtcah.getColumn("djzs").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="fzjg_<%=i%>" VALUE="<%=zgtcahdetail.get("fzjg")%>" style="width:120" MAXLENGTH="<%=dsrl_zgtcah.getColumn("fzjg").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
                <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="fzrq_<%=i%>" VALUE="<%=zgtcahdetail.get("fzrq")%>" style="width:90" MAXLENGTH="10" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(fzrq_<%=i%>);"></a></td>
                <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="tc_bz_<%=i%>" VALUE="<%=zgtcahdetail.get("bz")%>" style="width:150" MAXLENGTH="<%=dsrl_zgtcah.getColumn("bz").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
              </tr>
        <%}%>
        </table>
        <script language="javascript">initDefaultTableRow('tableview6',1);</script>
    </center>
</div>
<div id="cntDivINFO_EX_6" class="tabContent" style="display:none;width:750;height:110;overflow-y:auto;overflow-x:hidden;">
    <center>
          <table width="100%" border="0" cellpadding="0" cellspacing="1" class="table" id="tableview7" dwcopytype="CopyTableRow">
              <tr class="tableTitle">
                  <td>变动类型</td>
                  <td>变动日期</td>
                  <td>变动前</td>
                  <td>变动后</td>
                  <td>调整原因</td>
                  <td>制单人</td>
                  <td>备注</td>
                 </tr>
              <%
              if(dsrl_zgxxbd!=null)
              {
                int te=0;
                dsrl_zgxxbd.first();
                for(int i=0;i<dsrl_zgxxbd.getRowCount();i++){
                  String ss=dsrl_zgxxbd.getValue("bdlx");
                  try{
                    te=Integer.parseInt(ss)-1;
                    }catch(Exception e){te=0;}
              %>
              <tr>
                  <td class="td"><%=opval.get(te)%></td>
                  <td class="td"><%=dsrl_zgxxbd.getValue("bdrq")%></td>
                  <td class="td"><%=dsrl_zgxxbd.getValue("bdqID")%></td>
                  <td class="td"><%=dsrl_zgxxbd.getValue("bdhID")%></td>
                  <td class="td"><%=dsrl_zgxxbd.getValue("bdyy")%></td>
                  <td class="td"><%=dsrl_zgxxbd.getValue("czy")%></td>
                  <td class="td"><%=dsrl_zgxxbd.getValue("bz")%></td>
               </tr>
              <%dsrl_zgxxbd.next();}
              }%>
          </table>
          <script language="javascript">initDefaultTableRow('tableview7',1);</script>
    </center>
</div>
<div id="cntDivINFO_EX_7" class="tabContent" style="display:none;width:750;height:110;overflow-y:auto;overflow-x:hidden;">
    <center>
          <table width="100%" border="0" cellpadding="0" cellspacing="1" class="table" id="tableview8" dwcopytype="CopyTableRow">
          <tr class="tableTitle">
              <td>调整日期</td>
              <td>调整内容</td>
              <td>调整前</td>
              <td>调整金额</td>
              <td>调整后</td>
              <td>备注</td>
              <td>调整原因</td>
              <td>制单人</td>
               <td>审核人</td>
          </tr>
          <%
          if(dsDWTX_LXR!=null)
          {
          dsDWTX_LXR.first();
          for(int i=0;i<dsDWTX_LXR.getRowCount();i++){%>
          <tr >
              <td class="td" align="center">
              <td class="td"><%=""%></td>
              <td class="td"><%=""%></td>
              <td class="td"><%=""%></td>
              <td class="td"><%=""%></td>
              <td class="td"><%=""%></td>
              <td class="td"><%=""%></td>
              <td class="td"><%=""%></td>
              <td class="td"><%=""%></td>
               <td class="td"><%=""%></td>
          </tr>
          <%dsDWTX_LXR.next();}
          }%>
          </table>
          <script language="javascript">initDefaultTableRow('tableview8',1);</script>
    </center>
</div>
<div id="cntDivINFO_EX_8" class="tabContent" style="display:none;width:750;height:110;overflow-y:auto;overflow-x:hidden;">
<center>
    <table width="100%" border="0" cellpadding="0" cellspacing="1" class="table" id="tableview9" dwcopytype="CopyTableRow">
        <tr class="tableTitle">
            <td  width=45 align="center" nowrap><%if(!isdel){%><input name="image5" class="img" type="image" title="新增" onClick="sumitForm(<%=b_employeeinfoBean.ZGBXQK_ADD%>,-1)" src="../images/add.gif" border="0"><%}%></td>
            <td>险种</td>
            <td>承保公司</td>
            <td>保险日期</td>
            <td>保险额</td>
            <td>备注</td>
        </tr>
     <%
      RowMap zgbxqkdetail = null;
      for(int i=0; i<zgbxqkRows.length; i++)
      {
        zgbxqkdetail = zgbxqkRows[i];
     %>
        <tr >
            <td class="td" align="center">
            <input name="image33" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_employeeinfoBean.ZGBXQK_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle"></td>
            <td class="td"  align="left"><INPUT TYPE="TEXT" NAME="xz_<%=i%>" align="left" VALUE="<%=zgbxqkdetail.get("xz")%>" style="width:130" MAXLENGTH="<%=dsrl_zgbxqk.getColumn("xz").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
            <td class="td"  align="left"><INPUT TYPE="TEXT" NAME="bxgs_<%=i%>" align="left" VALUE="<%=zgbxqkdetail.get("bxgs")%>" style="width:130" MAXLENGTH="<%=dsrl_zgbxqk.getColumn("bxgs").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
            <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="bxrq_<%=i%>" maxlength="10" VALUE="<%=zgbxqkdetail.get("bxrq")%>" style="width:80" MAXLENGTH="<%=dsrl_zgbxqk.getColumn("bxrq").getPrecision()%>" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
            <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(bxrq_<%=i%>);"></a></td>
            <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="bxe_<%=i%>" VALUE="<%=zgbxqkdetail.get("bxe")%>" style="width:130" MAXLENGTH="<%=dsrl_zgbxqk.getColumn("bxe").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
            <td class="td"  align="left"><INPUT TYPE="TEXT" NAME="bx_bz_<%=i%>" align="left" VALUE="<%=zgbxqkdetail.get("bz")%>" style="width:150" MAXLENGTH="<%=dsrl_zgbxqk.getColumn("bz").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
        </tr>
        <%}%>
    </table>
    <script language="javascript">initDefaultTableRow('tableview9',1);</script>
</center>
</div>
<div id="cntDivINFO_EX_9" class="tabContent" style="display:none;width:750;height:110;overflow-y:auto;overflow-x:hidden;">
    <center>
        <table width="100%" border="0" cellpadding="0" cellspacing="1" class="table" id="tableview10" dwcopytype="CopyTableRow">
        <tr class="tableTitle">
     <%
           if(zgqtxxRows.length==0){
      %>
            <td  width=45 align="center" nowrap><%if(!isdel){%><input name="image5" class="img" type="image" title="新增" onClick="sumitForm(<%=b_employeeinfoBean.ZGQTXX_ADD%>,-1)" src="../images/add.gif" border="0"><%}%></td>
      <%}else{%><td  width=45 align="center" nowrap></td><%}%>
            <td>劳动合同签订日期</td>
            <td>合同号</td>
            <td>试用期限</td>
            <td>合同期限</td>
            <td>宿舍入住时间</td>
            <td>房间号</td>
            <td>工会</td>
            <td>户口</td>
            <td>暂住证号</td>
        </tr>
        <%      RowMap zgqtxxdetail = null;
      for(int i=0; i<zgqtxxRows.length; i++)
      {
        zgqtxxdetail = zgqtxxRows[i];%>
        <tr >
            <td class="td" align="center">
            <input name="image33" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_employeeinfoBean.ZGQTXX_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle"></td>
           <td class="td"  align="left"><INPUT TYPE="TEXT" NAME="qdrq_<%=i%>" VALUE="<%=zgqtxxdetail.get("qdrq")%>" style="width:70" MAXLENGTH="<%=dsrl_zgqtxx.getColumn("qdrq").getPrecision()%>" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
            <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(qdrq_<%=i%>);"></a></td>
            <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="hth_<%=i%>" VALUE="<%=zgqtxxdetail.get("hth")%>" style="width:60" MAXLENGTH="<%=dsrl_zgqtxx.getColumn("hth").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
            <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="syrq_<%=i%>" VALUE="<%=zgqtxxdetail.get("syrq")%>" style="width:70" MAXLENGTH="<%=dsrl_zgqtxx.getColumn("syrq").getPrecision()%>" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
            <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(syrq_<%=i%>);"></a></td>
            <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="htqx_<%=i%>" VALUE="<%=zgqtxxdetail.get("htqx")%>" style="width:70" MAXLENGTH="<%=dsrl_zgqtxx.getColumn("htqx").getPrecision()%>" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
            <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(htqx_<%=i%>);"></a></td>
            <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="rzrq_<%=i%>" VALUE="<%=zgqtxxdetail.get("rzrq")%>" style="width:70" MAXLENGTH="<%=dsrl_zgqtxx.getColumn("rzrq").getPrecision()%>" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();">
            <a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(rzrq_<%=i%>);"></a></td>
            <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="fjh_<%=i%>" VALUE="<%=zgqtxxdetail.get("fjh")%>" style="width:50" MAXLENGTH="<%=dsrl_zgqtxx.getColumn("fjh").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
            <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="gh_<%=i%>" VALUE="<%=zgqtxxdetail.get("gh")%>" style="width:50" MAXLENGTH="<%=dsrl_zgqtxx.getColumn("gh").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
            <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="hk_<%=i%>" VALUE="<%=zgqtxxdetail.get("hk")%>" style="width:50" MAXLENGTH="<%=dsrl_zgqtxx.getColumn("hk").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
            <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="zzz_<%=i%>" VALUE="<%=zgqtxxdetail.get("zzz")%>" style="width:50" MAXLENGTH="<%=dsrl_zgqtxx.getColumn("zzz").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();"></td>
        </tr>
        <%}%>
        </table>
        <script language="javascript">initDefaultTableRow('tableview10',1);</script>
    </center>
</div>
  <SCRIPT LANGUAGE="javascript">INFO_EX = new TabControl('INFO_EX',0);
      AddTabItem(INFO_EX,'INFO_EX_0','tabDivINFO_EX_0','cntDivINFO_EX_0');
      AddTabItem(INFO_EX,'INFO_EX_1','tabDivINFO_EX_1','cntDivINFO_EX_1');
      AddTabItem(INFO_EX,'INFO_EX_2','tabDivINFO_EX_2','cntDivINFO_EX_2');
      AddTabItem(INFO_EX,'INFO_EX_3','tabDivINFO_EX_3','cntDivINFO_EX_3');
      AddTabItem(INFO_EX,'INFO_EX_4','tabDivINFO_EX_4','cntDivINFO_EX_4');
      AddTabItem(INFO_EX,'INFO_EX_5','tabDivINFO_EX_5','cntDivINFO_EX_5');
      AddTabItem(INFO_EX,'INFO_EX_6','tabDivINFO_EX_6','cntDivINFO_EX_6');
      AddTabItem(INFO_EX,'INFO_EX_7','tabDivINFO_EX_7','cntDivINFO_EX_7');
      AddTabItem(INFO_EX,'INFO_EX_8','tabDivINFO_EX_8','cntDivINFO_EX_8');
      AddTabItem(INFO_EX,'INFO_EX_9','tabDivINFO_EX_9','cntDivINFO_EX_9');
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
          <%if(!isdel){%>
          <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value='保存添加'>
          <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value='保存返回'>
          <input name="btnback2" type="button" class="button" onClick='sumitForm(<%=b_employeeinfoBean.DELETE_RETURN%>,<%=request.getParameter("rownum")%>)' value='  删除  '>
          <%}%>
          <input name="btnback" type="button" class="button" onClick="backList();" value='  返回  '>
        </td>
      </tr>
    </table>
</form>
<%out.print(retu);%>
</body>
</html></font></font>
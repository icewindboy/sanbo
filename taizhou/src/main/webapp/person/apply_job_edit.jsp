<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "apply_job";
%>
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
  B_ApplyJob B_ApplyJobBean = B_ApplyJob.getInstance(request);
  LookUp areaBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_AREA);
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);
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
  location.href='apply_job.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
  </tr>
</table>
<%
  String retu = B_ApplyJobBean.doService(request, response);
  if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  RowMap m_RowInfo = B_ApplyJobBean.getMasterRowinfo();   //行到主表的一行信息

  RowMap[] ypgzjlRows= B_ApplyJobBean.getYpgzjlRowinfos();//从表应聘人员工作经历的多行信息
  RowMap[] ypjyqkRows= B_ApplyJobBean.getYpjyqkRowinfos();//从表应聘人员教育情况的多行信息
  RowMap[] msxxRows = B_ApplyJobBean.getMsxxRowinfos();   //面试信息人员的多行信息

  EngineDataSet dsDWTX = B_ApplyJobBean.getMaterTable();
  EngineDataSet dsDWTX_LXR = null;//B_ApplyJobBean.getDetailTable();

  EngineDataSet dsrl_ypgzjl = B_ApplyJobBean.getYpgzjlTable();
  EngineDataSet dsrl_ypjyqk = B_ApplyJobBean.getYpjyqkTable();
  EngineDataSet dsrl_msxx = B_ApplyJobBean.getMsxxTable();

  String typeClass = "class=edbox";
  String readonly = "";
  boolean isCanEdit = true;
  boolean isCanDelete = true;

  //typeClass = isCanEdit?"class=edbox":"class=edline";  //能编辑显示输入框,不能编辑显示直线...
  //readonly =  isCanEdit?"":"readonly";                 //能编辑为空,不能编辑为只读...
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
  <td width="400"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
  <td  class="activeVTab">应聘信息卡</td>
  </tr>
</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
  <td>
<table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">
    <INPUT TYPE="HIDDEN" NAME="personid"  VALUE="<%=m_RowInfo.get("personid")%>">
     <tr>
     <td nowrap class="tdTitle">姓名</td>
     <td nowrap class="td"><input type="text" name="xm" value='<%=m_RowInfo.get("xm")%>' maxlength="6" style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
     <td nowrap class="tdTitle">应聘职位</td>
     <td nowrap class="td"><input type="text" name="ypzw" value='<%=m_RowInfo.get("ypzw")%>' maxlength='<%=dsDWTX.getColumn("ypzw").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
     <td nowrap class="tdTitle">面试人员</td>
     <td nowrap class="td"><input type="text" name="msry" value='<%=m_RowInfo.get("msry")%>' maxlength="6" style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
     </tr>
     <tr>
     <td nowrap class="tdTitle">性别</td>
     <td nowrap class="td">
     <%
     boolean isMan = !m_RowInfo.get("sex").equals("0");
     %>
     <input type=RADIO name="sex" value='1'<%=isMan ? " checked" : ""%>>男&nbsp; <input type=RADIO name="sex" value='0'<%=isMan ? "" : " checked"%>>女
     <td nowrap class="tdTitle">电子邮件</td>
     <td nowrap class="td"><input type="text" name="email" value='<%=m_RowInfo.get("email")%>' maxlength='<%=dsDWTX.getColumn("email").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
     <td nowrap class="tdTitle">民族</td>
     <td nowrap class="td">
     <pc:select name="mz" style="width:131">
     <%=personNationBean.getList(m_RowInfo.get("mz"))%>
     </pc:select>
     </td>
     </tr>
     <tr>
     <td nowrap class="tdTitle">身份证号</td>
     <td nowrap class="td"><input type="text" name="sfzhm" value='<%=m_RowInfo.get("sfzhm")%>' maxlength='<%=dsDWTX.getColumn("sfzhm").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
     <td nowrap class="tdTitle">电话</td>
     <td nowrap class="td"><input type="text" name="phone" value='<%=m_RowInfo.get("phone")%>' maxlength='<%=dsDWTX.getColumn("phone").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
     <td nowrap class="tdTitle">政治面貌</td>
     <td nowrap class="td">
     <pc:select name="zzmm" style="width:131">
     <%=personPolityBean.getList(m_RowInfo.get("zzmm"))%>
     </pc:select>
     </td>
     </tr>
     <tr>
     <td nowrap class="tdTitle">学历</td>
     <td nowrap class="td">
     <pc:select name="study" style="width:131">
     <%=personEducationBean.getList(m_RowInfo.get("study"))%>
     </pc:select>
      </td>
      <td nowrap class="tdTitle">出生日期</td>
     <td nowrap class="td"><input type="text"  <%=typeClass%> name="date_born" value='<%=m_RowInfo.get("date_born")%>' maxlength="10" style="width:130"  onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=readonly%>>
     <a href="#"><img src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onClick="selectDate(date_born);"></a>
     </td>

     </tr>
     <tr>
     <td nowrap class="tdTitle">籍贯</td>
     <td nowrap class="td">
       <pc:select name="jg"  style="width:130">
     <%=personNativeBean.getList(m_RowInfo.get("jg"))%>
     </pc:select></td>
     </td>
     <td nowrap class="tdTitle">地址</td>
    <td colspan="3" nowrap class="td"><input type="text" name="addr" value='<%=m_RowInfo.get("addr")%>' maxlength='<%=dsDWTX.getColumn("addr").getPrecision()%>' style="width:90%" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
     </tr>
     <tr>
     <td nowrap class="tdTitle">手机</td>
     <td nowrap class="td"><input type="text" name="mobile" value='<%=m_RowInfo.get("mobile")%>' maxlength='<%=dsDWTX.getColumn("mobile").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
     <td nowrap class="tdTitle">备注</td>
     <td colspan="3" nowrap class="td"><input type="text" name="bz" value='<%=m_RowInfo.get("bz")%>' maxlength='<%=dsDWTX.getColumn("bz").getPrecision()%>' style="width:90%" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
     </tr>
     <tr>
     <td nowrap class="tdTitle">是否面试</td>
     <td  nowrap class="td">
     <%
     boolean sfms = !m_RowInfo.get("sfms").equals("0");
     %>
     <input type=RADIO name="sfms" value='1'<%=sfms ? " checked" : ""%>>是&nbsp;<input type=RADIO name="sfms" value='0'<%=sfms ? "" : " checked"%>>否
     </td>
     <td nowrap class="tdTitle">是否录用</td>
     <td nowrap class="td">
     <%
     boolean sfly = !m_RowInfo.get("sfly").equals("0");
     %>
     <input type=RADIO name="sfly" value='1'<%=sfly ? " checked" : ""%>>是&nbsp;<input type=RADIO name="sfly" value='0'<%=sfly ? "" : " checked"%>>否
     </td>

     </tr>

     <tr>
     <td colspan="6" nowrap>
    <table cellspacing=0 width="100%" cellpadding=0>
     <tr>
         <td nowrap><div id="tabDivINFO_EX_0" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">教育情况</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_1" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_1');return false;">工作经历</a></div></td>
         <td nowrap><div id="tabDivINFO_EX_2" class="normalTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_2');return false;">面试信息</a></div></td>
         <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
     </tr>
     </table>
<div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:750;height:110;overflow-y:auto;overflow-x:hidden;">
    <center>
        <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
            <tr class="tableTitle">
                  <td nowrap>
                   <%if(loginBean.hasLimits(pageCode,op_add)){
                     String add = "sumitForm("+B_ApplyJobBean.YPJYQK_ADD+",-1)";
                   %>
                  <%if(isCanEdit)%>
                  <input name="image4" class="img" type="image" value="新增(A)" onClick="sumitForm(<%=B_ApplyJobBean.YPJYQK_ADD%>,-1)" src="../images/add.gif" border="0">
                     <pc:shortcut key="a" script='<%=add%>'/><%}%>
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
      for(int i=0; i<ypjyqkRows.length; i++)
      {
        zgjydetail = ypjyqkRows[i];
     %>
            <tr>
                <td class="td" align="center">
                <%if(isCanDelete){%><input name="image32" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=B_ApplyJobBean.YPJYQK_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle"><%}%>
                </td>
                <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="byxx_<%=i%>" VALUE="<%=zgjydetail.get("byxx")%>" style="width:100" MAXLENGTH="<%=dsrl_ypjyqk.getColumn("byxx").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="sxzy_<%=i%>" VALUE="<%=zgjydetail.get("sxzy")%>" style="width:100" MAXLENGTH="<%=dsrl_ypjyqk.getColumn("sxzy").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="zmr_<%=i%>" VALUE="<%=zgjydetail.get("zmr")%>" style="width:100" MAXLENGTH="<%=dsrl_ypjyqk.getColumn("zmr").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                <td class="td" align="right">
                <INPUT TYPE="TEXT" NAME="kssj_<%=i%>" VALUE="<%=zgjydetail.get("kssj")%>" style="width:106" MAXLENGTH="10" <%=typeClass%>  onChange="checkDate(this)" <%=readonly%>>
                <%if((isCanEdit)){%><a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(kssj_<%=i%>);">
                </a><%}%>
                </td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="jssj_<%=i%>" VALUE="<%=zgjydetail.get("jssj")%>" style="width:106" MAXLENGTH="10" <%=typeClass%>  onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=readonly%>>
                <%if((isCanEdit)){%><a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(jssj_<%=i%>);"></a><%}%></td>
                <td class="td" align="right"><INPUT TYPE="TEXT" NAME="bz_<%=i%>" VALUE="<%=zgjydetail.get("bz")%>" style="width:100" MAXLENGTH="<%=dsrl_ypjyqk.getColumn("bz").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
            </tr>
            <%}%>
        </table>
        <script language="javascript">initDefaultTableRow('tableview1',1);</script>
    </center>
</div>
 <div id="cntDivINFO_EX_1" class="tabContent" style="display:none;width:750;height:110;overflow-y:auto;overflow-x:hidden;">
      <center>
            <table id="tableview2" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
                <tr class="tableTitle">
                  <%if(loginBean.hasLimits(pageCode,op_add)){
                     String add = "sumitForm("+B_ApplyJobBean.YPJYQK_ADD+",-1)";
                   %>
                  <%if(isCanEdit)%>
                    <td nowrap><%if(isCanEdit)%><input name="image5" class="img" type="image" title="新增" onClick="sumitForm(<%=B_ApplyJobBean.YPGZJL_ADD%>,-1)" src="../images/add.gif" border="0"><pc:shortcut key="a" script='<%=add%>'/><%}%></td>
                    <td>工作单位</td>
                    <td>职务</td>
                    <td>开始日期</td>
                    <td>结束日期</td>
                    <td>备注</td>
                </tr>
     <%
      RowMap ypgzjldetail = null;
      for(int i=0; i<ypgzjlRows.length; i++)
      {
          ypgzjldetail = ypgzjlRows[i];
     %>
                <tr>
                    <td class="td"  width=45 align="center">
                    <%if(isCanDelete){%><input name="image33" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=B_ApplyJobBean.YPGZJL_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle" ><%}%></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="gzdw_<%=i%>" VALUE="<%=ypgzjldetail.get("gzdw")%>" style="width:130" MAXLENGTH="<%=dsrl_ypgzjl.getColumn("gzdw").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="zw_<%=i%>" VALUE="<%=ypgzjldetail.get("zw")%>" style="width:130" MAXLENGTH="<%=dsrl_ypgzjl.getColumn("zw").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="gz_kssj_<%=i%>" VALUE="<%=ypgzjldetail.get("kssj")%>" style="width:106" MAXLENGTH="10" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=readonly%>>
                    <%if((isCanEdit)){%><a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(gz_kssj_<%=i%>);"></a><%}%></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="gz_jssj_<%=i%>" VALUE="<%=ypgzjldetail.get("jssj")%>" style="width:106" MAXLENGTH="10" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=readonly%>>
                    <%if((isCanEdit)){%><a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(gz_jssj_<%=i%>);"></a></a><%}%></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="bz2_<%=i%>" VALUE="<%=ypgzjldetail.get("bz")%>" style="width:130" MAXLENGTH="<%=dsrl_ypgzjl.getColumn("bz").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
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
                   <%if(loginBean.hasLimits(pageCode,op_add)){
                     String add = "sumitForm("+B_ApplyJobBean.YPJYQK_ADD+",-1)";
                   %>
                    <td nowrap><%if(isCanEdit)%><input name="image5" class="img" type="image" vaule="新增(A)" onClick="sumitForm(<%=B_ApplyJobBean.MSXX_ADD%>,-1)" src="../images/add.gif" border="0"><pc:shortcut key="a" script='<%=add%>'/><%}%></td>
                    <td>面试方式</td>
                    <td>面试人员</td>
                    <td>面试时间</td>
                    <td>面试意见</td>
                    <td>职位匹配信息</td>
                </tr>
     <%
      RowMap msxxdetail = null;
      for(int i=0; i<msxxRows.length; i++)
      {
          msxxdetail = msxxRows[i];
     %>
                <tr>
                    <td class="td"  width=45 align="center">
                    <%if(isCanDelete){%><input name="image33" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=B_ApplyJobBean.MSXX_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle" ><%}%></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="msfs_<%=i%>" VALUE="<%=msxxdetail.get("msfs")%>" style="width:130" MAXLENGTH="<%=dsrl_msxx.getColumn("msfs").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="msry_<%=i%>" VALUE="<%=msxxdetail.get("msry")%>" style="width:130" MAXLENGTH="<%=dsrl_msxx.getColumn("msry").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="mssj_<%=i%>" VALUE="<%=msxxdetail.get("mssj")%>" style="width:106" MAXLENGTH="10" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=readonly%>>
                    <%if((isCanEdit)){%><a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(mssj_<%=i%>);"></a><%}%></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="msyj_<%=i%>" VALUE="<%=msxxdetail.get("msyj")%>" style="width:130" MAXLENGTH="<%=dsrl_msxx.getColumn("msyj").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                    <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="zwppxx_<%=i%>" VALUE="<%=msxxdetail.get("zwppxx")%>" style="width:130" MAXLENGTH="<%=dsrl_msxx.getColumn("zwppxx").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                </tr>
            <%}%>
            </table>
            <script language="javascript">initDefaultTableRow('tableview3',1);</script>
      </center>
</div>

  <SCRIPT LANGUAGE="javascript">INFO_EX = new TabControl('INFO_EX',0);
      AddTabItem(INFO_EX,'INFO_EX_0','tabDivINFO_EX_0','cntDivINFO_EX_0');
      AddTabItem(INFO_EX,'INFO_EX_1','tabDivINFO_EX_1','cntDivINFO_EX_1');
      AddTabItem(INFO_EX,'INFO_EX_2','tabDivINFO_EX_2','cntDivINFO_EX_2');
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
             String ad = "sumitForm("+Operate.POST_CONTINUE+",-1)";
           %>
          <%if(isCanEdit)%>
          <input name="button2" type="button" title = "保存添加" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value='保存添加(S)'><pc:shortcut key="n" script='<%=ad%>'/><%}%>
           <%if(loginBean.hasLimits(pageCode,op_add)){ String save = "sumitForm("+Operate.POST+",-1)";%>
          <%if(isCanEdit)%><input name="button" type="button" title = "保存返回" class="button" onClick="sumitForm(<%=Operate.POST%>);" value='保存返回(S)'><pc:shortcut key="s" script='<%=save%>'/><%}%>
          <%if(loginBean.hasLimits(pageCode,op_add)){
             String del = "sumitForm("+B_ApplyJobBean.DELETE_RETURN+",-1)";
          %>
          <%if(isCanDelete)%><input name="btnback2" type="button" title = "删除" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=B_ApplyJobBean.DELETE_RETURN%>,<%=request.getParameter("rownum")%>)" value='  删除(D)  '><pc:shortcut key="d" script='<%=del%>'/><%}%>
           <%if(loginBean.hasLimits(pageCode,op_add)){
             String f = "backList()";
           %>
          <input name="btnback" type="button" title = "返回" class="button" onClick="backList();" value='  返回(C)  '><pc:shortcut key="c" script='<%=f%>'/><%}%>
        </td>
      </tr>
    </table>
</form>
<%out.print(retu);%>
</body>
</html></font></font>

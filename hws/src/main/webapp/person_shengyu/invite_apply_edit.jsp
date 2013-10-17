 <%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
 <%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
 <%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.shengyu.*"%>
 <%@ page import="java.util.*"%>
 <%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "invite_apply";
 %>
 <html>
 <head>
 <title></title>
 <META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
 <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 <link rel="stylesheet" href="../scripts/public.css" type="text/css">
 </head>
 <%if(!loginBean.hasLimits("invite_apply", request, response))
  return;
 B_InviteApply b_InviteApplyBean = B_InviteApply.getInstance(request);
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
   location.href='../person_shengyu/invite_apply.jsp';
 }
 </script>
 <BODY oncontextmenu="window.event.returnValue=true">
 <table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
  </tr>
 </table>

 <%
   String retu = b_InviteApplyBean.doService(request, response);
 if(retu.indexOf("location")>-1)
 {
   out.print(retu);
 }

 String curUrl = request.getRequestURL().toString();
 RowMap m_RowInfo = b_InviteApplyBean.getMasterRowinfo();   //行到主表的一行信息
 String s = m_RowInfo.get("personid");

 //RowMap[] pxkcRows= b_InviteApplyBean.getCcsqRowinfos();//从表的多行信息
  EngineDataSet ds = b_InviteApplyBean.getMaterTable();
  EngineDataSet dsDWTX_LXR = null;//b_InviteApplyBean.getDetailTable();


  if(b_InviteApplyBean.isApprove)
  {
    //corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
    //storeBean.regData(ds, "storeid");
    deptBean.regData(ds, "deptid");
    //corpBean.regData(ds, "dwt_dwtxid");
  }
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  String state=m_RowInfo.get("state");
  boolean isEnd =state.equals("1")||state.equals("9")||state.equals("8");
  boolean isCanEdit =!isEnd&&loginBean.hasLimits(pageCode, op_edit)||b_InviteApplyBean.masterIsAdd();
  boolean isCanDelete =!isEnd&&(loginBean.hasLimits(pageCode, op_delete))||b_InviteApplyBean.masterIsAdd();
  String typeClass = (isCanEdit)?"class=edFocused": "class=edline";
  String readonly = (isCanEdit)?"":"readonly";
  String detailClass = (isCanEdit) ? "class=ednone" : "class=edFocused";
  String detailClass_r = (isCanEdit) ? "class=edFocused_r" : "class=ednone_r";
%>
  <form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
  <table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
  <td width="400"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
  <td  class="activeVTab">招聘申请</td>
  </tr>
  </table>
  <table class="editformbox" cellspacing=1 cellpadding=0 width="100%">
  <tr>
   <td>
   <table cellspacing="4" cellpadding="1" border="0" width="100%" bgcolor="#f0f0f0">
<%--<%personBean.regConditionData(ds, "personid");%>--%>
              <tr>
              <%--<td nowrap class="tdTitle">姓名</td>
              <td nowrap class="td"><input type="text" name="emp_name" value='<%=m_RowInfo.get("emp_name")%>' maxlength='<%=ds.getColumn("emp_name").getPrecision()%>' style="width:120" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>--%>
      <td nowrap  class="tdTitle">申请单编号</td>
      <td noWrap class="td"><input type="text" name="apply_code" value='<%=m_RowInfo.get("apply_code")%>' maxlength='<%=ds.getColumn("apply_code").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>

      <td nowrap class="tdTitle">申请部门</td>
<%-- <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>--%>
      <td noWrap class="td">
      <%String onChange = "if(form1.deptid.value!='"+m_RowInfo.get("deptid")+"')sumitForm("+b_InviteApplyBean.DEPT_CHANGE+")";%>
      <%if(!isCanEdit) out.print("<input type='text' value='"+deptBean.getLookupName(m_RowInfo.get("deptid"))+"' style='width:110' class='edline' readonly>");
      else {%>
      <pc:select  name="deptid" addNull="1" style="width:111" onSelect="<%=onChange%>"> <%=deptBean.getList(m_RowInfo.get("deptid"))%>
      </pc:select>
      <%}%>
      </td>
      <%personBean.regConditionData(ds, "personid");%>
      <td nowrap class="tdTitle">申请人</td>
      <%--  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>--%>
      <td  noWrap class="td">
      <%if(!isCanEdit) out.print("<input type='text' value='"+personBean.getLookupName(m_RowInfo.get("personid"))+"' style='width:110' class='edline' readonly>");
      else {%>
      <pc:select name="personid" addNull="1" style="width:110">
      <%=personBean.getList(m_RowInfo.get("personid"), "deptid", m_RowInfo.get("deptid"))%>
      </pc:select>
      <%}%>
      </td>
      <td nowrap  colspan="1" class="tdTitle">需求日期</td>
      <%-- <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhrq").getFieldname()%></td>--%>
      <td noWrap class="td"><input type="text" name="need_date" value='<%=m_RowInfo.get("need_date")%>' maxlength='10' style="width:85" <%=typeClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
      <%if(isCanEdit){%>
      <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.need_date);"></a>
       <%}%>
   </tr>
     <tr>
            <td nowrap  class="tdTitle">工作性质</td>
           <td colspan="1" nowrap class="td"> <input type="text" name="job_kind" value='<%=m_RowInfo.get("job_kind")%>' maxlength='64' style="width:80" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%> ></td>
           <td nowrap class="tdTitle">聘用形式</td>
     <td nowrap class="td">
       <%if((isCanEdit)){%>
         <pc:select name="invite_kind"  style="width:110">
       <%=personclassBean.getList(m_RowInfo.get("invite_kind"))%>
    </pc:select>
     <%}else{%>
     <input type="text"  value='<%=personclassBean.getLookupName(m_RowInfo.get("invite_kind"))%>'  style="width:110" class="edline"  readonly>
     <%}%>
     </td>


        <td nowrap class="tdTitle">职位</td>
       <td nowrap class="td">
       <%if((isCanEdit)){%>
         <pc:select name="job" addNull="1" style="width:110">
  <%=dutyBean.getList(m_RowInfo.get("job"))%>
    </pc:select>
     <%}else{%>
     <input type="text"  value='<%=dutyBean.getLookupName(m_RowInfo.get("job"))%>'  style="width:110" class="edline"  readonly>
     <%}%>
     </td>
            <td nowrap  class="tdTitle">人数</td>
           <td colspan="1" nowrap class="td"> <input type="text" name="recruit_num" value='<%=m_RowInfo.get("recruit_num")%>' maxlength='64' style="width:80" <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%> ></td>

  </tr>
     <tr>
             <td nowrap class="tdTitle">性别</td>
     <%String emp_sex = m_RowInfo.get("sex");%>
      <td colspan="1" nowrap class="td">
      男
      <input type="radio" name="sex" value="1" <%=emp_sex.equals("1")?" checked" :""%> checked >
      女
      <input type="radio" name="sex" value="0" <%=emp_sex.equals("0")?" checked" :""%> ></td>
     <td nowrap  class="tdTitle">年龄</td>
           <td colspan="1" nowrap class="td" > <input type="text" name="age" value='<%=m_RowInfo.get("age")%>' maxlength='64' style="width:110" <%=detailClass_r%> onKeyDown="return getNextElement();" <%=readonly%> ></td>


           <td nowrap class="tdTitle">学历</td>
     <td nowrap class="td">
     <%if((isCanEdit)){%>
       <pc:select name="edu_level" style="width:110">
     <%=personEducationBean.getList(m_RowInfo.get("edu_level"))%>
          </pc:select>
     <%}else{%>
     <input type="text"  value='<%=personEducationBean.getLookupName(m_RowInfo.get("edu_level"))%>'  style="width:110" class="edline"  readonly>
     <%}%>
     </td>
     <td nowrap class="tdTitle">职称</td>
     <td nowrap class="td">
       <%if((isCanEdit)){%>
         <pc:select name="emp_title" addNull="1" style="width:131">
     <%=personctechBean.getList(m_RowInfo.get("emp_title"))%>
          </pc:select>
     <%}else{%>
     <input type="text"  value='<%=personctechBean.getLookupName(m_RowInfo.get("emp_title"))%>'  style="width:130" class="edline"  readonly>
     <%}%>
     </td>
     </tr>
     <tr>
     <td nowrap  class="tdTitle">外语</td>
     <td colspan="1" nowrap class="td"> <input type="text" name="foreign_lang" value='<%=m_RowInfo.get("foreign_lang")%>' maxlength='64' style="width:80" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%> ></td>
     <%
    ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");
    ArrayList opval = new ArrayList(); opval.add("不限"); opval.add("未婚"); opval.add("已婚");
    ArrayList[] lists  = new ArrayList[]{opkey, opval};
    String ss=m_RowInfo.get("isMarried");
    %>
     <td nowrap class="tdTitle">婚否</td>
     <td noWrap class="td">
     <%
     //String t="sumitForm("+b_employeeinfoChangeBean.BDLX_CHANGE+",-1)";
       int te=0;
            try{
              te=Integer.parseInt(ss)-1;
              }catch(Exception e){te=0;}
     if(!isCanEdit){%><input type="HIDDEN" name="isMarried" class="edbox" value="<%=opkey.get(te)%>"><%out.print(opval.get(te));}else{%>
       <pc:select name="isMarried"  style="width:110">  <%--onSelect="<%=t%>">--%>
     <%=b_InviteApplyBean.listToOption(lists, opkey.indexOf(m_RowInfo.get("isMarried")))%>
         </pc:select>
    <%}%>




     </td>
 <tr>
     <td nowrap class="tdTitle">经历</td>
     <td class="td" colspan="9" align="left"><textarea  name="work_story" cols="107" rows="3" <%=readonly%>><%=m_RowInfo.get("work_story")%></textarea></td>
     </tr>
 <tr>
     <td nowrap class="tdTitle">具备技能</td>
     <td class="td" colspan="9" align="left"><textarea  name="good_thing" cols="107" rows="3" <%=readonly%>><%=m_RowInfo.get("good_thing")%></textarea></td>
     </tr>
 <tr>
                   <td class="tdtitle" nowrap>增加人员<br>
                    工作内容</td>
  <%--   <td nowrap class="tdTitle">增加人员<p>工作内容</td>--%>
     <td class="td" colspan="9" align="left"><textarea  name="work_content" cols="107" rows="3" <%=readonly%>><%=m_RowInfo.get("work_content")%></textarea></td>
     </tr>
 <tr>
     <td nowrap class="tdTitle">备 注</td>
     <td class="td" colspan="9" align="left"><textarea  name="memo" cols="107" rows="3" <%=readonly%>><%=m_RowInfo.get("memo")%></textarea></td>
     </tr>
</tr>
      <tr>

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
        <td class="td" colspan="2"><b>登记日期:</b><%=m_RowInfo.get("createDate")%></td>
        <td class="td"></td>
        <td class="td"  colspan="0" align="right"><b>制单人:</b><%=m_RowInfo.get("creator")%></td>
      </tr>



  <tr>
        <td colspan="3" class="td" align="center">
  <%--<%if(loginBean.hasLimits(pageCode, op_add)&&b_InviteJobBean.masterIsAdd()){%>--%>
          <%if(loginBean.hasLimits(pageCode, op_add)&&isCanEdit){%>
          <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value='保存新增(N)'>
          <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
          <%}if(isCanEdit){%>


          <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value='保存返回(S)'>
          <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
          <%}if(isCanDelete){%>
          <input name="btnback2" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=b_InviteApplyBean.DELETE_RETURN%>,<%=request.getParameter("rownum")%>)" value='  删除  '>
          <%}%>
          <input name="btnback" type="button" class="button" onClick="backList();" value='  返回(C)  '>
          <pc:shortcut key="c" script='<%="backList();"%>'/>
        </td>
      </tr>

    </table>
</form>
<%//&#$
if(b_InviteApplyBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</body>
</html>

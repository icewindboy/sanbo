<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
    <%@ page import="engine.dataset.EngineDataSet,java.math.BigDecimal,engine.dataset.RowMap"%>

    <%@ page import="engine.action.Operate,engine.common.LoginBean,engine.html.*,java.util.ArrayList,engine.erp.baseinfo.*,engine.project.*,engine.erp.person.shengyu.*"%>
    <%@ page import="java.util.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_approve ="op_approve";
  String pageCode = "invite_job";
  if(!loginBean.hasLimits("invite_job", request, response))
    return;
  B_InviteJob b_InviteJobBean = B_InviteJob.getInstance(request);
  String retu = b_InviteJobBean.doService(request, response);
  LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT_BOTH);//BEAN_DEPT_LIST
  LookUp MdeptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT_BOTH);//BEAN_DEPT_LIST
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  LookUp dutyBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_DUTY);
  LookUp personclassBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_CLASS);
  LookUp personEducationBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_EDUCATION);
  LookUp personctechBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON_TECH);
  LookUp kkBean = LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON_APPLY);//招聘计划引招聘申请的bean
  EngineDataSet ds = b_InviteJobBean.getMaterTable();
  EngineDataSet list = b_InviteJobBean.getDetailTable();

  //HtmlTableProducer masterProducer = b_InviteJobBean.masterProducer;
  //HtmlTableProducer detailProducer = b_InviteJobBean.detailProducer;
  RowMap[] detailrowinfos= b_InviteJobBean.getDetailRowinfos();//从表的一行信息
  RowMap masterRow = b_InviteJobBean.getMasterRowinfo();
  String deptid = masterRow.get("deptid");
  String creatorID = masterRow.get("creatorID");
  boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, creatorID);//判断登陆员工是否有操作改制单人单据的权限

  if(b_InviteJobBean.isApprove)
  {
    personBean.regData(ds, "personid");
    deptBean.regData(ds, "deptid");
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
function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
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
    PersonSingleSelect('form1','srcVar=personid','fieldVar=personid','undefined',methodName='sumitForm(<%=b_InviteJobBean.PERSON_ONCHANGE%>)');
      }
      function backList()
      {
        location.href='invite_job.jsp';
      }
      function sl_onchange(i, isBigUnit)
      {
        var slObj = document.all['recruit_num_'+i];
        if(slObj.value=="")
          return;
        if(isNaN(slObj.value)){
          alert("输入的数量非法");
          slObj.focus();
          return;
        }
        if(slObj.value<0){
          alert("不能输入小于等于零的数")
              return;
        }
        cal_tot('recruit_num');
      }
      function cal_tot(type)
      {
        var tmpObj;
        var tot=0;
  for(i=0; i<<%=detailrowinfos.length%>; i++)
    {
    if(type == 'recruit_num')
      tmpObj = document.all['recruit_num_'+i];

    if(tmpObj.value!="" && !isNaN(tmpObj.value))
      tot += parseFloat(tmpObj.value);
  }
  if(type == 'recruit_num')
    document.all['t_recruit_num'].value = tot;

      }

      //引招聘申请
      function ImportMrpSelect(frmName, srcVar, methodName,notin)
      {
        var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
        var winName= "MrpGoodsSelector";
        paraStr = "../person_shengyu/invite_apply_edit2.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
        if(methodName+'' != 'undefined')
          paraStr += "&method="+methodName;
        if(notin+'' != 'undefined')
          paraStr += "&notin="+notin;
        newWin =window.open(paraStr,winName,winopt);
        newWin.focus();
      }

      /**  function ImportZpApply()
      {

        if(form1.deptid.value==''){alert('请选择部门'); return;}
        ApplyMultiSelect('form1','srcVar=ImportZpApply&deptid='+form1.deptid.value);
      }


      function ApplyMultiSelect(frmName, srcVar, methodName,notin)
      {
        var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
        var winName= "GoodsProdSelector";
        paraStr = "../person_shengyu/invite_apply_edit2.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
        if(methodName+'' != 'undefined')
          paraStr += "&method="+methodName;
        if(notin+'' != 'undefined')
          paraStr += "&notin="+notin;
        newWin =window.open(paraStr,winName,winopt);
        newWin.focus();
      }

      */
     </script>
  <%
    if(retu.indexOf("backList();")>-1)
    {
      out.print(retu);
      return;
    }
    String curUrl = request.getRequestURL().toString();
    RowMap m_RowInfo = b_InviteJobBean.getMasterRowinfo();   //行到主表的一行信息
    String state=m_RowInfo.get("state");
    boolean isEnd =state.equals("1")||state.equals("9");  //b_InviteJobBean.isApprove || (!b_InviteJobBean.masterIsAdd() && !state.equals("0"));
    //isEnd = isEnd || !(b_InviteJobBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
    boolean canedit = !isEnd&&loginBean.hasLimits(pageCode, op_edit)||b_InviteJobBean.masterIsAdd()||b_InviteJobBean.state.equals("0");
    boolean canDel = !isEnd&&loginBean.hasLimits(pageCode, op_delete)||b_InviteJobBean.masterIsAdd();
    //FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
    String edClass = canedit?"class=edbox": "class=edline";
    String detailClass = canedit ?"class=edFocused": "class=ednone";
    String detailClass_r = canedit ? "class=edFocused_r": "class=ednone_r" ;
    String readonly = canedit ?"": "readonly" ;
   %>
   <BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
   <iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
   <form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
   <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 ><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
  <table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
  <td width="400"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
  <td  class="activeVTab">招聘计划</td>
  </tr>
  </table>
   <table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
  <td>
   <table cellspacing="1" cellpadding="1" border="0"  bgcolor="#f0f0f0">
<%-- <INPUT TYPE="HIDDEN" NAME="personid"  VALUE="<%=m_RowInfo.get("personid")%>">--%>
    <%RowMap personRow = personBean.getLookupRow(m_RowInfo.get("personid"));%>
     <tr>
       <td nowrap  class="tdTitle">计划编号</td>
      <td noWrap class="td"><input type="text" name="plan_code" value='<%=m_RowInfo.get("plan_code")%>' maxlength='<%=ds.getColumn("plan_code").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
    </td>
   </tr>
<tr>
      <td nowrap class="tdTitle">招聘部门</td>
     <td nowrap class="td">
     <%if((canedit)){%>
       <pc:select name="deptid" addNull="1" style="width:131">
     <%=deptBean.getList(m_RowInfo.get("deptid"))%>
    </pc:select>
     <%}else{%>
     <input type="text"  value='<%=deptBean.getLookupName(m_RowInfo.get("deptid"))%>'  style="width:130" class="edline"  readonly>
     <%}%>
     </td>
      <%
        ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");opkey.add("4");
        ArrayList opval = new ArrayList(); opval.add("人才市场"); opval.add("网络招聘"); opval.add("刊登广告");opval.add("其它形式");
        ArrayList[] lists  = new ArrayList[]{opkey, opval};
        String ss=m_RowInfo.get("plan_mode");
      %>
     <td nowrap class="tdTitle">招聘形式</td>
     <td noWrap class="td">
     <%
     //String t="sumitForm("+b_InviteJobBean.BDLX_CHANGE+",-1)";
       int te=0;
      try{
        te=Integer.parseInt(ss)-1;
        }catch(Exception e){te=0;}
     if(!canedit){%><input type="HIDDEN" name="plan_mode" class="ednone" value="<%=opkey.get(te)%>"><%out.print(opval.get(te));}else{%>
       <pc:select name="plan_mode"  style="width:130">  <%--   onSelect="<%=t%>">--%>
     <%=b_InviteJobBean.listToOption(lists, opkey.indexOf(m_RowInfo.get("plan_mode")))%>
                               </pc:select>
    <%}%>
     </td>
      <td nowrap class="tdTitle">招聘日期</td>
     <td nowrap class="td"><input type="text" name="plan_date" value='<%=m_RowInfo.get("plan_date")%>' maxlength="10" style="width:80" <%=edClass%> onChange="checkDate(this)" <%=readonly%>>
     <%if(canedit){%><a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(plan_date);"></a><%}%>
     </td>
   </tr>
   <tr>
     <td colspan="6" nowrap>
     <table cellspacing=0 width="100%" cellpadding=0>
<tr>
     <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" >招聘要求</a></div></td>
     <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
     </tr>
     </table>

     <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:750;height:160;overflow-y:auto;overflow-x:auto;">
                      <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
                      <tr class="tableTitle">
     <%-- <td nowrap width=10 rowspan="2"></td>--%>
                      <td height='20' align="center" rowspan="2" nowrap>
                      <%if(!isEnd && isHasDeptLimit){%>
                      <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
                      <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%>
                      </td>
                      <td height='20' rowspan="2" nowrap>申请单编号</td>
                      <td height='20' rowspan="2" nowrap>申请部门</td>
                      <td height='20' rowspan="2" nowrap>申请人</td>
                      <td height='20' rowspan="2" nowrap>工作性质</td>
                      <td nowrap colspan="6">聘 雇 条 件</td>
                      <td height='20'rowspan="2" nowrap>聘用形式</td>
                      <td height='20'rowspan="2" nowrap>人数</td>
                      <td height='20' rowspan="2" nowrap>备注</td>
                        </tr>
                          <tr class="tableTitle">
                          <td>性   别</td>
                          <td>年 龄</td>
                          <td>学 历</td>
                          <td>职称</td>
                          <td >外 语</td>
                          <td >婚 否</td>
                        </tr>
                         <%
                           deptBean.regData(list,"deptid");
                           kkBean.regData(list,"apply_id");
     personBean.regData(list, "personid");
     BigDecimal t_recruit_num = new BigDecimal(0);
     //String t_recruit_num=new Integer(0);

     int i = 0;
     RowMap detail = null;
     int a = detailrowinfos.length;
     for(; i<a;i++){
       detail = detailrowinfos[i];
       String bdeptid = "bdeptid_"+i;
       String edu_level = "edu_level_"+i;
       String personid = "personid_"+i;
       String isMarried = "isMarried_"+i;
       String invite_kind = "invite_kind_"+i;
       String emp_sex = "emp_sex_"+i;
       String emp_title = "emp_title_"+i;
       String recruit_num = detail.get("recruit_num");
       String apply_ID=detail.get("apply_ID");
       boolean isimport = !apply_ID.equals("");//引入招聘申请单，从表当前行不能修改
       if(b_InviteJobBean.isDouble(recruit_num))
       t_recruit_num = t_recruit_num.add(new BigDecimal(recruit_num));
       RowMap  kkRow= kkBean.getLookupRow(detail.get("apply_id"));
      // t_recruit_num = t_recruit_num.recruit_num;


        %>
      <tr id="rowinfo_<%=i%>">

      <td height='20' nowrap>
         <%if(!isEnd && isHasDeptLimit){%>
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
        <%}%>
      </td>
      <td class="td" nowrap><%=kkRow.get("apply_code")%></td>
      <%-- <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>--%>
      <td noWrap class="td">
      <%String onChange = "if(form1."+bdeptid+".value!='"+detail.get("deptid")+"')sumitForm("+b_InviteJobBean.DEPT_CHANGE+")";%>
      <%if(!canedit) out.print("<input type='text' value='"+deptBean.getLookupName(detail.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else{%>
                    <pc:select  name="<%=bdeptid%>" addNull="1" style="width:111" onSelect="<%=onChange%>"> <%=deptBean.getList(detail.get("deptid"))%>
                        </pc:select>
                    <%}%>

                  </td>
                   <%personBean.regConditionData(list, "personid");%>

                    <%--  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>--%>
                  <td  noWrap class="td">
                  <%if(!canedit) out.print("<input type='text' value='"+personBean.getLookupName(detail.get("personid"))+"' style='width:110' class='edline' readonly>");
                  else{%>
                    <pc:select name="<%=personid%>" addNull="1" style="width:110">
                       <%=personBean.getList(detail.get("personid"), "deptid", detail.get("deptid"))%>
                      </pc:select>
                    <%}%>
                   </td>
                <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="job_kind_<%=i%>" id="job_kind_<%=i%>" value='<%=detail.get("job_kind")%>' maxlength='<%=list.getColumn("job_kind").getPrecision()%>'<%=readonly%>></td>


                <td noWrap class="td">
                <%String  emp_sexc=detail.get("emp_sex");
                String aa=detail.get("emp_sex").equals("0")? "女" : "男";
               %>
                <%if(!canedit) out.print("<input type='text' value='"+aa+"' style='width:50' class='edline' readonly>");
                 else {%>
                  <pc:select name="<%=emp_sex%>" style="width:50" value='<%=emp_sexc%>'>
                  <pc:option value='1'>男</pc:option>
                  <pc:option value='0'>女</pc:option>
                      </pc:select>
                    <%}%>
                  </td>
                  <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" name="age_<%=i%>" id="age_<%=i%>" value='<%=detail.get("age")%>' maxlength='<%=list.getColumn("age").getPrecision()%>'<%=readonly%>></td>
                   <td nowrap class="td">
                  <%if((canedit)){%>
                  <pc:select  name="<%=edu_level%>"  style="width:131">
                   <%=personEducationBean.getList(detail.get("edu_level"))%>
                       </pc:select>
                   <%}else{%>
                   <input type="text"  value='<%=personEducationBean.getLookupName(detail.get("edu_level"))%>'  style="width:130" class="edline"  readonly>
                     <%}%>
                  </td>

                  <%--<td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="age_<%=i%>" id="age_<%=i%>" value='<%=detail.get("age")%>' maxlength='<%=list.getColumn("age").getPrecision()%>'<%=readonly%>></td>
                   <td nowrap class="td">
                  <%if((canedit)){%>
                  <pc:select  name="<%=edu_level%>"  style="width:131">
                   <%=personEducationBean.getList(detail.get("edu_level"))%>
                               </pc:select>
                   <%}else{if(!isimport){%>
                   <input type="text"  value='<%=personEducationBean.getLookupName(detail.get("edu_level"))%>'  style="width:130" class="edline"  readonly>
                     <%}else{%>
                     <pc:select addNull="0" name="<%=edu_level%>" style="width:131">
                   <%=personEducationBean.getList(detail.get("edu_level"))%>
                   </pc:select>

                     <%}}%>
                  </td>--%>
               <td nowrap class="td">
       <%if(canedit){%>
       <pc:select  name="<%=emp_title%>"  style="width:131">
       <%=personctechBean.getList(detail.get("emp_title"))%>
                      </pc:select>
                 <%}else{%>
                 <input type="text"  value='<%=personctechBean.getLookupName(detail.get("emp_title"))%>'  style="width:130" class="edline"  readonly>
                     <%}%>
                  </td>
                  <%--      <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="emp_title_<%=i%>" id="emp_title_<%=i%>" value='<%=detail.get("emp_title")%>' maxlength='<%=list.getColumn("emp_title").getPrecision()%>'<%=readonly%>></td>--%>
                  <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="foreign_lang_<%=i%>" id="foreign_lang_<%=i%>" value='<%=detail.get("foreign_lang")%>' maxlength='<%=list.getColumn("foreign_lang").getPrecision()%>'<%=readonly%>></td>
                  <%
                    ArrayList bopkey = new ArrayList(); bopkey.add("1"); bopkey.add("2"); bopkey.add("3");
                  ArrayList bopval = new ArrayList(); bopval.add("不限"); bopval.add("未婚"); bopval.add("已婚");
                  ArrayList[] blists  = new ArrayList[]{bopkey, bopval};
                  String bss=detail.get("isMarried");
                    %>
                  <td noWrap class="td">
                  <%
                    int bte=0;
                    try{
                      bte=Integer.parseInt(bss)-1;
                      }catch(Exception e){bte=0;}
                 if((!canedit)){%><input type="HIDDEN" name="isMarried" class="edbox" value="<%=bopkey.get(bte)%>"><%out.print("<input type='none' class=ednone value='"+bopval.get(bte)+"' style='width:90'  readonly>");}else{%>
                 <%-- if((!canedit)&&!isimport){%><input type="HIDDEN" name="isMarried" class="edbox" value="<%=bopkey.get(bte)%>"><%out.print(bopval.get(bte));}else{%>--%>
                 <pc:select name="<%=isMarried%>"  style="width:130">  <%--onSelect="<%=t%>">--%>
                 <%=b_InviteJobBean.listToOption(blists, bopkey.indexOf(detail.get("isMarried")))%>
                     </pc:select>
    <%}%>
     </td>
     <td nowrap class="td">
       <%if(canedit){%>
         <pc:select addNull="0" name="<%=invite_kind%>"  style="width:110">
       <%=personclassBean.getList(detail.get("invite_kind"))%>
                     </pc:select>
     <%}else{%>
     <input type="text"  value='<%=personclassBean.getLookupName(m_RowInfo.get("invite_kind"))%>'  style="width:110" class="edline"  readonly>
     <%}%>
     </td>

     <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" name="recruit_num_<%=i%>" id="recruit_num_<%=i%>" value='<%=detail.get("recruit_num")%>' maxlength='<%=list.getColumn("recruit_num").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
     <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="memo_<%=i%>" id="memo_<%=i%>" value='<%=detail.get("memo")%>' maxlength='<%=list.getColumn("memo").getPrecision()%>'<%=readonly%>></td>

      </tr>
       <%
       list.next();
     }
     for(; i < 4; i++){
                   %>
    <tr id="rowinfo_<%=i%>">
    <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
    </tr>
    <%}%>
    <tr id="rowinfo_end">
    <td class="tdTitle" nowrap>合计</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <td class="td">&nbsp;</td>
    <%--   <td align="center" class="td"><input id="t_recruit_num" name="t_recruit_num" type="text" class="ednone_r" style="width:100%" value='<%=engine.util.Format.formatNumber(t_recruit_num.toString(),loginBean.getQtyFormat())%>' readonly></td>--%>
    <td align="center" class="td"><input id="t_recruit_num" name="t_recruit_num" type="text" class="ednone_r" style="width:100%" value='<%=t_recruit_num%>' readonly></td>
    <td class="td">&nbsp;</td>
 </tr>
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
     <td nowrap class="tdTitle">备注</td>
     <td class="td" colspan="0" align="center"><textarea  name="memo" cols="105" rows="3" <%=readonly%>><%=m_RowInfo.get("memo")%></textarea></td>
     </tr>
      <tr>
        <td class="td" colspan="2"><b>登记日期:</b><%=m_RowInfo.get("createDate")%></td>
        <td class="td"></td>
        <td class="td"  colspan="0" align="right"><b>制单人:</b><%=m_RowInfo.get("creator")%></td>
      </tr>
      <tr>
        <td colspan="3" class="td" align="center">
                   <%--<%if(loginBean.hasLimits(pageCode, op_add)&&b_InviteJobBean.masterIsAdd()){%>--%>
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
                     <%if(canedit){%><input type="hidden" name="mutiimportmrp" id="mutiimportmrp" value="" onchange="sumitForm(<%=b_InviteJobBean.DETAIL_APPLY_ADD%>)">
             <input name="btnback" class="button" type="button" value="引入申请单(W)" style="width:100" onClick="ImportMrpSelect('form1','srcVar=mutiimportmrp')" border="0">
               <pc:shortcut key="w" script="ImportMrpSelect('form1','srcVar=mutiimportmrp')"/>
             <%}%>

        </td>
      </tr>
    </table>
</form>
<%//&#$
if(b_InviteJobBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</body>
</html>

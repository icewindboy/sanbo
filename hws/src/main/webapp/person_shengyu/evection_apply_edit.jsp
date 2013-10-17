<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.shengyu.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "evection_apply";
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("evection_apply", request, response))
  return;
B_EvectionApply b_EvectionApplyBean = B_EvectionApply.getInstance(request);
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
  location.href='../person_shengyu/evection_apply.jsp';
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
  </tr>
</table>
<%
  String retu = b_EvectionApplyBean.doService(request, response);
if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
{
  out.print(retu);
  return;
}
String curUrl = request.getRequestURL().toString();
RowMap m_RowInfo = b_EvectionApplyBean.getMasterRowinfo();   //行到主表的一行信息
String s = m_RowInfo.get("personid");

RowMap[] pxkcRows= b_EvectionApplyBean.getCcsqRowinfos();//从表的多行信息


EngineDataSet ds = b_EvectionApplyBean.getMaterTable();
EngineDataSet dsDWTX_LXR = null;//b_EvectionApplyBean.getDetailTable();


if(b_EvectionApplyBean.isApprove)
{
  //corpBean.regData(ds, "dwtxid");
  personBean.regData(ds, "personid");
  //storeBean.regData(ds, "storeid");
  deptBean.regData(ds, "deptid");
  //corpBean.regData(ds, "dwt_dwtxid");
}

EngineDataSet dsrl_pxkc = b_EvectionApplyBean.getCcsqTable();

ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3");opkey.add("4");opkey.add("5");
ArrayList opval = new ArrayList(); opval.add("职员离职"); opval.add("部门调动"); opval.add("类别变动");opval.add("职务变迁");opval.add("职员复职");
ArrayList[] lists  = new ArrayList[]{opkey, opval};


boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit)||b_EvectionApplyBean.masterIsAdd();
boolean isCanDelete =(loginBean.hasLimits(pageCode, op_delete))||b_EvectionApplyBean.masterIsAdd();
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
  <td  class="activeVTab">出差申请</td>
  </tr>
</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
  <td>
<table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">
   <%personBean.regConditionData(ds, "personid");%>
              <tr>
                  <td nowrap class="tdTitle">姓名</td>
<%--  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>--%>
                  <td  noWrap class="td">
                  <%if(!isCanEdit) out.print("<input type='text' value='"+personBean.getLookupName(m_RowInfo.get("personid"))+"' style='width:110' class='edline' readonly>");
                  else {%>
                    <pc:select name="personid" addNull="1" style="width:110">
                       <%=personBean.getList(m_RowInfo.get("personid"), "deptid", m_RowInfo.get("deptid"))%>
                      </pc:select>
                    <%}%>
                   </td>
                    <td nowrap class="tdTitle">部门</td>
                  <%-- <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>--%>
                    <td noWrap class="td">
                    <%String onChange = "if(form1.deptid.value!='"+m_RowInfo.get("deptid")+"')sumitForm("+b_EvectionApplyBean.DEPT_CHANGE+")";%>
                    <%if(!isCanEdit) out.print("<input type='text' value='"+deptBean.getLookupName(m_RowInfo.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:111" onSelect="<%=onChange%>"> <%=deptBean.getList(m_RowInfo.get("deptid"))%>
                        </pc:select>
                    <%}%>
                  </td>
                   <td nowrap class="tdTitle">职务</td>
                   <td nowrap class="td">
                   <%if((isCanEdit)){%>
                     <pc:select name="duty" addNull="1" style="width:131">
                   <%=dutyBean.getList(m_RowInfo.get("duty"))%>
                        </pc:select>
                   <%}else{%>
                    <input type="text"  value='<%=dutyBean.getLookupName(m_RowInfo.get("duty"))%>'  style="width:130" class="edline"  readonly>
                     <%}%>
                  <td>
                    </tr>
     <tr>
       <td nowrap class="tdTitle">代理人</td>
      <td nowrap class="td"><input type="text" name="deputy" value='<%=m_RowInfo.get("deputy")%>' maxlength='<%=ds.getColumn("deputy").getPrecision()%>' style="width:120" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
       <td nowrap class="tdTitle">出差地点</td>
       <td nowrap class="td"><input type="text" name="evection_addr" value='<%=m_RowInfo.get("evection_addr")%>' maxlength="16" style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>

       <td nowrap class="tdTitle">旅费预算</td>
       <td nowrap class="td"><input type="text" name="fee_budget" value='<%=m_RowInfo.get("fee_budget")%>' maxlength='<%=ds.getColumn("fee_budget").getPrecision()%>' style="width:120" class=edFocused_r onKeyDown="return getNextElement();" <%=readonly%>></td>
     </tr>
     <tr>
       <td nowrap class="tdTitle">预支旅费</td>
       <td  nowrap class="td"><input type="text" name="fee_prepay" value='<%=m_RowInfo.get("fee_prepay")%>' maxlength='<%=ds.getColumn("fee_prepay").getPrecision()%>' style="width:120" class=edFocused_r onKeyDown="return getNextElement();" <%=readonly%>></td>
                          <td nowrap class="tdTitle">出差开始时间</td>
                                 <%-- <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhrq").getFieldname()%></td>--%>
                  <td noWrap class="td"><input type="text" name="evection_start" value='<%=m_RowInfo.get("evection_start")%>' maxlength='10' style="width:85" class="edbox" onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                  <%if(isCanEdit){%>
                  <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.evection_start);"></a>
       <%}%>
                  </td>
                                <td nowrap class="tdTitle">出差结束时间</td>
                                 <%-- <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhrq").getFieldname()%></td>--%>
                  <td noWrap class="td"><input type="text" name="evection_end" value='<%=m_RowInfo.get("evection_end")%>' maxlength='10' style="width:85" class="edbox" onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                  <%if(isCanEdit){%>
                  <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.evection_end);"></a>
       <%}%>
                  </td>

     </tr>
     <tr>



                  <td nowrap class="tdTitle">出差原因</td>
       <td nowrap class="td"><input type="text" name="evection_reason" value='<%=m_RowInfo.get("evection_reason")%>' maxlength='<%=ds.getColumn("evection_reason").getPrecision()%>' style="width:130" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
            <%--  <td class="td" nowrap><input class="edbox" id="evection_start$a" style="WIDTH: 130px" name="evection_start$a" value='<%=b_EvectionApplyBean.getFixedQueryValue("evection_start$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><img title=选择日期 onClick="selectDate(evection_start$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></td>
              <td class="td" nowrap align="center">--</td>
              <TD class="td" nowrap><input class="edbox" id="evection_end$b" style="WIDTH: 130px" name="evection_end$b" value='<%=b_EvectionApplyBean.getFixedQueryValue("evection_end$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><img title=选择日期 onClick="selectDate(evection_end$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></td>--%>
     </tr>
    <tr>
     <td colspan="6" nowrap>
     <table cellspacing=0 width="100%" cellpadding=0>
     <tr>
         <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" onClick="blur();SetActiveTab(INFO_EX,'INFO_EX_0');return false;">行程计划</a></div></td>
         <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
     </tr>
     </table>
     <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:750;height:110;overflow-y:auto;overflow-x:auto;">
     <table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
     <tr class="tableTitle">
          <td nowrap width=50>
          <%if(loginBean.hasLimits(pageCode,op_add)){
            String add = "sumitForm("+b_EvectionApplyBean.PXKC_ADD+",-1)";
          %>
          <%if(isCanEdit)%>
          <input name="image" class="img"  type="image" title="新增(A)" onClick="sumitForm(<%=b_EvectionApplyBean.PXKC_ADD%>,-1)" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='<%=add%>'/>
          <%}%>
           </td>
           <td>行程</td>
</tr>
     <%
       RowMap pxkcdetail = null;
          for(int i=0; i<pxkcRows.length; i++)
          {
            pxkcdetail = pxkcRows[i];
     %>
            <tr>
                <td class="td" align="center">
                <%if(isCanDelete){%><input name="image32" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_EvectionApplyBean.PXKC_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle"><%}%>
                </td>
                <%--  <td class="td"  align="right"><INPUT TYPE="TEXT" NAME="kcmc_<%=i%>" VALUE="<%=pxkcdetail.get("kcmc")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("kcmc").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>--%>
                <%--  <td class="td" align="right"><INPUT TYPE="TEXT" NAME="pxjs_<%=i%>" VALUE="<%=pxkcdetail.get("pxjs")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("pxjs").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>--%>
                <%--  <td class="td" align="right"><INPUT TYPE="TEXT" NAME="pxjc_<%=i%>" VALUE="<%=pxkcdetail.get("pxjc")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("pxjc").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>--%>
                <%--<td class="td" align="right">
                <INPUT TYPE="TEXT" NAME="pxsj_<%=i%>" VALUE="<%=pxkcdetail.get("pxsj")%>" style="width:80" MAXLENGTH="10" <%=typeClass%>  onChange="checkDate(this)" <%=readonly%>>
                <%if((isCanEdit)){%><a href="#"><img src="../images/seldate.gif" width="23" height="16" border="0" title="选择日期" onClick="selectDate(pxsj_<%=i%>);">
                </a><%}%>
                </td>--%>
               <td class="td" align="right"><INPUT TYPE="TEXT" NAME="routing_<%=i%>" VALUE="<%=pxkcdetail.get("routing")%>" style="width:688" MAXLENGTH="<%=dsrl_pxkc.getColumn("routing").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                <%-- <td class="td" align="right"><INPUT TYPE="TEXT" NAME="kcfy_<%=i%>" VALUE="<%=pxkcdetail.get("kcfy")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("kcfy").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>--%>
                <%-- <td class="td" align="right"><INPUT TYPE="TEXT" NAME="zxfy_<%=i%>" VALUE="<%=pxkcdetail.get("zxfy")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("zxfy").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>--%>
                <%-- <td class="td" align="right"><INPUT TYPE="TEXT" NAME="qtfy_<%=i%>" VALUE="<%=pxkcdetail.get("qtfy")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("qtfy").getPrecision()%>" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>--%>
           </tr>
      <%}%>
      </table>
      <script language="javascript">initDefaultTableRow('tableview1',1);</script>
      </div>
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
            String del = "sumitForm("+b_EvectionApplyBean.DELETE_RETURN+",-1)";
           %>
          <%if(isCanDelete)%><input name="btnback2" type="button" class="button" title = "删除"  onClick="if(confirm('是否删除该记录？'))sumitForm(<%=b_EvectionApplyBean.DELETE_RETURN%>,<%=request.getParameter("rownum")%>)" value='  删除(D)  '><pc:shortcut key="d" script='<%=del%>'/><%}%>
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
</html></font></font>
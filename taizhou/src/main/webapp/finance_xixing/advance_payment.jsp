<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>

<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "advance_payment";
  String op_over = "op_over";
  String op_cancel   = "op_cancel";
%>
<%if(!loginBean.hasLimits("advance_payment", request, response))
  return;
engine.erp.finance.xixing.B_AdvancePayment paymentBean=engine.erp.finance.xixing.B_AdvancePayment.getInstance(request);

engine.project.LookUp currencyBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);//货币LOOKUP
engine.project.LookUp companyBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);//往来单位LOOKUP
engine.project.LookUp DEPTBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_DEPT);//部门LOOKUP
engine.project.LookUp balanceBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_BALANCE_MODE);//结算方式
engine.project.LookUp personBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PERSON);//人员
engine.project.LookUp bankBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_BANK);//往来单位LOOKUP
//engine.common.b_a
String SYS_APPROVE_ONLY_SELF =paymentBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
boolean isApproveOnly = SYS_APPROVE_ONLY_SELF.equals("1") ? true : false;//true仅制单人可提交
String loginId = paymentBean.loginId;
String retu = paymentBean.doService(request, response);
if(retu.indexOf("location.href=")>-1)
  return;
// System.out.print(retu+"aaaaaaaaaaaaaa");
%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript" src="../scripts/frame.js"></script>
<SCRIPT LANGUAGE="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function showInterFrame(oper, rownum)
{
  location.href="advance_payment_edit.jsp?operate="+oper+"&rownum="+rownum;
  //var url = "advance_payment_edit.jsp?operate="+oper+"&rownum="+rownum;
  //document.all.interframe1.src = url;
  //showFrame('detailDiv',true,"",true);
}

function hideInterFrame()//隐藏FRAME
{
  lockScreenToWait("处理中, 请稍候！");
  hideFrame('detailDiv');
  form1.submit();
}
function hideFrameNoFresh(){
  hideFrame('detailDiv');
}
function showFixedQuery(){
  showFrame('fixedQuery', true, "", true);
}
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function goBack()
{
    location.href='<%=paymentBean.retuUrl%>';
}
function corpQueryCodeSelect(obj,srcVars)
{
  ProvideCodeChange(document.all['prod'], obj.form.name, srcVars,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc',obj.value);
}
function corpQueryNameSelect(obj,srcVars)
{
  ProvideNameChange(document.all['prod'], obj.form.name, srcVars,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="98%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>采购(加工)预付款</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = paymentBean.getOneTable();
String curUrl = request.getRequestURL().toString();
boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);//op_edit
companyBean.regData(list,"dwtxId");
DEPTBean.regData(list,"deptid");
balanceBean.regData(list,"jsfsid");
personBean.regData(list,"personid");
String state=null;
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="98%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>
     <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
</td> <TD align="right">
<%if(loginBean.hasLimits(pageCode,op_search)){%>
<input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
  <pc:shortcut key="q" script='showFixedQuery()'/>
<%}%>
<%if(paymentBean.retuUrl!=null){%>
<input name="button2222232" type="button" align="Right" class="button" onClick="location.href='<%=paymentBean.retuUrl%>'" value="返回(C)" border="0"><%}%></TD>
  <pc:shortcut key="c" script='goBack();'/>
    </TR>
  </TABLE>
  <table id="tableview1" width="98%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/>
      <td nowrap>往来单位</td>
      <td nowrap>部门</td>
      <td nowrap>结算方式</td>
      <td nowrap>人员</td>
      <td nowrap>预付款编号</td>
      <td nowrap>预付类型</td>
      <td nowrap>预付日期</td>
      <td nowrap>金额</td>
      <td nowrap>帐号</td>
      <td nowrap>银行</td>
      <td nowrap>预付描述</td>
      <td nowrap>状态</td>
      <td nowrap>操作日期</td>
      <td nowrap>操作员</td>
      <td nowrap>状态描述</td>
      <td nowrap>审批人</td>
   </td>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
        boolean isInit = false;
        String zt =list.getValue("zt");
        if(zt.equals("0"))isInit = true;
        String czyid = list.getValue("czyid");
        String sprid = list.getValue("sprid");//审批ID
        boolean isShow = isApproveOnly ? (loginId.equals(czyid) && isInit) : isInit;//如果时只有制单人才可以提交审批。制单人等于登录人才显示
        boolean isCancelApprove =  zt.equals("1");
        isCancelApprove = isCancelApprove && loginId.equals(sprid);//是否可以取消合同
        boolean isComplete  = zt.equals("1") && !zt.equals("8") && loginBean.hasLimits(pageCode, op_over);//有强制完成的权限
        String rowClass = engine.action.BaseAction.getStyleName(zt);
        int intsate=Integer.parseInt(zt);
        switch(intsate){
          case 9:state="审批中";break;
          case 1:state="已审";break;
          default:state="未审";break;
        }
        String isEdit=loginBean.hasLimits(pageCode, op_edit)&&zt.equals("0")?"修改":"查看";
    %>
  <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" onclick="selectRow();">
  <td class="td" nowrap>
     <input name="image2" class="img" type="image" title="<%=isEdit%>" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
    <%--<input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">--%>
     <%if(isShow){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
     <%if(isCancelApprove){%><input name="image3" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=paymentBean.CANCLE_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
    <%--if(isComplete){%><input name="image5" class="img" type="image" title='完成' onClick="if(confirm('是否强制完成该纪录？'))sumitForm(<%=paymentBean.COMPLETE%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}--%>
</td>
    <td  nowrap <%=rowClass%>>&nbsp;<%=companyBean.getLookupName(list.getValue("dwtxid"))%></td><%--往来单位--%>
    <td  nowrap <%=rowClass%>>&nbsp;<%=DEPTBean.getLookupName(list.getValue("deptid"))%></td><%--部门--%>
    <td  nowrap <%=rowClass%>>&nbsp;<%=balanceBean.getLookupName(list.getValue("jsfsid"))%></td><%--结算方式--%>
    <td  nowrap <%=rowClass%>>&nbsp;<%=personBean.getLookupName(list.getValue("personid"))%></td><%--人员--%>
    <td  nowrap <%=rowClass%>>&nbsp;<%=list.getValue("yfkbh")%></td><%--预付款编号--%>
    <td  nowrap <%=rowClass%>>&nbsp;<%=list.getValue("yflx").equals("0") ?"采购预付款":"加工预付款"%></td><%--预付类型--%>
    <td  nowrap <%=rowClass%>>&nbsp;<%=list.getValue("yfrq")%></td><%--预付日期--%>
    <td  nowrap <%=rowClass%>>&nbsp;<%=list.getValue("je")%></td><%--金额--%>
    <td  nowrap <%=rowClass%>>&nbsp;<%=list.getValue("zh")%></td><%--帐号--%>
    <td  nowrap <%=rowClass%>>&nbsp;<%=list.getValue("yh")%></td><%--银行--%>
    <td  nowrap <%=rowClass%>>&nbsp;<%=list.getValue("yfms")%></td><%--预付描述'+<%=list.getValue("cgyfkid")%>+'--%>
    <td  nowrap <%=rowClass%> align='center'>
    <a href='javascript:'onClick="openUrlOpt1('../pub/approve_result.jsp?operate=0&project=advance_payment&id=<%=list.getValue("cgyfkid")%>')">
    <%=state%></a></td><%--审批状态--%>
    <td nowrap <%=rowClass%>>&nbsp;<%=list.getValue("czrq")%></td><%--操作日期--%>
    <td nowrap <%=rowClass%>>&nbsp;<%=list.getValue("czy")%></td><%--操作员--%>
    <td nowrap <%=rowClass%>>&nbsp;<%=list.getValue("ztms")%></td><%--状态描述--%>
    <td nowrap <%=rowClass%>>&nbsp;<%=personBean.getLookupName(list.getValue("sprid"))%></td><%--审批人--%>
  </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap>&nbsp;</td>
    </tr>
    <%}%>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
 <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
     <TD>
      <TABLE cellspacing=3 cellpadding=0 border=0>
        <TR>
         <%paymentBean.table.printWhereInfo(pageContext);%>
            <TR>
              <TD nowrap colspan=3 height=30 align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <INPUT class="button"  onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <INPUT class="button" onClick="hideFrame('fixedQuery');" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
          </TABLE>
        </TD>
      </TR>
    </TABLE>
  </DIV>
  <div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv');refresh();" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="550" height="300" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</form>
<%out.print(retu);%>
</body>
</html>
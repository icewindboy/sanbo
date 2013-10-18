<%@page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*,engine.project.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode  = "wrapper_process_check";
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
  engine.erp.quality.B_ProcessCheck b_ProcessCheckBean = engine.erp.quality.B_ProcessCheck.getInstance(request);
  LookUp productBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT);//产品编码
  LookUp propertyBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_SPEC_PROPERTY);//规格属性
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);//人员
  LookUp workshopBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_WORKSHOP);//车间
%>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript">
function toDetail()
{
   parent.location.href='wrapper_process_check_edit.jsp';
}
function showDetail(mastereditRow){
  //单击主表的一行激活的操作
  selectRow();
  lockScreenToWait("处理中, 请稍候！");
  parent.bottom.location.href='wrapper_process_check_bottom.jsp?operate=<%=b_ProcessCheckBean.SHOW_DETAIL%>&rownum='+mastereditRow;
  unlockScreenWait();
}
function sumitForm(oper, row)
{
  //新增或修改
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
}
function showFixedQuery(){
  //点击查询按钮打开查询对话框
   showFrame('fixedQuery', true, "", true);//显示层
}
function sumitFixedQuery(oper)
{
lockScreenToWait("处理中, 请稍候！");
fixedQueryform.operate.value = oper;
fixedQueryform.submit();
}
function corpQueryCodeSelect(obj,srcVars)
{
  ProvideCodeChange(document.all['prod'], obj.form.name, srcVars,
                    'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpQueryNameSelect(obj,srcVars)
{
  ProvideNameChange(document.all['prod'], obj.form.name, srcVars,
                    'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function productCodeSelect(obj,srcVars)
{
  ProdCodeChange(document.all['prod'], obj.form.name, srcVars,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function productNameSelect(obj,srcVars)
{
  ProdNameChange(document.all['prod'], obj.form.name, srcVars,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function propertyNameSelect(obj,cpid, srcVar)
{
  PropertyNameChange(document.all['prod'], obj.form.name, srcVar,
                     'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
</script>
<%
  if(!loginBean.hasLimits("wrapper_process_check", request, response))
    return;
  String retu = b_ProcessCheckBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
 {
  out.print(retu);
  return;
  }
%>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">镀铝纸生产过程检验</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = b_ProcessCheckBean.getMasterTable();//主表数据集
  String curUrl = request.getRequestURL().toString();
  RowMap m_RowInfo =b_ProcessCheckBean.getMasterRowinfo();

  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);
  boolean isCanSearch=loginBean.hasLimits(pageCode,op_search);
  String SYS_APPROVE_ONLY_SELF =b_ProcessCheckBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  boolean isApproveOnly = SYS_APPROVE_ONLY_SELF.equals("1") ? true : false;//true仅制单人可提交

  propertyBean.regData(list,"dmsxid");
  productBean.regData(list,"cpid");
  personBean.regData(list,"personid");//approverID
  String loginID = b_ProcessCheckBean.loginID;
  String state=null;
%>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
  <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
      int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
         </td> <TD class="td" align="right">
         <input name="search" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)">
         <pc:shortcut key="q" script="showFixedQuery()"/>
        <%if(b_ProcessCheckBean.retuUrl!=null){%><input name="button2" type="button" align="Right"
       class="button" onClick="parent.location.href='<%=b_ProcessCheckBean.retuUrl%>'" value=" 返回 "border="0"><%}%>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr id="tr_0" class="tableTitle">
      <td nowrap>
      <%if(loginBean.hasLimits(pageCode, op_add)){%>
         <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
         <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD+",-1)"%>'/>
       <%}%></td>
      <td  nowrap >单据号</td>
      <td  nowrap >产品编码</td>
      <td  nowrap >品名规格</td>
      <td  nowrap >规格属性</td>
      <td  nowrap >车间</td>
      <td  nowrap >检验数量</td>
      <td  nowrap >不合格数量</td>
      <td  nowrap >状态</td>
      <td  nowrap >审批人</td>
      <td  nowrap >描述状态</td>
      <td  nowrap >制单日期</td>
      <td  nowrap >制单人</td>
    </tr>
     <%
    list.first();
    int i=0;
    for(;i<list.getRowCount();i++)
    {
       boolean isInit = false;
       String stateval =list.getValue("state");
       if(stateval.equals("0"))isInit = true;
       String creatorID = list.getValue("creatorID");//制单人ID
       String approverID = list.getValue("approverID");//审批人ID
       boolean isShow = isApproveOnly ? (loginID.equals(creatorID) && isInit) : isInit;//如果时只有制单人才可以提交审批。制单人等于登录人才显示
       boolean isCancelApprove =  stateval.equals("1");
       isCancelApprove = isCancelApprove && loginID.equals(approverID);//是否可以取消合同
       String rowClass = engine.action.BaseAction.getStyleName(stateval);
       if(stateval.equals("")) stateval="未审";
       if(stateval.equals("0")) stateval="未审";
       if(stateval.equals("1")) stateval="已审";
       if(stateval.equals("2")) stateval="已入库";
       if(stateval.equals("9")) stateval="审批中";
       RowMap productRow =productBean.getLookupRow(list.getValue("cpid"));
       String isEdit=loginBean.hasLimits(pageCode, op_edit)&&stateval.equals("0")?"修改":"查看";
       %>
      <tr id="tr_1" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="showDetail(<%=list.getRow()%>)">
        <td nowrap class=td><div align="center">
        <input name="image1" class="img" type="image" title="<%=isEdit%>" onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
        <%if(isShow){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
        <%if(isCancelApprove){%><input name="image3" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=b_ProcessCheckBean.CANCLE_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
        </div></td>
      <td nowrap <%=rowClass%> align="center"><%=list.getValue("processcheckNo")%></td><!--单据号-->
      <td nowrap <%=rowClass%> align="center"><%=productRow.get("cpbm")%></td><!--产品编码-->
      <td nowrap <%=rowClass%> align="center"><%=productBean.getLookupName(list.getValue("cpid"))%></td><!--品名规格-->
      <td nowrap <%=rowClass%> align="center"><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td><!--规格属性-->
      <td nowrap <%=rowClass%> align="center"><%=workshopBean.getLookupName(list.getValue("plantID"))%></td><!--车间-->
      <td nowrap <%=rowClass%> align="center"><%=list.getValue("checknum")%></td><!--检验数量-->
      <td nowrap <%=rowClass%> align="center"><%=list.getValue("rejectnum")%></td><!--不合格数量-->
      <td nowrap <%=rowClass%>>
      <a href='javascript:'onClick="openUrlOpt1('../pub/approve_result.jsp?operate=0&project=processcheck&id=<%=list.getValue("processcheckid")%>')">
      <%=stateval%></a></td><!--状态-->
      <td nowrap <%=rowClass%> align="center"><%=personBean.getLookupName(list.getValue("approverID"))%></td><!--审批人-->
      <td nowrap <%=rowClass%> align="center"><%=list.getValue("state_desc")%></td><!--描述状态-->
      <td nowrap <%=rowClass%> align="center"><%=list.getValue("createDate")%></td><!--制单日期-->
      <td nowrap <%=rowClass%> align="center"><%=list.getValue("creator")%></td><!--制单人-->
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize()-5; i++){
    %>
    <tr id="tr_2">
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
      <td nowrap class=td>&nbsp;</td>
    </tr>
     <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
</SCRIPT>
<%out.print(retu);%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <%b_ProcessCheckBean.table.printWhereInfo(pageContext);%>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+ Operate.FIXED_SEARCH +",-1)"%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
                  <pc:shortcut key="x" script="hideFrame('fixedQuery')"/>
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%out.print(retu);%>
</body>
</html>
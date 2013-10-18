<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.*"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "saler_handover";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%
if(!loginBean.hasLimits("saler_handover", request, response))
    return;
   engine.erp.sale.B_SalerHandover b_SalerHandoverBean  =   engine.erp.sale.B_SalerHandover.getInstance(request);

   LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
   LookUp deptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);
   LookUp mpersonBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
   LookUp mdeptBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);
   LookUp personListBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);

   LookUp tDBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_SALE_LADING);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='saler_handover.jsp';
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'afterid', 'deptid', eval('form1.afterdeptid.value'), '');
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>

<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">业务员移交</TD>
  </tr></table>
<%
  String retu = b_SalerHandoverBean.doService(request, response);

  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_SalerHandoverBean.getMaterTable();
  RowMap masterRow = b_SalerHandoverBean.getMasterRowinfo();   //主表
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add)||loginBean.hasLimits(pageCode, op_edit);
  String zt = masterRow.get("zt");
  isCanAdd= isCanAdd&&zt.equals("0");
  String edClass = isCanAdd?"class=edbox":"class=ednone";
  String readonly = isCanAdd ?"":"readonly";
  String tdid = masterRow.get("tdid");
  tDBean.regData(ds,"tdid");
  RowMap tdrow = tDBean.getLookupRow(masterRow.get("tdid"));
    //mpersonBean.regData(ds, "afterid");

     mpersonBean.regConditionData("deptid",new String[]{ds.getValue("afterdeptid")});
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="tdid" VALUE="<%=masterRow.get("tdid")%>">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;提单编号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="tdbh" VALUE="<%=tdrow.get("tdbh")%>" SIZE="30" MAXLENGTH="30" <%=edClass%> <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;购货单位</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="dwtxid" VALUE="<%=tdrow.get("dwmc")%>" SIZE="30" MAXLENGTH="30>" <%=edClass%> <%=readonly%>>
      </td>
    </tr>
       <tr>
      <td noWrap class="tableTitle">&nbsp;移交前部门</td>
      <td noWrap class="td">
      <%--if(zt.equals("0")){%>
      <pc:select name="beforedeptid" addNull="1" style="width:110"   >
      <%=deptBean.getList(masterRow.get("beforedeptid"))%>
      </pc:select>
      <%}else{--%>
      <%out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("beforedeptid"))+"' style='width:110' class='edline' readonly >");
      %>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;移交前业务员</td>
      <td noWrap class="td">
      <%--<%if(zt.equals("0")){%>
      <pc:select name="beforeid" style="width:80" addNull="1"   >
     <%=personBean.getList(masterRow.get("beforeid"))%>
      </pc:select>
      <%}else{--%>
     <% out.print("<input type='text' value='"+personBean.getLookupName(masterRow.get("beforeid"))+"' style='width:110' class='edline' readonly >");
      %>
      </td>
    </tr>
    <TR>
     <td nowrap class="tdTitle">&nbsp;移交后部门</td>
      <td  class="td">
      <%if(zt.equals("0")){%>
       <pc:select name="afterdeptid" addNull="1" style="width:110"  onSelect="deptchange();"  >
       <%=mdeptBean.getList(masterRow.get("afterdeptid"))%>
       </pc:select>
      <%}else{
      out.print("<input type='text' value='"+mdeptBean.getLookupName(masterRow.get("afterdeptid"))+"' style='width:110' class='edline' readonly >");
      }%>
      </td>
      </tr>
      <tr>
        <td nowrap class="tdTitle" align = "center">&nbsp;移交后业务员</td>
        <td  class="td">
      <%if(zt.equals("0")){%>

       <pc:select name="afterid" style="width:80" addNull="1"   >
       <%=mpersonBean.getList(masterRow.get("afterid"), "deptid", masterRow.get("afterdeptid"))%>
       </pc:select>
      <%}else{
      out.print("<input type='text' value='"+mpersonBean.getLookupName(masterRow.get("afterid"))+"' style='width:110' class='edline' readonly >");
      }%>
      </td>
      </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle a">
      <%
        if(isCanAdd)
        {
          String save = "sumitForm("+Operate.POST+",-1)";
      %>
       <input name="button"title = "保存"  type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存(S) "><pc:shortcut key="s" script='<%=save%>'/>
      <%}%>
      <%if(!b_SalerHandoverBean.isApprove){%>
        <input name="button2" type="button" title = "关闭(C)" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(C) ">
        <pc:shortcut key="c" script='parent.hideFrameNoFresh()'/><%}%>
      </td>
    </tr>
  </table>
</form>
<%//&#$
if(b_SalerHandoverBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
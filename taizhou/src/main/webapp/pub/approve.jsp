<%@page contentType="text/html; charset=UTF-8" %><%@ page import="engine.dataset.*,engine.project.*"%><%@ include file="../pub/init.jsp"%><%
  engine.common.ApproveFacade approveFacade = engine.common.ApproveFacade.getInstance(request);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  //String retu = approveFacade.doService(request, response);
  //String curUrl = request.getRequestURL().toString();
  EngineDataSet list = approveFacade.getDetailTable();
%><%--META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
<script language="javascript" src="../scripts/validate.js"></script--%>

<form name="approveform" method="post" action="../pub/approvelist.jsp" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="approveDiv" name="approveDiv">
    <div class="queryTitleBox" align="right"><img src="../images/closewin.gif" border=0></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
			<TABLE cellspacing=1 cellpadding=1 border=0 width="450">
            <TR>
              <TD nowrap class="tdtitle">审批结果</TD>
              <TD class="td" nowrap><input type="radio" name="sftg" value="1" checked>同意
                <input type="radio" name="sftg" value="2">不同意</TD>
              <TD class="tdtitle" nowrap>审批人</TD>
              <TD class="td" nowrap><%=loginBean.getUserName()%></TD>
              <TD class="tdtitle" nowrap>审批日期</TD>
              <TD class="td" nowrap><%=loginBean.getAccountDate()%></TD>
            </TR>
            <TR>
              <TD class="tdtitle" nowrap>审批意见</TD>
              <TD colspan="5" nowrap class="td"><input type="text" name="spyj" size="50" maxlength="<%=list.getColumn("spyj").getPrecision()%>"></TD>
            </TR>
          </TABLE>
		  <table id="approveTable" border="0" cellspacing="1" cellpadding="1" class="table" align="center" width="450">
            <tr class="tableTitle">
              <td nowrap width="10" align="center"></td>
              <td width="65" align="center" nowrap>审批名称</td>
              <td width="65" align="center" nowrap>审批结果</td>
              <td width="51" align="center" nowrap>审批人</td>
              <td width="62" align="center" nowrap>审批日期</td>
              <td align="center" nowrap>审批意见</td>
            </tr>
        <%list.first();
          int count = list.getRowCount();
          for(int i=0; i<count; i++)   {
            String rowClass =list.getValue("sftg");
            String result = "";
            if(rowClass.equals("1")){
              result = "通过";
              rowClass ="class=tdApproved";
            }
            else if(rowClass.equals("2")){
              result = "驳回";
              rowClass ="class=tdHandled";
            }
            else{
              result = "未审";
              rowClass ="class=td";
            }
            String title = null;
            String spyj = list.getValue("spyj");
            if(spyj.getBytes().length > 36)
            {
              title = spyj;
              spyj = engine.util.StringUtils.getUnicodeSubString(spyj, 36)+ "...";
            }
        %>  <tr>
              <td <%=rowClass%> nowrap><%=i+1%></td>
              <td <%=rowClass%> nowrap><%=list.getValue("spmc")%></td>
              <td <%=rowClass%> nowrap><%=result%></td>
              <td <%=rowClass%> nowrap><%=personBean.getLookupName(list.getValue("personid"))%></td>
              <td <%=rowClass%> nowrap><%=list.getValue("sprq")%></td>
              <td <%=rowClass%> nowrap <%=title == null ? "" : "title="+title%>><%=spyj%></td>
            </tr>
        <%  list.next();
          }
        %>
          </table>
		  <TABLE cellspacing=1 cellpadding=1 border=0 align="center">
            <TR>
              <TD nowrap align="center">
<INPUT class="button" onClick="submitApprove(<%=Operate.POST%>)" type="button" value=" 提交 " name="button" onKeyDown="return getNextElement();">
<%if(approveFacade.retuUrl != null){%><INPUT class="button" onClick="location.href='<%=approveFacade.isFromMain ? approveFacade.retuUrl : "../pub/approvelist.jsp"%>'" type="button" value=" 取消 " name="button" onKeyDown="return getNextElement();"><%}%>
              </TD>
            </TR>
		  </TABLE>
		  </TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<script language="javascript">
function submitApprove(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  approveform.operate.value = oper;
  approveform.submit();
}
function showApprove(){
  showFrame('approveDiv', true, "", true);
}
showApprove();initDefaultTableRow('approveTable',1);
</script>
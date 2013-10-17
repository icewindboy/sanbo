<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<%
  String op_edit="op_edit";
  if(!loginBean.hasLimits("approve_define", request, response))
    return;
  engine.erp.system.B_ApproveDefine b_ApproveDefineBean  =  engine.erp.system.B_ApproveDefine.getInstance(request);
  String result = b_ApproveDefineBean.doService(request, response);

  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);

  //EngineDataSet list = b_ApproveDefineBean.getDetailSPRTable();
  RowMap[] detailRows= b_ApproveDefineBean.getDetailSPRRowinfos();//从表多行
  String curUrl = request.getRequestURL().toString();
  //String[]  deid = request.getParameterValues("deid_23");
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
//选择人
function PersonSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
  var winName= "SingleProdSelector";
  paraStr = "../pub/personselect.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&curID="+curID;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">审批人定义</TD>
  </TR>
</TABLE>
  <table id="tableview1" width="80%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap colspan="2"><input name="image" class="img" type="image" title="新增" onClick="sumitForm(<%=b_ApproveDefineBean.SPR_ADD%>,-1)" src="../images/add_big.gif" border="0"></td>
      <td height='20' nowrap>提交部门</td>
      <td height='20' nowrap>审批人</td>
      </tr>
    <%
      RowMap detail = null;
      int i=0;
      for(; i<detailRows.length; i++)   {
      detail = detailRows[i];
      String personid="personid_"+i;
      String xm="xm_"+i;
      String deptid = "deptid_"+i;
    %>
    <tr>
      <td class="td" nowrap align="center"><%=i%>
      </td>
      <td class="td" nowrap align="center">
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_ApproveDefineBean.DETAIL_SPR_DEL%>,<%=i%>)" src="../images/del.gif" border="0">
      </td>
      <td noWrap class="td">
        <pc:select name="<%=deptid%>" addNull="1" style="width:110" valueName='<%="dpname_"+i%>'>
        <%=deptBean.getList(detail.get("deptid"))%> </pc:select>
      </td>
       <td noWrap class="td">
       <input type="text" name="<%=xm%>" value='<%=personBean.getLookupName(detail.get("personid"))%>' style="width:110" class="edline" readonly>
       <INPUT TYPE="HIDDEN" NAME="<%=personid%>" value='<%=detail.get("personid")%>'>
       <img style='cursor:hand' src='../images/view.gif' border=0 onClick="PersonSingleSelect('form1','srcVar=<%=personid%>&srcVar=<%=xm%>','fieldVar=personid&fieldVar=xm',form1.<%=personid%>.value)">
      </td>
      </tr>
      <%//list.next();
      }
      for(; i < 4; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
    </tr>
    <%}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',1);</script>
  <TABLE width="75%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
      <TD align="center">
        <input name="button" type="button" class="button" onClick="sumitForm(<%=b_ApproveDefineBean.SPRPOST%>);" value=" 保存 ">
        <input name="button2" type="button" class="button" onClick="window.close()" value=" 关闭 ">
      </TD>
    </TR>
  </TABLE>
</form>
<%out.print(result);%>
</BODY>
</Html>

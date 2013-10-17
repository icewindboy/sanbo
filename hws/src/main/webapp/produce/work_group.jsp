<%--生产工作组编辑页面--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%String pageCode = "work_group";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.produce.B_WorkGroup b_WorkGroupBean = engine.erp.produce.B_WorkGroup.getInstance(request);
  engine.project.LookUp workshopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  String retu = b_WorkGroupBean.doService(request, response);//location.href='baln.htm'
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type"  content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<SCRIPT language="javascript">
  /**function toDetail(){
    location.href='work_group_edit.jsp';
}*/
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value  = row;
    form1.submit();
  }

  function sumitFixedQuery(oper)
 {
   lockScreenToWait("处理中, 请稍候！");
   fixedQueryform.operate.value = oper;
   fixedQueryform.submit();
  }
  function showInterFrame(oper, rownum){
    var url = "work_group_edit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
  }
  function hideInterFrame()//隐藏FRAME
 {
   lockScreenToWait("处理中, 请稍候！");
   hideFrame('detailDiv');
   form1.submit();
  }
  function hideFrameNoFresh()
  {
    hideFrame('detailDiv');
  }

  function showFixedQuery()
   {
    showFrame('fixedQuery',true,"",true);
   }
</SCRIPT>
<%
  if(retu.indexOf("location.href=")>-1)
   {
     out.print(retu);
     return;
  }
  EngineDataSet list = b_WorkGroupBean.getMaterTable();
  String curUrl = request.getRequestURL().toString();
%>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD nowrap align="center">工作组设置</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <TABLE width="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
       <td class="td" nowrap>
     <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
</td>
      <TD align="right"><%if(loginBean.hasLimits(pageCode, op_search)){%>
        <INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询(Q)" name="Query" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=b_WorkGroupBean.retuUrl%>'" value=" 返回(C)"border="0">
        <% String back ="location.href='"+b_WorkGroupBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/>
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td width="45"><%if(loginBean.hasLimits(pageCode, op_add)){%><input name="image" class="img" type="image" title="新增(A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/><%}%></td>
      <%--input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0"--%>
      </td>
      <td nowrap>工作组编号</td>
      <td nowrap>工作组名称</td>
      <td nowrap>所属车间</td>
      <td nowrap>工作组描述</td>
    </tr>
     <%workshopBean.regData(list, "deptid");
       list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
       <td class="td" nowrap align="center" width=30>
      <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="showInterFrame(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
      </td>
      <td class="td" nowrap><%=list.getValue("gzzbh")%></td>
      <td class="td" nowrap><%=list.getValue("gzzmc")%></td>
      <td class="td" nowrap><%=workshopBean.getLookupName(list.getValue("deptid"))%></td>
      <td class="td" nowrap><%=list.getValue("gzzms")%></td>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
  function hide()
  {
    hideFrame('fixedQuery');
  }
</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
      <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
        <TR>
          <TD class="td" nowrap>工作组编号</TD>
          <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="gzzbh" name="gzzbh" value='<%=b_WorkGroupBean.getFixedQueryValue("gzzbh")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
         </TR>
         <TR>
          <TD class="td" nowrap>工作组名称</TD>
          <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" id="gzzmc" name="gzzmc" value='<%=b_WorkGroupBean.getFixedQueryValue("gzzmc")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
         </TR>
         <TR>
         <TD class="td" nowrap>所属车间</TD>
          <TD nowrap class="td"><pc:select name="deptid" addNull="1" style="width:160">
     <%=workshopBean.getList(b_WorkGroupBean.getFixedQueryValue("deptid"))%></pc:select>
       </TD>
       </TR>
       </TABLE>
         <TR>
        <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=b_WorkGroupBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
          <pc:shortcut key="f" script='<%="sumitFixedQuery("+ b_WorkGroupBean.FIXED_SEARCH +",-1)"%>'/>
          <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
       <pc:shortcut key="x" script='hide();'/>
        </TD>
      </TR>
    </TABLE>
  </DIV>
  <div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="460" height="350" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</form><%out.print(retu);%>
</body>
</html>
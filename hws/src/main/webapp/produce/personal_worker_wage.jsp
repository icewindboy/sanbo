<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%String pageCode = "personal_worker_wage";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.produce.B_PersonalWage personalWageBean = engine.erp.produce.B_PersonalWage.getInstance(request);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//通过工作组id得到工作组名称
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  String retu = personalWageBean.doService(request, response);//location.href='baln.htm'
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);

  javax.servlet.jsp.JspFactory jspf = JspFactory.getDefaultFactory();
  //jspf.getPageContext()
  //pageContext.include();
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
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value  = row;
    form1.submit();
  }
  function toDetail(oper, row){
    location.href='personal_workerdetail_wage.jsp?operate='+oper+'&rownum='+row;
  }
  function sumitFixedQuery(oper)
  {
    lockScreenToWait("处理中, 请稍候！");
    fixedQueryform.operate.value = oper;
    fixedQueryform.submit();
  }
  function showFixedQuery()
  {
    showFrame('fixedQuery',true,"",true);
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
  function productCodeSelect(obj, srcVars)
  {
    ProdCodeChange(document.all['prod'], obj.form.name, srcVars,
                   'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
  }
  function productNameSelect(obj,srcVars)
  {
    ProdNameChange(document.all['prod'], obj.form.name, srcVars,
                   'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
  }
  function syncData()
  {
    if(confirm('是否将工人工资人员数据同工作组人员数据同步?'))
      sumitForm('<%=personalWageBean.SYNC%>');
  }
</SCRIPT>
<%
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = personalWageBean.getMasterTable();
  //RowMap rowinfo = personalWageBean.getRowinfo();
  String curUrl = request.getRequestURL().toString();
%>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD nowrap align="center">工人工资列表</TD>
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
      <TD align="right">
        <%if(hasSearchLimit){%><INPUT class="button" onClick="showFixedQuery()" type="button" value="查询(Q)" name="Query" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <%if(loginBean.hasLimits(pageCode, op_edit)){%><INPUT class="button" onClick="syncData();" type="button" value="同步(T)" name="sync" title="将未完成单据的人员同步为工作组的人员" onKeyDown="return getNextElement();">
        <pc:shortcut key="t" script='syncData();'/><%}%>
        <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=personalWageBean.retuUrl%>'" value="返回(C)"border="0">
        <% String back ="location.href='"+personalWageBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/>
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
    <td nowrap>&nbsp</td>
      <td nowrap>车间</td>
      <td nowrap>工作组</td>
      <td nowrap>总工费</td>
      <td nowrap>单据号</td>
      <td nowrap>单据日期</td>
      <td nowrap>班次</td>
      <td nowrap>经手人</td>
    </tr>
     <%workShopBean.regData(list,"deptid");
       workGroupBean.regData(list,"gzzid");
       list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
        boolean isOver = list.getValue("ztbj").equals("8");
    %>
    <tr onClick="selectRow();" onDblClick="toDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)">
        <td class="td"><input name="image2" class="img" type="image" title="修改" onClick="toDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
       <%if(!isOver && loginBean.hasLimits(pageCode, op_over)){%><INPUT name="over" class="img" type="image" title="完成" onClick="if(confirm('设置完成将不能再修改单据！')) sumitForm(<%=personalWageBean.OVER%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>
       </td>
      <td class='<%=isOver ?"tdComplete":"td"%>' nowrap><%=workShopBean.getLookupName(list.getValue("deptid"))%></td>
      <td class='<%=isOver ?"tdComplete":"td"%>' nowrap><%=workGroupBean.getLookupName(list.getValue("gzzid"))%></td>
      <td class='<%=isOver ?"tdComplete":"td"%>' nowrap><%=list.getValue("zgf")%></td>
      <td class='<%=isOver ?"tdComplete":"td"%>' nowrap><%=list.getValue("djh")%></td>
      <td class='<%=isOver ?"tdComplete":"td"%>' nowrap><%=list.getValue("djrq")%></td>
      <td class='<%=isOver ?"tdComplete":"td"%>' nowrap><%=list.getValue("bc")%></td>
      <td class='<%=isOver ?"tdComplete":"td"%>' nowrap><%=list.getValue("jsr")%></td>
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
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0><%personalWageBean.table.printWhereInfo(pageContext);%>
            </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=personalWageBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+ personalWageBean.FIXED_SEARCH +",-1)"%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
                  <pc:shortcut key="x" script='hide();'/>
              </TD>
            </TR>
    </TABLE>
  </DIV>
</form>
<%out.print(retu);%>
</body>
</html>

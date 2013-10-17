<%--物料可替换件显示可选件列表页面--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%String pageCode = "bom_replace_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.produce.B_BomRefill bomRefillBean = engine.erp.produce.B_BomRefill.getInstance(request);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  String retu = bomRefillBean.doService(request, response);//location.href='baln.htm'
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
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
  function toDetail(){
    location.href='bom_refill.jsp';
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
</SCRIPT>
<%
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = bomRefillBean.getMasterTable();
  //RowMap rowinfo = bomRefillBean.getRowinfo();
  String curUrl = request.getRequestURL().toString();
%>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD nowrap align="center">可替换件列表</TD>
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
        <%if(hasSearchLimit){%><INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询(Q)" name="Query" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/><%}%>
        <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=bomRefillBean.retuUrl%>'" value=" 返回(C)"border="0">
        <% String back ="location.href='"+bomRefillBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/>
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
	<td>&nbsp;</td>
	<td colspan="5" align="center">可选件</td>
	<td colspan="4" align="center">父件</td>
	</tr>
	<tr class="tableTitle">
       <td nowrap align="center" width=45></td>
      <td nowrap>编码</td>
      <td nowrap>品名</td>
      <td nowrap>规格</td>
      <td nowrap>数量</td>
      <td nowrap>单位</td>
      <td nowrap>父件编码</td>
      <td nowrap>父件品名</td>
      <td nowrap>父件规格</td>
      <td nowrap>父件单位</td>
    </tr>
     <%prodBean.regData(list, "sjcpid");
       prodBean.regData(list,"cpid");
       list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)">
        <td class="td"><input name="image2" class="img" type="image" title="修改" onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
       </td>
	  <%RowMap prodrow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <%RowMap fatherprodRow = prodBean.getLookupRow(list.getValue("sjcpid"));%>
      <td class="td" nowrap><%=prodrow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodrow.get("pm")%></td>
      <td class="td" nowrap><%=prodrow.get("gg")%></td>
      <td class="td" nowrap><%=list.getValue("sl")%></td>
      <td class="td" nowrap><%=prodrow.get("jldw")%></td>
      <td class="td" nowrap><%=fatherprodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=fatherprodRow.get("pm")%></td>
      <td class="td" nowrap><%=fatherprodRow.get("gg")%></td>
      <td class="td" nowrap><%=fatherprodRow.get("jldw")%></td>
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
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0><%bomRefillBean.table.printWhereInfo(pageContext);%>
            </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=bomRefillBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+ bomRefillBean.FIXED_SEARCH +",-1)"%>'/>
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
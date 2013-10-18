<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "tool_info";
%>
<%if(!loginBean.hasLimits("tool_info", request, response))
    return;
  engine.erp.quality.B_ToolInfo certified=engine.erp.quality.B_ToolInfo.getInstance(request);
  //engine.erp.quality.B_CertifiedCard  certifiedBean =engine.erp.quality.B_CertifiedCard.getInstance(request);
  engine.erp.quality.B_ToolInfo certifiedBean=engine.erp.quality.B_ToolInfo.getInstance(request);
 // engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
 //engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  String retu = certifiedBean.doService(request, response);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp tooltypeBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_QUALITY_TOOLTYPE);
  if(retu.indexOf("location.href=")>-1)
    return;
%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<SCRIPT LANGUAGE="javascript">
  function sumitFixedQuery(oper)
  {
    lockScreenToWait("处理中, 请稍候！");
    fixedQueryform.operate.value = oper;
    fixedQueryform.submit();
  }
  function showFixedQuery(){
    showFrame('fixedQuery', true, "", true);
  }
  function sumitSearchFrame(oper)
  {
    lockScreenToWait("处理中, 请稍候！");
    searchform.operate.value = oper;
    searchform.submit();
  }
  function showSearchFrame(){
    hideFrame('fixedQuery');
    showFrame('searchframe', true, "", true);
  }
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum)
  {
    var url = "tool_info_edit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
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
  function productCodeSelect(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}

  </SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>检验器具信息</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = certifiedBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);
  //boolean issearch = loginBean.hasLimits(pageCode,op_search);
  RowMap row = certifiedBean.getRowinfo();
  //prodBean.regData(list,"deptid");
  deptBean.regData(list,"deptid");
  tooltypeBean.regData(list,"toolTypeID");
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="97%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>
 <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
  int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
   <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
    <td class="td" nowrap align="right">
     <%if(loginBean.hasLimits("tool_info", request, response)){%>
      <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
       <pc:shortcut key="q" script='showFixedQuery()'/><%--<script language='javascript'>GetShortcutControl(81,"showFixedQuery()");</script>--%>
        <%--<input name="print" type="button" class="button" onClick="showPdf()" value="打印(P)" onKeyDown="return getNextElement();">
         <pc:shortcut key="p" script='showPdf()'/><%--<script language='javascript'>GetShortcutControl(80,"showPdf()");</script>--%>
     <%}%>
     <%if(certifiedBean.retuUrl!=null){ String loc = "location.href='"+ certifiedBean.retuUrl+"';";%>
      <input name="button2" type="button" class="button" onClick="location.href='<%=certifiedBean.retuUrl%>'" value="返回(C)" onKeyDown="return getNextElement();">
       <pc:shortcut key="c" script='<%=loc%>'/><%}%></TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="97%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowarp width="50"><%if(isCanAdd){%><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0"><%}%>
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/></td>
      <td  nowarp align="center">器具分类</td>
      <td  nowarp align="center">部门名称</td>
      <td  nowarp align="center">器具编码</td>
      <td  nowarp align="center">器具名称</td>
      <td  nowarp align="center">器具规格型号</td>
      <td  nowarp align="center">器具备注</td>
      </td>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
        //RowMap prodRow = prodBean.getLookupRow(list.getValue("deptid"));
    %>
    <tr <%if(isCanEdit){out.print("onDblClick=showInterFrame("+Operate.EDIT+","+list.getRow()+")");}%>>
      <td class="td" nowarp><%if(isCanEdit){%>
       <input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
       <%}%>
      <%if(isCanDelete){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0"><%}%>
      </td>
      <%--<td class="td" nowrap><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodRow.get("product")%></td>
      <td class="td" nowrap><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
      <td class="td" nowrap>&nbsp;<%=list.getValue("toolTypeID")%></td>//tooltypeBean--%>
     <td class="td" nowrap>&nbsp;<%=tooltypeBean.getLookupName(list.getValue("toolTypeID"))%></td>
      <td class="td" nowrap>&nbsp;<%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td class="td" nowrap>&nbsp;<%=list.getValue("toolCode")%></td>
      <td class="td" nowrap>&nbsp;<%=list.getValue("toolName")%></td>
      <td class="td" nowrap>&nbsp;<%=list.getValue("toolSpec")%></td>
      <td class="td" nowrap>&nbsp;<%=list.getValue("toolMemo")%></td>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
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
     <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <%certifiedBean.table.printWhereInfo(pageContext);%>
            </TR>
              <TD nowrap colspan=3 height=30 align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <INPUT class="button"  onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <INPUT class="button" onClick="hideFrame('fixedQuery');hideFrame('detailDiv');" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
  <div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv');" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="480" height="180" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</form>
<%out.print(retu);%>
</body>
</html>
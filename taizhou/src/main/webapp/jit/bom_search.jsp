<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.*"%>
<%  String pageCode = "bom";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.jit.B_Bom bomBean = engine.erp.jit.B_Bom.getInstance(request);
  EngineDataSet list = bomBean.dsBomSearch;
  String retu = bomBean.doService(request, response);
  LookUp productKindBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT_KIND);
  int pageSize = loginBean.getPageSize();
%>
<html><head><title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>

<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">查询结果</TD>
  </TR></TABLE>
<table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
  <tr>
    <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize='<%=""+pageSize%>'/></td>
      <td class="td" align="right" nowrap>
        <input name="search" type="button" class="button" onClick="showFixedQuery()" accessKey="q" value="查询(Q)" onKeyDown="return getNextElement();">
      </td>
  </tr>
</table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td height="20" width=12 nowrap></td>
      <td nowrap>物资编码</td>
      <td nowrap>品名规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>单位</td>
      <td nowrap>存货类别</td>
      <td nowrap>存货性质</td>
      <td nowrap>父件编码</td>
      <td nowrap>父件名称</td>
      <td nowrap>父件规格属性</td>
    </tr>
    <%list.first();
    int i=0;
    for(; i<list.getRowCount(); i++)   {
      String wzlbid = list.getValue("wzlbid");
      String chxz = list.getValue("chxz");
  %>
    <tr onClick="selectRow()" onDblClick="searchPath('<%=list.getValue("cpid")%>_<%=list.getValue("dmsxid")%>$<%=list.getValue("sjcpid")%>_<%=list.getValue("sjdmsxid")%>')">
      <td nowrap class="td"><input type='image' style='cursor:hand' align='absmiddle' src='../images/view.gif' border=0
        onClick="searchPath('<%=list.getValue("cpid")%>_<%=list.getValue("dmsxid")%>$<%=list.getValue("sjcpid")%>_<%=list.getValue("sjdmsxid")%>')" title='展开树路径'></td>
      <td nowrap class="td"><%=list.getValue("cpbm")%></td>
      <td nowrap class="td"><%=list.getValue("product")%></td>
      <td nowrap class="td"><%=list.getValue("sxz")%></td>
      <td nowrap class="td"><%=list.getValue("jldw")%></td>
      <td nowrap class="td"><%=productKindBean.getLookupName(wzlbid)%></td>
      <td nowrap class="td"><%=chxz.equals("1") ? "自制件" : chxz.equals("2") ? "外购件" : chxz.equals("3") ? "外协件" : chxz.equals("4") ? "虚拟件" : ""%></td>
      <td nowrap class="td"><%=list.getValue("sjcpbm")%></td>
      <td nowrap class="td"><%=list.getValue("sjproduct")%></td>
      <td nowrap class="td"><%=list.getValue("sjsxz")%></td>
    </tr>
    <%  list.next();
    }
    for(; i < pageSize; i++){
  %>
    <tr>
      <td class="td"></td>
      <td nowrap class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  showFrame('fixedQuery', true, "", true);
}
function searchPath(nodeid){
  parent.depttree.form1.operate.value = '<%=bomBean.SEARCH_PATH%>';
  parent.depttree.form1.curNodeID.value = nodeid;
  parent.depttree.form1.submit();
}
</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=request.getRequestURI()%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
          <TABLE cellspacing=3 cellpadding=0 border=0><%bomBean.table.printWhereInfo(pageContext);%>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.MASTER_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <input class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
          </TABLE>
        </TD>
      </TR>
    </TABLE>
  </DIV>
</form>
</body>
</html>
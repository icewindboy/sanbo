<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.erp.baseinfo.*,engine.action.Operate,engine.project.*,engine.html.*"%>
<%@ page import="engine.dataset.EngineDataSet,com.borland.dx.dataset.DataSet"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode  = "corplist";
  B_Corp corpBean = null;
  class ListCellListener implements HtmlPrintCellListener
  {
    public void printCell(JspWriter out, HtmlPrintCellResponse reponse, DataSet ds) throws Exception
    {
      if(!reponse.getField().getFieldcode().toLowerCase().equals("ywlx"))
        return;
      StringBuffer header = new StringBuffer();
      if(reponse.getPrintType() == HtmlPrintCellResponse.PRINT_BODY)
      {
        String dwtxid = ds.getBigDecimal("dwtxid").toString();
        reponse.getCellContent().append(corpBean.getCorpTypeString(dwtxid));
      }
    }
  }
%><%
  if(!loginBean.hasLimits(pageCode,request,response))
   return;
  boolean hasAdd = loginBean.hasLimits(pageCode, op_add);
  //boolean hasDelete = loginBean.hasLimits(pageCode, op_delete);
  boolean hasEdit = loginBean.hasLimits(pageCode, op_edit);
  boolean hasSearch = loginBean.hasLimits(pageCode, op_search);
  corpBean = B_Corp.getInstance(request);
  //LookUp areaBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_AREA);
%>
<html>
<head>
<title>往来单位信息列表</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript" src="../scripts/exchangeselect.js"></script>
<script language="javascript">
function toDetail(){
  location.href='corpedit.jsp';
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function showSearchFrame(){
  showFrame('searchframe1', true, "", true);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD NOWRAP align="center">往来单位列表</TD>
  </TR></TABLE>
<%String retu = corpBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = corpBean.getMaterTable();
  String curUrl = request.getRequestURL().toString();
  corpBean.table.setHtmlPrintCellListener(new ListCellListener());
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = String.valueOf(iPage);%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right"><input name="search" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)">
        <input name="sort" type="button" class="button" onClick="showSort()" value="排序(P)">
        <pc:shortcut key="p" script='showSort()'/>
        <%if(corpBean.retuUrl!=null){%><input name="button22" type="button" class="button" onClick="location.href='<%=corpBean.retuUrl%>'" value="返回(C)">
        <pc:shortcut key="c" script='<%="location.href="+corpBean.retuUrl%>'/><%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td valign="middle" align="center" width=25 nowrap><%if(hasAdd){%><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%></td>
      <%corpBean.table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)   {
    %>
    <tr onDblClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)">
      <td align="center" class="td"><input name="image2" class="img" type="image" title='<%=hasEdit ? "修改" : "浏览"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
        <%--if(hasDelete){<input name="image3" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=i%>)" src="../images/del.gif" border="0">--%>
      </td>
      <%corpBean.table.printCells(pageContext, "class=td");%>
    </tr>
      <%list.next();
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <%corpBean.table.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}%>
  </table>
</form>
<script LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  hideFrame('divOrder')
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
</script>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="javascript:"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0 align=center>
      <TR>
        <TD><table cellspacing=3 cellpadding=0 border=0>
            <%corpBean.table.printWhereInfo(pageContext);%>
            <tr>
              <td nowrap colspan=6 height=30 align="center"><input class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <input class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </td>
            </tr>
          </table></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<script language="javascript">
function showSort(){
  hideFrame('fixedQuery')
  showFrame('divOrder', true, "", true);
}
function submitSort(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  getColumnArray();
  sortform.operate.value = oper;
  sortform.submit();
}
function getColumnArray()
{
  sortform.sortColumnStr.value = fnGetOptionValue(',', sortColumns);
}
</script>
<form name="sortform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="sortColumnStr" VALUE="">
  <div class="queryPop" id="divOrder" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('divOrder')" href="javascript:"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD><TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD colspan="4" nowrap class="td"><table cellspac="0" cellpadding="0" width="100%" border="0">
                  <tr>
                    <%java.util.List fieldCodes = corpBean.orderFieldCodes;
                      java.util.List fieldNames = corpBean.orderFieldNames;
                      java.util.List selectedCodes = corpBean.selectedOrders;
                      StringBuffer buf = new StringBuffer("<script language='javascript'>var oOption;");
                      //TOption(type, value, text, isEnable, rowClick, checkName, isChecked, checkClick)
                      for(i=0; i<fieldCodes.size(); i++)
                      {
                        String fieldCode = (String)fieldCodes.get(i);
                        if(selectedCodes.indexOf(fieldCode) >= 0)
                          continue;
                        buf.append("oOption = new TOption('none','").append(fieldCode).append("',");
                        buf.append("'").append(fieldNames.get(i)).append("',true, null);");
                        buf.append("fnAddOption(showColumns, oOption);");
                      }
                      buf.append("</script>");
                      //
                      StringBuffer sot = new StringBuffer("<script language='javascript'>");
                      for(i=0; i<selectedCodes.size(); i++)
                      {
                        String fieldCode = (String)selectedCodes.get(i);
                        int index = fieldCodes.indexOf(fieldCode);
                        if(index < 0)
                          continue;
                        sot.append("oOption = new TOption('none','").append(fieldCode).append("',");
                        sot.append("'").append(fieldNames.get(index)).append("',true,null);");
                        sot.append("fnAddOption(sortColumns, oOption);");
                      }
                      sot.append("</script>");
                    %>
                    <td><table width="100%" cellspacing="0" cellpadding="0" border="0">
                        <TR>
                          <td class=td> <table cellSpacing="0" cellPadding="0" border="0" width="100%">
                              <tr>
                                <td class="td" valign="top" nowrap height="25"><b>可选列名</b></td>
                              </tr>
                              <tr>
                                <td><div style="overflow-y: auto; width: 120; height: 160; background-color: white; border: 1 solid #777777;" onselectstart="return CancelSelect();">
                                    <table id="showColumns" name="showColumns" width="100%" cellSpacing="0" cellPadding="0" border="0" onclick="SelectOption(this, false)" ondblclick="fnExchangeSelect(showColumns, sortColumns, false)">
                                    </table>
                                    <%=buf%> </div></td>
                              </tr>
                            </table></td>
                        </tr>
                      </table></td>
                    <td><table cellSpacing="0" cellPadding="0" border="0" height="135" width="100%">
                        <tr>
                          <td class="td" valign="top" nowrap height="25">&nbsp;</td>
                        </tr>
                        <tr>
                          <td height="55"></td>
                        </tr>
                        <tr>
                          <td><input name="button3" type="button" class="edbox" style="width:20;" onclick="fnExchangeSelect(showColumns, sortColumns, false)" value="&gt;" title="添加排序列名"></td>
                        </tr>
                        <tr>
                          <td height="2"></td>
                        </tr>
                        <tr>
                          <td><input name="button3" type="button" class="edbox" style="width:20;" onclick="fnRemoveMultiOption(sortColumns)" value="&lt;" title="移去排序列名"></td>
                        </tr>
                        <tr>
                          <td height="55"></td>
                        </tr>
                      </table></td>
                    <td><table cellSpacing="0" cellPadding="0" border="0" width="100%">
                        <tr>
                          <td class="td" valign="top" nowrap height="25"><b>排序列名</b></td>
                        </tr>
                        <tr>
                          <td><div style="overflow-y: auto; width: 120; height: 160; background-color: white; border: 1 solid #777777;" onselectstart="return CancelSelect();">
                              <table id="sortColumns" name="sortColumns" width="100%" cellSpacing="0" cellPadding="0" border="0" onclick="SelectOption(this)" ondblclick="fnExchangeSelect(sortColumns, showColumns, false)">
                              </table>
                              <%=sot%> </div></td>
                        </tr>
                      </table></td>
                </table></TD>
            </tr>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="submitSort(<%=engine.action.Operate.ORDERBY%>)" type="button" value=" 排序 " name="button">
                <INPUT class="button" onClick="hideFrame('divOrder');" type="button" value=" 关闭 " name="button2">
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<iframe id="interframe1" src="" class="frameBox" width="328" height="200" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
<%out.print(retu);%>
</body>
</html>

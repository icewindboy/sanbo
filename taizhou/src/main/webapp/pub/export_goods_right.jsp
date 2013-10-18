<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate, engine.html.*, engine.project.*"%><%!
  class ListCellListener implements HtmlPrintCellListener
  {
    public void printCell(JspWriter out, HtmlPrintCellResponse reponse, com.borland.dx.dataset.DataSet ds) throws Exception
    {
      if(reponse.getPrintType() == HtmlPrintCellResponse.PRINT_BODY)
      {
        reponse.getCellHeader().append(" id='").append(reponse.getField().getFieldcode().toLowerCase())
            .append("_").append(ds.getRow()).append("'");
      }
    }
  }
%><%
  engine.erp.common.B_ExportGoodsSelect exportGoodsSelectBean = engine.erp.common.B_ExportGoodsSelect.getInstance(request);
  String retu = exportGoodsSelectBean.doService(request, response);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = exportGoodsSelectBean.getOneTable();
  exportGoodsSelectBean.table.setHtmlPrintCellListener(new ListCellListener());
%>
<html>
<head>
<title><%=request.getParameter("storeid")%></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript">
function sumitForm(oper){
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function selectProduct(row)
{
<%if(exportGoodsSelectBean.isMultiSelect){%>
  if(form1.sel.length+''=='undefined'){
    if(form1.sel.checked)
      singleSelect(0);
  }
  else{
    var multiId = '';
    var isSecond = false;
    for(var i=0; i<form1.sel.length; i++){
      if(!form1.sel[i].checked)
        continue;
      if(!isSecond) {
        isSecond = true;
        singleSelect(i);
      }
      else
        multiId += form1.sel[i].value+',';
    }
  }
  if(multiId != ''){
    multiId += "-1";
  <%if(exportGoodsSelectBean.multiIdInput != null){
      String mutiId = "parent.opener."+exportGoodsSelectBean.srcFrm+"."+exportGoodsSelectBean.multiIdInput;
      out.print(mutiId+".value=multiId;");
      out.print(mutiId+".onchange();");

    }%>
  }
<%}else{%>
 singleSelect(row);
 <%
  if(exportGoodsSelectBean.getMethodName() != null)
    out.print("parent.opener."+exportGoodsSelectBean.getMethodName()+";");
  }
%>
 parent.close();
}
function singleSelect(row){
  var obj;
  if(row +'' == 'undefined')
  {
    var rodioObj = gCheckedObj(form1, false);
    if(rodioObj != null)
      row = rodioObj.value;
    else
      return;
  }
  <%
  String inputName[] = exportGoodsSelectBean.inputName;
  String fieldName[] = exportGoodsSelectBean.fieldName;
  if(inputName != null && fieldName != null)
  {
    int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
    for(int i=0; i< length; i++)
    {
      out.println("obj = document.all['"+fieldName[i]+"_'+row];");
      out.print("parent.opener."+exportGoodsSelectBean.srcFrm+"."+inputName[i]);
      out.print(".value=");
      out.println("obj."+
                  (fieldName[i].equalsIgnoreCase("xpwzdjid") || fieldName[i].equalsIgnoreCase("cpid")||
                  fieldName[i].equalsIgnoreCase("wzlbid") || fieldName[i].equalsIgnoreCase("isprops") ||
                  fieldName[i].equalsIgnoreCase("pm") || fieldName[i].equalsIgnoreCase("gg") ||
                  fieldName[i].equalsIgnoreCase("hsbl") || fieldName[i].equalsIgnoreCase("ztqq") ||
                  fieldName[i].equalsIgnoreCase("zk")||fieldName[i].equalsIgnoreCase("dxpwzdjid") ? "value;" : "innerText;"));
    }
  }
  %>
}

function checkRadio(row){
  if(form1.sel.length+''=='undefined'){
    form1.sel.checked = !form1.sel.checked;
    row = 0;
  }
  else
    form1.sel[row].checked = !form1.sel[row].checked;

  var checkInput = document.all['num_'+row];
  if(checkInput != null && checkInput.value != '' && parseInt(checkInput.value) > 0){
    alert('有产品在促销之中！');
    if(form1.sel.length+''=='undefined'){
      form1.sel.checked = false;
    }
    else
      form1.sel[row].checked = false;
    return;
  }
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">产品明细选择</TD>
  </TR></TABLE>
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = 20;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="20"/></td>
      <td class="td" align="right"><input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
      <pc:shortcut key="q" script='showFixedQuery();'/>
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value="选用(X)" onKeyDown="return getNextElement();"> <%--sumitForm(<%=exportGoodsSelectBean.TURN_PAGE_CHANGE%>);--%>
        <pc:shortcut key="x" script='selectProduct();'/>
      <%}%>
        <input  type="button" class="button" onClick="parent.close();" value="返回(C)" onKeyDown="return getNextElement();"></td>
        <pc:shortcut key="c" script='parent.close();'/>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20>
     <%if(exportGoodsSelectBean.isMultiSelect){%>
     <input type='checkbox' name='checkform' readonly onclick='checkAll(form1,this);'>
      <%}%></td>
      <%exportGoodsSelectBean.table.printTitle(pageContext, "height='20'");%><%--td class="td" nowrap>编码</td>
      <td class="td" nowrap>助记码</td>
      <td class="td" nowrap>品名 规格</td>
      <td class="td" nowrap>单位</td>
      <td class="td" nowrap>库存量</td>
      <td class="td" nowrap>可供量</td>
      <td class="td" nowrap>计划单价</td>
      <td class="td" nowrap>销售价</td>
      <td class="td" nowrap>基准价</td>
      <td class="td" nowrap>提成率</td>
      <td class="td" nowrap>回款天数</td>
      <td class="td" nowrap>回款提成率</td--%>
    </tr>
    <%LookUp promotionBean = null;
      String dwtxid = exportGoodsSelectBean.dwtxid;
      if(dwtxid == null || dwtxid.length() == 0)
        dwtxid = "-1";
      if(exportGoodsSelectBean.isCheckPromotion){
        promotionBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PROMOTION_COUNT);
        String[] cpids = DataSetUtils.rowsToArray(list, "cpid");
        LookupParam[] param = new LookupParam[]{
          new LookupParam("dwtxid", new String[]{dwtxid}),
          new LookupParam("cpid", cpids)};
        /*LookupParam[] param = new LookupParam[]{
          new LookupParam("dwtxid", new String[]{"-1"}),
          new LookupParam("cpid", new String[]{"-1"})};*/
        promotionBean.regData(param);
      }
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr onClick="selectRow()" <%if(!exportGoodsSelectBean.isMultiSelect){%>onDblClick="checkRadio(<%=i%>);selectProduct(<%=i%>);"<%}%>>
      <td nowrap align="center" class="td">
        <%if(exportGoodsSelectBean.isMultiSelect){%>
        <input type="checkbox" name="sel" value="<%=list.getValue("xpwzdjid")%>" > <%--=exportGoodsSelectBean.wzdjlist.contains(list.getValue("xpwzdjid"))?" checked":""--%>
        <%}else{%>
        <input type="radio" name="sel" onKeyDown="return getNextElement();"  value="<%=i%>" <%=i==0?" checked":""%>>
        <%}%><input type="hidden" id="xpwzdjid_<%=i%>" value='<%=list.getValue("xpwzdjid")%>'>
        <input type="hidden" id="cpid_<%=i%>" value='<%=list.getValue("cpid")%>'>
        <input type="hidden" id="wzlbid_<%=i%>" value='<%=list.getValue("wzlbid")%>'>
        <input type="hidden" id="pm_<%=i%>" value='<%=list.getValue("pm")%>'>
        <input type="hidden" id="gg_<%=i%>" value='<%=list.getValue("gg")%>'>
        <input type="hidden" id="hsbl_<%=i%>" value='<%=list.getValue("hsbl")%>'>
        <input type="hidden" id="hsdw_<%=i%>" value='<%=list.getValue("hsdw")%>'>
        <input type="hidden" id="ztqq_<%=i%>" value='<%=list.getValue("ztqq")%>'>
        <input type="hidden" id="mrzk_<%=i%>" value='<%=list.getValue("mrzk")%>'>
        <input type="hidden" id="isprops_<%=i%>" value='<%=list.getValue("isprops")%>'>
        <input type="hidden" name="xpwzdjid" value='<%=list.getValue("xpwzdjid")%>'><!--复选用-->
        <input type="hidden" name="dxpwzdjid" value='<%=list.getValue("xpwzdjid")%>'><!--复选用-->
        <%if(exportGoodsSelectBean.isCheckPromotion){
        %><input type="hidden" id="num_<%=i%>" value='<%=promotionBean.getLookupName(new String[]{dwtxid, list.getValue("cpid")})%>'><%}%>
      </td>
      <%exportGoodsSelectBean.table.printCells(pageContext, "class='td' onClick='checkRadio("+i+")'");%>
    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){

    %>
    <tr>
      <td class="td">&nbsp;</td>
      <%exportGoodsSelectBean.table.printBlankCells(pageContext, "class=td");%>
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  //hideFrame('searchframe');
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
</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
<%exportGoodsSelectBean.table.printWhereInfo(pageContext);%>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form><%out.print(retu);%>
</body>
</html>
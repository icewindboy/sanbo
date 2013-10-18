<%@ page contentType="text/html;charset=UTF-8"%>
<%request.setCharacterEncoding("UTF-8");
  //session.setMaxInactiveInterval(900);
  response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","max-age=0");
  response.addHeader("Cache-Control","pre-check=0");
  response.addHeader("Cache-Control","post-check=0");
  response.setDateHeader("Expires", 0);
%><%@ taglib uri="/WEB-INF/pagescontrol.tld" prefix="pc"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
%><%
  engine.erp.sale.B_CustProdHistorySelect custProdHisBean = engine.erp.sale.B_CustProdHistorySelect.getInstance(request);
  String retu = custProdHisBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String operate = request.getParameter("operate");
  if(operate !=null && operate.equals(String.valueOf(Operate.INIT)))
  {
    srcFrm = request.getParameter("srcFrm");
    multiIdInput = request.getParameter("srcVar");
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = custProdHisBean.getOneTable();
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
%>
<html>
<head>
<title></title>
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
function selectProduct(row)
{
  var multiId = '';

  for(var i=0;i<form1.elements.length;i++)
  {
    var e = form1.elements[i];
    //alert("name:"+e.name+",type:"+e.type + ",checked:"+e.checked);
    if(e.type == "checkbox" && e.name!='checkform' && e.checked == true)
      multiId += e.value+',';
  }
  if(multiId.length > 0){
    multiId += '-1';
  <%if(multiIdInput != null){
      String mutiId = "window.opener."+srcFrm+"."+multiIdInput;
      out.print(mutiId+".value=multiId;");
      out.print(mutiId+".onchange();");
    }%>
  }
  window.close();
}
function checkRadio(row){
  //selectRow()
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = !form1.sel.checked;
  else
    form1.sel[row].checked = !form1.sel[row].checked;
}
function checkTr(row)
{
  if(form1.sel.length+''=='undefined')
    checkRow(form1.sel.checked);
  else
    checkRow(form1.sel[row].checked);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">客户产品</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = 20; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right">
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value="选用(S)" onKeyDown="return getNextElement();">
        <pc:shortcut key="s" script='selectProduct();'/>
      <%}%>
        <input  type="button" class="button" onClick="window.close();" value="返回(X)" onKeyDown="return getNextElement();">
        <pc:shortcut key="x" script='window.close();'/>
     </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td class="td" nowrap>产品编码</td>
      <td class="td" nowrap>品名 规格</td>
       <td class="td" nowrap>规格属性</td>
      <td class="td" nowrap>单位</td>
       <td class="td" nowrap>报价</td>
       <td class="td" nowrap>基准价</td>
      <td class="td" nowrap>单价</td>
      <td class="td" nowrap>折扣%</td>
    </tr>
    <%
      propertyBean.regData(list,"dmsxID");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {

    %>
    <tr onclick="checkTr(<%=i%>)">
      <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=list.getValue("cdmsxid")%>' onKeyDown="return getNextElement();"></td>
      <td nowrap class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("cpbm")%></td>
      <td nowrap class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("product")%></td>
      <td nowrap class="td" onClick="checkRadio(<%=i%>)"><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
      <td nowrap align="center" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("jldw")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("xsj")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("xsjzj")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("dj")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("zk")%></td>
    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
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
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
</SCRIPT>
<%--
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
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0 align="c" >
            <TR>
              <TD nowrap class="td">编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="cpbh" value='<%=custProdHisBean.getFixedQueryValue("cpbh")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap align="center">助记码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="zjm" value='<%=custProdHisBean.getFixedQueryValue("zjm")%>' maxlength='10' onKeyDown="return getNextElement();">
              </TD>
            </TR>
            <TR>
              <TD nowrap class="td">品名规格</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:160" name="product" value='<%=custProdHisBean.getFixedQueryValue("product")%>' maxlength='10' onKeyDown="return getNextElement();">
              </TD>
              <TD align="center" nowrap class="td">图号</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="th" value='<%=custProdHisBean.getFixedQueryValue("th")%>' maxlength='10' onKeyDown="return getNextElement();">
              </TD>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>--%><%out.print(retu);%>
</body>
</html>
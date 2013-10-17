<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
%><%
  engine.erp.finance.B_LadingSelProduct ladingSelProdBean = engine.erp.finance.B_LadingSelProduct.getInstance(request);
  String retu = ladingSelProdBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String operate = request.getParameter("operate");
  if(operate !=null && operate.equals(String.valueOf(Operate.INIT)))
  {
    String curID = request.getParameter("curID");
    if(curID == null)
    {
      out.print(ladingSelProdBean.showJavaScript("alert('请先选择仓库！');window.close();"));
      return;
    }
    curID = curID.trim();
    try{
      Integer.parseInt(curID);
    }
    catch(Exception ex){
      out.print(ladingSelProdBean.showJavaScript("alert('请选择的仓库非法！');window.close();"));
      return;
    }
    srcFrm = request.getParameter("srcFrm");
    multiIdInput = request.getParameter("srcVar");
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = ladingSelProdBean.getOneTable();
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
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = !form1.sel.checked;
  else
    form1.sel[row].checked = !form1.sel[row].checked;
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">产品明细选择</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right"><input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询 " onKeyDown="return getNextElement();">
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value=" 选用 " onKeyDown="return getNextElement();">
      <%}%>
        <input  type="button" class="button" onClick="window.close();" value=" 返回 " onKeyDown="return getNextElement();"></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td class="td" nowrap>编码</td>
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
      <td class="td" nowrap>回款提成率</td>
    </tr>
    <%int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr>
      <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=list.getValue("wzdjid")%>' onKeyDown="return getNextElement();"></td>
      <td nowrap class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("cpbm")%></td>
      <td nowrap class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("zjm")%></td>
      <td nowrap class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("product")%></td>
      <td nowrap align="center" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("jldw")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("kcsl")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("kckgl")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("jhdj")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("xsj")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("xsjzj")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("xstcl")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("hkts")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("hktcl")%></td>
    </tr>
    <% list.next();
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
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
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
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0 align="c" >
            <TR>
              <TD nowrap class="td">编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="cpbm" value='<%=ladingSelProdBean.getFixedQueryValue("cpbm")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap align="center">助记码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="zjm" value='<%=ladingSelProdBean.getFixedQueryValue("zjm")%>' onKeyDown="return getNextElement();">
              </TD>
            </TR>
            <TR>
              <TD nowrap class="td">条形码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="txm" value='<%=ladingSelProdBean.getFixedQueryValue("txm")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap align="center">图号</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="th" value='<%=ladingSelProdBean.getFixedQueryValue("th")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
            <TR>
              <TD nowrap class="td">品名规格</TD><INPUT type="hidden" name="cpid" value='<%=ladingSelProdBean.getFixedQueryValue("cpid")%>'>
              <TD colspan="3" nowrap class="td"><INPUT class="edline" style="WIDTH:300" name="product" value='<%=ladingSelProdBean.getFixedQueryValue("product")%>' readonly>
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=cpbm&srcVar=product','fieldVar=cpid&fieldVar=cpbm&fieldVar=product')"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';product.value='';"></TD>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center">
                <% String ss = "sumitFixedQuery("+Operate.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script="<%=ss%>" />
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                 <pc:shortcut key="x" script="hideFrame('fixedQuery')" />
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form><%out.print(retu);%>
</body>
</html>
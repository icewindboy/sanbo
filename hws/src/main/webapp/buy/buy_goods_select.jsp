<%--采购合同引入采购报价资料界面--%><%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
%><%
  engine.erp.buy.BuyGoodsSelect buyGoodsSelectBean = engine.erp.buy.BuyGoodsSelect.getInstance(request);
  String retu = buyGoodsSelectBean.doService(request, response);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//LookUp规格属性
  engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);//外币信息
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
  EngineDataSet list = buyGoodsSelectBean.getOneTable();
  String bjfs = null;
  bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
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
    <TD NOWRAP align="center">采购产品选择</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = 20; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right"><%--input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();"--%>
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value=" 选用 " onKeyDown="return getNextElement();">
      <%}%>
        <input  type="button" class="button" onClick="window.close();" value=" 返回(C)" onKeyDown="return getNextElement();">
       	  <pc:shortcut key="c" script='window.close();'/></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>产品编码</td>
      <td nowrap>品名规格</td>
      <td nowrap><%=isHsbj ? "换算单位" : "计量单位"%></td>
      <td nowrap>规格属性</td>
      <td nowrap>报价</td>
      <td nowrap>外币类别</td>
      <td nowrap>汇率</td>
      <td nowrap>外币报价</td>
      <td nowrap>优惠条件</td>
      <td nowrap>开始日期</td>
      <td nowrap>结束日期</td>
      <td nowrap>供应商料号</td>
      <td nowrap>备注</td>
      <td nowrap>制单日期</td>
    </tr>
    <%prodBean.regData(list,"cpid");
      wbBean.regData(list, "wbid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr onClick="checkTr(<%=i%>)">
    <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=list.getValue("cgbjid")%>' onKeyDown="return getNextElement();"></td>
      <td nowrap class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("cpbm")%><input type="hidden" id="cgbjid_<%=i%>" value='<%=list.getValue("cgbjid")%>'></td>
      <td nowrap class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("product")%></td>
      <td nowrap class="td" onClick="checkRadio(<%=i%>)"><%=isHsbj ? prodRow.get("hsdw") : prodRow.get("jldw")%></td>
     <td nowrap class="td" onClick="checkRadio(<%=i%>)"><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("bj")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=wbBean.getLookupName(list.getValue("bj"))%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("hl")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("wbbj")%></td>
      <td nowrap align="right" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("yhtj")%></td>
      <td nowrap align="center" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("ksrq")%></td>
      <td nowrap align="center" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("jsrq")%></td>
      <td nowrap align="center" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("gyslh")%></td>
      <td nowrap align="left" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("bz")%></td>
      <td nowrap align="center" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("czrq")%></td>
    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
    %>
    <tr>
      <td class="td"></td>
      <td class="td">&nbsp</td>
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
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
</form>
<%out.print(retu);%>
<script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</script>
<%--function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍后");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  //hideFrame('searchframe');
  showFrame('fixedQuery', true, "", true);
}
function sumitSearchFrame(oper)
{
  lockScreenToWait("处理中, 请稍后");
  searchform.operate.value = oper;
  searchform.submit();
}
function showSearchFrame(){
  hideFrame('fixedQuery');
  showFrame('searchframe', true, "", true);
}
</script>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0 align="c" >
            <TR>
              <TD class="td" nowrap>品名</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="cgbjid$pm" value='<%=buyGoodsSelectBean.getFixedQueryValue("cgbjid$pm")%>' maxlength='10' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" name="cgbjid$gg" value='<%=buyGoodsSelectBean.getFixedQueryValue("cgbjid$gg")%>' maxlength='10' onKeyDown="return getNextElement();">
              </TD>
              </TR>
              <TR>
              <TD align="center" nowrap class="td">往来单位</TD>
              <TD class="td" nowrap>
                <input class="EDLine" style="WIDTH:130px" name="dwmc" value='<%=corpBean.getLookupName(buyGoodsSelectBean.getFixedQueryValue("dwtxid"))%>' onKeyDown="return getNextElement();"readonly>
                <input type="hidden" name="dwtxid" value="<%=buyGoodsSelectBean.getFixedQueryValue("dwtxid")%>"> <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=dwmc','fieldVar=dwtxid&fieldVar=dwmc',fixedQueryform.dwtxid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';">
              </TD>
              </TR>
              <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=buyGoodsSelectBean.FIXED_SEARCH%>)" type="button" value=" 查询 " name="button" onKeyDown="return getNextElement();">
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 返回(C)" name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form><%out.print(retu);--%>
</body>
</html>

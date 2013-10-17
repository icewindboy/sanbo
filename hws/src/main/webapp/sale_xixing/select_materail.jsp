<%--移库单引入物资明细界面--%><%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.ArrayList"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
%><%
  engine.erp.sale.xixing.ImportMaterail importMaterailBean = engine.erp.sale.xixing.ImportMaterail.getInstance(request);
  String retu = importMaterailBean.doService(request, response);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
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
  EngineDataSet list = importMaterailBean.getOneTable();
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
<script language="javascript" src="../scripts/frame.js"></script>
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
function buttonEventF()
{
  sumitFixedQuery(<%=importMaterailBean.FIXED_SEARCH%>);
}
function buttonEventT()
{
  hideFrame('fixedQuery');
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">库存物资明细列表</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "97"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right"><%--input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询 " onKeyDown="return getNextElement();"--%>
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" title="选用(ALT+E)" value="选用(E)" onKeyDown="return getNextElement();">
        <pc:shortcut key="e" script='<%="selectProduct()"%>'/>
        <INPUT class="button" onClick="showFixedQuery()" type="button" title="查询(ALT+Q)" value="查询(Q)"  name="Query" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='<%="showFixedQuery()"%>'/>
    <%}%>
     <input  type="button" class="button" onClick="window.close();" title="返回(ALT+C)" value="返回(C)" onKeyDown="return getNextElement();">
      <pc:shortcut key="c" script='<%="window.close()"%>'/>
     </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>产品编码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>计量单位</td>
      <td nowrap>批号</td>
      <td nowrap>规格属性</td>
      <td nowrap>库存数量</td>
      <td nowrap>仓库</td>
      <td nowrap>库位</td>
    </tr>
    <%prodBean.regData(list,"cpid");
      storeAreaBean.regData(list,"kwid");
      propertyBean.regData(list,"dmsxid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr>
    <%--RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));--%>
      <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=list.getValue("wzmxid")%>' onKeyDown="return getNextElement();"></td>
      <td nowrap align="center" id="cpbm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("cpbm")%><input type="hidden" id="wzmxid_<%=i%>" value='<%=list.getValue("wzmxid")%>'></td>
      <td nowrap align="center" id="product_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("product")%></td>
      <td nowrap align="center" id="jldw_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("jldw")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="ph_<%=i%>" class="td"><%=list.getValue("ph")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="dmsxid_<%=i%>" class="td"><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="zl_<%=i%>" class="td" align="right"><%=list.getValue("zl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="ckmc_<%=i%>" class="td"><%=list.getValue("ckmc")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="kwid_<%=i%>" class="td"><%=storeAreaBean.getLookupName(list.getValue("kwid"))%></td>
    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
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
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
    <form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
              <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="cpbm" name="cpbm" value='<%=importMaterailBean.getFixedQueryValue("cpbm")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>品名规格</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="product" name="product" value='<%=importMaterailBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"></TD>
              </TR>
          </TABLE>
      <TR>
        <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=importMaterailBean.FIXED_SEARCH%>)" type="button" title="查询(ALT+F)" value="查询(F)" name="button" onKeyDown="return getNextElement();">
          <pc:shortcut key="f" script='<%="buttonEventF()"%>'/>
          <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" title="关闭(ALT+T)" value="关闭(T)" name="button2" onKeyDown="return getNextElement();">
           <pc:shortcut key="t" script='<%="buttonEventT()"%>'/>
        </TD>
      </TR>
    </TABLE>
  </DIV>
</form><%out.print(retu);%>
</body>
</html>


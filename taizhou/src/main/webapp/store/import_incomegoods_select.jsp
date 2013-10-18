<%--采购入库单引入采购进货单列表--%>
<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
%><%
  engine.erp.store.ImportIncomeOrderGoods ImportIncomeOrderGoodsBean = engine.erp.store.ImportIncomeOrderGoods.getInstance(request);
  String retu = ImportIncomeOrderGoodsBean.doService(request, response);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
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
  EngineDataSet list = ImportIncomeOrderGoodsBean.getOneTable();
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
    <TD NOWRAP align="center">外贸进/退货单货物清单</TD>
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
        <input name="button1" type="button" class="button" onClick="selectProduct();" title="选用(ALT+E)" value="选用(E)"  onKeyDown="return getNextElement();">
       <pc:shortcut key="e" script='<%="selectProduct()"%>'/>
      <%}%>
        <input  type="button" class="button" onClick="window.close();" title="返回(ALT+C)" value="返回(C)" onKeyDown="return getNextElement();">
       <pc:shortcut key="c" script='<%="window.close()"%>'/>
      </td>

    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>交货单编号</td>
      <td nowrap>交货日期</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>计量单位</td>
      <td nowrap>数量</td>
      <td nowrap>未入库数量</td>
      <%--td nowrap>单价</td>
      <td nowrap>金额</td--%>
      <td nowrap>供应商</td>
      <td nowrap>备注</td>
    </tr>
    <%prodBean.regData(list,"cpid");
      deptBean.regData(list,"deptid");
      corpBean.regData(list,"dwtxid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr>
    <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=list.getValue("jhdhwid")%>' onKeyDown="return getNextElement();"></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jhdbm_<%=i%>" class="td"><%=list.getValue("jhdbm")%><input type="hidden" id="jhdhwid_<%=i%>" value='<%=list.getValue("jhdhwid")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jhrq_<%=i%>" class="td"><%=list.getValue("jhrq")%></td>
      <td nowrap align="center" id="cpbm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("cpbm")%></td>
      <td nowrap align="center" id="product_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("product")%></td>
      <td nowrap align="center" id="jldw_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("jldw")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sl_<%=i%>" class="td" align="right"><%=list.getValue("sl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="wrksl_<%=i%>" class="td" align="right"><%=list.getValue("wrksl")%></td>
      <%--td nowrap onClick="checkRadio(<%=i%>)" id="dj_<%=i%>" class="td"><%=list.getValue("dj")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="je_<%=i%>" class="td"><%=list.getValue("je")%></td--%>
      <td nowrap onClick="checkRadio(<%=i%>)" id="dwtxid_<%=i%>" class="td"><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="bz_<%=i%>" class="td"><%=list.getValue("bz")%></td>
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
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
</form>
<%out.print(retu);%>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</body>
</html>
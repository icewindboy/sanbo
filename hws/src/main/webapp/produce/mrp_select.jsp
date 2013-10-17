<%--生产任务单引入物料需求界面--%><%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.ArrayList"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
%><%
  engine.erp.produce.ImportMrp importMrpBean = engine.erp.produce.ImportMrp.getInstance(request);
  String retu = importMrpBean.doService(request, response);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工序
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
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
  EngineDataSet list = importMrpBean.getOneTable();
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
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">物料需求列表</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "97"; pageContext.setAttribute(key, list);
       int iPage = 20; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right">
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value=" 选用 " onKeyDown="return getNextElement();">
      <input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
       <pc:shortcut key="q" script='showFixedQuery()'/>
      <%}%>
        <input  type="button" class="button" onClick="window.close();" value=" 返回(C)" onKeyDown="return getNextElement();"></td>
            <pc:shortcut key="c" script='window.close();'/>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>物料需求号</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>生产车间</td>
      <td nowrap>计量需求量</td>
      <td nowrap>未排任务量</td>
      <td nowrap>计量单位</td>
      <td nowrap>生产需求量</td>
      <td nowrap>未排任务生产量</td>
      <td nowrap>生产单位</td>
      <td nowrap>需求日期</td>
      <td nowrap>工艺类型</td>
      <td nowrap>存货性质</td>
      <td nowrap>加工要求</td>
    </tr>
    <%prodBean.regData(list,"cpid");
      deptBean.regData(list,"deptid");
      propertyBean.regData(list, "dmsxid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
        String chxzName = "chxz_"+i;
    %>
    <tr onClick="checkTr(<%=i%>)">
    <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=list.getValue("wlxqjhmxid")%>' onKeyDown="return getNextElement();"></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="wlxqh_<%=i%>" class="td"><%=list.getValue("wlxqh")%><input type="hidden" id="wlxqjhmxid_<%=i%>" value='<%=list.getValue("wlxqjhmxid")%>'></td>
      <td nowrap align="center" id="cpbm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("cpbm")%></td>
      <td nowrap align="center" id="product_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("product")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="dmsxid_<%=i%>" class="td"><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="deptid_<%=i%>" class="td"><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jlxql_<%=i%>" class="td"><%=list.getValue("jlxql")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="wprwl_<%=i%>" class="td"><%=list.getValue("wprwl")%></td>
      <td nowrap align="center" id="jldw_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("jldw")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="xql_<%=i%>" class="td"><%=list.getValue("xql")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="wprwscl_<%=i%>" class="td"><%=list.getValue("wprwscl")%></td>
      <td nowrap align="center" id="scydw_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("scydw")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="xqrq_<%=i%>" class="td"><%=list.getValue("xqrq")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="gylxid_<%=i%>" class="td"><%=technicsRouteBean.getLookupName(list.getValue("gylxid"))%></td>
      <td noWrap onClick="checkRadio(<%=i%>)" id="chxz_<%=i%>" class="td"><%=list.getValue("chxz").equals("1") ? "自制件" : (list.getValue("chxz").equals("3") ? "外协件" : "虚拟件")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jgyq_<%=i%>" class="td"><%=list.getValue("jgyq")%></td>
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
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
              <TR>
              <TD class="td" nowrap>物料需求号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="wlxqh" name="wlxqh" value='<%=importMrpBean.getFixedQueryValue("wlxqh")%>' onKeyDown="return getNextElement();"></TD>
              <TD align="center" nowrap class="td">产品名称</TD>
              <td nowrap class="td"><input class="EDLine" style="WIDTH:130px" name="pm" value='<%=prodBean.getLookupName(importMrpBean.getFixedQueryValue("cpid"))%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="cpid" value="<%=importMrpBean.getFixedQueryValue("cpid")%>">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=pm','fieldVar=cpid&fieldVar=pm',fixedQueryform.cpid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';pm.value='';">
              </td>
              </tr>
              <TR>
              <TD nowrap class="td">日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="rq$a" value='<%=importMrpBean.getFixedQueryValue("rq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(rq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="rq$b" style="WIDTH: 130px" name="rq$b" value='<%=importMrpBean.getFixedQueryValue("rq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(rq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
          </TABLE>
      <TR>
        <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=importMrpBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
           <pc:shortcut key="f" script='<%="sumitFixedQuery("+ importMrpBean.FIXED_SEARCH +",-1)"%>'/>
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
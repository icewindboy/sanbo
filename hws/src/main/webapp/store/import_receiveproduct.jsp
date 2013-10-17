<%--生产退料单引入生产领料单界面--%>
<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
%><%
  engine.erp.store.B_ImportReceive importReceiveBean = engine.erp.store.B_ImportReceive.getInstance(request);
  String retu = importReceiveBean.doService(request, response);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String operate = request.getParameter("operate");
  String isout = request.getParameter("isout");
  isout = isout != null ? isout : "0";
  String title = isout.equals("1") ? "外加工发料单物料列表" : "生产领料单物料列表";
  if(operate !=null && operate.equals(String.valueOf(Operate.INIT)))
  {
    srcFrm = request.getParameter("srcFrm");
    multiIdInput = request.getParameter("srcVar");
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = importReceiveBean.getOneTable();
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
    <TD NOWRAP align="center"><%=title%></TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "927"; pageContext.setAttribute(key, list);
       int iPage = 20; String pageSize = ""+iPage;%><pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right"><%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value=" 选用 " onKeyDown="return getNextElement();">
        <%}%>
        <input  type="button" class="button" onClick="window.close();" value=" 返回(C)" onKeyDown="return getNextElement();">
                  <pc:shortcut key="c" script='window.close();'/></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>领/发料单号</td>
      <td nowrap>仓库</td>
  　　 <td nowrap>领/发料日期</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名</td>
      <td nowrap>规格</td>
      <td nowrap>数量</td>
      <td nowrap>计量单位</td>
      <td nowrap>换算数量</td>
  　　<td nowrap>换算单位</td>
  　　<td nowrap>生产数量</td>
  　　<td nowrap>生产单位</td>
      <td nowrap>制单人</td>
    </tr>
    <%storeBean.regData(list,"storeid");
      propertyBean.regData(list, "dmsxid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr onClick="checkTr(<%=i%>)">
      <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=list.getValue("drawdetailid")%>' onKeyDown="return getNextElement();"></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="drawcode_<%=i%>" class="td"><%=list.getValue("drawcode")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="storeid_<%=i%>" class="td"><%=storeBean.getLookupName(list.getValue("storeid"))%> <input type="hidden" id="drawdetailid_<%=i%>" value='<%=list.getValue("drawdetailid")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="drawdate_<%=i%>" class="td"><%=list.getValue("drawdate")%></td>
      <td nowrap align="center" id="cpbm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("cpbm")%></td>
      <td nowrap align="center" id="pm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("pm")%></td>
      <td nowrap align="center" id="gg_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("gg")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="drawNum_<%=i%>" class="td"><%=list.getValue("drawNum")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jldw_<%=i%>" class="td"><%=list.getValue("jldw")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="drawBigNum_<%=i%>" class="td"><%=list.getValue("drawBigNum")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="hsdw_<%=i%>" class="td"><%=list.getValue("hsdw")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="produceNum_<%=i%>" class="td"><%=list.getValue("produceNum")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="scydw_<%=i%>" class="td"><%=list.getValue("scydw")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="creator_<%=i%>" class="td"><%=list.getValue("creator")%></td>
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
    </tr>
    <%}%>
  </table>
</form><SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
</SCRIPT>
<%out.print(retu);%>
</body>
</html>
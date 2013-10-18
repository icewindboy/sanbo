<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate ,java.util.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
  private boolean isMultiSelect = false;
  private String inputName[] = null;
  private String fieldName[] = null;
  private String methodName = null;
  private String curID = null;
  private String checktypeid = null;
%>
<%
  if(!loginBean.hasLimits("check_type", request, response))
    return;
  String operate = request.getParameter("operate");
if(operate !=null && operate.equals(String.valueOf(Operate.INIT)))
{
  srcFrm = request.getParameter("srcFrm");
  multiIdInput = request.getParameter("srcVar");
  isMultiSelect = request.getParameter("multi") != null && request.getParameter("multi").equals("1");
  inputName = request.getParameterValues("srcVar");
  fieldName = request.getParameterValues("fieldVar");
  methodName = request.getParameter("method");
  curID = request.getParameter("curID");
  checktypeid = request.getParameter("checktypeid");
  }
  engine.erp.quality.B_ReceiveSelect  ReceiveSelectBean = engine.erp.quality.B_ReceiveSelect.getInstance(request);
  //ReceiveSelectBean.curID=curID;
  ReceiveSelectBean.checktypeid=checktypeid;
  String retu = ReceiveSelectBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
  engine.project.LookUp productBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//产品编码
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//规格属性
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);//仓库
  engine.project.LookUp ReceiveBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_PRODUCE_IN);//收发单据类别-自制入库单
  engine.project.LookUp ProcessBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS);//生产加工单
  engine.project.LookUp proBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//产品编码
  engine.project.LookUp areaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);//仓库库位
  EngineDataSet list = ReceiveSelectBean.getOneTable();
  RowMap ds = ReceiveSelectBean.getRowinfo();
  String curUrl = request.getRequestURL().toString();
  //propertyBean.regData(list,"dmsxid");
  storeBean.regData(list,"storeid");
  ReceiveBean.regData(list,"sfdjlbid");
  ProcessBean.regData(list,"jgdmxid");
  areaBean.regData(list,"kwid");
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
function selectCorp(row)
{
<%if(isMultiSelect){%>
  var multiId = '';
  for(var i=0;i<form1.elements.length;i++)
  {
    var e = form1.elements[i];
    if(e.type == "checkbox" && e.name!='checkform' && e.checked == true)
      multiId += e.value+',';
  }
  if(multiId != ''){
    multiId += '-1';
  <%if(multiIdInput != null){
      String mutiId = "window.opener."+ srcFrm+"."+ multiIdInput;
      out.print(mutiId+".value=multiId;");
      out.print(mutiId+".onchange();");
      //"window.opener.sumitForm("+Operate.CUST_MULTI_SELECT+");");
    }%>
  }
<%}else{%>
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
  if(inputName != null && fieldName != null)
  {
    int length = inputName.length > fieldName.length ? fieldName.length : inputName.length;
    for(int i=0; i< length; i++)
    {
      out.println("obj = document.all['"+fieldName[i]+"_'+row];");
      out.print  ("window.opener."+ srcFrm+"."+inputName[i]);
      out.print(".value=");     out.println("obj."+ (fieldName[i].equals("receivedetailid")?"value;":"innerText;"));
    }
  }
  if(methodName != null)
    out.print("window.opener."+ methodName +";");
}
%>
window.close();
}
function checkRadio(row){
  selectRow();
  if(form1.sel.length+''=='undefined')
    form1.sel.checked = <%=isMultiSelect ? "!form1.sel.checked" : "true"%>;
  else
    form1.sel[row].checked = <%=isMultiSelect ? "!form1.sel[row].checked" : "true"%>;
}
function CustSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=20 left=50";
  var winName= "SingleCustSelector";
  paraStr = "../pub/corpselect.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&curID="+curID;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
</script>


<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>自制收货单明细</TD>
  </TR>
</TABLE>

<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>
<%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
      int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
</td>
 <TD align="right">
   <td class="td" align="right">
    <%if(list.getRowCount()>0){%>
      <input name="button1" type="button" class="button" onClick="selectCorp();" value=" 选用 " onKeyDown="return getNextElement();">
    <%}%>
      <input  type="button" class="button" onClick="window.close();" value=" 返回(C)" onKeyDown="return getNextElement();">
      <pc:shortcut key="c" script='window.close();'/></td>
 </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowarp width='30' align='center'></td>
      <td nowarp>自制收货单号</td>
      <td nowarp>收货日期</td>
      <td nowarp>仓库</td>
      <td nowarp>单据类别</td>
      <td nowarp>加工单号</td>
      <td nowarp>产品编码</td>
      <td nowarp>品名规格</td>
      <td nowarp>规格属性</td>
      <td nowarp>数量</td>
      <td nowarp>计量单位</td>
      <td nowarp>批号</td>
      <td nowarp>库位</td>
      <td nowarp>备注</td>
    </td>
    </tr>
    <%
    list.first();
    int i=0;
    for(;i<list.getRowCount();i++)
    {
    RowMap productRow =proBean.getLookupRow(list.getValue("cpid"));
    //System.out.println(list.getValue("kwid"));
    %>
    <tr onDblClick="checkRadio(<%=i%>);selectCorp(<%=i%>);">
<td class="td"><input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>></td>
<%--自制收货单号--%><td class="td" nowarp class=td  onClick="checkRadio(<%=i%>)" style="display:none">
                   <input type="hidden" name="receivedetailid_<%=i%>" value="<%=list.getValue("receivedetailid")%>">
                  </td>
     <td class="td" nowarp class=td id="receivecode_<%=i%>" onClick="checkRadio(<%=i%>)"><%=list.getValue("receivecode")%></td>
<%--收货日期--%><td class="td" nowarp class=td id="receivedate_<%=i%>" onClick="checkRadio(<%=i%>)"><%=list.getValue("receivedate")%></td>
<%--仓库--%><td class="td" nowarp class=td onClick="checkRadio(<%=i%>)"><%=storeBean.getLookupName(list.getValue("storeid"))%></td>
<%--单据类别--%><td class="td" nowarp class=td id="djlb_<%=i%>" onClick="checkRadio(<%=i%>)"><%=ReceiveBean.getLookupName(list.getValue("sfdjlbid"))%></td>
<%--加工单号--%><td class="td" nowarp class=td id="process_<%=i%>" onClick="checkRadio(<%=i%>)"><%=ProcessBean.getLookupName(list.getValue("jgdmxid"))%></td>
              <td class="td" nowarp  class=td id="cpid_<%=i%>" style="display:none"><%=list.getValue("cpid")%></td>
<%--产品编码--%><td class="td" nowarp class=td id="cpbm_<%=i%>" onClick="checkRadio(<%=i%>)"><%=productRow.get("cpbm")%></td>
<%--品名规格--%><td class="td" nowarp class=td id="pm_<%=i%>" onClick="checkRadio(<%=i%>)"><%=productBean.getLookupName(list.getValue("cpid"))%></td>

<td class="td" nowarp  class=td id="dmsxid_<%=i%>" style="display:none"><%=list.getValue("dmsxid")%></td>
<%--规格属性--%><td class="td" nowarp class=td id="sxz_<%=i%>" onClick="checkRadio(<%=i%>)"><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
<%--数量--%><td class="td" nowarp class=td id="sl_<%=i%>" onClick="checkRadio(<%=i%>)"><%=list.getValue("drawnum")%></td>
<%--计量单位--%><td class="td" nowarp class=td id="cpbm_<%=i%>" onClick="checkRadio(<%=i%>)"><%=productRow.get("jldw")%></td>
<%--批号--%><td class="td" nowarp class=td id="ph_<%=i%>" onClick="checkRadio(<%=i%>)"><%=list.getValue("batchno")%></td>
<%--库位--%><td class="td" nowarp class=td id="area_<%=i%>" onClick="checkRadio(<%=i%>)"><%=areaBean.getLookupName(list.getValue("kwid"))%></td>
<%--备注--%><td class="td" nowarp class=td id="memo_<%=i%>" onClick="checkRadio(<%=i%>)"><%=list.getValue("memo")%></td>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td">&nbsp;</td>
      <td class="td"></td>
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
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<%out.print(retu);%>
<div class="queryPop" id="detailDiv" name="detailDiv" style="width:240;height:240">
  <div class="queryTitleBox" align="right" style="width:240"><A onClick="hideFrame('detailDiv')" href="javascript:"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="240" height="260" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>
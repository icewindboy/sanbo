<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.*"%><%!
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
%>
<%
  engine.erp.produce.Select_Trackbill_of_lading Select_TrackBill_Of_LadingBean = engine.erp.produce.Select_Trackbill_of_lading.getInstance(request);
String pageCode = "sc_track_bill";
if(!loginBean.hasLimits(pageCode, request, response))
  return;
boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
//引用结算方式
engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
engine.project.LookUp guigeBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_SPEC_PROPERTY);
engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_STORE);
String retu = Select_TrackBill_Of_LadingBean.doService(request, response);
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
  isMultiSelect = request.getParameter("multi") != null && request.getParameter("multi").equals("1");
  inputName = request.getParameterValues("srcVar");
  fieldName = request.getParameterValues("fieldVar");
  methodName = request.getParameter("method");
}
String curUrl = request.getRequestURL().toString();
EngineDataSet list = Select_TrackBill_Of_LadingBean.getOneTable();

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
function sumitFixedQuery(oper)
{
  //执行查询,提交表单
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
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
      out.print(".value=");     out.println("obj."+ (fieldName[i].equals("drawDetailID")?"value;":"innerText;"));
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
      function masterproductCodeSelect(obj)
  {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=pm','fieldVar=cpid&fieldVar=cpbm&fieldVar=product',obj.value);
  }
  function masterproductNameSelect(obj)
  {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=pm','fieldVar=cpid&fieldVar=cpbm&fieldVar=product',obj.value);
  }
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">领料单明细选择</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
     <td height="21" nowrap class="td"><%String key = "dataset"; pageContext.setAttribute(key, list);   int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
      </td>
      <td class="td" align="right">

        <input name="button1" type="button" class="button" onClick="selectCorp();" value="选用(S)" onKeyDown="return getNextElement();">
        <pc:shortcut key="s" script='selectCorp();'/>
        <input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询 " onKeyDown="return getNextElement();">
        <input  type="button" class="button" onClick="window.close();" value="返回(X)" onKeyDown="return getNextElement();">
        <pc:shortcut key="X" script='window.close();'/>
     </td>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20></td>
      <td nowrap>领料批号</td>
      <td nowrap>品名</td>
      <td nowrap>规格</td>
      <td nowrap>仓库</td>
      <td nowrap>经手人</td>
      <td nowrap>制单人</td>
      <td nowrap>审核人</td>
      <td nowrap>检验员</td>

      <td nowrap>检验结果</td>
      <td nowrap>达因面</td>
      <td nowrap>热封面</td>

   </tr>
    <%
      guigeBean.regData(list,"dmsxID");
       prodBean.regData(list,"cpid");
       storeBean.regData(list,"storeid");
       int count = list.getRowCount();
       list.first();
       int i=0;
       for(; i<count; i++)   {
    %>
      <tr <%if(!isMultiSelect){%>onDblClick="checkRadio(<%=i%>);selectCorp(<%=i%>);"<%}%>>
      <td class="td"><input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>><input type="hidden" id="drawDetailID_<%=i%>" value='<%=list.getValue("drawDetailID")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="batchNo_<%=i%>" class="td"><%=list.getValue("batchNo")%></td>
      <td nowrap align="center" id="cpId_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodBean.getLookupName(list.getValue("cpId"))%></td>
      <td nowrap align="center" id="dmsxID_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=guigeBean.getLookupName(list.getValue("dmsxID"))%></td>
      <td nowrap align="center" id="storeid_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=storeBean.getLookupName(list.getValue("storeid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="handlePerson_<%=i%>" class="td"><%=list.getValue("handlePerson")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="creator_<%=i%>" class="td"><%=list.getValue("creator")%></td>
      <td nowrap align="center" id="approver_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("xm")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="checkor_<%=i%>" class="td"><%=list.getValue("checkor")%><input type="hidden" id="drawID_<%=i%>" value='<%=list.getValue("drawID")%>'></td>

      <td nowrap onClick="checkRadio(<%=i%>)" id="checkResult_<%=i%>" class="td"><%=list.getValue("checkResult")%>
      <td nowrap onClick="checkRadio(<%=i%>)" id="dyneSide_<%=i%>" class="td"><%=list.getValue("dyneSide")%>
      <td nowrap onClick="checkRadio(<%=i%>)" id="hotSide_<%=i%>" class="td"><%=list.getValue("hotSide")%>
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
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
  </form>
<SCRIPT LANGUAGE="javascript"> initDefaultTableRow('tableview1',1);
  function showFixedQuery(){
//点击查询按钮打开查询对话框
showFrame('fixedQuery', true, "", true);//显示层
}
</SCRIPT>
 <form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;">
    <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
    <div class="queryPop" id="fixedQuery" name="fixedQuery">
      <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
      <TABLE cellspacing=3 cellpadding=0 border=0>
        <TR>
          <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
             <td height='20' class="tdTitle" nowrap>品名规格

                  </td>
                  <td height='20' colspan="3" nowrap class="td"><input type="hidden" id="cpid" name="cpid" value='' >
                    <input type="text" class="edbox" style="width:65" onKeyDown="return getNextElement();" name="cpbm" value='' readonly>
                    <input type="text" class="edbox" style="width:180" id="pm" name="pm"  value='' readonly>

                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=cpbm&srcVar=pm','fieldVar=cpid&fieldVar=cpbm&fieldVar=product',fixedQueryform.cpid.value);">
                    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cpbm.value='';pm.value='';">

                  </td>
                  <td noWrap class="td" align="center">&nbsp;原料批号&nbsp;</td>
                 <td noWrap class="td"><input type="text" name="batchNo" value='<%=Select_TrackBill_Of_LadingBean.getFixedQueryValue("batchNo")%>' maxlength='50' style="width:120" class="edbox"></td>
           </TR>
          <TR>
          <td noWrap class="td" align="center">&nbsp;检验结果&nbsp;</td>
         <td noWrap class="td"><input type="text" name="checkResult" value='<%=Select_TrackBill_Of_LadingBean.getFixedQueryValue("check_result")%>' maxlength='50' style="width:120" class="edbox"></td>

          <td noWrap class="td" align="center">&nbsp;检验员&nbsp;</td>
         <td noWrap class="td"><input type="text" name="checkor" value='<%=Select_TrackBill_Of_LadingBean.getFixedQueryValue("checker")%>' maxlength='50' style="width:120" class="edbox"></td>

            </TR>
            <TR>

           <td noWrap class="td" align="center">&nbsp;经手人&nbsp;</td>
         <td noWrap class="td"><input type="text" name="handlePerson" value='<%=Select_TrackBill_Of_LadingBean.getFixedQueryValue("handler")%>' maxlength='50' style="width:120" class="edbox"></td>

        <td noWrap class="td" align="center">&nbsp;制单人&nbsp;</td>
         <td noWrap class="td"><input type="text" name="creator" value='<%=Select_TrackBill_Of_LadingBean.getFixedQueryValue("creator")%>' maxlength='50' style="width:120" class="edbox"></td>

            </TR>
             <TR>

          <td noWrap class="td" align="center">&nbsp;达因面&nbsp;</td>
         <td noWrap class="td"><input type="text" name="dyneSide" value='<%=Select_TrackBill_Of_LadingBean.getFixedQueryValue("dyne_side")%>' maxlength='50' style="width:120" class="edbox"></td>
           <td noWrap class="td" align="hot_side">&nbsp;热封面&nbsp;</td>
         <td noWrap class="td"><input type="text" name="hotSide" value='<%=Select_TrackBill_Of_LadingBean.getFixedQueryValue("hot_side")%>' maxlength='50' style="width:120" class="edbox"></td>
            </TR>
            <TR>
               <TD nowrap colspan=5 height=30 align="center">
                <% String ss = "sumitFixedQuery("+Operate.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script="<%=ss%>" />
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(X)" name="button2" onKeyDown="return getNextElement();">
                 <pc:shortcut key="x" script="hideFrame('fixedQuery')" />              </TD>
            </TR>
          </TABLE></TD>
        </TR>
      </TABLE>
    </DIV>
  </form>
<% out.print(retu);%>
</body>
</html>
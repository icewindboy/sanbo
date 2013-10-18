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
%>
<%
  engine.erp.produce.Select_Trackbilldetail_of_lading Select_TrackBilldetail_Of_LadingBean = engine.erp.produce.Select_Trackbilldetail_of_lading.getInstance(request);
  String pageCode = "sc_track_bill";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  //引用结算方式
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);

  engine.project.LookUp guigeBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  String retu = Select_TrackBilldetail_Of_LadingBean.doService(request, response);
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
  EngineDataSet list = Select_TrackBilldetail_Of_LadingBean.getOneTable();

  String gxmc=request.getParameter("gx");
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
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">自制收货单工序情况选择</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="90%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right">
        <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectCorp();" value="选用(S)" onKeyDown="return getNextElement();">

        <pc:shortcut key="s" script='selectCorp();'/>
        <%}%>
        <input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询 " onKeyDown="return getNextElement();">

        <input  type="button" class="button" onClick="window.close();" value="返回(X)" onKeyDown="return getNextElement();">
        <pc:shortcut key="X" script='window.close();'/>
    </td>
    </tr>
  </table>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20></td>
      <td nowrap>工序名称</td>
      <td nowrap>产品名称</td>
      <td nowrap>规格属性</td>
      <td nowrap>质量验证</td>
      <td nowrap>原断纸数</td>
      <td nowrap>断纸次数</td>
      <td nowrap>计划损耗</td>
      <td nowrap>实际损耗</td>
      <td nowrap>奖罚</td>
      <td nowrap>损耗原因</td>
      <td nowrap>机台</td>
      <td nowrap>班次</td>
      <td nowrap>批号</td>
      <td nowrap>经手人</td>
      <td nowrap>制单人</td>
      <td nowrap>审核人</td>
  </tr>
    <%
      guigeBean.regData(list,"dmsxID");
       prodBean.regData(list,"cpid");
      //prodBean.regData(list,"cpid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
        RowMap prodrow = prodBean.getLookupRow(list.getValue("cpId"));
    %>
    <tr <%if(!isMultiSelect){%>onDblClick="checkRadio(<%=i%>);selectCorp(<%=i%>);"<%}%>>
      <td class="td"><input type="radio" name="sel" onKeyDown="return getNextElement();" value="<%=i%>"<%=i==0?" checked":""%>></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="gx_<%=i%>" class="td"><%=list.getValue("gx")%><input type="hidden" name="receivedetailid_<%=i%>" value="<%=list.getValue("receivedetailid")%>"></td>
      <td nowrap align="center" id="pm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodrow.get("product")%></td>
      <div id="cpid_<%=i%>" style="display:none"><%=list.getValue("cpid")%></div>
      <div id="cpbm_<%=i%>" style="display:none"><%=prodrow.get("cpbm")%></div>
      <div id="hsbl_<%=i%>" style="display:none"><%=prodrow.get("hsbl")%></div>
      <div id="scdwgs_<%=i%>" style="display:none"><%=prodrow.get("scdwgs")%></div>

      <div id="dmsxid_<%=i%>" style="display:none"><%=list.getValue("dmsxID")%></div>
      <td nowrap align="center" id="sxz_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=guigeBean.getLookupName(list.getValue("dmsxID"))%></td>

      <td nowrap align="center" id="zlyz_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("zlyz")%></td>

      <td nowrap onClick="checkRadio(<%=i%>)" id="ydzs_<%=i%>" class="td"><%=list.getValue("ydzs")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="dzcs_<%=i%>" class="td"><%=list.getValue("dzcs")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jhsh_<%=i%>" class="td"><%=list.getValue("jhsh")%></td>
      <td nowrap align="center" id="sjsh_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("sjsh")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jf_<%=i%>" class="td"><%=list.getValue("jf")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="shyy_<%=i%>" class="td"><%=list.getValue("shyy")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jt_<%=i%>" class="td"><%=list.getValue("jt")%></td>
      <td nowrap align="center" id="bc_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("bc")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="batchno_<%=i%>" class="td"><%=list.getValue("batchNo")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="handleperson_<%=i%>" class="td"><%=list.getValue("handlePerson")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="creator_<%=i%>" class="td"><%=list.getValue("creator")%></td>
      <td nowrap align="center" id="approver_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("xm")%></td>
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
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
  </form>
<SCRIPT LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
//------------------------------以下与查询相关
function sumitFixedQuery(oper)
{
  //执行查询,提交表单
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  //点击查询按钮打开查询对话框
  <%if(hasSearchLimit){%>
   showFrame('fixedQuery', true, "", true);//显示层
   <%}%>
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
             <td height='20' class="tdTitle" nowrap>品名规格</td>
                  <td height='20' colspan="3" nowrap class="td"><input type="hidden" id="cpid" name="cpid" value='' >
                    <input type="text" class="edbox" style="width:65" onKeyDown="return getNextElement();" name="cpbm" value='' readonly>
                    <input type="text" class="edbox" style="width:180" id="pm" name="pm"  value='' readonly>

                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=cpbm&srcVar=pm','fieldVar=cpid&fieldVar=cpbm&fieldVar=product',fixedQueryform.cpid.value);">
                    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cpbm.value='';pm.value='';">
                 </td>

           </TR>
          <TR>
           <td noWrap class="td" align="center">&nbsp;批号&nbsp;</td>
           <td noWrap class="td"><input type="text" name="batchno" value='<%=Select_TrackBilldetail_Of_LadingBean.getFixedQueryValue("batchno")%>' maxlength='50' style="width:120" class="edbox"></td>
         <td noWrap class="td" align="center">&nbsp;班次&nbsp;</td>
          <td noWrap class="td"><input type="text" name="bc" value='<%=Select_TrackBilldetail_Of_LadingBean.getFixedQueryValue("bc")%>' maxlength='50' style="width:120" class="edbox"></td>
        </TR>
            <TR>

           <td noWrap class="td" align="center">&nbsp;经手人&nbsp;</td>
         <td noWrap class="td"><input type="text" name="handlePerson" value='<%=Select_TrackBilldetail_Of_LadingBean.getFixedQueryValue("handler")%>' maxlength='50' style="width:120" class="edbox"></td>

        <td noWrap class="td" align="center">&nbsp;制单人&nbsp;</td>
         <td noWrap class="td"><input type="text" name="creator" value='<%=Select_TrackBilldetail_Of_LadingBean.getFixedQueryValue("creator")%>' maxlength='50' style="width:120" class="edbox"></td>

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
<%out.print(retu);%>
</body>
</html>
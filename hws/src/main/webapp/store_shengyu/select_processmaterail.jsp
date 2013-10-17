<%--生产领料单引入加工单物料界面--%><%@ page contentType="text/html;charset=gb2312"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.ArrayList"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
%><%
  engine.erp.store.shengyu.B_ImportProcessMaterail importProcessMaterailBean = engine.erp.store.shengyu.B_ImportProcessMaterail.getInstance(request);
  String pageCode = "receive_material_list";
  if(!loginBean.hasLimits(pageCode, request, response))
  return;
  String retu = importProcessMaterailBean.doService(request, response);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
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
  String title = isout.equals("1") ? "外加工单物料清单列表" : "生产加工单物料清单列表";
  if(operate !=null && operate.equals(String.valueOf(Operate.INIT)))
  {
    srcFrm = request.getParameter("srcFrm");
    multiIdInput = request.getParameter("srcVar");
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = importProcessMaterailBean.getOneTable();
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
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
      <td height="21" nowrap class="td"><%String key = "97"; pageContext.setAttribute(key, list);
       int iPage = 20; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right"><%--input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询 " onKeyDown="return getNextElement();"--%>
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value=" 选用 " onKeyDown="return getNextElement();">
      <%}%>
        <INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询(Q)" name="Query" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/>
        <input  type="button" class="button" onClick="window.close();" value=" 返回(C)" onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script='window.close();'/></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>加工单号</td>
      <td nowrap>日期</td>
      <td nowrap>部门</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>数量</td>
      <td nowrap>未领数量</td>
      <td nowrap>计量单位</td>
      <%--td nowrap>生产数量</td>
      <td nowrap>未领生产数量</td>
      <td nowrap>生产单位</td--%>
      <td nowrap>存放仓库</td>
    </tr>
    <%deptBean.regData(list,"deptid");
      propertyBean.regData(list, "dmsxid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr onClick="checkTr(<%=i%>)" >
      <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=list.getValue("jgdwlid")%>' onKeyDown="return getNextElement();"></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jgdh_<%=i%>" class="td"><%=list.getValue("jgdh")%><input type="hidden" id="jgdwlid_<%=i%>" value='<%=list.getValue("jgdwlid")%>'></td>
      <td nowrap align="center" id="rq_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("rq")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="deptid_<%=i%>" class="td"><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td nowrap align="center" id="cpbm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("cpbm")%></td>
      <td nowrap align="center" id="product_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("product")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="dmsxid_<%=i%>" class="td"><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sl_<%=i%>" class="td" align="right"><%=list.getValue("sl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="wlsl_<%=i%>" class="td" align="right"><%=list.getValue("wlsl")%></td>
      <td nowrap align="center" id="jldw_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("jldw")%></td>
      <%--td nowrap onClick="checkRadio(<%=i%>)" id="scsl_<%=i%>" class="td" align="right"><%=list.getValue("scsl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="wlscsl_<%=i%>" class="td" align="right"><%=list.getValue("wlscsl")%></td>
      <td nowrap align="center" id="scydw_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=list.getValue("scydw")%></td--%>
      <td nowrap onClick="checkRadio(<%=i%>)" id="ckmc_<%=i%>" class="td" align="left"><%=list.getValue("ckmc")%></td>
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
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="wlxqh" name="wlxqh" value='<%=importProcessMaterailBean.getFixedQueryValue("wlxqh")%>' onKeyDown="return getNextElement();"></TD>
              <TD align="center" nowrap class="td">产品名称</TD>
              <td nowrap class="td"><input class="EDLine" style="WIDTH:130px" name="product" value='<%=prodBean.getLookupName(importProcessMaterailBean.getFixedQueryValue("cpid"))%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="cpid" value="<%=importProcessMaterailBean.getFixedQueryValue("cpid")%>">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.cpid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';product.value='';">
              </td>
              </tr>
              <TR>
              <TD nowrap class="td">日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="rq$a" value='<%=importProcessMaterailBean.getFixedQueryValue("rq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(rq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="rq$b" style="WIDTH: 130px" name="rq$b" value='<%=importProcessMaterailBean.getFixedQueryValue("rq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(rq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
          </TABLE>
      <TR>
        <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=importProcessMaterailBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
        <pc:shortcut key="f" script='<%="sumitFixedQuery("+ importProcessMaterailBean.FIXED_SEARCH +",-1)"%>'/>
          <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
           <pc:shortcut key="x" script='hide();'/>
        </TD>
      </TR>
    </TABLE>
  </DIV>
</form><%out.print(retu);%>
</body>
</html>


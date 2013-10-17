<%--销售出库单引入销售提单列表--%>
<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
%><%
  engine.erp.store.B_SingleOutLadding singleLaddingBean = engine.erp.store.B_SingleOutLadding.getInstance(request);
  String pageCode = "putoutlist";
  String retu = singleLaddingBean.doService(request, response);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);//LookUp仓库信息
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String operate = request.getParameter("operate");
  if(operate !=null )
  {
    srcFrm = singleLaddingBean.srcFrm;
    multiIdInput = request.getParameter("srcVar");
    String inputName[]  = singleLaddingBean.inputName;
    multiIdInput = inputName[1];
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = singleLaddingBean.getDetailTable();
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
    String mutiId = "parent.opener."+srcFrm+"."+multiIdInput;
    out.print(mutiId+".value=multiId;");
    out.print(mutiId+".onchange();");
    }%>
  }
  parent.close();
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
function buttonEventT()
{
  hideFrame('fixedQuery');
}
function buttonEventF()
{
  sumitFixedQuery(<%=singleLaddingBean.FIXED_SEARCH%>);
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
    <TD NOWRAP align="center">提单货物清单</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "97"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize()-6; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right"><%--input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询 " onKeyDown="return getNextElement();"--%>
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" title="选用(ALT+E)" value="选用(E)" onKeyDown="return getNextElement();">
                <pc:shortcut key="e" script='<%="selectProduct()"%>'/>
       <%}%><%--<input name="search2" type="button" class="button" onClick="showFixedQuery()" title="查询(ALT+Q)" value="查询(Q)" onKeyDown="return getNextElement();">
               <pc:shortcut key="q" script='<%="showFixedQuery()"%>'/>
       --%>
        <input  type="button" class="button" onClick="parent.close();;" title="返回(ALT+C)" value="返回(C)" onKeyDown="return getNextElement();">
             <pc:shortcut key="c" script='<%="parent.close();"%>'/>
       </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>提单编号</td>
      <td nowrap>提单日期</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>计量单位</td>
      <td nowrap>数量</td>
      <td nowrap>未提数量</td>
      <td nowrap>仓库</td>
      <td nowrap>备注</td>
    </tr>
    <%prodBean.regData(list,"cpid");
      //deptBean.regData(list,"deptid");
      corpBean.regData(list,"dwtxid");
      propertyBean.regData(list,"dmsxid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++){
    %>
    <tr onClick="checkTr(<%=i%>)">
    <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=list.getValue("tdhwid")%>' onKeyDown="return getNextElement();"></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="tdbh_<%=i%>" class="td"><%=list.getValue("tdbh")%><input type="hidden" id="tdhwid_<%=i%>" value='<%=list.getValue("tdhwid")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="tdrq_<%=i%>" class="td"><%=list.getValue("tdrq")%></td>
      <td nowrap align="center" id="cpbm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("cpbm")%></td>
      <td nowrap align="center" id="product_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("product")%></td>
      <td nowrap align="center" id="sxz_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
      <td nowrap align="center" id="jldw_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("jldw")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sl_<%=i%>" class="td" align="right"><%=list.getValue("sl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sjsl_<%=i%>" class="td" align="right"><%=list.getValue("sjsl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="storeid_<%=i%>" class="td"><%=storeBean.getLookupName(list.getValue("storeid"))%></td>
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
      <td class="td"></td><td class="td"></td>

    </tr>
    <%}%>
  </table>
</form>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
              <TR>
              <TD class="td" nowrap>提单编号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="tdbh" name="tdbh" value='<%=singleLaddingBean.getFixedQueryValue("tdbh")%>' onKeyDown="return getNextElement();"></TD>
              <TD align="center" nowrap class="td">产品名称</TD>
              <td nowrap class="td"><input class="EDLine" style="WIDTH:130px" name="cp_product" value='<%=prodBean.getLookupName(singleLaddingBean.getFixedQueryValue("cpid"))%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="cpid" value="<%=singleLaddingBean.getFixedQueryValue("cpid")%>">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=cp_product','fieldVar=cpid&fieldVar=product',fixedQueryform.cpid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cp_product.value='';">
              </td>
              </tr>
              <TR>
              <TD nowrap class="td">交货日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="tdrq$a" value='<%=singleLaddingBean.getFixedQueryValue("tdrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(tdrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="tdrq$b" style="WIDTH: 130px" name="tdrq$b" value='<%=singleLaddingBean.getFixedQueryValue("tdrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(tdrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="cpbm" name="cpbm" value='<%=singleLaddingBean.getFixedQueryValue("cpbm")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="product" name="product" value='<%=singleLaddingBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
          </TABLE>
      <TR>
        <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=singleLaddingBean.FIXED_SEARCH%>)" type="button" title="查询(ALT+F)" value="查询(F)" name="button" onKeyDown="return getNextElement();">
              <pc:shortcut key="f" script='<%="buttonEventF()"%>'/>
          <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" title="关闭(ALT+T)" value="关闭(T)" name="button2" onKeyDown="return getNextElement();">
               <pc:shortcut key="t" script='<%="buttonEventT()"%>'/>
        </TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%out.print(retu);%>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</body>
</html>
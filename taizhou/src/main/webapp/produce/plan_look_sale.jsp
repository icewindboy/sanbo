<%--生产计划引入销售合同货物界面--%>
<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
  private String selmethod = null;  //
%><%
  engine.erp.produce.PlanSelectSale planSelectSaleBean = engine.erp.produce.PlanSelectSale.getInstance(request);
  String pageCode = "produce_plan";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  String retu = planSelectSaleBean.doService(request, response);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
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
    selmethod = request.getParameter("selmethod");
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = planSelectSaleBean.getOneTable();
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
      String pref =  "window.opener."+srcFrm+".";
      out.print(pref + multiIdInput +".value=multiId;");
      out.print(pref + selmethod+".value=getMethodValue();");
      out.print(pref + multiIdInput +".onchange();");
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
function getMethodValue()
{
  var selmethodval = 0;
  if(form1.selmethod[0].checked)
    selmethodval = form1.selmethod[0].value;
  else if(form1.selmethod[1].checked)
    selmethodval = form1.selmethod[1].value;
  else if(form1.selmethod[2].checked)
    selmethodval = form1.selmethod[2].value;
  return selmethodval;
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
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">销售合同货物清单</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="100%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "97"; pageContext.setAttribute(key, list);
       int iPage = 21; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right"><%--input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询 " onKeyDown="return getNextElement();"--%>
      <input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
       <pc:shortcut key="q" script='showFixedQuery()'/>
        <input  type="button" class="button" onClick="window.close();" value=" 返回(C)" onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script='window.close();'/></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>合同编号</td>
      <td nowrap>单位名称</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名 规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>数量</td>
      <td nowrap>未计划数量</td>
      <td nowrap>计量单位</td>
      <td nowrap>交货日期</td>
      <td nowrap>备注</td>
    </tr>
    <%//prodBean.regData(list,"cpid");
      prodBean.regData(list,"cpid");
      corpBean.regData(list,"dwtxid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr>
      <td nowrap onClick="checkRadio(<%=i%>)" id="htbh_<%=i%>" class="td"><%=list.getValue("htbh")%></td>
      <td nowrap align="center" id="dwmc_<%=i%>" class="td" ><%=list.getValue("dwmc")%></td>
      <td nowrap align="center" id="cpbm_<%=i%>" class="td" ><%=list.getValue("cpbm")%></td>
      <td nowrap align="center" id="product_<%=i%>" class="td" ><%=list.getValue("product")%></td>
      <td nowrap  id="dmsxid_<%=i%>" class="td"><%=list.getValue("sxz")%></td>
      <td nowrap  id="sl_<%=i%>" class="td"><%=list.getValue("sl")%></td>
      <td nowrap  id="wjhsl_<%=i%>" class="td"><%=list.getValue("wjhsl")%></td>
      <td nowrap  class="td"><%=list.getValue("jldw")%></td>
      <td nowrap  id="jhrq_<%=i%>" class="td"><%=list.getValue("jhrq")%></td>
      <td nowrap id="bz_<%=i%>" class="td"><%=list.getValue("bz")%></td>

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
    </tr>
    <%}%>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
  function hide()
  {
    hideFrame('fixedQuery');
  }
  function productCodeSelect(obj)
{
   ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=q_cpbm&srcVar=q_product',
                    'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function productNameSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=q_cpbm&srcVar=q_product',
                    'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
function corpCodeSelect(obj)
{
  CustCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=q_dwdm&srcVar=q_dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpNameSelect(obj)
{
  alert(obj.value);
  CustNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=q_dwdm&srcVar=q_dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
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
              <TD class="td" nowrap>合同编号</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="htbh$a" name="htbh$a" value='<%=planSelectSaleBean.getFixedQueryValue("htbh$a")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>--</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="htbh$b" name="htbh$b" value='<%=planSelectSaleBean.getFixedQueryValue("htbh$b")%>' onKeyDown="return getNextElement();"></TD>
              </TR>
              <TR>
              <TD class="td" nowrap>单位编码</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="dwdm$a" name="dwdm$a" value='<%=planSelectSaleBean.getFixedQueryValue("dwdm$a")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>--</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="dwdm$b" name="dwdm$b" value='<%=planSelectSaleBean.getFixedQueryValue("dwdm$b")%>' onKeyDown="return getNextElement();"></TD>
              </TR>
              <%String cpid = planSelectSaleBean.getFixedQueryValue("cpid");
                String dwtxid = planSelectSaleBean.getFixedQueryValue("dwtxid");
                RowMap prodRow = prodBean.getLookupRow(cpid);
                RowMap corpRow = corpBean.getLookupRow(dwtxid);%>
              <tr>
             <td nowrap class='td'>产品</td>
             <td nowrap class='td'colspan='3'>
              <input class='edbox' style='width:70' id='q_cpbm' name='q_cpbm' value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this)">&nbsp;
              <input class='edbox' style='width:180' id='q_product' name='q_product' value='<%=prodRow.get("product")%>' onchange="productNameSelect(this)">
              <input type='hidden' name='cpid' value='<%=cpid%>'>
              <img style='cursor:hand' align='absmiddle' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=q_cpbm&srcVar=q_product','fieldVar=cpid&fieldVar=cpbm&fieldVar=product',fixedQueryform.cpid.value)"><img style='cursor:hand' align='absmiddle' src='../images/delete.gif' border=0 onClick="cpid.value='';q_cpbm.value='';q_product.value='';"></td>
              </tr>
              <tr>
             <td nowrap class='td'>客户</td>
             <td nowrap class='td'colspan='3'>
              <input class='edbox' style='width:70' id='q_dwdm' name='q_dwdm' value='<%=corpRow.get("dwdm")%>' onchange="corpCodeSelect(this)">&nbsp;
              <input class='edbox' style='width:180' id='q_dwmc' name='q_dwmc' value='<%=corpRow.get("dwmc")%>' onchange="corpNameSelect(this)">
              <input type='hidden' name='dwtxid' value='<%=dwtxid%>'>
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('fixedQueryform','srcVar=dwtxid&srcVar=q_dwdm&srcVar=q_dwmc','fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc',fixedQueryform.dwtxid.value)"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';q_dwdm.value='';q_dwmc.value='';"></TD>
              </tr>
              <TR>
              <TD class="td" nowrap>单位名称模糊</TD>
              <TD nowrap class="td"><INPUT class="edbox" style="WIDTH:130" id="dwmc" name="dwmc" value='<%=planSelectSaleBean.getFixedQueryValue("dwmc")%>' onKeyDown="return getNextElement();"></TD>
              <%--TD align="center" nowrap class="td">产品名称</TD>
              <td nowrap class="td"><input class="EDLine" style="WIDTH:130px" name="cp_product" value='<%=prodBean.getLookupName(planSelectSaleBean.getFixedQueryValue("cpid"))%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="cpid" value="<%=planSelectSaleBean.getFixedQueryValue("cpid")%>">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=cpid&srcVar=cp_product','fieldVar=cpid&fieldVar=product',fixedQueryform.cpid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cp_product.value='';">
              </td--%>
              </tr>
              <TR>
              <TD nowrap class="td">交货日期</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 130px" name="jhrq$a" value='<%=planSelectSaleBean.getFixedQueryValue("jhrq$a")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jhrq$a);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
              <TD class="td" nowrap align="center">--</TD>
              <TD class="td" nowrap><INPUT class="edbox" id="jhrq$b" style="WIDTH: 130px" name="jhrq$b" value='<%=planSelectSaleBean.getFixedQueryValue("jhrq$b")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                <A href="#"><IMG title=选择日期 onClick="selectDate(jhrq$b);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="cpbm" name="cpbm" value='<%=planSelectSaleBean.getFixedQueryValue("cpbm")%>' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="product" name="product" value='<%=planSelectSaleBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>状态</TD>
              <TD colspan="3" nowrap class="td">
                <%String zt = planSelectSaleBean.getFixedQueryValue("zt");%>
                <input type="radio" name="zt" value=""<%=zt.equals("")?" checked" :""%>>
                全部
                <input type="radio" name="zt" value="1"<%=zt.equals("1")?" checked" :""%>>
                已审
                <input type="radio" name="zt" value="8"<%=zt.equals("8")?" checked" :""%>>
                完成</TD>
                </TR>
            <%RowMap searchRow = planSelectSaleBean.getSearchRow();
              EngineDataSet dsSpec = planSelectSaleBean.getPropertyTable();
           %>
                       <%dsSpec.first();
              StringBuffer buf = new StringBuffer();
              for(int j=0; j<dsSpec.getRowCount(); j++)
              {
                buf.append("<TR><TD nowrap class='td'>").append(dsSpec.getValue("sxmc"));
                boolean isNumber = dsSpec.getValue("sxlx").equals("number");
                if(isNumber)
                {
                  buf.append("</TD><TD class='td'colspan='3' nowrap><input class=edbox  style='WIDTH:");
                  buf.append("88' maxlength=10 name='sxmc_").append(j).append("_min' value='");
                  buf.append(searchRow.get("sxmc_"+j+"_min")).append("' onKeyDown='return getNextElement();'> -- <input class=edbox style='WIDTH:");
                  buf.append("88' maxlength=10 name='sxmc_").append(j).append("_max' value='");
                  buf.append(searchRow.get("sxmc_"+j+"_max")).append("' onKeyDown='return getNextElement();'>");
                }
                else
                {
                  buf.append("</TD><TD class='td' nowrap><input class=edbox  style='WIDTH:");
                  buf.append("130' maxlength=20 name='sxmc_").append(j).append("' value='");
                  buf.append(searchRow.get("sxmc_"+j)).append("' onKeyDown='return getNextElement();'>");
                }
                buf.append("</TD></TR>");
                dsSpec.next();
              }
              out.print(buf.toString());
            %>
          </TABLE>
      <TR>
        <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=planSelectSaleBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
           <pc:shortcut key="f" script='<%="sumitFixedQuery("+ planSelectSaleBean.FIXED_SEARCH +",-1)"%>'/>
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
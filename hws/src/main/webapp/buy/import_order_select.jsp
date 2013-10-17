<%--采购进货单引入采购合同界面--%>
<%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  private String srcFrm=null;           //传递的原form的名称
  private String multiIdInput = null;   //多选的ID组合串
%><%
  engine.erp.buy.ImportOrder importOrderBean = engine.erp.buy.ImportOrder.getInstance(request);
  String retu = importOrderBean.doService(request, response);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
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
  EngineDataSet list = importOrderBean.getOneTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
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
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">合同货物清单</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td height="21" nowrap class="td"><%String key = "97"; pageContext.setAttribute(key, list);
       int iPage = 20; String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" align="right"><%--input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();"--%>
      <%if(list.getRowCount()>0){%>
        <input name="button1" type="button" class="button" onClick="selectProduct();" value=" 选用 " onKeyDown="return getNextElement();">
      <%}%>
        <input  type="button" class="button" onClick="window.close();" value=" 返回(C)" onKeyDown="return getNextElement();">
        	  <pc:shortcut key="c" script='window.close();'/></td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
      <td nowrap>合同编号</td>
      <td nowrap>交货日期</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名 规格</td>
      <td nowrap><%=isHsbj ? "换算单位" : "计量单位"%></td>
      <td nowrap><%=isHsbj ? "换算数量" : "数量"%></td>
      <td nowrap>单价</td>
      <td nowrap>金额</td>
      <td nowrap>已进货量</td>
      <td nowrap>未进货量</td>
      <td nowrap>规格属性</td>
      <td nowrap>存放仓库</td>
      <td nowrap>供应商资源号</td>
      <td nowrap>备注</td>
      <td nowrap>供应商</td>
    </tr>
    <%prodBean.regData(list,"cpid");
      deptBean.regData(list,"deptid");
      corpBean.regData(list,"dwtxid");
      propertyBean.regData(list, "dmsxid");
      int count = list.getRowCount();
      list.first();
      int i=0;
      for(; i<count; i++)   {
    %>
    <tr onClick="checkTr(<%=i%>)">
    <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=list.getValue("hthwid")%>' onKeyDown="return getNextElement();"></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="htbh_<%=i%>" class="td"><%=list.getValue("htbh")%><input type="hidden" id="hthwid_<%=i%>" value='<%=list.getValue("hthwid")%>'></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="jhrq_<%=i%>" class="td"><%=list.getValue("jhrq")%></td>
      <td nowrap align="center" id="cpbm_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("cpbm")%></td>
      <td nowrap align="center" id="product_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("product")%></td>
     <%if(isHsbj){%>
      <td nowrap align="right" id="hsdw_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"> <%=prodRow.get("hsdw")%></td>
      <%}else{%>
      <td nowrap align="center" id="jldw_<%=i%>" class="td" onClick="checkRadio(<%=i%>)"><%=prodRow.get("jldw")%></td>
      <%}%>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sl_<%=i%>" class="td" align="right"><%=list.getValue("sl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="dj_<%=i%>" class="td" align="right"><%=list.getValue("dj")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="je_<%=i%>" class="td" align="right"><%=list.getValue("je")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="sjjhl_<%=i%>" class="td" align="right"><%=list.getValue("sjjhl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="wjhl_<%=i%>" class="td" align="right"><%=list.getValue("wjhl")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="dmsxid_<%=i%>" class="td"><%=propertyBean.getLookupName(list.getValue("dmsxid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="storeid_<%=i%>" class="td"><%=storeBean.getLookupName(list.getValue("storeid"))%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="gyszyh_<%=i%>" class="td"><%=list.getValue("gyszyh")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="bz_<%=i%>" class="td"><%=list.getValue("bz")%></td>
      <td nowrap onClick="checkRadio(<%=i%>)" id="dwtxid_<%=i%>" class="td"><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td>
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
<%out.print(retu);%>
<script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</script>
</body>
</html>

<%--采购报价列表--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_provider="op_provider";
%><%String pageCode = "buyprice";

  engine.erp.buy.BuyApplyInfo buyPriceBean = engine.erp.buy.BuyApplyInfo.getInstance(request);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//规格属性
  engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);//外币信息
  String retu = buyPriceBean.doService(request, response);//location.href='baln.htm'

%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type"  content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/scripts/public.css" type="text/css">
</head>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/validate.js"></script>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/rowcontrol.js"></script>
<script language="javascript" src="<%=request.getContextPath() %>/scripts/tabcontrol.js"></script>

<script language="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value  = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum){
    var url = "buypriceedit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
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

   function hideInterFrame()//隐藏FRAME
   {
     lockScreenToWait("处理中, 请稍候！");
     hideFrame('detailDiv');
     form1.submit();
   }
   function hideFrameNoFresh(){
     hideFrame('detailDiv');
  }
  function corpCodeSelect(obj,srcVars)
 {
   ProvideCodeChange(document.all['prod'], obj.form.name, srcVars,
                     'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc',obj.value);
 }
 function corpNameSelect(obj,srcVars)
 {
   ProvideNameChange(document.all['prod'], obj.form.name, srcVars,
                     'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
 }
 function productCodeSelect(obj,srcVars)
 {
   ProdCodeChange(document.all['prod'], obj.form.name, srcVars,
                  'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
 }
 function productNameSelect(obj,srcVars)
 {
   ProdNameChange(document.all['prod'], obj.form.name, srcVars,
                  'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
  }
</script>
<%
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = buyPriceBean.getOneTable();
  String curUrl = request.getRequestURL().toString();

%>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD nowrap align="center">采购信息公告</TD>
  </TR></TABLE>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum"  VALUE="">
  <TABLE width="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
       <td class="td" nowrap>
     <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
</td>
      <TD align="right">
      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
       <td nowrap align="center" width=45></td>
      <td nowrap>产品编码</td>
      <td nowrap>品名</td>
      <td nowrap>规格</td>
      <td nowrap>数量</td>
      <td nowrap>计量单位</td>
      <td nowrap>需求日期</td>
    </tr>
     <%

       prodBean.regData(list,"cpid");
       wbBean.regData(list, "wbid");

       propertyBean.regData(list, "dwtxid");
       list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
        <td class="td">
        <input name="image2" class="img" type="image" title="修改" onClick="location.href='<%=request.getContextPath() %>/associate/supply/price/buyprice.jsp?cpid=<%=list.getValue("cpid")%>&operate=<%=engine.project.Operate.INIT%>&src=../pub/main.jsp'" src="<%=request.getContextPath() %>/images/edit.gif" border="0">
      </td>
      <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodRow.get("pm")%></td>
      <td class="td" nowrap><%=prodRow.get("gg")%></td>
      <td class="td" nowrap><%=list.getValue("sl")%></td>
      <td class="td" align="center" nowrap><%=prodRow.get("jldw")%></td>
      <td class="td" nowrap><%=list.getValue("xqrq")%></td>
</tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>

    </tr>
    <%}%>
  </table>
</form>
<script LANGUAGE="javascript">initDefaultTableRow('tableview1',1);

</script>


<%out.print(retu);%>
</body>
</html>

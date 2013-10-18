<%@ page contentType="text/html; charset=UTF-8" %>
<%request.setCharacterEncoding("UTF-8");
  //session.setMaxInactiveInterval(900);
  response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","max-age=0");
  response.addHeader("Cache-Control","pre-check=0");
  response.addHeader("Cache-Control","post-check=0");
  response.setDateHeader("Expires", 0);
  engine.common.OtherLoginBean loginBean = engine.common.OtherLoginBean.getInstance(request);
  if(!loginBean.isLogin(request, response))
    return;
%><%@ taglib uri="/WEB-INF/pagescontrol.tld" prefix="pc"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
  String op_provider="op_provider";
%><%String pageCode = "buyprice";

  engine.erp.buy.OtherBuyPrice buyPriceBean = engine.erp.buy.OtherBuyPrice.getInstance(request);
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
    <TD nowrap align="center">采购报价资料</TD>
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
        <INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询(Q)" name="Query" onKeyDown="return getNextElement();">
        <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=buyPriceBean.retuUrl%>'" value=" 返回(C)"border="0">
        <% String back ="location.href='"+buyPriceBean.retuUrl+"'" ;%>

      </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
       <td nowrap align="center" width=45></td>
      <td nowrap>产品编码</td>
      <td nowrap>品名</td>
      <td nowrap>规格</td>
      <td nowrap>计量单位</td>
      <td nowrap>原币报价</td>
      <td nowrap>外币类别</td>
      <td nowrap>汇率</td>
      <td nowrap>外币报价</td>
      <td nowrap>优惠条件</td>
      <td nowrap>开始日期</td>
      <td nowrap>结束日期</td>
      <td nowrap>供应商料号</td>
      <td nowrap>备注</td>
      <td nowrap>制单日期</td>
      <td nowrap>否历史报价</td>
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
        <td class="td"><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="<%=request.getContextPath() %>/images/edit.gif" border="0">
     <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="<%=request.getContextPath() %>/images/del.gif" border="0">
      </td>
      <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodRow.get("pm")%></td>
      <td class="td" nowrap><%=prodRow.get("gg")%></td>

      <td class="td" align="center" nowrap><%=prodRow.get("jldw")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("bj")%></td>
      <td class="td" align="right" nowrap><%=wbBean.getLookupName(list.getValue("wbid"))%></td>
      <td class="td" align="right" nowrap><%=list.getValue("hl")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("wbbj")%></td>
      <td class="td" nowrap><%=list.getValue("yhtj")%></td>
      <td class="td" nowrap><%=list.getValue("ksrq")%></td>
      <td class="td" nowrap><%=list.getValue("jsrq")%></td>
      <td class="td" nowrap><%=list.getValue("gyslh")%></td>
      <td class="td" nowrap><%=list.getValue("bz")%></td>
      <td class="td" nowrap><%=list.getValue("czrq")%></td>
      <td class="td" nowrap align='center'><%String sflsbj=list.getValue("sflsbj");if(sflsbj.equals("1")) out.print("是"); else out.print("否");%></td>
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
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap>&nbsp;</td>
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
  function hide()
  {
    hideFrame('fixedQuery');
  }
</script>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="<%=request.getContextPath() %>/images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>

              <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="cg_bj$cgbjid$cpbm" name="cg_bj$cgbjid$cpbm" value='' onKeyDown="return getNextElement();"></TD>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="cg_bj$cgbjid$product" name="cg_bj$cgbjid$product" value='' onKeyDown="return getNextElement();"></TD>
            </TR>

            </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center">
              <INPUT class="button" onClick="sumitFixedQuery(<%=buyPriceBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
               <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X) " name="button2" onKeyDown="return getNextElement();">
              </TD>
            </TR>
    </TABLE>
  </DIV>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="<%=request.getContextPath() %>/images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="550" height="320" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>

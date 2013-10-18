<%--生产外加工价格列表--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%String pageCode = "process_price";
  if(!loginBean.hasLimits("process_price", request, response))
    return;
  engine.erp.produce.B_ProcessPrice  b_ProcessPriceBean = engine.erp.produce.B_ProcessPrice.getInstance(request);

  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  String retu = b_ProcessPriceBean.doService(request, response);//location.href='baln.htm'
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type"  content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<SCRIPT language="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value  = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum){
    var url = "process_priceedit.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
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
</SCRIPT>
<%
  if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = b_ProcessPriceBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
%>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD nowrap align="center">外加工产品价格表</TD>
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
      <TD align="right"><%if(loginBean.hasLimits(pageCode, op_search)){%>
        <INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询(Q)" name="Query" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/>
        <%}%>
        <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=b_ProcessPriceBean.retuUrl%>'" value=" 返回(C)"border="0">
        <% String back ="location.href='"+b_ProcessPriceBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/> </TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
       <td nowrap align="center"><%if(loginBean.hasLimits(pageCode, op_add)){%><input name="image" class="img" type="image" title="新增(A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/><%}%></td>
      <td nowrap>加工厂</td>
      <td nowrap>产品编码</td>
      <td nowrap>商品名称</td>
      <td nowrap>计量单位</td>
      <td nowrap>换算单位</td>
      <td nowrap>单价</td>
      <td nowrap>优惠条件</td>
      <td nowrap>对方产品号</td>
      <td nowrap>备注</td>
    </tr>
     <%corpBean.regData(list, "dwtxid");
       prodBean.regData(list,"cpid");
       list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
        <td class="td"><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%if(loginBean.hasLimits(pageCode, op_delete)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      <%}%></td>
      <td class="td" nowrap><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td>
      <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
      <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodRow.get("product")%></td>
      <td class="td" align="center" nowrap><%=prodRow.get("jldw")%></td>
      <td class="td" align="center" nowrap><%=prodRow.get("hsdw")%></td>
      <td class="td" align="right" nowrap><%=list.getValue("dj")%></td>
      <td class="td" nowrap><%=list.getValue("yhtj")%></td>
      <td class="td" nowrap><%=list.getValue("dfcph")%></td>
      <td class="td" nowrap><%=list.getValue("bz")%></td>
    </tr>
    <%  list.next();
      }
      for(; i < loginBean.getPageSize(); i++){
    %>
    <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
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
              <TD align="center" nowrap class="td">加工厂</TD>
              <TD class="td" nowrap> <input class="EDLine" style="WIDTH:130px" name="dwmc" value='<%=corpBean.getLookupName(b_ProcessPriceBean.getFixedQueryValue("sc_wjgjg$dwtxid"))%>' onKeyDown="return getNextElement();"readonly>
                <input type="hidden" name="sc_wjgjg$dwtxid" value="<%=b_ProcessPriceBean.getFixedQueryValue("sc_wjgjg$dwtxid")%>">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProcessSingleSelect('fixedQueryform','srcVar=sc_wjgjg$dwtxid&srcVar=dwmc','fieldVar=dwtxid&fieldVar=dwmc',fixedQueryform.sc_wjgjg$dwtxid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="sc_wjgjg$dwtxid.value='';dwmc.value='';">
              </TD>
              </TR>
              <TR>
              <TD align="center" nowrap class="td">产品名称</TD>
              <td nowrap class="td"><input class="EDLine" style="WIDTH:130px" name="product" value='<%=b_ProcessPriceBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="sc_wjgjg$cpid" value="<%=b_ProcessPriceBean.getFixedQueryValue("sc_wjgjg$cpid")%>">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=sc_wjgjg$cpid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.sc_wjgjg$cpid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="sc_wjgjg$cpid.value='';product.value='';">
              </td>
            </TR>
            <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="sc_wjgjg$wjgjgid$cpbm" name="sc_wjgjg$wjgjgid$cpbm" value='<%=b_ProcessPriceBean.getFixedQueryValue("sc_wjgjg$wjgjgid$cpbm")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="sc_wjgjg$wjgjgid$product" name="sc_wjgjg$wjgjgid$product" value='<%=b_ProcessPriceBean.getFixedQueryValue("sc_wjgjg$wjgjgid$product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
            <tr>
              <TD class="td" nowrap>对方产品号</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" name="sc_wjgjg$dfcph" value='<%=b_ProcessPriceBean.getFixedQueryValue("sc_wjgjg$dfcph")%>' maxlength='20' onKeyDown="return getNextElement();"></TD>
            </tr>
          </TABLE>
      <TR>
        <TD colspan="4" nowrap class="td" align="center"><%if(loginBean.hasLimits(pageCode, op_search)){%>
           <INPUT class="button" onClick="sumitFixedQuery(<%=b_ProcessPriceBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
           <pc:shortcut key="f" script='<%="sumitFixedQuery("+ b_ProcessPriceBean.FIXED_SEARCH +",-1)"%>'/>
          <%}%><INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
          <pc:shortcut key="x" script='hide();'/>
        </TD>
      </TR>
    </TABLE>
  </DIV>
</form><%out.print(retu);%>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="320" height="340" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>
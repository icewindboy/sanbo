<%--生产工艺路线列表--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%>
<%String pageCode = "technics_route" ;
  engine.erp.produce.B_TechnicsRoute b_TechnicsRouteBean = engine.erp.produce.B_TechnicsRoute.getInstance(request);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp routeTypeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE_TYPE);
  if(!loginBean.hasLimits("technics_route", request, response))
    return;
  boolean isIe6 = request.getHeader("User-Agent").indexOf("MSIE 6") > -1;
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
  function showDetail(oper, row){
    if(row >= 0)
      selectRow();
    //lockScreenToWait("处理中, 请稍候！");
    document.all.interframe1.src='technics_route_particular.jsp?operate='+oper+'&rownum='+row;
    //unlockScreenWait();
  }
  function hideInterFrame()//隐藏FRAME
  {
    hideFrame('interframe1');
    form1.submit();
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
    <%if(!isIe6){%>
    document.all.tdiframe.style.display = 'none';
    <%}%>
  }
  function hideIframe()
  {
    <%if(!isIe6){%>
    document.all.tdiframe.style.display = 'block';
    <%}%>
  }
  function hideFixedQuery()
  {
    hideFrame('fixedQuery');
    <%if(!isIe6){%>
    document.all.tdiframe.style.display = 'block';
    <%}%>
  }

  function hideFrameNoFresh()
  {
    hideFrame('interframe1');
  }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true" >
<TABLE WIDTH="300" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD nowrap align="center">工艺路线设置</TD>
  </TR></TABLE>
  <%String retu = b_TechnicsRouteBean.doService(request, response);//location.href='baln.htm'
    if(retu.indexOf("location.href=")>-1)
    {
      out.print(retu);
      return;
    }
      EngineDataSet list = b_TechnicsRouteBean.getMaterTable();
      String curUrl = request.getRequestURL().toString();
      RowMap masterRow = b_TechnicsRouteBean.getMasterRowinfo();
%>
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
      <TD class="td" align="right"><%if(loginBean.hasLimits(pageCode, op_search)){%>
        <INPUT class="button" onClick="showFixedQuery()" type="button" value=" 查询(Q)" name="Query" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/>
        <%}%>
        <input name="button2" type="button" align="Right" class="button" onClick="location.href='<%=b_TechnicsRouteBean.retuUrl%>'" value=" 返回(C)" border="0">
        <% String back ="location.href='"+b_TechnicsRouteBean.retuUrl+"'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/>
      </TD>
    </TR>
  </TABLE>
  <TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center" width="95%">
  	<tr>
	<td valign="top">
  <div style="display:block;width:200 hight:300;overflow-x:auto;">
   <table id="tableview1" width="300" border="0" cellspacing="1" cellpadding="1" class="table" align="left">
   <tr class="tableTitle">
      <td nowrap width=30 align="center"><%if(loginBean.hasLimits(pageCode, op_add)){%><input name="image" class="img" type="image" title="新增(A)" onClick="showDetail(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
     <pc:shortcut key="a" script='<%="showDetail("+ Operate.ADD +",-1)"%>'/><%}%>
     </td>
      <td nowrap>产品编码</td>
      <td nowrap>品名规格</td>
      <td nowrap>工艺路线类型</td>
      </tr>
      <%routeTypeBean.regData(list,"gylxlxid");
       prodBean.regData(list,"cpid");
       list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
     %>
     <tr id="tr_<%=list.getRow()%>" onClick="showDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)">
     <td class="td" nowrap width=30 align="center">
     <input name="image" class="img" type="image" title="修改" onClick="showDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      </td>
            <%RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));%>
            <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
            <td class="td" nowrap><%=prodRow.get("product")%></td>
            <td class="td" nowrap><%=routeTypeBean.getLookupName(list.getValue("gylxlxid"))%></td>
          </tr>
      <%
        list.next();
        }
       for(; i < loginBean.getPageSize(); i++){
    %>
          <tr>
            <td class="td" nowrap>&nbsp;</td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
          </tr>
          <%}%>
        </table>
        </div>
    </td><td width="5" nowrap>&nbsp;</td><td width="100%" nowrap><div id="tdiframe" style="display:block;z-index:1;"><iframe id="interframe1" src="" style="display:block; position:absolute; z-index:1;" width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="auto"></iframe></div></td>
  </tr></table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1, false);
<%if(!b_TechnicsRouteBean.masterIsAdd()){
    int row = b_TechnicsRouteBean.getSelectedRow();
    if(row >= 0)
      out.print("showSelected('tr_"+ row +"');");
}%>
</SCRIPT>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFixedQuery()" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD align="center" nowrap class="td">产品名称</TD>
              <td nowrap class="td"><input class="EDLine" style="WIDTH:130px" name="product" value='<%=b_TechnicsRouteBean.getFixedQueryValue("product")%>' onKeyDown="return getNextElement();"readonly>
                <INPUT TYPE="HIDDEN" NAME="sc_gylx$cpid" value="<%=b_TechnicsRouteBean.getFixedQueryValue("sc_gylx$cpid")%>">
                <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=sc_gylx$cpid&srcVar=product','fieldVar=cpid&fieldVar=product',fixedQueryform.sc_gylx$cpid.value)">
                <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="sc_gylx$cpid.value='';product.value='';">
              </td>
            </TR>
            <TR>
              <TD class="td" nowrap>产品编码</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="sc_gylx$gylxid$cpbm" name="sc_gylx$gylxid$cpbm" value='<%=b_TechnicsRouteBean.getFixedQueryValue("sc_gylx$gylxid$cpbm")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
            <TR>
              <TD class="td" nowrap>品名规格</TD>
              <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:160" id="sc_gylx$gylxid$product" name="sc_gylx$gylxid$product" value='<%=b_TechnicsRouteBean.getFixedQueryValue("sc_gylx$gylxid$product")%>' onKeyDown="return getNextElement();"></TD>
            </TR>
            <TR>
            <TD class="td" nowrap>工艺路线类型</TD>
              <TD nowrap class="td"><pc:select name="sc_gylx$gylxlxid" addNull="1" style="width:160">
         <%=routeTypeBean.getList(b_TechnicsRouteBean.getFixedQueryValue("sc_gylx$gylxlxid"))%></pc:select>
           </TD>
           </TR>
      <TR>
        <TD nowrap class="td">生效时间</TD>
        <TD class="td" nowrap><INPUT class="edbox" style="WIDTH: 160px" name="sc_gylx$sxsj" value='<%=b_TechnicsRouteBean.getFixedQueryValue("sc_gylx$sxsj")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
          <A href="#"><IMG title=选择日期 onClick="selectDate(sc_gylx$sxsj);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A></TD>
      </TR>
      <TR>
        <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=b_TechnicsRouteBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
          <pc:shortcut key="f" script='<%="sumitFixedQuery("+ b_TechnicsRouteBean.FIXED_SEARCH +",-1)"%>'/>
          <INPUT class="button" onClick="hideFixedQuery()" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
     <pc:shortcut key="x" script='hideFixedQuery()'/>
        </TD>
      </TR>
      </TD>
      </TR>
      </TABLE>
    </TABLE>
  </div>
</form>
</body>
</html>
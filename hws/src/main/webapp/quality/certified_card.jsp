<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "certified_card";
%>
<%if(!loginBean.hasLimits("certified_card", request, response))
    return;
  engine.erp.quality.B_CertifiedCard  certifiedBean =engine.erp.quality.B_CertifiedCard.getInstance(request);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  String retu = certifiedBean.doService(request, response);
  HtmlTableProducer masterProducer = certifiedBean.table;//主表数据的表格打印

  engine.common.PdfProducerFacade pdf = engine.common.PdfProducerFacade.getInstance(request);
  pdf.reportDirPath = getServletContext().getRealPath("/WEB-INF/report");

%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>

<script language="javascript" src="../scripts/exchangeselect.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<SCRIPT LANGUAGE="javascript">
  function sumitFixedQuery(oper)
  {
    lockScreenToWait("处理中, 请稍候！");
    fixedQueryform.operate.value = oper;
    fixedQueryform.submit();
  }
  function showFixedQuery(){
    showFrame('fixedQuery', true, "", true);
  }
  function showSort(){
  hideFrame('fixedQuery')
  showFrame('divOrder', true, "", true);
}
  function sumitSearchFrame(oper)
  {
    lockScreenToWait("处理中, 请稍候！");
    searchform.operate.value = oper;
    searchform.submit();
  }
  function showSearchFrame(){
    hideFrame('fixedQuery');
    showFrame('searchframe', true, "", true);
  }
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function getColumnArray()
{
  sortform.sortColumnStr.value = fnGetOptionValue(',', sortColumns);
}
  function submitSort(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  getColumnArray();
  sortform.operate.value = oper;
  sortform.submit();
}
  function showInterFrame(oper, rownum)
  {
    var url = "certified_card_edit.jsp?operate="+oper+"&rownum="+rownum;
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
  function productCodeSelect(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
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
  function propertyNameSelect(obj,cpid, srcVar)
  {
    PropertyNameChange(document.all['prod'], obj.form.name, srcVar,
                   'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
//function showPdf()
  //{
  //  location.href='pdfprint.jsp?action=templet&operate=<%=pdf.SHOW_PDF%>';
 // }
  function refresh(){
    form1.submit();
  }
  </SCRIPT>
<jsp:include page="../pub/scan_bar.jsp" flush="true"/>
<BODY oncontextmenu="window.event.returnValue=true">
  <iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>产品合格证</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = certifiedBean.getOneTable();
  String curUrl = request.getRequestURL().toString();
  RowMap masterRow = certifiedBean.getRowinfo();
  RowMap row = certifiedBean.getRowinfo();
  //personBean.regConditionData(ds, "creatorID");
  //personBean.regData(ds,"creatorID");
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="97%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>
 <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
  int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
   <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
   <%propertyBean.regData(list, "dmsxid");
     prodBean.regData(list,"cpid");
     propertyBean.regData(list, "cpid");%>
    <td class="td" nowrap align="right">
     <%if(loginBean.hasLimits("certified_card", request, response)){%>
      <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
       <pc:shortcut key="q" script='showFixedQuery()'/><%--<script language='javascript'>GetShortcutControl(81,"showFixedQuery()");</script>--%>
    <%}%>
     <input name="sort" type="button" class="button" onClick="showSort()" value="排序(P)">
        <pc:shortcut key="p" script='showSort()'/>
     <%if(certifiedBean.retuUrl!=null){ String loc = "location.href='"+ certifiedBean.retuUrl+"';";%>
      <input name="button2" type="button" class="button" onClick="location.href='<%=certifiedBean.retuUrl%>'" value="返回(C)" onKeyDown="return getNextElement();">
       <pc:shortcut key="c" script='<%=loc%>'/><%}%></TD>
    </TR>
  </TABLE>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width="50"><%if(isCanAdd){%><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0"><%}%>
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/></td>
       <%masterProducer.printTitle(pageContext, "height='20'");%><!--打印表头-->
    </tr>
    <%//if(isCanEdit){
    //String onDblClick="showInterFrame("+out.print(Operate.EDIT)+","+out.print(list.getRow())+")";
    //}
    %>
    <%BigDecimal t_zsl = new BigDecimal(0), t_zhssl=new BigDecimal(0), t_zscsl=new BigDecimal(0);
      list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
        String zsl = list.getValue("saleNum");
        if(certifiedBean.isDouble(zsl))
          t_zsl = t_zsl.add(new BigDecimal(zsl));
        String zhssl = list.getValue("pageNum");
        if(certifiedBean.isDouble(zhssl))
          t_zhssl = t_zhssl.add(new BigDecimal(zhssl));
        String zscsl = list.getValue("produceNum");
        if(certifiedBean.isDouble(zscsl))
          t_zscsl = t_zscsl.add(new BigDecimal(zscsl));
        RowMap prodRow = prodBean.getLookupRow(list.getValue("cpid"));
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td" nowrap><%if(isCanEdit){%>
       <input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0"><%}%>
        <%if(isCanDelete){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0"><%}%>
      </td>
     <%masterProducer.printCells(pageContext, "Class=td");%><%--打印主表数据行--%>
    </tr>
    <%  list.next();
      }
      i=list.getRowCount()+1;
   %>
    <tr>
    <td class="tdTitle" nowrap>本页合计</td>
    <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
   <td class="td" nowrap></td>
   <td class="td" nowrap>&nbsp;</td>
   <td class="td" nowrap>&nbsp;</td>
    <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zsl%>' readonly></td>
    <td class="td" nowrap></td>
   <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zscsl%>' readonly></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
   <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=t_zhssl%>' readonly></td>
    <td class="td" nowrap></td>
   <td class="td" nowrap></td>
   <td class="td" nowrap></td>
     </tr>
    <tr>
    <td class="tdTitle" nowrap>总合计</td>
     <td class="td" nowrap>&nbsp;</td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
   <td class="td" nowrap></td>
   <td class="td" nowrap>&nbsp;</td>
   <td class="td" nowrap>&nbsp;</td>
    <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=certifiedBean.zsl%>' readonly></td>
    <td class="td" nowrap></td>
   <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=certifiedBean.zscsl%>' readonly></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
    <td class="td" nowrap></td>
   <td align="right" class="td"><input type="text" class="ednone_r" style="width:100%" value='<%=certifiedBean.zhssl%>' readonly></td>
    <td class="td" nowrap></td>
   <td class="td" nowrap></td>
   <td class="td" nowrap></td>
     </tr>
    <%
      for(; i < loginBean.getPageSize(); i++){
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
      <td class="td"></td>
      <td class="td"></td>
    </tr>
    <%}%>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
 <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
     <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
         <%certifiedBean.table.printWhereInfo(pageContext);%>
            <TR>
              <TD nowrap colspan=3 height=30 align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <INPUT class="button"  onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+Operate.FIXED_SEARCH+")"%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery');" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script="hideFrame('fixedQuery')"/>
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
  <div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv');refresh();" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="550" height="330" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</form>
<%--排序的查询框--%>
<form name="sortform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="sortColumnStr" VALUE="">
  <div class="queryPop" id="divOrder" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('divOrder')" href="javascript:"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD><TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD colspan="4" nowrap class="td"><table cellspac="0" cellpadding="0" width="100%" border="0">
                  <tr>
                    <%java.util.List fieldCodes = certifiedBean.orderFieldCodes;
                      java.util.List fieldNames = certifiedBean.orderFieldNames;
                      java.util.List selectedCodes = certifiedBean.selectedOrders;
                      StringBuffer buf = new StringBuffer("<script language='javascript'>var oOption;");
                      //TOption(type, value, text, isEnable, rowClick, checkName, isChecked, checkClick)
                      for(i=0; i<fieldCodes.size(); i++)
                      {
                        String fieldCode = (String)fieldCodes.get(i);
                        if(selectedCodes.indexOf(fieldCode) >= 0)
                          continue;
                        buf.append("oOption = new TOption('none','").append(fieldCode).append("',");
                        buf.append("'").append(fieldNames.get(i)).append("',true, null);");
                        buf.append("fnAddOption(showColumns, oOption);");
                      }
                      buf.append("</script>");
                      //
                      StringBuffer sot = new StringBuffer("<script language='javascript'>");
                      for(i=0; i<selectedCodes.size(); i++)
                      {
                        String fieldCode = (String)selectedCodes.get(i);
                        int index = fieldCodes.indexOf(fieldCode);
                        if(index < 0)
                          continue;
                        sot.append("oOption = new TOption('none','").append(fieldCode).append("',");
                        sot.append("'").append(fieldNames.get(index)).append("',true,null);");
                        sot.append("fnAddOption(sortColumns, oOption);");
                      }
                      sot.append("</script>");
                    %>
                    <td><table width="100%" cellspacing="0" cellpadding="0" border="0">
                        <TR>
                          <td class=td> <table cellSpacing="0" cellPadding="0" border="0" width="100%">
                              <tr>
                                <td class="td" valign="top" nowrap height="25"><b>可选列名</b></td>
                              </tr>
                              <tr>
                                <td><div style="overflow-y: auto; width: 120; height: 160; background-color: white; border: 1 solid #777777;" onselectstart="return CancelSelect();">
                                    <table id="showColumns" name="showColumns" width="100%" cellSpacing="0" cellPadding="0" border="0" onclick="SelectOption(this, false)" ondblclick="fnExchangeSelect(showColumns, sortColumns, false)">
                                    </table>
                                    <%=buf%> </div></td>
                              </tr>
                            </table></td>
                        </tr>
                      </table></td>
                    <td><table cellSpacing="0" cellPadding="0" border="0" height="135" width="100%">
                        <tr>
                          <td class="td" valign="top" nowrap height="25">&nbsp;</td>
                        </tr>
                        <tr>
                          <td height="55"></td>
                        </tr>
                        <tr>
                          <td><input name="button3" type="button" class="edbox" style="width:20;" onclick="fnExchangeSelect(showColumns, sortColumns, false)" value="&gt;" title="添加排序列名"></td>
                        </tr>
                        <tr>
                          <td height="2"></td>
                        </tr>
                        <tr>
                          <td><input name="button3" type="button" class="edbox" style="width:20;" onclick="fnRemoveMultiOption(sortColumns)" value="&lt;" title="移去排序列名"></td>
                        </tr>
                        <tr>
                          <td height="55"></td>
                        </tr>
                      </table></td>
                    <td><table cellSpacing="0" cellPadding="0" border="0" width="100%">
                        <tr>
                          <td class="td" valign="top" nowrap height="25"><b>排序列名</b></td>
                        </tr>
                        <tr>
                          <td><div style="overflow-y: auto; width: 120; height: 160; background-color: white; border: 1 solid #777777;" onselectstart="return CancelSelect();">
                              <table id="sortColumns" name="sortColumns" width="100%" cellSpacing="0" cellPadding="0" border="0" onclick="SelectOption(this)" ondblclick="fnExchangeSelect(sortColumns, showColumns, false)">
                              </table>
                              <%=sot%> </div></td>
                        </tr>
                      </table></td>
                </table></TD>
            </tr>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="submitSort(<%=engine.action.Operate.ORDERBY%>)" type="button" value=" 排序 " name="button">
                <INPUT class="button" onClick="hideFrame('divOrder');" type="button" value=" 关闭 " name="button2">
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%out.print(retu);%>
</body>
</html>
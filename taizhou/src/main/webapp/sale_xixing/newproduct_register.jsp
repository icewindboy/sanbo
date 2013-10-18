<%--新产品发货登记表--%><%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.html.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "newproduct_register";
%>
<%
  if(!loginBean.hasLimits("newproduct_register", request, response))
    return;
  engine.erp.sale.xixing.B_NewProductRegister b_NewProductRegisterBean  =  engine.erp.sale.xixing.B_NewProductRegister.getInstance(request);
  String retu = b_NewProductRegisterBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
  out.print(retu);
%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript" src="../scripts/frame.js"></script>
<SCRIPT LANGUAGE="javascript">
  function toDetail(oper, row){
    // 转到主从明细
    lockScreenToWait("处理中, 请稍候！");
    location.href='newproduct_register_edit.jsp?operate='+oper+'&rownum='+row;
  }
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
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
   }

 function corpCodeSelect(obj,srcVar)
{
  CustCodeChange(document.all['prod'], obj.form.name, srcVar,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function corpNameSelect(obj,srcVar)
{
  CustNameChange(document.all['prod'], obj.form.name, srcVar,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
//产品编码
function productCodeSelect(obj,srcVars)
{
   SaleProdCodeChange(document.all['prod'], obj.form.name, srcVars,'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
}
//产品名称
 function productNameSelect(obj,srcVars)
 {
   SaleProdNameChange(document.all['prod'], obj.form.name, srcVars,'fieldVar=cpid&fieldVar=cpbm&fieldVar=product', obj.value);
 }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>新产品发货登记表</TD>
  </TR>
</TABLE>
<%
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//引用产品
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//引用往来单位
  engine.project.LookUp saleProdBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_LADING_BILL);//提单货物
  EngineDataSet list = b_NewProductRegisterBean.getOneTable();
  HtmlTableProducer table = b_NewProductRegisterBean.masterProducer;//主表数据的表格打印
  String curUrl = request.getRequestURL().toString();
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE width="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
       <td class="td" nowrap>
        <%
          String key = "cpfhdjtable";
          pageContext.setAttribute(key, list);
          int iPage = loginBean.getPageSize();
          String pageSize = String.valueOf(iPage);
        %>
        <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
        </td>
        <td class="td" nowrap align="right">
        <input name="search2" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script="showFixedQuery()" />
        <%if(b_NewProductRegisterBean.retuUrl!=null){
          String s = "location.href='"+b_NewProductRegisterBean.retuUrl+"'";
           %>
         <input name="button22" type="button" class="button"  style="width:60"  onClick="location.href='<%=b_NewProductRegisterBean.retuUrl%>'" value=" 返回(C) " onKeyDown="return getNextElement();">
         <pc:shortcut key="c" script="<%=s%>" />
        <%}%>
        </td>
   </TR>
  </TABLE>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td>
      <%if(isCanAdd){%>
      <input name="image" class="img" type="image"  title="新增(A)"  onClick="toDetail(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <pc:shortcut key="a" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/>
      <%}%>
      </td>
      <td nowrap>单据号</td>
      <td nowrap>部门</td>
      <td nowrap>产品编码</td>
      <td nowrap>品名</td>
      <td nowrap>花号</td>
      <td nowrap>款式</td>
      <td nowrap>规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>客户名称</td>
      <td nowrap>单价</td>
      <td nowrap>计划数量</td>
      <td nowrap>发出数量</td>
      <td nowrap>清单号</td>
      <td nowrap>发货日期</td>
      <td nowrap>返单清单号</td>
      <td nowrap>返单日期</td>
      <td nowrap>返单数量</td>
    <%table.printTitle(pageContext, "height='20'");%><!--打印表头-->
    </tr>
    <%
      prodBean.regData(list,"cpid");
      propertyBean.regData(list,"dmsxid");
      corpBean.regData(list, "dwtxid");
      saleProdBean.regData(list, "tdhwid");
      int i=0;
      list.first();
      for(; i<list.getRowCount(); i++)
      {
        boolean isInit = false;
        String rowClass =list.getValue("zt");//0.初始化,1.已开提单(已经审核),9.出库
        if(rowClass.equals("0")){
          isInit = true;
          rowClass ="class=td";
        }
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        String loginid=b_NewProductRegisterBean.loginid;
        String sprid=list.getValue("sprid");
        String rowzt =list.getValue("zt");
        boolean cansubmit=false;
        boolean submitType = b_NewProductRegisterBean.submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
        String czyid = list.getValue("czyid");
        if(submitType&&czyid.equals(loginid))
          cansubmit=true;
        else if(!submitType)
          cansubmit=true;

        String cpid = list.getValue("cpid");
        RowMap productRow = prodBean.getLookupRow(cpid);
        RowMap propertyRow = propertyBean.getLookupRow(list.getValue("dmsxID"));
        RowMap corpRow =corpBean.getLookupRow(list.getValue("dwtxid"));
        boolean isCancer=rowzt.equals("1") && loginid.equals(sprid);
        RowMap saleprodRow =saleProdBean.getLookupRow(list.getValue("tdhwid"));
        String tdhwid = list.getValue("tdhwid");
        String xs__tdhwid = list.getValue("xs__tdhwid");
        RowMap xsprodpRow =saleProdBean.getLookupRow(list.getValue("xs__tdhwid"));
    %>
    <tr <%if(isCanEdit){%>onDblClick="toDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)"<%}%> onClick="selectRow()" >
      <td <%=rowClass%> align="center" nowrap>
      <%if(isCanEdit){%><input name="image2" class="img" type="image" title="修改" onClick="toDetail(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0"><%}%>
      <%if(isCanDelete&&rowzt.equals("0")){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0"><%}%>
      <%if(rowzt.equals("0")&&loginid.equals(czyid)){%><input name="image3" class="img" type="image" title='提交审批' onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
       <%if(isCancer&&tdhwid.equals("")&&xs__tdhwid.equals("")){%><input name="image3" class="img" type="image"  title='取消审批' onClick="if(confirm('确实要取消审批吗？'))sumitForm(<%=b_NewProductRegisterBean.CANCER_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}%>
      <%if(rowzt.equals("1")&&!tdhwid.equals("")){%><input name="image3" class="img" type="image" title='完成' onClick="if(confirm('确认完成吗？'))sumitForm(<%=b_NewProductRegisterBean.OVER%>,<%=list.getRow()%>)" src="../images/ok.gif" border="0"><%}%>

      </td>
      <td <%=rowClass%>  nowrap><%=list.getValue("djh")%></td>
      <td <%=rowClass%>  nowrap><%=deptBean.getLookupName(list.getValue("deptid"))%></td>
      <td <%=rowClass%>  nowrap><%=productRow.get("cpbm")%></td>
      <td <%=rowClass%>  height='24' nowrap><%=productRow.get("pm")%></td>
      <td <%=rowClass%>  nowrap><%=productRow.get("hh")%></td>
      <td <%=rowClass%>  nowrap><%=productRow.get("ks")%></td>
      <td <%=rowClass%>  height='24' nowrap><%=productRow.get("gg")%></td>
      <td <%=rowClass%>  nowrap><%=propertyRow.get("sxz")%></td>
      <td <%=rowClass%>  nowrap><%=corpBean.getLookupName(list.getValue("dwtxid"))%></td>
      <td <%=rowClass%>  nowrap><%=list.getValue("dj")%></td>
      <td <%=rowClass%>  nowrap><%=list.getValue("jhsl")%></td>
      <td <%=rowClass%>  nowrap><%=saleprodRow.get("sl")%></td>
      <td <%=rowClass%>  nowrap><%=saleprodRow.get("tdbh")%></td>
      <td <%=rowClass%>  nowrap><%=saleprodRow.get("tdrq")%></td>
      <td <%=rowClass%>  nowrap><%=xsprodpRow.get("tdbh")%></td>
      <td <%=rowClass%>  nowrap><%=xsprodpRow.get("tdrq")%></td>
      <td <%=rowClass%>  nowrap><%=xsprodpRow.get("sl")%></td>
       <%table.printCells(pageContext, rowClass);%><%--打印主表数据行--%>
    </tr>
    <%
      list.next();
      }
      for(; i < iPage; i++){
        out.print("<tr><td>&nbsp;</td>");
        out.print("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");
        out.print("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");
        out.print("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");
        table.printBlankCells(pageContext, "class=td");//打印空的格子
        out.print("</tr>");
      }
      %>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
         <TABLE cellspacing=3 cellpadding=0 border=0>
        <TR>
         <%b_NewProductRegisterBean.table.printWhereInfo(pageContext);%>
            <TR>
              <TD colspan="4" nowrap class="td" align="center">
                <% String qu = "sumitFixedQuery("+b_NewProductRegisterBean.FIXED_SEARCH+")";%>
                <INPUT class="button" onClick="sumitFixedQuery(<%=b_NewProductRegisterBean.FIXED_SEARCH%>)" type="button" value="查询(K)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="k" script='<%=qu%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value="关闭(x)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script="hideFrame('fixedQuery')"/>
            </td>
            </tr>
            </table>
              </TD>
            </TR>
    </TABLE>
  </DIV>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="320" height="325" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>
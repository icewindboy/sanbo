<%--库存盘点单主表--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%><%
  engine.erp.store.B_StoreCheck storeCheckBean = engine.erp.store.B_StoreCheck.getInstance(request);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);

  String pageCode = "store_check";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String loginId = storeCheckBean.loginId;
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
function toDetail(){
  location.href='store_check_edit.jsp';
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function showInterFrame(pdid, deptid,storeid,row){
  var url = "check_make_destroy.jsp?operate=0&pdid="+pdid+"&deptid="+deptid+"&storeid="+storeid+"&rownum="+row;
  document.all.interframe1.src = url;
  showFrame('detailDiv',true,"",true);
}
function hideFrameNoFresh()
  {
    hideFrame('detailDiv');
  }


</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR>
    <TD NOWRAP align="center">库存盘点单列表</TD>
  </TR></TABLE>
<%String retu = storeCheckBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  EngineDataSet list = storeCheckBean.getMaterTable();
  HtmlTableProducer table = storeCheckBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="100%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap><%String key = "1111"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right">
       <%if(loginBean.hasLimits(pageCode, op_add)){%>
        <input name="add" class="button" type="button" title="新增(ALT+A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" value="新增盘点单(A)">
       <pc:shortcut key="a" script='<%="sumitForm("+Operate.ADD+", -1)"%>'/>
      <%}%>
       <%if(hasSearchLimit){%><input name="search2" type="button" class="button" title="查询(ALT+Q)" value="查询(Q)" onClick="showFixedQuery()" onKeyDown="return getNextElement();">
       <pc:shortcut key="q" script="showFixedQuery()"/>
      <%}%>
        <%if(storeCheckBean.retuUrl!=null){%><input name="button22" type="button" class="button" title="返回(ALT+C)" value="返回(C)" onClick="location.href='<%=storeCheckBean.retuUrl%>'"  onKeyDown="return getNextElement();">
       <pc:shortcut key="c" script='buttonEventC()'/>
     <%}%></td>
    </tr>
  </table>
  <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center"></td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++){
        list.goToRow(i);
        boolean isInit = false;
        String rowClass =list.getValue("zt");
        if(rowClass.equals("0"))
          isInit = true;
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        //String sprid=list.getValue("sprid");
        String zt = list.getValue("zt");
        String sfdjid = list.getValue("sfdjid");
        //boolean isCancel = zt.equals("1");
        //isCancel =  isCancel && loginId.equals(sprid);//是否可以取消审批
    %>
    <%--02.19 22:42 去掉onclick事件,因为这张页面又要改成不是框架网页了 yjg--%>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick="selectRow();">
      <td <%=rowClass%> align="center" nowrap><input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit) ?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=i%>)" src="../images/edit.gif" border="0">
      <%--if(isInit){%><input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}--%>
      <%--if(isCancel){%><input name="image2" class="img" type="image" title='取消审批' onClick="if(confirm('是否取消审批该纪录？'))sumitForm(<%=storeCheckBean.CANCEL_APPROVE%>,<%=list.getRow()%>)" src="../images/clear.gif" border="0"><%}--%>
      <%--<input name="image" class="img" type="image" title=<%=sfdjid.equals("") ? "生成损溢单" : "查看损溢单"%> onClick="showInterFrame(<%=list.getValue("pdid")%>, <%=list.getValue("deptid")%>,<%=list.getValue("storeid")%>,<%=list.getRow()%>)"
         src='../images/edit.old.gif' border="0">
      --%>
    </td>
      <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%  list.next();
      }
      for(; i < iPage; i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td></tr>");
      }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
    <%if(!storeCheckBean.masterIsAdd()){
    int row = storeCheckBean.getSelectedRow();
    if(row >= 0)
      out.print("showSelected('tr_"+ row +"');");
  }%>
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  <%if(hasSearchLimit){%>showFrame('fixedQuery', true, "", true);<%}%>
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
//返回
  function buttonEventC()
  {
     location.href='<%=storeCheckBean.retuUrl%>';
  }
  //关闭查询小视窗
   function buttonEventT()
   {
     hideFrame('fixedQuery');
   }
   //产品编码选择
function productCodeSelect(obj, i)
{
ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=sfdjid$cpbm&srcVar=cpmc','fieldVar=cpbm&fieldVar=product', obj.value);
}
//产品名称改变时的选择
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=dwdm&srcVar=dwtxid&srcVar=buyerName','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value);
}
</SCRIPT>
<%if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0><%storeCheckBean.table.printWhereInfo(pageContext);%>
              <TD nowrap colspan=5 height=30 align="center"><INPUT  onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" class="button" title="查询(ALT+F)" value="查询(F)" name="button" onKeyDown="return getNextElement();">
                    <pc:shortcut key="f" script='<%="sumitFixedQuery("+Operate.FIXED_SEARCH+")"%>'/>
                <INPUT type="button" class="button"  title="关闭(ALT+T)" value="关闭(T)" onClick="hideFrame('fixedQuery')"  name="button2" onKeyDown="return getNextElement();">
                   <pc:shortcut key="t" script='<%="buttonEventT()"%>'/>
              </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
  <div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="800" height="400" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</form>
<%} out.print(retu);%>
</body>
</html>
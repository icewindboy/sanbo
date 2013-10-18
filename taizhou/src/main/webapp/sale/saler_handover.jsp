<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate, com.borland.dx.dataset.Locate,java.util.*"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.project.*,engine.erp.person.*,engine.html.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "saler_handover";
%>
<%
  engine.erp.sale.B_SalerHandover b_SalerHandoverBean = engine.erp.sale.B_SalerHandover.getInstance(request);
  String retu = b_SalerHandoverBean.doService(request, response);
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  LookUp personListBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
  String curUrl = request.getRequestURL().toString();
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);
  LookUp deptBeana = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//前
  LookUp deptBeanb = LookupBeanFacade.getInstance(request, SysConstant.BEAN_DEPT);//后
  HtmlTableProducer table = b_SalerHandoverBean.masterProducer;//主表数据的表格打印
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>

<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<SCRIPT LANGUAGE="javascript">
function toDetail()
  {
    location.href='saler_handover_edit.jsp';
  }
function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum){
    selectRow();
    var url = "saler_handover_view.jsp?operate="+oper+"&rownum="+rownum;
    document.all.interframe1.src = url;
    showFrame('detailDiv',true,"",true);
  }
  function hideInterFrame(){//隐藏FRAME
    hideFrame('detailDiv');
    form1.submit();
  }
  function hideFrameNoFresh(){
    hideFrame('detailDiv');
  }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<form name="form1" method="post" action="<%=curUrl%>"  onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
<TABLE WIDTH="90%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>业务员移交</TD>
  </TR>
</TABLE>
  <TABLE WIDTH="90%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td">
     <%
       String key = "loginmanagedata";
       EngineDataSet list = b_SalerHandoverBean.getMaterTable();
       pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize();
       String pageSize = ""+iPage;
     %>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"></pc:dbNavigator>
     </td>
      <TD align="right">
      <input name="button2" type="button" align="Right" title = "返回" class="button" onClick="location.href='<%=b_SalerHandoverBean.retuUrl%>'" value="返回(C)"border="0">
   </TD>
  </TR>
  </TABLE>
  <table id="tableview1" WIDTH="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap>
      <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
      </td>
      <%table.printTitle(pageContext, "height='20'");%><!--打印表头-->
    </tr>
    <%
      int i=0;
      list.first();
      for(; i < list.getRowCount(); i++) {
        String zt =list.getValue("zt");
        String rowClass =list.getValue("zt");
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
    %>
     <tr id="tr_<%=list.getRow()%>" onClick="selectRow();" <%if(isCanEdit){%>onDblClick="showInterFrame(<%=b_SalerHandoverBean.VIEW_DETAIL%>,<%=list.getRow()%>)"<%}%>>
      <td class="td" nowrap align="center">
      <input name="image2" class="img" type="image"  title='修改' onClick= "showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)"  src="../images/edit.gif" border="0">
      <%if(zt.equals("0")){%>
      <input name="image3" class="img" type="image" title='提交审批'onClick="sumitForm(<%=Operate.ADD_APPROVE%>,<%=list.getRow()%>)" src="../images/approve.gif" border="0"><%}%>
      <%if((loginBean.hasLimits(pageCode,op_delete)&&zt.equals("0"))){%>
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_SalerHandoverBean.DEL%>,<%=i %>)" src="../images/del.gif" border="0"><%}%>
      </td>
       <%table.printCells(pageContext, rowClass);%>
     </tr>
    <%
    list.next();
     }
     for(; i < iPage; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp</td>
      <td class="td">&nbsp</td>
      <td class="td">&nbsp</td>
      <td class="td">&nbsp</td>
      <td class="td">&nbsp</td>
      <td class="td">&nbsp</td>
    </tr>
    <%}%>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="500" height="400" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</body>
</html>
<%@page contentType="text/html; charset=UTF-8" %>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.math.BigDecimal,java.util.ArrayList,engine.html.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_cancel   = "op_cancel";
  String op_over = "op_over";
%>
<%
engine.erp.equipment.B_RecordCard recordCardBean=engine.erp.equipment.B_RecordCard.getInstance(request);
String pageCode = "record_card";
recordCardBean.billType="1";
recordCardBean.checkType="2";
if(!loginBean.hasLimits(pageCode, request, response))
  return;
boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);
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
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script language="javascript">
function toDetail(){
  location.href='record_card_edit.jsp';
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}

function equipmentSingleSelect(frmName, srcVar,fieldVar,methodName,code,name,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=930 height=570 top=0 left=0";
  var winName= "GoodsProdSelector";
  paraStr = "equipment_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&code="+code+"&name="+name;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
function equipmentCodeSelect(obj,srcVars)
{
  equipmentCodeChange(document.all['prod'], obj.form.name, srcVars,
                 'fieldVar=equipmentid&fieldVar=equipment_code&fieldVar=equipment_name', obj.value);
}
function equipmentNameSelect(obj,srcVars)
{
  equipmentNameChange(document.all['prod'], obj.form.name, srcVars,
                 'fieldVar=equipmentid&fieldVar=equipment_code&fieldVar=equipment_name', obj.value);
}

function equipmentCodeChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin)
{
  paraStr = "equipment_select.jsp?operate=53&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&code="+code;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  iframeObj.src=paraStr;
}

function equipmentNameChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin)
{
  paraStr = "equipment_select.jsp?operate=54&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&name="+code;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  iframeObj.src=paraStr;
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD NOWRAP align="center">设备履历卡</TD>
  </TR>
</TABLE>
<%String retu = recordCardBean.doService(request, response);
  if(retu.indexOf("toDetail();")>-1 || retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  //sengine.erp.quality.B_BuyCheck recordCardBean=engine.erp.quality.B_BuyCheck.getInstance(request);
  //engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp tooltypeBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_QUALITY_TOOLTYPE);
  //BEAN_PRODUCT_KIND
  engine.project.LookUp kindBean=engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT_KIND);
  EngineDataSet list = recordCardBean.getMaterTable();
  HtmlTableProducer table = recordCardBean.masterProducer;
  String curUrl = request.getRequestURL().toString();
  String loginId = recordCardBean.loginId;
  RowMap masterRow = recordCardBean.getMasterRowinfo();
  //deptBean.regData(list,"deptid");
    //kindBean.regData(list,"wzlbid");
  String SYS_APPROVE_ONLY_SELF =recordCardBean.SYS_APPROVE_ONLY_SELF;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  boolean isApproveOnly = SYS_APPROVE_ONLY_SELF.equals("1") ? true : false;//true仅制单人可提交
%>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
        <%String key = "datasetlist"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = String.valueOf(iPage);%>
        <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/></td>
      <td class="td" nowrap align="right">
        <%if(hasSearchLimit){%>
        <input name="search2" type="button" class="button" onClick="showFixedQuery()" value=" 查询(Q)" onKeyDown="return getNextElement();">
        <pc:shortcut key="q" script='showFixedQuery()'/>
        <%}%>
        <%if(recordCardBean.retuUrl!=null){%>
        <input name="button22" type="button" class="button" onClick="location.href='<%=recordCardBean.retuUrl%>'" value=" 返回(C)" onKeyDown="return getNextElement();">
        <% String back ="parent.location.href='"+recordCardBean.retuUrl+"'" ;%>
        <pc:shortcut key="c" script='<%=back%>'/>
        <%}%>
      </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap align="center">
        <%if(isCanAdd){%>
        <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add_big.gif" border="0">
        <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/>
        <%}%>
      </td>
      <%
        table.printTitle(pageContext, "height='20'");
      %>
    </tr>
    <%BigDecimal t_zsl = new BigDecimal(0), t_zje = new BigDecimal(0);
      list.first();
      int i=0;
      int count = list.getRowCount();
      for(; i<count; i++)   {
        list.goToRow(i);
        //String rowClass =list.getValue("state");
        String rowClass="0";
        rowClass = engine.action.BaseAction.getStyleName(rowClass);
        //String state =list.getValue("state");
        String state ="0";

        boolean isInit = false;
        if(state.equals("0"))isInit = true;
        boolean isShow = true;//isApproveOnly ? (loginId.equals(creatorid) && isInit) : isInit;//如果时只有制单人才可以提交审批。制单人等于登录人才显示
        boolean isCancelApprove =  state.equals("1");
        isCancelApprove =true; //isCancelApprove && loginId.equals(approverid);//是否可以取消审批
    %>
    <tr id="tr_<%=list.getRow()%>" onDblClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" onClick='selectRow()';>
      <td <%=rowClass%> align="center" nowrap> <input name="image2" class="img" type="image" title='<%=loginBean.hasLimits(pageCode, op_edit)?"修改" :"查看"%>' onClick="sumitForm(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
        <%table.printCells(pageContext, rowClass);%>
    </tr>
    <%  list.next();
      }
      i=count+1;
     %>
    <%
        for(; i < iPage; i++){
          out.print("<tr>");
          table.printBlankCells(pageContext, "class=td");
          out.print("<td>&nbsp;</td></tr>");
        }
    %>
  </table>
</form>
<SCRIPT LANGUAGE="javascript">
  initDefaultTableRow('tableview1',1);
<%if(!recordCardBean.masterIsAdd()){
  int row = recordCardBean.getSelectedRow();
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
    function hide()
    {
      hideFrame('fixedQuery');
    }
</SCRIPT>
<%if(hasSearchLimit){%>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <div class="queryPop" id="fixedQuery" name="fixedQuery">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <%recordCardBean.table.printWhereInfo(pageContext);%>
            </TR>
            <TR>
              <TD nowrap colspan=5 height=30 align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=Operate.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+ Operate.FIXED_SEARCH +",-1)"%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script='hide();'/> </TD>
            </TR>
          </TABLE></TD>
      </TR>
    </TABLE>
  </DIV>
</form>
<%} out.print(retu);%>
</body>
</html>

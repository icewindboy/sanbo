<%--车间工资计算方法--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal,engine.erp.baseinfo.BasePublicClass"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%
  String pageCode = "wage_calc_method";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.jit.WageCalcMethod wageCalcBean = engine.erp.jit.WageCalcMethod.getInstance(request);
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
  String retu = wageCalcBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;
%>
<jsp:include page="../baseinfo/script.jsp" flush="true"/>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<SCRIPT LANGUAGE="javascript">
  function sumitForm(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    form1.operate.value = oper;
    form1.rownum.value = row;
    form1.submit();
  }
  function showInterFrame(oper, rownum)
  {
    var url = "wage_calc_method_edit.jsp?operate="+oper+"&rownum="+rownum;
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
  function showFixedQuery()
  {
    showFrame('fixedQuery', true, "", true);
  }
  function deptChange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('fixedQueryform.deptid.value'), '',true);
   //associateSelect(document.all['prod1'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jsr', 'jsr', eval('form1.jsr.value'), '',true);
  }
  function sumitFixedQuery(oper, row)
  {
    lockScreenToWait("处理中, 请稍候！");
    fixedQueryform.operate.value = <%=Operate.FIXED_SEARCH%>;
    fixedQueryform.operate.value = oper;
    fixedQueryform.submit();
  }
  //返回
function buttonEventC()
{
  location.href='<%=wageCalcBean.retuUrl%>';
  }
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>车间工资计算方法</TD>
  </TR>
</TABLE>
<%
  EngineDataSet list = wageCalcBean.getOneTable();
  HtmlTableProducer table = wageCalcBean.masterProducer;
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  deptBean.regData(list,"deptid");
  personBean.regConditionData(list, "deptid");
  String curUrl = request.getRequestURL().toString();
%>
<form name="form1" method="post" action="<%=curUrl%>" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <TABLE WIDTH="90%" BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
    <td class="td" nowrap>
     <%String key = "ppdfsgg"; pageContext.setAttribute(key, list);
       int iPage = loginBean.getPageSize(); String pageSize = ""+iPage;%>
      <pc:dbNavigator dataSet="<%=key%>" pageSize="<%=pageSize%>"/>
   </td>

   <TD align="right">
    <%if(wageCalcBean.retuUrl!=null){%><input name="button2222232" type="button" align="Right"
   class="button" onClick="location.href='<%=wageCalcBean.retuUrl%>'" title="返回(ALT+C)"  value=" 返回(C) "border="0"><%}%>
      <pc:shortcut key="c" script="buttonEventC()"/>
   </TD>

    </TR>
  </TABLE>
  <table id="tableview1" width="90%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td width="45"><%if(loginBean.hasLimits(pageCode, op_add)){%><input name="image" class="img" type="image" title="新增" onClick="showInterFrame(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
      <%}%>
      </td>
      <%table.printTitle(pageContext, "height='20'");%>
    </tr>
    <%list.first();
      int i=0;
      for(; i<list.getRowCount(); i++)
      {
    %>
    <tr onDblClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)">
      <td class="td">
      <%if(loginBean.hasLimits(pageCode, op_edit)){%><input name="image2" class="img" type="image" title="修改" onClick="showInterFrame(<%=Operate.EDIT%>,<%=list.getRow()%>)" src="../images/edit.gif" border="0">
      <%}%>
      <%if(loginBean.hasLimits(pageCode, op_delete)){%><input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>,<%=list.getRow()%>)" src="../images/del.gif" border="0">
      <%}%></td>
      <td class="td">
    <%
      String deptid = list.getValue("deptid");
      String hourwage_calc = list.getValue("hourwage_calc");
      String piecewage_calc = list.getValue("piecewage_calc");
      String hourwageName = "";
      String priecewageName = "";
      out.println(deptBean.getLookupName(deptid));
      if (hourwage_calc.equals("1"))
          hourwageName = "固定计时工资";
        else if (hourwage_calc.equals("2"))
          hourwageName = "变动计时工资";

      if (piecewage_calc.equals("3"))
        priecewageName = "计件工资";
      else if (piecewage_calc.equals("4"))
        priecewageName = "日总产量工时平均工资(剔除计时)";
      else if (piecewage_calc.equals("5"))
        priecewageName = "日总产量人数平均工资(剔除计时) ";
    %>
      </td>
      <td class="td">
      <%=hourwageName%>
      </td>
     <td class="td">
      <%=priecewageName%>
      </td>
    </tr>
    <%  list.next();
      }
    %>
    <%
      for(; i < loginBean.getPageSize();i++){
        out.print("<tr>");
        table.printBlankCells(pageContext, "class=td");
        out.print("<td>&nbsp;</td></tr>");
      }
      %>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
</form>
<%out.print(retu);%>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" id="cc" align="right" style="width:300"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="300" height="250" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>
<%--车间工资计算方法--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%
  engine.erp.jit.WageCalcMethod wageCalcBean = engine.erp.jit.WageCalcMethod.getInstance(request);
  String pageCode = "wage_calc_method";
 if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
%>
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
    //form1.rownum.value = row;
    form1.submit();
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
  function deptChange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '',true);
   //associateSelect(document.all['prod1'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jsr', 'jsr', eval('form1.jsr.value'), '',true);
}
</SCRIPT>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prod1" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD colspan="6" align="center" NOWRAP>工资款项设置</TD>
  </TR>
</TABLE>
<%
  String retu = wageCalcBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;

  EngineDataSet ds = wageCalcBean.getOneTable();
  HtmlTableProducer table = wageCalcBean.masterProducer;
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  String curUrl = request.getRequestURL().toString();
  RowMap row = wageCalcBean.getRowinfo();
  boolean isEdit = wageCalcBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);
  String readonly = isEdit ? "" : "readonly";
  deptBean.regData(ds,"deptid");
  personBean.regConditionData(ds, "deptid");
  String jsr=row.get("jsr");
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" align="center" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle"><%=table.getFieldInfo("deptid").getFieldname()%></td>
      <td noWrap class="td"> <pc:select name="deptid" addNull="1" style="width:110">
        <%=deptBean.getList(row.get("deptid"))%> </pc:select> </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle"><%=table.getFieldInfo("hourwage_calc").getFieldname()%></td>
      <td noWrap class="td">
       <pc:select name="hourwage_calc" style="width:130" value='<%=row.get("hourwage_calc")%>'>
         <pc:option value=""></pc:option>
          <pc:option value="1">固定计时工资</pc:option>
          <pc:option value="2">变动计时工资</pc:option>
        </pc:select>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle"><%=table.getFieldInfo("piecewage_calc").getFieldname()%></td>
      <td noWrap class="td">
        <pc:select name="piecewage_calc" style="width:130" value='<%=row.get("piecewage_calc")%>'>
         <pc:option value=""></pc:option>
          <pc:option value="3">计件工资</pc:option>
          <pc:option value="4">日总产量工时平均工资(剔除计时)</pc:option>
          <pc:option value="5">日总产量人数平均工资(剔除计时)</pc:option>
        </pc:select>
      </td>
    </tr>
    <tr>
      <td colspan="4" noWrap class="tableTitle"><div align="center"><br>
          <%if(isEdit){%>
          <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存(S) " title="保存(ALT+S)">
           <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
          <%}%>
          <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(C) " title="关闭(ALT+C)">
            <pc:shortcut key="c" script="parent.hideFrame('detailDiv')"/>
        </div></td>
    </tr>
  </table>
</form>
<%out.print(retu);%>
</BODY>
</Html>
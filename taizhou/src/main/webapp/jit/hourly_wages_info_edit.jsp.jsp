<%--车间流转单--%>
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
  engine.erp.jit.HourlyWagesInfo hourWageInfoBean = engine.erp.jit.HourlyWagesInfo.getInstance(request);
  String pageCode = "hourly_wages_info";
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
  function showInterFrame(oper, rownum)
  {
    var url = "produce_useedit.jsp?operate="+oper+"&rownum="+rownum;
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
  String retu = hourWageInfoBean.doService(request, response);
  if(retu.indexOf("location.href=")>-1)
    return;

  EngineDataSet ds = hourWageInfoBean.getOneTable();
  HtmlTableProducer table = hourWageInfoBean.masterProducer;
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  String curUrl = request.getRequestURL().toString();
  RowMap row = hourWageInfoBean.getRowinfo();
  boolean isEdit = hourWageInfoBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);
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
      <td noWrap class="td"> <pc:select name="deptid" addNull="1" style="width:110" onSelect="deptChange()">
        <%=deptBean.getList(row.get("deptid"))%> </pc:select> </td>
      <td noWrap class="tableTitle"><%=table.getFieldInfo("personid").getFieldname()%></td>
      <td noWrap class="td"> <pc:select name="personid" addNull="1" style="width:110">
        <%=personBean.getList(row.get("personid"), "deptid", row.get("deptid"))%>
        </pc:select> </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle"><%=table.getFieldInfo("hour_unit").getFieldname()%></td>
      <td noWrap class="td"> <INPUT TYPE="TEXT" NAME="hour_unit" VALUE="<%=row.get("hour_unit")%>" SIZE="20" MAXLENGTH="<%=ds.getColumn("hour_unit").getPrecision()%>" CLASS="edbox" <%=readonly%>>
      </td>
      <td noWrap class="tableTitle"><%=table.getFieldInfo("hour_wage").getFieldname()%></td>
      <td noWrap class="td"> <INPUT TYPE="TEXT" NAME="hour_wage" VALUE="<%=row.get("hour_wage")%>" SIZE="20" MAXLENGTH="<%=ds.getColumn("hour_wage").getPrecision()%>" CLASS="edbox" <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle"><%=table.getFieldInfo("night_wage").getFieldname()%></td>
      <td noWrap class="td"> <INPUT TYPE="TEXT" NAME="night_wage" VALUE="<%=row.get("night_wage")%>" SIZE="20" MAXLENGTH="<%=ds.getColumn("night_wage").getPrecision()%>" CLASS="edbox" <%=readonly%>>
      </td>
      <td noWrap class="tableTitle"><%=table.getFieldInfo("over_wage").getFieldname()%></td>
      <td noWrap class="td"> <INPUT TYPE="TEXT" NAME="over_wage" VALUE="<%=row.get("over_wage")%>" SIZE="20" MAXLENGTH="<%=ds.getColumn("over_wage").getPrecision()%>" CLASS="edbox" <%=readonly%>>
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
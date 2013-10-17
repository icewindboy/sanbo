<%--生产工序名称编辑页面--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%String pageCode = "technics_name" ;
 if(!loginBean.hasLimits("technics_name", request, response))
    return;
  engine.erp.produce.B_TechnicsName  b_TechnicsNameBean  =  engine.erp.produce.B_TechnicsName.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script>
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">工序名称信息</TD>
  </tr></table>
<%String retu = b_TechnicsNameBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_TechnicsNameBean.getOneTable();
  RowMap row = b_TechnicsNameBean.getRowinfo();
  boolean isEdit = b_TechnicsNameBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);//在增加的时候又增加操作，否则必须有修改权限
  String readonly = isEdit ? "" : "readonly";
  engine.project.LookUp workProduceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_PROCEDURE);
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;工序编号</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="gybh" VALUE="<%=row.get("gybh")%>" SIZE="20" CLASS="edFocused" <%=readonly%>></td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;工序名称<em>*</em></td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="gymc" VALUE="<%=row.get("gymc")%>" SIZE="20" MAXLENGTH="<%=ds.getColumn("gymc").getPrecision()%>" CLASS="edFocused" <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;所属工段<em>*</em></td>
      <td noWrap class="td">
        <pc:select addNull="1" className="edFocused" name="gxfdid" style="width:130" disable='<%=isEdit?"0":"1"%>'>
        <%=workProduceBean.getList(row.get("gxfdid"))%></pc:select>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;工序描述</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="gyms" VALUE="<%=row.get("gyms")%>" SIZE="20" MAXLENGTH="<%=ds.getColumn("gyms").getPrecision()%>" CLASS="edFocused" <%=readonly%>>
      </td>
    </tr>
      <td colspan="2" noWrap class="tableTitle"><br><%if(isEdit){%>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
        <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
        <%}%><input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(X)">
         <pc:shortcut key="x" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
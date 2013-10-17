<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("storelist", request, response))
    return;
  engine.erp.baseinfo.B_Store storeBean = engine.erp.baseinfo.B_Store.getInstance(request);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">仓库信息</TD>
  </tr></table>
<%String retu = storeBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = storeBean.getOneTable();
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  RowMap row = storeBean.getRowinfo();
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
      <td noWrap class="tableTitle">&nbsp;仓库编码</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="ckbm" VALUE="<%=row.get("ckbm")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("ckbm").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;仓库名称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="ckmc" VALUE="<%=row.get("ckmc")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("ckmc").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;仓库全称</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="ckqc" VALUE="<%=row.get("ckqc")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("ckqc").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;仓库地址</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="ckdz" VALUE="<%=row.get("ckdz")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("ckdz").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;负责人</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="hzr" VALUE="<%=row.get("hzr")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("hzr").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;所属部门</td>
      <td noWrap class="td"><pc:select name="deptid" style="width:250"><%=deptBean.getList(row.get("deptid"))%>
        </pc:select>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;仓库电话</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="ckdh" VALUE="<%=row.get("ckdh")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("ckdh").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
     <tr>
      <td noWrap class="tableTitle">&nbsp;仓库传真</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="ckcz" VALUE="<%=row.get("ckcz")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("ckcz").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;备注</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="bz" VALUE="<%=row.get("bz")%>" SIZE="40" MAXLENGTH="<%=ds.getColumn("bz").getPrecision()%>" CLASS="edbox">
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" accessKey='s' value="保存(S)">
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()"  accessKey='s'value="关闭(C)">
      </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
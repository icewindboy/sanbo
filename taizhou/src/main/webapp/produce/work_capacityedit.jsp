<%--生产工作能力编辑页面--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate"%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%>
<%String pageCode="work_capacity";
  if(!loginBean.hasLimits("work_capacity", request, response))
    return;
  engine.erp.produce.B_WorkCapacity  b_WorkCapacityBean = engine.erp.produce.B_WorkCapacity.getInstance(request);
  engine.project.LookUp firstkindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_FIRST_KIND);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp workCenterBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_CENTER);
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.submit();
}
function productCodeSelect(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function productNameSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr>
    <TD NOWRAP align="center">生产能力信息</TD>
  </tr></table>
<%String retu = b_WorkCapacityBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
  return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_WorkCapacityBean.getOneTable();
  RowMap row = b_WorkCapacityBean.getRowinfo();
  boolean isEdit = b_WorkCapacityBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);//在增加的时候又增加操作，否则必须有修改权限
  String readonly = isEdit ? "" : "readonly";
  String tableClass = isEdit ? "edbox" : "edline";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
 <INPUT TYPE="HIDDEN" NAME="cpid" value='<%=row.get("cpid")%>'>
  <table BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
    <%RowMap prodRow=prodBean.getLookupRow(row.get("cpid"));%>
    <td noWrap class="tableTitle">产品编码</td>
      <td nowrap class="td">
     <input type="text" onKeyDown="return getNextElement();" name="cpbm" value='<%=prodRow.get("cpbm")%>' style="width:180" class="<%=tableClass%>" onchange="productCodeSelect(this)" <%=readonly%>>
     <img style='cursor:hand' title='单选物资' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw',form1.cpid.value)">
    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cpbm.value='';product.value='';jldw.value='';"></td>
     </td>
    <tr>
      <td noWrap class="tableTitle">品名规格</td>
      <td nowrap class="td">
     <input type="text" onKeyDown="return getNextElement();" name="product" value='<%=prodRow.get("product")%>' style="width:180" class="<%=tableClass%>" onchange="productNameSelect(this)" <%=readonly%>>
   </td>
    </tr>
     <tr>
      <td noWrap class="tableTitle">计量单位</td>
      <td nowrap class="td"><input class="edline" name="jldw" value='<%=prodRow.get("jldw")%>' style="width:180" class="edline" onKeyDown="return getNextElement();" readonly>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">物资类别</td>
      <td noWrap class="td">
     <%if(!isEdit)out.print("<input type='text' value='"+firstkindBean.getLookupName(row.get("wzlbid"))+"' style='width:160' class='ednone' readonly>");
     else {%>
        <pc:select name="wzlbid" addNull="1" style="width:180">
      <%=firstkindBean.getList(row.get("wzlbid"))%> </pc:select>
     <%}%>
       </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">工作中心</td>
      <td noWrap class="td">
     <%if(!isEdit)out.print("<input type='text' value='"+workCenterBean.getLookupName(row.get("gzzxid"))+"' style='width:160' class='ednone' readonly>");
     else {%>
      <pc:select name="gzzxid" addNull="1" style="width:180">
      <%=workCenterBean.getList(row.get("gzzxid"))%> </pc:select>
     <%}%></td>
    </tr>
    <tr>
    <td noWrap class="tableTitle">提前期</td>
      <TD class="td" nowrap><INPUT class="<%=tableClass%>" onKeyDown="return getNextElement();" style="WIDTH: 180px" name="tqq" value='<%=row.get("tqq")%>' maxlength='10' <%=readonly%>>
      </td>
    </tr>
    <tr>
    <td noWrap class="tableTitle">设备数</td>
      <TD class="td" nowrap><INPUT class="<%=tableClass%>" onKeyDown="return getNextElement();" style="WIDTH: 180px" name="sbs" value='<%=row.get("sbs")%>' maxlength='10' onchange='reshow();' <%=readonly%>>
      </td>
    </tr>
	<tr>
    <td noWrap class="tableTitle">人工数</td>
      <TD class="td" nowrap><INPUT class="<%=tableClass%>" onKeyDown="return getNextElement();" style="WIDTH: 180px" name="rgs" value='<%=row.get("rgs")%>' maxlength='10' onchange='reshow();' <%=readonly%>>
      </td>
    </tr>
    <tr>
    <td noWrap class="tableTitle">日工时</td>
      <TD class="td" nowrap><INPUT class="<%=tableClass%>" onKeyDown="return getNextElement();" style="WIDTH: 180px" name="rs" value='<%=row.get("rs")%>' maxlength='10' <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">产量</td>
      <td noWrap class="td"><INPUT TYPE="TEXT" NAME="cl" onKeyDown="return getNextElement();" VALUE="<%=row.get("cl")%>" style="width:180" class="<%=tableClass%>" onchange='reshow();' <%=readonly%>>
      </td>
    </tr>
    <tr>
      <td colspan="2" noWrap class="tableTitle"><br>
        <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
     <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
        <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(X)">
        <pc:shortcut key="x" script='parent.hideFrameNoFresh()'/>
      </td>
    </tr>
  </table>
</form>
<script language='javascript'>
  function reshow()
{
  var objsbs = form1.sbs;
  var objrgs = form1.rgs;
  var objcl = form1.cl;
  if(isNaN(objsbs.value))
  {
    alert('非法设备数');
    return;
  }
  sbs = parseInt(objsbs.value);
  if(sbs<1)
  {
    alert('设备数不能小于1');
    return;
  }
  if(isNaN(objrgs.value))
  {
   alert('非法人工数');
   return;
  }
  rgs = parseInt(objrgs.value);
   if(rgs<1)
   {
     alert('人工数不能小于1');
     return;
    }
    if(isNaN(objcl.value))
    {
      alert('非法产量');
   return;
    }
    cl = parseInt(objcl.value);
  if(cl<1)
  {
    alert('产量不能小于1');
    return;
  }
}
reshow();
</script>
</BODY>
</Html>
<%--业务员奖金设置--%><%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*, engine.project.Operate,java.util.ArrayList"%>
<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("saler_prix_set", request, response))
    return;
  engine.erp.sale.B_SalerPrixSet b_SalerPrixSetBean  =  engine.erp.sale.B_SalerPrixSet.getInstance(request);

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
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0 CLASS="headbar"><tr><TD NOWRAP align="center">奖金款项设置</TD></tr></table>
<%
  String retu = b_SalerPrixSetBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_SalerPrixSetBean.getOneTable();
  RowMap row = b_SalerPrixSetBean.getRowinfo();
  boolean canedit=true;
  if(!(ds.getValue("sfkxg")==null))
  {
    if(ds.getValue("sfkxg").equals("0"))
    canedit= false;
    else
    canedit= true;
  }
  canedit=canedit||b_SalerPrixSetBean.isAdd();
  String edclass=canedit?"class=edbox":"class=edline";
  String readonly = canedit?"":"readonly";

  ArrayList opkey = new ArrayList();
  opkey.add("avgcjtcl"); opkey.add("sl");opkey.add("XSA");opkey.add("XSB");
  opkey.add("XSC");opkey.add("XSD");opkey.add("cgj");opkey.add("tcj");
  opkey.add("rll");opkey.add("jj");opkey.add("xsje");opkey.add("lqf");opkey.add("swbt");
  ArrayList opval = new ArrayList();
  opval.add("平均差价提成率"); opval.add("省外销售数量");opval.add("销售允许天数利息"); opval.add("销售回笼利息");
  opval.add("应收款月初余额利息"); opval.add("销售利息");opval.add("差价奖"); opval.add("提成奖");opval.add("日利率");
  opval.add("奖金");opval.add("销售金额");opval.add("内勤费2");opval.add("省外补贴2");
  ArrayList[] lists  = new ArrayList[]{opkey, opval};

%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table  BORDER="0" cellpadding="1" cellspacing="3">
    <tr>
    <td>&nbsp;
    </td>
    </tr>
    <tr>
    <td>&nbsp;
    </td>
    </tr>
    <tr>
      <td width="67" noWrap class="tableTitle">&nbsp;名 称 <br></td>
      <td width="129"  noWrap class="td"><input type="TEXT" name="mc" value="<%=row.get("mc")%>" style="WIDTH:150" maxlength="<%=ds.getColumn("mc").getPrecision()%>" <%=edclass%>  onKeyDown="return getNextElement();" <%=readonly%>></td>
      <td width="75"  noWrap class="tableTitle">类 型</td>
      <td width="254" class="td">
       <%
         String lx = row.get("lx");
         if(canedit){
      %>
        <pc:select name="lx" style="width:150" value="<%=lx%>">
        <pc:option value="4">数值型</pc:option>
        <pc:option value="2">文本型</pc:option>
        <pc:option value="1">字符型</pc:option>
        </pc:select>
      <%}else{%>
       <input type="hidden" name="lx" value="<%=lx%>" ><input type="TEXT"  value="<%=(lx.equals("1")?"字符型":(lx.equals("2")?"文本型":"数值型"))%>" style="WIDTH:150" maxlength="<%=ds.getColumn("lx").getPrecision()%>" <%=edclass%> onKeyDown="return getNextElement();" readonly>
      <%}%>
    </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">长 度</td>
      <td noWrap class="td"><input type="TEXT" name="cd" value="<%=row.get("cd")%>" style="WIDTH:150" maxlength="<%=ds.getColumn("cd").getPrecision()%>" <%=edclass%> onKeyDown="return getNextElement();" <%=readonly%>>
      </td>
      <td noWrap class="tableTitle">精 度</td>
      <td noWrap class="td"><input type="TEXT" name="jd" value="<%=row.get("jd")%>" style="WIDTH:150" maxlength="<%=ds.getColumn("jd").getPrecision()%>" <%=edclass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
    </tr>
    <tr>
      <td height="21" noWrap class="tableTitle">来 源</td>
      <td noWrap class="td">
        <%
        String ly = row.get("ly");
        String dyzdm = row.get("dyzdm");
        if(canedit){
        %>
         <pc:select name="ly" style="width:150" value="<%=ly%>"   >
        <pc:option value="1"  >直接输入</pc:option> <pc:option value="2">公式计算</pc:option>
        <pc:option value="3">公式引入</pc:option>
        </pc:select>
      <%}else{%>
       <input type="hidden" name="ly" value="<%=ly%>" ><input type="TEXT"  value="<%=(ly.equals("1")?"直接输入":"公式计算")%>" style="WIDTH:110" maxlength="<%=ds.getColumn("ly").getPrecision()%>" <%=edclass%> onKeyDown="return getNextElement();" readonly>
      <%}%>
      </td>
      <td noWrap class="tableTitle">排序号</td>
      <td noWrap class="td"><input type="TEXT" name="pxh" value="<%=row.get("pxh")%>" style="WIDTH:150" maxlength="<%=ds.getColumn("pxh").getPrecision()%>" class="edbox" onKeyDown="return getNextElement();"></td>
    </tr>

     <tr>
      <td height="21" noWrap class="tableTitle">固定项目</td>
      <td noWrap  class="td">
        <%if(canedit){%>
          <pc:select name="xm" addNull="1"  input="0" style="width:130" >
         <%=b_SalerPrixSetBean.listToOption(lists, opkey.indexOf(dyzdm))%>
          </pc:select>
        <%}else if(!dyzdm.equals("")&&opkey.indexOf(dyzdm)!=-1){%>
         <input type="hidden" name="xm" value="<%=dyzdm%>" ><input type="TEXT"  value="<%=opval.get(opkey.indexOf(dyzdm))%>" style="WIDTH:110"  <%=edclass%> onKeyDown="return getNextElement();" readonly>
        <%}%>
      </td>
      <td height="21" noWrap class="tableTitle"></td>
      <td noWrap class="td"><input type="hidden" name="jsgs" value="<%=row.get("jsgs")%>" style="WIDTH:325" maxlength="<%=ds.getColumn("jsgs").getPrecision()%>" class="ednone" readonly>
      </td>
     </tr>
     <tr>
    <td colspan="4"  class="tableTitle">
        <%String sa = "sumitForm("+Operate.POST+");";%>
      <input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value=" 保存(S) ">
        <pc:shortcut key="s" script="<%=sa%>" />
      <input name="button2" type="button" class="button" onClick="parent.hideFrameNoFresh()" value=" 关闭(X)">
        <pc:shortcut key="x" script="parent.hideFrameNoFresh()" />
    </td>
    </tr>
  </table>
</form>
</BODY>
</Html>
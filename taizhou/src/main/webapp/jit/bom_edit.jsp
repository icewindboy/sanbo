<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
%><%
  String pageCode = "bom";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.jit.B_Bom bomBean = engine.erp.jit.B_Bom.getInstance(request);
  String retu = bomBean.doService(request, response);
%><%--2004.4.17 bom表子件类型增加跟踪件和主配料（主配料也是用于跟踪的）--%>
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
<script language="javascript">
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function submitTree()
{
  parent.depttree.form1.operate.value = '<%=bomBean.NODE_REFRESH%>';
  parent.depttree.form1.submit();
}
function refreshTree(){
  parent.depttree.form1.operate.value = '';
  parent.depttree.form1.submit();
}
function refreshNode()
{
  parent.depttree.RefreshNode('<%=bomBean.getParentId()+"_"+bomBean.getParentPropId()%>', '<%=bomBean.getPathCode()%>');
}
function cancleOperate()
{
  parent.depttree.CancleCopy();
  location.href='../blank.htm';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name,
                 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=isprops_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=isprops', obj.value,
                 'clearProductProp('+i+');');
}
function clearProductProp(i){
  eval("form1.dmsxid_"+i+".value=''");
  eval("form1.sxz_"+i+".value=''");
}
function parentCodeChange(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name,
                 'srcVar=sjcpid&srcVar=cpbm&srcVar=product&srcVar=jldw&srcVar=isprops',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=isprops',
                 obj.value, 'clearParentProductProp();');
}
function clearParentProductProp(i){
  form1.sjdmsxid.value='';
  form1.sxz.value='';
}
function parentPropSelect(obj,cpid)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=sjdmsxid&srcVar=sxz',
                         'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                         'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name,
                 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=isprops_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=isprops',
                 obj.value, 'clearProductProp('+i+');');
}
function parentNameSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name,
                 'srcVar=sjcpid&srcVar=cpbm&srcVar=product&srcVar=jldw&srcVar=sxz',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=isprops',
                 obj.value, 'clearParentProductProp();');
}
</script>
<%if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();

  String parentId = bomBean.getParentId();
  String parentPropId = bomBean.getParentPropId();

  EngineDataSet list = bomBean.dsBomData;
  RowMap[] detailRows = bomBean.getDetailRowinfos();

  LookUp prodBean = bomBean.getProductBean(request);
  LookUp techniceBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_TECHNICS_NAME);
  LookUp propertyBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  //boolean isEnd = bomBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);
  //注册查询对象
  String[] cpid = new String[detailRows.length+1];
  String[] dmsxid = new String[detailRows.length+1];
  int i=0;
  for(; i<detailRows.length; i++)
  {
    cpid[i] = detailRows[i].get("cpid");
    dmsxid[i] = detailRows[i].get("dmsxid");
  }
  cpid[i] = parentId;
  dmsxid[i] = parentPropId;
  prodBean.regData(cpid);
  propertyBean.regData(dmsxid);
%>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR><TD NOWRAP>&nbsp;</TD></TR></TABLE>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table width="95%" border="0" align="center" cellpadding="1" cellspacing="1" id="tbcontrol">
    <tr><%RowMap prodRow = prodBean.getLookupRow(parentId);
          String input = bomBean.isAdd ? " class='edbox'" : " class='edline' readonly";%>
      <td nowrap class="td"><b>父件编码:</b><input type="text" name="cpbm" value='<%=prodRow.get("cpbm")%>' style="width:85" <%=input%> onChange='parentCodeChange(this);'>
        <b>父件产品:</b><input type="hidden" name="sjcpid" value='<%=parentId%>'>
        <input type="text" name="product" value='<%=prodRow.get("product")%>' style="width:260" onchange="parentNameSelect(this)" <%=input%>>
        <%if(bomBean.isAdd){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=sjcpid&srcVar=cpbm&srcVar=product&srcVar=jldw','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw',form1.sjcpid.value, 'clearParentProductProp();')"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="sjcpid.value='';cpbm.value='';product.value='';jldw.value='';"><%}%>
        <b>单位:</b><input type="text" name="jldw" value='<%=prodRow.get("jldw")%>' style="width:40" class="edline" readonly>
    <br><b>规格属性:</b><input type="text" style="width:260" name="sxz" value='<%=propertyBean.getLookupName(parentPropId)%>' <%=bomBean.isAdd ? "class='edbox'" : "class='edline'"%>
        onchange="if(form1.sjcpid.value==''){alert('请先输入产品');return;} parentPropSelect(this,form1.sjcpid.value)" <%=bomBean.isAdd ? "": "readonly"%>>
        <%if(bomBean.isAdd){%>
        <img style='cursor:hand' class='img' src='../images/view.gif' border=0 onClick="if(form1.sjcpid.value==''){alert('请先输入产品');return;}if(form1.isprops.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','sjdmsxid','sxz',form1.sjcpid.value)">
        <img style='cursor:hand' class='img' src='../images/delete.gif' BORDER=0 ONCLICK="clearParentProductProp()()">
        <%}%>
        <%--b>本层期:</b><input type="text" name="ahead_time" value='<%=bomBean.aheadtime%>' style="width:60" class='edbox' style="text-align:right;"--%>
        <%--b>提前期:</b><input type="text" name="tot_time" value='<%=bomBean.dsAheadTime.getValue("tot_time")%>' style="width:60" class='edline' style="text-align:right;" readonly--%>
        <input type='hidden' id='sjdmsxid' name='sjdmsxid' value='<%=parentPropId%>'>
        <input type='hidden' id='isprops' name='isprops' value='<%=prodRow.get("isprops")%>'>
        </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <td height='20' align="center" nowrap>
        <%--input type="hidden" name="multiIdInput" value="" onchange="" ProdMultiSelect('form1','srcVar=multiIdInput')--%>
        <input name="image" class="img" type="image" title="新增" onClick="sumitForm(<%=bomBean.DETAIL_ADD%>)" src="../images/add_big.gif" border="0" accesskey="a">
      </td>
      <td nowrap>材料编码</td>
      <td height='20' nowrap>品名规格</td>
      <td nowrap>规格属性</td>
      <td height='20' nowrap>单位</td>
      <td nowrap>数量</td>
      <td nowrap>损耗率</td>
      <td nowrap>子件类型</td>
      <td nowrap>流转情况</td>
    </tr>
    <%RowMap detail = null;
      for(i=0; i<detailRows.length; i++){
        detail = detailRows[i];
        String zjlxName = "zjlx_"+i;
        prodRow = prodBean.getLookupRow(detail.get("cpid"));
        //String isprop = prodRow.get("isprops");
        //String gymcidName = "gymcid_"+i;
    %>
    <tr onclick="selectRow()">
      <td class="td" nowrap width=10><%=i+1%></td>
      <td class="td" nowrap align="center" width="70"><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
        <input name="image" class="img" type="image" title="领料工段" onClick="showInterFrame(<%=i%>)" src="../images/dan.gif" border="0">
        <input name="image" class="img" type="image" title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', '','clearProductProp(<%=i%>);')">
        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=bomBean.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
      </td>
      <td class="td" nowrap><input type="text" name="cpbm_<%=i%>" value='<%=detail.get("cpbm")%>' class="edbox" onChange="productCodeSelect(this,<%=i%>)" onKeyDown="return getNextElement();"></td>
      <td class="td" nowrap><input type="text" name="product_<%=i%>" value='<%=prodRow.get("product")%>' class="edbox" onchange="productNameSelect(this,<%=i%>)"></td>
      <td class="td" nowrap><input type="text" name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>'
        onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;} propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" class='edbox'>
        <img style='cursor:hand' class='img' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if(form1.isprops_<%=i%>.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>', form1.cpid_<%=i%>.value)">
        <img style='cursor:hand' class='img' src='../images/delete.gif' BORDER=0 ONCLICK="clearProductProp(<%=i%>);">
        <input type='hidden' id='dmsxid_<%=i%>' name='dmsxid_<%=i%>' value='<%=detail.get("dmsxid")%>'></td>
        <input type='hidden' id='isprops_<%=i%>' name='isprops_<%=i%>' value='<%=prodRow.get("isprops")%>'></td>
      <td class="td" nowrap><input type="text" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' class="ednone" readonly></td>
      <td class="td" nowrap align="right"><input type="text" class="edFocused_r" style="width:60" onKeyDown="return getNextElement();" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>'></td>
      <td class="td" nowrap align="right"><input type="text" class="edFocused_r" style="width:60" onKeyDown="return getNextElement();" name="shl_<%=i%>" value='<%=detail.get("shl")%>' maxlength='<%=list.getColumn("shl").getPrecision()%>'></td>
      <td class="td" nowrap><pc:select className='edFocused' style="width:60" name="<%=zjlxName%>" value='<%=detail.get("zjlx")%>'>
        <pc:option value="1">普通件</pc:option><pc:option value="2">可选件</pc:option><pc:option value="3">通用件</pc:option>
        <pc:option value="4">主辅料</pc:option><pc:option value="5">分切件</pc:option><pc:option value="6">主配料</pc:option>
        </pc:select> </td>
      <td class="td" nowrap>
        <pc:select className='edFocused' style="width:60" name='<%="wgcl_"+i%>' value='<%=detail.get("wgcl")%>'>
        <pc:option value="1">领料</pc:option><pc:option value="2">流转</pc:option>
        </pc:select> </td>
    </tr>
    <%list.next();
        }
        for(; i < 4; i++){
    %>
    <tr>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
      <td class="td">&nbsp;</td>
    </tr>
    <%}%>
  </table>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
    <%=bomBean.adjustInputSize(new String[]{"cpbm", "product", "sxz", "jldw", "sl", "shl", "zjlx"}, "form1", detailRows.length)%>
    function showInterFrame(rownum)
    {
      var url = "bom_section.jsp?operate=<%=bomBean.CHILD_SECTION_INI%>&rownum="+rownum;
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
  </SCRIPT>
  <table width="95%" border="0" align="center" cellpadding="1" cellspacing="1">
    <tr>
      <td nowrap class="td" align="center"><input name="button" type="button" class="button" onClick="sumitForm(<%=bomBean.NODE_EDIT_POST%>);" value="保存(S)" accesskey="s">
        <input name="button2" type="button" class="button" onClick="cancleOperate();" value="返回(C)" accesskey="c">
      </td>
    </tr>
  </table>
  </form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="230" height="300" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<%out.print(retu);%>
</BODY>
</Html>
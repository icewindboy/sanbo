<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
%><%
  String pageCode = "bom";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  engine.erp.produce.B_Bom bomBean = engine.erp.produce.B_Bom.getInstance(request);
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
function cancleOperate()
{
  parent.depttree.CancleCopy();
  location.href='../blank.htm';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function bomCodeChange(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=sjcpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
</script>
<%if(retu.indexOf("location.href=")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  String patentId = bomBean.getParentId();
  EngineDataSet list = bomBean.dsBomData;
  RowMap[] detailRows = bomBean.getDetailRowinfos();
  LookUp prodBean = bomBean.getProductBean(request);
  LookUp techniceBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_TECHNICS_NAME);
  //boolean isEnd = bomBean.isAdd ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);
  //注册查询对象
  String[] cpid = new String[detailRows.length+1];
  int i=0;
  for(; i<detailRows.length; i++)
    cpid[i] = detailRows[i].get("cpid");
  cpid[i] = patentId;
  prodBean.regData(cpid);
%>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar"><TR><TD NOWRAP>&nbsp;</TD></TR></TABLE>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  <table width="95%" border="0" align="center" cellpadding="1" cellspacing="1" id="tbcontrol">
    <tr><%RowMap prodRow = prodBean.getLookupRow(patentId);
          String input = bomBean.isAdd ? " class='edbox' onChange='bomCodeChange(this);' onKeyDown='return getNextElement();'" : " class='edline' readonly";%>
      <td nowrap class="td"><b>父件编码:</b><input type="text" name="cpbm" value='<%=prodRow.get("cpbm")%>' style="width:80"<%=input%>>
        <b>父件产品:</b><input type="hidden" name="sjcpid" value='<%=patentId%>'>
        <input type="text" name="product" value='<%=prodRow.get("product")%>' style="width:260" class="edline" readonly>
        <%if(bomBean.isAdd){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=sjcpid&srcVar=cpbm&srcVar=product&srcVar=jldw','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw',form1.sjcpid.value)"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="sjcpid.value='';cpbm.value='';product.value='';jldw.value='';"><%}%>
        <b>单位:</b><input type="text" name="jldw" value='<%=prodRow.get("jldw")%>' style="width:40" class="edline" readonly>
        </td>
    </tr>
  </table>
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td nowrap width=10></td>
      <td height='20' width="40" align="center" nowrap>
        <%--input type="hidden" name="multiIdInput" value="" onchange="" ProdMultiSelect('form1','srcVar=multiIdInput')--%>
        <input name="image" class="img" type="image" title="新增" onClick="sumitForm(<%=bomBean.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
      </td>
      <td nowrap>材料编码</td>
      <td height='20' nowrap>品名 规格</td>
      <td height='20' nowrap width=30>单位</td>
      <td nowrap>数量</td>
      <td nowrap>损耗率</td>
      <td nowrap>子件类型</td>
    </tr>
    <%RowMap detail = null;
      for(i=0; i<detailRows.length; i++){
        detail = detailRows[i];
        String zjlxName = "zjlx_"+i;
        //String gymcidName = "gymcid_"+i;
    %>
    <tr>
      <td class="td" nowrap width=10><%=i+1%></td>
      <td class="td" nowrap align="center"><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
      <input name="image" class="img" type="image" title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw')">
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=bomBean.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
      <%prodRow = prodBean.getLookupRow(detail.get("cpid"));%>
      </td>
      <td class="td" nowrap><input type="text" name="cpbm_<%=i%>" style="width:100%" value='<%=detail.get("cpbm")%>' class="edbox" onChange="productCodeSelect(this,<%=i%>)" onKeyDown="return getNextElement();"></td>
      <td class="td" nowrap><input type="text" name="product_<%=i%>" style="width:100%" value='<%=prodRow.get("product")%>' class="ednone" readonly></td>
      <td class="td" nowrap><input type="text" name="jldw_<%=i%>" style="width:100%" value='<%=prodRow.get("jldw")%>' class="ednone" readonly></td>
      <td class="td" nowrap align="right"><input type="text" class="edFocused_r" style="width:60" onKeyDown="return getNextElement();" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>'></td>
      <td class="td" nowrap align="right"><input type="text" class="edFocused_r" style="width:60" onKeyDown="return getNextElement();" name="shl_<%=i%>" value='<%=detail.get("shl")%>' maxlength='<%=list.getColumn("shl").getPrecision()%>'></td>
      <td class="td" nowrap><pc:select className='edFocused' style="width:60" name="<%=zjlxName%>" value='<%=detail.get("zjlx")%>'>
        <pc:option value="1">普通件</pc:option><pc:option value="2">可选件</pc:option><pc:option value="3">通用件</pc:option>
        <pc:option value="4">跟踪件</pc:option><pc:option value="5">分切件</pc:option><pc:option value="6">主配料</pc:option>
      </pc:select>
      </td>
      <%--td class="td" nowrap><pc:select className='edFocused' style="width:80" name="<%=gymcidName%>">
        <%=techniceBean.getList(detail.get("gymcid"))%>
      </pc:select></td--%>
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
    </tr>
    <%}%>
  </table><SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);</SCRIPT>
  <table width="95%" border="0" align="center" cellpadding="1" cellspacing="1">
    <tr>
      <td nowrap class="td" align="center"><input name="button" type="button" class="button" onClick="sumitForm(<%=bomBean.NODE_EDIT_POST%>);" value="保存(S)" onKeyDown="return getNextElement();">
        <pc:shortcut key="s" script='<%="sumitForm("+bomBean.NODE_EDIT_POST+");"%>'/>
        <input name="button2" type="button" class="button" onClick="cancleOperate();" value="返回(C)" onKeyDown="return getNextElement();">
        <pc:shortcut key="c" script='cancleOperate();'/>
      </td>
    </tr>
  </table>
  </form>
<%out.print(retu);%>
</BODY>
</Html>
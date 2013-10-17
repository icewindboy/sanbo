<%--物料可替换件修改页面--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.produce.*"%>
<%@ page import="java.util.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_over = "op_over";
%><%String pageCode = "bom_replace_list";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  B_BomRefill b_BomRefillBean = B_BomRefill.getInstance(request);
  LookUp productBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT);
  boolean isEdit = loginBean.hasLimits(pageCode, op_add) || loginBean.hasLimits(pageCode, op_edit) || loginBean.hasLimits(pageCode, op_delete);
%>
<%
  String retu = b_BomRefillBean.doService(request, response);
  if(retu!="")
  {
    out.print(retu);
  }
  String curUrl = request.getRequestURL().toString();
  RowMap m_RowInfo = b_BomRefillBean.getRowinfo();   //所填信息
  EngineDataSet ds_replace_bom = b_BomRefillBean.getDetailTable();
  RowMap[] d_rowinfos = b_BomRefillBean.getDetailRowinfos();
  String father_cpid = b_BomRefillBean.father_cpid;
  String replace_cpid = b_BomRefillBean.replace_cpid;
  String oldsl = b_BomRefillBean.oldsl;
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
  function sumitForm(oper, row)
  {
    form1.rownum.value = row;
    form1.operate.value = oper;
    form1.submit();
  }
  function backList()
  {
    location.href='bom_replace_list.jsp';
  }
  function checkreplacecpid()
  {
    if(form1.cpid.value == "")
    {
      alert("没选择被替换物料!");
      return;
    }
    else
    {
      form1.cpid2.value = "";
      form1.pm2.value = "";
      form1.gg2.value = "";
      ProdSingleSelect('form1','srcVar=cpid2&srcVar=pm2&srcVar=gg2&srcVar=gw','fieldVar=cpid&fieldVar=pm&fieldVar=gg&fieldVar=jldw',form1.cpid2.value);
      if(form1.cpid2.value == "")
      {
      }
      else
            sumitForm(<%=B_BomRefill.CHECK_REPLACE_MATERIAL%>,-1);
              }
  }
  /*/选择产品
  function SonProdSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
  {
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
  var winName= "SingleProdSelector";
  paraStr = "../produce/import_son_product.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&curID="+curID;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
*/
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr><td NOWRAP align="center">物料可替换件设置</td></tr>
</table>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center" width="95%">
  <tr valign="top">
    <td>
      <table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
      <tr>
      <td width="100%">
      <table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">
      <tr>
      <%RowMap fatherprodRow = productBean.getLookupRow(father_cpid);%>
      <td nowrap class="tdTitle">父件产品编码：</td>
      <td nowrap class="td" >
      <input class="edline"  name="cpbm" value='<%=fatherprodRow.get("cpbm")%>'  onKeyDown="return getNextElement();" readonly style="width:100px">
      <INPUT TYPE="HIDDEN" NAME="cpid" value='<%=father_cpid%>' >
     </td>
      <td nowrap class="tdTitle">品名规格：</td>
      <td nowrap class="td">
      <input type="text" name="product" value='<%=fatherprodRow.get("product")%>' onKeyDown="return getNextElement();"  class='edline' style="width:220px">
      </td>
      <td nowrap class="tdTitle"> 单位：</td>
      <td nowrap class="td">
      <input type="text" name="jldw" value='<%=fatherprodRow.get("jldw")%>' onKeyDown="return getNextElement();" class='edline' readonly style="width:50px">
      </td>
      <td></td><td></td>
      </tr>
      <tr>
      <%RowMap  replaceprodRow = productBean.getLookupRow(replace_cpid);%>
      <td nowrap class="tdTitle">子件产品编码：</td>
      <td nowrap class="td">
      <input class="edline"  name="cpbm" value='<%=replaceprodRow.get("cpbm")%>' onKeyDown="return getNextElement();" readonly style="width:100px">
      <INPUT TYPE="hidden" NAME="replace_cpid" value='<%=replace_cpid%>'><%--INPUT TYPE="hidden" NAME="bomid" value='<%=m_RowInfo.get("bomid")%>'--%>
      <td nowrap class="tdTitle">品名规格：</td>
      <td nowrap class="td"><input type="text" name="gg2" value='<%=replaceprodRow.get("product")%>' onKeyDown="return getNextElement();" readonly class='edline' style="width:220px">
      </td>
      <td nowrap class="tdTitle"> 数量：</td>
      <td nowrap class="td">
      <input type="text" name="sl" value='<%=oldsl%>' onKeyDown="return getNextElement();" class='edline' readonly style="width:50px">
      </td>
      <td nowrap class="tdTitle"> 单位：</td>
      <td nowrap class="td">
      <input type="text" name="gw" value='<%=replaceprodRow.get("jldw")%>' onKeyDown="return getNextElement();" class='edline' readonly style="width:50px">
      </td>
      </tr>
      <tr>
      <td>&nbsp;</td>
      </tr>
      <tr>
     <td colspan="8" nowrap>
     <div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:100%;height:220;overflow-y:auto;overflow-x:auto;">
     <table id="tableview1" width="100%" hight="400px" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
     <td  align="center" nowrap>
     <%if(isEdit){%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.ADD%>,-1)" src="../images/add.gif" border="0">
     <pc:shortcut key="a" script='<%="sumitForm("+ Operate.ADD +",-1)"%>'/><%}%></td>
     <td>替换件编码</td>
     <td>替换件名称</td>
     <td>单位</td>
     <td>数量</td>
     <td>报损率%</td>
    </tr>
      <%
      String detailClass = isEdit ?  "class=edFocused" : "class=ednone";
      String detailClass_r = isEdit ?  "class=edFocused_r" : "class=ednone_r";
      String readonly = !isEdit ? " readonly" : "";
      productBean.regData(ds_replace_bom,"cpid");
      RowMap  detail = null;
      String cpid = null;
      int i=0;
      for(; i<d_rowinfos.length; i++)
      {
        detail = (RowMap)d_rowinfos[i];
        cpid = detail.get("cpid");
        RowMap prodRow = productBean.getLookupRow(cpid);
      %>
    <tr>
      <td nowrap class="td" align="center">
            <%if(isEdit){%>
      <INPUT TYPE="HIDDEN" NAME="cpid_<%=i%>" value='<%=cpid%>'><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=jldw_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>','fieldVar=cpid&fieldVar=jldw&fieldVar=cpbm&fieldVar=product')">
      <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="sumitForm(<%=Operate.DEL%>,<%=i%>)">
      <%}%>
      </td>
      <td class="td" nowrap width="20%"><input type="text" <%=detailClass%> style="width:100%"  id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>></td>
      <td class="td" nowrap><input type="text" name="product_<%=i%>" value='<%=prodRow.get("product")%>'   <%=detailClass%> style="width:100%" onchange="productNameSelect(this,<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>></td>
      <td class="td" nowrap><input type="text" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>'   class='ednone' style="width:100%" readonly></td>
      <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:100%"  id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=ds_replace_bom.getColumn("sl").getPrecision()%>' onKeyDown="return getNextElement();" onchange="sl_onchange(<%=i%>)" <%=readonly%>></td>
      <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:100%"   id="shl_<%=i%>" name="shl_<%=i%>" value='<%=detail.get("shl")%>' maxlength='<%=ds_replace_bom.getColumn("shl").getPrecision()%>' onKeyDown="return getNextElement();" <%=readonly%>></td>
    </tr>
        <%
    ds_replace_bom.next();}%>
    <%for(; i < 8; i++){%>
          <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
     <td class="td" nowrap></td>
      <td class="td" nowrap></td>
     <td class="td" nowrap></td>
     <td class="td" nowrap></td>
        </tr>
      <%}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',0);
    function sl_onchange(i)
      {
        var slObj = document.all['sl_'+i];
        var shlObj = document.all['shl_'+i];
        if(slObj.value=="")
          return;
        if(isNaN(slObj.value)){
          alert("输入的数量非法");
          slObj.focus();
          return;
        }
        if(isNaN(shlObj.value)){
          alert("输入的损耗率非法");
          shlObj.focus();
          return;
        }
    }
</script>
  </div>
</td>
</tr>
</table>
</table>
</table>
    <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
      <tr>
        <td noWrap class="tableTitle"><p><br>
          <%if(isEdit){%>
          <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>)" value='保存(S)'>
          <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
          <input name="btnback" type="button" class="button" onClick="location.href='bom_replace_list.jsp'" value='返回(C)'>
         <% String back ="location.href='bom_replace_list.jsp'" ;%>
         <pc:shortcut key="c" script='<%=back%>'/>
        </p>
        </td>
      </tr>
    </table>
</form>
</body>
</html>
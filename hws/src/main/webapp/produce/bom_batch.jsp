<%--BOM表批量修改--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.produce.*"%>
<%@ page import="java.util.*"%>
<%if(!loginBean.hasLimits("bom_batch", request, response))
    return;
  B_BomBatch b_BomBatchBean = B_BomBatch.getInstance(request);//获得实例
  LookUp productBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PRODUCT);
  String retu = b_BomBatchBean.doService(request, response);//执行操作
  if(!retu.equals(""))
  {
    out.print(retu);
  }
  String curUrl = request.getRequestURL().toString();
  RowMap m_RowInfo = b_BomBatchBean.getRowinfo();   //所填信息
  EngineDataSet dsB_BomBatch = b_BomBatchBean.getDetailTable();
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
function checkcpid()
{
  if(form1.cpid.value=="")
  {
    alert("没选择被替换物料!");
    return;
  }
  if(form1.cpid2.value == "")
  {
    alert("没选择替换物料!");
    return;
  }
  else
    sumitForm(<%=Operate.POST%>);
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
    form1.cpbm2.value = "";
    form1.product2.value = "";
    ProdSingleSelect('form1','srcVar=cpid2&srcVar=cpbm2&srcVar=product2','fieldVar=cpid&fieldVar=cpbm&fieldVar=product',form1.cpid2.value,'sumitForm(<%=B_BomBatch.CHECK_REPLACE_MATERIAL%>)');
  }
}
function showFixedQuery()
{
  showFrame('fixedQuery',true,"",true);
}
function hideFrameNoFresh()
{
  hideFrame('interframe1');
}
function sumitForm(oper, row)
{
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function productCodeSelect(obj)
{
   ProdCodeChange(document.all['prod'], obj.form.name,'srcVar=cpbm&srcVar=cpid&srcVar=product',
                   'fieldVar=cpbm&fieldVar=cpid&fieldVar=product',obj.value,'sumitForm(<%=Operate.MASTER_SEARCH%>,-1)');
}
function productCodeSelect2(obj)
{
   ProdCodeChange(document.all['prod'], obj.form.name,'srcVar=cpbm2&srcVar=cpid2&srcVar=product2','fieldVar=cpbm&fieldVar=cpid&fieldVar=product',obj.value,'sumitForm(<%=B_BomBatch.CHECK_REPLACE_MATERIAL%>)');
}
function productNameSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpbm&srcVar=cpid&srcVar=product',
                 'fieldVar=cpbm&fieldVar=cpid&fieldVar=product',obj.value,'sumitForm(<%=Operate.MASTER_SEARCH%>,-1)');
}
function productNameSelect2(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpbm2&srcVar=cpid2&srcVar=product2',
                  'fieldVar=cpbm&fieldVar=cpid&fieldVar=product',obj.value,'sumitForm(<%=B_BomBatch.CHECK_REPLACE_MATERIAL%>)');
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>

<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr><td NOWRAP align="center">BOM表成批修改</td></tr>
</table>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
    <td width="400">
      <table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
      <tr><td  class="activeVTab">子件物料</td></tr>
      </table>
      <table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
      <tr>
      <td>
      <table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">
      <tr>
      <td nowrap class="tdTitle">被替换物料：</td>
      <td nowrap class="td">
      <img style='cursor:hand' src='../images/select_prod.gif' border=0 onClick="ProdCodeChange(document.all['prod'],'form1','srcVar=cpbm&srcVar=cpid&srcVar=product','fieldVar=cpbm&fieldVar=cpid&fieldVar=product','','sumitForm(<%=Operate.MASTER_SEARCH%>,-1)')">
      <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="form1.cpid.value='';form1.product.value='';form1.cpbm.value='';">

      产品编码:<input type="text" class="edbox" style="width:100" onKeyDown="return getNextElement();" name="cpbm" value='<%=m_RowInfo.get("cpbm")%>' onchange="productCodeSelect(this)" >
      <INPUT TYPE="HIDDEN" NAME="cpid" value='<%=m_RowInfo.get("cpid")%>'>
      </td>
      <td nowrap class="tdTitle">品名 规格：</td>
      <td nowrap class="td">
      <input type="text" name="product" value='<%=m_RowInfo.get("product")%>'  onKeyDown="return getNextElement();" style="width:200px" class='edbox' onchange="productNameSelect(this)">
      </td>
      </tr>

      <tr>
      <td nowrap class="tdTitle">替换物料：</td>
      <td nowrap class="td">
      <img style='cursor:hand' src='../images/select_prod.gif' border=0 onClick="checkreplacecpid()">
      <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="form1.cpid2.value='';form1.product2.value='';form1.cpbm2.value='';">
      产品编码:<input type="text" class="edbox" style="width:100" onKeyDown="return getNextElement();" name="cpbm2" value='<%=m_RowInfo.get("cpbm2")%>' onchange="if(form1.cpid.value==''){alert('请先选择被替换物料');return;}productCodeSelect2(this)" >
      <INPUT TYPE="HIDDEN" NAME="cpid2" value='<%=m_RowInfo.get("cpid2")%>'>
      </td>
      <td nowrap class="tdTitle">品名规格：</td>
      <td nowrap class="td">
      <input type="text" name="product2" value='<%=m_RowInfo.get("product2")%>' onKeyDown="return getNextElement();"  style="width:200px" class='edbox' onchange="if(form1.cpid.value==''){alert('请先选择被替换物料');return;}productCodeSelect2(this)">
      </td>
      </tr>
      <tr>
      <td>&nbsp;</td>
      </tr>
      <tr>
     <td colspan="6" nowrap>
      <table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
      <tr><td  class="activeVTab">要变动的父件物料</td></tr>
      </table>
<div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:100%;height:240;overflow-y:auto;overflow-x:auto;">
  <table id="tableview1" width="100%" hight="600" border="0" cellspacing="1" cellpadding="0" class="table" align="center">
    <tr class="tableTitle">
     <td width=12 align="center" nowrap>
     <input type='checkbox' name='checkform' onclick='checkAll(form1,this);' onKeyDown='return getNextElement();'>
     </td>
     <td>产品代码</td>
     <td>产品名称</td>
     <td>单位</td>
    </tr>
      <%
      productBean.regData(dsB_BomBatch,"sjcpId");
      dsB_BomBatch.first();
      int i=0;
      for(; i<dsB_BomBatch.getRowCount(); i++)
      {
        RowMap prodRow = productBean.getLookupRow(dsB_BomBatch.getValue("sjcpId"));
      %>
    <tr>
      <td nowrap class="td">
      <input type="checkbox" name="sel"  value="<%=i%>" >
      </td>
      <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodRow.get("product")%></td>
      <td class="td" nowrap><%=prodRow.get("jldw")%></td>
    </tr>
        <%
    dsB_BomBatch.next();}%>
    <%for(; i < 11; i++){%>
          <tr>
      <td class="td" nowrap>&nbsp;</td>
      <td class="td" nowrap></td>
      <td class="td" nowrap></td>
     <td class="td" nowrap></td>
        </tr>
      <%}%>
  </table>
  <script language="javascript">initDefaultTableRow('tableview1',0);</script>
  </div>
</td>
</tr>
</table>
</table>
</table>
    <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
      <tr>
        <td noWrap class="tableTitle"><p><br>
          <!--<INPUT class="button" onClick="showFixedQuery()" type="button" value="筛选要变动的父件物料" name="Query" onKeyDown="return getNextElement();">-->
          <input name="button2" type="button" class="button" onClick="checkcpid()" value=' 保存(S)'>
         <pc:shortcut key="c" script='checkcpid()'/>
          <input name="btnback" type="button" class="button" onClick="location.href='<%=b_BomBatchBean.retuUrl%>'" value=" 返回(C)">
        </p>
        </td>
      </tr>
    </table>
</form>
<form name="fixedQueryform" method="post" action="<%=curUrl%>" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
   <INPUT TYPE="HIDDEN" NAME="operate" VALUE="0">
   <div class="queryPop" id="fixedQuery" name="fixedQuery">
   <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD align="center" nowrap class="td">产品代码</TD>
              <td nowrap class="td">
              <input class="EDLine" style="WIDTH:100px" name="pm" value=''  onKeyDown="return getNextElement();">
              <INPUT TYPE="HIDDEN" NAME="sjcpId" value='' >
              <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('fixedQueryform','srcVar=sjcpId&srcVar=pm&srcVar=gg','fieldVar=cpid&fieldVar=pm&fieldVar=gg',fixedQueryform.sjcpId.value)">
              <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="fixedQueryform.sjcpId.value='';fixedQueryform.pm.value='';fixedQueryform.gg.value='';">
             </td>
              <td nowrap class="tdTitle">规格型号：</td>
              <td nowrap class="td">
              <input type="text" name="gg" value=''  style="width:100px" class='edline'>
              </td>
              </tr>
            </TABLE>
            <TR>
              <TD colspan="4" nowrap class="td" align="center"><INPUT class="button" onClick="sumitFixedQuery(<%=b_BomBatchBean.FIXED_SEARCH%>)" type="button" value=" 查询(F)" name="button" onKeyDown="return getNextElement();">
                <pc:shortcut key="f" script='<%="sumitFixedQuery("+ b_BomBatchBean.FIXED_SEARCH +",-1)"%>'/>
                <INPUT class="button" onClick="hideFrame('fixedQuery')" type="button" value=" 关闭(X)" name="button3" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script='hide();'/>
              </TD>
            </TR>
    </TABLE>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
function hide()
{
  hideFrame('fixedQuery');
}
</SCRIPT>
  </DIV>
</form>
<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="320" height="325" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
</body>
</html>
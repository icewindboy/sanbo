<%--销售分栏设置--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,java.text.*,java.util.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.sale.B_SaleColumnSet b_SaleColumnSetBean = engine.erp.sale.B_SaleColumnSet.getInstance(request);
  String pageCode = "sale_flz";
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
  lockScreenToWait("处理中, 请稍候！");
  form1.rownum.value = row;
  form1.operate.value = oper;
  form1.submit();
}
function backList()
{
  location.href='sale_flz.jsp';
}
//产品编码
function productCodeSelect(obj, i)
{
    SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=cpid_'+i,'fieldVar=cpid',obj.value,'sumitForm(<%=b_SaleColumnSetBean.DETAIL_CHANGE%>,'+i+')');
}
//产品名称
function productNameSelect(obj, i)
{
    SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=cpid_'+i,'fieldVar=cpid',obj.value,'sumitForm(<%=b_SaleColumnSetBean.DETAIL_CHANGE%>,'+i+')');
}
</script>
<%
  String retu = b_SaleColumnSetBean.doService(request, response);
  if(retu.indexOf("backList();")>-1 || retu.indexOf("toFee")>-1 || retu.indexOf("toDock")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门

  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//LookUp产品信息
  engine.project.LookUp wzlbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_KIND);//物资类别
  engine.project.LookUp firstkindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_KIND);
  engine.project.LookUp stockKindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STOCKS_KIND);//存货类别
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_SaleColumnSetBean.getMaterTable();
  EngineDataSet list = b_SaleColumnSetBean.getDetailTable();
  prodBean.regData(list,"cpid");
  wzlbBean.regData(list,"wzlbid");

  RowMap masterRow = b_SaleColumnSetBean.getMasterRowinfo();
  RowMap[] detailRows= b_SaleColumnSetBean.getDetailRowinfos();
%>
<BODY oncontextmenu="window.event.returnValue=true" onLoad="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="80%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table WIDTH="80%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="80%">
    <tr valign="top">
      <td>
         <table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
         <tr>
            <td class="activeVTab">销售分栏设置</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="80%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="80%" bgcolor="#f0f0f0">
                <tr>
                <td noWrap class="tdTitle">名称</td>
                <td  noWrap class="td">
                  <input type="text" class=edbox name="mc"  onKeyDown="return getNextElement();" value='<%=masterRow.get("mc")%>' style="width:255" >
                </td>

                </tr>
                <tr>
                <td colspan="8" noWrap class="td"><div style="display:block;width:800;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="80%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                          <%
                            if(true){
                            String add = "sumitForm("+Operate.DETAIL_ADD+")";
                          %>
                          <img style='cursor:hand' src='../images/select_prod.gif'  title="新增物资类" border=0 onClick="sumitForm(<%=b_SaleColumnSetBean.WZLB_ADD%>)">
                          <input type="hidden" name="singleInput" value="" onchange="sumitForm(<%=Operate.DETAIL_ADD%>)">
                          <input name="image" class="img" type="image" title="新增产品" onClick="SaleProdSingleSelect('form1','srcVar=singleInput','fieldVar=cpid','','sumitForm(<%=Operate.DETAIL_ADD%>,-1)')" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script="<%=add%>" />
                          <%}%>
                        </td>
                        <td height='20' nowrap>样品编码</td>
                        <td height='20' nowrap>栏目</td>
                      </tr>
                    <%
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                        </td>
                    <%
                       String cpid=detail.get("cpid");
                       String wzlbid=detail.get("wzlbid");
                       RowMap productRow = prodBean.getLookupRow(cpid);
                       RowMap wzRow = wzlbBean.getLookupRow(wzlbid);
                       String bm = productRow.get("cpbm");
                       String prod = productRow.get("product");
                       if(!wzlbid.equals(""))
                       {
                         bm=wzRow.get("bm");
                         prod=wzRow.get("mc");
                       }
                     %>
                       <td class="td" nowrap>
                       <input type="hidden" name="cpid_<%=i%>" value="<%=cpid%>">

                       <%
                         if(cpid.equals("")){
                       %>
                        <input type="text" class="ednone"  style="width:160" onKeyDown="return getNextElement();" name="lbbm_<%=i%>" value='<%=wzRow.get("bm")%>'   readonly>
                        <%}else{%>
                       <input type="text" class="edFocused"  style="width:160" onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=bm%>' onchange="productCodeSelect(this,<%=i%>)" >
                       <%}%>
                       </td>
                       <td class="td" nowrap>
                       <%if(wzlbid.equals("")&&!b_SaleColumnSetBean.wzlbIsAdd()){%>
                       <input type="text" class="ednone"   onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prod%>'  onchange="productNameSelect(this,<%=i%>)" readonly  >
                       <%}else
                         if(cpid.equals("")){
                         String sumit = "sumitForm("+b_SaleColumnSetBean.WZLB_CHANGE+")";
                         String wzlb = "wzlbid_"+i;
                         String test="getWzlbBM("+i+");";
                       %>
                        <pc:select name="<%=wzlb%>" style="width:160"   onSelect="<%=test%>">
                        <%=firstkindBean.getList(wzlbid)%> </pc:select>
                        <%}%>
                      </td>
                    </tr>
                      <%//list.next();
                      }
                      for(; i < 4; i++){
                  %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td"></td>
                      </tr>
                    </table></div>
                    <SCRIPT LANGUAGE="javascript">rowinfo = new RowControl();
                    <%for(int k=0; k<i; k++)
                      {
                        out.print("AddRowItem(rowinfo,'rowinfo_"+k+"');");
                      }%>AddRowItem(rowinfo,'rowinfo_end');InitRowControl(rowinfo);

                     function getWzlbBM(i){
                          getRowValue(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PRODUCT_KIND%>', 'form1', 'srcVar=wzlbid_'+i+'&srcVar=lbbm_'+i, 'fieldVar=wzlbid&fieldVar=bm',eval('form1.wzlbid_'+i+'.value'));
                     }
               </SCRIPT></td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle">备注</td><%--其他信息--%>
                  <td colspan="7" noWrap class="td"><textarea name="bz" rows="3" onKeyDown="return getNextElement();" style="width:690"><%=masterRow.get("bz")%></textarea></td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="80%%">
          <tr>
            <td colspan="3" noWrap class="tableTitle">
              <%
              String postcontinue = "sumitForm("+Operate.POST_CONTINUE+");";
              String post ="sumitForm("+Operate.POST+");";
              String del = "if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+");";
              %>
              <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
              <pc:shortcut key="c" script="<%=postcontinue%>" />
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script="<%=post%>" />
              <input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>);" value="删除(D)">
              <pc:shortcut key="d" script="<%=del%>" />
              <input name="btnback" type="button" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script="backList();" />
            </td>
          </tr>
        </table>
    </td>
    </tr>
  </table>
</form>
<script language="javascript">
  initDefaultTableRow('tableview1',1);
</script>
<%out.print(retu);%>
</BODY>
</Html>
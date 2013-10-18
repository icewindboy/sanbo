<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,java.math.BigDecimal,engine.erp.baseinfo.*,engine.project.*,engine.erp.store.*"%>
<%@ page import="java.util.*"%>
<%
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_approve ="op_approve";
  String pageCode = "package_list";
  if(!loginBean.hasLimits("package_list", request, response))
    return;
  engine.erp.store.B_Package b_PackageBean = engine.erp.store.B_Package.getInstance(request);

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
<script language="javascript" src="../scripts/tabcontrol.js"></script>

<script language="javascript">
function backList()
{
 location.href='../store/package_list.jsp';
}
function sumitForm(oper, row)
{
       form1.rownum.value = row;
       form1.operate.value = oper;
       form1.submit();
}

function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value, '');
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value, '');
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                     'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
</script>
<%
  String retu = b_PackageBean.doService(request, response);
 if(retu.indexOf("backList()")>-1 || retu.indexOf("toDetail()")>-1)
 {
   out.print(retu);
   return;
 }
 engine.project.LookUp typeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PACKAGETYPE);//物资规格属性
 engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
 engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//LookUp产品信息
  String curUrl = request.getRequestURL().toString();
  RowMap m_RowInfo = b_PackageBean.getMasterRowinfo();   //行到主表的一行信息
  RowMap areapriceRow= null;
  RowMap carRow= null;

  RowMap corpRow= null;
  RowMap carHaoRow= null;
   RowMap carareaRow= null;
  RowMap mastercorpRow= null;

 EngineDataSet dsrl_pxkc = b_PackageBean.getPxkcTable();
 EngineDataSet dsDWTX = b_PackageBean.getMaterTable();
 RowMap[] pxkcRows= b_PackageBean.getPxkcRowinfos();//从表应聘人员教育情况的多行信息


 boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
 boolean isCanEdit =loginBean.hasLimits(pageCode, op_edit);


 String typeClass = (isCanEdit)?"class='edFocused'": "class='edline'";
 String detailClass = (isCanEdit)?"class='edFocused'": "class='ednone'";
String detailClass_r = (isCanEdit)?"class='edFocused_r'": "class='ednone'";
 String readonly = (isCanEdit)?"":"readonly";
typeBean.regData(dsDWTX,"funditemid");
%>
<BODY oncontextmenu="window.event.returnValue=true" >
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prod2" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>

<table WidTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 class="headbar">
  <tr>
    <td NOWRAP align="center"></td>
 </tr>
</table>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="rownum" value=''>
  <INPUT TYPE="HIDDEN" NAME="zweight" value='0'>
  <INPUT TYPE="HIDDEN" NAME="operate" value=''>
<table BORDER="0" CELLPADDING="0" CELLSPACING="2" align="center">
  <tr valign="top">
   <td width="750"><table border=0 CELLPADDING=0 CELLSPACING=0 class="table">
  <tr>
   <td  class="activeVTab">包装方案设置</td>
  </tr>
</table>
<table class="editformbox" cellspacing=2 cellpadding=0 width="100%">
  <tr>
   <td>
<table cellspacing="2" cellpadding="0" border="0" width="100%" bgcolor="#f0f0f0">
  <tr>
   <td nowrap class="tdTitle">方案编号</td>
  <%if(isCanEdit){%>
   <td nowrap class="td"><input type="text" name="package_code" value='<%=m_RowInfo.get("package_code")%>' maxlength="10" style="width:80" <%=typeClass%> onKeyDown="return getNextElement();"></td>
  <%}else {%><td nowrap class="td"><input type="text" name="package_code" value='<%=m_RowInfo.get("package_code")%>' maxlength="10" style="width:80" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
  <%}%>
   <td nowrap class="tdTitle">方案名称</td>
  <%if(isCanEdit){%>
   <td nowrap class="td"><input type="text" name="package_name" value='<%=m_RowInfo.get("package_name")%>' maxlength="10" style="width:80" <%=typeClass%> onKeyDown="return getNextElement();"></td>
  <%}else {%><td nowrap class="td"><input type="text" name="package_name" value='<%=m_RowInfo.get("package_name")%>' maxlength="10" style="width:80" <%=typeClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
  <%}%>
   <td nowrap class="tdTitle">包装类别</td>
  <%if(!isCanEdit){%>
   <td nowrap class="td"><input type="text" name="package_type" value='<%=typeBean.getLookupName(m_RowInfo.get("funditemid"))%>' maxlength="10" style="width:80" <%=typeClass%> onKeyDown="return getNextElement();"></td>
  <%}else {%>    <td nowrap class="td"><pc:select name="funditemid" addNull="1" style="width:110">
                      <%=typeBean.getList(m_RowInfo.get("funditemid"))%> </pc:select></td>
  <%}%>
  </tr>

   <tr>
     <td colspan="6" nowrap>
     <table cellspacing=0 width="100%" cellpadding=0>
   <tr>
     <td nowrap><div id="tabDivINFO_EX_0" class="activeTab"><a class="tdTitle" href="#" >明细情况</a></div></td>
     <td class="lastTab" valign=bottom width=100% align=right><a class="tdTitle" href="#">&nbsp;</a></td>
  </tr>
</table>
<div id="cntDivINFO_EX_0" class="tabContent" style="display:block;width:750;height:350;overflow-y:auto;overflow-x:auto;">
<table id="tableview1" border="0" cellspacing="1" cellpadding="0" class="table" width="100%">
  <tr class="tableTitle">
     <td nowrap>
     <%if(isCanEdit){%>
     <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=b_PackageBean.PXKC_ADD%>,-1)" src="../images/add.gif" border="0">
     <%}%>
     </td>
                  <td nowrap>辅料编码</td>
                  <td nowrap>包装辅料</td>
                  <td nowrap>规格属性</td>
                  <td nowrap>数量</td>
                   <td nowrap>单位</td>
                  <td nowrap>备注</td>


      </tr>
      <%
       prodBean.regData(dsrl_pxkc,"cpid");
       propertyBean.regData(dsrl_pxkc,"dmsxid");
       BigDecimal t_sl = new BigDecimal(0);
       int i = 0;
       RowMap pxkcdetail = null;
       dsrl_pxkc.first();
       for(; i<pxkcRows.length; i++)
       {
         pxkcdetail = pxkcRows[i];
         String sl =pxkcdetail.get("sl");
         if(b_PackageBean.isDouble(sl))
         t_sl = t_sl.add(new BigDecimal(sl));
         RowMap  prodRow= prodBean.getLookupRow(pxkcdetail.get("cpid"));
     %>
    <tr>
                <td class="td" align="center">
                <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>

                <%if(isCanEdit){%><input name="image32" class="img" type="image" title="删除" onClick="sumitForm(<%=b_PackageBean.PXKC_DEL%>,<%=i%>)" src="../images/del.gif" border="0" align="absmiddle"><%}%>
                <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                          onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw','','')">
                </td>
               <td class="td" nowrap>
                            <input type="hidden" name="cpid_<%=i%>" value="<%=pxkcdetail.get("cpid")%>">
                          <input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>'  onchange="productCodeSelect(this,<%=i%>)"   <%=readonly%>></td>
                              <td class="td" nowrap> <input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>'  onchange="productNameSelect(this,<%=i%>)"   <%=readonly%>></td>
                <td class="td" nowrap>
                          <input type="hidden" name="dmsxid_<%=i%>" value="<%=pxkcdetail.get("dmsxid")%>">
                        <input  type="text" <%=detailClass%>   onKeyDown="return getNextElement();"  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(pxkcdetail.get("dmsxid"))%>' onchange="propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)"  <%=readonly%>>
                        <%if(isCanEdit){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value)">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                  <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="sl_<%=i%>" VALUE="<%=pxkcdetail.get("sl")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("sl").getPrecision()%>" <%=detailClass_r%>    onKeyDown="return getNextElement();" <%=readonly%>></td>
               <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="jldw_<%=i%>" VALUE="<%=prodRow.get("jldw")%>" style="width:80" MAXLENGTH="<%=dsrl_pxkc.getColumn("cpid").getPrecision()%>" class="ednone"   onKeyDown="return getNextElement();" <%=readonly%>></td>
                <td class="td" nowrap align="right"><INPUT TYPE="TEXT" NAME="bz_<%=i%>" VALUE="<%=pxkcdetail.get("bz")%>" style="width:100" MAXLENGTH="<%=dsrl_pxkc.getColumn("bz").getPrecision()%>" <%=detailClass%>    onKeyDown="return getNextElement();" <%=readonly%>></td>
               </tr>
          <%
           dsrl_pxkc.next(); }
            for(;i<10;i++)
            {
            %>
            <tr>
                  <td nowrap>&nbsp;</td>
                  <td nowrap>&nbsp;</td>
                  <td nowrap>&nbsp;</td>
                  <td nowrap>&nbsp;</td>
                  <td nowrap>&nbsp;</td>
                  <td nowrap>&nbsp;</td>
                  <td nowrap>&nbsp;</td>


            </tr>


            <%}%>
         <tr id="rowinfo_end">
          <td class="td">&nbsp;</td>
         <td class="tdTitle" nowrap>合计</td>
        <td class="td">&nbsp;</td>
         <td class="td">&nbsp;</td>

         <td align="right" class="td"><input id="t_zl" name="t_zl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                    <td class="td"></td>
         <td class="td"></td>


                      </tr>
      </table>
      <script language="javascript">initDefaultTableRow('tableview1',1);

     </script>
      </div>
      </td>
      </tr>
   </table>
  </td>
  </tr>
 </table>
</table>
    <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
      <tr>
        <td noWrap class="tableTitle">

        <%if(isCanEdit){
        String add = "sumitForm("+Operate.POST_CONTINUE+",-1)";
        //String wet="sumitForm("+b_PackageBean.COUNT_WET+",-1)";
        %>
        <%--input name="btnback" type="button" title = "计算运费计重" class="button" onKeyDown="return onInputKeyboard();" onClick="sumitForm(<%=b_PackageBean.COUNT_WET%>);" value='计算运费计重(f)'><pc:shortcut key="f" script='<%=wet%>'/--%>
        <input name="button2" type="button" class="button" title = "保存添加"onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value='保存添加(N)'><pc:shortcut key="n" script='<%=add%>'/>
        <%if(loginBean.hasLimits(pageCode,op_add)){
        String reu = "sumitForm("+Operate.POST+",-1)";
        %>
        <input name="button" type="button" title = "保存返回" class="button" onClick="sumitForm(<%=Operate.POST%>);" value='保存返回(S)'><pc:shortcut key="s" script='<%=reu%>'/><%}%>
        <%}%>
        <%
        String ret = "backList()";
        %>
        <input name="btnback" type="button" title = "返回" class="button" onKeyDown="return onInputKeyboard();" onClick="backList();" value='  返回(C)  '><pc:shortcut key="c" script='<%=ret%>'/>
        </td>
      </tr>
  </table>
</form>
<%out.print(retu);%>
</body>
</html>
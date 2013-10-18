<%--在生产计划中生成实际bom页面--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.jit.B_BuildFactBom buildFactBomBean = engine.erp.jit.B_BuildFactBom.getInstance(request);
  String pageCode = "produce_plan";
  //boolean hasApproveLimit = isApprove && loginBean.hasLimits(pageCode, op_approve);
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
   function propertyNameSelect(obj,cpid,i)
   {
     PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                        'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
   }
</script>
<%String retu = buildFactBomBean.doService(request, response);
  if(retu.indexOf("window")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_STOCK);
  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_ORDER_GOODS);//销售合同
  engine.project.LookUp planBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCE_PLAN);//根据生产计划id得到生产计划号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  String curUrl = request.getRequestURL().toString();
  EngineDataSet list = buildFactBomBean.getDetailTable();
  RowMap[] detailRows= buildFactBomBean.getDetailRowinfos();
  String cpid = request.getParameter("cpid");//传入参数产品ID
  String hthwid = request.getParameter("hthwid");//传入参数销售合同货物ID
  String sl = request.getParameter("xql"); //传入参数计划明细中数量
  String jhh = request.getParameter("scjhh");// 传入参数生产计划号
  String zt = request.getParameter("zt");//如果已生成物料需求实际BOM就不能修改了
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td>
      <TABLE width="95%" BORDER=0 CELLSPACING=0 CELLPADDING=0 >
      <%
       saleOrderBean.regData(new String[]{});
       prodBean.regData(new String[]{});
       String htbh = saleOrderBean.getLookupName(hthwid);
      %>
         <tr>
         <td noWrap class="tdTitle">生产计划号</td>
         <td noWrap class="td"><input type="text" name="jhh" value='<%=jhh%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
         <td noWrap class="tdTitle">销售合同号</td>
         <td noWrap class="td"><input type="text" name="htbh" value='<%=saleOrderBean.getLookupName(hthwid)%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
         <td noWrap class="tdTitle">数量</td>
         <td noWrap  colspan="3" class="td"><input type="text" name="scjhh" value='<%=sl%>' style="width:100" class="edline" onKeyDown="return getNextElement();" readonly></td>
         </tr>
         <tr>
         <%RowMap productRow = prodBean.getLookupRow(cpid);%>
         <td noWrap class="tdTitle">产品编码</td>
         <td noWrap class="td"><input type="text" name="cpbm" value='<%=productRow.get("cpbm")%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
         <td noWrap class="tdTitle">品名规格</td>
         <td noWrap class="td"><input type="text" name="product" value='<%=productRow.get("product")%>' style="width:180" class="edline" onKeyDown="return getNextElement();" readonly></td>
         <td noWrap class="tdTitle">计量单位</td>
         <td noWrap class="td"><input type="text" name="jldw" value='<%=productRow.get("jldw")%>' style="width:100" class="edline" onKeyDown="return getNextElement();" readonly></td>
         </td>
         </tr>
	 </table>
      <table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">实际BOM表</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
            <% int width = (detailRows.length > 4 ? (detailRows.length > 10 ? 10 : detailRows.length) : 4)*23 + 66;%>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td nowrap width=10></td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td height='20' nowrap>规格属性</td>
                        <td height='20' nowrap>子件数量</td>
                        <td height='20' nowrap>计量需求量</td>
                        <td height='20' nowrap>计量单位</td>
                        <td height='20' nowrap>生产需求量</td>
                        <td height='20' nowrap>生产单位</td>
                        <td nowrap>需求日期</td>
                        <td nowrap>存货性质</td>
                        <td nowrap>层次</td>
                        <td nowrap>子件类型</td>
                        <td nowrap>上级产品编码</td>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      prodBean.regData(list, "sjcpid");
                      propertyBean.regData(list, "dmsxid");
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++){
                        detail = detailRows[i];
                        String dmsxid = detail.get("dmsxid");
                        String chxzName = "chxz_"+i;
                        String cc = detail.get("cc");
                        String chxz=detail.get("chxz");
                        String zjlx = detail.get("zjlx");
                        String chxzxs = chxz.equals("1") ? "自制件" : (chxz.equals("2") ? "外购件" : (chxz.equals("3") ? "外协件" : "虚拟件"));//存货性质显示
                        String zjlxxs = zjlx.equals("1") ? "普通件" : (chxz.equals("2") ? "可选件" : (chxz.equals("3") ? "通用件" : (chxz.equals("4") ?  "跟踪件" : (chxz.equals("5") ? "分切件" : "主配料"))));//子件类型
                        boolean isNoEdit=cc.equals("0") || (!zt.equals("0") && !zt.equals("9") && !zt.equals("1"));
                        String tdclass = isNoEdit ? "class=ednone_r" : "class=edFocused_r" ;
                        String read = isNoEdit ? "readonly" : "";
                      %>
                        <tr id="rowinfo_<%=i%>">
                          <td class="td" nowrap><%=i+1%></td>
                          <td class="td" nowrap>
                          <%if(zjlx.equals("2")){%>
                          <input type="hidden" name="singleProduct_<%=i%>" value="">
                          <input name="image" class="img" type="image" title="替换可选件" onClick="ReplaceProduct('form1','srcVar=singleProduct_<%=i%>','fieldVar=cpid',<%=detail.get("cpid")%>,'sumitForm(<%=buildFactBomBean.DETAIL_REPLACE_PRODUCT%>,<%=i%>)')" src='../images/view.gif' border="0">
                          <%}%></td>
                          <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                            RowMap  fatherProdRow = prodBean.getLookupRow(detail.get("sjcpid"));
                            %>
                          <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
                          <td class="td" nowrap><%=prodRow.get("product")%></td>
                          <td class="td" nowrap>
                          <input  name="sxz_<%=i%>" <%=tdclass%> value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if('<%=detail.get("cpid")%>'==''){alert('请先输入产品');return;}propertyNameSelect(this,<%=detail.get("cpid")%>,<%=i%>)" onKeyDown="return getNextElement();" <%=read%>>
                          <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                          <%if(!isNoEdit && prodRow.get("isprops").equals("1")){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if('<%=prodRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',<%=detail.get("cpid")%>)">
                          <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                            <%}%>
                          </td>
                          <td class="td" align="right" nowrap><input <%=tdclass%> onKeyDown="return getNextElement();" name="sl_<%=i%>" value='<%=detail.get("sl")%>' onchange="changeAmount(<%=i%>);" <%=read%>></td>
                          <td class="td" align="right" nowrap><input class="ednone_r" name="xql_<%=i%>" value='<%=detail.get("xql")%>' onchange="changeAmount();" readonly></td>
                            <td class="td" align="center" nowrap><%=prodRow.get("jldw")%></td>
                          <td class="td" align="right" nowrap><input class="ednone_r" name="scxql_<%=i%>" value='<%=detail.get("scxql")%>' onchange="changeAmount();" readonly></td>
                            <td class="td" align="center" nowrap><%=prodRow.get("scydw")%></td>
                          <td class="td" nowrap><%=detail.get("xqrq")%></td>
                          <td class="td" nowrap><%=chxzxs%></td>
                          <td class="td" nowrap><%=cc%></td>
                          <td class="td" nowrap><%=zjlxxs%></td>
                          <td class="td" nowrap><%=fatherProdRow.get("cpbm")%></td>
                    </tr>
                      <%list.next();
                      }
                      for(; i < 5; i++){
                  %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                      </tr>
                      <%}%>
                    </table></div>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
          <tr>
            <td colspan="3" noWrap class="tableTitle">
                  <%if((zt.equals("0") || zt.equals("9") || zt.equals("1"))){%>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
                   <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
                  <%}%>
              <%--if(isCanDelete){%><input name="button3" type="button" class="button" onClick="sumitForm(<%=Operate.DEL%>);" value=" 删除 "><%}--%>
              <input  type="button" class="button" onClick="window.close();" value=" 关闭(X)" onKeyDown="return getNextElement();">
                  <pc:shortcut key="x" script='window.close();'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=buildFactBomBean.adjustInputSize(new String[]{"sxz", "sl","xql"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
  function ReplaceProduct(frmName,srcVar,fieldVar,curID,methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "ReplaceProduct";
    paraStr = "../produce/replace_factbom_product.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&kc__cpid="+curID;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function changeAmount(i)
  {
   var zsl = <%=sl%>;
   var xqlObj =  document.all['xql_'+i];
   var zjslObj = document.all['sl_'+i];
   if(zjslObj.value=="")
     return;
   if(isNaN(zjslObj.value))
   {
     alert("非法子件数量")
     return;
   }
   if(zsl!="" && !isNaN(zsl))
      xqlObj.value = formatSum(parseFloat(zsl) * parseFloat(zjslObj.value));
  }
  </script>
<%out.print(retu);%>
</body>
</html>

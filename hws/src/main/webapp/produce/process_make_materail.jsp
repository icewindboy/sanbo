<%--在生产计划中下达物料需求计划页面--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.produce.B_ProduceProcess produceProcessBean = engine.erp.produce.B_ProduceProcess.getInstance(request);
  String pageCode = "produce_process";
  //boolean hasApproveLimit = isApprove && loginBean.hasLimits(pageCode, op_approve);
%>
<jsp:include page="../baseinfo/script.jsp" flush="true"/>
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
   function refresh()
   {
     window.close();
     window.opener.form1.submit();
   }
   function productCodeSelect(obj, i)
  {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i,
                      'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops', obj.value);
  }
  function productNameSelect(obj,i)
  {
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i,
                      'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops', obj.value);
   }
   function propertyNameSelect(obj,cpid,i)
   {
     PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                          'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
   }
   function refresh()
   {
     window.close();
     window.opener.sumitForm(<%=produceProcessBean.DETAIL_REFRESH%>);
   }
</script>
<%String retu = produceProcessBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_STOCK);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  String curUrl = request.getRequestURL().toString();
  EngineDataSet dsCommonMaterail = produceProcessBean.getCommonMaterail();
  HtmlTableProducer detailProducer = produceProcessBean.commonProducer;
  RowMap[] commonRows= produceProcessBean.getCommonRowinfos();
  String SYS_PRODUCT_SPEC_PROP =produceProcessBean.SYS_PRODUCT_SPEC_PROP;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  String SC_PRODUCE_UNIT_STYLE = produceProcessBean.SC_PRODUCE_UNIT_STYLE;//是否强制换算
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
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">加工单物料</td>
          </tr>
        </table>
        <%int width = (commonRows.length > 4 ? (commonRows.length > 10 ? 10 : commonRows.length) : 4)*23 + 66;%>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                             <td nowrap>
                             <input name="image" class="img" type="image" title="新增(Z)" onClick="sumitForm(<%=produceProcessBean.COMMONMATERAIL_ADD%>)" src="../images/add_big.gif" border="0">
                             <pc:shortcut key="z" script='<%="sumitForm("+ produceProcessBean.COMMONMATERAIL_ADD +",-1)"%>'/>
                             </td>
                            <td nowrap>物料编码</td>
                            <td nowrap>物料品名规格</td>
                            <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                            <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                            <td nowrap><%=detailProducer.getFieldInfo("ylsl").getFieldname()%></td>
                            <td nowrap>物料计量单位</td>
                            <td nowrap><%=detailProducer.getFieldInfo("scsl").getFieldname()%></td>
                            <td nowrap><%=detailProducer.getFieldInfo("ylscsl").getFieldname()%></td>
                            <td nowrap>物料生产单位</td>
                            <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                          </tr>
                        <%prodBean.regData(dsCommonMaterail,"cpid");
                          propertyBean.regData(dsCommonMaterail,"dmsxid");
                          BigDecimal t_sl = new BigDecimal(0),t_ylsl = new BigDecimal(0),t_scsl = new BigDecimal(0),t_ylscsl = new BigDecimal(0);
                          int m=0;
                          RowMap materildetail = null;
                          for(; m<commonRows.length; m++)   {
                            materildetail = commonRows[m];
                            String sl = materildetail.get("sl");
                            if(produceProcessBean.isDouble(sl))
                              t_sl = t_sl.add(new BigDecimal(sl));
                            String ylsl = materildetail.get("ylsl");
                            if(produceProcessBean.isDouble(ylsl))
                              t_ylsl = t_ylsl.add(new BigDecimal(ylsl));
                            String scsl = materildetail.get("scsl");
                            if(produceProcessBean.isDouble(scsl))
                              t_scsl = t_scsl.add(new BigDecimal(scsl));
                            String ylscsl = materildetail.get("ylscsl");
                            if(produceProcessBean.isDouble(ylscsl))
                              t_ylscsl = t_ylscsl.add(new BigDecimal(ylscsl));
                            String wldmsxid = materildetail.get("dmsxid");
                            String wlsx = propertyBean.getLookupName(wldmsxid);
                            String M_widths = BasePublicClass.parseEspecialString(wlsx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                            boolean isReceive = !materildetail.get("ylsl").equals("");

                            String edClass = isReceive ? "class=edline" : "class=edbox";
                            String detailClass = isReceive ? "class=ednone" : "class=edFocused";
                            String detailClass_r = isReceive ? "class=ednone_r" : "class=edFocused_r";
                            String readonly = isReceive ? " readonly" : "";
                        %>
                          <tr id="rowinfo_<%=m%>">
                            <td class="td" nowrap><%=m+1%></td>
                            <td class="td" nowrap align="center">
                            <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                             onClick="ProdSingleSelect('form1','srcVar=cpid_<%=m%>&srcVar=product_<%=m%>&srcVar=cpbm_<%=m%>&srcVar=jldw_<%=m%>&srcVar=scydw_<%=m%>&srcVar=scdwgs_<%=m%>&srcVar=isprops_<%=m%>','fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fielfVar=jldw&fieldVar=scydw&filedVar=scdwgs&fieldVar=isprops','')">
                              <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=produceProcessBean.COMMONMATERAIL_DEL%>,<%=m%>)" src="../images/delete.gif" border="0">
                            </td>
                            <%RowMap  materailprodRow= prodBean.getLookupRow(materildetail.get("cpid"));%>
                            <td class="td" nowrap><input type="hidden" name="cpid_<%=m%>" value="<%=materildetail.get("cpid")%>">
                            <input type="hidden" name="scdwgs_<%=m%>" value="<%=materailprodRow.get("scdwgs")%>">
                            <input type="hidden" name="widths_<%=m%>" value="<%=M_widths%>">
                            <input type="hidden" name="isprops_<%=m%>" value="<%=materailprodRow.get("isprops")%>">
                            <input type="text" <%=detailClass%> onKeyDown="return getNextElement();" id="cpbm_<%=m%>" name="cpbm_<%=m%>" value='<%=materailprodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=m%>)" <%=isReceive ? "readonly" : readonly%>></td>
                            <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" id="product_<%=m%>" name="product_<%=m%>" value='<%=materailprodRow.get("product")%>' onchange="productNameSelect(this,<%=m%>)" <%=isReceive ? "readonly" : readonly%>></td>
                            <td class="td" nowrap>
                            <input <%=detailClass%> name="sxz_<%=m%>" value='<%=propertyBean.getLookupName(materildetail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=m%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=m%>.value,<%=m%>)" onKeyDown="return getNextElement();" <%=isReceive ? "readonly" : readonly%>>
                            <input type="hidden" id="dmsxid_<%=m%>" name="dmsxid_<%=m%>" value="<%=materildetail.get("dmsxid")%>">
                            <%if(materildetail.get("ylsl").equals("")){%>
                            <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=m%>.value==''){alert('请先输入产品');return;}if(form1.isprops_<%=m%>.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=m%>','sxz_<%=m%>',form1.cpid_<%=m%>.value,'propertyChange(<%=m%>)')">
                            <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=m%>.value='';sxz_<%=m%>.value='';">
                            <%}%>
                            </td>
                            <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="sl_<%=m%>" name="sl_<%=m%>" value='<%=materildetail.get("sl")%>' maxlength='<%=dsCommonMaterail.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=m%>, false)" <%=isReceive ? "readonly" : readonly%>></td>
                            <td class="td" align="right" nowrap><%=materildetail.get("ylsl")%></td>
                            <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="jldw_<%=m%>" name="jldw_<%=m%>" value='<%=materailprodRow.get("jldw")%>' readonly></td>
                            <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="scsl_<%=m%>" name="scsl_<%=m%>" value='<%=materildetail.get("scsl")%>' maxlength='<%=dsCommonMaterail.getColumn("scsl").getPrecision()%>' onchange="sl_onchange(<%=m%>, false)" <%=isReceive ? "readonly" : readonly%>></td>
                            <td class="td" align="right" nowrap><%=materildetail.get("ylscsl")%></td>
                            <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="scydw_<%=m%>" name="scydw_<%=m%>" value='<%=materailprodRow.get("scydw")%>' readonly></td>
                          </tr>
                          <%
                          }
                          for(; m < 5; m++){
                      %>
                          <tr id="rowinfo_<%=m%>">
                            <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                            <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                            <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                            <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                            <%--detailProducer.printBlankCells(pageContext, "class=td", true);--%>
                          </tr>
                          <%}%>
                           <tr id="rowinfo_end">
                           <td class="td">&nbsp;</td>
                           <td class="tdTitle" nowrap>合计</td>
                           <td class="td">&nbsp;</td>
                           <td class="td">&nbsp;</td>
                           <td class="td">&nbsp;</td>
                           <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                           <td align="right" class="td"><input id="t_ylsl" name="t_ylsl" type="text" class="ednone_r" style="width:100%" value='<%=t_ylsl%>' readonly></td>
                           <td class="td">&nbsp;</td>
                           <td align="right" class="td"><input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
                           <td align="right" class="td"><input id="t_ylscsl" name="t_ylscsl" type="text" class="ednone_r" style="width:100%" value='<%=t_ylscsl%>' readonly></td>
                           <td class="td">&nbsp;</td>
                          <%--detailProducer.printBlankCells(pageContext, "class=td", true);--%>
                      </tr>
                        </table></div>
                    </td>
                  </tr>
                </table>
                <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
                     <tr>
                    <td colspan="3" noWrap class="tableTitle">
                 <input name="btnback" type="button" class="button" onClick="sumitForm(<%=produceProcessBean.CONFIRM%>);" value="确定(Z)">
                <pc:shortcut key="z" script='<%="sumitForm("+ produceProcessBean.CONFIRM +",-1)"%>'/>
                <input name="btnback" type="button" class="button" onClick="window.close()" value=" 关闭(C)"> <pc:shortcut key="c" script='window.close()'/>
                  </td>
                  </tr>
                </table>
              </td>
            </tr>
            </td>
            </tr>
          </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=produceProcessBean.adjustInputSize(new String[]{"cpbm", "product", "sl","scsl"}, "form1", commonRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function propertyChange(i){
     var sxzObj = document.all['sxz_'+i];
     var scdwgsObj = document.all['scdwgs_'+i];
     if(sxzObj.value=='')
       return;
     var widthObj = document.all['widths_'+i];
     widthValue = parseString(sxzObj.value, '<%=SYS_PRODUCT_SPEC_PROP%>(', ')', '(');
     if(widthValue=='')
       return;
     widthObj.value =  widthValue;
     if(widthObj.value=='' || isNaN(widthObj.value))
      return;
     var slObj = document.all['sl_'+i];
     var scslObj = document.all['scsl_'+i];
     if(slObj.value=='' && scslObj.value=='')
       return;
     else if(slObj.value!='')
       scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthValue));
     else if(slObj.value=='' && scslObj.value!='')
       slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthValue)/parseFloat(scdwgsObj.value));
   }
   function sl_onchange(i, isBigUnit)
   {
     var slObj = document.all['sl_'+i];
     var scslObj = document.all['scsl_'+i];
     var scygsObj = document.all['scdwgs_'+i];//生产公式
     var obj = isBigUnit ? scslObj : slObj;
     var widthObj = document.all['widths_'+i];//规格属性的宽度
     var showText = isBigUnit ? "输入的生产数量非法" : "输入的数量非法";
     var showText2 = isBigUnit ? "输入的生产数量小于零" : "输入的数量小于零";
     var changeObj = isBigUnit ? slObj : scslObj;
        if(changeObj.value!="" && '<%=SC_PRODUCE_UNIT_STYLE%>'!='1')
          return;
        if(obj.value=="")
          return;
        if(isNaN(obj.value))
        {
          alert(showText);
          obj.focus();
          return;
        }
        if(obj.value<=0)
        {
          alert(showText2);
          obj.focus();
          return;
        }
         if(widthObj.value=="0" || scygsObj.value=="" || widthObj.value=="" || isNaN(widthObj.value))
        {
          changeObj.value=formatQty(isBigUnit ? parseFloat(scslObj.value) : parseFloat(slObj.value));
          if(isBigUnit)
            slObj.value=changeObj.value;
          else
            scslObj.value=changeObj.value;
        }
        if(scygsObj.value!="" && widthObj.value!="0" && widthObj.value!="" && !isNaN(scygsObj.value) && !isNaN(widthObj.value)){
          changeObj.value = formatQty(isBigUnit ? (parseFloat(scslObj.value)*parseFloat(widthObj.value)/parseFloat(scygsObj.value)) : (parseFloat(slObj.value)*parseFloat(scygsObj.value)/parseFloat(widthObj.value)));
          if(isBigUnit)
            slObj.value=changeObj.value;
          else
            scslObj.value=changeObj.value;
        }
        cal_tot('sl');
        cal_tot('scsl');
     }
     function cal_tot(type)
   {
     var tmpObj;
     var tot=0;
       for(i=0; i<<%=commonRows.length%>; i++)
         {
         if(type == 'sl')
           tmpObj = document.all['sl_'+i];
         else if(type == 'scsl')
           tmpObj = document.all['scsl_'+i];
         else
           return;
         if(tmpObj.value!="" && !isNaN(tmpObj.value))
           tot += parseFloat(tmpObj.value);
       }
       if(type == 'sl')
         document.all['t_sl'].value = formatQty(tot);
       else if(type == 'scsl')
         document.all['t_scsl'].value = formatQty(tot);
   }
  </script>
<%out.print(retu);%>
</body>
</html>

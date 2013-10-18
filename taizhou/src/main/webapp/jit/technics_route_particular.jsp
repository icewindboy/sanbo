<%--生产工艺路线设置编辑页面--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.jit.B_TechnicsRoute b_TechnicsRouteBean = engine.erp.jit.B_TechnicsRoute.getInstance(request);
String pageCode = "technics_route";
if(!loginBean.hasLimits("technics_route", request, response))
  return;
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
function refresh()
{
  // form1.submit();
}
function productCodeSelect(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw&srcVar=scydw&srcVar=hsdw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=hsdw', obj.value, 'prodchange();');
}

function productNameSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value, 'prodchange();');

}
function prodchange()
{
  form1.dmsxid.value=''; form1.sxz.value='';

}
function gxfdchange(i){
  associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_NAME%>', 'gymcid_'+i, 'gxfdid', eval('form1.gxfdid_'+i+'.value'), eval('form1.gymcid_'+i+'.value'), true);
    }
    function propertyNameSelect(obj,cpid)
    {
      PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid&srcVar=sxz',
                         'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
    }

    function showInterFrame(rownum)
    {
    var url = "technicexchange.jsp?operate=<%=b_TechnicsRouteBean.TECNICE_XCHANGE_INI%>&rownum="+rownum;
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
</script>
<BODY oncontextmenu="window.event.returnValue=true" onLoad="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"> </iframe>
<%String retu = b_TechnicsRouteBean.doService(request, response);
  if(retu.indexOf("refresh();")>-1)
  {
    out.print(retu);
    return;
  }
  int a=b_TechnicsRouteBean.d_RowInfos.size();
  if(b_TechnicsRouteBean.d_RowInfos == null)
    return;
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp PROCESS_FACTORYBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PROCESS_FACTORY_TYPE);
  engine.project.LookUp workCenterBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_CENTER);
  engine.project.LookUp workProduceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_PROCEDURE);
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_NAME);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp workgroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE_GROUP);
  engine.project.LookUp listprodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);


  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_TechnicsRouteBean.getMaterTable();
  EngineDataSet list = b_TechnicsRouteBean.getDetailTable();

  RowMap masterRow = b_TechnicsRouteBean.getMasterRowinfo();
  RowMap[] detailRows= b_TechnicsRouteBean.getDetailRowinfos();
  boolean isCanDelete = !b_TechnicsRouteBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//在修改状态,并有删除权限
  boolean isEdit = b_TechnicsRouteBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit);//在修改状态,并有修改权限
  String edClass = !isEdit ? "class=edline" : "class=edbox";
  String detailClass = !isEdit ? "class=ednone" : "class=edFocused";
  String detailClass_r = !isEdit ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEdit ? "":" readonly";
  boolean isMasterAdd = b_TechnicsRouteBean.isMasterAdd;
  boolean isFromBom = b_TechnicsRouteBean.isFromBom;
  String SYS_PRODUCT_SPEC_PROP = b_TechnicsRouteBean.SYS_PRODUCT_SPEC_PROP;
  String custName = loginBean.getSystemParam("SYS_CUST_NAME");//客户名称

%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
    <table WIDTH="760" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">


  <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="760" align="center" >
   <tr valign="top">
 <td>
 <table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">生产工艺路线设置</td>
          </tr>
        </table>
  <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="760">
  <tr>
  <td>
           <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%
                 prodBean.regData(ds,"cpid");
                 propertyBean.regData(ds,"dmsxid");
                 workgroupBean.regData(ds,"gylxzid");
                 RowMap prodRow = prodBean.getLookupRow(masterRow.get("cpid"));
                 RowMap propRow = propertyBean.getLookupRow(masterRow.get("dmsxid"));
                 // prodBean.regData(temp,"cpid");
                 // propertyBean.regData(temp,"dmsxid");
                 // RowMap TprodRow=prodBean.getLookupRow(temp.getValue("cpid"));
                 // RowMap TpropRow=propertyBean.getLookupRow(temp.getValue("dmsxid"));
                 String isprop = prodRow.get("isprops");
                 String cpid= masterRow.get("cpid");
                 list.first();
                 BigDecimal maxTime = new BigDecimal(0);
                 for(int i=0; i<list.getRowCount(); i++)
                 {
                   BigDecimal rowTime = list.getBigDecimal("scgs").add(list.getBigDecimal("ddgs"));
                   maxTime = rowTime.compareTo(maxTime) > 0 ? rowTime : maxTime;
                   list.next();
                 }
                 %>
                <tr>
                  <td noWrap class="tdTitle">产品编码</td>
                  <td noWrap class="td">
                  <%if(isFromBom) out.print("<INPUT TYPE='hidden' NAME='cpid' value='"+prodRow.get("cpid")+"'>");
                    else{%>
                  <INPUT TYPE="hidden"  NAME="cpid" value='<%=prodRow.get("cpid")%>'>
                  <%}%>
                  <%if(isFromBom) out.print("<input type='text' class='edbox' name='cpbm' value='"+prodRow.get("cpbm")+"' style='width:90' readonly>");
                  else {%>
                   <input  type="text" <%=edClass%> name="cpbm" value='<%=prodRow.get("cpbm")%>' style="width:90"  onKeyDown="return getNextElement();" onchange="productCodeSelect(this)" <%=readonly%>>
                   <%}%>
                    </td>

                  <td noWrap class="tdTitle">品名规格</td>
                  <td noWrap class="td">
                    <%if(isFromBom) out.print("<input type='text' class='edbox' name='product' value='"+prodRow.get("product")+"' style='width:260' readonly>");
                    else{%>
                    <input  type="text" <%=edClass%> name="product" value='<%=prodRow.get("product")%>' style="width:260"  onKeyDown="return getNextElement();" onchange="productNameSelect(this)" <%=readonly%>>
                    <img style='cursor:hand' title='单选物资' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw&srcVar=scydw&srcVar=hsdw','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=hsdw',form1.cpid.value,'sumitForm(<%=b_TechnicsRouteBean.CPSELECT%>);')">
                    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';product.value='';cpbm.value='';jldw.value='';scydw.value='';hsdw.value='';">
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">计量单位</td>
                  <td noWrap class="td">
                    <%if(isFromBom) out.print(" <input  type='text' class='edline' name='jldw' value='"+prodRow.get("jldw")+"' style='width:80' onKeyDown='return getNextElement();' readonly>");
                    else{%>
                    <input  type="text" class=edline name="jldw" value='<%=prodRow.get("jldw")%>' style="width:80"  onKeyDown="return getNextElement();" readonly>
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle">生产单位</td>
                  <td noWrap class="td">
                    <%if(isFromBom) out.print("<input type='text' class='edline' name='scydw' value='"+prodRow.get("scydw")+"' style='width:80' onKeyDown='return getNextElement();' readonly>");
                    else{%>
                    <input  type="text" class=edline name="scydw" value='<%=prodRow.get("scydw")%>' style="width:80"  onKeyDown="return getNextElement();" readonly>
                   <%}%>
                </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">换算单位</td>
                  <td noWrap class="td">
                     <%if(isFromBom) out.print("<input type='text' class='edline' name='hsdw' value='"+prodRow.get("hsdw")+"' style='width:80' onKeyDown='return getNextElement();' readonly>");
                    else{%>
                    <input  type="text" class=edline name="hsdw" value='<%=prodRow.get("hsdw")%>' style="width:80"  onKeyDown="return getNextElement();" readonly>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">规格属性</td>
                  <td noWrap class="td">
                    <%if(isFromBom) out.print("<input type='text' class='"+edClass+"' name='sxz' value='"+propertyBean.getLookupName(propRow.get("dmsxid"))+"' style='width:260' >");
                     else{%>
                    <input  type="text" <%=edClass%> name="sxz" value='<%=propertyBean.getLookupName(propRow.get("dmsxid"))%>' style="width:260"   onKeyDown="return getNextElement();"  onchange="if(form1.cpid.value==''){alert('请先输入产品');return} propertyNameSelect(this,form1.cpid.value)" <%=readonly%>>
                    <input type="hidden"  name="dmsxid" value="<%=masterRow.get("dmsxid")%>">
                    <%}%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid.value==''){alert('请先输入产品');return;}if('<%=isprop%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid','sxz', form1.cpid.value)">
                    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid.value='';sxz.value='';">

                  </td>
                  <td noWrap class="tdTitle">生效时间</td>
                  <td noWrap class="td">

                    <input type="text" name="sxsj" value='<%=masterRow.get("sxsj")%>' maxlength='10' style="width:80" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>

                    <%if(isEdit){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onClick="selectDate(form1.sxsj);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">提前期</td>
                  <td noWrap class="td"><%=maxTime%></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">工艺路线组</td>
                  <td noWrap class="td">
                    <pc:select name="gylxzid" className="edFocused" addNull="1" style="width:110" >
                      <%=workgroupBean.getList(masterRow.get("gylxzid"))%> </pc:select>
                </td>

                </tr>
              </table>
 </td>
    </tr>
 <tr>
      <td align="center" noWrap>
    <div style="display:block;width:760;overflow-y:auto;overflow-x:auto;">
        <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
          <tr class="tableTitle">
            <td nowrap width=20>
              <%if(isEdit){%>
              <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add.gif" border="0">
              <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +")"%>'/>
              <%}%>
            </td>
            <td nowrap>排序号</td>
            <td nowrap>产品编码</td>
            <td nowrap>产品名称</td>
            <td nowrap>工段名称</td>
            <td nowrap>工序名称</td>
            <td nowrap>工序单位</td>
            <td nowrap>执行工厂</td>
            <td nowrap>所占比例(%)</td>
            <td nowrap>调整率</td>

            <td nowrap>定额数量</td>
            <td nowrap>计量单位</td>
            <td nowrap>基准单价</td>
            <%--td nowrap>计件方式</td>

            <td nowrap>生产工时</td>
            <td nowrap>等待工时</td>
            <td nowrap>零部件价</td>
            <td nowrap>回收价</td--%>
            <td nowrap>是否外协</td>
            <td nowrap>外协价格</td>
            <%--td nowrap>备注</td--%>
          </tr>
          <%
            listprodBean.regData(list,"cpid");
            PROCESS_FACTORYBean.regData(list,"fctryid");
            workCenterBean.regData(list,"gzzxid");
            workCenterBean.regConditionData(list,"gzzxid");
            workProduceBean.regData(list,"gxfdid");
            workProduceBean.regConditionData(list,"gxfdid");
            if(isEdit)
              technicsNameBean.regData(list,"gxfdid");
            technicsNameBean.regConditionData(list, "gxfdid");
            list.first();
            RowMap detail = null;
            int i=0;
            for(; i<detailRows.length; i++)
            {
              detail = detailRows[i];
              String gxmc = detail.get("gxmc");
              String jjff = detail.get("jjff");
              String showjjff = jjff.equals("0") ? "计量单位计算" :
                                jjff.equals("1") ? "生产单位计算" :
                                jjff.equals("2") ? "换算单位计算" :
                                jjff.equals("3") ? "领料的计量单位除"+SYS_PRODUCT_SPEC_PROP :
                                jjff.equals("4") ? "领料的计量单位" : "等于单价";
              String aaaaa=workProduceBean.getLookupName(detail.get("gxfdid"));
              RowMap factoryrow = PROCESS_FACTORYBean.getLookupRow(detail.get("fctryid"));

              RowMap listprodRow = listprodBean.getLookupRow(detail.get("cpid"));

    %>
          <tr id="rowinfo_<%=i%>">
            <td class="td" nowrap align="center">
              <%if(isEdit){%>
              <img style='cursor:hand' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product','','')">
              <input name="image" class="img" type="image" title="可替换工序" onClick="showInterFrame(<%=i%>)" src="../images/dan.gif" border="0">
              <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
             </td>
            <%}%>
            <td class="td" nowrap>
            <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
            <input type='hidden' name='<%="internalRowNum_"+i%>' value='<%=detail.get("internalRowNum")%>'>
            <input type="text" <%=detailClass_r%> style="width:30" onKeyDown="return getNextElement();" id="list_<%=i%>" name="list_<%=i%>" value='<%=detail.get("list")%>' maxlength='5' <%=readonly%> onChange=''>
            </td>
            <td class="td" nowrap>
            <input type="HIDDEN"  id="cpid_<%=i%>"  name="cpid_<%=i%>"  value='<%=detail.get("cpid")%>' >
            <input type="text" class=ednone  onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=listprodRow.get("cpbm")%>' "readonly" >
            </td>
            <td class="td" nowrap>
            <input type="text" class=ednone  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=listprodRow.get("product")%>'  "readonly" >
            </td>
            <td class="td" nowrap>
              <%if
                (!isEdit)
    {
      out.print("<input type='text' value='"+workProduceBean.getLookupName(detail.get("gxfdid"))+"' class='ednone' style='width:100' readonly>");
    }
      else {%>
              <pc:select addNull="1" className="edFocused" name='<%="gxfdid_"+i%>' value='<%=detail.get("gxfdid")%>' style="width:90" onSelect='<%="gxfdchange("+i+");"%>'>
      <%-- onSelect='<%="sumitForm("+b_TechnicsRouteBean.GDJG_ONCHANGE+")"%>'--%>
              <%=workProduceBean.getList(detail.get("gxfdid"))%></pc:select>
              <%}%>
            </td>
            <td class="td" nowrap>
              <%if(!isEdit) out.print("<input type='text' style='width:100' value='"+technicsNameBean.getLookupName(detail.get("gymcid"))+"' class='ednone' readonly>");
                else {
      %>
              <pc:select addNull="1" className="edFocused" name='<%="gymcid_"+i%>' style="width:90">
              <%=technicsNameBean.getList(detail.get("gymcid"), "gxfdid", detail.get("gxfdid"))%></pc:select>
              <%}%>
            </td>

             <td class="td" nowrap>
              <input type="text" style="width:60" <%=detailClass%> onKeyDown="return getNextElement();" id="unit_<%=i%>" name="unit_<%=i%>" value='<%=detail.get("unit")%>' maxlength='<%=list.getColumn("unit").getPrecision()%>' <%=readonly%>>
            </td>

            <td class="td" nowrap>
              <%if(!isEdit) out.print("<input type='text' style='width:100' value='"+PROCESS_FACTORYBean.getLookupName(detail.get("fctryid"))+"' class='ednone' readonly>");
                else {
               %>
              <pc:select addNull="1" className="edFocused" name='<%="fctryid_"+i%>' style="width:90">
              <%=PROCESS_FACTORYBean.getList(detail.get("fctryid"))%></pc:select>
              <%}%>
            </td>
            <td class="td" nowrap>
              <input type="text" style="width:60" <%=detailClass_r%> onKeyDown="return getNextElement();" id="zybl_<%=i%>" name="zybl_<%=i%>" value='<%=detail.get("zybl")%>' maxlength='<%=list.getColumn("zybl").getPrecision()%>' <%=readonly%>>
            </td>
            <td class="td" nowrap>
              <input type="text" style="width:60" <%=detailClass_r%> onKeyDown="return getNextElement();" id="quot_<%=i%>" name="quot_<%=i%>" value='<%=detail.get("quot")%>' maxlength='<%=list.getColumn("quot").getPrecision()%>' <%=readonly%>>
            </td>

            <td class="td" nowrap>
              <input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="desl_<%=i%>" name="desl_<%=i%>" value='<%=detail.get("desl")%>' maxlength='<%=list.getColumn("desl").getPrecision()%>' <%=readonly%>>
            </td>
            <td class="td" nowrap><%=listprodRow.get("jldw")%></td>
            <td class="td" nowrap>
              <input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="deje_<%=i%>" name="deje_<%=i%>" value='<%=detail.get("deje")%>' maxlength='<%=list.getColumn("deje").getPrecision()%>' <%=readonly%>>
            </td>
            <%--onchange="sumitForm(<%=b_TechnicsRouteBean.GDJG_ONCHANGE%>);" --%>
            <%--td noWrap class="td">
              <%if(!isEdit) out.print("<input type='text' value='"+showjjff+"' class='ednone' style='width:140' readonly>");
      else {%>
              <pc:select className="edFocused" name='<%="jjff_"+i%>' style="width:140" value="<%=jjff%>">
                <pc:option value='0'>计量单位计算</pc:option> <pc:option value='1'>生产单位计算</pc:option>
              <pc:option value='2'>换算单位计算</pc:option> <pc:option value='3'>领料的计量单位除<%=SYS_PRODUCT_SPEC_PROP%></pc:option>
                <pc:option value='4'>领料的计量单位</pc:option> <pc:option value='5'>等于计件单价</pc:option>
          </pc:select>
              <%}%>
            </td>
            <td class="td" nowrap>
              <input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="scgs_<%=i%>" name="scgs_<%=i%>" value='<%=detail.get("scgs")%>' maxlength='<%=list.getColumn("scgs").getPrecision()%>' <%=readonly%>>
            </td>
            <td class="td" nowrap>
              <input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="ddgs_<%=i%>" name="ddgs_<%=i%>" value='<%=detail.get("ddgs")%>' maxlength='<%=list.getColumn("ddgs").getPrecision()%>' <%=readonly%>>
            </td>
            <td class="td" nowrap>
              <input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="lbjj_<%=i%>" name="lbjj_<%=i%>" value='<%=detail.get("lbjj")%>' maxlength='<%=list.getColumn("lbjj").getPrecision()%>' <%=readonly%>>
            </td>
            <td class="td" nowrap>
              <input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="hsj_<%=i%>" name="hsj_<%=i%>" value='<%=detail.get("hsj")%>' maxlength='<%=list.getColumn("hsj").getPrecision()%>' <%=readonly%>>
            </td--%>
            <td noWrap class="td">
              <%if(isEdit){%>
              <input type="radio" name="sfwx_<%=i%>" value="1"<%=detail.get("sfwx").equals("1") ? " checked" : "" %> onClick="ztChange(<%=i%>)">
              是
              <input type="radio" name="sfwx_<%=i%>" value="0"<%=detail.get("sfwx").equals("0") ? " checked" : "" %> onClick="hidden(<%=i%>)">
              否
              <%}else{%>
              <%=detail.get("sfwx").equals("1") ? "是" : "否"%>
              <%}%>
            </td>
            <td class="td" nowrap>
            <%
            if(detail.get("sfwx").equals("1")){%>
            <input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="wxjg_<%=i%>" name="wxjg_<%=i%>" value='<%=detail.get("wxjg")%>' maxlength='<%=list.getColumn("wxjg").getPrecision()%>' <%=readonly%>>
            <%}
              else{
             %>
            <input type="text" class="ednone" onKeyDown="return getNextElement();" id="wxjg_<%=i%>" name="wxjg_<%=i%>" value='<%=detail.get("wxjg")%>' maxlength='<%=list.getColumn("wxjg").getPrecision()%>' readonly>
            <%}%>
            </td>
            <%--td class="td" nowrap>
            <input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="bz_<%=i%>" name="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>'<%=readonly%>>
            </td--%>
          </tr>
          <%  list.next();
            }
            for(; i <16; i++){
    %>
          <tr>
            <td class="td" nowrap>&nbsp;</td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>

            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
            <td class="td" nowrap></td>
          </tr>
          <%}%>

    </table>
    </div>
    </td>
    </tr>
        </table>

 </td>
  </tr>
  </table>
   <table CELLSPACING=0 CELLPADDING=0 width="100%">
     <tr class="tableTitle">
     <td>&nbsp;</td>
     </tr>
            <tr>
              <td rowspan="8"  noWrap class="tableTitle">
                <%if(isEdit){%>
                <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
                <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
                <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)<%--=isFromBom ? "保存(S)" : "保存返回(S)"--%>">
                <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +")"%>'/>
                <%}%>
                <%if(isCanDelete){%>
                <input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>);" value=" 删除 ">
                  <pc:shortcut key="d" script='delMaster();'/>
                <%}%>
                <%if(!isFromBom){%>
                <input name="btnback" type="button" class="button" onClick="backlist();" value=" 返回(C) ">
                  <pc:shortcut key="c" script='backList();'/>
                <%}%>
                <%if(isEdit){%>
                <input type="hidden" name="multiIdInput" value="" onchange="sumitForm(<%=b_TechnicsRouteBean.MASTER_COPY%>)">
                <input name="btnback" class="button" type="button" value="复制给…" onClick="ProdMultiSelect('form1','srcVar=multiIdInput')">
                <%}%>
              </td>
            </tr>
          </table>
</form>

<div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=0 cellpadding=0 border=0>
    <TR>
      <TD><iframe id="interframe1" src="" width="460" height="300" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
      </TD>
    </TR>
  </TABLE>
</div>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
<%=b_TechnicsRouteBean.adjustInputSize(new String[]{"cpbm","product","deje", "desl","scgs","zybl","ddgs","lbjj","hsj",/*"gdjg",*/"wxjg","bz"}, "form1", detailRows.length)%>
  function delMaster(){
  if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
        }
        function backlist()
        {
          location.href='technics_route.jsp';
        }
        function  ztChange(row)
        {
          var obj=document.all['wxjg_'+row];
          obj.className="edbox";
          obj.readOnly=false;
        }
        function  hidden(row)
        {
          var obj=document.all['wxjg_'+row];
          obj.className="ednone";
          obj.readOnly=true;
        }
        function listchange(row)
        {

          var obj=document.all['list_'+row];
          var temp=obj.value;
          if(temp=="")
            return;
  var length=<%=detailRows.length%>;
    if(temp>length)
      temp=length;
    if(temp<row+1)
    {
      for(var i=temp-1;i<row;i++)
      {
        var tempobj=document.all['list_'+i];
        tempobj.value=i+2;
      }
    }
    else
    {
      for(var i=row+1;i<temp;i++)
      {
        var tempobj=document.all['list_'+i];
        tempobj.value=i;
      }
    }

        }
</SCRIPT>
<%out.print(retu);%>
</body>
</html>
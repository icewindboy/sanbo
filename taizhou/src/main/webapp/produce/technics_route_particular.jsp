<%--生产工艺路线设置编辑页面--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.produce.B_TechnicsRoute b_TechnicsRouteBean = engine.erp.produce.B_TechnicsRoute.getInstance(request);
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
  parent.form1.submit();
}
function productCodeSelect(obj)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw&srcVar=scydw&srcVar=hsdw',
                'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=hsdw', obj.value);
}
function productNameSelect(obj)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw',
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function gxfdchange(i){
  associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_NAME%>', 'gymcid_'+i, 'gxfdid', eval('form1.gxfdid_'+i+'.value'), eval('form1.gymcid_'+i+'.value'), true);
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<%String retu = b_TechnicsRouteBean.doService(request, response);
  if(retu.indexOf("refresh();")>-1)
  {
    out.print(retu);
    return;
  }
  if(b_TechnicsRouteBean.d_RowInfos == null)
    return;
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp routeTypeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE_TYPE);
  engine.project.LookUp workCenterBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_CENTER);
  engine.project.LookUp workProduceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_PROCEDURE);
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_NAME);
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
  String SYS_PRODUCT_SPEC_PROP = b_TechnicsRouteBean.SYS_PRODUCT_SPEC_PROP;
  String custName = loginBean.getSystemParam("SYS_CUST_NAME");//客户名称
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="cpid" value='<%=masterRow.get("cpid")%>'>
  <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="95%" align="center" bgcolor="#f0f0f0">
    <%prodBean.regData(ds,"cpid");%>
    <tr>
      <%RowMap prodRow = prodBean.getLookupRow(masterRow.get("cpid"));%>
      <td noWrap class="tdTitle">产品编码</td>
      <td noWrap class="td"><input  type="text" <%=edClass%> name="cpbm" value='<%=prodRow.get("cpbm")%>' style="width:80"  onKeyDown="return getNextElement();" onchange="productCodeSelect(this)" <%=readonly%>></td>
      <td noWrap class="tdTitle">品名规格</td>
      <td noWrap class="td"><input  type="text" <%=edClass%> name="product" value='<%=prodRow.get("product")%>' style="width:160"  onKeyDown="return getNextElement();" onchange="productNameSelect(this)" <%=readonly%>>
      <img style='cursor:hand' title='单选物资' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=cpbm&srcVar=product&srcVar=jldw&srcVar=scydw&srcVar=hsdw','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=hsdw',form1.cpid.value)">
      <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';product.value='';cpbm.value='';jldw.value='';scydw.value='';hsdw.value='';">
      </td>
      <td noWrap class="tdTitle">计量单位</td>
      <td noWrap class="td"><input  type="text" class=edline name="jldw" value='<%=prodRow.get("jldw")%>' style="width:30"  onKeyDown="return getNextElement();" readonly>
      </td>
      <td noWrap class="tdTitle">生产单位</td>
      <td noWrap class="td"><input  type="text" class=edline name="scydw" value='<%=prodRow.get("scydw")%>' style="width:30"  onKeyDown="return getNextElement();" readonly>
      </td>
      <td noWrap class="tdTitle">换算单位</td>
      <td noWrap class="td"><input  type="text" class=edline name="hsdw" value='<%=prodRow.get("hsdw")%>' style="width:30"  onKeyDown="return getNextElement();" readonly>
      </td>
      <td noWrap class="tdTitle">生效时间</td>
      <td noWrap class="td"><input type="text" name="sxsj" value='<%=masterRow.get("sxsj")%>' maxlength='10' style="width:70" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
        <%if(isEdit){%>
        <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.sxsj);"></a>
        <%}%>
      </td>
      <%--String onChange = "if(form1.gylxlxid.value!='"+masterRow.get("gylxlxid")+"' && form1.gylxlxid.value != ''){sumitForm("+b_TechnicsRouteBean.TECHNICS_CHANGE+");}";--%>
      <td noWrap class="tdTitle">工艺路线类型</td>
      <td noWrap class="td">
        <%if(!isEdit) out.print("<input type='text' value='"+routeTypeBean.getLookupName(masterRow.get("gylxlxid"))+"' style='width:110' class='edline' readonly>");
        else {%>
        <pc:select name="gylxlxid" style="width:130" > <%=routeTypeBean.getList(masterRow.get("gylxlxid"))%></pc:select>
        <%}%>
      </td>
    </tr>
    <tr>
      <td colspan="17" noWrap>
    <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
      <tr class="tableTitle">
        <td nowrap width=10></td>
        <td nowrap width=20>
          <%if(isEdit){%>
          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add.gif" border="0">
          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +")"%>'/>
          <%}%>
        </td>
        <td nowrap>工段名称</td>
        <td nowrap>工序名称</td>
        <td nowrap>工作中心</td>
        <td nowrap><%=custName.equals("xuening") ? "换算比例" : "定额数量"%></td>
        <td nowrap>计件价格</td>
        <td nowrap>计件方式</td>
        <%--td nowrap>工段价格</td--%>
        <td nowrap>生产工时</td>
        <td nowrap>等待工时</td>
        <td nowrap>零部件价</td>
        <td nowrap>回收价</td>
        <td nowrap>是否外协</td>
        <td nowrap>外协价格</td>
        <td nowrap>备注</td>
      </tr>
      <%
      workCenterBean.regData(list,"gzzxid");
      workProduceBean.regData(list,"gxfdid");
      if(isEdit)
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
    %>
      <tr id="rowinfo_<%=i%>">
        <td class="td" nowrap><%=i+1%></td>
        <td class="td" nowrap align="center">
          <%if(isEdit){%>
          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
        </td>
        <%}%>
        <td class="td" nowrap>
          <%if(!isEdit) out.print("<input type='text' value='"+workProduceBean.getLookupName(detail.get("gxfdid"))+"' class='ednone' style='width:100' readonly>");
      else {%>
          <pc:select addNull="1" className="edFocused" name='<%="gxfdid_"+i%>' style="width:90" onSelect='<%="gxfdchange("+i+");"%>'>
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
          <%if(!isEdit) out.print("<input type='text' style='width:100' value='"+workCenterBean.getLookupName(detail.get("gzzxid"))+"'  class='ednone' readonly>");
      else {%>
          <pc:select addNull="1" className="edFocused" style="width:90" name='<%="gzzxid_"+i%>'>
          <%=workCenterBean.getList(detail.get("gzzxid"))%></pc:select>
          <%}%>
        </td>
        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="desl_<%=i%>" name="desl_<%=i%>" value='<%=detail.get("desl")%>' maxlength='<%=list.getColumn("desl").getPrecision()%>' <%=readonly%>></td>
        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="deje_<%=i%>" name="deje_<%=i%>" value='<%=detail.get("deje")%>' maxlength='<%=list.getColumn("deje").getPrecision()%>' <%=readonly%>></td>
        <%--onchange="sumitForm(<%=b_TechnicsRouteBean.GDJG_ONCHANGE%>);" --%>
        <td noWrap class="td">
          <%if(!isEdit) out.print("<input type='text' value='"+showjjff+"' class='ednone' style='width:140' readonly>");
      else {%>
          <pc:select className="edFocused" name='<%="jjff_"+i%>' style="width:140" value="<%=jjff%>">
          <pc:option value='0'>计量单位计算</pc:option> <pc:option value='1'>生产单位计算</pc:option>
          <pc:option value='2'>换算单位计算</pc:option> <pc:option value='3'>领料的计量单位除<%=SYS_PRODUCT_SPEC_PROP%></pc:option>
          <pc:option value='4'>领料的计量单位</pc:option> <pc:option value='5'>等于计件单价</pc:option>
          </pc:select>
          <%}%>
        </td>
        <%--td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="gdjg_<%=i%>" name="gdjg_<%=i%>" value='<%=detail.get("gdjg")%>' maxlength='<%=list.getColumn("gdjg").getPrecision()%>' <%=readonly%>></td--%>
        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="scgs_<%=i%>" name="scgs_<%=i%>" value='<%=detail.get("scgs")%>' maxlength='<%=list.getColumn("scgs").getPrecision()%>' <%=readonly%>></td>
        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="ddgs_<%=i%>" name="ddgs_<%=i%>" value='<%=detail.get("ddgs")%>' maxlength='<%=list.getColumn("ddgs").getPrecision()%>' <%=readonly%>></td>
        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="lbjj_<%=i%>" name="lbjj_<%=i%>" value='<%=detail.get("lbjj")%>' maxlength='<%=list.getColumn("lbjj").getPrecision()%>' <%=readonly%>></td>
        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="hsj_<%=i%>" name="hsj_<%=i%>" value='<%=detail.get("hsj")%>' maxlength='<%=list.getColumn("hsj").getPrecision()%>' <%=readonly%>></td>
        <td noWrap class="td">
          <%if(isEdit){%>
          <input type="radio" name="sfwx_<%=i%>" value="1"<%=detail.get("sfwx").equals("1") ? " checked" : ""%>>
          是
          <input type="radio" name="sfwx_<%=i%>" value="0"<%=detail.get("sfwx").equals("0") ? " checked" : ""%>>
          否
          <%}else{%>
          <%=detail.get("sfwx").equals("1") ? "是" : "否"%>
          <%}%>
        </td>
        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="wxjg_<%=i%>" name="wxjg_<%=i%>" value='<%=detail.get("wxjg")%>' maxlength='<%=list.getColumn("wxjg").getPrecision()%>' <%=readonly%>></td>
        <td class="td" nowrap><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="bz_<%=i%>" name="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>'<%=readonly%>></td>
      </tr>
      <%  list.next();
      }
      for(; i < 6; i++){
    %>
      <tr>
        <td class="td" nowrap>&nbsp;</td>
        <td class="td" nowrap></td>
        <td class="td" nowrap></td>
        <td class="td" nowrap></td>
        <%--td class="td" nowrap></td--%>
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
      <table align="center">
        <tr>
          <td colspan="3" noWrap class="tableTitle">
            <%if(isEdit){%>
            <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
            <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
            <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
            <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/>
            <%}%>
            <%if(isCanDelete){%>
            <input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DEL%>);" value=" 删除 ">
            <pc:shortcut key="d" script='delMaster();'/>
            <%}%>
            <%--input name="btnback" type="button" class="button" onClick="parent.hideIframe();" value=" 返回(C) "--%>
            <%if(isEdit){%>
            <input type="hidden" name="multiIdInput" value="" onchange="sumitForm(<%=b_TechnicsRouteBean.MASTER_COPY%>)">
            <input name="btnback" class="button" type="button" value="复制给…" onClick="ProdMultiSelect('form1','srcVar=multiIdInput')">
            <%}%>
          </td>
        </tr>
      </table>
    </table>
&nbsp;</td>
    </tr>
  </TABLE>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
<%=b_TechnicsRouteBean.adjustInputSize(new String[]{"deje", "desl","scgs","ddgs","lbjj","hsj",/*"gdjg",*/"wxjg","bz"}, "form1", detailRows.length)%>
function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
  }
</SCRIPT>
<%out.print(retu);%>
</body>
</html>
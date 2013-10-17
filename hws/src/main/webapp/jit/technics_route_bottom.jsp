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
function toedit()
{
form1.submit();
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
  //engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp routeTypeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE_TYPE);
  engine.project.LookUp workCenterBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_CENTER);
  engine.project.LookUp workProduceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_PROCEDURE);
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_NAME);
  String curUrl = request.getRequestURL().toString();

  EngineDataSet list = b_TechnicsRouteBean.getDetailTable();
  synchronized(list){
    if(list.changesPending())
      b_TechnicsRouteBean.openDetailTable();
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
  <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="95%" align="center" bgcolor="">
    <%
       //prodBean.regData(ds,"cpid");
	%>
    <tr>
      <td colspan="17" noWrap>
        <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
          <tr class="tableTitle">
            <td nowrap width=10></td>
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
            <td class="td" nowrap> <%=workProduceBean.getLookupName(detail.get("gxfdid"))%>
            </td>
            <td class="td" nowrap> <%=technicsNameBean.getLookupName(detail.get("gymcid"))%>
            </td>
            <td class="td" nowrap> <%=workCenterBean.getLookupName(detail.get("gzzxid"))%>
            </td>
            <td class="td" nowrap>
              <input type="text" class=ednone onKeyDown="return getNextElement();" id="desl_<%=i%>" name="desl_<%=i%>" value='<%=detail.get("desl")%>' maxlength='<%=list.getColumn("desl").getPrecision()%>' readonly>
            </td>
            <td class="td" nowrap>
              <input type="text" class=ednone onKeyDown="return getNextElement();" id="deje_<%=i%>" name="deje_<%=i%>" value='<%=detail.get("deje")%>' maxlength='<%=list.getColumn("deje").getPrecision()%>' readonly>
            </td>
            <%--onchange="sumitForm(<%=b_TechnicsRouteBean.GDJG_ONCHANGE%>);" --%>
            <td noWrap class="td"> <%=list.getValue("jjff").equals("0") ? "计量单位计算":(list.getValue("jjff").equals("1")?"生产单位计算":(list.getValue("jjff").equals("2")?"换算单位计算":(list.getValue("jjff").equals("3")?"领料的计量单位除"+SYS_PRODUCT_SPEC_PROP:(list.getValue("jjff").equals("4")?"领料的计量单位":"等于计件单价"))))%>
            </td>
            <%--td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="gdjg_<%=i%>" name="gdjg_<%=i%>" value='<%=detail.get("gdjg")%>' maxlength='<%=list.getColumn("gdjg").getPrecision()%>' <%=readonly%>></td--%>
            <td class="td" nowrap>
              <input type="text" class=ednone onKeyDown="return getNextElement();" id="scgs_<%=i%>" name="scgs_<%=i%>" value='<%=detail.get("scgs")%>' maxlength='<%=list.getColumn("scgs").getPrecision()%>' readonly>
            </td>
            <td class="td" nowrap>
              <input type="text" class=ednone onKeyDown="return getNextElement();" id="ddgs_<%=i%>" name="ddgs_<%=i%>" value='<%=detail.get("ddgs")%>' maxlength='<%=list.getColumn("ddgs").getPrecision()%>' readonly>
            </td>
            <td class="td" nowrap>
              <input type="text" class=ednone onKeyDown="return getNextElement();" id="lbjj_<%=i%>" name="lbjj_<%=i%>" value='<%=detail.get("lbjj")%>' maxlength='<%=list.getColumn("lbjj").getPrecision()%>' readonly>
            </td>
            <td class="td" nowrap>
              <input type="text" class=ednone onKeyDown="return getNextElement();" id="hsj_<%=i%>" name="hsj_<%=i%>" value='<%=detail.get("hsj")%>' maxlength='<%=list.getColumn("hsj").getPrecision()%>' readonly>
            </td>
            <td noWrap class="td"> <%=detail.get("sfwx").equals("1") ? "是" : "否"%>
            </td>
            <td class="td" nowrap>
              <input type="text" class=ednone onKeyDown="return getNextElement();" id="wxjg_<%=i%>" name="wxjg_<%=i%>" value='<%=detail.get("wxjg")%>' maxlength='<%=list.getColumn("wxjg").getPrecision()%>' readonly>
            </td>
            <td class="td" nowrap>
              <input type="text" class=ednone  onKeyDown="return getNextElement();" id="bz_<%=i%>" name="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>'readonly>
            </td>
          </tr>
          <%  list.next();
      }
      for(; i < 6; i++){
    %>
          <tr>
            <td class="td" nowrap>&nbsp;</td>
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
        </table>
      </td>
    </tr>
  </TABLE>
</form>
<SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview1',1);
<%=b_TechnicsRouteBean.adjustInputSize(new String[]{"deje", "desl","scgs","ddgs","lbjj","hsj",/*"gdjg",*/"wxjg","bz"}, "form1", detailRows.length)%>
</SCRIPT>
<%}out.print(retu);%>
</body>
</html>
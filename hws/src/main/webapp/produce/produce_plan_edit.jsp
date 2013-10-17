<%--生产通用计划编辑页面从表--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.produce.B_ProducePlan producePlanBean = engine.erp.produce.B_ProducePlan.getInstance(request);
  String pageCode = "produce_plan";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
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
   function refresh(){
     form1.submit();
   }
   function backList()
   {
     location.href='produce_plan.jsp';
   }
   function productCodeSelect(obj, i)
   {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=ztqq_'+i,
                    'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=ztqq', obj.value,'product_change('+i+')');
   }
   function productNameSelect(obj,i)
  {
     ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=ztqq_'+i,
                    'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=ztqq', obj.value,'product_change('+i+')');
  }
  function propertyNameSelect(obj,cpid,i)
  {
    PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                       'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
  }
   </script>
   <%String retu = producePlanBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_PRODUCE_UNIT_STYLE = producePlanBean.SC_PRODUCE_UNIT_STYLE;
  String SYS_PRODUCT_SPEC_PROP =producePlanBean.SYS_PRODUCT_SPEC_PROP;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  String jhlx = producePlanBean.jhlx;//制定计划的类型，包括通用计划及分切计划。这里是指通用计划，value=0
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_STOCK);
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工序
  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_SALE_ORDER_GOODS);//根据销售合同货物id得到合同编号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = producePlanBean.getMaterTable();
  EngineDataSet list = producePlanBean.getDetailTable();
  HtmlTableProducer masterProducer = producePlanBean.masterProducer;
  HtmlTableProducer detailProducer = producePlanBean.detailProducer;
  RowMap masterRow = producePlanBean.getMasterRowinfo();
  RowMap[] detailRows= producePlanBean.getDetailRowinfos();
  String zt=masterRow.get("zt");
  boolean isEnd = producePlanBean.isReport || producePlanBean.isApprove || (!producePlanBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !producePlanBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(producePlanBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核") : (zt.equals("9") ? "审批中" : (zt.endsWith("2") ? "已生成物料需求" : (zt.equals("3") ? "已下达任务" : (zt.equals("8") ? "已完成" : "未审核"))));
  boolean isAdd = producePlanBean.isDetailAdd;
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
            <td class="activeVTab">生产计划维护(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                 <%deptBean.regData(ds,"deptid");%>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="jhh" value='<%=masterRow.get("jhh")%>' maxlength='<%=ds.getColumn("jhh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="jhrq" value='<%=masterRow.get("jhrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.jhrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110">
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td></td>
                  <td></td>
                </tr>
                <%/*打印用户自定义信息*/
                int j=0;
                while(j < mBakFields.length){
                  out.print("<tr>");
                  for(int k=0; k<4; k++)
                  {
                    out.print("<td noWrap class='tdTitle'>");
                    out.print(j < mBakFields.length ? mBakFields[j].getFieldname() : "&nbsp;");
                    out.print("</td><td noWrap class='td'");
                    if(j < mBakFields.length)
                    {
                      boolean isMemo = mBakFields[j].getType() == FieldInfo.MEMO_TYPE;
                      out.print(isMemo ? " colspan=7>" : ">");
                      String filedcode = mBakFields[j].getFieldcode();
                      String style = (isMemo ? "style='width:690'" : "style='width:110'")+ " onKeyDown='return getNextElement();'";
                      out.print(masterProducer.getFieldInput(mBakFields[j], masterRow.get(filedcode), filedcode, style, isEnd, true));
                      out.print("</td>");
                      if(isMemo)
                        break;
                    }
                    else
                      out.print(">&nbsp;</td>");
                    j++;
                  }
                  out.println("</tr>");
                }
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                           <%if(!isEnd){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%>
                        </td>
                        <td height='20' nowrap>销售合同号</td>
                        <td height='20' nowrap>单位名称</td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("gylxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("scsl").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("ywcl").getFieldname()%></td>
                        <td height='20' nowrap>生产单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("csl").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("ksrq").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("wcrq").getFieldname()%></td>
                        <td height='20' nowrap>出库数量</td>
                        <td nowrap><%=detailProducer.getFieldInfo("jgyq").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      saleOrderBean.regData(list,"hthwid");
                      propertyBean.regData(list,"dmsxid");
                      if(!isEnd)
                        technicsRouteBean.regConditionData(list,"cpid");
                      //importApplyBean.regData(list,"hthwid");
                      BigDecimal t_sl = new BigDecimal(0),t_ywcl = new BigDecimal(0), t_scsl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(producePlanBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String scsl = detail.get("scsl");
                        if(producePlanBean.isDouble(scsl))
                          t_scsl = t_scsl.add(new BigDecimal(scsl));
                        String ywcl = detail.get("ywcl");
                        if(producePlanBean.isDouble(ywcl))
                          t_ywcl = t_ywcl.add(new BigDecimal(ywcl));
                        String dmsxid = detail.get("dmsxid");
                        String sx = propertyBean.getLookupName(dmsxid);
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                        String gylxidName = "gylxid_"+i;
                        String dmsxidName = "dmsxid_"+i;
                        String hthwid = detail.get("hthwid");
                        boolean isimport = !hthwid.equals("");//从表当前行引入销售合同
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(!isEnd && !isimport){%>
                          <input type="hidden" name="singleIdInput_<%=i%>" value="">
                          <%--input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                          onClick="ProdSingleSelect('form1','srcVar=singleIdInput_<%=i%>','fieldVar=cpid','','sumitForm(<%=producePlanBean.SINGLE_PRODUCT_ADD%>,<%=i%>)')"--%>
                          <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                          onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=product_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=jldw_<%=i%>&srcVar=scydw_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=isprops_<%=i%>&srcVar=ztqq_<%=i%>','fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&fieldVar=isprops&fieldVar=ztqq','','product_change(<%=i%>)')">
                          <%}if(!isEnd){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                       <%RowMap saleOrderRow = saleOrderBean.getLookupRow(detail.get("hthwid"));%>
                        <td class="td" nowrap><%=saleOrderRow.get("htbh")%></td>
                        <td class="td" nowrap><%=saleOrderRow.get("dwmc")%></td>
                        <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));%>
                        <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="hidden" name="scdwgs_<%=i%>" value="<%=prodRow.get("scdwgs")%>">
                        <input type="hidden" name="widths_<%=i%>" value="<%=widths%>">
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                        <input type="hidden" name="ztqq_<%=i%>" value="<%=prodRow.get("ztqq")%>">
                        <input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                        <td class="td" nowrap>
                        <input <%=(isimport || isEnd) ? "class=ednone" : detailClass%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=isimport ? "readonly" : readonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd && !isimport){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if(form1.isprops_<%=i%>.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap>
                        <%if(isEnd)out.print("<input type='text' style='width:100' value='"+technicsRouteBean.getLookupName(detail.get("gylxid"))+"' class='ednone' readonly>");
                        else {%>
                        <pc:select name="<%=gylxidName%>" addNull="1" style='width:80'>
                        <%=technicsRouteBean.getList(detail.get("gylxid"),"cpid",detail.get("cpid"))%> </pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onblur="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone style="width:60" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="scsl_<%=i%>" name="scsl_<%=i%>" value='<%=detail.get("scsl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onblur="sl_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" nowrap><%=detail.get("ywcl")%></td>
                        <td class="td" nowrap><input type="text" class=ednone style="width:60" onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="csl_<%=i%>" name="csl_<%=i%>" value='<%=detail.get("csl")%>' maxlength='<%=list.getColumn("csl").getPrecision()%>' <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:65" onKeyDown="return getNextElement();" name="ksrq_<%=i%>" id="ksrq_<%=i%>"value='<%=detail.get("ksrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)"></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:65" onKeyDown="return getNextElement();" name="wcrq_<%=i%>" id="wcrq_<%=i%>"value='<%=detail.get("wcrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)"></td>
                        <td class="td" nowrap><%=saleOrderRow.get("stsl")%></td>
                        <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="jgyq_<%=i%>" id="jgyq_<%=i%>"value='<%=detail.get("jgyq")%>' maxlength='<%=list.getColumn("jgyq").getPrecision()%>' <%=readonly%>></td>
                        <%FieldInfo[] bakFields = detailProducer.getBakFieldCodes();
                        for(int k=0; k<bakFields.length; k++)
                        {
                          String fieldCode = bakFields[k].getFieldcode();
                          out.print("<td class='td' nowrap>");
                          out.print(detailProducer.getFieldInput(bakFields[k], detail.get(fieldCode), fieldCode+"_"+k, "style='width:65'", isEnd, true));
                          out.println("</td>");
                        }
                        %>
                      </tr>
                      <%list.next();
                      }
                      for(; i < 4; i++){
                  %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
                        <td align="right" class="td"><input id="t_ywcl" name="t_ywcl" type="text" class="ednone_r" style="width:100%" value='<%=t_ywcl%>' readonly></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
                     <tr>
                    <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhsm").getFieldname()%></td><%--其他信息--%>
                    <td colspan="7" noWrap class="td"><textarea name="jhsm" rows="3" onKeyDown="return getNextElement();" maxlength='<%=ds.getColumn("jhsm").getPrecision()%>' style="width:690"<%=readonly%>><%=masterRow.get("jhsm")%></textarea></td>
                    </tr>
                    </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("zdrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("zdr")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
            <%if(!isEnd){%>
              <input type="hidden" name="mutiSaleOrder" value="" onchange="sumitForm(<%=producePlanBean.MUTI_ORDER_ADD%>)"><INPUT type="hidden" NAME="selmethod" value="">
              <input name="btnback" class="button" type="button" value="引合同(W)" style="width:80" onClick="PlanSelOrder('form1','srcVar=mutiSaleOrder&selmethod=selmethod')">
              <pc:shortcut key="w" script="PlanSelOrder('form1','srcVar=mutiSaleOrder&selmethod=selmethod')"/>
              <input type="hidden" name="importorder" value="" onchange="sumitForm(<%=producePlanBean.DETAIL_PLAN_SELECT%>)">
              <input name="btnback" class="button" type="button" value="引合同货物(E)" style="width:100" onClick="importsale()" border="0">
              <pc:shortcut key="e" script="importsale()"/>
              <input type="hidden" name="storage" value="" onchange="sumitForm(<%=producePlanBean.DETAIL_SELECT_STORE%>)">
              <input name="btnback" class="button" type="button" value="按库存量生产(R)" style="width:115" onClick="StockMultiSelect('form1','srcVar=storage')" border="0">
              <pc:shortcut key="r" script="StockMultiSelect('form1','srcVar=storage')"/>
              <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
              <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" style="width:80" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
              <%if(isCanDelete && !producePlanBean.isReport){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
                <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <%if(!producePlanBean.isApprove && !producePlanBean.isReport){%><input name="btnback" type="button" class="button" onClick="backList();" style="width:60" value=" 返回(C)">
         <pc:shortcut key="c" script='backList();'/><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=producePlanBean.adjustInputSize(new String[]{"cpbm","product", "sl", "scsl", "jgyq", "sxz", "csl"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
  function product_change(i){
    document.all['dmsxid_'+i].value="";
    document.all['sxz_'+i].value="";
    document.all['widths_'+i].value="";
    associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '');
    var ksrqObj = document.all['ksrq_'+i];
    var wcrqObj = document.all['wcrq_'+i];
    var ztqqObj = document.all['ztqq_'+i];
    tmpDate = new Date();
    year= ''+tmpDate.getYear();
    month= ''+(tmpDate.getMonth()+1);
    date = ''+tmpDate.getDate();
    month = month.length<2 ? '0'+month : month;
    date = date.length<2 ? '0'+date : date;
    demo = year+'-'+month+'-'+date;
    ksrqObj.value=demo;
    if(ztqqObj.value=='' || isNaN(ztqqObj.value))
      wcrqObj.value = demo;
    else
      wcrqObj.value=addDate(ksrqObj.value,parseFloat(ztqqObj.value));
  }
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
  function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
  }
  function importsale()
  {
    OrderMultiSelect('form1','srcVar=importorder&selmethod=selmethod&jhlx='+<%=jhlx%>);
  }
    function sl_onchange(i, isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      var scslObj = document.all['scsl_'+i];
      var scygsObj = document.all['scdwgs_'+i];//生产公式
      var cslObj = document.all['csl_'+i];//超产率
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
      if(cslObj.value=="")
        return;
      if(isNaN(cslObj.value))
      {
        alert("输入的超产率非法");
        cslObj.focus();
        return;
      }
    }
    function cal_tot(type)
    {
      var tmpObj;
      var tot=0;
      for(i=0; i<<%=detailRows.length%>; i++)
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
      if(type == 'scsl')
        document.all['t_scsl'].value = formatQty(tot);
    }
    function OrderMultiSelect(frmName, srcVar, methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
      var winName= "GoodsProdSelector";
      paraStr = "../produce/plan_select_sale.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
    }
    function StockMultiSelect(frmName, srcVar, methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
      var winName= "GoodsProdSelector";
      paraStr = "../produce/plan_select_store.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
    }
    function PlanSelOrder(frmName,srcVar,fieldVar,curID,methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
      var winName= "PlanSelOrder";
      paraStr = "../produce/plan_mutiselect_order.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
    }
    function fileLink()
    {
      filename = prompt("文件名称:","");
      if (filename!=null) {
        addTxt = "<a href='javascript:' onclick=openUrlOpt2('../工艺文件/"
               + filename +"')>"+ filename +"</a>";
        form1.jhsm.value = addTxt;
      }
    }
  </script>
  <%if(producePlanBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</body>
</html>


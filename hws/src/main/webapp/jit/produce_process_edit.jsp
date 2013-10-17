<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve = "op_approve";
%><%
  engine.erp.jit.B_ProduceProcess produceProcessBean = engine.erp.jit.B_ProduceProcess.getInstance(request);
  String pageCode = "produce_process";
  produceProcessBean.sfwjg="0";
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
   function backList()
   {
     location.href='produce_process.jsp';
   }
   function productCodeSelect(obj, i)
   {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=ztqq_'+i,
                    'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=ztqq', obj.value,'product_change('+i+')');
     //ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=ztqq_'+i,'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=ztqq', obj.value);
   }
   function productNameSelect(obj,i)
   {
     ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=ztqq_'+i,
                    'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=ztqq', obj.value,'product_change('+i+')');
     //ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=ztqq_'+i,'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=ztqq', obj.value);
   }
   function propertyNameSelect(obj,cpid,i)
   {
     PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                          'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
   }
   function product_change(i){
  document.all['dmsxid_'+i].value="";
  document.all['sxz_'+i].value="";
  document.all['widths_'+i].value="";
  //associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '', true);
}
</script>
<%String retu = produceProcessBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_PRODUCE_UNIT_STYLE = produceProcessBean.SC_PRODUCE_UNIT_STYLE;//是否强制换算
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE_TYPE);//根据工艺路线id得到工序
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp taskBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_TASK_GOODS);//通过任务单明细id得到任务单号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性LookUp
  engine.project.LookUp gxfdBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_PROCEDURE);
  engine.project.LookUp wlxqBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_MRP_GOODS);
  engine.project.LookUp producePlanBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PLAN);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp htbhBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PLAN_DETAIL);
  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//通过工作组id得到工作组名称
  engine.project.LookUp mprodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_NAME);//工序 BEAN_TECHNICS_NAME
  engine.project.LookUp processcardBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PROCESS_CARD_DETAIL);//工序 BEAN_TECHNICS_NAME



  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = produceProcessBean.getMaterTable();
  EngineDataSet list = produceProcessBean.getDetailTable();//加工单信息
  //EngineDataSet dsDrawMaterail= produceProcessBean.getCommonMaterail();
  //EngineDataView dsMaterail = dsDrawMaterail.cloneEngineDataView();//通用加工单中，列表显示用
  //EngineDataView dsMaterail = produceProcessBean.getDataView();//加工单领料清单
  HtmlTableProducer masterProducer = produceProcessBean.masterProducer;
  HtmlTableProducer detailProducer = produceProcessBean.detailProducer;
  //HtmlTableProducer materailProducer = produceProcessBean.commonProducer;//领料清单
  RowMap masterRow = produceProcessBean.getMasterRowinfo();
  RowMap[] detailRows= produceProcessBean.getDetailRowinfos();
  //RowMap[] materailRows= produceProcessBean.getMaterailRowinfos();//领料清单
  String zt = masterRow.get("zt");
  boolean isCanAmend = true;//判断取消审批后主表数据是否能修改
  boolean isCanRework = true;//判断取消审批后从表数据是否能修改
  if(zt.equals("0"))
    isCanAmend = engine.erp.baseinfo.BasePublicClass.isRework(list,"ypgzl");//取消审批后从表已排工作量如果有一条大于零，主表不能修改。
  boolean isEdit = zt.equals("0") && (produceProcessBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));//在修改状态,并有修改权限
  boolean isCanDelete = !produceProcessBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete) && zt.equals("0");//没有结束,在修改状态,并有删除权限

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = !isEdit ? "class=edline" : "class=edbox";
  String detailClass = !isEdit ? "class=ednone" : "class=edFocused";
  String detailClass_r = !isEdit ? "class=ednone_r" : "class=edFocused_r";
  String readonly = !isEdit ? " readonly" : "";//没有修改权限时
  String masterReadonly = isCanAmend ? readonly : "readonly";
  boolean isAdd = produceProcessBean.isDetailAdd;
  String SYS_PRODUCT_SPEC_PROP =produceProcessBean.SYS_PRODUCT_SPEC_PROP;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  String jglx = produceProcessBean.getTaskType();

  String registeredPersonBean = masterRow.get("gzzid").equals("")?engine.project.SysConstant.BEAN_PERSON:engine.project.SysConstant.BEAN_GZZ_PERSON;
  boolean isGzzChoiced = masterRow.get("gzzid").equals("")?false:true;
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, registeredPersonBean);

%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="scjhid" value="<%=masterRow.get("scjhid")%>">
  <INPUT TYPE="HIDDEN" NAME="processmxid" value="<%=masterRow.get("processmxid")%>">
  <INPUT TYPE="HIDDEN" NAME="sdeptid" value="">
  <INPUT TYPE="HIDDEN" NAME="gylxmxid" value="<%=masterRow.get("gylxmxid")%>">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="800">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">生产加工单</td>
        </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                 <%
                   processcardBean.regData(ds,"processmxid");
                   technicsNameBean.regData(ds,"gymcid");
                   mprodBean.regData(ds,"cpid");
                   workShopBean.regData(ds,"deptid");
                   deptBean.regData(ds,"bm_deptid");//制定部门
                   producePlanBean.regData(ds, "scjhid");
                   gxfdBean.regConditionData(ds,"deptid");
                   workGroupBean.regConditionData(ds,"deptid");
                   RowMap  mprodRow = mprodBean.getLookupRow(masterRow.get("cpid"));
                   RowMap processrow = processcardBean.getLookupRow(masterRow.get("processmxid"));
                 %>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jgdh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="jgdh" value='<%=masterRow.get("jgdh")%>' maxlength='<%=ds.getColumn("jgdh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("kdrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="kdrq" value='<%=masterRow.get("kdrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=masterReadonly%>>
                    <%if(isEdit && isCanAmend){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.kdrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bm_deptid").getFieldname()%></td>
                  <%//String sumit = "if(form1.bm_deptid.value!='"+masterRow.get("bm_deptid")+"'){sumitForm("+produceProcessBean.ONCHANGE+");}";
                  %>
                  <td noWrap class="td">
                    <%if(!isEdit || !isCanAmend) out.print("<input type='hidden' name='bm_deptid' value='"+masterRow.get("bm_deptid")+"'><input type='text' value='"+deptBean.getLookupName(masterRow.get("bm_deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="bm_deptid" addNull="1" style="width:110">
                      <%=deptBean.getList(masterRow.get("bm_deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">加工车间</td>
                  <%String sumit0 = "sumitForm("+produceProcessBean.ONCHANGE+");";%>
                  <td  noWrap class="td">
                  <%if(list.getRowCount()>0||!isEdit){%>
                   <input type='hidden' value='<%=masterRow.get("deptid")%>' style='width:100' class='edline' readonly>
                   <input type='text' value='<%=deptBean.getLookupName(masterRow.get("deptid"))%>' style='width:100' class='edline' readonly>
                   <%}
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:100" onSelect="<%=sumit0%>">
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <%--td noWrap class="tdTitle">加工车间</td>
                  <%
                    String sumit = "sumitForm("+produceProcessBean.ONCHANGE+")";
                  %>
                  <td noWrap class="td">
                  <input type='hidden' name='deptid' value='<%=masterRow.get("deptid")%>'>
                    <%if(!isEdit) out.print("<input type='hidden' name='deptid' value='"+masterRow.get("deptid")+"'><input type='text' value='"+workShopBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="deptid" addNull="1" onSelect="<%=sumit%>" style="width:110">
                      <%=workShopBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td--%>

                  <td></td>
                  <td></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">工段名称</td>
                  <td class="td" nowrap>
                    <input type="text" style="width:110" class="edline" onKeyDown="return getNextElement();" value='<%=gxfdBean.getLookupName(masterRow.get("gxfdid"))%>' readonly>
                    <input type="hidden"  id="gxfdid" name="gxfdid"  value='<%=masterRow.get("gxfdid")%>' >
                 <%--
                   if(!isEdit || !isCanRework)out.print("<input type='text' style='width:100' value='"+gxfdBean.getLookupName(masterRow.get("gxfdid"))+"' class='ednone' readonly>");
                  else {%>
                  <pc:select name="gxfdid" addNull="1" style='width:100'>
                  <%=gxfdBean.getList(masterRow.get("gxfdid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                  <%}--%>
                  </td>
                   <td noWrap class="tdTitle">工序</td>
                   <td class="td" nowrap>
                    <input type="text" style="width:110" class="edline" onKeyDown="return getNextElement();" value='<%=technicsNameBean.getLookupName(masterRow.get("gymcid"))%>' readonly>
                    <input type="hidden"  id="gymcid" name="gymcid"  value='<%=masterRow.get("gymcid")%>' >
                   </td>



                  </tr>
                  <tr>
                  <TD align="center" nowrap class="tdTitle">品名规格</TD>
                  <td class="td" nowrap colspan="3">
                   <input type="text" class="edline"  style="width:70" onKeyDown="return getNextElement();" id="cpbm" name="cpbm" value='<%=mprodRow.get("cpbm")%>'  readonly>
                   <input type="text" class="edline"  style="width:120" onKeyDown="return getNextElement();" id="product" name="product" value='<%=mprodRow.get("product")%>'  readonly>
                   <input type="hidden" name="cpid" value="<%=masterRow.get("cpid")%>">
                   <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"    onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=product&srcVar=cpbm',   'fieldVar=cpid&fieldVar=product&fieldVar=cpbm','', 'sumitForm(<%=produceProcessBean.DWTXONCHNAGE%>,-1)')">
                  </td>
                   <td noWrap class="tdTitle">生产流程卡号</td>
                   <td class="td" nowrap>
                    <input type="text" style="width:110" class="edline" onKeyDown="return getNextElement();" value='<%=processrow.get("processdm")%>' readonly>
                   </td>
                   <td noWrap class="tdTitle">单号</td>
                   <td class="td" nowrap>
                    <input type="text" style="width:110" class="edline" onKeyDown="return getNextElement();" value='<%=processrow.get("djh")%>' readonly>
                   </td>
                </tr>
                <%/*打印用户自定义信息*/
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:800;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                       <%if(isEdit){%>
                         <%--input type='checkbox' name='checkform' onclick='checkAll(form1,this);'--%>
                        <input class="edFocused_r"  name="tCopyNumber" value="1" title="拷贝(ALT+A)" size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                       <%}%>
                           <%if(isEdit && !jglx.equals("1")){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
                           <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%>
                        </td>
                         <%
                        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length;i++)
                          out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
                        %>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap>换算数量</td>
                         <td nowrap>换算单位</td>
                         <td nowrap>工作组</td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("cpl").getFieldname()%>%</td>
                        <td nowrap><%=detailProducer.getFieldInfo("ksrq").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("wcrq").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jgyq").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("wgcl").getFieldname()%></td>
                        <td nowrap>返工</td>
                      </tr>
                    <%
                       propertyBean.regData(list,"dmsxid");
                       prodBean.regData(list,"cpid");
                       htbhBean.regData(list,"scjhmxid");
                      BigDecimal t_sl = new BigDecimal(0), t_ypgzl = new BigDecimal(0),t_scsl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++){
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(produceProcessBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String scsl = detail.get("scsl");
                        if(produceProcessBean.isDouble(scsl))
                          t_scsl = t_scsl.add(new BigDecimal(scsl));
                        String ypgzl = detail.get("ypgzl");
                        if(produceProcessBean.isDouble(ypgzl))
                          t_ypgzl = t_ypgzl.add(new BigDecimal(ypgzl));
                        //String gylxidName = "gylxid_"+i;
                        String wlxqjhmxid = detail.get("wlxqjhmxid");
                        String dmsxid = detail.get("dmsxid");
                        String wgcl = detail.get("wgcl");
                        String scjhmxid = detail.get("scjhmxid");


                        String sx = propertyBean.getLookupName(dmsxid);
                        RowMap wlxqrow = wlxqBean.getLookupRow(wlxqjhmxid);
                        String scjhh = producePlanBean.getLookupName(masterRow.get("scjhid"));
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用

                        boolean isimport = !scjhmxid.equals("");//!wlxqjhmxid.equals("");//是否是引用的物料需求里的货物
                        if(zt.equals("0"))
                          isCanRework = engine.erp.baseinfo.BasePublicClass.isRevamp(list, "ypgzl", i);//加工单状态在未审状态时，判断该条纪录是否能被修改
                        String detailReadonly = isCanRework ? readonly : "readonly";
                        boolean isline = isimport || !isCanRework;
                        //boolean isline = !isCanRework;
                        String Class = isline ? "class=ednone" : detailClass;//从表Class模式
                        String htbh = htbhBean.getLookupName(scjhmxid);
                        RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                        String personVariable = "personid_"+i;
                        String jgdid = list.getValue("jgdid");
                        String jgdmxid = list.getValue("jgdmxid");
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                        <input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="hidden" name="scdwgs_<%=i%>" value="<%=prodRow.get("scdwgs")%>">
                        <input type="hidden" name="widths_<%=i%>" value="<%=widths%>">
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                         <%if(isEdit && isCanRework){%>
                         <%--input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"    onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=product_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=jldw_<%=i%>',   'fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fieldVar=jldw','', 'sumitForm(<%=produceProcessBean.DWTXONCHNAGE%>,<%=i%>)')"--%>
                        <%--input type="checkbox" name="sel" value="<%=detail.get("internalRowNum")%>" onKeyDown="return getNextElement();"--%>
                        <input name="image" class="img" type="image" title="复制当前行" onClick="sumitForm(<%=produceProcessBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}//wlxqrow.get("WLXQH")
                          %>
                        </td>
                        <td class="td" nowrap>
                        <input type="text" class="ednone" onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" readonly ></td>
                        <td class="td" nowrap><input type="text"  class="ednone"  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)"  readonly ></td>
                        <td class="td" nowrap>
                        <input <%=Class%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=isimport ? "readonly" : detailReadonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(isEdit && !isimport && isCanRework){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if(form1.isprops_<%=i%>=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" align="center" nowrap>
                        <input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly>
                        <input type="hidden" <%=detailClass_r%> onKeyDown="return getNextElement();" id="scsl_<%=i%>" name="scsl_<%=i%>" value='<%=detail.get("scsl")%>' maxlength='<%=list.getColumn("scsl").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)" <%=readonly%>>
                        <input type="hidden" class=ednone style="width:65" onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>'  <%=readonly%>></td>
                        <td class="td" align="center" nowrap>
                        <input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly>
                        </td>

                         <td class="td" nowrap>
              <%if(!isEdit) out.print("<input type='text' value='"+workGroupBean.getLookupName(detail.get("gzzid"))+"' style='width:110' class='edline' readonly>");
                else {
      %>
              <pc:select addNull="1" className="edFocused" name='<%="gzzid_"+i%>' style="width:90">
               <%=workGroupBean.getList(detail.get("gzzid"),"deptid",masterRow.get("deptid"))%> </pc:select>
              <%}%>
            </td>
                        <td class="td" nowrap ><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="cpl_<%=i%>" name="cpl_<%=i%>" value='<%=detail.get("cpl")%>' maxlength='<%=list.getColumn("cpl").getPrecision()%>' <%=readonly%>>
                        <td class="td" nowrap ><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" onChange="checkDate(this)" id="ksrq_<%=i%>" name="ksrq_<%=i%>" value='<%=detail.get("ksrq")%>' maxlength='<%=list.getColumn("ksrq").getPrecision()%>' <%=readonly%>>
                        <%if(isEdit && isCanRework){%>
                        <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.ksrq_<%=i%>);"></a>
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" onChange="checkDate(this)" id="wcrq_<%=i%>" name="wcrq_<%=i%>" value='<%=detail.get("wcrq")%>' maxlength='<%=list.getColumn("wcrq").getPrecision()%>' <%=readonly%>>
                        <%if(isEdit && isCanRework){%>
                         <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.wcrq_<%=i%>);"></a>
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="jgyq_<%=i%>" id="jgyq_<%=i%>"value='<%=detail.get("jgyq")%>' maxlength='<%=list.getColumn("jgyq").getPrecision()%>' <%=readonly%>></td>
                        <td class="td" nowrap>
                        <pc:select name='<%="wgcl_"+i%>' style="width:110" value='<%=detail.get("wgcl")%>'>
                        <pc:option value='2'>流转</pc:option>
                        <pc:option value='1'>入库</pc:option>
                        </pc:select>
                        </td>
                        <td class="td" nowrap>
                        <pc:select name='<%="rtrn_"+i%>' style="width:110" value='<%=detail.get("rtrn")%>'>
                        <pc:option value='0'>否</pc:option>
                        <pc:option value='1'>是</pc:option>
                        </pc:select>
                        </td>
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
                  <td class="td">&nbsp;</td>
                      <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_scsl" name="t_scsl" type="hidden" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
                        <%--td align="right" class="td"><input id="t_ypgzl" name="t_ypgzl" type="text" class="ednone_r" style="width:100%" value='<%=t_ypgzl%>' readonly></td--%>
                        <td class="td">&nbsp;</td>
                  <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
                     <tr>
                    <td  noWrap class="tdTitle">加工要求说明</td><%--其他信息--%>
                    <td colspan="7" noWrap class="td"><textarea name="describe" rows="3" onKeyDown="return getNextElement();" style="width:660"<%=masterReadonly%>><%=masterRow.get("describe")%></textarea></td>
                    </tr>
                    </td>
                    </tr>
                    </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>

                    <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
                      <tr>
                      <td class="td"><b>登记日期:</b><%=masterRow.get("zdrq")%></td>
                      <td class="td"></td>
                      <td class="td" align="right"><b>制单人:</b><%=masterRow.get("zdr")%></td>
                     </tr>
                     <tr>

                      <td colspan="3" noWrap class="tableTitle">
                       <%if(isEdit&&list.getRowCount()==0){%>
                       <input name="btnback" class="button" type="button" value="引生产流程卡(W)" onClick="selctbilloflading()" border="0">
                       <pc:shortcut key="w" script="selctbilloflading()" />

                       <%}%>
                       <%if(isEdit&&list.getRowCount()>0){%>
                       <input name="btnback" class="button" type="button" value="查看生产计划" onClick="vw_scjh('<%=masterRow.get("processmxid")%>')" border="0">
                      <%}%>
                        <%if(isEdit){%>
                         <%--input name="button3" type="button" class="button" onClick="sumitForm(<%=produceProcessBean.DEL_SELECT%>);" value=" 删除所选行"--%>
                         <input name="btnback" type="button" class="button" onClick="sumitForm(<%=produceProcessBean.COMMON_POST%>);" value="保存(S)">
                        <pc:shortcut key="s" script='<%="sumitForm("+ produceProcessBean.COMMON_POST +",-1)"%>'/><%}%>
                        <%if(isCanDelete){%>
                         <input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=produceProcessBean.COMMONDETAIL_DEL%>);" value=" 删除(D)">
                         <pc:shortcut key="d" script='delMaster();'/>
 w
                         <%}%>
                        <input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)"> <pc:shortcut key="c" script='backList();'/>
                          </td>
                          </tr>
                        </table>
      </td>
    </tr>
  </table>
</form>

<script language="javascript">initDefaultTableRow('tableview1',1);//initDefaultTableRow('tableview2',1);
<%=produceProcessBean.adjustInputSize(new String[]{"cpbm", "product","wgcl","rtrn","jldw","hsdw","sxz", "sl","gzzid","scsl","ksrq","wcrq","hssl","jgyq","cpl"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
  function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= produceProcessBean.COMMONDETAIL_DEL%>,-1);
  }
  function selctbilloflading()
  {
    if(form1.deptid.value=='')
    {
      alert('请选择加工车间!')
      return;
    }
      OrderSingleSelect('form1','srcVar=processmxid&srcVar=cpid&srcVar=sdeptid&srcVar=gymcid&srcVar=gylxmxid&srcVar=gxfdid&srcVar=scjhid','fieldVar=processmxid&fieldVar=cpid&fieldVar=deptid&fieldVar=gymcid&fieldVar=gylxmxid&fieldVar=gxfdid&fieldVar=scjhid',form1.deptid.value,"sumitForm(<%=produceProcessBean.IMPORT_PROCESSCARD%>,-1)");
  }
  function OrderSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
  {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=450  top=0 left=0";
      var winName= "SingleladingSelector";
      paraStr = "../jit/select_processcard.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+curID;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
}
function vw_scjh(processmxid)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=900 height=600 top=500 left=0";
  var winName= "vsscjh";
  paraStr = "../jit/view_scjh.jsp?operate=0&processmxid="+processmxid;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
  function importTask(){
    if(form1.deptid.value==''||form1.gxfdid.value=='')
    {
      alert('请选择加工车间或工序分段');
      return;
    }
    ProcessSelTask('form1','srcVar=singleImportTask','fieldVar=wlxqjhid',form1.deptid.value, form1.gxfdid.value, '<%=jglx%>','sumitForm(<%=produceProcessBean.SINGLE_SELECT_TASK%>)');
  }
  function taskDetail(){
    if(form1.deptid.value=='')
    {
      alert('请选择车间'); return;
    }
    TaskGoodsSelect('form1','srcVar=mutitask&deptid='+form1.deptid.value)
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
     function checkCsl()
     {
       var cslObj = document.all['csl_'+i];
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
         else if(type == 'scsl')
           document.all['t_scsl'].value = formatQty(tot);
   }
   function CommonMaterail(rownum,srcParam)
   {
     var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
     var winName= "MrpGoodsSelector";
     paraStr = "../jit/process_make_materail.jsp?operate=11005&rownum="+rownum+"&"+srcParam;
     openUrlOpt2(paraStr);
     //newWin =window.open(paraStr,winName,winopt);
     //newWin.focus();
   }
  function CommonMaterailRefresh(rownum,srcParam)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
    var winName= "MrpGoodsSelector";
    paraStr = "../jit/process_make_materail.jsp?operate=2004&rownum="+rownum+"&"+srcParam;
    openUrlOpt2(paraStr);
  }
  function ProcessSelTask(frmName,srcVar,fieldVar,deptid,gxfdid, jglx,methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
    var winName= "ProcessSelTask";
    paraStr = "../jit/process_singlesel_task.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+deptid+"&gxfdid="+gxfdid+"&jglx="+jglx+"&multi=1";
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function ProdSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
  {
    paraStr = "../pub/productselect.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+curID;
    if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    openSelectUrl(paraStr, "SingleProdSelector", winopt2)
  }
    function MakeMaterail(frmName, srcVar, methodName,notin)//加工单明细生成加工单物料
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
      var winName= "MakeMrp";
      paraStr = "../jit/process_make_materail.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
    }
  </script>
  <%if(produceProcessBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</body>
</html>
<%--生产加工单编辑页面从表--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve = "op_approve";
%><%
  engine.erp.jit.B_ProcessCard B_ProcessCardBean = engine.erp.jit.B_ProcessCard.getInstance(request);
  String pageCode = "process_card";
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
     location.href='process_card.jsp';
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
<%String retu = B_ProcessCardBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_PRODUCE_UNIT_STYLE = B_ProcessCardBean.SC_PRODUCE_UNIT_STYLE;//是否强制换算
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
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_NAME);
  engine.project.LookUp technicsRouteDetailBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE);


  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = B_ProcessCardBean.getMaterTable();
  EngineDataSet list = B_ProcessCardBean.getDetailTable();//加工单信息
  //EngineDataSet dsDrawMaterail= B_ProcessCardBean.getCommonMaterail();
  //EngineDataView dsMaterail = dsDrawMaterail.cloneEngineDataView();//通用加工单中，列表显示用
  //EngineDataView dsMaterail = B_ProcessCardBean.getDataView();//加工单领料清单
  HtmlTableProducer masterProducer = B_ProcessCardBean.masterProducer;
  HtmlTableProducer detailProducer = B_ProcessCardBean.detailProducer;
  //HtmlTableProducer materailProducer = B_ProcessCardBean.commonProducer;//领料清单
  RowMap masterRow = B_ProcessCardBean.getMasterRowinfo();
  RowMap[] detailRows= B_ProcessCardBean.getDetailRowinfos();
  //RowMap[] materailRows= B_ProcessCardBean.getMaterailRowinfos();//领料清单
  String zt = masterRow.get("zt");
  boolean isCanAmend = true;//判断取消审批后主表数据是否能修改
  boolean isCanRework = true;//判断取消审批后从表数据是否能修改

  boolean isEdit = zt.equals("0") && (B_ProcessCardBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));//在修改状态,并有修改权限
  boolean isCanDelete = !B_ProcessCardBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete) && zt.equals("0");//没有结束,在修改状态,并有删除权限

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = !isEdit ? "class=edline" : "class=edbox";
  String detailClass = !isEdit ? "class=ednone" : "class=edFocused";
  String detailClass_r = !isEdit ? "class=ednone_r" : "class=edFocused_r";
  String readonly = !isEdit ? " readonly" : "";//没有修改权限时
  String masterReadonly = isCanAmend ? readonly : "readonly";
  boolean isAdd = B_ProcessCardBean.isDetailAdd;
  String SYS_PRODUCT_SPEC_PROP =B_ProcessCardBean.SYS_PRODUCT_SPEC_PROP;//生产用位换算的相关规格属性名称 得到的值为“宽度”……

%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="scjhID" value="<%=masterRow.get("scjhid")%>">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">生产流程卡</td>
        </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                 <%

                   deptBean.regData(ds,"deptid");//制定部门
                   producePlanBean.regData(ds, "scjhid");
                   //gxfdBean.regConditionData(ds,"deptid");

                 %>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("processdm").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="processdm" value='<%=masterRow.get("processdm")%>' maxlength='<%=ds.getColumn("processdm").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("kdrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="kdrq" value='<%=masterRow.get("kdrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=masterReadonly%>>
                    <%if(isEdit && isCanAmend){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.kdrq);"></a>
                    <%}%>
                  </td>

                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(!isEdit || !isCanAmend) out.print("<input type='hidden' name='deptid' value='"+masterRow.get("deptid")+"'><input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110">
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td></td>
                  <td></td>
                </tr>
                <tr>
                <%RowMap jhrow = producePlanBean.getLookupRow(masterRow.get("scjhid"));%>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("scjhID").getFieldname()%></td>
                  <td noWrap class="td">
                     <input type="text" name="jhh" value='<%=jhrow.get("jhh")%>' style="width:100%" class="edline" onKeyDown="return getNextElement();" readonly>
                  </td>
                  <td noWrap class="tdTitle">单号</td>
                  <td noWrap class="td">
                     <input type="text" name="djh" value='<%=jhrow.get("djh")%>' style="width:100%" class="edline" onKeyDown="return getNextElement();" readonly>
                  </td>
                </tr>
                <%
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                           <%--if(isEdit){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
                           <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}--%>
                        </td>

                         <%
                        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length;i++)
                          out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
                        %>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap>加工车间</td>
                        <td nowrap>加工工段</td>
                        <td nowrap>加工工序</td>
                        <td nowrap><%=detailProducer.getFieldInfo("rcvnmbr").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap>换算数量</td>
                        <td height='20' nowrap>换算</td>
                        <td height='20' nowrap>已完成数量</td>
                        <td height='20' nowrap>所占比例(%)</td>
                        <%--td nowrap>换算数量</td>
                        <td nowrap>换算单位</td--%>
                        <td nowrap><%=detailProducer.getFieldInfo("jhrq").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sjrq").getFieldname()%></td>
                        <td nowrap>完工处理</td>
                        <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                      </tr>
                    <%
                        technicsRouteDetailBean.regData(list,"gylxmxid");
                       technicsNameBean.regData(list,"gymcid");
                       workShopBean.regData(list,"deptid");
                       propertyBean.regData(list,"dmsxid");
                       prodBean.regData(list,"cpid");
                       htbhBean.regData(list,"scjhmxid");

                      BigDecimal t_sl = new BigDecimal(0), t_scsl = new BigDecimal(0);
                      int i=0;
                      double zbl = 0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++){
                        detail = detailRows[i];
                        String sl = detail.get("rcvnmbr");
                        if(B_ProcessCardBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));

                        String dmsxid = detail.get("dmsxid");
                        String scjhmxid = detail.get("scjhmxid");
                        String sx = propertyBean.getLookupName(dmsxid);
                        String processmxid = detail.get("processmxid");

                        String scjhh = producePlanBean.getLookupName(masterRow.get("scjhid"));
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用

                        boolean isimport = !scjhmxid.equals("");//!wlxqjhmxid.equals("");//是否是引用的物料需求里的货物
                        String detailReadonly = isCanRework ? readonly : "readonly";
                        boolean isline = isimport || !isCanRework;

                        String Class = isline ? "class=ednone" : detailClass;//从表Class模式
                        RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                        RowMap routrow = technicsRouteDetailBean.getLookupRow(detail.get("gylxmxid"));
                        double zybl = Double.parseDouble(routrow.get("zybl").equals("")?"0":routrow.get("zybl"));
                        double ywcsl = Double.parseDouble(detail.get("ywcsl").equals("")?"0":detail.get("ywcsl"));
                        double rcvnmbr = Double.parseDouble(detail.get("rcvnmbr").equals("")?"0":detail.get("rcvnmbr"));
                        double bl = 0;
                        if(rcvnmbr!=0)
                          bl = Double.parseDouble(engine.util.Format.formatNumber((ywcsl/rcvnmbr>1?1:ywcsl/rcvnmbr)*zybl,"0.00"));
                        zbl = zbl+bl;
                        //ArrayList[] lists  = B_ProcessCardBean.getOptionGylx(detail.get("gylxmxid"));
                        //ArrayList opkey  = (ArrayList)lists[1];

                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                        <%if(loginBean.getDeptID().equals(detail.get("deptid"))){%>

                        <%if(!processmxid.equals("")&&zt.equals("1")&&detail.get("statte").equals("0")){%>
                        <input name="image" class="img" type="image" title="完成" onClick="if(confirm('是否完成该记录？')) sumitForm(<%=B_ProcessCardBean.DETAIL_COMPLETE%>,<%=i%>)" src="../images/ok.gif" border="0">
                        <%if(ywcsl<rcvnmbr){%>
                         <input name="image233" type="image" class="img" title="生成领料单" onClick="select_product('deptid=<%=detail.get("deptid")%>&cpid=<%=detail.get("cpid")%>&processmxid=<%=processmxid%>&gymcid=<%=detail.get("gymcid")%>&gylxmxid=<%=detail.get("gylxmxid")%>&processid=<%=detail.get("processid")%>')" src="../images/edit.old.gif" width="15" height="16" border="0">
                         <%--input name="image23" type="image" class="img" title="计件工作量" onClick="piece_rate('deptid=<%=detail.get("deptid")%>&cpid=<%=detail.get("cpid")%>&processmxid=<%=processmxid%>&gymcid=<%=detail.get("gymcid")%>&gylxmxid=<%=detail.get("gylxmxid")%>&gxfdid=<%=detail.get("gxfdid")%>')" src="../images/bulletin.gif" width="16" height="16" border="0"--%>
                         <input name="image23" type="image" class="img" title="加工产品明细" onClick="sc_jgd('deptid=<%=detail.get("deptid")%>&cpid=<%=detail.get("cpid")%>&processmxid=<%=processmxid%>&gymcid=<%=detail.get("gymcid")%>&gylxmxid=<%=detail.get("gylxmxid")%>&gxfdid=<%=detail.get("gxfdid")%>&scjhid=<%=masterRow.get("scjhid")%>')" src="../images/a4.gif" width="16" height="16" border="0">
                        <%}%><%}%><%}%>
                        </td>
                        <td class="td" nowrap>
                        <input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="hidden" name="scdwgs_<%=i%>" value="<%=prodRow.get("scdwgs")%>">
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">

                        <input type="text" class="ednone" onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>'  Readonly></td>
                        <td class="td" nowrap><input type="text"  class="ednone"  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>'  Readonly></td>
                        <td class="td" nowrap>
                        <input <%=Class%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=isimport ? "readonly" : detailReadonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(isEdit && !isimport && isCanRework){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if(form1.isprops_<%=i%>=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>

                        <td class="td" nowrap><%=workShopBean.getLookupName(detail.get("deptid"))%>
                        <input type="hidden"  id="deptid_<%=i%>" name="deptid_<%=i%>" value='<%=detail.get("deptid")%>' >
                        </td>
                        <td class="td" nowrap><%=gxfdBean.getLookupName(detail.get("gxfdid"))%>
                        <input type="hidden"  onKeyDown="return getNextElement();" id="gxfdid_<%=i%>" name="gxfdid_<%=i%>" value='<%=detail.get("gxfdid")%>' maxlength='<%=list.getColumn("gxfdid").getPrecision()%>'  <%=readonly%>>
                         </td>
                        <td class="td" nowrap>
                        <%--if(isEdit){%>

                        <pc:select name='<%="gymcid_"+i%>' addNull="0"    style="width:130"  >
                        <%=B_ProcessCardBean.listToOption(lists, opkey.indexOf(detail.get("gymcid")))%>
                        </pc:select>
                        <%}else{--%>

                       <%=technicsNameBean.getLookupName(detail.get("gymcid"))%>
                        <input type="hidden" <%=detailClass_r%> onKeyDown="return getNextElement();" id="gymcid_<%=i%>" name="gymcid_<%=i%>" value='<%=detail.get("gymcid")%>' maxlength='<%=list.getColumn("gymcid").getPrecision()%>'  <%=readonly%>>
                        <%--}--%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="rcvnmbr_<%=i%>" name="rcvnmbr_<%=i%>" value='<%=detail.get("rcvnmbr")%>' maxlength='<%=list.getColumn("rcvnmbr").getPrecision()%>'  <%=readonly%>></td>
                        <td class="td" align="center" nowrap>
                        <input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                       <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="rcvbgnmbr_<%=i%>" name="rcvbgnmbr_<%=i%>" value='<%=detail.get("rcvbgnmbr")%>' maxlength='<%=list.getColumn("rcvbgnmbr").getPrecision()%>'  <%=readonly%>></td>
                      <td class="td" align="center" nowrap>
                       <input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                        <td class="td" align="center" nowrap>
                        <input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="ywcsl_<%=i%>" name="ywcsl_<%=i%>" value='<%=detail.get("ywcsl")%>' readonly></td>
                        <td class="td" align="center" nowrap>
                        <input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="bl_<%=i%>" name="bl_<%=i%>" value='<%=bl+""%>' readonly></td>
                        <td class="td" nowrap ><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" onChange="checkDate(this)" id="jhrq_<%=i%>" name="jhrq_<%=i%>" value='<%=detail.get("jhrq")%>' maxlength='<%=list.getColumn("jhrq").getPrecision()%>' <%=readonly%>>
                        <%if(isEdit && isCanRework){%>
                        <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.jhrq_<%=i%>);"></a>
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" onChange="checkDate(this)" id="sjrq_<%=i%>" name="sjrq_<%=i%>" value='<%=detail.get("sjrq")%>' maxlength='<%=list.getColumn("sjrq").getPrecision()%>' <%=readonly%>>
                        <%if(isEdit && isCanRework){%>
                         <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.sjrq_<%=i%>);"></a>
                        <%}%>
                        </td>
                        <td class="td" nowrap>
                        <%
                       String wgcl=detail.get("wgcl");
                     if(isEdit){

                      %>
                        <pc:select name='<%="wgcl_"+i%>' style="width:50" value='<%=wgcl%>'>
                        <pc:option value='2'>流转</pc:option>
                        <pc:option value='1'>入库</pc:option>
                        </pc:select>
                        <%}else{%>
                       <%=wgcl.equals("1")?"入库":"流转"%>
                        <input type="hidden"  id="wgcl_<%=i%>" name="wgcl_<%=i%>" value='<%=detail.get("wgcl")%>' >
                        <%}%>
                        </td>



                        <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>"value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>' <%=readonly%>></td>

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
                       <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
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
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input  type="text" class="ednone_r" style="width:100%" value='<%=zbl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
                     <tr>
                    <td  noWrap class="tdTitle">加工要求说明</td><%--其他信息--%>
                    <td colspan="7" noWrap class="td"><textarea name="jgyq" rows="3" onKeyDown="return getNextElement();" style="width:660"<%=masterReadonly%>><%=masterRow.get("jgyq")%></textarea></td>
                    </tr>

                      </td>
                    </tr>



                         </td>
                        </tr>
                      </table>


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
                        <%if(isEdit&&detailRows.length==0){%>
                        <input type="hidden" name="singleImportTask" value="引生产计划" onchange="sumitForm(<%=B_ProcessCardBean.MULTISELECT_SCBOM%>)">
                        <input name="btnback" class="button" type="button" value="引生产计划(W)" style="width:115" onClick="importTask();">
                        <pc:shortcut key="w" script="importTask();"/>
                        <%}%>
                        <%if(isEdit){%>
                         <input name="btnback" type="button" class="button" onClick="sumitForm(<%=B_ProcessCardBean.POST%>);" value="保存(S)">

                         <%}%>
                        <%if(isCanDelete){%>
                        <input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=B_ProcessCardBean.DEL%>);" value=" 删除(D)">
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
  <%=B_ProcessCardBean.adjustInputSize(new String[]{"cpbm", "product", "sxz", "rcvnmbr","jldw","ywcsl","bl","jhrq","sjrq", "bz"}, "form1", detailRows.length)%>

  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}


  function importTask()
  {
      ProcessSelTask('form1','srcVar=singleImportTask','fieldVar=scjhid','',"sumitForm(<%=B_ProcessCardBean.SINGLE_SELECT_TASK%>,-1)");
  }
  function ProcessSelTask(frmName,srcVar,fieldVar,curID,methodName,notin)
  {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
      var winName= "SingleladingSelector";
      paraStr = "../jit/select_scjh.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
}
function piece_rate(srcVar)
{
     var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
     var winName= "OrdersSelector";
     paraStr = "../jit/piece_rate_edit.jsp?operate=55555&"+srcVar;
     openSelectUrl(paraStr, winName, winopt2);
}
function sc_jgd(srcVar)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
  var winName= "scjgd";
  paraStr = "../jit/produce_process_edit.jsp?operate=55555&"+srcVar;
  openSelectUrl(paraStr, winName, winopt2);
}
function select_product(srcVar)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
  var winName= "scjgd";
  paraStr = "../store_shengyu/receive_material_edit.jsp?operate=55555&"+srcVar;
  openSelectUrl(paraStr, winName, winopt2);
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
  <%if(B_ProcessCardBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</body>
</html>
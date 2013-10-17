<%--工人工作量（按工人输入）编辑页面从表--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.produce.B_WorkloadWorker workloadWorkerBean = engine.erp.produce.B_WorkloadWorker.getInstance(request);
  String pageCode = "workload_worker";
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
     location.href='workload_worker.jsp';
   }
   function productCodeSelect(obj, i)
   {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=hsbl_'+i,
                    'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=hsbl', obj.value,'product_change('+i+')');
   }
   function productNameSelect(obj,i)
   {
     ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=hsbl_'+i,
                    'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=hsbl', obj.value,'product_change('+i+')');
   }
   function propertyNameSelect(obj,cpid,i)
   {
     PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                        'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
   }
   /**
   function productCodeSelect(obj, i)
   {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                        'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value,'sumitForm(<%=workloadWorkerBean.PRODUCT_ONCHANGE%>,'+i+')');
   }
   function productNameSelect(obj,i)
   {
     ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value,'sumitForm(<%=workloadWorkerBean.PRODUCT_ONCHANGE%>,'+i+')');
   }
   function propertyNameSelect(obj,cpid,i)
  {
    PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                       'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
  }
  */
   function deptchange(){
      associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
  }
  function technicschange(i){
     associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'gx_'+i, 'gylxid', eval('form1.gylxid_'+i+'.value'), '');
  }
</script>
<%String retu = workloadWorkerBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_STORE_UNIT_STYLE = workloadWorkerBean.SC_STORE_UNIT_STYLE;//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  String SYS_PRODUCT_SPEC_PROP =workloadWorkerBean.SYS_PRODUCT_SPEC_PROP;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  String SC_PRODUCE_UNIT_STYLE = workloadWorkerBean.SC_PRODUCE_UNIT_STYLE;//是否强制换算
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工艺路线类型
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE);//工序
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//通过加工单明细id得到加工单号
  engine.erp.produce.B_WageFormula formulaBean = engine.erp.produce.B_WageFormula.getInstance(request);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = workloadWorkerBean.getMaterTable();
  EngineDataSet list = workloadWorkerBean.getDetailTable();
  HtmlTableProducer masterProducer = workloadWorkerBean.masterProducer;
  HtmlTableProducer detailProducer = workloadWorkerBean.detailProducer;
  RowMap masterRow = workloadWorkerBean.getMasterRowinfo();
  RowMap[] detailRows= workloadWorkerBean.getDetailRowinfos();
  String zt = masterRow.get("zt");
  boolean isEdit = !workloadWorkerBean.isReport && zt.equals("0") && (workloadWorkerBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));//在修改状态,并有修改权限
  boolean isCanDelete = zt.equals("0") && !workloadWorkerBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = !isEdit ? "class=edline" : "class=edbox";
  String detailClass = !isEdit ? "class=ednone" : "class=edFocused";
  String detailClass_r = !isEdit ? "class=ednone_r" : "class=edFocused_r";
  String readonly = !isEdit ? " readonly" : "";
  boolean isAdd = workloadWorkerBean.isDetailAdd;
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
            <td class="activeVTab">工人工作量（按工人输入）</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                 <%workShopBean.regData(ds,"deptid");
                   if(isEdit)
                     personBean.regConditionData(ds,"deptid");
                 %>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%--String sumitDept = "if(form1.deptid.value!='"+masterRow.get("deptid")+"'){sumitForm("+workloadWorkerBean.DEPTCHANGE+");}";--%>
                    <%if(!isEdit) out.print("<input type='text' value='"+workShopBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="deptid" onSelect="deptchange();" addNull="1" style="width:110">
                      <%=workShopBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(!isEdit) out.print("<input type='text' value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="personid" addNull="1" style="width:110">
                      <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("rq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="rq" value='<%=masterRow.get("rq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(isEdit){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.rq);"></a>
                    <%}%>
                  </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("cq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="cq" value='<%=masterRow.get("cq")%>' maxlength='<%=ds.getColumn("cq").getPrecision()%>' style="width:80" <%=edClass%> onKeyDown="return getNextElement();" onchange='rb.value=calcDate(this.value); yb.value=calcNight(this.value);' <%=readonly%>>小时</td>
                  </tr>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("rb").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="rb" value='<%=masterRow.get("rb")%>' maxlength='<%=ds.getColumn("rb").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("yb").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="yb" value='<%=masterRow.get("yb")%>' maxlength='<%=ds.getColumn("yb").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("qj").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="qj" value='<%=masterRow.get("qj")%>' maxlength='<%=ds.getColumn("qj").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("je").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="je" value='<%=masterRow.get("je")%>' maxlength='<%=ds.getColumn("je").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
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
                      out.print(masterProducer.getFieldInput(mBakFields[j], masterRow.get(filedcode), filedcode, style, isEdit, true));
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
                           <%if(isEdit){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%>
                        </td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("jgdmxid").getFieldname()%></td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td nowrap><%=detailProducer.getFieldInfo("gylxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("gx").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("scsl").getFieldname()%></td>
                        <td height='20' nowrap>生产单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
                        <td height='20' nowrap>换算单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("desl").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("de").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jjgs").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jjgz").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      processBean.regData(list,"jgdmxid");
                      technicsRouteBean.regData(list, "gylxid");
                      if(isEdit){
                        technicsRouteBean.regConditionData(list,"cpid");
                        technicsNameBean.regConditionData(list,"gylxid");
                      }
                      BigDecimal t_sl = new BigDecimal(0),t_desl = new BigDecimal(0), t_jjgs = new BigDecimal(0), t_jjgz = new BigDecimal(0), t_hssl = new BigDecimal(0);
                      BigDecimal t_scsl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(workloadWorkerBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String scsl = detail.get("scsl");
                        if(workloadWorkerBean.isDouble(scsl))
                          t_scsl = t_scsl.add(new BigDecimal(scsl));
                        String hssl = detail.get("hssl");
                        if(workloadWorkerBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String jjgs = detail.get("jjgs");
                        if(workloadWorkerBean.isDouble(jjgs))
                          t_jjgs = t_jjgs.add(new BigDecimal(jjgs));
                        String jjgz = detail.get("jjgz");
                        if(workloadWorkerBean.isDouble(jjgz))
                          t_jjgz = t_jjgz.add(new BigDecimal(jjgz));
                        String dmsxid = detail.get("dmsxid");
                        String sx = propertyBean.getLookupName(dmsxid);
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                        String gylxidName = "gylxid_"+i;
                        String gxName = "gx_"+i;
                        String gx = detail.get("gylxid").length()>0 ? detail.get("gx") : "";
                        //String sumit = "if(form1.gylxid_"+i+".value != '"+detail.get("gylxid")+"')sumitForm("+workloadWorkerBean.ONCHANGE+","+i+")";
                        String onchange = "if(form1.gx_"+i+".value != '"+detail.get("gx")+"')sumitForm("+workloadWorkerBean.GXMC_ONCHANGE+","+i+")";
                        String jgdmxid = detail.get("jgdmxid");
                        boolean isimport = !jgdmxid.equals("");
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                    <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                         <%if(isEdit && !isimport){%>
                        <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                          onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=product_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=jldw_<%=i%>&srcVar=scydw_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=isprops_<%=i%>','fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fielfVar=jldw&fieldVar=scydw&filedVar=scdwgs&fieldVar=isprops','','product_change(<%=i%>)')">
                         <%}if(isEdit){%>
                         <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <td class="td" nowrap><%=processBean.getLookupName(detail.get("jgdmxid"))%></td>
                        <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                          String hsbl = prodRow.get("hsbl");
                          detail.put("hsbl",hsbl);%>
                        <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="hidden" name="scdwgs_<%=i%>" value="<%=prodRow.get("scdwgs")%>">
                        <input type="hidden" name="widths_<%=i%>" value="<%=widths%>">
                        <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                        <input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>>
                        <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'></td>
                        <td class="td" nowrap><input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                        <td class="td" nowrap>
                        <%if(!isEdit)out.print("<input type='text' style='width:80' value='"+technicsRouteBean.getLookupName(detail.get("gylxid"))+"' class='ednone' readonly>");
                        else {%>
                          <%String ls = "technicschange("+i+");";%>
                        <pc:select name="<%=gylxidName%>" style='width:80' addNull="1" onSelect="<%=ls%>"  >
                        <%=technicsRouteBean.getList(detail.get("gylxid"),"cpid",detail.get("cpid"))%> </pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap>
                        <%if(!isEdit){%><input type="text" <%=detailClass%> style="width:100" onKeyDown="return getNextElement();" id="gx_<%=i%>" name="gx_<%=i%>" value='<%=detail.get("gx")%>' maxlength='<%=list.getColumn("gx").getPrecision()%>' readonly>
                        <%}else {%>
                        <pc:select combox='1' name="<%=gxName%>" input="0" value="<%=gx%>" style="width:100" onSelect="<%=onchange%>">
                        <%=technicsNameBean.getList(detail.get("gx"), "gylxid", detail.get("gylxid"))%> </pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap>
                        <input <%=isimport ? "class=ednone" : detailClass%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=isimport ? "readonly" : readonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(isEdit && !isimport){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=prodRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="scsl_<%=i%>" name="scsl_<%=i%>" value='<%=detail.get("scsl")%>' maxlength='<%=list.getColumn("scsl").getPrecision()%>'  onchange="producesl_onchange(<%=i%>)" <%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" class=ednone_r onKeyDown="return getNextElement();" id="desl_<%=i%>" name="desl_<%=i%>" value='<%=detail.get("desl")%>' maxlength='<%=list.getColumn("desl").getPrecision()%>' readonly></td>
                        <td class="td" nowrap><input type="text" class=ednone_r  onKeyDown="return getNextElement();" id="de_<%=i%>" name="de_<%=i%>" value='<%=detail.get("de")%>' maxlength='<%=list.getColumn("de").getPrecision()%>' readonly></td>
                        <td class="td" nowrap><input type="text" class=ednone_r  onKeyDown="return getNextElement();" id="jjgs_<%=i%>" name="jjgs_<%=i%>" value='<%=detail.get("jjgs")%>' maxlength='<%=list.getColumn("jjgs").getPrecision()%>'  readonly></td>
                        <td class="td" nowrap><input type="text" class=ednone_r  onKeyDown="return getNextElement();" id="jjgz_<%=i%>" name="jjgz_<%=i%>" value='<%=detail.get("jjgz")%>' maxlength='<%=list.getColumn("jjgz").getPrecision()%>' readonly></td>
                        <%FieldInfo[] bakFields = detailProducer.getBakFieldCodes();
                        for(int k=0; k<bakFields.length; k++)
                        {
                          String fieldCode = bakFields[k].getFieldcode();
                          out.print("<td class='td' nowrap>");
                          out.print(detailProducer.getFieldInput(bakFields[k], detail.get(fieldCode), fieldCode+"_"+k, "style='width:65'", isEdit, true));
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
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                       <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_jjgs" name="t_jjgs" type="text" class="ednone_r" style="width:100%" value='<%=t_jjgs%>' readonly></td>
                        <td align="right" class="td"><input id="t_jjgz" name="t_jjgz" type="text" class="ednone_r" style="width:100%" value='<%=t_jjgz%>' readonly></td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
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
         <tr>
            <td colspan="3" noWrap class="tableTitle">
             <%if(isEdit){%><input type="hidden" name="singleImportProcess" value="" >
              <input name="btnback" class="button" type="button" value="引入加工单(W)" style="width:115" onClick="importProcess();">
              <pc:shortcut key="w" script="importProcess();"/>
             <input type="hidden" name="mutiprocess" value="" onchange="sumitForm(<%=workloadWorkerBean.DETAIL_SELECT_PROCESS%>)">
             <input name="btnback" class="button" type="button" value="引加工单明细(E)" style="width:115" onClick="processDetail();" border="0">
                <pc:shortcut key="e" script="processDetail();"/>
             <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
              <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
              <%if(isCanDelete && !workloadWorkerBean.isReport){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
             <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <%if(!workloadWorkerBean.isReport){%><input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
 <pc:shortcut key="c" script='backList();'/><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=workloadWorkerBean.adjustInputSize(new String[]{"cpbm", "product", "sxz","scsl","sl","hssl", "desl", "de","jjgs","jjgz"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
  function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
  }
  function importProcess(){
    WorkloadSelectProcess('form1','srcVar=singleImportProcess','fieldVar=jgdid',form1.deptid.value,'sumitForm(<%=workloadWorkerBean.SINGLE_SEL_PROCESS%>)');
  }
  function product_change(i){
    document.all['dmsxid_'+i].value="";
    document.all['sxz_'+i].value="";
    document.all['widths_'+i].value="";
    associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '',true);
  }
  function processDetail()
  {
    if(form1.deptid.value=='')
    {
      alert('请选择车间');
      return;
    }
    ProcessGoodsSelect('form1','srcVar=mutiprocess&deptid='+form1.deptid.value);
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
   var slObj = document.all['sl_'+i];
   var hsslObj = document.all['hssl_'+i];
   var scslObj = document.all['scsl_'+i];
   var hsblObj = document.all['hsbl_'+i];
   if(slObj.value=='' && scslObj.value=='')
     return;
   if(slObj.value!='')
     scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthValue));
   else if(slObj.value=='' && scslObj.value!=''){
     slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthValue)/parseFloat(scdwgsObj.value));
     if(hsblObj=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
        hsslObj.value = slobj.value;
     else
        hsslObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
   }
   sl_onchange(i,false);
 }
 function big_change(){
  if(<%=detailRows.length%><1)
    return;
  for(t=0; t<<%=detailRows.length%>; t++){
    sl_onchange(t,false);
  }
}
 function sl_onchange(i, isBigUnit)
     {
       var oldhsblObj = document.all['hsbl_'+i];
       var sxzObj = document.all['sxz_'+i];
       unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'nsl_onchange('+i+','+isBigUnit+')');
     }
    function nsl_onchange(i, isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      var hsslObj = document.all['hssl_'+i];
      var scslObj = document.all['scsl_'+i];
      var sxzObj = document.all['sxz_'+i];
      var scdwgsObj = document.all['scdwgs_'+i];
      var widthObj = document.all['widths_'+i];//规格属性的宽度
      var hsblObj = document.all['truebl_'+i];
      var deObj = document.all['de_'+i];
      var deslObj = document.all['desl_'+i];
      var jjgsObj = document.all['jjgs_'+i];
      var jjgzObj = document.all['jjgz_'+i];
      var jjgsgsz = <%=formulaBean.getWorkTime()%>;//(工资设置中计件工时的算法如8)

      var obj = isBigUnit ? hsslObj : slObj;
      var showText = isBigUnit ? "输入的换算数量非法" : "输入的数量非法";
      var changeObj = isBigUnit ? slObj : hsslObj;
      if(obj.value=="")
        return;
      if(isNaN(obj.value)){
        alert(showText);
        obj.focus();
        return;
    }
    if(isBigUnit){
      if(changeObj.value=="" || '<%=SC_STORE_UNIT_STYLE%>'=='1'){//是否强制转换
        if(hsblObj.value!="" && !isNaN(hsblObj.value))
          changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
        if(slObj.value!="" && !isNaN(slObj.value) && deslObj.value!="" && deslObj.value!=0)
          jjgsObj.value= formatSum(parseFloat(slObj.value) * jjgsgsz/ parseFloat(deslObj.value));
        if(slObj.value !="" && !isNaN(slObj.value) && deObj.value!="")
          jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(deObj.value));
      }
    }

    else{
      if(slObj.value!="" && !isNaN(slObj.value) && deslObj.value!="" && deslObj.value!=0)
        jjgsObj.value= formatSum(parseFloat(slObj.value) * jjgsgsz/ parseFloat(deslObj.value));
      if(slObj.value !="" && !isNaN(slObj.value) && deObj.value!="")
        jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(deObj.value));
      if(changeObj.value=="" || '<%=SC_STORE_UNIT_STYLE%>'=='1'){//是否强制转换
        if(hsblObj.value!="" && !isNaN(hsblObj.value))
          changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
      }
    }
    cal_tot('sl');
    cal_tot('jjgs');
    cal_tot('jjgz');
    cal_tot('hssl')
    if(scslObj.value!="" && '<%=SC_PRODUCE_UNIT_STYLE%>'!='1')
      return;
    else{
      if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
        scslObj.value= isBigUnit ? changeObj.value : slObj.value;
      else if(isBigUnit)
        scslObj.value = formatQty(hsblObj.value=="" ? parseFloat(hsslObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value) : parseFloat(hsslObj.value)*parseFloat(hsblObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
      else if(!isBigUnit)
        scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
    }
    cal_tot('scsl');
    }
    function producesl_onchange(i)
    {
      var oldhsblObj = document.all['hsbl_'+i];
      var sxzObj = document.all['sxz_'+i];
      unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'newproducesl_onchange('+i+')');
    }
    function newproducesl_onchange(i)
    {
      var slObj = document.all['sl_'+i];
      var hsslObj = document.all['hssl_'+i];
      var scslObj = document.all['scsl_'+i];
      var deObj = document.all['de_'+i];
      var deslObj = document.all['desl_'+i];
      var deslObj = document.all['desl_'+i];
      var jjgzObj = document.all['jjgz_'+i];
      var hsblObj = document.all['truebl_'+i];
      var scdwgsObj = document.all['scdwgs_'+i];//生产公式
      var widthObj = document.all['widths_'+i];//规格属性的宽度
      var jjgsgsz = <%=formulaBean.getWorkTime()%>;//(工资设置中计件工时的算法如8)
      if(slObj.value!="" && '<%=SC_PRODUCE_UNIT_STYLE%>'!='1')//生产数量与数量是否强制转换
        return;
      if(scslObj.value=="")
        return;
      if(isNaN(scslObj.value))
      {
        alert('输入的生产数量非法');
        obj.focus();
        return;
      }
      if(scslObj.value<=0)
      {
        alert('输入的生产数量小于零');
        obj.focus();
        return;
      }
      if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
        slObj.value= scslObj.value;
      else
        slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthObj.value)/parseFloat(scdwgsObj.value));
      if(slObj.value!="" && !isNaN(slObj.value) && deslObj.value!="" && deslObj.value!=0)
        jjgsObj.value= formatSum(parseFloat(slObj.value) * jjgsgsz/ parseFloat(deslObj.value));
      if(slObj.value !="" && !isNaN(slObj.value) && deObj.value!="")
        jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(deObj.value));
      cal_tot('scsl');
      cal_tot('sl');
      cal_tot('jjgs');
      cal_tot('jjgz');
      if(hsslObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1')
        return;
      if(hsblObj=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
        hsslObj.value = slobj.value;
      else
        hsslObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
      cal_tot('hssl');
    }
    function cal_tot(type)
    {
      var tmpObj;
      var tot=0;
      var je = form1.je;
      var gz = <%=formulaBean.getWage()%>;//(工人工资设置中设置，如果按计件工时计算为0，计件工资算为1)
     for(i=0; i<<%=detailRows.length%>; i++)
       {
       if(type == 'sl')
         tmpObj = document.all['sl_'+i];
       else if(type == 'hssl')
         tmpObj = document.all['hssl_'+i];
       else if(type == 'scsl')
         tmpObj = document.all['scsl_'+i];
       else if(type == 'jjgs')
         tmpObj = document.all['jjgs_'+i];
       else if(type == 'jjgz')
         tmpObj = document.all['jjgz_'+i];
       else
         return;
       if(tmpObj.value!="" && !isNaN(tmpObj.value))
         tot += parseFloat(tmpObj.value);
     }
     if(type == 'sl')
       document.all['t_sl'].value = formatQty(tot);
     else if(type == 'hssl')
       document.all['t_hssl'].value = formatQty(tot);
     else if(type == 'scsl')
       document.all['t_scsl'].value = formatQty(tot);
     else if(type == 'jjgs'){
       document.all['t_jjgs'].value = formatQty(tot);
       if(gz == '1')//以计件工资计算总金额，叠加计件工资
          je.value = document.all['t_jjgs'].value = formatQty(tot);
     }
     else if(type == 'jjgz'){
       document.all['t_jjgz'].value = formatSum(tot);
       if(gz == '1')//以计件工资计算总金额，叠加计件工资
         je.value = document.all['t_jjgz'].value = formatSum(tot);
     }
    }
    <%out.println(formulaBean.getScriptFunction("calcDate", true));
      out.println(formulaBean.getScriptFunction("calcNight", false));
    %>
    function ProcessGoodsSelect(frmName, srcVar, methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
      var winName= "ProcessGoodsSelector";
      paraStr = "../produce/select_process_goods.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
    }
    function WorkloadSelectProcess(frmName,srcVar,fieldVar,curID,methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
      var winName= "WorkloadSelectProcess";
      paraStr = "../produce/workload_singlesel_process.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+curID;
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
  </script>
<%out.print(retu);%>
</body>
</html>

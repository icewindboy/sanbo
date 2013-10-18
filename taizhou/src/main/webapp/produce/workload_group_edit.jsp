<%--工人工作量（按工作组输入）编辑页面从表--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.produce.B_WorkloadGroup workloadGroupBean = engine.erp.produce.B_WorkloadGroup.getInstance(request);
  String pageCode = "workload_group";
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
     location.href='workload_group.jsp';
   }
   function productCodeSelect(obj, i)
 {
   ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=scydw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scdwgs_'+i,
                  'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=hsbl&fieldVar=scdwgs', obj.value,'product_change('+i+')');
 }
 function productNameSelect(obj,i)
 {
   ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=scydw_'+i+'&srcVar=hsbl_'+i+'&srcVar=scdwgs_'+i,
                  'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=hsbl&fieldVar=scdwgs', obj.value,'product_change('+i+')');
 }
 function propertyNameSelect(obj,cpid,i)
 {
   PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                  'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_WORK_GROUP%>', 'gzzid', 'deptid', eval('form1.deptid.value'), '');
}
function dept_personchange(){
  associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jdr', 'deptid', eval('form1.deptid.value'), '');
}
function detailtechnicschange(i){//从表选择工艺路线事件
  associateSelect(document.all['prod_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'gx_'+i, 'gylxid', eval('form1.gylxid_'+i+'.value'), '', true);
  }
function product_change(i){
  document.all['dmsxid_'+i].value="";
  document.all['sxz_'+i].value="";
  document.all['widths_'+i].value="";
  associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '', true);
}

</script>
<%String retu = workloadGroupBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_STORE_UNIT_STYLE = workloadGroupBean.SC_STORE_UNIT_STYLE;//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  String SC_PRODUCE_UNIT_STYLE = workloadGroupBean.SC_PRODUCE_UNIT_STYLE;//计量单位和生产单位换算方式1=强制换算,0=仅空值时换算
  String SYS_PRODUCT_SPEC_PROP = workloadGroupBean.SYS_PRODUCT_SPEC_PROP;
  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//通过工作组id得到工作组名称

  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工序
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE);//工序
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp processMasterBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS);//通过加工单id得到加工单号
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//通过加工单明细id得到加工单号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性LookUp
  engine.erp.produce.B_WageFormula formulaBean = engine.erp.produce.B_WageFormula.getInstance(request);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = workloadGroupBean.getMaterTable();
  EngineDataSet list = workloadGroupBean.getDetailTable();//工作组输入工作量明细数据集
  EngineDataSet dsPerson = workloadGroupBean.getPersonDetailTable();//工作组输入工作量人员数据集
  HtmlTableProducer masterProducer = workloadGroupBean.masterProducer;
  HtmlTableProducer detailProducer = workloadGroupBean.detailProducer;//工作组输入工作量明细
  HtmlTableProducer persondetailProducer = workloadGroupBean.persondetailProducer;//工作组输入工作量员工明细
  RowMap masterRow = workloadGroupBean.getMasterRowinfo();
  RowMap[] detailRows= workloadGroupBean.getDetailRowinfos();//工作组输入工作量明细Array,用于页面打印和保存信息
  RowMap[] personDetailRows= workloadGroupBean.getPersonDetailRowinfos();//工作组输入工作量人员Array,用于页面打印和保存信息
  String zt = masterRow.get("zt");
  boolean isEdit = !workloadGroupBean.isReport && zt.equals("0") && (workloadGroupBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));//在修改状态,并有修改权限
  boolean isCanDelete = zt.equals("0") && !workloadGroupBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = !isEdit ? "class=edline" : "class=edbox";
  String detailClass = !isEdit ? "class=ednone" : "class=edFocused";
  String detailClass_r = !isEdit ? "class=ednone_r" : "class=edFocused_r";
  String readonly = !isEdit ? " readonly" : "";
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
            <td class="activeVTab">工人工作量（按工作组输入）</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                 <%workShopBean.regData(ds,"deptid");
                   processMasterBean.regData(ds, "jgdid");
                   String jsr = masterRow.get("jsr");
                   String mastergx = masterRow.get("gx");
                   String sumit = "if(form1.gzzid.value !='')sumitForm("+workloadGroupBean.ONCHANGE+")";
                   if(isEdit){
                     //technicsNameBean.regConditionData(ds,"gylxid");
                     workGroupBean.regConditionData(ds,"deptid");
                     personBean.regConditionData(ds,"deptid");
                   }
                  %>
                  <tr>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("djh").getFieldname()%></td>
                   <td noWrap class="td"><input type="text" name="djh" value='<%=masterRow.get("djh")%>' maxlength='<%=ds.getColumn("djh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("rq").getFieldname()%></td>
                   <td noWrap class="td"><input type="text" name="rq" value='<%=masterRow.get("rq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                   <%if(isEdit){%>
                     <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.rq);"></a>
                   <%}%>
                   </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                   <td noWrap class="td">
                   <%String sumitDept = "if(form1.deptid.value!='"+masterRow.get("deptid")+"'){sumitForm("+workloadGroupBean.DEPTCHANGE+");}";%>
                   <%if(!isEdit) out.print("<input type='text' value='"+workShopBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                   else {%>
                   <pc:select name="deptid" addNull="1" style="width:110" onSelect="<%=sumitDept%>">
                   <%=workShopBean.getList(masterRow.get("deptid"))%> </pc:select>
                   <%}%>
                   </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jgdid").getFieldname()%></td>
                   <td nowrap class="td">
                   <input type="text" name="jgdh" value='<%=processMasterBean.getLookupName(masterRow.get("jgdid"))%>' style="width:110" class="edline" readonly>
                   <%if(isEdit){%>
                   <input type="hidden" name="jgdid" value="<%=masterRow.get("jgdid")%>">
                   <img style='cursor:hand' name="image" class="img" type="image" title="引加工单" onClick="importProcess();" src="../images/view.gif" border="0">
                   <img style='cursor:hand' src='../images/delete.gif' BORDER=0 title="删除加工单" ONCLICK="sumitForm(<%=workloadGroupBean.DELETE_PROCESS%>);">
                   <%}%>
                   </td>
                   </tr>
                   <tr>
                   <%--td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("gylxid").getFieldname()%></td>
                    <td class="td" nowrap>
                    <%if(!isEdit)out.print("<input type='text' value='"+technicsRouteBean.getLookupName(masterRow.get("gylxid"))+"' style='width:100%' class='edline' readonly>");
                    else {%>
                    <pc:select name="gylxid" addNull="1" style="width:110" onSelect="">
                    <%=technicsRouteBean.getList()%> </pc:select>
                    <%}%>
                    </td>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("gx").getFieldname()%></td>
                    <td class="td" align="center" nowrap>
                    <%if(!isEdit){%><input type="text" class=ednone style="width:110" onKeyDown="return getNextElement();" id="gx" name="gx" value='<%=masterRow.get("gx")%>' maxlength='<%=ds.getColumn("gx").getPrecision()%>' readonly>
                    <%}else {%>
                    <pc:select combox='1' name="gx" input="0" style="width:110" value="<%=mastergx%>">
                    <%=technicsNameBean.getList(masterRow.get("gx"), "gylxid", masterRow.get("gylxid"))%> </pc:select>
                    <%}%>
                    </td--%>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("gzzid").getFieldname()%></td>
                    <td noWrap class="td">
                    <%String sumitGroup = "sumitForm("+workloadGroupBean.GROUP_DETAIL_ADD+");";%>
                   <%if(!isEdit) out.print("<input type='text' value='"+workGroupBean.getLookupName(masterRow.get("gzzid"))+"' style='width:110' class='edline' readonly>");
                   else {%>
                   <pc:select name="gzzid" addNull="1" style="width:110" onSelect="<%=sumitGroup%>">
                   <%=workGroupBean.getList(masterRow.get("gzzid"),"deptid",masterRow.get("deptid"))%> </pc:select>
                   <%}%>
                   </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bc").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="bc" value='<%=masterRow.get("bc")%>' <%=edClass%> maxlength='<%=ds.getColumn("bc").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sl").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="sl" value='<%=masterRow.get("sl")%>' maxlength='<%=ds.getColumn("sl").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();"  readonly></td>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("zgf").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="zgf" value='<%=masterRow.get("zgf")%>' maxlength='<%=ds.getColumn("zgf").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                   </tr>
                    <tr>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("zlyz").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="zlyz" value='<%=masterRow.get("zlyz")%>' <%=edClass%> maxlength='<%=ds.getColumn("zlyz").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("ydzs").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="ydzs" value='<%=masterRow.get("ydzs")%>' <%=edClass%> maxlength='<%=ds.getColumn("ydzs").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dzcs").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="dzcs" value='<%=masterRow.get("dzcs")%>' <%=edClass%> maxlength='<%=ds.getColumn("dzcs").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jf").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="jf" value='<%=masterRow.get("jf")%>' <%=edClass%> maxlength='<%=ds.getColumn("jf").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  </tr>
                   <tr>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jhsh").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="jhsh" value='<%=masterRow.get("jhsh")%>' <%=edClass%> maxlength='<%=ds.getColumn("jhsh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sjsh").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="sjsh" value='<%=masterRow.get("sjsh")%>'  <%=edClass%> maxlength='<%=ds.getColumn("sjsh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("shyy").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="shyy" value='<%=masterRow.get("shyy")%>' <%=edClass%> maxlength='<%=ds.getColumn("shyy").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jt").getFieldname()%></td>
                    <td noWrap class="td"><input type="text" name="jt" value='<%=masterRow.get("jt")%>' <%=edClass%> maxlength='<%=ds.getColumn("jt").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  </tr>
                  <tr>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jdr").getFieldname()%></td>
                   <td  noWrap class="td"><%if(!isEdit){%> <input type="text" name="jdr" value='<%=masterRow.get("jdr")%>' maxlength='<%=ds.getColumn("jdr").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                   <%}else {%>
                   <pc:select combox="1" className="edFocused" name="jsr" value="<%=jsr%>" style="width:110">
                   <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%></pc:select>
                   <%}%>
                   </td>
                    <%--td noWrap class="tdTitle"></td>
                    <td noWrap class="td" colspan="3" style="80"><input name="btnback" class="button" type="button" value="物料" onClick="GroupMaterail()" src='../images/edit.old.gif' border="0">
                     <td noWrap class="tdTitle"></td>
                    <td noWrap class="td" colspan="3" style="80"><input name="btnback" type="button" class="button"   onClick="backList();" value=" 计算分切工资"></td--%>
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
                int width = (detailRows.length > 5 ? detailRows.length : 5)*23 + 66;
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap width=30>
                           <%if(isEdit){%>
                         <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add.gif" border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +")"%>'/><%}%>
                        </td>
                        <td nowrap>加工单号</td>
                        <td nowrap>产品编码</td>
                        <td nowrap>品名规格</td>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("gylxid").getFieldname()%><img title="工艺路线批量修改" onClick="technicsChange()"  src="../images/down_arrow.gif"> </td>
                        <td nowrap><%=detailProducer.getFieldInfo("gx").getFieldname()%><img title="工序批量修改" onClick="technicsNameChange()"  src="../images/down_arrow.gif"></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td nowrap>计量单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("scsl").getFieldname()%></td>
                        <td nowrap>生产单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
                        <td nowrap>换算单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("jjdj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jjgz").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%
                      prodBean.regData(list,"cpid");
                      processBean.regData(list,"jgdmxid");
                      propertyBean.regData(list, "dmsxid");
                      technicsRouteBean.regData(list, "gylxid");
                      if(isEdit){
                        technicsRouteBean.regConditionData(list,"cpid");
                        technicsNameBean.regConditionData(list,"gylxid");
                      }
                      BigDecimal t_jjgz = new BigDecimal(0);
                      BigDecimal t_sl = new BigDecimal(0), t_scsl = new BigDecimal(0), t_hssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String jjgz = detail.get("jjgz");
                        if(workloadGroupBean.isDouble(jjgz))
                          t_jjgz = t_jjgz.add(new BigDecimal(jjgz));
                        String sl = detail.get("sl");
                       if(workloadGroupBean.isDouble(sl))
                         t_sl = t_sl.add(new BigDecimal(sl));
                       String hssl = detail.get("hssl");
                       if(workloadGroupBean.isDouble(hssl))
                         t_hssl = t_hssl.add(new BigDecimal(hssl));
                       String scsl = detail.get("scsl");
                       if(workloadGroupBean.isDouble(scsl))
                         t_scsl = t_scsl.add(new BigDecimal(scsl));
                       String dmsxid = detail.get("dmsxid");
                       String sx = propertyBean.getLookupName(dmsxid);
                       String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                       String gxName = "gx_"+i;
                       String gylxidName = "gylxid_"+i;
                       String gx = detail.get("gylxid").length()>0 ? detail.get("gx") : "";
                       String onchange = "if(form1.gx_"+i+".value != '"+detail.get("gx")+"')sumitForm("+workloadGroupBean.GXMC_ONCHANGE+","+i+")";
                       String jgdmxid=detail.get("jgdmxid");
                       boolean isimport = !jgdmxid.equals("");//引入加工单，从表产品编码当前行不能修改
                       RowMap prodRow = prodBean.getLookupRow(detail.get("cpid"));
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(isEdit || !isimport){%>
                         <img style='cursor:hand' title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=scydw_<%=i%>&srcVar=isprops_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=scdwgs_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=scydw&fieldVar=isprops&fieldVar=hsbl&fieldVar=scdwgs','','product_change(<%=i%>)')">
                          <%}if(isEdit){%>

                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <td class="td" nowrap><%=processBean.getLookupName(detail.get("jgdmxid"))%></td>
                        <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="hidden" name="scdwgs_<%=i%>" value="<%=prodRow.get("scdwgs")%>">
                        <input type="hidden" name="widths_<%=i%>" value="<%=widths%>">
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                        <input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>>
                        <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'></td>
                        <td class="td" nowrap><input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                       <td class="td" nowrap>
                        <input <%=isimport ? "class=ednone" : detailClass%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=isimport ? "readonly" : readonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(isEdit && !isimport){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=prodRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap>
                        <%if(!isEdit)out.print("<input type='text' style='width:90' value='"+technicsRouteBean.getLookupName(detail.get("gylxid"))+"' class='ednone' readonly>");
                        else {%>
                          <%String temp ="detailtechnicschange("+i+");"; %>
                        <iframe id="prod_<%=i%>" src="" width="0" height=0 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                        <pc:select name="<%=gylxidName%>" style='width:90' addNull="1" onSelect="<%=temp%>"  >
                        <%=technicsRouteBean.getList(detail.get("gylxid"),"cpid",detail.get("cpid"))%> </pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap>
                        <input type="hidden" name="jjff_<%=i%>" value="<%=detail.get("jjff")%>">
                        <%if(!isEdit){%><input type="text" <%=detailClass%> style="width:100" onKeyDown="return getNextElement();" id="gx_<%=i%>" name="gx_<%=i%>" value='<%=detail.get("gx")%>' maxlength='<%=list.getColumn("gx").getPrecision()%>' readonly>
                        <%}else {%>
                          <%String test="getJjdjValue("+i+");"; %>
                        <pc:select combox='1' name="<%=gxName%>" value="<%=gx%>" style="width:100" onSelect="<%=test%>" >
                        <%=technicsNameBean.getList(detail.get("gx"), "gylxid", detail.get("gylxid"))%> </pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onblur="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="scsl_<%=i%>" name="scsl_<%=i%>" value='<%=detail.get("scsl")%>' maxlength='<%=list.getColumn("scsl").getPrecision()%>' onblur="producesl_onchange(<%=i%>)" <%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onblur="sl_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" class=ednone_r  onKeyDown="return getNextElement();" id="jjdj_<%=i%>" name="jjdj_<%=i%>" value='<%=detail.get("jjdj")%>' maxlength='<%=list.getColumn("jjdj").getPrecision()%>' readonly></td>
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
                      for(; i < 5; i++){
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
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td"><input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td"><input id="t_jjgz" name="t_jjgz" type="text" class="ednone_r" style="width:100%" value='<%=t_jjgz%>' readonly></td>
                      </tr>
                    </table></div>
                    </td>
                </tr>
               <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
                <tr valign="top">
                 <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
                    <tr>
                      <td class="activeVTab">工作量人员列表</td>
                     </tr>
                    </table>
                     <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
                      <tr>
                       <td>
                        <tr>
                         <td colspan="8" noWrap class="td">
                           <%int width2 = (personDetailRows.length > 5 ? personDetailRows.length : 5)*23 + 66;%>
                           <div style="display:block;width:750;height=<%=width2%>;overflow-y:auto;overflow-x:auto;">
                           <table id="tableview2" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                            <tr class="tableTitle">
                            <td nowrap width=10></td>
                           <td height='20' align="center" nowrap width=30>
                               <%if(isEdit){%>
                             <input name="image" class="img" type="image" title="新增(R)" onClick="sumitForm(<%=workloadGroupBean.DETAIL_PERSON_ADD%>)" src="../images/add.gif" border="0">
                              <pc:shortcut key="r" script='<%="sumitForm("+ workloadGroupBean.DETAIL_PERSON_ADD +")"%>'/><%}%>
                            </td>
                            <td nowrap><%=persondetailProducer.getFieldInfo("personid").getFieldname()%></td>
                            <td nowrap><%=persondetailProducer.getFieldInfo("bl").getFieldname()%></td>
                            <td nowrap><%=persondetailProducer.getFieldInfo("jjgz").getFieldname()%></td>
                            <%persondetailProducer.printTitle(pageContext, "height='20'", true);%>
                          </tr>
                        <%personBean.regData(dsPerson,"personid");
                          BigDecimal t_je = new BigDecimal(0);
                          int m=0;
                          RowMap persondetail = null;
                          dsPerson.first();
                          for(; m<personDetailRows.length; m++)   {
                            persondetail = personDetailRows[m];
                            String jjgz = persondetail.get("jjgz");
                            if(workloadGroupBean.isDouble(jjgz))
                              t_je = t_je.add(new BigDecimal(jjgz));
                            String personidName="personid_"+m;
                        %>
                          <tr id="rowinfo_<%=m%>">
                            <td class="td" nowrap><%=m+1%></td>
                            <td class="td" nowrap align="center">
                              <%if(isEdit){%>
                              <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=workloadGroupBean.DETAIL_PERSON_DELETE%>,<%=m%>)" src="../images/delete.gif" border="0">
                              <%}%>
                            </td>
                            <td class="td" align="center" nowrap>
                            <%if(!isEdit) out.print("<input type='text' style='width:100' value='"+personBean.getLookupName(persondetail.get("personid"))+"' class='ednone' readonly>");
                            else {%>
                            <pc:select className="edFocused" name="<%=personidName%>" style='width:100' addNull="1">
                            <%=personBean.getList(persondetail.get("personid"), "deptid", masterRow.get("deptid"))%></pc:select>
                            <%}%>
                            </td>
                            <td class="td" align="center"  nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" style='width:100' id="bl_<%=m%>" name="bl_<%=m%>" value='<%=persondetail.get("bl")%>' maxlength='<%=dsPerson.getColumn("bl").getPrecision()%>' onblur="bl_onchange(<%=m%>)" <%=readonly%>></td>
                            <td class="td" nowrap><input type="text" class=ednone_r  onKeyDown="return getNextElement();" style='width:100' id="grjjgz_<%=m%>" name="grjjgz_<%=m%>" value='<%=persondetail.get("jjgz")%>' maxlength='<%=dsPerson.getColumn("jjgz").getPrecision()%>' readonly></td>
                            <%FieldInfo[] bakFields = persondetailProducer.getBakFieldCodes();
                            for(int k=0; k<bakFields.length; k++)
                            {
                              String fieldCode = bakFields[k].getFieldcode();
                              out.print("<td class='td' nowrap>");
                              out.print(persondetailProducer.getFieldInput(bakFields[k], persondetail.get(fieldCode), fieldCode+"_"+k, "style='width:65'", isEdit, true));
                              out.println("</td>");
                            }
                            %>
                          </tr>
                          <%dsPerson.next();
                          }
                          for(; m < 5; m++){
                      %>
                          <tr id="rowinfo_<%=m%>">
                            <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                            <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                            <%persondetailProducer.printBlankCells(pageContext, "class=td", true);%>
                          </tr>
                          <%}%>
                           <tr id="rowinfo_end">
                           <td class="td">&nbsp;</td>
                           <td class="tdTitle" nowrap>合计</td>
                           <td class="td">&nbsp;</td>
                           <td class="td">&nbsp;</td>
                           <td align="right" class="td"><input id="t_je" name="t_je" type="text" class="ednone_r" style="width:100%" value='<%=t_je%>' readonly></td>
                        <%persondetailProducer.printBlankCells(pageContext, "class=td", true);%>
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
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("zdrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("zdr")%></td>
          </tr>
          <tr>
         <tr>
            <td colspan="3" noWrap class="tableTitle">
              <%if(isEdit){%><input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
                <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
               <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
              <%if(isCanDelete && !workloadGroupBean.isReport){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
              <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <%if(!workloadGroupBean.isReport){%><input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
	  <pc:shortcut key="c" script='backList();'/><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);initDefaultTableRow('tableview2',1);
<%=workloadGroupBean.adjustInputSize(new String[]{"cpbm", "product", "sxz", "sl", "scsl","hssl"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
  function technicsChange(){
    //连接的对象。编码更改对应地区更改
    var gylxid = form1.gylxid_0.value;
    for(m=1;m<<%=detailRows.length%>; m++)
    {
      var linkObj = FindSelectObject("gylxid_"+m);
      if(linkObj == null)
        return;
      linkObj.SetSelectedKey(gylxid);
      linkObj.OnSelect();
    }
  }
  function technicsNameChange(){
    //连接的对象。编码更改对应地区更改
    var gx = form1.gx_0.value;
    for(y=1;y<<%=detailRows.length%>;y++)
    {
      var linkObj = FindSelectObject("gx_"+y);
      if(linkObj == null)
        return;
      linkObj.SetSelectedKey(gx);
      linkObj.OnSelect();
    }
  }
  function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
  }
  function importProcess(){
    WorkloadSelectProcess('form1','srcVar=jgdid','fieldVar=jgdid',form1.deptid.value,'sumitForm(<%=workloadGroupBean.SINGLE_SEL_PROCESS%>)');
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
   var slObj = document.all['sl_'+i];
   var hsslObj = document.all['hssl_'+i];
   var scslObj = document.all['scsl_'+i];
   var jjdjObj = document.all['jjdj_'+i];
   var jjgzObj = document.all['jjgz_'+i];
   var hsblObj = document.all['hsbl_'+i];
   var sfcObj = document.all['jjff_'+i];//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
   var scdwgsObj = document.all['scdwgs_'+i];//生产公式
   var obj = isBigUnit ? hsslObj : slObj;
   var widthObj = document.all['widths_'+i];//规格属性的宽度
   var showText = isBigUnit ? "输入的换算数量非法" : "输入的数量非法";
   var showText2 = isBigUnit ? "输入的换算数量小于零" : "输入的数量小于零";
   var changeObj = isBigUnit ? slObj : hsslObj;
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
    if(isBigUnit){
      if(hsslObj.value !="" && !isNaN(hsslObj.value) && jjdjObj.value!="" && !isNaN(jjdjObj.value)){
          if(sfcObj.value=="2")
            jjgzObj.value= formatSum(parseFloat(hsslObj.value) * parseFloat(jjdjObj.value));
      }
      if(changeObj.value=="" || '<%=SC_STORE_UNIT_STYLE%>'=='1'){//是否强制转换
        if(hsblObj=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
          changeObj.value = obj.value;
        else
          changeObj.value = formatQty(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
        //计算计件工资
        if(slObj.value !="" && !isNaN(slObj.value) && jjdjObj.value!="" && !isNaN(jjdjObj.value)){
          if(sfcObj.value=="0")
            jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj.value));
          else if(sfcObj.value=="3")
            jjgzObj.value = '';
        }
        else if(jjdjObj.value=="")
          jjgzObj.vlaue=0;
      }
    }
    else{
      if(slObj.value !="" && !isNaN(slObj.value) && jjdjObj.value!="" && !isNaN(jjdjObj.value)){
        if(sfcObj.value=='0')
          jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj.value));
        else if(sfcObj.value=="3" )
          jjgzObj.value = '';
      }
      if(changeObj.value=="" || '<%=SC_STORE_UNIT_STYLE%>'=='1'){//是否强制转换
        if(hsblObj=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
          changeObj.value = obj.value;
        else
          changeObj.value = formatQty(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
      }
      //计算计件工资
      if(changeObj.value !="" && !isNaN(changeObj.value) && jjdjObj.value!="" && !isNaN(jjdjObj.value)){
        if(sfcObj.value=="2")
          jjgzObj.value= formatSum(parseFloat(changeObj.value) * parseFloat(jjdjObj.value));
      }
      else if(jjdjObj.value=="" || slObj.value =="")
        jjgzObj.vlaue=0;
    }
      cal_tot('sl');
      cal_tot('hssl');
      cal_tot('jjgz');
      big_blchange();
    if(scslObj.value!="" && '<%=SC_PRODUCE_UNIT_STYLE%>'!='1'){
      if(sfcObj.value=='1' && scslObj.value !="" && !isNaN(scslObj.value) && jjdjObj.value!="" && !isNaN(jjdjObj.value))
          jjgzObj.value= formatSum(parseFloat(scslObj.value) * parseFloat(jjdjObj.value));
      cal_tot('jjgz');
      return;
    }
    else{
      if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
        scslObj.value= isBigUnit ? changeObj.value : slObj.value;
      else if(isBigUnit)
        scslObj.value = formatQty(hsblObj.value=="" ? parseFloat(hsslObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value) : parseFloat(hsslObj.value)*parseFloat(hsblObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
      else if(!isBigUnit)
        scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
      if(sfcObj.value=='1' && scslObj.value !="" && !isNaN(scslObj.value) && jjdjObj.value!="" && !isNaN(jjdjObj.value))
          jjgzObj.value= formatSum(parseFloat(scslObj.value) * parseFloat(jjdjObj.value));
    }
    cal_tot('jjgz');
    cal_tot('scsl');
   }
   function producesl_onchange(i)
   {
     var slObj = document.all['sl_'+i];
     var hsslObj = document.all['hssl_'+i];
     var scslObj = document.all['scsl_'+i];
     var jjdjObj = document.all['jjdj_'+i];
     var jjgzObj = document.all['jjgz_'+i];
     var hsblObj = document.all['hsbl_'+i];
     var sfcObj = document.all['jjff_'+i];//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
     var scdwgsObj = document.all['scdwgs_'+i];//生产公式
     var widthObj = document.all['widths_'+i];//规格属性的宽度
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
     if(sfcObj.value=="1" && jjdjObj.value!="" && !isNaN(jjdjObj.value))
        jjgzObj.value= formatSum(parseFloat(scslObj.value) * parseFloat(jjdjObj.value));
     if(slObj.value!="" && '<%=SC_PRODUCE_UNIT_STYLE%>'!='1'){//生产数量与数量是否强制转换
       if(sfcObj.value=="0")
         jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj.value));
      cal_tot('jjgz');
       return;
     }
     if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
       slObj.value= scslObj.value;
     else
       slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthObj.value)/parseFloat(scdwgsObj.value));
     if(slObj.value !="" && !isNaN(slObj.value) && jjdjObj.value!="" && !isNaN(jjdjObj.value)){
       if(sfcObj.value=="0")
         jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj.value));
       else if(sfcObj.value=="3")
         jjgzObj.value = '';
     }
     else if(jjdjObj.value=="")
       jjgzObj.vlaue=0;
     cal_tot('jjgz');
     if(hsslObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1'){
       if(sfcObj.vlaue=="2")
         jjgzObj.value= formatSum(parseFloat(hsslObj.value) * parseFloat(jjdjObj.value));
       cal_tot('jjgz');
       return;
     }
     if(hsblObj=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
       hsslObj.value = slObj.value;
     else
       hsslObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
     if(hsslObj.value !="" && !isNaN(hsslObj.value) && jjdjObj.value!="" && !isNaN(jjdjObj.value)){
       if(sfcObj.vlaue=="2")
         jjgzObj.value= formatSum(parseFloat(hsslObj.value) * parseFloat(jjdjObj.value));
      }
     cal_tot('jjgz');
     cal_tot('scsl');
     cal_tot('sl');
     cal_tot('hssl');
     big_blchange();
   }
   function cal_tot(type)
   {
     var tmpObj;
     var tot=0;
     for(i=0; i<<%=detailRows.length%>; i++)
     {
       if(type == 'sl'){
         tmpObj = document.all['sl_'+i];
       }
       else if(type == 'scsl')
         tmpObj = document.all['scsl_'+i];
       else if(type == 'hssl')
         tmpObj = document.all['hssl_'+i];
       else if(type == 'jjgz')
         tmpObj = document.all['jjgz_'+i];
       else
         return;
       if(tmpObj.value!="" && !isNaN(tmpObj.value))
         tot += parseFloat(tmpObj.value);
     }
     if(type == 'sl'){
       document.all['t_sl'].value = formatQty(tot);
       form1.sl.value = formatQty(tot);
     }
     if(type == 'scsl')
       document.all['t_scsl'].value = formatQty(tot);
     if(type == 'hssl')
       document.all['t_hssl'].value = formatQty(tot);
     if(type == 'jjgz'){
       document.all['t_jjgz'].value = formatQty(tot);
       form1.zgf.value = formatQty(tot);
     }
    }
  function big_blchange()
  {
    for(k=0; k<<%=personDetailRows.length%>; k++)
    {
      bl_onchange(k);
    }
  }
  function bl_onchange(i)
  {
    var blObj = document.all['bl_'+i];
    var zgf = form1.zgf;
    var grjjgzObj = document.all['grjjgz_'+i];
    if(zgf.value=="")
      return;
    if(isNaN(zgf.value))
    {
      alert("总工费非法");
      return;
    }
    if(blObj.value=="")
      return;
    if(isNaN(blObj.value))
    {
      alert("输入的比例非法");
      blObj.focus();
      return;
    }
    if(zgf.value!="" && !isNaN(zgf.value) && blObj.value!="" ){
      grjjgzObj.value= formatSum(parseFloat(zgf.value) * parseFloat(blObj.value));
    }
    grcal_tot('grjjgz');
  }
  //选择工序得到计件单价，并计算计件工资
  function getJjdjValue(i){
    getRowValue(document.all['prod_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE%>', 'form1', 'srcVar=jjdj_'+i+'&srcVar=jjff_'+i, 'fieldVar=deje&fieldVar=jjff',eval('form1.v_gx_'+i+'.value') , 'sl_onchange('+i+',false)');
  }
  function grcal_tot(type)
  {
    var tmpObj;
    var tot=0;
    for(n=0; n<<%=personDetailRows.length%>; n++)
    {
      if(type == 'grjjgz')
        tmpObj = document.all['grjjgz_'+n];
      else
        return;
      if(tmpObj.value!="" && !isNaN(tmpObj.value))
        tot += parseFloat(tmpObj.value);
    }
    if(type == 'grjjgz')
      document.all['t_je'].value = formatSum(tot);
  }
  function ProcessSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
    var winName= "ProcessSingleSelector";
    paraStr = "../produce/select_single_process.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+curID;;
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
  function GroupMaterail()
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
    var winName= "MrpGoodsSelector";
    paraStr = "../produce/group_materail.jsp";
    openUrlOpt2(paraStr);
  }
  </script>
<%out.print(retu);%>
</body>
</html>

<%--工人工作量（按部门输入）编辑页面从表--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.produce.B_WorkloadDept workloadDeptBean = engine.erp.produce.B_WorkloadDept.getInstance(request);
  String pageCode = "workload_dept";
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
   function backList()
   {
     location.href='workload_dept.jsp';
   }
   function productCodeSelect(obj, i)
   {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                    'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value,'sumitForm(<%=workloadDeptBean.PRODUCT_CHANGE%>,'+i+')');
   }
   function productNameSelect(obj,i)
  {
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value,'sumitForm(<%=workloadDeptBean.PRODUCT_CHANGE%>,'+i+')');
  }
  function propertyNameSelect(obj,cpid,i)
  {
    PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                       'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
  }
  function Add()
  {
    if(form1.deptid.value=='')
    {
      alert('请选择车间');
      return;
    }
    sumitForm(<%=workloadDeptBean.DETAIL_ADD_PERSON%>)
  }
</script>
<%String retu = workloadDeptBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工艺路线类型
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE);//工序
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//通过加工单明细id得到加工单号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.erp.produce.B_WageFormula formulaBean = engine.erp.produce.B_WageFormula.getInstance(request);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = workloadDeptBean.getMaterTable();
  EngineDataSet list = workloadDeptBean.getDetailTable();
  HtmlTableProducer masterProducer = workloadDeptBean.masterProducer;
  HtmlTableProducer detailProducer = workloadDeptBean.detailProducer;
  RowMap masterRow = workloadDeptBean.getMasterRowinfo();
  RowMap[] detailRows= workloadDeptBean.getDetailRowinfos();
  String zt = masterRow.get("zt");
  boolean isEdit = !workloadDeptBean.isReport && zt.equals("0") && (workloadDeptBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));//在修改状态,并有修改权限
  boolean isCanDelete = zt.equals("0") && !workloadDeptBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = !isEdit ? "class=edline" : "class=edbox";
  String detailClass = !isEdit ? "class=ednone" : "class=edFocused";
  String detailClass_r = !isEdit ? "class=ednone_r" : "class=edFocused_r";
  String readonly = !isEdit ? " readonly" : "";
  boolean isAdd = workloadDeptBean.isDetailAdd;
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
            <td class="activeVTab">工人工作量（按部门输入）</td>
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
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("djh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="djh" value='<%=masterRow.get("djh")%>' maxlength='<%=ds.getColumn("djh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%String sumitDept = "if(form1.deptid.value!='"+masterRow.get("deptid")+"'){sumitForm("+Operate.DETAIL_ADD+");}";%>
                    <%if(!isEdit) out.print("<input type='text' value='"+workShopBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110" onSelect="<%=sumitDept%>">
                      <%=workShopBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("rq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="rq" value='<%=masterRow.get("rq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(isEdit){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.rq);"></a>
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
                         <input name="image" class="img" type="image" title="新增(A)" onClick="Add();" src="../images/add.gif" border="0">
                         <pc:shortcut key="a" script='Add();'/> <%}%>
                        </td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("personid").getFieldname()%></td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("jgdmxid").getFieldname()%></td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("gylxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("gx").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("desl").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jjdj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jjgs").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jjgz").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      propertyBean.regData(list, "dmsxid");
                      processBean.regData(list,"jgdmxid");
                      technicsRouteBean.regData(list, "gylxid");
                      if(isEdit){
                        technicsRouteBean.regConditionData(list,"cpid");
                        technicsNameBean.regConditionData(list,"gylxid");
                      }
                      BigDecimal t_sl = new BigDecimal(0),t_desl = new BigDecimal(0), t_jjgs = new BigDecimal(0), t_jjgz = new BigDecimal(0), t_hssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(workloadDeptBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String hssl = detail.get("hssl");
                        if(workloadDeptBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String jjgs = detail.get("jjgs");
                        if(workloadDeptBean.isDouble(jjgs))
                          t_jjgs = t_jjgs.add(new BigDecimal(jjgs));
                        String jjgz = detail.get("jjgz");
                        if(workloadDeptBean.isDouble(jjgz))
                          t_jjgz = t_jjgz.add(new BigDecimal(jjgz));
                        String personidName = "personid_"+i;
                        String gylxidName = "gylxid_"+i;
                        String gxName = "gx_"+i;
                        boolean isempty = true;
                        isempty = isempty && i>0 && detail.get("cpid").equals("") && detail.get("jgdmxid").equals("");//触发产品复制功能的条件（该行cpid为空并且不为第一行,当前行得上一行不为空）
                        if(i>0){
                          RowMap olddetail = detailRows[i-1];
                          String oldCpid = olddetail.get("cpid");
                          isempty = isempty && !oldCpid.equals("");
                        }
                        String gx = detail.get("gylxid").length()>0 ? detail.get("gx") : "";
                        String jgdmxid=detail.get("jgdmxid");
                        boolean isimport = !jgdmxid.equals("");
                       // String gylxid = detail.get("gylxid");
                       // String gxmc = detail.get("gx");
                       // String sumit = "sumitForm("+workloadDeptBean.ONCHANGE+","+i+")";
                       // String onchange = "sumitForm("+workloadDeptBean.GXMC_ONCHANGE+","+i+")";
                    %>
                      <tr id="rowinfo_<%=i%>" >
                        <td class="td" nowrap ><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(isEdit){%>
                          <input type="hidden" name="singleProduct_<%=i%>" value=""><input type="hidden" name="singleId_<%=i%>" value="">
                          <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                          onClick="ProdSingleSelect('form1','srcVar=singleProduct_<%=i%>','fieldVar=cpid',form1.deptid.value,'sumitForm(<%=workloadDeptBean.DETAIL_SELECT_PRODUCT%>,<%=i%>)')">&nbsp<input name="image" class="img" type="image" title="引加工单"
                          onClick="ProcessSingleSelect('form1','srcVar=singleId_<%=i%>','fieldVar=jgdmxid',form1.deptid.value,'sumitForm(<%=workloadDeptBean.DETAIL_SELECT_PROCESS%>,<%=i%>)')" src="../images/view.gif" border="0">&nbsp<input name="image" class="img" type="image" title="删除"
                          onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <td class="td" nowrap>
                        <%if(!isEdit)out.print("<input  type='text' style='width:100' value='"+personBean.getLookupName(detail.get("personid"))+"' class='ednone' readonly>");
                        else {%>
                        <pc:select name="<%=personidName%>" style='width:100' addNull="1" >
                        <%=personBean.getList(detail.get("personid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap onClick="if(<%=isempty%>==true)sumitForm(<%=workloadDeptBean.PRODUCT_AUTO_ADD%>,<%=i%>);"><%=processBean.getLookupName(detail.get("jgdmxid"))%></td>
                        <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                          String hsbl = prodRow.get("hsbl");
                          detail.put("hsbl",hsbl);%>
                        <td class="td" nowrap>
                        <input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>>
                        <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'></td>
                        <td class="td" nowrap><input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                        <td class="td" align="center" nowrap onClick="if(<%=isempty%>==true)sumitForm(<%=workloadDeptBean.PRODUCT_AUTO_ADD%>,<%=i%>)">
                        <input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" nowrap>
                        <% String sumit = "if(form1.gylxid_"+i+".value != '"+detail.get("gylxid")+"')sumitForm("+workloadDeptBean.ONCHANGE+","+i+")";
                           String onchange = "if(form1.gx_"+i+".value!= '"+detail.get("gx")+"')sumitForm("+workloadDeptBean.GXMC_ONCHANGE+","+i+")";
                        %>
                        <%if(!isEdit)out.print("<input type='text' style='width:80' value='"+technicsRouteBean.getLookupName(detail.get("gylxid"))+"' style='width:100%' class='ednone' readonly>");
                        else {%>
                        <pc:select name="<%=gylxidName%>" addNull="1" style='width:80' onSelect="<%=sumit%>">
                        <%=technicsRouteBean.getList(detail.get("gylxid"),"cpid",detail.get("cpid"))%> </pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap>
                        <%if(!isEdit){%><input type="text" <%=detailClass%> style="width:100%" onKeyDown="return getNextElement();" id="gx_<%=i%>" name="gx_<%=i%>" value='<%=detail.get("gx")%>' maxlength='<%=list.getColumn("gx").getPrecision()%>' readonly>
                        <%}else {%>
                        <pc:select combox='1' name="<%=gxName%>" input="0" style="width:90" value="<%=gx%>" onSelect="<%=onchange%>">
                        <%=technicsNameBean.getList(null, "gylxid", detail.get("gylxid"))%> </pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap>
                        <input <%=isimport ? "class=ednone" : detailClass%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=isimport ? "readonly" : readonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(isEdit && !isimport){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=prodRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value)">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap onClick="if(<%=isempty%>==true)sumitForm(<%=workloadDeptBean.PRODUCT_AUTO_ADD%>,<%=i%>)"><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>,false)" <%=readonly%>></td>
                        <td class="td" nowrap onClick="if(<%=isempty%>==true)sumitForm(<%=workloadDeptBean.PRODUCT_AUTO_ADD%>,<%=i%>)"><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>,true)" <%=readonly%>></td>
                        <td class="td" nowrap onClick="if(<%=isempty%>==true)sumitForm(<%=workloadDeptBean.PRODUCT_AUTO_ADD%>,<%=i%>)"><input type="text" class=ednone_r  onKeyDown="return getNextElement();" id="desl_<%=i%>" name="desl_<%=i%>" value='<%=detail.get("desl")%>' maxlength='<%=list.getColumn("desl").getPrecision()%>' readonly></td>
                        <td class="td" nowrap onClick="if(<%=isempty%>==true)sumitForm(<%=workloadDeptBean.PRODUCT_AUTO_ADD%>,<%=i%>)"><input type="text" class=ednone_r   onKeyDown="return getNextElement();" id="jjdj_<%=i%>" name="jjdj_<%=i%>" value='<%=detail.get("jjdj")%>' maxlength='<%=list.getColumn("jjdj").getPrecision()%>' readonly></td>
                        <td class="td" nowrap onClick="if(<%=isempty%>==true)sumitForm(<%=workloadDeptBean.PRODUCT_AUTO_ADD%>,<%=i%>)"><input type="text" class=ednone_r   onKeyDown="return getNextElement();" id="jjgs_<%=i%>" name="jjgs_<%=i%>" value='<%=detail.get("jjgs")%>' maxlength='<%=list.getColumn("jjgs").getPrecision()%>' onchange="sl_onchange(<%=i%>)" readonly></td>
                        <td class="td" nowrap onClick="if(<%=isempty%>==true)sumitForm(<%=workloadDeptBean.PRODUCT_AUTO_ADD%>,<%=i%>)"><input type="text" class=ednone_r   onKeyDown="return getNextElement();" id="jjgz_<%=i%>" name="jjgz_<%=i%>" value='<%=detail.get("jjgz")%>' maxlength='<%=list.getColumn("jjgz").getPrecision()%>' onchange="sl_onchange(<%=i%>)" readonly></td>
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
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
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
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td align="right" class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
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
            <td colspan="3" noWrap class="tableTitle">
              <%--if(isEdit){%><input name="button4" type="button" class="button" onClick="sumitForm(<%=buyApplyBean.AUTO_MIN_PRICE%>);" value="产品自动复制"><%}--%>
              <%if(isEdit){%><input name="button2" type="button" class="button" onClick="sumitForm(<%=workloadDeptBean.DELETE_BLANK%>);" value="删除(产品为空)行">
               <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
                 <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +")"%>'/><%}%>
              <%if(isCanDelete && !workloadDeptBean.isReport){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
              <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <%if(!workloadDeptBean.isReport){%><input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
            	  <pc:shortcut key="c" script='backList();'/><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=workloadDeptBean.adjustInputSize(new String[]{"cpbm", "product", "sxz", "sl","hssl", "desl", "jjdj","jjgs","jjgz"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
  }
    function sl_onchange(i,isBigUnit)
    {
      var slObj = document.all['sl_'+i];
      var hsslObj = document.all['hssl_'+i];
      var hsblObj = document.all['hsbl_'+i];
      var jjdjObj = document.all['jjdj_'+i];
      var deslObj = document.all['desl_'+i];
      var jjgsObj = document.all['jjgs_'+i];
      var jjgzObj = document.all['jjgz_'+i];
      var jjgsgsz = <%=formulaBean.getWorkTime()%>;

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
      if(hsblObj.value!="" && !isNaN(hsblObj.value))
        changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
      if(slObj.value!="" && !isNaN(slObj.value) && deslObj.value!="" && deslObj.value!=0)
        jjgsObj.value= formatSum(parseFloat(slObj.value) * jjgsgsz/ parseFloat(deslObj.value));
      if(slObj.value !="" && !isNaN(slObj.value) && jjdjObj.value!="")
        jjgzObj.value= formatSum(parseFloat(slObj.value) * parseFloat(jjdjObj.value));
      cal_tot('sl');
      cal_tot('jjgs');
      cal_tot('jjgz');
    }
    function cal_tot(type)
    {
      var tmpObj;
      var tot=0;
      var je = form1.je;
     for(i=0; i<<%=detailRows.length%>; i++)
       {
       if(type == 'sl')
         tmpObj = document.all['sl_'+i];
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
     else if(type == 'jjgs')
       document.all['t_jjgs'].value = formatQty(tot);
     else if(type == 'jjgz')
       document.all['t_jjgz'].value = formatSum(tot);
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
  </script>
<%out.print(retu);%>
</body>
</html>

<%--生产加工单编辑分切加工单页面从表--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.produce.B_ProduceProcess produceProcessBean = engine.erp.produce.B_ProduceProcess.getInstance(request);
  String pageCode = "produce_process";
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
   function M_productCodeSelect(obj, i)
   {
      ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=wlcpid_'+i+'&srcVar=wlcpbm_'+i+'&srcVar=wlproduct_'+i+'&srcVar=wljldw_'+i+'&srcVar=wlscydw_'+i+'&srcVar=wlscdwgs_'+i+'&srcVar=wlisprops_'+i,
                       'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops', obj.value);
   }
   function M_productNameSelect(obj,i)
   {
     ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=wlcpid_'+i+'&srcVar=wlcpbm_'+i+'&srcVar=wlproduct_'+i+'&srcVar=wljldw_'+i+'&srcVar=wlscydw_'+i+'&srcVar=wlscdwgs_'+i+'&srcVar=wlisprops_'+i,
                       'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops', obj.value);
   }
   function M_propertyNameSelect(obj,cpid,i)
   {
     PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=wldmsxid_'+i+'&srcVar=wlsxz_'+i,
                          'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'M_propertyChange('+i+')');
   }
</script>
<%String retu = produceProcessBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_PRODUCE_UNIT_STYLE = produceProcessBean.SC_PRODUCE_UNIT_STYLE;//是否强制换算
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工序
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp taskBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_TASK_GOODS);//通过任务单明细id得到任务单号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性LookUp
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = produceProcessBean.getMaterTable();
  EngineDataSet list = produceProcessBean.getDetailTable();//加工单信息
  EngineDataSet dsMaterail = produceProcessBean.getMaterailTable();//加工单领料清单
  HtmlTableProducer masterProducer = produceProcessBean.masterProducer;
  HtmlTableProducer detailProducer = produceProcessBean.detailProducer;
  HtmlTableProducer materailProducer = produceProcessBean.materailProducer;//领料清单
  RowMap masterRow = produceProcessBean.getMasterRowinfo();
  RowMap[] detailRows= produceProcessBean.getDetailRowinfos();
  RowMap[] materailRows= produceProcessBean.getMaterailRowinfos();//领料清单
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
            <td class="activeVTab">生产加工单</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                 <%workShopBean.regData(ds,"deptid");%>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jgdh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="jgdh" value='<%=masterRow.get("jgdh")%>' maxlength='<%=ds.getColumn("jgdh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("rq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="rq" value='<%=masterRow.get("rq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=masterReadonly%>>
                    <%if(isEdit && isCanAmend){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.rq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <%String sumit = "if(form1.deptid.value!='"+masterRow.get("deptid")+"'){sumitForm("+produceProcessBean.ONCHANGE+");}";%>
                  <td noWrap class="td">
                    <%if(!isEdit || !isCanAmend) out.print("<input type='hidden' name='deptid' value='"+masterRow.get("deptid")+"'><input type='text' value='"+workShopBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="deptid" addNull="1" onSelect="<%=sumit%>" style="width:110">
                      <%=workShopBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td></td>
                  <td></td>
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
                       <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=produceProcessBean.DETAIL_ADD%>)" src="../images/add.gif" border="0">
                              <pc:shortcut key="a" script='<%="sumitForm("+ produceProcessBean.DETAIL_ADD +")"%>'/><%}%>
                        </td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("rwdmxid").getFieldname()%></td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("gylxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("scsl").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("ypgzl").getFieldname()%></td>
                        <td height='20' nowrap>生产单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("cpl").getFieldname()%>%</td>
                        <td nowrap><%=detailProducer.getFieldInfo("jgyq").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      if(isEdit)
                        technicsRouteBean.regConditionData(list,"cpid");
                      BigDecimal t_sl = new BigDecimal(0), t_ypgzl = new BigDecimal(0),t_scsl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
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
                        String gylxidName = "gylxid_"+i;
                        String rwdmxid = detail.get("rwdmxid");
                        String dmsxid = detail.get("dmsxid");
                        String sx = propertyBean.getLookupName(dmsxid);
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                        boolean isimport = !rwdmxid.equals("");
                        if(zt.equals("0"))
                          isCanRework = engine.erp.baseinfo.BasePublicClass.isRevamp(list, "ypgzl", i);//加工单状态在未审状态时，判断该条纪录是否能被修改
                        String detailReadonly = isCanRework ? readonly : "readonly";
                        boolean isline = isimport || !isCanRework;
                        String Class = isline ? "class=ednone" : detailClass;//从表Class模式
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(isEdit && !isimport && isCanRework){%>
                         <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                          onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=product_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=jldw_<%=i%>&srcVar=scydw_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=isprops_<%=i%>&srcVar=ztqq_<%=i%>','fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fielfVar=jldw&fieldVar=scydw&filedVar=scdwgs&fieldVar=isprops&filedVar=ztqq','','product_change(<%=i%>)')">
                         <%}%><%if(isEdit && isCanRework){%>
                         <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <td class="td" nowrap><%=taskBean.getLookupName(detail.get("rwdmxid"))%></td>
                        <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));%>
                        <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="hidden" name="scdwgs_<%=i%>" value="<%=prodRow.get("scdwgs")%>">
                        <input type="hidden" name="widths_<%=i%>" value="<%=widths%>">
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                        <input type="text" <%=Class%> onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>></td>
                        <td class="td" nowrap><input type="text" <%=Class%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>></td>
                        <td class="td" nowrap>
                        <input <%=Class%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=isimport ? "readonly" : detailReadonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(isEdit && !isimport && isCanRework){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=prodRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap><%if(!isEdit || !isCanRework)out.print("<input type='text' style='width:100' value='"+technicsRouteBean.getLookupName(detail.get("gylxid"))+"' class='ednone' readonly>");
                        else {%>
                        <pc:select name="<%=gylxidName%>" addNull="1" style='width:100'>
                        <%=technicsRouteBean.getList(detail.get("gylxid"),"cpid",detail.get("cpid"))%> </pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="scsl_<%=i%>" name="scsl_<%=i%>" value='<%=detail.get("scsl")%>' maxlength='<%=list.getColumn("scsl").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class="ednone_r" onKeyDown="return getNextElement();" id="ypgzl_<%=i%>" name="ypgzl_<%=i%>" value='<%=detail.get("ypgzl")%>' maxlength='<%=list.getColumn("ypgzl").getPrecision()%>' readonly></td>
                        <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly></td>
                        <td class="td" nowrap ><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="cpl_<%=i%>" name="cpl_<%=i%>" value='<%=detail.get("cpl")%>' maxlength='<%=list.getColumn("cpl").getPrecision()%>' <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="jgyq_<%=i%>" id="jgyq_<%=i%>"value='<%=detail.get("jgyq")%>' maxlength='<%=list.getColumn("jgyq").getPrecision()%>' <%=readonly%>></td>
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
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
                        <td align="right" class="td"><input id="t_ypgzl" name="t_ypgzl" type="text" class="ednone_r" style="width:100%" value='<%=t_ypgzl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
                     <tr>
                    <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("describe").getFieldname()%></td><%--其他信息--%>
                    <td colspan="7" noWrap class="td"><textarea name="describe" rows="3" onKeyDown="return getNextElement();" style="width:660"<%=masterReadonly%>><%=masterRow.get("describe")%></textarea></td>
                    </tr>
                     <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
                    <tr>
                    <td colspan="3" noWrap align="center" class="tableTitle">
                    <%if(isEdit){%><input type="hidden" name="singleImportTask" value="引生产任务单" >
                    <input name="btnback" class="button" type="button" value="引生产任务单(W)" style="width:115" onClick="importTask();">
                    <pc:shortcut key="w" script="importTask();"/><%}%>
                    </td>
                    </tr>
                    </TABLE>
                  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
                   <tr valign="top">
                    <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
                     <tr>
                      <td class="activeVTab">加工单物料清单</td>
                     </tr>
                     </table>
                     <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
                      <tr>
                       <td>
                        <tr>
                         <td colspan="8" noWrap class="td">
                           <%int width2 = (materailRows.length > 5 ? materailRows.length : 5)*23 + 66;%>
                           <div style="display:block;width:750;height=<%=width2%>;overflow-y:auto;overflow-x:auto;">
                           <table id="tableview2" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                            <tr class="tableTitle">
                            <td nowrap width=10></td>
                           <td height='20' align="center" nowrap width=30>
                               <%if(isEdit){%>

                             <input name="image" class="img" type="image" title="新增(Q)" onClick="sumitForm(<%=produceProcessBean.MATERAIL_ADD%>)" src="../images/add.gif" border="0">
                              <pc:shortcut key="q" script='<%="sumitForm("+ produceProcessBean.MATERAIL_ADD +")"%>'/><%}%>
                            </td>
                            <td nowrap>物料编码</td>
                            <td nowrap>物料品名规格</td>
                            <td nowrap><%=materailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                            <td nowrap><%=materailProducer.getFieldInfo("sl").getFieldname()%></td>
                            <td nowrap><%=materailProducer.getFieldInfo("ylsl").getFieldname()%></td>
                            <td nowrap>物料计量单位</td>
                            <td nowrap><%=materailProducer.getFieldInfo("scsl").getFieldname()%></td>
                            <td nowrap><%=materailProducer.getFieldInfo("ylscsl").getFieldname()%></td>
                            <td nowrap>物料生产单位</td>
                            <%materailProducer.printTitle(pageContext, "height='20'", true);%>
                          </tr>
                        <%prodBean.regData(dsMaterail,"cpid");
                          propertyBean.regData(dsMaterail,"dmsxid");
                          BigDecimal t_wlsl = new BigDecimal(0),t_ylsl = new BigDecimal(0),t_wlscsl = new BigDecimal(0),t_ylscsl = new BigDecimal(0);
                          int m=0;
                          RowMap materildetail = null;
                          for(; m<materailRows.length; m++)   {
                            materildetail = materailRows[m];
                            String wlsl = materildetail.get("sl");
                            if(produceProcessBean.isDouble(wlsl))
                              t_wlsl = t_wlsl.add(new BigDecimal(wlsl));
                            String ylsl = materildetail.get("ylsl");
                            if(produceProcessBean.isDouble(ylsl))
                              t_ylsl = t_wlsl.add(new BigDecimal(ylsl));
                            String wlscsl = materildetail.get("scsl");
                            if(produceProcessBean.isDouble(wlscsl))
                              t_wlscsl = t_wlscsl.add(new BigDecimal(wlscsl));
                            String ylscsl = materildetail.get("ylscsl");
                            if(produceProcessBean.isDouble(ylscsl))
                              t_ylscsl = t_ylscsl.add(new BigDecimal(ylscsl));
                            String wldmsxid = materildetail.get("dmsxid");
                            String wlsx = propertyBean.getLookupName(wldmsxid);
                            String M_widths = BasePublicClass.parseEspecialString(wlsx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                            boolean isReceive = !materildetail.get("ylsl").equals("");
                        %>
                          <tr id="rowinfo_<%=m%>">
                            <td class="td" nowrap><%=m+1%></td>
                            <td class="td" nowrap align="center">
                              <%if(isEdit){%>
                          <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                             onClick="ProdSingleSelect('form1','srcVar=wlcpid_<%=i%>&srcVar=wlproduct_<%=i%>&srcVar=wlcpbm_<%=i%>&srcVar=wljldw_<%=i%>&srcVar=wlscydw_<%=i%>&srcVar=wlscdwgs_<%=i%>&srcVar=wlisprops_<%=i%>','fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fielfVar=jldw&fieldVar=scydw&filedVar=scdwgs&fieldVar=isprops','')">
                              <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=produceProcessBean.MATERAIL_DEL%>,<%=m%>)" src="../images/delete.gif" border="0">
                              <%}%>
                            </td>
                            <%RowMap  materailprodRow= prodBean.getLookupRow(materildetail.get("cpid"));%>
                            <td class="td" nowrap><input type="hidden" name="wlcpid_<%=m%>" value="<%=materildetail.get("cpid")%>">
                            <input type="hidden" name="wlscdwgs_<%=m%>" value="<%=materailprodRow.get("scdwgs")%>">
                            <input type="hidden" name="wlwidths_<%=m%>" value="<%=M_widths%>">
                            <input type="hidden" name="wlisprops_<%=m%>" value="<%=materailprodRow.get("isprops")%>">
                            <input type="text" <%=detailClass%> onKeyDown="return getNextElement();" id="wlcpbm_<%=m%>" name="wlcpbm_<%=m%>" value='<%=materailprodRow.get("cpbm")%>' onchange="M_productCodeSelect(this,<%=m%>)" <%=isReceive ? "readonly" : readonly%>></td>
                            <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" id="wlproduct_<%=m%>" name="wlproduct_<%=m%>" value='<%=materailprodRow.get("product")%>' onchange="M_productNameSelect(this,<%=m%>)" <%=isReceive ? "readonly" : readonly%>></td>
                            <td class="td" nowrap>
                            <input <%=detailClass%> name="wlsxz_<%=m%>" value='<%=propertyBean.getLookupName(materildetail.get("dmsxid"))%>' onchange="if(form1.wlcpid_<%=m%>.value==''){alert('请先输入产品');return;}M_propertyNameSelect(this,form1.wlcpid_<%=m%>.value,<%=m%>)" onKeyDown="return getNextElement();" <%=isReceive ? "readonly" : readonly%>>
                            <input type="hidden" id="wldmsxid_<%=m%>" name="wldmsxid_<%=m%>" value="<%=materildetail.get("dmsxid")%>">
                            <%if(isEdit && materildetail.get("ylsl").equals("")){%>
                            <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.wlcpid_<%=m%>.value==''){alert('请先输入产品');return;}if(form1.wlisprops_<%=m%>.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','wldmsxid_<%=m%>','wlsxz_<%=m%>',form1.wlcpid_<%=m%>.value,'M_propertyChange(<%=m%>)')">
                            <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="wldmsxid_<%=m%>.value='';wlsxz_<%=m%>.value='';">
                            <%}%>
                            </td>
                            <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="wlsl_<%=m%>" name="wlsl_<%=m%>" value='<%=materildetail.get("sl")%>' maxlength='<%=dsMaterail.getColumn("sl").getPrecision()%>' onchange="M_slchange(<%=m%>, false)"  <%=isReceive ? "readonly" : readonly%>></td>
                            <td class="td" align="right" nowrap><%=materildetail.get("ylsl")%></td>
                            <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="wljldw_<%=m%>" name="wljldw_<%=m%>" value='<%=materailprodRow.get("jldw")%>' readonly></td>
                            <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="wlscsl_<%=m%>" name="wlscsl_<%=m%>" value='<%=materildetail.get("scsl")%>' maxlength='<%=dsMaterail.getColumn("scsl").getPrecision()%>' onchange="M_slchange(<%=m%>, true)" <%=isReceive ? "readonly" : readonly%>></td>
                            <td class="td" align="right" nowrap><%=materildetail.get("ylscsl")%></td>
                            <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="wlscydw_<%=m%>" name="wlscydw_<%=m%>" value='<%=materailprodRow.get("scydw")%>' readonly></td>
                            <%FieldInfo[] bakFields = materailProducer.getBakFieldCodes();
                            for(int k=0; k<bakFields.length; k++)
                            {
                              String fieldCode = bakFields[k].getFieldcode();
                              out.print("<td class='td' nowrap>");
                              out.print(materailProducer.getFieldInput(bakFields[k], materildetail.get(fieldCode), fieldCode+"_"+k, "style='width:65'", isEdit, true));
                              out.println("</td>");
                            }
                            %>
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
                            <%materailProducer.printBlankCells(pageContext, "class=td", true);%>
                          </tr>
                          <%}%>
                           <tr id="rowinfo_end">
                           <td class="td">&nbsp;</td>
                           <td class="tdTitle" nowrap>合计</td>
                           <td class="td">&nbsp;</td>
                           <td class="td">&nbsp;</td>
                           <td class="td">&nbsp;</td>
                           <td align="right" class="td"><input id="t_wlsl" name="t_wlsl" type="text" class="ednone_r" style="width:100%" value='<%=t_wlsl%>' readonly></td>
                           <td align="right" class="td"><input id="t_ylsl" name="t_ylsl" type="text" class="ednone_r" style="width:100%" value='<%=t_ylsl%>' readonly></td>
                           <td class="td">&nbsp;</td>
                           <td align="right" class="td"><input id="t_wlscsl" name="t_wlscsl" type="text" class="ednone_r" style="width:100%" value='<%=t_wlscsl%>' readonly></td>
                           <td align="right" class="td"><input id="t_ylscsl" name="t_ylscsl" type="text" class="ednone_r" style="width:100%" value='<%=t_ylscsl%>' readonly></td>
                           <td class="td">&nbsp;</td>
                          <%materailProducer.printBlankCells(pageContext, "class=td", true);%>
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
            <td colspan="3" noWrap class="tableTitle">
             <%if(isEdit){%>
             <%--input type="hidden" name="mutitask" value="" onchange="sumitForm(<%=produceProcessBean.DETAIL_SELECT_TASK%>)">
             <input name="btnback" class="button" type="button" value="引入任务明细(E)" style="width:115" onClick="if(form1.deptid.value==''){alert('请选择车间'); return;}TaskGoodsSelect('form1','srcVar=mutitask&deptid='+form1.deptid.value)" border="0">
              <pc:shortcut key="e" script="taskDetail();"/--%>
              <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
                 <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
            <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
              <%if(isCanDelete && isCanAmend){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除 ">
               <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)"> <pc:shortcut key="c" script='backList();'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);initDefaultTableRow('tableview2',1);
<%=produceProcessBean.adjustInputSize(new String[]{"cpbm", "product", "sxz", "cpl","sl","scsl","ypgzl"}, "form1", detailRows.length)%>
<%=produceProcessBean.adjustInputSize(new String[]{"wlcpbm", "wlproduct", "wlsxz", "wlsl","wlscsl"}, "form1", materailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
  function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
  }
  function importTask(){
    ProcessSelTask('form1','srcVar=singleImportTask','fieldVar=rwdid',form1.deptid.value,<%=jglx%>,'sumitForm(<%=produceProcessBean.SINGLE_SELECT_TASK%>)');
  }
  function taskDetail(){
    if(form1.deptid.value=='')
    {
      alert('请选择车间'); return;
    }
    TaskGoodsSelect('form1','srcVar=mutitask&deptid='+form1.deptid.value)
  }
  function product_change(i){
    document.all['dmsxid_'+i].value="";
    document.all['sxz_'+i].value="";
    document.all['widths_'+i].value="";
    associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '');
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
    cal_tot('sl');
    cal_tot('scsl');
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
   function M_propertyChange(i){
    var sxzObj = document.all['wlsxz_'+i];
    var scdwgsObj = document.all['wlscdwgs_'+i];
    if(sxzObj.value=='')
      return;
    var widthObj = document.all['wlwidths_'+i];
    widthValue = parseString(sxzObj.value, '<%=SYS_PRODUCT_SPEC_PROP%>(', ')', '(');
    if(widthValue=='')
      return;
    widthObj.value =  widthValue;
    if(widthObj.value=='' || isNaN(widthObj.value))
      return;
    var slObj = document.all['wlsl_'+i];
    var scslObj = document.all['wlscsl_'+i];
    if(slObj.value=='' && scslObj.value=='')
      return;
    else if(slObj.value!='')
      scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthValue));
    else if(slObj.value=='' && scslObj.value!='')
      slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(widthValue)/parseFloat(scdwgsObj.value));
    wlcal_tot('wlsl');
    wlcal_tot('wlscsl');
  }
  function M_slchange(i, isBigUnit)
  {
    var slObj = document.all['wlsl_'+i];
    var scslObj = document.all['wlscsl_'+i];
    var scygsObj = document.all['wlscdwgs_'+i];//生产公式
    var obj = isBigUnit ? scslObj : slObj;
    var widthObj = document.all['wlwidths_'+i];//规格属性的宽度
    var showText = isBigUnit ? "输入的物料生产数量非法" : "输入的物料数量非法";
    var showText2 = isBigUnit ? "输入的物料生产数量小于零" : "输入的物料数量小于零";
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
       wlcal_tot('wlsl');
       wlcal_tot('wlscsl');
     }
     function wlcal_tot(type)
     {
       var tmpObj;
       var tot=0;
         for(i=0; i<<%=materailRows.length%>; i++)
           {
           if(type == 'wlsl')
             tmpObj = document.all['wlsl_'+i];
           else if(type == 'wlscsl')
             tmpObj = document.all['wlscsl_'+i];
           else
             return;
           if(tmpObj.value!="" && !isNaN(tmpObj.value))
             tot += parseFloat(tmpObj.value);
         }
         if(type == 'wlsl')
           document.all['t_wlsl'].value = formatQty(tot);
         else if(type == 'wlscsl')
           document.all['t_wlscsl'].value = formatQty(tot);
   }
   function TaskGoodsSelect(frmName, srcVar, methodName,notin)
   {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
    var winName= "MrpGoodsSelector";
    paraStr = "../produce/select_task_goods.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function ProcessSelTask(frmName,srcVar,fieldVar,curID,jglx,methodName,notin)
 {
   var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
   var winName= "ProcessSelTask";
   paraStr = "../produce/process_singlesel_task.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+curID+"&jglx="+jglx;
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

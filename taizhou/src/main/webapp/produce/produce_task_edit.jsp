<%--生产任务编辑页面从表--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass, java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.produce.B_ProduceTask produceTaskBean = engine.erp.produce.B_ProduceTask.getInstance(request);
  String pageCode = "produce_task";
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
   function backList()
   {
     location.href='produce_task.jsp';
   }
   function productCodeSelect(obj, i)
   {
      ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=ztqq_'+i+'&deptid='+form1.deptid.value,
                       'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=ztqq', obj.value,'product_change('+i+')');
   }
   function productNameSelect(obj,i)
   {
     ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i+'&srcVar=ztqq_'+i+'&deptid='+form1.deptid.value,
                       'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=ztqq', obj.value,'product_change('+i+')');
   }
   function propertyNameSelect(obj,cpid,i)
   {
     PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                          'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
   }
</script>
<%String retu = produceTaskBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//部门
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工序
  engine.project.LookUp workShopBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_STOCK);
  engine.project.LookUp mrpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_MRP_GOODS);//通过物料需求明细id得到物料需求号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = produceTaskBean.getMaterTable();
  EngineDataSet list = produceTaskBean.getDetailTable();
  HtmlTableProducer masterProducer = produceTaskBean.masterProducer;
  HtmlTableProducer detailProducer = produceTaskBean.detailProducer;
  RowMap masterRow = produceTaskBean.getMasterRowinfo();
  RowMap[] detailRows= produceTaskBean.getDetailRowinfos();
  String zt=masterRow.get("zt");
  boolean isCanAmend = true;//判断取消审批后主表数据是否能修改
  boolean isCanRework = true;//判断取消审批后从表数据是否能修改
  if(zt.equals("0"))
    isCanAmend = engine.erp.baseinfo.BasePublicClass.isRework(list,"yjgl");//取消审批后从表已加工量如果有一条大于零，主表不能修改。
  boolean isEnd = produceTaskBean.isReport || produceTaskBean.isApprove || (!produceTaskBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !produceTaskBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(produceTaskBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  String masterReadonly = isCanAmend ? readonly : "readonly";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("9") ? "审批中" : "未审核");
  boolean isAdd = produceTaskBean.isDetailAdd;
  String SC_PRODUCE_UNIT_STYLE = produceTaskBean.SC_PRODUCE_UNIT_STYLE;//是否强制换算
  String SYS_PRODUCT_SPEC_PROP =produceTaskBean.SYS_PRODUCT_SPEC_PROP;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  String jhlx = produceTaskBean.getMrpType();
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
            <td class="activeVTab">生产任务维护(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                 <%workShopBean.regData(ds,"deptid");deptBean.regData(ds,"bm_deptid");%>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("rwdh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="rwdh" value='<%=masterRow.get("rwdh")%>' maxlength='<%=ds.getColumn("rwdh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("rq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="rq" value='<%=masterRow.get("rq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=masterReadonly%>>
                    <%if(!isEnd && isCanAmend){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.rq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bm_deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd || !isCanAmend) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("bm_deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="bm_deptid" addNull="1" style="width:110">
                      <%=deptBean.getList(masterRow.get("bm_deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <%String sumit = "if(form1.deptid.value!='"+masterRow.get("deptid")+"'){sumitForm("+produceTaskBean.ONCHANGE+");}";%>
                  <td noWrap class="td">
                    <%if(isEnd || !isCanAmend) out.print("<input type='hidden' name='deptid' value='"+masterRow.get("deptid")+"'><input type='text'  value='"+workShopBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="deptid" addNull="1" onSelect="<%=sumit%>" style="width:110">
                      <%=workShopBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
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
                           <%if(!isEnd && !jhlx.equals("1")){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%>
                        </td>
                        <td height='20' nowrap>物料需求号</td>
                        <td height='20' nowrap>单位名称</td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                       <td nowrap><%=detailProducer.getFieldInfo("scsl").getFieldname()%></td>
                       <td nowrap><%=detailProducer.getFieldInfo("yjgl").getFieldname()%></td>
                        <td height='20' nowrap>生产单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("gylxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("csl").getFieldname()%>%</td>
                        <td nowrap><%=detailProducer.getFieldInfo("ksrq").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("wcrq").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jgyq").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      mrpBean.regData(list,"wlxqjhmxid");
                      if(!isEnd)
                        technicsRouteBean.regConditionData(list,"cpid");
                      BigDecimal t_sl = new BigDecimal(0),t_yjgl = new BigDecimal(0),t_scsl = new BigDecimal(0);;
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(produceTaskBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String scsl = detail.get("scsl");
                       if(produceTaskBean.isDouble(scsl))
                          t_scsl = t_scsl.add(new BigDecimal(scsl));
                        String yjgl = detail.get("yjgl");
                        if(produceTaskBean.isDouble(yjgl))
                          t_yjgl = t_yjgl.add(new BigDecimal(yjgl));
                        String gylxidName = "gylxid_"+i;
                        String dmsxidName = "dmsxid_"+i;
                        String dmsxid = detail.get("dmsxid");
                        String sx = propertyBean.getLookupName(dmsxid);
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                        String wlxqjhmxid = detail.get("wlxqjhmxid");
                        boolean isimport = !wlxqjhmxid.equals("");
                        if(zt.equals("0"))
                          isCanRework = engine.erp.baseinfo.BasePublicClass.isRevamp(list, "yjgl", i);//任务单状态在未审状态时，判断该条纪录是否能被修改
                        String detailReadonly = isCanRework ? readonly : "readonly";
                        boolean isline = isimport || !isCanRework;
                        String Class = isline ? "class=ednone" : detailClass;//从表Class模式
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(!isEnd && !isimport && isCanRework){%>
                       <img style='cursor:hand' title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=scydw_<%=i%>&srcVar=isprops_<%=i%>&srcVar=scdwgs_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=isprops&fieldVar=scdwgs','&deptid='+form1.deptid.value,'product_change(<%=i%>)')">
                          <%}if(!isEnd && isCanRework){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <%RowMap mrpRow =mrpBean.getLookupRow(detail.get("wlxqjhmxid")); %>
                        <td class="td" nowrap><%=mrpRow.get("wlxqh")%></td>
                        <td class="td" nowrap><%=mrpRow.get("dwmc")%></td>
                        <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));%>
                        <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="hidden" name="scdwgs_<%=i%>" value="<%=prodRow.get("scdwgs")%>">
                        <input type="hidden" name="widths_<%=i%>" value="<%=widths%>">
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                        <input type="hidden" name="ztqq_<%=i%>" value="<%=prodRow.get("ztqq")%>">
                        <input type="text" <%=Class%> onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>></td>
                        <td class="td" nowrap><input type="text" <%=Class%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>></td>
                    <td class="td" nowrap>
                        <input <%=Class%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=isimport ? "readonly" : detailReadonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd && !isimport && isCanRework){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=prodRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="scsl_<%=i%>" name="scsl_<%=i%>" value='<%=detail.get("scsl")%>' maxlength='<%=list.getColumn("scsl").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class="ednone_r" onKeyDown="return getNextElement();" id="yjgl_<%=i%>" name="yjgl_<%=i%>" value='<%=detail.get("yjgl")%>' maxlength='<%=list.getColumn("yjgl").getPrecision()%>' readonly></td>
                        <td class="td" align="center" nowrap><input type="text" class=ednone style="width:65" onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly></td>
                         <td class="td" nowrap><%if(isEnd || !isCanRework)out.print("<input type='text' style='width:90' value='"+technicsRouteBean.getLookupName(detail.get("gylxid"))+"' class='ednone' readonly>");
                        else {%>
                        <pc:select name="<%=gylxidName%>" addNull="1" style='width:90' >
                        <%=technicsRouteBean.getList(detail.get("gylxid"),"cpid",detail.get("cpid"))%> </pc:select>
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="csl_<%=i%>" name="csl_<%=i%>" value='<%=detail.get("csl")%>' maxlength='<%=list.getColumn("csl").getPrecision()%>' onchange="checkCsl(<%=i%>)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:65" onKeyDown="return getNextElement();" name="ksrq_<%=i%>" id="ksrq_<%=i%>"value='<%=detail.get("ksrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)"></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:65" onKeyDown="return getNextElement();" name="wcrq_<%=i%>" id="wcrq_<%=i%>"value='<%=detail.get("wcrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)"></td>
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
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
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
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
                        <td align="right" class="td"><input id="t_yjgl" name="t_yjgl" type="text" class="ednone_r" style="width:100%" value='<%=t_yjgl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
                    <tr>
                    <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sm").getFieldname()%></td><%--其他信息--%>
                    <td colspan="7" noWrap class="td"><textarea name="sm" rows="3" onKeyDown="return getNextElement();" style="width:690"<%=masterReadonly%>><%=masterRow.get("sm")%></textarea></td>
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
             <%if(!isEnd){%><input type="hidden" name="singleImportMrp" value="引物料需求(E)" >
              <input name="btnback" class="button" type="button" value="引物料需求(E)" style="width:100" onClick="singlemrp();">
              <pc:shortcut key="e" script="singlemrp();"/>
             <%--input type="hidden" name="mutimrp" value="" onchange="sumitForm(<%=produceTaskBean.DETAIL_SELECT_MRP%>)">
             <input name="btnback" class="button" type="button" value="引入物料需求明细(W)" style="width:140" onClick="if(form1.deptid.value==''){alert('请选择车间'); return;}MrpGoodsSelect('form1','srcVar=mutimrp&deptid='+form1.deptid.value)" border="0">
                <pc:shortcut key="w" script="importMrp();"/--%>
             <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
                <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
              <%if(isCanDelete && isCanAmend && !produceTaskBean.isReport){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除 ">
              <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <%if(!produceTaskBean.isApprove && !produceTaskBean.isReport){%><input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
            	  <pc:shortcut key="c" script='backList();'/><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=produceTaskBean.adjustInputSize(new String[]{"cpbm", "product", "sxz", "csl","sl","scsl", "jgyq", "yjgl"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
  function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
  }
  function importMrp(){
    if(form1.deptid.value=='')
    {
      alert('请选择车间');
      return;
    }
    MrpGoodsSelect('form1','srcVar=mutimrp&deptid='+form1.deptid.value);
  }
  function singlemrp(){
    if(form1.deptid.value=='')
   {
     alert('请选择车间');
     return;
   }
    TaskSelMrp('form1','srcVar=singleImportMrp','fieldVar=wlxqjhid',form1.deptid.value,<%=jhlx%>,'sumitForm(<%=produceTaskBean.SINGLE_SELECT_MRP%>)');
  }
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
  cal_tot('yjgl');
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
        else if(type == 'yjgl')
          tmpObj = document.all['yjgl_'+i];
        else
          return;
        if(tmpObj.value!="" && !isNaN(tmpObj.value))
          tot += parseFloat(tmpObj.value);
      }
      if(type == 'sl')
        document.all['t_sl'].value = formatQty(tot);
      else if(type == 'scsl')
        document.all['t_scsl'].value = formatQty(tot);
      else if(type == 'yjgl')
        document.all['t_yjgl'].value = formatQty(tot);
   }
  function MrpGoodsSelect(frmName, srcVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "MrpGoodsSelector";
    paraStr = "../produce/mrp_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function TaskSelMrp(frmName,srcVar,fieldVar,curID,jhlx,methodName,notin)
 {
   var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
   var winName= "TaskSelMrp";
   paraStr = "../produce/task_singlesel_mrp.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+curID+"&jhlx="+jhlx;
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
  <%if(produceTaskBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</body>
</html>

<%--在生产计划中下达物料需求计划页面--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,engine.erp.baseinfo.BasePublicClass,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.jit.PlanMakeMrp planMakeMrp = engine.erp.jit.PlanMakeMrp.getInstance(request);
  String pageCode = "mrp";
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
   function propertyNameSelect(obj,cpid,i)
   {
     PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                        'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'sumitForm(<%=planMakeMrp.SUMIT_PROPERTY%>,'+i+')');
   }
   function productCodeSelect(obj, i)
  {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value,'sumitForm(<%=planMakeMrp.PRODUCT_ONCHANGE%>,'+i+')');
  }
 function productNameSelect(obj,i)
 {
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
              'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value,'sumitForm(<%=planMakeMrp.PRODUCT_ONCHANGE%>,'+i+')');
  }
   function refresh()
   {
     window.close();
     window.opener.sumitForm(<%=Operate.INIT%>,-1);
   }
</script>
<%String retu = planMakeMrp.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT_STOCK);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_ORDER);//根据htid得到合同编号
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工序
  engine.project.LookUp planBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCE_PLAN);//根据生产计划id得到生产计划号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp planUseAbleBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PALN_USABLE_NUMBER_TWO);//通过产品ID和规格属性ID得到计划可供量
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = planMakeMrp.getMaterTable();
  EngineDataSet list = planMakeMrp.getDetailTable();
  HtmlTableProducer masterProducer = planMakeMrp.masterProducer;
  HtmlTableProducer detailProducer = planMakeMrp.detailProducer;
  RowMap masterRow = planMakeMrp.getMasterRowinfo();
  RowMap[] detailRows= planMakeMrp.getDetailRowinfos();
  String zt=masterRow.get("zt");
  boolean isCanAmend = true;//判断取消审批后主表数据是否能修改
  boolean isCanRework = true;//判断取消审批后从表数据是否能修改
  boolean isBuy = true;//已经采购
  boolean isTask = true;//已经安排任务
  if(zt.equals("0")){
    isTask = engine.erp.baseinfo.BasePublicClass.isRework(list,"yprwl");//取消审批后从表已排任务量如果有一条大于零，主表不能修改。
    isBuy = engine.erp.baseinfo.BasePublicClass.isRework(list,"ygl");//已购量
    isCanAmend = isBuy && isTask;
  }
  boolean isEnd = (!planMakeMrp.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !planMakeMrp.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(planMakeMrp.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  String masterReadonly = isCanAmend ? readonly : "readonly";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("9") ? "审批中" : "未审核");
  //boolean isAdd = planMakeMrp.isMasterAdd;
  String SYS_PRODUCT_SPEC_PROP =planMakeMrp.SYS_PRODUCT_SPEC_PROP;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
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
            <td class="activeVTab">物料需求计划维护(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                 <%deptBean.regData(ds,"deptid");
                 planBean.regData(ds,"scjhid");%>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("wlxqh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="wlxqh" value='<%=masterRow.get("wlxqh")%>' maxlength='<%=ds.getColumn("wlxqh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("rq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="rq" value='<%=masterRow.get("rq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=masterReadonly%>>
                    <%if(!isEnd && isCanAmend){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.rq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                  <input type='text' value='<%=deptBean.getLookupName(masterRow.get("deptid"))%>' style='width:110' class='edline' readonly>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("scjhid").getFieldname()%><input type="hidden" name="scjhid" value="<%=masterRow.get("scjhid")%>"></td>
                   <td class="td" nowrap><input class="edline" style="WIDTH:110px" name="jhh" value='<%=planBean.getLookupName(masterRow.get("scjhid"))%>' readonly>
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
                int width = (detailRows.length > 4 ? (detailRows.length > 10 ? 10 : detailRows.length) : 4)*23 + 66;
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                           <%if(!isEnd ){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=planMakeMrp.DETAIL_ADD_BLANK%>)" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+ planMakeMrp.DETAIL_ADD_BLANK +",-1)"%>'/><%}%>
                        </td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("htid").getFieldname()%></td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td height='20' nowrap>库存量</td>
                        <td height='20' nowrap>计划可供量</td>
                        <td nowrap><%=detailProducer.getFieldInfo("xqrq").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jlxql").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("xgl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("xql").getFieldname()%></td>
                        <td height='20' nowrap>生产单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("gylxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("yprwl").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("chxz").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("cc").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      propertyBean.regData(list, "dmsxid");
                      planUseAbleBean.regData(list, new String[]{"cpid"});
                      if(!isEnd)
                        technicsRouteBean.regConditionData(list,"cpid");
                      int i=0;
                      boolean isBuyDetail = true;
                      boolean isTaskDetail = true;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String gylxidName = "gylxid_"+i;
                        String chxzName = "chxz_"+i;
                        String cc = detail.get("cc");
                        String chxz=detail.get("chxz");
                        String dmsxid = detail.get("dmsxid");
                        String sxz = propertyBean.getLookupName(dmsxid);
                        String wid = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");//宽度
                        String chxzxs = chxz.equals("1") ? "自制件" : (chxz.equals("2") ? "外购件" : (chxz.equals("3") ? "外协件" : "虚拟件"));
                        if(zt.equals("0")){
                          isBuyDetail = engine.erp.baseinfo.BasePublicClass.isRevamp(list, "ygl", i);//合同状态在未审状态时，判断该条纪录是否能被修改.即已购量为空
                          isTaskDetail = engine.erp.baseinfo.BasePublicClass.isRevamp(list, "yprwl", i);//合同状态在未审状态时，判断该条纪录是否能被修改。即已排任务量为空
                          isCanRework = isBuyDetail && isTaskDetail;
                        }
                        String read = isEnd || cc.equals("0") ? "readonly" : "";
                        detailClass_r = (isEnd || cc.equals("0")) ? "class=ednone_r" : "class=edFocused_r";
                        String htid = detail.get("htid");
                        boolean isimport = !htid.equals("") || cc.equals("0") || !isCanRework;
                      %>
                        <tr id="rowinfo_<%=i%>">
                          <td class="td" nowrap><%=i+1%></td>
                          <td class="td" nowrap align="center">
                        <%if(!isEnd && !isimport && isCanRework){%>
                        <input type="hidden" name="singleIdInput_<%=i%>" value="">
                        <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                        onClick="ProdSingleSelect('form1','srcVar=singleIdInput_<%=i%>','fieldVar=cpid','','sumitForm(<%=planMakeMrp.SINGLE_PRODUCT_ADD%>,<%=i%>)')">
                        <%}if(!isEnd && isCanRework){%>
                        <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                        <%}%>
                        </td>
                          <td class="td" nowrap><%=saleOrderBean.getLookupName(detail.get("htid"))%></td>
                          <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));;%>
                          <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                              <input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                          <td class="td" nowrap><input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                          <td class="td" nowrap>
                          <input <%=(isEnd || cc.equals("0") ? "class=ednone" : "class=edFocused")%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if('form1.cpid_<%=i%>.vlaue'==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.vlaue,<%=i%>)" onKeyDown="return getNextElement();" <%=(isEnd || cc.equals("0") ? "readonly" : readonly)%>>
                          <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                          <input type="hidden" id="width_<%=i%>" name="width_<%=i%>" value="<%=wid%>">
                         <input type="hidden" id="scdwgs_<%=i%>" name="scdwgs_<%=i%>" value="<%=prodRow.get("scdwgs")%>">
                          <%if(!isEnd && !cc.equals("0") && isCanRework){%>
                          <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=prodRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>)')">
                          <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                          <%}%>
                          </td>
                          <td class="td" align="right" nowrap><%=prodRow.get("kcl")%></td>
                          <td class="td" align="right" nowrap><input type="text" class="ednone" id="jhkgl_<%=i%>" name="jhkgl_<%=i%>" value='<%=planUseAbleBean.getLookupName(new String[]{detail.get("cpid")})%>' readonly></td>
                          <td class="td" align="center" nowrap><input type="text" <%=detailClass_r%> style="width:65" onKeyDown="return getNextElement();" id="xqrq_<%=i%>" name="xqrq_<%=i%>" value='<%=detail.get("xqrq")%>' maxlength='10' onChange="checkDate(this)" <%=read%>></td>
                          <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="jlxql_<%=i%>" name="jlxql_<%=i%>" value='<%=detail.get("jlxql")%>' maxlength='<%=list.getColumn("jlxql").getPrecision()%>' <%=read%> onchange="changeEvent(<%=i%>, false)"></td>
                          <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="xgl_<%=i%>" name="xgl_<%=i%>" value='<%=detail.get("xgl")%>' maxlength='<%=list.getColumn("xgl").getPrecision()%>' <%=read%> onchange="changeEvent(<%=i%>, true)"></td>
                         <td class="td" nowrap><input type="text" class=ednone style="width:60" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                          <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="xql_<%=i%>" name="xql_<%=i%>" value='<%=detail.get("xql")%>' maxlength='<%=list.getColumn("xql").getPrecision()%>' <%=read%> onchange="xql_change(<%=i%>)"></td>
                           <td class="td" nowrap><input type="text" class=ednone style="width:60" onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly></td>
                          <td class="td" nowrap><%if(isEnd || cc.equals("0") || !isCanRework)out.print("<input type='text' style='width:100' value='"+technicsRouteBean.getLookupName(detail.get("gylxid"))+"' class='edline' readonly>");
                          else {%>
                          <pc:select name="<%=gylxidName%>" addNull="1" style='width:100'>
                          <%=technicsRouteBean.getList(detail.get("gylxid"),"cpid",detail.get("cpid"))%> </pc:select>
                          <%}%>
                          </td>
                          <td class="td" nowrap><%=detail.get("yprwl")%></td>
                          <td class="td" nowrap>
                          <%if(isEnd || cc.equals("0") || !isCanRework)out.print("<input type='text' name='chxz_"+i+"' value='"+chxzxs+"' style='width:70' class='edline' readonly>");
                            else{%>
                           <%
                            ArrayList opkey = new ArrayList(); opkey.add("1"); opkey.add("2"); opkey.add("3"); opkey.add("4");
                            ArrayList opval = new ArrayList(); opval.add("自制件"); opval.add("外购件"); opval.add("外协件"); opval.add("虚拟件");
                            ArrayList[] lists  = new ArrayList[]{opkey, opval};
                          %>
                            <pc:select name="<%=chxzName%>" addNull="1" style="width:70">
                            <%=planMakeMrp.listToOption(lists, opkey.indexOf(detail.get("chxz")))%>
                          </pc:select>
                          </td><%}%>
                          <td class="td" align="center" nowrap><%=detail.get("cc")%></td>
                          <td class="td" nowrap>
                          <input type="text" <%=cc.equals("0") ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>"value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>' <%=read%>></td>
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
                      for(; i < 5; i++){
                  %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
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
              <%if(!isEnd ){%>
              <%--input name="btnback" type="button" class="button" onClick="sumitForm(<%=planMakeMrp.DETAIL_ADD%>);" value="生成物料需求"--%>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
              <%--if(isCanDelete){%><input name="button3" type="button" class="button" onClick="sumitForm(<%=Operate.DEL%>);" value=" 删除 "><%}--%>
              <input  type="button" class="button" onClick="window.close();" value=" 关闭(X)" onKeyDown="return getNextElement();">
                <pc:shortcut key="x" script='window.close();'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=planMakeMrp.adjustInputSize(new String[]{"xql", "jlxql", "xgl","bz", "sxz", "jhkgl"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function propertyChange(i){
      var sxzObj = document.all['sxz_'+i];
      var scdwgsObj = document.all['scdwgs_'+i];
      if(sxzObj.value=='')
        return;
      var widthObj = document.all['width_'+i];
      widthValue = parseString(sxzObj.value, '<%=SYS_PRODUCT_SPEC_PROP%>(', ')', '(');
      if(widthValue=='')
        return;
      widthObj.value =  widthValue;
      if(widthObj.value=='' || isNaN(widthObj.value))
        return;
      var jhkglObj = document.all['jhkgl_'+i];
      var jlxqlObj = document.all['jlxql_'+i];//计量需求量，即单位为计量单位
      var xglObj = document.all['xgl_'+i];
      var xqlObj = document.all['xql_'+i];//需求量，生产单位
      jhkgl = jhkglObj.value=="" ? 0 : parseFloat(jhkglObj.value);
      jlxql = jlxqlObj.value=="" ? 0 : parseFloat(jlxqlObj.value);
      xgl = xglObj.value=="" ? 0 : parseFloat(xglObj.value);
      if(jlxqlObj.value=='' && xqlObj.value=='')
        return;
      else if(jlxqlObj.value!=''){
        xqlObj.value = formatQty(parseFloat(jlxqlObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthValue));
        if(jhkgl<=0)
            xglObj.value = jlxqlObj.value;
          else if(jhkgl>=jlxql)
            xglObj.value=0;
          else if(jhkgl<jlxql)
            xglObj.value = jlxql - jhkgl;
    }
    }
    function changeEvent(i, isChange)
    {
      var jhkglObj = document.all['jhkgl_'+i];
      var jlxqlObj = document.all['jlxql_'+i];//计量需求量，即单位为计量单位
      var xglObj = document.all['xgl_'+i];
      var xqlObj = document.all['xql_'+i];//需求量，生产单位
      var widthObj = document.all['width_'+i];//规格属性的宽度
      var scdwgsObj = document.all['scdwgs_'+i];//该行产品的生产单位公式
      var obj = isChange ? xglObj : jlxqlObj;
      var showText = isChange ? "输入的生产数量非法" : "输入的数量非法";
      var showText2 = isChange ? "输入的生产数量小于零" : "输入的数量小于零";
      var changeObj = isChange ? jlxqlObj : xglObj;
      if(obj.value=="")
        return;
      if(isNaN(obj.value))
      {
        alert(showText)
            return;
      }
      if(obj.value<=0)
     {
       alert(showText2)
           return;
      }
      jhkgl = jhkglObj.value=="" ? 0 : parseFloat(jhkglObj.value);
      jlxql = jlxqlObj.value=="" ? 0 : parseFloat(jlxqlObj.value);
      xgl = xglObj.value=="" ? 0 : parseFloat(xglObj.value);
      width = (widthObj.value=="" || isNaN(widthObj.value)) ? 0 : parseFloat(widthObj.value);
      scdwgs = scdwgsObj.value=="" ? 0 : parseFloat(scdwgsObj.value);
      if(!isChange){
        if(jhkgl<=0)
          xglObj.value = jlxqlObj.value;
        else if(jhkgl>=jlxql)
          xglObj.value=0;
        else if(jhkgl<jlxql)
          xglObj.value = jlxql - jhkgl;
      }
      else{
        if(jhkgl<=0)
          jlxqlObj.value = xgl;
        else
          jlxqlObj.value = jhkgl+xgl;
      }
      if(width==0 || scdwgsObj.value==0)
        xqlObj.value = jlxqlObj.value;
      else
        xqlObj.value = formatQty(parseFloat(jlxqlObj.value)*scdwgs/width);
    }
    function xql_change(i)
    {
      var jhkglObj = document.all['jhkgl_'+i];
      var jlxqlObj = document.all['jlxql_'+i];//计量需求量，即单位为计量单位
      var xglObj = document.all['xgl_'+i];
      var xqlObj = document.all['xql_'+i];//需求量，生产单位
      var scdwgsObj = document.all['scdwgs_'+i];//生产单位公式
      var widthObj = document.all['width_'+i];//规格属性的宽度
      var showText3 = "输入的生产数量非法";
      var showText4 =  "输入的生产数量小于零" ;
      if(xqlObj.value=="")
        return;
      if(isNaN(xqlObj.value))
      {
        alert(showText3)
            return;
      }
      if(xqlObj.value<=0)
     {
       alert(showText4)
           return;
      }
      jhkgl = jhkglObj.value=="" ? 0 : parseFloat(jhkglObj.value);
      xgl = xglObj.value=="" ? 0 : parseFloat(xglObj.value);
      width = (widthObj.value=="" || isNaN(widthObj.value)) ? 0 : parseFloat(widthObj.value);
      scdwgs = scdwgsObj.value=="" ? 0 : parseFloat(scdwgsObj.value);
      if(width==0 || scdwgs==0){
        jlxqlObj.value = xqlObj.value;
        if(jhkgl<=0)
          xglObj.value = xqlObj.value;
        else if(jhkgl>=xqlObj.value)
          xglObj.value=0;
        else if(jhkgl<xqlObj.value)
          xglObj.value = xqlObj.value - jhkgl;
      }
      else{
        jlxqlObj.value = formatQty(parseFloat(xqlObj.value)*width/scdwgs);
        temp  = formatQty(parseFloat(xqlObj.value)*width/scdwgs);
        if(jhkgl<=0)
          xglObj.value = temp;
        else if(jhkgl>=temp)
          xglObj.value=0;
        else if(jhkgl<temp)
          xglObj.value = temp - jhkgl;
      }
    }
    function judge(i)
    {
      var xgl = document.all['xgl_'+i];
      if(xgl.value=="")
        return;
      if(isNaN(xgl.value))
      {
        alert('输入的需购量非法')
            return;
      }
    }
  </script>
<%out.print(retu);%>
</body>
</html>

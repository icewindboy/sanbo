<%--生产分切计划编辑页面从表--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,java.util.List,engine.action.BaseAction, engine.erp.produce.DispartMaterial, engine.erp.baseinfo.BasePublicClass, engine.project.Operate,java.util.Hashtable,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
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
  String SYS_PRODUCT_SPEC_PROP =producePlanBean.SYS_PRODUCT_SPEC_PROP;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
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
     location.href='produce_plan.jsp';
   }
  function showFixedQuery(){
     showFrame('detailDiv', true, "", true);
  }

  function taxis(num){
    for(i=0; i<num; i++)
    {
      isRight="0";
      var fdxsObj = document.all['fdxs_'+i];
      if(fdxsObj.value==''){
        isRight = "1";
        alert('不能有空的分段序数');
        return;
      }
      if(isNaN(fdxsObj.value))
      {
        isRight = "1";
        alert("输入的分段序数非法");
        return;
      }
      if(fdxsObj.value<0){
        isRight = "1";
        alert("不能输入小于零的分段序数")
        return;
      }
    }
    if(isRight!="1")
      sumitForm(<%=producePlanBean.SUBPLAN_TAXIS%>);
  }
  function checkFdxs(i)
  {
    var fdxsObj = document.all['fdxs_'+i];
      if(isNaN(fdxsObj.value))
      {
        alert("输入的分段序数非法");
        fdxsObj.focus();
        return;
      }
      if(fdxsObj.value<0){
        alert("不能输入小于零的分段序数")
        fdxsObj.focus();
        return;
      }
  }
  function productCodeSelect(obj, i)
  {
    ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i,
                   'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops&fieldVar=ztqq', obj.value);
  }
  function productNameSelect(obj,i)
  {
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=scydw_'+i+'&srcVar=scdwgs_'+i+'&srcVar=isprops_'+i,
                   'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=scydw&fieldVar=scdwgs&filedVar=isprops', obj.value);
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
  String jhlx = producePlanBean.jhlx;//制定计划的类型，包括通用计划及分切计划。这里是指分切计划，value=1

  Hashtable table = producePlanBean.table;//存放相同分段序数宽度和的Hashtable即小计
  Hashtable remainInfo = producePlanBean.getRemainTable();//存放余料信息的
  ArrayList remainKey = producePlanBean.getRemainKey();//存放remainInfo的Key
  ArrayList tempArray = (ArrayList)remainKey.clone();//临时Array

  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp technicsRouteBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_ROUTE);//根据工艺路线id得到工序
  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_SALE_ORDER_GOODS);//根据销售合同货物id得到合同编号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = producePlanBean.getMaterTable();//得到生产计划主表数据集
  EngineDataSet list = producePlanBean.getDetailTable();//得到生产分切计划从表数据集
  EngineDataSet dsSubMrp = producePlanBean.getSubMrpTable();//得到生产分切计划物料数据集
  HtmlTableProducer masterProducer = producePlanBean.masterProducer;
  HtmlTableProducer subdetailProducer = producePlanBean.detailProducer;//打印生产分切计划表
  HtmlTableProducer submrpProducer = producePlanBean.submrpProducer;//打印生产分切计划物料表
  RowMap masterRow = producePlanBean.getMasterRowinfo();
  RowMap[] detailRows= producePlanBean.getDetailRowinfos();
  RowMap[] subMrpRows = producePlanBean.getSubMrpRowinfos();//把生产分切计划物料表推入到数组里，数组保存了数据集数据
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
  String minwidth = producePlanBean.getMinWidth();//小计后，得到小计中规格属性宽度和的最小值
%>
   <BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1'); onload();">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="dispartmethod" value=""><!--选择哪种最佳搭配原料方法-->
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">生产分切计划维护(<%=title%>)</td>
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
                      <td nowrap width=15>
                        <input class="edFocused_r"  name="tCopyNumber" value="<%=request.getParameter("tCopyNumber")==null?"1":request.getParameter("tCopyNumber")%>"  size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                        </td>
                        <td height='20' align="center" nowrap>
                           <%if(!isEnd){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=producePlanBean.SUBPLAN_DET_ADD%>)" src="../images/add_big.gif" border="0">
                           <pc:shortcut key="a" script='<%="sumitForm("+ producePlanBean.SUBPLAN_DET_ADD +",-1)"%>'/><%}%>
                        </td>
                        <td height='20' nowrap>销售合同号</td>
                        <td height='20' nowrap>单位名称</td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td nowrap><%=subdetailProducer.getFieldInfo("fdxs").getFieldname()%></td>
                        <td nowrap><%=subdetailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <%--td nowrap><%=subdetailProducer.getFieldInfo("gylxid").getFieldname()%></td--%>
                        <td height='20' nowrap>所用用料</td>
                        <td nowrap><%=subdetailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap><%=subdetailProducer.getFieldInfo("scsl").getFieldname()%></td>
                        <td height='20' nowrap>生产单位</td>
                        <td height='20' nowrap>不参与配</td>
                        <td height='20' nowrap>开始日期</td>
                        <td height='20' nowrap>完成日期</td>
                        <td nowrap><%=subdetailProducer.getFieldInfo("jgyq").getFieldname()%></td>
                        <%subdetailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(list,"cpid");
                      saleOrderBean.regData(list,"hthwid");
                      propertyBean.regData(list,"dmsxid");
                      String id =null;//定义一个字符串，当作参数传入到生产分切物料里面。为约束条件
                      if(!isEnd)
                        technicsRouteBean.regConditionData(list,"cpid");
                      //importApplyBean.regData(list,"hthwid");
                      BigDecimal t_sl = new BigDecimal(0),t_scsl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      RowMap olddetail =null;
                      for(; i<detailRows.length; i++)   {
                        String oldfdxs=null, oldfdxh=null;
                        if(i>0){
                          olddetail = detailRows[i-1];
                          oldfdxs = olddetail.get("fdxs");
                          oldfdxh = olddetail.get("fdxh");
                        }
                        detail = detailRows[i];
                        if(i==0)
                          id= detail.get("cpid");
                        String sl = detail.get("sl");
                        if(producePlanBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String scsl = detail.get("scsl");
                        if(producePlanBean.isDouble(scsl))
                          t_scsl = t_scsl.add(new BigDecimal(scsl));
                        String dmsxid = detail.get("dmsxid");
                        String sx = propertyBean.getLookupName(dmsxid);
                        String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                        String fdxs = detail.get("fdxs");//分段序数
                        String fdxh = detail.get("fdxh");//分段序号
                        String gylxidName = "gylxid_"+i;
                        String dmsxidName = "dmsxid_"+i;
                        String hthwid = detail.get("hthwid");
                        RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                        String scdwgs = prodRow.get("scdwgs");
                        detail.put("scdwgs", scdwgs);
                        boolean isimport = !hthwid.equals("");//从表当前行引入销售合同
                        if((!fdxs.equals(oldfdxs) || !fdxh.equals(oldfdxh)) && i!=0 && !oldfdxs.equals("")){
                          String remain = (String)remainInfo.get(oldfdxh+","+oldfdxs);//剩余信息
                          if(remain !=null)
                            tempArray.remove(oldfdxh+","+oldfdxs);
                    %>
                    <%--if(remainInfo.get(oldfdxh+","+oldfdxs)!=null){%>
                        <tr id="rowinfo_remain">
                         <td class="td">&nbsp;</td>
                         <td class="tdTitle" >余料</td>
                         <td colspan="12" class="td" nowrap><%=remainInfo.get(oldfdxh+","+oldfdxs)%></td>
                        <%subdetailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}--%>
                    <tr id="rowinfo_collect">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>小计</td>
                        <td  colspan="5" class="td" nowrap><%=remain !=null ? remainInfo.get(oldfdxh+","+oldfdxs) : ""%></td>
                        <td align="right" class="td"><%=table.get(oldfdxh+","+oldfdxs)%></td>
                        <%--td class="td">&nbsp;</td--%>
                        <td align="right" class="td"><%--input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly--%></td>
                        <td align="right" class="td"><%--input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly--%></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%subdetailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(!isEnd && !isimport && detail.get("cpid").equals("")){%>
                          <input type="hidden" name="singleIdInput_<%=i%>" value="">
                          <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                          onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=product_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=jldw_<%=i%>&srcVar=scydw_<%=i%>&srcVar=scdwgs_<%=i%>&srcVar=isprops_<%=i%>','fieldVar=cpid&fieldVar=product&fieldVar=cpbm&fielfVar=jldw&fieldVar=scydw&filedVar=scdwgs&fieldVar=isprops','')">
                          <%}%><%if(!isEnd){%>
                          <input name="image" class="img" type="image" title="复制当前行" onClick="if(form1.cpid_<%=i%>.value==''){alert('请输入产品');return;}sumitForm(<%=producePlanBean.SUBDETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=producePlanBean.SUBPLAN_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <%RowMap saleOrderRow = saleOrderBean.getLookupRow(detail.get("hthwid"));%>
                        <td class="td" nowrap><%=saleOrderRow.get("htbh")%></td>
                        <td class="td" nowrap><%=saleOrderRow.get("dwmc")%></td>
                        <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="hidden" name="scdwgs_<%=i%>" value="<%=scdwgs%>">
                        <input type="hidden" name="widths_<%=i%>" value="<%=widths%>">
                        <input type="hidden" name="isprops_<%=i%>" value="<%=prodRow.get("isprops")%>">
                        <input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=isimport ? "class=ednone" : detailClass%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" id="fdxs_<%=i%>" name="fdxs_<%=i%>" value='<%=detail.get("fdxs")%>' maxlength='<%=list.getColumn("fdxs").getPrecision()%>' <%=readonly%> onchange="checkFdxs(<%=i%>)"></td>
                        <td class="td" nowrap>
                        <input <%=(isimport || isEnd) ? "class=ednone" : detailClass%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=isimport ? "readonly" : readonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd && !isimport){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if(form1.isprops_<%=i%>.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'propertyChange(<%=i%>);')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <%--td class="td" nowrap>
                        <%if(isEnd)out.print("<input type='text' style='width:100' value='"+technicsRouteBean.getLookupName(detail.get("gylxid"))+"' class='ednone' readonly>");
                        else {%>
                        <pc:select name="<%=gylxidName%>" addNull="1" style='width:80'>
                        <%=technicsRouteBean.getList(detail.get("gylxid"),"cpid",detail.get("cpid"))%> </pc:select>
                        <%}%>
                        </td--%>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> id="fdxh_<%=i%>" style="width:65" name="fdxh_<%=i%>" value='<%=detail.get("fdxh")%>' maxlength='<%=list.getColumn("fdxh").getPrecision()%>' <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onblur="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone style="width:60" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="scsl_<%=i%>" name="scsl_<%=i%>" value='<%=detail.get("scsl")%>' maxlength='<%=list.getColumn("scsl").getPrecision()%>' onblur="sl_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone style="width:60" onKeyDown="return getNextElement();" id="scydw_<%=i%>" name="scydw_<%=i%>" value='<%=prodRow.get("scydw")%>' readonly></td>
                        <td class="td" align="center" nowrap><input type="checkbox" name="ischeck_<%=i%>" value="0" <%=detail.get("ischeck").equals("0") ? "checked" : ""%>></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:65" onKeyDown="return getNextElement();" name="ksrq_<%=i%>" id="ksrq_<%=i%>"value='<%=detail.get("ksrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)"></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:65" onKeyDown="return getNextElement();" name="wcrq_<%=i%>" id="wcrq_<%=i%>"value='<%=detail.get("wcrq")%>' maxlength='10'<%=readonly%> onchange="checkDate(this)"></td>
                        <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="jgyq_<%=i%>" id="jgyq_<%=i%>" value='<%=detail.get("jgyq")%>' maxlength='<%=list.getColumn("jgyq").getPrecision()%>' <%=readonly%>></td>
                        <%FieldInfo[] bakFields = subdetailProducer.getBakFieldCodes();
                        for(int k=0; k<bakFields.length; k++)
                        {
                          String fieldCode = bakFields[k].getFieldcode();
                          out.print("<td class='td' nowrap>");
                          out.print(subdetailProducer.getFieldInput(bakFields[k], detail.get(fieldCode), fieldCode+"_"+k, "style='width:65'", isEnd, true));
                          out.println("</td>");
                        }
                        %>
                      </tr>
                      <%if(i==detailRows.length-1 && !fdxs.equals("")){//循环从表ArrayList,到最後一条纪录时，如果分段序数不为空增加一行小计
                          String remain2 = (String)remainInfo.get(fdxh+","+fdxs);//剩余信息
                          if(remain2 !=null)
                            tempArray.remove(fdxh+","+fdxs);
                        %>
                         <%--if(remainInfo.get(fdxh+","+fdxs)!=null){%>
                         <tr id="rowinfo_rema">
                         <td class="td">&nbsp;</td>
                         <td class="tdTitle">余料</td>
                         <td  colspan="4" class="td" nowrap><%=remainInfo.get(fdxh+","+fdxs)!=null ? remainInfo.get(fdxh+","+fdxs) : ""%></td>
                        <%subdetailProducer.printBlankCells(pageContext, "class=td", true);%>
                         </tr>
                      <%}--%>
                        <tr id="rowinfo_col">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>小计</td>
                        <td  colspan="5" class="td" nowrap><%=remain2!=null ? remainInfo.get(fdxh+","+fdxs) : ""%></td>
                        <td align="right" class="td"><%=table.get(fdxh+","+fdxs)%></td>
                        <%--td class="td">&nbsp;</td--%>
                        <td align="right" class="td"><%--input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly--%></td>
                        <td align="right" class="td"><%--input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly--%></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%subdetailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}
                        //boolean isLast = list.next();
                        if(i==detailRows.length-1)
                        {
                          for(int p=0; p<tempArray.size(); p++){%>
                            <tr id="rowinfo_col">
                            <td class="td">&nbsp;</td>
                            <td class="tdTitle" nowrap>余料</td>
                            <td  colspan="5" class="td" nowrap><%=remainInfo.get((String)tempArray.get(p))%></td>
                            <td align="right" class="td"></td>
                            <%--td class="td">&nbsp;</td--%>
                            <td align="right" class="td"><%--input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly--%></td>
                            <td align="right" class="td"><%--input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly--%></td>
                            <td class="td">&nbsp;</td>
                            <td class="td">&nbsp;</td>
                            <td class="td">&nbsp;</td>
                            <td class="td">&nbsp;</td>
                            <td class="td">&nbsp;</td>
                            <td class="td">&nbsp;</td>
                            <td class="td">&nbsp;</td>
                            <%subdetailProducer.printBlankCells(pageContext, "class=td", true);%>
                          </tr><%}
                        }
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
                        <%subdetailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td" colspan="4">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%subdetailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
                    <%--if(!remainInfo.equals("")){
                    %>
                    <tr>
                     <td  noWrap class="tdTitle">物料剩余说明</td>
                     <td colspan="7" noWrap class="td"><%=remainInfo%></td>
                    </tr>
                    <%}--%>
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
            <td colspan="3" noWrap class="tableTitle">
             <%if(!isEnd){%>
                  <INPUT type="hidden" NAME="selmethod" value="">
              <input type="hidden" name="importorder" value="" onchange="sumitForm(<%=producePlanBean.DETAIL_PLAN_SELECT%>)">
              <input name="btnback" class="button" type="button" value="引销售合同货物(W)" style="width:145" onClick="OrderMultiSelect('form1','srcVar=importorder&selmethod=selmethod&jhlx='+<%=jhlx%>)" border="0">
                <pc:shortcut key="w" script="importsale();"/>
                <input name="btnback" class="button" type="button" value="排序" onClick="taxis(<%=detailRows.length%>);"><%}%>
                <%if(detailRows.length>0 && !isEnd){%><input name="btnback" class="button" type="button" value="模拟配料" onClick="showFixedQuery()"><%}%><%--onClick="showFixedQuery()"><%}%>
                <%--input type="hidden" name="mutiSaleOrder" value="" onchange="sumitForm(<%=producePlanBean.MUTI_ORDER_ADD%>)">
              <input name="btnback" class="button" type="button" value="引销售合同" onClick="PlanSelOrder('form1','srcVar=mutiSaleOrder&selmethod=selmethod')"--%>
              <%--input type="hidden" name="storage" value="" onchange="sumitForm(<%=producePlanBean.DETAIL_SELECT_STORE%>)">
              <input name="btnback" class="button" type="button" value="按库存量生产" onClick="StockMultiSelect('form1','srcVar=storage')" border="0"--%>

            </td>
          </tr>
        </table>
        <%--生产分切物料打印信息--%>
        <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
         <tr valign="top">
          <td>
           <table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
            <tr>
              <td class="activeVTab">生产分切计划物料</td>
               </tr>
              </table>
          <%int width2 = (subMrpRows.length > 4 ? subMrpRows.length : 4)*23 + 66;%>
          <div style="display:block;width:750;height=<%=width2%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview2" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                           <%if(!isEnd){%>
                          <%String s= "Select_Materail('form1','srcVar=materailId&minWidth='+"+minwidth+"+'&cpid='+"+id+"')";%>
                          <input type="hidden" name="materailId" value="" onchange="sumitForm(<%=producePlanBean.SELECT_MATERAIL%>)">
                          <input name="image" class="img" type="image" title="新增(D)" src="../images/add_big.gif" border="0"
                          onClick="selMaterail();">
                          <pc:shortcut key="d" script='selMaterail();'/><%}%>
                        </td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td nowrap><%=submrpProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=submrpProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap>生产数量</td>
                        <td height='20' nowrap>生产单位</td>
                        <td nowrap><%=submrpProducer.getFieldInfo("fdxh").getFieldname()%></td>
                        <%submrpProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%prodBean.regData(dsSubMrp,"cpid");
                      propertyBean.regData(dsSubMrp,"dmsxid");
                      BigDecimal t_wlsl = new BigDecimal(0), t_wlscsl= new BigDecimal(0);//总物料数量
                      int m=0;
                      RowMap submrp = null;
                      dsSubMrp.first();
                      for(; m<subMrpRows.length; m++)   {
                        submrp = subMrpRows[m];
                        //dsSubMrp.goToRow(m);
                        //String fd = dsSubMrp.getValue("fdxh");
                        String wlsl = submrp.get("sl");
                        if(producePlanBean.isDouble(wlsl))
                          t_wlsl = t_wlsl.add(new BigDecimal(wlsl));
                        String wlscsl = submrp.get("scsl");
                        if(producePlanBean.isDouble(wlscsl))
                          t_wlscsl = t_wlscsl.add(new BigDecimal(wlscsl));
                        String wldmsxid = submrp.get("dmsxid");
                        String fdxh = submrp.get("fdxh");
                        String wlsx = propertyBean.getLookupName(submrp.get("dmsxid"));
                        String wlwidths = BasePublicClass.parseEspecialString(wlsx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                        RowMap  wlprodRow= prodBean.getLookupRow(submrp.get("cpid"));
                        String wlscdwgs = wlprodRow.get("scdwgs");
                        String dmsxidName = "dmsxid_"+m;
                    %>
                      <tr id="rowinfo_<%=m%>">
                        <td class="td" nowrap><%=m+1%></td>
                        <td class="td" nowrap align="center">
                          <%--if(!isEnd){%>
                          <input type="hidden" name="singleIdInput_<%=m%>" value="">
                          <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                          onClick="ProdSingleSelect('form1','srcVar=singleIdInput_<%=m%>','fieldVar=cpid','','sumitForm(<%=producePlanBean.SINGLE_PRODUCT_ADD%>,<%=m%>)')"--%>
                          <%if(!isEnd){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=producePlanBean.MATERAIL_DEL%>,<%=m%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <%RowMap  prodRow= prodBean.getLookupRow(submrp.get("cpid"));%>
                        <td class="td" nowrap><%=prodRow.get("cpbm")%></td>
                        <td class="td" nowrap><%=prodRow.get("product")%></td>
                        <td class="td" nowrap>
                        <%=propertyBean.getLookupName(submrp.get("dmsxid"))%>
                        </td>
                        <td class="td" nowrap>
                        <input type="hidden" name="wlscdwgs_<%=m%>" value="<%=wlscdwgs%>">
                        <input type="hidden" name="wlwidths_<%=m%>" value="<%=wlwidths%>">
                        <input type="text" <%=detailClass_r%> style="width:100" onKeyDown="return getNextElement();" id="wlsl_<%=m%>" name="wlsl_<%=m%>" value='<%=submrp.get("sl")%>' maxlength='<%=dsSubMrp.getColumn("sl").getPrecision()%>' onblur="wlsl_onchange(<%=m%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap><%=prodRow.get("jldw")%></td>
                       <td class="td" nowrap><input type="text" <%=detailClass_r%> style="width:100" onKeyDown="return getNextElement();" id="wlscsl_<%=m%>" name="wlscsl_<%=m%>" value='<%=submrp.get("scsl")%>' maxlength='<%=dsSubMrp.getColumn("scsl").getPrecision()%>' onblur="wlsl_onchange(<%=m%>, true)" <%=readonly%>></td>
                        <td class="td" nowrap><%=prodRow.get("scydw")%></td>
                        <td class="td" nowrap><%=submrp.get("fdxh")%></td>
                        <%FieldInfo[] bakFields = submrpProducer.getBakFieldCodes();
                        for(int n=0; n<bakFields.length; n++)
                        {
                          String fieldCode = bakFields[n].getFieldcode();
                          out.print("<td class='td' nowrap>");
                          out.print(submrpProducer.getFieldInput(bakFields[n], submrp.get(fieldCode), fieldCode+"_"+n, "style='width:65'", isEnd, true));
                          out.println("</td>");
                        }
                        %>
                      </tr>
                      <%dsSubMrp.next();
                      }
                      for(; m < 4; m++){
                  %>
                      <tr id="rowinfo_<%=m%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%submrpProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                       <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_wlsl" name="t_wlsl" type="text" class="ednone_r" style="width:100%" value='<%=t_wlsl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_wlscsl" name="t_wlscsl" type="text" class="ednone_r" style="width:100%" value='<%=t_wlscsl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%submrpProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table>
           </div>
        <table CELLSPACING=0 CELLPADDING=0 width="760" align="center">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("zdrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("zdr")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
             <%if(!isEnd){%>
              <input name="button2" type="button" class="button" onClick="sumitForm(<%=producePlanBean.SUBPLAN_POST_CONTINUE%>);" value="保存添加(N)">
                  <pc:shortcut key="n" script='<%="sumitForm("+ producePlanBean.SUBPLAN_POST_CONTINUE +",-1)"%>'/>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=producePlanBean.POST%>);" value="保存返回(S)">
             <pc:shortcut key="s" script='<%="sumitForm("+ producePlanBean.POST +",-1)"%>'/><%}%>
              <%if(isCanDelete && !producePlanBean.isReport){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=producePlanBean.SUBMASTER_DEL%>);" value=" 删除(D)">
            <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
              <%if(!producePlanBean.isApprove && !producePlanBean.isReport){%><input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
             	  <pc:shortcut key="c" script='backList();'/><%}%>
            </td>
          </tr>
        </table>
           </td>
           </tr>
         </table>
      </td>
    </tr>
  </table>
  <%--输入模拟配料的条件，即分段长度，毛边误差等，当宽度加起来大于物资原料就肯定得不到最佳配料--%>
  <div class="queryPop" id="detailDiv" name="detailDiv">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD>
       <TABLE cellspacing=2 cellpadding=0 border=0>
        <TR>
         <TD>
         <TR>
         <TD class="tdTitle" colspan=4 align="center" nowrap>分段设置</TD>
         </TR>
         <TR>
         <TD class="td" nowrap>分段长度</TD>
         <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="fdl" name="fdl" value='<%=masterRow.get("fdl")%>' onKeyDown="return getNextElement();">米</TD>
         <%--TD class="td" nowrap>原料分段误差</TD>
         <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="yxwc" name="yxwc" value='<%=masterRow.get("yxwc")%>' onKeyDown="return getNextElement();"></TD--%>
         </TR>
         <TR>
         <TD class="td" nowrap>毛边</TD>
         <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="mb1" name="mb1" value='<%=masterRow.get("mb1")%>' onKeyDown="return getNextElement();">毫米</TD>
         <TD class="td" align="center" nowrap>--</TD>
         <TD class="td" nowrap><INPUT class="edbox" style="WIDTH:130" id="mb2" name="mb2" value='<%=masterRow.get("mb2")%>' onKeyDown="return getNextElement();">毫米</TD>
         </TR>
        </TD>
       </TR>
      <TR>
      <TD nowrap colspan=4 height=30 align="center">
      <%--input type="hidden" name="dispart" value="" onchange="sumitForm(<%=producePlanBean.INSERT_MATERAIL%>)"--%>
      <INPUT class="button" onClick="sumitAddMaterail()" type="button" value=" 确定 " name="button" onKeyDown="return getNextElement();">
       <INPUT class="button" onClick="hideFrame('detailDiv')" type="button" value=" 关闭 " name="button2" onKeyDown="return getNextElement();">
      </TD>
     </TR>
   </TABLE>
   </TD>
   </TR>
</TABLE>
</div>
<%--打印经过模拟配料得到的数据，并可以选择方法--%>
<div class="queryPop" id="bestdispart" name="bestdispart">
  <div class="queryTitleBox" align="right"><A onClick="hideFrame('bestdispart')" href="#"><img src="../images/closewin.gif" border=0></A></div>
  <%List fineList = producePlanBean.getFineList();
  boolean isBest = producePlanBean.isBest();%>
  <TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0 align="center">
    <TR>
      <td class="tableTitle" align="center">选择搭配物料<%=isBest ? "(以下为最佳配料)" : "(以下为有余料的配料)"%>
      </td>
    </TR>
  </TABLE>
<%
  int listCount = fineList.size();
  for (int t=0;t<listCount;t++){
    DispartMaterial[] dispartList = (DispartMaterial[])fineList.get(t);
%>
<table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
   <TR>
    <TD class="td" nowrap></TD>
     <TD colspan="3" nowrap class="td">
      <input type="radio" name="dispart" value="<%=t%>">
     方法_<%=t+1%>
      </TD>
      </TR>
</table>
<%--int len = (dispartList.length > 4 ? dispartList.length : 4)*23 + 66;--%>
<div style="display:block;width=650;overflow-y:auto;overflow-x:auto;">
<TR>
<TD>
  <table id="tableview_<%=t%>" width="630" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td height="20" nowrap>产品编码</td>
      <td nowrap>品名规格</td>
      <td nowrap>规格属性</td>
      <td nowrap>数量</td>
      <td nowrap>计量单位</td>
      <td nowrap>生产数量</td>
      <td nowrap>生产单位</td>
      <td nowrap>件数</td>
    </tr>
    <%
      RowMap  dispartRow = null;
      for(int g=0; g<dispartList.length; g++)
      {
        String[] materail = (String[])(dispartList[g].getId());
        String cpid = materail[0];
        String dmsxid = materail[1];
        float useNum = dispartList[g].getUsedNum();
        float length = dispartList[g].getLength();
        float fdl = masterRow.get("fdl").length()>0 ? Float.parseFloat(masterRow.get("fdl")) : 0;
        double scsl = fdl*length*useNum;
        String sl = materail[2];
        float f_sl = Float.parseFloat(sl);
        //String scsl = materail[3];
        RowMap prodrow = prodBean.getLookupRow(cpid);
    %>
    <tr >
      <td class="td" nowrap><%=prodrow.get("cpbm")%></td>
      <td class="td" nowrap><%=prodrow.get("product")%></td>
      <td class="td" nowrap><%=propertyBean.getLookupName(dmsxid)%></td>
      <td class="td" nowrap><%=String.valueOf(f_sl*useNum)%></td>
      <td class="td" nowrap><%=prodrow.get("jldw")%></td>
      <td class="td" nowrap><%=loginBean.formatNumber(String.valueOf(scsl), loginBean.getQtyFormat())%></td>
      <td class="td" nowrap><%=prodrow.get("scydw")%></td>
      <td class="td" nowrap><%=useNum%></td>
    </tr>
    <%}%>
  </table>
</div>
  <SCRIPT LANGUAGE="javascript">initDefaultTableRow('tableview_<%=t%>',1);</SCRIPT>
  <%}%>
    <table BORDER="0" cellpadding="0" cellspacing="0" width="650">
     <tr>
       <td colspan="3" noWrap align="center"><br>
        <input name="button" type="button" class="button" onClick="sumitDispartForm(<%=producePlanBean.INSERT_MATERAIL%>);" value=" 选用 ">
        <input name="button2" type="button" class="button" onClick="hideFrame('bestdispart');" value=" 关闭 ">
       </td>
     </tr>
    </table>
   </TD>
  </TR>
</div>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);initDefaultTableRow('tableview2',1);
  function onload(){
   <%=producePlanBean.adjustInputSize(new String[]{"cpbm","product", "sl", "jgyq", "sxz", "scsl","fdxs"}, "form1", detailRows.length)%>
  }
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function selMaterail(){
       id = '<%=id%>';
       if(id=='null')
         id =''
       Select_Materail('form1','srcVar=materailId&minWidth='+<%=minwidth%>+'&cpid='+id);
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
      sumitForm(<%=producePlanBean.SUBMASTER_DEL%>,-1);
  }
    function importsale()
  {
    OrderMultiSelect('form1','srcVar=importorder&selmethod=selmethod&jhlx='+<%=jhlx%>);
  }
  function toBestDispart()
  {
    hideFrame('detailDiv');
    showFrame('bestdispart',true,"",true);
  }
  function sumitDispartForm(oper)
  {
    lockScreenToWait("处理中, 请稍候！");
    //row = <%=fineList.size()%>;
    //for(i=0; i<row; i++)
    //{
     // var slObj = document.all['dispart_'+i];
      //if(slObj.checked)
     //   form1.dispartmethod.value = i;
    //}
    form1.operate.value = oper;
    form1.submit();
  }
  function sumitAddMaterail()
  {
    fdlObj = form1.fdl.value;
    //yxwcObj = form1.yxwc.value;
    mb1 = form1.mb1.value;
    mb2 = form1.mb2.value;
    if(fdlObj=='')
    {
      alert('分段长度不能为空');
      return;
    }
    if(fdlObj!='' && isNaN(fdlObj))
    {
      alert('非法分段长度');
      return;
    }
    //if(yxwcObj!='' && isNaN(yxwcObj))
    //{
     // alert('非法分段误差');
     // return;
    //}
    if(mb1!='' && isNaN(mb1))
    {
      alert('请输入准切的毛边区间值');
      return;
    }
    if(mb2!='' && isNaN(mb2))
    {
      alert('请输入准切的毛边区间值');
      return;
    }
       sumitForm(<%=producePlanBean.FINE_DISPART%>);//得到最佳搭配
    }
    function bigsl_change()
    {
      if(<%=detailRows.length%><0)
        return;
      for(k=0; k<<%=detailRows.length%>; k++)
      {
        sl_onchange(k,true);
      }
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
      if(changeObj.value!="" && '<%=SC_PRODUCE_UNIT_STYLE%>' !='1')
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
      if(widthObj.value=='' || widthObj.value=='0' || isNaN(widthObj.value))
      {
        changObj.value=obj.value;
      }
      else{
        if(scygsObj.value!="" && widthObj.value!="" && widthObj.value!='0' && !isNaN(scygsObj.value) && !isNaN(widthObj.value)){
          changeObj.value = formatQty(isBigUnit ? (parseFloat(scslObj.value)*parseFloat(widthObj.value)/parseFloat(scygsObj.value)) : (parseFloat(slObj.value)*parseFloat(scygsObj.value)/parseFloat(widthObj.value)));
          if(isBigUnit)
            slObj.value=changeObj.value;
          else
            scslObj.value=changeObj.value;
        }
      }
      cal_tot('sl');
      cal_tot('scsl');
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
    function wlsl_onchange(m, isBigUnit)
    {
      var wlslObj = document.all['wlsl_'+m];
      var wlscslObj = document.all['wlscsl_'+m];
      var wlscygsObj = document.all['wlscdwgs_'+m];//生产公式
      var wlobj = isBigUnit ? wlscslObj : wlslObj;
      var wlwidthObj = document.all['wlwidths_'+m];//规格属性的宽度
      var showText3 = isBigUnit ? "输入的物料生产数量非法" : "输入的物料数量非法";
      var showText4 = isBigUnit ? "输入的物料生产数量小于零" : "输入的物料数量小于零";
      var wlchangeObj = isBigUnit ? wlslObj : wlscslObj;
      if(wlchangeObj.value!="" && '<%=SC_PRODUCE_UNIT_STYLE%>' !='1')
        return;
      if(wlobj.value=="")
        return;
      if(isNaN(wlobj.value))
      {
        alert(showText3);
        wlobj.focus();
        return;
      }
      if(wlobj.value<=0)
      {
        alert(showText4);
        wlobj.focus();
        return;
      }
      if(wlwidthObj.value=='' || wlwidthObj.value=='0' || isNaN(wlwidthObj.value))
      {
        changObj.value=obj.value;
      }
      if(wlscygsObj.value!="" && wlwidthObj.value!="0" && wlwidthObj.value!='' && !isNaN(wlscygsObj.value) && !isNaN(wlwidthObj.value)){
        wlchangeObj.value = formatQty(isBigUnit ? (parseFloat(wlscslObj.value)*parseFloat(wlwidthObj.value)/parseFloat(wlscygsObj.value)) : (parseFloat(wlslObj.value)*parseFloat(wlscygsObj.value)/parseFloat(wlwidthObj.value)));
        if(isBigUnit)
          wlslObj.value=wlchangeObj.value;
        else
          wlscslObj.value=wlchangeObj.value;
      }
      wlcal_tot('wlsl');
      wlcal_tot('wlscsl');
    }
    function wlcal_tot(type)
    {
      var tmpObj;
      var tot=0;
        for(i=0; i<<%=subMrpRows.length%>; i++)
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
        if(type == 'scsl')
          document.all['t_wlscsl'].value = formatQty(tot);
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
    function Select_Materail(frmName, srcVar)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
      var winName= "Materail";
      paraStr = "../produce/subplan_select_materail.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
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
  </script>
  <%if(producePlanBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</body>
</html>

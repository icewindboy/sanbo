<%--采购申请单从表--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.buy.xixing.BuyApply buyApplyBean = engine.erp.buy.xixing.BuyApply.getInstance(request);
  String pageCode = "buy_apply";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
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
  location.href='buy_apply.jsp';
}
function productCodeSelect(obj, i)
{
  //alert(obj.form.name);
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value);
  //alert(obj.value);
}
function productHsbjSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=hsdw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=hsdw', obj.value);
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value);
}
function productNameHsbjSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=hsdw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=hsdw', obj.value);
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
</script>
<%String retu = buyApplyBean.doService(request, response);
  if(retu.indexOf("backList();")>-1 || retu.indexOf("toFee")>-1 || retu.indexOf("toDock")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp mrpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_MRP_GOODS);//通过物料需求明细id得到物料需求号
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
  engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = buyApplyBean.getMaterTable();
  EngineDataSet list = buyApplyBean.getDetailTable();
  HtmlTableProducer masterProducer = buyApplyBean.masterProducer;
  HtmlTableProducer detailProducer = buyApplyBean.detailProducer;
  RowMap masterRow = buyApplyBean.getMasterRowinfo();
  RowMap[] detailRows= buyApplyBean.getDetailRowinfos();
  String bjfs = null;
  bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  String SYS_CUST_NAME = loginBean.getSystemParam("SYS_CUST_NAME");//客户名称,用于客制化程序
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  String zt=masterRow.get("zt");
  String deptid = masterRow.get("deptid");//得到该单据的制单部门id
  String czyid = masterRow.get("czyid");//得到该单据的制单员id
  boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, czyid);//判断登陆员工是否有操作改制单人单据的权限
  boolean isCanAmend = true;//判断取消审批后主表数据是否能修改
  boolean isCanRework = true;//判断取消审批后从表数据是否能修改
  if(zt.equals("0"))
    isCanAmend = engine.erp.baseinfo.BasePublicClass.isRework(list,"skhtl");//取消审批后从表实开合同量如果有一条大于零，主表不能修改。
  boolean isEnd = buyApplyBean.isApprove ||(!buyApplyBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !buyApplyBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(buyApplyBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  String loginid= buyApplyBean.loginId;
  isEnd = isEnd||!czyid.equals(loginid);
  //boolean isEdit = !(isEnd || !isHasDeptLimit) || (!isEnd && isHasDeptLimit);
  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = (isEnd || !isHasDeptLimit) ? "class=edline" : "class=edbox";
  String detailClass = (isEnd || !isHasDeptLimit) ? "class=ednone" : "class=edFocused";
  String detailClass_r = (isEnd || !isHasDeptLimit) ? "class=ednone_r" : "class=edFocused_r";
  String readonly = (isEnd || !isHasDeptLimit) ? "readonly" : "";
  String masterReadonly = isCanAmend ? readonly : "readonly";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("9") ? "审批中" : "未审核");
  boolean isAdd = buyApplyBean.isDetailAdd;
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('2');">
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
            <td class="activeVTab">采购申请单(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sqbh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sqbh" value='<%=masterRow.get("sqbh")%>' maxlength='<%=ds.getColumn("sqbh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sqrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sqrq" value='<%=masterRow.get("sqrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=masterReadonly%>>
                  <%if(!isEnd && isCanAmend && isHasDeptLimit){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.sqrq);"></a>
                    <%}%>
                  </td>

                 <%personBean.regConditionData(ds, "deptid");%>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%String onChange = "if(form1.deptid.value!='"+masterRow.get("deptid")+"')sumitForm("+buyApplyBean.DEPT_CHANGE+")";%>
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:111" onSelect="<%=onChange%>"> <%=deptBean.getList(masterRow.get("deptid"))%>
                    </pc:select>
                    <%}%>
                  </td>

                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("qgr").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd || !isCanAmend || !isHasDeptLimit) out.print("<input type='text' value='"+personBean.getLookupName(masterRow.get("qgr"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="qgr" addNull="1" style="width:110">
                    <%=personBean.getList(masterRow.get("qgr"), "deptid", masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  </tr>
         </tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd && isHasDeptLimit){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%>
                        </td>
                        <td height='20' nowrap>原辅料编号</td>
                     <%--<td nowrap><%=detailProducer.getFieldInfo("wlxqjhmxid").getFieldname()%></td>--%>
                        <td height='20' nowrap>品名规格</td>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td height='20' nowrap>用途</td>
                         <td nowrap>数量</td>
                         <td nowrap>计量单位</td>
                         <td nowrap>换算数量</td>
                         <td nowrap>换算单位</td>
                         <td nowrap><%=detailProducer.getFieldInfo("dj").getFieldname()%></td>
                         <td nowrap><%=detailProducer.getFieldInfo("je").getFieldname()%></td>
                         <td nowrap><%=detailProducer.getFieldInfo("skhtl").getFieldname()%></td>
                         <td nowrap><%=detailProducer.getFieldInfo("dwtxId").getFieldname()%></td>
                         <td nowrap><%=detailProducer.getFieldInfo("xqrq").getFieldname()%></td>
                         <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                        <%BigDecimal t_sl = new BigDecimal(0), t_je = new BigDecimal(0), t_skhtl = new BigDecimal(0), t_hssl = new BigDecimal(0);
                        int i=0;
                        RowMap detail = null;
                        prodBean.regData(list,"cpid");
                        corpBean.regData(list,"dwtxid");
                        mrpBean.regData(list,"wlxqjhmxid");
                        propertyBean.regData(list,"dmsxid");
                        list.first();
                        for(; i<detailRows.length; i++)   {
                          detail = detailRows[i];
                          String sl = detail.get("sl");
                          String hssl = detail.get("hssl");
                          if(buyApplyBean.isDouble(sl))
                            t_sl = t_sl.add(new BigDecimal(sl));
                          if(buyApplyBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));

                          String skhtl = detail.get("skhtl");
                          if(buyApplyBean.isDouble(skhtl))
                            t_skhtl = t_skhtl.add(new BigDecimal(skhtl));
                          if(zt.equals("0"))
                            isCanRework = engine.erp.baseinfo.BasePublicClass.isRevamp(list, "skhtl", i);//申请单状态在未审状态时，判断该条纪录是否能被修改
                          String detailReadonly = isCanRework ? readonly : "readonly";
                          String wlxqjhmxid=detail.get("wlxqjhmxid");
                          boolean isimport = !wlxqjhmxid.equals("");//如果是引入物料需求当前行产品编码不能输入
                          boolean isline = isimport || !isCanRework || !isHasDeptLimit;
                          String Class = isline ? "class=ednone" : detailClass;//从表Class模式
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                        <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd && !isimport && isCanRework && isHasDeptLimit){%>
                          <img style='cursor:hand' title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=isprops_<%=i%>','fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops',form1.cpid_<%=i%>.value)">
                          <%
                          }if(!isEnd && isCanRework && isHasDeptLimit){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <%-- <td class="td" nowrap><%=mrpBean.getLookupName(detail.get("wlxqjhmxid"))%></td>--%>
                        <%RowMap prodRow = prodBean.getLookupRow(detail.get("cpid"));%>
                        <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="text" <%=(isimport || !isCanRework) ? "class=ednone" : detailClass%>  onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)"<%=isimport ? "readonly" : detailReadonly%>>
                        <%--  <%if(!isHsbj){%><input type="text" <%=(isimport || !isCanRework) ? "class=ednone" : detailClass%>  onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)"<%=isimport ? "readonly" : detailReadonly%>>
                        <%}else{%>
                        <input type="text" <%=Class%> onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productHsbjSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>>
                        <%}%>--%>
                        <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'>
                        <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                        <input type='hidden' id='isprops_<%=i%>' name='isprops_<%=i%>' value='<%=prodRow.get("isprops")%>'>
                        </td>
                     <%-- <td class="td" nowrap><%if(isHsbj){%>
                        <input type="text" <%=Class%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameHsbjSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>></td>
                        <%}else{%>
                        <input type="text" <%=Class%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>><%}%></td>--%>
                        <td class="td" nowrap><input type="text" <%=Class%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=isimport ? "readonly" : detailReadonly%>></td>
                        <td class="td" nowrap>
                        <input <%=Class%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=isimport ? "readonly" : detailReadonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd && !isimport && isCanRework){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=prodRow.get("isprops")%>'=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value)">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                            <td class="td" nowrap align="center"><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="yt_<%=i%>" value='<%=detail.get("yt")%>' maxlength='<%=list.getColumn("yt").getPrecision()%>' <%=readonly%>></td>
                        <td class="td" nowrap align="center"><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>,false)" <%=readonly%>></td>
                        <%--
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="skhtl_<%=i%>" name="skhtl_<%=i%>" value='<%=detail.get("skhtl")%>' maxlength='<%=list.getColumn("skhtl").getPrecision()%>' onchange="cg_onchange(<%=i%>)" <%=readonly%>><%=t_skhtl%></td>
                        --%>
                       <%-- <td class="td" nowrap><%if(!isHsbj){%><input type="text" class=ednone style="width:60" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly ><%}else{%>
                        <input type="text" class=ednone style="width:60" onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly><%}%></td>--%>
                       <td class="td" nowrap><input type="text" class=ednone style="width:60" onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly ></td>
                       <td class="td" nowrap align="center"><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="hssl_onchange(<%=i%>,false)" <%=readonly%>></td>
                       <td class="td" nowrap><input type="text" class=ednone style="width:60" onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly ></td>
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' maxlength='<%=list.getColumn("dj").getPrecision()%>' onchange="dj_onchange(<%=i%>)" <%=readonly%>></td>
                       <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="je_<%=i%>" name="je_<%=i%>" value='<%=detail.get("je")%>' maxlength='<%=list.getColumn("je").getPrecision()%>' onchange="je_onchange(<%=i%>, false)" <%=readonly%>></td>
                       <td class="td" nowrap align="right" input type="text" <%=detailClass_r%> ><%=skhtl%></td>
                        <%/**
                          //BigDecimal zje = new BigDecimal(0);
                          //double sl = (double)(list.getValue("sl"));
                          //double dj = (double)(list.getValue("dj"));
                          //zje = sl.multiply;
                          String zsl = list.getValue("sl");
                          String dj = list.getValue("dj");
                          int a = Integer.parseInt("zsl");
                          int b = Integer.parseInt("dj");
                          double one = (double)a;
                          double two = (double)b;
                          double c = a * b;
                          String d = String.valueOf("c");
                          String zje = d;
                        */

                        %>
                        <%-- <INPUT TYPE="HIDDEN" NAME="rownum" value="<%=zje%>">    总数量 --%>

                        <td class="td" nowrap><input class="edline"  name="dwmc_<%=i%>" value='<%=corpBean.getLookupName(detail.get("dwtxid"))%>' readonly>
                        <input type="hidden" id="dwtxid_<%=i%>" name="dwtxid_<%=i%>" value="<%=detail.get("dwtxid")%>">
                        <%if(!isEnd && isCanRework){%>
                        <img style='cursor:hand' src='../images/view.gif' title="选择采购报价中的供应商" border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}corpSingleSelect('form1','srcVar=dwtxid_<%=i%>&srcVar=dwmc_<%=i%>&srcVar=dj_<%=i%>','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=bj&cpid='+form1.cpid_<%=i%>.value+'&dmsxid='+form1.dmsxid_<%=i%>.value,'','cg_onchange(<%=i%>)')">
                        <%--
                        <img style='cursor:hand' src='../images/view.gif' title="选择往来单位供应商" border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}ProvideSingleSelect('form1','srcVar=dwtxid_<%=i%>&srcVar=dwmc_<%=i%>','fieldVar=dwtxid&fieldVar=dwmc&cpid='+form1.cpid_<%=i%>.value+'&dmsxid='+form1.dmsxid_<%=i%>.value)">
                        --%>
                        <%--img style='cursor:hand' src='../images/view.gif' title="选择供应商" border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}corpSingleSelect('form1','srcVar=dwtxid_<%=i%>&srcVar=dwmc_<%=i%>&srcVar=dj_<%=i%>&srcVar=dmsxid_<%=i%>&srcVar=sxz_<%=i%>','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=bj&fieldVar=dmsxid&fieldVar=sxz&cpid='+form1.cpid_<%=i%>.value+'&dmsxid='+form1.dmsxid_<%=i%>.value,'','cg_onchange(<%=i%>)')"--%>
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid_<%=i%>.value='';dwmc_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap align="center"><input type="text" <%=detailClass%> style="width:65" onKeyDown="return getNextElement();" name="xqrq_<%=i%>" value='<%=detail.get("xqrq")%>' maxlength='10' onchange="checkDate(this)" <%=readonly%>></td>
                        <td class="td" nowrap align="center"><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" name="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>' <%=readonly%>></td>
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
                     <%
                       list.next();
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
                        <%detailProducer.printBlankCells(pageContext, "class=td",true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <td align="right" class="td"><input id="t_skhtl" name="t_skhtl" type="text" class="ednone_r" style="width:100%" value='<%=t_skhtl%>' readonly></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <td class="td"></td>
                        <%detailProducer.printBlankCells(pageContext, "class=td",true);%>
                      </tr>
                    </table></div>
                    </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle">备注</td><%--其他信息--%>
                  <td colspan="7" noWrap class="td"><textarea name="cgyy" rows="3" onKeyDown="return getNextElement();" style="width:690"<%=readonly%>><%=masterRow.get("cgyy")%></textarea></td>
                </tr>
              </table>
            </td>
          </tr>

        </table>
      </td>
    </tr>
        <table CELLSPACING=0 CELLPADDING=0 width="750" align="center">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("czrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("czy")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">

             <%if(!isEnd && isHasDeptLimit){%>
                        <%{%><%--if ( SYS_CUST_NAME.equals("huazheng") )--%>
         <%--      <input type="hidden" name="singleImportMrp" value="引物料需求" >
              <input name="btnback" class="button" type="button" value="引物料需求(W)" style="width:115" onClick="MrpSingleSelect('form1','srcVar=singleImportMrp','fieldVar=wlxqjhid','','sumitForm(<%=buyApplyBean.SINGLE_IMPORT_ADD%>)')">
                      <pc:shortcut key="w" script="importMrp();"/>
             <input type="hidden" name="mutiimportmrp" value="" onchange="sumitForm(<%=buyApplyBean.DETAIL_IMPORT_MRP%>)">
             <input name="btnback" class="button" type="button" value="引物料明细(E)" style="width:115" onClick="ImportMrpSelect('form1','srcVar=mutiimportmrp')" border="0">
                      <pc:shortcut key="e" script="ImportMrpSelect('form1','srcVar=mutiimportmrp')"/> --%>
            <%}%>
       <%--   <input name="button4" type="button" class="button" style="width:115" onClick="sumitForm(<%=buyApplyBean.AUTO_MIN_PRICE%>);" value="填入最低报价(R)">
                      <pc:shortcut key="r" script='<%="sumitForm("+ buyApplyBean.AUTO_MIN_PRICE +",-1)"%>'/>--%>
             <input name="button2" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
                      <pc:shortcut key="n" script='<%="sumitForm("+ Operate.POST_CONTINUE +",-1)"%>'/>
              <input name="btnback" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
              <%if(isCanDelete && isCanAmend && isHasDeptLimit&&!isEnd){%><input name="button3" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value=" 删除(D)">
               <pc:shortcut key="d" script='delMaster();'/><%}%>
              <%--input name="button4" type="button" class="button" onClick="sumitForm(<%=Operate.MASTER_CLEAR%>);" value=" 打印 "--%>
               <%if(!buyApplyBean.isApprove){%><input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
         	  <pc:shortcut key="c" script='backList();'/><%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=buyApplyBean.adjustInputSize(new String[]{"cpbm","product", "sl",  "sxz", "dwmc","bz","hssl","dj","je"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(<%= Operate.DEL%>,-1);
  }
  function importMrp(){
    MrpSingleSelect('form1','srcVar=singleImportMrp','fieldVar=wlxqjhid','','sumitForm(<%=buyApplyBean.SINGLE_IMPORT_ADD%>)');
  }
  function hl_onchange()
  {
    var hlObj = form1.hl;
    if(hlObj.value=="")
      return;
    if(hlObj.value==0) {
      alert('汇率不能为零');
      return;
    }
    if(isNaN(hlObj.value)){
      alert('输入的汇率非法');
      return;
    }
    for(k=0; k<<%=detailRows.length%>; k++)
    {
      cg_onchange(k);
    }
  }



  function sl_onchange(i,isBigUnit)
  {
    var oldhsblObj = document.all['hsbl_'+i];
    var sxzObj = document.all['sxz_'+i];
    unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'nsl_onchange('+i+','+isBigUnit+')');
  }
  function nsl_onchange(i,isBigUnit)
  {
    var slObj = document.all['sl_'+i];
    var hsslObj = document.all['hssl_'+i];
    var djObj = document.all['dj_'+i];
    var jeObj = document.all['je_'+i];
    var hsblObj = document.all['truebl_'+i];
    var sjrklObj = document.all['sjrkl_'+i];
    var hlObj = form1.hl;

    if('<%=bjfs%>'=="0")//报价方式为按计量单位报价时判断
    {

      if(slObj.value=="")
      return;
      if(isNaN(slObj.value)){
        alert("输入的数量非法");
        slObj.focus();
        return;
      }
      if(slObj.value<0){
        alert("不能输入小于等于零的数")
            return;
     }
     if(hsblObj.value!="" && !isNaN(hsblObj.value) && hsblObj.value!="0")
       hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
     else
       hsslObj.value=formatQty(parseFloat(slObj.value)/1);;
     cal_tot('hssl');
     cal_tot('sl');
     if(isNaN(djObj.value))
     {
       alert('输入的单价非法');
       return;
     }
     if(djObj.value=="")
       return;
     if(slObj.value!="" && !isNaN(slObj.value)){
       jeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value));
     }
     else{
       jeObj.value='';
     }
   }
   else//按换算单位报价的判断和计算
   {
    if(slObj.value=="")
       return;
     if(isNaN(slObj.value)){
       alert("输入的换算数量非法");
       slObj.focus();
       return;
     }
       if(slObj.value<0){
         alert("不能输入小于等于零的数")
         return;
     }
     if(hsblObj.value!="" && !isNaN(hsblObj.value))
       //slObj.value= formatQty(parseFloat(hsslObj.value) * parseFloat(hsblObj.value));
      hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
     else
      hsslObj.value=formatQty(parseFloat(slObj.value)/1);
     cal_tot('sl');
     cal_tot('hssl');
     if(djObj.value=="")
       return;
     if(hsslObj.value!="" && !isNaN(hsslObj.value)){
       jeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value));
     }
       else{
       jeObj.value='';
     }
   }
   cal_tot('je');
 }


 function hssl_onchange(i,isBigUnit)
   {
     var oldhsblObj = document.all['hsbl_'+i];
     var sxzObj = document.all['sxz_'+i];
     unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'msl_onchange('+i+','+isBigUnit+')');
   }
   function msl_onchange(i,isBigUnit)
   {
     var slObj = document.all['sl_'+i];
     var hsslObj = document.all['hssl_'+i];
     var djObj = document.all['dj_'+i];
     var jeObj = document.all['je_'+i];
     var hsblObj = document.all['truebl_'+i];
     var sjrklObj = document.all['sjrkl_'+i];
     var hlObj = form1.hl;
     if('<%=bjfs%>'=="0")//报价方式为按计量单位报价时判断
       {
       if(hsslObj.value=="")
         return;
       if(isNaN(hsslObj.value)){
         alert("输入的换算数量非法");
         hsslObj.focus();
         return;
       }
       if(hsslObj.value<0){
         alert("不能输入小于等于零的数")
             return;
       }
       if(hsblObj.value!="" && !isNaN(hsblObj.value))
         slObj.value= formatQty(parseFloat(hsslObj.value) * parseFloat(hsblObj.value));
       //hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
       else
         slObj.value=formatQty(parseFloat(hsslObj.value) * 1);;
       cal_tot('sl');
       cal_tot('hssl');
       if(isNaN(djObj.value))
       {
         alert('输入的单价非法');
         return;
       }
       if(djObj.value=="")
         return;
       if(slObj.value!="" && !isNaN(slObj.value)){
         jeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value));
       }
       else{
         jeObj.value='';
       }
     }
     else//按换算单位报价的判断和计算
     {
       if(hsslObj.value=="")
         return;
       if(isNaN(hsslObj.value)){
         alert("输入的数量非法");
         hsslObj.focus();
         return;
       }
       if(hsslObj.value<0){
         alert("不能输入小于等于零的数")
             return;
       }

       if(hsblObj.value!="" && !isNaN(hsblObj.value) && hsblObj.value!="0")
         //hsslObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
     slObj.value= formatQty(parseFloat(hsslObj.value) * parseFloat(hsblObj.value));
       else
         slObj.value=formatQty(parseFloat(hsslObj.value) * 1);;
       cal_tot('hssl');
       cal_tot('sl');

       if(djObj.value=="")
        return;
      if(hsslObj.value!="" && !isNaN(hsslObj.value)){
        jeObj.value = formatSum(parseFloat(hsslObj.value) * parseFloat(djObj.value));
       }
      else{
        jeObj.value='';
       }
     }
     cal_tot('je');
    }
    function dj_onchange(i)
 {
   var slObj = document.all['sl_'+i];
   var hsslObj = document.all['hssl_'+i];
   var djObj = document.all['dj_'+i];
   var jeObj = document.all['je_'+i];
   var bjfs = <%=bjfs%>;
   if(djObj.value==""){
     jeObj.value='';
     return;
   }
   if(isNaN(djObj.value)){
     alert("输入的单价非法");
     djObj.focus();
     return;
   }
   if(djObj.value<0){
    alert("不能输入小于等于零的数")
    return;
    }
   if(bjfs==0)
   {
     if(slObj.value!="" && !isNaN(slObj.value)){
        jeObj.value = formatQty(parseFloat(slObj.value) * parseFloat(djObj.value));
     }
     else{
       jeObj.value='';
     }
   }
   else
   {
     if(hsslObj.value!="" && !isNaN(hsslObj.value)){
       jeObj.value = formatQty(parseFloat(hsslObj.value) * parseFloat(djObj.value));
     }
     else{
       jeObj.value='';
       ybjeObj.value ='';
     }
   }
   cal_tot('je');
    }
  function cg_onchange(i)
  {
    var slObj = document.all['sl_'+i];
   // var djObj = document.all['dj_'+i];
    var hlObj = form1.hl;
    var xqrqObj = document.all['xqrq_'+i];
    if(slObj.value=="")
      return;
    if(isNaN(slObj.value))
    {
      alert("输入的数量非法");
      slObj.focus();
      return;
    }
    if(slObj.value<=0){
      alert("输入的数量不能小于等于零");
      slObj.focus();
      return;
    }
    cal_tot('sl');
   // if(djObj.value=="")
   //   return;
 //   if(isNaN(djObj.value))
//    {
//      alert("输入的单价非法");
//      djObj.focus();
//      return;
//    }
 //   if(djObj.value<=0){
  //    alert("输入的单价不能小于等于零");
 //     djObj.focus();
 //     return;
 //   }
    /* if(slObj.value!="" && !isNaN(slObj.value))
     jeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value));
     if(slObj.value!="" && !isNaN(slObj.value) && hlObj.value!=0)
     ybjeObj.value = formatSum(parseFloat(slObj.value) * parseFloat(djObj.value)/parseFloat(hlObj.value));
     if(hlObj.value=="" || hlObj.value==0)
     ybjeObj.value ="";
     cal_tot('je');
    */

    cal_tot('ybje');
  }
  function je_onchange(i, isBigUnit)
 {
   var slObj = document.all['sl_'+i];
   var djObj = document.all['dj_'+i];
   var jeObj = document.all['je_'+i];
   var hsslObj = document.all['hssl_'+i];
   var hsblObj = document.all['truebl_'+i];
   var hlObj = form1.hl;
   var bjfs = <%=bjfs%>;
   var obj = jeObj;
   var showText = isBigUnit ? "输入的原币金额非法" : "输入的金额非法";
   var showText2 = isBigUnit ? "输入的原币金额小于零" : "输入的金额小于零";
   var changeObj = jeObj
   if(obj.value=="")
     return;
   if(isNaN(obj.value))
   {
     alert(showText);
     obj.focus();
     return;
   }
   if(!isBigUnit){
     if(bjfs==0){
       if(slObj.value!="" && !isNaN(slObj.value && slObj.value!="0")){
         djObj.value = Math.abs(formatPrice(parseFloat(jeObj.value) / parseFloat(slObj.value)));
         if(jeObj.value*slObj.value<0)
         {
           hsslObj.value=formatQty(-1*hsslObj.value);
           slObj.value=formatQty(-1*slObj.value);
         }
       }
     }
     else{

       if(hsslObj.value!="" && !isNaN(hsslObj.value && hsslObj.value!="0"))
       {
         djObj.value =Math.abs(formatPrice(parseFloat(jeObj.value) / parseFloat(hsslObj.value)));
         if(jeObj.value*slObj.value<0)
         {
           hsslObj.value=formatQty(-1*hsslObj.value);
           slObj.value=formatQty(-1*slObj.value);
         }
       } //Math.abs
     }
   }
   cal_tot('je');
    }
  function cal_tot(type)
  {
    var tmpObj;
    var tot=0;
    for(i=0; i<<%=detailRows.length%>; i++)
    {
      if(type == 'sl')
        tmpObj = document.all['sl_'+i];
      else if(type == 'hssl')
        tmpObj = document.all['hssl_'+i];
     // else if(type == 'je')
     // tmpObj = document.all['je_'+i];
     //else if(type == 'ybje')
     //tmpObj = document.all['ybje_'+i];

      else
        return;
      if(tmpObj.value!="" && !isNaN(tmpObj.value))
        tot += parseFloat(tmpObj.value);
    }
    if(type == 'sl')
      document.all['t_sl'].value = formatQty(tot);
   else if(type == 'hssl')
      document.all['t_hssl'].value = formatQty(tot);
    //else if(type == 'je')
    //document.all['t_je'].value = formatSum(tot);
    //else if(type == 'ybje')
    //document.all['t_ybje'].value = formatSum(tot);
  }
  function corpSingleSelect(frmName,srcVar,fieldVar,curID,methodName)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "corpSelector";
    paraStr = "../buy/buy_corp_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&curID="+curID;
    //alert(methodName);
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    //alert(paraStr);
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function ImportMrpSelect(frmName, srcVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "MrpGoodsSelector";
    paraStr = "../buy/select_mrp_goods.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function MrpSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "MrpSingleSelect";
    paraStr = "../buy/mrp_single_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&curID="+curID;;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
</script>
<%
if(buyApplyBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
</body>
</html>

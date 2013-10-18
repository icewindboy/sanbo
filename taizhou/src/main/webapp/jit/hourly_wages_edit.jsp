<%--计时工作量--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.action.Operate,java.util.ArrayList,engine.html.*"%>
<%@ page import="java.math.BigDecimal,engine.erp.baseinfo.BasePublicClass"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
%>
<%
  engine.erp.jit.HourlyWages hourlyWagesBean = engine.erp.jit.HourlyWages.getInstance(request);
  String pageCode = "hourly_wages";
  if(!loginBean.hasLimits(pageCode, request, response))
    return;
  boolean hasSearchLimit = loginBean.hasLimits(pageCode, op_search);
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
  location.href='hourly_wages_top.jsp';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value);
}
function corpCodeSelect(obj)
{
  //02.18 15:47 CustomerCodeChange函数使用参数1与ProvideCodeChange函数功能是相同的.查找供应商 yjg
  CustomerCodeChange('1',document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
  //ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 //'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(10601)');
}
function productNameSelect(obj,i)
{
   ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
            'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.valu);
}

function corpNameSelect(obj)
{
     CustomerNameChange('1', document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value);
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
     'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
function deptChange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_WORK_GROUP%>', 'gzzid', 'deptid', eval('form1.deptid.value'), '',true);
   //associateSelect(document.all['prod2'], '<%=engine.project.SysConstant.BEAN_WORK_GROUP%>', 'sc__gzzid', 'deptid', eval('form1.bm_deptid.value'), '',true);
   //associateSelect(document.all['prod1'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jsr', 'jsr', eval('form1.jsr.value'), '',true);
}
function personSelect(i, isTextCall)
{
  srcFrm = "form1";
  srcVar = "srcVar=personid_"+i+"&srcVar=xm_"+i;
  fieldVar = "fieldVar=personid&fieldVar=xm";
  methodName = "sumitForm(<%=hourlyWagesBean.PERSONCHANGE%>,"+i+")";
  gzzid = form1.gzzid.value;
  deptid = form1.deptid.value;
  xm = eval("form1.xm_"+i+".value");
  SingleSelectperson(srcFrm, srcVar, fieldVar, gzzid, deptid, xm, methodName, true);
}
function SingleSelectperson(srcFrm, srcVar, fieldVar, gzzid, deptid, xm,methodName, isTextCall)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
   operate = isTextCall?"54":"0";
    //function PersonSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
    //PersonSingleSelect("form1", "srcVar=personid_"+i+"&srcVar=xm_"+i+"&deptid="+form1.deptid.value, "fieldVar=personid&fieldVar=xm",
    //                   "", "methodName=sumitForm(<%=hourlyWagesBean.PERSONCHANGE%>," + i +")");
    paraStr = "../jit/person_select.jsp?operate="+operate+"&srcFrm="+srcFrm+"&"+srcVar+"&"+fieldVar+"&deptid="+deptid
              +"&gzzid="+gzzid+"&xm="+xm+"&isdelete=0"
             +"&methodName="+methodName;
    /*if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    */
  //newWin =window.open(paraStr,"SinglePersonSelector");
  //newWin.focus();
  openSelectUrl(paraStr, "SinglePersonSelector", winopt);
}
</script>
<%String retu = hourlyWagesBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_PRODUCE_UNIT_STYLE = hourlyWagesBean.SC_PRODUCE_UNIT_STYLE;
  String SYS_PRODUCT_SPEC_PROP =hourlyWagesBean.SYS_PRODUCT_SPEC_PROP;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//存货信息
  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_SALE_ORDER_GOODS);//根据销售合同货物id得到合同编号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//通过工作组id得到工作组名称
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS);//通过jgdmxid得到加工单号(?任务单号?)
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = hourlyWagesBean.getMaterTable();
  EngineDataSet list = hourlyWagesBean.getDetailTable();
  HtmlTableProducer masterProducer = hourlyWagesBean.masterProducer;
  HtmlTableProducer detailProducer = hourlyWagesBean.detailProducer;
  RowMap masterRow = hourlyWagesBean.getMasterRowinfo();
  RowMap[] detailRows= hourlyWagesBean.getDetailRowinfos();

  String zt="0";
  boolean isEnd = hourlyWagesBean.isReport || hourlyWagesBean.isApprove || (!hourlyWagesBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !hourlyWagesBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(hourlyWagesBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  String deptid = masterRow.get("deptid");//得到该单据的制单部门id
  String zdrid = masterRow.get("zdrid");//得到该单据的制单员id
  String gzzid = masterRow.get("gzzid");
  boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, zdrid);//判断登陆员工是否有操作该制单人单据的权限

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  //String title = zt.equals("1") ? ("已审核") : (zt.equals("9") ? "审批中" : (zt.endsWith("2") ? "已生成物料需求" : (zt.equals("3") ? "已下达任务" : (zt.equals("8") ? "已完成" : "未审核"))));
  boolean isAdd = hourlyWagesBean.isDetailAdd;
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<script language="javascript">var scaner=parent.scaner;</script>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prod1" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prod2" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="prod3" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="760" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">计时工作量
            </td>
          </tr>
        </table>
             <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%deptBean.regData(ds,"deptid");
                  personBean.regConditionData(ds, "deptid");
                  workGroupBean.regConditionData(ds,"deptid");
                %>
                <tr>
                  <td  noWrap class="tdTitle">单据号</td>
                  <td noWrap class="td">
                    <input type="text"  <%=edClass%> style="width:85"  id="djh" name="djh" value='<%=masterRow.get("djh")%>'  maxlength='<%=ds.getColumn("djh").getPrecision()%>' onKeyDown="return getNextElement();" <%=readonly%> >
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("wage_date").getFieldname()%></td>
                  <td noWrap class="td">
                    <input type="text" name="wage_date" value='<%=masterRow.get("wage_date")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.wage_date);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <%String sumit0 = "sumitForm("+hourlyWagesBean.DEPTCHANGE+");";%>
                  <td noWrap class="td">
                   <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110" onSelect="<%=sumit0%>">
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("gzzid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "sumitForm("+hourlyWagesBean.GZZCHANGE+");";%>
                    <%if(isEnd) out.print("<input type='text' value='"+workGroupBean.getLookupName(masterRow.get("gzzid"))+"' style='width:110' class='edline' readonly>");
                   else {%>
                   <pc:select name="gzzid" addNull="1" style="width:110" onSelect="<%=sumit%>">
                   <%=workGroupBean.getList(masterRow.get("gzzid"),"deptid",masterRow.get("deptid"))%> </pc:select>
                   <%}%>
                  </td>
                  </tr>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("tot_hour_wage").getFieldname()%></td>
                  <td noWrap class="td">
                    <input type="text" id="tot_hour_wage" name="tot_hour_wage" value='<%=masterRow.get("tot_hour_wage")%>' maxlength='<%=ds.getColumn("tot_hour_wage").getPrecision()%>' style="width:85" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>>
                  </td>
                </tr>
                 <%/*打印用户自定义信息*/
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                           <%if(!isEnd){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=Operate.DETAIL_ADD%>)" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script='<%="sumitForm("+ Operate.DETAIL_ADD +",-1)"%>'/><%}%>
                        </td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("personid").getFieldname()%></td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("work_hour").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("work_price").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("work_wage").getFieldname()%></td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("over_hour").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("over_wage").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("night_hour").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("night_wage").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("bounty").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("amerce").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("hour_wage").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("memo").getFieldname()%></td>
                      </tr>
                    <%personBean.regData(list,"personid");
                      BigDecimal t_work_hour = new BigDecimal(0), t_work_wage = new BigDecimal(0),
                                 t_over_hour = new BigDecimal(0), t_over_wage = new BigDecimal(0),
                                 t_night_hour = new BigDecimal(0),t_night_wage = new BigDecimal(0),
                                 t_bounty = new BigDecimal(0),    t_amerce = new BigDecimal(0),t_hour_wage = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++){
                        detail = detailRows[i];
                        String work_hour = detail.get("work_hour");
                        String work_wage = detail.get("work_wage");
                        String over_hour = detail.get("over_hour");
                        String over_wage = detail.get("over_wage");
                        String night_hour = detail.get("night_hour");
                        String night_wage = detail.get("night_wage");
                        String bounty = detail.get("bounty");
                        String amerce = detail.get("amerce");
                        String hour_wage = detail.get("hour_wage");

                        if(hourlyWagesBean.isDouble(work_hour))
                          t_work_hour = t_work_hour.add(new BigDecimal(work_hour));
                        if(hourlyWagesBean.isDouble(work_wage))
                          t_work_wage = t_work_wage.add(new BigDecimal(work_wage));
                        if(hourlyWagesBean.isDouble(over_hour))
                          t_over_hour = t_over_hour.add(new BigDecimal(over_hour));
                        if(hourlyWagesBean.isDouble(over_wage))
                          t_over_wage = t_over_wage.add(new BigDecimal(over_wage));
                        if(hourlyWagesBean.isDouble(night_hour))
                          t_night_hour = t_night_hour.add(new BigDecimal(night_hour));
                        if(hourlyWagesBean.isDouble(night_wage))
                          t_night_wage = t_night_wage.add(new BigDecimal(night_wage));
                        if(hourlyWagesBean.isDouble(bounty))
                          t_bounty = t_bounty.add(new BigDecimal(bounty));
                        if(hourlyWagesBean.isDouble(amerce))
                          t_amerce = t_amerce.add(new BigDecimal(amerce));
                        if(hourlyWagesBean.isDouble(hour_wage))
                          t_hour_wage = t_hour_wage.add(new BigDecimal(hour_wage));
                        //取得任务单LOOKUP bean用来得到任务单号
                        RowMap processRow = processBean.getLookupRow(detail.get("jgdmxID"));
                        RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));

                        String work_price = detail.get("work_price");
                        String night_price = detail.get("night_price");
                        String over_price = detail.get("over_price");
                    %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td>
                        <td class="td" nowrap>
                        <input <%=(isEnd) ? "class=ednone" : detailClass%> name="xm_<%=i%>" value='<%=personBean.getLookupName(detail.get("personid"))%>' onChange="SingleSelectperson('form1', 'srcVar=personid_<%=i%>&srcVar=xm_<%=i%>', 'fieldVar=personid&fieldVar=xm', eval('form1.gzzid.value'), eval('form1.deptid.value'), eval('form1.xm_<%=i%>.value'), 'sumitForm(<%=hourlyWagesBean.PERSONCHANGE%>,<%=i%>)',true);">
                        <input type="hidden" id="personid_<%=i%>" name="personid_<%=i%>" value="<%=detail.get("personid")%>" onChange="sumitForm(<%=hourlyWagesBean.PERSONCHANGE%>, <%=i%>);">
                        <%if(!isEnd){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.deptid.value==''){alert('请先选择部门');return;}personSelect(<%=i%>, true);">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="personid_<%=i%>.value='';xm_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="work_hour_<%=i%>" name="work_hour_<%=i%>" value='<%=detail.get("work_hour")%>' maxlength='<%=list.getColumn("work_hour").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'work_hour')" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="work_price_<%=i%>" name="work_price_<%=i%>" value='<%=detail.get("work_price")%>' maxlength='<%=list.getColumn("work_price").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'work_hour')" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class="ednone_r" align="right" style="width:60" onKeyDown="return getNextElement();" id="work_wage_<%=i%>" name="work_wage_<%=i%>" value='<%=detail.get("work_wage")%>' readonly></td>

                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="over_hour_<%=i%>" name="over_hour_<%=i%>" value='<%=detail.get("over_hour")%>' maxlength='<%=list.getColumn("over_hour").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'over_hour')" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class="ednone_r" onKeyDown="return getNextElement();" id="over_wage_<%=i%>" name="over_wage_<%=i%>" value='<%=detail.get("over_wage")%>' maxlength='<%=list.getColumn("over_wage").getPrecision()%>'  readonly>
                        <input type="hidden" <%=detailClass_r%> onKeyDown="return getNextElement();" id="over_price_<%=i%>" name="over_price_<%=i%>" value='<%=detail.get("over_price")%>' maxlength='<%=list.getColumn("over_price").getPrecision()%>'  <%=readonly%>>
                        </td>

                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="night_hour_<%=i%>" name="night_hour_<%=i%>" value='<%=detail.get("night_hour")%>' maxlength='<%=list.getColumn("night_hour").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'night_hour')" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class="ednone_r"  onKeyDown="return getNextElement();" id="night_wage_<%=i%>" name="night_wage_<%=i%>" value='<%=detail.get("night_wage")%>' maxlength='<%=list.getColumn("night_wage").getPrecision()%>'  readonly>
                        <input type="hidden" <%=detailClass_r%> onKeyDown="return getNextElement();" id="night_price_<%=i%>" name="night_price_<%=i%>" value='<%=detail.get("night_price")%>' maxlength='<%=list.getColumn("night_price").getPrecision()%>'  <%=readonly%>>
                        </td>

                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="bounty_<%=i%>" name="bounty_<%=i%>" value='<%=detail.get("bounty")%>' maxlength='<%=list.getColumn("bounty").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'bounty')" <%=readonly%>></td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="amerce_<%=i%>" name="amerce_<%=i%>" value='<%=detail.get("amerce")%>' maxlength='<%=list.getColumn("amerce").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'amerce')" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class="ednone_r" onKeyDown="return getNextElement();" id="hour_wage_<%=i%>" name="hour_wage_<%=i%>" value='<%=detail.get("hour_wage")%>' maxlength='<%=list.getColumn("hour_wage").getPrecision()%>'  readonly>
                        </td>

                        <td class="td" nowrap><input type="text" <%=detailClass%> onKeyDown="return getNextElement();" id="memo_<%=i%>" name="memo_<%=i%>" value='<%=detail.get("memo")%>' maxlength='<%=list.getColumn("memo").getPrecision()%>'  <%=readonly%>></td>

                        <%FieldInfo[] bakFields = detailProducer.getBakFieldCodes();
                          for(int k=0; k<bakFields.length; k++)
                          {
                            String fieldCode = bakFields[k].getFieldcode();
                            out.print("<td class='td' nowrap>");
                            out.print(detailProducer.getFieldInput(bakFields[k], detail.get(fieldCode), fieldCode+"_"+k, "style='width:65'", isEnd, true));
                            out.println("</td>");
                          }
                        %></tr>
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
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>

                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_work_hour" name="t_work_hour" type="text" class="ednone_r" style="width:100%" value='<%=t_work_hour%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_work_wage" name="t_work_wage" type="text" class="ednone_r" style="width:100%" value='<%=t_work_wage%>' readonly></td>
                        <td align="right" class="td"><input id="t_over_hour" name="t_over_hour" type="text" class="ednone_r" style="width:100%" value='<%=t_over_hour%>' readonly></td>
                        <td align="right" class="td"><input id="t_over_wage" name="t_over_wage" type="text" class="ednone_r" style="width:100%" value='<%=t_over_wage%>' readonly></td>
                        <td align="right" class="td"><input id="t_night_hour" name="t_night_hour" type="text" class="ednone_r" style="width:100%" value='<%=t_night_hour%>' readonly></td>
                        <td align="right" class="td"><input id="t_night_wage" name="t_night_wage" type="text" class="ednone_r" style="width:100%" value='<%=t_night_wage%>' readonly></td>
                        <td align="right" class="td"><input id="t_bounty" name="t_bounty" type="text" class="ednone_r" style="width:100%" value='<%=t_bounty%>' readonly></td>
                        <td align="right" class="td"><input id="t_amerce" name="t_amercee" type="text" class="ednone_r" style="width:100%" value='<%=t_amerce%>' readonly></td>
                        <td align="right" class="td"><input id="t_hour_wage" name="t_hour_wage" type="text" class="ednone_r" style="width:100%" value='<%=t_hour_wage%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
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
             <%if(!isEnd){%>
             <input name="button2" type="button" class="button" title="保存添加(ALT+N)" value="保存添加(N)" style="width:75" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" >
              <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="保存(ALT+S)" value="保存(S)" onClick="sumitForm(<%=Operate.POST%>);" >
              <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%}%>
              <%--02.23 11:46 新增 新增显示下面这几个按钮的条件中加上isReport条件 yjg--%>
              <%if(isCanDelete && isHasDeptLimit && !hourlyWagesBean.isReport){%>
              <input name="button3" type="button" class="button" title="删除(ALT+D)" onClick="buttonEventD()" value="删除(D)">
              <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%if(!hourlyWagesBean.isReport){%>
              <input name="btnback" type="button" class="button" title="返回(ALT+C)" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
              <%--03.08 21:14 新增 新增关闭按钮提供给当此页面是被报表调用时使用. yjg--%>
              <%if(hourlyWagesBean.isReport){%>
              <input name="btnback" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)" onClick="window.close()" >
              <pc:shortcut key="t" script='<%="window.close()"%>'/>
              <%}%>
           <input type="button" class="button" value="打印(P)" onclick="location.href='../pub/pdfprint.jsp?code=hourly_wages_edit_bill&operate=<%=Operate.PRINT_BILL%>&a$hourwage_id=<%=masterRow.get("hourwage_id")%>&src=../jit/hourly_wages_edit.jsp'">
           <%
           String pr = "location.href='../pub/pdfprint.jsp?code=hourly_wages_edit_bill&operate="+Operate.PRINT_BILL+"&a$hourwage_id="+masterRow.get("hourwage_id")+"&src=../finance/hourly_wages_edit.jsp'";
           %>
           <pc:shortcut key="p" script="<%=pr%>" />
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=hourlyWagesBean.adjustInputSize
                     (new String[]{"xm", "work_hour", "work_price", "work_wage",
                                   "over_hour", "over_price", "over_wage", "night_hour",
                                   "night_price", "night_wage", "bounty", "amerce", "hour_wage", "memo"
                                   }, "form1", detailRows.length)
%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
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
      sumitForm(<%= Operate.DEL%>,-1);
  }
  function importsale()
  {
    OrderMultiSelect('form1','srcVar=importorder&selmethod=selmethod');
  }
  function OrderMultiSelect(frmName, srcVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "../produce_huazheng/plan_select_sale.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function sl_onchange(i, type)
  {
    var work_hourObj = document.all['work_hour_'+i];
    var work_priceObj = document.all['work_price_'+i];
    var work_wageObj = document.all['work_wage_'+i];
    var over_hourObj = document.all['over_hour_'+i];
    var over_wageObj = document.all['over_wage_'+i];
    var over_priceObj = document.all['over_price_'+i];
    var night_hourObj = document.all['night_hour_'+i];
    var night_wageObj = document.all['night_wage_'+i];
    var night_priceObj = document.all['night_price_'+i];
    var bountyObj = document.all['bounty_'+i];
    var amerceObj = document.all['amerce_'+i];
    var hour_wageObj = document.all['hour_wage_'+i];

    var showTextwork_hour  = "输入的计时工时或计时单价非法";
    var showTextover_hour = "输入的加班工时非法";
    var showTextnight_hour = "输入的夜班工时非法";
    var showTextbounty = "输入的奖励非法";
    var showTextamerce = "输入的罚款非法";
    var totHourWage = 0;
    switch (type)
    {
      case 'work_hour':
        //计时工时和计时单价
        if(work_hourObj.value=="" || work_priceObj.value=="")
          return;
        if(isNaN(work_hourObj.value)||isNaN(work_priceObj.value))
        {
          alert(eval("showText"+type));
          work_hourObj.focus();
          return;
        }
        work_wageObj.value = formatSum(parseFloat(work_hourObj.value)*parseFloat(work_priceObj.value));
        break
      case 'over_hour':
        var obj = eval(type+"Obj");
        if ( over_priceObj.value=="" ||isNaN(over_priceObj.value))
          over_priceObj.value = 0;
        if(obj.value=="")
          return;
        if(isNaN(obj.value))
        {
          alert(eval("showText"+type));
          obj.focus();
          return;
        }
        over_wageObj.value = formatSum(parseFloat(over_hourObj.value)*parseFloat(over_priceObj.value));
        break
      case 'night_hour':
         var obj = eval(type+"Obj");
         if (night_priceObj.value == "" || isNaN(night_priceObj.value))
          night_priceObj.value = 0;
        if(obj.value=="")
          return;
        if(isNaN(obj.value))
        {
          alert(eval("showText"+type));
          obj.focus();
          return;
        }
        night_wageObj.value=formatSum(parseFloat(night_hourObj.value)*parseFloat(night_priceObj.value));
        break
      case 'bounty':
         var obj = eval(type+"Obj");
        if(obj.value=="")
          return;
        if(isNaN(obj.value))
        {
          alert(eval("showText"+type));
          obj.focus();
        return;
        }
        break
      case 'amerce':
        var obj = eval(type+"Obj");
        if(obj.value=="")
          return;
        if(isNaN(obj.value))
        {
          alert(eval("showText"+type));
          obj.focus();
          return;
        }
        break
    }
    totHourWage += verify(work_wageObj);
    totHourWage += verify(over_wageObj);
    totHourWage += verify(night_wageObj);
    totHourWage -= verify(amerceObj);
    totHourWage += verify(bountyObj);
    hour_wageObj.value = formatSum(totHourWage);
    cal_tot(type);
  }
  function cal_tot(type)
  {
    var tmp0Obj, tmp1Obj, tmp2Obj, tmp3Obj, tmp4Obj,
        tmp5Obj, tmp6Obj, tmp7Obj, tmp8Obj, tmp9Obj;
    var tot0=0, tot1 = 0, tot2 = 0, tot3 = 0,
        tot4=0, tot5 = 0, tot6 = 0, tot7 = 0,
        tot8=0, tot9 = 0, tot10 = 0;
    for(i=0; i<<%=detailRows.length%>; i++)
    {
      switch (type){
      case 'work_hour':
        tmp0Obj = document.all['work_hour_'+i];
        tmp1Obj = document.all['work_wage_'+i];
        tot0 += verify(tmp0Obj)
        tot1 += verify(tmp1Obj);
        break
      case 'over_hour':
        tmp2Obj = document.all['over_hour_'+i];
        tmp3Obj = document.all['over_wage_'+i];
        tot2 += verify(tmp2Obj);
        tot3 += verify(tmp3Obj);
        break
      case 'night_hour':
        tmp4Obj = document.all['night_hour_'+i];
        tmp5Obj = document.all['night_wage_'+i];
        tot4 += verify(tmp4Obj);
        tot5 += verify(tmp5Obj);
        break
      case 'bounty':
        tmp6Obj = document.all['bounty_'+i];
        tot6 += verify(tmp6Obj);
        break
      case 'amerce':
        tmp7Obj = document.all['amerce_'+i];
        tot7 += verify(tmp7Obj);
        break
    }
    tmp8Obj = document.all['hour_wage_'+i];
    tot8 += verify(tmp8Obj);
    /*for (j=0;j<10;j++){
      if(eval("tmp"+j+"Obj")!=null && eval("tmp"+j+"Obj.value")!="" && !isNaN(eval("tmp"+j+"Obj.value")))
      {
         var objtmp = eval(eval("tot"+j));
         objtmp += parseFloat(eval("tmp"+j+"Obj.value"));
         alert(tot0);
      }
    }*/
    }
    switch (type){
      case 'work_hour':
        document.all['t_work_hour'].value = formatQty(tot0);
        document.all['t_work_wage'].value = formatQty(tot1);
      break
    case 'over_hour':
      document.all['t_over_hour'].value = formatQty(tot2);
      document.all['t_over_wage'].value = formatSum(tot3);
      break
    case 'night_hour':
      document.all['t_night_hour'].value = formatQty(tot4);
      document.all['t_night_wage'].value = formatSum(tot5);
      break
    case 'bounty':
      document.all['t_bounty'].value = formatQty(tot6);
      break
    case 'amerce':
      document.all['t_amerce'].value = formatQty(tot7);
    }
    //alert(document.all['t_work_hour'].value);
    document.all['t_hour_wage'].value = formatSum(tot8);
    document.all['tot_hour_wage'].value = formatSum(tot8);
  }
  function verify(obj)
  {
    if (isNaN(obj.value) || obj.value =="" )
      return 0;
    else
      return parseFloat(obj.value);
  }
  function Import_Produce_Task(frmName,srcVar,fieldVar,deptid, curID, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "Import_Porduce_Task";
      paraStr = "../produce_shengyu/draw_single_processTask.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&"+deptid+"&multi=1";
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
  }
    //删除
  function buttonEventD()
  {
   if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);
  }
  function buttonEventW()
  {
   Import_Produce_Task("form1", "srcVar=drawSingleProcessTask","fieldVar=rwdid", "deptid="+form1.deptid.value);
  }
  </script>
<%if(hourlyWagesBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</body>
</html>


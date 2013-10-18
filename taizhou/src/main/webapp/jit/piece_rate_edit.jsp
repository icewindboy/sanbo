<%--车间流转单--%>
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
  engine.erp.jit.PieceRate pieceRateBean = engine.erp.jit.PieceRate.getInstance(request);
  String pageCode = "piece_rate";
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
  location.href='piece_rate_top.jsp';
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
function select_person()
{
  if(form1.gzzid.value=='')
  {
  alert('请引用加工单');
  return;
  }
  personMultiSelect('form1','srcVar=personids','fieldVar=personid',form1.gzzid.value,"sumitForm(<%=pieceRateBean.PERSON_ADD%>,-1)");
}
function personMultiSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
 {
   var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
   var winName= "MultiProdSelector";
   paraStr = "../jit/multi_gzz_person.jsp?operate=0&multi=1&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&gzzid="+curID;
   if(methodName+'' != 'undefined')
     paraStr += "&method="+methodName;
   if(notin+'' != 'undefined')
     paraStr += "&notin="+notin;
   newWin =window.open(paraStr,winName,winopt);
   newWin.focus();
}
</script>
<%String retu = pieceRateBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String SC_PRODUCE_UNIT_STYLE = pieceRateBean.SC_PRODUCE_UNIT_STYLE;
  String SYS_PRODUCT_SPEC_PROP = pieceRateBean.SYS_PRODUCT_SPEC_PROP;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORKSHOP);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//存货信息
  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_SALE_ORDER_GOODS);//根据销售合同货物id得到合同编号
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp workGroupBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_WORK_GROUP);//通过工作组id得到工作组名称
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS);//通过jgdmxid得到加工单号(?任务单号?)
  engine.project.LookUp technicsNameBeanOption = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_OPTION_PROCEDURE);//工序 BEAN_TECHNICS_OPTION_PROCEDURE
  engine.project.LookUp technicsNameBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_NAME);//工序 BEAN_TECHNICS_NAME

  engine.project.LookUp CJLZDCODEBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SHOP_FLOW_DETALL);//车间流转的bean
  engine.project.LookUp SELFGAINCODEBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_RECEIVE_MATERIAL_DETALL);//自制收货单的bean
  engine.project.LookUp GYLXMX_Bean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_TECHNICS_PROCEDURE);//工艺路线明细

  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = pieceRateBean.getMaterTable();
  EngineDataSet list = pieceRateBean.getDetailTable();
  HtmlTableProducer masterProducer = pieceRateBean.masterProducer;
  HtmlTableProducer detailProducer = pieceRateBean.detailProducer;
  RowMap masterRow = pieceRateBean.getMasterRowinfo();
  RowMap[] detailRows= pieceRateBean.getDetailRowinfos();

  String zt=masterRow.get("zt");
  boolean isEnd = pieceRateBean.isReport || pieceRateBean.isApprove || (!pieceRateBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !pieceRateBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(pieceRateBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  String deptid = masterRow.get("deptid");//得到该单据的制单部门id
  String zdrid = masterRow.get("zdrid");//得到该单据的制单员id
  boolean isHasDeptLimit = loginBean.getUser().isDeptHandle(deptid, zdrid);//判断登陆员工是否有操作该制单人单据的权限

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  //String title = zt.equals("1") ? ("已审核") : (zt.equals("9") ? "审批中" : (zt.endsWith("2") ? "已生成物料需求" : (zt.equals("3") ? "已下达任务" : (zt.equals("8") ? "已完成" : "未审核"))));
  boolean isAdd = pieceRateBean.isDetailAdd;
  String cjlzdmxID = masterRow.get("cjlzdmxID");
  String receiveDetailID = masterRow.get("receiveDetailID");
  String tmpDjh = masterRow.get("djh"), tmpDjid = masterRow.get("djid");
  //如果有工作组,那么就人员数据取自工作组的BEAN,如果没有GZZID则取部门的人员数据
  String registeredPersonBean = masterRow.get("gzzid").equals("")?engine.project.SysConstant.BEAN_PERSON:engine.project.SysConstant.BEAN_GZZ_PERSON;
  boolean isGzzChoiced = masterRow.get("gzzid").equals("")?false:true;
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  CJLZDCODEBean.regData(ds, "cjlzdmxID");
  SELFGAINCODEBean.regData(ds, "receiveDetailID");
  if (!cjlzdmxID.equals("")||!receiveDetailID.equals(""))
  {
    RowMap  prodRowCjlz= CJLZDCODEBean.getLookupRow(masterRow.get("cjlzdmxID"));
    RowMap  prodRowSelfGain= SELFGAINCODEBean.getLookupRow(masterRow.get("receiveDetailID"));
    if (!cjlzdmxID.equals(""))
    {
      tmpDjh = prodRowCjlz.get("cjlzdh");
      tmpDjid = prodRowCjlz.get("cjlzdID");
    }
    else if (!receiveDetailID.equals(""))
    {
      tmpDjh = prodRowSelfGain.get("receiveCode");
      tmpDjid = prodRowSelfGain.get("receiveID");
    }
  }
  masterRow.put("djh", tmpDjh);
  masterRow.put("djid", tmpDjid);
  boolean isImport = !tmpDjid.equals("");//wage_produce
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

  <INPUT TYPE="HIDDEN" NAME="gylxmxid" value="<%=masterRow.get("gylxmxid")%>">
  <INPUT TYPE="HIDDEN" NAME="gxfdid" value="<%=masterRow.get("gxfdid")%>">
  <INPUT TYPE="HIDDEN" NAME="gylxid" value="<%=masterRow.get("gylxid")%>">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">计件工作量
            </td>
          </tr>
        </table>
             <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%
                  processBean.regData(ds,"jgdid");
                  deptBean.regData(ds,"deptid");
                  prodBean.regData(ds,"cpid");
                  technicsNameBean.regData(ds,"gymcid");
                  GYLXMX_Bean.regData(ds,"gylxmxid");
                  RowMap  prodRow = prodBean.getLookupRow(masterRow.get("cpid"));
                  workGroupBean.regConditionData(ds,"deptid");
                  String dmsxid = masterRow.get("dmsxid");
                  String sx = propertyBean.getLookupName(dmsxid);
                  String widths = BasePublicClass.parseEspecialString(sx, SYS_PRODUCT_SPEC_PROP, "()");//页面换算数量用
                  RowMap gylxrow = GYLXMX_Bean.getLookupRow(masterRow.get("gylxmxid"));
                  String deje = gylxrow.get("deje");
                  RowMap processRow = processBean.getLookupRow(masterRow.get("jgdid"));
                %>
                <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("piece_code").getFieldname()%>
                   <INPUT TYPE="HIDDEN" NAME="deje" value="<%=deje%>">
                  </td>
                  <td noWrap class="td">
                    <input type="text" style="width:100" class="edline" id="piece_code" name="piece_code" value='<%=masterRow.get("piece_code")%>'  maxlength='<%=ds.getColumn("piece_code").getPrecision()%>' onKeyDown="return getNextElement();" readonly>
                  </td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("piece_date").getFieldname()%>
                  </td>
                  <td  noWrap class="td">
                    <input type="text" name="piece_date" value='<%=masterRow.get("piece_date")%>' maxlength='10'  <%=edClass%> onChange="checkDate(this)" style="width:90" onKeyDown="return getNextElement();" <%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.piece_date);"></a>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle">车间</td>
                  <%String sumit0 = "sumitForm("+pieceRateBean.DEPTCHANGE+");";%>
                  <td  noWrap class="td">
                  <%if(true){%>
                   <input type='hidden'  name="deptid" value='<%=masterRow.get("deptid")%>' style='width:100' class='edline' readonly>
                   <input type='text' value='<%=deptBean.getLookupName(masterRow.get("deptid"))%>' style='width:100' class='edline' readonly>
                   <%}
                   //--if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:100' class='edline' readonly>");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:100" onSelect="<%=sumit0%>">
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("gzzid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "sumitForm("+pieceRateBean.GZZCHANGE+");";%>
                  <%if(isEnd){%>
                   <input type='hidden' name="gzzid" value='<%=masterRow.get("gzzid")%>' style='width:100' class='edline' readonly>
                   <input type='text' value='<%=workGroupBean.getLookupName(masterRow.get("gzzid"))%>' style='width:100' class='edline' readonly>
                   <%}else {%>
                   <pc:select name="gzzid" addNull="1" style="width:110" onSelect="<%=sumit%>">
                   <%=workGroupBean.getList(masterRow.get("gzzid"),"deptid",masterRow.get("deptid"))%> </pc:select>
                   <%}%>
                  </td>
               </tr>
               <tr>
                  <%--td noWrap class="tdTitle">相关单据号</td>
                  <td noWrap class="td">
                    <input type="text" id="djh" name="djh" value='<%=masterRow.get("djh")%>' maxlength='30' style="width:100" <%=edClass%> onChange="buttonEventW(false);" onKeyDown="return getNextElement();" <%=readonly%>>
                  </td--%>
                  <TD align="center" nowrap class="tdTitle">品名规格</TD>
                  <td class="td" nowrap colspan="3">
                   <input type="text" class="edline"  style="width:70" onKeyDown="return getNextElement();" id="cpbm" name="cpbm" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this)" readonly>
                   <input type="text" class="edline"  style="width:220" onKeyDown="return getNextElement();" id="product" name="product" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this)" readonly>
                   <input type="hidden" name="cpid" value="<%=masterRow.get("cpid")%>">
                   <input type="hidden" name="jgdid" value="<%=masterRow.get("jgdid")%>" onchange="sumitForm(<%=pieceRateBean.CPID_CHANGE%>)">
                   <input type="hidden" name="dmsxid" value="<%=dmsxid%>">
                   <%--img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=product&srcVar=cpbm','fieldVar=cpid&fieldVar=product&fieldVar=cpbm',form1.cpid.value)"--%>
                  </td>
                  <td noWrap class="tdTitle">加工单号</td>
                  <td noWrap class="td">
                    <input type="text" class="edline" id="jgdh" name="jgdh" value='<%=processRow.get("jgdh")%>'  style="width:100" <%=edClass%> onKeyDown="return getNextElement();" readonly>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">总产量</td>
                  <td noWrap class="td">
                     <input type="text" class="edline"  id="proc_num" name="proc_num" value='<%=masterRow.get("proc_num")%>' maxlength='<%=ds.getColumn("proc_num").getPrecision()%>' style="width:100"  onKeyDown="return getNextElement();" readonly>
                  </td>
                  <td noWrap class="tdTitle">单位</td>
                  <td noWrap class="td">
                     <input type="text" style="width:110" class="edline" onKeyDown="return getNextElement();" id="jldw" name="jldw" value='<%=prodRow.get("jldw")%>' readonly>
                     <input type="hidden" class=ednone onKeyDown="return getNextElement();" id="scydw" name="scydw" value='<%=prodRow.get("scydw")%>' readonly>
                  </td>
                   <td noWrap class="tdTitle">工序</td>
                   <td class="td" nowrap>
                    <input type="text" style="width:110" class="edline" onKeyDown="return getNextElement();" value='<%=technicsNameBean.getLookupName(masterRow.get("gymcid"))%>' readonly>
                   <input type="hidden"  id="gymcid" name="gymcid"  value='<%=masterRow.get("gymcid")%>' ></td>
               </tr>
                 <%/*打印用户自定义信息*/
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td rowspan="2"  nowrap width=10></td>
                        <td rowspan="2"  height='20' align="center" nowrap>
                       <%if(!isEnd){%>
                         <input type="hidden" name="personids" value="" onchange='sumitForm(<%=pieceRateBean.PERSON_ADD%>,-1)' >
                         <input name="image" class="img" type="image" title="新增(A)" onClick="select_person()" src="../images/add.gif" border="0">
                          <pc:shortcut key="a" script='select_person()'/>
                        <%}%>
                        </td>
                        <td  rowspan="2"  height='20' nowrap><%=detailProducer.getFieldInfo("personid").getFieldname()%></td>
                        <%--td height='20' nowrap><%=detailProducer.getFieldInfo("work_proc").getFieldname()%></td--%>
                        <td rowspan="2"  nowrap><%=detailProducer.getFieldInfo("piece_num").getFieldname()%></td>
                        <td rowspan="2"  height='20' nowrap>计价单价</td>
                        <td rowspan="2"  nowrap><%=detailProducer.getFieldInfo("piece_wage").getFieldname()%></td>
                          <td colspan="3" nowrap>奖 励 核 算</td>
                          <td colspan="3" nowrap>处 罚 核 算</td>
                          <td rowspan="2" nowrap>总工资</td>
                          <td rowspan="2" nowrap>备注</td>
                      </tr>
                        <tr class="tableTitle">
                          <td height="20" nowrap >补贴</td>
                          <td height="20" nowrap >超产奖</td>
                          <td height="20" nowrap>综合奖</td>
                          <td nowrap>报废</td>
                          <td nowrap>缺勤</td>
                          <td nowrap>综合罚</td>
                        </tr>
                    <%
                      //technicsNameBeanOption.regData(list,"work_proc");
                      personBean.regData(list,"personid");
                      BigDecimal t_piece_num = new BigDecimal(0), t_proc_num = new BigDecimal(0),
                                 t_piece_wage = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++){
                        detail = detailRows[i];
                        String piece_num = detail.get("piece_num");
                        String proc_num = detail.get("proc_num");
                        String piece_wage = detail.get("piece_wage");
                        String gylxmxid = detail.get("gylxmxid");

                        String gzzidtmp = masterRow.get("gzzid");
                        String deptidtmp = masterRow.get("deptid");
                        String tmpPersonLookUpKeyValue = isGzzChoiced?gzzidtmp:deptidtmp;
                        String tmpPersonLookUpKey = isGzzChoiced?"gzzid":"deptid";
                        if(pieceRateBean.isDouble(piece_num))
                          t_piece_num = t_piece_num.add(new BigDecimal(piece_num));

                        //if(pieceRateBean.isDouble(proc_num))
                        //  t_proc_num = t_proc_num.add(new BigDecimal(proc_num));

                        //if(pieceRateBean.isDouble(piece_wage))
                        //  t_piece_wage = t_piece_wage.add(new BigDecimal(piece_wage));

                        //取得任务单LOOKUP bean用来得到任务单号

                        //RowMap  prodRow = prodBean.getLookupRow(detail.get("cpid"));
                        String work_procName = "work_proc_"+i;
                        String work_proc = detail.get("work_proc");
                        String personVariable = "personid_"+i;
                        personBean.regData(list,"personid");
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
                          <%=personBean.getLookupName(detail.get("personid"))%>
                          <input type="hidden"  onKeyDown="return getNextElement();" id="<%=personVariable%>" name="<%=personVariable%>" value='<%=detail.get("personid")%>' readonly>
                        </td>
                        <td class="td" nowrap>
                         <input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="piece_num_<%=i%>" name="piece_num_<%=i%>" value='<%=detail.get("piece_num")%>' style="width:100%" maxlength='<%=list.getColumn("piece_num").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'num')" <%=readonly%>>
                          <input type="hidden" class="ednone_r" onKeyDown="return getNextElement();" id="proc_num_<%=i%>" name="proc_num_<%=i%>" value='<%=detail.get("proc_num")%>' style="width:100%" maxlength='<%=list.getColumn("proc_num").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'num')" readonly>
                        </td>
                        <td class="td" nowrap align="right">
                        <input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="piece_price_<%=i%>" name="piece_price_<%=i%>" value='<%=detail.get("piece_price")%>' style="width:100%" maxlength='<%=list.getColumn("piece_price").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'sum')"  <%=readonly%> >
                        </td>
                        <%
                        String m = detail.get("piece_wage");
                        String gylxmxid2 = detail.get("gylxmxid");
                        String m2 = detail.get("piece_wage");
                        %>
                        <td class="td" nowrap align="right"><input type="text" class="ednone_r"  onKeyDown="return getNextElement();" id="piece_wage_<%=i%>" name="piece_wage_<%=i%>" value='<%=piece_wage%>' style="width:100%" maxlength='<%=list.getColumn("piece_wage").getPrecision()%>'  readonly>
                        </td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="bt_<%=i%>" name="bt_<%=i%>" value='<%=detail.get("bt")%>' style="width:100%" maxlength='<%=list.getColumn("bt").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'sum')"  <%=readonly%>   ></td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="ccj_<%=i%>" name="ccj_<%=i%>" value='<%=detail.get("ccj")%>' style="width:100%" maxlength='<%=list.getColumn("ccj").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'sum')"  <%=readonly%>  ></td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="zhj_<%=i%>" name="zhj_<%=i%>" value='<%=detail.get("zhj")%>' style="width:100%" maxlength='<%=list.getColumn("zhj").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'sum')"  <%=readonly%>  ></td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="bf_<%=i%>" name="bf_<%=i%>" value='<%=detail.get("bf")%>' style="width:100%" maxlength='<%=list.getColumn("bf").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'sum')"  <%=readonly%>    ></td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="qq_<%=i%>" name="qq_<%=i%>" value='<%=detail.get("qq")%>' style="width:100%" maxlength='<%=list.getColumn("qq").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'sum')"  <%=readonly%>   ></td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="zhf_<%=i%>" name="zhf_<%=i%>" value='<%=detail.get("zhf")%>' style="width:100%" maxlength='<%=list.getColumn("zhf").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'sum')"  <%=readonly%>  ></td>
                        <td class="td" nowrap align="right"><input type="text" class="edline" onKeyDown="return getNextElement();" id="zgz_<%=i%>" name="zgz_<%=i%>" value='<%=detail.get("zgz")%>' style="width:100%" maxlength='<%=list.getColumn("zgz").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'sum')"    readonly ></td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="bz_<%=i%>" name="bz_<%=i%>" value='<%=detail.get("bz")%>' style="width:100%" maxlength='<%=list.getColumn("bz").getPrecision()%>'  onChange="sl_onchange(<%=i%>, 'sum')"  <%=readonly%>   ></td>
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
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>

                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_piece_num" name="t_piece_num" type="text" class="ednone_r" style="width:100%" value='<%=t_piece_num%>' readonly></td>
                        <td align="right" class="td"></td>
                        <td align="right" class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>

                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
              </table>
            </td>
          </tr>
                <tr>
                  <td  noWrap class="td">备注:<textarea name="bz" rows="3" onKeyDown="return getNextElement();" style="width:690" <%=(zt.equals("9")||zt.equals("8"))?"readonly":""%> ><%=masterRow.get("bz")%></textarea></td>
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
              <input type="hidden" name="djid" value="<%=masterRow.get("djid")%>" onchange="sumitForm(<%=pieceRateBean.IMPORT_OTHER_BILL%>)">
              <input name="btnback" class="button" type="button" value="引加工单(W)" style="width:100" onClick="buttonEventW();">
              <pc:shortcut key="w" script="buttonEventW();"/>
             <%--input name="button2" type="button" class="button" title="保存添加(ALT+N)" value="保存添加(N)" style="width:75" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" >
              <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/--%>
              <input name="btnback" type="button" class="button" title="保存(ALT+S)" value="保存(S)" onClick="sumitForm(<%=Operate.POST%>);" >
              <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%}%>
              <%--02.23 11:46 新增 新增显示下面这几个按钮的条件中加上isReport条件 yjg--%>
              <%if(isCanDelete && isHasDeptLimit && !pieceRateBean.isReport){%>
              <input name="button3" type="button" class="button" title="删除(ALT+D)" onClick="buttonEventD()" value="删除(D)">
              <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%if(!pieceRateBean.isApprove && !pieceRateBean.isReport){%>
              <input name="btnback" type="button" class="button" title="返回(ALT+C)" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
              <%--03.08 21:14 新增 新增关闭按钮提供给当此页面是被报表调用时使用. yjg--%>
              <%if(pieceRateBean.isReport){%>
              <input name="btnback" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)" onClick="window.close()" >
              <pc:shortcut key="t" script='<%="window.close()"%>'/>
              <%}%>
              <input type="button" class="button" value="打印(P)" onclick="location.href='../pub/pdfprint.jsp?code=piece_rate_edit_bill&operate=<%=Operate.PRINT_BILL%>&a$piecewage_id=<%=masterRow.get("piecewage_id")%>&src=../jit/piece_rate_edit.jsp'">
             <%
              String pr = "location.href='../pub/pdfprint.jsp?code=piece_rate_edit_bill&operate="+Operate.PRINT_BILL+"&a$piecewage_id="+masterRow.get("piecewage_id")+"&src=../jit/piece_rate_edit.jsp'";
             %>
             <pc:shortcut key="p" script="<%=pr%>" />
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">initDefaultTableRow('tableview1',1);
<%=pieceRateBean.adjustInputSize(new String[]{"work_proc", "piece_num", "proc_num","piece_price", "piece_wage", "personid"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
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
 function  master_sl_onchange(isBigUnit)
 {
    var piece_numObj = document.all['piece_num'];
    var proc_numObj = document.all['proc_num'];
    var scdwgsObj = document.all['scdwgs'];
    var widthObj = document.all['widths'];
    if (isBigUnit)
    {
       if (proc_numObj.value!="" && '<%=SC_PRODUCE_UNIT_STYLE%>'!='1')
         return;
       if (widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
         proc_numObj.value= piece_numObj.value;
       else
         proc_numObj.value =  formatQty(parseFloat(piece_numObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthObj.value));
    }
    else if (!isBigUnit)
    {
       if(piece_numObj.value!="" && '<%=SC_PRODUCE_UNIT_STYLE%>'!='1')//生产数量与数量是否强制转换
         return;
       if(widthObj.value=="" || widthObj.value=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
         piece_numObj.value= proc_numObj.value;
       else
         piece_numObj.value = formatQty(parseFloat(proc_numObj.value)*parseFloat(widthObj.value)/parseFloat(scdwgsObj.value));
    }
  }
  function sl_onchange(i, type)
  {
    var piece_numObj = document.all['piece_num_'+i];
    var piece_priceObj = document.all['piece_price_'+i];
    var piece_wageObj = document.all['piece_wage_'+i];

    var btObj = document.all['bt_'+i];
    var ccjObj = document.all['ccj_'+i];
    var zhjObj = document.all['zhj_'+i];
    var bfObj = document.all['bf_'+i];
    var qqObj = document.all['qq_'+i];
    var zhfObj = document.all['zhf_'+i];
    var zgzObj = document.all['zgz_'+i];

    piece_wageObj.value = formatSum(verify(piece_numObj)*verify(piece_priceObj));

    zgzObj.value = formatSum(verify(piece_wageObj)+verify(btObj)+verify(ccjObj)+verify(zhjObj)-verify(bfObj)-verify(qqObj)-verify(zhfObj));
    cal_tot('piece_wage');
  }
  function cal_tot(type)
  {
    var tmpObj;
    var tot=0;
    for(i=0; i<<%=detailRows.length%>; i++)
    {
      if(type == 'piece_num')
        tmpObj = document.all['piece_num_'+i];
      else if(type == 'proc_num')
        tmpObj = document.all['proc_num_'+i];
      else if(type == 'piece_wage')
        tmpObj = document.all['piece_wage_'+i];
      else
        return;
      if(tmpObj.value!="" && !isNaN(tmpObj.value))
        tot += parseFloat(tmpObj.value);
    }
    if(type == 'piece_num')
    {
      document.all['t_piece_num'].value = formatQty(tot);
      document.all['piece_num'].value = formatQty(tot);
    }
    if(type == 'proc_num')
    {
      document.all['t_proc_num'].value = formatQty(tot);
      document.all['proc_num'].value = formatQty(tot);
    }
    if(type == 'piece_wage')
      document.all['t_piece_wage'].value = formatSum(tot);
  }
  function verify(obj)
  {
    if (isNaN(obj.value) || obj.value =="" )
      return 0;
    else
      return parseFloat(obj.value);
  }
    //删除
  function buttonEventD()
  {
   if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);
  }
  function buttonEventW()
  {
   Import_Other_Bill("form1", "srcVar=jgdid","fieldVar=jgdid", 'sumitForm(<%=pieceRateBean.IMPORT_OTHER_BILL%>)');
  }
  function Import_Other_Bill(frmName,srcVar,fieldVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "Import_Porduce_Task";
      paraStr = "../jit/piece_select_bill.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
  }
  function Import_Other_Bill_Cpid_ACTION(frmName,srcVar,fieldVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "Import_Porduce_Task";
      paraStr = "../jit/select_other_bill_cpid.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
  }
  function getJjdjValue(i){
    getRowValue(document.all['prod_'+i], '<%=engine.project.SysConstant.BEAN_TECHNICS_OPTION_PROCEDURE_EXPAND%>', 'form1', 'srcVar=piece_price_'+i+'&srcVar=piece_type_'+i, 'fieldVar=deje&fieldVar=jjff',
      eval('form1.gylxmxid_'+i+'.value')+eval('form1.gylxid.value')
      +eval('form1.gxfdid.value')+eval('form1.v_work_proc_'+i+'.value'));
  }

  function Import_Other_Bill_Cpid()
  {
   Import_Other_Bill_Cpid_ACTION("form1", "srcVar=jgdid","fieldVar=jgdid", "deptid="+form1.deptid.value, "gzzid="+form1.gzzid.value, "djid="+form1.djid.value, 'sumitForm(<%=pieceRateBean.CPID_CHANGE%>)');
  }

  </script>
  <%if(pieceRateBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</body>
</html>


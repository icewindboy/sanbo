<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.store.xixing.B_InBalance inBalanceBean = engine.erp.store.xixing.B_InBalance.getInstance(request);
  String pageCode = "in_balance_list";
  //boolean hasApproveLimit = isApprove && loginBean.hasLimits(pageCode, op_approve);
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
  location.href='in_balance_list.jsp';
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_STORE%>', 'storeid', 'deptid', eval('form1.deptid.value'), '');
}
function deptchange2(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_STORE%>', 'kc_storeid', 'deptid', eval('form1.kc__deptid.value'), '');
}
</script>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<%String retu = inBalanceBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//结算方式
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp moveBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_MOVE);
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = inBalanceBean.getMaterTable();
  EngineDataSet list = inBalanceBean.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  HtmlTableProducer masterProducer = inBalanceBean.masterProducer;
  HtmlTableProducer detailProducer = inBalanceBean.detailProducer;
  RowMap masterRow = inBalanceBean.getMasterRowinfo();
  RowMap[] detailRows= inBalanceBean.getDetailRowinfos();
  String zt=masterRow.get("zt");
  //2004-6-8 14:57 为给明细数据集加入分页功能
  String count = String.valueOf(list.getRowCount());
  int iPage = 20;
  String pageSize = String.valueOf(iPage);
  if(inBalanceBean.isApprove)
  {
    personBean.regData(ds, "personid");
    personBean.regData(ds, "deptid");
    personBean.regData(ds, "kc__deptid");
  }
  boolean isEnd = inBalanceBean.isReport || inBalanceBean.isApprove || (!inBalanceBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !inBalanceBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(inBalanceBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("9") ? "审批中" : (zt.equals("2")?"记帐":"未审核"));
  String SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
%>
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
            <td class="activeVTab">内部流转单(<%=title%>)
            <%//当是新增的时候不显示出上一笔下一笔
              if (!inBalanceBean.masterIsAdd())
              {
                ds.goToInternalRow(inBalanceBean.getMasterRow());
                boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
                if (!isAtFirst)
              {%>
              <a href="#" title="到上一笔(ALT+Z)" onClick="sumitForm(<%=inBalanceBean.PRIOR%>)">&lt
              <pc:shortcut key='z' script='<%="sumitForm("+inBalanceBean.PRIOR+")"%>'/></a>
             <%}
               if (!isAtLast)
              {%>
              <a href="#" title="到上一笔(ALT+X)" onClick="sumitForm(<%=inBalanceBean.NEXT%>)">&gt
              <pc:shortcut key='x' script='<%="sumitForm("+inBalanceBean.NEXT+")"%>'/></a>
             <%}
               }
            %>
            </td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("nbjsdh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="nbjsdh" value='<%=masterRow.get("nbjsdh")%>' maxlength='<%=ds.getColumn("nbjsdh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="jsrq" value='<%=masterRow.get("jsrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.jsrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:110"  onSelect="deptchange();" ><%--2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg--%>
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                   </td>
                  <td noWrap class="tdTitle">收仓库</td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="storeid" addNull="1" style="width:110" ><%--2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg--%>
                      <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                    <%}%>
                   </td>
                </tr>
                <tr>
                  </tr>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bz").getFieldname()%></td>
                  <td noWrap class="td" colspan="3"><input type="text" name="bz" value='<%=masterRow.get("bz")%>' maxlength='<%=ds.getColumn("bz").getPrecision()%>' style="width:280" class="edbox" onKeyDown="return getNextElement();" <%=readonly%>></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("kc__deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("kc__deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="kc__deptid" addNull="1" style="width:110" onSelect="deptchange2()"><%--2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg--%>
                      <%=deptBean.getList(masterRow.get("kc__deptid"))%> </pc:select>
                    <%}%>
                   </td>
                  <td noWrap class="tdTitle">发仓库</td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("kc_storeid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="kc_storeid" addNull="1" style="width:110" ><%--2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg--%>
                      <%=storeBean.getList(masterRow.get("kc_storeid"))%> </pc:select>
                    <%}%>
                   </td>

                  </tr>
                <%/*打印用户自定义信息*/
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                %>
                <tr>
                  <td class="td" colspan="8" nowrap>
                   <pc:navigator id="move_store_listNav" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+inBalanceBean.TURNPAGE+")"%>' disable='<%=inBalanceBean.isRepeat.equals("1")?"1":"0"%>'/>
                 </td>
               </tr>
                <tr>
                  <td colspan="8" noWrap class="td">
                   <div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){%>
                          <input type="hidden" name="multiIdInput" value="" onchange="sumitForm(<%=Operate.DETAIL_ADD%>)">
                          <input name="image" class="img" type="image" title="新增(ALT+A)" onClick="Add()" src="../images/add_big.gif" border="0">
                           <pc:shortcut key="a" script='<%="Add()"%>'/>
                          <%}%>
                        </td>
                         <%
                        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-2;i++)
                          out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
                        %>
                        <%--03.09 12:06 修改 调整了表格中的规格属性, 数量,换算数量td的排列位置. 下面的具体显示这些值的jsp scripts也做了相应调整. yjg--%>
                        <td nowrap><%=detailProducer.getFieldInfo("dmsxid").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td nowrap>计量单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("hssl").getFieldname()%></td>
                        <td nowrap>换算单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("dj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("je").getFieldname()%></td>
                        <td nowrap>备注</td>
                      </tr>
                      <%
                       personBean.regConditionData(ds, "deptid");
                      // prodBean.regData(list,"cpid");
                      //propertyBean.regData(list,"dmsxid");
                      //buyOrderBean.regData(list,"hthwid");
                      int i=0;
                      RowMap detail = null;
                      int min = move_store_listNav.getRowMin(request);
                      int max = move_store_listNav.getRowMax(request);
                      //类中取得笔每一页的数据范围
                      inBalanceBean.min = min;
                      inBalanceBean.max = max > detailRows.length-1 ? detailRows.length-1 : max;
                      ArrayList cpidList = new ArrayList(max-min+1);
                      ArrayList dmsxidList = new ArrayList(max-min+1);
                      for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        String cpid = detail.get("cpid");
                        String dmsxid = detail.get("dmsxid");

                        cpidList.add(cpid);
                        dmsxidList.add(dmsxid);
                      }
                      prodBean.regData((String[])cpidList.toArray(new String[cpidList.size()]));
                      propertyBean.regData((String[])dmsxidList.toArray(new String[dmsxidList.size()]));//02.15 新增 新增注册dmsxid属性id因为不注册下面页面就会出错 yjg
                      BigDecimal t_sl = new BigDecimal(0),  t_hssl = new BigDecimal(0), t_je = new BigDecimal(0);;
                      list.goToRow(min);
                       for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(inBalanceBean.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String je = detail.get("je");
                        if(inBalanceBean.isDouble(je))
                          t_je = t_je.add(new BigDecimal(je));
                        String hssl = detail.get("hssl");
                        if(inBalanceBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String dckwName = "kwid_"+i;//调出库位
                        String dckwMcName = "kwmc_"+i;//调出库位
                        String drkwName = "kc__kwid_"+i;//调入库位
                        String drkwMcName = "kc__kwmc_"+i;//调入库位
                        String wzmxid = detail.get("wzmxid");
                        String dmsxidName = "dmsxid_"+i;
                    %>
                      <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                           <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                           <%if(!isEnd){%>
                           <img style='cursor:hand' title='单选物资' src='../images/select_prod.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=isprops_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops','','product_change(<%=i%>)')">
                           <%}if(!isEnd){%>
                        <%--   <input name="image" class="img" type="image" title="复制当前行" onClick="if(form1.cpid_<%=i%>.value==''){alert('请输入产品');return;}sumitForm(<%=inBalanceBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">--%>
                           <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                            <%}%>
                            </td>
                       <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                        String hsbl = prodRow.get("hsbl");
                        detail.put("hsbl",hsbl);
                        String isprops = prodRow.get("isprops");
                        %>
                        <td class="td" nowrap>
                        <input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                        <input type="text" <%=detailClass%>   style="width:100%" onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=readonly%>>
                        <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'>
                        <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                        <input type='hidden' id='isprops_<%=i%>' name='isprops_<%=i%>' value='<%=prodRow.get("isprops")%>'>
                        </td>
                        <td class="td" nowrap align="center">
                            <input type="text" <%=detailClass%> onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=readonly%>>
                        </td>
                        <%--02:14:50 新增一个规格属性 dmsxid td. 为新增一个规格属性而新增的.因为要它来和cpid, ph组合来确定只唯一性--%>
                        <td class="td" nowrap align="center">
                            <input type="HIDDEN"  id="dmsxid_<%=i%>"  name="dmsxid_<%=i%>"  value='<%=detail.get("dmsxid")%>'>
                            <input  <%=detailClass%>  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;} propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" <%=readonly%>>
                            <%if(!isEnd){%>
                            <img style='cursor:hand' src='../images/view.gif' title="规格属性" border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if('<%=isprops%>'=='0'){alert('该产品无规格属性!');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>', form1.cpid_<%=i%>.value)">
                            <img style='cursor:hand' src='../images/delete.gif' title="删除" border=0 onClick="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                            <%}%>
                        </td>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)"<%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone  onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" class=ednone  onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%> onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' onchange="dj_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="je_<%=i%>" name="je_<%=i%>" value='<%=detail.get("je")%>' maxlength='<%=list.getColumn("je").getPrecision()%>' onchange="dj_onchange(<%=i%>, true)" <%=readonly%>></td>
                        <td class="td" nowrap align="right"><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='<%=list.getColumn("bz").getPrecision()%>'<%=readonly%>></td>
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
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%--03.09 11:43 修改 去掉上面定义widths[]数组的几行代码,同时修改t_hssl, t_sl的宽度为width:100% yjg--%>
                        <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r" style="width:100%" value='<%=t_sl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td"><input id="t_je" name="t_je" type="text" class="ednone_r" style="width:100%" value='<%=t_je%>' readonly></td>
                        <td class="td">&nbsp;</td>
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
              <%if(!isEnd){%>
              <input name="button2" type="button" class="button" title="保存添加(ALT+N)" value="保存添加(N)" style="width:75" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
                <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="保存(ALT+S)" value="保存(S)"  onClick="sumitForm(<%=Operate.POST%>);">
                <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
               <%}%>
              <%if(isCanDelete && !inBalanceBean.isReport){%><input name="button3" type="button" class="button" title="删除(ALT+D)" value="删除(D)"  onClick="buttonEventD();" value=" 删除 ">
                <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%if(!inBalanceBean.isApprove && !inBalanceBean.isReport){%>
               <input name="btnback" type="button" class="button" title="返回(ALT+C)" value="返回(C)" onClick="backList();">
                <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
              <%--03.09 11:43 新增 新增关闭按钮提供给当此页面是被报表调用时使用. yjg--%>
              <%if(inBalanceBean.isReport){%>
                <input name="btnback" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)" onClick="window.close()">
                  <pc:shortcut key="t" script='<%="window.close()"%>'/>
              <%}%>
              <%--03.15 10:28 新增 新增打印单据按钮来把这张采购入库单页面上的内容打印出来. yjg--%>
       <%--      <input type="button" class="button" title="打印(ALT+P)" value="打印(P)"  onclick="buttonEventP()">
                  <pc:shortcut key="p" script='<%="buttonEventP()"%>'/> --%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
initDefaultTableRow('tableview1',1);
   <%=inBalanceBean.adjustInputSize(new String[]{"cpbm","product", "sl", "dj", "je","sxz","bz","hssl","jldw","hsdw"}, "form1", detailRows.length)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function compare()
    {
      if(storeid.value == kc__storeid.value){
        alert('调入仓库不能与调出仓库相等');
        return;
      }
    }
    function totalCalSl()
    {
      for (i=<%=min%>;i<=<%=max%>;i++)
      {
        sl_onchange(i, false);
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
      var hsblObj = document.all['hsbl_'+i];
      var hsblObj = document.all['truebl_'+i];
      var hsslObj = document.all['hssl_'+i];
      var obj = isBigUnit ? hsslObj : slObj;
      var showText = isBigUnit ? "输入的换算数量非法" : "输入的数量非法";
      var showText0 = "输入的调入数量非法";
      var changeObj = isBigUnit ? slObj : hsslObj;
      var djObj = document.all['dj_'+i];
      var jeObj = document.all['je_'+i];
      var bjfs=<%=bjfs%>;
      if(obj.value=="")
      return;
    if(!(changeObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1'))//是否强制转换
      {
      if(hsblObj.value!="" && !isNaN(hsblObj.value))
        changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
    }
    if(djObj.value!="")
    {
      if(isNaN(djObj.value)){
        djObj.value = "";
        alert("输入的加工单价非法");
        djObj.focus();
        return;
      }
      if('<%=bjfs%>'=="1")
        {
        jeObj.value = parseFloat(hsslObj.value)* parseFloat(djObj.value);
      }
      else{
        jeObj.value = parseFloat(slObj.value)* parseFloat(djObj.value);
      }
    }
    if ( isNaN( changeObj.value ) ) changeObj.value = "";
    cal_tot('sl');
    cal_tot('hssl');
    }
    function dj_onchange(i,isJeUnit)
    {
      var slObj = document.all['sl_'+i];
      var hsslObj = document.all['hssl_'+i];
      var obj = slObj;
      var showText =  "输入的数量非法";
      var showText2 = "输入的数量小于零";
      var djObj = document.all['dj_'+i];
      var jeObj = document.all['je_'+i];
  var bjfs=<%=bjfs%>;
    if(slObj.value=="")
      return;
    if(isNaN(djObj.value)){
      djObj.value = "";
      alert("输入的加工单价非法");
      djObj.focus();
      return;
    }
  if ('<%=bjfs%>'=="1"){
    if (isJeUnit)
      djObj.value = formatPrice(parseFloat(jeObj.value) / parseFloat(hsslObj.value));
    else
      jeObj.value = formatSum(parseFloat(hsslObj.value)* parseFloat(djObj.value));
  }
  else {
    if (isJeUnit)
      djObj.value = formatPrice(parseFloat(jeObj.value) / parseFloat(slObj.value));
    else
      jeObj.value = formatSum(parseFloat(slObj.value)* parseFloat(djObj.value));
  }
  cal_tot('je');
    }
    function cal_tot(type)
    {
      var tmpObj;
      var tot=0;
      for(i=<%=min%>;i<=<%=max%>;i++)
        {
        if(type == 'sl')
          tmpObj = document.all['sl_'+i];
        else if(type == 'hssl')
          tmpObj = document.all['hssl_'+i];
        else if(type == 'drsl')
          tmpObj = document.all['drsl_'+i];
        else  return;
        if(tmpObj.value!="" && !isNaN(tmpObj.value))
          tot += parseFloat(tmpObj.value);
      }
      if(type == 'sl')
        document.all['t_sl'].value = formatQty(tot);
      else if(type == 'hssl')
        document.all['t_hssl'].value = formatQty(tot);
      else if(type == 'drsl')
        document.all['t_je'].value = formatQty(tot);
      }
//删除
 function buttonEventD()
 {
   if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);
 }
 //打印
 function buttonEventP()
 {
   location.href='../pub/pdfprint.jsp?code=move_store_edit_bill&operate=<%=inBalanceBean.PRINT_BILL%>&a$sfdjid=<%=masterRow.get("sfdjid")%>&src=../store_xixing/move_store_edit.jsp';
 }
 //新增
      function Add()
      {
   sumitForm(<%=Operate.DETAIL_ADD%>)
     }
   function product_change(i){
     document.all['dmsxid_'+i].value="";
     document.all['sxz_'+i].value="";
   }
   function buttonEventW()
   {
     if(form1.storeid.value==''){alert('请选择仓库');return;}
    sumitForm(<%=inBalanceBean.DETAIL_ADD_BLANK%>);
      }
      //盘点机事件.
      function buttonEventE(isNew)//新旧盘点机调用
      {
        if(form1.storeid.value=='')
        {
      alert('请选择仓库');return;
    }
    //2004-06-09 11:28新旧盘点机区分 yjg
    transferScan(isNew);
  }
      <%--02.18 11:34 新增 新增调用盘点单的js函数. yjg--%>
  function transferScan(isNew)//调用盘点机
 {

   var scanValueObj = form1.scanValue;
       scanValueObj.value = scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "store_check_edit.jsp", "IT3CW32d.DLL")%>');//得到包含产品编码和批号的字符串
       if(scanValueObj.value=='')
         return;
       //2004-4-23 11:32 新旧盘点机区分 yjg
       if(isNew)
         sumitForm(<%=inBalanceBean.NEW_TRANSFERSCAN%>);
       else
         sumitForm(<%=inBalanceBean.TRANSFERSCAN%>);
  }
 //2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg
  /*
function deptChange()
{
  associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>','deptid', eval('form1.deptid.value'), '');
 }
  */
 function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsbl_'+i+'&srcVar=hsdw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsbl&fieldVar=hsdw', obj.value);
}
function productNameSelect(obj,i)
{
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsbl_'+i+'&srcVar=hsdw_'+i,
               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsbl&fieldVar=hsdw', obj.value);
}
    <%--2004-4-23 14:24新增 新增用于规格属性选择的js函数 yjg--%>
    function propertyNameSelect(obj,cpid,i)
    {
      PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
/**
 *@para frmName form表格.指示出srcVar等是在那个表单里的.在后面的操作里就是对这个表单里的元素进行操作的.
 *@para srcVar frmName里的元素.此值是可能是一组表单元素.主要是让后续的操作中得到这些元素,然后再给本页面中的srcVar赋值.如:把zl给sl_0
 *@para whichCall 指出调用此js函数是的谁.在本页面下.图标调用和批号输入框两种.
 *@para i 指出数据行.主要是要组成类似sl_i这样的srcVar
 *@para isAdjustKwid 是否须要调整kwid.因为在销售出库单中当wzsxid不为空的时候就不允许修改kwid.须要调整则为true.
 *@para methodName 本页面类的方法.
 */
function BatchMultiSelect(frmName, srcVar, whichCall, i, isAdjustKwid, methodName,notin)
{
  //whichCall:参数表示调用配批号是由左边的图标事件触发的还是批号输入框事件触发的.这两个事件各自己对应的不同的类处理
  //输入框事件whichCall:true及isMultiSelect=0.图标事件:false及isMultiSelect=1.
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
  var winName= "BatchSelector";
  //if ( !isAdjustKwid) alert("kk");
  var kwSrcVarCondition = isAdjustKwid?"&srcVarTwo=kwid_"+i:"";
  var kwFieldVarCondition = isAdjustKwid?"&fieldVar=kwid":"";
  paraStr = "../store/select_batch.jsp?operate="+(whichCall?58:0) + "&srcFrm="+frmName+"&"+srcVar+(whichCall?"&isMultiSelect=0":"&isMultiSelect=1")
          + "&srcVarTwo=sl_"+i
          + "&srcVarTwo=ph_"+i
          + kwSrcVarCondition+"&srcVarTwo=dmsxid_"+i+"&srcVarTwo=sxz_"+i+"&srcVarTwo=wzmxid_"+i
          + "&fieldVar=zl&fieldVar=ph"
          + kwFieldVarCondition+"&fieldVar=dmsxid&fieldVar=sxz&fieldVar=wzmxid";
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  var iframeObj = document.all['prod'];
  iframeObj.src = paraStr;
  //newWin =window.open(paraStr,winName,winopt);
  //newWin.focus();
}
function batchSelectOpen(){openSelectUrl("../store/select_batch.jsp", "BatchSelector", winopt2);}
  function showDetail(masterRow){
    selectRow();
  }
</script>
<%if(inBalanceBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
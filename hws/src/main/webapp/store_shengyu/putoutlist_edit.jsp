<%--外贸出库单从表--%>
<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
  String op_print  = "op_print";
%><%
  engine.erp.store.shengyu.B_OutSaleStore b_OutSaleStore = engine.erp.store.shengyu.B_OutSaleStore.getInstance(request);
  b_OutSaleStore.djxz = 13;
  String pageCode = "putoutlist";
%>
<html>
<head>
<title></title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
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
  location.href='putoutlist.jsp';
}
function corpCodeSelect(obj)
{
  CustomerCodeChange('2',document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=b_OutSaleStore.ONCHANGE%>)');
}
function corpNameSelect(obj)
{
     CustomerNameChange('2', document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc', obj.value, 'sumitForm(<%=b_OutSaleStore.ONCHANGE%>)');
}
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                         'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
function ladingprint(sfdjid)
{
    location.href='../pub/pdfprint.jsp?operate=<%=Operate.PRINT_PRECISION%>&code=yuz_outstore_print&sfdjid='+sfdjid;
}
</script>
<%String retu = b_OutSaleStore.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp saleOrderGoodsBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.VW_BEAN_XPORT_LADING_DETAIL);//引入外贸提单
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp balanceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  engine.project.LookUp saleOutBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_OUT_SALE);//单据类型
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_OutSaleStore.getMaterTable();
  EngineDataSet list = b_OutSaleStore.getDetailTable();
  String bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");//得到登陆用户报价方式的系统参数
  boolean isHsbj = bjfs.equals("1");//如果等于1就以换算单位报价
  HtmlTableProducer masterProducer = b_OutSaleStore.masterProducer;
  HtmlTableProducer detailProducer = b_OutSaleStore.detailProducer;
  RowMap masterRow = b_OutSaleStore.getMasterRowinfo();
  RowMap[] detailRows = b_OutSaleStore.getDetailRowinfos();
  String tempJustName = "";
  String zt=masterRow.get("zt");
  //2004-5-2 16:43 为给明细数据集加入分页功能
  String count = String.valueOf(list.getRowCount());
  int iPage = 20;
  String pageSize = String.valueOf(iPage);

  if(b_OutSaleStore.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  boolean isEnd = b_OutSaleStore.isReport || b_OutSaleStore.isApprove || (!b_OutSaleStore.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !b_OutSaleStore.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);//没有结束,在修改状态,并有删除权限
  isEnd = isEnd || !(b_OutSaleStore.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("9") ? "审批中" : (zt.equals("2")?"记帐":"未审核"));
  String SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  boolean isPriorNext = false;//saleOutStoreBean.isApprove?false:!saleOutStoreBean.masterIsAdd();

  boolean isCanPrint = loginBean.hasLimits(pageCode, op_print) ; //是否有权限打印,只有制单人才有打印权限


%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<jsp:include page="../pub/scan_bar.jsp" flush="true"/>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<iframe id="iprint" style="display:none"></iframe>
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
            <td class="activeVTab">外贸出库单(<%=title%>)
            <%//当是新增的时候不显示出上一笔下一笔
              if (isPriorNext)//!b_OutSaleStore.masterIsAdd()
              {
                ds.goToInternalRow(b_OutSaleStore.getMasterRow());
                boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
              %>
              <%if (!isAtFirst)
              {%>
              <a href="#" title="到上一笔(ALT+Z)" onClick="sumitForm(<%=b_OutSaleStore.PRIOR%>)">&lt</a>
              <pc:shortcut key='z' script='<%="sumitForm("+b_OutSaleStore.PRIOR+")"%>'/>
             <%}%>
               <%if (!isAtLast)
              {%>
              <a href="#" title="到下一笔(ALT+X)" onClick="sumitForm(<%=b_OutSaleStore.NEXT%>)">&gt</a>
              <pc:shortcut key='x' script='<%="sumitForm("+b_OutSaleStore.NEXT+")"%>'/>
             <%}
              }%>
   </td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%
                corpBean.regData(ds,"dwtxid");String jsr=masterRow.get("jsr");
                //saleOutBean.regConditionData(ds,"sfdjlbid");
                //storeBean.regData(ds, "storeid");
                  if(!isEnd)
                        storeAreaBean.regConditionData(ds, "storeid");
                 %>
                  <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjdh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sfdjdh" value='<%=masterRow.get("sfdjdh")%>' maxlength='<%=ds.getColumn("sfdjdh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfrq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="sfrq" value='<%=masterRow.get("sfrq")%>' maxlength='10' style="width:85" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.sfrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%String sumit = "if(form1.storeid.value!='"+masterRow.get("storeid")+"')sumitForm("+b_OutSaleStore.ONCHANGE+")";%>
                    <%if(isEnd) out.print("<input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="storeid" addNull="0" onSelect="<%=sumit%>" style="width:110" >
                      <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select  name="deptid" addNull="1" style="width:110" onSelect="deptChange()"><%--2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg--%>
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                   </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwtxid").getFieldname()%></td>
                   <%
                     RowMap corpRow = corpBean.getLookupRow(masterRow.get("dwtxid"));
                     RowMap sfdjlbRow = saleOutBean.getLookupRow(masterRow.get("sfdjlbid"));
                     String srlx = sfdjlbRow.get("srlx");
                     srlx = srlx.equals("")?"1":srlx;
                     sfdjlbRow.put("srlx", srlx);
                   %>
                  <td  noWrap class="td" colspan="3"><input type="text" name="dwdm" value='<%=corpRow.get("dwdm")%>' style="width:60" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpCodeSelect(this);" <%=readonly%>>
                  <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <%--02.18 15:47 修改 购货单位 选择改成 可输入的文本框. 同时加上响应在此文本框内回车自动弹出进行模糊查询窗口的事件 yjg--%>
                  <input type="text" name="dwmc" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:170" <%=edClass%> onKeyDown="return getNextElement();" onchange="corpNameSelect(this);" <%=readonly%>>
                  <%if(!isEnd){%>
                  <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=b_OutSaleStore.ONCHANGE%>)');"><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';sumitForm(<%=b_OutSaleStore.ONCHANGE%>)">
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("khlx").getFieldname()%></td>
                  <td noWrap class="td">
                  <%String khlx=masterRow.get("khlx");%>
                  <%if(isEnd) out.print("<input type='text' value='"+masterRow.get("khlx")+"' style='width:50' class='edline' readonly>");
                  else {%>
                  <pc:select name="khlx" style="width:50" value='<%=khlx%>'>
                  <pc:option value='A'>A</pc:option>
                  <pc:option value='C'>C</pc:option>
                  </pc:select>
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsr").getFieldname()%></td>
                  <td class="td" nowrap>
                  <%if(isEnd){%> <input type="text" name="jsr" value='<%=masterRow.get("jsr")%>' maxlength='<%=ds.getColumn("jsr").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  <%}else {%><%--2004-4-23 21:36 修改 销售出库单经手人默认为空 yjg--%>
                  <pc:select combox="1" className="edFocused" name="jsr" value="<%=jsr%>" style="width:110">
                  <%=personBean.getList()%></pc:select>
                  <%}%>
                  </td>
                  </tr>
                  <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsfsid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+balanceBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:110' class='edline' readonly>");
                    else {%>
                    <pc:select name="jsfsid" addNull="1" style="width:110">
                      <%=balanceBean.getList(masterRow.get("jsfsid"))%> </pc:select>
                    <%}%>
                  </td>
                   <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("sfdjlbid").getFieldname()%></td>
                   <td  noWrap class="td">
                    <%if(isEnd)
                     {
                      out.print("<input type='text' value='"+sfdjlbRow.get("lbmc")+"' style='width:110' class='edline' readonly>");
                      out.print("<input type='hidden' name='srlx' value="+sfdjlbRow.get("srlx")+">");
                     }
                    else {
                      String submit = "sumitForm("+b_OutSaleStore.ONCHANGE+")";
                      String aaa=masterRow.get("sfdjlbid");
                    %>
                    <pc:select name="sfdjlbid" addNull="0" style="width:110" onSelect="<%=submit%>">
                      <%=saleOutBean.getList(masterRow.get("sfdjlbid"))%> </pc:select>
                     <input type="hidden" name="srlx" value='<%=sfdjlbRow.get("srlx")%>'>
                    <%}%>
                  </td>

                  </tr>
                <%/*打印用户自定义信息*/
                int width = (detailRows.length > 4 ? detailRows.length : 4)*23 + 66;
                %>
                <tr>
                  <td class="td" colspan="8" nowrap>
                   <pc:navigator id="putoutlistNav" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+b_OutSaleStore.TURNPAGE+")"%>' disable='<%=b_OutSaleStore.isRepeat.equals("1")?"1":"0"%>'/>
                 </td>
               </tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){%>
                          <input type="hidden" name="importLadingGoods" value="引入提单货物" onChange="sumitForm(<%=b_OutSaleStore.DETAIL_LADINGGOODS_ADD%>)">
                          <input type="hidden" name="signleimportLadingGoods" value="引入提单货物" onChange="sumitForm(<%=b_OutSaleStore.SINGLELADDING_DETAIL_LADINGGOODS_ADD%>)">
                          <input name="image" class="img" type="image" title="引入提单货物(ALT+A)" onClick="buttonEventA()" src="../images/add_big.gif" border="0">
                           <pc:shortcut key="a" script='buttonEventA()'/>
                           <input class="edFocused_r"  name="tCopyNumber" value="<%=request.getParameter("tCopyNumber")==null?"1":request.getParameter("tCopyNumber")%>" title="拷贝(ALT+A)" size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                          <%}%>
                        </td>
                        <td nowrap>提单编号</td>
                        <td nowrap>单号</td>
                         <%
                        for (int i=0;i<detailProducer.getFieldInfo("cpid").getShowFieldNames().length-2;i++)
                          out.println("<td nowrap>"+detailProducer.getFieldInfo("cpid").getShowFieldName(i)+"<//td>");
                        %>
                         <td nowrap>规格属性</td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td nowrap>计量单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("ph").getFieldname()%></td>

                        <%
                          if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
                          {
                        %>
                           <td nowrap>单价</td>
                           <td nowrap>金额</td>
                        <%
                          }
                        %>
                        <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                      </tr>
                    <%
                      personBean.regConditionData(ds, "deptid");
                      RowMap detail = null;
                      int i=0;
                      int min = putoutlistNav.getRowMin(request);
                      int max = putoutlistNav.getRowMax(request);
                      //类中取得笔每一页的数据范围
                      b_OutSaleStore.min = min;
                      b_OutSaleStore.max = max > detailRows.length-1 ? detailRows.length-1 : max;
                      ArrayList cpidList = new ArrayList(max-min+1);
                      ArrayList dmsxidList = new ArrayList(max-min+1);
                      ArrayList wjidlist = new ArrayList(max-min+1);
                      for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        String cpid = detail.get("cpid");
                        String dmsxid = detail.get("dmsxid");
                        String wjid = detail.get("wjid");

                        cpidList.add(cpid);
                        wjidlist.add(wjid);
                        dmsxidList.add(dmsxid);
                      }
                      prodBean.regData((String[])cpidList.toArray(new String[cpidList.size()]));
                      propertyBean.regData((String[])dmsxidList.toArray(new String[dmsxidList.size()]));//02.15 新增 新增注册dmsxid属性id因为不注册下面页面就会出错 yjg
                      saleOrderGoodsBean.regData((String[])wjidlist.toArray(new String[wjidlist.size()]));
                      //buyOrderBean.regData(list,"hthwid");
                      BigDecimal t_sl = new BigDecimal(0), t_je = new BigDecimal(0), t_hssl = new BigDecimal(0);
                      list.goToRow(min);
                     //2004-5-2 16:43 修改 将原来的i<detailRows.length修改成现在的i<=max && i<list.getRowCount();
                      for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        String sl = detail.get("sl");
                        if(b_OutSaleStore.isDouble(sl))
                          t_sl = t_sl.add(new BigDecimal(sl));
                        String hssl = detail.get("hssl");
                        if(b_OutSaleStore.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));
                        String je = detail.get("je");
                        if(b_OutSaleStore.isDouble(je))
                          t_je = t_je.add(new BigDecimal(je));
                        String wzmxid = detail.get("wzmxid");
                        String kwName = "kwid_"+i;
                         String kwMcName = "kwmc_"+i;
                        String kwid = detail.get("kwid");
                        String cpid = detail.get("cpid");
                    %>
                      <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                         <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd){%>
                          <input type="hidden" name="mutibatch_<%=i%>" value="" onchange="sumitForm(<%=b_OutSaleStore.SELECTBATCH%>,<%=i%>)">
                          <input name="image" class="img" type="image" title="配批号" onClick="BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+<%=detail.get("cpid")%>+'&storeid='+form1.storeid.value<%=detail.get("dmsxid").equals("")?"":"+'&sxz='+"+"'" + propertyBean.getLookupName(detail.get("dmsxid")) + "'"%>)" src="../images/select_prod.gif" border="0">
                          <input name="image" class="img" type="image" title="复制当前行" onClick="sumitForm(<%=b_OutSaleStore.COPYROW%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <input name="image" class="img" type="image" title="删除" onClick="sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%>
                        </td><%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                        String hsbl = prodRow.get("hsbl");
                        String isprop = prodRow.get("isprops");
                        detail.put("hsbl",hsbl);%>
                        <%RowMap saleOrderGoodsRow=saleOrderGoodsBean.getLookupRow(detail.get("wjid"));%>
                        <td class="td" nowrap><%=saleOrderGoodsRow.get("tdbh")%></td>
                        <td class="td" nowrap><input type="text" class="ednone"  onKeyDown="return getNextElement();" id="dh_<%=i%>" name="dh_<%=i%>" value='<%=detail.get("dh")%>' maxlength='<%=list.getColumn("dh").getPrecision()%>' readonly></td>
                        <td class="td" nowrap><%=prodRow.get("cpbm")%>
                         <input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                         <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'>
                         <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                        </td>
                        <td class="td" nowrap><%=prodRow.get("product")%>

                        </td>
                        <td class="td" nowrap>
                        <input <%=detailClass_r%> name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" onKeyDown="return getNextElement();" <%=readonly%>>
                        <input type="hidden" id="dmsxid_<%=i%>" name="dmsxid_<%=i%>" value="<%=detail.get("dmsxid")%>">
                        <%if(!isEnd){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if(form1.isprops_<%=i%>=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>',form1.cpid_<%=i%>.value,'')">
                        <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                        <%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onchange="sl_onchange(<%=i%>, false)" <%=readonly%>></td>
                        <td class="td" nowrap><%=prodRow.get("jldw")%>
                          <input type="hidden" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)" <%=readonly%>>
                          <input type="hidden" id="wzmxid_<%=i%>" name="wzmxid_<%=i%>" value="<%=detail.get("wzmxid")%>">
                        </td>
                         <td class="td" nowrap> <input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="ph_<%=i%>" name="ph_<%=i%>" value='<%=detail.get("ph")%>' style="width:100%"  onchange="BatchMultiSelect('form1','srcVar=mutibatch_<%=i%>&cpid='+<%=detail.get("cpid")%>+'&storeid='+form1.storeid.value+'&row='+<%=i%><%=detail.get("dmsxid").equals("")?"":"+'&sxz='+"+"'" + propertyBean.getLookupName(detail.get("dmsxid")) + "'"%><%="+'&ph='+"+"form1.ph_"+i+".value"%>, true, <%=i%>, <%=!wzmxid.equals("")?false:true%>, 'methodName=sl_onchange(<%=i%>, false)')" <%=readonly%>>
                         </td>
                        <%
                          if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
                          {
                        %>
                           <td class="td" nowrap>
                              <input type="text" class="ednone"  onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' onchange="dj_onchange(<%=i%>)" maxlength='<%=list.getColumn("dj").getPrecision()%>' readonly>
                           </td>
                           <td class="td" nowrap align="right">
                             <input type="text" class="ednone_r"  id="je_<%=i%>" name="je_<%=i%>" value='<%=detail.get("je")%>' readonly>
                           </td>
                        <%
                          }
                        %>
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

                       <%
                          if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
                          {
                        %>
                          <td class="td"></td>
                          <td class="td">&nbsp;</td>
                        <%
                          }
                        %>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>小计</td>

                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>

                        <td class="td">&nbsp;</td>
                         <td align="right" class="td"><input id="t_sl" name="t_sl" type="text" class="ednone_r"    style="width:100%" value='<%=t_sl%>' readonly></td>
                         <td class="td">&nbsp;</td>
                         <td align="right" class="td">&nbsp;</td>
                         <td align="right" class="td"><input type="hidden" id="t_hssl" name="t_hssl" type="text" class="ednone_r"  style="width:100%" value='<%=t_hssl%>' readonly></td>
                        <%
                          if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
                          {
                        %>
                          <td class="td"></td>
                          <td align="right" class="td">
                            <input id="t_je" name="t_je" type="text" class="ednone_r" value='<%=t_je%>'  style="width:100%" readonly>
                          </td>
                        <%
                          }
                        %>

                      </tr>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>

                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                       <td class="td">&nbsp;</td>
                        <td align="right" class="td"><%=b_OutSaleStore.masterIsAdd()&&!b_OutSaleStore.isApprove?"0":ds.getValue("zsl")%></td>
                        <td class="td">&nbsp;</td>
                        <td align="right" class="td">&nbsp;</td>
                        <td align="right" class="td"><%--=b_OutSaleStore.masterIsAdd()&&!b_OutSaleStore.isApprove?"0":b_OutSaleStore.totalDeatilHssl--%></td>
                        <%
                          if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
                          {
                        %>
                          <td class="td"></td>
                          <td align="right" class="td"></td>
                        <%
                          }
                        %>
                      </tr>
                    </table></div>
                    </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle">备注</td><%--其他信息--%>
                  <td colspan="7" noWrap class="td"><textarea name="bz" rows="3" onKeyDown="return getNextElement();" style="width:690" <%=(zt.equals("9")||zt.equals("8"))?"readonly":""%> ><%=masterRow.get("bz")%></textarea></td>
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
              <%if(!isEnd){
                  String importTitle = srlx.equals("1")?"引外贸提单(Q)":"引外贸退货单(Q)";
              %>
              <input type="hidden" name="singleSelectLadding" value=""><%--02.16 修改 将 button显示的名称改为 引入提货单 yjg--%>
              <input name="btnback" type="button" class="button"  title="<%=importTitle%>" value="<%=importTitle%>" style="width:100" onClick="buttonEventQ()">
              <pc:shortcut key="q" script='<%="buttonEventQ()"%>'/>

              <input name="button2" type="button" class="button" title="保存添加(ALT+N)" value="保存添加(N)" style="width:75" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
              <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="保存(ALT+S)" value="保存(S)"  onClick="sumitForm(<%=Operate.POST%>);">
              <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
             <%}%>
              <%if(isCanDelete && !b_OutSaleStore.isReport){%><input name="button3" type="button" class="button" title="删除(ALT+D)" value="删除(D)" onClick="buttonEventD()">
              <pc:shortcut key="d" script="buttonEventD()"/>
             <%}%>
             <%--2004-3-27 11:32 新增 新增删除数量为空行的按钮 yjg--%>
             <%if(!isEnd && !b_OutSaleStore.isReport){%>
             <input name="button3" type="button" class="button" title="删除数量为空行(ALT+R)" value="删除空行(R)" style="width:75" onClick="buttonEventR()">
             <pc:shortcut key="r" script="buttonEventR()"/>
              <%}%>

              <%if(isCanPrint){%>
             <input name="btnback11"  style="width:50"  type="button" class="button" onClick="ladingprint('<%=masterRow.get("sfdjid")%>');" value="打印(P)">
              <%}%>

              <%if(!b_OutSaleStore.isApprove && !b_OutSaleStore.isReport){%><input name="btnback" type="button" class="button" title="返回(ALT+C)" value="返回(C)"  onClick="backList();">
              <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
                <%if(b_OutSaleStore.isReport){%><input name="btnback" type="button" class="button" title="关闭(ALT+T)" value="关闭(T)"  onClick="window.close()">
                <pc:shortcut key="t" script='<%="window.close()"%>'/>
                <%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
  initDefaultTableRow('tableview1',1);
  <%--02.27 17:54 新增 新增 下面这行jsp代码用来调整网页中的明细资料部分的input的长度. yjg--%>
  <%=b_OutSaleStore.adjustInputSize(
    b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1")? new String[]{"hssl","sl", "bz", "je", "dj", "ph"}:new String[]{"hssl","sl", "bz", "ph"},
   "form1", (b_OutSaleStore.max-min+1), min)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
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
      //var hsblObj = document.all['hsbl_'+i];
      var hsblObj = document.all['truebl_'+i];
      var hsslObj = document.all['hssl_'+i];
      var djObj = document.all['dj_'+i];
      var jeObj = document.all['je_'+i];
      var sxz = document.all['sxz_'+i];
      var obj = isBigUnit ? hsslObj : slObj;
      var showText = isBigUnit ? "输入的换算数量非法" : "输入的数量非法";
      var changeObj = isBigUnit ? slObj : hsslObj;
      <%if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
        {
     %>
       if (!isNaN(slObj.value)&&!isNaN(djObj.value))
       {
         jeObj.value = formatSum(slObj.value*djObj.value);
       }
    <%}%>
      if(obj.value=="")
        return;
      if(isNaN(obj.value))
      {
        alert(showText);
        obj.focus();
        return;
      }
      //2004-3-30 15:27 修改 javascript修改页面上可以输入数值的数量,换算数量栏位,
      //数量,换算数量互动关系仅在如此情况下有效:当修改其中的一个如果另一个是空的话则跟随改变
      //除了不算的情况下其它的情况下都要去算.
     if(!(changeObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1'))//是否强制转换.1=强制换算,0=仅空值时换算
       {
       if(hsblObj.value!="" && !isNaN(hsblObj.value))
       {
         /*alert(hsblObj.value);
         alert(slObj.value);
         alert(hsslObj.value);
         alert(sxz.value);
         */
         changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
         //unitConvert(document.all['prod'], "form1", "srcVar=sl_"+i+"&srcVar=hssl_"+i, "exp=1122/{宽}&exp=11*22/{宽}", sxz.value, null);
       }
       //changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
       //else
       //  changeObj.value ="";

     }
     cal_tot('sl');
     cal_tot('hssl');
    <%if (b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("1"))
      {
   %>
     cal_tot('je');
   <%}%>

     }
     function dj_onchange(i)
     {
       var slObj = document.all['sl_'+i];
       var djObj = document.all['dj_'+i];
       var jeObj = document.all['je_'+i];
       if(djObj.value=="")
         return;
       if(isNaN(djObj.value))
       {
         alert("输入的单价非法");
         djObj.focus();
         return;
       }
       if (!isNaN(slObj.value)&&!isNaN(djObj.value))
       {
         jeObj.value = formatSum(slObj.value*djObj.value);
       }
       cal_tot('je');
     }
     function cal_tot(type)
     {
       var tmpObj;
       var tot=0;
    for(i=<%=min%>; i<=<%=max%>;i++){
      if(type == 'sl')
        tmpObj = document.all['sl_'+i];
      else if(type == 'hssl')
        tmpObj = document.all['hssl_'+i];
      else if  (type=='je')
        tmpObj = document.all['je_'+i];
      else
        return;
      if(tmpObj.value!="" && !isNaN(tmpObj.value))
        tot += parseFloat(tmpObj.value);
    }
    if(type == 'sl')
      document.all['t_sl'].value = formatQty(tot);
    else if(type == 'hssl')
      document.all['t_hssl'].value = formatQty(tot);
    else if(type == 'je')
      document.all['t_je'].value = formatSum(tot);
   }
function LadingGoodsMultiSelect(frmName, srcVar, methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
  var winName= "GoodsProdSelector";
  paraStr = "../store/import_saleoutlading_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&djlx=<%=srlx%>";
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
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
function LaddingSingleSelect(frmName,srcVar,fieldVar,curID,storeid,methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
  var winName= "LaddingSingleSelect";
  paraStr = "../store/laddingout_single_list.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&multi=true"
          +"&dwtxid="+curID+"&storeid="+storeid+"&djlx=<%=srlx%>";
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  openSelectUrl(paraStr, "LaddingSingleSelect", winopt2);
  //newWin =window.open(paraStr,winName,winopt);
  //newWin.focus();

}
function certifyCardPrint(oper, row)
{
  var paraStr = "../store_shengyu/barcode_print.jsp?operate="+oper+"&rownum="+row;
  //openSelectUrl(paraStr, "CertifyCardPrint", winopt2);
  window.open(paraStr, "CertifyCardPrint");
  //document.all.iprint.src=paraStr;
}
function transferScan(isNew)//调用盘点机
{
  //alert(scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "contract_instore_edit", "IT3CW32d.DLL")%>'));
  var scanValueObj = form1.scanValue;
   scanValueObj.value = scaner.Read('<%=engine.util.StringUtils.replace(curUrl, "putoutlist_edit.jsp", "IT3CW32d.DLL")%>');//得到包含产品编码和批号的字符串
     if(scanValueObj.value=='')
       return;
     //2004-4-23 11:32 新旧盘点机区分 yjg
   if(isNew)
     sumitForm(<%=b_OutSaleStore.NEW_TRANSFERSCAN%>);
   else
     sumitForm(<%=b_OutSaleStore.TRANSFERSCAN%>);
}
//引入提货单
function buttonEventQ()
{
  if (form1.srlx.value=="" ){
    alert("请先指定单据类别");
    return;
  }
  LaddingSingleSelect('form1','srcVar=singleSelectLadding&srcVar=signleimportLadingGoods','fieldVar=tdid',form1.dwtxid.value,form1.storeid.value,'sumitForm(<%=b_OutSaleStore.SINGLE_LADDING%>)');
}
     //盘点机事件.
  function buttonEventE(isNew)//新旧盘点机调用
  {
    if(form1.storeid.value=='')
    {
      alert('请选择仓库');return;
    }
    //2004-4-23 11:32 新旧盘点机区分 yjg
    transferScan(isNew);
  }
     //删除
     function buttonEventD()
     {
   if(confirm('是否删除该记录？'))  sumitForm(<%=Operate.DEL%>);
     }
     //打印
     function buttonEventP()
     {
    <%
      String billName = "";
    //销售出库是否显示单价金额.1=显示,0=不显示
    if ( b_OutSaleStore.KC_OUT_SHOW_PRICE.equals("0") )
      billName="putoutlist_edit_bill";
    else
      billName="putoutlist_edit_bill0";
    billName = "putoutlist_bill_wrapper_print";
     %>
    location.href='../pub/pdfprint.jsp?code=<%=billName%>&operate=<%=Operate.PRINT_PRECISION%>&sfdjid=<%=masterRow.get("sfdjid")%>';
     //location.href='putoutlist_bill_wrapper_print.jsp?operate=<%=b_OutSaleStore.WRAPPER_PRINT%>';
       }
       //图标新增事件
       function buttonEventA()
       {
         if(form1.storeid.value==''){alert('请选择仓库');return;}
         if(form1.dwtxid.value==''){alert('请选择供货单位');return;}
         if(form1.srlx.value==''){alert('请选择单据');return;}
         LadingGoodsMultiSelect('form1','srcVar=importLadingGoods&dwtxid='+form1.dwtxid.value+'&storeid='+form1.storeid.value);
       }
  //删除数量是空白的行
  function buttonEventR()
  {
      sumitForm(<%=b_OutSaleStore.DELETE_BLANK%>)
  }
      //2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg
  function deptChange()
  {
    associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'jsr', 'deptid', eval('form1.deptid.value'), '');
  }
  function showDetail(masterRow){
    selectRow();
  }
</script>
<%if(b_OutSaleStore.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
<%@ page contentType="text/html; charset=gb2312" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%><%
  engine.erp.store.shengyu.B_StoreCheck storeCheckBean = engine.erp.store.shengyu.B_StoreCheck.getInstance(request);
  String pageCode = "store_check";
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
<script language="javascript" src="../scripts/frame.js"></script>
<script language="javascript" src="../scripts/rowcontrol.js"></script>
<script language="javascript" src="../scripts/tabcontrol.js"></script>
<script language="javascript">
var isBatAdd = false;
function sumitForm(oper, row)
{
    //02.25 12:06 新增 新增了下面判断部门和仓库不能为空的判断.因为主要是为了显示出损yi单来的时候会出现javasript错误.
    //盘点单调用语句如下:showInterFrame(pdid, deptid, storeid, row).当deptid,及storeid为空时就会出错.
    if (form1.deptid.value == "" || form1.storeid.value == "")
    {
      alert("部门或仓库不能为空!");
      return;
    }
    if ( !checkDetails(oper) ) {return;}
    //主要是验证是不是有产品编码, 批号, 规格属性有相同的
    lockScreenToWait("处理中, 请稍候！");
    form1.rownum.value = row;
    form1.operate.value = oper;
    form1.submit();
}
function backList()
{
  location.href='store_check.jsp';
}
function checkRadio(row){
  if(form1.sel.length + ''=='undefined')
    form1.sel.checked = !form1.sel.checked;
  else
    form1.sel[row].checked = !form1.sel[row].checked;
}
function submitAdd(row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = isBatAdd ? <%=storeCheckBean.DETAIL_BIG_ADD%> : <%=Operate.DETAIL_ADD%>;
  if (row+''!='undefined') form1.rownum.value = row; else form1.rownum.value=-1;
  form1.submit();
}
function showText(obj){
  if(obj=='2'){
   document.all.chlbtext.style.display = 'block';
   document.all.cpbmtext.style.display = 'none';
   document.all.selectKind.style.display = 'none';
   //document.all.dmsxidtext.style.display = 'none';
  }
  else if(obj=='3'){
    document.all.cpbmtext.style.display = 'block';
    document.all.chlbtext.style.display = 'none';
    document.all.selectKind.style.display = 'none';
    //document.all.dmsxidtext.style.display = 'none';
  }
}
function hideText(){
    document.all.cpbmtext.style.display = 'none';
    document.all.chlbtext.style.display = 'none';
    document.all.selectKind.style.display = 'none';
    //document.all.dmsxidtext.style.display = 'none';
}
function showFixedQuery(isBat){
  isBatAdd = isBat;
  showFrame('fixedQuery', true, "", true);
  document.all.chlbtext.style.display = 'none';
  document.all.cpbmtext.style.display = 'none';
  document.all.selectKind.style.display = 'none';
}
function productCodeSelect(obj, i)
{
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value,'product_change('+i+')');
}
function prodchange(i){
  document.all['zcsl_'+i].value="0";
}
function productNameSelect(obj,i)
{
  ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i+'&srcVar=hsdw_'+i+'&srcVar=hsbl_'+i+'&srcVar=isprops_'+i+'&storeid='+form1.storeid.value,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops', obj.value,'product_change('+i+')');
}
//02.20 18:21 下面两个函数为 增加上 查看或生成损溢单的图标 而加.同时相应的js和DIV也做了增加 yjg
function showInterFrame(pdid, deptid,storeid,row)
{
     var url = "../store_shengyu/check_make_destroy.jsp?operate=0&pdid="+pdid+"&deptid="+deptid+"&storeid="+storeid+"&rownum="+row;
     document.location.href = url;
     //showFrame('detailDiv',true,"",true);
}
function hideFrameNoFresh()
{
  hideFrame('detailDiv');
}
    <%--03.05 16:54 新增 新增用于规格属性选择的js函数 yjg--%>
function propertyNameSelect(obj,cpid,i)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                         'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
function product_change(i){
  document.all['dmsxid_'+i].value="";
  document.all['sxz_'+i].value="";
  form1.cpbm_a.value=eval("form1.cpbm_"+i+".value");
  form1.cpbm_b.value=eval("form1.cpbm_"+i+".value");
  for (j=0;j<form1.scope.length;j++)
  {
    if (form1.scope[j].value=='3')
    {
      form1.scope[j].checked=true;
    }
  }
  submitAdd(i);
  //document.all['widths_'+i].value="";
  //associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_TECHNICS_ROUTE%>', 'gylxid_'+i, 'cpid', eval('form1.cpid_'+i+'.value'), '', true);
}
</script>
<%String retu = storeCheckBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp stockKindBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STOCKS_KIND);//存货类别
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//LookUp部门信息
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);//LookUp人员信息
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);//LookUp仓库信息
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp storeAreaBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE_AREA);
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = storeCheckBean.getMaterTable();//得到主表
  EngineDataSet list = storeCheckBean.getDetailTable();//得到从表
  HtmlTableProducer masterProducer = storeCheckBean.masterProducer;
  HtmlTableProducer detailProducer = storeCheckBean.detailProducer;
  RowMap masterRow = storeCheckBean.getMasterRowinfo();//主表行信息
  RowMap[] detailRows= storeCheckBean.getDetailRowinfos();//从表行信息
  String zt=masterRow.get("zt");
  String zdrid = masterRow.get("zdrid");//得到该单据的制单员id
  String loginId = storeCheckBean.loginId;
  //2004-5-2 16:43 为给明细数据集加入分页功能
  String count = String.valueOf(list.getRowCount());
  int iPage = 30;
  String pageSize = String.valueOf(iPage);

  if(storeCheckBean.isApprove)
  {
    storeBean.regData(ds, "storeid");
    deptBean.regData(ds, "deptid");
  }
  boolean isEnd = storeCheckBean.isApprove || (!storeCheckBean.masterIsAdd() && !zt.equals("0"));//表示已经审核或已完成
  boolean isCanDelete = !isEnd && !storeCheckBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete)
                        && loginId.equals(zdrid);//没有结束,在修改状态,并有删除权限,2004-08-04 并且登陆人等于制单人
  isEnd = isEnd
          || !(storeCheckBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit))
          || !loginId.equals(zdrid);//2004-08-04 新增 只有当前登陆人是制单人的时候才可以修改 yjg

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("1") ? ("已审核"/* 审核人:"+ds.getValue("shr")*/) : (zt.equals("9") ? "审批中" : "未审核");
  boolean isAdd = storeCheckBean.isDetailAdd;
  String SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
%>
<BODY oncontextmenu="window.event.returnValue=true" onload="syncParentDiv('tableview1');">
<jsp:include page="../pub/scan_bar.jsp" flush="true"/>
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="760" BORDER=0 CELLSPACING=0 CELLPADDING=0>
    <tr>
      <td align="center" height="5"></td>
    </tr>
  </table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="old_scsl" value="">
  <INPUT TYPE="HIDDEN" NAME="old_hssl" value="">
  <INPUT TYPE="HIDDEN" NAME="old_ce" value="">
  <INPUT TYPE="HIDDEN" NAME="beginRNo" value="">
  <INPUT TYPE="HIDDEN" NAME="endRNo" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0>
          <tr>
            <td class="activeVTab">库存盘点单(<%=title%>)
              <%
              //当是新增的时候不显示出上一笔下一笔
              if (!storeCheckBean.masterIsAdd())
              {
                ds.goToInternalRow(storeCheckBean.getMasterRow());
                boolean isAtFirst = ds.atFirst();boolean isAtLast = ds.atLast();
              %>
              <%if (!isAtFirst)
              {%>
              <a href="#" title="到上一笔(ALT+Z)" onClick="sumitForm(<%=storeCheckBean.PRIOR%>)">&lt</a>
              <pc:shortcut key='z' script='<%="sumitForm("+storeCheckBean.PRIOR+")"%>'/>
             <%}%>
               <%if (!isAtLast)
              {%>
              <a href="#" title="到下一笔(ALT+X)" onClick="sumitForm(<%=storeCheckBean.NEXT%>)">&gt</a>
              <pc:shortcut key='x' script='<%="sumitForm("+storeCheckBean.NEXT+")"%>'/>
             <%}//
              }%>
            </td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td> <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0">
                <%storeBean.regData(ds,"storeid");
                  deptBean.regData(ds,"deptid");
                  String pdr=masterRow.get("pdr");
                  storeAreaBean.regConditionData(ds, "storeid");
                  String sumit = "if(form1.storeid.value !='"+masterRow.get("storeid")+"'){sumitForm("+storeCheckBean.STORECHANGE+");}";
                %>
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("pdhm").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="pdhm" value='<%=masterRow.get("pdhm")%>' maxlength='<%=ds.getColumn("pdhm").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("rq").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="rq" value='<%=masterRow.get("rq")%>' maxlength='10' style="width:90" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();"<%=readonly%>>
                    <%if(!isEnd){%>
                    <a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.sfrq);"></a>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                  <td  noWrap class="td">
                    <%if(isEnd) {
                      out.print("<input name='storeidName' type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                      out.print("<input name='storeid' type='hidden' value='"+masterRow.get("storeid")+"'>");
                    }else {%>
                    <pc:select name="storeid" addNull="1" style="width:110" onSelect="<%=sumit%>">
                    <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd){
                      out.print("<input  type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly>");
                      out.print("<input  name='deptid' type='hidden' value='"+masterRow.get("deptid")+"' readonly>");
                    }else {%>
                    <pc:select  name="deptid" addNull="1" style="width:110" onSelect="deptChange()"><%--2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg--%>
                       <%=deptBean.getList(masterRow.get("deptid"))%>
                    </pc:select>
                    <%}%>
                  </td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("pdr").getFieldname()%></td>
                  <td class="td" nowrap>
                    <%if(isEnd){%>
                    <input type="text"  value='<%=masterRow.get("pdr")%>' maxlength='<%=ds.getColumn("pdr").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                    <%}else {%>
                    <pc:select combox="1" className="edFocused" name="pdr" value="<%=pdr%>" style="width:110">
                    <%=personBean.getList()%></pc:select>
                    <%}%>
                  </td>
                  <td></td>
                  <td></td>
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
                  <td class="td" colspan="8" nowrap>
                   <pc:navigator id="store_checkNav" recordCount="<%=count%>" pageSize="<%=pageSize%>" form="form1" operate='<%="operate=sumitForm("+storeCheckBean.TURNPAGE+")"%>'/>
                 </td>
               </tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height=<%=width%>;overflow-y:auto;overflow-x:auto;">
                      <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                        <tr class="tableTitle">
                          <td nowrap width=10></td>
                          <td height='20' align="center" nowrap>
                            <%if(!isEnd){%>
                            <input name="image" class="img" type="image" title="添加(ALT+A)" onClick="showFixedQuery(false);" src="../images/add_big.gif" border="0">
                             <input class="edFocused_r"  name="tCopyNumber" value="<%=request.getParameter("tCopyNumber")==null?"1":request.getParameter("tCopyNumber")%>" size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                             <pc:shortcut key="a" script='<%="showFixedQuery(false)"%>'/>
                            <%}%>
                          </td>
                          <td nowrap>产品编码</td>
                          <td nowrap>品名规格（花号款式）</td>
                          <td nowrap>规格属性</td>
                          <td nowrap>电脑库存数量</td>
                          <td nowrap>实际库存数量</td>
                          <td nowrap>计量单位</td>
                          <td nowrap>电脑库存换算数量</td>
                          <td nowrap>实际库存换算数量</td>
                          <td nowrap>换算单位</td>
                          <%--02.19 20:36 修改帐存数量, 库存数量 改为现在的名称电脑库存数量  实际库存数量 yjg --%>
                          <td nowrap>批号</td>
                          <td nowrap>库位</td>
                          <td nowrap>盈亏</td>
                          <td nowrap>差额</td>
                        </tr>
                        <%
                      personBean.regConditionData(ds, "deptid");
                      BigDecimal t_zcsl = new BigDecimal(0),t_scsl = new BigDecimal(0), t_ce = new BigDecimal(0),t_hssl = new BigDecimal(0), t_zchssl = new BigDecimal(0);
                      double d_ce = 0;
                      String ce = null;
                      String isLoss = null;
                      int i=0;
                      RowMap detail = null;
                      //2004-5-2 16:43 为明细资料页面加入页
                      int min = store_checkNav.getRowMin(request);
                      int max = store_checkNav.getRowMax(request);
                      //类中取得笔每一页的数据范围
                      storeCheckBean.min = min;
                      storeCheckBean.max = max > detailRows.length-1 ? detailRows.length-1 : max;
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

                      list.goToRow(min);
                      //2004-5-2 16:43 修改 将原来的i<detailRows.length修改成现在的i<=max && i<list.getRowCount();
                      for(i=min; i<=max && i<detailRows.length; i++){
                        detail = detailRows[i];
                        String zcsl = detail.get("zcsl");
                        if(storeCheckBean.isDouble(zcsl))
                          t_zcsl = t_zcsl.add(new BigDecimal(zcsl));
                        String scsl = detail.get("scsl");
                        if(storeCheckBean.isDouble(scsl))
                          t_scsl = t_scsl.add(new BigDecimal(scsl));
                        String hssl = detail.get("hssl");
                        if(storeCheckBean.isDouble(hssl))
                          t_hssl = t_hssl.add(new BigDecimal(hssl));

                        String zchssl = detail.get("zchssl");
                       if(storeCheckBean.isDouble(zchssl))
                          t_zchssl = t_zchssl.add(new BigDecimal(zchssl));
                        double d_zcsl = zcsl.length()>0 ?  Double.parseDouble(zcsl) : 0;
                        double d_scsl = scsl.length()>0 ?  Double.parseDouble(scsl) : 0;
                        d_ce = d_scsl - d_zcsl;
                        ce = String.valueOf(d_ce);
                        t_ce = t_ce.add(new BigDecimal(ce));
                        isLoss = d_scsl>d_zcsl ? "盈" : (d_scsl==d_zcsl ? "平" : "亏");
                        String bz = detail.get("bz");
                        String cpid = detail.get("cpid");
                        String wzmxid = detail.get("wzmxid");
                        String kwName = "kwid_"+i;
                        String kwMcName = "kwMc_"+i;
                        String kwid = detail.get("kwid");
                    %>
                        <tr id="rowinfo_<%=i%>" onClick="showDetail()">
                          <td class="td" nowrap><%=i+1%></td>
                          <td class="td" nowrap align="center">
                           <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                            <%if(!isEnd){%>
                            <input type="hidden" name="singleIdInput_<%=i%>" value="">
                            <input name="image" class="img" type="image" title="单选物资" src="../images/select_prod.gif" border="0"
                             onClick="ProdSingleSelect('form1','srcVar=cpid_<%=i%>&srcVar=cpbm_<%=i%>&srcVar=product_<%=i%>&srcVar=jldw_<%=i%>&srcVar=hsdw_<%=i%>&srcVar=hsbl_<%=i%>&srcVar=isprops_<%=i%>',
                               'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw&fieldVar=hsdw&fieldVar=hsbl&fieldVar=isprops','&storeid='+form1.storeid.value,'product_change(<%=i%>)')">
                            <input name="image" class="img" type="image" title="复制当前行" onClick="sumitForm(<%=storeCheckBean.COPYROW%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                            <input name="image" class="img" type="image" title="删除" onClick="sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                            <%}%>
                          </td>
                          <%RowMap  prodRow= prodBean.getLookupRow(detail.get("cpid"));
                                    String isprops = prodRow.get("isprops");
                                    detail.put("hsbl",prodRow.get("hsbl"));
                         %>
                          <td class="td" nowrap><input type="hidden" name="cpid_<%=i%>" value="<%=detail.get("cpid")%>">
                            <input type="text" <%=bz.equals("1") ? "class=ednone" :  detailClass%>  style="width:100%" onKeyDown="return getNextElement();" id="cpbm_<%=i%>" name="cpbm_<%=i%>" value='<%=prodRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=bz.equals("1") ? "readonly" : readonly%>>
                            <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=prodRow.get("hsbl")%>'>
                            <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                            <input type='hidden' id='isprops_<%=i%>' name='isprops_<%=i%>' value=''>
                          </td>
                          <td class="td" nowrap align="center">
                            <input type="text" <%=bz.equals("1") ? "class=ednone" :  detailClass%>   onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=prodRow.get("product")%>' onchange="productNameSelect(this,<%=i%>)" <%=bz.equals("1") ? "readonly" : readonly%>>
                          </td>
                          <%--02:14:50 新增一个规格属性 dmsxid td. 为新增一个规格属性而新增的.因为要它来和cpid, ph组合来确定只唯一性--%>
                          <td class="td" nowrap align="center">
                            <input type="HIDDEN"  id="dmsxid_<%=i%>"  name="dmsxid_<%=i%>"  value='<%=detail.get("dmsxid")%>'>
                            <input  <%=detailClass%>  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;} propertyNameSelect(this,form1.cpid_<%=i%>.value,<%=i%>)" <%=bz.equals("1") ? "readonly" : readonly%>>
                            <%if(!isEnd){%>
                            <img style='cursor:hand' src='../images/view.gif' title="规格属性" border=0 onClick="if(form1.cpid_<%=i%>.value==''){alert('请先输入产品');return;}if(form1.isprops_<%=i%>.value=='0'){alert('该产品无规格属性!');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>', form1.cpid_<%=i%>.value)">
                            <img style='cursor:hand' src='../images/delete.gif' title="删除" border=0 onClick="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';">
                            <%}%>
                            </td>
                          <td class="td" nowrap align="right"><input type="text" class="ednone_r"   onKeyDown="return getNextElement();" id="zcsl_<%=i%>" name="zcsl_<%=i%>" value='<%=detail.get("zcsl")%>' maxlength='<%=list.getColumn("zcsl").getPrecision()%>' readonly></td>
                          <td class="td" nowrap align="right"> <input type="text" <%=detailClass_r%>  style="width:100%" onKeyDown="return getNextElement();" id="scsl_<%=i%>" name="scsl_<%=i%>" value='<%=detail.get("scsl")%>' onFocus="saveCurrentVariable(<%=i%>)"  onChange="sl_onchange(<%=i%>, false)" maxlength='<%=list.getColumn("scsl").getPrecision()%>' <%=readonly%>>
                          <td class="td" nowrap align="right"><input type="text" class="ednone_r"   onKeyDown="return getNextElement();" id="jldw_<%=i%>" name="jldw_<%=i%>" value='<%=prodRow.get("jldw")%>' readonly></td>
                          <td class="td" nowrap align="right"><input type="text" class="ednone_r"   style="width:100%" onKeyDown="return getNextElement();" id="zchssl_<%=i%>" name="zchssl_<%=i%>" value='<%=detail.get("zchssl")%>' readonly></td>
                          <td class="td" nowrap align="right"> <input type="text" <%=detailClass_r%>   style="width:100%" onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' onFocus="saveCurrentVariable(<%=i%>)" onChange="sl_onchange(<%=i%>, true)" maxlength='<%=list.getColumn("hssl").getPrecision()%>' <%=readonly%>>
                          </td>
                          <%--
                          <td class="td" nowrap><%=prodRow.get("jldw")%></td>
                          <td class="td" nowrap><%=prodRow.get("hsdw")%></td>
                          --%>
                          <td class="td" nowrap align="right"><input type="text" class="ednone"  style="width:100%" onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=prodRow.get("hsdw")%>' readonly></td>
                          <%--02.23 15:13 修改js事件onblur为onChange事件 杨建国--%>
                          <td class="td" nowrap align="center"><input type="text" <%=bz.equals("1") ? "class=ednone" :  detailClass%>  style="width:100" onKeyDown="return getNextElement();" id="ph_<%=i%>" name="ph_<%=i%>" value='<%=detail.get("ph")%>' <%=bz.equals("1") ? "readonly" : readonly%>>
                          </td>
                          <td class="td" nowrap>
                        <%if(isEnd)
                          {
                           out.print("<input type='text' name="+kwMcName+" value='"+storeAreaBean.getLookupName(detail.get("kwid"))+"' class='ednone' readonly>");
                           out.print("<input type='hidden' name="+kwName+" value='"+detail.get("kwid")+"'>");
                          }
                        else {%>
                        <pc:select addNull="1" className="edFocused" name="<%=kwName%>">
                        <%=storeAreaBean.getList(detail.get("kwid"), "storeid", masterRow.get("storeid"))%></pc:select>
                        <%}%>
                        </td>
                          <td class="td" nowrap align="center"><input id="isLoss_<%=i%>" name="isLoss_<%=i%>" type="text" class="ednone"  value='<%=isLoss%>' readonly style="width:100%"></td>
                          <td class="td" nowrap align="right"><input id="ce_<%=i%>" name="ce_<%=i%>" type="text" class="ednone_r"  value='<%=ce%>' readonly></td>
                        </tr>
                        <%list.next();
                        }
                        for(; i < 4; i++){
                  %>
                        <tr id="rowinfo_<%=i%>">
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                        </tr>
                        <%}%>
                        <tr id="rowinfo_end">
                          <td class="td">&nbsp;</td>
                          <td class="tdTitle" nowrap>合计
                          </td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td"><input id="t_zcsl" name="t_zcsl" type="text" class="ednone_r" style="width:100%" value='<%=t_zcsl%>' readonly></td>
                          <td class="td"><input id="t_scsl" name="t_scsl" type="text" class="ednone_r" style="width:100%" value='<%=t_scsl%>' readonly></td>
                          <td align="right" class="td">&nbsp;</td>
                          <td class="td"><input id="t_zchssl" name="t_zchssl" type="text" class="ednone_r" style="width:100%" value='<%=t_zchssl%>' readonly></td>
                          <td class="td"><input id="t_hssl" name="t_hssl" type="text" class="ednone_r" style="width:100%" value='<%=t_hssl%>' readonly></td>
                          <td align="right" class="td">&nbsp;</td>
                          <td align="right" class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td class="td">&nbsp;</td>
                          <td align="right" class="td"><input id="t_ce" name="t_ce" type="text" class="ednone_r" style="width:100%" value='<%=t_ce%>' readonly></td>
                        </tr>
                      </table>
                    </div></td>
                </tr>
              </table></td>
          </tr>
        </table></td>
    </tr>
    <tr>
      <td> <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("zdrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("zdr")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
              <%if(!isEnd){%>
              <input name="button4" type="button" class="button" title="增加一行(ALT+W)" value="增加一行(W)" style="width:75" onClick="buttonEventW()">
              <pc:shortcut key="w" script='<%="buttonEventW()"%>'/>
              <input name="button3" type="button" class="button" title="删除实际库存量为空行(ALT+R)" style="width:75" value="删除空行(R)" onClick="sumitForm(<%=storeCheckBean.DELETE_BLANK%>);">
               <pc:shortcut key="r" script='<%="sumitForm("+storeCheckBean.DELETE_BLANK+")"%>'/>
              <%--02.18 11:36 新增 新增盘点机按钮 yjg--%>
              <input type="hidden" name="scanValue" value="">
              <%--input type="button" class="button" title="盘点机(E)" value="盘点机(E)" style="width:75" onClick="buttonEventE(false)">
              <pc:shortcut key="e" script='<%="buttonEventE(false)"%>'/--%>
              <input name="button2" type="button" class="button" title="保存添加(ALT+N)" value="保存添加(N)" style="width:75" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);">
              <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+")"%>'/>
              <input name="btnback" type="button" class="button" title="保存(ALT+S)" value="保存(S)"  onClick="sumitForm(<%=Operate.POST%>);">
              <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+")"%>'/>
              <%}%>
              <%/*if(!isEnd){*/
              %>
              <%--02.20 18:21 加上查看或生成损溢单的图标.同时相应的js和DIV也做了增加 yjg--%>
               <%--02.24 0:59 新增 新增给显示出查看损易单按钮加上条件.即在没有盘点号的情况下不显示出来.新增一笔主表资料的时候不显示 yjg--%>
              <%
                if ( !masterRow.get("pdhm").equals("") )
                { String dispTitle = masterRow.get("sfdjid").equals("") ? "生成损溢单" : "查看损溢单";
              %>
              <input name="viewDestory" type="button" class="button" style='width:90' title="<%=dispTitle%>(ALT+F)" value="<%=dispTitle%>(F)" onClick="buttonEventF()" src='../images/edit.old.gif' border="0">
              <pc:shortcut key="f" script='<%="buttonEventF()"%>'/>
              <%
                }
              /*}*/
              %>
              <%if(isCanDelete){%>
              <input name="button3" type="button" class="button" title="删除(ALT+D)" value="删除(D)" onClick="buttonEventD();">
              <pc:shortcut key="d" script="buttonEventD()"/>
              <%}%>
              <%if(!storeCheckBean.isApprove){%>
              <input name="btnback" type="button" class="button" title="返回(ALT+C)" value="返回(C)" onClick="backList();">
              <pc:shortcut key="c" script='<%="backList()"%>'/>
              <%}%>
              <%--<input name="import" type="button" class="button" title="" value="import" onClick="sumitForm(<%=storeCheckBean.IMPORT_DATA%>);">--%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
<script language="javascript">initDefaultTableRow('tableview1',1);
  <%--02.27 17:54 新增 新增 下面这行jsp代码用来调整网页中的明细资料部分的input的长度. yjg--%>
<%=storeCheckBean.adjustInputSize(new String[]{"cpbm","product", "jldw", "ph", "sxz", "zcsl", "scsl", "isLoss", "ce", "kwid", "zchssl", "hssl"},  "form1", (storeCheckBean.max-min+1), min)%>
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
  function sl_onchange(i, isBigUnit)
  {
    var oldhsblObj = document.all['hsbl_'+i];
    var sxzObj = document.all['sxz_'+i];
    unitConvert(document.all['prod_'+i], 'form1', 'srcVar=truebl_'+i, 'exp='+oldhsblObj.value, sxzObj.value, 'nsl_onchange('+i+','+isBigUnit+')');
  }
  function nsl_onchange(i, isBigUnit)
  {
    var scslObj = document.all['scsl_'+i];
    var zcslObj = document.all['zcsl_'+i];
    var hsslObj = document.all['hssl_'+i];
    //var hsblObj = document.all['hsbl_'+i];
    var hsblObj = document.all['truebl_'+i];
    var ceObj = document.all['ce_'+i];
    var isLossObj = document.all['isLoss_'+i];

    var obj = isBigUnit ? hsslObj : scslObj;
     var showText = isBigUnit ? "输入的换算数量非法" : "输入的数量非法";
     var changeObj = isBigUnit ? scslObj : hsslObj;
     if(obj.value==""){
       //2004-4-1 21:40 新增 当换算数量或数量输入不合法的时候一定要合计值一定要减去获得焦点时保存下来的旧有的值. yjg
       if ( isBigUnit )
         document.all['t_hssl'].value -= form1.old_hssl.value;
      else
        document.all['t_scsl'].value -= form1.old_scsl.value;
       return;
     }

     if(isNaN(obj.value))
     {
       //2004-4-1 21:40 新增 当换算数量或数量输入不合法的时候一定要合计值一定要减去获得焦点时保存下来的旧有的值. yjg
       if ( isBigUnit )
         document.all['t_hssl'].value -= form1.old_hssl.value;
      else
        document.all['t_scsl'].value -= form1.old_scsl.value;
       alert(showText);
       obj.focus();
       return;
     }
     /*2004-3-30 15:27 修改 javascript修改页面上可以输入数值的数量,换算数量栏位,*/
     //数量,换算数量互动关系仅在如此情况下有效:当修改其中的一个如果另一个是空的话则跟随改变
     if(!(changeObj.value!="" && '<%=SC_STORE_UNIT_STYLE%>'!='1'))//是否强制转换
     {
       if(hsblObj.value!="" && !isNaN(hsblObj.value))
         changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(scslObj.value)/parseFloat(hsblObj.value)));
       else
         changeObj.value ="";
     }
     //02.20 14:53 修改判断盈亏的语句为如下这样. yjg
    var result = parseFloat(scslObj.value) - parseFloat(zcslObj.value)
    isLossObj.value = result >0  ? "盈" : (result==0 ? "平" : "亏");
    ceObj.value = formatQty(result);//formatQty(parseFloat(scslObj.value) - parseFloat(zcslObj.value));
    cal_all(i);
  }
  function cal_all(i)
  {
    cal_tot('scsl', i);
    cal_tot('ce', i);
    cal_tot('hssl', i);
  }
  function cal_tot(type, i)
  {
    var tmpObj;
    var tot=0;
    //for(i=0; i<<%=detailRows.length%>; i++)
    //{
      if(type == 'scsl')
        tmpObj = document.all['scsl_'+i];
      else if(type == 'ce')
          tmpObj = document.all['ce_'+i];
      else if(type == 'hssl')
          tmpObj = document.all['hssl_'+i];
      else
        return;
      if(isNaN(tmpObj.value) || tmpObj.value == "")
        return;
      //  tot += parseFloat(tmpObj.value);
      //}
    if(type == 'scsl')
      document.all['t_scsl'].value = formatQty(
          parseFloat(document.all['t_scsl'].value)
          -parseFloat(form1.old_scsl.value)
          +parseFloat(tmpObj.value)
          );
    else if(type == 'ce')
      document.all['t_ce'].value = formatQty(
          parseFloat(document.all['t_ce'].value)
          -parseFloat(form1.old_ce.value)
          +parseFloat(tmpObj.value)
          );
    else if (type == 'hssl')
      document.all['t_hssl'].value = formatQty(
          parseFloat(document.all['t_hssl'].value)
          -parseFloat(form1.old_hssl.value)
          +parseFloat(tmpObj.value)
          );
  }
  function saveCurrentVariable(i)
  {
    var isScVariable = !isNaN(document.all['scsl_'+i].value) && document.all['scsl_'+i].value != "";
    var isHsVariable = !isNaN(document.all['hssl_'+i].value) && document.all['hssl_'+i].value != "";
    var isCeVariable = !isNaN(document.all['ce_'+i].value) && document.all['ce_'+i].value != "";

    form1.old_scsl.value = isScVariable?document.all['scsl_'+i].value:0;
    form1.old_hssl.value = isHsVariable?document.all['hssl_'+i].value:0;
    form1.old_ce.value = isCeVariable?document.all['ce_'+i].value:0;
  }
  function OrderGoodsMultiSelect(frmName, srcVar, methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "../store/import_ordergoods_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function OrderGoodsSingleSelect(frmName,srcVar,fieldVar,curID,storeid,methodName,notin)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "OrderSingleSelector";
    paraStr = "../store/ordergoods_single_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&dwtxid="+curID+"&storeid="+storeid;
    if(methodName+'' != 'undefined')
      paraStr += "&method="+methodName;
    if(notin+'' != 'undefined')
      paraStr += "&notin="+notin;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
  function showTreeTable(){
    document.all.selectKind.style.display = 'block';
    document.all.cpbmtext.style.display = 'none';
    document.all.chlbtext.style.display = 'none';
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
     sumitForm(<%=storeCheckBean.NEW_TRANSFERSCAN%>);
   else
     sumitForm(<%=storeCheckBean.TRANSFERSCAN%>);
  }
</script>
<%
  EngineDataSet dsProductSort = storeCheckBean.getProductSortTable();//得到物资类别数据集
%>
  <div class="queryPop" id="fixedQuery" name="fixedQuery" >
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('fixedQuery')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=3 cellpadding=0 border=0>
      <TR>
        <TD> <TABLE cellspacing=3 cellpadding=0 border=0>
            <TR>
              <TD height="20" CLASS="td"><b>仓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;库</b></TD>
              <TD nowrap class="td"><pc:select name="add_storeid" style="width:160">
                <%=storeBean.getList(masterRow.get("storeid"))%></pc:select> </TD>
            </TR>
            <TR>
              <TD colspan="2" NOWRAP CLASS="td"><b>存货范围</b></TD>
            </TR>
            <TR>
              <TD colspan="2" NOWRAP CLASS="td"> <INPUT TYPE=RADIO NAME="scope" VALUE="1" onClick="hideText();" CHECKED>
                所有存货</TD>
            </TR>
            <TR>
              <TD class="td"><INPUT TYPE="RADIO" NAME="scope" VALUE="2" onClick="showText(this.value);">
                按存货类别筛选</TD>
              <TD CLASS="td" id="chlbtext" name="chlbtext" style="display:block"><pc:select name="chlbid" style="width:170">
                <%=stockKindBean.getList()%></pc:select> </TD>
            </TR>
            <TR>
              <TD colspan="2" CLASS="td"> <INPUT TYPE="RADIO" NAME="scope" VALUE="3" onClick="showText(this.value);">
                指定存货代码范围</TD>
            </TR>
            <TR id="cpbmtext" style="display:block">
              <TD colspan="2" CLASS="td">
                代码范围<input type="text" name="cpbm_a" style="width:70" class="edbox">
                至
                <input type="text" name="cpbm_b" style="width:70" class="edbox">
            </TD>
           </TR>
            <TR>
              <TD colspan="2" CLASS="td"> <INPUT TYPE="RADIO" NAME="scope" VALUE="4" onClick="showTreeTable();" >
                指定某几个大类</TD>
            </TR>
            <TD colspan="2" CLASS="td"> <div id="selectKind" name="selectKind" style="display:none;width:300;height:200;overflow-y:auto;overflow-x:auto;">
                <table id="tableview2" width="260" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                  <tr class="tableTitle">
                    <td class="td" nowrap valign="middle" width=20><input type='checkbox' name='checkform' onclick='checkAll(form1,this);'></td>
                    <td nowrap>编码</td>
                    <td nowrap>名称</td>
                  </tr>
                  <%
               int rowcount = dsProductSort.getRowCount();
               dsProductSort.first();
               for(int k=0; k<rowcount; k++){
              %>
                  <tr>
                    <td nowrap align="center" class="td"><input type="checkbox" name="sel" value='<%=dsProductSort.getValue("wzlbid")%>' onKeyDown="return getNextElement();"></td>
                    <td nowrap align="right" class="td" onClick="checkRadio(<%=k%>)"><%=dsProductSort.getValue("bm")%>
                      <input name="hidden" type="hidden" id="wzlbid<%=k%>" value='<%=dsProductSort.getValue("wzlbid")%>'></td>
                    <td nowrap align="right" class="td" onClick="checkRadio(<%=k%>)"><%=dsProductSort.getValue("mc")%></td>
                  </tr>
                  <%dsProductSort.next();
               }%>
                </TABLE>
              </DIV></TD>
          </TABLE>
      <TR>
        <TD colspan="4" nowrap class="td" align="center"><INPUT type="button" class="button" title="确定(ALT+M)" value="确定(M)" onClick="submitAdd()"   name="button" onKeyDown="return getNextElement();">
               <pc:shortcut key="m" script='<%="submitAdd()"%>'/>
          <INPUT onClick="hideFrame('fixedQuery')" type="button"  class="button" title="关闭(ALT+T)" value="关闭(T)" name="button2" onKeyDown="return getNextElement();">
                <pc:shortcut key="t" script='<%="buttonEventT()"%>'/>
        </TD>
      </TR>
    </TABLE>
  </DIV>

  <div class="queryPop" id="detailDiv" name="detailDiv">
    <div class="queryTitleBox" align="right"><A onClick="hideFrame('detailDiv')" href="#"><img src="../images/closewin.gif" border=0></A></div>
    <TABLE cellspacing=0 cellpadding=0 border=0>
      <TR>
        <TD><iframe id="interframe1" src="" width="800" height="400" marginWidth="0" marginHeight="0" frameBorder="0" noResize scrolling="no"></iframe>
        </TD>
      </TR>
    </TABLE>
  </div>
  </form>
<SCRIPT language="javascript">
  initDefaultTableRow('tableview2',1);
  function buttonEventW()
  {
    if(form1.storeid.value==''){alert('请选择仓库');return;}
    sumitForm(<%=storeCheckBean.DETAIL_ADD_BLANK%>);
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
   if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);
 }
 //打印
 function buttonEventP()
 {
    location.href='../pub/pdfprint.jsp?code=store_check_edit_bill&operate=<%=storeCheckBean.PRINT_BILL%>&a$pdid=<%=masterRow.get("pdid")%>&src=../store_shengyu/store_check_edit.jsp';
 }
 //查看或生成损益单
 <%
   if ( !masterRow.get("pdhm").equals("") )
   {
 %>
 function buttonEventF()
 {
   showInterFrame(<%=masterRow.get("pdid")%>, <%=masterRow.get("deptid")%>,<%=masterRow.get("storeid")%>,<%=0%>);
 }
 <%}%>
   //关闭查询小视窗
   function buttonEventT()
   {
     hideFrame('fixedQuery');
   }
   //2004-3-30 15:44 新增 给部门下拉表加上能使经手人跟随它自己不同部门而变的js函数 yjg
  function deptChange()
  {
    associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'pdr', 'deptid', eval('form1.deptid.value'), '');
  }
  function checkDetails(oper)
  {

   //if ( oper !=<%=storeCheckBean.TURNPAGE%> ) return true;
    if ( <%=max%>==0 ) return true;
    for (j=<%=min%>;j<=<%=max%>;j++)
   {
      obj = document.all['hssl_'+j];
      if(obj+''!='undefined' && isNaN(obj.value))
        obj.value = "";
      obj = document.all['scsl_'+j];
      if(obj+''!='undefined' && isNaN(obj.value))
        obj.value = "";
   }
    for (i=<%=min%>;i<=<%=max%>;i++)
    {
      hsslobj = document.all['hssl_'+i];
      scslobj = document.all['scsl_'+i];
      if ((hsslobj+''!='undefined' && isNaN(hsslobj.value))||
          (scslobj+''!='undefined' && isNaN(scslobj.value)) )
      {
         alert("第"+(i+1)+"行数据不合法");
         return false;
      }
      else
        return true;
    }
  }
  function showDetail(masterRow){
    selectRow();
  }
</SCRIPT>
<%if(storeCheckBean.isApprove){%>
<jsp:include page="../pub/approve.jsp" flush="true"/>
<%}%>
<%out.print(retu);%>
</BODY>
</Html>
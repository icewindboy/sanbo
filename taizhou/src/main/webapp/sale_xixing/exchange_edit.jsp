<%--销售提单--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%>
<%
  engine.erp.sale.xixing.B_SaleExchange b_SaleExchangeBean = engine.erp.sale.xixing.B_SaleExchange.getInstance(request);
  String pageCode = "exchange";
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
  location.href='exchange.jsp';
}
//打印
function ladingprint(tdid)
{
  location.href='../pub/pdfprint.jsp?operate=<%=Operate.PRINT_PRECISION%>&code=xs_lading_bill_print&tdid='+tdid;
}
//输入购货单位代码
function customerCodeSelect(obj)
{
  if(form1.tdrq.value=='')
  {
    alert('请输入提单日期!');
    return;
  }
  else
  {
    if(!checkDate(form1.tdrq))
      return;
  }
  CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=personid&srcVar=deptid','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=personid&fieldVar=deptid',obj.value,'sumitForm(<%=b_SaleExchangeBean.DWTXID_CHANGE%>,-1)');
}
//选择购货单位
function selectCustomer()
{
  if(form1.tdrq.value=='')
  {
    alert('请输入提单日期!');
    return;
  }
  else
  {
    if(!checkDate(form1.tdrq))
      return;
  }
CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=b_SaleExchangeBean.DWTXID_CHANGE%>,-1)');
}
//输入购货单位名称
function customerNameSelect(obj)
{
  if(form1.tdrq.value=='')
  {
    alert('请输入提单日期!');
    return;
  }
  else
  {
    if(!checkDate(form1.tdrq))
      return;
  }
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=dz','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr',obj.value,'sumitForm(<%=b_SaleExchangeBean.DWTXID_CHANGE%>,-1)');
}
//运输单位
function TransportDmChange(obj, i)
{
    TransportCodeChange(document.all['prod'], obj.form.name,'srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc',obj.value);
}
function TransportName(obj, i)
{
    TransportNameChange(document.all['prod'], obj.form.name,'srcVar=dwtxid_'+i+'&srcVar=dwdm_'+i+'&srcVar=dwmc_'+i,'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc',obj.value);
}
//提单日期变化--引起回款日期变化
function tdrqOnchange()
{
  checkDate(document.form1.tdrq);
  if(document.form1.hkts.value=="")
    return;
  else
  {
    document.form1.hkrq.value=addDate(document.form1.tdrq.value,parseFloat(document.form1.hkts.value));
  }
}
function selectTdrq()
{
  selectDate(document.form1.tdrq);
}
//回款天数变化--引起回款日期变化
function hktsOnchange()
{
  if(isNaN(document.form1.hkts.value)){
    alert("输入数据非法!");
    document.form1.hkts.value="";
    return;
    }
    document.form1.hkrq.value=addDate(document.form1.tdrq.value,parseFloat(document.form1.hkts.value));
}
//引起回款日期变化--回款天数变化
function hkrqOnchange()
{
  var td = document.form1.tdrq.value;
  var datestr = td.replace(/-/gi, "/");
  var dt = new Date(datestr);
  var tds = dt.getTime();

  var hk = document.form1.hkrq.value;
  datestr = hk.replace(/-/gi, "/");
  dt = new Date(datestr);
  var hks = dt.getTime();

  document.form1.hkts.value=(hks-tds)/(1000*60*60*24);
}
function addDate(sdate,sl)
{
  var datestr = sdate.replace(/-/gi, "/");
  var dt = new Date(datestr);
  var dt2 = new Date(dt.getYear() + "/" + (dt.getMonth() + 1) + "/" + (dt.getDate()+sl));
  var obj = dt2.getYear() + "-" + (dt2.getMonth() + 1) + "-" + dt2.getDate();
  return obj;
}
function detailAdd()
{
  if(form1.storeid.value=='')
  {
  alert('请选择仓库!');
  return;
  }
  if(form1.deptid.value=='')
  {
  alert('请选择直销店!');
  return;
  }
  if(form1.personid.value=='')
  {
  alert('请选择业务员!');
  return;
  }
sumitForm(<%=Operate.DETAIL_ADD%>);
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
</script>
<%
  String retu = b_SaleExchangeBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);//引用结算方式
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);//引用往来单位
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//引用部门
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//引用产品
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);//引用人员信息
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);//引用仓库信息
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp sendModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SEND_MODE);//发货方式
  engine.project.LookUp dprodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//引用产品
  engine.project.LookUp dpropertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性

  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_SaleExchangeBean.getMaterTable();
  EngineDataSet list = b_SaleExchangeBean.getDetailTable();
  EngineDataSet cyqktable = b_SaleExchangeBean.getCyqkTable();

  HtmlTableProducer masterProducer = b_SaleExchangeBean.masterProducer;
  HtmlTableProducer detailProducer = b_SaleExchangeBean.detailProducer;


  RowMap masterRow = b_SaleExchangeBean.getMasterRowinfo();//主表一行
  RowMap[] detailRows= b_SaleExchangeBean.getDetailRowinfos();//从表多行
  RowMap[] cyqkRows= b_SaleExchangeBean.getTdcyqkRowinfos();//从表多行


  corpBean.regData(ds,"dwtxid");
  if(b_SaleExchangeBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  String djlx=masterRow.get("djlx");
  sendModeBean.regData(ds, "sendmodeid");


  String zt=masterRow.get("zt");//0.初始化,1.已开提单(已经审核),4.作废,9.出库
  boolean isEnd =  b_SaleExchangeBean.isReport||b_SaleExchangeBean.isApprove || (!b_SaleExchangeBean.masterIsAdd() && !zt.equals("0"));
  boolean isCanDelete = !isEnd && !b_SaleExchangeBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);
  isEnd = isEnd || !(b_SaleExchangeBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段

  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edbox";
  String prodClass = detailClass;
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  String prodreanonly = readonly;
  String title = zt.equals("0") ? ("未审批") : ((zt.equals("9") ? "审批中" :(zt.equals("4")?"已作废":(zt.equals("8")?"完成":"已审"))) );
  String tdlx=djlx.equals("1")?"提货单":"退货单";
  boolean count=list.getRowCount()==0?true:false;
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));


  boolean canedt = zt.equals("0")||zt.equals("1")||zt.equals("2");
  String djandjjeClass = canedt ?"class=edFocused_r": "class=ednone_r" ;
  String dwtxClass = count? "class=edbox":"class=edline";
  String dwtxRead = count? " " : "readonly";
  boolean mustConversion = b_SaleExchangeBean.conversion;//是否需要强制转换
  String djhclass = zt.equals("2")?"class=edbox":"class=edline";
  String djhreadonly = zt.equals("2")?"":"readonly";

  propertyBean.regData(list,"dmsxid");
  dpropertyBean.regData(cyqktable,"dmsxid");
  prodBean.regData(list,"cpid");
  dprodBean.regData(cyqktable,"cpid");

%>
<BODY id="docbody" oncontextmenu="window.event.returnValue=true"  onLoad="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="djlx" value="<%=djlx%>">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">销售换货单</td>
          </tr>
        </table>
  <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0" width="100%">
                 <tr>
                    <td  noWrap class="tdTitle">单据编号</td>
                    <td  noWrap class="td"><input type="text" name="tdbh" value='<%=masterRow.get("tdbh")%>' maxlength='<%=ds.getColumn("tdbh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                    <td  noWrap class="tdTitle">日期</td>
                    <td  noWrap class="td">
                    <input type="text" name="tdrq" value='<%=masterRow.get("tdrq")%>' maxlength='10' style="width:110" <%=edClass%> onChange="tdrqOnchange()" <%=readonly%> >
                    <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectTdrq()"></a><%}%>
                    </td>
                   <td   noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                   <td  noWrap class="td">
                  <%if(isEnd||!count) out.print("<input type='hidden' name='storeid' value='"+masterRow.get("storeid")+"'><input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                  else {%>
                  <pc:select name="storeid" addNull="1" style="width:110">
                    <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle">直销店</td>
                  <td noWrap class="td">
                    <%
                      String onChange ="if(form1.deptid.value!='"+masterRow.get("deptid")+"')sumitForm("+Operate.DEPT_CHANGE+")";
                    %>
                    <%if(isEnd||!count) out.print("<input type='hidden' name='deptid' value='"+masterRow.get("deptid")+"'><input type='text'  value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110"  onSelect="deptchange();" >
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                    </td>
             </tr>
             <tr>
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>
                    <td  noWrap class="td">
                    <%
                      if(!isEnd)
                        personBean.regConditionData(ds, "deptid");
                      if(isEnd||!count) out.print("<input type='hidden' name='personid' value='"+masterRow.get("personid")+"'><input type='text'   value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:110' class='edline' readonly>");
                      else {%>
                     <pc:select name="personid" style="width:110" >
                      <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                   </td>
                    <td noWrap class="tdTitle">更换差价</td>
                    <td  noWrap class="td">
                    <input type="text" name="zje" class="edline" value='<%=masterRow.get("zje")%>'  style="width:110" <%=edClass%> readonly  >
                    </td>
               </tr>
                <tr>
                  <td  noWrap class="tdTitle">备注</td><%--其他信息--%>
                  <td colspan="7" noWrap class="td"><textarea name="bz" rows="3" onKeyDown="return getNextElement();" style="width:690"<%=readonly%>><%=masterRow.get("bz")%></textarea></td>
                </tr>
             <tr>
                <td nowrap class="tdTitle">
                 客户退货产品
              </td>
            </tr>
                <tr>
                  <td colspan="8" noWrap class="td">
                   <div style="display:block;width:760;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="760" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                          <%if(!isEnd){%><%if(b_SaleExchangeBean.canOperate){%>
                          <input name="image" class="img" type="image" title="新增(A)" onClick="detailAdd()" src="../images/add_big.gif" border="0">
                          <pc:shortcut key="a" script="detailAdd()" /><%}%>
                          <input class="edFocused_r"  name="tCopyNumber" value="1" title="拷贝(ALT+A)" size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                          <%}%>
                        </td>
                          <td nowrap>产品编码</td>
                          <td nowrap>品名</td>
                          <td nowrap>数量</td>
                          <td nowrap>单位</td>
                          <td nowrap>单价</td>
                          <td nowrap>金额</td>
                          <td nowrap>备注</td>
                        <%--detailProducer.printTitle(pageContext, "height='20'", true);--%>
                      </tr>
                    <%
                      BigDecimal t_sl = new BigDecimal(0), t_xsje = new BigDecimal(0), t_jje = new BigDecimal(0),t_hssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        RowMap productRow = prodBean.getLookupRow(detail.get("cpid"));
                        String isprops=productRow.get("isprops");
                        String cpid = detail.get("cpid");
                        String dj = detail.get("dj");
                        String sl = detail.get("sl");
                        String xsje = detail.get("xsje");
                        String jje = detail.get("jje");
                        double dsl=0;
                        double dxsje=0;
                        double djje=0;
                        if(b_SaleExchangeBean.isDouble(sl))
                        {
                          dsl=Double.parseDouble(sl);
                          t_sl = t_sl.add(new BigDecimal(dsl));
                        }
                        if(b_SaleExchangeBean.isDouble(jje))
                        {
                          djje=Double.parseDouble(jje);
                          t_jje = t_jje.add(new BigDecimal(djje));
                        }
                        prodClass = detailClass;
                        prodreanonly = readonly;
                        RowMap propertyRow = propertyBean.getLookupRow(detail.get("dmsxID"));
                     %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                         <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd){%>
                          <%if(detail.get("hthwid").equals("")){%>
                          <img style='cursor:hand' src='../images/select_prod.gif' border=0 onClick="SaleProdSingleSelect('form1','srcVar=wzdjid_<%=i%>&srcVar=cpid_<%=i%>&stroeid='+form1.storeid.value,'fieldVar=wzdjid&fieldVar=cpid','','sumitForm(<%=b_SaleExchangeBean.DETAIL_CHANGE%>,<%=i%>)')">
                          <%}%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <input name="image" class="img" type="image" title="复制当前行" onClick="sumitForm(<%=b_SaleExchangeBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <%}%></td>
                        <td class="td" nowrap>
                           <input type='HIDDEN' id='cpid_<%=i%>' name='cpid_<%=i%>' value='<%=detail.get("cpid")%>'>
                           <input type="HIDDEN" id="wzdjid_<%=i%>" name="wzdjid_<%=i%>" value='<%=detail.get("wzdjid")%>' >
                           <input type="HIDDEN" id="xsj_<%=i%>" name="xsj_<%=i%>" value='<%=detail.get("xsj")%>' >
                           <input type="HIDDEN"  id="dmsxid_<%=i%>"  name="dmsxid_<%=i%>"  value='<%=detail.get("dmsxid")%>' >
                           <input type="HIDDEN"  <%=detailClass%>  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="propertiesInput(this,form1.cpid_<%=i%>.value,<%=i%>)"  <%=readonly%> >

                           <input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=productRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=prodreanonly%>>
                        </td><!--产品编码(存货代码)--->
                        <td class="td" nowrap>
                          <input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=productRow.get("product")%>'  onchange="productNameSelect(this,<%=i%>)"   <%=prodreanonly%>></td>
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>'  onblur="sl_onchange(<%=i%>, false)" <%=readonly%>></td><!--数量-->
                        <td class="td" nowrap><%=productRow.get("jldw")%></td><!--计量单位--->
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>   onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' onchange="dj_onchange(<%=i%>)"  <%=readonly%> ></td><!--单价-->
                        <td class="td" nowrap><input type="text" class='ednone_r'  onKeyDown="return getNextElement();" id="jje_<%=i%>" name="jje_<%=i%>" value='<%=detail.get("jje")%>'  readonly></td><!--净金额-->
                        <td class="td" nowrap align="left"><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='100'  <%=readonly%> ></td><!--备注-->
                      </tr>
                      <%
                      }
                      for(; i < 4; i++){
                      %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap> 合计 </td>
                        <td class="td">&nbsp;</td>
                         <td class="td">&nbsp;</td>
                        <td class="td" nowrap><input id="t_sl" name="t_sl" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_sl.toString(),loginBean.getSumFormat())%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap><input id="t_jje" name="t_jje" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_jje.toString(),loginBean.getPriceFormat())%>'  readonly></td>
                        <td class="td">&nbsp;</td>
                      </tr>
                    </table></div>
                    <SCRIPT LANGUAGE="javascript">rowinfo = new RowControl();
                      <%for(int k=0; k<i; k++)
                        {
                         out.print("AddRowItem(rowinfo,'rowinfo_"+k+"');");
                       }%>
                      AddRowItem(rowinfo,'rowinfo_end');
                      InitRowControl(rowinfo);
                      initDefaultTableRow('tableview1',1);
                       <%=b_SaleExchangeBean.adjustInputSize(new String[]{"cpbm","product","dj","bz"}, "form1", detailRows.length)%>
                    </SCRIPT>
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
  <table><tr>
	 <td  class="tdTitle">客户更换产品</td> <td colspan="8"  class="tdTitle">&nbsp;</td>
	 </tr>
 </table>
  </td>
  </tr>
  <td colspan="8" noWrap class="td">
   <div style="display:block;width:800;overflow-y:auto;overflow-x:auto;">
    <table id="tableview2" width="800" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
   <tr class="tableTitle">
     <td nowrap width=10></td>
     <td height='20' align="center" nowrap width=30>
      <%if(!isEnd&&!count){%><%if(b_SaleExchangeBean.canOperate){%>
      <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(<%=b_SaleExchangeBean.CYQK_ADD%>)" src="../images/add_big.gif" border="0">
      <pc:shortcut key="a" script="sumitForm(<%=Operate.CYQK_ADD%>)" /><%}%>
      <%}%>
     </td>
      <td nowrap>产品编码</td>
      <td nowrap>品名</td>
      <td nowrap>数量</td>
      <td nowrap>单位</td>
      <td nowrap>单价</td>
      <td nowrap>金额</td>
      <td nowrap>备注</td>
   </tr>
   <%
     int k = 0;
     int cyrowcount = cyqkRows.length;
     BigDecimal dt_sl = new BigDecimal(0), dt_xsje = new BigDecimal(0), dt_jje = new BigDecimal(0),dt_hssl = new BigDecimal(0);
     RowMap cyrow = null;
     for(;k<cyrowcount;k++)
     {
       cyrow = cyqkRows[k];
       //RowMap dwpRow =corpBean.getLookupRow(cyrow.get("dwtxId"));
       String cpid  = cyrow.get("cpid");
       RowMap productRow = dprodBean.getLookupRow(cyrow.get("cpid"));
       RowMap propertyRow = dpropertyBean.getLookupRow(cyrow.get("dmsxID"));
       String isprops=productRow.get("isprops");
       String dj = cyrow.get("dj");
       String sl = cyrow.get("sl");
       String jje = cyrow.get("jje");
       double dsl=0;
       double djje=0;
       if(b_SaleExchangeBean.isDouble(sl))
       {
         dsl=Double.parseDouble(sl);
         dt_sl = dt_sl.add(new BigDecimal(dsl));
       }
       if(b_SaleExchangeBean.isDouble(jje))
       {
         djje=Double.parseDouble(jje);
         dt_jje = dt_jje.add(new BigDecimal(djje));
       }
   %>
   <tr id="rowinfo_<%=k%>">
     <td class="td" nowrap><%=k+1%></td>
     <td class="td" align="center" nowrap>
      <%if(!isEnd){%>
      <img style='cursor:hand' src='../images/select_prod.gif' border=0 onClick="SaleProdSingleSelect('form1','srcVar=dwzdjid_<%=k%>&srcVar=dcpid_<%=k%>&storeid='+form1.storeid.value,'fieldVar=wzdjid&fieldVar=cpid','','sumitForm(<%=b_SaleExchangeBean.DETAIL_EXCHANGE%>,<%=k%>)')">
      <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=b_SaleExchangeBean.CYQK_DEL%>,<%=k%>)" src="../images/delete.gif" border="0">
     <%}%>
     </td>
    <td class="td" nowrap>
       <input type='HIDDEN' id='dcpid_<%=k%>' name='dcpid_<%=k%>' value='<%=cyrow.get("cpid")%>'>
       <input type="HIDDEN" id="dwzdjid_<%=k%>" name="dwzdjid_<%=k%>" value='<%=cyrow.get("wzdjid")%>' >
       <input type="HIDDEN" id="dxsj_<%=k%>" name="dxsj_<%=i%>" value='<%=cyrow.get("xsj")%>' >
       <input type="HIDDEN"  id="ddmsxid_<%=k%>"  name="ddmsxid_<%=k%>"  value='<%=cyrow.get("dmsxid")%>'  >
       <input type="HIDDEN"  <%=detailClass%>  name="dsxz_<%=k%>" value='<%=propertyBean.getLookupName(cyrow.get("dmsxid"))%>' onchange="propertiesInput(this,form1.dcpid_<%=k%>.value,<%=k%>)"  <%=readonly%> >

       <input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" name="dcpbm_<%=k%>" value='<%=productRow.get("cpbm")%>' onchange="dproductCodeSelect(this,<%=k%>)" <%=readonly%>></td><!--产品编码(存货代码)--->
    <td class="td" nowrap>
      <input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" id="dproduct_<%=k%>" name="dproduct_<%=k%>" value='<%=productRow.get("product")%>'  onchange="dproductNameSelect(this,<%=k%>)"   <%=readonly%>></td>
    <td class="td" nowrap><input type="text" <%=detailClass_r%>  onKeyDown="return getNextElement();" id="dsl_<%=k%>" name="dsl_<%=k%>" value='<%=cyrow.get("sl")%>' maxlength='<%=cyqktable.getColumn("sl").getPrecision()%>' onblur="dsl_onchange(<%=k%>)" <%=readonly%>></td><!--数量-->
    <td class="td" nowrap><%=productRow.get("jldw")%></td><!--计量单位--->
    <td class="td" nowrap><input type="text" <%=detailClass_r%>   onKeyDown="return getNextElement();" id="ddj_<%=k%>" name="ddj_<%=k%>" value='<%=cyrow.get("dj")%>' onchange="ddj_onchange(<%=k%>)"  <%=readonly%> ></td><!--单价-->
    <td class="td" nowrap><input type="text" class='ednone_r'  onKeyDown="return getNextElement();" id="djje_<%=k%>" name="djje_<%=k%>" value='<%=cyrow.get("jje")%>'  readonly></td><!--净金额-->
    <td class="td" nowrap align="left"><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" name="dbz_<%=k%>" id="dbz_<%=k%>" value='<%=cyrow.get("bz")%>' maxlength='100'  <%=readonly%> ></td><!--备注-->
   </tr>
   <%
     }
     for(; k < 4; k++){
   %>
     <tr id="rowinfo_<%=k%>">
       <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
       <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
       <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
     </tr>
   <%}%>
      <tr id="rowinfo_end">
        <td class="td">&nbsp;</td>
        <td class="td" nowrap> 合计 </td>
        <td class="td">&nbsp;</td>
        <td class="td">&nbsp;</td>
        <td class="td" nowrap><input id="dt_sl" name="dt_sl" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(dt_sl.toString(),loginBean.getSumFormat())%>' readonly></td>
        <td class="td">&nbsp;</td>
        <td class="td">&nbsp;</td>
        <td class="td" nowrap><input id="dt_jje" name="dt_jje" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(dt_jje.toString(),loginBean.getPriceFormat())%>'  readonly></td>
        <td class="td">&nbsp;</td>
      </tr>
 </table>
 <script language="javascript">initDefaultTableRow('tableview2',1);</script>
</div>
</center>
</div>
<SCRIPT LANGUAGE="javascript">
  INFO_EX = new TabControl('INFO_EX',0);
  AddTabItem(INFO_EX,'INFO_EX_0','tabDivINFO_EX_0','cntDivINFO_EX_0');
 if (window.top.StatFrame+''!='undefined')
 {
  var tmp_curtab=window.top.StatFrame.GetRegisterVar('INFO_EX');
  if (tmp_curtab!='')
   {
     SetActiveTab(INFO_EX,tmp_curtab);
   }
 }
 <%=b_SaleExchangeBean.adjustInputSize(new String[]{"dcpbm","dproduct","dsxz", "ddj","dbz"}, "form1", cyqkRows.length)%>
</SCRIPT>
   <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760"></table>
</td>
  </tr>

    <tr>
      <td>
        <table CELLSPACING=0 CELLPADDING=0 width="100%" align="center">
          <tr>
            <td class="td"><b>登记日期:</b><%=masterRow.get("czrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("czy")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
             <%
               if(!isEnd){
               String retun = "sumitForm("+Operate.POST_CONTINUE+")";
              %>
              <input name="button2"  style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存新增(N)">
              <pc:shortcut key="n" script="<%=retun%>" />
              <%
               }
               if(zt.equals("0"))
               {
                 if(!b_SaleExchangeBean.isReport){
                   String post ="sumitForm("+Operate.POST+");";
               %>
              <input name="btnback" style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script="<%=post%>" />
               <%}
               }
               %>
              <%
               if(isCanDelete&&!b_SaleExchangeBean.isReport){
               String del = "if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+");";
               String detNull = "sumitForm("+b_SaleExchangeBean.DEL_NULL+");";
              %>
              <input name="button3"  style="width:60" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value="删除(D)">
              <pc:shortcut key="d" script="<%=del%>" />
              <%
              }if(!b_SaleExchangeBean.isReport){
              %>
              <input name="btnback"  style="width:50" type="button" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script="backList();" /><%}%>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</form>
<script language="javascript">
function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
function sl_onchange(i, isBigUnit)
{
  var slObj = document.all['sl_'+i];
  var xsjObj = document.all['xsj_'+i];
  var djObj = document.all['dj_'+i];
  var jjeObj = document.all['jje_'+i];
  var hsblObj = document.all['truebl_'+i];
  var hsslObj = document.all['hssl_'+i];
  var obj = isBigUnit ? hsslObj : slObj;
  var showText = isBigUnit ? "输入的换算数量非法！" : "输入的数量非法！";//显示错误信息
  var changeObj = isBigUnit ? slObj : hsslObj;//所调用的控件
  if(obj.value=="")
    return;
  if(obj.value.trim()=="")
    return;
  if(isNaN(obj.value)){
    alert(showText);
    obj.focus();
    return;
  }
    if(djObj.value!="" && !isNaN(djObj.value))
      jjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(djObj.value));
    cal_tot('sl');
    cal_tot('jje');
    var tjjeObj = document.all['t_jje'];
    var dtjjeObj = document.all['dt_jje'];
    var zjeObj = document.all['zje'];
    zjeObj.value = formatPrice(parseFloat(tjjeObj.value) - parseFloat(dtjjeObj.value));
}
function dsl_onchange(i)
{
  var slObj = document.all['dsl_'+i];
  var xsjObj = document.all['dxsj_'+i];
  var djObj = document.all['ddj_'+i];
  var jjeObj = document.all['djje_'+i];
  var showText = "输入的数量非法！";//显示错误信息
  var changeObj =  slObj ;//所调用的控件
  if(slObj.value=="")
    return;
  if(slObj.value.trim()=="")
    return;
  if(isNaN(slObj.value)){
    alert(showText);
    slObj.focus();
    return;
  }
    if(djObj.value!="" && !isNaN(djObj.value))
      jjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(djObj.value));
    dcal_tot('dsl');
    dcal_tot('djje');
    var tjjeObj = document.all['t_jje'];
    var dtjjeObj = document.all['dt_jje'];
    var zjeObj = document.all['zje'];
    zjeObj.value = formatPrice(parseFloat(tjjeObj.value) - parseFloat(dtjjeObj.value));
}
function dj_onchange(i)
{
  var slObj = document.all['sl_'+i];
  var xsjObj = document.all['xsj_'+i];
  var djObj = document.all['dj_'+i];
  var jjeObj = document.all['jje_'+i];
  var obj = djObj;
  var showText = "输入的单价非法！";
  var changeObj =  djObj ;
  if(obj.value=="")
    return;
  if(isNaN(obj.value)){
    alert(showText);
    obj.focus();
    return ;
  }
  if(slObj.value!="" && !isNaN(slObj.value))
    jjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(djObj.value));
  cal_tot('jje');
  var tjjeObj = document.all['t_jje'];
  var dtjjeObj = document.all['dt_jje'];
  var zjeObj = document.all['zje'];
  zjeObj.value = formatPrice(parseFloat(tjjeObj.value) - parseFloat(dtjjeObj.value));
}
function ddj_onchange(i)
{
  var slObj = document.all['dsl_'+i];
  var xsjObj = document.all['dxsj_'+i];
  var djObj = document.all['ddj_'+i];
  var jjeObj = document.all['djje_'+i];
  var obj = djObj;
  var showText = "输入的单价非法！";
  var changeObj =  djObj ;
  if(obj.value=="")
    return;
  if(isNaN(obj.value)){
    alert(showText);
    obj.focus();
    return ;
  }
  if(slObj.value!="" && !isNaN(slObj.value))
    jjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(djObj.value));
  dcal_tot('djje');
  var tjjeObj = document.all['t_jje'];
  var dtjjeObj = document.all['dt_jje'];
  var zjeObj = document.all['zje'];
  zjeObj.value = formatPrice(parseFloat(tjjeObj.value) - parseFloat(dtjjeObj.value));
}
function cal_tot(type)
{
  var tmpObj;
  var tot=0;
  for(i=0; i<<%=detailRows.length%>; i++)
  {
    if(type == 'sl')
      tmpObj = document.all['sl_'+i];
    else if(type == 'jje')
      tmpObj = document.all['jje_'+i];
    else
      return;
    if(tmpObj.value!="" && !isNaN(tmpObj.value))
      tot += parseFloat(tmpObj.value);
  }
  if(type == 'sl')
    document.all['t_sl'].value = formatQty(tot);
  else if(type == 'jje')
    document.all['t_jje'].value = formatPrice(tot);
}
function dcal_tot(type)
{
  var tmpObj;
  var tot=0;
  for(i=0; i<<%=cyqkRows.length%>; i++)
  {
    if(type == 'dsl')
      tmpObj = document.all['dsl_'+i];
    else if(type == 'djje')
      tmpObj = document.all['djje_'+i];
    else
      return;
    if(tmpObj.value!="" && !isNaN(tmpObj.value))
      tot += parseFloat(tmpObj.value);
  }
  if(type == 'dsl')
    document.all['dt_sl'].value = formatQty(tot);
  else if(type == 'djje')
    document.all['dt_jje'].value = formatPrice(tot);
}

 function prodsMultiSelect(frmName, srcVar)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=500 height=400 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "../sale_xixing/import_apply_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
 function goodsMultiSelect(frmName, srcVar, storeid)
  {
    var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=570 top=0 left=0";
    var winName= "GoodsProdSelector";
    paraStr = "../sale/lading_sel_product.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&curID="+storeid;
    newWin =window.open(paraStr,winName,winopt);
    newWin.focus();
  }
function OrderSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
  {
   var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=800 height=600  top=0 left=0";
   var winName= "SingleOrderSelector";
   paraStr = "../sale/import_order.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+curID;
   if(methodName+'' != 'undefined')
     paraStr += "&method="+methodName;
   if(notin+'' != 'undefined')
     paraStr += "&notin="+notin;
   newWin =window.open(paraStr,winName,winopt);
   newWin.focus();
  }
function propertiesInput(obj,cpid,i)
  {
    PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i, 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value, 'sumitForm(<%=b_SaleExchangeBean.DETAIL_CHANGE%>,'+i+')');
  }
function checkdwst()
  {
  if(form1.storeid.value=='')
  {
    alert('请选择仓库');
    return;
  }
  OrderMultiSelect('form1','srcVar=importOrder&dwtxid='+form1.dwtxid.value+'&khlx='+form1.khlx.value+'&storeid='+form1.storeid.value+'&djlx='+form1.djlx.value+'&personid='+form1.personid.value+'&jsfsid='+form1.jsfsid.value+'&sendmodeid='+form1.sendmodeid.value)
}
function OrderMultiSelect(frmName, srcVar, methodName,notin)
{
     var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
     var winName= "OrdersSelector";
     paraStr = "../sale_xixing/import_order.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
     if(methodName+'' != 'undefined')
       paraStr += "&method="+methodName;
     if(notin+'' != 'undefined')
     paraStr += "&notin="+notin;
     openSelectUrl(paraStr, winName, winopt2);
}
function checkdwtxid()
{
  if(form1.dwtxid.value=='')
  {
    alert('请选择购货单位');
    return;
  }
  if(form1.storeid.value=='')
  {
    alert('请选择仓库');
    return;
  }
  if(form1.personid.value=='')
  {
    alert('请选择业务员');
    return;
  }
  if(form1.jsfsid.value=='')
  {
    alert('请选择结算方式');
    return;
  }
  MultiSelectOrder('form1','srcVar=importOrderproduct&dwtxid='+form1.dwtxid.value+'&storeid='+form1.storeid.value+'&djlx=1&personid='+form1.personid.value+'&jsfsid='+form1.jsfsid.value)
}
function MultiSelectOrder(frmName, srcVar, methodName,notin)
{
  var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=700 height=600 top=0 left=0";
  var winName= "OrdersSelector";
  paraStr = "../sale_xixing/select_order_product.jsp?operate=0&srcFrm="+frmName+"&"+srcVar;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  newWin =window.open(paraStr,winName,winopt);
  newWin.focus();
}
//产品编码
function productCodeSelect(obj, i)
{
    SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=wzdjid_'+i+'&srcVar=cpid_'+i,'fieldVar=wzdjid&fieldVar=cpid&storeid='+form1.storeid.value,obj.value,'sumitForm(<%=b_SaleExchangeBean.DETAIL_CHANGE%>,'+i+')');
}
//产品名称
function productNameSelect(obj, i)
{
    SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=wzdjid_'+i+'&srcVar=cpid_'+i,'fieldVar=wzdjid&fieldVar=cpid&storeid='+form1.storeid.value,obj.value,'sumitForm(<%=b_SaleExchangeBean.DETAIL_CHANGE%>,'+i+')');
}
//产品编码
function dproductCodeSelect(obj, i)
{
    SaleProdCodeChange(document.all['prod'], obj.form.name,'srcVar=dwzdjid_'+i+'&srcVar=dcpid_'+i,'fieldVar=wzdjid&fieldVar=cpid&storeid='+form1.storeid.value,obj.value,'sumitForm(<%=b_SaleExchangeBean.DETAIL_EXCHANGE%>,'+i+')');
}
//产品名称
function dproductNameSelect(obj, i)
{
    SaleProdNameChange(document.all['prod'], obj.form.name,'srcVar=dwzdjid_'+i+'&srcVar=dcpid_'+i,'fieldVar=wzdjid&fieldVar=cpid&storeid='+form1.storeid.value,obj.value,'sumitForm(<%=b_SaleExchangeBean.DETAIL_EXCHANGE%>,'+i+')');
}
</script>
<%//&#$
if(b_SaleExchangeBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
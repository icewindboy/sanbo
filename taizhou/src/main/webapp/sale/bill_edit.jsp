<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%>
<%
  engine.erp.sale.B_Sale_Gentd b_Sale_GentdBean = engine.erp.sale.B_Sale_Gentd.getInstance(request);
  String pageCode = "sale_order_list";

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
  location.href='lading_bill.jsp';
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
  if(document.form1.hkts.value=="")
    return;
  else
  {
    document.form1.hkrq.value=addDate(document.form1.tdrq.value,parseFloat(document.form1.hkts.value));
  }
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
//运输单位
function TransportChange(obj)
{
    CustomerCodeChange('4',document.all['prod'], obj.form.name,'srcVar=dwdm2&srcVar=dwt_dwtxId&srcVar=dwmc2','fieldVar=dwdm&fieldVar=dwtxId&fieldVar=dwmc',obj.value);
}
function TransportNameChange(obj)
{
  CustomerNameChange('4',document.all['prod'], obj.form.name,'srcVar=dwdm2&srcVar=dwt_dwtxId&srcVar=dwmc2','fieldVar=dwdm&fieldVar=dwtxId&fieldVar=dwmc',obj.value);
}
</script>
<%
  String retu = b_Sale_GentdBean.doService(request, response);
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
  engine.project.LookUp saleOrderBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_ORDER_GOODS);//销售合同货物
  engine.project.LookUp creditBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP_CREDIT);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性
  engine.project.LookUp salePriceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_PRICE);
  engine.project.LookUp sendModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SEND_MODE);//发货方式


  String curUrl = request.getRequestURL().toString();

  EngineDataSet ds = b_Sale_GentdBean.getMaterTable();
  EngineDataSet list = b_Sale_GentdBean.getDetailTable();

  HtmlTableProducer masterProducer = b_Sale_GentdBean.masterProducer;
  HtmlTableProducer detailProducer = b_Sale_GentdBean.detailProducer;

  creditBean.regData(ds,"dwtxid");
  RowMap masterRow = b_Sale_GentdBean.getMasterRowinfo();//主表一行
  RowMap[] detailRows= b_Sale_GentdBean.getDetailRowinfos();//从表多行
  String zt=masterRow.get("zt");//0.初始化,1.已开提单(已经审核),4.作废,9.出库
  if(b_Sale_GentdBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  String djlx=masterRow.get("djlx");
  sendModeBean.regData(ds, "sendmodeid");

  boolean isEnd =  b_Sale_GentdBean.isReport||b_Sale_GentdBean.isApprove || (!b_Sale_GentdBean.masterIsAdd() && !zt.equals("0"));

  boolean isCanDelete = !isEnd && !b_Sale_GentdBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);
  isEnd = isEnd || !(b_Sale_GentdBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段

  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=edline" : "class=edFocused";
  String prodClass = detailClass;
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  String prodreanonly = readonly;

  String tdlx=djlx.equals("1")?"提货单":"退货单";
  boolean count=list.getRowCount()==0?true:false;
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
  RowMap dwpRow =corpBean.getLookupRow(masterRow.get("dwt_dwtxId"));
  boolean canedt = zt.equals("0")||zt.equals("1")||zt.equals("2");
  String djandjjeClass = canedt ?"class=edFocused_r": "class=ednone_r" ;
  String dwtxClass = count? "class=edbox":"class=edline";
  String dwtxRead = count? " " : "readonly";
  boolean mustConversion = b_Sale_GentdBean.conversion;//是否需要强制转换
  String djhclass = zt.equals("2")?"class=edbox":"class=edline";
  String djhreadonly = zt.equals("2")?"":"readonly";
  String isnet = masterRow.get("isnet");
  String title= isnet.equals("1")?"来料加工货物":"销售货物";

%>
<BODY id="docbody" oncontextmenu="window.event.returnValue=true"  onLoad="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td>
       <table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab"><%=title%></td>
          </tr>
        </table>
  <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0" width="100%">
      <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("tdrq").getFieldname()%></td>
                  <td  noWrap class="td">
                  <input type="text" name="tdrq" value='<%=masterRow.get("tdrq")%>' maxlength='10' style="width:110" <%=edClass%> onChange="tdrqOnchange()"  >
                  <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectTdrq()"></a><%}%>
                  </td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("tdbh").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="tdbh" value='<%=masterRow.get("tdbh")%>' maxlength='<%=ds.getColumn("tdbh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle">单据类型</td>
                  <td noWrap class="td">
                  <%if(!isEnd){%>
                  <INPUT TYPE="radio" NAME="djlx" VALUE="1" <%=(masterRow.get("djlx").equals("1"))?"checked":((masterRow.get("djlx").equals(""))?"checked":"")%> onClick="sumitForm(<%=b_Sale_GentdBean.DJLX_ONCHANGE%>,-1)">提货
                  <INPUT TYPE="radio" NAME="djlx" VALUE="-1" <%=(masterRow.get("djlx").equals("-1"))?"checked":""%>  onClick="sumitForm(<%=b_Sale_GentdBean.DJLX_ONCHANGE%>,-1)">退货
                  <%}else{%><%=(masterRow.get("djlx").equals("1"))?"提货":"退货"%><%}%>
                  </td>
                  <td  noWrap class="tdTitle">发货方式</td>
                  <td  noWrap class="td">
                  <%RowMap sendRow = sendModeBean.getLookupRow(masterRow.get("sendmodeid"));%>
                  <input type='hidden' name='sendmodeid' value='<%=masterRow.get("sendmodeid")%>' style='width:85' class='edline' readonly>
                  <input type='text'  value='<%=sendRow.get("sendmode")%>' style='width:85' class='edline' readonly>
                  </td>



        </tr>
        <tr>
        <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwtxid").getFieldname()%></td><%--购货单位--%>
                  <td colspan="3" noWrap class="td">
                    <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text" class="edline" style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>'  readonly>
                    <input type="text" class="edline" name="dwmc" onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:200"     readonly>
                  </td>
                  <td noWrap class="tdTitle">地址</td>
                  <td colspan="3" noWrap class="td">
                    <input type="text" class='edline' name="dz" onKeyDown="return getNextElement();" value='<%=masterRow.get("dz")%>' style="width:280"  readonly>
                  </td>
          </tr>
          <tr>
                  <td noWrap class="tdTitle">联系人</td>
                  <td noWrap class="td">
                    <input type="text"  class='edline' name="lxr" onKeyDown="return getNextElement();" value='<%=masterRow.get("lxr")%>' style="width:110"   readonly>
                  </td>
                  <td noWrap class="tdTitle">电话</td>
                  <td  noWrap class="td">
                    <input type="text"  class='edline' name="dh" onKeyDown="return getNextElement();" value='<%=masterRow.get("dh")%>' style="width:110"   readonly>
                  </td>
                 <td noWrap class="tdTitle">部门</td>
                 <td noWrap class="td"><%---为部门添加触发事件--%>
                 <input type='hidden' name='deptid' value='<%=masterRow.get("deptid")%>'>
                 <input type='text'  value='<%=deptBean.getLookupName(masterRow.get("deptid"))%>' style='width:110' class='edline' readonly >
                 </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>
                   <td  noWrap class="td">
                  <input type='hidden' name='personid' value='<%=masterRow.get("personid")%>'>
                  <input type='text'   value='<%=personBean.getLookupName(masterRow.get("personid"))%>' style='width:110' class='edline' readonly>
                 </td>


      </tr>
      <tr>
                <td   noWrap class="tdTitle"><%=masterProducer.getFieldInfo("storeid").getFieldname()%></td>
                <td  noWrap class="td">
                  <%
                    String st = "sumitForm("+b_Sale_GentdBean.SELECT_STORE+")";
                  %>
                  <%if(isEnd||!count) out.print("<input type='hidden' name='storeid' value='"+masterRow.get("storeid")+"'><input type='text' value='"+storeBean.getLookupName(masterRow.get("storeid"))+"' style='width:110' class='edline' readonly>");
                  else {%>
                  <pc:select name="storeid" addNull="1" style="width:110" onSelect="<%=st%>">
                    <%=storeBean.getList(masterRow.get("storeid"))%> </pc:select>
                  <%}%>
                  </td>
                  <td noWrap class="tdTitle">客户类型</td>
                  <td width="120" class="td">
                  <input  name='khlx' type='text' value='<%=masterRow.get("khlx")%>' style='width:110' class='edline' readonly>
                 </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("hkts").getFieldname()%></td>
                  <td noWrap class="td">
                  <input type="text" name="hkts" value='<%=masterRow.get("hkts")%>' maxlength='<%=ds.getColumn("hkts").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" onchange="hktsOnchange()" <%=readonly%>>
                  </td>
                 <td  noWrap class="tdTitle">结算方式</td>
                  <td  noWrap class="td">
                   <input type='hidden' name='jsfsid' value='<%=masterRow.get("jsfsid")%>'>
                   <input type='text'   value='<%=balanceModeBean.getLookupName(masterRow.get("jsfsid"))%>' style='width:110' class='edline' readonly >
                   </td>
      </tr>
      <tr>
                <td   noWrap class="tdTitle">运费单价(元/公斤)</td>
                <td   noWrap class="td">
                  <%
                    String yfdj = masterRow.get("yfdj");
                     if(isEnd||!count){
                     //out.print(khlx.equals("A")?"A":"C");
                     out.print("<input  name='yfdj' type='text' value='"+masterRow.get("yfdj")+"' style='width:110' class='edline' readonly>");
                    }else{%>
                   <input type="text" name="yfdj" value='<%=masterRow.get("yfdj")%>' maxlength='10' style="width:110" <%=edClass%>  onKeyDown="return getNextElement();"<%=readonly%>>
                 <%}%>
                </td>
                <td   noWrap class="tdTitle"><%=masterProducer.getFieldInfo("yf").getFieldname()%></td>
                <td   noWrap class="td">
                  <%
                    String yf = masterRow.get("yf");
                     if(isEnd||!count){
                     //out.print(khlx.equals("A")?"A":"C");
                     out.print("<input  name='yf' type='text' value='"+masterRow.get("yf")+"' style='width:110' class='edline' readonly>");
                    }else{%>
                    <input type="text" name="yf" value='<%=masterRow.get("yf")%>' maxlength='10' style="width:110" <%=edClass%>  onKeyDown="return getNextElement();"<%=readonly%>>
                <%}%>
               </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("hkrq").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="hkrq" value='<%=masterRow.get("hkrq")%>' maxlength='10' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" onChange="checkDate(this);hkrqOnchange()">
                  <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.hkrq);hkrqOnchange()"></a><%}%></td>




       </tr>
       <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwt_dwtxId").getFieldname()%></td>
                  <td noWrap class="td" colspan="3">
                    <input type="hidden" name="dwt_dwtxId" value='<%=masterRow.get("dwt_dwtxId")%>'>
                    <input type="text" <%=detailClass%> style="width:70" onKeyDown="return getNextElement();" name="dwdm2" value='<%=dwpRow.get("dwdm")%>' onchange="TransportChange(this)" <%=readonly%>>
                    <input type="text" <%=detailClass%> name="dwmc2" onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(masterRow.get("dwt_dwtxId"))%>' style="width:200"   onchange="TransportNameChange(this)" <%=readonly%>>
                    <%if(!isEnd){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="TransportSingleSelect('form1','srcVar=dwt_dwtxId&srcVar=dwdm2&srcVar=dwmc2','fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc',form1.dwt_dwtxId.value)">
                    <%}%>
                   </td>
                <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("ddfy").getFieldname()%></td>
                <td   noWrap class="td"><input type="text" name="ddfy" value='<%=masterRow.get("ddfy")%>' maxlength='10' style="width:110" <%=edClass%>  onKeyDown="return getNextElement();"<%=readonly%>></td>

       </tr>
       </tr>
       </tr>
       <tr>
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
                %>

                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                        <input class="edFocused_r"  name="tCopyNumber" value="1" title="拷贝(ALT+A)" size="2" maxlength="2" onChange=" if ( isNaN(this.value) ) this.value=1">
                        </td>
                        <td height='20' nowrap><%=detailProducer.getFieldInfo("hthwid").getFieldname()%></td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td height='20' nowrap>规格属性</td>
                        <td nowrap>换算数量</td>
                        <td height='20' nowrap>换算单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("sl").getFieldname()%></td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap><%=detailProducer.getFieldInfo("xsj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("xsje").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("zk").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("dj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("jje").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("bz").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("ckrq").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("sthssl").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("stsl").getFieldname()%></td>
                        <td nowrap>未出库数量</td>



                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%
                      propertyBean.regData(list,"dmsxid");

                      prodBean.regData(list,"cpid");
                      saleOrderBean.regData(list,"tdid");
                      salePriceBean.regData(list,"hthwid");
                      BigDecimal t_sl = new BigDecimal(0), t_xsje = new BigDecimal(0), t_jje = new BigDecimal(0),t_hssl = new BigDecimal(0);
                      int i=0;
                      RowMap detail = null;
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String hssl = detail.get("hssl");
                        String sl = detail.get("sl");
                        String xsje = detail.get("xsje");
                        String jje = detail.get("jje");
                        String stsl = detail.get("stsl");
                        double dstsl = Double.parseDouble(stsl.equals("")?"0":stsl);
                        double dhssl=0;
                        double dsl=0;
                        double dxsje=0;
                        double djje=0;
                        String wtsl = "";
                        if(b_Sale_GentdBean.isDouble(hssl))
                        {
                          dhssl=Double.parseDouble(hssl);
                          if(masterRow.get("djlx").equals("-1")&&dhssl<0)
                          {
                            dhssl=-1*dhssl;
                          }
                          t_hssl = t_hssl.add(new BigDecimal(dhssl));
                        }
                        if(b_Sale_GentdBean.isDouble(sl))
                        {
                          dsl=Double.parseDouble(sl);
                          if(masterRow.get("djlx").equals("-1")&&dsl<0)
                          {
                            dsl=-1*dsl;
                          }
                          t_sl = t_sl.add(new BigDecimal(dsl));
                        }
                        if(b_Sale_GentdBean.isDouble(xsje))
                        {
                          dxsje=Double.parseDouble(xsje);
                          if(masterRow.get("djlx").equals("-1")&&dxsje<0)
                          {
                            dxsje=-1*dxsje;
                          }
                          t_xsje = t_xsje.add(new BigDecimal(dxsje));
                        }
                        if(b_Sale_GentdBean.isDouble(jje))
                        {
                          djje=Double.parseDouble(jje);
                          if(masterRow.get("djlx").equals("-1")&&djje<0)
                          {
                            djje=-1*djje;
                          }
                          t_jje = t_jje.add(new BigDecimal(djje));
                        }
                        if(!detail.get("hthwid").equals(""))
                          {
                          prodClass="class=ednone";
                          prodreanonly="readonly";
                          }else
                          {
                            prodClass = detailClass;
                            prodreanonly = readonly;
                          }
                          RowMap propertyRow = propertyBean.getLookupRow(detail.get("dmsxID"));
                          wtsl = engine.util.Format.formatNumber(dsl-dstsl,"#00.00");


                     %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                         <iframe id="prod_<%=i%>" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
                          <%if(!isEnd){%>
                          <%if(detail.get("hthwid").equals("")){%><img style='cursor:hand' src='../images/select_prod.gif' border=0 onClick="if(form1.khlx.value==''){alert('请选择客户类型');return}SaleProdSingleSelect('form1','srcVar=wzdjid_<%=i%>&srcVar=xsj_<%=i%>&srcVar=cpid_<%=i%>&storeid='+form1.storeid.value,'fieldVar=wzdjid&fieldVar=xsj&fieldVar=cpid','','sumitForm(<%=b_Sale_GentdBean.DETAIL_CHANGE%>,-1)')"><%}%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <input name="image" class="img" type="image" title="复制当前行" onClick="sumitForm(<%=b_Sale_GentdBean.DETAIL_COPY%>,<%=i%>)" src="../images/copyadd.gif" border="0">
                          <%}%></td>
                        <td class="td" nowrap>
                        <%
                          RowMap productRow = prodBean.getLookupRow(detail.get("cpid"));
                          String isprops=productRow.get("isprops");
                          String cpid = detail.get("cpid");
                          String hsbl = productRow.get("hsbl");
                          detail.put("hsbl", hsbl);
                          //RowMap prodRow = salePriceBean.getLookupRow(detail.get("wzdjid"));
                          RowMap  saleOrderRow= saleOrderBean.getLookupRow(detail.get("hthwid"));
                          boolean bh = detail.get("hthwid").equals("");
                          String instrstyle = bh?"class=edFocused_r": "class=ednone_r" ;
                        %>
                           <input type='hidden' id='hsbl_<%=i%>' name='hsbl_<%=i%>' value='<%=hsbl%>'>
                           <input type='hidden' id='truebl_<%=i%>' name='truebl_<%=i%>' value=''>
                           <input type='hidden' id='hthwid_<%=i%>' name='hthwid_<%=i%>' value='<%=detail.get("hthwid")%>'>
                           <input type='hidden' id='cpid_<%=i%>' name='cpid_<%=i%>' value='<%=detail.get("cpid")%>'>
                        <input type="text"  style="width:80" class='ednone' onKeyDown="return getNextElement();" maxlength='13' value='<%=saleOrderRow.get("htbh")%>'  readonly>
                        </td><!--合同编号-->
                        <td class="td" nowrap><input type="text" <%=prodClass%>  onKeyDown="return getNextElement();" name="cpbm_<%=i%>" value='<%=productRow.get("cpbm")%>' onchange="productCodeSelect(this,<%=i%>)" <%=prodreanonly%>></td><!--产品编码(存货代码)--->
                        <td class="td" nowrap>
                          <input type="text" <%=prodClass%>  onKeyDown="return getNextElement();" id="product_<%=i%>" name="product_<%=i%>" value='<%=productRow.get("product")%>'  onchange="productNameSelect(this,<%=i%>)"   <%=prodreanonly%>></td>
                        </td><!--品名 规格(存货名称与规格)--->
                        <td class="td" nowrap>
                        <input type="text"  <%=djandjjeClass%>  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="propertiesInput(this,form1.cpid_<%=i%>.value,<%=i%>)"  <%=canedt?"":" readonly"%> >
                        <input type="HIDDEN"  id="dmsxid_<%=i%>"  name="dmsxid_<%=i%>"  value='<%=detail.get("dmsxid")%>' <%=bh?"":"readonly"%> >
                        <%if(canedt){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if('<%=detail.get("cpid")%>'==''){alert('请先输入产品');return;}if('<%=isprops%>'=='0'){alert('该产品无规格属性!');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>','<%=cpid%>')"><%}%>
                        <%if(!isEnd&&!isprops.equals("0")){%><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';"><%}%>
                        </td>
                        <td class="td" nowrap><input type="text" <%=djandjjeClass%>  onKeyDown="return getNextElement();" id="hssl_<%=i%>" name="hssl_<%=i%>" value='<%=detail.get("hssl")%>' maxlength='<%=list.getColumn("hssl").getPrecision()%>' onchange="sl_onchange(<%=i%>, true)"  <%=canedt?"":" readonly"%>></td><!--换算数量--->
                        <td class="td" nowrap>
                            <input type="text" style="width:20" class='ednone_r'  onKeyDown="return getNextElement();" id="hsdw_<%=i%>" name="hsdw_<%=i%>" value='<%=productRow.get("hsdw")%>'  readonly></td>
                        <td class="td" nowrap><input type="text" <%=djandjjeClass%>  onKeyDown="return getNextElement();" id="sl_<%=i%>" name="sl_<%=i%>" value='<%=detail.get("sl")%>' maxlength='<%=list.getColumn("sl").getPrecision()%>' onblur="sl_onchange(<%=i%>, false)" <%=canedt?"":" readonly"%>></td><!--数量-->
                        <td class="td" nowrap><%=productRow.get("jldw")%></td><!--计量单位--->
                        <td class="td" nowrap><input type="text" class='ednone_r'   onKeyDown="return getNextElement();" id="xsj_<%=i%>" name="xsj_<%=i%>" value='<%=detail.get("xsj")%>' readonly></td><!--销售价-->
                        <td class="td" nowrap><input type="text" class='ednone_r'   onKeyDown="return getNextElement();" id="xsje_<%=i%>" name="xsje_<%=i%>" value='<%=String.valueOf(dxsje)%>' readonly></td><!--销售金额-->
                        <td class="td" nowrap><input type="text" <%=detailClass_r%>   onKeyDown="return getNextElement();" id="zk_<%=i%>" name="zk_<%=i%>" value='<%=detail.get("zk")%>'  onchange="dj_onchange(<%=i%>, true)" <%=canedt?"":" readonly"%> ></td><!--折扣-->
                        <td class="td" nowrap>
                            <input type="text" <%=djandjjeClass%>   onKeyDown="return getNextElement();" id="dj_<%=i%>" name="dj_<%=i%>" value='<%=detail.get("dj")%>' onchange="dj_onchange(<%=i%>, false)"  <%=canedt?"":" readonly"%> >
                            <input type="HIDDEN" id="wzdjid_<%=i%>" name="wzdjid_<%=i%>" value='<%=detail.get("wzdjid")%>' >
                        </td><!--单价-->
                        <td class="td" nowrap><input type="text" class='ednone_r'  onKeyDown="return getNextElement();" id="jje_<%=i%>" name="jje_<%=i%>" value='<%=String.valueOf(djje)%>'  readonly></td><!--净金额-->
                        <td class="td" nowrap align="left"><input type="text" <%=detailClass%>  onKeyDown="return getNextElement();" name="bz_<%=i%>" id="bz_<%=i%>" value='<%=detail.get("bz")%>' maxlength='100'  <%=readonly%> ></td><!--备注-->
                        <td class="td" nowrap align="right"><%=detail.get("ckrq") %></td>
                        <td class="td" nowrap align="right"><%=detail.get("sthssl") %></td><!--实提换算数量-->
                        <td class="td" nowrap align="right"><%=detail.get("stsl") %></td><!--出库数量-->
                        <td class="td" nowrap align="right"><%=wtsl %></td>

                        <%
                          FieldInfo[] bakFields = detailProducer.getBakFieldCodes();
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
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td>

                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap> 合计 </td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap><input id="t_hssl" name="t_hssl" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_hssl.toString(),loginBean.getSumFormat())%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap><input id="t_sl" name="t_sl" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_sl.toString(),loginBean.getSumFormat())%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap><input id="t_xsje" name="t_xsje" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_xsje.toString(),loginBean.getPriceFormat())%>'  readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" nowrap><input id="t_jje" name="t_jje" type="text" class='ednone_r' style="width:100%" value='<%=engine.util.Format.formatNumber(t_jje.toString(),loginBean.getPriceFormat())%>'  readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                    </table></div>
                    <SCRIPT LANGUAGE="javascript">rowinfo = new RowControl();
                    <%for(int k=0; k<i; k++)
                      {
                        out.print("AddRowItem(rowinfo,'rowinfo_"+k+"');");
                      }%>AddRowItem(rowinfo,'rowinfo_end');InitRowControl(rowinfo);</SCRIPT></td>
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
            <td class="td"><b>登记日期:</b><%=masterRow.get("czrq")%></td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b><%=masterRow.get("czy")%></td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
              <%
                String post ="sumitForm("+Operate.POST+");";
                String detNull = "sumitForm("+b_Sale_GentdBean.DEL_NULL+");";
              %>
              <input name="btnback" style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
              <pc:shortcut key="s" script="<%=post%>" />

              <input name="button3"  style="width:110" type="button" class="button" title="删除数量为空行(R)" value="删除数量为空行(R)" onClick="<%=detNull%>">
              <pc:shortcut key="r" script="<%=detNull%>" />

              <input name="btnback"  style="width:50" type="button" class="button" onClick="window.close()" value="返回(C)">
              <pc:shortcut key="c" script="window.close()" />

            </td>
          </tr>
        </table></td>
    </tr>
  </table>

</form>
<script language="javascript">
  initDefaultTableRow('tableview1',1);
  <%=b_Sale_GentdBean.adjustInputSize(new String[]{"cpbm","product","sxz","hssl","sl","xsj","xsje", "zk","dj","jje","bz"}, "form1", detailRows.length)%>
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
    var slObj = document.all['sl_'+i];

    var xsjObj = document.all['xsj_'+i];
    var xsjeObj = document.all['xsje_'+i];
    var djObj = document.all['dj_'+i];
    var jjeObj = document.all['jje_'+i];
    //var hsblObj = document.all['hsbl_'+i];
    var hsblObj = document.all['truebl_'+i];
    var hsslObj = document.all['hssl_'+i];

    var yfObj = document.all['yf'];
    var yfdjObj = document.all['yfdj'];



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
    if(yfdjObj.value=="")
      yfdjObj.value="0";
    /*if(parseFloat(obj.value)<0){
      alert(showText);
      obj.focus();
      return;
    }*/
    <%
    if(mustConversion){
    //要强制转换
    %>
    changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
    <%}else {%>
      if(hsslObj.value==""||slObj.value=="")
        changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
      <%}%>
    //changeObj.value = formatPrice(isBigUnit ? (parseFloat(hsslObj.value)*parseFloat(hsblObj.value)) : (parseFloat(slObj.value)/parseFloat(hsblObj.value)));
    if(xsjObj.value!="" && !isNaN(xsjObj.value))
      xsjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(xsjObj.value));
    if(djObj.value!="" && !isNaN(djObj.value))
      jjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(djObj.value));
    cal_tot('sl');
    cal_tot('xsje');
    cal_tot('jje');
    cal_tot('hssl');
    //--------------------------------------------
    var t_slObj = document.all['t_sl'];
    yfObj.value =parseFloat(yfdjObj.value)*parseFloat(t_slObj.value);
  }
  function dj_onchange(i, isRebate)
  {
    var slObj = document.all['sl_'+i];
    var zkObj = document.all['zk_'+i];
    var xsjObj = document.all['xsj_'+i];
    var xsjeObj = document.all['xsje_'+i];
    var djObj = document.all['dj_'+i];
    var jjeObj = document.all['jje_'+i];

    var obj = isRebate ? zkObj : djObj;
    var showText = isRebate ? "输入的折扣非法！" : "输入的单价非法！";
    var changeObj = isRebate ? djObj : zkObj;
    if(obj.value=="")
      return;
    if(isNaN(obj.value)){
      alert(showText);
      obj.focus();
      return ;
    }
    changeObj.value = formatPrice(isRebate ? (parseFloat(xsjObj.value)*parseFloat(zkObj.value)/100) : (parseFloat(djObj.value)/parseFloat(xsjObj.value)*100));
    if(slObj.value!="" && !isNaN(slObj.value))
      xsjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(xsjObj.value));
    if(slObj.value!="" && !isNaN(slObj.value))
      jjeObj.value = formatPrice(parseFloat(slObj.value) * parseFloat(djObj.value));
    cal_tot('xsje');
    cal_tot('jje');
  }
  function cal_tot(type)
  {
    var tmpObj;
    var tot=0;
    for(i=0; i<<%=detailRows.length%>; i++)
    {
      if(type == 'sl')
        tmpObj = document.all['sl_'+i];
      else if(type == 'xsje')
        tmpObj = document.all['xsje_'+i];
      else if(type == 'hssl')
        tmpObj = document.all['hssl_'+i];
      else if(type == 'jje')
        tmpObj = document.all['jje_'+i];
      else
        return;
      if(tmpObj.value!="" && !isNaN(tmpObj.value))
        tot += parseFloat(tmpObj.value);
    }
    if(type == 'sl')
      document.all['t_sl'].value = formatQty(tot);
    else if(type == 'xsje')
      document.all['t_xsje'].value = formatPrice(tot);
    else if(type == 'hssl')
      document.all['t_hssl'].value = formatQty(tot);
    else if(type == 'jje')
      document.all['t_jje'].value = formatPrice(tot);
  }
function propertiesInput(obj,cpid,i)
  {
    PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i, 'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value, 'sumitForm(<%=b_Sale_GentdBean.DETAIL_CHANGE%>,'+i+')');
  }
</script>
<%//&#$
if(b_Sale_GentdBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
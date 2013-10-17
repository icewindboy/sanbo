<%--
销售结算
2004-2-16增加了单位名称的模糊输入
--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList,engine.erp.finance.xixing.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
  String pageCode = "freight_account";
%>
<%
if(!loginBean.hasLimits(pageCode, request, response))
    return;
  B_SaleFreightAccount b_SaleFreightAccountBean  =  B_SaleFreightAccount.getInstance(request);
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp bankaccountBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK_ACCOUNT);
  engine.project.LookUp bankBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  engine.project.LookUp saleLadingBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_LADING_BILL);
  engine.project.LookUp saleCarryBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_CARRY_THING);//b_SaleFreightAccountBean.getBuyPriceBean(request);
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
  location.href='freight_account.jsp';
}
function customerCodeSelect(obj)
{
    CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value,'sumitForm(<%=b_SaleFreightAccountBean.DWTXID_CHANGE%>,-1)');
}
</script>

<%
  String retu = b_SaleFreightAccountBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_SaleFreightAccountBean.getMaterTable();
  EngineDataSet list = b_SaleFreightAccountBean.getDetailTable();//引用过来的数据集
  HtmlTableProducer masterProducer = b_SaleFreightAccountBean.masterProducer;
  //HtmlTableProducer detailProducer = b_SaleFreightAccountBean.detailProducer;
  RowMap masterRow = b_SaleFreightAccountBean.getMasterRowinfo();//主表一行
  RowMap[] detailRows= b_SaleFreightAccountBean.getDetailRowinfos();//从表多行
  ds.first();
  String zt=masterRow.get("zt");
  //&#$
  if(b_SaleFreightAccountBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
  }
  boolean isEnd =  b_SaleFreightAccountBean.isApprove || (!b_SaleFreightAccountBean.masterIsAdd() && !zt.equals("0"));
  //没有结束,在修改状态,并有删除权限
  boolean isCanDelete = !isEnd && !b_SaleFreightAccountBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);
  isEnd = isEnd || !(b_SaleFreightAccountBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));
  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String classType =  zt.equals("8") ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  String title = zt.equals("0") ? ("未审核") : ("已审核");
  bankBean.regData(ds, "yh");
  boolean count=list.getRowCount()==0?true:false;
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
  bankaccountBean.regConditionData("yhmc", new String[]{});
  bankBean.regData(ds, "yh");
  saleCarryBean.regData(list,"tdcyqkid");

%>
<BODY oncontextmenu="window.event.returnValue=true" onLoad="syncParentDiv('tableview1');">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">运费结算(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0" width="100%">
                <%corpBean.regData(ds,"dwtxid");%>
                  <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("rq").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="rq" value='<%=masterRow.get("rq")%>' maxlength='10' style="width:110" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();" <%=isEnd?"readonly":""%>>
                  <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.rq);"></a>
                  <%}%></td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("djh").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="djh" value='<%=masterRow.get("djh")%>' maxlength='<%=ds.getColumn("djh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="deptid" addNull="1" style="width:110">
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%></td>
                 </tr>
                 <tr>

                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwtxid").getFieldname()%></td><%--购货单位--%>
                  <td colspan="3" noWrap class="td">
                    <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text" <%=detailClass%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="customerCodeSelect(this)" <%=readonly%>>
                    <input type="text" name="dwmc"  onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:255" class="edline" readonly>
                    <%if(!isEnd){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc','fieldVar=dwtxid&fieldVar=dwmc',form1.dwtxid.value,'sumitForm(<%=b_SaleFreightAccountBean.DWTXID_CHANGE%>,-1)');">
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsdh").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="jsdh" value='<%=masterRow.get("jsdh")%>' maxlength='<%=ds.getColumn("jsdh").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"<%=readonly%>></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("je").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="je" value='<%=masterRow.get("je")%>' maxlength='<%=ds.getColumn("je").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                 </tr>
                 <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("zh").getFieldname()%></td>
                  <td noWrap class="td" >
                  <input type="text" name="zh" value='<%=masterRow.get("zh")%>' maxlength='<%=ds.getColumn("zh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  </td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("yh").getFieldname()%></td>
                  <td noWrap class="td" >
                  <input type="text" name="yh" value='<%=masterRow.get("yh")%>' maxlength='<%=ds.getColumn("yh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly>
                  </td>
                  <%--td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>
                  <td   noWrap class="td">
                  <%if(isEnd) out.print("<input type='text' name='xm' value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:110' class='edline' readonly><input type='hidden' name='personid' value='"+masterRow.get("personid")+"' style='width:110' class='edline' readonly>");
                  else {%>
                  <pc:select name="personid" addNull="1" style="width:110">
                    <%=personBean.getList(masterRow.get("personid"))%> </pc:select>
                   <%}%></td>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("jsfsid").getFieldname()%></td>
                  <td  noWrap class="td">
                  <%if(isEnd){String jsfsid=masterRow.get("jsfsid");out.print("<input type='text' value='"+balanceModeBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:110' class='edline' readonly>");
                    }else {%>
                    <pc:select name="jsfsid"  style="width:110">
                    <%=balanceModeBean.getList(masterRow.get("jsfsid"))%>
                    </pc:select>
                 <%}%></td--%>
                 </tr>
                 <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bz").getFieldname()%></td>
                  <td  class="td" colspan="3"><input type="text" align="left" <%=edClass%> name="bz" value='<%=masterRow.get("bz")%>' maxlength='<%=ds.getColumn("bz").getPrecision()%>' style="width:100%"  onKeyDown="return getNextElement();"<%=readonly%>></td>
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
                %>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                        <%if(!b_SaleFreightAccountBean.isReport){%>
                        <%if(!zt.equals("8")){%>
                        <input type="hidden" name="tdcyqkids" value="" onchange="sumitForm(<%=b_SaleFreightAccountBean.DETAIL_SALE_ADD%>,-1);">
                        <input name="image" class="img" type="image" title="新增(A)" onClick="selctTDHWOfLading()" src="../images/add_big.gif" border="0">
                        <pc:shortcut key="a" script="selctTDHWOfLading();" />
                        <%}%><%}%></td>
                        <td height='20' nowrap>销售单据号</td>
                        <td height='20' nowrap>起始地</td>
                        <td height='20' nowrap>目的地</td>
                        <td height='20' nowrap>运费</td>
                        <td height='20' nowrap>核销金额</td>
                      </tr>
                    <%
                      BigDecimal t_jje = new BigDecimal(0), t_jsje = new BigDecimal(0), t_tcj = new BigDecimal(0);
                      int i=0;
                      String[] widthName = new String[]{"jsje", "tcl", "tcj"};
                      int[] widthMin = new int[]{60, 60, 70};
                      int[] widths = b_SaleFreightAccountBean.getMaxStyleWidth(detailRows, widthName, widthMin);
                      RowMap detail = null;
                      RowMap cyqkRow = null;
                      list.first();
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String tdcyqkid= detail.get("tdcyqkid");
                        cyqkRow = saleCarryBean.getLookupRow(tdcyqkid);
                        String jsje = detail.get("jsje");
                        if(b_SaleFreightAccountBean.isDouble(jsje))
                          t_jsje = t_jsje.add(new BigDecimal(jsje));
                        String tcj = detail.get("tcj");
                        if(b_SaleFreightAccountBean.isDouble(tcj))
                          t_tcj= t_tcj.add(new BigDecimal(tcj));
                        BigDecimal bjsje =new BigDecimal(detail.get("jsje"));
                     %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(!b_SaleFreightAccountBean.isReport){%>
                          <%if(!zt.equals("8")){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%><%}%></td>
                        <td class="td" nowrap><%=cyqkRow.get("tdbh")%></td>
                        <td class="td" nowrap><%=cyqkRow.get("qsd")%></td>
                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'  style="width:<%=widths[0]%>"  value='<%=cyqkRow.get("mdd")%>'  readonly></td>
                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'  style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="fy_<%=i%>" name="fy__<%=i%>" value='<%=cyqkRow.get("fy")%>'  readonly></td>
                        <td class="td" nowrap align="right"><input type="text" <%=classType%>  style="width:100%" onKeyDown="return getNextElement();" id="jsje_<%=i%>" name="jsje_<%=i%>"     value='<%=detail.get("jsje")%>'   maxlength='<%=list.getColumn("jsje").getPrecision()%>'   <%=zt.equals("8")?"readonly":""%> ></td>
                      </tr>
                      <%list.next();
                      }
                      for(; i < 4; i++){
                  %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" align="right"><input id="t_jje" name="t_jje" type="text" class='ednone_r' style="width:<%=widths[0]%>" value='<%=t_jje%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td" align="right"><input id="t_jsje" name="t_jsje" type="text" class='ednone_r' style="width:<%=widths[0]%>" value='<%=t_jsje%>' readonly></td>
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
            <%if(!b_SaleFreightAccountBean.isReport){%>
            <%if(!zt.equals("8")){
            String quto = "sumitForm("+b_SaleFreightAccountBean.AUTO_CANCER+");";
            String po = "sumitForm("+Operate.POST+");";
             %>
             <input name="btnback" class="button" type="button" value="自动核销(W)" onClick="sumitForm(<%=b_SaleFreightAccountBean.AUTO_CANCER%>);" border="0">
             <pc:shortcut key="w" script="<%=quto%>" />
             <input name="btnback" type="button" class="button" onClick="sumitForm(<%=b_SaleFreightAccountBean.MASTER_DETAIL_POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script="<%=po%>" />
              <%}%>
              <input name="btnback" type="button" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script="backList();" />
              <%}%>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
  function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '<%=loginBean.getPriceFormat()%>');}
  function formatSum(srcStr){ return formatNumber(srcStr, '<%=loginBean.getSumFormat()%>');}
    function hxje_onchange(i)
    {
      var jsjeObj = document.all['jsje_'+i];
      var jjeObj = document.all['jje_'+i];
      //var bceObj = document.all['bce_'+i];
      var tclObj = document.all['tcl_'+i];
      var tcjObj = document.all['tcj_'+i];
        if(jsjeObj.value=="")
          return;
        if(isNaN(jsjeObj.value)){
          alert("输入的金额非法");
          jsjeObj.focus();
          return;
        }
        /*
        if((parseFloat(jsjeObj.value))<0)
        {
          alert("输入的金额非法,最大值为货款金额:"+jjeObj.value);
          jsjeObj.value=jjeObj.value;
        }

        if((parseFloat(Math.abs(jjeObj.value)) - Math.abs(parseFloat(jsjeObj.value)))<0)
        {
          alert("输入的金额非法,最大值为货款金额:"+jjeObj.value);
          jsjeObj.value=jjeObj.value;
        }
        */
        //bceObj.value=formatQty(parseFloat(jjeObj.value) - parseFloat(jsjeObj.value));
        tcjObj.value=formatQty(parseFloat(jsjeObj.value) * parseFloat(tclObj.value))*0.01;
        cal_tot('jsje');
        //cal_tot('tcj');
    }
     function cal_tot(type)
    {
      var tmpObj;
      var tcjObj;
      var tot=0;
      var ttcj=0;
      var tjje=0;
      //var tbce=0;
      for(i=0; i<<%=detailRows.length%>; i++)
      {
        if(type == 'jsje')
          tmpObj = document.all['jsje_'+i];
          tcjObj = document.all['tcj_'+i];
          tjjeObj = document.all['jje_'+i];
          //tbceObj = document.all['bce_'+i];
        if(tmpObj.value!="" && !isNaN(tmpObj.value))
          tot += parseFloat(tmpObj.value);
        if(tcjObj.value!="" && !isNaN(tcjObj.value))
          ttcj += parseFloat(tcjObj.value);
        if(tjjeObj.value!="" && !isNaN(tjjeObj.value))
          tjje += parseFloat(tjjeObj.value);
        //if(tbceObj.value!="" && !isNaN(tbceObj.value))
        //  tbce += parseFloat(tbceObj.value);
      }
      if(type == 'jsje')
      {
        document.all['t_jsje'].value = formatQty(tot);
        document.all['t_tcj'].value = formatQty(ttcj);
        document.all['t_jje'].value = formatQty(tjje);
        //document.all['t_bce'].value = formatQty(tbce);
      }
    }
function TdhwMultiSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
{
 var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
 var winName= "MultiProdSelector";
 paraStr = "../finance_xixing/import_chenyu_fy.jsp?operate=0&multi=1&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+curID;
 if(methodName+'' != 'undefined')
   paraStr += "&method="+methodName;
 if(notin+'' != 'undefined')
   paraStr += "&notin="+notin;
 newWin =window.open(paraStr,winName,winopt);
 newWin.focus();
}
function selctTDHWOfLading()
{
 TdhwMultiSelect('form1','srcVar=tdcyqkids','fieldVar=tdid','&dwtxid='+form1.dwtxid.value,"sumitForm(<%=b_SaleFreightAccountBean.DETAIL_SALE_ADD%>,-1)");
}
    function OrderSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
       {
        var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
        var winName= "SingleladingSelector";
        paraStr = "../finance/sale_balance_import_lading.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&dwtxid="+curID;
        if(methodName+'' != 'undefined')
          paraStr += "&method="+methodName;
        if(notin+'' != 'undefined')
          paraStr += "&notin="+notin;
        newWin =window.open(paraStr,winName,winopt);
        newWin.focus();
       }
    function selctbilloflading()
       {
         form1.selectedtdid.value='';
         OrderSingleSelect('form1','srcVar=selectedtdid','fieldVar=tdid',form1.dwtxid.value,"sumitForm(<%=b_SaleFreightAccountBean.IMPORT_TD%>,-1)");
   }
</script>
</script>
<%
//&#$
if(b_SaleFreightAccountBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
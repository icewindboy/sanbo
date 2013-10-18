<%--
销售结算
2004-2-16增加了单位名称的模糊输入
--%>
<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList,engine.erp.finance.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
  String pageCode = "sale_balance";
%>
<%
if(!loginBean.hasLimits(pageCode, request, response))
    return;
  B_SaleBalance b_SaleBalanceBean  =  B_SaleBalance.getInstance(request);

  //引用结算方式
  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  //引用往来单位
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  //引用部门
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  //引用产品
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  //引用人员信息
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  //BEAN_BANK_ACCOUNT
  engine.project.LookUp bankaccountBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK_ACCOUNT);
  //BEAN_BANK
  engine.project.LookUp bankBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK);
 //引用仓库信息
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  //销售提单货物
  engine.project.LookUp saleLadingBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_LADING_BILL);
  //销售价
  engine.project.LookUp buyPriceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//b_SaleBalanceBean.getBuyPriceBean(request);

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
  location.href='sale_balance.jsp';
}
function customerCodeSelect(obj)
{
    CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc',obj.value,'sumitForm(<%=b_SaleBalanceBean.DWTXID_CHANGE%>,-1)');
}
</script>

<%
  String retu = b_SaleBalanceBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_SaleBalanceBean.getMaterTable();
  EngineDataSet list = b_SaleBalanceBean.getDetailTable();//引用过来的数据集
  HtmlTableProducer masterProducer = b_SaleBalanceBean.masterProducer;
  HtmlTableProducer detailProducer = b_SaleBalanceBean.detailProducer;
  RowMap masterRow = b_SaleBalanceBean.getMasterRowinfo();//主表一行
  RowMap[] detailRows= b_SaleBalanceBean.getDetailRowinfos();//从表多行
  ds.first();
  String zt=masterRow.get("zt");
  //&#$
  if(b_SaleBalanceBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  String djxz=masterRow.get("djxz");
  boolean isEnd =  b_SaleBalanceBean.isApprove || (!b_SaleBalanceBean.masterIsAdd() && !zt.equals("0"));
  //没有结束,在修改状态,并有删除权限
  boolean isCanDelete = !isEnd && !b_SaleBalanceBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);
  isEnd = isEnd || !(b_SaleBalanceBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String classType =  zt.equals("8") ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String title = zt.equals("0") ? ("(未审核)") : ("(已审核)");
  bankBean.regData(ds, "yh");
  //boolean count=list.getRowCount()==0?true:false;
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
  bankaccountBean.regConditionData("yhmc", new String[]{});
  bankBean.regData(ds, "yh");
  String isnet = masterRow.get("isnet");
  String  title = isnet.equals("1")?"来料外加工单货物结算":"销售提单货物结算";
  int count = detailRows.length;

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
            <td class="activeVTab"><%=title%></td>
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
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("djxz").getFieldname()%></td>
                  <td noWrap class="td">
                  <%if(!isEnd){%><INPUT TYPE="radio" NAME="djxz" VALUE="1" <%=(masterRow.get("djxz").equals("1"))?"checked":((masterRow.get("djxz").equals(""))?"checked":"")%>>销售收款
                  <INPUT TYPE="radio" NAME="djxz" VALUE="-1" <%=(masterRow.get("djxz").equals("-1"))?"checked":""%>>销售退款
                  <%}else{%><input type='text' name='djxz' value='<%=(masterRow.get("djxz").equals("1"))?"销售收款":"销售退款"%>' style='width:110' class='edline' readonly><%}%></td>
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
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc','fieldVar=dwtxid&fieldVar=dwmc',form1.dwtxid.value,'sumitForm(<%=b_SaleBalanceBean.DWTXID_CHANGE%>,-1)');">
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
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>
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
                 <%}%></td>
                 </tr>
                 <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("bz").getFieldname()%></td>
                  <td  class="td" colspan="3"><input type="text" align="left" <%=edClass%> name="bz" value='<%=masterRow.get("bz")%>' maxlength='<%=ds.getColumn("bz").getPrecision()%>' style="width:100%"  onKeyDown="return getNextElement();"<%=readonly%>></td>
                  <td noWrap class="tdTitle">客户类型</td>
                  <td width="120" class="td">
                  <%String khlx = masterRow.get("khlx");if(isEnd){%><input type='text' name='khlx' value='<%=khlx.equals("A")?"A":"C"%>' style='width:110' class='edline' readonly><%}else{%>
                  <pc:select name="khlx" style="width:110" value="<%=khlx%>">
                    <pc:option value="A">A</pc:option> <pc:option value="C">C</pc:option>
                  </pc:select>
                  <%}%>
                </td>
                  <td noWrap class="tdTitle">数据来源</td>
                  <td width="120" class="td">
                  <%if(zt.equals("8")||count>0){%><input type='hidden' name='isnet' value='<%=isnet%>' style='width:110' class='edline' readonly><input type='text' name='' value='<%=isnet.equals("1")?"来料外加工单货物":"销售提单货物"%>' style='width:110' class='edline' readonly><%}else{%>
                  <pc:select name="isnet" style="width:110" value="<%=isnet%>">
                    <pc:option value="0">销售提单货物</pc:option> <pc:option value="1">来料外加工单货物</pc:option>
                  </pc:select>
                  <%}%>
                </td>
                </tr>
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="100%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                        <td nowrap width=10></td>
                        <td height='20' align="center" nowrap>
                        <%if(!b_SaleBalanceBean.isReport){%>
                        <%if(!zt.equals("8")){%>
                        <input type="hidden" name="tdhwids" value="" onchange="sumitForm(<%=b_SaleBalanceBean.DETAIL_SALE_ADD%>,-1);">
                        <input name="image" class="img" type="image" title="新增(A)" onClick="selctTDHWOfLading()" src="../images/add_big.gif" border="0">
                        <pc:shortcut key="a" script="selctTDHWOfLading();" />
                        <%}%><%}%></td>
                        <td height='20' nowrap>提单编号</td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td height='20' nowrap>单位</td>
                        <td height='20' nowrap>货款金额</td>
                        <td height='20' nowrap>未核销金额</td>
                        <td nowrap><%=detailProducer.getFieldInfo("jsje").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("tcl").getFieldname()%>(%)</td>
                        <td nowrap><%=detailProducer.getFieldInfo("tcj").getFieldname()%></td>
                        <td nowrap><%=detailProducer.getFieldInfo("yhrq").getFieldname()%></td>
                        <%detailProducer.printTitle(pageContext, "height='20'", true);%>
                      </tr>
                    <%
                      prodBean.regData(list,"cpid");
                      saleLadingBean.regData(list,"tdhwId");
                      BigDecimal t_jje = new BigDecimal(0), t_jsje = new BigDecimal(0), t_tcj = new BigDecimal(0);
                      int i=0;
                      String[] widthName = new String[]{"jsje", "tcl", "tcj"};
                      int[] widthMin = new int[]{60, 60, 70};
                      int[] widths = b_SaleBalanceBean.getMaxStyleWidth(detailRows, widthName, widthMin);
                      RowMap detail = null;
                      list.first();
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String jsje = detail.get("jsje");
                        if(b_SaleBalanceBean.isDouble(jsje))
                          t_jsje = t_jsje.add(new BigDecimal(jsje));
                        String tcj = detail.get("tcj");
                        if(b_SaleBalanceBean.isDouble(tcj))
                          t_tcj= t_tcj.add(new BigDecimal(tcj));
                        RowMap promap=prodBean.getLookupRow(detail.get("cpId"));
                        RowMap tdRow = saleLadingBean.getLookupRow(detail.get("tdhwId"));
                        BigDecimal bhwje =new BigDecimal(tdRow.get("jje").equals("")?"0":tdRow.get("jje"));
                        BigDecimal bjsje =new BigDecimal(detail.get("jsje"));
                        //BigDecimal bce =bhwje.subtract(bjsje);
                        BigDecimal bwhje =new BigDecimal(tdRow.get("jje").equals("")?"0":tdRow.get("jje")).subtract(new BigDecimal(tdRow.get("ssje").equals("")?"0":tdRow.get("ssje")));//未核销

                        t_jje=t_jje.add(new BigDecimal(tdRow.get("jje").equals("")?"0":tdRow.get("jje")));

                     %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%></td>
                        <td class="td" nowrap align="center">
                          <%if(!b_SaleBalanceBean.isReport){%>
                          <%if(!zt.equals("8")){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%><%}%></td>
                        <td class="td" nowrap><%=tdRow.get("tdbh")%></td>
                        <td class="td" nowrap><%=promap.get("cpbm")%></td><!--产品编码(存货代码)--->
                        <td class="td" nowrap><%=promap.get("product")%></td><!--品名 规格(存货名称与规格)--->
                        <td class="td" nowrap><%=promap.get("jldw")%></td><!--计量单位--->
                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'  style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="jje_<%=i%>" name="jje_<%=i%>" value='<%=tdRow.get("jje")%>'  readonly></td>

                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'  style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="whje_<%=i%>" name="whje__<%=i%>" value='<%=bwhje%>'  readonly></td>

                        <td class="td" nowrap align="right"><input type="text" <%=classType%>  style="width:100%" onKeyDown="return getNextElement();" id="jsje_<%=i%>" name="jsje_<%=i%>"     value='<%=detail.get("jsje")%>'   maxlength='<%=list.getColumn("jsje").getPrecision()%>'  onchange="hxje_onchange(<%=i%>)" <%=zt.equals("8")?"readonly":""%> ></td>
                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'  style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="tcl_<%=i%>" name="tcl_<%=i%>" value='<%=detail.get("tcl")%>' maxlength='<%=list.getColumn("tcl").getPrecision()%>' readonly></td>
                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'  align="right"     style="width:<%=widths[2]%>" onKeyDown="return getNextElement();" id="tcj_<%=i%>" name="tcj_<%=i%>"     value='<%=detail.get("tcj")%>'   maxlength='<%=list.getColumn("tcj").getPrecision()%>'   readonly></td>
                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'  style="width:<%=widths[2]%>" onKeyDown="return getNextElement();" id="yhrq_<%=i%>" name="yhrq_<%=i%>" value='<%=detail.get("yhrq")%>' maxlength='<%=list.getColumn("yhrq").getPrecision()%>'  readonly></td>
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
                        <%detailProducer.printBlankCells(pageContext, "class=td", true);%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" align="right"></td>
                        <td class="td" align="right"><input id="t_jje" name="t_jje" type="text" class='ednone_r' style="width:<%=widths[0]%>" value='<%=t_jje%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td" align="right"><input id="t_jsje" name="t_jsje" type="text" class='ednone_r' style="width:<%=widths[0]%>" value='<%=t_jsje%>' readonly></td>
                        <td class="td">&nbsp;</td>
                        <td class="td" align="right"><input id="t_tcj" name="t_tcj" type="text" class='ednone_r' style="width:70" value='<%=t_tcj%>' style="width:<%=widths[2]%>" readonly></td>
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
            <%if(!b_SaleBalanceBean.isReport){%>
            <%if(!zt.equals("8")){
            String quto = "sumitForm("+b_SaleBalanceBean.AUTO_CANCER+");";
            String po = "sumitForm("+Operate.POST+");";
             %>
             <input name="btnback" class="button" type="button" value="自动核销(W)" onClick="sumitForm(<%=b_SaleBalanceBean.AUTO_CANCER%>);" border="0">
             <pc:shortcut key="w" script="<%=quto%>" />
             <input name="btnback" type="button" class="button" onClick="sumitForm(<%=b_SaleBalanceBean.MASTER_DETAIL_POST%>);" value="保存返回(S)">
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
         paraStr = "../finance/sale_balance_import_lading_product.jsp?operate=0&multi=1&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+curID;
         if(methodName+'' != 'undefined')
           paraStr += "&method="+methodName;
         if(notin+'' != 'undefined')
           paraStr += "&notin="+notin;
         newWin =window.open(paraStr,winName,winopt);
         newWin.focus();
    }

    function selctTDHWOfLading()
    {
      if(form1.dwtxid.value=='')
      {
        alert('请选择购货单位');
        return;
      }
      if(form1.khlx.value=='')
      {
        alert('请选择客户类型');
        return;
      }
      if(form1.personid.value=='')
      {
        alert('请选择业务员');
        return;
      }
      TdhwMultiSelect('form1','srcVar=tdhwids','fieldVar=tdid','&dwtxid='+form1.dwtxid.value+'&personid='+form1.personid.value+'&khlx='+form1.khlx.value+'&isnet='+form1.isnet.value,"sumitForm(<%=b_SaleBalanceBean.DETAIL_SALE_ADD%>,-1)");
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
         OrderSingleSelect('form1','srcVar=selectedtdid','fieldVar=tdid',form1.dwtxid.value,"sumitForm(<%=b_SaleBalanceBean.IMPORT_TD%>,-1)");
   }
</script>
</script>
<%
//&#$
if(b_SaleBalanceBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
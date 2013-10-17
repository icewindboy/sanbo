<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList,engine.erp.finance.xixing.*,engine.common.*"%><%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
  String pageCode = "out_process_balance";
%>
<%
if(!loginBean.hasLimits(pageCode, request, response))
    return;
  B_OutProcessBalance b_OutProcessBalanceBean  =  B_OutProcessBalance.getInstance(request);

  engine.project.LookUp balanceModeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BALANCE_MODE);
  engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_CORP);
  engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);
  engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);
  engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
  engine.project.LookUp bankaccountBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK_ACCOUNT);
  engine.project.LookUp bankBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BANK);
  engine.project.LookUp storeBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_STORE);
  //engine.project.LookUp buyLadingBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_BUY_ORDER_STOCK);
  engine.project.LookUp processBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCE_PROCESS_GOODS);//通过加工单明细id得到加工单号
  engine.project.LookUp buyPriceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PRODUCT);//b_OutProcessBalanceBean.getBuyPriceBean(request);
  engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);//物资规格属性

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
  location.href='out_process_balance.jsp';
}
function customerCodeSelect(obj)
{
    CustomerCodeChange('3',document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=dz','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr',obj.value,'sumitForm(<%=b_OutProcessBalanceBean.DWTXID_CHANGE%>,-1)');
}
function customerNameChange(obj)
{
    CustomerNameChange('3',document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=dz','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr',obj.value,'sumitForm(<%=b_OutProcessBalanceBean.DWTXID_CHANGE%>,-1)');
}
</script>

<%
  String retu = b_OutProcessBalanceBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
  {
    out.print(retu);
    return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_OutProcessBalanceBean.getMaterTable();
  EngineDataSet list = b_OutProcessBalanceBean.getDetailTable();//引用过来的数据集
  HtmlTableProducer masterProducer = b_OutProcessBalanceBean.masterProducer;
  HtmlTableProducer detailProducer = b_OutProcessBalanceBean.detailProducer;
  RowMap masterRow = b_OutProcessBalanceBean.getMasterRowinfo();//主表一行
  RowMap[] detailRows= b_OutProcessBalanceBean.getDetailRowinfos();//从表多行
  ds.first();
  String zt=masterRow.get("zt");
  //&#$
  if(b_OutProcessBalanceBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  //String djxz=masterRow.get("djxz");
  boolean isEnd =  b_OutProcessBalanceBean.isApprove || (!b_OutProcessBalanceBean.masterIsAdd() && !zt.equals("0"));
  //没有结束,在修改状态,并有删除权限
  boolean isCanDelete = !isEnd && !b_OutProcessBalanceBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);
  isEnd = isEnd || !(b_OutProcessBalanceBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=ednone" : "class=edFocused";
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String classType =  zt.equals("8") ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  //String needColor = isEnd ? "" : " style='color:#660000'";
  String title = zt.equals("0") ? ("未审核") : ("已审核");
  //String tdlx=djxz.equals("2")?"企业普通发票":"增值税发票";
  //bankaccountBean.regConditionData("", );
  bankBean.regData(ds, "yh");
  boolean count=list.getRowCount()==0?true:false;
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));
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
            <td class="activeVTab">外加工结算(<%=title%>)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0" width="100%">
                <%
                  corpBean.regData(ds,"dwtxid");
                  personBean.regConditionData(ds,"deptid");
                 %>
                  <tr>
                  <td  noWrap class="tdTitle"><%=masterProducer.getFieldInfo("rq").getFieldname()%></td>
                  <td  noWrap class="td"><input type="text" name="rq" value='<%=masterRow.get("rq")%>' maxlength='10' style="width:110" <%=edClass%> onChange="checkDate(this)" onKeyDown="return getNextElement();" >
                  <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.rq);"></a>
                  <%}%></td>
                  <td  noWrap class="tdTitle">单据号</td>
                  <td  noWrap class="td"><input type="text" name="djh" value='<%=masterRow.get("djh")%>' maxlength='<%=ds.getColumn("djh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("deptid").getFieldname()%></td>
                  <td noWrap class="td">
                    <%
                      String t="sumitForm("+b_OutProcessBalanceBean.DEPT_CHANGE+",-1)";
                      if(isEnd) out.print("<input type='text' value='"+deptBean.getLookupName(masterRow.get("deptid"))+"' style='width:110' class='edline' readonly >");
                    else {
                     %>
                    <pc:select name="deptid" addNull="1" style="width:110" onSelect="deptchange();">
                      <%=deptBean.getList(masterRow.get("deptid"))%> </pc:select>
                    <%}%></td>
                 </tr>
                 <tr>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("dwtxid").getFieldname()%></td><%--购货单位--%>
                  <td  noWrap colspan='3' class="td">
                    <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text" <%=edClass%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="customerCodeSelect(this)" <%=readonly%>>
                    <input type="text" <%=edClass%> name="dwmc" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>'  onKeyDown="return getNextElement();"  style="width:200"  onchange="customerNameChange(this)" <%=readonly%>>
                    <%if(!isEnd){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProcessSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc','fieldVar=dwtxid&fieldVar=dwmc',form1.dwtxid.value,'sumitForm(<%=b_OutProcessBalanceBean.DWTXID_CHANGE%>,-1)');">
                    <%}%>
                    <%--<img style='cursor:hand' src='../images/dept.gif' border=0 onClick="ViewCust();">--%>
                  </td>
                  <td noWrap class="tdTitle">结算单号</td>
                  <td noWrap class="td"><input type="text" name="jsdh" value='<%=masterRow.get("jsdh")%>' maxlength='<%=ds.getColumn("jsdh").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();"<%=readonly%>></td>
                  <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("je").getFieldname()%></td>
                  <td noWrap class="td"><input type="text" name="je" value='<%=masterRow.get("je")%>' maxlength='<%=ds.getColumn("je").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" <%=readonly%>></td>
                 </tr>
                 <tr>
                  <td noWrap class="tdTitle">银行</td>
                  <td noWrap class="td" >
                  <%if(isEnd)
                    {
                    out.print("<input type='text' value='"+masterRow.get("yh")+"' style='width:110' class='edline' readonly>");
                    }else
                    {String yh=masterRow.get("yh");%>
                  <pc:select name="yh" addNull="1" style="width:180"   onSelect="yhchange()"  combox="1" value="<%=yh%>">
                  <%=bankBean.getList()%> </pc:select>
                   <%}%></td>
                  <td noWrap class="tdTitle">帐号</td>
                  <td noWrap class="td" >
                  <%if(isEnd) out.print("<input type='text' value='"+masterRow.get("zh")+"' style='width:110' class='edline' readonly>");
                  else {String zh=masterRow.get("zh");%>
                  <pc:select name="zh" addNull="1" style="width:180" combox="1" value="<%=zh%>">
                  <%=bankaccountBean.getList(masterRow.get("zh"),"yhmc",masterRow.get("yh"))%> </pc:select>
                   <%}%></td>
                  <td noWrap class="tdTitle">冲减预付</td>
                  <td noWrap class="td"><input type="text" name="cjyf" value='<%=masterRow.get("cjyf")%>' maxlength='<%=ds.getColumn("cjyf").getPrecision()%>' style="width:110" <%=edClass%> onKeyDown="return getNextElement();" onchange="cjyf_onchange()" <%=readonly%>></td>

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
                  <td noWrap class="tdTitle">申请人</td>
                  <td   noWrap class="td">
                  <%if(isEnd) {%>
                    <input type='text' value='<%=personBean.getLookupName(masterRow.get("personid"))%>' style='width:110' class='edline' readonly>
                    <input type='hidden'  name="personid"  value='<%=masterRow.get("personid")%>' style='width:110' class='edline' readonly>
                  <%}else {%>
                  <pc:select name="personid" addNull="1" style="width:110">
                    <%=personBean.getList(masterRow.get("personid"),"deptid",masterRow.get("deptid"))%> </pc:select>
                   <%}%></td>
                  <td noWrap class="tdTitle">总付款金额</td>
                  <td noWrap class="td"><input type="text" name="zfkje" value='<%=masterRow.get("zfkje")%>' maxlength='<%=ds.getColumn("zfkje").getPrecision()%>' style="width:110" class=edline onKeyDown="return getNextElement();" readonly></td>

                 <td noWrap class="tdTitle"><%="客户类型"%></td>
                  <td width="120" class="td">
                  <%String khlx = masterRow.get("khlx");if(isEnd){out.print(khlx.equals("A")?"A":"C");}else{%>
                  <pc:select name="khlx" style="width:110" value="<%=khlx%>">
                    <pc:option value="A">A</pc:option> <pc:option value="C">C</pc:option>
                  </pc:select><%}%></td>
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
                        <%if(!zt.equals("8")){%>
                        <input type="hidden" name="tdhwids" value="" onchange="sumitForm(<%=b_OutProcessBalanceBean.DETAIL_SALE_ADD%>,-1);">
                        <input name="image" class="img" type="image" title="新增(A)" onClick="selctTDHWOfLading()" src="../images/add_big.gif" border="0">
                        <pc:shortcut key="a" script="selctTDHWOfLading();" />
                        <%}%></td>
                        <td height='20' nowrap>加工单号</td>
                        <td height='20' nowrap>产品编码</td>
                        <td height='20' nowrap>品名 规格</td>
                        <td height='20' nowrap>规格属性</td>
                        <td height='20' nowrap>单位</td>
                        <td height='20' nowrap>应付金额</td>
                        <td nowrap>核销金额</td>
                        <td height='20' nowrap>差额</td>
                        <%--detailProducer.printTitle(pageContext, "height='20'", true);--%>
                      </tr>
                    <%
                      prodBean.regData(list,"cpid");
                      //buyLadingBean.regData(list,"jgdmxid");
                      processBean.regData(list,"jgdmxid");
                      propertyBean.regData(list,"dmsxID");
                      BigDecimal t_jje = new BigDecimal(0), t_jsje = new BigDecimal(0),t_bce = new BigDecimal(0), t_tcj = new BigDecimal(0);
                      //BigDecimal t_jsje = new BigDecimal(0), t_tcj = new BigDecimal(0);
                      int i=0;
                      String[] widthName = new String[]{"jsje"};
                      int[] widthMin = new int[]{60, 60, 70};
                      int[] widths = b_OutProcessBalanceBean.getMaxStyleWidth(detailRows, widthName, widthMin);
                      RowMap detail = null;
                      list.first();
                      for(; i<detailRows.length; i++)   {
                        detail = detailRows[i];
                        String jsje = detail.get("jsje").length()==0?"0":detail.get("jsje");
                        String cpid=detail.get("cpid");
                        if(b_OutProcessBalanceBean.isDouble(jsje))
                          t_jsje = t_jsje.add(new BigDecimal(jsje));
                        RowMap promap=prodBean.getLookupRow(detail.get("cpId"));
                        RowMap jhdRow = processBean.getLookupRow(detail.get("jgdmxid"));

                        BigDecimal bhwje =new BigDecimal(jhdRow.get("jgje").equals("")?"0":jhdRow.get("jgje"));
                        BigDecimal bjsje =new BigDecimal(detail.get("jsje"));
                        BigDecimal bce =bhwje.subtract(bjsje);

                        t_jje=t_jje.add(new BigDecimal(jhdRow.get("je").equals("")?"0":jhdRow.get("je")));
                        t_bce = t_bce.add(bce);

                        String isprops=promap.get("isprops");
                     %>
                      <tr id="rowinfo_<%=i%>">
                        <td class="td" nowrap><%=i+1%><input type="HIDDEN"  id="cpid_<%=i%>"  name="cpid_<%=i%>"  value='<%=detail.get("cpid")%>'  ></td>
                        <td class="td" nowrap align="center">
                          <%if(!zt.equals("8")){%>
                          <input name="image" class="img" type="image" title="删除" onClick="if(confirm('是否删除该记录？')) sumitForm(<%=Operate.DETAIL_DEL%>,<%=i%>)" src="../images/delete.gif" border="0">
                          <%}%></td>
                     <%--   <td class="td" nowrap><%=jhdRow.get("jhdbm")%></td>--%>
                        <td class="td" nowrap><%=processBean.getLookupName(detail.get("jgdmxid"))%></td>
                        <td class="td" nowrap><%=promap.get("cpbm")%></td><!--产品编码(存货代码)--->
                        <td class="td" nowrap><%=promap.get("product")%></td><!--品名 规格(存货名称与规格)--->
                        <td class="td" nowrap>
                        <input type="text" style="width:100%" <%=edClass%>  name="sxz_<%=i%>" value='<%=propertyBean.getLookupName(detail.get("dmsxid"))%>' onchange="propertiesInput(this,form1.cpid_<%=i%>.value,<%=i%>)"  <%=isEnd?"readonly":" "%> >
                        <input type="HIDDEN"  id="dmsxid_<%=i%>"  name="dmsxid_<%=i%>"  value='<%=detail.get("dmsxid")%>'  >
                        <%if(!isEnd){%>
                        <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if('<%=detail.get("cpid")%>'==''){alert('请先输入产品');return;}if('<%=isprops%>'=='0'){alert('该产品无规格属性!');return;}PropertySelect('form1','dmsxid_<%=i%>','sxz_<%=i%>','<%=cpid%>')"><%}%>
                        <%if(!isEnd&&!isprops.equals("0")){%><img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid_<%=i%>.value='';sxz_<%=i%>.value='';"><%}%>
                        </td>
                        <td class="td" nowrap><%=promap.get("jldw")%></td><!--计量单位--->
                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'  style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="jje_<%=i%>" name="jje_<%=i%>" value='<%=bhwje%>'  readonly></td>
                        <td class="td" nowrap align="right"><input type="text" <%=classType%>  style="width:100%" onKeyDown="return getNextElement();" id="jsje_<%=i%>" name="jsje_<%=i%>"     value='<%=detail.get("jsje")%>'   maxlength='<%=list.getColumn("jsje").getPrecision()%>'  onchange="hxje_onchange(<%=i%>)" <%=zt.equals("8")?"readonly":""%> ></td>
                        <td class="td" nowrap align="right"><input type="text" class='ednone_r'  style="width:<%=widths[0]%>" onKeyDown="return getNextElement();" id="bce_<%=i%>" name="bce_<%=i%>" value='<%=bce%>'  readonly></td>
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
                        <td class="td">&nbsp;</td>
                        <%--detailProducer.printBlankCells(pageContext, "class=td", true);--%>
                      </tr>
                      <%}%>
                      <tr id="rowinfo_end">
                        <td class="td">&nbsp;</td>
                        <td class="tdTitle" nowrap>合计</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        <td class="td" align="right"></td>
                         <td class="td">&nbsp;</td>
                        <td class="td" align="right"><input id="t_jje" name="t_jje" type="text" class='ednone_r' style="width:<%=widths[0]%>" value='<%=t_jje%>' readonly></td>
                        <td class="td" align="right"><input id="t_jsje" name="t_jsje" type="text" class='ednone_r' style="width:<%=widths[0]%>" value='<%=t_jsje%>' readonly></td>
                        <td class="td" align="right"><input id="t_bce" name="t_bce" type="text" class='ednone_r' style="width:<%=widths[0]%>" value='<%=t_bce%>' readonly></td>
                        <%--detailProducer.printBlankCells(pageContext, "class=td", true);--%>
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
            <%if(!zt.equals("8")){
            String quto = "sumitForm("+b_OutProcessBalanceBean.AUTO_CANCER+");";
            String po = "sumitForm("+Operate.POST+");";
             %>
             <input name="btnback" class="button" type="button" value="自动核销(W)" onClick="sumitForm(<%=b_OutProcessBalanceBean.AUTO_CANCER%>);" border="0">
             <pc:shortcut key="w" script="<%=quto%>" />
             <input name="btnback" type="button" class="button" onClick="sumitForm(<%=b_OutProcessBalanceBean.MASTER_DETAIL_POST%>);" value="保存返回(S)">
              <pc:shortcut key="s" script="<%=po%>" />
              <%}%>
              <input name="btnback" type="button" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script="backList();" />
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
          var bceObj = document.all['bce_'+i];
            if(jsjeObj.value=="")
              return;
            if(isNaN(jsjeObj.value)){
              alert("输入的金额非法");
              jsjeObj.focus();
              return;
            }
            if((parseFloat(Math.abs(jjeObj.value)) - Math.abs(parseFloat(jsjeObj.value)))<0)
            {
              alert("输入的金额非法,最大值为货款金额:"+jjeObj.value);
              jsjeObj.value=jjeObj.value;
            }
            bceObj.value=formatQty(parseFloat(jjeObj.value) - parseFloat(jsjeObj.value));
            cal_tot('jsje');
        }
         function cal_tot(type)
        {
          var tmpObj;
          var tot=0;
          var tjje=0;
          var tbce=0;
          for(i=0; i<<%=detailRows.length%>; i++)
          {
            if(type == 'jsje')
              tmpObj = document.all['jsje_'+i];
              tjjeObj = document.all['jje_'+i];
              tbceObj = document.all['bce_'+i];
            if(tmpObj.value!="" && !isNaN(tmpObj.value))
              tot += parseFloat(tmpObj.value);
            if(tjjeObj.value!="" && !isNaN(tjjeObj.value))
              tjje += parseFloat(tjjeObj.value);
            if(tbceObj.value!="" && !isNaN(tbceObj.value))
              tbce += parseFloat(tbceObj.value);
          }
          if(type == 'jsje')
          {
            document.all['t_jsje'].value = formatQty(tot);
            document.all['t_jje'].value = formatQty(tjje);
            document.all['t_bce'].value = formatQty(tbce);
          }
    }
    function TdhwMultiSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
       {
         var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
         var winName= "MultiProdSelector";
         paraStr = "../finance_xixing/import_wjgdhw_detail.jsp?operate=0&multi=1&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+curID;
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
      TdhwMultiSelect('form1','srcVar=tdhwids','fieldVar=tdid','&dwtxid='+form1.dwtxid.value+'&personid='+form1.personid.value,"sumitForm(<%=b_OutProcessBalanceBean.DETAIL_SALE_ADD%>,-1)");
        }
/*
    function OrderSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
       {
        var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
        var winName= "SingleladingSelector";
        paraStr = "../finance_xixing/import_stocking_detail.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&dwtxid="+curID;
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
         OrderSingleSelect('form1','srcVar=selectedtdid','fieldVar=tdid',form1.dwtxid.value,"sumitForm(<%=b_OutProcessBalanceBean.IMPORT_TD%>,-1)");
   }
        */
</script>
</script>
<%
//&#$
if(b_OutProcessBalanceBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
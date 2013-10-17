<%--销售冲帐单--%><%@ page contentType="text/html; charset=UTF-8" %><%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.math.BigDecimal,engine.html.*,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String op_approve ="op_approve";
%>
<%
  engine.erp.sale.xixing.B_StrikeBalance b_StrikeBalanceBean = engine.erp.sale.xixing.B_StrikeBalance.getInstance(request);
  String pageCode = "strike_balance";
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
  location.href='strike_balance.jsp';
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
    alert('请输入开单日期!');
    return;
  }
  else
  {
    if(!checkDate(form1.tdrq))
      return;
  }
  CustCodeChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=personid&srcVar=deptid','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=personid&fieldVar=deptid',obj.value,'sumitForm(<%=b_StrikeBalanceBean.DWTXID_CHANGE%>,-1)');
}
//选择购货单位
function selectCustomer()
{
  if(form1.tdrq.value=='')
  {
    alert('请输入开单日期!');
    return;
  }
  else
  {
    if(!checkDate(form1.tdrq))
      return;
  }
CustSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dz&srcVar=dwdm','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr&fieldVar=dwdm',form1.dwtxid.value,'sumitForm(<%=b_StrikeBalanceBean.DWTXID_CHANGE%>,-1)');
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
  CustNameChange(document.all['prod'], obj.form.name,'srcVar=dwdm&srcVar=dwtxid&srcVar=dwmc&srcVar=dz','fieldVar=dwdm&fieldVar=dwtxid&fieldVar=dwmc&fieldVar=addr',obj.value,'sumitForm(<%=b_StrikeBalanceBean.DWTXID_CHANGE%>,-1)');
}
function selectTdrq()
{
  selectDate(document.form1.tdrq);
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid', eval('form1.deptid.value'), '');
}
</script>
<%
  String retu = b_StrikeBalanceBean.doService(request, response);
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
  EngineDataSet ds = b_StrikeBalanceBean.getMaterTable();
  EngineDataSet list = b_StrikeBalanceBean.getDetailTable();


  HtmlTableProducer masterProducer = b_StrikeBalanceBean.masterProducer;
  HtmlTableProducer detailProducer = b_StrikeBalanceBean.detailProducer;


  RowMap masterRow = b_StrikeBalanceBean.getMasterRowinfo();//主表一行
  RowMap[] detailRows= b_StrikeBalanceBean.getDetailRowinfos();//从表多行


  creditBean.regData(ds,"dwtxid");
  if(b_StrikeBalanceBean.isApprove)
  {
    corpBean.regData(ds, "dwtxid");
    personBean.regData(ds, "personid");
  }
  String djlx=masterRow.get("djlx");
  sendModeBean.regData(ds, "sendmodeid");


  String zt=masterRow.get("zt");


  boolean isEnd =  b_StrikeBalanceBean.isReport||b_StrikeBalanceBean.isApprove || (!b_StrikeBalanceBean.masterIsAdd() && !zt.equals("0"));
  //没有结束,在修改状态,并有删除权限
  boolean isCanDelete = !isEnd && !b_StrikeBalanceBean.masterIsAdd() && loginBean.hasLimits(pageCode, op_delete);
  isEnd = isEnd || !(b_StrikeBalanceBean.masterIsAdd() ? loginBean.hasLimits(pageCode, op_add) : loginBean.hasLimits(pageCode, op_edit));


  String czyid= masterRow.get("czyid");
  String loginid= b_StrikeBalanceBean.loginId;
  isEnd = isEnd||!czyid.equals(loginid);

  FieldInfo[] mBakFields = masterProducer.getBakFieldCodes();//主表用户的自定义字段
  String edClass = isEnd ? "class=edline" : "class=edbox";
  String detailClass = isEnd ? "class=edline" : "class=edFocused";
  String prodClass = detailClass;
  String detailClass_r = isEnd ? "class=ednone_r" : "class=edFocused_r";
  String readonly = isEnd ? " readonly" : "";
  String prodreanonly = readonly;
  String title = zt.equals("0") ? ("未审批") : ((zt.equals("9") ? "审批中" :(zt.equals("4")?"已作废":(zt.equals("8")?"完成":"已审"))) );

  boolean count=list.getRowCount()==0?true:false;
  RowMap corpRow =corpBean.getLookupRow(masterRow.get("dwtxid"));


  boolean canedt = zt.equals("0")||zt.equals("1")||zt.equals("2");
  String djandjjeClass = canedt ?"class=edFocused_r": "class=ednone_r" ;
  String dwtxClass = count? "class=edbox":"class=edline";
  String dwtxRead = count? " " : "readonly";
  boolean mustConversion = b_StrikeBalanceBean.conversion;//是否需要强制转换
  String djhclass = zt.equals("2")?"class=edbox":"class=edline";
  String djhreadonly = zt.equals("2")?"":"readonly";


%>
<BODY id="docbody" oncontextmenu="window.event.returnValue=true"  >
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr><td align="center" height="5"></td></tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <INPUT TYPE="HIDDEN" NAME="djlx" value="<%=djlx%>">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td>

         <table border=0 CELLSPACING=0 CELLPADDING=0 class="table"><tr><td class="activeVTab">销售冲帐单</td></tr></table>
         <table class="editformbox" CELLSPACING=1 CELLPADDING=0 >
          <tr>
            <td>


              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" bgcolor="#f0f0f0" width="100%">
                 <tr>
                    <td  noWrap class="tdTitle">单据号</td>
                    <td  noWrap class="td"><input type="text" name="tdbh" value='<%=masterRow.get("tdbh")%>' maxlength='<%=ds.getColumn("tdbh").getPrecision()%>' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                    <td  noWrap class="tdTitle">冲帐日期</td>
                    <td  noWrap class="td">
                    <input type="text" name="tdrq" value='<%=masterRow.get("tdrq")%>' maxlength='10' style="width:110" <%=edClass%>   >
                    <%if(!isEnd){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectTdrq()"></a><%}%>
                    </td>
                    <td noWrap class="tdTitle">部门</td>
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
                    <td noWrap class="tdTitle"><%=masterProducer.getFieldInfo("personid").getFieldname()%></td>
                    <td  noWrap class="td">
                    <%
                      if(!isEnd)
                        personBean.regConditionData(ds, "deptid");
                      if(isEnd||!count) out.print("<input type='hidden' name='personid' value='"+masterRow.get("personid")+"'><input type='text'   value='"+personBean.getLookupName(masterRow.get("personid"))+"' style='width:110' class='edline' readonly>");
                      else {%>
                     <pc:select name="personid"  addNull="1" style="width:110" >
                      <%=personBean.getList(masterRow.get("personid"), "deptid", masterRow.get("deptid"))%> </pc:select>
                    <%}%>
                   </td>
               </tr>
               <tr>
                  <td noWrap class="tdTitle">客户名称</td>
                  <td colspan="3" noWrap class="td">
                    <input type="hidden" name="dwtxid" value='<%=masterRow.get("dwtxid")%>'>
                    <input type="text" <%=dwtxClass%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="customerCodeSelect(this)" <%=dwtxRead%>>
                    <input type="text" <%=dwtxClass%> name="dwmc" onKeyDown="return getNextElement();" value='<%=corpBean.getLookupName(masterRow.get("dwtxid"))%>' style="width:200"   onchange="customerNameSelect(this)"  <%=dwtxRead%>>
                    <%if(count){%>
                    <img style='cursor:hand' src='../images/view.gif' border=0 onClick="selectCustomer()">
                    <%}%>
                  </td>
                  <td noWrap class="tdTitle">客户地址</td>
                  <td colspan="3" noWrap class="td">
                    <input type="text" <%=dwtxClass%> name="dz" onKeyDown="return getNextElement();" value='<%=masterRow.get("dz")%>' style="width:280"   <%=dwtxRead%>>
                  </td>
              </tr>
              <tr>
                  <td noWrap class="tdTitle">客户类型</td>
                  <td width="120" class="td">
                  <%
                    String khlx = masterRow.get("khlx");
                     if(isEnd){
                     out.print("<input  name='khlx' type='text' value='"+masterRow.get("khlx")+"' style='width:110' class='edline' readonly>");
                    }else{%>
                  <pc:select name="khlx" style="width:110" addNull="1" value="<%=khlx%>"  >
                    <pc:option value="A">A</pc:option> <pc:option value="C">C</pc:option>
                  </pc:select>
                  <%}%>
                 </td>
                  <td noWrap class="tdTitle">经办人</td>
                  <td noWrap class="td" >
                  <%if(isEnd)
                    {
                    out.print("<input type='text' value='"+masterRow.get("jbr")+"' style='width:110' class='edline' readonly>");
                    }else
                    {String jbr=masterRow.get("jbr");%>
                  <pc:select name="jbr" addNull="1" style="width:110"   combox="1" value="<%=jbr%>">
                  <%=personBean.getList()%> </pc:select>
                   <%}%></td>
                  <td  noWrap class="tdTitle">结算方式</td>
                  <td  noWrap class="td">
                    <%if(isEnd) out.print("<input type='hidden' name='jsfsid' value='"+masterRow.get("jsfsid")+"'><input type='text'   value='"+balanceModeBean.getLookupName(masterRow.get("jsfsid"))+"' style='width:110' class='edline' readonly >");
                    else {%>
                    <pc:select name="jsfsid"  addNull="1"  style="width:110">
                    <%=balanceModeBean.getList(masterRow.get("jsfsid"))%>
                    </pc:select>
                    <%}%></td>
                  <td noWrap class="tdTitle">金额</td>
                  <td noWrap class="td"><input type="text" name="zje" value='<%=masterRow.get("zje")%>' maxlength='10' style="width:85" <%=edClass%>  onKeyDown="return getNextElement();"<%=readonly%>>

                 </tr>
                <tr>
                  <td  noWrap class="tdTitle">备注</td>
                  <td colspan="7" noWrap class="td"><textarea name="bz" rows="3" onKeyDown="return getNextElement();" style="width:690"<%=readonly%>><%=masterRow.get("bz")%></textarea></td>
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
               if(!isEnd){
               String retun = "sumitForm("+Operate.POST_CONTINUE+")";
              %>
              <input name="button2"  style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存新增(N)">
              <pc:shortcut key="n" script="<%=retun%>" />
              <%
               }
               if(!isEnd&&czyid.equals(loginid))
               {
                 if(!b_StrikeBalanceBean.isReport){
                   String post ="sumitForm("+Operate.POST+");";
               %>
              <input name="btnback" style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
              <pc:shortcut key="s" script="<%=post%>" />
               <%}
               }if(false){
                 if(!b_StrikeBalanceBean.isReport&&czyid.equals(loginid)){
                   String appost ="sumitForm("+b_StrikeBalanceBean.APPROVED_MASTER_ADD+");";
             %>
              <input name="btnback" style="width:80" type="button" class="button" onClick="sumitForm(<%=b_StrikeBalanceBean.APPROVED_MASTER_ADD%>);" value="保存(S)">
              <pc:shortcut key="s" script="<%=appost%>" />
              <%
              }}if(isCanDelete&&!b_StrikeBalanceBean.isReport&&czyid.equals(loginid)){
               String del = "if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+");";
              %>
              <input name="button3"  style="width:60" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>);" value="删除(D)">
              <pc:shortcut key="d" script="<%=del%>" />
              <%
              }
               if(zt.equals("1")||zt.equals("8")){%>
              <input type="button" class="button" value="打印(P)" onclick="location.href='../pub/pdfprint.jsp?code=shyu_stike_balance_print&operate=<%=Operate.PRINT_BILL%>&a$tdid=<%=masterRow.get("tdid")%>&src=../sale_xixing/send_list_edit.jsp'">
              <%}
              if(!b_StrikeBalanceBean.isReport){
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
<%
if(b_StrikeBalanceBean.isApprove){%><jsp:include page="../pub/approve.jsp" flush="true"/><%}%>
<%out.print(retu);%>
</BODY>
</Html>
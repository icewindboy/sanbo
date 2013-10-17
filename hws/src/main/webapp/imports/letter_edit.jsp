<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.EngineDataSet,engine.dataset.RowMap"%>
<%@ page import="engine.action.Operate,engine.common.LoginBean,engine.erp.baseinfo.*,engine.project.*,engine.erp.person.*"%>
<%@ page import="java.util.*"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "letter";
%>
<%
  if(!loginBean.hasLimits("letter", request, response))
    return;
  engine.erp.imports.B_Letter  b_LetterBean = engine.erp.imports.B_Letter.getInstance(request);
  LookUp corpBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_CORP);//往来单位
  LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);//人员
  LookUp bankBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_BANK);//银行
  LookUp deptBean = LookupBeanFacade.getInstance(request,SysConstant.BEAN_DEPT);//部门
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
  location.href='letter.jsp';
}
function corpCodeSelect(obj)
{
  ProvideCodeChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc&srcVar=addr',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc&fieldVar=addr', obj.value, '');
}
function corpNameSelect(obj)
{
  ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=dwtxid&srcVar=dwdm&srcVar=dwmc&srcVar=addr',
                 'fieldVar=dwtxid&fieldVar=dwdm&fieldVar=dwmc&fieldVar=addr', obj.value, '');
}
function deptchange(){
   associateSelect(document.all['prod'], '<%=engine.project.SysConstant.BEAN_PERSON%>', 'personid', 'deptid',  eval('form1.deptid.value'), '');
}
function moneyChang(money){
  document.all['prod'].src='letter.jsp?money='+money;
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<%String retu = b_LetterBean.doService(request, response);
  if(retu.indexOf("backList();")>-1)
 {
  out.print(retu);
  return;
  }
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = b_LetterBean.getOneTable();
  RowMap row = b_LetterBean.getRowinfo();
  boolean isCanDelete =loginBean.hasLimits(pageCode, op_delete);
  boolean isCanAdd =loginBean.hasLimits(pageCode, op_add);
  boolean isCanEdit=loginBean.hasLimits(pageCode, op_edit);
  corpBean.regData(ds,"dwtxid");
  personBean.regData(ds,"personid");
  bankBean.regData(ds,"yhid");
  deptBean.regData(ds,"deptid");
  RowMap corpRow =corpBean.getLookupRow(row.get("dwtxid"));
  String state=row.get("state");
  String edClass = state.equals("8") ? "class=edline" : "class=edbox";
  String readonly = state.equals("8") ? "readonly" : "";
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();"  >
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0>
    <tr>
      <td align="center" height="5"></td>
    </tr>
  </table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table width="51" border=0 CELLPADDING=0 CELLSPACING=0 class="table">
          <tr>
            <td width="48" class="activeVTab">信用证</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="92%">
          <tr>
            <td> <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                <tr>
                  <td noWrap class="tdTitle">信用证号码</td>
                  <td width="120" noWrap class="td">
                  <input  type="text" name=" letterno" value='<%=row.get("letterno")%>' maxlength='32'  style="width:110"  class=edline onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle">银行</td>
                  <td noWrap class="td">
                  <%if(!state.equals("8")){%>
                  <pc:select name="yhid" addNull="1" style="width:110" value='<%=row.get("yhid")%>'>
                  <%=bankBean.getList(row.get("yhid"))%></pc:select>
                  <%}else{%>
                  <input type="text"  value='<%=bankBean.getLookupName(row.get("yhid"))%>'  style="width:110"  <%=edClass%> <%=readonly%>><%}%>
                  </td>
                  <td noWrap class="tdTitle">开证方式</td>
                  <td  noWrap class="td">
                  <%if(!state.equals("8")){%>
                  <pc:select name="kzfs" addNull="1" value='<%=row.get("kzfs")%>' style="width:110">
                    <pc:option value='1'>信开</pc:option>
                    <pc:option value='2'>简电开</pc:option>
                    <pc:option value='3'>电开</pc:option>
                 </pc:select>
                  <%}else{%>
                  <input type="text"  value='<%String mykzfs=row.get("kzfs");
                    if(mykzfs.equals("1"))out.print("信开");
                    if(mykzfs.equals("2"))out.print("简电开");
                    if(mykzfs.equals("3"))out.print("电开");%>'  style="width:110" <%=edClass%> <%=readonly%>> <%}%>
                  </td>
                  <td  noWrap class="tdtitle">信用证类型</td>
                  <td  noWrap class="td">
                  <%if(!state.equals("8")){%>
                  <pc:select name="lettertype" addNull="1" value='<%=row.get("lettertype")%>' style="width:110">
                    <pc:option value='1'>即期付款</pc:option>
                    <pc:option value='2'>承税</pc:option>
                    <pc:option value='3'>议付</pc:option>
                    <pc:option value='4'>迟期付款</pc:option>
                  </pc:select>
                  <%}else{%>
                  <input type="text"  value='<%String mylettertype=row.get("lettertype");
                    if(mylettertype.equals("1"))out.print("即期付款");
                    if(mylettertype.equals("2"))out.print("承税");
                    if(mylettertype.equals("3"))out.print("议付");
                    if(mylettertype.equals("4"))out.print("迟期付款");%>'  style="width:110" <%=edClass%> <%=readonly%>> <%}%>
                  </td>
                </tr>
                <tr>
                  <TD class="tdtitle" nowrap>受益人</TD>
                  <TD nowrap class="td" colspan="3"> <input type="hidden" name="dwtxid"  value='<%=row.get("dwtxid")%>'>
                    <input type="text" <%=edClass%> <%=readonly%> style="width:70" onKeyDown="return getNextElement();" name="dwdm" value='<%=corpRow.get("dwdm")%>' onchange="corpCodeSelect(this)">
                    <input type="text" <%=edClass%> <%=readonly%> name="dwmc" value='<%=corpBean.getLookupName(row.get("dwtxid"))%>'  style="width:180" onchange="corpNameSelect(this)">
                    <%if(!state.equals("8")){%><img style='cursor:hand' src='../images/view.gif' border=0 onClick="ProvideSingleSelect('form1','srcVar=dwtxid&srcVar=dwmc&srcVar=dwdm&srcVar=addr','fieldVar=dwtxid&fieldVar=dwmc&fieldVar=dwdm&fieldVar=addr',form1.dwtxid.value)">
                    <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="dwtxid.value='';dwmc.value='';dwdm.value='';addr.value='';">
                  <%}%></TD>
                  <td noWrap class="tdTitle">受益人详细地址</td>
                  <td colspan="3"  noWrap class="td"><input type="text" name="addr" value='<%=corpRow.get("addr")%>' maxlength='20' style="width:270" class=edline readonly="true" onKeyDown="return getNextElement();" onchange="hl_onchange();"></td>
                </tr>
                <tr>
                  <td noWrap class="tdTitle">通知行</td>
                  <td  noWrap class="td">
                  <%if(!state.equals("8")){%>
                  <pc:select name="yh_yhid" addNull="1" style="width:110" value='<%=row.get("yh_yhid")%>'>
                  <%=bankBean.getList()%> </pc:select>
                  <%}else{%>
                  <input type="text"  value='<%=bankBean.getLookupName(row.get("yh_yhid"))%>'  style="width:110"  <%=edClass%> <%=readonly%>><%}%>
                  </td>
                  <td noWrap class="tdTitle">金额</td>
                  <td noWrap class="td" >
                  <input type="text" name="xxje" value='<%=row.get("xxje")%>' maxlength='21' style="width:110" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();"></td>
                  <td noWrap class="tdTitle" style="display:none">大写金额</td>
                  <td noWrap colspan="3" class="td" style="display:none">
                  <input type="text" name="dxje" value='<%=row.get("dxje")%>' maxlength='20' style="width:270" class=edline readonly onKeyDown="return getNextElement();" ></td>
                </tr>
                <tr>
                <TD class="tdtitle" nowrap>分批装运</TD>
                  <TD nowrap class="td">
                  <%if(!state.equals("8")){%>
                  <pc:select name="fpzy" addNull="1"  value='<%=row.get("fpzy")%>' style="width:110">
                    <pc:option value='0'>不允许</pc:option>
                    <pc:option value='1'>允许</pc:option>
                  </pc:select>
                  <%}else{%>
                  <input type="text"  value='<%String myfpzy=row.get("fpzy");
                    if(myfpzy.equals("0"))out.print("不允许");
                    if(myfpzy.equals("1"))out.print("允许");%>'  style="width:110" <%=edClass%> <%=readonly%>><%}%>
                  </TD>
                  <TD nowrap class="tdtitle">转运</TD>
                  <TD nowrap class="td">
                     <%if(!state.equals("8")){%>
                     <pc:select name="zy" addNull="1"  value='<%=row.get("zy")%>' style="width:110">
                        <pc:option value='0'>不允许</pc:option>
                        <pc:option value='1'>允许</pc:option>
                      </pc:select>
                      <%}else{%>
                    <input type="text"  value='<%String myzy=row.get("zy");
                    if(myzy.equals("0"))out.print("不允许");
                    if(myzy.equals("1"))out.print("允许");%>'  style="width:110" <%=edClass%> <%=readonly%>><%}%>
                  </TD>
                  <TD nowrap class="tdtitle">价格条款</TD>
                  <TD nowrap class="td">
                   <%if(!state.equals("8")){%>
                   <pc:select name="jgtk" addNull="1" combox="1" value='<%=row.get("jgtk")%>' style="width:110">
                      <pc:option value='FOB'>FOB</pc:option>
                      <pc:option value='CFR'>CFR</pc:option>
                      <pc:option value='CIF'>CIF</pc:option>
                      <pc:option value='FCA'>FCA</pc:option>
                      <pc:option value='CPT'>CPT</pc:option>
                      <pc:option value='CIP'>CIP</pc:option>
                      <pc:option value='其他价格条款'>其他价格条款</pc:option>
                   </pc:select>
                   <%}else{%>
                    <input type="text"  value='<%=row.get("jgtk")%>'  style="width:110" <%=edClass%> <%=readonly%>><%}%>
                  </TD>
                  <TD nowrap class="tdtitle">申请日期</TD>
                  <TD nowrap class="td"><INPUT <%=edClass%> <%=readonly%> style="WIDTH: 85px" name="apply_date" value='<%=row.get("apply_date")%>' maxlength='10' onChange="checkDate(this)" onKeyDown="return getNextElement();">
                  <%if(!state.equals("8")){%><A href="#"><IMG title=选择日期 onClick="selectDate(apply_date);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A><%}%></TD>
                </tr>
                <tr>
                <td noWrap class="tdTitle">申请部门</td>
                  <td noWrap class="td">
                   <%if(!state.equals("8")){%>
                   <pc:select name="deptid" addNull="1" style="width:110" value='<%=row.get("deptid")%>' onSelect="deptchange();">
                   <%=deptBean.getList(row.get("deptid"))%></pc:select>
                   <%}else{%>
                  <input type="text"  value='<%=deptBean.getLookupName(row.get("deptid"))%>'  style="width:110"  <%=edClass%> <%=readonly%>><%}%>
                  </td>
                  <td noWrap class="tdTitle">申请人</td>
                  <td noWrap class="td">
                  <%if(!state.equals("8")){%>
                  <pc:select name="personid" addNull="1" style="width:110" value='<%=row.get("personid")%>'>
                  <%=personBean.getList()%></pc:select>
                  <%}else{%>
                  <input type="text"  value='<%=personBean.getLookupName(row.get("personid"))%>'  style="width:110"  <%=edClass%> <%=readonly%>><%}%>
                  </td>
                  <td noWrap class="tdTitle">有效期</td>
                  <td  noWrap class="td"> <input <%=edClass%> <%=readonly%> type="text" name="useful_life" value='<%=row.get("useful_life")%>' maxlength='10' style="width:85" class=edFocused onChange="checkDate(this)" onKeyDown="return getNextElement();">
                  <%if(!state.equals("8")){%><a href="#"><img align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.useful_life);"></a><%}%>
                  </td>
                  <td noWrap class="tdTitle">地址</td>
                  <td noWrap class="td">
                     <%if(!state.equals("8")){%>
                     <pc:select name="address" addNull="1"  value='<%=row.get("address")%>' style="width:115">
                       <pc:option value='1'>在受益人所在国家</pc:option>
                       <pc:option value='2'>在开证行柜台</pc:option>
                     </pc:select>
                    <%}else{%>
                    <input type="text"  value='<%
                    String myaddress = row.get("address");
                    if(myaddress.equals("1"))out.print("在受益人所在国家");
                    if(myaddress.equals("2"))out.print("在开证行柜台");
                    %>'  style="width:110"  <%=edClass%> <%=readonly%>><%}%>
                </td>
                </tr>
                <tr>
                  <td  noWrap class="tdTitle">备注</td>
                  <td colspan="7" noWrap class="td">
                  <textarea name="memo" rows="6" <%=edClass%> <%=readonly%> onKeyDown="return getNextElement();" style="width:690;height:65"><%=row.get("memo")%></textarea></td>
                </tr>
              </table></td>
          </tr>
        </table></td>
    </tr>
    <tr>
      <td> <table CELLSPACING=0 CELLPADDING=0 width="100%">
          <tr>
            <td noWrap class="tableTitle">
             <%if(isCanAdd&&!state.equals("8")){%>
             <input name="button2"  style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST_CONTINUE%>);" value="保存添加(N)">
             <pc:shortcut key="n" script='<%="sumitForm("+Operate.POST_CONTINUE+",-1)"%>'/>
             <input name="btnback"  style="width:80" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存返回(S)">
             <pc:shortcut key="s" script='<%="sumitForm("+Operate.POST+",-1)"%>' />
             <%}if(isCanDelete&&!b_LetterBean.isAdd&&!state.equals("8")){
              String del="if(confirm('是否删除该记录？'))sumitForm("+Operate.DEL+","+request.getParameter("rownum")+")";%>
              <input name="btnback2" type="button" class="button" onClick="if(confirm('是否删除该记录？'))sumitForm(<%=Operate.DEL%>)" value="删除(D)">
              <pc:shortcut key="d" script='<%=del%>'/><%}%>
              <input name="btnback" type="button"  style="width:50" class="button" onClick="backList();" value="返回(C)">
              <pc:shortcut key="c" script='backList()'/>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<%out.print(retu);%>
</BODY>
</Html>

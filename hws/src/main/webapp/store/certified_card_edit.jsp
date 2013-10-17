<%--Title: 质量管理--产品合格证</p>--%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="../pub/init.jsp"%>
<%@ page import="engine.dataset.*,engine.project.Operate,java.util.ArrayList"%>
<%!
  String op_add    = "op_add";
  String op_delete = "op_delete";
  String op_edit   = "op_edit";
  String op_search = "op_search";
  String pageCode = "certified_card";
%>

<html>
<head>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<%if(!loginBean.hasLimits("certified_card", request, response))
  return;
engine.project.LookUp personBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_PERSON);
engine.erp.quality.B_CertifiedCard certifiedBean=engine.erp.quality.B_CertifiedCard.getInstance(request);
engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_PRODUCT);
engine.project.LookUp propertyBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SPEC_PROPERTY);
boolean issearch = loginBean.hasLimits(pageCode,op_search);
String sysParam = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");
%>
<script language="javascript" src="../scripts/validate.js"></script>
<script language="javascript">
function sumitForm(oper, row)
{
lockScreenToWait("处理中, 请稍候！");
form1.operate.value = oper;
form1.submit();
}
function productNameSelect(obj)
{
ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=pm&srcVar=gg&srcVar=chlbid&srcVar=hsbl&srcVar=scdwgs',
  'fieldVar=cpid&fieldVar=cpbm&fieldVar=pm&fieldVar=gg&fieldVar=chlbid&fieldVar=hsbl&fieldVar=scdwgs', obj.value, 'getPrintModeValue()');
}
function productCodeSelect(obj)
{
  form1.dmsxid,value = "";
  form1.sxz.value="";
  ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid&srcVar=cpbm&srcVar=pm&srcVar=gg&srcVar=chlbid&srcVar=hsbl&srcVar=scdwgs',
  'fieldVar=cpid&fieldVar=cpbm&fieldVar=pm&fieldVar=gg&fieldVar=chlbid&fieldVar=hsbl&fieldVar=scdwgs', obj.value, 'getPrintModeValue()');
}
function propertyNameSelect(obj,cpid)
{
  PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid&srcVar=sxz',
                     'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value);
}
/**
 * iframeObj IFrame的对象
 * lookup    Lookup Bean 的名称
 * frmName   表单名称
 * srcVar    表单各个需要取值的控件名称字符串。如:srcVar=dmsxid_0&srcVar=sxz_0
 * fieldVar  与各个需要取值的控件名称相对应的字段名称字符串。如:fieldVar=dmsxid&fieldVar=sxz
 * curID     当前要得到的ID的值
 * methodName  对输入框赋值后的要调用的方法名称
 */
function getPrintModeValue()
{
  //alert(form1.chlbid.value);
  getRowValue(document.all['prod'], '<%=engine.project.SysConstant.BEAN_STOCKS_KIND%>', "form1", "srcVar=dygs", "fieldVar=dygs", form1.chlbid.value, "showBillStyle()");
}
</script>
<BODY oncontextmenu="window.event.returnValue=true" onload="showBillStyle()">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>
<table WIDTH="100%" BORDER=0 cellspacing=0 cellpadding=0
CLASS="headbar"><tr><TD NOWRAP
align="center">产品合格证属性</TD></tr></table>
<%String retu = certifiedBean.doService(request, response);
  out.print(retu);
  if(retu.indexOf("location.href=")>-1)
    return;
  String curUrl = request.getRequestURL().toString();
  EngineDataSet ds = certifiedBean.getOneTable();
  RowMap row = certifiedBean.getRowinfo();
  boolean isCanAdd =loginBean.hasLimits(pageCode,op_add)||loginBean.hasLimits(pageCode, op_edit);//权限
  String edClass = isCanAdd?"class=edbox":"class=ednone";
  String readonly = isCanAdd?"":"readonly";
  prodBean.regData(ds,"cpid");
  propertyBean.regData(ds,"dmsxid");
  String WorkNo=loginBean.getUser().getWorkNo();
  boolean isMasterAdd = certifiedBean.isAdd;
  boolean isCanPrint = certifiedBean.isPrint;
%>
<form name="form1" action="<%=curUrl%>" method="POST" onsubmit="return
  false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <table BORDER="0" cellpadding="1" cellspacing="3">
   <tr>
      <td noWrap class="tableTitle">&nbsp;&nbsp;合格证日期</td>
       <td noWrap class="td">
        <INPUT TYPE="text" NAME="cardDate" style="width:130"VALUE="<%=row.get("cardDate")%>" <%=edClass%> <%=readonly%>>
        <%if(isCanAdd){%><A href="#"><IMG title=选择日期 onClick="selectDate(cardDate);" height=20 src="../images/seldate.gif" width=20 align=absMiddle border=0></A>
        <%}%>
      </td>
      <td noWrap class="tableTitle">&nbsp;&nbsp;质检员</td>
        <td  noWrap class="td"><input type="text" name="qualityer" value='<%=row.get("qualityer")%>'  style="width:130" class="edline" onKeyDown="return getNextElement();" readonly>
      </td>
    </tr>
    <tr>
     <%RowMap prodRow = prodBean.getLookupRow(row.get("cpid"));%>
       <input type="hidden" name="cpid" value="<%=prodRow.get("cpid")%>">
       <input type="hidden" name="dygs" value="<%=prodRow.get("dygs")%>">
       <input type="hidden" name="isprops" value="<%=prodRow.get("isprops")%>">
       <input type="hidden" name="creator" value="<%=row.get("creator")%>">
       <input type="hidden" name="hsbl" value="<%=prodRow.get("hsbl")%>">
       <input type="hidden" name="scdwgs" value="<%=prodRow.get("scdwgs")%>">
       <input type="hidden" name="truebl" value="">
       <input type="hidden" name="chlbid" value="83">
        <td noWrap class="tableTitle">&nbsp;产品编码</td>
          <td nowrap class="td">
           <input type="text" onKeyDown="return getNextElement();" name="cpbm" <%=edClass%> value='<%=prodRow.get("cpbm")%>' style="width:130" onchange="productCodeSelect(this)" <%=readonly%>>
<%if(isCanAdd){%>
           <img style='cursor:hand;' title='单选物资' src='../images/view.gif' border=0 onClick="ProdSingleSelect('form1','srcVar=cpid&srcVar=cpbm&srcVar=pm&srcVar=gg&srcVar=chlbid&srcVar=hsbl&srcVar=scdwgs','fieldVar=cpid&fieldVar=cpbm&fieldVar=pm&fieldVar=gg&fieldVar=chlbid&fieldVar=hsbl&fieldVar=scdwgs',form1.cpid.value, 'getPrintModeValue();')">
           <img style='cursor:hand;' src='../images/delete.gif' BORDER=0 ONCLICK="cpid.value='';cpbm.value='';pm.value='';gg.value='';dmsxid.value='';sxz.value='';">
<%}%>
        </td>
          <td noWrap class="tableTitle">品名</td>
          <td nowrap class="td">
          <input type="text" onKeyDown="return getNextElement();" class="edline" name="pm" value='<%=prodRow.get("pm")%>' style="width:130" onchange="productNameSelect(this)"  readonly>
       </td>
     </tr>
     <tr>
       <td noWrap class="tableTitle">&nbsp;规格</td>
       <td nowrap class="td">
         <input type="text" onKeyDown="return getNextElement();" class="edline" name="gg" value='<%=prodRow.get("gg")%>' style="width:130" onchange="productNameSelect(this)"  readonly>
       </td>
       <td noWrap class="tableTitle">&nbsp;&nbsp;&nbsp;&nbsp;规格属性</td>
       <td class="td" nowrap><input name="sxz" <%=edClass%> value='<%=propertyBean.getLookupName(row.get("dmsxid"))%>' style="width:130" onchange="if(form1.cpid.value==''){alert('请先输入产品');return;}propertyNameSelect(this,form1.cpid.value)" onKeyDown="return getNextElement();" <%=readonly%>>
         <input type="hidden" id="dmsxid" name="dmsxid" value="<%=row.get("dmsxid")%>">
<%if(isCanAdd){%>
        <img style='cursor:hand;' src='../images/view.gif' border=0 onClick="if(form1.cpid.value==''){alert('请先输入产品');return;}if(form1.isprops.value=='0'){alert('该物资没有规格属性');return;}PropertySelect('form1','dmsxid','sxz',form1.cpid.value)">
            <img style='cursor:hand;' src='../images/delete.gif' BORDER=0 ONCLICK="dmsxid.value='';sxz.value='';">
<%}%>
       </td>
      </tr>
      <tr>
      <td noWrap class="tableTitle"><div id="divGrossNum0" style="display:block">&nbsp;&nbsp;毛重</div></td>
      <td noWrap class="td"><div id="divGrossNum1" style="display:block">
        <INPUT TYPE="text" NAME="grossNum" style="width:130" VALUE="<%=row.get("grossnum")%>" <%=edClass%> <%=readonly%> onchange="gross_onchange()"><font size="2"> kg</font>
        </div>
      </td>
       <td noWrap class="tableTitle"><div id="divOtherNum0" style="display:block">&nbsp;&nbsp;纸芯重</div></td>
      <td noWrap class="td"><div id="divOtherNum1" style="display:block">
        <INPUT TYPE="text" NAME="otherNum" style="width:130" VALUE="<%=row.get("otherNum")%>" <%=edClass%> <%=readonly%> onchange="gross_onchange()"><font size="2"> kg</font>
        </div>
      </td>
      </tr>
     <tr>
      <td id="divSaleNum" noWrap class="tableTitle">&nbsp;净重</td>
      <td noWrap class="td">
       <INPUT TYPE="TEXT" NAME="saleNum" VALUE="<%=row.get("saleNum")%>" style="WIDTH:130" <%=edClass%> <%=readonly%> onchange="sl_onchange(true)">kg
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle">&nbsp;长度</td>
      <td noWrap class="td">
       <INPUT TYPE="TEXT" NAME="produceNum" VALUE="<%=row.get("produceNum")%>" style="WIDTH:130" onchange="producesl_onchange()" <%=edClass%> <%=readonly%>><font size="2"> m</font>
      </td>
      <td noWrap class="tableTitle">&nbsp;&nbsp;&nbsp;&nbsp;生产批号</td>
       <td noWrap class="td">
        <INPUT TYPE="text" NAME="batNo" style="width:130" VALUE="<%=row.get("batNo")%>" <%=edClass%> <%=readonly%>>
      </td>
      </td>
    </tr>
     <tr>
      <td noWrap class="tableTitle">&nbsp;等级</td>
      <td noWrap class="td">
       <INPUT TYPE="TEXT" NAME="produceGrade" VALUE="<%=row.get("produceGrade")%>" style="WIDTH:130" <%=edClass%> <%=readonly%>>
      </td>
      <td noWrap class="tableTitle"><div id="divDispartNum0" style="display:block">&nbsp;&nbsp;&nbsp;&nbsp;分切班次</div></td>
       <td noWrap class="td"><div id="divDispartNum1" style="display:block">
        <INPUT TYPE="text" NAME="dispartNum" style="width:130" VALUE="<%=row.get("dispartNum")%>" <%=edClass%> <%=readonly%>>
       </div>
      </td>
      </td>
    </tr>
     <tr>
      <td noWrap class="tableTitle">&nbsp;产地</td>
      <td noWrap class="td">
       <INPUT TYPE="TEXT" NAME="produceArea" VALUE="<%=row.get("produceArea")%>" style="WIDTH:130" <%=edClass%> <%=readonly%>>
      </td>
      <td noWrap class="tableTitle"><div id="divLineNum0" style="display:block">纹号</div></td>
       <td noWrap class="td"><div id="divLineNum1" style="display:block">
        <INPUT TYPE="text" NAME="lineNum" style="width:130" VALUE="<%=row.get("lineNum")%>" <%=edClass%> <%=readonly%>>
       </div>
      </td>
      </td>
    </tr>
    <tr>
      <td noWrap class="tableTitle"><div id="divpageNum0" style="display:block">&nbsp;张数</div></td>
      <td noWrap class="td"><div id="divpageNum1" style="display:block">
       <INPUT TYPE="TEXT" NAME="pageNum" VALUE="<%=row.get("pageNum")%>" style="WIDTH:130"   onchange="sl_onchange(false)" <%=edClass%> <%=readonly%>>张
      </div></td>
      <td noWrap class="tableTitle">&nbsp;备注</td>
       <td noWrap class="td">
        <INPUT TYPE="TEXT" NAME="memo" VALUE="<%=row.get("memo")%>" style="WIDTH:130" <%=edClass%> <%=readonly%>>
      </td>
      </td>
    </tr>
    <td colspan="4" noWrap class="tableTitle" align='center'>
      <%if(isCanAdd){%><input name="button" type="button" class="button" onClick="sumitForm(<%=Operate.POST%>);" value="保存(S)">
      <pc:shortcut key="s" script='<%="sumitForm("+ Operate.POST +",-1)"%>'/><%}%>
      <input name="button2" type="button" class="button" onClick="parent.hideInterFrame()" value="关闭(X)">
      <pc:shortcut key="x" script="parent.hideInterFrame()"/>
      <input name="print" type="button" class="button" onClick="showPrint()" value="打印(P)" <%=isCanPrint ? "" : "disabled"%>>
      <pc:shortcut key="p" script='showPrint()'/>
      <%if(isCanAdd){%>
      <input name="new" type="button" class="button" onClick="showInterFrame(<%=Operate.ADD%>,-1)" value="新增(N)">
      <pc:shortcut key="n" script='<%="showInterFrame("+ Operate.ADD +",-1)"%>'/>
      </td><%}%>
    </tr>
  </table>
</form>
</BODY>
<script language="javascript">
function formatQty(srcStr){ return formatNumber(srcStr, '<%=loginBean.getQtyFormat()%>');}
function gross_onchange(){
  var grossnumObj = document.all['grossNum'];
  var slObj = document.all['saleNum'];
  var otherObj = document.all['otherNum'];
  if(grossnumObj.value=='' || otherObj.value=='')
    return;
  if(isNaN(grossnumObj.value)){
    alert("输入的毛重非法");
    grossnumObj.focus();
    return;
  }
  if(isNaN(otherObj.value)){
    alert("输入的纸芯重非法");
    otherObj.focus();
    return;
  }
  slObj.value = formatQty(parseFloat(grossnumObj.value)-parseFloat(otherObj.value));
  sl_onchange(true);
}
function sl_onchange(isBigUnit)
{
  var oldhsblObj = document.all['hsbl'];
  var sxzObj = document.all['sxz'];
  var slObj = document.all['saleNum'];
  var scslObj = document.all['produceNum'];
  var pageNumObj =document.all['pageNum'];
  var obj = isBigUnit ? slObj : pageNumObj;
  var changeObj = isBigUnit ? pageNumObj : slObj;

  if(obj.value=="")
    return;
  if(isNaN(obj.value)){
    alert("输入的数量非法");
    obj.focus();
    return;
  }
  if(obj.value<0){
    alert("不能输入小于零的数")
        return;
  }
  printMode = form1.dygs.value;
  if(printMode=='3'){
    if(changeObj.value!='')
      return;
    unitConvert(document.all['prod'], 'form1', 'srcVar=truebl', 'exp='+oldhsblObj.value, sxzObj.value, 'nsl_onchange('+isBigUnit+')');
  }
  else{
    if(scslObj.value!='')
      return;
    unitConvert(document.all['prod'], 'form1', 'srcVar=truebl', 'exp='+oldhsblObj.value, sxzObj.value, 'nsl2_onchange()');
  }
}
function nsl_onchange(isBigUnit)
{
  var slObj = document.all['saleNum'];
  var scslObj = document.all['produceNum'];
  var pageNumObj =document.all['pageNum'];
  var hsblObj = document.all['truebl'];
  var scdwgsObj = document.all['scdwgs'];
  sxz = form1.sxz.value;
  width = parseString(sxz,'<%=sysParam%>',')','(');

  var obj = isBigUnit ? slObj : pageNumObj;
  var changeObj = isBigUnit ? pageNumObj : slObj;
  if(isBigUnit){
    if(pageNumObj.value==''){
      if(hsblObj.value!="" && !isNaN(hsblObj.value))
        pageNumObj.value= formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
      else
        pageNumObj.value="";
    }
  }
  else{
    if(slObj.value==''){
      if(hsblObj.value!="" && !isNaN(hsblObj.value))
        slObj.value= formatQty(parseFloat(pageNumObj.value)*parseFloat(hsblObj.value));
      else
        slObj.value="";
    }
  }
}
function nsl2_onchange()
{
  var slObj = document.all['saleNum'];
  var scslObj = document.all['produceNum'];
  var scdwgsObj = document.all['scdwgs'];
  sxz = form1.sxz.value;
  width = parseString(sxz,'<%=sysParam%>',')','(');
  if(slObj.value=="")
    return;
  if(isNaN(slObj.value)){
    alert("输入的数量非法");
    slObj.focus();
    return;
  }
  if(slObj.value<0){
    alert("不能输入小于零的数")
    return;
  }
  if(scslObj.value==''){
    if(width=="" || width=="0" || scdwgsObj.value=="" || scdwgsObj.value=="0")
      scslObj.value= slObj.value;
    else
      scslObj.value = formatQty(parseFloat(slObj.value)*parseFloat(scdwgsObj.value)/parseFloat(width));
  }
}
function producesl_onchange()
{
  var oldhsblObj = document.all['hsbl'];
  var sxzObj = document.all['sxz'];
  var scslObj = document.all['produceNum'];
  var slObj = document.all['saleNum'];
  if(scslObj.value=="")
    return;
  if(isNaN(scslObj.value))
  {
    alert('输入的长度非法');
    scslObj.focus();
    return;
  }
  if(scslObj.value<0)
  {
    alert('输入的长度小于零');
    scslObj.focus();
    return;
  }
  if(slObj.value!='')
    return;
  unitConvert(document.all['prod'], 'form1', 'srcVar=truebl', 'exp='+oldhsblObj.value, sxzObj.value, 'newproducesl_onchange()');
}
function newproducesl_onchange(i)
{
  var slObj = document.all['saleNum'];
  var scslObj = document.all['produceNum'];
  var hsblObj = document.all['truebl'];
  var scdwgsObj = document.all['scdwgs'];
  sxz = form1.sxz.value;
  width = parseString(sxz,'<%=sysParam%>',')','(');

  if(slObj.value==''){
    if(width=="" || width=="0" || width=="" || width=="0")
      slObj.value= scslObj.value;
    else
      slObj.value = formatQty(parseFloat(scslObj.value)*parseFloat(width)/parseFloat(scdwgsObj.value));
  }
  printMode = form1.dygs.value;
  if(printMode=='3'){
    var pageNumObj =document.all['pageNum'];
    if(pageNumObj.value==''){
      if(hsblObj=="" || isNaN(hsblObj.value) || hsblObj.value=="0")
        pageNumObj.value = slObj.value;
      else
        pageNumObj.value = formatQty(parseFloat(slObj.value)/parseFloat(hsblObj.value));
    }
  }
}
var scaner = parent.scaner;
  function showInterFrame(oper, rownum)
{
  location.href= "certified_card_edit.jsp?operate="+oper+"&rownum="+rownum;
  //document.all.interframe1.src = url;
  //showFrame('detailDiv',true,"",true);
}
function parseString(sxz,s,t,y){
  if(sxz=='' || s=='' || t=='' || y=='')
    return '';
  else{
    leng = sxz.length;//得到字符串长度
    start= sxz.indexOf(s);//得到sxz第一个s出现的位置
    startValue = sxz.substring(start, leng);//截掉第一个出现位置前面的字符
    end = startValue.indexOf(t);
    temp = startValue.substring(0,end);
    cur= temp.indexOf(y);
    value = temp.substring(cur+1, temp.length);
    return value;
  }
}
function showLength(str){
  start= str.indexOf(")");
  startValue = str.substring(start+2,str.length);
  productlen=parseString(startValue,'长度',')','(');
  return productlen;
}
var doUrl ='<%=engine.util.StringUtils.replace(curUrl, "certified_card_edit.jsp", "do.dll")%>';
var zebraUrl ='<%=engine.util.StringUtils.replace(curUrl, "certified_card_edit.jsp", "ZEBRA32.DLL")%>';
function showPrint(){
  WorkNo='<%=WorkNo%>';                          //质检员:工号
  sxz = form1.sxz.value;
  width = parseString(sxz,'<%=sysParam%>',')','(');
  pzzlength = parseString(sxz,'长度',')','(');
  gg1=form1.gg.value;
  dl = gg1.substring(0, gg1.length-1)+'g/m2';    //定量
  pm = form1.pm.value;                           //品名
  cpbm=form1.cpbm.value;                         //产品编码
  gg2= gg1+width;                                //卷筒纸规格
  pzlen= pzzlength +'*'+width;   //平张纸规格长度乘宽度
  dmsxid=form1.dmsxid.value;
  qualityer = form1.qualityer.value;             //质检员:ID
  saleNum = form1.saleNum.value+'kg';            //净重
  pageNum=form1.pageNum.value+'张';              //平张纸张数
  otherNum = form1.otherNum.value+'kg';          //纸芯重
  produceNum = form1.produceNum.value+'m';       //长度
  produceGrade = form1.produceGrade.value;       //等级
  dispartNum = form1.dispartNum.value;           //分切班次
  produceArea = form1.produceArea.value;         //产地
  lineNum = form1.lineNum.value;                 //纹号
  batNo = form1.batNo.value;                     //生产编号<--生产批号
  cardDate = form1.cardDate.value;               //生产日期<--合格证日期
  printFormat = form1.dygs.value;                //打印格式
  temp=cpbm+'-'+dmsxid;                          //产品编码+规格属性ID
  //cardDate = form1.
  //alert(temp);
  if(printFormat == '1')
  {
      var a = scaner.Print(1,false,doUrl,zebraUrl,pm,gg2,qualityer,lineNum,saleNum,
                          produceGrade,produceNum, dl, produceArea,batNo,cardDate,batNo,temp);
  }
  else if(printFormat == '2')
  {
      var b = scaner.Print(2,false,doUrl,zebraUrl,pm,gg2,qualityer,produceGrade,saleNum,
                  otherNum,produceNum,dispartNum,produceArea,batNo,cardDate,batNo,temp);
  }
  else
  {
      var c = scaner.Print(3,false,doUrl,zebraUrl,pm,pzlen,batNo,cardDate,saleNum,
                       dispartNum,produceGrade,qualityer,temp,batNo,pageNum,'','');
  }
}
function showBillStyle()
{
  var printMode = form1.dygs.value;
  //alert(printMode);
  if(printMode == '1')//纸
  {
    //显示4=文号，隐藏6=纸芯重8=分切班次， 静重->重量
    document.all.divLineNum0.style.display = 'block';
    document.all.divLineNum1.style.display = 'block';
    form1.lineNum.disabled = false;

    document.all.divOtherNum0.style.display = 'none';
    document.all.divOtherNum1.style.display = 'none';
    form1.otherNum.disabled = true;
    document.all.divGrossNum0.style.display = 'none';
    document.all.divGrossNum1.style.display = 'none';
    form1.grossNum.disabled = true;

    document.all.divDispartNum0.style.display = 'none';
    document.all.divDispartNum1.style.display = 'none';
    form1.dispartNum.disabled = true;

    document.all.divpageNum0.style.display = 'none';
    document.all.divpageNum1.style.display = 'none';
    form1.pageNum.disabled = true;

    document.all.divSaleNum.innerHTML = '&nbsp;重量';
  }
  else if(printMode == '2')
  {
    document.all.divLineNum0.style.display = 'none';
    document.all.divLineNum1.style.display = 'none';
    form1.lineNum.disabled = true;

    document.all.divOtherNum0.style.display = 'block';
    document.all.divOtherNum1.style.display = 'block';
    form1.otherNum.disabled = false;
    document.all.divGrossNum0.style.display = 'block';
    document.all.divGrossNum1.style.display = 'block';
    form1.grossNum.disabled = false;

    document.all.divDispartNum0.style.display = 'block';
    document.all.divDispartNum1.style.display = 'block';
    form1.dispartNum.disabled = false;


    document.all.divpageNum0.style.display = 'none';
    document.all.divpageNum1.style.display = 'none';
    form1.pageNum.disabled = true;

    document.all.divSaleNum.innerHTML = '&nbsp;净重';
  }
  else{
    document.all.divLineNum0.style.display = 'block';
    document.all.divLineNum1.style.display = 'block';
    form1.lineNum.disabled = false;

    document.all.divOtherNum0.style.display = 'none';
    document.all.divOtherNum1.style.display = 'none';
    form1.otherNum.disabled = true;
    document.all.divGrossNum0.style.display = 'none';
    document.all.divGrossNum1.style.display = 'none';
    form1.grossNum.disabled = true;

    document.all.divDispartNum0.style.display = 'block';
    document.all.divDispartNum1.style.display = 'block';
    form1.dispartNum.disabled = false;

    document.all.divpageNum0.style.display = 'block';
    document.all.divpageNum1.style.display = 'block';
    form1.pageNum.disabled = false;


    document.all.divSaleNum.innerHTML = '&nbsp;重量';
  }
}
</script>
<%=prodRow.get("creator")%>
</Html>
<%--生产任务单引入物料需求界面--%><%@ page contentType="text/html;charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<SCRIPT LANGUAGE="javascript">
/**
 * sxz 要解析的字符串
 * s    第一次分割索引，分隔掉s第一次出现的位置前面的字符
 * t    第二次分割的索引，分割第一次分割后的字符串，把第一次出现t后面的字符隔掉
 * y    最后一次分割的索引，分割第二次分割后的字符串，分隔掉y第一次出现的位置前面的字符
 */
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
  function addDate(sdate,sl)
  {
    var datestr = sdate.replace(/-/gi, "/");
    var dt = new Date(datestr);
    var dt2 = new Date(dt.getYear() + "/" + (dt.getMonth() + 1) + "/" + (dt.getDate()+sl));
    var obj = dt2.getYear() + "-" + (dt2.getMonth() + 1) + "-" + dt2.getDate();
    return obj;
  }
</SCRIPT>

</html>
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
   //lockScreenToWait("处理中, 请稍候！");
    //form1.rownum.value = row;
    //form1.operate.value = oper;
   //form1.submit();
   }
   function backList()
   {
     location.href='mrp.jsp';
   }
   function productCodeSelect(obj, i)
   {
     ProdCodeChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                    'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value,'sumitForm(10002,'+i+')');
   }
   function productNameSelect(obj,i)
  {
    ProdNameChange(document.all['prod'], obj.form.name, 'srcVar=cpid_'+i+'&srcVar=cpbm_'+i+'&srcVar=product_'+i+'&srcVar=jldw_'+i,
                 'fieldVar=cpid&fieldVar=cpbm&fieldVar=product&fieldVar=jldw', obj.value,'sumitForm(10002,'+i+')');
  }
  function propertyNameSelect(obj,cpid,i)
  {
    PropertyNameChange(document.all['prod'], obj.form.name, 'srcVar=dmsxid_'+i+'&srcVar=sxz_'+i,
                       'fieldVar=dmsxid&fieldVar=sxz', cpid, obj.value,'propertyChange('+i+')');
  }
function corpNameSelect(obj)
{
  ProvideNameChange(document.all['prod'], obj.form.name, 'srcVar=scjhid&srcVar=jhh',
                 'fieldVar=scjhid&fieldVar=jhh', obj.value, 'sumitForm(10501)');
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<iframe id="prod" src="" width="95%" height=25 marginwidth=0 marginheight=0 hspace=0 vspace=0 frameborder=0 scrolling=no style="display:none"></iframe>

<form name="form1" action="../produce/mrp_edit.jsp" method="POST" onsubmit="return false;" onKeyDown="return onInputKeyboard();">
  <table WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0><tr>
    <td align="center" height="5"></td>
  </tr></table>
  <INPUT TYPE="HIDDEN" NAME="operate" value="">
  <INPUT TYPE="HIDDEN" NAME="rownum" value="">
  <table BORDER="0" CELLPADDING="1" CELLSPACING="0" align="center" width="760">
    <tr valign="top">
      <td><table border=0 CELLSPACING=0 CELLPADDING=0 class="table">
        <tr>
            <td class="activeVTab">物料需求计划维护(未审核)</td>
          </tr>
        </table>
        <table class="editformbox" CELLSPACING=1 CELLPADDING=0 width="100%">
          <tr>
            <td>
              <table CELLSPACING="1" CELLPADDING="1" BORDER="0" width="100%" bgcolor="#f0f0f0">
                 
                  <tr>
                  <td noWrap class="tdTitle">物料需求号</td>
                  <td noWrap class="td"><input type="text" name="wlxqh" value='' maxlength='32' style="width:110" class="edline" onKeyDown="return getNextElement();" readonly></td>
                  <td noWrap class="tdTitle">日期</td>
                  <td noWrap class="td"><input type="text" name="rq" value='2004-05-22' maxlength='10' style="width:85" class=edbox onChange="checkDate(this)" onKeyDown="return getNextElement();">
                    
                    <a ><img style='cursor:hand' align="absmiddle" src="../images/seldate.gif" width="20" height="16" border="0" title="选择日期" onclick="selectDate(form1.rq);"></a>
                    
                  </td>
                  <td noWrap class="tdTitle">部门</td>
                  <td noWrap class="td">
                    
                    
                    <DIV ID='d2_deptid' class=edbox style='width:5;width:110' onClick=ToggleSelect('deptid')><table border=0 cellspacing=0 cellpadding=0><tr><td nowrap width='100%'><INPUT CLASS='ednone' NAME='v_deptid' style='width:100%' onKeyDown="return SelectKeyDown('deptid');" onKeyUp='SelectKeyUp(this.value);' onChange="SelectChange('deptid');" value=''></td><td nowrap><IMG SRC='../images/down_arrow.gif' style='cursor:hand;' border=0></td></tr></table><INPUT TYPE=HIDDEN NAME='deptid' link='v_deptid' value=''></DIV>
<script language='javascript'>o_deptid=new TSelectObject('deptid','deptid','v_deptid','d1_deptid','d2_deptid',false,"if(form1.deptid.value!='361'){sumitForm(10031);}",true);RegisterSelect(o_deptid);SetDestSelectObject(o_deptid);AddSelectItem('','');

                      AddSelectItem('361','总经理办公室');AddSelectItem('362','行政部');AddSelectItem('363','财务部');AddSelectItem('364','设备部');AddSelectItem('365','质量管理部');AddSelectItem('366','进出口采购部');AddSelectItem('367','销售部');AddSelectItem('368','生产部');AddSelectItem('373','技术研发部');SetSelectedIndex('361'); </script>

                    
                  </td>
                  <td noWrap class="tdTitle">生产计划号</td>
                   <td class="td" nowrap>
<input type="hidden" name="scjhid" value="">
<input class="edline" style="WIDTH:110px" name="jhh" value="" onKeyDown="return getNextElement();" onchange="corpNameSelect(this);">
                  
                   
                   <img style='cursor:hand' src='../images/view.gif' border=0 onClick="if(form1.deptid.value==''){alert('请选择车间');return;}ProduceSingleSelect('form1','srcVar=scjhid&srcVar=jhh','fieldVar=scjhid&fieldVar=jhh',form1.deptid.value,0,'sumitForm(21,-1)')">
                   <img style='cursor:hand' src='../images/delete.gif' BORDER=0 ONCLICK="scjhid.value='';jhh.value='';sumitForm(10031)">
                   
                   </td>
                </tr>
                
                <tr>
                  <td colspan="8" noWrap class="td"><div style="display:block;width:750;height:350;overflow-y:auto;overflow-x:auto;">
                    <table id="tableview1" width="750" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
                      <tr class="tableTitle">
                      <td nowrap width=10></td>
                         <td height='20' align="center" nowrap>
                           
                          <input name="image" class="img" type="image" title="新增(A)" onClick="sumitForm(11131)" src="../images/add_big.gif" border="0">
                          <script language='javascript'>GetShortcutControl(65,"sumitForm(11131,-1)");</script>

                        </td>
                        <td height='20' nowrap>销售订单号</td>
                        <td height='20' nowrap>产品编码</td>
                          <td height='20' nowrap>品名规格(款式花号)</td>
                        <td nowrap>规格属性</td>
                        <td height='20' nowrap>库存量</td>
                        <td height='20' nowrap>计划可供量</td>
                        <td nowrap>需求日期</td>
                        <td nowrap>计量需求量</td>
                        <td nowrap>需购量</td>
                        <td height='20' nowrap>计量单位</td>
                        <td nowrap>生产需求量</td>
                        <td height='20' nowrap>生产单位</td>
                        <td nowrap>工艺类型</td>
                        <td nowrap>已排任务量</td>
                        <td nowrap>存货性质</td>
                        <td nowrap>层次</td>
                        <td nowrap>备注</td>
                        
                      </tr>
                    
                      <tr id="rowinfo_0">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_1">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_2">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_3">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_4">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_5">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_6">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_7">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_8">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_9">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_10">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_11">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_12">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_13">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                      <tr id="rowinfo_14">
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td><td class="td">&nbsp;</td><td class="td">&nbsp;</td>
                        <td class="td">&nbsp;</td>
                        
                      </tr>
                      
                    </table></div>
                    <SCRIPT LANGUAGE="javascript">rowinfo = new RowControl();
                    AddRowItem(rowinfo,'rowinfo_0');AddRowItem(rowinfo,'rowinfo_1');AddRowItem(rowinfo,'rowinfo_2');AddRowItem(rowinfo,'rowinfo_3');AddRowItem(rowinfo,'rowinfo_4');AddRowItem(rowinfo,'rowinfo_5');AddRowItem(rowinfo,'rowinfo_6');AddRowItem(rowinfo,'rowinfo_7');AddRowItem(rowinfo,'rowinfo_8');AddRowItem(rowinfo,'rowinfo_9');AddRowItem(rowinfo,'rowinfo_10');AddRowItem(rowinfo,'rowinfo_11');AddRowItem(rowinfo,'rowinfo_12');AddRowItem(rowinfo,'rowinfo_13');AddRowItem(rowinfo,'rowinfo_14');InitRowControl(rowinfo);</SCRIPT></td>
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
            <td class="td"><b>登记日期:</b>2004-05-22</td>
            <td class="td"></td>
            <td class="td" align="right"><b>制单人:</b>yl</td>
          </tr>
          <tr>
            <td colspan="3" noWrap class="tableTitle">
              <input name="button2" type="button" class="button" onClick="sumitForm(19);" value='保存添加(N)'>
              <script language='javascript'>GetShortcutControl(78,"sumitForm(19,-1)");</script>

              <input name="btnback" type="button" class="button" onClick="sumitForm(14);" value='保存返回(S)'>
              <script language='javascript'>GetShortcutControl(83,"sumitForm(14,-1)");</script>

              
              <input name="btnback" type="button" class="button" onClick="backList();" value=" 返回(C)">
                  <script language='javascript'>GetShortcutControl(67,"backList();");</script>

            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
<script language="javascript">
obj=GetInputControl('cpbm',8);obj.AdjustInputSize();obj=GetInputControl('product',8);obj.AdjustInputSize();obj=GetInputControl('jlxql',8);obj.AdjustInputSize();obj=GetInputControl('xql',8);obj.AdjustInputSize();obj=GetInputControl('xgl',8);obj.AdjustInputSize();obj=GetInputControl('bz',8);obj.AdjustInputSize();obj=GetInputControl('sxz',8);obj.AdjustInputSize();obj=GetInputControl('jhkgl',8);obj.AdjustInputSize();
  function formatQty(srcStr){ return formatNumber(srcStr, '#0.####');}
  function formatPrice(srcStr){ return formatNumber(srcStr, '#0.00####');}
  function formatSum(srcStr){ return formatNumber(srcStr, '#0.00');}
  function delMaster(){
    if(confirm('是否删除该记录？'))
      sumitForm(13,-1);
  }
  function propertyChange(i){
    var sxzObj = document.all['sxz_'+i];
    var scdwgsObj = document.all['scdwgs_'+i];
    if(sxzObj.value=='')
      return;
    var widthObj = document.all['width_'+i];
    widthValue = parseString(sxzObj.value, '宽度(', ')', '(');
    if(widthValue=='')
      return;
    widthObj.value =  widthValue;
    if(widthObj.value=='' || isNaN(widthObj.value))
      return;
    var jhkglObj = document.all['jhkgl_'+i];
    var jlxqlObj = document.all['jlxql_'+i];//计量需求量，即单位为计量单位
    var xglObj = document.all['xgl_'+i];
    var xqlObj = document.all['xql_'+i];//需求量，生产单位
    jhkgl = jhkglObj.value=="" ? 0 : parseFloat(jhkglObj.value);
    jlxql = jlxqlObj.value=="" ? 0 : parseFloat(jlxqlObj.value);
    xgl = xglObj.value=="" ? 0 : parseFloat(xglObj.value);
    if(jlxqlObj.value=='' && xqlObj.value=='')
      return;
    else if(jlxqlObj.value!=''){
      xqlObj.value = formatQty(parseFloat(jlxqlObj.value)*parseFloat(scdwgsObj.value)/parseFloat(widthValue));
      if(jhkgl<=0)
          xglObj.value = jlxqlObj.value;
        else if(jhkgl>=jlxql)
          xglObj.value=0;
        else if(jhkgl<jlxql)
          xglObj.value = jlxql - jhkgl;
    }
    else if(jlxqlObj.value=='' && xqlObj.value!=''){
      jlxqlObj.value = formatQty(parseFloat(xqlObj.value)*parseFloat(widthValue)/parseFloat(scdwgsObj.value));
      if(jhkgl<=0)
        xglObj.value = jlxqlObj.value;
      else if(jhkgl>=jlxqlObj.value)
          xglObj.value=0;
        else if(jhkgl<jlxqlObj.value)
          xglObj.value = jlxqlObj.value - jhkgl;
    }
  }
    function ProduceSingleSelect(frmName,srcVar,fieldVar,curID,jhlx,methodName,notin)
    {
      var winopt = "location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450  top=0 left=0";
      var winName= "SingleProdSelector";
      paraStr = "../produce/select_produceplan.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&deptid="+curID+"&jhlx="+jhlx;
      if(methodName+'' != 'undefined')
        paraStr += "&method="+methodName;
      if(notin+'' != 'undefined')
        paraStr += "&notin="+notin;
      newWin =window.open(paraStr,winName,winopt);
      newWin.focus();
    }
    function changeEvent(i, isChange)
    {
      var jhkglObj = document.all['jhkgl_'+i];
      var jlxqlObj = document.all['jlxql_'+i];//计量需求量，即单位为计量单位
      var xglObj = document.all['xgl_'+i];
      var xqlObj = document.all['xql_'+i];//需求量，生产单位
      var widthObj = document.all['width_'+i];//规格属性的宽度
      var scdwgsObj = document.all['scdwgs_'+i];//该行产品的生产单位公式
      var obj = isChange ? xglObj : jlxqlObj;
      var showText = isChange ? "输入的生产数量非法" : "输入的数量非法";
      var showText2 = isChange ? "输入的生产数量小于零" : "输入的数量小于零";
      var changeObj = isChange ? jlxqlObj : xglObj;
      if(obj.value=="")
        return;
      if(isNaN(obj.value))
      {
        alert(showText)
            return;
      }
      if(obj.value<=0)
     {
       alert(showText2)
           return;
      }
      jhkgl = jhkglObj.value=="" ? 0 : parseFloat(jhkglObj.value);
      jlxql = jlxqlObj.value=="" ? 0 : parseFloat(jlxqlObj.value);
      xgl = xglObj.value=="" ? 0 : parseFloat(xglObj.value);
      width = (widthObj.value=="" || isNaN(widthObj.value)) ? 0 : parseFloat(widthObj.value);
      scdwgs = scdwgsObj.value=="" ? 0 : parseFloat(scdwgsObj.value);
      if(!isChange){
        if(jhkgl<=0)
          xglObj.value = jlxqlObj.value;
        else if(jhkgl>=jlxql)
          xglObj.value=0;
        else if(jhkgl<jlxql)
          xglObj.value = jlxql - jhkgl;
      }
      else{
        if(jhkgl<=0)
          jlxqlObj.value = xgl;
        else
          jlxqlObj.value = jhkgl+xgl;
      }
      if(width==0 || scdwgsObj.value==0)
        xqlObj.value = jlxqlObj.value;
      else
        xqlObj.value = formatQty(parseFloat(jlxqlObj.value)*scdwgs/width);
    }
    function xql_change(i)
    {
      var jhkglObj = document.all['jhkgl_'+i];
      var jlxqlObj = document.all['jlxql_'+i];//计量需求量，即单位为计量单位
      var xglObj = document.all['xgl_'+i];
      var xqlObj = document.all['xql_'+i];//需求量，生产单位
      var scdwgsObj = document.all['scdwgs_'+i];//生产单位公式
      var widthObj = document.all['width_'+i];//规格属性的宽度
      var showText3 = "输入的生产数量非法";
      var showText4 =  "输入的生产数量小于零" ;
      if(xqlObj.value=="")
        return;
      if(isNaN(xqlObj.value))
      {
        alert(showText3)
            return;
      }
      if(xqlObj.value<=0)
      {
        alert(showText4)
            return;
      }
      jhkgl = jhkglObj.value=="" ? 0 : parseFloat(jhkglObj.value);
      xgl = xglObj.value=="" ? 0 : parseFloat(xglObj.value);
      width = (widthObj.value=="" || isNaN(widthObj.value)) ? 0 : parseFloat(widthObj.value);
      scdwgs = scdwgsObj.value=="" ? 0 : parseFloat(scdwgsObj.value);
      if(width==0 || scdwgs==0){
        jlxqlObj.value = xqlObj.value;
        if(jhkgl<=0)
          xglObj.value = xqlObj.value;
        else if(jhkgl>=xqlObj.value)
          xglObj.value=0;
        else if(jhkgl<xqlObj.value)
          xglObj.value = xqlObj.value - jhkgl;
      }
      else{
        jlxqlObj.value = formatQty(parseFloat(xqlObj.value)*width/scdwgs);
        temp  = formatQty(parseFloat(xqlObj.value)*width/scdwgs);
        if(jhkgl<=0)
          xglObj.value = temp;
        else if(jhkgl>=temp)
          xglObj.value=0;
        else if(jhkgl<temp)
          xglObj.value = temp - jhkgl;
      }
    }
    function judge(i)
    {
      var xgl = document.all['xgl_'+i];
      if(xgl.value=="")
        return;
      if(isNaN(xgl.value))
      {
        alert('输入的需购量非法')
            return;
      }
    }
  </script>
  

</body>
</html>

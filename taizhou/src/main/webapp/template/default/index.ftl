


<html>
<head>
<title>往来单位信息列表</title>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${base}/${brandingStaticLocation}css/zh_cn/public.css" type="text/css">
</head>
<script language="javascript" src="/scripts/validate.js"></script>

<script language="javascript" src="/scripts/exchangeselect.js"></script>
<script language="javascript">
function toDetail(){
  location.href='corpedit.jsp';
}
function sumitForm(oper, row)
{
  lockScreenToWait("处理中, 请稍候！");
  form1.operate.value = oper;
  form1.rownum.value = row;
  form1.submit();
}
function showSearchFrame(){
  showFrame('searchframe1', true, "", true);
}
rowColorLight = "";
rowColorDark = "";
rowColorHighlight = "";
rowColorSelected = "";
rowStartIdx = "";
function initDefaultTableRow(tableName,startRow, enableDiv)
{
  initTableRow(tableName, startRow, '#ddeeff','#F2F9FC','#f0f0e0', '#00ccff', enableDiv);
}
function initTableRow(tableName,startRow,colorLight,colorDark,colorHigh,colorSelected, enableDiv)
{
  var tbl = document.all[tableName];
  var row;
  var i;
  rowColorLight = colorLight;
  rowColorDark = colorDark;
  rowColorHighlight = colorHigh;
  rowColorSelected = colorSelected;
  rowStartIdx = startRow;
  for (i=startRow;i<tbl.rows.length;i++)
  {
    row = tbl.rows[i];
    isLight = (i+startRow)%2==0;
    row.style.backgroundColor = isLight ? colorLight : colorDark;
    if (colorHigh!=''){
      row.onmouseover = highlightRow;
      row.onmouseout = normalRow;
    }
  }
}
</script>
<BODY oncontextmenu="window.event.returnValue=true">
<TABLE WIDTH="100%" BORDER=0 CELLSPACING=0 CELLPADDING=0 CLASS="headbar">
  <TR>
    <TD NOWRAP align="center">往来单位列表</TD>
  </TR></TABLE>

<form name="form1" method="post" action="http://localhost:8000/engineERP/baseinfo/corplist.jsp" onsubmit="return false;" onKeyDown="return onInputKeyboard();" >
  <INPUT TYPE="HIDDEN" NAME="operate" VALUE="">
  <INPUT TYPE="HIDDEN" NAME="rownum" VALUE="">
  
  <table id="tbcontrol" width="95%" border="0" cellspacing="1" cellpadding="1" align="center">
    <tr>
      <td class="td" nowrap>
      <img src='/engineERP/images/arrow_1st_n.gif' border=0 alt='首页'>&nbsp;<img src='/engineERP/images/arrow_left_n.gif' border=0 alt='前一页'>&nbsp;<img src='/engineERP/images/arrow_right_n.gif' border=0 alt='后一页'>&nbsp;<img src='/engineERP/images/arrow_end_n.gif' border=0 alt='尾页'>&nbsp;到第<input type='text' name='goPage' size ='3' value=1 readonly style='border-style:solid; border-width:1px; border-color:#3399ff; background-color:lightcyan; color:red; font-size:8pt; width: auto'' onchange="location.href='http://localhost:8000/engineERP/baseinfo/corplist.jsp?pageNo='+this.value">页&nbsp;共1页&nbsp;2条记录
</td>
      <td class="td" align="right"><input name="search" type="button" class="button" onClick="showFixedQuery()" value="查询(Q)">
        <input name="sort" type="button" class="button" onClick="showSort()" value="排序(P)">
        <script language='javascript'>GetShortcutControl(80,"showSort()");</script>

        <input name="button22" type="button" class="button" onClick="location.href='../pub/main.jsp'" value="返回(C)">
        <script language='javascript'>GetShortcutControl(67,"location.href=../pub/main.jsp");</script>
</td>
    </tr>
  </table>
  
  <table id="tableview1" width="95%" border="0" cellspacing="1" cellpadding="1" class="table" align="center">
    <tr class="tableTitle">
      <td valign="middle" align="center" width=25 nowrap><input name="image" class="img" type="image" title="新增(ALT+A)" onClick="sumitForm(11,-1)" src="../images/add.gif" border="0">
      <script language='javascript'>GetShortcutControl(65,"sumitForm(21,-1)");</script>
</td>
      <td nowrap height='20'>单位编号</td><td nowrap height='20'>单位名称</td><td nowrap height='20'>国家</td><td nowrap height='20'>区号</td><td nowrap height='20'>地区</td><td nowrap height='20'>所属部门</td><td nowrap height='20'>所属业务员</td><td nowrap height='20'>助记码</td><td nowrap height='20'>地址</td><td nowrap height='20'>电话</td><td nowrap height='20'>传真</td><td nowrap height='20'>电子邮件</td><td nowrap height='20'>网址</td><td nowrap height='20'>邮编</td><td nowrap height='20'>联系人</td><td nowrap height='20'>税号</td><td nowrap height='20'>法人代表</td><td nowrap height='20'>开户行</td><td nowrap height='20'>帐号</td><td nowrap height='20'>关系深度</td><td nowrap height='20'>资信等级</td><td nowrap height='20'>所有制</td><td nowrap height='20'>行业</td><td nowrap height='20'>业务类型</td>
    </tr>
    
    <tr class="tableTitle" >
      <td align="center" class="td"><input name="image2" class="img" type="image" title='修改' onClick="sumitForm(12,0)" src="../images/edit.gif" border="0">
        
      </td>
      <td nowrap class="td">001</td><td nowrap class="td">admin1</td><td nowrap class="td">美国</td><td nowrap class="td">001</td><td nowrap class="td">美国</td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td">未知型</td><td nowrap class="td">★</td><td nowrap class="td"></td><td nowrap class="td"></td><td nowrap class="td">供货单位,销货单位</td>
    </tr>
      
  </table>
</form>

<script LANGUAGE="javascript">
initDefaultTableRow('tableview1',1);
function sumitFixedQuery(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  fixedQueryform.operate.value = oper;
  fixedQueryform.submit();
}
function showFixedQuery(){
  hideFrame('divOrder')
  showFrame('fixedQuery', true, "", true);
}
function sumitSearchFrame(oper)
{
  lockScreenToWait("处理中, 请稍候！");
  searchform.operate.value = oper;
  searchform.submit();
}
function showSearchFrame(){
  hideFrame('fixedQuery');
  showFrame('searchframe', true, "", true);
}
</script>
</body>
</html>

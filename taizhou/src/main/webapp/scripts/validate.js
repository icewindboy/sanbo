//<--onclick,onkeydown-->
document.onclick = hidemenu;
document.onkeydown = onkeyboard;
// BrowserCheck Object
function TBrowser() {
  var b = navigator.appName;
  if (b=="Netscape")
    this.b = "ns";
  else if (b == "Microsoft Internet Explorer")
    this.b = "ie";
  else
    this.b = b;
  this.version = navigator.appVersion;
  this.v = parseInt(this.version);
  this.ns = (this.b=="ns" && this.v>=4);
  this.ns4 = (this.b=="ns" && this.v==4);
  this.ns5 = (this.b=="ns" && this.v==5);
  this.ie = (this.b=="ie" && this.v>=4);
  this.ie4 = (this.version.indexOf('MSIE 4')>0);
  this.ie5 = (this.version.indexOf('MSIE 5')>0);
  this.min = (this.ns || this.ie);
  return this;
}
//
function hidemenu()
{
  time = 0;
  var parentObj = parent;
  while(parentObj != null)
  {
    if(time > 3)
      break;
    var obj = parentObj.document.all['public_left_menu'];
    if(obj != null){
      parentObj.slideIn();
      break;
    }
    parentObj = parentObj.parent;
    time++;
  }
}
function onkeyboard()
{
  keyCode = window.event.keyCode;
  //disable back
  if(keyCode == 8)//backspace
  {
    var obj = event.srcElement;
    tagname = obj.tagName.toUpperCase();
    isinput = tagname=='TEXTAREA' || (tagname=='INPUT' && (obj.type == 'text' || obj.type == 'password'));
    return isinput && !obj.readOnly;
  }
  //disable ctrl+n
  else if(keyCode==78 && event.ctrlKey)//ctrl+n
    return false;
  else if(event.altKey)
    return procShortcutKey(keyCode);
  else if(keyCode==116)//f5
    return false;
}
//处理快捷键
function procShortcutKey(keyCode)
{
  var obj = FindShortcutControl(keyCode);
  if(obj != null)
  {
    var srcobj = event.srcElement;
    var methodstr = srcobj.onchange;
    if(methodstr != null && methodstr != 'undefined')
      srcobj.onchange();
    methodstr = srcobj.onblur;
    if(methodstr != null && methodstr != 'undefined')
      srcobj.onblur();
    obj.notify();
  }
}

//扩展 String 对象的方法
String.prototype.trim = function() {
  return this.replace(/(^\s*)|(\s*$)/g, "");
}

String.prototype.ltrim = function() {
  return this.replace(/(^\s*)/g, "");
}

String.prototype.rtrim = function() {
  return this.replace(/(\s*$)/g, "");
}
/**
 * 检查数值类型。若不是有效的数值，则弹出警告信息，并将焦点定位于该文本框
 * @param obj 文本输入框对象
 * @return true - 有效；false - 无效
 */
function checkNumber(obj) {
  if (isNaN(obj.value)){
    alert(obj.value + " 不是有效的数值。请核对后重新输入。");
    obj.focus();
    obj.select();
    return false;
  }
  return true;
}
/**
 * 检查日期类型。若不是有效的日期字串，则弹出警告信息，并将焦点定位于该文本框
 * @param obj 文本输入框对象
 * @return true - 有效；false - 无效
 */
function checkDate(obj) {
  if (obj.value.trim() == ""){
    return true;
  }

  var datestr = obj.value.replace(/-/gi, "/");
  var dt = new Date(datestr);
  if (isNaN(dt)){
    alert(obj.value + " 不是有效的日期字串。请核对后重新输入。");
    obj.focus();
    obj.select();
    return false;
  }
  //bug:year小于2000的，得到的是后面两位树的
  obj.value = (dt.getYear()<100 ? dt.getYear()+1900 : dt.getYear()) + "-" + (dt.getMonth() + 1) + "-" + dt.getDate();
  return true;
}
/**
 * 选择日期
 * @param obj 文本框，初始日期由该文本框的值决定，选择后的日期将赋值给该文本框
 */
function selectDate(obj) {
  showx = event.screenX - event.offsetX - 180;
  showy = event.screenY - event.offsetY + 18;
  showx = showx < 0 ? 0 : showx;
  showy = showy < 0 ? 0 : showy;
  window.showModalDialog("../pub/calendar.htm", obj, "dialogWidth:197px; dialogHeight:205px; dialogLeft:"+showx+"px; dialogTop:"+showy+"px; status:no; directories:yes;scrollbars:no;Resizable=no; ");
}
function select_Date(contextpath,obj) {
	  showx = event.screenX - event.offsetX - 180;
	  showy = event.screenY - event.offsetY + 18;
	  showx = showx < 0 ? 0 : showx;
	  showy = showy < 0 ? 0 : showy;
	  window.showModalDialog(contextpath+"/pub/calendar.htm", obj, "dialogWidth:197px; dialogHeight:205px; dialogLeft:"+showx+"px; dialogTop:"+showy+"px; status:no; directories:yes;scrollbars:no;Resizable=no; ");
}
/**
 * 选择日期
 * @param obj 文本框，初始日期由该文本框的值决定，选择后的日期将赋值给该文本框
 */
function rootSelectDate(obj) {
  showx = event.screenX - event.offsetX - 180;
  showy = event.screenY - event.offsetY + 18;
  showx = showx < 0 ? 0 : showx;
  showy = showy < 0 ? 0 : showy;

  window.showModalDialog("pub/calendar.htm", obj, "dialogWidth:197px; dialogHeight:205px; dialogLeft:"+showx+"px; dialogTop:"+showy+"px; status:no; directories:yes;scrollbars:no;Resizable=no; ");
}
/**
 * 获取屏幕分辨率
 * @return 分辨率类型。0 - 640 * 480，1 - 800 * 600，2 - 1024 * 768，3 - 1280 * 720，4 - higher
 */
function getScreenType() {
  if (screen.width < 800){//>640
    return 0;
  }
  else if (screen.width < 1024) {
    return 1;
  }
  else if (screen.width < 1280) {
    return 2;
  }
  else if (screen.width <= 1600) {
    return 3;
  }
  else{
    return 4;
  }
}
//禁止一切可以引起交的对象，如按钮、图片、超链等
function disableActions() {
  for(var i = 0; i < document.images.length; i++){
    document.images[i].disabled = true;
  }

  for(var i = 0; i < document.links.length; i++){
    document.links[i].href = "javascript: void(0)";
  }

  for(var i = 0; i < document.forms.length; i++){
    for(var j = 0; j < document.forms[i].elements.length; j++){
      if (document.forms[i].elements[j].type == "button"){
        document.forms[i].elements[j].disabled = true;
      }
    }
  }
}
function onInputKeyboard()
{
  var keyCode = window.event.keyCode;
  //33, 34, 37, 38, 39, 40, 13 pageup, pagedown, left, up, right, down
  if (!(keyCode==33 || keyCode==34 || keyCode==13 ||
        keyCode==37 || keyCode==38 || keyCode==39 || keyCode==40)){
    return true;
  }
  var obj = event.srcElement;
  var tagname = obj.tagName;
  if(!(tagname=="INPUT" && obj.type != "hidden")|| tagname=="SELECT" || tagname=="TEXTAREA")
    return true;
  var isButton = tagname=="INPUT" && (obj.type=="button" || obj.type=="submit");
  var selObj = null;
  if(isButton && (keyCode==37 || keyCode==38))
    selObj = prevElement(obj);
  else if(isButton && (keyCode==39 || keyCode==40))
    selObj = nextElement(obj);
  else if((!isButton && obj.type!="textarea" && keyCode==13) ||
          (obj.type=="textarea" && keyCode==13 && event.ctrlKey))
    selObj = nextElement(obj);
  else if(!isButton && (keyCode==33 || keyCode==34))
  {
    pos = obj.name.lastIndexOf("_");
    if(pos < 0)
      return true;
    posAfter = obj.name.substring(pos+1);
    if(isNaN(posAfter))
      return true;
    crease = keyCode==33 ? -1 : 1;
    selObjName = obj.name.substring(0, pos+1) + (parseInt(posAfter) + crease);
    selObj = obj.form.all[selObjName];
  }
  //
  if(selObj!=null) //event.keyCode==9
  {
    selObj.focus();
    if(selObj.tagName.toUpperCase()!="SELECT" && selObj.type != "button")
      selObj.select();
    return false;
  }
  return true;
}
//回车跳到下一个输入框
function getNextElement()
{
  return true;
}
//得到上一个输入框
function prevElement(field){
  var form = field.form;
  var e=0;
  var prev;
  for(; e <form.elements.length; e++)
    if(field == form.elements[e])
      break;

  //找上一个输入域
  for(e--; e >=0; e--)
  {
    prev = form.elements[e];
    if(prev.readOnly || prev.disabled)
      continue;
    tagname = prev.tagName.toUpperCase();
    if((tagname=="INPUT" && prev.type != "hidden")|| tagname=="SELECT" || tagname=="TEXTAREA")
      break;
  }
  if(e == form.elements.length)
    return null;
  else
    return prev;
}
//得到下一个输入框
function nextElement(field) {
  var form = field.form;
  var e=0;
  var next;
  for(; e <form.elements.length; e++)
    if(field == form.elements[e])
      break;
  //找下一个输入域
  for(e++; e <form.elements.length; e++)
  {
    next = form.elements[e];
    if(next.readOnly || next.disabled)
      continue;
    tagname = next.tagName.toUpperCase();
    if((tagname=="INPUT" && next.type != "hidden")|| tagname=="SELECT" || tagname=="TEXTAREA")
      break;
  }
  //if end, select first
  if(e == form.elements.length)
  {
    //找到第一个输入域
    for(e = 0; e <form.elements.length; e++)
    {
      next = form.elements[e];
      if(next.readOnly || next.disabled)
        continue;
      tagname = next.tagName.toUpperCase();
      if((tagname=="INPUT" && next.type != "hidden") || tagname=="SELECT"
         || tagname=="A" || tagname=="TEXTAREA")
      break;
    }
    if(e == form.elements.length)
      return null;
    else
      return next;
  }
  else
    return next;
}
//打开URL
function openurl(winName,url, top, left, iWidth,iHeight,isCenter,isResizable,isScrollbars){
  if(top+'' == 'undefined')
    top = 0;
  if(left+'' == 'undefined')
    left = 0;
  if(isCenter+'' == 'undefined')
    isCenter = false;
  if(isResizable+'' == 'undefined')
    isResizable = false;
  if(isScrollbars+'' == 'undefined')
    isScrollbars = false;

  if(iWidth+'' == 'undefined' || iWidth <= 0)
    iWidth = screen.width-10;
  else if(iWidth > screen.width-10)
    iWidth = screen.width-10;
  else if(isCenter)
    left = (screen.width-10 - iWidth)/2;

  if(iHeight+'' == 'undefined' || iHeight <= 0)
    iHeight = screen.height-56;
  else if(iHeight > screen.height-140)
    iHeight = screen.height-56;
  else if(isCenter)
    top = (screen.height-0 - iHeight)/2;

  if(winName +'' == 'undefined')
    winName = "";
  var winretu = window.open(url,winName,"left="+left+",top="+top+",width="+ iWidth +",height="+ iHeight
              + "location=no,menubar=no,toolbar=no,status=no,scrollbars="+(isScrollbars ? "yes" : "no")
              + ",resizable="+ (isResizable ? "yes" : "no"));//790,height=455
  return winretu;
}
var winopt1 = 1;//"location=no scrollbars=yes menubar=no status=no resizable=1 width=640 height=450 top=0 left=0";
var winopt2 = 2;//"location=no scrollbars=yes menubar=no status=no resizable=1 width=790 height=560 top=0 left=0";
function openUrlOpt1(paraStr){ openSelectUrl(paraStr, '', winopt1); }
function openUrlOpt2(paraStr){ openSelectUrl(paraStr, '', winopt2); }
function openSelectUrl(paraStr, winName, winOpt)
{
  if(winOpt +'' == 'undefined')
    winOpt = 2;
  if(winOpt == 2)
    newWin = openurl(winName, paraStr, 90, 0, null, screen.height-145, false, true, true);//window.open(paraStr, winName, winOpt);
  else
    newWin = openurl(winName, paraStr, null, null, 640, 450, true, true, true);//window.open(paraStr, winName, winOpt);
  newWin.focus();
}
//弹出一个模态对话框
function showModal(url,iWidth,iHeight,noresizable){
  var Width;
  var Height;
  if(iWidth > 0)    Width = iWidth
  else    Width = screen.width-10;

  if(iHeight > 0)    Height = iHeight
  else    Height = screen.height-130;

  var winretu;
  if(window.showModalDialog)
    winretu = window.showModalDialog(url," ","dialogWidth:"+ Width +"px;dialogHeight:"+ Height +"px;center:yes;help:no;resizable:" +(noresizable ? "yes" : "no")+ ";status:no");
  else
    winretu = openurl(url,Width,Height,noresizable);
  return winretu;
}
//显示信息
function showMessage(msg, isError, afterUrl)
{
  var notUrl = (afterUrl+'' == 'undefined');
  if(isError+'' == 'undefined')
    isError = false;
  var doc = document;
  var div = doc.all['divMessageBox'];
  if(div+''=="undefined")
  {
    div = doc.createElement("DIV");
    div.setAttribute("id","divMessageBox");
    div.className = "queryPop";
    //div.style.z-index = "300";
    doc.body.appendChild(div);
  }
  var hidesrcipt = "document.all['divMessageBox'].style.display='none'";
  var html ="<div class='queryTitleBox' align='right'><img src='../images/closewin.gif' onClick=\""
       + (notUrl ? hidesrcipt : "location.href='"+afterUrl+"'")
       +"\" border=0 style='cursor:hand'></div>"
       +"<table align='center' cellspacing='1' cellpadding='0'><tr><td><fieldset><legend align='center'"
       +(isError ?" style='color: red'>错误": ">信息")+"</legend><div align='left'"
       +(isError ?" style='color: red'>": ">") + msg
       +"</div></fieldset></td></tr><tr><td align='center'><input type='button' value=' 确定 ' class=button onClick=\""
       + (notUrl ? hidesrcipt : "location.href='"+afterUrl+"'")
       +"\"></td></tr></table>";
  //
  div.innerHTML = html;
  showFrame('divMessageBox', true, '', true);
}
/*导航栏点击菜单*/
function lockScreenForOpen(msg, documentObj)
{
  var doc = documentObj+''=='undefined' ? document: documentObj;
  //doc.body.onfocus = return false;
  doc.body.oncontextmenu = cancelClick;
  var div = doc.all["divWaitingForOpen"];
  if (div+''=="undefined"){
    div = doc.createElement("DIV");
    div.setAttribute("id","divWaitingForOpen");
    div.className = "waitBox";
    div.style.padding = 15;
    div.style.paddingLeft = 20;
    div.style.paddingRight = 20;
    div.innerHTML = msg;
    doc.body.appendChild(div);
  }
  var x =(doc.body.clientWidth-div.offsetWidth)/2;
  var y = (doc.body.clientHeight-div.offsetHeight)/2;
  div.style.pixelLeft = x;
  div.style.pixelTop = y;
  div.style.visibility = "visible";
  div.onclick = cancelClick;
  //div.setCapture();
}

scrWaitBodyFocus=null;
scrWaitBodyContext=null;
function lockScreenToWait(msg)
{
  var doc = document;
  if (scrWaitBodyFocus==null && scrWaitBodyContext==null){
    scrWaitBodyFocus = doc.body.onfocus+'';
    scrWaitBodyContext = doc.body.oncontextmenu+'';
    doc.body.onfocus = lockScreenToWait;
    doc.body.oncontextmenu = cancelClick;
  }
  var div = doc.all["divWaitingStatus"];
  if (div+''=="undefined"){
    div = doc.createElement("DIV");
    div.setAttribute("id","divWaitingStatus");
    div.className = "waitBox";
    div.style.padding = 10;
    div.style.paddingLeft = 15;
    div.style.paddingRight = 15;
    div.innerHTML = msg;
    doc.body.appendChild(div);
  }
  var x =(doc.body.clientWidth-div.offsetWidth)/2;
  var y = (doc.body.clientHeight-div.offsetHeight)/2;
  div.style.pixelLeft = x;
  div.style.pixelTop = y;
  div.style.visibility = "visible";
  div.onclick = cancelClick;
//  div.setCapture();
}

function unlockScreenWait()
{
  var div = document.all["divWaitingStatus"];
  if (div+''=="undefined")
    return;
  div.style.visibility = "hidden";
  document.body.onfocus = scrWaitBodyFocus;
  document.body.oncontextmenu = scrWaitBodyContext;
  scrWaitBodyFocus = null;
  scrWaitBodyContext = null;
  document.releaseCapture();
}

function cancelClick()
{
  var elem = event.srcElement;
  if (elem.className=='menuBar'){
    UnlockScreenWait();
    event.cancelBubble = false;
    return false;
  }
  return false;
}
//---------------------------------------------------------------------------
function addSelectOption(objname, caption, value)
{
  var obj = document.all[objname];
  obj.options[obj.length] = new Option(caption,value);
}
//得到checkbox或radiobutton的对象
function gCheckedObj(form, isCheckBox)
{
  var checkType = isCheckBox ? 'checkbox' : 'radio';
  for(var i=0;i<form.elements.length;i++)
  {
    var e = form.elements[i];
    if(e.type == checkType && e.checked == true)
      return e;
  }
  return null;
}
//勾选表单下的所有checkbox
function checkAll(frmObj, curCheckObj)
{
  for(var i=0; i<frmObj.elements.length; i++)
  {
    var e = frmObj.elements[i];
    if(e!=curCheckObj && e.type == 'checkbox')
      e.checked = curCheckObj.checked;
  }
}
//显示隐藏的行信息
function ShowHideInfo(Layer, ImageNode)
{
  var ishide = Layer.style.display=='none';
  Layer.style.display= ishide  ?'block' : 'none';
  ImageNode.src= ishide ?'../images/closedetail.gif' :'../images/opendetail.gif';
}

function formatNumber(srcStr, formatStr){
  //var srcStr,
  formatStr = ""+formatStr+"";
  formatLen = formatStr.length;
  formatPos = formatStr.indexOf(".",0);
  nAfterDot = formatPos == -1 ? 0 : formatLen - formatPos - 1;
  isFill = formatStr.substring(formatLen-1, formatLen)=="0";//是否补0

  var resultStr, nTen;
  srcStr = ""+srcStr+"";
  strLen = srcStr.length;
  dotPos = srcStr.indexOf(".",0);
  if (dotPos == -1){
    if(nAfterDot >0 && isFill){
      resultStr = srcStr + ".";
      for (i=0; i<nAfterDot; i++)
        resultStr = resultStr+"0";
    }
    else
      resultStr = srcStr;
    return resultStr;
  }
  else
  {
    if ((strLen - dotPos - 1) >= nAfterDot){
      nAfter = dotPos + nAfterDot + 1;
      nTen =1;
      for(j=0;j<nAfterDot;j++){
        nTen = nTen*10;
      }
      resultStr = Math.round(parseFloat(srcStr)*nTen)/nTen;
      //if(isFill)
        //return formatNumber(resultStr, formatStr);
      return resultStr;
    }
    else{
      resultStr = srcStr;
      for (i=0; isFill && i<(nAfterDot - strLen + dotPos + 1); i++){
        resultStr = resultStr+"0";
      }
      return resultStr;
    }
  }
}
//--------------------------------------------------table row control
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
//同步父div
function syncParentDiv(tableName)
{
  var tbl = document.all[tableName];
  var parentDiv = tbl.parentElement;
  if(parentDiv.tagName.toUpperCase() == 'DIV')
  {
    maxheight = document.body.clientHeight;
    var curheight = tbl.offsetHeight+120;
    var curtop = tbl.offsetTop;
    //依次读取父容器在更高一级父容器中的相对位置
    while(tbl=tbl.offsetParent){
      curtop+=tbl.offsetTop;
    }
    parentDiv.style.height = curtop+curheight < maxheight ? curheight-100 : maxheight-curtop-100;
  }
}
function highlightRow()
{
  var obj = event.srcElement;
  var objTagName;
  if (obj.tagName.toUpperCase()=='TABLE')
    return;
  while ((objTagName = obj.tagName.toUpperCase())!='TR')
  {
    if (objTagName=='BODY')
      return;
    obj = obj.parentElement;
  }
  if(obj.style.backgroundColor != rowColorSelected)
    obj.style.backgroundColor = rowColorHighlight;
}

function normalRow()
{
  var obj = event.srcElement;
  var objTagName;
  if (obj.tagName.toUpperCase()=='TABLE')
    return;
  while ((objTagName = obj.tagName.toUpperCase())!='TR')
  {
    if (objTagName=='BODY')
      return;
    obj = obj.parentElement;
  }
  var idx = obj.rowIndex;
  if(obj.style.backgroundColor != rowColorSelected)
  {
    if ((idx+rowStartIdx)%2==0)
      obj.style.backgroundColor = rowColorLight;
    else
      obj.style.backgroundColor = rowColorDark;
  }
}
//选中行，并改变改行颜色
function selectRow() {
  var obj = event.srcElement;
  var tbl;
  var objTagName;

  while ((objTagName = obj.tagName.toUpperCase())!='TR')
  {
    if (objTagName=='BODY')
      return;
    obj = obj.parentElement;
  }
  tbl = obj;
  while ((objTagName = tbl.tagName.toUpperCase())!='TABLE')
  {
    if (objTagName=='BODY')
      return;
    tbl = tbl.parentElement;
  }

  for (i=rowStartIdx; i<tbl.rows.length; i++)
  {
    row = tbl.rows[i];
    isLight = (i+rowStartIdx)%2==0;
    row.style.backgroundColor = isLight ? rowColorLight : rowColorDark;
  }
  obj.style.backgroundColor = rowColorSelected;
}

/**
 * 变换改行的颜色
 * @param isChecked 是:选中,否:不选择并还原颜色
 */
function checkRow(isChecked)
{
  if(isChecked+'' == 'undefined')
    isChecked = false;
  var obj = event.srcElement;
  var tbl;
  var objTagName;
  while ((objTagName = obj.tagName.toUpperCase())!='TR')
  {
    if (objTagName=='BODY')
      return;
    obj = obj.parentElement;
  }
  tbl = obj.parentElement;

  if(isChecked)
  {
    obj.style.backgroundColor = rowColorSelected;
    return;
  }
  //
  for (i=rowStartIdx; i<tbl.rows.length; i++)
  {
    if(tbl.rows[i] != obj)
      continue;

    row = tbl.rows[i];
    isLight = (i+rowStartIdx)%2==0;
    row.style.backgroundColor = isLight ? rowColorLight : rowColorDark;
  }
}

function showSelected(trName)
{
  var obj = document.all[trName];
  if(obj != null && obj.tagName.toUpperCase()=='TR')
    obj.style.backgroundColor = rowColorSelected;
}
//--------------------------------------------------float control
var NS = (document.layers) ? 1 : 0;
var IE = (document.all) ? 1: 0;

function showFrame(frameName, isCenter, url, isDiv)
{
  if(!isDiv){
    lockScreenToWait("处理中, 请稍候！");
    setTimeout("unlockScreenWait();", 600);
  }
  var frameObj = document.all[frameName];
  frameObj.style.display="block";
  if(isCenter)
  {
    frameObj.style.posLeft=(document.body.clientWidth-frameObj.offsetWidth)/2;
    frameObj.style.posTop=(document.body.clientHeight-frameObj.offsetHeight)/2-15;
  }
  else
  {
    frameObj.style.posLeft=document.body.scrollLeft+window.event.clientX;
    frameObj.style.posTop=document.body.scrollTop+window.event.clientY;
  }
  if(frameObj.style.posLeft+frameObj.offsetWidth > document.body.scrollLeft+document.body.clientWidth)
    frameObj.style.posLeft=document.body.scrollLeft+document.body.clientWidth-frameObj.offsetWidth;
  if(frameObj.style.posLeft < 0) frameObj.style.posLeft=0;
  if(frameObj.style.posTop+frameObj.offsetHeight > document.body.scrollTop+document.body.clientHeight)
    frameObj.style.posTop=document.body.scrollTop+document.body.clientHeight-frameObj.offsetHeight;
  if(frameObj.style.posTop < 0)
    frameObj.style.posTop=0;

  if(!isDiv) {
    frameObj.src = url;
  }
}

function hideFrame(frameName)
{
  var frameObj = document.all[frameName];
  frameObj.style.display="none";
  frameObj.src="";
}

//<!-- DRAG DROP CODE -->
var dragapproved = false;
var currentX, currentY, whichIt;
//self.onError=null;
function dragIt(e) {
  if(IE) {
    //fixedQueryform.htrq$a.value += event.srcElement.className;
    if(event.srcElement.className !="queryTitleBox")
      return;
    whichIt = event.srcElement.parentElement;
    if(whichIt == null)
      return;
    dragapproved = true;
    whichIt.style.pixelLeft = whichIt.offsetLeft;
    whichIt.style.pixelTop = whichIt.offsetTop;
    currentX = (event.clientX + document.body.scrollLeft);
    currentY = (event.clientY + document.body.scrollTop);
    document.onmousemove = moveIt;
  }
  else {
    window.captureEvents(Event.MOUSEMOVE);
    if(checkFocus (e.pageX,e.pageY)) {
      whichIt = document.floater;
      StalkerTouchedX = e.pageX-document.floater.pageX;
      StalkerTouchedY = e.pageY-document.floater.pageY;
      window.onmousemove = moveIt;
    }
  }
  return true;
}

function moveIt(e) {
  if (whichIt == null)
    return false;

  if(IE) {
    if(!(event.button==1 && dragapproved))
      return false;
    //z.style.pixelLeft=temp1+event.clientX-x;
    //z.style.pixelTop=temp2+event.clientY-y;
    newX = (event.clientX + document.body.scrollLeft);
    newY = (event.clientY + document.body.scrollTop);
    distanceX = (newX - currentX);
    distanceY = (newY - currentY);
    currentX = newX;
    currentY = newY;
    whichIt.style.pixelLeft += distanceX;
    whichIt.style.pixelTop += distanceY;
    if(whichIt.style.pixelTop < document.body.scrollTop)
      whichIt.style.pixelTop = document.body.scrollTop;
    if(whichIt.style.pixelLeft < document.body.scrollLeft)
      whichIt.style.pixelLeft = document.body.scrollLeft;
    if(whichIt.style.pixelLeft > document.body.offsetWidth - document.body.scrollLeft - whichIt.style.pixelWidth - 20)
      whichIt.style.pixelLeft = document.body.offsetWidth - whichIt.style.pixelWidth - 20;
    if(whichIt.style.pixelTop > document.body.offsetHeight + document.body.scrollTop - whichIt.style.pixelHeight - 5)
      whichIt.style.pixelTop = document.body.offsetHeight + document.body.scrollTop - whichIt.style.pixelHeight - 5;
    //event.returnValue = false;
  }
  else
  {
    whichIt.moveTo(e.pageX-StalkerTouchedX,e.pageY-StalkerTouchedY);
    if(whichIt.left < 0+self.pageXOffset)
      whichIt.left = 0+self.pageXOffset;
    if(whichIt.top < 0+self.pageYOffset)
      whichIt.top = 0+self.pageYOffset;
    if((whichIt.left + whichIt.clip.width) >= (window.innerWidth+self.pageXOffset-17))
      whichIt.left = ((window.innerWidth+self.pageXOffset)-whichIt.clip.width)-17;
    if((whichIt.top + whichIt.clip.height) >= (window.innerHeight+self.pageYOffset-17))
      whichIt.top = ((window.innerHeight+self.pageYOffset)-whichIt.clip.height)-17;
  }
  return false;
}

function dropIt() {
  whichIt = null;
  dragapproved=false;
  if(NS)
    window.releaseEvents (Event.MOUSEMOVE);
  return true;
}
//NS 's checkFocus
function checkFocus(x,y) {
  stalkerx = document.floater.pageX;
  stalkery = document.floater.pageY;
  stalkerwidth = document.floater.clip.width;
  stalkerheight = document.floater.clip.height;
  return (x > stalkerx && x < (stalkerx+stalkerwidth)) && (y > stalkery && y < (stalkery+stalkerheight));
}
//<!-- DRAG DROP CODE -->
if(NS) {
  window.captureEvents(Event.MOUSEUP|Event.MOUSEDOWN);
  window.onmousedown = dragIt;
  window.onmouseup = dropIt;
}
if(IE) {
  document.onmousedown = dragIt;
  document.onmouseup = dropIt;
}
//--------------------------------------------------shortcut control
//--快捷键控制对象--
//保存对象的数组
var shortcutObjs = false;
/**
 * @param keyCode 快捷键值
 * @param eventScript 触发是需要执行的script语句
 */
function TShortcutControl(keyCode, eventScript)
{
  this.keyCode = keyCode;
  this.eventScript = eventScript;
  this.notify = TSCCT_Notify;
  return this;
}
/**
 * 得到一个快捷键控制对象，若不存在则创建一个。
 */
function GetShortcutControl(keyCode, eventScript)
{
  var obj = FindShortcutControl(keyCode);
  if(obj == null)
  {
    obj = new TShortcutControl(keyCode, eventScript);
    RegisterShortcutControl(obj);
  }
  return obj;
}
/**
 * 注册控制输入框的对象
 * @param shortcutControl 快捷键的对象
 */
function RegisterShortcutControl(shortcutControl)
{
  if (!shortcutObjs)
    shortcutObjs = new Array();
  shortcutObjs[shortcutObjs.length] = shortcutControl;
}

function FindShortcutControl(keyCode)
{
  var i;
  for (i=0;i<shortcutObjs.length;i++)
    if (shortcutObjs[i].keyCode == keyCode)
      return shortcutObjs[i];
  return null;
}
/**
 * 触发快捷键
 */
function TSCCT_Notify()
{
  setTimeout(this.eventScript, 300);
}
//--------------------------------------------------select control
selectObjs = false;
curSelectObj = null;
window.onresize=TryAdjustSelect;
function RegisterSelect(selobj)
{
  if (!selectObjs)
    selectObjs = new Array();
  selectObjs[selectObjs.length] = selobj;
}

function FindSelectObject(name)
{
  var i;
  for (i=0;i<selectObjs.length;i++)
    if (selectObjs[i].name==name)
      return selectObjs[i];
  //alert('not found '+name);
  return null;
}

function TryAdjustSelect()
{
  if (selectObjs){
    for (i=0;i<selectObjs.length;i++)
      if (selectObjs[i].visible){
        CalcSelectPosition(selectObjs[i]);
      }
  }
}

/**
 * @param name 对象名称
 * @param isLookup 是:可查询的下拉框,否:仅仅只有一个值的combox
 * @param formVarH lookup:表示值对应的标题(caption),combox：就是值
 * @param formVarV lookup:表示值,combox：没有
 * @param idOn 显示列表框的DIV的ID
 * @param idOff 整个最外层的DIV的ID
 * @param enter2tab 是否将tab转化为回车符
 * @param onselectFunc 选择时执行的script语句
 */
function TSelectObject(name,formVarH,formVarV,idOn,idOff,enter2tab,onselectFunc, isLookup)
{
  this.name = name;
  this.formVarH = formVarH;//isLookup ? caption : value
  this.formVarV = formVarV;//isLookup ? value : caption
  this.idOn = idOn;
  this.idOff = idOff;//selectItemDiv
  this.selectedIndex = null;//selectedIndex;
  this.index0 = null;//selectedIndex;
  this.index1 = null;//selectedIndex;
  this.visible = false;
  this.nItems = 0;
  this.onselectFunc = onselectFunc;
  this.isEnter2tab = enter2tab;
  this.isLookup = isLookup;
  this.length = 5;//最大字符的长度，默认5

  this.oldInputValue = "";
  this.docKeyDown = "";
  this.docClick = "";
  this.scrollHandles = new Array();

  this.itemKey = new Array();
  this.itemValue = new Array();
  this.AddItem = TSLCT_AddItem;
  this.RemoveAll = TSLCT_RemoveAll;
  this.GetXMLData = TSLCT_GetXMLData;
  this.InitSelectedIndex= TSLCT_InitSelectedIndex;
  this.InitSelectedValue= TSLCT_InitSelectedValue;
  this.Refresh = TSLCT_Refresh;
  this.Focus = TSLCT_Focus;
  this.OnSelect= TSLCT_OnSelect;
  this.SetSelectedKey = TSLCT_SetSelectedKey;//设置控件显示的值
  this.SetEnterAsTab = TSLCT_SetEnterAsTab;
  this.SetEvents = TSLCT_SetEvents;
  this.RestoreEvents = TSLCT_RestoreEvents;
  this.GetSelectedKey = TSLCT_GetSelectedKey;
  this.GetSelectedValue = TSLCT_GetSelectedValue;
  return this;
}

function TSLCT_SetEnterAsTab()
{
  var formObj=null;
  var inputObj;

  formObj = FindFormByVarName(this.formVarH);
  if (formObj!=null){
    eval("inputObj = formObj."+this.formVarV+";");
    inputObj.onkeydown = KeyDownEnter2Tab;
    //alert(1);
  }
}

function TSLCT_AddItem(k,v)
{
  this.itemKey[this.nItems] = k;
  this.itemValue[this.nItems] = v;
  this.nItems++;
  curLength = strlen(v);
  if(curLength > this.length)
    this.length = curLength;

  if(this.isLookup && this.selectedIndex == null)
  {
    this.selectedIndex = 0;
    this.index0 = 0;
    this.index1 = 0;
    var formObj = FindFormByVarName(this.formVarH);
    if(formObj!=null){
      eval("formObj."+this.formVarV+".value ='"+ this.itemValue[this.selectedIndex] +"';");
      eval("formObj."+this.formVarH+".value ='"+ this.itemKey[this.selectedIndex] +"';")
    }
  }
}

function TSLCT_RemoveAll()
{
  this.nItems = 0;
  //2004.3.29 add。清除后将选择的index清楚
  this.selectedIndex = null;//selectedIndex;
  this.index0 = null;//selectedIndex;
  this.index1 = null;//selectedIndex;
  //
  this.itemKey = new Array();
  this.itemValue = new Array();
}

function TSLCT_GetXMLData(id)
{
  this.RemoveAll();
  var xmlObj = document.all[id];
  var node = xmlObj.firstChild.firstChild;
  while (node!=null){
    this.AddItem(node.getAttribute("KEY"),node.getAttribute("VALUE"));
    node = node.nextSibling;
  }
}

function TSLCT_InitSelectedIndex(i)
{
  this.selectedIndex = i;
  this.index0 = i;
  this.index1 = i;
}

function TSLCT_InitSelectedValue(val)
{
  var i;

  for (i=0;i<this.itemKey.length;i++){
    if (this.itemKey[i]==val){
      this.InitSelectedIndex(i);
      return;
    }
  }
}

function TSLCT_Refresh()
{
  var formObj=null;

  formObj = FindFormByVarName(this.formVarH);
  if (formObj!=null){
    eval("formObj."+this.formVarV+".value = this.itemValue[this.selectedIndex];");
    eval("formObj."+this.formVarH+".value = this.itemKey[this.selectedIndex];");
  }
}

function TSLCT_GetSelectedKey(){
  return this.itemKey[this.selectedIndex];
}

function TSLCT_GetSelectedValue(){
  return this.itemValue[this.selectedIndex];
}

function TSLCT_Focus()
{
  var formObj=null;

  formObj = FindFormByVarName(this.formVarH);
  if (formObj!=null){
    eval("formObj."+this.formVarV+".focus();");
    eval("formObj."+this.formVarV+".select();");
  }
}
//--
function TSLCT_OnSelect()
{
  if (this.onselectFunc!='' && this.onselectFunc!=null){
    eval(this.onselectFunc);
    //selobj.onselect();
  }
}
//2004.2.27增加
function TSLCT_SetSelectedKey(key)
{
  index = -1;
  for (i=0; i<this.nItems; i++){
    if(key == (this.isLookup? this.itemKey[i] : this.itemValue[i]))
    {
      this.selectedIndex = i;
      this.index0 = i;
      this.index1 = i;
      index = i;
      break;
    }
  }

  var formObj = FindFormByVarName(this.formVarH);
  if(index > -1 && formObj!=null){
    eval("formObj."+this.formVarV+".value ='"+ this.itemValue[index] +"';");
    eval("formObj."+this.formVarH+".value ='"+ this.itemKey[index] +"';")
  }
}

function TSLCT_SetEvents()
{
  this.docClick = document.onclick;
  //this.docKeyDown = document.onkeydown;
  //document.onkeydown = SelectKeyDown;
  document.onclick = SelectClickDocument;
  var obj = document.all[this.idOff];
  for (obj=obj.offsetParent; obj!=document.body; obj=obj.offsetParent){
    this.scrollHandles[this.scrollHandles.length] =obj.onscroll;
    obj.onscroll = TryHideSelect;
  }
}

function TSLCT_RestoreEvents()
{
  document.onclick = this.docClick;
  //document.onkeydown = this.docKeyDown;
  var obj = document.all[this.idOff];
  var i;
  for (obj=obj.offsetParent,i=0; obj!=document.body; obj=obj.offsetParent,i++){
    obj.onscroll = this.scrollHandles[i];
  }
  this.scrollHandles = new Array();
}

function ToggleSelect(name)
{
  if (IsDisabledVar(name))
    return;
  var selobj = FindSelectObject(name);
  if (selobj=="")
    return;
  if (selobj.visible)
    HideSelect(selobj);
  else{
    if (document.onclick!=null) {
        document.onclick();
    }
    ShowSelect(selobj);
  }
  event.cancelBubble = true;
  return false;
}

function HighlightOption(name,i)
{
  if(i == null)
    return;
  var selobj = FindSelectObject(name);
  if (selobj=="")
    return;
  NormalOption(name,selobj.index1);
  var obj = document.all[selobj.idOn+"_"+i];
  obj.style.backgroundColor = "#113399";
  obj.style.color = "#ffffff";
  var pobj = obj.parentElement;
  if (obj.offsetTop+obj.offsetHeight>pobj.offsetHeight+pobj.scrollTop){
    pobj.scrollTop = obj.offsetTop+obj.offsetHeight-pobj.offsetHeight;
  }
  if (obj.offsetTop<pobj.scrollTop){
    pobj.scrollTop = obj.offsetTop;
  }
  selobj.index1 = i;
}

function NormalOption(name,i)
{
  if(i == null)
    return;
  var selobj = FindSelectObject(name);
  if (selobj=="")
    return;
  var obj = document.all[selobj.idOn+"_"+i];
  obj.style.backgroundColor = "#f0f0f0";
  obj.style.color = "#000";
}
//select相应keydown事件
function SelectKeyDown(name)
{
  if (IsDisabledVar(name))//是否的不能用的
    return true;
  var selobj = FindSelectObject(name);
  if (selobj=="")
    return true;
  var keyCode = window.event.keyCode;
  if(keyCode==9)//tab
  {
    if(selobj.visible)
      HideSelect(selobj);
    return true;
  }
  else if(!selobj.visible)
  {
    if(keyCode==13)//回车
    {
      if (selobj.isEnter2tab){
        getNextElement();
        return false;
      }
    }
    ShowSelect(selobj);
  }

  if (!(keyCode==38 || keyCode==40 || keyCode==13)){
    return true;
  }
  if (keyCode==38 && selobj.index1>0){
    NormalOption(selobj.name, selobj.index1);
    HighlightOption(selobj.name, selobj.index1-1);
  }
  if (keyCode==40 && selobj.index1<selobj.nItems-1){
    NormalOption(selobj.name,selobj.index1);
    HighlightOption(selobj.name, selobj.index1+1);
  }
  if (keyCode==13){// && selobj.isLookup
    OptionClicked(selobj.name, selobj.index1, selobj.itemKey[selobj.index1]);
  }
  return false;
}
//select相应keyup事件
function SelectKeyUp(value)
{
  var selobj = curSelectObj;
  if (selobj==null)
    return;
  var keyCode = window.event.keyCode;
  if (keyCode==38 || keyCode==40 || keyCode==13 || keyCode==9)
    return true;

  if(selobj.oldInputValue == value)
    return;
  else
    selobj.oldInputValue = value;

  valueLen = value.length;
  for (i=0; i<selobj.nItems; i++){
    v = selobj.itemValue[i];
    if(valueLen > v.length)
      continue;
    if(v.substring(0, valueLen) == value)
    {
      HighlightOption(selobj.name, i);
      break;
    }
  }
}
//select相应onchange事件
function SelectChange(name)
{
  var selobj = FindSelectObject(name);
  if (selobj=="")
    return;
  //if (selobj.visible)
    //HideSelect(selobj);
  formObj = FindFormByVarName(selobj.formVarH);
  if (formObj!=null && selobj.isLookup){
    eval("formObj."+selobj.formVarV+".value = selobj.itemValue[selobj.selectedIndex];");
  }
}

function SelectClickDocument()
{
  if (curSelectObj==null)
    return;
  var selobj = curSelectObj;
  var divOn = document.all[selobj.idOn];
  if (event.srcElement!=divOn && event.srcElement.parentElement!=divOn){
    HideSelect(selobj);
  }
  event.cancelBubble = true;
  return false;
}

function OptionClicked(name,idx,val)
{
  var selobj = FindSelectObject(name);
  if (selobj=="")
    return;
  var obj = document.all[selobj.idOn+"_"+idx];
  if(obj == null)
    return;
  var vstr = obj.innerText;
  vstr = vstr.replace(/^( )+/,"");
  var formObj=null;

  formObj = FindFormByVarName(selobj.formVarH);
  //SetFormChanged(formObj);
  if (formObj!=null){
    eval("formObj."+selobj.formVarV+".value = vstr;");

    eval("formObj."+selobj.formVarH+".value = '"+val+"';");
  }
  selobj.selectedIndex = idx;
  HideSelect(selobj);
  selobj.OnSelect();
}

function CalcSelectPosition(selobj)
{
  if (document.all){
    var obj = document.all[selobj.idOff];
    var left = 0;
    var top = 0;
    for (; obj!=document.body; obj=obj.offsetParent){
      left += obj.offsetLeft-obj.scrollLeft;
      top  += obj.offsetTop-obj.scrollTop;
    }
    obj = document.all[selobj.idOn].style;
    obj.pixelLeft = left;
    var parentTop = top;

    obj.pixelTop = top+document.all[selobj.idOff].offsetHeight+1;
    obj.pixelWidth = document.all[selobj.idOff].offsetWidth;
    if (selobj.nItems<=10){
      obj.pixelHeight = selobj.nItems*16+2;
      obj.overflow = "visible";
    }
    else{
      obj.pixelHeight = 10*16+2;
      obj.overflow = "auto";
    }
    if (obj.pixelTop+obj.pixelHeight>document.body.clientHeight+document.body.scrollTop)
      obj.pixelTop = parentTop-obj.pixelHeight;
  }
}

function _GetSelectOnDiv(name)
{
  var obj;
  if (document.all[name]+''=='undefined'){
    obj = document.createElement("DIV");
    document.body.appendChild(obj);
    obj.setAttribute("id",name);
    obj.className = "selectOn";
    obj.style.visibility = "hidden";
    return obj;
  }
  else{
    return document.all[name];
  }
}

function _RebuildSelectOnDiv(selobj,divOn)
{
  var str = "";
  var i,num,v;
  var name = selobj.name;
  for (i=0;i<selobj.nItems;i++){
    k = selobj.itemKey[i];
    v = selobj.itemValue[i];
    v = EscapeHTMLString(v);
    str += "<DIV CLASS=\"selectItem\" ID=\""+selobj.idOn+"_"+i+"\" ONCLICK=\"OptionClicked('"+name+"',"+i+",'"+k+"');\" ONMOUSEOVER=\"HighlightOption('"+name+"',"+i+");\">"+v+"</DIV>";
  }
  divOn.innerHTML = str;
}

function ShowSelect(selobj)
{
  if (curSelectObj!=null)
    HideSelect(curSelectObj);
  var obj = _GetSelectOnDiv(selobj.idOn);
  _RebuildSelectOnDiv(selobj,obj);
  selobj.visible = true;
  obj.style.visibility="visible";
  CalcSelectPosition(selobj);
  if(selobj.selectedIndex != null)
    HighlightOption(selobj.name,selobj.selectedIndex);
  curSelectObj = selobj;
  selobj.SetEvents();
}

function HideSelect(selobj)
{
  var obj = _GetSelectOnDiv(selobj.idOn);
  obj.style.visibility="hidden";
  selobj.visible = false;
  curSelectObj = null;
  selobj.RestoreEvents();
}

function TryHideSelect()
{
  if (curSelectObj!=null)
    HideSelect(curSelectObj);
}
//name:
function GetSelectHTML(name, isLookup, className, style, readonly, initKeyValue, valueName, imagePath)
{
  if(className+''=='undefined')
    className='edbox';
  if(valueName+''=='undefined')
    valueName = "v_"+name;
  if(imagePath+''=='undefined')
    imagePath = "../images/down_arrow.gif";
  if(isLookup+''=='undefined')
    isLookup = false;
  readonly = readonly ? "readonly" : "";
  //var bigDivID = "d2_"+ name;//最外的DIV ID
  //var itemDivdID = "d1_"+name;//选项的DIV ID
  var str = "<DIV ID='d2_"+name+"' class="+className;
  if(style+''=='undefined')
    str += " style='"+style+"'";
  //如果是combox,就将两个控件的名字调换一下。以便表单提交是得到输入的值
  str += " onClick=ToggleSelect('"+name+"')><table border=0 cellspacing=0 cellpadding=0 width='100%'>"
      +  "<tr><td width='100%' nowrap><INPUT CLASS='ednone' NAME='"+ (isLookup ? valueName : name)
      +  "' style='width:100%' onKeyDown=\\\"return SelectKeyDown('"+name+"');\\\""
      +  " onKeyUp='SelectKeyUp(this.value);' onChange=\\\"SelectChange('"+name+"');\\\" "
      +  readonly+" value='";
  if(!isLookup && initKeyValue+''!='undefined')//这时不是主键值，而是值
    str += initKeyValue;
  str += "'></td><td nowrap><IMG SRC='"+imagePath+"' style='cursor:hand;' border=0></td></tr></table>";
  //如果是combox,就将两个控件的名字调换一下。以便表单提交是得到输入的值
  str += "<INPUT TYPE=HIDDEN NAME='"+(isLookup ? name : valueName)+"' value=''></DIV>";
  return str;
}

function GetSelectObject(name, isLookup, enter2tab, onselect)
{
  var selobj = FindSelectObject(name);
  if(selobj == null)
  {
    selobj = new TSelectObject(name, name, 'v_'+name,'d1_'+name, 'd2_'+name, enter2tab, onselect, isLookup);
    RegisterSelect(selobj);
    SetDestSelectObject(selobj);
  }
  return selobj;
}

_dest_select_obj = false;
function SetDestSelectObject(obj)
{
  _dest_select_obj = obj;
}

function AddSelectItem(k,v)
{
  if (!_dest_select_obj)
    return;
  //	v = EscapeHTMLString(v);
  _dest_select_obj.AddItem(k,v);
}
//设置控件显示的值
function SetSelectedIndex(key)
{
  if (!_dest_select_obj)
    return;
  selobj = _dest_select_obj;
  //init combox's value is not null
  if(selobj.isLookup)
    selobj.SetSelectedKey(key);
  /*index = -1;//2004.2.27 modify
  for (i=0; selobj.isLookup && i<selobj.nItems; i++){
    if(key == selobj.itemKey[i])
    {
      selobj.selectedIndex = i;
      selobj.index0 = i;
      selobj.index1 = i;
      index = i;
      break;
    }
  }
  var formObj = FindFormByVarName(selobj.formVarH);
  if(index > -1 && formObj!=null){
    eval("formObj."+selobj.formVarV+".value ='"+ selobj.itemValue[index] +"';");
    eval("formObj."+selobj.formVarH+".value ='"+ selobj.itemKey[index] +"';")
  }*/
}

//---------------------------------------------------------------common
/* 取得字符串的字节长度 */
function strlen(str)
{
  var i;
  var len;
  len = 0;
  for (i=0; i<str.length; i++)
    len += str.charCodeAt(i)>255 ? 2 : 1;
  return len;
}

function EscapeHTMLString(str)
{
  str  = str+"";
  str = str.replace(/&nbsp;/g," ");
  str = str.replace(/&/g,"&amp;");
  str = str.replace(/</g,"&lt;");
  str = str.replace(/>/g,"&gt;");
  str = str.replace(/"/g,"&quot;");
  str = str.replace(/ /g,"&nbsp;");
  return str;
}

function EscapeXMLString(str)
{
  str = str+'';
  str = str.replace(/&/g,"&amp;");
  str = str.replace(/</g,"&lt;");
  str = str.replace(/>/g,"&gt;");
  str = str.replace(/"/g,"&quot;");
  str = str.replace(/'/g,"&apos;");
  return str;
}

function ArrayCutElem(arr,idx)
{
  if (arr.length<=idx || idx<0)
    return arr;
  if (idx==0)
    return arr.slice(1);
  if (idx==arr.length-1)
    return arr.slice(0,idx);
  return arr.slice(0,idx).concat(arr.slice(idx+1));
}

function FindFormByVarName(vname)
{
  for (i=0;i<document.forms.length;i++){
    if (document.forms[i].all[vname]!=null)
      return document.forms[i];
  }
  return null;
}

function FindVarByName(vname)
{
  var obj;
  for (i=0;i<document.forms.length;i++){
    obj = document.forms[i].all.item(vname);
    if (obj!=null){
      if (obj.length>0)
        return obj.item(0);
      else
        return obj;
    }
  }
  return null;
}

function SetFormChanged(formObj)
{
  if (formObj==null)
    return;
  if (typeof(formObj.tuiFormChanged)=="undefined")
    return;
  formObj.tuiFormChanged.value = 1;
}

gblDisabledVar = new Array();
//是否是不能选用的select组件
function IsDisabledVar(vname)
{
  var len,i;
  len = gblDisabledVar.length;
  for (i=0;i<len;i++)
    if (gblDisabledVar[i]==vname)
      return true;
  return false;
}
function DisableVar(vname)
{
  var len = gblDisabledVar.length;
  var i;
  for (i=0;i<len;i++){
    if (gblDisabledVar[i]==vname)
      return;
  }
  gblDisabledVar[len] = vname;
}
function EnableVar(vname)
{
  var len = gblDisabledVar.length;
  var i;
  for (i=0;i<len;i++){
    if (gblDisabledVar[i]==vname){
      gblDisabledVar = ArrayCutElem(gblDisabledVar,i);
      return;
    }
  }
}

function KeyDownEnter2Tab()
{
  var keyCode = window.event.keyCode;
  if (!(keyCode==38 || keyCode==40 || keyCode==13)){
    return false;
  }
  if (keyCode==38){
    window.event.cancelBubble= true;
    var obj = window.event.srcElement;
    var i,frm;
    frm = obj.form;
    for (i=0;i<frm.elements.length;i++)
      if (frm.elements[i]==obj)
        break;
    for (i--;i>=0;i--){
      if (frm.elements[i].tagName=="INPUT" && frm.elements[i].type!="hidden"){
        frm.elements[i].focus();
        frm.elements[i].select();
        break;
      }
    }
    return true;
  }
  if (keyCode==40){
    window.event.cancelBubble= false;
    window.event.keyCode = 9;
    return false;
  }
  if (keyCode==13){
    window.event.cancelBubble= false;
    window.event.keyCode = 9;
    return false;
  }
  return false;
}

//----------------------------------------------------------------------business
//供货单位编码选择和名称选择
function ProvideCodeChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin){
  CustomerCodeChange('1',iframeObj, frmName,srcVar,fieldVar,code,methodName,notin);
}
function ProvideNameChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin){
  CustomerNameChange('1',iframeObj, frmName,srcVar,fieldVar,code,methodName,notin);
}
//销货单位
function CustCodeChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin){
  CustomerCodeChange('2',iframeObj, frmName,srcVar,fieldVar,code,methodName,notin);
}
function CustNameChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin){
  CustomerNameChange('2',iframeObj, frmName,srcVar,fieldVar,code,methodName,notin);
}
//外加工单位
function ProcessCodeChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin){
  CustomerCodeChange('3',iframeObj, frmName,srcVar,fieldVar,code,methodName,notin);
}
function ProcessNameChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin){
  CustomerNameChange('3',iframeObj, frmName,srcVar,fieldVar,code,methodName,notin);
}
//运输单位
function TransportCodeChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin){
  CustomerCodeChange('4',iframeObj, frmName,srcVar,fieldVar,code,methodName,notin);
}
function TransportNameChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin){
  CustomerNameChange('4',iframeObj, frmName,srcVar,fieldVar,code,methodName,notin);
}
//内部往来单位
function InsideCodeChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin){
  CustomerCodeChange('5',iframeObj, frmName,srcVar,fieldVar,code,methodName,notin);
}
function InsideNameChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin){
  CustomerNameChange('5',iframeObj, frmName,srcVar,fieldVar,code,methodName,notin);
}
function CustomerCodeChange(lx,iframeObj,frmName,srcVar,fieldVar,code,methodName,notin)
{
  if(lx+'' == 'undefined')
    lx = '';
  paraStr = "../pub/corpselect.jsp?operate=52&ywlx="+lx+"&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&code="+code;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  iframeObj.src=paraStr;
}
function CustomerNameChange(lx,iframeObj,frmName,srcVar,fieldVar,name,methodName,notin)
{
  if(lx+'' == 'undefined')
    lx = '';
  paraStr = "../pub/corpselect.jsp?operate=55&ywlx="+lx+"&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&name="+name;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  iframeObj.src=paraStr;
}
function ProdCodeChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin)
{
  paraStr = "../pub/productselect.jsp?operate=53&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&code="+code;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  iframeObj.src=paraStr;
}
function OtherProdCodeChange(contextPath,iframeObj, frmName,srcVar,fieldVar,code,methodName,notin)
{
  paraStr = contextPath+"/associate/pub/productselect.jsp?operate=53&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&code="+code;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  alert(paraStr);
  iframeObj.src=paraStr;
}
function OtherProdSingleSelect(contextPath,frmName,srcVar,fieldVar,curID,methodName,notin)
{
  paraStr = contextPath+"/associate/pub/productselect.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&curID="+curID;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  openSelectUrl(paraStr, "SingleProdSelector", winopt2);
}
function ProdNameChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin)
{
  paraStr = "../pub/productselect.jsp?operate=54&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&name="+code;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  iframeObj.src=paraStr;
}
function SaleProdCodeChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin)
{
  paraStr = "../pub/sale_goods_select.jsp?operate=53&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&code="+code;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  iframeObj.src=paraStr;
}
function SaleProdNameChange(iframeObj, frmName,srcVar,fieldVar,code,methodName,notin)
{
  paraStr = "../pub/sale_goods_select.jsp?operate=54&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&name="+code;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  iframeObj.src=paraStr;
}
//选择客户
function CustSelectOpen(){openSelectUrl("../pub/corpselect.jsp", "SingleCustSelector", winopt2);}
//供货单位
function ProvideSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin){
  CustomerSingleSelect('1',frmName,srcVar,fieldVar,curID,methodName,notin);
}
//销货单位
function CustSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin){
  CustomerSingleSelect('2',frmName,srcVar,fieldVar,curID,methodName,notin);
}
//外加工单位
function ProcessSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin){
  CustomerSingleSelect('3',frmName,srcVar,fieldVar,curID,methodName,notin);
}
//运输单位
function TransportSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin){
  CustomerSingleSelect('4',frmName,srcVar,fieldVar,curID,methodName,notin);
}
//内部往来单位
function InsideSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin){
  CustomerSingleSelect('5',frmName,srcVar,fieldVar,curID,methodName,notin);
}
function CustomerSingleSelect(lx,frmName,srcVar,fieldVar,curID,methodName,notin)
{
  if(lx+'' == 'undefined')
    lx = '';
  paraStr = "../pub/corpselect.jsp?operate=0&ywlx="+lx+"&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&curID="+curID;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  openSelectUrl(paraStr, "SingleCustSelector", winopt2);
}
function CustomerMultiSelect(lx,frmName,srcVar,methodName,notin)
{
  paraStr = "../pub/corpselect.jsp?operate=0&multi=1&srcFrm="+frmName+"&"+srcVar;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  openSelectUrl(paraStr, "MultiCustSelector", winopt2);
}
//选择产品
function SaleProdSelectOpen(){openSelectUrl("../pub/sale_goods_select.jsp", "SingleSaleProdSelector", winopt2);}
function SaleProdSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
{
  paraStr = "../pub/sale_goods_select.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&curID="+curID;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  openSelectUrl(paraStr, "SingleSaleProdSelector", winopt2);
}
function ProdSelectOpen(){openSelectUrl("../pub/productselect.jsp", "SingleProdSelector", winopt2);}
function ProdSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
{
  paraStr = "../pub/productselect.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&curID="+curID;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  openSelectUrl(paraStr, "SingleProdSelector", winopt2);
}

function ProdMultiSelect(frmName,srcVar,methodName,notin)
{
  paraStr = "../pub/productselect.jsp?operate=0&multi=1&srcFrm="+frmName+"&"+srcVar;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  openSelectUrl(paraStr, "MultiProdSelector", winopt2);
}
//选择人员
function PersonSelectOpen(){openSelectUrl("../pub/personselect.jsp", "SinglePersonSelector", winopt1);}
function PersonSingleSelect(frmName,srcVar,fieldVar,curID,methodName,notin)
{
  paraStr = "../pub/personselect.jsp?operate=0&srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&curID="+curID;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  openSelectUrl(paraStr, "SinglePersonSelector", winopt1);
}
function PersonMultiSelect(frmName,srcVar,methodName,notin)
{
  paraStr = "../pub/personselect.jsp?operate=0&multi=1&srcFrm="+frmName+"&"+srcVar;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  if(notin+'' != 'undefined')
    paraStr += "&notin="+notin;
  openSelectUrl(paraStr, "MultiPersonSelector", winopt1);
}
//属性名称模糊查询
function PropertyNameChange(iframeObj, frmName, idname, valuename, cpid, name, methodName)
{
  paraStr = "../pub/propertyselect.jsp?operate=58&fieldVar=dmsxid&fieldVar=sxz&srcFrm="
          + frmName+"&srcVar="+idname+"&srcVar="+valuename+"&cpid="+cpid+"&name="+name;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  iframeObj.src=paraStr;
}
//选择属性
function PropertySelectOpen(){openSelectUrl("../pub/propertyselect.jsp", "SinglePropertySelector", winopt2);}
//选择属性,frmName：表单名称,idname:外键输入框名称,valuename：属性值输入框名称,cpid:物资id,methodName:选中后调用的函数名
function PropertySelect(frmName, idname, valuename, cpid, methodName)
{
  paraStr = "../pub/propertyselect.jsp?operate=0&fieldVar=dmsxid&fieldVar=sxz&srcFrm="
          + frmName+"&srcVar="+idname+"&srcVar="+valuename+"&cpid="+cpid;
  if(methodName+'' != 'undefined')
    paraStr += "&method="+methodName;
  openSelectUrl(paraStr, "SinglePropertySelector", winopt2);
}
/**
 * 关联下拉框
 * iframeObj IFrame的对象
 * lookup    Lookup Bean 的名称
 * select    目标选择框的名称
 * field     关联选中的字段名
 * parentid  关联的ID
 * selectid  选择框默认选择的ID
 * addnull  是否增加空行
 */
function associateSelect(iframeObj, lookup, select, field, parentid, selectid, addnull)
{
  if(lookup+'' == 'undefined' || select+'' == 'undefined' ||
     field+'' == 'undefined' || parentid+'' == 'undefined')
    return;
  if(addnull+'' == 'undefined')
    addnull = false;
  var url = "../pub/associate_select.jsp?lookup="+ lookup +"&select="+ select
          + "&field="+ field +"&parentid="+ parentid +"&selectid="+selectid
          + "&addnull="+(addnull ? '1' : '0');
  iframeObj.src = url;
}
/**
 * 得到一行的数据
 * iframeObj IFrame的对象
 * lookup    Lookup Bean 的名称
 * frmName   表单名称
 * srcVar    表单各个需要取值的控件名称字符串。如:srcVar=dmsxid_0&srcVar=sxz_0
 * fieldVar  与各个需要取值的控件名称相对应的字段名称字符串。如:fieldVar=dmsxid&fieldVar=sxz
 * curID     当前要得到的ID的值
 * methodName  对输入框赋值后的要调用的方法名称
 */
function getRowValue(iframeObj, lookup, frmName, srcVar, fieldVar, curID, methodName)
{
  if(curID+'' == 'undefined')
    return;
  getRowMultiValue(iframeObj, lookup, frmName, srcVar, fieldVar, 'idVar='+curID, methodName);
}
/**
 * 得到多行的数据
 * iframeObj IFrame的对象
 * lookup    Lookup Bean 的名称
 * frmName   表单名称
 * srcVar    表单各个需要取值的控件名称字符串。如:srcVar=dmsxid_0&srcVar=sxz_0
 * fieldVar  与各个需要取值的控件名称相对应的字段名称字符串。如:fieldVar=dmsxid&fieldVar=sxz
 * idVar     当前要得到的ID的值。如:idVar=1&idVar=2
 * methodName  对输入框赋值后的要调用的方法名称
 */
function getRowMultiValue(iframeObj, lookup, frmName, srcVar, fieldVar, idVar, methodName)
{
  if(lookup+'' == 'undefined' || srcVar+'' == 'undefined' ||
     fieldVar+'' == 'undefined' || idVar+'' == 'undefined')
    return;

  if(frmName+'' == 'undefined')
    frmName = '';
  if(methodName +'' =='undefined')
    methodName = '';
  var url = "../pub/associate_lookup.jsp?srcFrm="+frmName+"&"+srcVar+"&"+fieldVar+"&lookup="+lookup
          + "&"+idVar +"&method="+methodName;
  iframeObj.src = url;
}
/**
 * 计量单位与生产单位，换算单位的公式换算
 * iframeObj IFrame的对象
 * frmName   表单名称
 * srcVar    表单各个需要取值的控件名称字符串。如:srcVar=dmsxid_0&srcVar=sxz_0
 * expVar    与各个需要取值的控件名称相对应的换算公式字符串。如:exp=1122/{宽}&exp=11*22/{宽}
 * sxz       属性值表达式
 * methodName  对输入框赋值后的要调用的方法名称
 */
function unitConvert(iframeObj, frmName, srcVar, expVar, sxz, methodName)
{
  if(sxz+'' == 'undefined' || srcVar+'' == 'undefined' || expVar+'' == 'undefined')
    return;
  if(frmName+'' == 'undefined')
    frmName = '';
  if(methodName +'' =='undefined')
    methodName = '';
  var url = "../pub/unit_convert.jsp?srcFrm="+frmName+"&"+srcVar+"&"+expVar+"&sxz="+sxz
          + "&method="+methodName;
  iframeObj.src = url;
}
function unit_Convert(contextpath,iframeObj, frmName, srcVar, expVar, sxz, methodName)
{
	
  if(sxz+'' == 'undefined' || srcVar+'' == 'undefined' || expVar+'' == 'undefined')
    return;

  if(frmName+'' == 'undefined')
    frmName = '';
  if(methodName +'' =='undefined')
    methodName = '';
  var url = contextpath+"/pub/unit_convert.jsp?srcFrm="+frmName+"&"+srcVar+"&"+expVar+"&sxz="+sxz
          + "&method="+methodName;
  iframeObj.src = url;
}
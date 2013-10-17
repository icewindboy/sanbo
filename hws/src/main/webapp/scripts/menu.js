var cancelHideMenu = true;
function P_OnMouseOver(id, parent)
{
  id.className=id.overClass;
}

function P_OnMouseOut(id, parent)
{
  id.className=id.outClass;
}

function EnterMenu()
{
  cancelHideMenu = true;
}

function LeaveMenu(popMenuIdName)
{
  cancelHideMenu = false;
  setTimeout("HideMenu('"+popMenuIdName+"');", 600);
}

function HideMenu(popMenuIdName, flag)
{
  if(!cancelHideMenu)
  {
    var popMenu= eval(popMenuIdName);
    popMenu.style.display="none";
  }
}

function OnMouseUp(popMenuIdName)
{
  if(window.event.button == 2)
    showPopMenu(popMenuIdName);
}

function showPopMenu(popMenuIdName){
  var popMenu = eval(popMenuIdName);
  popMenu.style.display="block";
  popMenu.style.posLeft=document.body.scrollLeft+window.event.clientX;
  popMenu.style.posTop=document.body.scrollTop+window.event.clientY;
  if(popMenu.style.posLeft+popMenu.offsetWidth > document.body.scrollLeft+document.body.clientWidth)
    popMenu.style.posLeft=document.body.scrollLeft+document.body.clientWidth-popMenu.offsetWidth;
  if(popMenu.style.posLeft < 0) popMenu.style.posLeft=0;
  if(popMenu.style.posTop+popMenu.offsetHeight > document.body.scrollTop+document.body.clientHeight)
    popMenu.style.posTop=document.body.scrollTop+document.body.clientHeight-popMenu.offsetHeight;
  if(popMenu.style.posTop < 0)
    popMenu.style.posTop=0;
}

function showMenuItem(menuItemId)
{
  menuItemId.style.display="block";
}

function hideMenuItem(menuItemId)
{
  menuItemId.style.display="none";
}

//------------------------------------------------------------------------------
//--- 树状导航树
//------------------------------------------------------------------------------
function setTabActive(tabName, tabPrefix, tabContextPrefix, tabExtendPrefix, tabCount)
{
  var tab;
  var tabContext;
  var tabExtend;
  for(i=0; i<tabCount; i++)
  {
    var name = tabPrefix+i;
    var contextName = tabContextPrefix+i;
    var extendName = tabExtendPrefix+i;
    tab = document.all[name];
    tabContext = document.all[contextName];
    tabExtend = document.all[extendName];
    if(tab+''!="undefined"){
      ;//tab.style.backgroundColor = (name==tabName ? '#205aa7' : '#0d73bd');
    }
    if(tabContext+''!="undefined"){
      tabContext.style.display = name==tabName ? 'block' : 'none';
    }
    if(tabContext+''!="undefined")
      tabExtend.style.display = name==tabName ? 'block' : 'none';
  }
}
function treeMenuMouseOut()
{
  var obj = event.srcElement;
  if(obj.tagName != 'TD')
    return;
  obj.style.backgroundColor = "#BCD7F4";
  obj.style.color= "#000000";
}
function treeMenuMouseOver()
{
  var obj = event.srcElement;
  if(obj.tagName != 'TD')
    return;
  obj.style.backgroundColor = "#003471";
  obj.style.color= "#ffffff";
}

//------------------------------------------------------------------------------
//--- outlookbar Methods
//------------------------------------------------------------------------------
var menubarheight = 0;
var menubarsum = 0;
var menuspeed = 10;
var menuinc = 40;//动画出现次数的倒数
var scrollspeed = 20;
var scrollinc = 10;
var menuchoose = 0;
var iconX = new Array();
var menuIconWidth = new Array();
var menuIconHeight = new Array();
var menuscroll = 0;
var iconareaheight = 0;
var iconrightpos = 0;
var maxscroll = 0;
var scrolling = false;
var scrollTimerID = 0;
//
var menulayer = null;
var iconlayer = null;
var barlayer = null;
var uplayer = null;
var downlayer = null;
var browser = null;

function getDynLayer(id, nestref, frame)
{
  //browser:
  if(browser == null)
    browser = new TBrowser();
  return GetDynamicLayer(id, browser, nestref, frame);
}

function init(barheight, barsum, menuLayerPrefix, iconLayerPrefix,
              barLayerPrefix, upLayerPrefix, downLayerPrefix, postfix)
{
  menubarheight = barheight;
  menubarsum = barsum;
  menulayer = new Array(menubarsum);
  iconlayer = new Array(menubarsum);
  barlayer = new Array(menubarsum);
  uplayer = new Array(menubarsum);
  downlayer = new Array(menubarsum);

  for (var i=0; i<menubarsum; i++)
  {
    menulayer[i] = getDynLayer(menuLayerPrefix + i + postfix);
    menulayer[i].slideInit();

    menuIconWidth[i] = document.all[iconLayerPrefix + i + postfix].scrollWidth;
    menuIconHeight[i] = document.all[iconLayerPrefix + i + postfix].scrollHeight;

    iconlayer[i] = getDynLayer(iconLayerPrefix + i + postfix, menuLayerPrefix + i + postfix);
    iconlayer[i].slideInit();

    if (menuIconWidth[i] > document.body.clientWidth)
    {
      iconlayer[i].setWidth(menuIconWidth[i]);
      iconX[i] = (document.body.clientWidth-menuIconWidth[i])/2;
    }
    else
    {
      iconlayer[i].setWidth(document.body.clientWidth);
      iconX[i] = 0;
    }
    iconlayer[i].moveTo(iconX[i], menubarheight);

    barlayer[i] = getDynLayer(barLayerPrefix + i + postfix, menuLayerPrefix + i + postfix);
    barlayer[i].slideInit();

    uplayer[i] = getDynLayer(upLayerPrefix + i + postfix, menuLayerPrefix + i + postfix);
    uplayer[i].slideInit();

    downlayer[i] = getDynLayer(downLayerPrefix + i + postfix, menuLayerPrefix + i + postfix);
    downlayer[i].slideInit();
  }

  menureload();
}

function menubarpush(num)
{
  if (num != menuchoose && num >= 0 && num < menubarsum) {
    iconlayer[menuchoose].moveTo(iconX[menuchoose], menubarheight);
    menuscroll = 0;
    scrolling = false;
    for (var i=0; i <=num; i++) {
      menulayer[i].slideTo(0, i*menubarheight, menuinc, menuspeed);
    }
    nAdCornerOriginY = document.body.clientHeight;
    nAdCornerOriginY += document.body.scrollTop;
    for (var i=menubarsum-1; i>num; i--) {
      nAdCornerOriginY -= menubarheight;
      menulayer[i].slideTo(0,nAdCornerOriginY, menuinc, menuspeed);
    }
    menuchoose = num;
    menuscrollbar();
  }
}

function menureload() {
  nAdCornerOriginY = document.body.clientHeight;
  nAdCornerOriginY += document.body.scrollTop;
  for (var i=menubarsum-1; i>menuchoose; i--)
  {
    nAdCornerOriginY -= menubarheight;
    menulayer[i].moveTo(0, nAdCornerOriginY);
  }
  for (var i=0; i<menubarsum; i++)
  {
    if (menuIconWidth[i] > document.body.clientWidth)
    {
      iconlayer[i].setWidth(menuIconWidth[i]);
      iconX[i] = (document.body.clientWidth-menuIconWidth[i])/2;
    }
    else
    {
      iconlayer[i].setWidth(document.body.clientWidth);
      iconX[i] = 0;
    }
    iconlayer[i].moveX(iconX[i], menubarheight);
  }

  menuscrollbar();
}

function menuscrollbar() {
  iconareaheight = document.body.clientHeight-menubarheight*menubarsum;
  iconrightpos = document.body.clientWidth-16-8;
  maxscroll = menuIconHeight[menuchoose] - iconareaheight;

  if (maxscroll > 0) {
    if (menuscroll > 0)
    {
      uplayer[menuchoose].moveTo(iconrightpos, menubarheight+4)
    }
    else
    {
      uplayer[menuchoose].moveTo(-20, 0)
    }
    if (menuscroll < maxscroll)
    {
      downlayer[menuchoose].moveTo(iconrightpos, iconareaheight+2);
    }
    else
    {
      downlayer[menuchoose].moveTo(-20, 0);
    }
  }
  else {
    uplayer[menuchoose].moveTo(-20, 0);
    downlayer[menuchoose].moveTo(-20, 0);
  }
}

function menuscrollup() {
  if (menuscroll > 0) {
    scrolling = true;
    menuscroll -= scrollinc;
    iconlayer[menuchoose].moveTo(iconX[menuchoose], menubarheight-menuscroll);

    scrollTimerID = setTimeout("menuscrollup()", scrollspeed)
  }
  else {
    menuscrollstop()
  }

  menuscrollbar()
}

function menuscrolldown() {
  if (menuscroll < maxscroll)
  {
    scrolling = true;
    menuscroll += scrollinc;
    if (menuscroll < maxscroll)
      iconlayer[menuchoose].moveTo(iconX[menuchoose], menubarheight-menuscroll)
    else
      iconlayer[menuchoose].moveTo(iconX[menuchoose], menubarheight-maxscroll)
    scrollTimerID = setTimeout("menuscrolldown()", scrollspeed)
  }
  else
    menuscrollstop();

  menuscrollbar();

}

function menuscrollstop() {
  scrolling = false;
  if (scrollTimerID) {
    clearTimeout(scrollTimerID);
    scrollTimerID = 0;
  }
}

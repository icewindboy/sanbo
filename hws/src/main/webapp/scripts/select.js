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
  return "";
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
  this.onselect = onselectFunc;
  this.isEnter2tab = enter2tab;
  this.isLookup = isLookup;

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
}

function TSLCT_RemoveAll()
{
  this.nItems = 0;
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
  if (keyCode==13 && selobj.isLookup){
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
  if (selobj.onselect!='' && selobj.onselect!=null){
    eval(selobj.onselect);
    //selobj.onselect();
  }
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

/*function GenSelectHTML(selname,idoff,varV,varH,inVal,dispTxt,size,className,enter2tab)
{
  var str;
  if (className+''=='undefined')
    className='selectOff';
  if (enter2tab+''=='undefined')
    enter2tab=false;
  if (enter2tab)
    keydown = "ONKEYDOWN=\"KeyDownEnter2Tab();\"";
  else
    keydown = "";
  str = "<DIV ID=\""+idoff+"\" CLASS=\""+className+"\" ONCLICK=\"ToggleSelect('"+selname+"');\">";
  str += "<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0><TR><TD><INPUT CLASS=\"ednone\" READONLY STYLE=\"cursor:default;\" NAME=\""+varV+"\" VALUE=\""+dispTxt+"\" SIZE=\""+size+"\" "+keydown+">";
  str += "</TD><TD WIDTH=\"1%\"><IMG SRC=\"/img/down_arrow.gif\" STYLE=\"cusor:hand;\" BORDER=0>";
  str += "</TD></TR></TABLE></DIV>\n";
  str += "<INPUT TYPE=HIDDEN NAME=\""+varH+"\" VALUE=\""+inVal+"\">";
  return str;
}*/

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
  index = -1;
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
  }
}

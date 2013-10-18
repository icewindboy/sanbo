////////////////////////////////////////////////////////////////////////////////////////////////////
//	描述: 提供给exchangselect控件的客户端函数文件
////////////////////////////////////////////////////////////////////////////////////////////////////

var g_iSelectedIndex		//前一个选中选项的索引值
var g_fnSelectOnclick		//记录选择框鼠标单击事件
var g_fnSelectOndblclick	//记录选择框鼠标双击事件

//document.onselectstart = CancelSelect

//	函数: 取消鼠标选择文本
//	参数: 无
//	返回: 无

function CancelSelect() {
	event.cancelBubble = true
	return false;
}

/**
 * 选中选择框的选项
 * @param oThis 被处理的选择框对象
 * @param isCheck 是否有checkbox的选择框
 */
function SelectOption(oThis, isCheck) {
  if(IsDisabledVar(oThis.id))
    return true;

  if (isCheck+'' == 'undefined')
    isCheck = false;

  var oCurrent = event.srcElement;
  for (; oCurrent.tagName!='TR'; oCurrent=oCurrent.parentElement);
  if(oCurrent+'' == 'undefined')
    return true;

  var tmp = oCurrent;
  for (; tmp.tagName!='TABLE'; tmp=tmp.parentElement);
  if(oThis != tmp)
    return true;

  var iSelectedIndex;
	for (i = 0; i < oThis.rows.length; i++) {
		if (oThis.rows(i) == oCurrent) {
			iSelectedIndex = i;
		}
	}
	if (event.ctrlKey != true) {
		for (i = 0; i < oThis.rows.length; i++) {
      var disableText = isCheck ? oThis.rows(i).cells(3).innerText : oThis.rows(i).cells(2).innerText;
			if (disableText == 1) {
        szColor = "#000000"
			}
      else {
          //szColor = "#ff0000"
        szColor = "#999999"
			}
			oThis.rows(i).style.color = szColor
			oThis.rows(i).style.backgroundColor = "#ffffff"
		}
	}
	if (!isCheck && event.shiftKey == true && !isNaN(g_iSelectedIndex)) {
		iLengthOfRow = oThis.rows.length
		iStep = (iSelectedIndex - g_iSelectedIndex) / Math.abs(iSelectedIndex - g_iSelectedIndex)
		for (i = g_iSelectedIndex; (oThis.rows(i) != oCurrent); i = i + iStep) {
			if (i >= iLengthOfRow)
				continue;
			oThis.rows(i).style.color = "#ffffff"
			oThis.rows(i).style.backgroundColor = "#113399"
		}
	}
  else {
    g_iSelectedIndex = iSelectedIndex;
	}

  var disableText = isCheck ? oCurrent.cells(3).innerText : oCurrent.cells(2).innerText;
	if (disableText == 1) {
		szColor = "#000000"
	}
  else {
    //szColor = "#ff0000"
		szColor = "#999999"
	}
	if (oCurrent.style.backgroundColor == "#113399") {
		oCurrent.style.color = szColor
		oCurrent.style.backgroundColor = "#ffffff"
	}
  else {
    oCurrent.style.color = "#ffffff"
		oCurrent.style.backgroundColor = "#113399"
  }
	event.cancelBubble = true;
	return false;
}

/**
 * 当前选择框选项是否可用
 * @param isEnble 是否可用
 * @param isCheckSelect 是否含有checkbox的选择框
 */
function fnEnableOption(isEnble, isCheckSelect)
{
  if (isCheckSelect+'' == 'undefined')
    isCheckSelect = false;

  var oCurrent = event.srcElement;
  for (; oCurrent.tagName!='TR'; oCurrent=oCurrent.parentElement);
  if(oCurrent+'' == 'undefined')
    return;

  var tmp;
  for (tmp=oCurrent;tmp.tagName!='TABLE';tmp=tmp.parentElement);
  if (IsDisabledVar(tmp.id))
    return;

  if(isCheckSelect)
    oCurrent.cells(3).innerText = isEnble ? '1' : '0';
  else
    oCurrent.cells(2).innerText = '0';

  oCurrent.style.color = isEnble ? "#000000" : "#999999";
}

/**
 * 当前选择框选项是否可用
 * @param isEnble 是否可用
 * @param isCheckSelect 是否含有checkbox的选择框
 * @param oCurrent 处理的选择框
 * @param sOptionValue 键值
 * @return 返回
 */
function fnEnbleOtherOption(isEnble, isCheck, oCurrent, sOptionValue)
{
  if (isCheck+'' == 'undefined')
    isCheck = false;

  if (IsDisabledVar(oCurrent.id))
    return;
  var i=0;
  var oRow;
  var hasOption = false;
  for (; i < oCurrent.rows.length; i++)
  {
    oRow = oCurrent.rows(i);
    key = isCheck ? oRow.cells(1).innerText : oRow.cells(0).innerText;
    if(key == sOptionValue)
    {
      hasOption = true;
      break;
    }
  }
  if(!hasOption)
    return -1;

  if(isCheck)
    oRow.cells(3).innerText = isEnble ? '1' : '0';
  else
    oRow.cells(2).innerText = '0';
  oRow.style.color = isEnble ? "#000000" : "#999999";

  return i;
}
/**
 * 左右移动选择框的选项
 * @param oSource:	源选择框对象
 * @param oTarget:	目标选择框对象
 * @param isSourceCheck 是否有checkbox的源选择框
 */
function fnExchangeSelect(oSource, oTarget, isSourceCheck) {
  if (isSourceCheck+'' == 'undefined')
    isSourceCheck = false;
  //if (isTargetCheck+'' == 'undefined')
    //isTargetCheck = false;
  if (IsDisabledVar(oSource.id) || IsDisabledVar(oTarget.id))
    return;
  var i = 0;
  for (;i < oSource.rows.length; i++) {
    oRow = oSource.rows(i);
    if (oRow.style.color == "#ffffff" && oRow.style.backgroundColor == "#113399")
    {
      disableText = isSourceCheck ? oRow.cells(3).innerText : oRow.cells(2).innerText;
      if (disableText == 1)
      {
				iOptionValue = isSourceCheck ? oRow.cells(1).innerText : oRow.cells(0).innerText;
				szOptionText = isSourceCheck ? oRow.cells(2).innerText : oRow.cells(1).innerText;
        var oOption = new TOption('none', iOptionValue, szOptionText, true, null);
				fnAddOption(oTarget, oOption, isSourceCheck);
        if(isSourceCheck)
          continue;
        oSource.deleteRow(i);
        i--;
			}
		}
  }
}

/**
 * 上下移动选择框选项的顺序
 * @param oCurrent 被处理的选择框对象
 * @param iDirection 移动方向 (－1: 上移； 1: 下移)
 * @param oCheckBox checkbox的对象
 */
function fnMoveOption(oCurrent, iDirection, oCheckBox)
{
	for (i = 0; i < oCurrent.rows.length; i++) {
		if (oCurrent.rows(i).style.backgroundColor == "#113399") {
      iSelectedIndex = i;
		}
	}
  if(iDirection < 0 && iSelectedIndex > 0)
  {
    ischeck = oCheckBox[iSelectedIndex].checked;
    oCurrent.moveRow(iSelectedIndex, iSelectedIndex + iDirection);
    oCheckBox[iSelectedIndex + iDirection].checked = ischeck;
  }
  else if(iDirection > 0 && iSelectedIndex < oCurrent.rows.length-1)
  {
    ischeck = oCheckBox[iSelectedIndex].checked;
    oCurrent.moveRow(iSelectedIndex, iSelectedIndex + iDirection);
    oCheckBox[iSelectedIndex + iDirection].checked = ischeck;
  }
}

//	函数: 得到选择框选项值的字符串
//	参数: chCompartedSign, oCurrent
//       chCompartedSign:	字符串的分隔符
//       oCurrent:			被取值的选择框对象
//	返回: 选择框选项值的字符串
function fnGetOptionValue(chCompartedSign, oCurrent, isCheck) {
  if(isCheck+'' == 'undefined')
    isCheck = false;
	szOptionValue = ""
	for (i = 0; i < oCurrent.rows.length; i++) {
		if (szOptionValue == "") {
			szOptionValue += isCheck? oCurrent.rows(i).cells(1).innerText : oCurrent.rows(i).cells(0).innerText;
		} else {
			szOptionValue += chCompartedSign
                    + (isCheck? oCurrent.rows(i).cells(1).innerText : oCurrent.rows(i).cells(0).innerText);
		}
	}
    return szOptionValue;
}
/**
 * 得到选择框选项值的关联数组
 * oCurrent 被取值的选择框对象
 * isCheck 是否是带有check的选择框
 * @return 选择框选项值的关联数组
 */
function fnGetOptionsArray(oCurrent, isCheck) {
  if(isCheck+'' == 'undefined')
    isCheck = false;
	aOptions = new Array()
	for (i = 0; i < oCurrent.rows.length; i++) {
		iOptionValue = isCheck? oCurrent.rows(i).cells(1).innerText : oCurrent.rows(i).cells(0).innerText;
		szOptionText = isCheck? oCurrent.rows(i).cells(2).innerText : oCurrent.rows(i).cells(1).innerText;
		aOptions[iOptionValue] = szOptionText;
	}
  return aOptions;
}
//	函数: 构造选择框选项值的对象
//	参数: szOptionValue, szOptionText
//       szOptionValue:		选项的值
//       szOptionText:		选项的文本
//	返回: 无
function objOptions(szOptionValue, szOptionText) {
	this.szValue = szOptionValue
	this.szText = szOptionText
}

//	函数: 得到选择框选项值的对象
//	参数: oCurrent
//       oCurrent:		被取值的选择框对象
//	返回: 选择框选项值的对象
function fnGetOptionsObject(oCurrent) {
	aOptions = new Array()
	for (i = 0; i < oCurrent.rows.length; i++) {
		szOptionValue = oCurrent.rows(i).cells(0).innerText
		szOptionText = oCurrent.rows(i).cells(1).innerText
		aOptions[i] = new objOptions(szOptionValue, szOptionText)
	}
  return aOptions;
}

//	函数: 选中多项选择框选项
//	参数: oCurrent, fnCallBack, paraCallBack
//       oCurrent:		被处理的选择框对象
//       fnCallBack:	返回的函数名称
//       paraCallBack:	返回的参数
//	返回: 无
function fnMultiSelect(oCurrent, fnCallBack, paraCallBack) {
  var flag;
  for (i = 0; i < oCurrent.rows.length; i++) {
    flag = fnCallBack(oCurrent.rows(i).all(0).innerText, paraCallBack);
    if (flag != 0)
    {
      //edit by leonduan for trigger checked
      if (flag == 1){
        oCurrent.rows(i).style.color = "#ffffff"
        oCurrent.rows(i).style.backgroundColor = "#113399"
      }
      else if (flag == -1) {
        oCurrent.rows(i).style.color = "#000000"
        oCurrent.rows(i).style.backgroundColor = "#ffffff"
      }
    }
    else{
      //oCurrent.rows(i).style.color = "#000000"
      //oCurrent.rows(i).style.backgroundColor = "#ffffff"
    }
  }
}

/**
 * 添加选择框选项到含有radio的目标选择框
 * @param oTarget 目标选择框
 * @param radioName radio名称
 * @param rowClick 行点击事件
 * @param checkClick check对象点击事件
 */
function fnAddRadioOption(oTarget, radioName, rowClick, checkClick)
{
  var oCurrent = event.srcElement;
  for (; oCurrent.tagName!='TD'; oCurrent=oCurrent.parentElement);
  if(oCurrent+'' == 'undefined')
    return;

  oRow = oCurrent.parentElement;
  iOptionValue = oRow.cells(1).innerText;
  szOptionText = oRow.cells(2).innerText;
  var oOption = new TOption('radio', iOptionValue, szOptionText, true, rowClick, radioName, false, checkClick);
  fnAddOption(oTarget, oOption, true);
}
/**
 * 添加一条选择框选项
 * @param oCurrent 被处理的选择框对象
 * @param iOptionValue 选项的值
 * @param szOptionText 选项的文本
 * @param isUnique 是否是唯一(不可重复出现)
 * @param checkText 含有checkbox或rodio的文本
 * @param isEnable 是否可以交换
 */
function fnAddOption(oCurrent, oOption, isUnique)
{
  type = oOption.type;
  isNone = type == 'none';
  isCheck = type == 'checkbox' || type == 'radio';
  if(!isNone && !isCheck)
    return;

  if (isUnique+'' == 'undefined')
    isUnique = false;
  if(isUnique)
  {
    for (i = 0; i < oCurrent.rows.length; i++)
    {
      oRow = oCurrent.rows(i);
      key = isCheck ? oRow.cells(1).innerText : oRow.cells(0).innerText;
      if(key == oOption.value)
        return;
    }
  }

	bOptionExchangeable = oOption.isEnable ? 1 : 0;
	oTR = oCurrent.insertRow();
	oTR.style.cursor = "default";
  oTR.name = oOption.value+"_"+(oCurrent.rows.length);
  if(oOption.rowClick != null)
    oTR.onclick = new Function(oOption.rowClick);
	oTR.insertCell();
	oTR.insertCell();
	oTR.insertCell();
  if(isNone)//no checbox or no radio
  {
    oTR.cells(0).style.display = "none";
    oTR.cells(1).className = "selectItem";
    oTR.cells(2).style.display = "none";
    oTR.cells(0).innerText = oOption.value;
    oTR.cells(1).innerText = oOption.text;
    oTR.cells(2).innerText = bOptionExchangeable;
  }
  else if(isCheck)
  {
    oTR.insertCell();
    checkHTML = "<input type='"+ type + "' id='"+ oOption.checkName + "' name='"
              + oOption.checkName +"' value='"+ oOption.value +"'";
    if(oOption.checkClick != null)
      checkHTML += " onclick='" + oOption.checkClick +"'";
    checkHTML += oOption.isChecked ? " checked>" : ">";

    oTR.cells(0).style.width = "10";
    oTR.cells(0).className = "selectItem";
    oTR.cells(1).style.display = "none";
    oTR.cells(2).className = "selectItem";
    oTR.cells(3).style.display = "none";

    oTR.cells(0).innerHTML = checkHTML;
    oTR.cells(1).innerText = oOption.value;
    oTR.cells(2).innerText = oOption.text;
    oTR.cells(3).innerText = bOptionExchangeable;
  }
}

//	函数: 添加多条选择框选项
//	参数: oCurrent, aOptions
//       oCurrent:		被处理的选择框对象
//       aOptions:		需要添加选项的数组
//	返回: 无
function fnAddMultiOptions(oCurrent, aOptions) {
	for (key in aOptions) {
    var oOption = new TOption('none', key, aOptions[key], true, null);
		fnAddOption(oCurrent, oOption, true);
	}
}

//	函数: 删除一条选择框选项
//	参数: oCurrent, iIndex
//       oCurrent:		被处理的选择框对象
//       iIndex:		选项的索引值
//	返回: 无
function fnDeleteOption(oCurrent, iIndex) {
	oCurrent.deleteRow(iIndex)
}

/**
 * 删除一条选择框选项（更据id）
 * @param oCurrent 被处理的选择框对象
 * @param iOptionValue 键值
 * @param isCheck 是否是check的选择框
 */
function fnRemoveOption(oCurrent, iOptionValue, isCheck) {
  if(isCheck+'' == 'undefined')
    isCheck = false;
  for (i = 0; i < oCurrent.rows.length; i++)
  {
    oRow = oCurrent.rows(i);
    key = isCheck ? oRow.cells(1).innerText : oRow.cells(0).innerText;
    if(key != iOptionValue)
      continue;
    oCurrent.deleteRow(i);
    i--;
  }
}

/**
 * 移去选中的选择框的多个选项
 * @param oCurrent 被处理的选择框对象
 */
function fnRemoveMultiOption(oCurrent) {
  if (IsDisabledVar(oCurrent.id))
    return;
  for (i = 0; i < oCurrent.rows.length; i++) {
    oRow = oCurrent.rows(i);
    if (oRow.style.color == "#ffffff" && oRow.style.backgroundColor == "#113399")
    {
      disableText = oRow.cells(2).innerText;
      if (disableText == 1)
      {
        oCurrent.deleteRow(i);
        i--;
      }
    }
  }
}

//	函数: 删除所有选择框选项
//	参数: oCurrent
//       oCurrent:		被处理的选择框对象
//	返回: 无
function fnDeleteAllOptions(oCurrent) {
	iLength = oCurrent.rows.length
  for (i = 0; i < iLength; i++) {
    fnDeleteOption(oCurrent, 0)
  }
}

//	函数: 清除单个选择框选项的被选中状态
//	参数: oCurrent, iIndex
//       oCurrent:		被处理的选择框对象
//       iIndex:		被处理的选择框选项的索引值
//	返回: 无
function fnClearOption(oCurrent, iIndex) {
	oCurrent.rows(iIndex).style.color = "#000000"
	oCurrent.rows(iIndex).style.backgroundColor = "#ffffff"
}

//	函数: 清除所有选择框选项的被选中状态
//	参数: oCurrent
//       oCurrent:		被处理的选择框对象
//	返回: 无
function fnClearAllOptions(oCurrent) {
	iLength = oCurrent.rows.length
  for (i = 0; i < iLength; i++) {
    fnClearOption(oCurrent, i)
  }
}

//	函数: 设置选择框禁用状态
//	参数: oCurrent
//       oCurrent:		被处理的选择框对象
//	返回: 无
function fnSetSelectDisabled(oCurrent) {
	fnClearAllOptions(oCurrent);
	DisableVar(oCurrent.id);
}

//	函数: 设置选择框起用状态
//	参数: oCurrent
//       oCurrent:		被处理的选择框对象
//	返回: 无
function fnSetSelectEnabled(oCurrent) {
	EnableVar(oCurrent.id);
}
/**
 * 得到行对象在选择框中的位置
 * @param oCurrent 选择框对象
 * @param oRow 选择框选项对象
 * @return -1:不存在该对象, null:只有一个对象, >0:位置
 */
function getRowIndex(oCurrent, oRow)
{
  var i = 0;
  var length = oCurrent.rows.length;
  for (; i < length; i++)
  {
    if(oRow == oCurrent.rows(i))
      return length==1 ? null : i;
  }
  return -1;
}
/**
 * 得到选项值在选择框中的位置
 * @param oCurrent 选择框对象
 * @param optionValue 选项值
 * @param isCheck 是否是选择类型
 * @return -1:不存在该对象, null:只有一个对象, >0:位置
 */
function getRowIndexByValue(oCurrent, optionValue, isCheck)
{
  var i = 0;
  var length = oCurrent.rows.length;
  for (; i < length; i++)
  {
    oRow = oCurrent.rows(i);
    key = isCheck ? oRow.cells(1).innerText : oRow.cells(0).innerText;
    if(key == optionValue)
      return length==1 ? null : i;
  }
  return -1;
}
//none: 没有含check的, checkbox: 含checkbox的, radio: 含radio的
function TOption(type, value, text, isEnable, rowClick, checkName, isChecked, checkClick)
{
  this.type = type;
  this.value = value;
  this.text = text;
  this.isEnable = isEnable;
  this.checkName = checkName;
  this.isChecked = isChecked;
  this.rowClick = rowClick;
  this.checkClick = checkClick;
  return this;
}
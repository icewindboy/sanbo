function RowControl()
{
  this.rowColorLight = '#ddeeff';
  this.rowColorDark = '#F2F9FC';
  this.rowColorHighlight = '#f0f0e0';
  this.allRow = new Array();
  return this;
}

function AddRowItem(rowCtrl, curRow)
{
  rowCtrl.allRow[rowCtrl.allRow.length] = document.all[curRow];
}

function InitRowControl(rowCtrl)
{
  var row;
  var colorLight = rowCtrl.rowColorLight;
  var colorDark = rowCtrl.rowColorDark;
  var rowlen = rowCtrl.allRow.length;
  for(i=0; i<rowlen; i++)
  {
    row = rowCtrl.allRow[i];
    isLight = i%2==0;
    row.style.backgroundColor = isLight ? colorLight : colorDark;
    //row.onmouseover = (row.style.backgroundColor='#f0f0e0');
    //row.onmouseout = (row.style.backgroundColor=isLight ? colorLight : colorDark);
  }
}
//--------------------
//--输入框的长度控制对象--
//--------------------
//保存对象的数组
var sizeObjs = false;
/**
 * @param name 对象名称,可以是字段名称
 * @param initSize 初始化的长度
 */
function TInputControl(name, initSize)
{
  this.name = name;
  this.maxSize = initSize;
  this.inputObjs = new Array();
  this.AddInputSize = TICT_AddInputSize;
  this.AdjustInputSize = TICT_AdjustInputSize;
  return this;
}

/**
 * 得到一个输入框控制对象，若不存在则创建一个。
 */
function GetInputControl(name, initSize)
{
  var obj = FindInputControl(name);
  if(obj == null)
  {
    obj = new TInputControl(name, initSize);
    RegisterInputControl(obj);
  }
  return obj;
}

/**
 * 注册控制输入框的对象
 * @param sizeob 控制输入框的对象
 */
function RegisterInputControl(inputControl)
{
  if (!sizeObjs)
    sizeObjs = new Array();
  sizeObjs[sizeObjs.length] = inputControl;
}

function FindInputControl(name)
{
  var i;
  for (i=0;i<sizeObjs.length;i++)
    if (sizeObjs[i].name == name)
      return sizeObjs[i];
  return null;
}
/**
 * 添加一个输入框，并比较最大长度
 * @praram inputName 输入框的名称
 * @praram size 输入值的长度
 */
function TICT_AddInputSize(inputObj)
{
  var size;
  this.inputObjs[this.inputObjs.length] = inputObj;
  //is select control
  if(inputObj.link +'' == 'undefined')
    size = strlen(inputObj.value);
  else{
    selObj = FindSelectObject(inputObj.name);
    if(selObj != null)
      size = selObj.length;
  }
  if(size > this.maxSize)
    this.maxSize = size;
}
/**
 * 调整各个输入框的长度为最大长度
 */
function TICT_AdjustInputSize()
{
  var i=0;
  for(; i<this.inputObjs.length; i++)
  {
    width = this.maxSize;//*80/13;
    linkName = this.inputObjs[i].link;
    if(linkName +'' == 'undefined')
      this.inputObjs[i].size = width;
    else
    {
      form = this.inputObjs[i].form;
      if(form != null)
      {
        form.elements[linkName].size = width;
      }
      else
        eval(linkName+".size="+width);
    }
    //eval(inputStr);
  }
}